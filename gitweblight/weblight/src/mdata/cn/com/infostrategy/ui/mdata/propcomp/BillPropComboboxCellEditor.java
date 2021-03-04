package cn.com.infostrategy.ui.mdata.propcomp;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

import com.l2fprod.common.beans.editor.AbstractPropertyEditor;

public class BillPropComboboxCellEditor extends AbstractPropertyEditor {

	private Pub_Templet_1_ItemVO itemVO = null;
	private String str_value = null; //

	public BillPropComboboxCellEditor(Pub_Templet_1_ItemVO _itemvo, String _value) {
		this.itemVO = _itemvo; //
		this.str_value = _value; //
		ComBoxItemVO[] comBoBoxitemvos = itemVO.getComBoxItemVos(); //取得所有数据
		JComboBox combo = new JComboBox(new DefaultComboBoxModel(comBoBoxitemvos)); //
		int index = -1;
		for (int i = 0; i < comBoBoxitemvos.length; i++) {
			if (comBoBoxitemvos[i].getId().equalsIgnoreCase(str_value)) {
				index = i;
				break;
			}
		}

		combo.setSelectedIndex(index); //
		combo.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuCanceled(PopupMenuEvent e) {
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				myFirePropertyChange(); //
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
			}
		});

		combo.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					myFirePropertyChange(); //
				}
			}
		});

		editor = combo; //
	}

	private void myFirePropertyChange() {
		ComBoxItemVO valueVO = (ComBoxItemVO) ((JComboBox) editor).getSelectedItem();
		if (valueVO != null) {
			firePropertyChange(str_value, valueVO.getId()); //
		}
	}

	public Object getValue() {
		ComBoxItemVO valueVO = (ComBoxItemVO) ((JComboBox) editor).getSelectedItem();
		if (valueVO == null) {
			return null;
		}
		return valueVO.getId(); //
	}

	public void setValue(Object value) {
		JComboBox combo = (JComboBox) editor;
		int index = -1;
		for (int i = 0, c = combo.getModel().getSize(); i < c; i++) {
			ComBoxItemVO valueVO = (ComBoxItemVO) combo.getModel().getElementAt(i);
			if (value != null && value.toString().equalsIgnoreCase(valueVO.getId())) {
				index = i;
				break;
			}
		}

		((JComboBox) editor).setSelectedIndex(index);
	}

}
