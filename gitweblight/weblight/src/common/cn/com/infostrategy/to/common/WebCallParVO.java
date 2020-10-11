package cn.com.infostrategy.to.common;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Web调用的参数
 * @author xch
 *
 */
public class WebCallParVO implements Serializable {

	private static final long serialVersionUID = -7352105963904336304L;

	private String callClassName = null; //

	private HashMap parsMap = null;

	/**
	 * 构造方法
	 */
	public WebCallParVO() {
	}

	public String getCallClassName() {
		return callClassName;
	}

	public void setCallClassName(String callClassName) {
		this.callClassName = callClassName;
	}

	public HashMap getParsMap() {
		return parsMap;
	}

	public void setParsMap(HashMap parsMap) {
		this.parsMap = parsMap;
	}
}
