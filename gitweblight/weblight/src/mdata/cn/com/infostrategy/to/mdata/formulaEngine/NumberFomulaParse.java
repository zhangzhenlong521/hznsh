package cn.com.infostrategy.to.mdata.formulaEngine;

import java.math.BigDecimal;

import cn.com.infostrategy.to.common.HashVO;

/**
 * 因子公式类型为[数字]解析执行工具。
 * 
 * @author haoming create by 2013-7-4
 */
public class NumberFomulaParse extends AbstractFomulaParse {
	@Override
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String value = _factorHashVO.getStringValue("value");// 设定的值，可以是公式。
		String conditions = _factorHashVO.getStringValue("conditions"); // 条件
		String name = _factorHashVO.getStringValue("name");
		String extend = _factorHashVO.getStringValue("extend");
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
		Object obj = util.execFormula(value, _baseDataHashVO, sourceValue); //该方法已经帮助出了了sql的特殊情况。
		if (obj == null || "".equals(obj)) {
			if (rtStr != null)
				rtStr.append("在计算[" + name + "]时,求出的结果为空值。执行公式原[" + _factorHashVO.getStringValue("value") + "]+转换后公式[" + value + "].系统默认返回0.请核实");
			return 0;
		}
		try {
			if (obj instanceof HashVO) {
				obj = ((HashVO) obj).getStringValue(0);
			} else if (obj instanceof HashVO[]) {
				HashVO v[] = (HashVO[]) obj;
				if (v != null && v.length > 0) {
					obj = (v[0]).getStringValue(0);
				}
			}
			if (extend == null || extend.equals("") || "整数".equals(extend.trim())) {
				if (!(obj instanceof Integer)) {
					BigDecimal big = new BigDecimal(String.valueOf(obj).trim()).setScale(0, BigDecimal.ROUND_HALF_UP);
					obj = big.intValue();
				}
			} else {
				String digit = extend.substring(0, extend.indexOf("位"));
				obj = new BigDecimal(String.valueOf(obj).trim()).setScale(Integer.parseInt(digit), BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		} catch (Exception ex) {
			throw new Exception("[" + name + "][" + value + "]的,计算后的值为【" + obj + "】,保留数字位数配置[" + extend + "]");
		}
		return obj;
	}
}
