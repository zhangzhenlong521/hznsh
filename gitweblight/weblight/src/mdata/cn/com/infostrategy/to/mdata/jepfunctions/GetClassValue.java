/**************************************************************************
 * $RCSfile: GetClassValue.java,v $  $Revision: 1.4 $  $Date: 2012/09/14 09:22:56 $
 **************************************************************************/

package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.ArrayList;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class GetClassValue extends PostfixMathCommand {

	private int li_type = -1;

	public GetClassValue() {// 这个参数必须要有，，，TNND，搞了半天，真是太不爽了.
		numberOfParameters = -1;
	}

	public GetClassValue(int _type) {
		numberOfParameters = -1;
		li_type = _type;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);

		String str_clsname = null;
		ArrayList al_pars = new ArrayList(); //
		for (int i = 0; i < curNumberOfParameters; i++) {
			if (i == curNumberOfParameters - 1) {
				str_clsname = (String) inStack.pop(); //
			} else {
				al_pars.add(inStack.pop()); //
			}
		}

		String[] str_pars = new String[al_pars.size()]; //
		int li_index = 0; //
		for (int i = al_pars.size() - 1; i >= 0; i--) {
			str_pars[li_index] = (String) al_pars.get(i); //
			li_index++;
		}

		try {
			IClassJepFormulaParseIFC ifc = (IClassJepFormulaParseIFC) Class.forName((String) str_clsname).newInstance(); //
			String str_returnvalue = ifc.getForMulaValue(str_pars); //
			inStack.push(str_returnvalue); //
		} catch (Exception ex) {
			ex.printStackTrace(); //
			inStack.push(ex); //
		}

	}
}
