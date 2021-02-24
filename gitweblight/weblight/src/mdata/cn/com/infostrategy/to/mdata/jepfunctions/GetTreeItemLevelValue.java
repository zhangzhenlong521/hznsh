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

	//UI传入的参数
	private BillPanel billPanel = null;
	/**
	 * 取得某一项的值!!
	 */
	public GetTreeItemLevelValue(BillPanel _billPanel) {
		numberOfParameters = 2; //2个参数,第一个是ItemKey,第二个是树形参照或者自定义参照(树形)的层数
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
	

		String str_itemkey = (String) param_2; ////
		String str_level = (String) param_1; ////
		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
			str_value = getUIValue(str_itemkey,str_level); //
		}

		inStack.push(str_value); //置入堆栈!!
		
		

	}

	private String getUIValue(String _itemkey,String _level) {
		String str_value = null;
		if (billPanel instanceof BillCardPanel) { //如果是卡片面板
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			RefItemVO refItemVO = ((RefItemVO) cardPanel.getValueAt(_itemkey));
			if (refItemVO != null) {
				str_value =  (refItemVO.getHashVO().getAttributeValue("TreeItemLevelValue_"+_level)).toString(); //
			}
		}
//		else if (billPanel instanceof BillListPanel) {
//			BillListPanel listPanel = (BillListPanel) billPanel;
//			str_value = listPanel.getRealValueAtModel(listPanel.getSelectedRow(), _itemkey); //取得选中行的数据
//		} 
//		else if (billPanel instanceof BillPropPanel) {
//
//		}

		if (str_value == null) { //如果是空，则转换成空字符串!!
			str_value = "";
		}

		return str_value; //
	}

}
