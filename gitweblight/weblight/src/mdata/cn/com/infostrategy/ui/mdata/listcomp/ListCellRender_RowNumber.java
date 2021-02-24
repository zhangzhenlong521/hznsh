/**************************************************************************
 * $RCSfile: ListCellRender_RowNumber.java,v $  $Revision: 1.8 $  $Date: 2012/10/08 02:22:49 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.RowNumberItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ListCellRender_RowNumber implements TableCellRenderer { //extends JLabel

	private static final long serialVersionUID = 1L;
	private Color fontColor = new Color(75, 75, 75);
	private Color insertColor = new Color(0, 128, 0);
	private BillListPanel billList = null; //

	public Component getTableCellRendererComponent(JTable table, Object _value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
		int li_frontRecords = 0; //
		if (billList != null) {
			li_frontRecords = billList.getFrontAllRecords(); //前面
		}
		String str_text = "" + (li_frontRecords + rowIndex + 1); //
		JComponent compent = null; //
		if (billList == null || !billList.isRowNumberChecked()) { //如果是Label
			compent = new JLabel(str_text); //
			((JLabel) compent).setHorizontalAlignment(SwingConstants.CENTER);
		} else { //勾选框!
			if (str_text.length() == 1) {
				str_text = " " + str_text;
			}
			compent = new JCheckBox(str_text); //
			compent.setFocusable(false); //
			compent.setPreferredSize(new Dimension(12, 12));
			((JCheckBox) compent).setMargin(new Insets(0, 0, 0, 0)); //
		}
		compent.setOpaque(true); //不透明!!
		if (isSelected) {
			compent.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) table.getClientProperty("$rowbackground_" + rowIndex); //
			if (defColor != null) { //如果指定了背景颜色,则直接使用!!!
				compent.setBackground(defColor); //
			} else { //否则是奇偶变色!!!
				if (rowIndex % 2 == 0) {
					compent.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					compent.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}
		//		if (isSelected) {
		//			this.setBackground(table.getSelectionBackground());
		//		} else {
		//			this.setBackground(LookAndFeel.systembgcolor);
		//		}
		if (_value != null && (_value instanceof RowNumberItemVO)) { //如能是行号,也可能
			RowNumberItemVO valueVO = (RowNumberItemVO) _value; //
			if ("INIT".equals(valueVO.getState())) {
				compent.setForeground(fontColor);
			} else if ("INSERT".equals(valueVO.getState())) {
				compent.setForeground(insertColor);
			} else if ("UPDATE".equals(valueVO.getState())) {
				compent.setForeground(java.awt.Color.BLUE);
			} else {
				compent.setForeground(fontColor); //
			}
			compent.setToolTipText(_value.toString());
			if (compent instanceof JCheckBox) { //
				((JCheckBox) compent).setSelected(valueVO.isChecked()); //
			}
		} else {
			compent.setForeground(fontColor);
		}
		compent.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, LookAndFeel.tableHeadLineClolr)); //
		return compent;
	}

	public void setBillList(BillListPanel _billList) {
		billList = _billList; //
	}

	public void validate() {
	}

	public void revalidate() {
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	}

	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	}

}

/**************************************************************************
 * $RCSfile: ListCellRender_RowNumber.java,v $  $Revision: 1.8 $  $Date: 2012/10/08 02:22:49 $
 *
 * $Log: ListCellRender_RowNumber.java,v $
 * Revision 1.8  2012/10/08 02:22:49  xch123
 * *** empty log message ***
 *
 * Revision 1.7  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:00  Administrator
 * *** empty log message ***
 *
 * Revision 1.6  2011/10/10 06:31:44  wanggang
 * restore
 *
 * Revision 1.4  2011/08/10 14:18:07  xch123
 * *** empty log message ***
 *
 * Revision 1.3  2010/12/28 10:30:11  xch123
 * 12月28日提交
 *
 * Revision 1.2  2010/10/29 05:21:17  xch123
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
 * Revision 1.5  2009/05/04 06:45:50  chendu
 * *** empty log message ***
 *
 * Revision 1.4  2009/04/03 10:15:59  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/04/03 08:03:33  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/03/12 07:57:14  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2008/12/13 01:58:43  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/11/12 05:50:55  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:25  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/02/28 13:13:37  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:20  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:31  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/11/03 03:19:55  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:43  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:20  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 05:05:40  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
