package com.pushworld.ipushlbs.ui.powermanage.p030;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowProcessPanel;
import cn.com.infostrategy.ui.workflow.engine.WorkFlowUIIntercept2;

/**
 * 授权管理流程结束时执行
 * 
 * @author yinliang
 * @since 2011.12.14
 * 
 */
public class LawManageEndWFUIIntercept implements WorkFlowUIIntercept2 {

	public void afterWorkFlowEnd(WorkFlowProcessPanel panel, BillVO billvo) throws Exception {
		// 通过当前信息的流程ID
		// 取得当前流程的运行状态以及是否正常结束情况，来到这一步，status肯定是end了。。。endtype也不可能为空，肯定只有正常结束和非正常结束两种情况了。。。
		String sql_flow = "select endtype from pub_wf_prinstance where Id = " + billvo.getStringValue("wfprinstanceid");
		String flow_endtype = UIUtil.getStringValueByDS(null, sql_flow);
		String sql_flowstate = "";
		if (flow_endtype.equals("正常结束")) // 流程正常结束
			sql_flowstate = "update " + billvo.getSaveTableName() + " set flowstate = '审批通过' where id = " + billvo.getStringValue("id");
		else
			// 未正常结束
			sql_flowstate = "update " + billvo.getSaveTableName() + " set flowstate = '审批未通过' where id = " + billvo.getStringValue("id");
		UIUtil.executeBatchByDS(null, new String[] { sql_flowstate });
	}
}
