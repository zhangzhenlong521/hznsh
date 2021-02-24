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
 * 属性列表!!!
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
		this.setRowHeight(20); //行高
		this.setRowSelectionAllowed(true); //是否可以选择行
		this.setColumnSelectionAllowed(false); //是否可以选择列
		this.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS); //
		this.getColumnModel().getColumn(0).setPreferredWidth(80); //列宽!!
		this.getColumnModel().getColumn(1).setPreferredWidth(115); //列宽!!
		this.setFont(LookAndFeel.font); //
		this.setGridColor(new java.awt.Color(215, 215, 215)); //网络线的颜色!!
		this.setCategoryBackground(new java.awt.Color(215, 215, 215)); //组的背景颜色

		this.templetVO = _templetVO; //主表
		this.templetItemVOs = _templetVO.getItemVos(); //明细项
		this.cancelEditing();
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		Item item = (Item) this.getValueAt(row, 0); //取得某一项的内容!!
		if (item == null || item.getProperty() == null) {
			return super.getCellRenderer(row, column);
		}

		if (column == 1) {
			String str_key = item.getProperty().getName(); //取得ItemKey,
			Pub_Templet_1_ItemVO itemVO = templetVO.getItemVo(str_key); //
			String itemType = itemVO.getItemtype();
			if (itemType.equals(WLTConstants.COMP_TEXTFIELD) || itemType.equals(WLTConstants.COMP_NUMBERFIELD) || itemType.equals(WLTConstants.COMP_PASSWORDFIELD)) { //如果是文本框或数字框
				return super.getCellRenderer(row, column);
			} else if (itemType.equals(WLTConstants.COMP_COMBOBOX)) { //下拉框
				return new BillPropComboboxCellRender(itemVO); //
			} else if (itemType.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
					itemType.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照1
					itemType.equals(WLTConstants.COMP_REFPANEL_CUST) || //自己定义参照
					itemType.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //树型参照1
					itemType.equals(WLTConstants.COMP_DATE) || //日历
					itemType.equals(WLTConstants.COMP_DATETIME) || //时间
					itemType.equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框
					itemType.equals(WLTConstants.COMP_COLOR) || //颜色选择框
					itemType.equals(WLTConstants.COMP_BIGAREA) || //大文本框
					itemType.equals(WLTConstants.COMP_PICTURE) //图标选择框)
			) {
				return super.getCellRenderer(row, column); //
			} else if (itemType.equals(WLTConstants.COMP_REFPANEL_MULTI)) { //多选参照
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
	 * 编辑器!!!
	 */
	public TableCellEditor getCellEditor(int row, int column) {
		Item item = (Item) this.getValueAt(row, 0); //取得某一项的内容!!
		String str_key = item.getProperty().getName(); //取得ItemKey,

		Pub_Templet_1_ItemVO itemVO = templetVO.getItemVo(str_key); //
		String itemType = itemVO.getItemtype();
		if (itemType.equals(WLTConstants.COMP_TEXTFIELD)) {
			return super.getCellEditor(row, column); //
		} else if (itemType.equals(WLTConstants.COMP_NUMBERFIELD)) {
			JTextField textField = new JFormattedTextField();
			textField.setDocument(new NumberFormatdocument());
			textField.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 0, cn.com.infostrategy.ui.common.LookAndFeel.compBorderLineColor)); //
			return new DefaultCellEditor(textField);
		} else if (itemType.equals(WLTConstants.COMP_COMBOBOX)) { //如果是下拉框!!!
			String str_value = (String) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropComboboxCellEditor(itemVO, str_value)); //创建下拉框控件!
		} else if (itemType.equals(WLTConstants.COMP_REFPANEL)) { //标准参照
			String str_value = (String) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropRefCellEditor(itemVO, str_value, this)); //创建参照控件
		} else if (itemType.equals(WLTConstants.COMP_REFPANEL) || //表型参照1
				itemType.equals(WLTConstants.COMP_REFPANEL_TREE) || //树型参照1
				itemType.equals(WLTConstants.COMP_REFPANEL_CUST) || //自定义参照
				itemType.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || //列表模板参照
				itemType.equals(WLTConstants.COMP_REFPANEL_TREETEMPLET) || //树型模板参照
				itemType.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || //注册样板参照
				itemType.equals(WLTConstants.COMP_REFPANEL_REGEDIT) || //注册参照
				itemType.equals(WLTConstants.COMP_DATE) || //日历
				itemType.equals(WLTConstants.COMP_DATETIME) || //时间
				itemType.equals(WLTConstants.COMP_FILECHOOSE) || //文件选择框
				itemType.equals(WLTConstants.COMP_COLOR) || //颜色选择框
				itemType.equals(WLTConstants.COMP_BIGAREA) || //大文本框
				itemType.equals(WLTConstants.COMP_PICTURE) //图标选择框)
		) {
			String str_value = (String) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropRefCellEditor(itemVO, str_value, this)); //参照2的编辑器!!
		} else if (itemType.equals(WLTConstants.COMP_REFPANEL_MULTI)) { //多选参照
			RefItemVO str_value = (RefItemVO) item.getProperty().getValue(); //
			return new CellEditorAdapter(new BillPropRefCellEditor_Multi2(itemVO, str_value, this)); //创建参照控件
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
