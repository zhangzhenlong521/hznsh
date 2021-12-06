package com.pushworld.ipushgrc.ui.lawcase.p010;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CaseEditWKPanel extends LawyerImportWorkPanel{
	private BillListPanel billList = null; //
	private WLTButton editbtn;
	private WLTButton deletebtn;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billList = new BillListPanel("LBS_CASE_CODE1"); //
		billList.addBillListButtonActinoListener(this);
		billList.setDataFilterCustCondition("REPLYSTATE=3");
		billList.setItemVisible("STATE", false);
		billList.setItemVisible("JUDGERESULT", false);
		billList.setItemVisible("CHARGETARGET", true);
		editbtn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		deletebtn = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		billList.addBatchBillListButton(new WLTButton[] { editbtn, deletebtn });
		billList.repaintBillListButton();
		this.add(billList, BorderLayout.CENTER); //
	}
}
