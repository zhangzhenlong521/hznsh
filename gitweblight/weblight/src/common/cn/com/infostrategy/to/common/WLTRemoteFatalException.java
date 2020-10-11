package cn.com.infostrategy.to.common;

/**
 * Զ�������쳣!!���Ծ����ͻ����Ƿ�ֱ���˳�!!
 * @author xch
 *
 */
public class WLTRemoteFatalException extends WLTRemoteException {

	boolean isExit = false; //Ĭ���ǲ��˵�

	public WLTRemoteFatalException(String _message) {
		super(_message);
	}

	public WLTRemoteFatalException(String _message, boolean _isExit) {
		super(_message);
		isExit = _isExit; //
	}

	public boolean isExit() {
		return isExit;
	}

	public void setExit(boolean isExit) {
		this.isExit = isExit;
	}

}
