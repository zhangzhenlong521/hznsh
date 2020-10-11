package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

public class GetRefCode extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetRefCode(BillPanel _billPanel) {
		numberOfParameters = 1; //ֻ��һ������������������Ժ���������仯������������ǰ�滹���Լ�����ҳ�����ĸ����ֵ������ţ��Ӷ�ʵ�����֮�������!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_itemKey = (String) param_1; ////
		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			str_value = getUIValue(str_itemKey); //
		}
		inStack.push(str_value); //�����ջ!!
	}

	private String getUIValue(String _itemkey) {
		String str_value = null;
		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			Object obj = cardPanel.getValueAt(_itemkey);
			if (obj instanceof RefItemVO) {
				str_value = ((RefItemVO) obj).getCode(); //
			} else if (obj instanceof ComBoxItemVO) {
				str_value = ((ComBoxItemVO) obj).getCode(); //
			}
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			str_value = listPanel.getRealValueAtModel(listPanel.getSelectedRow(), _itemkey); //ȡ��ѡ���е�����
		} else if (billPanel instanceof BillPropPanel) {

		}
		if (str_value == null) { //����ǿգ���ת���ɿ��ַ���!!
			str_value = "";
		}
		return str_value; //
	}

}
