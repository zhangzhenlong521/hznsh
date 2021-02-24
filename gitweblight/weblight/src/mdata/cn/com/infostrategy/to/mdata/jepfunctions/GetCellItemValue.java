package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.HashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;

/**
 * ȡ������е�ĳһ���ֵ!!
 * �ú����ڿͻ�����������˶���ʹ��!!!
 * @author xch
 *
 */
public class GetCellItemValue extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private boolean isNumber = false;

	//BS����Ĳ���
	private HashMap colDataTypeMap = null; //һ������!!
	private HashMap rowDataMap = null; //һ������!!

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetCellItemValue(BillPanel _billPanel, boolean _isnumber) {
		numberOfParameters = 1; //ֻ��һ������������������Ժ���������仯������������ǰ�滹���Լ�����ҳ�����ĸ����ֵ������ţ��Ӷ�ʵ�����֮�������!!
		this.billPanel = _billPanel;
		this.isNumber = _isnumber; //�Ƿ�������
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �ڷ������˵��õĹ��췽��
	 * @param dataMap
	 */
	public GetCellItemValue(HashMap _dataMap) {
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
		String str_itemKey = (String) param_1; ////
		String str_value = null;
		if (callType == WLTConstants.JEPTYPE_UI) { //����ǿͻ��˵���
			BillCellPanel cellPanel = (BillCellPanel) billPanel;
			str_value = cellPanel.getValueAt(str_itemKey); //
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

		}
	}

	public void setRowDataMap(HashMap rowDataMap) {
		this.rowDataMap = rowDataMap;
	}

	public void setColDataTypeMap(HashMap colDataTypeMap) {
		this.colDataTypeMap = colDataTypeMap;
	}
}
