package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * ˢ���¼��󶨹�ʽ,�쳣����,������Ч��!!
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
		String par_itemkey = (String) inStack.pop(); //����ˢ�µı�����
		Object par_billPanel = inStack.pop(); //����ˢ�µı������

		if (par_billPanel instanceof BillListPanel) { //����Ǳ������
			BillListPanel billlist = (BillListPanel) par_billPanel;
			BillVO billVO = billlist.getSelectedBillVO(); //
			if (billVO != null) {
				inStack.push(billVO.getStringValue(par_itemkey)); //
				return;
			}

		} else if (par_billPanel instanceof BillTreePanel) { //������������!!!
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
