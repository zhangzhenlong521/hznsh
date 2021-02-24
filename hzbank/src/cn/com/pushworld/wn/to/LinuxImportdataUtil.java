package cn.com.pushworld.wn.to;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import ch.ethz.ssh2.ChannelCondition;
import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.SFTPv3DirectoryEntry;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class LinuxImportdataUtil {

	public static void main(String[] args) throws IOException {

		Connection login = getInstance().login("192.168.1.25", "oracle",
				"ABCabc@123");
		Session openSession = login.openSession();
		String execute = getInstance().execute(login,
				"java -jar /home/oracle/wnImportTest.jar");
		System.out.println("执行效果:" + execute);

	}

	private LinuxImportdataUtil() {
	}

	private volatile static LinuxImportdataUtil linuxUtil;

	public static LinuxImportdataUtil getInstance() {
		if (linuxUtil == null) {
			synchronized (LinuxImportdataUtil.class) {
				if (linuxUtil == null) {
					linuxUtil = new LinuxImportdataUtil();
				}
			}
		}
		return linuxUtil;
	}

	/**
	 * 登录Linux系统 (功能执行OK)
	 * 
	 * @param ip
	 *            :虚拟机IP
	 * @param username
	 *            :用户名
	 * @param password
	 *            :密码
	 * @return
	 */
	public Connection login(String ip, String username, String password) {
		boolean flag = false;
		Connection conn = null;
		try {
			conn = new Connection(ip);
			conn.connect();// 连接Linux
			if (!conn.isAuthenticationComplete()) {
				synchronized (this) {
					flag = conn.authenticateWithPassword(username, password);// 登录认证
					if (flag) {
						return conn;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	/**
	 * 
	 * @param conn
	 *            :和虚拟机建立连接(测试执行成功)
	 * @param cmd
	 *            :执行的cmd命令
	 * @return
	 */
	public static String execute(Connection conn, String cmd) {
		String result = "";
		InputStream stdOut = null;
		InputStream stdErr = null;
		try {
			if (conn != null) {
				Session session = conn.openSession();// 打开会话建立连接
				session.requestPTY("bash");
				session.startShell();
				PrintWriter out = new PrintWriter(session.getStdin());
				out.print(cmd);
				out.close();
				// session.execCommand(cmd);// 执行Shell脚本命令
				// stdOut = new StreamGobbler(session.getStdout());
				// stdErr = new StreamGobbler(session.getStderr());
				// session.waitForCondition(ChannelCondition.EXIT_STATUS,
				// ChannelCondition.CLOSED);
				// result = "执行命令成功,链接conn" + conn + ",执行的命令:" + cmd;
				// Integer exitStatus = session.getExitStatus();
				// conn.close();
				// session.close();
			} else {
				result = "未正常连接服务器,错误的连接为:" + conn;
			}
		} catch (Exception e) {
			result = "执行命令失败,链接为:" + conn + ",执行的命令:" + cmd;
			e.printStackTrace();
		} finally {
			closeAll(conn, null);
		}
		return result;
	}

	/**
	 * 获取到上一次导入中的截止日期(测试成功,可以正常使用)
	 * 
	 * @return
	 */
	public static String getLastImport1() {
		String result = "";
		try {
			Connection login = getInstance().login("192.168.1.25", "oracle",
					"ABCabc@123");// 登录Linux系统
			SFTPv3Client sft = new SFTPv3Client(login);
			String remotePath = "/home/oracle/wnCtlLog/";
			Vector ls = sft.ls(remotePath);
			SFTPv3DirectoryEntry s = new SFTPv3DirectoryEntry();
			s = (SFTPv3DirectoryEntry) ls.get(ls.size() - 1);
			result = s.filename;
			closeAll(login, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 获取道指定日期后一天(测试成功，可以正常使用)
	 * 
	 * @param date
	 *            (20190101)
	 * @return
	 */
	public static String getNextDay(String date) {
		String nextDate = "";
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date d = format.parse(date);
			cal.setTime(d);
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day + 1);
			nextDate = format.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return nextDate;
	}

	/**
	 * 获取到当前日期前一天的数据(测试成功，可以使用)
	 * 
	 * @param date
	 *            :传入的日期格式为(20190101)
	 * @return
	 */
	public static String getPreDay(String date) {
		String preDate = "";
		try {
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			Date d = format.parse(date);
			cal.setTime(d);
			int day = cal.get(Calendar.DATE);
			cal.set(Calendar.DATE, day - 1);
			preDate = format.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return preDate;
	}

	/**
	 * 计算导入数据的大小(全量导入情况下) 测试成功，可以正常使用
	 * 
	 * @return
	 */
	public static long getImportLength() {
		Long importLength = 0L;
		Connection login = null;
		try {
			login = getInstance().login("192.168.1.25", "oracle", "ABCabc@123");// 登录Linux系统
			SFTPv3Client sft = new SFTPv3Client(login);
			String remotePath = "/home/oracle/wnData/";
			Vector ls = sft.ls(remotePath);
			String lastImport = getLastImport1();// 获取到上一次导入最后的日期
			for (int i = 0; i < ls.size(); i++) {
				SFTPv3DirectoryEntry s = new SFTPv3DirectoryEntry();
				s = (SFTPv3DirectoryEntry) ls.get(i);
				String fileName = s.filename;
				if (fileName.compareTo(lastImport) < 1) {
					continue;
				}
				importLength += s.attributes.size;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			closeAll(login, null);
		}
		return importLength;
	}

	/**
	 * 计算导入一天的文件的大小（测试成功）
	 * 
	 * @param date
	 *            :日期格式(格式:20190101)
	 * @return
	 */
	public static long getImportLength(String date) {
		long importLength = 0L;
		Connection login = null;
		try {
			login = getInstance().login("192.168.1.25", "oracle", "ABCabc@123");// 登录Linux系统
			SFTPv3Client sft = new SFTPv3Client(login);
			String remotePath = "/home/oracle/wnData/";
			// Session session = login.openSession();
			// String cmd="sh /home/oracle/wntest2.sh";
			// session.execCommand(cmd);//暂时用这个方式解压文件

			Vector ls = sft.ls(remotePath);
			for (int i = 0; i < ls.size(); i++) {
				SFTPv3DirectoryEntry s = new SFTPv3DirectoryEntry();
				s = (SFTPv3DirectoryEntry) ls.get(i);
				String fileName = s.filename;
				if (fileName.equals(date)) {
					System.out.println(fileName);
					importLength = s.attributes.size;
				}
			}
			closeAll(login, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			login.close();
		}
		return importLength;
	}

	public static void closeAll(Connection conn, Session session) {
		try {
			if (conn != null) {
				conn.close();
			}
			if (session != null) {
				session.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn = null;
			session = null;
		}
	}

}
