package com.pushworld.ipushgrc.ui.cmpcheck.p060;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 检查统计,将一个模块的所有统计放在一个功能点上搞定!!!
 * @author Gwang
 *
 */
public class CheckStatisWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillReportPanel reportPanel = new BillReportPanel("CMP_CHECK_REPORT", "com.pushworld.ipushgrc.bs.cmpcheck.CheckReportBuilderAdapter");
		this.add(reportPanel);	
	}

}
