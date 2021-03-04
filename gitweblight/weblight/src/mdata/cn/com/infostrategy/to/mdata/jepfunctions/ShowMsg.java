package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ����һ����ʾ
 * 
 */
public class ShowMsg extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public ShowMsg(BillPanel _billPanel) {
		numberOfParameters = 1;
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String msg = (String) param_1;
		if (msg != null && !msg.trim().equals("")) {
			MessageBox.show(billPanel, msg);
		}
		inStack.push("ok");
	}
}
