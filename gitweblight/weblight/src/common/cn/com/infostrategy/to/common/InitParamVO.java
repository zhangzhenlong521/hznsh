package cn.com.infostrategy.to.common;

import java.io.Serializable;

public class InitParamVO implements Serializable {
	private static final long serialVersionUID = -5741891370472533867L;

	private String key = null;
	private String value = null;
	private String descr = null;

	public InitParamVO() {

	}

	public InitParamVO(String _key, String _value, String _descr) {
		this.key = _key;
		this.value = _value;
		this.descr = _descr;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}
}
