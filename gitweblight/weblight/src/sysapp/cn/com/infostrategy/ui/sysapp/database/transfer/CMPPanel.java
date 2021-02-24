package cn.com.infostrategy.ui.sysapp.database.transfer;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class CMPPanel extends AbstractWorkPanel {
	private static final long serialVersionUID = -7379929844794709L;
	private CMPPanel_SoloTransfer soloCompare = null;
	private CMPPanel_MultiTransfer muliCompare = null;
	private JTabbedPane jTabbedpane = null;

	@Override
	public void initialize() {
		soloCompare = new CMPPanel_SoloTransfer();
		muliCompare = new CMPPanel_MultiTransfer();
		soloCompare.initialize();
		muliCompare.initialize();
		jTabbedpane = new JTabbedPane();
		jTabbedpane.add("单表迁移", soloCompare);
		jTabbedpane.add("多表迁移", muliCompare);
		this.setLayout(new BorderLayout());
		this.add(jTabbedpane, BorderLayout.CENTER);
	}

}
