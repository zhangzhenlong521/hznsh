package com.pushworld.ipushgrc.ui.lawcase.p010;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;

public class CaseApplyWFUIIntercept implements WorkFlowUIIntercept2 {

	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo) throws Exception {
		String sql = "update "+ billvo.getSaveTableName() +" set REPLYSTATE = '3' where id = "+billvo.getStringValue("id");
		UIUtil.executeBatchByDS(null, new String[]{sql});
	}

}

