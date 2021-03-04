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
		this.templet_1_itemvos = _templet_1vo.getItemVos(); //所有明细
	}

	public void paint(Graphics g, JComponent c) {
		Rectangle clipBounds = g.getClipBounds(); //取得长方形
		if (header.getColumnModel() == null) {
			return;
		}

		((BillListGroupableTableHeader) header).setColumnMargin(); //
		int column = 0;
		Dimension size = header.getSize();
		Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
		Hashtable h = new Hashtable();
		int columnMargin = header.getColumnModel().getColumnMargin();
		Enumeration enumeration = header.getColumnModel().getColumns(); //得到所有的列,遍历画所有的列
		while (enumeration.hasMoreElements()) {
			cellRect.height = size.height;
			cellRect.y = 0;
			TableColumn aColumn = (TableColumn) enumeration.nextElement(); //以得某一列
			Pub_Templet_1_ItemVO itemVO = getItemVO(aColumn.getIdentifier().toString()); //
			Color[] colors = geBgColor(aColumn.getIdentifier().toString()); //
			Enumeration cGroups = ((BillListGroupableTableHeader) header).getColumnGroups(aColumn);
			if (cGroups != null) {
				int li_index = 0;
				int groupHeight = 0;
				while (cGroups.hasMoreElements()) { //遍历由该列开始的所有组合列
					BillListGroupColumn cGroup = (BillListGroupColumn) cGroups.nextElement(); //
					//System.out.println("组名:" + cGroup.getHeaderValue());
					Rectangle groupRect = (Rectangle) h.get(cGroup); //
					if (groupRect == null) {
						groupRect = new Rectangle(cellRect);
						Dimension d = cGroup.getSize(header.getTable());
						groupRect.width = d.width; //
						groupRect.height = d.height; //
						h.put(cGroup, groupRect); //
					}

					groupRect.width = groupRect.width; //
					li_index = li_index + 1; //第一个组
					Color groupbgcolor = null; //
					if (colors != null) {
						if (colors.length > li_index) //当颜色数组的长度大于该数时!
						{
							groupbgcolor = colors[colors.length - li_index]; ////
						}
					}

					paintCell(g, groupRect, cGroup, groupbgcolor); //画合并的表头
					groupHeight = groupHeight + groupRect.height;
					cellRect.height = size.height - groupHeight;
					cellRect.y = groupHeight;
				}
			}

			cellRect.width = aColumn.getWidth(); // + columnMargin;  //千万不能加空白边框,一回就会出现错位现象

			if (cellRect.intersects(clipBounds)) {
				Color bgColor = null;
				if (colors != null) {
					bgColor = colors[0];
				}
				paintCell(g, cellRect, column, bgColor, itemVO); //画最底层的列!!!
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
	 * 画合并的表头
	 * @param g
	 * @param cellRect
	 * @param cGroup
	 */
	private void paintCell(Graphics g, Rectangle cellRect, BillListGroupColumn cGroup, Color _bgcolor) //上面的,蓝色的
	{
		TableCellRenderer renderer = cGroup.getHeaderRenderer(); //
		Component component = renderer.getTableCellRendererComponent(header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);

		Hashtable ht_convert = ((BillListGroupableTableHeader) header).getColValueConverter(); //
		String str_headervalue = (String) cGroup.getHeaderValue(); //
		if (ht_convert.get(str_headervalue) != null) {
			str_headervalue = (String) ht_convert.get(str_headervalue); //
		}

		JLabel aJLabel = new JLabel(str_headervalue, SwingConstants.CENTER); //真正创建表头!!关键
		aJLabel.setOpaque(true);
		aJLabel.setFont(new Font("System", 0, 12)); //
		if (_bgcolor != null) {
			aJLabel.setBackground(_bgcolor);
		}

		aJLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder")); //设置边框
		component = aJLabel;
		rendererPane.add(component);
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true); //关键是这一行,才是真正实现表头合并效果的!
	}

	/**
	 * 画最底层的,也是真正的实际列!!
	 * @param g
	 * @param cellRect
	 * @param nIndecolumncolor 
	 */
	private void paintCell(Graphics g, Rectangle cellRect, int columnIndex, Color _bgcolor, Pub_Templet_1_ItemVO itemVO) //下面的,红色的
	{
		TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
		String str_key = aColumn.getIdentifier().toString(); //
		//System.out.println("列的唯一性说明:" + aColumn.getIdentifier());  //
		TableCellRenderer renderer = aColumn.getHeaderRenderer();
		Component component = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, false, -1, columnIndex);
		String str_text = (String) aColumn.getHeaderValue(); //
		String original_str_text = str_text; //
		String str_tooltiptext = str_text; //

		JLabel aJLabel = new JLabel("", SwingConstants.CENTER); //真正创建表头!!关键
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
				str_tooltiptext = str_tooltiptext + " [" + itemVO.getPub_Templet_1VO().getSavedtablename() + "],该表没有该列!!将会保存失败!!"; //
			}

		}

		if (itemVO.isViewColumn()) {
			str_text = "☆" + str_text; //
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

		// 如果是管理员，则显示提示信息.
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
		aJLabel.setBorder(UIManager.getBorder("TableHeader.cellBorder")); // 设置边框
		component = aJLabel;
		rendererPane.add(component);
		rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true); //关键是这一行,才是真正实现表头合并效果的!
	}

	private Color[] geBgColor(String _itemKey) {
		for (int i = 0; i < templet_1_itemvos.length; i++) {
			if (templet_1_itemvos[i].getItemkey().equals(_itemKey)) {
				if (templet_1_itemvos[i].getShowbgcolor() != null && !templet_1_itemvos[i].getShowbgcolor().equals("")) {
					String[] str_colors = getTBUtil().split(templet_1_itemvos[i].getShowbgcolor(), "."); //
					Color[] colos = new Color[str_colors.length]; //所有颜色数组
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
	 * 取得工具类
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
			TableCellRenderer renderer = aColumn.getHeaderRenderer(); //为空
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
	 * 宽度是一定的,关键是高度比较麻烦计算!!
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
		Enumeration enumeration = header.getColumnModel().getColumns(); //取得所有的列!!
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth(); //要去掉边框的宽度
		}
		return createHeaderSize(width);
	}

}
