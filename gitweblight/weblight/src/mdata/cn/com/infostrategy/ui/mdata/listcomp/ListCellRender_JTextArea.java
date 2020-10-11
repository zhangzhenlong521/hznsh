/**************************************************************************
 * $RCSfile: ListCellRender_JTextArea.java,v $  $Revision: 1.12 $  $Date: 2013/02/28 06:14:47 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.io.Serializable;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;

public class ListCellRender_JTextArea implements TableCellRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private Pub_Templet_1_ItemVO itemVO;

	private TBUtil tBUtil = null;
	private JTextArea textarea = new JTextArea(); //
	private Color selBorderColor = new Color(99, 130, 191); //

	private boolean isHaveMacroTail = false; //是否有尾巴??

	int li_aa = 0; //

	public ListCellRender_JTextArea(Pub_Templet_1_ItemVO _itemVO) {
		this(_itemVO, false); //
	}

	public ListCellRender_JTextArea(Pub_Templet_1_ItemVO _itemVO, boolean _isHaveMacroTail) {
		this.itemVO = _itemVO;
		textarea.setUI(new BasicTextAreaUI());
		textarea.setLineWrap(true); //
		textarea.setWrapStyleWord(true); //
		textarea.setAlignmentY((float) 0.5); //
		textarea.setAlignmentX((float) 0.5); //
		//		textarea.setMargin(new Insets(15,0,0,0));  //
		isHaveMacroTail = _isHaveMacroTail; //
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		if (value == null) {
			textarea.setText(""); //
		} else {
			String str_value = value.toString(); //
			if (isHaveMacroTail) { //如果尾部有宏代码,则裁掉之!!!
				int li_pos_1 = str_value.lastIndexOf("#@$"); //
				int li_pos_2 = str_value.lastIndexOf("$@#"); // 
				if (str_value.endsWith("$@#") && li_pos_1 > 0 && li_pos_2 > 0 && (li_pos_2 - li_pos_1) < 20) {
					textarea.setText(str_value.substring(0, li_pos_1)); //
				} else {
					textarea.setText(str_value); //
				}
			} else {
				textarea.setText(str_value); //
			}
		}
		textarea.setFont(LookAndFeel.font);//win7下面需要设置一把,否则有锯齿！郝明[2012-09-05]
		int[] li_selRows = table.getSelectedRows(); //
		int[] li_selCols = table.getSelectedColumns(); //
		if (hasFocus || isExistInArray(row, li_selRows) && isExistInArray(column, li_selCols)) { //如果得到光标,则有显示边框!!
			textarea.setBorder(BorderFactory.createLineBorder(selBorderColor, 1)); //
		} else {
			textarea.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); //以前第二行老是冒出半个头来,现在改下边框是面的空白就好了! 但这样其实比文本框是矮1个像素的,只是肉眼很难看出来!!
		}

		if (isSelected) {
			textarea.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) table.getClientProperty("$rowbackground_" + row); //
			if (defColor != null) { //如果指定了背景颜色,则直接使用!!!
				textarea.setBackground(defColor); //
			} else { //否则是奇偶变色!!!
				if (row % 2 == 0) {
					textarea.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					textarea.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}

		if (itemVO.getListishtmlhref()) {//李春娟添加多行文本框的链接效果，多行文本框不能用html标签实现文字加下划线和改变字体
			Font font = LookAndFeel.font;//先取系统默认字体
			Map fontMap = font.getAttributes();
			fontMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);//字体加下划线
			fontMap.put(TextAttribute.FOREGROUND, Color.BLUE);//字体颜色为蓝色

			font = font.deriveFont(fontMap);//重新获得字体
			textarea.setFont(font);//设置字体
			return textarea;
		}

		if (itemVO.getListiseditable() != null && !itemVO.getListiseditable().equals("1")) {
			textarea.setForeground(new java.awt.Color(99, 99, 99)); //不可编辑
		} else {
			textarea.setForeground(LookAndFeel.systemLabelFontcolor); ////
		}

		if (value != null) {
			String str_foreColor = ((BillItemVO) value).getForeGroundColor(); //设置前景颜色
			if (str_foreColor != null) { //如果前景颜色不为空
				textarea.setForeground(getTBUtil().getColor(str_foreColor)); //设置前景颜色
			}
			String str_backColor = ((BillItemVO) value).getBackGroundColor();
			if (str_backColor != null) { //如果背景颜色不为空【李春娟/2014-11-13】
				textarea.setBackground(getTBUtil().getColor(str_backColor)); //设置背景颜色
			}
		}

		return textarea;
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
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil;
	}

}
