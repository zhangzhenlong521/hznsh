package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.ArrayList;

/**
 * 平台表的表结构
 * @author xch
 *
 */
public class WLTTableDf {

	private String tableName; //表名
	private String tableDesc; //表说明
	private ArrayList alColumns = new ArrayList(); //存储所有列信息,一条信息是一个一维数组.. 
	private String pkFieldName = "id"; //主键字段名称,默认是ID
	private String[] indexName = null; //索引名称,比如code,name等
	private ArrayList allIndexs = new ArrayList();
	public static String VARCHAR = "varchar"; //字符型
	public static String NUMBER = "number"; //字符型
	public static String CHAR = "char"; //
	public static String CLOB = "clob"; //

	//表结构
	private WLTTableDf() {
	}

	public WLTTableDf(String _tableName, String _tableDesc) {
		this.tableName = _tableName; //表名
		this.tableDesc = _tableDesc; //表说明
	}

	/**
	 * 增加一个列说明,最关键,参数是一个宽度为5的一维数组,分别是列名,列说明,类型,长度,是否允许为空!
	 * 这5个参数在主流关每户数据库存中都有!!
	 * @param _colInfo
	 */
	public void addCol(String[] _colInfo) {
		alColumns.add(_colInfo); //
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

	public String getPkFieldName() {
		return pkFieldName;
	}

	//设置主键字段名,不设的话则使用默认的ID,主键是必不可少的!!
	public void setPkFieldName(String pkFieldName) {
		this.pkFieldName = pkFieldName;
	}

	public String[] getIndexName() {
		return indexName;
	}

	//设置索引名
	public void setIndexName(String[] indexName) {
		this.indexName = indexName;
	}

	//取得所有列的定义
	public String[][] getColDefines() {
		String[][] str_return = new String[alColumns.size()][5]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = (String[]) alColumns.get(i); //
		}
		return str_return; //
	}

	public ArrayList getAllIndexs() {
		return allIndexs;
	}

	public void setAllIndexs(ArrayList allIndexs) {
		this.allIndexs = allIndexs;
	}

}
