package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
  * ���ӹ�ʽ����Ϊ[ϵͳ����]����ִ�й��ߡ�
 * @author haoming
 * create by 2013-7-4
 */
public class SystemObjectFomulaParse extends AbstractFomulaParse {

	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String sourceType = _factorHashVO.getStringValue("sourcetype"); // �õ�������Դ���͡�
		String value = _factorHashVO.getStringValue("value");// �趨��ֵ�������ǹ�ʽ��
		String conditions = _factorHashVO.getStringValue("conditions"); // ����
		String name = _factorHashVO.getStringValue("name");
		if (TBUtil.getTBUtil().isEmpty(sourceType) || TBUtil.getTBUtil().isEmpty(value)) {
			return "";
		}
		if (value.contains("[") && !value.contains("ƥ��")) { // ��������
			value = (String) util.getReflectOtherFactor(value, _baseDataHashVO, rtStr, _level, true);
		} else {
			value = (String) util.getReflectOtherFactor(value, _baseDataHashVO, rtStr, _level, false);
		}

		Object obj = null;
		String condition = null;
		if (!tbutil.isEmpty(conditions)) {
			String[] str = conditions.split("="); // ��Ҫ��
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
				throw new Exception("[" + name + "]����[" + value + "]������[" + conditions + "]�������ȡ�󣬴Ӵ���ֵ��ȡ��������ֵ!\r\n����ֵ�������£�\r\n" + _baseDataHashVO.getSBStr());
			}
			condition = conditonWhere + "='" + conditionValue + "'";
		}
		if (value.contains("ƥ��")) { //����Ǵ�map������ƥ�������
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
