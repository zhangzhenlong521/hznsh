package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;

public class GetStringItemVO extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public GetStringItemVO(BillPanel _billPanel) {
		numberOfParameters = 1; //只有一个参数，但这个参数以后可以有许多变化，比如点操作，前面还可以加上是页面上哪个部分的索引号，从而实现组件之间的连动!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //检查堆栈
		Object param_1 = inStack.pop(); //
		String str_value = (String) param_1; ////
		inStack.push(new StringItemVO(str_value)); //置入堆栈!!返回StringItemVO
	}

}
