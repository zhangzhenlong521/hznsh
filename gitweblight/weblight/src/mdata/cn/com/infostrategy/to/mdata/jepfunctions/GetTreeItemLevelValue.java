package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

public class GetTreeItemLevelValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetTreeItemLevelValue(BillPanel _billPanel) {
		numberOfParameters = 2; //2������,��һ����ItemKey,�ڶ��������β��ջ����Զ������(����)�Ĳ���
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
		Object param_2 = inStack.pop();
	

		String str_itemkey = (String) param_2; ////
		String str_level = (String) param_1; ////
		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			str_value = getUIValue(str_itemkey,str_level); //
		}

		inStack.push(str_value); //�����ջ!!
		
		

	}

	private String getUIValue(String _itemkey,String _level) {
		String str_value = null;
		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			RefItemVO refItemVO = ((RefItemVO) cardPanel.getValueAt(_itemkey));
			if (refItemVO != null) {
				str_value =  (refItemVO.getHashVO().getAttributeValue("TreeItemLevelValue_"+_level)).toString(); //
			}
		}
//		else if (billPanel instanceof BillListPanel) {
//			BillListPanel listPanel = (BillListPanel) billPanel;
//			str_value = listPanel.getRealValueAtModel(listPanel.getSelectedRow(), _itemkey); //ȡ��ѡ���е�����
//		} 
//		else if (billPanel instanceof BillPropPanel) {
//
//		}

		if (str_value == null) { //����ǿգ���ת���ɿ��ַ���!!
			str_value = "";
		}

		return str_value; //
	}

}
