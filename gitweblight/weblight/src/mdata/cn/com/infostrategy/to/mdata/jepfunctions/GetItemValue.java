package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

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
 * 取得面板中的某一项的值!!
 * 该函数在客户端与服务器端都会使用!!!
 * @author xch
 *
 */
public class GetItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;

	//BS传入的参数
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	/**
	 * 取得某一项的值!!
	 */
	public GetItemValue(BillPanel _billPanel) {
		numberOfParameters = 1; //只有一个参数，但这个参数以后可以有许多变化，比如点操作，前面还可以加上是页面上哪个部分的索引号，从而实现组件之间的连动!!
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 在服务器端调用的构造方法
	 * @param dataMap
	 */
	public GetItemValue(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	/**
	 * 是否是创建模板时
	 * @param _isCreateTemplet
	 */
	public GetItemValue(boolean _isCreateTemplet) {
		numberOfParameters = 1; //
		callType = 2; //
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_itemKey = (String) param_1; ////
		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
			str_value = getUIValue(str_itemKey); //
		} else if (callType == WLTConstants.JEPTYPE_BS) {
			str_value = getBSValue(str_itemKey); //
		} else if (callType == 2) { //创建模板时
			str_value = "${" + str_itemKey + "}"; //实在没办法,在创建模板时无法取得页面上某一项的值,但又要兼容旧的公式配置,只能转换成${corpid}的样子,然后前台执行时再翻译!! 徐长华(2011-11-1 14:40)
		}
		inStack.push(str_value); //置入堆栈!!
	}

	private String getUIValue(String _itemkey) {
		String str_value = null;
		if (billPanel instanceof BillCardPanel) { //如果是卡片面板
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			str_value = cardPanel.getRealValueAt(_itemkey); //
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;
			str_value = listPanel.getRealValueAtModel(listPanel.getSelectedRow(), _itemkey); //取得选中行的数据
		} else if (billPanel instanceof BillQueryPanel) {
			BillQueryPanel queryPanel = (BillQueryPanel) billPanel;
			str_value = queryPanel.getRealValueAt(_itemkey); //
		} else if (billPanel instanceof BillPropPanel) {

		}

		if (str_value == null) { //如果是空，则转换成空字符串!!
			str_value = "";
		}

		return str_value; //
	}

	/**
	 * 取得服务器端值!!!
	 * @param _itemkey
	 * @return
	 */
	private String getBSValue(String _itemkey) {
		String str_value = null;
		if (rowDataMap.containsKey(_itemkey)) {//如果包含这个key值 
			Object obj = rowDataMap.get(_itemkey); //

			if (obj != null) {
				if (obj instanceof String) {
					str_value = (String) obj; //
				} else if (obj instanceof StringItemVO) {
					StringItemVO itemVO = (StringItemVO) obj; //
					str_value = itemVO.getStringValue(); //
				} else if (obj instanceof ComBoxItemVO) {
					ComBoxItemVO itemVO = (ComBoxItemVO) obj; //
					str_value = itemVO.getId(); //
				} else if (obj instanceof RefItemVO) {
					RefItemVO itemVO = (RefItemVO) obj; //
					str_value = itemVO.getId(); //
				} else {
					str_value = obj.toString(); //
				}
			}
		}

		if (str_value == null) { //如果是空，则转换成空字符串!!
			str_value = "";
		}
		//System.out.println("加载公式取得[" + _itemkey + "]=[" + str_value + "]!!!"); //
		return str_value; //
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}
}
