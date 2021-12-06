package com.pushworld.ipushgrc.ui.wfrisk.p060;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 流程与风险统计表! 给领导充分展示的重要亮点! 也是决定该模块是否成功的最终成果!!!
 * A.主要有图表与Excel两种! Excel还要能实现钻取!!!
 * @author xch
 *
 */
public class WFAndRiskStatisWKPanel extends AbstractWorkPanel implements ChangeListener {
	private JTabbedPane tab;
	private JPanel jPanel2;
	private boolean ifclick2 = false;

	public void initialize() {
		this.setLayout(new BorderLayout());
		BillReportPanel reportPanel = new BillReportPanel("CMP_CMPFILE_REPORT", "com.pushworld.ipushgrc.bs.wfrisk.p060.CmpfileReportBuilderAdapter");
		tab = new JTabbedPane();
		jPanel2 = new JPanel();
		tab.addTab("文件统计", reportPanel);
		tab.addTab("流程统计", jPanel2);
		tab.addChangeListener(this);
		this.add(tab);
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1 && !ifclick2) {
			jPanel2.setLayout(new BorderLayout());
			jPanel2.add(new BillReportPanel("V_PROCESS_FILE_REPORT", "com.pushworld.ipushgrc.bs.wfrisk.p060.WFReportBuilderAdapter"));
			ifclick2 = true;
		}
	}
}
