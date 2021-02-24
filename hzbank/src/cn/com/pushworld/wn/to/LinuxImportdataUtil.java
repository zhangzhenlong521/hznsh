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
		System.out.println("ִ��Ч��:" + execute);

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
	 * ��¼Linuxϵͳ (����ִ��OK)
	 * 
	 * @param ip
	 *            :�����IP
	 * @param username
	 *            :�û���
	 * @param password
	 *            :����
	 * @return
	 */
	public Connection login(String ip, String username, String password) {
		boolean flag = false;
		Connection conn = null;
		try {
			conn = new Connection(ip);
			conn.connect();// ����Linux
			if (!conn.isAuthenticationComplete()) {
				synchronized (this) {
					flag = conn.authenticateWithPassword(username, password);// ��¼��֤
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
	 *            :���������������(����ִ�гɹ�)
	 * @param cmd
	 *            :ִ�е�cmd����
	 * @return
	 */
	public static String execute(Connection conn, String cmd) {
		String result = "";
		InputStream stdOut = null;
		InputStream stdErr = null;
		try {
			if (conn != null) {
				Session session = conn.openSession();// �򿪻Ự��������
				session.requestPTY("bash");
				session.startShell();
				PrintWriter out = new PrintWriter(session.getStdin());
				out.print(cmd);
				out.close();
				// session.execCommand(cmd);// ִ��Shell�ű�����
				// stdOut = new StreamGobbler(session.getStdout());
				// stdErr = new StreamGobbler(session.getStderr());
				// session.waitForCondition(ChannelCondition.EXIT_STATUS,
				// ChannelCondition.CLOSED);
				// result = "ִ������ɹ�,����conn" + conn + ",ִ�е�����:" + cmd;
				// Integer exitStatus = session.getExitStatus();
				// conn.close();
				// session.close();
			} else {
				result = "δ�������ӷ�����,���������Ϊ:" + conn;
			}
		} catch (Exception e) {
			result = "ִ������ʧ��,����Ϊ:" + conn + ",ִ�е�����:" + cmd;
			e.printStackTrace();
		} finally {
			closeAll(conn, null);
		}
		return result;
	}

	/**
	 * ��ȡ����һ�ε����еĽ�ֹ����(���Գɹ�,��������ʹ��)
	 * 
	 * @return
	 */
	public static String getLastImport1() {
		String result = "";
		try {
			Connection login = getInstance().login("192.168.1.25", "oracle",
					"ABCabc@123");// ��¼Linuxϵͳ
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
	 * ��ȡ��ָ�����ں�һ��(���Գɹ�����������ʹ��)
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
	 * ��ȡ����ǰ����ǰһ�������(���Գɹ�������ʹ��)
	 * 
	 * @param date
	 *            :��������ڸ�ʽΪ(20190101)
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
	 * ���㵼�����ݵĴ�С(ȫ�����������) ���Գɹ�����������ʹ��
	 * 
	 * @return
	 */
	public static long getImportLength() {
		Long importLength = 0L;
		Connection login = null;
		try {
			login = getInstance().login("192.168.1.25", "oracle", "ABCabc@123");// ��¼Linuxϵͳ
			SFTPv3Client sft = new SFTPv3Client(login);
			String remotePath = "/home/oracle/wnData/";
			Vector ls = sft.ls(remotePath);
			String lastImport = getLastImport1();// ��ȡ����һ�ε�����������
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
	 * ���㵼��һ����ļ��Ĵ�С�����Գɹ���
	 * 
	 * @param date
	 *            :���ڸ�ʽ(��ʽ:20190101)
	 * @return
	 */
	public static long getImportLength(String date) {
		long importLength = 0L;
		Connection login = null;
		try {
			login = getInstance().login("192.168.1.25", "oracle", "ABCabc@123");// ��¼Linuxϵͳ
			SFTPv3Client sft = new SFTPv3Client(login);
			String remotePath = "/home/oracle/wnData/";
			// Session session = login.openSession();
			// String cmd="sh /home/oracle/wntest2.sh";
			// session.execCommand(cmd);//��ʱ�������ʽ��ѹ�ļ�

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
