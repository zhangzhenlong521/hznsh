package com.pushworld.ipushgrc.ui.risk.p040;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * ���յ�ͳ��! �������ǳ��õ��Ǹ��ɶ�ά��ȡ�ı���!!
 * @author xch
 *
 */
public class RiskStatisWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.add(new BillReportPanel("V_RISK_PROCESS_FILE_REPORT", "com.pushworld.ipushgrc.bs.risk.p040.RiskReportBuilderAdapter")); //
	}

}
