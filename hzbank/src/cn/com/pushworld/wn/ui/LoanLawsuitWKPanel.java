package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class LoanLawsuitWKPanel extends AbstractWorkPanel {
	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_LAWSUIT_IMP_ZPY_Q01");
		this.add(listPanel);
	}
}