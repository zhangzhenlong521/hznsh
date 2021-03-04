package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicComboBoxUI;

import cn.com.infostrategy.ui.common.ImageIconFactory;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_ComboBoxUI 
 * @Description: 下拉框UI。
 * @author haoming
 * @date Mar 19, 2013 4:25:33 PM
 *  
*/
public class I_ComboBoxUI extends BasicComboBoxUI {
	//createUI这个方法必须有。否则L&F中不能实例对象。
	public static ComponentUI createUI(JComponent c) {
		c.setOpaque(false);
		((JComboBox) c).setEditable(true);
		return new I_ComboBoxUI();
	}

	public I_ComboBoxUI() {
	}

	public JButton getArrowButton() {
		return this.arrowButton; //
	}

	Icon ic = ImageIconFactory.getDownEntityArrowIcon(new Color(120, 120, 120));

	protected JButton createArrowButton() {
		arrowButton = new JButton() {
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2d = (Graphics2D) g.create();
				if (getModel().isEnabled() && getModel().isPressed()) {
					ic.paintIcon(this, g2d, 5, this.getHeight() / 2 - 4);
				} else {
					ic.paintIcon(this, g2d, 4, this.getHeight() / 2 - 5);
				}
				g2d.dispose();
			}
		};
		arrowButton.putClientProperty("JButton.RefTextField", comboBox.getEditor().getEditorComponent());
		arrowButton.putClientProperty("JButton.ArrawButton", comboBox.getEditor().getEditorComponent());
		return arrowButton;
	}

	protected I_ComboboxEditor createEditor() {
		return new I_ComboboxEditor.UIResource();
	}

	protected ListCellRenderer createRenderer() {
		return new I_ComboBoxRenderer.UIResource(this);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		Dimension mindim = super.getMinimumSize(c);
		if (mindim.getHeight() < 20) { //小于20就出现按钮异型。
			mindim.setSize(mindim.getWidth(), 20);
		}
		return mindim;
	}
}
