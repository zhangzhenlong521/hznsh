package com.pushworld.ipushgrc.bs.risk.p040;

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

public class RiskReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	CommDMO dmo = new CommDMO();

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		//现有查询条件
		String filestate = (String) condition.get("filestate");//文件状态,多选参照,值为“3;5;”		
		String blcorpid = (String) condition.get("blcorpid");//所属机构
		RefItemVO ref_identify_date = (RefItemVO) condition.get("obj_identify_date");//识别日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey}
		RefItemVO ref_evaluate_date = (RefItemVO) condition.get("obj_evaluate_date");//评估日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey}

		//可以直接在模板查询面板中添加
		String rank = (String) condition.get("risk_rank");//风险等级
		String risktype = (String) condition.get("risk_risktype");//风险分类
		String bsactid = (String) condition.get("bsactid");//业务活动
		String riskreftype = (String) condition.get("riskreftype");//风险关联类型
		String findchannel = (String) condition.get("findchannel");//发现渠道		

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select id, risk_risktype 风险分类,risk_rank 风险等级,filestatename 文件状态, blcorpid 所属机构,bsactid 业务活动 from v_risk_process_file where riskreftype='环节' ");

		if (filestate != null && !"".equals(filestate)) {
			sb_sql.append(" and filestate in(" + tbutil.getInCondition(filestate) + ") ");
		}
		if (blcorpid != null && !"".equals(blcorpid)) {
			sb_sql.append(" and blcorpid in(" + tbutil.getInCondition(blcorpid) + ") ");
		}
		//识别日期!!!
		if (ref_identify_date != null) {
			String str_cons = ref_identify_date.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "identify_date"); //替换其中的特殊符号!为实际字段名!
			sb_sql.append(" and " + str_cons);
		}
		//评估日期!!!
		if (ref_evaluate_date != null) {
			String str_cons = ref_evaluate_date.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "evaluate_date"); //替换其中的特殊符号!为实际字段名!
			sb_sql.append(" and " + str_cons);
		}

		//查询过滤逻辑已实现，可以直接在模板查询面板中添加
		if (rank != null && !"".equals(rank)) {
			sb_sql.append(" and risk_rank  in(" + tbutil.getInCondition(rank) + ") ");//将风险等级下拉框设置为多选【李春娟/2012-03-15】
		}
		if (risktype != null && !"".equals(risktype)) {
			sb_sql.append(" and risk_risktype in(" + tbutil.getInCondition(risktype) + ") ");
		}
		if (bsactid != null && !"".equals(bsactid)) {
			sb_sql.append(" and bsactid in(" + tbutil.getInCondition(bsactid) + ") ");
		}
		if (riskreftype != null && !"".equals(riskreftype)) {
			sb_sql.append(" and riskreftype in(" + tbutil.getInCondition(riskreftype) + ") ");
		}
		if (findchannel != null && !"".equals(findchannel)) {
			sb_sql.append(" and findchannel in(" + tbutil.getInCondition(findchannel) + ") ");
		}
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());

		//处理树型结构显示不同层级【李春娟/2012-03-13】
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "业务活动(第1层)", "业务活动", "select id,name,parentid from bsd_bsact", 1, true, 1); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "业务活动(第2层)", "业务活动", "select id,name,parentid from bsd_bsact", 2, true, 1); //加上第2层

		reportDMO.addOneFieldFromOtherTree(vos, "所属机构(第1层)", "所属机构", "select id,name,parentid from pub_corp_dept", 2, true, 2); //从第2层开始 加上第2层
		reportDMO.addOneFieldFromOtherTree(vos, "所属机构(第2层)", "所属机构", "select id,name,parentid from pub_corp_dept", 3, true, 2); //从第2层开始 加上第3层

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "发布日期", "发布日期", "季");//处理时间显示格式

		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "风险分类", "风险等级", "文件状态", "所属机构(第1层)", "所属机构(第2层)", "业务活动(第1层)", "业务活动(第2层)" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	@Override
	/**
	 * 钻取明细时的模板编码!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "V_RISK_PROCESS_FILE_CODE1"; //
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
			al.add(getBeforehandGroupType("风险等级_风险分类", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("风险分类_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("风险分类_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("风险等级_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("风险等级_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件状态_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件状态_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第1层)_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第2层)_所属机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
		} else {
			al.add(getBeforehandGroupType("风险等级_风险分类", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("风险等级_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第1层)_风险等级", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("文件状态_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("业务活动(第1层)_所属机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
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

	@Override
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			map.put("风险等级", dmo.getStringArrayFirstColByDS(null, "select id from PUB_COMBOBOXDICT where type = '风险等级' order by seq"));
			map.put("文件状态", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='文件状态' order by seq"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
