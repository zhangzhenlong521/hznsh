package com.pushworld.ipushgrc.ui.cmpevent.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;

/**
 * �ɹ�������ѯ!!!
 * @author xch
 *
 */
public class CmpWardQueryWKPanel extends AbstractWorkPanel {

	private BillListPanel billList; //

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_WARD_CODE1"); //
		WLTButton btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		//�����ղؼа�ť
		WLTButton btn_joinFavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("�ɹ�����", this.getClass().getName(), "wardname");
		billList.addBatchBillListButton(new WLTButton[] {  btn_list, btn_joinFavority }); //
		billList.repaintBillListButton(); //ˢ�°�ť!!!
		this.add(billList); //
	}

}