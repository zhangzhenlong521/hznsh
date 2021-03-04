package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import javax.swing.JButton;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * ˢ���¼��󶨹�ʽ,�쳣����,������Ч��!!
 * @author xch
 *
 */
public class SetBillPanelBtnVisiableParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public SetBillPanelBtnVisiableParse(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 3; //
	}

	public void run(Stack inStack) throws ParseException {
		String par_visiable = (String) inStack.pop(); //����ˢ�µı�����
		String par_btnname = (String) inStack.pop(); //��ť����
		Object par_passive_fieldname = inStack.pop(); //����ˢ�µı�����

		boolean bo_visiable = true; //
		if (par_visiable.equalsIgnoreCase("false")) {
			bo_visiable = false;
		}

		if (par_passive_fieldname instanceof BillListPanel) { //����Ǳ������
			BillListPanel billlist = (BillListPanel) par_passive_fieldname;
			billlist.setBillListBtnVisiable(par_btnname, bo_visiable); //
		} else if (par_passive_fieldname instanceof BillTreePanel) { //������������!!!
			BillTreePanel billTree = (BillTreePanel) par_passive_fieldname;
			//billTree.get
		} else if (par_passive_fieldname instanceof BillCardPanel) { //������������!!!
			BillCardPanel billCard = (BillCardPanel) par_passive_fieldname;
			JButton btn = billCard.getBillCardBtn("par_btnname");
			if (btn != null) {
				btn.setVisible(bo_visiable); //
			}
		}
	}
}
