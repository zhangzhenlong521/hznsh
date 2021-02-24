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
public class GetCellSumValueByRowCol extends PostfixMathCommand {
	private int callType = -1; //

	//UI传入的参数
	private BillPanel billPanel = null;
	private int currRow = 0; //当前行
	private int currCol = 0; //当前列

	//BS传入的参数
	private HashMap colDataTypeMap = null; //一行数据!!
	private HashMap rowDataMap = null; //一行数据!!

	/**
	 * 取得某一项的值!!
	 */
	public GetCellSumValueByRowCol(BillPanel _billPanel, int _currRow, int _currCol) {
		numberOfParameters = 4; //四个参数,即起始行,起始列,结束行,结束列,如果行号与列号是负数,则表示是当前行与当前列..
		this.billPanel = _billPanel;
		this.currRow = _currRow; //当前行.
		this.currCol = _currCol; //当前列.
		callType = WLTConstants.JEPTYPE_UI; //客户端调用
	}

	/**
	 * 在服务器端调用的构造方法
	 * @param dataMap
	 */
	public GetCellSumValueByRowCol(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //客户端调用
	}

	/**
	 * 真正的逻辑执行!!
	 */
	public void run(Stack inStack) throws ParseException {
		//先取得参数!!
		checkStack(inStack);
		Object param_1 = inStack.pop();
		Object param_2 = inStack.pop();
		Object param_3 = inStack.pop();
		Object param_4 = inStack.pop();

		int li_end_col = Integer.parseInt("" + param_1); ////
		int li_end_row = Integer.parseInt("" + param_2); ////
		int li_begin_col = Integer.parseInt("" + param_3); ////
		int li_begin_row = Integer.parseInt("" + param_4); ////

		if (li_begin_row < 0) {
			li_begin_row = this.currRow + 1;
		}

		if (li_begin_col < 0) {
			li_begin_col = this.currCol + 1;
		}

		if (li_end_row < 0) {
			li_end_row = this.currRow + 1;
		}

		if (li_end_col < 0) {
			li_end_col = this.currCol + 1;
		}

		if (callType == WLTConstants.JEPTYPE_UI) { //如果是客户端调用
			BillCellPanel cellPanel = (BillCellPanel) billPanel;
			BigDecimal bd_value = new BigDecimal(0); //初始化计算结果值,必须使用BigDecimal,否则会出现浮点精度的问题,比如0.2+0.7=0.89999999的样子.
			for (int i = li_begin_row - 1; i <= li_end_row - 1; i++) {
				for (int j = li_begin_col - 1; j <= li_end_col - 1; j++) {
					try {
						String itemValue = cellPanel.getValueAt(i, j); //
						if (itemValue != null && !itemValue.trim().equals("")) {
							bd_value = bd_value.add(new BigDecimal(itemValue)); //在结果集上加上阿方索个格子上的值.
						}
					} catch (Exception ex) {
						ex.printStackTrace(); //
					}
				}
			}
			String str_value = bd_value.toString();//以前这里用到bd_value.doubleValue()，导致位数过长或显示科学计数法，后面截取后数值不正确【李春娟/2018-12-26】
			if (str_value.contains(".")) {
				bd_value = bd_value.setScale(2, BigDecimal.ROUND_HALF_UP);//四舍五入
				str_value = bd_value.toString();
			}
			inStack.push(str_value); //置入堆栈!!
		} else if (callType == WLTConstants.JEPTYPE_BS) {
		}
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}

}
