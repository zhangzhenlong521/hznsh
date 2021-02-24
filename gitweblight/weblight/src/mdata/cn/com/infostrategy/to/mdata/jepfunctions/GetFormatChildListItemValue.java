package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;

/**
 * 取得面板中的某一项的值!!
 * 该函数在客户端与服务器端都会使用!!!
 * @author xch
 *
 */
public class GetFormatChildListItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;

	//BS传入的参数
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	/**
	 * 取得某一项的值!!
	 */
	public GetFormatChildListItemValue(BillPanel _billPanel) {
		numberOfParameters = 2; ////只有一个参数，但这个参数以后可以有许多变化，比如点操作，前面还可以加上是页面上哪个部分的索引号，从而实现组件之间的连动!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		checkStack(inStack);

		String str_billvoitemkey = (String) inStack.pop(); ////
		String str_paneltempletcode = (String) inStack.pop(); ////

		boolean bo_iffind = false; //
		if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			if (listPanel.getLoaderBillFormatPanel() != null) {
				BillListPanel childList = listPanel.getLoaderBillFormatPanel().getBillListPanelByTempletCode(str_paneltempletcode);
				if (childList != null) {
					BillVO selVO = childList.getSelectedBillVO(); //
					if (selVO != null) {
						String str_value = selVO.getStringValue(str_billvoitemkey); //
						inStack.push(str_value); //置入堆栈!!
						bo_iffind = true;
					}
				}
			}
		} else if (billPanel instanceof BillTreePanel) {
			BillTreePanel treePanel = (BillTreePanel) billPanel;
			if (treePanel.getLoaderBillFormatPanel() != null) {
				BillListPanel childList = treePanel.getLoaderBillFormatPanel().getBillListPanelByTempletCode(str_paneltempletcode); //
				if (childList != null) {
					BillVO selVO = childList.getSelectedBillVO(); //
					if (selVO != null) {
						String str_value = selVO.getStringValue(str_billvoitemkey); //
						inStack.push(str_value); //置入堆栈!!
						bo_iffind = true;
					}
				}
			}
		} else if (billPanel instanceof BillCardPanel) {
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			if (cardPanel.getLoaderBillFormatPanel() != null) {
				BillListPanel childList = cardPanel.getLoaderBillFormatPanel().getBillListPanelByTempletCode(str_paneltempletcode);
				if (childList != null) {
					BillVO selVO = childList.getSelectedBillVO(); //
					if (selVO != null) {
						String str_value = selVO.getStringValue(str_billvoitemkey); //
						inStack.push(str_value); //置入堆栈!!
						bo_iffind = true;
					}
				}
			}
		}

		if (!bo_iffind) {
			inStack.push(""); //置入堆栈!!
		}
	}

}
