package com.pushworld.ipushgrc.ui.law.p040;

import cn.com.infostrategy.ui.common.WLTButton;

import com.pushworld.ipushgrc.ui.law.p030.LawMappingEditWKPanel;

/**
 * 法规映射查询!
 * 它应该继承于编辑界面,但会去掉那映射按钮
 * @author xch
 *
 */
public class LawMappingQueryWKPanel extends LawMappingEditWKPanel {

	private WLTButton btn_list;

	@Override
	public void initialize() {
		super.initialize(); //
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		billList_mapping.addBatchBillListButton(new WLTButton[] { btn_list });
		billList_mapping.setBillListBtnVisiable("映射", false);
		billList_mapping.setBillListBtnVisiable("删除", false);
		billList_mapping.repaintBillListButton();
		//隐藏映射按钮!!
	}
}
