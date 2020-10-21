package com.pushworld.ipushgrc.bs.score.p080;

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
 * 减免积分统计逻辑类【李春娟/2013-05-16】
 * @com.pushworld.ipushgrc.ui.score.p080.ScoreStatisWKPanel
 * @author lcj
 *
 */
public class ScoreReduceReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		CommDMO dmo = new CommDMO();
		//现有查询条件
		String dealtype = (String) condition.get("DEALTYPE");//处理结果

		String corpid = (String) condition.get("CORPID");//减免机构
		RefItemVO ref_examinedate = (RefItemVO) condition.get("obj_EXAMINEDATE");//减免日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey}

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select id,realscore 减免积分, corpid 减免机构,dealtype 处理结果,examinedate 减免日期  from score_reduce where STATE='已审核' ");//这里查询已生效的记录

		if (dealtype != null && !"".equals(dealtype)) {
			sb_sql.append(" and dealtype in(" + tbutil.getInCondition(dealtype) + ")");
		}

		if (corpid != null && !"".equals(corpid)) {
			sb_sql.append(" and corpid in(" + tbutil.getInCondition(corpid) + ") ");
		}

		if (ref_examinedate != null) {//减免日期
			String str_cons = ref_examinedate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "examinedate"); //替换其中的特殊符号!为实际字段名!
			sb_sql.append(" and " + str_cons);
		}

		sb_sql.append(" order by examinedate desc");

		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());

		//处理树型结构显示不同层级【李春娟/2013-05-16】
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "减免机构(第1层)", "减免机构", "select id,name,parentid from pub_corp_dept", 2, true, 2); //从第2层开始 加上第2层
		reportDMO.addOneFieldFromOtherTree(vos, "减免机构(第2层)", "减免机构", "select id,name,parentid from pub_corp_dept", 3, true, 2); //从第2层开始 加上第3层
		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "减免日期", "减免日期", "月");//处理时间显示格式
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "减免积分", "减免机构(第1层)", "减免机构(第2层)", "处理结果", "减免日期" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量", "减免积分" };
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
			al.add(getBeforehandGroupType2("处理结果_减免机构(第1层)"));
			al.add(getBeforehandGroupType2("处理结果_减免机构(第2层)"));
			al.add(getBeforehandGroupType2("减免日期_减免机构(第1层)"));
			al.add(getBeforehandGroupType2("减免日期_减免机构(第2层)"));

		} else {//图表的分类个数最好是四个以上十个以下的才好看【李春娟/2013-05-16】
			al.add(getBeforehandGroupType("处理结果_减免机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
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

	public BeforeHandGroupTypeVO getBeforehandGroupType2(String _name) {
		String[] names = _name.split("_");
		BeforeHandGroupTypeVO typeVo = new BeforeHandGroupTypeVO();
		typeVo.setName(_name);
		typeVo.setRowHeaderGroupFields(new String[] { names[0] });
		typeVo.setColHeaderGroupFields(new String[] { names[1] });
		typeVo.setComputeGroupFields(new String[][] { { "数量", BeforeHandGroupTypeVO.COUNT }, { "减免积分", BeforeHandGroupTypeVO.SUM } });
		return typeVo;
	}

	//	//增加排序字段【李春娟/2013-05-16】
	//	public HashMap getGroupFieldOrderConfig() {
	//		HashMap map = new HashMap();
	//		try {
	//			CommDMO dmo = new CommDMO();
	//			String[] riskrank = dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='减免积分_处理结果' order by seq");
	//			String[] findrank = dmo.getStringArrayFirstColByDS(null, "select name from pub_comboboxdict where type='减免积分_发现渠道' order by seq");
	//			map.put("处理结果", riskrank);
	//			map.put("发现渠道", findrank);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		return map;
	//	}

	public HashMap getDrilGroupBind() {
		HashMap map = new HashMap(); //
		map.put("减免机构(第1层)", "减免机构(第2层)"); //实现点击减免机构(第1层)即可弹出减免机构(第2层)的统计结果
		return map;
	}

	public String getDrillTempletCode() throws Exception {
		return "SCORE_REDUCE_LCJ_Q01";
	}

}
