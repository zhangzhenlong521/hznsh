package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

public class GetEditState extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public GetEditState(BillPanel _billPanel) {
		numberOfParameters = 0; //不需要传参
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //检查堆栈
		if (billPanel instanceof BillCardPanel) {
			String state = ((BillCardPanel) billPanel).getEditState();
			inStack.push(state.toUpperCase()); //置入堆栈!!返回状态
		} else {
			inStack.push("");
		}
	}
}
