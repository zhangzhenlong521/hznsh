package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

/**
 * ���ҳ������ֵ
 * @author xch
 *
 */
public class ResetAllItemValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public ResetAllItemValue(BillPanel _billPanel) {
		numberOfParameters = 0; //û�в���
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);

		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			cardPanel.reset(); //
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			listPanel.reset(listPanel.getSelectedRow()); //
		} else if (billPanel instanceof BillPropPanel) {

		}

		inStack.push("ok"); //��Ϊû��ʵ�ʷ���ֵ�����Է�һ��"ok"��ʾ�����ɹ�!!!!!
	}
}
