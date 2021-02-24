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
 * 日志文件.所有的Logger为weblightlogger的子Logger
 * @author sunxf
 *
 * 2007 12:59:04 PM
 */
public class WLTLogger {
	private static String str_logprefix = "WebLightLoggerRoot"; //
	private static org.apache.log4j.Logger rootlogger = null; //根日志
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
		if (!log_dir_file.exists()) { //如果目录不存在,立即创建!!
			log_dir_file.mkdirs(); //
		}
		log_dir = log_dir_file.getAbsolutePath();
		rootlogger = Logger.getLogger(str_logprefix); //创建Logger!!!
		initLogLevel(); //定义显示级别
		initLogAppender(); //定义显示风格
	}

	/**
	 * 定义显示级别!
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
	 * 定义显示风格,!!
	 * 1:只输出控制台不输出文件
	 * 2:只输出文件,不输出控件台
	 * 3:同时输出控制台与文件
	 */
	private static void initLogAppender() {
		try {
			rootlogger.removeAllAppenders(); //删除本日志对象的所有显示风格.
			rootlogger.getRootLogger().removeAllAppenders(); //删除根日志对象的所有显示风格,一定要处理一下,否则会莫名其妙的输出两行!!一行是带格式的,一行是不带格式的!!
			PatternLayout patternLayout = new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %p %m <%C.%M(%L)> <%t> %n"); //定义显示风格
			if (outputtype == null || outputtype.equalsIgnoreCase("1")) { //只输出至控制台
				rootlogger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT)); //输出至控制台!!!
			} else if (outputtype.equalsIgnoreCase("2")) { //只输出至文件
				Appender appender1 = new DailyRollingFileAppender(patternLayout, log_dir + System.getProperty("file.separator") + "weblight.txt", "'.'yyyy-MM-dd"); //输出至文件
				((DailyRollingFileAppender) appender1).setAppend(false);
				rootlogger.addAppender(appender1);
				System.out.println("日志输出目录:[" + log_dir + "]");
			} else if (outputtype.equalsIgnoreCase("3")) { //同时输出至控制台与文件
				rootlogger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT)); //输出至控制台!!!
				Appender appender2 = new DailyRollingFileAppender(patternLayout, log_dir + System.getProperty("file.separator") + "weblight.txt", "'.'yyyy-MM-dd"); //输出至文件
				((DailyRollingFileAppender) appender2).setAppend(false);
				rootlogger.addAppender(appender2);
				System.out.println("日志输出目录:[" + log_dir + "]");
			} else {
				rootlogger.addAppender(new ConsoleAppender(patternLayout, ConsoleAppender.SYSTEM_OUT)); //输出至控制台!!
			}
		} catch (IOException e) {
			System.out.println("初始化log4j发生异常:");
			e.printStackTrace();
		}
	}

}
