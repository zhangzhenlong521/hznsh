package cn.com.infostrategy.to.common;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * ��־�ļ�.���е�LoggerΪweblightlogger����Logger
 * @author sunxf
 *
 * 2007 12:59:04 PM
 */
public class WLTLogger {
	private static String str_logprefix = "WebLightLoggerRoot"; //
	private static org.apache.log4j.Logger rootlogger = null; //����־
	private static String contextpath = null;
	private static String loglevel = null;
	private static String outputtype = null;
	private static String log_dir = null;

	private WLTLogger() {
	}

	public static Logger getLogger() {
		if (rootlogger == null) {
			return null;
		}

		return rootlogger.getLogger(str_logprefix + ".UnknownClass");
	}

	public static Logger getLogger(Object obj) {
		return getLogger(obj.getClass());
	}

	public static Logger getLogger(Class clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String clazz) {
		if (rootlogger == null) {
			return null;
		}

		return rootlogger.getLogger(str_logprefix + "." + clazz); //
	}

	public static void config(String _contextpath, String _level, String _outputtype) {
		contextpath = _contextpath;
		loglevel = _level;
		outputtype = _outputtype;

		if (contextpath == null) {
			contextpath = "c:";
		}
		File log_dir_file = new File(contextpath + System.getProperty("file.separator") + "_weblight_log");
		if (!log_dir_file.exists()) { //���Ŀ¼������,��������!!
			log_dir_file.mkdirs(); //
		}
		log_dir = log_dir_file.getAbsolutePath();
		rootlogger = Logger.getLogger(str_logprefix); //����Logger!!!
		initLogLevel(); //������ʾ����
		initLogAppender(); //������ʾ���
	}

	/**
	 * ������ʾ����!
	 */
	private static void initLogLevel() {
		Level level = Level.INFO;
		if (loglevel == null) {
			level = Level.INFO;
		} else if (loglevel.equalsIgnoreCase("DEBUG")) {
			level = Level.DEBUG;
		} else if (loglevel.equalsIgnoreCase("INFO")) {
			level = Level.INFO;
		} else if (loglevel.equalsIgnoreCase("ERROR")) {
			level = Level.ERROR;
		} else if (loglevel.equalsIgnoreCase("FATAL")) {
			level = Level.FATAL;
		} else if (loglevel.equalsIgnoreCase("WARN")) {
			level = Level.WARN;
		} else {
			level = Level.INFO;
		}
		rootlogger.setLevel(level);
	}

	/**
	 * ������ʾ���,!!
	 * 1:ֻ�������̨������ļ�
	 * 2:ֻ����ļ�,������ؼ�̨
	 * 3:ͬʱ�������̨���ļ�
	 */
	private static void initLogAppender() {
		try {
			rootlogger.removeAllAppenders(); //ɾ������־�����������ʾ���.
			rootlogger.getRootLogger().removeAllAppenders(); //ɾ������־�����������ʾ���,һ��Ҫ����һ��,�����Ī��������������!!һ���Ǵ���ʽ��,һ���ǲ�����ʽ��!!
			PatternLayout patternLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p %m <%C.%M(%L)> <%t> %n"); //������ʾ���
			if (outputtype == null || outputtype.equalsIgnoreCase("1")) { //ֻ���������̨
				rootlogger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT)); //���������̨!!!
			} else if (outputtype.equalsIgnoreCase("2")) { //ֻ������ļ�
				Appender appender1 = new DailyRollingFileAppender(patternLayout, log_dir + System.getProperty("file.separator") + "weblight.txt", "'.'yyyy-MM-dd"); //������ļ�
				((DailyRollingFileAppender) appender1).setAppend(false);
				rootlogger.addAppender(appender1);
				System.out.println("��־���Ŀ¼:[" + log_dir + "]");
			} else if (outputtype.equalsIgnoreCase("3")) { //ͬʱ���������̨���ļ�
				rootlogger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT)); //���������̨!!!
				Appender appender2 = new DailyRollingFileAppender(patternLayout, log_dir + System.getProperty("file.separator") + "weblight.txt", "'.'yyyy-MM-dd"); //������ļ�
				((DailyRollingFileAppender) appender2).setAppend(false);
				rootlogger.addAppender(appender2);
				System.out.println("��־���Ŀ¼:[" + log_dir + "]");
			} else {
				rootlogger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT)); //���������̨!!
			}
		} catch (IOException e) {
			System.out.println("��ʼ��log4j�����쳣:");
			e.printStackTrace();
		}
	}

}
