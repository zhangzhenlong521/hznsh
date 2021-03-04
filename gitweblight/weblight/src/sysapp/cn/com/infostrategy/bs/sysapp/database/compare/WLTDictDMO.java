package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TableDataStruct;

/**
 * 数据字典的DMO,它用来管理数据字典
 * 之所以提出这个类是为了解决跨平台的问题,以
 * @author xch
 *
 */
public class WLTDictDMO extends AbstractDMO {

	//取得所有定义的
	public WLTTableDf[] getAllPlatformTablesDefines() {
		return new WLTDictFactory().getAllPlatformTablesDefine(); //取得平台工厂定义的所有表
	}

	/**
	 * 返回所有定义有表名,要提供给客户端
	 * @return
	 */
	public String[][] getAllTableDefineNames() {
		WLTTableDf[] allTdfs = getAllPlatformTablesDefines(); //取得所有结构定义
		String[][] str_tabNames = new String[allTdfs.length][2];
		for (int i = 0; i < allTdfs.length; i++) {
			str_tabNames[i][0] = allTdfs[i].getTableName(); //名称
			str_tabNames[i][1] = allTdfs[i].getTableDesc(); //说明
		}
		return str_tabNames;
	}

	/**
	 * 根據表名模糊查詢
	 * @return
	 */
	public String[][] getAllTableDefineNames(String tableName) {
		WLTTableDf[] allTdfs = getAllPlatformTablesDefines(); //取得所有结构定义
		String[][] str_tabNames = new String[allTdfs.length][2];
		String[][] str_tabNames2 = null;
		List ss = new ArrayList();
		if (allTdfs != null && allTdfs.length > 0) {
			int j = 0;
			for (int i = 0; i < allTdfs.length; i++) {
				if (allTdfs[i].getTableName().indexOf(tableName) != -1) {
					str_tabNames[j][0] = allTdfs[i].getTableName(); //名称
					str_tabNames[j][1] = allTdfs[i].getTableDesc(); //说明
					ss.add(str_tabNames[j]);
					j++;
				}
			}
			/*str_tabNames2 = new String[j][2];
			for (int i = 0; i < j; i++) {
				str_tabNames2[i][0] = str_tabNames[i][0]; //名称
				str_tabNames2[i][1] = str_tabNames[i][1]; //说明
			}*/
			if (ss != null && ss.size() > 0) {
				str_tabNames2 = (String[][]) ss.toArray(new String[0][0]);
			}
		}

		return str_tabNames2;
	}

	/**
	 * 得到所有只定义有的表
	 * @return
	 */
	public String[][] getAllTableOnlyDFhave() {
		String[][] s = getAllTableDefineNames();
		List ss = new ArrayList();
		String[][] sss = null;
		if (s != null && s.length > 0) {
			for (int i = 0; i < s.length; i++) {
				try {
					if ("定义中有,但数据源中没有！".equals(getCompareSQLByTabName(s[i][0]))) {
						ss.add(s[i]);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (ss != null && ss.size() > 0) {
			sss = (String[][]) ss.toArray(new String[0][0]);
		}
		return sss;
	}

	/**
	 * 得到所有只数据源有的表
	 * @return
	 */
	public String[][] getAllTableOnlyDBhave() {
		String[] s = null;
		try {
			s = new CommDMO().getAllSysTables(null, "%");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		List ss = new ArrayList();
		String[][] sss = null;
		if (s != null && s.length > 0) {
			for (int i = 0; i < s.length; i++) {
				try {
					if ("定义中没有,但数据源中有！".equals(getCompareSQLByTabName(s[i]))) {
						ss.add(s[i]);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (ss != null && ss.size() > 0) {
			sss = new String[ss.size()][2];
			for (int i = 0; i < ss.size(); i++) {
				sss[i][0] = (String) ss.get(i);
				sss[i][1] = (String) ss.get(i);
			}
		}
		return sss;
	}

	/**
	 * 得到所有平台定义与数据源都有的表
	 * @return
	 */
	public String[][] getAllTableBHhave() {
		String[][] s = getAllTableDefineNames();
		List ss = new ArrayList();
		String[][] sss = null;
		if (s != null && s.length > 0) {
			for (int i = 0; i < s.length; i++) {
				try {
					if (!"定义中有,但数据源中没有！".equals(getCompareSQLByTabName(s[i][0])) && !"定义中没有,但数据源中有！".equals(getCompareSQLByTabName(s[i][0]))) {
						ss.add(s[i]);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (ss != null && ss.size() > 0) {
			sss = (String[][]) ss.toArray(new String[0][0]);
		}
		return sss;
	}

	/**
	 * 根据表名返回所有列的信息,要提供给客户端
	 * @return
	 */
	public String[][] getAllColumnsDefineNames(String _tabName) {
		WLTTableDf allTdfs = getTableDefByName(_tabName); //
		String[][] str_tabNames = null;
		if (allTdfs != null) {
			str_tabNames = allTdfs.getColDefines();
		}
		return str_tabNames;
	}

	/**
	 * 根据名称取得某一个表的定义..
	 * @param _tabName
	 * @return
	 */
	private WLTTableDf getTableDefByName(String _tabName) {
		WLTTableDf[] allTdfs = getAllPlatformTablesDefines(); //取得所有结构定义
		for (int i = 0; i < allTdfs.length; i++) {
			if (allTdfs[i].getTableName().equalsIgnoreCase(_tabName)) {
				return allTdfs[i]; //
			}
		}
		return null; //
	}

	/**
	 * 根据表名,生成默认数据源的数据库类型的Create脚本! 该方法要提供给远程的客户端
	 */
	public String getCreateSQLByTabDefineName(String _tabName) {
		return getCreateSQLByTabDefineName(ServerEnvironment.getDefaultDataSourceType(), _tabName);
	}

	/**
	 * 根据表名,字段名,生成默认数据源的数据库类型的alter脚本! 该方法要提供给远程的客户端
	 */
	public String getAlterSQLByTabDefineName(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) {
		if (_dbtype == null || "null".equals(_dbtype) || "".equals(_dbtype.trim())) {
			_dbtype = ServerEnvironment.getDefaultDataSourceType();
		}
		HashMap typeMap = getTypeMap(_dbtype);
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("alter table " + _tabName + " add " + _cName + " " + (String) typeMap.get(_cType) + "(" + _cLength + ");\r\n");
		return sb_sql.toString(); //
	}

	/**
	 * 得到所的alter脚本! 该方法要提供给远程的客户端
	 */
	public String getAllAlterSQLByTabDefineName() {
		String[][] bhhaveTables = getAllTableBHhave();
		StringBuffer ssbb=new StringBuffer();
		if (bhhaveTables != null && bhhaveTables.length > 0) {
			for(int i = 0;i<bhhaveTables.length;i++){
				try {
					List cpList = getCompareLISTByTabName(ServerEnvironment.getDefaultDataSourceName(),bhhaveTables[i][0]);
					String[][] ss=(String[][])cpList.get(1);
					if(ss!=null&&ss.length>0){
					for(int j=0;j<ss.length;j++){
						ssbb.append(getAlterSQLByTabDefineName(ServerEnvironment.getDefaultDataSourceType(),bhhaveTables[i][0],ss[j][0],ss[j][2],ss[j][3]));
					}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return ssbb.toString();
	}

	/**
	 * 根据表名计算出指定数据库类型的Create脚本!
	 * @param _dbType
	 * @param _tabName
	 * @return
	 */
	public String getCreateSQLByTabDefineName(String _dbType, String _tabName) {
		WLTTableDf tabDef = getTableDefByName(_tabName); //
		if (tabDef != null) {
			return getCreateSQL(_dbType, tabDef); //取得创建表的SQL,即Create Table脚本!!
		} else {
			return "/**没有找到表[" + _tabName + "]的定义*/";
		}
	}

	/**
	 * 孙富君负责补全!
	 * 根据定义的对象与数据库类型,输出创建表的SQL语句,即create table...,不同的数据库类型输出略有不同!!
	 * @param _dbType 数据库类型,比如Oracle,Mysql,SQLServer等
	 * @param _tdf 定义的对象
	 * @return
	 */
	private String getCreateSQL(String _dbType, WLTTableDf _tdf) {
		HashMap typeMap = getTypeMap(_dbType);

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create table " + _tdf.getTableName() + " (\r\n");
		String[][] str_cols = _tdf.getColDefines(); //所有列,5列!!
		for (int i = 0; i < str_cols.length; i++) {
			sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + "), /*" + str_cols[i][1] + "*/\r\n");
		}
		sb_sql.append(" constraint pk_" + _tdf.getTableName() + " primary key (" + _tdf.getPkFieldName() + ")\r\n");
		sb_sql.append(")");
		sb_sql.append("DEFAULT CHARSET=GBK;\r\n");
		if (_tdf.getIndexName() != null && _tdf.getIndexName().length > 0) {
			for (int i = 0; i < _tdf.getIndexName().length; i++) {
				sb_sql.append("create index in_" + _tdf.getTableName() + "_" + _tdf.getIndexName()[i] + " on " + _tdf.getTableName() + "(" + _tdf.getIndexName()[i] + ");\r\n");
			}
		}
		return sb_sql.toString(); //
	}

	private HashMap getTypeMap(String _dbType) {
		HashMap typeMap = new HashMap();
		if ("Oracle".equalsIgnoreCase(_dbType)) {
			typeMap.put(WLTTableDf.VARCHAR, "varchar2");
			typeMap.put(WLTTableDf.NUMBER, "number");
			typeMap.put(WLTTableDf.CHAR, "char");
			typeMap.put(WLTTableDf.CLOB, "clob");
		} else if ("Mysql".equalsIgnoreCase(_dbType)) {
			typeMap.put(WLTTableDf.VARCHAR, "varchar");
			typeMap.put(WLTTableDf.NUMBER, "decimal");
			typeMap.put(WLTTableDf.CHAR, "char");
			typeMap.put(WLTTableDf.CLOB, "text");
		} else {
			typeMap.put(WLTTableDf.VARCHAR, "varchar");
			typeMap.put(WLTTableDf.NUMBER, "decimal");
			typeMap.put(WLTTableDf.CHAR, "char");
			typeMap.put(WLTTableDf.CLOB, "text");
		}
		return typeMap;
	}

	/**
	 * 取得当前默认数据中表与定义的比较
	 * @param _dataSourceName
	 * @param _tabName
	 * @return
	 * @throws Exception
	 */
	public String getCompareSQLByTabName(String _tabName) throws Exception {
		return getCompareSQLByTabName(ServerEnvironment.getDefaultDataSourceName(), _tabName); //
	}

	/**
	 * 孙富君实现!!!
	 * 对某一个表进行比较!! 即将定义的表与实际数据库中的表进行比较
	 * 比较的结果有三种情况:
	 * 1.定义中有,但数据源中没有
	 * 2.定义中没有,但数据源中有
	 * 3.两者都有,但列情况不一样,列情况不一样又有三种情况，即与表情况的差异一样!!
	 * @param _dataSourceName 数据源名称
	 * @param _tabName 表名
	 * @return
	 */
	private String getCompareSQLByTabName(String _dataSourceName, String _tabName) throws Exception {
		String str_dbtpye = ServerEnvironment.getInstance().getDataSourceVO(_dataSourceName).getDbtype(); //取得该数据源的类型!!比如Oracle,Mysql,SQLServer
		String returnString = "";
		TableDataStruct dsTabTD = null;
		//先取出数据源中该表的实际定义
		try {
			dsTabTD = new CommDMO().getTableDataStructByDS(_dataSourceName, "select * from " + _tabName + " where 1=2");
		} catch (Exception e) {
			//e.printStackTrace();
			dsTabTD = null;
		}
		//再取出定义中的表结构原数据对象
		WLTTableDf tabdef = getTableDefByName(_tabName);

		//下面就根据数据库类型,实际表定义,数据字典的定义，这三个值进行比较!!生成返回结果!!
		if (dsTabTD == null && tabdef != null) {
			returnString = "定义中有,但数据源中没有！";
		}
		if (tabdef == null && dsTabTD != null) {
			returnString = "定义中没有,但数据源中有！";
		}
		if (tabdef == null && dsTabTD == null) {
			returnString = "定义中没有,数据源中也没有！";
		}

		if (tabdef != null && dsTabTD != null) {
			StringBuffer dsTabTDHave = new StringBuffer();
			StringBuffer tabdefHave = new StringBuffer();
			HashMap dsTabTDHeaderNameMap = new HashMap();
			HashMap tabdefHeaderNameMap = new HashMap();
			int dsTabTDClength = dsTabTD.getHeaderLength().length;
			int tabdefClength = tabdef.getColDefines().length;
			int maxLength = dsTabTDClength;
			if (tabdefClength > dsTabTDClength) {
				maxLength = tabdefClength;
			}
			for (int i = 0; i < maxLength; i++) {
				if (i < dsTabTDClength)
					dsTabTDHeaderNameMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), i);
				if (i < tabdefClength)
					tabdefHeaderNameMap.put(tabdef.getColDefines()[i][0].toLowerCase(), i);
			}
			for (int i = 0; i < maxLength; i++) {
				if (i < dsTabTDClength) {
					if (!tabdefHeaderNameMap.containsKey(dsTabTD.getHeaderName()[i].toLowerCase())) {
						if (i < dsTabTDClength - 1)
							dsTabTDHave.append(dsTabTD.getHeaderName()[i].toLowerCase() + "、");
						if (i == dsTabTDClength - 1)
							dsTabTDHave.append(dsTabTD.getHeaderName()[i].toLowerCase());
					}
				}
				if (i < tabdefClength) {
					if (!dsTabTDHeaderNameMap.containsKey(tabdef.getColDefines()[i][0].toLowerCase())) {
						if (i < tabdefClength - 1)
							tabdefHave.append(tabdef.getColDefines()[i][0].toLowerCase() + "、");
						if (i == tabdefClength - 1)
							tabdefHave.append(tabdef.getColDefines()[i][0].toLowerCase());
					}
				}
			}
			if (tabdefHave != null && tabdefHave.length() > 0) {
				returnString = returnString + "定义有而数据源没有的列：{" + tabdefHave.toString() + "}\r\n";
			}
			if (dsTabTDHave != null && dsTabTDHave.length() > 0) {
				returnString = returnString + "数据源有而定义没有的列：{" + dsTabTDHave.toString() + "}\r\n";
			}
		}

		if ("".equals(returnString.trim())) {
			returnString = "表名与列名均可匹配！";
		}
		return returnString;
	}

	public List getCompareLISTByTabName(String _dataSourceName, String _tabName) throws Exception {
		String str_dbtpye = ServerEnvironment.getInstance().getDataSourceVO(_dataSourceName).getDbtype(); //取得该数据源的类型!!比如Oracle,Mysql,SQLServer
		List returnList = new ArrayList();
		List dbHave = new ArrayList();
		List dfHave = new ArrayList();
		List bothHaveDB = new ArrayList();
		TableDataStruct dsTabTD = null;
		//先取出数据源中该表的实际定义
		try {
			dsTabTD = new CommDMO().getTableDataStructByDS(_dataSourceName, "select * from " + _tabName + " where 1=2");
		} catch (Exception e) {
			//e.printStackTrace();
			dsTabTD = null;
		}
		//再取出定义中的表结构原数据对象
		WLTTableDf tabdef = getTableDefByName(_tabName);

		//下面就根据数据库类型,实际表定义,数据字典的定义，这三个值进行比较!!生成返回结果!!
		if (dsTabTD == null && tabdef != null) {
			returnList = null;
		}
		if (tabdef == null && dsTabTD != null) {
			returnList = null;
		}
		if (tabdef == null && dsTabTD == null) {
			returnList = null;
		}

		if (tabdef != null && dsTabTD != null) {
			StringBuffer dsTabTDHave = new StringBuffer();
			StringBuffer tabdefHave = new StringBuffer();
			HashMap dsTabTDHeaderNameMap = new HashMap();
			HashMap tabdefHeaderNameMap = new HashMap();
			HashMap dsTabTDHeaderTypeMap = new HashMap();
			HashMap tabdefHeaderTypeMap = new HashMap();
			HashMap tabdefHeaderDescMap = new HashMap();
			HashMap dsTabTDHeaderLengthMap = new HashMap();
			HashMap tabdefHeaderLengthMap = new HashMap();
			HashMap dsTabTDHeaderIfNullMap = new HashMap();
			HashMap tabdefHeaderIfNullMap = new HashMap();
			int dsTabTDClength = dsTabTD.getHeaderLength().length;
			int tabdefClength = tabdef.getColDefines().length;
			int maxLength = dsTabTDClength;
			if (tabdefClength > dsTabTDClength) {
				maxLength = tabdefClength;
			}
			for (int i = 0; i < maxLength; i++) {
				if (i < dsTabTDClength) {
					dsTabTDHeaderNameMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), i);
					dsTabTDHeaderTypeMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getHeaderTypeName()[i]);
					dsTabTDHeaderLengthMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getHeaderLength()[i]);
					dsTabTDHeaderIfNullMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getIsNullAble()[i]);
				}
				if (i < tabdefClength) {
					tabdefHeaderNameMap.put(tabdef.getColDefines()[i][0].toLowerCase(), i);
					tabdefHeaderTypeMap.put(tabdef.getColDefines()[i][0].toLowerCase(), tabdef.getColDefines()[i][2]);
					tabdefHeaderLengthMap.put(tabdef.getColDefines()[i][0].toLowerCase(), tabdef.getColDefines()[i][3]);
					tabdefHeaderIfNullMap.put(tabdef.getColDefines()[i][0].toLowerCase(), tabdef.getColDefines()[i][4]);
					tabdefHeaderDescMap.put(tabdef.getColDefines()[i][0].toLowerCase(), tabdef.getColDefines()[i][1]);
				}
			}
			for (int i = 0; i < maxLength; i++) {
				if (i < dsTabTDClength) {
					if (!tabdefHeaderNameMap.containsKey(dsTabTD.getHeaderName()[i].toLowerCase())) {
						dbHave.add(new String[] { dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getHeaderTypeName()[i], String.valueOf(dsTabTD.getHeaderLength()[i]), dsTabTD.getIsNullAble()[i] });
					} else {
						bothHaveDB.add(new String[] { dsTabTD.getHeaderName()[i].toLowerCase() + "/" + dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getHeaderName()[i].toLowerCase() + "/" + tabdefHeaderDescMap.get(dsTabTD.getHeaderName()[i].toLowerCase()),
								dsTabTD.getHeaderTypeName()[i] + "/" + tabdefHeaderTypeMap.get(dsTabTD.getHeaderName()[i].toLowerCase()), String.valueOf(dsTabTD.getHeaderLength()[i]) + "/" + (String) tabdefHeaderLengthMap.get(dsTabTD.getHeaderName()[i].toLowerCase()),
								dsTabTD.getIsNullAble()[i] + "/" + (String) tabdefHeaderIfNullMap.get(dsTabTD.getHeaderName()[i].toLowerCase()) });
					}
				}
				if (i < tabdefClength) {
					if (!dsTabTDHeaderNameMap.containsKey(tabdef.getColDefines()[i][0].toLowerCase())) {
						dfHave.add(tabdef.getColDefines()[i]);
					}
				}
			}

		}
		returnList.add((String[][]) dbHave.toArray(new String[0][0]));
		returnList.add((String[][]) dfHave.toArray(new String[0][0]));
		returnList.add((String[][]) bothHaveDB.toArray(new String[0][0]));
		return returnList;
	}

	//反向生成Java代码
	public String reverseCreateJavaCode(String _tableName) {
		return reverseCreateJavaCode(ServerEnvironment.getDefaultDataSourceName(), _tableName); //
	}

	/**
	 * 李春娟负责
	 * 根据数据源中的实际表名反向生成Java代码!!
	 * 为了提高效率反向工程!!
	 * @param _tableName
	 * @return
	 * @throws Exception 
	 */
	public String reverseCreateJavaCode(String _dsName, String _tableName) {

		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("//表[" + _tableName + "]定义\r\n"); //
		sb_text.append("tdf = new WLTTableDf(\"" + _tableName + "\", \"" + _tableName + "\");\r\n"); //
		TableDataStruct dsTabTD = null;
		try {
			dsTabTD = new CommDMO().getTableDataStructByDS(_dsName, "select * from " + _tableName + " where 1=2");

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//		
		String[] headerName = dsTabTD.getHeaderName();
		String[] headerTypeName = dsTabTD.getHeaderTypeName();
		int[] headerLength = dsTabTD.getHeaderLength();
		int[] headerType = dsTabTD.getHeaderType();
		String[] isNullAble = dsTabTD.getIsNullAble(); //字段是否为空

		for (int i = 0; i < headerName.length; i++) {
			if (headerTypeName[i].indexOf("INT") != -1 || headerTypeName[i].equalsIgnoreCase("DECIMAL")) {
				sb_text.append("tdf.addCol(new String[] { \"" + headerName[i] + "\", \"" + headerName[i] + "\", WLTTableDf.NUMBER, \"" + headerLength[i] + "\", \"" + isNullAble[i] + "\" });\r\n");
			} else if (headerTypeName[i].equalsIgnoreCase("DATETIME")) {
				sb_text.append("tdf.addCol(new String[] { \"" + headerName[i] + "\", \"" + headerName[i] + "\", WLTTableDf.CHAR, \"10\", \"" + isNullAble[i] + "\" });\r\n");
			} else {
				sb_text.append("tdf.addCol(new String[] { \"" + headerName[i] + "\", \"" + headerName[i] + "\", WLTTableDf." + headerTypeName[i] + ", \"" + headerLength[i] + "\", \"" + isNullAble[i] + "\" });\r\n");
			}

		}
		sb_text.append("tdf.setPkFieldName(\"id\");\r\n");

		sb_text.append("tdf.setIndexName(new String[] { \"code\" });\r\n");
		sb_text.append("list.add(tdf);\r\n");
		return sb_text.toString(); //
	}

	public static void main(String[] _args) {
		String str_sql = new WLTDictDMO().getCreateSQLByTabDefineName("ORACLE", "pub_user"); //
		System.out.println(str_sql); //

	}
}
