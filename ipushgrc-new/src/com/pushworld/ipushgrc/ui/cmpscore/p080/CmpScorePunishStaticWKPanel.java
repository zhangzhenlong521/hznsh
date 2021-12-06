package com.pushworld.ipushgrc.ui.cmpscore.p080;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

public class CmpScorePunishStaticWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillReportPanel reportPanel = new BillReportPanel("CMP_SCORE_PUNISH_Report", "com.pushworld.ipushgrc.bs.cmpscore.CmpScorePunishReportBuilderAdapter");
		this.add(reportPanel);
	}

}
