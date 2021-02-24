package cn.com.infostrategy.ui.common;

import java.util.Hashtable;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;

/**
 * �ͻ������Ի���
 * @author xch
 *
 */
public class ClientLanguageCache {

	private static ClientLanguageCache clientLanguage = null;

	private Hashtable hashTable = new Hashtable(); //

	private boolean ifFirstGetData = false;
	private FrameWorkCommServiceIfc service = null;

	/**
	 * ���췽��
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
		if ("Y".equalsIgnoreCase(System.getProperty("ISLOADRUNDERCALL"))) { //�����LoadRunder����ʡȥ�������!�Ժ���Ժϲ�!
			return _key; //
		}

		if (!ifFirstGetData) { //��һ���ȼ�����������
			try {
				Hashtable ht = getService().getLanguage();
				hashTable.putAll(ht); //
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				ifFirstGetData = true; //
			}
		}

		String[] str_languages = (String[]) hashTable.get(_key); //�ȴӿͻ��˻�����ȡ
		if (str_languages != null) { //����ӻ�����ȡ����!
			if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.SIMPLECHINESE)) { //��������
				return str_languages[0]; //
			} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
				return str_languages[1]; //Ӣ��
			} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.TRADITIONALCHINESE)) {
				return str_languages[2]; //��������
			} else {
				return str_languages[0];
			}
		} else {
			try {
				String[] str_tmp_language = getService().getLanguage(_key); ////ȥԶ��ȡ,�϶���ȡ��,���û��,�����Զ��������в���һ������!!
				hashTable.put(str_tmp_language[0], str_tmp_language); //���뻺��,�´ξͲ�Ҫ��Զ��ȡ��!!!!
				if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.SIMPLECHINESE)) { //��������
					return str_tmp_language[0]; //��������
				} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.ENGLISH)) {
					return str_tmp_language[1]; //Ӣ��
				} else if (ClientEnvironment.getInstance().getDefaultLanguageType().equals(WLTConstants.TRADITIONALCHINESE)) {
					return str_tmp_language[2]; //��������
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
	 * ȡ�÷���..
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
