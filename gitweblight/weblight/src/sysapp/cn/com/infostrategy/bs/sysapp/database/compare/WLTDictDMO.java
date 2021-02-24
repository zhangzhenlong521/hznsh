package cn.com.infostrategy.bs.sysapp.database.compare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.TableDataStruct;

/**
 * �����ֵ��DMO,���������������ֵ�
 * ֮��������������Ϊ�˽����ƽ̨������,��
 * @author xch
 *
 */
public class WLTDictDMO extends AbstractDMO {

	//ȡ�����ж����
	public WLTTableDf[] getAllPlatformTablesDefines() {
		return new WLTDictFactory().getAllPlatformTablesDefine(); //ȡ��ƽ̨������������б�
	}

	/**
	 * �������ж����б���,Ҫ�ṩ���ͻ���
	 * @return
	 */
	public String[][] getAllTableDefineNames() {
		WLTTableDf[] allTdfs = getAllPlatformTablesDefines(); //ȡ�����нṹ����
		String[][] str_tabNames = new String[allTdfs.length][2];
		for (int i = 0; i < allTdfs.length; i++) {
			str_tabNames[i][0] = allTdfs[i].getTableName(); //����
			str_tabNames[i][1] = allTdfs[i].getTableDesc(); //˵��
		}
		return str_tabNames;
	}

	/**
	 * ��������ģ����ԃ
	 * @return
	 */
	public String[][] getAllTableDefineNames(String tableName) {
		WLTTableDf[] allTdfs = getAllPlatformTablesDefines(); //ȡ�����нṹ����
		String[][] str_tabNames = new String[allTdfs.length][2];
		String[][] str_tabNames2 = null;
		List ss = new ArrayList();
		if (allTdfs != null && allTdfs.length > 0) {
			int j = 0;
			for (int i = 0; i < allTdfs.length; i++) {
				if (allTdfs[i].getTableName().indexOf(tableName) != -1) {
					str_tabNames[j][0] = allTdfs[i].getTableName(); //����
					str_tabNames[j][1] = allTdfs[i].getTableDesc(); //˵��
					ss.add(str_tabNames[j]);
					j++;
				}
			}
			/*str_tabNames2 = new String[j][2];
			for (int i = 0; i < j; i++) {
				str_tabNames2[i][0] = str_tabNames[i][0]; //����
				str_tabNames2[i][1] = str_tabNames[i][1]; //˵��
			}*/
			if (ss != null && ss.size() > 0) {
				str_tabNames2 = (String[][]) ss.toArray(new String[0][0]);
			}
		}

		return str_tabNames2;
	}

	/**
	 * �õ�����ֻ�����еı�
	 * @return
	 */
	public String[][] getAllTableOnlyDFhave() {
		String[][] s = getAllTableDefineNames();
		List ss = new ArrayList();
		String[][] sss = null;
		if (s != null && s.length > 0) {
			for (int i = 0; i < s.length; i++) {
				try {
					if ("��������,������Դ��û�У�".equals(getCompareSQLByTabName(s[i][0]))) {
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
	 * �õ�����ֻ����Դ�еı�
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
					if ("������û��,������Դ���У�".equals(getCompareSQLByTabName(s[i]))) {
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
	 * �õ�����ƽ̨����������Դ���еı�
	 * @return
	 */
	public String[][] getAllTableBHhave() {
		String[][] s = getAllTableDefineNames();
		List ss = new ArrayList();
		String[][] sss = null;
		if (s != null && s.length > 0) {
			for (int i = 0; i < s.length; i++) {
				try {
					if (!"��������,������Դ��û�У�".equals(getCompareSQLByTabName(s[i][0])) && !"������û��,������Դ���У�".equals(getCompareSQLByTabName(s[i][0]))) {
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
	 * ���ݱ������������е���Ϣ,Ҫ�ṩ���ͻ���
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
	 * ��������ȡ��ĳһ����Ķ���..
	 * @param _tabName
	 * @return
	 */
	private WLTTableDf getTableDefByName(String _tabName) {
		WLTTableDf[] allTdfs = getAllPlatformTablesDefines(); //ȡ�����нṹ����
		for (int i = 0; i < allTdfs.length; i++) {
			if (allTdfs[i].getTableName().equalsIgnoreCase(_tabName)) {
				return allTdfs[i]; //
			}
		}
		return null; //
	}

	/**
	 * ���ݱ���,����Ĭ������Դ�����ݿ����͵�Create�ű�! �÷���Ҫ�ṩ��Զ�̵Ŀͻ���
	 */
	public String getCreateSQLByTabDefineName(String _tabName) {
		return getCreateSQLByTabDefineName(ServerEnvironment.getDefaultDataSourceType(), _tabName);
	}

	/**
	 * ���ݱ���,�ֶ���,����Ĭ������Դ�����ݿ����͵�alter�ű�! �÷���Ҫ�ṩ��Զ�̵Ŀͻ���
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
	 * �õ�����alter�ű�! �÷���Ҫ�ṩ��Զ�̵Ŀͻ���
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
	 * ���ݱ��������ָ�����ݿ����͵�Create�ű�!
	 * @param _dbType
	 * @param _tabName
	 * @return
	 */
	public String getCreateSQLByTabDefineName(String _dbType, String _tabName) {
		WLTTableDf tabDef = getTableDefByName(_tabName); //
		if (tabDef != null) {
			return getCreateSQL(_dbType, tabDef); //ȡ�ô������SQL,��Create Table�ű�!!
		} else {
			return "/**û���ҵ���[" + _tabName + "]�Ķ���*/";
		}
	}

	/**
	 * �︻������ȫ!
	 * ���ݶ���Ķ��������ݿ�����,����������SQL���,��create table...,��ͬ�����ݿ�����������в�ͬ!!
	 * @param _dbType ���ݿ�����,����Oracle,Mysql,SQLServer��
	 * @param _tdf ����Ķ���
	 * @return
	 */
	private String getCreateSQL(String _dbType, WLTTableDf _tdf) {
		HashMap typeMap = getTypeMap(_dbType);

		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("create table " + _tdf.getTableName() + " (\r\n");
		String[][] str_cols = _tdf.getColDefines(); //������,5��!!
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
	 * ȡ�õ�ǰĬ�������б��붨��ıȽ�
	 * @param _dataSourceName
	 * @param _tabName
	 * @return
	 * @throws Exception
	 */
	public String getCompareSQLByTabName(String _tabName) throws Exception {
		return getCompareSQLByTabName(ServerEnvironment.getDefaultDataSourceName(), _tabName); //
	}

	/**
	 * �︻��ʵ��!!!
	 * ��ĳһ������бȽ�!! ��������ı���ʵ�����ݿ��еı���бȽ�
	 * �ȽϵĽ�����������:
	 * 1.��������,������Դ��û��
	 * 2.������û��,������Դ����
	 * 3.���߶���,���������һ��,�������һ������������������������Ĳ���һ��!!
	 * @param _dataSourceName ����Դ����
	 * @param _tabName ����
	 * @return
	 */
	private String getCompareSQLByTabName(String _dataSourceName, String _tabName) throws Exception {
		String str_dbtpye = ServerEnvironment.getInstance().getDataSourceVO(_dataSourceName).getDbtype(); //ȡ�ø�����Դ������!!����Oracle,Mysql,SQLServer
		String returnString = "";
		TableDataStruct dsTabTD = null;
		//��ȡ������Դ�иñ��ʵ�ʶ���
		try {
			dsTabTD = new CommDMO().getTableDataStructByDS(_dataSourceName, "select * from " + _tabName + " where 1=2");
		} catch (Exception e) {
			//e.printStackTrace();
			dsTabTD = null;
		}
		//��ȡ�������еı�ṹԭ���ݶ���
		WLTTableDf tabdef = getTableDefByName(_tabName);

		//����͸������ݿ�����,ʵ�ʱ���,�����ֵ�Ķ��壬������ֵ���бȽ�!!���ɷ��ؽ��!!
		if (dsTabTD == null && tabdef != null) {
			returnString = "��������,������Դ��û�У�";
		}
		if (tabdef == null && dsTabTD != null) {
			returnString = "������û��,������Դ���У�";
		}
		if (tabdef == null && dsTabTD == null) {
			returnString = "������û��,����Դ��Ҳû�У�";
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
							dsTabTDHave.append(dsTabTD.getHeaderName()[i].toLowerCase() + "��");
						if (i == dsTabTDClength - 1)
							dsTabTDHave.append(dsTabTD.getHeaderName()[i].toLowerCase());
					}
				}
				if (i < tabdefClength) {
					if (!dsTabTDHeaderNameMap.containsKey(tabdef.getColDefines()[i][0].toLowerCase())) {
						if (i < tabdefClength - 1)
							tabdefHave.append(tabdef.getColDefines()[i][0].toLowerCase() + "��");
						if (i == tabdefClength - 1)
							tabdefHave.append(tabdef.getColDefines()[i][0].toLowerCase());
					}
				}
			}
			if (tabdefHave != null && tabdefHave.length() > 0) {
				returnString = returnString + "�����ж�����Դû�е��У�{" + tabdefHave.toString() + "}\r\n";
			}
			if (dsTabTDHave != null && dsTabTDHave.length() > 0) {
				returnString = returnString + "����Դ�ж�����û�е��У�{" + dsTabTDHave.toString() + "}\r\n";
			}
		}

		if ("".equals(returnString.trim())) {
			returnString = "��������������ƥ�䣡";
		}
		return returnString;
	}

	public List getCompareLISTByTabName(String _dataSourceName, String _tabName) throws Exception {
		String str_dbtpye = ServerEnvironment.getInstance().getDataSourceVO(_dataSourceName).getDbtype(); //ȡ�ø�����Դ������!!����Oracle,Mysql,SQLServer
		List returnList = new ArrayList();
		List dbHave = new ArrayList();
		List dfHave = new ArrayList();
		List bothHaveDB = new ArrayList();
		TableDataStruct dsTabTD = null;
		//��ȡ������Դ�иñ��ʵ�ʶ���
		try {
			dsTabTD = new CommDMO().getTableDataStructByDS(_dataSourceName, "select * from " + _tabName + " where 1=2");
		} catch (Exception e) {
			//e.printStackTrace();
			dsTabTD = null;
		}
		//��ȡ�������еı�ṹԭ���ݶ���
		WLTTableDf tabdef = getTableDefByName(_tabName);

		//����͸������ݿ�����,ʵ�ʱ���,�����ֵ�Ķ��壬������ֵ���бȽ�!!���ɷ��ؽ��!!
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

	//��������Java����
	public String reverseCreateJavaCode(String _tableName) {
		return reverseCreateJavaCode(ServerEnvironment.getDefaultDataSourceName(), _tableName); //
	}

	/**
	 * ��긺��
	 * ��������Դ�е�ʵ�ʱ�����������Java����!!
	 * Ϊ�����Ч�ʷ��򹤳�!!
	 * @param _tableName
	 * @return
	 * @throws Exception 
	 */
	public String reverseCreateJavaCode(String _dsName, String _tableName) {

		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("//��[" + _tableName + "]����\r\n"); //
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
		String[] isNullAble = dsTabTD.getIsNullAble(); //�ֶ��Ƿ�Ϊ��

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
