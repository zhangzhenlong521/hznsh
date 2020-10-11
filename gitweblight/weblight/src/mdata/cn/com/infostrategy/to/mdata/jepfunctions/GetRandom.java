package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Random;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.UIUtil;

public class GetRandom  extends PostfixMathCommand{
	public GetRandom() {
		numberOfParameters = 0;
	}
	
	public void run(Stack inStack) throws ParseException {
		try {
			checkStack(inStack); //
			StringBuffer sb_code = new StringBuffer("1234567890");
			StringBuffer sb = new StringBuffer();
			sb.append(UIUtil.getCurrDate().substring(0, 4)+"-");
			Random r = new Random();
			for (int i = 0; i < sb_code.length(); i++) {
				sb.append(sb_code.charAt(r.nextInt(sb_code.length())));
			}
			String str_code = sb.toString();
			inStack.push(str_code);
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
