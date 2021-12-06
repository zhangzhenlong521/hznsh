package com.pushworld.ipushgrc.ui.lawcase.p010;

import java.awt.BorderLayout;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CaseApplyWKPanel extends LawyerImportWorkPanel{
	private BillListPanel billList = null; //
	private WLTButton insertbtn;
	private WLTButton editbtn;
	private WLTButton deletebtn;
	private WLTButton btn_commit =null;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		billList = new BillListPanel("LBS_CASE_CODE1"); //
		billList.addBillListButtonActinoListener(this);
		billList.setDataFilterCustCondition("REPLYSTATE!=3");
		billList.setItemVisible("STATE", false);
		billList.setItemVisible("JUDGERESULT", false);
		billList.setItemVisible("CHARGETARGET", true);
		insertbtn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		editbtn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		deletebtn = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		billList.addBatchBillListButton(new WLTButton[] { insertbtn, editbtn, deletebtn });
		if(new TBUtil().getSysOptionBooleanValue("立案是否走流程", true)){
			btn_commit= WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR);
			billList.addBillListButton(btn_commit);
			billList.getTempletItemVO("REPLYSTATE").setDefaultvalueformula("getComBoxItemVO(\"1\",\"001\",\"未提交\")");
		}else{
			billList.getTempletItemVO("REPLYSTATE").setDefaultvalueformula("getComBoxItemVO(\"3\",\"001\",\"审核通过\")");
		}
		billList.repaintBillListButton();
		this.add(billList, BorderLayout.CENTER); //
	}
	
}
