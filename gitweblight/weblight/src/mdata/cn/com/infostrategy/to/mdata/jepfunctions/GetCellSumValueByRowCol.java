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
 * ȡ������е�һ�������ڵ�ֵ,������ʼ����ʼ�е������н�����,��һ����������,������������ڵ����и��ӵ�ֵ�ĺ�!!
 * @author xch
 *
 */
public class GetCellSumValueByRowCol extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private int currRow = 0; //��ǰ��
	private int currCol = 0; //��ǰ��

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetCellSumValueByRowCol(BillPanel _billPanel, int _currRow, int _currCol) {
		numberOfParameters = 4; //�ĸ�����,����ʼ��,��ʼ��,������,������,����к����к��Ǹ���,���ʾ�ǵ�ǰ���뵱ǰ��..
		this.billPanel = _billPanel;
		this.currRow = _currRow; //��ǰ��.
		this.currCol = _currCol; //��ǰ��.
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �ڷ������˵��õĹ��췽��
	 * @param dataMap
	 */
	public GetCellSumValueByRowCol(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
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

		if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			BillCellPanel cellPanel = (BillCellPanel) billPanel;
			BigDecimal bd_value = new BigDecimal(0); //��ʼ��������ֵ,����ʹ��BigDecimal,�������ָ��㾫�ȵ�����,����0.2+0.7=0.89999999������.
			for (int i = li_begin_row - 1; i <= li_end_row - 1; i++) {
				for (int j = li_begin_col - 1; j <= li_end_col - 1; j++) {
					try {
						String itemValue = cellPanel.getValueAt(i, j); //
						if (itemValue != null && !itemValue.trim().equals("")) {
							bd_value = bd_value.add(new BigDecimal(itemValue)); //�ڽ�����ϼ��ϰ������������ϵ�ֵ.
						}
					} catch (Exception ex) {
						ex.printStackTrace(); //
					}
				}
			}
			String str_value = bd_value.toString();//��ǰ�����õ�bd_value.doubleValue()������λ����������ʾ��ѧ�������������ȡ����ֵ����ȷ�����/2018-12-26��
			if (str_value.contains(".")) {
				bd_value = bd_value.setScale(2, BigDecimal.ROUND_HALF_UP);//��������
				str_value = bd_value.toString();
			}
			inStack.push(str_value); //�����ջ!!
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
