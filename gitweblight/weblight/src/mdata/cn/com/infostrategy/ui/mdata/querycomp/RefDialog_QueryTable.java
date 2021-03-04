/**************************************************************************
 * $RCSfile: RefDialog_QueryTable.java,v $  $Revision: 1.8 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.querycomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.CommUCDefineVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractRefDialog;

public class RefDialog_QueryTable extends AbstractRefDialog {
	private static final long serialVersionUID = 1L;

	private JPanel northpanel = null;
	private String str_sql = null;
	private String str_datasourcename = null;
	private TableDataStruct struct = null;
	private JComboBox combox_condition = null;
	private JComboBox combox_compare = null;
	private JCheckBox checkbox_iflike = null;
	private JTextField textField;
	private JTable table = null;
	private String str_ref_pk;
	private String str_ref_code;
	private String str_ref_name;

	private HashVO returnDataHashVO = null;
	private int li_opentype = -1;

	/**
	 * 第一种方式,通过一个SQL带入
	 * @param _parent
	 * @param _name
	 * @param _refinitvalue
	 * @param _dsname
	 * @param _sql
	 */
	public RefDialog_QueryTable(Container _parent, String _name, RefItemVO _refinitvalue, BillPanel _billPanel, CommUCDefineVO _definevo) {
		super(_parent, _name, _refinitvalue, _billPanel); //
		this.str_datasourcename = _definevo.getConfValue("数据源"); //数据源名称
		this.str_sql = getSQLFromConf(_definevo)[0]; //
		li_opentype = 1; //第一种打开方式,即直接构造
	}

	/**
	 * 第二种方式,直接送入数据!!
	 * @param _parent
	 * @param _name
	 * @param _refinitvalue
	 * @param _vos
	 */
	public RefDialog_QueryTable(Container _parent, String _name, RefItemVO _refinitvalue, BillPanel _billPanel, HashVO[] _vos) {
		super(_parent, _name, _refinitvalue, _billPanel); //
		li_opentype = 2; //第2种打开方式,从元原模板构造!!
	}

	/**
	 * 初始化页面
	 */
	public void initialize() {
		if (ClientEnvironment.isAdmin()) {
			this.setTitle(this.getTitle() + " 【字段名以$结尾则自动隐藏】"); //
		}
		if (li_opentype == 1) {
			init_1(); //第一种打开方式!!
		} else if (li_opentype == 2) {
			init_2(); //第二种打开方式!!
		}
	}

	/**
	 * 第一种打开方式
	 */
	private void init_1() {
		try {
			struct = UIUtil.getTableDataStructByDS(this.str_datasourcename, str_sql); // 真正的数据
			this.getContentPane().setLayout(new BorderLayout());
			JPanel mainPanel = new JPanel();
			mainPanel.setLayout(new BorderLayout());
			mainPanel.add(getNorthPanel(), BorderLayout.NORTH);
			mainPanel.add(getMainPanel(), BorderLayout.CENTER);
			mainPanel.add(getSouthPanel(), BorderLayout.SOUTH);
			this.getContentPane().add(mainPanel);
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 第二种打开方式
	 */
	private void init_2() {

	}

	/**
	 * 找出以SQL语句为前辍
	 * @param _definevo
	 * @return
	 */
	private String[] getSQLFromConf(CommUCDefineVO _definevo) {
		String[] str_keys = _definevo.getAllConfKeys("SQL语句", true); //以SQL语句开关的!!
		String[] str_values = new String[str_keys.length]; //
		for (int i = 0; i < str_keys.length; i++) {
			str_values[i] = _definevo.getConfValue(str_keys[i]); // 
		}
		return str_values; //
	}

	/**
	 * 初始宽度
	 * @return
	 */
	public int getInitWidth() {
		return 500;
	}

	public int getInitHeight() {
		return 400;
	}

	private JPanel getNorthPanel() {
		if (northpanel != null) {
			return northpanel;
		}

		northpanel = new JPanel();
		northpanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		northpanel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					onShowSQL();
				}
			}

		});

		combox_condition = new JComboBox();
		combox_condition.setPreferredSize(new Dimension(150, 20));
		String[] items = struct.getHeaderName();
		for (int i = 0; i < items.length; i++) {
			if (!items[i].endsWith(WLTConstants.STRING_REFPANEL_UNSHOWSIGN))
				combox_condition.addItem(items[i]);
		}

		combox_compare = new JComboBox();
		combox_compare.setPreferredSize(new Dimension(70, 20));
		combox_compare.addItem("like");
		combox_compare.addItem("=");
		combox_compare.addItem(">");
		combox_compare.addItem(">=");
		combox_compare.addItem("<");
		combox_compare.addItem("<=");

		textField = new JTextField();
		textField.setPreferredSize(new Dimension(125, 20));

		textField.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
			}

			public void keyReleased(KeyEvent e) {
				onKeyReleased();
			}

			public void keyTyped(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					onClose();
				}
			}
		});

		checkbox_iflike = new JCheckBox(); //
		checkbox_iflike.setToolTipText("是否模糊匹配");
		checkbox_iflike.setPreferredSize(new Dimension(20, 20));
		checkbox_iflike.setBackground(java.awt.Color.WHITE); //

		JButton btn_search = new WLTButton(UIUtil.getLanguage("查询"));
		btn_search.setPreferredSize(new Dimension(60, 20));
		btn_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onSearch();
			}
		});

		northpanel.add(combox_condition);
		northpanel.add(combox_compare);
		northpanel.add(textField);
		northpanel.add(checkbox_iflike);
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

		for (int i = 0; i < struct.getHeaderName().length; i++) {
			if (!struct.getHeaderName()[i].endsWith(WLTConstants.STRING_REFPANEL_UNSHOWSIGN)) {
				TableColumn col = new TableColumn(i, 150); //

				col.setCellRenderer(new MyCellRender()); //
				col.setHeaderValue(struct.getHeaderName()[i]);
				col.setIdentifier(struct.getHeaderName()[i]);
				colmodel.addColumn(col);
			}
		}
		table = new JTable(model, colmodel);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		table.getTableHeader().setReorderingAllowed(false);
		table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		table.setAutoscrolls(true);
		table.setRowHeight(18);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					onConfirm(); //
				}
			}
		});

		JButton btn_showsql = new JButton(UIUtil.getImage("scrollpanel_corn.gif"));
		btn_showsql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onShowSQL();
			}
		}); //

		scrollPanel.setCorner(JScrollPane.UPPER_RIGHT_CORNER, btn_showsql);

		scrollPanel.getViewport().add(table);
		return scrollPanel;
	}

	private int getInitRow() {
		int li_rowcount = table.getModel().getRowCount();
		String temp_str;
		for (int i = 0; i < li_rowcount; i++) {
			temp_str = (String) this.table.getModel().getValueAt(i, 0);
			if (this.getInitRefItemVO() != null) {
				if (temp_str.equals(this.getInitRefItemVO().getId())) {
					return i;
				}
			}
		}
		return -1;
	}

	private JPanel getSouthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		panel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3) {
					onShowSQL();
				}
			}

		});

		JButton btn_1 = new WLTButton(UIUtil.getLanguage("确定"));
		btn_1.setPreferredSize(new Dimension(85, 20));
		btn_1.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		JButton btn_3 = new WLTButton(UIUtil.getLanguage("取消"));
		btn_3.setPreferredSize(new Dimension(85, 20));
		btn_3.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				onClose();
			}
		});

		panel.add(btn_1);
		panel.add(btn_3);

		return panel;
	}

	private void onKeyReleased() {
		clearTableData(); // 清空数据
		String str_text = textField.getText(); //
		String[] items = struct.getHeaderName();
		String[][] str_data = struct.getBodyData(); //

		int columnCount = items.length;
		for (int i = 0; i < str_data.length; i++) {
			boolean bo_ifinsert = false;
			for (int j = 0; j < columnCount; j++) {
				if (str_text == null || str_text.trim().equals("")) { // 如果没条件则直接加入
					bo_ifinsert = true;
					break;
				} else if (str_data[i][j] != null && !str_data[i][j].trim().equals("")) {
					if (checkbox_iflike.isSelected()) { // 模糊品配
						if (str_data[i][j].toLowerCase().indexOf(str_text.toLowerCase()) >= 0) {
							bo_ifinsert = true;
							break;
						}
					} else {
						if (str_data[i][j].toLowerCase().indexOf(str_text.toLowerCase()) == 0) {
							bo_ifinsert = true;
							break;
						}
					}
				}
			}

			if (bo_ifinsert) {
				((DefaultTableModel) table.getModel()).addRow(str_data[i]);
			}
		}

		table.updateUI();
		// getFocuse();
	}

	private void onSearch() {
		clearTableData(); // 清空数据

		String str_text = textField.getText(); //
		String str_condition = combox_condition.getSelectedItem().toString();
		int li_pos = findPos(str_condition);

		try {
			struct = UIUtil.getTableDataStructByDS(this.str_datasourcename, this.str_sql); // 取数
			String[][] str_data = struct.getBodyData();

			String str_compare = combox_compare.getSelectedItem().toString();

			for (int i = 0; i < str_data.length; i++) {
				if (str_text == null || str_text.trim().equals("")) {
					((DefaultTableModel) table.getModel()).addRow(str_data[i]);
				} else {
					if (str_compare.equals("=")) {
						if (str_data[i][li_pos].equals(str_text)) {
							((DefaultTableModel) table.getModel()).addRow(str_data[i]);
						}
					} else if (str_compare.equals(">")) {
						// if (str_data[i][li_pos].indexOf(str_text) >= 0) {
						// ((DefaultTableModel)
						// table.getModel()).addRow(str_data[i]);
						// }
					} else if (str_compare.equals(">=")) {
						// if (str_data[i][li_pos].indexOf(str_text) >= 0) {
						// ((DefaultTableModel)
						// table.getModel()).addRow(str_data[i]);
						// }
					} else if (str_compare.equals("<")) {
						// if (str_data[i][li_pos].indexOf(str_text) >= 0) {
						// ((DefaultTableModel)
						// table.getModel()).addRow(str_data[i]);
						// }
					} else if (str_compare.equals("<=")) {
						// if (str_data[i][li_pos].indexOf(str_text) >= 0) {
						// ((DefaultTableModel)
						// table.getModel()).addRow(str_data[i]);
						// }
					} else if (str_compare.equals("<>")) {
						// if (str_data[i][li_pos].indexOf(str_text) >= 0) {
						// ((DefaultTableModel)
						// table.getModel()).addRow(str_data[i]);
						// }
					} else if (str_compare.equals("like")) {
						if (str_data[i][li_pos].indexOf(str_text) >= 0) {
							((DefaultTableModel) table.getModel()).addRow(str_data[i]);
						}
					}
				}
			}
			table.updateUI();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private int findPos(String _str_condition) {
		String[] items = struct.getHeaderName();

		for (int i = 0; i < items.length; i++) {
			if (items[i].equals(_str_condition)) {
				return i;
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

	/**
	 * 确定
	 * 
	 */
	private void onConfirm() {
		int li_selectedRow = table.getSelectedRow(); //
		if (li_selectedRow < 0) {
			MessageBox.showSelectOne(this);
			this.requestFocus();
			return;
		}

		str_ref_pk = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 0));
		str_ref_code = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 1));
		str_ref_name = String.valueOf(this.table.getModel().getValueAt(li_selectedRow, 2));

		returnDataHashVO = new HashVO(); //创建返回值的VO
		String[] str_allkeys = struct.getHeaderName(); //所有的列!!
		for (int i = 0; i < str_allkeys.length; i++) {
			returnDataHashVO.setAttributeValue(str_allkeys[i], this.table.getModel().getValueAt(li_selectedRow, i)); //
		}

		setCloseType(1); //
		this.dispose();
	}

	/**
	 * 关闭
	 */
	protected void onClose() {
		setCloseType(2);
		this.dispose();
	}

	private void onShowSQL() {
		System.out.println(this.str_sql);
		MessageBox.showTextArea(this, this.str_sql);
	}

	public RefItemVO getReturnRefItemVO() {
		return new RefItemVO(str_ref_pk, str_ref_code, str_ref_name, returnDataHashVO);
	}

	class MyCellRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			label.setOpaque(true); //
			if (isSelected) {
				label.setBackground(table.getSelectionBackground()); //
			} else {
				if (row % 2 == 0) {
					label.setBackground(Color.WHITE);
				} else {
					label.setBackground(new Color(245, 255, 255));
				}
			}
			return label;
		}
	}
}
