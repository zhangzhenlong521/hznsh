package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;

public class TableRefPanel extends JPanel implements ItemListener {
	private static final long serialVersionUID = 5550121525585752776L;
	private String str_datasourcename = null; //
	private String str_sql = null; //
	private RefItemVO initRefItemVO = null; //初始化的数据

	private TableDataStruct struct = null;
	private int allWidth = 0; //
	private JComboBox combox_condition = null;
	private JTextField textField;
	private JTable table = null;
	private JPanel northpanel = null;
	private HashMap rowheight_map = null;
	private String str_lasteditingText = null; //
	private JLabel label_count = new JLabel(""); //

	public TableRefPanel(String _dsName, String _sql, RefItemVO _initRefItemVO) {
		this.str_datasourcename = _dsName;
		this.str_sql = _sql; //
		this.initRefItemVO = _initRefItemVO; //
		try {
			struct = UIUtil.getTableDataStructByDS(null, str_sql); // 真正的数据
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		initContent(); //
	}

	/**
	 * 直接传进来数据!! 在从表创建模板时需要查询所有系统表,它不是一个SQL,而是一个远程方法!!!
	 * @param _dsName
	 * @param _struct
	 * @param _initRefItemVO
	 */
	public TableRefPanel(String _dsName, TableDataStruct _struct, RefItemVO _initRefItemVO) {
		this.str_datasourcename = _dsName;
		this.struct = _struct; //
		this.initRefItemVO = _initRefItemVO; //
		initContent(); //
	}

	private void initContent() {
		try {
			JPanel mainPanel = new JPanel();
			mainPanel.setOpaque(false); //透明!!
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getNorthPanel(), BorderLayout.NORTH);
			mainPanel.add(getMainPanel(), BorderLayout.CENTER);

			label_count.setForeground(Color.BLUE); //
			mainPanel.add(label_count, BorderLayout.SOUTH);

			this.setLayout(new BorderLayout());
			this.setOpaque(false); //透明!!
			this.add(mainPanel);
			int li_scrolltorow = getInitRow();
			if (li_scrolltorow >= 0) {
				Rectangle rect = table.getCellRect(li_scrolltorow, 0, true);
				table.scrollRectToVisible(rect); //滚动到对应的行的位置!!!!!!!!!!!!
				table.setRowSelectionInterval(li_scrolltorow, li_scrolltorow); //滚动到对应的行的位置!!!!!!!!!!!!
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textField.requestFocus(); //
					textField.requestFocusInWindow(); //
				}
			});
			label_count.setText("共有记录[" + table.getRowCount() + "]条"); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private JPanel getNorthPanel() {
		if (northpanel != null) {
			return northpanel;
		}

		northpanel = new JPanel();
		northpanel.setOpaque(false); //透明!!!
		northpanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		northpanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					onShowSQL();
				}
			}

		});

		combox_condition = new JComboBox();
		combox_condition.setPreferredSize(new Dimension(100, 20));
		String[] items = struct.getHeaderName();
		combox_condition.addItem("所有列"); //
		for (int i = 0; i < items.length; i++) {
			if (!items[i].endsWith(WLTConstants.STRING_REFPANEL_UNSHOWSIGN))
				combox_condition.addItem(items[i]);
		}
		combox_condition.addItemListener(this); //

		textField = new JTextField();
		textField.setPreferredSize(new Dimension(125, 20));
		textField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				onKeyReleased();
			}

			public void keyTyped(KeyEvent e) {
			}
		});

		JButton btn_search = new WLTButton(UIUtil.getLanguage("查询"));
		btn_search.setPreferredSize(new Dimension(60, 20));
		btn_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSearch();
			}
		});

		JLabel label1 = new JLabel("数据列", JLabel.RIGHT); //
		label1.setPreferredSize(new Dimension(50, 20)); //
		northpanel.add(label1); //
		northpanel.add(combox_condition);

		JLabel label2 = new JLabel("包含", JLabel.CENTER); //
		label2.setPreferredSize(new Dimension(25, 20)); //
		northpanel.add(label2); //
		northpanel.add(textField);
		northpanel.add(btn_search);

		return northpanel;
	}

	private JScrollPane getMainPanel() {
		JScrollPane scrollPanel = new JScrollPane();
		DefaultTableModel model = new DefaultTableModel(struct.getBodyData(), struct.getHeaderName()) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		DefaultTableColumnModel colmodel = new DefaultTableColumnModel();
		HashMap map_width = new HashMap(); //
		int li_allwidth = 0; //
		for (int i = 0; i < struct.getHeaderName().length; i++) { //处理每一列
			if (!struct.getHeaderName()[i].endsWith("$")) { //如果以美元符号结尾的则不显示!
				int li_colUIWidth = 150; //默认宽度
				if (struct.getColValueMaxLen() != null) { //如果有定义!!
					int li_colWidth = struct.getColValueMaxLen()[i]; //后台算出来的!!!
					li_colUIWidth = (li_colWidth * 6) + 10; //
					if (li_colUIWidth < 50) { //太小的显示50
						li_colUIWidth = 50;
					}
					if (li_colUIWidth > 175) { //太宽的只显示175
						li_colUIWidth = 175;
					}
				}
				map_width.put("" + i, new Integer(li_colUIWidth)); //记录下该列的宽度!
				li_allwidth = li_allwidth + li_colUIWidth; //记录下总的宽度!
			}
		}

		if (li_allwidth < 400) { //如果小于450,为了美观,自动补充宽度！ 这个处理很重要,从而避免缩在一角!!!
			int li_addFix = (400 - li_allwidth) / map_width.size(); //
			String[] str_keys = (String[]) map_width.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				int li_oldwidth = (Integer) map_width.get(str_keys[i]); //
				map_width.put(str_keys[i], new Integer(li_oldwidth + li_addFix)); //加上后缀
			}
			allWidth = 400; //
		} else {
			allWidth = li_allwidth; //
		}
		int max_all_width = 550;
		if (allWidth < max_all_width) {//如果宽度未达到最大，则进行加宽处理
			int col_all_width = 0;//所有列总共被缩了多少
			HashMap add_width_map = new HashMap();//记下列号，及被压缩的宽度
			for (int i = 0; i < struct.getHeaderName().length; i++) { //处理每一列
				if (!struct.getHeaderName()[i].endsWith("$")) { //如果以美元符号结尾的则不显示!
					int li_colUIWidth = 150; //默认宽度
					if (struct.getColValueMaxLen() != null) { //如果有定义!!
						int li_colWidth = struct.getColValueMaxLen()[i]; //后台算出来的!!!
						li_colUIWidth = (li_colWidth * 6) + 10; //
						int orig_width = (Integer) map_width.get("" + i);
						if (li_colUIWidth > orig_width) {
							int add_width = li_colUIWidth - orig_width;//被压缩的宽度
							col_all_width += add_width;
							add_width_map.put("" + i, new Integer(add_width));
						}
					}
				}
			}
			if (col_all_width != 0) {
				int AllAddWidth = max_all_width - allWidth;
				String[] str_keyss = (String[]) add_width_map.keySet().toArray(new String[0]); //
				for (int i = 0; i < str_keyss.length; i++) {
					int add_width = (Integer) add_width_map.get(str_keyss[i]);
					int real_add_width = AllAddWidth * add_width / col_all_width;//属于宽度不够，则按比例加宽
					real_add_width = real_add_width > add_width ? add_width : real_add_width;
					int orig_width = (Integer) map_width.get(str_keyss[i]);
					map_width.put(str_keyss[i], orig_width + real_add_width);
					li_allwidth += real_add_width;
					allWidth += real_add_width;
				}
			}
		}//加宽end
		//设置最终确定的列宽
		for (int i = 0; i < struct.getHeaderName().length; i++) { //处理每一列
			if (!struct.getHeaderName()[i].endsWith("$")) { //如果以美元符号结尾的则不显示!
				TableColumn col = new TableColumn(i, (Integer) map_width.get("" + i)); //取得宽度
				col.setCellRenderer(new MyCellRender()); //
				col.setHeaderValue(struct.getHeaderName()[i]);
				col.setIdentifier(struct.getHeaderName()[i]);
				colmodel.addColumn(col);
			}
		}
		table = new ResizableTable(model, colmodel);
		table.setRowHeight(19);
		//==开始加高处理(遍历每行各单元格，看是否有换行的)
		TBUtil tb = new TBUtil();
		String[][] data = struct.getBodyData();
		rowheight_map = new HashMap();
		for (int i = 0; i < data.length; i++) {
			rowheight_map.put(i, new Integer(19));//默认行高
			int max_row = 1;
			for (int j = 0; j < data[i].length; j++) {
				if (!struct.getHeaderName()[j].endsWith("$")) { //如果以美元符号结尾的则不显示!
					int col_width = (Integer) map_width.get("" + j);
					int col_str_len = tb.getStrUnicodeLength(data[i][j]);
					int col_len = col_str_len * 6 + 6;
					if (col_len > col_width) {//需要换行
						int row_num;//此单元格所占行数
						if (col_len % col_width != 0) {
							row_num = col_len / col_width + 1;
						} else {
							row_num = col_len / col_width;
						}
						if (row_num > max_row) {
							max_row = row_num;//设置为行最高
						}
					}
				}
			}
			if (max_row > 1) {
				rowheight_map.put(i, new Integer(max_row * 15));//设置行高
				table.setRowHeight(i, max_row * 15);
			}
		}
		table.setRowSelectionAllowed(true); //
		table.setColumnSelectionAllowed(false); //
		table.getTableHeader().setReorderingAllowed(false); //
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoscrolls(true);
		table.setOpaque(false); //透明
		table.getTableHeader().setOpaque(false);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getClickCount() == 2) {
						mouseDoubleClicked(e);
					}
				}
			}
		});

		//		JButton btn_showsql = new JButton(UIUtil.getImage("scrollpanel_corn.gif"));
		//		btn_showsql.addActionListener(new ActionListener() {
		//			public void actionPerformed(ActionEvent e) {
		//				onShowSQL();
		//			}
		//		}); //
		//
		//		scrollPanel.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btn_showsql);
		scrollPanel.getViewport().add(table);

		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //一定要设一下，否则上边总有个白条
		jv2.setView(table.getTableHeader());
		scrollPanel.setColumnHeader(jv2); //

		scrollPanel.setOpaque(false); //透明!!
		scrollPanel.getViewport().setOpaque(false); //透明!!
		return scrollPanel;
	}

	private void mouseDoubleClicked(MouseEvent e) {//sunfujun/20121208/华夏提出
		if (table.getModel().getColumnCount() < 3) {
			MessageBox.show(this, "表中列数必须至少有3列,现在少于3列,这是不允许的!");
		} else {
			RefDialog_Table rdt = (RefDialog_Table) SwingUtilities.getWindowAncestor(this);
			rdt.onConfirm();
		}
	}

	private int getInitRow() {
		int li_rowcount = table.getModel().getRowCount();
		String temp_str;
		for (int i = 0; i < li_rowcount; i++) {
			temp_str = (String) this.table.getModel().getValueAt(i, 0);
			if (this.initRefItemVO != null) {
				if (temp_str.equals(this.initRefItemVO.getId())) { //需要遍历!!!
					return i;
				}
			}
		}
		return -1;
	}

	private void clearTableData() {
		int li_rowcount = table.getModel().getRowCount();
		for (int i = 0; i < li_rowcount; i++) {
			((DefaultTableModel) table.getModel()).removeRow(0);
		}
	}

	private void onKeyReleased() {
		quickQueryData(); //
	}

	private void onSearch() {
		quickQueryData(); //
	}

	//快速查询数据!
	private void quickQueryData() {
		clearTableData(); // 清空数据
		String str_text = textField.getText(); //
		this.str_lasteditingText = (str_text == null ? null : str_text.toLowerCase()); //
		String[] items = struct.getHeaderName();
		String[][] str_data = struct.getBodyData(); //
		int columnCount = items.length; //列数
		String str_field = (String) combox_condition.getSelectedItem(); //

		int li_colIndex = -1; //
		if (str_field != null && !str_field.trim().equals("")) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].equals(str_field)) {
					li_colIndex = i;
					break; //
				}
			}
		}

		DefaultTableModel tableModel = (DefaultTableModel) table.getModel(); //
		if (str_text == null || str_text.trim().equals("")) { //如果为空,则直接插入!!
			for (int i = 0; i < str_data.length; i++) {
				tableModel.addRow(str_data[i]); //加入
				table.setRowHeight(i, ((Integer) rowheight_map.get(i)));
			}
		} else {
			int new_row_index = 0;
			for (int i = 0; i < str_data.length; i++) { //遍历所有数据!!!
				if (li_colIndex >= 0) { //如果指定了位置
					if (str_data[i][li_colIndex] != null && str_data[i][li_colIndex].toLowerCase().indexOf(str_text.toLowerCase()) >= 0) { //如果这一列品配上!
						tableModel.addRow(str_data[i]); //加入
						table.setRowHeight(new_row_index, ((Integer) rowheight_map.get(i)));
						new_row_index++;
					}
				} else {
					for (int j = 0; j < columnCount; j++) { //遍历各列
						if (items[j].endsWith("$")) {//不显示的列不进行匹配
							continue;
						}
						if (str_data[i][j] != null && str_data[i][j].toLowerCase().indexOf(str_text.toLowerCase()) >= 0) { //如果这一列品配上!
							tableModel.addRow(str_data[i]); //加入
							table.setRowHeight(new_row_index, ((Integer) rowheight_map.get(i)));
							new_row_index++;
							break; //中断里面的循环!!
						}
					}
				}
			}
		}

		label_count.setText("共有记录[" + table.getRowCount() + "]条"); //
	}

	public RefItemVO getSelectVO() {
		if (table.getModel().getColumnCount() < 3) {
			MessageBox.show(this, "表中列数必须至少有3列,现在少于3列,这是不允许的!"); //
			return null; //
		}

		int li_selectedRow = table.getSelectedRow(); //
		if (li_selectedRow < 0) {
			return null;
		}
		String str_ref_pk = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 0));
		String str_ref_code = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 1));
		String str_ref_name = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 2));

		HashVO returnDataHashVO = new HashVO(); //创建返回值的VO
		String[] str_allkeys = struct.getHeaderName(); //所有的列!!
		for (int i = 0; i < str_allkeys.length; i++) {
			returnDataHashVO.setAttributeValue(str_allkeys[i], this.table.getModel().getValueAt(li_selectedRow, i)); //
		}
		return new RefItemVO(str_ref_pk, str_ref_code, str_ref_name, returnDataHashVO); //
	}

	public int getAllWidth() {
		return allWidth;
	}

	public void itemStateChanged(ItemEvent e) {
		quickQueryData(); //快速查询数据
	}

	private void onShowSQL() {
		MessageBox.showTextArea(this, this.str_sql); //
	}

	class MyCellRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			//JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //
			WLTLabel label = new WLTLabel(value == null ? "" : value.toString());
			label.setOpaque(true); //不透明
			label.setHorizontalAlignment(SwingConstants.LEFT); //

			//设背景!!!
			if (isSelected) {
				label.setBackground(table.getSelectionBackground()); //
			} else {
				if (row % 2 == 0) {
					label.setBackground(Color.WHITE);
				} else {
					label.setBackground(new Color(245, 255, 255));
				}
			}

			//			//设字的颜色!!
			if (str_lasteditingText != null && !str_lasteditingText.trim().equals("")) {
				if (combox_condition.getSelectedIndex() == 0 || column == combox_condition.getSelectedIndex() - 1) { //如果是所有列都过滤,或者是过滤的这个列
					label.addStrItemColor(str_lasteditingText, Color.RED); //
				}
			}

			return label;
		}
	}

	class TableRowResizer extends MouseInputAdapter {
		public Cursor resizeCursor = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);

		private int mouseYOffset, resizingRow;
		private Cursor otherCursor = resizeCursor;
		private JTable tmptable;

		public TableRowResizer(JTable table) {
			this.tmptable = table;
			table.addMouseListener(this);
			table.addMouseMotionListener(this);
		}

		private int getResizingRow(Point p) {
			return getResizingRow(p, tmptable.rowAtPoint(p));
		}

		private int getResizingRow(Point p, int row) {
			if (row == -1) {
				return -1;
			}

			int col = tmptable.columnAtPoint(p);
			if (col == -1 || col > 0)
				return -1;
			Rectangle r = tmptable.getCellRect(row, col, true); // 表格中对应行列的范围区域
			r.grow(0, -3); // 将网格的范围减少3个像素，即网线上下3个像素内，光标就会变
			if (r.contains(p)) // 如果对应的行列范围包含鼠标的范围
				return -1;
			int midPoint = r.y + r.height / 2;
			int rowIndex = (p.y < midPoint) ? row - 1 : row;
			return rowIndex;
		}

		public void mousePressed(MouseEvent e) {
			Point p = e.getPoint();
			resizingRow = getResizingRow(p);
			mouseYOffset = p.y - tmptable.getRowHeight(resizingRow);
		}

		private void swapCursor() {
			Cursor tmp = tmptable.getCursor();
			tmptable.setCursor(otherCursor);
			otherCursor = tmp;
		}

		public void mouseMoved(MouseEvent e) {
			int li_row = getResizingRow(e.getPoint());
			if (li_row >= 0 != (tmptable.getCursor() == resizeCursor)) {
				swapCursor();
			}
		}

		public void mouseDragged(MouseEvent e) {
			int mouseY = e.getY();
			if (resizingRow >= 0) {
				int newHeight = mouseY - mouseYOffset;
				if (newHeight > 0) {
					if (e.isControlDown()) {
						int li_rowcount = tmptable.getRowCount();
						for (int i = 0; i < li_rowcount; i++) {
							tmptable.setRowHeight(i, newHeight); // 设置新的行高..
							table.setRowHeight(i, newHeight); // 设置主表中的行高
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // 设置新的行高..
						table.setRowHeight(resizingRow, newHeight); // 设置主表中的行高
					}
				}
			}
		}
	}

	class ResizableTable extends JTable {
		private static final long serialVersionUID = 1L;

		public ResizableTable(TableModel tableModel, DefaultTableColumnModel rowHeaderColumnModel) {
			super(tableModel, rowHeaderColumnModel); //
			new TableRowResizer(this); // 
		}

		public void changeSelection(int row, int column, boolean toggle, boolean extend) {
			if (getCursor().getType() == Cursor.N_RESIZE_CURSOR)
				return;
			super.changeSelection(row, column, toggle, extend);
		}
	}
}
