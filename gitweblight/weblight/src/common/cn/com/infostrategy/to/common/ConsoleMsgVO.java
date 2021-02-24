package cn.com.infostrategy.to.common;

import java.io.Serializable;

/**
 * 控制台信息对象,为了能够区分System.out与System.err并用颜色来分别显示，所以必须知道其类型.
 * @author xch
 *
 */
public class ConsoleMsgVO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static byte SYSTEM_OUT = 1; //
	public static byte SYSTEM_ERROR = 2; //

	private byte type = -1;
	private String msg = null; //

	private ConsoleMsgVO() {
	}

	public ConsoleMsgVO(byte _type, String _msg) {
		type = _type; //
		msg = _msg; //
	}

	public String getMsg() {
		return msg;
	}

	public byte getType() {
		return type; //
	}

}
