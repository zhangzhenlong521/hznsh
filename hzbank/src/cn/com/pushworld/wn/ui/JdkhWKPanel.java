package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class JdkhWKPanel extends AbstractWorkPanel implements ActionListener {
	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_JDKH_ZPY_Q01");
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
