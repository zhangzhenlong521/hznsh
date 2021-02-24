package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CsbhzQuery extends AbstractWorkPanel {
	private BillListPanel billListPanel;
	
	@Override
	public void initialize() {
		billListPanel = new BillListPanel("WN_CSBHZ_01_QUERY");
		this.add(billListPanel);
	}

}
