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
	private RefItemVO initRefItemVO = null; //��ʼ��������

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
			struct = UIUtil.getTableDataStructByDS(null, str_sql); // ����������
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		initContent(); //
	}

	/**
	 * ֱ�Ӵ���������!! �ڴӱ���ģ��ʱ��Ҫ��ѯ����ϵͳ��,������һ��SQL,����һ��Զ�̷���!!!
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
			mainPanel.setOpaque(false); //͸��!!
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getNorthPanel(), BorderLayout.NORTH);
			mainPanel.add(getMainPanel(), BorderLayout.CENTER);

			label_count.setForeground(Color.BLUE); //
			mainPanel.add(label_count, BorderLayout.SOUTH);

			this.setLayout(new BorderLayout());
			this.setOpaque(false); //͸��!!
			this.add(mainPanel);
			int li_scrolltorow = getInitRow();
			if (li_scrolltorow >= 0) {
				Rectangle rect = table.getCellRect(li_scrolltorow, 0, true);
				table.scrollRectToVisible(rect); //��������Ӧ���е�λ��!!!!!!!!!!!!
				table.setRowSelectionInterval(li_scrolltorow, li_scrolltorow); //��������Ӧ���е�λ��!!!!!!!!!!!!
			}
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					textField.requestFocus(); //
					textField.requestFocusInWindow(); //
				}
			});
			label_count.setText("���м�¼[" + table.getRowCount() + "]��"); //
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private JPanel getNorthPanel() {
		if (northpanel != null) {
			return northpanel;
		}

		northpanel = new JPanel();
		northpanel.setOpaque(false); //͸��!!!
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
		combox_condition.addItem("������"); //
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

		JButton btn_search = new WLTButton(UIUtil.getLanguage("��ѯ"));
		btn_search.setPreferredSize(new Dimension(60, 20));
		btn_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSearch();
			}
		});

		JLabel label1 = new JLabel("������", JLabel.RIGHT); //
		label1.setPreferredSize(new Dimension(50, 20)); //
		northpanel.add(label1); //
		northpanel.add(combox_condition);

		JLabel label2 = new JLabel("����", JLabel.CENTER); //
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
		for (int i = 0; i < struct.getHeaderName().length; i++) { //����ÿһ��
			if (!struct.getHeaderName()[i].endsWith("$")) { //�������Ԫ���Ž�β������ʾ!
				int li_colUIWidth = 150; //Ĭ�Ͽ��
				if (struct.getColValueMaxLen() != null) { //����ж���!!
					int li_colWidth = struct.getColValueMaxLen()[i]; //��̨�������!!!
					li_colUIWidth = (li_colWidth * 6) + 10; //
					if (li_colUIWidth < 50) { //̫С����ʾ50
						li_colUIWidth = 50;
					}
					if (li_colUIWidth > 175) { //̫���ֻ��ʾ175
						li_colUIWidth = 175;
					}
				}
				map_width.put("" + i, new Integer(li_colUIWidth)); //��¼�¸��еĿ��!
				li_allwidth = li_allwidth + li_colUIWidth; //��¼���ܵĿ��!
			}
		}

		if (li_allwidth < 400) { //���С��450,Ϊ������,�Զ������ȣ� ����������Ҫ,�Ӷ���������һ��!!!
			int li_addFix = (400 - li_allwidth) / map_width.size(); //
			String[] str_keys = (String[]) map_width.keySet().toArray(new String[0]); //
			for (int i = 0; i < str_keys.length; i++) {
				int li_oldwidth = (Integer) map_width.get(str_keys[i]); //
				map_width.put(str_keys[i], new Integer(li_oldwidth + li_addFix)); //���Ϻ�׺
			}
			allWidth = 400; //
		} else {
			allWidth = li_allwidth; //
		}
		int max_all_width = 550;
		if (allWidth < max_all_width) {//������δ�ﵽ�������мӿ���
			int col_all_width = 0;//�������ܹ������˶���
			HashMap add_width_map = new HashMap();//�����кţ�����ѹ���Ŀ��
			for (int i = 0; i < struct.getHeaderName().length; i++) { //����ÿһ��
				if (!struct.getHeaderName()[i].endsWith("$")) { //�������Ԫ���Ž�β������ʾ!
					int li_colUIWidth = 150; //Ĭ�Ͽ��
					if (struct.getColValueMaxLen() != null) { //����ж���!!
						int li_colWidth = struct.getColValueMaxLen()[i]; //��̨�������!!!
						li_colUIWidth = (li_colWidth * 6) + 10; //
						int orig_width = (Integer) map_width.get("" + i);
						if (li_colUIWidth > orig_width) {
							int add_width = li_colUIWidth - orig_width;//��ѹ���Ŀ��
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
					int real_add_width = AllAddWidth * add_width / col_all_width;//���ڿ�Ȳ������򰴱����ӿ�
					real_add_width = real_add_width > add_width ? add_width : real_add_width;
					int orig_width = (Integer) map_width.get(str_keyss[i]);
					map_width.put(str_keyss[i], orig_width + real_add_width);
					li_allwidth += real_add_width;
					allWidth += real_add_width;
				}
			}
		}//�ӿ�end
		//��������ȷ�����п�
		for (int i = 0; i < struct.getHeaderName().length; i++) { //����ÿһ��
			if (!struct.getHeaderName()[i].endsWith("$")) { //�������Ԫ���Ž�β������ʾ!
				TableColumn col = new TableColumn(i, (Integer) map_width.get("" + i)); //ȡ�ÿ��
				col.setCellRenderer(new MyCellRender()); //
				col.setHeaderValue(struct.getHeaderName()[i]);
				col.setIdentifier(struct.getHeaderName()[i]);
				colmodel.addColumn(col);
			}
		}
		table = new ResizableTable(model, colmodel);
		table.setRowHeight(19);
		//==��ʼ�Ӹߴ���(����ÿ�и���Ԫ�񣬿��Ƿ��л��е�)
		TBUtil tb = new TBUtil();
		String[][] data = struct.getBodyData();
		rowheight_map = new HashMap();
		for (int i = 0; i < data.length; i++) {
			rowheight_map.put(i, new Integer(19));//Ĭ���и�
			int max_row = 1;
			for (int j = 0; j < data[i].length; j++) {
				if (!struct.getHeaderName()[j].endsWith("$")) { //�������Ԫ���Ž�β������ʾ!
					int col_width = (Integer) map_width.get("" + j);
					int col_str_len = tb.getStrUnicodeLength(data[i][j]);
					int col_len = col_str_len * 6 + 6;
					if (col_len > col_width) {//��Ҫ����
						int row_num;//�˵�Ԫ����ռ����
						if (col_len % col_width != 0) {
							row_num = col_len / col_width + 1;
						} else {
							row_num = col_len / col_width;
						}
						if (row_num > max_row) {
							max_row = row_num;//����Ϊ�����
						}
					}
				}
			}
			if (max_row > 1) {
				rowheight_map.put(i, new Integer(max_row * 15));//�����и�
				table.setRowHeight(i, max_row * 15);
			}
		}
		table.setRowSelectionAllowed(true); //
		table.setColumnSelectionAllowed(false); //
		table.getTableHeader().setReorderingAllowed(false); //
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		table.setAutoscrolls(true);
		table.setOpaque(false); //͸��
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
		jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
		jv2.setView(table.getTableHeader());
		scrollPanel.setColumnHeader(jv2); //

		scrollPanel.setOpaque(false); //͸��!!
		scrollPanel.getViewport().setOpaque(false); //͸��!!
		return scrollPanel;
	}

	private void mouseDoubleClicked(MouseEvent e) {//sunfujun/20121208/�������
		if (table.getModel().getColumnCount() < 3) {
			MessageBox.show(this, "������������������3��,��������3��,���ǲ������!");
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
				if (temp_str.equals(this.initRefItemVO.getId())) { //��Ҫ����!!!
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

	//���ٲ�ѯ����!
	private void quickQueryData() {
		clearTableData(); // �������
		String str_text = textField.getText(); //
		this.str_lasteditingText = (str_text == null ? null : str_text.toLowerCase()); //
		String[] items = struct.getHeaderName();
		String[][] str_data = struct.getBodyData(); //
		int columnCount = items.length; //����
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
		if (str_text == null || str_text.trim().equals("")) { //���Ϊ��,��ֱ�Ӳ���!!
			for (int i = 0; i < str_data.length; i++) {
				tableModel.addRow(str_data[i]); //����
				table.setRowHeight(i, ((Integer) rowheight_map.get(i)));
			}
		} else {
			int new_row_index = 0;
			for (int i = 0; i < str_data.length; i++) { //������������!!!
				if (li_colIndex >= 0) { //���ָ����λ��
					if (str_data[i][li_colIndex] != null && str_data[i][li_colIndex].toLowerCase().indexOf(str_text.toLowerCase()) >= 0) { //�����һ��Ʒ����!
						tableModel.addRow(str_data[i]); //����
						table.setRowHeight(new_row_index, ((Integer) rowheight_map.get(i)));
						new_row_index++;
					}
				} else {
					for (int j = 0; j < columnCount; j++) { //��������
						if (items[j].endsWith("$")) {//����ʾ���в�����ƥ��
							continue;
						}
						if (str_data[i][j] != null && str_data[i][j].toLowerCase().indexOf(str_text.toLowerCase()) >= 0) { //�����һ��Ʒ����!
							tableModel.addRow(str_data[i]); //����
							table.setRowHeight(new_row_index, ((Integer) rowheight_map.get(i)));
							new_row_index++;
							break; //�ж������ѭ��!!
						}
					}
				}
			}
		}

		label_count.setText("���м�¼[" + table.getRowCount() + "]��"); //
	}

	public RefItemVO getSelectVO() {
		if (table.getModel().getColumnCount() < 3) {
			MessageBox.show(this, "������������������3��,��������3��,���ǲ������!"); //
			return null; //
		}

		int li_selectedRow = table.getSelectedRow(); //
		if (li_selectedRow < 0) {
			return null;
		}
		String str_ref_pk = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 0));
		String str_ref_code = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 1));
		String str_ref_name = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 2));

		HashVO returnDataHashVO = new HashVO(); //��������ֵ��VO
		String[] str_allkeys = struct.getHeaderName(); //���е���!!
		for (int i = 0; i < str_allkeys.length; i++) {
			returnDataHashVO.setAttributeValue(str_allkeys[i], this.table.getModel().getValueAt(li_selectedRow, i)); //
		}
		return new RefItemVO(str_ref_pk, str_ref_code, str_ref_name, returnDataHashVO); //
	}

	public int getAllWidth() {
		return allWidth;
	}

	public void itemStateChanged(ItemEvent e) {
		quickQueryData(); //���ٲ�ѯ����
	}

	private void onShowSQL() {
		MessageBox.showTextArea(this, this.str_sql); //
	}

	class MyCellRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			//JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //
			WLTLabel label = new WLTLabel(value == null ? "" : value.toString());
			label.setOpaque(true); //��͸��
			label.setHorizontalAlignment(SwingConstants.LEFT); //

			//�豳��!!!
			if (isSelected) {
				label.setBackground(table.getSelectionBackground()); //
			} else {
				if (row % 2 == 0) {
					label.setBackground(Color.WHITE);
				} else {
					label.setBackground(new Color(245, 255, 255));
				}
			}

			//			//���ֵ���ɫ!!
			if (str_lasteditingText != null && !str_lasteditingText.trim().equals("")) {
				if (combox_condition.getSelectedIndex() == 0 || column == combox_condition.getSelectedIndex() - 1) { //����������ж�����,�����ǹ��˵������
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
			Rectangle r = tmptable.getCellRect(row, col, true); // ����ж�Ӧ���еķ�Χ����
			r.grow(0, -3); // ������ķ�Χ����3�����أ�����������3�������ڣ����ͻ��
			if (r.contains(p)) // �����Ӧ�����з�Χ�������ķ�Χ
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
							tmptable.setRowHeight(i, newHeight); // �����µ��и�..
							table.setRowHeight(i, newHeight); // ���������е��и�
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // �����µ��и�..
						table.setRowHeight(resizingRow, newHeight); // ���������е��и�
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
