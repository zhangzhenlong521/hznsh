package cn.com.infostrategy.to.mdata.jepfunctions;

import java.text.DecimalFormat;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.ui.common.MessageBox;

/**
 * 将数字转换成相应的格式，第一个参数为格式，第二个参数为数字（有可能为字符串）
 * @author lichunjuan
 *
 */
public class GetDecimalFormatNumber extends PostfixMathCommand {

	public GetDecimalFormatNumber() {
		numberOfParameters = -1;
	}

	public void run(Stack inStack) throws ParseException {
		checkStack(inStack);
		String[] str_pa = new String[curNumberOfParameters]; //创建报有参数列表
		for (int i = str_pa.length - 1; i >= 0; i--) { //倒叙获得函数参数
			str_pa[i] = (String) inStack.pop();
		}		
		if(str_pa.length==2){
			Object param_2 = str_pa[1]; //第二个参数
			Object param_1 = str_pa[0]; //第一个参数

			String str_format = String.valueOf(param_1);
			String str_number = String.valueOf(param_2);
			str_number = str_number.replaceAll(",", "");

			if (str_format == null || str_format.trim().equals("") || str_number == null || str_number.trim().equals("")) {
				return;
			}
			DecimalFormat df = new DecimalFormat(str_format);
			String str_return = "";
			try {
				double number = Double.parseDouble(str_number);
				df.setMinimumIntegerDigits(1);
				str_return = df.format(number);
				inStack.push(str_return);
			} catch (Exception e) {
				inStack.push(str_return);
				MessageBox.show("格式不正确，请重新输入！");
			}
			
		}else{
			Object param_2 = str_pa[2]; //第三个参数
			Object param_1 = str_pa[1]; //第二个参数
			Object param_0 = str_pa[0]; //第一个参数

			String str_name = String.valueOf(param_0);
			String str_format = String.valueOf(param_1);
			String str_number = String.valueOf(param_2);
			str_number = str_number.replaceAll(",", "");

			if (str_format == null || str_format.trim().equals("") || str_number == null || str_number.trim().equals("")) {
				return;
			}
			DecimalFormat df = new DecimalFormat(str_format);
			String str_return = "";
			try {
				double number = Double.parseDouble(str_number);
				df.setMinimumIntegerDigits(1);
				str_return = df.format(number);
				inStack.push(str_return);
			} catch (Exception e) {
				inStack.push(str_return);
				MessageBox.show("【"+str_name+"】格式不正确，请重新输入！");
			}
			
		}
	}
}
