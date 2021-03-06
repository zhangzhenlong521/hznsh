package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * 刷新事件绑定公式,异常有用,大大提高效率!!
 * @author xch
 *
 */
public class GetSelectedBillVOItemValueParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public GetSelectedBillVOItemValueParse(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 2; //
	}

	public void run(Stack inStack) throws ParseException {
		String par_itemkey = (String) inStack.pop(); //被动刷新的表的外键
		Object par_billPanel = inStack.pop(); //主动刷新的表的主键

		if (par_billPanel instanceof BillListPanel) { //如果是表型面板
			BillListPanel billlist = (BillListPanel) par_billPanel;
			BillVO billVO = billlist.getSelectedBillVO(); //
			if (billVO != null) {
				inStack.push(billVO.getStringValue(par_itemkey)); //
				return;
			}

		} else if (par_billPanel instanceof BillTreePanel) { //如果是树型面板!!!
			BillTreePanel billTree = (BillTreePanel) par_billPanel;
			BillVO billVO = billTree.getSelectedVO(); //
			if (billVO != null) {
				inStack.push(billVO.getStringValue(par_itemkey)); //
				return;
			}
		}

		inStack.push(""); //
	}

}
