package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import java.util.List;
import java.util.Map;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;

import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;
/**
 * 计算数组，集合，Map长度。
 * @author haoming
 * create by 2013-8-27
 */
public class Length extends AbstractPostfixMathCommand implements CallbackEvaluationI {

	public Length(JEP jepParse, Object wholeObjData, SalaryFomulaParseUtil salaryParseUtil, StringBuffer rtSb) {
		super(jepParse, wholeObjData, salaryParseUtil, rtSb);
	}

	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		int index = node.jjtGetNumChildren();
		Object obj[] = new Object[index];
		int length = 0;
		for (int j = 0; j < index; j++) {
			Node cnode = node.jjtGetChild(j); //获取到节点
			obj[j] = evaluatori.eval(cnode);
		}
		if (obj.length > 0) {
			if (obj[0] instanceof Object[]) {
				length = ((Object[]) obj[0]).length;
			} else if (obj[0] instanceof List) {
				length = ((List) obj[0]).size();
			} else if (obj[0] instanceof Map) {
				length = ((Map) obj[0]).size();
			} else {
				length = obj.length;
			}

		}
		return length;
	}
}
