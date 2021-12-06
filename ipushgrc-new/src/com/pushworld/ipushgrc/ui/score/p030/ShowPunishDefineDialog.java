package com.pushworld.ipushgrc.ui.score.p030;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.AbstractHyperLinkDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * 违规积分-》违规惩罚处理 卡片中惩罚类型的链接类【李春娟/2013-05-15】
 * 主要是查看违规惩罚定义
 * @author lcj
 *
 */
public class ShowPunishDefineDialog extends AbstractHyperLinkDialog {
	private static final long serialVersionUID = 1L;

	public ShowPunishDefineDialog(Container _parent) {
		super(_parent);
	}

	@Override
	public void initialize() {
		this.getContentPane().setLayout(new BorderLayout());
		BillListPanel billlistPanel = new BillListPanel("SCORE_PUNISH_LCJ_Q01"); //
		this.getContentPane().add(billlistPanel, BorderLayout.CENTER); //
		JPanel panel_south = WLTPanel.createDefaultPanel();
		panel_south.setLayout(new FlowLayout());
		JButton btn_close = new WLTButton(UIUtil.getLanguage("关闭"));
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ShowPunishDefineDialog.this.dispose(); //
			}
		});
		panel_south.add(btn_close);
		this.getContentPane().add(panel_south, BorderLayout.SOUTH); //

		int height = billlistPanel.getRowCount() * 22 + 180;
		this.setSize(700, height); //
		locationToCenterPosition(); //
	}
}
