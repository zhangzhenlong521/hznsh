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
public class GetCellSumValueByKeys extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private int currRow = 0; //��ǰ��
	private int currCol = 0; //��ǰ��

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetCellSumValueByKeys(BillPanel _billPanel, int _currRow, int _currCol) {
		numberOfParameters = -1;
		this.billPanel = _billPanel;
		this.currRow = _currRow; //��ǰ��.
		this.currCol = _currCol; //��ǰ��.
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �ڷ������˵��õĹ��췽��
	 * @param dataMap
	 */
	public GetCellSumValueByKeys(HashMap _dataMap) {
		numberOfParameters = -1; //
		callType = WLTConstants.JEPTYPE_BS; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
		checkStack(inStack);

		String[] str_pa = new String[curNumberOfParameters];
		for (int i = str_pa.length - 1; i >= 0; i--) {//�����ú�������
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
