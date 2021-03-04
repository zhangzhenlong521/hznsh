/**************************************************************************
 * $RCSfile: Length.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class Length extends PostfixMathCommand {

	public Length() {
		numberOfParameters = 1;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String str_text = "" + inStack.pop(); //
		inStack.push(new Integer(str_text.length())); //
	}
}
