/**************************************************************************
 * $RCSfile: GetClientEnvironmentPut.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.ClientEnvironment;

public class GetClientEnvironmentPut extends PostfixMathCommand {

	public GetClientEnvironmentPut() {
		numberOfParameters = -1;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //
			int li_pars = curNumberOfParameters;
			if (li_pars == 1) {
				Object param_1 = inStack.pop();
				String str_itemKey = (String) param_1; ////
				String str_result = (String) ClientEnvironment.getInstance().get(str_itemKey);
				inStack.push(str_result); //
			} else if (li_pars == 2) {
				String str_nvlValue = (String) inStack.pop(); //
				String str_itemKey = (String) inStack.pop(); //
				String str_result = (String) ClientEnvironment.getInstance().get(str_itemKey);
				if (str_result == null) {
					inStack.push(str_nvlValue); //
				} else {
					inStack.push(str_result); //
				}
			} else if (li_pars == 3) {
				String isPull = (String) inStack.pop(); //
				String str_nvlValue = (String) inStack.pop(); //
				String str_itemKey = (String) inStack.pop(); //
				String str_result = (String) ClientEnvironment.getInstance().get(str_itemKey);
				if (str_result == null) {
					inStack.push(str_nvlValue); //
				} else {
					inStack.push(str_result); //
				}
				if (isPull.equalsIgnoreCase("true") || isPull.equalsIgnoreCase("Y")) {  //
					ClientEnvironment.getInstance().remove(str_itemKey);  //
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

}
