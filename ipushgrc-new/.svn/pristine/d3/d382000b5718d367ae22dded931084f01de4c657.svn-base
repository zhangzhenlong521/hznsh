package com.pushworld.ipushgrc.ui.score.p010;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 违规积分-》基础信息-》通知书模板定义【李春娟/2013-05-15】
 * @author lcj
 *
 */
public class TempletEditWKPanel extends AbstractWorkPanel implements ActionListener {
	BillListPanel listPanel;
	WLTButton btn_help;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("SCORE_TEMPLET_LCJ_E01");
		btn_help = new WLTButton("帮助");
		btn_help.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_help });
		listPanel.repaintBillListButton();
		this.setLayout(new BorderLayout());
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_help) {
			onShowHelp();
		}
	}

	private void onShowHelp() {
		StringBuffer helpBuffer = new StringBuffer();
		helpBuffer.append("1.同一模板类型只能有一条记录。\r\n");
		helpBuffer.append("2.如果一条记录有多个模板文件则优先使用第一个。\r\n");
		helpBuffer.append("3.认定通知书中需要从系统中获得的数据，可以从我的积分界面中选择并添加标识，\r\n比如要获得违规人，可在模板文件内容中相应位置添加“$违规人$”。\r\n");
		helpBuffer.append("4.惩罚通知书同上，但需要从惩罚跟踪界面中获得数据。\r\n");
		helpBuffer.append("5.各模板通用$自动生效时间$。\r\n");
		MessageBox.showTextArea(this, "温馨提示", helpBuffer.toString());
	}
}
