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
 * 服务器端缓存
 * @author user
 *
 */
public class ServerEnvironment implements Serializable {

	private static final long serialVersionUID = -3231827644600597388L;

	private static ServerEnvironment serverEnv = null; //

	public static int EVER_MAX_ONLINEUSERS = 0; //曾经最大的在线用户数
	public static long EVER_MAX_TOTALMEMORY = 0; //曾经最大的内存数

	public static boolean isPrintExceptionStack = false; //是否打印异常堆栈,只限于使用TBUtil.printStackTrace()时起效果,因为有时在一些循环处理一些逻辑时,为了防止频繁打印异常堆栈导致性能极慢(比如加载公式中),所以默认是不输堆栈的!

	private String configFileName = null;
	private VectorMap map = null;
	private HashMap descmap = null;
	private HashMap custMap = null; //用户自定义需要的缓存!
	private String str_defaultdatasource = null;

	public static Vector vc_alltables = null; //
	private Log4jConfigVO log4jConfigVO = null;
	private InitParamVO[] initParamVOs = null;
	private DataSourceVO[] dataSourceVOs = null;
	private static String[] ext3Jars = null; //ext3的文件清单..
	private static String[] bin3Dlls = null; //ext3的文件清单..
	private static String[] ext3JarClsNames = null; //ext3的文件清单..

	private String[] imagesNames = null; //所有图片的列表!!

	private static HashMap loginUserMap = new HashMap(); //记录登录人员的哈希表
	private static HashMap serverProperties = new HashMap(); //服务器端属性....
	private static String[][] loginHref = null; //
	private static String[][] innerSys = null; //

	private static HashMap sessionSqlListenerMap = new HashMap(); //
	private static HashMap dbTriggerTableMap = null; //注册的需要进行增删改触发监听的表名清单!!!
	private static HashMap dbTriggerTableDescrMap = null; //改触发监听的表名清单的表的列描述.从table.xml中读取[2012-11-27郝明]

	public static boolean isLoadRunderCall = false; //是否是LoadRunDer在调用!!,即我们后来搞了个线程调用过程监控的东东,还有其他许多地方做了一些监控处理等!结果后来在招行现场进行压力测试时这些东东都会在压力测试时影响性能! 所以必须有个开关能一下子关掉所有这些东东!! 从而保证压力测试时能过!
	public static boolean isPaginationInCache = false; //分页时是否做缓存,一共有两个缓存,一个是count(*)缓存,一个是首页主键缓存!!因为在招行项目中压力测试时死活不达标,所以一度使用缓存技术,但后来客户不认可!所以继续研究发现是SessionFactory中的同步造成的! 所以默认改成了False! 即默认是不做缓存的! 
	//以后可以考虑直接去掉!但由于做缓存还是效率非常高的(能达到hit500/每秒)! 所以说以后应在模板定义中放这个参数,即将最常用的数据量又大,容易出性能问题的功能点做缓存! 比如内外规查询! 即选择性使用该技术!! 
	//以后所有的这种参数都有这样一种机制,即系统有个总开关,然后模板中有个分开关! 总开关:有Y/N(强Y/强N),分开关有三个值: Y/N/使用总开关配置,即分开关有自己的权力!总开关有强开强关权利! 即总开关有4个值!分开关有3个值!!

	public static boolean isOutPutToQueue = true; //是否将控制台信息输出到缓存队列中?默认是输出的,但邮储项目中竟然发现因为这个输出而造成内存溢出???

	public static boolean isPageFalsity = false; //是否分页造假,即有时压力测试时,不允许使用缓存!必须每次都查询数据库!则hit80根本达不到!! 所以干脆造假!
	public static int realBusiCallCountOneSecond = 15; //每秒超过多少个认为是真的很快!!
	public static int falsitySleep = 300; //休息300毫秒!
	public static int falsityThreadLimit = 300; //虚拟线程数量!

	public static int pageFalsityCycleCount = 5; //每6次搞一下虚拟缓存!
	public static int newThreadCallCount = 0; //

	public static Hashtable pageFalsityMapCount = new Hashtable(); //分页造假的哈希表! key是表名,Value是一页的数据! 必须返回实际一页数据,否则LR测试时会发现没网络流量!! 另外必须是一个表一条记录而不是一条SQL一个,因为否则会内存溢出!!
	public static HashMap pageFalsityMapData = new HashMap();
	public static long ll_falsity_1 = 0; //
	public static long ll_falsity_2 = 0; //

	public static boolean isPageCountInCache = false; //分页的求和SQL是否做缓存??
	public static int pacgeCountCacheCycle = -1; //

	private static Queue serverConsoleQueue = new Queue(500); //服务器端控制台队列,即记录服务器端控制观所有输出!!,最多存2000行!，后为发现太大会造成JVM内存太高,所以搞成500算了,毕竟这个功能用的少!【xch/2012-08-22】

	public static Hashtable pagePKValue = new Hashtable(); //分页的所有主键的缓存!即为了更高的提高性能,将第一页的数据的id进行缓存!!! 这样下次再查时则快的多!!
	public static Hashtable countSQLCache = new Hashtable(); //一定要是Hashtable而不是HashMap,因为这里存在多线程访问! 因为Hashtable是线程安全的,而HashMap不是! 切记!!

	public static long ll_firstPageFromPkCache = 0; //首页记录直接从主键缓存取!
	public static long ll_firstPageNotFromPkCache = 0; //

	public static long ll_fromCache = 0;
	public static long ll_notfromCache = 0;

	public static HashVO[] allCascadeRefFieldHVO = null; //数据字典中所有级联删除定义的外键

	private static HashMap<String, String> singleEncrypt = new HashMap<String, String>(); //单点登录密钥

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
	 * 为了与系统参数区别,提供一个用户临时存储的Map,就像clientProp一样! 如果与map放在一起,在控制台等地方都会出来!
	 * 比如中铁项目是单点登录时遇到有加载自定义html文件,如果每次加载必然慢!!所以需要一个缓存!!
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
	 * 默认数据源名称
	 * @return
	 */
	public static String getDefaultDataSourceName() {
		return getInstance().str_defaultdatasource;
	}

	/**
	 * 默认数据源类型
	 * @return
	 */
	public static String getDefaultDataSourceType() {
		return getInstance().getDataSourceVOs()[0].getDbtype(); //
	}

	/**
	 * 设置默认数据源..
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
	 * 设置服务器端属性..
	 * @param _key
	 * @param _value
	 */
	public static void setProperty(String _key, String _value) {
		serverProperties.put(_key, _value); //
	}

	/**
	 * 返回服务器端属性..
	 * @param _key
	 * @return
	 */
	public static String getProperty(String _key) {
		return (String) serverProperties.get(_key); //
	}

	//增加为空时默认返回什么,就像HashVO中的方法一样。。
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
	 * 取得需要进行触发器监听的所有表!!
	 * @return
	 */
	public static HashMap getDBTriggerTableMap() {
		if (dbTriggerTableMap != null) {
			return dbTriggerTableMap; //
		}
		dbTriggerTableMap = new HashMap(); //
		String str_tables = cn.com.infostrategy.bs.common.SystemOptions.getStringValue("触发器方式监听的表", null); //
		if (str_tables != null && !str_tables.trim().equals("")) { //不为空!!!
			TBUtil tbUtil = new TBUtil(); //
			String[] str_items = tbUtil.split(str_tables, ";"); //
			for (int i = 0; i < str_items.length; i++) {
				if (str_items[i].indexOf("/") > 0) { //如果有/
					dbTriggerTableMap.put(str_items[i].substring(0, str_items[i].indexOf("/")).toLowerCase(), str_items[i].substring(str_items[i].indexOf("/") + 1, str_items[i].length())); ////
				} else {
					dbTriggerTableMap.put(str_items[i].toLowerCase(), str_items[i]); //
				}
			}
		}
		return dbTriggerTableMap;
	}

	/*
	 * 把触发器监听表的字段描述信息放到缓存中。插入到PUB_DBTRIGGERLOG_B表中，需要把table.xml表descr描述填写清楚，<50字符.[郝明2012-11-27]
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
							String col = tbutil.split(table_col, ".")[1]; //找到col的key
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
	//移除唯一码
	public static void removeSingleEncrypt(String _userCode) {
		if(TBUtil.isEmpty(_userCode)){
			return ;
		}
		singleEncrypt.remove(_userCode);
	}

}
