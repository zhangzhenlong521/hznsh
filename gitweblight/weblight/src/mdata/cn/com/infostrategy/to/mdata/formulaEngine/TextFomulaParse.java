package cn.com.infostrategy.to.mdata.formulaEngine;

import cn.com.infostrategy.to.common.HashVO;

/**
 * ���ӹ�ʽ����Ϊ[�ı�]����ִ�й��ߡ�
 * @author haoming
 * create by 2013-7-4
 */
public class TextFomulaParse extends AbstractFomulaParse {
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String value = _factorHashVO.getStringValue("value");// �趨��ֵ�������ǹ�ʽ��
		String conditions = _factorHashVO.getStringValue("conditions"); // ����
		boolean issql = false;
		if (value != null && value.trim().indexOf("select ") == 0 && value.contains(" from ")) { //sql
			issql = true;
		}
		if (value != null && value.contains("[")) { // ��������
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
