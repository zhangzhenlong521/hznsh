/**************************************************************************
 * $RCSfile: GetCurrentTime.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class GetCurrentTime extends PostfixMathCommand {

	private Date d_curr = null;

	public static int DATE = 1;
	public static int TIME = 2;

	int li_type = 1;

	private GetCurrentTime() { //这个参数必须要有,TNND，搞了半天，真是太不爽了.
		numberOfParameters = 0;
	}

	public GetCurrentTime(int _type) { //
		numberOfParameters = 0;
		li_type = _type; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		d_curr = new Date();
		String str_format = "yyyy-MM-dd"; //
		if (li_type == GetCurrentTime.DATE) {
			str_format = "yyyy-MM-dd"; //
		} else if (li_type == GetCurrentTime.TIME) {
			str_format = "yyyy-MM-dd HH:mm:ss";
		}

		SimpleDateFormat sdf_curr = new SimpleDateFormat(str_format, Locale.SIMPLIFIED_CHINESE);
		String str_date = sdf_curr.format(d_curr);
		inStack.push(str_date);
	}
}
/**************************************************************************
 * $RCSfile: GetCurrentTime.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetCurrentTime.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:54  Administrator
 * *** empty log message ***
 *
 * Revision 1.3  2011/10/10 06:31:42  wanggang
 * restore
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:01  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:29  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2008/12/09 13:08:05  xuchanghua
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
 * Revision 1.1  2007/09/21 02:28:33  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:28  xch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/07 02:47:23  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 02:04:18  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:59:23  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
