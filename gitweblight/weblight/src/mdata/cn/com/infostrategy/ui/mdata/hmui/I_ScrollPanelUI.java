package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicScrollPaneUI;
import javax.swing.text.JTextComponent;

import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.NinePatch;

/**
 * Copyright Pushine
 * 
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_ScrollPanelUI
 * @Description: scrollPane的UI。主要是针对包含文本域的特殊处理。
 * @author haoming
 * @date Mar 26, 2013 5:05:32 PM
 * 
 */
public class I_ScrollPanelUI extends BasicScrollPaneUI {
	public static ComponentUI createUI(JComponent c) {
		return new I_ScrollPanelUI();
	}

	protected void installDefaults(JScrollPane scrollpane) {
		super.installDefaults(scrollpane);
		Border border = new I_ScrollPanelBorder();
		scrollpane.setBorder(border);
		QuaquaUtilities.installProperty(scrollpane, "opaque", UIManager.get("ScrollPane.opaque"));
	}
}

// 给整个JscrollPanel添加一个边框。
class I_ScrollPanelBorder implements Border, FocusListener {
	private Insets inset = new Insets(2, 2, 2, 2);
	private NinePatch hasfocus = IconFactory.getInstance().getTextFieldBgFocused();
	private NinePatch normalbg = IconFactory.getInstance().getTextFieldBgNormal();
	private JTextComponent textarea;
	private JScrollPane scrollPane;

	public Insets getBorderInsets(Component c) {
		if (textarea == null && c != null) {
			scrollPane = (JScrollPane) c;
			if (scrollPane.getViewport().getComponentCount() > 0) {
				JComponent com = (JComponent) scrollPane.getViewport().getComponent(0);
				if (com instanceof JTextComponent) {
					textarea = (JTextComponent) com;
					textarea.addFocusListener(this);
					textarea.putClientProperty("lostborder", "Y");
					if (textarea.getClientProperty("JTextArea.Insets") != null) {
						inset = (Insets) textarea.getClientProperty("JTextArea.Insets");
					} else {
						inset.set(2, 3, 2, 3);
					}
				} else if (com.toString().contains("FilePane")) {
					inset.set(2, 3, 2, 3);
				} else {
					inset.set(0, 0, 0, 0);
				}
			}
		}
		return inset;
	}

	public boolean isBorderOpaque() {
		return false;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		if (textarea != null) {
			Object o_width = (textarea).getClientProperty("JTextField.OverWidth");
			if (o_width != null) {
				int overwidth = (Integer) o_width;
				if ("Y".equals((textarea).getClientProperty("JTextField.DrawFocusBorder"))
						|| (textarea.hasFocus() && (textarea.isEditable() || textarea.getBackground() == LookAndFeel.inputbgcolor_enable || textarea.getForeground() == LookAndFeel.inputbgcolor_disable))) { // 如果强行设置有焦点边框状态。
					hasfocus.draw(g, x, y, width + overwidth, height);
				} else {
					normalbg.draw(g, x, y, width + overwidth, height);
				}
				return;
			}

			if (textarea.hasFocus() && textarea.isEnabled() && textarea.isEditable()) {
				hasfocus.draw(g, x, y, width, height);
			} else if (textarea.isEnabled()) {
				normalbg.draw(g, x, y, width, height);
			}
		}
		if (((JScrollPane) c).getViewport().getComponentCount() > 0) {
			if ((((JScrollPane) c).getViewport().getComponent(0)).toString().contains("FilePane")) {
				normalbg.draw(g, x, y, width, height);
			}
		}
	}

	public void focusGained(FocusEvent e) {
		scrollPane.repaint();
	}

	public void focusLost(FocusEvent e) {
		scrollPane.repaint();
	}

}
