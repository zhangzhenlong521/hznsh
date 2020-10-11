package cn.com.infostrategy.ui.report.style3;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class TestBillChartReportPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		DefaultStyleReportPanel_3 reportPanel = new DefaultStyleReportPanel_3("wangjian_query_1", "cn.com.infostrategy.bs.report.style3.TestBillChartReportBuilder"); //
		this.add(reportPanel); //
	}

	

}
