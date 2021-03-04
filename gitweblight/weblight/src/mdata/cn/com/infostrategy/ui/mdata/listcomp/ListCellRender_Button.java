/**************************************************************************
 * $RCSfile: ListCellRender_Button.java,v $  $Revision: 1.6 $  $Date: 2012/09/14 09:22:57 $
 **************************************************************************/
package cn.com.infostrategy.ui.mdata.listcomp;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.UIUtil;

public class ListCellRender_Button implements TableCellRenderer {

	private static final long serialVersionUID = 1L;

	private Pub_Templet_1_ItemVO itemVO;

	//private TBUtil tBUtil = null;

	int li_aa = 0; //

	public ListCellRender_Button(Pub_Templet_1_ItemVO _itemVO) {
		this.itemVO = _itemVO;
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel label = new JLabel(); //
		label.setHorizontalAlignment(JLabel.CENTER); //
		if ("�й�����".equals(System.getProperty("LICENSEDTO"))) { //��ʱΪ���й���������ͼƬЧ��! Ϊ�˲�Ӱ��������Ŀ,��ʱд������!
			label.setIcon(UIUtil.getImage("zt_list_ope.gif")); //
		} else {
			label.setText("<html><font color=blue><u>" + itemVO.getItemname() + "</u></font></html>"); //�����Html��ʾ,�����»�����ʾ
		}
		label.setOpaque(true); //
		if (isSelected) {
			label.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			if (row % 2 == 0) {
				label.setBackground(LookAndFeel.table_bgcolor_odd); //
			} else {
				label.setBackground(LookAndFeel.tablebgcolor); //
			}
		}

		//		if (isSelected) {
		//			label.setBackground(new Color(184, 207, 229));//
		//		} else {
		//			label.setBackground(Color.WHITE); //
		//		}
		return label; //��������
	}

}
