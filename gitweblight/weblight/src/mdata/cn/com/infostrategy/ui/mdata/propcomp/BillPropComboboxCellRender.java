package cn.com.infostrategy.ui.mdata.propcomp;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;

public class BillPropComboboxCellRender implements TableCellRenderer {

	private Pub_Templet_1_ItemVO itemVO;

	public BillPropComboboxCellRender(Pub_Templet_1_ItemVO _itemVO) {
		this.itemVO = _itemVO;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		String str_value = ""; //

		if (value != null) {
			boolean bo_iffind = false;
			ComBoxItemVO[] VOs = itemVO.getComBoxItemVos();
			for (int i = 0; i < VOs.length; i++) {
				if (VOs[i].getId().equals(value.toString())) {
					str_value = VOs[i].getName();
					bo_iffind = true;
					break;
				}
			}

			if (!bo_iffind) {
				str_value = value.toString();
			}
		}

		JLabel label = new JLabel(str_value); //
		if (isSelected || hasFocus) {
			label.setBackground(table.getSelectionBackground()); //
		} else {
			label.setBackground(java.awt.Color.WHITE); //
		}
		label.setOpaque(true); //必须设置成不透明的,否则底色不起效果!!
		return label; //
	}
}
