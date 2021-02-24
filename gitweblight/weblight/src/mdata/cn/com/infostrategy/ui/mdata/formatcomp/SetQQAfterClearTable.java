package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class SetQQAfterClearTable extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public SetQQAfterClearTable(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 2; //
	}

	public void run(Stack inStack) throws ParseException {
	

		final Object par_passive_panel = inStack.pop(); //被刷新的面板
		final Object main_panel = inStack.pop(); //主刷新的面板

		if (par_passive_panel == null || main_panel==null) {
			return;
		}
	
		final BillListPanel billlistpanel = (BillListPanel) par_passive_panel;
	
		if (main_panel instanceof BillListPanel) { //如果是表型面板

			final BillListPanel billlist = (BillListPanel) main_panel;

			billlist.addBillListAfterQueryListener(new BillListAfterQueryListener() {

				public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
					billlistpanel.clearTable();
				}
			});
		}

	}
}
