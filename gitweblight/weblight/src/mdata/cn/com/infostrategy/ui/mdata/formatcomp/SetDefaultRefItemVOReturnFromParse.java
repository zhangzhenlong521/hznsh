package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 刷新事件绑定公式,异常有用,大大提高效率!!
 * @author xch
 *
 */
public class SetDefaultRefItemVOReturnFromParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public SetDefaultRefItemVOReturnFromParse(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = -1; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);

		int li_currparcount = curNumberOfParameters; //
		if (li_currparcount == 1) {
			Object par_passive_fieldname = inStack.pop(); //被动刷新的表的外键
			if (formatpanel != null) {
				formatpanel.setReturnRefItemVOFrom((BillPanel) par_passive_fieldname);
			}
		} else {
			String str_refnamefield = (String) inStack.pop(); //被动刷新的表的外键
			String str_refidfield = (String) inStack.pop(); //被动刷新的表的外键
			BillPanel par_panel = (BillPanel) inStack.pop(); //被动刷新的表的外键
			formatpanel.setReturnRefItemVOFrom(par_panel); //
			formatpanel.setReturnRefItemVOIDFieldName(str_refidfield);
			formatpanel.setReturnRefItemVONameFieldName(str_refnamefield);
		}
	}
}
