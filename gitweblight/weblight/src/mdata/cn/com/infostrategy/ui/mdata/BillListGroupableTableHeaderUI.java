package cn.com.infostrategy.ui.mdata;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.Pub_Templet_1VO;
import cn.com.infostrategy.to.mdata.Pub_Templet_1_ItemVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.ImageIconFactory;

public class BillListGroupableTableHeaderUI extends BasicTableHeaderUI {

	private Pub_Templet_1VO templet_1vo = null; //
	private Pub_Templet_1_ItemVO[] templet_1_itemvos = null;
	private TBUtil tBUtil = null;

	public BillListGroupableTableHeaderUI(Pub_Templet_1VO _templet_1vo) {
		super(); //
		this.templet_1vo = _templet_1vo; //
		this.templet_1_itemvos = _templet_1vo.getItemVos(); //������ϸ
	}

	public void paint(Graphics g, JComponent c) {
		Rectangle clipBounds = g.getClipBounds(); //ȡ�ó�����
		if (header.getColumnModel() == null) {
			return;
		}

		((BillListGroupableTableHeader) header).setColumnMargin(); //
		int column = 0;
		Dimension size = header.getSize();
		Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
		Hashtable h = new Hashtable();
		int columnMargin = header.getColumnModel().getColumnMargin();
		Enumeration enumeration = header.getColumnModel().getColumns(); //�õ����е���,���������е���
		while (enumeration.hasMoreElements()) {
			cellRect.height = size.height;
			cellRect.y = 0;
			TableColumn aColumn = (TableColumn) enumeration.nextElement(); //�Ե�ĳһ��
			Pub_Templet_1_ItemVO itemVO = getItemVO(aColumn.getIdentifier().toString()); //
			Color[] colors = geBgColor(aColumn.getIdentifier().toString()); //
			Enumeration cGroups = ((BillListGroupableTableHeader) header).getColumnGroups(aColumn);
			if (cGroups != null) {
				int li_index = 0;
				int groupHeight = 0;
				while (cGroups.hasMoreElements()) { //�����ɸ��п�ʼ�����������
					BillListGroupColumn cGroup = (BillListGroupColumn) cGroups.nextElement(); //
					//System.out.println("����:" + cGroup.getHeaderValue());
					Rectangle groupRect = (Rectangle) h.get(cGroup); //
					if (groupRect == null) {
						groupRect = new Rectangle(cellRect);
						Dimension d = cGroup.getSize(header.getTable());
						groupRect.width = d.width; //
						groupRect.height = d.height; //
						h.put(cGroup, groupRect); //
					}

					groupRect.width = groupRect.width; //
					li_index = li_index + 1; //��һ����
					Color groupbgcolor = null; //
					if (colors != null) {
						if (colors.length > li_index) //����ɫ����ĳ��ȴ��ڸ���ʱ!
						{
							groupbgcolor = colors[colors.length - li_index]; ////
						}
					}

					paintCell(g, groupRect, cGroup, groupbgcolor); //���ϲ��ı�ͷ
					groupHeight = groupHeight + groupRect.height;
					cellRect.height = size.height - groupHeight;
					cellRect.y = groupHeight;
				}
			}

			cellRect.width = aColumn.getWidth(); // + columnMargin;  //ǧ���ܼӿհױ߿�,һ�ؾͻ���ִ�λ����

			if (cellRect.intersects(clipBounds)) {
				Color bgColor = null;
				if (colors != null) {
					bgColor = colors[0];
				}
				paintCell(g, cellRect, column, bgColor, itemVO); //����ײ����!!!
			}

			cellRect.x = cellRect.x + cellRect.width;
			column++;
		}
	}

	private Pub_Templet_1_ItemVO getItemVO(String _itemKey) {
		for (int i = 0; i < templet_1_itemvos.length; i++) {
			if (templet_1_itemvos[i].getItemkey().equals(_itemKey)) {
				return templet_1_itemvos[i];
			}
		}
		return null;
	}

	/**
	 * ���ϲ��ı�ͷ
	 * @param g
	 * @param cellRect
	 * @param cGroup
	 */
	private void paintCell(Graphics g, Rectangle cellRect, BillListGroupColumn cGroup, Color _bgcolor) //�����,��ɫ��
	{
		TableCellRenderer renderer = cGroup.getHeaderRenderer(); //
		Component component = renderer.getTableCellRendererComponent(header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);

		Hashtable ht_convert = ((BillListGroupableTableHeader) header).getColValueConverter(); //
		String str_headervalue = (String) cGroup.getHeaderValue(); //
		if (ht_convert.get(str_headervalue) != null) {
			str_headervalue = (String) ht_convert.get(str_headervalue); //
		}

		JLabel aJLabel = new JLabel(str_headervalue, SwingConstants.CENTER); //����������ͷ!!�ؼ�
		aJLabel.setOpaque(true);
		aJLabel.setFont(new Font("System", 0, 12)); //
		if (_bgcolor != null) {
			aJLabel.setBackground(_bgcolor);
		}

		aJLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder")); //���ñ߿�
		component = aJLabel;
		rendererPane.add(component);
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true); //�ؼ�����һ��,��������ʵ�ֱ�ͷ�ϲ�Ч����!
	}

	/**
	 * ����ײ��,Ҳ��������ʵ����!!
	 * @param g
	 * @param cellRect
	 * @param nIndecolumncolor 
	 */
	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex, Color _bgcolor, Pub_Templet_1_ItemVO itemVO) //�����,��ɫ��
	{
		TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		String str_key = aColumn.getIdentifier().toString(); //
		//System.out.println("�е�Ψһ��˵��:" + aColumn.getIdentifier());  //
		TableCellRenderer renderer = aColumn.getHeaderRenderer();
		Component component = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
		String str_text = (String) aColumn.getHeaderValue(); //
		String original_str_text = str_text; //
		String str_tooltiptext = str_text; //

		JLabel aJLabel = new JLabel("", SwingConstants.CENTER); //����������ͷ!!�ؼ�
		aJLabel.setOpaque(true);
		aJLabel.setFont(new Font("System", 0, 12)); //

		//Color color = geBgColor(aColumn.getIdentifier().toString()); //
		if (_bgcolor != null) {
			aJLabel.setBackground(_bgcolor);
		}

		if (itemVO.isNeedSave()) {
			if (itemVO.isCanSave()) {
				str_text = "+" + str_text; //
				str_tooltiptext = str_tooltiptext + " [" + itemVO.getPub_Templet_1VO().getSavedtablename() + "]"; //
			} else {
				str_text = "+" + str_text; //
				str_tooltiptext = str_tooltiptext + " [" + itemVO.getPub_Templet_1VO().getSavedtablename() + "],�ñ�û�и���!!���ᱣ��ʧ��!!"; //
			}

		}

		if (itemVO.isViewColumn()) {
			str_text = "��" + str_text; //
			str_tooltiptext = "[" + itemVO.getPub_Templet_1VO().getTablename() + "." + itemVO.getItemkey() + "]" + str_tooltiptext;
		} else {
			str_tooltiptext = "[" + itemVO.getItemkey() + "]" + str_tooltiptext; //
		}
		if (itemVO.isPrimaryKey()) {
			aJLabel.setForeground(java.awt.Color.BLUE); //
		} else if ("Y".equals(itemVO.getIsmustinput2())) {
			if (isAdmin()) {
				aJLabel.setForeground(new java.awt.Color(38, 147, 255)); // 
			}
		} else {
			aJLabel.setForeground(java.awt.Color.BLACK); //
		}

		if (itemVO.isNeedSave()) {
			if (isAdmin()) {
				if (!itemVO.isCanSave()) {
					aJLabel.setForeground(java.awt.Color.RED); //
				}
			}
		}

		// ����ǹ���Ա������ʾ��ʾ��Ϣ.
		if (isAdmin()) {
			aJLabel.setText(str_text); //
			aJLabel.setToolTipText(str_tooltiptext);
		} else {
			aJLabel.setText(original_str_text); //
		}

		BillListPanel billListPanel = ((BillListModel) header.getTable().getModel()).getBillListPanel(); //
		if (str_key.equals(billListPanel.str_currsortcolumnkey)) {
			if (billListPanel.li_currsorttype == 1) {
				aJLabel.setIcon(ImageIconFactory.getUpArrowIcon());
			} else if (billListPanel.li_currsorttype == -1) {
				aJLabel.setIcon(ImageIconFactory.getDownArrowIcon()); //
			} else {
				aJLabel.setIcon(null);
			}
		} else {
			aJLabel.setIcon(null);
		}

		aJLabel.updateUI(); //
		aJLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder")); // ���ñ߿�
		component = aJLabel;
		rendererPane.add(component);
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true); //�ؼ�����һ��,��������ʵ�ֱ�ͷ�ϲ�Ч����!
	}

	private Color[] geBgColor(String _itemKey) {
		for (int i = 0; i < templet_1_itemvos.length; i++) {
			if (templet_1_itemvos[i].getItemkey().equals(_itemKey)) {
				if (templet_1_itemvos[i].getShowbgcolor() != null && !templet_1_itemvos[i].getShowbgcolor().equals("")) {
					String[] str_colors = getTBUtil().split(templet_1_itemvos[i].getShowbgcolor(), "."); //
					Color[] colos = new Color[str_colors.length]; //������ɫ����
					for (int j = 0; j < colos.length; j++) {
						colos[j] = getTBUtil().getColor(str_colors[j]); //
					}
					return colos;
				}
			}
		}
		return null;
	}

	private boolean isAdmin() {
		return ClientEnvironment.getInstance().isAdmin();
	}

	/**
	 * ȡ�ù�����
	 * @return
	 */
	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil; //
	}

	private int getHeaderHeight() {
		int height = 0;
		TableColumnModel columnModel = header.getColumnModel();
		for (int column = 0; column < columnModel.getColumnCount(); column++) {
			TableColumn aColumn = columnModel.getColumn(column);
			DefaultTableCellRenderer aDefaultTableCellRenderer = new DefaultTableCellRenderer();
			aDefaultTableCellRenderer.setBorder(new javax.swing.border.EtchedBorder(javax.swing.border.EtchedBorder.RAISED));
			aColumn.setHeaderRenderer(aDefaultTableCellRenderer);
			TableCellRenderer renderer = aColumn.getHeaderRenderer(); //Ϊ��
			Component comp = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, column);
			int cHeight = comp.getPreferredSize().height;
			Enumeration enums = ((BillListGroupableTableHeader) header).getColumnGroups(aColumn);
			if (enums != null) {
				while (enums.hasMoreElements()) {
					BillListGroupColumn cGroup = (BillListGroupColumn) enums.nextElement();
					cHeight += cGroup.getSize(header.getTable()).height;
				}
			}
			height = Math.max(height, cHeight);
		}

		return height;
	}

	/**
	 * �����һ����,�ؼ��Ǹ߶ȱȽ��鷳����!!
	 * @param width
	 * @return
	 */
	private Dimension createHeaderSize(long width) {
		TableColumnModel columnModel = header.getColumnModel();
		width += columnModel.getColumnMargin() * columnModel.getColumnCount();
		if (width > Integer.MAX_VALUE) {
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, getHeaderHeight());
	}

	public Dimension getPreferredSize(JComponent c) {
		long width = 0;
		Enumeration enumeration = header.getColumnModel().getColumns(); //ȡ�����е���!!
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth(); //Ҫȥ���߿�Ŀ��
		}
		return createHeaderSize(width);
	}

}
