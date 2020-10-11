package cn.com.infostrategy.bs.sysapp.transferdb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;

public class ExportDBDMOMysql2Sqlserver {

	HashMap mapSqlServer_keyWord = null;
	HashMap mapMysql2SqlserverDatatype = null;
	ArrayList listSys = null;

	public ExportDBDMOMysql2Sqlserver() {
	}

	public String getTablesSchema() throws WLTRemoteException, Exception {
		File fw = new File("c:/mysql2sqlserver/diferenttablename&&columnname.sql");
		PrintWriter pf = new PrintWriter(new FileOutputStream(fw), true);
		
		CommDMO dmo = new CommDMO(); // dmo的作用 DataManagerObject
		StringBuffer sb_return = new StringBuffer(); //
		HashVO[] hvs_tables = dmo.getHashVoArrayByDS(null, "select * from tables where table_schema like 'bjbank' "); //
		HashVO[] hvs_columns = dmo.getHashVoArrayByDS(null, "select * from columns where table_schema like 'bjbank' "); //

		for (int i = 0; i < hvs_tables.length; i++) {
			String tableName = hvs_tables[i].getStringValue("table_name");
		
			String converttablename = getConvertKeyWord(tableName); //
			if (!tableName.equals(converttablename)) {
				pf.println("tablename  : " + tableName);
			}
			sb_return.append("create table " + converttablename + " (\r\n");
			HashVO[] hvs_cls = getAllColumns(hvs_columns, hvs_tables[i].getStringValue("table_name"));
			for (int j = 0; j < hvs_cls.length; j++) {
				String columName = hvs_cls[j].getStringValue("column_name");
				String convertcolname = getConvertKeyWord(columName); //
				if (!columName.equals(convertcolname)) {//
					pf.println("tablename  : " + tableName + "-->columnname : " + columName);
				}//
				String datatype = getDataType(hvs_cls[j].getStringValue("data_type")); //
				String convertdatatype = getConvertDatatype(datatype);

				String datalength = getDataLength(hvs_cls[j].getStringValue("column_type"));

				sb_return.append(convertcolname + " " + convertdatatype + datalength);
				if (j != hvs_cls.length - 1) {
					sb_return.append(",\n");
				}
			}
			sb_return.append("\n);\n\n");
		}
		pf.close();
		return sb_return.toString(); //
	}

	private HashVO[] getAllColumns(HashVO[] _hvs_columns, String _tablename) {
		Vector v_cols = new Vector(); //
		for (int i = 0; i < _hvs_columns.length; i++) {
			if (_hvs_columns[i].getStringValue("table_name").equals(_tablename)) {
				v_cols.add(_hvs_columns[i]); //
			}
		}
		return (HashVO[]) v_cols.toArray(new HashVO[0]); //
	}

	private String getDataType(String _datatype) {
		if (_datatype.indexOf("(") > 0) {
			return _datatype.substring(0, _datatype.indexOf("("));
		} else {
			return _datatype;
		}
	}

	private String getDataLength(String _datatype) {
		if (_datatype.indexOf("(") > 0) {
			if (_datatype.startsWith("date") || _datatype.indexOf("text") >= 0) {
				return "";
			} else {
				return "(" + _datatype.substring(_datatype.indexOf("(") + 1, _datatype.length() - 1) + ")";
			}
		} else {
			return "";
		}
	}

	private String getConvertDatatype(String _type) {
		if (getSQLToMysqlDatatype().containsKey(_type)) {
			return (String) getSQLToMysqlDatatype().get(_type);
		} else {
			return _type;
		}
	}

	private HashMap getSQLToMysqlDatatype() {
		if (mapMysql2SqlserverDatatype != null) {
			return mapMysql2SqlserverDatatype;
		}

		mapMysql2SqlserverDatatype = new HashMap<String, String>();
		mapMysql2SqlserverDatatype.put("text", "text");
		mapMysql2SqlserverDatatype.put("datetime", "char(19)");
		mapMysql2SqlserverDatatype.put("nvarchar", "varchar");
		return mapMysql2SqlserverDatatype; //
	}

	private String getConvertKeyWord(String _type) {
		if (getSQLToMysqlKeyWord().containsKey(_type)) {
			return (String) getSQLToMysqlKeyWord().get(_type);
		} else {
			return _type;
		}
	}

	private HashMap getSQLToMysqlKeyWord() {
		if (mapSqlServer_keyWord != null) {
			return mapSqlServer_keyWord;
		}

		mapSqlServer_keyWord = new HashMap();
		mapSqlServer_keyWord.put("dual", "dual_mysql");
		mapSqlServer_keyWord.put("show", "isshow");
		mapSqlServer_keyWord.put("explain", "explainor");
		mapSqlServer_keyWord.put("sql", "sqlcontent");
		mapSqlServer_keyWord.put("condition", "conditionor");
		return mapSqlServer_keyWord; //
	}

	private ArrayList getmapSys() {
		if (listSys != null) {
			return listSys;
		}

		listSys.add("syssegments");
		listSys.add("sysconstraints");
		return listSys; //
	}

	public String getDBData() throws WLTRemoteException, Exception {
		CommDMO dmo = new CommDMO(); // dmo的作用 DataManagerObject
		StringBuffer sb_return = new StringBuffer(); //
		HashVO[] hvs_tables = dmo.getHashVoArrayByDS(null, "select * from v_pub_systables"); //
		for (int i = 0; i < hvs_tables.length; i++) {
			String tableName = hvs_tables[i].getStringValue("tabcode");
			String converttablename = getConvertKeyWord(tableName); //
			HashVO[] db = dmo.getHashVoArrayByDS(null, "select * from " + tableName); //
			for (int j = 0; j < db.length; j++) {
				sb_return.append("insert into " + converttablename + " values ( ");
				for (int k = 0; k < db[i].length(); k++) {
					sb_return.append(db[i].getStringValue(k));
					if (j != db.length - 1) {
						sb_return.append(",");
					}
				}
				sb_return.append(");\n");
			}
		}
		return sb_return.toString();
	}

	public Hashtable getData() throws WLTRemoteException, Exception {
		CommDMO test = new CommDMO();
		HashVO[] table_name_VO = test.getHashVoArrayByDS(null, "select * from v_pub_systables");
		Hashtable htReturn = new Hashtable(); //
		
		for (int i = 0; i < table_name_VO.length; i++) {
			StringBuffer insert_str = new StringBuffer();
			//System.out.println("" + (i + 1) + "" + table_name_VO[i].getStringValue("tabname"));
			String tableName = table_name_VO[i].getStringValue("tabname");
			String converttablename = getConvertKeyWord(tableName); //
			if (!tableName.startsWith("pub_")) {
				continue;
			}
			
			HashVOStruct hvostr = test.getHashVoStructByDS(null, "select * from " + tableName);
			String[] str_allkeys = hvostr.getHeaderName();
			String[] str_alltypes = hvostr.getHeaderTypeName(); //
			HashVO[] hvs_values = hvostr.getHashVOs(); //
			if (hvs_values != null && hvs_values.length > 0) {
				for (int j = 0; j < hvs_values.length; j++) {

					insert_str.append("insert into " + tableName + "("); //
					for (int k = 0; k < str_allkeys.length; k++) {
						insert_str.append(" " + str_allkeys[k] + " "); //

						if (k != str_allkeys.length - 1) {
							insert_str.append(",");
						}
					}

					insert_str.append(" ) values ( "); //

					for (int k = 0; k < str_allkeys.length; k++) {
						if (hvs_values[j].getStringValue(str_allkeys[k]) == null) {
							insert_str.append("null");//
						} else {
							String tab_cloumns_type_str = str_alltypes[k];
							//							if (tab_cloumns_type_str.startsWith("datetime")) { //数字
							//								insert_str.append(hvs_values[j].getStringValue(str_allkeys[k]));//
							//							} else {
							insert_str.append(" '" + hvs_values[j].getStringValue(str_allkeys[k]) + "' ");//
							//							}
						}

						if (k != str_allkeys.length - 1) {
							insert_str.append(",");
						}
					}
					insert_str.append(" );\n"); //
				}
			}
			if (i < table_name_VO.length - 1) {
				insert_str.append("");
			}

			htReturn.put(converttablename, insert_str.toString()); //
		}

		return htReturn;
	}

	public String getViewDefines() throws WLTRemoteException, Exception {
		CommDMO dmo = new CommDMO(); // dmo的作用 DataManagerObject
		StringBuffer sb_return = new StringBuffer(); //
		HashVO[] hvs_views = dmo.getHashVoArrayByDS(null, "select * from information_schema.views where table_schema = 'bjbank' "); //
		for (int i = 0; i < hvs_views.length; i++) {
			String viewsdefinition = hvs_views[i].getStringValue("view_definition");
			String tablename = hvs_views[i].getStringValue("table_name");

			if (tablename == null || tablename.indexOf("syssegments") > -1 || tablename.indexOf("sysconstraints") > -1) {
				continue;
			}
			if (viewsdefinition != null) {
				viewsdefinition = viewsdefinition.replaceAll("dbo.", "");
				viewsdefinition = viewsdefinition.replaceAll("condition", "conditionor");
				String[] str_items = new TBUtil().split(viewsdefinition, "\n");
				for (int j = 0; j < str_items.length; j++) {
					//System.out.println();  
					if (str_items[j].indexOf("--") > 0) {
						sb_return.append(str_items[j].substring(0, str_items[j].indexOf("--")) + "\n"); //
					} else {
						sb_return.append(str_items[j] + "\n"); //
					}
				}
				sb_return.append(";\n\n");
			}
		}
		return sb_return.toString(); //
	}

	public void showTable() throws Exception {
		CommDMO test = new CommDMO();
		HashVO[] table_name_VO = test.getHashVoArrayByDS(null, "select * from v_pub_systables");

		File fw = new File("c:/insert.sql");
		PrintWriter pf = new PrintWriter(new FileOutputStream(fw), true);

		for (int i = 0; i < table_name_VO.length; i++) {
			//System.out.println("" + (i + 1) + "" + table_name_VO[i].getStringValue("tabname"));
			String tabname_str = table_name_VO[i].getStringValue("tabname");
			if (!tabname_str.startsWith("pub_")) {
				continue;
			}

			HashVOStruct hvostr = test.getHashVoStructByDS(null, "select * from " + tabname_str);
			String[] str_allkeys = hvostr.getHeaderName();
			String[] str_alltypes = hvostr.getHeaderTypeName(); //
			HashVO[] hvs_values = hvostr.getHashVOs(); //
			if (hvs_values != null && hvs_values.length > 0) {
				for (int j = 0; j < hvs_values.length; j++) {
					StringBuffer insert_str = new StringBuffer();
					insert_str.append("insert into " + tabname_str + "("); //
					for (int k = 0; k < str_allkeys.length; k++) {
						insert_str.append(" " + str_allkeys[k] + " "); //

						if (k != str_allkeys.length - 1) {
							insert_str.append(",");
						}
					}

					insert_str.append(" ) values ( "); //

					for (int k = 0; k < str_allkeys.length; k++) {
						if (hvs_values[j].getStringValue(str_allkeys[k]) == null) {
							insert_str.append("null");//
						} else {
							String tab_cloumns_type_str = str_alltypes[k];
							if (tab_cloumns_type_str.startsWith("datetime")) { //数字
								insert_str.append(hvs_values[j].getStringValue(str_allkeys[k]));//
							} else {
								insert_str.append(" '" + hvs_values[j].getStringValue(str_allkeys[k]) + "' ");//
							}
						}

						if (k != str_allkeys.length - 1) {
							insert_str.append(",");
						}
					}
					insert_str.append(" )"); //
					pf.println(insert_str.toString());
				}
			}
		}
		pf.close();
	}

	public void getAllTableName() throws WLTRemoteException, Exception {

		File fwsqlserver = new File("c:/sql/allselectsqlserver.sql");
		PrintWriter pfserver = new PrintWriter(new FileOutputStream(fwsqlserver), true);
		File fwmysql = new File("c:/sql/allselectmysql.sql");
		PrintWriter pfmysql = new PrintWriter(new FileOutputStream(fwmysql), true);
		CommDMO dmo = new CommDMO(); // dmo的作用 DataManagerObject
		StringBuffer sb_return_sqlserver = new StringBuffer(); //
		StringBuffer sb_return_mysql = new StringBuffer(); //
		HashVO[] hvs_tables = dmo.getHashVoArrayByDS(null, "select * from v_pub_systables"); //
		for (int i = 0; i < hvs_tables.length; i++) {
			String tableName = hvs_tables[i].getStringValue("tabcode");
			String converttablename = getConvertKeyWord(tableName); //
			sb_return_sqlserver.append("select count(*) from ").append(tableName);
			if (i != hvs_tables.length - 1) {
				sb_return_sqlserver.append(" union \n");
			}
			sb_return_mysql.append("select count(*) from ").append(converttablename);
			if (i != hvs_tables.length - 1) {
				sb_return_mysql.append(" union \n");
			}
		}
		pfserver.print(sb_return_sqlserver.toString());
		pfserver.close();
		pfmysql.print(sb_return_sqlserver.toString());
		pfmysql.close();
	}
}
