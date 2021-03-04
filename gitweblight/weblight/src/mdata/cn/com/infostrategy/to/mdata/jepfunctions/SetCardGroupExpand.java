package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

/**
 * 设置某一项是否可编辑!!
 * @author xch
 *
 */
public class SetCardGroupExpand extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public SetCardGroupExpand(BillPanel _billPanel) {
		numberOfParameters = 2; //有两个参数，其中第一个是ItemKey,第二个是"true"/"false"
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_grouptitle = (String) param_2; //可以是一组,以逗号隔开!!
		String str_editable = (String) param_1; //第二个值,应该是"true"/"false"

		if (billPanel instanceof BillCardPanel) { //如果是卡片面板
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			if (str_editable.trim().equalsIgnoreCase("true")) { //如果是true,则可编辑
				if (str_grouptitle != null && !str_grouptitle.trim().equals("")) {
					str_grouptitle = str_grouptitle.trim();
					String[] str_allgroups = str_grouptitle.split(",");
					for (int i = 0; i < str_allgroups.length; i++) {
						cardPanel.setGroupExpandable(str_allgroups[i], true); //
					}
				}
			} else if (str_editable.trim().equalsIgnoreCase("false")) { //如果是"false"，则不可编辑!!!!
				if (str_grouptitle != null && !str_grouptitle.equals("")) {
					str_grouptitle = str_grouptitle.trim();
					String[] str_allgroups = str_grouptitle.split(",");
					for (int i = 0; i < str_allgroups.length; i++) {
						cardPanel.setGroupExpandable(str_allgroups[i], false); //
					}
				}
			} else { //如果不小心输了别的参数，则啥都不干!!
			}
		} else if (billPanel instanceof BillListPanel) {
		} else if (billPanel instanceof BillPropPanel) {
		}

		inStack.push("ok"); //因为设置值，没有实际返回值，所以就返回一个"ok"表示赋值成功了!!
	}
}
