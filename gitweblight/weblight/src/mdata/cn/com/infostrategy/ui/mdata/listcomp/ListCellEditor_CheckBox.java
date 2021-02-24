/**************************************************************************
 * $RCSfile: ListCellEditor_CheckBox.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;

public class ListCellEditor_CheckBox extends DefaultCellEditor implements IBillCellEditor {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO itemVO = null;
	private JCheckBox checkBox = null;
	private StringItemVO oldStrItemVO = null;

	public ListCellEditor_CheckBox(JCheckBox checkBox, Pub_Templet_1_ItemVO _itemvo) {
		super(checkBox);
		this.itemVO = _itemvo;
	}

	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (value != null) {
			oldStrItemVO = new StringItemVO(value.toString());
		}
		checkBox = (JCheckBox) super.getTableCellEditorComponent(table, value, isSelected, row, column);
		checkBox.setBackground(table.getSelectionBackground()); //背景颜色是表格选择的背景颜色!!
		checkBox.setHorizontalAlignment(SwingConstants.CENTER); //

		if (oldStrItemVO != null && oldStrItemVO.getStringValue().equals("Y")) {
			checkBox.setSelected(true);
		} else {
			checkBox.setSelected(false);
		}

		//取得行号状态!!!
		if (itemVO.getListiseditable() == null || itemVO.getListiseditable().equals("1") || itemVO.getListiseditable().equals("2") || itemVO.getListiseditable().equals("3")) {
			checkBox.setEnabled(true);
		} else {
			checkBox.setEnabled(false); //
		}

		return checkBox;
	}

	public Object getCellEditorValue() {
		if (oldStrItemVO == null) {
			if (checkBox.isSelected()) {
				StringItemVO newItemVO = new StringItemVO("Y");
				newItemVO.setValueChanged(true); //
				return newItemVO;
			} else {
				StringItemVO newItemVO = new StringItemVO("N");
				newItemVO.setValueChanged(true); //
				return newItemVO;
			}
		} else { //如果原来有值
			if (checkBox.isSelected()) {
				if (!oldStrItemVO.getStringValue().equals("Y")) {
					oldStrItemVO.setValueChanged(true);
				}
				oldStrItemVO.setStringValue("Y");
			} else {
				if (!oldStrItemVO.getStringValue().equals("N")) {
					oldStrItemVO.setValueChanged(true);
				}
				oldStrItemVO.setStringValue("N");
			}
			return oldStrItemVO;
		}
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (itemVO.getListiseditable() != null && !itemVO.getListiseditable().equals("1")) {
			return false;
		} else {
			if (anEvent instanceof MouseEvent) {
				return ((MouseEvent) anEvent).getClickCount() >= 1;
			} else {
				return true;
			}
		}
	}

	public javax.swing.JComponent getCompent() {
		return checkBox;
	}
}
/**************************************************************************
 * $RCSfile: ListCellEditor_CheckBox.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: ListCellEditor_CheckBox.java,v $
 * Revision 1.5  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:00  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:31:44  wanggang
 * restore
 *
 * Revision 1.2  2011/08/10 10:55:20  xch123
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2010/02/08 11:01:57  sunfujun
 * *** empty log message ***
 *
 * Revision 1.2  2009/12/02 08:38:36  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/10/15 01:36:17  kuangli
 * *** empty log message ***
 *
 * Revision 1.2  2009/10/14 08:04:23  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:24  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/06/19 07:14:04  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:30  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:43  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:20  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/30 10:00:06  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/28 11:23:02  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:05:40  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
