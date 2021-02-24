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
 * �����̬�����߽�����ؼ��Ķ�̬������!!!�����Ż�����������,Ȼ�����ָ���Ļ��������ҵ������!! ���ҳ��û����µ������������!!
 * ���ݻ�������·����ϵ�ҳ�����·���ϵ�ĳ�ֻ������͵����л���!!! ������·��������,ֱ���ҵ����һ����������Ϊָ�����͵Ļ���!! Ȼ����û������µ�����������!! �������Ϻ�����!!!
 * Ȼ������Щ������Χ���ҳ�ָ����ɫ����Ա!! ����ȡ����!!!
 * Ҫ�����������!!!
 * @author Administrator
 *
 */
public class WDPUser_TreePathCorpChooseRole implements WorkflowDynamicParticipateIfc {

	private String str_dynparName = null; //��̬����������
	private String loginUserCorpId = null; //��¼��Ա��������
	private String treePathCorpType = null; //�������ϵĻ�������,�������,֧��,һ�����е�....
	private String roleCode = null; //��ɫ����
	private CommDMO commDMO = new CommDMO(); //
	private TBUtil tbUtil = new TBUtil(); //

	private Logger logger = WLTLogger.getLogger(WDPUser_TreePathCorpChooseRole.class); //

	/**
	 * ���췽��,���ݻ���Id���ɫ�������߸㶨һ��!! �����Ǿ���� "����+��ɫ"����ģʽ!!
	 * @param _loginUserDeptId
	 * @param _roleCode
	 */
	public WDPUser_TreePathCorpChooseRole(String _dynName, String _loginUserDeptId, String _treePathCorpType, String _roleCode) {
		this.str_dynparName = _dynName; //
		this.loginUserCorpId = _loginUserDeptId;
		this.treePathCorpType = _treePathCorpType; //��������..
		this.roleCode = _roleCode; //��ɫ����
	}

	private String getDefineInfo() {
		return "***��̬������[" + str_dynparName + "],���㷽��[��������ĳ�����͵��ϼ�����],ȡ���׻�������[" + treePathCorpType + "],��ɫ:[" + roleCode + "]"; //
	}

	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String code, String dealTyp, String code2, String code3, String name) throws Exception {
		long ll_begin = System.currentTimeMillis(); //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		if (loginUserCorpId == null) {
			parBean.setParticiptMsg("��¼��Ա��������Ϊ��,�޷����ж�̬�����߼���!,�뽫��¼��Ա������������!!\r\n"); //
			return parBean;
		}

		HashVO[] hvo_role = commDMO.getHashVoArrayByDS(null, "select id,code,name from pub_role where code='" + roleCode + "'"); //
		String str_roleid = null;
		String str_rolename = null;
		if (hvo_role.length > 0) {
			str_roleid = hvo_role[0].getStringValue("id"); //
			str_rolename = hvo_role[0].getStringValue("name"); //
		} else {
			parBean.setParticiptMsg(getDefineInfo() + ",���ñ����ڽ�ɫ��������û���ҵ���Ӧ�ļ�¼,��������Ϊ����Ǩ��ʱ�����������ʧЧ��!!\r\n"); ///
			return parBean; //ֱ�ӷ���
		}
		long ll_1 = System.currentTimeMillis(); //
		HashVO[] hvs_tree = getAllCorpTreeHVS(); //
		long ll_2 = System.currentTimeMillis(); //
		HashMap[] mapIdNames = tbUtil.getHashMapsFromHashVOs(hvs_tree, new String[][] { { "id", "parentid" }, { "id", "name" }, { "id", "corptype" }, { "id", "$rownum" },{ "id", "linkcode" } }); //��HashVO[]�г�ȡһ�ԶԵĹ��α�����,���������!!
		HashMap map_id_parentID = mapIdNames[0];
		HashMap map_id_name = mapIdNames[1];
		HashMap map_id_corptype = mapIdNames[2];
		HashMap map_id_index = mapIdNames[3];
		HashMap map_id_linkcode = mapIdNames[4];
		String loginUserCorpName = (String) map_id_name.get(loginUserCorpId); //��¼��Ա��������������!
		ArrayList alPath = new ArrayList(); //
		findTreePath(loginUserCorpId, map_id_parentID, alPath); //
		String str_rootCorpId = null; //
		String str_rootCorpName = null; //
		StringBuffer sb_parentpath = new StringBuffer(); //
		for (int i = alPath.size() - 1; i >= 0; i--) { //��������������,����Ҫ��������������!!
			String str_corpId = (String) alPath.get(i); //
			String str_corpName = (String) map_id_name.get(str_corpId); //
			String str_corpType = (String) map_id_corptype.get(alPath.get(i)); //
			sb_parentpath.append("����[" + str_corpId + "," + str_corpName + "]������=[" + str_corpType + "]\r\n"); //
			if (str_corpType != null && str_corpType.equals(treePathCorpType)) { //
				str_rootCorpId = str_corpId; //
				str_rootCorpName = str_corpName; //
				break;
			}
		}

		if (str_rootCorpId != null) { //����ҵ��������ҵĻ���,�������ĸ����!!
			parBean.setParticipantDeptId(str_rootCorpId); //
			parBean.setParticipantDeptLinkcode(map_id_linkcode.get(str_rootCorpId).toString()); //
			parBean.setParticipantDeptType(treePathCorpType); //

			//�ҳ������������������������!!!
			int li_index = (Integer) map_id_index.get(str_rootCorpId); //
			int li_level = hvs_tree[li_index].getIntegerValue("$level"); //��ǰ���Ĳ��!!
			ArrayList al_childrenIds = new ArrayList(); //���е������㶼����������!!! ����Ҫ������!!!���˻������������!!!ʣ�µľ������ɫ������Ļ���ȡ������!!!
			al_childrenIds.add(hvs_tree[li_index].getStringValue("id")); //
			for (int i = li_index + 1; i < hvs_tree.length; i++) { //��ĳһ����㿪ʼ,�����
				if (hvs_tree[i].getIntegerValue("$level") <= li_level) { //���ĳ���Ĳ�δ��ڵ��ڵ�ǰ�Ĳ��,˵�����ֵ���,���˳�ѭ��,�������!!
					break;
				} else {
					al_childrenIds.add(hvs_tree[i].getStringValue("id")); //���ĳ���Ĳ��С�ڸ����,˵������������!!!
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
				parBean.setParticiptMsg(getDefineInfo() + "\r\n���ݵ�¼��Ա����������[" + loginUserCorpId + "," + loginUserCorpName + "]�ҵ�����Ϊ[" + treePathCorpType + "]�ĸ�ϵ����[" + str_rootCorpId + "," + str_rootCorpName + "],���¹�[" + al_childrenIds.size() + "]���ӻ���!!\r\n�����¹��ҵ���ɫΪ[" + roleCode + "]����Ա["
						+ hvs_users.length + "]��\r\n"); //
				WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs_users.length]; //
				for (int i = 0; i < userBeans.length; i++) {
					userBeans[i] = new WorkFlowParticipantUserBean(); //

					userBeans[i].setUserid(hvs_users[i].getStringValue("userid")); //��Ա
					userBeans[i].setUsercode(hvs_users[i].getStringValue("usercode")); //
					userBeans[i].setUsername(hvs_users[i].getStringValue("username")); //

					userBeans[i].setUserdeptid(hvs_users[i].getStringValue("deptid")); //�����ߴ���Ļ���
					userBeans[i].setUserdeptcode(hvs_users[i].getStringValue("deptcode")); //
					userBeans[i].setUserdeptname(hvs_users[i].getStringValue("deptname")); //

					userBeans[i].setUserroleid(str_roleid); //�����ߴ���Ľ�ɫid
					userBeans[i].setUserrolecode(this.roleCode); //
					userBeans[i].setUserrolename(str_rolename); //��ɫ����

					userBeans[i].setParticipantType("��̬������"); //
					userBeans[i].setSuccessParticipantReason("���㶯̬����������,[" + treePathCorpType + "],��ɫ[" + this.roleCode + "]"); ///
				}
				parBean.setParticipantUserBeans(userBeans); //
			} else {
				parBean.setParticiptMsg(getDefineInfo() + "\r\n���ݵ�¼��Ա����������[" + loginUserCorpId + "," + loginUserCorpName + "]�ҵ�����Ϊ[" + treePathCorpType + "]�ĸ�ϵ����[" + str_rootCorpId + "," + str_rootCorpName + "],���¹�[" + al_childrenIds.size() + "]���ӻ���!!\r\n��������Ա��ɫ[" + roleCode
						+ "]ȡ����ʱû���ҵ�һ����Ա,�ܿ����ǻ���[" + str_rootCorpId + "," + str_rootCorpName + "]��û��һ����ɫΪ[" + roleCode + "]����Ա!!\r\n"); //
			}
		} else {
			parBean.setParticiptMsg(getDefineInfo() + "\r\n���ݵ�¼��Ա����������[" + loginUserCorpId + "," + loginUserCorpName + " ]������Ϊ[" + treePathCorpType + "]�ĸ�ϵ����ʱû�ҵ�!\r\n������Ϊû�����丸ϵ������û�����öԻ�������!�丸ϵ·�����������ͷֱ���:\r\n" + sb_parentpath.toString() + "\r\n"); //
		}
		long ll_end = System.currentTimeMillis(); //
		logger.debug("ִ��һ����̬������[" + str_dynparName + "]����,����ʱ[" + (ll_end - ll_begin) + "]����"); //
		return parBean;
	}

	private HashVO[] getAllCorpTreeHVS() throws Exception {
		return ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //ȡ�û�����������!!
	}

	private void findTreePath(String _corpId, HashMap _map, ArrayList _alPath) {
		_alPath.add(_corpId); //�Ƚ��Լ�����
		String str_parentid = (String) _map.get(_corpId); //�����ĸ��׼�¼ID!!
		if (str_parentid == null || str_parentid.trim().equals("")) { //������׼�¼Ϊ����,��˵�����˸����!!!
			return;
		} else {
			findTreePath(str_parentid, _map, _alPath); //�����Ҹ��׼�¼�ĸ���!!�ݹ��㷨
		}
	}
}
