package cn.com.infostrategy.ui.mdata.formatcomp;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.mdata.BillFormatPanel;

/**
 * 刷新事件绑定公式,异常有用,大大提高效率!!
 * @author xch
 *
 */
public class GetRegFormulaParse extends PostfixMathCommand {
	private BillFormatPanel formatpanel = null; //
	private String str_regformula1;
	private String str_regformula2;
	private String str_regformula3;

	public GetRegFormulaParse(BillFormatPanel _billcellpanel, String _regformula1, String _regformula2, String _regformula3) {
		this.formatpanel = _billcellpanel; //
		this.str_regformula1 = _regformula1; //
		this.str_regformula2 = _regformula2; //
		this.str_regformula3 = _regformula3; //

		numberOfParameters = 1; //
	}

	public void run(Stack inStack) throws ParseException {
		String par_regcode = (String) inStack.pop(); //被动刷新的表的外键
		if (par_regcode.trim().equalsIgnoreCase("1")) {
			System.out.println("第一个注册公式[" + str_regformula1 + "]!!!"); //
			inStack.push(str_regformula1); //
		} else if (par_regcode.trim().equalsIgnoreCase("2")) {
			System.out.println("第2个注册公式[" + str_regformula2 + "]!!!"); //
			inStack.push(str_regformula2); //
		} else if (par_regcode.trim().equalsIgnoreCase("3")) {
			inStack.push(str_regformula3); //
		}
	}
}
