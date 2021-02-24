package com.pushworld.ipushgrc.ui.score.p070;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 违规积分-》单位积分查询【李春娟/2013-05-14】
 * @author lcj
 *
 */
public class QueryScoreWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton btn_query;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SCORE_USER_LCJ_Q06");
		btn_query = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		btn_query.setText("浏览");
		btn_query.setToolTipText("查看详细信息");
		listPanel.addBillListButton(btn_query);
		listPanel.repaintBillListButton();
		String curryear = TBUtil.getTBUtil().getCurrDate().substring(0, 4);
		listPanel.QueryDataByCondition("EFFECTDATE like '" + curryear + "%'");//查询本年度的记录
		this.setLayout(new BorderLayout());
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
	}
}
