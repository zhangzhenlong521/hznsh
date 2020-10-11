package cn.com.pushworld.salary.ui.target.p040;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillListModel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ScoreCellRenderer implements TableCellRenderer {

	private Pub_Templet_1_ItemVO itemVO;

	public ScoreCellRenderer(Pub_Templet_1_ItemVO _itemVO) {
		this.itemVO = _itemVO;
	}

	public Component getTableCellRendererComponent(JTable jtable, Object obj, boolean isSelected, boolean hasFocus, int row, int col) {
		JLabel label = new JLabel();
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setOpaque(true);
		if (obj == null || "".equals(obj)) {
			label.setText("请选择");
		} else {
			label.setText("-" + String.valueOf(obj) + "%");
		}
		label.setForeground(Color.blue);
		if (isSelected) {
			label.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) jtable.getClientProperty("$rowbackground_" + row); //
			if (defColor != null) { // 如果指定了背景颜色,则直接使用!!!
				label.setBackground(defColor); //
			} else { // 否则是奇偶变色!!!
				if (row % 2 == 0) {
					label.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					label.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}

		if (itemVO.getItemkey().equalsIgnoreCase("checkdratio")) {
			BillListPanel billListPanel = ((BillListModel) jtable.getModel()).getBillListPanel(); //
			Object statusvalue = billListPanel.getValueAt(row, "status");
			Object checkscore = billListPanel.getValueAt(row, "checkdratio");
			if (statusvalue != null && !statusvalue.toString().equals(DeptTargetScoredWKPanel.SCORE_STATUS_INIT)) { // 没有填写
				label.setEnabled(false);
			} else if (statusvalue == null && checkscore != null) { //如果状态为空,checkscore不为空.说明提交失败了.
				//暂时先不做任何提示。可能会产生误导.
//				label.setToolTipText("");
			}
		} else if (itemVO.getItemkey().equalsIgnoreCase("recheckdratio")) {
			BillListPanel billListPanel = ((BillListModel) jtable.getModel()).getBillListPanel(); //
			if (billListPanel.getClientProperty("reedit") != null) {
				label.setEnabled(true);
			} else {
				label.setEnabled(false);
			}
		}
		return label;
	}
}
