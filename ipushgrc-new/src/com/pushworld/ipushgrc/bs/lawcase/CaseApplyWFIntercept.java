package com.pushworld.ipushgrc.bs.lawcase;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.workflow.WFIntercept2IFC;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

public class CaseApplyWFIntercept implements WFIntercept2IFC {
	/**
	 * 流程开始启动是拦截器
	 */
	public void afterAction(WFParVO callVO, String _loginuserid, BillVO _billvo, String _dealtype) throws Exception {
		//起草提交成功 状态变为审批中
		String id = _billvo.getStringValue("id");
		String sql = "update "+_billvo.getSaveTableName()+" set REPLYSTATE='2' where id = "+id ;
		new CommDMO().executeBatchByDS(null, new String[]{sql});
	}

	public void beforeAction(WFParVO callVO, String _loginuserid, BillVO _billvo, String _dealtype) throws Exception {

	}

}
