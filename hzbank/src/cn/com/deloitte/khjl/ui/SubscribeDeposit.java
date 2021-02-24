package cn.com.deloitte.khjl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class SubscribeDeposit  extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	@Override
	public void initialize() {
		listPanel=new BillListPanel("");
		
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		
	}

	

}