/**************************************************************************
 * $RCSfile: RecordShowDialog.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class RecordShowDialog extends BillDialog {
	private static final long serialVersionUID = 1L;

	private String dataSourceName = null;

	private String str_tablename = null;

	private String str_pkvalue = null;

	private String[] itemkeys = null;

	private String[] itemvalue = null;

	private String str_pkname = null;

	private JTable table = null;

	private TableDataStruct tabledata = null;

	private Color color = new Color(240, 240, 240);

	public RecordShowDialog(Container _parent, String _tablename, String _pkname, String _pkvalue) throws Exception {
		super(_parent);
		this.str_tablename = _tablename;
		this.str_pkvalue = _pkvalue;
		this.str_pkname = _pkname;
		this.setTitle("明细");
		this.setSize(665, 360);
		this.setLocation(200, 100);
		initialize();
	}

	public RecordShowDialog(BillListPanel _parent, String _tablename, String _pkname, String _pkvalue) throws Exception {
		super(_parent);
		this.dataSourceName = _parent.getDataSourceName(); //取得数据源名称
		this.str_tablename = _tablename;
		this.str_pkvalue = _pkvalue;
		this.str_pkname = _pkname;
		this.setTitle("明细");
		this.setSize(665, 360);
		this.setLocation(200, 100);
		initialize();
	}

	public RecordShowDialog(BillCardPanel _parent, String _tablename, String _pkname, String _pkvalue) throws Exception {
		super(_parent);
		this.dataSourceName = _parent.getDataSourceName(); //取得数据源名称
		this.str_tablename = _tablename;
		this.str_pkvalue = _pkvalue;
		this.str_pkname = _pkname;
		this.setTitle("明细");
		this.setSize(665, 360);
		this.setLocation(200, 100);
		initialize();
	}

	public RecordShowDialog(BillListPanel _parent, String _tablename, String[] _itemkeys, String[] _itemvalue) throws Exception {
		super(_parent);
		this.dataSourceName = _parent.getDataSourceName(); //取得数据源名称
		this.str_tablename = _tablename;
		this.itemkeys = _itemkeys;
		this.itemvalue = _itemvalue;
		this.setTitle("明细");
		this.setSize(665, 360);
		this.setLocation(200, 100);
		initialize();
	}

	public RecordShowDialog(BillCardPanel _parent, String _tablename, String[] _itemkeys, String[] _itemvalue) throws Exception {
		super(_parent);
		this.dataSourceName = _parent.getDataSourceName(); //取得数据源名称
		this.str_tablename = _tablename;
		this.itemkeys = _itemkeys;
		this.itemvalue = _itemvalue;
		this.setTitle("明细");
		this.setSize(665, 360);
		this.setLocation(200, 100);
		initialize();
	}

	private void initialize() throws Exception {
		this.getContentPane().setLayout(new BorderLayout());
		StringBuffer str_sql = new StringBuffer();
		if (str_pkname == null) {
			str_sql.append("select * from  " + str_tablename + " where ");
			for (int i = 0; i < itemkeys.length; i++) {
				if (i == itemkeys.length - 1) {
					str_sql.append("" + itemkeys[i] + "='" + itemvalue[i] + "'");
				} else {
					str_sql.append("" + itemkeys[i] + "='" + itemvalue[i] + "' AND ");
				}
			}
		} else {
			str_sql.append("select * from " + str_tablename + " where " + str_pkname + " ='" + str_pkvalue + "'");
		}

		try {
			tabledata = UIUtil.getTableDataStructByDS(this.dataSourceName, str_sql.toString());
			String[] str_row = tabledata.getHeaderName();
			String[] str_type = tabledata.getHeaderTypeName();
			int[] li_length = tabledata.getHeaderLength(); //
			int[] li_precision = tabledata.getPrecision();
			int[] li_scale = tabledata.getScale();
			String[][] data = tabledata.getBodyData();
			String[][] str_data = new String[str_row.length][3];
			if (str_row == null || str_row.length <= 0) {
				return;
			}
			if (data == null || data.length <= 0) {
				str_sql.append("【根据SQL没有找到一条记录!】");
			} else {
				str_sql.append("【共有" + data.length + "条数据】");
			}
			for (int i = 0; i < str_row.length; i++) {
				str_data[i][0] = str_row[i];

				if (str_type[i].equalsIgnoreCase("DECIMAL") || str_type[i].equalsIgnoreCase("NUMBER")) {
					str_data[i][1] = str_type[i] + "(" + li_precision[i] + "," + li_scale[i] + ")";
				} else {
					str_data[i][1] = str_type[i] + "(" + li_length[i] + ")";
				}
				if (data.length > 0) {
					str_data[i][2] = data[0][i];
				}
			}
			table = new JTable(str_data, new String[] { "数据库列名", "字段类型", "字段值" });
			table.setRowSelectionAllowed(true);
			table.setColumnSelectionAllowed(true); //
			table.getColumn(table.getColumnName(0)).setCellEditor(new TypeCellEditor(new JTextField()));
			table.getColumn(table.getColumnName(1)).setCellEditor(new TypeCellEditor(new JTextField()));
			table.getColumn(table.getColumnName(2)).setCellEditor(new ValueCellEditor(new JTextField()));
			table.getColumnModel().getColumn(0).setPreferredWidth(150);
			table.getColumnModel().getColumn(1).setPreferredWidth(100);
			table.getColumnModel().getColumn(2).setPreferredWidth(400);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setAutoscrolls(true);
			table.setBackground(color);
			JScrollPane scrollPanel = new JScrollPane(table);
			scrollPanel.setBackground(color);
			if (str_pkname == null) {
				this.getContentPane().add(new JLabel(str_sql.toString() + "【该表无主键】", SwingConstants.CENTER), BorderLayout.NORTH);
			} else {
				JTextField textField = new JTextField(str_sql.toString()); //
				textField.setHorizontalAlignment(JTextField.CENTER); //
				textField.setBorder(BorderFactory.createEmptyBorder()); //
				textField.setBackground(LookAndFeel.systembgcolor); //
				textField.setEditable(false); //
				this.getContentPane().add(textField, BorderLayout.NORTH);
			}
			this.getContentPane().add(scrollPanel, BorderLayout.CENTER);
			JButton bn_exit = new JButton("确定");
			bn_exit.setPreferredSize(new Dimension(80, 20));
			bn_exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					onExit();
				}
			});
			JPanel panel_south = new JPanel();
			panel_south.setLayout(new FlowLayout(FlowLayout.CENTER));
			panel_south.add(bn_exit);
			panel_south.setBackground(color);
			this.getContentPane().add(panel_south, BorderLayout.SOUTH);
			this.getContentPane().setBackground(color);
			this.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;
		}
	}

	private void onExit() {
		this.dispose();
	}

	class TypeCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		public TypeCellEditor(JTextField textField) {
			super(textField);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JTextField textField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			return textField;
		}

		public boolean isCellEditable(EventObject evt) {
			return false;
		}
	}

	class ValueCellEditor extends DefaultCellEditor {

		private static final long serialVersionUID = 1L;

		public ValueCellEditor(JTextField textField) {
			super(textField);
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
			JTextField textField = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
			return textField;
		}

		public boolean isCellEditable(EventObject evt) {
			if (evt instanceof MouseEvent) {
				return ((MouseEvent) evt).getClickCount() >= 2;
			}
			return true;
		}
	}
}
/**************************************************************************
 * $RCSfile: RecordShowDialog.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: RecordShowDialog.java,v $
 * Revision 1.6  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.5  2012/05/09 05:56:41  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:31:46  wanggang
 * restore
 *
 * Revision 1.2  2011/08/23 09:16:56  haoming
 * *** empty log message ***
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
 * Revision 1.2  2009/07/09 09:38:35  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:46  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/02/19 07:30:59  wangjian
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/07/03 18:20:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.6  2008/06/24 09:25:41  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2008/05/30 04:19:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2008/05/30 04:17:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/05/29 11:09:30  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/03/16 12:11:42  xuchanghua
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
 * Revision 1.3  2007/09/20 05:08:25  xch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/02 05:02:51  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/02/10 08:59:51  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 08:51:57  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:31  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
