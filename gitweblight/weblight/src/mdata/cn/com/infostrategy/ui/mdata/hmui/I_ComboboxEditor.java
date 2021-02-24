package cn.com.infostrategy.ui.mdata.hmui;

import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicComboBoxEditor;

import cn.com.infostrategy.ui.common.LookAndFeel;

public class I_ComboboxEditor extends BasicComboBoxEditor {

	JTextField editor;

	protected JTextField createEditorComponent() {
		editor = new JTextField("", 9);
		editor.putClientProperty("JTextField.OverWidth", 10);
//		editor.setEditable(false);// --郝明2013-5-14设置为可以编辑，主要是菜单导出注册xml时，下拉框不可以编辑, 如果发现其他地方下拉框异常，还需要改回来。
		editor.setBackground(LookAndFeel.inputbgcolor_enable);
		return editor;
	}

	public void focusGained(FocusEvent e) {
		super.focusGained(e);
	}

	public void focusLost(FocusEvent e) {
		super.focusLost(e);
	}

	public Object getItem() {
		return oldValue;
	}

	public void setItem(Object anObject) {
		if(anObject instanceof FileFilter){ //如果是存放的文件后缀过滤。
			super.setItem(((FileFilter)anObject).getDescription());	
			editor.setText(((FileFilter)anObject).getDescription());
			if (anObject != null) {
				oldValue = anObject;
			} else {
				editor.setText("");
			}
		}else{
			oldValue = anObject;
			super.setItem(anObject);	
		}
	
	}

	private Object oldValue;

	public static class UIResource extends I_ComboboxEditor implements javax.swing.plaf.UIResource {
	}
}
