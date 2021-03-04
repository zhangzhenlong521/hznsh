package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ȡ�����������ݶ���!!!
 * @author xch
 *
 */
public class GetComBoxItemVO extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetComBoxItemVO(BillPanel _billPanel) {
		numberOfParameters = 3; //��������
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //����ջ
		Object param_1 = inStack.pop(); //
		Object param_2 = inStack.pop(); //
		Object param_3 = inStack.pop(); //

		String str_id = (String) param_3; ////
		String str_code = (String) param_2; ////
		String str_name = (String) param_1; ////

		inStack.push(new ComBoxItemVO(str_id, str_code, str_name)); //�����ջ!!���� ComBoxItemVO
	}

}
