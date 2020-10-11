package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.StylePadEditDialog;

public class ListCellEditor_StylePad extends AbstractCellEditor implements TableCellEditor, IBillCellEditor, ActionListener {

	private Pub_Templet_1_ItemVO itemVO = null; //
	private BillListPanel billList = null; //
	private JPanel panel = null; //
	private JTextArea textArea = null; //
	private JButton btn_edit = null; //编辑按钮

	private String str_batchid = null; //
	private int currrow = -1; //当前行!

	private ListCellEditor_StylePad() {
	}

	public ListCellEditor_StylePad(Pub_Templet_1_ItemVO _itemvo, BillListPanel _billList) {
		itemVO = _itemvo; //
		billList = _billList; //
		panel = new JPanel(new BorderLayout()); //
		textArea = new JTextArea(); //
		textArea.setLineWrap(true); //
		textArea.setEditable(false); //不可编辑!!!
		textArea.setFocusable(true); //
		btn_edit = new WLTButton(UIUtil.getImage("office_043.gif")); //
		btn_edit.addActionListener(this); //
		panel.add(textArea, BorderLayout.CENTER); //
		panel.add(btn_edit, BorderLayout.EAST); //
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int _row, int column) {
		str_batchid = null; //
		currrow = _row; //
		if (value == null) {
			textArea.setText(""); //
		} else {
			String str_value = value.toString(); //
			int li_pos_1 = str_value.lastIndexOf("#@$"); //
			int li_pos_2 = str_value.lastIndexOf("$@#"); // 
			if (str_value.endsWith("$@#") && li_pos_1 > 0 && li_pos_2 > 0 && (li_pos_2 - li_pos_1) < 20) {
				textArea.setText(str_value.substring(0, li_pos_1)); //
				str_batchid = str_value.substring(li_pos_1 + 3, li_pos_2); //
			} else {
				textArea.setText(str_value); //
			}
		}
		textArea.requestFocus(); //
		return panel; //
	}

	/**
	 * 返回StringItemVO
	 */
	public Object getCellEditorValue() {
		String str_text = textArea.getText(); //
		if (str_text == null || str_text.trim().equals("")) {
			return null; //
		}
		if (str_batchid != null) {
			str_text = str_text + "#@$" + str_batchid + "$@#"; //如果有后辍,则加上!
		}
		return new StringItemVO(str_text);
	}

	public JComponent getCompent() {
		return panel;
	}

	public boolean isCellEditable(EventObject evt) {
		if (itemVO.getListiseditable() != null && itemVO.getListiseditable().equals("1")) {
			if (evt instanceof MouseEvent) {
				return ((MouseEvent) evt).getClickCount() >= 2;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_edit) {
			onEdit(); //
		}
	}

	private void onEdit() {
		String[] str_updatesqls = null; //
		String str_tableName = itemVO.getPub_Templet_1VO().getSavedtablename(); //
		String str_itemkey = itemVO.getItemkey(); //
		boolean isNeedSave = itemVO.isNeedSave(); //
		boolean isCanSave = itemVO.isCanSave(); //
		String str_pkname = itemVO.getPub_Templet_1VO().getPkname();
		String str_pkvalue = billList.getRealValueAtModel(currrow, str_pkname); //
		if (str_tableName != null && str_itemkey != null && str_pkname != null && str_pkvalue != null && isNeedSave && isCanSave) {
			str_updatesqls = new String[] { str_tableName, str_itemkey, str_pkname, str_pkvalue }; //
		}
		StylePadEditDialog dialog = new StylePadEditDialog(panel, itemVO.getItemname(), textArea.getText(), str_batchid, str_updatesqls); //
		dialog.setVisible(true); //
		if (dialog.getCloseType() == 1) { //返回后重新设置类变量!!!
			String str_newBatchId = dialog.getReturnBatchId(); //
			String str_text = dialog.getReturnText(); //
			str_batchid = str_newBatchId; //
			textArea.setText(str_text); //
		}
		this.stopCellEditing(); //
	}
}
