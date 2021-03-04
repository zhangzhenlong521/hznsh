package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

public class DecideMessageBox extends PostfixMathCommand {

	public DecideMessageBox() {

		numberOfParameters = 3; //
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {

		// ��ȡ�ò���!!
		checkStack(inStack);
		String str_message = (String) inStack.pop(); // //
		final String str_templetcode = (String) inStack.pop(); // //
		String str_templettype = (String) inStack.pop(); // //
		if (str_templettype == null) {
			return;
		} else {

			if (str_templettype.equals("�б�")) {// ����Ǳ������
				final BillListPanel billlistpanel = new BillListPanel(
						str_templetcode);
				if (billlistpanel.getSelectedBillVO() == null) {

					try {
						 throw new WLTAppException(str_message);
					} catch (Exception e) {

						e.printStackTrace();
						inStack.push(new WLTAppException(str_message)); // ����һ���쳣
					}

				}

			} else if (str_templettype.equals("����")) {
				final BillTreePanel billtreepanel = new BillTreePanel(
						str_templetcode);
				if (billtreepanel.getSelectedVO() == null) {
					try {
						 throw new WLTAppException(str_message);
					} catch (Exception e) {

						e.printStackTrace();
						inStack.push(new WLTAppException(str_message)); // ����һ���쳣
					}

				}
			}
		}
	}
}
