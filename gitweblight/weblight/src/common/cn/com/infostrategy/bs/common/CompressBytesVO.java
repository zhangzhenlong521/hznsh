package cn.com.infostrategy.bs.common;

import java.io.Serializable;

/**
 * 压缩对象VO,即可能真压缩,也可能不压缩(比如rar文件)!
 * @author xch
 *
 */
public class CompressBytesVO implements Serializable {

	private static final long serialVersionUID = 1415549238209133362L;
	private boolean isZip = false; //是否压缩
	private byte[] bytes = null; //实际数据

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
