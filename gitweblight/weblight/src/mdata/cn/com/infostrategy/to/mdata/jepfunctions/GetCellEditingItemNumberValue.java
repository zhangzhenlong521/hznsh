package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * ȡ������е�ĳһ���ֵ!!
 * �ú����ڿͻ�����������˶���ʹ��!!!
 * @author xch
 *
 */
public class GetCellEditingItemNumberValue extends PostfixMathCommand {

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private String editingValue = null; //

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetCellEditingItemNumberValue(BillCellPanel _billPanel, String _editingValue) {
		numberOfParameters = 0; //û�в���...
		this.billPanel = _billPanel;
		this.editingValue = _editingValue; //
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //У��..
		if (this.editingValue == null || this.editingValue.trim().equals("")) { //���ֵΪ��,������0..
			inStack.push(new Double(0)); //
		} else {
			inStack.push(new Double(this.editingValue)); //
		}
	}

}
