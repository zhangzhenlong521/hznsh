package cn.com.infostrategy.to.mdata.formulaEngine.jepFunctions;

import org.nfunk.jep.EvaluatorI;
import org.nfunk.jep.JEP;
import org.nfunk.jep.Node;
import org.nfunk.jep.ParseException;
import org.nfunk.jep.function.CallbackEvaluationI;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.formulaEngine.SalaryFomulaParseUtil;
/**
 * 暂时没有用到该解析器。
 * @author haoming
 * create by 2014-2-26
 */
public class GetSqlInCondition extends AbstractPostfixMathCommand implements CallbackEvaluationI {
	private TBUtil tbutil = new TBUtil();

	public GetSqlInCondition(JEP jepParse, Object wholeObjData, SalaryFomulaParseUtil salaryParseUtil, StringBuffer rtSb) {
		super(jepParse, wholeObjData, salaryParseUtil, rtSb);
	}

	public Object evaluate(Node node, EvaluatorI evaluatori) throws ParseException {
		int index = node.jjtGetNumChildren();
		Object obj[] = new Object[index];
		for (int j = 0; j < index; j++) {
			Node cnode = node.jjtGetChild(j); //获取到节点
			obj[j] = evaluatori.eval(cnode);
		}
		String incondition = null;
		if (obj.length == 2) {
			HashVO[] valueVO = null;
			if (obj[0] instanceof HashVO[]) {
				valueVO = (HashVO[]) obj[0];
			} else {
				throw new ParseException("GetSqlInCondition公式传入值有问题。");
			}
			incondition = salaryTBUtil.getInCondition(valueVO, String.valueOf(obj[1]));
		} else {
			incondition = tbutil.getInCondition(String.valueOf(obj[0]));
		}

		return incondition;
	}
}
