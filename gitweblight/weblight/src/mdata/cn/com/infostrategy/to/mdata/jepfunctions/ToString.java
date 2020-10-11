/**************************************************************************
 * $RCSfile: ToString.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class ToString extends PostfixMathCommand {

	public ToString() {
		numberOfParameters = 1;
	}

	public ToString(int par_count) {
		numberOfParameters = par_count;
	}

	public void run(Stack inStack) throws ParseException {
		String str_return = "";
		checkStack(inStack); //
		Object param_1 = inStack.pop();
		if (param_1 instanceof Double) {
			Double ld_value = (Double) param_1;
			if (ld_value.intValue() == ld_value.doubleValue()) {
				str_return = "" + ld_value.intValue();
			} else {
				str_return = "" + param_1;
			}

		} else {
			str_return = "" + param_1;
		}
		inStack.push(str_return); //

	}

}
/**************************************************************************
 * $RCSfile: ToString.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: ToString.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:56  Administrator
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
 * Revision 1.2  2009/02/19 07:30:59  wangjian
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
 * Revision 1.2  2007/10/09 03:03:10  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:34  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:27  xch
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
