package cn.com.infostrategy.bs.workflow;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

public class WDPUser_SameBankChooseRole implements WorkflowDynamicParticipateIfc {

	private String str_areakey = null;
	private String str_areaname = null; //
	private String str_deptid = null; //
	private String str_rolecode = null; //

	/**
	 * 默认查询条件
	 */
	private WDPUser_SameBankChooseRole() {
	}

	public WDPUser_SameBankChooseRole(String _areaKey, String _areaName, String _loginUserDeptID, String _rolecode) {
		this.str_areakey = _areaKey;
		this.str_areaname = _areaName; //
		this.str_deptid = _loginUserDeptID; //
		this.str_rolecode = _rolecode; //
	}

	private String getDefineInfo() {
		return "动态参与者定义,范围:[" + str_areaname + "],角色编码为空:[" + str_rolecode + "]"; //
	}

	/**
	 * 得到一个参与者
	 */
	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String code, String dealTyp, String code2, String _curractivityCode, String _curractivityName) throws Exception {
		CommDMO commDmo = new CommDMO(); //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		parBean.setParticipantDeptType(str_areaname); //部门类型

		if (str_deptid == null || str_deptid.trim().equals("") || str_deptid.trim().equalsIgnoreCase("null")) {
			parBean.setParticiptMsg(getDefineInfo() + ",但登录人员的所属部门为空!!"); ///
			return parBean; //直接返回
		}

		String[][] str_bldeptidName = commDmo.getStringArrayByDS(null, "select " + str_areakey + "," + str_areakey + "_name from pub_corp_dept where id='" + str_deptid + "'"); //找出登录人员某种所属类型的机构.
		if (str_bldeptidName == null || str_bldeptidName.length == 0) {
			parBean.setParticiptMsg(getDefineInfo() + ",但登录人员的所属机构id[" + str_deptid + "]在机构基本表中没有找到对应记录!"); ///
			return parBean; //直接返回
		}

		String str_bldeptid = str_bldeptidName[0][0];
		String str_bldeptname = str_bldeptidName[0][1];
		if (str_bldeptid == null || str_bldeptid.trim().equals("") || str_bldeptid.trim().equalsIgnoreCase("null")) {
			parBean.setParticiptMsg(getDefineInfo() + ",但登录人员的所属[" + str_areaname + "](" + str_areakey + ")为空!!"); ///
			return parBean; //直接返回
		}

		parBean.setParticipantDeptId(str_bldeptid); //部门主键.

		if (str_rolecode == null || str_rolecode.trim().equals("")) { //如果不定义角色,则直接返回!!这时就等于是发邮件的样子了!!!
			parBean.setParticiptMsg(getDefineInfo() + ",这样虽然不能直接找到人,但可以通过从部门选择人!"); ///
			return parBean; //直接返回
		}

		//如果定义了角色!!!!!!!!!!
		HashVO[] hvo_role = commDmo.getHashVoArrayByDS(null, "select id,code,name from pub_role where code='" + str_rolecode + "'"); //
		String str_roleid = null;
		String str_rolename = null;
		if (hvo_role.length > 0) {
			str_roleid = hvo_role[0].getStringValue("id"); //
			str_rolename = hvo_role[0].getStringValue("name"); //
		} else {
			parBean.setParticiptMsg(getDefineInfo() + ",但该编码在角色基本表中没有找到对应的记录!!"); ///
			return parBean; //直接返回
		}

		TBUtil tbUtil = new TBUtil();
		String[] str_allDeptIDs = commDmo.getStringArrayFirstColByDS(null, "select id from pub_corp_dept where " + str_areakey + "='" + str_bldeptid + "'"); //
		String str_deptincondition = tbUtil.getInCondition(str_allDeptIDs); //

		//找出本部门的所有人,由于一个人可以兼职三个部门,所以条件比以前加了许多!!
		HashMap map_user_dept = commDmo.getHashMapBySQLByDS(null, "select t1.userid c1,t1.userid c2 from pub_user_post t1,pub_user t2 where t1.userid=t2.id and t1.userdept in (" + str_deptincondition + ") and (t2.status is null or t2.status<>'D')"); //找出本机构的所有人员
		HashMap map_user_role = commDmo.getHashMapBySQLByDS(null, "select userid c1,userid c2 from pub_user_role where roleid='" + str_roleid + "'"); //
		String[] str_users_1 = (String[]) (map_user_dept.keySet().toArray(new String[0]));
		String[] str_users_2 = (String[]) (map_user_role.keySet().toArray(new String[0]));
		String[] str_userids = tbUtil.getInterSectionFromTwoArray(str_users_1, str_users_2); //取交集!!!
		String str_condition = new TBUtil().getInCondition(str_userids); //

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
		sb_sql.append("where userid in (" + str_condition + ")"); //

		HashVO[] hvs = commDmo.getHashVoArrayByDS(null, sb_sql.toString()); //
		parBean.setParticiptMsg(getDefineInfo() + ",实际是:[" + str_bldeptname + "],共[" + str_allDeptIDs.length + "]个子机构,找到人员数量[" + hvs.length + "]"); //
		if (hvs != null && hvs.length > 0) {
			WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs.length]; //
			for (int i = 0; i < userBeans.length; i++) {
				userBeans[i] = new WorkFlowParticipantUserBean(); //

				userBeans[i].setUserid(hvs[i].getStringValue("userid")); //人员
				userBeans[i].setUsercode(hvs[i].getStringValue("usercode")); //
				userBeans[i].setUsername(hvs[i].getStringValue("username")); //

				userBeans[i].setUserdeptid(hvs[i].getStringValue("deptid")); //参与者代表的机构
				userBeans[i].setUserdeptcode(hvs[i].getStringValue("deptcode")); //
				userBeans[i].setUserdeptname(hvs[i].getStringValue("deptname")); //

				userBeans[i].setUserroleid(str_roleid); //参与者代表的角色
				userBeans[i].setUserrolecode(str_rolecode); //
				userBeans[i].setUserrolename(str_rolename); //

				userBeans[i].setParticipantType("动态参与者"); //
				userBeans[i].setSuccessParticipantReason("满足动态参与者条件,[" + str_areaname + "],角色[" + str_rolename + "]"); ///
			}
			parBean.setParticipantUserBeans(userBeans); //
		}
		return parBean; //
	}
}
