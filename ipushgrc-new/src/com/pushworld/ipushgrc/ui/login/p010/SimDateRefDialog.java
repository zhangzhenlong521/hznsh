package com.pushworld.ipushgrc.ui.login.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.BackGroundDrawingUtil;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;
import cn.com.infostrategy.ui.mdata.querycomp.QueryDateModelPanel;

/**
 * �򵥵�����ѡ����ʾ�������� ֻ��ѡ��һ������ ���صĲ�����id��name�����ַ������磺2013-11-28��
 * @author ��Ӫ����2013/11/28��
 * */
public class SimDateRefDialog extends AbstractRefDialog implements ActionListener {
	private WLTButton btn_confirm, btn_cancel; //
	private RefItemVO returnRefItemVO = null; //
	private QueryDateModelPanel daily_1 = null; //
	private static final long serialVersionUID = -5947285976849351291L;

	public SimDateRefDialog(Container _parent, String _title, RefItemVO refItemVO, BillPanel panel) {
		super(_parent, _title, refItemVO, panel);
	}

	@Override
	public RefItemVO getReturnRefItemVO() {
		return returnRefItemVO;
	}

	@Override
	public void initialize() {
		JPanel panel_daily = new JPanel(new FlowLayout()); //
		panel_daily.setOpaque(false); //
		daily_1 = new QueryDateModelPanel();
		this.add(daily_1, BorderLayout.CENTER);
		this.add(getSouthPanel(), BorderLayout.SOUTH); //
		this.setSize(240, 320);
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout(), BackGroundDrawingUtil.HORIZONTAL_FROM_MIDDLE);
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) { //�����ȷ���򷵻�����
			onConfirm(); //ȷ��!!!
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //ȡ��
		}
	}

	//���ȷ��!
	private void onConfirm() {
		String str_date1 = daily_1.getDataStringValue(); ////
		returnRefItemVO = new RefItemVO(); //
		returnRefItemVO.setId(str_date1); //
		returnRefItemVO.setName(str_date1); //
		this.setCloseType(BillDialog.CONFIRM);
		this.dispose(); //
	}

	//���ȡ��!!!
	private void onCancel() {
		returnRefItemVO = null; //
		this.setCloseType(BillDialog.CANCEL);
		this.dispose(); //
	}
}
