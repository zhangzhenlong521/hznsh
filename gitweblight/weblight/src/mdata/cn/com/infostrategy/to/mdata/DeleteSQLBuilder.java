package cn.com.infostrategy.to.mdata;

import cn.com.infostrategy.to.common.SQLBuilderIfc;

/**
 * ����Delete SQL�Ĺ�����
 * 
 * @author xch
 * 
 */
public class DeleteSQLBuilder implements SQLBuilderIfc {

	private String tableName = null; // ����
	private String whereCondition = null; // where����

	public DeleteSQLBuilder() {
	}

	public DeleteSQLBuilder(String _tableName) {
		this.tableName = _tableName; //
	}

	public DeleteSQLBuilder(String _tableName, String _whereCondition) {
		this.tableName = _tableName; //
		this.whereCondition = _whereCondition; //
	}

	/**
	 * ƴ��SQL
	 * 
	 * @return
	 */
	public String getSQL() {
		String sql = "delete from " + getTableName();
		if (getWhereCondition() != null && !"".equals(getWhereCondition())) {
			sql += " where " + getWhereCondition(); //	
		}
		return sql; //
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public String getWhereCondition() {
		return whereCondition;
	}

	public void setWhereCondition(String whereCondition) {
		this.whereCondition = whereCondition;
	}

	/**
	 * �ع�ToString()����,����SQL
	 */
	@Override
	public String toString() {
		return getSQL(); //
	}

}
