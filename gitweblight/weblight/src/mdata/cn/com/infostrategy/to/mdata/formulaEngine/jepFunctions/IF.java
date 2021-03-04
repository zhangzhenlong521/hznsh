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
 * �˴��൱��һ���jstl��
 * if(�������,��ô����ֵ,�������,��ô����,.....,�������,��ô����,������������)
 * @author haoming
 * create by 2013-8-8
 */
public class IF extends AbstractPostfixMathCommand implements CallbackEvaluationI {
	public IF(JEP jepParse) {
		super(jepParse);
	}

	/*
	 *����jep��jf��ԭ�� 
	 */
	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		int index = node.jjtGetNumChildren();
		for (int j = 0; j < index; j++) {
			if (j == index - 1 && j % 2 == 0) {//������һ��������������.�൱��defaultֵ
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
