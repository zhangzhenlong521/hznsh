package cn.com.infostrategy.bs.common;

import java.net.Socket;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.WLTLogger;

public class KillMeTimerTask extends TimerTask {

	private static Logger logger = WLTLogger.getLogger(KillMeTimerTask.class);

	@Override
	public void run() {
		int li_port = 9001;
		try {
			li_port = Integer.parseInt(System.getProperty("PUSH.WEBPORT"));
			Socket socket = new Socket("127.0.0.1", li_port); //
			socket.close(); //
			logger.debug("心跳程序自检Tomcat[" + li_port + "]还活着...."); //
		} catch (Exception ex) {
			//logger.error("心跳程序自检Tomcat[" + li_port + "]已死,原因:[" + ex.getMessage() + ",系统将强行退出!"); //
			//System.exit(0); //
			ex.printStackTrace();
		}
	}

}
