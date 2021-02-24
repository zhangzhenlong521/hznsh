package com.pushworld.ipushlbs.ui.lawreport.p010;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.report.BillReportPanel;
/**
 * ��ʽ��ͬͳ��
 * @author yinliang
 * @since 2011.12.19
 *
 */
public class FormatConstactReport extends AbstractWorkPanel {
	WLTTabbedPane tabpane = null ;
	@Override
		public void initialize() {
			tabpane = new WLTTabbedPane();
			// ��ʽ��ͬ ���� ͳ��
			BillReportPanel dr2 = new BillReportPanel("LBS_STDFILE_CODE1_1", "com.pushworld.ipushlbs.ui.lawreport.p010.FormatPrint1");
			tabpane.addTab("������ͬͳ��", dr2);
			// ��ά����
			BillReportPanel reportpanel = new BillReportPanel("FORMAT_DEAL_CHECK_CODE2_1", "com.pushworld.ipushlbs.ui.lawreport.p010.FormatPrint2");
			tabpane.addTab("��ͬ��ӡͳ��", reportpanel);
			
			this.add(tabpane);

	}

}
