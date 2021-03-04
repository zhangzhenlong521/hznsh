package cn.com.infostrategy.ui.report;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class TestReportWFPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillReportPanel reportPanel = new BillReportPanel("test_xch_report", "cn.com.infostrategy.bs.report.MyTestReportBuilder"); //
		this.add(reportPanel); //
	}

}
