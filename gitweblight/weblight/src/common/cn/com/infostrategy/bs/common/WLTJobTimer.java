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
 * ƽ̨�Ķ�ʱ��,ÿ�����һ������,�ͻῪ�������һ���߳�ʵ��!!!
 * Ȼ���ڸ�����߳�ʵ����,��ѭ������(ÿ��һ�봦��һ��),Ȼ��ÿ���ж�һ���Ƿ���������,��������������������!!
 * @author xch   20170411����
 */
public class WLTJobTimer extends Thread {

	private HashVO hvo_define = null; // ������
	private CommDMO commDMO = new CommDMO(); //
	private Logger logger = WLTLogger.getLogger(WLTJobTimer.class); //
	private TBUtil tbUtil = new TBUtil(); //
	private long ll_cycle_count = 0; // ѭ�����õ��ۼƴ���
	private String str_timing_lastmask = null; //
	private boolean doClose = false; // liuxuanfei 2011-07-20

	private static HashMap allThreadMap = new HashMap(); //

	/**
	 * ���췽��..
	 * @param _hvo
	 */
	public WLTJobTimer(HashVO _hvo) {
		this.hvo_define = _hvo; //
	}

	@Override
	/**
	 * ִ���߼�,����߼�ÿ���Ӷ��ᱻ����һ��!!
	 */
	public void run() {
		String str_name = hvo_define.getStringValue("name"); // ������...
		String str_type = hvo_define.getStringValue("triggertype", "").trim(); // ��������
		String str_className = hvo_define.getStringValue("classname"); // ����������ʵ������

		allThreadMap.put(str_name, this); //

		// ��Զ����ѭ��!!!!!�ܹؼ�,�������ܱ�֤һ��������Զ��һ���߳���ִ��,�������������,�򲻻���Quartzһ�����߳�!!!!
		// liuxuanfei 2011-07-20 ������ʶ, ʹ���ն˿��Կ���JOB������������!!
		while (true) { // ����ѭ��!
			if (doClose) {
				logger.debug("����[" + str_name + "�ɹ���ֹͣ��!"); //
				break;
			}
			try {
				if (str_type.equals("ѭ��")) { // �����ѭ�����ȵ�
					ll_cycle_count = ll_cycle_count + 1; // �Ƚ������ۼ�1
					Long ll_conf = hvo_define.getLognValue("triggerconf"); // ѭ��ʱ���Ƕ���һ�����֣��ͱ�ʾ������
					if (ll_conf != null && ll_conf.longValue() > 0 && ll_cycle_count >= ll_conf.longValue()) { // ���������,���ۼӵĴ��������˶����
						execJobClass(str_name, str_className); // �������þ����Job.....
						ll_cycle_count = 0; // �������ִ�з�������,����������ִ����ɺ������ۼ�һ��ʱ�����ִ��(����5��).
					}
				} else if (str_type.equals("��ʱ")) { // ����Ƕ�ʱ����
					String str_conf = hvo_define.getStringValue("triggerconf", "").trim(); // ���磺ÿ��;
					String[] str_items = tbUtil.split(str_conf, ";"); //
					if (str_items[0].equals("ÿ��")) { // ÿ�����,//���磺ÿ��;23:50;
						execDayJob(str_name, str_items[1], str_className); // ���Ƿ񵽵�
					} else if (str_items[0].equals("ÿ��")) { // ÿ�ܵ���,���磺ÿ��;3;13:50;
						execWeekJob(str_name, Integer.parseInt(str_items[1]), str_items[2], str_className); // ���Ƿ񵽵�
					} else if (str_items[0].equals("ÿ��")) { // ÿ�µ���,���磺ÿ��;10;13:50;
						execMonthJob(str_name, Integer.parseInt(str_items[1]), str_items[2], str_className); // ���Ƿ񵽵�
					} else if (str_items[0].equals("ÿ��")) { // ÿ�����,ÿ��;360;13:50;
						execYearJob(str_name, Integer.parseInt(str_items[1]), str_items[2], str_className); // ���Ƿ񵽵�
					} else if (str_items[0].equals("ÿ��")) { // ÿ���ȵڼ����µڼ���ļ�ʱ����,ÿ��;1;2;13:50;
						execQuarterJob(str_name, Integer.parseInt(str_items[1]), Integer.parseInt(str_items[2]), str_items[3], str_className);
					} else if (str_items[0].equals("ÿʱ")) { // ÿʱ����,//���磺ÿʱ;50;
						execMinJob(str_name, Integer.parseInt(str_items[1]), str_className);
					} else {
						logger.error("��ʱ����" + str_name + "���Ĳ�����" + str_items[0] + "�����ò���,Ӧ���ǣ�ÿ��/ÿ��/ÿ��/ÿ��/ÿ�������е�һ��!"); //
					}
				} else {
					logger.debug("����[" + str_name + "]������δ֪�Ĵ�������[" + str_type + "],Ӧ����[ѭ��/��ʱ]�е�һ��"); //
				}
				sleep(1000); // ÿһ��ѭ��һ��
			} catch (Exception ex) {
				logger.error(null, ex); // �����쳣!! ��Job��������ȥ!!
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
				return "����[" + _jobName + "]��������!"; //
			} else {
				return "����[" + _jobName + "]�Ѿ�ֹͣ!"; //
			}
		} else {
			return "��ȡ����[" + _jobName + "]Ϊ��,����û�м���������,����Ϊֹͣ��!"; //
		}
	}

	public static String stopJob(String _jobName) {
		WLTJobTimer oneJob = (WLTJobTimer) allThreadMap.get(_jobName); //
		if (oneJob != null) {
			if (!oneJob.isRuning()) {
				return "����[" + _jobName + "]�Ѿ�ֹͣ��!"; //
			} else {
				oneJob.stopMe(); // ֹͣ!!!
				allThreadMap.remove(_jobName); //
				return "ֹͣ����[" + _jobName + "]�ɹ�!"; //
			}
		} else {
			return "��ȡ����[" + _jobName + "]Ϊ��,������ֹͣ��,����������!"; //
		}
	}

	/**
	 * ����һ��״̬
	 * @param _jobName
	 * @return
	 */
	public static String startJob(String _jobName) {
		if (!"Y".equalsIgnoreCase(ServerEnvironment.getProperty("ISSTARTJOB"))) {
			return "��Ϊû����ϵͳ����XML�ж��塾ISSTARTJOB��=��Y��,���Բ�������Job,���ȶ������!"; //
		}
		WLTJobTimer oneJob = (WLTJobTimer) allThreadMap.get(_jobName); //
		if (oneJob != null && oneJob.isRuning()) {
			return "����[" + _jobName + "]��������,����ֹͣ��������!!"; //
		}
		try { // ���Ϊ��!���߲�������״̬
			HashVO[] hvs_jobs = new CommDMO().getHashVoArrayByDS(null, "select * from pub_job where name ='" + _jobName + "'"); //
			if (!hvs_jobs[0].getBooleanValue("activeflag")) { // ������Ǽ���״̬
				return "����[" + _jobName + "]�ǷǼ���״̬,��������,��ѡ�޸���״̬Ϊ����!!"; //
			}
			new WLTJobTimer(hvs_jobs[0]).start(); // �����߳�
			return "��������[" + _jobName + "]�ɹ�!"; //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return "��������[" + _jobName + "]�����쳣!"; //
		}
	}
	
	

	/**
	 * ÿ������ִ��һ�ε�Job!!!
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_week
	 * @param _hour
	 * @param _className
	 */
	private void execMinJob(String _jobName, Integer _min, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("dd", Locale.SIMPLIFIED_CHINESE);
		String str_currday = sdf_curr.format(currdate); // ��ǰ����
		if (Integer.parseInt(str_currday)>24||Integer.parseInt(str_currday)==1) {
			try {
				execJobClass(_jobName, _className); // ����ִ�о��������!!!!
				sleep(_min*60000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}

	/**
	 * ÿ�켸��ִ��һ�ε�Job,��ÿ�쵽�����һ��!!!
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_week
	 * @param _hour
	 * @param _className
	 */
	private void execDayJob(String _jobName, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // ��ǰСʱ

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // ��ǰ����
		int li_year = calendar.get(Calendar.YEAR); // ���
		int li_day_of_year = calendar.get(Calendar.DAY_OF_YEAR) + 1; // �����еĵڼ���

		String str_timerMask = li_year + "��" + li_day_of_year + "��"; // ���Ǳ�ǣ���ʾ2014��187�죬��һ��������Ѿ������ˣ�����û��Ҫ�����ˣ�
		if (str_currhour.equals(_hour)) { // ���������.
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // �����ע���˸�ʱ��
				execJobClass(_jobName, _className); // ����ִ�о��������!!!!
				str_timing_lastmask = str_timerMask;
			}
		}

	}

	/**
	 * ÿ�ܼ��ļ���ִ��һ�ε�����
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_week
	 * @param _hour
	 * @param _className
	 */
	private void execWeekJob(String _jobName, int _day_of_week, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // ��ǰСʱ

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // ��ǰ����
		int li_year = calendar.get(Calendar.YEAR); // ���
		int li_month = calendar.get(Calendar.MONTH) + 1; // �·�
		int li_day_of_week = calendar.get(Calendar.DAY_OF_WEEK) - 1; // ���ڼ�,
		int li_week_of_month = calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH) + 1; // ��ǰ���еڼ���

		String str_timerMask = li_year + "��" + li_month + "��" + li_week_of_month + "��"; //
		if (li_day_of_week == _day_of_week && str_currhour.equals(_hour)) { // ���Ʒ������,�����Ǳ��ܵĵڼ���,����ʱ��Ҳ����,���絽��:�������ڶ���8:30
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // �����ע���˸�ʱ��
				execJobClass(_jobName, _className); // ����ִ�о��������!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	/**
	 * ÿ�¼��ŵļ���ִ��һ�ε�����..
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_month
	 * @param _hour
	 */
	private void execMonthJob(String _jobName, int _day_of_month, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // ��ǰСʱ

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // ��ǰ����
		int li_year = calendar.get(Calendar.YEAR); // ���
		int li_month = calendar.get(Calendar.MONTH) + 1; // �·�
		int li_day_of_month = calendar.get(Calendar.DAY_OF_MONTH) + 1; // ��ǰ���еڼ���

		String str_timerMask = li_year + "��" + li_month + "��"; //
		if (li_day_of_month == _day_of_month && str_currhour.equals(_hour)) { // ���Ʒ������,�����Ǳ��ܵĵڼ���,����ʱ��Ҳ����,���絽��:�������ڶ���8:30
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // �����ע���˸�ʱ��
				execJobClass(_jobName, _className); // ����ִ�о��������!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	/**
	 * ÿ�꼸�¼��ŵļ���ִ��һ�ε�����..
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_month
	 * @param _hour
	 */
	private void execYearJob(String _jobName, int _day_of_year, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // ��ǰСʱ

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // ��ǰ����
		int li_year = calendar.get(Calendar.YEAR); // ���
		int li_month = calendar.get(Calendar.MONTH) + 1; // �·�
		int li_day_of_year = calendar.get(Calendar.DAY_OF_YEAR) + 1; // ��ǰ���еڼ���

		String str_timerMask = li_year + "��" + _day_of_year + "��"; //
		if (li_day_of_year == _day_of_year && str_currhour.equals(_hour)) { // ���Ʒ������,�����Ǳ��ܵĵڼ���,����ʱ��Ҳ����,���絽��:�������ڶ���8:30
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // �����ע���˸�ʱ��
				execJobClass(_jobName, _className); // ����ִ�о��������!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	/**
	 * ÿ���ȵڼ����µڼ���ļ���ִ��һ�ε�����..
	 * @param _vmJob
	 * @param _jobName
	 * @param _day_of_month
	 * @param _hour
	 */
	private void execQuarterJob(String _jobName, int _month_of_quarter, int _day_of_month, String _hour, String _className) {
		Date currdate = new Date(System.currentTimeMillis()); //
		SimpleDateFormat sdf_curr = new SimpleDateFormat("HH:mm", Locale.SIMPLIFIED_CHINESE);
		String str_currhour = sdf_curr.format(currdate); // ��ǰСʱ

		Calendar calendar = new GregorianCalendar(); //
		calendar.setTime(currdate); // ��ǰ����
		int li_quarter;
		int li_month_quarter;
		int li_year = calendar.get(Calendar.YEAR); // ���
		int li_month = calendar.get(Calendar.MONTH) + 1; // �·�
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
		int li_day_of_month = calendar.get(Calendar.DAY_OF_MONTH); // ��ǰ���еڼ���

		String str_timerMask = li_year + "��" + li_quarter + "��";
		if (li_month_quarter == _month_of_quarter && li_day_of_month == _day_of_month && str_currhour.equals(_hour)) { //
			if (str_timing_lastmask == null || (!str_timing_lastmask.equals(str_timerMask))) { // �����ע���˸�ʱ��
				execJobClass(_jobName, _className); // ����ִ�о��������!!!!
				str_timing_lastmask = str_timerMask; //
			}
		}
	}

	public void closeme() {
		doClose = true;
	}

	/**
	 * ����ִ�з����ඨ�������!!
	 * @param _className
	 */
	private void execJobClass(String _jobName, String _className) {
		WLTInitContext wltInitContext = new WLTInitContext(); //
		long ll_1 = System.currentTimeMillis(); //
		String str_time1 = tbUtil.getCurrTime(); //
		String str_return = null; //
		try {
			WLTJobIFC job = (WLTJobIFC) Class.forName(_className).newInstance(); //
			str_return = job.run(); // ������ȥִ��ʵ�ʵ���
			wltInitContext.commitAllTrans(); //
		} catch (Exception ex) {
			str_return = ex.getClass().getName() + ":" + ex.getMessage(); //
			wltInitContext.rollbackAllTrans(); //
			logger.error("ƽ̨���������ִ������[" + _className + "]�����쳣", ex); // //
		}

		long ll_2 = System.currentTimeMillis(); //
		String str_time2 = tbUtil.getCurrTime(); //
		logger.debug("��[" + str_time1 + "]��[" + str_time2 + "]ִ������[" + _jobName + "][" + _className + "]����,����ʱ[" + (ll_2 - ll_1) + "]����,ִ�н��=[" + str_return + "]!"); //
		try {
			String str_exechist = commDMO.getStringValueByDS(null, "select exechist from pub_job where name='" + _jobName + "'"); // �Ȱ�ԭ����ȡ����,Ȼ����ƴ��,�������,��ص������,ֻ��ǰ4000���ַ�!
			if (str_exechist == null) {
				str_exechist = "";
			}
			str_exechist = str_exechist + "[" + str_time1 + "]-[" + str_time2 + "],��ʱ[" + (ll_2 - ll_1) + "]����,���=[" + str_return + "];\r\n"; //
			if (str_exechist.length() > 4000) {
				str_exechist = str_exechist.substring(str_exechist.length() - 4000, str_exechist.length()); //
			}
			String str_logsql = new UpdateSQLBuilder("pub_job", "name='" + _jobName + "'", new String[][] { { "exechist", str_exechist }, { "lastexectime", str_time2 } }).getSQL(); //
			commDMO.executeUpdateByDSImmediately(null, str_logsql, false); // ��д��ʷ!!!
		} catch (Exception ex) {
			logger.error("ƽ̨�����������������[" + _jobName + "]��д��־ʱ�����쳣", ex); // //
		} finally {
			wltInitContext.closeAllConn(); // �ر���������..
		}
	}

}
