package cn.com.infostrategy.ui.report.cellcompent;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

/**
 * 绘制器,swing最底层的,从网下下载来的那份代码好象有性能问题!!
 * 这是经过我修改后的!
 * @author xch
 *
 */
public class MultiSpanCellTableUI extends BasicTableUI {

	private AttributiveCellTableModel tableModel = null; //
	private CellSpan cellAtt = null; //
	private TableCellRenderer renderer = null; //
	private Component rendererComponent = null; //

	public void paint(Graphics g, JComponent c) {
		Rectangle oldClipBounds = g.getClipBounds(); //以前的取法!!
		//Rectangle oldClipBounds = c.getVisibleRect(); //这句话在纵向滚动条滚动时会出现白屏，李春娟修改

		tableModel = (AttributiveCellTableModel) table.getModel(); //
		cellAtt = (CellSpan) tableModel.getCellAttribute();

		Rectangle clipBounds = new Rectangle(oldClipBounds);
		//int tableWidth = table.getColumnModel().getTotalColumnWidth();
		//clipBounds.width = Math.min(clipBounds.width, tableWidth);
		//g.setClip(clipBounds);

		//这里解决了部分不能刷新出现白屏的问题，但是如果数据量大并且合并行比较多的话，滚动滚动条会特别卡。
		//因为一般情况下是前两三列的合并行数比较多，所以从右边列取点判断开始和结束行号，徐老师说有可能右边列也有好多纵向合并，所以以后要写个方法，精确判断显示区域的开始和结束行号！李春娟修改
		int firstIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y)) - 1; //可以看见的第一行!以前是new Point(0, clipBounds.y)，后来发现横向滚动条滚动时，就出现白屏现象，李春娟修改
		int allIndex = table.rowAtPoint(new Point(clipBounds.x + clipBounds.width, clipBounds.y + clipBounds.height)) + 1; //总行多少行table.rowAtPoint(new Point(0, clipBounds.width)); 
		//System.out.println(">>>>>>" + firstIndex + "--" + allIndex + ">>>>>>>" + clipBounds.x + "==" + clipBounds.y + "==" + clipBounds.width + "==" + clipBounds.height);
		int li_undrawn = 0; //
		for (int index = (firstIndex <= 0 ? 0 : firstIndex); index <= allIndex; index++) {
			boolean bo_isdrawnrow = paintRow(g, index); //如果在可视区内,则画之!
			if (!bo_isdrawnrow) {
				li_undrawn++; //
			}
			if (li_undrawn > 40) { //如果连续发生40次没画,则退出循环!!这个问题很妖,一直没搞明白!通过Debug发现,有时忽然会发生从前几行就开始就不画了,感觉是Swing的一个Bug.所以我干脆放了一个大一点的数,即40
				break;
			}
		}
	}

	/**
	 * 画某一行
	 * @param g
	 * @param row
	 */
	private boolean paintRow(Graphics g, int row) {
		Rectangle rect = g.getClipBounds();
		boolean drawn = false;
		int numColumns = table.getColumnCount(); //
		for (int column = 0; column < numColumns; column++) {
			Rectangle cellRect = table.getCellRect(row, column, true); //可能是这个方法有问题,
			int cellRow, cellColumn;
			if (cellAtt.isVisible(row, column)) {
				cellRow = row;
				cellColumn = column;
				//System.out.print("   " + column + " "); // debug
			} else {
				cellRow = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
				cellColumn = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
				//System.out.print("  (" + column + ")"); // debug
			}

			if (cellRect.intersects(rect)) {
				drawn = true; //画了
				paintCell(g, cellRect, cellRow, cellColumn);
				//System.out.println("实际画[" + cellRow + "]行"); //
			} else {
				if (drawn)
					break;
			}
		}

		return drawn; //
	}

	private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
		int spacingHeight = table.getRowMargin();
		int spacingWidth = table.getColumnModel().getColumnMargin();

		Color c = g.getColor();
		g.setColor(table.getGridColor());
		//g.setColor(Color.RED);

		g.drawRect(cellRect.x, cellRect.y, cellRect.width - 1, cellRect.height - 1);
		g.setColor(c);
		cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y + spacingHeight / 2, cellRect.width - spacingWidth, cellRect.height - spacingHeight);
		if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
			//System.out.println("编辑[" + row + "," + column + "]");  //
			Component component = table.getEditorComponent();
			component.setBounds(cellRect);
			component.validate();
		} else {
			renderer = table.getCellRenderer(row, column); //取得绘制器
			rendererComponent = table.prepareRenderer(renderer, row, column); //取得Render中的返回的控件,比如下拉框,文本框等.

			if (rendererComponent.getParent() == null) {
				rendererPane.add(rendererComponent);
			}

			//System.out.println("画第[" + row + "]数据!!!"); //
			rendererPane.paintComponent(g, rendererComponent, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
		}
	}

}
