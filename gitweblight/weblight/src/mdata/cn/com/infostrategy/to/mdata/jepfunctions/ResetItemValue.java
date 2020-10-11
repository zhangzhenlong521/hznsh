package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.AbstractWLTCompentPanel;

/**
 * ȡ������е�ĳһ���ֵ!!
 * @author xch
 *
 */
public class ResetItemValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public ResetItemValue(BillPanel _billPanel) {
		numberOfParameters = 1; //ֻ��һ������������������Ժ���������仯������������ǰ�滹���Լ�����ҳ�����ĸ����ֵ������ţ��Ӷ�ʵ�����֮�������!!
		this.billPanel = _billPanel; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_itemKey = (String) param_1;

		if (str_itemKey != null && !str_itemKey.trim().equals("")) {
			str_itemKey = str_itemKey.trim();
			String[] str_allitems = str_itemKey.split(",");
			if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
				BillCardPanel cardPanel = (BillCardPanel) billPanel;
				for (int i = 0; i < str_allitems.length; i++) {
					AbstractWLTCompentPanel panel = cardPanel.getCompentByKey(str_allitems[i]); //
					panel.reset(); //���
					//panel.focus();  //
				}
			} else if (billPanel instanceof BillListPanel) {
				BillListPanel listPanel = (BillListPanel) billPanel;
				for (int i = 0; i < str_allitems.length; i++) {
					listPanel.setValueAt(null, listPanel.getSelectedRow(), str_allitems[i]); //����ĳһ��ֵΪ��!!
				}
			} else if (billPanel instanceof BillPropPanel) {

			} else if (billPanel instanceof BillQueryPanel) {
				BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
				for (int i = 0; i < str_allitems.length; i++) {
					AbstractWLTCompentPanel panel = queryPanel.getCompentByKey(str_allitems[i]); //
					panel.reset(); //���
					//panel.focus();  //
				}
			}
		}

		inStack.push("ok"); //��Ϊû��ʵ�ʷ���ֵ�����Է�һ��"ok"��ʾ�����ɹ�!!!!!
	}
}
