package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillItemVO;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * 设置面板中某一项的背景颜色【李春娟/2014-11-13】
 * @author lcj
 *
 */
public class SetItemBackGround extends PostfixMathCommand {
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
	public SetItemBackGround(BillPanel _billPanel) {
		numberOfParameters = 2; //有两个参数，其中第一个是ItemKey,第二个是值! 第一个ItemKey以后可能会有更复杂的变化!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	public SetItemBackGround(HashMap _dataMap) {
		numberOfParameters = 2; //有两个参数，其中第一个是ItemKey,第二个是值! 第一个ItemKey以后可能会有更复杂的变化!!
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();

		String str_itemKey = (String) param_2; //itemKey
		String str_itemBackGroundColor = (param_1 == null ? "" : String.valueOf(param_1)); //第二个值可能是字符串或数字!!

		String str_value = null;

		if (str_itemKey != null && str_itemKey.contains(",")) {//可设置多个
			String[] keys = TBUtil.getTBUtil().split(str_itemKey, ",");
			if (keys != null && keys.length > 0) {
				for (int i = 0; i < keys.length; i++) {
					if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
						str_value = getUIValue(keys[i], str_itemBackGroundColor); //
					} else if (callType == WLTConstants.JEPTYPE_BS) {
						str_value = getBSValue(keys[i], str_itemBackGroundColor); //
					}
				}
			}
		} else {
			if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
				str_value = getUIValue(str_itemKey, str_itemBackGroundColor); //
			} else if (callType == WLTConstants.JEPTYPE_BS) {
				str_value = getBSValue(str_itemKey, str_itemBackGroundColor); //
			}
		}
		if (str_value == null) { //如果是空，则转换成空字符串!!
			str_value = "";
		}

		inStack.push(str_value); //置入堆栈!!

	}

	private String getUIValue(String _itemkey, String _itemBackGround) {
		if (billPanel instanceof BillListPanel) {//只实现列表显示背景颜色即可
			BillListPanel listPanel = (BillListPanel) billPanel;
			int li_row = listPanel.getSelectedRow(); //选中的行,编辑公式调用时必然触发的是选中的行
			listPanel.setItemBackGroundColor(_itemBackGround, li_row, _itemkey); //设置实际值
		}
		return "ok"; //
	}

	private String getBSValue(String _itemkey, String _itemBackGround) {
		if (colDataTypeMap.containsKey(_itemkey)) {//如果包含这个key值
			BillItemVO rowItemVO = (BillItemVO) rowDataMap.get(_itemkey); //
			if (rowItemVO != null) {
				rowItemVO.setBackGroundColor(_itemBackGround); //
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
