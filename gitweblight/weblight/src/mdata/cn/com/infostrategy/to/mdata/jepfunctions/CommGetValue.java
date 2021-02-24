/**************************************************************************
 * $RCSfile: CommGetValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

/**
 * 通用的取数函数!
 * CODE=>commGetValue("xxx.formulafunction.TestFUN","myFn","{登录人员所属区域}","{局向}","{ID}");
 */
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class CommGetValue extends PostfixMathCommand {
	private String str_function;

	public CommGetValue() {
		numberOfParameters = -1;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);

		String[] str_pa = new String[curNumberOfParameters - 1];
		for (int i = str_pa.length - 1; i >= 2; i--) {// 倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}
		Object method_name = inStack.pop();// 方法名
		String class_name = (String) inStack.pop();// 类名
		if (method_name != null) {
			str_function = (String) method_name;
		} else {
			throw new ParseException("Invalid parameter function name");
		}
		Object result = null;
		try {
			result = Class.forName(class_name).getClass().getMethod(str_function, new Class[] { String[].class }).invoke(Class.forName(class_name).newInstance(), str_pa);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		inStack.push(result);
	}
}
/**************************************************************************
 * $RCSfile: CommGetValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: CommGetValue.java,v $
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
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:49  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.2  2009/02/23 07:49:42  xuchanghua
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
 * Revision 1.1  2007/09/21 02:28:32  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:28  xch
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
