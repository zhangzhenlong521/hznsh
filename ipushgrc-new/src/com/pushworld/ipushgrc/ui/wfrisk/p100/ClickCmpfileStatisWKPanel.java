package com.pushworld.ipushgrc.ui.wfrisk.p100;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 流程文件查看统计! 
 * @author lcj
 *
 */
public class ClickCmpfileStatisWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.add(new BillReportPanel("V_CMPFILE_CLICKLOG_REPORT", "com.pushworld.ipushgrc.bs.wfrisk.p100.ClickCmpfileReportBuilderAdapter")); //
	}

}
