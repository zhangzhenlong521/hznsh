package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;

/**
 * ���������ĳһ���ֵ!!���ķ���ֵӦ�ò��ᱻ��ĺ���ʹ��!!!
 * @author xch
 *
 */
public class SetItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI
	private BillPanel billPanel = null;

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	private HashVO[] hvs_data = null; //���е�����

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public SetItemValue(BillPanel _billPanel) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����ֵ! ��һ��ItemKey�Ժ���ܻ��и����ӵı仯!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	public SetItemValue(HashMap _dataMap) {
		numberOfParameters = 2; //���������������е�һ����ItemKey,�ڶ�����ֵ! ��һ��ItemKey�Ժ���ܻ��и����ӵı仯!!
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

		if (str_value == null) { //����ǿգ���ת���ɿ��ַ���!!
			str_value = "";
		}

		inStack.push(str_value); //�����ջ!!

	}

	private String getUIValue(String _itemkey, String _itemValue) {
		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			cardPanel.setRealValueAt(_itemkey, _itemValue); //
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			int li_row = listPanel.getSelectedRow(); //
			listPanel.setRealValueAt(_itemValue, li_row, _itemkey); //����ʵ��ֵ
		} else if (billPanel instanceof BillQueryPanel) {
			BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
			queryPanel.setRealValueAt(_itemkey, _itemValue); //
		} else if (billPanel instanceof BillPropPanel) {

		}

		return "ok"; //
	}

	private String getBSValue(String _itemkey, String _itemValue) {
		if (colDataTypeMap.containsKey(_itemkey)) {//����������keyֵ
			String str_datatype = (String) colDataTypeMap.get(_itemkey); //
			if (str_datatype.equals(WLTConstants.COMP_LABEL) || str_datatype.equals(WLTConstants.COMP_TEXTFIELD) || str_datatype.equals(WLTConstants.COMP_NUMBERFIELD) || str_datatype.equals(WLTConstants.COMP_PASSWORDFIELD) || str_datatype.equals(WLTConstants.COMP_TEXTAREA)) {
				rowDataMap.put(_itemkey, new StringItemVO(_itemValue)); //
			} else if (str_datatype.equals(WLTConstants.COMP_REFPANEL) || str_datatype.equals(WLTConstants.COMP_REFPANEL_TREE) || str_datatype.equals(WLTConstants.COMP_REFPANEL_MULTI) || str_datatype.equals(WLTConstants.COMP_REFPANEL_CUST)
					|| str_datatype.equals(WLTConstants.COMP_REFPANEL_LISTTEMPLET) || str_datatype.equals(WLTConstants.COMP_REFPANEL_REGFORMAT) || str_datatype.equals(WLTConstants.COMP_REFPANEL_REGEDIT)) {
				RefItemVO itemVO = (RefItemVO) rowDataMap.get(_itemkey); //
				if (itemVO == null) {
					rowDataMap.put(_itemkey, new RefItemVO(_itemValue, _itemValue, _itemValue)); ////
				} else {
					itemVO.setId(_itemValue); //
				}
			} else if (str_datatype.equals(WLTConstants.COMP_DATE) || str_datatype.equals(WLTConstants.COMP_DATETIME) || str_datatype.equals(WLTConstants.COMP_BIGAREA) || str_datatype.equals(WLTConstants.COMP_FILECHOOSE) || str_datatype.equals(WLTConstants.COMP_COLOR)
					|| str_datatype.equals(WLTConstants.COMP_CALCULATE) || str_datatype.equals(WLTConstants.COMP_PICTURE)) {
				rowDataMap.put(_itemkey, new RefItemVO(_itemValue, null, _itemValue)); ////
			} else if (str_datatype.equals(WLTConstants.COMP_COMBOBOX)) {
				rowDataMap.put(_itemkey, new ComBoxItemVO(_itemValue, null, _itemValue)); ////
			} else {
				rowDataMap.put(_itemkey, new StringItemVO(_itemValue)); //
			}
		}
		return "ok";
	}

	public void setRowDataMap(HashMap _rowDataMap) {
		this.rowDataMap = _rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}
}
