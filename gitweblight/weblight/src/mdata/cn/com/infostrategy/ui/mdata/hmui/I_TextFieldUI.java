package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.NinePatch;

/**
 * Copyright Pushine
 * 
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_TextFieldUI
 * @Description: 文本框UI
 * @author haoming
 * @date Mar 25, 2013 5:44:10 PM
 * 
 */
public class I_TextFieldUI extends BasicTextFieldUI implements LAFUtil.TextComponentFocusActionIfc {
	private NinePatch bg = IconFactory.getInstance().getTextFieldBgNormal();
	private int overwidth = 0; // 超出部分宽度，参照把按钮放到文本框里面。

	public static I_TextFieldUI createUI(JComponent c) {
		c.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 3));
		return new I_TextFieldUI();
	}

	protected void installDefaults() {
		super.installDefaults();
		addOtherListener(getComponent());
	}

	protected void paintBackground(Graphics g) {
		JTextComponent editor = getComponent();
		if ("Y".equals(editor.getClientProperty("JTextField.DrawFocusBorder"))) { // 如果强行设置有焦点边框状态。
			switchBgToFocused();
		}
		Object o_width = editor.getClientProperty("JTextField.OverWidth");
		if (o_width != null) {
			overwidth = (Integer) o_width;
		}
		paintBg(g, 0, 0, editor.getWidth() + overwidth, editor.getHeight(), editor.isEditable() || editor.getBackground() == LookAndFeel.inputbgcolor_enable
				|| editor.getForeground() == LookAndFeel.inputbgcolor_disable, this.bg);
	}

	protected void uninstallDefaults() {
		super.uninstallDefaults();
		bg = null;
		overwidth = 0;
		if (getComponent().getFocusListeners().length > 0) {
			getComponent().removeFocusListener(getComponent().getFocusListeners()[0]);
		}
	}

	public void switchBgToNomal() {
		this.bg = IconFactory.getInstance().getTextFieldBgNormal();
	}

	public void switchBgToFocused() {
		this.bg = IconFactory.getInstance().getTextFieldBgFocused();
	}

	public static void paintBg(Graphics g, int x, int y, int w, int h, boolean enabled, NinePatch bg) {
		if (enabled) {
			bg.draw((Graphics2D) g, x, y, w, h);
		} else {
			IconFactory.getInstance().getTextFieldBgDisabled().draw((Graphics2D) g, x, y, w, h);
		}
	}

	public static void addOtherListener(JComponent c) {
		c.addFocusListener(new MyFocusListener());
	}
}

class MyFocusListener implements FocusListener {

	public void focusGained(FocusEvent e) {
		JTextComponent field = (JTextComponent) e.getSource();
		LAFUtil.TextComponentFocusActionIfc ui = (LAFUtil.TextComponentFocusActionIfc) field.getUI();
		ui.switchBgToFocused();
		if (field.getClientProperty("refpanel") != null) {
			((JComponent) field.getClientProperty("refpanel")).repaint();
			return;
		}
		if (field.getParent() != null) {
			field.getParent().repaint();
		} else {
			field.repaint();
		}
	}

	public void focusLost(FocusEvent e) {
		JTextComponent field = (JTextComponent) e.getSource();
		LAFUtil.TextComponentFocusActionIfc ui = (LAFUtil.TextComponentFocusActionIfc) field.getUI();
		ui.switchBgToNomal();
		if (field.getClientProperty("refpanel") != null) {
			((JComponent) field.getClientProperty("refpanel")).repaint();
			return;
		}
		if (field.getParent() != null) {
			field.getParent().repaint();
		} else {
			field.repaint();
		}
	}

	public static MyFocusListener getInstance() {
		return new MyFocusListener();
	}

	public static Color getTextFieldFocusedColor() {
		return LAFUtil.getColor(UIManager.getColor("TextField.selectionBackground"), 30, 30, 30);
	}

	public static Color getComboBoxFocusedColor() {
		return LAFUtil.getColor(UIManager.getColor("ComboBox.selectionBackground"), 30, 30, 30);
	}
}
