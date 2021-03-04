package cn.com.infostrategy.bs.sysapp.transferdb;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.UIUtil;

public class ExportDBDMO {

	ArrayList listSys = null;

	ConvertAdapterIfc convertAdap = null;

	TBUtil tbutil = new TBUtil(); //

	String _sourceDBType = null;

	String _destDBType = null;

	public ExportDBDMO() {
	}

	public ExportDBDMO(String _sourceDBType, String _destDBType) {
		this._sourceDBType = _sourceDBType;
		this._destDBType = _destDBType;
		this.convertAdap = ConvertFactory.getInstance().createConvertAdapter(_sourceDBType, _destDBType);
	}

	public String getTablesSchema(String _resourceds) throws WLTRemoteException, Exception {

		// 需要转换的表名和列名 ,写入文件中
		File fw = new File("c:/diferenttablename&&columnname.sql");
		PrintWriter pf = new PrintWriter(new FileOutputStream(fw), true);

		CommDMO dmo = new CommDMO(); // dmo的作用 DataManagerObject
		StringBuffer sb_return = new StringBuffer(); //
		HashVO[] hvs_tables = dmo.getHashVoArrayByDS(_resourceds, "select * from v_pub_systables"); //
		HashVO[] hvs_columns = dmo.getHashVoArrayByDS(_resourceds, "select * from v_pub_syscolumns"); //

		for (int i = 0; i < hvs_tables.length; i++) {
			String tableName = hvs_tables[i].getStringValue("tabcode");
			String converttablename = getConvertKeyWord(tableName); //
			if (!tableName.equals(converttablename)) {
				pf.println("tablename  : " + tableName);
			}
			sb_return.append("create table " + converttablename + " (\r\n");
			HashVO[] hvs_cls = getAllColumns(hvs_columns, hvs_tables[i].getStringValue("tabcode"));
			for (int j = 0; j < hvs_cls.length; j++) {
				String columName = hvs_cls[j].getStringValue("colname");
				String convertcolname = getConvertKeyWord(columName); //
				if (!columName.equals(convertcolname)) {//
					pf.println("tablename  : " + tableName + "-->columnname : " + columName);
				}//
				String datatype = getDataType(hvs_cls[j].getStringValue("datatype")); //
				String convertdatatype = getConvertDatatype(datatype);

				String datalength = getDataLength(hvs_cls[j].getStringValue("datatype"));

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
			if (_hvs_columns[i].getStringValue("tabname").equals(_tablename)) { //
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
		if (getConvertAdap().getConvertDataType().containsKey(_type)) {
			return (String) getConvertAdap().getConvertDataType().get(_type);
		} else {
			return _type;
		}
	}

	private String getConvertKeyWord(String _type) {
		if (getConvertAdap().getKeyWorld().containsKey(_type)) {
			return (String) getConvertAdap().getKeyWorld().get(_type);
		} else {
			return _type;
		}
	}

	private ArrayList getmapSys() {
		if (listSys != null) {
			return listSys;
		}

		listSys.add("syssegments");
		listSys.add("sysconstraints");
		return listSys; //
	}

	/**
	 *  自己写的没用的方法
	 * @return  String 
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
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

	/**
	 *  本方法是为提供把源数据库的数据写成一个数据库一个文件的方式而提供的,所以返回的是一个 HashMap
	 *  HashMap key值是数据库的表名, value 是insert语句,一条记录一条
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	public Hashtable getDataAsText() throws WLTRemoteException, Exception {

		CommDMO commDmo = new CommDMO();
		HashVO[] table_name_VO = commDmo.getHashVoArrayByDS(null, "select * from v_pub_systables");
		Hashtable htReturn = new Hashtable(); //

		System.out.println("共有表" + table_name_VO.length + "个!");

		StringBuffer insert_str = new StringBuffer();
		for (int i = 0; i < table_name_VO.length; i++) {
			//			StringBuffer insert_str = new StringBuffer();
			//			StringBuffer insert_str = new StringBuffer();
			String tableName = table_name_VO[i].getStringValue("tabname");

//			if (tableName.startsWith("law_content")||tableName.startsWith("pub_billcelltemplet_d")) {
//				continue;
//			}

			if(table_name_VO.length>8000){
				continue;
			}
			if ("oracle".equalsIgnoreCase(_destDBType)) {
				if (tableName.equals("cmp_cmpfile") || tableName.equals("cmp_riskstep")) {
					String s = dealWithTableClob(tableName, null);
					htReturn.put(tableName, s);
					continue;
				}
			}
			String converttablename = getConvertKeyWord(tableName); //
			insert_str.append("truncate table ").append(converttablename).append(";\n");

			HashVOStruct hvostr = commDmo.getHashVoStructByDS(null, "select * from " + tableName);

			String[] str_allkeys = hvostr.getHeaderName();
			String[] str_alltypes = hvostr.getHeaderTypeName(); //
			HashVO[] hvs_values = hvostr.getHashVOs(); //

			if (hvs_values != null && hvs_values.length > 0) {
				for (int j = 0; j < hvs_values.length; j++) {

					insert_str.append("insert into ").append(converttablename).append(" (");
					for (int k = 0; k < str_allkeys.length; k++) {
						String columnname = str_allkeys[k];
						String conventcolumnname = getConvertKeyWord(str_allkeys[k]);
						insert_str.append(" " + conventcolumnname + " "); //

						if (k != str_allkeys.length - 1) {
							insert_str.append(",");
						}

					}

					insert_str.append(" ) values ( "); //

					for (int k = 0; k < str_allkeys.length; k++) {
						if (hvs_values[j].getStringValue(str_allkeys[k]) == null || hvs_values[j].getStringValue(str_allkeys[k]).trim().length() == 0) {
							insert_str.append("null");//
						} else {
							String tab_cloumns_type_str = str_alltypes[k];

							String temp_str = hvs_values[j].getStringValue(k);
							String temp_str1 = tbutil.replaceAll(temp_str, "\\", "\\\\");
							temp_str1 = tbutil.replaceAll(temp_str1, "\'", "\'\'");
							insert_str.append(" '" + temp_str1 + "' ");//

						}

						if (k != str_allkeys.length - 1) {
							insert_str.append(",");
						}

					}
					insert_str.append(" );\n"); //
				}
			}
			htReturn.put(converttablename, insert_str.toString()); //
			insert_str.delete(0, insert_str.length());
		}
		System.out.println("共导出表 " + htReturn.size() + "个!");
		return htReturn;
	}

	/**
	 * 
	 */
	public void getDataBySql(String resourceDBName, String destDBName) throws WLTRemoteException, Exception {

		CommDMO commDmo = new CommDMO();
		HashVO[] table_name_VO = commDmo.getHashVoArrayByDS(resourceDBName, "select * from v_pub_systables");
		Hashtable htReturn = new Hashtable(); //

		for (int i = 0; i < table_name_VO.length; i++) {
			List sqls = new ArrayList();
			String tableName = table_name_VO[i].getStringValue("tabname");

			String converttablename = getConvertKeyWord(tableName); //

			sqls.add("truncate table " + converttablename);

			HashVOStruct hvostr = commDmo.getHashVoStructByDS(resourceDBName, "select * from " + tableName);

			String[] str_allkeys = hvostr.getHeaderName();
			String[] str_alltypes = hvostr.getHeaderTypeName(); //
			HashVO[] hvs_values = hvostr.getHashVOs(); //

			if (hvs_values != null && hvs_values.length > 0) {
				for (int j = 0; j < hvs_values.length; j++) {

					StringBuffer sb_sql = new StringBuffer();
					sb_sql.append("insert into ").append(converttablename).append("(");
					for (int k = 0; k < str_allkeys.length; k++) {
						String columnname = str_allkeys[k];
						String conventcolumnname = getConvertKeyWord(str_allkeys[k]);
						sb_sql.append(conventcolumnname);

						if (k != str_allkeys.length - 1) {
							sb_sql.append(",");
						}

					}

					sb_sql.append(" ) values ( "); //

					for (int k = 0; k < str_allkeys.length; k++) {
						if (hvs_values[j].getStringValue(str_allkeys[k]) == null || hvs_values[j].getStringValue(str_allkeys[k]).trim().length() == 0) {
							sb_sql.append("null");//
						} else {
							String tab_cloumns_type_str = str_alltypes[k];

							String temp_str = hvs_values[j].getStringValue(k);
							String temp_str1 = tbutil.replaceAll(temp_str, "\\", "\\\\");
							temp_str1 = tbutil.replaceAll(temp_str1, "\'", "\'\'");
							sb_sql.append(" '" + temp_str1 + "' ");//

						}

						if (k != str_allkeys.length - 1) {
							sb_sql.append(",");
						}

					}
					sb_sql.append(")"); //
					sqls.add(sb_sql.toString());
				}
			}

			commDmo.executeBatchByDS(destDBName, sqls);
			//htReturn.put(converttablename, insert_str.toString()); //

		}
		//return htReturn;
	}

	public void getData(String _resourceDSName, String _destDSName) throws WLTRemoteException, Exception {
		CommDMO comDmp = new CommDMO();
		HashVO[] table_name_VO = comDmp.getHashVoArrayByDS(_resourceDSName, "select * from v_pub_systables");
		Hashtable htReturn = new Hashtable(); //

		for (int i = 0; i < table_name_VO.length; i++) {
			//StringBuffer insert_str = new StringBuffer();
			String tableName = table_name_VO[i].getStringValue("tabname");
			//			if (!tableName.startsWith("pub_")) {
			//				continue;
			//			}

			if (tableName.startsWith("Sheet2$")) {
				continue;
			}

			String converttablename = getConvertKeyWord(tableName); //
			//insert_str.append("truncate table ").append(converttablename).append(";\n");
			HashVOStruct hvostr = comDmp.getHashVoStructByDS(null, "select * from " + tableName);
			System.out.println("处理表 : " + tableName + " 共有数据 " + hvostr.getHashVOs().length + "条!");
			comDmp.executeInsertData(_destDSName, converttablename, hvostr);
			System.out.println("表 " + tableName + "处理完毕!");
		}
	}

	public String getViewDefines(String _datasourcename) throws WLTRemoteException, Exception {
		CommDMO dmo = new CommDMO(); // dmo的作用 DataManagerObject
		StringBuffer sb_return = new StringBuffer(); //
		HashVO[] hvs_views = dmo.getHashVoArrayByDS(_datasourcename, "select * from information_schema.views"); //
		System.out.println("共有视图 " + hvs_views.length + " 个!");
		for (int i = 0; i < hvs_views.length; i++) {
			String viewsdefinition = hvs_views[i].getStringValue("view_definition");
			String tablename = hvs_views[i].getStringValue("table_name");

			if (tablename == null || tablename.indexOf("syssegments") > -1 || tablename.indexOf("sysconstraints") > -1) {
				continue;
			}
			if (viewsdefinition != null) {
				viewsdefinition = viewsdefinition.replaceAll("dbo.", "");
				//viewsdefinition = viewsdefinition.replaceAll("condition", "conditionor");
				String[] str_items = new TBUtil().split(viewsdefinition, "\n");
				for (int j = 0; j < str_items.length; j++) {
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

	public void transfDBViewsByDatasource(String _datasourcename) throws Throwable {
		CommDMO comDmp = new CommDMO();
		String viewsSql = "";
		comDmp.executeUpdateByDS(_datasourcename, viewsSql);

	}

	public void transferDBByDatasource() throws WLTRemoteException, Exception {

		CommDMO comDmp = new CommDMO();
		HashVO[] table_name_VO = comDmp.getHashVoArrayByDS(null, "select * from v_pub_systables");
		Hashtable htReturn = new Hashtable(); //

		for (int i = 0; i < table_name_VO.length; i++) {
			StringBuffer insert_str = new StringBuffer();
			String tableName = table_name_VO[i].getStringValue("tabname");
//			if (!tableName.startsWith("pub_") || !tableName.startsWith("cmp_")) {
//				continue;
//			}

			if(!tableName.equals("law_content")){
				continue;
			}
			String converttablename = getConvertKeyWord(tableName); //
			insert_str.append("truncate table ").append(converttablename).append(";\n");
			HashVOStruct hvostr = comDmp.getHashVoStructByDS(null, "select * from " + tableName);
			comDmp.executeInsertData("tcm_mysql", converttablename, hvostr);
		}

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

	public String dealWithTableClob(String tablename, String resourceDBName) throws Exception {

		CommDMO commDmo = new CommDMO();

		StringBuffer re_sb = new StringBuffer();
		HashVOStruct hvostr = commDmo.getHashVoStructByDS(resourceDBName, "select * from " + tablename);

		String[] str_allkeys = hvostr.getHeaderName();
		String[] str_alltypes = hvostr.getHeaderTypeName(); //
		HashVO[] hvs_values = hvostr.getHashVOs(); //

		for (int i = 0; i < hvs_values.length; i++) {
			StringBuffer sb = new StringBuffer();
			sb.append("declare   \n");

			// 建立不同的变量来
			for (int j = 0; j < str_alltypes.length; j++) {

				if (str_alltypes[j].indexOf("decimal") > -1) {
					sb.append("v_" + j + " number(10)");
				} else {
					if (str_alltypes[j].indexOf("varchar") > -1) {
						sb.append("v_" + j + " varchar2(4000) ");
					} else {
						if (str_alltypes[j].indexOf("char") > -1) {
							sb.append("v_" + j + " varchar2(1000)");
						} else {
							if (str_alltypes[j].indexOf("TEXT") > -1) {
								sb.append("v_" + j + " clob");
							} else {
								sb.append("v_" + j + " clob");
							}
						}
					}
				}
				String value = hvs_values[i].getStringValue(str_allkeys[j]);
				if (value != null) {
					value = UIUtil.replaceAll(value, "'", "''");
					value = UIUtil.replaceAll(value, "\\", "\\\\");
					sb.append(":='" + value + "';\n");
				} else {
					sb.append(":=null;\n");
				}
			}

			sb.append("\n begin \n");
			sb.append("insert into " + tablename + " values (");
			for (int j = 0; j < str_allkeys.length; j++) {
				sb.append("v_");
				sb.append(j);

				if (j != str_allkeys.length - 1) {
					sb.append(",");
				}
			}
			sb.append(");");
			sb.append("commit;\n");
			sb.append("end;\n");
			sb.append("/\n");
			re_sb.append(sb.toString());
		}
		return re_sb.toString();
	}

	/**
	 * 
	 */
	public void copyDataBetweenTwoDS(DataSourceVO _sourceDB, DataSourceVO _destDB) throws WLTRemoteException, Exception {
		String str_resourceDBName = _sourceDB.getName();
		String str_destDBName = _destDB.getName();

		CommDMO commDmo = new CommDMO();
		HashVO[] table_name_VO = commDmo.getHashVoArrayByDS(str_resourceDBName, "select * from v_pub_systables where tabname not like 'pub_%'"); //取出源数据库存中所有表
		Hashtable htReturn = new Hashtable(); //

		//遍历所有表
		StringBuffer sb_sql = null; //
		for (int i = 0; i < table_name_VO.length; i++) {
			String tableName = table_name_VO[i].getStringValue("tabname");
			System.out.println("开始同步表[" + tableName + "]数据........."); //
			try {
				List al_sqls = new ArrayList();
				String converttablename = tableName; //getConvertKeyWord(tableName); //
				al_sqls.add("truncate table " + converttablename);

				HashVOStruct hvostr = commDmo.getHashVoStructByDS(str_resourceDBName, "select * from " + tableName); //取出源数据库某表中的所有数据!!
				String[] str_allkeys = hvostr.getHeaderName(); //取得所有列名
				String[] str_alltypes = hvostr.getHeaderTypeName(); //取出所有列类型
				HashVO[] hvs_values = hvostr.getHashVOs(); ////取出所有数据

				if (hvs_values != null && hvs_values.length > 0) {
					int li_count = 0; //
					for (int j = 0; j < hvs_values.length; j++) { //遍历所有数据!!!
						sb_sql = new StringBuffer(); //
						sb_sql.append("insert into ").append(converttablename).append(" (");

						//拼出所有列名
						for (int k = 0; k < str_allkeys.length; k++) {
							String columnname = str_allkeys[k];
							String conventcolumnname = columnname; //getConvertKeyWord(str_allkeys[k]);
							sb_sql.append(conventcolumnname);
							if (k != str_allkeys.length - 1) {
								sb_sql.append(",");
							}
						}

						sb_sql.append(" ) values ( "); //

						for (int k = 0; k < str_allkeys.length; k++) {
							if (hvs_values[j].getStringValue(str_allkeys[k]) == null) {
								sb_sql.append("null");//
							} else {
								String colname = str_allkeys[k];
								String coltype = str_alltypes[k]; //
								String colvalue = hvs_values[j].getStringValue(colname); //
								colvalue = tbutil.replaceAll(colvalue, "'", "''");
								sb_sql.append(" '" + colvalue + "' ");//
							}

							if (k != str_allkeys.length - 1) {
								sb_sql.append(",");
							}
						}
						sb_sql.append(")"); //
						al_sqls.add(sb_sql.toString());

						li_count++;
						if (li_count >= 200) { //每100条提交一次!!
							commDmo.executeBatchByDSImmediately(str_destDBName, al_sqls); //
							al_sqls.clear(); //清空
							li_count = 0; //
						}
					}

					commDmo.executeBatchByDSImmediately(str_destDBName, al_sqls); //提交剩余的
					System.out.println("同步表[" + tableName + "]数据成功,共同步[" + hvs_values.length + "]条数据!!"); //
				} //遍历所有数据结束!!
			} catch (Exception exx) {
				System.err.println("同步表[" + tableName + "]数据失败,原因:" + exx.getMessage()); //
			}
		}

		System.out.println("同步数据结束!!!"); //
	}

	public ConvertAdapterIfc getConvertAdap() {
		return convertAdap;
	}

	public Vector updateSequence() throws Exception {
		Vector l = new Vector();
		CommDMO commDmo = new CommDMO();
		HashVO[] allSequences = commDmo.getHashVoArrayByDS(null, "select * from pub_sequence");

		for (int i = 0; i < allSequences.length; i++) {

			String vtableName = allSequences[i].getStringValue("sename");
			String oldId = allSequences[i].getStringValue("currvalue");
			Long oldValue = Long.parseLong(oldId);
			String tableName = vtableName.substring(2, vtableName.length());
			HashVO[] values = null;
			try {
				values = commDmo.getHashVoArrayByDS(null, "select max(id) val from " + tableName);
			} catch (Exception e) {

			}

			if (values == null) {
				continue;
			}
			String value = values[0].getStringValue("val");

			Long newValue = null;
			try {
				newValue = Long.parseLong(value) + 1;
			} catch (Exception e) {
			}
			if (newValue == null) {
				continue;
			}

			if (oldValue > newValue) {
				continue;
			}

			String sql = "update pub_sequence set currvalue=" + newValue + " where sename ='" + vtableName + "'";
			l.add(sql);
		}

		return l;
	}

}
