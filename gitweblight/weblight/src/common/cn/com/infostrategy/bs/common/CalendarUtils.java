package cn.com.infostrategy.bs.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CalendarUtils {

	private static String defaultTimeZone = "GMT+8";
	public static SimpleDateFormat DATE_AND_TIME_STRIPING_FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	public static SimpleDateFormat DATE_AND_HOUR_MINUTE_STRIPING_FORMATER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm");

	public static SimpleDateFormat YEAR_MONTH_DAY_STRIPING_FORMATER = new SimpleDateFormat("yyyy-MM-dd");
	
	public static SimpleDateFormat YEAR_MONTH_STRIPING_FORMATER = new SimpleDateFormat("yyyy-MM");
	
	public static SimpleDateFormat CHINA_YEAR_MONTH_DAY_STRIPING_FORMATER = new SimpleDateFormat("yyyy年MM月dd日");

	public static SimpleDateFormat YEAR_M_D_STRIPING_FORMATER = new SimpleDateFormat(
			"yyyy-M-d");

	public static SimpleDateFormat YEAR_MONTH_DAY_BACKSLANT_SEPARATED_FORMATER = new SimpleDateFormat(
			"yyyy\\MM\\dd");
	
	public static SimpleDateFormat YEAR = new SimpleDateFormat(
			"yyyy");

	public static SimpleDateFormat YEAR_MONTH_DAY_SLANT_SEPARTED_FORMATER = new SimpleDateFormat(
			"yyyy/MM/dd");

	public static SimpleDateFormat DATE_AND_TIME_SLANT_SEPARTED_FORMATER = new SimpleDateFormat(
			"yyyy/MM/dd HH:mm:ss");

	public static SimpleDateFormat MONTH_DAY_FORWARD_SEPARTED_FORMATER = new SimpleDateFormat(
			"MM/dd");

	public static SimpleDateFormat MONTH_DAY_FORMATER = new SimpleDateFormat(
			"MM-dd");

	public static SimpleDateFormat MONTH_SEPARTED_FORMATER = new SimpleDateFormat(
			"MM");

	public static SimpleDateFormat YEARE_MONTH_DAY_NO_SEPARTED_FORMATER = new SimpleDateFormat(
			"yyyyMMdd");
	
	public static SimpleDateFormat YEARE_MONTH_NO_SEPARTED_FORMATER = new SimpleDateFormat(
			"yyyyMM");

	public static SimpleDateFormat DATE_AND_TIME_NO_SEPARTED_FORMATER = new SimpleDateFormat(
			"yyyyMMddHHmmss");
	
	public static SimpleDateFormat TIME_NO_SEPARTED_FORMATER = new SimpleDateFormat(
			"HHmmss");

	public static SimpleDateFormat DATE_AND_TIME_SPACE_SEPARTED_FORMATER = new SimpleDateFormat(
			"yyyyMMdd HHmmss");

	public static SimpleDateFormat HOUR_FORMATER = new SimpleDateFormat("HH");

	public static SimpleDateFormat DAY_FORMATER = new SimpleDateFormat("dd");

	/**
	 * DAY_OF_MILLIS:(每天的毫秒数)
	 */
	public static Integer DAY_OF_MILLIS = 1000 * 60 * 60 * 24;

	public static String DAY_NAMES[] = { "Sunday", "Monday", "Tuesday",
			"Wednesday", "Thursday", "Firday", "SaturDay" };

	public synchronized static String formatDateAndTime(Date date) {
		return DATE_AND_TIME_STRIPING_FORMATER.format(date);
	}

	public synchronized static Calendar dateStringToCalendar(String calendar) {
		try {
			Date date = DATE_AND_TIME_STRIPING_FORMATER.parse(calendar);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			return c;
		} catch (ParseException e) {
			return null;
		}
	}

	public synchronized static String formatMonthDay(Date date) {
		return MONTH_DAY_FORWARD_SEPARTED_FORMATER.format(date);
	}

	public synchronized static String formatMonth(Date date) {
		return MONTH_SEPARTED_FORMATER.format(date);
	}

	public synchronized static String formatYearMonthDay(Date date) {
		return YEAR_MONTH_DAY_STRIPING_FORMATER.format(date);
	}
	
	public synchronized static Calendar formatYearMonthDayToDate(String dateStr) {
		try {
			Calendar cal = Calendar.getInstance();
			Date date = YEAR_MONTH_DAY_STRIPING_FORMATER.parse(dateStr);
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized static String formatChinaYearMonthDay(Date date) {
		return CHINA_YEAR_MONTH_DAY_STRIPING_FORMATER.format(date);
	}
	
	public synchronized static Calendar formatChinaYearMonthDayToDate(String dateStr) {
		try {
			Calendar cal = Calendar.getInstance();
			Date date = CHINA_YEAR_MONTH_DAY_STRIPING_FORMATER.parse(dateStr);
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public synchronized static String formatYearMonth(Date date) {
		return YEAR_MONTH_STRIPING_FORMATER.format(date);
	}
	
	public synchronized static Calendar formatYearMonthToDate(String dateStr) {
		try {
			Calendar cal = Calendar.getInstance();
			Date date = YEAR_MONTH_STRIPING_FORMATER.parse(dateStr);
			cal.setTime(date);
			return cal;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized static String formatYearMonthDaySlashSeparated(Date date) {
		return YEAR_MONTH_DAY_BACKSLANT_SEPARATED_FORMATER.format(date);
	}
	
	public synchronized static String formatYear(Date date) {
		return YEAR.format(date);
	}

	public synchronized static String formatYearMonthDayForwardSlashSeparated(
			Date date) {
		return YEAR_MONTH_DAY_SLANT_SEPARTED_FORMATER.format(date);
	}
	
	public synchronized static String formatTimeNoSepartedFormater(
			Date date) {
		return TIME_NO_SEPARTED_FORMATER.format(date);
	}

	public synchronized static String formatYearMonthDayNoSeparated(Date date) {
		return YEARE_MONTH_DAY_NO_SEPARTED_FORMATER.format(date);
	}
	
	public synchronized static Calendar formatYearMonthDayNoSeparated(
			String calendar) {
		try {
			Date date = YEARE_MONTH_DAY_NO_SEPARTED_FORMATER.parse(calendar);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			return c;
		} catch (ParseException e) {
			return null;
		}
	}
	
	public synchronized static String formatYearMonthNoSeparated(Date date) {
		return YEARE_MONTH_NO_SEPARTED_FORMATER.format(date);
	}
	
	public synchronized static Calendar formatYearMonthNoSeparated(
			String calendar) {
		try {
			Date date = YEARE_MONTH_NO_SEPARTED_FORMATER.parse(calendar);
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			return c;
		} catch (ParseException e) {
			return null;
		}
	}

	public synchronized static String formatYearMonthDayHourMinute(Date date) {
		return DATE_AND_HOUR_MINUTE_STRIPING_FORMATER.format(date);
	}

	public synchronized static String formatYearMonthDayHourMinute(long timeLong) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(timeLong);
		return DATE_AND_HOUR_MINUTE_STRIPING_FORMATER.format(c.getTime());
	}

	public synchronized static String formaterHour(Date date) {
		return HOUR_FORMATER.format(date);
	}

	public synchronized static String formaterDay(Date date) {
		return DAY_FORMATER.format(date);
	}

	public synchronized static String formaterDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		String week_day = DAY_NAMES[day - 1];
		return week_day;
	}

	public synchronized static Calendar createStringToCalendar(
			String paramString, TimeZone timeZone) {

		if ((paramString != null) && (paramString.length() >= 8)) {
			Date date = null;
			Calendar localCalendar = null;
			try {
				if (paramString.indexOf("/") > 0) {
					if (timeZone == null) {
						YEAR_MONTH_DAY_SLANT_SEPARTED_FORMATER
								.setTimeZone(TimeZone
										.getTimeZone(defaultTimeZone));
					} else {
						YEAR_MONTH_DAY_SLANT_SEPARTED_FORMATER
								.setTimeZone(timeZone);
					}
					date = YEAR_MONTH_DAY_SLANT_SEPARTED_FORMATER
							.parse(paramString);
				} else if (paramString.indexOf("-") > 0) {
					if (timeZone == null) {
						YEAR_MONTH_DAY_STRIPING_FORMATER.setTimeZone(TimeZone
								.getTimeZone(defaultTimeZone));
					} else {
						YEAR_MONTH_DAY_STRIPING_FORMATER.setTimeZone(timeZone);
					}
					date = YEAR_MONTH_DAY_STRIPING_FORMATER.parse(paramString);
				}

				localCalendar = Calendar.getInstance();
				localCalendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return localCalendar;
		}
		return null;
	}

	public synchronized static void setMaxTime(Calendar calendar, Integer hour,
			Integer minute, Integer second) {
		if (null != calendar) {
			calendar.set(calendar.get(Calendar.YEAR), calendar
					.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
					hour, minute, second);
		}
	}

	public synchronized static String createCalendarToString(
			Calendar paramCalendar, TimeZone timeZone) {
		if (paramCalendar != null) {
			if (timeZone == null) {
				YEARE_MONTH_DAY_NO_SEPARTED_FORMATER.setTimeZone(TimeZone
						.getTimeZone(defaultTimeZone));
			} else {
				YEARE_MONTH_DAY_NO_SEPARTED_FORMATER.setTimeZone(timeZone);
			}
			return YEARE_MONTH_DAY_NO_SEPARTED_FORMATER.format(paramCalendar
					.getTime());
		}
		return null;
	}

	public synchronized static String createCalendarToDateTimeString(
			Calendar paramCalendar, TimeZone timeZone) {
		if (paramCalendar != null) {
			if (timeZone == null) {
				DATE_AND_TIME_SPACE_SEPARTED_FORMATER.setTimeZone(TimeZone
						.getTimeZone(defaultTimeZone));
			} else {
				DATE_AND_TIME_SPACE_SEPARTED_FORMATER.setTimeZone(timeZone);
			}
			return DATE_AND_TIME_SPACE_SEPARTED_FORMATER.format(paramCalendar
					.getTime());
		}
		return null;
	}

	public synchronized static Calendar createTimeStringToCalendar(
			String paramString, TimeZone timeZone) {

		if ((paramString != null) && (paramString.length() >= 8)) {
			Date date = null;
			Calendar localCalendar = null;
			try {
				if (paramString.indexOf("/") > 0) {
					if (timeZone == null) {
						DATE_AND_TIME_SLANT_SEPARTED_FORMATER
								.setTimeZone(TimeZone
										.getTimeZone(defaultTimeZone));
					} else {
						DATE_AND_TIME_SLANT_SEPARTED_FORMATER
								.setTimeZone(timeZone);
					}
					date = DATE_AND_TIME_SLANT_SEPARTED_FORMATER
							.parse(paramString);

				} else if (paramString.indexOf("-") > 0) {
					if (timeZone == null) {
						DATE_AND_TIME_SLANT_SEPARTED_FORMATER
								.setTimeZone(TimeZone
										.getTimeZone(defaultTimeZone));
					} else {
						DATE_AND_TIME_SLANT_SEPARTED_FORMATER
								.setTimeZone(timeZone);
					}

					date = DATE_AND_TIME_STRIPING_FORMATER.parse(paramString);
				}

				localCalendar = Calendar.getInstance();
				localCalendar.setTime(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return localCalendar;
		}
		return null;
	}
	
	public static int daysBetween(Date smdate,Date bdate) throws ParseException{    
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        smdate=sdf.parse(sdf.format(smdate));  
        bdate=sdf.parse(sdf.format(bdate));  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(smdate);    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(bdate);    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));           
    }    
      
	/** 
	*字符串的日期格式的计算 
	*/  
    public static int daysBetween(String smdate,String bdate) throws ParseException{  
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
        Calendar cal = Calendar.getInstance();    
        cal.setTime(sdf.parse(smdate));    
        long time1 = cal.getTimeInMillis();                 
        cal.setTime(sdf.parse(bdate));    
        long time2 = cal.getTimeInMillis();         
        long between_days=(time2-time1)/(1000*3600*24);  
            
       return Integer.parseInt(String.valueOf(between_days));     
    }

	public static void main(String[] args) {

		// Calendar calendar = Calendar.getInstance();
		//		
		System.out.println("startDate: "
				+ CalendarUtils.formatYearMonthDayNoSeparated("20010202"));

		// Calendar end_calendar = Calendar.getInstance();
		// end_calendar.add(Calendar.HOUR_OF_DAY, -8);

	}
}
