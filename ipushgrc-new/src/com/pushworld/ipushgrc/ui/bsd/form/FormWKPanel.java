package com.pushworld.ipushgrc.ui.bsd.form;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * ±Ìµ•ø‚Œ¨ª§£°
 * @author hm
 *
 */
public class FormWKPanel extends AbstractWorkPanel {
	private static final long serialVersionUID = 1L;
	private WLTButton btn_insert,btn_edit,btn_delete,btn_show;
	public void initialize() {
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		BillListPanel listPanel  = new BillListPanel("BSD_FORM_CODE1");
		listPanel.addBatchBillListButton(new WLTButton[]{ btn_insert,btn_edit,btn_delete,btn_show});
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}
}
