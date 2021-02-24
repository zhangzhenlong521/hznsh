package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTLogger;

/*
 * 1、该公式可以直接对HashVO数组中的某一个字段的值进行求平均
 * 2、直接传入数字，求平均。用double类型
 *  求平均。
 */
public class Avg extends AbstractPostfixMathCommand implements CallbackEvaluationI {

	public Avg(JEP jepParse) {
		super(jepParse);
	}

	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		int index = node.jjtGetNumChildren();
		Object obj[] = new Object[index];
		for (int j = 0; j < index; j++) {
			Node cnode = node.jjtGetChild(j); //获取到节点
			obj[j] = evaluatori.eval(cnode);
		}
		double f = 0;
		if (obj.length > 0 && obj[0] instanceof HashVO[]) {
			if (obj.length != 2) {
				throw new ParseException("用avg公式对HashVO数组进行计算，需要传入对象数组，计算字段的两个参数");
			}
			String vagkey = (String) obj[1];
			HashVO vos[] = (HashVO[]) obj[0];
			for (int i = 0; i < vos.length; i++) {
				f += vos[i].getDoubleValue(vagkey, 0d);
			}
			if (vos.length == 0) {
				return 0;
			} else {
				f = f / vos.length;
			}
		} else {

			for (int i = 0; i < obj.length; i++) {
				try {
					f += Double.parseDouble(obj[i] + "");
				} catch (Exception ex) {
					WLTLogger.getLogger(Avg.class).error("传入值转换成Float报错。" + Avg.class.getName(), ex);
				}
			}
			if (obj.length == 0) {
				f = 0;
			} else {
				f = f / obj.length;
			}
		}

		return f;

	}

}
