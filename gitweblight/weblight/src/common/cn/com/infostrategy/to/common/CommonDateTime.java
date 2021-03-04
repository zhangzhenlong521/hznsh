package cn.com.infostrategy.to.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期时间类型封装 格式：yyyy-MM-dd hh:mm:ss，不含时区、语言、地区等信息。
 * 注意：Date、GregorianCalendar和UFDateTime中的getTime()方法含义完全不同 Date
 * getTime()返回当前时间距格林威制时间1970-01-01 00:00:00(北京时间1970-01-01 08:00:00)的毫秒数(long)
 * GregorianCalendar getTime()返回含有地区信息的日期时间(Date) UFDateTime
 * getTime()返回hh:mm:ss格式字符串(String) getMillis()返回当前时间距1970-01-01
 * 00:00:00的毫秒数(long); getMillisAfter(UFDateTime)返回当前时间距参数时间的毫秒数(long); 作者:张扬
 */
public final class CommonDateTime implements java.io.Serializable, Comparable
{
	static final long serialVersionUID = -7539595826392466408L;

	private String value = null;

	private static final long millisPerDay = 24 * 60 * 60 * 1000;

	private static final int millisPerHour = 60 * 60 * 1000;

	private static final int millisPerMinute = 60 * 1000;

	private static final int millisPerSecond = 1000;

	/**
	 * UFTime 构造子注释。
	 */
	public CommonDateTime()
	{
		this("1970-01-01 00:00:00");
	}

	/**
	 * 以从1970年1月1日0时0分0秒到现在的毫秒数来构造日期和时间 创建日期：(00-7-10 14:37:17)
	 *
	 * @param m
	 *            long
	 */
	public CommonDateTime(long m)
	{
		this(new java.util.Date(m));
	}

	/**
	 * 用字符串yyyy-mm-dd hh:mm:ss格式表示日期时间。
	 */
	public CommonDateTime(String strDateTime)
	{
		this(strDateTime, true);
	}

	public CommonDateTime(java.sql.Date date)
	{
		this((java.util.Date) date);
	}

	/**
	 * @param date
	 *            java.util.Date
	 */
	public CommonDateTime(java.util.Date date)
	{
		this(sd.format(date));
	}

	/**
	 * 比较日期先后，true为之后
	 */
	public boolean after(CommonDate when)
	{
		return getDate().toString().compareTo(when.toString()) > 0;
	}

	/**
	 * 比较日期时间先后，true为之后
	 */
	public boolean after(CommonDateTime dateTime)
	{
		return value.compareTo(dateTime.toString()) > 0;
	}

	/**
	 * 比较日期先后，true为之前
	 */
	public boolean before(CommonDate when)
	{
		return getDate().toString().compareTo(when.toString()) < 0;
	}

	/**
	 * 比较日期时间先后，true为之前
	 */
	public boolean before(CommonDateTime dateTime)
	{
		return value.compareTo(dateTime.toString()) < 0;
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-4-11 16:36:04)
	 *
	 * @return nc.vo.pub.lang.UFDateTime
	 */
	public Object clone()
	{
		return new CommonDateTime(value);
	}

	/**
	 * 返回日期先后，大于0为之后，等于0为同一日期，小于0为之前
	 */
	public int compareTo(CommonDate when)
	{
		return getDate().toString().compareTo(when.toString());
	}

	/**
	 * 返回日期先后，大于0为之后，等于0为同一日期时间，小于0为之前
	 */
	public int compareTo(CommonDateTime dateTime)
	{
		return value.compareTo(dateTime.toString());
	}

	/**
	 * 比较日期先后，true为同一天
	 */
	public boolean equals(CommonDate when)
	{
		return getDate().toString().equals(when.toString());
	}

	/**
	 * 返回日期。
	 *
	 * @return UFDate
	 */
	public CommonDate getDate()
	{
		return new CommonDate(value.substring(0, 10));
	}

	/**
	 * 返回天数后的日期时间，时间不变。
	 *
	 * @param days
	 *            int
	 */
	public CommonDateTime getDateAfter(int days)
	{
		java.util.GregorianCalendar mdate = new java.util.GregorianCalendar(
				getYear(), getMonth() - 1, getDay());
		return new CommonDateTime(mdate.getTime().getTime() + millisPerDay * days);
	}

	/**
	 * 返回天。
	 *
	 * @return int
	 */
	public int getDay()
	{
		return Integer.valueOf(value.substring(8, 10)).intValue();
	}

	/**
	 * 返回某一日期距今天数，负数表示在今天之后
	 *
	 * @return int
	 * @param when
	 *            UFDate
	 */
	public int getDaysAfter(CommonDate when)
	{
		int days = 0;
		if (when != null)
		{
			java.util.GregorianCalendar mdatewhen = new java.util.GregorianCalendar(
					when.getYear(), when.getMonth() - 1, when.getDay());
			java.util.GregorianCalendar mdateEnd = new java.util.GregorianCalendar(
					getYear(), getMonth() - 1, getDay());
			days = (int) ((mdateEnd.getTime().getTime() - mdatewhen.getTime()
					.getTime()) / millisPerDay);
		}
		return days;
	}

	/**
	 * 返回某一日期距今天数，负数表示在今天之前,忽略时间
	 *
	 * @return int
	 * @param begin
	 *            UFDate
	 */
	public int getDaysAfter(CommonDateTime begin)
	{
		return getDaysAfter(begin.getDate());
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
	public static int getDaysBetween(CommonDate begin, CommonDate end)
	{
		if (begin != null && end != null)
		{
			java.util.GregorianCalendar mdateBegin = new java.util.GregorianCalendar(
					begin.getYear(), begin.getMonth() - 1, begin.getDay());
			java.util.GregorianCalendar mdateEnd = new java.util.GregorianCalendar(
					end.getYear(), end.getMonth() - 1, end.getDay());
			long sbtw = (mdateEnd.getTime().getTime() - mdateBegin.getTime()
					.getTime());
			return (int)(sbtw / millisPerDay);
		}
		return 0;
	}

	/**
	 * 返回后一日期距前一日期之后后的小时数
	 *
	 * @return int
	 * @param begin
	 *            UFDate
	 * @param end
	 *            UFDate
	 */
	public static int getHoursBetween(CommonDateTime begin, CommonDateTime end)
	{
		return (int)(getMiliSBetweenTwoDate(begin,end) / millisPerHour);
	}

	/**
	 * 返回后一日期距前一日期之后后的分钟数
	 *
	 * @return int
	 * @param begin
	 *            UFDate
	 * @param end
	 *            UFDate
	 */
	public static int getMinutesBetween(CommonDateTime begin, CommonDateTime end)
	{
		return (int)(getMiliSBetweenTwoDate(begin,end) / millisPerMinute);
	}

	/**
	 * 返回后一日期距前一日期之后后的秒数
	 *
	 * @return int
	 * @param begin
	 *            UFDate
	 * @param end
	 *            UFDate
	 */
	public static int getSecondsBetween(CommonDateTime begin, CommonDateTime end)
	{
		return (int)(getMiliSBetweenTwoDate(begin,end) / millisPerSecond);
	}

	/**
	 * 取得两个UFDate之间相差的毫秒数
	 * @param begin
	 * @param end
	 * @return
	 */
	private static long getMiliSBetweenTwoDate(CommonDateTime begin, CommonDateTime end)
	{
		if (begin != null && end != null)
		{
			java.util.GregorianCalendar mdateBegin = new java.util.GregorianCalendar(
					begin.getYear(), begin.getMonth() - 1, begin.getDay(),
					begin.getHour(),begin.getMinute(),begin.getSecond());
			java.util.GregorianCalendar mdateEnd = new java.util.GregorianCalendar(
					end.getYear(), end.getMonth() - 1, end.getDay(),
					end.getHour(),end.getMinute(),end.getSecond());
			long sbtw = (mdateEnd.getTime().getTime() - mdateBegin.getTime()
					.getTime());
			return sbtw;
		}
		return 0;
	}

	/**
	 * 返回后一日期距前一日期之间的天数,不考虑时间
	 *
	 * @return int
	 * @param begin
	 *            UFDate
	 * @param end
	 *            UFDate
	 */
	public static int getDaysBetween(CommonDate begin, CommonDateTime end)
	{
		return getDaysBetween(begin, end.getDate());
	}

	/**
	 * 返回后一日期距前一日期之间的天数,不考虑时间
	 *
	 * @return int
	 * @param begin
	 *            UFDate
	 * @param end
	 *            UFDate
	 */
	public static int getDaysBetween(CommonDateTime begin, CommonDate end)
	{
		return getDaysBetween(begin.getDate(), end);
	}

	/**
	 * 返回后一日期距前一日期之间的天数,不考虑时间
	 *
	 * @return int
	 * @param begin
	 *            UFDate
	 * @param end
	 *            UFDate
	 */
	public static int getDaysBetween(CommonDateTime begin, CommonDateTime end)
	{
		return getDaysBetween(begin.getDate(), end.getDate());
	}

	public int getDaysMonth()
	{
		return getDaysMonth(getYear(), getMonth());
	}

	public static int getDaysMonth(int year, int month)
	{
		switch (month)
		{
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

	public String getEnMonth()
	{
		switch (getMonth())
		{
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
	public String getEnWeek()
	{
		switch (getWeek())
		{
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
	 * 在此处插入方法说明。 创建日期：(00-7-10 15:01:20)
	 *
	 * @return int
	 */
	public int getHour()
	{
		return Integer.valueOf(value.substring(11, 13)).intValue();
	}

	/**
	 * 得到毫秒数。 创建日期：(2001-4-13 10:24:00)
	 *
	 * @return long
	 */
	public long getMillis()
	{
		java.util.GregorianCalendar datetime = new java.util.GregorianCalendar(
				getYear(), getMonth() - 1, getDay(), getHour(), getMinute(),
				getSecond());
		return datetime.getTime().getTime();
		// 有个时区差问题
		// return getMillisAfter(new UFDateTime("1970-01-01 00:00:00"));
	}

	/**
	 * 此处插入方法说明。 创建日期：(2001-4-13 10:24:00)
	 *
	 * @return long
	 * @param dateTime
	 *            nc.vo.pub.lang.UFDateTime
	 */
	public long getMillisAfter(CommonDateTime dateTime)
	{
		long millis = 0;
		if (dateTime != null)
		{
			java.util.GregorianCalendar mdateBegin = new java.util.GregorianCalendar(
					dateTime.getYear(), dateTime.getMonth() - 1, dateTime
							.getDay(), dateTime.getHour(),
					dateTime.getMinute(), dateTime.getSecond());
			java.util.GregorianCalendar mdateEnd = new java.util.GregorianCalendar(
					getYear(), getMonth() - 1, getDay(), getHour(),
					getMinute(), getSecond());
			millis = mdateEnd.getTime().getTime()
					- mdateBegin.getTime().getTime();
		}
		return millis;
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-7-10 15:02:25)
	 *
	 * @return int
	 */
	public int getMinute()
	{
		return Integer.valueOf(value.substring(14, 16)).intValue();
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-7-10 14:53:44)
	 *
	 * @return int
	 */
	public int getMonth()
	{
		return Integer.valueOf(value.substring(5, 7)).intValue();
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-7-10 15:03:01)
	 *
	 * @return int
	 */
	public int getSecond()
	{
		return Integer.valueOf(value.substring(17, 19)).intValue();
	}

	public String getStrDay()
	{
		if (getDay() > 0 && getDay() < 10)
			return "0" + Integer.toString(getDay());
		else if (getDay() >= 10 && getDay() < 32)
			return Integer.toString(getDay());
		else
			return null;
	}

	public String getStrMonth()
	{
		if (getMonth() > 0 && getMonth() < 10)
			return "0" + Integer.toString(getMonth());
		else if (getMonth() >= 10 && getMonth() < 13)
			return Integer.toString(getMonth());
		else
			return null;
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-7-10 14:54:26)
	 *
	 * @return String
	 */
	public String getTime()
	{
		return value.substring(11, 19);
	}

	/**
	 * 返回<tt>UFTime</tt>。
	 * <p>
	 * 创建日期：(2006-1-11 14:54:26)
	 *
	 * @return String
	 */
	public CommonTime getUFTime()
	{
		return new CommonTime(getTime());
	}

	/**
	 * 在此处插入方法说明。 创建日期：(00-12-18 20:49:02)
	 *
	 * 原理 1980-1-6是星期日
	 *
	 * @return int Sunday-Monday-Saturday 0-6
	 */
	public int getWeek()
	{
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
	public int getYear()
	{
		return Integer.valueOf(value.substring(0, 4)).intValue();
	}

	/**
	 * 是否闰年。
	 *
	 * @return boolean
	 * @param year
	 *            int
	 */
	public static boolean isLeapYear(int year)
	{
		if ((year % 4 == 0) && (year % 100 != 0 || year % 400 == 0))
			return true;
		else
			return false;
	}

	public String toString()
	{
		return value == null ? "" : value;
	}

	/**
	 * 比较日期时间先后，true为同一日期时间
	 */
	public boolean equals(Object o)
	{
		if ((o != null) && (o instanceof CommonDateTime))
		{
			return value.equals(o.toString());
		}
		return false;
	}

	/**
	 * 返回天数前的日期时间，时间不变。
	 *
	 * @param days
	 *            int
	 */
	public CommonDateTime getDateBefore(int days)
	{
		return getDateAfter(-days);
	}

	/**
	 * 如果字符串的日期时间能转换成有效日期时间串－－－转换。 创建日期：(2001-5-28 13:28:29)
	 *
	 * @return java.lang.String
	 * @param sDateTime
	 *            java.lang.String
	 */
	public static String getValidUFDateTimeString(String sDateTime)
	{
		if (sDateTime == null)
			return null;
		int index = sDateTime.indexOf("/");
		if (index >= 0)
			sDateTime = sDateTime.replace('/', '-');
		index = sDateTime.indexOf(".");
		if (index >= 0)
			sDateTime = sDateTime.replace('.', '-');
		if (isAllowDateTime(sDateTime))
			return sDateTime;
		else
		{
			try
			{
				// 如果能转化，则转换
				index = sDateTime.indexOf("-");
				if (index < 1)
					return null;
				int year = Integer.parseInt(sDateTime.trim()
						.substring(0, index));
				//
				String sTemp = sDateTime.trim().substring(index + 1);
				index = sTemp.indexOf("-");
				if (index < 1)
					return null;
				//
				int month = Integer.parseInt(sTemp.trim().substring(0, index));
				if (month < 1 || month > 12)
					return null;
				sTemp = sTemp.trim().substring(index + 1);
				index = sTemp.indexOf(" ");
				int day = 1;
				if (index > 0)
				{
					day = Integer.parseInt(sTemp.trim().substring(0, index));
					sTemp = sTemp.trim().substring(index + 1);
				} else
				{
					day = Integer.parseInt(sTemp.trim().substring(0));
					sTemp = "";
				}
				int MONTH_LENGTH[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31,
						30, 31 };
				int LEAP_MONTH_LENGTH[] = { 31, 29, 31, 30, 31, 30, 31, 31, 30,
						31, 30, 31 };
				int daymax = isLeapYear(year) ? LEAP_MONTH_LENGTH[month - 1]
						: MONTH_LENGTH[month - 1];
				if (day < 1 || day > daymax)
					return null;

				// 检查时间
				// 如果能转化，则转换
				int hour = 0;
				int minute = 0;
				int second = 0;
				index = sTemp.indexOf(":");
				if (index < 1)
				{
					if (sTemp.trim().length() > 0)
						hour = Integer.parseInt(sTemp.trim());
				} else
				{
					hour = Integer.parseInt(sTemp.trim().substring(0, index));
					sTemp = sTemp.trim().substring(index + 1);
					if (sTemp.trim().length() > 0)
					{
						index = sTemp.indexOf(":");
						if (index < 1)
						{
							minute = Integer.parseInt(sTemp.trim());
						} else
						{
							minute = Integer.parseInt(sTemp.trim().substring(0,
									index));
							if (sTemp.trim().substring(index + 1).trim()
									.length() > 0)
								second = Integer.parseInt(sTemp.trim()
										.substring(index + 1));
						}
					}
				}
				if (hour < 0 || hour > 24 || minute < 0 || minute > 59
						|| second < 0 || second > 59)
					return null;
				//
				String strYear = String.valueOf(year);
				for (int i = strYear.length(); i < 4; i++)
					strYear = "0" + strYear;
				String strMonth = String.valueOf(month);
				if (strMonth.length() < 2)
					strMonth = "0" + strMonth;
				String strDay = String.valueOf(day);
				if (strDay.length() < 2)
					strDay = "0" + strDay;
				String strHour = String.valueOf(hour);
				if (strHour.length() < 2)
					strHour = "0" + strHour;
				String strMinute = String.valueOf(minute);
				if (strMinute.length() < 2)
					strMinute = "0" + strMinute;
				String strSecond = String.valueOf(second);
				if (strSecond.length() < 2)
					strSecond = "0" + strSecond;
				//
				return strYear + "-" + strMonth + "-" + strDay + " " + strHour
						+ ":" + strMinute + ":" + strSecond;
			} catch (Exception e)
			{
				return null;
			}
		}
	}

	/**
	 * 如果字符串能转换成日期返回true。
	 *
	 * @return boolean
	 * @param strDateTime
	 *            java.lang.String
	 */
	public static boolean isAllowDate(String strDateTime)
	{
		if (strDateTime == null || strDateTime.trim().length() == 0)
			return true;
		if (strDateTime.trim().length() > 9)
			return CommonDate.isAllowDate(strDateTime.substring(0, 10));
		else
			return false;
	}

	/**
	 * 如果字符串能转换成日期时间返回true。
	 *
	 * @return boolean
	 * @param strDaateTime
	 *            java.lang.String
	 */
	public static boolean isAllowDateTime(String strDateTime)
	{
		if (strDateTime == null || strDateTime.trim().length() == 0)
			return true;
		if (strDateTime.trim().length() != 19)
			return false;
		char c = strDateTime.trim().charAt(10);
		if (c != ' ')
			return false;
		if (!isAllowDate(strDateTime) || !isAllowTime(strDateTime))
			return false;
		return true;
	}

	/**
	 * 如果字符串11-18位能转换成时间返回true。
	 *
	 * @return boolean
	 * @param strDateTime
	 *            java.lang.String
	 */
	public static boolean isAllowTime(String strDateTime)
	{
		if (strDateTime == null || strDateTime.trim().length() == 0)
			return true;
		if (strDateTime.trim().length() != 19)
			return false;
		for (int i = 11; i < 19; i++)
		{
			char c = strDateTime.trim().charAt(i);
			if (i == 13 || i == 16)
			{
				if (c != ':')
					return false;
			} else if (c < '0' || c > '9')
				return false;
		}
		int hour = Integer.parseInt(strDateTime.trim().substring(11, 13));
		int minute = Integer.parseInt(strDateTime.trim().substring(14, 16));
		int second = Integer.parseInt(strDateTime.trim().substring(17, 19));
		if (hour < 0 || hour > 24 || minute < 0 || minute > 59 || second < 0
				|| second > 59)
			return false;
		return true;
	}

	private static SimpleDateFormat sd = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 此处插入方法说明。 创建日期：(2001-9-18 10:10:11)
	 *
	 * @param strDateTime
	 *            java.lang.String
	 * @param isParse
	 *            boolean
	 */
	public CommonDateTime(String strDateTime, boolean isParse)
	{
		if (isParse)
			value = getValidUFDateTimeString(strDateTime);
		else
			value = strDateTime;
	}

	/**
	 * 通过UFDate及UFTime构造函数
	 * 如果UFDate为空，那么设置为当前日期
	 * 如果UFTime为空，那么设置为当前时间
	 * @param date
	 *            nc.vo.pub.lang.UFDate
	 * @param time
	 *            nc.vo.pub.lang.UFTime
	 */
	public CommonDateTime(CommonDate date, CommonTime time)
	{
		if (date == null || date.toString().trim().length() == 0)
			value = new CommonDate(new Date()).toString();
		else
			value = date.toString();
		if (time == null || time.toString().trim().length() == 0)
			value += " " + new SimpleDateFormat("HH:mm:ss").format(new Date());//" 00:00:00";
		else
			value += " " + time.toString();
	}

	public int compareTo(Object o)
	{
		if (o instanceof CommonDate)
			return getDate().toString().compareTo(o.toString());
		else if (o instanceof CommonDateTime)
			return value.compareTo(o.toString());
		throw new RuntimeException(
				"Unsupported parameter type while comparing the UFDateTime!");
	}
}