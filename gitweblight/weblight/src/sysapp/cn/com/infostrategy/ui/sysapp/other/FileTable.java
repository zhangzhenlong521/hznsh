package cn.com.infostrategy.ui.sysapp.other;

import javax.swing.table.AbstractTableModel;

public class FileTable extends AbstractTableModel {

	/**
	 * 显示将文件的信息放在本表格中
	 */
	private static final long serialVersionUID = -7317730659817645147L;
	private Object[][] tableCells = null;
	private String[] tableHandle = null;
	public FileTable(Object[][] tableCells,String[] tableHandle){
		this.tableCells = tableCells;
		this.tableHandle = tableHandle;
	}
	public int getColumnCount() {
		return this.tableHandle.length;
	}

	public int getRowCount() {
		return this.tableCells.length;
	}
	public String getColumnName(int i){
		return tableHandle[i];
	}
	public Object getValueAt(int rowIndex, int columnIndex) {
		return this.tableCells[rowIndex][columnIndex];
	}
	public Class getColumnClass(int col){
		return getValueAt(0, col).getClass();
	}
}
