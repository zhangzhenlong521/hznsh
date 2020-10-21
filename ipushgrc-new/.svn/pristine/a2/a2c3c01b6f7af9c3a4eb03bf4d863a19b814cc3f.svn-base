package com.pushworld.ipushgrc.ui.rule.p030;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 制度评审流程!! 就是对表[rule_apply_wf]走工作流!!
 * @author xch
 *
 */
public class RuleCreateApplyWKPanel extends AbstractWorkPanel {

	private BillListPanel billList; //

	private WLTButton btn_insert, btn_update, btn_delete, btn_list; //

	@Override
	public void initialize() {
		billList = new BillListPanel("RULE_APPLY_WF_CODE1"); //

		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
		billList.repaintBillListButton(); //刷新按钮!!!

		this.add(billList); //
	}

}

