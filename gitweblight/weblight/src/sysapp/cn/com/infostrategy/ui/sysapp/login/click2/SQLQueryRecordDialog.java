package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import cn.com.infostrategy.ui.common.BillDialog;

public class SQLQueryRecordDialog extends BillDialog {

	private static final long serialVersionUID = 5055978985822081382L;

	public SQLQueryRecordDialog(Container _parent, String[] _colname, String[] _colvalue, String[] _coltype) {
		super(_parent, "Record", 500, 500); //
		this.setModal(false); //
		this.getContentPane().setLayout(new BorderLayout()); //
		String[][] str_data = new String[_colname.length][3];
		for (int i = 0; i < _colname.length; i++) {
			str_data[i][0] = _colname[i];
			str_data[i][1] = _colvalue[i];
			str_data[i][2] = _coltype[i];
		}

		JTable table = new JTable(str_data, new String[] { "Column Name", "Column Value", "Data Type" }); //
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(true);  //
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // 
		
		table.getColumn(table.getColumnName(0)).setPreferredWidth(125);
		table.getColumn(table.getColumnName(1)).setPreferredWidth(225);
		table.getColumn(table.getColumnName(2)).setPreferredWidth(95);

		this.getContentPane().add(new JScrollPane(table)); //
	}

}
