package com.pushworld.ipushgrc.ui.cmpscore.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CmpScoreRuleWKPanel extends AbstractWorkPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3483328053469434149L;
	private WLTButton btn_add=null;
	private WLTButton btn_edit=null;
	private WLTButton btn_delete=null;
	private WLTButton btn_view=null;
	
	public String getTempletcode() {
		return "CMP_SCORESTAND_CODE1";
	}

	@Override
	public void initialize() {
		BillListPanel list=new BillListPanel(this.getTempletcode());
		
		btn_add=WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "ÐÂÔö");
		btn_edit=WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "ÐÞ¸Ä");
		btn_delete=WLTButton.createButtonByType(WLTButton.LIST_DELETE, "É¾³ý");
		btn_view=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "ä¯ÀÀ");
		
		list.addBatchBillListButton(new WLTButton[]{btn_add,btn_edit,btn_delete,btn_view});
		list.repaintBillListButton();
		
		this.add(list);
	}
}
