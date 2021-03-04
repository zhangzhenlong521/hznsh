package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ������ĳһ�������ֵ!!ֻ��BillCardPanel��ʹ�ã�
 * @author xch
 *
 */
public class ResetCardGroupValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public ResetCardGroupValue(BillPanel _billPanel) {
		numberOfParameters = 1; //ֻ��һ�������������ö��ŷֿ�
		this.billPanel = _billPanel; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_grouptitles = (String) param_1;

		if (str_grouptitles != null && !str_grouptitles.trim().equals("")) {
			String[] str_allgrouptitles = new TBUtil().split(str_grouptitles, ",");
			if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				for (int i = 0; i < str_allgrouptitles.length; i++) {
					cardPanel.resetByGrouptitle(str_allgrouptitles[i]);
				}
			}
		}
		inStack.push("ok"); //��Ϊû��ʵ�ʷ���ֵ�����Է�һ��"ok"��ʾ�����ɹ�!!!!!
	}
}
