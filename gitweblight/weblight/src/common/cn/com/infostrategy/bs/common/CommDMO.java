package cn.com.infostrategy.bs.common;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.apache.log4j.Logger;

import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.LinkForeignTableDefineVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.SQLBuilderIfc;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.TableDataStruct;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

public class CommDMO extends AbstractDMO {

	// public static Logger logger = WLTLogger.getLogger(CommDMO.class); //

	Logger logger = WLTLogger.getLogger(CommDMO.class); //
	private ClobUtil pushClob = null;
	int limitcount = TBUtil.getTBUtil().getSysOptionIntegerValue("列表最大查询条数", 2000); //太平项目提出需要导出列表，但因分页就只能查询出2000记录，故需要设置一个参数，否则应用服务启动报错【李春娟/2017-09-26】

	public CommDMO() {
	}

	public String[] getAllSysTables(String _datasourcename, String _tableNamePattern) throws Exception {
		String[][] str_tabs = getAllSysTableAndDescr(_datasourcename, _tableNamePattern, false, false); //
		String[] str_return = new String[str_tabs.length]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = str_tabs[i][0]; //
		}
		return str_return; //
	}

	/**
	 * 返回所有系统表
	 * @return
	 * @throws Exception
	 */
	public String[][] getAllSysTableAndDescr(String _datasourcename, String _tableNamePattern, boolean _isContainView, boolean _isGetDescrFromXML) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName(); //
		}
		ResultSet resultSet = null; //结果集!!
		try {
			WLTDBConnection conn = getConn(_datasourcename); ////
			DatabaseMetaData dbmd = conn.getMetaData(); //取得整个数据库的原数据定义!
			//dbmd.getPrimaryKeys(null, null, null);  //得到表的主键信息!!!反向推算出主键,是非常重要的事情!!! 经对Mysql,Oracle,SQLServer反复测试
			//dbmd.getIndexInfo(catalog, schema, table, unique, approximate);  //得到表的索引信息,以后反向从实际表找出索引,然后与XML差异化分析,是一个非常重要的事情
			String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getDbtype(); //数据库类型
			String str_schema = null;
			if (str_dbtype.equalsIgnoreCase("ORACLE") || str_dbtype.equalsIgnoreCase("MYSQL")) { //如果是ORACLE或MYSQL则还要指定Schema名称!!!SQLSERVSER则不能指定!!
				str_schema = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getUser();
			} else if (str_dbtype.equalsIgnoreCase("DB2")) {//增加DB2数据库的判断【李春娟/2015-12-24】
				str_schema = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getDburl();
				if (str_schema != null) {
					str_schema = str_schema.toLowerCase().trim();
					if (str_schema.contains("currentschema=")) {
						str_schema = str_schema.substring(str_schema.indexOf("currentschema=") + 14);
						if (str_schema.endsWith(";")) {
							str_schema = str_schema.substring(0, str_schema.length() - 1);
						}
					} else {
						str_schema = null;
					}
				}
			}
			resultSet = dbmd.getTables(null, (str_schema == null ? null : str_schema.toUpperCase()), (_tableNamePattern == null ? "%" : _tableNamePattern), (_isContainView ? new String[] { "TABLE", "VIEW" } : new String[] { "TABLE" }));
			ArrayList al_tnames = new ArrayList(); //创建对象..
			while (resultSet.next()) {
				HashVO hvo = new HashVO(); //
				//String tableCatalog = resultSet.getString(1); //存储目录名
				//String tableSchema = resultSet.getString(2); //存储的Schema名
				String str_tableName = resultSet.getString("TABLE_NAME"); //表名
				if (str_tableName.toUpperCase().startsWith("BIN$")) {//oracle中删除的表存在回收站，表名以"BIN$"开头，不需要导出，清空回收站可执行一下 purge recyclebin【李春娟/2012-08-24】
					continue;
				}
				String str_tabType = resultSet.getString("TABLE_TYPE"); //
				String str_tabRemark = resultSet.getString("REMARKS"); //
				hvo.setAttributeValue("tabname", str_tableName); //
				hvo.setAttributeValue("tabtype", str_tabType); //
				hvo.setAttributeValue("tabdescr", str_tabRemark); //
				al_tnames.add(hvo); //
			}
			HashVO[] hvs = (HashVO[]) al_tnames.toArray(new HashVO[0]); //
			new TBUtil().sortHashVOs(hvs, new String[][] { { "tabtype", "N", "N" }, { "tabname", "N", "N" } }); //排序一把!
			String[][] str_returns = new String[hvs.length][3]; //
			for (int i = 0; i < str_returns.length; i++) {
				str_returns[i][0] = hvs[i].getStringValue("tabname"); //
				str_returns[i][1] = hvs[i].getStringValue("tabtype"); //
				str_returns[i][2] = hvs[i].getStringValue("tabdescr"); //
			}

			if (_isGetDescrFromXML) { //如果需要从XML中取得备注!
				String[][] str_tabdesc = new InstallDMO().getAllIntallTablesDescr(); //
				HashMap map_nameDescr = new HashMap(); //
				for (int i = 0; i < str_tabdesc.length; i++) {
					map_nameDescr.put(str_tabdesc[i][0].toUpperCase(), str_tabdesc[i][1]); //
				}
				for (int i = 0; i < str_returns.length; i++) {
					if (str_returns[i][2] == null || str_returns[i][2].trim().equals("")) {
						if (map_nameDescr.containsKey(str_returns[i][0].toUpperCase())) { //
							str_returns[i][2] = (String) map_nameDescr.get(str_returns[i][0].toUpperCase()); //设置一下
						}
					}
				}
			}
			return str_returns; // str_tnames; //返回!!!
		} catch (Exception ex) {
			throw new Exception("在数据源[" + _datasourcename + "]上取所有系统表名时失败,原因:" + ex.getMessage());
		} finally {
			try {
				if (resultSet != null) {
					resultSet.close(); //
				}
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 直接返回一个字符串
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String getStringValueByDS(String _datasourcename, String _sql) throws Exception {
		String[][] str_data = getStringArrayByDS(_datasourcename, _sql); //
		if (str_data != null && str_data.length > 0 && str_data[0] != null) {
			return str_data[0][0]; //
		} else {
			return null; //
		}
	}

	/**
	 * 根据SQL,快速返回一个hashMap,就是拿该SQL的第一列作为key，第2列作为Value。
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql) throws Exception {
		return getHashMapBySQLByDS(_datasourcename, _sql, false);
	}

	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql, boolean _appendSameKey) throws Exception {
		return getHashMapBySQLByDS(_datasourcename, _sql, _appendSameKey, false, false);
	}

	/**
	 * 根据一个SQL语句返回一个HashMap,该SQL至少有两列以上,然后第一列作为hashMap的key,第2列作为HashMap的Value
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public HashMap getHashMapBySQLByDS(String _datasourcename, String _sql, boolean _appendSameKey, boolean _isAllKeyToLower, boolean _isAllValueToLower) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName();
		}

		HashMap map = new HashMap(); //
		String[][] str_data = getStringArrayByDS(_datasourcename, _sql); //
		if (_isAllKeyToLower || _isAllValueToLower) { //如果全部转换成小写
			for (int i = 0; i < str_data.length; i++) {
				if (_isAllKeyToLower) { //如果所有Key都转小写
					if (str_data[i][0] != null) {
						str_data[i][0] = str_data[i][0].toLowerCase(); //
					}
				}
				if (_isAllValueToLower) { //如果所有Value都转小写!
					if (str_data[i][1] != null) {
						str_data[i][1] = str_data[i][1].toLowerCase(); //
					}
				}
			}
		}
		String str_temp = null;
		for (int i = 0; i < str_data.length; i++) {
			if (_appendSameKey) { //如果增加相同的值
				if (map.containsKey(str_data[i][0])) { //如果包含某个key
					str_temp = (String) map.get(str_data[i][0]); //
					if (str_temp != null) {
						map.put(str_data[i][0], str_temp + ";" + str_data[i][1]); //在屁股后面继续增加
					}
				} else {
					map.put(str_data[i][0], str_data[i][1]); //向哈希表中塞入...
				}
			} else {
				map.put(str_data[i][0], str_data[i][1]); //向哈希表中塞入...
			}
		}
		return map;
	}

	/**
	 * 取得一维数组,将只取SQL语句中的第一列!
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public String[] getStringArrayFirstColByDS(String _datasourcename, String _sql) throws Exception {
		String[][] str_data = getStringArrayByDS(_datasourcename, _sql); //
		String[] str_return = new String[str_data.length]; //
		for (int i = 0; i < str_return.length; i++) {
			str_return[i] = str_data[i][0];
		}
		return str_return; //
	}

	public String[][] getStringArrayByDS(String _datasourcename, String _sql) throws Exception {
		return getStringArrayByDS(_datasourcename, _sql, true); //
	}

	public String[][] getStringArrayByDS(String _datasourcename, String _sql, boolean _isDebugLog) throws Exception {
		return getStringArrayByDS(_datasourcename, _sql, true, true);
	}

	/**
	 * 取得二维数组对象!!
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _isDebugLog
	 * @param _getClob  是否进行转换，根据“pushclob:” 标志找到对应pub_clob 里的值，默认转换。因在ClobUtil更新clob数据时需要获得旧的“pushclob:”值并删除掉，故这里新增参数【李春娟/2018-08-11】
	 * @return
	 * @throws Exception
	 */
	public String[][] getStringArrayByDS(String _datasourcename, String _sql, boolean _isDebugLog, boolean _getClob) throws Exception {
		WLTDBConnection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		String[][] str_data = null;
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}
			StringBuilder sb_sqlTrace = new StringBuilder(_sql); //
			if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder在调用,即在使用LoadRunder测试时,是不做这个逻辑的!!
				addSQLToSessionSQLListener(_sql); //
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) {
					sb_sqlTrace.append("," + getCurrThreadTrace()); //
				}
			}
			conn = getConn(_datasourcename); //取得连接
			long ll_rs_1 = System.currentTimeMillis(); //
			boolean isPrePareSQL = "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")); //
			String str_preSQL = null; //
			ArrayList al_pres = null; //
			if (isPrePareSQL) {
				al_pres = new PrepareSQLUtil().prepareSQL(_sql); //
				if (al_pres == null || al_pres.size() <= 1) {
					isPrePareSQL = false; //
				} else {
					str_preSQL = (String) al_pres.get(0); //预编译转换后的SQL
					sb_sqlTrace.append("\n经过预编译转换SQL为[" + str_preSQL + "],参数分别是:"); //
					for (int i = 1; i < al_pres.size(); i++) { ////
						sb_sqlTrace.append("[" + i + "]=[" + al_pres.get(i) + "]"); //
						if (i != al_pres.size() - 1) {
							sb_sqlTrace.append(","); //
						}
					}
				}
			}
			if (_isDebugLog) {
				logger.debug("取数SQL*S:" + sb_sqlTrace.toString()); //
			}
			if (isPrePareSQL) { //如果是真正进行预编译!!!
				stmt = conn.prepareStatement(str_preSQL); //预编译!!!
				for (int i = 1; i < al_pres.size(); i++) {
					((PreparedStatement) stmt).setObject(i, al_pres.get(i)); //
				}
				rs = ((PreparedStatement) stmt).executeQuery(); //
			} else {
				stmt = conn.createStatement(); //创建游标!
				rs = stmt.executeQuery(_sql); //要出错就在这里!!!要慢也慢在这里!!!	
			}

			long ll_rs_2 = System.currentTimeMillis(); //
			ResultSetMetaData rsmd = rs.getMetaData();
			int li_columncount = rsmd.getColumnCount(); //共有几列!!
			ArrayList al_rowData = new ArrayList();
			int li_rows = 0;
			//int li_warnrowcount = 2000; //
			long ll_allDataSize = 0; //记录数据的大小,用于性能监控!!!
			while (rs.next()) {
				String[] str_row = new String[li_columncount];
				for (int i = 1; i <= li_columncount; i++) {
					String str_cell = _getClob ? getClobUtil().getClob(rs.getString(i)) : rs.getString(i); //【李春娟/2018-08-11】
					if (str_cell == null) { //如果是空值,自动转空串,这是因为许多地方输出时都有"null"字样,为了用的时候方便,但这样一改导致许多地方根据空值判断的都要加上同时根据空串判断了!
						str_cell = "";
					}
					str_row[i - 1] = str_cell;
					if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder在调用,即在使用LoadRunder测试时,是不做这个逻辑的!!
						ll_allDataSize = ll_allDataSize + str_cell.length(); //累加!
					}
				}
				al_rowData.add(str_row); //
				li_rows++;
			}
			int li_rowcount = al_rowData.size(); //
			str_data = new String[li_rowcount][li_columncount];
			for (int i = 0; i < li_rowcount; i++) {
				String[] str_rowdata = (String[]) al_rowData.get(i);
				str_data[i] = str_rowdata;
			}
			long ll_rs_3 = System.currentTimeMillis(); //
			if (_isDebugLog) {
				if (ll_rs_3 - ll_rs_2 > 50 || ll_rs_2 - ll_rs_1 > 50) { //如果超过50毫秒,则警告!!!
					logger.warn("取数SQL*S(DB耗时" + (ll_rs_2 - ll_rs_1) + ",Web耗时" + (ll_rs_3 - ll_rs_2) + "):入参数SQL:" + _sql); //
				}
			}
			if (!ServerEnvironment.isLoadRunderCall && ll_allDataSize > 0) {
				new WLTInitContext().setCurrSessionCustInfoByKey("取数大小", new String[] { "(getStrArray[" + li_rows + "])" + _sql, "" + ll_allDataSize }); //
			}
			return str_data;
		} catch (SQLException ex) { //
			throw new SQLException("在数据源[" + _datasourcename + "]上执行SQL[" + _sql + "]出错,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace(); //
			throw new Exception("在数据源[" + _datasourcename + "]上执行SQL[" + _sql + "]出错,原因:" + ex.getMessage());
		} finally {
			try {
				rs.close();
			} catch (Exception exx) {
			}
			try {
				stmt.close();
			} catch (Exception exx) {
			}
		}
	}

	/**
	 * 取得In条件..
	 * @return
	 */
	public String getInCondition(String _datasourcename, String _sql) throws Exception {
		String[] str_data = getStringArrayFirstColByDS(_datasourcename, _sql); //
		if (str_data == null || str_data.length == 0) {
			return "'-99999'"; //
		} else {
			return getSubSQLFromTempSQLTableByIDs(str_data); //
		}
	}

	/**
	 * 根据一组Id[]去送入一个临时表中,然后拼成一个子查询语句返回
	 * @param _ids
	 * @return  "select ids from pub_sqlins where bno='345'", 然后在别的地方用到,比如 select * from law_innerrules where createcorp in (select ids from pub_sqlins where bno='345')
	 */
	public String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception {
		//拼出一批SQL,快速提交,否则也容易死锁!
		long ll_1 = System.currentTimeMillis(); //
		if (_ids == null || _ids.length <= 0) { //
			return "'-99999'"; //直接返回
		}
		String[] str_ids = new TBUtil().distinctStrArray(_ids); //进行唯一性过滤!!!因为如果有重复的,则会浪费性能!!	
		if (str_ids == null || str_ids.length <= 0) { //曾经在兴业中遇到一个变态的情况是_ids就一项,而且是空字符串,然后经过唯一性合并后,长度为0了!,结果SQL条件返回就是个空串!!!所以必须还要有这个判断!!【xch/2012-04-28】
			return "'-99999'"; //直接返回
		}
		if (str_ids.length <= 999) { //如果在999个以内,则直接拼起来返回!!!!!!即不会太长了,这样兼容直接in的情况!!!
			StringBuffer sb_alltext = new StringBuffer(); //
			for (int i = 0; i < str_ids.length; i++) { ////
				sb_alltext.append("'" + (str_ids[i] == null ? "" : str_ids[i]) + "'"); ////
				if (i != str_ids.length - 1) { //如果不是最后一个则加上逗号!!!!!
					sb_alltext.append(","); //如果不是最后一个,则加逗号!!!!!
				}
			}
			return sb_alltext.toString(); //
		}

		//删除1小时前的数据,立即提交,否则容易死锁!!!
		int li_deletepreMinutes = 60; //删除60分钟之前的数据!!!
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMddHHmm", Locale.SIMPLIFIED_CHINESE);
		long ll_currtime = System.currentTimeMillis();
		String str_currhour = sdf_curr.format(new Date(ll_currtime)); //
		String str_pre2hours = sdf_curr.format(ll_currtime - (li_deletepreMinutes * 60 * 1000)); //删除N分钟之前的数据,如果现在是 05-06-12:59 返回的是 05-06 11:59
		String str_delsql = "delete from pub_sqlins where createtime<=" + str_pre2hours; //
		//System.out.println("删除的SQL:" + str_delsql); ////
		executeUpdateByDSImmediately(null, str_delsql); //立即删除1小时之前的数据!按道理只要在每小时的交接点的时候才会真正删除到数据!!!而且会被第一个人删除掉!

		//预先取得BNO
		String str_newbatchno = getSequenceNextValByDS(null, "S_PUB_SQLINS_BNO"); //先取得新的批号!!
		int li_onerowcount = 30; //一行有多少个,现在是20,以后根据性能需要可能会变成30,50,甚至100
		int li_cycles = str_ids.length / li_onerowcount; //几次循环,如果小于20个则返回0,即不执行，直接处理余数!!
		int li_lefts = str_ids.length % li_onerowcount; //余数,即剩下的!!紧随后一次处理
		ArrayList al_sqls = new ArrayList(); //
		StringBuffer sb_sql = null;
		String str_itemValue = null;
		for (int i = 0; i < li_cycles; i++) { //遍历循环!!!
			sb_sql = new StringBuffer(); //
			sb_sql.append("insert into pub_sqlins values (" + str_newbatchno + "," + str_currhour + ","); //
			for (int j = 0; j < li_onerowcount; j++) { //20个列
				str_itemValue = str_ids[i * li_onerowcount + j];
				if (str_itemValue == null) { //可能为空!!
					sb_sql.append("null");
				} else {
					sb_sql.append("'" + str_itemValue + "'");
				}
				if (j != li_onerowcount - 1) {
					sb_sql.append(","); //如果不是最后一个,则加上逗号!!
				}
			}
			sb_sql.append(")"); //
			al_sqls.add(sb_sql.toString());
		}

		if (li_lefts != 0) { //如果有余数,则插入余数!!这里要将列名显示标注,因为可能个数不是正好20个!!!
			sb_sql = new StringBuffer(); //
			sb_sql.append("insert into pub_sqlins (bno,createtime,");
			for (int j = 0; j < li_lefts; j++) {
				sb_sql.append("id" + (j + 1)); //
				if (j != li_lefts - 1) {
					sb_sql.append(","); //如果不是最后一个,则加上逗号!!
				}
			}
			sb_sql.append(") values ("); //
			sb_sql.append(str_newbatchno + "," + str_currhour + ","); //
			for (int j = 0; j < li_lefts; j++) { //遍历剩下的
				str_itemValue = str_ids[li_cycles * li_onerowcount + j]; //////
				if (str_itemValue == null) {
					sb_sql.append("null");
				} else {
					sb_sql.append("'" + str_itemValue + "'");
				}
				if (j != li_lefts - 1) {
					sb_sql.append(","); //如果不是最后一个,则加上逗号!!
				}
			}
			sb_sql.append(")"); //
			al_sqls.add(sb_sql.toString());
		}
		executeBatchByDSImmediately(null, al_sqls); //立即插入,即不参与事务!!
		long ll_2 = System.currentTimeMillis(); //
		logger.info("在临时表处理[" + str_ids.length + "]ids,共插入[" + al_sqls.size() + "]条记录,生成子查询,耗时[" + (ll_2 - ll_1) + "]"); //
		return "select ids from v_pub_sqlins where bno=" + str_newbatchno; //返回子查询,这样在原来的SQL中就是【select * from cmp_cmpfile where createcorp in (select ids from v_pub_sqlins where bno=345)】  //
	}

	public String getSubSQLFromTempSQLTableByIDs(String[] _ids, String fileName) throws Exception {
		//拼出一批SQL,快速提交,否则也容易死锁!

		if (_ids == null || _ids.length <= 0) { //
			return "'-99999'"; //直接返回
		}
		String[] str_ids = new TBUtil().distinctStrArray(_ids); //进行唯一性过滤!!!因为如果有重复的,则会浪费性能!!	
		if (str_ids == null || str_ids.length <= 0) { //曾经在兴业中遇到一个变态的情况是_ids就一项,而且是空字符串,然后经过唯一性合并后,长度为0了!,结果SQL条件返回就是个空串!!!所以必须还要有这个判断!!【xch/2012-04-28】
			return "'-99999'"; //直接返回
		}
		if (str_ids.length <= 999) { //如果在999个以内,则直接拼起来返回!!!!!!即不会太长了,这样兼容直接in的情况!!!
			StringBuffer sb_alltext = new StringBuffer(); //
			for (int i = 0; i < str_ids.length; i++) { ////
				sb_alltext.append("'" + (str_ids[i] == null ? "" : str_ids[i]) + "'"); ////
				if (i != str_ids.length - 1) { //如果不是最后一个则加上逗号!!!!!
					sb_alltext.append(","); //如果不是最后一个,则加逗号!!!!!
				}
			}
			String sql_str = fileName + " in (" + sb_alltext.toString() + ")";
			return sql_str; //
		}

		return getOracleSQLIn(str_ids, fileName);
	}

	private String getOracleSQLIn(String[] _ids, String fieldName) {
		String[] str_ids = new TBUtil().distinctStrArray(_ids); //进行唯一性过滤!!!因为如果有重复的,则会浪费性能!!	
		int count = 900;
		int len = str_ids.length;
		int size = len % count;
		if (size == 0) {
			size = len / count;
		} else {
			size = (len / count) + 1;
		}
		StringBuilder sql = new StringBuilder();
		for (int i = 0; i < size; i++) {
			int length = (i + 1) * count;
			if (length >= len) {
				length = len;
			}
			if (i == 0) {
				sql.append(fieldName + " in ( ");
			} else {
				sql.append(" or " + fieldName + " in ( ");
			}

			for (int j = i * count; j < length; j++) {
				sql.append("'" + _ids[j] + "',");
				if (j == length - 1) {
					sql.append("'" + _ids[j] + "'");
				}
			}
			sql.append(")");

		}
		return sql.toString();
	}

	public TableDataStruct getTableDataStructByDS(String _datasourcename, String _sql) throws Exception {
		return getTableDataStructByDS(_datasourcename, _sql, true); //
	}

	/**
	 * 取得表结构对象!!
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public TableDataStruct getTableDataStructByDS(String _datasourcename, String _sql, boolean _isDebugLog) throws Exception {
		WLTDBConnection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		int li_columncount = 0;

		String str_fromtablename = null; // 表名
		// String str_catalogname = null; //catalog
		// String str_schemaname = null; //schema

		String[] str_columnnames = null;
		int[] li_column_types = null;
		String[] str_column_typenames = null;
		int[] li_column_lengths = null;
		int[] li_valueMaxWidth = null; //
		String[] isNullAble = null;
		int[] precision = null;
		int[] scale = null;
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}
			String str_sqlTrace = null; //
			if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder在调用,即在使用LoadRunder测试时,是不做这个逻辑的!!!
				addSQLToSessionSQLListener(_sql); //
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) {
					str_sqlTrace = getCurrThreadTrace(); //取得当前线程堆栈!
				}
			}
			conn = getConn(_datasourcename); //取得数据库连接!!		
			stmt = conn.createStatement(); //
			if (_isDebugLog) {
				logger.debug("取数SQL*T:" + _sql + (str_sqlTrace == null ? "" : "," + str_sqlTrace)); //
			}
			long ll_rs_1 = System.currentTimeMillis(); //
			rs = stmt.executeQuery(_sql); //要出错就在这里!!!要慢也是慢在这里!!!
			long ll_rs_2 = System.currentTimeMillis(); //
			ResultSetMetaData rsmd = rs.getMetaData();
			li_columncount = rsmd.getColumnCount(); // 总共有几列
			str_columnnames = new String[li_columncount];
			li_column_types = new int[li_columncount];
			str_column_typenames = new String[li_columncount];
			li_column_lengths = new int[li_columncount];
			li_valueMaxWidth = new int[li_columncount]; //每列的每大宽度!
			isNullAble = new String[li_columncount]; //是否为空
			precision = new int[li_columncount]; //精确度
			scale = new int[li_columncount]; //小数点后几位
			for (int i = 0; i < str_columnnames.length; i++) {
				if (i == 0) {
					String isGetTableName = ServerEnvironment.getProperty("ISMETADATAGETTABLENAME"); //是否需要执行MetaData取表名的逻辑，sybase数据库会报错，所以增加此参数，默认是要取的
					if (!"N".equalsIgnoreCase(isGetTableName)) {
						str_fromtablename = rsmd.getTableName(i + 1); //
					}
				}
				str_columnnames[i] = rsmd.getColumnLabel(i + 1);
				li_column_types[i] = rsmd.getColumnType(i + 1);
				if ("mysql".equalsIgnoreCase(ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getDbtype()) && -1 == li_column_types[i] && "varchar".equalsIgnoreCase(rsmd.getColumnTypeName(i + 1))) { //处理Mysql的Text类型
					str_column_typenames[i] = "text";//避免mysql的text了姓返回varchar
				} else {
					str_column_typenames[i] = rsmd.getColumnTypeName(i + 1);
				}
				li_column_lengths[i] = rsmd.getColumnDisplaySize(i + 1); //
				if (str_column_typenames[i] != null && ("clob".equals(str_column_typenames[i].toLowerCase()) || "text".equals(str_column_typenames[i].toLowerCase()))) {
					li_column_lengths[i] = Integer.MAX_VALUE;
				}
				//注意，如果数据源为Oracle数据库，字段为Clob类型并且服务器端使用的jdk版本为1.6.0_26时下面一句代码则会报错，这时将jdk版本降低为1.6.0_18或jdk1.6.0_14就可以了【李春娟/2012-03-15】
				//并且jdk版本为1.6.0_26，用平台工具/state 对比表结构时，经常出现数据库中有该表但比较出来的结构是需要创建表，还会出现某个数据库中已存在的字段在卡片上变红（平台默认勾选了保存并且数据库中没有该字段时，在卡片展现时会变红）
				precision[i] = rsmd.getPrecision(i + 1);
				if (precision[i] <= 0) { //如果是oracle number类型，并且是默认长度 ，如：id number , 此值为空 。by haoming 2016-05-17
					precision[i] = li_column_lengths[i];
				}
				scale[i] = rsmd.getScale(i + 1); //
				if (scale[i] < 0) {//如果是oracle number类型，并且是默认长度 ，如：id number , 此值为-127 。by haoming 2016-05-17
					scale[i] = 0;
				}
				if (rsmd.isNullable(i + 1) == 0) { //不为空
					isNullAble[i] = "N";
				} else { //可以为空或不知道是否为空
					isNullAble[i] = "Y";
				}
			}

			int li_rows = 0;
			//int li_warnrowcount = 3000; //
			long ll_allDataSize = 0; //记录下查询数据的大小,用于监控性能问题!!!
			TBUtil tbUtil = new TBUtil(); //
			ArrayList al_rowData = new ArrayList(); //以前是Vector,现改成ArrayList,这样性能会高些!
			while (rs.next()) { //
				String[] str_row = new String[li_columncount];
				for (int i = 1; i <= li_columncount; i++) { ////
					String str_cell = getClobUtil().getClob(rs.getString(i));
					if (str_cell == null) {
						str_cell = "";
					} else if (str_column_typenames[i - 1].equalsIgnoreCase("DATE")) {
						str_cell = str_cell.substring(0, str_cell.length() - 2);
					}
					str_row[i - 1] = str_cell;
					if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder在调用,即在使用LoadRundered测试时,是不做这个逻辑的!!!
						int li_strlen = tbUtil.getStrUnicodeLength(str_cell); //计算这个符串的宽度!因为在表格参照中需要自动计算一列中最大的宽度,然后合理设置窗口宽度!!!要考虑中文!!!
						if (li_strlen > li_valueMaxWidth[i - 1]) { //
							li_valueMaxWidth[i - 1] = li_strlen; //
						}
						ll_allDataSize = ll_allDataSize + li_strlen; //
					}
				}
				al_rowData.add(str_row); ////
				li_rows++; ////
			}

			int li_rowcount = al_rowData.size(); //
			String[][] str_data = new String[li_rowcount][li_columncount];
			for (int i = 0; i < li_rowcount; i++) {
				String[] str_rowdata = (String[]) al_rowData.get(i);
				str_data[i] = str_rowdata;
			}
			TableDataStruct struct = new TableDataStruct();
			struct.setTableName(str_fromtablename); // 设置从哪个表取数!!!!!
			struct.setHeaderName(str_columnnames); // 设置表头结构
			struct.setHeaderType(li_column_types); // 表头字段类型!!!
			struct.setHeaderTypeName(str_column_typenames);// 设置字段类型名称
			struct.setHeaderLength(li_column_lengths); // 宽度
			struct.setBodyData(str_data); // 设置表体结构
			struct.setIsNullAble(isNullAble);//设置是否为空
			struct.setColValueMaxLen(li_valueMaxWidth); //每列的最大宽度
			struct.setPrecision(precision);
			struct.setScale(scale);

			long ll_rs_3 = System.currentTimeMillis(); //
			if (_isDebugLog) { //是否要输出日志???
				if (ll_rs_3 - ll_rs_2 > 50 || ll_rs_2 - ll_rs_1 > 50) { //如果超过50毫秒,则警告!!
					logger.warn("取数SQL*T(DB耗时" + (ll_rs_2 - ll_rs_1) + ",Web耗时" + (ll_rs_3 - ll_rs_2) + "):" + _sql + (str_sqlTrace == null ? "" : "," + str_sqlTrace)); //
				}
			}
			if (!ServerEnvironment.isLoadRunderCall && ll_allDataSize > 0) { //如果不是LoadRunder在调用,即在使用LoadRunder测试时,是不做这个逻辑的!!!
				new WLTInitContext().setCurrSessionCustInfoByKey("取数大小", new String[] { "(getTabStc[" + li_rows + "])" + _sql, "" + ll_allDataSize }); //
			}
			return struct;
		} catch (SQLException ex) {
			throw new SQLException("在数据源[" + _datasourcename + "]上执行SQL[" + _sql + "]出错,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage()); //
		} catch (Exception ex) {
			throw new Exception("在数据源[" + _datasourcename + "]上执行SQL[" + _sql + "]出错,原因:" + ex.getMessage()); //
		} finally {
			try {
				rs.close();
			} catch (Exception ex) {
			}
			try {
				stmt.close();
			} catch (Exception ex) {
			}
		}
	}

	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, true, false, null, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))); //
	}

	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, int _topRecords) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, true, false, new int[] { 1, _topRecords }, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))); //
	}

	//根据SQL得到HashVO[],最常用的方法!!!!
	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, true, false, null, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))).getHashVOs(); //
	}

	/**
	 * 根据SQL语句返回HashVO!!而且只取前多少条!! 在导出数据等功能时必须用到该方法!!!
	 * 后台数据库自动进行根据数据源类型选择不同的分页机制!! 这个很关键!!
	 * @param _sql
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, int _topRecords) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, true, false, new int[] { 1, _topRecords }, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))).getHashVOs(); //
	}

	/**
	 * 根据SQL语句返回HashVO
	 * 
	 * @param _sql
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, boolean _isDebugLog) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, _isDebugLog, false, null, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))).getHashVOs(); //
	}

	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, boolean _isDebugLog, boolean _isUpperLimit, int[] _rowArea) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, _isDebugLog, _isUpperLimit, _rowArea, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))); //
	}

	/**
	 * 取得带结构的HashVO数据,表体是HashVO[]
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _rowArea  行范围,即30-50
	 * @return
	 * @throws Exception
	 */
	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, boolean _isDebugLog, boolean _isUpperLimit, int[] _rowArea, boolean _isNeedCount, boolean _isImmediately) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, _isDebugLog, _isUpperLimit, _rowArea, _isNeedCount, _isImmediately, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")));
	}

	/**
	 * 实际调用的方法!!
	 * @param _datasourcename
	 * @param _initSql
	 * @param _isDebugLog
	 * @param _isUpperLimit
	 * @param _rowArea
	 * @param _isNeedCount
	 * @param _isImmediately
	 * @param _isPrepareSQL
	 * @return
	 * @throws Exception
	 */
	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _initSql, boolean _isDebugLog, boolean _isUpperLimit, int[] _rowArea, boolean _isNeedCount, boolean _isImmediately, boolean _isPrepareSQL) throws Exception {
		java.sql.Connection conn = null;
		Statement stmt = null; //
		Statement stmt_count = null; //求记录总数!!
		ResultSet rs = null; //
		ResultSet rs_count = null; //求记录总数!!

		String str_sql_convert = null;
		String str_count_sql = null; //
		try {
			WLTInitContext initContext = new WLTInitContext(); //
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); //
			}
			String str_sqlTrace = null; //
			if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder调用,才监听!!
				addSQLToSessionSQLListener(_initSql); //向SQL监听器中增加本SQL
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) { //如果是Debug模式,则记录下调用的堆栈!!!
					str_sqlTrace = getCurrThreadTrace(); //
				}
			}

			String str_preSQL = _initSql; //先设成原来的!!
			ArrayList al_pres = null; //预编译的参数!!
			if (_isPrepareSQL) {
				al_pres = new PrepareSQLUtil().prepareSQL(_initSql); //
				if (al_pres != null && al_pres.size() > 1) {
					str_preSQL = (String) al_pres.get(0); // 
				} else {
					_isPrepareSQL = false; //否则不真正预编译!!
				}
			}
			int li_from_pos = str_preSQL.toLowerCase().indexOf(" from "); //
			int li_orderby_pos = str_preSQL.toLowerCase().indexOf(" order by "); //看有没有指定排序字段!

			String str_selfield = null; //选择的字段!
			String str_initfromtable = null; //原始SQL查询的表名!因为有的数据库不知为什么从metadata中取不到查询的表名,这时需要从SQL中解析!!!
			String str_fromsql = null; //从From往后的SQL
			String str_unOrderSQL = null; //去掉排序条件后剩下的SQL
			String str_order_cons = null; //,排序条件

			if (li_from_pos > 0) { //如果有From
				str_selfield = str_preSQL.substring(str_preSQL.toLowerCase().indexOf("select ") + 7, li_from_pos); //选择的字段!!!
				String str_sql_right = str_preSQL.substring(li_from_pos + 6, str_preSQL.length()); //
				if (str_sql_right.indexOf(" ") > 0) {
					str_initfromtable = str_sql_right.substring(0, str_sql_right.indexOf(" ")); //
				} else {
					str_initfromtable = str_sql_right.trim(); //
				}
				if (li_orderby_pos > 0) { //如果有Orderby
					str_fromsql = str_preSQL.substring(li_from_pos, li_orderby_pos); //从From往后的的
				} else {
					str_fromsql = str_preSQL.substring(li_from_pos, str_preSQL.length()); //从From往后的的
				}
			}

			if (li_orderby_pos > 0) { //如果有Orderby
				str_unOrderSQL = str_preSQL.substring(0, li_orderby_pos); //
				str_order_cons = str_preSQL.substring(li_orderby_pos, str_preSQL.length()); //排序条件
			} else {
				str_unOrderSQL = str_preSQL; //不排序的SQL
			}

			//这里要根据数据库类型对SQL进行不同的分页处理!! 即以前分页机制都有问题,都经不住压力测试!! 唯一有效的办法还是有数据库中分页!!
			str_sql_convert = str_preSQL; //先赋值为原来的!!
			boolean isConvertSQL = false; //是否进行了转换!! 如果没有进行转换,则还是使用原来的滚游标的方法!!!!

			//如果有分页要求,则要进行复杂的分页SQL处理,即使用不同的数据库分页函数进行SQL转换!!! 在招行项目中经过严格的性能测试,发现必须在数据库中分页,否则性能根本过不了!
			if (_rowArea != null && _rowArea[0] >= 0) { //如果指定了分页,即是通过BillList面板中查询过来的!!
				//解析SQL,计算出from,order等参数!!!
				DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
				String str_dbtype = dsVO.getDbtype(); //数据库类型!
				String str_dbVersion = dsVO.getDbversion(); //数据库版本!
				if (str_dbtype.equalsIgnoreCase("ORACLE")) { //如果是Oracle,则使用rownum包三层!! 这是以前搞过无数次的方法!!!
					isConvertSQL = true; //转换!!
					StringBuilder sb_sql = new StringBuilder(); //
					sb_sql.append("with tt1 as ("); //
					sb_sql.append(str_preSQL); //
					sb_sql.append(") "); //
					if (_rowArea[0] == 1) { //如果是第一页则又可以特殊处理!即可以少一层子查询!从而提高性能!!
						sb_sql.append("select * from tt1 where rownum<=" + _rowArea[1]); //
					} else {
						sb_sql.append("select * from (select rownum as tt1_rownum,tt1.* from tt1 where rownum<=" + _rowArea[1] + ") tt2 where tt1_rownum>=" + _rowArea[0]); //
					}
					str_sql_convert = sb_sql.toString(); //包三层的SQL,如果没有定义order by 可以少一层!
				} else if (str_dbtype.equalsIgnoreCase("SQLSERVER")) { //SQLServer
					if (str_dbVersion.startsWith("2000")) { //如果是2000则可能需要使用两次top颠倒的方法搞一下!!
					} else if (str_dbVersion.startsWith("2005")) { //如果是2005则使用row_number()函数进行转换!!
						isConvertSQL = true; //转换!!
						StringBuilder sb_sql_new = new StringBuilder(); //
						sb_sql_new.append("with t1_ as "); //
						sb_sql_new.append("("); //
						if (str_order_cons != null) { //如果指定了排序条件
							sb_sql_new.append("select row_number() over (" + str_order_cons + ") rownum_,"); //
						} else {
							sb_sql_new.append("select row_number() over (order by id asc) rownum_,"); ////这里可能因为有的人没有定义主键名为id,从而造成问题!! 以后再想办法! 如果发生这个问题,可以暂时先在原来表上面加个名为id的字段!!
						}
						sb_sql_new.append(str_unOrderSQL.substring(str_unOrderSQL.toLowerCase().indexOf("select ") + 7, str_unOrderSQL.length()).trim()); //将原来的select后面开始的内容接上来!
						sb_sql_new.append(") ");
						sb_sql_new.append("select top " + (_rowArea[1] - _rowArea[0] + 1) + " * from t1_ where rownum_ >= " + _rowArea[0] + ""); //分页!!!
						str_sql_convert = sb_sql_new.toString(); //
					}
				} else if (str_dbtype.equalsIgnoreCase("MYSQL")) { //Mysql,使用limit搞一下!!! 即 select * from pub_user where name like '%张%' order by name limit 21,40
					isConvertSQL = true; //是否转换SQL!!
					str_sql_convert = str_preSQL; // 
					str_sql_convert = str_sql_convert + " limit " + (_rowArea[0] - 1) + "," + (_rowArea[1] - _rowArea[0] + 1); //使用limit控制!!
				} else if (str_dbtype.equalsIgnoreCase("DB2")) { //db2,暂时还不知道怎么搞!!
					isConvertSQL = true; //转换!!
					StringBuilder sb_sql_new = new StringBuilder(); //
					sb_sql_new.append("with t1_ as "); //
					sb_sql_new.append("("); //
					if (str_order_cons != null) { //如果指定了排序条件
						sb_sql_new.append("select row_number() over (" + str_order_cons + ") rownum_,"); //
					} else {
						sb_sql_new.append("select row_number() over (order by id asc) rownum_,"); ////这里可能因为有的人没有定义主键名为id,从而造成问题!! 以后再想办法! 如果发生这个问题,可以暂时先在原来表上面加个名为id的字段!!
					}
					if ("*".equals(str_selfield.trim())) { //如果取的字段就是*,则DB2有个变态的地方，就是必须在*前面加上表名，否则会报错,SQLServer与Oracle都没有这个问题!!!
						sb_sql_new.append(str_initfromtable + ".* " + str_fromsql); //
					} else {
						sb_sql_new.append(str_selfield + str_fromsql); //将原来的select后面开始的内容接上来!
					}
					sb_sql_new.append(") ");
					sb_sql_new.append("select * from t1_ where rownum_ >= " + _rowArea[0] + "  and rownum_<=" + _rowArea[1]); //分页!!!
					str_sql_convert = sb_sql_new.toString(); //
				} else { //其他的!
				}
			}
			if (_isImmediately) { //如果是独立事务一次性查询!
				DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
				if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) {
					WLTDBConnection wltc = new WLTDBConnection(_datasourcename);
					conn = wltc.getConn();
				} else {
					String str_dburl = "jdbc:apache:commons:dbcp:" + _datasourcename; // //
					conn = DriverManager.getConnection(str_dburl); // //真正创建连接,其实也是从dbcp池中取的!!!
				}
				//conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED); //设置事务隔离级别!!设置死了!!
			} else {
				conn = getConn(_datasourcename).getConn(); //因为可能不取数据库!所以必须放在这,否则每次SQLServer都会连一把,实际上还是慢!!!
			}
			if (_isDebugLog) { //输出Debug
				StringBuilder sb_sql_info = new StringBuilder(); //
				sb_sql_info.append("取数SQL*H" + _initSql); //
				if (_isPrepareSQL) {
					sb_sql_info.append("\r\n取数SQL*H(预编译转换)" + str_preSQL); //
				}
				if (isConvertSQL) { //如果进行了分页转换
					sb_sql_info.append("\r\n取数SQL*H(分页转换)" + str_sql_convert); //
				}
				if (str_sqlTrace != null) {
					sb_sql_info.append("," + str_sqlTrace); //
				}
				logger.debug(sb_sql_info.toString());
			}
			long ll_rs_1 = System.currentTimeMillis(); //
			if (_isPrepareSQL) { //如果是预编译
				stmt = conn.prepareStatement(str_sql_convert); //
				for (int i = 1; i < al_pres.size(); i++) {
					((PreparedStatement) stmt).setObject(i, al_pres.get(i)); //设置值!!
				}
				rs = ((PreparedStatement) stmt).executeQuery(); //
			} else {
				stmt = conn.createStatement(); //
				rs = stmt.executeQuery(str_sql_convert); //
			}
			long ll_rs_2 = System.currentTimeMillis(); //
			initContext.addCurrSessionForClientTrackMsg((isConvertSQL ? "进行了SQL分页转换" : "没有进行SQL分页转换") + ",实际执行的SQL是[" + str_sql_convert + "],耗时[" + (ll_rs_2 - ll_rs_1) + "]\r\n"); //返回给客户端!!!
			ResultSetMetaData rsmd = rs.getMetaData(); //得到元数据!!!
			String str_fromtablename = null; // 表名
			int li_columncount = rsmd.getColumnCount(); // 总共有几列
			String[] str_columnnames = new String[li_columncount]; // 每一列的名称!!
			int[] li_column_types = new int[li_columncount]; // 每一列的类型
			String[] str_column_typenames = new String[li_columncount]; // 每一列类型的名字
			int[] li_column_lengths = new int[li_columncount]; // 每一列的宽度
			int[] precision = new int[li_columncount];
			int[] scale = new int[li_columncount];
			for (int i = 1; i < rsmd.getColumnCount() + 1; i++) { //遍历各个列!!!
				if (i == 1) {
					String isGetTableName = ServerEnvironment.getProperty("ISMETADATAGETTABLENAME"); //是否需要执行MetaData取表名的逻辑，sybase数据库会报错，所以增加此参数，默认是要取的
					if (!"N".equalsIgnoreCase(isGetTableName)) {
						str_fromtablename = rsmd.getTableName(i); //
					}
					if (str_fromtablename == null || str_fromtablename.equals("")) { //有的数据库不知道为什么取不到表名!!!
						str_fromtablename = str_initfromtable; //
					}
				}
				str_columnnames[i - 1] = rsmd.getColumnLabel(i);
				li_column_types[i - 1] = rsmd.getColumnType(i);
				str_column_typenames[i - 1] = rsmd.getColumnTypeName(i); //
				li_column_lengths[i - 1] = rsmd.getColumnDisplaySize(i); //
				//注意，如果数据源为Oracle数据库，字段为Clob类型并且服务器端使用的jdk版本为1.6.0_26时下面一句代码则会报错，这时将jdk版本降低为1.6.0_18或jdk1.6.0_14就可以了【李春娟/2012-03-15】
				//并且jdk版本为1.6.0_26，用平台工具/state 对比表结构时，经常出现数据库中有该表但比较出来的结构是需要创建表，还会出现某个数据库中已存在的字段在卡片上变红（平台默认勾选了保存并且数据库中没有该字段时，在卡片展现时会变红）
				precision[i - 1] = rsmd.getPrecision(i);
				scale[i - 1] = rsmd.getScale(i); //
			}
			HashVOStruct hashVOStruct = new HashVOStruct(); // 创建HashVOStruct
			hashVOStruct.setTableName(str_fromtablename); // 设置从哪个表取数!!!!!
			hashVOStruct.setFromSQL(str_sql_convert); //设置是从哪个SQL生成的!
			hashVOStruct.setHeaderName(str_columnnames); // 设置表头结构
			hashVOStruct.setHeaderType(li_column_types); // 表头字段类型!!!
			hashVOStruct.setHeaderTypeName(str_column_typenames);// 设置字段类型名称
			hashVOStruct.setHeaderLength(li_column_lengths); // 宽度
			hashVOStruct.setPrecision(precision);
			hashVOStruct.setScale(scale);
			HashVO rowHVO = null; //
			int li_rows = 0;
			long ll_allDataSize = 0;//记录这次查询数据的大小!!!用于来判断是否会产生性能问题!!! 比如发现一次远程调用中,发生的查询数据的内容非常大,甚至超过10M,则认为很可能会导致内存溢出!!!即OutOfMemory.
			Object value = null; //
			String str_colname = null; //
			int li_coltype = 0; //

			ArrayList al_rowData = new ArrayList(); //以前是Vector,改成ArrayList,还是要快一点的!
			if (limitcount <= 0) {
				limitcount = 2000;
			}
			while (rs.next()) { //遍历各行,循环取数!!
				li_rows++; //累数器
				if (_isUpperLimit && li_rows > limitcount) { //如果有上限控制设置,并且超过800条了(以前是500的,后来李克振说要800,招行又说是2000),则直接退出!!这是一个非常重要的保险措施!因为在列表查询时,如果有人忘了做分页设置,然后又查询所有数据,则极有可能导致系统其慢,甚至会造成内存溢出!! 所以控制在500条之内,然后做分页!
					li_rows = -limitcount; //写成负数,提示别人做分页!
					break; //
				}
				if (!isConvertSQL && _rowArea != null && _rowArea[0] >= 0) { //如果有分页,且没有使用转换SQL实现!
					if (li_rows < _rowArea[0]) { //如果小于计算出来的分页的起始行号,则继续!!1-20,21-40
						continue;
					}
					if (li_rows > _rowArea[1]) { //如果大于计算出来的分页的结束行号,则只是空滚游标,而不做处理!!!
						break; //中断,不往下找了! 以前的缺点是绝大多数情况还是第一页,然后10万条的记录会空转游标9万多次! 所以性能很慢,现在中断后就不一样了,但如果就是跳转到最后一页,则依然很慢,但好就好在这种情况的概率很低!
					}
				}
				rowHVO = new HashVO(); //
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) { //遍历各列!
					str_colname = rsmd.getColumnLabel(i); // 列名
					li_coltype = rsmd.getColumnType(i); // 列类型
					value = null; //
					if (li_coltype == Types.VARCHAR) { // 如果是字符
						value = getClobUtil().getClob(rs.getString(i));
					} else if (li_coltype == Types.NUMERIC) { // 如果是Number
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.DATE || li_coltype == Types.TIMESTAMP) { // 如果是日期或时间类型,统统精确到秒,Oracle中的Date类型是Types.DATE,但返回的值是Timestamp!!!
						value = rs.getTimestamp(i);
					} else if (li_coltype == Types.SMALLINT) { // 如果是整数
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Integer(bigDecimal.intValue()); // 
						}
					} else if (li_coltype == Types.INTEGER) { // 如果是整数
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Long(bigDecimal.longValue()); // (rs.getBigDecimal(i)
						}
					} else if (li_coltype == Types.DECIMAL || li_coltype == Types.DOUBLE || li_coltype == Types.DOUBLE || li_coltype == Types.FLOAT) {
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.CLOB) { // 如果是Clob类型,则要使用Read进行读取!!!
						java.sql.Clob clob = rs.getClob(i); // clob
						if (clob != null) {
							java.io.Reader inread = clob.getCharacterStream();
							char[] buf = new char[2048];
							StringBuilder sb_aa = new StringBuilder(); ////
							int len = -1;
							while ((len = inread.read(buf)) != -1) {
								sb_aa.append(buf, 0, len); //
							}
							value = getClobUtil().getClob(new String(sb_aa.toString())); //
						}
					} else {
						value = getClobUtil().getClob(rs.getString(i));
					}
					rowHVO.setAttributeValue(str_colname, value); // 设置值!!!

					//计算内容的大小,用于判断一次远程调用中有无发生大数据量的查询!!!即可能会造成OutOfMemory的可能性!!!
					if (!ServerEnvironment.isLoadRunderCall && value != null) { //如果不是LoadRunder调用,才监听!!,即在LoadRunder测试时不做这个逻辑从而大大提高速度!!!!
						ll_allDataSize = ll_allDataSize + String.valueOf(value).length(); //直接转String 算长度,有一定道理,因为普通数据类型就是这样!
					}
				}
				//BS端数据权限过滤!!!
				al_rowData.add(rowHVO); ////
			} //end while..

			HashVO[] vos = (HashVO[]) al_rowData.toArray(new HashVO[0]); //转换一把!!!
			hashVOStruct.setHashVOs(vos); //设置数据!!
			long ll_rs_3 = System.currentTimeMillis(); //
			if (_isDebugLog) {
				if (ll_rs_3 - ll_rs_2 > 50 || ll_rs_2 - ll_rs_1 > 50) { //如果超过50毫秒,则警告!!
					logger.warn("取数SQL*H" + str_sql_convert + "(DB耗时" + (ll_rs_2 - ll_rs_1) + ",Web耗时" + (ll_rs_3 - ll_rs_2) + ",可能需要优化!"); ////
				}
			}
			if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder调用,才监听!!,即在LoadRunder测试时不做这个逻辑从而大大提高速度!!!
				initContext.setCurrSessionCustInfoByKey("取数大小", new String[] { "(getHashVOs[" + li_rows + "])" + str_sql_convert, "" + ll_allDataSize }); //将ll_allDataSize的大小送入 CurrSessionVO中的custMap中,key是"取数量",value是二维数组(第一列是[getHashVOs/getTableStruct/getStringArray]SQL,第二列是大小)
			}

			//算总数!!
			if (_isNeedCount && _rowArea != null && _rowArea[0] >= 0) { //如果有分页,则总是使用count(*)计算总数!!!因为像层次数据时只要分页而并不需要求总数所以要再加上开关_isNeedCount,求总数只有在列表查询时用到!!!在招行中被逼的!!!
				long ll_rs_count_1 = System.currentTimeMillis(); //
				int li_allCount = 0; //
				str_count_sql = "select count(*) " + str_unOrderSQL.substring(li_from_pos, str_unOrderSQL.length()); //求总数的SQL!!!
				if (_isPrepareSQL) { //如果是预编译
					stmt_count = conn.prepareStatement(str_count_sql); //
					for (int i = 1; i < al_pres.size(); i++) {
						((PreparedStatement) stmt_count).setObject(i, al_pres.get(i)); //设置值!!
					}
					rs_count = ((PreparedStatement) stmt_count).executeQuery(); //
				} else { //如果不需预编译!!!
					stmt_count = conn.createStatement(); //
					rs_count = stmt_count.executeQuery(str_count_sql); //
				}
				long ll_rs_count_2 = System.currentTimeMillis(); //
				if (rs_count.next()) {
					li_allCount = rs_count.getInt(1); //
				}
				hashVOStruct.setTotalRecordCount(li_allCount); //设置记录总数!!!
				if (_isDebugLog) { //如果不是LoadRunder调用,才监听!!
					logger.debug("求总数SQL(从库中取)" + "(DB耗时" + (ll_rs_count_2 - ll_rs_count_1) + ")" + ":" + str_count_sql); ////
				}
				initContext.addCurrSessionForClientTrackMsg("进行了Count()计算,实际执行的SQL是:[" + str_count_sql + "],耗时[" + (ll_rs_count_2 - ll_rs_count_1) + "]\r\n"); //
			} else {
				hashVOStruct.setTotalRecordCount(li_rows); //设置实际的总计记录数
			}
			return hashVOStruct; ////
		} catch (WLTAppException ex) {
			throw ex; //
		} catch (SQLException ex) {
			String str_errsql = null; //
			if (str_count_sql != null) {
				str_errsql = str_count_sql;
			} else {
				str_errsql = str_sql_convert;
			}
			System.err.println("执行sql报错[" + str_errsql + "]"); //
			ex.printStackTrace(); //
			throw new SQLException("在数据源[" + _datasourcename + "]上执行SQL[" + str_errsql + "]出错,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage() + ",\n原始SQL是:[" + _initSql + "]");
		} catch (Exception ex) {
			//System.out.println("str_sql_convert2是啥[" + str_sql_convert2 + "],str_sql_convert又是啥[" + str_sql_convert + "],是否预编译[" + _isPrepareSQL + "]");
			ex.printStackTrace(); //
			throw new Exception("在数据源[" + _datasourcename + "]上执行SQL(可能不是这条,而是转换后的)[" + _initSql + "]出错,原因:" + ex.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception exx) {
			}
			try {
				if (stmt != null)
					stmt.close();
			} catch (Exception exx) {
			}
			try {
				if (rs_count != null)
					rs_count.close();
			} catch (Exception exx) {
			}
			try {
				if (stmt_count != null)
					stmt_count.close();
			} catch (Exception exx) {
			}
			if (_isImmediately) { //如果是独立事务查询
				if (conn != null) {
					try {
						conn.close(); //关闭连接!!
					} catch (Exception exx2) {
					}
				}
			}
		}
	}

	/**
	 * 在性能测试时遇到登录时的几条SQL，只是查询,应该使用预编译的方式!
	 * @param _datasourcename
	 * @param _sql
	 * @param _pars
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getHashVoArrayByDSByMacro(String _datasourcename, String _sql, Object[] _pars) throws Exception {
		java.sql.Connection conn = null; //
		PreparedStatement p_stmt = null; //
		ResultSet rs = null; //
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); //
			}
			String str_sqlTrace = ""; //
			if (!ServerEnvironment.isLoadRunderCall) { //如果不是LoadRunder调用,才监听!!
				addSQLToSessionSQLListener(_sql); //向SQL监听器中增加本SQL
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) { //如果是Debug模式,则记录下调用的堆栈!!!
					str_sqlTrace = getCurrThreadTrace(); //
				}
			}
			logger.debug(_sql + str_sqlTrace); //输出日志!!

			conn = getConn(_datasourcename).getConn(); //因为可能不取数据库!所以必须放在这,否则每次SQLServer都会连一把,实际上还是慢!!!
			p_stmt = conn.prepareStatement(_sql); //
			for (int i = 0; i < _pars.length; i++) {
				if (_pars[i] == null || "".equals(("" + _pars[i]).trim()) || "null".equalsIgnoreCase(("" + _pars[i]).trim())) {
					p_stmt.setNull(i + 1, java.sql.Types.VARCHAR); //
				} else {
					if (_pars[i] instanceof String) {
						p_stmt.setString(i + 1, (String) _pars[i]); //执行宏..
					} else if (_pars[i] instanceof Integer) {
						p_stmt.setInt(i + 1, ((Integer) _pars[i]).intValue()); //
					} else {
						p_stmt.setString(i + 1, "" + _pars[i]); //执行宏..
					}
				}
			}

			rs = p_stmt.executeQuery(); //查询
			ResultSetMetaData rsmd = rs.getMetaData(); //得到元数据!!!
			HashVO rowHVO = null; //
			Object value = null; //
			String str_colname = null; //
			int li_coltype = 0; //
			ArrayList al_rowData = new ArrayList(); //以前是Vector,改成ArrayList,还是要快一点的!
			while (rs.next()) { //遍历各行,循环取数!!
				rowHVO = new HashVO(); //
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) { //遍历各列!
					str_colname = rsmd.getColumnLabel(i); // 列名
					li_coltype = rsmd.getColumnType(i); // 列类型
					value = null; //
					if (li_coltype == Types.VARCHAR) { // 如果是字符
						value = getClobUtil().getClob(rs.getString(i));
					} else if (li_coltype == Types.NUMERIC) { // 如果是Number
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.DATE || li_coltype == Types.TIMESTAMP) { // 如果是日期或时间类型,统统精确到秒,Oracle中的Date类型是Types.DATE,但返回的值是Timestamp!!!
						value = rs.getTimestamp(i);
					} else if (li_coltype == Types.SMALLINT) { // 如果是整数
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Integer(bigDecimal.intValue()); // 
						}
					} else if (li_coltype == Types.INTEGER) { // 如果是整数
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Long(bigDecimal.longValue()); // (rs.getBigDecimal(i)
						}
					} else if (li_coltype == Types.DECIMAL || li_coltype == Types.DOUBLE || li_coltype == Types.DOUBLE || li_coltype == Types.FLOAT) {
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.CLOB) { // 如果是Clob类型,则要使用Read进行读取!!!
						java.sql.Clob clob = rs.getClob(i); // clob
						if (clob != null) {
							java.io.Reader inread = clob.getCharacterStream();
							char[] buf = new char[2048];
							StringBuilder sb_aa = new StringBuilder(); ////
							int len = -1;
							while ((len = inread.read(buf)) != -1) {
								sb_aa.append(buf, 0, len); //
							}
							value = new String(sb_aa.toString()); //
							value = getClobUtil().getClob((String) value);
						}
					} else {
						value = getClobUtil().getClob(rs.getString(i));
					}
					rowHVO.setAttributeValue(str_colname, value); // 设置值!!!
				}
				//BS端数据权限过滤!!!
				al_rowData.add(rowHVO); //加入列表
			} //end while..
			HashVO[] vos = (HashVO[]) al_rowData.toArray(new HashVO[0]); //转换一把!!!
			return vos; //返回
		} catch (SQLException ex) {
			ex.printStackTrace(); //
			throw new SQLException("在数据源[" + _datasourcename + "]上执行SQL[" + _sql + "]出错,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage());
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			throw new Exception("在数据源[" + _datasourcename + "]上执行SQL(可能不是这条,而是转换后的)[" + _sql + "]出错,原因:" + ex.getMessage()); //
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception exx) {
			}
			try {
				if (p_stmt != null)
					p_stmt.close();
			} catch (Exception exx) {
			}
		}

	}

	/**
	 * 根据一个SQL语句生成HashVO数组,该数据的输出顺序是自动构造成一颗树,并按树型顺序输出!!其实就是一个树!!
	 * 从某种机制上说,他将取代where linkCode like '0001%' order by linkcode的机制,即使用_rootNodeCondition来实现过滤,比如id=125(广州分行的主键)，使用parentid来实现树的勾结,seq来实现兄弟间的排序!
	 * 它与linkCode的区别在于,linkCode从数据库中返回就是想要的[广州分行]的所有数据,而这种机制是先取出所有数据，然后在内存中构建树型结构，然后再找出[广州分行]的所有子结点!所以必然性能较低!
	 * 但只要在3万条以下的数据应该还是能接受的，因为这是在服务器端做，硬件的强大功力可以弥补这些！！！而超过3万条的树型结构实际应用中很少见！
	 * 所有机关与技巧在于SQL过滤与root过滤，两种常见情况就是:1.SQL有过滤条件而rootCondition没有(比如找出某一法律的所有条件) 2.SQL无过滤条件,而rootcondition有(比如找出广州分行)
	  * @param _datasourcename 数据源名称
	 * @param _sql  任一SQL语句,不需要order by seq,写了也不要紧,最后仍然是按 _seqField字段来排序! 这理解成是第一次过滤!即取出哪些所有数据，结果集必然在这中间!!按道理SQL过滤后应是一个完整的树！
	 * @param _idField  树型结构的勾结关联的id字段名,一般都是id
	 * @param _parentIDField  树型结构的勾结关联的parentid字段名,一般都是parentid
	 * @param _rootNodeCondition 指定取树型结构中的哪几个根结点的所有数据!!比如id=125(广州分行), 可以理解成是第二次过滤!!即在构造成树型结构之后,再次从树型结构中取指定根结构的数据!!
	 * @return HashVO数组，只不过是顺序对了！！
	 * @throws Exception
	 * 举例: 
	 * 1.getHashVoArrayAsTreeStructByDS(null,"select * from pub_corp_dept","id","parentid","seq","id=125");  //取出广州分行的所有机构树
	 * 2.getHashVoArrayAsTreeStructByDS(null,"select * from law_content where lawpropid='561'","id","parentid","seq",null);  //取出法律[贷款通则]的所有条文
	 */
	public HashVO[] getHashVoArrayAsTreeStructByDS(String _datasourcename, String _sql, String _idField, String _parentIDField, String _seqField, String _rootNodeCondition) throws Exception {
		HashVO[] hvsData = getHashVoArrayByDS(_datasourcename, _sql); //
		if (hvsData == null || hvsData.length == 0) {
			return hvsData; //
		}
		TBUtil tbUtil = new TBUtil(); //
		//应该先排序一下,根据seq,这样可能性能很慢,因为到时找爸爸时找得不快,感觉应该改成找儿子的算法,然后将所有找到的儿子进行排序!
		if (_seqField != null && !_seqField.trim().equals("")) { //如果定义了seq
			tbUtil.sortHashVOs(hvsData, new String[][] { { _seqField, "N", "Y" } }); ////当数字排序
		}

		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hvsData.length]; //创建结点
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("根结点");
		long t1 = System.currentTimeMillis();
		HashMap map_parent = new HashMap();
		for (int i = 0; i < node_level_1.length; i++) {
			hvsData[i].setEqualsFieldName(_idField); //设置比较的值字段
			node_level_1[i] = new DefaultMutableTreeNode(hvsData[i]); //
			map_parent.put(hvsData[i].getStringValue(_idField), node_level_1[i]); //先在哈希表中注册好..
			//			rootNode.add(node_level_1[i]); //如果一上来把所有节点放到根节点下，当下次循环找父亲时，断开链的关系很耗时！数据量少的时候不明显。如3W条机构树。都加到root下，再循环找父亲，耗时400-500ms。现在可以缩短到60ms左右[郝明2012-03-31]
		}

		HashVO nodeVO = null; //
		String str_pk_parentPK = null; //
		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (HashVO) node_level_1[i].getUserObject(); //先取得当前结点的值
			str_pk_parentPK = nodeVO.getStringValue(_parentIDField); //父亲主键
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) { //如果父亲结点为空,则不做处理..
				rootNode.add(node_level_1[i]); // 如果没有父亲信息，就加到根节点下。
				continue;
			}
			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) { //如果找到爸爸了..
				try {
					parentnode.add(node_level_1[i]); //在爸爸下面加入我..
				} catch (Exception ex) {
					WLTLogger.getLogger(this).error("在[" + parentnode + "]上创建子结点[" + node_level_1[i] + "]失败!!", ex); //
				}
			} else {
				rootNode.add(node_level_1[i]); //如果没有找到父亲，也加到根节点下。
			}
		}
		//		System.out.println("找父亲：" + (System.currentTimeMillis() - t1));
		BSUtil bsUtil = new BSUtil(); //

		if (_rootNodeCondition == null || _rootNodeCondition.trim().equals("")) { //如果没有二次过滤条件,则直接返回..
			DefaultMutableTreeNode[] allNodes = getOneNodeAllChildrenNodes(rootNode); //所有子结点,包括要结点
			HashVO[] returnVOs = new HashVO[allNodes.length - 1]; //
			for (int i = 1; i < allNodes.length; i++) { //根结点不算!!
				returnVOs[i - 1] = (HashVO) allNodes[i].getUserObject(); //
				returnVOs[i - 1].setAttributeValue("$level", allNodes[i].getLevel()); //加入级层伪列
				String[] str_parentPathIdNames = bsUtil.getOneTreeNodeParentPathItemValue(allNodes[i], new String[] { _idField, "name" }); //父系路径的所有结点的id拼在一起!!
				returnVOs[i - 1].setAttributeValue("$parentpathids", str_parentPathIdNames[0]); //将父亲路径的所有结点的id加入
				returnVOs[i - 1].setAttributeValue("$parentpathnames", str_parentPathIdNames[1]); //加入级层伪列
				returnVOs[i - 1].setAttributeValue("$parentpathnamelink", getPathNameSpans(tbUtil, str_parentPathIdNames[1], false)); //加入级层伪列,是【兴业银行-上海分行-法规部】的样子
				returnVOs[i - 1].setAttributeValue("$parentpathnamelink1", getPathNameSpans(tbUtil, str_parentPathIdNames[1], true)); //加入级层伪列,是【上海分行-法规部】的样子即自动截取掉第一层!因为第一层是废话,
				if (allNodes[i].isLeaf()) { //如果是叶子结点,则加入标记
					returnVOs[i - 1].setAttributeValue("$isleaf", "Y"); //
				}
			}
			//			System.out.println("整理：" + (System.currentTimeMillis() - t1));
			return returnVOs; //
		} else { //如果有二次过滤!
			String str_key = null;
			String[] str_values = null; //
			if (_rootNodeCondition.indexOf("=") > 0) { //如果等于机制！！
				str_key = _rootNodeCondition.substring(0, _rootNodeCondition.indexOf("=")).trim(); //
				String str_value = _rootNodeCondition.substring(_rootNodeCondition.indexOf("=") + 1, _rootNodeCondition.length()).trim(); //
				str_value = tbUtil.replaceAll(str_value, "'", ""); //可能有单引号!则去掉
				str_values = new String[] { str_value }; //
			} else if (_rootNodeCondition.indexOf(" in") > 0) { //如果是in机制！！
				str_key = _rootNodeCondition.substring(0, _rootNodeCondition.indexOf(" in")).trim(); //
				String str_value = _rootNodeCondition.substring(_rootNodeCondition.indexOf(" in") + 3, _rootNodeCondition.length()).trim(); //
				str_value = tbUtil.replaceAll(str_value, "(", ""); //去掉左括号
				str_value = tbUtil.replaceAll(str_value, ")", ""); //去掉右括号
				str_value = tbUtil.replaceAll(str_value, "'", ""); //去掉单引号
				str_values = tbUtil.split(str_value, ","); //根据逗号进行分割!!
			} else if (_rootNodeCondition.indexOf(" like ") > 0) { //如果有Like机制,比如【扩展机构类型 like '%;法规部;%'】
				//以后有时间实现....
			}

			//
			ArrayList al_return = new ArrayList(); //
			if (str_key != null) {
				DefaultMutableTreeNode[] allNodes = getOneNodeAllChildrenNodes(rootNode); //所有子结点,包括要结点
				HashVO hvo_temp = null; //
				for (int i = 1; i < allNodes.length; i++) { //遍历所有结点!
					hvo_temp = (HashVO) allNodes[i].getUserObject(); //取得结点的值
					String str_itemValue = hvo_temp.getStringValue(str_key); //该项的值!
					if (str_itemValue != null && tbUtil.isExistInArray(str_itemValue, str_values)) { //如果该结点数据满足二次过滤条件中中指定的数组范围中！！！则找出其所有子结点...
						DefaultMutableTreeNode[] allChildrenNodes = getOneNodeAllChildrenNodes(allNodes[i]); //找出该结点下的所有子结点,包括自己
						for (int j = 0; j < allChildrenNodes.length; j++) {
							HashVO hvo_children = (HashVO) allChildrenNodes[j].getUserObject(); //取得结点的值
							hvo_children.setAttributeValue("$level", "" + allChildrenNodes[j].getLevel()); //加入级层伪列!!还需要加入父亲路径结点的ID的路径!!以";"相隔!
							String[] str_parentPathIdNames = bsUtil.getOneTreeNodeParentPathItemValue(allChildrenNodes[j], new String[] { _idField, "name" }); //父系路径的所有结点的id拼在一起!!
							hvo_children.setAttributeValue("$parentpathids", str_parentPathIdNames[0]); //加入级层伪列
							hvo_children.setAttributeValue("$parentpathnames", str_parentPathIdNames[1]); //加入级层伪列
							hvo_children.setAttributeValue("$parentpathnamelink", getPathNameSpans(tbUtil, str_parentPathIdNames[1], false)); //加入级层伪列，是【兴业银行-上海分行-法规部】的样子
							hvo_children.setAttributeValue("$parentpathnamelink1", getPathNameSpans(tbUtil, str_parentPathIdNames[1], true)); //加入级层伪列，是【上海分行-法规部】的样子即自动截取掉第一层!因为第一层是废话,
							if (allChildrenNodes[j].isLeaf()) {
								hvo_children.setAttributeValue("$isleaf", "Y"); //如果是叶子结点!!则加入标记
							}
							if (!al_return.contains(hvo_children)) { //如果没有加入过,则加入,否则可能产生重复情况!!
								al_return.add(hvo_children); //
							}
						}
					}
				}
			}
			return (HashVO[]) al_return.toArray(new HashVO[0]); //
		}
	}

	private String getPathNameSpans(TBUtil _tbUtil, String _names, boolean _isTrim1) {
		if (_names == null || _names.trim().equals("")) {
			return "";
		}
		StringBuilder sb_names = new StringBuilder(); //
		String[] str_nameItems = _tbUtil.split(_names, ";"); //
		int li_startLevel = 0; //默认是全的,
		if (_isTrim1) { //如果要截掉第一层,则从1开始!!
			li_startLevel = 1;
		}
		for (int k = li_startLevel; k < str_nameItems.length; k++) { //从第一位开始,即去掉兴业银行!!
			sb_names.append(str_nameItems[k]); //
			if (k != str_nameItems.length - 1) {
				sb_names.append("-"); //
			}
		}
		return sb_names.toString(); //
	}

	//取得当前线程堆栈说明
	private String getCurrThreadTrace() {
		StackTraceElement stackTrace[] = Thread.currentThread().getStackTrace(); ////得到调用堆栈...
		StringBuffer sb_trace = new StringBuffer(); //
		int li_count = 1;
		int i = 0;
		HashSet hs_tmp = new HashSet(); //
		for (i = 0; i < stackTrace.length; i++) {
			String str_calssname = stackTrace[i].getClassName();
			int li_pos = str_calssname.lastIndexOf(".");
			String str_realclsname = str_calssname.substring(li_pos + 1, str_calssname.length());
			if (str_calssname.startsWith("java.") || str_calssname.startsWith("sun.") || str_realclsname.equals("Thread") || str_realclsname.startsWith("$") || str_realclsname.equals("NativeMethodAccessorImpl")) {
				continue; //
			}
			if (hs_tmp.contains(str_realclsname + "." + stackTrace[i].getMethodName())) {
				continue; //
			} else {
				hs_tmp.add(str_realclsname + "." + stackTrace[i].getMethodName()); //
			}
			sb_trace.append((new StringBuilder(String.valueOf("[" + i + "]" + str_realclsname))).append(".").append(stackTrace[i].getMethodName()).append("(").append(stackTrace[i].getLineNumber()).append(")").append("\u2190").toString());
			if (++li_count > 5)
				break;
		}
		return "StackTrace:" + sb_trace.toString() + "...<" + (stackTrace.length - i) + ">...";
	}

	/**
	 * 根据某一条记录去表型结构表找到父亲路径的所有记录!!它与上一种方法的区别是
	 * 上一种方法是一下子取出所有数据,然后在Java中计算!
	 * 而这一种方法是一条条的找,每次都查一次数据库!!因为一般一个树型结构的层次不会超过5层!!所以性能够不会太低!!!
	 * @param _datasourcename
	 * @param _tableName
	 * @param _idFieldName
	 * @param _parentIdFieldName
	 * @param _whereField
	 * @param _whereCondition
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getTreePathVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereField, String _whereCondition) throws Exception {
		HashVO[] hvsLeaf = getHashVoArrayByDS(null, "select * from " + _tableName + " where " + _whereField + "='" + _whereCondition + "'"); ////
		if (hvsLeaf == null || hvsLeaf.length <= 0) {
			return null;
		}
		BSUtil bsUtil = new BSUtil(); //
		HashVO[] parentVOs = bsUtil.getTreePathVOsByOneRecord(hvsLeaf[0], _tableName, _idFieldName, _parentIdFieldName); //取得所有父亲路径的的记录!!
		return parentVOs; //
	}

	/**
	 * 根据一批记录取得树型结构的
	 * @param _datasourcename
	 * @param _tableName
	 * @param _idFieldName
	 * @param _parentIdFieldName
	 * @param _whereCondition
	 * @return
	 * @throws Exception
	 */
	public HashMap getTreePathNameByRecords(String _datasourcename, String _tableName, String _idFieldName, String _nameFieldName, String _parentIdFieldName, String[] _idValues) throws Exception {
		HashVO[] hvs = getHashVoArrayAsTreeStructByDS(_datasourcename, "select " + _idFieldName + "," + _nameFieldName + "," + _parentIdFieldName + " from " + _tableName, _idFieldName, _parentIdFieldName, null, null); //
		HashMap allMap = new HashMap(); //
		for (int i = 0; i < hvs.length; i++) {
			allMap.put(hvs[i].getStringValue(_idFieldName), hvs[i].getStringValue("$parentpathnamelink1")); //
		}

		HashMap returnMap = new HashMap(); //
		for (int i = 0; i < _idValues.length; i++) {
			returnMap.put(_idValues[i], (String) allMap.get(_idValues[i])); //
		}
		return returnMap; //
	}

	/**
	 * 找到一条记录的所有子孙结点!每次都取数据库!!!
	 * 即层次不会太多! 
	 * @param _datasourcename
	 * @param _tableName
	 * @param _idFieldName
	 * @param _parentIdFieldName
	 * @param _whereField
	 * @param _whereCondition
	 * @return
	 */
	public HashVO[] getTreeChildVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereCondition) throws Exception {
		HashVO[] hvsRoot = getHashVoArrayByDS(_datasourcename, "select * from " + _tableName + " where 1=1  " + (_whereCondition == null ? "" : " and (" + _whereCondition + ")")); //查询SQL
		ArrayList al_result = new ArrayList(); ////
		addAllChildren(_datasourcename, hvsRoot, _tableName, _idFieldName, _parentIdFieldName, al_result); //递归调用,找出所有子孙!!!
		return (HashVO[]) al_result.toArray(new HashVO[0]); ////
	}

	//递归函数,找出所有子孙结点!!
	private void addAllChildren(String _datasourcename, HashVO[] _hvsRoots, String _tableName, String _idFieldName, String _parentIdFieldName, ArrayList _al_result) throws Exception {
		for (int i = 0; i < _hvsRoots.length; i++) {
			_al_result.add(_hvsRoots[i]); //
		}
		TBUtil tbUtil = new TBUtil(); //
		ArrayList al_ids = new ArrayList(); //
		for (int i = 0; i < _hvsRoots.length; i++) {
			String str_id = _hvsRoots[i].getStringValue(_idFieldName); //主键
			al_ids.add(str_id); //加入主键!!
		}
		HashVO[] hvsChildren = getHashVoArrayByDS(null, "select * from " + _tableName + " where " + _parentIdFieldName + " in (" + tbUtil.getInCondition(al_ids) + ")"); //找出我的儿子
		if (hvsChildren == null || hvsChildren.length == 0) { //如果一个儿子没有,则直接返回!!!
			return;
		}
		addAllChildren(_datasourcename, hvsChildren, _tableName, _idFieldName, _parentIdFieldName, _al_result); //加入子孙结点!!
	}

	/**
	 * 取得某个结点的所有子结点..
	 * @param _node
	 * @return
	 */
	private DefaultMutableTreeNode[] getOneNodeAllChildrenNodes(TreeNode _node) {
		ArrayList vector = new ArrayList();
		visitAllNodes(vector, _node); //
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //遍历所有结点..
		return allNodes; //
	}

	private void visitAllNodes(ArrayList _vector, TreeNode node) {
		_vector.add(node); // 加入该结点
		if (node.getChildCount() >= 0) { //如果有子结点,则遍历每个子结点，递归调用本方法
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // 找到该儿子!!
				visitAllNodes(_vector, childNode); // 继续查找该儿子
			}
		}
	}

	/**
	 * 通过几个子查询SQL,来实现外边接效果，即不使用left outer join的SQL语法了，而是在Java中计算实现同样的效果
	 * @param _datasourcename
	 * @param _parentsql
	 * @param _childsqls
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getHashVoArrayBySubSQL(String _datasourcename, String _parentsql, String _childsqls[]) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); //
		}

		HashVO hvs_parent[] = getHashVoArrayByDS(_datasourcename, _parentsql); //
		TBUtil tbutil = new TBUtil();
		HashVO hvs_child[] = (HashVO[]) null;
		for (int i = 0; i < hvs_parent.length; i++) {
			if (_childsqls != null) {
				for (int j = 0; j < _childsqls.length; j++) {
					String str_childsql = _childsqls[j];
					int li_pos_1 = str_childsql.indexOf("{");
					int li_pos_2 = str_childsql.indexOf("}");
					if (li_pos_1 < 0 || li_pos_2 < 0) {
						throw new WLTAppException((new StringBuilder("执行子查询 SQL[")).append(_childsqls[j]).append("] 格式不对,没有定义宏代码段,请检查!").toString());
					}
					String str_itemfield = str_childsql.substring(li_pos_1 + 1, li_pos_2);
					String str_parentItemValue = hvs_parent[i].getStringValue(str_itemfield);
					if (str_parentItemValue == null || str_parentItemValue == "") {
						//str_parentItemValue = "";

					} else {
						str_childsql = tbutil.replaceAll(str_childsql, (new StringBuilder("{")).append(str_itemfield).append("}").toString(), str_parentItemValue);
						hvs_child = getHashVoArrayByDS(_datasourcename, str_childsql); //
					}
					if (hvs_child != null && hvs_child.length > 0) {
						String str_keys[] = hvs_child[0].getKeys();
						for (int k = 0; k < str_keys.length; k++) { //
							hvs_parent[i].setAttributeValue((new StringBuilder(String.valueOf(str_itemfield))).append("_").append(str_keys[k]).toString(), hvs_child[0].getStringValue(str_keys[k])); //
						}
					}
				}
			}
		}
		return hvs_parent; //
	}

	/**
	 * 通过几个子查询SQL,来实现外边接效果，即不使用left outer join的SQL语法了，而是在Java中计算实现同样的效果
	 * @param _datasourcename
	 * @param _parentsql
	 * @param _childVOs
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getHashVoArrayBySubSQL(String _datasourcename, String _parentsql, LinkForeignTableDefineVO _childVOs[]) throws Exception {
		if (_datasourcename == null)
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); //
		HashVO hvs_parent[] = getHashVoArrayByDS(_datasourcename, _parentsql); //
		HashMap map_parentfieldvalue = null; //
		HashVOStruct obj_childresults[] = new HashVOStruct[_childVOs.length]; //
		for (int j = 0; j < _childVOs.length; j++) { //
			String str_foreignFieldname = _childVOs[j].getForeignField(); //
			String str_parentfieldname = _childVOs[j].getParentField(); //
			map_parentfieldvalue = new HashMap();
			for (int k = 0; k < hvs_parent.length; k++)
				if (hvs_parent[k].getStringValue(str_parentfieldname) != null) {
					map_parentfieldvalue.put(hvs_parent[k].getStringValue(str_parentfieldname), "1"); //
				}
			String str_allparentfieldvalues[] = (String[]) map_parentfieldvalue.keySet().toArray(new String[0]);
			if (str_allparentfieldvalues != null && str_allparentfieldvalues.length > 0) {
				String str_childsql = _childVOs[j].getSQL(str_allparentfieldvalues);
				HashVOStruct hvstruct_child = getHashVoStructByDS(_datasourcename, str_childsql);
				obj_childresults[j] = hvstruct_child;
			}
		}

		for (int j = 0; j < _childVOs.length; j++) { //遍历每个子查询
			String str_foreignFieldname = _childVOs[j].getForeignField(); //
			String str_parentfieldname = _childVOs[j].getParentField(); //
			for (int p = 0; p < hvs_parent.length; p++) { //遍历父记录
				if (hvs_parent[p].getStringValue(str_parentfieldname) != null) { //如果父亲的值不为空
					String str_childcolumnkeys[] = obj_childresults[j].getHeaderName(); //
					HashVO hvo_child[] = obj_childresults[j].getHashVOs();
					for (int q = 0; q < hvo_child.length; q++) { //遍历所有子查询记录
						if (!hvo_child[q].getStringValue(str_foreignFieldname).equals(hvs_parent[p].getStringValue(str_parentfieldname))) { //如果没品配上
							continue;
						}

						for (int r = 0; r < str_childcolumnkeys.length; r++) {
							if (!str_childcolumnkeys[r].equalsIgnoreCase(str_foreignFieldname)) {
								hvs_parent[p].setAttributeValue((new StringBuilder(String.valueOf(str_parentfieldname))).append("_").append(str_childcolumnkeys[r]).toString(), hvo_child[q].getStringValue(str_childcolumnkeys[r]));
							}
						}

						break;
					}

				}
			}
		}

		return hvs_parent;
	}

	public Vector getHashVoArrayReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}

		Vector vector = new Vector(); //
		for (int i = 0; i < _sqls.length; i++) {
			HashVO[] hvs = getHashVoArrayByDS(_datasourcename, _sqls[i]);
			vector.add(hvs); //
		}
		return vector;
	}

	public Vector getHashVoStructReturnVectorByDS(String _datasourcename, String[] _sqls) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		Vector vector = new Vector(); //
		for (int i = 0; i < _sqls.length; i++) {
			HashVOStruct hvstruct = getHashVoStructByDS(_datasourcename, _sqls[i]);
			vector.add(hvstruct); //
		}
		return vector;
	}

	public HashMap getHashVoArrayReturnMapByDS(String _datasourcename, String[] _sqls, String[] _keys) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}

		HashMap map = new HashMap(); //
		for (int i = 0; i < _sqls.length; i++) {
			HashVO[] hvs = getHashVoArrayByDS(_datasourcename, _sqls[i]);
			map.put(_keys[i], hvs); //
		}
		return map; //
	}

	/**
	 * 执行一条SQL
	 * @param _datasourcename
	 * @param _sqlBuilder
	 * @return
	 * @throws Exception
	 */
	public int executeUpdateByDS(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws Exception {
		return executeUpdateByDS(_datasourcename, _sqlBuilder.toString()); //
	}

	/**
	 * 执行Update语句!!!!!
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public int executeUpdateByDS(String _datasourcename, String _sql) throws Exception {
		return executeUpdateByDS(_datasourcename, _sql, true); //
	}

	/**
	 * 执行一条SQL!
	 * 
	 * @param _sql
	 * @throws Exception
	 */
	public int executeUpdateByDS(String _datasourcename, String _sql, boolean _isDebugLog) throws Exception {
		return executeBatchByDS(_datasourcename, new String[] { _sql }, _isDebugLog)[0]; //还是转调批量SQL的方法!!
	}

	/**
	 * 执行一条SQL!
	 * 
	 * @param _sql
	 * @throws Exception
	 */
	public int executeUpdateByDSByMacro(String _datasourcename, String _sql, String[] _macro) throws Exception {
		WLTDBConnection conn = null;
		PreparedStatement p_stmt = null; //

		// long ll_1 = System.currentTimeMillis();
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}
			addSQLToSessionSQLListener(_sql); //向SQL监听器中增加本SQL
			conn = new WLTInitContext().getConn(_datasourcename); // 取得数据库连接!
			WLTLogger.getLogger(this).debug(_sql); //
			p_stmt = conn.prepareStatement(_sql); // 创建游标,凡是一次远程调用都是在同一事务中进行,即取得的是同一个Connection!!
			for (int i = 0; i < _macro.length; i++) {
				p_stmt.setString(i + 1, _macro[i]); //执行宏..
			}
			int li_count = p_stmt.executeUpdate(); // 修改
			// long ll_2 = System.currentTimeMillis();
			return li_count; //
		} catch (SQLException ex) {
			// ex.printStackTrace(); //
			throw new SQLException("在数据源[" + _datasourcename + "]上执行下列SQL出错:\r\n" + _sql + "\r\n错误编码[" + ex.getErrorCode() + "]\r\n状态[" + ex.getSQLState() + "]\r\n原因:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("在数据源[" + _datasourcename + "]执行SQL:\r\n" + _sql + "\r\n出错,原因:" + ex.getMessage());
		} finally {
			try {
				if (p_stmt != null) {
					p_stmt.close(); // 关闭游标
				}
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 执行宏代码
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public int executeMacroUpdateByDS(String _datasourcename, String _sql, String[] _macrovalues) throws Exception {
		WLTDBConnection conn = null;
		PreparedStatement p_stmt = null; //

		// long ll_1 = System.currentTimeMillis();
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}
			addSQLToSessionSQLListener(_sql); //向SQL监听器中增加本SQL
			conn = new WLTInitContext().getConn(_datasourcename); // 取得数据库连接!
			WLTLogger.getLogger(this).debug(_sql); //
			p_stmt = conn.prepareStatement(_sql); // 创建游标,凡是一次远程调用都是在同一事务中进行,即取得的是同一个Connection!!
			if (_macrovalues != null) {
				for (int i = 0; i < _macrovalues.length; i++) {
					Reader clobReader = new StringReader(_macrovalues[i]);
					p_stmt.setCharacterStream(i + 1, clobReader, _macrovalues[i].length()); // 设置字符串!!!
				}
			}

			int li_count = p_stmt.executeUpdate(); // 修改
			// long ll_2 = System.currentTimeMillis();
			return li_count; //
		} catch (SQLException ex) {
			// ex.printStackTrace(); //
			throw new SQLException("在数据源[" + _datasourcename + "]上执行下列SQL出错:\r\n" + _sql + "\r\n错误编码[" + ex.getErrorCode() + "]\r\n状态[" + ex.getSQLState() + "]\r\n原因:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("在数据源[" + _datasourcename + "]执行SQL:\r\n" + _sql + "\r\n出错,原因:" + ex.getMessage());
		} finally {
			try {
				if (p_stmt != null) {
					p_stmt.close(); // 关闭游标
				}
			} catch (Exception ex) {
			}
		}
	}

	//降低事务隔离级别的提交
	public int executeUpdateByDSAutoCommit(String _datasourcename, String _sql) throws Exception {
		return executeUpdateByDSImmediately(_datasourcename, _sql, true, true); //
	}

	/**
	 * 立即提交一个SQL对象
	 * @param _datasourcename
	 * @param _sqlBuilder
	 * @return
	 * @throws Exception
	 */
	public int executeUpdateByDSImmediately(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws Exception {
		return executeUpdateByDSImmediately(_datasourcename, _sqlBuilder.toString()); //
	}

	public int executeUpdateByDSImmediately(String _datasourcename, String _sql) throws Exception {
		return executeUpdateByDSImmediately(_datasourcename, _sql, true, false); //
	}

	public int executeUpdateByDSImmediately(String _datasourcename, String _sql, boolean _isDebugLog) throws Exception {
		return executeUpdateByDSImmediately(_datasourcename, _sql, _isDebugLog, false); //
	}

	/**
	 * 立即开一个新事务,独立提交一条SQL,适用于后台的任务..或生成主键时使用!!! 该方法不抛露给客户端,因为客户端没必要用这个方法
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @throws Exception
	 */
	public int executeUpdateByDSImmediately(String _datasourcename, String _sql, boolean _isDebugLog, boolean _autoCommit) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		Connection conn = null;
		Statement stmt = null;
		try {
			addSQLToSessionSQLListener(_sql); //向SQL监听器中增加本SQL
			String str_dburl = "jdbc:apache:commons:dbcp:" + _datasourcename; // //
			DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
			if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) {
				WLTDBConnection wltc = new WLTDBConnection(_datasourcename);
				conn = wltc.getConn();
			} else {
				conn = DriverManager.getConnection(str_dburl); // //真正创建连接,其实也是从dbcp池中取的!!!
			}
			//★★★★强烈提醒:以前这里也是事务级别,但实际是更多的一条语句提交是不需要锁的★★★★★★
			//在邮储项目压力测试中就发现,update pub_user set DESKTOPSTYLE='2' where id=21,总是行锁!
			//原因就在于,压力测试里总是同一个用户,然后修改修改的同一条记录,然后又是事务级别,所以产生性能!
			//实际上这条SQL根本不需要事务
			//TRANSACTION_READ_COMMITTED(有事务) TRANSACTION_READ_UNCOMMITTED(无事务)
			if (_autoCommit) {
				//conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED); //降低事务隔离级别!
				conn.setAutoCommit(true); //立即提交
			} else {
				conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED);
				conn.setAutoCommit(false); // 在这里定死了!!
			}

			if (_isDebugLog) {
				WLTLogger.getLogger(this).debug(_sql); //
			}
			boolean isPrepareSQL = "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")); //
			int li_count = 0; //
			if (!isPrepareSQL) {
				stmt = conn.createStatement(); // 创建游标,凡是一次远程调用都是在同一事务中进行,即取得的是同一个Connection!!
				li_count = stmt.executeUpdate(_sql); // 修改
			} else {
				PrepareSQLUtil preSQLUtil = new PrepareSQLUtil(); //
				ArrayList al_pres = preSQLUtil.prepareSQL(_sql); //ArrayL
				if (al_pres == null || al_pres.size() <= 1) { //如果解析失败!!
					stmt = conn.createStatement(); // 创建游标,凡是一次远程调用都是在同一事务中进行,即取得的是同一个Connection!!
					li_count = stmt.executeUpdate(_sql); // 修改
				} else {
					String str_preSQL = (String) al_pres.get(0); //第一条是SQL
					stmt = conn.prepareStatement(str_preSQL); //预编译!!!
					for (int i = 1; i < al_pres.size(); i++) {
						((PreparedStatement) stmt).setObject(i, al_pres.get(i)); //
					}
					li_count = ((PreparedStatement) stmt).executeUpdate(); //
				}
			}
			conn.commit(); // 立即提交,最关键!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			return li_count; //
		} catch (SQLException ex) {
			try {
				conn.rollback();
			} catch (Exception exx) {
			}
			// e.printStackTrace(); //
			throw new SQLException("在数据源[" + _datasourcename + "]上独立事务执行下列SQL出错:\r\n" + _sql + "\r\n错误编码[" + ex.getErrorCode() + "]\r\n状态[" + ex.getSQLState() + "]\r\n原因:" + ex.getMessage());
		} catch (Exception ex) {
			// e.printStackTrace(); //
			throw new Exception("在数据源[" + _datasourcename + "]上独立事务执行SQL:\r\n" + _sql + "\r\n出错,原因:" + ex.getMessage()); //
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception ex) {
				}
			}

			if (conn != null) {
				try {
					conn.close(); //
				} catch (Exception ex) {
				}
			}
		}
	}

	public void executeInsertData(String _datasourcename, String _tablename, HashVOStruct _fromhvs) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}

		WLTDBConnection conn = null;
		PreparedStatement p_stmt = null; //

		HashVOStruct _tohvs = getHashVoStructByDS(_datasourcename, "select * from " + _tablename + " where 1=2");

		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}

			conn = new WLTInitContext().getConn(_datasourcename); // 取得数据库连接!

			StringBuffer sql_insert = new StringBuffer();

			String[] str_allkeys = _tohvs.getHeaderName();
			String[] str_alltypes = _tohvs.getHeaderTypeName(); //
			HashVO[] hvs_values = _fromhvs.getHashVOs(); //

			sql_insert.append("insert into ").append(_tablename).append("( ");

			for (int i = 0; i < str_allkeys.length; i++) {

				sql_insert.append(str_allkeys[i]);
				if (i != str_allkeys.length - 1) {
					sql_insert.append(",");
				}
			}

			sql_insert.append(" ) values ( ");

			for (int i = 0; i < str_allkeys.length; i++) {

				sql_insert.append("?");
				if (i != str_allkeys.length - 1) {
					sql_insert.append(",");
				}
			}

			sql_insert.append(" )");

			addSQLToSessionSQLListener(sql_insert.toString()); //向SQL监听器中增加本SQL
			p_stmt = conn.prepareStatement(sql_insert.toString());

			for (int i = 0; i < hvs_values.length; i++) {
				for (int j = 0; j < str_allkeys.length; j++) {

					if (hvs_values[i].getStringValue(str_allkeys[j]) == null || hvs_values[i].getStringValue(str_allkeys[j]).trim().length() == 0) {
						p_stmt.setString(j + 1, "");
					} else {
						if (str_alltypes[j].indexOf("CHAR") > -1) {
							p_stmt.setString(j + 1, hvs_values[i].getStringValue(str_allkeys[j]));
						} else {
							if (str_alltypes[j].indexOf("NUMBER") > -1) {
								p_stmt.setBigDecimal(j + 1, new BigDecimal(hvs_values[i].getStringValue(str_allkeys[j])));
							} else {
								if (str_alltypes[j].indexOf("CLOB") > -1) {
									String value = hvs_values[i].getStringValue(j);
									if (value.length() > 2000) {
										p_stmt.setString(j + 1, value.substring(0, 2000));
									} else {
										p_stmt.setString(j + 1, value);
									}
									// p_stmt.setString(j + 1, value);
									// p_stmt.setClob(j + 1, value);
									// p_stmt.setObject(j + 1,
									// value.substring(0,2000));
									// p_stmt.setBytes(j + 1,
									// (Clob)value.getBytes());
									// InputStream ip = new
									// StringBufferInputStream(value);
									// p_stmt.setBinaryStream(j + 1, ip,
									// value.length());

								} else {
								}

							}
						}
					}

				}
				p_stmt.execute();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (p_stmt != null) {
					p_stmt.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 一次性执行一批SQL
	 * @param _datasourcename
	 * @param _sqlBuilders
	 * @throws Exception
	 */
	public int[] executeBatchByDS(String _datasourcename, SQLBuilderIfc[] _sqlBuilders) throws Exception {
		String[] str_sqls = new String[_sqlBuilders.length]; //
		for (int i = 0; i < str_sqls.length; i++) {
			str_sqls[i] = _sqlBuilders[i].getSQL(); //
		}
		return executeBatchByDS(_datasourcename, str_sqls); //
	}

	//批量执行SQL
	public int[] executeBatchByDS(String _datasourcename, String[] _sqls) throws Exception {
		return executeBatchByDS(_datasourcename, _sqls, true); //
	}

	/**
	 * 一次性执行一批SQL
	 * @param _datasourcename
	 * @param _sqls
	 * @throws Exception
	 */
	public int[] executeBatchByDS(String _datasourcename, String[] _sqls, boolean _isDebugLog) throws Exception {
		return executeBatchByDS(_datasourcename, Arrays.asList(_sqls), _isDebugLog); ////
	}

	////
	public void executeBatchByDSNoLog(String _datasourcename, String _sqls) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		java.util.List list = new ArrayList();
		if (checkIfSql(_sqls)) {
			list = Arrays.asList(_sqls.split(";")); // 转换成一个List
		} else {
			list = Arrays.asList(new TBUtil().convertHexStringToStr(_sqls).split(";")); // 转换成一个List
		}
		executeBatchByDS(_datasourcename, list, false); //
	}

	/**
	 * 批量执行SQL
	 * @param _datasourcename
	 * @param _sqllist
	 * @param _isDebugLog
	 * @throws Exception
	 */
	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist) throws Exception {
		executeBatchByDS(_datasourcename, _sqllist, true); //
	}

	/**
	 * 根据一个SQL来分析记录数据库的日志
	 * @param _sql
	 * @return
	 */
	private String[] getDBOperateLogInfo(String _sql, HashMap _dbTriggetTableMap) {
		String str_sql = _sql.toLowerCase().trim(); //
		String str_tablename = null; //直接处理的表名
		String str_tabledesc = null; //
		String str_itemValue = null; //旧值.
		if (str_sql.startsWith("insert") && str_sql.indexOf(",") > 0) { //必须有值!!
			str_sql = str_sql.substring(str_sql.indexOf("into") + 4, str_sql.length()).trim(); //从insert into后面开始...
			str_tablename = str_sql.substring(0, str_sql.indexOf("(")).trim().toLowerCase(); //表名
			str_tabledesc = (String) _dbTriggetTableMap.get(str_tablename); //
			if (str_sql.indexOf("values") > 0) {
				str_sql = str_sql.substring(str_sql.indexOf("values") + 5, str_sql.length()); //
				str_sql = str_sql.substring(str_sql.indexOf("(") + 1, str_sql.indexOf(",")); //
				if (str_sql.startsWith("'")) {
					str_sql = str_sql.substring(1, str_sql.length()); //
				}
				if (str_sql.endsWith("'")) {
					str_sql = str_sql.substring(0, str_sql.length() - 1); //
				}
				str_itemValue = str_sql; //
			}
			return new String[] { _sql, str_tablename, str_tabledesc, "新增", str_itemValue }; ////
		} else if (str_sql.startsWith("update")) {
			str_sql = str_sql.substring(str_sql.indexOf(" "), str_sql.length()).trim(); //从insert into后面开始...
			str_tablename = str_sql.substring(0, str_sql.indexOf(" ")).toLowerCase(); //表名
			str_tabledesc = (String) _dbTriggetTableMap.get(str_tablename); //
			if (str_sql.indexOf("where") > 0) {
				str_sql = str_sql.substring(str_sql.indexOf("where") + 5, str_sql.length()).trim(); //
				if (str_sql.startsWith("id='")) { //必须后面紧跟着id
					str_itemValue = str_sql.substring(4, str_sql.length() - 1); //
				}
			}

			if (str_tablename != null) {
				str_tablename = str_tablename.trim(); //
			}

			if (str_itemValue != null) {
				str_itemValue = str_itemValue.trim(); //
			}
			//如果只是修改皮肤,则不处理,发后要搞个map定义!先这样临时搞下 ,邮储项目, xch/2012-09-13
			String[] str_filterSQL = new String[] { "update pub_user set desktopstyle", "update pub_user set lookandfeeltype" }; //这两条SQL是登录时做的!不写日志!
			for (int i = 0; i < str_filterSQL.length; i++) { //
				if (_sql.toLowerCase().startsWith(str_filterSQL[i])) {
					//System.out.println("特定的SQL不写日志!!"); //
					return null; //
				}

			}
			return new String[] { _sql, str_tablename, str_tabledesc, "修改", str_itemValue }; ////
		} else if (str_sql.startsWith("delete")) {
			str_sql = str_sql.substring(str_sql.indexOf(" "), str_sql.length()).trim(); //从insert into后面开始...
			if (str_sql.startsWith("from")) {
				str_sql = str_sql.substring(str_sql.indexOf("from") + 4, str_sql.length()).trim(); //从insert into后面开始...
			}
			if (str_sql.contains(" ")) {
				str_tablename = str_sql.substring(0, str_sql.indexOf(" ")).toLowerCase(); //表名
				if (str_sql.indexOf("where") > 0) {
					str_sql = str_sql.substring(str_sql.indexOf("where") + 5, str_sql.length()).trim(); //
					if (str_sql.startsWith("id='")) { //必须后面紧跟着id
						str_itemValue = str_sql.substring(4, str_sql.length() - 1); //
					}
				}
			} else {
				str_tablename = str_sql.toLowerCase(); //表名
			}

			str_tabledesc = (String) _dbTriggetTableMap.get(str_tablename); //
			return new String[] { _sql, str_tablename, str_tabledesc, "删除", str_itemValue }; ////
		}
		return null; //
	}

	/**
	 * 一次性执行一批SQL
	 * @param _datasourcename
	 * @param _sqllist
	 * @param _isDebugLog
	 * @throws Exception
	 */
	public int[] executeBatchByDS(String _datasourcename, java.util.List _sqllist, boolean _isDebugLog) throws Exception {
		return executeBatchByDS(_datasourcename, _sqllist, _isDebugLog, true);
	}

	public int[] executeBatchByDS(String _datasourcename, java.util.List _sqllist, boolean _isDebugLog, boolean _isDBLog) throws Exception {
		return executeBatchByDS(_datasourcename, _sqllist, _isDebugLog, _isDBLog, false);
	}

	public int[] executeBatchByDS(String _datasourcename, java.util.List _sqllist, boolean _isDebugLog, boolean _isDBLog, boolean _isPrePareSQL) throws Exception {
		WLTDBConnection conn = null;
		Statement p_stmt = null; // ..
		PreparedStatement p_stmt_log = null; //日志主表!!
		PreparedStatement p_stmt_log_b = null; // ..

		// long ll_1 = System.currentTimeMillis();
		int[] li_dealRecords = null; //
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}
			ArrayList al_dbopelog_all = new ArrayList(); //记录插入数据库的日志
			ArrayList al_dbopelog_log = new ArrayList(); //记录插入数据库的日志
			HashMap triggerTablesMap = ServerEnvironment.getDBTriggerTableMap(); //处理日志,平台参数配置的触发器方式监听的表
			boolean isPrepareSQL = "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")); //是否预编译SQL,如果是Y,则才处理!!
			PrepareSQLUtil preSQLUtil = null; //
			boolean isRealPreSQL = (_isPrePareSQL || isPrepareSQL) && (_sqllist.size() < 100); //是否真正进行预编译?如果强行指定了,或者系统参数定义了!!同时必须小于100条SQL,因为大于100条SQL需要创建100个statement,而一个conn是不允许超过300个的!
			conn = getConn(_datasourcename); //创建数据库连接!!!
			//System.out.println("is Auto Commit:" + conn.getConn().getAutoCommit() +",Trans Level:" +  conn.getConn().getTransactionIsolation());  //
			if (isRealPreSQL) { //如果要进行预编译,则创建工具类!
				preSQLUtil = new PrepareSQLUtil(); //
			} else {
				p_stmt = conn.createStatement(); //只有一条游标!!
			}
			//遍历各条SQL!!!
			ArrayList al_result = new ArrayList(); //
			for (int i = 0; i < _sqllist.size(); i++) {
				Object objsql = _sqllist.get(i);
				if (objsql == null) { //如果为空,则跳过!!!因为可能前台拼接SQL时某一条会为null
					continue;
				}
				String sql = null; //
				if (objsql instanceof String) {
					sql = (String) objsql;
				} else if (objsql instanceof SQLBuilderIfc) {
					sql = ((SQLBuilderIfc) objsql).getSQL();
				} else {
					sql = "" + objsql;
				}
				addSQLToSessionSQLListener(sql); //向SQL监听器中增加本SQL
				if (_isDebugLog) { //是否输出日志!!!有时是不想输出日志的!!!
					WLTLogger.getLogger(this).debug(sql); //
				}
				if (!isRealPreSQL) { //如果不是预编译的,则直接批处理!!
					p_stmt.addBatch(sql); // 批增加!!!
				} else { //如果是预编译!
					ArrayList al_presqls = preSQLUtil.prepareSQL(sql); //解析SQL
					if (al_presqls == null || al_presqls.size() <= 1) { //如果为空,即可能解析失败!或者根本没有参数!!,则直接使用标准的stmt
						Statement itemStmt = conn.createStatement(); //创建一个游标!!
						int li_result = itemStmt.executeUpdate(sql); //直接执行
						al_result.add(new Integer(li_result)); //
						itemStmt.close(); //
						//psList.add();  //加入!!
					} else { //如果解析成功!!
						String str_preSQL = (String) al_presqls.get(0); //第一条是 SQL
						PreparedStatement itemPstmt = conn.prepareStatement(str_preSQL); //
						for (int j = 1; j < al_presqls.size(); j++) { //
							itemPstmt.setObject(j, al_presqls.get(j)); //
						}
						int li_result = itemPstmt.executeUpdate(); //直接执行!!!
						al_result.add(new Integer(li_result)); //
						itemPstmt.close(); //直接关闭!!!
					}
				}
				if (_isDBLog && triggerTablesMap.size() > 0) { //如果需要记日志并且又存在需要记日志的表，则解析出日志sql
					String[] str_opetab = getDBOperateLogInfo(sql, triggerTablesMap); //解析SQL!!关键逻辑!!!
					if (str_opetab != null) { //如果取到了!!!
						str_opetab[0] = getTBUtil().subStrByGBLength(str_opetab[0], 3960); //如果此表被监控到操作日志，sql不能过长。数据量过大的表不建议监控。 [郝明2012-06-06]
						al_dbopelog_all.add(str_opetab); //String[] { _sql, str_tablename, str_tabledesc, "删除", str_itemValue }; ////
						if (triggerTablesMap.containsKey(str_opetab[1])) { //如果定义了需要日志处理,则存储!
							al_dbopelog_log.add(str_opetab); //
						}
					}
				}
			}

			//查下旧数据!!!
			VectorMap[] vmap = null;
			if (al_dbopelog_log.size() > 0) { //
				vmap = new VectorMap[al_dbopelog_log.size()]; //
				for (int i = 0; i < al_dbopelog_log.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_log.get(i); ////
					if ((str_itemOpeLog[3].equals("修改") || str_itemOpeLog[3].equals("删除")) && str_itemOpeLog[4] != null) {
						String str_sql_temp = "select * from " + str_itemOpeLog[1] + " where id='" + str_itemOpeLog[4] + "'"; //
						HashVO[] hvos = getHashVoArrayByDS(_datasourcename, str_sql_temp); //
						if (hvos.length > 0) {
							vmap[i] = new VectorMap(); //
							String[] str_keys = hvos[0].getKeys(); //
							for (int j = 0; j < str_keys.length; j++) {
								vmap[i].put(str_keys[j].toLowerCase(), new String[] { hvos[0].getStringValue(str_keys[j]), null }); //送入旧值!!!
							}
						}
					}
				}
			}
			//这个地方把批处理提交，上面一段逻辑是查到修改前的数据
			if (!isRealPreSQL) { //如果不是预编译的,则需要执行下批处理!!
				li_dealRecords = p_stmt.executeBatch(); //真正处理数据库!!!
			} else {
				Integer[] li_rs = (Integer[]) al_result.toArray(new Integer[0]); //
				li_dealRecords = new int[li_rs.length]; //
				for (int i = 0; i < li_dealRecords.length; i++) {
					li_dealRecords[i] = li_rs[i].intValue();
				}
			}
			//处理日志表,再查下新数据...在执行批处理后执行！
			if (al_dbopelog_log.size() > 0) {
				for (int i = 0; i < al_dbopelog_log.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_log.get(i); ////
					if ((str_itemOpeLog[3].equals("修改") || str_itemOpeLog[3].equals("新增")) && str_itemOpeLog[4] != null) {
						String str_sql_temp = "select * from " + str_itemOpeLog[1] + " where id='" + str_itemOpeLog[4] + "'"; //
						//HashVO[] hvos = getHashVoArrayByDS(_datasourcename, str_sql_temp); //
						HashVO[] hvos = getHashVoStructByDS(_datasourcename, str_sql_temp, true, false, null, false, false, _isPrePareSQL).getHashVOs();
						if (hvos.length > 0) {
							if (vmap[i] == null) {
								vmap[i] = new VectorMap(); //如果为空,则创建之!!!!
							}
							String[] str_keys = hvos[0].getKeys(); //
							for (int j = 0; j < str_keys.length; j++) {
								if (vmap[i].containsKey(str_keys[j].toLowerCase())) { //如果已包含!!
									String[] str_rowItemValue = (String[]) vmap[i].get(str_keys[j].toLowerCase()); //
									str_rowItemValue[1] = hvos[0].getStringValue(str_keys[j]); //新值
									vmap[i].put(str_keys[j].toLowerCase(), str_rowItemValue); //重新置入!!!
								} else {
									vmap[i].put(str_keys[j], new String[] { null, hvos[0].getStringValue(str_keys[j]) }); //直接送入新值,即没有旧值,在Insert情况下是无旧值的!!!!!
								}
							}
						}
					}
				}
			}

			//将日志插入到处理日志表中!!!!如果有旧值与新值的比较,则还要插入子表!!!
			if (al_dbopelog_log.size() > 0) {
				p_stmt_log = conn.prepareStatement("insert into PUB_DBTRIGGERLOG(id,tabname,tabdesc,opetype,opeuser,deptid,deptname,opetime,opesql,clientip) values (?,?,?,?,?,?,?,?,?,?)"); //主表
				p_stmt_log_b = conn.prepareStatement("insert into PUB_DBTRIGGERLOG_B(id,triggerlog_id,tabname,opetype,itemname,olditemvalue,newitemvalue,itemdesc) values (?,?,?,?,?,?,?,?)"); //子表

				String str_currtime = new TBUtil().getCurrTime(); //当前时间..
				WLTInitContext initContext = new WLTInitContext(); //
				String str_loginUserCode = null;
				String str_loginUserName = null; //
				String str_loginUserDeptid = null; //
				String str_loginUserDeptname = null; //
				String str_clientip = null;
				CurrSessionVO currsession = initContext.getCurrSession();
				str_loginUserCode = currsession.getLoginUserCode(); //登录人员编码!!
				str_loginUserName = currsession.getLoginUserName(); //登录人员名称!!
				str_clientip = currsession.getClientIP1() + "/" + currsession.getClientIP2(); //
				String str_sql_temp = "select a.id,a.name from pub_corp_dept a,pub_user_post b where a.id = b.userdept and isdefault='Y' and userid =" + currsession.getLoginUserId(); //pub_user中的pkdept不准，应用pub_user_post
				//HashVO[] hvos = getHashVoArrayByDS(_datasourcename, str_sql_temp); //
				HashVO[] hvos = getHashVoStructByDS(_datasourcename, str_sql_temp, true, false, null, false, false, _isPrePareSQL).getHashVOs();
				if (hvos != null && hvos.length > 0) {
					str_loginUserDeptid = hvos[0].getStringValue("id");
					str_loginUserDeptname = hvos[0].getStringValue("name");
				}
				String str_itemsql = null; //
				String showuser = new TBUtil().getSysOptionStringValue("是否显示登陆号", "0");//是否显示登陆号,默认0为登录号+姓名，1为只显示人员姓名
				//遍历各条SQL!!!
				for (int i = 0; i < al_dbopelog_log.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_log.get(i); ////
					String str_parentrecordid = getSequenceNextValByDS(null, "S_PUB_DBTRIGGERLOG"); //主表的主键!!!
					p_stmt_log.setString(1, str_parentrecordid);
					p_stmt_log.setString(2, str_itemOpeLog[1]);
					p_stmt_log.setString(3, str_itemOpeLog[2]);
					p_stmt_log.setString(4, str_itemOpeLog[3]);
					if ("1".equals(showuser)) { //航天项目不显示用户名，因为用户名用的是身份证号！
						p_stmt_log.setString(5, str_loginUserName);
					} else {
						p_stmt_log.setString(5, str_loginUserCode + "/" + str_loginUserName);
					}
					p_stmt_log.setString(6, str_loginUserDeptid);
					p_stmt_log.setString(7, str_loginUserDeptname);
					p_stmt_log.setString(8, str_currtime);
					p_stmt_log.setString(9, str_itemOpeLog[0]);
					p_stmt_log.setString(10, str_clientip);
					p_stmt_log.addBatch(); //批量加入!!!
					if (_isDebugLog) { //是否输出日志!!!有时是不想输出日志的!!!
						WLTLogger.getLogger(this).debug("insert into PUB_DBTRIGGERLOG(id,tabname,tabdesc,opetype,opeuser,deptid,deptname,opetime,opesql,clientip) values (?,?,?,?,?,?,?,?,?,?)"); //
					}
					HashMap tabledescr = ServerEnvironment.getDBTriggerTableDescrMap();
					HashMap coldescr = new HashMap();
					if (tabledescr.containsKey(str_itemOpeLog[1].toUpperCase())) {
						coldescr = (HashMap) tabledescr.get(str_itemOpeLog[1].toUpperCase());
					}
					if (vmap[i] != null) {
						String[] str_allkeys = vmap[i].getKeysAsString(); //所有的列!
						for (int j = 0; j < str_allkeys.length; j++) { //各个列的名称!!!
							String[] str_old_new_itemvalue = (String[]) vmap[i].get(str_allkeys[j]); ////
							String str_childrecordid = getSequenceNextValByDS(null, "S_PUB_DBTRIGGERLOG_B", 50); //主表的主键!!!
							p_stmt_log_b.setString(1, str_childrecordid); //
							p_stmt_log_b.setString(2, str_parentrecordid); //
							p_stmt_log_b.setString(3, str_itemOpeLog[1]); //
							p_stmt_log_b.setString(4, str_itemOpeLog[3]); //
							p_stmt_log_b.setString(5, str_allkeys[j]); //
							p_stmt_log_b.setString(6, str_old_new_itemvalue[0]); //
							p_stmt_log_b.setString(7, str_old_new_itemvalue[1]); //
							String descr = "";
							if (coldescr != null && coldescr.containsKey(str_allkeys[j].toUpperCase())) {
								descr = (String) coldescr.get(str_allkeys[j].toUpperCase());
							}
							p_stmt_log_b.setString(8, descr); //设置字段描述
							if (_isDebugLog) { //是否输出日志!!!有时是不想输出日志的!!!
								WLTLogger.getLogger(this).debug("insert into PUB_DBTRIGGERLOG_B(id,triggerlog_id,tabname,opetype,itemname,olditemvalue,newitemvalue,itemdesc) values (?,?,?,?,?,?,?,?)"); //
							}
							p_stmt_log_b.addBatch(); //送入
						}
					}
				}
				p_stmt_log.executeBatch(); //
				p_stmt_log_b.executeBatch(); //
			}
			//清空查询分页时的求Count的缓存!!即凡是修改了某个表,则立即刷新该表在查询分页是注册的SQL缓存!
			if (al_dbopelog_all.size() > 0) { //是all
				HashSet hstTableName = new HashSet(); //
				for (int i = 0; i < al_dbopelog_all.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_all.get(i); ////
					hstTableName.add(str_itemOpeLog[1].toLowerCase()); //一定要转小写!
				}
				String[] str_tabNames = (String[]) hstTableName.toArray(new String[0]); //所有表名!
				String[] str_cacheTableNames = (String[]) ServerEnvironment.countSQLCache.keySet().toArray(new String[0]); //得到所有的key
				for (int i = 0; i < str_tabNames.length; i++) { ////
					for (int j = 0; j < str_cacheTableNames.length; j++) { ////
						if (str_cacheTableNames[j].equals(str_tabNames[i]) || str_cacheTableNames[j].startsWith("v_" + str_tabNames[i])) { //如果缓存中的这个表名等于或且以v_本表名开头,则清空这条缓存!
							ServerEnvironment.countSQLCache.remove(str_cacheTableNames[j]); ///
							ServerEnvironment.pagePKValue.remove(str_cacheTableNames[j]); //
							//System.out.println("清空表[" + str_cacheTableNames[j] + "]的缓存"); //
						}
					}
				}
			}

			return li_dealRecords; //
			// long ll_2 = System.currentTimeMillis();
		} catch (SQLException ex) {
			String str_message = "";
			for (int i = 0; i < _sqllist.size(); i++) {
				str_message = str_message + _sqllist.get(i) + ";\n"; //
			}
			// System.out.println("执行批量SQL语句失败,SQL如下:\n"); //
			ex.printStackTrace(); //
			if (ex.getNextException() != null) {
				System.err.println("下一个异常:"); //
				ex.getNextException().printStackTrace(); //实际原因!!!
			}
			conn.getConn().rollback();
			throw new SQLException("在数据源[" + _datasourcename + "]上执行批量SQL失败:\n" + str_message + ",错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage());
		} catch (Exception ex) {
			conn.getConn().rollback();
			String str_message = "";
			for (int i = 0; i < _sqllist.size(); i++) {
				str_message = str_message + _sqllist.get(i) + "\n"; //
			}
			// System.out.println("执行批量SQL语句失败,SQL如下:\n"); //
			// System.out.println("失败原因:");
			ex.printStackTrace(); //
			throw new Exception("在数据源[" + _datasourcename + "]上执行批量SQL[" + str_message + "]失败,原因:[" + ex.getMessage() + "]");
		} finally {
			try {
				if (p_stmt != null) {
					p_stmt.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				if (p_stmt_log != null) {
					p_stmt_log.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			try {
				if (p_stmt_log != null) {
					p_stmt_log.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	//批量执行SQL,但不输出日志!!
	public void executeBatchByDSNoLog(String _datasourcename, java.util.List _sqllist) throws Exception {
		executeBatchByDS(_datasourcename, _sqllist, false); //
	}

	/**
	 * 立即提交一批SQL
	 * @param _datasourcename
	 * @param _sqlBuilders
	 * @throws Exception
	 */
	public void executeBatchByDSImmediately(String _datasourcename, SQLBuilderIfc[] _sqlBuilders) throws Exception {
		String[] str_sqls = new String[_sqlBuilders.length]; //
		for (int i = 0; i < str_sqls.length; i++) {
			str_sqls[i] = _sqlBuilders[i].getSQL(); //
		}
		executeBatchByDSImmediately(_datasourcename, str_sqls); //
	}

	public void executeBatchByDSImmediately(String _datasourcename, String[] _sqls) throws Exception {
		executeBatchByDSImmediately(_datasourcename, _sqls, true); //
	}

	/**
	 * 独立新开事务执行一批SQL,适用于后台任务逻辑..但这一批SQL之间本身是一个事务.. 该方法不抛露给客户端,因为客户端没必要用这个方法
	 * 
	 * @param _datasourcename
	 * @param _sqllist
	 * @throws Exception
	 */
	public void executeBatchByDSImmediately(String _datasourcename, String[] _sqls, boolean _isDebugLog) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		java.util.List list = Arrays.asList(_sqls); // 转换成一个List
		executeBatchByDSImmediately(_datasourcename, list, _isDebugLog); //
	}

	public void executeBatchByDSImmediately(String _datasourcename, java.util.List _sqllist) throws Exception {
		executeBatchByDSImmediately(_datasourcename, _sqllist, true); ////
	}

	/**
	 * 独立新开事务执行一批SQL,适用于后台任务逻辑..但这一批SQL之间本身是一个事务.. 该方法不抛露给客户端,因为客户端没必要用这个方法
	 * 
	 * @param _datasourcename
	 * @param _sqllist
	 * @throws Exception
	 */
	public void executeBatchByDSImmediately(String _datasourcename, java.util.List _sqllist, boolean _isDebugLog) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}

		Connection conn = null;
		Statement p_stmt = null;
		try {
			String str_dburl = "jdbc:apache:commons:dbcp:" + _datasourcename; // //
			DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
			if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) {
				WLTDBConnection wltc = new WLTDBConnection(_datasourcename);
				conn = wltc.getConn();
			} else {
				conn = DriverManager.getConnection(str_dburl); // //真正创建连接,其实也是从dbcp池中取的!!!
			}
			conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED); // 设置事务隔离级别!!设置死了!!
			conn.setAutoCommit(false); // 在这里定死了!!

			p_stmt = conn.createStatement(); //
			for (int i = 0; i < _sqllist.size(); i++) {
				Object objsql = _sqllist.get(i);
				String sql = null;
				if (objsql instanceof String) {
					sql = (String) objsql; //
				} else if (objsql instanceof SQLBuilderIfc) {
					sql = ((SQLBuilderIfc) objsql).getSQL(); //
				} else {
					sql = "" + objsql; //
				}
				addSQLToSessionSQLListener(sql); //向SQL监听器中增加本SQL
				if (_isDebugLog) {
					WLTLogger.getLogger(this).debug(sql); //
				}
				p_stmt.addBatch(sql); // 批增加!!!
				// System.out.println("ExcuteTran:" + sql);
			}
			p_stmt.executeBatch();
			conn.commit(); // 立即提交
		} catch (Exception e) {
			try {
				conn.rollback(); // 立即回滚
			} catch (SQLException e1) {
			}
			String str_message = "";
			for (int i = 0; i < _sqllist.size(); i++) {
				str_message = str_message + _sqllist.get(i) + "\n"; //
			}
			throw new Exception("在数据源[" + _datasourcename + "]上执行批量SQL[" + str_message + "]失败,原因:[" + e.getMessage() + "]"); //重抛异常
		} finally {
			try {
				if (p_stmt != null) {
					p_stmt.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				if (conn != null) {
					conn.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public String getSequenceNextValByDS(String _datasourcename, String _sequenceName) throws Exception {
		return getSequenceNextValByDS(_datasourcename, _sequenceName, true); //
	}

	public String getSequenceNextValByDS(String _datasourcename, String _sequenceName, boolean _isDebugLog) throws Exception {
		if (_datasourcename == null) {
			return "" + ServerSequenceFactory.getInstance().getSequenceFromCache(_sequenceName, 20, _isDebugLog); //
		} else {
			return "" + ServerSequenceFactory.getInstance().getSequenceFromCache(_datasourcename, _sequenceName, 20, _isDebugLog); //
		}
	}

	/**
	 * 该方法不外抛给客户端!
	 * 
	 * @param _datasourcename
	 * @param _sequenceName
	 * @return
	 * @throws Exception
	 */
	public String getSequenceNextValByDS(String _datasourcename, String _sequenceName, long _dbbatch) throws Exception {
		return "" + ServerSequenceFactory.getInstance().getSequenceFromCache(_sequenceName, _dbbatch); //
	}

	/**
	 * 该方法外抛给客户端,从客户端永远是一取一批!!
	 * 
	 * @param _datasourcename
	 * @param _sequenceName
	 * @param _batch
	 * @return
	 * @throws Exception
	 */
	public long[] getSequenceBatchNextValByDS(String _datasourcename, String _sequenceName, int _batch) throws Exception {
		return ServerSequenceFactory.getInstance().getBatchSequence(_sequenceName, _batch, 20); //
	}

	/**
	 * 该方法外抛给客户端,从客户端永远是一取一批!!
	 * 
	 * @param _datasourcename
	 * @param _sequenceName
	 * @param _batch
	 * @return
	 * @throws Exception
	 */
	public long[] getSequenceBatchNextValByDS(String _datasourcename, String _sequenceName, int _batch, long _dbbatch) throws Exception {
		return ServerSequenceFactory.getInstance().getBatchSequence(_sequenceName, _batch, _dbbatch); //
	}

	/**
	 * 快速往一张表插入一批数据!
	 * @param _table
	 * @param _pk
	 * @param _fieldArraysMap,key是字段名,value是String[]或String,如果String,则以分号相隔!,比如
	 * dataMap.put("Question_Crop_Id", new String[] { "2001", "1999", "1756", "1759", "1742", "1746", "1747" }); //
	 * dataMap.put("Question_Type", "1;2;3;4;5"); //
	 * @param _records
	 */
	public void insertDemoData(String _table, String _pk, HashMap _fieldArraysMap, int _records) throws Exception {
		String[] str_keys = (String[]) _fieldArraysMap.keySet().toArray(new String[0]); //
		java.util.Random rans = new java.util.Random(); //随机数生成器!
		TBUtil tbUtil = new TBUtil(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < _records; i++) { //遍历所有记录!
			InsertSQLBuilder isql = new InsertSQLBuilder(_table); //
			if (_pk != null) {
				isql.putFieldValue(_pk, getSequenceNextValByDS(null, "S_" + _table)); //主键值
			}
			for (int j = 0; j < str_keys.length; j++) {
				//String str_fieldName = str_keys[j]; //字段名
				Object obj = _fieldArraysMap.get(str_keys[j]); //boolean b = object.getClass().isArray();
				if (obj != null) {
					String[] str_items = null; //
					if (obj.getClass().isArray()) { //如果本身是个数组!
						str_items = (String[]) obj; //		
					} else { //如果不是数组
						str_items = tbUtil.split(((String) obj), ";"); //分割
					}
					String str_value = str_items[rans.nextInt(str_items.length)]; //随机值! 
					isql.putFieldValue(str_keys[j], str_value); //插入数据
				}
			}
			al_sqls.add(isql); //
		}
		executeBatchByDS(null, al_sqls); //
	}

	/**
	 * 调用存储过程不返回值 创建日期：(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                异常说明。
	 */
	public String callProcedureByDS(String _datasourcename, String procedureName, String[] parmeters, int _outputindex) throws Exception {
		long ll_1 = System.currentTimeMillis();
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		WLTDBConnection conn = null;
		java.sql.CallableStatement proc = null; //
		String str_return = null;
		String str_pars = procedureName + "(";
		if (parmeters != null) {
			for (int i = 0; i < parmeters.length; i++) {
				if (parmeters[i] == null) {
					str_pars = str_pars + "null,";
				} else {
					str_pars = str_pars + "'" + parmeters[i] + "',";
				}
			}
			str_pars = str_pars.substring(0, str_pars.length() - 1);
		}
		str_pars = str_pars + ")";
		addSQLToSessionSQLListener(str_pars); //向SQL监听器中增加本SQL
		WLTLogger.getLogger(this).debug(str_pars); //
		try {
			conn = getConn(_datasourcename); //
			String strTemp = "{ call " + procedureName + "(";
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length; i++) {
					strTemp = strTemp + "?,";
				}
				strTemp = strTemp.substring(0, strTemp.length() - 1);
			}
			strTemp = strTemp + ")}";
			proc = conn.prepareCall(strTemp);
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length; i++) {
					proc.setString(i + 1, parmeters[i]); // 设置入参
				}

				if (_outputindex > 0) {
					proc.registerOutParameter(_outputindex, java.sql.Types.VARCHAR); // 注册返回出参数,第一个总是游标类型
				}
			}

			proc.executeUpdate();
			if (_outputindex > 0) {
				str_return = String.valueOf(proc.getObject(_outputindex)); // 如果有出参,则返回之
			}

			long ll_2 = System.currentTimeMillis();
			return str_return; //
		} catch (SQLException ex) {
			// ex.printStackTrace();
			throw new SQLException("在数据源[" + _datasourcename + "]上调用存储过程[" + str_pars + "]失败,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new Exception("在数据源[" + _datasourcename + "]上调用存储过程[" + str_pars + "]失败,失败原因:" + ex.getMessage());
		} finally {
			try {
				if (proc != null) {
					proc.close();
				}
			} catch (Exception ex) {
			}
			try {
				// conn.close();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 调用存储过程不返回值 创建日期：(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                异常说明。
	 */
	public void callProcedureByDSSqlServer(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		long ll_1 = System.currentTimeMillis();
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		WLTDBConnection conn = null;
		java.sql.CallableStatement proc = null; //
		String str_pars = procedureName + " ";
		if (parmeters != null) {
			for (int i = 0; i < parmeters.length; i++) {
				if (parmeters[i] == null) {
					str_pars = str_pars + "null,";
				} else {
					str_pars = str_pars + "'" + parmeters[i] + "',";
				}
			}
			str_pars = str_pars.substring(0, str_pars.length() - 1);
		}

		addSQLToSessionSQLListener(str_pars); //向SQL监听器中增加本SQL
		WLTLogger.getLogger(this).debug(str_pars); //
		try {
			conn = getConn(_datasourcename); //
			String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getDbtype();
			String strTemp = "";
			if (str_dbtype.equalsIgnoreCase("SQLSERVER")) {
				strTemp = "EXEC " + str_pars;
			} else if (str_dbtype.equalsIgnoreCase("ORACLE")) {
				strTemp = "EXEC " + str_pars;
			} else if (str_dbtype.equalsIgnoreCase("DB2")) {
				strTemp = "EXEC " + str_pars;
			}

			proc = conn.prepareCall(strTemp);
			WLTLogger.getLogger(this).debug("strTemp===========" + strTemp); //

			proc.execute();

			long ll_2 = System.currentTimeMillis();
		} catch (SQLException ex) {
			// ex.printStackTrace();
			throw new SQLException("在数据源[" + _datasourcename + "]上调用存储过程[" + str_pars + "]失败,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new Exception("在数据源[" + _datasourcename + "]上调用存储过程[" + str_pars + "]失败,失败原因:" + ex.getMessage());
		} finally {
			try {
				if (proc != null) {
					proc.close();
				}
			} catch (Exception ex) {
			}
			try {
				// conn.close();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 调用存储过程返回值 创建日期：(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                异常说明。
	 */
	public String callProcedureReturnStrByDS(String _datasourcename, String procedureName, String[] parmeters) throws Exception {
		WLTDBConnection conn = null;
		java.sql.CallableStatement proc = null;

		long ll_1 = System.currentTimeMillis();
		String str_pars = procedureName + "(";
		if (parmeters != null) {
			for (int i = 0; i < parmeters.length; i++) {
				if (parmeters[i] == null) {
					str_pars = str_pars + "null,";
				} else {
					str_pars = str_pars + "'" + parmeters[i] + "',";
				}
			}
			str_pars = str_pars.substring(0, str_pars.length() - 1);
		}
		str_pars = str_pars + ")";
		addSQLToSessionSQLListener(str_pars); //向SQL监听器中增加本SQL
		WLTLogger.getLogger(this).debug(str_pars); //
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}
			conn = getConn(_datasourcename); //
			String strTemp = "{ call " + procedureName + "(";
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length; i++) {
					strTemp = strTemp + "?,";
				}
				strTemp = strTemp.substring(0, strTemp.length() - 1);
			}
			strTemp = strTemp + ")}";
			proc = conn.prepareCall(strTemp);
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length - 1; i++) {
					proc.setString(i + 1, parmeters[i]);
				} // 设置入参
			}
			proc.registerOutParameter(parmeters.length, java.sql.Types.VARCHAR); // 设置出参
			proc.execute();
			String ls_return = String.valueOf(proc.getObject(parmeters.length)); // 得到出参
			long ll_2 = System.currentTimeMillis();
			return ls_return; // 返回出参
		} catch (SQLException ex) { //
			// ex.printStackTrace();
			throw new SQLException("在数据源[" + _datasourcename + "]上调用存储过程[" + str_pars + "]失败,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage()); //
		} catch (Exception ex) { //
			// ex.printStackTrace();
			throw new Exception("在数据源[" + _datasourcename + "]上调用存储过程[" + str_pars + "]失败,失败原因:" + ex.getMessage()); //
		} finally {
			try {
				if (proc != null) {
					proc.close();
				}
			} catch (Exception ex) {
			}

			try {
				// conn.close();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 调用函数返回字符串 创建日期：(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                异常说明。
	 */
	public String callFunctionReturnStrByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		WLTDBConnection conn = null;
		java.sql.CallableStatement proc = null;

		String str_pars = functionName + "(";
		if (parmeters != null) {
			for (int i = 0; i < parmeters.length; i++) {
				if (parmeters[i] == null) {
					str_pars = str_pars + "null,";
				} else {
					str_pars = str_pars + "'" + parmeters[i] + "',";
				}
			}
			str_pars = str_pars.substring(0, str_pars.length() - 1);
		}
		str_pars = str_pars + ")";
		addSQLToSessionSQLListener(str_pars); //向SQL监听器中增加本SQL
		WLTLogger.getLogger(this).debug(str_pars); //

		try {
			conn = getConn(_datasourcename); //
			String strTemp = "{ ? = call " + functionName + "(";
			if (parmeters != null) // 如果入参不为空,则拼成参数集
			{
				for (int i = 0; i < parmeters.length; i++) {
					strTemp = strTemp + "?,";
				}
				strTemp = strTemp.substring(0, strTemp.length() - 1);
			}
			strTemp = strTemp + ")}";
			proc = conn.prepareCall(strTemp);
			proc.registerOutParameter(1, java.sql.Types.VARCHAR); // 注册返回出参数,第一个总是游标类型
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length; i++) {
					proc.setString(i + 2, parmeters[i]);
				}
			}
			proc.execute();
			String str_return = String.valueOf(proc.getObject(1));
			return str_return;
		} catch (SQLException ex) {
			throw new SQLException("在数据源[" + _datasourcename + "]上调用函数[" + str_pars + "]失败,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("在数据源[" + _datasourcename + "]上调用函数[" + str_pars + "]失败,失败原因:" + ex.getMessage());
		} finally {
			try {
				if (proc != null) {
					proc.close();
				}
			} catch (Exception ex) {
			}
			try {
				// conn.close();
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * 调用函数返回表 创建日期：(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                异常说明。
	 */
	public String[][] callFunctionReturnTableByDS(String _datasourcename, String functionName, String[] parmeters) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}

		WLTDBConnection conn = null; //
		java.sql.CallableStatement proc = null;
		java.sql.ResultSet rs = null;

		String str_pars = functionName + "(";
		if (parmeters != null) {
			for (int i = 0; i < parmeters.length; i++) {
				if (parmeters[i] == null) {
					str_pars = str_pars + "null,";
				} else {
					str_pars = str_pars + "'" + parmeters[i] + "',";
				}
			}
			str_pars = str_pars.substring(0, str_pars.length() - 1);
		}
		str_pars = str_pars + ")";
		addSQLToSessionSQLListener(str_pars); //向SQL监听器中增加本SQL
		WLTLogger.getLogger(this).debug(str_pars); //
		try {
			conn = getConn(_datasourcename); //
			String strTemp = "{ ? = call " + functionName + "(";
			if (parmeters != null) // 如果入参不为空,则拼成参数集
			{
				for (int i = 0; i < parmeters.length; i++) {
					strTemp += "?,";
				}
				strTemp = strTemp.substring(0, strTemp.length() - 1);
			}
			strTemp += ")}";

			proc = conn.prepareCall(strTemp);

			String[][] str_data = null; // 返回数据

			// 如果是ORACLE,则使用游标类型
			proc.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR); // 注册返回出参数,第一个总是游标类型
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length; i++) {
					proc.setString(i + 2, parmeters[i]);
				}
			}
			proc.execute();
			rs = (ResultSet) proc.getObject(1);
			if (rs != null) // 如果结果集不为空
			{
				Vector aVector = new Vector();
				int nColCnt = rs.getMetaData().getColumnCount();
				while (rs.next()) // 遍历结果集
				{
					String[] str_rowdata = new String[nColCnt];
					for (int i = 1; i <= nColCnt; i++) {
						str_rowdata[i - 1] = getClobUtil().getClob(rs.getString(i));
					}
					aVector.add(str_rowdata);
				}

				str_data = new String[aVector.size()][nColCnt];
				for (int i = 0; i < aVector.size(); i++) {
					String[] str_rowdata = (String[]) aVector.get(i);
					for (int j = 0; j < nColCnt; j++) {
						str_data[i][j] = str_rowdata[j];
					}
				}
			}

			return str_data;
		} catch (SQLException ex) {
			throw new SQLException("在数据源[" + _datasourcename + "]上调用函数[" + str_pars + "]失败,错误编码[" + ex.getErrorCode() + "],状态[" + ex.getSQLState() + "],原因:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("在数据源[" + _datasourcename + "]上调用函数[" + str_pars + "]失败,失败原因:" + ex.getMessage());
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (Exception ex) {
			}
			try {
				if (proc != null) {
					proc.close();
				}
			} catch (Exception ex) {
			}

			try {
				// conn.close();
			} catch (Exception ex) {
			}
		}
	}

	//从一颗树中取得某一个结点的上层结点的某个字段的值
	public String getTreePathColValue(String _tableName, String _returnItemName, String _linkedIdField, String _parentLinkedField, String _whereField, String _whereCondition) throws Exception {
		BSUtil bsUtil = new BSUtil(); //
		if (_tableName.equalsIgnoreCase("pub_corp_dept")) { //如果是机构,则特殊处理,从缓存取!!
			HashVO[] hvsAll = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //
			String str_return = bsUtil.getTreePathItemValueFromHashVOs(hvsAll, _linkedIdField, _returnItemName, _whereField, _whereCondition); ////
			return str_return; //
		} else {
			String str_sql = ("select * from " + _tableName).toLowerCase(); ////
			HashVO[] hvsAll = getHashVoArrayAsTreeStructByDS(null, str_sql, _linkedIdField, _parentLinkedField, null, null);
			String str_return = bsUtil.getTreePathItemValueFromHashVOs(hvsAll, _linkedIdField, _returnItemName, _whereField, _whereCondition); ////
			return str_return; ////
		}
	}

	/**
	 * 执行一次查询超过多少毫秒后就警告的上限!!!
	 * 
	 * @return
	 */
	private long getWarnSQLTime() {
		return 10000;
	}

	private boolean checkIfSql(String sql) {
		String sqlUP = sql.toUpperCase();
		if (sqlUP.indexOf("SELECT") < 0 && sqlUP.indexOf("UPDATE") < 0 && sqlUP.indexOf("ALTER") < 0 && sqlUP.indexOf("DROP") < 0 && sqlUP.indexOf("DELETE") < 0 && sqlUP.indexOf("INSERT") < 0 && sqlUP.indexOf("CREATE") < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 向SQL监听器中增加监听
	 * @param _sql
	 */
	private void addSQLToSessionSQLListener(String _sql) {
		if (getCurrSession() != null) {
			if (ServerEnvironment.getSessionSqlListenerMap().containsKey(getCurrSession().getHttpsessionid())) { //如果已注册需要监听
				Queue sqlQueue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(getCurrSession().getHttpsessionid()); //
				sqlQueue.add(_sql); //从屁股后插入..
			}
		}
	}

	/**
	 * 保留修改痕迹.
	 */
	public void saveKeepTrace(String _tablename, String _pkname, String _pkvalue, HashMap _fieldvalues, String _tracer) throws Exception {
		String str_currtime = new TBUtil().getCurrTime(); //
		String[] str_fieldkeys = (String[]) _fieldvalues.keySet().toArray(new String[0]);
		String[] str_sqls = new String[str_fieldkeys.length]; //
		CommDMO dmo = new CommDMO(); //
		for (int i = 0; i < str_fieldkeys.length; i++) {
			String str_value = (_fieldvalues.get(str_fieldkeys[i]) == null ? "null" : "'" + _fieldvalues.get(str_fieldkeys[i]) + "'");

			InsertSQLBuilder sb_sql = new InsertSQLBuilder("pub_bill_keeptrace");
			String str_newid = dmo.getSequenceNextValByDS(null, "s_pub_bill_keeptrace"); //
			sb_sql.putFieldValue("id", str_newid);
			sb_sql.putFieldValue("tablename", _tablename);
			sb_sql.putFieldValue("pkname", _pkname);
			sb_sql.putFieldValue("pkvalue", _pkvalue);
			sb_sql.putFieldValue("fieldname", str_fieldkeys[i]);
			sb_sql.putFieldValue("fieldvalue", str_value);
			sb_sql.putFieldValue("tracer", _tracer);
			sb_sql.putFieldValue("tracetime", str_currtime);
			str_sqls[i] = sb_sql.toString(); //
		}
		dmo.executeBatchByDS(null, str_sqls); //
	}

	private TBUtil getTBUtil() {
		return new TBUtil();
	}

	private ClobUtil getClobUtil() {
		if (pushClob == null) {
			pushClob = new ClobUtil();
		}
		return pushClob;
	}

}