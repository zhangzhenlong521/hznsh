package com.pushworld.ipushgrc.ui.bsd.duty;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * �������ÿ�ά��ҳ��
 * @author zhanghao
 *
 */
public class DutyQuoteWKPanel extends AbstractWorkPanel{
	private BillListPanel billList = null;
	private WLTButton btn_insert, btn_edit, btn_delete;

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_OPERATE_PRACTICE_GUIDE_CODE1");
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); // ����
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //�༭
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //ɾ��
		billList.addBatchBillListButton(new WLTButton[] {btn_insert, btn_edit, btn_delete});
		billList.repaintBillListButton();
		this.add(billList);
	}
}