package cn.com.infostrategy.ui.mdata;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * ֱ���׳�һ���쳣...
 * @author xch
 *
 */
public class SystemOutPrintln extends PostfixMathCommand {

	public SystemOutPrintln() {
		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		final Object par_passive_panel = inStack.pop(); // ��ˢ�µ����
		String str_message = "" + par_passive_panel;
		System.out.println("��ʽ���溯��SystemOutPrintln���һ�λ�[" + str_message + "]");
	}
}
