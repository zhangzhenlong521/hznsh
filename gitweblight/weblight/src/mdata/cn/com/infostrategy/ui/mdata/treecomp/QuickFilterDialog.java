package cn.com.infostrategy.ui.mdata.treecomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTLabel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * 树型控件数据快速过滤的对话框
 * @author xch
 *
 */
public class QuickFilterDialog extends BillDialog implements ActionListener, KeyListener, ItemListener {

	private String[][] str_data = null; //
	private int maxLevel = 0; //最大层数
	private JTextField textFieldCondition = null; //
	private JComboBox comboBox_level = null; //
	private JLabel label_records = null; //

	private JTable filterTable = null; //
	protected WLTButton btn_confirm, btn_cancel;
	private String str_LastEditingText = "";

	private int[] selectedRows = null; //
	private int closeType = -1; //
	private TBUtil tbUtil = new TBUtil();//
	private BillTreePanel treePanel = null;//树型面板【李春娟/2016-02-19】
	boolean showToString = false;//树型面板结果中查找是否显示ToString的列【李春娟/2016-02-19】

	public QuickFilterDialog(java.awt.Container _parent, String[][] _data, int _maxLevel) {
		this(_parent, _data, _maxLevel, false);
	}

	public QuickFilterDialog(java.awt.Container _parent, String[][] _data, int _maxLevel, boolean _showToString) {
		super(_parent, "过滤/定位", 660, 400); //
		if (_parent instanceof BillTreePanel) {//【李春娟/2016-02-19】
			treePanel = (BillTreePanel) _parent;
			this.showToString = _showToString;//只有第一个参数是BillTreePanel，才可获得树型模板的ToString的列
		}
		this.str_data = _data; //实际数据
		this.maxLevel = _maxLevel; //最大层数
		initialize();
	}

	/**
	 * 初始化页面
	 */
	private void initialize() {
		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH);
		this.getContentPane().add(getMainPanel(), BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		label_records.setText("共【" + filterTable.getRowCount() + "】条记录"); //
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("结点名(关键字)", SwingConstants.RIGHT); //
		label.setForeground(Color.BLUE); //
		label.setPreferredSize(new Dimension(95, 20)); //

		textFieldCondition = new JTextField(); //
		textFieldCondition.setPreferredSize(new Dimension(175, 20)); //

		comboBox_level = new JComboBox(); //
		comboBox_level.setPreferredSize(new Dimension(80, 20)); //
		comboBox_level.addItem(""); //
		for (int i = 1; i <= maxLevel; i++) {
			comboBox_level.addItem("第" + i + "层"); //
		}
		comboBox_level.addItem("目录结点"); //
		comboBox_level.addItem("叶子结点"); //

		label_records = new JLabel("");
		label_records.setPreferredSize(new Dimension(125, 20));

		panel.add(label);
		panel.add(textFieldCondition);
		panel.add(comboBox_level);
		panel.add(label_records);

		textFieldCondition.addKeyListener(this); //
		comboBox_level.addItemListener(this); //
		return panel;
	}

	private JScrollPane getMainPanel() {
		if (showToString) {//树型面板结果中查找是否显示ToString的列【李春娟/2016-02-19】
			String toStringKey = treePanel.getTempletVO().getTostringkey();
			Pub_Templet_1_ItemVO itemvo = treePanel.getTempletVO().getItemVo(toStringKey);
			String toStringName = toStringKey;
			if (itemvo != null) {
				toStringName = itemvo.getItemname();
			}
			filterTable = new JTable(new DefaultTableModel(this.str_data, new String[] { "序号", "结点名", toStringName, "路径", "层次", "叶子结点?" }) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			}); //
		} else {
			filterTable = new JTable(new DefaultTableModel(this.str_data, new String[] { "序号", "结点名", "路径", "层次", "叶子结点?" }) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			}); //
		}

		filterTable.getTableHeader().setDefaultRenderer(new MyTableHeaderCellRender()); //
		filterTable.getTableHeader().setReorderingAllowed(false); //改变列顺序
		filterTable.setDefaultRenderer(Object.class, new MyTableRenderer()); // 默认绘制器
		filterTable.getColumnModel().getColumn(0).setPreferredWidth(35); //
		filterTable.getColumnModel().getColumn(1).setPreferredWidth(105); //
		if (showToString) {//结果中查询时是显示ToString的列【李春娟/2016-02-19】
			filterTable.getColumnModel().getColumn(2).setPreferredWidth(65); //
			filterTable.getColumnModel().getColumn(3).setPreferredWidth(315); //
			filterTable.getColumnModel().getColumn(4).setPreferredWidth(35); //
			filterTable.getColumnModel().getColumn(5).setPreferredWidth(65); //
		} else {
			filterTable.getColumnModel().getColumn(2).setPreferredWidth(315); //
			filterTable.getColumnModel().getColumn(3).setPreferredWidth(35); //
			filterTable.getColumnModel().getColumn(4).setPreferredWidth(65); //
		}

		filterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //不自动变化宽度

		JScrollPane mainScrollPanel = new JScrollPane(filterTable);
		return mainScrollPanel; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
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

	private void onConfirm() {
		int[] li_rows = filterTable.getSelectedRows();
		if (li_rows.length <= 0) {
			MessageBox.showSelectOne(this); //
			return;
		}
		selectedRows = new int[li_rows.length]; //
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
		editTextChanged(true);
	}

	public void keyTyped(KeyEvent e) {
	}

	public void itemStateChanged(ItemEvent e) {
		editTextChanged(false);
	}

	private void editTextChanged(boolean _detectEdit) {
		if (_detectEdit) {
			if (str_LastEditingText.equals(textFieldCondition.getText())) {
				return;
			}
		}
		str_LastEditingText = textFieldCondition.getText(); //
		filterTable.clearSelection(); //
		for (int i = filterTable.getModel().getRowCount() - 1; i >= 0; i--) {
			((DefaultTableModel) filterTable.getModel()).removeRow(i);
		}
		if (str_LastEditingText.trim().equals("")) {
			int li_level = comboBox_level.getSelectedIndex();
			int li_itemCount = comboBox_level.getItemCount(); //
			for (int i = 0; i < str_data.length; i++) {
				if (li_level > 0) {
					if (li_level == li_itemCount - 1) { //如果是找末结点!
						if (showToString) {
							if (!str_data[i][5].equals("是")) { //如果不是末结点
								continue;
							}
						} else {
							if (!str_data[i][4].equals("是")) { //如果不是末结点
								continue;
							}
						}
					} else if (li_level == li_itemCount - 2) {
						if (showToString) {
							if (str_data[i][5].equals("是")) { //如果是末结点
								continue;
							}
						} else {
							if (str_data[i][4].equals("是")) { //如果是末结点
								continue;
							}
						}
					} else {
						if (showToString) {
							if (!str_data[i][4].equals("" + li_level)) {
								continue;
							}
						} else {
							if (!str_data[i][3].equals("" + li_level)) {
								continue;
							}
						}
					}
				}

				((DefaultTableModel) filterTable.getModel()).addRow(str_data[i]); //	
			}
		} else {
			String[] str_items = tbUtil.split(str_LastEditingText.trim(), " ");
			int li_level = comboBox_level.getSelectedIndex();
			int li_itemCount = comboBox_level.getItemCount(); //
			for (int i = 0; i < str_data.length; i++) {
				if (li_level > 0) {
					if (li_level == li_itemCount - 1) { //如果是找末结点!
						if (showToString) {
							if (!str_data[i][5].equals("是")) { //如果不是末结点
								continue;
							}
						} else {
							if (!str_data[i][4].equals("是")) { //如果不是末结点
								continue;
							}
						}
					} else if (li_level == li_itemCount - 2) {
						if (showToString) {
							if (str_data[i][5].equals("是")) { //如果是末结点
								continue;
							}
						} else {
							if (str_data[i][4].equals("是")) { //如果是末结点
								continue;
							}
						}
					} else {
						if (showToString) {
							if (!str_data[i][4].equals("" + li_level)) {
								continue;
							}
						} else {
							if (!str_data[i][3].equals("" + li_level)) {
								continue;
							}
						}
					}
				}

				boolean bo_match = false; //这里的判断以前判断反了，故修改之【李春娟/2016-02-19】
				for (int j = 0; j < str_items.length; j++) { //
					if (str_data[i][1].toLowerCase().indexOf(str_items[j].trim().toLowerCase()) > -1) { //忽略大小写
						bo_match = true;
						break;
					}
					if (showToString && str_data[i][2].toLowerCase().indexOf(str_items[j].trim().toLowerCase()) > -1) {//新增toString的列
						bo_match = true;
						break;
					}
				}
				if (bo_match) {
					((DefaultTableModel) filterTable.getModel()).addRow(str_data[i]); //
				}

			}
		}
		label_records.setText("共【" + filterTable.getRowCount() + "】条记录"); //
	}

	class MyTableRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label2 = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 1 || (QuickFilterDialog.this.showToString && column == 2)) {//【李春娟/2016-02-19】
				WLTLabel label = new WLTLabel(label2.getText());
				label.setOpaque(true); //
				label.setHorizontalAlignment(SwingConstants.LEFT); //
				if (isSelected) {
					label.setBackground(table.getSelectionBackground()); //
				} else {
					label.setBackground(Color.WHITE); //
				}
				HashMap colorMap = new HashMap(); //
				if (!str_LastEditingText.trim().equals("")) {
					String[] str_items = tbUtil.split(str_LastEditingText.trim(), " ");
					for (int i = 0; i < str_items.length; i++) {
						label.addStrItemColor(str_items[i].toLowerCase(), Color.RED); //必须设置成小写！不然查不到！
					}
				}
				return label; //
			}
			return label2; //
		}
	}

	/**
	 * 表头的绘制器.
	 * @author xch
	 *
	 */
	class MyTableHeaderCellRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;
		private Color bgcolor = new Color(240, 240, 240);

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setOpaque(true); //
			label.setHorizontalAlignment(SwingConstants.CENTER); //
			label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY)); //
			label.setBackground(bgcolor); //

			if (column == 1) {
				label.setForeground(Color.BLUE);
			} else {
				label.setForeground(Color.BLACK);
			}
			return label; //
		}
	}

}
