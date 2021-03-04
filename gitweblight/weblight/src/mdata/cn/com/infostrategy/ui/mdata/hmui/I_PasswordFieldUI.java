package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicPasswordFieldUI;
import javax.swing.text.JTextComponent;

import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.IconFactory;
import cn.com.infostrategy.ui.mdata.hmui.ninepatch.NinePatch;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_PasswordFieldUI 
 * @Description: 密码框的UI
 * @author haoming
 * @date Mar 26, 2013 9:32:30 AM
 *  
*/
public class I_PasswordFieldUI extends BasicPasswordFieldUI implements LAFUtil.TextComponentFocusActionIfc {
	private NinePatch bg = IconFactory.getInstance().getTextFieldBgNormal();
	
	public static I_PasswordFieldUI createUI(JComponent c) {
		c.setBorder(BorderFactory.createEmptyBorder(2, 3, 2, 3)); //设置边距
		I_TextFieldUI.addOtherListener(c);//添加焦点事件
		return new I_PasswordFieldUI();
	}

	protected void paintBackground(Graphics g) {
		JTextComponent editor = getComponent();
		I_TextFieldUI.paintBg(g, 0, 0, editor.getWidth(), editor.getHeight(), editor.isEditable() || editor.getBackground() == LookAndFeel.inputbgcolor_enable, this.bg);
	}

	//
	public void switchBgToNomal() {
		this.bg = IconFactory.getInstance().getTextFieldBgNormal();
	}

	//
	public void switchBgToFocused() {
		this.bg = IconFactory.getInstance().getTextFieldBgFocused();
	}
}
