package com.pushworld.ipushgrc.ui.lawcase.p020;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class DeCaseQueryWKPanel extends AbstractWorkPanel {
	private BillListPanel billList = null; //
	WLTButton showcardbtn=null;
	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billList = new BillListPanel("LBS_DECASE_CODE1"); //
	    showcardbtn=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
	    billList.addBillListButton(showcardbtn);
	    billList.repaintBillListButton();
		this.add(billList, BorderLayout.CENTER); //
	}
}

	

