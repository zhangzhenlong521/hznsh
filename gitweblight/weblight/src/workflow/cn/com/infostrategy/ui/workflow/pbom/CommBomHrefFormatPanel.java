package cn.com.infostrategy.ui.workflow.pbom;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;

public class CommBomHrefFormatPanel extends JPanel {

	public CommBomHrefFormatPanel(String _regcode) {
		this.setLayout(new BorderLayout()); //
		BillFormatPanel formatPanel = BillFormatPanel.getRegisterFormatPanel(_regcode); //
		this.add(formatPanel); //
	}
}
