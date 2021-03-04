package cn.com.infostrategy.bs.sysapp.install.database;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.JDOMException;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTDBConnection;
import cn.com.infostrategy.bs.mdata.DataBaseValidate;
import cn.com.infostrategy.bs.sysapp.database.compare.WLTTableDf;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;

/**
 * 数据字典工具类
 * @author xch
 *
 */
public class DataBaseUtilDMO extends AbstractDMO {
	private CommDMO commDMO = null;
	private DataBaseValidate dbValidate = new DataBaseValidate();

	/**
	 * 孙富君实现
	 * 比较XML中定义的数据字典与实际数据库,返回结果!!
	 * @return  二维数组,第一列是表名,第二列是类型(创建表;创建列;修改列;删除列),第三列中说明,第四列是SQL
	 */
	public String[][] compareDictByDB(String datasourcename, String xmlPath) {
		if (datasourcename == null || datasourcename.trim().equals("")) {
			datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		String dbtype = ServerEnvironment.getInstance().getDataSourceVO(datasourcename).getDbtype();
		WLTTableDf[] allTdfs = getAllXMLTable(dbtype, xmlPath);
		List returnList = new ArrayList();
		for (int i = 0; i < allTdfs.length; i++) {
			try {
				List a = getCompareSQLByTabName(datasourcename, allTdfs[i]);
				if (a != null) {
					for (int j = 0; j < a.size(); j++) {
						returnList.add(a.get(j));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (returnList != null && returnList.size() > 0) {
			return (String[][]) returnList.toArray(new String[0][0]);
		}
		return null;
	}

	/**
	 * 数据源的比较 数据源1与数据源2的比较以2为标准
	 * @param datesourcename
	 * @return
	 */
	public String[][] compareDictByDBS(String datasourcename, String datasourcename2, String xmlPath) {
		WLTTableDf[] allTdfs = getAllDBTable(datasourcename2, xmlPath);
		List returnList = new ArrayList();
		if (allTdfs != null) {
			for (int i = 0; i < allTdfs.length; i++) {
				try {
					List a = getCompareSQLByTabName(datasourcename, allTdfs[i]);
					if (a != null) {
						for (int j = 0; j < a.size(); j++) {
							returnList.add(a.get(j));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		if (returnList != null && returnList.size() > 0) {
			return (String[][]) returnList.toArray(new String[0][0]);
		}
		return null;
	}

	/**
	 * 根据数据源得到所有业务表的create语句
	 * @param tablename
	 * @return
	 * @throws Exception 
	 */

	public String getAllCreateSql(String datasourcename, String type, String xmlPath) throws Exception {
		WLTDBConnection conn = getConn(datasourcename); // 取得数据库连接!!	
		if (xmlPath == null || "".equals(xmlPath)) {
			xmlPath = "/cn/com/infostrategy/bs/sysapp/install/database/tables.xml";
		}
		try {
			StringBuffer str_table = new StringBuffer();
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			str_table.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
			str_table.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
			str_table.append("</head>\r\n<body>\r\n");
			str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>导出" + type + "数据库sql</a></td></tr>");
			str_table.append("<tr>");
			str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>SQL\r\n");
			str_table.append("</td></tr>");
			StringBuffer sb = new StringBuffer();
			StringBuffer sb2 = new StringBuffer();
			Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			if (list_tables != null && list_tables.size() > 0) {
				for (int i = 0; i < list_tables.size(); i++) {
					org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
					String str_tname = param.getAttributeValue("name");
					try {
						sb.append("<tr bgcolor='#eeeeee'><td>\r\n");
						sb.append(new TBUtil().replaceAll(getCreateSQL(datasourcename, str_tname, type, null), "\r\n", "\r\n<br>"));
						sb.append("</td></tr>");
					} catch (Exception e) {
						sb2.append(new TBUtil().replaceAll(e.getMessage(), "\r\n", "") + "<br>");
						continue;
					}

				}
			}
			if (sb2 != null && sb2.length() > 0) {
				str_table.append(sb2 + "<br>");
			}
			str_table.append(sb);
			str_table.append("<table>");
			str_table.append("</tr></table><br><hr>\r\n");
			str_table.append("</body></html>");
			return str_table.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * xml中定义的某个表与数据库中比较
	 * @param datasourcename
	 * @param tablename
	 * @param xmlPath
	 * @return
	 */
	public String[][] compareTableDictByDB(String datasourcename, String tablename, String xmlPath) {
		if (datasourcename == null) {
			datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		WLTTableDf tdf = getXMLTableByTN(datasourcename, tablename, xmlPath);
		if (tdf != null) {
			List returnList = new ArrayList();
			try {
				List a = getCompareSQLByTabName(datasourcename, tdf);
				if (a != null) {
					for (int j = 0; j < a.size(); j++) {
						returnList.add(a.get(j));
					}
				}
				if (returnList != null && returnList.size() > 0) {
					return (String[][]) returnList.toArray(new String[0][0]);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;

	}

	/**
	 * 2个数据源的比较 datasourcename为需要操作数据源 datasourcename2为标准
	 * @param tablename
	 * @return
	 */
	public String[][] compareDictByDB2(String datasourcename, String datasourcename2, String tablename) {
		WLTTableDf tdf = getAllDBTableByName(datasourcename2, tablename);
		if (tdf != null) {
			List returnList = new ArrayList();
			try {
				List a = getCompareSQLByTabName(datasourcename, tdf);
				if (a != null) {
					for (int j = 0; j < a.size(); j++) {
						returnList.add(a.get(j));
					}
				}
				if (returnList != null && returnList.size() > 0) {
					return (String[][]) returnList.toArray(new String[0][0]);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		return null;

	}

	/**
	 * 2个数据源的比较其中以2为标准1需执行那些sql
	 * @param dsTabTD1
	 * @param dsTabTD2
	 * @return
	 */
	private List getCompareSQLByDateSource(TableDataStruct dsTabTD1, TableDataStruct dsTabTD2) {
		String[] headerName1 = dsTabTD1.getHeaderName();
		String[] headerTypeName1 = dsTabTD1.getHeaderTypeName();
		int[] headerLength1 = dsTabTD1.getHeaderLength();
		int[] headerType1 = dsTabTD1.getHeaderType();
		String[] isNullAble1 = dsTabTD1.getIsNullAble(); //字段是否为空
		String[] headerName2 = dsTabTD2.getHeaderName();
		String[] headerTypeName2 = dsTabTD2.getHeaderTypeName();
		int[] headerLength2 = dsTabTD2.getHeaderLength();
		int[] headerType2 = dsTabTD2.getHeaderType();
		String[] isNullAble2 = dsTabTD2.getIsNullAble(); //字段是否为空
		return null;
	}

	private List getCompareSQLByTabName(String _dataSourceName, WLTTableDf _tabDf) throws Exception {
		if (_dataSourceName == null || "".equals(_dataSourceName)) {
			_dataSourceName = ServerEnvironment.getDefaultDataSourceName();
		}
		String _dbType = ServerEnvironment.getInstance().getDataSourceVO(_dataSourceName).getDbtype();
		List returnList = new ArrayList();
		//String _dbType = ServerEnvironment.getDefaultDataSourceType();
		HashMap typeMap = getTypeMap(_dbType);
		TableDataStruct dsTabTD = null;
		//先取出数据源中该表的实际定义
		try {
			dsTabTD = new CommDMO().getTableDataStructByDS(_dataSourceName, "select * from " + _tabDf.getTableName() + " where 1=2");
		} catch (Exception e) {
			//e.printStackTrace();
			dsTabTD = null;
		}
		//再取出定义中的表结构原数据对象
		WLTTableDf tabdef = _tabDf;

		//下面就根据数据库类型,实际表定义,数据字典的定义，这三个值进行比较!!生成返回结果!!
		if (dsTabTD == null && tabdef != null) {
			String[] sqlanddesc = getCreateSQLAndDesc(tabdef, _dbType);//获得sql和是否有关键字的说明【李春娟/2013-05-24】
			returnList.add(new String[] { _tabDf.getTableName(), "创建表", "数据库中没有,创建之", sqlanddesc[0], sqlanddesc[1] });
			return returnList;
		}
		if (tabdef != null && dsTabTD != null) {
			StringBuffer dsTabTDHave = new StringBuffer();
			StringBuffer tabdefHave = new StringBuffer();
			HashMap bothHaveBut = new HashMap();
			HashMap dsTabTDHeaderNameMap = new HashMap();
			HashMap tabdefHeaderNameMap = new HashMap();
			HashMap dsTabTDColLengthMap = new HashMap();
			HashMap tabdefColLengthMap = new HashMap();
			HashMap dsTabTDColTypeMap = new HashMap();
			HashMap tabdefColTypeMap = new HashMap();
			int dsTabTDClength = dsTabTD.getHeaderLength().length;
			int tabdefClength = tabdef.getColDefines().length;
			int maxLength = dsTabTDClength;
			if (tabdefClength > dsTabTDClength) {
				maxLength = tabdefClength;
			}
			for (int i = 0; i < maxLength; i++) {
				if (i < dsTabTDClength) {
					dsTabTDHeaderNameMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), i);
					if ("decimal".equals(typeMap.get(dsTabTD.getHeaderTypeName()[i].toLowerCase())) || "number".equals(typeMap.get(dsTabTD.getHeaderTypeName()[i].toLowerCase())) || "integer".equals(typeMap.get(dsTabTD.getHeaderTypeName()[i].toLowerCase()))) {
						dsTabTDColLengthMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getPrecision()[i] + "," + dsTabTD.getScale()[i]);
					} else {
						dsTabTDColLengthMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getPrecision()[i] + "");
					}
					dsTabTDColTypeMap.put(dsTabTD.getHeaderName()[i].toLowerCase(), dsTabTD.getHeaderTypeName()[i]);
				}
				if (i < tabdefClength) {
					tabdefHeaderNameMap.put(tabdef.getColDefines()[i][0].toLowerCase(), i);
					tabdefColLengthMap.put(tabdef.getColDefines()[i][0].toLowerCase(), tabdef.getColDefines()[i][3]);
					tabdefColTypeMap.put(tabdef.getColDefines()[i][0].toLowerCase(), tabdef.getColDefines()[i][2]);
				}
			}
			String dblength = null;
			String deflength = null;
			String dbtype = null;
			String deftype = null;
			for (int i = 0; i < maxLength; i++) {

				if (i < dsTabTDClength) {
					if (!tabdefHeaderNameMap.containsKey(dsTabTD.getHeaderName()[i].toLowerCase())) {
						dsTabTDHave.append(dsTabTD.getHeaderName()[i].toLowerCase() + "、");
					} else {
						dblength = ((String) dsTabTDColLengthMap.get(dsTabTD.getHeaderName()[i].toLowerCase())).toLowerCase();
						deflength = ((String) tabdefColLengthMap.get(dsTabTD.getHeaderName()[i].toLowerCase())).toLowerCase();
						dbtype = (String) typeMap.get(dsTabTDColTypeMap.get(dsTabTD.getHeaderName()[i].toLowerCase()).toString().toLowerCase());
						deftype = (String) tabdefColTypeMap.get(dsTabTD.getHeaderName()[i].toLowerCase()).toString().toLowerCase();
						//增加一样的长度与类型判断bothHaveBut里面放
						if ((!dblength.equalsIgnoreCase(deflength) && !dblength.equalsIgnoreCase(deflength + ",0")) || !dbtype.equalsIgnoreCase(deftype)) {
							if (dbtype.equalsIgnoreCase("text") || dbtype.equalsIgnoreCase("clob")) {
								if (!dbtype.equalsIgnoreCase(deftype)) {
									String newValue = sureTypeOrLength(bothHaveBut, dsTabTD.getHeaderName()[i].toLowerCase(), deftype + "(" + deflength + ")", "类型不同");
									bothHaveBut.put(dsTabTD.getHeaderName()[i].toLowerCase(), newValue);
								}
							} else {
								if ((!dblength.equalsIgnoreCase(deflength) && !dblength.equalsIgnoreCase(deflength + ",0"))) {
									String newValue = sureTypeOrLength(bothHaveBut, dsTabTD.getHeaderName()[i].toLowerCase(), deftype + "(" + deflength + ")", "长度不同");
									bothHaveBut.put(dsTabTD.getHeaderName()[i].toLowerCase(), newValue);
								}
								if (!dbtype.equalsIgnoreCase(deftype)) {
									String newValue = sureTypeOrLength(bothHaveBut, dsTabTD.getHeaderName()[i].toLowerCase(), deftype + "(" + deflength + ")", "类型不同");
									bothHaveBut.put(dsTabTD.getHeaderName()[i].toLowerCase(), newValue);
								}
							}
						}
					}
				}
				if (i < tabdefClength) {
					if (!dsTabTDHeaderNameMap.containsKey(tabdef.getColDefines()[i][0].toLowerCase())) {
						tabdefHave.append(tabdef.getColDefines()[i][0].toLowerCase() + "、");
					} else {
						dblength = ((String) dsTabTDColLengthMap.get(tabdef.getColDefines()[i][0].toLowerCase())).toLowerCase();
						deflength = ((String) tabdefColLengthMap.get(tabdef.getColDefines()[i][0].toLowerCase())).toLowerCase();
						dbtype = (String) typeMap.get(((String) dsTabTDColTypeMap.get(tabdef.getColDefines()[i][0].toLowerCase())).toLowerCase());
						deftype = (String) typeMap.get(((String) tabdefColTypeMap.get(tabdef.getColDefines()[i][0].toLowerCase())).toLowerCase());
						//增加一样的长度与类型判断bothHaveBut里面放//clobtext不判断长度了
						if ((!dblength.equalsIgnoreCase(deflength) && !dblength.equalsIgnoreCase(deflength + ",0")) || !dbtype.equalsIgnoreCase(deftype)) {
							if (dbtype.equalsIgnoreCase("text") || dbtype.equalsIgnoreCase("clob")) {
								if (!dbtype.equalsIgnoreCase(deftype)) {
									String newValue = sureTypeOrLength(bothHaveBut, tabdef.getColDefines()[i][0].toLowerCase(), deftype + "(" + deflength + ")", "类型不同");
									bothHaveBut.put(tabdef.getColDefines()[i][0].toLowerCase(), newValue);
								}
							} else {
								if ((!dblength.equalsIgnoreCase(deflength) && !dblength.equalsIgnoreCase(deflength + ",0"))) {
									String newValue = sureTypeOrLength(bothHaveBut, tabdef.getColDefines()[i][0].toLowerCase(), deftype + "(" + deflength + ")", "长度不同");
									bothHaveBut.put(tabdef.getColDefines()[i][0].toLowerCase(), newValue);
								}
								if (!dbtype.equalsIgnoreCase(deftype)) {
									String newValue = sureTypeOrLength(bothHaveBut, tabdef.getColDefines()[i][0].toLowerCase(), deftype + "(" + deflength + ")", "类型不同");
									bothHaveBut.put(tabdef.getColDefines()[i][0].toLowerCase(), newValue);
								}
							}
						}
					}
				}
			}
			if (tabdefHave != null && tabdefHave.length() > 0) {
				//StringBuffer sb = new StringBuffer();
				String[] cols = tabdefHave.toString().split("、");
				for (int ii = 0; ii < cols.length; ii++) {
					if (cols[ii] != null && !"".equals(cols[ii])) {
						//sb.append(getAlterSQLByTabDefineName(null, _tabDf.getTableName(), cols[ii], (String) tabdefColTypeMap.get(cols[ii]), (String) tabdefColLengthMap.get(cols[ii])));
						returnList.add(new String[] { _tabDf.getTableName(), "创建列", "数据库中没有,创建之", getAlterSQLByTabDefineName(_dbType, _tabDf.getTableName(), cols[ii], (String) tabdefColTypeMap.get(cols[ii]), (String) tabdefColLengthMap.get(cols[ii])) });
					}
				}
			}
			if (dsTabTDHave != null && dsTabTDHave.length() > 0) {
				//StringBuffer sb = new StringBuffer();
				String[] cols = dsTabTDHave.toString().split("、");
				for (int ii = 0; ii < cols.length; ii++) {
					if (cols[ii] != null && !"".equals(cols[ii]))
						returnList.add(new String[] { _tabDf.getTableName(), "删除列", "定义中没有,数据库有,删除之", getAlterDeleteSQLByTabDefineName(_dbType, _tabDf.getTableName(), cols[ii]) });
				}
			}
			if (bothHaveBut != null && bothHaveBut.size() > 0) {
				//输出长度类型不一致的alter
				String[] keys = (String[]) bothHaveBut.keySet().toArray(new String[0]);
				for (int jj = 0; jj < keys.length; jj++) {
					String value = (String) bothHaveBut.get(keys[jj]);
					if (value.indexOf("类型不同") >= 0 && value.indexOf("长度不同") >= 0) {
						returnList.add(new String[] { _tabDf.getTableName(), "修改列", "数据库中与定义的类型与长度不同,修改之(" + dsTabTDColTypeMap.get(keys[jj].toLowerCase()) + "(" + dsTabTDColLengthMap.get(keys[jj].toLowerCase()) + ")" + ")", getAlterModifySQLByTabDefineName(_tabDf.getTableName(), keys[jj], value.split(";")[1], _dbType) });
					}
					if (value.indexOf("类型不同") >= 0 && value.indexOf("长度不同") < 0) {
						returnList.add(new String[] { _tabDf.getTableName(), "修改列", "数据库中与定义的类型不同,修改之(" + dsTabTDColTypeMap.get(keys[jj].toLowerCase()) + ")", getAlterModifySQLByTabDefineName(_tabDf.getTableName(), keys[jj], value.split(";")[1], _dbType) });
					}
					if (value.indexOf("类型不同") < 0 && value.indexOf("长度不同") >= 0) {
						returnList.add(new String[] { _tabDf.getTableName(), "修改列", "数据库中与定义的长度不同,修改之(" + dsTabTDColLengthMap.get(keys[jj].toLowerCase()) + ")", getAlterModifySQLByTabDefineName(_tabDf.getTableName(), keys[jj], value.split(";")[1], _dbType) });
					}
				}
			}
		}
		return returnList;
	}

	private String sureTypeOrLength(HashMap hm, String key, String value, String type) {
		String returnStr = "";
		if (hm.containsKey(key)) {
			if ("类型不同".equals(type)) {
				if (((String) hm.get(key)).indexOf("类型不同") < 0) {
					returnStr = type + hm.get(key);
				} else {
					returnStr = (String) hm.get(key);
				}
			} else {
				if (((String) hm.get(key)).indexOf("长度不同") < 0) {
					returnStr = type + hm.get(key);
				} else {
					returnStr = (String) hm.get(key);
				}
			}
		} else {
			returnStr = type + ";" + value;
		}
		return returnStr;

	}

	private String getCreateSQL(WLTTableDf _tdf, String _dbtype) {
		if (_dbtype == null) {
			_dbtype = ServerEnvironment.getDefaultDataSourceType();
		}
		HashMap typeMap = getTypeMap(_dbtype);

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append(" create table " + _tdf.getTableName() + " (\r\n");
		String[][] str_cols = _tdf.getColDefines(); //所有列,5列!!
		for (int i = 0; i < str_cols.length; i++) {
			if (((String) typeMap.get("decimal")).equalsIgnoreCase((String) typeMap.get(str_cols[i][2]))) {
				if (_tdf.getPkFieldName() != null && str_cols[i][0].equalsIgnoreCase(_tdf.getPkFieldName()) && !"".equals(_tdf.getPkFieldName().trim())) {
					if ("mysql".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ")  ,\r\n");//, /*" + str_cols[i][1] + "*/\r\n
					} else if ("sqlserver".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ")  ,\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					} else {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ")  ,\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					}
				} else {
					if ("mysql".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ") ,\r\n");//, /*" + str_cols[i][1] + "*/\r\n
					} else if ("sqlserver".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + "),\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					} else {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + "),\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					}
				}

			} else if ("clob".equalsIgnoreCase((String) typeMap.get(str_cols[i][2])) || "text".equalsIgnoreCase((String) typeMap.get(str_cols[i][2]))) {
				sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + ",\r\n");
			} else {
				sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + "),\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
			}
		}
		//可能没有主键
		if (_tdf != null && (_tdf.getPkFieldName() != null && !"".equals(_tdf.getPkFieldName()))) {
			sb_sql.append(" constraint pk_" + _tdf.getTableName() + " primary key (" + _tdf.getPkFieldName() + "),\r\n");
		}
		int a = sb_sql.lastIndexOf(",");
		sb_sql.delete(a, a + 1);
		sb_sql.append(")");
		if ("mysql".equalsIgnoreCase(_dbtype)) {
			sb_sql.append(" DEFAULT CHARSET=GBK");
		}
		sb_sql.append(";\r\n");
		if (_tdf.getAllIndexs() != null && _tdf.getAllIndexs().size() > 0) {
			for (int i = 0; i < _tdf.getAllIndexs().size(); i++) {
				sb_sql.append(" create index " + ((String[]) _tdf.getAllIndexs().get(i))[0] + " on " + _tdf.getTableName() + "(" + ((String[]) _tdf.getAllIndexs().get(i))[1] + ");\r\n");
			}
		}
		return sb_sql.toString(); //
	}

	/**
	 * 获得创建表的sql和关键字说明【李春娟/2013-05-24】
	 * @param _tdf
	 * @param _dbtype
	 * @return
	 */
	private String[] getCreateSQLAndDesc(WLTTableDf _tdf, String _dbtype) {
		if (_dbtype == null) {
			_dbtype = ServerEnvironment.getDefaultDataSourceType();
		}
		HashMap typeMap = getTypeMap(_dbtype);

		StringBuffer sb_keysql = new StringBuffer();
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append(" create table " + _tdf.getTableName() + " (\r\n");
		String[][] str_cols = _tdf.getColDefines(); //所有列,5列!!
		for (int i = 0; i < str_cols.length; i++) {
			if (((String) typeMap.get("decimal")).equalsIgnoreCase((String) typeMap.get(str_cols[i][2]))) {
				if (_tdf.getPkFieldName() != null && str_cols[i][0].equalsIgnoreCase(_tdf.getPkFieldName()) && !"".equals(_tdf.getPkFieldName().trim())) {
					if ("mysql".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ")  not null,\r\n");//, /*" + str_cols[i][1] + "*/\r\n
					} else if ("sqlserver".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ")  not null,\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					} else {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ")  not null,\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					}
				} else {
					if ("mysql".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + ") ,\r\n");//, /*" + str_cols[i][1] + "*/\r\n
					} else if ("sqlserver".equalsIgnoreCase(_dbtype)) {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + "),\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					} else {
						sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + "),\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
					}
				}

			} else if ("clob".equalsIgnoreCase((String) typeMap.get(str_cols[i][2])) || "text".equalsIgnoreCase((String) typeMap.get(str_cols[i][2]))) {
				sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + ",\r\n");
			} else {
				sb_sql.append(" " + str_cols[i][0] + " " + (String) typeMap.get(str_cols[i][2]) + "(" + str_cols[i][3] + "),\r\n");//, /*" + str_cols[i][1] + "*/\r\n	
			}
			//判断是否是关键字
			sb_keysql.append(dbValidate.checkValidateByCol(str_cols[i][0]));
		}
		//可能没有主键
		if (_tdf != null && (_tdf.getPkFieldName() != null && !"".equals(_tdf.getPkFieldName()))) {
			sb_sql.append(" constraint pk_" + _tdf.getTableName() + " primary key (" + _tdf.getPkFieldName() + "),\r\n");
		}
		int a = sb_sql.lastIndexOf(",");
		sb_sql.delete(a, a + 1);
		sb_sql.append(")");
		if ("mysql".equalsIgnoreCase(_dbtype)) {
			sb_sql.append(" DEFAULT CHARSET=GBK");
		}
		sb_sql.append(";\r\n");
		if (_tdf.getAllIndexs() != null && _tdf.getAllIndexs().size() > 0) {
			for (int i = 0; i < _tdf.getAllIndexs().size(); i++) {
				sb_sql.append(" create index " + ((String[]) _tdf.getAllIndexs().get(i))[0] + " on " + _tdf.getTableName() + "(" + ((String[]) _tdf.getAllIndexs().get(i))[1] + ");\r\n");
			}
		}

		return new String[] { sb_sql.toString(), sb_keysql.toString() }; //
	}

	private String getAlterModifySQLByTabDefineName(String tableName, String key, String typeL, String _dbtype) {
		if (_dbtype == null) {
			_dbtype = ServerEnvironment.getDefaultDataSourceType();
		}
		HashMap typeMap = getTypeMap(_dbtype);
		StringBuffer sb_sql = new StringBuffer(); //
		if ("sqlserver".equalsIgnoreCase(_dbtype)) {
			sb_sql.append(" alter table " + tableName + " alter column  " + key + " " + typeL + ";");
		} else if ("db2".equalsIgnoreCase(_dbtype)) {//db2中修改字段类型和长度的语句不同，故增加该判断【李春娟】
			sb_sql.append(" alter table " + tableName + " alter  " + key + " set data type " + typeL + ";");
		} else {
			sb_sql.append(" alter table " + tableName + " modify  " + key + " " + typeL + ";");
		}
		return sb_sql.toString(); //
	}

	public String getAlterSQLByTabDefineName(String _dbtype, String _tabName, String _cName, String _cType, String _cLength) {
		if (_dbtype == null || "null".equals(_dbtype) || "".equals(_dbtype.trim())) {
			_dbtype = ServerEnvironment.getDefaultDataSourceType();
		}
		HashMap typeMap = getTypeMap(_dbtype);
		StringBuffer sb_sql = new StringBuffer(); //
		if ("decimal".equalsIgnoreCase(_cType) || "number".equalsIgnoreCase(_cType)) {
			if ("mysql".equalsIgnoreCase(_dbtype)) {
				sb_sql.append(" alter table " + _tabName + " add  " + _cName + " " + (String) typeMap.get(_cType) + "(" + _cLength + ");");
			} else if ("sqlserver".equalsIgnoreCase(_dbtype)) {
				sb_sql.append(" alter table " + _tabName + " add  " + _cName + " " + (String) typeMap.get(_cType) + "(" + _cLength + ");");
			} else {
				sb_sql.append(" alter table " + _tabName + " add  " + _cName + " " + (String) typeMap.get(_cType) + "(" + _cLength + ");");
			}
		} else if ("clob".equalsIgnoreCase(_cType) || "text".equalsIgnoreCase(_cType)) {
			sb_sql.append(" alter table " + _tabName + " add  " + _cName + " " + (String) typeMap.get(_cType) + ";");
		} else {
			sb_sql.append(" alter table " + _tabName + " add  " + _cName + " " + (String) typeMap.get(_cType) + "(" + _cLength + ");");
		}
		return sb_sql.toString(); //
	}

	public String getAlterDeleteSQLByTabDefineName(String _dbtype, String _tabName, String _cName) {
		if (_dbtype == null || "null".equals(_dbtype) || "".equals(_dbtype.trim())) {
			_dbtype = ServerEnvironment.getDefaultDataSourceType();
		}
		HashMap typeMap = getTypeMap(_dbtype);
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append(" alter table " + _tabName + " drop column " + _cName + ";");
		return sb_sql.toString(); //
	}

	private String getDeleteSQL(WLTTableDf _tdf) {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append(" drop table " + _tdf.getTableName());
		return sb_sql.toString();
	}

	private HashMap getTypeMap(String _dbType) {//转化成一致的避免麻烦
		HashMap typeMap = new HashMap();
		if ("Oracle".equalsIgnoreCase(_dbType)) {
			typeMap.put(WLTTableDf.VARCHAR, "varchar2");
			typeMap.put("varchar2", "varchar2");
			typeMap.put(WLTTableDf.NUMBER, "number");
			typeMap.put("decimal", "number");
			typeMap.put(WLTTableDf.CHAR, "char");
			typeMap.put(WLTTableDf.CLOB, "clob");
		} else if ("Mysql".equalsIgnoreCase(_dbType)) {
			typeMap.put(WLTTableDf.VARCHAR, "varchar");
			typeMap.put(WLTTableDf.NUMBER, "decimal");
			typeMap.put("decimal", "decimal");
			typeMap.put(WLTTableDf.CHAR, "char");
			typeMap.put(WLTTableDf.CLOB, "text");
			typeMap.put("text", "text");
		} else {
			typeMap.put(WLTTableDf.VARCHAR, "varchar");
			typeMap.put(WLTTableDf.NUMBER, "decimal");
			typeMap.put("decimal", "decimal");
			typeMap.put(WLTTableDf.CHAR, "char");
			typeMap.put(WLTTableDf.CLOB, "text");
			typeMap.put("text", "text");
		}
		return typeMap;
	}

	private WLTTableDf getOneXMLTable(String _dbType, String _tabName, String xmlPath) {
		if (_dbType == null || "null".equals(_dbType) || "".equals(_dbType.trim())) {
			_dbType = ServerEnvironment.getDefaultDataSourceType();
		}
		if (xmlPath == null || "".equals(xmlPath)) {
			xmlPath = "/cn/com/infostrategy/bs/sysapp/install/database/tables.xml";
		}
		HashMap typeMap = getTypeMap(_dbType);
		WLTTableDf tdf = null;
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name");
				if (_tabName.equals(str_tname)) {
					String str_tdesc = param.getAttributeValue("descr");
					String str_tpk = param.getAttributeValue("pkname");
					tdf = new WLTTableDf(str_tname, str_tdesc);
					tdf.setPkFieldName(str_tpk);
					if (str_tpk == null || "".equals(str_tpk)) {
						tdf.setPkFieldName(null);
					}
					java.util.List list_cols = ((org.jdom.Element) param.getChildren("columns").get(0)).getChildren("col");
					for (int j = 0; j < list_cols.size(); j++) {
						org.jdom.Element paramc = (org.jdom.Element) list_cols.get(j);
						if (str_tpk != null && str_tpk.equals(paramc.getAttributeValue("name"))) {
							if ("mysql".equalsIgnoreCase(_dbType)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							} else if ("sqlserver".equalsIgnoreCase(_dbType)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							} else {
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							}
						} else {
							if ("mysql".equalsIgnoreCase(_dbType)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							} else if ("sqlserver".equalsIgnoreCase(_dbType)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							} else {
								if ("number".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							}
						}
					}
					java.util.List list_indexs = param.getChildren("indexs");
					if (list_indexs != null && list_indexs.size() > 0) {
						java.util.List list_indexss = ((org.jdom.Element) list_indexs.get(0)).getChildren("index");
						if (list_indexss != null && list_indexss.size() > 0) {
							List indexall = new ArrayList();
							for (int jjj = 0; jjj < list_indexss.size(); jjj++) {
								org.jdom.Element index = (org.jdom.Element) list_indexss.get(jjj);
								indexall.add(new String[] { index.getAttributeValue("name"), index.getAttributeValue("cols") });
							}
							if (indexall != null && indexall.size() > 0) {
								tdf.setAllIndexs((ArrayList) indexall);
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return tdf;

	}

	private WLTTableDf[] getAllDBTable(String datasourcename, String xmlPath) {
		HashMap typeMap = getTypeMap(getDBType(datasourcename));
		ArrayList list = new ArrayList(); //
		if (xmlPath == null || "".equals(xmlPath)) {
			xmlPath = "/cn/com/infostrategy/bs/sysapp/install/database/tables.xml";
		}
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name");
				TableDataStruct dsTabTD = null;
				try {
					dsTabTD = new CommDMO().getTableDataStructByDS(datasourcename, "select * from " + str_tname + " where 1=2");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				//		
				String[] headerName = dsTabTD.getHeaderName();
				String[] headerTypeName = dsTabTD.getHeaderTypeName();
				int[] headerLength = dsTabTD.getHeaderLength();
				int[] headerType = dsTabTD.getHeaderType();
				int[] headerp = dsTabTD.getPrecision();
				int[] headerc = dsTabTD.getScale();
				String[] isNullAble = dsTabTD.getIsNullAble(); //字段是否为空
				WLTTableDf tdf = null;
				tdf = new WLTTableDf(str_tname, "");
				String str_tpk = "id";
				tdf.setPkFieldName(null);
				for (int j = 0; j < headerName.length; j++) {
					if (str_tpk != null && str_tpk.equals(headerName[j])) {
						if ("mysql".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j])))
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "N" });
							else
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "N" });
						} else if ("sqlserver".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j])))
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "N" });
							else
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "N" });
						} else {
							tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "N" });
						}
					} else {
						if ("mysql".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j])))
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "Y" });
							else
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "Y" });
						} else if ("sqlserver".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j])))
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "Y" });
							else
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "Y" });
						} else {
							tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "Y" });
						}
					}
				}
				list.add(tdf);
			}
			if (list != null && list.size() > 0) {
				return (WLTTableDf[]) list.toArray(new WLTTableDf[0]); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return null;
	}

	private WLTTableDf getAllDBTableByName(String datasourcename, String tablename) {
		HashMap typeMap = getTypeMap(getDBType(datasourcename));
		WLTTableDf tdf = null;
		ArrayList list = new ArrayList(); //
		try {
			java.util.List list_tables = Arrays.asList(new CommDMO().getAllSysTables(null, "%"));
			for (int i = 0; i < list_tables.size(); i++) {
				if (((String) list_tables.get(i)).indexOf("$") > 0 || !((String) list_tables.get(i)).equalsIgnoreCase(tablename)) {
					continue;
				}
				TableDataStruct dsTabTD = null;
				try {
					dsTabTD = new CommDMO().getTableDataStructByDS(datasourcename, "select * from " + (String) list_tables.get(i) + " where 1=2");
				} catch (Exception e) {
					e.printStackTrace();
				}
				//		
				if (dsTabTD != null) {
					String[] headerName = dsTabTD.getHeaderName();
					String[] headerTypeName = dsTabTD.getHeaderTypeName();
					int[] headerLength = dsTabTD.getHeaderLength();
					int[] headerType = dsTabTD.getHeaderType();
					int[] headerp = dsTabTD.getPrecision();
					int[] headerc = dsTabTD.getScale();
					String[] isNullAble = dsTabTD.getIsNullAble(); //字段是否为空
					String str_tname = (String) list_tables.get(i);
					tdf = new WLTTableDf(str_tname, "");
					String str_tpk = "id";
					tdf.setPkFieldName(null);
					for (int j = 0; j < headerName.length; j++) {
						if (str_tpk != null && str_tpk.equals(headerName[j])) {
							if ("mysql".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j].toLowerCase())))
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "N" });
								else
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "N" });
							} else if ("sqlserver".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j].toLowerCase())))
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "N" });
								else
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "N" });
							} else {
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "N" });
							}
						} else {
							if ("mysql".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j].toLowerCase())))
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "Y" });
								else
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "Y" });
							} else if ("sqlserver".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(headerTypeName[j].toLowerCase())))
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), headerp[j] + "," + headerc[j], "Y" });
								else
									tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "Y" });
							} else {
								tdf.addCol(new String[] { headerName[j], "", (String) typeMap.get(headerTypeName[j].toLowerCase()), String.valueOf(headerp[j]), "Y" });
							}
						}
					}
				}

			}
			if (tdf != null) {
				return tdf; //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return null;
	}

	private String getDBType(String _dsname) {
		DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_dsname); //
		return dsVO.getDbtype(); //
	}

	private WLTTableDf[] getAllXMLTable(String dbtype, String xmlPath) {
		if (dbtype == null || dbtype.trim().equals("")) {
			dbtype = ServerEnvironment.getDefaultDataSourceType();
		}
		if (xmlPath == null || "".equals(xmlPath)) {
			xmlPath = "/cn/com/infostrategy/bs/sysapp/install/database/tables.xml";
		}
		HashMap typeMap = getTypeMap(dbtype);
		ArrayList list = new ArrayList(); //
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			for (int i = 0; i < list_tables.size(); i++) {
				WLTTableDf tdf = null;
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name");
				String str_tdesc = param.getAttributeValue("descr");
				String str_tpk = param.getAttributeValue("pkname");
				tdf = new WLTTableDf(str_tname, str_tdesc);
				tdf.setPkFieldName(str_tpk);
				if (str_tpk == null || "".equals(str_tpk)) {
					tdf.setPkFieldName(null);
				}
				java.util.List list_cols = ((org.jdom.Element) param.getChildren("columns").get(0)).getChildren("col");
				for (int j = 0; j < list_cols.size(); j++) {
					org.jdom.Element paramc = (org.jdom.Element) list_cols.get(j);
					if (str_tpk != null && str_tpk.equals(paramc.getAttributeValue("name"))) {
						if ("mysql".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							else
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
						} else if ("sqlserver".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							else
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
						} else {
							tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
						}
					} else {
						if ("mysql".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							else
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
						} else if ("sqlserver".equalsIgnoreCase(ServerEnvironment.getDefaultDataSourceType())) {
							if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							else
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
						} else {
							if ("number".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							else
								tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
						}
					}
				}
				java.util.List list_indexs = param.getChildren("indexs");
				if (list_indexs != null && list_indexs.size() > 0) {
					java.util.List list_indexss = ((org.jdom.Element) list_indexs.get(0)).getChildren("index");
					if (list_indexss != null && list_indexss.size() > 0) {
						List indexall = new ArrayList();
						for (int jjj = 0; jjj < list_indexss.size(); jjj++) {
							org.jdom.Element index = (org.jdom.Element) list_indexss.get(jjj);
							indexall.add(new String[] { index.getAttributeValue("name"), index.getAttributeValue("cols") });
						}
						if (indexall != null && indexall.size() > 0) {
							tdf.setAllIndexs((ArrayList) indexall);
						}
					}
				}

				list.add(tdf);
			}
			if (list != null && list.size() > 0) {
				return (WLTTableDf[]) list.toArray(new WLTTableDf[0]); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
		}
		return null;
	}

	private WLTTableDf getXMLTableByTN(String datasourcename, String tablename, String xmlPath) {
		if (datasourcename == null || datasourcename.trim().equals("")) {
			datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		if (xmlPath == null || "".equals(xmlPath)) {
			xmlPath = "/cn/com/infostrategy/bs/sysapp/install/database/tables.xml";
		}
		String dbtype = ServerEnvironment.getInstance().getDataSourceVO(datasourcename).getDbtype();
		HashMap typeMap = getTypeMap(dbtype);
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			WLTTableDf tdf = null;
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name");
				String str_tdesc = param.getAttributeValue("descr");
				String str_tpk = param.getAttributeValue("pkname");
				if (tablename != null && tablename.equalsIgnoreCase(str_tname)) {
					tdf = new WLTTableDf(str_tname, str_tdesc);
					tdf.setPkFieldName(str_tpk);
					if (str_tpk == null || "".equals(str_tpk)) {
						tdf.setPkFieldName(null);
					}
					java.util.List list_cols = ((org.jdom.Element) param.getChildren("columns").get(0)).getChildren("col");
					for (int j = 0; j < list_cols.size(); j++) {
						org.jdom.Element paramc = (org.jdom.Element) list_cols.get(j);
						if (str_tpk != null && str_tpk.equals(paramc.getAttributeValue("name"))) {
							if ("mysql".equalsIgnoreCase(dbtype)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							} else if ("sqlserver".equalsIgnoreCase(dbtype)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							} else {
								if ("number".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "N" });
							}
						} else {
							if ("mysql".equalsIgnoreCase(dbtype)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							} else if ("sqlserver".equalsIgnoreCase(dbtype)) {
								if ("decimal".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							} else {
								if ("number".equalsIgnoreCase((String) typeMap.get(paramc.getAttributeValue("type"))))
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
								else
									tdf.addCol(new String[] { paramc.getAttributeValue("name"), paramc.getAttributeValue("descr"), (String) typeMap.get(paramc.getAttributeValue("type")), paramc.getAttributeValue("length"), "Y" });
							}
						}
					}
					java.util.List list_indexs = param.getChildren("indexs");
					if (list_indexs != null && list_indexs.size() > 0) {
						java.util.List list_indexss = ((org.jdom.Element) list_indexs.get(0)).getChildren("index");
						if (list_indexss != null && list_indexss.size() > 0) {
							List indexall = new ArrayList();
							for (int jjj = 0; jjj < list_indexss.size(); jjj++) {
								org.jdom.Element index = (org.jdom.Element) list_indexss.get(jjj);
								indexall.add(new String[] { index.getAttributeValue("name"), index.getAttributeValue("cols") });
							}
							if (indexall != null && indexall.size() > 0) {
								tdf.setAllIndexs((ArrayList) indexall);
							}
						}
					}
					return tdf; //
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
		return null;
	}

	public String[] getAllInstallSQLTexts(String _dbtype, String xmlPath) {
		WLTTableDf[] WLTTableDf = getAllXMLTable(_dbtype, xmlPath);
		String[] installSqls = new String[WLTTableDf.length];
		for (int i = 0; i < WLTTableDf.length; i++) {
			installSqls[i] = getCreateSQL(WLTTableDf[i], _dbtype);
		}
		return installSqls;
	}

	public String exportCreateTableSqlAsHtml(String _dbtype, String _tabName, String xmlPath) {
		StringBuffer sqlsAsHtml = new StringBuffer();
		sqlsAsHtml.append(" ");
		sqlsAsHtml.append(" <html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		sqlsAsHtml.append(" <style>\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		sqlsAsHtml.append(" </head>\r\n<body>\r\n");
		if (_tabName == null || "".equals(_tabName)) { //导出全部表
			String[] installSqls = null;
			try {
				installSqls = getAllCreateSQL(_dbtype, xmlPath);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (installSqls != null) {
				StringBuffer content = new StringBuffer();
				for (int i = 0; i < installSqls.length; i++) {
					content.append(installSqls[i] + "<br><br>");
				}
				sqlsAsHtml.append(new TBUtil().replaceAll(content.toString(), "\r\n", "\r\n<br>"));
			}
		} else {
			try {
				sqlsAsHtml.append(new TBUtil().replaceAll(getCreateSQL(_dbtype, _tabName, xmlPath), "\r\n", "\r\n<br>"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		sqlsAsHtml.append(" </body></html>");
		return sqlsAsHtml.toString();
	}

	public String getCreateSQL(String _dbtype, String _tabName, String xmlPath) throws Exception {
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
			java.util.List list_tables = doc.getRootElement().getChildren("table");
			String str_tpk = null;
			for (int i = 0; i < list_tables.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name"); //表名
				if (_tabName.equalsIgnoreCase(str_tname)) { //如果找到了!!
					HashMap typeMap = getTypeMap(_dbtype);
					java.util.List list_cols = ((org.jdom.Element) param.getChildren("columns").get(0)).getChildren("col"); //取得所有列!!
					StringBuilder sb_sql = new StringBuilder(); //
					sb_sql.append("create table " + _tabName + "(\r\n"); //
					str_tpk = param.getAttributeValue("pkname");
					for (int j = 0; j < list_cols.size(); j++) {
						org.jdom.Element colNode = (org.jdom.Element) list_cols.get(j); //
						String str_colName = colNode.getAttributeValue("name"); //
						String str_colType = colNode.getAttributeValue("type"); //
						String str_colLength = colNode.getAttributeValue("length"); //
						sb_sql.append(str_colName + " " + (String) typeMap.get(colNode.getAttributeValue("type")) + "(" + str_colLength + ")"); //
						if (str_colName.equalsIgnoreCase(str_tpk)) { //如果是主键,则必须是not null
							sb_sql.append(" NOT NULL"); ////
						}
						if (j != list_cols.size() - 1) {
							sb_sql.append(",\r\n"); ////
						}
					}

					if (str_tpk != null && !"".equals(str_tpk)) {
						sb_sql.append(",\r\n constraint pk_" + str_tname + " primary key (" + str_tpk + ")\r\n");
					}
					sb_sql.append(")"); //
					if (_dbtype.equalsIgnoreCase("MYSQL")) {
						sb_sql.append(" DEFAULT CHARSET = GBK"); //
					}
					sb_sql.append(";\r\n");
					java.util.List list_indexs = param.getChildren("indexs");
					if (list_indexs != null && list_indexs.size() > 0) {
						java.util.List list_indexss = ((org.jdom.Element) list_indexs.get(0)).getChildren("index");
						if (list_indexss != null && list_indexss.size() > 0) {
							List indexall = new ArrayList();
							for (int jjj = 0; jjj < list_indexss.size(); jjj++) {
								org.jdom.Element index = (org.jdom.Element) list_indexss.get(jjj);
								sb_sql.append(" create index " + index.getAttributeValue("name") + " on " + _tabName + "(" + index.getAttributeValue("cols") + ");\r\n");
							}
						}
					}
					return sb_sql.toString(); //
				}
			}
			return ""; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return ""; //
		}
	}

	public String[] getAllCreateSQL(String _dbtype, String xmlPath) throws Exception {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
		java.util.List list_tables = doc.getRootElement().getChildren("table");
		List rl = new ArrayList();
		String str_tpk = null;
		for (int i = 0; i < list_tables.size(); i++) {
			try {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name"); //表名
				HashMap typeMap = getTypeMap(_dbtype);
				java.util.List list_cols = ((org.jdom.Element) param.getChildren("columns").get(0)).getChildren("col"); //取得所有列!!
				StringBuilder sb_sql = new StringBuilder(); //
				sb_sql.append("create table " + str_tname + "(\r\n"); //
				for (int j = 0; j < list_cols.size(); j++) {
					org.jdom.Element colNode = (org.jdom.Element) list_cols.get(j); //
					String str_colName = colNode.getAttributeValue("name"); //
					String str_colType = colNode.getAttributeValue("type"); //
					String str_colLength = colNode.getAttributeValue("length"); //
					sb_sql.append(str_colName + " " + (String) typeMap.get(colNode.getAttributeValue("type")) + "(" + str_colLength + ")"); //
					if (str_colName.equalsIgnoreCase(str_tpk)) { //如果是主键,则必须是not null
						sb_sql.append(" NOT NULL"); ////
					}
					if (j != list_cols.size() - 1) {
						sb_sql.append(",\r\n"); ////
					}

				}
				str_tpk = param.getAttributeValue("pkname");
				if (str_tpk != null && !"".equals(str_tpk)) {
					sb_sql.append(",\r\n constraint pk_" + str_tname + " primary key (" + str_tpk + ")\r\n");
				}
				sb_sql.append(")"); //
				if (_dbtype.equalsIgnoreCase("MYSQL")) {
					sb_sql.append(" DEFAULT CHARSET = GBK"); //
				}
				sb_sql.append(";\r\n");
				java.util.List list_indexs = param.getChildren("indexs");
				if (list_indexs != null && list_indexs.size() > 0) {
					java.util.List list_indexss = ((org.jdom.Element) list_indexs.get(0)).getChildren("index");
					if (list_indexss != null && list_indexss.size() > 0) {
						List indexall = new ArrayList();
						for (int jjj = 0; jjj < list_indexss.size(); jjj++) {
							org.jdom.Element index = (org.jdom.Element) list_indexss.get(jjj);
							sb_sql.append(" create index " + index.getAttributeValue("name") + " on " + str_tname + "(" + index.getAttributeValue("cols") + ");\r\n");
						}
					}
				}
				rl.add(sb_sql.toString());
			} catch (Exception ex) {
				ex.printStackTrace(); //
				continue;
			}
		}
		if (rl != null && rl.size() > 0) {
			return (String[]) rl.toArray(new String[0]);
		}
		return null;
	}

	public String exportCompareTableSqlAsHtml(String datasourcename, String _tabName, String xmlPath) throws Exception {

		StringBuffer str_table = new StringBuffer();
		StringBuffer str_column = new StringBuffer();
		str_table.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		str_table.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		str_table.append("<script type=\"text/javascript\">function myconfirm(sql,datesourcename){" + "if(confirm(\"您确定执行吗\")){" + "window.open('./state?type=comitsql&sql='+sql+'&datesourcename='+datesourcename,'','','');" + "}" + "}</script>");
		str_table.append("</head>\r\n<body>\r\n");
		str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>" + "平台表比较" + "</a></td></tr>");
		String str_tname = _tabName;

		str_column.append("表名：<a name='" + str_tname + "' >" + str_tname + "</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
		str_column.append("&nbsp;&nbsp;&nbsp;&nbsp;\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
		String[][] allsqls = compareTableDictByDB(datasourcename, str_tname, xmlPath);
		if (allsqls != null && allsqls.length > 0) {
			for (int j = 0; j < allsqls.length; j++) {
				if ("创建表".equals(allsqls[j][1])) {
					str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>"));
					if (allsqls[j][4] != null && !allsqls[j][4].trim().equals("")) {//是否有关键字提示
						str_column.append("<font  color=\"red\">" + allsqls[j][4] + "请修改</font></td></tr>\r\n");
					} else {
						str_column.append("</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename + "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					}
				} else if ("创建列".equals(allsqls[j][1])) {
					str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				} else if ("修改列".equals(allsqls[j][1])) {
					str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=blue\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				} else {
					str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=red\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				}
				//str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td>" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</td></tr>\r\n");
			}
		}
		str_column.append("</table><br>\r\n");
		str_table.append("</tr></table><br><hr>\r\n");
		str_column.append("</body></html>");
		str_table.append(str_column);
		return str_table.toString();

	}

	/**
	 * 将数据字典导出成Html表格形式 李春娟实现
	 * @return
	 */
	public String exportDictAsHtml(String _title, String _filename) throws Exception {
		StringBuffer str_table = new StringBuffer();
		StringBuffer str_column = new StringBuffer();
		String type = "平台表";
		if ("项目数据字典".equals(_title)) {
			type = "项目表";
		}
		if (_filename == null || "".equals(_filename)) {
			_filename = "/cn/com/infostrategy/bs/sysapp/install/database/tables.xml";
		}
		DataSourceVO[] alldatasources = ServerEnvironment.getInstance().getDataSourceVOs();
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		URL url = this.getClass().getResource(_filename);
		if (url == null) {
			throw new WLTAppException("资源文件[" + _filename + "]不存在,请与产品开发组联系!"); //
		}
		Document doc = builder.build(this.getClass().getResourceAsStream(_filename)); // 加载XML"/cn/com/infostrategy/bs/sysapp/install/database/tables.xml"
		java.util.List list_tables = doc.getRootElement().getChildren("table");
		java.util.List list_columns = null;
		java.util.List list_indexs = null;
		StringBuffer sbdatasource = new StringBuffer();
		if (alldatasources != null) {
			for (int i = 0; i < alldatasources.length; i++) {
				sbdatasource.append("<option value=\"" + alldatasources[i].getName() + "\">" + alldatasources[i].getName() + "</option>");
			}
		}

		str_table.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		str_table.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		str_table.append("</head>\r\n<body>\r\n");
		str_table.append(" <form action=\"./state\" method=\"post\" target=\"_blank\">" + "<input id=\"type\" name=\"type\" type=\"hidden\" value=\"createtable\" ><input id=\"xmlpath\" name=\"xmlPath\" type=\"hidden\" value=\"" + _filename + "\" >");
		str_table.append("1.将" + type + "XML定义导出SQL：<select id=\"dbtype\" name=\"dbtype\"><option value=\"ORACLE\">ORACLE</option><option value=\"MYSQL\">MYSQL</option><option value=\"SQLSERVER\">SQLSERVER</option><option value=\"DB2\">DB2</option></select><input type=\"submit\" value =\"执行\">");
		str_table.append("</form>");
		str_table.append(" <form action=\"./state\" method=\"post\" target=\"_blank\">" + "<input id=\"type\" name=\"type\" type=\"hidden\" value=\"comparesfj\" ><input id=\"xmlpath\" name=\"xmlPath\" type=\"hidden\" value=\"" + _filename + "\" >");
		str_table.append("2.将" + type + "XML定义与数据源实际结构比较 比较的数据源：<select id=\"datasourcename\" name=\"datasourcename\">");
		str_table.append(sbdatasource);
		//为了兼容旧机制，一般情况只执行新增和修改不执行删除，所以常用按操作类型排列，故设为默认条件【李春娟/2012-03-12】
		str_table.append("</select>排列方式<select id=\"howtosort\" name=\"howtosort\"><option value=\"2\">按操作类型排列</option><option value=\"1\">按表分类</option></select><input type=\"submit\" value =\"执行\">");
		str_table.append("</form>");
		/*str_table.append("<a href ='./state?type=createtable&dbtype=mysql&tabname=' target='about:blank'><font  color=\"blue\">Mysql</font></a>，");
		str_table.append("<a href ='./state?type=createtable&dbtype=sqlserver&tabname=' target='about:blank'><font  color=\"blue\">SQL Server</font></a>，<br>");*/
		str_table.append(" <form action=\"./state\" method=\"post\" target=\"_blank\">" + "<input id=\"type\" name=\"type\" type=\"hidden\" value=\"getallcreatesql\" ><input id=\"xmlpath\" name=\"xmlPath\" type=\"hidden\" value=\"" + _filename + "\" >");
		str_table.append("3.将数据源中的实际结构导出SQL 数据源<select id=\"datasourcename\" name=\"datasourcename\">");
		str_table.append(sbdatasource);
		str_table.append("</select>目标类型<select id=\"dbtype\" name=\"dbtype\"><option value=\"ORACLE\">ORACLE</option><option value=\"MYSQL\">MYSQL</option><option value=\"SQLSERVER\">SQLSERVER</option></select><input type=\"submit\" value =\"执行\">");
		str_table.append("</form>");
		//"  <a href ='./state?type=getallcreatesql' target='_blank'><font  color=\"blue\">导出所有平台表create语句</font></a>(");
		/*str_table.append("<a href ='./state?type=getallcreatesql&type=oracle' target='_blank'><font  color=\"blue\">ORACLE</font></a>，");
		str_table.append("<a href ='./state?type=getallcreatesql&type=mysql' target='_blank'><font  color=\"blue\">MYSQL</font></a>，");
		str_table.append("<a href ='./state?type=getallcreatesql&type=sqlserver' target='_blank'><font  color=\"blue\">SQLSERVER</font></a>)，<br>");*/
		str_table.append(" <form action=\"./state\" method=\"post\" target=\"_blank\">" + "<input id=\"type\" name=\"type\" type=\"hidden\" value=\"dbcomparesfj\" ><input id=\"xmlpath\" name=\"xmlPath\" type=\"hidden\" value=\"" + _filename + "\" >");
		str_table.append("4.直接比较两个数据源(以前者为标准),数据源1<select id=\"datasourcename1\" name=\"datasourcename1\">");
		str_table.append(sbdatasource);
		str_table.append("</select> 数据源2<select id=\"datasourcename2\" name=\"datasourcename2\">");
		str_table.append(sbdatasource);
		//为了兼容旧机制，一般情况只执行新增和修改不执行删除，所以常用按操作类型排列，故设为默认条件【李春娟/2012-03-12】
		str_table.append("</select> 排列方式<select id=\"howtosort\" name=\"howtosort\"><option value=\"2\">按操作类型排列</option><option value=\"1\">按表分类</option></select><input type=\"submit\" value =\"执行\"></form>");
		str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>" + _title + "</a></td></tr>");
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
			String str_tname = param.getAttributeValue("name");
			String str_tdesc = param.getAttributeValue("descr");
			if (i == 0) {
				str_table.append("<tr><td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			} else if (i % 5 == 0) {
				str_table.append("</tr>\r\n<tr><td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			} else {
				str_table.append("<td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			}

			list_columns = param.getChild("columns").getChildren();

			str_column.append("表名：<a name='" + str_tname + "' >" + str_tname + "</a>&nbsp;&nbsp;&nbsp;&nbsp;说明：" + str_tdesc + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
			str_column.append(" <form action=\"./state\" method=\"post\" target=\"_blank\">" + "<input id=\"type\" name=\"type\" type=\"hidden\" value=\"createtable\" >" + "<input id=\"tabname\" name=\"tabname\" type=\"hidden\" value=\"" + str_tname + "\" ><input id=\"xmlpath\" name=\"xmlPath\" type=\"hidden\" value=\"" + _filename + "\" >");
			str_column.append("1.将平台表XML定义导出SQL：<select id=\"dbtype\" name=\"dbtype\"><option value=\"ORACLE\">ORACLE</option><option value=\"MYSQL\">MYSQL</option><option value=\"SQLSERVER\">SQLSERVER</option><option value=\"DB2\">DB2</option></select><input type=\"submit\" value =\"执行\">");
			str_column.append("</form>");
			str_column.append(" <form action=\"./state\" method=\"post\" target=\"_blank\">" + "<input id=\"type\" name=\"type\" type=\"hidden\" value=\"compareonetable\" >" + "<input id=\"tabname\" name=\"tabname\" type=\"hidden\" value=\"" + str_tname + "\" ><input id=\"xmlpath\" name=\"xmlPath\" type=\"hidden\" value=\"" + _filename + "\" >");
			str_column.append("2.将平台表XML定义与数据源实际结构比较 比较的数据源：<select id=\"datasourcename\" name=\"datasourcename\">");
			str_column.append(sbdatasource);
			str_column.append("</select><input type=\"submit\" value =\"执行\">");
			str_column.append("</form>");
			//str_column.append("创建表,数据库类型：<a href ='./state?type=createtable&dbtype=oracle&tabname=" + str_tname + "' target='about:blank'><font  color=\"blue\">Oracle</font></a>，");
			//str_column.append("<a href ='./state?type=createtable&dbtype=mysql&tabname=" + str_tname + "' target='about:blank'><font  color=\"blue\">Mysql</font></a>，");
			//str_column.append("<a href ='./state?type=createtable&dbtype=sqlserver&tabname=" + str_tname + "' target='about:blank'><font  color=\"blue\">SQL Server</font></a>，");
			//str_column.append("<a href ='./state?type=compareonetable&tabname=" + str_tname + "' target='_blank'><font  color=\"blue\">平台表比较</font></a>\r\n");
			//袁江晓  20130226修改  添加  外键描述  更直观    begin
			str_column.append("<a href ='#title'><font  color=\"blue\">页首1</font></a>\r\n<br><br>");
			boolean EsistFK = false;
			for (int j = 0; j < list_columns.size(); j++) {
				org.jdom.Element param1 = (org.jdom.Element) list_columns.get(j);
				if (null != param1.getAttributeValue("reffield")) {
					EsistFK = true;
					break;
				}
			}
			if (EsistFK) {//如果有外键
				str_column.append("<table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>name</td><td>type</td><td>length</td><td>descr</td><td>reftable</td><td>refcol</td><td>ismulti</td>\r\n</tr>\r\n");
			} else {
				str_column.append("<table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>name</td><td>type</td><td>length</td><td>descr</td>\r\n</tr>\r\n");
			}
			for (int j = 0; j < list_columns.size(); j++) {
				org.jdom.Element param1 = (org.jdom.Element) list_columns.get(j);
				if (EsistFK) { //如果有外键
					String reffieldVal = param1.getAttributeValue("reffield");//xml中的值为pub_user.id
					String reftable = "";
					String refcol = "";
					String ismulti = "";
					if (reffieldVal != null) {//如果其中一列有外键则显示如下
						String[] strs_temp = reffieldVal.split("\\.");//pub_user.id  一定要用\\.否则分割不了
						if (strs_temp.length == 2) {
							reftable = strs_temp[0];//外键表名
							refcol = strs_temp[1];//外键列名
						}
						ismulti = param1.getAttributeValue("ismulti") == null ? "" : param1.getAttributeValue("ismulti");
						str_column.append("<tr><td>" + param1.getAttributeValue("name") + "</td><td>" + param1.getAttributeValue("type") + "</td><td>" + param1.getAttributeValue("length") + "</td><td>" + param1.getAttributeValue("descr") + "</td><td>" + reftable + "</td><td>" + refcol + "</td><td>" + ismulti + "</td></tr>\r\n");
					} else {//如果没有外键的列则显示为空
						str_column.append("<tr><td>" + param1.getAttributeValue("name") + "</td><td>" + param1.getAttributeValue("type") + "</td><td>" + param1.getAttributeValue("length") + "</td><td>" + param1.getAttributeValue("descr") + "</td><td>" + reftable + "</td><td>" + refcol + "</td><td>" + ismulti + "</td></tr>\r\n");
					}
				} else {
					str_column.append("<tr><td>" + param1.getAttributeValue("name") + "</td><td>" + param1.getAttributeValue("type") + "</td><td>" + param1.getAttributeValue("length") + "</td><td>" + param1.getAttributeValue("descr") + "</td></tr>\r\n");
				}
			}
			str_column.append("</table><br>\r\n");
			//袁江晓  20130226修改  添加  外键描述  更直观    end
			if (param.getChild("indexs") != null) {
				list_indexs = param.getChild("indexs").getChildren();
				str_column.append("索引：<table border='1'cellpadding='5'  bordercolor='#888888'><tr bgcolor='#eeeeee'><td>name</td><td>cols</td></tr>\r\n");
				for (int z = 0; z < list_indexs.size(); z++) {
					org.jdom.Element param2 = (org.jdom.Element) list_indexs.get(z);
					str_column.append("<tr><td>" + param2.getAttributeValue("name") + "</td><td>" + param2.getAttributeValue("cols") + "</td></tr>\r\n");
				}
				str_column.append("</table><hr>\r\n");
			}

		}

		str_table.append("</tr></table><br><hr>\r\n");
		str_column.append("</body></html>");
		str_table.append(str_column);
		return str_table.toString();
	}

	public String exportCompareSortByTypeAsHtml(String datasourcename, String _title, String xmlPath) throws Exception {
		StringBuffer str_table = new StringBuffer();
		StringBuffer str_column = new StringBuffer();
		StringBuffer str_column2 = new StringBuffer();
		StringBuffer str_column3 = new StringBuffer();
		StringBuffer str_column4 = new StringBuffer();
		str_table.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		str_table.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		str_table.append("<script type=\"text/javascript\">function myconfirm(sql,datesourcename){" + "if(confirm(\"您确定执行吗\")){" + "window.open('./state?type=comitsql&sql='+sql+'&datesourcename='+datesourcename,'','','');" + "}" + "}</script>");
		str_table.append("</head>\r\n<body>\r\n");
		str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>平台表XML定义与数据源" + datasourcename + "实际结构比较结果</a></td></tr>");

		String[][] allsqls = compareDictByDB(datasourcename, xmlPath);
		if (allsqls != null && allsqls.length > 0) {
			str_column.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>创建表:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			str_column2.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>创建列:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			str_column3.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>修改列:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			str_column4.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>删除列:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			for (int j = 0; j < allsqls.length; j++) {
				if ("创建表".equals(allsqls[j][1])) {
					str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>"));
					if (allsqls[j][4] != null && !allsqls[j][4].trim().equals("")) {//是否有关键字提示
						str_column.append("<font  color=\"red\">" + allsqls[j][4] + "请修改</font></td></tr>\r\n");
					} else {
						str_column.append("</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename + "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					}
				} else if ("创建列".equals(allsqls[j][1])) {
					str_column2.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				} else if ("修改列".equals(allsqls[j][1])) {
					str_column3.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=blue\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				} else {
					str_column4.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=red\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				}
			}
		}
		str_column.append("</table><br>\r\n");
		str_column2.append("</table><br>\r\n");
		str_column3.append("</table><br>\r\n");
		str_column4.append("</table><br>\r\n");
		str_table.append("</tr></table><br><hr>\r\n");
		str_table.append(str_column);
		str_table.append(str_column2);
		str_table.append(str_column3);
		str_table.append(str_column4);
		str_table.append("</body></html>");
		return str_table.toString();
	}

	public String exportDBCompareSortByTypeAsHtml(String _title, String datasourcename, String datasourcename2, String xmlPath) throws Exception {
		WLTDBConnection conn = getConn(datasourcename); // 取得数据库连接!!	
		WLTDBConnection conn2 = getConn(datasourcename2); // 取得数据库连接!!	
		StringBuffer str_table = new StringBuffer();
		StringBuffer str_column = new StringBuffer();
		StringBuffer str_column2 = new StringBuffer();
		StringBuffer str_column3 = new StringBuffer();
		StringBuffer str_column4 = new StringBuffer();
		str_table.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		str_table.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		str_table.append("<script type=\"text/javascript\">function myconfirm(sql,datesourcename){" + "if(confirm(\"您确定执行吗\")){" + "window.open('./state?type=comitsql&sql='+sql+'&datesourcename='+datesourcename,'','','');" + "}" + "}</script>");
		str_table.append("</head>\r\n<body>\r\n");
		str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>" + "数据源" + datasourcename + "比较" + datasourcename2 + "以后者为标准</a></td></tr>");

		String[][] allsqls = compareDictByDBS(datasourcename, datasourcename2, xmlPath);
		if (allsqls != null && allsqls.length > 0) {
			str_column.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>创建表:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			str_column2.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>创建列:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			str_column3.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>修改列:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			str_column4.append("&nbsp;&nbsp;&nbsp;&nbsp;<strong>删除列:</strong>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
			for (int j = 0; j < allsqls.length; j++) {
				if ("创建表".equals(allsqls[j][1])) {
					str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>"));
					if (allsqls[j][4] != null && !allsqls[j][4].trim().equals("")) {//是否有关键字提示
						str_column.append("<font  color=\"red\">" + allsqls[j][4] + "请修改</font></td></tr>\r\n");
					} else {
						str_column.append("</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename + "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					}
				} else if ("创建列".equals(allsqls[j][1])) {
					str_column2.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				} else if ("修改列".equals(allsqls[j][1])) {
					str_column3.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=blue\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				} else {
					str_column4.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=red\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
							+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
				}
			}
		}
		str_column.append("</table><br>\r\n");
		str_column2.append("</table><br>\r\n");
		str_column3.append("</table><br>\r\n");
		str_column4.append("</table><br>\r\n");
		str_table.append("</tr></table><br><hr>\r\n");
		str_table.append(str_column);
		str_table.append(str_column2);
		str_table.append(str_column3);
		str_table.append(str_column4);
		str_table.append("</body></html>");
		return str_table.toString();
	}

	public String exportCompareAsHtml(String datasourcename, String _title, String _filename) throws Exception {
		StringBuffer str_table = new StringBuffer();
		StringBuffer str_column = new StringBuffer();
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		URL url = this.getClass().getResource(_filename);
		if (url == null) {
			throw new WLTAppException("资源文件[" + _filename + "]不存在,请与产品开发组联系!"); //
		}
		WLTDBConnection conn = getConn(datasourcename); // 取得数据库连接!!		
		Document doc = builder.build(this.getClass().getResourceAsStream(_filename)); // 加载XML"/cn/com/infostrategy/bs/sysapp/install/database/tables.xml"
		java.util.List list_tables = doc.getRootElement().getChildren("table");
		str_table.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		str_table.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		str_table.append("<script type=\"text/javascript\">function myconfirm(sql,datesourcename){" + "if(confirm(\"您确定执行吗\")){" + "window.open('./state?type=comitsql&sql='+sql+'&datesourcename='+datesourcename);" + "}" + "}</script>");
		str_table.append("</head>\r\n<body>\r\n");
		//str_table.append("<a href ='./state?type=sortedcomparesfj' target='_blank'><font  color=\"blue\">平台表XML定义与数据源"+datasourcename+"比较结果</font></a><br><br>");
		str_table.append("<a ><font  color=\"blue\">平台表XML定义与数据源" + datasourcename + "比较结果</font></a><br><br>");
		str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>" + _title + "</a></td></tr>");
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
			String str_tname = param.getAttributeValue("name");
			String str_tdesc = param.getAttributeValue("descr");
			if (i == 0) {
				str_table.append("<tr><td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			} else if (i % 5 == 0) {
				str_table.append("</tr>\r\n<tr><td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			} else {
				str_table.append("<td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			}

			String[][] allsqls = compareTableDictByDB(datasourcename, str_tname, _filename);
			if (allsqls != null && allsqls.length > 0) {
				str_column.append("表名：<a name='" + str_tname + "' >" + str_tname + "</a>&nbsp;&nbsp;&nbsp;&nbsp;说明：" + str_tdesc + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
				str_column.append("&nbsp;&nbsp;&nbsp;&nbsp;<a href ='#title'><font  color=\"blue\">页首</font></a>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
				for (int j = 0; j < allsqls.length; j++) {
					if ("创建表".equals(allsqls[j][1])) {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>"));
						if (allsqls[j][4] != null && !allsqls[j][4].trim().equals("")) {//是否有关键字提示
							str_column.append("<font  color=\"red\">" + allsqls[j][4] + "请修改</font></td></tr>\r\n");
						} else {
							str_column.append("</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename + "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
						}
					} else if ("创建列".equals(allsqls[j][1])) {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
								+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					} else if ("修改列".equals(allsqls[j][1])) {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=blue\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
								+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					} else {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=red\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datasourcename
								+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					}
				}
			}
			str_column.append("</table><br>\r\n");
		}

		str_table.append("</tr></table><br><hr>\r\n");
		str_column.append("</body></html>");
		str_table.append(str_column);
		return str_table.toString();
	}

	/**
	 * 2个数据源的比较 1该怎么做2为标准
	 * @param datesourcename
	 * @return
	 * @throws Exception
	 */
	public String exportCompareDBAsHtml(String _title, String _filename, String datesourcename, String datesourcename2, boolean iffanxiang) throws Exception {
		WLTDBConnection conn = getConn(datesourcename); // 取得数据库连接!!	
		WLTDBConnection conn2 = getConn(datesourcename2); // 取得数据库连接!!	
		StringBuffer str_table = new StringBuffer();
		StringBuffer str_column = new StringBuffer();
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		URL url = this.getClass().getResource(_filename);
		if (url == null) {
			throw new WLTAppException("资源文件[" + _filename + "]不存在,请与产品开发组联系!"); //
		}
		Document doc = builder.build(this.getClass().getResourceAsStream(_filename)); // 加载XML"/cn/com/infostrategy/bs/sysapp/install/database/tables.xml"
		java.util.List list_tables = doc.getRootElement().getChildren("table");
		str_table.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		str_table.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		str_table.append("<script type=\"text/javascript\">function myconfirm(sql,datesourcename){" + "if(confirm(\"您确定执行吗\")){" + "window.open('./state?type=comitsql&sql='+sql+'&datesourcename='+datesourcename,'','','');" + "}" + "}</script>");
		str_table.append("</head>\r\n<body>\r\n");
		/*if (!iffanxiang) {
			str_table.append("<a href ='./state?type=dbcomparesfjrevose' target='_blank'><font  color=\"blue\">反向</font></a>，");
			str_table.append("<a href ='./state?type=sortedDBcomparesfj&ifrevose=false' target='_blank'><font  color=\"blue\">按类型分类</font></a><br><br>");
		} else {
			str_table.append("<a href ='./state?type=sortedDBcomparesfj&ifrevose=true' target='_blank'><font  color=\"blue\">按类型分类</font></a><br><br>");
		}*/
		str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>" + "数据源" + datesourcename + "比较" + datesourcename2 + "以后者为标准</a></td></tr>");
		int jj = 0;
		for (int i = 0; i < list_tables.size(); i++) {
			org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
			String str_tname = param.getAttributeValue("name");
			String str_tdesc = param.getAttributeValue("descr");
			String[][] allsqls = compareDictByDB2(datesourcename, datesourcename2, str_tname);
			if (allsqls != null && allsqls.length > 0) {
				if (jj == 0) {
					str_table.append("<tr><td align=\"center\">" + "" + "<br><a href='#" + str_tname + "'><font color=\"blue\">" + str_tname + "</font></a></td>\r\n");
				} else if (jj % 5 == 0) {
					str_table.append("</tr>\r\n<tr><td align=\"center\">" + "" + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
				} else {
					str_table.append("<td align=\"center\">" + "" + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
				}
				jj++;
				str_column.append("表名：<a name='" + str_tname + "' >" + str_tname + "</a>&nbsp;&nbsp;&nbsp;&nbsp;说明：" + str_tdesc + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
				str_column.append("&nbsp;&nbsp;&nbsp;&nbsp;<a href ='#title'><font  color=\"blue\">页首</font></a>\r\n<br><br><table border='1'cellpadding='5' bordercolor='#888888'>\r\n<tr bgcolor='#eeeeee'><td>表名</td><td>类型</td><td>说明</td><td>SQL</td>\r\n</tr>\r\n");
				for (int j = 0; j < allsqls.length; j++) {
					if ("创建表".equals(allsqls[j][1])) {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>"));
						if (allsqls[j][4] != null && !allsqls[j][4].trim().equals("")) {//是否有关键字提示
							str_column.append("<font  color=\"red\">" + allsqls[j][4] + "请修改</font></td></tr>\r\n");
						} else {
							str_column.append("</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datesourcename + "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
						}
					} else if ("创建列".equals(allsqls[j][1])) {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=green\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datesourcename
								+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					} else if ("修改列".equals(allsqls[j][1])) {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=blue\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datesourcename
								+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					} else {
						str_column.append("<tr><td>" + allsqls[j][0] + "</td><td>" + allsqls[j][1] + "</td><td>" + allsqls[j][2] + "</td><td><font style=\"color=red\">" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "\r\n<br>") + "</font><a style=\"cursor: hand;\" onclick=\"myconfirm('" + new TBUtil().replaceAll(allsqls[j][3], "\r\n", "") + "','" + datesourcename
								+ "')\" ><font  color=\"blue\">执行</font></a></td></tr>\r\n");
					}
				}
				str_column.append("</table><br>\r\n");
			}
		}

		str_table.append("</tr></table><br><hr>\r\n");
		str_column.append("</body></html>");
		str_table.append(str_column);
		return str_table.toString();
	}

	/**
	 * 重新安装树型结构数据
	 */
	public List reInstallTreeTypeTalbeData(String _str_table, String _str_sename, String _str_xmlfile, String _str_pkfield, String _str_path, String _str_parentpkfield) {//
		String str_table = "pub_menu"; //处理的表
		String str_sename = "S_PUB_MENU"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/1_pub_menu.xml"; //XML文件路径!!
		String str_pkfield = "id";
		String str_path = "code";//描述关联
		String str_parentpkfield = "parentmenuid"; //
		String str_firstClearSQL = "delete from " + str_table + " where code like '$%'"; //重新安装之前先要行做清空处理的SQL
		HashMap allTreeDates = new HashMap();
		List allSqls = new ArrayList();
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(str_xmlfile)); // 加载XML
			java.util.List list_records = doc.getRootElement().getChildren("record"); //遍历各条记录
			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				java.util.List list_colvalues = param.getChildren(); //
				for (int j = 0; j < list_colvalues.size(); j++) {
					org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
					allTreeDates.put(colValuNode.getAttributeValue(str_path), colValuNode.getAttributeValue(str_pkfield));//先放到map里找的时候方便
				}
			}

			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				java.util.List list_colvalues = param.getChildren(); //
				StringBuffer tableSB = new StringBuffer("insert into " + str_table + "(");
				StringBuffer dateSB = new StringBuffer("values(");
				for (int j = 0; j < list_colvalues.size(); j++) {
					org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
					String str_colFieldName = colValuNode.getName(); //列的名称
					String str_colFieldValue = colValuNode.getText(); //列的值
					if (str_parentpkfield.equalsIgnoreCase(str_colFieldName)) {
						String codeValue = param.getChildText(str_path) == null ? "-1" : param.getChildText(str_path);
						if (codeValue.indexOf("-") < 0) {
							str_colFieldValue = "-1";
						} else {
							str_colFieldValue = (String) allTreeDates.get(codeValue.substring(0, codeValue.lastIndexOf("-")));
						}
					}
					if (j == 0) {
						tableSB.append(str_colFieldName);
						dateSB.append(convertSQLValue(str_colFieldValue));
						continue;
					}
					tableSB.append(" ," + str_colFieldName);
					tableSB.append(" ," + convertSQLValue(str_colFieldValue));

					allSqls.add(new String("insert into " + str_table));
				}
			}
			return allSqls;
		} catch (Exception e) {
			e.printStackTrace(); //
		}
		return null;
	}

	/**
	 * 根据列名返回列类型
	 * @param columnname
	 * @param dsTabTD
	 * @return
	 */
	public String getType(String columnname, TableDataStruct dsTabTD) {
		if (dsTabTD == null || columnname == null || "".equals(columnname)) {
			return null;
		}
		for (int i = 0; i < dsTabTD.getHeaderName().length; i++) {
			if (dsTabTD.getHeaderName()[i].equalsIgnoreCase(columnname)) {
				return dsTabTD.getHeaderTypeName()[i];
			}
		}
		return null;
	}

	public List getDeleteSqlByParam(String _str_table, String _str_pkfield, String _str_path, String _str_parentpkfield, String codeValue, HashVO[] allDateDb) {
		List returnList = new ArrayList();
		returnList.add("delete from " + _str_table + " where " + _str_path + "='" + codeValue + "'");
		//新增后需要更新其子节点的（数据库里的）parentid
		String[] allChildid = getAllChild(_str_pkfield, _str_path, codeValue, allDateDb);
		if (allChildid != null && allChildid.length > 0) {
			for (int jj = 0; jj < allChildid.length; jj++) {
				returnList.add("update " + _str_table + " set " + _str_parentpkfield + " = null where " + _str_pkfield + " = " + allChildid[jj]);
			}
		}
		return returnList;
	}

	/**
	 * 树形 新增sql
	 * @param _str_table
	 * @param _str_sename
	 * @param _str_pkfield
	 * @param _str_path
	 * @param _str_parentpkfield
	 * @param param
	 * @param allTreeDates
	 * @param dsTabTD
	 * @param allDateDb
	 * @return
	 */
	public List getInsertSqlByParam(String _str_table, String _str_sename, String _str_pkfield, String _str_path, String _str_parentpkfield, org.jdom.Element param, HashMap allTreeDates, TableDataStruct dsTabTD, HashVO[] allDateDb) {
		List returnList = new ArrayList();
		java.util.List list_colvalues = param.getChildren(); //
		StringBuffer tableSB = new StringBuffer("insert into " + _str_table + "(");
		StringBuffer dateSB = new StringBuffer("values(");
		String id = "";
		String codeValue = "";
		for (int j = 0; j < list_colvalues.size(); j++) {
			org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
			String str_colFieldName = colValuNode.getName(); //列的名称
			String str_colFieldValue = colValuNode.getText(); //列的值
			if (_str_parentpkfield.equalsIgnoreCase(str_colFieldName)) {
				codeValue = param.getChildText(_str_path) == null ? "" : param.getChildText(_str_path);
				if (codeValue.indexOf("-") < 0) {
					str_colFieldValue = "";
				} else {
					str_colFieldValue = (String) allTreeDates.get(codeValue.substring(0, codeValue.lastIndexOf("-")));
					str_colFieldValue = str_colFieldValue == null ? "" : str_colFieldValue;
				}
			}
			if (_str_pkfield.equalsIgnoreCase(str_colFieldName)) {
				try {
					str_colFieldValue = id = new CommDMO().getSequenceNextValByDS(null, _str_sename);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			String type = getType(str_colFieldName, dsTabTD);
			if (j == 0) {
				tableSB.append(str_colFieldName);
				if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
					dateSB.append(("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)));
				} else {
					dateSB.append(("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
				}
				continue;
			}
			tableSB.append(" ," + str_colFieldName);
			if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
				dateSB.append(" ," + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)));
			} else {
				dateSB.append(" ," + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
			}

		}
		returnList.add(tableSB.append(" ) " + dateSB + " )").toString());
		//新增后需要更新其子节点的（数据库里的）parentid
		String[] allChildid = getAllChild(_str_pkfield, _str_path, codeValue, allDateDb);
		if (allChildid != null && allChildid.length > 0) {
			for (int jj = 0; jj < allChildid.length; jj++) {
				returnList.add("update " + _str_table + " set " + _str_parentpkfield + " = " + convertSQLValue(id) + " where " + _str_pkfield + " = " + convertSQLValue(allChildid[jj]));
			}
		}
		return returnList;

	}

	/**
	 * 单表 新增sql
	 * @param _str_table
	 * @param _str_sename
	 * @param _str_pkfield
	 * @param _str_path
	 * @param param
	 * @param dsTabTD
	 * @return list.get(1)为主键值
	 */
	public List getInsertSqlOneTable(String _str_table, String _str_sename, String _str_pkfield, org.jdom.Element param, TableDataStruct dsTabTD, HashMap noRef, HashMap mustRef) {
		List returnList = new ArrayList();
		java.util.List list_colvalues = param.getChildren(); //
		StringBuffer tableSB = new StringBuffer("insert into " + _str_table + "(");
		StringBuffer dateSB = new StringBuffer("values(");
		String codeValue = "";
		String id = "";
		for (int j = 0; j < list_colvalues.size(); j++) {
			org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
			String str_colFieldName = colValuNode.getName(); //列的名称
			String str_colFieldValue = colValuNode.getText(); //列的值
			if (noRef != null && noRef.size() > 0) {//不需要管的列
				if (noRef.containsKey(str_colFieldName)) {
					continue;
				}
			}
			if (_str_pkfield.equalsIgnoreCase(str_colFieldName)) {
				try {
					id = str_colFieldValue = new CommDMO().getSequenceNextValByDS(null, _str_sename);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (mustRef != null && mustRef.size() > 0) {//一定要管的列
				if (mustRef.containsKey(str_colFieldName)) {
					str_colFieldValue = (String) mustRef.get(str_colFieldName);
				}
			}

			String type = getType(str_colFieldName, dsTabTD);
			if (tableSB.indexOf("(") == tableSB.length() - 1) {
				tableSB.append(str_colFieldName);
				if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
					dateSB.append(("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)));
				} else {
					dateSB.append(("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
				}
				continue;
			}
			tableSB.append(" ," + str_colFieldName);
			if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
				dateSB.append(" ," + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)));
			} else {
				dateSB.append(" ," + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
			}
		}
		returnList.add(tableSB.append(" ) " + dateSB + " )").toString());
		returnList.add(id);
		return returnList;
	}

	/**
	 * 双表 新增sql
	 * @param _str_table
	 * @param _str_sename
	 * @param _str_pkfield
	 * @param _str_path
	 * @param param
	 * @param dsTabTD
	 * @return
	 */
	public List getInsertSqlDoubleTable(String _str_table, String _str_sename, String _str_pkfield, String forinKey, String forinValue, String _str_table2, String _str_sename2, String _str_pkfield2, String forinKey2, String forinValue2, org.jdom.Element param, TableDataStruct dsTabTD) {
		List returnList = new ArrayList();
		java.util.List list_colvalues = param.getChildren(); //
		StringBuffer tableSB = new StringBuffer("insert into " + _str_table + "(");
		StringBuffer dateSB = new StringBuffer("values(");
		String codeValue = "";
		String id = "";
		for (int j = 0; j < list_colvalues.size(); j++) {
			org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
			String str_colFieldName = colValuNode.getName(); //列的名称
			String str_colFieldValue = colValuNode.getText(); //列的值
			if (_str_pkfield.equalsIgnoreCase(str_colFieldName)) {
				try {
					str_colFieldValue = new CommDMO().getSequenceNextValByDS(null, _str_sename);
					id = str_colFieldValue;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (forinKey != null) {
				if (forinKey.equalsIgnoreCase(str_colFieldName)) {
					try {
						str_colFieldValue = forinValue;
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			if ("templet_item".equalsIgnoreCase(str_colFieldName)) {
				continue;
			}
			String type = getType(str_colFieldName, dsTabTD);
			if (j == 0) {
				tableSB.append(str_colFieldName);
				if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
					dateSB.append(("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)));
				} else {
					dateSB.append(("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
				}
				continue;
			}
			tableSB.append(" ," + str_colFieldName);
			if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
				dateSB.append(" ," + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)));
			} else {
				dateSB.append(" ," + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
			}
		}
		returnList.add(tableSB.append(" ) " + dateSB + " )").toString());

		if (_str_table2 != null) {
			TableDataStruct dsTabTDi = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTDi = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table2 + " where 1=2");
			} catch (Exception e) {
				dsTabTDi = null;
			}
			List allitems = param.getChildren("templet_item");
			if (allitems != null && allitems.size() > 0) {
				for (int jjj = 0; jjj < allitems.size(); jjj++) {
					/*HashMap noRef = new HashMap();
					noRef.put("pk_pub_templet_1_item", "pk_pub_templet_1_item");*/
					HashMap mustRef = new HashMap();
					mustRef.put("pk_pub_templet_1", id);
					List sql = getInsertSqlOneTable(_str_table2, _str_sename2, _str_pkfield2, (org.jdom.Element) allitems.get(jjj), dsTabTDi, null, mustRef);
					if (sql != null && sql.size() > 0) {
						returnList.add(sql.get(0));
					}
				}
			}
		}

		return returnList;
	}

	/**
	 * 单表更新
	 * @param _str_table
	 * @param _str_sename
	 * @param _str_pkfield
	 * @param _str_pkvalue
	 * @param _str_path
	 * @param param
	 * @param hsvo
	 * @param dsTabTD
	 * @return
	 */
	public List getCompareSqlOneTable(String _str_table, String _str_pkfield, String _str_pkvalue, String _str_path, org.jdom.Element param, HashVO hsvo, TableDataStruct dsTabTD) {
		List returnList = new ArrayList();
		java.util.List list_colvalues = param.getChildren(); //
		boolean paramb = false;
		StringBuffer tableSB = new StringBuffer("update " + _str_table + " set ");
		for (int j = 0; j < list_colvalues.size(); j++) {
			org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
			String str_colFieldName = colValuNode.getName(); //列的名称
			String str_colFieldValue = colValuNode.getText(); //列的值
			if (_str_pkfield.equalsIgnoreCase(str_colFieldName)) {
				continue;//主键就不管了
			}
			if (_str_path.equalsIgnoreCase(str_colFieldName)) {
				continue;//主键就不管了
			}
			if (hsvo != null && !str_colFieldValue.equals(hsvo.getStringValue(str_colFieldName) == null ? "" : hsvo.getStringValue(str_colFieldName))) {
				String type = getType(str_colFieldName, dsTabTD);
				if (!paramb) {
					paramb = true;
					if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
						tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)) + " ");
					} else {
						tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
					}
					continue;
				}
				if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
					tableSB.append("," + str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)) + " ");
				} else {
					tableSB.append("," + str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
				}
			}
		}
		if (tableSB.indexOf("=") <= 0) {
			return returnList;
		}

		String type2 = getType(_str_pkfield, dsTabTD);
		if ("decimal".equalsIgnoreCase(type2) || "number".equalsIgnoreCase(type2)) {
			tableSB.append(" where " + _str_pkfield + " =" + convertSQLValue(_str_pkvalue) + "");
		} else {
			tableSB.append(" where " + _str_pkfield + " ='" + convertSQLValue(_str_pkvalue) + "'");
		}

		returnList.add(tableSB.toString());
		return returnList;
	}

	/**
	 * 单表更新
	 * @param _str_table
	 * @param _str_sename
	 * @param _str_pkfield
	 * @param _str_pkvalue
	 * @param _str_path
	 * @param param
	 * @param hsvo
	 * @param dsTabTD
	 * @return
	 */
	public List getCompareSqlOneTable2(String _str_table, String _str_pkfield, String _str_pkvalue, String _str_path[], org.jdom.Element param, HashVO hsvo, TableDataStruct dsTabTD) {
		List returnList = new ArrayList();
		java.util.List list_colvalues = param.getChildren(); //
		boolean paramb = false;
		StringBuffer tableSB = new StringBuffer("update " + _str_table + " set ");
		for (int j = 0; j < list_colvalues.size(); j++) {
			org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
			String str_colFieldName = colValuNode.getName(); //列的名称
			String str_colFieldValue = colValuNode.getText(); //列的值
			if (_str_pkfield.equalsIgnoreCase(str_colFieldName)) {
				continue;//主键就不管了
			}
			boolean par = false;
			for (int ii = 0; ii < _str_path.length; ii++) {
				if (_str_path[ii].equalsIgnoreCase(str_colFieldName)) {
					par = true;
					break;
				}
			}
			if (par) {
				continue;//主键就不管了	
			}
			if (hsvo != null && !str_colFieldValue.equals(hsvo.getStringValue(str_colFieldName) == null ? "" : hsvo.getStringValue(str_colFieldName))) {
				String type = getType(str_colFieldName, dsTabTD);
				if (!paramb) {
					paramb = true;
					if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
						tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)) + " ");
					} else {
						tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
					}
					continue;
				}
				if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
					tableSB.append("," + str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)) + " ");
				} else {
					tableSB.append("," + str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
				}
			}
		}
		if (tableSB.indexOf("=") <= 0) {
			return returnList;
		}

		String type2 = getType(_str_pkfield, dsTabTD);
		if ("decimal".equalsIgnoreCase(type2) || "number".equalsIgnoreCase(type2)) {
			tableSB.append(" where " + _str_pkfield + " =" + convertSQLValue(_str_pkvalue) + "");
		} else {
			tableSB.append(" where " + _str_pkfield + " ='" + convertSQLValue(_str_pkvalue) + "'");
		}

		returnList.add(tableSB.toString());
		return returnList;
	}

	/**
	 * 一条数据的更新sql
	 * @param paramMap
	 * @param noRef
	 * @param mustRef
	 * @return
	 */
	public List getUpdateSqlOneDate(HashMap paramMap, HashMap noRef, HashMap mustRef) {
		String _str_table = (String) paramMap.get("表名");
		String pkname = (String) paramMap.get("主键名");
		String _str_codename = (String) paramMap.get("编码名");//
		String codevalues = (String) paramMap.get("编码值");//能够找到此条数据
		org.jdom.Element param = (org.jdom.Element) paramMap.get("xml结构");
		TableDataStruct dsTabTD = (TableDataStruct) paramMap.get("表结构");

		List returnList = new ArrayList();
		java.util.List list_colvalues = param.getChildren(); //
		String _str_codevalue = codevalues;
		String id = "";
		boolean paramm = false;
		if (codevalues == null || "".equals(codevalues)) {
			if (_str_codename != null && !"".equals(_str_codename)) {
				for (int j = 0; j < list_colvalues.size(); j++) {
					org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
					String str_colFieldName = colValuNode.getName(); //列的名称
					String str_colFieldValue = colValuNode.getText(); //列的值
					if (_str_codename.equalsIgnoreCase(str_colFieldName)) {
						_str_codevalue = str_colFieldValue;
						break;
					}
				}
			}

		}

		if ("".equals(_str_codevalue)) {
			return null;
		}
		HashVO[] hsvos = null;
		try {
			hsvos = new CommDMO().getHashVoArrayByDS(null, "select * from " + _str_table + " where " + _str_codename + " = '" + convertSQLValue(_str_codevalue) + "' ");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		StringBuffer tableSB = new StringBuffer("update " + _str_table + " set ");
		HashVO hsvo = null;
		if (hsvos != null && hsvos.length > 0) {
			hsvo = hsvos[0];
			if (hsvo != null) {
				id = hsvo.getStringValue(pkname);
			}
			String returnStr = new String();
			for (int j = 0; j < list_colvalues.size(); j++) {
				org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
				String str_colFieldName = colValuNode.getName(); //列的名称
				String str_colFieldValue = colValuNode.getText(); //列的值
				String type = getType(str_colFieldName, dsTabTD);
				if (noRef != null && noRef.size() > 0) {
					if (noRef.containsKey(str_colFieldName)) {
						continue;//主键就不管了
					}
				}
				if (mustRef != null && mustRef.size() > 0) {
					if (mustRef.containsKey(str_colFieldName)) {

						if (!paramm) {
							paramm = true;
							if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
								tableSB.append(str_colFieldName + "=" + convertSQLValue((String) mustRef.get(str_colFieldName)) + " ");
							} else {
								tableSB.append(str_colFieldName + "= '" + convertSQLValue((String) mustRef.get(str_colFieldName)) + "' ");
							}
						} else {
							if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
								tableSB.append("," + str_colFieldName + "=" + convertSQLValue((String) mustRef.get(str_colFieldName)) + " ");
							} else {
								tableSB.append("," + str_colFieldName + "= '" + convertSQLValue((String) mustRef.get(str_colFieldName)) + "' ");
							}
						}

					}
				}

				if (hsvo != null && !str_colFieldValue.equals(hsvo.getStringValue(str_colFieldName) == null ? "" : hsvo.getStringValue(str_colFieldName))) {

					if (!paramm) {
						paramm = true;
						if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
							tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)) + " ");
						} else {
							tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
						}
						continue;
					}
					if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
						tableSB.append("," + str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : convertSQLValue(str_colFieldValue)) + " ");
					} else {
						tableSB.append("," + str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : " '" + convertSQLValue(str_colFieldValue) + "' "));
					}
				}
			}
		}

		if (!paramm) {
			return null;
		}
		String type = getType(_str_codename, dsTabTD);
		if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
			tableSB.append(" where " + _str_codename + "=" + ("".equals(_str_codevalue) ? null : convertSQLValue(_str_codevalue)) + " ");
		} else {
			tableSB.append(" where " + _str_codename + "=" + ("".equals(_str_codevalue) ? null : " '" + convertSQLValue(_str_codevalue) + "' "));
		}
		returnList.add(tableSB.toString());
		returnList.add(id);
		return returnList;
	}

	//先只考虑子表维护关系的情况,
	public List getUpdateSqlDoubleTable(HashMap paramMap, org.jdom.Element param) {
		String _str_table = (String) paramMap.get("主表名");
		String _str_pkfield = (String) paramMap.get("主表主键名");
		String _str_squ1 = (String) paramMap.get("主表序列名");
		String _str_pkvalue = (String) paramMap.get("主表主键值");
		String _str_code = (String) paramMap.get("主表编码名");
		String zibiaoBiaoshi = (String) paramMap.get("子表标识名");
		String _str_pkfield2 = (String) paramMap.get("子表主键名");
		String _str_squ2 = (String) paramMap.get("子表序列名");
		String _str_code2 = (String) paramMap.get("子表编码名");
		String _str_forkey = (String) paramMap.get("子表外键名");
		String _str_table2 = (String) paramMap.get("子表表名");
		TableDataStruct dsTabTD = (TableDataStruct) paramMap.get("主表表结构");
		TableDataStruct dsTabTD2 = (TableDataStruct) paramMap.get("子表表结构");
		List returnList = new ArrayList();
		String codeValue = param.getChildText(_str_code);
		if (codeValue != null && !"".equals(codeValue.trim())) {
			HashMap noRef = new HashMap();
			noRef.put(_str_pkfield, _str_pkfield);
			noRef.put(_str_code, _str_code);
			noRef.put(zibiaoBiaoshi, zibiaoBiaoshi);
			HashMap param1 = new HashMap();
			param1.put("表名", _str_table);
			param1.put("主键名", _str_pkfield);
			param1.put("编码名", _str_code);
			param1.put("xml结构", param);
			param1.put("表结构", dsTabTD);
			List updatesqls = getUpdateSqlOneDate(param1, noRef, null);//主表的更新sql
			if (updatesqls != null && updatesqls.size() > 0) {
				returnList.add(new String[] { codeValue, "更新数据", "更新主表", (String) updatesqls.get(0) });
			}

			List allzibiaoxml = param.getChildren(zibiaoBiaoshi);//这里可能需要传进来 就用表名得了
			HashMap allzibiaoxmlmap = new HashMap();//放到map里号比较
			HashMap allzibiaodbmap = new HashMap();
			HashMap allzibiaoxmlhave = new HashMap();//只xml有点子表信息 需要创建
			HashMap allzibiaodbhave = new HashMap();//只数据库有的子表信息 需要删除
			HashMap allzibiaobthave = new HashMap();//两者都有的需要比较更新

			TableDataStruct dsTabTDitem = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTDitem = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table + " where 1=2");
			} catch (Exception e) {
				e.printStackTrace();
				dsTabTDitem = null;
			}

			if (allzibiaoxml != null && allzibiaoxml.size() > 0) {

				for (int ji = 0; ji < allzibiaoxml.size(); ji++) {
					org.jdom.Element pe = (org.jdom.Element) allzibiaoxml.get(ji);
					if (pe != null) {
						String itemkey = pe.getChildText("itemkey");
						if (itemkey != null && !"".equals(itemkey.trim()))
							allzibiaoxmlmap.put(itemkey.toLowerCase(), pe);
					}
				}
			}

			HashVO[] allzibiaodbvos = null;
			try {
				allzibiaodbvos = new CommDMO().getHashVoArrayByDS(null, "select * from " + _str_table2 + " where  " + _str_forkey + "  = '" + _str_pkvalue + "'");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (allzibiaodbvos != null && allzibiaodbvos.length > 0) {
				for (int ji = 0; ji < allzibiaodbvos.length; ji++) {
					String itemkey = allzibiaodbvos[ji].getStringValue("itemkey");
					if (itemkey != null && !"".equals(itemkey.trim()))
						allzibiaodbmap.put(itemkey.toLowerCase(), allzibiaodbvos[ji]);
				}
			}
			//接下来是子表的更新信息 可能有新建、删除、更新
			if (allzibiaoxmlmap == null || allzibiaoxmlmap.size() <= 0) {//xml没有子表信息则是全部删除
				if (allzibiaodbmap != null && allzibiaodbmap.size() > 0) {
					for (int jj = 0; jj < allzibiaodbmap.size(); jj++) {
						returnList.add(new String[] { codeValue, "删除数据", "删除子表", "delete from " + _str_table2 + " where " + _str_pkfield2 + " = " + ((HashVO) allzibiaodbmap.get(jj)).getStringValue("pk_pub_templet_1_item") });
					}
				} else {
				}
			} else {
				if (allzibiaodbmap != null && allzibiaodbmap.size() > 0) {
					int maxsize = allzibiaodbmap.size() >= allzibiaoxmlmap.size() ? allzibiaodbmap.size() : allzibiaoxmlmap.size();
					HashMap xmlhave = new HashMap();
					HashMap dbhave = new HashMap();
					HashMap bothhave = new HashMap();
					String[] xmlkeys = (String[]) allzibiaoxmlmap.keySet().toArray(new String[0]);
					String[] dbkeys = (String[]) allzibiaodbmap.keySet().toArray(new String[0]);
					for (int jj = 0; jj < maxsize; jj++) {

						if (jj <= allzibiaodbmap.size() - 1) {
							if (allzibiaoxmlmap.containsKey(dbkeys[jj])) {
								if (!bothhave.containsKey(dbkeys[jj]))
									bothhave.put(dbkeys[jj], allzibiaoxmlmap.get(dbkeys[jj]));
							} else {
								if (!dbhave.containsKey(dbkeys[jj]))
									dbhave.put(dbkeys[jj], allzibiaodbmap.get(dbkeys[jj]));
							}
						}
						if (jj <= allzibiaoxmlmap.size() - 1) {
							if (allzibiaodbmap.containsKey(xmlkeys[jj])) {
								if (!bothhave.containsKey(xmlkeys[jj]))
									bothhave.put(xmlkeys[jj], allzibiaoxmlmap.get(xmlkeys[jj]));
							} else {
								if (!dbhave.containsKey(xmlkeys[jj]))
									xmlhave.put(xmlkeys[jj], allzibiaoxmlmap.get(xmlkeys[jj]));
							}
						}
					}

					if (dbhave != null && dbhave.size() > 0) {
						String[] keys1 = (String[]) dbhave.keySet().toArray(new String[0]);
						for (int jjj = 0; jjj < dbhave.size(); jjj++) {
							returnList.add(new String[] { codeValue, "删除数据", "删除子表", "delete from " + _str_table2 + " where " + _str_pkfield2 + " = " + ((HashVO) dbhave.get(keys1[jjj])).getStringValue(_str_pkfield2) });
						}
					}
					if (xmlhave != null && xmlhave.size() > 0) {
						String[] keys2 = (String[]) xmlhave.keySet().toArray(new String[0]);
						HashMap mustRef = new HashMap();
						mustRef.put("pk_pub_templet_1", _str_pkvalue);
						for (int jjj = 0; jjj < xmlhave.size(); jjj++) {
							List allsqladd = getInsertSqlOneTable(_str_table2, _str_squ2, _str_pkfield2, (org.jdom.Element) xmlhave.get(keys2[jjj]), dsTabTDitem, null, mustRef);
							returnList.add(new String[] { codeValue, "创建数据", "创建子表", (String) allsqladd.get(0) });
						}
					}
					if (bothhave != null && bothhave.size() > 0) {
						String[] keys3 = (String[]) bothhave.keySet().toArray(new String[0]);
						HashMap noRef2 = new HashMap();
						noRef2.put("pk_pub_templet_1_item", "pk_pub_templet_1_item");
						noRef2.put("pk_pub_templet_1", _str_pkvalue);
						/*HashMap mustRef2 = new HashMap();
						mustRef2.put("pk_pub_templet_1", _str_pkvalue);*/
						HashMap param2 = new HashMap();
						param2.put("表名", _str_table2);
						param2.put("主键名", "pk_pub_templet_1_item");
						param2.put("表结构", dsTabTD2);
						for (int jjj = 0; jjj < bothhave.size(); jjj++) {
							param2.put("xml结构", (org.jdom.Element) bothhave.get(keys3[jjj]));
							param2.put("编码名", "pk_pub_templet_1_item");
							param2.put("编码值", ((HashVO) allzibiaodbmap.get(keys3[jjj])).getStringValue(_str_pkfield2));
							List updatesql = getUpdateSqlOneDate(param2, noRef2, null);
							if (updatesql != null && updatesql.size() > 0) {
								returnList.add(new String[] { codeValue, "更新数据", "更新子表", (String) updatesql.get(0) });
							}
						}
					}
				} else {
					HashMap mustRef = new HashMap();
					mustRef.put("pk_pub_templet_1", _str_pkvalue);
					for (int jj = 0; jj < allzibiaoxmlmap.size(); jj++) {
						List allsqladd = getInsertSqlOneTable(_str_table2, _str_squ2, _str_pkfield2, (org.jdom.Element) allzibiaoxmlmap.get(jj), dsTabTDitem, null, mustRef);
						returnList.add(new String[] { codeValue, "创建数据", "创建子表", (String) allsqladd.get(0) });
					}
				}
			}

			//String zhubiaoid = hsvo.getStringValue(_str_pkfield);//主表id
			//得到所有子表數據

		}

		return returnList;
	}

	/**
	 * 返回所有儿子节点
	 * @param pkname
	 * @param codeName
	 * @param codeValue
	 * @param allDateDb
	 * @return
	 */
	public String[] getAllChild(String pkname, String codeName, String codeValue, HashVO[] allDateDb) {
		List returnList = new ArrayList();
		if (allDateDb != null && allDateDb.length > 0) {
			for (int i = 0; i < allDateDb.length; i++) {
				String childValue = allDateDb[i].getStringValue(codeName);
				if (childValue.indexOf("-") <= 0) {
					continue;
				}
				if (childValue.substring(0, childValue.lastIndexOf("-")).equals(codeValue)) {
					returnList.add(allDateDb[i].getStringValue(pkname));
				}
			}
			if (returnList != null && returnList.size() > 0) {
				return (String[]) returnList.toArray(new String[0]);
			}
		}
		return null;
	}

	/**
	 * 树形更新sql
	 * @param _str_table
	 * @param _str_sename
	 * @param _str_pkfield
	 * @param _str_path
	 * @param _str_parentpkfield
	 * @param param
	 * @param allTreeDates
	 * @param hsvo
	 * @param dsTabTD
	 * @param allDateDb
	 * @return
	 */
	public List getCompareSqlByParam(String _str_table, String _str_sename, String _str_pkfield, String _str_path, String _str_parentpkfield, org.jdom.Element param, HashMap allTreeDates, HashVO hsvo, TableDataStruct dsTabTD, HashVO[] allDateDb) {
		List returnList = new ArrayList();
		java.util.List list_colvalues = param.getChildren(); //
		StringBuffer tableSB = new StringBuffer("update " + _str_table + " set ");
		for (int j = 0; j < list_colvalues.size(); j++) {
			org.jdom.Element colValuNode = (org.jdom.Element) list_colvalues.get(j); //
			String str_colFieldName = colValuNode.getName(); //列的名称
			String str_colFieldValue = colValuNode.getText(); //列的值
			if (_str_parentpkfield.equalsIgnoreCase(str_colFieldName)) {
				continue;//主键就不管了
			}
			if (_str_pkfield.equalsIgnoreCase(str_colFieldName)) {
				continue;//主键就不管了
			}
			if (_str_path.equalsIgnoreCase(str_colFieldName)) {
				continue;//主键就不管了
			}
			if (hsvo != null && !str_colFieldValue.equals(hsvo.getStringValue(str_colFieldName) == null ? "" : hsvo.getStringValue(str_colFieldName))) {
				String type = getType(str_colFieldName, dsTabTD);
				if (j == 0) {
					if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
						tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : str_colFieldValue) + " ");
					} else {
						tableSB.append(str_colFieldName + "='" + str_colFieldValue + "' ");
					}
					continue;
				}
				if ("decimal".equalsIgnoreCase(type) || "number".equalsIgnoreCase(type)) {
					tableSB.append(str_colFieldName + "=" + ("".equals(str_colFieldValue) ? null : str_colFieldValue) + " ");
				} else {
					tableSB.append(str_colFieldName + "='" + str_colFieldValue + "' ");
				}
			}
		}
		if (tableSB.indexOf("=") <= 0) {
			return returnList;
		}
		tableSB.append(" where " + _str_path + " ='" + param.getChildText(_str_path) + "'");
		returnList.add(tableSB.toString());
		return returnList;
	}

	/**
	 * 菜单比较
	 * @return
	 */
	public String[][] compareMenuDateByDB() {
		String str_table = "pub_menu"; //处理的表
		String str_sename = "S_PUB_MENU"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/1_pub_menu.xml"; //XML文件路径!!
		String str_pkfield = "id";
		String str_path = "code";//描述关联
		String str_parentpkfield = "parentmenuid"; //
		return compareTreeDateByDB(str_table, str_sename, str_xmlfile, str_pkfield, str_path, str_parentpkfield);
	}

	/**
	 * 菜单提交
	 * @param codeValue
	 */
	public void dealOneMenuCommit(String codeValue) {
		String str_table = "pub_menu"; //处理的表
		String str_sename = "S_PUB_MENU"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/1_pub_menu.xml"; //XML文件路径!!
		String str_pkfield = "id";
		String str_path = "code";//描述关联
		String str_parentpkfield = "parentmenuid"; //
		dealOneTreeCommit(str_table, str_sename, str_xmlfile, str_pkfield, str_path, str_parentpkfield, codeValue);
	}

	//树形数据执行更新sql时候  那就在生成sql时候先不给id与parentid 在提交时在给得了 那就是每一条记录
	public void dealOneTreeCommit(String _str_table, String _str_sename, String _str_xmlfile, String _str_pkfield, String _str_path, String _str_parentpkfield, String codeValue) {
		HashMap allTreeDates = new HashMap();
		HashMap allTypeDates = new HashMap();
		HashMap allXmlDates = new HashMap();
		List allSqls = new ArrayList();
		List sql = new ArrayList();
		try {
			TableDataStruct dsTabTD = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTD = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table + " where 1=2");
			} catch (Exception e) {
				//e.printStackTrace();
				dsTabTD = null;
			}
			HashVO[] allDateDb = new CommDMO().getHashVoArrayByDS(null, "select * from " + _str_table + " where code like '$%'");
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(_str_xmlfile)); // 加载XML
			java.util.List list_records = doc.getRootElement().getChildren("record"); //遍历各条记录 由于主键与parentid自动生成做一要到数据库里找，没有的要先创建
			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				String colValu = param.getChildText(_str_path);
				allXmlDates.put(colValu, colValu);
				if (ifContain(allDateDb, _str_path, colValu)) {
					allTreeDates.put(colValu, getBillVOValue(allDateDb, _str_path, colValu, _str_pkfield));//从数据库里查主键
				}
			}
			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				if (codeValue.equals(param.getChildText(_str_path))) {
					if (ifContain(allDateDb, _str_path, codeValue)) {//如果有这条记录 更新的原则 主键肯定不管 其他的好说 如果code变化了即树结构变化了 则需要更新parentid 仔细一想其实也没有修改code的情况 因为改了code就是新增了和删除了不能修改code
						sql = getCompareSqlByParam(_str_table, _str_sename, _str_pkfield, _str_path, _str_parentpkfield, param, allTreeDates, getContainVO(allDateDb, _str_path, codeValue), dsTabTD, allDateDb);
					} else {//如果没有这条记录直接创建 原则是:查该节点的父节点 没有则先置为空。 执行sqlcomit后更新 其子节点的parentid
						sql = getInsertSqlByParam(_str_table, _str_sename, _str_pkfield, _str_path, _str_parentpkfield, param, allTreeDates, dsTabTD, allDateDb);
					}
				}
			}
			for (int jj = 0; jj < allDateDb.length; jj++) {
				if (!allXmlDates.containsKey(codeValue)) {
					sql = getDeleteSqlByParam(_str_table, _str_pkfield, _str_path, _str_parentpkfield, codeValue, allDateDb);
				}
			}

			if (sql != null && sql.size() > 0) {
				for (int jjj = 0; jjj < sql.size(); jjj++) {
					new CommDMO().executeBatchByDS(null, new String[] { (String) sql.get(jjj) });
				}
			}

		} catch (Exception e) {
			e.printStackTrace(); //
		}
	}

	/**
	 * 判断hashvo是否存在某代码的数据
	 * @param dates
	 * @param _str_path
	 * @param _str_pathValue
	 * @return
	 */
	private boolean ifContain(HashVO[] dates, String _str_path, String _str_pathValue) {
		if (dates == null || dates.length <= 0) {
			return false;
		} else {
			for (int i = 0; i < dates.length; i++) {
				if (dates[i].getStringValue(_str_path).equalsIgnoreCase(_str_pathValue)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断hashvo是否存在某代码的数据
	 * @param dates
	 * @param _str_path
	 * @param _str_pathValue
	 * @return
	 */
	private boolean ifContain(HashVO[] dates, String[] _str_path, String[] _str_pathValue) {
		if (dates == null || dates.length <= 0) {
			return false;
		} else {
			for (int i = 0; i < dates.length; i++) {
				int param = 0;
				for (int j = 0; j < _str_path.length; j++) {
					if (dates[i].getStringValue(_str_path[j]) != null)
						if (dates[i].getStringValue(_str_path[j]).equalsIgnoreCase(_str_pathValue[j])) {
							param++;
						}
				}
				if (param == _str_path.length) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 得到某字段_str_path的值=_str_pathValue的HASHVO
	 * @param dates
	 * @param _str_path
	 * @param _str_pathValue
	 * @return
	 */
	private HashVO getContainVO(HashVO[] dates, String _str_path, String _str_pathValue) {
		if (dates == null || dates.length <= 0) {
			return null;
		} else {
			for (int i = 0; i < dates.length; i++) {
				if (dates[i].getStringValue(_str_path).equalsIgnoreCase(_str_pathValue)) {
					return dates[i];
				}
			}
		}
		return null;
	}

	/**
	 * 得到某字段_str_path的值=_str_pathValue的HASHVO
	 * @param dates
	 * @param _str_path
	 * @param _str_pathValue
	 * @return
	 */
	private HashVO getContainVO(HashVO[] dates, String[] _str_path, String[] _str_pathValue) {
		if (dates == null || dates.length <= 0) {
			return null;
		} else {
			for (int i = 0; i < dates.length; i++) {
				int param = 0;
				for (int j = 0; j < _str_path.length; j++) {
					if (dates[i].getStringValue(_str_path[j]) != null)
						if (dates[i].getStringValue(_str_path[j]).equalsIgnoreCase(_str_pathValue[j])) {
							param++;
						}
				}
				if (param == _str_path.length) {
					return dates[i];
				}
			}
		}
		return null;
	}

	/**
	 * 返回需要列needcolumn的值
	 * @param dates
	 * @param whichcolumn
	 * @param columnValue
	 * @param needcolumn
	 * @return
	 */
	private String getBillVOValue(HashVO[] dates, String whichcolumn, String columnValue, String needcolumn) {
		if (dates == null || dates.length <= 0) {
			return "";
		} else {
			for (int i = 0; i < dates.length; i++) {
				if (dates[i].getStringValue(whichcolumn).equalsIgnoreCase(columnValue)) {
					return dates[i].getStringValue(needcolumn);
				}
			}
		}
		return "";
	}

	/**
	 * 返回需要列needcolumn的值
	 * @param dates
	 * @param whichcolumn
	 * @param columnValue
	 * @param needcolumn
	 * @return
	 */
	private String getBillVOValue(HashVO[] dates, String[] whichcolumn, String[] columnValue, String needcolumn) {
		if (dates == null || dates.length <= 0) {
			return "";
		} else {
			for (int i = 0; i < dates.length; i++) {

				int param = 0;
				for (int j = 0; j < whichcolumn.length; j++) {
					if (dates[i].getStringValue(whichcolumn[j]) != null)
						if (dates[i].getStringValue(whichcolumn[j]).equalsIgnoreCase(columnValue[j])) {
							param++;
						}
				}
				if (param == whichcolumn.length) {
					return dates[i].getStringValue(needcolumn);
				}
			}
		}
		return "";
	}

	//按钮比较
	public String[][] compareRegbuttonDateByDB() {
		String str_table = "pub_regbuttons"; //处理的表
		String str_sename = "S_PUB_REGBUTTONS"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/2_pub_regbuttons.xml"; //XML文件路径!!
		String str_pkfield = "id";
		String str_path = "code";//
		return compareOneTableDateByDB(str_table, str_sename, str_xmlfile, str_pkfield, str_path, "refbutton");
	}

	//样板比较
	public String[][] compareRegformatPanelDateByDB() {
		String str_table = "pub_regformatpanel"; //处理的表
		String str_sename = "S_PUB_REGFORMATPANEL"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/3_pub_regformatpanel.xml"; //XML文件路径!!
		String str_pkfield = "id";
		String str_path = "code";//
		return compareOneTableDateByDB(str_table, str_sename, str_xmlfile, str_pkfield, str_path, "regformatpanel");
	}

	//参照比较
	public String[][] compareRegregisterDateByDB() {
		String str_table = "pub_refregister"; //处理的表
		String str_sename = "S_PUB_REGREGISTER"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/4_pub_refregister.xml"; //XML文件路径!!
		String str_pkfield = "id";
		String str_path = "name";//
		return compareOneTableDateByDB(str_table, str_sename, str_xmlfile, str_pkfield, str_path, "refregister");
	}

	//下拉字典比较 可能需要改一下 下拉字典除了id没有能唯一标识的字段 应该先按类型比较 都有的类型再按名称比较即2个字段来唯一标识
	public String[][] compareComboboxdictDateByDB() {
		String str_table = "pub_comboboxdict"; //处理的表
		String str_sename = "S_PUB_COMBOBOXDICT"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/5_pub_comboboxdict.xml"; //XML文件路径!!
		String str_pkfield = "pk_pub_comboboxdict";
		String str_path = "type,id";//
		return compareOneTableDateByDB2(str_table, str_sename, str_xmlfile, str_pkfield, str_path, "combobox");
	}

	//參數
	public String[][] compareOptionDateByDB() {
		String str_table = "pub_option"; //处理的表
		String str_sename = "S_PUB_OPTION"; //序列名
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/7_pub_option.xml"; //XML文件路径!!
		String str_pkfield = "id";//主键
		String str_path = "parkey";//编码 能标识一条记录的字段
		return compareOneTableDateByDB(str_table, str_sename, str_xmlfile, str_pkfield, str_path, "option");
	}

	//系統風格
	public String[][] compareLookandfeelDateByDB() {
		String str_table = "pub_lookandfeel"; //处理的表
		String str_sename = "S_PUB_LOOKANDFEEL"; //序列名
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/6_pub_lookandfeel.xml"; //XML文件路径!!
		String str_pkfield = "id";//主键
		String str_path = "code";//编码 能标识一条记录的字段
		return compareOneTableDateByDB(str_table, str_sename, str_xmlfile, str_pkfield, str_path, "lookandfeel");
	}

	/**
	 * 用户比较 不用在升级或安装完再去数据库增加用户直接增加一个用户$admin 1 1
	 * @return
	 */
	public String[][] compareUserDateByDB() {
		String str_table = "pub_user"; //处理的表
		String str_sename = "S_PUB_USER"; //
		String str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/9_pub_users.xml"; //XML文件路径!!
		String str_pkfield = "id";
		String str_path = "code";//
		return compareOneTableDateByDB(str_table, str_sename, str_xmlfile, str_pkfield, str_path, "pub_user");
	}

	/**
	 * 模板比较
	 * @return
	 */
	public String[][] comparetempletDateByDB() {
		String _str_table1 = "pub_templet_1"; //处理的表
		String _str_sename1 = "S_PUB_TEMPLET_1"; //
		String _str_xmlfile = "/cn/com/infostrategy/bs/sysapp/install/initdata/8_pub_templet.xml"; //XML文件路径!!
		String _str_pkfield1 = "pk_pub_templet_1";
		String _str_code1 = "templetcode";//
		String _str_table2 = "pub_templet_1_item"; //处理的表
		String _str_sename2 = "S_PUB_TEMPLET_1_ITEM"; //
		String _str_pkfield2 = "pk_pub_templet_1_item";
		String _str_code2 = "itemkey";//
		String forainKey = "pk_pub_templet_1";
		return compareDoubleTableDateByDB(_str_table1, _str_table2, _str_sename1, _str_sename2, _str_xmlfile, _str_pkfield1, _str_pkfield2, _str_code1, _str_code2, forainKey);
	}

	/**
	 * 孙富君实现
	 * 比较XML中定义的数据菜单数据与数据库的菜单数据比较,返回结果!!
	 * @return  二维数组,第一列是数据code,第二列是类型(那些字段数据不同或者直接创建,不比较id与parentid),第三列中说明,第四列是SQL(一条记录一个sql)，删除记录的先不比较
	 */
	public String[][] compareTreeDateByDB(String _str_table, String _str_sename, String _str_xmlfile, String _str_pkfield, String _str_path, String _str_parentpkfield) {
		HashMap allTreeDates = new HashMap();
		HashMap allTypeDates = new HashMap();
		HashMap allXmlDates = new HashMap();
		List allSqls = new ArrayList();
		try {
			TableDataStruct dsTabTD = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTD = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table + " where 1=2");
			} catch (Exception e) {
				//e.printStackTrace();
				dsTabTD = null;
			}
			HashVO[] allDateDb = new CommDMO().getHashVoArrayByDS(null, "select * from " + _str_table + " where code like '$%'");
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(_str_xmlfile)); // 加载XML
			java.util.List list_records = doc.getRootElement().getChildren("record"); //遍历各条记录 由于主键与parentid自动生成做一要到数据库里找，没有的要先创建
			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				String colValu = param.getChildText(_str_path);
				allXmlDates.put(colValu, colValu);
				if (ifContain(allDateDb, _str_path, colValu)) {
					allTreeDates.put(colValu, getBillVOValue(allDateDb, _str_path, colValu, _str_pkfield));//从数据库里查主键
				}
			}

			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				if (ifContain(allDateDb, _str_path, param.getChildText(_str_path))) {//如果有这条记录 更新的原则 主键肯定不管 其他的好说 如果code变化了即树结构变化了 则需要更新parentid
					List sqlList = getCompareSqlByParam(_str_table, _str_sename, _str_pkfield, _str_path, _str_parentpkfield, param, allTreeDates, getContainVO(allDateDb, _str_path, param.getChildText(_str_path)), dsTabTD, allDateDb);
					if (sqlList != null && sqlList.size() > 0) {
						String sql = (String) sqlList.get(0);
						if (sql != null && !"".equals(sql.trim()))
							allSqls.add(new String[] { param.getChildText(_str_path), "更新数据", "更新了菜单树", sql });
					}

				} else {//如果没有这条记录直接创建 原则是:查该节点的父节点 没有则先置为空。 执行sqlcomit后更新 其子节点的parentid

					List sqlList = getInsertSqlByParam(_str_table, _str_sename, _str_pkfield, _str_path, _str_parentpkfield, param, allTreeDates, dsTabTD, allDateDb);
					if (sqlList != null && sqlList.size() > 0) {
						String sql = (String) sqlList.get(0);
						if (sql != null && !"".equals(sql.trim()))
							allSqls.add(new String[] { param.getChildText(_str_path), "创建数据", "新增功能菜单", sql });
					}

				}
			}
			for (int jj = 0; jj < allDateDb.length; jj++) {
				String value = allDateDb[jj].getStringValue(_str_path);
				if (!allXmlDates.containsKey(value)) {
					allSqls.add(new String[] { value, "删除数据", "数据库有而定义无则删除之", "delete from " + _str_table + "where " + _str_path + "='" + value + "'" });
				}
			}
			//删除的应该便利hashvo了
			if (allSqls != null && allSqls.size() > 0) {
				return (String[][]) allSqls.toArray(new String[0][0]);
			}

		} catch (Exception e) {
			e.printStackTrace(); //
		}
		return null;
	}

	/**
	 * 孙富君实现 单表
	 * 比较XML中定义的数据菜单数据与数据库的菜单数据比较,返回结果!!
	 * @return  二维数组,第一列是数据code,第二列是类型(那些字段数据不同或者直接创建,不比较id与parentid),第三列中说明,第四列是SQL(一条记录一个sql)，删除记录的先不比较
	 */
	public String[][] compareOneTableDateByDB(String _str_table, String _str_sename, String _str_xmlfile, String _str_pkfield, String _str_path, String biaoshi) {
		try {
			HashMap allXmlDates = new HashMap();
			List allSqls = new ArrayList();
			TableDataStruct dsTabTD = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTD = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table + " where 1=2");
			} catch (Exception e) {
				dsTabTD = null;
			}
			HashVO[] allDateDb = new CommDMO().getHashVoArrayByDS(null, "select * from " + _str_table + " where " + _str_path + " like '$%'");//这里要不要搞$是个问题

			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(_str_xmlfile)); // 加载XML
			java.util.List list_records = doc.getRootElement().getChildren(biaoshi); //遍历各条记录 由于主键与parentid自动生成做一要到数据库里找，没有的要先创建
			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				String colValu = param.getChildText(_str_path);
				allXmlDates.put(colValu, colValu);
				if (ifContain(allDateDb, _str_path, colValu)) {
					String dbidvalue = getBillVOValue(allDateDb, _str_path, colValu, _str_pkfield);
					HashVO containVO = getContainVO(allDateDb, _str_path, colValu);
					List sql = getCompareSqlOneTable(_str_table, _str_pkfield, dbidvalue, _str_path, param, containVO, dsTabTD);
					if (sql != null && sql.size() > 0) {
						allSqls.add(new String[] { colValu, "更新数据", "更新数据", (String) sql.get(0) });
					}
				} else {
					List sql = getInsertSqlOneTable(_str_table, _str_sename, _str_pkfield, param, dsTabTD, null, null);
					if (sql != null && sql.size() > 0) {
						allSqls.add(new String[] { colValu, "创建数据", "创建数据", (String) sql.get(0) });
					}
				}
			}

			//删除的应该便利hashvo了
			for (int jj = 0; jj < allDateDb.length; jj++) {
				String value = allDateDb[jj].getStringValue(_str_path);
				if (!allXmlDates.containsKey(value)) {
					allSqls.add(new String[] { value, "删除数据", "数据库有而定义无则删除之", "delete from " + _str_table + " where " + _str_path + "='" + convertSQLValue(value) + "'" });
				}
			}
			if (allSqls != null && allSqls.size() > 0) {
				return (String[][]) allSqls.toArray(new String[0][0]);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 孙富君实现 单表 可能没有编码即不能用一个字段类似编码来标识一条记录 可能2个或多个字段来标识例如下拉字典只能用类型加上编码或其他字段来唯一标识了
	 * 比较XML中定义的数据菜单数据与数据库的菜单数据比较,返回结果!!
	 * @return  二维数组,第一列是数据code,第二列是类型,第三列中说明,第四列是SQL，删除记录的先不比较
	 */
	public String[][] compareOneTableDateByDB2(String _str_table, String _str_sename, String _str_xmlfile, String _str_pkfield, String _str_path, String biaoshi) {
		try {
			String[] paths = _str_path.split(",");
			HashMap allXmlDates = new HashMap();
			List allSqls = new ArrayList();
			TableDataStruct dsTabTD = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTD = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table + " where 1=2");
			} catch (Exception e) {
				dsTabTD = null;
			}
			HashVO[] allDateDb = new CommDMO().getHashVoArrayByDS(null, "select * from " + _str_table + " where 1=1 ");//+ paths[0] + " like '$%'");先不搞$

			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(_str_xmlfile)); // 加载XML
			java.util.List list_records = doc.getRootElement().getChildren(biaoshi); //遍历各条记录 由于主键与parentid自动生成做一要到数据库里找，没有的要先创建
			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				String[] colValus = new String[paths.length];
				StringBuffer keys = new StringBuffer();
				for (int jc = 0; jc < colValus.length; jc++) {
					colValus[jc] = param.getChildText(paths[jc]);
					if (jc == 0) {
						keys.append(param.getChildText(paths[jc]));
					} else {
						keys.append("-" + param.getChildText(paths[jc]));
					}

				}
				allXmlDates.put(keys.toString(), keys.toString());
				if (ifContain(allDateDb, paths, colValus)) {//数据库包含的更新
					String dbidvalue = getBillVOValue(allDateDb, paths, colValus, _str_pkfield);
					HashVO containVO = getContainVO(allDateDb, paths, colValus);
					List sql = getCompareSqlOneTable2(_str_table, _str_pkfield, dbidvalue, paths, param, containVO, dsTabTD);
					if (sql != null && sql.size() > 0) {
						allSqls.add(new String[] { keys.toString(), "更新数据", "更新数据", (String) sql.get(0) });
					}
				} else {//否则创建数据
					List sql = getInsertSqlOneTable(_str_table, _str_sename, _str_pkfield, param, dsTabTD, null, null);
					if (sql != null && sql.size() > 0) {
						allSqls.add(new String[] { keys.toString(), "创建数据", "创建数据", (String) sql.get(0) });
					}
				}
			}

			//删除的应该便利hashvo了
			for (int jj = 0; jj < allDateDb.length; jj++) {
				String[] colValus = new String[paths.length];
				StringBuffer keys = new StringBuffer();
				StringBuffer sqlap = new StringBuffer();
				for (int jc = 0; jc < colValus.length; jc++) {
					colValus[jc] = allDateDb[jj].getStringValue(paths[jc]);
					sqlap.append(" and " + paths[jc] + "='" + colValus[jc] + "'");
					if (jc == 0) {
						keys.append(allDateDb[jj].getStringValue(paths[jc]));
					} else {
						keys.append("-" + allDateDb[jj].getStringValue(paths[jc]));
					}

				}
				//String value = allDateDb[jj].getStringValue(_str_path);
				if (!allXmlDates.containsKey(keys.toString())) {//
					allSqls.add(new String[] { keys.toString(), "删除数据", "数据库有而定义无则删除之", "delete from " + _str_table + " where 1=1 " + sqlap });
				}
			}
			if (allSqls != null && allSqls.size() > 0) {
				return (String[][]) allSqls.toArray(new String[0][0]);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 孙富君实现 双表
	 * 比较XML中定义的模板数据与数据库的模板数据比较,返回结果!!
	 * @return  二维数组,第一列是数据code,第二列是类型(那些字段数据不同或者直接创建,不比较id与parentid),第三列中说明,第四列是SQL(一条记录一个sql)，删除记录的先不比较
	 * 比较原则主表好说，主表新增子表就不比较，主表删除子表就不比较，主表都有可能更细  子表呢根据key 判断主表有那些key与数据库比较判断是否增加key与删除，相同key子表去比较更新
	 * 有主表存子表主键、有子表存主表主键、有2中综合 先考虑子表存主表主键
	 */
	public String[][] compareDoubleTableDateByDB(String _str_table1, String _str_table2, String _str_sename1, String _str_sename2, String _str_xmlfile, String _str_pkfield1, String _str_pkfield2, String _str_code1, String _str_code2, String forainKey) {
		//先比较主表的普通单表比较

		try {
			HashMap allTreeDates = new HashMap();
			HashMap allTypeDates = new HashMap();
			HashMap allXmlDates = new HashMap();
			HashMap paramsMap = new HashMap();
			List allSqls = new ArrayList();
			TableDataStruct dsTabTD = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTD = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table1 + " where 1=2");
			} catch (Exception e) {
				dsTabTD = null;
			}
			TableDataStruct dsTabTD2 = null;
			//先取出数据源中该表的实际定义
			try {
				dsTabTD2 = new CommDMO().getTableDataStructByDS(null, "select * from " + _str_table2 + " where 1=2");
			} catch (Exception e) {
				//e.printStackTrace();
				dsTabTD2 = null;
			}
			HashVO[] allDateDb = new CommDMO().getHashVoArrayByDS(null, "select * from " + _str_table1 + " where " + _str_code1 + " like '%'");//先不搞$
			paramsMap.put("主表名", _str_table1);
			paramsMap.put("主表主键名", _str_pkfield1);
			paramsMap.put("主表序列名", _str_sename1);
			paramsMap.put("主表编码名", _str_code1);
			paramsMap.put("子表标识名", "templet_item");
			paramsMap.put("子表表名", _str_table2);
			paramsMap.put("主表表结构", dsTabTD);
			paramsMap.put("子表表结构", dsTabTD2);
			paramsMap.put("子表主键名", _str_pkfield2);
			paramsMap.put("子表序列名", _str_sename2);
			paramsMap.put("子表编码名", _str_code2);
			paramsMap.put("子表外键名", forainKey);
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream(_str_xmlfile)); // 加载XML
			java.util.List list_records = doc.getRootElement().getChildren("billtemplet"); //遍历各条记录 由于主键与parentid自动生成做一要到数据库里找，没有的要先创建
			for (int i = 0; i < list_records.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_records.get(i);
				String colValu = param.getChildText(_str_code1);
				allXmlDates.put(colValu, colValu);
				if (ifContain(allDateDb, _str_code1, colValu)) {
					String dbidvalue = getBillVOValue(allDateDb, _str_code1, colValu, _str_pkfield1);
					allTreeDates.put(colValu, dbidvalue);//从数据库里查主键
					HashVO containVO = getContainVO(allDateDb, _str_code1, colValu);
					paramsMap.put("主表主键值", containVO.getStringValue(_str_pkfield1));
					List sql = getUpdateSqlDoubleTable(paramsMap, param);
					if (sql != null && sql.size() > 0) {
						//跟新包括更新主表信息或更新子表信息或删除子表或增加子表 情况较多
						//更新可以选择执行
						for (int ii = 0; ii < sql.size(); ii++) {
							allSqls.add((String[]) sql.get(ii));
						}
					}
				} else {
					//创建就是创建主表与子表
					//方法有点笨
					List sql = getInsertSqlDoubleTable(_str_table1, _str_sename1, _str_pkfield1, null, null, _str_table2, _str_sename2, _str_pkfield2, forainKey, null, param, dsTabTD);
					if (sql != null && sql.size() > 0) {
						StringBuffer sqlsb = new StringBuffer();
						for (int ii = 0; ii < sql.size(); ii++) {
							Object obj = sql.get(ii);
							if (obj != null) {
								if (obj instanceof String) {
									if (ii == 0) {
										sqlsb.append((String) obj);
									} else {
										sqlsb.append(">;<" + (String) obj);
									}

								} else if (obj instanceof List) {
									if (obj != null && ((List) obj).size() > 0) {
										for (int jjj = 0; jjj < ((List) obj).size(); jjj++) {
											sqlsb.append(">;<" + (String) ((List) obj).get(jjj));
										}
									}
								}
							}
						}
						//也做成一起执行的
						allSqls.add(new String[] { colValu, "创建数据", "定义有数据库无创建数据", sqlsb.toString() });
					}
				}
			}

			//删除的应该便利hashvo了
			for (int jj = 0; jj < allDateDb.length; jj++) {
				String value = allDateDb[jj].getStringValue(_str_code1);
				String id = allDateDb[jj].getStringValue(_str_pkfield1);
				if (!allXmlDates.containsKey(value)) {
					//删除主表与所有子表 直接写就OK了 感觉要执行还是一起执行的号 先做成一起执行
					allSqls.add(new String[] { value, "删除主表", "数据库有而定义无则删除之", "delete from " + _str_table1 + " where " + _str_pkfield1 + "='" + id + "' >;< " + "delete from " + _str_table2 + " where " + forainKey + "='" + id + "'" });
					//allSqls.add(new String[] { value, "删除关联子表", "数据库有而定义无则删除之", });
				}
			}
			if (allSqls != null && allSqls.size() > 0) {
				return (String[][]) allSqls.toArray(new String[0][0]);
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

		//然后比较子表
	}

	//先之比较列名 不知怎么比较号
	public String[][] compareViewByDB() {
		List allSqls = new ArrayList();
		try {
			org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
			Document doc = builder.build(this.getClass().getResourceAsStream("/cn/com/infostrategy/bs/sysapp/install/database/views.xml"));
			java.util.List list_records = doc.getRootElement().getChildren("view");
			if (list_records != null && list_records.size() > 0) {
				for (int i = 0; i < list_records.size(); i++) {
					org.jdom.Element param = (org.jdom.Element) list_records.get(i);
					String viewname = param.getAttributeValue("name");
					String sql = param.getChildText("sql");
					if (sql != null && !"".equals(sql.trim()) && viewname != null && !"".equals(viewname.trim())) {
						TableDataStruct dsTabTD = null;
						//先取出数据源中该表的实际定义
						try {
							dsTabTD = new CommDMO().getTableDataStructByDS(null, "select * from " + viewname + " where 1=2");
						} catch (Exception e) {
							//e.printStackTrace();
							dsTabTD = null;
						}
						if (dsTabTD != null) {
							//数据库里有此视图则进行比较
							int bg = sql.indexOf(viewname);//提取select的sql
							String sql2 = sql.substring(bg + viewname.length(), sql.length());
							String sql3 = sql2.substring(sql2.indexOf("as") + 2, sql2.length());
							TableDataStruct dsTabTD2 = null;
							try {
								dsTabTD2 = new CommDMO().getTableDataStructByDS(null, "select * from (" + sql3 + ")aaa where 1=2");//定义的表结构
							} catch (Exception e) {
								//e.printStackTrace();
								dsTabTD2 = null;
							}

							//接下来就是比较dsTabTD2与dsTabTD了 先之比较列名得了 列的个数先比较了  更新视图就是 drop了再create
							if (dsTabTD2 != null) {
								//放到map里
								String[] headnamedb = dsTabTD.getHeaderName();
								String[] headnamexml = dsTabTD2.getHeaderName();
								HashMap headnamedbmap = new HashMap();
								HashMap headnamexmlmap = new HashMap();
								HashMap bothhave = new HashMap();
								HashMap xmlhave = new HashMap();
								HashMap dbhave = new HashMap();
								int maxlength = headnamedb.length > headnamexml.length ? headnamedb.length : headnamexml.length;
								for (int j = 0; j < maxlength; j++) {
									if (j < headnamedb.length) {
										headnamedbmap.put(headnamedb[j].toLowerCase(), headnamedb[j].toLowerCase());
									}
									if (j < headnamexml.length) {
										headnamexmlmap.put(headnamexml[j].toLowerCase(), headnamexml[j].toLowerCase());
									}
								}

								for (int j = 0; j < maxlength; j++) {
									if (j < headnamedb.length) {
										if (!headnamexmlmap.containsKey(headnamedb[j].toLowerCase())) {
											dbhave.put(headnamedb[j].toLowerCase(), headnamedb[j].toLowerCase());
										}
									}
									if (j < headnamexml.length) {
										if (!headnamedbmap.containsKey(headnamexml[j].toLowerCase())) {
											xmlhave.put(headnamexml[j].toLowerCase(), headnamexml[j].toLowerCase());
										}
									}
								}
								StringBuffer sb = new StringBuffer();
								if (xmlhave != null && xmlhave.size() > 0) {
									String[] keys = (String[]) xmlhave.keySet().toArray(new String[0]);
									sb.append("定义新增了列:");
									for (int jj = 0; jj < xmlhave.size(); jj++) {
										if (jj == 0) {
											sb.append(keys[jj].toLowerCase());
											continue;
										}
										sb.append("、" + keys[jj].toLowerCase());
									}
								}
								if (dbhave != null && dbhave.size() > 0) {
									String[] keys = (String[]) dbhave.keySet().toArray(new String[0]);
									sb.append("定义删除了列:");
									for (int jj = 0; jj < dbhave.size(); jj++) {
										if (jj == 0) {
											sb.append(keys[jj].toLowerCase());
											continue;
										}
										sb.append("、" + keys[jj].toLowerCase());
									}
								}

								if ((dbhave != null && dbhave.size() > 0) || (xmlhave != null && xmlhave.size() > 0)) {
									allSqls.add(new String[] { viewname, "更新视图", sb.toString(), "drop view " + viewname + "; " + sql });
								}
							}
						} else {
							allSqls.add(new String[] { viewname, "创建视图", "创建视图", sql });
						}

					}
				}
			}
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (allSqls != null && allSqls.size() > 0) {
			return (String[][]) allSqls.toArray(new String[0][0]);
		}
		return null;
	}

	/**
	 * 防止'造成的问题 类似
	 * @param _value
	 * @return
	 */
	private String convertSQLValue(String _value) {
		if (_value == null) {
			return null;
		} else if ("".equals(_value.trim())) {
			return null;
		} else {
			_value = replaceAll(_value, "'", "''"); //
			_value = replaceAll(_value, "\\", "\\\\"); //
			/*_value = replaceAll(_value, "\r", " "); //有没有去掉空格回车的必要？没必要xml写什么就存什么  要不比较的时候会有麻烦
			_value = _value.replaceAll("\n", " ");
			_value = _value.replaceAll("\t", " ");*/
			return _value; //
		}
	}

	public String getLength(String _length, int add) {
		String returnStr = null;
		String[] strs = _length.split(",");
		if (strs.length > 1) {
			returnStr = (Integer.parseInt(strs[0]) + add) + "";
		} else {
			returnStr = (Integer.parseInt(_length) + add) + "";
		}
		return returnStr;
	}

	public String getLength2(String _length) {
		String returnStr = null;
		String[] strs = _length.split(",");
		if (strs.length > 1) {
			returnStr = _length.substring(_length.indexOf(","));
		} else {
			returnStr = "";
		}
		return returnStr;
	}

	public String replaceAll(String str_par, String old_item, String new_item) {
		String str_return = "";
		String str_remain = str_par;
		boolean bo_1 = true;
		while (bo_1) {
			int li_pos = str_remain.indexOf(old_item);
			if (li_pos < 0) {
				break;
			} // 如果找不到,则返回
			String str_prefix = str_remain.substring(0, li_pos);
			str_return = str_return + str_prefix + new_item; // 将结果字符串加上原来前辍
			str_remain = str_remain.substring(li_pos + old_item.length(), str_remain.length());
		}
		str_return = str_return + str_remain; // 将剩余的加上
		return str_return;
	}

	/**
	 * 根据metadatadmo
	 * @param _tabname
	 * @param _dbtype
	 * @param _sqlviewtype
	 * @return
	 * @throws Exception
	 */
	public String getCreateSQL(String datasourcename, String _tabname, String _dbtype, String _sqlviewtype) throws Exception {
		if (datasourcename == null || datasourcename.trim().equals("")) {
			datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}
		HashVOStruct hvst = new CommDMO().getHashVoStructByDS(datasourcename, "select * from " + _tabname.toUpperCase() + " where 1=2", true, false, null); //
		String[] str_cols = hvst.getHeaderName();
		String[] str_coltypes = hvst.getHeaderTypeName(); //
		int[] li_collength = hvst.getHeaderLength(); //
		int[] precision = hvst.getPrecision();
		int[] scale = hvst.getScale();
		String pkname = hvst.getPkN();
		String pkc = hvst.getPkC();
		HashMap index = hvst.getIndex();
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create table " + _tabname + "\r\n(\r\n");
		for (int i = 0; i < str_coltypes.length; i++) {
			String str_coltype = convertColType(str_coltypes[i], _dbtype); //根据数据库类型取得平台的指定的类型的转换成实际数据库以后的类型!!!
			if (str_cols[i].equalsIgnoreCase("ID")) {
				sb_sql.append(str_cols[i] + " " + str_coltype); //
			} else {
				sb_sql.append(str_cols[i] + " " + str_coltype); //	
			}

			if (str_coltype.equalsIgnoreCase("text") || str_coltype.equalsIgnoreCase("clob")) {
			} else if (str_coltype.equalsIgnoreCase("integer") || str_coltype.equalsIgnoreCase("decimal") || str_coltype.equalsIgnoreCase("number")) {
				sb_sql.append("(" + precision[i] + "," + scale[i] + ")");
			} else {
				sb_sql.append("(" + li_collength[i] + ")");
			}
			sb_sql.append(",\r\n"); //
		}
		if (pkname != null && !"".equals(pkname) && pkc != null && !"".equals(pkname)) {
			sb_sql.append("constraint " + pkname + " primary key (" + pkc + ")\r\n");
		} else {
			sb_sql.delete(sb_sql.lastIndexOf(","), sb_sql.lastIndexOf(",") + 1);
		}
		sb_sql.append(")");
		if ("mysql".equalsIgnoreCase(_dbtype)) {
			sb_sql.append(" DEFAULT CHARSET=GBK");
		}
		sb_sql.append(";\r\n");
		if (index != null && index.size() > 0) {
			String[] indexname = (String[]) index.keySet().toArray(new String[0]);
			for (int i = 0; i < index.size(); i++) {
				sb_sql.append(" create index " + indexname[i] + " on " + _tabname.toUpperCase() + "(" + index.get(indexname[i]) + ");\r\n");
			}
		}
		return sb_sql.toString();
	}

	/**
	 * 转换列的类型!!
	 * @param _oldtype
	 * @param _destdbtype
	 * @return
	 */
	private String convertColType(String _oldtype, String _destdbtype) {
		String str_oldtype = _oldtype.toUpperCase(); // //
		_destdbtype = _destdbtype.toUpperCase(); // //
		if (str_oldtype.startsWith("NUMBER") || str_oldtype.startsWith("DECIMAL") || str_oldtype.startsWith("INTEGER")) { //
			if (_destdbtype.equals(WLTConstants.ORACLE)) {
				return "number"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) {
				return "decimal"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) {
				return "decimal"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) {
				return "integer"; //
			} else {
				return "decimal"; //
			}
		} else if (str_oldtype.startsWith("VARCHAR")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) {
				return "varchar2"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) {
				return "varchar"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) {
				return "varchar"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) {
				return "varchar"; //
			} else {
				return "varchar"; //
			}
		} else if (str_oldtype.startsWith("CHAR") || str_oldtype.startsWith("CHARACTER")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) { //
				return "char"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) { //
				return "character"; //
			} else {
				return "char"; //
			}
		} else if (str_oldtype.startsWith("TEXT") || str_oldtype.startsWith("CLOB")) {
			if (_destdbtype.equals(WLTConstants.ORACLE)) { //
				return "clob"; //
			} else if (_destdbtype.equals(WLTConstants.SQLSERVER)) { //
				return "text"; //
			} else if (_destdbtype.equals(WLTConstants.MYSQL)) { //
				return "text"; //
			} else if (_destdbtype.equals(WLTConstants.DB2)) { //
				return "clob"; //
			} else {
				return "text"; //
			}
		} else {
			return "varchar"; //
		}
	}

	/**
	 * 从指定的XML中得到VIEW的元素列表
	 * @param xmlPath
	 * @return
	 */
	private List<org.jdom.Element> getViewListFromViewXml(String xmlPath) {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		//以前用 URL url = this.getClass().getClassLoader().getResource(xmlPath); 如果xml在class目录下是没有问题，但xml打到jar包中放到lib中是找不到的[【李春娟/2012-03-02】
		Document doc = null;
		try {
			doc = builder.build(this.getClass().getResourceAsStream(xmlPath));
		} catch (Exception e) {
			e.printStackTrace();
			throw new WLTAppException("资源文件[" + xmlPath + "]不存在,请与产品开发组联系!"); //
		}
		java.util.List list_view = doc.getRootElement().getChildren("view");
		return list_view;
	}

	/**
	 * 从xml中解析出所有的视图定义
	 * @param xmlPath
	 * @return
	 */
	private List<ViewDefineInfo> getAllViewDefInfoFromXml(String xmlPath) {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		//以前用 URL url = this.getClass().getClassLoader().getResource(xmlPath); 如果xml在class目录下是没有问题，但xml打到jar包中放到lib中是找不到的[【李春娟/2012-03-02】
		Document doc = null;
		try {
			doc = builder.build(this.getClass().getResourceAsStream(xmlPath));
		} catch (Exception e) {
			e.printStackTrace();
			throw new WLTAppException("资源文件[" + xmlPath + "]不存在,请与产品开发组联系!"); //
		}
		java.util.List list_view = doc.getRootElement().getChildren("view");
		List<ViewDefineInfo> viewList = null;
		if (list_view != null && list_view.size() > 0) {
			viewList = new ArrayList<ViewDefineInfo>();
			for (int i = 0; i < list_view.size(); i++) {
				org.jdom.Element param = (org.jdom.Element) list_view.get(i);
				String str_name = param.getAttributeValue("name");
				String str_desc = param.getAttributeValue("descr");
				String str_sql = param.getChild("sql").getValue();
				ViewDefineInfo viewDefInfo = new ViewDefineInfo();
				viewDefInfo.setName(str_name);
				viewDefInfo.setDescr(str_desc);
				viewDefInfo.setSql(str_sql);
				viewList.add(viewDefInfo);
			}
		}
		return viewList;
	}

	/**
	 * 将XML中定义的视图显示出来
	 * @param title
	 * @param xmlPath
	 * @return
	 */
	public String exportAllXmlViewAsHtml(String title, String xmlPath) {
		//全部试图预览表
		StringBuffer all_view_table = new StringBuffer();
		DataSourceVO[] alldatasources = ServerEnvironment.getInstance().getDataSourceVOs();

		StringBuffer sbdatasource = new StringBuffer();
		if (alldatasources != null) {
			for (int i = 0; i < alldatasources.length; i++) {
				sbdatasource.append("<option value=\"" + alldatasources[i].getName() + "\">" + alldatasources[i].getName() + "</option>");
			}
		}

		all_view_table.append("<form action=\"./state\" method=\"post\" target=\"_blank\">\r\n");
		all_view_table.append("<input id=\"type\" name=\"type\" type=\"hidden\" value=\"compareallview\" />\r\n");
		all_view_table.append("<input id=\"xmlPath\" name=\"xmlPath\" type=\"hidden\" value=\"" + xmlPath + "\" />\r\n");
		all_view_table.append("将XML定义与数据源实际结构比较 比较的数据源：");
		all_view_table.append("<select id=\"datasourcename\" name=\"datasourcename\" >");
		for (int j = 0; j < alldatasources.length; j++) {
			all_view_table.append("<option value=\"" + URLEncoder.encode(alldatasources[j].getName()) + "\">" + alldatasources[j].getName() + "</option>");
		}
		all_view_table.append("</select><input type=\"submit\" value =\"执行\"></form>");

		//单个试图内容
		StringBuffer single_view_table = new StringBuffer();

		java.util.List list_view = getViewListFromViewXml(xmlPath);
		all_view_table.append("<table border='1' width=70% cellpadding='5' bordercolor='#888888'>");
		//全部试图预览表--标题
		all_view_table.append("<tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>" + title + "</a></td></tr>");

		List<ViewDefineInfo> viewList = getAllViewDefInfoFromXml(xmlPath);
		//遍历XML中所有视图
		for (int i = 0; i < viewList.size(); i++) {
			ViewDefineInfo viewDefineInfo = viewList.get(i);
			String str_tname = viewDefineInfo.getName();
			String str_tdesc = viewDefineInfo.getDescr();
			String str_tsql = viewDefineInfo.getSql();

			if (i == 0) {
				all_view_table.append("<tr><td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			} else if (i % 5 == 0) {
				all_view_table.append("</tr>\r\n<tr><td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			} else {
				all_view_table.append("<td align=\"center\">" + str_tdesc + "<br><a href='#" + str_tname + "'><font  color=\"blue\">" + str_tname + "</font></a></td>\r\n");
			}
			single_view_table.append("<a name='" + str_tname + "'></a><br/><br/>");
			single_view_table.append("<hr>");
			single_view_table.append("<a href ='#title'><font  color=\"blue\">页首</font></a>\r\n<br><br>    ");

			//单个试图内容--标题
			single_view_table.append("<a >" + "视图名称:" + str_tname + "</a><br/><br/>");
			single_view_table.append("描述:" + str_tdesc);

			//单个试图内容--XML定义与数据源实际结构比较
			single_view_table.append("<form action=\"./state\" method=\"post\" target=\"_blank\">\r\n");
			single_view_table.append("<input id=\"type\" name=\"type\" type=\"hidden\" value=\"compareoneview\" >\r\n");
			single_view_table.append("<input id=\"viewname\" name=\"viewname\" type=\"hidden\" value=\"" + str_tname + "\" >\r\n");
			single_view_table.append("<input id=\"viewdescr\" name=\"viewdescr\" type=\"hidden\" value=\"" + (URLEncoder.encode(str_tdesc)) + "\" >\r\n");
			single_view_table.append("<input id=\"viewddl\" name=\"viewddl\" type=\"hidden\" value=\"" + (URLEncoder.encode(str_tsql)) + "\" >");
			single_view_table.append("<input id=\"appTitleName\" name=\"appTitleName\" type=\"hidden\" value=\"" + title + "\" >");
			single_view_table.append("将XML定义与数据源实际结构比较 比较的数据源：");
			single_view_table.append("<form><select id=\"datasourcename\" name=\"datasourcename\">");
			for (int j = 0; j < alldatasources.length; j++) {
				single_view_table.append("<option value=\"" + alldatasources[j].getName() + "\">" + alldatasources[j].getName() + "</option>");
			}
			single_view_table.append("</select><input type=\"submit\" value =\"执行\"></form>");

			single_view_table.append("<table width=50%  border='1'cellpadding='5' bordercolor='#888888'>");

			//单个试图内容--表头
			single_view_table.append("<tr bgcolor='#eeeeee'>");
			single_view_table.append("<td>SQL语句</td>");
			single_view_table.append("</tr>");

			//单个试图内容--表内容
			String sql = str_tsql;
			sql = sql.replaceAll("\n", "</br>");
			single_view_table.append("<tr><td>" + sql + "</td></tr>\r\n");

			//单个试图内容--表尾
			single_view_table.append("</table><br>");

			single_view_table.append("<hr>");
		}
		all_view_table.append("</table></br>");

		StringBuffer sb_html = new StringBuffer();
		//html头，style定义......
		sb_html.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		sb_html.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		sb_html.append("</head>\r\n<body>\r\n");
		//html内容
		sb_html.append(all_view_table);//视图预览表
		sb_html.append("<hr>");//分界线
		sb_html.append(single_view_table);////单个试图内容表
		//html尾部
		sb_html.append("</body></html>");

		return sb_html.toString();
	}

	/**
	 * 视图比较页面头
	 * @return
	 */
	private String getCompareViewSqlHtmlHeader() {
		StringBuilder sb_html_header = new StringBuilder();
		sb_html_header.append("<html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		sb_html_header.append("<style>\r\ntable{\r\nborder-collapse:collapse;FONT-SIZE: 13px;\r\n}\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		sb_html_header.append("<script type=\"text/javascript\">function myconfirm(sql,datesourcename){" + "  if(confirm(\"您确定执行吗\")){" + "window.open('./state?type=comitsql&sql='+sql+'&datesourcename='+datesourcename,'','','');" + "}" + "}</script>");
		sb_html_header.append("</head>\r\n<body>\r\n");
		return sb_html_header.toString();
	}

	/**
	 * 视图比较页面尾
	 * @return
	 */
	private String getCompareViewSqlHtmlFooter() {
		return "</body></html>";
	}

	/**
	 * 单个视图比较页面
	 * @param datasourcename
	 * @param viewname
	 * @param viewddlxml
	 * @param appTitleName
	 * @param viewdescr 
	 * @return
	 */
	public String getCompareOneViewHtml(String datasourcename, String viewname, String viewddlxml, String appTitleName, String viewdescr) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getCompareViewSqlHtmlHeader());
		sb.append(this.exportCompareOneViewSqlAsHtml(datasourcename, viewname, viewddlxml, appTitleName, viewdescr));
		sb.append(this.getCompareViewSqlHtmlFooter());
		return sb.toString();
	}

	/**
	 * XML中所有视图与数据库中比较
	 * @param datasourcename
	 * @param xmlPath
	 * @return
	 */
	public String getCompareAllViewHtml(String datasourcename, String xmlPath) {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getCompareViewSqlHtmlHeader());
		List<ViewDefineInfo> list = this.getAllViewDefInfoFromXml(xmlPath);
		for (int i = 0; i < list.size(); i++) {
			ViewDefineInfo view = list.get(i);
			sb.append(this.exportCompareOneViewSqlAsHtml(datasourcename, view.getName(), view.getSql(), "", view.getDescr()));
		}
		sb.append(this.getCompareViewSqlHtmlFooter());
		return sb.toString();
	}

	/**
	 * XML中单个试图与数据库中比较
	 * @param datasourcename
	 * @param viewname
	 * @param viewdescr 
	 * @param xmlpath
	 * @return
	 */
	private String exportCompareOneViewSqlAsHtml(String datasourcename, String viewname, String viewddlxml, String viewdescr) {
		return exportCompareOneViewSqlAsHtml(datasourcename, viewname, viewddlxml, "", viewdescr);
	}

	private String exportCompareOneViewSqlAsHtml(String datasourcename, String viewname, String viewddlxml, String appTitleName, String viewdescr) {
		viewddlxml = trimViewCreateSQL(viewddlxml);
		StringBuffer str_table = new StringBuffer();
		str_table.append("<hr/>");
		str_table.append("<font size=10>视图名：<a name='" + viewname + "' >" + viewname + "</a></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
		str_table.append("<br/><br/>");
		str_table.append("描述：<a name='" + viewdescr + "' >" + viewdescr + "</a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;\r\n");
		str_table.append("<table width=50% border='1'cellpadding='5' bordercolor='#888888'><tr bgcolor='#eeeeee'><td align='center'colspan='5' ><a name='title'>" + appTitleName + "与数据库[" + datasourcename + "]比较</a></td></tr>");
		str_table.append("&nbsp;&nbsp;&nbsp;&nbsp;\r\n<br><br>");
		//ddl比较
		str_table.append("1.DDL比较");
		str_table.append("&nbsp;&nbsp;&nbsp;&nbsp;\r\n<br><br>");
		str_table.append("<table width=50% border='1'cellpadding='5' bordercolor='#888888'>\r\n");
		//两次encode可以保证字符串原样恢复，不会有中文乱码问题，还可以避免单引号嵌套问题
		str_table.append("<tr bgcolor='#eeeeee'><td width=50%>xml中视图DDL&nbsp;<a style=\"cursor: hand;\"  onclick=\"myconfirm('" + URLEncoder.encode(URLEncoder.encode((viewddlxml))) + "','" + datasourcename + "')\"><font color=\"blue\">执行</font></a></td><td width=50%>数据库中视图DDL</td>\r\n</tr>\r\n");
		String viewDBDDL = null;
		try {
			viewDBDDL = getViewDDLFromDb(datasourcename, viewname);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		str_table.append("<tr>");
		str_table.append("<td>");

		//sql显示
		str_table.append(sqlFormatToHtml(viewddlxml));
		str_table.append("</td>");
		str_table.append("<td>");
		str_table.append((viewDBDDL != null && (viewDBDDL.trim().length() > 0)) ? sqlFormatToHtml(viewDBDDL) : "数据库中未定义该试图");
		str_table.append("</td>");
		str_table.append("</tr>");

		str_table.append("</table><br>\r\n");
		str_table.append("</tr></table><br>\r\n");
		boolean isEqual = true;
		//如果数据库中已经有该视图，则进一步比较试图的所有查询字段
		if (viewDBDDL != null && viewDBDDL.trim().length() > 0) {
			str_table.append("<br/>");
			str_table.append("2.字段比较(*字体为红色的字段表示在对方没有出现)");
			str_table.append("&nbsp;&nbsp;&nbsp;&nbsp;\r\n<br><br>");
			String xmlSQL = viewddlxml.substring(viewddlxml.indexOf(" as") + 3);
			String dbSQL = "select * from " + viewname + " where 1=2";
			String[] xmlViewHeaders = null;
			String[] dbViewHeaders = null;
			try {
				dbViewHeaders = getCommDMO().getTableDataStructByDS(datasourcename, dbSQL, false).getHeaderName();
			} catch (Exception e) {
				//e.printStackTrace();
				str_table.append("<br/><font color=\"red\">");
				str_table.append("在数据源" + datasourcename + "上执行SQL语句时出错,SQL语句为:<br/>" + dbSQL);
				str_table.append("</font><br/>");
			}

			try {
				xmlViewHeaders = getCommDMO().getTableDataStructByDS(datasourcename, xmlSQL, false).getHeaderName();
			} catch (Exception e) {
				//e.printStackTrace();
				str_table.append("<br/><font color=\"red\">");
				str_table.append("在数据源" + datasourcename + "上执行SQL语句时出错,SQL语句为:<br/>" + xmlSQL);
				str_table.append("</font><br/>");
			}

			if (xmlViewHeaders != null && xmlViewHeaders.length > 0) {
				//xml视图字段表
				str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'>\r\n");
				str_table.append("<tr ><td align=\"left\" colspan=" + xmlViewHeaders.length + ">xml中视图字段列表</td>\r\n</tr>\r\n");
				str_table.append("<tr bgcolor='#eeeeee'>");
				for (int i = 0; i < xmlViewHeaders.length; i++) {
					if (!isInArray(xmlViewHeaders[i], dbViewHeaders)) {
						//如果此字段不在数据库视图字段表里，则字体置红
						str_table.append("<td > <font color=\"red\">" + xmlViewHeaders[i] + "</font></td>\r\n");
						isEqual = false;
					} else {
						str_table.append("<td >" + xmlViewHeaders[i] + "</td>\r\n");
					}
				}
				str_table.append("</tr>\r\n");
				str_table.append("</table>");
				str_table.append("<br>");
			}
			if (dbViewHeaders != null && dbViewHeaders.length > 0) {
				//数据库视图字段表
				str_table.append("<table border='1'cellpadding='5' bordercolor='#888888'>\r\n");
				str_table.append("<tr ><td align=\"left\" colspan=" + dbViewHeaders.length + ">数据库中视图字段列表</td>\r\n</tr>\r\n");
				str_table.append("<tr bgcolor='#eeeeee'>");
				for (int i = 0; i < dbViewHeaders.length; i++) {
					//如果此字段不在xml视图字段表里，则字体置红
					if (!isInArray(dbViewHeaders[i], xmlViewHeaders)) {
						str_table.append("<td > <font color=\"red\">" + dbViewHeaders[i] + "</font></td>\r\n");
						isEqual = false;
					} else {
						str_table.append("<td >" + dbViewHeaders[i] + "</td>\r\n");
					}
				}
				str_table.append("</tr>\r\n");
				str_table.append("</table>");
			}
			//str_table.append("&<br>");
			if (isEqual) {
				str_table.append("<font color=\"blue\"><a>字段比较结果：完全一致</a></font>");
			} else {
				str_table.append("<font color=\"red\"><a>字段比较结果：不一致,请查看上面的字段对比表</a></font>");
			}
		}

		return str_table.toString();
	}

	/**
	 * 判断一个元素是否在一个数组里面
	 * @param obj
	 * @param objArr
	 * @return
	 */
	private boolean isInArray(Object obj, Object[] objArr) {

		for (int i = 0; (objArr != null) && (i < objArr.length); i++) {
			if (objArr[i].equals(obj)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 得到数据库中试图的DDL
	 * @param _sourcedsname
	 * @param _viewname
	 * @return
	 * @throws Exception
	 */
	public String getViewDDLFromDb(String _sourcedsname, String _viewname) throws Exception {
		if (_sourcedsname == null) {
			_sourcedsname = ServerEnvironment.getDefaultDataSourceName();
		}
		String str_sourcedbtype = ServerEnvironment.getInstance().getDataSourceVO(_sourcedsname).getDbtype();
		String str_viewdefine = null;
		if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.SQLSERVER)) { // SQLServer
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select view_definition from INFORMATION_SCHEMA.VIEWS  where table_name='" + _viewname + "'"); //
			if (hvs.length > 0) {
				str_viewdefine = hvs[0].getStringValue("view_definition"); //
			}
		} else if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.ORACLE)) { // Oracle
			str_viewdefine = getCommDMO().getStringValueByDS(_sourcedsname, "select text from user_views where view_name = '" + _viewname.toUpperCase() + "'");

		} else if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.MYSQL)) { // Mysql
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select view_definition,table_schema from INFORMATION_SCHEMA.VIEWS  where table_name='" + _viewname + "'"); //
			if (hvs.length > 0) {
				str_viewdefine = hvs[0].getStringValue("view_definition"); //
			}
		} else if (str_sourcedbtype.equalsIgnoreCase(WLTConstants.DB2)) { // DB2
			HashVO[] hvs = getCommDMO().getHashVoArrayByDS(_sourcedsname, "select view_definition  from  sysibm.views  where table_name='" + _viewname.toUpperCase() + "'"); //
			if (hvs.length > 0) {
				str_viewdefine = hvs[0].getStringValue("view_definition"); //
			}
		}
		return str_viewdefine;
	}

	private String sqlFormatToHtml(String sql) {
		return sql.replace(",", ",<br/>").replace("from", " from <br/> ");
	}

	/**
	 * 将SQL中多余的空格,换行,TAB去掉
	 * @param sql
	 * @return
	 */
	private String trimViewCreateSQL(String sql) {
		sql = sql.replace(";", " ");
		sql = sql.replace("\r", " ");
		sql = sql.replace("\n", " ");
		sql = sql.replace("\r\n", " ");
		sql = sql.replace((char) 9, ' ');//TAB键
		while (sql.contains("  ")) {
			sql = sql.replace("  ", " ");
		}
		return sql;
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			synchronized (this) {
				if (commDMO == null)
					commDMO = new CommDMO(); //
			}
		}
		return commDMO; //
	}

	private class ViewDefineInfo {
		private String name;
		private String descr;
		private String sql;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getDescr() {
			return descr;
		}

		public void setDescr(String descr) {
			this.descr = descr;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}
	}

	//创建索引SQL Gwang 2013-08-27 
	public String exportCreateIndexSqlAsHtml(String xmlPath) {

		StringBuffer sqlsAsHtml = new StringBuffer();
		sqlsAsHtml.append(" ");
		sqlsAsHtml.append(" <html>\r\n<head>\r\n<meta http-equiv=\"Content-Language\" content=\"zh-cn\">\r\n<meta http-equiv=\"Content-Type\" content=\"text/html; charset=gb2312\">\r\n");
		sqlsAsHtml.append(" <style>\r\nbody{\r\nFONT-SIZE: 13px;\r\n}\r\n</style>");
		sqlsAsHtml.append(" </head>\r\n<body>\r\n");
		// 导出全部表
		String[] installSqls = null;
		try {
			installSqls = getAllCreateIndexSQL(xmlPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (installSqls != null) {
			StringBuffer content = new StringBuffer();
			for (int i = 0; i < installSqls.length; i++) {
				content.append(installSqls[i] + "<br>");
			}
			sqlsAsHtml.append(new TBUtil().replaceAll(content.toString(), "\r\n", "\r\n<br>"));
		}

		sqlsAsHtml.append(" </body></html>");
		return sqlsAsHtml.toString();
	}

	//创建索引SQL Gwang 2013-08-27 
	public String[] getAllCreateIndexSQL(String xmlPath) throws Exception {
		org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder(); //
		org.jdom.Document doc = builder.build(this.getClass().getResourceAsStream(xmlPath)); // 加载XML
		java.util.List list_tables = doc.getRootElement().getChildren("table");
		List rl = new ArrayList();
		String str_tpk = null;
		for (int i = 0; i < list_tables.size(); i++) {
			try {
				org.jdom.Element param = (org.jdom.Element) list_tables.get(i);
				String str_tname = param.getAttributeValue("name"); // 表名
				StringBuilder sb_sql = new StringBuilder(); //

				java.util.List list_indexs = param.getChildren("indexs");
				if (list_indexs != null && list_indexs.size() > 0) {
					java.util.List list_indexss = ((org.jdom.Element) list_indexs.get(0)).getChildren("index");
					if (list_indexss != null && list_indexss.size() > 0) {
						List indexall = new ArrayList();
						for (int jjj = 0; jjj < list_indexss.size(); jjj++) {
							org.jdom.Element index = (org.jdom.Element) list_indexss.get(jjj);
							sb_sql.append(" create index " + index.getAttributeValue("name") + " on " + str_tname + "(" + index.getAttributeValue("cols") + ");\r\n");
						}
					}
				}
				rl.add(sb_sql.toString());
			} catch (Exception ex) {
				ex.printStackTrace(); //
				continue;
			}
		}
		if (rl != null && rl.size() > 0) {
			return (String[]) rl.toArray(new String[0]);
		}
		return null;
	}
}
