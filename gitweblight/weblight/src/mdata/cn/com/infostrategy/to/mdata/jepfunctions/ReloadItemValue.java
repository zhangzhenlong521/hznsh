package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.BillPropPanel;

/**
 * ���¼���ĳһ�������!!!Ŀǰֻ֧�������������1!!,��Ϊ��������������֮������������������Ҫ����ˢ�±�һ���������е�����!!
 * @author xch
 *
 */
public class ReloadItemValue extends PostfixMathCommand {

	private BillPanel billPanel = null;

	private int li_row = -1; //

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public ReloadItemValue(BillPanel _billPanel, int _row) {
		numberOfParameters = 1; //��һ������,��ItemKey
		this.billPanel = _billPanel;
		this.li_row = _row; //
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		Object param_1 = inStack.pop();
		String str_itemKey = (String) param_1; //

		if (billPanel instanceof BillCardPanel) { //����ǿ�Ƭ���
			BillCardPanel cardPanel = (BillCardPanel) billPanel;
			
		} else if (billPanel instanceof BillListPanel) {
			BillListPanel listPanel = (BillListPanel) billPanel;  //
		} else if (billPanel instanceof BillPropPanel) {

		}
		inStack.push("ok"); //��Ϊ����ֵ��û��ʵ�ʷ���ֵ�����Ծͷ���һ��"ok"��ʾ��ֵ�ɹ���!!
	}
}
