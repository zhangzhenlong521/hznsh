package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

/**
 * ȡ������е�ĳһ���ֵ!!
 * �ú����ڿͻ�����������˶���ʹ��!!!
 * @author xch
 *
 */
public class ExecWLTAction extends PostfixMathCommand {
	private int callType = -1; //

	//UI����Ĳ���
	private BillPanel billPanel = null;
	private WLTButton wltButton = null;

	/**
	 * ȡ��ĳһ���ֵ!!
	 */
	public ExecWLTAction(BillPanel _billPanel, WLTButton _btn) {
		numberOfParameters = 1; ////ֻ��һ������������������Ժ���������仯������������ǰ�滹���Լ�����ҳ�����ĸ����ֵ������ţ��Ӷ�ʵ�����֮�������!!
		this.billPanel = _billPanel;
		this.wltButton = _btn; //
		callType = WLTConstants.JEPTYPE_UI; //�ͻ��˵���
	}

	/**
	 * �������߼�ִ��!!
	 */
	public void run(Stack inStack) throws ParseException {
		//��ȡ�ò���!!
		checkStack(inStack);
		String str_wltactionclassname = (String) inStack.pop(); ////
		try {
			WLTActionListener listener = (WLTActionListener) Class.forName(str_wltactionclassname).newInstance(); //
			WLTActionEvent event = new WLTActionEvent(wltButton, billPanel); //
			listener.actionPerformed(event); //
			inStack.push("ok"); //
		} catch (Exception e) {
			e.printStackTrace(); //
			inStack.push(new WLTAppException(e.getMessage())); //����һ���쳣
		}
	}

}
