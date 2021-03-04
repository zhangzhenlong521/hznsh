/**************************************************************************
 * $RCSfile: IndexOf.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class IndexOf extends PostfixMathCommand {
	private char ch_index = 0;

	private String str_index = null;

	public IndexOf() {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str_par = "" + inStack.pop();
		String str_text = "" + inStack.pop();
		if ("".equals(str_par)) {
			inStack.push(-1);
		} else {
			inStack.push(new Integer(str_text.indexOf(str_par))); //
		}
	}
}

/**************************************************************************
 * $RCSfile: IndexOf.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: IndexOf.java,v $
 * Revision 1.4  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:55  Administrator
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
 * Revision 1.1  2009/02/20 06:10:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/02/19 07:30:58  wangjian
 * *** empty log message ***
 *
 * Revision 1.2  2008/12/09 13:08:05  xuchanghua
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
 * Revision 1.3  2007/09/20 05:08:27  xch
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
