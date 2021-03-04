package cn.com.infostrategy.bs.common;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

/**
 * �������˿���̨
 * @author xch
 *
 */
public class ServerConsolePrintStream extends PrintStream {

	public ServerConsolePrintStream(OutputStream out) {
		super(out, true); //
	}

	@Override
	/**
	 * �����־
	 */
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len); //�Ƚ�ԭ�з�ʽԭ�ⲻ�������,������̨��Ȼ���.
		if (ServerEnvironment.isLoadRunderCall) {  //
			return;
		}

		//��������һ�����ü����,˭����ʱ�ر��أ�������Խ����������ֱ��ʹ�õ�ǰʱ���longֵ,Ȼ��ֻÿ��ֻҪ��ǰʱ�������������1Сʱ��,�����Ϊ�ǿ��Լ�أ���͵��ڱ����ʵ����,1Сʱ���Զ��ر���!��xch/2012-08-22��
		if (ServerEnvironment.isOutPutToQueue) { //�����Ҫ���!!
			final String message = new String(buf, off, len); //ȡ���ַ���
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ServerEnvironment.getServerConsoleQueue().push(message); //���ڴ��˼�¼��
				}
			});
		}
	}
}
