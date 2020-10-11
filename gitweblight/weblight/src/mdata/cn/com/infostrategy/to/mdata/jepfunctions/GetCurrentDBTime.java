/**************************************************************************
 * $RCSfile: GetCurrentDBTime.java,v $  $Revision: 1.5 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.UIUtil;

public class GetCurrentDBTime extends PostfixMathCommand {
	private int li_type = -1;
	private TBUtil tbUtil = null; //

	private GetCurrentDBTime() {// 这个参数必须要有，，，TNND，搞了半天，真是太不爽了.
		numberOfParameters = 0;
	}

	public GetCurrentDBTime(int _type) {// 这个参数必须要有，，，TNND，搞了半天，真是太不爽了.
		numberOfParameters = 0;
		li_type = _type; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str_currTime = ""; //
		try {
			if (li_type == WLTConstants.JEPTYPE_UI) { //如果是客户端调用该公式!!
				str_currTime = UIUtil.getServerCurrTime(); //
			} else if (li_type == WLTConstants.JEPTYPE_BS) { //如果是服务器端调用该公式!!
				str_currTime = getTBUtil().getCurrTime(); //
			} else {
				str_currTime = getTBUtil().getCurrTime(); //
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		inStack.push(str_currTime); //
	}

	private TBUtil getTBUtil() {
		if (tbUtil != null) {
			return tbUtil; //
		}
		tbUtil = new TBUtil(); //
		return tbUtil; //
	}
}
