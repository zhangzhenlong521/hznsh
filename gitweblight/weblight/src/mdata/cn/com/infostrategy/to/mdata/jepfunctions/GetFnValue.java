/**************************************************************************
 * $RCSfile: GetFnValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;


import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetFnValue extends PostfixMathCommand {

	private String str_function;

	private int li_type = -1;

	public GetFnValue() {// 这个参数必须要有，，，TNND，搞了半天，真是太不爽了.
		numberOfParameters = -1;
	}

	public GetFnValue(int _type) {
		numberOfParameters = -1;
		li_type = _type;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);

		String[] str_pa = new String[curNumberOfParameters - 1];
		for (int i = str_pa.length - 1; i >= 0; i--) { //倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}
		Object obj_fn = inStack.pop();//最后获得函数名

		if (obj_fn != null) {
			str_function = (String) obj_fn;
		} else {
			throw new ParseException("Invalid parameter function name");
		}
		String str_data = null;
		try {
			if (li_type == WLTConstants.JEPTYPE_UI) { // 如果是客户端调用
				str_data = UIUtil.callFunctionByReturnVarchar(null, str_function, str_pa);
			} else if (li_type == WLTConstants.JEPTYPE_BS) { // 如果是Server端调用
				str_data = ServerEnvironment.getCommDMO().callFunctionReturnStrByDS(null, str_function, str_pa);
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		inStack.push(str_data); //
	}
}
/**************************************************************************
 * $RCSfile: GetFnValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 *
 * $Log: GetFnValue.java,v $
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
 * Revision 1.3  2008/12/29 09:03:50  xuchanghua
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
 * Revision 1.2  2007/11/13 05:57:58  xch
 * *** empty log message ***
 *
 * Revision 1.1  2007/09/21 02:28:33  xch
 * *** empty log message ***
 *
 * Revision 1.3  2007/09/20 05:08:28  xch
 * *** empty log message ***
 *
 * Revision 1.2  2007/03/07 02:25:01  shxch
 * *** empty log message ***
 *
 * Revision 1.1  2007/03/07 02:04:18  shxch
 * *** empty log message ***
 *
 * Revision 1.5  2007/03/02 05:16:43  shxch
 * *** empty log message ***
 *
 * Revision 1.4  2007/03/02 05:02:50  shxch
 * *** empty log message ***
 *
 * Revision 1.3  2007/03/01 07:20:47  shxch
 * *** empty log message ***
 *
 * Revision 1.2  2007/01/30 04:59:24  lujian
 * *** empty log message ***
 *
 *
 **************************************************************************/
