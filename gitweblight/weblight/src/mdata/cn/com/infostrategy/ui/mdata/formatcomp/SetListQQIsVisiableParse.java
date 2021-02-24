package cn.com.infostrategy.ui.mdata.formatcomp;

/**
 * ���ÿ��ٲ�ѯ��ͨ�ð�ť�Ƿ���ʾ
 */
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryEvent;
import cn.com.infostrategy.ui.mdata.BillListAfterQueryListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class SetListQQIsVisiableParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //

	public SetListQQIsVisiableParse(BillFormatPanel _billcellpanel) {
		this.formatpanel = _billcellpanel; //
		numberOfParameters = 9; //
	}

	public void run(Stack inStack) throws ParseException {
		Object par_select_isvist = inStack.pop(); //��ѯ�Ƿ���ʾ
		Object par_select = inStack.pop(); //��ѯ
		Object par_delete_isvist = inStack.pop(); //ɾ���Ƿ���ʾ
		Object par_delete = inStack.pop(); //ɾ��
		Object par_update_isvist = inStack.pop(); //�༭�Ƿ���ʾ
		Object par_update = inStack.pop(); //�༭
		Object par_insert_isvist = inStack.pop(); //�����Ƿ���ʾ
		Object par_insert = inStack.pop(); //����

		final Object par_passive_panel = inStack.pop(); //��ˢ�µ����

		if (par_passive_panel == null) {
			return;
		}
		final String str_select_isvist = (String) par_select_isvist;
		final String str_select = (String) par_select;
		final String str_delete_isvist = (String) par_delete_isvist;
		final String str_delete = (String) par_delete;
		final String str_update_isvist = (String) par_update_isvist;
		final String str_update = (String) par_update;
		final String str_insert_isvist = (String) par_insert_isvist;
		final String str_insert = (String) par_insert;

		final BillListPanel billlistpanel = (BillListPanel) par_passive_panel;

		if (par_passive_panel instanceof BillListPanel) { //����Ǳ������

			final BillListPanel billlist = (BillListPanel) par_passive_panel;

			billlist.addBillListAfterQueryListener(new BillListAfterQueryListener() {

				public void onBillListAfterQuery(BillListAfterQueryEvent _event) {
					if (str_select_isvist.equals("false")) {
						billlistpanel.getBillListBtn(str_select).setEnabled(false);
					} else {
						billlistpanel.getBillListBtn(str_select).setEnabled(true);
					}
					if (str_delete_isvist.equals("false")) {
						billlistpanel.getBillListBtn(str_delete).setEnabled(false);
					} else {
						billlistpanel.getBillListBtn(str_delete).setEnabled(true);
					}
					if (str_update_isvist.equals("false")) {
						billlistpanel.getBillListBtn(str_update).setEnabled(false);
					} else {
						billlistpanel.getBillListBtn(str_update).setEnabled(true);
					}
					if (str_insert_isvist.equals("false")) {
						billlistpanel.getBillListBtn(str_insert).setEnabled(false);
					} else {
						billlistpanel.getBillListBtn(str_insert).setEnabled(true);
					}

				}
			});
		}

	}
}
