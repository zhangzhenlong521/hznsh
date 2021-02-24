/**************************************************************************
 * $RCSfile: ListCellEditor_Label.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

public class ListCellEditor_Label extends AbstractCellEditor implements TableCellEditor, IBillCellEditor {

	private static final long serialVersionUID = 1L;

	Pub_Templet_1_ItemVO itemVO = null;

	JLabel label = null;

	public ListCellEditor_Label(Pub_Templet_1_ItemVO _itemvo) {
		this.itemVO = _itemvo;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		String str_text = (value == null ? "" : String.valueOf(value));
		label = new JLabel(str_text);
		return label; //
	}

	public boolean isCellEditable(EventObject evt) {
		return false;
	}

	public Object getCellEditorValue() {
		return label.getText(); //
	}

	public javax.swing.JComponent getCompent() {
		return label;
	}

}
