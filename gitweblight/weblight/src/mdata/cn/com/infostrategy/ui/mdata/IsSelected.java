package cn.com.infostrategy.ui.mdata;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * �ж�һ���б��Ƭ�Ƿ�ѡ��!!
 * @author xch
 *
 */
public class IsSelected extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public IsSelected(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		final Object par_passive_panel = inStack.pop(); // ��ˢ�µ����
		if (par_passive_panel == null) {
			inStack.push(Boolean.FALSE); // 
			return;
		}

		if (par_passive_panel instanceof BillListPanel) { // ����Ǳ������
			BillListPanel billlist = (BillListPanel) par_passive_panel;
			if (billlist.getSelectedRow() >= 0) {
				inStack.push(Boolean.TRUE); // 
				return; //
			} else {
				inStack.push(Boolean.FALSE); // 
				return; //
			}
		} else if (par_passive_panel instanceof BillTreePanel) {
			BillTreePanel billtree = (BillTreePanel) par_passive_panel;
			if (billtree.getSelectedNode() != null) {
				inStack.push(Boolean.TRUE); // 
				return; //
			} else {
				inStack.push(Boolean.FALSE); // 
				return; //
			}
		} else {
			inStack.push(Boolean.FALSE); // 
		}

	}
}
