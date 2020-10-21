package com.pushworld.ipushgrc.bs.cmpscore;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

public class Y_ScoreLostReportBuilder extends MultiLevelReportDataBuilderAdapter {

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {
		
		StringBuffer sb = new StringBuffer(" where 1=1 ");
		if(_condition.get("scoredate")!=null && !_condition.get("scoredate").equals(""))  //扣分日期
			sb.append(" and a.scoredate = '" + _condition.get("scoredate") + "'") ;   
		if(_condition.get("deptid")!=null && !_condition.get("deptid").equals(""))          //机构
			sb.append(" and a.deptid = '" + _condition.get("deptid") + "'") ;
		String sql = 
			"select a.scoredate 日期, p.name 人员, a.scorelost 应扣分,a.resultscore 裁定扣分, a.discovery 发现渠道,b.id deptid,b.name 机构 ,c.name 状态" +
			" from cmp_score_record a " +
			" left join pub_corp_dept b on b.id = a.deptid ,pub_user p ,PUB_COMBOBOXDICT c "
			+sb.toString()+" and p.id = a.userid and c.id = a.sendstate and c.type = '违规积分状态' order by linkcode ,scoredate desc";		
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
		return new String[]{"裁定扣分"};
	}
	
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid(){
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_发现渠道");
		vo.setRowHeaderGroupFields(new String[] { "日期","发现渠道"});
		vo.setColHeaderGroupFields(new String[] { "机构"});
		vo.setComputeGroupFields(new String[][] { { "裁定扣分", BeforeHandGroupTypeVO.SUM} });
		al.add(vo);
		
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_人员");
		vo.setRowHeaderGroupFields(new String[] { "日期"});
		vo.setColHeaderGroupFields(new String[] { "机构","人员"});
		vo.setComputeGroupFields(new String[][] { { "裁定扣分", BeforeHandGroupTypeVO.SUM} });
		al.add(vo);
		
		
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_部门_发现渠道");
		vo.setRowHeaderGroupFields(new String[] { "日期","发现渠道"});
		vo.setColHeaderGroupFields(new String[] { "机构"});
		vo.setComputeGroupFields(new String[][] { { "裁定扣分", BeforeHandGroupTypeVO.SUM} });
		al.add(vo);
		
		vo = new BeforeHandGroupTypeVO();
		vo.setName("日期_机构_状态");
		vo.setRowHeaderGroupFields(new String[] { "日期","状态"});
		vo.setColHeaderGroupFields(new String[] { "机构"});
		vo.setComputeGroupFields(new String[][] { { "裁定扣分", BeforeHandGroupTypeVO.SUM} });
		al.add(vo);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

}
