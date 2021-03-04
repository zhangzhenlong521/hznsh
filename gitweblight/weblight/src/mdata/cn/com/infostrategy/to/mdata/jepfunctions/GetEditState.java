package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

public class GetEditState extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetEditState(BillPanel _billPanel) {
		numberOfParameters = 0; //����Ҫ����
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //����ջ
		if (billPanel instanceof BillCardPanel) {
			String state = ((BillCardPanel) billPanel).getEditState();
			inStack.push(state.toUpperCase()); //�����ջ!!����״̬
		} else {
			inStack.push("");
		}
	}
}
