package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * ȡ��Excel�ؼ��е�ǰ�еĵڼ��л�ǰ�еĵڼ��е�ֵ
 * ֮���Է�װ��һ��������Ϊ�����ü�����,�Ӷ�����������ܡ�
 * @author xch
 *
 */
public class GetCellCurrRowColItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private int currrow = 0; //��ǰ��
	private int currcol = 0; //��ǰ��
	private int rowcoltype = 1; //����������,�����1��ʾ�ǵ�ǰ��,�����2��ʾ�ǵ�ǰ��.
	private boolean isNumber = false; //�Ƿ�������

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetCellCurrRowColItemValue(BillPanel _billPanel, int _row, int _col, int _type, boolean _isnumber) {
		numberOfParameters = 1; //ֻ��һ������,����ʾ�ǵ�ǰ�еĵڼ���,��ǰ�еĵڼ���.
		this.billPanel = _billPanel;
		this.currrow = _row; //��ǰ��
		this.currcol = _col; //��ǰ��
		this.rowcoltype = _type;
		this.isNumber = _isnumber; //�Ƿ�������..
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �ڷ������˵��õĹ��췽��
	 * @param dataMap
	 */
	public GetCellCurrRowColItemValue(HashMap _dataMap) {
		numberOfParameters = 1; //
		this.rowDataMap = _dataMap; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		try {
			//��ȡ�ò���!!
			checkStack(inStack);
			Object param_1 = inStack.pop();
			String str_rowcol = "" + param_1; ////
			if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
				BillCellPanel cellPanel = (BillCellPanel) billPanel; //
				String str_value = null; //
				if (this.rowcoltype == 1) { //�����ȡͬһ�еĵڼ���
					str_value = cellPanel.getValueAt(this.currrow, Integer.parseInt(str_rowcol) - 1); //
				} else if (this.rowcoltype == 2) { //�����ȡͬһ�еĵڼ���
					str_value = cellPanel.getValueAt(Integer.parseInt(str_rowcol) - 1, this.currcol); //
				}

				if (this.isNumber) { //���������
					if (str_value == null || str_value.trim().equals("")) {
						inStack.push(new Double(0));
					} else {
						inStack.push(new Double(str_value));
					}
				} else { //������ַ���
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
