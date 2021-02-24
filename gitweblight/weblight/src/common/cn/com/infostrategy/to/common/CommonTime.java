package cn.com.infostrategy.to.common;

import java.text.SimpleDateFormat;

/**
 * ////////////////////////////////////////////////////////////////////
 * 功能描述：用HH:mm:ss的格式表示时间。
 * ////////////////////////////////////////////////////////////////////
 */
public final class CommonTime implements java.io.Serializable,Comparable{
	static final long serialVersionUID = 7886265777567493523L;
/**
 * UFTime 构造子注释。
 */
public CommonTime() {
	super();
}
/**
 * 以从1970年1月1日0时0分0秒到现在的毫秒数来构造时间
 * 创建日期：(00-7-10 14:37:17)
 * @param m long
 */
public CommonTime(long m) {
	this(new java.util.Date(m));
}
/**
 *
 * @version (00-7-3 13:55:11)
 *
 * @param strTime java.lang.String
 */
public CommonTime(String strTime) {
	this(strTime,true);
}
/**
 * /////////////////////////////////////////////////////////
 * 功能说明：用java.sql.Date类型构造UF时间类型
 * 参数说明：
 * 返回值：
 * 已知错误：（可选）
 * 抛出异常：（可选）
 * 更改的对象属性：（可选）
 * 更改历史：（可选）
 * 使用范例：（可选）
 * 状态变迁：（可选）
 * 并发问题：（可选）
 * /////////////////////////////////////////////////////////
 *
 * @param date java.sql.Date
 */
public CommonTime(java.sql.Date date) {
	this( (java.util.Date)date );
}
/**
 * /////////////////////////////////////////////////////////
 * 功能说明：用java.util.Date类型构造时间类型
 * 参数说明：
 * 返回值：
 * 已知错误：（可选）
 * 抛出异常：（可选）
 * 更改的对象属性：（可选）
 * 更改历史：（可选）
 * 使用范例：（可选）
 * 状态变迁：（可选）
 * 并发问题：（可选）
 * /////////////////////////////////////////////////////////
 *
 * @param date java.util.Date
 */
public CommonTime(java.util.Date date) {
	this((new SimpleDateFormat("HH:mm:ss")).format(date));
}



/**
 * 在此处插入方法说明。
 * 创建日期：(00-7-6 9:53:30)
 * @return java.lang.String
 */
public String toString() {
	return value==null?"":value;
}

	private String value = null;

/**
 * 比较时间先后，对象时间在参数时间之后为true
 */
public boolean after(CommonTime when) {
	return value.compareTo(when.toString()) > 0;
}

/**
 * 比较时间先后，对象时间在参数时间之前为true，不考虑日期
 */
public boolean before(CommonTime when) {
	return value.compareTo(when.toString()) < 0;
}

/**
 * 克隆时间对象。
 * @return nc.vo.pub.lang.UFTime
 */
public Object clone() {
	return new CommonTime(value);
}

/**
 * 返回时间先后，不考虑日期：
	大于0为参数之后时间
	等于0和参数为同一时刻
	小于0为参数之前时间
 */
public int compareTo(CommonTime when) {
	return value.compareTo(when.toString());
}

/**
 * 比较日期先后，true为同一天
 */
public boolean equals(Object o) {
	if ((o != null) && (o instanceof CommonTime)) {
		return value.equals(o.toString());
	}
	return false;
}

/**
 * 在此处插入方法说明。
 * 创建日期：(00-7-10 15:01:20)
 * @return java.lang.Integer
 */
public int getHour() {
	return Integer.valueOf(value.substring(0,2)).intValue();
}

/**
 * 在此处插入方法说明。
 * 创建日期：(00-7-10 15:02:25)
 * @return java.lang.Integer
 */
public int getMinute() {
	return Integer.valueOf(value.substring(3,5)).intValue();
}

/**
 * 在此处插入方法说明。
 * 创建日期：(00-7-10 15:03:01)
 * @return java.lang.Integer
 */
public int getSecond() {
	return Integer.valueOf(value.substring(6,8)).intValue();
}

/**
 * 如果字符串的时间能转换成有效时间串－－－转换。
 * 创建日期：(2001-5-28 13:28:29)
 * @return java.lang.String
 * @param sTime java.lang.String
 */
public static String getValidUFTimeString(String sTime) {
	if (sTime == null)
		return null;
	if (isAllowTime(sTime))
		return sTime;
	else {
		//如果能转化，则转换
		try {
			int hour = 0;
			int minute = 0;
			int second = 0;
			int index = sTime.indexOf(":");
			if (index < 1) {
				if (sTime.trim().length() > 0)
					hour = Integer.parseInt(sTime.trim());
			} else {
				hour = Integer.parseInt(sTime.trim().substring(0, index));
				String sTemp = sTime.trim().substring(index + 1);
				if (sTemp.trim().length() > 0) {
					index = sTemp.indexOf(":");
					if (index < 1) {
						minute = Integer.parseInt(sTemp.trim());
					} else {
						minute = Integer.parseInt(sTemp.trim().substring(0, index));
						if (sTemp.trim().substring(index + 1).trim().length() > 0)
							second = Integer.parseInt(sTemp.trim().substring(index + 1));
					}
				}
			}
			if (hour < 0 || hour > 24 || minute < 0 || minute > 59 || second < 0 || second > 59)
				return null;
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
			return strHour + ":" + strMinute + ":" + strSecond;
		} catch (Exception e) {
			return null;
		}
	}
}

/**
 * 如果字符串11-18位能转换成时间返回true。
 * @return boolean
 * @param strDateTime java.lang.String
 */
public static boolean isAllowTime(String strTime) {
	if (strTime == null || strTime.trim().length() == 0)
		return true;
	if (strTime.trim().length() != 8)
		return false;
	for (int i = 0; i < 8; i++) {
		char c = strTime.trim().charAt(i);
		if (i == 2 || i == 5) {
			if (c != ':')
				return false;
		} else
			if (c < '0' || c > '9')
				return false;
	}
	int hour = Integer.parseInt(strTime.trim().substring(0, 2));
	int minute = Integer.parseInt(strTime.trim().substring(3, 5));
	int second = Integer.parseInt(strTime.trim().substring(6, 8));
	if (hour < 0 || hour > 24 || minute < 0 || minute > 59 || second < 0 || second > 59)
		return false;
	return true;
}

/**
 * 以从1970年1月1日0时0分0秒到现在的毫秒数来构造时间
 * 参数o无意义，只是为了表示一个新的构造
 * 创建日期：(00-7-10 14:37:17)
 * @param m long
 */
public CommonTime(long m, Object o) {
	if (m == 24 * 3600000) {
		value = "24:00:00";
		return;
	}
	long seconds = m / 1000;
	long hour = seconds / 3600;
	hour %= 24;
	long minute = seconds / 60;
	minute %= 60;
	long second = seconds % 60;
	value = "";
	if (hour < 10)
		value += "0" + hour;
	else
		value += hour;
	value += ":";
	if (minute < 10)
		value += "0" + minute;
	else
		value += minute;
	value += ":";
	if (second < 10)
		value += "0" + second;
	else
		value += second;
}

/**
 * 此处插入方法说明。
 * 创建日期：(2001-9-18 10:54:22)
 * @param strTime java.lang.String
 * @param isParse boolean
 */
public CommonTime(String strTime, boolean isParse) {
	if (isParse)
		value = getValidUFTimeString(strTime);
	else
		value = strTime;
}

/**
 * 得到毫秒数。
 * 创建日期：(2002-8-23 13:38:33)
 * @return java.lang.Long
 */
public long getMillis() {
	return ((getHour() * 60 + getMinute()) * 60 + getSecond()) * 1000;
}

public int compareTo(Object o)
{
	if(o instanceof CommonTime)
		return value.compareTo(o.toString());
	throw new RuntimeException("Unsupported parameter type while comparing UFTime!");
}

}