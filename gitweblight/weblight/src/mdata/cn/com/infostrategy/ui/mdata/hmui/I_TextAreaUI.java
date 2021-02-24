package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.text.JTextComponent;

import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.NinePatch;

/**
 * Copyright Pushine
 * 
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_TextAreaUI
 * @Description: TextAreaUIʵ����
 * @author haoming
 * @date Mar 25, 2013 5:48:42 PM
 * 
 *       lostborder����ǿ���趨�ı����ޱ߿���Ҫ��Էŵ��������е��ı������border�ù�����ġ�
 * 
 */
public class I_TextAreaUI extends BasicTextAreaUI implements LAFUtil.TextComponentFocusActionIfc {
	private NinePatch bg = IconFactory.getInstance().getTextFieldBgNormal();
	private int overwidth = 0; // �������ֿ�ȣ����հѰ�ť�ŵ��ı������档

	public static ComponentUI createUI(JComponent c) {
		c.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
		I_TextFieldUI.addOtherListener(c);
		return new I_TextAreaUI();
	}

	protected void paintBackground(Graphics g) {
		JTextComponent editor = getComponent();
		if ("Y".equals(editor.getClientProperty("JTextField.DrawFocusBorder"))) { // ���ǿ�������н���߿�״̬��
			switchBgToFocused();
		}
		Object o_width = editor.getClientProperty("JTextField.OverWidth");
		if (o_width != null) {
			overwidth = (Integer) o_width;
		}
		if (editor.getClientProperty("lostborder") == null) {
			I_TextFieldUI.paintBg(g, 0, 0, editor.getWidth() + overwidth, editor.getHeight(), editor.isEditable() || editor.getBackground() == LookAndFeel.inputbgcolor_enable
					|| editor.getForeground() == LookAndFeel.inputbgcolor_disable, this.bg);
		} else {
			super.paintBackground(g);
		}
	}

	//
	public void switchBgToNomal() {
		if (getComponent().getClientProperty("lostborder") == null) {
			this.bg = IconFactory.getInstance().getTextFieldBgNormal();
		}
	}

	//
	public void switchBgToFocused() {
		if (getComponent().getClientProperty("lostborder") == null) {
			this.bg = IconFactory.getInstance().getTextFieldBgFocused();
		}
	}
}