package cn.com.infostrategy.to.mdata.formulaEngine;

import bsh.TargetError;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 因子公式类型为[代码计算]解析执行工具。
 * @author haoming
 * create by 2013-7-4
 */
public class BshFomulaParse extends AbstractFomulaParse {

	@Override
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // 得到数据来源类型。
		String value = _factorHashVO.getStringValue("value");// 设定的值，可以是公式。
		String conditions = _factorHashVO.getStringValue("conditions"); // 条件
		String extend = _factorHashVO.getStringValue("extend");
		String name = _factorHashVO.getStringValue("name");
		Object sendTOBsh = null;
		if (conditions != null && !conditions.equals("")) {
			sendTOBsh = util.getReflectOtherFactor(conditions, _baseDataHashVO, rtStr, _level);
			sendTOBsh = util.execFormula(sendTOBsh.toString(), _baseDataHashVO);
		} else {
			sendTOBsh = _baseDataHashVO;
		}
		try {
			BshTool tool = new BshTool();
			return tool.getValueByBshFormula(value, sendTOBsh);
		} catch (Exception ex) {
			StringBuffer expsb = new StringBuffer();
			expsb.append("系统因子[" + name + "],类型[" + sourceType + "]计算期间发生异常");
			rtStr.append("因子[" + name + "]计算异常");
			if (_baseDataHashVO != null) {
				expsb.append("传入参与计算对象内容：" + _baseDataHashVO.getSBStr());
			}
			if (ex instanceof TargetError) {
				if (((TargetError) ex).getTarget() != null) {
					expsb.append("\r\n" + ((TargetError) ex).getTarget().getMessage());
				}
				throw new Exception(expsb.toString());
			} else {
				throw new Exception(expsb.toString() + ex.getMessage());
			}
		}
	}
}
