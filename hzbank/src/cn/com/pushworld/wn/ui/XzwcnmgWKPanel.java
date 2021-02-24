package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.listcomp.QuickSearchDialog;

public class XzwcnmgWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_XZWCNMG_ZPY_Q01");
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	}

}
