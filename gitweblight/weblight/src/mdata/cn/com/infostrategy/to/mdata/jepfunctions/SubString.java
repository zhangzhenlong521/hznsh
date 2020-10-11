/**************************************************************************
 * $RCSfile: SubString.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class SubString extends PostfixMathCommand {

	private int li_begin = 0;

	private int li_end = 0;

	public SubString() {
		numberOfParameters = 3;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		Object param_3 = inStack.pop();
		String str_param;
		if (param_3 != null) {
			str_param = (String) param_3;
		} else {
			throw new ParseException("Invalid parameter string");
		}
		String str_result;
		if (param_2 instanceof Double) {
			li_begin = ((Double) param_2).intValue();
			// //
			if (li_begin < 0) {
				throw new ParseException("Invalid parameter beginIndex");
			}
			if (param_1 == null) {
				str_result = str_param.substring(li_begin);
			} else if (param_1 instanceof Double) {
				li_end = ((Double) param_1).intValue();
				if (li_end < li_begin) {
					throw new ParseException("Invalid parameter endIndex");
				}
				str_result = str_param.substring(li_begin, li_end);
			} else if (param_1 instanceof Integer) {//因为公式indexOf会返回Integer类型，故这里增加Integer类型【李春娟/2013-05-30】
				li_end = ((Integer) param_1).intValue();
				if (li_end < li_begin) {
					throw new ParseException("Invalid parameter endIndex");
				}
				str_result = str_param.substring(li_begin, li_end);
			} else {
				throw new ParseException("Invalid parameter endIndex");
			}
			inStack.push(str_result);
		} else {
			throw new ParseException("Invalid parameter beginIndex");
		}
	}
}
/**************************************************************************
 * $RCSfile: SubString.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: SubString.java,v $
 * Revision 1.5  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:56  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2011/10/10 06:31:43  wanggang
 * restore
 *
 * Revision 1.2  2011/08/22 11:49:57  lichunjuan
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/17 10:23:08  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/05/05 11:31:56  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2010/04/08 04:33:02  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2010/02/08 11:01:54  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:32  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.4  2009/02/19 07:30:58  wangjian
 * *** empty log message ***
 *
 * Revision 1.3  2008/12/26 02:42:20  wangning
 * *** empty log message ***
 *
 * Revision 1.1  2008/07/24 09:31:26  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/06/27 14:47:10  xuchanghua
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
 * Revision 1.1  2007/03/07 02:04:18  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:59:24  lujian
 * *** empty log message ***
 *
 *
**************************************************************************/
