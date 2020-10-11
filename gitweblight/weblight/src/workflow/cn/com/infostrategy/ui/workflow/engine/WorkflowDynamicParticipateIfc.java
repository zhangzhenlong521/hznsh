package cn.com.infostrategy.ui.workflow.engine;

import cn.com.infostrategy.bs.workflow.WorkFlowParticipantBean;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;

public interface WorkflowDynamicParticipateIfc {

	/**
	 * 取得参与者列表,应该返回pub_user.id的一个数组
	 * @param _billvo 
	 * @param _loginuserid 
	 * @param _fromactivityCode 
	 * @param _transitionCode 
	 * @param _dealpool 
	 */
	public WorkFlowParticipantBean getDynamicParUsers(String _loginuserid, BillVO _billvo, HashVO _dealpool, String _transitionCode, String _transitionDealTyp, String _fromactivityCode,String _curractivityCode,String _curractivityName) throws Exception; //
}
