/**************************************************************************
 * $RCSfile: QuickSearchDialog.java,v $  $Revision: 1.12 $  $Date: 2012/11/06 07:48:32 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

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

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.common.WLTPanel;

/**
 * ���ٹ����붨λ�ĶԻ���
 * @author xch
 *
 */
public class QuickSearchDialog extends BillDialog implements ActionListener, KeyListener, ItemListener {

	private static final long serialVersionUID = 1L;
	private static final int DEFAULT_ROW_HEIGHT = 19;//Ĭ���и�
	private int li_colIndex = 0; //
	private String str_name = null; //
	private String[] str_colNames = null;
	private int[] li_colWidths = null;
	private int[] li_rowHeights = null;
	private String[][] str_datas = null;

	private JTextField textFieldCondition = null; //
	private JComboBox comBoBox_field, comBoBox_value = null;
	private JLabel label_allrecords = null; //

	private JTable filterTable = null; //
	private JPanel mainPanel = null; //
	private JScrollPane mainScrollPanel = null; // 
	protected WLTButton btn_confirm, btn_cancel;

	private int[] selectedRows = null; //
	private int closeType = -1; //

	private String str_LastEditingText = ""; // 
	private TBUtil tbUtil = new TBUtil();//
	private String str_helpinfo = "����ʾ:�ò���ֻ�ڵ�ǰҳ�����в���,�����Ҫ��������,ֻ������ÿҳ��ʾ��¼����Χ���ɡ�"; //

	public QuickSearchDialog(java.awt.Container _parent, int _colIndex, String _name, String[] _colNames, int[] _colWidths, String[][] _data) {
		super(_parent, "����/��λ");
		this.li_colIndex = _colIndex; //�е�λ��
		this.str_name = _name; //
		this.str_colNames = getNewColumnName(_colNames); //��������

		//����ʵ�����ݼ����п�
		TBUtil tbUtil = new TBUtil(); //
		int[] li_header_width = new int[_colNames.length]; //
		for (int i = 0; i < li_header_width.length; i++) {
			li_header_width[i] = tbUtil.getStrUnicodeLength(_colNames[i]) * 6 + 10; //
		}
		for (int i = 0; i < _colWidths.length; i++) {
			int li_width = 50; //
			for (int r = 0; r < _data.length; r++) {
				String str_item = _data[r][i]; //
				if (str_item != null) {
					int li_tmpwidth = tbUtil.getStrUnicodeLength(str_item) * 6 + 10; //
					if (li_tmpwidth > li_width) {
						li_width = li_tmpwidth;
					}
				}
			}
			if (li_width < li_header_width[i]) { //���С�ڱ�ͷ���
				li_width = li_header_width[i]; //
			}

			if (li_width < 50) {
				li_width = 50;
			}

			if (li_width > 225) {
				li_width = 225;
			}
			_colWidths[i] = li_width; //
		}

		//==========�����и�
		li_rowHeights = new int[_data.length];
		for (int i = 0; i < _data.length; i++) {
			li_rowHeights[i] = QuickSearchDialog.DEFAULT_ROW_HEIGHT;
			int max_row_colspan = 1;//�����е������ռ�˼���
			for (int r = 0; r < _colWidths.length; r++) {
				int curr_row_colspan = 1;
				String str_item = _data[i][r]; //
				if (str_item != null) {
					int li_tmpwidth = tbUtil.getStrUnicodeLength(str_item) * 6 + 10; //
					int curr_col_row_height = 0;
					if (li_tmpwidth > _colWidths[r]) {
						if (li_tmpwidth % _colWidths[r] == 0)
							curr_row_colspan = li_tmpwidth / _colWidths[r];
						else
							curr_row_colspan = li_tmpwidth / _colWidths[r] + 1;
					}
				}
				if (curr_row_colspan != 1) {
					if (curr_row_colspan > max_row_colspan)
						max_row_colspan = curr_row_colspan;
				}
			}
			if (max_row_colspan != 1) {
				int li_itemHeight = max_row_colspan * 15;
				li_rowHeights[i] = (li_itemHeight > 50 ? 50 : li_itemHeight); //
			}
		}//===============�����и߽���

		this.li_colWidths = getNewColumnWidths(_colWidths); //�����п�
		this.str_datas = getNewData(_data); //

		int li_allWidth = 30;
		for (int i = 0; i < li_colWidths.length; i++) {
			li_allWidth = li_allWidth + li_colWidths[i];
		}

		if (li_allWidth <= 600) {
			li_allWidth = 600;
		}
		if (li_allWidth >= 900) {
			li_allWidth = 900;
		}

		int li_allHeight = 200;
		if (_data != null) {
			li_allHeight = 150 + _data.length * 20; //�ܸ�
		}

		if (li_allHeight < 300) {
			li_allHeight = 300;
		}

		if (li_allHeight >= 600) {
			li_allHeight = 600;
		}

		this.setSize(li_allWidth, li_allHeight); //
		this.locationToCenterPosition(); //�����м�λ��
		initialize(); //
	}

	/**
	 * ��ʼ��
	 */
	private void initialize() {
		JPanel contentPanel = WLTPanel.createDefaultPanel(new BorderLayout()); //
		contentPanel.add(getNorthPanel(), BorderLayout.NORTH); //
		contentPanel.add(getMainPanel(), BorderLayout.CENTER); //
		contentPanel.add(getSouthPanel(), BorderLayout.SOUTH); //
		this.getContentPane().add(contentPanel, BorderLayout.CENTER); //
	}

	/**
	 * �Ϸ��İ�ť..
	 * @return
	 */
	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panel.setOpaque(false); //͸��!!!

		comBoBox_field = new JComboBox();
		comBoBox_field.setFocusable(false); //
		comBoBox_field.addItem("������"); //
		for (int i = 1; i < this.str_colNames.length; i++) {
			comBoBox_field.addItem(str_colNames[i]); //
		}

		JLabel label_contain = new JLabel("����", JLabel.CENTER); //

		comBoBox_value = new JComboBox(); //
		comBoBox_value.addItem(""); //
		if (str_datas != null && str_datas.length > 0) {
			VectorMap vm_distinct = new VectorMap(); //
			for (int i = 0; i < str_datas.length; i++) {
				vm_distinct.put(str_datas[i][li_colIndex + 1], null); //
			}
			String[] str_keys = vm_distinct.getKeysAsString(); //
			for (int i = 0; i < str_keys.length; i++) {
				comBoBox_value.addItem(str_keys[i]); //	
			}
		}
		comBoBox_value.setEditable(true); //

		textFieldCondition = ((JTextField) ((JComponent) comBoBox_value.getEditor().getEditorComponent())); //
		textFieldCondition.setEditable(true);
		comBoBox_field.setPreferredSize(new Dimension(100, 20));
		label_contain.setPreferredSize(new Dimension(30, 20)); //
		comBoBox_value.setPreferredSize(new Dimension(200, 20));

		panel.add(comBoBox_field);
		panel.add(label_contain);
		panel.add(comBoBox_value);

		textFieldCondition.addKeyListener(this);
		comBoBox_field.addItemListener(this); //
		comBoBox_value.addItemListener(this);//
		return panel; //
	}

	/**
	 * �����
	 * @return
	 */
	private JPanel getMainPanel() {
		filterTable = new ResizableTable(new DefaultTableModel(this.str_datas, this.str_colNames) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		}); //

		filterTable.getTableHeader().setDefaultRenderer(new MyTableHeaderCellRender()); //
		filterTable.getTableHeader().setReorderingAllowed(false); //�ı���˳��
		filterTable.setDefaultRenderer(Object.class, new MyTableRenderer()); // Ĭ�ϻ�����
		for (int i = 0; i < li_colWidths.length; i++) {
			filterTable.getColumnModel().getColumn(i).setPreferredWidth(li_colWidths[i]); //
		}

		for (int i = 0; i < li_rowHeights.length; i++) {
			filterTable.setRowHeight(i, li_rowHeights[i]);
		}
		filterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //���Զ��仯���
		filterTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && (e.getButton() == MouseEvent.BUTTON1)) { //˫�������ʾѡ�в�����
					onConfirm(); //��ͬ�ڵ��ȷ����ť
				}
			}

		});
		filterTable.setAutoscrolls(true);
		filterTable.setOpaque(false); //
		filterTable.getTableHeader().setOpaque(false);

		mainPanel = new JPanel(new BorderLayout());
		mainScrollPanel = new JScrollPane(filterTable);
		mainPanel.setOpaque(false); //͸��!!
		mainScrollPanel.setOpaque(false); //
		mainScrollPanel.getViewport().setOpaque(false); //

		//һ��Ҫ����������,�������ͷ�ұ߻���һ������!!!

		JViewport jv2 = new JViewport();
		jv2.setOpaque(false); //һ��Ҫ��һ�£������ϱ����и�����
		jv2.setView(filterTable.getTableHeader());
		mainScrollPanel.setColumnHeader(jv2); //

		mainPanel.add(mainScrollPanel); //

		label_allrecords = new JLabel("��[" + filterTable.getRowCount() + "]����¼" + str_helpinfo); //
		label_allrecords.setForeground(new Color(0, 128, 192)); //
		mainPanel.add(label_allrecords, BorderLayout.SOUTH); //
		return mainPanel; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout()); //
		panel.setOpaque(false); //
		btn_confirm = new WLTButton("ȷ��");
		btn_cancel = new WLTButton("ȡ��");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_confirm) {
			onConfirm(); //
		} else if (e.getSource() == btn_cancel) {
			onCancel(); //
		}

	}

	/**
	 * �µ�����
	 * @param _colNames
	 * @return
	 */
	private String[] getNewColumnName(String[] _colNames) {
		String[] str_newColNames = new String[_colNames.length + 1];
		str_newColNames[0] = "���";
		System.arraycopy(_colNames, 0, str_newColNames, 1, _colNames.length);
		return str_newColNames;
	}

	/**
	 * �µ��п�
	 * @param _colWidths
	 * @return
	 */
	private int[] getNewColumnWidths(int[] _colWidths) {

		int[] str_newColWidths = new int[_colWidths.length + 1];
		str_newColWidths[0] = 30;
		System.arraycopy(_colWidths, 0, str_newColWidths, 1, _colWidths.length);
		return str_newColWidths;
	}

	/**
	 * �µ�����
	 * @param _colWidths
	 * @return
	 */
	private String[][] getNewData(String[][] _datas) {
		if (_datas == null || _datas.length == 0) {
			return null;
		}
		String[][] str_newDatas = new String[_datas.length][_datas[0].length + 1];
		for (int i = 0; i < _datas.length; i++) {
			str_newDatas[i][0] = "" + (i + 1);
			System.arraycopy(_datas[i], 0, str_newDatas[i], 1, _datas[i].length);
		}
		return str_newDatas;
	}

	private void onConfirm() {
		int[] li_rows = filterTable.getSelectedRows(); //
		if (li_rows.length <= 0) {
			MessageBox.showSelectOne(this); //
			return;
		}
		this.selectedRows = new int[li_rows.length];
		for (int i = 0; i < li_rows.length; i++) {
			selectedRows[i] = Integer.parseInt("" + filterTable.getValueAt(li_rows[i], 0)) - 1; //
		}
		closeType = 1;
		this.dispose();
	}

	private void onCancel() {
		closeType = 2;
		this.dispose();
	}

	public int getCloseType() {
		return closeType;
	}

	public int[] getSelectedRows() {
		return selectedRows;
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
		editTextChanged();
	}

	public void keyTyped(KeyEvent e) {

	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == comBoBox_field) {//��̬�ı�comBoBox_value��Item����
			int select_col_index = comBoBox_field.getSelectedIndex();
			comBoBox_value.removeAllItems();
			comBoBox_value.addItem(""); //
			VectorMap vm_distinct = new VectorMap(); //
			if (str_datas != null && str_datas.length > 0) {
				if (select_col_index == 0)//�����У�Ĭ�ϵ�һ��
					select_col_index = 1;
				for (int i = 0; i < str_datas.length; i++) {
					vm_distinct.put(str_datas[i][select_col_index], null); //
				}
			}
			String[] str_keys = vm_distinct.getKeysAsString(); //
			for (int i = 0; i < str_keys.length; i++) {
				comBoBox_value.addItem(str_keys[i]); //	
			}
		} else if (e.getSource() == comBoBox_value) {
			editTextChanged();
		}
	}

	/**
	 * ���������仯
	 */
	private void editTextChanged() {
		str_LastEditingText = textFieldCondition.getText(); //
		filterTable.clearSelection(); //
		DefaultTableModel tableModel = (DefaultTableModel) filterTable.getModel(); //
		for (int i = filterTable.getModel().getRowCount() - 1; i >= 0; i--) {
			tableModel.removeRow(i);
		}
		int new_row_num = 0;
		if(null!=str_datas){   //Ԭ���� 20130412�޸�  ��Ҫ������������ ���ֵΪ�ձ��������
			if (str_LastEditingText.trim().equals("")) { //���Ϊ��!
				for (int i = 0; i < str_datas.length; i++) {
					tableModel.addRow(str_datas[i]); //
					filterTable.setRowHeight(new_row_num++, li_rowHeights[i]);//=========����߶�
				}
			} else {
				String[] str_items = tbUtil.split(str_LastEditingText.trim(), " ");
					for (int i = 0; i < str_datas.length; i++) {
						boolean bo_match = false; //��Ĺ�ϵ
						for (int j = 0; j < str_items.length; j++) { //�ָ�������
							if (comBoBox_field.getSelectedIndex() == 0) { //�����������,��ֻҪ��һ�����Ǿ���!
								for (int k = 1; k < str_datas[i].length; k++) {
									if (str_datas[i][k] != null && str_datas[i][k].toLowerCase().indexOf(str_items[j].trim().toLowerCase()) >= 0) {
										bo_match = true; //
										break;
									}
								}
								if (bo_match) {
									break;
								}
							} else {
								if (str_datas[i][comBoBox_field.getSelectedIndex()].toLowerCase().indexOf(str_items[j].trim().toLowerCase()) >= 0) { //���Դ�Сд
									bo_match = true;
									break;
								}
							}
						}
						if (bo_match) {
							tableModel.addRow(str_datas[i]); //
							filterTable.setRowHeight(new_row_num++, li_rowHeights[i]);//=========����߶�
						}
				}
			}
		}

		label_allrecords.setText("��[" + filterTable.getRowCount() + "]����¼" + str_helpinfo); //
	}

	class MyTableRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label2 = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			WLTLabel label = new WLTLabel(label2.getText());
			label.setOpaque(true); //
			label.setHorizontalAlignment(SwingConstants.LEFT); //
			if (isSelected) {
				label.setBackground(table.getSelectionBackground()); //
			} else {
				label.setBackground(Color.WHITE); //
			}
			if (column != 0 && (comBoBox_field.getSelectedIndex() == 0 || column == comBoBox_field.getSelectedIndex())) { //������
				if (!str_LastEditingText.trim().equals("")) {
					String[] str_items = tbUtil.split(str_LastEditingText.trim(), " ");
					for (int i = 0; i < str_items.length; i++) {
						label.addStrItemColor(str_items[i].toLowerCase(), Color.RED); //
					}
				}
			}
			return label; //
		}
	}

	class MyTableHeaderCellRender extends DefaultTableCellRenderer {
		private Color bgcolor = new Color(240, 240, 240);

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setOpaque(true); //
			label.setHorizontalAlignment(SwingConstants.CENTER); //
			label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY)); //
			label.setBackground(bgcolor); //
			if (comBoBox_field.getSelectedIndex() != 0 && column == comBoBox_field.getSelectedIndex()) { //������
				label.setForeground(Color.BLUE);
			} else {
				label.setForeground(Color.BLACK);
			}
			return label; //
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
			if (col == -1 || col > 0) {
				return -1;
			}
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
							filterTable.setRowHeight(i, newHeight); // ���������е��и�
						}
					} else {
						tmptable.setRowHeight(resizingRow, newHeight); // �����µ��и�..
						filterTable.setRowHeight(resizingRow, newHeight); // ���������е��и�
					}
				}
			}
		}
	}

	class ResizableTable extends JTable {
		private static final long serialVersionUID = 1L;

		public ResizableTable(DefaultTableModel tableModel) {
			super(tableModel);
			new TableRowResizer(this); //
		}

		public void changeSelection(int row, int column, boolean toggle, boolean extend) {
			if (getCursor().getType() == Cursor.N_RESIZE_CURSOR)
				return;
			super.changeSelection(row, column, toggle, extend);
		}
	}
}