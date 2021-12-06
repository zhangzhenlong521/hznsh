package com.pushworld.ipushgrc.bs.rule.p110;

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
 * 制度统计!!
 * @author xch
 *
 */
public class RuleReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {
	TBUtil tbutil = new TBUtil();
	CommDMO dmo = new CommDMO();

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String str_blcorp = (String) condition.get("blcorp");//主管部门
		String str_ruletype = (String) condition.get("ruletype"); //制度分类
		String str_busitype = (String) condition.get("busitype");//业务类型
		String str_ictype = (String) condition.get("ictype");//内控分类
		String str_producttype = (String) condition.get("producttype");//内控分类

		RefItemVO ref_publishdate = (RefItemVO) condition.get("obj_publishdate");//发布日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey},许多人不知道这个!
		String state = (String) condition.get("state"); //状态

		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select id id, blcorp 主管部门,ruletype 制度分类,publishdate 发布日期,busitype 业务类型, ictype 内控分类 ,producttype 产品分类 ,state 状态 from rule_rule where 1=1");

		//主管部门
		if (str_blcorp != null && !str_blcorp.equals("")) {
			sql_sb.append(" and blcorp in (" + tbutil.getInCondition(str_blcorp) + ")");
		}

		//制度分类查询条件
		if (str_ruletype != null && !str_ruletype.equals("")) {
			sql_sb.append(" and ruletype in (" + tbutil.getInCondition(str_ruletype) + ")");
		}

		//业务类型查询条件,树型多选!!
		if (str_busitype != null && !str_busitype.equals("")) { //业务类型
			sql_sb.append(" and busitype in (" + tbutil.getInCondition(str_busitype) + ")");
		}

		//内控类型查询条件!!树型多选!!
		if (str_ictype != null && !str_ictype.equals("")) { //内控类型!
			sql_sb.append(" and ictype in (" + tbutil.getInCondition(str_ictype) + ")");
		}

		if (str_producttype != null && !str_producttype.equals("")) {//产品分类
			sql_sb.append(" and str_producttype in (" + tbutil.getInCondition(str_producttype) + ")");
		}
		//发布日期!!!
		if (ref_publishdate != null) {
			String str_cons = ref_publishdate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "publishdate"); //替换其中的特殊符号!为实际字段名!
			sql_sb.append(" and " + str_cons);
		}

		if (state != null && !state.equals("")) {
			sql_sb.append(" and state in (" + tbutil.getInCondition(state) + ")");
		}
		sql_sb.append(" order by publishdate"); //如果不按时间排序，统计出来的季度是乱的
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sql_sb.toString());

		ReportDMO reportDMO = new ReportDMO(); //
		//处理主管部门
		reportDMO.addOneFieldFromOtherTree(vos, "主管部门(第1层)", "主管部门", "select id,name,parentid from pub_corp_dept", 2, true, 2); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "主管部门(第2层)", "主管部门", "select id,name,parentid from pub_corp_dept", 3, true, 2); //加上第2层

		//处理业务类型第1层与第2层!!
		reportDMO.addOneFieldFromOtherTree(vos, "业务类型(第1层)", "业务类型", "select id,name,parentid from bsd_bsact", 1, true, 1); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "业务类型(第2层)", "业务类型", "select id,name,parentid from bsd_bsact", 2, true, 1); //加上第2层

		//处理内控分类之第一层与第二层
		reportDMO.addOneFieldFromOtherTree(vos, "内控分类(第1层)", "内控分类", "select id,name,parentid from bsd_icsys", 1, true, 1); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "内控分类(第2层)", "内控分类", "select id,name,parentid from bsd_icsys", 2, true, 1); //加上第2层
		//产品分类之第一层与第二层
		reportDMO.addOneFieldFromOtherTree(vos, "产品分类(第1层)", "产品分类", "select id,name,parentid from bsd_product", 1, true, 1); //加上第1层
		reportDMO.addOneFieldFromOtherTree(vos, "产品分类(第2层)", "产品分类", "select id,name,parentid from bsd_product", 2, true, 1); //加上第2层
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "发布日期", "发布日期", "季"); //将发而日期折算成季度!
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "制度分类", "发布日期", "主管部门(第1层)", "主管部门(第2层)", "业务类型(第1层)", "业务类型(第2层)", "内控分类(第1层)", "内控分类(第2层)", "产品分类(第1层)", "产品分类(第2层)", "状态" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		return getBeforehandGroupType();
	}

	@Override
	/**
	 * 钻取明细时的模板编码!!
	 */
	public String getDrillTempletCode() throws Exception {
		return "RULE_RULE_CODE6"; //
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType();
	}

	/**
	 * 雷达图
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("制度分类_业务类型(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("发布日期_业务类型(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	/**
	 * 表格与图表!!!
	 * @return
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("主管部门(第1层)_业务类型(第1层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("主管部门(第2层)_业务类型(第2层)", "数量", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("制度分类_业务类型(第1层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("制度分类_业务类型(第2层)", "数量", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("发布日期_业务类型(第1层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("发布日期_业务类型(第2层)", "数量", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("发布日期_内控分类(第1层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("发布日期_内控分类(第2层)", "数量", BeforeHandGroupTypeVO.COUNT)); //

		al.add(getBeforehandGroupType("发布日期_制度分类", "数量", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("发布日期_状态", "数量", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("产品分类_状态", "数量", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("业务类型(第2层)_状态", "数量", BeforeHandGroupTypeVO.COUNT));
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
			map.put("状态", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='法规状态' order by seq"));
			map.put("制度分类", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='制度类型' order by seq"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

}
