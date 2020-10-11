package cn.com.infostrategy.ui.mdata;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.common.WLTAppException;

/**
 * 直接抛出一个异常...
 * @author xch
 *
 */
public class ThrowException extends PostfixMathCommand {

	public ThrowException() {
		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		final Object par_passive_panel = inStack.pop(); // 被刷新的面板
		String str_message = (String) par_passive_panel;
		inStack.push(new WLTAppException(str_message)); // 返回一个异常
	}
}
