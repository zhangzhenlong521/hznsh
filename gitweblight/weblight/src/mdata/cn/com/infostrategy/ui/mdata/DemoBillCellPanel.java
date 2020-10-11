package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class DemoBillCellPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 5529052685476844288L;

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		BillCellPanel cellPanel = new BillCellPanel(); //
		this.add(cellPanel, BorderLayout.CENTER); //
	}

}
