package com.pushworld.ipushgrc.ui.lawcase.p010;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CaseQueryWKPanel extends AbstractWorkPanel {
	private BillListPanel billList = null; //
	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billList = new BillListPanel("LBS_CASE_CODE1"); //
		billList.setDataFilterCustCondition("REPLYSTATE=3");
		WLTButton showcardbtn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		billList.addBillListButton(showcardbtn);
		billList.repaintBillListButton();
		this.add(billList, BorderLayout.CENTER); //
	}
}
