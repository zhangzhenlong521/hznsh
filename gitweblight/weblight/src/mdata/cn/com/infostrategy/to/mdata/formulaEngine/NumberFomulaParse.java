package cn.com.infostrategy.to.mdata.formulaEngine;

import java.math.BigDecimal;

import cn.com.infostrategy.to.common.HashVO;

/**
 * ���ӹ�ʽ����Ϊ[����]����ִ�й��ߡ�
 * 
 * @author haoming create by 2013-7-4
 */
public class NumberFomulaParse extends AbstractFomulaParse {
	@Override
	public Object parse(SalaryFomulaParseUtil util, HashVO _factorHashVO, HashVO _baseDataHashVO, int _level, StringBuffer rtStr) throws Exception {
		String value = _factorHashVO.getStringValue("value");// �趨��ֵ�������ǹ�ʽ��
		String conditions = _factorHashVO.getStringValue("conditions"); // ����
		String name = _factorHashVO.getStringValue("name");
		String extend = _factorHashVO.getStringValue("extend");
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
		Object obj = util.execFormula(value, _baseDataHashVO, sourceValue); //�÷����Ѿ�����������sql�����������
		if (obj == null || "".equals(obj)) {
			if (rtStr != null)
				rtStr.append("�ڼ���[" + name + "]ʱ,����Ľ��Ϊ��ֵ��ִ�й�ʽԭ[" + _factorHashVO.getStringValue("value") + "]+ת����ʽ[" + value + "].ϵͳĬ�Ϸ���0.���ʵ");
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
			if (extend == null || extend.equals("") || "����".equals(extend.trim())) {
				if (!(obj instanceof Integer)) {
					BigDecimal big = new BigDecimal(String.valueOf(obj).trim()).setScale(0, BigDecimal.ROUND_HALF_UP);
					obj = big.intValue();
				}
			} else {
				String digit = extend.substring(0, extend.indexOf("λ"));
				obj = new BigDecimal(String.valueOf(obj).trim()).setScale(Integer.parseInt(digit), BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		} catch (Exception ex) {
			throw new Exception("[" + name + "][" + value + "]��,������ֵΪ��" + obj + "��,��������λ������[" + extend + "]");
		}
		return obj;
	}
}
