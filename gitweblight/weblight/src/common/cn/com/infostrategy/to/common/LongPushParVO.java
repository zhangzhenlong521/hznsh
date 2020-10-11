package cn.com.infostrategy.to.common;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 服务器端推送对象
 * @author haoming
 * create by 2013-12-2
 */
public class LongPushParVO implements Serializable {
	private static final long serialVersionUID = -69624299858494163L;
	private String className;
	private String methodName;
	private Class[] parClasses = null;
	private Object[] parObjs = null;

	public Class[] getParClasses() {
		return parClasses;
	}

	public void setParClasses(Class[] parClasses) {
		this.parClasses = parClasses;
	}

	public Object[] getParObjs() {
		return parObjs;
	}

	public void setParObjs(Object[] parObjs) {
		this.parObjs = parObjs;
	}

	public LongPushParVO(String _class, String method, Class[] _parClasses, Object[] _parObjs) {
		className = _class;
		methodName = method;
		parClasses = _parClasses;
		parObjs = _parObjs;
	}

	public LongPushParVO(Class _class, Method method, Class[] _parClasses, Object[] _parObjs) {
		if (_class != null) {
			className = _class.getName();
		}
		if (method != null) {
			methodName = method.getName();
		}
		parClasses = _parClasses;
		parObjs = _parObjs;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getMothodName() {
		return methodName;
	}

	public void setMothodName(String mothodName) {
		this.methodName = mothodName;
	}
}
