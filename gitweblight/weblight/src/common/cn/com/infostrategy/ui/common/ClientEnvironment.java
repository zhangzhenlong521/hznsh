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
 * �ͻ��˻���
 * @author user
 *
 */
public final class ClientEnvironment implements Serializable {

	private static final long serialVersionUID = -3231827644600597388L;

	private static Queue clientConsoleQueue = new Queue(2000); //�������˿���̨����,����¼�������˿��ƹ��������!!,����2000��!

	private static ClientEnvironment clientEnv = null; //
	private static CurrLoginUserVO currLoginUserVO = new CurrLoginUserVO(); //��¼��ǰ��¼��Ա��VO
	private static CurrSessionVO currSessionVO = new CurrSessionVO(); //��ǰ�ỰVO
	private static String defaultLanguageType = WLTConstants.SIMPLECHINESE; //Ĭ����������
	private String url = null; //Զ�̷��ʵ�url

	public static int LOGINMODEL_NORMAL = 0; //��¼ģʽ,��ͨ��ݵ�¼.
	public static int LOGINMODEL_ADMIN = 1; //��¼ģʽ,����Ա��ݵ�¼.
	public static int STARTMODEL_DESKTOP = 0; //����ģʽ,�����ݷ�ʽ����
	public static int STARTMODEL_BROWSE = 1; //����ģʽ,���������
	public static int WORKMODEL_DEBUG = 0; //����ģʽ,Debugģʽ,Ҳ��Ϊ����ģʽ
	public static int WORKMODEL_RUN = 1; //����ģʽ,Runģʽ,Ҳ��Ϊ��������ģʽ
	public static int DEPLOYMODEL_SINGLE = 0; //����ģʽ,��������
	public static int DEPLOYMODEL_CLUSTER = 1; //����ģʽ,��Ⱥ����

	private VectorMap map = null; //
	private HashMap descmap = null; //

	private DataSourceVO[] dataSourceVOs = null;
	private Log4jConfigVO log4jConfigVO = null; //!!

	private String[] imagesNames = null; //����ͼƬ���б�!
	private static String[][] clientSysOptions = null; //ϵͳ����,��pub_option����ȡ��,��3��

	private static String[][] innerSys = null; //
	public static String chooseISys = null; //ѡ�е���ϵͳ

	public static HashMap callSessionTrackMsg = new HashMap(); //ĳ��Զ�̵��õ���Ϣ��¼
	public static Vector clientTimerMap = new Vector(); // 

	public static String str_downLoadFileDir = "C:\\"; //
	public static boolean isOutPutCallObjToFile = false; //
	public static Long llDiffServerTime = null; //�ͻ�����������˵�ʱ�����,��Ϊÿ��ȡ������ʱ��ʱ�ܺ�ʱ,��ȡ�ͻ����ֲ�׼,������õķ������ڵ�һ����ȡ�·������˵�ʱ��,Ȼ���¼�����߲���,Ȼ��ÿ��ȡ�ͻ���ʱ�������������!������ʹ�ó�����ڼ����߾ͷǳ��ӽ���!

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

	//����ֵ
	public final void put(Object _key, Object _value) {
		map.put(_key, _value);
	}

	//���ĳһ��key��ֵ
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
		if (!map.containsKey(_key)) { //���������,��ֱ�Ӽ���!
			map.put(_key, _value); //
		} else { //�����Ƚ��ɵ�ȡ����,����ֵƴ��Ȼ��������!!!
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
	 * Ĭ������Դ!!!
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
	 * ȡ��Ĭ����������
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
	 * ����Ĭ����������
	 * @param _languageType
	 * @return
	 */
	public String setDefaultLanguageType(String _languageType) {
		return defaultLanguageType = _languageType; //
	}

	/**
	 * ȡ�õ�¼�û�������
	 * @return
	 */
	public String getLoginUserID() {
		return getCurrLoginUserVO().getId(); //
	}

	/**
	 * ȡ�õ�¼�û��ı���
	 * @return
	 */
	public String getLoginUserCode() {
		return getCurrLoginUserVO().getCode(); //
	}

	/**
	 * ȡ�õ�¼�û��ı���
	 * @return
	 */
	public String getLoginUserName() {
		return getCurrLoginUserVO().getName(); //
	}

	/**
	 * ȡ�õ�¼�û�������������
	 * @return
	 */
	public String getLoginUserDeptId() {
		return getCurrLoginUserVO().getPKDept(); //
	}

	/**
	 * zzl ȡ�õ�¼�û����������Ƿ���ũ
	 * @return
	 */
	public static String getLoginUserDeptCorpdistinct() {
		return getCurrLoginUserVO().getCorpdistinct(); //
	}

	/**
	 * zzl ȡ�õ�¼�û�������ũ����
	 * @return
	 */
	public static String getLoginUserDeptCorpclass() {
		return getCurrLoginUserVO().getCorpclass(); //
	}

	/**
	 * ȡ�õ�¼�û�������������
	 * @return
	 */
	public static String getLoginUserDeptName() {
		return getCurrLoginUserVO().getDeptname(); //
	}
	/**
	 * ȡ�õ�¼�û�������������
	 * @return
	 */
	public static String getLoginUserDeptLinkCode() {
		return getCurrLoginUserVO().getDeptlinkcode(); //
	}

	/**
	 * ȡ�õ�¼��Ա���и�λ����,ƴ��Condision����
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
	 * ȡ�õ�¼��Ա���и�λ����,�����������ְ��
	 * @return
	 */
	public String[] getLoginUserPostCodes() {
		return getCurrLoginUserVO().getAllPostCodes(); //
	}

	/**
	 * ȡ�õ�¼��Ա���и�λ����,�����������ְ��
	 * @return
	 */
	public String[] getLoginUserPostIds() {
		return getCurrLoginUserVO().getAllPostCodes(); //
	}

	/**
	 * ȡ�õ�¼��Ա���н�ɫ����
	 * @return
	 */
	public String[] getLoginUserRoleCodes() {
		return getCurrLoginUserVO().getAllRoleCodes(); //
	}

	/**
	 * ȡ�õ�¼��Ա���н�ɫ����
	 * @return
	 */
	public String[] getLoginUserRoleIds() {
		return getCurrLoginUserVO().getAllRoleIds(); //
	}

	public static void setCurrCallSessionTrackMsg(String _msg) {
		Thread[] allThreads = (Thread[]) callSessionTrackMsg.keySet().toArray(new Thread[0]); //
		Thread thisThread = Thread.currentThread(); //
		for (int i = 0; i < allThreads.length; i++) { //ɾ�����Ǳ��̵߳�������������,�Ӷ���ֹ�ڴ�й¶!!
			if (allThreads[i] != thisThread) {
				callSessionTrackMsg.remove(allThreads[i]); //���!!
			}
		}
		callSessionTrackMsg.put(thisThread, _msg); //����!!!
	}

	public static String getCurrCallSessionTrackMsg() {
		Thread thisThread = Thread.currentThread(); //
		String str_msg = (String) callSessionTrackMsg.get(thisThread); //
		return str_msg; //
	}

	public static String getCurrCallSessionTrackMsgAndClear() {
		Thread thisThread = Thread.currentThread(); //
		String str_msg = (String) callSessionTrackMsg.get(thisThread); //
		callSessionTrackMsg.remove(thisThread); //���!!
		return str_msg; //
	}

	/**
	 * ȡ�õ�¼ģʽ
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
	 * ����ģʽ
	 * @return
	 */
	public int getStartModel() {
		if (System.getProperty("STARTMODEL") != null && System.getProperty("STARTMODEL").equals("DESKTOP")) {
			return STARTMODEL_DESKTOP; //�����ݷ�ʽ����
		} else {
			return STARTMODEL_BROWSE; //
		}
	}

	/**
	 * ����ģʽ!!!
	 * @return
	 */
	public int getWorkModel() {
		if (System.getProperty("WORKMODEL") != null && System.getProperty("WORKMODEL").equals("DEBUG")) {
			return WORKMODEL_DEBUG; //�����ݷ�ʽ����
		} else {
			return WORKMODEL_RUN; //
		}
	}

	/**
	 * ����ģʽ!!!
	 * @return
	 */
	public int getDeployModel() {
		if (System.getProperty("DEPLOYMODEL") != null && System.getProperty("DEPLOYMODEL").equals("CLUSTER")) {
			return DEPLOYMODEL_CLUSTER; //�����ݷ�ʽ����
		} else {
			return DEPLOYMODEL_SINGLE; //
		}
	}

	/**
	 * ȡ�ÿͻ��˴��뻺���·��
	 * @return
	 */
	public String getClientCodeCachePath() {
		return System.getProperty("ClientCodeCache"); //
	}

	/**
	 * ȡ��URL��ַ!!!
	 * @return
	 */
	public String getUrl() {
		return System.getProperty("URL"); //
	}

	/**
	 * ȡ��CALLURL��ַ!!!
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
	 * �õ��ͻ��˿���̨��Ϣ����.
	 * @param _msg
	 */
	public static Queue getClientConsoleQueue() {
		return clientConsoleQueue;
	}

	/**
	 * ���ͻ��˵��������һ���������ַ���,ʲô����!!!
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
	 * ȡ�õ�¼�û����ܼ�
	 * @return
	 */
	public int getLoginUserSecuritylevel() {
		return getCurrLoginUserVO().getSecuritylevel(); //
	}
	
	/**
	 * ȡ�õ�¼�û�������������(ע�ⲻ�ǲ���/����, �ǻ���!!)
	 * @return
	 */
	public String getLoginUserCorpId() {
		return getCurrLoginUserVO().getCorpID();
	}

	/**
	 * ȡ�õ�¼�û�������������(ע�ⲻ�ǲ���/����, �ǻ���!!)
	 * @return
	 */
	public String getLoginUserCorpName() {
		return getCurrLoginUserVO().getCorpName(); //
	}
}
