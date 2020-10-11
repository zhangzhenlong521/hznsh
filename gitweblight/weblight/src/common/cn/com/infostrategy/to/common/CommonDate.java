package cn.com.infostrategy.to.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;

public final class CommonDate implements java.io.Serializable, Comparable {
	static final long serialVersionUID = -1037968151602108293L;

	private String value = null;

	private static final long millisPerDay = 24 * 60 * 60 * 1000;

	//private static final int millisPerHour = 60 * 60 * 1000;

	//private static final int millisPerMinute = 60 * 1000;

	private static Hashtable allUsedDate = new java.util.Hashtable();

	private static Hashtable htDateTo0 = new java.util.Hashtable();

	private int dayTo_0 = 0;

	/**
	 * UFDate 构造子注释。
	 */
	public CommonDate() {
		this("1970-01-01");
	}

	/**
	 * 以从1970年1月1日0时0分0秒到现在的毫秒数来构造日期
	 * 
	 * @param m
	 *            long
	 */
	public CommonDate(long m) {
		this(new java.util.Date(m));
	}

	/**
	 * 功能说明：用yyyy-MM-dd形式的字符串构造日期类型
	 */
	public CommonDate(String strDate) {
		this(strDate, true);
	}

	/**
	 * 功能说明：如果解析，用yyyy-MM-dd形式的字符串构造日期类型 如果不解析，可能是yyyy，yyyy-MM 创建日期：(2001-9-18
	 * 10:03:53)
	 * 
	 * @param strDate
	 *            java.lang.String
	 * @param isParse
	 *            boolean
	 */
	public CommonDate(String strDate, boolean isParse) {
		if (isParse) {
			value = getValidUFDateString(strDate);
			if (value == null)
				throw new IllegalArgumentException(
						"Illegal Date String:" + strDate);
		} else
			value = strDate;
		if (value != null && value.length() == 10) {
			try {
				java.util.GregorianCalendar mdateBegin = new java.util.GregorianCalendar(
						getYear(), getMonth() - 1, getDay());
				dayTo_0 = (int) (mdateBegin.getTime().getTime() / millisPerDay);
				htDateTo0.put(new Integer(dayTo_0), this);
			} catch (Exception exception) {
				exception.printStackTrace(System.out);
			}
		}
	}

	/**
	 * /////////////////////////////////////////////////////////
	 * 功能说明：用java.sql.Date类型构造UF日期类型
	 * 
	 * @param date
	 *            java.sql.Date
	 */
	public CommonDate(java.sql.Date date) {
		this((java.util.Date) date);
	}

	/**
	 * 功能说明：用java.util.Date类型构造日期类型
	 */
	public CommonDate(java.util.Date date) {
		this((new SimpleDateFormat("yyyy-MM-dd")).format(date));
	}

	/**
	 * 比较日期先后，对象日期在参数日期之后为true
	 */
	public boolean after(CommonDate when) {
		return value.compareTo(when.toString()) > 0;
	}

	/**
	 * 比较日期先后，对象日期在参数日期之前为true
	 */
	public boolean before(CommonDate when) {
		return value.compareTo(when.toString()) < 0;
	}

	/**
	 * 克隆日期兑对象。
	 * 
	 * @return nc.vo.pub.lang.UFDate
	 */
	public Object clone() {
		return new CommonDate(value);
	}

	/**
	 * 返回日期先后： 大于0 ---为参数之后日期 等于0 ---和参数为同一天 小于0 ---为参数之前日期
	 */
	public int compareTo(CommonDate when) {
		return value.compareTo(when.toString());
	}

	/**
	 * 比较日期先后，true为同一天
	 */
	public boolean equals(Object o) {
		if ((o != null) && (o instanceof CommonDate)) {
			if (value == null)
				return false;
			return value.equals(o.toString());
		} else if (o == null && value == null) {
			return true;
		}

		return false;
	}

	public static CommonDate getDate(String date) {
		return getDate(date, false);
	}

	public static CommonDate getDate(String date, boolean check) {
		CommonDate o = (CommonDate) allUsedDate.get(date);
		if (o != null)
			return o;
		o = new CommonDate(date, check);
		allUsedDate.put(date, o);
		return o;
	}

	/**
	 * 返回天数后的日期。
	 * 
	 * @param days
	 *            int
	 */
	public CommonDate getDateAfter(int days) {
		int nFrom1970_01_01TO_TODAY_after_days = dayTo_0 + days;
		CommonDate ufd = (CommonDate) htDateTo0.get(new Integer(
				nFrom1970_01_01TO_TODAY_after_days));
		if (ufd != null)
			return ufd;
		java.util.GregorianCalendar mdate = new java.util.GregorianCalendar(
				getYear(), getMonth() - 1, getDay());
		ufd = new CommonDate(mdate.getTime().getTime() + millisPerDay * days);
		htDateTo0.put(new Integer(nFrom1970_01_01TO_TODAY_after_days), ufd);
		return ufd;
	}

	/**
	 * 返回天数前的日期。
	 * 
	 * @param days
	 *            int
	 */
	public CommonDate getDateBefore(int days) {
		return getDateAfter(-days);
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-7-10 14:54:26)
	 * 
	 * @return int
	 */
	public int getDay() {
		return Integer.valueOf(value.substring(8, 10)).intValue();
	}

	/**
	 * 返回某一日期距今天数，负数表示在今天之后
	 * 
	 * @return int
	 * @param when
	 *            UFDate
	 */
	public int getDaysAfter(CommonDate when) {
		int days = 0;
		if (when != null) {
			java.util.GregorianCalendar mdatewhen = new java.util.GregorianCalendar(
					when.getYear(), when.getMonth() - 1, when.getDay());
			java.util.GregorianCalendar mdateEnd = new java.util.GregorianCalendar(
					getYear(), getMonth() - 1, getDay());
			days = (int) ((mdateEnd.getTime().getTime() - mdatewhen.getTime().getTime()) / millisPerDay);
		}
		return days;
	}

	/**
	 * 返回后一日期距前一日期之后后的天数
	 * 
	 * @return int
	 * @param begin
	 *            UFDate
	 * @param end
	 *            UFDate
	 */
	public static int getDaysBetween(CommonDate begin, CommonDate end) {
		int days = 0;
		if (begin != null && end != null) {
			java.util.GregorianCalendar mdateBegin = new java.util.GregorianCalendar(
					begin.getYear(), begin.getMonth() - 1, begin.getDay());
			java.util.GregorianCalendar mdateEnd = new java.util.GregorianCalendar(
					end.getYear(), end.getMonth() - 1, end.getDay());
			days = (int) ((mdateEnd.getTime().getTime() - mdateBegin.getTime().getTime()) / millisPerDay);
		}
		return days;
	}

	public int getDaysMonth() {
		return getDaysMonth(getYear(), getMonth());
	}

	public static int getDaysMonth(int year, int month) {
		switch (month) {
		case 1:
			return 31;
		case 2:
			if (isLeapYear(year))
				return 29;
			else
				return 28;
		case 3:
			return 31;
		case 4:
			return 30;
		case 5:
			return 31;
		case 6:
			return 30;
		case 7:
			return 31;
		case 8:
			return 31;
		case 9:
			return 30;
		case 10:
			return 31;
		case 11:
			return 30;
		case 12:
			return 31;
		default:
			return 30;
		}
	}

	public String getEnMonth() {
		switch (getMonth()) {
		case 1:
			return "Jan";
		case 2:
			return "Feb";
		case 3:
			return "Mar";
		case 4:
			return "Apr";
		case 5:
			return "May";
		case 6:
			return "Jun";
		case 7:
			return "Jul";
		case 8:
			return "Aug";
		case 9:
			return "Sep";
		case 10:
			return "Oct";
		case 11:
			return "Nov";
		case 12:
			return "Dec";
		}
		return null;
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-12-18 20:57:29)
	 * 
	 * @return java.lang.String
	 */
	public String getEnWeek() {
		switch (getWeek()) {
		case 0:
			return "Sun";
		case 1:
			return "Mon";
		case 2:
			return "Tue";
		case 3:
			return "Wed";
		case 4:
			return "Thu";
		case 5:
			return "Fri";
		case 6:
			return "Sat";
		}
		return null;
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-7-10 14:53:44)
	 * 
	 * @return int
	 */
	public int getMonth() {
		return Integer.valueOf(value.substring(5, 7)).intValue();
	}

	public String getStrDay() {
		if (getDay() > 0 && getDay() < 10)
			return "0" + Integer.toString(getDay());
		else if (getDay() >= 10 && getDay() < 32)
			return Integer.toString(getDay());
		else
			return null;
	}

	public String getStrMonth() {
		if (getMonth() > 0 && getMonth() < 10)
			return "0" + Integer.toString(getMonth());
		else if (getMonth() >= 10 && getMonth() < 13)
			return Integer.toString(getMonth());
		else
			return null;
	}

	/**
	 * 如果字符串的日期能转换成有效日期串－－－转换。 创建日期：(2001-5-28 13:28:29)
	 * 
	 * @return java.lang.String
	 * @param sDate
	 *            java.lang.String
	 */
	public static String getValidUFDateString(String sDate) {
		if (sDate==null || sDate.trim().length()==0)
			return null;
		int index = sDate.indexOf("/");
		if (index >= 0)
			sDate = sDate.replace('/', '-');
		index = sDate.indexOf(".");
		if (index >= 0)
			sDate = sDate.replace('.', '-');
		if (isAllowDate(sDate))
			return sDate;
		else {
			try {
				// 如果能转化，则转换
				index = sDate.indexOf("-");
				if (index < 1)
					return null;
				int year = Integer.parseInt(sDate.trim().substring(0, index));
				//
				String sTemp = sDate.trim().substring(index + 1);
				index = sTemp.indexOf("-");
				if (index < 1)
					return null;
				int month = Integer.parseInt(sTemp.trim().substring(0, index));
				//
				if (month < 1 || month > 12)
					return null;
				int day = Integer.parseInt(sTemp.trim().substring(index + 1));
				int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
						30, 31 };
				int LEAP_MONTH_LENGTH[] = { 31, 29, 31, 30, 31, 30, 31, 31, 30,
						31, 30, 31 };
				int daymax = isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1]
						: MONTH_LENGTH[month - 1];
				if (day < 1 || day > daymax)
					return null;
				String strYear = String.valueOf(year);
				for (int i = strYear.length(); i < 4; i++)
					strYear = "0" + strYear;
				String strMonth = String.valueOf(month);
				if (strMonth.length() < 2)
					strMonth = "0" + strMonth;
				String strDay = String.valueOf(day);
				if (strDay.length() < 2)
					strDay = "0" + strDay;
				return strYear + "-" + strMonth + "-" + strDay;
			} catch (Exception e) {
				return null;
			}
		}
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-12-18 20:49:02)
	 * 
	 * 原理 1980-1-6是星期日
	 * 
	 * @return int Sunday-Monday-Saturday 0-6
	 */
	public int getWeek() {
		int days = getDaysAfter(new CommonDate("1980-01-06"));
		int week = days % 7;
		if (week < 0)
			week += 7;
		return week;
	}
	

	/**
	 * 获得年的数值。 创建日期：(00-7-10 14:52:13)
	 * 
	 * @return int
	 */
	public int getYear() {
		return Integer.valueOf(value.substring(0, 4)).intValue();
	}

	/**
	 * 如果字符串能转换成日期返回true。
	 * 
	 * @return boolean
	 * @param strDate
	 *            java.lang.String
	 */
	public static boolean isAllowDate(String strDate) {
		if (strDate == null || strDate.trim().length() == 0)
			return true;
		if (strDate.trim().length() != 10)
			return false;
		for (int i = 0; i < 10; i++) {
			char c = strDate.trim().charAt(i);
			if (i == 4 || i == 7) {
				if (c != '-')
					return false;
			} else if (c < '0' || c > '9')
				return false;
		}
		int year = Integer.parseInt(strDate.trim().substring(0, 4));
		int month = Integer.parseInt(strDate.trim().substring(5, 7));
		if (month < 1 || month > 12)
			return false;
		int day = Integer.parseInt(strDate.trim().substring(8, 10));
		int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int LEAP_MONTH_LENGTH[] = { 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30,
				31 };
		int daymax = isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1]
				: MONTH_LENGTH[month - 1];
		if (day < 1 || day > daymax)
			return false;
		return true;
	}

	/**
	 * 是否闰年。
	 * 
	 * @return boolean
	 */
	public boolean isLeapYear() {
		return isLeapYear(getYear());
	}

	/**
	 * 是否闰年。
	 * 
	 * @return boolean
	 * @param year
	 *            int
	 */
	public static boolean isLeapYear(int year) {
		if ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0))
			return true;
		else
			return false;
	}

	public String toString() {
		return value == null ? "" : value;
	}

	public int compareTo(Object o) {
		if (o instanceof CommonDate)
			return value.compareTo(o.toString());
		else if (o instanceof CommonDateTime)
			return value.compareTo(((CommonDateTime) o).getDate().toString());
		else if (o==null || o instanceof String)
			return value.compareTo("");
		throw new RuntimeException(
				"Unsupported parameter type while comparing the UFDate!");
	}

	/**
	 * 得到毫秒数。
	 * <p>
	 * 创建日期：(2006-1-11 10:24:00)
	 * 
	 * @return long
	 */
	public long getMillis() {
		GregorianCalendar datetime = new GregorianCalendar(getYear(),
				getMonth() - 1, getDay(), 0, 0, 0);
		return datetime.getTimeInMillis();
		// 有个时区差问题
		// return getMillisAfter(new UFDateTime("1970-01-01 00:00:00"));
	}
	/**
	 * 返加当前日期在一年内的周数。
	 * @return
	 */
	public int getWeekOfYear() {
		GregorianCalendar calendar = new GregorianCalendar(getYear(), getMonth(), getDay());
		return calendar.get(Calendar.WEEK_OF_YEAR);
	}
	
	/**
	 * 转换为{@link java.util.Date}。
	 * @return
	 */
	public Date toDate() {
		return new Date(getMillis());
	}
}