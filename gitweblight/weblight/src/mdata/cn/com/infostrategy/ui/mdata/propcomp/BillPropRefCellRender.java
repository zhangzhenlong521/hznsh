package cn.com.infostrategy.ui.mdata.propcomp;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

public class BillPropRefCellRender implements TableCellRenderer {

	private Pub_Templet_1_ItemVO itemVO; //

	public BillPropRefCellRender(Pub_Templet_1_ItemVO _itemVO) {
		this.itemVO = _itemVO; //
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String str_value = ""; //
		if (value != null) {
			str_value = value.toString();
		}

		JLabel label = new JLabel(str_value); //
		return label;
	}

}
