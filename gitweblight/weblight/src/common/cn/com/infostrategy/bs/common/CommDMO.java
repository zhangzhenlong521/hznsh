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
	int limitcount = TBUtil.getTBUtil().getSysOptionIntegerValue("�б�����ѯ����", 2000); //̫ƽ��Ŀ�����Ҫ�����б������ҳ��ֻ�ܲ�ѯ��2000��¼������Ҫ����һ������������Ӧ�÷��������������/2017-09-26��

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
	 * ��������ϵͳ��
	 * @return
	 * @throws Exception
	 */
	public String[][] getAllSysTableAndDescr(String _datasourcename, String _tableNamePattern, boolean _isContainView, boolean _isGetDescrFromXML) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getDefaultDataSourceName(); //
		}
		ResultSet resultSet = null; //�����!!
		try {
			WLTDBConnection conn = getConn(_datasourcename); ////
			DatabaseMetaData dbmd = conn.getMetaData(); //ȡ���������ݿ��ԭ���ݶ���!
			//dbmd.getPrimaryKeys(null, null, null);  //�õ����������Ϣ!!!�������������,�Ƿǳ���Ҫ������!!! ����Mysql,Oracle,SQLServer��������
			//dbmd.getIndexInfo(catalog, schema, table, unique, approximate);  //�õ����������Ϣ,�Ժ����ʵ�ʱ��ҳ�����,Ȼ����XML���컯����,��һ���ǳ���Ҫ������
			String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getDbtype(); //���ݿ�����
			String str_schema = null;
			if (str_dbtype.equalsIgnoreCase("ORACLE") || str_dbtype.equalsIgnoreCase("MYSQL")) { //�����ORACLE��MYSQL��Ҫָ��Schema����!!!SQLSERVSER����ָ��!!
				str_schema = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getUser();
			} else if (str_dbtype.equalsIgnoreCase("DB2")) {//����DB2���ݿ���жϡ����/2015-12-24��
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
			ArrayList al_tnames = new ArrayList(); //��������..
			while (resultSet.next()) {
				HashVO hvo = new HashVO(); //
				//String tableCatalog = resultSet.getString(1); //�洢Ŀ¼��
				//String tableSchema = resultSet.getString(2); //�洢��Schema��
				String str_tableName = resultSet.getString("TABLE_NAME"); //����
				if (str_tableName.toUpperCase().startsWith("BIN$")) {//oracle��ɾ���ı���ڻ���վ��������"BIN$"��ͷ������Ҫ��������ջ���վ��ִ��һ�� purge recyclebin�����/2012-08-24��
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
			new TBUtil().sortHashVOs(hvs, new String[][] { { "tabtype", "N", "N" }, { "tabname", "N", "N" } }); //����һ��!
			String[][] str_returns = new String[hvs.length][3]; //
			for (int i = 0; i < str_returns.length; i++) {
				str_returns[i][0] = hvs[i].getStringValue("tabname"); //
				str_returns[i][1] = hvs[i].getStringValue("tabtype"); //
				str_returns[i][2] = hvs[i].getStringValue("tabdescr"); //
			}

			if (_isGetDescrFromXML) { //�����Ҫ��XML��ȡ�ñ�ע!
				String[][] str_tabdesc = new InstallDMO().getAllIntallTablesDescr(); //
				HashMap map_nameDescr = new HashMap(); //
				for (int i = 0; i < str_tabdesc.length; i++) {
					map_nameDescr.put(str_tabdesc[i][0].toUpperCase(), str_tabdesc[i][1]); //
				}
				for (int i = 0; i < str_returns.length; i++) {
					if (str_returns[i][2] == null || str_returns[i][2].trim().equals("")) {
						if (map_nameDescr.containsKey(str_returns[i][0].toUpperCase())) { //
							str_returns[i][2] = (String) map_nameDescr.get(str_returns[i][0].toUpperCase()); //����һ��
						}
					}
				}
			}
			return str_returns; // str_tnames; //����!!!
		} catch (Exception ex) {
			throw new Exception("������Դ[" + _datasourcename + "]��ȡ����ϵͳ����ʱʧ��,ԭ��:" + ex.getMessage());
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
	 * ֱ�ӷ���һ���ַ���
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
	 * ����SQL,���ٷ���һ��hashMap,�����ø�SQL�ĵ�һ����Ϊkey����2����ΪValue��
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
	 * ����һ��SQL��䷵��һ��HashMap,��SQL��������������,Ȼ���һ����ΪhashMap��key,��2����ΪHashMap��Value
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
		if (_isAllKeyToLower || _isAllValueToLower) { //���ȫ��ת����Сд
			for (int i = 0; i < str_data.length; i++) {
				if (_isAllKeyToLower) { //�������Key��תСд
					if (str_data[i][0] != null) {
						str_data[i][0] = str_data[i][0].toLowerCase(); //
					}
				}
				if (_isAllValueToLower) { //�������Value��תСд!
					if (str_data[i][1] != null) {
						str_data[i][1] = str_data[i][1].toLowerCase(); //
					}
				}
			}
		}
		String str_temp = null;
		for (int i = 0; i < str_data.length; i++) {
			if (_appendSameKey) { //���������ͬ��ֵ
				if (map.containsKey(str_data[i][0])) { //�������ĳ��key
					str_temp = (String) map.get(str_data[i][0]); //
					if (str_temp != null) {
						map.put(str_data[i][0], str_temp + ";" + str_data[i][1]); //��ƨ�ɺ����������
					}
				} else {
					map.put(str_data[i][0], str_data[i][1]); //���ϣ��������...
				}
			} else {
				map.put(str_data[i][0], str_data[i][1]); //���ϣ��������...
			}
		}
		return map;
	}

	/**
	 * ȡ��һά����,��ֻȡSQL����еĵ�һ��!
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
	 * ȡ�ö�ά�������!!
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _isDebugLog
	 * @param _getClob  �Ƿ����ת�������ݡ�pushclob:�� ��־�ҵ���Ӧpub_clob ���ֵ��Ĭ��ת��������ClobUtil����clob����ʱ��Ҫ��þɵġ�pushclob:��ֵ��ɾ�������������������������/2018-08-11��
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
			if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder�ڵ���,����ʹ��LoadRunder����ʱ,�ǲ�������߼���!!
				addSQLToSessionSQLListener(_sql); //
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) {
					sb_sqlTrace.append("," + getCurrThreadTrace()); //
				}
			}
			conn = getConn(_datasourcename); //ȡ������
			long ll_rs_1 = System.currentTimeMillis(); //
			boolean isPrePareSQL = "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")); //
			String str_preSQL = null; //
			ArrayList al_pres = null; //
			if (isPrePareSQL) {
				al_pres = new PrepareSQLUtil().prepareSQL(_sql); //
				if (al_pres == null || al_pres.size() <= 1) {
					isPrePareSQL = false; //
				} else {
					str_preSQL = (String) al_pres.get(0); //Ԥ����ת�����SQL
					sb_sqlTrace.append("\n����Ԥ����ת��SQLΪ[" + str_preSQL + "],�����ֱ���:"); //
					for (int i = 1; i < al_pres.size(); i++) { ////
						sb_sqlTrace.append("[" + i + "]=[" + al_pres.get(i) + "]"); //
						if (i != al_pres.size() - 1) {
							sb_sqlTrace.append(","); //
						}
					}
				}
			}
			if (_isDebugLog) {
				logger.debug("ȡ��SQL*S:" + sb_sqlTrace.toString()); //
			}
			if (isPrePareSQL) { //�������������Ԥ����!!!
				stmt = conn.prepareStatement(str_preSQL); //Ԥ����!!!
				for (int i = 1; i < al_pres.size(); i++) {
					((PreparedStatement) stmt).setObject(i, al_pres.get(i)); //
				}
				rs = ((PreparedStatement) stmt).executeQuery(); //
			} else {
				stmt = conn.createStatement(); //�����α�!
				rs = stmt.executeQuery(_sql); //Ҫ�����������!!!Ҫ��Ҳ��������!!!	
			}

			long ll_rs_2 = System.currentTimeMillis(); //
			ResultSetMetaData rsmd = rs.getMetaData();
			int li_columncount = rsmd.getColumnCount(); //���м���!!
			ArrayList al_rowData = new ArrayList();
			int li_rows = 0;
			//int li_warnrowcount = 2000; //
			long ll_allDataSize = 0; //��¼���ݵĴ�С,�������ܼ��!!!
			while (rs.next()) {
				String[] str_row = new String[li_columncount];
				for (int i = 1; i <= li_columncount; i++) {
					String str_cell = _getClob ? getClobUtil().getClob(rs.getString(i)) : rs.getString(i); //�����/2018-08-11��
					if (str_cell == null) { //����ǿ�ֵ,�Զ�ת�մ�,������Ϊ���ط����ʱ����"null"����,Ϊ���õ�ʱ�򷽱�,������һ�ĵ������ط����ݿ�ֵ�жϵĶ�Ҫ����ͬʱ���ݿմ��ж���!
						str_cell = "";
					}
					str_row[i - 1] = str_cell;
					if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder�ڵ���,����ʹ��LoadRunder����ʱ,�ǲ�������߼���!!
						ll_allDataSize = ll_allDataSize + str_cell.length(); //�ۼ�!
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
				if (ll_rs_3 - ll_rs_2 > 50 || ll_rs_2 - ll_rs_1 > 50) { //�������50����,�򾯸�!!!
					logger.warn("ȡ��SQL*S(DB��ʱ" + (ll_rs_2 - ll_rs_1) + ",Web��ʱ" + (ll_rs_3 - ll_rs_2) + "):�����SQL:" + _sql); //
				}
			}
			if (!ServerEnvironment.isLoadRunderCall && ll_allDataSize > 0) {
				new WLTInitContext().setCurrSessionCustInfoByKey("ȡ����С", new String[] { "(getStrArray[" + li_rows + "])" + _sql, "" + ll_allDataSize }); //
			}
			return str_data;
		} catch (SQLException ex) { //
			throw new SQLException("������Դ[" + _datasourcename + "]��ִ��SQL[" + _sql + "]����,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			ex.printStackTrace(); //
			throw new Exception("������Դ[" + _datasourcename + "]��ִ��SQL[" + _sql + "]����,ԭ��:" + ex.getMessage());
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
	 * ȡ��In����..
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
	 * ����һ��Id[]ȥ����һ����ʱ����,Ȼ��ƴ��һ���Ӳ�ѯ��䷵��
	 * @param _ids
	 * @return  "select ids from pub_sqlins where bno='345'", Ȼ���ڱ�ĵط��õ�,���� select * from law_innerrules where createcorp in (select ids from pub_sqlins where bno='345')
	 */
	public String getSubSQLFromTempSQLTableByIDs(String[] _ids) throws Exception {
		//ƴ��һ��SQL,�����ύ,����Ҳ��������!
		long ll_1 = System.currentTimeMillis(); //
		if (_ids == null || _ids.length <= 0) { //
			return "'-99999'"; //ֱ�ӷ���
		}
		String[] str_ids = new TBUtil().distinctStrArray(_ids); //����Ψһ�Թ���!!!��Ϊ������ظ���,����˷�����!!	
		if (str_ids == null || str_ids.length <= 0) { //��������ҵ������һ����̬�������_ids��һ��,�����ǿ��ַ���,Ȼ�󾭹�Ψһ�Ժϲ���,����Ϊ0��!,���SQL�������ؾ��Ǹ��մ�!!!���Ա��뻹Ҫ������ж�!!��xch/2012-04-28��
			return "'-99999'"; //ֱ�ӷ���
		}
		if (str_ids.length <= 999) { //�����999������,��ֱ��ƴ��������!!!!!!������̫����,��������ֱ��in�����!!!
			StringBuffer sb_alltext = new StringBuffer(); //
			for (int i = 0; i < str_ids.length; i++) { ////
				sb_alltext.append("'" + (str_ids[i] == null ? "" : str_ids[i]) + "'"); ////
				if (i != str_ids.length - 1) { //����������һ������϶���!!!!!
					sb_alltext.append(","); //����������һ��,��Ӷ���!!!!!
				}
			}
			return sb_alltext.toString(); //
		}

		//ɾ��1Сʱǰ������,�����ύ,������������!!!
		int li_deletepreMinutes = 60; //ɾ��60����֮ǰ������!!!
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyyMMddHHmm", Locale.SIMPLIFIED_CHINESE);
		long ll_currtime = System.currentTimeMillis();
		String str_currhour = sdf_curr.format(new Date(ll_currtime)); //
		String str_pre2hours = sdf_curr.format(ll_currtime - (li_deletepreMinutes * 60 * 1000)); //ɾ��N����֮ǰ������,��������� 05-06-12:59 ���ص��� 05-06 11:59
		String str_delsql = "delete from pub_sqlins where createtime<=" + str_pre2hours; //
		//System.out.println("ɾ����SQL:" + str_delsql); ////
		executeUpdateByDSImmediately(null, str_delsql); //����ɾ��1Сʱ֮ǰ������!������ֻҪ��ÿСʱ�Ľ��ӵ��ʱ��Ż�����ɾ��������!!!���һᱻ��һ����ɾ����!

		//Ԥ��ȡ��BNO
		String str_newbatchno = getSequenceNextValByDS(null, "S_PUB_SQLINS_BNO"); //��ȡ���µ�����!!
		int li_onerowcount = 30; //һ���ж��ٸ�,������20,�Ժ����������Ҫ���ܻ���30,50,����100
		int li_cycles = str_ids.length / li_onerowcount; //����ѭ��,���С��20���򷵻�0,����ִ�У�ֱ�Ӵ�������!!
		int li_lefts = str_ids.length % li_onerowcount; //����,��ʣ�µ�!!�����һ�δ���
		ArrayList al_sqls = new ArrayList(); //
		StringBuffer sb_sql = null;
		String str_itemValue = null;
		for (int i = 0; i < li_cycles; i++) { //����ѭ��!!!
			sb_sql = new StringBuffer(); //
			sb_sql.append("insert into pub_sqlins values (" + str_newbatchno + "," + str_currhour + ","); //
			for (int j = 0; j < li_onerowcount; j++) { //20����
				str_itemValue = str_ids[i * li_onerowcount + j];
				if (str_itemValue == null) { //����Ϊ��!!
					sb_sql.append("null");
				} else {
					sb_sql.append("'" + str_itemValue + "'");
				}
				if (j != li_onerowcount - 1) {
					sb_sql.append(","); //����������һ��,����϶���!!
				}
			}
			sb_sql.append(")"); //
			al_sqls.add(sb_sql.toString());
		}

		if (li_lefts != 0) { //���������,���������!!����Ҫ��������ʾ��ע,��Ϊ���ܸ�����������20��!!!
			sb_sql = new StringBuffer(); //
			sb_sql.append("insert into pub_sqlins (bno,createtime,");
			for (int j = 0; j < li_lefts; j++) {
				sb_sql.append("id" + (j + 1)); //
				if (j != li_lefts - 1) {
					sb_sql.append(","); //����������һ��,����϶���!!
				}
			}
			sb_sql.append(") values ("); //
			sb_sql.append(str_newbatchno + "," + str_currhour + ","); //
			for (int j = 0; j < li_lefts; j++) { //����ʣ�µ�
				str_itemValue = str_ids[li_cycles * li_onerowcount + j]; //////
				if (str_itemValue == null) {
					sb_sql.append("null");
				} else {
					sb_sql.append("'" + str_itemValue + "'");
				}
				if (j != li_lefts - 1) {
					sb_sql.append(","); //����������һ��,����϶���!!
				}
			}
			sb_sql.append(")"); //
			al_sqls.add(sb_sql.toString());
		}
		executeBatchByDSImmediately(null, al_sqls); //��������,������������!!
		long ll_2 = System.currentTimeMillis(); //
		logger.info("����ʱ����[" + str_ids.length + "]ids,������[" + al_sqls.size() + "]����¼,�����Ӳ�ѯ,��ʱ[" + (ll_2 - ll_1) + "]"); //
		return "select ids from v_pub_sqlins where bno=" + str_newbatchno; //�����Ӳ�ѯ,������ԭ����SQL�о��ǡ�select * from cmp_cmpfile where createcorp in (select ids from v_pub_sqlins where bno=345)��  //
	}

	public String getSubSQLFromTempSQLTableByIDs(String[] _ids, String fileName) throws Exception {
		//ƴ��һ��SQL,�����ύ,����Ҳ��������!

		if (_ids == null || _ids.length <= 0) { //
			return "'-99999'"; //ֱ�ӷ���
		}
		String[] str_ids = new TBUtil().distinctStrArray(_ids); //����Ψһ�Թ���!!!��Ϊ������ظ���,����˷�����!!	
		if (str_ids == null || str_ids.length <= 0) { //��������ҵ������һ����̬�������_ids��һ��,�����ǿ��ַ���,Ȼ�󾭹�Ψһ�Ժϲ���,����Ϊ0��!,���SQL�������ؾ��Ǹ��մ�!!!���Ա��뻹Ҫ������ж�!!��xch/2012-04-28��
			return "'-99999'"; //ֱ�ӷ���
		}
		if (str_ids.length <= 999) { //�����999������,��ֱ��ƴ��������!!!!!!������̫����,��������ֱ��in�����!!!
			StringBuffer sb_alltext = new StringBuffer(); //
			for (int i = 0; i < str_ids.length; i++) { ////
				sb_alltext.append("'" + (str_ids[i] == null ? "" : str_ids[i]) + "'"); ////
				if (i != str_ids.length - 1) { //����������һ������϶���!!!!!
					sb_alltext.append(","); //����������һ��,��Ӷ���!!!!!
				}
			}
			String sql_str = fileName + " in (" + sb_alltext.toString() + ")";
			return sql_str; //
		}

		return getOracleSQLIn(str_ids, fileName);
	}

	private String getOracleSQLIn(String[] _ids, String fieldName) {
		String[] str_ids = new TBUtil().distinctStrArray(_ids); //����Ψһ�Թ���!!!��Ϊ������ظ���,����˷�����!!	
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
	 * ȡ�ñ�ṹ����!!
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

		String str_fromtablename = null; // ����
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
			if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder�ڵ���,����ʹ��LoadRunder����ʱ,�ǲ�������߼���!!!
				addSQLToSessionSQLListener(_sql); //
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) {
					str_sqlTrace = getCurrThreadTrace(); //ȡ�õ�ǰ�̶߳�ջ!
				}
			}
			conn = getConn(_datasourcename); //ȡ�����ݿ�����!!		
			stmt = conn.createStatement(); //
			if (_isDebugLog) {
				logger.debug("ȡ��SQL*T:" + _sql + (str_sqlTrace == null ? "" : "," + str_sqlTrace)); //
			}
			long ll_rs_1 = System.currentTimeMillis(); //
			rs = stmt.executeQuery(_sql); //Ҫ�����������!!!Ҫ��Ҳ����������!!!
			long ll_rs_2 = System.currentTimeMillis(); //
			ResultSetMetaData rsmd = rs.getMetaData();
			li_columncount = rsmd.getColumnCount(); // �ܹ��м���
			str_columnnames = new String[li_columncount];
			li_column_types = new int[li_columncount];
			str_column_typenames = new String[li_columncount];
			li_column_lengths = new int[li_columncount];
			li_valueMaxWidth = new int[li_columncount]; //ÿ�е�ÿ����!
			isNullAble = new String[li_columncount]; //�Ƿ�Ϊ��
			precision = new int[li_columncount]; //��ȷ��
			scale = new int[li_columncount]; //С�����λ
			for (int i = 0; i < str_columnnames.length; i++) {
				if (i == 0) {
					String isGetTableName = ServerEnvironment.getProperty("ISMETADATAGETTABLENAME"); //�Ƿ���Ҫִ��MetaDataȡ�������߼���sybase���ݿ�ᱨ���������Ӵ˲�����Ĭ����Ҫȡ��
					if (!"N".equalsIgnoreCase(isGetTableName)) {
						str_fromtablename = rsmd.getTableName(i + 1); //
					}
				}
				str_columnnames[i] = rsmd.getColumnLabel(i + 1);
				li_column_types[i] = rsmd.getColumnType(i + 1);
				if ("mysql".equalsIgnoreCase(ServerEnvironment.getInstance().getDataSourceVO(_datasourcename).getDbtype()) && -1 == li_column_types[i] && "varchar".equalsIgnoreCase(rsmd.getColumnTypeName(i + 1))) { //����Mysql��Text����
					str_column_typenames[i] = "text";//����mysql��text���շ���varchar
				} else {
					str_column_typenames[i] = rsmd.getColumnTypeName(i + 1);
				}
				li_column_lengths[i] = rsmd.getColumnDisplaySize(i + 1); //
				if (str_column_typenames[i] != null && ("clob".equals(str_column_typenames[i].toLowerCase()) || "text".equals(str_column_typenames[i].toLowerCase()))) {
					li_column_lengths[i] = Integer.MAX_VALUE;
				}
				//ע�⣬�������ԴΪOracle���ݿ⣬�ֶ�ΪClob���Ͳ��ҷ�������ʹ�õ�jdk�汾Ϊ1.6.0_26ʱ����һ�������ᱨ����ʱ��jdk�汾����Ϊ1.6.0_18��jdk1.6.0_14�Ϳ����ˡ����/2012-03-15��
				//����jdk�汾Ϊ1.6.0_26����ƽ̨����/state �Աȱ�ṹʱ�������������ݿ����иñ��Ƚϳ����Ľṹ����Ҫ�������������ĳ�����ݿ����Ѵ��ڵ��ֶ��ڿ�Ƭ�ϱ�죨ƽ̨Ĭ�Ϲ�ѡ�˱��沢�����ݿ���û�и��ֶ�ʱ���ڿ�Ƭչ��ʱ���죩
				precision[i] = rsmd.getPrecision(i + 1);
				if (precision[i] <= 0) { //�����oracle number���ͣ�������Ĭ�ϳ��� ���磺id number , ��ֵΪ�� ��by haoming 2016-05-17
					precision[i] = li_column_lengths[i];
				}
				scale[i] = rsmd.getScale(i + 1); //
				if (scale[i] < 0) {//�����oracle number���ͣ�������Ĭ�ϳ��� ���磺id number , ��ֵΪ-127 ��by haoming 2016-05-17
					scale[i] = 0;
				}
				if (rsmd.isNullable(i + 1) == 0) { //��Ϊ��
					isNullAble[i] = "N";
				} else { //����Ϊ�ջ�֪���Ƿ�Ϊ��
					isNullAble[i] = "Y";
				}
			}

			int li_rows = 0;
			//int li_warnrowcount = 3000; //
			long ll_allDataSize = 0; //��¼�²�ѯ���ݵĴ�С,���ڼ����������!!!
			TBUtil tbUtil = new TBUtil(); //
			ArrayList al_rowData = new ArrayList(); //��ǰ��Vector,�ָĳ�ArrayList,�������ܻ��Щ!
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
					if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder�ڵ���,����ʹ��LoadRundered����ʱ,�ǲ�������߼���!!!
						int li_strlen = tbUtil.getStrUnicodeLength(str_cell); //������������Ŀ��!��Ϊ�ڱ���������Ҫ�Զ�����һ�������Ŀ��,Ȼ��������ô��ڿ��!!!Ҫ��������!!!
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
			struct.setTableName(str_fromtablename); // ���ô��ĸ���ȡ��!!!!!
			struct.setHeaderName(str_columnnames); // ���ñ�ͷ�ṹ
			struct.setHeaderType(li_column_types); // ��ͷ�ֶ�����!!!
			struct.setHeaderTypeName(str_column_typenames);// �����ֶ���������
			struct.setHeaderLength(li_column_lengths); // ���
			struct.setBodyData(str_data); // ���ñ���ṹ
			struct.setIsNullAble(isNullAble);//�����Ƿ�Ϊ��
			struct.setColValueMaxLen(li_valueMaxWidth); //ÿ�е������
			struct.setPrecision(precision);
			struct.setScale(scale);

			long ll_rs_3 = System.currentTimeMillis(); //
			if (_isDebugLog) { //�Ƿ�Ҫ�����־???
				if (ll_rs_3 - ll_rs_2 > 50 || ll_rs_2 - ll_rs_1 > 50) { //�������50����,�򾯸�!!
					logger.warn("ȡ��SQL*T(DB��ʱ" + (ll_rs_2 - ll_rs_1) + ",Web��ʱ" + (ll_rs_3 - ll_rs_2) + "):" + _sql + (str_sqlTrace == null ? "" : "," + str_sqlTrace)); //
				}
			}
			if (!ServerEnvironment.isLoadRunderCall && ll_allDataSize > 0) { //�������LoadRunder�ڵ���,����ʹ��LoadRunder����ʱ,�ǲ�������߼���!!!
				new WLTInitContext().setCurrSessionCustInfoByKey("ȡ����С", new String[] { "(getTabStc[" + li_rows + "])" + _sql, "" + ll_allDataSize }); //
			}
			return struct;
		} catch (SQLException ex) {
			throw new SQLException("������Դ[" + _datasourcename + "]��ִ��SQL[" + _sql + "]����,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage()); //
		} catch (Exception ex) {
			throw new Exception("������Դ[" + _datasourcename + "]��ִ��SQL[" + _sql + "]����,ԭ��:" + ex.getMessage()); //
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

	//����SQL�õ�HashVO[],��õķ���!!!!
	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, true, false, null, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))).getHashVOs(); //
	}

	/**
	 * ����SQL��䷵��HashVO!!����ֻȡǰ������!! �ڵ������ݵȹ���ʱ�����õ��÷���!!!
	 * ��̨���ݿ��Զ����и�������Դ����ѡ��ͬ�ķ�ҳ����!! ����ܹؼ�!!
	 * @param _sql
	 * @return
	 * @throws Exception
	 * @throws SQLException
	 */
	public HashVO[] getHashVoArrayByDS(String _datasourcename, String _sql, int _topRecords) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, true, false, new int[] { 1, _topRecords }, false, false, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL"))).getHashVOs(); //
	}

	/**
	 * ����SQL��䷵��HashVO
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
	 * ȡ�ô��ṹ��HashVO����,������HashVO[]
	 * 
	 * @param _datasourcename
	 * @param _sql
	 * @param _rowArea  �з�Χ,��30-50
	 * @return
	 * @throws Exception
	 */
	public HashVOStruct getHashVoStructByDS(String _datasourcename, String _sql, boolean _isDebugLog, boolean _isUpperLimit, int[] _rowArea, boolean _isNeedCount, boolean _isImmediately) throws Exception {
		return getHashVoStructByDS(_datasourcename, _sql, _isDebugLog, _isUpperLimit, _rowArea, _isNeedCount, _isImmediately, "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")));
	}

	/**
	 * ʵ�ʵ��õķ���!!
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
		Statement stmt_count = null; //���¼����!!
		ResultSet rs = null; //
		ResultSet rs_count = null; //���¼����!!

		String str_sql_convert = null;
		String str_count_sql = null; //
		try {
			WLTInitContext initContext = new WLTInitContext(); //
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName(); //
			}
			String str_sqlTrace = null; //
			if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder����,�ż���!!
				addSQLToSessionSQLListener(_initSql); //��SQL�����������ӱ�SQL
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) { //�����Debugģʽ,���¼�µ��õĶ�ջ!!!
					str_sqlTrace = getCurrThreadTrace(); //
				}
			}

			String str_preSQL = _initSql; //�����ԭ����!!
			ArrayList al_pres = null; //Ԥ����Ĳ���!!
			if (_isPrepareSQL) {
				al_pres = new PrepareSQLUtil().prepareSQL(_initSql); //
				if (al_pres != null && al_pres.size() > 1) {
					str_preSQL = (String) al_pres.get(0); // 
				} else {
					_isPrepareSQL = false; //��������Ԥ����!!
				}
			}
			int li_from_pos = str_preSQL.toLowerCase().indexOf(" from "); //
			int li_orderby_pos = str_preSQL.toLowerCase().indexOf(" order by "); //����û��ָ�������ֶ�!

			String str_selfield = null; //ѡ����ֶ�!
			String str_initfromtable = null; //ԭʼSQL��ѯ�ı���!��Ϊ�е����ݿⲻ֪Ϊʲô��metadata��ȡ������ѯ�ı���,��ʱ��Ҫ��SQL�н���!!!
			String str_fromsql = null; //��From�����SQL
			String str_unOrderSQL = null; //ȥ������������ʣ�µ�SQL
			String str_order_cons = null; //,��������

			if (li_from_pos > 0) { //�����From
				str_selfield = str_preSQL.substring(str_preSQL.toLowerCase().indexOf("select ") + 7, li_from_pos); //ѡ����ֶ�!!!
				String str_sql_right = str_preSQL.substring(li_from_pos + 6, str_preSQL.length()); //
				if (str_sql_right.indexOf(" ") > 0) {
					str_initfromtable = str_sql_right.substring(0, str_sql_right.indexOf(" ")); //
				} else {
					str_initfromtable = str_sql_right.trim(); //
				}
				if (li_orderby_pos > 0) { //�����Orderby
					str_fromsql = str_preSQL.substring(li_from_pos, li_orderby_pos); //��From����ĵ�
				} else {
					str_fromsql = str_preSQL.substring(li_from_pos, str_preSQL.length()); //��From����ĵ�
				}
			}

			if (li_orderby_pos > 0) { //�����Orderby
				str_unOrderSQL = str_preSQL.substring(0, li_orderby_pos); //
				str_order_cons = str_preSQL.substring(li_orderby_pos, str_preSQL.length()); //��������
			} else {
				str_unOrderSQL = str_preSQL; //�������SQL
			}

			//����Ҫ�������ݿ����Ͷ�SQL���в�ͬ�ķ�ҳ����!! ����ǰ��ҳ���ƶ�������,������סѹ������!! Ψһ��Ч�İ취���������ݿ��з�ҳ!!
			str_sql_convert = str_preSQL; //�ȸ�ֵΪԭ����!!
			boolean isConvertSQL = false; //�Ƿ������ת��!! ���û�н���ת��,����ʹ��ԭ���Ĺ��α�ķ���!!!!

			//����з�ҳҪ��,��Ҫ���и��ӵķ�ҳSQL����,��ʹ�ò�ͬ�����ݿ��ҳ��������SQLת��!!! ��������Ŀ�о����ϸ�����ܲ���,���ֱ��������ݿ��з�ҳ,�������ܸ���������!
			if (_rowArea != null && _rowArea[0] >= 0) { //���ָ���˷�ҳ,����ͨ��BillList����в�ѯ������!!
				//����SQL,�����from,order�Ȳ���!!!
				DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
				String str_dbtype = dsVO.getDbtype(); //���ݿ�����!
				String str_dbVersion = dsVO.getDbversion(); //���ݿ�汾!
				if (str_dbtype.equalsIgnoreCase("ORACLE")) { //�����Oracle,��ʹ��rownum������!! ������ǰ��������εķ���!!!
					isConvertSQL = true; //ת��!!
					StringBuilder sb_sql = new StringBuilder(); //
					sb_sql.append("with tt1 as ("); //
					sb_sql.append(str_preSQL); //
					sb_sql.append(") "); //
					if (_rowArea[0] == 1) { //����ǵ�һҳ���ֿ������⴦��!��������һ���Ӳ�ѯ!�Ӷ��������!!
						sb_sql.append("select * from tt1 where rownum<=" + _rowArea[1]); //
					} else {
						sb_sql.append("select * from (select rownum as tt1_rownum,tt1.* from tt1 where rownum<=" + _rowArea[1] + ") tt2 where tt1_rownum>=" + _rowArea[0]); //
					}
					str_sql_convert = sb_sql.toString(); //�������SQL,���û�ж���order by ������һ��!
				} else if (str_dbtype.equalsIgnoreCase("SQLSERVER")) { //SQLServer
					if (str_dbVersion.startsWith("2000")) { //�����2000�������Ҫʹ������top�ߵ��ķ�����һ��!!
					} else if (str_dbVersion.startsWith("2005")) { //�����2005��ʹ��row_number()��������ת��!!
						isConvertSQL = true; //ת��!!
						StringBuilder sb_sql_new = new StringBuilder(); //
						sb_sql_new.append("with t1_ as "); //
						sb_sql_new.append("("); //
						if (str_order_cons != null) { //���ָ������������
							sb_sql_new.append("select row_number() over (" + str_order_cons + ") rownum_,"); //
						} else {
							sb_sql_new.append("select row_number() over (order by id asc) rownum_,"); ////���������Ϊ�е���û�ж���������Ϊid,�Ӷ��������!! �Ժ�����취! ��������������,������ʱ����ԭ��������Ӹ���Ϊid���ֶ�!!
						}
						sb_sql_new.append(str_unOrderSQL.substring(str_unOrderSQL.toLowerCase().indexOf("select ") + 7, str_unOrderSQL.length()).trim()); //��ԭ����select���濪ʼ�����ݽ�����!
						sb_sql_new.append(") ");
						sb_sql_new.append("select top " + (_rowArea[1] - _rowArea[0] + 1) + " * from t1_ where rownum_ >= " + _rowArea[0] + ""); //��ҳ!!!
						str_sql_convert = sb_sql_new.toString(); //
					}
				} else if (str_dbtype.equalsIgnoreCase("MYSQL")) { //Mysql,ʹ��limit��һ��!!! �� select * from pub_user where name like '%��%' order by name limit 21,40
					isConvertSQL = true; //�Ƿ�ת��SQL!!
					str_sql_convert = str_preSQL; // 
					str_sql_convert = str_sql_convert + " limit " + (_rowArea[0] - 1) + "," + (_rowArea[1] - _rowArea[0] + 1); //ʹ��limit����!!
				} else if (str_dbtype.equalsIgnoreCase("DB2")) { //db2,��ʱ����֪����ô��!!
					isConvertSQL = true; //ת��!!
					StringBuilder sb_sql_new = new StringBuilder(); //
					sb_sql_new.append("with t1_ as "); //
					sb_sql_new.append("("); //
					if (str_order_cons != null) { //���ָ������������
						sb_sql_new.append("select row_number() over (" + str_order_cons + ") rownum_,"); //
					} else {
						sb_sql_new.append("select row_number() over (order by id asc) rownum_,"); ////���������Ϊ�е���û�ж���������Ϊid,�Ӷ��������!! �Ժ�����취! ��������������,������ʱ����ԭ��������Ӹ���Ϊid���ֶ�!!
					}
					if ("*".equals(str_selfield.trim())) { //���ȡ���ֶξ���*,��DB2�и���̬�ĵط������Ǳ�����*ǰ����ϱ���������ᱨ��,SQLServer��Oracle��û���������!!!
						sb_sql_new.append(str_initfromtable + ".* " + str_fromsql); //
					} else {
						sb_sql_new.append(str_selfield + str_fromsql); //��ԭ����select���濪ʼ�����ݽ�����!
					}
					sb_sql_new.append(") ");
					sb_sql_new.append("select * from t1_ where rownum_ >= " + _rowArea[0] + "  and rownum_<=" + _rowArea[1]); //��ҳ!!!
					str_sql_convert = sb_sql_new.toString(); //
				} else { //������!
				}
			}
			if (_isImmediately) { //����Ƕ�������һ���Բ�ѯ!
				DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
				if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) {
					WLTDBConnection wltc = new WLTDBConnection(_datasourcename);
					conn = wltc.getConn();
				} else {
					String str_dburl = "jdbc:apache:commons:dbcp:" + _datasourcename; // //
					conn = DriverManager.getConnection(str_dburl); // //������������,��ʵҲ�Ǵ�dbcp����ȡ��!!!
				}
				//conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED); //����������뼶��!!��������!!
			} else {
				conn = getConn(_datasourcename).getConn(); //��Ϊ���ܲ�ȡ���ݿ�!���Ա��������,����ÿ��SQLServer������һ��,ʵ���ϻ�����!!!
			}
			if (_isDebugLog) { //���Debug
				StringBuilder sb_sql_info = new StringBuilder(); //
				sb_sql_info.append("ȡ��SQL*H" + _initSql); //
				if (_isPrepareSQL) {
					sb_sql_info.append("\r\nȡ��SQL*H(Ԥ����ת��)" + str_preSQL); //
				}
				if (isConvertSQL) { //��������˷�ҳת��
					sb_sql_info.append("\r\nȡ��SQL*H(��ҳת��)" + str_sql_convert); //
				}
				if (str_sqlTrace != null) {
					sb_sql_info.append("," + str_sqlTrace); //
				}
				logger.debug(sb_sql_info.toString());
			}
			long ll_rs_1 = System.currentTimeMillis(); //
			if (_isPrepareSQL) { //�����Ԥ����
				stmt = conn.prepareStatement(str_sql_convert); //
				for (int i = 1; i < al_pres.size(); i++) {
					((PreparedStatement) stmt).setObject(i, al_pres.get(i)); //����ֵ!!
				}
				rs = ((PreparedStatement) stmt).executeQuery(); //
			} else {
				stmt = conn.createStatement(); //
				rs = stmt.executeQuery(str_sql_convert); //
			}
			long ll_rs_2 = System.currentTimeMillis(); //
			initContext.addCurrSessionForClientTrackMsg((isConvertSQL ? "������SQL��ҳת��" : "û�н���SQL��ҳת��") + ",ʵ��ִ�е�SQL��[" + str_sql_convert + "],��ʱ[" + (ll_rs_2 - ll_rs_1) + "]\r\n"); //���ظ��ͻ���!!!
			ResultSetMetaData rsmd = rs.getMetaData(); //�õ�Ԫ����!!!
			String str_fromtablename = null; // ����
			int li_columncount = rsmd.getColumnCount(); // �ܹ��м���
			String[] str_columnnames = new String[li_columncount]; // ÿһ�е�����!!
			int[] li_column_types = new int[li_columncount]; // ÿһ�е�����
			String[] str_column_typenames = new String[li_columncount]; // ÿһ�����͵�����
			int[] li_column_lengths = new int[li_columncount]; // ÿһ�еĿ��
			int[] precision = new int[li_columncount];
			int[] scale = new int[li_columncount];
			for (int i = 1; i < rsmd.getColumnCount() + 1; i++) { //����������!!!
				if (i == 1) {
					String isGetTableName = ServerEnvironment.getProperty("ISMETADATAGETTABLENAME"); //�Ƿ���Ҫִ��MetaDataȡ�������߼���sybase���ݿ�ᱨ���������Ӵ˲�����Ĭ����Ҫȡ��
					if (!"N".equalsIgnoreCase(isGetTableName)) {
						str_fromtablename = rsmd.getTableName(i); //
					}
					if (str_fromtablename == null || str_fromtablename.equals("")) { //�е����ݿⲻ֪��Ϊʲôȡ��������!!!
						str_fromtablename = str_initfromtable; //
					}
				}
				str_columnnames[i - 1] = rsmd.getColumnLabel(i);
				li_column_types[i - 1] = rsmd.getColumnType(i);
				str_column_typenames[i - 1] = rsmd.getColumnTypeName(i); //
				li_column_lengths[i - 1] = rsmd.getColumnDisplaySize(i); //
				//ע�⣬�������ԴΪOracle���ݿ⣬�ֶ�ΪClob���Ͳ��ҷ�������ʹ�õ�jdk�汾Ϊ1.6.0_26ʱ����һ�������ᱨ����ʱ��jdk�汾����Ϊ1.6.0_18��jdk1.6.0_14�Ϳ����ˡ����/2012-03-15��
				//����jdk�汾Ϊ1.6.0_26����ƽ̨����/state �Աȱ�ṹʱ�������������ݿ����иñ��Ƚϳ����Ľṹ����Ҫ�������������ĳ�����ݿ����Ѵ��ڵ��ֶ��ڿ�Ƭ�ϱ�죨ƽ̨Ĭ�Ϲ�ѡ�˱��沢�����ݿ���û�и��ֶ�ʱ���ڿ�Ƭչ��ʱ���죩
				precision[i - 1] = rsmd.getPrecision(i);
				scale[i - 1] = rsmd.getScale(i); //
			}
			HashVOStruct hashVOStruct = new HashVOStruct(); // ����HashVOStruct
			hashVOStruct.setTableName(str_fromtablename); // ���ô��ĸ���ȡ��!!!!!
			hashVOStruct.setFromSQL(str_sql_convert); //�����Ǵ��ĸ�SQL���ɵ�!
			hashVOStruct.setHeaderName(str_columnnames); // ���ñ�ͷ�ṹ
			hashVOStruct.setHeaderType(li_column_types); // ��ͷ�ֶ�����!!!
			hashVOStruct.setHeaderTypeName(str_column_typenames);// �����ֶ���������
			hashVOStruct.setHeaderLength(li_column_lengths); // ���
			hashVOStruct.setPrecision(precision);
			hashVOStruct.setScale(scale);
			HashVO rowHVO = null; //
			int li_rows = 0;
			long ll_allDataSize = 0;//��¼��β�ѯ���ݵĴ�С!!!�������ж��Ƿ�������������!!! ���緢��һ��Զ�̵�����,�����Ĳ�ѯ���ݵ����ݷǳ���,��������10M,����Ϊ�ܿ��ܻᵼ���ڴ����!!!��OutOfMemory.
			Object value = null; //
			String str_colname = null; //
			int li_coltype = 0; //

			ArrayList al_rowData = new ArrayList(); //��ǰ��Vector,�ĳ�ArrayList,����Ҫ��һ���!
			if (limitcount <= 0) {
				limitcount = 2000;
			}
			while (rs.next()) { //��������,ѭ��ȡ��!!
				li_rows++; //������
				if (_isUpperLimit && li_rows > limitcount) { //��������޿�������,���ҳ���800����(��ǰ��500��,���������˵Ҫ800,������˵��2000),��ֱ���˳�!!����һ���ǳ���Ҫ�ı��մ�ʩ!��Ϊ���б��ѯʱ,���������������ҳ����,Ȼ���ֲ�ѯ��������,���п��ܵ���ϵͳ����,����������ڴ����!! ���Կ�����500��֮��,Ȼ������ҳ!
					li_rows = -limitcount; //д�ɸ���,��ʾ��������ҳ!
					break; //
				}
				if (!isConvertSQL && _rowArea != null && _rowArea[0] >= 0) { //����з�ҳ,��û��ʹ��ת��SQLʵ��!
					if (li_rows < _rowArea[0]) { //���С�ڼ�������ķ�ҳ����ʼ�к�,�����!!1-20,21-40
						continue;
					}
					if (li_rows > _rowArea[1]) { //������ڼ�������ķ�ҳ�Ľ����к�,��ֻ�ǿչ��α�,����������!!!
						break; //�ж�,����������! ��ǰ��ȱ���Ǿ������������ǵ�һҳ,Ȼ��10�����ļ�¼���ת�α�9����! �������ܺ���,�����жϺ�Ͳ�һ����,�����������ת�����һҳ,����Ȼ����,���þͺ�����������ĸ��ʺܵ�!
					}
				}
				rowHVO = new HashVO(); //
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) { //��������!
					str_colname = rsmd.getColumnLabel(i); // ����
					li_coltype = rsmd.getColumnType(i); // ������
					value = null; //
					if (li_coltype == Types.VARCHAR) { // ������ַ�
						value = getClobUtil().getClob(rs.getString(i));
					} else if (li_coltype == Types.NUMERIC) { // �����Number
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.DATE || li_coltype == Types.TIMESTAMP) { // ��������ڻ�ʱ������,ͳͳ��ȷ����,Oracle�е�Date������Types.DATE,�����ص�ֵ��Timestamp!!!
						value = rs.getTimestamp(i);
					} else if (li_coltype == Types.SMALLINT) { // ���������
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Integer(bigDecimal.intValue()); // 
						}
					} else if (li_coltype == Types.INTEGER) { // ���������
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Long(bigDecimal.longValue()); // (rs.getBigDecimal(i)
						}
					} else if (li_coltype == Types.DECIMAL || li_coltype == Types.DOUBLE || li_coltype == Types.DOUBLE || li_coltype == Types.FLOAT) {
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.CLOB) { // �����Clob����,��Ҫʹ��Read���ж�ȡ!!!
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
					rowHVO.setAttributeValue(str_colname, value); // ����ֵ!!!

					//�������ݵĴ�С,�����ж�һ��Զ�̵��������޷������������Ĳ�ѯ!!!�����ܻ����OutOfMemory�Ŀ�����!!!
					if (!ServerEnvironment.isLoadRunderCall && value != null) { //�������LoadRunder����,�ż���!!,����LoadRunder����ʱ��������߼��Ӷ��������ٶ�!!!!
						ll_allDataSize = ll_allDataSize + String.valueOf(value).length(); //ֱ��תString �㳤��,��һ������,��Ϊ��ͨ�������;�������!
					}
				}
				//BS������Ȩ�޹���!!!
				al_rowData.add(rowHVO); ////
			} //end while..

			HashVO[] vos = (HashVO[]) al_rowData.toArray(new HashVO[0]); //ת��һ��!!!
			hashVOStruct.setHashVOs(vos); //��������!!
			long ll_rs_3 = System.currentTimeMillis(); //
			if (_isDebugLog) {
				if (ll_rs_3 - ll_rs_2 > 50 || ll_rs_2 - ll_rs_1 > 50) { //�������50����,�򾯸�!!
					logger.warn("ȡ��SQL*H" + str_sql_convert + "(DB��ʱ" + (ll_rs_2 - ll_rs_1) + ",Web��ʱ" + (ll_rs_3 - ll_rs_2) + ",������Ҫ�Ż�!"); ////
				}
			}
			if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder����,�ż���!!,����LoadRunder����ʱ��������߼��Ӷ��������ٶ�!!!
				initContext.setCurrSessionCustInfoByKey("ȡ����С", new String[] { "(getHashVOs[" + li_rows + "])" + str_sql_convert, "" + ll_allDataSize }); //��ll_allDataSize�Ĵ�С���� CurrSessionVO�е�custMap��,key��"ȡ����",value�Ƕ�ά����(��һ����[getHashVOs/getTableStruct/getStringArray]SQL,�ڶ����Ǵ�С)
			}

			//������!!
			if (_isNeedCount && _rowArea != null && _rowArea[0] >= 0) { //����з�ҳ,������ʹ��count(*)��������!!!��Ϊ��������ʱֻҪ��ҳ��������Ҫ����������Ҫ�ټ��Ͽ���_isNeedCount,������ֻ�����б��ѯʱ�õ�!!!�������б��Ƶ�!!!
				long ll_rs_count_1 = System.currentTimeMillis(); //
				int li_allCount = 0; //
				str_count_sql = "select count(*) " + str_unOrderSQL.substring(li_from_pos, str_unOrderSQL.length()); //��������SQL!!!
				if (_isPrepareSQL) { //�����Ԥ����
					stmt_count = conn.prepareStatement(str_count_sql); //
					for (int i = 1; i < al_pres.size(); i++) {
						((PreparedStatement) stmt_count).setObject(i, al_pres.get(i)); //����ֵ!!
					}
					rs_count = ((PreparedStatement) stmt_count).executeQuery(); //
				} else { //�������Ԥ����!!!
					stmt_count = conn.createStatement(); //
					rs_count = stmt_count.executeQuery(str_count_sql); //
				}
				long ll_rs_count_2 = System.currentTimeMillis(); //
				if (rs_count.next()) {
					li_allCount = rs_count.getInt(1); //
				}
				hashVOStruct.setTotalRecordCount(li_allCount); //���ü�¼����!!!
				if (_isDebugLog) { //�������LoadRunder����,�ż���!!
					logger.debug("������SQL(�ӿ���ȡ)" + "(DB��ʱ" + (ll_rs_count_2 - ll_rs_count_1) + ")" + ":" + str_count_sql); ////
				}
				initContext.addCurrSessionForClientTrackMsg("������Count()����,ʵ��ִ�е�SQL��:[" + str_count_sql + "],��ʱ[" + (ll_rs_count_2 - ll_rs_count_1) + "]\r\n"); //
			} else {
				hashVOStruct.setTotalRecordCount(li_rows); //����ʵ�ʵ��ܼƼ�¼��
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
			System.err.println("ִ��sql����[" + str_errsql + "]"); //
			ex.printStackTrace(); //
			throw new SQLException("������Դ[" + _datasourcename + "]��ִ��SQL[" + str_errsql + "]����,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage() + ",\nԭʼSQL��:[" + _initSql + "]");
		} catch (Exception ex) {
			//System.out.println("str_sql_convert2��ɶ[" + str_sql_convert2 + "],str_sql_convert����ɶ[" + str_sql_convert + "],�Ƿ�Ԥ����[" + _isPrepareSQL + "]");
			ex.printStackTrace(); //
			throw new Exception("������Դ[" + _datasourcename + "]��ִ��SQL(���ܲ�������,����ת�����)[" + _initSql + "]����,ԭ��:" + ex.getMessage());
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
			if (_isImmediately) { //����Ƕ��������ѯ
				if (conn != null) {
					try {
						conn.close(); //�ر�����!!
					} catch (Exception exx2) {
					}
				}
			}
		}
	}

	/**
	 * �����ܲ���ʱ������¼ʱ�ļ���SQL��ֻ�ǲ�ѯ,Ӧ��ʹ��Ԥ����ķ�ʽ!
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
			if (!ServerEnvironment.isLoadRunderCall) { //�������LoadRunder����,�ż���!!
				addSQLToSessionSQLListener(_sql); //��SQL�����������ӱ�SQL
				if (ServerEnvironment.getInstance().getLog4jConfigVO().getServer_level().equalsIgnoreCase("DEBUG")) { //�����Debugģʽ,���¼�µ��õĶ�ջ!!!
					str_sqlTrace = getCurrThreadTrace(); //
				}
			}
			logger.debug(_sql + str_sqlTrace); //�����־!!

			conn = getConn(_datasourcename).getConn(); //��Ϊ���ܲ�ȡ���ݿ�!���Ա��������,����ÿ��SQLServer������һ��,ʵ���ϻ�����!!!
			p_stmt = conn.prepareStatement(_sql); //
			for (int i = 0; i < _pars.length; i++) {
				if (_pars[i] == null || "".equals(("" + _pars[i]).trim()) || "null".equalsIgnoreCase(("" + _pars[i]).trim())) {
					p_stmt.setNull(i + 1, java.sql.Types.VARCHAR); //
				} else {
					if (_pars[i] instanceof String) {
						p_stmt.setString(i + 1, (String) _pars[i]); //ִ�к�..
					} else if (_pars[i] instanceof Integer) {
						p_stmt.setInt(i + 1, ((Integer) _pars[i]).intValue()); //
					} else {
						p_stmt.setString(i + 1, "" + _pars[i]); //ִ�к�..
					}
				}
			}

			rs = p_stmt.executeQuery(); //��ѯ
			ResultSetMetaData rsmd = rs.getMetaData(); //�õ�Ԫ����!!!
			HashVO rowHVO = null; //
			Object value = null; //
			String str_colname = null; //
			int li_coltype = 0; //
			ArrayList al_rowData = new ArrayList(); //��ǰ��Vector,�ĳ�ArrayList,����Ҫ��һ���!
			while (rs.next()) { //��������,ѭ��ȡ��!!
				rowHVO = new HashVO(); //
				for (int i = 1; i < rsmd.getColumnCount() + 1; i++) { //��������!
					str_colname = rsmd.getColumnLabel(i); // ����
					li_coltype = rsmd.getColumnType(i); // ������
					value = null; //
					if (li_coltype == Types.VARCHAR) { // ������ַ�
						value = getClobUtil().getClob(rs.getString(i));
					} else if (li_coltype == Types.NUMERIC) { // �����Number
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.DATE || li_coltype == Types.TIMESTAMP) { // ��������ڻ�ʱ������,ͳͳ��ȷ����,Oracle�е�Date������Types.DATE,�����ص�ֵ��Timestamp!!!
						value = rs.getTimestamp(i);
					} else if (li_coltype == Types.SMALLINT) { // ���������
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Integer(bigDecimal.intValue()); // 
						}
					} else if (li_coltype == Types.INTEGER) { // ���������
						BigDecimal bigDecimal = rs.getBigDecimal(i); //
						if (bigDecimal != null) {
							value = new Long(bigDecimal.longValue()); // (rs.getBigDecimal(i)
						}
					} else if (li_coltype == Types.DECIMAL || li_coltype == Types.DOUBLE || li_coltype == Types.DOUBLE || li_coltype == Types.FLOAT) {
						value = rs.getBigDecimal(i); //
					} else if (li_coltype == Types.CLOB) { // �����Clob����,��Ҫʹ��Read���ж�ȡ!!!
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
					rowHVO.setAttributeValue(str_colname, value); // ����ֵ!!!
				}
				//BS������Ȩ�޹���!!!
				al_rowData.add(rowHVO); //�����б�
			} //end while..
			HashVO[] vos = (HashVO[]) al_rowData.toArray(new HashVO[0]); //ת��һ��!!!
			return vos; //����
		} catch (SQLException ex) {
			ex.printStackTrace(); //
			throw new SQLException("������Դ[" + _datasourcename + "]��ִ��SQL[" + _sql + "]����,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage());
		} catch (Exception ex) { //
			ex.printStackTrace(); //
			throw new Exception("������Դ[" + _datasourcename + "]��ִ��SQL(���ܲ�������,����ת�����)[" + _sql + "]����,ԭ��:" + ex.getMessage()); //
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
	 * ����һ��SQL�������HashVO����,�����ݵ����˳�����Զ������һ����,��������˳�����!!��ʵ����һ����!!
	 * ��ĳ�ֻ�����˵,����ȡ��where linkCode like '0001%' order by linkcode�Ļ���,��ʹ��_rootNodeCondition��ʵ�ֹ���,����id=125(���ݷ��е�����)��ʹ��parentid��ʵ�����Ĺ���,seq��ʵ���ֵܼ������!
	 * ����linkCode����������,linkCode�����ݿ��з��ؾ�����Ҫ��[���ݷ���]����������,�����ֻ�������ȡ���������ݣ�Ȼ�����ڴ��й������ͽṹ��Ȼ�����ҳ�[���ݷ���]�������ӽ��!���Ա�Ȼ���ܽϵ�!
	 * ��ֻҪ��3�������µ�����Ӧ�û����ܽ��ܵģ���Ϊ�����ڷ�����������Ӳ����ǿ���������ֲ���Щ������������3���������ͽṹʵ��Ӧ���к��ټ���
	 * ���л����뼼������SQL������root���ˣ����ֳ����������:1.SQL�й���������rootConditionû��(�����ҳ�ĳһ���ɵ���������) 2.SQL�޹�������,��rootcondition��(�����ҳ����ݷ���)
	  * @param _datasourcename ����Դ����
	 * @param _sql  ��һSQL���,����Ҫorder by seq,д��Ҳ��Ҫ��,�����Ȼ�ǰ� _seqField�ֶ�������! �������ǵ�һ�ι���!��ȡ����Щ�������ݣ��������Ȼ�����м�!!������SQL���˺�Ӧ��һ������������
	 * @param _idField  ���ͽṹ�Ĺ��������id�ֶ���,һ�㶼��id
	 * @param _parentIDField  ���ͽṹ�Ĺ��������parentid�ֶ���,һ�㶼��parentid
	 * @param _rootNodeCondition ָ��ȡ���ͽṹ�е��ļ�����������������!!����id=125(���ݷ���), ���������ǵڶ��ι���!!���ڹ�������ͽṹ֮��,�ٴδ����ͽṹ��ȡָ�����ṹ������!!
	 * @return HashVO���飬ֻ������˳����ˣ���
	 * @throws Exception
	 * ����: 
	 * 1.getHashVoArrayAsTreeStructByDS(null,"select * from pub_corp_dept","id","parentid","seq","id=125");  //ȡ�����ݷ��е����л�����
	 * 2.getHashVoArrayAsTreeStructByDS(null,"select * from law_content where lawpropid='561'","id","parentid","seq",null);  //ȡ������[����ͨ��]����������
	 */
	public HashVO[] getHashVoArrayAsTreeStructByDS(String _datasourcename, String _sql, String _idField, String _parentIDField, String _seqField, String _rootNodeCondition) throws Exception {
		HashVO[] hvsData = getHashVoArrayByDS(_datasourcename, _sql); //
		if (hvsData == null || hvsData.length == 0) {
			return hvsData; //
		}
		TBUtil tbUtil = new TBUtil(); //
		//Ӧ��������һ��,����seq,�����������ܺ���,��Ϊ��ʱ�Ұְ�ʱ�ҵò���,�о�Ӧ�øĳ��Ҷ��ӵ��㷨,Ȼ�������ҵ��Ķ��ӽ�������!
		if (_seqField != null && !_seqField.trim().equals("")) { //���������seq
			tbUtil.sortHashVOs(hvsData, new String[][] { { _seqField, "N", "Y" } }); ////����������
		}

		DefaultMutableTreeNode[] node_level_1 = new DefaultMutableTreeNode[hvsData.length]; //�������
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("�����");
		long t1 = System.currentTimeMillis();
		HashMap map_parent = new HashMap();
		for (int i = 0; i < node_level_1.length; i++) {
			hvsData[i].setEqualsFieldName(_idField); //���ñȽϵ�ֵ�ֶ�
			node_level_1[i] = new DefaultMutableTreeNode(hvsData[i]); //
			map_parent.put(hvsData[i].getStringValue(_idField), node_level_1[i]); //���ڹ�ϣ����ע���..
			//			rootNode.add(node_level_1[i]); //���һ���������нڵ�ŵ����ڵ��£����´�ѭ���Ҹ���ʱ���Ͽ����Ĺ�ϵ�ܺ�ʱ���������ٵ�ʱ�����ԡ���3W�������������ӵ�root�£���ѭ���Ҹ��ף���ʱ400-500ms�����ڿ������̵�60ms����[����2012-03-31]
		}

		HashVO nodeVO = null; //
		String str_pk_parentPK = null; //
		for (int i = 0; i < node_level_1.length; i++) {
			nodeVO = (HashVO) node_level_1[i].getUserObject(); //��ȡ�õ�ǰ����ֵ
			str_pk_parentPK = nodeVO.getStringValue(_parentIDField); //��������
			if (str_pk_parentPK == null || str_pk_parentPK.trim().equals("")) { //������׽��Ϊ��,��������..
				rootNode.add(node_level_1[i]); // ���û�и�����Ϣ���ͼӵ����ڵ��¡�
				continue;
			}
			DefaultMutableTreeNode parentnode = (DefaultMutableTreeNode) map_parent.get(str_pk_parentPK); //
			if (parentnode != null) { //����ҵ��ְ���..
				try {
					parentnode.add(node_level_1[i]); //�ڰְ����������..
				} catch (Exception ex) {
					WLTLogger.getLogger(this).error("��[" + parentnode + "]�ϴ����ӽ��[" + node_level_1[i] + "]ʧ��!!", ex); //
				}
			} else {
				rootNode.add(node_level_1[i]); //���û���ҵ����ף�Ҳ�ӵ����ڵ��¡�
			}
		}
		//		System.out.println("�Ҹ��ף�" + (System.currentTimeMillis() - t1));
		BSUtil bsUtil = new BSUtil(); //

		if (_rootNodeCondition == null || _rootNodeCondition.trim().equals("")) { //���û�ж��ι�������,��ֱ�ӷ���..
			DefaultMutableTreeNode[] allNodes = getOneNodeAllChildrenNodes(rootNode); //�����ӽ��,����Ҫ���
			HashVO[] returnVOs = new HashVO[allNodes.length - 1]; //
			for (int i = 1; i < allNodes.length; i++) { //����㲻��!!
				returnVOs[i - 1] = (HashVO) allNodes[i].getUserObject(); //
				returnVOs[i - 1].setAttributeValue("$level", allNodes[i].getLevel()); //���뼶��α��
				String[] str_parentPathIdNames = bsUtil.getOneTreeNodeParentPathItemValue(allNodes[i], new String[] { _idField, "name" }); //��ϵ·�������н���idƴ��һ��!!
				returnVOs[i - 1].setAttributeValue("$parentpathids", str_parentPathIdNames[0]); //������·�������н���id����
				returnVOs[i - 1].setAttributeValue("$parentpathnames", str_parentPathIdNames[1]); //���뼶��α��
				returnVOs[i - 1].setAttributeValue("$parentpathnamelink", getPathNameSpans(tbUtil, str_parentPathIdNames[1], false)); //���뼶��α��,�ǡ���ҵ����-�Ϻ�����-���沿��������
				returnVOs[i - 1].setAttributeValue("$parentpathnamelink1", getPathNameSpans(tbUtil, str_parentPathIdNames[1], true)); //���뼶��α��,�ǡ��Ϻ�����-���沿�������Ӽ��Զ���ȡ����һ��!��Ϊ��һ���Ƿϻ�,
				if (allNodes[i].isLeaf()) { //�����Ҷ�ӽ��,�������
					returnVOs[i - 1].setAttributeValue("$isleaf", "Y"); //
				}
			}
			//			System.out.println("����" + (System.currentTimeMillis() - t1));
			return returnVOs; //
		} else { //����ж��ι���!
			String str_key = null;
			String[] str_values = null; //
			if (_rootNodeCondition.indexOf("=") > 0) { //������ڻ��ƣ���
				str_key = _rootNodeCondition.substring(0, _rootNodeCondition.indexOf("=")).trim(); //
				String str_value = _rootNodeCondition.substring(_rootNodeCondition.indexOf("=") + 1, _rootNodeCondition.length()).trim(); //
				str_value = tbUtil.replaceAll(str_value, "'", ""); //�����е�����!��ȥ��
				str_values = new String[] { str_value }; //
			} else if (_rootNodeCondition.indexOf(" in") > 0) { //�����in���ƣ���
				str_key = _rootNodeCondition.substring(0, _rootNodeCondition.indexOf(" in")).trim(); //
				String str_value = _rootNodeCondition.substring(_rootNodeCondition.indexOf(" in") + 3, _rootNodeCondition.length()).trim(); //
				str_value = tbUtil.replaceAll(str_value, "(", ""); //ȥ��������
				str_value = tbUtil.replaceAll(str_value, ")", ""); //ȥ��������
				str_value = tbUtil.replaceAll(str_value, "'", ""); //ȥ��������
				str_values = tbUtil.split(str_value, ","); //���ݶ��Ž��зָ�!!
			} else if (_rootNodeCondition.indexOf(" like ") > 0) { //�����Like����,���硾��չ�������� like '%;���沿;%'��
				//�Ժ���ʱ��ʵ��....
			}

			//
			ArrayList al_return = new ArrayList(); //
			if (str_key != null) {
				DefaultMutableTreeNode[] allNodes = getOneNodeAllChildrenNodes(rootNode); //�����ӽ��,����Ҫ���
				HashVO hvo_temp = null; //
				for (int i = 1; i < allNodes.length; i++) { //�������н��!
					hvo_temp = (HashVO) allNodes[i].getUserObject(); //ȡ�ý���ֵ
					String str_itemValue = hvo_temp.getStringValue(str_key); //�����ֵ!
					if (str_itemValue != null && tbUtil.isExistInArray(str_itemValue, str_values)) { //����ý������������ι�����������ָ�������鷶Χ�У��������ҳ��������ӽ��...
						DefaultMutableTreeNode[] allChildrenNodes = getOneNodeAllChildrenNodes(allNodes[i]); //�ҳ��ý���µ������ӽ��,�����Լ�
						for (int j = 0; j < allChildrenNodes.length; j++) {
							HashVO hvo_children = (HashVO) allChildrenNodes[j].getUserObject(); //ȡ�ý���ֵ
							hvo_children.setAttributeValue("$level", "" + allChildrenNodes[j].getLevel()); //���뼶��α��!!����Ҫ���븸��·������ID��·��!!��";"���!
							String[] str_parentPathIdNames = bsUtil.getOneTreeNodeParentPathItemValue(allChildrenNodes[j], new String[] { _idField, "name" }); //��ϵ·�������н���idƴ��һ��!!
							hvo_children.setAttributeValue("$parentpathids", str_parentPathIdNames[0]); //���뼶��α��
							hvo_children.setAttributeValue("$parentpathnames", str_parentPathIdNames[1]); //���뼶��α��
							hvo_children.setAttributeValue("$parentpathnamelink", getPathNameSpans(tbUtil, str_parentPathIdNames[1], false)); //���뼶��α�У��ǡ���ҵ����-�Ϻ�����-���沿��������
							hvo_children.setAttributeValue("$parentpathnamelink1", getPathNameSpans(tbUtil, str_parentPathIdNames[1], true)); //���뼶��α�У��ǡ��Ϻ�����-���沿�������Ӽ��Զ���ȡ����һ��!��Ϊ��һ���Ƿϻ�,
							if (allChildrenNodes[j].isLeaf()) {
								hvo_children.setAttributeValue("$isleaf", "Y"); //�����Ҷ�ӽ��!!�������
							}
							if (!al_return.contains(hvo_children)) { //���û�м����,�����,������ܲ����ظ����!!
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
		int li_startLevel = 0; //Ĭ����ȫ��,
		if (_isTrim1) { //���Ҫ�ص���һ��,���1��ʼ!!
			li_startLevel = 1;
		}
		for (int k = li_startLevel; k < str_nameItems.length; k++) { //�ӵ�һλ��ʼ,��ȥ����ҵ����!!
			sb_names.append(str_nameItems[k]); //
			if (k != str_nameItems.length - 1) {
				sb_names.append("-"); //
			}
		}
		return sb_names.toString(); //
	}

	//ȡ�õ�ǰ�̶߳�ջ˵��
	private String getCurrThreadTrace() {
		StackTraceElement stackTrace[] = Thread.currentThread().getStackTrace(); ////�õ����ö�ջ...
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
	 * ����ĳһ����¼ȥ���ͽṹ���ҵ�����·�������м�¼!!������һ�ַ�����������
	 * ��һ�ַ�����һ����ȡ����������,Ȼ����Java�м���!
	 * ����һ�ַ�����һ��������,ÿ�ζ���һ�����ݿ�!!��Ϊһ��һ�����ͽṹ�Ĳ�β��ᳬ��5��!!�������ܹ�����̫��!!!
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
		HashVO[] parentVOs = bsUtil.getTreePathVOsByOneRecord(hvsLeaf[0], _tableName, _idFieldName, _parentIdFieldName); //ȡ�����и���·���ĵļ�¼!!
		return parentVOs; //
	}

	/**
	 * ����һ����¼ȡ�����ͽṹ��
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
	 * �ҵ�һ����¼������������!ÿ�ζ�ȡ���ݿ�!!!
	 * ����β���̫��! 
	 * @param _datasourcename
	 * @param _tableName
	 * @param _idFieldName
	 * @param _parentIdFieldName
	 * @param _whereField
	 * @param _whereCondition
	 * @return
	 */
	public HashVO[] getTreeChildVOsByOneRecord(String _datasourcename, String _tableName, String _idFieldName, String _parentIdFieldName, String _whereCondition) throws Exception {
		HashVO[] hvsRoot = getHashVoArrayByDS(_datasourcename, "select * from " + _tableName + " where 1=1  " + (_whereCondition == null ? "" : " and (" + _whereCondition + ")")); //��ѯSQL
		ArrayList al_result = new ArrayList(); ////
		addAllChildren(_datasourcename, hvsRoot, _tableName, _idFieldName, _parentIdFieldName, al_result); //�ݹ����,�ҳ���������!!!
		return (HashVO[]) al_result.toArray(new HashVO[0]); ////
	}

	//�ݹ麯��,�ҳ�����������!!
	private void addAllChildren(String _datasourcename, HashVO[] _hvsRoots, String _tableName, String _idFieldName, String _parentIdFieldName, ArrayList _al_result) throws Exception {
		for (int i = 0; i < _hvsRoots.length; i++) {
			_al_result.add(_hvsRoots[i]); //
		}
		TBUtil tbUtil = new TBUtil(); //
		ArrayList al_ids = new ArrayList(); //
		for (int i = 0; i < _hvsRoots.length; i++) {
			String str_id = _hvsRoots[i].getStringValue(_idFieldName); //����
			al_ids.add(str_id); //��������!!
		}
		HashVO[] hvsChildren = getHashVoArrayByDS(null, "select * from " + _tableName + " where " + _parentIdFieldName + " in (" + tbUtil.getInCondition(al_ids) + ")"); //�ҳ��ҵĶ���
		if (hvsChildren == null || hvsChildren.length == 0) { //���һ������û��,��ֱ�ӷ���!!!
			return;
		}
		addAllChildren(_datasourcename, hvsChildren, _tableName, _idFieldName, _parentIdFieldName, _al_result); //����������!!
	}

	/**
	 * ȡ��ĳ�����������ӽ��..
	 * @param _node
	 * @return
	 */
	private DefaultMutableTreeNode[] getOneNodeAllChildrenNodes(TreeNode _node) {
		ArrayList vector = new ArrayList();
		visitAllNodes(vector, _node); //
		DefaultMutableTreeNode[] allNodes = (DefaultMutableTreeNode[]) vector.toArray(new DefaultMutableTreeNode[0]); //�������н��..
		return allNodes; //
	}

	private void visitAllNodes(ArrayList _vector, TreeNode node) {
		_vector.add(node); // ����ý��
		if (node.getChildCount() >= 0) { //������ӽ��,�����ÿ���ӽ�㣬�ݹ���ñ�����
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) e.nextElement(); // �ҵ��ö���!!
				visitAllNodes(_vector, childNode); // �������Ҹö���
			}
		}
	}

	/**
	 * ͨ�������Ӳ�ѯSQL,��ʵ����߽�Ч��������ʹ��left outer join��SQL�﷨�ˣ�������Java�м���ʵ��ͬ����Ч��
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
						throw new WLTAppException((new StringBuilder("ִ���Ӳ�ѯ SQL[")).append(_childsqls[j]).append("] ��ʽ����,û�ж��������,����!").toString());
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
	 * ͨ�������Ӳ�ѯSQL,��ʵ����߽�Ч��������ʹ��left outer join��SQL�﷨�ˣ�������Java�м���ʵ��ͬ����Ч��
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

		for (int j = 0; j < _childVOs.length; j++) { //����ÿ���Ӳ�ѯ
			String str_foreignFieldname = _childVOs[j].getForeignField(); //
			String str_parentfieldname = _childVOs[j].getParentField(); //
			for (int p = 0; p < hvs_parent.length; p++) { //��������¼
				if (hvs_parent[p].getStringValue(str_parentfieldname) != null) { //������׵�ֵ��Ϊ��
					String str_childcolumnkeys[] = obj_childresults[j].getHeaderName(); //
					HashVO hvo_child[] = obj_childresults[j].getHashVOs();
					for (int q = 0; q < hvo_child.length; q++) { //���������Ӳ�ѯ��¼
						if (!hvo_child[q].getStringValue(str_foreignFieldname).equals(hvs_parent[p].getStringValue(str_parentfieldname))) { //���ûƷ����
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
	 * ִ��һ��SQL
	 * @param _datasourcename
	 * @param _sqlBuilder
	 * @return
	 * @throws Exception
	 */
	public int executeUpdateByDS(String _datasourcename, SQLBuilderIfc _sqlBuilder) throws Exception {
		return executeUpdateByDS(_datasourcename, _sqlBuilder.toString()); //
	}

	/**
	 * ִ��Update���!!!!!
	 * @param _datasourcename
	 * @param _sql
	 * @return
	 * @throws Exception
	 */
	public int executeUpdateByDS(String _datasourcename, String _sql) throws Exception {
		return executeUpdateByDS(_datasourcename, _sql, true); //
	}

	/**
	 * ִ��һ��SQL!
	 * 
	 * @param _sql
	 * @throws Exception
	 */
	public int executeUpdateByDS(String _datasourcename, String _sql, boolean _isDebugLog) throws Exception {
		return executeBatchByDS(_datasourcename, new String[] { _sql }, _isDebugLog)[0]; //����ת������SQL�ķ���!!
	}

	/**
	 * ִ��һ��SQL!
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
			addSQLToSessionSQLListener(_sql); //��SQL�����������ӱ�SQL
			conn = new WLTInitContext().getConn(_datasourcename); // ȡ�����ݿ�����!
			WLTLogger.getLogger(this).debug(_sql); //
			p_stmt = conn.prepareStatement(_sql); // �����α�,����һ��Զ�̵��ö�����ͬһ�����н���,��ȡ�õ���ͬһ��Connection!!
			for (int i = 0; i < _macro.length; i++) {
				p_stmt.setString(i + 1, _macro[i]); //ִ�к�..
			}
			int li_count = p_stmt.executeUpdate(); // �޸�
			// long ll_2 = System.currentTimeMillis();
			return li_count; //
		} catch (SQLException ex) {
			// ex.printStackTrace(); //
			throw new SQLException("������Դ[" + _datasourcename + "]��ִ������SQL����:\r\n" + _sql + "\r\n�������[" + ex.getErrorCode() + "]\r\n״̬[" + ex.getSQLState() + "]\r\nԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("������Դ[" + _datasourcename + "]ִ��SQL:\r\n" + _sql + "\r\n����,ԭ��:" + ex.getMessage());
		} finally {
			try {
				if (p_stmt != null) {
					p_stmt.close(); // �ر��α�
				}
			} catch (Exception ex) {
			}
		}
	}

	/**
	 * ִ�к����
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
			addSQLToSessionSQLListener(_sql); //��SQL�����������ӱ�SQL
			conn = new WLTInitContext().getConn(_datasourcename); // ȡ�����ݿ�����!
			WLTLogger.getLogger(this).debug(_sql); //
			p_stmt = conn.prepareStatement(_sql); // �����α�,����һ��Զ�̵��ö�����ͬһ�����н���,��ȡ�õ���ͬһ��Connection!!
			if (_macrovalues != null) {
				for (int i = 0; i < _macrovalues.length; i++) {
					Reader clobReader = new StringReader(_macrovalues[i]);
					p_stmt.setCharacterStream(i + 1, clobReader, _macrovalues[i].length()); // �����ַ���!!!
				}
			}

			int li_count = p_stmt.executeUpdate(); // �޸�
			// long ll_2 = System.currentTimeMillis();
			return li_count; //
		} catch (SQLException ex) {
			// ex.printStackTrace(); //
			throw new SQLException("������Դ[" + _datasourcename + "]��ִ������SQL����:\r\n" + _sql + "\r\n�������[" + ex.getErrorCode() + "]\r\n״̬[" + ex.getSQLState() + "]\r\nԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("������Դ[" + _datasourcename + "]ִ��SQL:\r\n" + _sql + "\r\n����,ԭ��:" + ex.getMessage());
		} finally {
			try {
				if (p_stmt != null) {
					p_stmt.close(); // �ر��α�
				}
			} catch (Exception ex) {
			}
		}
	}

	//����������뼶����ύ
	public int executeUpdateByDSAutoCommit(String _datasourcename, String _sql) throws Exception {
		return executeUpdateByDSImmediately(_datasourcename, _sql, true, true); //
	}

	/**
	 * �����ύһ��SQL����
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
	 * ������һ��������,�����ύһ��SQL,�����ں�̨������..����������ʱʹ��!!! �÷�������¶���ͻ���,��Ϊ�ͻ���û��Ҫ���������
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
			addSQLToSessionSQLListener(_sql); //��SQL�����������ӱ�SQL
			String str_dburl = "jdbc:apache:commons:dbcp:" + _datasourcename; // //
			DataSourceVO dsVO = ServerEnvironment.getInstance().getDataSourceVO(_datasourcename); //
			if (dsVO.getProvider_url() != null && dsVO.getInitial_context_factory() != null) {
				WLTDBConnection wltc = new WLTDBConnection(_datasourcename);
				conn = wltc.getConn();
			} else {
				conn = DriverManager.getConnection(str_dburl); // //������������,��ʵҲ�Ǵ�dbcp����ȡ��!!!
			}
			//�����ǿ������:��ǰ����Ҳ�����񼶱�,��ʵ���Ǹ����һ������ύ�ǲ���Ҫ���ġ������
			//���ʴ���Ŀѹ�������оͷ���,update pub_user set DESKTOPSTYLE='2' where id=21,��������!
			//ԭ�������,ѹ������������ͬһ���û�,Ȼ���޸��޸ĵ�ͬһ����¼,Ȼ���������񼶱�,���Բ�������!
			//ʵ��������SQL��������Ҫ����
			//TRANSACTION_READ_COMMITTED(������) TRANSACTION_READ_UNCOMMITTED(������)
			if (_autoCommit) {
				//conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED); //����������뼶��!
				conn.setAutoCommit(true); //�����ύ
			} else {
				conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED);
				conn.setAutoCommit(false); // �����ﶨ����!!
			}

			if (_isDebugLog) {
				WLTLogger.getLogger(this).debug(_sql); //
			}
			boolean isPrepareSQL = "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")); //
			int li_count = 0; //
			if (!isPrepareSQL) {
				stmt = conn.createStatement(); // �����α�,����һ��Զ�̵��ö�����ͬһ�����н���,��ȡ�õ���ͬһ��Connection!!
				li_count = stmt.executeUpdate(_sql); // �޸�
			} else {
				PrepareSQLUtil preSQLUtil = new PrepareSQLUtil(); //
				ArrayList al_pres = preSQLUtil.prepareSQL(_sql); //ArrayL
				if (al_pres == null || al_pres.size() <= 1) { //�������ʧ��!!
					stmt = conn.createStatement(); // �����α�,����һ��Զ�̵��ö�����ͬһ�����н���,��ȡ�õ���ͬһ��Connection!!
					li_count = stmt.executeUpdate(_sql); // �޸�
				} else {
					String str_preSQL = (String) al_pres.get(0); //��һ����SQL
					stmt = conn.prepareStatement(str_preSQL); //Ԥ����!!!
					for (int i = 1; i < al_pres.size(); i++) {
						((PreparedStatement) stmt).setObject(i, al_pres.get(i)); //
					}
					li_count = ((PreparedStatement) stmt).executeUpdate(); //
				}
			}
			conn.commit(); // �����ύ,��ؼ�!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			return li_count; //
		} catch (SQLException ex) {
			try {
				conn.rollback();
			} catch (Exception exx) {
			}
			// e.printStackTrace(); //
			throw new SQLException("������Դ[" + _datasourcename + "]�϶�������ִ������SQL����:\r\n" + _sql + "\r\n�������[" + ex.getErrorCode() + "]\r\n״̬[" + ex.getSQLState() + "]\r\nԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			// e.printStackTrace(); //
			throw new Exception("������Դ[" + _datasourcename + "]�϶�������ִ��SQL:\r\n" + _sql + "\r\n����,ԭ��:" + ex.getMessage()); //
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

			conn = new WLTInitContext().getConn(_datasourcename); // ȡ�����ݿ�����!

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

			addSQLToSessionSQLListener(sql_insert.toString()); //��SQL�����������ӱ�SQL
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
	 * һ����ִ��һ��SQL
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

	//����ִ��SQL
	public int[] executeBatchByDS(String _datasourcename, String[] _sqls) throws Exception {
		return executeBatchByDS(_datasourcename, _sqls, true); //
	}

	/**
	 * һ����ִ��һ��SQL
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
			list = Arrays.asList(_sqls.split(";")); // ת����һ��List
		} else {
			list = Arrays.asList(new TBUtil().convertHexStringToStr(_sqls).split(";")); // ת����һ��List
		}
		executeBatchByDS(_datasourcename, list, false); //
	}

	/**
	 * ����ִ��SQL
	 * @param _datasourcename
	 * @param _sqllist
	 * @param _isDebugLog
	 * @throws Exception
	 */
	public void executeBatchByDS(String _datasourcename, java.util.List _sqllist) throws Exception {
		executeBatchByDS(_datasourcename, _sqllist, true); //
	}

	/**
	 * ����һ��SQL��������¼���ݿ����־
	 * @param _sql
	 * @return
	 */
	private String[] getDBOperateLogInfo(String _sql, HashMap _dbTriggetTableMap) {
		String str_sql = _sql.toLowerCase().trim(); //
		String str_tablename = null; //ֱ�Ӵ���ı���
		String str_tabledesc = null; //
		String str_itemValue = null; //��ֵ.
		if (str_sql.startsWith("insert") && str_sql.indexOf(",") > 0) { //������ֵ!!
			str_sql = str_sql.substring(str_sql.indexOf("into") + 4, str_sql.length()).trim(); //��insert into���濪ʼ...
			str_tablename = str_sql.substring(0, str_sql.indexOf("(")).trim().toLowerCase(); //����
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
			return new String[] { _sql, str_tablename, str_tabledesc, "����", str_itemValue }; ////
		} else if (str_sql.startsWith("update")) {
			str_sql = str_sql.substring(str_sql.indexOf(" "), str_sql.length()).trim(); //��insert into���濪ʼ...
			str_tablename = str_sql.substring(0, str_sql.indexOf(" ")).toLowerCase(); //����
			str_tabledesc = (String) _dbTriggetTableMap.get(str_tablename); //
			if (str_sql.indexOf("where") > 0) {
				str_sql = str_sql.substring(str_sql.indexOf("where") + 5, str_sql.length()).trim(); //
				if (str_sql.startsWith("id='")) { //������������id
					str_itemValue = str_sql.substring(4, str_sql.length() - 1); //
				}
			}

			if (str_tablename != null) {
				str_tablename = str_tablename.trim(); //
			}

			if (str_itemValue != null) {
				str_itemValue = str_itemValue.trim(); //
			}
			//���ֻ���޸�Ƥ��,�򲻴���,����Ҫ���map����!��������ʱ���� ,�ʴ���Ŀ, xch/2012-09-13
			String[] str_filterSQL = new String[] { "update pub_user set desktopstyle", "update pub_user set lookandfeeltype" }; //������SQL�ǵ�¼ʱ����!��д��־!
			for (int i = 0; i < str_filterSQL.length; i++) { //
				if (_sql.toLowerCase().startsWith(str_filterSQL[i])) {
					//System.out.println("�ض���SQL��д��־!!"); //
					return null; //
				}

			}
			return new String[] { _sql, str_tablename, str_tabledesc, "�޸�", str_itemValue }; ////
		} else if (str_sql.startsWith("delete")) {
			str_sql = str_sql.substring(str_sql.indexOf(" "), str_sql.length()).trim(); //��insert into���濪ʼ...
			if (str_sql.startsWith("from")) {
				str_sql = str_sql.substring(str_sql.indexOf("from") + 4, str_sql.length()).trim(); //��insert into���濪ʼ...
			}
			if (str_sql.contains(" ")) {
				str_tablename = str_sql.substring(0, str_sql.indexOf(" ")).toLowerCase(); //����
				if (str_sql.indexOf("where") > 0) {
					str_sql = str_sql.substring(str_sql.indexOf("where") + 5, str_sql.length()).trim(); //
					if (str_sql.startsWith("id='")) { //������������id
						str_itemValue = str_sql.substring(4, str_sql.length() - 1); //
					}
				}
			} else {
				str_tablename = str_sql.toLowerCase(); //����
			}

			str_tabledesc = (String) _dbTriggetTableMap.get(str_tablename); //
			return new String[] { _sql, str_tablename, str_tabledesc, "ɾ��", str_itemValue }; ////
		}
		return null; //
	}

	/**
	 * һ����ִ��һ��SQL
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
		PreparedStatement p_stmt_log = null; //��־����!!
		PreparedStatement p_stmt_log_b = null; // ..

		// long ll_1 = System.currentTimeMillis();
		int[] li_dealRecords = null; //
		try {
			if (_datasourcename == null) {
				_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
			}
			ArrayList al_dbopelog_all = new ArrayList(); //��¼�������ݿ����־
			ArrayList al_dbopelog_log = new ArrayList(); //��¼�������ݿ����־
			HashMap triggerTablesMap = ServerEnvironment.getDBTriggerTableMap(); //������־,ƽ̨�������õĴ�������ʽ�����ı�
			boolean isPrepareSQL = "Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISPREPARESQL")); //�Ƿ�Ԥ����SQL,�����Y,��Ŵ���!!
			PrepareSQLUtil preSQLUtil = null; //
			boolean isRealPreSQL = (_isPrePareSQL || isPrepareSQL) && (_sqllist.size() < 100); //�Ƿ���������Ԥ����?���ǿ��ָ����,����ϵͳ����������!!ͬʱ����С��100��SQL,��Ϊ����100��SQL��Ҫ����100��statement,��һ��conn�ǲ�������300����!
			conn = getConn(_datasourcename); //�������ݿ�����!!!
			//System.out.println("is Auto Commit:" + conn.getConn().getAutoCommit() +",Trans Level:" +  conn.getConn().getTransactionIsolation());  //
			if (isRealPreSQL) { //���Ҫ����Ԥ����,�򴴽�������!
				preSQLUtil = new PrepareSQLUtil(); //
			} else {
				p_stmt = conn.createStatement(); //ֻ��һ���α�!!
			}
			//��������SQL!!!
			ArrayList al_result = new ArrayList(); //
			for (int i = 0; i < _sqllist.size(); i++) {
				Object objsql = _sqllist.get(i);
				if (objsql == null) { //���Ϊ��,������!!!��Ϊ����ǰ̨ƴ��SQLʱĳһ����Ϊnull
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
				addSQLToSessionSQLListener(sql); //��SQL�����������ӱ�SQL
				if (_isDebugLog) { //�Ƿ������־!!!��ʱ�ǲ��������־��!!!
					WLTLogger.getLogger(this).debug(sql); //
				}
				if (!isRealPreSQL) { //�������Ԥ�����,��ֱ��������!!
					p_stmt.addBatch(sql); // ������!!!
				} else { //�����Ԥ����!
					ArrayList al_presqls = preSQLUtil.prepareSQL(sql); //����SQL
					if (al_presqls == null || al_presqls.size() <= 1) { //���Ϊ��,�����ܽ���ʧ��!���߸���û�в���!!,��ֱ��ʹ�ñ�׼��stmt
						Statement itemStmt = conn.createStatement(); //����һ���α�!!
						int li_result = itemStmt.executeUpdate(sql); //ֱ��ִ��
						al_result.add(new Integer(li_result)); //
						itemStmt.close(); //
						//psList.add();  //����!!
					} else { //��������ɹ�!!
						String str_preSQL = (String) al_presqls.get(0); //��һ���� SQL
						PreparedStatement itemPstmt = conn.prepareStatement(str_preSQL); //
						for (int j = 1; j < al_presqls.size(); j++) { //
							itemPstmt.setObject(j, al_presqls.get(j)); //
						}
						int li_result = itemPstmt.executeUpdate(); //ֱ��ִ��!!!
						al_result.add(new Integer(li_result)); //
						itemPstmt.close(); //ֱ�ӹر�!!!
					}
				}
				if (_isDBLog && triggerTablesMap.size() > 0) { //�����Ҫ����־�����ִ�����Ҫ����־�ı����������־sql
					String[] str_opetab = getDBOperateLogInfo(sql, triggerTablesMap); //����SQL!!�ؼ��߼�!!!
					if (str_opetab != null) { //���ȡ����!!!
						str_opetab[0] = getTBUtil().subStrByGBLength(str_opetab[0], 3960); //����˱���ص�������־��sql���ܹ���������������ı������ء� [����2012-06-06]
						al_dbopelog_all.add(str_opetab); //String[] { _sql, str_tablename, str_tabledesc, "ɾ��", str_itemValue }; ////
						if (triggerTablesMap.containsKey(str_opetab[1])) { //�����������Ҫ��־����,��洢!
							al_dbopelog_log.add(str_opetab); //
						}
					}
				}
			}

			//���¾�����!!!
			VectorMap[] vmap = null;
			if (al_dbopelog_log.size() > 0) { //
				vmap = new VectorMap[al_dbopelog_log.size()]; //
				for (int i = 0; i < al_dbopelog_log.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_log.get(i); ////
					if ((str_itemOpeLog[3].equals("�޸�") || str_itemOpeLog[3].equals("ɾ��")) && str_itemOpeLog[4] != null) {
						String str_sql_temp = "select * from " + str_itemOpeLog[1] + " where id='" + str_itemOpeLog[4] + "'"; //
						HashVO[] hvos = getHashVoArrayByDS(_datasourcename, str_sql_temp); //
						if (hvos.length > 0) {
							vmap[i] = new VectorMap(); //
							String[] str_keys = hvos[0].getKeys(); //
							for (int j = 0; j < str_keys.length; j++) {
								vmap[i].put(str_keys[j].toLowerCase(), new String[] { hvos[0].getStringValue(str_keys[j]), null }); //�����ֵ!!!
							}
						}
					}
				}
			}
			//����ط����������ύ������һ���߼��ǲ鵽�޸�ǰ������
			if (!isRealPreSQL) { //�������Ԥ�����,����Ҫִ����������!!
				li_dealRecords = p_stmt.executeBatch(); //�����������ݿ�!!!
			} else {
				Integer[] li_rs = (Integer[]) al_result.toArray(new Integer[0]); //
				li_dealRecords = new int[li_rs.length]; //
				for (int i = 0; i < li_dealRecords.length; i++) {
					li_dealRecords[i] = li_rs[i].intValue();
				}
			}
			//������־��,�ٲ���������...��ִ���������ִ�У�
			if (al_dbopelog_log.size() > 0) {
				for (int i = 0; i < al_dbopelog_log.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_log.get(i); ////
					if ((str_itemOpeLog[3].equals("�޸�") || str_itemOpeLog[3].equals("����")) && str_itemOpeLog[4] != null) {
						String str_sql_temp = "select * from " + str_itemOpeLog[1] + " where id='" + str_itemOpeLog[4] + "'"; //
						//HashVO[] hvos = getHashVoArrayByDS(_datasourcename, str_sql_temp); //
						HashVO[] hvos = getHashVoStructByDS(_datasourcename, str_sql_temp, true, false, null, false, false, _isPrePareSQL).getHashVOs();
						if (hvos.length > 0) {
							if (vmap[i] == null) {
								vmap[i] = new VectorMap(); //���Ϊ��,�򴴽�֮!!!!
							}
							String[] str_keys = hvos[0].getKeys(); //
							for (int j = 0; j < str_keys.length; j++) {
								if (vmap[i].containsKey(str_keys[j].toLowerCase())) { //����Ѱ���!!
									String[] str_rowItemValue = (String[]) vmap[i].get(str_keys[j].toLowerCase()); //
									str_rowItemValue[1] = hvos[0].getStringValue(str_keys[j]); //��ֵ
									vmap[i].put(str_keys[j].toLowerCase(), str_rowItemValue); //��������!!!
								} else {
									vmap[i].put(str_keys[j], new String[] { null, hvos[0].getStringValue(str_keys[j]) }); //ֱ��������ֵ,��û�о�ֵ,��Insert��������޾�ֵ��!!!!!
								}
							}
						}
					}
				}
			}

			//����־���뵽������־����!!!!����о�ֵ����ֵ�ıȽ�,��Ҫ�����ӱ�!!!
			if (al_dbopelog_log.size() > 0) {
				p_stmt_log = conn.prepareStatement("insert into PUB_DBTRIGGERLOG(id,tabname,tabdesc,opetype,opeuser,deptid,deptname,opetime,opesql,clientip) values (?,?,?,?,?,?,?,?,?,?)"); //����
				p_stmt_log_b = conn.prepareStatement("insert into PUB_DBTRIGGERLOG_B(id,triggerlog_id,tabname,opetype,itemname,olditemvalue,newitemvalue,itemdesc) values (?,?,?,?,?,?,?,?)"); //�ӱ�

				String str_currtime = new TBUtil().getCurrTime(); //��ǰʱ��..
				WLTInitContext initContext = new WLTInitContext(); //
				String str_loginUserCode = null;
				String str_loginUserName = null; //
				String str_loginUserDeptid = null; //
				String str_loginUserDeptname = null; //
				String str_clientip = null;
				CurrSessionVO currsession = initContext.getCurrSession();
				str_loginUserCode = currsession.getLoginUserCode(); //��¼��Ա����!!
				str_loginUserName = currsession.getLoginUserName(); //��¼��Ա����!!
				str_clientip = currsession.getClientIP1() + "/" + currsession.getClientIP2(); //
				String str_sql_temp = "select a.id,a.name from pub_corp_dept a,pub_user_post b where a.id = b.userdept and isdefault='Y' and userid =" + currsession.getLoginUserId(); //pub_user�е�pkdept��׼��Ӧ��pub_user_post
				//HashVO[] hvos = getHashVoArrayByDS(_datasourcename, str_sql_temp); //
				HashVO[] hvos = getHashVoStructByDS(_datasourcename, str_sql_temp, true, false, null, false, false, _isPrePareSQL).getHashVOs();
				if (hvos != null && hvos.length > 0) {
					str_loginUserDeptid = hvos[0].getStringValue("id");
					str_loginUserDeptname = hvos[0].getStringValue("name");
				}
				String str_itemsql = null; //
				String showuser = new TBUtil().getSysOptionStringValue("�Ƿ���ʾ��½��", "0");//�Ƿ���ʾ��½��,Ĭ��0Ϊ��¼��+������1Ϊֻ��ʾ��Ա����
				//��������SQL!!!
				for (int i = 0; i < al_dbopelog_log.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_log.get(i); ////
					String str_parentrecordid = getSequenceNextValByDS(null, "S_PUB_DBTRIGGERLOG"); //���������!!!
					p_stmt_log.setString(1, str_parentrecordid);
					p_stmt_log.setString(2, str_itemOpeLog[1]);
					p_stmt_log.setString(3, str_itemOpeLog[2]);
					p_stmt_log.setString(4, str_itemOpeLog[3]);
					if ("1".equals(showuser)) { //������Ŀ����ʾ�û�������Ϊ�û����õ������֤�ţ�
						p_stmt_log.setString(5, str_loginUserName);
					} else {
						p_stmt_log.setString(5, str_loginUserCode + "/" + str_loginUserName);
					}
					p_stmt_log.setString(6, str_loginUserDeptid);
					p_stmt_log.setString(7, str_loginUserDeptname);
					p_stmt_log.setString(8, str_currtime);
					p_stmt_log.setString(9, str_itemOpeLog[0]);
					p_stmt_log.setString(10, str_clientip);
					p_stmt_log.addBatch(); //��������!!!
					if (_isDebugLog) { //�Ƿ������־!!!��ʱ�ǲ��������־��!!!
						WLTLogger.getLogger(this).debug("insert into PUB_DBTRIGGERLOG(id,tabname,tabdesc,opetype,opeuser,deptid,deptname,opetime,opesql,clientip) values (?,?,?,?,?,?,?,?,?,?)"); //
					}
					HashMap tabledescr = ServerEnvironment.getDBTriggerTableDescrMap();
					HashMap coldescr = new HashMap();
					if (tabledescr.containsKey(str_itemOpeLog[1].toUpperCase())) {
						coldescr = (HashMap) tabledescr.get(str_itemOpeLog[1].toUpperCase());
					}
					if (vmap[i] != null) {
						String[] str_allkeys = vmap[i].getKeysAsString(); //���е���!
						for (int j = 0; j < str_allkeys.length; j++) { //�����е�����!!!
							String[] str_old_new_itemvalue = (String[]) vmap[i].get(str_allkeys[j]); ////
							String str_childrecordid = getSequenceNextValByDS(null, "S_PUB_DBTRIGGERLOG_B", 50); //���������!!!
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
							p_stmt_log_b.setString(8, descr); //�����ֶ�����
							if (_isDebugLog) { //�Ƿ������־!!!��ʱ�ǲ��������־��!!!
								WLTLogger.getLogger(this).debug("insert into PUB_DBTRIGGERLOG_B(id,triggerlog_id,tabname,opetype,itemname,olditemvalue,newitemvalue,itemdesc) values (?,?,?,?,?,?,?,?)"); //
							}
							p_stmt_log_b.addBatch(); //����
						}
					}
				}
				p_stmt_log.executeBatch(); //
				p_stmt_log_b.executeBatch(); //
			}
			//��ղ�ѯ��ҳʱ����Count�Ļ���!!�������޸���ĳ����,������ˢ�¸ñ��ڲ�ѯ��ҳ��ע���SQL����!
			if (al_dbopelog_all.size() > 0) { //��all
				HashSet hstTableName = new HashSet(); //
				for (int i = 0; i < al_dbopelog_all.size(); i++) {
					String[] str_itemOpeLog = (String[]) al_dbopelog_all.get(i); ////
					hstTableName.add(str_itemOpeLog[1].toLowerCase()); //һ��ҪתСд!
				}
				String[] str_tabNames = (String[]) hstTableName.toArray(new String[0]); //���б���!
				String[] str_cacheTableNames = (String[]) ServerEnvironment.countSQLCache.keySet().toArray(new String[0]); //�õ����е�key
				for (int i = 0; i < str_tabNames.length; i++) { ////
					for (int j = 0; j < str_cacheTableNames.length; j++) { ////
						if (str_cacheTableNames[j].equals(str_tabNames[i]) || str_cacheTableNames[j].startsWith("v_" + str_tabNames[i])) { //��������е�����������ڻ�����v_��������ͷ,�������������!
							ServerEnvironment.countSQLCache.remove(str_cacheTableNames[j]); ///
							ServerEnvironment.pagePKValue.remove(str_cacheTableNames[j]); //
							//System.out.println("��ձ�[" + str_cacheTableNames[j] + "]�Ļ���"); //
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
			// System.out.println("ִ������SQL���ʧ��,SQL����:\n"); //
			ex.printStackTrace(); //
			if (ex.getNextException() != null) {
				System.err.println("��һ���쳣:"); //
				ex.getNextException().printStackTrace(); //ʵ��ԭ��!!!
			}
			conn.getConn().rollback();
			throw new SQLException("������Դ[" + _datasourcename + "]��ִ������SQLʧ��:\n" + str_message + ",�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			conn.getConn().rollback();
			String str_message = "";
			for (int i = 0; i < _sqllist.size(); i++) {
				str_message = str_message + _sqllist.get(i) + "\n"; //
			}
			// System.out.println("ִ������SQL���ʧ��,SQL����:\n"); //
			// System.out.println("ʧ��ԭ��:");
			ex.printStackTrace(); //
			throw new Exception("������Դ[" + _datasourcename + "]��ִ������SQL[" + str_message + "]ʧ��,ԭ��:[" + ex.getMessage() + "]");
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

	//����ִ��SQL,���������־!!
	public void executeBatchByDSNoLog(String _datasourcename, java.util.List _sqllist) throws Exception {
		executeBatchByDS(_datasourcename, _sqllist, false); //
	}

	/**
	 * �����ύһ��SQL
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
	 * �����¿�����ִ��һ��SQL,�����ں�̨�����߼�..����һ��SQL֮�䱾����һ������.. �÷�������¶���ͻ���,��Ϊ�ͻ���û��Ҫ���������
	 * 
	 * @param _datasourcename
	 * @param _sqllist
	 * @throws Exception
	 */
	public void executeBatchByDSImmediately(String _datasourcename, String[] _sqls, boolean _isDebugLog) throws Exception {
		if (_datasourcename == null) {
			_datasourcename = ServerEnvironment.getInstance().getDefaultDataSourceName();
		}
		java.util.List list = Arrays.asList(_sqls); // ת����һ��List
		executeBatchByDSImmediately(_datasourcename, list, _isDebugLog); //
	}

	public void executeBatchByDSImmediately(String _datasourcename, java.util.List _sqllist) throws Exception {
		executeBatchByDSImmediately(_datasourcename, _sqllist, true); ////
	}

	/**
	 * �����¿�����ִ��һ��SQL,�����ں�̨�����߼�..����һ��SQL֮�䱾����һ������.. �÷�������¶���ͻ���,��Ϊ�ͻ���û��Ҫ���������
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
				conn = DriverManager.getConnection(str_dburl); // //������������,��ʵҲ�Ǵ�dbcp����ȡ��!!!
			}
			conn.setTransactionIsolation(java.sql.Connection.TRANSACTION_READ_COMMITTED); // ����������뼶��!!��������!!
			conn.setAutoCommit(false); // �����ﶨ����!!

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
				addSQLToSessionSQLListener(sql); //��SQL�����������ӱ�SQL
				if (_isDebugLog) {
					WLTLogger.getLogger(this).debug(sql); //
				}
				p_stmt.addBatch(sql); // ������!!!
				// System.out.println("ExcuteTran:" + sql);
			}
			p_stmt.executeBatch();
			conn.commit(); // �����ύ
		} catch (Exception e) {
			try {
				conn.rollback(); // �����ع�
			} catch (SQLException e1) {
			}
			String str_message = "";
			for (int i = 0; i < _sqllist.size(); i++) {
				str_message = str_message + _sqllist.get(i) + "\n"; //
			}
			throw new Exception("������Դ[" + _datasourcename + "]��ִ������SQL[" + str_message + "]ʧ��,ԭ��:[" + e.getMessage() + "]"); //�����쳣
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
	 * �÷��������׸��ͻ���!
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
	 * �÷������׸��ͻ���,�ӿͻ�����Զ��һȡһ��!!
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
	 * �÷������׸��ͻ���,�ӿͻ�����Զ��һȡһ��!!
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
	 * ������һ�ű����һ������!
	 * @param _table
	 * @param _pk
	 * @param _fieldArraysMap,key���ֶ���,value��String[]��String,���String,���Էֺ����!,����
	 * dataMap.put("Question_Crop_Id", new String[] { "2001", "1999", "1756", "1759", "1742", "1746", "1747" }); //
	 * dataMap.put("Question_Type", "1;2;3;4;5"); //
	 * @param _records
	 */
	public void insertDemoData(String _table, String _pk, HashMap _fieldArraysMap, int _records) throws Exception {
		String[] str_keys = (String[]) _fieldArraysMap.keySet().toArray(new String[0]); //
		java.util.Random rans = new java.util.Random(); //�����������!
		TBUtil tbUtil = new TBUtil(); //
		ArrayList al_sqls = new ArrayList(); //
		for (int i = 0; i < _records; i++) { //�������м�¼!
			InsertSQLBuilder isql = new InsertSQLBuilder(_table); //
			if (_pk != null) {
				isql.putFieldValue(_pk, getSequenceNextValByDS(null, "S_" + _table)); //����ֵ
			}
			for (int j = 0; j < str_keys.length; j++) {
				//String str_fieldName = str_keys[j]; //�ֶ���
				Object obj = _fieldArraysMap.get(str_keys[j]); //boolean b = object.getClass().isArray();
				if (obj != null) {
					String[] str_items = null; //
					if (obj.getClass().isArray()) { //��������Ǹ�����!
						str_items = (String[]) obj; //		
					} else { //�����������
						str_items = tbUtil.split(((String) obj), ";"); //�ָ�
					}
					String str_value = str_items[rans.nextInt(str_items.length)]; //���ֵ! 
					isql.putFieldValue(str_keys[j], str_value); //��������
				}
			}
			al_sqls.add(isql); //
		}
		executeBatchByDS(null, al_sqls); //
	}

	/**
	 * ���ô洢���̲�����ֵ �������ڣ�(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                �쳣˵����
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
		addSQLToSessionSQLListener(str_pars); //��SQL�����������ӱ�SQL
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
					proc.setString(i + 1, parmeters[i]); // �������
				}

				if (_outputindex > 0) {
					proc.registerOutParameter(_outputindex, java.sql.Types.VARCHAR); // ע�᷵�س�����,��һ�������α�����
				}
			}

			proc.executeUpdate();
			if (_outputindex > 0) {
				str_return = String.valueOf(proc.getObject(_outputindex)); // ����г���,�򷵻�֮
			}

			long ll_2 = System.currentTimeMillis();
			return str_return; //
		} catch (SQLException ex) {
			// ex.printStackTrace();
			throw new SQLException("������Դ[" + _datasourcename + "]�ϵ��ô洢����[" + str_pars + "]ʧ��,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new Exception("������Դ[" + _datasourcename + "]�ϵ��ô洢����[" + str_pars + "]ʧ��,ʧ��ԭ��:" + ex.getMessage());
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
	 * ���ô洢���̲�����ֵ �������ڣ�(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                �쳣˵����
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

		addSQLToSessionSQLListener(str_pars); //��SQL�����������ӱ�SQL
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
			throw new SQLException("������Դ[" + _datasourcename + "]�ϵ��ô洢����[" + str_pars + "]ʧ��,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			// ex.printStackTrace();
			throw new Exception("������Դ[" + _datasourcename + "]�ϵ��ô洢����[" + str_pars + "]ʧ��,ʧ��ԭ��:" + ex.getMessage());
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
	 * ���ô洢���̷���ֵ �������ڣ�(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                �쳣˵����
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
		addSQLToSessionSQLListener(str_pars); //��SQL�����������ӱ�SQL
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
				} // �������
			}
			proc.registerOutParameter(parmeters.length, java.sql.Types.VARCHAR); // ���ó���
			proc.execute();
			String ls_return = String.valueOf(proc.getObject(parmeters.length)); // �õ�����
			long ll_2 = System.currentTimeMillis();
			return ls_return; // ���س���
		} catch (SQLException ex) { //
			// ex.printStackTrace();
			throw new SQLException("������Դ[" + _datasourcename + "]�ϵ��ô洢����[" + str_pars + "]ʧ��,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage()); //
		} catch (Exception ex) { //
			// ex.printStackTrace();
			throw new Exception("������Դ[" + _datasourcename + "]�ϵ��ô洢����[" + str_pars + "]ʧ��,ʧ��ԭ��:" + ex.getMessage()); //
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
	 * ���ú��������ַ��� �������ڣ�(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                �쳣˵����
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
		addSQLToSessionSQLListener(str_pars); //��SQL�����������ӱ�SQL
		WLTLogger.getLogger(this).debug(str_pars); //

		try {
			conn = getConn(_datasourcename); //
			String strTemp = "{ ? = call " + functionName + "(";
			if (parmeters != null) // �����β�Ϊ��,��ƴ�ɲ�����
			{
				for (int i = 0; i < parmeters.length; i++) {
					strTemp = strTemp + "?,";
				}
				strTemp = strTemp.substring(0, strTemp.length() - 1);
			}
			strTemp = strTemp + ")}";
			proc = conn.prepareCall(strTemp);
			proc.registerOutParameter(1, java.sql.Types.VARCHAR); // ע�᷵�س�����,��һ�������α�����
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length; i++) {
					proc.setString(i + 2, parmeters[i]);
				}
			}
			proc.execute();
			String str_return = String.valueOf(proc.getObject(1));
			return str_return;
		} catch (SQLException ex) {
			throw new SQLException("������Դ[" + _datasourcename + "]�ϵ��ú���[" + str_pars + "]ʧ��,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("������Դ[" + _datasourcename + "]�ϵ��ú���[" + str_pars + "]ʧ��,ʧ��ԭ��:" + ex.getMessage());
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
	 * ���ú������ر� �������ڣ�(2005-1-13 11:25:26)
	 * 
	 * @return java.lang.String[]
	 * @param dnsName
	 *            java.lang.String
	 * @param procudereName
	 *            java.lang.String
	 * @param parmeter
	 *            java.lang.String[]
	 * @exception java.rmi.RemoteException
	 *                �쳣˵����
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
		addSQLToSessionSQLListener(str_pars); //��SQL�����������ӱ�SQL
		WLTLogger.getLogger(this).debug(str_pars); //
		try {
			conn = getConn(_datasourcename); //
			String strTemp = "{ ? = call " + functionName + "(";
			if (parmeters != null) // �����β�Ϊ��,��ƴ�ɲ�����
			{
				for (int i = 0; i < parmeters.length; i++) {
					strTemp += "?,";
				}
				strTemp = strTemp.substring(0, strTemp.length() - 1);
			}
			strTemp += ")}";

			proc = conn.prepareCall(strTemp);

			String[][] str_data = null; // ��������

			// �����ORACLE,��ʹ���α�����
			proc.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR); // ע�᷵�س�����,��һ�������α�����
			if (parmeters != null) {
				for (int i = 0; i < parmeters.length; i++) {
					proc.setString(i + 2, parmeters[i]);
				}
			}
			proc.execute();
			rs = (ResultSet) proc.getObject(1);
			if (rs != null) // ����������Ϊ��
			{
				Vector aVector = new Vector();
				int nColCnt = rs.getMetaData().getColumnCount();
				while (rs.next()) // ���������
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
			throw new SQLException("������Դ[" + _datasourcename + "]�ϵ��ú���[" + str_pars + "]ʧ��,�������[" + ex.getErrorCode() + "],״̬[" + ex.getSQLState() + "],ԭ��:" + ex.getMessage());
		} catch (Exception ex) {
			throw new Exception("������Դ[" + _datasourcename + "]�ϵ��ú���[" + str_pars + "]ʧ��,ʧ��ԭ��:" + ex.getMessage());
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

	//��һ������ȡ��ĳһ�������ϲ����ĳ���ֶε�ֵ
	public String getTreePathColValue(String _tableName, String _returnItemName, String _linkedIdField, String _parentLinkedField, String _whereField, String _whereCondition) throws Exception {
		BSUtil bsUtil = new BSUtil(); //
		if (_tableName.equalsIgnoreCase("pub_corp_dept")) { //����ǻ���,�����⴦��,�ӻ���ȡ!!
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
	 * ִ��һ�β�ѯ�������ٺ����;��������!!!
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
	 * ��SQL�����������Ӽ���
	 * @param _sql
	 */
	private void addSQLToSessionSQLListener(String _sql) {
		if (getCurrSession() != null) {
			if (ServerEnvironment.getSessionSqlListenerMap().containsKey(getCurrSession().getHttpsessionid())) { //�����ע����Ҫ����
				Queue sqlQueue = (Queue) ServerEnvironment.getSessionSqlListenerMap().get(getCurrSession().getHttpsessionid()); //
				sqlQueue.add(_sql); //��ƨ�ɺ����..
			}
		}
	}

	/**
	 * �����޸ĺۼ�.
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