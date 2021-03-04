package cn.com.infostrategy.ui.mdata;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.propcomp.BillPropComboboxCellEditor;
import cn.com.infostrategy.ui.mdata.propcomp.BillPropComboboxCellRender;
import cn.com.infostrategy.ui.mdata.propcomp.BillPropRefCellEditor;
import cn.com.infostrategy.ui.mdata.propcomp.BillPropRefCellEditor_Multi2;

import com.l2fprod.common.propertysheet.CellEditorAdapter;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;

/**
 * �����б�!!!
 * @author xch
 *
 */
public class BillPropTable extends PropertySheetTable {

	private static final long serialVersionUID = -8806146954615180208L;
	private BillPropPanel billPropPanel = null;
	private Pub_Templet_1VO templetVO = null;
	private Pub_Templet_1_ItemVO[] templetItemVOs = null; //

	public BillPropTable(Pub_Templet_1VO _templetVO) {
		super(); //
		this.setRowHeight(20); //�и�
		this.setRowSelectionAllowed(true); //�Ƿ����ѡ����
		this.setColumnSelectionAllowed(false); //�Ƿ����ѡ����
		this.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS); //
		this.getColumnModel().getColumn(0).setPreferredWidth(80); //�п�!!
		this.getColumnModel().getColumn(1).setPreferredWidth(115); //�п�!!
		this.setFont(LookAndFeel.font); //
		this.setGridColor(new java.awt.Color(215, 215, 215)); //�����ߵ���ɫ!!
		this.setCategoryBackground(new java.awt.Color(215, 215, 215)); //��ı�����ɫ

		this.templetVO = _templetVO; //����
		this.templetItemVOs = _templetVO.getItemVos(); //��ϸ��
		this.cancelEditing();
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		Item item = (Item) this.getValueAt(row, 0); //ȡ��ĳһ�������!!
		if (item == null || item.getProperty() == null) {
			return super.getCellRenderer(row, column);
		}

		if (column == 1) {
			String str_key = item.getProperty().getName(); //ȡ��ItemKey,
			Pub_Templet_1_ItemVO itemVO = templetVO.getItemVo(str_key); //
			String itemType = itemVO.getItemtype();
			if (itemType.equals(WLTConstants.COMP_TEXTFIELD) || itemType.equals(WLTConstants.COMP_NUMBERFIELD) || itemType.equals(WLTConstants.COMP_PASSWORDFIELD)) { //������ı�������ֿ�
				return super.getCellRenderer(row, column);
			} else if (itemType.equals(WLTConstants.COMP_COMBOBOX)) { //������
				return new BillPropComboboxCellRender(itemVO); //
			} else if (itemType.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���1
					itemType.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���1
					itemType.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Լ��������
					itemType.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //���Ͳ���1
					itemType.equals(WLTConstants.COMP_DATE) || //����
					itemType.equals(WLTConstants.COMP_DATETIME) || //ʱ��
					itemType.equals(WLTConstants.COMP_FILECHOOSE) || //�ļ�ѡ���
					itemType.equals(WLTConstants.COMP_COLOR) || //��ɫѡ���
					itemType.equals(WLTConstants.COMP_BIGAREA) || //���ı���
					itemType.equals(WLTConstants.COMP_PICTURE) //ͼ��ѡ���)
			) {
				return super.getCellRenderer(row, column); //
			} else if (itemType.equals(WLTConstants.COMP_REFPANEL_MULTI)) { //��ѡ����
				//return new BillPropRefCellRender_Multi2(itemVO); //
				return super.getCellRenderer(row, column); //
			} else {
				return super.getCellRenderer(row, column);
			}
		} else {
			return super.getCellRenderer(row, column);
		}
	}

	/**
	 * �༭��!!!
	 */
	public TableCellEditor getCellEditor(int row, int column) {
		Item item = (Item) this.getValueAt(row, 0); //ȡ��ĳһ�������!!
		String str_key = item.getProperty().getName(); //ȡ��ItemKey,

		Pub_Templet_1_ItemVO itemVO = templetVO.getItemVo(str_key); //
		String itemType = itemVO.getItemtype();
		if (itemType.equals(WLTConstants.COMP_TEXTFIELD)) {
			return super.getCellEditor(row, column); //
		} else if (itemType.equals(WLTConstants.COMP_NUMBERFIELD)) {
			JTextField textField = new JFormattedTextField();
			textField.setDocument(new NumberFormatdocument());
			textField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
			return new DefaultCellEditor(textField);
		} else if (itemType.equals(WLTConstants.COMP_COMBOBOX)) { //�����������!!!
			String str_value = (String) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropComboboxCellEditor(itemVO, str_value)); //����������ؼ�!
		} else if (itemType.equals(WLTConstants.COMP_REFPANEL)) { //��׼����
			String str_value = (String) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropRefCellEditor(itemVO, str_value, this)); //�������տؼ�
		} else if (itemType.equals(WLTConstants.COMP_REFPANEL) || //���Ͳ���1
				itemType.equals(WLTConstants.COMP_REFPANEL_TREE) || //���Ͳ���1
				itemType.equals(WLTConstants.COMP_REFPANEL_CUST) || //�Զ������
				itemType.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //�б�ģ�����
				itemType.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //����ģ�����
				itemType.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //ע���������
				itemType.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //ע�����
				itemType.equals(WLTConstants.COMP_DATE) || //����
				itemType.equals(WLTConstants.COMP_DATETIME) || //ʱ��
				itemType.equals(WLTConstants.COMP_FILECHOOSE) || //�ļ�ѡ���
				itemType.equals(WLTConstants.COMP_COLOR) || //��ɫѡ���
				itemType.equals(WLTConstants.COMP_BIGAREA) || //���ı���
				itemType.equals(WLTConstants.COMP_PICTURE) //ͼ��ѡ���)
		) {
			String str_value = (String) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropRefCellEditor(itemVO, str_value, this)); //����2�ı༭��!!
		} else if (itemType.equals(WLTConstants.COMP_REFPANEL_MULTI)) { //��ѡ����
			RefItemVO str_value = (RefItemVO) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropRefCellEditor_Multi2(itemVO, str_value, this)); //�������տؼ�
		} else {
			return super.getCellEditor(row, column); //
		}
	}

	/**
	 * 
	 */
	public void stopEditing() {
		try {
			if (this.getRowCount() >= 0 && this.getCellEditor() != null) {
				this.getCellEditor().stopCellEditing(); //
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public BillPropPanel getBillPropPanel() {
		return billPropPanel;
	}

	public void setBillPropPanel(BillPropPanel billPropPanel) {
		this.billPropPanel = billPropPanel;
	}

}
