/**************************************************************************
 * $RCSfile: SetClientEnvironmentPut.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.ClientEnvironment;

public class SetClientEnvironmentPut extends PostfixMathCommand {

	public SetClientEnvironmentPut() {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack); //
		String str_value = (String) inStack.pop();
		String str_key = (String) inStack.pop();
		ClientEnvironment.getInstance().put(str_key, str_value); //
	}

}
