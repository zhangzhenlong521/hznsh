package cn.com.pushworld.salary.bs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.pushworld.salary.bs.ifc.DelayMoneyCalIfc;
import cn.com.pushworld.salary.bs.report.YearPersonCheckReportAdpader;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.to.jepFunction.AbstractFormulaClassIfc;
import cn.com.pushworld.salary.ui.report.PersonStyleReportWKPanel;

/**
 * �������˵��ù�ʽ�������Dmo ������Ҫ���ڵ�����ʽ��ִ����� bsh�ͻ���ִ�в�������ϵͳ���ࡣ
 *
 * @author haoming create by 2013-8-24
 */
public class SalaryFormulaDMO extends AbstractDMO {
	private SalaryFomulaParseUtil formulaUtil = new SalaryFomulaParseUtil();
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(null).getDbtype(); // ���ݿ�����
	private CommDMO dmo;
	private TBUtil tbutil = new TBUtil();

	public CommDMO getDmo() {
		if (dmo == null) {
			dmo = new CommDMO();
		}
		return dmo;
	}

	/*
	 * �ͻ��˵���ĳ�����Ӷ�����в��ԡ�
	 */
	public Object[] onExecute(HashVO _factorVO, HashVO _baseDataHashVO) throws Exception {
		StringBuffer stepSB = new StringBuffer();
		Object obj = formulaUtil.onExecute(_factorVO, _baseDataHashVO, stepSB);
		return new Object[] { obj, stepSB };
	}

	public static final HashMap<String, Object> calcSchedule = new HashMap<String, Object>();

	/*
	 * ���ڸ߹ܼ��㶨�Կ���ʱ���õ��˲����ܷ֡�
	 * ����Ӳ��żƼ�ָ�� 2015-04-15
	 */
	public synchronized void calcDeptDLtarget(HashVO planVO, String state, String _targetType) throws Exception {
		try {
			String planid = planVO.getStringValue("id");
			Object currSchedule = getRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
			HashVO[] hvodept = stbutil.getHashVoArrayByDS(null, "select t1.*,t2.factors from sal_dept_check_score t1 left join sal_target_list t2 on t1.targetid = t2.id where t1.targettype='" + _targetType + "' and t1.checkdate='" + planVO.getStringValue("checkdate") + "'");
			int deptNum = hvodept.length;
			List updateSqlList = new ArrayList();
			updateSqlList.add("delete from sal_factor_calvalue where logid='" + planid + "' and type='" + _targetType + "'");
			for (int i = 0; i < hvodept.length; i++) {
				HashVO scoreVO = hvodept[i];
				scoreVO.setAttributeValue("maindeptid", scoreVO.getStringValue("checkeddept"));
				String targetName = scoreVO.getStringValue("targetname"); // �õ�ָ������
				String targetFactorName = targetName + "����";
				String targetdef = scoreVO.getStringValue("targetdefine"); // ��ʽ����
				String getvalue = scoreVO.getStringValue("getvalue"); // �õ�ȡֵ�Ĺ�ʽ
				String reportformula = scoreVO.getStringValue("reportformula");// ����ʽ
				String hisFactors = scoreVO.getStringValue("factors", ""); //������Ҫ��¼���������ӽ��
				String factors[] = tbutil.split(hisFactors, ";");
				HashMap<String, String> factor_value_map = new LinkedHashMap<String, String>(); //�洢���Ӽ���Ľ��
				Object reportvalue = null;
				if (targetdef == null || getvalue == null) {
					continue;
				}
				StringBuffer sb = new StringBuffer();
				formulaUtil.putDefaultFactorVO("����", getvalue, targetFactorName, "", "4λС��");
				Object rtobj = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetFactorName), scoreVO, sb);
				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				if (tbutil.isEmpty(reportformula)) {
					reportvalue = rtobj;
				} else {
					if (!reportformula.equalsIgnoreCase("x")) {
						String reportfactorName = targetName + "�������";
						formulaUtil.putDefaultFactorVO("����", reportformula, reportfactorName, "", "4λС��");
						try {
							reportvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(reportfactorName), scoreVO, sb);
						} catch (Exception ex) {
							throw new Exception("ָ��[" + targetName + "]����ʽȡִֵ��ʧ��.\n" + ex.getMessage());
						}
					} else {
						reportvalue = rtobj;
					}
				}
				// System.out.println("ȡֵ����" + rtobj);
				targetdef = stbutil.formulaReplaceX(targetdef, "x", "[" + targetFactorName + "]");
				targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[�ƻ�ֵ]", "[��������." + "planedvalue]");
				targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[Ŀ��ֵ]", "[��������." + "planedvalue]");
				targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[Ȩ��]", "[��������." + "weights]");
				formulaUtil.putDefaultFactorVO("����", targetdef, targetName + "�÷ּ���", "", "4λС��");
				Object obj = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "�÷ּ���"), scoreVO, sb);
				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				// System.out.println(obj);
				UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_dept_check_score");
				sql.putFieldValue("currvalue", String.valueOf(rtobj));
				sql.putFieldValue("checkscore", String.valueOf(obj));
				sql.putFieldValue("reportvalue", String.valueOf(reportvalue));
				sql.putFieldValue("status", "���ύ");
				sql.setWhereCondition("id='" + scoreVO.getStringValue("id") + "'");
				updateSqlList.add(sql);
				buildSqlAfterCalTargetFactorValue(scoreVO.getStringValue("id"), planid, factor_value_map, _targetType, updateSqlList);
				setCurrPlanSchedule(planid, _targetType + "����:" + (i + 1) + "/" + deptNum);
			}
			String stateSql = "update sal_target_check_log set status='" + state + "' where id=" + planid;
			updateSqlList.add(stateSql);
			new CommDMO().executeBatchByDS(null, updateSqlList, true, false);
			removeRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
		} catch (Exception ex) {
			// ��������ˣ���Ҫ�Ƴ���ǰ����״̬
			removeRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
			throw ex;
		}
	}

	/*
	 * ���㶨��ָ��,ִ�ж���ָ��ȡֵ��ʽ�� ��������Ϊ"ֱ�ӡ���λϵ������"����Ҫִ�м��㹫ʽ��
	 */
	public synchronized void calcPersonDLtarget(HashVO planVO, String _calbatch) throws Exception {
		String planid = planVO.getStringValue("id");
		Object currSchedule = getRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
		if (currSchedule != null) {
			StringBuffer strsb = new StringBuffer("��������[" + planVO.getStringValue("checkdate") + "]�ƻ����ڼ�����...\r\n");
			if (currSchedule != null) {
				Object[] obj = (Object[]) currSchedule;
				String schedule = (String) obj[0];
				strsb.append(schedule);
			}
			throw new Exception(strsb.toString());
		}
		try {
			List updateSqlList = new ArrayList();
			updateSqlList.add("delete from sal_factor_calvalue where logid='" + planid + "' and type='Ա������ָ��'");
			StringBuffer sqlTarget=new StringBuffer();
			if(planVO.getStringValue("zbtype").equals("����")){
				sqlTarget.append("select t1.*,t2.valuetype,t2.operationtype,t2.factors,t3.maindeptid,t3.maindeptid deptid,t3.maindeptid checkeddept,t4.id wgid from sal_person_check_score t1 left join  sal_person_check_list t2 on t1.targetid = t2.id left join wnsalarydb.v_wg_info t4 on t1.checkeduser = t4.id left join  v_sal_personinfo t3 on t4.G = t3.code  where t1.targettype='Ա������ָ��' and logid='"+planid+"' and t2.catalogid in('215')"); // ZPY ��2020-07-12�� ��Ϊ����ʵ�������������ԣ����������excel_tab_85Ϊv_wg_info
			}else{
				sqlTarget.append("select t1.*,t2.valuetype,t2.operationtype,t2.factors,t3.maindeptid,t3.maindeptid deptid,t3.maindeptid checkeddept from sal_person_check_score t1 left join  sal_person_check_list t2 on t1.targetid = t2.id left join v_sal_personinfo t3 on t1.checkeduser = t3.id  where t1.targettype='Ա������ָ��' and t1.logid='" + planid + "' and t2.catalogid not in('215')");
			}
			if (tbutil.isEmpty(_calbatch)) {
				sqlTarget.append(" and (t2.calbatch is null or t2.calbatch='') ");
			} else {
				sqlTarget.append(" and t2.calbatch=" + _calbatch);
			}
			HashVO[] hvoperson = stbutil.getHashVoArrayByDS(null, sqlTarget.toString());
			int personNum = hvoperson.length;

			for (int i = 0; i < hvoperson.length; i++) {
				HashVO scoreVO = hvoperson[i];
				String targetName = scoreVO.getStringValue("targetname"); // �õ�ָ������
				String targetFactorName = targetName + "����";
				String targetdef = scoreVO.getStringValue("targetdefine"); // ��ʽ����
				String getvalue = scoreVO.getStringValue("getvalue"); // �õ�ȡֵ�Ĺ�ʽ
				String partvalue = scoreVO.getStringValue("partvalue"); // �������ֵ��ʽ��
				String valueType = scoreVO.getStringValue("valuetype");
				String operationtype = scoreVO.getStringValue("operationtype"); // ���㷽ʽ
				// ,�Ӽ�
				String hisFactors = scoreVO.getStringValue("factors", ""); //������Ҫ��¼���������ӽ��
				String factors[] = tbutil.split(hisFactors, ";");
				HashMap<String, String> factor_value_map = new LinkedHashMap<String, String>(); //�洢���Ӽ���Ľ��
				StringBuffer sb = new StringBuffer();
				formulaUtil.putDefaultFactorVO("����", getvalue, targetFactorName, "", "4λС��");
				// scoreVO.setAttributeValue("", value)
				Object currvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetFactorName), scoreVO, sb); // �õ�ʵ��ֵ
				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				// System.out.println("ȡֵ����" + rtobj);
				Object money = 0;
				Object currpartvalue = null;
				if (!tbutil.isEmpty(partvalue)) {
					partvalue = stbutil.formulaReplaceX(partvalue, "x", "[" + targetFactorName + "]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[�ƻ�ֵ]", "[��������." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[Ŀ��ֵ]", "[��������." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[Ȩ��]", "[��������." + "weights]");
					formulaUtil.putDefaultFactorVO("����", partvalue, targetName + "�������ֵ", "", "2λС��");
					currpartvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "�������ֵ"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				} else {
					currpartvalue = currvalue;
					formulaUtil.putDefaultFactorVO("����", partvalue, targetName + "�������ֵ", "", "2λС��");
					formulaUtil.putDefaultFactorValue(targetName + "�������ֵ", currpartvalue);
				}

				StringBuffer descrsb = new StringBuffer();
				if (("ֱ��".equals(valueType) || "��λϵ������".equals(valueType)) && targetdef != null && !targetdef.trim().equals("")) { // ͨ����ʽ�������Ч�湤��
					targetdef = stbutil.formulaReplaceX(targetdef, "x", "[" + targetName + "�������ֵ]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[�ƻ�ֵ]", "[��������." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[Ŀ��ֵ]", "[��������." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[Ȩ��]", "[��������." + "weights]");
					formulaUtil.putDefaultFactorVO("����", targetdef, targetName + "�÷ּ���", "", "2λС��");
					money = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "�÷ּ���"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
					descrsb.append("Ȩ��:" + scoreVO.getStringValue("weights") + "  ");
					descrsb.append("���㹫ʽ:" + scoreVO.getStringValue("targetdefine") + "\r\n�������:" + sb.toString());
				}
				UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_person_check_score");
				if (currvalue == null || "".equals(String.valueOf(currvalue))) {
					currvalue = 0;
				}
				sql.putFieldValue("realvalue", String.valueOf(currvalue)); // ������ֵ
				if (currpartvalue == null || "".equals(String.valueOf(currpartvalue))) {
					currpartvalue = 0;
				}
				sql.putFieldValue("checkscore", String.valueOf(currpartvalue));// �������ֵ
				sql.putFieldValue("status", "���ύ");
				if ("��".equals(operationtype)) {
					BigDecimal big = new BigDecimal(String.valueOf(money)).setScale(2, BigDecimal.ROUND_HALF_UP); //
					money = 0 - big.floatValue();
				}

				if (money != null) {
					sql.putFieldValue("money", String.valueOf(money));
					if (descrsb.length() > 3980) {
						sql.putFieldValue("descr", descrsb.substring(0, 3980));
					} else {

					}
				}
				sql.setWhereCondition("id='" + scoreVO.getStringValue("id") + "'");
				updateSqlList.add(sql);
				//�洢������������ӵĽ��
				buildSqlAfterCalTargetFactorValue(scoreVO.getStringValue("id"), planid, factor_value_map, "Ա������ָ��", updateSqlList);
				setCurrPlanSchedule(planid, "\r\nԱ������ָ�����:" + (i + 1) + "/" + (personNum));
			}
			new CommDMO().executeBatchByDS(null, updateSqlList, true, false);
			/** **Ա���������������ƽ��ֵ�����ֵ��������ֵ[YangQing/2013-11-23]**** */
			String sql_targetlist = "select id targetid,postid from v_sal_postgroup_dl_target  where targettype='Ա������ָ��'";

			//			StringBuffer sql_userpost = new StringBuffer("");
			//			sql_userpost.append(" select t1.id postid,t1.stationkind,t2.userid from pub_post t1 ");
			//			sql_userpost.append(" right join pub_user_post t2  ");
			//			sql_userpost.append(" on t1.id=t2.postid ");
			//			sql_userpost.append(" where t1.id is not null ");

			StringBuffer sql_userpost = new StringBuffer("");
			sql_userpost.append(" select stationkind,id from v_sal_personinfo where isuncheck='N' or isuncheck is null");

			String sql_scorelist = "select id,targetid,checkeduser,checkscore from sal_person_check_score where targettype='Ա������ָ��' and logid='" + planid + "'";

			// Ա������ָ������
			HashVO[] targetVO = new CommDMO().getHashVoArrayByDS(null, sql_targetlist);
			// Ա����λ����
			HashVO[] userpostVO = new CommDMO().getHashVoArrayByDS(null, sql_userpost.toString());
			// Ա���÷�����
			HashVO[] scoreVO = new CommDMO().getHashVoArrayByDS(null, sql_scorelist);
			HashMap<String, List<String>> posttype_user = new HashMap<String, List<String>>();// ��ŵõ�ÿ����λ�����µ���
			// �õ�ÿ����λ������������
			for (int a = 0; a < userpostVO.length; a++) {
				String stationkind = userpostVO[a].getStringValue("stationkind", "");// ��λ����
				String userid = userpostVO[a].getStringValue("id", "");// ��ԱID
				if (posttype_user.containsKey(stationkind)) {// ����ø�λ������ڣ�����ȡ������������Ա
					List<String> userids = posttype_user.get(stationkind);
					userids.add(userid);
					posttype_user.put(stationkind, userids);
				} else {
					List<String> userids = new ArrayList<String>();
					userids.add(userid);
					posttype_user.put(stationkind, userids);
				}
			}
			List<String> sql_score = new ArrayList<String>();
			for (int b = 0; b < targetVO.length; b++) {
				String posttype = targetVO[b].getStringValue("postid", "");// ��������Ⱥ
				String targetid = targetVO[b].getStringValue("targetid", "");// ָ��ID

				List<String> type_users = new ArrayList<String>();// ��ָ����Ȩ����ͬ�ĸ�λ������������
				if (!TBUtil.isEmpty(posttype) && posttype.contains(";")) {
					String[] split_posttype = posttype.split(";");
					for (int i = 0; i < split_posttype.length; i++) {
						String everypost = split_posttype[i];
						if (TBUtil.isEmpty(everypost)) {
							continue;
						}
						if (posttype_user.containsKey(everypost)) {// �ҵ���ͬ��λ����
							List<String> userids = posttype_user.get(everypost);
							for (int x = 0; x < userids.size(); x++) {// ��ÿ����λ�����µ��˻��ܵ���ָ�꿼����Ⱥ��
								type_users.add(userids.get(x));
							}
						}
					}
				}

				double total_score = 0;// ��ָ������ͬȨ���˵��ܵ÷�
				double max_score = 0;// ��ָ������ͬȨ���˵���ߵ÷�
				int count = 0;// ����
				String[][] score_detail = new String[scoreVO.length][2];// ��ÿ�����ݵ�ID���÷�
				// �����÷�,�ó��ܷ�
				for (int x = 0; x < scoreVO.length; x++) {
					String score_target = scoreVO[x].getStringValue("targetid", "");// �÷ֱ����ָ��ID
					String checkeduser = scoreVO[x].getStringValue("checkeduser", "");// ��������
					if (targetid.equals(score_target)) {// ָ����ͬ
						if (type_users.contains(checkeduser)) {// ֻ�����ָ����ͬȨ�صĿ�����Ⱥ
							String str_score = scoreVO[x].getStringValue("checkscore", "0");// �÷�
							String scoreid = scoreVO[x].getStringValue("id", "");// ID
							double checkscore = Double.parseDouble(str_score);// ���˵÷�

							count++;
							// ��������
							score_detail[count - 1][0] = scoreid;
							score_detail[count - 1][1] = checkscore + "";
							if (checkscore > max_score) {// �Ƚ����ֵ
								max_score = checkscore;
							}
							total_score += checkscore;// �ۼ��ܷ�
						}
					}

				}
				// ����ƽ��ֵ
				BigDecimal result_avg = new BigDecimal("0");
				if (count != 0) {// �жϳ����Ƿ�Ϊ0
					BigDecimal operand1 = new BigDecimal(total_score + "");
					BigDecimal operand2 = new BigDecimal(count + "");
					result_avg = operand1.divide(operand2, 2, RoundingMode.HALF_UP); // �ó���ָ������ͬȨ���˵�ƽ���÷�,������λ����������
				}
				// �������ֵ
				String str_maxscore = max_score + "";
				if (Math.round(max_score) - max_score == 0) {// ������ֵ��6.0��ʽ������Ϊ����;������ʽ���䣬��6.5
					str_maxscore = ((long) max_score) + "";
				}
				// ���򣬽���
				for (int x = 0; x < count - 1; x++) {
					if (score_detail[x][0] == null) {
						continue;
					}
					for (int y = 0; y < count - 1 - x; y++) {
						if (score_detail[y][0] == null) {
							continue;
						}
						String[] change = null;
						if (Double.parseDouble(score_detail[y][1]) < Double.parseDouble(score_detail[y + 1][1])) {
							change = score_detail[y + 1];
							score_detail[y + 1] = score_detail[y];
							score_detail[y] = change;
						}
					}
				}
				// ÿ������ƴ�Ӹ���SQL
				for (int a = 0; a < count; a++) {
					String id = score_detail[a][0];
					int seq = a + 1;// ����
					sql_score.add("update sal_person_check_score set avg_score='" + result_avg.toString() + "',max_score='" + str_maxscore + "',seq_score='" + (seq + "/" + count) + "' where id=" + id);
				}

			}
			new CommDMO().executeBatchByDS(null, sql_score);

			removeRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
		} catch (Exception ex) {
			// ��������ˣ���Ҫ�Ƴ���ǰ����״̬
			removeRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
			throw ex;
		}
	}

	//��������
	private void dealAfterCalTargetFactorValue(SalaryFomulaParseUtil _parse, HashMap _factor_value, String[] factor) {
		if (factor != null && factor.length > 0) {
			for (int i = 0; i < factor.length; i++) {
				if (!_factor_value.containsKey(factor[i])) { //���û�д洢��
					Object obj = _parse.getFactorisCalc(factor[i]);
					if (obj != null && !String.valueOf(obj).equals("") && !String.valueOf(obj).equals("null")) {
						_factor_value.put(factor[i], String.valueOf(obj));
					}
				}
			}
		}
	}

	//���������ָ��󣬴洢�����е�����ֵ.
	private void buildSqlAfterCalTargetFactorValue(String scoreID, String logid, HashMap _factor_value, String type, List sqlList) throws Exception {
		if (_factor_value != null && _factor_value.size() > 0) {
			Iterator it = _factor_value.entrySet().iterator();
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String factorName = (String) entry.getKey();
				String factorValue = (String) entry.getValue();
				InsertSQLBuilder insert = new InsertSQLBuilder("sal_factor_calvalue");
				insert.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_FACTOR_CALVALUE"));
				insert.putFieldValue("name", factorName);
				insert.putFieldValue("value", factorValue);
				insert.putFieldValue("logid", logid);
				insert.putFieldValue("type", type);
				insert.putFieldValue("foreignid", scoreID);
				sqlList.add(insert);
			}
		}
	}

	public void calcDeptTotleScoreIntoReviseTable(String logid, String state) throws Exception {
		HashVO vos[] = stbutil.getHashVoArrayByDS(null, "select distinct(t1.checkeddeptid),t1.checkeddeptname,t1.logid,t1.checkeddeptid maindeptid,t2.checkdate from sal_target_check_result t1 left join sal_target_check_log t2 on t2.id = t1.logid where logid = '" + logid + "'");
		if (vos.length > 0) {
			CommDMO dmo = new CommDMO();
			List list = new ArrayList();
			String deleteSql = "delete from sal_target_check_revise_result where logid = '" + logid + "'";

			HashMap<String, String> dxscoreMap = stbutil.getHashMapBySQLByDS(null, "select checkeddeptid,finalres from sal_target_check_result where logid='" + logid + "' and targettype='���Ŷ���ָ��'");
			HashMap<String, String> dlscoreMap = stbutil.getHashMapBySQLByDS(null, "select checkeddeptid,finalres from sal_target_check_result where logid='" + logid + "' and targettype='���Ŷ���ָ��'");
			list.add(deleteSql);
			for (int j = 0; j < vos.length; j++) {
				HashVO factorVO = formulaUtil.getFoctorHashVO("���ſ��˵÷�");
				Object obj = formulaUtil.onExecute(factorVO, vos[j], new StringBuffer());
				Object dxobj = dxscoreMap.get(vos[j].getStringValue("checkeddeptid"));
				if (dxobj == null) {
					dxobj = "0";
				}
				Object dlobj = dlscoreMap.get(vos[j].getStringValue("checkeddeptid"));
				if (dlobj == null) {
					dlobj = "0";
				}
				if (obj != null) {
					String deptscore = (new BigDecimal(String.valueOf(obj)).setScale(2, BigDecimal.ROUND_HALF_UP)).toString();
					String dxscore = (new BigDecimal(String.valueOf(dxobj)).setScale(2, BigDecimal.ROUND_HALF_UP)).toString();
					String dlscore = (new BigDecimal(String.valueOf(dlobj)).setScale(2, BigDecimal.ROUND_HALF_UP)).toString();
					InsertSQLBuilder insertsql = new InsertSQLBuilder("sal_target_check_revise_result");
					insertsql.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_SAL_TARGET_CHECK_REVISE_RESULT"));
					insertsql.putFieldValue("checkeddeptid", vos[j].getStringValue("checkeddeptid"));
					insertsql.putFieldValue("checkeddeptname", vos[j].getStringValue("checkeddeptname"));
					insertsql.putFieldValue("dxscore", dxscore);
					insertsql.putFieldValue("dlscore", dlscore);
					insertsql.putFieldValue("currscore", deptscore);
					insertsql.putFieldValue("revisescore", "0");
					insertsql.putFieldValue("logid", logid);
					list.add(insertsql);
				}
			}
			String updatestateSql = "update sal_target_check_log set status='" + state + "' where id=" + logid;
			list.add(updatestateSql);
			dmo.executeBatchByDS(null, list, true, false);
		}
	}

	/**
	 * ����Ч�湤�ʣ�ͨ�÷��������������ָ�꣬��ô�����scoreȫ���Ǽٵġ�IDΪ����
	 *
	 * @param isPerformCalc
	 *            �Ƿ������㡣������㣬���Բ���planid
	 * @return
	 */
	public synchronized HashVO[] calc_comm_p_Pay(HashVO[] hvoperson, String _planid, String checkdate, boolean isPerformCalc) throws Exception {
		CommDMO dmo = new CommDMO();
		// ÿ��ָ���ӱ��Ӧ�ĵ÷��ۺ�
		HashMap targetValueSumMap = new HashMap(); // ÿ����λȺ���ܷ�
		if (isPerformCalc) {
			for (int i = 0; i < hvoperson.length; i++) {//
				float f = 0;
				String groupid = hvoperson[i].getStringValue("groupid");
				if (targetValueSumMap.containsKey(groupid)) {
					f = Float.parseFloat(String.valueOf(targetValueSumMap.get(groupid)));
				}
				String checkscore = hvoperson[i].getStringValue("checkscore", "0");
				if (checkscore != null && !checkscore.equals("")) {
					f += Float.parseFloat(checkscore);
				}
				targetValueSumMap.put(groupid, f);
			}
		} else {
			targetValueSumMap = dmo.getHashMapBySQLByDS(null, "select t2.id,sum(t1.checkscore) A from sal_person_check_score t1 left join sal_person_check_post t2 on t1.groupid = t2.id   left join   sal_person_check_list t3 on t3.id = t2.targetid  where  t3.valuetype!='ֱ��'  and t1.targettype='Ա������ָ��' and  t1.logid='" + _planid + "' group by t2.id ");// �õ�ĳָ���Ӧ��ĳ����Ⱥ�÷��ܺ�
		}

		HashVO[] groupPostWeights = null;
		if ("ORACLE".equalsIgnoreCase(str_dbtype)) {
			groupPostWeights = stbutil.getHashVoArrayByDS(null, "select t2.id groupid ,t3.id posttype,sum(t2.weights) weights from sal_person_check_list t1 left join sal_person_check_post t2 on   t1.id = t2.targetid  left join pub_comboboxdict t3 on t2.postid like '%;'||t3.id||';%' and t3.type ='н��_��λ����' where t1.state='���뿼��'  group by t2.id,t3.id");
		} else { // mysql
			groupPostWeights = stbutil.getHashVoArrayByDS(null, "select t2.id groupid ,t3.id posttype,sum(t2.weights) weights from sal_person_check_list t1 left join sal_person_check_post t2 on   t1.id = t2.targetid  left join pub_comboboxdict t3 on t2.postid like concat('%;',t3.id,';%') and t3.type ='н��_��λ����' where t1.state='���뿼��'  group by t2.id,t3.id");
		}
		HashMap personStationratio = stbutil.getHashMapBySQLByDS(null, "select id,stationratio from sal_personinfo"); // ��Ա�ĸ�λϵ��
		HashMap<String, Float> ggcalc = new HashMap<String, Float>(); // ���ڼ���
		HashMap<String, Float> gbgcalc = new HashMap<String, Float>();// �����ڼ��㡣

		HashMap<String, Float> deptscoreMap = new HashMap<String, Float>(); // ���ſ��˷�������
		for (int i = 0; i < hvoperson.length; i++) {
			HashVO scoreVO = hvoperson[i];
			String scoreGroupId = scoreVO.getStringValue("groupid");
			String deptid = scoreVO.getStringValue("deptid"); //
			String valuetype = scoreVO.getStringValue("valuetype"); // ��ָ���Ӧ��Ⱥ���õļ��㷽ʽ.Ŀǰ�ô�������������.
			String checkeduser = scoreVO.getStringValue("checkeduser"); //
			float scoreValue = Float.parseFloat(scoreVO.getStringValue("checkscore")); // Ա����ָ��ʵ�����ֵ

			if ("���ڼ���".equals(valuetype)) {// (���˵÷�*��λϵ��)��
				String userStationRatio = (String) personStationratio.get(checkeduser);
				BigDecimal big = new BigDecimal(userStationRatio);
				float sum = 0;
				if (ggcalc.containsKey(scoreGroupId)) {
					sum = ggcalc.get(scoreGroupId);
				}
				sum += big.floatValue() * scoreValue;
				ggcalc.put(scoreGroupId, sum);
			} else if ("�����ڼ���".equals(valuetype)) {// (���˵÷�*���ŵ÷�*��λϵ��)��*�Ʒ���Ч����
				HashVO formulaVO = formulaUtil.getFoctorHashVO("���ſ��˷���������");
				float deptscore = 0;
				if (deptscoreMap.containsKey(deptid)) {
					deptscore = deptscoreMap.get(deptid);
				} else {
					deptscore = new BigDecimal(String.valueOf(formulaUtil.onExecute(formulaVO, scoreVO, new StringBuffer()))).floatValue(); // �Ժ�Ӳ��ſ����÷ֱ���ȡֵ
					deptscoreMap.put(deptid, deptscore); // ��������
				}
				String userStationRatio = (String) personStationratio.get(checkeduser);
				BigDecimal big = new BigDecimal(userStationRatio);
				float sum = 0;
				if (gbgcalc.containsKey(scoreGroupId)) {
					sum = gbgcalc.get(scoreGroupId);
				}
				sum += big.floatValue() * scoreValue * deptscore;
				gbgcalc.put(scoreGroupId, sum);
			}
		}

		HashMap hmap = stbutil.getHashMapBySQLByDS(null, "select stationkind A,sum(stationratio) B from v_sal_personinfo group by stationkind"); // ÿ����λ��Ⱥ��ϵ��
		TBUtil tbutil = new TBUtil();
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		Object obj = util.onExecute(util.getFoctorHashVO("Ч�湤�ʻ���"), null, new StringBuffer());
		if (obj == null) {
			throw new Exception("��ʽ���Ӷ�����û�з�������Ϊ��Ч�湤�ʻ�����,�����.");
		}
		float jxgzjs = Float.parseFloat(String.valueOf(obj)); // ��Ч���ʻ���
		HashMap<String, Float> postWeightSumMap = new HashMap(); // ÿ����λȺ����ָ�������õ�����Ȩ�غ͡�

		for (int i = 0; i < groupPostWeights.length; i++) {
			HashVO vo = groupPostWeights[i];
			String posttype = vo.getStringValue("posttype");
			if (!tbutil.isEmpty(posttype)) {
				float f = 0;
				if (postWeightSumMap.containsKey(posttype)) {
					f = postWeightSumMap.get(posttype);
				}
				f += Float.parseFloat(vo.getStringValue("weights"));
				postWeightSumMap.put(posttype, f);
			}
		}

		List sqlList = new ArrayList<UpdateSQLBuilder>();
		HashMap<String, Float> oneTargetGroupJFMoneySum = new HashMap<String, Float>(); // ĳ��ָ���Ӧ�ĸ�λ���ӱ��һ����¼
		// ���Էֵ�Ǯ���ܺ�
		HashMap<String, String> descrMap1 = new HashMap<String, String>();
		for (int i = 0; i < hvoperson.length; i++) {
			HashVO scoreVO = hvoperson[i];
			String scoreGroupId = scoreVO.getStringValue("groupid");
			String posts = scoreVO.getStringValue("postid"); // ��ȡĳ����ĳָ�걻���˵����и�λ.
			String valuetype = scoreVO.getStringValue("valuetype"); // ��ָ���Ӧ��Ⱥ���õļ��㷽ʽ.Ŀǰ�ô�������������.
			String allpost[] = tbutil.split(posts, ";"); // �õ����и�λȺ������
			StringBuffer descr = new StringBuffer(); // ������μ�����ĸ�ָ���Ч�湤�ʡ�
			descr.append("һ��ϵ��Ч�湤��" + jxgzjs + "\r\n��ָ�꿼�˵ĸ�λ�� [" + posts + "]");
			float targetMoneyJF = 0;
			if (oneTargetGroupJFMoneySum.containsKey(scoreGroupId)) {
				targetMoneyJF = oneTargetGroupJFMoneySum.get(scoreGroupId);
				descr.append(String.valueOf(descrMap1.get(scoreGroupId)));
			} else {
				StringBuffer innserDescrSB = new StringBuffer();
				for (int j = 0; j < allpost.length; j++) { // ���Ի���
					String postQName = allpost[j]; // ĳ��Ⱥ������
					if (!tbutil.isEmpty(postQName)) { // �����Ϊ��
						String onePostQratioSum = (String) hmap.get(postQName); // ĳ����λȺ��ϵ��֮��
						if (!tbutil.isEmpty(onePostQratioSum)) {
							// �ҵ��ĸ�λȺ�Ը�ָ��� Ȩ�ر���.
							for (int k = 0; k < groupPostWeights.length; k++) {
								HashVO groupAndpostAndSum = groupPostWeights[k];
								String groupid = groupAndpostAndSum.getStringValue("groupid"); //
								String posttype = groupAndpostAndSum.getStringValue("posttype"); //
								String weights = groupAndpostAndSum.getStringValue("weights"); //
								if (scoreGroupId.equals(groupid) && postQName.equals(posttype)) {
									float weighttotle = postWeightSumMap.get(posttype); // �õ��ø�λ���ڶ���ָ���е�Ȩ�غ�
									if (weighttotle == 0) {
										WLTLogger.getLogger().warn(">>" + posttype + ",û��Ȩ�غ͡�����������������<<");
									} else {
										float ncmoney = jxgzjs * Float.parseFloat(onePostQratioSum) * Float.parseFloat(weights) / weighttotle; // �Ѹø�λ��ϵ����*����*��ָ��ռ��
										targetMoneyJF += ncmoney;
										innserDescrSB.append("\r\n[" + postQName + "]�����˸�λϵ����:" + onePostQratioSum + ",��ָ�굱ǰȨ��ռ��" + weights + "/" + weighttotle + "=" + (Float.parseFloat(weights) / weighttotle) + ",�ó�Ч�湤��:" + jxgzjs + "*" + onePostQratioSum + "*" + (Float.parseFloat(weights) / weighttotle) + "=" + ncmoney);
									}
								}
							}
						}
					}
				}
				descrMap1.put(scoreGroupId, innserDescrSB.toString());
				oneTargetGroupJFMoneySum.put(scoreGroupId, targetMoneyJF);
				descr.append(innserDescrSB.toString());
			}
			descr.append("\r\n��ָ��Ʒ���Ч�湤�ʣ�" + targetMoneyJF);
			float scoreValue = Float.parseFloat(scoreVO.getStringValue("checkscore")); // Ա����ָ��ʵ�����ֵ
			float targetValueSum = Float.parseFloat(String.valueOf(targetValueSumMap.get(scoreVO.getStringValue("groupid")))); // ��ָ��÷��ܺ�
			float personMoney = 0;
			float targetMoneyYF = targetMoneyJF; // Ӧ��
			String checkeduser = scoreVO.getStringValue("checkeduser"); //
			String planvalueStr = scoreVO.getStringValue("planedvalue");
			if (!tbutil.isEmpty(planvalueStr)) { // ����мƻ�ֵ,�ᰴ�ձ�������.
				float planvalue = Float.parseFloat(planvalueStr);
				if (targetValueSum > planvalue) {
					targetMoneyYF = targetValueSum / planvalue * targetMoneyJF;
					descr.append("\r\n���ֵ:" + targetValueSum + ">�ƻ�ֵ:" + planvalue + "��ָ��Ӧ��Ч�湤������Ϊ:" + targetMoneyYF);
				} else {
					descr.append("\r\n[���ֵ]û�дﵽ[�ƻ�ֵ],ָ��Ӧ��Ч�湤��Ϊ:" + targetMoneyYF);
				}
			} else {
				descr.append("\r\n��ָ��û���趨���ֵ,ָ��Ӧ��Ч�湤��Ϊ:" + targetMoneyYF);
			}
			if ("����".equals(valuetype)) { // ֱ�Ӽ���,���ձ��˷���ռ��ָ��
				// ͬռ��Ȩ�ص���,���з���ռ�ȼ���.��ʽ=���˵÷ֱ�*�Ʒ���Ч����
				if (targetValueSum == 0) {
					float weight = Float.parseFloat(hvoperson[i].getStringValue("weights", "0"));
					float weighttotle = postWeightSumMap.get(hvoperson[i].getStringValue("stationkind"));
					personMoney = (Float.parseFloat((String) personStationratio.get(checkeduser))) * jxgzjs * weight / weighttotle; // �ó����
					descr.append("��������Ա���ڵĸ�λ��[" + hvoperson[i].getStringValue("stationkind") + "]�������˵Ľ��ֵΪ0,�����ϴ��������в���������ֵ.");
				} else {
					personMoney = scoreValue / targetValueSum * targetMoneyYF;
					descr.append("\r\n��ʼ�������Ա��ָ��Ч�湤��=����ʵ��ֵ/������ʵ��ֵ��*ָ��Ӧ��Ч�湤��");
					descr.append("\r\n=" + scoreValue + "/" + targetValueSum + "*" + targetMoneyJF + "=" + personMoney);
				}
			} else if ("���ڼ���".equals(valuetype)) {// (���˵÷�*��λϵ��)��*�Ʒ���Ч����
				Float sumscore = ggcalc.get(scoreGroupId);
				if (sumscore == 0) {// �жϷ�ĸ�Ƿ�Ϊ0������������ָ�����ʱ����ĸ����Ϊ0.
					personMoney = targetMoneyYF / hvoperson.length;
					descr.append("\r\n=���ݿ��������⣨�����˵�ʵ�ʵ÷�֮��Ϊ0����ȡƽ��ֵ���������ʵȷ�ϣ�����");
				} else {
					personMoney = (scoreValue * (Float.parseFloat((String) personStationratio.get(checkeduser)))) / sumscore * targetMoneyYF;
					descr.append("\r\n��ʼ�������Ա��ָ��Ч�湤��=(����ʵ��ֵ*��λϵ��)��ռ��*ָ��Ӧ��Ч�湤��");
					descr.append("\r\n=(" + scoreValue + "*" + (Float.parseFloat((String) personStationratio.get(checkeduser))) + ")/" + sumscore + "*" + targetMoneyYF + "=" + personMoney);
				}
			} else if ("�����ڼ���".equals(valuetype)) {// (���˵÷�*���ŵ÷�*��λϵ��)��*�Ʒ���Ч����
				float deptscores = deptscoreMap.get(scoreVO.getStringValue("deptid"));
				personMoney = (scoreValue * deptscores * (Float.parseFloat((String) personStationratio.get(checkeduser)))) / gbgcalc.get(scoreGroupId) * targetMoneyYF;
				descr.append("\r\n��ʼ�������Ա��ָ��Ч�湤��=(����ʵ��ֵ*���ŵ÷�*��λϵ��)��ռ��*ָ��Ӧ��Ч�湤��");
				descr.append("\r\n=(" + scoreValue + "*" + deptscores + "*" + (Float.parseFloat((String) personStationratio.get(checkeduser))) + ")/" + gbgcalc.get(scoreGroupId) + "*" + targetMoneyYF + "=" + personMoney);

			}
			if ("��".equals(scoreVO.getStringValue("operationtype"))) {
				personMoney = 0 - personMoney;
			}
			descr.append("\r\n��ָ���������Ϊ[" + scoreVO.getStringValue("operationtype") + "],����ֵΪ��" + personMoney);
			float weight = Float.parseFloat(hvoperson[i].getStringValue("weights", "0"));
			if (!postWeightSumMap.containsKey(hvoperson[i].getStringValue("stationkind"))) {// ��������ָ��û�и��£�����Ա�����˸�λ��
				UpdateSQLBuilder sqlb = new UpdateSQLBuilder("sal_person_check_score");
				sqlb.setWhereCondition("id = " + scoreVO.getStringValue("id"));
				descr = new StringBuffer();
				descr.append("��ָ�꿼�˵ĸ�λ��[" + posts + "],��������Ա��ǰ��λΪ[" + hvoperson[i].getStringValue("stationkind") + "]");
				descr.append("\r\n�������˲��ڸ�ָ�꿼�˷�Χ��.����ԭ��:���ɸôο��˼ƻ���,�Ѿ�������Ա���ɸ�ָ��Ŀ�������,Ȼ���ֵ����˸���Ա�ĸ�λ,ǰ�����ݲ�ƥ�䡣");
				descr.append("\r\n�޸ķ�ʽ�������ֱ���¹��ܵ㣬���[���ֱ����(����)]��ť,Ȼ�������¼���Ч�湤�ʡ�");
				sqlb.putFieldValue("descr", descr.toString());
				sqlList.add(sqlb);
				scoreVO.setAttributeValue("descr", descr.toString());
				continue;
			} else {
				float weighttotle = postWeightSumMap.get(hvoperson[i].getStringValue("stationkind"));
				float ncmoney = (Float.parseFloat((String) personStationratio.get(checkeduser))) * jxgzjs * weight / weighttotle; // �ó����
				descr.insert(0, "��ָ�����Ա�ó�" + ncmoney + ",ʵ�ʵ�:" + personMoney + ",����:" + (personMoney - ncmoney) + ",�������£�\r\n");
				if (!isPerformCalc) {
					setCurrPlanSchedule(_planid, "������Ա��ָ��Ч�湤��" + (i + 1) + "/" + hvoperson.length);
				} else {
					setRemoteActionSchedule("Ա������ָ������", "Ա������ָ������", "������Ա��ָ��Ч�湤��" + (i + 1) + "/" + hvoperson.length);
				}
				UpdateSQLBuilder sqlb = new UpdateSQLBuilder("sal_person_check_score");
				BigDecimal decimal = null;
				try {
					decimal = new BigDecimal(personMoney).setScale(4, BigDecimal.ROUND_HALF_UP);
				} catch (Exception e) {
					throw new Exception("�ڼ���[" + scoreVO.getStringValue("checkedusername") + "]��[" + scoreVO.getStringValue("targetname") + "]ʱ,��ָ��Ӧ����" + targetMoneyYF + ",����ռ:" + personMoney + "�Ѿ������:" + descr + "\n" + e.getMessage());
				}
				if (decimal.floatValue() == 0) {
					sqlb.putFieldValue("money", 0);
					scoreVO.setAttributeValue("money", 0);
				} else {
					sqlb.putFieldValue("money", String.valueOf(decimal.toString()));
					scoreVO.setAttributeValue("money", String.valueOf(decimal.toString()));
				}
				if (!isPerformCalc) {
					sqlb.putFieldValue("descr", descr.toString());
					sqlb.setWhereCondition("id = " + scoreVO.getStringValue("id"));
					sqlList.add(sqlb);
				}
				scoreVO.setAttributeValue("descr", descr.toString());
			}

		}
		dmo.executeBatchByDS(null, sqlList, true, false);

		// ��ʼ���ڶ� ��׼��������ĸ�ָ��Ч�湤�ʾ��е���
		List reviseSqlList = new ArrayList(); // ����Ч�湤�ʵ�����sql
		for (int i = 0; i < hvoperson.length; i++) {
			HashVO scoreVO = hvoperson[i];
			String targetdefine = scoreVO.getStringValue("targetdefine"); // �鿴�����Ƿ����õ���ִ�е��ࡣ
			if (targetdefine == null || targetdefine.equals("")) {
				continue;
			}
			if (targetdefine.indexOf("+") >= 0 || targetdefine.indexOf("-") >= 0 || targetdefine.indexOf("*") >= 0 || targetdefine.indexOf("/") >= 0 || (targetdefine.indexOf("[") >= 0 && targetdefine.indexOf("]") >= 0)) {
				String[] formulas = targetdefine.split(";\n"); // �ܶ��鹫ʽ
				StringBuffer desc = new StringBuffer();
				for (int j = 0; j < formulas.length; j++) {
					String customFactorName = "��ʽ" + j;
					String targetdef = formulas[j];
					targetdef = stbutil.formulaReplaceX(targetdef, "x", "[��������.money]");
					formulaUtil.putDefaultFactorVO("����", targetdef, customFactorName, "", "2λС��");
					Object lastReviseValue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(customFactorName), scoreVO, desc);
					if (formulas.length - 1 == j) { // ��������һ������Ҫ�жϷ���ֵ
						String descrs = scoreVO.getStringValue("descr", "") + "\r\n���ݹ�ʽ���µ���Ч�湤��Ϊ:" + lastReviseValue + ".��������\r\n" + desc.toString() + "";
						scoreVO.setAttributeValue("descr", descrs);
						if (!isPerformCalc) {
							UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_person_check_score");
							updateSql.putFieldValue("money", lastReviseValue + "");
							updateSql.putFieldValue("descr", descrs + "");
							updateSql.setWhereCondition("id = " + scoreVO.getStringValue("id"));
							reviseSqlList.add(updateSql);
						} else {
							hvoperson[i].setAttributeValue("money", lastReviseValue);
						}
					}
					desc.append("\r\n");
				}
			} else if (targetdefine.indexOf(".") >= 0 || targetdefine.indexOf("cn") >= 0 || targetdefine.indexOf("com") >= 0) {// �����.�ſ�������,Լ����.������cn����com.
				try {
					AbstractFormulaClassIfc formulaClassIfc = (AbstractFormulaClassIfc) Class.forName(targetdefine).newInstance();
					formulaClassIfc.onExecute(null, scoreVO, formulaUtil); // ���շ���ֵ
				} catch (ClassNotFoundException ex) {
					WLTLogger.getLogger(SalaryFormulaDMO.class).error(targetdefine + "�಻����", ex);
				} catch (Exception ex) {
					throw ex;
				}
			}
		}
		if (reviseSqlList.size() > 0 && !isPerformCalc) {
			dmo.executeBatchByDS(null, reviseSqlList); // ִ�е�����ĸ������
		}

		if (isPerformCalc) {
			removeRemoteActionSchedule("Ա������ָ������", "Ա������ָ������");
			return hvoperson;
		}

		// ��������㣬���治ִ��   group by t2.id
		HashVO uservo[] = dmo.getHashVoArrayByDS(null, "select distinct(t2.id),t2.*,t1.checkdate from sal_person_check_score t1 left join v_sal_personinfo t2 on t1.checkeduser = t2.id where t1.targettype='Ա������ָ��' and t1.logid='" + _planid + "'");
		String logid = _planid;
		List insertList = new ArrayList();
		// ɾ��һ�α��µ�Ч�湤��result�����ݡ�
		String deleteSql = "delete from sal_person_check_result where targettype='Ա������ָ��' and logid=" + logid;
		insertList.add(deleteSql);
		for (int i = 0; i < uservo.length; i++) {
			InsertSQLBuilder insertSql = new InsertSQLBuilder("sal_person_check_result"); // ��Ա�����յ÷ֱ����һ������.Ч�湤��
			insertSql.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_RESULT"));
			insertSql.putFieldValue("checkeduserid", uservo[i].getStringValue("id"));
			uservo[i].setAttributeValue("checkeduserid", uservo[i].getStringValue("id"));
			uservo[i].setAttributeValue("checkeduser", uservo[i].getStringValue("id"));
			Object money = util.onExecute(util.getFoctorHashVO("Ч�湤��"), uservo[i], new StringBuffer()); // �ӹ�ʽ��ȡЧ�湤��
			Object centerMoneyTotle = util.onExecute(util.getFoctorHashVO("�м����Ч�湤��"), uservo[i], new StringBuffer()); // ����ָ��Ч�湤���ܺ�
			Object stationratio = util.onExecute(util.getFoctorHashVO("��λϵ��"), uservo[i], new StringBuffer()); // ����ָ��Ч�湤���ܺ�
			Object money_jf = util.onExecute(util.getFoctorHashVO("ĳԱ���Ʒ�Ч�湤��"), uservo[i], new StringBuffer()); // ����ָ��Ч�湤���ܺ�
			Object jishu = util.onExecute(util.getFoctorHashVO("Ч�湤�ʻ���"), uservo[i], new StringBuffer());
			if (money == null) {
				money = 0;
			}
			StringBuffer descr = new StringBuffer("��Ա����λϵ��Ϊ��" + stationratio + ",");
			descr.append("��λ�Ʒ�Ч�湤��Ϊ��" + stationratio + "*" + jishu + "=" + money_jf + ",");
			descr.append("����ָ��Ч���ܹ���Ϊ:" + centerMoneyTotle + "");
			insertSql.putFieldValue("finalres", String.valueOf(centerMoneyTotle));
			insertSql.putFieldValue("money", String.valueOf(money));
			if ((new BigDecimal(String.valueOf(centerMoneyTotle)).floatValue()) < 0) { // ���Ч�湤�ʿ����ˡ�
				descr.append("Ч�湤��С��0,ʵ����0");
			} else {
				descr.append("ʵ��:" + money);
			}
			insertSql.putFieldValue("logid", logid);
			insertSql.putFieldValue("descr", descr.toString());
			insertSql.putFieldValue("targettype", "Ա������ָ��");
			setCurrPlanSchedule(_planid, "������ԱЧ�湤��" + (i + 1) + "/" + uservo.length);
			insertList.add(insertSql);
		}

		// �����ӳ�֧����
		dmo.executeBatchByDS(null, insertList, true, false); // ����¼sql������־
		removeRemoteActionSchedule(_planid, "ָ�����");
		return hvoperson;
	}

	/*
	 * �����ӳ�֧����
	 */
	public void calcDelayPay(String logid) throws Exception {
		try {
			String sys_delay_money_factor = tbutil.getSysOptionStringValue("ͨ��ϵͳ��������֧����������", "");
			String dealy_class = tbutil.getSysOptionStringValue("����֧���Զ���ʵ����", "");
			if (dealy_class.length() > 0 && dealy_class.indexOf(".") > 0) { //����
				DelayMoneyCalIfc formulaClassIfc = (DelayMoneyCalIfc) Class.forName(dealy_class).newInstance();
				formulaClassIfc.calc(logid);
				return; //ֱ�ӷ���
			}
			HashVO dealy_factor_hashvo = null;
			if (!tbutil.isEmpty(sys_delay_money_factor)) {
				dealy_factor_hashvo = formulaUtil.getFoctorHashVO(sys_delay_money_factor);
			}

			float jxbase = Float.parseFloat(formulaUtil.onExecute(formulaUtil.getFoctorHashVO("Ч�湤�ʻ���"), null, new StringBuffer()) + "");
			HashVO xyMoneyVOs[] = getDmo().getHashVoArrayByDS(null,
					"select t2.*,t1.checkeduserid,t2.id checkeduser,t1.money,t4.code postcode,t5.checkdate  from sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid= t2.id   left join PUB_COMBOBOXDICT t4 on t4.id = t2.stationkind left join sal_target_check_log t5 on t5.id = t1.logid where t1.targettype='Ա������ָ��' and t1.logid='" + logid + "' order by t2.deptseq,t2.postseq");
			// ��λ���Ӧ��ƽ��Ч�湤�ʡ�
			HashMap<String, String> postcode_avgMoney = getDmo().getHashMapBySQLByDS(null, "select t4.code,avg(t1.money) from sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid= t2.id   left join PUB_COMBOBOXDICT t4 on t4.id = t2.stationkind where t1.targettype='Ա������ָ��' and t1.logid='" + logid + "' group by t4.code");
			String currtime = tbutil.getCurrTime();
			String currdate = tbutil.getCurrDate().substring(0, 7); // ���·�
			List list = new ArrayList();
			setRemoteActionSchedule(logid, "ָ�����", "��ʼ��������֧����");
			boolean no_money_insert = tbutil.getSysOptionBooleanValue("������֧�������Ƿ�����", true);
			for (int i = 0; i < xyMoneyVOs.length; i++) {
				int delay = 0;
				StringBuffer descr = new StringBuffer();
				if (!tbutil.isEmpty(sys_delay_money_factor) && dealy_factor_hashvo != null) {
					Object exc_money = formulaUtil.onExecute(dealy_factor_hashvo, xyMoneyVOs[i], new StringBuffer());
					if (exc_money != null) {
						delay = new BigDecimal(String.valueOf(exc_money)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
					}
				} else {
					float yfmoney = Float.parseFloat(xyMoneyVOs[i].getStringValue("money", "0")); //
					String stationRatio = xyMoneyVOs[i].getStringValue("stationRatio"); // ��λϵ��
					String postcode = xyMoneyVOs[i].getStringValue("postcode"); // ��λ����,�ͻ��ܾ���Ϳͻ�����codeһ��.
					if (postcode == null || postcode.equals("")) {
						continue;
					}
					float xsmoney = Float.parseFloat(stationRatio) * jxbase;// ϵ�����ʣ��õ�ĳ�˵ĿɼƷ���Ч�湤��.
					float avg = Float.parseFloat((String) postcode_avgMoney.get(postcode));
					float delaymoney = 0;

					if ("�ͻ�����".equals(postcode) || "�Ŵ�Ա".equals(postcode) || "֧���г�".equals(postcode)) {
						if (yfmoney > avg) {
							delaymoney = (yfmoney - avg) / 2;
						}
						delay = new BigDecimal(delaymoney).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
						if (delay > 0) {
							descr.append("��" + postcode + "����λȺ��ƽ��ֵΪ" + avg + ",Ӧ��" + yfmoney + ",�۳���������һ��" + "(" + yfmoney + "-" + avg + ")/2=" + delay + "��Ϊ����֧����");
						}
					} else { // ��
						if (yfmoney > avg && yfmoney > xsmoney) {
							if (avg > xsmoney) {
								delaymoney = yfmoney - avg;//
								delay = new BigDecimal(delaymoney).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
								descr.append("��" + postcode + "����λȺ��ƽ��ֵΪ" + avg + ",ƽ��ֵ���ڸ�λϵ��Ч�湤��" + xsmoney + ",Ӧ��" + yfmoney + ",�۳�" + yfmoney + "-" + avg + "=" + delay + "��Ϊ����֧����");
							} else {
								delaymoney = yfmoney - xsmoney; // �����Ʒ���ȫ���۵�
								delay = new BigDecimal(delaymoney).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
								descr.append("��" + postcode + "����λȺ��ƽ��ֵΪ" + avg + ",ƽ��ֵС�ڸ�λϵ��Ч�湤��" + xsmoney + ",Ӧ��" + yfmoney + ",�۳�" + yfmoney + "-" + xsmoney + "=" + delay + "��Ϊ����֧����");
							}
						}
					}
				}
				if (delay <= 0 && !no_money_insert) {
					continue;
				}
				if (delay <= 0) {
					descr.append("����û������֧����������");
				}
				InsertSQLBuilder sql = new InsertSQLBuilder("sal_person_fund_account");
				sql.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_SAL_PERSON_FUND_ACCOUNT"));
				sql.putFieldValue("userid", xyMoneyVOs[i].getStringValue("checkeduserid"));
				sql.putFieldValue("username", xyMoneyVOs[i].getStringValue("name"));
				sql.putFieldValue("logid", logid);
				sql.putFieldValue("formtype", "����֧������");
				sql.putFieldValue("busitype", "����֧��");
				sql.putFieldValue("calc", "+");
				sql.putFieldValue("money", delay);
				sql.putFieldValue("descr", descr.toString());
				sql.putFieldValue("datadate", currdate);
				sql.putFieldValue("createtime", currtime);
				sql.putFieldValue("createuser", "ϵͳ����");
				sql.putFieldValue("isdeleted", "N");
				list.add(sql);
			}
			if (list.size() > 0) {
				list.add(0, "delete from sal_person_fund_account where logid = " + logid);
				dmo.executeBatchByDS(null, list);
			}
			removeRemoteActionSchedule(logid, "ָ�����");
		} catch (Exception e) {
			removeRemoteActionSchedule(logid, "ָ�����");
			throw e;
		}
	}

	/*
	 * ����Ч�湤�ʣ�����ǰ��Ҫ��ִ�ж���ָ��ȡֵ
	 */
	public synchronized void calc_P_Pay(HashVO planVO, String _calbatch) throws Exception {
		Object currSchedule = getRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
		if (currSchedule != null) {
			StringBuffer strsb = new StringBuffer("��������[" + planVO.getStringValue("checkdate") + "]�ƻ����ڼ���Ч�湤��...\r\n");
			if (currSchedule != null) {
				Object[] obj = (Object[]) currSchedule;
				String schedule = (String) obj[0];
				strsb.append(schedule);
			}
			throw new Exception(strsb.toString());
		}
		try {
			StringBuffer hvoStr = new StringBuffer(
					"select t1.*,t2.postid,t4.deptid,t4.deptid maindeptid,t4.tellerno , t4.name ,t2.weights gweights,t4.stationratio,t4.stationkind,t3.valuetype,t3.operationtype from sal_person_check_score t1 left join sal_person_check_post t2  on  t1.groupid = t2.id    left join sal_person_check_list t3 on  t3.id = t2.targetid left join v_sal_personinfo t4 on t4.id = t1.checkeduser  where (t3.valuetype='����' or t3.valuetype='���ڼ���' or t3.valuetype='�����ڼ���') and  t1.targettype='Ա������ָ��' and t1.logid='"
							+ planVO.getStringValue("id") + "' ");
			if (tbutil.isEmpty(_calbatch)) {
				hvoStr.append(" and (t3.calbatch is null or t3.calbatch='') ");
			} else {
				hvoStr.append(" and t3.calbatch=" + _calbatch);
			}
			HashVO[] hvoperson = stbutil.getHashVoArrayByDS(null, hvoStr.toString());

			calc_comm_p_Pay(hvoperson, planVO.getStringValue("id"), planVO.getStringValue("checkdate"), false);
		} catch (Exception ex) {
			removeRemoteActionSchedule(planVO.getStringValue("id"), "ָ�����");
			throw ex;
		}
	}

	/*
	 * �������鹤��
	 */
	public void onCalcQQMoney(HashVO _planVO) throws Exception {
		String planid = _planVO.getStringValue("id");
		try {
			boolean calcQQ = tbutil.getSysOptionBooleanValue("�Ƿ�������鹤��", true); // Ĭ�ϼ������鹤�ʡ�������������鹤�ʣ��ܷ�����������Ҫ��ġ�
			CommDMO dmo = new CommDMO();
			HashVO users[] = stbutil.getHashVoArrayByDS(null,
					"select t2.id,t2.name,t2.tellerno,t2.maindeptid,t2.deptid,t4.name deptname,t4.id deptid,t3.checkdate,t2.id checkeduser,t1.logid from sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid = t2.id  left join sal_target_check_log t3 on t3.id = t1.logid left join pub_corp_dept t4 on t4.id = t2.deptid where t1.targettype='Ա������ָ��' and t1.logid='"
							+ _planVO.getStringValue("id") + "' order by t2.deptseq,t2.postseq");

			// if (users.length == 0) {
			// throw new Exception("�������鹤��,�����ȼ���Ա�����Կ��˽��.");
			// }
			List sqlList = new ArrayList();
			String deleteSql = "delete from sal_person_check_result where targettype='Ա�����˷���' and logid=" + _planVO.getStringValue("id");
			sqlList.add(deleteSql);
			for (int i = 0; i < users.length; i++) {
				Object value = formulaUtil.onExecute(formulaUtil.getFoctorHashVO("���˿��˷���"), users[i], new StringBuffer());
				if (value == null) {
					value = 0;
				} else {
					setCurrPlanSchedule(planid, "����Ա�����˷���" + (i + 1) + "/" + users.length);
				}
				InsertSQLBuilder insert = new InsertSQLBuilder("sal_person_check_result");
				insert.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_RESULT"));
				insert.putFieldValue("checkeduserid", users[i].getStringValue("id"));
				insert.putFieldValue("finalres", String.valueOf(value)); // ���յ÷�
				insert.putFieldValue("targettype", "Ա�����˷���"); // ���յ÷�
				insert.putFieldValue("logid", users[i].getStringValue("logid")); // ���յ÷�

				insert.putFieldValue("checkeduserdeptname", users[i].getStringValue("deptname"));
				insert.putFieldValue("checkeduserdeptid", users[i].getStringValue("deptid"));
				insert.putFieldValue("checkedusername", users[i].getStringValue("name"));

				sqlList.add(insert);
			}
			dmo.executeBatchByDS(null, sqlList);
			// ���˵÷��Ѿ�������ϣ�Ȼ��ʼ����Ƚϡ�

			HashVO[] hvo = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('��������','��������') and id not like '$%' order by seq");

			HashVO[] kindVOs = dmo.getHashVoArrayByDS(null, "select *from sal_person_check_type");// ���˶������
			HashVO[] scoreVO = dmo.getHashVoArrayByDS(null, "select t1.*,t2.tellerno,t2.id checkeduser ,t2.stationkind,t4.corptype from  sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid = t2.id left join pub_corp_dept t4 on t4.id = t2.maindeptid  where t2.stationkind!='����' and  t1.targettype='Ա�����˷���' and t1.logid=" + _planVO.getStringValue("id"));

			HashMap<String, String> findDeptSelfDept = new HashMap<String, String>(); // ��¼���ŵı����ţ��Ӳ������ҡ�
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
			for (int i = 0; i < scoreVO.length; i++) {
				String userdepttype = scoreVO[i].getStringValue("corptype");
				userdepttype = findDeptSelfDept.get(userdepttype);// תһ��
				if (sameDeptTypeUserScores.containsKey(userdepttype)) {
					List list = sameDeptTypeUserScores.get(userdepttype);
					list.add(scoreVO[i]);
				} else {
					List list = new ArrayList();
					list.add(scoreVO[i]);
					sameDeptTypeUserScores.put(userdepttype, list);
				}
			}
			HashMap usermoney = new HashMap();
			List updateSqlList = new ArrayList();
			if (calcQQ) {
				setCurrPlanSchedule(planid, "�������鹤��...");
			}
			HashVO factorVO = formulaUtil.getFoctorHashVO("���鹤�ʼ���");//
			HashVO factorVO_2 = formulaUtil.getFoctorHashVO("Ա������������");//
			for (Iterator iterator = sameDeptTypeUserScores.entrySet().iterator(); iterator.hasNext();) {
				Entry object = (Entry) iterator.next();
				String depttype = (String) object.getKey();
				List depttypeUserScores = (List) object.getValue();
				for (int i = 0; i < kindVOs.length; i++) {
					HashVO kindvo = kindVOs[i];
					String kinds = kindvo.getStringValue("stationkinds"); // ��ȡÿһ�������Ӧ��
					// ��λ��
					List list = new ArrayList();
					for (int j = 0; j < depttypeUserScores.size(); j++) {
						HashVO score = (HashVO) depttypeUserScores.get(j);
						String score_kind = score.getStringValue("stationkind");
						if (score != null && kinds.contains(score_kind)) { // ����С�
							list.add(score);
						}
					}
					// �������Ⱥ��������
					HashVO[] kind_ScoreVOs = (HashVO[]) list.toArray(new HashVO[0]);
					TBUtil.getTBUtil().sortHashVOs(kind_ScoreVOs, new String[][] { { "finalres", "Y", "Y" } });
					int num = kind_ScoreVOs.length; // ������
					for (int j = 0; j < kind_ScoreVOs.length; j++) {
						int money = 0;
						kind_ScoreVOs[j].setAttributeValue("����", new BigDecimal(j + 1).divide(new BigDecimal(num), 2, BigDecimal.ROUND_HALF_UP).floatValue()); // ����
						UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_person_check_result");
						if (calcQQ) {
							money = new BigDecimal(String.valueOf(formulaUtil.onExecute(factorVO, kind_ScoreVOs[j], new StringBuffer()))).intValue();
							usermoney.put(kind_ScoreVOs[j].getStringValue("checkeduserid"), money);
							sql.putFieldValue("money", money);
						}
						StringBuffer descr = new StringBuffer();
						if (factorVO_2 != null) {
							String yl = String.valueOf(formulaUtil.onExecute(factorVO_2, kind_ScoreVOs[j], new StringBuffer())); // �����ж�
							if (yl != null && !yl.equals("")) {
								descr.append("��" + yl + "�� ");
							}
							sql.putFieldValue("finalres2", yl);
						}
						descr.append("�ڡ�" + depttype + "���ġ�" + kindvo.getStringValue("name") + "����������" + (j + 1) + "/" + num);
						sql.putFieldValue("descr", descr.toString());
						sql.setWhereCondition(" id = " + kind_ScoreVOs[j].getStringValue("id"));
						updateSqlList.add(sql);
					}
				}
			}
			dmo.executeBatchByDS(null, updateSqlList, true, false);
			removeRemoteActionSchedule(planid, "ָ�����");
		} catch (Exception ex) {
			removeRemoteActionSchedule(planid, "ָ�����");
			throw ex;
		}
	}

	private synchronized void setCurrPlanSchedule(String planid, String _msg) {
		setRemoteActionSchedule(planid, "ָ�����", _msg);
	}

	public static synchronized void setRemoteActionSchedule(String _key, String _billType, String _msg) {
		Object[] obj;
		String ckey = _key + "$" + _billType;
		if (calcSchedule.containsKey(ckey)) {
			obj = (Object[]) calcSchedule.get(ckey);
			obj[0] = _msg;
		} else {
			obj = new Object[] { _msg };
		}
		calcSchedule.put(ckey, obj);
	}

	public static synchronized void removeRemoteActionSchedule(String _key, String _billType) {
		calcSchedule.remove(_key + "$" + _billType);
	}

	public HashMap<String, Float> getDeptTypeMaxScore(String logid) throws Exception {
		CommDMO dmo = new CommDMO();
		HashVO[] hvo = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('��������','��������') and id not like '$%' order by seq");
		HashMap<String, String> findDeptSelfDept = new HashMap<String, String>(); // ��¼���ŵı����ţ��Ӳ������ҡ�
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
		HashMap<String, String> deptType = dmo.getHashMapBySQLByDS(null, "select id,corptype from pub_corp_dept");
		HashVO[] deptLastScore = dmo.getHashVoArrayByDS(null, "select checkeddeptid, currscore+revisescore num from sal_target_check_revise_result where logid = " + logid); // ���в��ŵ����յ÷�
		HashMap<String, Float> depttype_AllScores = new HashMap<String, Float>(); // �õ�ĳ�����������µ���߷�
		for (int i = 0; i < deptLastScore.length; i++) {
			String deptid = deptLastScore[i].getStringValue("checkeddeptid");// ����ID
			String depttype = deptType.get(deptid); // �õ���������
			if (findDeptSelfDept.containsKey(depttype)) {
				depttype = findDeptSelfDept.get(depttype); // �ҵ����׵�
			}
			if (depttype_AllScores.containsKey(depttype)) {
				Float maxScore = depttype_AllScores.get(depttype);
				float currDeptScore = deptLastScore[i].getBigDecimalValue("num").floatValue();
				if (currDeptScore > maxScore) {
					depttype_AllScores.put(depttype, currDeptScore);
				}
			} else {
				float currDeptScore = deptLastScore[i].getBigDecimalValue("num").floatValue();
				depttype_AllScores.put(depttype, currDeptScore);
			}
		}
		for (Iterator iterator = findDeptSelfDept.entrySet().iterator(); iterator.hasNext();) {
			Entry depttypeAndParent = (Entry) iterator.next(); // �������ͣ��͸��׻���
			String depttype = (String) depttypeAndParent.getKey();
			String deptparentType = (String) depttypeAndParent.getValue();
			if (!depttype_AllScores.containsKey(depttype)) {
				if (depttype_AllScores.containsKey(deptparentType)) {
					float parentMaxScore = depttype_AllScores.get(deptparentType);
					depttype_AllScores.put(depttype, parentMaxScore);
				}
			}
		}
		return depttype_AllScores;
	}

	/*
	 * ��ȡԶ�̵��õ�ǰ��ʾ����
	 */
	public static synchronized Object getRemoteActionSchedule(String _key, String _billType) {
		return calcSchedule.get(_key + "$" + _billType);
	}

	// ��Աҵ�����ƽ��ֵ
	public float getGYTellerProfessionAVG(String _checkdate) throws Exception {
		return getGYTellerProfessionAVG(_checkdate, "��Աʵ��ҵ�����", "��Ա");
	}

	// ��Աҵ�����ƽ��ֵ
	public float getGYTellerProfessionAVG(String _checkdate, String factorName, String stationKind) throws Exception {
		return getGYTellerProfessionAVG(_checkdate, factorName, stationKind, null);
	}

	// ��Աҵ�����ƽ��ֵ
	public float getGYTellerProfessionAVG(String _checkdate, String factorName, String stationKind, String _mainDetpID) throws Exception {
		StringBuffer sql = new StringBuffer("select * from v_sal_personinfo where (isuncheck ='N' or isuncheck is null) ");
		if (tbutil.isEmpty(stationKind)) {
			sql.append(" and stationkind ='" + stationKind + "'");
		}
		if (tbutil.isEmpty(_mainDetpID)) {
			sql.append(" and maindeptid ='" + _mainDetpID + "'");
		}
		HashVO vos[] = stbutil.getHashVoArrayByDS(null, sql.toString());
		int totle = 0;
		int length = 0;
		for (int i = 0; i < vos.length; i++) {
			HashVO factorVO = formulaUtil.getFoctorHashVO(factorName);
			HashVO baseVO = vos[i];
			baseVO.setAttributeValue("checkdate", _checkdate);
			baseVO.setAttributeValue("checkeduser", baseVO.getStringValue("id"));
			Object obj = formulaUtil.onExecute(factorVO, baseVO, new StringBuffer());
			if (obj != null) {
				BigDecimal decimal = new BigDecimal(String.valueOf(obj));
				totle += decimal.intValue();
				length++;
			}
		}
		if (length == 0) { // ���û�й�Ա����
			return 0;
		}
		return (int) (totle / vos.length);
	}

	/*
	 * ĳ��λ����˾����ڴ�����
	 */
	public HashMap<String, Double> getPostAvgDeposit(HashVO _hvo) throws Exception {
		CommDMO dmo = new CommDMO();
		String targetid = _hvo.getStringValue("targetid"); //
		String checkdate = _hvo.getStringValue("checkdate");
		HashVO targetAndPost[] = dmo.getHashVoArrayByDS(null, "select groupid,postid from v_sal_postgroup_dl_target where id=" + targetid); // ָ���Ӧ�ĸ�λ��
		HashVO excelAllRows[] = dmo.getHashVoArrayByDS(null, "select B,E from excel_tab_2 where concat(year,'-',month)='" + checkdate + "'");
		HashMap<String, Double> userAndMoneyMap = new HashMap<String, Double>(); // Ա�����������
		for (int i = 0; i < excelAllRows.length; i++) {
			String username = excelAllRows[i].getStringValue("B"); // ��Ա����
			if (username == null) {
				continue;
			}
			String money = excelAllRows[i].getStringValue("E", "0"); // ��Ա����
			double currv = 0;
			try {
				currv = Double.parseDouble(money);
			} catch (Exception ex) {
				// ������ǰ���С�
				WLTLogger.getLogger(PersonStyleReportWKPanel.class).error("");
			}
			double value = 0;
			if (userAndMoneyMap.containsKey(username)) {
				value = (Double) userAndMoneyMap.get(username);
			}
			value += currv;
			userAndMoneyMap.put(username, value);
		}
		// �õ���ָ��Ķ����
		HashMap<String, Double> groupAndAvgMoney = new HashMap<String, Double>(); // ÿ�����ƽ��������
		for (int i = 0; i < targetAndPost.length; i++) { // Ӧ��û����������������ִ�в�ѯ
			String postids = targetAndPost[i].getStringValue("postid");
			String groupid = targetAndPost[i].getStringValue("groupid");
			HashVO users[] = dmo.getHashVoArrayByDS(null, "select name from v_sal_personinfo where stationkind in(" + TBUtil.getTBUtil().getInCondition(postids) + ")");
			if (users.length == 0) {
				continue;
			}
			double allMoney = 0;
			for (int j = 0; j < users.length; j++) {
				String username = users[j].getStringValue("name");
				if (userAndMoneyMap.containsKey(username)) {
					double theUserMoney = userAndMoneyMap.get(username);
					allMoney += theUserMoney;
				}
			}
			double avg = new BigDecimal(allMoney / users.length).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); // �ø�λ����˾�������
			groupAndAvgMoney.put(groupid, avg);
		}
		return groupAndAvgMoney;
	}

	/**
	 * ĳ��λ�������ƽ��ֵ
	 *
	 * @param _hvo
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Double> getPostAvg_LC(HashVO _hvo) throws Exception {
		CommDMO dmo = new CommDMO();
		String targetid = _hvo.getStringValue("targetid"); //
		HashVO targetAndPost[] = dmo.getHashVoArrayByDS(null, "select groupid,postid from v_sal_postgroup_dl_target where id=" + targetid); // ָ���Ӧ�ĸ�λ��
		HashVO foctor = formulaUtil.getFoctorHashVO("����������");
		HashVO excelAllRows[] = (HashVO[]) formulaUtil.onExecute(foctor, _hvo, new StringBuffer());
		HashMap<String, HashVO> userAndMoneyMap = new HashMap<String, HashVO>(); // Ա�����������
		for (int i = 0; i < excelAllRows.length; i++) {
			String username = excelAllRows[i].getStringValue("B"); // ��Ա����
			if (username == null) {
				continue;
			}
			userAndMoneyMap.put(username, excelAllRows[i]);
		}
		// �õ���ָ��Ķ����
		HashMap<String, Double> groupAndAvgMoney = new HashMap<String, Double>(); // ÿ�����ƽ��������
		HashVO factor_calc = formulaUtil.getFoctorHashVO("�����������");
		HashVO factor_lc_warn = formulaUtil.getFoctorHashVO("���澯��ֵ");

		for (int i = 0; i < targetAndPost.length; i++) { // Ӧ��û����������������ִ�в�ѯ
			String postids = targetAndPost[i].getStringValue("postid");
			String groupid = targetAndPost[i].getStringValue("groupid");
			HashVO users[] = dmo.getHashVoArrayByDS(null, "select name,stationkind from v_sal_personinfo where stationkind in(" + TBUtil.getTBUtil().getInCondition(postids) + ")");
			if (users.length == 0) {
				continue;
			}
			double allMoney = 0;
			int over_user = 0;// ����������
			for (int j = 0; j < users.length; j++) {
				String username = users[j].getStringValue("name");
				if (userAndMoneyMap.containsKey(username)) {
					// �����������
					HashVO uservo = userAndMoneyMap.get(username);
					uservo.setAttributeValue("stationkind", users[j].getStringValue("stationkind"));
					double theUserMoney = (Double) formulaUtil.onExecute(factor_calc, uservo, new StringBuffer());
					Object obj = formulaUtil.onExecute(factor_lc_warn, uservo, new StringBuffer());
					if (obj != null) {
						double warnvalue = Double.parseDouble(String.valueOf(obj));
						if (warnvalue < theUserMoney && warnvalue > 0) {
							over_user++; // �������Ԥ��ֵ��ֱ��������
							continue;
						}
					}
					allMoney += theUserMoney;
				}
			}
			double avg = new BigDecimal(allMoney / (users.length - over_user)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); // �ø�λ����˾�������
			groupAndAvgMoney.put(groupid, avg);
		}
		return groupAndAvgMoney;
	}

	/*
	 * ����һ��ָ���Ч�湤��
	 */
	public HashVO[] calcOnePersonTarget_P_Money(String _targetID, String _checkdate) throws Exception {
		try {
			CommDMO dmo = new CommDMO();
			TBUtil tbutil = new TBUtil();
			HashVO target[] = dmo.getHashVoArrayByDS(null, "select * from sal_person_check_list where id=" + _targetID); // �ҵ���ָ��

			if (target == null || target.length == 0) {
				throw new Exception("ָ�괫������!");
			}
			String valuetype = target[0].getStringValue("valuetype");
			boolean zjCalc = true; // ���ָ���Ƿ�ʽ�Ѿ�ֱ�Ӽ���Ч�湤�ʡ�
			if ("����".equals(valuetype) || "���ڼ���".equals(valuetype) || "�����ڼ���".equals(valuetype)) {
				zjCalc = false;
			}
			Boolean wgflg=tbutil.getTBUtil().getSysOptionBooleanValue("�Ƿ���������ָ�����ģʽ",false);
			HashVO target_post_vos[];
			HashVO [] vos=dmo.getHashVoArrayByDS(null,"select * from SAL_TARGET_CATALOG where id='"+target[0].getStringValue("catalogid")+"'");
			if(wgflg && vos[0].getStringValue("name").equals("����ָ��")){
				target_post_vos= dmo.getHashVoArrayByDS(null, "select * from sal_person_check_post_wg where targetid=" + _targetID); // �ҵ���ָ��ı����˶���
			}else{
				target_post_vos= dmo.getHashVoArrayByDS(null, "select * from sal_person_check_post where targetid=" + _targetID); // �ҵ���ָ��ı����˶���
			}
			HashMap<String, HashVO> post_targetMap = new HashMap<String, HashVO>(); // ��λ�������ƺ�sal_person_check_post��Ӧ��ϵ��
			List posttypes = new ArrayList();
			for (int i = 0; i < target_post_vos.length; i++) { // �õ����б����˵��ˡ�
				String postids = target_post_vos[i].getStringValue("postid"); // �õ������˸�λ�������
				String posts[] = postids.split(";"); // ���еĸ�λ������
				for (int j = 0; j < posts.length; j++) {
					if (posts[j] != null && !posts[j].trim().equals("")) {
						posttypes.add(posts[j].trim());
						post_targetMap.put(posts[j].trim(), target_post_vos[i]);
					}
				}
			}
			HashVO allCheckedUsers[];
			if(wgflg && vos[0].getStringValue("name").equals("����ָ��")){
//				allCheckedUsers = dmo.getHashVoArrayByDS(null, "select exc.ID wgid,exc.YEAR,exc.MONTH,exc.CREATTIME,exc.A,exc.B,exc.C,exc.D,exc.E,exc.F,exc.G,sal.id userid,sal.NAME,sal.SEX,sal.BIRTHDAY,sal.TELLERNO,sal.CARDID,sal.POSITION,sal.STATIONDATE,sal.STATIONRATIO,sal.AGE,sal.DEGREE,sal.UNIVERSITY,sal.SPECIALITIES,sal.POSTTITLE,sal.POSTTITLEAPPLYDATE,sal.POLITICALSTATUS,sal.CONTRACTDATE,sal.JOINWORKDATE,sal.JOINSELFBANKDATE,sal.WORKAGE,sal.SELFBANKAGE,sal.ONLYCHILDRENBTHDAY,sal.SELFBANKACCOUNT,sal.OTHERACCOUNT,sal.FAMILYACCOUNT,sal.PENSION,sal.HOUSINGFUND,sal.PLANWAY,sal.PLANRATIO,sal.ISUNCHECK,sal.FAMILYNAME,sal.MEDICARE,sal.TEMPORARY,sal.OTHERGLOD,sal.TECHNOLOGY,sal.STATIONKIND,sal.MAINDEPTID,sal.DEPTID,sal.DEPTNAME,sal.MAINSTATIONID,sal.MAINSTATION,sal.POSTSEQ,sal.DEPTSEQ,sal.LINKCODE,sal.DEPTCODE from EXCEL_TAB_85 exc left join v_sal_personinfo sal on exc.g=sal.code where exc.id in (" + tbutil.getInCondition(posttypes) + ")"); // [2020-5-11]�ҵ�������Ҫ���˵�����
				allCheckedUsers = dmo.getHashVoArrayByDS(null, "select exc.ID wgid,exc.YEAR,exc.MONTH,exc.CREATTIME,exc.A,exc.B,exc.C,exc.D,exc.E,exc.F,exc.G,sal.id userid,sal.NAME,sal.SEX,sal.BIRTHDAY,sal.TELLERNO,sal.CARDID,sal.POSITION,sal.STATIONDATE,sal.STATIONRATIO,sal.AGE,sal.DEGREE,sal.UNIVERSITY,sal.SPECIALITIES,sal.POSTTITLE,sal.POSTTITLEAPPLYDATE,sal.POLITICALSTATUS,sal.CONTRACTDATE,sal.JOINWORKDATE,sal.JOINSELFBANKDATE,sal.WORKAGE,sal.SELFBANKAGE,sal.ONLYCHILDRENBTHDAY,sal.SELFBANKACCOUNT,sal.OTHERACCOUNT,sal.FAMILYACCOUNT,sal.PENSION,sal.HOUSINGFUND,sal.PLANWAY,sal.PLANRATIO,sal.ISUNCHECK,sal.FAMILYNAME,sal.MEDICARE,sal.TEMPORARY,sal.OTHERGLOD,sal.TECHNOLOGY,sal.STATIONKIND,sal.MAINDEPTID,sal.DEPTID,sal.DEPTNAME,sal.MAINSTATIONID,sal.MAINSTATION,sal.POSTSEQ,sal.DEPTSEQ,sal.LINKCODE,sal.DEPTCODE from v_wg_info    exc left join v_sal_personinfo sal on exc.g=sal.code where exc.id in (" + tbutil.getInCondition(posttypes) + ")");// ZPY ��2020-07-07������������Ҫ�����޸�
			}else{
				allCheckedUsers = dmo.getHashVoArrayByDS(null, "select t1.*,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.maindeptid = t2.id where  ( isuncheck ='N' or isuncheck is null)   and  stationkind in (" + tbutil.getInCondition(posttypes) + ")"); // �ҵ��ø�λ��������������
			}

			// ����������ٵ�score���е����ݡ����Բ�������score���ݡ����Ǳ����б�Ҫ���ϴ�excel���ݡ�

			HashVO scorevo[] = new HashVO[allCheckedUsers.length]; // ÿ��һ��
			for (int i = 0; i < allCheckedUsers.length; i++) {
				if(wgflg && vos[0].getStringValue("name").equals("����ָ��")){
					scorevo[i] = new HashVO();
					scorevo[i].setAttributeValue("wgid", allCheckedUsers[i].getStringValue("wgid"));//
					scorevo[i].setAttributeValue("targetid", _targetID);
					scorevo[i].setAttributeValue("checkeduser", allCheckedUsers[i].getStringValue("id"));
					scorevo[i].setAttributeValue("getvalue", target[0].getStringValue("getvalue"));
					scorevo[i].setAttributeValue("partvalue", target[0].getStringValue("partvalue"));
					scorevo[i].setAttributeValue("targetdefine", target[0].getStringValue("define"));
					scorevo[i].setAttributeValue("operationtype", target[0].getStringValue("operationtype"));
					scorevo[i].setAttributeValue("valuetype", target[0].getStringValue("valuetype"));
					scorevo[i].setAttributeValue("targetname", target[0].getStringValue("name"));
					scorevo[i].setAttributeValue("code", target[0].getStringValue("code"));
					scorevo[i].setAttributeValue("shortname", allCheckedUsers[i].getStringValue("A"));
					scorevo[i].setAttributeValue("checkedusername", allCheckedUsers[i].getStringValue("B"));
					scorevo[i].setAttributeValue("factors", target[0].getStringValue("factors")); //����������

					String weights = "";
					String planvalue = "";
					String groupid = "";
					String postids = "";
					String dw = target[0].getStringValue("unitvalue");// ��λ
					if (post_targetMap.containsKey(allCheckedUsers[i].getStringValue("stationkind"))) {
						HashVO target_post = post_targetMap.get(allCheckedUsers[i].getStringValue("stationkind"));
						if (target_post != null) {
							weights = target_post.getStringValue("weights");
							planvalue = target_post.getStringValue("planedvalue");
							groupid = target_post.getStringValue("id");
							postids = target_post.getStringValue("postid");
						}
					}

					scorevo[i].setAttributeValue("weights", weights);

					scorevo[i].setAttributeValue("planedvalue", planvalue);
					scorevo[i].setAttributeValue("groupid", groupid);
					scorevo[i].setAttributeValue("checkdate", _checkdate);
					scorevo[i].setAttributeValue("unitvalue", dw);
					scorevo[i].setAttributeValue("maindeptid", allCheckedUsers[i].getStringValue("maindeptid"));
					scorevo[i].setAttributeValue("deptid", allCheckedUsers[i].getStringValue("maindeptid"));
					scorevo[i].setAttributeValue("checkeddept", allCheckedUsers[i].getStringValue("maindeptid"));
					scorevo[i].setAttributeValue("postid", postids);
					scorevo[i].setAttributeValue("tellerno", allCheckedUsers[i].getStringValue("tellerno"));
					scorevo[i].setAttributeValue("name", allCheckedUsers[i].getStringValue("name"));
					scorevo[i].setAttributeValue("stationratio", allCheckedUsers[i].getStringValue("stationratio"));// ��λϵ��
					scorevo[i].setAttributeValue("stationkind", allCheckedUsers[i].getStringValue("C")+allCheckedUsers[i].getStringValue("D"));

				}else{
					scorevo[i] = new HashVO();
					scorevo[i].setAttributeValue("id", -i);//
					scorevo[i].setAttributeValue("targetid", _targetID);
					scorevo[i].setAttributeValue("checkeduser", allCheckedUsers[i].getStringValue("id"));
					scorevo[i].setAttributeValue("getvalue", target[0].getStringValue("getvalue"));
					scorevo[i].setAttributeValue("partvalue", target[0].getStringValue("partvalue"));
					scorevo[i].setAttributeValue("targetdefine", target[0].getStringValue("define"));
					scorevo[i].setAttributeValue("operationtype", target[0].getStringValue("operationtype"));
					scorevo[i].setAttributeValue("valuetype", target[0].getStringValue("valuetype"));
					scorevo[i].setAttributeValue("targetname", target[0].getStringValue("name"));
					scorevo[i].setAttributeValue("code", target[0].getStringValue("code"));
					scorevo[i].setAttributeValue("shortname", allCheckedUsers[i].getStringValue("shortname"));
					scorevo[i].setAttributeValue("checkedusername", allCheckedUsers[i].getStringValue("name"));
					scorevo[i].setAttributeValue("factors", target[0].getStringValue("factors")); //����������

					String weights = "";
					String planvalue = "";
					String groupid = "";
					String postids = "";
					String dw = target[0].getStringValue("unitvalue");// ��λ
					if (post_targetMap.containsKey(allCheckedUsers[i].getStringValue("stationkind"))) {
						HashVO target_post = post_targetMap.get(allCheckedUsers[i].getStringValue("stationkind"));
						if (target_post != null) {
							weights = target_post.getStringValue("weights");
							planvalue = target_post.getStringValue("planedvalue");
							groupid = target_post.getStringValue("id");
							postids = target_post.getStringValue("postid");
						}
					}

					scorevo[i].setAttributeValue("weights", weights);

					scorevo[i].setAttributeValue("planedvalue", planvalue);
					scorevo[i].setAttributeValue("groupid", groupid);
					scorevo[i].setAttributeValue("checkdate", _checkdate);
					scorevo[i].setAttributeValue("unitvalue", dw);
					scorevo[i].setAttributeValue("maindeptid", allCheckedUsers[i].getStringValue("maindeptid"));
					scorevo[i].setAttributeValue("deptid", allCheckedUsers[i].getStringValue("maindeptid"));
					scorevo[i].setAttributeValue("checkeddept", allCheckedUsers[i].getStringValue("maindeptid"));
					scorevo[i].setAttributeValue("postid", postids);
					scorevo[i].setAttributeValue("tellerno", allCheckedUsers[i].getStringValue("tellerno"));
					scorevo[i].setAttributeValue("name", allCheckedUsers[i].getStringValue("name"));
					scorevo[i].setAttributeValue("stationratio", allCheckedUsers[i].getStringValue("stationratio"));// ��λϵ��
					scorevo[i].setAttributeValue("stationkind", allCheckedUsers[i].getStringValue("stationkind"));

				}
			}

			// ��ʼ����ָ���ʵ��ֵ
			int personNum = scorevo.length;
			for (int i = 0; i < scorevo.length; i++) {
				HashVO scoreVO = scorevo[i];
				String targetName = scoreVO.getStringValue("targetname"); // �õ�ָ������
				String targetFactorName = targetName + "����";
				String targetdef = scoreVO.getStringValue("targetdefine"); // ��ʽ����
				String getvalue = scoreVO.getStringValue("getvalue"); // �õ�ȡֵ�Ĺ�ʽ
				String partvalue = scoreVO.getStringValue("partvalue"); // �������ֵ��ʽ��
				String valueType = scoreVO.getStringValue("valuetype");
				String operationtype = scoreVO.getStringValue("operationtype"); // ���㷽ʽ
				String hisFactors = scoreVO.getStringValue("factors", ""); //������Ҫ��¼���������ӽ��
				String factors[] = tbutil.split(hisFactors, ";");
				LinkedHashMap<String, String> factor_value_map = new LinkedHashMap<String, String>(); //�洢���Ӽ���Ľ��

				// ,�Ӽ�
				StringBuffer sb = new StringBuffer();
				formulaUtil.putDefaultFactorVO("����", getvalue, targetFactorName, null, "4λС��");
				// scoreVO.setAttributeValue("", value)
				Object currvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetFactorName), scoreVO, sb); // �õ�ʵ��ֵ

				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors); //
				// System.out.println("ȡֵ����" + rtobj);
				Object money = 0;
				Object currpartvalue = null;
				if (!tbutil.isEmpty(partvalue)) {
					partvalue = stbutil.formulaReplaceX(partvalue, "x", "[" + targetFactorName + "]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[�ƻ�ֵ]", "[��������." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[Ŀ��ֵ]", "[��������." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[Ȩ��]", "[��������." + "weights]");
					formulaUtil.putDefaultFactorVO("����", partvalue, targetName + "�������ֵ", "", "2λС��");
					currpartvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "�������ֵ"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors); //
				} else {
					currpartvalue = currvalue;
					formulaUtil.putDefaultFactorVO("����", partvalue, targetName + "�������ֵ", "", "2λС��");
					formulaUtil.putDefaultFactorValue(targetName + "�������ֵ", currpartvalue);
				}

				if (("ֱ��".equals(valueType) || "��λϵ������".equals(valueType)) && targetdef != null && !targetdef.trim().equals("")) { // ͨ����ʽ�������Ч�湤��
					targetdef = stbutil.formulaReplaceX(targetdef, "x", "[" + targetName + "�������ֵ]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[�ƻ�ֵ]", "[��������." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[Ŀ��ֵ]", "[��������." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[Ȩ��]", "[��������." + "weights]");
					formulaUtil.putDefaultFactorVO("����", targetdef, targetName + "�÷ּ���", "", "2λС��");
					money = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "�÷ּ���"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors); //
				}
				// System.out.println(obj);
				if (currvalue == null || "".equals(String.valueOf(currvalue))) {
					currvalue = 0;
				}
				scoreVO.setAttributeValue("realvalue", String.valueOf(currvalue)); // ������ֵ
				if (currpartvalue == null || "".equals(String.valueOf(currpartvalue))) {
					currpartvalue = 0;
				}
				scoreVO.setAttributeValue("checkscore", String.valueOf(currpartvalue));
				if ("��".equals(operationtype)) {
					BigDecimal big = new BigDecimal(String.valueOf(money)).setScale(2, BigDecimal.ROUND_HALF_UP); //
					money = 0 - big.floatValue();
				}
				if (money != null) {
					scoreVO.setAttributeValue("money", String.valueOf(money));

					Iterator it = factor_value_map.entrySet().iterator();
					StringBuffer factorStr = new StringBuffer();
					while (it.hasNext()) {
						Entry entry = (Entry) it.next();
						String factorName = (String) entry.getKey();
						String factorValue = (String) entry.getValue();
						scoreVO.setAttributeValue(factorName, factorValue);
					}
					scoreVO.setAttributeValue("descr", sb.toString());
				}
				setRemoteActionSchedule("Ա������ָ������" + _targetID, "Ա������ָ������", "Ա������ָ�����:" + (i + 1) + "/" + (personNum));
			}
			removeRemoteActionSchedule("Ա������ָ������" + _targetID, "Ա������ָ������");
			if (zjCalc) {
				return scorevo;
			}
			return calc_comm_p_Pay(scorevo, "", _checkdate, true);
		} catch (Exception e) {
			removeRemoteActionSchedule("Ա������ָ������" + _targetID, "Ա������ָ������");
			throw e;
		}
	}

	/*
	 * ��Ա���Ķ��Կ���ָ��ÿ��ָ��÷ּ������������
	 */
	public void onePlanCalcAllUserEveryDXTargetScore(String logid) throws Exception {
		StringBuffer sb = new StringBuffer("select * from sal_person_check_score where logid=" + logid + " and targettype='Ա������ָ��' and scoretype='�ֶ����' ");
		HashMap user_level = getDmo().getHashMapBySQLByDS(null, "select id, stationratio from sal_personinfo where stationratio is not null");
		// ���뿼�˵��ˣ����и�λϵ��
		HashMap error = getDmo().getHashMapBySQLByDS(null, "select name, '1' from  v_sal_personinfo where ( isuncheck ='N' or isuncheck is null)  and (stationratio is null or stationratio='')");
		HashVO[] allscores = getDmo().getHashVoArrayByDS(null, sb.toString()); // ���д�ּ�¼
		List allscorers = new ArrayList(); // ���е�������
		// ָ��-��-�����˼�¼vo
		HashMap[] tempmap = getAllTempMap_Person(allscores); // ���з����map
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>> user_target_group_scoresList_Map = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>>) tempmap[0];

		Iterator it = user_target_group_scoresList_Map.entrySet().iterator();
		StringBuffer sbmsg = new StringBuffer();
		String erroruser = null;
		try {
			List sqllist = new ArrayList();
			int size = user_target_group_scoresList_Map.size();
			int index = 1;
			while (it.hasNext()) {
				Entry entry = (Entry) it.next();
				String scoreduserid = (String) entry.getKey();
				LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>> target_group_scorelist_map = (LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>) entry.getValue();
				// HashMap<String, String> target_weightstype = new
				// HashMap<String, String>(); // ָ��-���Ȩ�ط�ʽ

				List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
				List<BillCellItemVO> item0 = new ArrayList<BillCellItemVO>();
				// item0.add(getBillTitleCellItemVO("Ȩ��"));
				list.add(item0.toArray(new BillCellItemVO[0]));

				for (Iterator iterator = target_group_scorelist_map.entrySet().iterator(); iterator.hasNext();) {
					Entry entry2 = (Entry) iterator.next();
					String targetid = (String) entry2.getKey();
					HashMap<String, List<HashVO>> group_vos = (HashMap<String, List<HashVO>>) entry2.getValue();
					String[] grups = group_vos.keySet().toArray(new String[0]);
					BigDecimal groupsumweight = new BigDecimal("0");
					BigDecimal groupsumscore = new BigDecimal("0");
					String targetName = null;
					String userName = null;
					String targetWeights = null;
					String targettype = null;
					String checktype = null;
					for (Iterator iterator2 = group_vos.entrySet().iterator(); iterator2.hasNext();) {
						Entry entry3 = (Entry) iterator2.next();
						BigDecimal group_weight = null;
						List<HashVO> scorevos = (List<HashVO>) entry3.getValue();
						BigDecimal usersumweight = new BigDecimal("0");
						BigDecimal usersumscore = new BigDecimal("0");
						String targetweightstype = null;
						String scorerweightstype = null;
						for (int s = 0; s < scorevos.size(); s++) {
							if (targetName == null) {
								targetName = scorevos.get(s).getStringValue("targetname");
								userName = scorevos.get(s).getStringValue("checkedusername");
								erroruser = userName;
								targetWeights = scorevos.get(s).getStringValue("weights");
								targettype = scorevos.get(s).getStringValue("targettype");
								checktype = scorevos.get(s).getStringValue("checktype");
							}
							if (targetweightstype == null) {
								targetweightstype = scorevos.get(s).getStringValue("scorerweightstype");
								scorerweightstype = scorevos.get(s).getStringValue("groupweightstype");
								group_weight = new BigDecimal(scorevos.get(s).getStringValue("groupweights"));
							}
							if ("ƽ��".equals(scorerweightstype)) {
								usersumweight = usersumweight.add(new BigDecimal("1"));
								usersumscore = usersumscore.add(new BigDecimal(scorevos.get(s).getStringValue("checkscore", "0")));
							} else {
								usersumweight = usersumweight.add(new BigDecimal(user_level.get(scorevos.get(s).getStringValue("scoreuser")) + ""));
								usersumscore = usersumscore.add(new BigDecimal(scorevos.get(s).getStringValue("checkscore", "0")).multiply(new BigDecimal(user_level.get(scorevos.get(s).getStringValue("scoreuser")) + "")));
							}
						}
						if ("ƽ��".equals(targetweightstype)) {
							groupsumweight = groupsumweight.add(new BigDecimal("1"));
							groupsumscore = groupsumscore.add(usersumscore.divide(usersumweight, 6, BigDecimal.ROUND_HALF_UP));
						} else {
							groupsumweight = groupsumweight.add(group_weight);
							groupsumscore = groupsumscore.add(usersumscore.divide(usersumweight, 6, BigDecimal.ROUND_HALF_UP).multiply(group_weight));
						}
					}
					String targetvalue = groupsumscore.divide(groupsumweight, 2, BigDecimal.ROUND_HALF_UP).toString();
					InsertSQLBuilder insertSql = new InsertSQLBuilder("sal_person_check_target_score");
					String id = getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_TARGET_SCORE");
					insertSql.putFieldValue("id", id);
					insertSql.putFieldValue("targetid", targetid);
					insertSql.putFieldValue("targetname", targetName);
					insertSql.putFieldValue("checktype", checktype);
					insertSql.putFieldValue("targettype", targettype);
					insertSql.putFieldValue("checkeduser", scoreduserid);
					insertSql.putFieldValue("checkedusername", userName);
					insertSql.putFieldValue("checkscore", targetvalue);
					insertSql.putFieldValue("weights", targetWeights);
					insertSql.putFieldValue("logid", logid);
					sqllist.add(insertSql);
				}
				setRemoteActionSchedule(logid, "ָ�����", "���ڼ���Ա�������ָ��÷�" + index + "/" + size);
				index++;
			}
			sqllist.add(0, "delete from sal_person_check_target_score where logid = " + logid);
			getDmo().executeBatchByDS(null, sqllist);
			removeRemoteActionSchedule(logid, "ָ�����");
		} catch (Exception ex) {
			removeRemoteActionSchedule(logid, "ָ�����");
			throw ex;
		}
	}

	/*
	 * ���������ּ�¼���ձ������˷��飬Ȼ��ÿ���������˵�ĳ����¼������������з��࣬Ȼ���ٸ��������˽��з���
	 */
	public HashMap[] getAllTempMap_Person(HashVO[] allscores) throws Exception {
		HashMap[] maps = new HashMap[5];
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>> user_score = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>>();
		LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>> user_score_ggcc = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>>();
		LinkedHashMap<String, List<HashVO>> user_quantifyscore = new LinkedHashMap<String, List<HashVO>>();
		List allscorers = new ArrayList();
		String checkeduserid = null;
		String checkedusername = null;
		String groupid = null;
		String scorer = null;
		String groupweightstyle = null;
		String groupweight = null;
		String userweightstyle = null;
		if (allscores != null && allscores.length > 0) {
			List allcheckeduserids = new ArrayList();
			for (int i = 0; i < allscores.length; i++) {
				allcheckeduserids.add(allscores[i].getStringValue("checkeduser", ""));
			}
			for (int i = 0; i < allscores.length; i++) {
				checkeduserid = allscores[i].getStringValue("checkeduser", ""); // ��������
				String targetID = allscores[i].getStringValue("targetid");
				if ("Ա������ָ��".equals(allscores[i].getStringValue("targettype", ""))) { // ����ָ����Ҫһ���µ�hashmapֱ����userid-List
					if (user_quantifyscore.containsKey(checkeduserid)) {
						user_quantifyscore.get(checkeduserid).add(allscores[i]);
					} else {
						List<HashVO> scorers = new ArrayList<HashVO>();
						scorers.add(allscores[i]);
						user_quantifyscore.put(checkeduserid, scorers);
					}
				} else { // Ա������ָ�ꡢ�߹ܶ���ָ�ꡢ���Ŷ���ָ�� ����֮ǰ���߼�
					checkedusername = allscores[i].getStringValue("checkedusername", ""); // ��������
					if (checkedusername.equals("������")) {
						// System.out.println(">>");
					}
					groupid = allscores[i].getStringValue("scoreusertype", ""); // ������
					scorer = allscores[i].getStringValue("scoreuser", ""); // ������
					allscorers.add(scorer);
					groupweightstyle = allscores[i].getStringValue("groupweightstype", ""); // ������Ա��Ȩ�ؼ��㷽ʽ
					groupweight = allscores[i].getStringValue("groupweights", ""); // �������Ȩ��
					userweightstyle = allscores[i].getStringValue("scorerweightstype", ""); // ������֮���Ȩ�ط�ʽ
					if (!"".equals(checkeduserid) && !"".equals(groupid) && !"".equals(userweightstyle)) {
						LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>> target_group = new LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>();
						boolean have = false;
						if ("�߹ܴ�ֱ".equals(allscores[i].getStringValue("checktype"))) { // ��������Ǹ߹ܴ�ֱ��
							if (user_score_ggcc.containsKey(targetID)) {
								target_group = user_score_ggcc.get(checkeduserid);
								have = true;
							}
						} else {
							if (user_score.containsKey(checkeduserid)) {
								target_group = user_score.get(checkeduserid);
								have = true;
							}
						}
						if (have && target_group.containsKey(targetID)) {
							LinkedHashMap<String, List<HashVO>> group_scores = target_group.get(targetID);
							if (group_scores.containsKey(groupid)) {
								List<HashVO> scorers = group_scores.get(groupid);
								scorers.add(allscores[i]);
							} else {
								List<HashVO> scorers = new ArrayList<HashVO>();
								scorers.add(allscores[i]);
								group_scores.put(groupid, scorers);
							}
						} else {
							LinkedHashMap<String, List<HashVO>> group_scores = new LinkedHashMap<String, List<HashVO>>(); // һ�������˶Ա������˵����д�ֽ��
							List<HashVO> scorers = new ArrayList<HashVO>();
							scorers.add(allscores[i]);
							group_scores.put(groupid, scorers);
							target_group.put(targetID, group_scores); // sal_person_check_plan_scorer��ID��������
							if ("�߹ܴ�ֱ".equals(allscores[i].getStringValue("checktype"))) {
								user_score_ggcc.put(checkeduserid, target_group); // ��������
							} else {
								user_score.put(checkeduserid, target_group); // ��������
							}
						}
					}
				}
			}
		}
		HashMap user_level = getDmo().getHashMapBySQLByDS(null, "select id, stationratio from sal_personinfo where stationratio is not null and id in (" + tbutil.getInCondition(allscorers) + ")");
		HashMap erroruser = getDmo().getHashMapBySQLByDS(null, "select u.name, '1' from pub_user u left join sal_personinfo p on u.id=p.id where (p.stationratio is null or p.stationratio='') and u.id in (" + tbutil.getInCondition(allscorers) + ")");
		maps[0] = user_score;
		maps[1] = user_level;
		maps[2] = erroruser;
		maps[3] = user_quantifyscore; // Ӧ��û��
		maps[4] = user_score_ggcc; // Ŀǰû��
		return maps;
	}

	public Object[][] calcYearPersonCheckReport(String[] logid) throws Exception {
		return new YearPersonCheckReportAdpader().calcYearPersonCheckReport(logid);
	}

	public HashVO getWarnVoById(String _warnConfigid, String _logid) throws Exception {
		HashVO warnvos[] = getDmo().getHashVoArrayByDS(null, "select * from sal_target_warn where id = " + _warnConfigid);
		String checkdate = getDmo().getStringValueByDS(null, "select checkdate from sal_target_check_log  where id = " + _logid);
		if (warnvos == null && warnvos.length == 0) {
			return null;
		}
		HashVO warnvo = warnvos[0];
		String type = warnvo.getStringValue("type");
		if ("ϵͳָ��".equals(type)) {
			String targetid = warnvo.getStringValue("targetid");
			if (tbutil.isEmpty(targetid)) {
				throw new Exception("Ԥ�����������Ϊ[" + _warnConfigid + "]��������Ϣ��ָ���ֶ�ֵΪ��.");
			}
			HashVO targetscores[] = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where logid = " + _logid + " and targetid = " + targetid);
			if (targetscores == null || targetscores.length == 0) {
				return null;
			}
			String formulatype = warnvo.getStringValue("formulatype");
			float value = 0f;
			boolean isnull = true;
			if (targetscores == null || targetscores.length == 0) {
				warnvo.setAttributeValue("name", warnvo.getAttributeValue("name") + "(û����)");
				return getWarnVO(warnvo, 0, isnull);
			}
			for (int j = 0; j < targetscores.length; j++) {
				HashVO scorevo = (HashVO) targetscores[j];
				if (scorevo != null) {
					String currvalue = scorevo.getStringValue("currvalue");
					if (!TBUtil.isEmpty(currvalue)) {
						value += Float.parseFloat(currvalue);
						isnull = false;
					}
				}
			}
			if ("��ƽ��".equals(formulatype)) {
				value = value / targetscores.length;
				value = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			}
			return getWarnVO(warnvo, value, isnull);
		} else if ("�Զ���".equals(type)) {
			// �Զ������Ҫִ�й�ʽ��
			String formula = warnvo.getStringValue("formula"); //
			if (formula == null) {
				warnvo.setAttributeValue("name", warnvo.getAttributeValue("name") + "(δ���ù�ʽ)");
				return getWarnVO(warnvo, 0, true);
			} else {
				formulaUtil.putDefaultFactorVO("����", formula, "Ԥ��������", "", "2λС��");
				HashVO factorvo = formulaUtil.getFoctorHashVO("Ԥ��������");
				warnvo.setAttributeValue("checkdate", checkdate);
				warnvo.setAttributeValue("logid", _logid);
				Object obj = formulaUtil.onExecute(factorvo, warnvo, new StringBuffer());
				return getWarnVO(warnvo, Float.parseFloat(String.valueOf(obj)), false);
			}
		}
		return null;
	}

	private HashVO getWarnVO(HashVO warnvo, float _currvalue, boolean isnull) throws Exception {
		double w1 = warnvo.getDoubleValue("warn1");
		double w2 = warnvo.getDoubleValue("warn2");
		double w3 = warnvo.getDoubleValue("warn3");
		double w4 = warnvo.getDoubleValue("warn4");
		String name = warnvo.getStringValue("targetname");
		warnvo.setAttributeValue("����", name); // �ɹ��������漰ʱ��
		warnvo.setAttributeValue("X��", warnvo.getStringValue("unitvalue", "")); //
		if (!isnull) {
			warnvo.setAttributeValue("ʵ��ֵ", _currvalue); //	
		}
		if (_currvalue <= w1) {
			warnvo.setAttributeValue("��Сֵ", _currvalue); //
		} else {
			warnvo.setAttributeValue("��Сֵ", w1); //
		}
		warnvo.setAttributeValue("����ֵ", w2); //
		warnvo.setAttributeValue("����ֵ", w3); //
		warnvo.setAttributeValue("��ʾ", ""); //
		if (_currvalue >= w4) {
			warnvo.setAttributeValue("��ʾ", "ϵͳ���õ����ֵΪ" + "" + w4 + ",�ֱ�����.");
			warnvo.setAttributeValue("���ֵ", _currvalue); //	
		} else {
			warnvo.setAttributeValue("���ֵ", w4); //
		}
		warnvo.setAttributeValue("����ɫ", "FFBD9D");
		return warnvo;
	}

	public String calcOneDeptTargetDL(HashVO targetVO, String selectDate) throws Exception {
		String formulatext = targetVO.getStringValue("getvalue");
		String define = targetVO.getStringValue("define");
		String currdeptid = "";
		CommDMO dmo = new CommDMO();
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		String checkdeptid = targetVO.getStringValue("checkeddept");
		String weights = targetVO.getStringValue("weights");
		String targetType = targetVO.getStringValue("type");
		HashVO checkdeptvos[] = dmo.getHashVoArrayByDS(null, "select * from sal_target_checkeddept where id in(" + TBUtil.getTBUtil().getInCondition(checkdeptid) + ")");
		StringBuffer executeValueSB = new StringBuffer();
		String dw = "���żƼ�ָ��".equals(targetType) ? "н��" : "�÷�";
		for (int i = 0; i < checkdeptvos.length; i++) {
			String deptids = checkdeptvos[i].getStringValue("deptid");
			String planvalue = checkdeptvos[i].getStringValue("planedvalue");
			String deptarrays[] = TBUtil.getTBUtil().split(deptids, ";");
			for (int j = 0; j < deptarrays.length; j++) {
				String uid = deptarrays[j];
				String deptname = dmo.getStringValueByDS(null, "select name from pub_corp_Dept where id = '" + uid + "'");
				if (uid == null || uid.equals("") || tbutil.isEmpty(deptname)) {
					continue;
				}
				HashVO scorevo = new HashVO();
				scorevo.setAttributeValue("targetid", targetVO.getStringValue("id"));
				scorevo.setAttributeValue("targetname", targetVO.getStringValue("name"));
				scorevo.setAttributeValue("checkeddept", uid);
				scorevo.setAttributeValue("maindeptid", uid);
				scorevo.setAttributeValue("checkdradio", planvalue);
				scorevo.setAttributeValue("weights", weights);
				scorevo.setAttributeValue("checkdate", selectDate);
				scorevo.setAttributeValue("checkeddeptname", deptname);
				currdeptid = uid;
				StringBuffer sb = new StringBuffer();
				String targetName = targetVO.getStringValue("name");
				String targetFactorName = targetName + "����";
				util.putDefaultFactorVO("����", formulatext, targetFactorName, "", "4λС��");
				Object rtobj = util.onExecute(util.getFoctorHashVO(targetFactorName), scorevo, sb);
				define = define.replaceAll("x", "[" + targetFactorName + "]");
				define = TBUtil.getTBUtil().replaceAll(define, "[�ƻ�ֵ]", "[��������." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[Ŀ��ֵ]", "[��������." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[Ȩ��]", "[��������." + "weights]");
				util.putDefaultFactorVO("����", define, targetName + "�÷ּ���", "", "4λС��");
				// �Ѽƻ�ֵ�ĵ�
				scorevo.setAttributeValue("planedvalue", planvalue);
				scorevo.setAttributeValue("weights", weights);
				Object obj = util.onExecute(util.getFoctorHashVO(targetName + "�÷ּ���"), scorevo, sb);
				double value = 0;
				if (obj instanceof Number) {
					try {
						value = Double.parseDouble(String.valueOf(obj));
					} catch (Exception e) {
						throw e;
					}
				}

				executeValueSB.append("��ָ�꡾" + scorevo.getStringValue("checkeddeptname") + "��ʵ��ֵ���ֵ��" + rtobj + "��  " + "����" + dw + value + " \r\n" + sb + "\r\n");
			}
		}
		return executeValueSB.toString();
	}

	/**
	 *
	 * @param targetVO
	 * @param selectDate
	 * @return
	 * @throws Exception
	 * zzl[2020-5-14] ���ż���������HashVo[]
	 */
	public HashVO [] calcOneDeptTargetDL2(HashVO targetVO, String selectDate) throws Exception {
		HashVO [] vos;
		String formulatext = targetVO.getStringValue("getvalue");
		String define = targetVO.getStringValue("define");
		String currdeptid = "";
		CommDMO dmo = new CommDMO();
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		String checkdeptid = targetVO.getStringValue("checkeddept");
		String weights = targetVO.getStringValue("weights");
		String targetType = targetVO.getStringValue("type");
		HashVO checkdeptvos[] = dmo.getHashVoArrayByDS(null, "select * from sal_target_checkeddept where id in(" + TBUtil.getTBUtil().getInCondition(checkdeptid) + ")");
		StringBuffer executeValueSB = new StringBuffer();
		String dw = "���żƼ�ָ��".equals(targetType) ? "н��" : "�÷�";
		List <HashVO> list=new ArrayList<HashVO>();
		for (int i = 0; i < checkdeptvos.length; i++) {
			String deptids = checkdeptvos[i].getStringValue("deptid");
			String planvalue = checkdeptvos[i].getStringValue("planedvalue");
			String deptarrays[] = TBUtil.getTBUtil().split(deptids, ";");
			for (int j = 0; j < deptarrays.length; j++) {
				String uid = deptarrays[j];
				String deptname = dmo.getStringValueByDS(null, "select name from pub_corp_Dept where id = '" + uid + "'");
				if (uid == null || uid.equals("") || tbutil.isEmpty(deptname)) {
					continue;
				}
				HashVO scorevo = new HashVO();
				scorevo.setAttributeValue("targetid", targetVO.getStringValue("id"));
				scorevo.setAttributeValue("targetname", targetVO.getStringValue("name"));
				scorevo.setAttributeValue("checkeddept", uid);
				scorevo.setAttributeValue("maindeptid", uid);
				scorevo.setAttributeValue("checkdradio", planvalue);
				scorevo.setAttributeValue("weights", weights);
				scorevo.setAttributeValue("checkdate", selectDate);
				scorevo.setAttributeValue("checkeddeptname", deptname);
				currdeptid = uid;
				StringBuffer sb = new StringBuffer();
				String targetName = targetVO.getStringValue("name");
				String targetFactorName = targetName + "����";
				util.putDefaultFactorVO("����", formulatext, targetFactorName, "", "4λС��");
				Object rtobj = util.onExecute(util.getFoctorHashVO(targetFactorName), scorevo, sb);
				define = define.replaceAll("x", "[" + targetFactorName + "]");
				define = TBUtil.getTBUtil().replaceAll(define, "[�ƻ�ֵ]", "[��������." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[Ŀ��ֵ]", "[��������." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[Ȩ��]", "[��������." + "weights]");
				util.putDefaultFactorVO("����", define, targetName + "�÷ּ���", "", "4λС��");
				// �Ѽƻ�ֵ�ĵ�
				scorevo.setAttributeValue("planedvalue", planvalue);
				scorevo.setAttributeValue("weights", weights);
				Object obj = util.onExecute(util.getFoctorHashVO(targetName + "�÷ּ���"), scorevo, sb);
				double value = 0;
				if (obj instanceof Number) {
					try {
						value = Double.parseDouble(String.valueOf(obj));
					} catch (Exception e) {
						throw e;
					}
				}
				scorevo.setAttributeValue("rtobj",rtobj);
				scorevo.setAttributeValue("value",value);
				scorevo.setAttributeValue("process",sb.toString());
				list.add(scorevo);
//				executeValueSB.append("��ָ�꡾" + scorevo.getStringValue("checkeddeptname") + "��ʵ��ֵ���ֵ��" + rtobj + "��  " + "����" + dw + value + " \r\n" + sb + "\r\n");
			}
		}
		vos = new HashVO [list.size()];
		for(int i=0;i<list.size();i++){
			vos[i]=list.get(i);
		}
		return vos;
	}
	/**
	 * ͨ����ʱ�����Զ����㲿�ֶ���ָ��ʵ��Ч�湤�ʡ�
	 * �������ڸ�ʽ yyyy-MM-dd
	 * ����ֵ0Ϊû����Ҫ�����ָ��
	 * 		1Ϊ�ɹ�
	 */
	public int autoCalcPersonDLTargetByTimer(String jobid, String datadate) throws Exception {
		String checkdate = datadate.substring(0, 4) + "-" + datadate.substring(5, 7);
		String[] autoCalcTargetIDs = getDmo().getStringArrayFirstColByDS(null, "select id from sal_person_check_list where state='���뿼��' and alwaysview='Y'"); //�ҳ�������Ҫ�����ָ��
		if (autoCalcTargetIDs.length == 0) {
			return 0;
		}
		List list = new ArrayList();
		DeleteSQLBuilder delsql = new DeleteSQLBuilder("sal_person_check_auto_score"); //
		delsql.setWhereCondition(" checkdate='" + checkdate + "' and datadate='" + datadate + "'");
		list.add(delsql);//����������Ѿ�ִ�й�,��ɾ������
		for (int i = 0; i < autoCalcTargetIDs.length; i++) {
			HashVO[] rtvos = calcOnePersonTarget_P_Money(autoCalcTargetIDs[i], checkdate); //���㷵�ص�ֵ
			for (int j = 0; j < rtvos.length; j++) {
				InsertSQLBuilder insert = new InsertSQLBuilder("sal_person_check_auto_score");
				String id = getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_AUTO_SCORE");
				HashVO value = rtvos[j];
				insert.putFieldValue("id", id);
				insert.putFieldValue("targetid", value.getStringValue("targetid"));
				insert.putFieldValue("targetname", value.getStringValue("targetname"));
				insert.putFieldValue("checktype", value.getStringValue("checktype"));
				insert.putFieldValue("targettype", value.getStringValue("targettype"));
				insert.putFieldValue("checkeduser", value.getStringValue("checkeduser"));
				insert.putFieldValue("checkedusername", value.getStringValue("checkedusername"));
				insert.putFieldValue("getvalue", value.getStringValue("getvalue"));
				insert.putFieldValue("partvalue", value.getStringValue("partvalue"));
				insert.putFieldValue("planedvalue", value.getStringValue("planedvalue"));
				insert.putFieldValue("groupid", value.getStringValue("groupid"));
				insert.putFieldValue("targetdefine", value.getStringValue("targetdefine"));
				insert.putFieldValue("realvalue", value.getStringValue("realvalue"));
				insert.putFieldValue("checkscore", value.getStringValue("checkscore"));
				insert.putFieldValue("weights", value.getStringValue("weights"));
				insert.putFieldValue("checkdate", value.getStringValue("checkdate", checkdate));
				insert.putFieldValue("datadate", datadate);
				insert.putFieldValue("jobid", jobid);
				insert.putFieldValue("money", value.getStringValue("money"));
				insert.putFieldValue("descr", value.getStringValue("descr"));
				list.add(insert);
			}
		}
		getDmo().executeBatchByDS(null, list); //ֻҪsqlƴ����ɣ�Ӧ�ò��ᱨ�����в�������������
		return 1;
	}

}
