package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * ˢ���¼��󶨹�ʽ,�쳣����,������Ч��!!
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
		String str_condition = (String) inStack.pop(); //����ˢ�µı�����
		Object par_passive_fieldname = inStack.pop(); //����ˢ�µı�����

		if (par_passive_fieldname instanceof BillListPanel) { //����Ǳ������
			BillListPanel billlist = (BillListPanel) par_passive_fieldname;
			billlist.QueryDataByCondition(str_condition); //
		} else if (par_passive_fieldname instanceof BillTreePanel) { //������������!!!
			BillTreePanel billTree = (BillTreePanel) par_passive_fieldname;
			billTree.queryDataByCondition(str_condition); //
		}

	}

}
