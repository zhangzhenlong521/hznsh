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
 * 设置面板中某一项的值!!它的返回值应该不会被别的函数使用!!!
 * @author xch
 *
 */
public class SetItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI
	private BillPanel billPanel = null;

	//BS传入的参数
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	private HashVO[] hvs_data = null; //所有的数据

	/**
	 * 取得某一项的值!!
	 */
	public SetItemValue(BillPanel _billPanel) {
		numberOfParameters = 2; //有两个参数，其中第一个是ItemKey,第二个是值! 第一个ItemKey以后可能会有更复杂的变化!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	public SetItemValue(HashMap _dataMap) {
		numberOfParameters = 2; //有两个参数，其中第一个是ItemKey,第二个是值! 第一个ItemKey以后可能会有更复杂的变化!!
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();

		String str_itemKey = (String) param_2; //
		String str_itemValue = (param_1 == null ? "" : String.valueOf(param_1)); //第二个值可能是字符串或数字!!

		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
			str_value = getUIValue(str_itemKey, str_itemValue); //
		} else if (callType == WLTConstants.JEPTYPE_BS) {
			str_value = getBSValue(str_itemKey, str_itemValue); //
		}

		if (str_value == null) { //如果是空，则转换成空字符串!!
			str_value = "";
		}

		inStack.push(str_value); //置入堆栈!!

	}

	private String getUIValue(String _itemkey, String _itemValue) {
		if (billPanel instanceof BillCardPanel) { //如果是卡片面板
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			cardPanel.setRealValueAt(_itemkey, _itemValue); //
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			int li_row = listPanel.getSelectedRow(); //
			listPanel.setRealValueAt(_itemValue, li_row, _itemkey); //设置实际值
		} else if (billPanel instanceof BillQueryPanel) {
			BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
			queryPanel.setRealValueAt(_itemkey, _itemValue); //
		} else if (billPanel instanceof BillPropPanel) {

		}

		return "ok"; //
	}

	private String getBSValue(String _itemkey, String _itemValue) {
		if (colDataTypeMap.containsKey(_itemkey)) {//如果包含这个key值
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
