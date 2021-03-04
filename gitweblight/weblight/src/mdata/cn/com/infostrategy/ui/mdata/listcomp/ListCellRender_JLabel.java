/**************************************************************************
 * $RCSfile: ListCellRender_JLabel.java,v $  $Revision: 1.11 $  $Date: 2012/10/08 02:22:49 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 绘制器,表格中一列就对应一个该对象的实例!!所以说JTable是列模式!!!
 * @author xch
 *
 */
public class ListCellRender_JLabel extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO itemVO;
	private TBUtil tBUtil = null;
	private Color selBorderColor = new Color(99, 130, 191); //

	int li_aa = 0; //

	public ListCellRender_JLabel(Pub_Templet_1_ItemVO _itemVO) {
		this.itemVO = _itemVO;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		//BillListModel billListModel = (BillListModel) table.getModel(); //
		if ("图片选择框".equals(itemVO.getItemtype()) && value != null) {
			if (value instanceof Icon) {
				JLabel label = new JLabel((Icon) value);
				label.setOpaque(true);
				setComponentBColor(label, table, isSelected, row);
				return label;
			} else {
				ImageIcon icon = UIUtil.getImage(value.toString());//列表直接渲染出图片。by hm2013-5-30
				if (icon.getDescription() != null && icon.getDescription().equals("office_001.gif")) {//如果是报错的图片，就再去服务器取一下
					ImageIcon icon2 = UIUtil.getImageFromServerRespath(value.toString());
					if (icon2 != null) {//如果不为空
						icon = icon2;
					}
				}
				JLabel label = new JLabel(icon);
				label.setOpaque(true);
				setComponentBColor(label, table, isSelected, row);
				return label;
			}
		}
		JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		label.setFont(LookAndFeel.font); //

		int[] li_selRows = table.getSelectedRows(); //
		int[] li_selCols = table.getSelectedColumns(); //
		if (hasFocus || isExistInArray(row, li_selRows) && isExistInArray(column, li_selCols)) { //如果得到光标,则有显示边框!!
			label.setBorder(BorderFactory.createLineBorder(selBorderColor, 1)); //
		} else {
			label.setBorder(BorderFactory.createEmptyBorder()); //
		}

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
		//如果是日历或时间类型则居中显示！
		if (itemVO.getItemtype().equals(WLTConstants.COMP_DATE) || itemVO.getItemtype().equals(WLTConstants.COMP_DATETIME)) { //文本是否居中显示
			label.setHorizontalAlignment(JLabel.CENTER);
		}

		//以前加上链接效果，行高就减小了，因为没有设置line-height,因为billlistpanel中默认行高是22像素，所以这里也设置为22像素,为什么这里的行高不起作用？
		if (itemVO.getListishtmlhref()) {
			label.setText("<html><font  style=\"line-height:22px;color:#" + new TBUtil().convertColor(LookAndFeel.htmlrefcolor) + "\"><u>" + label.getText() + "</u></font></html>"); //如果是Html显示,则用下划线显示
			label.setVerticalAlignment(JLabel.TOP);
			return label; //立即返回
		}

		if (itemVO.getItemtype().equals(WLTConstants.COMP_NUMBERFIELD)) {
			label.setHorizontalAlignment(JLabel.RIGHT);
		} else if (itemVO.getItemtype().equals(WLTConstants.COMP_PASSWORDFIELD)) {
			label.setHorizontalAlignment(JLabel.RIGHT);
			if (label.getText() != null) {
				int li_length = label.getText().length(); //
				String str_password = "";
				for (int i = 0; i < li_length; i++) {
					str_password = str_password + "*"; //
				}
				label.setText(str_password); //
			}
		} else if (itemVO.getItemtype().equals(WLTConstants.COMP_COMBOBOX)) {//如果是下拉框控件，则需要判断列表是否可编辑来设置是否显示下拉框选择提示语【李春娟/2012-08-21】
			if (label.getText().equals(getTBUtil().getSysOptionStringValue("下拉框控件选择提示语", ""))) {
				label.setText("");
			}
		}

		if (itemVO.getListiseditable() != null && !itemVO.getListiseditable().equals("1")) {
			label.setForeground(new java.awt.Color(99, 99, 99)); //不可编辑
		} else {
			label.setForeground(LookAndFeel.systemLabelFontcolor); //
		}

		if (value != null && value instanceof BillItemVO) {
			try {
				String str_foreColor = ((BillItemVO) value).getForeGroundColor(); //这行代码的逻辑以前不知怎么被人改了!!导致颜色公式都不能用了!
				if (str_foreColor != null) { //如果前景颜色不为空
					label.setForeground(getTBUtil().getColor(str_foreColor)); //设置前景颜色
				}
				String str_backColor = ((BillItemVO) value).getBackGroundColor();
				if (str_backColor != null) { //如果背景颜色不为空【李春娟/2014-11-13】
					label.setBackground(getTBUtil().getColor(str_backColor)); //设置背景颜色
				}
			} catch (Exception exx) {
				System.err.println("绘制[" + itemVO.getItemkey() + "," + itemVO.getItemname() + "]发生异常:" + exx.getClass().getName() + "!"); //
				exx.printStackTrace(); //
			}
		}

		//执行自定义的绘制器
		try {
			if (this.itemVO.getPub_Templet_1VO() != null) {
				String strRenderer = this.itemVO.getPub_Templet_1VO().getDefineRenderer();
				if (strRenderer != null && !strRenderer.trim().equals("")) {
					IDefineListCellRenderer defRenderer = (IDefineListCellRenderer) Class.forName(strRenderer).newInstance();
					defRenderer.defineRenderer(label, table, value, isSelected, hasFocus, row, column);
				}
			}

		} catch (Exception e) {
			System.err.println("执行自定义绘制器出错..." + e.getMessage());
			//忽略此异常..
		}

		return label;
	}

	private boolean isExistInArray(int _item, int[] _arrays) {
		for (int i = 0; i < _arrays.length; i++) {
			if (_arrays[i] == _item) {
				return true;
			}
		}
		return false; //
	}

	private TBUtil getTBUtil() {
		return TBUtil.getTBUtil(); //
	}

	private void setComponentBColor(JComponent _component, JTable table, boolean isSelected, int row) {
		if (isSelected) {
			_component.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) table.getClientProperty("$rowbackground_" + row); //
			if (defColor != null) { //如果指定了背景颜色,则直接使用!!!
				_component.setBackground(defColor); //
			} else { //否则是奇偶变色!!!
				if (row % 2 == 0) {
					_component.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					_component.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}
	}
}
