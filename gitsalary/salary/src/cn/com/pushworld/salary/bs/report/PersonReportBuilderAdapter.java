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
		sb_sql.append("select id,name ����,cardid ���֤��,politicalstatus ������ò,degree ѧ��,posttitle ְ��,joinworkdate �μӹ�������" + " from v_sal_personinfo where 1=1 ");

		String querycorp = (String) consMap.get("querycorp");
		if (querycorp != null && !querycorp.equals("")) {
			querycorp = getTBUtil().getInCondition(querycorp);
			sb_sql.append(" and maindeptid in(" + querycorp + ")");
		}

		HashVO[] hashvo = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString());

		FormulaTool tool = new FormulaTool();
		for (int i = 0; i < hashvo.length; i++) {
			hashvo[i].setAttributeValue("����", tool.getAgeByIdCard(hashvo[i].getStringValue("���֤��", "")));
			hashvo[i].setAttributeValue("����", tool.getWorkAgeByWorkDate(hashvo[i].getStringValue("�μӹ�������", "")));
		}

		getReportUtil().leftOuterJoin_NumberAreaFromDouble(hashvo, "�����", "����", new String[] { "0-20", "20-25", "25-30", "30-35", "35-40", "40-45", "45-50", "50-55", "55-60", "60-100" });
		getReportUtil().leftOuterJoin_NumberAreaFromDouble(hashvo, "�����", "����", new String[] { "0-2", "3-5", "6-10", "11-15", "16-20", "21-25", "26-30", "31-35", "36-40", "41-45", "46-50", "51-100" });

		return hashvo;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "�����", "������ò", "ѧ��", "ְ��", "�����" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "����" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al_vos = new ArrayList();
		BeforeHandGroupTypeVO bhGroupVO = null;

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-�����"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "�����" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-������ò"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "������ò" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-ѧ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "ѧ��" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-ְ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "ְ��" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-�����"));
		bhGroupVO.setRowHeaderGroupFields(new String[] {});
		bhGroupVO.setColHeaderGroupFields(new String[] { "�����" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		ArrayList al_vos = new ArrayList();
		BeforeHandGroupTypeVO bhGroupVO = null;

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-�����"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "�����" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-������ò"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "������ò" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-ѧ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "ѧ��" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-ְ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "ְ��" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
		bhGroupVO.setColGroupTiled(true);
		bhGroupVO.setType("CHART");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-�����"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "�����" });
		bhGroupVO.setColHeaderGroupFields(new String[] {});
		bhGroupVO.setComputeGroupFields(new String[][] { { "����", "count" }, { "����-ռ��", "PercentCount" } });
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
		map.put("�����", new String[] { "0-20", "20-25", "25-30", "30-35", "35-40", "40-45", "45-50", "50-55", "55-60", "60-100" });
		map.put("�����", new String[] { "0-2", "3-5", "6-10", "11-15", "16-20", "21-25", "26-30", "31-35", "36-40", "41-45", "46-50", "51-100" });
		try {
			map.put("ѧ��", getCommDMO().getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type = 'н��_ѧ��' order by seq"));
			map.put("ְ��", getCommDMO().getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type = 'н��_ְ��' order by seq"));
			map.put("������ò", getCommDMO().getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type = 'н��_������ò' order by seq"));
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
