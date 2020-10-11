package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * 取得面板中的某一项的值!!
 * 该函数在客户端与服务器端都会使用!!!
 * @author xch
 *
 */
public class ExecWLTAction extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;
	private WLTButton wltButton = null;

	/**
	 * 取得某一项的值!!
	 */
	public ExecWLTAction(BillPanel _billPanel, WLTButton _btn) {
		numberOfParameters = 1; ////只有一个参数，但这个参数以后可以有许多变化，比如点操作，前面还可以加上是页面上哪个部分的索引号，从而实现组件之间的连动!!
		this.billPanel = _billPanel;
		this.wltButton = _btn; //
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		checkStack(inStack);
		String str_wltactionclassname = (String) inStack.pop(); ////
		try {
			WLTActionListener listener = (WLTActionListener) Class.forName(str_wltactionclassname).newInstance(); //
			WLTActionEvent event = new WLTActionEvent(wltButton, billPanel); //
			listener.actionPerformed(event); //
			inStack.push("ok"); //
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new WLTAppException(e.getMessage())); //返回一个异常
		}
	}

}
