package com.pushworld.ipushgrc.bs.cmpscore;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

public class Y_ScorePunishReportBuilder extends MultiLevelReportDataBuilderAdapter {

	@Override
	public HashVO[] buildReportData(HashMap _condition) throws Exception {

		StringBuffer sql = new StringBuffer("select  d.name 违规人员机构 , real_score 实际扣分 , s.scoredate 扣分日期 ,c.name 处理情况 , p.PUNISH_DATE 处罚日期 " + "from cmp_punish_record p , cmp_score_record s ,Pub_ComboBoxDict c ,pub_corp_dept d "
				+ "where s.id = p.record_id and c.id = p.deal_type and d.id = s.deptid and c.type = '违规积分处理情况' ");

		String dept = (String) _condition.get("USER_DEPT");
		RefItemVO punish_date = (RefItemVO) _condition.get("obj_PUNISH_DATE");
		RefItemVO score_date = (RefItemVO) _condition.get("obj_SCORE_DATE");
		TBUtil tbutil = new TBUtil();
		if (dept != null && !dept.equals("")) {
			sql.append(" and  s.deptid in (" + tbutil.getInCondition(dept) + ") ");
		}
		if (punish_date != null) {
			String str_cons = punish_date.getHashVO().getStringValue("querycondition"); // 取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "p.PUNISH_DATE"); // 替换其中的特殊符号!为实际字段名!
			sql.append(" and " + str_cons);
		}

		if (score_date != null) {
			String str_cons = score_date.getHashVO().getStringValue("querycondition"); // 取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "s.scoredate"); // 替换其中的特殊符号!为实际字段名!
			sql.append(" and " + str_cons);
		}
		CommDMO comm = new CommDMO();
		HashVO vos[] = comm.getHashVoArrayByDS(null, sql.toString());
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "扣分日期", "扣分日期", "季");
		util.leftOuterJoin_YSMDFromDateTime(vos, "处罚日期", "处罚日期", "季");
		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "违规人员机构", "扣分日期", "处理情况", "处罚日期" };
	}

	@Override
	public String[] getSumFiledNames() {
		return new String[] { "实际扣分" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("违规人员机构_扣分日期");
		vo.setRowHeaderGroupFields(new String[] { "违规人员机构" });
		vo.setColHeaderGroupFields(new String[] { "扣分日期" });
		vo.setComputeGroupFields(new String[][] { { "实际扣分", BeforeHandGroupTypeVO.SUM } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("违规人员机构_处罚日期");
		vo.setRowHeaderGroupFields(new String[] { "违规人员机构" });
		vo.setColHeaderGroupFields(new String[] { "处罚日期" });
		vo.setComputeGroupFields(new String[][] { { "实际扣分", BeforeHandGroupTypeVO.SUM } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("违规人员机构_扣分日期_处理情况");
		vo.setRowHeaderGroupFields(new String[] { "违规人员机构", "处理情况" });
		vo.setColHeaderGroupFields(new String[] { "扣分日期" });
		vo.setComputeGroupFields(new String[][] { { "实际扣分", BeforeHandGroupTypeVO.SUM } });
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("违规人员机构_处罚日期_处理情况");
		vo.setRowHeaderGroupFields(new String[] { "违规人员机构", "处理情况" });
		vo.setColHeaderGroupFields(new String[] { "处罚日期" });
		vo.setComputeGroupFields(new String[][] { { "实际扣分", BeforeHandGroupTypeVO.SUM } });
		al.add(vo);

		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

}
