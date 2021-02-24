/**************************************************************************
 * $RCSfile: ShowExistTempleteCodeDialog.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;

public class ShowExistTempleteCodeDialog extends BillDialog {

	private static final long serialVersionUID = 6700062011846933715L;

	private BillListPanel listPanel_main;

	private BillListPanel listPanel_child;

	private String str_condition = "";

	private String str_columnname = "";

	private String str_sql = null;

	private String str_selected_code = null;

	private String str_selected_id = null;

	public ShowExistTempleteCodeDialog(Container _parent, String _name) {
		super(_parent, _name, 550, 300); //
		str_sql = "select * from pub_templet_1";
		initialize();
	}

	public ShowExistTempleteCodeDialog(Container _parent, String _name, String _column_name, String _condition) {
		super(_parent, _name, 750, 500); //
		this.str_columnname = _column_name;
		this.str_condition = _condition;
		initialize();
	}

	public ShowExistTempleteCodeDialog(Container _parent, String _name, Hashtable _ht_condition) {
		super(_parent, _name, 750, 500); //
		String str_sql_end = "";
		String str_table = (String) _ht_condition.get("TABLENAME");
		String str_code = (String) _ht_condition.get("TEMPLETCODE");

		if (str_table != null && !str_table.equals("")) {
			str_sql_end = str_sql_end + " And TABLENAME='" + str_table + "'";
		}
		if (str_code != null && !str_code.equals("")) {
			str_sql_end = str_sql_end + " And TEMPLETCODE='" + str_code + "'";
		}
		str_sql = "select * from pub_templet_1 where 1=1" + str_sql_end;
		initialize();
	}

	private void initialize() {
		this.getContentPane().setLayout(new BorderLayout());

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, getMainPanel(), getChildPanel());
		splitPane.setDividerLocation(200);
		splitPane.setDividerSize(10);
		splitPane.setOneTouchExpandable(true);

		this.getContentPane().add(splitPane, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	private Component getSouthPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());

		JButton btn_confirm = new JButton("确定");
		btn_confirm.setPreferredSize(new Dimension(100, 20));
		btn_confirm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onConfirm();
			}
		});

		panel.add(btn_confirm);
		return panel;
	}

	protected void onConfirm() {
		int li_count = listPanel_main.getSelectedRow();
		if (li_count >= 0) {			
			str_selected_id = ((StringItemVO) listPanel_main.getTable().getModel().getValueAt(li_count, 1)).getStringValue();
			str_selected_code = ((StringItemVO) listPanel_main.getTable().getModel().getValueAt(li_count, 2)).getStringValue();

		}
		this.dispose();
	}

	private BillListPanel getChildPanel() {
		if (listPanel_child == null) {
			listPanel_child = new BillListPanel("PUB_TEMPLET_1_ITEM"); //
		}
		return listPanel_child;
	}

	private BillListPanel getMainPanel() {
		if (listPanel_main == null) {
			listPanel_main = new BillListPanel("PUB_TEMPLET_1"); //
			if (str_sql == null) {
				str_sql = listPanel_main.getSQL(" " + str_columnname + " = '" + str_condition.trim().toUpperCase() + "'");
			}
			listPanel_main.QueryData(str_sql);
			listPanel_main.getTable().getSelectionModel().addListSelectionListener(new ListSelectionListener() {

				public void valueChanged(ListSelectionEvent e) {
					if (!e.getValueIsAdjusting()) {
						onRowSelectChanged();
					}
				}
			});
		}
		return listPanel_main;
	}

	protected void onRowSelectChanged() {
		int li_row = getMainPanel().getSelectedRow(); // 取得选中的行
		if (li_row < 0) {
			return;
		}
		String str_pk = (String) getMainPanel().getRealValueAtModel(li_row, "PK_PUB_TEMPLET_1"); // 取得主键值
		// System.out.println(str_pk);
		String str_sql = getChildPanel().getSQL(" 1=1 and PK_PUB_TEMPLET_1='" + str_pk + "'");
		getChildPanel().QueryData(str_sql);
	}

	public String getSelectedRowCode() {
		return str_selected_code;
	}

	public String getSelectedRowCodeID() {
		return str_selected_id;
	}
}
