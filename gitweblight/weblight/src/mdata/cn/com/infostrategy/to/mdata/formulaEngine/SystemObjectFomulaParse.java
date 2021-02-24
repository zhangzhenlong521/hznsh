package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
  * 因子公式类型为[系统对象]解析执行工具。
 * @author haoming
 * create by 2013-7-4
 */
public class SystemObjectFomulaParse extends AbstractFomulaParse {

	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // 得到数据来源类型。
		String value = _factorHashVO.getStringValue("value");// 设定的值，可以是公式。
		String conditions = _factorHashVO.getStringValue("conditions"); // 条件
		String name = _factorHashVO.getStringValue("name");
		if (TBUtil.getTBUtil().isEmpty(sourceType) || TBUtil.getTBUtil().isEmpty(value)) {
			return "";
		}
		if (value.contains("[") && !value.contains("匹配")) { // 其他因子
			value = (String) util.getReflectOtherFactor(value, _baseDataHashVO, rtStr, _level, true);
		} else {
			value = (String) util.getReflectOtherFactor(value, _baseDataHashVO, rtStr, _level, false);
		}

		Object obj = null;
		String condition = null;
		if (!tbutil.isEmpty(conditions)) {
			String[] str = conditions.split("="); // 需要改
			String conditonWhere = null;
			String conditionValue = null;
			if (str.length == 2) {
				conditonWhere = str[0];
				String con = str[1];
				con = (String) util.getReflectOtherFactor(con, _baseDataHashVO, rtStr, _level);
				Object retVO = util.execFormula(con, _baseDataHashVO);
				if (retVO != null && !con.equals((String) retVO) && retVO instanceof String) {
					conditionValue = (String) retVO;
				} else {
					conditionValue = _baseDataHashVO.getStringValue(str[1]);
				}
			} else {
				conditonWhere = conditions;
				conditionValue = _baseDataHashVO.getStringValue(conditions);
			}
			if (conditionValue == null) {
				throw new Exception("[" + name + "]因子[" + value + "]的条件[" + conditions + "]解析后读取后，从传入值读取不到真正值!\r\n传入值内容如下：\r\n" + _baseDataHashVO.getSBStr());
			}
			condition = conditonWhere + "='" + conditionValue + "'";
		}
		if (value.contains("匹配")) { //如果是从map缓存中匹配出来的
			obj = util.execFormula(value, _baseDataHashVO);
			if (obj instanceof HashVO) {
				obj = new HashVO[] { (HashVO) obj };
			}
		} else {
			obj = util.execFormula(getHashVOByTableParseStr(value, condition), _baseDataHashVO);
		}
		return obj;
	}

	/*
	 * 
	 */
	protected String getHashVOByTableParseStr(String _tableName, String _condition) {
		StringBuffer formularStr = new StringBuffer();
		formularStr.append("getHashVOByTable(");
		formularStr.append("\"" + _tableName + "\",\"" + _condition + "\"");
		formularStr.append(")");
		return formularStr.toString();
	}

}
