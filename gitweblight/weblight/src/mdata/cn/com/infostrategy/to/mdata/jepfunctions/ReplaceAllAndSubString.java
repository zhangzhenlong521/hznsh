/**************************************************************************
 * $RCSfile: ReplaceAllAndSubString.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;

public class ReplaceAllAndSubString extends PostfixMathCommand {

	public ReplaceAllAndSubString() {
		numberOfParameters = 3;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		Object param_3 = inStack.pop();
		String str_param;
		String str_regex;
		String str_replace;
		if (param_3 != null) {
			str_param = (String) param_3;
		} else {
			throw new ParseException("Invalid parameter string");
		}
		if (param_2 != null) {
			str_regex = (String) param_2;
		} else {
			throw new ParseException("Invalid parameter regx");
		}
		if (param_1 != null) {
			str_replace = (String) param_1;
		} else {
			throw new ParseException("Invalid parameter replacement");
		}
		if (str_param != null && str_param.startsWith(str_regex)) {//取数据时经常取出id串，如";124;445;444;",前后都一个分号，一般情况是要拼成in机制，即"124,445,444"的格式，故这里进行了修改【李春娟/2012-06-21】
			str_param = str_param.substring(1);
		}
		str_param = new TBUtil().replaceAll(str_param, str_regex, str_replace);
		inStack.push(str_param.substring(0, str_param.length() - 1));
	}
}

/*******************************************************************************
 * $RCSfile: ReplaceAllAndSubString.java,v $ $Revision: 1.5 $ $Date: 2012/09/14 09:22:56 $
 * 
 * $Log: ReplaceAllAndSubString.java,v $
 * Revision 1.5  2012/09/14 09:22:56  xch123
 * 邮储现场回来统一修改
 *
 * Revision 1.1  2012/08/28 09:40:55  Administrator
 * *** empty log message ***
 *
 * Revision 1.4  2012/06/21 07:28:54  lichunjuan
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
 * Revision 1.3  2010/02/08 11:01:53  sunfujun
 * *** empty log message ***
 *
 * Revision 1.1  2009/11/03 10:12:50  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2009/02/20 06:10:31  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.3  2009/02/19 07:30:59  wangjian
 * *** empty log message ***
 *
 * Revision 1.2  2008/12/09 13:08:05  xuchanghua
 * *** empty log message ***
 *
 * Revision 1.1  2008/12/08 02:55:46  wangjian
 * *** empty log message ***
 * Revision 1.1 2008/07/24 09:31:26 xuchanghua ***
 * empty log message ***
 * 
 * Revision 1.1 2008/06/27 14:47:10 xuchanghua *** empty log message ***
 * 
 * Revision 1.1 2008/02/19 13:28:15 xuchanghua *** empty log message ***
 * 
 * Revision 1.2 2008/01/17 02:48:23 xch *** empty log message ***
 * 
 * Revision 1.1 2007/09/21 02:28:33 xch *** empty log message ***
 * 
 * Revision 1.3 2007/09/20 05:08:28 xch *** empty log message ***
 * 
 * Revision 1.1 2007/03/07 02:04:18 shxch *** empty log message ***
 * 
 * Revision 1.2 2007/01/30 04:59:23 lujian *** empty log message ***
 * 
 * 
 ******************************************************************************/
