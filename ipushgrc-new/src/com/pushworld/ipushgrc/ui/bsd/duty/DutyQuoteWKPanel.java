package com.pushworld.ipushgrc.ui.bsd.duty;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 岗责引用库维护页面
 * @author zhanghao
 *
 */
public class DutyQuoteWKPanel extends AbstractWorkPanel{
	private BillListPanel billList = null;
	private WLTButton btn_insert, btn_edit, btn_delete;

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_OPERATE_PRACTICE_GUIDE_CODE1");
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); // 新增
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //编辑
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //删除
		billList.addBatchBillListButton(new WLTButton[] {btn_insert, btn_edit, btn_delete});
		billList.repaintBillListButton();
		this.add(billList);
	}
}
