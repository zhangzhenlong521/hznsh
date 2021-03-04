package cn.com.infostrategy.ui.report.cellcompent;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.JepFormulaParseAtUI;
import cn.com.infostrategy.ui.mdata.NumberFormatdocument;
import cn.com.infostrategy.ui.report.BillCellPanel;

public class AttributiveCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {

	private static final long serialVersionUID = -8349802394735604850L;

	private BillCellItemVO cellItemVO = null; //

	private JComponent component = null;

	private String comptype = null; //

	private BillCellPanel billCellPanel = null; //
	private MultiSpanCellTable editorParentTable = null; //

	private JepFormulaParseAtUI formulaParse = null; //公式解释器..

	private int editingRow = 0, editingCol = 0; //

	public AttributiveCellEditor(MultiSpanCellTable _table, BillCellPanel _billCellPanel) {
		editorParentTable = _table; //
		this.billCellPanel = _billCellPanel; //网格控件..
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		editingRow = row; //编辑的行..
		editingCol = column; //编辑的列..
		if (value == null) {
			component = new JTextField(); //格式化的数字框
			cellItemVO = new BillCellItemVO(); //
			cellItemVO.setCelltype("TEXT"); //
		} else {
			cellItemVO = (BillCellItemVO) value; //
			if (cellItemVO.getCelltype() == null) {
				comptype = BillCellPanel.ITEMTYPE_TEXT;
			} else {
				comptype = cellItemVO.getCelltype(); //
			}

			if (cellItemVO.getCelltype() == null || cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_TEXT)) { //文本框
				component = new JTextField(cellItemVO.getCellvalue()); //			
				if (cellItemVO.getHalign() == 2) {
					((JTextField) component).setHorizontalAlignment(JTextField.CENTER); //
				} else if (cellItemVO.getHalign() == 3) {
					((JTextField) component).setHorizontalAlignment(JTextField.RIGHT); //
				}
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_NUMBERTEXT)) {
				component = new JFormattedTextField(); //格式化的数字框
				String descr = cellItemVO.getCelldesc();
				CommUCDefineVO defVO = new CommUCDefineVO();
				if (descr != null && !descr.equals("")) {
					defVO.setConfValue("类型", descr);
				}
				((JFormattedTextField) component).setDocument(new NumberFormatdocument(defVO));
				((JFormattedTextField) component).setText(cellItemVO.getCellvalue());
				if (cellItemVO.getHalign() == 2) {
					((JFormattedTextField) component).setHorizontalAlignment(JTextField.CENTER); //
				} else if (cellItemVO.getHalign() == 3) {
					((JFormattedTextField) component).setHorizontalAlignment(JTextField.RIGHT); //
				}
				component.addKeyListener(this); //当回车时结束编辑状态..
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_TEXTAREA)) {
				component = new JTextArea(cellItemVO.getCellvalue()); //
				((JTextArea) component).setLineWrap(true); //
				((JTextArea) component).setWrapStyleWord(true); //
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_CHECKBOX)) { //勾选框
				component = new JCheckBox(); //
				if (cellItemVO.getCellvalue() != null && cellItemVO.getCellvalue().equals("Y")) {
					((JCheckBox) component).setSelected(true);
				} else {
					((JCheckBox) component).setSelected(false);
				}
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_COMBOBOX)) { //下拉框
				JComboBox combobox = new JComboBox(); //下拉框
				if (cellItemVO.getCelldesc() != null) {
					String[] str_items = cellItemVO.getCelldesc().split(","); //
					for (int i = 0; i < str_items.length; i++) {
						combobox.addItem(str_items[i]); //
						if (value.toString() != null) {
							if (value.toString().equals(str_items[i])) {
								combobox.setSelectedIndex(i); //
							}
						}
					}
				}

				component = combobox; //
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_DATE)) { //下拉框
				CellRefPanel refPanel = new CellRefPanel(BillCellPanel.ITEMTYPE_DATE); //
				refPanel.setValue(cellItemVO.getCellvalue()); //
				component = refPanel;
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_DATETIME)) { //下拉框
				CellRefPanel refPanel = new CellRefPanel(BillCellPanel.ITEMTYPE_DATETIME); //
				refPanel.setValue(cellItemVO.getCellvalue()); //
				component = refPanel;
			} else {
				component = new JTextField(cellItemVO.getCellvalue()); //	
			}

			if (cellItemVO.getIseditable() == null || cellItemVO.getIseditable().equals("Y")) {
				component.setEnabled(true); //
			} else if (((Boolean) table.getClientProperty("isshowline")).booleanValue()) { //如果显示线
				component.setEnabled(false); //
			} else {
				component = new JLabel(value == null ? "" : value.toString()); //
				cellItemVO = new BillCellItemVO(); //
				cellItemVO.setCelltype("TEXT"); //
			}
		}
		component.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1)); //
		component.setFont(LookAndFeel.font);
		return component;
	}

	public Object getCellEditorValue() {
		try {
			if (component instanceof JLabel) { //如果是文本框
				cellItemVO.setCellvalue(((JLabel) component).getText()); //
			} else if (component instanceof JFormattedTextField) { //如果是文本框
				String str_value = ((JFormattedTextField) component).getText(); //取得控件中的值..
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //校验公式!!!可能会抛异常,即当校验失败时,会弹出并停止在控件上!!
				cellItemVO.setCellvalue(str_value); //
			} else if (component instanceof JTextField) { //如果是文本框..
				String str_value = ((JTextField) component).getText(); //取得控件中的值..
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //校验公式!!!可能会抛异常,即当校验失败时,会弹出并停止在控件上!!
				cellItemVO.setCellvalue(str_value); //
			} else if (component instanceof JTextArea) { //如果是文本框
				String str_value = ((JTextArea) component).getText(); //取得控件中的值..
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //校验公式!!!可能会抛异常,即当校验失败时,会弹出并停止在控件上!!
				cellItemVO.setCellvalue(str_value); //
			} else if (component instanceof JCheckBox) { //如果是勾选框
				String str_value = null; //
				if (((JCheckBox) component).isSelected()) {
					str_value = "Y";
				} else {
					str_value = "N"; //
				}
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //校验公式!!!可能会抛异常,即当校验失败时,会弹出并停止在控件上!!
				cellItemVO.setCellvalue(str_value);
			} else if (component instanceof JComboBox) { //如果是下拉框
				String str_value = "" + ((JComboBox) component).getSelectedItem(); //
				validateFormula(cellItemVO, str_value, editingRow, editingCol);
				cellItemVO.setCellvalue(str_value);
			} else if (component instanceof CellRefPanel) { //如果是日期
				String str_value = ((CellRefPanel) component).getValue(); //
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //
				cellItemVO.setCellvalue(str_value); //
			}
			return cellItemVO;
		} catch (Exception ex) {
			ex.printStackTrace(); //
			JOptionPane.showMessageDialog(component, ex.getMessage());
			throw new RuntimeException(ex.getMessage()); //
		}
	}

	/**
	 * 执行校验公式...
	 * @param _itemVO
	 * @param _editingValue 编辑公式
	 * @param _row 第几行
	 * @param _col 第几列
	 * @throws WLTAppException
	 */
	private void validateFormula(BillCellItemVO _itemVO, String _editingValue, int _row, int _col) throws WLTAppException {
		if (!TBUtil.isEmpty(_itemVO.getValidateformula())) { //如果校验公式不为空
			formulaParse = new JepFormulaParseAtUI(this.billCellPanel, _editingValue, _row, _col); //
			Object obj = formulaParse.execFormula(_itemVO.getValidateformula());
			if (obj != null && (obj instanceof WLTAppException)) { //如果返回对象是一个异常对象,则重抛之
				throw (WLTAppException) obj; //
			}
		}
	}

	/**
	 * 是否可编辑...
	 */
	public boolean isCellEditable(EventObject evt) {
		if (evt instanceof MouseEvent) { //如果是鼠标事件
			MouseEvent mvt = (MouseEvent) evt;
			MultiSpanCellTable table = (MultiSpanCellTable) mvt.getSource(); //
			if (!table.isEditable()) {
				return false;
			}
			int li_row = table.rowAtPoint(mvt.getPoint()); //
			int li_col = table.columnAtPoint(mvt.getPoint()); //
			//System.out.println("点击了[" + li_row + "," + li_col + "]"); //

			Object obj = table.getValueAt(li_row, li_col);
			if (obj != null && (obj instanceof BillCellItemVO)) { //如果不为空,并且是BillCellItemVO...
				BillCellItemVO cellItemVO = (BillCellItemVO) obj; //
				if (cellItemVO == null) {
					return mvt.getClickCount() >= 2; //如果是其他控件则需要点两次.
				} else {
					if (cellItemVO.getIseditable() != null && cellItemVO.getIseditable().equals("N")) { //如果是N,即不可编辑,则直接返回false.
						return false; //
					} else { //
						if (cellItemVO.getCelltype() != null && cellItemVO.getCelltype().equals("CHECKBOX")) { //如果是勾选框,则只要点击1次就可编辑.
							return mvt.getClickCount() >= 1; //
						} else {
							return mvt.getClickCount() >= 2; //如果是其他控件则需要点两次.
						}
					}
				}
			} else { //如果对象为空则需要点击两次
				return mvt.getClickCount() >= 2; //如果是其他控件则需要点两次.
			}
		} else {
			if (!editorParentTable.isEditable()) {
				return false;
			}
			int li_selrow = editorParentTable.getSelectedRow();
			int li_selcol = editorParentTable.getSelectedColumn();
			if (li_selrow >= 0 && li_selcol >= 0) { //如果有选中的行与列
				BillCellItemVO cellItemVO = (BillCellItemVO) editorParentTable.getValueAt(li_selrow, li_selcol); //
				if (cellItemVO != null && cellItemVO.getIseditable() != null && cellItemVO.getIseditable().equals("N")) {
					return false; //
				} else {
					return true; //
				}
			} else {
				return false; //
			}
		}
	}

	/**
	 * 如果按了回车则直接结束编辑状态.
	 */
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			this.stopCellEditing(); //
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

}
