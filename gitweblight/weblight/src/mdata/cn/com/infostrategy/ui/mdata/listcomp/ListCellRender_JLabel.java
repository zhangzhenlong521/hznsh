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
 * ������,�����һ�оͶ�Ӧһ���ö����ʵ��!!����˵JTable����ģʽ!!!
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
		if ("ͼƬѡ���".equals(itemVO.getItemtype()) && value != null) {
			if (value instanceof Icon) {
				JLabel label = new JLabel((Icon) value);
				label.setOpaque(true);
				setComponentBColor(label, table, isSelected, row);
				return label;
			} else {
				ImageIcon icon = UIUtil.getImage(value.toString());//�б�ֱ����Ⱦ��ͼƬ��by hm2013-5-30
				if (icon.getDescription() != null && icon.getDescription().equals("office_001.gif")) {//����Ǳ����ͼƬ������ȥ������ȡһ��
					ImageIcon icon2 = UIUtil.getImageFromServerRespath(value.toString());
					if (icon2 != null) {//�����Ϊ��
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
		if (hasFocus || isExistInArray(row, li_selRows) && isExistInArray(column, li_selCols)) { //����õ����,������ʾ�߿�!!
			label.setBorder(BorderFactory.createLineBorder(selBorderColor, 1)); //
		} else {
			label.setBorder(BorderFactory.createEmptyBorder()); //
		}

		if (isSelected) {
			label.setBackground(LookAndFeel.tablerowselectbgcolor);
		} else {
			Color defColor = (Color) table.getClientProperty("$rowbackground_" + row); //
			if (defColor != null) { //���ָ���˱�����ɫ,��ֱ��ʹ��!!!
				label.setBackground(defColor); //
			} else { //��������ż��ɫ!!!
				if (row % 2 == 0) {
					label.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					label.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}
		//�����������ʱ�������������ʾ��
		if (itemVO.getItemtype().equals(WLTConstants.COMP_DATE) || itemVO.getItemtype().equals(WLTConstants.COMP_DATETIME)) { //�ı��Ƿ������ʾ
			label.setHorizontalAlignment(JLabel.CENTER);
		}

		//��ǰ��������Ч�����и߾ͼ�С�ˣ���Ϊû������line-height,��Ϊbilllistpanel��Ĭ���и���22���أ���������Ҳ����Ϊ22����,Ϊʲô������и߲������ã�
		if (itemVO.getListishtmlhref()) {
			label.setText("<html><font  style=\"line-height:22px;color:#" + new TBUtil().convertColor(LookAndFeel.htmlrefcolor) + "\"><u>" + label.getText() + "</u></font></html>"); //�����Html��ʾ,�����»�����ʾ
			label.setVerticalAlignment(JLabel.TOP);
			return label; //��������
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
		} else if (itemVO.getItemtype().equals(WLTConstants.COMP_COMBOBOX)) {//�����������ؼ�������Ҫ�ж��б��Ƿ�ɱ༭�������Ƿ���ʾ������ѡ����ʾ����/2012-08-21��
			if (label.getText().equals(getTBUtil().getSysOptionStringValue("������ؼ�ѡ����ʾ��", ""))) {
				label.setText("");
			}
		}

		if (itemVO.getListiseditable() != null && !itemVO.getListiseditable().equals("1")) {
			label.setForeground(new java.awt.Color(99, 99, 99)); //���ɱ༭
		} else {
			label.setForeground(LookAndFeel.systemLabelFontcolor); //
		}

		if (value != null && value instanceof BillItemVO) {
			try {
				String str_foreColor = ((BillItemVO) value).getForeGroundColor(); //���д�����߼���ǰ��֪��ô���˸���!!������ɫ��ʽ����������!
				if (str_foreColor != null) { //���ǰ����ɫ��Ϊ��
					label.setForeground(getTBUtil().getColor(str_foreColor)); //����ǰ����ɫ
				}
				String str_backColor = ((BillItemVO) value).getBackGroundColor();
				if (str_backColor != null) { //���������ɫ��Ϊ�ա����/2014-11-13��
					label.setBackground(getTBUtil().getColor(str_backColor)); //���ñ�����ɫ
				}
			} catch (Exception exx) {
				System.err.println("����[" + itemVO.getItemkey() + "," + itemVO.getItemname() + "]�����쳣:" + exx.getClass().getName() + "!"); //
				exx.printStackTrace(); //
			}
		}

		//ִ���Զ���Ļ�����
		try {
			if (this.itemVO.getPub_Templet_1VO() != null) {
				String strRenderer = this.itemVO.getPub_Templet_1VO().getDefineRenderer();
				if (strRenderer != null && !strRenderer.trim().equals("")) {
					IDefineListCellRenderer defRenderer = (IDefineListCellRenderer) Class.forName(strRenderer).newInstance();
					defRenderer.defineRenderer(label, table, value, isSelected, hasFocus, row, column);
				}
			}

		} catch (Exception e) {
			System.err.println("ִ���Զ������������..." + e.getMessage());
			//���Դ��쳣..
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
			if (defColor != null) { //���ָ���˱�����ɫ,��ֱ��ʹ��!!!
				_component.setBackground(defColor); //
			} else { //��������ż��ɫ!!!
				if (row % 2 == 0) {
					_component.setBackground(LookAndFeel.table_bgcolor_odd); //
				} else {
					_component.setBackground(LookAndFeel.tablebgcolor); //
				}
			}
		}
	}
}
