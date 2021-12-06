package com.pushworld.ipushgrc.ui.cmpevent.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import com.pushworld.ipushgrc.ui.favorite.MyFavoriteQueryWKPanel;

/**
 * 成功防范查询!!!
 * @author xch
 *
 */
public class CmpWardQueryWKPanel extends AbstractWorkPanel {

	private BillListPanel billList; //

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_WARD_CODE1"); //
		WLTButton btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		//加入收藏夹按钮
		WLTButton btn_joinFavority = MyFavoriteQueryWKPanel.getJoinFavorityButton("成功防范", this.getClass().getName(), "wardname");
		billList.addBatchBillListButton(new WLTButton[] {  btn_list, btn_joinFavority }); //
		billList.repaintBillListButton(); //刷新按钮!!!
		this.add(billList); //
	}

}
