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
 * ���Ϳؼ����ݿ��ٹ��˵ĶԻ���
 * @author xch
 *
 */
public class QuickFilterDialog extends BillDialog implements ActionListener, KeyListener, ItemListener {

	private String[][] str_data = null; //
	private int maxLevel = 0; //������
	private JTextField textFieldCondition = null; //
	private JComboBox comboBox_level = null; //
	private JLabel label_records = null; //

	private JTable filterTable = null; //
	protected WLTButton btn_confirm, btn_cancel;
	private String str_LastEditingText = "";

	private int[] selectedRows = null; //
	private int closeType = -1; //
	private TBUtil tbUtil = new TBUtil();//
	private BillTreePanel treePanel = null;//������塾���/2016-02-19��
	boolean showToString = false;//����������в����Ƿ���ʾToString���С����/2016-02-19��

	public QuickFilterDialog(java.awt.Container _parent, String[][] _data, int _maxLevel) {
		this(_parent, _data, _maxLevel, false);
	}

	public QuickFilterDialog(java.awt.Container _parent, String[][] _data, int _maxLevel, boolean _showToString) {
		super(_parent, "����/��λ", 660, 400); //
		if (_parent instanceof BillTreePanel) {//�����/2016-02-19��
			treePanel = (BillTreePanel) _parent;
			this.showToString = _showToString;//ֻ�е�һ��������BillTreePanel���ſɻ������ģ���ToString����
		}
		this.str_data = _data; //ʵ������
		this.maxLevel = _maxLevel; //������
		initialize();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initialize() {
		this.getContentPane().add(getNorthPanel(), BorderLayout.NORTH);
		this.getContentPane().add(getMainPanel(), BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		label_records.setText("����" + filterTable.getRowCount() + "������¼"); //
	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel("�����(�ؼ���)", SwingConstants.RIGHT); //
		label.setForeground(Color.BLUE); //
		label.setPreferredSize(new Dimension(95, 20)); //

		textFieldCondition = new JTextField(); //
		textFieldCondition.setPreferredSize(new Dimension(175, 20)); //

		comboBox_level = new JComboBox(); //
		comboBox_level.setPreferredSize(new Dimension(80, 20)); //
		comboBox_level.addItem(""); //
		for (int i = 1; i <= maxLevel; i++) {
			comboBox_level.addItem("��" + i + "��"); //
		}
		comboBox_level.addItem("Ŀ¼���"); //
		comboBox_level.addItem("Ҷ�ӽ��"); //

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
		if (showToString) {//����������в����Ƿ���ʾToString���С����/2016-02-19��
			String toStringKey = treePanel.getTempletVO().getTostringkey();
			Pub_Templet_1_ItemVO itemvo = treePanel.getTempletVO().getItemVo(toStringKey);
			String toStringName = toStringKey;
			if (itemvo != null) {
				toStringName = itemvo.getItemname();
			}
			filterTable = new JTable(new DefaultTableModel(this.str_data, new String[] { "���", "�����", toStringName, "·��", "���", "Ҷ�ӽ��?" }) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			}); //
		} else {
			filterTable = new JTable(new DefaultTableModel(this.str_data, new String[] { "���", "�����", "·��", "���", "Ҷ�ӽ��?" }) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			}); //
		}

		filterTable.getTableHeader().setDefaultRenderer(new MyTableHeaderCellRender()); //
		filterTable.getTableHeader().setReorderingAllowed(false); //�ı���˳��
		filterTable.setDefaultRenderer(Object.class, new MyTableRenderer()); // Ĭ�ϻ�����
		filterTable.getColumnModel().getColumn(0).setPreferredWidth(35); //
		filterTable.getColumnModel().getColumn(1).setPreferredWidth(105); //
		if (showToString) {//����в�ѯʱ����ʾToString���С����/2016-02-19��
			filterTable.getColumnModel().getColumn(2).setPreferredWidth(65); //
			filterTable.getColumnModel().getColumn(3).setPreferredWidth(315); //
			filterTable.getColumnModel().getColumn(4).setPreferredWidth(35); //
			filterTable.getColumnModel().getColumn(5).setPreferredWidth(65); //
		} else {
			filterTable.getColumnModel().getColumn(2).setPreferredWidth(315); //
			filterTable.getColumnModel().getColumn(3).setPreferredWidth(35); //
			filterTable.getColumnModel().getColumn(4).setPreferredWidth(65); //
		}

		filterTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //���Զ��仯���

		JScrollPane mainScrollPanel = new JScrollPane(filterTable);
		return mainScrollPanel; //
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel(new FlowLayout());
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
					if (li_level == li_itemCount - 1) { //�������ĩ���!
						if (showToString) {
							if (!str_data[i][5].equals("��")) { //�������ĩ���
								continue;
							}
						} else {
							if (!str_data[i][4].equals("��")) { //�������ĩ���
								continue;
							}
						}
					} else if (li_level == li_itemCount - 2) {
						if (showToString) {
							if (str_data[i][5].equals("��")) { //�����ĩ���
								continue;
							}
						} else {
							if (str_data[i][4].equals("��")) { //�����ĩ���
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
					if (li_level == li_itemCount - 1) { //�������ĩ���!
						if (showToString) {
							if (!str_data[i][5].equals("��")) { //�������ĩ���
								continue;
							}
						} else {
							if (!str_data[i][4].equals("��")) { //�������ĩ���
								continue;
							}
						}
					} else if (li_level == li_itemCount - 2) {
						if (showToString) {
							if (str_data[i][5].equals("��")) { //�����ĩ���
								continue;
							}
						} else {
							if (str_data[i][4].equals("��")) { //�����ĩ���
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

				boolean bo_match = false; //������ж���ǰ�жϷ��ˣ����޸�֮�����/2016-02-19��
				for (int j = 0; j < str_items.length; j++) { //
					if (str_data[i][1].toLowerCase().indexOf(str_items[j].trim().toLowerCase()) > -1) { //���Դ�Сд
						bo_match = true;
						break;
					}
					if (showToString && str_data[i][2].toLowerCase().indexOf(str_items[j].trim().toLowerCase()) > -1) {//����toString����
						bo_match = true;
						break;
					}
				}
				if (bo_match) {
					((DefaultTableModel) filterTable.getModel()).addRow(str_data[i]); //
				}

			}
		}
		label_records.setText("����" + filterTable.getRowCount() + "������¼"); //
	}

	class MyTableRenderer extends DefaultTableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label2 = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (column == 1 || (QuickFilterDialog.this.showToString && column == 2)) {//�����/2016-02-19��
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
						label.addStrItemColor(str_items[i].toLowerCase(), Color.RED); //�������ó�Сд����Ȼ�鲻����
					}
				}
				return label; //
			}
			return label2; //
		}
	}

	/**
	 * ��ͷ�Ļ�����.
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
