package com.pushworld.ipushgrc.bs.cmpreport.p110;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

/**
 * 综合合规报告数据生成器
 * @author hm
 *
 */
public class CmpReportStaticBuildDate1 extends MultiLevelReportDataBuilderAdapter {
	private CommDMO commdmo = new CommDMO();;
	private HashVO[] returnvos = null;
	private ReportDMO dmo = new ReportDMO();
	private TBUtil tbutil = new TBUtil();

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {
		String reportyear = (String) _condition.get("reportyear");
		String reporttype = (String) _condition.get("reporttype");
		String reportcorp = (String) _condition.get("reportcorp");
		StringBuffer sb = new StringBuffer(" select id,reporttype 报告类型,reportcorp 报告机构,state 状态  from cmp_report where 1=1 ");
		if (reportyear != null && !reportyear.equals("")) {
			sb.append(" and reportyear in (" + tbutil.getInCondition(reportyear) + ")");
		}
		if (reporttype != null && !reporttype.equals("")) {
			sb.append(" and reporttype in (" + tbutil.getInCondition(reporttype) + ")");
		}
		if (reportcorp != null && !reportcorp.equals("")) {
			sb.append(" and reportcorp in (" + tbutil.getInCondition(reportcorp) + ")");
		}
		returnvos = commdmo.getHashVoArrayByDS(null, sb.toString());
		ReportDMO reportDMO = new ReportDMO(); //
		//报告机构【岳耀彪/2012-03-14】
		reportDMO.addOneFieldFromOtherTree(returnvos, "报告机构(第1层)", "报告机构", "select id,name,parentid from pub_corp_dept", 2, true, 2); //加上第1层
		reportDMO.addOneFieldFromOtherTree(returnvos, "报告机构(第2层)", "报告机构", "select id,name,parentid from pub_corp_dept", 3, true, 2); //加上第2层
		return returnvos;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "报告类型", "报告机构(第1层)", "报告机构(第2层)", "状态" };
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	@Override
	/**
	 * 钻取明细时的模板编码!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "CMP_REPORT_CODE1"; //
	}

	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			map.put("报告类型", commdmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='合规报告_报告类型' order by seq"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType(1);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType(2);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType(int _type) {
		ArrayList al = new ArrayList(); //
		if (_type == 1) {
			al.add(getBeforehandGroupType("报告类型_报告机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告类型_报告机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));

			al.add(getBeforehandGroupType("报告机构(第1层)_状态", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第2层)_状态", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告类型_状态", "数量", BeforeHandGroupTypeVO.COUNT));
			return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
		} else {
			al.add(getBeforehandGroupType("报告类型_报告机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告类型_报告机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));

			al.add(getBeforehandGroupType("报告机构(第1层)_状态", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第2层)_状态", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告类型_状态", "数量", BeforeHandGroupTypeVO.COUNT));
		}
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO getBeforehandGroupType(String _name, String typeName, String _type) {
		String[] names = _name.split("_");
		BeforeHandGroupTypeVO typeVo = new BeforeHandGroupTypeVO();
		typeVo.setName(_name);
		typeVo.setRowHeaderGroupFields(new String[] { names[0] });
		typeVo.setColHeaderGroupFields(new String[] { names[1] });
		typeVo.setComputeGroupFields(new String[][] { { typeName, _type } });
		return typeVo;
	}

}
