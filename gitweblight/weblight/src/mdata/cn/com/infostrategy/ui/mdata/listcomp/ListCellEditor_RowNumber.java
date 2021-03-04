/**************************************************************************
 * $RCSfile: ListCellEditor_RowNumber.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillListCheckedEvent;
import cn.com.infostrategy.ui.mdata.BillListCheckedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ListCellEditor_RowNumber extends AbstractCellEditor implements TableCellEditor, IBillCellEditor, ActionListener {

	private static final long serialVersionUID = 1L;
	private BillListPanel billList = null; //
	private JComponent rowNumberCompent = null; //控件!!
	private Object oldValue = null; //
	private Vector v_checkedListeners = new Vector();//袁江晓 20131029 修改 主要对勾选框时每行的序号添加监听事件
	private int rownum;

	public ListCellEditor_RowNumber() {
	}

	public Component getTableCellEditorComponent(JTable _table, Object _value, boolean _isSelected, int _row, int _column) { //
		oldValue = _value; //赋值!!
		rownum = _row;
		int li_frontRecords = 0; //
		if (billList != null) {
			li_frontRecords = billList.getFrontAllRecords(); //前面
		}
		String str_text = "" + (li_frontRecords + _row + 1); //
		if (billList != null || billList.isRowNumberChecked()) { //如果是勾选框
			if (str_text.length() == 1) {
				str_text = " " + str_text;
			}
			rowNumberCompent = new JCheckBox(str_text); //
			rowNumberCompent.setFocusable(false); //
			((JCheckBox) rowNumberCompent).setMargin(new Insets(0, 0, 0, 0)); //
			if (oldValue != null && oldValue instanceof RowNumberItemVO) { //如果是rowNumberVO,则取得原来的值!!
				((JCheckBox) rowNumberCompent).setSelected(((RowNumberItemVO) oldValue).isChecked()); //赋给控件!!
			} else { //
				((JCheckBox) rowNumberCompent).setSelected(false); //
			}
			((JCheckBox) rowNumberCompent).addActionListener(this); //
		} else {
			rowNumberCompent = new JLabel(str_text); //
		}
		rowNumberCompent.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, LookAndFeel.tableHeadLineClolr)); //
		return rowNumberCompent;
	}

	//取得控件中的值!!!
	public Object getCellEditorValue() {
		if (rowNumberCompent instanceof JCheckBox) { //如果是勾选框
			boolean isSelected = ((JCheckBox) rowNumberCompent).isSelected(); //是否选中???
			if (oldValue != null && oldValue instanceof RowNumberItemVO) {
				((RowNumberItemVO) oldValue).setChecked(isSelected); //如果是勾选中的
			} else {
				oldValue = new RowNumberItemVO(); //
				((RowNumberItemVO) oldValue).setChecked(isSelected); //
			}
			if (billList != null || billList.isRowNumberChecked()) {
				if (isSelected) {
					boolean istitlechecked = true;
					for (int i = 0; i < billList.getRowCount(); i++) {
						if (!billList.getRowNumberVO(i).isChecked()) {
							istitlechecked = false;
							break;
						}
					}
					billList.setTitleChecked(istitlechecked);
				} else {
					billList.setTitleChecked(false);
				}
				checkedChanged();
			}
			return oldValue; //
		} else {
			return oldValue; //
		}
	}

	//添加监听事件 袁江晓 20131029 添加
	private void checkedChanged() {
		BillVO billVO = billList.getBillVO(rownum);
		for (int i = 0; i < v_checkedListeners.size(); i++) {
			BillListCheckedListener listener = (BillListCheckedListener) v_checkedListeners.get(i);
			listener.onBillListChecked(new BillListCheckedEvent(rownum, billVO, this));
		}
	}

	public Vector getV_checkedListeners() {
		return v_checkedListeners;
	}

	public void setV_checkedListeners(Vector vCheckedListeners) {
		v_checkedListeners = vCheckedListeners;
	}

	public boolean isCellEditable(EventObject anEvent) {
		if (billList != null && billList.isRowNumberChecked()) { //如果是勾选框则是可以编辑的!!!
			if (anEvent instanceof MouseEvent) {
				return ((MouseEvent) anEvent).getClickCount() >= 1;
			} else {
				return true;
			}
		} else {
			return false; //如果是
		}
	}

	public javax.swing.JComponent getCompent() {
		return rowNumberCompent; //
	}

	public BillListPanel getBillList() {
		return billList;
	}

	public void setBillList(BillListPanel billList) {
		this.billList = billList;
	}

	public void actionPerformed(ActionEvent e) {
		this.stopCellEditing(); //
	}
}

/**************************************************************************
 * $RCSfile: ListCellEditor_RowNumber.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: ListCellEditor_RowNumber.java,v $
 * Revision 1.5  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:00  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:31:44  wanggang
 * restore
 *
 * Revision 1.2  2011/08/10 14:18:07  xch123
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
 * Revision 1.3  2010/02/08 11:01:57  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:58  xuchanghua
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
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:31  xch
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
 * Revision 1.2  2007/01/30 05:05:39  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
