package com.pushworld.ipushgrc.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.BillDialog;

import com.pushworld.ipushgrc.ui.risk.p110.RiskAlarmWKPanel;

public class SysboardAction extends AbstractAction {

	public void actionPerformed(ActionEvent e) {
		JPanel deskTopPanel = (JPanel) this.getValue("DeskTopPanel");
		BillDialog billdialog = new BillDialog(deskTopPanel, 1000, 700);
		billdialog.getContentPane().setLayout(new BorderLayout());
		RiskAlarmWKPanel riskpanel = new RiskAlarmWKPanel();
		riskpanel.initialize();
		billdialog.add(riskpanel, BorderLayout.CENTER);
		billdialog.setTitle("∑Áœ’‘§æØ");
		billdialog.setVisible(true);
	}
}
