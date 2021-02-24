package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ˢ���¼��󶨹�ʽ,�쳣����,������Ч��!!
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
			Object par_passive_fieldname = inStack.pop(); //����ˢ�µı�����
			if (formatpanel != null) {
				formatpanel.setReturnRefItemVOFrom((BillPanel) par_passive_fieldname);
			}
		} else {
			String str_refnamefield = (String) inStack.pop(); //����ˢ�µı�����
			String str_refidfield = (String) inStack.pop(); //����ˢ�µı�����
			BillPanel par_panel = (BillPanel) inStack.pop(); //����ˢ�µı�����
			formatpanel.setReturnRefItemVOFrom(par_panel); //
			formatpanel.setReturnRefItemVOIDFieldName(str_refidfield);
			formatpanel.setReturnRefItemVONameFieldName(str_refnamefield);
		}
	}
}
