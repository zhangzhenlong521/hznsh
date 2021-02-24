package com.pushworld.ipushgrc.ui.HR.p060;

import java.util.ArrayList;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

/**
 * 
 * @author longlonggo521
 * 员工基本情况统计
 */
public class StaffmemberCountWorkPanel extends MultiLevelReportDataBuilderAdapter {
	TBUtil tbutil = new TBUtil();
	CommDMO dmo = new CommDMO();

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		String age = (String) condition.get("age");//年龄
		String politicalstatus = (String) condition.get("politicalstatus"); //政治面貌
		String degree = (String) condition.get("degree");//学历
		String stationkind = (String) condition.get("stationkind");//岗位归类
		String posttitle = (String) condition.get("posttitle");//职称
		String workage = (String) condition.get("workage");//工龄

		StringBuffer sql_sb = new StringBuffer();
		sql_sb.append("select id id, age 年龄,politicalstatus 政治面貌,degree 学历,stationkind 岗位归类, posttitle 职称 ,workage 工龄  from v_sal_personinfo where 1=1");

		//主管部门
		if (age != null && !age.equals("")) {
			String [] str=getSplit(age);
			sql_sb.append(" and (age>='"+str[0]+"' and age<='"+str[1]+"')");
		}

		//制度分类查询条件
		if (politicalstatus != null && !politicalstatus.equals("")) {
			sql_sb.append(" and politicalstatus in ('" + politicalstatus+ "')");
		}

		//业务类型查询条件,树型多选!!
		if (degree != null && !degree.equals("")) { //业务类型
			sql_sb.append(" and degree in ('" +degree + "')");
		}

		//内控类型查询条件!!树型多选!!
		if (stationkind != null && !stationkind.equals("")) { //内控类型!
			sql_sb.append(" and stationkind in ('" + stationkind+ "')");
		}

		if (posttitle != null && !posttitle.equals("")) {//产品分类
			sql_sb.append(" and posttitle in ('" + posttitle + "')");
		}
		if (workage != null && !workage.equals("")) {
			String [] str=getSplit(workage);
			sql_sb.append("and (workage>='"+str[0]+"' and workage<='"+str[1]+"')");
		}
		sql_sb.append(" order by linkcode"); //如果不按时间排序，统计出来的季度是乱的
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sql_sb.toString());

		ReportDMO reportDMO = new ReportDMO(); 
//		//处理主管部门
//		reportDMO.addOneFieldFromOtherTree(vos, "政治面貌", "政治面貌", "select politicalstatus,politicalstatus from v_sal_personinfo", 2, false, 2); //加上第1层
//
//		//处理业务类型第1层与第2层!!
//		reportDMO.addOneFieldFromOtherTree(vos, "学历", "学历", "select degree,degree from v_sal_personinfo", 1, false, 1); //加上第1层
//
//		//处理内控分类之第一层与第二层
//		reportDMO.addOneFieldFromOtherTree(vos, "岗位归类", "岗位归类", "select stationkind from v_sal_personinfo", 1, false, 1); //加上第1层
//		//产品分类之第一层与第二层
//		reportDMO.addOneFieldFromOtherTree(vos, "职称", "职称", "select posttitle from v_sal_personinfo", 1, false, 1); //加上第1层
//		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "发布日期", "发布日期", "季"); //将发而日期折算成季度!
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] {"年龄","政治面貌","学历","岗位归类","职称","工龄"};
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
		return "SAL_PERSONINFO_ZZL_CODE4"; //
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType();
	}

	/**
	 * 雷达图
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Splider() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("政治面貌_岗位归类", "数量", BeforeHandGroupTypeVO.COUNT));
		al.add(getBeforehandGroupType("学历_政治面貌", "数量", BeforeHandGroupTypeVO.COUNT));
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	/**
	 * 表格与图表!!!
	 * @return
	 */
	public BeforeHandGroupTypeVO[] getBeforehandGroupType() {
		ArrayList al = new ArrayList(); //
		al.add(getBeforehandGroupType("政治面貌_学历", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("岗位归类_学历", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("职称_学历", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("政治面貌_岗位归类", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("政治面貌_职称", "数量", BeforeHandGroupTypeVO.COUNT)); //
		al.add(getBeforehandGroupType("岗位归类_职称", "数量", BeforeHandGroupTypeVO.COUNT)); //
//
//		al.add(getBeforehandGroupType("发布日期_业务类型(第1层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
//		al.add(getBeforehandGroupType("发布日期_业务类型(第2层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
//
//		al.add(getBeforehandGroupType("发布日期_内控分类(第1层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
//		al.add(getBeforehandGroupType("发布日期_内控分类(第2层)", "数量", BeforeHandGroupTypeVO.COUNT)); //
//
//		al.add(getBeforehandGroupType("发布日期_制度分类", "数量", BeforeHandGroupTypeVO.COUNT));
//		al.add(getBeforehandGroupType("发布日期_状态", "数量", BeforeHandGroupTypeVO.COUNT));
//		al.add(getBeforehandGroupType("产品分类_状态", "数量", BeforeHandGroupTypeVO.COUNT));
//		al.add(getBeforehandGroupType("业务类型(第2层)_状态", "数量", BeforeHandGroupTypeVO.COUNT));
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
			map.put("政治面貌", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='薪酬_政治面貌' order by seq"));
			map.put("学历", dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='薪酬_学历' order by seq"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public String [] getSplit(String str){
		String [] split=str.split(";");
		return split;
		
	}

}

