package cn.com.infostrategy.bs.sysapp.transferdb;

import java.util.HashMap;

public class SQLServerToMySQLConverter implements ConvertAdapterIfc {

	private HashMap mapSQL_KeyWord = null;
	
	private HashMap mapSQL_Mysql = null;
	
	public HashMap getConvertDataType() {
		if (mapSQL_Mysql != null) {
			return mapSQL_Mysql;
		}

		mapSQL_Mysql = new HashMap();
		mapSQL_Mysql.put("text", "text");
		mapSQL_Mysql.put("datetime", "char(19)");
		mapSQL_Mysql.put("nvarchar", "varchar");
		return mapSQL_Mysql; //
	}

	public HashMap getKeyWorld() {
		if (mapSQL_KeyWord != null) {
			return mapSQL_KeyWord;
		}

		mapSQL_KeyWord = new HashMap();
		mapSQL_KeyWord.put("dual", "dual_mysql");
		mapSQL_KeyWord.put("show", "isshow");
		mapSQL_KeyWord.put("explain", "explainor");
		mapSQL_KeyWord.put("sql", "sqlcontent");
		mapSQL_KeyWord.put("condition", "conditionor");
		return mapSQL_KeyWord; //
	}

}
