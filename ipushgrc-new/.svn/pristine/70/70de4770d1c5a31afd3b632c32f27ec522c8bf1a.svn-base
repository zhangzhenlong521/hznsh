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
 * 违规积分统计逻辑类【李春娟/2013-05-16】
 * @com.pushworld.ipushgrc.ui.score.p080.ScoreStatisWKPanel
 * @author lcj
 *
 */
public class ScoreReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		CommDMO dmo = new CommDMO();
		//现有查询条件
		String scoretypeid = (String) condition.get("SCORETYPEID");//违规类型
		String riskrank = (String) condition.get("RISKRANK");//风险等级
		String findrank = (String) condition.get("FINDRANK");//发现渠道

		String deptid = (String) condition.get("DEPTID");//违规机构
		String state = (String) condition.get("state");//状态
		RefItemVO ref_effectdate = (RefItemVO) condition.get("obj_EFFECTDATE");//生效日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey}

		String punishtype = (String) condition.get("punishtype");//惩罚类型

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer("select id, scoretype 违规类型,riskrank 风险等级,findrank 发现渠道, deptid 违规机构,effectdate 生效日期,finalscore 违规积分,finalmoney 罚款,state 状态,punishtype 惩罚类型  from v_score_user where 1=1 ");//这里查询所有的记录

		if (scoretypeid != null && !"".equals(scoretypeid)) {
			sb_sql.append(" and scoretypeid in(" + tbutil.getInCondition(scoretypeid) + ") ");
		}
		if (riskrank != null && !"".equals(riskrank)) {
			sb_sql.append(" and riskrank in(" + tbutil.getInCondition(riskrank) + ")");
		}
		if (findrank != null && !"".equals(findrank)) {
			sb_sql.append(" and findrank in(" + tbutil.getInCondition(findrank) + ") ");
		}
		if (deptid != null && !"".equals(deptid)) {
			sb_sql.append(" and deptid in(" + tbutil.getInCondition(deptid) + ") ");
		}
		if (state != null && !"".equals(state)) {
			sb_sql.append(" and state in(" + tbutil.getInCondition(state) + ") ");
		}
		if (ref_effectdate != null) {//生效日期
			String str_cons = ref_effectdate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "effectdate"); //替换其中的特殊符号!为实际字段名!
			sb_sql.append(" and " + str_cons);
		}
		if (punishtype != null && !"".equals(punishtype)) {
			sb_sql.append(" and punishtype in(" + tbutil.getInCondition(punishtype) + ") ");
		}

		sb_sql.append(" order by effectdate desc");

		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());

		for (int i = 0; i < vos.length; i++) {
			String str_punishtype = vos[i].getStringValue("惩罚类型");
			if (str_punishtype == null || str_punishtype.equals("")) {
				vos[i].setAttributeValue("惩罚类型", "无惩罚");
			}
		}

		//处理树型结构显示不同层级【李春娟/2013-05-16】
		ReportDMO reportDMO = new ReportDMO(); //
		reportDMO.addOneFieldFromOtherTree(vos, "违规机构(第1层)", "违规机构", "select id,name,parentid from pub_corp_dept", 2, true, 2); //从第2层开始 加上第2层
		reportDMO.addOneFieldFromOtherTree(vos, "违规机构(第2层)", "违规机构", "select id,name,parentid from pub_corp_dept", 3, true, 2); //从第2层开始 加上第3层

		new ReportUtil().leftOuterJoin_YSMDFromDateTime(vos, "生效日期", "生效日期", "月");//处理时间显示格式
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "违规类型", "风险等级", "发现渠道", "违规机构(第1层)", "违规机构(第2层)", "生效日期", "违规积分", "罚款", "状态", "惩罚类型" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "数量", "违规积分" };
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
			al.add(getBeforehandGroupType("生效日期_违规机构(第1层)", "违规积分", BeforeHandGroupTypeVO.SUM));
			al.add(getBeforehandGroupType("生效日期_违规机构(第2层)", "违规积分", BeforeHandGroupTypeVO.SUM));
			al.add(getBeforehandGroupType("违规类型_违规机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("违规类型_违规机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			int model = TBUtil.getTBUtil().getSysOptionIntegerValue("违规积分扣分模式", 1);
			if (model == 1) {//如果使用风险等级维度
				al.add(getBeforehandGroupType("风险等级_违规机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
				al.add(getBeforehandGroupType("风险等级_违规机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			}

			al.add(getBeforehandGroupType("发现渠道_违规机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("发现渠道_违规机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("惩罚类型_违规机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("惩罚类型_违规机构(第2层)", "数量", BeforeHandGroupTypeVO.COUNT));
		} else {//图表的分类个数最好是四个以上十个以下的才好看【李春娟/2013-05-16】
			al.add(getBeforehandGroupType("风险等级_违规机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
			al.add(getBeforehandGroupType("发现渠道_违规机构(第1层)", "数量", BeforeHandGroupTypeVO.COUNT));
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

	//增加排序字段【李春娟/2013-05-16】
	public HashMap getGroupFieldOrderConfig() {
		HashMap map = new HashMap();
		try {
			CommDMO dmo = new CommDMO();
			String[] riskrank = dmo.getStringArrayFirstColByDS(null, "select id from pub_comboboxdict where type='违规积分_风险等级'  order by seq");
			String[] findrank = dmo.getStringArrayFirstColByDS(null, "select id from pub_comboboxdict where type='违规积分_发现渠道' order by seq");
			String[] punishtype = dmo.getStringArrayFirstColByDS(null, "select '无惩罚' punish from wltdual union all select * from(select punish from SCORE_PUNISH order by score)");
			map.put("风险等级", riskrank);
			map.put("发现渠道", findrank);
			map.put("惩罚类型", punishtype);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public HashMap getDrilGroupBind() {
		HashMap map = new HashMap(); //
		map.put("违规机构(第1层)", "违规机构(第2层)"); //实现点击违规机构(第1层)即可弹出违规机构(第2层)的统计结果
		return map;
	}

	public String getDrillTempletCode() throws Exception {
		return "SCORE_USER_LCJ_Q05";
	}

}
