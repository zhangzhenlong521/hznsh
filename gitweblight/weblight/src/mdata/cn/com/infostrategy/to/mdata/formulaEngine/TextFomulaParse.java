package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 因子公式类型为[文本]解析执行工具。
 * @author haoming
 * create by 2013-7-4
 */
public class TextFomulaParse extends AbstractFomulaParse {
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String value = _factorHashVO.getStringValue("value");// 设定的值，可以是公式。
		String conditions = _factorHashVO.getStringValue("conditions"); // 条件
		boolean issql = false;
		if (value != null && value.trim().indexOf("select ") == 0 && value.contains(" from ")) { //sql
			issql = true;
		}
		if (value != null && value.contains("[")) { // 其他因子
			value = (String) util.getReflectOtherFactor(value, _baseDataHashVO, rtStr, _level, issql);
		}
		Object sourceValue = null;
		if (conditions != null && !conditions.equals("")) {
			if (conditions.contains("[")) {
				conditions = (String) util.getReflectOtherFactor(conditions, _baseDataHashVO, rtStr, _level);
			}
			sourceValue = util.execFormula(conditions, _baseDataHashVO);
		}
		Object obj = util.execFormula(value, _baseDataHashVO, sourceValue);
		obj = String.valueOf(obj);
		if (obj == null) {
			obj = "null";
		}
		return obj;
	}

}
