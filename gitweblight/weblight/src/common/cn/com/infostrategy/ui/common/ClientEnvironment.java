/**************************************************************************
 * $RCSfile: ClientEnvironment.java,v $  $Revision: 1.16 $  $Date: 2012/09/14 09:17:30 $
 **************************************************************************/
package cn.com.infostrategy.ui.common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Vector;

import cn.com.infostrategy.to.common.CurrSessionVO;
import cn.com.infostrategy.to.common.DataSourceVO;
import cn.com.infostrategy.to.common.Log4jConfigVO;
import cn.com.infostrategy.to.common.Queue;
import cn.com.infostrategy.to.common.VectorMap;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

/**
 * 客户端缓存
 * @author user
 *
 */
public final class ClientEnvironment implements Serializable {

	private static final long serialVersionUID = -3231827644600597388L;

	private static Queue clientConsoleQueue = new Queue(2000); //服务器端控制台队列,即记录服务器端控制观所有输出!!,最多存2000行!

	private static ClientEnvironment clientEnv = null; //
	private static CurrLoginUserVO currLoginUserVO = new CurrLoginUserVO(); //记录当前登录人员的VO
	private static CurrSessionVO currSessionVO = new CurrSessionVO(); //当前会话VO
	private static String defaultLanguageType = WLTConstants.SIMPLECHINESE; //默认语言类型
	private String url = null; //远程访问的url

	public static int LOGINMODEL_NORMAL = 0; //登录模式,普通身份登录.
	public static int LOGINMODEL_ADMIN = 1; //登录模式,管理员身份登录.
	public static int STARTMODEL_DESKTOP = 0; //启动模式,桌面快捷方式启动
	public static int STARTMODEL_BROWSE = 1; //启动模式,浏览器启动
	public static int WORKMODEL_DEBUG = 0; //工作模式,Debug模式,也称为开发模式
	public static int WORKMODEL_RUN = 1; //工作模式,Run模式,也称为正常运行模式
	public static int DEPLOYMODEL_SINGLE = 0; //部署模式,单机部署
	public static int DEPLOYMODEL_CLUSTER = 1; //部署模式,集群部署

	private VectorMap map = null; //
	private HashMap descmap = null; //

	private DataSourceVO[] dataSourceVOs = null;
	private Log4jConfigVO log4jConfigVO = null; //!!

	private String[] imagesNames = null; //所有图片的列表!
	private static String[][] clientSysOptions = null; //系统参数,从pub_option表中取数,共3列

	private static String[][] innerSys = null; //
	public static String chooseISys = null; //选中的子系统

	public static HashMap callSessionTrackMsg = new HashMap(); //某次远程调用的消息记录
	public static Vector clientTimerMap = new Vector(); // 

	public static String str_downLoadFileDir = "C:\\"; //
	public static boolean isOutPutCallObjToFile = false; //
	public static Long llDiffServerTime = null; //客户端与服务器端的时间差异,因为每次取服务器时间时很耗时,但取客户端又不准,所以最好的方案是在第一次先取下服务器端的时间,然后记录下两者差异,然后每次取客户端时都补上这个差异!这样在使用程序的期间两者就非常接近了!

	private ClientEnvironment() {
		map = new VectorMap();
		descmap = new HashMap();
	}

	public final static ClientEnvironment getInstance() {
		if (clientEnv != null) {
			return clientEnv;
		}
		clientEnv = new ClientEnvironment();
		return clientEnv;
	}

	//送入值
	public final void put(Object _key, Object _value) {
		map.put(_key, _value);
	}

	//清除某一个key的值
	public final void remove(Object _key) {
		map.remove(_key); //
	}

	public final void put(Object _key, String _desc, Object _value) {
		map.put(_key, _value);
		descmap.put(_key, _desc);
	}

	public final Object get(Object _key) {
		return map.get(_key); //
	}

	public String[] getKeys() {
		return map.getKeysAsString(); //
	}

	public boolean containsKey(Object _key) {
		return map.containsKey(_key); //
	}

	public void appendValue(Object _key, String _value) {
		if (!map.containsKey(_key)) { //如果不包含,则直接加入!
			map.put(_key, _value); //
		} else { //否则先将旧的取出来,与新值拼接然后再塞入!!!
			String str_oldValue = (String) map.get(_key); //
			map.put(_key, str_oldValue + _value); //
		}
	}

	public String[] getRowValue(Object _key) {
		String[] str_return = new String[3];
		if (get(_key) == null) {
			return null;
		}

		str_return[0] = (String) _key;
		str_return[1] = descmap.get(_key) == null ? "" : descmap.get(_key).toString();
		Object obj = get(_key);
		String str_value_tmp = "";
		if (obj.getClass().isArray()) {
			Object[] arrays = (Object[]) obj;
			for (int i = 0; i < arrays.length; i++) {
				str_value_tmp = str_value_tmp + arrays[i];
				if (i != arrays.length - 1) {
					str_value_tmp = str_value_tmp + ",";
				}
			}
		} else {
			str_value_tmp = "" + obj;
		}
		str_return[2] = str_value_tmp;
		return str_return;
	}

	public String[][] getAllData() {
		String[] str_keys = getKeys();
		String[][] str_data = new String[str_keys.length][3];
		for (int i = 0; i < str_keys.length; i++) {
			str_data[i][0] = str_keys[i]; //

			String[] rowValue = getRowValue(str_keys[i]);
			if (rowValue != null) {
				str_data[i][1] = getRowValue(str_keys[i])[1];
				str_data[i][2] = getRowValue(str_keys[i])[2];
			}

		}
		return str_data;
	}

	/**
	 * 默认数据源!!!
	 * @return
	 */
	public String getDefaultDataSourceName() {
		DataSourceVO[] dsvos = getDataSourceVOs(); //
		if (dsvos == null) {
			return null;
		}
		if (getDataSourceVOs()[0] == null) {
			return null;
		}
		return getDataSourceVOs()[0].getName(); //
	}

	public String getDefaultDataSourceType() {
		return getDataSourceVOs()[0].getDbtype(); //
	}

	public int getAppletWidth() {
		if (System.getProperty("APPLETWIDTH") != null) {
			return Integer.parseInt(System.getProperty("APPLETWIDTH"));
		} else {
			return 1000;
		}

	}

	/**
	 * 取得默认语言类型
	 * @return
	 */
	public String getDefaultLanguageType() {
		return defaultLanguageType; //
	}

	public boolean isEngligh() {
		if (getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 设置默认语言类型
	 * @param _languageType
	 * @return
	 */
	public String setDefaultLanguageType(String _languageType) {
		return defaultLanguageType = _languageType; //
	}

	/**
	 * 取得登录用户的主键
	 * @return
	 */
	public String getLoginUserID() {
		return getCurrLoginUserVO().getId(); //
	}

	/**
	 * 取得登录用户的编码
	 * @return
	 */
	public String getLoginUserCode() {
		return getCurrLoginUserVO().getCode(); //
	}

	/**
	 * 取得登录用户的编码
	 * @return
	 */
	public String getLoginUserName() {
		return getCurrLoginUserVO().getName(); //
	}

	/**
	 * 取得登录用户所属部门主键
	 * @return
	 */
	public String getLoginUserDeptId() {
		return getCurrLoginUserVO().getPKDept(); //
	}

	/**
	 * zzl 取得登录用户所属网点是否涉农
	 * @return
	 */
	public static String getLoginUserDeptCorpdistinct() {
		return getCurrLoginUserVO().getCorpdistinct(); //
	}

	/**
	 * zzl 取得登录用户机构涉农类型
	 * @return
	 */
	public static String getLoginUserDeptCorpclass() {
		return getCurrLoginUserVO().getCorpclass(); //
	}

	/**
	 * 取得登录用户所属部门名称
	 * @return
	 */
	public static String getLoginUserDeptName() {
		return getCurrLoginUserVO().getDeptname(); //
	}
	/**
	 * 取得登录用户所属部门主键
	 * @return
	 */
	public static String getLoginUserDeptLinkCode() {
		return getCurrLoginUserVO().getDeptlinkcode(); //
	}

	/**
	 * 取得登录人员所有岗位编码,拼成Condision条件
	 * @return
	 */
	public String getLoginUserPostCodesInCondision() {
		String postcode = "";
		String[] postcodes = getCurrLoginUserVO().getAllPostCodes();
		for (int i = 0; i < postcodes.length; i++) {
			if (i == postcodes.length - 1) {
				postcode += "'" + postcodes[i] + "'";
			} else {
				postcode += "'" + postcodes[i] + "',";
			}
		}

		return postcode; //
	}

	/**
	 * 取得登录人员所有岗位编码,包括主属与兼职的
	 * @return
	 */
	public String[] getLoginUserPostCodes() {
		return getCurrLoginUserVO().getAllPostCodes(); //
	}

	/**
	 * 取得登录人员所有岗位主键,包括主属与兼职的
	 * @return
	 */
	public String[] getLoginUserPostIds() {
		return getCurrLoginUserVO().getAllPostCodes(); //
	}

	/**
	 * 取得登录人员所有角色编码
	 * @return
	 */
	public String[] getLoginUserRoleCodes() {
		return getCurrLoginUserVO().getAllRoleCodes(); //
	}

	/**
	 * 取得登录人员所有角色主键
	 * @return
	 */
	public String[] getLoginUserRoleIds() {
		return getCurrLoginUserVO().getAllRoleIds(); //
	}

	public static void setCurrCallSessionTrackMsg(String _msg) {
		Thread[] allThreads = (Thread[]) callSessionTrackMsg.keySet().toArray(new Thread[0]); //
		Thread thisThread = Thread.currentThread(); //
		for (int i = 0; i < allThreads.length; i++) { //删除不是本线程的所有其他数据,从而防止内存泄露!!
			if (allThreads[i] != thisThread) {
				callSessionTrackMsg.remove(allThreads[i]); //清空!!
			}
		}
		callSessionTrackMsg.put(thisThread, _msg); //设置!!!
	}

	public static String getCurrCallSessionTrackMsg() {
		Thread thisThread = Thread.currentThread(); //
		String str_msg = (String) callSessionTrackMsg.get(thisThread); //
		return str_msg; //
	}

	public static String getCurrCallSessionTrackMsgAndClear() {
		Thread thisThread = Thread.currentThread(); //
		String str_msg = (String) callSessionTrackMsg.get(thisThread); //
		callSessionTrackMsg.remove(thisThread); //清空!!
		return str_msg; //
	}

	/**
	 * 取得登录模式
	 * @return
	 */
	public int getLoginModel() {
		if (System.getProperty("LOGINMODEL") != null && System.getProperty("LOGINMODEL").equals("ADMIN")) {
			return LOGINMODEL_ADMIN;
		} else {
			return LOGINMODEL_NORMAL;
		}
	}

	public static boolean isAdmin() {
		int li_model = getInstance().getLoginModel(); //
		if (li_model == LOGINMODEL_ADMIN) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isMultiLanguage() {
		if (System.getProperty("MULTILANGUAGE") != null && System.getProperty("MULTILANGUAGE").equalsIgnoreCase("YES")) {
			return true;
		} else {
			return false;
		}
	}

	public void setLoginModel(int _model) {
		if (_model == LOGINMODEL_ADMIN) {
			System.setProperty("LOGINMODEL", "ADMIN");
		} else if (_model == LOGINMODEL_NORMAL) {
			System.setProperty("LOGINMODEL", "NORMAL");
		} else {
			System.setProperty("LOGINMODEL", "NORMAL");
		}
	}

	/**
	 * 启动模式
	 * @return
	 */
	public int getStartModel() {
		if (System.getProperty("STARTMODEL") != null && System.getProperty("STARTMODEL").equals("DESKTOP")) {
			return STARTMODEL_DESKTOP; //桌面快捷方式启动
		} else {
			return STARTMODEL_BROWSE; //
		}
	}

	/**
	 * 工作模式!!!
	 * @return
	 */
	public int getWorkModel() {
		if (System.getProperty("WORKMODEL") != null && System.getProperty("WORKMODEL").equals("DEBUG")) {
			return WORKMODEL_DEBUG; //桌面快捷方式启动
		} else {
			return WORKMODEL_RUN; //
		}
	}

	/**
	 * 部署模式!!!
	 * @return
	 */
	public int getDeployModel() {
		if (System.getProperty("DEPLOYMODEL") != null && System.getProperty("DEPLOYMODEL").equals("CLUSTER")) {
			return DEPLOYMODEL_CLUSTER; //桌面快捷方式启动
		} else {
			return DEPLOYMODEL_SINGLE; //
		}
	}

	/**
	 * 取得客户端代码缓存的路径
	 * @return
	 */
	public String getClientCodeCachePath() {
		return System.getProperty("ClientCodeCache"); //
	}

	/**
	 * 取得URL地址!!!
	 * @return
	 */
	public String getUrl() {
		return System.getProperty("URL"); //
	}

	/**
	 * 取得CALLURL地址!!!
	 * @return
	 */
	public String getCallUrl() {
		return System.getProperty("CALLURL"); //
	}

	public DataSourceVO[] getDataSourceVOs() {
		return dataSourceVOs;
	}

	public void setDataSourceVOs(DataSourceVO[] dataSourceVOs) {
		this.dataSourceVOs = dataSourceVOs;
	}

	public Log4jConfigVO getLog4jConfigVO() {
		return log4jConfigVO;
	}

	public void setLog4jConfigVO(Log4jConfigVO log4jConfigVO) {
		this.log4jConfigVO = log4jConfigVO;
	}

	public static CurrSessionVO getCurrSessionVO() {
		return currSessionVO;
	}

	public String[] getImagesNames() {
		return imagesNames;
	}

	public void setImagesNames(String[] imagesNames) {
		this.imagesNames = imagesNames;
	}

	public static String getDeskTopStyle() {
		return getCurrLoginUserVO().getDeskTopStyle();
	}

	public synchronized static String[][] getClientSysOptions() {
		return clientSysOptions; //
	}

	public synchronized static void setClientSysOptions(String[][] _sysoptions) {
		clientSysOptions = _sysoptions; //
	}

	public static CurrLoginUserVO getCurrLoginUserVO() {
		return currLoginUserVO;
	}

	public static void setCurrLoginUserVO(CurrLoginUserVO currLoginUserVO) {
		ClientEnvironment.currLoginUserVO = currLoginUserVO;
	}

	public static String[][] getInnerSys() {
		return innerSys;
	}

	public static void setInnerSys(String[][] innerSys) {
		ClientEnvironment.innerSys = innerSys;
	}

	/**
	 * 得到客户端控制台信息队列.
	 * @param _msg
	 */
	public static Queue getClientConsoleQueue() {
		return clientConsoleQueue;
	}

	/**
	 * 将客户端的所有输出一个完整的字符串,什么都有!!!
	 * @return
	 */
	public String getClientEnvToString() {
		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("SessionId=[" + currSessionVO.getHttpsessionid() + "]\r\n");
		sb_text.append("\r\n");
		sb_text.append(currLoginUserVO.toString()); //
		return sb_text.toString();
	}

	/**
	 * 取得登录用户的密级
	 * @return
	 */
	public int getLoginUserSecuritylevel() {
		return getCurrLoginUserVO().getSecuritylevel(); //
	}
	
	/**
	 * 取得登录用户所属机构主键(注意不是部门/处室, 是机构!!)
	 * @return
	 */
	public String getLoginUserCorpId() {
		return getCurrLoginUserVO().getCorpID();
	}

	/**
	 * 取得登录用户所属机构名称(注意不是部门/处室, 是机构!!)
	 * @return
	 */
	public String getLoginUserCorpName() {
		return getCurrLoginUserVO().getCorpName(); //
	}
}
