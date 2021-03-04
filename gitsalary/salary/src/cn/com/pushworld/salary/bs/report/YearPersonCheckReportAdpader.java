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
 * �������ָ���ȫ��ȵ�ƽ���֡��Ѿ�����ָ��ļ�Ȩ�������Ҽ��������Ա���ۺ�����
 * 
 * @author haoming create by 2013-10-25
 */
public class YearPersonCheckReportAdpader {
	private SalaryFomulaParseUtil formulaUtil = new SalaryFomulaParseUtil();

	public Object[][] calcYearPersonCheckReport(String[] logid) {
		TBUtil tbutil = new TBUtil();
		CommDMO dmo = new CommDMO();
		ReportDMO reportDMO = new ReportDMO();

		List rtList = new ArrayList(); // ���ص�list
		try {
			// ֱ�Ӳ�����������ڸ������������У�����ָ���ƽ���֡�
			HashVO user_target_num[] = dmo.getHashVoArrayByDS(null,
					"select * from (select t3.code,t3.deptseq,t3.postseq,t1.checkeduser,t1.checkedusername,t1.checktype,t2.name targetname,sum(t1.checkscore)/count(t1.id) targetavgnum from sal_person_check_target_score t1 left join sal_person_check_list t2 on t1.targetid = t2.id left join v_sal_personinfo t3 on t1.checkeduser = t3.id where logid in(" + tbutil.getInCondition(logid)
							+ ")  and t1.targettype='Ա������ָ��' group by t1.checktype,t1.checkeduser,t1.checkedusername,t1.targetid) as b where b.targetname is not null order by deptseq,postseq");
			// �鱻��������ĳ���������µļ�Ȩ�ܷ�
			HashVO[] totlescore = dmo.getHashVoArrayByDS(null, "select temp.checkedusername,temp.checkeduser,temp.checktype,sum(temp.targetavgnum*t2.weights)/sum(weights) totle from " + "(select checkeduser,checkedusername,targetid,checktype,avg(checkscore) targetavgnum from sal_person_check_target_score  where  logid in(" + tbutil.getInCondition(logid)
					+ ")  and targettype='Ա������ָ��' group by checktype,checkeduser,checkedusername,targetid) as temp " + " left join sal_person_check_list t2 on temp.targetid= t2.id group by temp.checktype,temp.checkeduser,temp.checkedusername");

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
			HashMap<String, String> findDeptSelfDept = new HashMap<String, String>(); // ��¼���ŵı����ţ��Ӳ������ҡ�
			HashVO[] hvo = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('��������','��������') and id not like '$%' order by seq");
			HashVO[] kindVOs = dmo.getHashVoArrayByDS(null, "select *from sal_person_check_type");// ���˶������
			for (int i = 0; i < hvo.length; i++) {
				String id = hvo[i].getStringValue("id");
				String code = hvo[i].getStringValue("code");
				String deptName = "";
				if (code == null) {
					continue;
				}
				if (code.indexOf("$������") >= 0) {
					if (code.indexOf("$", code.indexOf("$������") + 4) >= 0) { // ������滹��
						deptName = code.substring(code.indexOf("$������") + 5, code.indexOf("$", code.indexOf("$������") + 4) - 1);
					} else {
						deptName = code.substring(code.indexOf("$������") + 5);
					}
				} else {
					deptName = id;
				}
				findDeptSelfDept.put(id, deptName);
			}
			HashMap<String, List<HashVO>> sameDeptTypeUserScores = new HashMap<String, List<HashVO>>();// ͬһ�����������µ�������Ա
			for (int i = 0; i < totlescore.length; i++) {
				String userdepttype = totlescore[i].getStringValue("corptype");
				userdepttype = findDeptSelfDept.get(userdepttype);// תһ��
				if (sameDeptTypeUserScores.containsKey(userdepttype)) {
					List list = sameDeptTypeUserScores.get(userdepttype);
					list.add(totlescore[i]);
				} else {
					List list = new ArrayList();
					list.add(totlescore[i]);
					sameDeptTypeUserScores.put(userdepttype, list);
				}
			}
			HashVO factorVO_2 = formulaUtil.getFoctorHashVO("Ա������ۺ�����");//
			List newList = new ArrayList();
			HashMap<String, HashMap<String, String>> checktype_user_zhcp = new HashMap<String, HashMap<String, String>>();
			for (Iterator iterator = sameDeptTypeUserScores.entrySet().iterator(); iterator.hasNext();) {
				Entry object = (Entry) iterator.next();
				String depttype = (String) object.getKey();
				List depttypeUserScores = (List) object.getValue();
				for (int i = 0; i < kindVOs.length; i++) {
					HashVO kindvo = kindVOs[i];
					String kinds = kindvo.getStringValue("stationkinds"); // ��ȡÿһ�������Ӧ�ġ���λ��
					List list = new ArrayList();// ����Ҫ�Ƚϵ��˷ŵ�һ��
					for (int j = 0; j < depttypeUserScores.size(); j++) {//
						HashVO score = (HashVO) depttypeUserScores.get(j);
						String score_kind = score.getStringValue("stationkind");
						if (score != null && kinds.contains(score_kind)) { // ����С�
							list.add(score);
						}
					}
					// �������Ⱥ��������
					HashVO[] kind_ScoreVOs = (HashVO[]) list.toArray(new HashVO[0]);
					TBUtil.getTBUtil().sortHashVOs(kind_ScoreVOs, new String[][] { { "totle", "Y", "Y" } }); // ���շ�������
					int num = kind_ScoreVOs.length; // ������
					for (int j = 0; j < kind_ScoreVOs.length; j++) {
						kind_ScoreVOs[j].setAttributeValue("����", new BigDecimal(j + 1).divide(new BigDecimal(num), 2, BigDecimal.ROUND_HALF_UP).floatValue()); // ����
						if (factorVO_2 != null) {
							String yl = String.valueOf(formulaUtil.onExecute(factorVO_2, kind_ScoreVOs[j], new StringBuffer())); // �����ж�,���ù�ʽ���м��㡣
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
			LinkedHashMap<String, LinkedHashMap<String, HashVO>> checktype_user_scorevo = new LinkedHashMap<String, LinkedHashMap<String, HashVO>>();// ����hashvo��һ������ƴ�ӵ�ֵ
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
						userscorevo.setAttributeValue("����", username);
						userscorevo.setAttributeValue("��Ȩ�ܷ�", checktype_user_score.get(checktype).get(userid));
						userscorevo.setAttributeValue("�ۺ�����", checktype_user_zhcp.get(checktype).get(userid));
						userscorevo.setAttributeValue(targetName, value);
						user_scorevo.put(userid, userscorevo);
					}
				} else {
					LinkedHashMap<String, HashVO> user_scorevo = new LinkedHashMap<String, HashVO>();
					HashVO userscorevo = new HashVO();
					userscorevo.setAttributeValue("userid", userid);
					userscorevo.setAttributeValue("checktype", checktype);
					userscorevo.setAttributeValue("����", username);
					userscorevo.setAttributeValue("��Ȩ�ܷ�", checktype_user_score.get(checktype).get(userid));
					userscorevo.setAttributeValue("�ۺ�����", checktype_user_zhcp.get(checktype).get(userid));
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
			String[] typeseq = dmo.getStringArrayFirstColByDS(null, "select name from sal_person_check_type order by code"); //������������
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
			WLTLogger.getLogger(YearPersonCheckReportAdpader.class).error("��ȡʧ�ܣ�" + ex.getMessage());
		}
		return null;
	}

	private BillCellItemVO getBillTitleCellItemVO(String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setIseditable("N");
		item.setCellvalue(value);
		item.setBackground("184,255,185");
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("1");
		item.setSpan("1,1");
		return item;
	}

	// ������ɸ��г��� Gwang 2013-08-29
	private BillCellItemVO getBillNormalCellItemVO(int row, String value) {
		BillCellItemVO item = new BillCellItemVO();
		item.setIseditable("N");
		item.setCellvalue(value);
		if (row % 2 == 0) {
			item.setBackground("234,240,248");
		} else {
			item.setBackground("255,255,255");
		}
		item.setFonttype("������");
		item.setFontsize("12");
		item.setFontstyle("0");
		item.setSpan("1,1");
		return item;
	}
}
