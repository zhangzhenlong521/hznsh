/**************************************************************************
 * $RCSfile: TempletCellRender.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.styletemplet.config;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

public class TempletCellRender  implements TableCellRenderer
{
	JTextArea field = new JTextArea();
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int r, int c)
    {
		if(c!=3)
		{
			field.setText(value.toString());
			field.setForeground(Color.GRAY);
			if(((Boolean)table.getValueAt(r, 3)).booleanValue()&&c==0)
			{
				field.setForeground(Color.BLACK);
			}
		
			field.setBackground(new Color(240,240,240));
			return field;
		}
		JCheckBox box = new JCheckBox();
		box.setForeground(Color.gray);
		box.setSelected(((Boolean)value).booleanValue());
		
		return box;
    }

}
/**************************************************************************
 * $RCSfile: TempletCellRender.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:57 $
 *
 * $Log: TempletCellRender.java,v $
 * Revision 1.4  2012/09/14 09:22:57  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:41:01  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:48  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:16  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:32:06  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:18  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:57  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:13:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:57  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:33  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:27  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:24  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:34  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:46  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:21  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:23:45  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/