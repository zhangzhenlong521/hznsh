package com.pushworld.ipushgrc.bs.law.p070;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

public class LawReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	TBUtil tbutil = new TBUtil();

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		CommDMO dmo = new CommDMO();
		String lawtype = (String) condition.get("lawtype"); // ��������
		String state = (String) condition.get("state"); // ��ѯ����-����״̬
		String issuecorp = (String) condition.get("issuecorp"); // ��ѯ����-��������
		String issue_date = (String) condition.get("issue_date");// ��ѯ����-��������
		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select id, lawtype ��������,issue_date ��������,state ״̬  from law_law where 1=1 ");
		if (lawtype != null && !lawtype.equals("")) {
			lawtype = tbutil.getInCondition(lawtype);
			sql_sb.append(" and lawtype in (" + lawtype + ")");
		}
		if (state != null && !state.equals("")) {
			state = tbutil.getInCondition(state);
			sql_sb.append(" and state in(" + state + ")");
		}
		if (issuecorp != null && !issuecorp.equals("")) {
			sql_sb.append(getMultiOrCondition("issuecorp", issuecorp));
		}
		if (issue_date != null && !issue_date.equals("")) {
			issue_date = tbutil.convertComp_dateTimeFormat(issue_date);
			String[] date = issue_date.split(";");
			sql_sb.append(" and issue_date >= '" + date[0] + "' and issue_date <='" + date[1] + "'");
		}
		sql_sb.append(" order by issue_date");
		HashVO[] hashvo = dmo.getHashVoArrayByDS(null, sql_sb.toString());
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(hashvo, "��������", "��������", "��");

		return hashvo;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "��������", "��������", "״̬" };
	}

	//	@Override
	//	/**
	//	 * ��ȡ��ϸʱ��ģ�����!!
	//	 */
	//	public String getDrillTempletCode() throws Exception {
	//		return "LAW_LAW_CODE3"; //
	//	}

	@Override
	public String getDrillActionClassPath() throws Exception {
		// TODO Auto-generated method stub
		return "com.pushworld.ipushgrc.ui.law.p070.LawStatisWKPanel";
	}

	public String[] getSumFiledNames() {
		return new String[] { "����" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType(1);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType(2);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType(int _type) {
		ArrayList al = new ArrayList(); //
		if (_type == 1) {//��ά��ͼ���û����������ȥ�������/2014-11-05��
			al.add(getBeforehandGroupType("��������_��������", "����", BeforeHandGroupTypeVO.COUNT));
		}
		al.add(getBeforehandGroupType("��������_״̬", "����", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("��������_״̬", "����", BeforeHandGroupTypeVO.COUNT));
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

	public String getMultiOrCondition(String key, String _condition) {
		StringBuffer sb_sql = new StringBuffer();
		String[] tempid = tbutil.split(_condition, ";"); // str_realvalue.split(";");
		if (tempid != null && tempid.length > 0) {
			sb_sql.append(" and (");
			for (int j = 0; j < tempid.length; j++) {
				sb_sql.append(key + " like '%;" + tempid[j] + ";%'"); // 
				if (j != tempid.length - 1) { //
					sb_sql.append(" or ");
				}
			}
			sb_sql.append(") "); //
		}
		return sb_sql.toString();
	}

	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			CommDMO dmo = new CommDMO();
			String[] statesore = dmo.getStringArrayFirstColByDS(null, "select id from pub_comboboxdict where type='����״̬' order by seq");
			String[] typeorder = dmo.getStringArrayFirstColByDS(null, "select id  from pub_comboboxdict where type='��������' order by code");
			map.put("״̬", statesore);
			map.put("��������", typeorder);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
