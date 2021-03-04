package cn.com.pushworld.salary.to.baseinfo;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.regex.Pattern;

import cn.com.infostrategy.to.common.TBUtil;

public class FormulaTool {
	protected String codeAndCity[][] = { { "11", "北京" }, { "12", "天津" }, { "13", "河北" }, { "14", "山西" }, { "15", "内蒙古" }, { "21", "辽宁" }, { "22", "吉林" }, { "23", "黑龙江" }, { "31", "上海" }, { "32", "江苏" }, { "33", "浙江" }, { "34", "安徽" }, { "35", "福建" }, { "36", "江西" }, { "37", "山东" }, { "41", "河南" }, { "42", "湖北" }, { "43", "湖南" }, { "44", "广东" }, { "45", "广西" }, { "46", "海南" }, { "50", "重庆" },
			{ "51", "四川" }, { "52", "贵州" }, { "53", "云南" }, { "54", "西藏" }, { "61", "陕西" }, { "62", "甘肃" }, { "63", "青海" }, { "64", "宁夏" }, { "65", "新疆" }, { "71", "台湾" }, { "81", "香港" }, { "82", "澳门" }, { "91", "国外" } };

	private String cityCode[] = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82", "91" };

	// 每位加权因子
	private int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	// 第18位校检码
	//	private String verifyCode[] = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };

	/**
	 * 验证所有的身份证的合法性
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isValidatedAllIdcard(String idcard) {
		if (!isIdcard(idcard)) {
			return false;
		}
		if (idcard.length() == 15) {
			idcard = this.convertIdcarBy15bit(idcard);
		}
		return this.isValidate18Idcard(idcard);
	}

	/**
	 * <p>
	 * 判断18位身份证的合法性
	 * </p>
	 * 根据〖中华人民共和国国家标准GB11643-1999〗中有关公民身份号码的规定，公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成。
	 * 排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
	 * <p>
	 * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
	 * </p>
	 * <p>
	 * 1.前1、2位数字表示：所在省份的代码； 2.第3、4位数字表示：所在城市的代码； 3.第5、6位数字表示：所在区县的代码；
	 * 4.第7~14位数字表示：出生年、月、日； 5.第15、16位数字表示：所在地的派出所的代码；
	 * 6.第17位数字表示性别：奇数表示男性，偶数表示女性；
	 * 7.第18位数字是校检码：也有的说是个人信息码，一般是随计算机的随机产生，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示。
	 * </p>
	 * <p>
	 * 第十八位数字(校验码)的计算方法为： 1.将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4
	 * 2 1 6 3 7 9 10 5 8 4 2
	 * </p>
	 * <p>
	 * 2.将这17位数字和系数相乘的结果相加。
	 * </p>
	 * <p>
	 * 3.用加出来和除以11，看余数是多少？
	 * </p>
	 * 4.余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3
	 * 2。
	 * <p>
	 * 5.通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2。
	 * </p>
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isValidate18Idcard(String idcard) {
		// 非18位为假
		if (idcard.length() != 18) {
			return false;
		}
		// 获取前17位
		String idcard17 = idcard.substring(0, 17);
		// 获取第18位
		String idcard18Code = idcard.substring(17, 18);
		char c[] = null;
		String checkCode = "";
		// 是否都为数字
		if (isDigital(idcard17)) {
			c = idcard17.toCharArray();
		} else {
			return false;
		}

		if (null != c) {
			int bit[] = new int[idcard17.length()];

			bit = converCharToInt(c);

			int sum17 = 0;

			sum17 = getPowerSum(bit);

			// 将和值与11取模得到余数进行校验码判断
			checkCode = getCheckCodeBySum(sum17);
			if (null == checkCode) {
				return false;
			}
			// 将身份证的第18位与算出来的校码进行匹配，不相等就为假
			if (!idcard18Code.equalsIgnoreCase(checkCode)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 验证15位身份证的合法性,该方法验证不准确，最好是将15转为18位后再判断，该类中已提供。
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isValidate15Idcard(String idcard) {
		// 非15位为假
		if (idcard.length() != 15) {
			return false;
		}

		// 是否全都为数字
		if (isDigital(idcard)) {
			String provinceid = idcard.substring(0, 2);
			String birthday = idcard.substring(6, 12);
			int year = Integer.parseInt(idcard.substring(6, 8));
			int month = Integer.parseInt(idcard.substring(8, 10));
			int day = Integer.parseInt(idcard.substring(10, 12));

			// 判断是否为合法的省份
			boolean flag = false;
			for (String id : cityCode) {
				if (id.equals(provinceid)) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				return false;
			}
			// 该身份证生出日期在当前日期之后时为假
			Date birthdate = null;
			try {
				birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (birthdate == null || new Date().before(birthdate)) {
				return false;
			}

			// 判断是否为合法的年份
			GregorianCalendar curDay = new GregorianCalendar();
			int curYear = curDay.get(Calendar.YEAR);
			int year2bit = Integer.parseInt(String.valueOf(curYear).substring(2));

			// 判断该年份的两位表示法，小于50的和大于当前年份的，为假
			if ((year < 50 && year > year2bit)) {
				return false;
			}

			// 判断是否为合法的月份
			if (month < 1 || month > 12) {
				return false;
			}

			// 判断是否为合法的日期
			boolean mflag = false;
			curDay.setTime(birthdate); // 将该身份证的出生日期赋于对象curDay
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				mflag = (day >= 1 && day <= 31);
				break;
			case 2: // 公历的2月非闰年有28天,闰年的2月是29天。
				if (curDay.isLeapYear(curDay.get(Calendar.YEAR))) {
					mflag = (day >= 1 && day <= 29);
				} else {
					mflag = (day >= 1 && day <= 28);
				}
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				mflag = (day >= 1 && day <= 30);
				break;
			}
			if (!mflag) {
				return false;
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * 将15位的身份证转成18位身份证
	 * 
	 * @param idcard
	 * @return
	 */
	public String convertIdcarBy15bit(String idcard) {
		String idcard17 = null;
		// 非15位身份证
		if (idcard.length() != 15) {
			return null;
		}

		if (isDigital(idcard)) {
			// 获取出生年月日
			String birthday = idcard.substring(6, 12);
			Date birthdate = null;
			try {
				birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Calendar cday = Calendar.getInstance();
			cday.setTime(birthdate);
			String year = String.valueOf(cday.get(Calendar.YEAR));

			idcard17 = idcard.substring(0, 6) + year + idcard.substring(8);

			char c[] = idcard17.toCharArray();
			String checkCode = "";

			if (null != c) {
				int bit[] = new int[idcard17.length()];

				// 将字符数组转为整型数组
				bit = converCharToInt(c);
				int sum17 = 0;
				sum17 = getPowerSum(bit);

				// 获取和值与11取模得到余数进行校验码
				checkCode = getCheckCodeBySum(sum17);
				// 获取不到校验位
				if (null == checkCode) {
					return null;
				}

				// 将前17位与第18位校验码拼接
				idcard17 += checkCode;
			}
		} else { // 身份证包含数字
			return null;
		}
		return idcard17;
	}

	/**
	 * 15位和18位身份证号码的基本数字和位数验校
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isIdcard(String idcard) {
		return idcard == null || "".equals(idcard) ? false : Pattern.matches("(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idcard);
	}

	/**
	 * 15位身份证号码的基本数字和位数验校
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean is15Idcard(String idcard) {
		return idcard == null || "".equals(idcard) ? false : Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$", idcard);
	}

	/**
	 * 18位身份证号码的基本数字和位数验校
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean is18Idcard(String idcard) {
		return Pattern.matches("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$", idcard);
	}

	/**
	 * 数字验证
	 * 
	 * @param str
	 * @return
	 */
	public boolean isDigital(String str) {
		return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");
	}

	/**
	 * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
	 * 
	 * @param bit
	 * @return
	 */
	public int getPowerSum(int[] bit) {

		int sum = 0;

		if (power.length != bit.length) {
			return sum;
		}

		for (int i = 0; i < bit.length; i++) {
			for (int j = 0; j < power.length; j++) {
				if (i == j) {
					sum = sum + bit[i] * power[j];
				}
			}
		}
		return sum;
	}

	/**
	 * 将和值与11取模得到余数进行校验码判断
	 * 
	 * @param checkCode
	 * @param sum17
	 * @return 校验位
	 */
	public String getCheckCodeBySum(int sum17) {
		String checkCode = null;
		switch (sum17 % 11) {
		case 10:
			checkCode = "2";
			break;
		case 9:
			checkCode = "3";
			break;
		case 8:
			checkCode = "4";
			break;
		case 7:
			checkCode = "5";
			break;
		case 6:
			checkCode = "6";
			break;
		case 5:
			checkCode = "7";
			break;
		case 4:
			checkCode = "8";
			break;
		case 3:
			checkCode = "9";
			break;
		case 2:
			checkCode = "x";
			break;
		case 1:
			checkCode = "0";
			break;
		case 0:
			checkCode = "1";
			break;
		}
		return checkCode;
	}

	/**
	 * 将字符数组转为整型数组
	 * 
	 * @param c
	 * @return
	 * @throws NumberFormatException
	 */
	public int[] converCharToInt(char[] c) throws NumberFormatException {
		int[] a = new int[c.length];
		int k = 0;
		for (char temp : c) {
			a[k++] = Integer.parseInt(String.valueOf(temp));
		}
		return a;
	}

	public String getBirthDayByIdCard(String idcard) {
		if (idcard.length() == 15) {
			idcard = convertIdcarBy15bit(idcard);
		}
		String birthday = idcard.substring(6, 14);
		return birthday.substring(0, 4) + "-" + birthday.substring(4, 6) + "-" + birthday.substring(6, 8);
	}

	public String getSexByIdCard(String idcard) {
		if (idcard.length() == 15) {
			idcard = convertIdcarBy15bit(idcard);
		}
		// 获取性别
		String id17 = idcard.substring(16, 17);
		if (Integer.parseInt(id17) % 2 != 0) {
			return "男";
		} else {
			return "女";
		}
	}

	public double getAgeByDay(String currdate, String birthday, boolean isup) {
		double age = 0;
		if (birthday == null || "".equals(birthday.trim())) {
			return age;
		}
		if (currdate == null || "".equals(currdate.trim())) {
			currdate = TBUtil.getTBUtil().getCurrDate();
		}
		try {
			//这里的判断以前有问题，每次都返回了0，故修改之【李春娟/2019-03-11】
			Date birthdate = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
			GregorianCalendar currentDay = new GregorianCalendar();
			currentDay.setTime(birthdate);
			int birthyear = currentDay.get(Calendar.YEAR);
			String toyear = currdate.substring(0, 4);
			int yearlogic = Integer.parseInt(toyear) - birthyear;
			Date tobirthdate = new SimpleDateFormat("yyyy-MM-dd").parse(toyear + birthday.substring(4));
			Date currdate_ = new SimpleDateFormat("yyyy-MM-dd").parse(currdate);
			if (currdate_.getTime() > tobirthdate.getTime()) { // 今年过了生日了
				Date nextbirthdate = new SimpleDateFormat("yyyy-MM-dd").parse((Integer.parseInt(toyear) + 1) + birthday.substring(4));
				if (isup) { // 算工龄
					age = (new BigDecimal(yearlogic).add(new BigDecimal(1))).doubleValue();
				} else {
					age = new BigDecimal(yearlogic).add(new BigDecimal(currdate_.getTime() - tobirthdate.getTime()).divide(new BigDecimal(nextbirthdate.getTime() - tobirthdate.getTime()), 2, BigDecimal.ROUND_HALF_DOWN)).doubleValue();
				}
			} else { // 还没过
				Date oldbirthdate = new SimpleDateFormat("yyyy-MM-dd").parse((Integer.parseInt(toyear) - 1) + birthday.substring(4));
				if (isup) { // 算工龄
					age = (new BigDecimal(yearlogic).add(new BigDecimal(0))).doubleValue();
				} else {
					age = new BigDecimal(yearlogic - 1).add(new BigDecimal(currdate_.getTime() - oldbirthdate.getTime()).divide(new BigDecimal(tobirthdate.getTime() - oldbirthdate.getTime()), 2, BigDecimal.ROUND_HALF_DOWN)).doubleValue();
				}
			}
		} catch (ParseException e) {
			System.out.println(">>>>>>>>>>>>>>>>>" + birthday);
			e.printStackTrace();
		}
		return age;
	}

	/**
	 * 返回月的行龄
	 * 
	 * @param currdate
	 * @param birthday
	 * @param isup
	 * @return
	 */

	public int getWorkMonth(String birthday) {
		return getWorkMonth(TBUtil.getTBUtil().getCurrDate(), birthday);
	}

	/**
	 * 更准了
	 * @param currdate
	 * @param birthday
	 * @return
	 */
	public int getWorkMonth(String todate, String fromday) {
		if (fromday == null || "".equals(fromday)) {
			return 0;
		}
		if (todate == null || "".equals(todate)) {
			todate = TBUtil.getTBUtil().getCurrDate();
		}
		int[] temp = getDateLength(fromday, todate);
		if (Integer.parseInt(todate.substring(8, 10)) > Integer.parseInt(fromday.substring(8, 10)) && !fromday.equals(getLastDay(fromday))) {
			temp[1] = temp[1] + 1;
		}
		return temp[1];
	}

	public double getAgeByIdCard(String idcard) {
		String birthday = getBirthDayByIdCard(idcard);
		return getAgeByDay(null, birthday, false);
	}

	public int getWorkAgeByWorkDate(String workDate) {
		return getWorkAgeByWorkDate(TBUtil.getTBUtil().getCurrDate(), workDate);
	}

	public int getWorkAgeByWorkDate(String currdate, String workDate) {
		double age = getAgeByDay(currdate, workDate, true);
		return new BigDecimal(age).intValue();
	}

	public int[] getDateLength(String fromDate, String toDate) {
		Calendar c1 = getCal(fromDate);
		Calendar c2 = getCal(toDate);
		int[] p1 = { c1.get(Calendar.YEAR), c1.get(Calendar.MONTH), c1.get(Calendar.DAY_OF_MONTH) };
		int[] p2 = { c2.get(Calendar.YEAR), c2.get(Calendar.MONTH), c2.get(Calendar.DAY_OF_MONTH) };
		return new int[] { p2[0] - p1[0], p2[0] * 12 + p2[1] - p1[0] * 12 - p1[1], (int) ((c2.getTimeInMillis() - c1.getTimeInMillis()) / (24 * 3600 * 1000)) };
	}

	public Calendar getCal(String date) {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)) - 1, Integer.parseInt(date.substring(8, 10)));
		return cal;
	}

	public String getFirstDay(String date) {
		Date theDate = null;
		try {
			theDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		GregorianCalendar gcfast = (GregorianCalendar) Calendar.getInstance();
		gcfast.setTime(theDate);
		gcfast.set(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(gcfast.getTime());
	}

	public String getLastDay(String date_) {
		Date date = null;
		try {
			date = new SimpleDateFormat("yyyy-MM-dd").parse(date_);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.DATE, -1);
		SimpleDateFormat sdf_curr = new SimpleDateFormat("yyyy-MM-dd", Locale.SIMPLIFIED_CHINESE);
		return sdf_curr.format(calendar.getTime());
	}
	/*
	public static void main(String[] args) {
		System.out.println(new FormulaTool().getWorkMonth("2013-03-30", "2012-02-29"));
		System.out.println(new FormulaTool().getFirstDay("2012-02-29"));
		System.out.println(new FormulaTool().getLastDay("2012-02-29"));
	}
	*/
}
