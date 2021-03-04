package cn.com.pushworld.salary.bs;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * ����������Ⱦ�Ӫ�ƻ�ģ�����Dmo �����/2014-01-07��
 * 
 */
public class SalaryOperatePlanDMO extends AbstractDMO {
	private CommDMO dmo;
	private TBUtil tbutil;

	public CommDMO getDmo() {
		if (dmo == null) {
			dmo = new CommDMO();
		}
		return dmo;
	}

	public TBUtil getTBUtil() {
		if (tbutil == null) {
			tbutil = new TBUtil();
		}
		return tbutil;
	}

	//������Ⱦ�Ӫ�ƻ������/2014-01-07��
	public void copyOperatePlan(String _planid, String _newplanid) throws Exception {
		ArrayList sqlList = new ArrayList();
		//���Ƽƻ���Ŀ
		HashVO[] hashvos2 = getDmo().getHashVoArrayByDS(null, "select * from sal_year_plan_item where planid=" + _planid);
		if (hashvos2 != null && hashvos2.length > 0) {
			InsertSQLBuilder sqlBuilder2 = new InsertSQLBuilder("sal_year_plan_item");
			String[] keys2 = hashvos2[0].getKeys();
			long[] ids = getDmo().getSequenceBatchNextValByDS(null, "s_sal_year_plan_item", hashvos2.length);
			for (int i = 0; i < hashvos2.length; i++) {
				for (int j = 0; j < keys2.length; j++) {
					sqlBuilder2.putFieldValue(keys2[j], hashvos2[i].getStringValue(keys2[j]));
				}
				sqlBuilder2.putFieldValue("id", ids[i] + "");
				sqlBuilder2.putFieldValue("planid", _newplanid);
				sqlList.add(sqlBuilder2.getSQL());
			}
			getDmo().executeBatchByDS(null, sqlList);
		}
	}

	//�����Ⱦ�Ӫ�ƻ�֮����ʡ����/2014-01-09��
	public HashVO[] getOperatePlanFullfillRate(String _planid, String _months) throws Exception {
		String curryear = getDmo().getStringValueByDS(null, "select curryear from sal_year_operateplan where id=" + _planid);
		String[] str_months = getTBUtil().split(_months, "-");
		String month1 = str_months[0];
		String month2 = str_months[1];

		String monthstart = curryear + "-" + (month1.length() == 1 ? "0" + month1 : month1);
		String monthend = curryear + "-" + (month2.length() == 1 ? "0" + month2 : month2);
		HashVO[] planitems = getDmo().getHashVoArrayByDS(null, "select * from sal_year_plan_item where planid=" + _planid + " order by id");
		DecimalFormat df_2 = new DecimalFormat("#.00");//����2λС��
		df_2.setMinimumIntegerDigits(1);//����������������һλ
		for (int i = 0; i < planitems.length; i++) {
			String targetids = planitems[i].getStringValue("targetids");
			if (targetids == null || targetids.length() == 0) {
				planitems[i].setAttributeValue("realvalue", "0");//ʵ��ֵ
				planitems[i].setAttributeValue("fullfillrate", "0%");//�����
			} else {
				String formulatype = planitems[i].getStringValue("formulatype");
				String str_targetids = getTBUtil().getInCondition(targetids);
				String realvalue = "0";
				if ("��ƽ��".equals(formulatype)) {
					realvalue = getDmo().getStringValueByDS(null, "select sum(currvalue) from sal_dept_check_score where targetid in(" + str_targetids + ") and currvalue is not null and status='���ύ' and checkdate>='" + monthstart + "' and checkdate<='" + monthend + "'");
				} else {
					realvalue = getDmo().getStringValueByDS(null, "select avg(currvalue) from sal_dept_check_score where targetid in(" + str_targetids + ") and currvalue is not null and status='���ύ' and checkdate>='" + monthstart + "' and checkdate<='" + monthend + "'");
				}
				if (realvalue == null || "".equals(realvalue)) {
					realvalue = "0";
				}
				planitems[i].setAttributeValue("realvalue", realvalue);
				String planvalue = planitems[i].getStringValue("planvalue");
				if (planvalue == null || "".equals(planvalue) || "0".equals(planvalue)) {
					planitems[i].setAttributeValue("fullfillrate", "-");//�����
				} else {
					double double_planvalue = Double.parseDouble(planvalue);
					double double_realvalue = Double.parseDouble(realvalue);
					planitems[i].setAttributeValue("fullfillrate", df_2.format(double_realvalue * 100 / double_planvalue) + "%");//�����
				}
			}
		}
		return planitems;
	}
}
