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
 * 成功防范统计，数据生成器
 * 
 * @author hm
 * 
 */
public class WardReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		TBUtil tbutil = new TBUtil();

		RefItemVO ref_finddate = (RefItemVO) condition.get("obj_finddate"); // 发现日期

		String reportcorp = (String) condition.get("reportcorp");//报送机构
		RefItemVO reportdate = (RefItemVO) condition.get("obj_reportdate"); //报送日期
		String findchannel = (String) condition.get("findchannel");//发现渠道

		StringBuffer sql = new StringBuffer();

		sql.append("select w.id id,w.findchannel 发现渠道, cd.id 报告机构,w.bsactname 业务类型 , substr(w.reportdate, 1, 7) 报告日期 " + "from cmp_ward w " + "left join pub_corp_dept cd on w.reportcorp = cd.id where 1=1");

		//发现日期
		if (ref_finddate != null) {
			String str_cons = ref_finddate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "w.finddate"); //替换其中的特殊符号!为实际字段名!
			sql.append(" and " + str_cons);
		}

		if (reportcorp != null && !"".equals(reportcorp)) {
			String cordIds = tbutil.getInCondition(reportcorp);
			sql.append(" and w.reportcorp in (" + cordIds + ")");
		}
		//加上[报送日期]和[发现渠道]SQL条件 [YangQing/2013-08-27]
		if (reportdate != null && !"".equals(reportdate)) {
			String str_cons = reportdate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "w.reportdate"); //替换其中的特殊符号!为实际字段名!
			sql.append(" and " + str_cons);
		}
		//发现渠道
		if (!TBUtil.isEmpty(findchannel)) {
			sql.append(" and w.findchannel = '" + findchannel + "'");
		}
		CommDMO comm = new CommDMO();
		HashVO vos[] = comm.getHashVoArrayByDS(null, sql.toString());

		//报告机构【岳耀彪/2012-03-14】
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "报告机构(第1层)", "报告机构", "select id,name,parentid from pub_corp_dept", 2, true, 2); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "报告机构(第2层)", "报告机构", "select id,name,parentid from pub_corp_dept", 3, true, 2); //加上第2层

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "报告日期", "报告日期", "季"); //将报告日期折算成季度!

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
		return "CMP_WARD_CODE1"; //
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
}
