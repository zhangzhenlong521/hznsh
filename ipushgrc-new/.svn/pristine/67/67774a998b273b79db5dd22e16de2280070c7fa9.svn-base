package com.pushworld.ipushgrc.ui.tools.intercepts;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.workflow.WorkFlowParticipantBean;
import cn.com.infostrategy.bs.workflow.WorkFlowParticipantUserBean;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

/**
 * @author Gwang 2016-12-17 下午06:11:06
 *  工作流的动态参与者： 找流程的创建人
 */
public class WFDynamicCreateUser implements WorkflowDynamicParticipateIfc {

	public WorkFlowParticipantBean getDynamicParUsers(String loginuserid, BillVO billvo, HashVO dealpool, String transitionCode, String transitionDealTyp, String fromactivityCode, String curractivityCode, String curractivityName) throws Exception {
		CommDMO dmo = new CommDMO();
		// 流程的创建人
		String create_Userid = billvo.getStringValue("create_userid");
		// 流程创建人的 部门
		String user_dept = dmo.getStringValueByDS(null, "select USERDEPT from pub_user_post where userid ='" + create_Userid + "'");

		//		// 流程创建人的角色 信息
		//		HashVO[] user_role_message = dmo.getHashVoArrayByDS(null, "select id,name,code from pub_role where name ='总行员工'");

		// 创建人的 信息
		HashVO[] user_message = dmo.getHashVoArrayByDS(null, "select id,name,code from pub_user where id ='" + create_Userid + "'");
		if (user_message == null || user_message.length == 0) {//判断一下是否为空【李春娟/2016-12-18】
			return null;
		}
		// 创建人的 部门信息
		HashVO[] user_dept_message = dmo.getHashVoArrayByDS(null, "select id,name,code from pub_corp_dept where id ='" + user_dept + "'");

		WorkFlowParticipantUserBean[] ParticpantUserBeans = null;
		ParticpantUserBeans = new WorkFlowParticipantUserBean[1];
		ParticpantUserBeans[0] = new WorkFlowParticipantUserBean();
		ParticpantUserBeans[0].setUserid(create_Userid);
		ParticpantUserBeans[0].setUsercode(user_message[0].getStringValue("code"));
		ParticpantUserBeans[0].setUsername(user_message[0].getStringValue("name"));

		ParticpantUserBeans[0].setUserdeptid(user_dept);
		if (user_dept_message != null && user_dept_message.length > 0) {//判断一下是否为空【李春娟/2016-12-18】
			ParticpantUserBeans[0].setUserdeptcode(user_dept_message[0].getStringValue("code"));
			ParticpantUserBeans[0].setUserdeptname(user_dept_message[0].getStringValue("name"));
		}

		//统一写流程创建人
		ParticpantUserBeans[0].setUserroleid("-999");
		ParticpantUserBeans[0].setUserrolename("流程创建人");
		ParticpantUserBeans[0].setUserrolecode("流程创建人");

		ParticpantUserBeans[0].setParticipantType("动态参与者");

		WorkFlowParticipantBean wfpd = new WorkFlowParticipantBean();
		wfpd.setParticipantDeptId(user_dept);
		wfpd.setParticipantUserBeans(ParticpantUserBeans);
		return wfpd;
	}

}
