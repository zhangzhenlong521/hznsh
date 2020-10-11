/**************************************************************************
 * $RCSfile: ListAndTableFactoty.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.mdata.listcomp.ListCellEditor_RowNumber;

/**
 * 用来生成带有滚动条的JList和JTable
 * 
 * @author Administrator
 * 
 */
public class ListAndTableFactoty {
	private static String str_rownumberMark = "_RECORD_ROW_NUMBER";

	private static final Font smallFont = new Font("宋体", Font.PLAIN, 12);

	private JList jl_list;

	private JTable jt_table;

	private int[] columnwiths;

	private DefaultTableColumnModel dtcm_com = null;

	public ListAndTableFactoty() {
	}

	/**
	 * 返回一个带有标题的JList
	 * 
	 * @param _title
	 * @return
	 */
	public JPanel getJSPList(String _title) {
		JPanel jpn_temp = new JPanel();
		jpn_temp.setLayout(new BorderLayout());

		JLabel jlb_title = new JLabel(_title);
		jlb_title.setPreferredSize(new Dimension(150, 20));
		jlb_title.setHorizontalAlignment(JLabel.CENTER);

		jl_list = new JList(new DefaultListModel());
		jl_list.setFont(smallFont);

		JScrollPane jsp_list = new JScrollPane(jl_list);
		jsp_list.setPreferredSize(new Dimension(250, 280));
		jsp_list.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		jpn_temp.add(jsp_list, BorderLayout.CENTER);
		jpn_temp.add(jlb_title, BorderLayout.NORTH);

		return jpn_temp;
	}

	/**
	 * 返回带有滚动条的JTable
	 * 
	 * @param _values：Object[][]
	 * @param _columnnames
	 * @param _columnwidths
	 * @return
	 */
	public JScrollPane getJSPTable(Vector _values, String[] _columnnames, int[] _columnwidths) {
		this.columnwiths = _columnwidths;
		Object[][] obj_temp = new Object[_values.size()][];
		for (int i = 0; i < _values.size(); i++) {
			obj_temp[i] = (Object[]) _values.get(i);
		}
		jt_table = getTable(obj_temp, _columnnames);
		return getJSP(jt_table);
	}

	/**
	 * 返回带有滚动条的JTable
	 * 
	 * @param _values
	 * @param _columnnames
	 * @param _columnwidths
	 * @return
	 */
	public JScrollPane getJSPTable(Object[][] _values, String[] _columnnames, int[] _columnwidths) {
		this.columnwiths = _columnwidths;
		jt_table = getTable(_values, _columnnames);
		return getJSP(jt_table);
	}

	/**
	 * 根据指定的TableModel和ColumnModel来产生带有滚动条的JTable
	 * 
	 * @param _tablemodel
	 * @param _columnmodel
	 * @return
	 */
	public JScrollPane getJSPTable(DefaultTableModel _tablemodel, DefaultTableColumnModel _columnmodel) {
		dtcm_com = _columnmodel;
		jt_table = new JTable(_tablemodel, dtcm_com);
		return getJSP(jt_table);
	}

	/**
	 * 给JTable加滚动条
	 * 
	 * @param _table
	 * @return
	 */
	private JScrollPane getJSP(JTable _table) {
		JScrollPane jsp_table = new JScrollPane(_table);
		JTable t_header = getTableHeader((DefaultTableModel) _table.getModel());
		_table.setSelectionModel(t_header.getSelectionModel());
		jsp_table.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp_table.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

		JViewport jv_field = new JViewport();
		jv_field.setView(t_header);

		int _height = new Double(t_header.getMaximumSize().getHeight()).intValue();
		jv_field.setPreferredSize(new Dimension(45, _height));

		jsp_table.setRowHeader(jv_field);
		jsp_table.setCorner(JScrollPane.UPPER_LEFT_CORNER, t_header.getTableHeader());
		jsp_table.updateUI();
		return jsp_table;
	}

	/**
	 * 获得JTable
	 * 
	 * @param _values
	 * @param _columnnames
	 * @return
	 */
	private JTable getTable(Object[][] _values, String[] _columnnames) {

		DefaultTableModel tableModel = getTableModel(_columnnames, _values);
		dtcm_com = getColumnModel(_columnnames);

		JTable table = new JTable(tableModel, dtcm_com);
		table.setRowSelectionAllowed(true);// 设置可否被选择.默认为false
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		return table;
	}

	/**
	 * 返回产生的JTable
	 * 
	 * @return
	 */
	public JTable getTable() {
		return jt_table;
	}

	/**
	 * 返回产生的JTable的columnModel
	 * 
	 * @return
	 */
	public DefaultTableColumnModel getTableColumnModel() {
		if (jt_table == null) {
			return null;
		}
		return (DefaultTableColumnModel) jt_table.getColumnModel();
	}

	/**
	 * 返回产生的JTable的TableModel
	 * 
	 * @return
	 */
	public DefaultTableModel getTableModel() {
		if (jt_table == null) {
			return null;
		}
		return (DefaultTableModel) jt_table.getModel();
	}

	public JList getList() {
		return jl_list;
	}

	/**
	 * 返回产生的JList的ListModel
	 * 
	 * @return
	 */
	public DefaultListModel getListModel() {
		if (jl_list == null) {
			return null;
		}
		return (DefaultListModel) jl_list.getModel();
	}

	/**
	 * 把指定列设为指定的CellRenderer
	 * 
	 * @param _render
	 * @param _index
	 */
	public void setAllColumnCellRender(TableCellRenderer _render) {
		if (dtcm_com == null) {
			return;
		}
		int li_count = dtcm_com.getColumnCount();
		for (int i = 0; i < li_count; i++) {
			dtcm_com.getColumn(i).setCellRenderer(_render);
		}
		jt_table.updateUI();
	}

	/**
	 * 把指定列设为指定的CellRenderer
	 * 
	 * @param _render
	 * @param _index
	 */
	public void setColumnCellRender(TableCellRenderer _render, int _index) {
		if (dtcm_com == null) {
			return;
		}
		if (_index >= dtcm_com.getColumnCount()) {
			return;
		}
		dtcm_com.getColumn(_index).setCellRenderer(_render);
		jt_table.updateUI();
	}

	/**
	 * 把指定列设为指定的CellEditor
	 * 
	 * @param _render
	 * @param _index
	 */
	public void setColumnCellEditor(TableCellEditor _editor, int _index) {
		if (dtcm_com == null) {
			return;
		}
		if (_index >= dtcm_com.getColumnCount()) {
			return;
		}
		dtcm_com.getColumn(_index).setCellEditor(_editor);
		jt_table.updateUI();
	}

	/**
	 * 所有列均设为不可编辑
	 */
	public void setAllColumnUnEditeable() {
		if (dtcm_com == null) {
			return;
		}
		int li_count = dtcm_com.getColumnCount();
		setColumnUnEditeable(li_count);
	}

	/**
	 * _index前面的所有列均设为不可编辑
	 * 
	 * @param _index
	 */
	public void setColumnUnEditeable(int _index) {
		if (dtcm_com == null) {
			return;
		}
		MyColumnCellEditor mce_com = new MyColumnCellEditor();

		int li_count = dtcm_com.getColumnCount() > _index ? _index : dtcm_com.getColumnCount();
		for (int i = 0; i < li_count; i++) {
			dtcm_com.getColumn(i).setCellEditor(mce_com);
		}
		jt_table.updateUI();
	}

	/**
	 * 设置不可编辑的列
	 * 
	 * @param _index
	 */
	public void setColumnUnEditeable(int[] _indexes) {
		if (dtcm_com == null) {
			return;
		}
		MyColumnCellEditor mce_com = new MyColumnCellEditor();

		for (int i = 0; i < _indexes.length; i++) {
			if (dtcm_com.getColumnCount() <= _indexes[i]) {
				continue;
			}
			dtcm_com.getColumn(_indexes[i]).setCellEditor(mce_com);
		}
		jt_table.updateUI();
	}

	/**
	 * 获取数据表的TableModel
	 * 
	 * @param _columnnames
	 * @param _data
	 * @return
	 */
	private DefaultTableModel getTableModel(String[] _columnnames, Object[][] _data) {
		DefaultTableModel temp_tablemodel = null;

		String[] new_columns = new String[_columnnames.length + 1];
		new_columns[0] = str_rownumberMark; //
		for (int i = 0; i < _columnnames.length; i++) {
			new_columns[i + 1] = _columnnames[i];
		}
		temp_tablemodel = new DefaultTableModel(); // 创建数据Model

		temp_tablemodel.setColumnIdentifiers(new_columns);
		for (int i = 0; i < _data.length; i++) {
			temp_tablemodel.addRow(_data[i]);
		}
		return temp_tablemodel;
	}

	/**
	 * 获取数据表的ColumnModel
	 * 
	 * @param _columnnames
	 * @return
	 */
	private DefaultTableColumnModel getColumnModel(String[] _columnnames) {
		DefaultTableColumnModel temp_columnmodel = null;

		MyColumnCellRender myTableCellRenderer = new MyColumnCellRender(); // 创建表体的Render

		temp_columnmodel = new DefaultTableColumnModel(); // 创建列模式

		//		TableColumn[] columns = new TableColumn[_columnnames.length]; // 创建列,有列号

		if (columnwiths == null) {
			columnwiths = getColumnwidth(_columnnames.length);
		}
		for (int i = 0; i < _columnnames.length; i++) {
			if (!_columnnames[i].endsWith(WLTConstants.STRING_REFPANEL_UNSHOWSIGN)) {
				TableColumn column = new TableColumn(i);
				//				columns[i] = new TableColumn(i); //
				//				columns[i].setPreferredWidth(columnwiths[i]);
				//				columns[i].setHeaderValue(_columnnames[i]);
				//				columns[i].setCellRenderer(myTableCellRenderer); //
				//				temp_columnmodel.addColumn(columns[i]);
				column = new TableColumn(i); //
				column.setPreferredWidth(columnwiths[i]);
				column.setHeaderValue(_columnnames[i]);
				column.setCellRenderer(myTableCellRenderer); //
				temp_columnmodel.addColumn(column);
			}
		}

		return temp_columnmodel;
	}

	private int[] getColumnwidth(int _length) {
		int[] width = new int[_length];
		for (int i = 0; i < _length; i++) {
			width[i] = 120;
		}
		return width;
	}

	/**
	 * 获取行号列
	 * 
	 * @return
	 */
	private TableColumn getRowNumberColumn() {
		TableCellRenderer render = new RowNumberCellRender(); // 创建列绘制器..
		ListCellEditor_RowNumber editor = new ListCellEditor_RowNumber();
		TableColumn rowNumberColumn = new TableColumn(0, 45, render, editor); // 创建列,对应第一列数据
		rowNumberColumn.setHeaderValue("行号");
		rowNumberColumn.setIdentifier(str_rownumberMark);
		return rowNumberColumn;
	}

	/**
	 * 产生所有表的表头
	 * 
	 * @param _tablemodel
	 * @return
	 */
	private JTable getTableHeader(DefaultTableModel _tablemodel) {
		DefaultTableColumnModel headerColumnModel = new DefaultTableColumnModel();
		headerColumnModel.addColumn(getRowNumberColumn()); // 加入行号列

		JTable table_header = new JTable(_tablemodel, headerColumnModel);
		table_header.setRowSelectionAllowed(true);
		table_header.setColumnSelectionAllowed(false);
		table_header.setBackground(new Color(240, 240, 240));
		table_header.getTableHeader().setReorderingAllowed(false);
		table_header.getTableHeader().setResizingAllowed(false);
		table_header.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		return table_header;
	}

	/**
	 * 数据表的CellRender
	 * 
	 * @author Administrator
	 * 
	 */
	class MyColumnCellRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 3282053393215235179L;

		public MyColumnCellRender() {
		}

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (isSelected) {
				label.setBackground(table.getSelectionBackground());
			} else {
				label.setBackground(Color.WHITE);
			}
			return label;
		}
	}

	/**
	 * 数据表中不可编辑的时候，选择该Editor
	 * 
	 * @author Administrator
	 * 
	 */
	class MyColumnCellEditor extends JLabel implements TableCellEditor {
		private static final long serialVersionUID = 1113504046115564928L;

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			if (value == null) {
				this.setText("");
			} else {
				this.setText("" + value.toString());
			}
			return this;
		}

		public void addCellEditorListener(CellEditorListener l) {
		}

		public void cancelCellEditing() {
		}

		public Object getCellEditorValue() {
			return null;
		}

		public boolean isCellEditable(EventObject anEvent) {
			return false;
		}

		public void removeCellEditorListener(CellEditorListener l) {
		}

		public boolean shouldSelectCell(EventObject anEvent) {
			return false;
		}

		public boolean stopCellEditing() {
			return false;
		}
	}

	class RowNumberCellRender extends JLabel implements TableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
			this.setOpaque(true);
			if (isSelected) {
				this.setBackground(table.getSelectionBackground());
			} else {
				this.setBackground(new Color(240, 240, 240));
			}

			if (value instanceof RowNumberItemVO) {
				RowNumberItemVO valueVO = (RowNumberItemVO) value; //
				if (valueVO != null) {
				} else {
					this.setForeground(java.awt.Color.BLACK);
					this.setText("" + (rowIndex + 1)); //
				}

				this.setHorizontalAlignment(JLabel.RIGHT);//SwingConstants.RIGHT

				this.setToolTipText(value.toString());
			} else if (value instanceof String) {
				this.setText("" + (rowIndex + 1));
			}

			return this;
		}

		public void validate() {
		}

		public void revalidate() {
		}

		protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		}

		public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
		}

	}
}
/**************************************************************************
 * $RCSfile: ListAndTableFactoty.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: ListAndTableFactoty.java,v $
 * Revision 1.6  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.5  2011/10/10 06:31:47  wanggang
 * restore
 *
 * Revision 1.3  2011/08/10 14:18:07  xch123
 * *** empty log message ***
 *
 * Revision 1.2  2010/12/28 10:30:11  xch123
 * 12月28日提交
 *
 * Revision 1.1  2010/05/17 10:23:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:02:00  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:30  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:42  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:24  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:29  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
