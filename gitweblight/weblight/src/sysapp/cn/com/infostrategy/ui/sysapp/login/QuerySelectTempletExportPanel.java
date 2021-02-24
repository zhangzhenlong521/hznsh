/**************************************************************************
 * $RCSfile: QuerySelectTempletExportPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:32 $
 **************************************************************************/
package cn.com.infostrategy.ui.sysapp.login;

/*
 * ������ѯѡ��ģ�������ΪSQL�ļ������㵼�뵽�������ݿ���.
 * by sxf
 */

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.AbstractCustomerButtonBarPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.t08.AbstractStyleWorkPanel_08;

public class QuerySelectTempletExportPanel extends AbstractCustomerButtonBarPanel {

	private static final long serialVersionUID = 8135325269924620211L;

	private String[][] str_tem_cols = null;

	private String[][] str_item_cols = null;

	public void initialize() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		JButton export = new JButton("��������");
		export.setPreferredSize(new Dimension(100, 20));
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExport();
			}
		});
		this.add(export);
	}

	private void onExport() {
		int li_count = ((AbstractStyleWorkPanel_08) super.getParentWorkPanel()).getParent_BillListPanel().getTable().getSelectedRowCount();
		if (li_count <= 0) {
			JOptionPane.showMessageDialog(this, "����ѡ��һ����¼!");
			return;
		}
		int[] selected_rows = ((AbstractStyleWorkPanel_08) super.getParentWorkPanel()).getParent_BillListPanel().getTable().getSelectedRows();
		dealOut(selected_rows);
	}

	private void dealOut(int[] _selected_rows) {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = chooser.showSaveDialog(this);

		if (result != 0) {
			return;
		}
		String file_path = chooser.getSelectedFile().getPath();
		File directory = new File(file_path);
		File all_file = new File(file_path + "\\all.sql");
		if (!directory.exists()) {
			if (!directory.mkdir()) {
				JOptionPane.showMessageDialog(this, "����SQLĿ¼����", "������ʾ", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		// ����ALL�ļ�
		if (!all_file.exists()) {
			try {
				all_file.createNewFile();
			} catch (HeadlessException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "����all.sql����:" + e.getMessage(), "������ʾ", JOptionPane.ERROR_MESSAGE);
				return;
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(this, "����all.sql����" + e.getMessage(), "������ʾ", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		FileWriter resultFile = null;
		try {
			resultFile = new FileWriter(all_file);

			PrintWriter myFile = new PrintWriter(resultFile);

			getColumnsArray();
			for (int i = 0; i < _selected_rows.length; i++) {
				String str_templete_code = ((AbstractStyleWorkPanel_08) super.getParentWorkPanel()).getParent_BillListPanel().getRealValueAtModel(_selected_rows[i], "TEMPLETCODE").toString();
				if (result == 0) {
					String file_total_path = file_path + "\\" + str_templete_code + ".sql";
					writeIntoFile(file_total_path, str_templete_code);
					myFile.println("@.\\" + str_templete_code + ".sql");
				}
			}
			JOptionPane.showMessageDialog(this, "���ݵ����ɹ���", "������ʾ", JOptionPane.INFORMATION_MESSAGE);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				resultFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getColumnsArray() {
		if (str_tem_cols == null) {
			String _sql_1 = "Select * From cols where TABLE_NAME='PUB_QUERYTEMPLET' Order by COLUMN_ID";
			try {
				str_tem_cols = UIUtil.getStringArrayByDS(null, _sql_1);
			} catch (WLTRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (str_item_cols == null) {
			String _sql_item = "Select * From cols where TABLE_NAME='PUB_QUERYTEMPLET_ITEM' Order by COLUMN_ID";
			try {
				str_item_cols = UIUtil.getStringArrayByDS(null, _sql_item);
			} catch (WLTRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void writeIntoFile(String _filename, String _templete_code) {
		if (_filename == null || _filename.equals("")) {
			JOptionPane.showMessageDialog(this, "��������ȷ���ļ�����");
		}
		File out_file = new File(_filename);
		if (!out_file.exists()) {
			try {
				out_file.createNewFile();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "����" + _filename + ".sql����");
				e.printStackTrace();
				return;
			}
		}
		try {
			FileWriter resultFile = new FileWriter(out_file);
			PrintWriter myFile = new PrintWriter(resultFile);
			myFile.println("Delete From PUB_QUERYTEMPLET_ITEM Where PK_PUB_QUERYTEMPLET in " + "(select PK_PUB_QUERYTEMPLET from PUB_QUERYTEMPLET where TEMPLETCODE = '" + _templete_code + "');");
			myFile.println("Delete From PUB_QUERYTEMPLET where TEMPLETCODE='" + _templete_code + "';");
			myFile.print("INSERT INTO PUB_QUERYTEMPLET");
			myFile.print("(");

			// ��pub_templet_1���ÿ�ж�д���ļ�
			for (int i = 0; i < str_tem_cols.length; i++) {
				if (i == str_tem_cols.length - 1) {// ���һ�в��Ӷ���,
					myFile.print(" " + str_tem_cols[i][1]);
				} else {
					myFile.print(" " + str_tem_cols[i][1] + ",");
				}
			}
			myFile.print(")");

			String _sql_context = "Select * From PUB_QUERYTEMPLET Where templetcode ='" + _templete_code + "'";
			String[][] str_context = null;
			try {
				str_context = UIUtil.getStringArrayByDS(null, _sql_context);
			} catch (WLTRemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// ��pub_templet_1��ÿ�е����ݶ�д���ļ�
			myFile.print(" VALUES ");
			myFile.print("(s_pub_querytemplet.nextval,");

			for (int i = 1; i < str_context[0].length; i++) {
				String insert = getInsertValue(str_context[0][i].trim());
				if (i != str_context[0].length - 1)
					insert += ",";
				myFile.print(insert);
			}
			myFile.println(");");
			myFile.println("");

			// ��ÿ��Item���к�����д���ļ�
			String _sql_item_context = "Select * From PUB_QUERYTEMPLET_ITEM Where PK_PUB_QUERYTEMPLET in " + "(select PK_PUB_QUERYTEMPLET from PUB_QUERYTEMPLET where templetcode = '" + _templete_code + "')";
			TableDataStruct str_item_context = null;
			try {
				str_item_context = UIUtil.getTableDataStructByDS(null, _sql_item_context);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (str_item_context == null) {
				MessageBox.show(this, "Error occured when exporting.");
				return;
			}
			for (int i = 0; i < str_item_context.getBodyData().length; i++) {

				myFile.print("INSERT INTO PUB_QUERYTEMPLET_ITEM");
				myFile.print("(");

				for (int j = 0; j < str_item_cols.length; j++) {
					if (j == str_item_cols.length - 1) {
						myFile.print(str_item_cols[j][1]);
					} else {
						myFile.print(str_item_cols[j][1] + ",");
					}
				}
				myFile.print(")");
				myFile.print(" VALUES ");
				myFile.print("(");
				// myFile.print("S_PUB_QUERYTEMPLET_ITEM.nextval");
				StringBuffer insert = new StringBuffer();
				for (int j = 0; j < str_item_context.getBodyData()[i].length; j++) {
					insert.replace(0, insert.length(), "");
					if (str_item_context.getHeaderName()[j].equalsIgnoreCase("PK_PUB_QUERYTEMPLET_ITEM")) {// �����������
						insert.append("S_PUB_QUERYTEMPLET_ITEM.nextval");
					} else {
						if (!str_item_context.getHeaderName()[j].equalsIgnoreCase("PK_PUB_QUERYTEMPLET"))// ����Ǹ�������
							insert.append(getInsertValue(str_item_context.getBodyData()[i][j].trim()));
						else
							insert.append("(select PK_PUB_QUERYTEMPLET  From PUB_QUERYTEMPLET Where TEMPLETCODE = '" + _templete_code + "')");
					}
					if (j != str_item_context.getBodyData()[i].length - 1)
						insert.append(",");
					myFile.print(insert);
				}
				myFile.println(");");
			}
			myFile.println("commit;");
			resultFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getInsertValue(String _value) {
		String str_value = null;
		if (_value.equals("")) {
			str_value = "null";
		} else {
			str_value = "'" + convert(_value) + "'";
		}
		return str_value;
	}

	private String convert(String _str) {
		if (_str == null) {
			return "";
		}
		return _str.replaceAll("'", "''");
	}

}
/**************************************************************************
 * $RCSfile: QuerySelectTempletExportPanel.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:19:32 $
 *
 * $Log: QuerySelectTempletExportPanel.java,v $
 * Revision 1.4  2012/09/14 09:19:32  xch123
 * �ʴ��ֳ�����ͳһ����
 *
 * Revision 1.1  2012/08/28 09:41:14  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:32:08  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:42  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:51  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:59  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:11:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:48  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:52  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:36  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:41  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:29:10  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/09/20 05:08:31  xch
 * *** empty log message ***
 *
 * Revision 1.6  2007/03/07 02:01:56  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/05 09:59:28  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/02 05:02:50  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/02/10 08:59:36  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:20:38  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
