package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

/**
 * 这个动态参与者将是最关键的动态参与者!!!即沿着机构树往上爬,然后根据指定的机构类型找到其机构!! 再找出该机构下的所有子孙机构!!
 * 根据机构树的路径关系找出父亲路径上的某种机构类型的所有机构!!! 即沿着路径往上找,直至找到最后一个结点的类型为指定类型的机构!! 然后出该机构的下的所有子孙结点!! 即先往上后往下!!!
 * 然后在这些机构范围中找出指定角色的人员!! 两者取交集!!!
 * 要解决性能问题!!!
 * @author Administrator
 *
 */
public class WDPUser_TreePathCorpChooseRole implements WorkflowDynamicParticipateIfc {

	private String str_dynparName = null; //动态参与者名称
	private String loginUserCorpId = null; //登录人员所属机构
	private String treePathCorpType = null; //机构树上的机构灯型,比如分行,支行,一级分行等....
	private String roleCode = null; //角色编码
	private CommDMO commDMO = new CommDMO(); //
	private TBUtil tbUtil = new TBUtil(); //

	private Logger logger = WLTLogger.getLogger(WDPUser_TreePathCorpChooseRole.class); //

	/**
	 * 构造方法,根据机构Id与角色编码两者搞定一切!! 即还是经典的 "机构+角色"控制模式!!
	 * @param _loginUserDeptId
	 * @param _roleCode
	 */
	public WDPUser_TreePathCorpChooseRole(String _dynName, String _loginUserDeptId, String _treePathCorpType, String _roleCode) {
		this.str_dynparName = _dynName; //
		this.loginUserCorpId = _loginUserDeptId;
		this.treePathCorpType = _treePathCorpType; //机构类型..
		this.roleCode = _roleCode; //角色编码
	}

	private String getDefineInfo() {
		return "***动态参与者[" + str_dynparName + "],计算方法[本机构的某种类型的上级机构],取父亲机构类型[" + treePathCorpType + "],角色:[" + roleCode + "]"; //
	}

	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String code, String dealTyp, String code2, String code3, String name) throws Exception {
		long ll_begin = System.currentTimeMillis(); //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		if (loginUserCorpId == null) {
			parBean.setParticiptMsg("登录人员所属机构为空,无法进行动态参与者计算!,请将登录人员设置所属机构!!\r\n"); //
			return parBean;
		}

		HashVO[] hvo_role = commDMO.getHashVoArrayByDS(null, "select id,code,name from pub_role where code='" + roleCode + "'"); //
		String str_roleid = null;
		String str_rolename = null;
		if (hvo_role.length > 0) {
			str_roleid = hvo_role[0].getStringValue("id"); //
			str_rolename = hvo_role[0].getStringValue("name"); //
		} else {
			parBean.setParticiptMsg(getDefineInfo() + ",但该编码在角色基本表中没有找到对应的记录,可能是因为数据迁移时造成了主键绑定失效了!!\r\n"); ///
			return parBean; //直接返回
		}
		long ll_1 = System.currentTimeMillis(); //
		HashVO[] hvs_tree = getAllCorpTreeHVS(); //
		long ll_2 = System.currentTimeMillis(); //
		HashMap[] mapIdNames = tbUtil.getHashMapsFromHashVOs(hvs_tree, new String[][] { { "id", "parentid" }, { "id", "name" }, { "id", "corptype" }, { "id", "$rownum" },{ "id", "linkcode" } }); //由HashVO[]中抽取一对对的哈段表数据,以提高性能!!
		HashMap map_id_parentID = mapIdNames[0];
		HashMap map_id_name = mapIdNames[1];
		HashMap map_id_corptype = mapIdNames[2];
		HashMap map_id_index = mapIdNames[3];
		HashMap map_id_linkcode = mapIdNames[4];
		String loginUserCorpName = (String) map_id_name.get(loginUserCorpId); //登录人员的所属机构名称!
		ArrayList alPath = new ArrayList(); //
		findTreePath(loginUserCorpId, map_id_parentID, alPath); //
		String str_rootCorpId = null; //
		String str_rootCorpName = null; //
		StringBuffer sb_parentpath = new StringBuffer(); //
		for (int i = alPath.size() - 1; i >= 0; i--) { //从最下面往上爬,即从要结点或总行往下找!!
			String str_corpId = (String) alPath.get(i); //
			String str_corpName = (String) map_id_name.get(str_corpId); //
			String str_corpType = (String) map_id_corptype.get(alPath.get(i)); //
			sb_parentpath.append("机构[" + str_corpId + "," + str_corpName + "]的类型=[" + str_corpType + "]\r\n"); //
			if (str_corpType != null && str_corpType.equals(treePathCorpType)) { //
				str_rootCorpId = str_corpId; //
				str_rootCorpName = str_corpName; //
				break;
			}
		}

		if (str_rootCorpId != null) { //如果找到了我想找的机构,即真正的根结点!!
			parBean.setParticipantDeptId(str_rootCorpId); //
			parBean.setParticipantDeptLinkcode(map_id_linkcode.get(str_rootCorpId).toString()); //
			parBean.setParticipantDeptType(treePathCorpType); //

			//找出这个机构下面的所有子孙机构!!!
			int li_index = (Integer) map_id_index.get(str_rootCorpId); //
			int li_level = hvs_tree[li_index].getIntegerValue("$level"); //当前结点的层次!!
			ArrayList al_childrenIds = new ArrayList(); //所有的子孙结点都存在这里了!!! 最重要的数据!!!至此机构已算出来了!!!剩下的就是与角色算出来的机构取交集了!!!
			al_childrenIds.add(hvs_tree[li_index].getStringValue("id")); //
			for (int i = li_index + 1; i < hvs_tree.length; i++) { //从某一个结点开始,至最后
				if (hvs_tree[i].getIntegerValue("$level") <= li_level) { //如果某结点的层次大于等于当前的层次,说明是兄弟了,则退出循环,提高性能!!
					break;
				} else {
					al_childrenIds.add(hvs_tree[i].getStringValue("id")); //如果某结点的层次小于根结点,说明是他的子孙!!!
				}
			}

			String str_inCons = tbUtil.getInCondition(al_childrenIds); //
			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("select "); //
			sb_sql.append("deptid,"); //
			sb_sql.append("deptcode,"); //
			sb_sql.append("deptname,"); //
			sb_sql.append("postid,"); //
			sb_sql.append("postcode,"); //
			sb_sql.append("postname,"); //
			sb_sql.append("userid,"); //
			sb_sql.append("usercode,"); //
			sb_sql.append("username "); //
			sb_sql.append("from v_pub_user_post_1 "); //
			sb_sql.append("where deptid in (" + str_inCons + ") and userid in (select userid from v_pub_user_role_1 where rolecode in ('" + this.roleCode + "'))"); //

			HashVO[] hvs_users = commDMO.getHashVoArrayByDS(null, sb_sql.toString()); //		
			if (hvs_users != null && hvs_users.length > 0) {
				parBean.setParticiptMsg(getDefineInfo() + "\r\n根据登录人员的所属机构[" + loginUserCorpId + "," + loginUserCorpName + "]找到类型为[" + treePathCorpType + "]的父系机构[" + str_rootCorpId + "," + str_rootCorpName + "],其下共[" + al_childrenIds.size() + "]个子机构!!\r\n在其下共找到角色为[" + roleCode + "]的人员["
						+ hvs_users.length + "]个\r\n"); //
				WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs_users.length]; //
				for (int i = 0; i < userBeans.length; i++) {
					userBeans[i] = new WorkFlowParticipantUserBean(); //

					userBeans[i].setUserid(hvs_users[i].getStringValue("userid")); //人员
					userBeans[i].setUsercode(hvs_users[i].getStringValue("usercode")); //
					userBeans[i].setUsername(hvs_users[i].getStringValue("username")); //

					userBeans[i].setUserdeptid(hvs_users[i].getStringValue("deptid")); //参与者代表的机构
					userBeans[i].setUserdeptcode(hvs_users[i].getStringValue("deptcode")); //
					userBeans[i].setUserdeptname(hvs_users[i].getStringValue("deptname")); //

					userBeans[i].setUserroleid(str_roleid); //参与者代表的角色id
					userBeans[i].setUserrolecode(this.roleCode); //
					userBeans[i].setUserrolename(str_rolename); //角色名称

					userBeans[i].setParticipantType("动态参与者"); //
					userBeans[i].setSuccessParticipantReason("满足动态参与者条件,[" + treePathCorpType + "],角色[" + this.roleCode + "]"); ///
				}
				parBean.setParticipantUserBeans(userBeans); //
			} else {
				parBean.setParticiptMsg(getDefineInfo() + "\r\n根据登录人员的所属机构[" + loginUserCorpId + "," + loginUserCorpName + "]找到类型为[" + treePathCorpType + "]的父系机构[" + str_rootCorpId + "," + str_rootCorpName + "],共下共[" + al_childrenIds.size() + "]个子机构!!\r\n但加上人员角色[" + roleCode
						+ "]取交集时没有找到一个人员,很可能是机构[" + str_rootCorpId + "," + str_rootCorpName + "]下没有一个角色为[" + roleCode + "]的人员!!\r\n"); //
			}
		} else {
			parBean.setParticiptMsg(getDefineInfo() + "\r\n根据登录人员的所属机构[" + loginUserCorpId + "," + loginUserCorpName + " ]找类型为[" + treePathCorpType + "]的父系机构时没找到!\r\n这是因为没有在其父系机构中没有设置对机构类型!其父系路径机构的类型分别是:\r\n" + sb_parentpath.toString() + "\r\n"); //
		}
		long ll_end = System.currentTimeMillis(); //
		logger.debug("执行一个动态参与者[" + str_dynparName + "]结束,共耗时[" + (ll_end - ll_begin) + "]毫秒"); //
		return parBean;
	}

	private HashVO[] getAllCorpTreeHVS() throws Exception {
		return ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //取得机构缓存数据!!
	}

	private void findTreePath(String _corpId, HashMap _map, ArrayList _alPath) {
		_alPath.add(_corpId); //先将自己加入
		String str_parentid = (String) _map.get(_corpId); //找他的父亲记录ID!!
		if (str_parentid == null || str_parentid.trim().equals("")) { //如果父亲记录为空了,则说明到了根结点!!!
			return;
		} else {
			findTreePath(str_parentid, _map, _alPath); //继续找父亲记录的父亲!!递归算法
		}
	}
}
