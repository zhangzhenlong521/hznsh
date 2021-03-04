/**************************************************************************
 * $RCSfile: AfterDate.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 指定某一天的往后几天!!!比如AfterDate("2007-07-01",3)就返回"2007-07-03"
 * 第二个参数支持负数,即表示往前几天!!
 * @author xch
 *
 */
public class AfterDate extends PostfixMathCommand {

	private Date d_curr = null;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //

	public AfterDate() {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_2 = inStack.pop(); //第二个参数
		Object param_1 = inStack.pop(); //第一个参数

		String str_olddate = String.valueOf(param_1); //第二个值可能是字符串或数字!!
		double ld_addvalue = ((Double) param_2).doubleValue(); //		

		String str_return = "";
		try {
			Date date = sdf.parse(str_olddate);
			GregorianCalendar calendar = new GregorianCalendar(); //
			calendar.setTime(date); //设置日期
			calendar.add(Calendar.DAY_OF_MONTH, (int) ld_addvalue); //增加
			str_return = sdf.format(calendar.getTime()); //
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}

		inStack.push(str_return);
	}
}

/**************************************************************************
 * $RCSfile: AfterDate.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: AfterDate.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:53  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:42  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:07  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:00  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:28  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:09  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/02/19 13:28:15  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/01/17 02:48:23  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/10/09 03:03:10  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:33  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:28  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/07 02:47:23  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 02:04:19  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:59:23  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
