package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 取得参照数据对象!!!
 * @author xch
 *
 */
public class GetNotNullItemCount extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public GetNotNullItemCount(BillPanel _billPanel) {
		numberOfParameters = -1;
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String[] str_pa = new String[curNumberOfParameters];
		for (int i = str_pa.length - 1; i >= 0; i--) {//倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}
		if (billPanel instanceof BillCardPanel) {
			int li_count = 0;
			BillCardPanel card = (BillCardPanel) billPanel; //
			for (int i = 0; i < str_pa.length; i++) { //
				Object obj_value = card.getValueAt(str_pa[i]); //
				if (obj_value == null || obj_value.toString().trim().equals("")) { //
				} else {
					li_count++; //
				}
			}
			inStack.push(new Integer(li_count)); //
		}

	}
}
