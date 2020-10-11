/**************************************************************************
 * $RCSfile: GetCurrentDBDate.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

//取服务器端时间,以前是取数据医库,所以一直叫getDBDate
public class GetCurrentDBDate extends PostfixMathCommand {

	private int li_type = -1;
	private TBUtil tbUtil = null; //

	private GetCurrentDBDate() {// 这个参数必须要有，，，TNND，搞了半天，真是太不爽了.
		numberOfParameters = 0;
	}

	public GetCurrentDBDate(int _type) {
		numberOfParameters = 0;
		li_type = _type;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str_currDate = ""; //
		try {
			if (li_type == WLTConstants.JEPTYPE_UI) { //如果是客户端调用该公式!!则要远程调用!!
				str_currDate = UIUtil.getServerCurrDate(); //
			} else if (li_type == WLTConstants.JEPTYPE_BS) { //如果是服务器端调用该公式!!
				str_currDate = getTBUtil().getCurrDate(); //
			} else {
				str_currDate = getTBUtil().getCurrDate(); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		inStack.push(str_currDate); //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}
}
