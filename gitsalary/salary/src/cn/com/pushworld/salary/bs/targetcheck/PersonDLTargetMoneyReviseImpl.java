package cn.com.pushworld.salary.bs.targetcheck;

import java.math.BigDecimal;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.to.jepFunction.AbstractFormulaClassIfc;

public class PersonDLTargetMoneyReviseImpl implements AbstractFormulaClassIfc {

	public void onExecute(HashVO factorVO, HashVO baseHashVO, SalaryFomulaParseUtil parseUtil) throws Exception {
		String money = baseHashVO.getStringValue("money");
		parseUtil.putDefaultFactorVO("数字", "[传入数据.money]/[某员工某定量指标应发效益工资]", "公式一", "", "2位小数");
		Object n = parseUtil.onExecute(parseUtil.getFoctorHashVO("公式一"), baseHashVO, new StringBuffer());

		parseUtil.putDefaultFactorVO("数字", "[考核人外勤存款余额]/[考核组人均外勤存款余额]", "公式二", "", "2位小数");
		Object m = parseUtil.onExecute(parseUtil.getFoctorHashVO("公式二"), baseHashVO, new StringBuffer());

		double double_n = new BigDecimal(String.valueOf(n)).doubleValue();
		double double_m = new BigDecimal(String.valueOf(m)).doubleValue();
		BigDecimal personsavemoney = new BigDecimal(String.valueOf(parseUtil.onExecute(parseUtil.getFoctorHashVO("考核人外勤存款余额"), baseHashVO, new StringBuffer()))).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal avgsavemoney = new BigDecimal(String.valueOf(parseUtil.onExecute(parseUtil.getFoctorHashVO("考核组人均外勤存款余额"), baseHashVO, new StringBuffer()))).setScale(2, BigDecimal.ROUND_HALF_UP);
		BigDecimal ynmoney = new BigDecimal(String.valueOf(parseUtil.onExecute(parseUtil.getFoctorHashVO("某员工某定量指标应发效益工资"), baseHashVO, new StringBuffer()))).setScale(2, BigDecimal.ROUND_HALF_UP);
		//		m=[考核人外勤存款余额]/[考核组人均外勤存款余额];
		BigDecimal big_last_money = null;
		StringBuffer descr = new StringBuffer();
		descr.append("\r\n--------------执行代码调控-----------------");
		descr.append("\r\n考核人外勤存款余额:" + personsavemoney);
		descr.append("\r\n考核组人均外勤存款余额:" + avgsavemoney);
		if (double_n < 1) {
			big_last_money = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
			descr.append("\r\n该员工该指标实发效益工资低于拿出效益工资.");
		} else if (double_m >= double_n) {
			big_last_money = new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP);
			descr.append("\r\n该员工该指标 实发效益工资/该指标应发效益工资=" + double_n);
			descr.append("\r\n该员工该指标[考核人外勤存款余额]/[考核组人均外勤存款余额]=" + double_m + ",而且该值大于" + double_n + ",说明该人员存款完成很好");
		} else if (double_m < 1) {
			big_last_money = ynmoney;
			descr.append("\r\n该员工该指标[考核人外勤存款余额]/[考核组人均外勤存款余额]=" + double_m + ",他的存款余额小于平均值，把该指标实发效益工资调整为拿出的效益工资" + big_last_money.toString());
		} else {
			big_last_money = new BigDecimal(ynmoney.doubleValue() * double_m).setScale(2, BigDecimal.ROUND_HALF_UP);
			descr.append("\r\n该员工该指标 实发效益工资/该指标应发效益工资=" + double_n);
			descr.append("\r\n该员工该指标[考核人外勤存款余额]/[考核组人均外勤存款余额]=" + double_m + ",他的存款余额高于平均存款余额,但是比例小于" + double_n + "效益工资调整为" + ynmoney.doubleValue() + "*" + double_m + "=" + big_last_money.toString());
		}
		String descr_old = baseHashVO.getStringValue("descr");
		descr_old = descr_old + descr.toString();
		String updateSql = "update sal_person_check_score set money = '" + big_last_money.toString() + "',descr='" + descr_old + "'  where id = " + baseHashVO.getStringValue("id");
		baseHashVO.setAttributeValue("descr", descr_old);
		baseHashVO.setAttributeValue("money", big_last_money.toString());
		new CommDMO().executeUpdateByDS(null, updateSql);
	}
};
