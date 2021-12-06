package com.pushworld.ipushgrc.bs.cmpscore;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

public class CmpScorePunishReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	CommDMO comm = new CommDMO();
	@Override
	public HashVO[] buildReportData(HashMap condition) throws Exception {
		
		String where = "where 1=1";
		if (condition.get("obj_createdate") != null) {
			RefItemVO refVO = (RefItemVO) condition.get("obj_createdate");
			String tmp = refVO.getHashVO().getStringValue("querycondition");
			tmp = new TBUtil().replaceAll(tmp,"{itemkey}","a.createdate");
			where += " and " + tmp;
		}
		
		String sql = 
			"select a.createdate ����, a.username ��Ա, a.punish �ͷ�����, b.id deptid,b.name ���� from cmp_score_punish a left join pub_corp_dept b on a.deptid = b.id " +
			" " + where;		
		HashVO vos[] = comm.getHashVoArrayByDS(null,sql);
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "����", "����", "��");
		
		ReportDMO dmo = new ReportDMO();
		dmo.leftOuterJoin_TreeTableFieldName(vos, "����", "pub_corp_dept", "name", "id", "deptid", "id", "parentid", 2);
		
		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[]{"����", "��Ա", "����", "����",  "�ͷ�����"};
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[]{"����"};
	}
	
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid(){
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("����_����");
		vo.setRowHeaderGroupFields(new String[] { "����"});
		vo.setColHeaderGroupFields(new String[] { "����"});
		vo.setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT} });
		al.add(vo);	
		vo = new BeforeHandGroupTypeVO();
		vo.setName("����_����_����");
		vo.setRowHeaderGroupFields(new String[] { "����"});
		vo.setColHeaderGroupFields(new String[] { "����", "����"});
		vo.setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT} });
		al.add(vo);
		vo = new BeforeHandGroupTypeVO();
		vo.setName("�ͷ�����_����");
		vo.setRowHeaderGroupFields(new String[] { "�ͷ�����"});
		vo.setColHeaderGroupFields(new String[] { "����"});
		vo.setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);
		vo = new BeforeHandGroupTypeVO();
		vo.setName("����_�ͷ�����_����");
		vo.setRowHeaderGroupFields(new String[] { "�ͷ�����","����"});
		vo.setColHeaderGroupFields(new String[] { "����"});
		vo.setComputeGroupFields(new String[][] { { "����", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			map.put("�ͷ�����",comm.getStringArrayFirstColByDS(null, "select punish from cmp_score_rule  order by score"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
