package com.pushworld.ipushgrc.ui;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 模仿C#中的string.format()方法，　格式化字符串。　最大的好处体现在拼SQL语句时
 * 		String sql = "insert into pub_user(id, name, code, sex, old) values({0}, '{1}', '{1}', '{2}', {3})";
 *		sql = StringFormat.formatSQL(sql, 3, "wang", "gang", "男", 18);
 *		System.out.println(sql);
 *		返回结果：　insert into pub_user(id, name, code, sex, old) values(3, 'wang', 'wang', '男', 18)
 * @author Gwang
 *
 */
public class StringFormat {
	
	public static String formatString(String str, Object[] args) {
		//这里用于验证数据有效性
		if (str == null || "".equals(str))
			return "";
		if (args.length == 0) {
			return str;
		}

		String result = str;
		String value = "";

		//这里的作用是只匹配{}里面是数字的子字符串
		java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\{(\\d+)\\}");
		java.util.regex.Matcher m = p.matcher(str);

		while (m.find()) {
			//获取{}里面的数字作为匹配组的下标取值
			int index = Integer.parseInt(m.group(1));

			if (index < args.length) {
				//替换，以{}数字为下标，在参数数组中取值
				value = convertSQLValue(args[index]);
				result = result.replace(m.group(), value);
			}
		}
		return result;
		
	}

	//字符串合并方法，返回一个合并后的字符串
	public static String formatSQL(String str, Object... args) {
		//这里用于验证数据有效性
		if (str == null || "".equals(str))
			return "";
		
		if (args.length == 0) {
			return str;
		}
		
		Object[] objs = new Object[args.length];		
		for(int i=0;i<args.length;i++)
		{
			objs[i] = args[i];
		}
		return StringFormat.formatString(str, objs);		
	}
	
	/**
	 * 替换SQL中的单引号,因为单引号会导致保存失败!!
	 * @param _value
	 * @return
	 */
	private static String convertSQLValue(Object _value) {
		if (_value == null) {
			return "null";
		} else {
			String val = _value.toString();
			TBUtil tbUtil = new TBUtil(); //
			val = tbUtil.replaceAll(val, "'", "''"); //
			val = tbUtil.replaceAll(val, "\\", "\\\\"); //
			return val; //
		}
	}

	public static void main(String[] args) {
		String sql = "insert into pub_user(id, name, code, sex, old) values({0}, '{1}', '{1}', '{2}', {3})";
		String s = StringFormat.formatSQL(sql, 3, "wang", "'男", 18);
		System.out.println(s);

		String s1 = StringFormat.formatString("({0}+{1})/{1}", new Object[]{3, 18});
		System.out.println(s1);
	}
}
