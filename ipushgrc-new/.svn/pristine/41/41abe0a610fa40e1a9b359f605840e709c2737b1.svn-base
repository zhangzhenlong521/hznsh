package com.pushworld.ipushgrc.ui.cmpreport.p110;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 合规报告统计
 * @author SFJ
 *
 */
public class CmpReportStatisWKPanel extends AbstractWorkPanel {
	private WLTTabbedPane tab = null;
	private JPanel panel2 = null;
	private BillReportPanel report1 = null;
	private BillReportPanel report2 = null;
	private boolean ifclick2 = false;

	@Override
	public void initialize() {
		tab = new WLTTabbedPane();
		report1 = new BillReportPanel("CMP_REPORT_Report", "com.pushworld.ipushgrc.bs.cmpreport.p110.CmpReportStaticBuildDate1");
		panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		tab.addTab("综合报告统计", report1);
		tab.addTab("专项报告统计", panel2);
		tab.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				change(e);
			}
		});
		this.add(tab); //
	}

	private void change(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1) {
			if (!ifclick2) {
				report2 = new BillReportPanel("CMP_REPORT2_Report", "com.pushworld.ipushgrc.bs.cmpreport.p110.CmpReportStaticBuildDate2");
				panel2.add(report2);
				ifclick2 = true;
			}
		}
	}

}
