package cn.com.infostrategy.ui.mdata.styletemplet.config;

/**
 * 设置快速查询后自定义按钮是否显示
 */
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class SetSelfListQQIsVisiable extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public SetSelfListQQIsVisiable(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = -1; //
	}

	public void run(Stack inStack) throws ParseException {
		Object par_select_isvist = inStack.pop(); //查询是否显示
		Object par_select = inStack.pop(); //查询

		final Object par_passive_panel = inStack.pop(); //被刷新的面板

		if (par_passive_panel == null) {
			return;
		}
		final String str_select_isvist = (String) par_select_isvist;
		final String str_select = (String) par_select;

		final BillListPanel billlistpanel = (BillListPanel) par_passive_panel;
		billlistpanel.getBillListBtn(str_select).setEnabled(false);

		if (par_passive_panel instanceof BillListPanel) { //如果是表型面板
			final BillListPanel billlist = (BillListPanel) par_passive_panel; //
			billlist.addBillListAfterQueryListener(new BillListAfterQueryListener() { //
						public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
							if (str_select_isvist.equalsIgnoreCase("false")) { //如果是false
								billlistpanel.getBillListBtn(str_select).setEnabled(false);
							} else {
								billlistpanel.getBillListBtn(str_select).setEnabled(true);
							}

						}
					});
		}

	}
}
