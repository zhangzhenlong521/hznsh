package com.pushworld.ipushlbs.ui.lawreport.p050;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * ��Ȩ���뱨��
 * 
 * @author yinliang
 * @since 2011.12.19
 */
public class PowerApplyReport extends AbstractWorkPanel {
	WLTTabbedPane tabpane = null;

	@Override
	public void initialize() {
		tabpane = new WLTTabbedPane();
		// ��ά����
		BillReportPanel reportpanel = new BillReportPanel("LBS_POWERAPPLY_CODE1", "com.pushworld.ipushlbs.ui.lawreport.p050.PowerApplyPrint2");
		tabpane.addTab("��ά����ͳ��", reportpanel);

		this.add(tabpane);

	}

}
