package com.pushworld.ipushgrc.ui.score.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 违规积分-》基础信息-》积分惩罚定义【李春娟/2013-05-09】
 * 包括个人积分惩罚和单位积分惩罚定义，暂时实现了个人惩罚及惩罚跟踪，
 * 单位积分惩罚可作为单位负责人年度考评评优和晋升提供参考，在统计中进行体现【李春娟/2014-09-25】
 * @author lcj
 *
 */
public class PunishEditWKPanel extends AbstractWorkPanel implements ActionListener {

	BillListPanel listPanel, listPanel2;//增加单位积分惩罚定义【李春娟/2014-09-25】
	WLTButton btn_refresh, btn_refresh2;
	WLTTabbedPane tab;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SCORE_PUNISH_LCJ_E01");//个人积分惩罚定义
		btn_refresh = new WLTButton("刷新");
		btn_refresh.addActionListener(this);
		listPanel.addBillListButton(btn_refresh);
		listPanel.repaintBillListButton();

		listPanel2 = new BillListPanel("SCORE_PUNISH2_LCJ_E01");//单位积分惩罚定义
		btn_refresh2 = new WLTButton("刷新");
		btn_refresh2.addActionListener(this);
		listPanel2.addBillListButton(btn_refresh2);
		listPanel2.repaintBillListButton();

		tab = new WLTTabbedPane();
		tab.addTab("个人积分惩罚", listPanel);
		tab.addTab("单位积分惩罚", listPanel2);
		this.setLayout(new BorderLayout());
		this.add(tab);
		onRefresh();//自动查询一下
		onRefresh2();
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_refresh) {
			onRefresh();
		} else if (e.getSource() == btn_refresh2) {
			onRefresh2();
		}
	}

	private void onRefresh() {
		listPanel.QueryDataByCondition(null);
	}

	private void onRefresh2() {
		listPanel2.QueryDataByCondition(null);
	}
}
