package cn.com.infostrategy.bs.common;

import java.io.Serializable;

/**
 * ѹ������VO,��������ѹ��,Ҳ���ܲ�ѹ��(����rar�ļ�)!
 * @author xch
 *
 */
public class CompressBytesVO implements Serializable {

	private static final long serialVersionUID = 1415549238209133362L;
	private boolean isZip = false; //�Ƿ�ѹ��
	private byte[] bytes = null; //ʵ������

	public CompressBytesVO() {
	}

	public CompressBytesVO(boolean _isZip, byte[] _bytes) {
		isZip = _isZip; //
		bytes = _bytes; //
	}

	public boolean isZip() {
		return isZip;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
