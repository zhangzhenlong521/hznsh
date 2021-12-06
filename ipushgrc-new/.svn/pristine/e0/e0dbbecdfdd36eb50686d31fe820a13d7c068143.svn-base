package com.pushworld.ipushgrc.bs.score.p080;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.infostrategy.to.report.ReportUtil;

/**
 * 违规积分统计逻辑类【李春娟/2013-05-16】& 郝明
 * @com.pushworld.ipushgrc.ui.score.p080.ScoreStatisWKPanel
 * @author lcj&hm
 *
 */
public class ScoreReportBuilderAdapter2 extends MultiLevelReportDataBuilderAdapter {
	private CommDMO dmo = new CommDMO();
	private boolean reportUserByCode = TBUtil.getTBUtil().getSysOptionBooleanValue("违规积分统计人员是否显示编码", true);

	public HashVO[] buildReportData(HashMap condition) throws Exception {
		//现有查询条件
		String scoretypeid = (String) condition.get("SCORETYPEID");//违规类型
		String riskrank = (String) condition.get("RISKRANK");//风险等级
		String findrank = (String) condition.get("FINDRANK");//发现渠道

		String deptid = (String) condition.get("DEPTID");//违规机构
		String state = (String) condition.get("state");//状态
		RefItemVO ref_effectdate = (RefItemVO) condition.get("obj_EFFECTDATE");//生效日期,日期类型比较特殊,需要取得参照对象值,然后替换其中的{itemkey}
		String punishtype = (String) condition.get("punishtype");//惩罚类型

		TBUtil tbutil = new TBUtil();
		StringBuffer sb_sql = new StringBuffer(
				"select t1.id,t1.HAPPENDATE 发生日期, t1.scoretype 违规类型,t1.findrank 发现渠道,t1.deptid, t1.deptid 部门,t1.effectdate 生效日期,t1.finalscore 扣分,t1.finalmoney 罚款,t1.state 状态,t1.punishtype 惩罚类型,t1.scorestandardid,t1.scorestandard2 违规行为内容,t1.userid,t1.username 人员,t1.usercode 登录号 , t1.CREATEDEPT from v_score_user t1 left join pub_corp_dept t2 on t1.deptid = t2.id left join (select userid,max(seq) seq from v_pub_user_post_1 group by userid) t3 on t3.userid = t1.userid where 1=1 ");//这里查询所有的记录

		StringBuffer sb_sql2 = new StringBuffer("select t1.deptid,t1.userid,t1.usercode 登录号,sum(t1.finalscore) score from v_score_user t1 left join pub_corp_dept t2 on t1.deptid = t2.id where 1=1");

		if (scoretypeid != null && !"".equals(scoretypeid)) {
			sb_sql.append(" and t1.scoretypeid in(" + tbutil.getInCondition(scoretypeid) + ") ");
			sb_sql2.append(" and t1.scoretypeid in(" + tbutil.getInCondition(scoretypeid) + ") ");
		}
		if (riskrank != null && !"".equals(riskrank)) {
			sb_sql.append(" and t1.riskrank in(" + tbutil.getInCondition(riskrank) + ")");

			sb_sql2.append(" and t1.riskrank in(" + tbutil.getInCondition(riskrank) + ")");

		}
		if (findrank != null && !"".equals(findrank)) {
			sb_sql.append(" and t1.findrank in(" + tbutil.getInCondition(findrank) + ") ");

			sb_sql2.append(" and t1.findrank in(" + tbutil.getInCondition(findrank) + ") ");
		}
		if (deptid != null && !"".equals(deptid)) {
			sb_sql.append(" and t1.deptid in(" + tbutil.getInCondition(deptid) + ") ");

			sb_sql2.append(" and t1.deptid in(" + tbutil.getInCondition(deptid) + ") ");
		}
		if (state != null && !"".equals(state)) {
			sb_sql.append(" and t1.state in(" + tbutil.getInCondition(state) + ") ");
			sb_sql2.append(" and t1.state in(" + tbutil.getInCondition(state) + ") ");
		}
		if (ref_effectdate != null) {//生效日期
			String str_cons = ref_effectdate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "t1.effectdate"); //替换其中的特殊符号!为实际字段名!
			sb_sql.append(" and " + str_cons);
			sb_sql2.append(" and " + str_cons);
		}
		if (punishtype != null && !"".equals(punishtype)) {
			sb_sql.append(" and t1.punishtype in(" + tbutil.getInCondition(punishtype) + ") ");
			sb_sql2.append(" and t1.punishtype in(" + tbutil.getInCondition(punishtype) + ") ");
		}
		if (ref_effectdate != null) {//生效日期
			String str_cons = ref_effectdate.getHashVO().getStringValue("querycondition"); //取得查询条件itemkey
			str_cons = tbutil.replaceAll(str_cons, "{itemkey}", "t1.effectdate"); //替换其中的特殊符号!为实际字段名!
			if (str_cons.contains("" + tbutil.getCurrDate().substring(0, 4))) { //如果有当年的就不按照时间排序了。通过代码排序
				sb_sql.append(" order by t2.linkcode");
			} else {
				sb_sql.append(" order by t1.effectdate desc,t2.linkcode");
			}
		} else {
			sb_sql.append(" order by t1.effectdate desc,t2.linkcode");
		}
		
		sb_sql.append(",case when t3.seq is null then 999 else t3.seq end ");
		
		sb_sql2.append(" group by deptid,userid,usercode");
		HashVO[] vos = dmo.getHashVoArrayByDS(null, sb_sql.toString());
		ReportUtil util = new ReportUtil();
		util.leftOuterJoin_YSMDFromDateTime(vos, "发生日期(季)", "发生日期", "季");
		HashVO hvo[] = dmo.getHashVoArrayByDS(null, sb_sql2.toString());
		//处理树型结构显示不同层级【李春娟/2013-05-16】
		ReportDMO reportDMO = new ReportDMO(); //
		util.leftOuterJoin_YSMDFromDateTime(vos, "生效日期(季)", "生效日期", "季");
		reportDMO.leftOuterJoin_TreeTableFieldName(vos, "机构", "pub_corp_dept", "name", "id", "部门", "id", "parentid", 2);
		reportDMO.leftOuterJoin_TreeTableFieldName(vos, "机构_下一层", "pub_corp_dept", "name", "id", "部门", "id", "parentid", 3);
		reportDMO.leftOuterJoin_TableFieldName(vos, "部门", "pub_corp_dept", "name", "id", "部门");
		reportDMO.leftOuterJoin_TableFieldName(vos, "登记机构", "pub_corp_dept", "name", "id", "CREATEDEPT");
		HashMap<String, String> totle = new HashMap<String, String>();
		for (int i = 0; i < hvo.length; i++) {
			totle.put(hvo[i].getStringValue("deptid") + "_" + hvo[i].getStringValue("userid"), hvo[i].getStringValue("score"));
		}
		for (int i = 0; i < vos.length; i++) {
			String str_punishtype = vos[i].getStringValue("惩罚类型");
			if (str_punishtype == null || str_punishtype.equals("")) {
				vos[i].setAttributeValue("惩罚类型", "无惩罚");
			}
			String userid = vos[i].getStringValue("userid");
			String deptid_ = vos[i].getStringValue("deptid");
			if (!tbutil.isEmpty(userid) && totle.containsKey(deptid_ + "_" + userid)) {
				String score = totle.get(deptid_ + "_" + userid);
				if (tbutil.isEmpty(score)) {
					score = "0";
				}
				vos[i].setAttributeValue("总积分", score);
			} else {
				vos[i].setAttributeValue("总积分", "未知");
			}
			if (tbutil.isEmpty(vos[i].getStringValue("机构_下一层"))) {
				vos[i].setAttributeValue("机构_下一层", "-");
			}
		}

		boolean needCon = false;
		boolean needAddItem = false; //是否需要项目、子项目
		if (condition.get("obj_colheader") != null) {
			String[] headers = (String[]) condition.get("obj_colheader");
			for (int i = 0; i < headers.length; i++) {
				if (!needCon && headers[i] != null && headers[i].contains("违规行为内容")) {
					needCon = true;
				}
				if (!needAddItem && headers[i] != null && headers[i].contains("项目")) {
					needAddItem = true;
				}
			}
			if (!needCon) {
				String[] colders = (String[]) condition.get("obj_colheader");
				for (int i = 0; i < colders.length; i++) {
					if (!needCon && colders[i] != null && colders[i].contains("违规行为内容")) {
						needCon = true;
					}
					if (!needAddItem && colders[i] != null && colders[i].contains("项目")) {
						needAddItem = true;
					}
				}
			}
		}
		if (needAddItem || condition.get("obj_colheader") == null) {
			leftJoin(vos, "项目", "子项目", "scorestandardid");
		}
		return vos;
	}

	public String[] getGroupFieldNames() {
		return new String[] { "发生日期", "生效日期", "发生日期(季)", "生效日期(季)", "登录号", "人员", "发现渠道", "机构", "部门", "违规行为内容", "项目", "子项目", "登记机构", "总积分" };
	}

	public String[] getSumFiledNames() {
		return new String[] { "扣分" };
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al = new ArrayList();
		BeforeHandGroupTypeVO vo = null;
		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_机构(第1层)_发现渠道");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)", "发现渠道" });
		vo.setColHeaderGroupFields(new String[] { "机构" });
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_机构_部门_" + (reportUserByCode ? "登录号_" : "") + "人员_总积分_登机机构");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)" });
		if (reportUserByCode) {
			vo.setColHeaderGroupFields(new String[] { "机构", "部门", "登录号", "人员", "总积分", "登记机构" });
		} else {
			vo.setColHeaderGroupFields(new String[] { "机构", "部门", "人员", "总积分", "登记机构" });
		}
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_机构_部门_" + (reportUserByCode ? "登录号_" : "") + "人员");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)" });
		if (reportUserByCode) {
			vo.setColHeaderGroupFields(new String[] { "机构", "登录号", "部门", "人员" });
		} else {
			vo.setColHeaderGroupFields(new String[] { "机构", "部门", "人员" });
		}
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_机构_下一层_部门_" + (reportUserByCode ? "登录号_" : "") + "人员");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)" });
		if (reportUserByCode) {
			vo.setColHeaderGroupFields(new String[] { "机构", "机构_下一层", "部门", "登录号", "人员" });
		} else {
			vo.setColHeaderGroupFields(new String[] { "机构", "机构_下一层", "部门", "人员" });
		}
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_机构_部门_发现渠道");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)", "发现渠道" });
		vo.setColHeaderGroupFields(new String[] { "机构", "部门" });
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_违规行为内容_频率");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)" });
		vo.setColHeaderGroupFields(new String[] { "违规行为内容" });
		vo.setComputeGroupFields(new String[][] { { "发生频率", BeforeHandGroupTypeVO.COUNT } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_违规行为内容_扣分");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)" });
		vo.setColHeaderGroupFields(new String[] { "违规行为内容" });
		vo.setComputeGroupFields(new String[][] { { "扣分", BeforeHandGroupTypeVO.SUM } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);

		vo = new BeforeHandGroupTypeVO();
		vo.setName("生效日期_项目_违规行为内容_频率");
		vo.setRowHeaderGroupFields(new String[] { "生效日期(季)" });
		vo.setColHeaderGroupFields(new String[] { "项目", "子项目", "违规行为内容" });
		vo.setComputeGroupFields(new String[][] { { "发生频率", BeforeHandGroupTypeVO.COUNT } });
		vo.setRowGroupSubTotal(false);
		vo.setColGroupSubTotal(false);
		al.add(vo);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
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
			String[] punishtype = dmo.getStringArrayFirstColByDS(null, "select '无惩罚' punish from wltdual union all select * from(select punish from SCORE_PUNISH order by score) as b");
			map.put("风险等级", riskrank);
			map.put("发现渠道", findrank);
			map.put("惩罚类型", punishtype);
			int year = Integer.parseInt(TBUtil.getTBUtil().getCurrDate().substring(0, 4));
			String y_descrs[] = new String[12];
			for (int i = 0; i < 3; i++) {
				y_descrs[i * 4 + 0] = (year - i) + "年" + 4 + "季度";
				y_descrs[i * 4 + 1] = (year - i) + "年" + 3 + "季度";
				y_descrs[i * 4 + 2] = (year - i) + "年" + 2 + "季度";
				y_descrs[i * 4 + 3] = (year - i) + "年" + 1 + "季度";
			}
			map.put("生效日期(季)", y_descrs);

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

	public void leftJoin(HashVO[] _hvs, String _newItemName1, String _newItemName2, String _joinedFieldName) throws Exception {

		String _sql = "select id,typename,parentid from SCORE_TYPE";
		//		HashMap standMap = new CommDMO().getHashMapBySQLByDS(null, "select id,content from cmp_score_stand");
		HashVO vos[] = new CommDMO().getHashVoArrayAsTreeStructByDS(null, _sql, "id", "parentid", "seq", "");

		HashMap standMap = new HashMap(); //类型树 
		for (int i = 0; i < vos.length; i++) {
			standMap.put(vos[i].getStringValue("id"), vos[i]);
		}
		HashVO standard2[] = dmo.getHashVoArrayByDS(null, "select id,scoretype,point from SCORE_STANDARD2");
		HashMap standMap_2 = new HashMap(); //真正的内容
		for (int i = 0; i < standard2.length; i++) {
			standMap_2.put(standard2[i].getStringValue("id"), standard2[i]);
		}
		ReportDMO dmo = new ReportDMO();
		TBUtil tbUtil = new TBUtil();
		for (int i = 0; i < _hvs.length; i++) {
			String str_custids = _hvs[i].getStringValue(_joinedFieldName);
			if (str_custids == null || str_custids.trim().equals("")) {
				continue;
			}
			if (!standMap_2.containsKey(str_custids)) {
				continue;
			}
			HashVO vo = (HashVO) standMap_2.get(str_custids);
			String scoretype = vo.getStringValue("scoretype");
			if (scoretype == null || scoretype.equals("")) {
				continue;
			}

			Vector p_stand = new Vector();
			if (scoretype != null && !scoretype.equals("")) {
				if (standMap.containsKey(scoretype)) {
					HashVO currStandVO = (HashVO) standMap.get(scoretype);
					String pids = currStandVO.getStringValue("$parentpathids");
					String parent_standid[] = tbUtil.split(pids, ";");
					for (int j = 0; j < parent_standid.length; j++) {
						if (parent_standid[j] != null && !parent_standid[j].equals("") && !parent_standid[j].equals(str_custids) && standMap.containsKey(parent_standid[j])) {
							HashVO pvo = (HashVO) standMap.get(parent_standid[j]);
							p_stand.add(pvo.getStringValue("typename"));
						}
					}
				}
			}
			if (p_stand.size() == 1) {
				_hvs[i].setAttributeValue(_newItemName1, p_stand.get(0)); //增加新列
				_hvs[i].setAttributeValue(_newItemName2, "-"); //增加新列
			} else if (p_stand.size() == 2) {
				_hvs[i].setAttributeValue(_newItemName1, p_stand.get(0)); //增加新列
				_hvs[i].setAttributeValue(_newItemName2, p_stand.get(1)); //增加新列
			} else {
				_hvs[i].setAttributeValue(_newItemName1, "-"); //增加新列
				_hvs[i].setAttributeValue(_newItemName2, "-"); //增加新列
			}
			//			_hvs[i].setAttributeValue(_joinedFieldName, vo.getStringValue("point")); //增加新列
		}
	}

	public String getDrillTempletCode() throws Exception {
		return "SCORE_USER_LCJ_Q05";
	}
}
