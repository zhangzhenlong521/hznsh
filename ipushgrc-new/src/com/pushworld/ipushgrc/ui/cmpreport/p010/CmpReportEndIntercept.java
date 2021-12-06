package com.pushworld.ipushgrc.ui.cmpreport.p010;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;

public class CmpReportEndIntercept implements WorkFlowUIIntercept2 {

	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo)
			throws Exception {
		String id = billvo.getStringValue("id");
		String sql = "update "+billvo.getSaveTableName()+" set state='Á÷³Ì½áÊø' where id = "+id ;
		UIUtil.executeBatchByDS(null, new String[]{sql});

	}

}
