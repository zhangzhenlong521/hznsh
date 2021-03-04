/**
 * 
 */
package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;
import org.nfunk.jep.type.Complex;

/**
 * 此处相当于一层的jstl。
 * if(如果成立,那么返回值,如果成立,那么返回,.....,如果成立,那么返回,都不成立返回)
 * @author haoming
 * create by 2013-8-8
 */
public class IF extends AbstractPostfixMathCommand implements CallbackEvaluationI {
	public IF(JEP jepParse) {
		super(jepParse);
	}

	/*
	 *调用jep中jf的原理。 
	 */
	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		int index = node.jjtGetNumChildren();
		for (int j = 0; j < index; j++) {
			if (j == index - 1 && j % 2 == 0) {//如果最后一个，而且是奇数.相当于default值
				return evaluatori.eval(node.jjtGetChild(j));
			}
			if (j % 2 == 0) {
				Object falg = evaluatori.eval(node.jjtGetChild(j));
				if (falg instanceof Boolean && ((Boolean) falg).booleanValue()) {
					return evaluatori.eval(node.jjtGetChild(j + 1));
				}
				double d;
				if (falg instanceof Complex)
					d = ((Complex) falg).re();
				else if (falg instanceof Number)
					d = ((Number) falg).doubleValue();
				else
					throw new ParseException("Condition in if operator must be double or complex");
				if (d > 0.0D) {
					return evaluatori.eval(node.jjtGetChild(j + 1));
				}
			}
		}
		return null;
	}
}
