package com.pushworld.ipushgrc.ui.wfrisk.p110;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;

import com.pushworld.ipushgrc.ui.wfrisk.WFRiskUIUtil;

/**
 * ɾ��״̬Ϊ����Ч���������ļ���������ʷ�汾�����ҷ���һ��1.0�İ汾
 * @author lcj
 *
 */
public class ResetPublishAllCmpfileWKPanel extends AbstractWorkPanel implements ActionListener {
	private WLTButton btn_publish = null;

	@Override
	public void initialize() {
		WLTLabel label1 = new WLTLabel("1.ϵͳ������״̬Ϊ����Ч���������ļ���������ʷ�汾����գ�Ȼ�󷢲�1.0�汾����������������ť");
		label1.setBounds(40, 20, 800, 30);
		label1.setPreferredSize(new Dimension(800, 50));
		btn_publish = new WLTButton("����");
		btn_publish.setBounds(40, 60, 100, 25);
		btn_publish.addActionListener(this);
		JPanel mainpanel = WLTPanel.createDefaultPanel(null);
		mainpanel.add(label1);
		mainpanel.add(btn_publish);
		mainpanel.setBounds(0, 0, 800, 400);
		this.add(mainpanel); //
	}

	public void actionPerformed(ActionEvent e) {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					HashMap HashMap = new WFRiskUIUtil().publishAllCmpFile();
					StringBuffer sb_msg = new StringBuffer("�����ļ���δ��д���ĵ��·���ʧ�ܣ�\r\n���д���ĺ����������\r\n");
					String[] keys = (String[]) HashMap.keySet().toArray(new String[0]);
					if (keys != null && keys.length > 0) {
						for (int i = 0; i < keys.length; i++) {
							sb_msg.append((i + 1) + "��[" + keys[i] + "]=[" + HashMap.get(keys[i]) + "]\r\n");
						}
						MessageBox.showTextArea(ResetPublishAllCmpfileWKPanel.this, sb_msg.toString());
					} else {
						MessageBox.show(ResetPublishAllCmpfileWKPanel.this, "ȫ�������ɹ�!");
					}
				} catch (Exception e1) {
					MessageBox.showException(ResetPublishAllCmpfileWKPanel.this, e1);
				}
			}
		}, 600, 130, 300, 300);
	}
}
