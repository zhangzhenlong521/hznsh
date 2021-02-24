package cn.com.infostrategy.bs.workflow.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * ����������������·�ɱ��!!
 * @author xch
 *
 */
public class GetWFBillItemValue extends PostfixMathCommand {

	private BillVO billVO = null; //

	/**
	 * ���췽��..
	 * @param _dealpoolid 
	 * @param _prinstanceid 
	 * @param _callvo 
	 * @param __billvo 
	 */
	public GetWFBillItemValue(WFParVO _callvo, String _prinstanceid, String _dealpoolid, BillVO _billvo) {
		numberOfParameters = 1;
		billVO = _billvo; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_key = (String) inStack.pop(); //
		inStack.push(billVO.getStringValue(str_key)); //
	}

	
}
