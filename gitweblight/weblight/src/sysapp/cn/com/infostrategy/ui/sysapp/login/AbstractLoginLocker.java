package cn.com.infostrategy.ui.sysapp.login;

abstract public class AbstractLoginLocker {
	/**
	 * ʵ���߼�
	 * @param type success��fail
	 */
	public abstract void locker(String usercode, String type);

	/**
	 * ������Ϣ
	 * @return
	 */
	public abstract String getMessage();
}
