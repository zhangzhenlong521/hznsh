package com.pushworld.ipushlbs.ui.lawreport.p050;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 授权申请报表
 * 
 * @author yinliang
 * @since 2011.12.19
 */
public class PowerApplyReport extends AbstractWorkPanel {
	WLTTabbedPane tabpane = null;

	@Override
	public void initialize() {
		tabpane = new WLTTabbedPane();
		// 多维报表
		BillReportPanel reportpanel = new BillReportPanel("LBS_POWERAPPLY_CODE1", "com.pushworld.ipushlbs.ui.lawreport.p050.PowerApplyPrint2");
		tabpane.addTab("多维报表统计", reportpanel);

		this.add(tabpane);

	}

}
