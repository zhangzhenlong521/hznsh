/**************************************************************************
 * $RCSfile: ListCellRender_FileChoose.java,v $  $Revision: 1.5 $  $Date: 2012/10/08 02:22:49 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;

/**
 * 文件选择框在列表中的绘制器
 * @author xch
 *
 */
public class ListCellRender_FileChoose implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO itemVO;

	int li_aa = 0; //

	private TBUtil tBUtil = null;

	public ListCellRender_FileChoose(Pub_Templet_1_ItemVO _itemVO) {
		this.itemVO = _itemVO;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel(); //
		label.setHorizontalAlignment(JLabel.CENTER); //

		try {
			RefItemVO refvo = (RefItemVO) value;
			if (refvo != null) {
				label.setText("<html><font color=blue><u>" + refvo.getName() + "</u></font></html>"); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}

		label.setOpaque(true); //
		if (isSelected) {
			label.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) table.getClientProperty("$rowbackground_" + row); //
			if (defColor != null) { //如果指定了背景颜色,则直接使用!!!
				label.setBackground(defColor); //
			} else { //否则是奇偶变色!!!
				if (row % 2 == 0) {
					label.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					label.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}

		if (value != null) {
			String str_foreColor = ((BillItemVO) value).getForeGroundColor(); //设置前景颜色
			if (str_foreColor != null) { //如果前景颜色不为空
				label.setForeground(getTBUtil().getColor(str_foreColor)); //设置前景颜色
			}
			String str_backColor = ((BillItemVO) value).getBackGroundColor();
			if (str_backColor != null) { //如果背景颜色不为空【李春娟/2014-11-13】
				label.setBackground(getTBUtil().getColor(str_backColor)); //设置背景颜色
			}
		}

		//		if (isSelected) {
		//			label.setBackground(new Color(184, 207, 229));//
		//		} else {
		//			label.setBackground(Color.WHITE); //
		//		}

		return label; //立即返回
	}

	private TBUtil getTBUtil() {
		return TBUtil.getTBUtil(); //
	}

}
