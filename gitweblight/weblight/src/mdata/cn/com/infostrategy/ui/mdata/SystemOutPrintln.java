package cn.com.infostrategy.ui.mdata;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 直接抛出一个异常...
 * @author xch
 *
 */
public class SystemOutPrintln extends PostfixMathCommand {

	public SystemOutPrintln() {
		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		final Object par_passive_panel = inStack.pop(); // 被刷新的面板
		String str_message = "" + par_passive_panel;
		System.out.println("公式引擎函数SystemOutPrintln输出一段话[" + str_message + "]");
	}
}
