package cn.com.infostrategy.to.common;

import java.io.Serializable;

/**
 * ����̨��Ϣ����,Ϊ���ܹ�����System.out��System.err������ɫ���ֱ���ʾ�����Ա���֪��������.
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
