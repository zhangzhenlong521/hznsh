package cn.com.infostrategy.to.mdata.jepfunctions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class GetDateDifference extends PostfixMathCommand {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //

	public GetDateDifference() {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_2 = inStack.pop(); //第二个参数
		Object param_1 = inStack.pop(); //第一个参数

		String str_olddate = String.valueOf(param_1); //第二个值可能是字符串或数字!!系统时间
		//	double ld_addvalue = ((Double) param_2).doubleValue(); //		
		String str_newdate = String.valueOf(param_2); //第一个值可能是字符串或数字!!传进来的时间

		if (str_newdate == null || str_newdate.trim().equals("") || str_olddate == null || str_olddate.trim().equals("")) {
			return;
		}
		int str_return = 0;
		try {
			Date date = sdf.parse(str_olddate);
			Date newdate = sdf.parse(str_newdate);
			//			GregorianCalendar calendar = new GregorianCalendar(); //
			//			GregorianCalendar newcalendar = new GregorianCalendar(); //
			//			calendar.setTime(date); //设置日期
			//			newcalendar.setTime(newdate); //设置日期
			int difference_year = date.getYear() - newdate.getYear();
			//	System.out.println("ddddddddd=="+(date.getDate()-newdate.getDate()));
			int difference_month = 0;
			int difference_day = 0;
			if (difference_year >= 0) {
				difference_month = difference_year * 12 + date.getMonth() - newdate.getMonth();
				difference_day = date.getDate() - newdate.getDate();
				str_return = difference_month * 30 + difference_day;
			} else {
				difference_month = -1;
				str_return = difference_month;
			}

			//			System.out.println("difference=======" + str_return);
			//	calendar.add(Calendar.DAY_OF_MONTH, (int) ld_addvalue); //增加

		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		inStack.push(str_return);
	}
}
