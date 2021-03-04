package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

/**
 * ����ĳһ���Ƿ�ɱ༭!!
 * @author xch
 *
 */
public class SetCardGroupExpand extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public SetCardGroupExpand(BillPanel _billPanel) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����"true"/"false"
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_grouptitle = (String) param_2; //������һ��,�Զ��Ÿ���!!
		String str_editable = (String) param_1; //�ڶ���ֵ,Ӧ����"true"/"false"

		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			if (str_editable.trim().equalsIgnoreCase("true")) { //�����true,��ɱ༭
				if (str_grouptitle != null && !str_grouptitle.trim().equals("")) {
					str_grouptitle = str_grouptitle.trim();
					String[] str_allgroups = str_grouptitle.split(",");
					for (int i = 0; i < str_allgroups.length; i++) {
						cardPanel.setGroupExpandable(str_allgroups[i], true); //
					}
				}
			} else if (str_editable.trim().equalsIgnoreCase("false")) { //�����"false"���򲻿ɱ༭!!!!
				if (str_grouptitle != null && !str_grouptitle.equals("")) {
					str_grouptitle = str_grouptitle.trim();
					String[] str_allgroups = str_grouptitle.split(",");
					for (int i = 0; i < str_allgroups.length; i++) {
						cardPanel.setGroupExpandable(str_allgroups[i], false); //
					}
				}
			} else { //�����С�����˱�Ĳ�������ɶ������!!
			}
		} else if (billPanel instanceof BillListPanel) {
		} else if (billPanel instanceof BillPropPanel) {
		}

		inStack.push("ok"); //��Ϊ����ֵ��û��ʵ�ʷ���ֵ�����Ծͷ���һ��"ok"��ʾ��ֵ�ɹ���!!
	}
}
