package com.pushworld.ipushlbs.ui.lawreport.p030;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 主诉案件追踪---多多维报表面板
 * @author wupeng
 *
 */
public class LawCaseMultiReport extends AbstractWorkPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		BillReportPanel report=new BillReportPanel("LBS_CASE_SELF_report","com.pushworld.ipushlbs.ui.lawreport.p030.LawCaseMultiReportQueryData");
		this.add(report);
	}
}