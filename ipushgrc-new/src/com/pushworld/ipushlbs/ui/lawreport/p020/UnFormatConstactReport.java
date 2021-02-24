package com.pushworld.ipushlbs.ui.lawreport.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * �Ǹ�ʽ��ͬͳ��
 * 
 * @author yinliang
 * @since 2011.12.19
 * 
 */
public class UnFormatConstactReport extends AbstractWorkPanel {

	WLTTabbedPane tabpane = null;

	@Override
	public void initialize() {
		tabpane = new WLTTabbedPane();
		// �Ǹ�ʽ��ͬ ���� ͳ��
		BillReportPanel dr2 = new BillReportPanel("LBS_UNSTDFILE_CODE1_1", "com.pushworld.ipushlbs.ui.lawreport.p020.UnFormatPrint1");
		tabpane.addTab("������ͬͳ��", dr2);
		// ��ά����
		BillReportPanel reportpanel = new BillReportPanel("UNFORMAT_DEAL_CHECK_CODE2", "com.pushworld.ipushlbs.ui.lawreport.p020.UnFormatPrint2");
		tabpane.addTab("��ͬ��ӡͳ��", reportpanel);

		this.add(tabpane);

	}

}
