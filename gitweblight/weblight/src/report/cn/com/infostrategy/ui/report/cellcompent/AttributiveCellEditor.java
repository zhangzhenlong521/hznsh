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

	private JepFormulaParseAtUI formulaParse = null; //��ʽ������..

	private int editingRow = 0, editingCol = 0; //

	public AttributiveCellEditor(MultiSpanCellTable _table, BillCellPanel _billCellPanel) {
		editorParentTable = _table; //
		this.billCellPanel = _billCellPanel; //����ؼ�..
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		editingRow = row; //�༭����..
		editingCol = column; //�༭����..
		if (value == null) {
			component = new JTextField(); //��ʽ�������ֿ�
			cellItemVO = new BillCellItemVO(); //
			cellItemVO.setCelltype("TEXT"); //
		} else {
			cellItemVO = (BillCellItemVO) value; //
			if (cellItemVO.getCelltype() == null) {
				comptype = BillCellPanel.ITEMTYPE_TEXT;
			} else {
				comptype = cellItemVO.getCelltype(); //
			}

			if (cellItemVO.getCelltype() == null || cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_TEXT)) { //�ı���
				component = new JTextField(cellItemVO.getCellvalue()); //			
				if (cellItemVO.getHalign() == 2) {
					((JTextField) component).setHorizontalAlignment(JTextField.CENTER); //
				} else if (cellItemVO.getHalign() == 3) {
					((JTextField) component).setHorizontalAlignment(JTextField.RIGHT); //
				}
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_NUMBERTEXT)) {
				component = new JFormattedTextField(); //��ʽ�������ֿ�
				String descr = cellItemVO.getCelldesc();
				CommUCDefineVO defVO = new CommUCDefineVO();
				if (descr != null && !descr.equals("")) {
					defVO.setConfValue("����", descr);
				}
				((JFormattedTextField) component).setDocument(new NumberFormatdocument(defVO));
				((JFormattedTextField) component).setText(cellItemVO.getCellvalue());
				if (cellItemVO.getHalign() == 2) {
					((JFormattedTextField) component).setHorizontalAlignment(JTextField.CENTER); //
				} else if (cellItemVO.getHalign() == 3) {
					((JFormattedTextField) component).setHorizontalAlignment(JTextField.RIGHT); //
				}
				component.addKeyListener(this); //���س�ʱ�����༭״̬..
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_TEXTAREA)) {
				component = new JTextArea(cellItemVO.getCellvalue()); //
				((JTextArea) component).setLineWrap(true); //
				((JTextArea) component).setWrapStyleWord(true); //
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_CHECKBOX)) { //��ѡ��
				component = new JCheckBox(); //
				if (cellItemVO.getCellvalue() != null && cellItemVO.getCellvalue().equals("Y")) {
					((JCheckBox) component).setSelected(true);
				} else {
					((JCheckBox) component).setSelected(false);
				}
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_COMBOBOX)) { //������
				JComboBox combobox = new JComboBox(); //������
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
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_DATE)) { //������
				CellRefPanel refPanel = new CellRefPanel(BillCellPanel.ITEMTYPE_DATE); //
				refPanel.setValue(cellItemVO.getCellvalue()); //
				component = refPanel;
			} else if (cellItemVO.getCelltype().equals(BillCellPanel.ITEMTYPE_DATETIME)) { //������
				CellRefPanel refPanel = new CellRefPanel(BillCellPanel.ITEMTYPE_DATETIME); //
				refPanel.setValue(cellItemVO.getCellvalue()); //
				component = refPanel;
			} else {
				component = new JTextField(cellItemVO.getCellvalue()); //	
			}

			if (cellItemVO.getIseditable() == null || cellItemVO.getIseditable().equals("Y")) {
				component.setEnabled(true); //
			} else if (((Boolean) table.getClientProperty("isshowline")).booleanValue()) { //�����ʾ��
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
			if (component instanceof JLabel) { //������ı���
				cellItemVO.setCellvalue(((JLabel) component).getText()); //
			} else if (component instanceof JFormattedTextField) { //������ı���
				String str_value = ((JFormattedTextField) component).getText(); //ȡ�ÿؼ��е�ֵ..
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //У�鹫ʽ!!!���ܻ����쳣,����У��ʧ��ʱ,�ᵯ����ֹͣ�ڿؼ���!!
				cellItemVO.setCellvalue(str_value); //
			} else if (component instanceof JTextField) { //������ı���..
				String str_value = ((JTextField) component).getText(); //ȡ�ÿؼ��е�ֵ..
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //У�鹫ʽ!!!���ܻ����쳣,����У��ʧ��ʱ,�ᵯ����ֹͣ�ڿؼ���!!
				cellItemVO.setCellvalue(str_value); //
			} else if (component instanceof JTextArea) { //������ı���
				String str_value = ((JTextArea) component).getText(); //ȡ�ÿؼ��е�ֵ..
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //У�鹫ʽ!!!���ܻ����쳣,����У��ʧ��ʱ,�ᵯ����ֹͣ�ڿؼ���!!
				cellItemVO.setCellvalue(str_value); //
			} else if (component instanceof JCheckBox) { //����ǹ�ѡ��
				String str_value = null; //
				if (((JCheckBox) component).isSelected()) {
					str_value = "Y";
				} else {
					str_value = "N"; //
				}
				validateFormula(cellItemVO, str_value, editingRow, editingCol); //У�鹫ʽ!!!���ܻ����쳣,����У��ʧ��ʱ,�ᵯ����ֹͣ�ڿؼ���!!
				cellItemVO.setCellvalue(str_value);
			} else if (component instanceof JComboBox) { //�����������
				String str_value = "" + ((JComboBox) component).getSelectedItem(); //
				validateFormula(cellItemVO, str_value, editingRow, editingCol);
				cellItemVO.setCellvalue(str_value);
			} else if (component instanceof CellRefPanel) { //���������
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
	 * ִ��У�鹫ʽ...
	 * @param _itemVO
	 * @param _editingValue �༭��ʽ
	 * @param _row �ڼ���
	 * @param _col �ڼ���
	 * @throws WLTAppException
	 */
	private void validateFormula(BillCellItemVO _itemVO, String _editingValue, int _row, int _col) throws WLTAppException {
		if (!TBUtil.isEmpty(_itemVO.getValidateformula())) { //���У�鹫ʽ��Ϊ��
			formulaParse = new JepFormulaParseAtUI(this.billCellPanel, _editingValue, _row, _col); //
			Object obj = formulaParse.execFormula(_itemVO.getValidateformula());
			if (obj != null && (obj instanceof WLTAppException)) { //������ض�����һ���쳣����,������֮
				throw (WLTAppException) obj; //
			}
		}
	}

	/**
	 * �Ƿ�ɱ༭...
	 */
	public boolean isCellEditable(EventObject evt) {
		if (evt instanceof MouseEvent) { //���������¼�
			MouseEvent mvt = (MouseEvent) evt;
			MultiSpanCellTable table = (MultiSpanCellTable) mvt.getSource(); //
			if (!table.isEditable()) {
				return false;
			}
			int li_row = table.rowAtPoint(mvt.getPoint()); //
			int li_col = table.columnAtPoint(mvt.getPoint()); //
			//System.out.println("�����[" + li_row + "," + li_col + "]"); //

			Object obj = table.getValueAt(li_row, li_col);
			if (obj != null && (obj instanceof BillCellItemVO)) { //�����Ϊ��,������BillCellItemVO...
				BillCellItemVO cellItemVO = (BillCellItemVO) obj; //
				if (cellItemVO == null) {
					return mvt.getClickCount() >= 2; //����������ؼ�����Ҫ������.
				} else {
					if (cellItemVO.getIseditable() != null && cellItemVO.getIseditable().equals("N")) { //�����N,�����ɱ༭,��ֱ�ӷ���false.
						return false; //
					} else { //
						if (cellItemVO.getCelltype() != null && cellItemVO.getCelltype().equals("CHECKBOX")) { //����ǹ�ѡ��,��ֻҪ���1�ξͿɱ༭.
							return mvt.getClickCount() >= 1; //
						} else {
							return mvt.getClickCount() >= 2; //����������ؼ�����Ҫ������.
						}
					}
				}
			} else { //�������Ϊ������Ҫ�������
				return mvt.getClickCount() >= 2; //����������ؼ�����Ҫ������.
			}
		} else {
			if (!editorParentTable.isEditable()) {
				return false;
			}
			int li_selrow = editorParentTable.getSelectedRow();
			int li_selcol = editorParentTable.getSelectedColumn();
			if (li_selrow >= 0 && li_selcol >= 0) { //�����ѡ�е�������
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
	 * ������˻س���ֱ�ӽ����༭״̬.
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
