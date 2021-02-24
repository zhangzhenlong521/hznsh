package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTTextArea;
import cn.com.infostrategy.ui.sysapp.login.DeskTopPanel;

/**
 * 查询面板!!!以前只有一个,现在增加成5个!!!
 * @author xch
 *
 */
public class SQLQueryPanel extends JPanel implements KeyListener, ActionListener {

	private static final long serialVersionUID = 1L; //
	private JComboBox comBox_ds = null;
	private JButton btn_execute = null;
	private JCheckBox checkIsDealSplit = null; //
	private JTextArea textArea = null; //
	private JPanel panel_result = new JPanel(); //

	public SQLQueryPanel() {
		initialize(); //
	}

	/**
	 * 初始化页面..
	 */
	private void initialize() {
		this.setLayout(new BorderLayout()); //
		this.add(getNorthPanel(), BorderLayout.NORTH); //
		textArea = new WLTTextArea(); //有撤销/重做功能[郝明2012-05-02]
		textArea.setToolTipText(null);
		textArea.setLineWrap(true); //
		textArea.setFont(new Font("宋体", 0, 12)); //
		textArea.setForeground(Color.BLUE); //
		textArea.setBackground(new Color(255, 255, 249)); //
		textArea.addKeyListener(this);
		textArea.requestFocus(); //
		textArea.setFocusCycleRoot(true); //

		panel_result.setLayout(new BorderLayout()); //

		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(textArea), panel_result); //
		splitPanel.setDividerLocation(200); //
		splitPanel.setOneTouchExpandable(true); //

		this.add(splitPanel, BorderLayout.CENTER); //

	}

	private JPanel getNorthPanel() {
		JPanel panel = new JPanel(); //
		panel.setLayout(new FlowLayout(FlowLayout.LEFT)); //

		comBox_ds = new JComboBox(); //
		DataSourceVO[] dsVOs = ClientEnvironment.getInstance().getDataSourceVOs();
		for (int i = 0; i < dsVOs.length; i++) {
			comBox_ds.addItem(dsVOs[i].getName()); //
		}
		comBox_ds.setFocusable(false); //
		comBox_ds.setPreferredSize(new Dimension(125, 20)); //

		checkIsDealSplit = new JCheckBox("是否分割SQL", true); //
		checkIsDealSplit.setFocusable(false); //
		checkIsDealSplit.setToolTipText("如果勾选了,则就按分号分隔成多条SQL,但如果SQL中需要有SQL,则可能报错,不分割就意味着只能处理一条SQL!"); //

		btn_execute = new WLTButton("Execute SQL"); //
		btn_execute.addActionListener(this); //

		panel.add(comBox_ds);
		panel.add(checkIsDealSplit);
		panel.add(btn_execute);
		return panel; //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_execute) {
			onExecute(); //
		}
	}

	/**
	 * 
	 */
	private void onExecute() {
		btn_execute.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		textArea.setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try {
			if (DeskTopPanel.deskTopPanel != null) {
				DeskTopPanel.deskTopPanel.setRealCallMenuName("首页");
			}
			String str_dsname = (String) comBox_ds.getSelectedItem();
			String str_sql = textArea.getSelectedText();
			if (str_sql == null) {
				str_sql = textArea.getText(); //
			}

			str_sql = str_sql.trim(); //
			if (str_sql.equals("")) {
				panel_result.removeAll(); //
				panel_result.updateUI(); //
				return; //
			}

			String[] str_tempSqls = str_sql.split("\n"); //先按换行处理
			StringBuffer sb_sqls = new StringBuffer(); //
			for (int i = 0; i < str_tempSqls.length; i++) {
				sb_sqls.append(str_tempSqls[i].trim() + "\n"); //
			}
			str_sql = sb_sqls.toString(); //
			String[] str_sqls = null; //
			if (checkIsDealSplit.isSelected()) { //如果需要处理分号
				str_sqls = str_sql.split(";\n"); //
				for (int i = 0; i < str_sqls.length; i++) {
					str_sqls[i] = str_sqls[i].trim(); //
					if (str_sqls[i].endsWith(";")) { //
						str_sqls[i] = str_sqls[i].substring(0, str_sqls[i].length() - 1); //
					}
					//System.out.println(i + ":" + str_sqls[i]); //
				}
			} else {
				str_sqls = new String[] { str_sql }; //
			}
			panel_result.removeAll(); //
			if (str_sqls.length == 1) { //如果只有一个SQL
				JPanel panel = getTabPanel(str_dsname, str_sqls[0]); //
				panel_result.add(panel); //
			} else { //如果有许多SQL
				JTabbedPane tabPane = new JTabbedPane(); //
				for (int i = 0; i < str_sqls.length; i++) {
					JPanel panel = getTabPanel(str_dsname, str_sqls[i]); //
					String str_tabtitle = "[" + (i + 1) + "]"; //
					if (str_sqls[i].trim().length() >= 6) {
						str_tabtitle = str_tabtitle + str_sqls[i].trim().substring(0, 6);
					}
					tabPane.addTab(str_tabtitle, panel); //
				}
				panel_result.add(tabPane); //
			}
			panel_result.updateUI(); //
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
			if (DeskTopPanel.deskTopPanel != null) {
				DeskTopPanel.deskTopPanel.setRealCallMenuName(null);
			}
		} finally {
			btn_execute.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			textArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			if (DeskTopPanel.deskTopPanel != null) {
				DeskTopPanel.deskTopPanel.setRealCallMenuName(null);
			}
		}
	}

	private JPanel getTabPanel(String _dsname, String _sql) {
		_sql = _sql.trim(); //
		if (_sql.toLowerCase().startsWith("select ") || _sql.toLowerCase().startsWith("with ")) {
			return getQueryPanel(_dsname, _sql); //查询
		} else {
			return getUpdatePanel(_dsname, _sql); //修改
		}
	}

	private JPanel getQueryPanel(String _dsname, String _sql) {
		try {
			TableDataStruct tds = UIUtil.getTableDataStructByDS(_dsname, _sql); ///
			TableResultPanel resultPanel = new TableResultPanel(tds, _sql); //
			return resultPanel; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return getErrorPanel(ex); //
		}
	}

	private JPanel getUpdatePanel(String _dsname, String _sql) {
		try {
			JPanel panel = new JPanel(); //
			panel.setLayout(new BorderLayout());
			int li_count = UIUtil.executeUpdateByDS(_dsname, _sql); //
			JTextArea textArea = new JTextArea("Execute Success,deal [" + li_count + "] records."); //
			if (li_count == 0) {
				textArea.setForeground(Color.BLACK); //
			} else {
				textArea.setForeground(Color.BLUE); //
			}
			textArea.setToolTipText("[" + _sql + "]"); //
			JScrollPane scrollPanel = new JScrollPane(textArea); //
			panel.add(scrollPanel); //
			return panel; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return getErrorPanel(ex); //
		}
	}

	private JPanel getErrorPanel(Exception _ex) {
		JPanel panel = new JPanel(); //
		panel.setLayout(new BorderLayout());
		JTextArea textArea = new JTextArea(_ex.getMessage());
		textArea.setLineWrap(true); //
		textArea.setForeground(Color.RED); //
		panel.add(new JScrollPane(textArea));
		return panel;
	}

	public void keyPressed(KeyEvent e) {
		//System.out.println(e.getKeyChar() + "," + e.getKeyCode()); //
		JTextArea textArea = ((JTextArea) e.getSource()); //
		if (e.isControlDown()) {
			int fontsize = textArea.getFont().getSize(); //
			if (e.getKeyCode() == e.VK_EQUALS) { //如果按了加号
				textArea.setFont(new Font("Serif", Font.PLAIN, fontsize + 5)); //
			} else if (e.getKeyCode() == e.VK_MINUS) { //如果按了减号
				int li_newSize = fontsize - 5; //
				if (li_newSize < 9) {
					li_newSize = 9;
				}
				textArea.setFont(new Font("Serif", Font.PLAIN, li_newSize)); //
			} else if (e.getKeyCode() == e.VK_BACK_SPACE) { //如果按了后退
				textArea.setFont(new Font("Serif", Font.PLAIN, 14)); //
			}
		}
		if (e.getKeyCode() >= e.VK_F1 && e.getKeyCode() <= e.VK_F12) { //按任意一个F键都会触发.
			onExecute();
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
	}

	class TableResultPanel extends JPanel implements MouseListener, ActionListener {
		private static final long serialVersionUID = -1368219850294261208L;
		private TableDataStruct tds = null;
		private String sql = null; //
		private JTable table = null; //
		private JButton btn_detail, btn_decrypt,btn_buildsql = null; //

		public TableResultPanel(TableDataStruct _tds, String _sql) {
			this.tds = _tds; //
			this.sql = _sql;
			this.setLayout(new BorderLayout()); //
			table = new JTable(tds.getBodyData(), tds.getHeaderName()); //
			table.setToolTipText("点击右键,可以将选中的记录坚起来查看!!"); //
			table.setRowSelectionAllowed(true);
			table.setColumnSelectionAllowed(true); //
			table.addMouseListener(this); //
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //
			JScrollPane scrollPanel = new JScrollPane(table); //

			this.add(scrollPanel, BorderLayout.CENTER); //

			JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.LEFT)); //
			panel_btn.setToolTipText("[" + sql + "]"); //
			btn_detail = new WLTButton(UIUtil.getImage("office_148.gif")); //
			btn_detail.setToolTipText("坚起来查看"); //
			btn_detail.addActionListener(this); //

			btn_decrypt = new WLTButton(UIUtil.getImage("office_131.gif")); //
			btn_decrypt.setToolTipText("解密密码"); //
			btn_decrypt.addActionListener(this); //
			btn_buildsql = new WLTButton(UIUtil.getImage("office_154.gif")); //
			btn_buildsql.setToolTipText("构造InsertSQL"); //
			btn_buildsql.addActionListener(this); //
			panel_btn.add(btn_detail); //
			panel_btn.add(btn_decrypt); //
			panel_btn.add(btn_buildsql); //

			this.add(panel_btn, BorderLayout.NORTH); ////
		}

		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == e.BUTTON3) { //如果按了右键
				showRecordByDetailModel(); //
			}
		}

		private void showRecordByDetailModel() {
			int li_row = table.getSelectedRow();
			if (li_row < 0) {
				MessageBox.showSelectOne(this);
				return;
			}

			int li_colcount = table.getColumnCount(); //
			String[] str_colname = new String[li_colcount]; //
			String[] str_colvalue = new String[li_colcount]; //
			String[] str_coltype = new String[li_colcount]; //
			for (int i = 0; i < li_colcount; i++) {
				str_colname[i] = table.getColumnName(i); //
				str_colvalue[i] = (String) table.getValueAt(li_row, i); //
				str_coltype[i] = findColType(str_colname[i]); //
			}

			SQLQueryRecordDialog dialog = new SQLQueryRecordDialog(this, str_colname, str_colvalue, str_coltype); //
			dialog.setVisible(true); //
		}

		private void onDecrypt() {
			int li_row = table.getSelectedRow();
			if (li_row < 0) {
				MessageBox.showSelectOne(this);
				return;
			}

			int li_col = table.getSelectedColumn(); //
			String str_value = "" + table.getValueAt(li_row, li_col); //
			String str_dcry = new DESKeyTool().decrypt(str_value); //
			if (str_value.equals(str_dcry)) {
				MessageBox.show("使用DESKeyTool解密【" + str_value + "】后的结果是:\r\n【" + str_dcry + "】\r\n可能发生解密失败(比如这根本就不是一个DES加密后数据),请至控制台查看详细!"); //
			} else {
				MessageBox.show("使用DESKeyTool解密【" + str_value + "】后的结果是:\r\n【" + str_dcry + "】"); //
			}
		}

		public void mouseEntered(MouseEvent e) {

		}

		public void mouseExited(MouseEvent e) {

		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {

		}

		private String findColType(String _colname) {
			String[] str_colnames = tds.getHeaderName();
			String[] str_coltypes = tds.getHeaderTypeName(); //
			for (int i = 0; i < str_colnames.length; i++) {
				if (str_colnames[i].equals(_colname)) {
					return str_coltypes[i]; //
				}
			}
			return null;
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btn_detail) {
				showRecordByDetailModel(); //
			} else if (e.getSource() == btn_decrypt) {
				onDecrypt(); //
			}else if (e.getSource() == btn_buildsql) {
				onBuildInsertSQL(); //
			}
		}
		private String getTableName() {
			String str_tablename = "${TableName}"; //表名
			int li_pos = sql.toLowerCase().indexOf(" from "); //
			if (li_pos > 0) {
				String str_subfix = sql.substring(li_pos + 6, sql.length()); //后面的内容
				str_subfix = str_subfix.trim(); //
				if (str_subfix.indexOf(" ") > 0) {
					str_tablename = str_subfix.substring(0, str_subfix.indexOf(" ")); ////
				} else {
					str_tablename = str_subfix; //
				}
			}
			return str_tablename; //
		}
		//有时将表格中的数据快速生成SQL,然后插入到目标库中.
		private void onBuildInsertSQL() {
			String str_tablename = getTableName();
			/*BillCardDialog cardDialog = new BillCardDialog(this, "请填写表名,将根据这个表名拼出SQL", new String[][] { { "表名", "150" } }, 350, 150, true); //
			cardDialog.setCardItemValue("表名", str_tablename); //
			cardDialog.setVisible(true); //
			if (cardDialog.getCloseType() != 1) {
				return; //
			}

			str_tablename = cardDialog.getCardItemValue("表名"); //
*/
			int[] li_selRows = table.getSelectedRows(); //选中的行!
			int[] li_selCols = table.getSelectedColumns(); //选中的列!
			if (li_selRows.length <= 0) { //如果有选中的数据,则认为是选中所有数据!!
				li_selRows = new int[table.getRowCount()]; //
				for (int i = 0; i < li_selRows.length; i++) {
					li_selRows[i] = i; //
				}
				li_selCols = new int[table.getColumnCount()];
				for (int i = 0; i < li_selCols.length; i++) {
					li_selCols[i] = i; //
				}
			}

			String[] str_columns = new String[li_selCols.length]; //所有列名			
			for (int i = 0; i < str_columns.length; i++) {
				str_columns[i] = table.getColumnName(li_selCols[i]).toLowerCase(); //
			}

			StringBuilder sb_sql = new StringBuilder(); //
			for (int i = 0; i < li_selRows.length; i++) {
				InsertSQLBuilder isql = new InsertSQLBuilder(str_tablename); //
				for (int j = 0; j < li_selCols.length; j++) {
					Object cellValue = table.getValueAt(li_selRows[i], li_selCols[j]); //
					isql.putFieldValue(str_columns[j], (cellValue == null ? "" : cellValue.toString())); //
				}
				sb_sql.append(isql.getSQL() + ";\r\n"); //
			}
			MessageBox.showTextArea(this, "SQL", sb_sql.toString(), 900, 500); //弹出框口,显示
		}
	}

}
