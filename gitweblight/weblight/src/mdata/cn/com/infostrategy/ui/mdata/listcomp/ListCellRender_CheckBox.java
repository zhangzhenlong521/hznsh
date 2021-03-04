/**************************************************************************
 * $RCSfile: ListCellRender_CheckBox.java,v $  $Revision: 1.5 $  $Date: 2012/10/08 02:22:49 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;

public class ListCellRender_CheckBox extends JCheckBox implements TableCellRenderer {

	private static final long serialVersionUID = 1L;
	private TBUtil tBUtil = null;

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {
		// 'value' is value contained in the cell located at
		// (rowIndex, vColIndex)
		JCheckBox checkBox = new JCheckBox();

		checkBox.setHorizontalAlignment(SwingConstants.CENTER);
		checkBox.setForeground(LookAndFeel.systemLabelFontcolor); //
		if (isSelected) {
			checkBox.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			if (rowIndex % 2 == 0) {
				checkBox.setBackground(LookAndFeel.table_bgcolor_odd); //
			} else {
				checkBox.setBackground(LookAndFeel.tablebgcolor); //
			}
		}
		//		if (isSelected) {
		//			this.setBackground(table.getSelectionBackground());
		//		} else {
		//			this.setBackground(LookAndFeel.systembgcolor);
		//		}

		if (hasFocus) {
			// this cell is the anchor and the table has the focus
		}

		// Configure the component with the specified value
		if (value != null && value.toString().equals("Y")) {
			checkBox.setSelected(true);
		} else {
			checkBox.setSelected(false);
		}
		//		if (value != null) {  //好象勾选框的前景设置没有效果,所以不设了!
		//			String str_foreColor = ((BillItemVO) value).getForeGroundColor(); //设置前景颜色
		//			if (str_foreColor != null) { //如果前景颜色不为空
		//				checkBox.setForeground(getTBUtil().getColor(str_foreColor)); //设置前景颜色
		//			}
		//		}

		// Since the renderer is a component, return itself
		return checkBox;

	}

	// The following methods override the defaults for performance reasons
	public void validate() {
	}

	public void revalidate() {
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
	}

	public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil;
	}

}
/**************************************************************************
 * $RCSfile: ListCellRender_CheckBox.java,v $  $Revision: 1.5 $  $Date: 2012/10/08 02:22:49 $
 *
 * $Log: ListCellRender_CheckBox.java,v $
 * Revision 1.5  2012/10/08 02:22:49  xch123
 * *** empty log message ***
 *
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:00  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:44  wanggang
 * restore
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
 * Revision 1.6  2009/10/14 08:10:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.5  2009/10/13 07:13:41  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2009/05/04 06:45:50  chendu
 * *** empty log message ***
 *
 * Revision 1.3  2009/04/07 02:55:22  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/04/03 06:41:21  xuchanghua
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
 * Revision 1.2  2007/01/30 05:05:40  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
