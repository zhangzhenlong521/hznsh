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

	private boolean isHaveMacroTail = false; //�Ƿ���β��??

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
			if (isHaveMacroTail) { //���β���к����,��õ�֮!!!
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
		textarea.setFont(LookAndFeel.font);//win7������Ҫ����һ��,�����о�ݣ�����[2012-09-05]
		int[] li_selRows = table.getSelectedRows(); //
		int[] li_selCols = table.getSelectedColumns(); //
		if (hasFocus || isExistInArray(row, li_selRows) && isExistInArray(column, li_selCols)) { //����õ����,������ʾ�߿�!!
			textarea.setBorder(BorderFactory.createLineBorder(selBorderColor, 1)); //
		} else {
			textarea.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0)); //��ǰ�ڶ�������ð�����ͷ��,���ڸ��±߿�����Ŀհ׾ͺ���! ��������ʵ���ı����ǰ�1�����ص�,ֻ�����ۺ��ѿ�����!!
		}

		if (isSelected) {
			textarea.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) table.getClientProperty("$rowbackground_" + row); //
			if (defColor != null) { //���ָ���˱�����ɫ,��ֱ��ʹ��!!!
				textarea.setBackground(defColor); //
			} else { //��������ż��ɫ!!!
				if (row % 2 == 0) {
					textarea.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					textarea.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}

		if (itemVO.getListishtmlhref()) {//�����Ӷ����ı��������Ч���������ı�������html��ǩʵ�����ּ��»��ߺ͸ı�����
			Font font = LookAndFeel.font;//��ȡϵͳĬ������
			Map fontMap = font.getAttributes();
			fontMap.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);//������»���
			fontMap.put(TextAttribute.FOREGROUND, Color.BLUE);//������ɫΪ��ɫ

			font = font.deriveFont(fontMap);//���»������
			textarea.setFont(font);//��������
			return textarea;
		}

		if (itemVO.getListiseditable() != null && !itemVO.getListiseditable().equals("1")) {
			textarea.setForeground(new java.awt.Color(99, 99, 99)); //���ɱ༭
		} else {
			textarea.setForeground(LookAndFeel.systemLabelFontcolor); ////
		}

		if (value != null) {
			String str_foreColor = ((BillItemVO) value).getForeGroundColor(); //����ǰ����ɫ
			if (str_foreColor != null) { //���ǰ����ɫ��Ϊ��
				textarea.setForeground(getTBUtil().getColor(str_foreColor)); //����ǰ����ɫ
			}
			String str_backColor = ((BillItemVO) value).getBackGroundColor();
			if (str_backColor != null) { //���������ɫ��Ϊ�ա����/2014-11-13��
				textarea.setBackground(getTBUtil().getColor(str_backColor)); //���ñ�����ɫ
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
