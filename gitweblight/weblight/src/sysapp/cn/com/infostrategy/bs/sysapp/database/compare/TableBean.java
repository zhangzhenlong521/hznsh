package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.ArrayList;
import java.util.List;

public class TableBean {

	private String id = "";
	private String database = "";
	private String tableCode = "";
	private String tableName = "";
	private String tableDesc = "";
	private List theColumns = null;

	public String getTableCode() {
		return tableCode;
	}

	public void setTableCode(String tableCode) {
		this.tableCode = tableCode;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableDesc() {
		return tableDesc;
	}

	public void setTableDesc(String tableDesc) {
		this.tableDesc = tableDesc;
	}

	public List<ColumnBean> getTheColumns() {
		if (theColumns == null) {
			return new ArrayList<ColumnBean>();
		}
		return theColumns;
	}

	public void setTheColumns(List<ColumnBean> theColumns) {
		this.theColumns = theColumns;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}
}
