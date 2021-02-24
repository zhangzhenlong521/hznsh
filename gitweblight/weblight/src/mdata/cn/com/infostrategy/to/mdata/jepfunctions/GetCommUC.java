package cn.com.infostrategy.to.mdata.jepfunctions;

import java.util.Stack;

import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.PostfixMathCommand;

import cn.com.infostrategy.to.mdata.CommUCDefineVO;

/**
 * 通用的取得控件类型!!!以前是一个个的公式与VO,后来发现扩展不方便!!!而且会产生大量类!!
 * 所以弄成一个通用的公式与VO,通用VO是CommUCDefineVO
 * @author xch
 *
 */
public class GetCommUC extends PostfixMathCommand {
	public GetCommUC() {
		numberOfParameters = -1;
	}

	public void run(Stack _inStack) throws ParseException {
		try {
			checkStack(_inStack); //
			Object[] str_pars = new String[curNumberOfParameters]; //构造参数列表!应该是一对,即位数和是偶数!
			for (int i = str_pars.length - 1; i >= 0; i--) { //倒叙获得函数参数!!
				str_pars[i] = _inStack.pop(); //
			}
			CommUCDefineVO commUCVO = new CommUCDefineVO(); //创建控件定义VO
			commUCVO.setTypeName((String) str_pars[0]); //第一个肯定是控件类型,其余的分别是参数名与参数值!!
			//如果后面有参数,则依次塞入!!!
			for (int i = 0; i < (str_pars.length - 1) / 2; i++) { //
				String str_key = (String) str_pars[i * 2 + 1]; //因为第一个是类型,所以要加1
				String str_value = (String) str_pars[i * 2 + 2]; //因为第一个是类型,并且是第二个,所以要加2
				commUCVO.setConfValue(str_key, str_value); //设置值!!
			}
			_inStack.push(commUCVO); //返回控件定义VO!!!
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}
}
