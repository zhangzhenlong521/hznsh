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
public class SetBtnBarVisiableParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public SetBtnBarVisiableParse(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 2; //
	}

	public void run(Stack inStack) throws ParseException {
		String par_visiable = (String) inStack.pop(); //����ˢ�µı�����
		Object par_passive_fieldname = inStack.pop(); //����ˢ�µı�����

		boolean bo_visiable = true; //
		if (par_visiable.equalsIgnoreCase("false")) {
			bo_visiable = false;
		}

		if (par_passive_fieldname instanceof BillListPanel) { //����Ǳ������
			BillListPanel billlist = (BillListPanel) par_passive_fieldname;
			billlist.setAllBillListBtnVisiable(bo_visiable); //
		} else if (par_passive_fieldname instanceof BillTreePanel) { //������������!!!
			BillTreePanel billTree = (BillTreePanel) par_passive_fieldname;
			billTree.setAllBillTreeBtnVisiable(bo_visiable); //
		}
	}
}
