package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.LinkedHashMap;
import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

/**
 * 将所有参数是奇偶位分别为key与value灌入到一个哈希表中! 返回一个HashMap
 * 注意,它返回的不是字符串而是任意对象!! 以后凡是遇到的参数都应该使用统一与万能的方法来配置!!!
 * @author xch
 *
 */
public class GetParAsMap extends PostfixMathCommand {
	public GetParAsMap() {
		numberOfParameters = -1;
	}

	public void run(Stack _inStack) throws ParseException {
		try {
			checkStack(_inStack); //
			Object[] str_pars = new String[curNumberOfParameters]; //构造参数列表!应该是一对,即位数和是偶数!
			for (int i = str_pars.length - 1; i >= 0; i--) { //倒叙获得函数参数!!
				str_pars[i] = _inStack.pop(); //
			}
			LinkedHashMap returnMap = new LinkedHashMap(); //
			for (int i = 0; i < str_pars.length / 2; i++) {  //
				String str_key = (String) str_pars[i * 2]; //
				Object obj_value = str_pars[i * 2 + 1]; //
				returnMap.put(str_key, obj_value); //
			}
			_inStack.push(returnMap); //再塞进去!!!返回的是个哈希表!!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}

	}
}
