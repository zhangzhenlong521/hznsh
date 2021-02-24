package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

/**
 * 上报路线的动态参与者,包括上报路线1,上报路线2,上报路线3
 * @author xch
 *
 */
public abstract class AbstractWDPUser_ReportDeptLine implements WorkflowDynamicParticipateIfc {

	private String str_rolecode = null; //

	public abstract String getReportDeptType(); //定义上报路线的类型..

	/**
	 * 默认查询条件
	 */
	private AbstractWDPUser_ReportDeptLine() {
	}

	public AbstractWDPUser_ReportDeptLine(String _rolename) {
		this.str_rolecode = _rolename; //
	}

	/**
	 * 得到一个参与者
	 */
	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String code, String dealTyp, String code2, String _curractivityCode, String _curractivityName) throws Exception {
		CommDMO dmo = new CommDMO(); //
		String str_pkdept = _dealpool.getStringValue("participant_userdept"); //流程任务的所属部门,即该人是代表哪个部门处理的....
		String str_pkdept_code = null;
		String str_pkdept_name = null;

		HashVO[] hvo_selfdept = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_corp_dept where id='" + str_pkdept + "'"); //
		if (hvo_selfdept.length > 0) {
			str_pkdept_code = hvo_selfdept[0].getStringValue("code"); //
			str_pkdept_name = hvo_selfdept[0].getStringValue("name"); //
		}

		//计算下级层面机构
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		String str_reportdeptid = dmo.getStringValueByDS(null, "select reportdept" + getReportDeptType() + " from pub_corp_dept where id='" + str_pkdept + "'"); //取得本部门的LinkCode
		if (str_reportdeptid == null || str_reportdeptid.trim().equals("")) { //如果没有定义上报路线1,直接返回空.
			parBean.setParticipantDeptType(null); //
			parBean.setParticipantDeptId(null); //上报路线的机构ID
			parBean.setParticiptMsg("动态参与者定义,范围:[上报路线" + getReportDeptType() + "],角色:[" + str_rolecode + "],发现本人机构[" + str_pkdept_name + "]的上报路线" + getReportDeptType() + "(pub_corp_dept.reportdept" + getReportDeptType() + ")为空!"); //
			return parBean; //直接返回
		}

		HashVO[] hvo_reportdept = dmo.getHashVoArrayByDS(null, "select id,code,name,linkcode from pub_corp_dept where id='" + str_reportdeptid + "'"); //
		String str_reportdept_name = hvo_reportdept[0].getStringValue("name"); //
		String str_reportdept_linkcode = hvo_reportdept[0].getStringValue("linkcode"); //

		parBean.setParticipantDeptType("上报路线" + getReportDeptType()); //
		parBean.setParticipantDeptId(str_reportdeptid); //上报路线的机构ID
		parBean.setParticipantDeptLinkcode(str_reportdept_linkcode); //

		if (str_rolecode == null || str_rolecode.trim().equals("")) { //如果不定义角色,则直接返回!!这时就等于是发邮件的样子了!!!
			parBean.setParticiptMsg("动态参与者定义,范围:[上报路线" + getReportDeptType() + "],发现本人机构[" + str_pkdept_name + "]的上报路线机构[" + str_reportdept_name + "],角色编码为空:[" + str_rolecode + "],这样虽然没有直接找到人,但可以通过从部门选择人!"); ///
			return parBean; //直接返回
		}

		//如果有角色定义!!!!!!!!!!!!!!!!!!!!!!!
		HashVO[] hvo_role = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_role where code='" + str_rolecode + "'"); //
		String str_roleid = null;
		String str_rolename = null;
		if (hvo_role.length > 0) {
			str_roleid = hvo_role[0].getStringValue("id"); //
			str_rolename = hvo_role[0].getStringValue("name"); //
		}

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select ");
		sb_sql.append("t1.userid userid,");
		sb_sql.append("t2.code usercode, ");
		sb_sql.append("t2.name username, ");
		sb_sql.append("t1.userdept userdeptid, ");
		sb_sql.append("t3.code userdeptcode, ");
		sb_sql.append("t3.name userdeptname ");
		sb_sql.append("from pub_user_role t1 ");
		sb_sql.append("left join pub_user t2 on t1.userid=t2.id  ");
		sb_sql.append("left join pub_corp_dept t3 on t1.userdept=t3.id  ");
		sb_sql.append("where t3.linkcode like '" + str_reportdept_linkcode + "%' and t1.roleid='" + str_roleid + "' ");

		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //
		parBean.setParticiptMsg("动态参与者定义,范围:[上报路线" + getReportDeptType() + "],找到对应机构[" + str_reportdeptid + "," + str_reportdept_name + "],角色:[" + str_rolecode + "],找到人员数量[" + hvs.length + "]"); //
		if (hvs.length > 0) {
			WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs.length]; //
			for (int i = 0; i < userBeans.length; i++) {
				userBeans[i] = new WorkFlowParticipantUserBean(); //

				userBeans[i].setUserid(hvs[i].getStringValue("userid")); //人员
				userBeans[i].setUsercode(hvs[i].getStringValue("usercode")); //
				userBeans[i].setUsername(hvs[i].getStringValue("username")); //

				userBeans[i].setUserdeptid(hvs[i].getStringValue("userdeptid")); //参与者代表的机构
				userBeans[i].setUserdeptcode(hvs[i].getStringValue("userdeptcode")); //
				userBeans[i].setUserdeptname(hvs[i].getStringValue("userdeptname")); //

				userBeans[i].setUserroleid(str_roleid); //参与者代表的角色
				userBeans[i].setUserrolecode(str_rolecode); //
				userBeans[i].setUserrolename(str_rolename); //

				userBeans[i].setParticipantType("动态参与者"); //
				userBeans[i].setSuccessParticipantReason("满足动态参与者条件,本机构的[上报路线" + getReportDeptType() + "]的机构[" + str_reportdeptid + "],角色[" + str_rolename + "]"); //
			}

			parBean.setParticipantUserBeans(userBeans); //
		}

		return parBean; //
	}

}
