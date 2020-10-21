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
			"select a.createdate 日期, a.username 人员, a.punish 惩罚类型, b.id deptid,b.name 部门 from cmp_score_punish a left join pub_corp_dept b on a.deptid = b.id " +
			" " + where;		
		HashVO vos[] = comm.getHashVoArrayByDS(null,sql);
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "日期", "日期", "季");
		
		ReportDMO dmo = new ReportDMO();
		dmo.leftOuterJoin_TreeTableFieldName(vos, "机构", "pub_corp_dept", "name", "id", "deptid", "id", "parentid", 2);
		
		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[]{"日期", "人员", "机构", "部门",  "惩罚类型"};
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[]{"数量"};
	}
	
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid(){
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构");
		vo.setRowHeaderGroupFields(new String[] { "日期"});
		vo.setColHeaderGroupFields(new String[] { "机构"});
		vo.setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT} });
		al.add(vo);	
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_部门");
		vo.setRowHeaderGroupFields(new String[] { "日期"});
		vo.setColHeaderGroupFields(new String[] { "机构", "部门"});
		vo.setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT} });
		al.add(vo);
		vo = new BeforeHandGroupTypeVO();
		vo.setName("惩罚类型_机构");
		vo.setRowHeaderGroupFields(new String[] { "惩罚类型"});
		vo.setColHeaderGroupFields(new String[] { "机构"});
		vo.setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);
		vo = new BeforeHandGroupTypeVO();
		vo.setName("机构_惩罚类型_日期");
		vo.setRowHeaderGroupFields(new String[] { "惩罚类型","日期"});
		vo.setColHeaderGroupFields(new String[] { "机构"});
		vo.setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
		al.add(vo);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			map.put("惩罚类型",comm.getStringArrayFirstColByDS(null, "select punish from cmp_score_rule  order by score"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
