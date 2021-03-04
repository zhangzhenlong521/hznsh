package cn.com.infostrategy.bs.common;

import java.util.Random;

/**
 * 
 * This class delivers some simple functionality for
 * CharSequence(String,StringBuffer,StringBuilder) the class is writting. from
 * spring source
 * 
 * 
 */
public final class WLTStringUtils {

	public static final String COMMA = ",";
	public static final String VERTICAL = "|";
	/**
	 * set image source separator. from www or local file.
	 */
	public static final String INTERNET_PREFIX_HTTP = "http://";
	/**
	 * set image source separator. from www or local file.
	 */
	public static final String INTERNET_PREFIX_HTTPS = "https://";

	// file attribute constat utility

	public static final String FOLDER_SEPARATOR = "/";

	public static final String WINDOWS_FOLDER_SEPARATOR = "\\";

	public static final String TOP_PATH = "..";

	public static final String CURRENT_PATH = ".";

	public static final char EXTENSION_SEPARATOR = '.';

	public static final String UNDERLINE = "_";

	/**
	 * Check that the given CharSequence is neither null nor of length 0.
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * Check that the given String is neither <code>null</code> nor of length 0.
	 */
//	public static boolean hasLength(String str) {
//		return hasLength((CharSequence) str);
//	}

	/**
	 * Check whether the given CharSequence has actual text.
	 */
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given String has actual text.
	 */
	public static boolean hasText(String str) {
		return hasText((CharSequence) str);
	}

	/**
	 * Check whether the given CharSequence contains any whitespace characters.
	 */
	public static boolean containsWhitespace(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check whether the given String contains any whitespace characters.
	 */
	public static boolean containsWhitespace(String str) {
		return containsWhitespace((CharSequence) str);
	}

	/**
	 * Trim all whitespace from the given String: leading, trailing, and
	 * inbetween characters.
	 */
	public static String trimAllWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		int index = 0;
		while (buf.length() > index) {
			if (Character.isWhitespace(buf.charAt(index))) {
				buf.deleteCharAt(index);
			} else {
				index++;
			}
		}
		return buf.toString();
	}

	/**
	 * Trim leading whitespace from the given String.
	 */
	public static String trimLeadingWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && Character.isWhitespace(buf.charAt(0))) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Trim trailing whitespace from the given String.
	 */
	public static String trimTrailingWhitespace(String str) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0
				&& Character.isWhitespace(buf.charAt(buf.length() - 1))) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	/**
	 * Trim all occurences of the supplied leading character from the given
	 * String.
	 */
	public static String trimLeadingCharacter(String str, char leadingCharacter) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0 && buf.charAt(0) == leadingCharacter) {
			buf.deleteCharAt(0);
		}
		return buf.toString();
	}

	/**
	 * Trim all occurences of the supplied trailing character from the given
	 * String.
	 */
	public static String trimTrailingCharacter(String str,
			char trailingCharacter) {
		if (!hasLength(str)) {
			return str;
		}
		StringBuffer buf = new StringBuffer(str);
		while (buf.length() > 0
				&& buf.charAt(buf.length() - 1) == trailingCharacter) {
			buf.deleteCharAt(buf.length() - 1);
		}
		return buf.toString();
	}

	/**
	 * Test if the given String starts with the specified prefix, ignoring
	 * upper/lower case.
	 */
	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	/**
	 * Test if the given String ends with the specified suffix, ignoring
	 * upper/lower case.
	 */
	public static boolean endsWithIgnoreCase(String str, String suffix) {
		if (str == null || suffix == null) {
			return false;
		}
		if (str.endsWith(suffix)) {
			return true;
		}
		if (str.length() < suffix.length()) {
			return false;
		}

		String lcStr = str.substring(str.length() - suffix.length())
				.toLowerCase();
		String lcSuffix = suffix.toLowerCase();
		return lcStr.equals(lcSuffix);
	}

	/**
	 * Test whether the given string matches the given substring at the given
	 * index.
	 */
	public static boolean substringMatch(CharSequence str, int index,
			CharSequence substring) {
		for (int j = 0; j < substring.length(); j++) {
			int i = index + j;
			if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Count the occurrences of the substring in string s
	 */
	public static int countOccurrencesOf(String str, String sub) {
		if (str == null || sub == null || str.length() == 0
				|| sub.length() == 0) {
			return 0;
		}
		int count = 0, pos = 0, idx = 0;
		while ((idx = str.indexOf(sub, pos)) != -1) {
			++count;
			pos = idx + sub.length();
		}
		return count;
	}

	/**
	 * 
	 * from array to delimited String.
	 */
	public static String arrayByDelimitedString(Object[] arr, String delim) {
		if (ObjectUtils.isEmpty(arr)) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	public static Long[] StringToLongArray(String str) {
		if (str != null && str.length() > 0) {
			String[] targetIdsArray = str.split(WLTStringUtils.COMMA);
			int arrayLength = targetIdsArray.length;
			Long[] targetIdsLongArray = new Long[arrayLength];
			for (int i = 0; i < arrayLength; i++) {
				targetIdsLongArray[i] = Long.parseLong(targetIdsArray[i]);
			}
			return targetIdsLongArray;
		} else {
			return null;
		}
	}
	
	/**
	 * 
	 * @param _time
	 * @param status 0取开始, 1取区间, 2结束
	 * @return
	 */
	public static String convertDateTimeFormat(String _time, int status){
		if(!hasText(_time)){
			return null;
		}
		
		String result = "";
		if(_time.split(";").length==1){
			result = convertDateToString(_time, status);
		}else if(_time.split(";").length>1){
			String temp = _time.split(";")[0]+";";
			result+=convertDateToString(temp, 0);
			temp = _time.split(";")[1]+";";
			result+=(";"+convertDateToString(temp, 2));
		}
		
		return result;
	}
	
	/**
	 * 
	 * @param _time
	 * @param status 0取开始, 1取区间, 2结束
	 * @return
	 */
	private static String convertDateToString(String _time, int status){
		
		String begin = "";
		String end = "";
		String year = _time.substring(0, 4);
		
		if (_time.endsWith("年;")) { // 格式："2008年;"
			begin = year+"-01-01";
			end = year+"-12-31";
		} else if (_time.endsWith("季度;")) { // 格式："2008年1季度;"
			String quarter = _time.substring(5, 6);
			String month1 = Integer.parseInt(quarter) * 3 - 2 + "";
			String month2 = Integer.parseInt(quarter) * 3 + 0 + "";
			if (month1.length() == 1) {
				begin = year + "-0" + month1+"-01";
			} else {
				begin = year + "-" + month1+"-01";
			}
			if (month2.length() == 1) {
				end = year + "-0" + month2 + getDayByMonth(year, month2);
			} else {
				if(Integer.parseInt(month2)>12){
					end = Integer.parseInt(year) + "-12-31";
				}else{
					end = year + "-" + month2 + getDayByMonth(year, month2);
				}
			}
		} else if (_time.endsWith("月;")) { // 格式："2011年02月;"
			String month1 = _time.substring(5, 7);
			String month2 = month1;
			if (month2.length() == 1) {
				month2 = "0" + month2;
			}
			begin = year + "-" + month1+"-01";
			end = year + "-" + month2 + getDayByMonth(year, month2);
			if ("12".equals(month1)) {
				end = year + "-12-31";
			}
		} else if (_time.endsWith(";")) { // 格式为："2008-01-01;"
			begin = _time.substring(0, 10);
			end = begin;
		} else { // 格式为："2008-01-01;2008-02-01"
			return _time;
		}
		
		if(0==status){
			return begin;
		}else if(1==status){
			return begin + ";" + end;
		}else{
			return end;
		}

	}
	
	private static String getDayByMonth(String year, String month){
		if(Integer.parseInt(month)<=7){
			if(Integer.parseInt(month)==2){
				if(Integer.parseInt(year)%4==0){
					return "-29";
				}else{
					return "-28";
				}
			}else if(Integer.parseInt(month)%2==0){
				return "-30";
			}else{
				return "-31";
			}
		}else{
			if(Integer.parseInt(month)%2==0){
				return "-31";
			}else{
				return "-30";
			}
		}
	}
	
	public static String dateYearPeriod(String finddate){
		String perion = "";
		if(!WLTStringUtils.hasText(finddate)){
			return perion;
		}
		
		String[] dateStr = finddate.split("-");
		perion = dateStr[0]+"年";
		return perion;
	}
	
	public static String dateQuarterPeriod(String finddate){
		String perion = "";
		if(!WLTStringUtils.hasText(finddate)){
			return perion;
		}
		
		String[] dateStr = finddate.split("-");
		int day = Integer.parseInt(dateStr[1]);
		perion = dateStr[0]+"年"+(day/3+(day%3==0?0:1))+"季度";
		return perion;
	}
	
	public static String dateMonthPeriod(String finddate){
		String perion = "";
		if(!WLTStringUtils.hasText(finddate)){
			return perion;
		}
		
		String[] dateStr = finddate.split("-");
		perion = dateStr[0]+"年"+dateStr[1]+"月";
		return perion;
	}
	
	public static String checkValue(String str) {
		String value="";
		
		if(str==null){
			value="N";
		}else if(str.equals("")){
			value="N";
		}else{
			value=str;
		}
		return value;
		
	}
	
	/**
	 * 取得随机三位数
	 * @return
	 */
	public static String getRandom(){
		StringBuffer sb_code = new StringBuffer("13579");
		StringBuffer sb = new StringBuffer();
		Random r = new Random();
		for (int i = 0; i < sb_code.length(); i++) {
			sb.append(sb_code.charAt(r.nextInt(sb_code.length())));
		}
		String str_code = sb.toString();
		return str_code;
	}
	
	/**
	 * 补全字符串 
	 * @param data
	 * @param length
	 * @return
	 */
	public static String fillingData(int data, int length){
		String temp = String.valueOf(data);
		String result = temp;
		for(int i=temp.length();i<length;i++){
			result = "0"+result;
		}
		return result;
		
	}
}
