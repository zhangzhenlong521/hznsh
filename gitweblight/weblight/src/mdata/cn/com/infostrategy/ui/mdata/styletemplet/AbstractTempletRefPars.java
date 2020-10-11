/**************************************************************************
 * $RCSfile: AbstractTempletRefPars.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet;

import java.awt.Color;
import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;

public abstract class AbstractTempletRefPars extends JPanel {
	private VectorMap components_map = null;
	private String uiinterceptor = "";
	private String bsinterceptor = "";
	private String customerpanel = "";
	private String showsysbutton = "是";
	JTable interceptortable = new JTable();

	public AbstractTempletRefPars() {
		super();
	}

	public AbstractTempletRefPars(VectorMap _map) {
		super();
		this.components_map = _map;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		createView();
	}

	public void initialization(VectorMap _map) {
		this.components_map = _map;
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		createView();
	}

	private void createView() {
		JSplitPane split = new JSplitPane();
		split.setOneTouchExpandable(true);
		split.setOrientation(JSplitPane.VERTICAL_SPLIT);
		split.setDividerLocation(200);
		split.setTopComponent(getTopComponent());
		split.setBottomComponent(getBottomComponent());
		this.add(split);
	}

	private JScrollPane getTopComponent() {
		JComponent comp = (JComponent) this.components_map.get(WLTConstants.STRING_REFPANEL_COMMON_TITLE);
		JScrollPane jsp = new JScrollPane(comp);
		return jsp;
	}

	private JScrollPane getBottomComponent() {
		MyTableModel model = new MyTableModel();
		MyCellEditor editor = new MyCellEditor();
		interceptortable.setModel(model);
		interceptortable.setRowHeight(20);
		interceptortable.getColumnModel().getColumn(0).setCellEditor(editor);
		interceptortable.getColumnModel().getColumn(1).setCellRenderer(new AbstractTempletRefPars.TempletRefCellRender());
		interceptortable.getColumnModel().getColumn(0).setPreferredWidth(150);
		interceptortable.getColumnModel().getColumn(1).setPreferredWidth(150);
		JScrollPane jsp = new JScrollPane(interceptortable);
		return jsp;
	}

	private class TempletRefCellRender implements TableCellRenderer {
		JTextArea field = new JTextArea();

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
			field.setText(value.toString());
			field.setForeground(Color.GRAY);
			field.setBackground(new Color(240, 240, 240));
			return field;
		}

	}

	private class MyTableModel extends AbstractTableModel {

		public int getColumnCount() {
			return 2;
		}

		public String getColumnName(int e) {
			String re = "";
			if (e == 0)
				re = "选项";
			else if (e == 1)
				re = "说明";
			return re;
		}

		public int getRowCount() {
			return 4;
		}

		public boolean isCellEditable(int r, int c) {
			if (c == 1)
				return false;
			return true;
		}

		public void setValueAt(Object arg0, int r, int c) {
			if (arg0 == null)
				return;
			if (c == 0) {
				if (r == 0)
					customerpanel = arg0.toString();
				else if (r == 1)
					uiinterceptor = arg0.toString();
				else if (r == 2)
					bsinterceptor = arg0.toString();
				else if (r == 3)
					showsysbutton = arg0.toString();

			}
		}

		public Object getValueAt(int r, int c) {
			if (c == 0) {
				if (r == 0)
					return customerpanel;
				else if (r == 1)
					return uiinterceptor;
				else if (r == 2)
					return bsinterceptor;
				else if (r == 3)
					return showsysbutton;
			} else {
				if (r == 0)
					return "自定义面板";
				else if (r == 1)
					return uiInformation();
				else if (r == 2)
					return bsInformation();
				else if (r == 3)
					return "是否显示系统按钮栏";
			}
			return "";
		}

	}

	public class MyCellEditor extends AbstractCellEditor implements TableCellEditor {
		JTextField text = null;
		Object[] items = new Object[] { "是", "否" };
		JComboBox showsysbtn = new JComboBox(items);
		boolean onselected = false;

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int col) {
			if (row == 3 && col == 0) {
				onselected = true;
				if (value instanceof String) {
					if (((String) value).equals("是"))
						showsysbtn.setSelectedIndex(0);
					else
						showsysbtn.setSelectedIndex(1);
					return showsysbtn;
				}
			}
			text = new JTextField(value.toString());
			return text;
		}

		public Object getCellEditorValue() {
			if (onselected)
				return showsysbtn.getSelectedItem();
			return text.getText();
		}
	}

	public String getUIInterceptor() {
		return this.uiinterceptor;
	}

	public String getBSInterceptor() {
		return this.bsinterceptor;
	}

	public String getCustomerpanel() {
		return this.customerpanel;
	}

	public String isShowSysBtn() {
		return this.showsysbutton;
	}

	public void setShowSysBtn(String _show) {
		this.showsysbutton = _show;
	}

	public void setUIInterceptor(String _ui) {
		this.uiinterceptor = _ui;
	}

	public void setBSInterceptor(String _bs) {
		this.bsinterceptor = _bs;
	}

	public void setCustomerpanel(String _customer) {
		this.customerpanel = _customer;
	}

	public void stopTableEditing() {
		interceptortable.editingStopped(new ChangeEvent(interceptortable));
	}

	public JTable getInterceptorTable() {
		return interceptortable;
	}

	public abstract VectorMap getParameters();

	public abstract void stopEdit();

	protected abstract String uiInformation();

	protected abstract String bsInformation();
}
