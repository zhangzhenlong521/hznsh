package cn.com.infostrategy.ui.sysapp.login;

abstract public class AbstractLoginLocker {
	/**
	 * 实现逻辑
	 * @param type success、fail
	 */
	public abstract void locker(String usercode, String type);

	/**
	 * 返回信息
	 * @return
	 */
	public abstract String getMessage();
}
