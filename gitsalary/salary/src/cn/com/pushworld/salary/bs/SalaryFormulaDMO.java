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
 * 服务器端调用公式引擎入口Dmo 该类主要用于单个公式的执行情况 bsh客户端执行不会下载系统的类。
 *
 * @author haoming create by 2013-8-24
 */
public class SalaryFormulaDMO extends AbstractDMO {
	private SalaryFomulaParseUtil formulaUtil = new SalaryFomulaParseUtil();
	private SalaryTBUtil stbutil = new SalaryTBUtil();
	private String str_dbtype = ServerEnvironment.getInstance().getDataSourceVO(null).getDbtype(); // 数据库类型
	private CommDMO dmo;
	private TBUtil tbutil = new TBUtil();

	public CommDMO getDmo() {
		if (dmo == null) {
			dmo = new CommDMO();
		}
		return dmo;
	}

	/*
	 * 客户端调用某个因子对象进行测试。
	 */
	public Object[] onExecute(HashVO _factorVO, HashVO _baseDataHashVO) throws Exception {
		StringBuffer stepSB = new StringBuffer();
		Object obj = formulaUtil.onExecute(_factorVO, _baseDataHashVO, stepSB);
		return new Object[] { obj, stepSB };
	}

	public static final HashMap<String, Object> calcSchedule = new HashMap<String, Object>();

	/*
	 * 由于高管计算定性考核时，用到了部门总分。
	 * 新添加部门计价指标 2015-04-15
	 */
	public synchronized void calcDeptDLtarget(HashVO planVO, String state, String _targetType) throws Exception {
		try {
			String planid = planVO.getStringValue("id");
			Object currSchedule = getRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
			HashVO[] hvodept = stbutil.getHashVoArrayByDS(null, "select t1.*,t2.factors from sal_dept_check_score t1 left join sal_target_list t2 on t1.targetid = t2.id where t1.targettype='" + _targetType + "' and t1.checkdate='" + planVO.getStringValue("checkdate") + "'");
			int deptNum = hvodept.length;
			List updateSqlList = new ArrayList();
			updateSqlList.add("delete from sal_factor_calvalue where logid='" + planid + "' and type='" + _targetType + "'");
			for (int i = 0; i < hvodept.length; i++) {
				HashVO scoreVO = hvodept[i];
				scoreVO.setAttributeValue("maindeptid", scoreVO.getStringValue("checkeddept"));
				String targetName = scoreVO.getStringValue("targetname"); // 得到指标名称
				String targetFactorName = targetName + "对象";
				String targetdef = scoreVO.getStringValue("targetdefine"); // 公式定义
				String getvalue = scoreVO.getStringValue("getvalue"); // 得到取值的公式
				String reportformula = scoreVO.getStringValue("reportformula");// 报表公式
				String hisFactors = scoreVO.getStringValue("factors", ""); //所有需要记录下来的因子结果
				String factors[] = tbutil.split(hisFactors, ";");
				HashMap<String, String> factor_value_map = new LinkedHashMap<String, String>(); //存储因子计算的结果
				Object reportvalue = null;
				if (targetdef == null || getvalue == null) {
					continue;
				}
				StringBuffer sb = new StringBuffer();
				formulaUtil.putDefaultFactorVO("数字", getvalue, targetFactorName, "", "4位小数");
				Object rtobj = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetFactorName), scoreVO, sb);
				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				if (tbutil.isEmpty(reportformula)) {
					reportvalue = rtobj;
				} else {
					if (!reportformula.equalsIgnoreCase("x")) {
						String reportfactorName = targetName + "报表对象";
						formulaUtil.putDefaultFactorVO("数字", reportformula, reportfactorName, "", "4位小数");
						try {
							reportvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(reportfactorName), scoreVO, sb);
						} catch (Exception ex) {
							throw new Exception("指标[" + targetName + "]报表公式取值执行失败.\n" + ex.getMessage());
						}
					} else {
						reportvalue = rtobj;
					}
				}
				// System.out.println("取值返回" + rtobj);
				targetdef = stbutil.formulaReplaceX(targetdef, "x", "[" + targetFactorName + "]");
				targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[计划值]", "[传入数据." + "planedvalue]");
				targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[目标值]", "[传入数据." + "planedvalue]");
				targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[权重]", "[传入数据." + "weights]");
				formulaUtil.putDefaultFactorVO("数字", targetdef, targetName + "得分计算", "", "4位小数");
				Object obj = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "得分计算"), scoreVO, sb);
				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				// System.out.println(obj);
				UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_dept_check_score");
				sql.putFieldValue("currvalue", String.valueOf(rtobj));
				sql.putFieldValue("checkscore", String.valueOf(obj));
				sql.putFieldValue("reportvalue", String.valueOf(reportvalue));
				sql.putFieldValue("status", "已提交");
				sql.setWhereCondition("id='" + scoreVO.getStringValue("id") + "'");
				updateSqlList.add(sql);
				buildSqlAfterCalTargetFactorValue(scoreVO.getStringValue("id"), planid, factor_value_map, _targetType, updateSqlList);
				setCurrPlanSchedule(planid, _targetType + "进度:" + (i + 1) + "/" + deptNum);
			}
			String stateSql = "update sal_target_check_log set status='" + state + "' where id=" + planid;
			updateSqlList.add(stateSql);
			new CommDMO().executeBatchByDS(null, updateSqlList, true, false);
			removeRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
		} catch (Exception ex) {
			// 如果报错了，需要移除当前进度状态
			removeRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
			throw ex;
		}
	}

	/*
	 * 计算定量指标,执行定量指标取值公式。 计算类型为"直接、岗位系数计算"，需要执行计算公式。
	 */
	public synchronized void calcPersonDLtarget(HashVO planVO, String _calbatch) throws Exception {
		String planid = planVO.getStringValue("id");
		Object currSchedule = getRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
		if (currSchedule != null) {
			StringBuffer strsb = new StringBuffer("服务器端[" + planVO.getStringValue("checkdate") + "]计划正在计算中...\r\n");
			if (currSchedule != null) {
				Object[] obj = (Object[]) currSchedule;
				String schedule = (String) obj[0];
				strsb.append(schedule);
			}
			throw new Exception(strsb.toString());
		}
		try {
			List updateSqlList = new ArrayList();
			updateSqlList.add("delete from sal_factor_calvalue where logid='" + planid + "' and type='员工定量指标'");
			StringBuffer sqlTarget=new StringBuffer();
			if(planVO.getStringValue("zbtype").equals("网格")){
				sqlTarget.append("select t1.*,t2.valuetype,t2.operationtype,t2.factors,t3.maindeptid,t3.maindeptid deptid,t3.maindeptid checkeddept,t4.id wgid from sal_person_check_score t1 left join  sal_person_check_list t2 on t1.targetid = t2.id left join Excel_tab_85 t4 on t1.checkeduser = t4.id left join  v_sal_personinfo t3 on t4.G = t3.code  where t1.targettype='员工定量指标' and logid='"+planid+"' and t2.catalogid in('215')"); //
			}else{
				sqlTarget.append("select t1.*,t2.valuetype,t2.operationtype,t2.factors,t3.maindeptid,t3.maindeptid deptid,t3.maindeptid checkeddept from sal_person_check_score t1 left join  sal_person_check_list t2 on t1.targetid = t2.id left join v_sal_personinfo t3 on t1.checkeduser = t3.id  where t1.targettype='员工定量指标' and t1.logid='" + planid + "' and t2.catalogid not in('215')");
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
				String targetName = scoreVO.getStringValue("targetname"); // 得到指标名称
				String targetFactorName = targetName + "对象";
				String targetdef = scoreVO.getStringValue("targetdefine"); // 公式定义
				String getvalue = scoreVO.getStringValue("getvalue"); // 得到取值的公式
				String partvalue = scoreVO.getStringValue("partvalue"); // 参与计算值公式。
				String valueType = scoreVO.getStringValue("valuetype");
				String operationtype = scoreVO.getStringValue("operationtype"); // 计算方式
				// ,加减
				String hisFactors = scoreVO.getStringValue("factors", ""); //所有需要记录下来的因子结果
				String factors[] = tbutil.split(hisFactors, ";");
				HashMap<String, String> factor_value_map = new LinkedHashMap<String, String>(); //存储因子计算的结果
				StringBuffer sb = new StringBuffer();
				formulaUtil.putDefaultFactorVO("数字", getvalue, targetFactorName, "", "4位小数");
				// scoreVO.setAttributeValue("", value)
				Object currvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetFactorName), scoreVO, sb); // 得到实际值
				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				// System.out.println("取值返回" + rtobj);
				Object money = 0;
				Object currpartvalue = null;
				if (!tbutil.isEmpty(partvalue)) {
					partvalue = stbutil.formulaReplaceX(partvalue, "x", "[" + targetFactorName + "]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[计划值]", "[传入数据." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[目标值]", "[传入数据." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[权重]", "[传入数据." + "weights]");
					formulaUtil.putDefaultFactorVO("数字", partvalue, targetName + "参与计算值", "", "2位小数");
					currpartvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "参与计算值"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
				} else {
					currpartvalue = currvalue;
					formulaUtil.putDefaultFactorVO("数字", partvalue, targetName + "参与计算值", "", "2位小数");
					formulaUtil.putDefaultFactorValue(targetName + "参与计算值", currpartvalue);
				}

				StringBuffer descrsb = new StringBuffer();
				if (("直接".equals(valueType) || "岗位系数计算".equals(valueType)) && targetdef != null && !targetdef.trim().equals("")) { // 通过公式定义计算效益工资
					targetdef = stbutil.formulaReplaceX(targetdef, "x", "[" + targetName + "参与计算值]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[计划值]", "[传入数据." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[目标值]", "[传入数据." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[权重]", "[传入数据." + "weights]");
					formulaUtil.putDefaultFactorVO("数字", targetdef, targetName + "得分计算", "", "2位小数");
					money = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "得分计算"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors);
					descrsb.append("权重:" + scoreVO.getStringValue("weights") + "  ");
					descrsb.append("计算公式:" + scoreVO.getStringValue("targetdefine") + "\r\n计算过程:" + sb.toString());
				}
				UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_person_check_score");
				if (currvalue == null || "".equals(String.valueOf(currvalue))) {
					currvalue = 0;
				}
				sql.putFieldValue("realvalue", String.valueOf(currvalue)); // 真正的值
				if (currpartvalue == null || "".equals(String.valueOf(currpartvalue))) {
					currpartvalue = 0;
				}
				sql.putFieldValue("checkscore", String.valueOf(currpartvalue));// 参与计算值
				sql.putFieldValue("status", "已提交");
				if ("减".equals(operationtype)) {
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
				//存储计算过程中因子的结果
				buildSqlAfterCalTargetFactorValue(scoreVO.getStringValue("id"), planid, factor_value_map, "员工定量指标", updateSqlList);
				setCurrPlanSchedule(planid, "\r\n员工定量指标进度:" + (i + 1) + "/" + (personNum));
			}
			new CommDMO().executeBatchByDS(null, updateSqlList, true, false);
			/** **员工考核评分详情表，平均值、最大值、排名赋值[YangQing/2013-11-23]**** */
			String sql_targetlist = "select id targetid,postid from v_sal_postgroup_dl_target  where targettype='员工定量指标'";

			//			StringBuffer sql_userpost = new StringBuffer("");
			//			sql_userpost.append(" select t1.id postid,t1.stationkind,t2.userid from pub_post t1 ");
			//			sql_userpost.append(" right join pub_user_post t2  ");
			//			sql_userpost.append(" on t1.id=t2.postid ");
			//			sql_userpost.append(" where t1.id is not null ");

			StringBuffer sql_userpost = new StringBuffer("");
			sql_userpost.append(" select stationkind,id from v_sal_personinfo where isuncheck='N' or isuncheck is null");

			String sql_scorelist = "select id,targetid,checkeduser,checkscore from sal_person_check_score where targettype='员工定量指标' and logid='" + planid + "'";

			// 员工定量指标数据
			HashVO[] targetVO = new CommDMO().getHashVoArrayByDS(null, sql_targetlist);
			// 员工岗位数据
			HashVO[] userpostVO = new CommDMO().getHashVoArrayByDS(null, sql_userpost.toString());
			// 员工得分数据
			HashVO[] scoreVO = new CommDMO().getHashVoArrayByDS(null, sql_scorelist);
			HashMap<String, List<String>> posttype_user = new HashMap<String, List<String>>();// 存放得到每个岗位分类下的人
			// 得到每个岗位分类下所有人
			for (int a = 0; a < userpostVO.length; a++) {
				String stationkind = userpostVO[a].getStringValue("stationkind", "");// 岗位分类
				String userid = userpostVO[a].getStringValue("id", "");// 人员ID
				if (posttype_user.containsKey(stationkind)) {// 如果该岗位分类存在，则提取出继续加入人员
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
				String posttype = targetVO[b].getStringValue("postid", "");// 被考核人群
				String targetid = targetVO[b].getStringValue("targetid", "");// 指标ID

				List<String> type_users = new ArrayList<String>();// 该指标下权重相同的岗位分类下所有人
				if (!TBUtil.isEmpty(posttype) && posttype.contains(";")) {
					String[] split_posttype = posttype.split(";");
					for (int i = 0; i < split_posttype.length; i++) {
						String everypost = split_posttype[i];
						if (TBUtil.isEmpty(everypost)) {
							continue;
						}
						if (posttype_user.containsKey(everypost)) {// 找到相同岗位分类
							List<String> userids = posttype_user.get(everypost);
							for (int x = 0; x < userids.size(); x++) {// 把每个岗位分类下的人汇总到该指标考核人群里
								type_users.add(userids.get(x));
							}
						}
					}
				}

				double total_score = 0;// 该指标下相同权重人的总得分
				double max_score = 0;// 该指标下相同权重人的最高得分
				int count = 0;// 个数
				String[][] score_detail = new String[scoreVO.length][2];// 存每条数据的ID、得分
				// 遍历得分,得出总分
				for (int x = 0; x < scoreVO.length; x++) {
					String score_target = scoreVO[x].getStringValue("targetid", "");// 得分表里的指标ID
					String checkeduser = scoreVO[x].getStringValue("checkeduser", "");// 被考核人
					if (targetid.equals(score_target)) {// 指标相同
						if (type_users.contains(checkeduser)) {// 只计算该指标相同权重的考核人群
							String str_score = scoreVO[x].getStringValue("checkscore", "0");// 得分
							String scoreid = scoreVO[x].getStringValue("id", "");// ID
							double checkscore = Double.parseDouble(str_score);// 考核得分

							count++;
							// 计算排名
							score_detail[count - 1][0] = scoreid;
							score_detail[count - 1][1] = checkscore + "";
							if (checkscore > max_score) {// 比较最大值
								max_score = checkscore;
							}
							total_score += checkscore;// 累加总分
						}
					}

				}
				// 处理平均值
				BigDecimal result_avg = new BigDecimal("0");
				if (count != 0) {// 判断除数是否为0
					BigDecimal operand1 = new BigDecimal(total_score + "");
					BigDecimal operand2 = new BigDecimal(count + "");
					result_avg = operand1.divide(operand2, 2, RoundingMode.HALF_UP); // 得出该指标下相同权重人的平均得分,保留两位，四舍五入
				}
				// 处理最大值
				String str_maxscore = max_score + "";
				if (Math.round(max_score) - max_score == 0) {// 如果最大值是6.0形式，则处理为整数;其它形式不变，如6.5
					str_maxscore = ((long) max_score) + "";
				}
				// 排序，降序
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
				// 每条数据拼接更新SQL
				for (int a = 0; a < count; a++) {
					String id = score_detail[a][0];
					int seq = a + 1;// 排名
					sql_score.add("update sal_person_check_score set avg_score='" + result_avg.toString() + "',max_score='" + str_maxscore + "',seq_score='" + (seq + "/" + count) + "' where id=" + id);
				}

			}
			new CommDMO().executeBatchByDS(null, sql_score);

			removeRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
		} catch (Exception ex) {
			// 如果报错了，需要移除当前进度状态
			removeRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
			throw ex;
		}
	}

	//处理计算过
	private void dealAfterCalTargetFactorValue(SalaryFomulaParseUtil _parse, HashMap _factor_value, String[] factor) {
		if (factor != null && factor.length > 0) {
			for (int i = 0; i < factor.length; i++) {
				if (!_factor_value.containsKey(factor[i])) { //如果没有存储过
					Object obj = _parse.getFactorisCalc(factor[i]);
					if (obj != null && !String.valueOf(obj).equals("") && !String.valueOf(obj).equals("null")) {
						_factor_value.put(factor[i], String.valueOf(obj));
					}
				}
			}
		}
	}

	//处理计算完指标后，存储过程中的因子值.
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

			HashMap<String, String> dxscoreMap = stbutil.getHashMapBySQLByDS(null, "select checkeddeptid,finalres from sal_target_check_result where logid='" + logid + "' and targettype='部门定性指标'");
			HashMap<String, String> dlscoreMap = stbutil.getHashMapBySQLByDS(null, "select checkeddeptid,finalres from sal_target_check_result where logid='" + logid + "' and targettype='部门定量指标'");
			list.add(deleteSql);
			for (int j = 0; j < vos.length; j++) {
				HashVO factorVO = formulaUtil.getFoctorHashVO("部门考核得分");
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
	 * 计算效益工资，通用方法。如果是演算指标，那么构造的score全部是假的。ID为负。
	 *
	 * @param isPerformCalc
	 *            是否是演算。如果演算，可以不传planid
	 * @return
	 */
	public synchronized HashVO[] calc_comm_p_Pay(HashVO[] hvoperson, String _planid, String checkdate, boolean isPerformCalc) throws Exception {
		CommDMO dmo = new CommDMO();
		// 每条指标子表对应的得分综合
		HashMap targetValueSumMap = new HashMap(); // 每个岗位群，总分
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
			targetValueSumMap = dmo.getHashMapBySQLByDS(null, "select t2.id,sum(t1.checkscore) A from sal_person_check_score t1 left join sal_person_check_post t2 on t1.groupid = t2.id   left join   sal_person_check_list t3 on t3.id = t2.targetid  where  t3.valuetype!='直接'  and t1.targettype='员工定量指标' and  t1.logid='" + _planid + "' group by t2.id ");// 得到某指标对应的某类人群得分总和
		}

		HashVO[] groupPostWeights = null;
		if ("ORACLE".equalsIgnoreCase(str_dbtype)) {
			groupPostWeights = stbutil.getHashVoArrayByDS(null, "select t2.id groupid ,t3.id posttype,sum(t2.weights) weights from sal_person_check_list t1 left join sal_person_check_post t2 on   t1.id = t2.targetid  left join pub_comboboxdict t3 on t2.postid like '%;'||t3.id||';%' and t3.type ='薪酬_岗位归类' where t1.state='参与考核'  group by t2.id,t3.id");
		} else { // mysql
			groupPostWeights = stbutil.getHashVoArrayByDS(null, "select t2.id groupid ,t3.id posttype,sum(t2.weights) weights from sal_person_check_list t1 left join sal_person_check_post t2 on   t1.id = t2.targetid  left join pub_comboboxdict t3 on t2.postid like concat('%;',t3.id,';%') and t3.type ='薪酬_岗位归类' where t1.state='参与考核'  group by t2.id,t3.id");
		}
		HashMap personStationratio = stbutil.getHashMapBySQLByDS(null, "select id,stationratio from sal_personinfo"); // 人员的岗位系数
		HashMap<String, Float> ggcalc = new HashMap<String, Float>(); // 个岗计算
		HashMap<String, Float> gbgcalc = new HashMap<String, Float>();// 个部岗计算。

		HashMap<String, Float> deptscoreMap = new HashMap<String, Float>(); // 部门考核分数缓存
		for (int i = 0; i < hvoperson.length; i++) {
			HashVO scoreVO = hvoperson[i];
			String scoreGroupId = scoreVO.getStringValue("groupid");
			String deptid = scoreVO.getStringValue("deptid"); //
			String valuetype = scoreVO.getStringValue("valuetype"); // 该指标对应人群采用的计算方式.目前用代码计算的有三种.
			String checkeduser = scoreVO.getStringValue("checkeduser"); //
			float scoreValue = Float.parseFloat(scoreVO.getStringValue("checkscore")); // 员工该指标实际完成值

			if ("个岗计算".equals(valuetype)) {// (个人得分*岗位系数)和
				String userStationRatio = (String) personStationratio.get(checkeduser);
				BigDecimal big = new BigDecimal(userStationRatio);
				float sum = 0;
				if (ggcalc.containsKey(scoreGroupId)) {
					sum = ggcalc.get(scoreGroupId);
				}
				sum += big.floatValue() * scoreValue;
				ggcalc.put(scoreGroupId, sum);
			} else if ("个部岗计算".equals(valuetype)) {// (个人得分*部门得分*岗位系数)比*计发绩效工资
				HashVO formulaVO = formulaUtil.getFoctorHashVO("部门考核分数调整后");
				float deptscore = 0;
				if (deptscoreMap.containsKey(deptid)) {
					deptscore = deptscoreMap.get(deptid);
				} else {
					deptscore = new BigDecimal(String.valueOf(formulaUtil.onExecute(formulaVO, scoreVO, new StringBuffer()))).floatValue(); // 以后从部门考评得分表中取值
					deptscoreMap.put(deptid, deptscore); // 缓存起来
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

		HashMap hmap = stbutil.getHashMapBySQLByDS(null, "select stationkind A,sum(stationratio) B from v_sal_personinfo group by stationkind"); // 每个岗位人群总系数
		TBUtil tbutil = new TBUtil();
		SalaryFomulaParseUtil util = new SalaryFomulaParseUtil();
		Object obj = util.onExecute(util.getFoctorHashVO("效益工资基数"), null, new StringBuffer());
		if (obj == null) {
			throw new Exception("公式因子定义中没有发现名称为【效益工资基数】,请添加.");
		}
		float jxgzjs = Float.parseFloat(String.valueOf(obj)); // 绩效工资基数
		HashMap<String, Float> postWeightSumMap = new HashMap(); // 每个岗位群的在指标中配置的所有权重和。

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
		HashMap<String, Float> oneTargetGroupJFMoneySum = new HashMap<String, Float>(); // 某个指标对应的岗位组子表的一条记录
		// 可以分的钱的总和
		HashMap<String, String> descrMap1 = new HashMap<String, String>();
		for (int i = 0; i < hvoperson.length; i++) {
			HashVO scoreVO = hvoperson[i];
			String scoreGroupId = scoreVO.getStringValue("groupid");
			String posts = scoreVO.getStringValue("postid"); // 获取某个人某指标被考核的所有岗位.
			String valuetype = scoreVO.getStringValue("valuetype"); // 该指标对应人群采用的计算方式.目前用代码计算的有三种.
			String allpost[] = tbutil.split(posts, ";"); // 得到所有岗位群的名称
			StringBuffer descr = new StringBuffer(); // 描述如何计算出的各指标的效益工资。
			descr.append("一个系数效益工资" + jxgzjs + "\r\n该指标考核的岗位有 [" + posts + "]");
			float targetMoneyJF = 0;
			if (oneTargetGroupJFMoneySum.containsKey(scoreGroupId)) {
				targetMoneyJF = oneTargetGroupJFMoneySum.get(scoreGroupId);
				descr.append(String.valueOf(descrMap1.get(scoreGroupId)));
			} else {
				StringBuffer innserDescrSB = new StringBuffer();
				for (int j = 0; j < allpost.length; j++) { // 可以缓存
					String postQName = allpost[j]; // 某个群的名称
					if (!tbutil.isEmpty(postQName)) { // 如果不为空
						String onePostQratioSum = (String) hmap.get(postQName); // 某个岗位群的系数之和
						if (!tbutil.isEmpty(onePostQratioSum)) {
							// 找到改岗位群对该指标的 权重比例.
							for (int k = 0; k < groupPostWeights.length; k++) {
								HashVO groupAndpostAndSum = groupPostWeights[k];
								String groupid = groupAndpostAndSum.getStringValue("groupid"); //
								String posttype = groupAndpostAndSum.getStringValue("posttype"); //
								String weights = groupAndpostAndSum.getStringValue("weights"); //
								if (scoreGroupId.equals(groupid) && postQName.equals(posttype)) {
									float weighttotle = postWeightSumMap.get(posttype); // 得到该岗位组在定量指标中的权重和
									if (weighttotle == 0) {
										WLTLogger.getLogger().warn(">>" + posttype + ",没有权重和。可能是配置有问题<<");
									} else {
										float ncmoney = jxgzjs * Float.parseFloat(onePostQratioSum) * Float.parseFloat(weights) / weighttotle; // 把该岗位的系数和*基数*该指标占比
										targetMoneyJF += ncmoney;
										innserDescrSB.append("\r\n[" + postQName + "]所有人岗位系数和:" + onePostQratioSum + ",该指标当前权重占比" + weights + "/" + weighttotle + "=" + (Float.parseFloat(weights) / weighttotle) + ",拿出效益工资:" + jxgzjs + "*" + onePostQratioSum + "*" + (Float.parseFloat(weights) / weighttotle) + "=" + ncmoney);
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
			descr.append("\r\n该指标计发总效益工资：" + targetMoneyJF);
			float scoreValue = Float.parseFloat(scoreVO.getStringValue("checkscore")); // 员工该指标实际完成值
			float targetValueSum = Float.parseFloat(String.valueOf(targetValueSumMap.get(scoreVO.getStringValue("groupid")))); // 该指标得分总和
			float personMoney = 0;
			float targetMoneyYF = targetMoneyJF; // 应发
			String checkeduser = scoreVO.getStringValue("checkeduser"); //
			String planvalueStr = scoreVO.getStringValue("planedvalue");
			if (!tbutil.isEmpty(planvalueStr)) { // 如果有计划值,会按照比例计算.
				float planvalue = Float.parseFloat(planvalueStr);
				if (targetValueSum > planvalue) {
					targetMoneyYF = targetValueSum / planvalue * targetMoneyJF;
					descr.append("\r\n完成值:" + targetValueSum + ">计划值:" + planvalue + "，指标应发效益工资增幅为:" + targetMoneyYF);
				} else {
					descr.append("\r\n[完成值]没有达到[计划值],指标应发效益工资为:" + targetMoneyYF);
				}
			} else {
				descr.append("\r\n该指标没有设定完成值,指标应发效益工资为:" + targetMoneyYF);
			}
			if ("计算".equals(valuetype)) { // 直接计算,按照本人分数占该指标
				// 同占比权重的人,就行分数占比计算.公式=个人得分比*计发绩效工资
				if (targetValueSum == 0) {
					float weight = Float.parseFloat(hvoperson[i].getStringValue("weights", "0"));
					float weighttotle = postWeightSumMap.get(hvoperson[i].getStringValue("stationkind"));
					personMoney = (Float.parseFloat((String) personStationratio.get(checkeduser))) * jxgzjs * weight / weighttotle; // 拿出金额
					descr.append("被考核人员所在的岗位组[" + hvoperson[i].getStringValue("stationkind") + "]的所有人的结果值为0,可能上传的数据中不包含该组值.");
				} else {
					personMoney = scoreValue / targetValueSum * targetMoneyYF;
					descr.append("\r\n开始计算该人员该指标效益工资=本人实际值/所有人实际值和*指标应发效益工资");
					descr.append("\r\n=" + scoreValue + "/" + targetValueSum + "*" + targetMoneyJF + "=" + personMoney);
				}
			} else if ("个岗计算".equals(valuetype)) {// (个人得分*岗位系数)比*计发绩效工资
				Float sumscore = ggcalc.get(scoreGroupId);
				if (sumscore == 0) {// 判断分母是否为0，贷款新增量指标计算时，分母可能为0.
					personMoney = targetMoneyYF / hvoperson.length;
					descr.append("\r\n=数据可能有问题（所有人的实际得分之和为0，暂取平均值。），请核实确认！！！");
				} else {
					personMoney = (scoreValue * (Float.parseFloat((String) personStationratio.get(checkeduser)))) / sumscore * targetMoneyYF;
					descr.append("\r\n开始计算该人员该指标效益工资=(本人实际值*岗位系数)的占比*指标应发效益工资");
					descr.append("\r\n=(" + scoreValue + "*" + (Float.parseFloat((String) personStationratio.get(checkeduser))) + ")/" + sumscore + "*" + targetMoneyYF + "=" + personMoney);
				}
			} else if ("个部岗计算".equals(valuetype)) {// (个人得分*部门得分*岗位系数)比*计发绩效工资
				float deptscores = deptscoreMap.get(scoreVO.getStringValue("deptid"));
				personMoney = (scoreValue * deptscores * (Float.parseFloat((String) personStationratio.get(checkeduser)))) / gbgcalc.get(scoreGroupId) * targetMoneyYF;
				descr.append("\r\n开始计算该人员该指标效益工资=(本人实际值*部门得分*岗位系数)的占比*指标应发效益工资");
				descr.append("\r\n=(" + scoreValue + "*" + deptscores + "*" + (Float.parseFloat((String) personStationratio.get(checkeduser))) + ")/" + gbgcalc.get(scoreGroupId) + "*" + targetMoneyYF + "=" + personMoney);

			}
			if ("减".equals(scoreVO.getStringValue("operationtype"))) {
				personMoney = 0 - personMoney;
			}
			descr.append("\r\n该指标计算类型为[" + scoreVO.getStringValue("operationtype") + "],最终值为：" + personMoney);
			float weight = Float.parseFloat(hvoperson[i].getStringValue("weights", "0"));
			if (!postWeightSumMap.containsKey(hvoperson[i].getStringValue("stationkind"))) {// 可能由于指标没有更新，而人员被换了岗位。
				UpdateSQLBuilder sqlb = new UpdateSQLBuilder("sal_person_check_score");
				sqlb.setWhereCondition("id = " + scoreVO.getStringValue("id"));
				descr = new StringBuffer();
				descr.append("该指标考核的岗位有[" + posts + "],被考核人员当前岗位为[" + hvoperson[i].getStringValue("stationkind") + "]");
				descr.append("\r\n被考核人不在该指标考核范围内.导致原因:生成该次考核计划后,已经给该人员生成该指标的考核详情,然后又调整了该人员的岗位,前后数据不匹配。");
				descr.append("\r\n修改方式：在评分表更新功能点，点击[评分表更新(定量)]按钮,然后再重新计算效益工资。");
				sqlb.putFieldValue("descr", descr.toString());
				sqlList.add(sqlb);
				scoreVO.setAttributeValue("descr", descr.toString());
				continue;
			} else {
				float weighttotle = postWeightSumMap.get(hvoperson[i].getStringValue("stationkind"));
				float ncmoney = (Float.parseFloat((String) personStationratio.get(checkeduser))) * jxgzjs * weight / weighttotle; // 拿出金额
				descr.insert(0, "该指标该人员拿出" + ncmoney + ",实际得:" + personMoney + ",浮动:" + (personMoney - ncmoney) + ",具体如下：\r\n");
				if (!isPerformCalc) {
					setCurrPlanSchedule(_planid, "计算人员各指标效益工资" + (i + 1) + "/" + hvoperson.length);
				} else {
					setRemoteActionSchedule("员工定量指标演算", "员工定量指标演算", "计算人员各指标效益工资" + (i + 1) + "/" + hvoperson.length);
				}
				UpdateSQLBuilder sqlb = new UpdateSQLBuilder("sal_person_check_score");
				BigDecimal decimal = null;
				try {
					decimal = new BigDecimal(personMoney).setScale(4, BigDecimal.ROUND_HALF_UP);
				} catch (Exception e) {
					throw new Exception("在计算[" + scoreVO.getStringValue("checkedusername") + "]的[" + scoreVO.getStringValue("targetname") + "]时,该指标应发：" + targetMoneyYF + ",个人占:" + personMoney + "已经计算出:" + descr + "\n" + e.getMessage());
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

		// 开始调节对 标准计算出来的各指标效益工资就行调整
		List reviseSqlList = new ArrayList(); // 调整效益工资的所有sql
		for (int i = 0; i < hvoperson.length; i++) {
			HashVO scoreVO = hvoperson[i];
			String targetdefine = scoreVO.getStringValue("targetdefine"); // 查看定义是否配置单独执行的类。
			if (targetdefine == null || targetdefine.equals("")) {
				continue;
			}
			if (targetdefine.indexOf("+") >= 0 || targetdefine.indexOf("-") >= 0 || targetdefine.indexOf("*") >= 0 || targetdefine.indexOf("/") >= 0 || (targetdefine.indexOf("[") >= 0 && targetdefine.indexOf("]") >= 0)) {
				String[] formulas = targetdefine.split(";\n"); // 很多组公式
				StringBuffer desc = new StringBuffer();
				for (int j = 0; j < formulas.length; j++) {
					String customFactorName = "公式" + j;
					String targetdef = formulas[j];
					targetdef = stbutil.formulaReplaceX(targetdef, "x", "[传入数据.money]");
					formulaUtil.putDefaultFactorVO("数字", targetdef, customFactorName, "", "2位小数");
					Object lastReviseValue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(customFactorName), scoreVO, desc);
					if (formulas.length - 1 == j) { // 如果是最后一个，需要判断返回值
						String descrs = scoreVO.getStringValue("descr", "") + "\r\n根据公式重新调整效益工资为:" + lastReviseValue + ".详情如下\r\n" + desc.toString() + "";
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
			} else if (targetdefine.indexOf(".") >= 0 || targetdefine.indexOf("cn") >= 0 || targetdefine.indexOf("com") >= 0) {// 如果有.才可能是类,约定好.必须有cn或者com.
				try {
					AbstractFormulaClassIfc formulaClassIfc = (AbstractFormulaClassIfc) Class.forName(targetdefine).newInstance();
					formulaClassIfc.onExecute(null, scoreVO, formulaUtil); // 最终返回值
				} catch (ClassNotFoundException ex) {
					WLTLogger.getLogger(SalaryFormulaDMO.class).error(targetdefine + "类不存在", ex);
				} catch (Exception ex) {
					throw ex;
				}
			}
		}
		if (reviseSqlList.size() > 0 && !isPerformCalc) {
			dmo.executeBatchByDS(null, reviseSqlList); // 执行调整后的更新语句
		}

		if (isPerformCalc) {
			removeRemoteActionSchedule("员工定量指标演算", "员工定量指标演算");
			return hvoperson;
		}

		// 如果是演算，后面不执行   group by t2.id
		HashVO uservo[] = dmo.getHashVoArrayByDS(null, "select distinct(t2.id),t2.*,t1.checkdate from sal_person_check_score t1 left join v_sal_personinfo t2 on t1.checkeduser = t2.id where t1.targettype='员工定量指标' and t1.logid='" + _planid + "'");
		String logid = _planid;
		List insertList = new ArrayList();
		// 删除一次本月的效益工资result表数据。
		String deleteSql = "delete from sal_person_check_result where targettype='员工定量指标' and logid=" + logid;
		insertList.add(deleteSql);
		for (int i = 0; i < uservo.length; i++) {
			InsertSQLBuilder insertSql = new InsertSQLBuilder("sal_person_check_result"); // 往员工最终得分表插入一条数据.效益工资
			insertSql.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_RESULT"));
			insertSql.putFieldValue("checkeduserid", uservo[i].getStringValue("id"));
			uservo[i].setAttributeValue("checkeduserid", uservo[i].getStringValue("id"));
			uservo[i].setAttributeValue("checkeduser", uservo[i].getStringValue("id"));
			Object money = util.onExecute(util.getFoctorHashVO("效益工资"), uservo[i], new StringBuffer()); // 从公式中取效益工资
			Object centerMoneyTotle = util.onExecute(util.getFoctorHashVO("中间计算效益工资"), uservo[i], new StringBuffer()); // 所有指标效益工资总和
			Object stationratio = util.onExecute(util.getFoctorHashVO("岗位系数"), uservo[i], new StringBuffer()); // 所有指标效益工资总和
			Object money_jf = util.onExecute(util.getFoctorHashVO("某员工计发效益工资"), uservo[i], new StringBuffer()); // 所有指标效益工资总和
			Object jishu = util.onExecute(util.getFoctorHashVO("效益工资基数"), uservo[i], new StringBuffer());
			if (money == null) {
				money = 0;
			}
			StringBuffer descr = new StringBuffer("此员工岗位系数为：" + stationratio + ",");
			descr.append("岗位计发效益工资为：" + stationratio + "*" + jishu + "=" + money_jf + ",");
			descr.append("定量指标效益总工资为:" + centerMoneyTotle + "");
			insertSql.putFieldValue("finalres", String.valueOf(centerMoneyTotle));
			insertSql.putFieldValue("money", String.valueOf(money));
			if ((new BigDecimal(String.valueOf(centerMoneyTotle)).floatValue()) < 0) { // 如果效益工资扣完了。
				descr.append("效益工资小于0,实发：0");
			} else {
				descr.append("实发:" + money);
			}
			insertSql.putFieldValue("logid", logid);
			insertSql.putFieldValue("descr", descr.toString());
			insertSql.putFieldValue("targettype", "员工定量指标");
			setCurrPlanSchedule(_planid, "计算人员效益工资" + (i + 1) + "/" + uservo.length);
			insertList.add(insertSql);
		}

		// 计算延迟支付金额。
		dmo.executeBatchByDS(null, insertList, true, false); // 不记录sql操作日志
		removeRemoteActionSchedule(_planid, "指标计算");
		return hvoperson;
	}

	/*
	 * 计算延迟支付金额。
	 */
	public void calcDelayPay(String logid) throws Exception {
		try {
			String sys_delay_money_factor = tbutil.getSysOptionStringValue("通过系统计算延期支付因子名称", "");
			String dealy_class = tbutil.getSysOptionStringValue("延期支付自定义实现类", "");
			if (dealy_class.length() > 0 && dealy_class.indexOf(".") > 0) { //是类
				DelayMoneyCalIfc formulaClassIfc = (DelayMoneyCalIfc) Class.forName(dealy_class).newInstance();
				formulaClassIfc.calc(logid);
				return; //直接返回
			}
			HashVO dealy_factor_hashvo = null;
			if (!tbutil.isEmpty(sys_delay_money_factor)) {
				dealy_factor_hashvo = formulaUtil.getFoctorHashVO(sys_delay_money_factor);
			}

			float jxbase = Float.parseFloat(formulaUtil.onExecute(formulaUtil.getFoctorHashVO("效益工资基数"), null, new StringBuffer()) + "");
			HashVO xyMoneyVOs[] = getDmo().getHashVoArrayByDS(null,
					"select t2.*,t1.checkeduserid,t2.id checkeduser,t1.money,t4.code postcode,t5.checkdate  from sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid= t2.id   left join PUB_COMBOBOXDICT t4 on t4.id = t2.stationkind left join sal_target_check_log t5 on t5.id = t1.logid where t1.targettype='员工定量指标' and t1.logid='" + logid + "' order by t2.deptseq,t2.postseq");
			// 岗位组对应的平均效益工资。
			HashMap<String, String> postcode_avgMoney = getDmo().getHashMapBySQLByDS(null, "select t4.code,avg(t1.money) from sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid= t2.id   left join PUB_COMBOBOXDICT t4 on t4.id = t2.stationkind where t1.targettype='员工定量指标' and t1.logid='" + logid + "' group by t4.code");
			String currtime = tbutil.getCurrTime();
			String currdate = tbutil.getCurrDate().substring(0, 7); // 到月份
			List list = new ArrayList();
			setRemoteActionSchedule(logid, "指标计算", "开始计算延期支付金");
			boolean no_money_insert = tbutil.getSysOptionBooleanValue("零延期支付基金是否入账", true);
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
					String stationRatio = xyMoneyVOs[i].getStringValue("stationRatio"); // 岗位系数
					String postcode = xyMoneyVOs[i].getStringValue("postcode"); // 岗位归类,客户总经理和客户经理code一样.
					if (postcode == null || postcode.equals("")) {
						continue;
					}
					float xsmoney = Float.parseFloat(stationRatio) * jxbase;// 系数工资，得到某人的可计发的效益工资.
					float avg = Float.parseFloat((String) postcode_avgMoney.get(postcode));
					float delaymoney = 0;

					if ("客户经理".equals(postcode) || "信贷员".equals(postcode) || "支行行长".equals(postcode)) {
						if (yfmoney > avg) {
							delaymoney = (yfmoney - avg) / 2;
						}
						delay = new BigDecimal(delaymoney).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
						if (delay > 0) {
							descr.append("【" + postcode + "】岗位群的平均值为" + avg + ",应发" + yfmoney + ",扣除超出部分一半" + "(" + yfmoney + "-" + avg + ")/2=" + delay + "作为延期支付金");
						}
					} else { // 先
						if (yfmoney > avg && yfmoney > xsmoney) {
							if (avg > xsmoney) {
								delaymoney = yfmoney - avg;//
								delay = new BigDecimal(delaymoney).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
								descr.append("【" + postcode + "】岗位群的平均值为" + avg + ",平均值大于岗位系数效益工资" + xsmoney + ",应发" + yfmoney + ",扣除" + yfmoney + "-" + avg + "=" + delay + "作为延期支付金");
							} else {
								delaymoney = yfmoney - xsmoney; // 超过计发的全部扣掉
								delay = new BigDecimal(delaymoney).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
								descr.append("【" + postcode + "】岗位群的平均值为" + avg + ",平均值小于岗位系数效益工资" + xsmoney + ",应发" + yfmoney + ",扣除" + yfmoney + "-" + xsmoney + "=" + delay + "作为延期支付金");
							}
						}
					}
				}
				if (delay <= 0 && !no_money_insert) {
					continue;
				}
				if (delay <= 0) {
					descr.append("本月没有延期支付基金入账");
				}
				InsertSQLBuilder sql = new InsertSQLBuilder("sal_person_fund_account");
				sql.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_SAL_PERSON_FUND_ACCOUNT"));
				sql.putFieldValue("userid", xyMoneyVOs[i].getStringValue("checkeduserid"));
				sql.putFieldValue("username", xyMoneyVOs[i].getStringValue("name"));
				sql.putFieldValue("logid", logid);
				sql.putFieldValue("formtype", "延期支付基金");
				sql.putFieldValue("busitype", "延期支付");
				sql.putFieldValue("calc", "+");
				sql.putFieldValue("money", delay);
				sql.putFieldValue("descr", descr.toString());
				sql.putFieldValue("datadate", currdate);
				sql.putFieldValue("createtime", currtime);
				sql.putFieldValue("createuser", "系统计算");
				sql.putFieldValue("isdeleted", "N");
				list.add(sql);
			}
			if (list.size() > 0) {
				list.add(0, "delete from sal_person_fund_account where logid = " + logid);
				dmo.executeBatchByDS(null, list);
			}
			removeRemoteActionSchedule(logid, "指标计算");
		} catch (Exception e) {
			removeRemoteActionSchedule(logid, "指标计算");
			throw e;
		}
	}

	/*
	 * 计算效益工资，计算前需要先执行定量指标取值
	 */
	public synchronized void calc_P_Pay(HashVO planVO, String _calbatch) throws Exception {
		Object currSchedule = getRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
		if (currSchedule != null) {
			StringBuffer strsb = new StringBuffer("服务器端[" + planVO.getStringValue("checkdate") + "]计划正在计算效益工资...\r\n");
			if (currSchedule != null) {
				Object[] obj = (Object[]) currSchedule;
				String schedule = (String) obj[0];
				strsb.append(schedule);
			}
			throw new Exception(strsb.toString());
		}
		try {
			StringBuffer hvoStr = new StringBuffer(
					"select t1.*,t2.postid,t4.deptid,t4.deptid maindeptid,t4.tellerno , t4.name ,t2.weights gweights,t4.stationratio,t4.stationkind,t3.valuetype,t3.operationtype from sal_person_check_score t1 left join sal_person_check_post t2  on  t1.groupid = t2.id    left join sal_person_check_list t3 on  t3.id = t2.targetid left join v_sal_personinfo t4 on t4.id = t1.checkeduser  where (t3.valuetype='计算' or t3.valuetype='个岗计算' or t3.valuetype='个部岗计算') and  t1.targettype='员工定量指标' and t1.logid='"
							+ planVO.getStringValue("id") + "' ");
			if (tbutil.isEmpty(_calbatch)) {
				hvoStr.append(" and (t3.calbatch is null or t3.calbatch='') ");
			} else {
				hvoStr.append(" and t3.calbatch=" + _calbatch);
			}
			HashVO[] hvoperson = stbutil.getHashVoArrayByDS(null, hvoStr.toString());

			calc_comm_p_Pay(hvoperson, planVO.getStringValue("id"), planVO.getStringValue("checkdate"), false);
		} catch (Exception ex) {
			removeRemoteActionSchedule(planVO.getStringValue("id"), "指标计算");
			throw ex;
		}
	}

	/*
	 * 计算亲情工资
	 */
	public void onCalcQQMoney(HashVO _planVO) throws Exception {
		String planid = _planVO.getStringValue("id");
		try {
			boolean calcQQ = tbutil.getSysOptionBooleanValue("是否计算亲情工资", true); // 默认计算亲情工资。如果不计算亲情工资，总分排名还是需要算的。
			CommDMO dmo = new CommDMO();
			HashVO users[] = stbutil.getHashVoArrayByDS(null,
					"select t2.id,t2.name,t2.tellerno,t2.maindeptid,t2.deptid,t4.name deptname,t4.id deptid,t3.checkdate,t2.id checkeduser,t1.logid from sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid = t2.id  left join sal_target_check_log t3 on t3.id = t1.logid left join pub_corp_dept t4 on t4.id = t2.deptid where t1.targettype='员工定性指标' and t1.logid='"
							+ _planVO.getStringValue("id") + "' order by t2.deptseq,t2.postseq");

			// if (users.length == 0) {
			// throw new Exception("计算亲情工资,必须先计算员工定性考核结果.");
			// }
			List sqlList = new ArrayList();
			String deleteSql = "delete from sal_person_check_result where targettype='员工考核分数' and logid=" + _planVO.getStringValue("id");
			sqlList.add(deleteSql);
			for (int i = 0; i < users.length; i++) {
				Object value = formulaUtil.onExecute(formulaUtil.getFoctorHashVO("个人考核分数"), users[i], new StringBuffer());
				if (value == null) {
					value = 0;
				} else {
					setCurrPlanSchedule(planid, "计算员工考核分数" + (i + 1) + "/" + users.length);
				}
				InsertSQLBuilder insert = new InsertSQLBuilder("sal_person_check_result");
				insert.putFieldValue("id", dmo.getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_RESULT"));
				insert.putFieldValue("checkeduserid", users[i].getStringValue("id"));
				insert.putFieldValue("finalres", String.valueOf(value)); // 最终得分
				insert.putFieldValue("targettype", "员工考核分数"); // 最终得分
				insert.putFieldValue("logid", users[i].getStringValue("logid")); // 最终得分

				insert.putFieldValue("checkeduserdeptname", users[i].getStringValue("deptname"));
				insert.putFieldValue("checkeduserdeptid", users[i].getStringValue("deptid"));
				insert.putFieldValue("checkedusername", users[i].getStringValue("name"));

				sqlList.add(insert);
			}
			dmo.executeBatchByDS(null, sqlList);
			// 个人得分已经计算完毕，然后开始分组比较。

			HashVO[] hvo = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型') and id not like '$%' order by seq");

			HashVO[] kindVOs = dmo.getHashVoArrayByDS(null, "select *from sal_person_check_type");// 考核对象分类
			HashVO[] scoreVO = dmo.getHashVoArrayByDS(null, "select t1.*,t2.tellerno,t2.id checkeduser ,t2.stationkind,t4.corptype from  sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid = t2.id left join pub_corp_dept t4 on t4.id = t2.maindeptid  where t2.stationkind!='待岗' and  t1.targettype='员工考核分数' and t1.logid=" + _planVO.getStringValue("id"));

			HashMap<String, String> findDeptSelfDept = new HashMap<String, String>(); // 记录部门的本部门，从策略中找。
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
			for (int i = 0; i < scoreVO.length; i++) {
				String userdepttype = scoreVO[i].getStringValue("corptype");
				userdepttype = findDeptSelfDept.get(userdepttype);// 转一下
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
				setCurrPlanSchedule(planid, "计算亲情工资...");
			}
			HashVO factorVO = formulaUtil.getFoctorHashVO("亲情工资计算");//
			HashVO factorVO_2 = formulaUtil.getFoctorHashVO("员工排名算优良");//
			for (Iterator iterator = sameDeptTypeUserScores.entrySet().iterator(); iterator.hasNext();) {
				Entry object = (Entry) iterator.next();
				String depttype = (String) object.getKey();
				List depttypeUserScores = (List) object.getValue();
				for (int i = 0; i < kindVOs.length; i++) {
					HashVO kindvo = kindVOs[i];
					String kinds = kindvo.getStringValue("stationkinds"); // 获取每一个分类对应的
					// 岗位组
					List list = new ArrayList();
					for (int j = 0; j < depttypeUserScores.size(); j++) {
						HashVO score = (HashVO) depttypeUserScores.get(j);
						String score_kind = score.getStringValue("stationkind");
						if (score != null && kinds.contains(score_kind)) { // 如果有。
							list.add(score);
						}
					}
					// 把这个大群就行排序
					HashVO[] kind_ScoreVOs = (HashVO[]) list.toArray(new HashVO[0]);
					TBUtil.getTBUtil().sortHashVOs(kind_ScoreVOs, new String[][] { { "finalres", "Y", "Y" } });
					int num = kind_ScoreVOs.length; // 总人数
					for (int j = 0; j < kind_ScoreVOs.length; j++) {
						int money = 0;
						kind_ScoreVOs[j].setAttributeValue("排名", new BigDecimal(j + 1).divide(new BigDecimal(num), 2, BigDecimal.ROUND_HALF_UP).floatValue()); // 排名
						UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_person_check_result");
						if (calcQQ) {
							money = new BigDecimal(String.valueOf(formulaUtil.onExecute(factorVO, kind_ScoreVOs[j], new StringBuffer()))).intValue();
							usermoney.put(kind_ScoreVOs[j].getStringValue("checkeduserid"), money);
							sql.putFieldValue("money", money);
						}
						StringBuffer descr = new StringBuffer();
						if (factorVO_2 != null) {
							String yl = String.valueOf(formulaUtil.onExecute(factorVO_2, kind_ScoreVOs[j], new StringBuffer())); // 优良判断
							if (yl != null && !yl.equals("")) {
								descr.append("【" + yl + "】 ");
							}
							sql.putFieldValue("finalres2", yl);
						}
						descr.append("在【" + depttype + "】的【" + kindvo.getStringValue("name") + "】组内排名" + (j + 1) + "/" + num);
						sql.putFieldValue("descr", descr.toString());
						sql.setWhereCondition(" id = " + kind_ScoreVOs[j].getStringValue("id"));
						updateSqlList.add(sql);
					}
				}
			}
			dmo.executeBatchByDS(null, updateSqlList, true, false);
			removeRemoteActionSchedule(planid, "指标计算");
		} catch (Exception ex) {
			removeRemoteActionSchedule(planid, "指标计算");
			throw ex;
		}
	}

	private synchronized void setCurrPlanSchedule(String planid, String _msg) {
		setRemoteActionSchedule(planid, "指标计算", _msg);
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
		HashVO[] hvo = dmo.getHashVoArrayByDS(null, "select id,code,name from pub_comboboxdict where type in ('机构分类','机构类型') and id not like '$%' order by seq");
		HashMap<String, String> findDeptSelfDept = new HashMap<String, String>(); // 记录部门的本部门，从策略中找。
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
		HashMap<String, String> deptType = dmo.getHashMapBySQLByDS(null, "select id,corptype from pub_corp_dept");
		HashVO[] deptLastScore = dmo.getHashVoArrayByDS(null, "select checkeddeptid, currscore+revisescore num from sal_target_check_revise_result where logid = " + logid); // 所有部门的最终得分
		HashMap<String, Float> depttype_AllScores = new HashMap<String, Float>(); // 得到某个机构类型下的最高分
		for (int i = 0; i < deptLastScore.length; i++) {
			String deptid = deptLastScore[i].getStringValue("checkeddeptid");// 部门ID
			String depttype = deptType.get(deptid); // 得到机构类型
			if (findDeptSelfDept.containsKey(depttype)) {
				depttype = findDeptSelfDept.get(depttype); // 找到父亲的
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
			Entry depttypeAndParent = (Entry) iterator.next(); // 机构类型，和父亲机构
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
	 * 获取远程调用当前显示内容
	 */
	public static synchronized Object getRemoteActionSchedule(String _key, String _billType) {
		return calcSchedule.get(_key + "$" + _billType);
	}

	// 柜员业务笔数平均值
	public float getGYTellerProfessionAVG(String _checkdate) throws Exception {
		return getGYTellerProfessionAVG(_checkdate, "柜员实际业务笔数", "柜员");
	}

	// 柜员业务笔数平均值
	public float getGYTellerProfessionAVG(String _checkdate, String factorName, String stationKind) throws Exception {
		return getGYTellerProfessionAVG(_checkdate, factorName, stationKind, null);
	}

	// 柜员业务笔数平均值
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
		if (length == 0) { // 如果没有柜员数据
			return 0;
		}
		return (int) (totle / vos.length);
	}

	/*
	 * 某岗位组的人均外勤存款余额
	 */
	public HashMap<String, Double> getPostAvgDeposit(HashVO _hvo) throws Exception {
		CommDMO dmo = new CommDMO();
		String targetid = _hvo.getStringValue("targetid"); //
		String checkdate = _hvo.getStringValue("checkdate");
		HashVO targetAndPost[] = dmo.getHashVoArrayByDS(null, "select groupid,postid from v_sal_postgroup_dl_target where id=" + targetid); // 指标对应的岗位组
		HashVO excelAllRows[] = dmo.getHashVoArrayByDS(null, "select B,E from excel_tab_2 where concat(year,'-',month)='" + checkdate + "'");
		HashMap<String, Double> userAndMoneyMap = new HashMap<String, Double>(); // 员工姓名和余额
		for (int i = 0; i < excelAllRows.length; i++) {
			String username = excelAllRows[i].getStringValue("B"); // 人员姓名
			if (username == null) {
				continue;
			}
			String money = excelAllRows[i].getStringValue("E", "0"); // 人员姓名
			double currv = 0;
			try {
				currv = Double.parseDouble(money);
			} catch (Exception ex) {
				// 可能是前几行。
				WLTLogger.getLogger(PersonStyleReportWKPanel.class).error("");
			}
			double value = 0;
			if (userAndMoneyMap.containsKey(username)) {
				value = (Double) userAndMoneyMap.get(username);
			}
			value += currv;
			userAndMoneyMap.put(username, value);
		}
		// 得到该指标的多个组
		HashMap<String, Double> groupAndAvgMoney = new HashMap<String, Double>(); // 每个组的平均存款余额
		for (int i = 0; i < targetAndPost.length; i++) { // 应该没几个。可以在里面执行查询
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
			double avg = new BigDecimal(allMoney / users.length).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); // 该岗位组的人均存款余额
			groupAndAvgMoney.put(groupid, avg);
		}
		return groupAndAvgMoney;
	}

	/**
	 * 某岗位组的揽存平均值
	 *
	 * @param _hvo
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Double> getPostAvg_LC(HashVO _hvo) throws Exception {
		CommDMO dmo = new CommDMO();
		String targetid = _hvo.getStringValue("targetid"); //
		HashVO targetAndPost[] = dmo.getHashVoArrayByDS(null, "select groupid,postid from v_sal_postgroup_dl_target where id=" + targetid); // 指标对应的岗位组
		HashVO foctor = formulaUtil.getFoctorHashVO("所有人揽存");
		HashVO excelAllRows[] = (HashVO[]) formulaUtil.onExecute(foctor, _hvo, new StringBuffer());
		HashMap<String, HashVO> userAndMoneyMap = new HashMap<String, HashVO>(); // 员工姓名和余额
		for (int i = 0; i < excelAllRows.length; i++) {
			String username = excelAllRows[i].getStringValue("B"); // 人员姓名
			if (username == null) {
				continue;
			}
			userAndMoneyMap.put(username, excelAllRows[i]);
		}
		// 得到该指标的多个组
		HashMap<String, Double> groupAndAvgMoney = new HashMap<String, Double>(); // 每个组的平均存款余额
		HashVO factor_calc = formulaUtil.getFoctorHashVO("调用揽存计算");
		HashVO factor_lc_warn = formulaUtil.getFoctorHashVO("揽存警戒值");

		for (int i = 0; i < targetAndPost.length; i++) { // 应该没几个。可以在里面执行查询
			String postids = targetAndPost[i].getStringValue("postid");
			String groupid = targetAndPost[i].getStringValue("groupid");
			HashVO users[] = dmo.getHashVoArrayByDS(null, "select name,stationkind from v_sal_personinfo where stationkind in(" + TBUtil.getTBUtil().getInCondition(postids) + ")");
			if (users.length == 0) {
				continue;
			}
			double allMoney = 0;
			int over_user = 0;// 超出的人数
			for (int j = 0; j < users.length; j++) {
				String username = users[j].getStringValue("name");
				if (userAndMoneyMap.containsKey(username)) {
					// 调用揽存计算
					HashVO uservo = userAndMoneyMap.get(username);
					uservo.setAttributeValue("stationkind", users[j].getStringValue("stationkind"));
					double theUserMoney = (Double) formulaUtil.onExecute(factor_calc, uservo, new StringBuffer());
					Object obj = formulaUtil.onExecute(factor_lc_warn, uservo, new StringBuffer());
					if (obj != null) {
						double warnvalue = Double.parseDouble(String.valueOf(obj));
						if (warnvalue < theUserMoney && warnvalue > 0) {
							over_user++; // 如果超出预计值，直接跳过。
							continue;
						}
					}
					allMoney += theUserMoney;
				}
			}
			double avg = new BigDecimal(allMoney / (users.length - over_user)).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue(); // 该岗位组的人均存款余额
			groupAndAvgMoney.put(groupid, avg);
		}
		return groupAndAvgMoney;
	}

	/*
	 * 演算一个指标的效益工资
	 */
	public HashVO[] calcOnePersonTarget_P_Money(String _targetID, String _checkdate) throws Exception {
		try {
			CommDMO dmo = new CommDMO();
			TBUtil tbutil = new TBUtil();
			HashVO target[] = dmo.getHashVoArrayByDS(null, "select * from sal_person_check_list where id=" + _targetID); // 找到该指标

			if (target == null || target.length == 0) {
				throw new Exception("指标传入有误!");
			}
			String valuetype = target[0].getStringValue("valuetype");
			boolean zjCalc = true; // 这个指标是否公式已经直接计算效益工资。
			if ("计算".equals(valuetype) || "个岗计算".equals(valuetype) || "个部岗计算".equals(valuetype)) {
				zjCalc = false;
			}
			Boolean wgflg=tbutil.getTBUtil().getSysOptionBooleanValue("是否启动网格指标计算模式",false);
			HashVO target_post_vos[];
			HashVO [] vos=dmo.getHashVoArrayByDS(null,"select * from SAL_TARGET_CATALOG where id='"+target[0].getStringValue("catalogid")+"'");
			if(wgflg && vos[0].getStringValue("name").equals("网格指标")){
				target_post_vos= dmo.getHashVoArrayByDS(null, "select * from sal_person_check_post_wg where targetid=" + _targetID); // 找到该指标的被考核对象
			}else{
				target_post_vos= dmo.getHashVoArrayByDS(null, "select * from sal_person_check_post where targetid=" + _targetID); // 找到该指标的被考核对象
			}
			HashMap<String, HashVO> post_targetMap = new HashMap<String, HashVO>(); // 岗位类型名称和sal_person_check_post对应关系。
			List posttypes = new ArrayList();
			for (int i = 0; i < target_post_vos.length; i++) { // 得到所有被考核的人。
				String postids = target_post_vos[i].getStringValue("postid"); // 得到被考核岗位组的名称
				String posts[] = postids.split(";"); // 所有的岗位组名称
				for (int j = 0; j < posts.length; j++) {
					if (posts[j] != null && !posts[j].trim().equals("")) {
						posttypes.add(posts[j].trim());
						post_targetMap.put(posts[j].trim(), target_post_vos[i]);
					}
				}
			}
			HashVO allCheckedUsers[];
			if(wgflg && vos[0].getStringValue("name").equals("网格指标")){
//				allCheckedUsers = dmo.getHashVoArrayByDS(null, "select exc.ID wgid,exc.YEAR,exc.MONTH,exc.CREATTIME,exc.A,exc.B,exc.C,exc.D,exc.E,exc.F,exc.G,sal.id userid,sal.NAME,sal.SEX,sal.BIRTHDAY,sal.TELLERNO,sal.CARDID,sal.POSITION,sal.STATIONDATE,sal.STATIONRATIO,sal.AGE,sal.DEGREE,sal.UNIVERSITY,sal.SPECIALITIES,sal.POSTTITLE,sal.POSTTITLEAPPLYDATE,sal.POLITICALSTATUS,sal.CONTRACTDATE,sal.JOINWORKDATE,sal.JOINSELFBANKDATE,sal.WORKAGE,sal.SELFBANKAGE,sal.ONLYCHILDRENBTHDAY,sal.SELFBANKACCOUNT,sal.OTHERACCOUNT,sal.FAMILYACCOUNT,sal.PENSION,sal.HOUSINGFUND,sal.PLANWAY,sal.PLANRATIO,sal.ISUNCHECK,sal.FAMILYNAME,sal.MEDICARE,sal.TEMPORARY,sal.OTHERGLOD,sal.TECHNOLOGY,sal.STATIONKIND,sal.MAINDEPTID,sal.DEPTID,sal.DEPTNAME,sal.MAINSTATIONID,sal.MAINSTATION,sal.POSTSEQ,sal.DEPTSEQ,sal.LINKCODE,sal.DEPTCODE from EXCEL_TAB_85 exc left join v_sal_personinfo sal on exc.g=sal.code where exc.id in (" + tbutil.getInCondition(posttypes) + ")"); // [2020-5-11]找到所有需要考核的网格
				allCheckedUsers = dmo.getHashVoArrayByDS(null, "select exc.ID wgid,exc.YEAR,exc.MONTH,exc.CREATTIME,exc.A,exc.B,exc.C,exc.D,exc.E,exc.F,exc.G,sal.id userid,sal.NAME,sal.SEX,sal.BIRTHDAY,sal.TELLERNO,sal.CARDID,sal.POSITION,sal.STATIONDATE,sal.STATIONRATIO,sal.AGE,sal.DEGREE,sal.UNIVERSITY,sal.SPECIALITIES,sal.POSTTITLE,sal.POSTTITLEAPPLYDATE,sal.POLITICALSTATUS,sal.CONTRACTDATE,sal.JOINWORKDATE,sal.JOINSELFBANKDATE,sal.WORKAGE,sal.SELFBANKAGE,sal.ONLYCHILDRENBTHDAY,sal.SELFBANKACCOUNT,sal.OTHERACCOUNT,sal.FAMILYACCOUNT,sal.PENSION,sal.HOUSINGFUND,sal.PLANWAY,sal.PLANRATIO,sal.ISUNCHECK,sal.FAMILYNAME,sal.MEDICARE,sal.TEMPORARY,sal.OTHERGLOD,sal.TECHNOLOGY,sal.STATIONKIND,sal.MAINDEPTID,sal.DEPTID,sal.DEPTNAME,sal.MAINSTATIONID,sal.MAINSTATION,sal.POSTSEQ,sal.DEPTSEQ,sal.LINKCODE,sal.DEPTCODE from Excel_tab_85    exc left join v_sal_personinfo sal on exc.g=sal.code where exc.id in (" + tbutil.getInCondition(posttypes) + ")");
			}else{
				allCheckedUsers = dmo.getHashVoArrayByDS(null, "select t1.*,t2.shortname from v_sal_personinfo t1 left join pub_corp_dept t2 on t1.maindeptid = t2.id where  ( isuncheck ='N' or isuncheck is null)   and  stationkind in (" + tbutil.getInCondition(posttypes) + ")"); // 找到该岗位被考评的所有人
			}

			// 接下来构造假的score表中的数据。可以不用生存score内容。但是必须有必要的上传excel数据。

			HashVO scorevo[] = new HashVO[allCheckedUsers.length]; // 每人一条
			for (int i = 0; i < allCheckedUsers.length; i++) {
				if(wgflg && vos[0].getStringValue("name").equals("网格指标")){
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
					scorevo[i].setAttributeValue("factors", target[0].getStringValue("factors")); //过程中因子

					String weights = "";
					String planvalue = "";
					String groupid = "";
					String postids = "";
					String dw = target[0].getStringValue("unitvalue");// 单位
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
					scorevo[i].setAttributeValue("stationratio", allCheckedUsers[i].getStringValue("stationratio"));// 岗位系数
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
					scorevo[i].setAttributeValue("factors", target[0].getStringValue("factors")); //过程中因子

					String weights = "";
					String planvalue = "";
					String groupid = "";
					String postids = "";
					String dw = target[0].getStringValue("unitvalue");// 单位
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
					scorevo[i].setAttributeValue("stationratio", allCheckedUsers[i].getStringValue("stationratio"));// 岗位系数
					scorevo[i].setAttributeValue("stationkind", allCheckedUsers[i].getStringValue("stationkind"));

				}
			}

			// 开始计算指标的实际值
			int personNum = scorevo.length;
			for (int i = 0; i < scorevo.length; i++) {
				HashVO scoreVO = scorevo[i];
				String targetName = scoreVO.getStringValue("targetname"); // 得到指标名称
				String targetFactorName = targetName + "对象";
				String targetdef = scoreVO.getStringValue("targetdefine"); // 公式定义
				String getvalue = scoreVO.getStringValue("getvalue"); // 得到取值的公式
				String partvalue = scoreVO.getStringValue("partvalue"); // 参与计算值公式。
				String valueType = scoreVO.getStringValue("valuetype");
				String operationtype = scoreVO.getStringValue("operationtype"); // 计算方式
				String hisFactors = scoreVO.getStringValue("factors", ""); //所有需要记录下来的因子结果
				String factors[] = tbutil.split(hisFactors, ";");
				LinkedHashMap<String, String> factor_value_map = new LinkedHashMap<String, String>(); //存储因子计算的结果

				// ,加减
				StringBuffer sb = new StringBuffer();
				formulaUtil.putDefaultFactorVO("数字", getvalue, targetFactorName, null, "4位小数");
				// scoreVO.setAttributeValue("", value)
				Object currvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetFactorName), scoreVO, sb); // 得到实际值

				dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors); //
				// System.out.println("取值返回" + rtobj);
				Object money = 0;
				Object currpartvalue = null;
				if (!tbutil.isEmpty(partvalue)) {
					partvalue = stbutil.formulaReplaceX(partvalue, "x", "[" + targetFactorName + "]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[计划值]", "[传入数据." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[目标值]", "[传入数据." + "planedvalue]");
					partvalue = TBUtil.getTBUtil().replaceAll(partvalue, "[权重]", "[传入数据." + "weights]");
					formulaUtil.putDefaultFactorVO("数字", partvalue, targetName + "参与计算值", "", "2位小数");
					currpartvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "参与计算值"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors); //
				} else {
					currpartvalue = currvalue;
					formulaUtil.putDefaultFactorVO("数字", partvalue, targetName + "参与计算值", "", "2位小数");
					formulaUtil.putDefaultFactorValue(targetName + "参与计算值", currpartvalue);
				}

				if (("直接".equals(valueType) || "岗位系数计算".equals(valueType)) && targetdef != null && !targetdef.trim().equals("")) { // 通过公式定义计算效益工资
					targetdef = stbutil.formulaReplaceX(targetdef, "x", "[" + targetName + "参与计算值]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[计划值]", "[传入数据." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[目标值]", "[传入数据." + "planedvalue]");
					targetdef = TBUtil.getTBUtil().replaceAll(targetdef, "[权重]", "[传入数据." + "weights]");
					formulaUtil.putDefaultFactorVO("数字", targetdef, targetName + "得分计算", "", "2位小数");
					money = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetName + "得分计算"), scoreVO, sb);
					dealAfterCalTargetFactorValue(formulaUtil, factor_value_map, factors); //
				}
				// System.out.println(obj);
				if (currvalue == null || "".equals(String.valueOf(currvalue))) {
					currvalue = 0;
				}
				scoreVO.setAttributeValue("realvalue", String.valueOf(currvalue)); // 真正的值
				if (currpartvalue == null || "".equals(String.valueOf(currpartvalue))) {
					currpartvalue = 0;
				}
				scoreVO.setAttributeValue("checkscore", String.valueOf(currpartvalue));
				if ("减".equals(operationtype)) {
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
				setRemoteActionSchedule("员工定量指标演算" + _targetID, "员工定量指标演算", "员工定量指标进度:" + (i + 1) + "/" + (personNum));
			}
			removeRemoteActionSchedule("员工定量指标演算" + _targetID, "员工定量指标演算");
			if (zjCalc) {
				return scorevo;
			}
			return calc_comm_p_Pay(scorevo, "", _checkdate, true);
		} catch (Exception e) {
			removeRemoteActionSchedule("员工定量指标演算" + _targetID, "员工定量指标演算");
			throw e;
		}
	}

	/*
	 * 把员工的定性考核指标每个指标得分计算出存起来。
	 */
	public void onePlanCalcAllUserEveryDXTargetScore(String logid) throws Exception {
		StringBuffer sb = new StringBuffer("select * from sal_person_check_score where logid=" + logid + " and targettype='员工定性指标' and scoretype='手动打分' ");
		HashMap user_level = getDmo().getHashMapBySQLByDS(null, "select id, stationratio from sal_personinfo where stationratio is not null");
		// 参与考核的人，得有岗位系数
		HashMap error = getDmo().getHashMapBySQLByDS(null, "select name, '1' from  v_sal_personinfo where ( isuncheck ='N' or isuncheck is null)  and (stationratio is null or stationratio='')");
		HashVO[] allscores = getDmo().getHashVoArrayByDS(null, sb.toString()); // 所有打分记录
		List allscorers = new ArrayList(); // 所有的评分人
		// 指标-组-评分人记录vo
		HashMap[] tempmap = getAllTempMap_Person(allscores); // 就行分组的map
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
				// HashMap<String, String>(); // 指标-组间权重方式

				List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
				List<BillCellItemVO> item0 = new ArrayList<BillCellItemVO>();
				// item0.add(getBillTitleCellItemVO("权重"));
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
							if ("平均".equals(scorerweightstype)) {
								usersumweight = usersumweight.add(new BigDecimal("1"));
								usersumscore = usersumscore.add(new BigDecimal(scorevos.get(s).getStringValue("checkscore", "0")));
							} else {
								usersumweight = usersumweight.add(new BigDecimal(user_level.get(scorevos.get(s).getStringValue("scoreuser")) + ""));
								usersumscore = usersumscore.add(new BigDecimal(scorevos.get(s).getStringValue("checkscore", "0")).multiply(new BigDecimal(user_level.get(scorevos.get(s).getStringValue("scoreuser")) + "")));
							}
						}
						if ("平均".equals(targetweightstype)) {
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
				setRemoteActionSchedule(logid, "指标计算", "正在计算员工各项定性指标得分" + index + "/" + size);
				index++;
			}
			sqllist.add(0, "delete from sal_person_check_target_score where logid = " + logid);
			getDmo().executeBatchByDS(null, sqllist);
			removeRemoteActionSchedule(logid, "指标计算");
		} catch (Exception ex) {
			removeRemoteActionSchedule(logid, "指标计算");
			throw ex;
		}
	}

	/*
	 * 将所有评分记录按照被考核人分组，然后每个被考核人的某个记录按照评分组进行分类，然后再根据评分人进行分组
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
				checkeduserid = allscores[i].getStringValue("checkeduser", ""); // 被考核人
				String targetID = allscores[i].getStringValue("targetid");
				if ("员工定量指标".equals(allscores[i].getStringValue("targettype", ""))) { // 定量指标需要一个新的hashmap直接是userid-List
					if (user_quantifyscore.containsKey(checkeduserid)) {
						user_quantifyscore.get(checkeduserid).add(allscores[i]);
					} else {
						List<HashVO> scorers = new ArrayList<HashVO>();
						scorers.add(allscores[i]);
						user_quantifyscore.put(checkeduserid, scorers);
					}
				} else { // 员工定性指标、高管定性指标、部门定性指标 就是之前的逻辑
					checkedusername = allscores[i].getStringValue("checkedusername", ""); // 被考核人
					if (checkedusername.equals("姜启敏")) {
						// System.out.println(">>");
					}
					groupid = allscores[i].getStringValue("scoreusertype", ""); // 评分组
					scorer = allscores[i].getStringValue("scoreuser", ""); // 评分人
					allscorers.add(scorer);
					groupweightstyle = allscores[i].getStringValue("groupweightstype", ""); // 组内人员的权重计算方式
					groupweight = allscores[i].getStringValue("groupweights", ""); // 评分组的权重
					userweightstyle = allscores[i].getStringValue("scorerweightstype", ""); // 评分组之间的权重方式
					if (!"".equals(checkeduserid) && !"".equals(groupid) && !"".equals(userweightstyle)) {
						LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>> target_group = new LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>();
						boolean have = false;
						if ("高管垂直".equals(allscores[i].getStringValue("checktype"))) { // 如果考核是高管垂直。
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
							LinkedHashMap<String, List<HashVO>> group_scores = new LinkedHashMap<String, List<HashVO>>(); // 一个评分人对被考核人的所有打分结果
							List<HashVO> scorers = new ArrayList<HashVO>();
							scorers.add(allscores[i]);
							group_scores.put(groupid, scorers);
							target_group.put(targetID, group_scores); // sal_person_check_plan_scorer的ID，评分组
							if ("高管垂直".equals(allscores[i].getStringValue("checktype"))) {
								user_score_ggcc.put(checkeduserid, target_group); // 被考核人
							} else {
								user_score.put(checkeduserid, target_group); // 被考核人
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
		maps[3] = user_quantifyscore; // 应该没有
		maps[4] = user_score_ggcc; // 目前没有
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
		if ("系统指标".equals(type)) {
			String targetid = warnvo.getStringValue("targetid");
			if (tbutil.isEmpty(targetid)) {
				throw new Exception("预警配置中序号为[" + _warnConfigid + "]的配置信息的指标字段值为空.");
			}
			HashVO targetscores[] = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where logid = " + _logid + " and targetid = " + targetid);
			if (targetscores == null || targetscores.length == 0) {
				return null;
			}
			String formulatype = warnvo.getStringValue("formulatype");
			float value = 0f;
			boolean isnull = true;
			if (targetscores == null || targetscores.length == 0) {
				warnvo.setAttributeValue("name", warnvo.getAttributeValue("name") + "(没数据)");
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
			if ("求平均".equals(formulatype)) {
				value = value / targetscores.length;
				value = new BigDecimal(value).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			}
			return getWarnVO(warnvo, value, isnull);
		} else if ("自定义".equals(type)) {
			// 自定义的需要执行公式。
			String formula = warnvo.getStringValue("formula"); //
			if (formula == null) {
				warnvo.setAttributeValue("name", warnvo.getAttributeValue("name") + "(未配置公式)");
				return getWarnVO(warnvo, 0, true);
			} else {
				formulaUtil.putDefaultFactorVO("数字", formula, "预警计算结果", "", "2位小数");
				HashVO factorvo = formulaUtil.getFoctorHashVO("预警计算结果");
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
		warnvo.setAttributeValue("标题", name); // 成功防范报告及时率
		warnvo.setAttributeValue("X轴", warnvo.getStringValue("unitvalue", "")); //
		if (!isnull) {
			warnvo.setAttributeValue("实际值", _currvalue); //	
		}
		if (_currvalue <= w1) {
			warnvo.setAttributeValue("最小值", _currvalue); //
		} else {
			warnvo.setAttributeValue("最小值", w1); //
		}
		warnvo.setAttributeValue("触发值", w2); //
		warnvo.setAttributeValue("法定值", w3); //
		warnvo.setAttributeValue("提示", ""); //
		if (_currvalue >= w4) {
			warnvo.setAttributeValue("提示", "系统设置的最大值为" + "" + w4 + ",现被调整.");
			warnvo.setAttributeValue("最大值", _currvalue); //	
		} else {
			warnvo.setAttributeValue("最大值", w4); //
		}
		warnvo.setAttributeValue("背景色", "FFBD9D");
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
		String dw = "部门计价指标".equals(targetType) ? "薪酬" : "得分";
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
				String targetFactorName = targetName + "对象";
				util.putDefaultFactorVO("数字", formulatext, targetFactorName, "", "4位小数");
				Object rtobj = util.onExecute(util.getFoctorHashVO(targetFactorName), scorevo, sb);
				define = define.replaceAll("x", "[" + targetFactorName + "]");
				define = TBUtil.getTBUtil().replaceAll(define, "[计划值]", "[传入数据." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[目标值]", "[传入数据." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[权重]", "[传入数据." + "weights]");
				util.putDefaultFactorVO("数字", define, targetName + "得分计算", "", "4位小数");
				// 把计划值改掉
				scorevo.setAttributeValue("planedvalue", planvalue);
				scorevo.setAttributeValue("weights", weights);
				Object obj = util.onExecute(util.getFoctorHashVO(targetName + "得分计算"), scorevo, sb);
				double value = 0;
				if (obj instanceof Number) {
					try {
						value = Double.parseDouble(String.valueOf(obj));
					} catch (Exception e) {
						throw e;
					}
				}

				executeValueSB.append("该指标【" + scorevo.getStringValue("checkeddeptname") + "】实际值完成值【" + rtobj + "】  " + "最终" + dw + value + " \r\n" + sb + "\r\n");
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
	 * zzl[2020-5-14] 部门计算结果返回HashVo[]
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
		String dw = "部门计价指标".equals(targetType) ? "薪酬" : "得分";
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
				String targetFactorName = targetName + "对象";
				util.putDefaultFactorVO("数字", formulatext, targetFactorName, "", "4位小数");
				Object rtobj = util.onExecute(util.getFoctorHashVO(targetFactorName), scorevo, sb);
				define = define.replaceAll("x", "[" + targetFactorName + "]");
				define = TBUtil.getTBUtil().replaceAll(define, "[计划值]", "[传入数据." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[目标值]", "[传入数据." + "planedvalue]");
				define = TBUtil.getTBUtil().replaceAll(define, "[权重]", "[传入数据." + "weights]");
				util.putDefaultFactorVO("数字", define, targetName + "得分计算", "", "4位小数");
				// 把计划值改掉
				scorevo.setAttributeValue("planedvalue", planvalue);
				scorevo.setAttributeValue("weights", weights);
				Object obj = util.onExecute(util.getFoctorHashVO(targetName + "得分计算"), scorevo, sb);
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
//				executeValueSB.append("该指标【" + scorevo.getStringValue("checkeddeptname") + "】实际值完成值【" + rtobj + "】  " + "最终" + dw + value + " \r\n" + sb + "\r\n");
			}
		}
		vos = new HashVO [list.size()];
		for(int i=0;i<list.size();i++){
			vos[i]=list.get(i);
		}
		return vos;
	}
	/**
	 * 通过定时器，自动计算部分定量指标实际效益工资。
	 * 传入日期格式 yyyy-MM-dd
	 * 返回值0为没有需要计算的指标
	 * 		1为成功
	 */
	public int autoCalcPersonDLTargetByTimer(String jobid, String datadate) throws Exception {
		String checkdate = datadate.substring(0, 4) + "-" + datadate.substring(5, 7);
		String[] autoCalcTargetIDs = getDmo().getStringArrayFirstColByDS(null, "select id from sal_person_check_list where state='参与考核' and alwaysview='Y'"); //找出所有需要计算的指标
		if (autoCalcTargetIDs.length == 0) {
			return 0;
		}
		List list = new ArrayList();
		DeleteSQLBuilder delsql = new DeleteSQLBuilder("sal_person_check_auto_score"); //
		delsql.setWhereCondition(" checkdate='" + checkdate + "' and datadate='" + datadate + "'");
		list.add(delsql);//如果改日期已经执行过,先删除掉。
		for (int i = 0; i < autoCalcTargetIDs.length; i++) {
			HashVO[] rtvos = calcOnePersonTarget_P_Money(autoCalcTargetIDs[i], checkdate); //计算返回的值
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
		getDmo().executeBatchByDS(null, list); //只要sql拼接完成，应该不会报错。所有不单独开启事务。
		return 1;
	}

}
