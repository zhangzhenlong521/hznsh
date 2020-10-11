package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * 取得面板中的某一项的值!!
 * 该函数在客户端与服务器端都会使用!!!
 * @author xch
 *
 */
public class GetCellEditingItemNumberValue extends PostfixMathCommand {

	//UI传入的参数
	private BillPanel billPanel = null;
	private String editingValue = null; //

	/**
	 * 取得某一项的值!!
	 */
	public GetCellEditingItemNumberValue(BillCellPanel _billPanel, String _editingValue) {
		numberOfParameters = 0; //没有参数...
		this.billPanel = _billPanel;
		this.editingValue = _editingValue; //
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //校验..
		if (this.editingValue == null || this.editingValue.trim().equals("")) { //如果值为空,则送入0..
			inStack.push(new Double(0)); //
		} else {
			inStack.push(new Double(this.editingValue)); //
		}
	}

}
