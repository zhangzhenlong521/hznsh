package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * 取得面板中的某一项的值!!
 * @author xch
 *
 */
public class ResetItemValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * 取得某一项的值!!
	 */
	public ResetItemValue(BillPanel _billPanel) {
		numberOfParameters = 1; //只有一个参数，但这个参数以后可以有许多变化，比如点操作，前面还可以加上是页面上哪个部分的索引号，从而实现组件之间的连动!!
		this.billPanel = _billPanel; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_itemKey = (String) param_1;

		if (str_itemKey != null && !str_itemKey.trim().equals("")) {
			str_itemKey = str_itemKey.trim();
			String[] str_allitems = str_itemKey.split(",");
			if (billPanel instanceof BillCardPanel) { //如果是卡片面板
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				for (int i = 0; i < str_allitems.length; i++) {
					AbstractWLTCompentPanel panel = cardPanel.getCompentByKey(str_allitems[i]); //
					panel.reset(); //清空
					//panel.focus();  //
				}
			} else if (billPanel instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) billPanel;
				for (int i = 0; i < str_allitems.length; i++) {
					listPanel.setValueAt(null, listPanel.getSelectedRow(), str_allitems[i]); //设置某一项值为空!!
				}
			} else if (billPanel instanceof BillPropPanel) {

			} else if (billPanel instanceof BillQueryPanel) {
				BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
				for (int i = 0; i < str_allitems.length; i++) {
					AbstractWLTCompentPanel panel = queryPanel.getCompentByKey(str_allitems[i]); //
					panel.reset(); //清空
					//panel.focus();  //
				}
			}
		}

		inStack.push("ok"); //因为没有实际返回值，所以放一个"ok"表示操作成功!!!!!
	}
}
