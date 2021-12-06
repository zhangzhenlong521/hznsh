package com.pushworld.ipushgrc.ui.cmpreport.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;
/**
 * 积分统计
 * @author yinliang
 *
 */
public class CmpScoreLostReportWKPanel extends AbstractWorkPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3156865042007448676L;

	@Override
	public void initialize() {
		BillReportPanel reportPanel = new BillReportPanel("CMP_SCORE_RECORD_CODE1","com.pushworld.ipushgrc.bs.cmpscore.Y_ScoreLostReportBuilder");
		this.add(reportPanel);
	}

}
