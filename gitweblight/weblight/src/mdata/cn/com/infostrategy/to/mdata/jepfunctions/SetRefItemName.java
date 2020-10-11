package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.UIRefPanel;

/**
 * ���������ĳһ���ֵ!!���ķ���ֵӦ�ò��ᱻ��ĺ���ʹ��!!!
 * @author xch
 *
 */
public class SetRefItemName extends PostfixMathCommand {
	private int callType = -1; //

	//UI
	private BillPanel billPanel = null;

	//BS
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public SetRefItemName(BillPanel _billPanel) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����ֵ! ��һ��ItemKey�Ժ���ܻ��и����ӵı仯!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * 
	 * @param dataMap
	 */
	public SetRefItemName(HashMap _dataMap) {
		numberOfParameters = 2; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		String str_itemKey = (String) param_2; //
		String str_itemValue = (param_1 == null ? "" : String.valueOf(param_1)); //�ڶ���ֵ�������ַ���������!!

		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			str_value = getUIValue(str_itemKey, str_itemValue); //
		} else if (callType == WLTConstants.JEPTYPE_BS) {
			str_value = getBSValue(str_itemKey, str_itemValue); //
		}

		inStack.push(str_value); //�����ջ!!
	}

	private String getUIValue(String _itemkey, String _itemvalue) {
		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			UIRefPanel refPanel = (UIRefPanel) cardPanel.getCompentByKey(_itemkey);
			refPanel.setRefName(_itemvalue); //
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			RefItemVO refItemVO = (RefItemVO) listPanel.getValueAt(listPanel.getSelectedRow(), _itemkey); //
			refItemVO.setName(_itemvalue); //
		} else if (billPanel instanceof BillPropPanel) {
		} else if (billPanel instanceof BillQueryPanel) {
			BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
			RefItemVO refItemVO = (RefItemVO) queryPanel.getValueAt(_itemkey); //
			System.out.println(_itemvalue);
			refItemVO.setName(_itemvalue); //
			queryPanel.setCompentObjectValue(_itemkey, refItemVO);
		}

		return "ok"; //
	}

	/**
	 * ȡ�÷�������ֵ!!!
	 * @param _itemkey
	 * @return
	 */
	private String getBSValue(String _itemkey, String _itemvalue) {
		String str_value = null;
		if (rowDataMap.containsKey(_itemkey)) { //����������keyֵ
			RefItemVO itemVO = (RefItemVO) rowDataMap.get(_itemkey); //ȡ�ò�������
			if (itemVO != null) {
				itemVO.setName(_itemvalue); //
			} else {
				rowDataMap.put(_itemkey, new RefItemVO(_itemvalue, null, _itemvalue));
			}
		}
		return "ok"; //
	}

	/**
	 * ����ÿ������!!
	 * @param rowDataMap
	 */
	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}

}
