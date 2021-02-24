package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

/**
 * 重新加载某一项的内容!!!目前只支持下拉框与参照1!!,因为经常遇到下拉框之间数据连动，所以需要重新刷新别一个下拉框中的内容!!
 * @author xch
 *
 */
public class ReloadItemValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	private int li_row = -1; //

	/**
	 * 取得某一项的值!!
	 */
	public ReloadItemValue(BillPanel _billPanel, int _row) {
		numberOfParameters = 1; //有一个参数,即ItemKey
		this.billPanel = _billPanel;
		this.li_row = _row; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_itemKey = (String) param_1; //

		if (billPanel instanceof BillCardPanel) { //如果是卡片面板
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;  //
		} else if (billPanel instanceof BillPropPanel) {

		}
		inStack.push("ok"); //因为设置值，没有实际返回值，所以就返回一个"ok"表示赋值成功了!!
	}
}
