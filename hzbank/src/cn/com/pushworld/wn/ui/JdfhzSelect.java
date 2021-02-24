package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class JdfhzSelect extends AbstractWorkPanel{
	private BillListPanel billListPanel;	
	
	@Override
	public void initialize() {
		billListPanel = new BillListPanel("WN_JDFKMHZB_02_QUERY");
		this.add(billListPanel);
	}

}
