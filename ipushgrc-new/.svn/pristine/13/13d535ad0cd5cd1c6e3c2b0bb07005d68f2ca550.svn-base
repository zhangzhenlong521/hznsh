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

public class CmpScoreReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	@Override
	public HashVO[] buildReportData(HashMap condition) throws Exception {
		
		String where = "where 1=1";
		if (condition.get("obj_createdate") != null) {
			RefItemVO refVO = (RefItemVO) condition.get("obj_createdate");
			String tmp = refVO.getHashVO().getStringValue("querycondition");
			tmp = new TBUtil().replaceAll(tmp,"{itemkey}","scoredate");
			where += " and " + tmp;
		}
		
		String sql = 
			"select a.scoredate 日期, a.username 人员, a.scorelost 扣分,a.findchannel 发现渠道,b.id deptid,b.name 部门 from cmp_score_lost a " +
			" left join pub_corp_dept b on b.id = a.deptid "+where+" order by linkcode ,scoredate desc";		
		CommDMO comm = new CommDMO();
		HashVO vos[] = comm.getHashVoArrayByDS(null,sql);
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "日期", "日期", "季");
		ReportDMO dmo = new ReportDMO();
		dmo.leftOuterJoin_TreeTableFieldName(vos, "机构", "pub_corp_dept", "name", "id", "deptid", "id", "parentid", 2);
		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[]{"日期", "人员","发现渠道", "机构", "部门"};
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[]{"扣分"};
	}
	
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid(){
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_发现渠道");
		vo.setRowHeaderGroupFields(new String[] { "日期","发现渠道"});
		vo.setColHeaderGroupFields(new String[] { "机构"});
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM} });
		al.add(vo);
		
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_人员");
		vo.setRowHeaderGroupFields(new String[] { "日期"});
		vo.setColHeaderGroupFields(new String[] { "机构","人员"});
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM} });
		al.add(vo);
		
		
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_部门_发现渠道");
		vo.setRowHeaderGroupFields(new String[] { "日期","发现渠道"});
		vo.setColHeaderGroupFields(new String[] { "机构", "部门"});
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM} });
		al.add(vo);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

}
