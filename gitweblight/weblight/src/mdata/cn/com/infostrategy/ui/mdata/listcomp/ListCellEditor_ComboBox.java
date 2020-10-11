/**************************************************************************
 * $RCSfile: ListCellEditor_ComboBox.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;

import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

public class ListCellEditor_ComboBox extends DefaultCellEditor implements IBillCellEditor {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO itemVO = null; //
	private JComboBox comboBox = null; //
	private ComBoxItemVO oldComBoxItemVO = null; //

	public ListCellEditor_ComboBox(JComboBox _comboBox, Pub_Templet_1_ItemVO _itemvo) {
		super(_comboBox);
		_comboBox.setEditable(true); //
		this.itemVO = _itemvo;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		comboBox = (JComboBox) super.getTableCellEditorComponent(table, value, isSelected, row, column); //�õ��ؼ�
		oldComBoxItemVO = (ComBoxItemVO) value; //
		if (oldComBoxItemVO != null) {
			boolean isFinded = false; //
			if (comboBox.getItemCount() > 0) {
				for (int i = 0; i < comboBox.getItemCount(); i++) {
					ComBoxItemVO tempItemVO = (ComBoxItemVO) comboBox.getItemAt(i); //
					if (tempItemVO != null && tempItemVO.getId() != null && tempItemVO.getId().equals(oldComBoxItemVO.getId())) { //���ID���
						comboBox.setSelectedIndex(i); //ѡ��!!
						isFinded = true;
						break;
					}
				}
			}
			if (!isFinded) {
				JTextField cmb_textField = ((JTextField) ((JComponent) comboBox.getEditor().getEditorComponent()));
				cmb_textField.setText(oldComBoxItemVO.getId()); //
			}
		}
		return comboBox; //
	}

	public Object getCellEditorValue() {
		int li_selindex = comboBox.getSelectedIndex(); //
		if (li_selindex <= 0 || comboBox.getSelectedItem() == null) {
			return null; //���ûѡ
		} else {
			ComBoxItemVO seledComBoxItemVO = (ComBoxItemVO) comboBox.getSelectedItem(); //ȡ������ѡ�е�
			if (oldComBoxItemVO == null) {
				ComBoxItemVO newItemVO = (ComBoxItemVO) seledComBoxItemVO.deepClone(); //һ��Ҫ��¡һ��!
				newItemVO.setValueChanged(true); //
				return newItemVO; //
			} else {
				if (!seledComBoxItemVO.getId().equals(oldComBoxItemVO.getId())) { //����仯��!!
					ComBoxItemVO newItemVO = (ComBoxItemVO) seledComBoxItemVO.deepClone(); //
					newItemVO.setValueChanged(true); //
					return newItemVO; //
				} else {
					oldComBoxItemVO.setValueChanged(false); //
					return oldComBoxItemVO; //
				}
			}
		}
	}

	public boolean isCellEditable(EventObject evt) {
		if (itemVO.getListiseditable() == null || itemVO.getListiseditable().equals("1") || itemVO.getListiseditable().equals("2") || itemVO.getListiseditable().equals("3")) { //ֻ��������༭������²��ж�
			if (evt instanceof MouseEvent) {
				return ((MouseEvent) evt).getClickCount() >= 2;
			}
		} else {
			return false;
		}
		return true;
	}

	public javax.swing.JComponent getCompent() {
		return comboBox;
	}

}
