package com.pushworld.ipushgrc.ui.cmpreport.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;
/**
 * ���ֳͷ�ͳ��
 * @author yinliang
 *
 */
public class CmpScorePunishReportWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillReportPanel reportPanel = new BillReportPanel("CMP_PUNISH_RECORD_CODE1", "com.pushworld.ipushgrc.bs.cmpscore.Y_ScorePunishReportBuilder");
		this.add(reportPanel);
	}

}
