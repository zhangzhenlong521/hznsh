package cn.com.infostrategy.ui.mdata;

import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
  * ColumnGroup,这是个普通对象
  *
  * @version 1.0 10/20/98
  * @author Nobuo Tamemasa
  */

public class BillListGroupColumn implements Serializable {

	private static final long serialVersionUID = 6723568399858067077L;
	protected TableCellRenderer renderer; //
	protected Vector v; //向量
	protected String text;
	protected int margin = 0;

	/**
	 * 构造方法
	 */
	public BillListGroupColumn(String text) {
		this(null, text);
	}

	public BillListGroupColumn(TableCellRenderer renderer, String text) {
		if (renderer == null) {
			this.renderer = new DefaultTableCellRenderer() //创建Render
			{
				public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
					JTableHeader header = table.getTableHeader();
					if (header != null) {
						setForeground(header.getForeground());
						setBackground(header.getBackground());
						setFont(header.getFont());
					}
					setHorizontalAlignment(JLabel.CENTER);
					setText((value == null) ? "" : value.toString()); //设置表头文字说明
					setBorder(UIManager.getBorder("TableHeader.cellBorder")); //设置
					return this;
				}
			};
		} else {
			this.renderer = renderer;
		}

		this.text = text;
		v = new Vector();
	}

	/**
	 * @param obj    TableColumn or ColumnGroup
	 */
	public void add(Object obj) {
		if (obj == null) {
			return;
		}
		v.addElement(obj);
	}

	/**
	 * 
	 * @return
	 */
	public boolean containsGroup(BillListGroupColumn _group) {
		if (v.contains(_group)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param c    TableColumn
	 * @param v    ColumnGroups
	 */
	public Vector getColumnGroups(TableColumn c, Vector g) {
		g.addElement(this);
		if (v.contains(c)) {
			return g;
		}
		Enumeration enums = v.elements();
		while (enums.hasMoreElements()) {
			Object obj = enums.nextElement();
			if (obj instanceof BillListGroupColumn) //如果是一个组,则递归计算
			{
				Vector groups = (Vector) ((BillListGroupColumn) obj).getColumnGroups(c, (Vector) g.clone());
				if (groups != null) {
					return groups;
				}
			}
		}
		return null;
	}

	public TableCellRenderer getHeaderRenderer() {
		return renderer;
	}

	public void setHeaderRenderer(TableCellRenderer renderer) {
		if (renderer != null) {
			this.renderer = renderer;
		}
	}

	public Object getHeaderValue() {
		return text;
	}

	/**
	 * 取得某一个合并组的宽度与高度
	 * @param table
	 * @return
	 */
	public Dimension getSize(JTable table) {
		Component comp = renderer.getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
		int height = comp.getPreferredSize().height;
		int width = 0;
		Enumeration enums = v.elements();
		while (enums.hasMoreElements()) {
			Object obj = enums.nextElement();
			if (obj instanceof TableColumn) {
				TableColumn aColumn = (TableColumn) obj;
				width = width + aColumn.getWidth();
				//width = width + margin;  //千万不能加边框空白,一加就会出现错位的情况
			} else {
				width = width + ((BillListGroupColumn) obj).getSize(table).width;
			}
		}

		return new Dimension(width, height);
	}

	public void setColumnMargin(int margin) {
		this.margin = margin;
		Enumeration enums = v.elements();
		while (enums.hasMoreElements()) {
			Object obj = enums.nextElement();
			if (obj instanceof BillListGroupColumn) {
				((BillListGroupColumn) obj).setColumnMargin(margin);
			}
		}
	}
}
