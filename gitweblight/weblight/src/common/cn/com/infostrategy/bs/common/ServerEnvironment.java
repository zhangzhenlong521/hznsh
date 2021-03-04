/**************************************************************************
 * $RCSfile: ServerEnvironment.java,v $  $Revision: 1.35 $  $Date: 2012/11/27 08:44:30 $
 **************************************************************************/
package cn.com.infostrategy.bs.common;

import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import cn.com.infostrategy.bs.mdata.MetaDataDMO;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.to.common.DESKeyTool;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.InitParamVO;
import cn.com.infostrategy.to.common.Log4jConfigVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.VectorMap;

/**
 * �������˻���
 * @author user
 *
 */
public class ServerEnvironment implements Serializable {

	private static final long serialVersionUID = -3231827644600597388L;

	private static ServerEnvironment serverEnv = null; //

	public static int EVER_MAX_ONLINEUSERS = 0; //�������������û���
	public static long EVER_MAX_TOTALMEMORY = 0; //���������ڴ���

	public static boolean isPrintExceptionStack = false; //�Ƿ��ӡ�쳣��ջ,ֻ����ʹ��TBUtil.printStackTrace()ʱ��Ч��,��Ϊ��ʱ��һЩѭ������һЩ�߼�ʱ,Ϊ�˷�ֹƵ����ӡ�쳣��ջ�������ܼ���(������ع�ʽ��),����Ĭ���ǲ����ջ��!

	private String configFileName = null;
	private VectorMap map = null;
	private HashMap descmap = null;
	private HashMap custMap = null; //�û��Զ�����Ҫ�Ļ���!
	private String str_defaultdatasource = null;

	public static Vector vc_alltables = null; //
	private Log4jConfigVO log4jConfigVO = null;
	private InitParamVO[] initParamVOs = null;
	private DataSourceVO[] dataSourceVOs = null;
	private static String[] ext3Jars = null; //ext3���ļ��嵥..
	private static String[] bin3Dlls = null; //ext3���ļ��嵥..
	private static String[] ext3JarClsNames = null; //ext3���ļ��嵥..

	private String[] imagesNames = null; //����ͼƬ���б�!!

	private static HashMap loginUserMap = new HashMap(); //��¼��¼��Ա�Ĺ�ϣ��
	private static HashMap serverProperties = new HashMap(); //������������....
	private static String[][] loginHref = null; //
	private static String[][] innerSys = null; //

	private static HashMap sessionSqlListenerMap = new HashMap(); //
	private static HashMap dbTriggerTableMap = null; //ע�����Ҫ������ɾ�Ĵ��������ı����嵥!!!
	private static HashMap dbTriggerTableDescrMap = null; //�Ĵ��������ı����嵥�ı��������.��table.xml�ж�ȡ[2012-11-27����]

	public static boolean isLoadRunderCall = false; //�Ƿ���LoadRunDer�ڵ���!!,�����Ǻ������˸��̵߳��ù��̼�صĶ���,�����������ط�����һЩ��ش����!��������������ֳ�����ѹ������ʱ��Щ����������ѹ������ʱӰ������! ���Ա����и�������һ���ӹص�������Щ����!! �Ӷ���֤ѹ������ʱ�ܹ�!
	public static boolean isPaginationInCache = false; //��ҳʱ�Ƿ�������,һ������������,һ����count(*)����,һ������ҳ��������!!��Ϊ��������Ŀ��ѹ������ʱ������,����һ��ʹ�û��漼��,�������ͻ����Ͽ�!���Լ����о�������SessionFactory�е�ͬ����ɵ�! ����Ĭ�ϸĳ���False! ��Ĭ���ǲ��������! 
	//�Ժ���Կ���ֱ��ȥ��!�����������滹��Ч�ʷǳ��ߵ�(�ܴﵽhit500/ÿ��)! ����˵�Ժ�Ӧ��ģ�嶨���з��������,������õ��������ִ�,���׳���������Ĺ��ܵ�������! ����������ѯ! ��ѡ����ʹ�øü���!! 
	//�Ժ����е����ֲ�����������һ�ֻ���,��ϵͳ�и��ܿ���,Ȼ��ģ�����и��ֿ���! �ܿ���:��Y/N(ǿY/ǿN),�ֿ���������ֵ: Y/N/ʹ���ܿ�������,���ֿ������Լ���Ȩ��!�ܿ�����ǿ��ǿ��Ȩ��! ���ܿ�����4��ֵ!�ֿ�����3��ֵ!!

	public static boolean isOutPutToQueue = true; //�Ƿ񽫿���̨��Ϣ��������������?Ĭ���������,���ʴ���Ŀ�о�Ȼ������Ϊ������������ڴ����???

	public static boolean isPageFalsity = false; //�Ƿ��ҳ���,����ʱѹ������ʱ,������ʹ�û���!����ÿ�ζ���ѯ���ݿ�!��hit80�����ﲻ��!! ���Ըɴ����!
	public static int realBusiCallCountOneSecond = 15; //ÿ�볬�����ٸ���Ϊ����ĺܿ�!!
	public static int falsitySleep = 300; //��Ϣ300����!
	public static int falsityThreadLimit = 300; //�����߳�����!

	public static int pageFalsityCycleCount = 5; //ÿ6�θ�һ�����⻺��!
	public static int newThreadCallCount = 0; //

	public static Hashtable pageFalsityMapCount = new Hashtable(); //��ҳ��ٵĹ�ϣ��! key�Ǳ���,Value��һҳ������! ���뷵��ʵ��һҳ����,����LR����ʱ�ᷢ��û��������!! ���������һ����һ����¼������һ��SQLһ��,��Ϊ������ڴ����!!
	public static HashMap pageFalsityMapData = new HashMap();
	public static long ll_falsity_1 = 0; //
	public static long ll_falsity_2 = 0; //

	public static boolean isPageCountInCache = false; //��ҳ�����SQL�Ƿ�������??
	public static int pacgeCountCacheCycle = -1; //

	private static Queue serverConsoleQueue = new Queue(500); //�������˿���̨����,����¼�������˿��ƹ��������!!,����2000��!����Ϊ����̫������JVM�ڴ�̫��,���Ը��500����,�Ͼ���������õ���!��xch/2012-08-22��

	public static Hashtable pagePKValue = new Hashtable(); //��ҳ�����������Ļ���!��Ϊ�˸��ߵ��������,����һҳ�����ݵ�id���л���!!! �����´��ٲ�ʱ���Ķ�!!
	public static Hashtable countSQLCache = new Hashtable(); //һ��Ҫ��Hashtable������HashMap,��Ϊ������ڶ��̷߳���! ��ΪHashtable���̰߳�ȫ��,��HashMap����! �м�!!

	public static long ll_firstPageFromPkCache = 0; //��ҳ��¼ֱ�Ӵ���������ȡ!
	public static long ll_firstPageNotFromPkCache = 0; //

	public static long ll_fromCache = 0;
	public static long ll_notfromCache = 0;

	public static HashVO[] allCascadeRefFieldHVO = null; //�����ֵ������м���ɾ����������

	private static HashMap<String, String> singleEncrypt = new HashMap<String, String>(); //�����¼��Կ

	public static DESKeyTool des = new DESKeyTool();
	private ServerEnvironment() {
		map = new VectorMap();
		descmap = new HashMap();
	}

	public static ServerEnvironment getInstance() {
		if (serverEnv != null) {
			return serverEnv;
		}

		serverEnv = new ServerEnvironment(); ////.......
		return serverEnv;
	}

	public synchronized void put(Object _key, Object _value) {
		map.put(_key, _value);
	}

	public synchronized void put(Object _key, String _desc, Object _value) {
		map.put(_key, _value);
		descmap.put(_key, _desc);
	}

	/**
	 * Ϊ����ϵͳ��������,�ṩһ���û���ʱ�洢��Map,����clientPropһ��! �����map����һ��,�ڿ���̨�ȵط��������!
	 * ����������Ŀ�ǵ����¼ʱ�����м����Զ���html�ļ�,���ÿ�μ��ر�Ȼ��!!������Ҫһ������!!
	 * @param _key
	 * @param _value
	 */
	public void putCustMap(Object _key, Object _value) {
		if (custMap == null) {
			custMap = new HashMap();
		}
		custMap.put(_key, _value);
	}

	public Object getCustMapValue(Object _key) {
		if (custMap == null) {
			return null; //
		}
		return custMap.get(_key); //
	}

	public Object get(Object _key) {
		return map.get(_key); //
	}

	public String[] getKeys() {
		return map.getKeysAsString(); //
	}

	public String[] getRowValue(Object _key) {
		String[] str_return = new String[3];
		if (get(_key) == null) {
			return null;
		}

		str_return[0] = (String) _key;
		str_return[1] = descmap.get(_key) == null ? "" : descmap.get(_key).toString();
		str_return[2] = "" + get(_key);
		return str_return;
	}

	public String[][] getAllData() {
		ArrayList al_temp = new ArrayList(); //
		String[] str_keys = getKeys();
		//String[][] str_data = new String[str_keys.length][3];
		for (int i = 0; i < str_keys.length; i++) {
			String[] str_rowData = new String[] { str_keys[i], null, null }; //
			String[] rowValue = getRowValue(str_keys[i]);
			if (rowValue != null) {
				str_rowData[1] = rowValue[1];
				str_rowData[2] = rowValue[2];
			}
			al_temp.add(str_rowData); //
		}

		String[] str_propkeys = (String[]) serverProperties.keySet().toArray(new String[0]); //
		for (int i = 0; i < str_propkeys.length; i++) {
			al_temp.add(new String[] { str_propkeys[i], null, (String) serverProperties.get(str_propkeys[i]) }); //
		}
		return (String[][]) al_temp.toArray(new String[0][0]);
	}

	/**
	 * Ĭ������Դ����
	 * @return
	 */
	public static String getDefaultDataSourceName() {
		return getInstance().str_defaultdatasource;
	}

	/**
	 * Ĭ������Դ����
	 * @return
	 */
	public static String getDefaultDataSourceType() {
		return getInstance().getDataSourceVOs()[0].getDbtype(); //
	}

	/**
	 * ����Ĭ������Դ..
	 * @param _defaultdatasource
	 */
	public void setDefaultdatasource(String _defaultdatasource) {
		this.str_defaultdatasource = _defaultdatasource;
	}

	public String getConfigFileName() {
		return configFileName;
	}

	public void setConfigFileName(String configFileName) {
		this.configFileName = configFileName;
	}

	public DataSourceVO[] getDataSourceVOs() {
		return dataSourceVOs;
	}

	public DataSourceVO getDataSourceVO(String _name) {
		if (_name == null) {
			_name = getDefaultDataSourceName(); //
		}

		for (int i = 0; i < getDataSourceVOs().length; i++) {
			if (getDataSourceVOs()[i].getName().equalsIgnoreCase(_name)) {
				return getDataSourceVOs()[i];
			}
		}

		return null;
	}

	public synchronized void setDataSourceVOs(DataSourceVO[] dataSourceVOs) {
		this.dataSourceVOs = dataSourceVOs;
	}

	public InitParamVO[] getInitParamVOs() {
		return initParamVOs;
	}

	public synchronized void setInitParamVOs(InitParamVO[] initParamVOs) {
		this.initParamVOs = initParamVOs;
	}

	public static String[] getExt3Jars() {
		return ext3Jars;
	}

	public static void setExt3Jars(String[] ext3Jars) {
		ServerEnvironment.ext3Jars = ext3Jars;
	}

	public static void setExt3JarClsNames(String[] _ext3JarClsNames) {
		ServerEnvironment.ext3JarClsNames = _ext3JarClsNames; //
	}

	public static String[] getExt3JarClsNames() {
		return ext3JarClsNames;
	}

	public static String[] getBin3Dlls() {
		return bin3Dlls;
	}

	public static void setBin3Dlls(String[] _bin3Dlls) {
		ServerEnvironment.bin3Dlls = _bin3Dlls;
	}

	public Log4jConfigVO getLog4jConfigVO() {
		return log4jConfigVO;
	}

	public synchronized void setLog4jConfigVO(Log4jConfigVO log4jConfigVO) {
		this.log4jConfigVO = log4jConfigVO;
	}

	public String[] getImagesNames() {
		return imagesNames;
	}

	public synchronized void setImagesNames(String[] imagesNames) {
		this.imagesNames = imagesNames;
	}

	public static CommDMO getCommDMO() {
		return new CommDMO();
	}

	public static MetaDataDMO getMetaDataDMO() { //
		return new MetaDataDMO(); //
	}

	public static HashMap getLoginUserMap() {
		return loginUserMap;
	}

	/**
	 * ���÷�����������..
	 * @param _key
	 * @param _value
	 */
	public static void setProperty(String _key, String _value) {
		serverProperties.put(_key, _value); //
	}

	/**
	 * ���ط�����������..
	 * @param _key
	 * @return
	 */
	public static String getProperty(String _key) {
		return (String) serverProperties.get(_key); //
	}

	//����Ϊ��ʱĬ�Ϸ���ʲô,����HashVO�еķ���һ������
	public static String getProperty(String _key, String _nvl) {
		String str_value = (String) serverProperties.get(_key); //
		if (str_value == null) {
			return _nvl;
		} else {
			return str_value; //
		}
	}

	public static HashMap getPropertys() {
		return serverProperties; //
	}

	public static String[][] getLoginHref() {
		return loginHref;
	}

	public static void setLoginHref(String[][] loginHref) {
		ServerEnvironment.loginHref = loginHref;
	}

	public static HashMap getSessionSqlListenerMap() {
		return sessionSqlListenerMap;
	}

	public static void setSessionSqlListenerMap(HashMap sessionSqlMap) {
		sessionSqlListenerMap = sessionSqlMap;
	}

	/**
	 * ȡ����Ҫ���д��������������б�!!
	 * @return
	 */
	public static HashMap getDBTriggerTableMap() {
		if (dbTriggerTableMap != null) {
			return dbTriggerTableMap; //
		}
		dbTriggerTableMap = new HashMap(); //
		String str_tables = cn.com.infostrategy.bs.common.SystemOptions.getStringValue("��������ʽ�����ı�", null); //
		if (str_tables != null && !str_tables.trim().equals("")) { //��Ϊ��!!!
			TBUtil tbUtil = new TBUtil(); //
			String[] str_items = tbUtil.split(str_tables, ";"); //
			for (int i = 0; i < str_items.length; i++) {
				if (str_items[i].indexOf("/") > 0) { //�����/
					dbTriggerTableMap.put(str_items[i].substring(0, str_items[i].indexOf("/")).toLowerCase(), str_items[i].substring(str_items[i].indexOf("/") + 1, str_items[i].length())); ////
				} else {
					dbTriggerTableMap.put(str_items[i].toLowerCase(), str_items[i]); //
				}
			}
		}
		return dbTriggerTableMap;
	}

	/*
	 * �Ѵ�������������ֶ�������Ϣ�ŵ������С����뵽PUB_DBTRIGGERLOG_B���У���Ҫ��table.xml��descr������д�����<50�ַ�.[����2012-11-27]
	 */
	public static HashMap getDBTriggerTableDescrMap() {
		if (dbTriggerTableDescrMap != null) {
			return dbTriggerTableDescrMap;
		}
		getDBTriggerTableMap();
		dbTriggerTableDescrMap = new HashMap();
		if (dbTriggerTableMap.size() > 0) {
			InstallDMO installdmo = new InstallDMO();
			TBUtil tbutil = new TBUtil();
			Iterator it = dbTriggerTableMap.keySet().iterator();
			while (it.hasNext()) {
				String tableName = (String) it.next();
				try {
					String table_descr[][] = installdmo.getAllIntallTabColumnsDescr(tableName);
					HashMap col_descr = new HashMap();
					for (int i = 0; i < table_descr.length; i++) {
						String table_col = table_descr[i][0];
						String descr = table_descr[i][1];
						if (descr == null) {
							descr = "";
						}
						if (table_col != null && table_col.contains(".")) {
							String col = tbutil.split(table_col, ".")[1]; //�ҵ�col��key
							col_descr.put(col.toUpperCase(), descr);
						}
					}
					dbTriggerTableDescrMap.put(tableName.toUpperCase(), col_descr);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return dbTriggerTableDescrMap;
	}

	public static Queue getServerConsoleQueue() {
		return serverConsoleQueue;
	}

	public static String[][] getInnerSys() {
		return innerSys;
	}

	public static void setInnerSys(String[][] innerSys) {
		ServerEnvironment.innerSys = innerSys;
	}
	//
	public static String getSingleEncrypt(String _userCode) {
		if(TBUtil.isEmpty(_userCode)){
			return "error";
		}
		String str = singleEncrypt.get(_userCode);
		if (TBUtil.isEmpty(str)) {
			str = System.currentTimeMillis() + "";
			str = des.encrypt(str);
			str = URLEncoder.encode(str);
			singleEncrypt.put(_userCode, str);
		}
		return str;
	}
	//�Ƴ�Ψһ��
	public static void removeSingleEncrypt(String _userCode) {
		if(TBUtil.isEmpty(_userCode)){
			return ;
		}
		singleEncrypt.remove(_userCode);
	}

}
