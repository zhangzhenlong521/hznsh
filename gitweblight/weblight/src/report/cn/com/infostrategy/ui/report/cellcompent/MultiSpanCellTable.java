package cn.com.infostrategy.ui.report.cellcompent;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Enumeration;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

/**
 * @version 1.0 11/26/98
 */

public class MultiSpanCellTable extends JTable {

	private static final long serialVersionUID = 3200874864649631383L;

	private boolean showCellKey = false; //是否显示CellKey
	private boolean triggerEditListenerEvent = true; //是否触发编辑事件.
	private boolean isEditable = true; //
	private CellSpan cellAtt = null; //
	private int mouseMovingRow = -1, mouseMovingCol = -1; //
	private boolean isBlankEditor = false;

	public MultiSpanCellTable(TableModel _model) {
		super(_model);
		setUI(new MultiSpanCellTableUI());
		getTableHeader().setReorderingAllowed(false);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		cellAtt = (CellSpan) ((AttributiveCellTableModel) _model).getCellAttribute(); //
	}

	public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
		Rectangle sRect = super.getCellRect(row, column, includeSpacing); //
		if ((row < 0) || (column < 0) || (getRowCount() <= row) || (getColumnCount() <= column)) {
			return sRect;
		}

		if (!cellAtt.isVisible(row, column)) {
			int temp_row = row;
			int temp_column = column;
			row += cellAtt.getSpan(temp_row, temp_column)[CellSpan.ROW];
			column += cellAtt.getSpan(temp_row, temp_column)[CellSpan.COLUMN];
		}
		int[] n = cellAtt.getSpan(row, column);

		int index = 0;
		int columnMargin = 0; //getColumnModel().getColumnMargin(); //强行设为零,否则用原来的方法会造成表头列与表格线错位,这个鸟问题曾经折磨得我痛苦得很长时间!!!!
		Rectangle retutnCellRect = new Rectangle();

		//修改后的代码
		retutnCellRect.y = 0;
		for (int i = 0; i < row; i++) {
			retutnCellRect.y = retutnCellRect.y + getRowHeight(i) + rowMargin; //将前面的所有行加起来!!!
		}

		retutnCellRect.height = 0;
		for (int i = 0; i < n[CellSpan.ROW]; i++) { //将合并的行都加起来
			retutnCellRect.height = retutnCellRect.height + getRowHeight(row + i) + rowMargin; //将合并的行分别加起来!!!
		}

		Enumeration enumeration = getColumnModel().getColumns();
		while (enumeration.hasMoreElements()) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			retutnCellRect.width = aColumn.getWidth() + columnMargin;
			if (index == column)
				break;
			retutnCellRect.x += retutnCellRect.width;
			index++;
		}

		for (int i = 0; i < n[CellSpan.COLUMN] - 1; i++) {
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			retutnCellRect.width += aColumn.getWidth() + columnMargin;
		}

		if (!includeSpacing) {
			Dimension spacing = getIntercellSpacing();
			retutnCellRect.setBounds(retutnCellRect.x + spacing.width / 2, retutnCellRect.y + spacing.height / 2, retutnCellRect.width - spacing.width, retutnCellRect.height - spacing.height);
		}

		return retutnCellRect;
	}

	private int[] rowColumnAtPoint(Point point) {
		int[] retValue = { -1, -1 }; //
		int row = 0; //

		int mouse_y = point.y; //鼠标的位置!!!即从表格的最左上角到该鼠标的位置..
		int li_yy = 0;
		for (int i = 0; i < getRowCount(); i++) {
			//System.out.println("第[" + i + "]行的行高=[" + getRowHeight(i) + "]"); //
			li_yy = li_yy + getRowHeight(i) + rowMargin; //
			if (li_yy >= point.y - 0) {
				row = i; //
				break;
			}
		}

		//int row = point.y / (rowHeight + rowMargin);

		if ((row < 0) || (getRowCount() <= row))
			return retValue;

		int column = getColumnModel().getColumnIndexAtX(point.x);

		CellSpan cellAtt = (CellSpan) ((AttributiveCellTableModel) getModel()).getCellAttribute();

		if (cellAtt.isVisible(row, column)) {
			retValue[CellSpan.COLUMN] = column;
			retValue[CellSpan.ROW] = row;
			return retValue;
		}
		retValue[CellSpan.COLUMN] = column + cellAtt.getSpan(row, column)[CellSpan.COLUMN];
		retValue[CellSpan.ROW] = row + cellAtt.getSpan(row, column)[CellSpan.ROW];
		return retValue;
	}

	public int rowAtPoint(Point point) {
		return rowColumnAtPoint(point)[CellSpan.ROW];
	}

	public int columnAtPoint(Point point) {
		return rowColumnAtPoint(point)[CellSpan.COLUMN];
	}

	public void columnSelectionChanged(ListSelectionEvent e) {
		repaint();
	}

	public boolean isShowCellKey() {
		return showCellKey;
	}

	public void setShowCellKey(boolean showCellKey) {
		this.showCellKey = showCellKey;
	}

	public boolean isTriggerEditListenerEvent() {
		return triggerEditListenerEvent;
	}

	public void setTriggerEditListenerEvent(boolean triggerEditListenerEvent) {
		this.triggerEditListenerEvent = triggerEditListenerEvent;
	}

	public boolean isEditable() {
		return isEditable;
	}

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public int getMouseMovingRow() {
		return mouseMovingRow;
	}

	public void setMouseMovingRow(int mouseMovingRow) {
		this.mouseMovingRow = mouseMovingRow;
	}

	public int getMouseMovingCol() {
		return mouseMovingCol;
	}

	public void setMouseMovingCol(int mouseMovingCol) {
		this.mouseMovingCol = mouseMovingCol;
	}

	/**
	 * 构建对象
	 */
	public void setValueAtIgnoreEditEvent(Object aValue, int row, int column) {
		triggerEditListenerEvent = false; //不触发
		getModel().setValueAt(aValue, row, convertColumnIndexToModel(column));
		triggerEditListenerEvent = true; //触发
		//"aaa".equalsIgnoreCase(anotherString)
	}

	public void valueChanged(ListSelectionEvent e) {
		int firstIndex = e.getFirstIndex();
		int lastIndex = e.getLastIndex();
		if (firstIndex == -1 && lastIndex == -1) { // Selection cleared.
			repaint();
		}
		Rectangle dirtyRegion = getCellRect(firstIndex, 0, false);
		int numCoumns = getColumnCount();
		int index = firstIndex;
		for (int i = 0; i < numCoumns; i++) {
			dirtyRegion.add(getCellRect(index, i, false));
		}
		index = lastIndex;
		for (int i = 0; i < numCoumns; i++) {
			dirtyRegion.add(getCellRect(index, i, false));
		}
		repaint(dirtyRegion.x, dirtyRegion.y, dirtyRegion.width, dirtyRegion.height);
	}
	/*
	 * 解决文本录入前的动作。如果不是有光标状态，就会清楚原始数据。 
	 */
	public Component prepareEditor(TableCellEditor tablecelleditor, int row, int column) {
		Component c = super.prepareEditor(tablecelleditor, row, column);
		if (isBlankEditor) {
			if (c instanceof JTextComponent) {
				((JTextComponent) c).setText("");
			}
		}
		return c;
	}

	protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition, boolean pressed) {
		if (!"F2".equals(KeyEvent.getKeyText(e.getKeyCode()))) //f2进入编辑状态
			isBlankEditor = true;
		boolean retValue = super.processKeyBinding(ks, e, condition, pressed);
		isBlankEditor = false;
		return retValue;
	}

}
