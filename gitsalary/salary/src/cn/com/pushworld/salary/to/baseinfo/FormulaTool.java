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
	protected String codeAndCity[][] = { { "11", "����" }, { "12", "���" }, { "13", "�ӱ�" }, { "14", "ɽ��" }, { "15", "���ɹ�" }, { "21", "����" }, { "22", "����" }, { "23", "������" }, { "31", "�Ϻ�" }, { "32", "����" }, { "33", "�㽭" }, { "34", "����" }, { "35", "����" }, { "36", "����" }, { "37", "ɽ��" }, { "41", "����" }, { "42", "����" }, { "43", "����" }, { "44", "�㶫" }, { "45", "����" }, { "46", "����" }, { "50", "����" },
			{ "51", "�Ĵ�" }, { "52", "����" }, { "53", "����" }, { "54", "����" }, { "61", "����" }, { "62", "����" }, { "63", "�ຣ" }, { "64", "����" }, { "65", "�½�" }, { "71", "̨��" }, { "81", "���" }, { "82", "����" }, { "91", "����" } };

	private String cityCode[] = { "11", "12", "13", "14", "15", "21", "22", "23", "31", "32", "33", "34", "35", "36", "37", "41", "42", "43", "44", "45", "46", "50", "51", "52", "53", "54", "61", "62", "63", "64", "65", "71", "81", "82", "91" };

	// ÿλ��Ȩ����
	private int power[] = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };

	// ��18λУ����
	//	private String verifyCode[] = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };

	/**
	 * ��֤���е����֤�ĺϷ���
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
	 * �ж�18λ���֤�ĺϷ���
	 * </p>
	 * ���ݡ��л����񹲺͹����ұ�׼GB11643-1999�����йع�����ݺ���Ĺ涨��������ݺ�������������룬��ʮ��λ���ֱ������һλ����У������ɡ�
	 * ����˳�������������Ϊ����λ���ֵ�ַ�룬��λ���ֳ��������룬��λ����˳�����һλ����У���롣
	 * <p>
	 * ˳����: ��ʾ��ͬһ��ַ������ʶ������Χ�ڣ���ͬ�ꡢͬ�¡�ͬ �ճ������˱ඨ��˳��ţ�˳�����������������ԣ�ż������ ��Ů�ԡ�
	 * </p>
	 * <p>
	 * 1.ǰ1��2λ���ֱ�ʾ������ʡ�ݵĴ��룻 2.��3��4λ���ֱ�ʾ�����ڳ��еĴ��룻 3.��5��6λ���ֱ�ʾ���������صĴ��룻
	 * 4.��7~14λ���ֱ�ʾ�������ꡢ�¡��գ� 5.��15��16λ���ֱ�ʾ�����ڵص��ɳ����Ĵ��룻
	 * 6.��17λ���ֱ�ʾ�Ա�������ʾ���ԣ�ż����ʾŮ�ԣ�
	 * 7.��18λ������У���룺Ҳ�е�˵�Ǹ�����Ϣ�룬һ��������������������������������֤����ȷ�ԡ�У���������0~9�����֣���ʱҲ��x��ʾ��
	 * </p>
	 * <p>
	 * ��ʮ��λ����(У����)�ļ��㷽��Ϊ�� 1.��ǰ������֤����17λ���ֱ���Բ�ͬ��ϵ�����ӵ�һλ����ʮ��λ��ϵ���ֱ�Ϊ��7 9 10 5 8 4
	 * 2 1 6 3 7 9 10 5 8 4 2
	 * </p>
	 * <p>
	 * 2.����17λ���ֺ�ϵ����˵Ľ����ӡ�
	 * </p>
	 * <p>
	 * 3.�üӳ����ͳ���11���������Ƕ��٣�
	 * </p>
	 * 4.����ֻ������0 1 2 3 4 5 6 7 8 9 10��11�����֡���ֱ��Ӧ�����һλ���֤�ĺ���Ϊ1 0 X 9 8 7 6 5 4 3
	 * 2��
	 * <p>
	 * 5.ͨ�������֪���������2���ͻ������֤�ĵ�18λ�����ϳ����������ֵĢ������������10�����֤�����һλ�������2��
	 * </p>
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isValidate18Idcard(String idcard) {
		// ��18λΪ��
		if (idcard.length() != 18) {
			return false;
		}
		// ��ȡǰ17λ
		String idcard17 = idcard.substring(0, 17);
		// ��ȡ��18λ
		String idcard18Code = idcard.substring(17, 18);
		char c[] = null;
		String checkCode = "";
		// �Ƿ�Ϊ����
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

			// ����ֵ��11ȡģ�õ���������У�����ж�
			checkCode = getCheckCodeBySum(sum17);
			if (null == checkCode) {
				return false;
			}
			// �����֤�ĵ�18λ���������У�����ƥ�䣬����Ⱦ�Ϊ��
			if (!idcard18Code.equalsIgnoreCase(checkCode)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * ��֤15λ���֤�ĺϷ���,�÷�����֤��׼ȷ������ǽ�15תΪ18λ�����жϣ����������ṩ��
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isValidate15Idcard(String idcard) {
		// ��15λΪ��
		if (idcard.length() != 15) {
			return false;
		}

		// �Ƿ�ȫ��Ϊ����
		if (isDigital(idcard)) {
			String provinceid = idcard.substring(0, 2);
			String birthday = idcard.substring(6, 12);
			int year = Integer.parseInt(idcard.substring(6, 8));
			int month = Integer.parseInt(idcard.substring(8, 10));
			int day = Integer.parseInt(idcard.substring(10, 12));

			// �ж��Ƿ�Ϊ�Ϸ���ʡ��
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
			// �����֤���������ڵ�ǰ����֮��ʱΪ��
			Date birthdate = null;
			try {
				birthdate = new SimpleDateFormat("yyMMdd").parse(birthday);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			if (birthdate == null || new Date().before(birthdate)) {
				return false;
			}

			// �ж��Ƿ�Ϊ�Ϸ������
			GregorianCalendar curDay = new GregorianCalendar();
			int curYear = curDay.get(Calendar.YEAR);
			int year2bit = Integer.parseInt(String.valueOf(curYear).substring(2));

			// �жϸ���ݵ���λ��ʾ����С��50�ĺʹ��ڵ�ǰ��ݵģ�Ϊ��
			if ((year < 50 && year > year2bit)) {
				return false;
			}

			// �ж��Ƿ�Ϊ�Ϸ����·�
			if (month < 1 || month > 12) {
				return false;
			}

			// �ж��Ƿ�Ϊ�Ϸ�������
			boolean mflag = false;
			curDay.setTime(birthdate); // �������֤�ĳ������ڸ��ڶ���curDay
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
			case 2: // ������2�·�������28��,�����2����29�졣
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
	 * ��15λ�����֤ת��18λ���֤
	 * 
	 * @param idcard
	 * @return
	 */
	public String convertIdcarBy15bit(String idcard) {
		String idcard17 = null;
		// ��15λ���֤
		if (idcard.length() != 15) {
			return null;
		}

		if (isDigital(idcard)) {
			// ��ȡ����������
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

				// ���ַ�����תΪ��������
				bit = converCharToInt(c);
				int sum17 = 0;
				sum17 = getPowerSum(bit);

				// ��ȡ��ֵ��11ȡģ�õ���������У����
				checkCode = getCheckCodeBySum(sum17);
				// ��ȡ����У��λ
				if (null == checkCode) {
					return null;
				}

				// ��ǰ17λ���18λУ����ƴ��
				idcard17 += checkCode;
			}
		} else { // ���֤��������
			return null;
		}
		return idcard17;
	}

	/**
	 * 15λ��18λ���֤����Ļ������ֺ�λ����У
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean isIdcard(String idcard) {
		return idcard == null || "".equals(idcard) ? false : Pattern.matches("(^\\d{15}$)|(\\d{17}(?:\\d|x|X)$)", idcard);
	}

	/**
	 * 15λ���֤����Ļ������ֺ�λ����У
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean is15Idcard(String idcard) {
		return idcard == null || "".equals(idcard) ? false : Pattern.matches("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$", idcard);
	}

	/**
	 * 18λ���֤����Ļ������ֺ�λ����У
	 * 
	 * @param idcard
	 * @return
	 */
	public boolean is18Idcard(String idcard) {
		return Pattern.matches("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([\\d|x|X]{1})$", idcard);
	}

	/**
	 * ������֤
	 * 
	 * @param str
	 * @return
	 */
	public boolean isDigital(String str) {
		return str == null || "".equals(str) ? false : str.matches("^[0-9]*$");
	}

	/**
	 * �����֤��ÿλ�Ͷ�Ӧλ�ļ�Ȩ�������֮���ٵõ���ֵ
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
	 * ����ֵ��11ȡģ�õ���������У�����ж�
	 * 
	 * @param checkCode
	 * @param sum17
	 * @return У��λ
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
	 * ���ַ�����תΪ��������
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
		// ��ȡ�Ա�
		String id17 = idcard.substring(16, 17);
		if (Integer.parseInt(id17) % 2 != 0) {
			return "��";
		} else {
			return "Ů";
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
			//������ж���ǰ�����⣬ÿ�ζ�������0�����޸�֮�����/2019-03-11��
			Date birthdate = new SimpleDateFormat("yyyy-MM-dd").parse(birthday);
			GregorianCalendar currentDay = new GregorianCalendar();
			currentDay.setTime(birthdate);
			int birthyear = currentDay.get(Calendar.YEAR);
			String toyear = currdate.substring(0, 4);
			int yearlogic = Integer.parseInt(toyear) - birthyear;
			Date tobirthdate = new SimpleDateFormat("yyyy-MM-dd").parse(toyear + birthday.substring(4));
			Date currdate_ = new SimpleDateFormat("yyyy-MM-dd").parse(currdate);
			if (currdate_.getTime() > tobirthdate.getTime()) { // �������������
				Date nextbirthdate = new SimpleDateFormat("yyyy-MM-dd").parse((Integer.parseInt(toyear) + 1) + birthday.substring(4));
				if (isup) { // �㹤��
					age = (new BigDecimal(yearlogic).add(new BigDecimal(1))).doubleValue();
				} else {
					age = new BigDecimal(yearlogic).add(new BigDecimal(currdate_.getTime() - tobirthdate.getTime()).divide(new BigDecimal(nextbirthdate.getTime() - tobirthdate.getTime()), 2, BigDecimal.ROUND_HALF_DOWN)).doubleValue();
				}
			} else { // ��û��
				Date oldbirthdate = new SimpleDateFormat("yyyy-MM-dd").parse((Integer.parseInt(toyear) - 1) + birthday.substring(4));
				if (isup) { // �㹤��
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
	 * �����µ�����
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
	 * ��׼��
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
