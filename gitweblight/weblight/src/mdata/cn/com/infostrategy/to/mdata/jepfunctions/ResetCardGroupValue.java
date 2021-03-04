package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 清空面板某一组或多组的值!!只在BillCardPanel里使用！
 * @author xch
 *
 */
public class ResetCardGroupValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public ResetCardGroupValue(BillPanel _billPanel) {
		numberOfParameters = 1; //只有一个参数，组名用逗号分开
		this.billPanel = _billPanel; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_grouptitles = (String) param_1;

		if (str_grouptitles != null && !str_grouptitles.trim().equals("")) {
			String[] str_allgrouptitles = new TBUtil().split(str_grouptitles, ",");
			if (billPanel instanceof BillCardPanel) { //如果是卡片面板
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				for (int i = 0; i < str_allgrouptitles.length; i++) {
					cardPanel.resetByGrouptitle(str_allgrouptitles[i]);
				}
			}
		}
		inStack.push("ok"); //因为没有实际返回值，所以放一个"ok"表示操作成功!!!!!
	}
}
