package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

public class GetFillZeroNum extends PostfixMathCommand {

	public GetFillZeroNum() {
		numberOfParameters = 2;
	}

	public void run(Stack inStack) throws ParseException {
		try {
			Object param_1 = inStack.pop();
			String str_digit = (String) param_1; // //

			Object param_2 = inStack.pop();

			if (param_2 instanceof Double) {
				double str_itemKey = (Double) param_2;

				String str_value1 = Double.toString(str_itemKey);
			
				String[] temp = str_value1.split(".0");
				
				String str_value = temp[0];
				int digit = Integer.parseInt(str_digit);
				if (digit > str_value.length()) {
					if (digit - str_value.length() == 0) {

					} else if (digit - str_value.length() == 1) {
						str_value = "0" + str_value;

					} else if (digit - str_value.length() == 2) {
						str_value = "00" + str_value;
					} else if (digit - str_value.length() == 3) {
						str_value = "000" + str_value;
					} else if (digit - str_value.length() == 4) {
						str_value = "0000" + str_value;
					} else if (digit - str_value.length() == 5) {
						str_value = "00000" + str_value;
					} else if (digit - str_value.length() == 6) {
						str_value = "000000" + str_value;
					}
				}
				inStack.push(str_value); // ÷√»Î∂—’ª!!
			} else {
				String str_itemKey = (String) param_2; // //

				String str_value = str_itemKey;

				int digit = Integer.parseInt(str_digit);
				if (digit > str_value.length()) {
					if (digit - str_value.length() == 0) {
					} else if (digit - str_value.length() == 1) {
						str_value = "0" + str_value;

					} else if (digit - str_value.length() == 2) {
						str_value = "00" + str_value;
					} else if (digit - str_value.length() == 3) {
						str_value = "000" + str_value;
					} else if (digit - str_value.length() == 4) {
						str_value = "0000" + str_value;
					} else if (digit - str_value.length() == 5) {
						str_value = "00000" + str_value;
					} else if (digit - str_value.length() == 6) {
						str_value = "000000" + str_value;
					}
				}
				inStack.push(str_value); // ÷√»Î∂—’ª!!
			}

		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
}
