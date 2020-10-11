package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.UIUtil;

public class GetFormatString extends PostfixMathCommand {

	public GetFormatString(int _type) {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			Object param_1 = inStack.pop();
			String str_itemKey = (String) param_1; // //
			Object param_2 = inStack.pop();
			String str_digit = (String) param_2; // //
			String str_value = UIUtil.getSequenceNextValByDS(null, str_itemKey);
			int n=Integer.parseInt(str_digit);
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<n;i++){
				sb.append("0");	
			}
			sb.append(str_value);
			sb.append(str_itemKey);
			
			String str=sb.toString();
			inStack.push(str); // ÖÃÈë¶ÑÕ»!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
}
