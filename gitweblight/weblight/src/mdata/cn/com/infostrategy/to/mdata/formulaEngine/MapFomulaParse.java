package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;

public class MapFomulaParse extends AbstractFomulaParse {

	@Override
	public Object parse(SalaryFomulaParseUtil util, HashVO factorHashVO, HashVO baseDataHashVO, int level, StringBuffer rtStr) throws Exception {
		String formula = factorHashVO.getStringValue("value");
		if (formula.substring(formula.indexOf("("), formula.indexOf(",")).contains("select ")) {//如果是sql传入
			formula = (String) util.getReflectOtherFactor(formula, baseDataHashVO, rtStr, level, true);
		} else {
			formula = (String) util.getReflectOtherFactor(formula, baseDataHashVO, rtStr, level);
		}
		Object obj = util.execFormula(formula);
		return obj;
	}

}
