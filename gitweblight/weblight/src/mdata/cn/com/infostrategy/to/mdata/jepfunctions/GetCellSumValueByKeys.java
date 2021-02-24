package cn.com.infostrategy.to.mdata.jepfunctions;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * 取得面板中的一个区域内的值,即从起始行起始列到结束行结束列,是一个矩形区域,即计算该区域内的所有格子的值的和!!
 * @author xch
 *
 */
public class GetCellSumValueByKeys extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;
	private int currRow = 0; //当前行
	private int currCol = 0; //当前列

	/**
	 * 取得某一项的值!!
	 */
	public GetCellSumValueByKeys(BillPanel _billPanel, int _currRow, int _currCol) {
		numberOfParameters = -1;
		this.billPanel = _billPanel;
		this.currRow = _currRow; //当前行.
		this.currCol = _currCol; //当前列.
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 在服务器端调用的构造方法
	 * @param dataMap
	 */
	public GetCellSumValueByKeys(HashMap _dataMap) {
		numberOfParameters = -1; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		checkStack(inStack);

		String[] str_pa = new String[curNumberOfParameters];
		for (int i = str_pa.length - 1; i >= 0; i--) {//倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}

		BillCellPanel cellPanel = (BillCellPanel) billPanel; //
		BigDecimal bd_value = new BigDecimal(0);
		for (int i = 0; i < str_pa.length; i++) {
			String str_value = cellPanel.getValueAt(str_pa[i]); //
			if (str_value != null && !str_value.trim().equals("")) {
				bd_value = bd_value.add(new BigDecimal(Double.parseDouble(str_value))); //
			}
		}
		String str_doublevalue = bd_value.doubleValue() + "";
		if (str_doublevalue.indexOf(".") > 0) {
			String str_integral = str_doublevalue.substring(0, str_doublevalue.indexOf("."));
			String str_radix = str_doublevalue.substring(str_doublevalue.indexOf(".") + 1, str_doublevalue.length()); //
			if (str_radix.length() > 2) {
				str_doublevalue = str_integral + "." + str_radix.substring(0, 2); //
			}
		}
		inStack.push(str_doublevalue); //
	}
}
