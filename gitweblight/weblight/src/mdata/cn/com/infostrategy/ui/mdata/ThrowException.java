package cn.com.infostrategy.ui.mdata;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTAppException;

/**
 * ֱ���׳�һ���쳣...
 * @author xch
 *
 */
public class ThrowException extends PostfixMathCommand {

	public ThrowException() {
		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		final Object par_passive_panel = inStack.pop(); // ��ˢ�µ����
		String str_message = (String) par_passive_panel;
		inStack.push(new WLTAppException(str_message)); // ����һ���쳣
	}
}
