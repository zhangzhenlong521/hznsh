package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * 取得Excel控件中当前行的第几列或当前列的第几行的值
 * 之所以封装在一个类中是为是少用几个类,从而提高下载性能。
 * @author xch
 *
 */
public class GetCellCurrRowColItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;
	private int currrow = 0; //当前行
	private int currcol = 0; //当前列
	private int rowcoltype = 1; //行与列类型,如果是1表示是当前行,如果是2表示是当前列.
	private boolean isNumber = false; //是否是数字

	//BS传入的参数
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	/**
	 * 取得某一项的值!!
	 */
	public GetCellCurrRowColItemValue(BillPanel _billPanel, int _row, int _col, int _type, boolean _isnumber) {
		numberOfParameters = 1; //只有一个参数,即表示是当前行的第几列,或当前列的第几行.
		this.billPanel = _billPanel;
		this.currrow = _row; //当前行
		this.currcol = _col; //当前列
		this.rowcoltype = _type;
		this.isNumber = _isnumber; //是否是数字..
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 在服务器端调用的构造方法
	 * @param dataMap
	 */
	public GetCellCurrRowColItemValue(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		try {
			//先取得参数!!
			checkStack(inStack);
			Object param_1 = inStack.pop();
			String str_rowcol = "" + param_1; ////
			if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
				BillCellPanel cellPanel = (BillCellPanel) billPanel; //
				String str_value = null; //
				if (this.rowcoltype == 1) { //如果是取同一行的第几列
					str_value = cellPanel.getValueAt(this.currrow, Integer.parseInt(str_rowcol) - 1); //
				} else if (this.rowcoltype == 2) { //如果是取同一列的第几行
					str_value = cellPanel.getValueAt(Integer.parseInt(str_rowcol) - 1, this.currcol); //
				}

				if (this.isNumber) { //如果是数字
					if (str_value == null || str_value.trim().equals("")) {
						inStack.push(new Double(0));
					} else {
						inStack.push(new Double(str_value));
					}
				} else { //如果是字符串
					inStack.push(str_value); //
				}
			} else if (callType == WLTConstants.JEPTYPE_BS) {
				//str_value = getBSValue(str_itemKey); //
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //
			throw new ParseException(ex.getMessage());
		}

	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}
}
