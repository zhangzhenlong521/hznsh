package cn.com.infostrategy.bs.sysapp.transferdb;

import java.util.HashMap;

public class SQLServerToOracleConverter implements ConvertAdapterIfc {

	private HashMap mssql2oracleKeyword = null;
	
	private HashMap mssql2oracleDatatype = null;
	
	public HashMap getConvertDataType() {
		
		if(mssql2oracleDatatype==null){
			
			mssql2oracleDatatype = new HashMap();
			mssql2oracleDatatype.put("text", "clob");
			mssql2oracleDatatype.put("date", "char(10)");
			mssql2oracleDatatype.put("datetime", "char(19)");
			mssql2oracleDatatype.put("nvarchar", "varchar");
			mssql2oracleDatatype.put("int", "decimal");
		}
		return mssql2oracleDatatype;
	}

	public HashMap getKeyWorld() {
		if (mssql2oracleKeyword != null) {
			return mssql2oracleKeyword;
		}

		mssql2oracleKeyword = new HashMap();
		mssql2oracleKeyword.put("uid", "usorid");
		mssql2oracleKeyword.put("date", "workdate");
		mssql2oracleKeyword.put("dual", "oracle_dual");
		mssql2oracleKeyword.put("explain", "explainor");
		mssql2oracleKeyword.put("sql", "sqlcontent");
		mssql2oracleKeyword.put("condition", "conditionor");
		return mssql2oracleKeyword; //
	}

}
