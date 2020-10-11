package cn.com.infostrategy.to.mdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 通用的UI控件定义的数据对象!! 以前是树型对照,表型参照都各有一个类,扩展很不方便!!
 * 该控件定义的参数,既可能是服务器端构造数据用到,比如SQL，也可能是UI端控件效果用到,比如是否可编辑等!!
 * @author xch
 *
 */
public class CommUCDefineVO implements Serializable {

	private static final long serialVersionUID = -6740334691570327167L;
	private String typeName = null; //类型名称!!比如:表型参照,树型参照...等
	private LinkedHashMap confMap = new LinkedHashMap(); //配置参数,支持排序!!!

	public CommUCDefineVO() {
	}

	public CommUCDefineVO(String _typeName) {
		this.typeName = _typeName; //
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * 控件类型名称,比如:表型参照,树型参照...等
	 * @return
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * 取得整个配置参数的哈希表!!
	 * @return
	 */
	public LinkedHashMap getConfMap() {
		return confMap; //
	}

	public void setConfMap(LinkedHashMap confMap) {
		this.confMap = confMap;
	}

	/**
	 * 设置配置参数值!!!
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
	 * 得到配置参数值!!!做了忽略大小写的处理!!!
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
	 * 有默认值!
	 * @param _key
	 * @param _nvl
	 * @return
	 */
	public String getConfValue(String _key, String _nvl) {
		String str_value = getConfValue(_key); //
		if (str_value == null || str_value.trim().equals("")) { //如果为空,则返回默认值
			return _nvl; //
		} else {
			return str_value;
		}
	}

	/**
	 * 看是否包含一个配置信息!
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
	 * 大量参数都是Boolean值,所以需要一个直接进行Boolean转换的方法!!!
	 * @param _key
	 * @param _nvl 如果没有定义这个参数,则其默认值是什么!!
	 * @return
	 */
	public boolean getConfBooleanValue(String _key, boolean _nvl) { //
		String str_value = getConfValue(_key); //取字符串值,忽略大小写!!
		if (str_value == null || str_value.trim().equals("")) { //如果为空!!!
			return _nvl; //
		}
		if ("Y".equalsIgnoreCase(str_value) || "Yes".equalsIgnoreCase(str_value) || "True".equalsIgnoreCase(str_value) || "On".equalsIgnoreCase(str_value)) { //如果是这几种情况,则是true
			return true;
		} else if ("N".equalsIgnoreCase(str_value) || "No".equalsIgnoreCase(str_value) || "False".equalsIgnoreCase(str_value) || "Off".equalsIgnoreCase(str_value)) { //如果这几种情况,则是false
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
	 * 有时需要找出一些特定的key,比如SQL1,SQL2等
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
