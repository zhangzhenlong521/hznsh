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
public class SetCardRowVisiable extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public SetCardRowVisiable(BillPanel _billPanel) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����"true"/"false"
		this.billPanel = _billPanel;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_itemKey = (String) param_2; //������һ��,�Զ��Ÿ���!!
		String str_editable = (String) param_1; //�ڶ���ֵ,Ӧ����"true"/"false"

		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			if (str_editable.trim().equalsIgnoreCase("true")) { //�����true,��ɱ༭
				if (str_itemKey != null && !str_itemKey.equals("")) {
					str_itemKey = str_itemKey.trim();
					String[] str_allitems = str_itemKey.split(",");
					for (int i = 0; i < str_allitems.length; i++) {
						cardPanel.setRowPanelVisiable(str_allitems[i], true); //
					}
				}
			} else if (str_editable.trim().equalsIgnoreCase("false")) { //�����"false"���򲻿ɱ༭!!!!
				if (str_itemKey != null && !str_itemKey.equals("")) {
					str_itemKey = str_itemKey.trim();
					String[] str_allitems = str_itemKey.split(",");
					for (int i = 0; i < str_allitems.length; i++) {
						cardPanel.setRowPanelVisiable(str_allitems[i], false); //
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
