package cn.com.infostrategy.ui.common;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.ConsoleMsgVO;

/**
 * �������˿���̨
 * @author xch
 *
 */
public class ClientConsolePrintStream extends PrintStream {

	private byte type = -1; // 

	public ClientConsolePrintStream(byte _type, OutputStream out) {
		super(out, true); ////
		this.type = _type; ////
	}

	@Override
	/**
	 * �����־
	 */
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len); //�Ƚ�ԭ�з�ʽԭ�ⲻ�������,������̨��Ȼ���.
		final String message = new String(buf, off, len); //ȡ���ַ���
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClientEnvironment.getClientConsoleQueue().push(new ConsoleMsgVO(type, message)); //���ڴ��˼�¼��
			}
		});
	}

	
	
	
}
