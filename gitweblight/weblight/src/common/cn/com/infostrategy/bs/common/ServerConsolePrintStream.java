package cn.com.infostrategy.bs.common;

import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.SwingUtilities;

/**
 * 服务器端控制台
 * @author xch
 *
 */
public class ServerConsolePrintStream extends PrintStream {

	public ServerConsolePrintStream(OutputStream out) {
		super(out, true); //
	}

	@Override
	/**
	 * 输出日志
	 */
	public void write(byte[] buf, int off, int len) {
		super.write(buf, off, len); //先将原有方式原封不动的输出,即控制台仍然输出.
		if (ServerEnvironment.isLoadRunderCall) {  //
			return;
		}

		//但问题是一量启用监控了,谁来何时关闭呢？或许可以将这个变量就直接使用当前时间的long值,然后只每次只要当前时间与这个变量在1小时内,则就认为是可以监控！这就等于变相的实现了,1小时后自动关闭了!【xch/2012-08-22】
		if (ServerEnvironment.isOutPutToQueue) { //如果需要监控!!
			final String message = new String(buf, off, len); //取得字符串
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ServerEnvironment.getServerConsoleQueue().push(message); //在内存了记录下
				}
			});
		}
	}
}
