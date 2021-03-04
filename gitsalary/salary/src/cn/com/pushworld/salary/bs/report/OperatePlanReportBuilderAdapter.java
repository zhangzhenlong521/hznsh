package cn.com.pushworld.salary.bs.report;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * ��Ⱦ�Ӫ�ƻ�֮�ƻ�����ʱ������ݹ��졾���/2014-01-09��
 * @author lcj
 *
 */
public class OperatePlanReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	private CommDMO commdmo = null;
	private ReportUtil reportutil = null;
	private TBUtil tbutil = null;

	public HashVO[] buildReportData(HashMap consMap) throws Exception {
		String planid = (String) consMap.get("planid");
		String curryear = getCommDMO().getStringValueByDS(null, "select curryear from sal_year_operateplan where id=" + planid);
		String months = (String) consMap.get("months");
		String[] str_months = getTBUtil().split(months, "-");
		String month1 = str_months[0];
		String month2 = str_months[1];

		String monthstart = curryear + "-" + (month1.length() == 1 ? "0" + month1 : month1);
		String monthend = curryear + "-" + (month2.length() == 1 ? "0" + month2 : month2);
		HashVO[] planitems = getCommDMO().getHashVoArrayByDS(null, "select id,name ��Ŀ����,planvalue �ƻ�ֵ,unitvalue ��λ,targetids,formulatype from sal_year_plan_item where planid=" + planid + " order by id");
		DecimalFormat df_2 = new DecimalFormat("#.00");//����2λС��
		df_2.setMinimumIntegerDigits(1);//����������������һλ
		for (int i = 0; i < planitems.length; i++) {
			String targetids = planitems[i].getStringValue("targetids");
			if (targetids == null || targetids.length() == 0) {
				planitems[i].setAttributeValue("ʵ��ֵ", "0");
				planitems[i].setAttributeValue("�����", "0");//�����
			} else {
				String formulatype = planitems[i].getStringValue("formulatype");
				String str_targetids = getTBUtil().getInCondition(targetids);
				String realvalue = "0";
				if ("��ƽ��".equals(formulatype)) {
					realvalue = getCommDMO().getStringValueByDS(null, "select sum(currvalue) from sal_dept_check_score where targetid in(" + str_targetids + ") and currvalue is not null and status='���ύ' and checkdate>='" + monthstart + "' and checkdate<='" + monthend + "'");
				} else {
					realvalue = getCommDMO().getStringValueByDS(null, "select avg(currvalue) from sal_dept_check_score where targetid in(" + str_targetids + ") and currvalue is not null and status='���ύ' and checkdate>='" + monthstart + "' and checkdate<='" + monthend + "'");
				}
				if (realvalue == null || "".equals(realvalue)) {
					realvalue = "0";
				}
				planitems[i].setAttributeValue("ʵ��ֵ", realvalue);
				String planvalue = planitems[i].getStringValue("�ƻ�ֵ");
				if (planvalue == null || "".equals(planvalue)) {
					planitems[i].setAttributeValue("�����", "0");//�����
				} else {
					double double_planvalue = Double.parseDouble(planvalue);
					double double_realvalue = Double.parseDouble(realvalue);
					planitems[i].setAttributeValue("�����", df_2.format(double_realvalue * 100 / double_planvalue) + "");//�����
				}
			}
		}

		return planitems;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "��Ŀ����", "�ƻ�ֵ", "ʵ��ֵ", "�����" };
	}

	public String[] getSumFiledNames() {
		return new String[] {};
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al_vos = new ArrayList();
		BeforeHandGroupTypeVO bhGroupVO = null;

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-�ƻ������"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "��Ŀ����" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "�ƻ�ֵ", BeforeHandGroupTypeVO.SUM }, { "ʵ��ֵ", BeforeHandGroupTypeVO.SUM }, { "�����", BeforeHandGroupTypeVO.SUM, "�Ƿ���ٷֺ�=Y" } });
		bhGroupVO.setColGroupTiled(false);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);
		return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public String getDrillTempletCode() throws Exception {
		return "SAL_YEAR_PLAN_ITEM_LCJ_E01";
	}

	public CommDMO getCommDMO() {
		if (commdmo == null) {
			commdmo = new CommDMO();
		}
		return commdmo;
	}

	public ReportUtil getReportUtil() {
		if (reportutil == null) {
			reportutil = new ReportUtil();
		}
		return reportutil;
	}

	public TBUtil getTBUtil() {
		if (tbutil == null) {
			tbutil = new TBUtil();
		}
		return tbutil;
	}

}
