package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 取得下拉框数据对象!!!
 * @author xch
 *
 */
public class GetComBoxItemVO extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public GetComBoxItemVO(BillPanel _billPanel) {
		numberOfParameters = 3; //三个参数
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //检查堆栈
		Object param_1 = inStack.pop(); //
		Object param_2 = inStack.pop(); //
		Object param_3 = inStack.pop(); //

		String str_id = (String) param_3; ////
		String str_code = (String) param_2; ////
		String str_name = (String) param_1; ////

		inStack.push(new ComBoxItemVO(str_id, str_code, str_name)); //置入堆栈!!返回 ComBoxItemVO
	}

}
