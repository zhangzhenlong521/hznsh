package cn.com.pushworld.salary.bs.targetcheck;

import java.math.BigDecimal;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.to.jepFunction.AbstractFormulaClassIfc;

public class PersonDLTargetMoneyReviseImpl implements AbstractFormulaClassIfc {

	public void onExecute(HashVO factorVO, HashVO baseHashVO, SalaryFomulaParseUtil parseUtil) throws Exception {
		String money = baseHashVO.getStringValue("money");
		parseUtil.putDefaultFactorVO("����", "[��������.money]/[ĳԱ��ĳ����ָ��Ӧ��Ч�湤��]", "��ʽһ", "", "2λС��");
		Object n = parseUtil.onExecute(parseUtil.getFoctorHashVO("��ʽһ"), baseHashVO, new StringBuffer());

		parseUtil.putDefaultFactorVO("����", "[���������ڴ�����]/[�������˾����ڴ�����]", "��ʽ��", "", "2λС��");
		Object m = parseUtil.onExecute(parseUtil.getFoctorHashVO("��ʽ��"), baseHashVO, new StringBuffer());

		double double_n = new BigDecimal(String.valueOf(n)).doubleValue();
		double double_m = new BigDecimal(String.valueOf(m)).doubleValue();
		BigDecimal personsavemoney = new BigDecimal(String.valueOf(parseUtil.onExecute(parseUtil.getFoctorHashVO("���������ڴ�����"), baseHashVO, new StringBuffer()))).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal avgsavemoney = new BigDecimal(String.valueOf(parseUtil.onExecute(parseUtil.getFoctorHashVO("�������˾����ڴ�����"), baseHashVO, new StringBuffer()))).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal ynmoney = new BigDecimal(String.valueOf(parseUtil.onExecute(parseUtil.getFoctorHashVO("ĳԱ��ĳ����ָ��Ӧ��Ч�湤��"), baseHashVO, new StringBuffer()))).setScale(2, BigDecimal.ROUND_HALF_UP);
		//		m=[���������ڴ�����]/[�������˾����ڴ�����];
		BigDecimal big_last_money = null;
		StringBuffer descr = new StringBuffer();
		descr.append("\r\n--------------ִ�д������-----------------");
		descr.append("\r\n���������ڴ�����:" + personsavemoney);
		descr.append("\r\n�������˾����ڴ�����:" + avgsavemoney);
		if (double_n < 1) {
			big_last_money = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
			descr.append("\r\n��Ա����ָ��ʵ��Ч�湤�ʵ����ó�Ч�湤��.");
		} else if (double_m >= double_n) {
			big_last_money = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
			descr.append("\r\n��Ա����ָ�� ʵ��Ч�湤��/��ָ��Ӧ��Ч�湤��=" + double_n);
			descr.append("\r\n��Ա����ָ��[���������ڴ�����]/[�������˾����ڴ�����]=" + double_m + ",���Ҹ�ֵ����" + double_n + ",˵������Ա�����ɺܺ�");
		} else if (double_m < 1) {
			big_last_money = ynmoney;
			descr.append("\r\n��Ա����ָ��[���������ڴ�����]/[�������˾����ڴ�����]=" + double_m + ",���Ĵ�����С��ƽ��ֵ���Ѹ�ָ��ʵ��Ч�湤�ʵ���Ϊ�ó���Ч�湤��" + big_last_money.toString());
		} else {
			big_last_money = new BigDecimal(ynmoney.doubleValue() * double_m).setScale(2, BigDecimal.ROUND_HALF_UP);
			descr.append("\r\n��Ա����ָ�� ʵ��Ч�湤��/��ָ��Ӧ��Ч�湤��=" + double_n);
			descr.append("\r\n��Ա����ָ��[���������ڴ�����]/[�������˾����ڴ�����]=" + double_m + ",���Ĵ��������ƽ��������,���Ǳ���С��" + double_n + "Ч�湤�ʵ���Ϊ" + ynmoney.doubleValue() + "*" + double_m + "=" + big_last_money.toString());
		}
		String descr_old = baseHashVO.getStringValue("descr");
		descr_old = descr_old + descr.toString();
		String updateSql = "update sal_person_check_score set money = '" + big_last_money.toString() + "',descr='" + descr_old + "'  where id = " + baseHashVO.getStringValue("id");
		baseHashVO.setAttributeValue("descr", descr_old);
		baseHashVO.setAttributeValue("money", big_last_money.toString());
		new CommDMO().executeUpdateByDS(null, updateSql);
	}
};
