package cn.com.infostrategy.ui.common;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.ConsoleMsgVO;

/**
 * 服务器端控制台
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
	 * 输出日志
	 */
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len); //先将原有方式原封不动的输出,即控制台仍然输出.
		final String message = new String(buf, off, len); //取得字符串
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				ClientEnvironment.getClientConsoleQueue().push(new ConsoleMsgVO(type, message)); //在内存了记录下
			}
		});
	}

	
	
	
}
