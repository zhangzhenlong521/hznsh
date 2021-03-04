package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * 刷新事件绑定公式,异常有用,大大提高效率!!
 * @author xch
 *
 */
public class QueryDataByConditionParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public QueryDataByConditionParse(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 2; //
	}

	public void run(Stack inStack) throws ParseException {
		String str_condition = (String) inStack.pop(); //被动刷新的表的外键
		Object par_passive_fieldname = inStack.pop(); //被动刷新的表的外键

		if (par_passive_fieldname instanceof BillListPanel) { //如果是表型面板
			BillListPanel billlist = (BillListPanel) par_passive_fieldname;
			billlist.QueryDataByCondition(str_condition); //
		} else if (par_passive_fieldname instanceof BillTreePanel) { //如果是树型面板!!!
			BillTreePanel billTree = (BillTreePanel) par_passive_fieldname;
			billTree.queryDataByCondition(str_condition); //
		}

	}

}
