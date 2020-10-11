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
 * Ա������Ԥ���������ݹ��졣
 * @author haoming
 * create by 2013-10-25
 */
public class CheckWarnCommDMO extends MultiLevelReportDataBuilderAdapter {
	//Ա����������У�Ԥ�����۵�ָ����ͬ�ƽ��ֵ
	private CommDMO dmo = new CommDMO();
	private ReportDMO reportDMO = new ReportDMO();
	private SalaryTBUtil stbutil = new SalaryTBUtil();

	@Override
	public HashVO[] buildReportData(HashMap queryConsMap) throws Exception {
		boolean add_all_avg = false; //�Ƿ������ƽ����
		if (queryConsMap.containsKey("obj_rowheader")) {
			String[] header = (String[]) queryConsMap.get("obj_rowheader");
			if (header != null && header.length == 1 && "�����".equals(header[0])) {
				String name = (String) queryConsMap.get("obj_$typename");
				if (name != null && name.contains("ƽ����")) {
					add_all_avg = true;
				}
			}
		}
		String checkdate = (String) queryConsMap.get("checkdate"); //ȡ�������
		String checktype_condition = (String) queryConsMap.get("checktype");//ȡ������Ա����

		String logid[] = dmo.getStringArrayFirstColByDS(null, "select id from sal_target_check_log where checkdate='" + checkdate + "'");//ȡ�üƻ���
		if (logid.length == 0) {//
			return new HashVO[0];
		}
		StringBuffer condition = new StringBuffer();
		if (checktype_condition != null && !checktype_condition.equals("")) {
			condition.append(" and checktype='" + checktype_condition + "'");
		}
		//��������˶�ĳ��ָ���ƽ���֣������Сֵ��������������
		HashVO avg_max_min[] = dmo.getHashVoArrayByDS(null, "select scoreuser ,checktype ��������,targetid,avg(checkscore) ƽ��ֵ,max(checkscore+0) ���ֵ,min(checkscore+0) ��Сֵ,count(Id) ���� from sal_person_check_score where logid =" + logid[0] + " and targettype='Ա������ָ��' " + condition.toString() + " group by checktype,scoreuser,targetid having count(id)>1");
		//����������������ֵ�ĸ����������������ֵ��ͬ��
		HashVO maxcount[] = dmo.getHashVoArrayByDS(null, "select scoreuser , checktype ��������,targetid,count(id) ֵ from sal_person_check_score where logid  =" + logid[0] + "  and targettype='Ա������ָ��' " + condition.toString() + " group by checktype,scoreuser,targetname,checkscore having checkscore = max(checkscore) and count(id)>1");
		HashVO alluser[] = dmo.getHashVoArrayByDS(null, "select t1.id,t1.name,t1.code,t1.deptid,t1.deptname,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.deptid = t2.id");
		HashMap<String, HashVO> allusermap = new HashMap<String, HashVO>();
		for (int i = 0; i < alluser.length; i++) {
			allusermap.put(alluser[i].getStringValue("id"), alluser[i]);
		}
		HashMap<String, String> checktype_scoreuser_target_ltnum = new HashMap<String, String>(); //��� ͬһ������ͣ�ͬһ������ˣ�ͬһ��ָ�����ͬ��
		HashMap<String, ArrayList> target_avglist = new HashMap<String, ArrayList>(); //ָ���Ӧ�������˵�����ƽ��ֵ����

		String samenamepeople[] = dmo.getStringArrayFirstColByDS(null, "select name from v_sal_personinfo group by name having count(name)>1"); //�ҳ���ͬ���ˡ�
		HashMap<String, String> samepeoplemap_newname = new HashMap<String, String>(); //��ͬ��Ա������
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
			String checktype = maxcount[i].getStringValue("��������");
			String user = maxcount[i].getStringValue("scoreuser");
			String targetid = maxcount[i].getStringValue("targetid");
			String key = checktype + "_" + user + "_" + targetid;
			checktype_scoreuser_target_ltnum.put(key, maxcount[i].getStringValue("ֵ"));//��ͬ
		}
		HashMap<String, LinkedHashMap<String, ArrayList>> checktype_user_scorelist = new HashMap<String, LinkedHashMap<String, ArrayList>>();
		for (int i = 0; i < avg_max_min.length; i++) {
			String checktype = avg_max_min[i].getStringValue("��������");
			String user = avg_max_min[i].getStringValue("scoreuser");
			String targetid = avg_max_min[i].getStringValue("targetid");
			String totleNum = avg_max_min[i].getStringValue("����");
			double avg = avg_max_min[i].getDoubleValue("ƽ��ֵ", 0d);
			String avg_2 = String.format("%.2f", avg);
			avg_max_min[i].setAttributeValue("ƽ��ֵ", stbutil.subZeroAndDot(avg_2));
			String key = checktype + "_" + user + "_" + targetid;
			if (checktype_scoreuser_target_ltnum.containsKey(key)) {
				String value = checktype_scoreuser_target_ltnum.get(key);
				String per = "0";
				if (value != null) {
					float f = Float.parseFloat(value) / Float.parseFloat(totleNum);
					per = new BigDecimal(f * 100).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
				}
				avg_max_min[i].setAttributeValue("���ֵ��ͬ��", value);
				avg_max_min[i].setAttributeValue("���ֵ��ͬ��", stbutil.subZeroAndDot(per));
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
				list.add(avg_max_min[i].getStringValue("ƽ��ֵ"));
			} else {
				ArrayList avglist = new ArrayList();
				avglist.add(avg_max_min[i].getStringValue("ƽ��ֵ", "0"));
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
				allavg.setAttributeValue("ƽ��ֵ", stbutil.subZeroAndDot(one_target_avg));
				allavg.setAttributeValue("�����", "��ƽ��");
				allavg.setAttributeValue("���ֵ��ͬ��", "0");
				allavg.setAttributeValue("���ֵ��ͬ��", "0");
				allavg.setAttributeValue("��Сֵ", "0");
				avgHashvoList.add(allavg);
			}
		}
		if (!add_all_avg) {
			reportDMO.addOneFieldFromOtherTable(avg_max_min, "ָ������", "targetid", "select id,name from sal_person_check_list  ");
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
					avg_avg += hvo.getDoubleValue("ƽ��ֵ");
					same_avg += hvo.getDoubleValue("���ֵ��ͬ��", 0d);
				}
				HashVO avgtargetvo = new HashVO();
				avgtargetvo.setAttributeValue("scoreuser", userid);
				avgtargetvo.setAttributeValue("ָ������", "��ƽ��");
				avgtargetvo.setAttributeValue("ƽ��ֵ", avg_avg / target.size());
				//				avgtargetvo.setAttributeValue("���ֵ��ͬ��", "0");
				avgtargetvo.setAttributeValue("��Сֵ", "0");
				avgtargetvo.setAttributeValue("���ֵ��ͬ��", same_avg / target.size());
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
					lastvo[i].setAttributeValue("�����", uservo.getStringValue("name"));
					lastvo[i].setAttributeValue("��������", uservo.getStringValue("shortname"));
				}
			}
			reportDMO.addOneFieldFromOtherTable(lastvo, "�����", "scoreuser", "select id,name from pub_user ");
			if (samepeoplemap_newname.size() > 0) {
				for (int i = 0; i < lastvo.length; i++) {
					String userid = lastvo[i].getStringValue("scoreuser");
					if (userid != null && samepeoplemap_newname.containsKey(userid)) {
						lastvo[i].setAttributeValue("�����", samepeoplemap_newname.get(userid));
					}
				}
			}
			return lastvo;
		}
		reportDMO.addOneFieldFromOtherTable(avg_max_min, "�����", "scoreuser", "select id,name from pub_user ");
		if (samepeoplemap_newname.size() > 0) {
			for (int i = 0; i < avg_max_min.length; i++) {
				String userid = avg_max_min[i].getStringValue("scoreuser");
				if (userid != null && samepeoplemap_newname.containsKey(userid)) {
					avg_max_min[i].setAttributeValue("�����", samepeoplemap_newname.get(userid));
				}
			}
		}
		HashVO[] allavgs = (HashVO[]) avgHashvoList.toArray(new HashVO[0]);
		HashVO lastvo[] = new HashVO[avg_max_min.length + allavgs.length];
		System.arraycopy(allavgs, 0, lastvo, 0, allavgs.length);
		System.arraycopy(avg_max_min, 0, lastvo, allavgs.length, avg_max_min.length);
		reportDMO.addOneFieldFromOtherTable(lastvo, "ָ������", "targetid", "select id,name from sal_person_check_list  ");
		return lastvo;
	}

	@Override
	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Grid() {
		ArrayList al_vos = new ArrayList();
		//		BeforeHandGroupTypeVO bhGroupVO = new BeforeHandGroupTypeVO();
		//		bhGroupVO.setName((al_vos.size() + 1 + "-ָ������-�����-ƽ����"));
		//		bhGroupVO.setRowHeaderGroupFields(new String[] { "�����" });
		//		bhGroupVO.setColHeaderGroupFields(new String[] { "ָ������" });
		//		bhGroupVO.setComputeGroupFields(new String[][] { { "ƽ��ֵ", "sum" } });
		//		bhGroupVO.setType("GRID");
		//		al_vos.add(bhGroupVO);

		BeforeHandGroupTypeVO bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-ָ������-����-�����-ƽ��ֵ-��ͬ��-Ԥ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "ָ������" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "��������", "�����" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "ƽ��ֵ", "sum" }, { "���ֵ��ͬ��", "sum", "�������=100,80,50" } });
		bhGroupVO.setColGroupSubTotal(false); 
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-�����-��������-ָ������-Ԥ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "ָ������" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "�����" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "ƽ��ֵ", "sum" }, { "���ֵ��ͬ��", "sum" }, { "���ֵ��ͬ��", "sum", "�������=100,80,50" } });
		bhGroupVO.setRowGroupSubTotal(false);
		bhGroupVO.setColGroupSubTotal(false);
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-ָ������-�����-��ͬ��-��ͬ��-Ԥ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "�����" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "ָ������" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "ƽ��ֵ", "sum" }, { "���ֵ��ͬ��", "sum" }, { "���ֵ��ͬ��", "sum", "�������=100,80,50" } });
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		bhGroupVO = new BeforeHandGroupTypeVO();
		bhGroupVO.setName((al_vos.size() + 1 + "-ָ������-�����-��Сֵ-��ͬ��-Ԥ��"));
		bhGroupVO.setRowHeaderGroupFields(new String[] { "�����" });
		bhGroupVO.setColHeaderGroupFields(new String[] { "ָ������" });
		bhGroupVO.setComputeGroupFields(new String[][] { { "ƽ��ֵ", "sum" }, { "��Сֵ", "sum" }, { "���ֵ��ͬ��", "sum", "�������=100,80,50" } });
		bhGroupVO.setType("GRID");
		al_vos.add(bhGroupVO);

		return (BeforeHandGroupTypeVO[]) al_vos.toArray(new BeforeHandGroupTypeVO[0]);
	}

	@Override
	public String[] getGroupFieldNames() {
		return new String[] { "�����", "ͳ������", "��������", "ָ������", "ƽ��ֵ", "���ֵ", "��Сֵ", "����", "���ֵ��ͬ��", "���ֵ��ͬ��" };
	}

	@Override
	public String[] getSumFiledNames() {

		return new String[] { "ƽ��ֵ", "���ֵ��ͬ��", "���ֵ��ͬ��" };
	}

	@Override
	public String getDrillActionClassPath() throws Exception {
		return "cn.com.pushworld.salary.ui.warn.PersonCheckWarnWKPanel";
	}
}
