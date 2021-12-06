package com.pushworld.ipushgrc.ui.score.p080;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;

/**
 * 违规积分-》违规积分统计【李春娟/2013-05-16】 
 *  
 * @author lcj
 *
 */
public class ScoreStatisWKPanel extends AbstractWorkPanel implements ChangeListener {
	private JTabbedPane tab;
	private JPanel jPanel2;
	private boolean ifclick2 = false;

	public void initialize() {
		this.setLayout(new BorderLayout());
		BillReportPanel reportPanel = new BillReportPanel("SCORE_USER_LCJ_Q05", "com.pushworld.ipushgrc.bs.score.p080.ScoreReportBuilderAdapter2");
		tab = new JTabbedPane();
		jPanel2 = new JPanel();
		tab.addTab("违规积分统计", reportPanel);
		tab.addTab("减免积分统计", jPanel2);
		tab.addChangeListener(this);
		this.add(tab);
	}

	public void stateChanged(ChangeEvent e) {
		if (tab.getSelectedIndex() == 1 && !ifclick2) {
			jPanel2.setLayout(new BorderLayout());
			jPanel2.add(new BillReportPanel("SCORE_REDUCE_LCJ_Q01", "com.pushworld.ipushgrc.bs.score.p080.ScoreReduceReportBuilderAdapter"));
			ifclick2 = true;
		}
	}
}
