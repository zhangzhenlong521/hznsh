package com.pushworld.ipushgrc.ui.cmpcheck.p060;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * ���ͳ��,��һ��ģ�������ͳ�Ʒ���һ�����ܵ��ϸ㶨!!!
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
