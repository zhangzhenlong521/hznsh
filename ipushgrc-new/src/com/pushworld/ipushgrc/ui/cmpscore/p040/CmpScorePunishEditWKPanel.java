package com.pushworld.ipushgrc.ui.cmpscore.p040;

import java.awt.Dimension;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 处理结果维护
 * 
 * @author hm
 * 
 */
public class CmpScorePunishEditWKPanel extends AbstractWorkPanel {
	private WLTButton btn_deal;
	BillListPanel listPanel;

	public void initialize() {
		listPanel = new BillListPanel("CMP_SCORE_PUNISH_CODE1");
		btn_deal = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_deal.setPreferredSize(new Dimension(100, 23));
		btn_deal.setText("处理结果录入");
		listPanel.addBatchBillListButton(new WLTButton[] { btn_deal });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	
}
