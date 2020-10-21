package com.pushworld.ipushgrc.bs.cmpevent;

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

/**
 * 违规事件统计,数据生成器
 * 
 * @author hm
 * 
 */
public class EventReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	private TBUtil tbutil = new TBUtil();

	@Override
	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String eventname = (String) condition.get("eventname"); // 事件名称
		String findchannel = (String) condition.get("findchannel"); // 发现渠道
		RefItemVO ref_reportdate = (RefItemVO) condition.get("obj_reportdate"); // 报告日期
		RefItemVO ref_happendate = (RefItemVO) condition.get("obj_happendate");// 发生日期
		String eventcorpid = (String) condition.get("eventcorpid");// 发生机构
		String reportcorp = (String) condition.get("reportcorp"); // 报告机构
		StringBuffer sql = new StringBuffer();
		sql.append("select e.id id, e.findchannel 发现渠道, cd.id 报告机构,e.bsactname 业务类型, substr(e.reportdate, 1, 7) 报告日期 " + "from cmp_event e " + "left join pub_corp_dept cd on e.reportcorp = cd.id where 1=1");
		if (eventname != null && !eventname.equals("")) {
			sql.append(" and e.eventname like '%" + eventname + "%'");
		}
		if (findchannel != null && !findchannel.equals("")) {
			sql.append(" and e.findchannel in (" + tbutil.getInCondition(findchannel) + ")");
		}

		// 报告日期!!!
		if (ref_reportdate != null) {
			String str_cons = ref_reportdate.getHashVO().getStringValue("querycondition"); // 取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "reportdate"); // 替换其中的特殊符号!为实际字段名!
			sql.append(" and " + str_cons);
		}

		// 发生日期
		if (ref_happendate != null) {
			String str_cons = ref_happendate.getHashVO().getStringValue("querycondition"); // 取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "happendate"); // 替换其中的特殊符号!为实际字段名!
			sql.append(" and " + str_cons);
		}

		if (reportcorp != null && !reportcorp.equals("")) { // 报告机构
			String cordIds = tbutil.getInCondition(reportcorp);
			sql.append(" and e.reportcorp in (" + cordIds + ")");
		}
		String likeContion = getMultiOrCondition("e.eventcorpid", eventcorpid);
		sql.append(" " + likeContion);
		CommDMO comm = new CommDMO();
		HashVO vos[] = comm.getHashVoArrayByDS(null, sql.toString());

		ReportDMO reportDMO = new ReportDMO(); //
		// 处理主管部门
		reportDMO.addOneFieldFromOtherTree(vos, "报告机构(第1层)", "报告机构", "select id,name,parentid from pub_corp_dept", 2, true, 2); // 加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "报告机构(第2层)", "报告机构", "select id,name,parentid from pub_corp_dept", 3, true, 2); // 加上第2层

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "报告日期", "报告日期", "季"); // 将发而日期折算成季度!

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "发生日期", "发生日期", "季"); // 将发而日期折算成季度!
		return vos;
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "发现渠道", "报告机构(第1层)", "报告机构(第2层)", "报告日期", "业务类型" };
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
		return "CMP_EVENT_CODE2"; //
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
			al.add(getBeforehandGroupType("报告机构(第1层)_发现渠道", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第2层)_发现渠道", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第1层)_报告日期", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第2层)_报告日期", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第1层)_业务类型", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第2层)_业务类型", "数量", BeforeHandGroupTypeVO.COUNT));

			BeforeHandGroupTypeVO vo = new BeforeHandGroupTypeVO();
			vo.setName("报告机构(第1层)_发现渠道_报告日期");
			vo.setRowHeaderGroupFields(new String[] { "报告机构(第1层)" });
			vo.setColHeaderGroupFields(new String[] { "发现渠道", "报告日期" });
			vo.setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
			al.add(vo);

			vo = new BeforeHandGroupTypeVO();
			vo.setName("报告机构(第2层)_发现渠道_报告日期");
			vo.setRowHeaderGroupFields(new String[] { "报告机构(第2层)" });
			vo.setColHeaderGroupFields(new String[] { "发现渠道", "报告日期" });
			vo.setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT } });
			al.add(vo);
		} else {
			al.add(getBeforehandGroupType("报告机构(第1层)_发现渠道", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第2层)_发现渠道", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第1层)_报告日期", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("报告机构(第2层)_报告日期", "数量", BeforeHandGroupTypeVO.COUNT));

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
}
