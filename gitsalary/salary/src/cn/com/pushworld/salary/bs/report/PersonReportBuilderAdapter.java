package cn.com.pushworld.salary.bs.report;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.pushworld.salary.to.baseinfo.FormulaTool;

public class PersonReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	private CommDMO commdmo = null;
	private ReportUtil reportutil = null;
	private TBUtil tbutil = null;

	public HashVO[] buildReportData(HashMap consMap) throws Exception {
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("select id,name 姓名,cardid 身份证号,politicalstatus 政治面貌,degree 学历,posttitle 职称,joinworkdate 参加工作日期" + " from v_sal_personinfo where 1=1 ");

		String querycorp = (String) consMap.get("querycorp");
		if (querycorp != null && !querycorp.equals("")) {
			querycorp = getTBUtil().getInCondition(querycorp);
			sb_sql.append(" and maindeptid in(" + querycorp + ")");
		}

		HashVO[] hashvo = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString());

		FormulaTool tool = new FormulaTool();
		for (int i = 0; i < hashvo.length; i++) {
			hashvo[i].setAttributeValue("年龄", tool.getAgeByIdCard(hashvo[i].getStringValue("身份证号", "")));
			hashvo[i].setAttributeValue("工龄", tool.getWorkAgeByWorkDate(hashvo[i].getStringValue("参加工作日期", "")));
		}

		getReportUtil().leftOuterJoin_NumberAreaFromDouble(hashvo, "年龄段", "年龄", new String[] { "0-20", "20-25", "25-30", "30-35", "35-40", "40-45", "45-50", "50-55", "55-60", "60-100" });
		getReportUtil().leftOuterJoin_NumberAreaFromDouble(hashvo, "工龄段", "工龄", new String[] { "0-2", "3-5", "6-10", "11-15", "16-20", "21-25", "26-30", "31-35", "36-40", "41-45", "46-50", "51-100" });

		return hashvo;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "年龄段", "政治面貌", "学历", "职称", "工龄段" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al_vos = new ArrayList();
		BeforeHandGroupTypeVO bhGroupVO = null;

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-年龄段"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "年龄段" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-政治面貌"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "政治面貌" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-学历"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "学历" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-职称"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "职称" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-工龄段"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "工龄段" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		ArrayList al_vos = new ArrayList();
		BeforeHandGroupTypeVO bhGroupVO = null;

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-年龄段"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "年龄段" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-政治面貌"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "政治面貌" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-学历"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "学历" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-职称"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "职称" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-工龄段"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "工龄段" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "数量", "count" }, { "数量-占比", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public String getDrillTempletCode() throws Exception {
		return "REPORTUSE_CODE1";
	}

	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		map.put("年龄段", new String[] { "0-20", "20-25", "25-30", "30-35", "35-40", "40-45", "45-50", "50-55", "55-60", "60-100" });
		map.put("工龄段", new String[] { "0-2", "3-5", "6-10", "11-15", "16-20", "21-25", "26-30", "31-35", "36-40", "41-45", "46-50", "51-100" });
		try {
			map.put("学历", getCommDMO().getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type = '薪酬_学历' order by seq"));
			map.put("职称", getCommDMO().getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type = '薪酬_职称' order by seq"));
			map.put("政治面貌", getCommDMO().getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type = '薪酬_政治面貌' order by seq"));
		} catch (Exception e) {
			WLTLogger.getLogger(PersonReportBuilderAdapter.class).error("", e);
		}
		return map;
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
