package cn.com.infostrategy.ui.report.cellcompent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * @version 1.0 11/22/98
 */
public class AttributiveCellRenderer implements TableCellRenderer {

	private static final long serialVersionUID = 6825402525623577142L;

	private JLabel cell_keyLabel = new JLabel(); //
	//private JLabel cell_emptylabel = new JLabel(""); //label
	private JLabel cell_label = new JLabel(""); //label
	private JLabel cell_wltlabel = new WLTLabel(""); //label
	private JCheckBox cell_checkbox = new JCheckBox(); //��ѡ��
	private JComboBox cell_combobox = new JComboBox(); //������
	private CellRefPanel cell_ref_date = new CellRefPanel(BillCellPanel.ITEMTYPE_DATE); //����
	private CellRefPanel cell_ref_time = new CellRefPanel(BillCellPanel.ITEMTYPE_DATETIME); //ʱ��
	private Boolean isShowLine = null;

	//private HashMap ht_comp = new HashMap(); //

	public AttributiveCellRenderer() {
		cell_label.setOpaque(true); //
		cell_wltlabel.setOpaque(true); //
		cell_checkbox.setOpaque(true); //
		cell_combobox.setOpaque(true); //
		cell_ref_date.setOpaque(true); //
		cell_ref_time.setOpaque(true); //
		cell_checkbox.setBorderPainted(true); //
	}

	public Component getTableCellRendererComponent(JTable _table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		MultiSpanCellTable table = (MultiSpanCellTable) _table;
		if (isShowLine == null) {
			isShowLine = (Boolean) table.getClientProperty("isshowline");
			if (isShowLine == null) {
				isShowLine = Boolean.TRUE;
			}
		}
		Color foreground = null;
		Color background = null;
		Font font = null;
		JComponent component = null;
		if (value != null) {
			BillCellItemVO cellItemVO = (BillCellItemVO) value; //
			String str_celltype = cellItemVO.getCelltype(); //
			if (str_celltype == null || str_celltype.equals(BillCellPanel.ITEMTYPE_TEXT) || str_celltype.equals(BillCellPanel.ITEMTYPE_NUMBERTEXT)) { //�ı���
				component = cell_label; //
			} else if (str_celltype.equals(BillCellPanel.ITEMTYPE_TEXTAREA)) { //�����ı�
				component = cell_wltlabel; //
			} else if (str_celltype.equals(BillCellPanel.ITEMTYPE_CHECKBOX)) { //��ѡ��
				component = cell_checkbox; //
			} else if (str_celltype.equals(BillCellPanel.ITEMTYPE_COMBOBOX)) { //������
				component = cell_label;
			} else if (str_celltype.equals(BillCellPanel.ITEMTYPE_DATE)) { //����..
				component = cell_ref_date;
			} else if (str_celltype.equals(BillCellPanel.ITEMTYPE_DATETIME)) { //ʱ��..
				component = cell_ref_time;
			} else {
				component = cell_label;
			}
			cellItemVO.setItemTypechanged(false); //
		} else {
			component = cell_label; //
		}

		boolean isMouseMoving = false; //
		if (table.getMouseMovingRow() == row && table.getMouseMovingCol() == column) { //�����ǰ��������
			isMouseMoving = true; //
		}
		//����ֵ,���ÿؼ�״̬
		BillCellItemVO cellItemVO = null;
		boolean isHtmlHref = false; //�Ƿ��ǳ����ӷ��?
		if (value != null) {
			cellItemVO = (BillCellItemVO) value; //
			if (component instanceof WLTLabel) { //�ı���
				if (isMouseMoving && "Y".equals(cellItemVO.getIshtmlhref())) { //ֻ�й����ȥ����ʾHtmlЧ��
					cell_wltlabel.setText("<html><u>" + value.toString() + "</u></html>"); //
					isHtmlHref = true; //
				} else {
					cell_wltlabel.setText(value.toString()); //
				}
				//��������
				if (cellItemVO.getHalign() == 1) {
					cell_wltlabel.setHorizontalAlignment(SwingConstants.LEFT);
				} else if (cellItemVO.getHalign() == 2) {
					cell_wltlabel.setHorizontalAlignment(SwingConstants.CENTER);
				} else if (cellItemVO.getHalign() == 3) {
					cell_wltlabel.setHorizontalAlignment(SwingConstants.RIGHT);
				}

				//��������
				if (cellItemVO.getValign() == 1) {
					cell_wltlabel.setVerticalAlignment(SwingConstants.TOP);
				} else if (cellItemVO.getValign() == 2) {
					cell_wltlabel.setVerticalAlignment(SwingConstants.CENTER);
				} else if (cellItemVO.getValign() == 3) {
					cell_wltlabel.setVerticalAlignment(SwingConstants.BOTTOM);
				}
			} else if (component instanceof JLabel) {
				if (isMouseMoving && "Y".equals(cellItemVO.getIshtmlhref())) { //ֻ�й����ȥ����ʾHtmlЧ��
					cell_label.setText("<html><u>" + value.toString() + "</u></html>"); ////
					isHtmlHref = true; //
				} else {
					cell_label.setText(value.toString()); ////
				}
				if (cellItemVO.getHalign() == 1) {
					cell_label.setHorizontalAlignment(SwingConstants.LEFT);
				} else if (cellItemVO.getHalign() == 2) {
					cell_label.setHorizontalAlignment(SwingConstants.CENTER);
				} else if (cellItemVO.getHalign() == 3) {
					cell_label.setHorizontalAlignment(SwingConstants.RIGHT);
				}
			} else if (component instanceof JCheckBox) { //��ѡ��
				if (cellItemVO.getCellvalue() != null && cellItemVO.getCellvalue().equals("Y")) {
					cell_checkbox.setSelected(true); //
				} else {
					cell_checkbox.setSelected(false); //
				}
			} else if (component instanceof CellRefPanel) { //����
				if (((CellRefPanel) component).getType().equals(BillCellPanel.ITEMTYPE_DATE)) {
					cell_ref_date.setValue(value.toString());
				} else if (((CellRefPanel) component).getType().equals(BillCellPanel.ITEMTYPE_DATETIME)) {
					cell_ref_time.setValue(value.toString());
				}
			}

			foreground = getColor(cellItemVO.getForeground());
			background = getColor(cellItemVO.getBackground());
			if (cellItemVO.getFonttype() != null) {
				font = new Font(cellItemVO.getFonttype(), Integer.parseInt(cellItemVO.getFontstyle()), Integer.parseInt(cellItemVO.getFontsize())+LookAndFeel.getFONT_REVISE_SIZE()); //
			}

			//			if (cellItemVO.getCellhelp() != null) { ����2013-09-17ע�͵�
			component.setToolTipText(cellItemVO.getCellhelp()); //
			//			}
		} else {
			cell_label.setText(""); //
		}

		component.setFont((font != null) ? font : table.getFont()); //��������
		if (isHtmlHref) { //�����Html,��������ɫ�̶�Ϊ��ɫ
			component.setForeground(Color.BLUE); //
		} else {
			component.setForeground((foreground != null) ? foreground : Color.BLACK); //ǰ����ɫ
		}
		component.setBackground((background != null) ? background : Color.WHITE); //������ɫ

		if (isShowLine.booleanValue()) { //�����ʾ��
			if (isSelected) { //����ǵ����ѡ����,��߿�Ϊ��ɫ
				if (!table.isShowCellKey()) { //�����������,������ʾCellKey,���ɫ
					component.setBorder(BorderFactory.createLineBorder(Color.RED, 1)); //
				} else {
					cell_keyLabel.setText(""); //
					if (value != null) {
						if (cellItemVO != null && cellItemVO.getCellkey() != null) {
							this.cell_keyLabel.setText(cellItemVO.getCellkey()); //
						}
					}
					this.cell_keyLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 1)); //�������ʾcellKey,����ɫ��ʾ!
					component = this.cell_keyLabel; //
				}

			} else {
				component.setBorder(new EmptyBorder(1, 2, 1, 2));
			}
		}

		return component;
	}

	private Color getColor(String _value) {
		if (_value == null) {
			return null;
		}
		String[] str_items = _value.split(",");
		int li_red = Integer.parseInt(str_items[0]); //
		int li_green = Integer.parseInt(str_items[1]); //
		int li_blue = Integer.parseInt(str_items[2]); //
		return new Color(li_red, li_green, li_blue); //
	}
}
