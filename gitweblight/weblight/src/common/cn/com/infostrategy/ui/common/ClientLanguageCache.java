package cn.com.infostrategy.ui.common;

import java.util.Hashtable;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;

/**
 * 客户端语言缓存
 * @author xch
 *
 */
public class ClientLanguageCache {

	private static ClientLanguageCache clientLanguage = null;

	private Hashtable hashTable = new Hashtable(); //

	private boolean ifFirstGetData = false;
	private FrameWorkCommServiceIfc service = null;

	/**
	 * 构造方法
	 */
	private ClientLanguageCache() {
	}

	public static ClientLanguageCache getInstance() {
		if (clientLanguage == null) {
			clientLanguage = new ClientLanguageCache();
		}
		return clientLanguage; //
	}

	public synchronized String getLanguage(String _key) {
		if ("Y".equalsIgnoreCase(System.getProperty("ISLOADRUNDERCALL"))) { //如果是LoadRunder可以省去这个操作!以后可以合并!
			return _key; //
		}

		if (!ifFirstGetData) { //第一次先加载所有语言
			try {
				Hashtable ht = getService().getLanguage();
				hashTable.putAll(ht); //
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ifFirstGetData = true; //
			}
		}

		String[] str_languages = (String[]) hashTable.get(_key); //先从客户端缓存中取
		if (str_languages != null) { //如果从缓存中取到了!
			if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.SIMPLECHINESE)) { //简体中文
				return str_languages[0]; //
			} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
				return str_languages[1]; //英文
			} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.TRADITIONALCHINESE)) {
				return str_languages[2]; //繁体中文
			} else {
				return str_languages[0];
			}
		} else {
			try {
				String[] str_tmp_language = getService().getLanguage(_key); ////去远程取,肯定能取得,如果没有,则会在远程往库存中插入一条数据!!
				hashTable.put(str_tmp_language[0], str_tmp_language); //加入缓存,下次就不要从远程取了!!!!
				if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.SIMPLECHINESE)) { //简体中文
					return str_tmp_language[0]; //简体中文
				} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
					return str_tmp_language[1]; //英文
				} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.TRADITIONALCHINESE)) {
					return str_tmp_language[2]; //繁体中文
				} else {
					return str_tmp_language[0];
				}
			} catch (Exception e) {
				e.printStackTrace();
				hashTable.put(_key, new String[] { _key, _key, _key }); //
				return _key;
			}
		}
	}

	/**
	 * 取得服务..
	 * @return
	 * @throws WLTRemoteException
	 * @throws Exception
	 */
	private FrameWorkCommServiceIfc getService() throws WLTRemoteException, Exception {
		if (service == null) {
			service = (FrameWorkCommServiceIfc) RemoteServiceFactory.getInstance().lookUpService(FrameWorkCommServiceIfc.class);
		}

		return service;
	}
}
