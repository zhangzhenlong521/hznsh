package cn.com.infostrategy.bs.common;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;


import org.apache.log4j.Logger;



import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;

/**
 * 平台的定时器,每定义的一条任务,就会开启该类的一个线程实例!!!
 * 然后在该类的线程实例中,死循环处理(每隔一秒处理一回),然后每次判断一下是否满足条件,如果满足则调用真正的类!!
 * @author xch   20170411更新
 */
public class WLTJobTimer extends Thread {

	private HashVO hvo_define = null; // 任务定义
	private CommDMO commDMO = new CommDMO(); //
	private Logger logger = WLTLogger.getLogger(WLTJobTimer.class); //
	private TBUtil tbUtil = new TBUtil(); //
	private long ll_cycle_count = 0; // 循环调用的累计次数
	private String str_timing_lastmask = null; //
	private boolean doClose = false; // liuxuanfei 2011-07-20

	private static HashMap allThreadMap = new HashMap(); //

	/**
	 * 构造方法..
	 * @param _hvo
	 */
	public WLTJobTimer(HashVO _hvo) {
		this.hvo_define = _hvo; //
	}

	@Override
	/**
	 * 执行逻辑,这段逻辑每秒钟都会被调用一次!!
	 */
	public void run() {
		String str_name = hvo_define.getStringValue("name"); // 任务名...
		String str_type = hvo_define.getStringValue("triggertype", "").trim(); // 触发类型
		String str_className = hvo_define.getStringValue("classname"); // 真正的任务实现类名

		allThreadMap.put(str_name, this); //

		// 永远做死循环!!!!!很关键,这样就能保证一条任务永远在一个线程中执行,如果任务发生阻塞,则不会像Quartz一样另开线程!!!!
		// liuxuanfei 2011-07-20 给定标识, 使得终端可以控制JOB的启动和销毁!!
		while (true) { // 做死循环!
			if (doClose) {
				logger.debug("任务[" + str_name + "成功被停止了!"); //
				break;
			}
			try {
				if (str_type.equals("循环")) { // 如果是循环调度的
					ll_cycle_count = ll_cycle_count + 1; // 先将次数累加1
					Long ll_conf = hvo_define.getLognValue("triggerconf"); // 循环时就是定义一个数字，就表示多少秒
					if (ll_conf != null && ll_conf.longValue() > 0 && ll_cycle_count >= ll_conf.longValue()) { // 如果定义了,且累加的次数大于了定义的
						execJobClass(str_name, str_className); // 真正调用具体的Job.....
						ll_cycle_count = 0; // 如果任务执行发生阻塞,则是在任务执行完成后重新累计一定时间后再执行(比如5秒).
					}
				} else if (str_type.equals("定时")) { // 如果是定时调度
					String str_conf = hvo_define.getStringValue("triggerconf", "").trim(); // 比如：每天;
					String[] str_items = tbUtil.split(str_conf, ";"); //
					if (str_items[0].equals("每天")) { // 每天调用,//比如：每天;23:50;
						execDayJob(str_name, str_items[1], str_className); // 看是否到点
					} else if (str_items[0].equals("每周")) { // 每周调用,比如：每周;3;13:50;
						execWeekJob(str_name, Integer.parseInt(str_items[1]), str_items[2], str_className); // 看是否到点
					} else if (str_items[0].equals("每月")) { // 每月调用,比如：每月;10;13:50;
						execMonthJob(str_name, Integer.parseInt(str_items[1]), str_items[2], str_className); // 看是否到点
					} else if (str_items[0].equals("每年")) { // 每年调用,每年;360;13:50;
						execYearJob(str_name, Integer.parseInt(str_items[1]), str_items[2], str_className); // 看是否到点
					} else if (str_items[0].equals("每季")) { // 每季度第几个月第几天的几时调用,每季;1;2;13:50;
						execQuarterJob(str_name, Integer.parseInt(str_items[1]), Integer.parseInt(str_items[2]), str_items[3], str_className);
					} else if (str_items[0].equals("每时")) { // 每时调用,//比如：每时;50;
						execMinJob(str_name, Integer.parseInt(str_items[1]), str_className);
					} else {
						logger.error("定时任务【" + str_name + "】的参数【" + str_items[0] + "】设置不对,应该是：每天/每周/每月/每年/每季，其中的一种!"); //
					}
				} else {
					logger.debug("任务[" + str_name + "]定义了未知的触发类型[" + str_type + "],应该是[循环/定时]中的一种"); //
				}
				sleep(1000); // 每一秒循环一次
			} catch (Exception ex) {
				logger.error(null, ex); // 捕获异常!! 让Job继续跑下去!!
			}
		}
	}

	public boolean isRuning() {
		return !doClose; //
	}

	public void stopMe() {
		doClose = true; //
	}

	public static String lookJobState(String _jobName) {
		WLTJobTimer oneJob = (WLTJobTimer) allThreadMap.get(_jobName); //
		if (oneJob != null) {
			if (oneJob.isRuning()) {
				return "任务[" + _jobName + "]正在运行!"; //
			} else {
				return "任务[" + _jobName + "]已经停止!"; //
			}
		} else {
			return "获取任务[" + _jobName + "]为空,可能没有激活与启动,或被人为停止了!"; //
		}
	}

	public static String stopJob(String _jobName) {
		WLTJobTimer oneJob = (WLTJobTimer) allThreadMap.get(_jobName); //
		if (oneJob != null) {
			if (!oneJob.isRuning()) {
				return "任务[" + _jobName + "]已经停止了!"; //
			} else {
				oneJob.stopMe(); // 停止!!!
				allThreadMap.remove(_jobName); //
				return "停止任务[" + _jobName + "]成功!"; //
			}
		} else {
			return "获取任务[" + _jobName + "]为空,可能已停止了,请重新启动!"; //
		}
	}

	/**
	 * 启动一个状态
	 * @param _jobName
	 * @return
	 */
	public static String startJob(String _jobName) {
		if (!"Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISSTARTJOB"))) {
			return "因为没有在系统参数XML中定义【ISSTARTJOB】=【Y】,所以不能启动Job,请先定义参数!"; //
		}
		WLTJobTimer oneJob = (WLTJobTimer) allThreadMap.get(_jobName); //
		if (oneJob != null && oneJob.isRuning()) {
			return "任务[" + _jobName + "]正在运行,请先停止后再运行!!"; //
		}
		try { // 如果为空!或者不是运行状态
			HashVO[] hvs_jobs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_job where name ='" + _jobName + "'"); //
			if (!hvs_jobs[0].getBooleanValue("activeflag")) { // 如果不是激活状态
				return "任务[" + _jobName + "]是非激活状态,不能启动,请选修改其状态为激活!!"; //
			}
			new WLTJobTimer(hvs_jobs[0]).start(); // 启动线程
			return "启动任务[" + _jobName + "]成功!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "启动任务[" + _jobName + "]发生异常!"; //
		}
	}
	
	

	/**
	 * 每隔几分执行一次的Job!!!
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_week
	 * @param _hour
	 * @param _className
	 */
	private void execMinJob(String _jobName, Integer _min, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("dd", Locale.SIMPLIFIED_CHINESE);
		String str_currday = sdf_curr.format(currdate); // 当前分钟
		if (Integer.parseInt(str_currday)>24||Integer.parseInt(str_currday)==1) {
			try {
				execJobClass(_jobName, _className); // 真正执行具体的任务!!!!
				sleep(_min*60000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}

	/**
	 * 每天几点执行一次的Job,即每天到点会做一次!!!
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_week
	 * @param _hour
	 * @param _className
	 */
	private void execDayJob(String _jobName, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // 当前小时

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // 当前日期
		int li_year = calendar.get(Calendar.YEAR); // 年度
		int li_day_of_year = calendar.get(Calendar.DAY_OF_YEAR) + 1; // 当年中的第几天

		String str_timerMask = li_year + "年" + li_day_of_year + "天"; // 这是标记，表示2014第187天，这一天的任务已经做过了！！则没必要再做了！
		if (str_currhour.equals(_hour)) { // 如果到点了.
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // 如果已注册了该时段
				execJobClass(_jobName, _className); // 真正执行具体的任务!!!!
				str_timing_lastmask = str_timerMask;
			}
		}

	}

	/**
	 * 每周几的几点执行一次的任务
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_week
	 * @param _hour
	 * @param _className
	 */
	private void execWeekJob(String _jobName, int _day_of_week, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // 当前小时

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // 当前日期
		int li_year = calendar.get(Calendar.YEAR); // 年度
		int li_month = calendar.get(Calendar.MONTH) + 1; // 月份
		int li_day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 周期几,
		int li_week_of_month = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) + 1; // 当前月中第几周

		String str_timerMask = li_year + "年" + li_month + "月" + li_week_of_month + "周"; //
		if (li_day_of_week == _day_of_week && str_currhour.equals(_hour)) { // 如果品配上了,即既是本周的第几天,又是时间也到了,比如到了:本周星期二的8:30
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // 如果已注册了该时段
				execJobClass(_jobName, _className); // 真正执行具体的任务!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	/**
	 * 每月几号的几点执行一次的任务..
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_month
	 * @param _hour
	 */
	private void execMonthJob(String _jobName, int _day_of_month, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // 当前小时

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // 当前日期
		int li_year = calendar.get(Calendar.YEAR); // 年度
		int li_month = calendar.get(Calendar.MONTH) + 1; // 月份
		int li_day_of_month = calendar.get(Calendar.DAY_OF_MONTH) + 1; // 当前月中第几天

		String str_timerMask = li_year + "年" + li_month + "月"; //
		if (li_day_of_month == _day_of_month && str_currhour.equals(_hour)) { // 如果品配上了,即既是本周的第几天,又是时间也到了,比如到了:本周星期二的8:30
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // 如果已注册了该时段
				execJobClass(_jobName, _className); // 真正执行具体的任务!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	/**
	 * 每年几月几号的几点执行一次的任务..
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_month
	 * @param _hour
	 */
	private void execYearJob(String _jobName, int _day_of_year, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // 当前小时

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // 当前日期
		int li_year = calendar.get(Calendar.YEAR); // 年度
		int li_month = calendar.get(Calendar.MONTH) + 1; // 月份
		int li_day_of_year = calendar.get(Calendar.DAY_OF_YEAR) + 1; // 当前年中第几天

		String str_timerMask = li_year + "年" + _day_of_year + "天"; //
		if (li_day_of_year == _day_of_year && str_currhour.equals(_hour)) { // 如果品配上了,即既是本周的第几天,又是时间也到了,比如到了:本周星期二的8:30
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // 如果已注册了该时段
				execJobClass(_jobName, _className); // 真正执行具体的任务!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	/**
	 * 每季度第几个月第几天的几点执行一次的任务..
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_month
	 * @param _hour
	 */
	private void execQuarterJob(String _jobName, int _month_of_quarter, int _day_of_month, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // 当前小时

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // 当前日期
		int li_quarter;
		int li_month_quarter;
		int li_year = calendar.get(Calendar.YEAR); // 年度
		int li_month = calendar.get(Calendar.MONTH) + 1; // 月份
		switch (li_month) {
		case 1:
			li_month_quarter = 1;
			li_quarter = 1;
			break;
		case 2:
			li_month_quarter = 2;
			li_quarter = 1;
			break;
		case 3:
			li_month_quarter = 3;
			li_quarter = 1;
			break;
		case 4:
			li_month_quarter = 1;
			li_quarter = 2;
			break;
		case 5:
			li_month_quarter = 2;
			li_quarter = 2;
			break;
		case 6:
			li_month_quarter = 3;
			li_quarter = 2;
			break;
		case 7:
			li_month_quarter = 1;
			li_quarter = 3;
			break;
		case 8:
			li_month_quarter = 2;
			li_quarter = 3;
			break;
		case 9:
			li_month_quarter = 3;
			li_quarter = 3;
			break;
		case 10:
			li_month_quarter = 1;
			li_quarter = 4;
			break;
		case 11:
			li_month_quarter = 2;
			li_quarter = 4;
			break;
		default:
			li_month_quarter = 3;
			li_quarter = 4;
			break;
		}
		int li_day_of_month = calendar.get(Calendar.DAY_OF_MONTH); // 当前月中第几天

		String str_timerMask = li_year + "年" + li_quarter + "季";
		if (li_month_quarter == _month_of_quarter && li_day_of_month == _day_of_month && str_currhour.equals(_hour)) { //
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // 如果已注册了该时段
				execJobClass(_jobName, _className); // 真正执行具体的任务!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	public void closeme() {
		doClose = true;
	}

	/**
	 * 真正执行反射类定义的任务!!
	 * @param _className
	 */
	private void execJobClass(String _jobName, String _className) {
		WLTInitContext wltInitContext = new WLTInitContext(); //
		long ll_1 = System.currentTimeMillis(); //
		String str_time1 = tbUtil.getCurrTime(); //
		String str_return = null; //
		try {
			WLTJobIFC job = (WLTJobIFC) Class.forName(_className).newInstance(); //
			str_return = job.run(); // 真正的去执行实际的类
			wltInitContext.commitAllTrans(); //
		} catch (Exception ex) {
			str_return = ex.getClass().getName() + ":" + ex.getMessage(); //
			wltInitContext.rollbackAllTrans(); //
			logger.error("平台任务调试器执行任务[" + _className + "]发生异常", ex); // //
		}

		long ll_2 = System.currentTimeMillis(); //
		String str_time2 = tbUtil.getCurrTime(); //
		logger.debug("从[" + str_time1 + "]至[" + str_time2 + "]执行任务[" + _jobName + "][" + _className + "]结束,共耗时[" + (ll_2 - ll_1) + "]毫秒,执行结果=[" + str_return + "]!"); //
		try {
			String str_exechist = commDMO.getStringValueByDS(null, "select exechist from pub_job where name='" + _jobName + "'"); // 先把原来的取出来,然后在拼接,如果超长,则截掉后面的,只留前4000个字符!
			if (str_exechist == null) {
				str_exechist = "";
			}
			str_exechist = str_exechist + "[" + str_time1 + "]-[" + str_time2 + "],耗时[" + (ll_2 - ll_1) + "]毫秒,结果=[" + str_return + "];\r\n"; //
			if (str_exechist.length() > 4000) {
				str_exechist = str_exechist.substring(str_exechist.length() - 4000, str_exechist.length()); //
			}
			String str_logsql = new UpdateSQLBuilder("pub_job", "name='" + _jobName + "'", new String[][] { { "exechist", str_exechist }, { "lastexectime", str_time2 } }).getSQL(); //
			commDMO.executeUpdateByDSImmediately(null, str_logsql, false); // 回写历史!!!
		} catch (Exception ex) {
			logger.error("平台任务调试器处理任务[" + _jobName + "]回写日志时发生异常", ex); // //
		} finally {
			wltInitContext.closeAllConn(); // 关闭所有连接..
		}
	}

}
