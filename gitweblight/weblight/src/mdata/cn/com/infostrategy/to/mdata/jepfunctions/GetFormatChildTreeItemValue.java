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
 * ȡ������е�ĳһ���ֵ!!
 * �ú����ڿͻ�����������˶���ʹ��!!!
 * @author xch
 *
 */
public class GetFormatChildTreeItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetFormatChildTreeItemValue(BillPanel _billPanel) {
		numberOfParameters = 2; ////ֻ��һ������������������Ժ���������仯������������ǰ�滹���Լ�����ҳ�����ĸ����ֵ������ţ��Ӷ�ʵ�����֮�������!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
		checkStack(inStack);

		String str_billvoitemkey = (String) inStack.pop(); ////
		String str_paneltempletcode = (String) inStack.pop(); ////

		boolean bo_iffind = false; //
		if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			if (listPanel.getLoaderBillFormatPanel() != null) {
				BillTreePanel childTree = listPanel.getLoaderBillFormatPanel().getBillTreePanelByTempletCode(str_paneltempletcode);
				if (childTree != null) {
					BillVO selVO = childTree.getSelectedVO(); //
					if (selVO != null) {
						String str_value = selVO.getStringValue(str_billvoitemkey); //
						inStack.push(str_value); //�����ջ!!
						bo_iffind = true;
					}
				}
			}
		} else if (billPanel instanceof BillTreePanel) {
			BillTreePanel treePanel = (BillTreePanel) billPanel;
			if (treePanel.getLoaderBillFormatPanel() != null) {
				BillTreePanel childTree = treePanel.getLoaderBillFormatPanel().getBillTreePanelByTempletCode(str_paneltempletcode);
				if (childTree != null) {
					BillVO selVO = childTree.getSelectedVO(); //
					if (selVO != null) {
						String str_value = selVO.getStringValue(str_billvoitemkey); //
						inStack.push(str_value); //�����ջ!!
						bo_iffind = true;
					}
				}
			}
		} else if (billPanel instanceof BillCardPanel) {
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			if (cardPanel.getLoaderBillFormatPanel() != null) {
				BillTreePanel childTree = cardPanel.getLoaderBillFormatPanel().getBillTreePanelByTempletCode(str_paneltempletcode);
				if (childTree != null) {
					BillVO selVO = childTree.getSelectedVO(); //
					if (selVO != null) {
						String str_value = selVO.getStringValue(str_billvoitemkey); //
						inStack.push(str_value); //�����ջ!!
						bo_iffind = true;
					}
				}
			}
		}

		if (!bo_iffind) {
			inStack.push(""); //�����ջ!!
		}

	}

}
