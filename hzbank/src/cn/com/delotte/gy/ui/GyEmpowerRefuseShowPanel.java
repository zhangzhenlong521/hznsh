package cn.com.delotte.gy.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class GyEmpowerRefuseShowPanel extends AbstractWorkPanel {

	private BillListPanel listPanel;
	@Override
	public void initialize() {
		listPanel=new BillListPanel("EXCEL_TAB_148_SHOW_CODE");
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

}
