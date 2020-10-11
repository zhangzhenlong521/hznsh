package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;

/**
 * ȡ�ò������ݶ���!!!
 * @author xch
 *
 */
public class GetNotNullItemCount extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public GetNotNullItemCount(BillPanel _billPanel) {
		numberOfParameters = -1;
		this.billPanel = _billPanel;
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String[] str_pa = new String[curNumberOfParameters];
		for (int i = str_pa.length - 1; i >= 0; i--) {//�����ú�������
			str_pa[i] = (String) inStack.pop();
		}
		if (billPanel instanceof BillCardPanel) {
			int li_count = 0;
			BillCardPanel card = (BillCardPanel) billPanel; //
			for (int i = 0; i < str_pa.length; i++) { //
				Object obj_value = card.getValueAt(str_pa[i]); //
				if (obj_value == null || obj_value.toString().trim().equals("")) { //
				} else {
					li_count++; //
				}
			}
			inStack.push(new Integer(li_count)); //
		}

	}
}
