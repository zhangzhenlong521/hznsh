package cn.com.infostrategy.to.mdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ͨ�õ�UI�ؼ���������ݶ���!! ��ǰ�����Ͷ���,���Ͳ��ն�����һ����,��չ�ܲ�����!!
 * �ÿؼ�����Ĳ���,�ȿ����Ƿ������˹��������õ�,����SQL��Ҳ������UI�˿ؼ�Ч���õ�,�����Ƿ�ɱ༭��!!
 * @author xch
 *
 */
public class CommUCDefineVO implements Serializable {

	private static final long serialVersionUID = -6740334691570327167L;
	private String typeName = null; //��������!!����:���Ͳ���,���Ͳ���...��
	private LinkedHashMap confMap = new LinkedHashMap(); //���ò���,֧������!!!

	public CommUCDefineVO() {
	}

	public CommUCDefineVO(String _typeName) {
		this.typeName = _typeName; //
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * �ؼ���������,����:���Ͳ���,���Ͳ���...��
	 * @return
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * ȡ���������ò����Ĺ�ϣ��!!
	 * @return
	 */
	public LinkedHashMap getConfMap() {
		return confMap; //
	}

	public void setConfMap(LinkedHashMap confMap) {
		this.confMap = confMap;
	}

	/**
	 * �������ò���ֵ!!!
	 * @param _key
	 * @param _value
	 */
	public void setConfValue(String _key, String _value) {
		confMap.put(_key, _value); //
	}

	public void setConfValueAll(Map _map) {
		confMap.putAll(_map); //
	}

	/**
	 * �õ����ò���ֵ!!!���˺��Դ�Сд�Ĵ���!!!
	 * @param _key
	 * @return
	 */
	public String getConfValue(String _key) {
		String[] str_keys = getAllConfKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase(_key)) {
				return (String) confMap.get(str_keys[i]); //
			}
		}
		return null; //
	}

	/**
	 * ��Ĭ��ֵ!
	 * @param _key
	 * @param _nvl
	 * @return
	 */
	public String getConfValue(String _key, String _nvl) {
		String str_value = getConfValue(_key); //
		if (str_value == null || str_value.trim().equals("")) { //���Ϊ��,�򷵻�Ĭ��ֵ
			return _nvl; //
		} else {
			return str_value;
		}
	}

	/**
	 * ���Ƿ����һ��������Ϣ!
	 * @param _key
	 * @return
	 */
	public boolean containsConfKey(String _key) {
		String[] str_keys = getAllConfKeys(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (str_keys[i].equalsIgnoreCase(_key)) {
				return true;
			}
		}
		return false; //
	}

	/**
	 * ������������Booleanֵ,������Ҫһ��ֱ�ӽ���Booleanת���ķ���!!!
	 * @param _key
	 * @param _nvl ���û�ж����������,����Ĭ��ֵ��ʲô!!
	 * @return
	 */
	public boolean getConfBooleanValue(String _key, boolean _nvl) { //
		String str_value = getConfValue(_key); //ȡ�ַ���ֵ,���Դ�Сд!!
		if (str_value == null || str_value.trim().equals("")) { //���Ϊ��!!!
			return _nvl; //
		}
		if ("Y".equalsIgnoreCase(str_value) || "Yes".equalsIgnoreCase(str_value) || "True".equalsIgnoreCase(str_value) || "On".equalsIgnoreCase(str_value)) { //������⼸�����,����true
			return true;
		} else if ("N".equalsIgnoreCase(str_value) || "No".equalsIgnoreCase(str_value) || "False".equalsIgnoreCase(str_value) || "Off".equalsIgnoreCase(str_value)) { //����⼸�����,����false
			return false;
		} else {
			return _nvl; //
		}

	}

	public String[] getAllConfKeys() {
		String[] str_keys = (String[]) confMap.keySet().toArray(new String[0]); //
		return str_keys; //
	}

	/**
	 * ��ʱ��Ҫ�ҳ�һЩ�ض���key,����SQL1,SQL2��
	 * @param _likeStr
	 * @param _isStart
	 * @return
	 */
	public String[] getAllConfKeys(String _likeStr, boolean _isStart) {
		String[] str_keys = getAllConfKeys();
		ArrayList al_return = new ArrayList(); //
		for (int i = 0; i < str_keys.length; i++) {
			if (_isStart) {
				if (str_keys[i].toLowerCase().startsWith(_likeStr.toLowerCase())) {
					al_return.add(str_keys[i]); //
				}
			} else {
				if (str_keys[i].toLowerCase().indexOf(_likeStr.toLowerCase()) >= 0) {
					al_return.add(str_keys[i]); //
				}
			}
		}
		return (String[]) al_return.toArray(new String[0]); //
	}

}
