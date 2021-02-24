package cn.com.infostrategy.ui.mdata.hmui;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;

import cn.com.infostrategy.ui.mdata.UIRefPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;

/** 
 * Copyright Pushine
 * @ClassName: cn.com.infostrategy.ui.mdata.hmui.I_UIRefPanelUI 
 * @Description: 参照的UI。
 * @author haoming
 * @date Apr 9, 2013 5:06:08 PM
 *  
*/ 
public class I_UIRefPanelUI extends ComponentUI {
	public static ComponentUI createUI(JComponent c) {
		if(c instanceof UIRefPanel){
			UIRefPanel refPanel = (UIRefPanel) c;
			JButton btn = refPanel.getBtn_ref();
			JTextComponent textField = refPanel.getTextField();
			btn.putClientProperty("JButton.RefTextField", textField);
			textField.putClientProperty("JTextField.OverWidth", (int) btn.getPreferredSize().getWidth()); //没必要搞按钮这么宽，只要超出一点儿就可以。
		}else{
			QueryCPanel_UIRefPanel refPanel = (QueryCPanel_UIRefPanel)c;
			JButton btn = refPanel.getBtn_ref();
			JTextField textField = refPanel.getTextField();
			btn.putClientProperty("JButton.RefTextField", textField);
			textField.putClientProperty("JTextField.OverWidth", (int) btn.getPreferredSize().getWidth());
		}
		return new I_UIRefPanelUI();
	}
}
