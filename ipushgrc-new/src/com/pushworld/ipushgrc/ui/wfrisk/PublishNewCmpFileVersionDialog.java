package com.pushworld.ipushgrc.ui.wfrisk;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;

/**
 * �����°汾
 * @author xch
 *
 */
public class PublishNewCmpFileVersionDialog extends BillDialog implements ActionListener {

	private static final long serialVersionUID = -7021998328641833323L;
	private JComboBox combobox = null; //
	private WLTButton btn_confirm, btn_cancel; //
	private String returnNewVersion = null; //

	public PublishNewCmpFileVersionDialog(Container _parent, String versionno) {
		super(_parent, "�����°汾", 320, 160);
		int bigversion = Integer.parseInt(versionno.substring(0, versionno.indexOf(".")));
		//���������������ʶ�������ڽ����༭ʱҪ�����С�汾���ʰ汾������ǰ�ı���һλС����Ϊ�˱�����λС����������Ҫ��ȡС�����һλ���ɣ������������Խ�硾���/2012-03-08��
		int smallversion = Integer.parseInt(versionno.substring(versionno.indexOf(".") + 1, versionno.indexOf(".") + 2));

		String[] itemValues = new String[10 - smallversion];
		for (int i = 0; i < itemValues.length - 1; i++) {
			String version = bigversion + "." + (smallversion + i + 1);
			itemValues[i] = version; //
		}
		itemValues[10 - smallversion - 1] = (bigversion + 1) + ".0";

		JPanel jpanel = new WLTPanel(WLTPanel.INCLINE_NW_TO_SE, null, LookAndFeel.defaultShadeColor1, true);//����ɫ
		JLabel lable1 = new JLabel("ԭ�汾�ţ�", JLabel.RIGHT);
		JTextField text1 = new JTextField(versionno);
		text1.setEditable(false);
		jpanel.add(lable1);
		jpanel.add(text1);

		JLabel lable2 = new JLabel("�°汾�ţ�", JLabel.RIGHT);
		combobox = new JComboBox(itemValues); //
		combobox.setPreferredSize(new Dimension(80, 20));

		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_confirm.addActionListener(this);
		btn_cancel.addActionListener(this);

		lable1.setBounds(0, 25, 100, 20); //
		text1.setBounds(100, 25, 150, 20); //
		lable2.setBounds(0, 50, 100, 20); //
		combobox.setBounds(100, 50, 150, 20); //
		btn_confirm.setBounds(90, 90, 70, 20); //
		btn_cancel.setBounds(170, 90, 70, 20); //

		jpanel.add(lable1);
		jpanel.add(text1);
		jpanel.add(lable2);
		jpanel.add(combobox);
		jpanel.add(btn_confirm);
		jpanel.add(btn_cancel);

		this.getContentPane().add(jpanel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //
		}
	}

	private void onConfirm() {
		returnNewVersion = (String) combobox.getSelectedItem(); //
		this.setCloseType(1); //
		this.dispose(); //
	}

	private void onCancel() {
		this.setCloseType(2); //
		this.dispose(); //
	}

	//ȡ�÷��صİ汾��!
	public String getReturnNewVersion() {
		return returnNewVersion;
	}

}
