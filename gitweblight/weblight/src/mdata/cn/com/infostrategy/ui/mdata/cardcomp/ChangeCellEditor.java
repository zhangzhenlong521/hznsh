/**************************************************************************
 * $RCSfile: ChangeCellEditor.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.cardcomp;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.listcomp.ListCPanel_Ref;

/**
 * 高级查询框中一行行的"并且/或者"等!!
 * @author xch
 *
 */
public class ChangeCellEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO[] templetItemVOs = null;

	private JTextField textField = null;

	private JComboBox comboBox = null;;

	private ListCPanel_Ref uiref = null;

	private ListCPanel_Ref uitime = null;

	private ListCPanel_Ref uidatetime = null;

	private JCheckBox checkBox = null;

	private JTextField number = null;

	private String str_type = null;

	private JPasswordField passwordField = null;

	private ListCPanel_Ref filePathPanel_List = null;

	private ListCPanel_Ref colorPanel_List = null;

	private ListCPanel_Ref imagePanel_list = null;

	private ListCPanel_Ref textAreaDetailPanel_List = null;

	private ListCPanel_Ref textAreaPanel_List = null;

	public ChangeCellEditor(Pub_Templet_1VO _templetVO) {
		this.templetItemVOs = _templetVO.getItemVos();
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		ComBoxItemVO vo = (ComBoxItemVO) table.getValueAt(row, 1);
		str_type = findType(vo.getId());

		if (str_type.equals("文本框")) {
			textField = new JTextField(value == null ? "" : value + "");
			return textField;
		} else if (str_type.equals("下拉框")) {
			comboBox = new JComboBox();
			ComBoxItemVO[] itemVos = findComBoxItemVO(vo.getId());
			for (int i = 0; i < itemVos.length; i++) {
				comboBox.addItem(itemVos[i]);
			}
			return comboBox;
		} else if (str_type.equals("参照")) {
			uiref = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			if (value instanceof RefItemVO) {
				uiref.setObject((RefItemVO) value);
			}
			return uiref;
		} else if (str_type.equals("日历")) {
			uidatetime = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			uidatetime.setObject(value);
			return uidatetime;
		} else if (str_type.equals("时间")) {
			uitime = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			uitime.setObject(value);
			return uitime;
		} else if (str_type.equals("勾选框")) {
			checkBox = new JCheckBox();
			checkBox.setHorizontalAlignment(SwingConstants.CENTER);
			if (value != null && value.toString().equals("Y")) {
				checkBox.setSelected(true);
			} else {
				checkBox.setSelected(false);
			}
			return checkBox;
		} else if (str_type.equals("密码框")) {
			passwordField = new JPasswordField(value == null ? "" : value + "");
			return passwordField;
		} else if (str_type.equals("文件选择框")) {
			filePathPanel_List = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			filePathPanel_List.setObject(value);
			return filePathPanel_List;
		} else if (str_type.equals("颜色")) {
			colorPanel_List = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			colorPanel_List.setObject(value);
			return colorPanel_List;
		} else if (str_type.equals("大文本框")) {
			textAreaDetailPanel_List = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			textAreaDetailPanel_List.setObject(value);
			return textAreaDetailPanel_List;
		} else if (str_type.equals("多行文本框")) {
			textAreaPanel_List = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			textAreaPanel_List.setObject(value);
			return textAreaPanel_List;
		} else if (str_type.equals("图片选择框")) {
			imagePanel_list = new ListCPanel_Ref(templetItemVOs[findPos(vo.getId())], null, null);
			imagePanel_list.setObject(value);
			return imagePanel_list;
		} else if (str_type.equals("数字框")) {
			number = new JTextField(value == null ? "" : value + "");
			number.setHorizontalAlignment(JTextField.RIGHT);
			number.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					// TODO Auto-generated method stub

				}

				public void keyReleased(KeyEvent e) {
					// TODO Auto-generated method stub

				}

				public void keyTyped(KeyEvent e) {
					// TODO Auto-generated method stub

					Object o = e.getSource();
					Pattern NUM_PATTERN = Pattern.compile("(\\-|\\.|\\d)*");

					if (o == number) {
						String str = number.getText();
						Matcher m = NUM_PATTERN.matcher(str);
						if (!m.matches()) {
							e.consume();
							str = str.substring(0, str.length() - 1);
							number.setText(str);
						}
					}
				}

			});
			return number;
		} else {
			return new JTextField(value + "");
		}
	}

	private int findPos(String _key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_key)) {
				return i;
			}
		}
		return -1;
	}

	private String findType(String _key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_key)) {
				return templetItemVOs[i].getItemtype();
			}
		}
		return null;
	}

	private ComBoxItemVO[] findComBoxItemVO(String _key) {
		for (int i = 0; i < templetItemVOs.length; i++) {
			if (templetItemVOs[i].getItemkey().equals(_key)) {
				return templetItemVOs[i].getComBoxItemVos();
			}
		}
		return null;
	}

	public Object getCellEditorValue() {
		if (str_type == null) {
			return null;
		}
		if (str_type.equals("文本框")) {
			return textField.getText();
		} else if (str_type.equals("下拉框")) {
			ComBoxItemVO vo = (ComBoxItemVO) comboBox.getSelectedItem();
			return vo;
		} else if (str_type.equals("参照")) {
			return (RefItemVO) uiref.getObject();
		} else if (str_type.equals("日历")) {
			return uidatetime.getObject();
		} else if (str_type.equals("时间")) {
			return uitime.getObject();
		} else if (str_type.equals("勾选框")) {
			return checkBox.isSelected() ? "Y" : "N";
		} else if (str_type.equals("数字框")) {
			return number.getText();
		} else if (str_type.equals("密码框")) {
			return passwordField.getText();
		} else if (str_type.equals("文件选择框")) {
			return filePathPanel_List.getObject();
		} else if (str_type.equals("颜色")) {
			return colorPanel_List.getObject();
		} else if (str_type.equals("大文本框")) {
			return textAreaDetailPanel_List.getObject();
		} else if (str_type.equals("多行文本框")) {
			return textAreaPanel_List.getObject();
		} else if (str_type.equals("图片选择框")) {
			return imagePanel_list.getObject();
		} else {
			return null;
		}
	}

	public boolean isCellEditable(EventObject evt) {
		if (evt instanceof MouseEvent) {
			return ((MouseEvent) evt).getClickCount() >= 2;
		}
		return true;
	}

}
/**************************************************************************
 * $RCSfile: ChangeCellEditor.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: ChangeCellEditor.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:59  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:46  wanggang
 * restore
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
 * Revision 1.1  2009/02/20 06:10:45  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:22  xuchanghua
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
 * Revision 1.3  2007/09/20 05:08:24  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:14:31  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
