package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

/**
 * 清空页面所有值
 * @author xch
 *
 */
public class ResetAllItemValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public ResetAllItemValue(BillPanel _billPanel) {
		numberOfParameters = 0; //没有参数
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);

		if (billPanel instanceof BillCardPanel) { //如果是卡片面板
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			cardPanel.reset(); //
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			listPanel.reset(listPanel.getSelectedRow()); //
		} else if (billPanel instanceof BillPropPanel) {

		}

		inStack.push("ok"); //因为没有实际返回值，所以放一个"ok"表示操作成功!!!!!
	}
}
