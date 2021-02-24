package cn.com.infostrategy.to.common;

/**
 * 远程严重异常!!可以决定客户端是否直接退出!!
 * @author xch
 *
 */
public class WLTRemoteFatalException extends WLTRemoteException {

	boolean isExit = false; //默认是不退的

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
