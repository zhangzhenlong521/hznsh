package cn.com.infostrategy.ui.mdata.formatcomp;

/**
 * 模板选择事件清空另一个模板
 */
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

public class ClearTable extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public ClearTable(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		Object billPanel = inStack.pop(); //需要清空的模板
		if (billPanel instanceof BillListPanel) { //如果是表型面板
			BillListPanel billlist = (BillListPanel) billPanel;
			billlist.clearTable(); //
		} else if (billPanel instanceof BillTreePanel) { //如果是树型面板!!!
			BillTreePanel billTree = (BillTreePanel) billPanel;
			billTree.clearTree(); //
		} else if (billPanel instanceof BillCardPanel) { //如果是树型面板!!!
			BillCardPanel bilCard = (BillCardPanel) billPanel;
			bilCard.clear(); //
		}
	}
}
