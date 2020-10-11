package cn.com.pushworld.salary.bs.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.ReportDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;

/**
 * 计算各个指标的全年度的平均分。已经各项指标的加权法。并且计算出改人员的综合评价
 * 
 * @author haoming create by 2013-10-25
 */
public class YearPersonCheckReportAdpader {
	private SalaryFomulaParseUtil formulaUtil = new SalaryFomulaParseUtil();

	public Object[][] calcYearPersonCheckReport(String[] logid) {
		TBUtil tbutil = new TBUtil();
		CommDMO dmo = new CommDMO();
		ReportDMO reportDMO = new ReportDMO();

		List rtList = new ArrayList(); // 返回的list
		try {
			// 直接查出被考核人在各个考核类型中，各个指标的平均分。
			HashVO user_target_num[] = dmo.getHashVoArrayByDS(null,
					"select * from (select t3.code,t3.deptseq,t3.postseq,t1.checkeduser,t1.checkedusername,t1.checktype,t2.name targetname,sum(t1.checkscore)/count(t1.id) targetavgnum from sal_person_check_target_score t1 left join sal_person_check_list t2 on t1.targetid = t2.id left join v_sal_personinfo t3 on t1.checkeduser = t3.id where logid in(" + tbutil.getInCondition(logid)
							+ ")  and t1.targettype='员工定性指标' group by t1.checktype,t1.checkeduser,t1.checkedusername,t1.targetid) as b where b.targetname is not null order by deptseq,postseq");
			// 查被考核人在某考核类型下的加权总分
			HashVO[] totlescore = dmo.getHashVoArrayByDS(null, "select temp.checkedusername,temp.checkeduser,temp.checktype,sum(temp.targetavgnum*t2.weights)/sum(weights) totle from " + "(select checkeduser,checkedusername,targetid,checktype,avg(checkscore) targetavgnum from sal_person_check_target_score  where  logid in(" + tbutil.getInCondition(logid)
					+ ")  and targettype='员工定性指标' group by checktype,checkeduser,checkedusername,targetid) as temp " + " left join sal_person_check_list t2 on temp.targetid= t2.id group by temp.checktype,temp.checkeduser,temp.checkedusername");

			HashMap<String, HashMap<String, String>> checktype_user_score = new HashMap<String, HashMap<String, String>>();
			for (int i = 0; i < totlescore.length; i++) {
				String checkeduser = totlescore[i].getStringValue("checkeduser");
				String checktype = totlescore[i].getStringValue("checktype");
				String totle = totlescore[i].getStringValue("totle");
				if (checktype_user_score.containsKey(checktype)) {
					HashMap<String, String> user_score = checktype_user_score.get(checktype);
					user_score.put(checkeduser, totle);
				} else {
					HashMap<String, String> user_score = new HashMap<String, String>();
					user_score.put(checkeduser, totle);
					checktype_user_score.put(checktype, user_score);
				}
			}
			reportDMO.addOneFieldFromOtherTable(totlescore, "corptype", "checkeduser", "select t1.id,t2.corptype from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid= t2.id");
			// reportDMO.addOneFieldFromOtherTable(totlescore, "stationkind",
			// "checkeduser", "select id,stationkind from v_sal_personinfo ");
			reportDMO.addMoreFieldFromOtherTable(totlescore, "checkeduser", new String[] { "stationkind", "deptseq", "postseq" }, "select id,stationkind,deptseq,postseq from v_sal_personinfo ", "id");
			HashMap<String, String> findDeptSelfDept = new HashMap<String, String>(); // 记录部门的本部门，从策略中找。
			HashVO[] hvo = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型') and id not like '$%' order by seq");
			HashVO[] kindVOs = dmo.getHashVoArrayByDS(null, "select *from sal_person_check_type");// 考核对象分类
			for (int i = 0; i < hvo.length; i++) {
				String id = hvo[i].getStringValue("id");
				String code = hvo[i].getStringValue("code");
				String deptName = "";
				if (code == null) {
					continue;
				}
				if (code.indexOf("$本部门") >= 0) {
					if (code.indexOf("$", code.indexOf("$本部门") + 4) >= 0) { // 如果后面还有
						deptName = code.substring(code.indexOf("$本部门") + 5, code.indexOf("$", code.indexOf("$本部门") + 4) - 1);
					} else {
						deptName = code.substring(code.indexOf("$本部门") + 5);
					}
				} else {
					deptName = id;
				}
				findDeptSelfDept.put(id, deptName);
			}
			HashMap<String, List<HashVO>> sameDeptTypeUserScores = new HashMap<String, List<HashVO>>();// 同一个机构类型下的所有人员
			for (int i = 0; i < totlescore.length; i++) {
				String userdepttype = totlescore[i].getStringValue("corptype");
				userdepttype = findDeptSelfDept.get(userdepttype);// 转一下
				if (sameDeptTypeUserScores.containsKey(userdepttype)) {
					List list = sameDeptTypeUserScores.get(userdepttype);
					list.add(totlescore[i]);
				} else {
					List list = new ArrayList();
					list.add(totlescore[i]);
					sameDeptTypeUserScores.put(userdepttype, list);
				}
			}
			HashVO factorVO_2 = formulaUtil.getFoctorHashVO("员工年度综合评价");//
			List newList = new ArrayList();
			HashMap<String, HashMap<String, String>> checktype_user_zhcp = new HashMap<String, HashMap<String, String>>();
			for (Iterator iterator = sameDeptTypeUserScores.entrySet().iterator(); iterator.hasNext();) {
				Entry object = (Entry) iterator.next();
				String depttype = (String) object.getKey();
				List depttypeUserScores = (List) object.getValue();
				for (int i = 0; i < kindVOs.length; i++) {
					HashVO kindvo = kindVOs[i];
					String kinds = kindvo.getStringValue("stationkinds"); // 获取每一个分类对应的　岗位组
					List list = new ArrayList();// 把需要比较的人放到一起。
					for (int j = 0; j < depttypeUserScores.size(); j++) {//
						HashVO score = (HashVO) depttypeUserScores.get(j);
						String score_kind = score.getStringValue("stationkind");
						if (score != null && kinds.contains(score_kind)) { // 如果有。
							list.add(score);
						}
					}
					// 把这个大群就行排序
					HashVO[] kind_ScoreVOs = (HashVO[]) list.toArray(new HashVO[0]);
					TBUtil.getTBUtil().sortHashVOs(kind_ScoreVOs, new String[][] { { "totle", "Y", "Y" } }); // 按照分数排序
					int num = kind_ScoreVOs.length; // 总人数
					for (int j = 0; j < kind_ScoreVOs.length; j++) {
						kind_ScoreVOs[j].setAttributeValue("排名", new BigDecimal(j + 1).divide(new BigDecimal(num), 2, BigDecimal.ROUND_HALF_UP).floatValue()); // 排名
						if (factorVO_2 != null) {
							String yl = String.valueOf(formulaUtil.onExecute(factorVO_2, kind_ScoreVOs[j], new StringBuffer())); // 优良判断,调用公式进行计算。
							if (yl != null && !yl.equals("")) {
								String checktype = kind_ScoreVOs[j].getStringValue("checktype");
								String checkuser = kind_ScoreVOs[j].getStringValue("checkeduser");
								HashMap<String, String> user_zhcp = null;
								if (checktype_user_zhcp.containsKey(checktype)) {
									user_zhcp = checktype_user_zhcp.get(checktype);
								} else {
									user_zhcp = new HashMap<String, String>();
									checktype_user_zhcp.put(checktype, user_zhcp);
								}
								user_zhcp.put(checkuser, yl);
							}
						}
					}
				}
			}
			//
			LinkedHashMap<String, LinkedHashMap<String, HashVO>> checktype_user_scorevo = new LinkedHashMap<String, LinkedHashMap<String, HashVO>>();// 最后的hashvo是一个横向拼接的值
			for (int i = 0; i < user_target_num.length; i++) {
				String userid = user_target_num[i].getStringValue("checkeduser");
				String username = user_target_num[i].getStringValue("checkedusername");
				String checktype = user_target_num[i].getStringValue("checktype");
				String targetName = user_target_num[i].getStringValue("targetname");
				String value = user_target_num[i].getStringValue("targetavgnum");
				if (checktype_user_scorevo.containsKey(checktype)) {
					LinkedHashMap<String, HashVO> user_scorevo = checktype_user_scorevo.get(checktype);
					if (user_scorevo.containsKey(userid)) {
						HashVO userscorevo = user_scorevo.get(userid);
						if (targetName == null) {
							continue;
						}
						userscorevo.setAttributeValue(targetName, value);
					} else {
						HashVO userscorevo = new HashVO();
						userscorevo.setAttributeValue("userid", userid);
						userscorevo.setAttributeValue("checktype", checktype);
						userscorevo.setAttributeValue("姓名", username);
						userscorevo.setAttributeValue("加权总分", checktype_user_score.get(checktype).get(userid));
						userscorevo.setAttributeValue("综合评价", checktype_user_zhcp.get(checktype).get(userid));
						userscorevo.setAttributeValue(targetName, value);
						user_scorevo.put(userid, userscorevo);
					}
				} else {
					LinkedHashMap<String, HashVO> user_scorevo = new LinkedHashMap<String, HashVO>();
					HashVO userscorevo = new HashVO();
					userscorevo.setAttributeValue("userid", userid);
					userscorevo.setAttributeValue("checktype", checktype);
					userscorevo.setAttributeValue("姓名", username);
					userscorevo.setAttributeValue("加权总分", checktype_user_score.get(checktype).get(userid));
					userscorevo.setAttributeValue("综合评价", checktype_user_zhcp.get(checktype).get(userid));
					userscorevo.setAttributeValue(targetName, value);
					user_scorevo.put(userid, userscorevo);
					checktype_user_scorevo.put(checktype, user_scorevo);
				}
			}
			List<BillCellItemVO[]> billcellvorows = new ArrayList<BillCellItemVO[]>();
			for (Iterator iterator = checktype_user_scorevo.entrySet().iterator(); iterator.hasNext();) {
				billcellvorows = new ArrayList<BillCellItemVO[]>();
				Entry entry = (Entry) iterator.next();
				String checktype = (String) entry.getKey();
				LinkedHashMap<String, HashVO> user_scorevo = (LinkedHashMap<String, HashVO>) entry.getValue();
				HashVO userscores[] = user_scorevo.values().toArray(new HashVO[0]);
				String keys[] = new String[0];
				if (userscores.length > 0) {
					List<BillCellItemVO> titleCellList = new ArrayList<BillCellItemVO>();
					keys = userscores[0].getKeys();
					for (int i = 2; i < keys.length; i++) {
						BillCellItemVO itemvo = getBillTitleCellItemVO(keys[i]);
						titleCellList.add(itemvo);
					}
					billcellvorows.add(titleCellList.toArray(new BillCellItemVO[0]));
				}
				for (int i = 0; i < userscores.length; i++) {
					List<BillCellItemVO> onerowCellList = new ArrayList<BillCellItemVO>();
					for (int j = 2; j < keys.length; j++) {
						String cellvalue = userscores[i].getStringValue(keys[j]);
						if (j > 4 || j == 3) {
							cellvalue = new BigDecimal(cellvalue).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
						}
						BillCellItemVO cellvo = getBillNormalCellItemVO(i, cellvalue);
						if (j == 2) {
							cellvo.setCustProperty("userid", userscores[i].getStringValue("userid"));
						}
						onerowCellList.add(cellvo);
					}
					billcellvorows.add(onerowCellList.toArray(new BillCellItemVO[0]));
				}
				BillCellItemVO allcells[][] = billcellvorows.toArray(new BillCellItemVO[0][0]);
				BillCellVO cellvo = new BillCellVO();
				cellvo.setRowlength(allcells.length);
				cellvo.setCollength(keys.length - 2);
				cellvo.setCellItemVOs(allcells);
				Object[] obj = new Object[] { checktype, cellvo };
				rtList.add(obj);
			}
			String[] typeseq = dmo.getStringArrayFirstColByDS(null, "select name from sal_person_check_type order by code"); //考核类型排序
			List list = new ArrayList();
			for (int i = 0; i < typeseq.length; i++) {
				for (int j = 0; j < rtList.size(); j++) {
					Object[] obj = (Object[]) rtList.get(j);
					String type = (String) obj[0];
					if (typeseq[i] != null && typeseq[i].equals(type)) {
						list.add(obj);
						rtList.remove(obj);
						break;
					}
				}
			}
			for (int i = 0; i < rtList.size(); i++) {
				list.add(rtList.get(i));
			}
			return (Object[][]) list.toArray(new Object[0][0]);
		} catch (Exception ex) {
			ex.printStackTrace();
			WLTLogger.getLogger(YearPersonCheckReportAdpader.class).error("获取失败：" + ex.getMessage());
		}
		return null;
	}

	private BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setIseditable("N");
		item.setCellvalue(value);
		item.setBackground("184,255,185");
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		return item;
	}

	// 背景搞成隔行出现 Gwang 2013-08-29
	private BillCellItemVO getBillNormalCellItemVO(int row, String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setIseditable("N");
		item.setCellvalue(value);
		if (row % 2 == 0) {
			item.setBackground("234,240,248");
		} else {
			item.setBackground("255,255,255");
		}
		item.setFonttype("新宋体");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		return item;
	}
}
