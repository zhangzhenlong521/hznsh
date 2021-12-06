package com.pushworld.ipushlbs.ui.lawreport.p010;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.report.BillReportPanel;
/**
 * 格式合同统计
 * @author yinliang
 * @since 2011.12.19
 *
 */
public class FormatConstactReport extends AbstractWorkPanel {
	WLTTabbedPane tabpane = null ;
	@Override
		public void initialize() {
			tabpane = new WLTTabbedPane();
			// 格式合同 机构 统计
			BillReportPanel dr2 = new BillReportPanel("LBS_STDFILE_CODE1_1", "com.pushworld.ipushlbs.ui.lawreport.p010.FormatPrint1");
			tabpane.addTab("机构合同统计", dr2);
			// 多维报表
			BillReportPanel reportpanel = new BillReportPanel("FORMAT_DEAL_CHECK_CODE2_1", "com.pushworld.ipushlbs.ui.lawreport.p010.FormatPrint2");
			tabpane.addTab("合同打印统计", reportpanel);
			
			this.add(tabpane);

	}

}
