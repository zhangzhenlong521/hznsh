package cn.com.infostrategy.bs.sysapp.cometpush;

import java.io.Serializable;

/**
 * 推送配置机制配置对象。
 * @author haoming
 * create by 2013-12-3
 */
public class PushConfigVO implements Serializable {
	private String pushtype; //推送类型
	private String fromSession; //来源回话 
	private String usercode[];// 发送给的code.
	private String toSession[]; // 要推送给的session。
	private byte [] message;
	public byte[] getMessage() {
		return message;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public PushConfigVO(String _pushtype, String _fromSession) throws Exception {
		if (_pushtype == null) {
			throw new Exception("pushtype不能为空");
		}
		if (!(ServerPushToClientUtil.PUSH_TYPE_EXCEPT_ME.equals(_pushtype) || ServerPushToClientUtil.PUSH_TYPE_ONLINE.equals(_pushtype))) {
			throw new Exception("此构造方法pushtype=" + _pushtype + "不支持.");
		}
		pushtype = _pushtype;
		fromSession = _fromSession;
	}

	public PushConfigVO(String _pushtype, String _usercode_or_sessions[], String _fromsession) throws Exception {
		pushtype = _pushtype;
		if (ServerPushToClientUtil.PUSH_TYPE_BY_SESSION.equals(_pushtype)) {
			toSession = _usercode_or_sessions;
		} else if (ServerPushToClientUtil.PUSH_TYPE_BY_CODE.equals(_pushtype)) {
			usercode = _usercode_or_sessions;
		} else {
			throw new Exception("此构造方法pushtype=" + _pushtype + "不支持.");
		}
		fromSession = _fromsession;
	}

	public String getPushtype() {
		return pushtype;
	}

	public String getFromSession() {
		return fromSession;
	}

	public String[] getUsercode() {
		return usercode;
	}

	public String[] getToSession() {
		return toSession;
	}

}
