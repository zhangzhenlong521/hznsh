package com.pushworld.ipushgrc.ui.lawcase.p020;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.lawcase.p010.LawyerImportWorkPanel;

public class DeCaseEditWKPanel extends LawyerImportWorkPanel{
	private static final long serialVersionUID = 1L;
	private BillListPanel billList = null; //
	private WLTButton insertbtn;
	private WLTButton editbtn;
	private WLTButton deletebtn;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billList = new BillListPanel("LBS_DECASE_CODE1"); //
		billList.addBillListButtonActinoListener(this);
		insertbtn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		editbtn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		deletebtn = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		billList.addBatchBillListButton(new WLTButton[] { insertbtn, editbtn, deletebtn });
		billList.repaintBillListButton();
		this.add(billList, BorderLayout.CENTER); //
	}
}