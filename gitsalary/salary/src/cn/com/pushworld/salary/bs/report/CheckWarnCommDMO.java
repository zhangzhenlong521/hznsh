package cn.com.pushworld.salary.bs.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * 员工评议预警报表数据构造。
 * @author haoming
 * create by 2013-10-25
 */
public class CheckWarnCommDMO extends MultiLevelReportDataBuilderAdapter {
	//员工评议过程中，预警评价单指标雷同项，平均值
	private CommDMO dmo = new CommDMO();
	private ReportDMO reportDMO = new ReportDMO();
	private SalaryTBUtil stbutil = new SalaryTBUtil();

	@Override
	public HashVO[] buildReportData(HashMap queryConsMap) throws Exception {
		boolean add_all_avg = false; //是否添加总平均。
		if (queryConsMap.containsKey("obj_rowheader")) {
			String[] header = (String[]) queryConsMap.get("obj_rowheader");
			if (header != null && header.length == 1 && "打分人".equals(header[0])) {
				String name = (String) queryConsMap.get("obj_$typename");
				if (name != null && name.contains("平均分")) {
					add_all_avg = true;
				}
			}
		}
		String checkdate = (String) queryConsMap.get("checkdate"); //取检查日期
		String checktype_condition = (String) queryConsMap.get("checktype");//取考核人员类型

		String logid[] = dmo.getStringArrayFirstColByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "'");//取得计划。
		if (logid.length == 0) {//
			return new HashVO[0];
		}
		StringBuffer condition = new StringBuffer();
		if (checktype_condition != null && !checktype_condition.equals("")) {
			condition.append(" and checktype='" + checktype_condition + "'");
		}
		//查出评分人对某个指标的平均分，最大最小值。和评分数量。
		HashVO avg_max_min[] = dmo.getHashVoArrayByDS(null, "select scoreuser ,checktype 考核类型,targetid,avg(checkscore) 平均值,max(checkscore+0) 最大值,min(checkscore+0) 最小值,count(Id) 总数 from sal_person_check_score where logid =" + logid[0] + " and targettype='员工定性指标' " + condition.toString() + " group by checktype,scoreuser,targetid having count(id)>1");
		//查出评分人评出最大值的个数。用来计算最大值相同比
		HashVO maxcount[] = dmo.getHashVoArrayByDS(null, "select scoreuser , checktype 考核类型,targetid,count(id) 值 from sal_person_check_score where logid  =" + logid[0] + "  and targettype='员工定性指标' " + condition.toString() + " group by checktype,scoreuser,targetname,checkscore having checkscore = max(checkscore) and count(id)>1");
		HashVO alluser[] = dmo.getHashVoArrayByDS(null, "select t1.id,t1.name,t1.code,t1.deptid,t1.deptname,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid = t2.id");
		HashMap<String, HashVO> allusermap = new HashMap<String, HashVO>();
		for (int i = 0; i < alluser.length; i++) {
			allusermap.put(alluser[i].getStringValue("id"), alluser[i]);
		}
		HashMap<String, String> checktype_scoreuser_target_ltnum = new HashMap<String, String>(); //存放 同一检查类型，同一个打分人，同一个指标的雷同数
		HashMap<String, ArrayList> target_avglist = new HashMap<String, ArrayList>(); //指标对应的所有人的评分平均值集合

		String samenamepeople[] = dmo.getStringArrayFirstColByDS(null, "select name from v_sal_personinfo group by name having count(name)>1"); //找出相同的人。
		HashMap<String, String> samepeoplemap_newname = new HashMap<String, String>(); //相同人员新名字
		if (samenamepeople != null && samenamepeople.length > 1) {
			HashVO[] savepeoplevo = dmo.getHashVoArrayByDS(null, "select t1.id,t1.name,t1.code,t1.deptid,t1.deptname,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid = t2.id  where t1.name in(" + TBUtil.getTBUtil().getInCondition(samenamepeople) + ")");
			for (int i = 0; i < savepeoplevo.length; i++) {
				String shortname = savepeoplevo[i].getStringValue("shortname");
				StringBuffer sb = new StringBuffer();
				if (TBUtil.isEmpty(shortname)) {
					sb.append(savepeoplevo[i].getStringValue("deptname") + "-" + savepeoplevo[i].getStringValue("name"));
				} else {
					sb.append(shortname + "-" + savepeoplevo[i].getStringValue("name"));
				}
				if (samepeoplemap_newname.containsKey(sb.toString())) {
					sb.append("-" + savepeoplevo[i].getStringValue("code"));
				}
				samepeoplemap_newname.put(savepeoplevo[i].getStringValue("id"), sb.toString());
			}
		}
		for (int i = 0; i < maxcount.length; i++) {
			String checktype = maxcount[i].getStringValue("考核类型");
			String user = maxcount[i].getStringValue("scoreuser");
			String targetid = maxcount[i].getStringValue("targetid");
			String key = checktype + "_" + user + "_" + targetid;
			checktype_scoreuser_target_ltnum.put(key, maxcount[i].getStringValue("值"));//雷同
		}
		HashMap<String, LinkedHashMap<String, ArrayList>> checktype_user_scorelist = new HashMap<String, LinkedHashMap<String, ArrayList>>();
		for (int i = 0; i < avg_max_min.length; i++) {
			String checktype = avg_max_min[i].getStringValue("考核类型");
			String user = avg_max_min[i].getStringValue("scoreuser");
			String targetid = avg_max_min[i].getStringValue("targetid");
			String totleNum = avg_max_min[i].getStringValue("总数");
			double avg = avg_max_min[i].getDoubleValue("平均值", 0d);
			String avg_2 = String.format("%.2f", avg);
			avg_max_min[i].setAttributeValue("平均值", stbutil.subZeroAndDot(avg_2));
			String key = checktype + "_" + user + "_" + targetid;
			if (checktype_scoreuser_target_ltnum.containsKey(key)) {
				String value = checktype_scoreuser_target_ltnum.get(key);
				String per = "0";
				if (value != null) {
					float f = Float.parseFloat(value) / Float.parseFloat(totleNum);
					per = new BigDecimal(f * 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
				}
				avg_max_min[i].setAttributeValue("最高值雷同量", value);
				avg_max_min[i].setAttributeValue("最高值雷同比", stbutil.subZeroAndDot(per));
			}
			if (checktype_user_scorelist.containsKey(checktype)) {
				LinkedHashMap<String, ArrayList> user_target = checktype_user_scorelist.get(checktype);
				if (user_target.containsKey(user)) {
					ArrayList list = user_target.get(user);
					list.add(avg_max_min[i]);
				} else {
					ArrayList list = new ArrayList();
					list.add(avg_max_min[i]);
					user_target.put(user, list);
				}
			} else {
				LinkedHashMap<String, ArrayList> user_target = new LinkedHashMap<String, ArrayList>();
				ArrayList list = new ArrayList();
				list.add(avg_max_min[i]);
				user_target.put(user, list);
				checktype_user_scorelist.put(checktype, user_target);
			}
			if (target_avglist.containsKey(targetid)) {
				ArrayList list = target_avglist.get(targetid);
				list.add(avg_max_min[i].getStringValue("平均值"));
			} else {
				ArrayList avglist = new ArrayList();
				avglist.add(avg_max_min[i].getStringValue("平均值", "0"));
				target_avglist.put(targetid, avglist);
			}
		}
		ArrayList avgHashvoList = new ArrayList();
		for (Iterator iterator = target_avglist.entrySet().iterator(); iterator.hasNext();) {
			Entry<String, ArrayList> type = (Entry<String, ArrayList>) iterator.next();
			String targetid = type.getKey();
			ArrayList userscorelist = type.getValue();
			float value = 0;
			for (int i = 0; i < userscorelist.size(); i++) {
				value += Float.parseFloat(String.valueOf(userscorelist.get(i)));
			}
			String one_target_avg = new BigDecimal(value / userscorelist.size()).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
			if (add_all_avg) {
				HashVO allavg = new HashVO();
				allavg.setAttributeValue("targetid", targetid);
				allavg.setAttributeValue("平均值", stbutil.subZeroAndDot(one_target_avg));
				allavg.setAttributeValue("打分人", "总平均");
				allavg.setAttributeValue("最高值雷同量", "0");
				allavg.setAttributeValue("最高值雷同比", "0");
				allavg.setAttributeValue("最小值", "0");
				avgHashvoList.add(allavg);
			}
		}
		if (!add_all_avg) {
			reportDMO.addOneFieldFromOtherTable(avg_max_min, "指标名称", "targetid", "select id,name from sal_person_check_list  ");
			LinkedHashMap<String, ArrayList> user_scorelist = checktype_user_scorelist.get(checktype_condition);
			List list = new ArrayList();
			for (Iterator iterator = user_scorelist.entrySet().iterator(); iterator.hasNext();) {
				Entry object = (Entry) iterator.next();
				String userid = (String) object.getKey();
				ArrayList target = (ArrayList) object.getValue();
				float avg_avg = 0;
				float same_avg = 0;
				for (int i = 0; i < target.size(); i++) {
					HashVO hvo = (HashVO) target.get(i);
					avg_avg += hvo.getDoubleValue("平均值");
					same_avg += hvo.getDoubleValue("最高值雷同比", 0d);
				}
				HashVO avgtargetvo = new HashVO();
				avgtargetvo.setAttributeValue("scoreuser", userid);
				avgtargetvo.setAttributeValue("指标名称", "总平均");
				avgtargetvo.setAttributeValue("平均值", avg_avg / target.size());
				//				avgtargetvo.setAttributeValue("最高值雷同量", "0");
				avgtargetvo.setAttributeValue("最小值", "0");
				avgtargetvo.setAttributeValue("最高值雷同比", same_avg / target.size());
				list.add(avgtargetvo);
			}
			HashVO[] allavgs = (HashVO[]) list.toArray(new HashVO[0]);
			HashVO lastvo[] = new HashVO[avg_max_min.length + allavgs.length];
			System.arraycopy(allavgs, 0, lastvo, 0, allavgs.length);
			System.arraycopy(avg_max_min, 0, lastvo, allavgs.length, avg_max_min.length);
			for (int i = 0; i < lastvo.length; i++) {
				String userid = lastvo[i].getStringValue("scoreuser");
				HashVO uservo = allusermap.get(userid);
				if (uservo != null) {
					lastvo[i].setAttributeValue("打分人", uservo.getStringValue("name"));
					lastvo[i].setAttributeValue("部门名称", uservo.getStringValue("shortname"));
				}
			}
			reportDMO.addOneFieldFromOtherTable(lastvo, "打分人", "scoreuser", "select id,name from pub_user ");
			if (samepeoplemap_newname.size() > 0) {
				for (int i = 0; i < lastvo.length; i++) {
					String userid = lastvo[i].getStringValue("scoreuser");
					if (userid != null && samepeoplemap_newname.containsKey(userid)) {
						lastvo[i].setAttributeValue("打分人", samepeoplemap_newname.get(userid));
					}
				}
			}
			return lastvo;
		}
		reportDMO.addOneFieldFromOtherTable(avg_max_min, "打分人", "scoreuser", "select id,name from pub_user ");
		if (samepeoplemap_newname.size() > 0) {
			for (int i = 0; i < avg_max_min.length; i++) {
				String userid = avg_max_min[i].getStringValue("scoreuser");
				if (userid != null && samepeoplemap_newname.containsKey(userid)) {
					avg_max_min[i].setAttributeValue("打分人", samepeoplemap_newname.get(userid));
				}
			}
		}
		HashVO[] allavgs = (HashVO[]) avgHashvoList.toArray(new HashVO[0]);
		HashVO lastvo[] = new HashVO[avg_max_min.length + allavgs.length];
		System.arraycopy(allavgs, 0, lastvo, 0, allavgs.length);
		System.arraycopy(avg_max_min, 0, lastvo, allavgs.length, avg_max_min.length);
		reportDMO.addOneFieldFromOtherTable(lastvo, "指标名称", "targetid", "select id,name from sal_person_check_list  ");
		return lastvo;
	}

	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al_vos = new ArrayList();
		//		BeforeHandGroupTypeVO bhGroupVO = new BeforeHandGroupTypeVO();
		//		bhGroupVO.setName((al_vos.size() + 1 + "-指标名称-打分人-平均分"));
		//		bhGroupVO.setRowHeaderGroupFields(new String[] { "打分人" });
		//		bhGroupVO.setColHeaderGroupFields(new String[] { "指标名称" });
		//		bhGroupVO.setComputeGroupFields(new String[][] { { "平均值", "sum" } });
		//		bhGroupVO.setType("GRID");
		//		al_vos.add(bhGroupVO);

		BeforeHandGroupTypeVO bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-指标名称-部门-打分人-平均值-雷同比-预警"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "指标名称" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "部门名称", "打分人" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "平均值", "sum" }, { "最高值雷同比", "sum", "警界规则=100,80,50" } });
		bhGroupVO.setColGroupSubTotal(false); 
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-打分人-考核类型-指标名称-预警"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "指标名称" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "打分人" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "平均值", "sum" }, { "最高值雷同量", "sum" }, { "最高值雷同比", "sum", "警界规则=100,80,50" } });
		bhGroupVO.setRowGroupSubTotal(false);
		bhGroupVO.setColGroupSubTotal(false);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-指标名称-打分人-雷同量-雷同比-预警"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "打分人" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "指标名称" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "平均值", "sum" }, { "最高值雷同量", "sum" }, { "最高值雷同比", "sum", "警界规则=100,80,50" } });
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-指标名称-打分人-最小值-雷同比-预警"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "打分人" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "指标名称" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "平均值", "sum" }, { "最小值", "sum" }, { "最高值雷同比", "sum", "警界规则=100,80,50" } });
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "打分人", "统计类型", "考核类型", "指标名称", "平均值", "最大值", "最小值", "总数", "最高值雷同量", "最高值雷同比" };
	}

	@Override
	public String[] getSumFiledNames() {

		return new String[] { "平均值", "最高值雷同量", "最高值雷同比" };
	}

	@Override
	public String getDrillActionClassPath() throws Exception {
		return "cn.com.pushworld.salary.ui.warn.PersonCheckWarnWKPanel";
	}
}
