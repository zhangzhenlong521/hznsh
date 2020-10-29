package cn.com.pushworld.salary.bs;

import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import javax.swing.SwingUtilities;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.bs.mdata.MetaDataDMO;
import cn.com.infostrategy.bs.sysapp.SysAppDMO;
import cn.com.infostrategy.bs.sysapp.cometpush.ServerPushToClientServlet;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.sysapp.other.ImportExcelDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.*;
import cn.com.infostrategy.to.report.ReportUtil;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.pushworld.salary.bs.dinterface.DataInterfaceDMO;
import cn.com.pushworld.salary.bs.dmo.PostDutyCheckDMO;
import cn.com.pushworld.salary.bs.dmo.TargetDMO;
import cn.com.pushworld.salary.bs.feedback.FeedBackDMO;
import cn.com.pushworld.salary.bs.report_.ReportService;
import cn.com.pushworld.salary.bs.report_.ReportServiceImpl;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.to.word.UseCellTempletParseUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;

public class SalaryServiceImpl implements SalaryServiceIfc {
	private CommDMO dmo = null;
	private SysAppDMO sdmo = null;
	private MetaDataDMO mdmo = null;
	private TBUtil tb = null;
	private String scoreRolename = null;
	private String scoreFinishState = ";已提交;申请修改;";
	private BigDecimal[] allrights = null;
	private SalaryTBUtil stbutil = new SalaryTBUtil();

	public TBUtil getTb() {
		if (tb == null) {
			tb = new TBUtil();
		}
		return tb;
	}

	private String getScoreRoleName() {
		if (scoreRolename == null) {
			scoreRolename = getTb().getSysOptionStringValue("部门考核评分人角色名", "部门考核评分人");
		}
		return scoreRolename;
	}

	public CommDMO getDmo() {
		if (dmo == null) {
			dmo = new CommDMO();
		}
		return dmo;
	}

	public MetaDataDMO getMdmo() {
		if (mdmo == null) {
			mdmo = new MetaDataDMO();
		}
		return mdmo;
	}

	public SysAppDMO getSdmo() {
		if (sdmo == null) {
			sdmo = new SysAppDMO();
		}
		return sdmo;
	}

	public String[][] getTableItemAndDescr(String table) throws Exception {
		return new InstallDMO().getAllIntallTabColumnsDescr(table);
	}

	/**
	 * 重要方法 验证指标以及每个部门评分人是否合法 最终返回一个部门id-人员id（评分人） 的hashMap
	 * 
	 * @return
	 */
	public HashMap<String, Object> checkViladate(String month) {
		return checkViladate(month, null, null);
	}

	public HashMap<String, Object> checkViladate(String month, String[] deptTargetids, String[] quantifyTargetids) {
		HashMap<String, Object> rtnmap = new HashMap<String, Object>();
		try {
			rtnmap.put("res", "success");
			// 这个月的考核表要考核哪些频率类型的指标
			String plv = getDmo().getStringValueByDS(null, "select code from PUB_COMBOBOXDICT where type ='薪酬_月度考核频率' and id='" + month.substring(5) + "'");
			// 生成此次考核表的所有指标
			StringBuffer sb1 = new StringBuffer("select * from sal_target_list where state='参与考核' and type='部门定性指标' ");
			if (deptTargetids != null) {
				sb1.append(" and id in (" + getTb().getInCondition(deptTargetids) + ")");
			}
			HashVO[] targets = getDmo().getHashVoArrayByDS(null, sb1.toString());
			rtnmap.put("plv", plv);
			rtnmap.put("month", month);
			if (targets != null && targets.length > 0) {
				StringBuffer sb = new StringBuffer(); // 所有需要评分的部门id
				StringBuffer sb2 = new StringBuffer();
				StringBuffer sb3 = new StringBuffer();
				for (int i = 0; i < targets.length; i++) {
					// 被考核部门
					if (!"".equals(targets[i].getStringValue("checkeddept", ""))) {
						sb3.append(targets[i].getStringValue("checkeddept", "") + ";");
					}
					// 主考核部门
					if (!"".equals(targets[i].getStringValue("rdept", ""))) {
						sb.append(targets[i].getStringValue("rdept", "") + ";");
					}
					// 参与考核部门
					if (!"".equals(targets[i].getStringValue("pdept", ""))) {
						sb.append(targets[i].getStringValue("pdept", "") + ";");
					}
					// 这里以前存人后来改成岗位
					if (!"".equals(targets[i].getStringValue("pleader", ""))) {
						sb2.append(targets[i].getStringValue("pleader", "") + ";");
					}
					if (!"".equals(targets[i].getStringValue("rleader", ""))) {
						sb2.append(targets[i].getStringValue("rleader", "") + ";");
					}
				}
				StringBuffer sbinfo = new StringBuffer();
				// 接下来就是要验证这些部门下是否有"部门考核评分人"这个角色的人,一个部门下只能有1个人否则就不能启动
				String roleid = getDmo().getStringValueByDS(null, "select id from pub_role where name='" + getScoreRoleName() + "'");
				if (roleid == null || "".equals(roleid)) {
					rtnmap.put("res", "fail");
					rtnmap.put("msg", "没有定义角色\"" + getScoreRoleName() + "\",操作失败!");
				} else {
					HashMap<String, String> map = getDmo().getHashMapBySQLByDS(null, "select deptid, userid from v_pub_user_post_2 where userid in (select userid from pub_user_role where roleid='" + roleid + "' ) and deptid in (" + getTb().getInCondition(sb.toString()) + ") group by deptid, userid", true);
					HashMap<String, String> map_name = getDmo().getHashMapBySQLByDS(null, "select id, name from pub_corp_dept where id in (" + getTb().getInCondition(sb.toString()) + "," + getTb().getInCondition(sb3.toString()) + ")");
					// 岗位下都有那些人
					HashMap<String, String> map_post_name = getDmo().getHashMapBySQLByDS(null, "select id,name from pub_post where id in (" + getTb().getInCondition(sb2.toString()) + ")");
					HashMap<String, String> map_post_user = getDmo().getHashMapBySQLByDS(null, "select p.postid, u.id from pub_user u left join pub_user_post p on u.id=p.userid where p.userid is not null and p.userdept is not null and p.postid in (" + getTb().getInCondition(sb2.toString()) + ")");
					if (map_post_user != null && map_post_user.size() > 0) {
						String[] keys = (String[]) map_post_user.keySet().toArray(new String[0]);
						for (int j = 0; j < map_post_user.size(); j++) {
							if (map_post_user.get(keys[j]).toString().indexOf(";") >= 0) {
								String[] userids = map_post_user.get(keys[j]).toString().split(";");
								HashMap<String, String> param = new HashMap();
								for (int k = 0; k < userids.length; k++) {
									if (userids[k] != null && !"".equals(userids[k])) {
										param.put(userids[k], "");
									}
								}
								if (param.size() > 1) {
									if (sbinfo.length() == 0) {
										sbinfo.append(map_post_name.get(keys[j]));
									} else {
										sbinfo.append("、" + map_post_name.get(keys[j]));
									}
								}
							}
						}
					}

					if (map != null && map.size() > 0) {
						StringBuffer sb0 = new StringBuffer();
						StringBuffer sbn = new StringBuffer();
						String[] keys = (String[]) map.keySet().toArray(new String[0]);
						for (int j = 0; j < map.size(); j++) {
							if (map.get(keys[j]) == null || "".equals(map.get(keys[j]))) {
								if (sb0.length() == 0) {
									sb0.append(map_name.get(keys[j]));
								} else {
									sb0.append("、" + map_name.get(keys[j]));
								}
							} else if (map.get(keys[j]).toString().indexOf(";") >= 0) {
								if (sbn.length() == 0) {
									sbn.append(map_name.get(keys[j]));
								} else {
									sbn.append("、" + map_name.get(keys[j]));
								}
							}
						}
						if (sb0.length() > 0 || sbn.length() > 0) {
							rtnmap.put("res", "fail");
							StringBuffer msg = new StringBuffer();
							if (sb0.length() > 0) {
								msg.append("部门【" + sb0 + "】没有设置角色【" + getScoreRoleName() + "】");
							}
							if (sbn.length() > 0) {
								if (msg.length() > 0) {
									msg.append("\r\n部门【" + sbn + "】设置了多个【" + getScoreRoleName() + "】角色的人员!");
								} else {
									msg.append("部门【" + sbn + "】设置了多个【" + getScoreRoleName() + "】角色的人员!");
								}
							}

							if (sbinfo.length() > 0) {
								msg.append("岗位【" + sbinfo + "】设置了多个人员!");
							}
							rtnmap.put("msg", msg);
						} else {
							if (sbinfo.length() > 0) {
								rtnmap.put("msginfo", "岗位【" + sbinfo + "】设置了多个人员!");
							}
							rtnmap.put("res", "success");
							rtnmap.put("date", map);
							rtnmap.put("leader", map_post_user);
							rtnmap.put("datename", map_name);
							rtnmap.put("deptTargetids", deptTargetids);
						}
					} else {
						rtnmap.put("date", map);
						rtnmap.put("leader", map_post_user);
						rtnmap.put("datename", map_name);
						rtnmap.put("deptTargetids", deptTargetids);
					}
				}
			}
			// 验证一下部门定量指标
			// 只验证一下一个定量指标可能多次考核同一部门的现象
			StringBuffer sb2 = new StringBuffer("select id,name from sal_target_list where state='参与考核' and (type='部门定量指标' or type='部门计价指标')"); //  and checkcycle in(" + tb.getInCondition(plv) + ") 定量的不涉及频率了每次都生成的
			if (quantifyTargetids != null) {
				sb2.append(" and id in (" + getTb().getInCondition(quantifyTargetids) + ")");
			}
			HashMap targets_name = getDmo().getHashMapBySQLByDS(null, sb2.toString());
			HashMap targets_deptids = getDmo().getHashMapBySQLByDS(null, "select targetid, deptid from sal_target_checkeddept where targetid in (" + getTb().getInCondition((String[]) targets_name.keySet().toArray(new String[0])) + ")", true);
			if (targets_deptids != null && targets_deptids.size() > 0) {
				String[] targetids = (String[]) targets_deptids.keySet().toArray(new String[0]);
				String deptids = null;
				List<String> questiondept = new ArrayList<String>();
				LinkedHashMap<String, HashMap<String, String>> questiontarget = new LinkedHashMap<String, HashMap<String, String>>();
				for (int q = 0; q < targetids.length; q++) {
					HashMap<String, String> ishavecheck_ = new HashMap<String, String>(); // 一个定量指标只能考核同一部门一次
					if (targets_deptids.get(targetids[q]) != null) {
						deptids = targets_deptids.get(targetids[q]) + "";
						String[] deptid = getTb().split(deptids, ";");
						if (deptid != null && deptid.length > 0) {
							for (int d = 0; d < deptid.length; d++) {
								if (deptid[d] != null && !"".equals(deptid[d])) {
									questiondept.add(deptid[d]);
									if (ishavecheck_.containsKey(deptid[d])) {
										if (questiontarget.containsKey(targetids[q])) {
											questiontarget.get(targetids[q]).put(deptid[d], "");
										} else {
											HashMap<String, String> temp = new HashMap<String, String>();
											temp.put(deptid[d], "");
											questiontarget.put(targetids[q], temp);
										}
									}
									ishavecheck_.put(deptid[d], "");
								}
							}
						}
					}
				}
				HashMap dept_name = getDmo().getHashMapBySQLByDS(null, "select id,name from pub_corp_dept where id in(" + getTb().getInCondition(questiondept) + ")");
				if (questiontarget.size() > 0) {
					StringBuffer sb = new StringBuffer();
					String[] qtargetids = questiontarget.keySet().toArray(new String[0]);
					for (int q = 0; q < questiontarget.size(); q++) {
						HashMap<String, String> dept = questiontarget.get(qtargetids[q]);
						if (dept == null) {
							continue;
						}
						String[] perdeptids = dept.keySet().toArray(new String[0]);
						sb.append("\r\n定量指标【" + targets_name.get(qtargetids[q]) + "】多次设置:");
						if (dept != null && dept.size() > 0) {
							for (int d = 0; d < dept.size(); d++) {
								if (d == 0) {
									sb.append(dept_name.get(perdeptids[d]));
								} else {
									sb.append("、" + dept_name.get(perdeptids[d]));
								}
							}
						}
						sb.append("为被考核部门!");
					}
					if (sb.length() > 0) {
						rtnmap.put("res", "fail");
						if (rtnmap.containsKey("msg")) {
							rtnmap.put("msg", rtnmap.get("msg") + sb.toString());
						} else {
							rtnmap.put("msg", sb.toString());
						}
					}
				} else {
					rtnmap.put("datename2", dept_name);
					rtnmap.put("quantifyTargetids", quantifyTargetids);
				}
			}
			if ((targets == null || targets.length <= 0) && (targets_deptids == null || targets_deptids.size() <= 0)) { //没有指标也可以考
				//				rtnmap.put("res", "fail");
				//				rtnmap.put("msg", "没有找到需要考核的指标,操作失败!");
			}
		} catch (Exception e) {
			rtnmap.put("res", "error");
			e.printStackTrace();
			WLTLogger.getLogger(SalaryServiceImpl.class).error(e);
		}
		return rtnmap;
	}

	/**
	 * 重要方法 生成评分表数据 此方法包含了部门、员工的评分表生成 此方法包含了 更新的逻辑，更新与生成是一个方法 关于频率的描述:
	 * 定性指标:只负责生成频率内的和频率外（复制上个月的得分） 定量:全部生成空的
	 */
	public void createScoreTable(HashMap param) throws Exception {
		List<String> sqls = new ArrayList<String>();
		String month = param.get("month").toString();
		String loginuserid = param.get("loginuserid").toString();
		String logindeptid = param.get("logindeptid").toString();
		String plv = param.get("plv").toString();
		String isupdate = param.get("isupdate") + ""; // 是否是更新操作
		HashMap<String, HashVO> ishave = new HashMap<String, HashVO>(); // 被考核人_考核人_指标_指标类型
		// 唯一的存在
		param.put("dept", ishave);
		if ("Y".equals(isupdate)) { // 如果是更新的操作 则将旧数据放到map里判断是否已经存在
			HashVO[] vs = null;
			if ("Y".equals(param.get("isdlonly") + "")) {
				vs = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where checkdate='" + month + "' and (targettype='部门定量指标' or targettype='部门计价指标')");
			} else {
				vs = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where checkdate='" + month + "'");
			}
			for (int v = 0; v < vs.length; v++) {
				if (ishave != null && vs[v] != null) {
					ishave.put(vs[v].getStringValue("checkeddept") + "_" + vs[v].getStringValue("scoreuser", "") + "_" + vs[v].getStringValue("targetid", "") + "_" + vs[v].getStringValue("targettype"), vs[v]);
				}
			}
		}
		HashMap dept_usermap = (HashMap) param.get("date");
		HashMap leader_deptmap = (HashMap) param.get("leader");
		HashMap post_usermap = (HashMap) param.get("leader");
		HashMap dept_namemap = (HashMap) param.get("datename");
		HashMap dept_namemap2 = (HashMap) param.get("datename2");
		String corpname = SystemOptions.getStringValue("工资结算最小单位", "行社");
		String logid = null;
		if (!"Y".equals(isupdate)) {
			InsertSQLBuilder logisb = new InsertSQLBuilder("sal_target_check_log");
			logid = getDmo().getSequenceNextValByDS(null, "S_SAL_TARGET_CHECK_LOG");
			logisb.putFieldValue("id", logid);
			logisb.putFieldValue("name", getSdmo().getLoginUserParentCorpItemValueByType(corpname, "集团", "name") + month + "月份考核评分");
			logisb.putFieldValue("checkdate", month);
			logisb.putFieldValue("status", "考核中");
			logisb.putFieldValue("creator", loginuserid);
			logisb.putFieldValue("createdate", getTb().getCurrDate());
			logisb.putFieldValue("createcorp", getSdmo().getLoginUserParentCorpItemValueByType(corpname, "集团", "id"));
			logisb.putFieldValue("zbtype",param.get("zbtype").toString());
			param.put("logid", logid);
			sqls.add(logisb.getSQL());
		} else {
			logid = getDmo().getStringValueByDS(null, "select id from sal_target_check_log where checkdate='" + month + "'");
			param.put("logid", logid);
		}
		// 处理部门定性指标
		if (!"Y".equals(param.get("isdlonly") + "")) {
			createScoreTable_DeptTarget(param, sqls);
		}
		// 处理部门定量指标
		// 所有生成的打分记录中的评分人默认都是-99999 特殊处理
		HashVO[] targetsvos = getDmo().getHashVoArrayByDS(null, "select * from sal_target_list where state='参与考核' and (type='部门定量指标' or type='部门计价指标' )"); // and
		// checkcycle
		// in(" + tb.getInCondition(plv) + ")
		HashMap<String, HashVO> targets_vo = new HashMap<String, HashVO>();
		for (int t = 0; t < targetsvos.length; t++) {
			targets_vo.put(targetsvos[t].getStringValue("id"), targetsvos[t]);
		}
		HashVO[] targets_deptids = getDmo().getHashVoArrayByDS(null, "select targetid, deptid, planedvalue, id from sal_target_checkeddept where targetid in (" + getTb().getInCondition((String[]) targets_vo.keySet().toArray(new String[0])) + ")", true);
		if (targets_deptids != null && targets_deptids.length > 0) {
			String deptids = null;
			String targetids = null;
			String planedvalue = null;
			String groupid = null;
			String checkeduser = null;
			String scoreuser = null;
			String targetid = null;
			String targettype = null;
			for (int q = 0; q < targets_deptids.length; q++) {
				if (targets_deptids[q].getStringValue("deptid") != null) {
					deptids = targets_deptids[q].getStringValue("deptid");
					targetids = targets_deptids[q].getStringValue("targetid");
					planedvalue = targets_deptids[q].getStringValue("planedvalue");
					groupid = targets_deptids[q].getStringValue("id");
					String[] deptid = getTb().split(deptids, ";");
					if (deptid != null && deptid.length > 0) {
						for (int d = 0; d < deptid.length; d++) {
							if (deptid[d] != null && !"".equals(deptid[d])) {
								if (dept_namemap2.get(deptid[d]) == null) {
									continue;
								}
								checkeduser = deptid[d];
								scoreuser = "-99999";
								targetid = targets_vo.get(targetids).getStringValue("id");
								targettype = targets_vo.get(targetids).getStringValue("type");
								String key = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
								if ("Y".equals(isupdate) && ishave != null && ishave.containsKey(key)) {
									targets_vo.get(targetids).setAttributeValue("planedvalue", planedvalue);
									targets_vo.get(targetids).setAttributeValue("getvalue", targets_vo.get(targetids).getStringValue("getvalue"));
									if (compareIsCanUpdateDept(targets_vo.get(targetids), ishave.get(key)) || !groupid.equals(ishave.get(key).getStringValue("groupid", ""))) {
										UpdateSQLBuilder isb = new UpdateSQLBuilder("sal_dept_check_score");
										isb.setWhereCondition(" id=" + ishave.get(key).getStringValue("id"));
										copyProperty2(targets_vo.get(targetids), isb, "-99999", deptid[d], dept_namemap2.get(deptid[d]) + "", month, logid, ishave.get(key).getStringValue("checkdratio", "a"), "", logindeptid);
										isb.putFieldValue("planedvalue", planedvalue);
										isb.putFieldValue("getvalue", targets_vo.get(targetids).getStringValue("getvalue"));
										isb.putFieldValue("reportformula", targets_vo.get(targetids).getStringValue("reportformula", ""));
										isb.putFieldValue("groupid", groupid);
										sqls.add(isb.getSQL());
									}
									ishave.remove(key);
								} else {
									InsertSQLBuilder isb = new InsertSQLBuilder("sal_dept_check_score");
									isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_DEPT_CHECK_SCORE", 5000));
									copyProperty(targets_vo.get(targetids), isb, "-99999", deptid[d], dept_namemap2.get(deptid[d]) + "", month, logid, "", logindeptid);
									isb.putFieldValue("planedvalue", planedvalue);
									isb.putFieldValue("getvalue", targets_vo.get(targetids).getStringValue("getvalue"));
									isb.putFieldValue("reportformula", targets_vo.get(targetids).getStringValue("reportformula", ""));
									isb.putFieldValue("groupid", groupid);
									sqls.add(isb.getSQL());
								}
							}
						}
					}
				}
			}
		}

		/**
		 * 剩下的就是需要删除的评分项
		 */
		if (ishave != null && ishave.size() > 0) {
			String[] keys = (String[]) ishave.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_dept_check_score");
				dsb.setWhereCondition("id=" + ishave.get(keys[i]).getStringValue("id"));
				System.out.println(ishave.get(keys[i]).getStringValue("id"));
				sqls.add(dsb.getSQL());
			}
		}

		// 处理人员考核
		createScoreTable_Person(sqls, param);

		//创建岗责评价指标评分表
		createPostDutyScoreTable(sqls, param);
		long li_start = System.currentTimeMillis();
		getDmo().executeBatchByDS(null, sqls, false, false); //不输出sql，也不判断sql日志。
		System.out.println("结束提交sql,共耗时：" + (System.currentTimeMillis() - li_start) + ",提交" + sqls.size() + "条sql");
	}

	public void createPostDutyScoreTable(HashMap param) throws Exception {
		List<String> sqls = new ArrayList<String>();
		this.createPostDutyScoreTable(sqls, param);
		getDmo().executeBatchByDS(null, sqls);
	}

	/**
	 * 创建岗责评价指标评分表
	 * 根据岗位职责指标，系统取值、手工录入，系统取值：需要考核前上传excel数据表，手工录入，根据考核策略，生成打分表。
	 * @param sqls
	 * @param param
	 */
	private void createPostDutyScoreTable(List<String> sqls, HashMap param) throws Exception {
		new PostDutyCheckDMO().createPostDutyScoreTable(sqls, param);
	}

	/**
	 * 将生成部门评分表的处理部门定性指标的 代码挖出来在更新评分表的时候可以复用 主要逻辑是根据垂直R、垂直P、平行R、平行P分别处理
	 * 同时考核人、被考核部门、指标作为唯一的key来过滤已经有了就不进行再次评分表的插入 如果是更新的话 一
	 * 考核人、被考核部门、指标、指标类型作为唯一key来判断 如果有了就对比一些字段决定是否进行更新
	 * 如果没有就进行插入操作，插入操作进行是否沿用的逻辑处理
	 * 
	 * @throws Exception
	 */
	public void createScoreTable_DeptTarget(HashMap param, List<String> sqls) throws Exception {
		String month = param.get("month").toString();

		String isupdate = param.get("isupdate") + "";
		HashMap<String, HashVO> ishave = (HashMap<String, HashVO>) param.get("dept");
		String checkeduser = null;
		String scoreuser = null;
		String targetid = null;
		String targettype = null;

		String loginuserid = param.get("loginuserid").toString();
		String logindeptid = param.get("logindeptid").toString();
		String plv = param.get("plv").toString();
		HashMap dept_usermap = (HashMap) param.get("date");
		HashMap leader_deptmap = (HashMap) param.get("leader");
		HashMap post_usermap = (HashMap) param.get("leader");
		HashMap dept_namemap = (HashMap) param.get("datename");
		HashMap dept_namemap2 = (HashMap) param.get("datename2");
		String logid = param.get("logid").toString();
		// HashMap detailMap = new HashMap();
		StringBuffer sb1 = new StringBuffer("select * from sal_target_list where state='参与考核' and type='部门定性指标' and checkcycle in(" + getTb().getInCondition(plv) + ")");

		HashVO[] targets = getDmo().getHashVoArrayByDS(null, sb1.toString());
		HashMap<String, String> ishavecheck = new HashMap<String, String>();

		// 后来定性指标增加了一个是否沿用的字段，就是说这次考核本不应该对这个指标进行打分的话
		// 就沿用之前最新的结果，即我要将那些本次不应该考核（频率）的指标查出来，直接复制上个月的结果到这次考核中
		HashVO[] targets_cuse = getDmo().getHashVoArrayByDS(null, "select * from sal_target_list where state='参与考核' and type='部门定性指标' and checkcycle not in(" + getTb().getInCondition(plv) + ") and checkcycle is not null"); // and
		// iscuse='Y'
		HashMap<String, HashVO> cusetargetid_vo = new HashMap<String, HashVO>();
		HashMap<String, HashVO> cuseishave = new HashMap<String, HashVO>();
		if (targets_cuse != null && targets_cuse.length > 0) {
			for (int t = 0; t < targets_cuse.length; t++) {
				cusetargetid_vo.put(targets_cuse[t].getStringValue("id"), targets_cuse[t]);
			}
			String lastmonth = getBackMonth(month, 1); // 上个月
			HashVO[] cusevs = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where checkdate='" + lastmonth + "' and targetid in (" + getTb().getInCondition(cusetargetid_vo.keySet().toArray(new String[0])) + ")");
			if (cusevs != null && cusevs.length > 0) {
				for (int v = 0; v < cusevs.length; v++) {
					cuseishave.put(cusevs[v].getStringValue("checkeddept") + "_" + cusevs[v].getStringValue("scoreuser", "") + "_" + cusevs[v].getStringValue("targetid", "") + "_" + cusevs[v].getStringValue("targettype"), cusevs[v]);
				}
			}
			HashVO[] c = new HashVO[targets.length + targets_cuse.length]; // 就是说沿用的也是要考核的
			System.arraycopy(targets, 0, c, 0, targets.length);
			System.arraycopy(targets_cuse, 0, c, targets.length, targets_cuse.length);
			targets = c;
		}
		String checkeddept = null;
		String rleader = null;
		String pleader = null;
		String rdept = null;
		String pdept = null;
		if (targets != null && targets.length > 0) {
			for (int i = 0; i < targets.length; i++) {
				checkeddept = targets[i].getStringValue("checkeddept"); // 被考核部门
				if (checkeddept != null && !"".equals(checkeddept.trim())) {
					String[] checkdepts = getTb().split(checkeddept, ";");
					if (checkdepts != null && checkdepts.length > 0) {
						for (int j = 0; j < checkdepts.length; j++) {
							if (checkdepts[j] == null || "".equals(checkdepts[j])) {
								continue;
							}
							rleader = targets[i].getStringValue("rleader"); // r领导
							if (rleader != null && !"".equals(rleader)) {
								String[] rleaders = getTb().split(rleader, ";");
								if (rleaders != null && rleaders.length > 0) {
									for (int rl = 0; rl < rleaders.length; rl++) {
										if (post_usermap.containsKey(rleaders[rl])) {
											String userid = "" + post_usermap.get(rleaders[rl]);
											String[] userids = getTb().split(userid, ";");
											HashMap<String, String> p = new HashMap<String, String>(); //防止重复插入,一个考核人对该部门的该指标只有一条
											for (int s = 0; s < userids.length; s++) {
												if (userids[s] != null && !"".equals(userids[s])) {
													if (!p.containsKey(userids[s])) {
														if (ishavecheck.containsKey(userids[s] + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j])) {
															continue;
														}
														if (dept_namemap.get(checkdepts[j]) == null) {
															continue;
														}
														String key = userids[s] + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j];
														ishavecheck.put(key, "");
														// 如果考核表存在里则需要更新
														checkeduser = checkdepts[j];
														scoreuser = userids[s];
														targetid = targets[i].getStringValue("id");
														targettype = targets[i].getStringValue("type");
														String key_ = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
														if (ishave.containsKey(key_)) { // 如果已经有了
															// 肯定是更新
															targets[i].setAttributeValue("checktype", "垂直R");
															if (compareIsCanUpdateDept(targets[i], ishave.get(key_))) {
																UpdateSQLBuilder usb = new UpdateSQLBuilder("sal_dept_check_score");
																copyProperty2(targets[i], usb, userids[s], checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, ishave.get(key_).getStringValue("checkdratio", "a"), "垂直R", logindeptid);
																usb.setWhereCondition("id=" + ishave.get(key_).getStringValue("id"));
																sqls.add(usb.getSQL());
															}
															ishave.remove(key_);
														} else { // 否则执行插入操作
															InsertSQLBuilder isb = new InsertSQLBuilder("sal_dept_check_score");
															isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_DEPT_CHECK_SCORE", 5000));
															copyProperty(targets[i], isb, userids[s], checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, "垂直R", logindeptid);
															if (cusetargetid_vo.containsKey(targetid) && cuseishave.containsKey(key_)) { // 如果是本次不该考核且沿用的
																// 同时上个月有记录的
																// 则需要从把分从上个月复制过来
																if (cuseishave.get(key_).getStringValue("checkdratio", "a").indexOf("a") < 0) {
																	isb.putFieldValue("checkscore", new BigDecimal(targets[i].getStringValue("weights")).multiply(new BigDecimal("100").subtract(new BigDecimal(cuseishave.get(key_).getStringValue("checkdratio", "a")))).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).toString());
																	isb.putFieldValue("checkdratio", cuseishave.get(key_).getStringValue("checkdratio", "a"));
																	isb.putFieldValue("status", "待提交");
																}
															}
															sqls.add(isb.getSQL());
														}
													}
													p.put(userids[s], userids[s]);
												}
											}
										}
									}
								}
							}
							pleader = targets[i].getStringValue("pleader"); // p领导
							if (pleader != null && !"".equals(pleader)) {
								String[] pleaders = getTb().split(pleader, ";");
								if (pleaders != null && pleaders.length > 0) {
									for (int pl = 0; pl < pleaders.length; pl++) {
										if (post_usermap.containsKey(pleaders[pl])) {
											String userid = "" + post_usermap.get(pleaders[pl]);
											String[] userids = getTb().split(userid, ";");
											HashMap p = new HashMap();
											for (int s = 0; s < userids.length; s++) {
												if (userids[s] != null && !"".equals(userids[s])) {
													if (!p.containsKey(userids[s])) {
														if (ishavecheck.containsKey(userids[s] + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j])) {
															continue;
														}
														if (dept_namemap.get(checkdepts[j]) == null) {
															continue;
														}
														ishavecheck.put(userids[s] + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j], "");

														checkeduser = checkdepts[j];
														scoreuser = userids[s];
														targetid = targets[i].getStringValue("id");
														targettype = targets[i].getStringValue("type");
														String key_ = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
														if (ishave.containsKey(key_)) {
															targets[i].setAttributeValue("checktype", "垂直P");
															if (compareIsCanUpdateDept(targets[i], ishave.get(key_))) {
																UpdateSQLBuilder usb = new UpdateSQLBuilder("sal_dept_check_score");
																copyProperty2(targets[i], usb, userids[s], checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, ishave.get(key_).getStringValue("checkdratio", "a"), "垂直P", logindeptid);
																usb.setWhereCondition("id=" + ishave.get(key_).getStringValue("id"));
																sqls.add(usb.getSQL());
															}
															ishave.remove(key_);
														} else {
															InsertSQLBuilder isb = new InsertSQLBuilder("sal_dept_check_score");
															isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_DEPT_CHECK_SCORE", 5000));
															copyProperty(targets[i], isb, userids[s], checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, "垂直P", logindeptid);
															if (cusetargetid_vo.containsKey(targetid) && cuseishave.containsKey(key_)) { // 如果是本次不该考核且沿用的
																// 同时上个月有记录的
																// 则需要从把分从上个月复制过来
																if (cuseishave.get(key_).getStringValue("checkdratio", "a").indexOf("a") < 0) {
																	isb.putFieldValue("checkscore", new BigDecimal(targets[i].getStringValue("weights")).multiply(new BigDecimal("100").subtract(new BigDecimal(cuseishave.get(key_).getStringValue("checkdratio", "a")))).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).toString());
																	isb.putFieldValue("checkdratio", cuseishave.get(key_).getStringValue("checkdratio", "a"));
																	isb.putFieldValue("status", "待提交");
																}
															}
															sqls.add(isb.getSQL());
														}
													}
													p.put(userids[s], userids[s]);
												}
											}
										}
									}
								}
							}
							rdept = targets[i].getStringValue("rdept"); // r部门
							if (rdept != null && !"".equals(rdept)) {
								String[] rdepts = getTb().split(rdept, ";");
								if (rdepts != null && rdepts.length > 0) {
									for (int rd = 0; rd < rdepts.length; rd++) {
										if (checkdepts[j].equals(rdepts[rd])) {
											continue;
										}
										if (dept_usermap != null && dept_usermap.containsKey(rdepts[rd])) {
											if (ishavecheck.containsKey(dept_usermap.get(rdepts[rd]) + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j])) {
												continue;
											}
											if (dept_namemap.get(checkdepts[j]) == null) {
												continue;
											}
											ishavecheck.put(dept_usermap.get(rdepts[rd]) + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j], "");
											checkeduser = checkdepts[j];
											scoreuser = dept_usermap.get(rdepts[rd]) + "";
											targetid = targets[i].getStringValue("id");
											targettype = targets[i].getStringValue("type");
											String key_ = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
											if (ishave.containsKey(key_)) {
												targets[i].setAttributeValue("checktype", "平行R");
												if (compareIsCanUpdateDept(targets[i], ishave.get(key_)) || ishave.get(key_).getStringValue("scoredeptid", "").equals("")) {
													UpdateSQLBuilder usb = new UpdateSQLBuilder("sal_dept_check_score");
													copyProperty2(targets[i], usb, dept_usermap.get(rdepts[rd]) + "", checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, ishave.get(key_).getStringValue("checkdratio", "a"), "平行R", logindeptid);
													usb.putFieldValue("scoredeptid", rdepts[rd]);
													usb.setWhereCondition("id=" + ishave.get(key_).getStringValue("id"));
													sqls.add(usb.getSQL());
												}
												ishave.remove(key_);
											} else {
												InsertSQLBuilder isb = new InsertSQLBuilder("sal_dept_check_score");
												isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_DEPT_CHECK_SCORE", 5000));
												copyProperty(targets[i], isb, dept_usermap.get(rdepts[rd]) + "", checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, "平行R", logindeptid);
												isb.putFieldValue("scoredeptid", rdepts[rd]);
												if (cusetargetid_vo.containsKey(targetid) && cuseishave.containsKey(key_)) {
													// 如果是本次不该考核且沿用的
													// 同时上个月有记录的
													// 则需要从把分从上个月复制过来
													if (cuseishave.get(key_).getStringValue("checkdratio", "a").indexOf("a") < 0) {
														isb.putFieldValue("checkscore", new BigDecimal(targets[i].getStringValue("weights")).multiply(new BigDecimal("100").subtract(new BigDecimal(cuseishave.get(key_).getStringValue("checkdratio", "a")))).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).toString());
														isb.putFieldValue("checkdratio", cuseishave.get(key_).getStringValue("checkdratio", "a"));
														isb.putFieldValue("status", "待提交");
													}
												}
												sqls.add(isb.getSQL());
											}
										}
									}
								}
							}
							pdept = targets[i].getStringValue("pdept"); // p部门
							if (pdept != null && !"".equals(pdept)) {
								String[] pdepts = getTb().split(pdept, ";");
								if (pdepts != null && pdepts.length > 0) {
									for (int pd = 0; pd < pdepts.length; pd++) {
										if (checkdepts[j].equals(pdepts[pd])) {
											continue;
										}
										if (dept_usermap != null && dept_usermap.containsKey(pdepts[pd])) {
											if (ishavecheck.containsKey(dept_usermap.get(pdepts[pd]) + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j])) {
												continue;
											}
											if (dept_namemap.get(checkdepts[j]) == null) {
												continue;
											}
											ishavecheck.put(dept_usermap.get(pdepts[pd]) + "_" + targets[i].getStringValue("id") + "_" + checkdepts[j], "");
											checkeduser = checkdepts[j];
											scoreuser = dept_usermap.get(pdepts[pd]) + "";
											targetid = targets[i].getStringValue("id");
											targettype = targets[i].getStringValue("type");
											String key_ = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
											if (ishave.containsKey(key_)) {
												targets[i].setAttributeValue("checktype", "平行P");
												if (compareIsCanUpdateDept(targets[i], ishave.get(key_)) || ishave.get(key_).getStringValue("scoredeptid", "").equals("")) {
													UpdateSQLBuilder usb = new UpdateSQLBuilder("sal_dept_check_score");
													copyProperty2(targets[i], usb, dept_usermap.get(pdepts[pd]) + "", checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, ishave.get(key_).getStringValue("checkdratio", "a"), "平行P", logindeptid);
													usb.putFieldValue("scoredeptid", pdepts[pd]);
													usb.setWhereCondition("id=" + ishave.get(key_).getStringValue("id"));
													sqls.add(usb.getSQL());
												}
												ishave.remove(key_);
											} else {
												InsertSQLBuilder isb = new InsertSQLBuilder("sal_dept_check_score");
												isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_DEPT_CHECK_SCORE", 5000));
												copyProperty(targets[i], isb, dept_usermap.get(pdepts[pd]) + "", checkdepts[j], dept_namemap.get(checkdepts[j]) + "", month, logid, "平行P", logindeptid);
												if (cusetargetid_vo.containsKey(targetid) && cuseishave.containsKey(key_)) { // 如果是本次不该考核且沿用的
													// 同时上个月有记录的
													// 则需要从把分从上个月复制过来
													if (cuseishave.get(key_).getStringValue("checkdratio", "a").indexOf("a") < 0) {
														isb.putFieldValue("checkscore", new BigDecimal(targets[i].getStringValue("weights")).multiply(new BigDecimal("100").subtract(new BigDecimal(cuseishave.get(key_).getStringValue("checkdratio", "a")))).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).toString());
														isb.putFieldValue("checkdratio", cuseishave.get(key_).getStringValue("checkdratio", "a"));
														isb.putFieldValue("status", "待提交");
													}
												}
												isb.putFieldValue("scoredeptid", pdepts[pd]);
												sqls.add(isb.getSQL());
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 重要方法检查人员考核方案的合法性 和给予一些提醒
	 * 
	 * @return
	 */
	public HashMap checkViladate_Person() {
		return null;
	}

	/**
	 * 重要方法生成员工考核评分表 主要逻辑是根据方案遍历 体系方案的优先级与方案中评分组的优先级 即A在优先级高的方案中被考核了就不在其他方案中
	 * 考核了，同一方案中如果有考核人同属不同评分组，则只在评分组优先级 高的组中评分 后来增加中层只季度考的逻辑同部门的沿用逻辑
	 * 即所有方案每月都要进行考核，只是如果当月不考的 频率的方案，就要查一下上月的记录 如果有就复制得分没有就插入 员工考核关于频率的描述:
	 * 定性:方案有频率,如果本月不该考核此频率的方案(中层) 则看上个月有没有分数如果有就复制分数过来
	 * 即每次不管什么频率的指标都生成评分表，只是不在当月该考核的频率则从上个月复制得分（如果有） 定量：没处理
	 * 
	 * @param param
	 * @throws Exception
	 */
	public void createScoreTable_Person(List<String> sqls, HashMap param) throws Exception {
		boolean wgflg = getTb().getSysOptionBooleanValue("是否启动网格指标计算模式",false);
		String plv = param.get("plv").toString();
		String isupdate = param.get("isupdate") + ""; // 是否是更新操作
		HashMap<String, HashVO> ishave = new HashMap<String, HashVO>(); // 被考核人_考核人_指标_指标类型
		// 唯一的存在
		param.put("person", ishave);
		if ("Y".equals(isupdate)) { // 如果是更新的操作 则将旧数据放到map里判断是否已经存在
			String month = param.get("month") + "";
			HashVO[] vs = null;
			if ("Y".equals(param.get("isdlonly") + "")) {
				vs = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_score where checkdate='" + month + "' and targettype='员工定量指标'");
			} else {
				vs = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_score where checkdate='" + month + "'");
			}
			for (int v = 0; v < vs.length; v++) {
				if (ishave != null && vs[v] != null) {
					ishave.put(vs[v].getStringValue("checkeduser") + "_" + vs[v].getStringValue("scoreuser", "") + "_" + vs[v].getStringValue("targetid", "") + "_" + vs[v].getStringValue("targettype"), vs[v]);
				}
			}
		}
		// 先取所有考核方案
		HashVO[] allplanvo = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_plan order by seq");
		LinkedHashMap userid_planid = new LinkedHashMap(); // 用户只能被一个方案考核体现方案的优先级
		LinkedHashMap userid_scorerid = new LinkedHashMap(); // 同一评分人不对同一被评分人评价2次
		HashMap uncheckusermap = getDmo().getHashMapBySQLByDS(null, "select id,name from v_sal_personinfo where isuncheck='Y' union all select id,name from pub_user where id not in(select id from v_sal_personinfo)");
		param.put("uncheckuser", uncheckusermap);

		//找人的直属领导。
		HashVO postvos[] = getDmo().getHashVoArrayByDS(null, "select * from v_pub_user_post_2 where isdefault='Y' order by deptid,seq,postcode");
		HashMap<String, String> post_leader_map = new HashMap<String, String>(); //找到岗位配置了的直属领导。一般是某部门或者分支机构的一把手会配置。
		HashMap<String, String> dept_leader_map = new HashMap<String, String>();//找到部门的一把手，主要岗位排第一的。
		HashMap<String, LinkedHashSet> leader_under_userids = new HashMap<String, LinkedHashSet>(); //领导的下一级成员。
		HashMap post_userids_all = getDmo().getHashMapBySQLByDS(null, "select mainstationid postid,id userid from  v_sal_personinfo where isuncheck='N' or isuncheck is null  ", true);
		HashMap<String, String> user_stationkind_map = getDmo().getHashMapBySQLByDS(null, "select id,stationkind from v_sal_personinfo where isuncheck='N' or isuncheck is null ");//找出人员和岗位归类的关系
		HashMap<String, String> user_mainstationPostid_map = new LinkedHashMap<String, String>(); //设置人员ID和主岗位ID
		for (int i = 0; i < postvos.length; i++) {
			String leader = postvos[i].getStringValue("leader");
			String postid = postvos[i].getStringValue("postid");
			user_mainstationPostid_map.put(postvos[i].getStringValue("userid"), postid);
			if (!getTb().isEmpty(leader)) { //把包含直属领导的岗位放进去。
				String[] users = getTb().split(String.valueOf(post_userids_all.get(getTb().split(leader, ";")[0])), ";"); // 该岗位的领导id
				if (users.length == 0) {
					throw new WLTAppException(postvos[i].getStringValue("deptname") + "-" + postvos[i].getStringValue("postname") + "设置的直属领导不存在,请重新设置.");
				} else if (users.length > 1) {
					throw new WLTAppException(postvos[i].getStringValue("deptname") + "-" + postvos[i].getStringValue("postname") + "设置的直属领导有多个,请保证唯一性.");
				}
				for (int j = 0; j < users.length; j++) {
					if (!getTb().isEmpty(users[j])) {
						post_leader_map.put(postid, users[j]); //直接放人得了。key=岗位ID，value该岗位的负责领导ID
						if (leader_under_userids.containsKey(users[j])) {
							LinkedHashSet under_userid = leader_under_userids.get(users[j]);
							under_userid.add(postvos[i].getStringValue("userid"));
						} else {
							LinkedHashSet under_userid = new LinkedHashSet();
							under_userid.add(postvos[i].getStringValue("userid"));
							leader_under_userids.put(users[j], under_userid); //通过领导ID加入他下属IDs
						}
						break; //跳出，其实这个地方应该先整体验证一把。就是看看岗位有没有人，领导岗位有没有多个人。需要提示。
					}
				}
			}
			String deptid = postvos[i].getStringValue("deptid");
			if (!dept_leader_map.containsKey(deptid)) {
				//检查此岗位下是否有人。
				if (post_userids_all.containsKey(postid)) {
					if (post_userids_all.get(postid) != null) {//岗位下有人
						String[] users = getTb().split(String.valueOf(post_userids_all.get(postid)), ";");
						for (int j = 0; j < users.length; j++) {
							if (!getTb().isEmpty(users[j])) {
								dept_leader_map.put(deptid, users[j]); //部门加入领导id
								LinkedHashSet set = new LinkedHashSet();
								if (leader_under_userids.containsKey(users[j])) {
									set = leader_under_userids.get(users[j]);
								}
								leader_under_userids.put(users[j], set); //把领导下的所有员工。
								break; //跳出，其实这个地方应该先整体验证一把。就是看看岗位有没有人，领导岗位有没有多个人。需要提示。
							}
						}
					}
				}
			} else {
				String deptleaderid = dept_leader_map.get(deptid);
				LinkedHashSet set = leader_under_userids.get(deptleaderid);
				if (set != null) {
					set.add(postvos[i].getStringValue("userid"));
				}
			}
		}

		// 体现了评分组的优先级
		//20131010郝明添加了忽略优先级功能。
		if (allplanvo != null && allplanvo.length > 0 && !"Y".equals(param.get("isdlonly") + "")) {
			String checktypeid = null; // 方案绑定的考核对象分类id，可以找到实际的被考核用户和关联的指标
			String planid = null;
			String planplv = null;
			// 遍历方案
			for (int i = 0; i < allplanvo.length; i++) {
				checktypeid = allplanvo[i].getStringValue("checkobj", "");
				planid = allplanvo[i].getStringValue("id", "");
				planplv = allplanvo[i].getStringValue("checkcycle", "");
				if ("".equals(checktypeid)) {
					continue;
				}
				// 根据ID取到考核对象归类
				HashVO[] checkobj = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_type where id=" + checktypeid);
				if (checkobj == null || checkobj.length <= 0) {
					continue;
				}
				// 考核对象归类所关联的岗位归类
				String stationkinds = checkobj[0].getStringValue("stationkinds", "");
				if ("".equals(stationkinds)) {
					continue;
				}
				// 根据岗位归类找到具体的岗位
				String[] postids = getDmo().getStringArrayFirstColByDS(null, "select id from pub_post where stationkind in (" + getTb().getInCondition(stationkinds) + ")");
				if (postids == null || postids.length <= 0) {
					continue;
				}

				/**
				 * 本次需要沿用的方案 主要是中层要季度
				 */
				HashMap<String, HashVO> cuseishave = new HashMap<String, HashVO>();
				String[] plv_arrs = getTb().split(plv, ";");
				boolean ifcontains = false;
				for (int k = 0; k < plv_arrs.length; k++) {
					if (!getTb().isEmpty(plv_arrs[k]) && plv_arrs[k].equals(planplv)) {
						ifcontains = true;
					}
				}
				if (!(!"".equals(planplv) && ifcontains)) { // 本次考核不应该进行此次考核
					// 就得沿用上个月的
					String lastmonth = getBackMonth(param.get("month") + "", 1); // 上个月
					HashVO[] cusevs = getDmo().getHashVoArrayByDS(null,
							"select * from sal_person_check_score where checkdate='" + lastmonth + "' and checktype='" + checkobj[0].getStringValue("name") + "' and targettype='员工定性指标' union all select * from sal_person_check_score where checkdate='" + lastmonth + "' and checktype='" + SalaryTBUtil.ggCheckTargetType + "' and targettype='高管定性指标' and scoretype='手动打分'");
					if (cusevs != null && cusevs.length > 0) {
						for (int v = 0; v < cusevs.length; v++) {
							cuseishave.put(cusevs[v].getStringValue("checkeduser") + "_" + cusevs[v].getStringValue("scoreuser", "") + "_" + cusevs[v].getStringValue("targetid", "") + "_" + cusevs[v].getStringValue("targettype"), cusevs[v]);
						}
					}
				}
				param.put("cuse", cuseishave);
				// if (!"Y".equals(isupdate)) { //
				// 更新操作肯定是一个重新插入的过程，然后判断已经有了就更新、没有就插入、抑或多的删除
				// String check_date = getDmo().getStringValueByDS(null,
				// "select max(checkdate) from sal_person_check_score where checktype='"
				// + checkobj[0].getStringValue("name") + "' ");
				// if (check_date != null && !"".equals(check_date)) { // 已经考核过了
				// if (!(!"".equals(planplv) && plv.indexOf(planplv) >= 0)) { //
				// 不应该进行此次考核
				// HashVO[] alldetails = getDmo().getHashVoArrayByDS(null,
				// "select * from sal_person_check_score where checktype='" +
				// checkobj[0].getStringValue("name") + "' and checkdate='" +
				// check_date + "' ");
				// for (int k = 0; k < alldetails.length; k++) {
				// InsertSQLBuilder isb = new
				// InsertSQLBuilder("sal_person_check_score");
				// VectorMap vm = alldetails[k].getM_hData();
				// String[] keys = (String[]) vm.keySet().toArray(new
				// String[0]);
				// for (int ks = 0; ks < keys.length; ks++) {
				// isb.putFieldValue(keys[ks],
				// alldetails[k].getStringValue(keys[ks]));
				// }
				// isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null,
				// "S_SAL_PERSON_CHECK_SCORE", 8000));
				// sqls.add(isb.getSQL());
				// }
				// continue;
				// }
				// }
				// }
				// 查一下那些人不需要考核，没有主岗位的也不考核
				String[] uncheckuserids = (String[]) uncheckusermap.keySet().toArray(new String[0]);

				// 根据岗位去找到主岗是这些岗位的人就是被考核人了
				HashMap personid_name = getDmo().getHashMapBySQLByDS(null, "select u.id,u.name from pub_user u left join pub_user_post p on u.id=p.userid where p.userid not in (" + getTb().getInCondition(uncheckuserids) + ") and p.userid is not null and p.userdept is not null and  p.postid in (" + getTb().getInCondition(postids) + ")");
				// 这些人的主部门都是什么
				HashMap userid_deptid = getDmo().getHashMapBySQLByDS(null, "select u.id, p.userdept from pub_user u left join pub_user_post p on u.id=p.userid where p.userid not in (" + getTb().getInCondition(uncheckuserids) + ") and p.userid is not null and p.userdept is not null and p.isdefault='Y' and p.postid in (" + getTb().getInCondition(postids) + ")");
				HashMap userid_postids = null;
				if (personid_name == null) {
					userid_postids = new HashMap();
				} else {
					userid_postids = getDmo().getHashMapBySQLByDS(null, "select u.id, p.postid from pub_user u left join pub_user_post p on u.id=p.userid where p.userid not in (" + getTb().getInCondition(uncheckuserids) + ") and p.userid is not null and p.userdept is not null and p.userid in (" + getTb().getInCondition((String[]) personid_name.keySet().toArray(new String[0])) + ")", true);
				}
				// 这些部门下都有哪些人
				HashMap deptid_userid = getDmo().getHashMapBySQLByDS(null, "select p.userdept,u.id from pub_user u left join pub_user_post p on u.id=p.userid where p.userid not in (" + getTb().getInCondition(uncheckuserids) + ") and p.userid is not null and p.userdept is not null and p.userdept in (" + getTb().getInCondition((String[]) userid_deptid.values().toArray(new String[0])) + ")", true);
				// 找到了岗位接下来找要考核哪些指标
				HashVO[] targetvos = null;
				if (allplanvo[i].getStringValue("targetsource", "").equals("部门定性指标")) {
					targetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_target_list where 1=1  and state='参与考核' and type='部门定性指标' order by code");
				} else if (allplanvo[i].getStringValue("targetsource", "").equals("员工定性指标")) {
					targetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_list where 1=1  and type='" + checktypeid + "' and targettype='员工定性指标'  and state='参与考核'  order by seq");
				} else { // 高管定性指标
					targetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_check_list where 1=1 order by seq");
				}
				if (targetvos == null || targetvos.length <= 0) {
					continue;
				}
				// 找到了要考核哪些指标之后开始找评价人是那些人 
				HashVO[] scorersgroup = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_plan_scorer where 1=1  and (planid='" + planid + "')  order by seq ");
				if (scorersgroup == null || scorersgroup.length <= 0) {
					continue;
				}
				String type = null;
				String scoretype = null; // 评分方式 手动打分就是正常的需要生成评分表
				for (int j = 0; j < scorersgroup.length; j++) {
					type = scorersgroup[j].getStringValue("type", "");
					scoretype = scorersgroup[j].getStringValue("scoretype", "手动打分");
					if (!"手动打分".equals(scoretype)) { // 还有一种是在高管的考核方案中定义要从部门考核得分中直接计算得分
						// 这种我考虑是增加一条空的打分记录，在算得分的时候 判断如果是空的就去取部门得分进行计算
						deptCheck(param, allplanvo[i], scorersgroup[j], checkobj[0], personid_name, userid_deptid, null, userid_scorerid, userid_planid, targetvos, sqls, userid_postids);
					} else {
						String[] userid = null;
						if ("本部门员工".equals(type)) { // 找到本部门的所有人
							selfDeptCheck(param, allplanvo[i], scorersgroup[j], checkobj[0], personid_name, userid_deptid, deptid_userid, userid_scorerid, userid_planid, targetvos, sqls, userid_postids);
						} else if ("被考核人互评".equals(type)) { // 那就需要找到被考核人了
							if (personid_name != null && personid_name.size() > 0) {
								userid = (String[]) personid_name.keySet().toArray(new String[0]);
							}
						} else if ("岗位归类".equals(type)) { //以后把这个地方添加上岗位组。
							String postkinds = scorersgroup[j].getStringValue("postkind");
							if (getTb().isEmpty(postkinds)) {
								continue;
							}
							String posts[] = getDmo().getStringArrayFirstColByDS(null, "select * from pub_post where stationkind in (" + getTb().getInCondition(postkinds) + ")");
							if (posts.length == 0) {
								continue;
							}
							List userList = new ArrayList();//所有人集合
							for (int k = 0; k < posts.length; k++) {
								String postid = posts[k];
								if (post_userids_all.containsKey(postid)) { //找到该岗位下的所有人
									String userids = (String) post_userids_all.get(postid);
									if (!getTb().isEmpty(userids)) {
										String useridsarr[] = getTb().split(userids, ";");
										for (int l = 0; l < useridsarr.length; l++) {
											userList.add(useridsarr[l]);
										}
									}
								}
							}
							userid = (String[]) userList.toArray(new String[0]);
						} else if ("直属领导".equals(type)) {
							String checkeduserid[] = (String[]) personid_name.keySet().toArray(new String[0]);
							for (int k = 0; k < checkeduserid.length; k++) {
								String leader = null;
								if (user_mainstationPostid_map.containsKey(checkeduserid[k])) {//找到该人员主岗位
									String checkedUserPostid = user_mainstationPostid_map.get(checkeduserid[k]);
									if (!TBUtil.isEmpty(checkedUserPostid)) {
										leader = post_leader_map.get(checkedUserPostid);
									}
								}
								if (TBUtil.isEmpty(leader)) {
									leader = dept_leader_map.get(userid_deptid.get(checkeduserid[k])); //如果没有找到直属领导，那么本部门领导有可能就是直属领导(部门一把手)。2015-02-01
								}
								if(TBUtil.isEmpty(leader)){ //如果还是空。则跳出,##是否需要预警##
									continue;
								}
								HashMap checkeduser = new HashMap();
								checkeduser.put(checkeduserid[k], personid_name.get(checkeduserid[k]));
								otherCheck(param, allplanvo[i], scorersgroup[j], checkobj[0], checkeduser, userid_deptid, new String[] { leader }, userid_scorerid, userid_planid, targetvos, sqls, userid_postids);
							}
							continue;
						} else if ("下一级员工".equals(type)) {
							String checkeduserid[] = (String[]) personid_name.keySet().toArray(new String[0]);
							String postkind = scorersgroup[j].getStringValue("postkind"); //有可能是下级员工的某岗位类型打分。by haoming 2016-1-15 昌吉

							String[] andPostKinds = null;
							if (!TBUtil.isEmpty(postkind)) {
								andPostKinds = getTb().split(postkind, ";");
							}

							for (int k = 0; k < checkeduserid.length; k++) {
								LinkedHashSet scoreuseridmap = leader_under_userids.get(checkeduserid[k]);
								if (scoreuseridmap == null) { //如果没有找到,#是否需要抛出问题#
									continue;
								}
								String scoreusers[] = (String[]) scoreuseridmap.toArray(new String[0]); //找到打分人
								if (!TBUtil.isEmpty(postkind)) {
									for (int l = 0; l < scoreusers.length; l++) {
										String userMainStationKind = user_stationkind_map.get(scoreusers[l]); //找到下属员工的岗位归类
										if (!getTb().isEmpty(userMainStationKind)) {
											if (postkind.indexOf(";" + userMainStationKind + ";") < 0) { //如果不包含该人员
												scoreuseridmap.remove(scoreusers[l]); //如果该人员岗位归类不是配置中的，就移除
											}
										}
									}
								}
								HashMap checkeduser = new HashMap();
								checkeduser.put(checkeduserid[k], personid_name.get(checkeduserid[k]));
								otherCheck(param, allplanvo[i], scorersgroup[j], checkobj[0], checkeduser, userid_deptid, (String[]) scoreuseridmap.toArray(new String[0]), userid_scorerid, userid_planid, targetvos, sqls, userid_postids);
							}
							continue;
						} else { // 自由选择的 则直接找到实际评分人
							String userids = scorersgroup[j].getStringValue("userids", "");
							if (!"".equals(userids)) {
								userid = getTb().split(userids, ";");
							}
						}
						if (userid != null && userid.length > 0) {
							otherCheck(param, allplanvo[i], scorersgroup[j], checkobj[0], personid_name, userid_deptid, userid, userid_scorerid, userid_planid, targetvos, sqls, userid_postids);
						}
					}
				}
			}
		}

		// 按照考核方案 处理的员工定性指标，接下来处理员工定量指标
		// 所有生成的打分记录中的评分人默认都是-99999 和部门的定量考核类似
		// 查询所有的员工定量指标
		HashVO[] quantifytargetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_list where 1=1 and targettype='员工定量指标' and state='参与考核' order by seq");
		HashMap<String, HashVO> targets_vo = new HashMap<String, HashVO>();
		HashMap<String, HashVO> targets_vo_wg = new HashMap<String, HashVO>();//zzl[2020-5-18]增加网格指标考核
		HashVO [] hashVO;
		for (int t = 0; t < quantifytargetvos.length; t++) { // 将员工定量指标缓存
			hashVO=getDmo().getHashVoArrayByDS(null,"select * from SAL_TARGET_CATALOG where id='"+quantifytargetvos[t].getStringValue("catalogid")+"'");
			if(wgflg && hashVO[0].getStringValue("name").equals("网格指标")){
				targets_vo_wg.put(quantifytargetvos[t].getStringValue("id"), quantifytargetvos[t]);

			}else{
				targets_vo.put(quantifytargetvos[t].getStringValue("id"), quantifytargetvos[t]);
			}

		}
		// 后来postid改成存的是岗位归类的id了
		// 每个岗位归类都有哪些岗位(主岗)
		HashMap sort_postids = getDmo().getHashMapBySQLByDS(null, "select stationkind,postid from v_pub_user_post_2 where isdefault='Y' ", true);
	   //zzl[2020-5-18] 获取网格的信息
		HashVO[] targets_postids_wg= getDmo().getHashVoArrayByDS(null, "select id, targetid, postid, planedvalue, weights from sal_person_check_post_wg where targetid in (" + tb.getInCondition((String[]) targets_vo_wg.keySet().toArray(new String[0])) + ")", true);
		HashVO[] targets_postids= getDmo().getHashVoArrayByDS(null, "select id, targetid, postid, planedvalue, weights from sal_person_check_post where targetid in (" + tb.getInCondition((String[]) targets_vo.keySet().toArray(new String[0])) + ")", true);
		if(targets_postids_wg != null && targets_postids_wg.length > 0){
			String postids = null;
			String targetids = null;
			String planedvalue = null;
			String groupid = null;
			HashMap<String, String> userid_targetid = new HashMap<String, String>(); // 一个人对同一指标只能有一条记录
			String checkeduser = null;
			String scoreuser = null;
			String targetid = null;
			String targettype = null;
			String groupweights = null;
			HashMap uncheckuser = (HashMap) param.get("uncheckuser");
			for (int q = 0; q < targets_postids_wg.length; q++) {
				postids = targets_postids_wg[q].getStringValue("postid", "");
				targetids = targets_postids_wg[q].getStringValue("targetid");
				planedvalue = targets_postids_wg[q].getStringValue("planedvalue");
				groupid = targets_postids_wg[q].getStringValue("id");
				groupweights = targets_postids_wg[q].getStringValue("weights");
				HashVO [] wgVO=getDmo().getHashVoArrayByDS(null,"select * from excel_tab_85 where id in("+getTb().getInCondition(targets_postids_wg[q].getStringValue("postid"))+")");
				for(int u = 0; u < wgVO.length; u++){
					scoreuser = "-99999";
					targetid = targetids;
					targettype = "员工定量指标";
					String key = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
					if ("Y".equals(isupdate) && ishave != null && ishave.containsKey(key)) {
						targets_vo_wg.get(targetids).setAttributeValue("planedvalue", planedvalue);
						targets_vo_wg.get(targetids).setAttributeValue("checktype", "");
						targets_vo_wg.get(targetids).setAttributeValue("scorerweightstype", "");
						targets_vo_wg.get(targetids).setAttributeValue("groupweights", "");
						targets_vo_wg.get(targetids).setAttributeValue("groupweightstype", "");
						targets_vo_wg.get(targetids).setAttributeValue("weights", groupweights);
						targets_vo_wg.get(targetids).setAttributeValue("scoretype", "手动打分");
						if (compareIsCanUpdatePerson(targets_vo.get(targetids), ishave.get(key)) || !groupid.equals(ishave.get(key).getStringValue("groupid", ""))) {
							UpdateSQLBuilder isb = new UpdateSQLBuilder("sal_person_check_score");
							isb.setWhereCondition(" id=" + ishave.get(key).getStringValue("id"));
							isb.putFieldValue("targetid", targetids);
							isb.putFieldValue("targetname", targets_vo_wg.get(targetids).getStringValue("name", ""));
							isb.putFieldValue("checktype", "");
							isb.putFieldValue("targettype", "员工定量指标");
							isb.putFieldValue("scorerweightstype", "");
							isb.putFieldValue("checkeduser", wgVO[u].getStringValue("id"));
							isb.putFieldValue("checkedusername", wgVO[u].getStringValue("C") + "-"+wgVO[u].getStringValue("D"));
							isb.putFieldValue("scoreuser", "-99999");
							isb.putFieldValue("scoreusertype", "");
							isb.putFieldValue("groupweights", ""); // 评分组的权重
							isb.putFieldValue("groupweightstype", ""); // 评分组的权重方式
							isb.putFieldValue("weights", targets_vo_wg.get(targetids).getStringValue("weights", "")); // 指标权重
							isb.putFieldValue("checkdate", param.get("month") + "");
							isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
							isb.putFieldValue("logid", param.get("logid") + "");
							isb.putFieldValue("scoretype", "手动打分"); // 后来在高管考核方案中增加了这个字段
							isb.putFieldValue("planedvalue", planedvalue);
							isb.putFieldValue("groupid", groupid);
							isb.putFieldValue("getvalue", targets_vo_wg.get(targetids).getStringValue("getvalue", ""));
							isb.putFieldValue("partvalue", targets_vo_wg.get(targetids).getStringValue("partvalue", ""));
							isb.putFieldValue("targetdefine", targets_vo_wg.get(targetids).getStringValue("define", ""));
							sqls.add(isb.getSQL());
						}
						ishave.remove(key);
					} else {
						InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_check_score");
						isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_SCORE", 8000));
						isb.putFieldValue("targetid", targetids);
						isb.putFieldValue("targetname", targets_vo_wg.get(targetids).getStringValue("name", ""));
						isb.putFieldValue("checktype", "");
						isb.putFieldValue("targettype", "员工定量指标");
						isb.putFieldValue("scorerweightstype", "");
						isb.putFieldValue("checkeduser", wgVO[u].getStringValue("id"));
						isb.putFieldValue("checkedusername", wgVO[u].getStringValue("C") + "-"+wgVO[u].getStringValue("D"));
						isb.putFieldValue("scoreuser", "-99999");
						isb.putFieldValue("scoreusertype", "");
						isb.putFieldValue("groupweights", ""); // 评分组的权重
						isb.putFieldValue("groupweightstype", ""); // 评分组的权重方式
						isb.putFieldValue("weights", groupweights); // 指标权重
						// 后来改成子表中的权重而指标的权重也影藏了
						isb.putFieldValue("checkdate", param.get("month") + "");
						isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
						isb.putFieldValue("logid", param.get("logid") + "");
						isb.putFieldValue("scoretype", "手动打分"); // 后来在高管考核方案中增加了这个字段
						isb.putFieldValue("planedvalue", planedvalue);
						isb.putFieldValue("groupid", groupid);
						isb.putFieldValue("getvalue", targets_vo_wg.get(targetids).getStringValue("getvalue", ""));
						isb.putFieldValue("partvalue", targets_vo_wg.get(targetids).getStringValue("partvalue", ""));
						isb.putFieldValue("targetdefine", targets_vo_wg.get(targetids).getStringValue("define", ""));
						sqls.add(isb.getSQL());
					}
					userid_targetid.put(wgVO[u].getStringValue("id")+ "_" + targetids, "");
				}
			}


		}
		if (targets_postids != null && targets_postids.length > 0) {
			String postids = null;
			String targetids = null;
			String planedvalue = null;
			String groupid = null;
			List<String> allposts = new ArrayList<String>();
			String[] realpostid = null;
			for (int q = 0; q < targets_postids.length; q++) {
				postids = targets_postids[q].getStringValue("postid", "");
				StringBuffer sb = new StringBuffer();
				String[] postid = getTb().split(postids, ";");
				if (postid != null && postid.length > 0) {
					for (int d = 0; d < postid.length; d++) {
						if (postid[d] != null && !"".equals(postid[d])) {
							if (sort_postids.containsKey(postid[d])) {
								realpostid = getTb().split(sort_postids.get(postid[d]) + "", ";");
								if (realpostid != null && realpostid.length > 0) {
									for (int p = 0; p < realpostid.length; p++) {
										allposts.add(realpostid[p]);
										sb.append(";" + realpostid[p]);
									}
								}
							}
						}
					}
				}
				targets_postids[q].setAttributeValue("postid", sb.toString()); // 改成真正的岗位id后面的逻辑就不用改了，为了后来选择岗位归类而改
			}

			HashMap post_userids = getDmo().getHashMapBySQLByDS(null, "select mainstationid postid,id userid from  v_sal_personinfo where  mainstationid in(" + getTb().getInCondition(allposts) + ")", true);
			HashMap userid_name = getDmo().getHashMapBySQLByDS(null, "select id userid, name username from  v_sal_personinfo where mainstationid in(" + getTb().getInCondition(allposts) + ")", false);
			HashMap<String, String> userid_targetid = new HashMap<String, String>(); // 一个人对同一指标只能有一条记录
			String checkeduser = null;
			String scoreuser = null;
			String targetid = null;
			String targettype = null;
			String groupweights = null;
			HashMap uncheckuser = (HashMap) param.get("uncheckuser");
			for (int q = 0; q < targets_postids.length; q++) {
				if (targets_postids[q].getStringValue("postid") != null) {
					postids = targets_postids[q].getStringValue("postid", "");
					targetids = targets_postids[q].getStringValue("targetid");
					planedvalue = targets_postids[q].getStringValue("planedvalue");
					groupid = targets_postids[q].getStringValue("id");
					groupweights = targets_postids[q].getStringValue("weights");
					String[] postid = getTb().split(postids, ";");
					if (postid != null && postid.length > 0) {
						for (int d = 0; d < postid.length; d++) {
							if (postid[d] != null && !"".equals(postid[d])) {
								if (post_userids.containsKey(postid[d]) && post_userids.get(postid[d]) != null) {
									String[] userids = getTb().split(post_userids.get(postid[d]).toString(), ";");
									if (userids != null && userids.length > 0) {
										for (int u = 0; u < userids.length; u++) {
											if (userids[u] != null && !"".equals(userids[u]) && !userid_targetid.containsKey(userids[u] + "_" + targetids)) {
												checkeduser = userids[u];
												if (uncheckuser != null && uncheckuser.containsKey(checkeduser)) {
													continue;
												}
												scoreuser = "-99999";
												targetid = targetids;
												targettype = "员工定量指标";
												String key = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
												if ("Y".equals(isupdate) && ishave != null && ishave.containsKey(key)) {
													targets_vo.get(targetids).setAttributeValue("planedvalue", planedvalue);
													targets_vo.get(targetids).setAttributeValue("checktype", "");
													targets_vo.get(targetids).setAttributeValue("scorerweightstype", "");
													targets_vo.get(targetids).setAttributeValue("groupweights", "");
													targets_vo.get(targetids).setAttributeValue("groupweightstype", "");
													targets_vo.get(targetids).setAttributeValue("weights", groupweights);
													targets_vo.get(targetids).setAttributeValue("scoretype", "手动打分");
													if (compareIsCanUpdatePerson(targets_vo.get(targetids), ishave.get(key)) || !groupid.equals(ishave.get(key).getStringValue("groupid", ""))) {
														UpdateSQLBuilder isb = new UpdateSQLBuilder("sal_person_check_score");
														isb.setWhereCondition(" id=" + ishave.get(key).getStringValue("id"));
														isb.putFieldValue("targetid", targetids);
														isb.putFieldValue("targetname", targets_vo.get(targetids).getStringValue("name", ""));
														isb.putFieldValue("checktype", "");
														isb.putFieldValue("targettype", "员工定量指标");
														isb.putFieldValue("scorerweightstype", "");
														isb.putFieldValue("checkeduser", userids[u]);
														isb.putFieldValue("checkedusername", userid_name.get(userids[u]) + "");
														isb.putFieldValue("scoreuser", "-99999");
														isb.putFieldValue("scoreusertype", "");
														isb.putFieldValue("groupweights", ""); // 评分组的权重
														isb.putFieldValue("groupweightstype", ""); // 评分组的权重方式
														isb.putFieldValue("weights", targets_vo.get(targetids).getStringValue("weights", "")); // 指标权重
														isb.putFieldValue("checkdate", param.get("month") + "");
														isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
														isb.putFieldValue("logid", param.get("logid") + "");
														isb.putFieldValue("scoretype", "手动打分"); // 后来在高管考核方案中增加了这个字段
														isb.putFieldValue("planedvalue", planedvalue);
														isb.putFieldValue("groupid", groupid);
														isb.putFieldValue("getvalue", targets_vo.get(targetids).getStringValue("getvalue", ""));
														isb.putFieldValue("partvalue", targets_vo.get(targetids).getStringValue("partvalue", ""));
														isb.putFieldValue("targetdefine", targets_vo.get(targetids).getStringValue("define", ""));
														sqls.add(isb.getSQL());
													}
													ishave.remove(key);
												} else {
													InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_check_score");
													isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_SCORE", 8000));
													isb.putFieldValue("targetid", targetids);
													isb.putFieldValue("targetname", targets_vo.get(targetids).getStringValue("name", ""));
													isb.putFieldValue("checktype", "");
													isb.putFieldValue("targettype", "员工定量指标");
													isb.putFieldValue("scorerweightstype", "");
													isb.putFieldValue("checkeduser", userids[u]);
													isb.putFieldValue("checkedusername", userid_name.get(userids[u]) + "");
													isb.putFieldValue("scoreuser", "-99999");
													isb.putFieldValue("scoreusertype", "");
													isb.putFieldValue("groupweights", ""); // 评分组的权重
													isb.putFieldValue("groupweightstype", ""); // 评分组的权重方式
													isb.putFieldValue("weights", groupweights); // 指标权重
													// 后来改成子表中的权重而指标的权重也影藏了
													isb.putFieldValue("checkdate", param.get("month") + "");
													isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
													isb.putFieldValue("logid", param.get("logid") + "");
													isb.putFieldValue("scoretype", "手动打分"); // 后来在高管考核方案中增加了这个字段
													isb.putFieldValue("planedvalue", planedvalue);
													isb.putFieldValue("groupid", groupid);
													isb.putFieldValue("getvalue", targets_vo.get(targetids).getStringValue("getvalue", ""));
													isb.putFieldValue("partvalue", targets_vo.get(targetids).getStringValue("partvalue", ""));
													isb.putFieldValue("targetdefine", targets_vo.get(targetids).getStringValue("define", ""));
													sqls.add(isb.getSQL());
												}
												userid_targetid.put(userids[u] + "_" + targetids, "");
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		if (ishave != null && ishave.size() > 0) {
			String[] keys = (String[]) ishave.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_person_check_score");
				dsb.setWhereCondition("id=" + ishave.get(keys[i]).getStringValue("id"));
				sqls.add(dsb.getSQL());
			}
		}
	}

	/**
	 * 本部门互评
	 * 
	 * @param scorerid
	 * @param personid_name
	 * @param deptid_userid
	 * @param userid_scorerid
	 * @param targetvos
	 * @param sqls
	 */
	private void selfDeptCheck(HashMap param, HashVO planvo, HashVO scorervo, HashVO objvo, HashMap userid_name, HashMap userid_deptid, HashMap deptid_userid, LinkedHashMap userid_scorerid, LinkedHashMap userid_planid, HashVO[] _targetvos, List sqls, HashMap userid_postids) throws Exception {
		if (planvo == null || deptid_userid == null) {
			throw new Exception("SalaryServiceImpl的selfDeptCheck的部分入参不能为空");
		}
		String[] checkeduserids = (String[]) userid_deptid.keySet().toArray(new String[0]); // 被考核人
		HashVO[] targetvos = null;
		HashMap<String, HashVO> ishave = (HashMap<String, HashVO>) param.get("person");
		HashMap<String, HashVO> cuse = (HashMap<String, HashVO>) param.get("cuse");
		HashMap uncheckuser = (HashMap) param.get("uncheckuser");
		String isupdate = param.get("isupdate") + "";
		String isignoreseq = planvo.getStringValue("ignoreseq", "N"); //是否需要忽略优先级。忽略优先级后，此记录一定会加入到考核打分中。
		if (checkeduserids != null && checkeduserids.length > 0) {
			for (int i = 0; i < checkeduserids.length; i++) {
				if (!"Y".equals(isignoreseq) && userid_planid.containsKey(checkeduserids[i]) && !((String) userid_planid.get(checkeduserids[i])).equals(planvo.getStringValue("id"))) {
					continue;
				} // 如果被考核人已经被考核过了
				if (!"Y".equals(isignoreseq)) { //被忽略掉优先级的。也不影响其他方案。
					userid_planid.put(checkeduserids[i], planvo.getStringValue("id"));
				}
				if ("部门定性指标".equals(planvo.getStringValue("targetsource", ""))) {
					targetvos = getTargetByPostId(_targetvos, userid_postids.get(checkeduserids[i]) + "", "rleader");
				} else if ("高管定性指标".equals(planvo.getStringValue("targetsource", ""))) { // 如果是高管定性指标则要找当前人的指标是哪些通过岗位来找
					targetvos = getTargetByPostId(_targetvos, userid_postids.get(checkeduserids[i]) + "", "checkedpost");
				} else {
					targetvos = _targetvos;
				}
				if (targetvos == null || targetvos.length <= 0) {
					continue;
				}
				String checkeduser = null;
				String scoreuser = null;
				String targetid = null;
				String targettype = null;
				for (int j = 0; j < targetvos.length; j++) {
					String scorer = (String) deptid_userid.get(userid_deptid.get(checkeduserids[i]));
					if (scorer != null && !"".equals(scorer) && !"null".equals(scorer)) {
						String[] scorers = getTb().split(scorer, ";");
						if (scorers != null && scorers.length > 0) {
							HashMap scorermap = new HashMap();
							for (int k = 0; k < scorers.length; k++) {
								if (scorers[k] != null && !"".equals(scorers[k]) && ("Y".equals(isignoreseq) || !userid_scorerid.containsKey(scorers[k] + "_" + checkeduserids[i] + "_") || ((String) userid_scorerid.get(scorers[k] + "_" + checkeduserids[i] + "_")).equals(scorervo.getStringValue("id"))) && !checkeduserids[i].equals(scorers[k])
										&& ("Y".equals(isignoreseq) || !userid_planid.containsKey(checkeduserids[i]) || ((String) userid_planid.get(checkeduserids[i])).equals(planvo.getStringValue("id")))) { // +
									if (scorermap.containsKey(scorers[k])) {
										continue; // 可能有多岗位
									}
									if (uncheckuser != null && (uncheckuser.containsKey(scorers[k]) || uncheckuser.containsKey(checkeduserids[i]))) {
										continue;
									}
									scorermap.put(scorers[k], "");

									checkeduser = checkeduserids[i];
									scoreuser = scorers[k];
									targetid = targetvos[j].getStringValue("id", "");
									targettype = planvo.getStringValue("targetsource", "员工定性指标");
									String checktype = objvo.getStringValue("name");
									if (targettype.equals("高管定性指标")) {
										checktype = SalaryTBUtil.ggCheckTargetType;
									}
									String key = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
									if ("Y".equals(isupdate) && ishave.containsKey(key)) {
										// 这条记录是在计算分管部门得分的时候用的到 只需要更新组的权重就可以了
										targetvos[j].setAttributeValue("checktype", objvo.getStringValue("name"));
										targetvos[j].setAttributeValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
										targetvos[j].setAttributeValue("groupweights", scorervo.getStringValue("weights", ""));
										targetvos[j].setAttributeValue("groupweightstype", scorervo.getStringValue("weightstype", ""));
										targetvos[j].setAttributeValue("scoretype", scorervo.getStringValue("scoretype", ""));
										if (compareIsCanUpdatePerson(targetvos[j], ishave.get(key))) {
											UpdateSQLBuilder isb = new UpdateSQLBuilder("sal_person_check_score");
											isb.setWhereCondition(" id=" + ishave.get(key).getStringValue("id"));
											isb.putFieldValue("targetname", targetvos[j].getStringValue("name", ""));
											isb.putFieldValue("checktype", checktype);
											isb.putFieldValue("targettype", targettype);
											isb.putFieldValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
											isb.putFieldValue("checkeduser", checkeduserids[i]);
											isb.putFieldValue("checkedusername", userid_name.get(checkeduserids[i]) + "");
											isb.putFieldValue("scoreuser", scorers[k]);
											isb.putFieldValue("scoreusertype", scorervo.getStringValue("id"));
											isb.putFieldValue("groupweights", scorervo.getStringValue("weights", "")); // 评分组的权重
											isb.putFieldValue("groupweightstype", scorervo.getStringValue("weightstype", "")); // 评分组的权重方式
											// 因为是员工定性所以不用重新算
											// 如果是部门的就要根据权重重新算了
											isb.putFieldValue("weights", targetvos[j].getStringValue("weights", "")); // 指标权重
											isb.putFieldValue("checkdate", param.get("month") + "");
											isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
											isb.putFieldValue("logid", param.get("logid") + "");
											isb.putFieldValue("scoretype", scorervo.getStringValue("scoretype", "手动打分"));
											sqls.add(isb.getSQL());
										}
										// 有的处理过了就删除 最终都处理完之后剩下的就是需要删除的啦
										ishave.remove(key);
									} else {
										InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_check_score");
										isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_SCORE", 8000));
										isb.putFieldValue("targetid", targetvos[j].getStringValue("id", ""));
										isb.putFieldValue("targetname", targetvos[j].getStringValue("name", ""));
										isb.putFieldValue("checktype", checktype);
										isb.putFieldValue("targettype", targettype);
										isb.putFieldValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
										isb.putFieldValue("checkeduser", checkeduserids[i]);
										isb.putFieldValue("checkedusername", userid_name.get(checkeduserids[i]) + "");
										isb.putFieldValue("scoreuser", scorers[k]);
										isb.putFieldValue("scoreusertype", scorervo.getStringValue("id"));
										isb.putFieldValue("groupweights", scorervo.getStringValue("weights", "")); // 评分组的权重
										isb.putFieldValue("groupweightstype", scorervo.getStringValue("weightstype", "")); // 评分组的权重方式
										isb.putFieldValue("weights", targetvos[j].getStringValue("weights", "")); // 指标权重
										isb.putFieldValue("checkdate", param.get("month") + "");
										isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
										isb.putFieldValue("logid", param.get("logid") + "");
										isb.putFieldValue("scoretype", scorervo.getStringValue("scoretype", "手动打分")); // 后来在高管考核方案中增加了这个字段
										if (cuse.containsKey(key)) { // 就是说本月不该考但是上月有
											isb.putFieldValue("checkscore", cuse.get(key).getStringValue("checkscore", ""));
											isb.putFieldValue("status", "待提交");
										}
										sqls.add(isb.getSQL());
									}
									if (!"Y".equals(isignoreseq)) { //忽略优先级的
										userid_scorerid.put(scorers[k] + "_" + checkeduserids[i] + "_", scorervo.getStringValue("id")); // +
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void otherCheck(HashMap param, HashVO planvo, HashVO scorervo, HashVO objvo, HashMap userid_name, HashMap userid_deptid, String[] scorers, LinkedHashMap userid_scorerid, LinkedHashMap userid_planid, HashVO[] _targetvos, List sqls, HashMap userid_postids) throws Exception {
		String[] checkeduserids = (String[]) userid_name.keySet().toArray(new String[0]); // 被考核人
		HashVO[] targetvos = null;
		HashMap<String, HashVO> ishave = (HashMap<String, HashVO>) param.get("person");
		HashMap<String, HashVO> cuse = (HashMap<String, HashVO>) param.get("cuse");
		HashMap uncheckuser = (HashMap) param.get("uncheckuser");
		String isupdate = param.get("isupdate") + "";
		String checkeduser = null;
		String scoreuser = null;
		String targetid = null;
		String targettype = null;
		if (checkeduserids != null && checkeduserids.length > 0) {
			String isignoreseq = planvo.getStringValue("ignoreseq", "N"); //是否需要忽略优先级。忽略优先级后，此记录一定会加入到考核打分中。
			for (int i = 0; i < checkeduserids.length; i++) { //循环被考核人
				if (!"Y".equals(isignoreseq) && userid_planid.containsKey(checkeduserids[i]) && !((String) userid_planid.get(checkeduserids[i])).equals(planvo.getStringValue("id"))) {
					continue;
				}
				if (!"Y".equals(isignoreseq)) {
					userid_planid.put(checkeduserids[i], planvo.getStringValue("id"));
				}
				if (planvo.getStringValue("targetsource", "").equals("部门定性指标")) { // 如果是部门定性指标则要找一下当前被考核人要考那些指标
					targetvos = getTargetByPostId(_targetvos, userid_postids.get(checkeduserids[i]) + "", "rleader");
				} else if (planvo.getStringValue("targetsource", "").equals("高管定性指标")) { // 如果是高管定性指标则要找当前人的指标是哪些通过岗位来找
					targetvos = getTargetByPostId(_targetvos, userid_postids.get(checkeduserids[i]) + "", "checkedpost");
				} else {
					targetvos = _targetvos;
				}
				if (targetvos == null || targetvos.length <= 0) {
					continue;
				}
				for (int j = 0; j < targetvos.length; j++) {//得到考核的指标。
					if (scorers != null && scorers.length > 0) {
						HashMap scorermap = new HashMap();
						for (int k = 0; k < scorers.length; k++) {
							if (scorers[k] != null && !"".equals(scorers[k]) && ("Y".equals(isignoreseq) || !userid_scorerid.containsKey(scorers[k] + "_" + checkeduserids[i] + "_") || ((String) userid_scorerid.get(scorers[k] + "_" + checkeduserids[i] + "_")).equals(scorervo.getStringValue("id"))) && !checkeduserids[i].equals(scorers[k])
									&& ("Y".equals(isignoreseq) || !userid_planid.containsKey(checkeduserids[i]) || ((String) userid_planid.get(checkeduserids[i])).equals(planvo.getStringValue("id")))) {
								if (scorermap.containsKey(scorers[k])) {
									continue; //
								}
								if (uncheckuser != null && (uncheckuser.containsKey(scorers[k]) || uncheckuser.containsKey(checkeduserids[i]))) {
									continue;
								}
								scorermap.put(scorers[k], "");
								checkeduser = checkeduserids[i];
								scoreuser = scorers[k];
								targetid = targetvos[j].getStringValue("id", "");
								String checktype = objvo.getStringValue("name");
								targettype = planvo.getStringValue("targetsource", "员工定性指标");
								if (targettype.equals("高管定性指标")) { //如果是高管定性指标，就是董事长垂直考核高管
									checktype = SalaryTBUtil.ggCheckTargetType;
								}
								String key = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
								if ("Y".equals(isupdate) && ishave.containsKey(key)) {
									// 这条记录是在计算分管部门得分的时候用的到 只需要更新组的权重就可以了
									targetvos[j].setAttributeValue("checktype", objvo.getStringValue("name"));
									targetvos[j].setAttributeValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
									targetvos[j].setAttributeValue("groupweights", scorervo.getStringValue("weights", ""));
									targetvos[j].setAttributeValue("groupweightstype", scorervo.getStringValue("weightstype", ""));
									targetvos[j].setAttributeValue("scoretype", scorervo.getStringValue("scoretype", ""));
									if (compareIsCanUpdatePerson(targetvos[j], ishave.get(key))) {
										UpdateSQLBuilder isb = new UpdateSQLBuilder("sal_person_check_score");
										isb.setWhereCondition(" id=" + ishave.get(key).getStringValue("id"));
										isb.putFieldValue("targetname", targetvos[j].getStringValue("name", ""));
										isb.putFieldValue("checktype", checktype);
										isb.putFieldValue("targettype", targettype);
										isb.putFieldValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
										isb.putFieldValue("checkeduser", checkeduserids[i]);
										isb.putFieldValue("checkedusername", userid_name.get(checkeduserids[i]) + "");
										isb.putFieldValue("scoreuser", scorers[k]);
										isb.putFieldValue("scoreusertype", scorervo.getStringValue("id"));
										isb.putFieldValue("groupweights", scorervo.getStringValue("weights", "")); // 评分组的权重
										isb.putFieldValue("groupweightstype", scorervo.getStringValue("weightstype", "")); // 评分组的权重方式
										// 因为是员工定性所以不用重新算
										// 如果是部门的就要根据权重重新算了
										isb.putFieldValue("weights", targetvos[j].getStringValue("weights", "")); // 指标权重
										isb.putFieldValue("checkdate", param.get("month") + "");
										isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
										isb.putFieldValue("logid", param.get("logid") + "");
										isb.putFieldValue("scoretype", scorervo.getStringValue("scoretype", "手动打分"));
										sqls.add(isb.getSQL());
									}
									// 有的处理过了就删除 最终都处理完之后剩下的就是需要删除的啦
									ishave.remove(key);
								} else {
									InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_check_score");
									isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_SCORE", 8000));
									isb.putFieldValue("targetid", targetvos[j].getStringValue("id", ""));
									isb.putFieldValue("targetname", targetvos[j].getStringValue("name", ""));
									isb.putFieldValue("checktype", checktype);
									isb.putFieldValue("targettype", planvo.getStringValue("targetsource", "员工定性指标"));
									isb.putFieldValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
									isb.putFieldValue("checkeduser", checkeduserids[i]);
									isb.putFieldValue("checkedusername", userid_name.get(checkeduserids[i]) + "");
									isb.putFieldValue("scoreuser", scorers[k]);
									isb.putFieldValue("scoreusertype", scorervo.getStringValue("id"));
									isb.putFieldValue("groupweights", scorervo.getStringValue("weights", "")); // 评分组的权重
									isb.putFieldValue("groupweightstype", scorervo.getStringValue("weightstype", "")); // 评分组的权重方式
									isb.putFieldValue("weights", targetvos[j].getStringValue("weights", "")); // 指标权重
									isb.putFieldValue("checkdate", param.get("month") + "");
									isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
									isb.putFieldValue("logid", param.get("logid") + "");
									isb.putFieldValue("scoretype", scorervo.getStringValue("scoretype", "手动打分")); // 后来在高管考核方案中增加了这个字段
									if (cuse.containsKey(key)) { // 就是说本月不该考但是上月有
										isb.putFieldValue("checkscore", cuse.get(key).getStringValue("checkscore", ""));
										isb.putFieldValue("status", "待提交");
									}
									sqls.add(isb.getSQL());
								}
								if (!"Y".equals(isignoreseq)) {
									userid_scorerid.put(scorers[k] + "_" + checkeduserids[i] + "_", scorervo.getStringValue("id")); // +
									userid_planid.put(checkeduserids[i], planvo.getStringValue("id"));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 如果评分方式是要取部门考核的得分进行计算是不需要向人员考核的评分表中插数据的
	 * 但是逻辑设计是会插一条没有评分人和指标信息的空记录，在计算得分的时候可以用到
	 * 
	 * @param param
	 * @param planvo
	 * @param scorervo
	 * @param objvo
	 * @param userid_name
	 * @param userid_deptid
	 * @param scorers
	 * @param userid_scorerid
	 * @param userid_planid
	 * @param _targetvos
	 * @param sqls
	 * @param userid_postids
	 * @throws Exception
	 */
	private void deptCheck(HashMap param, HashVO planvo, HashVO scorervo, HashVO objvo, HashMap userid_name, HashMap userid_deptid, String[] scorers, LinkedHashMap userid_scorerid, LinkedHashMap userid_planid, HashVO[] _targetvos, List sqls, HashMap userid_postids) throws Exception {
		String[] checkeduserids = (String[]) userid_name.keySet().toArray(new String[0]); // 被考核人
		HashMap<String, HashVO> ishave = (HashMap<String, HashVO>) param.get("person");
		HashMap<String, HashVO> cuse = (HashMap<String, HashVO>) param.get("cuse");
		String isupdate = param.get("isupdate") + "";
		HashMap uncheckuser = (HashMap) param.get("uncheckuser");
		if (checkeduserids != null && checkeduserids.length > 0) {
			String checkeduser = null;
			String scoreuser = null;
			String targetid = null;
			String targettype = null;
			String isignoreseq = planvo.getStringValue("ignoreseq", "N"); //是否需要忽略优先级。忽略优先级后，此记录一定会加入到考核打分中。
			for (int i = 0; i < checkeduserids.length; i++) {
				if (!"Y".equals(isignoreseq) && userid_planid.containsKey(checkeduserids[i]) && !((String) userid_planid.get(checkeduserids[i])).equals(planvo.getStringValue("id"))) {
					continue;
				}
				if (uncheckuser != null && uncheckuser.containsKey(checkeduserids[i])) {
					continue;
				}
				if (!"Y".equals(isignoreseq)) { //被忽略掉优先级的。也不影响其他方案。
					userid_planid.put(checkeduserids[i], planvo.getStringValue("id"));
				}
				checkeduser = checkeduserids[i];
				scoreuser = "";
				targetid = "";
				targettype = planvo.getStringValue("targetsource", "员工定性指标");
				String checktype = objvo.getStringValue("name");
				if (targettype.equals("高管定性指标")) { //如果是高管定性指标，就是董事长垂直考核高管
					checktype = SalaryTBUtil.ggCheckTargetType;
				}
				String key = checkeduser + "_" + scoreuser + "_" + targetid + "_" + targettype;
				if ("Y".equals(isupdate) && ishave.containsKey(key)) {
					// 这条记录是在计算分管部门得分的时候用的到 只需要更新组的权重就可以了
					HashVO temp = new HashVO();
					temp.setAttributeValue("name", "");
					temp.setAttributeValue("weights", "");
					temp.setAttributeValue("checktype", checktype);
					temp.setAttributeValue("scoretype", scorervo.getStringValue("scoretype"));
					temp.setAttributeValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
					temp.setAttributeValue("groupweights", scorervo.getStringValue("weights", ""));
					temp.setAttributeValue("groupweightstype", scorervo.getStringValue("weightstype", ""));
					if (compareIsCanUpdatePerson(temp, ishave.get(key))) {
						UpdateSQLBuilder isb = new UpdateSQLBuilder("sal_person_check_score");
						isb.putFieldValue("groupweights", scorervo.getStringValue("weights", ""));
						isb.putFieldValue("groupweightstype", scorervo.getStringValue("weightstype", ""));
						isb.putFieldValue("scoretype", scorervo.getStringValue("scoretype"));
						isb.setWhereCondition(" id=" + ishave.get(key).getStringValue("id"));
						sqls.add(isb.getSQL());
					}
					// 有的处理过了就删除 最终都处理完之后剩下的就是需要删除的啦
					ishave.remove(key);
				} else {
					InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_check_score");
					isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_SCORE", 8000));
					isb.putFieldValue("targetid", "");
					isb.putFieldValue("targetname", "");
					isb.putFieldValue("checktype", checktype);
					isb.putFieldValue("targettype", targettype);
					isb.putFieldValue("scorerweightstype", planvo.getStringValue("scorerweightstype"));
					isb.putFieldValue("checkeduser", checkeduserids[i]);
					isb.putFieldValue("checkedusername", userid_name.get(checkeduserids[i]) + "");
					isb.putFieldValue("scoreuser", "");
					isb.putFieldValue("scoreusertype", scorervo.getStringValue("id"));
					isb.putFieldValue("groupweights", scorervo.getStringValue("weights", "")); // 评分组的权重
					isb.putFieldValue("groupweightstype", scorervo.getStringValue("weightstype", "")); // 评分组的权重方式
					isb.putFieldValue("weights", ""); // 指标权重
					isb.putFieldValue("checkdate", param.get("month") + "");
					isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
					isb.putFieldValue("logid", param.get("logid") + "");
					isb.putFieldValue("scoretype", scorervo.getStringValue("scoretype", "手动打分")); // 后来在高管考核方案中增加了这个字段
					if (cuse.containsKey(key)) { // 就是说本月不该考但是上月有
						// 理论上在这个方法不存在这段逻辑
						isb.putFieldValue("checkscore", cuse.get(key).getStringValue("checkscore", ""));
						isb.putFieldValue("status", "待提交");
					}
					isb.putFieldValue("status", "已提交");
					sqls.add(isb.getSQL());
				}
			}
		}
	}

	private HashVO[] getTargetByPostId(HashVO[] alltargets, String postid, String postcolumnname) {
		if (postid == null || "".equals(postid)) {
			return null;
		}
		if (alltargets == null || alltargets.length <= 0) {
			return null;
		}
		List<HashVO> rtnlist = new ArrayList<HashVO>();
		String rleader = null;
		for (int i = 0; i < alltargets.length; i++) {
			rleader = alltargets[i].getStringValue(postcolumnname, "");
			if (getTb().containTwoArrayCompare(getTb().split(rleader, ";"), getTb().split(postid, ";"))) {
				rtnlist.add(alltargets[i]);
			}
		}
		return rtnlist.toArray(new HashVO[0]);
	}

	/**
	 * 复用 无特别意义
	 * 
	 * @param vo
	 * @param isb
	 * @param scoruser
	 * @param checkdeptid
	 * @param checkdeptname
	 * @param checkdate
	 * @param logid
	 */
	public void copyProperty(HashVO vo, InsertSQLBuilder isb, String scoruser, String checkdeptid, String checkdeptname, String checkdate, String logid, String checktype, String createdeptid) {
		isb.putFieldValue("targetid", vo.getStringValue("id"));
		isb.putFieldValue("targetcode", vo.getStringValue("code"));
		isb.putFieldValue("targetname", vo.getStringValue("name"));
		isb.putFieldValue("targettype", vo.getStringValue("type"));
		isb.putFieldValue("evalstandard", vo.getStringValue("evalstandard"));
		isb.putFieldValue("targetdescr", vo.getStringValue("descr"));
		isb.putFieldValue("targetdefine", vo.getStringValue("define"));
		isb.putFieldValue("weights", vo.getStringValue("weights"));
		isb.putFieldValue("lasteditdate", getTb().getCurrDate());
		isb.putFieldValue("scoreuser", scoruser);
		isb.putFieldValue("checkeddept", checkdeptid);
		isb.putFieldValue("checkeddeptname", checkdeptname);
		isb.putFieldValue("checkdate", checkdate);
		isb.putFieldValue("status", "");
		isb.putFieldValue("logid", logid);
		isb.putFieldValue("checktype", checktype);
		isb.putFieldValue("createdeptid", createdeptid);
		isb.putFieldValue("scoredeptid", "");
		isb.putFieldValue("scoredeptname", "");
	}

	public void copyProperty2(HashVO vo, UpdateSQLBuilder isb, String scoruser, String checkdeptid, String checkdeptname, String checkdate, String logid, String score, String checktype, String createdeptid) {
		isb.putFieldValue("targetid", vo.getStringValue("id"));
		isb.putFieldValue("targetcode", vo.getStringValue("code"));
		isb.putFieldValue("targetname", vo.getStringValue("name"));
		isb.putFieldValue("targettype", vo.getStringValue("type"));
		isb.putFieldValue("evalstandard", vo.getStringValue("evalstandard"));
		isb.putFieldValue("targetdescr", vo.getStringValue("descr"));
		isb.putFieldValue("targetdefine", vo.getStringValue("define"));
		isb.putFieldValue("weights", vo.getStringValue("weights"));
		if (score.indexOf("a") < 0) {
			isb.putFieldValue("checkscore", new BigDecimal(vo.getStringValue("weights")).multiply(new BigDecimal("100").subtract(new BigDecimal(score))).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP).toString());
		}
		isb.putFieldValue("lasteditdate", getTb().getCurrDate());
		isb.putFieldValue("scoreuser", scoruser);
		isb.putFieldValue("checkeddept", checkdeptid);
		isb.putFieldValue("checkeddeptname", checkdeptname);
		isb.putFieldValue("checkdate", checkdate);
		isb.putFieldValue("logid", logid);
		isb.putFieldValue("checktype", checktype);
		isb.putFieldValue("createdeptid", createdeptid);
		isb.putFieldValue("scoredeptid", "");
		isb.putFieldValue("scoredeptname", "");
	}

	/**
	 * 根据考核日期获取考评进度的vo 部门考核进度统计
	 * 性能有问题! 用SQL重新实现, Gwang 2014-4-1
	 */
	public BillCellVO getTargetCheckProcessVO(String month) throws Exception {
		BillCellVO cell = new BillCellVO();
		// 需要定义一个参数
		String corpname = SystemOptions.getStringValue("工资结算最小单位", "行社");
		String blcorpdept = getSdmo().getLoginUserParentCorpItemValueByType(corpname, "分行", "id");
		StringBuffer sb = new StringBuffer("select * from sal_target_check_log where createcorp='" + blcorpdept + "' ");
		if (month != null && !"".equals(month)) {
			sb.append(" and checkdate='" + month + "' ");
		}
		sb.append(" order by checkdate desc ");
		// 取得所有的日志表 一条日志记录代表一次考核
		HashVO[] logvo = getDmo().getHashVoArrayByDS(null, sb.toString());
		if (logvo == null || logvo.length <= 0) {
			cell.setRowlength(1);
			cell.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			cell.setCellItemVOs(items);
			return cell;
		} else {
			List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
			for (int i = 0; i < logvo.length; i++) {
				String logid = logvo[i].getStringValue("id");
				// 日志标题
				BillCellItemVO[] item0 = new BillCellItemVO[4];
				item0[0] = getBillTitleCellItemVO(logvo[i].getStringValue("name"));
				item0[0].setHalign(2);
				item0[0].setSpan("1,4");
				item0[1] = new BillCellItemVO();
				item0[2] = new BillCellItemVO();
				items.add(item0);

				// 列表标题
				BillCellItemVO[] item1 = new BillCellItemVO[4];
				item1[0] = getBillTitleCellItemVO("评分人/部门");
				item1[1] = getBillTitleCellItemVO("评分类型");
				item1[2] = getBillTitleCellItemVO("评分情况");
				item1[3] = getBillTitleCellItemVO("评分状态");
				items.add(item1);

				// 部门考核(垂直打分+平行打分+部门定量考核)
				StringBuffer sql = new StringBuffer();
				sql.append(" select * from (");
				sql.append("	select p.name as name, '垂直' as type, count(s.id)as num_total, sum(case when checkscore is null then 0 else 1 end)as num_scored, sum(case when s.status ='已提交' then 1 else 0 end)as num_submit");
				sql.append("	from sal_dept_check_score s left join v_sal_personinfo p on s.scoreuser = p.id");
				sql.append("	where logid='" + logid + "' and (checktype = '垂直R' or checktype = '垂直P') group by name order by deptseq,postseq)tmp");
				sql.append(" union all");
				sql.append(" select * from (");
				sql.append("	select d.name as name, '平行' as type,  count(s.id)as num_total, sum(case when checkscore is null then 0 else 1 end)as num_scored, sum(case when s.status ='已提交' then 1 else 0 end)as num_submit");
				sql.append("	from sal_dept_check_score s left join pub_corp_dept d on s.scoredeptid = d.id");
				sql.append("	where logid='" + logid + "' and (checktype = '平行R' or checktype = '平行P')");
				sql.append("	group by name order by d.linkcode)tmp");
				sql.append(" union all");
				sql.append(" select * from (");
				sql.append("	select '全行' as name, '部门定量考核' as type, count(id)as num_total, sum(case when checkscore is null then 0 else 1 end)as num_scored, sum(case when status ='已提交' then 1 else 0 end)as num_submit");
				sql.append("	from sal_dept_check_score where logid='" + logid + "' and (checktype = '' or checktype is null) and targettype='部门定量指标')tmp");

				HashVO hvo[] = getDmo().getHashVoArrayByDS(null, sql.toString());
				for (int n = 0; n < hvo.length; n++) {
					BillCellItemVO[] item = new BillCellItemVO[4];
					item[0] = getBillNormalCellItemVO(n, hvo[n].getStringValue("name"));
					item[1] = getBillNormalCellItemVO(n, hvo[n].getStringValue("type"));
					int num_total = hvo[n].getIntegerValue("num_total", 0);
					int num_scored = hvo[n].getIntegerValue("num_scored", 0);
					int num_submit = hvo[n].getIntegerValue("num_submit", 0);
					item[2] = getBillNormalCellItemVO(n, num_scored + "/" + num_total);
					String status = "";
					if (num_scored == 0) {
						status = "未评分";
					} else if (num_scored == num_total) {
						if (num_submit == num_total) {
							status = "已提交";
						} else {
							status = "已完成";
						}
					} else {
						status = "评分中";
					}
					item[3] = getBillNormalCellItemVO(i, status);
					item[3].setForeground(getColorByState(item[3].getCellvalue()));
					items.add(item);
				}

				// 如果有多个日志则加一个空行分隔
				if (i < logvo.length - 1) {
					BillCellItemVO[] itemn = new BillCellItemVO[3];
					itemn[0] = getBillNormalCellItemVO(i, "");
					itemn[0].setSpan("1,4");
					itemn[1] = new BillCellItemVO();
					itemn[2] = new BillCellItemVO();
					itemn[3] = new BillCellItemVO();
					items.add(itemn);
				}
			}

			cell.setRowlength(items.size());
			cell.setCollength(4);
			cell.setCellItemVOs((BillCellItemVO[][]) items.toArray(new BillCellItemVO[0][0]));
			// 调整单元格 的长度
			int li_allowMaxColWidth = 375;
			BillCellItemVO[][] items_ = cell.getCellItemVOs();
			FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
			for (int j = 0; j < items_[0].length; j++) {
				int li_maxwidth = 0;
				String str_cellValue = null;
				for (int i = 1; i < items_.length; i++) {
					str_cellValue = items_[i][j].getCellvalue();
					if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items_[i][j].getSpan())) {
						int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
						if (li_width > li_maxwidth) {
							li_maxwidth = li_width;
						}
					}
				}
				li_maxwidth = li_maxwidth + 13;
				if (li_maxwidth > li_allowMaxColWidth) {
					li_maxwidth = li_allowMaxColWidth;
				}
				for (int i = 1; i < items_.length; i++) {
					str_cellValue = items_[i][j].getCellvalue();
					if (str_cellValue != null && !str_cellValue.trim().equals("")) {
						items_[i][j].setColwidth("" + li_maxwidth);
					}
				}
			}
			return cell;
		}

	}

	/**
	 * 根据考核日期获取考评进度的vo 员工考核进度统计
	 * 2014-4-1郝明重新修改人员打分进度统计查询逻辑。需要利用数据库性能优越性。
	 */
	public BillCellVO getPersonCheckProcessVO(String month) throws Exception {
		BillCellVO cell = new BillCellVO();
		String corpname = SystemOptions.getStringValue("工资结算最小单位", "行社");
		String blcorpdept = getSdmo().getLoginUserParentCorpItemValueByType(corpname, "分行", "id");
		StringBuffer sb = new StringBuffer("select * from sal_target_check_log where createcorp='" + blcorpdept + "' ");
		if (month != null && !"".equals(month)) {
			sb.append(" and checkdate='" + month + "' ");
		}
		sb.append(" order by checkdate desc ");
		// 取得所有的日志表 一条日志记录代表一次考核
		HashVO[] logvo = getDmo().getHashVoArrayByDS(null, sb.toString());
		if (logvo == null || logvo.length <= 0) {
			cell.setRowlength(1);
			cell.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			cell.setCellItemVOs(items);
		} else {
			List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
			for (int m = 0; m < logvo.length; m++) {
				String logid = logvo[m].getStringValue("id");

				StringBuffer sql = new StringBuffer(); //未完成查询
				sql.append("select scoreuser,count(id) from sal_person_check_score where 1=1 and logid = '" + logid + "' ");
				sql.append(" and (checkscore is null or checkscore='' )");
				sql.append(" group by scoreuser");
				HashMap unchecked = getDmo().getHashMapBySQLByDS(null, sql.toString());
				String[] allunfinisheduser = (String[]) unchecked.keySet().toArray(new String[0]);

				StringBuffer sql2 = new StringBuffer();//总量查询
				sql2.append("select scoreuser,count(id) from sal_person_check_score where 1=1 and logid = '" + logid + "' ");
				sql2.append(" group by scoreuser");
				HashMap all = getDmo().getHashMapBySQLByDS(null, sql2.toString());

				StringBuffer sql3 = new StringBuffer(); //已提交的数量
				sql3.append("select scoreuser,count(id) from sal_person_check_score where 1=1 and status='已提交' and logid = '" + logid + "' ");
				sql3.append(" group by scoreuser");

				HashMap submited = getDmo().getHashMapBySQLByDS(null, sql3.toString());

				String incondition = getTb().getInCondition((String[]) all.keySet().toArray(new String[0]));
				HashVO hvo[] = getDmo().getHashVoArrayByDS(null, "select id,deptname,name from v_sal_personinfo where id in(" + incondition + ")  order by linkcode,postseq");
				BillCellItemVO[] _items0 = new BillCellItemVO[4];
				_items0[0] = getBillTitleCellItemVO(logvo[m].getStringValue("name"));
				_items0[0].setHalign(2);
				_items0[0].setSpan("1,4");
				_items0[1] = getBillTitleCellItemVO("");
				_items0[2] = getBillTitleCellItemVO("");
				_items0[3] = getBillTitleCellItemVO("");
				items.add(_items0);

				BillCellItemVO[] _items1 = new BillCellItemVO[4];
				_items1[0] = getBillTitleCellItemVO("机构");
				_items1[1] = getBillTitleCellItemVO("姓名");
				_items1[2] = getBillTitleCellItemVO("评分情况");
				_items1[3] = getBillTitleCellItemVO("评分状态");
				items.add(_items1);
				for (int i = 0; i < hvo.length; i++) {
					BillCellItemVO[] _items = new BillCellItemVO[4];
					_items[0] = getBillNormalCellItemVO(i, hvo[i].getStringValue("deptname"));
					_items[1] = getBillNormalCellItemVO(i, hvo[i].getStringValue("name"));
					String userid = hvo[i].getStringValue("id");
					int uncheckcount = 0;
					if (unchecked.containsKey(userid)) {
						uncheckcount = Integer.parseInt((String) unchecked.get(userid));
					}
					_items[2] = getBillNormalCellItemVO(i, ((Integer.parseInt((String) all.get(userid)) - uncheckcount)) + "/" + all.get(userid));
					if (uncheckcount == 0) {
						String status = "待提交";

						if (submited != null && submited.containsKey(userid)) {
							int havesubmit = Integer.parseInt((String) submited.get(userid));
							if (havesubmit == (Integer.parseInt((String) all.get(userid)))) { //已提交的等于总量
								status = "已完成";
							}
						}
						_items[3] = getBillNormalCellItemVO(i, status);
					} else if (uncheckcount == (Integer.parseInt((String) all.get(userid)))) {
						_items[3] = getBillNormalCellItemVO(i, "未评分");
					} else {
						_items[3] = getBillNormalCellItemVO(i, "评分中");
					}
					_items[3].setForeground(getColorByState(_items[3].getCellvalue()));
					items.add(_items);
				}

				//员工定量
				BillCellItemVO[] _items = new BillCellItemVO[4];
				_items[0] = getBillNormalCellItemVO(hvo.length, "员工定量指标");
				_items[1] = getBillNormalCellItemVO(hvo.length, "员工定量指标");
				int uncheckcount = 0;
				String userid = "-99999";
				if (unchecked.containsKey("-99999")) {
					uncheckcount = Integer.parseInt((String) unchecked.get(userid));
				}
				_items[2] = getBillNormalCellItemVO(hvo.length, ((Integer.parseInt((String) all.get(userid)) - uncheckcount)) + "/" + all.get(userid));
				if (uncheckcount == 0) {
					String status = "待提交";

					if (submited != null && submited.containsKey(userid)) {
						int havesubmit = Integer.parseInt((String) submited.get(userid));
						if (havesubmit == (Integer.parseInt((String) all.get(userid)))) { //已提交的等于总量
							status = "已完成";
						}
					}
					_items[3] = getBillNormalCellItemVO(hvo.length, status);
				} else if (uncheckcount == (Integer.parseInt((String) all.get(userid)))) {
					_items[3] = getBillNormalCellItemVO(hvo.length, "未评分");
				} else {
					_items[3] = getBillNormalCellItemVO(hvo.length, "评分中");
				}
				_items[3].setForeground(getColorByState(_items[3].getCellvalue()));
				items.add(_items);
			}
			cell.setRowlength(items.size());
			cell.setCollength(4);
			BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
			formatClen(allitems);
			formatSpan(allitems, new int[1]);
			cell.setCellItemVOs(allitems);
		}
		return cell;
	}

	/**
	 * 生成某一种类型考核的itemvo
	 * 废弃 Gwang 2014-4-1
	 * @param  item
	 * @param rleaderscore
	 * @param usercolumn
	 * @param id_namemap
	 */
	//	private void getBillCellItemVO(List<BillCellItemVO[]> item, HashVO[] rleaderscore, String usercolumn, HashMap<String, String> id_namemap, boolean ishavedept, String checktype) {
	//		if (rleaderscore == null || rleaderscore.length <= 0) {
	//			return;
	//		}
	//		LinkedHashMap<String, Integer> rlcount = new LinkedHashMap<String, Integer>();
	//		LinkedHashMap<String, Integer> rluncount = new LinkedHashMap<String, Integer>();
	//		HashMap<String, String> user_deptname = new HashMap<String, String>();
	//		HashMap<String, HashMap<String, String>> statemap = new HashMap<String, HashMap<String, String>>();
	//		String userid = null;
	//		for (int i = 0; i < rleaderscore.length; i++) {
	//			userid = rleaderscore[i].getStringValue(usercolumn);
	//			if (ishavedept) {
	//				user_deptname.put(userid, rleaderscore[i].getStringValue("checkeduserdeptname"));
	//				if ("-99999".equals(userid)) {
	//					user_deptname.put("-99999", "员工定量考核");
	//				}
	//			}
	//			if (statemap.containsKey(userid)) {
	//				statemap.get(userid).put(rleaderscore[i].getStringValue("status", ""), "");
	//			} else {
	//				HashMap<String, String> map = new HashMap<String, String>();
	//				map.put(rleaderscore[i].getStringValue("status", ""), "");
	//				statemap.put(userid, map);
	//			}
	//			if (rlcount.containsKey(userid)) {
	//				rlcount.put(userid, rlcount.get(userid) + 1);
	//			} else {
	//				rlcount.put(userid, 1);
	//			}
	//			if ("".equals(rleaderscore[i].getStringValue("checkscore", ""))) {
	//				if (rluncount.containsKey(userid)) {
	//					rluncount.put(userid, rluncount.get(userid) + 1);
	//				} else {
	//					rluncount.put(userid, 1);
	//				}
	//			}
	//		}
	//		String[] userids = rlcount.keySet().toArray(new String[0]);
	//		int count = 0;
	//		String statestr = "";
	//		for (int j = 0; j < userids.length; j++) {
	//			HashMap<String, String> state = statemap.get(userids[j]);
	//			count = rlcount.get(userids[j]);
	//			String[] states = (String[]) state.keySet().toArray(new String[0]);
	//			if (states != null && states.length > 0) {
	//				for (int i = 0; i < states.length; i++) {
	//					if (states[i].equals("")) {
	//						if (!"".equals(statestr)) {
	//							statestr = "评分中";
	//							break;
	//						} else {
	//							statestr = "未评分";
	//						}
	//					} else if (scoreFinishState.indexOf(states[i]) >= 0) {
	//						if (!"已完成".equals(statestr) && !"".equals(statestr)) {
	//							statestr = "评分中";
	//							break;
	//						} else {
	//							statestr = "已完成";
	//						}
	//					} else {
	//						statestr = "评分中";
	//						break;
	//					}
	//				}
	//			}
	//			BillCellItemVO[] one = null;
	//			if (ishavedept) {
	//				one = new BillCellItemVO[4];
	//				one[0] = getBillNormalCellItemVO(item.size(), user_deptname.get(userids[j]) + "");
	//				one[1] = getBillNormalCellItemVO(item.size(), id_namemap.get(userids[j]) + "");
	//
	//				one[3] = getBillNormalCellItemVO(item.size(), statestr);
	//				one[3].setCellkey(userids[j]);
	//				one[3].setForeground(getColorByState(statestr));
	//
	//				one[2] = getBillNormalCellItemVO(item.size(), statestr);
	//				one[2].setCellkey(userids[j]);
	//				if (rluncount.containsKey(userids[j])) {
	//					one[2].setCellvalue((count - rluncount.get(userids[j])) + "/" + count);
	//				} else { // 全部提交
	//					one[2].setCellvalue(count + "/" + count);
	//				}
	//			} else {
	//				one = new BillCellItemVO[4];
	//				one[0] = getBillNormalCellItemVO(item.size(), id_namemap.get(userids[j]) + "");
	//				one[1] = getBillNormalCellItemVO(item.size(), checktype);
	//
	//				one[3] = getBillNormalCellItemVO(item.size(), statestr);
	//				one[3].setCellkey(userids[j]);
	//				one[3].setForeground(getColorByState(statestr));
	//
	//				one[2] = getBillNormalCellItemVO(item.size(), "");
	//				one[2].setCellkey(userids[j]);
	//				if (rluncount.containsKey(userids[j])) {
	//					one[2].setCellvalue((count - rluncount.get(userids[j])) + "/" + count);
	//				} else { // 全部提交
	//					one[2].setCellvalue(count + "/" + count);
	//				}
	//			}
	//			item.add(one);
	//			statestr = "";
	//		}
	// }
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

	/**
	 * 得分统计
	 * 
	 * @return
	 */
	public BillCellVO getScoreVO(String month) throws Exception {
		String id = getDmo().getStringValueByDS(null, "select id,name from sal_target_check_log where checkdate='" + month + "'");
		return getScoreVOByID(id, false, false, true, null, false, true);
	}

	/**
	 * 转换成零点几的权重
	 * 
	 * @return
	 * @throws Exception
	 */
	private BigDecimal[] getDeptRight() throws Exception {
		if (allrights == null) {
			String rightstr = SystemOptions.getStringValue("部门考核权重配置", "垂直=60;平行=40;垂直R=70;垂直P=30;平行R=70;平行P=30;");
			HashMap parammap = getTb().convertStrToMapByExpress(rightstr);
			BigDecimal c_ = new BigDecimal(60);
			if (parammap.containsKey("垂直")) {
				c_ = new BigDecimal(parammap.get("垂直") + "");
			}
			BigDecimal p_ = new BigDecimal(40);
			if (parammap.containsKey("平行")) {
				p_ = new BigDecimal(parammap.get("平行") + "");
			}
			BigDecimal c = c_.divide(c_.add(p_), 6, BigDecimal.ROUND_HALF_UP);
			BigDecimal p = p_.divide(c_.add(p_), 6, BigDecimal.ROUND_HALF_UP);
			BigDecimal cr_ = new BigDecimal(60);
			if (parammap.containsKey("垂直R")) {
				cr_ = new BigDecimal(parammap.get("垂直R") + "");
			}
			BigDecimal cp_ = new BigDecimal(60);
			if (parammap.containsKey("垂直P")) {
				cp_ = new BigDecimal(parammap.get("垂直P") + "");
			}
			BigDecimal cr = cr_.divide(cr_.add(cp_), 6, BigDecimal.ROUND_HALF_UP);
			BigDecimal cp = cp_.divide(cr_.add(cp_), 6, BigDecimal.ROUND_HALF_UP);
			BigDecimal pr_ = new BigDecimal(60);
			if (parammap.containsKey("平行R")) {
				pr_ = new BigDecimal(parammap.get("平行R") + "");
			}
			BigDecimal pp_ = new BigDecimal(60);
			if (parammap.containsKey("平行P")) {
				pp_ = new BigDecimal(parammap.get("平行P") + "");
			}
			BigDecimal pr = pr_.divide(pr_.add(pp_), 6, BigDecimal.ROUND_HALF_UP);
			BigDecimal pp = pp_.divide(pr_.add(pp_), 6, BigDecimal.ROUND_HALF_UP);
			allrights = new BigDecimal[] { c, p, cr, cp, pr, pp };
		}
		return allrights;
	}

	/**
	 * 考核结束了在查询部门得分统计时 从结果表中直接取,不再进行计算 直接从结果表里查 返回vo比较简单
	 * 
	 * @param id
	 * @param checkedDeptid
	 * @param issort
	 * @param iswatch
	 * @return
	 */
	public BillCellVO getScoreVOByResult(String id, String checkedDeptid, boolean issort, boolean iswatch) throws Exception {
		BillCellVO vo = new BillCellVO();
		String logname = getDmo().getStringValueByDS(null, "select name from sal_target_check_log where id=" + id);
		StringBuffer sb = new StringBuffer("select s.* from sal_target_check_result s left join pub_corp_dept d on s.checkeddeptid=d.id where s.logid=" + id);
		if (checkedDeptid != null) {
			sb.append(" and s.checkeddeptid='" + checkedDeptid + "'");
		}
		sb.append(" order by d.linkcode ");
		HashMap dept_finares = getDmo().getHashMapBySQLByDS(null, "select checkeddeptid, currscore+revisescore from sal_target_check_revise_result where logid=" + id);
		// 算综合分的排名
		HashMap score_seq = null;
		if (dept_finares != null && dept_finares.size() > 0) {
			String[] allscores = (String[]) dept_finares.values().toArray(new String[0]);
			List scores = new ArrayList();
			for (int s = 0; s < allscores.length; s++) {
				scores.add(allscores[s]);
			}
			score_seq = getSort(scores);
		}
		if (score_seq == null) {
			score_seq = new HashMap();
		}

		HashVO[] allresult = getDmo().getHashVoArrayByDS(null, sb.toString());
		if (allresult != null && allresult.length > 0) {
			LinkedHashMap<String, HashVO> param = new LinkedHashMap<String, HashVO>();
			for (int i = 0; i < allresult.length; i++) {
				String checkeddeptid = allresult[i].getStringValue("checkeddeptid");
				if (param.containsKey(allresult[i].getStringValue("checkeddeptid"))) {
					if ("部门定量指标".equals(allresult[i].getStringValue("targettype"))) {
						param.get(checkeddeptid).setAttributeValue("status2", allresult[i].getStringValue("status2"));
						param.get(checkeddeptid).setAttributeValue("finalres2", allresult[i].getStringValue("finalres") + "%");
					} else {
						param.get(checkeddeptid).setAttributeValue("checkeddeptid", allresult[i].getStringValue("checkeddeptid"));
						param.get(checkeddeptid).setAttributeValue("checkeddeptname", allresult[i].getStringValue("checkeddeptname"));
						param.get(checkeddeptid).setAttributeValue("allcount", allresult[i].getStringValue("allcount"));
						param.get(checkeddeptid).setAttributeValue("finishcount", allresult[i].getStringValue("finishcount"));
						param.get(checkeddeptid).setAttributeValue("status", allresult[i].getStringValue("status"));
						param.get(checkeddeptid).setAttributeValue("finalres", allresult[i].getStringValue("finalres") + "%");
						param.get(checkeddeptid).setAttributeValue("avgres", allresult[i].getStringValue("avgres"));
						param.get(checkeddeptid).setAttributeValue("maxres", allresult[i].getStringValue("maxres"));
						param.get(checkeddeptid).setAttributeValue("sortseq", allresult[i].getStringValue("sortseq"));
					}
				} else {
					HashVO vo_ = new HashVO();
					vo_.setAttributeValue("checkeddeptid", allresult[i].getStringValue("checkeddeptid"));
					vo_.setAttributeValue("checkeddeptname", allresult[i].getStringValue("checkeddeptname"));
					vo_.setAttributeValue("allcount", allresult[i].getStringValue("allcount"));
					vo_.setAttributeValue("finishcount", allresult[i].getStringValue("finishcount"));
					if ("部门定量指标".equals(allresult[i].getStringValue("targettype"))) {
						vo_.setAttributeValue("status2", allresult[i].getStringValue("status2"));
						vo_.setAttributeValue("finalres2", allresult[i].getStringValue("finalres") + "%");
					} else {
						vo_.setAttributeValue("status", allresult[i].getStringValue("status"));
						vo_.setAttributeValue("finalres", allresult[i].getStringValue("finalres") + "%");
						vo_.setAttributeValue("avgres", allresult[i].getStringValue("avgres"));
						vo_.setAttributeValue("maxres", allresult[i].getStringValue("maxres"));
						vo_.setAttributeValue("sortseq", allresult[i].getStringValue("sortseq"));
					}
					param.put(checkeddeptid, vo_);
				}
			}
			HashVO[] allvos = param.values().toArray(new HashVO[0]);
			List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
			List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
			items0.add(getBillTitleCellItemVO(logname));
			items0.add(getBillNormalCellItemVO(0, ""));
			items0.add(getBillNormalCellItemVO(0, ""));
			// items0.add(getBillNormalCellItemVO(0, ""));
			items0.add(getBillNormalCellItemVO(0, ""));
			items0.add(getBillNormalCellItemVO(0, ""));
			items0.add(getBillNormalCellItemVO(0, ""));
			items0.add(getBillNormalCellItemVO(0, ""));
			if (issort) {
				items0.add(getBillNormalCellItemVO(0, ""));
				// items0.add(getBillNormalCellItemVO(0, ""));
				// items0.add(getBillNormalCellItemVO(0, ""));
			}
			if (iswatch) {
				items0.add(getBillNormalCellItemVO(0, ""));
			}
			items0.get(0).setSpan("1," + items0.size());
			list.add(items0.toArray(new BillCellItemVO[0]));
			List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
			items1.add(getBillTitleCellItemVO("被考核部门"));
			items1.add(getBillTitleCellItemVO("被考核指标数量"));
			// items1.add(getBillTitleCellItemVO("已完成数量"));
			items1.add(getBillTitleCellItemVO("定性考核状态"));
			items1.add(getBillTitleCellItemVO("定性考核得分"));
			items1.add(getBillTitleCellItemVO("定量考核状态"));
			items1.add(getBillTitleCellItemVO("定量考核得分"));
			items1.add(getBillTitleCellItemVO("综合得分"));
			if (issort) {
				items1.add(getBillTitleCellItemVO("排名"));
				// items1.add(getBillTitleCellItemVO("平均得分比"));
				// items1.add(getBillTitleCellItemVO("最高得分比"));
			}
			if (iswatch) {
				items1.add(getBillTitleCellItemVO("查看详细"));
			}
			list.add(items1.toArray(new BillCellItemVO[0]));
			for (int a = 0; a < allvos.length; a++) {
				if (allvos[a] == null) {
					continue;
				}
				List<BillCellItemVO> itemsa = new ArrayList<BillCellItemVO>();
				itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("checkeddeptname")));
				itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("allcount")));
				// itemsa.add(getBillNormalCellItemVO(list.size(),
				// allvos[a].getStringValue("finishcount")));
				itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("status", "-")));
				itemsa.get(itemsa.size() - 1).setForeground(getColorByState(allvos[a].getStringValue("status", "-")));
				itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("finalres", "-")));
				itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("status2", "-")));
				itemsa.get(itemsa.size() - 1).setForeground(getColorByState(allvos[a].getStringValue("status2", "-")));
				itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("finalres2", "-")));
				if (dept_finares != null && dept_finares.containsKey(allvos[a].getStringValue("checkeddeptid"))) {
					itemsa.add(getBillNormalCellItemVO(list.size(), dept_finares.get(allvos[a].getStringValue("checkeddeptid")) + "%"));
				} else {
					itemsa.add(getBillNormalCellItemVO(list.size(), "未计算"));
				}
				if (issort) {
					itemsa.add(getBillNormalCellItemVO(list.size(), score_seq.get(dept_finares.get(allvos[a].getStringValue("checkeddeptid"))) + "")); // allvos[a].getStringValue("sortseq")
					// itemsa.add(getBillNormalCellItemVO(list.size(),
					// allvos[a].getStringValue("avgres")));
					// itemsa.add(getBillNormalCellItemVO(list.size(),
					// allvos[a].getStringValue("maxres")));
				}
				if (iswatch) {
					BillCellItemVO vo_ = getBillNormalCellItemVO(list.size(), "查看详细");
					vo_.setForeground("0,0,255");
					vo_.setFontstyle("0");
					vo_.setIshtmlhref("Y");
					vo_.setCustProperty("logid", id);
					vo_.setCustProperty("deptid", allvos[a].getStringValue("checkeddeptid"));
					itemsa.add(vo_);
				}
				list.add(itemsa.toArray(new BillCellItemVO[0]));
			}
			vo.setRowlength(list.size());
			vo.setCollength(list.get(0).length);
			BillCellItemVO[][] items = list.toArray(new BillCellItemVO[0][0]);
			formatClen(items);
			vo.setCellItemVOs(items);
		} else {
			vo.setRowlength(1);
			vo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			vo.setCellItemVOs(items);
		}
		return vo;
	}

	/**
	 * 考核结束了在查询员工得分统计时 从结果表中直接取,不再进行计算
	 * 
	 * @param month
	 * @param checkedusers
	 * @param issave
	 * @param issave2
	 * @param isreturn
	 * @param isshowdx
	 * @param iswatch
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getScoreVO_PersonByResult(String logid, String[] checkedusers, boolean isshowdx, boolean iswatch) throws Exception {
		BillCellVO vo = new BillCellVO();
		String logname = getDmo().getStringValueByDS(null, "select name from sal_target_check_log where id=" + logid);
		StringBuffer sb = new StringBuffer("select s.*, case when s.stationkind='高管' then '1' when s.stationkind='中层' then '2' else '3' end as sseq from sal_person_check_result s left join pub_corp_dept d on s.checkeduserdeptid=d.id left join v_sal_personinfo u on s.checkeduserid=u.id where s.logid=" + logid);
		if (checkedusers != null) {
			sb.append(" and s.checkeduserid in (" + getTb().getInCondition(checkedusers) + " ) ");
		}
		sb.append(" and s.targettype='员工定性指标' order by  d.linkcode,u.postseq, u.code ");

		HashVO[] allvos_person = getDmo().getHashVoArrayByDS(null, sb.toString());

		StringBuffer sbggcc = new StringBuffer("select s.*, case when s.stationkind='高管' then '1' when s.stationkind='中层' then '2' else '3' end as sseq from sal_person_check_result s left join pub_corp_dept d on s.checkeduserdeptid=d.id left join v_sal_personinfo u on s.checkeduserid=u.id where s.logid=" + logid);
		if (checkedusers != null) {
			sbggcc.append(" and s.checkeduserid in (" + getTb().getInCondition(checkedusers) + " ) ");
		}
		sbggcc.append(" and s.targettype='高管垂直指标' order by d.linkcode,u.postseq, u.code ");
		HashVO[] allggccvos = getDmo().getHashVoArrayByDS(null, sbggcc.toString());
		if (allvos_person.length == 0 && allggccvos.length == 0) {
			vo.setRowlength(1);
			vo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			vo.setCellItemVOs(items);
		}
		List cellitemvoList = new ArrayList();
		List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
		int person_start_row = 0; //
		for (int index = 0; index < 2; index++) {
			HashVO[] allvos = null;
			if (index == 0) {
				allvos = allggccvos;
			} else {
				allvos = allvos_person;
				person_start_row = list.size(); // 第二个报表起始行。
			}
			if (allvos != null && allvos.length > 0) {
				List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
				if (person_start_row == 0) {
					items0.add(getBillTitleCellItemVO(logname));
				} else {
					items0.add(getBillTitleCellItemVO("员工评议结果"));
				}
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				// items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				if (isshowdx) { // 加这个判断是因为员工的定量直接出钱了不在这里算了
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
				}
				if (iswatch) {
					items0.add(getBillNormalCellItemVO(0, ""));
				}
				items0.get(0).setSpan("1," + items0.size());
				list.add(items0.toArray(new BillCellItemVO[0]));
				// 表头
				List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
				items1.add(getBillTitleCellItemVO("岗位归类"));
				items1.add(getBillTitleCellItemVO("被考核人部门"));
				items1.add(getBillTitleCellItemVO("被考核人"));
				items1.add(getBillTitleCellItemVO("定性考核状态"));
				if (index == 0) {
					items1.add(getBillTitleCellItemVO("高管垂直、条线"));
				} else {
					items1.add(getBillTitleCellItemVO("定性考核得分")); // 得分比
				}
				if (isshowdx) {
					items1.add(getBillTitleCellItemVO("定量考核状态"));
					items1.add(getBillTitleCellItemVO("定量考核得分"));
				}
				if (iswatch) {
					items1.add(getBillTitleCellItemVO("查看详细"));
				}
				list.add(items1.toArray(new BillCellItemVO[0]));
				for (int a = 0; a < allvos.length; a++) {
					List<BillCellItemVO> itemsa = new ArrayList<BillCellItemVO>();
					itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("stationkind")));
					itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("checkeduserdeptname")));
					itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("checkedusername")));
					itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("state")));
					itemsa.get(itemsa.size() - 1).setForeground(getColorByState(allvos[a].getStringValue("state")));
					// itemsa.add(getBillNormalCellItemVO(list.size(),
					// allvos[a].getStringValue("finalres")));
					itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("finalres2") + "%"));
					if (isshowdx) {
						itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("state2", "")));
						itemsa.add(getBillNormalCellItemVO(list.size(), allvos[a].getStringValue("finalres3", "") + "%"));
					}
					if (iswatch) {
						BillCellItemVO vo_ = getBillNormalCellItemVO(list.size(), "查看详细");
						vo_.setForeground("0,0,255");
						vo_.setFontstyle("0");
						vo_.setIshtmlhref("Y");
						vo_.setCustProperty("ishaveperson", allvos[a].getBooleanValue("ishaveperson"));
						vo_.setCustProperty("ishavedept", allvos[a].getBooleanValue("ishavedept"));
						vo_.setCustProperty("ishavegg", allvos[a].getBooleanValue("ishavegg"));
						vo_.setCustProperty("logid", logid);
						vo_.setCustProperty("checkeduserid", allvos[a].getStringValue("checkeduserid"));
						itemsa.add(vo_);
					}
					list.add(itemsa.toArray(new BillCellItemVO[0]));
				}
			}
		}
		BillCellItemVO[][] items = list.toArray(new BillCellItemVO[0][0]);
		if (items.length == 0) {
			vo.setRowlength(1);
			vo.setCollength(1);
			items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			vo.setCellItemVOs(items);
			return vo;
		}
		formatClen(items);
		formatSpan(items, new int[] { 0, 1 });
		if (person_start_row > 0) {
			items[person_start_row][0].setSpan("1," + items[person_start_row].length);// 重置空行合并
			items[person_start_row][0].setBackground("184,255,185");// 重置颜色
			items[person_start_row + 1][0].setBackground("184,255,185");// 重置颜色
			items[person_start_row + 1][1].setBackground("184,255,185");// 重置颜色
		}
		//

		vo.setCellItemVOs(items);
		vo.setRowlength(items.length);
		vo.setCollength(items[0].length);
		return vo;
	}

	/**
	 * 重要方法部门的得分就靠他了
	 * 
	 * @param id
	 *            logid
	 * @param issave
	 *            是否存储到结果表里（定性）
	 * @param issave2
	 *            是否存储到结果表里（定量）
	 * @param isreturn
	 *            是否返回vo
	 * @param checkedDeptid
	 *            部门id
	 * @param issort
	 *            是否排名 返回的vo会增加最大、平均、排名列 （后来需要隐藏最大、平均且排名放最后） （定性的排名）
	 * @param iswatch
	 *            是否查看明细 返回的vo是否有查看明细列
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getScoreVOByID(String id, boolean issave, boolean issave2, boolean isreturn, String checkedDeptid, boolean issort, boolean iswatch) throws Exception {
		BillCellVO vo = new BillCellVO();
		List<String> sqls = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("select id,name,status from sal_target_check_log where 1=1 ");
		// if (id != null && !"".equals(id)) {
		sql.append(" and id=" + id);
		// }
		sql.append(" order by checkdate desc ");
		BigDecimal[] allrights = getDeptRight();
		BigDecimal c = allrights[0];
		BigDecimal p = allrights[1];
		BigDecimal cr = allrights[2];
		BigDecimal cp = allrights[3];
		BigDecimal pr = allrights[4];
		BigDecimal pp = allrights[5];
		List<String> allscore = new ArrayList<String>();
		String[][] logidandname = getDmo().getStringArrayByDS(null, sql.toString());
		if (logidandname != null && logidandname.length > 0) {
			if ("考核结束".equals(logidandname[0][2]) && !issave && !issave2) { // 考核结束了就要从结果表里取了,并且没有需要保存的。
				return getScoreVOByResult(logidandname[0][0], checkedDeptid, issort, iswatch);
			}
			HashMap dept_finares = getDmo().getHashMapBySQLByDS(null, "select checkeddeptid, currscore+revisescore from sal_target_check_revise_result where logid=" + id);
			String logid = null;
			List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
			for (int o = 0; o < logidandname.length; o++) { // 支持多个日志，实际就是一个
				logid = logidandname[o][0];
				StringBuffer sb = new StringBuffer("select * from sal_dept_check_score where logid='" + logid + "' ");
				DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_target_check_result");
				StringBuffer sb_del = new StringBuffer("logid=" + logid + " and targettype='部门定性指标' ");
				StringBuffer sb_del2 = new StringBuffer("logid=" + logid + " and targettype='部门定量指标' ");
				if (!issort) { // 如果排名的话 需要都算出来再排序
					if (checkedDeptid != null) {
						sb.append(" and checkeddept='" + checkedDeptid + "'");
						sb_del.append(" and checkeddept='" + checkedDeptid + "'");
						sb_del2.append(" and checkeddept='" + checkedDeptid + "'");
					}
				}
				if (issave) { // 如果保存就先删除
					dsb.setWhereCondition(sb_del.toString());
					sqls.add(dsb.getSQL());
				}
				if (issave2) { // 如果保存就先删除
					dsb.setWhereCondition(sb_del2.toString());
					sqls.add(dsb.getSQL());
				}
				HashVO[] allscores = getDmo().getHashVoArrayByDS(null, "select s.* from (" + sb.toString() + ") s left join pub_corp_dept d on s.checkeddept=d.id order by d.linkcode");
				if (allscores != null && allscores.length > 0) {
					// 这是一个重要方法，此方法里缓存了几个map要在下面的逻辑里用到
					LinkedHashMap[] maps = getAllTempMap(allscores);
					LinkedHashMap<String, Integer> dept_count = maps[0];
					LinkedHashMap<String, String> dept_state = maps[1];
					LinkedHashMap<String, String> dept_state1 = maps[6]; // 部门——部门的状态（定性）
					LinkedHashMap<String, String> dept_state2 = maps[7]; // 部门——部门的状态（定量）
					LinkedHashMap<String, Integer> dept_finshc = maps[2];
					LinkedHashMap dept_score = maps[3]; // 重要 部门-（指标-（类型-详情））
					LinkedHashMap target_weight = maps[4];
					LinkedHashMap dept_target_state = maps[5];
					String[] alldeptids = (String[]) dept_count.keySet().toArray(new String[0]);
					HashMap<String, String> deptid_name = getDmo().getHashMapBySQLByDS(null, "select id,name from pub_corp_dept where id in (" + getTb().getInCondition(alldeptids) + ")");
					if (isreturn) {
						List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
						items0.add(getBillTitleCellItemVO(logidandname[o][1]));
						items0.add(getBillNormalCellItemVO(0, ""));
						items0.add(getBillNormalCellItemVO(0, ""));
						// items0.add(getBillNormalCellItemVO(0, ""));
						items0.add(getBillNormalCellItemVO(0, ""));
						items0.add(getBillNormalCellItemVO(0, ""));
						items0.add(getBillNormalCellItemVO(0, ""));
						items0.add(getBillNormalCellItemVO(0, ""));
						if (iswatch) {
							items0.add(getBillNormalCellItemVO(0, ""));
						}
						if (issort) {
							// items0.add(getBillNormalCellItemVO(0, ""));
							// items0.add(getBillNormalCellItemVO(0, ""));
							items0.add(getBillNormalCellItemVO(0, ""));
						}
						items0.get(0).setSpan("1," + items0.size());
						list.add(items0.toArray(new BillCellItemVO[0]));
						List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
						// 列头
						items1.add(getBillTitleCellItemVO("被考核部门"));
						items1.add(getBillTitleCellItemVO("被考核指标数量"));
						// items1.add(getBillTitleCellItemVO("已完成数量"));
						items1.add(getBillTitleCellItemVO("定性考核状态"));
						items1.add(getBillTitleCellItemVO("定性考核得分"));
						items1.add(getBillTitleCellItemVO("定量考核状态"));
						items1.add(getBillTitleCellItemVO("定量考核得分"));
						items1.add(getBillTitleCellItemVO("综合得分"));
						if (issort) {
							items1.add(getBillTitleCellItemVO("排名"));
							// items1.add(getBillTitleCellItemVO("平均得分比"));
							// items1.add(getBillTitleCellItemVO("最高得分比"));
						}
						if (iswatch) {
							items1.add(getBillTitleCellItemVO("查看详细"));
						}
						list.add(items1.toArray(new BillCellItemVO[0]));
					}
					for (int i = 0; i < alldeptids.length; i++) {
						List<BillCellItemVO> itemsi = new ArrayList<BillCellItemVO>();
						itemsi.add(getBillNormalCellItemVO(i, deptid_name.get(alldeptids[i]) + "")); // 部门名称
						itemsi.add(getBillNormalCellItemVO(i, "")); // 指标数量
						// itemsi.add(2, getBillNormalCellItemVO(i, "")); //
						// 完成数量
						if (dept_state1.get(alldeptids[i]) != null) {
							itemsi.add(getBillNormalCellItemVO(i, dept_state1.get(alldeptids[i]))); // 定性状态
							itemsi.get(itemsi.size() - 1).setForeground(getColorByState(itemsi.get(itemsi.size() - 1).getCellvalue())); // 状态列设置颜色
							itemsi.add(getBillNormalCellItemVO(i, "")); // 得分
						} else {
							itemsi.add(getBillNormalCellItemVO(i, "-")); // 表明没有考核定性指标
							itemsi.get(itemsi.size() - 1).setForeground(getColorByState(itemsi.get(itemsi.size() - 1).getCellvalue())); // 状态列设置颜色
							itemsi.add(getBillNormalCellItemVO(i, "-"));
						}
						if (dept_target_state.containsKey(alldeptids[i])) {
							itemsi.get(1).setCellvalue(((HashMap) dept_target_state.get(alldeptids[i])).size() + ""); // 指标数量
							// HashMap targetstate = (HashMap)
							// dept_target_state.get(alldeptids[i]);
							// String[] states = (String[])
							// targetstate.values().toArray(new String[0]);
							// int unc = 0;
							// for (int gg = 0; gg < states.length; gg++) {
							// if (!"".equals(states[gg]) &&
							// ("评分完成".equals(states[gg]) ||
							// "申请修改".equals(states[gg]))) {
							// unc++;
							// }
							// }
							// itemsi.get(2).setCellvalue(unc + ""); // 已完成数量
						} else {
							itemsi.get(1).setCellvalue("0");
							// itemsi.get(2).setCellvalue("0");
						}
						int param = 4;
						if ("评分完成".equals(dept_state1.get(alldeptids[i]))) { // 评分完成的才进行计算
							if (dept_score.containsKey(alldeptids[i])) {
								HashMap target_score = (HashMap) dept_score.get(alldeptids[i]); // 指标-（类型-评分记录）
								String[] targetids = (String[]) target_score.keySet().toArray(new String[0]);
								BigDecimal sumscore = new BigDecimal(0);
								BigDecimal sumweight = new BigDecimal(0);
								HashMap target_res = new HashMap();
								for (int k = 0; k < targetids.length; k++) {
									HashMap type_score = (HashMap) target_score.get(targetids[k]); // 类型-评分记录
									// 有哪几类
									// 垂直R、垂直P、平行R、平行P
									if (type_score.containsKey("") || type_score.containsKey(null)) { // 为空的就是定量指标了
										continue;
									}
									BigDecimal right = new BigDecimal(0);
									BigDecimal CR = new BigDecimal(0);
									BigDecimal CP = new BigDecimal(0);
									BigDecimal PR = new BigDecimal(0);
									BigDecimal PP = new BigDecimal(0);
									if (type_score.containsKey("垂直R")) {
										List score = (List) type_score.get("垂直R"); // 这个部门的这个指标
										// 垂直R的所有打分明细
										if (score.size() > 0) {
											right = right.add(c.multiply(cr)); // 权重=垂直R的权重*垂直的权重
											CR = getScore(score, c.multiply(cr));// 根据模拟计算
											// 某一类打分比如垂直R的所有得分算平均
											// *
											// （垂直R的权重*垂直的权重）
										}
									}
									if (type_score.containsKey("垂直P")) {
										List score = (List) type_score.get("垂直P");
										if (score.size() > 0) {
											right = right.add(c.multiply(cp));
											CP = getScore(score, c.multiply(cp));
										}
									}
									if (type_score.containsKey("平行R")) {
										List score = (List) type_score.get("平行R");
										if (score.size() > 0) {
											right = right.add(p.multiply(pr));
											PR = getScore(score, p.multiply(pr));
										}
									}
									if (type_score.containsKey("平行P")) {
										List score = (List) type_score.get("平行P");
										if (score.size() > 0) {
											right = right.add(p.multiply(pp));
											PP = getScore(score, p.multiply(pp));
										}
									}
									BigDecimal finalresc = CR.add(CP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
									BigDecimal finalresp = PR.add(PP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
									BigDecimal finalres = finalresc.add(finalresp);
									sumscore = sumscore.add(finalres);
									sumweight = sumweight.add(new BigDecimal(target_weight.get(targetids[k]) + ""));
								}
								if (sumweight.compareTo(BigDecimal.ZERO) == 0) {
									itemsi.get(itemsi.size() - 1).setCellvalue("0%");
								} else {
									itemsi.get(itemsi.size() - 1).setCellvalue((sumscore.divide(sumweight, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "%");
								}
								if (issave) { // 如果保存定性的结果
									InsertSQLBuilder isb = new InsertSQLBuilder("sal_target_check_result");
									String resid = getDmo().getSequenceNextValByDS(null, "S_SALTARGET_CHECK_RESULT");
									isb.putFieldValue("id", resid);
									itemsi.get(0).setCustProperty("resid", resid);
									isb.putFieldValue("checkeddeptname", deptid_name.get(alldeptids[i]) + "");
									isb.putFieldValue("checkeddeptid", alldeptids[i]);
									isb.putFieldValue("status", dept_state1.get(alldeptids[i]));
									isb.putFieldValue("targettype", "部门定性指标");
									isb.putFieldValue("allcount", itemsi.get(1).getCellvalue());
									// isb.putFieldValue("finishcount",
									// itemsi.get(2).getCellvalue());
									isb.putFieldValue("logid", logid);
									if (sumweight.compareTo(BigDecimal.ZERO) == 0) {
										isb.putFieldValue("finalres", "0");
									} else {
										isb.putFieldValue("finalres", (sumscore.divide(sumweight, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
									}
									sqls.add(isb.getSQL());
								}
							}
							if (issort) {
								// itemsi.add(5, getBillNormalCellItemVO(i,
								// ""));
								// itemsi.add(6, getBillNormalCellItemVO(i,
								// ""));
								// itemsi.add(7, getBillNormalCellItemVO(i,
								// ""));
								allscore.add(itemsi.get(itemsi.size() - 1).getCellvalue().replaceAll("%", "")); // 存起来后面要计算排名
								// param = 8;
							}
						} else {
							// if (issort) {
							// itemsi.add(5, getBillNormalCellItemVO(i, ""));
							// itemsi.add(6, getBillNormalCellItemVO(i, ""));
							// itemsi.add(7, getBillNormalCellItemVO(i, ""));
							// param = 8;
							// }
						}
						itemsi.add(param, getBillNormalCellItemVO(i, "-"));
						itemsi.add(param + 1, getBillNormalCellItemVO(i, "-"));
						if (dept_state2.containsKey(alldeptids[i])) {
							itemsi.set(param, getBillNormalCellItemVO(i, dept_state2.get(alldeptids[i]) + ""));
							itemsi.get(param).setForeground(getColorByState(dept_state2.get(alldeptids[i])));
							itemsi.set(param + 1, getBillNormalCellItemVO(i, ""));
							if ("评分完成".equals(dept_state2.get(alldeptids[i]))) { // 评分完成的才进行计算
								itemsi.set(param, getBillNormalCellItemVO(i, dept_state2.get(alldeptids[i])));
								itemsi.get(param).setForeground(getColorByState(dept_state2.get(alldeptids[i])));
								if (dept_score.containsKey(alldeptids[i])) {
									HashMap target_score = (HashMap) dept_score.get(alldeptids[i]);
									String[] targetids = (String[]) target_score.keySet().toArray(new String[0]);
									BigDecimal sumscore = new BigDecimal(0);
									BigDecimal sumweight = new BigDecimal(0);
									for (int k = 0; k < targetids.length; k++) {
										HashMap type_score = (HashMap) target_score.get(targetids[k]);
										if (type_score.containsKey("") || type_score.containsKey(null)) { // 为空的就是定量指标了
											BigDecimal right = new BigDecimal(0);
											BigDecimal CR = new BigDecimal(0);
											List score = (List) type_score.get(null);
											if (score.size() > 0) {
												CR = getScore(score, new BigDecimal(1));
											}
											sumscore = sumscore.add(CR);
											sumweight = sumweight.add(new BigDecimal(target_weight.get(targetids[k]) + ""));
										}
									}
									if (sumweight.compareTo(BigDecimal.ZERO) == 0) {
										itemsi.get(param + 1).setCellvalue("0%");
									} else {
										itemsi.get(param + 1).setCellvalue((sumscore.divide(sumweight, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "%");
									}
									if (issave2) {
										InsertSQLBuilder isb = new InsertSQLBuilder("sal_target_check_result");
										isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SALTARGET_CHECK_RESULT"));
										isb.putFieldValue("checkeddeptname", deptid_name.get(alldeptids[i]) + "");
										isb.putFieldValue("checkeddeptid", alldeptids[i]);
										isb.putFieldValue("status2", dept_state2.get(alldeptids[i]));
										isb.putFieldValue("targettype", "部门定量指标");
										isb.putFieldValue("allcount", itemsi.get(1).getCellvalue());
										// isb.putFieldValue("finishcount",
										// itemsi.get(2).getCellvalue());
										isb.putFieldValue("logid", logid);
										if (sumweight.compareTo(BigDecimal.ZERO) == 0) {
											isb.putFieldValue("finalres", "0");
										} else {
											isb.putFieldValue("finalres", (sumscore.divide(sumweight, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"))).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
										}
										sqls.add(isb.getSQL());
									}
								}
							}
						}
						itemsi.add(param + 2, getBillNormalCellItemVO(i, "未计算"));
						if ("评分完成".equals(dept_state1.get(alldeptids[i]))) {
							if (issort) {
								itemsi.add(getBillNormalCellItemVO(i, "-"));
							}
						} else {
							if (issort) {
								itemsi.add(getBillNormalCellItemVO(i, "-"));
							}
						}
						if (iswatch) {
							itemsi.add(getBillNormalCellItemVO(i, "查看详细"));
							itemsi.get(itemsi.size() - 1).setForeground("0,0,255");
							itemsi.get(itemsi.size() - 1).setFontstyle("0");
							itemsi.get(itemsi.size() - 1).setIshtmlhref("Y");
							itemsi.get(itemsi.size() - 1).setCustProperty("logid", logid);
							itemsi.get(itemsi.size() - 1).setCustProperty("deptid", alldeptids[i]);
						}
						if (isreturn) {
							if (checkedDeptid != null) {
								if (checkedDeptid.equals(alldeptids[i])) {
									list.add(itemsi.toArray(new BillCellItemVO[0]));
								}
							} else {
								list.add(itemsi.toArray(new BillCellItemVO[0]));
							}
						}
					}
				} else {
					List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
					items0.add(getBillTitleCellItemVO(logidandname[o][1]));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					// items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					if (issort) {
						// items0.add(getBillNormalCellItemVO(0, ""));
						// items0.add(getBillNormalCellItemVO(0, ""));
						items0.add(getBillNormalCellItemVO(0, ""));
					}
					if (iswatch) {
						items0.add(getBillNormalCellItemVO(0, ""));
					}
					items0.get(0).setSpan("1," + items0.size());
					list.add(items0.toArray(new BillCellItemVO[0]));
					List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
					items1.add(getBillTitleCellItemVO("未查询到相应考评信息"));
					items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					// items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					if (issort) {
						// items0.add(getBillNormalCellItemVO(0, ""));
						// items0.add(getBillNormalCellItemVO(0, ""));
						items0.add(getBillNormalCellItemVO(0, ""));
					}
					if (iswatch) {
						items0.add(getBillNormalCellItemVO(0, ""));
					}
					items1.get(0).setColwidth("300");
					items1.get(0).setSpan("1," + items1.size());
					list.add(items1.toArray(new BillCellItemVO[0]));
				}
				if (o < logidandname.length - 1) {
					List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
					items1.add(getBillTitleCellItemVO(""));
					items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					// items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					items1.add(getBillNormalCellItemVO(0, ""));
					if (issort) {
						// items1.add(getBillNormalCellItemVO(0, ""));
						// items1.add(getBillNormalCellItemVO(0, ""));
						items1.add(getBillNormalCellItemVO(0, ""));
					}
					if (iswatch) {
						items1.add(getBillNormalCellItemVO(0, ""));
					}
					items1.get(0).setColwidth("300");
					items1.get(0).setSpan("1," + items1.size());
					list.add(items1.toArray(new BillCellItemVO[0]));
				}
			}
			if (isreturn) {
				vo.setCollength(list.get(0).length);
				vo.setRowlength(list.size());
				BillCellItemVO[][] items = (BillCellItemVO[][]) list.toArray(new BillCellItemVO[0][0]);
				int li_allowMaxColWidth = 375;
				FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
				for (int j = 0; j < items[0].length; j++) {
					int li_maxwidth = 0;
					String str_cellValue = null;
					for (int i = 0; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
							int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
							if (li_width > li_maxwidth) {
								li_maxwidth = li_width;
							}
						}
					}
					li_maxwidth = li_maxwidth + 13;
					if (li_maxwidth > li_allowMaxColWidth) {
						li_maxwidth = li_allowMaxColWidth;
					}
					for (int i = 1; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						items[i][j].setColwidth("" + li_maxwidth);
					}
				}
				vo.setCellItemVOs(items);
				if (issort) {
					HashMap score_seq = getSort(allscore);
					String realvalue = null;
					for (int i = 0; i < items.length; i++) {
						realvalue = items[i][4].getCellvalue().replaceAll("%", "");
						if (score_seq.containsKey(realvalue)) {
							// 后来改成综合得分的排名此处就不显示了，动态算的时候综合分为未计算，排名为-（考核结束时才显示从结果表里查，但是我这边没有综合分所以排名和综合分这里存不了，只能显示的时候动态算了）
							// 以前的定性排名还是存一下
							// 有没有用以后看吧
							// items[i][items[0].length -
							// 1].setCellvalue(score_seq.get(realvalue) + "");
							// items[i][7].setCellvalue(score_seq.get("最大值") +
							// "%");
							// items[i][6].setCellvalue(score_seq.get("平均值") +
							// "%");
							if (items[i][0].getCustProperty("resid") != null) { // 将排名信息更新到结果表里
								UpdateSQLBuilder usb = new UpdateSQLBuilder("sal_target_check_result");
								usb.setWhereCondition("id=" + items[i][0].getCustProperty("resid"));
								usb.putFieldValue("sortseq", score_seq.get(realvalue) + "");
								usb.putFieldValue("maxres", score_seq.get("最大值") + "%");
								usb.putFieldValue("avgres", score_seq.get("平均值") + "%");
								sqls.add(usb.getSQL());
							}

						}
					}
				}
			}
		} else {
			vo.setRowlength(1);
			vo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			vo.setCellItemVOs(items);
		}
		if (issave || issave2) {
			getDmo().executeBatchByDS(null, sqls);
		}
		return vo;
	}

	private HashMap getSort(List<String> allscore) {
		HashMap score_seq = new HashMap();
		BigDecimal sum = new BigDecimal("0");
		for (int i = 0; i < allscore.size(); i++) {
			sum = sum.add(new BigDecimal(allscore.get(i)));
			for (int j = 0; j < allscore.size(); j++) {
				if (Float.parseFloat(allscore.get(i)) > Float.parseFloat(allscore.get(j))) {
					String temp = allscore.get(j);
					allscore.set(j, allscore.get(i));
					allscore.set(i, temp);
				}
			}
		}
		for (int i = 0; i < allscore.size(); i++) {
			if (!score_seq.containsKey(allscore.get(i))) {
				score_seq.put(allscore.get(i), i + 1);
			}
		}
		if (allscore.size() > 0) {
			score_seq.put("最大值", allscore.get(0));
			score_seq.put("平均值", sum.divide(new BigDecimal(allscore.size()), 2, BigDecimal.ROUND_HALF_UP));
		} else {
			score_seq.put("最大值", "");
			score_seq.put("平均值", "");
		}
		return score_seq;
	}

	/**
	 * 重要方法员工考核得分统计
	 * 
	 * @param month
	 *            月度
	 * @param month
	 *            可以指定计算哪些人的分数为null则计算所有人
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getScoreVO_Person(String month, String[] checkedusers) throws Exception {
		String id = getDmo().getStringValueByDS(null, "select id,name from sal_target_check_log where checkdate='" + month + "'");
		return getScoreVO_Person(id, checkedusers, false, false, true, false, true);
	}

	/**
	 * 
	 * @param month
	 * @param checkedusers
	 * @param issave
	 * @param isreturn
	 *            减少一些逻辑计算不返回VO 一些排序什么的就不需要了 减少计算时间
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getScoreVO_Person(String id, String[] checkedusers, boolean issave, boolean issave2, boolean isreturn, boolean isshowdx, boolean iswatch) throws Exception {
		BillCellVO vo = new BillCellVO();
		List<String> sqls = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("select id, name, status from sal_target_check_log where 1=1 ");
		// if (id != null && !"".equals(id)) {
		sql.append(" and (id=" + id + " )");
		// }
		sql.append(" order by checkdate desc ");
		String[][] logidandname = getDmo().getStringArrayByDS(null, sql.toString());
		if (logidandname != null && logidandname.length > 0) {
			if ("考核结束".equals(logidandname[0][2]) && !issave && !issave2) {
				return getScoreVO_PersonByResult(logidandname[0][0], checkedusers, isshowdx, iswatch);
			}
			HashMap selfDeptCheckMap = getDmo().getHashMapBySQLByDS(null, "select id, name from sal_person_check_plan_scorer where type='本部门员工'");
			String logid = null;
			List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
			HashMap objid_name = getDmo().getHashMapBySQLByDS(null, "select id, name from sal_person_check_type");
			for (int o = 0; o < logidandname.length; o++) {
				logid = logidandname[o][0];
				// sql改进。这种大量数据的表，千万不要用子查询，真的等的太累了。hm
				StringBuffer sb2 = new StringBuffer("select s.*,g.planid,p.checkobj,p.seq from  sal_person_check_score s left join sal_person_check_plan_scorer g on s.scoreusertype=g.id  left join sal_person_check_plan p on g.planid=p.id left join v_sal_personinfo vp on vp.id =s.checkeduser  where s.logid='" + logid + "'");
				DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_person_check_result");
				StringBuffer sb_del = new StringBuffer("logid=" + logid + " and targettype!='员工定量指标' ");
				StringBuffer sb_del2 = new StringBuffer("logid=" + logid + " and targettype='员工定量指标' ");
				if (checkedusers != null && checkedusers.length > 0) {
					sb2.append(" and s.checkeduser in (" + getTb().getInCondition(checkedusers) + ")");
					sb_del.append(" and checkeduserid in (" + getTb().getInCondition(checkedusers) + ")");
					sb_del2.append(" and checkeduserid in (" + getTb().getInCondition(checkedusers) + ")");
				}
				sb2.append(" order by s.checktype,vp.linkcode,vp.postseq");
				
				if (issave) {
					dsb.setWhereCondition(sb_del.toString());
					sqls.add(dsb.getSQL());
				}
				if (issave2) {
					dsb.setWhereCondition(sb_del2.toString());
					sqls.add(dsb.getSQL());
				}
				HashVO[] allscores = getDmo().getHashVoArrayByDS(null, sb2.toString()); // 所有打分记录
				ReportUtil rutil = new ReportUtil();
				rutil.allOneFieldNameFromOtherTable(allscores, "usercode", "checkeduser", "select id,code from pub_user");
//				getTb().sortHashVOs(allscores, new String[][] { { "usercode", "N", "N" } });
				HashMap[] alltempmaps = getAllTempMap_Person(allscores);
				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>> map = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>>) alltempmaps[0];
				LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>> map_ggcc = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>>) alltempmaps[4];
				LinkedHashMap<String, List<HashVO>> userid_quantifyscore = (LinkedHashMap<String, List<HashVO>>) alltempmaps[3];
				HashMap<String, String> user_level = (HashMap<String, String>) alltempmaps[1]; // 评分人-岗位系数
				// 这里要判断是否所有评分人都设置了岗位系数
				HashMap<String, String> error = (HashMap<String, String>) alltempmaps[2];
				if (error != null && error.size() > 0) {
					String[] names = error.keySet().toArray(new String[0]);
					StringBuffer sb_ = new StringBuffer();
					for (int n = 0; n < names.length; n++) {
						if (names[n] != null && !"".equals(names[n])) {
							if (sb_.length() <= 0) {
								sb_.append(names[n]);
							} else {
								sb_.append("、" + names[n]);
							}
						}
					}
					throw new WLTAppException("评分人:" + sb_.toString() + "未设置岗位系数,无法计算得分!");
				}

				HashMap sa = getSelfDeptCheckScore(selfDeptCheckMap, user_level, map);
				if (map.size() == 0 && map_ggcc.size() == 0) { // 如果都没有，说明没有考核信息。
					vo.setRowlength(1);
					vo.setCollength(1);
					BillCellItemVO[][] items = new BillCellItemVO[1][1];
					items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
					items[0][0].setColwidth("300");
					vo.setCellItemVOs(items);
					return vo;
				}
				int person_start_row = 0;// 员工评议在表中开始行数。
				for (int mapindex = 0; mapindex < 2; mapindex++) { // 1里面是高管垂直的指标以分管条线指标，2里面是员工评议的指标。
					LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>> currMap = new LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>>();
					if (mapindex == 0) {
						currMap = map_ggcc; // 先计算高管的
					} else {
						currMap = map;
						person_start_row = list.size();
					}
					if (currMap.size() > 0) {
						if (isreturn) {
							// 标题
							List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
							if (person_start_row == 0) {
								items0.add(getBillTitleCellItemVO(logidandname[o][1]));
							} else {
								items0.add(getBillTitleCellItemVO("员工评议结果"));
							}
							items0.add(getBillNormalCellItemVO(0, ""));
							items0.add(getBillNormalCellItemVO(0, ""));
							items0.add(getBillNormalCellItemVO(0, ""));
							items0.add(getBillNormalCellItemVO(0, ""));
							if (isshowdx) { // 加这个判断是因为员工的定量直接出钱了不在这里算了
								items0.add(getBillNormalCellItemVO(0, ""));
								items0.add(getBillNormalCellItemVO(0, ""));
							}
							if (iswatch) {
								items0.add(getBillNormalCellItemVO(0, ""));
							}
							items0.get(0).setSpan("1," + items0.size());
							list.add(items0.toArray(new BillCellItemVO[0]));
							// 表头
							List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
							items1.add(getBillTitleCellItemVO("岗位归类"));
							items1.add(getBillTitleCellItemVO("被考核人部门"));
							items1.add(getBillTitleCellItemVO("被考核人"));
							items1.add(getBillTitleCellItemVO("定性考核状态"));
							// items1.add(getBillTitleCellItemVO("定性考核得分"));
							if (mapindex == 0) {
								items1.add(getBillTitleCellItemVO("高管垂直、条线"));
							} else {
								items1.add(getBillTitleCellItemVO("定性考核得分"));
							}
							if (isshowdx) {
								items1.add(getBillTitleCellItemVO("定量考核状态"));
								items1.add(getBillTitleCellItemVO("定量考核得分"));
							}
							if (iswatch) {
								items1.add(getBillTitleCellItemVO("查看详细"));
							}
							list.add(items1.toArray(new BillCellItemVO[0]));
						}
						String[] allcheckedusers = (String[]) currMap.keySet().toArray(new String[0]); // 所有被定性指标考核的考核人
						String checkedusersname = null;
						String checkeduerobj = null;
						String checkeduerdeptname = null;
						String checkeduerdeptid = null;
						HashMap user_state = new HashMap();
						HashMap user_state2 = new HashMap();
						if (allcheckedusers != null && allcheckedusers.length > 0) {
							// HashMap userid_deptname =
							// getDmo().getHashMapBySQLByDS(null,
							// "select p.userid, u.name from pub_user_post p
							// left join pub_corp_dept u on u.id=p.userdept
							// where p.userid is not null and u.name is not null
							// and p.isdefault='Y' and p.userid in ("
							// + getTb().getInCondition(allcheckedusers) + ")");

							for (int i = 0; i < allcheckedusers.length; i++) { // 遍历所有被考核人

								BigDecimal onecheckeduserscore = new BigDecimal(0);// 郝明从循环外移动到此
								BigDecimal onecheckeduserfullscore = new BigDecimal(0);
								List alldeptdetail = null;
								List allpersondetail = new ArrayList();
								List allggdetail = new ArrayList();
								checkedusersname = null;
								checkeduerobj = null;
								checkeduerdeptname = null;
								checkeduerdeptid = null;
								String userweightstype = null; // 被考核人的权限方式
								// 平均、加权平均
								BigDecimal onegroupscore = new BigDecimal(0); // 每个评分组的最终评分
								BigDecimal onegroupfullscore = new BigDecimal(0); // 每个评分组的满分
								BigDecimal allgroupscore = new BigDecimal(0); // 组的评分和
								BigDecimal allgroupfullscore = new BigDecimal(0); // 组的评分和
								BigDecimal allgroupleve = new BigDecimal(0); // 组的权重和或者个数
								boolean isok = true;
								boolean isok2 = true;
								LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>> group_scorer = currMap.get(allcheckedusers[i]); // 被考核人被哪些组考核
								String[] groupids = (String[]) group_scorer.keySet().toArray(new String[0]);
								if (groupids != null && groupids.length > 0) {
									for (int g = 0; g < groupids.length; g++) { // 遍历考核人所有的评价组
										String scoretype = "";
										String groupweightstype = null; // 评分组的权限方式
										// 平均、按岗位系数加权平均
										String groupweight = null; // 这个评分组的权重
										LinkedHashMap<String, List<HashVO>> scorer_scorer = group_scorer.get(groupids[g]);
										String[] scorers = (String[]) scorer_scorer.keySet().toArray(new String[0]);
										BigDecimal onescorerscore = new BigDecimal(0); // 每个评分人的最终评分
										BigDecimal onescorerfullscore = new BigDecimal(0);
										BigDecimal allscorerscore = new BigDecimal(0); // 组下面所有评分人的最终的评分和
										BigDecimal allscorerfullscore = new BigDecimal(0);
										BigDecimal allscorerleve = new BigDecimal(0); // 组下面所有评分人岗位系数和或者人数
										if (scorers != null && scorers.length > 0) {
											for (int s = 0; s < scorers.length; s++) { // 遍历这个评分组下的评分人
												if (scorers[s] == null) {
													continue;
												}
												List<HashVO> scorervos = scorer_scorer.get(scorers[s]); // 当前评分人的打分记录
												if (scorervos != null && scorervos.size() > 0) {
													scoretype = scorervos.get(0).getStringValue("scoretype", "手动打分");
													BigDecimal allweight = new BigDecimal(0); // 权重和
													BigDecimal allscore = new BigDecimal(0); // 所有得分和
													BigDecimal allfullscore = new BigDecimal(0); // 所有满分和
													BigDecimal realscore = null; // 每一条打分记录的
													// 实际得分
													BigDecimal targetweights = null; // 指标权重
													String scorer = null; // 评分人id
													String state = null;
													for (int v = 0; v < scorervos.size(); v++) { // 遍历每个评分人的打分记录
														if (scorervos.get(v) == null) {
															continue;
														}
														state = scorervos.get(v).getStringValue("status", "abc");
														if (groupweightstype == null || "".equals(groupweightstype)) { // 理论上每一条打分记录这个值都是一样的
															groupweightstype = scorervos.get(v).getStringValue("groupweightstype", "");
														}
														if (groupweight == null || "".equals(groupweight)) { // 理论上每一条打分记录这个值都是一样的
															groupweight = scorervos.get(v).getStringValue("groupweights", "");
														}
														if (userweightstype == null || "".equals(userweightstype)) { // 理论上每一条打分记录这个值都是一样的
															userweightstype = scorervos.get(v).getStringValue("scorerweightstype", "");
														}
														if (checkedusersname == null || "".equals(checkedusersname)) { // 理论上每一条打分记录这个值都是一样的
															checkedusersname = scorervos.get(v).getStringValue("checkedusername", "");
														}

														if (checkeduerobj == null || "".equals(checkeduerobj)) { // 理论上每一条打分记录这个值都是一样的
															checkeduerobj = objid_name.get(scorervos.get(v).getStringValue("checkobj", "")) + "";
														}

														if (checkeduerdeptname == null || "".equals(checkeduerdeptname)) { // 理论上每一条打分记录这个值都是一样的
															checkeduerdeptname = scorervos.get(v).getStringValue("checkeduserdeptname", "无");
														}

														if (checkeduerdeptid == null || "".equals(checkeduerdeptid)) { // 理论上每一条打分记录这个值都是一样的
															checkeduerdeptid = scorervos.get(v).getStringValue("checkeduserdeptid", "");
														}

														if (!scoretype.equals("手动打分")) {
															break;
														}
														if ("高管定性指标".equals(scorervos.get(v).getStringValue("targettype", "abc")) && allggdetail != null) {
															allggdetail.add(scorervos.get(v).getStringValue("id", ""));
														}
														if ("员工定性指标".equals(scorervos.get(v).getStringValue("targettype", "abc")) && allpersondetail != null) {
															allpersondetail.add(scorervos.get(v).getStringValue("id", ""));
														}
														if (!"已提交".equals(state)) { // 有一条未提交那么这个被考核人的分数就算不了
															isok = false;
														}
														getState(user_state, allcheckedusers[i], state);
														if (isok) {
															realscore = new BigDecimal(scorervos.get(v).getStringValue("checkscore"));
															targetweights = new BigDecimal(scorervos.get(v).getStringValue("weights"));
															allweight = allweight.add(targetweights); // 指标权重的和
															allscore = allscore.add(realscore.multiply(targetweights)); // 得分乘以指标权重的和
															allfullscore = allfullscore.add(new BigDecimal("10").multiply(targetweights));
														}
													}
													if (!scoretype.equals("手动打分")) {
														break;
													}
													if (isok) {
														if (allweight.compareTo(BigDecimal.ZERO) == 0) {
															onescorerscore = new BigDecimal("0"); // 最终这个评分人对被考核人的加权分
															onescorerfullscore = new BigDecimal("0"); // 满分
														} else {
															onescorerscore = allscore.divide(allweight, 6, BigDecimal.ROUND_HALF_UP); // 最终这个评分人对被考核人的加权分
															onescorerfullscore = allfullscore.divide(allweight, 6, BigDecimal.ROUND_HALF_UP);
														}
													}
												} else {
													onescorerscore = new BigDecimal(0);
													onescorerfullscore = new BigDecimal(0);
												}
												if (isok) {
													if ("平均".equals(groupweightstype)) {
														allscorerleve = allscorerleve.add(new BigDecimal(1));
														allscorerscore = allscorerscore.add(onescorerscore);
														allscorerfullscore = allscorerfullscore.add(onescorerfullscore); //bug 2015-02-02
													} else {
														if (user_level.containsKey(scorers[s])) {
															allscorerscore = allscorerscore.add(onescorerscore.multiply(new BigDecimal(user_level.get(scorers[s])))); // 这个组下所有评分人最终分的加权
															allscorerfullscore = allscorerfullscore.add(onescorerfullscore.multiply(new BigDecimal(user_level.get(scorers[s]))));
															allscorerleve = allscorerleve.add(new BigDecimal(user_level.get(scorers[s])));
														}
													}
												}
											}
											if (!scoretype.equals("手动打分")) {
												// 这里需要补一个方法
												// 先查询到部门考核评分记录中的垂直R是被考核人同时评分人又不是被考核人的所有记录
												// 然后处理这些记录，先看状态吧
												// 如果状态都满足的话就算分
												// 算分的逻辑是这样： 先按照指标分组，同个指标的分数求平均
												// 然后每个指标的分再加权平均就是最后得分啦
												HashMap temp = getScoreByDeptScore(scoretype, allcheckedusers[i], logid, user_state);
												alldeptdetail = (List) temp.get("alldetails");
												if ("N".equals(temp.get("isok"))) {
													isok = false;
												} else {
													if (!temp.containsKey("value")) { // 部门考核中没有我主要负责的
														continue;
													}
													HashVO scorevo = null;
													if (scorers.length > 0) {
														List scores = scorer_scorer.get(scorers[0]);
														if (scores != null && scores.size() > 0) {
															scorevo = (HashVO) scores.get(0);
														}
													}
													allscorerscore = (BigDecimal) temp.get("value");
													allscorerfullscore = (BigDecimal) temp.get("fullvalue");
													allscorerleve = (BigDecimal) temp.get("allweights");
													UpdateSQLBuilder updateSql = new UpdateSQLBuilder("sal_person_check_score");
													if (scorevo != null) { // 这个地方可以把实际完成比例
														// 反写到数据库中。
														updateSql.putFieldValue("checkscore", allscorerscore.divide(allscorerfullscore, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
													}
												}
											}
											if (isok) {
												if (allscorerleve.compareTo(BigDecimal.ZERO) == 0) {
													onegroupscore = new BigDecimal("0");
													onegroupfullscore = new BigDecimal("0");
												} else {
													onegroupscore = allscorerscore.divide(allscorerleve, 6, BigDecimal.ROUND_HALF_UP); // 这个组对被考核人的加权平均分
													onegroupfullscore = allscorerfullscore.divide(allscorerleve, 6, BigDecimal.ROUND_HALF_UP);
												}
											}

										} else {
											onegroupscore = new BigDecimal(0);
											onegroupfullscore = new BigDecimal(0);
										}

										if (selfDeptCheckMap.containsKey(groupids[g]) && sa.containsKey(allcheckedusers[i]) && groupids.length > 1) { // 这个人的部门内部分
											// 这个map里有我的部门内部分肯定我的本组状态是ok的
											if ("平均".equals(userweightstype)) {
												allgroupscore = allgroupscore.add(new BigDecimal(sa.get(allcheckedusers[i]) + "").divide(new BigDecimal("10"), 2, BigDecimal.ROUND_HALF_UP));
												allgroupfullscore = allgroupfullscore.add(new BigDecimal("10"));
												allgroupleve = allgroupleve.add(new BigDecimal(1));
											} else {
												allgroupscore = allgroupscore.add(new BigDecimal(sa.get(allcheckedusers[i]) + "").divide(new BigDecimal("10"), 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(groupweight)));
												allgroupfullscore = allgroupfullscore.add(new BigDecimal("10").multiply(new BigDecimal(groupweight)));
												allgroupleve = allgroupleve.add(new BigDecimal(groupweight));
											}
										} else {
											if (isok) {
												if ("平均".equals(userweightstype)) {
													allgroupscore = allgroupscore.add(onegroupscore);
													allgroupfullscore = allgroupfullscore.add(onegroupfullscore);
													allgroupleve = allgroupleve.add(new BigDecimal(1));
												} else {
													allgroupscore = allgroupscore.add(onegroupscore.multiply(new BigDecimal(groupweight)));
													allgroupfullscore = allgroupfullscore.add(onegroupfullscore.multiply(new BigDecimal(groupweight)));
													allgroupleve = allgroupleve.add(new BigDecimal(groupweight));
												}
											}
										}

									}
									if (isok) {
										if (allgroupleve.compareTo(BigDecimal.ZERO) == 0) {
											onecheckeduserscore = new BigDecimal(0);
											onecheckeduserfullscore = new BigDecimal(0);
										} else {
											onecheckeduserscore = allgroupscore.divide(allgroupleve, 6, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
											onecheckeduserfullscore = allgroupfullscore.divide(allgroupleve, 6, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP);
										}
									}
								} else {
									onecheckeduserscore = new BigDecimal(0.00);
									onecheckeduserfullscore = new BigDecimal(0.00);
								}
								List<BillCellItemVO> itemsn = new ArrayList<BillCellItemVO>();
								itemsn.add(getBillNormalCellItemVO(list.size(), checkeduerobj));
								itemsn.add(getBillNormalCellItemVO(list.size(), checkeduerdeptname));
								itemsn.add(getBillNormalCellItemVO(list.size(), checkedusersname));
								BillCellItemVO vo3 = getBillNormalCellItemVO(list.size(), user_state.get(allcheckedusers[i]) + "");
								vo3.setForeground(getColorByState(user_state.get(allcheckedusers[i]) + ""));
								itemsn.add(vo3);
								if (isok) {
									// itemsn.add(getBillNormalCellItemVO(i,
									// onecheckeduserscore.toString()));
									if (onecheckeduserfullscore.compareTo(BigDecimal.ZERO) == 0) {
										itemsn.add(getBillNormalCellItemVO(i, "0.00%"));
									} else {
										itemsn.add(getBillNormalCellItemVO(i, onecheckeduserscore.multiply(new BigDecimal("100")).divide(onecheckeduserfullscore, 6, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP).toString() + "%"));
									}
									if (issave) {
										InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_check_result");
										isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_RESULT"));
										isb.putFieldValue("checkeduserid", allcheckedusers[i]);
										isb.putFieldValue("logid", logid);
										isb.putFieldValue("finalres", onecheckeduserscore.toString());
										if (mapindex == 0) {// 如果是高管垂直
											isb.putFieldValue("targettype", "高管垂直指标");
										} else {
											isb.putFieldValue("targettype", "员工定性指标");
										}
										isb.putFieldValue("stationkind", checkeduerobj);
										isb.putFieldValue("checkeduserdeptname", checkeduerdeptname);
										isb.putFieldValue("checkeduserdeptid", checkeduerdeptid);
										isb.putFieldValue("checkedusername", checkedusersname);
										isb.putFieldValue("state", vo3.getCellvalue());
										isb.putFieldValue("finalres2", itemsn.get(itemsn.size() - 1).getCellvalue().replaceAll("%", ""));
										if (alldeptdetail != null && alldeptdetail.size() > 0) {
											isb.putFieldValue("ishavedept", "Y");
										} else {
											isb.putFieldValue("ishavedept", "N");
										}
										if (allpersondetail != null && allpersondetail.size() > 0) {
											isb.putFieldValue("ishaveperson", "Y");
										} else {
											isb.putFieldValue("ishaveperson", "N");
										}
										if (allggdetail != null && allggdetail.size() > 0) {
											isb.putFieldValue("ishavegg", "Y");
										} else {
											isb.putFieldValue("ishavegg", "N");
										}
										if (sa.containsKey(allcheckedusers[i])) {
											isb.putFieldValue("finalres3", sa.get(allcheckedusers[i]) + "");
										}
										sqls.add(isb.getSQL());
									}
								} else {
									// itemsn.add(getBillNormalCellItemVO(list.size(),
									// ""));
									itemsn.add(getBillNormalCellItemVO(list.size(), ""));
								}

								// 处理定量指标
								String state2 = null;
								BigDecimal realscore2 = null;
								BigDecimal targetweights2 = null;
								BigDecimal allweight2 = new BigDecimal(0);
								BigDecimal allscore2 = new BigDecimal(0);
								if (userid_quantifyscore.containsKey(allcheckedusers[i])) { // 如果此人有定量的指标
									List<HashVO> scorervos = userid_quantifyscore.get(allcheckedusers[i]);
									for (int v = 0; v < scorervos.size(); v++) { // 遍历每个评分人的打分记录
										state2 = scorervos.get(v).getStringValue("status", "abc");
										if (!"已提交".equals(state2)) { // 有一条未提交那么这个被考核人的分数就算不了
											isok2 = false;
										}
										getState(user_state2, allcheckedusers[i], state2);
										if (isok2) {
											realscore2 = new BigDecimal(scorervos.get(v).getStringValue("checkscore"));
											targetweights2 = new BigDecimal(scorervos.get(v).getStringValue("weights"));
											allweight2 = allweight2.add(targetweights2); // 指标权重的和
											allscore2 = allscore2.add(realscore2.multiply(targetweights2)); // 得分乘以指标权重的和
										}
									}
									if (isok2) {
										if (isshowdx) {
											BillCellItemVO vo6 = getBillNormalCellItemVO(i, user_state2.get(allcheckedusers[i]) + "");
											vo6.setForeground(getColorByState(user_state2.get(allcheckedusers[i]) + ""));
											itemsn.add(vo6);
											if (allweight2.compareTo(BigDecimal.ZERO) == 0) {
												itemsn.add(getBillNormalCellItemVO(list.size(), "0"));
											} else {
												itemsn.add(getBillNormalCellItemVO(list.size(), allscore2.divide(allweight2, 6, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP) + ""));
											}
										}
										if (issave2) {
											InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_check_result");
											isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_RESULT"));
											isb.putFieldValue("checkeduserid", allcheckedusers[i]);
											isb.putFieldValue("logid", logid);
											if (allweight2.compareTo(BigDecimal.ZERO) == 0) {
												isb.putFieldValue("finalres", "0");
											} else {
												isb.putFieldValue("finalres", allscore2.divide(allweight2, 6, BigDecimal.ROUND_HALF_UP).setScale(2, BigDecimal.ROUND_HALF_UP) + "");
											}
											isb.putFieldValue("targettype", "员工定量指标");
											sqls.add(isb.getSQL());
										}
									} else {
										if (isshowdx) {
											BillCellItemVO vo6 = getBillNormalCellItemVO(list.size(), user_state2.get(allcheckedusers[i]) + "");
											vo6.setForeground(getColorByState(user_state2.get(allcheckedusers[i]) + ""));
											itemsn.add(vo6);
											itemsn.add(getBillNormalCellItemVO(list.size(), ""));
										}
									}
								} else {
									if (isshowdx) {
										BillCellItemVO vo6 = getBillNormalCellItemVO(list.size(), "-");
										vo6.setForeground(getColorByState("-"));
										itemsn.add(vo6);
										itemsn.add(getBillNormalCellItemVO(list.size(), "-"));
									}
								}
								if (isreturn) {
									if (iswatch) {
										BillCellItemVO vol = getBillNormalCellItemVO(list.size(), "查看详细");
										vol.setForeground("0,0,255");
										vol.setFontstyle("0");
										vol.setIshtmlhref("Y");
										if (alldeptdetail != null && alldeptdetail.size() > 0) {
											vol.setCustProperty("ishavedept", true);
										} else {
											vol.setCustProperty("ishavedept", false);
										}
										if (allpersondetail != null && allpersondetail.size() > 0) {
											vol.setCustProperty("ishaveperson", true);
										} else {
											vol.setCustProperty("ishaveperson", false);
										}
										if (allggdetail != null && allggdetail.size() > 0) {
											vol.setCustProperty("ishavegg", true);
										} else {
											vol.setCustProperty("ishavegg", false);
										}
										vol.setCustProperty("logid", logid);
										vol.setCustProperty("checkeduserid", allcheckedusers[i]);
										itemsn.add(vol);
									}
									list.add(itemsn.toArray(new BillCellItemVO[0]));
								}
							}
						}
					}
					// 原来的逻辑是支持多次考核的查询
					if (o < logidandname.length - 1 && isreturn) {
						List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
						items1.add(getBillNormalCellItemVO(0, ""));
						items1.add(getBillNormalCellItemVO(0, ""));
						items1.add(getBillNormalCellItemVO(0, ""));
						items1.add(getBillNormalCellItemVO(0, ""));
						// items1.add(getBillNormalCellItemVO(0, ""));
						items1.add(getBillNormalCellItemVO(0, ""));
						if (isshowdx) {
							items1.add(getBillNormalCellItemVO(0, ""));
							items1.add(getBillNormalCellItemVO(0, ""));
						}
						if (iswatch) {
							items1.add(getBillNormalCellItemVO(0, ""));
						}
						items1.get(0).setColwidth("300");
						items1.get(0).setSpan("1," + items1.size());
						list.add(items1.toArray(new BillCellItemVO[0]));
					}
				}
				if (isreturn) {
					vo.setCollength(list.get(0).length);
					vo.setRowlength(list.size());
					BillCellItemVO[][] items = (BillCellItemVO[][]) list.toArray(new BillCellItemVO[0][0]);
					int li_allowMaxColWidth = 375;
					FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
					for (int j = 0; j < items[0].length; j++) {
						int li_maxwidth = 0;
						String str_cellValue = null;
						for (int i = 0; i < items.length; i++) {
							str_cellValue = items[i][j].getCellvalue();
							if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
								int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
								if (li_width > li_maxwidth) {
									li_maxwidth = li_width;
								}
							}
						}
						li_maxwidth = li_maxwidth + 13;
						if (li_maxwidth > li_allowMaxColWidth) {
							li_maxwidth = li_allowMaxColWidth;
						}
						for (int i = 1; i < items.length; i++) {
							str_cellValue = items[i][j].getCellvalue();
							items[i][j].setColwidth("" + li_maxwidth);
						}
					}
					formatSpan(items, new int[] { 0, 1 });
					// 重置颜色和合并内容。
					if (person_start_row > 0) {
						items[person_start_row][0].setSpan("1," + items[person_start_row].length);// 重置空行合并
						items[person_start_row][0].setBackground("184,255,185");// 重置颜色
						items[person_start_row + 1][0].setBackground("184,255,185");// 重置颜色
						items[person_start_row + 1][1].setBackground("184,255,185");// 重置颜色
					}
					vo.setCellItemVOs(items);
				}
			}
		} else {
			vo.setRowlength(1);
			vo.setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			vo.setCellItemVOs(items);
		}
		if (issave || issave2) {
			getDmo().executeBatchByDS(null, sqls);
		}
		return vo;
	}

	/**
	 * 
	 * 部门内部互评组的得分使用与部门内部最高分的比
	 * 
	 * @param selfDeptCheckMap
	 * @param user_level
	 * @param map
	 * @return
	 * @throws Exception
	 */
	public HashMap getSelfDeptCheckScore(HashMap selfDeptCheckMap, HashMap<String, String> user_level, LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>> map) throws Exception {
		HashMap selfDeptCheckPerson_score = new HashMap();
		String[] allcheckedusers = (String[]) map.keySet().toArray(new String[0]); // 所有被定性指标考核的考核人
		String checkeduerdeptid = null;
		if (selfDeptCheckMap == null) { //
			return null;
		}
		if (allcheckedusers != null && allcheckedusers.length > 0) {
			BigDecimal onecheckeduserscore = new BigDecimal(0);
			BigDecimal onecheckeduserfullscore = new BigDecimal(0);
			for (int i = 0; i < allcheckedusers.length; i++) { // 遍历所有被考核人
				checkeduerdeptid = null;
				String userweightstype = null; // 被考核人的权限方式
				// 平均、加权平均
				BigDecimal onegroupscore = new BigDecimal(0); // 每个评分组的最终评分
				BigDecimal onegroupfullscore = new BigDecimal(0); // 每个评分组的满分
				LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>> group_scorer = map.get(allcheckedusers[i]); // 被考核人被哪些组考核
				String[] groupids = (String[]) group_scorer.keySet().toArray(new String[0]);
				if (groupids != null && groupids.length > 0) {
					boolean isok = true;
					for (int g = 0; g < groupids.length; g++) { // 遍历考核人所有的评价组
						if (groupids[g] == null || !selfDeptCheckMap.containsKey(groupids[g])) {
							continue;
						}
						String scoretype = null;
						String groupweightstype = null; // 评分组的权限方式
						// 平均、按岗位系数加权平均
						String groupweight = null; // 这个评分组的权重
						LinkedHashMap<String, List<HashVO>> scorer_scorer = group_scorer.get(groupids[g]);
						String[] scorers = (String[]) scorer_scorer.keySet().toArray(new String[0]);
						BigDecimal onescorerscore = new BigDecimal(0); // 每个评分人的最终评分
						BigDecimal onescorerfullscore = new BigDecimal(0);
						BigDecimal allscorerscore = new BigDecimal(0); // 组下面所有评分人的最终的评分和
						BigDecimal allscorerfullscore = new BigDecimal(0);
						BigDecimal allscorerleve = new BigDecimal(0); // 组下面所有评分人岗位系数和或者人数
						if (scorers != null && scorers.length > 0) {
							for (int s = 0; s < scorers.length; s++) { // 遍历这个评分组下的评分人
								List<HashVO> scorervos = scorer_scorer.get(scorers[s]); // 当前评分人的打分记录
								if (scorervos != null && scorervos.size() > 0) {
									scoretype = scorervos.get(0).getStringValue("scoretype", "手动打分");
									BigDecimal allweight = new BigDecimal(0); // 权重和
									BigDecimal allscore = new BigDecimal(0); // 所有得分和
									BigDecimal allfullscore = new BigDecimal(0); // 所有满分和
									BigDecimal realscore = null; // 每一条打分记录的
									// 实际得分
									BigDecimal targetweights = null; // 指标权重
									String scorer = null; // 评分人id
									String state = null;
									for (int v = 0; v < scorervos.size(); v++) { // 遍历每个评分人的打分记录
										state = scorervos.get(v).getStringValue("status", "abc");
										if (groupweightstype == null || "".equals(groupweightstype)) { // 理论上每一条打分记录这个值都是一样的
											groupweightstype = scorervos.get(v).getStringValue("groupweightstype", "");
										}
										if (groupweight == null || "".equals(groupweight)) { // 理论上每一条打分记录这个值都是一样的
											groupweight = scorervos.get(v).getStringValue("groupweights", "");
										}
										if (userweightstype == null || "".equals(userweightstype)) { // 理论上每一条打分记录这个值都是一样的
											userweightstype = scorervos.get(v).getStringValue("scorerweightstype", "");
										}
										if (checkeduerdeptid == null || "".equals(checkeduerdeptid)) { // 理论上每一条打分记录这个值都是一样的
											checkeduerdeptid = scorervos.get(v).getStringValue("checkeduserdeptid", "");
										}
										if (!scoretype.equals("手动打分")) {
											break;
										}
										if (!"已提交".equals(state)) { // 有一条未提交那么这个被考核人的分数就算不了
											isok = false;
										}
										if (isok) {
											realscore = new BigDecimal(scorervos.get(v).getStringValue("checkscore"));
											targetweights = new BigDecimal(scorervos.get(v).getStringValue("weights"));
											allweight = allweight.add(targetweights); // 指标权重的和
											allscore = allscore.add(realscore.multiply(targetweights)); // 得分乘以指标权重的和
											allfullscore = allfullscore.add(new BigDecimal("10").multiply(targetweights));
										}
									}
									if (!scoretype.equals("手动打分")) {
										break;
									}
									if (isok) {
										if (allweight.compareTo(BigDecimal.ZERO) == 0) {
											onescorerscore = new BigDecimal("0"); // 最终这个评分人对被考核人的加权分
											onescorerfullscore = new BigDecimal("0"); // 满分
										} else {
											onescorerscore = allscore.divide(allweight, 6, BigDecimal.ROUND_HALF_UP); // 最终这个评分人对被考核人的加权分
											onescorerfullscore = allfullscore.divide(allweight, 6, BigDecimal.ROUND_HALF_UP);
										}
									}
								} else {
									onescorerscore = new BigDecimal(0);
									onescorerfullscore = new BigDecimal(0);
								}
								if (isok) {
									if ("平均".equals(groupweightstype)) {
										allscorerleve = allscorerleve.add(new BigDecimal(1));
										allscorerscore = allscorerscore.add(onescorerscore);
										allscorerfullscore = allscorerfullscore.add(onescorerfullscore);
									} else {
										if (user_level != null && user_level.containsKey(scorers[s])) {
											allscorerscore = allscorerscore.add(onescorerscore.multiply(new BigDecimal(user_level.get(scorers[s])))); // 这个组下所有评分人最终分的加权
											allscorerfullscore = allscorerfullscore.add(onescorerfullscore.multiply(new BigDecimal(user_level.get(scorers[s]))));
											allscorerleve = allscorerleve.add(new BigDecimal(user_level.get(scorers[s])));
										}
									}
								}
							}
							if (isok) {
								if (allscorerleve.compareTo(BigDecimal.ZERO) == 0) {
									onegroupscore = new BigDecimal("0");
									onegroupfullscore = new BigDecimal("0");
								} else {
									onegroupscore = allscorerscore.divide(allscorerleve, 6, BigDecimal.ROUND_HALF_UP); // 这个组对被考核人的加权平均分
									onegroupfullscore = allscorerfullscore.divide(allscorerleve, 6, BigDecimal.ROUND_HALF_UP);
								}
								// 这里增加 本部门互评组的最终得分
								if (selfDeptCheckMap != null && selfDeptCheckMap.containsKey(groupids[g])) {
									if (checkeduerdeptid != null && !"".equals(checkeduerdeptid)) { // 没有部门的就不处理了
										String value = "";
										if (onegroupfullscore.compareTo(BigDecimal.ZERO) == 0) {
											value = "0";
										} else { // 报持一致否则小数点位数会导致结果有出入
											value = new BigDecimal(onegroupscore.toString()).setScale(2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100")).divide(onegroupfullscore, 2, BigDecimal.ROUND_HALF_UP).toString();
										}
										if (selfDeptCheckPerson_score.containsKey(checkeduerdeptid)) {
											((HashMap) selfDeptCheckPerson_score.get(checkeduerdeptid)).put(allcheckedusers[i], value);
										} else {
											HashMap map_ = new HashMap();
											map_.put(allcheckedusers[i], value);
											selfDeptCheckPerson_score.put(checkeduerdeptid, map_);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		HashMap temp = getPersonDeptSelfCheck(selfDeptCheckPerson_score);
		return temp;
	}

	public BillCellVO getScoreVO_Person(String month) throws Exception {
		String id = getDmo().getStringValueByDS(null, "select id,name from sal_target_check_log where checkdate='" + month + "'");
		return getScoreVO_Person(id, null, false, false, true, false, true);
	}

	/**
	 * 部门内部考核的评分比
	 */
	public HashMap getPersonDeptSelfCheck(HashMap map) {
		String score_adjuse = getTb().getSysOptionStringValue("员工评议分数映射区间", null);
		HashMap temp = new HashMap();
		if (map != null && map.size() > 0) {
			String[] depts = (String[]) map.keySet().toArray(new String[0]);
			for (int i = 0; i < depts.length; i++) {
				HashMap user_score = (HashMap) map.get(depts[i]);
				String[] users = (String[]) user_score.keySet().toArray(new String[0]);
				String[] allscore = (String[]) user_score.values().toArray(new String[0]);
				if (allscore != null && allscore.length > 0) {
					BigDecimal max = new BigDecimal("0");
					BigDecimal min = new BigDecimal("100");
					for (int a = 0; a < allscore.length; a++) {
						if (new BigDecimal(allscore[a]).compareTo(max) > 0) {
							max = new BigDecimal(allscore[a]);
						}
						if (new BigDecimal(allscore[a]).compareTo(min) < 0) {
							min = new BigDecimal(allscore[a]);
						}
					}
					if (max.compareTo(BigDecimal.ZERO) > 0) {
						float d_value = max.subtract(min).floatValue();
						for (int a = 0; a < users.length; a++) {
							if (score_adjuse == null) {
								temp.put(users[a], user_score.get(users[a]));
								continue;
							}
							float definevalue = 0f; // 根据人员数量取定义的最小分数
							String dfstr = stbutil.getScoreConfigMinVal(score_adjuse, users.length);
							if (dfstr != null) {
								definevalue = Float.parseFloat(dfstr);
								if (d_value != 0) { // 等比放大
									float lastvalue = (Float.parseFloat(String.valueOf(user_score.get(users[a]))) - min.floatValue()) / d_value * (100f - definevalue) + definevalue;
									temp.put(users[a], new BigDecimal(lastvalue + "").setScale(2, BigDecimal.ROUND_HALF_UP));
								} else { // 都是相等的。//该部门需要被警告。以后可以添加.郝明2014-4-4
									temp.put(users[a], user_score.get(users[a]));
								}
							} else {
								temp.put(users[a], user_score.get(users[a]));
							}
							// temp.put(users[a], new
							// BigDecimal(user_score.get(users[a]) +
							// "").multiply(new BigDecimal("100")).divide(max,
							// 2, BigDecimal.ROUND_HALF_UP));
						}
					} else {
						for (int a = 0; a < users.length; a++) {
							temp.put(users[a], "100");
						}
					}
				}
			}
		}
		return temp;
	}

	/**
	 * 重要方法将部门打分详情表逻辑处理 返回各种hashmap
	 * 
	 * @param allscores
	 * @return
	 */
	public LinkedHashMap[] getAllTempMap(HashVO[] allscores) {
		LinkedHashMap[] rtnmaps = new LinkedHashMap[10];
		LinkedHashMap<String, Integer> dept_count = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, String> dept_state = new LinkedHashMap<String, String>(); // 总状态
		LinkedHashMap<String, String> dept_state1 = new LinkedHashMap<String, String>(); // 定性的状态
		LinkedHashMap<String, String> dept_state2 = new LinkedHashMap<String, String>(); // 定量的状态
		LinkedHashMap<String, String> dept_state3 = new LinkedHashMap<String, String>(); // 部门计价指标的状态
		LinkedHashMap<String, Integer> dept_finshc = new LinkedHashMap<String, Integer>();
		LinkedHashMap<String, String> targetid_name = new LinkedHashMap<String, String>();
		LinkedHashMap dept_score = new LinkedHashMap();
		LinkedHashMap target_value = new LinkedHashMap();
		LinkedHashMap dept_target_state = new LinkedHashMap();
		rtnmaps[0] = dept_count;
		rtnmaps[1] = dept_state;
		rtnmaps[2] = dept_finshc;
		rtnmaps[3] = dept_score;
		rtnmaps[4] = target_value;
		rtnmaps[5] = dept_target_state;
		rtnmaps[6] = dept_state1;
		rtnmaps[7] = dept_state2;
		rtnmaps[8] = targetid_name;
		rtnmaps[9] = dept_state3;
		String deptid = null;
		for (int i = 0; i < allscores.length; i++) {
			deptid = allscores[i].getStringValue("checkeddept");
			// 用来存储 部门-指标数量
			target_value.put(allscores[i].getStringValue("targetid"), allscores[i].getStringValue("weights"));
			targetid_name.put(allscores[i].getStringValue("targetid"), allscores[i].getStringValue("targetname"));
			if (dept_count.containsKey(deptid)) {
				dept_count.put(deptid, dept_count.get(deptid) + 1);
			} else {
				dept_count.put(deptid, 1);
			}
			// 用来存储 部门-状态
			getState(dept_state, deptid, allscores[i].getStringValue("status", "abc"));
			if ("部门定性指标".equals(allscores[i].getStringValue("targettype", "部门定性指标"))) {
				getState(dept_state1, deptid, allscores[i].getStringValue("status", "abc"));
			} else if ("部门定量指标".equals(allscores[i].getStringValue("targettype", "部门定量指标"))) {
				getState(dept_state2, deptid, allscores[i].getStringValue("status", "abc"));
			} else if ("部门计价指标".equals(allscores[i].getStringValue("targettype", "部门计价指标"))) {
				getState(dept_state3, deptid, allscores[i].getStringValue("status", "abc"));
			}
			// 用来存储 部门-完成指标数量
			if (scoreFinishState.indexOf(allscores[i].getStringValue("status", "abc")) >= 0) {
				if (dept_finshc.containsKey(deptid)) {
					dept_finshc.put(deptid, dept_finshc.get(deptid) + 1);
				} else {
					dept_finshc.put(deptid, 1);
				}
			}

			if (dept_target_state.containsKey(deptid)) {
				LinkedHashMap target_state = (LinkedHashMap) dept_target_state.get(deptid);
				getState(target_state, allscores[i].getStringValue("targetid", ""), allscores[i].getStringValue("status", ""));
			} else {
				LinkedHashMap target_state = new LinkedHashMap();
				getState(target_state, allscores[i].getStringValue("targetid", ""), allscores[i].getStringValue("status", ""));
				dept_target_state.put(deptid, target_state);
			}

			// 用来存储 部门-（指标-（考核类型-所有分数））
			if (dept_score.containsKey(deptid)) {
				HashMap target_score = (HashMap) dept_score.get(deptid);
				if (target_score.containsKey(allscores[i].getStringValue("targetid"))) {
					HashMap type_score = (HashMap) target_score.get(allscores[i].getStringValue("targetid"));
					if (type_score.containsKey(allscores[i].getStringValue("checktype"))) {
						((List) type_score.get(allscores[i].getStringValue("checktype"))).add(allscores[i].getStringValue("checkscore", "0"));
					} else {
						List scorelist = new ArrayList();
						scorelist.add(allscores[i].getStringValue("checkscore", "0"));
						type_score.put(allscores[i].getStringValue("checktype"), scorelist);
					}
				} else {
					HashMap type_score = new HashMap();
					List scorelist = new ArrayList();
					scorelist.add(allscores[i].getStringValue("checkscore", "0"));
					type_score.put(allscores[i].getStringValue("checktype"), scorelist);
					target_score.put(allscores[i].getStringValue("targetid"), type_score);
				}
			} else {
				HashMap target_score = new HashMap();
				HashMap type_score = new HashMap();
				List scorelist = new ArrayList();
				scorelist.add(allscores[i].getStringValue("checkscore", "0"));
				type_score.put(allscores[i].getStringValue("checktype"), scorelist);
				target_score.put(allscores[i].getStringValue("targetid"), type_score);
				dept_score.put(deptid, target_score);
			}
		}
		return rtnmaps;
	}

	/*
	 * 将所有评分记录按照被考核人分组，然后每个被考核人的所有评分记录按照评分组进行分类，然后再根据评分人进行分组
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
			ReportUtil rtil = new ReportUtil();
			List allcheckeduserids = new ArrayList();
			for (int i = 0; i < allscores.length; i++) {
				allcheckeduserids.add(allscores[i].getStringValue("checkeduser", ""));
			}
			rtil.allOneFieldNameFromOtherTable(allscores, "checkeduserdeptname", "checkeduser", "select p.userid, u.name from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition(allcheckeduserids) + ")");
			rtil.allOneFieldNameFromOtherTable(allscores, "checkeduserdeptid", "checkeduser", "select p.userid, u.id from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition(allcheckeduserids) + ")");
			rtil.allOneFieldNameFromOtherTable(allscores, "deptseq", "checkeduser", "select p.userid, u.seq from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition(allcheckeduserids) + ")");
			getTb().sortHashVOs(allscores, new String[][] { { "seq", "N", "Y" }});
			for (int i = 0; i < allscores.length; i++) {
				checkeduserid = allscores[i].getStringValue("checkeduser", ""); // 被考核人
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
					groupid = allscores[i].getStringValue("scoreusertype", ""); // 评分组
					scorer = allscores[i].getStringValue("scoreuser", ""); // 评分人
					allscorers.add(scorer);
					groupweightstyle = allscores[i].getStringValue("groupweightstype", ""); // 组内人员的权重计算方式
					groupweight = allscores[i].getStringValue("groupweights", ""); // 评分组的权重
					userweightstyle = allscores[i].getStringValue("scorerweightstype", ""); // 评分组之间的权重方式
					if (!"".equals(checkeduserid) && !"".equals(groupid) && !"".equals(userweightstyle)) {
						LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>> group_score = new LinkedHashMap<String, LinkedHashMap<String, List<HashVO>>>();
						boolean have = false;
						if ("高管垂直".equals(allscores[i].getStringValue("checktype"))) { // 如果考核是高管垂直。
							if (user_score_ggcc.containsKey(checkeduserid)) {
								group_score = user_score_ggcc.get(checkeduserid);
								have = true;
							}
						} else {
							if (user_score.containsKey(checkeduserid)) {
								group_score = user_score.get(checkeduserid);
								have = true;
							}
						}
						if (have && group_score.containsKey(groupid)) {
							LinkedHashMap<String, List<HashVO>> score_score = group_score.get(groupid);
							if (score_score.containsKey(scorer)) {
								List<HashVO> scorers = score_score.get(scorer);
								scorers.add(allscores[i]);
							} else {
								List<HashVO> scorers = new ArrayList<HashVO>();
								scorers.add(allscores[i]);
								score_score.put(scorer, scorers);
							}
						} else {
							LinkedHashMap<String, List<HashVO>> score_score = new LinkedHashMap<String, List<HashVO>>(); // 一个评分人对被考核人的所有打分结果
							List<HashVO> scorers = new ArrayList<HashVO>();
							scorers.add(allscores[i]);
							score_score.put(scorer, scorers);
							group_score.put(groupid, score_score); // sal_person_check_plan_scorer的ID，评分组
							if ("高管垂直".equals(allscores[i].getStringValue("checktype"))) {
								user_score_ggcc.put(checkeduserid, group_score); // 被考核人
							} else {
								user_score.put(checkeduserid, group_score); // 被考核人
							}
						}
					}
				}
			}
		}
		HashMap user_level = getDmo().getHashMapBySQLByDS(null, "select id, stationratio from sal_personinfo where stationratio is not null and id in (" + getTb().getInCondition(allscorers) + ")");
		HashMap erroruser = getDmo().getHashMapBySQLByDS(null, "select u.name, '1' from pub_user u left join sal_personinfo p on u.code=p.code where (p.stationratio is null or p.stationratio='') and u.id in (" + getTb().getInCondition(allscorers) + ")");
		maps[0] = user_score;
		maps[1] = user_level;
		maps[2] = erroruser;
		maps[3] = user_quantifyscore;
		maps[4] = user_score_ggcc;
		return maps;
	}

	/**
	 * 可以获得到高管的分管部门得分。
	 * 
	 * @param checkeduserid
	 * @param logid
	 * @param user_state
	 * @return
	 * @throws Exception
	 */
	private HashMap getScoreByDeptScore(String scoretype, String checkeduserid, String logid, HashMap user_state) throws Exception {
		HashMap rtn = new HashMap();
		rtn.put("isok", "Y");
		// 先找我已垂直R身份要给那些指标打分
		HashVO[] allscorevo = null;
		List<HashVO> allscorevos = new ArrayList<HashVO>();
		if ("分管部门得分".equals(scoretype) || "分管部门定性得分".equals(scoretype) || "分管部门总分".equals(scoretype)) {
			String[] alltargetids = getDmo().getStringArrayFirstColByDS(null, "select targetid  from sal_dept_check_score where checktype='垂直R' and scoreuser=" + checkeduserid + " and logid=" + logid);
			// 然后找这些指标的打分记录当然要去除自己的打分记录
			HashVO[] allscorevo_ = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where checktype<>'垂直R' and targetid in (" + getTb().getInCondition(alltargetids) + ") and logid=" + logid);
			for (int i = 0; i < allscorevo_.length; i++) {
				allscorevos.add(allscorevo_[i]);
			}
		}
		if ("分管部门定量得分".equals(scoretype) || "分管部门总分".equals(scoretype)) {
			String[] stations = getDmo().getStringArrayFirstColByDS(null, "select postid from pub_user_post where userid=" + checkeduserid); // 不是主岗也有可能
			StringBuffer sb = new StringBuffer("(1=2 ");
			for (int i = 0; i < stations.length; i++) {
				sb.append(" or rleader like '%;" + stations[i] + ";%' ");
			}
			sb.append(")");
			// 获取被考核人的岗位 找到 定量指标
			HashMap p = new HashMap();
			HashVO[] allscorevo_1 = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString() + ") and (pleader is not null and pleader <> '') and state='参与考核') and logid=" + logid);
			for (int i = 0; i < allscorevo_1.length; i++) {
				allscorevo_1[i].setAttributeValue("main", "0.6"); // 是主管领导
				allscorevos.add(allscorevo_1[i]);
				p.put(allscorevo_1[i].getStringValue("id"), "");
			}
			HashVO[] allscorevo_1_ = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString() + ") and (pleader is null or pleader='') and state='参与考核') and logid=" + logid);
			for (int i = 0; i < allscorevo_1_.length; i++) {
				allscorevo_1_[i].setAttributeValue("main", "1"); // 是主管领导
				allscorevos.add(allscorevo_1_[i]);
				p.put(allscorevo_1_[i].getStringValue("id"), "");
			}
			HashVO[] allscorevo_2 = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString().replaceAll("rleader", "pleader") + ") and (rleader is not null and rleader <> '')  and state='参与考核') and logid=" + logid);
			for (int i = 0; i < allscorevo_2.length; i++) {
				if (!p.containsKey(allscorevo_2[i].getStringValue("id"))) {
					allscorevo_2[i].setAttributeValue("main", "0.4"); // 是分管领导
					allscorevos.add(allscorevo_2[i]);
				}
			}
			HashVO[] allscorevo_2_ = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString().replaceAll("rleader", "pleader") + ") and (rleader is null or rleader='')  and state='参与考核') and logid=" + logid);
			for (int i = 0; i < allscorevo_2_.length; i++) {
				if (!p.containsKey(allscorevo_2_[i].getStringValue("id"))) {
					allscorevo_2_[i].setAttributeValue("main", "1"); // 是分管领导
					allscorevos.add(allscorevo_2_[i]);
				}
			}
		}
		List alldetails = new ArrayList();
		allscorevo = allscorevos.toArray(new HashVO[0]);
		if (allscorevo != null && allscorevo.length > 0) {
			LinkedHashMap<String, HashMap<String, List<HashVO>>> target_type = new LinkedHashMap<String, HashMap<String, List<HashVO>>>();
			boolean isok = true;
			for (int i = 0; i < allscorevo.length; i++) {
				getState(user_state, checkeduserid, allscorevo[i].getStringValue("status", ""));
				alldetails.add(allscorevo[i].getStringValue("id", ""));
				if (!"已提交".equals(allscorevo[i].getStringValue("status", ""))) {
					isok = false;
				}
				if (isok) {
					if (target_type.containsKey(allscorevo[i].getStringValue("targetid", ""))) {
						if (target_type.get(allscorevo[i].getStringValue("targetid", "")).containsKey(allscorevo[i].getStringValue("checktype", ""))) {
							List<HashVO> scores = target_type.get(allscorevo[i].getStringValue("targetid", "")).get(allscorevo[i].getStringValue("checktype", ""));
							scores.add(allscorevo[i]);
						} else {
							List<HashVO> scores = new ArrayList<HashVO>();
							scores.add(allscorevo[i]);
							target_type.get(allscorevo[i].getStringValue("targetid", "")).put(allscorevo[i].getStringValue("checktype", ""), scores);
						}
					} else {
						HashMap<String, List<HashVO>> type_score = new HashMap<String, List<HashVO>>(); // 指标-打分记录VO
						List<HashVO> scores = new ArrayList<HashVO>();
						scores.add(allscorevo[i]);
						type_score.put(allscorevo[i].getStringValue("checktype", ""), scores);
						target_type.put(allscorevo[i].getStringValue("targetid", ""), type_score);
					}
				}
			}
			rtn.put("alldetails", alldetails);
			if (!isok) {
				rtn.put("isok", "N");
				return rtn;
			}
			BigDecimal[] allrights = getDeptRight();
			BigDecimal c = allrights[0];
			BigDecimal p = allrights[1];
			BigDecimal cr = allrights[2];
			BigDecimal cp = allrights[3];
			BigDecimal pr = allrights[4];
			BigDecimal pp = allrights[5];
			String[] targets = target_type.keySet().toArray(new String[0]);
			BigDecimal allscoreweight = new BigDecimal(0); // 得分加权的和
			BigDecimal allscoreweight_ = new BigDecimal(0); // 满分得分加权的和
			BigDecimal allweight = new BigDecimal(0); // 权重和
			for (int j = 0; j < targets.length; j++) {
				HashMap<String, List<HashVO>> type_score = target_type.get(targets[j]); // 这个部门的这个指标
				// 有哪几类
				// 垂直R、垂直P、平行R、平行P、""
				if (type_score.containsKey("") || type_score.containsKey(null)) { // 为空的就是定量指标了
					// 有定量指标肯定是分管部门定量得分或总得分
					List<HashVO> score = type_score.get("");
					if (score == null || score.size() < 1) {
						score = type_score.get(null);
					}
					if (score != null && score.size() > 0) {
						if (!"".equals(score.get(0).getStringValue("main", ""))) {
							String r = score.get(0).getStringValue("main", "");
							String weights = score.get(0).getStringValue("weights", "0");
							BigDecimal finalres = getScore2(score, new BigDecimal("1"));
							finalres = new BigDecimal(weights).subtract((new BigDecimal(weights).subtract(finalres)).multiply(new BigDecimal(r)));
							allscoreweight = allscoreweight.add(finalres.multiply(new BigDecimal(weights)));
							allscoreweight_ = allscoreweight_.add(new BigDecimal(weights).multiply(new BigDecimal(weights)));
							allweight = allweight.add(new BigDecimal(weights));
						}
					}
					// continue;
				} else {
					BigDecimal right = new BigDecimal(0);
					BigDecimal CR = new BigDecimal(0);
					BigDecimal CP = new BigDecimal(0);
					BigDecimal PR = new BigDecimal(0);
					BigDecimal PP = new BigDecimal(0);
					String weights = null;
					if (type_score.containsKey("垂直R")) {
						List<HashVO> score = type_score.get("垂直R"); // 这个部门的这个指标
						// 垂直R的所有打分明细
						if (score.size() > 0) {
							right = right.add(c.multiply(cr)); // 权重=垂直R的权重*垂直的权重
							CR = getScore2(score, c.multiply(cr));// 根据模拟计算
							// 某一类打分比如垂直R的所有得分算平均
							// *
							// （垂直R的权重*垂直的权重）
							weights = score.get(0).getStringValue("weights", "0");
						}
					}
					if (type_score.containsKey("垂直P")) {
						List<HashVO> score = type_score.get("垂直P");
						if (score.size() > 0) {
							right = right.add(c.multiply(cp));
							CP = getScore2(score, c.multiply(cp));
							if (weights == null) {
								weights = score.get(0).getStringValue("weights", "0");
							}
						}
					}
					if (type_score.containsKey("平行R")) {
						List<HashVO> score = type_score.get("平行R");
						if (score.size() > 0) {
							right = right.add(p.multiply(pr));
							PR = getScore2(score, p.multiply(pr));
							if (weights == null) {
								weights = score.get(0).getStringValue("weights", "0");
							}
						}
					}
					if (type_score.containsKey("平行P")) {
						List<HashVO> score = type_score.get("平行P");
						if (score.size() > 0) {
							right = right.add(p.multiply(pp));
							PP = getScore2(score, p.multiply(pp));
							if (weights == null) {
								weights = score.get(0).getStringValue("weights", "0");
							}
						}
					}
					BigDecimal finalresc = CR.add(CP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
					BigDecimal finalresp = PR.add(PP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
					BigDecimal finalres = finalresc.add(finalresp); // 一个指标根据垂直R、、、计算出来的最终得分
					allscoreweight = allscoreweight.add(finalres.multiply(new BigDecimal(weights)));
					allscoreweight_ = allscoreweight_.add(new BigDecimal(weights).multiply(new BigDecimal(weights)));
					allweight = allweight.add(new BigDecimal(weights));
				}
			}
			rtn.put("value", allscoreweight);
			rtn.put("fullvalue", allscoreweight_);
			rtn.put("allweights", allweight);
		}
		return rtn;
	}

	public BigDecimal getScore(List score, BigDecimal right) {
		BigDecimal rtn = new BigDecimal(0);
		for (int j = 0; j < score.size(); j++) {
			if (score.get(j) != null && !"".equals(score.get(j))) {
				rtn = rtn.add(new BigDecimal(score.get(j) + ""));
			}
		}
		rtn = rtn.divide(new BigDecimal(score.size()), 6, BigDecimal.ROUND_HALF_UP).multiply(right);
		return rtn;
	}

	public BigDecimal getScore2(List<HashVO> score, BigDecimal right) {
		BigDecimal rtn = new BigDecimal(0);
		for (int j = 0; j < score.size(); j++) {
			if (score.get(j) != null) {
				rtn = rtn.add(new BigDecimal(score.get(j).getStringValue("checkscore", "0") + ""));
			}
		}
		rtn = rtn.divide(new BigDecimal(score.size()), 6, BigDecimal.ROUND_HALF_UP).multiply(right);
		return rtn;
	}

	public void getState(HashMap dept_state, String deptid, String state) {
		if (dept_state.containsKey(deptid)) {
			String oldstate = dept_state.get(deptid) + "";
			if ("未评分".equals(oldstate)) {
				if (!"".equals(state) && !"abc".equals(state)) {
					dept_state.put(deptid, "评分中");
				}
			} else if ("申请修改".equals(oldstate)) {
				if (scoreFinishState.indexOf(state) < 0 || "".equals(state)) {
					dept_state.put(deptid, "评分中");
				}
			} else if ("评分中".equals(oldstate)) {
			} else if ("评分完成".equals(oldstate)) {
				if ("已提交".equals(state)) {
				} else if ("申请修改".equals(state)) {
					dept_state.put(deptid, "申请修改");
				} else {
					dept_state.put(deptid, "评分中");
				}
			}
		} else {
			if ("".equals(state) || "abc".equals(state)) {
				dept_state.put(deptid, "未评分");
			} else if ("申请修改".equals(state)) {
				dept_state.put(deptid, "申请修改");
			} else if ("已提交".equals(state)) {
				dept_state.put(deptid, "评分完成");
			} else {
				dept_state.put(deptid, "评分中");
			}
		}
	}

	/**
	 * @param sql
	 * @return
	 */
	public BillCellVO getScoreDetailCellVO(String sql, String templetcode) throws Exception {
		BillCellVO vo = new BillCellVO();
		Pub_Templet_1VO templetvo = getMdmo().getPub_Templet_1VO(templetcode);
		BillVO[] bilvos = getScoreDetail(sql, templetvo); // 排好序的billvo
		if (bilvos != null && bilvos.length > 0) {
			Pub_Templet_1_ItemVO[] itemvos = templetvo.getItemVos();
			if (itemvos != null && itemvos.length > 0) {
				List<String> columnname = new ArrayList<String>();
				List<String> columnkey = new ArrayList<String>();
				for (int i = 0; i < itemvos.length; i++) { // 获取模板显示列根据模板配置excel显示那些列
					if (itemvos[i].getListisshowable()) {
						columnname.add(itemvos[i].getItemname());
						columnkey.add(itemvos[i].getItemkey());
						;
					}
				}
				if (columnname.size() > 0) {
					List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
					BillCellItemVO[] item0 = new BillCellItemVO[columnname.size()]; // 标题
					for (int c = 0; c < columnname.size(); c++) {
						item0[c] = getBillTitleCellItemVO(columnname.get(c));
					}
					items.add(item0);
					for (int i = 0; i < bilvos.length; i++) {
						BillCellItemVO[] itemi = new BillCellItemVO[columnkey.size()];
						for (int c = 0; c < columnkey.size(); c++) {
							itemi[c] = getBillNormalCellItemVO(i, bilvos[i].getStringViewValue(columnkey.get(c)));
							itemi[c].setForeground(getColorByState(bilvos[i].getStringViewValue(columnkey.get(c))));
						}
						items.add(itemi);
					}
					vo.setRowlength(items.size());
					vo.setCollength(columnname.size());
					BillCellItemVO[][] itemsa = (BillCellItemVO[][]) items.toArray(new BillCellItemVO[0][0]);
					vo.setCellItemVOs(itemsa);
					formatClen(itemsa);
					formatSpan(itemsa, new int[] { 1 });
					return vo;
				}
			}
		}
		vo.setRowlength(1);
		vo.setCollength(1);
		BillCellItemVO[][] itemsno = new BillCellItemVO[1][1];
		itemsno[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
		itemsno[0][0].setColwidth("300");
		vo.setCellItemVOs(itemsno);
		return vo;
	}

	/**
	 * 设置单元格的长度，根据字的长度算一把
	 * 
	 * @param items
	 */
	public void formatClen(BillCellItemVO[][] items) {
		int li_allowMaxColWidth = 375;
		FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
		for (int j = 0; j < items[0].length; j++) {
			int li_maxwidth = 0;
			String str_cellValue = null;
			for (int i = 0; i < items.length; i++) {
				str_cellValue = items[i][j].getCellvalue();
				if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
					int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
					if (li_width > li_maxwidth) {
						li_maxwidth = li_width;
					}
				}
			}
			li_maxwidth = li_maxwidth + 13;
			if (li_maxwidth > li_allowMaxColWidth) {
				li_maxwidth = li_allowMaxColWidth;
			}
			for (int i = 1; i < items.length; i++) {
				str_cellValue = items[i][j].getCellvalue();
				items[i][j].setColwidth("" + li_maxwidth);
			}
		}
	}

	/**
	 * 将单元格相同内容的合并
	 * 
	 * @param cellItemVOs
	 * @param _spanColumns
	 *            那几列需要处理
	 */
	public void formatSpan(BillCellItemVO[][] cellItemVOs, int[] _spanColumns) {
		if (_spanColumns != null) {
			HashMap temp = new HashMap();
			for (int i = 0; i < _spanColumns.length; i++) {
				int li_pos = _spanColumns[i];
				if (li_pos >= 0) {
					int li_spancount = 1;
					int li_spanbeginpos = 1;
					for (int k = 2; k < cellItemVOs.length; k++) {
						String str_value = cellItemVOs[k][li_pos].getCellvalue();
						// 合并的列颜色一样
						cellItemVOs[k][li_pos].setBackground("234,240,248");
						String str_value_front = cellItemVOs[k - 1][li_pos].getCellvalue();
						if (getTb().compareTwoString(str_value_front, str_value)) {
							if (i >= 1) {
								String str_value0 = cellItemVOs[k][_spanColumns[i - 1]].getCellvalue();
								String str_value_front0 = cellItemVOs[k - 1][_spanColumns[i - 1]].getCellvalue();
								if (getTb().compareTwoString(str_value0, str_value_front0)) {
									li_spancount++;
								} else {
									cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
									li_spancount = 1;
									li_spanbeginpos = k;
								}
							} else {
								li_spancount++;
							}

						} else {
							cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
							li_spancount = 1;
							li_spanbeginpos = k;
						}
					}
					cellItemVOs[li_spanbeginpos][li_pos].setSpan(li_spancount + ",1");
				}
			}
		}
	}

	/**
	 * 查询评分详情 根据考核日期、部门、指标、评分类型 排序
	 * 
	 * @param sql
	 * @param templet
	 * @return
	 */

	public BillVO[] getScoreDetail(String sql, Pub_Templet_1VO templetvo) throws Exception {
		// 这样没有2000的限制
		Pub_Templet_1ParVO pvo = templetvo.getParPub_Templet_1VO();
		pvo.setBsdatafilterclass("");
		pvo.setBsdatafilterissql(false);
		//
		BillVO[] vos = getMdmo().getBillVOsByDS(null, sql, pvo, true);
		// BillVO temp = null;
		// // 按照垂直R、垂直P、平行R、平行P排序
		// HashMap<String, Integer> map = new HashMap<String, Integer>();
		// map.put("垂直R", 0);
		// map.put("垂直P", 1);
		// map.put("平行R", 2);
		// map.put("平行P", 3);
		// String checkdatei = null;
		// String checkdeptnamei = null;
		// String targetcodei = null;
		// String checktypei = null;
		// String checkdatej = null;
		// String checkdeptnamej = null;
		// String targetcodej = null;
		// String checktypej = null;
		// List a = new ArrayList();
		// BillVO currvo = null;
		// for (int i = 0; i < vos.length; i++) {
		// if (vos[i].getStringValue("checktype", "").indexOf("平行") >= 0) {
		// vos[i].setObject("scoreuser", vos[i].getObject("scoredeptid"));
		// }
		// }
		// for (int i = 0; i < vos.length - 1; i++) {
		// for (int j = 0; j < vos.length - i - 1; j++) {
		// checkdatei = vos[j + 1].getStringValue("checkdate", " ");
		// checkdeptnamei = vos[j + 1].getStringValue("checkeddeptname", " ");
		// targetcodei = vos[j + 1].getStringValue("targetcode", " ");
		// checktypei = vos[j + 1].getStringValue("checktype", " ");
		// checkdatej = vos[j].getStringValue("checkdate", " ");
		// checkdeptnamej = vos[j].getStringValue("checkeddeptname", " ");
		// targetcodej = vos[j].getStringValue("targetcode", " ");
		// checktypej = vos[j].getStringValue("checktype", " ");
		// // if ((checkdatei + "_" + checkdeptnamei + "_" +
		// targetcodei).compareTo(checkdatej + "_" + checkdeptnamej + "_" +
		// targetcodej) > 0) {
		// // temp = vos[j];
		// // vos[j] = vos[j + 1];
		// // vos[j + 1] = temp;
		// // } else
		// if ((checkdatei + "_" + checkdeptnamei + "_" +
		// targetcodei).compareTo(checkdatej + "_" + checkdeptnamej + "_" +
		// targetcodej) == 0) {
		// String itype = vos[j + 1].getStringValue("checktype", "");
		// String jtype = vos[j].getStringValue("checktype", "");
		// if (map.containsKey(itype) && map.containsKey(jtype) &&
		// map.get(itype) < map.get(jtype)) {
		// temp = vos[j];
		// vos[j] = vos[j + 1];
		// vos[i] = temp;
		// } else if ("".equals(jtype)) {
		// temp = vos[j];
		// vos[j] = vos[j + 1];
		// vos[i] = temp;
		// }
		// }
		// }
		// }
		return vos;
	}

	public BillVO[] getScoreDetail(String sql, String templetcode) throws Exception {
		return getScoreDetail(sql, getMdmo().getPub_Templet_1VO(templetcode));
	}

	private String getColorByState(String statestr) {
		if (statestr == null || "".equals(statestr)) {
			return "191,213,255";
		} else if (statestr.equals("未评分")) {
			return "255,31,32";
		} else if (statestr.equals("已完成") || statestr.equals("已提交") || statestr.equals("评分完成") || statestr.equals("-")) {
			return "61,137,211";
		} else if (statestr.equals("评分中") || statestr.equals("待提交")) {
			return "1,164,97";
		} else if (statestr.equals("申请修改")) {
			return "191,0,255";
		}
		return "191,213,255";
	}

	public HashMap endPlan(String logid, boolean isquiet, String state) throws Exception {
		String[] unfinishDeptScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_dept_check_score where (status <> '已提交' or status is null) and TARGETTYPE='部门定性指标' and logid=" + logid);
		String[] unfinishPersonScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_person_check_score where (status <> '已提交' or status is null) and TARGETTYPE='员工定性指标' and logid=" + logid);
		LinkedHashMap<String, String> unfinishiuser = new LinkedHashMap<String, String>();
		boolean deptquantify = false;
		boolean personquantify = false;
		if (unfinishDeptScorer != null && unfinishDeptScorer.length > 0) {
			for (int i = 0; i < unfinishDeptScorer.length; i++) {
				if (unfinishDeptScorer[i] != null && !"".equals(unfinishDeptScorer[i])) {
					unfinishiuser.put(unfinishDeptScorer[i], "");
					if (unfinishDeptScorer[i].equals("-99999")) {
						deptquantify = true;
					}
				}
			}
		}
		if (unfinishPersonScorer != null && unfinishPersonScorer.length > 0) {
			for (int i = 0; i < unfinishPersonScorer.length; i++) {
				if (unfinishPersonScorer[i] != null && !"".equals(unfinishPersonScorer[i])) {
					unfinishiuser.put(unfinishPersonScorer[i], "");
					if (unfinishPersonScorer[i].equals("-99999")) {
						personquantify = true;
					}
				}
			}
		}
		HashMap rtn_map = new HashMap();
		if (unfinishiuser.size() > 0) {
			rtn_map.put("res", "fail");
			if (!isquiet) { // 需要有提醒 返回一个billcellvo 即什么部门谁没有完成评分
				HashVO[] users = getDmo().getHashVoArrayByDS(null, "select id, name from pub_user where id in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				ReportUtil rutil = new ReportUtil();
				rutil.allOneFieldNameFromOtherTable(users, "deptname", "id", "select p.userid, u.name from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "deptseq", "id", "select p.userid, u.seq from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "corptype", "id", "select p.userid, u.corptype from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				getTb().sortHashVOs(users, new String[][] { { "deptseq", "N", "Y" } });
				BillCellVO vo = new BillCellVO();
				vo.setCollength(2);
				List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
				BillCellItemVO[] _items0 = new BillCellItemVO[2];
				_items0[0] = getBillTitleCellItemVO("未完成评分人员");
				_items0[0].setSpan("1,2");
				_items0[1] = getBillTitleCellItemVO("");
				items.add(_items0);
				for (int i = 0; i < users.length; i++) {
					BillCellItemVO[] _items = new BillCellItemVO[2];
					_items[0] = getBillNormalCellItemVO(i, users[i].getStringValue("deptname"));
					_items[1] = getBillNormalCellItemVO(i, users[i].getStringValue("name"));
					items.add(_items);
				}
				if (deptquantify) {
					BillCellItemVO[] _items = new BillCellItemVO[2];
					_items[0] = getBillNormalCellItemVO(0, "部门定量考核");
					_items[1] = getBillNormalCellItemVO(0, "部门定量考核");
					items.add(_items);
				}
				if (personquantify) {
					BillCellItemVO[] _items = new BillCellItemVO[2];
					_items[0] = getBillNormalCellItemVO(0, "员工定量考核");
					_items[1] = getBillNormalCellItemVO(0, "员工定量考核");
					items.add(_items);
				}
				vo.setRowlength(items.size());
				BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
				formatClen(allitems);
				formatSpan(allitems, new int[1]);
				vo.setCellItemVOs(allitems);
				rtn_map.put("vo", vo);
			}
		} else {
			rtn_map.put("res", "success");
			// getScoreVOByID(logid, true, true, false, null, false, true);
			// getScoreVO_Person(logid, null, true, false, false);
			getDmo().executeUpdateByDS(null, "update sal_target_check_log set status='考核结束' where id=" + logid);
		}
		return rtn_map;
	}

	public boolean endPlanDL_Dept(String logid) throws Exception {
		String[] unfinishDeptScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_dept_check_score where (status <> '已提交' or status is null) and targettype='部门定量指标' and scoreuser='-99999' and logid=" + logid);
		if (unfinishDeptScorer != null && unfinishDeptScorer.length > 0) {
			return false;
		} else {
			getScoreVOByID(logid, false, true, false, null, false, false);
			return true;
		}
	}

	/*
	 * 部门定性
	 */
	public HashMap endCalcDeptDXScore(String logid, boolean isquiet, String state) throws Exception {
		HashVO[] planvos = getDmo().getHashVoArrayByDS(null, "select *from sal_target_check_log where id=" + logid);
		if (planvos.length == 0) {
			return null;
		}
		String[] unfinishDeptScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_dept_check_score where (status <> '已提交' or status is null) and logid=" + logid);
		LinkedHashMap<String, String> unfinishiuser = new LinkedHashMap<String, String>();
		if (unfinishDeptScorer != null && unfinishDeptScorer.length > 0) {
			for (int i = 0; i < unfinishDeptScorer.length; i++) {
				if (unfinishDeptScorer[i] != null && !"".equals(unfinishDeptScorer[i]) && !"-99999".equals(unfinishDeptScorer[i])) {
					unfinishiuser.put(unfinishDeptScorer[i], "");
				}
			}
		}
		HashMap rtn_map = new HashMap();
		if (unfinishiuser.size() > 0) {
			rtn_map.put("res", "fail");
			if (!isquiet) { // 需要有提醒 返回一个billcellvo 即什么部门谁没有完成评分
				HashVO[] users = getDmo().getHashVoArrayByDS(null, "select id, name from pub_user where id in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				ReportUtil rutil = new ReportUtil();
				rutil.allOneFieldNameFromOtherTable(users, "deptname", "id", "select p.userid, u.name from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "deptseq", "id", "select p.userid, u.seq from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "corptype", "id", "select p.userid, u.corptype from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				getTb().sortHashVOs(users, new String[][] { { "deptseq", "N", "Y" } });
				BillCellVO vo = new BillCellVO();
				vo.setCollength(2);
				List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
				BillCellItemVO[] _items0 = new BillCellItemVO[2];
				_items0[0] = getBillTitleCellItemVO("未完成评分人员");
				_items0[0].setSpan("1,2");
				_items0[1] = getBillTitleCellItemVO("");
				items.add(_items0);
				for (int i = 0; i < users.length; i++) {
					BillCellItemVO[] _items = new BillCellItemVO[2];
					_items[0] = getBillNormalCellItemVO(i, users[i].getStringValue("deptname"));
					_items[1] = getBillNormalCellItemVO(i, users[i].getStringValue("name"));
					items.add(_items);
				}
				vo.setRowlength(items.size());
				BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
				formatClen(allitems);
				formatSpan(allitems, new int[1]);
				vo.setCellItemVOs(allitems);
				rtn_map.put("vo", vo);
			}
		} else {
			SalaryFormulaDMO.setRemoteActionSchedule(logid, "指标计算", "正在计算部门定性、定量得分");
			getScoreVOByID(logid, true, false, true, null, true, true); // 计算部门定性考核得分
			getDmo().executeUpdateByDS(null, "update sal_target_check_log set status='" + state + "' where id=" + logid);
			rtn_map.put("res", "success");
			SalaryFormulaDMO.removeRemoteActionSchedule(logid, "指标计算");
		}
		return rtn_map;
	}

	/*
	 * 员工定性计算
	 */
	public HashMap endCalcPersonDXScore(String logid, boolean isquiet, String state) throws Exception {
		String[] unfinishPersonScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_person_check_score where (status <> '已提交' or status is null) and TARGETTYPE='员工定性指标' and logid=" + logid );
		LinkedHashMap<String, String> unfinishiuser = new LinkedHashMap<String, String>();
		if (unfinishPersonScorer != null && unfinishPersonScorer.length > 0) {
			for (int i = 0; i < unfinishPersonScorer.length; i++) {
				if (unfinishPersonScorer[i] != null && !"".equals(unfinishPersonScorer[i]) && !"-99999".equals(unfinishPersonScorer[i])) {
					unfinishiuser.put(unfinishPersonScorer[i], "");
				}
			}
		}

		HashMap rtn_map = new HashMap();
		if (unfinishiuser.size() > 0) {
			rtn_map.put("res", "fail");
			if (!isquiet) { // 需要有提醒 返回一个billcellvo 即什么部门谁没有完成评分
				HashVO[] users = getDmo().getHashVoArrayByDS(null, "select id, name from pub_user where id in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				ReportUtil rutil = new ReportUtil();
				rutil.allOneFieldNameFromOtherTable(users, "deptname", "id", "select p.userid, u.name from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "deptseq", "id", "select p.userid, u.seq from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "corptype", "id", "select p.userid, u.corptype from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				getTb().sortHashVOs(users, new String[][] { { "deptseq", "N", "Y" } });
				BillCellVO vo = new BillCellVO();
				vo.setCollength(2);
				List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
				BillCellItemVO[] _items0 = new BillCellItemVO[2];
				_items0[0] = getBillTitleCellItemVO("未完成评分人员");
				_items0[0].setSpan("1,2");
				_items0[1] = getBillTitleCellItemVO("");
				items.add(_items0);
				for (int i = 0; i < users.length; i++) {
					BillCellItemVO[] _items = new BillCellItemVO[2];
					_items[0] = getBillNormalCellItemVO(i, users[i].getStringValue("deptname"));
					_items[1] = getBillNormalCellItemVO(i, users[i].getStringValue("name"));
					items.add(_items);
				}
				vo.setRowlength(items.size());
				BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
				formatClen(allitems);
				formatSpan(allitems, new int[1]);
				vo.setCellItemVOs(allitems);
				rtn_map.put("vo", vo);
			}
		} else {
			rtn_map.put("res", "success");
			getScoreVO_Person(logid, null, true, false, false, false, false);
			getDmo().executeUpdateByDS(null, "update sal_target_check_log set status='" + state + "' where id=" + logid);
			rtn_map.put("res", "success");
			SalaryFormulaDMO.removeRemoteActionSchedule(logid, "指标计算");
		}
		return rtn_map;
	}

	public HashMap endPlanDx(String logid, boolean isquiet, String state) throws Exception {
		String[] unfinishDeptScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_dept_check_score where (status <> '已提交' or status is null) and logid=" + logid);
		String[] unfinishPersonScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_person_check_score where (status <> '已提交' or status is null) and logid=" + logid);
		LinkedHashMap<String, String> unfinishiuser = new LinkedHashMap<String, String>();
		if (unfinishDeptScorer != null && unfinishDeptScorer.length > 0) {
			for (int i = 0; i < unfinishDeptScorer.length; i++) {
				if (unfinishDeptScorer[i] != null && !"".equals(unfinishDeptScorer[i]) && !"-99999".equals(unfinishDeptScorer[i])) {
					unfinishiuser.put(unfinishDeptScorer[i], "");
				}
			}
		}
		if (unfinishPersonScorer != null && unfinishPersonScorer.length > 0) {
			for (int i = 0; i < unfinishPersonScorer.length; i++) {
				if (unfinishPersonScorer[i] != null && !"".equals(unfinishPersonScorer[i]) && !"-99999".equals(unfinishPersonScorer[i])) {
					unfinishiuser.put(unfinishPersonScorer[i], "");
				}
			}
		}
		HashMap rtn_map = new HashMap();
		if (unfinishiuser.size() > 0) {
			rtn_map.put("res", "fail");
			if (!isquiet) { // 需要有提醒 返回一个billcellvo 即什么部门谁没有完成评分
				HashVO[] users = getDmo().getHashVoArrayByDS(null, "select id, name from pub_user where id in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				ReportUtil rutil = new ReportUtil();
				rutil.allOneFieldNameFromOtherTable(users, "deptname", "id", "select p.userid, u.name from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "deptseq", "id", "select p.userid, u.seq from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				rutil.allOneFieldNameFromOtherTable(users, "corptype", "id", "select p.userid, u.corptype from pub_user_post p left join pub_corp_dept u on u.id=p.userdept where p.userid is not null and u.name is not null and p.isdefault='Y' and p.userid in (" + getTb().getInCondition((String[]) unfinishiuser.keySet().toArray(new String[0])) + ")");
				getTb().sortHashVOs(users, new String[][] { { "deptseq", "N", "Y" } });
				BillCellVO vo = new BillCellVO();
				vo.setCollength(2);
				List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
				BillCellItemVO[] _items0 = new BillCellItemVO[2];
				_items0[0] = getBillTitleCellItemVO("未完成评分人员");
				_items0[0].setSpan("1,2");
				_items0[1] = getBillTitleCellItemVO("");
				items.add(_items0);
				for (int i = 0; i < users.length; i++) {
					BillCellItemVO[] _items = new BillCellItemVO[2];
					_items[0] = getBillNormalCellItemVO(i, users[i].getStringValue("deptname"));
					_items[1] = getBillNormalCellItemVO(i, users[i].getStringValue("name"));
					items.add(_items);
				}
				vo.setRowlength(items.size());
				BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
				formatClen(allitems);
				formatSpan(allitems, new int[1]);
				vo.setCellItemVOs(allitems);
				rtn_map.put("vo", vo);
			}
		} else {
			rtn_map.put("res", "success");
			getScoreVOByID(logid, true, false, true, null, true, true);
			getScoreVO_Person(logid, null, true, false, false, false, false);
			getDmo().executeUpdateByDS(null, "update sal_target_check_log set status='" + state + "' where id=" + logid);
		}
		return rtn_map;

	}

	/**
	 * 部门的得分比、最高得分比、平均得分比、排名
	 * 
	 * @param checkdate
	 * @return
	 */
	public BillCellVO getDeptCheckResVO(String checkdate, String checkedDeptid) throws Exception {
		String id = getDmo().getStringValueByDS(null, "select id,name from sal_target_check_log where checkdate='" + checkdate + "'");
		return getScoreVOByID(id, false, false, true, checkedDeptid, true, false);
	}

	/**
	 * 员工定性考核指标每个指标的得分
	 * 
	 * @param logid
	 * @param checkeduserid
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getPersonTargetCheckResVO(String logid, String checkeduserid) throws Exception {
		BillCellVO vo = new BillCellVO();
		StringBuffer sb = new StringBuffer("select * from sal_person_check_score where logid=" + logid + " and checkeduser=" + checkeduserid + " and targettype='员工定性指标' and scoretype='手动打分' ");
		HashVO[] allscores = getDmo().getHashVoArrayByDS(null, sb.toString()); // 所有打分记录
		List allscorers = new ArrayList(); // 所有的评分人
		// 指标-组-评分人记录vo
		LinkedHashMap<String, HashMap<String, List<HashVO>>> nbmap = new LinkedHashMap<String, HashMap<String, List<HashVO>>>();
		HashMap<String, String> targetid_name = new HashMap<String, String>(); // 指标ID——name
		HashMap<String, String> targetid_weight = new HashMap<String, String>(); // 指标ID——权重
		HashMap<String, String> group_weightstype = new HashMap<String, String>(); // 组-组内权重方式
		HashMap<String, String> group_groupweight = new HashMap<String, String>(); // 组-组的权重
		HashMap<String, String> target_weightstype = new HashMap<String, String>(); // 指标-组间权重方式
		String groupid, scorer, groupweightstyle, groupweight, userweightstyle, targetid, targetname = null;
		boolean isok = true;
		for (int i = 0; i < allscores.length; i++) {
			if (!"已提交".equals(allscores[i].getStringValue("status", ""))) {
				isok = false;
			}
			groupid = allscores[i].getStringValue("scoreusertype", ""); // 评分组
			scorer = allscores[i].getStringValue("scoreuser", ""); // 评分人
			allscorers.add(scorer);
			groupweightstyle = allscores[i].getStringValue("groupweightstype", ""); // 组内人员的权重计算方式
			groupweight = allscores[i].getStringValue("groupweights", ""); // 评分组的权重
			userweightstyle = allscores[i].getStringValue("scorerweightstype", ""); // 评分组之间的权重方式
			targetid = allscores[i].getStringValue("targetid", "");
			targetname = allscores[i].getStringValue("targetname", "");
			targetid_name.put(targetid, targetname);
			targetid_weight.put(targetid, allscores[i].getStringValue("weights", ""));
			group_weightstype.put(groupid, groupweightstyle);
			group_groupweight.put(groupid, groupweight);
			target_weightstype.put(targetid, userweightstyle);
			if (nbmap.containsKey(targetid)) {
				HashMap<String, List<HashVO>> group_vo = nbmap.get(targetid);
				if (group_vo.containsKey(groupid)) {
					group_vo.get(groupid).add(allscores[i]);
				} else {
					List<HashVO> vos = new ArrayList<HashVO>();
					vos.add(allscores[i]);
					group_vo.put(groupid, vos);
				}
			} else {
				HashMap<String, List<HashVO>> group_vo = new HashMap<String, List<HashVO>>();
				List<HashVO> vos = new ArrayList<HashVO>();
				vos.add(allscores[i]);
				group_vo.put(groupid, vos);
				nbmap.put(targetid, group_vo);
			}
		}
		HashMap user_level = getDmo().getHashMapBySQLByDS(null, "select id, stationratio from v_sal_personinfo where stationratio is not null and id in (" + getTb().getInCondition(allscorers) + ")");
		HashMap error = getDmo().getHashMapBySQLByDS(null, "select u.name, '1' from pub_user u left join sal_personinfo p on u.id=p.id where (p.stationratio is null or p.stationratio='') and u.id in (" + getTb().getInCondition(allscorers) + ")");
		// 这里要判断是否所有评分人都设置了岗位系数
		if (error != null && error.size() > 0) {
			String[] names = (String[]) error.keySet().toArray(new String[0]);
			StringBuffer sb_ = new StringBuffer();
			for (int n = 0; n < names.length; n++) {
				if (names[n] != null && !"".equals(names[n])) {
					if (sb_.length() <= 0) {
						sb_.append(names[n]);
					} else {
						sb_.append("、" + names[n]);
					}
				}
			}
			throw new WLTAppException("评分人:" + sb_.toString() + "未设置岗位系数,无法计算得分!");
		}
		if (nbmap.size() > 0) {
			List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
			List<BillCellItemVO> item0 = new ArrayList<BillCellItemVO>();
			item0.add(getBillTitleCellItemVO("指标名称"));
			// item0.add(getBillTitleCellItemVO("权重"));
			item0.add(getBillTitleCellItemVO("得分"));
			list.add(item0.toArray(new BillCellItemVO[0]));
			String[] targetids = nbmap.keySet().toArray(new String[0]);
			for (int j = 0; j < nbmap.size(); j++) {
				List<BillCellItemVO> itemj = new ArrayList<BillCellItemVO>();
				itemj.add(getBillNormalCellItemVO(list.size(), targetid_name.get(targetids[j])));
				// itemj.add(getBillNormalCellItemVO(list.size(),
				// targetid_weight.get(targetids[j])));
				if (isok) {
					HashMap<String, List<HashVO>> group_vos = nbmap.get(targetids[j]);
					String targetweightstype = target_weightstype.get(targetids[j]);
					String[] grups = group_vos.keySet().toArray(new String[0]);
					BigDecimal groupsumweight = new BigDecimal("0");
					BigDecimal groupsumscore = new BigDecimal("0");
					for (int g = 0; g < grups.length; g++) {
						BigDecimal group_weight = new BigDecimal(group_groupweight.get(grups[g]));
						List<HashVO> scorevos = group_vos.get(grups[g]);
						String scorerweightstype = group_weightstype.get(grups[g]);
						BigDecimal usersumweight = new BigDecimal("0");
						BigDecimal usersumscore = new BigDecimal("0");
						for (int s = 0; s < scorevos.size(); s++) {
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
							groupsumweight = groupsumweight.add(new BigDecimal(group_groupweight.get(grups[g])));
							groupsumscore = groupsumscore.add(usersumscore.divide(usersumweight, 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(group_groupweight.get(grups[g]))));
						}
					}
					itemj.add(getBillNormalCellItemVO(list.size(), groupsumscore.divide(groupsumweight, 2, BigDecimal.ROUND_HALF_UP).toString()));
				} else {
					itemj.add(getBillNormalCellItemVO(list.size(), ""));
				}
				list.add(itemj.toArray(new BillCellItemVO[0]));
			}
			vo.setCollength(list.get(0).length);
			vo.setRowlength(list.size());
			vo.setCellItemVOs(list.toArray(new BillCellItemVO[0][0]));
		} else {
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
			items[0][0].setColwidth("300");
			vo.setCellItemVOs(items);
		}
		return vo;
	}

	/**
	 * 部门定性指标得分比
	 * 
	 * @param checkdate
	 * @param checkedDeptid
	 * @return
	 * @throws Exception
	 */
	public BillCellVO getTargetCheckResVO(String checkdate, String checkedDeptid) throws Exception {
		BillCellVO vo = new BillCellVO();
		StringBuffer sql = new StringBuffer("select id,name from sal_target_check_log where 1=1 ");
		if (checkdate != null && !"".equals(checkdate)) {
			sql.append(" and (checkdate='" + checkdate + "' )");
		}
		sql.append(" order by checkdate desc ");
		BigDecimal[] allrights = getDeptRight();
		BigDecimal c = allrights[0];
		BigDecimal p = allrights[1];
		BigDecimal cr = allrights[2];
		BigDecimal cp = allrights[3];
		BigDecimal pr = allrights[4];
		BigDecimal pp = allrights[5];
		List<String> allscore = new ArrayList<String>();
		String[][] logidandname = getDmo().getStringArrayByDS(null, sql.toString());
		if (logidandname != null && logidandname.length > 0) {
			String logid = null;
			List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
			logid = logidandname[0][0];
			StringBuffer sb = new StringBuffer("select * from sal_dept_check_score where logid='" + logid + "' ");
			if (checkedDeptid != null) {
				sb.append(" and checkeddept='" + checkedDeptid + "' "); // and
				// targettype='部门定性指标'
			}
			sb.append(" order by targettype, targetcode ");
			HashVO[] allscores = getDmo().getHashVoArrayByDS(null, sb.toString());
			if (allscores != null && allscores.length > 0) {
				LinkedHashMap[] maps = getAllTempMap(allscores);
				LinkedHashMap<String, Integer> dept_count = maps[0];
				LinkedHashMap dept_score = maps[3];
				LinkedHashMap target_weight = maps[4];
				LinkedHashMap dept_target_state = maps[5];
				LinkedHashMap targetid_name = maps[8];
				HashMap targetid_type = getDmo().getHashMapBySQLByDS(null, " select targetid, targettype from sal_dept_check_score where targetid in (" + getTb().getInCondition((String[]) targetid_name.keySet().toArray(new String[0])) + ") and logid='" + logid + "' ");
				String[] alldeptids = (String[]) dept_count.keySet().toArray(new String[0]);
				HashMap<String, String> deptid_name = getDmo().getHashMapBySQLByDS(null, "select id,name from pub_corp_dept where id in (" + getTb().getInCondition(alldeptids) + ")");
				List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
				items0.add(getBillTitleCellItemVO(logidandname[0][1]));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				// items0.add(getBillNormalCellItemVO(0, ""));//郝明注释掉 20130914
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.get(0).setSpan("1," + items0.size());
				list.add(items0.toArray(new BillCellItemVO[0]));
				List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
				items1.add(getBillTitleCellItemVO("被考核部门"));
				items1.add(getBillTitleCellItemVO("被考核指标"));
				items1.add(getBillTitleCellItemVO("指标类型"));
				items1.add(getBillTitleCellItemVO("状态"));
				// items1.add(getBillTitleCellItemVO("得分")); //郝明注释掉 20130914
				items1.add(getBillTitleCellItemVO("得分")); // 百分比
				list.add(items1.toArray(new BillCellItemVO[0]));
				for (int i = 0; i < alldeptids.length; i++) {
					if (dept_target_state.containsKey(alldeptids[i])) {
						HashMap target_score = (HashMap) dept_score.get(alldeptids[i]); // 这个部门的所有指标
						HashMap targetstate = (HashMap) dept_target_state.get(alldeptids[i]);
						String[] targetids = (String[]) targetstate.keySet().toArray(new String[0]);
						for (int gg = 0; gg < targetids.length; gg++) {
							List<BillCellItemVO> itemsi = new ArrayList<BillCellItemVO>();
							itemsi.add(getBillNormalCellItemVO(list.size(), deptid_name.get(alldeptids[i]) + ""));
							itemsi.add(getBillNormalCellItemVO(list.size(), targetid_name.get(targetids[gg]) + ""));
							itemsi.add(getBillNormalCellItemVO(list.size(), targetid_type.get(targetids[gg]) + ""));
							itemsi.add(getBillNormalCellItemVO(list.size(), targetstate.get(targetids[gg]) + ""));
							itemsi.get(itemsi.size() - 1).setForeground(getColorByState(targetstate.get(targetids[gg]) + ""));
							if ("评分完成".equals(targetstate.get(targetids[gg]) + "")) {
								HashMap type_score = (HashMap) target_score.get(targetids[gg]); // 这个部门的这个指标
								// 有哪几类
								// 垂直R、垂直P、平行R、平行P
								if (type_score.containsKey("") || type_score.containsKey(null)) { // 为空的就是定量指标了
									// continue;
									// 部门得分明细增加定量指标的明细
									List score = (List) type_score.get("");
									if (score == null || score.size() < 1) {
										score = (List) type_score.get(null);
									}
									BigDecimal finalres = new BigDecimal(score.get(0) + "");
									BigDecimal WE = new BigDecimal(target_weight.get(targetids[gg]) + "");
									if ("0".equals(target_weight.get(targetids[gg]) + "") || "".equals(target_weight.get(targetids[gg]) + "")) {
										itemsi.add(getBillNormalCellItemVO(list.size(), "0%"));
									} else {
										itemsi.add(getBillNormalCellItemVO(list.size(), finalres.multiply(new BigDecimal("100")).divide(WE, 2, BigDecimal.ROUND_HALF_UP).toString() + "%"));
									}
								} else {
									BigDecimal right = new BigDecimal(0);
									BigDecimal CR = new BigDecimal(0);
									BigDecimal CP = new BigDecimal(0);
									BigDecimal PR = new BigDecimal(0);
									BigDecimal PP = new BigDecimal(0);
									if (type_score.containsKey("垂直R")) {
										List score = (List) type_score.get("垂直R"); // 这个部门的这个指标
										// 垂直R的所有打分明细
										if (score.size() > 0) {
											right = right.add(c.multiply(cr)); // 权重=垂直R的权重*垂直的权重
											CR = getScore(score, c.multiply(cr));// 根据模拟计算
											// 某一类打分比如垂直R的所有得分算平均
											// *
											// （垂直R的权重*垂直的权重）
										}
									}
									if (type_score.containsKey("垂直P")) {
										List score = (List) type_score.get("垂直P");
										if (score.size() > 0) {
											right = right.add(c.multiply(cp));
											CP = getScore(score, c.multiply(cp));
										}
									}
									if (type_score.containsKey("平行R")) {
										List score = (List) type_score.get("平行R");
										if (score.size() > 0) {
											right = right.add(p.multiply(pr));
											PR = getScore(score, p.multiply(pr));
										}
									}
									if (type_score.containsKey("平行P")) {
										List score = (List) type_score.get("平行P");
										if (score.size() > 0) {
											right = right.add(p.multiply(pp));
											PP = getScore(score, p.multiply(pp));
										}
									}
									BigDecimal finalresc = CR.add(CP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
									BigDecimal finalresp = PR.add(PP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
									BigDecimal finalres = finalresc.add(finalresp).setScale(2, BigDecimal.ROUND_HALF_UP);
									// itemsi.add(3,
									// getBillNormalCellItemVO(list.size(),
									// finalres.toString())); //郝明注释掉 20130914
									if ("0".equals(target_weight.get(targetids[gg]) + "") || "".equals(target_weight.get(targetids[gg]) + "")) {
										itemsi.add(getBillNormalCellItemVO(list.size(), "0%"));
									} else {
										itemsi.add(getBillNormalCellItemVO(list.size(), finalres.multiply(new BigDecimal("100")).divide(new BigDecimal(target_weight.get(targetids[gg]) + ""), 2, BigDecimal.ROUND_HALF_UP).toString() + "%"));
									}
								}
							} else {
								// itemsi.add(3,
								// getBillNormalCellItemVO(list.size(), ""));
								// //郝明注释掉 20130914
								itemsi.add(getBillNormalCellItemVO(list.size(), ""));
							}
							list.add(itemsi.toArray(new BillCellItemVO[0]));
						}
					}
				}
				vo.setCollength(list.get(0).length);
				vo.setRowlength(list.size());
				BillCellItemVO[][] items = (BillCellItemVO[][]) list.toArray(new BillCellItemVO[0][0]);
				int li_allowMaxColWidth = 375;
				FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
				for (int j = 0; j < items[0].length; j++) {
					int li_maxwidth = 0;
					String str_cellValue = null;
					for (int i = 0; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
							int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
							if (li_width > li_maxwidth) {
								li_maxwidth = li_width;
							}
						}
					}
					li_maxwidth = li_maxwidth + 13;
					if (li_maxwidth > li_allowMaxColWidth) {
						li_maxwidth = li_allowMaxColWidth;
					}
					for (int i = 1; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						items[i][j].setColwidth("" + li_maxwidth);
					}
				}
				vo.setCellItemVOs(items);
				formatSpan(items, new int[] { 0 });
				return vo;
			}
		}
		vo.setRowlength(1);
		vo.setCollength(1);
		BillCellItemVO[][] items = new BillCellItemVO[1][1];
		items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
		items[0][0].setColwidth("300");
		vo.setCellItemVOs(items);
		return vo;
	}

	/**
	 * 高管分管指标的结果
	 * 
	 * @return
	 */
	public BillCellVO getPostTargetCheckResVO(String checkdate, String checkeduserid) throws Exception {
		return getPostTargetCheckResVO(checkdate, checkeduserid, false);
	}

	// 是否显示得分,用作一个参数,防止以后用.郝明2013-10-12
	public BillCellVO getPostTargetCheckResVO(String checkdate, String checkeduserid, boolean isshowscore) throws Exception {
		BillCellVO cell = new BillCellVO();
		String logid = null;
		StringBuffer sql = new StringBuffer("select id,name from sal_target_check_log where 1=1 ");
		if (checkdate != null && !"".equals(checkdate)) {
			sql.append(" and (checkdate='" + checkdate + "' )");
		}
		String[][] logidandname = getDmo().getStringArrayByDS(null, sql.toString());
		if (logidandname != null && logidandname.length > 0) {
			// 先找我已垂直R身份要给那些指标打分
			logid = logidandname[0][0];
			String scoretype = getDmo().getStringValueByDS(null, "select scoretype from sal_person_check_score where logid=" + logid + " and checkeduser=" + checkeduserid + " and scoretype<>'手动打分' ");

			HashVO[] allscorevo = null;
			List<HashVO> allscorevos = new ArrayList<HashVO>();
			if ("分管部门得分".equals(scoretype) || "分管部门定性得分".equals(scoretype) || "分管部门总分".equals(scoretype)) {
				String[] alltargetids = getDmo().getStringArrayFirstColByDS(null, "select targetid  from sal_dept_check_score where checktype='垂直R' and scoreuser=" + checkeduserid + " and logid=" + logid);
				// 然后找这些指标的打分记录当然要去除自己的打分记录
				HashVO[] allscorevo_ = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where checktype<>'垂直R' and targetid in (" + getTb().getInCondition(alltargetids) + ") and logid=" + logid);
				for (int i = 0; i < allscorevo_.length; i++) {
					allscorevos.add(allscorevo_[i]);
				}
			}
			if ("分管部门定量得分".equals(scoretype) || "分管部门总分".equals(scoretype)) {
				String[] stations = getDmo().getStringArrayFirstColByDS(null, "select postid from pub_user_post where userid=" + checkeduserid); // 不是主岗也有可能
				StringBuffer sb = new StringBuffer("(1=2 ");
				for (int i = 0; i < stations.length; i++) {
					sb.append(" or rleader like '%;" + stations[i] + ";%' ");
				}
				sb.append(")");
				// 获取被考核人的岗位 找到 定量指标
				HashMap p = new HashMap();
				HashVO[] allscorevo_1 = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString() + ") and (pleader is not null and pleader <> '') and state='参与考核') and logid=" + logid);
				for (int i = 0; i < allscorevo_1.length; i++) {
					allscorevo_1[i].setAttributeValue("main", "0.6"); // 是主管领导
					allscorevos.add(allscorevo_1[i]);
					p.put(allscorevo_1[i].getStringValue("id"), "");
				}
				HashVO[] allscorevo_1_ = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString() + ") and (pleader is null or pleader='') and state='参与考核') and logid=" + logid);
				for (int i = 0; i < allscorevo_1_.length; i++) {
					allscorevo_1_[i].setAttributeValue("main", "1"); // 是主管领导
					allscorevos.add(allscorevo_1_[i]);
					p.put(allscorevo_1_[i].getStringValue("id"), "");
				}
				HashVO[] allscorevo_2 = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString().replaceAll("rleader", "pleader") + ") and (rleader is not null and rleader <> '')  and state='参与考核') and logid=" + logid);
				for (int i = 0; i < allscorevo_2.length; i++) {
					if (!p.containsKey(allscorevo_2[i].getStringValue("id"))) {
						allscorevo_2[i].setAttributeValue("main", "0.4"); // 是分管领导
						allscorevos.add(allscorevo_2[i]);
					}
				}
				HashVO[] allscorevo_2_ = getDmo().getHashVoArrayByDS(null, "select * from sal_dept_check_score where  targetid in (select id from sal_target_list where type='部门定量指标' and ( " + sb.toString().replaceAll("rleader", "pleader") + ") and (rleader is null or rleader='')  and state='参与考核') and logid=" + logid);
				for (int i = 0; i < allscorevo_2_.length; i++) {
					if (!p.containsKey(allscorevo_2_[i].getStringValue("id"))) {
						allscorevo_2_[i].setAttributeValue("main", "1"); // 是分管领导
						allscorevos.add(allscorevo_2_[i]);
					}
				}
			}
			List alldetails = new ArrayList();
			allscorevo = allscorevos.toArray(new HashVO[0]);
			// HashVO[] allscorevo = getDmo().getHashVoArrayByDS(null,
			// "select * from sal_dept_check_score where checktype<>'垂直R' and
			// targetid in ("
			// + getTb().getInCondition(alltargetids) + ") and logid=" + logid +
			// " order by targetcode ");
			if (allscorevo != null && allscorevo.length > 0) {
				List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
				List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
				items0.add(getBillTitleCellItemVO(logidandname[0][1]));
				items0.add(getBillNormalCellItemVO(list.size(), ""));
				items0.add(getBillNormalCellItemVO(list.size(), ""));
				if (isshowscore) {
					items0.add(getBillNormalCellItemVO(list.size(), ""));
				}
				items0.add(getBillNormalCellItemVO(list.size(), ""));
				items0.get(0).setSpan("1," + items0.size());
				list.add(items0.toArray(new BillCellItemVO[0]));
				List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
				items1.add(getBillTitleCellItemVO("指标编码"));
				items1.add(getBillTitleCellItemVO("指标名称"));
				items1.add(getBillTitleCellItemVO("状态"));
				if (isshowscore) {
					items1.add(getBillTitleCellItemVO("得分"));
				}
				items1.add(getBillTitleCellItemVO("得分比"));
				list.add(items1.toArray(new BillCellItemVO[0]));
				LinkedHashMap<String, HashMap<String, List<HashVO>>> target_type = new LinkedHashMap<String, HashMap<String, List<HashVO>>>();
				HashMap<String, String> target_state = new HashMap<String, String>();
				HashMap<String, String> target_name = new HashMap<String, String>();
				HashMap<String, String> target_code = new HashMap<String, String>();
				for (int i = 0; i < allscorevo.length; i++) {
					getState(target_state, allscorevo[i].getStringValue("targetid"), allscorevo[i].getStringValue("status", ""));
					target_name.put(allscorevo[i].getStringValue("targetid"), allscorevo[i].getStringValue("targetname", ""));
					target_code.put(allscorevo[i].getStringValue("targetid"), allscorevo[i].getStringValue("targetcode", ""));
					if (target_type.containsKey(allscorevo[i].getStringValue("targetid", ""))) {
						if (target_type.get(allscorevo[i].getStringValue("targetid", "")).containsKey(allscorevo[i].getStringValue("checktype", ""))) {
							List<HashVO> scores = target_type.get(allscorevo[i].getStringValue("targetid", "")).get(allscorevo[i].getStringValue("checktype", ""));
							scores.add(allscorevo[i]);
						} else {
							List<HashVO> scores = new ArrayList<HashVO>();
							scores.add(allscorevo[i]);
							target_type.get(allscorevo[i].getStringValue("targetid", "")).put(allscorevo[i].getStringValue("checktype", ""), scores);
						}
					} else {
						HashMap<String, List<HashVO>> type_score = new HashMap<String, List<HashVO>>(); // 指标-打分记录VO
						List<HashVO> scores = new ArrayList<HashVO>();
						scores.add(allscorevo[i]);
						type_score.put(allscorevo[i].getStringValue("checktype", ""), scores);
						target_type.put(allscorevo[i].getStringValue("targetid", ""), type_score);
					}
				}
				BigDecimal[] allrights = getDeptRight();
				BigDecimal c = allrights[0];
				BigDecimal p = allrights[1];
				BigDecimal cr = allrights[2];
				BigDecimal cp = allrights[3];
				BigDecimal pr = allrights[4];
				BigDecimal pp = allrights[5];
				String[] targets = target_type.keySet().toArray(new String[0]);
				// BigDecimal allscoreweight = new BigDecimal(0); // 得分加权的和
				// BigDecimal allscoreweight_ = new BigDecimal(0); // 满分得分加权的和
				// BigDecimal allweight = new BigDecimal(0); // 权重和
				for (int j = 0; j < targets.length; j++) {
					List<BillCellItemVO> itemsi = new ArrayList<BillCellItemVO>();
					itemsi.add(0, getBillNormalCellItemVO(list.size(), target_code.get(targets[j]) + ""));
					itemsi.add(1, getBillNormalCellItemVO(list.size(), target_name.get(targets[j]) + ""));
					itemsi.add(2, getBillNormalCellItemVO(list.size(), target_state.get(targets[j]) + ""));
					itemsi.get(2).setForeground(getColorByState(target_state.get(targets[j]) + ""));
					if ("评分完成".equals(target_state.get(targets[j]))) {
						HashMap<String, List<HashVO>> type_score = target_type.get(targets[j]); // 这个部门的这个指标
						// 有哪几类
						// 垂直R、垂直P、平行R、平行P
						if (type_score.containsKey("") || type_score.containsKey(null)) { // 为空的就是定量指标了
							// 有定量指标肯定是分管部门定量得分或总得分
							List<HashVO> score = type_score.get("");
							if (score == null || score.size() < 1) {
								score = type_score.get(null);
							}
							if (score != null && score.size() > 0) {
								if (!"".equals(score.get(0).getStringValue("main", ""))) {
									String r = score.get(0).getStringValue("main", "");
									String weights = score.get(0).getStringValue("weights", "0");
									BigDecimal finalres = getScore2(score, new BigDecimal("1"));
									finalres = new BigDecimal(weights).subtract((new BigDecimal(weights).subtract(finalres)).multiply(new BigDecimal(r)));
									if (isshowscore) {
										itemsi.add(3, getBillNormalCellItemVO(list.size(), finalres.toString()));
									}
									BigDecimal w = new BigDecimal(weights);
									if (w.compareTo(BigDecimal.ZERO) == 0) {
										itemsi.add(isshowscore ? 4 : 3, getBillNormalCellItemVO(list.size(), "0"));
									} else {
										itemsi.add(isshowscore ? 4 : 3, getBillNormalCellItemVO(list.size(), finalres.multiply(new BigDecimal("100")).divide(w, 2, BigDecimal.ROUND_HALF_UP).toString() + "%"));
									}
								}
							}
							// continue;
						} else {// 定性指标
							BigDecimal right = new BigDecimal(0);
							BigDecimal CR = new BigDecimal(0);
							BigDecimal CP = new BigDecimal(0);
							BigDecimal PR = new BigDecimal(0);
							BigDecimal PP = new BigDecimal(0);
							String weights = null;
							if (type_score.containsKey("垂直R")) {
								List<HashVO> score = type_score.get("垂直R"); // 这个部门的这个指标
								// 垂直R的所有打分明细
								if (score.size() > 0) {
									right = right.add(c.multiply(cr)); // 权重=垂直R的权重*垂直的权重
									CR = getScore2(score, c.multiply(cr));// 根据模拟计算
									// 某一类打分比如垂直R的所有得分算平均
									// *
									// （垂直R的权重*垂直的权重）
									weights = score.get(0).getStringValue("weights", "0");
								}
							}
							if (type_score.containsKey("垂直P")) {
								List<HashVO> score = type_score.get("垂直P");
								if (score.size() > 0) {
									right = right.add(c.multiply(cp));
									CP = getScore2(score, c.multiply(cp));
									if (weights == null) {
										weights = score.get(0).getStringValue("weights", "0");
									}
								}
							}
							if (type_score.containsKey("平行R")) {
								List<HashVO> score = type_score.get("平行R");
								if (score.size() > 0) {
									right = right.add(p.multiply(pr));
									PR = getScore2(score, p.multiply(pr));
									if (weights == null) {
										weights = score.get(0).getStringValue("weights", "0");
									}
								}
							}
							if (type_score.containsKey("平行P")) {
								List<HashVO> score = type_score.get("平行P");
								if (score.size() > 0) {
									right = right.add(p.multiply(pp));
									PP = getScore2(score, p.multiply(pp));
									if (weights == null) {
										weights = score.get(0).getStringValue("weights", "0");
									}
								}
							}
							BigDecimal finalresc = CR.add(CP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
							BigDecimal finalresp = PR.add(PP).divide(right, 6, BigDecimal.ROUND_HALF_UP);
							BigDecimal finalres = finalresc.add(finalresp).setScale(2, BigDecimal.ROUND_HALF_UP); // 一个指标根据垂直R、、、计算出来的最终得分
							if (isshowscore) {
								itemsi.add(3, getBillNormalCellItemVO(list.size(), finalres.toString()));
							}
							BigDecimal w = new BigDecimal(weights);
							if (w.compareTo(BigDecimal.ZERO) == 0) {
								itemsi.add(isshowscore ? 4 : 3, getBillNormalCellItemVO(list.size(), "0"));
							} else {
								itemsi.add(isshowscore ? 4 : 3, getBillNormalCellItemVO(list.size(), finalres.multiply(new BigDecimal("100")).divide(w, 2, BigDecimal.ROUND_HALF_UP).toString() + "%"));
							}
						}
					} else {
						if (isshowscore) {
							itemsi.add(3, getBillNormalCellItemVO(list.size(), ""));
							itemsi.add(4, getBillNormalCellItemVO(list.size(), ""));
						} else {
							itemsi.add(3, getBillNormalCellItemVO(list.size(), ""));
						}
					}
					list.add(itemsi.toArray(new BillCellItemVO[0]));
				}
				cell.setCollength(list.get(0).length);
				cell.setRowlength(list.size());
				BillCellItemVO[][] items = (BillCellItemVO[][]) list.toArray(new BillCellItemVO[0][0]);
				int li_allowMaxColWidth = 375;
				FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
				for (int j = 0; j < items[0].length; j++) {
					int li_maxwidth = 0;
					String str_cellValue = null;
					for (int i = 0; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
							int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
							if (li_width > li_maxwidth) {
								li_maxwidth = li_width;
							}
						}
					}
					li_maxwidth = li_maxwidth + 13;
					if (li_maxwidth > li_allowMaxColWidth) {
						li_maxwidth = li_allowMaxColWidth;
					}
					for (int i = 1; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						items[i][j].setColwidth("" + li_maxwidth);
					}
				}
				cell.setCellItemVOs(items);
				return cell;
			}
		}

		cell.setRowlength(1);
		cell.setCollength(1);
		BillCellItemVO[][] items = new BillCellItemVO[1][1];
		items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
		items[0][0].setColwidth("300");
		cell.setCellItemVOs(items);
		return cell;
	}

	/**
	 * 此方法用于查看高管定性指标得分（即董事长直接根据岗责的评分）
	 */
	public BillCellVO getPostCheckResVO(String checkdate, String checkeduserid) throws Exception {
		BillCellVO cell = new BillCellVO();
		String logid = null;
		StringBuffer sql = new StringBuffer("select id,name from sal_target_check_log where 1=1 ");
		if (checkdate != null && !"".equals(checkdate)) {
			sql.append(" and (checkdate='" + checkdate + "' )");
		}
		String[][] logidandname = getDmo().getStringArrayByDS(null, sql.toString());
		if (logidandname != null && logidandname.length > 0) {
			logid = logidandname[0][0];
			HashVO[] allscorevo = getDmo().getHashVoArrayByDS(null, "select * from sal_person_check_score where targettype='高管定性指标' and scoretype='手动打分' and checkeduser='" + checkeduserid + "' and logid=" + logid);
			if (allscorevo != null && allscorevo.length > 0) {
				List<BillCellItemVO[]> list = new ArrayList<BillCellItemVO[]>();
				List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
				items0.add(getBillTitleCellItemVO(logidandname[0][1]));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.get(0).setSpan("1," + items0.size());
				list.add(items0.toArray(new BillCellItemVO[0]));
				List<BillCellItemVO> items1 = new ArrayList<BillCellItemVO>();
				items1.add(getBillTitleCellItemVO("指标名称"));
				items1.add(getBillTitleCellItemVO("得分"));
				list.add(items1.toArray(new BillCellItemVO[0]));
				for (int i = 0; i < allscorevo.length; i++) {
					List<BillCellItemVO> itemsi = new ArrayList<BillCellItemVO>();
					itemsi.add(0, getBillNormalCellItemVO(i, allscorevo[i].getStringValue("targetname")));
					itemsi.get(0).setCelltype("TEXTAREA");
					itemsi.add(1, getBillNormalCellItemVO(i, allscorevo[i].getStringValue("checkscore")));
					list.add(itemsi.toArray(new BillCellItemVO[0]));
				}
				cell.setCollength(list.get(0).length);
				cell.setRowlength(list.size());
				BillCellItemVO[][] items = (BillCellItemVO[][]) list.toArray(new BillCellItemVO[0][0]);
				int li_allowMaxColWidth = 375;
				FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font_b);
				for (int j = 0; j < items[0].length; j++) {
					int li_maxwidth = 0;
					String str_cellValue = null;
					for (int i = 0; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
							int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue) + 10;
							if (li_width > li_maxwidth) {
								li_maxwidth = li_width;
							}
						}
					}
					li_maxwidth = li_maxwidth + 13;
					if (li_maxwidth > li_allowMaxColWidth) {
						li_maxwidth = li_allowMaxColWidth;
					}
					for (int i = 1; i < items.length; i++) {
						str_cellValue = items[i][j].getCellvalue();
						items[i][j].setColwidth("" + li_maxwidth);
					}
				}

				for (int i = 0; i < items.length; i++) {
					int maxrc = 0;
					String str_cellValue = null;
					for (int j = 0; j < items[0].length; j++) {
						str_cellValue = items[i][j].getCellvalue();
						if (str_cellValue != null && !str_cellValue.trim().equals("") && "1,1".equals(items[i][j].getSpan())) {
							int rc = (SwingUtilities.computeStringWidth(fm, str_cellValue) + 10) / Integer.parseInt(items[i][j].getColwidth());
							if (rc > maxrc) {
								maxrc = rc;
							}
						}
					}
					for (int j = 0; j < items[0].length; j++) {
						items[i][j].setRowheight(maxrc * 18 + "");
					}
				}
				cell.setCellItemVOs(items);
				return cell;
			}
		}
		cell.setRowlength(1);
		cell.setCollength(1);
		BillCellItemVO[][] items = new BillCellItemVO[1][1];
		items[0][0] = getBillTitleCellItemVO("未查询到相应考评信息");
		items[0][0].setColwidth("300");
		cell.setCellItemVOs(items);
		return cell;

	}

	/**
	 * 创建工资单
	 */
	public void onCreateSalaryBill(List sql, String salarybillid, String accountid, String checkdate,BillVO vo) throws Exception {
		// 查询该帐套的所有因子公式
		HashVO[] vos = getDmo().getHashVoArrayByDS(null, "select f.*,a.seq,a.viewname from sal_account_factor a left join sal_factor_def f on a.factorid=f.id where a.accountid=" + accountid);
		if (vos != null && vos.length > 0) {
			for (int i = 0; i < vos.length; i++) {
				InsertSQLBuilder isb = new InsertSQLBuilder("sal_salarybill_factor");
				isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_SALARYBILL_FACTOR", 1000));
				isb.putFieldValue("salarybillid", salarybillid);
				isb.putFieldValue("factorid", vos[i].getStringValue("id"));
				isb.putFieldValue("viewname", vos[i].getStringValue("viewname"));
				isb.putFieldValue("seq", vos[i].getStringValue("seq"));
				sql.add(isb.getSQL());
			}
		}
		//zzl[2020-5-18] 增加网格工资单
		HashVO[] allperson;
		HashVO [] modelVos=getDmo().getHashVoArrayByDS(null,"select * from sal_account_set where id='"+vo.getStringValue("sal_account_setid")+"'");
		if(modelVos[0].getStringValue("name").contains("网格")){
			allperson=getDmo().getHashVoArrayByDS(null,"select t1.*,t1.id checkeduser,t1.id wgid,sal.ID vuserid,sal.CODE,sal.NAME,sal.SEX,sal.BIRTHDAY,sal.TELLERNO,sal.CARDID,sal.POSITION,sal.STATIONDATE,sal.STATIONRATIO,sal.AGE,sal.DEGREE,sal.UNIVERSITY,sal.SPECIALITIES,sal.POSTTITLE,sal.POSTTITLEAPPLYDATE,sal.POLITICALSTATUS,sal.CONTRACTDATE,sal.JOINWORKDATE,sal.JOINSELFBANKDATE,sal.WORKAGE,sal.SELFBANKAGE,sal.ONLYCHILDRENBTHDAY,sal.SELFBANKACCOUNT,sal.OTHERACCOUNT,sal.FAMILYACCOUNT,sal.PENSION,sal.HOUSINGFUND,sal.PLANWAY,sal.PLANRATIO,sal.ISUNCHECK,sal.FAMILYNAME,sal.MEDICARE,sal.TEMPORARY,sal.OTHERGLOD,sal.TECHNOLOGY,sal.STATIONKIND,sal.MAINDEPTID,sal.DEPTID,sal.DEPTNAME,sal.MAINSTATIONID,sal.MAINSTATION,sal.POSTSEQ,sal.DEPTSEQ,sal.LINKCODE,sal.DEPTCODE from excel_tab_85 t1 left join v_sal_personinfo sal on t1.G=sal.code\n" +
					"where t1.id in (select personinfoid from sal_account_personinfo where accountid='"+accountid+"')");
		}else{
			// 得到该帐套的所有人
			allperson = getDmo().getHashVoArrayByDS(null, "select t1.*,t1.id checkeduser from v_sal_personinfo t1 where id in (select personinfoid from sal_account_personinfo where accountid=" + accountid + ")");

		}
		SalaryFomulaParseUtil putil = new SalaryFomulaParseUtil();
		putil.initFactorHashVOCache(vos); // 注册把所有公式
		for (int k = 0; k < allperson.length; k++) {
			if (allperson[k] == null) {
				continue;
			}
			allperson[k].setAttributeValue("checkdate", checkdate);
			SalaryFormulaDMO.setRemoteActionSchedule("", "工资计算", "工资计算中,请耐心等待" + (k + 1) + "/" + allperson.length); // 服务器端执行状态更新
			if (vos == null || vos.length == 0) {
				break;
			}
			for (int p = 0; p < vos.length; p++) {
				InsertSQLBuilder isb = new InsertSQLBuilder("sal_salarybill_detail");
				isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_SALARYBILL_DETAIL"));
				isb.putFieldValue("salarybillid", salarybillid);
				isb.putFieldValue("userid", allperson[k].getStringValue("id"));
				isb.putFieldValue("factorname", vos[p].getStringValue("name"));
				isb.putFieldValue("viewname", vos[p].getStringValue("viewname"));
				isb.putFieldValue("factorid", vos[p].getStringValue("id"));
				try {
					StringBuffer sb = new StringBuffer();
					isb.putFieldValue("factorvalue", "" + putil.onExecute(putil.getFoctorHashVO(vos[p].getStringValue("name")), allperson[k], sb));
					isb.putFieldValue("computedesc", "" + sb);
				} catch (Exception e) {
					WLTLogger.getLogger(SalaryServiceImpl.class).error(e);
					SalaryFormulaDMO.removeRemoteActionSchedule("", "工资计算");
					throw new Exception("在生成员工[" + allperson[k].getStringValue("name") + "]的工资单时计算[" + vos[p].getStringValue("viewname") + "]发生异常请检查定义或与管理员联系!");
				}
				isb.putFieldValue("seq", vos[p].getStringValue("seq"));
				sql.add(isb.getSQL());
			}
		}
		SalaryFormulaDMO.setRemoteActionSchedule("", "工资计算", "数据库处理中...");
		getDmo().executeBatchByDS(null, sql);
		SalaryFormulaDMO.removeRemoteActionSchedule("", "工资计算");
	}

	/**
	 * 比较一些属性来决定是否进行更新 目前只对比指标名称、指标权重、组权重、组内权重方式、组间权重方式
	 * 
	 * @return
	 */
	private boolean compareIsCanUpdatePerson(HashVO newtargetvo, HashVO olddetailvo) {
		if (!newtargetvo.getStringValue("name", "").equals(olddetailvo.getStringValue("targetname", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("weights", "").equals(olddetailvo.getStringValue("weights", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("planedvalue", "").equals(olddetailvo.getStringValue("planedvalue", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("getvalue", "").equals(olddetailvo.getStringValue("getvalue", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("checktype", "").equals(olddetailvo.getStringValue("checktype", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("define", "").equals(olddetailvo.getStringValue("targetdefine", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("scorerweightstype", "").equals(olddetailvo.getStringValue("scorerweightstype", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("groupweights", "").equals(olddetailvo.getStringValue("groupweights", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("groupweightstype", "").equals(olddetailvo.getStringValue("groupweightstype", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("scoretype", "").equals(olddetailvo.getStringValue("scoretype", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("partvalue", "").equals(olddetailvo.getStringValue("partvalue", ""))) {
			return true;
		}
		return false;
	}

	/**
	 * 部门指标对比
	 * 
	 * @param oldvo
	 * @param newvo
	 * @return
	 */
	private boolean compareIsCanUpdateDept(HashVO newtargetvo, HashVO olddetailvo) {
		if (!newtargetvo.getStringValue("code", "").equals(olddetailvo.getStringValue("targetcode", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("name", "").equals(olddetailvo.getStringValue("targetname", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("evalstandard", "").equals(olddetailvo.getStringValue("evalstandard", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("descr", "").equals(olddetailvo.getStringValue("targetdescr", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("define", "").equals(olddetailvo.getStringValue("targetdefine", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("weights", "").equals(olddetailvo.getStringValue("weights", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("planedvalue", "").equals(olddetailvo.getStringValue("planedvalue", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("getvalue", "").equals(olddetailvo.getStringValue("getvalue", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("checktype", "").equals(olddetailvo.getStringValue("checktype", ""))) {
			return true;
		}
		if (!newtargetvo.getStringValue("reportformula", "").equals(olddetailvo.getStringValue("reportformula", ""))) {
			return true;
		}
		return false;
	}

	/**
	 * 前几个月的str
	 * 
	 * @param date
	 * @param frontmonth
	 * @return
	 * @throws Exception
	 */
	public String getBackMonth(String date, int frontmonth) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.SIMPLIFIED_CHINESE);
		Calendar c = new GregorianCalendar();
		c.setTime(sdf.parse(date));
		c.add(c.MONTH, -frontmonth);
		return sdf.format(c.getTime());
	}

	/**
	 * 直接录入的定量指标数据
	 * 
	 * @param deptid
	 * @return
	 */
	public BillCellVO[] getExtDateDirect(String checkdate, String[] deptid) throws Exception {
		if (checkdate != null) {
			String[] deptids = getDmo().getStringArrayFirstColByDS(null, "select targetid from sal_quantifytargetdate where checkdate='" + checkdate + "' and targettype='部门定量指标'");
			String[] perids = getDmo().getStringArrayFirstColByDS(null, "select targetid from sal_quantifytargetdate where checkdate='" + checkdate + "' and targettype='员工定量指标'");
			HashVO[] depttargetvos = getDmo().getHashVoArrayByDS(null, "select s.*, t.name, t.descr,t.checkcycle,t.unitvalue from sal_quantifytargetdate s left join sal_target_list t on s.targetid=t.id  where s.checkdate='" + checkdate + "' and s.targettype='部门定量指标'");
			HashVO[] pertargetvos = getDmo().getHashVoArrayByDS(null, "select s.*, t.name, t.descr,t.checkcycle,t.unitvalue from sal_quantifytargetdate s left join sal_person_check_list t on s.targetid=t.id  where s.checkdate='" + checkdate + "' and s.targettype='员工定量指标'");
			if ((depttargetvos != null && depttargetvos.length > 0) || (pertargetvos != null && pertargetvos.length > 0)) {
				List<BillCellItemVO[]> all = new ArrayList<BillCellItemVO[]>();
				List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
				items0.add(getBillTitleCellItemVO(checkdate + "考核数据"));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.add(getBillNormalCellItemVO(0, ""));
				items0.get(0).setSpan("1," + items0.size());
				all.add(items0.toArray(new BillCellItemVO[0]));
				List<BillCellItemVO> itemsd2 = new ArrayList<BillCellItemVO>();
				itemsd2.add(getBillTitleCellItemVO("编码"));
				itemsd2.add(getBillTitleCellItemVO("名称"));
				itemsd2.add(getBillTitleCellItemVO("描述"));
				itemsd2.add(getBillTitleCellItemVO("频率"));
				itemsd2.add(getBillTitleCellItemVO("单位"));
				itemsd2.add(getBillTitleCellItemVO("结果"));
				all.add(itemsd2.toArray(new BillCellItemVO[0]));
				if (depttargetvos != null && depttargetvos.length > 0) {
					List<BillCellItemVO> itemsd = new ArrayList<BillCellItemVO>();
					itemsd.add(getBillNormalCellItemVO(0, "部门定量指标"));
					itemsd.get(itemsd.size() - 1).setBackground("191,213,255");
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.get(0).setSpan("1," + itemsd.size());
					all.add(itemsd.toArray(new BillCellItemVO[0]));
					for (int d = 0; d < depttargetvos.length; d++) {
						List<BillCellItemVO> itemsn = new ArrayList<BillCellItemVO>();
						itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("targetcode")));
						itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("name")));
						itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("descr")));
						itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("checkcycle")));
						itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("unitvalue", "")));
						itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("finavalue", "")));
						itemsn.get(itemsn.size() - 1).setCustProperty("id", depttargetvos[d].getStringValue("id", ""));
						itemsn.get(itemsn.size() - 1).setCustProperty("targetid", depttargetvos[d].getStringValue("targetid", ""));
						itemsn.get(itemsn.size() - 1).setCustProperty("targetcode", depttargetvos[d].getStringValue("targetcode", ""));
						itemsn.get(itemsn.size() - 1).setCustProperty("targettype", "1");
						itemsn.get(itemsn.size() - 1).setCellkey("value");
						if (depttargetvos[d].getStringValue("state", "").equals("已提交")) {
							itemsn.get(itemsn.size() - 1).setIseditable("N");
						} else {
							itemsn.get(itemsn.size() - 1).setIseditable("Y");
						}
						itemsn.get(itemsn.size() - 1).setCelltype("NUMBERTEXT");
						all.add(itemsn.toArray(new BillCellItemVO[0]));
					}
				}

				if (pertargetvos != null && pertargetvos.length > 0) {
					List<BillCellItemVO> itemsd = new ArrayList<BillCellItemVO>();
					itemsd.add(getBillNormalCellItemVO(0, "员工定量指标"));
					itemsd.get(itemsd.size() - 1).setBackground("191,213,255");
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.add(getBillNormalCellItemVO(0, ""));
					itemsd.get(0).setSpan("1," + itemsd.size());
					all.add(itemsd.toArray(new BillCellItemVO[0]));
					for (int d = 0; d < pertargetvos.length; d++) {
						List<BillCellItemVO> itemsn = new ArrayList<BillCellItemVO>();
						itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("targetcode")));
						itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("name")));
						itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("descr")));
						itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("checkcycle")));
						itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("unitvalue", "")));
						itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("finavalue", "")));
						itemsn.get(itemsn.size() - 1).setCustProperty("id", pertargetvos[d].getStringValue("id", ""));
						itemsn.get(itemsn.size() - 1).setCustProperty("targetid", pertargetvos[d].getStringValue("targetid", ""));
						itemsn.get(itemsn.size() - 1).setCustProperty("targetcode", pertargetvos[d].getStringValue("targetcode", ""));
						itemsn.get(itemsn.size() - 1).setCustProperty("targettype", "2");
						itemsn.get(itemsn.size() - 1).setCellkey("value");
						if (pertargetvos[d].getStringValue("state", "").equals("已提交")) {
							itemsn.get(itemsn.size() - 1).setIseditable("N");
						} else {
							itemsn.get(itemsn.size() - 1).setIseditable("Y");
						}
						itemsn.get(itemsn.size() - 1).setCelltype("NUMBERTEXT");
						all.add(itemsn.toArray(new BillCellItemVO[0]));
					}
				}
				BillCellVO cell = new BillCellVO();
				cell.setSeq("Y");
				cell.setDescr(checkdate);
				BillCellItemVO[][] items = all.toArray(new BillCellItemVO[0][0]);
				formatClen(items);
				cell.setCellItemVOs(items);
				cell.setCollength(items[0].length);
				cell.setRowlength(items.length);
				return new BillCellVO[] { cell };
			} else {
				BillCellVO[] cellvo = new BillCellVO[1];
				cellvo[0] = new BillCellVO();
				cellvo[0].setDescr("没有相关数据");
				cellvo[0].setRowlength(1);
				cellvo[0].setCollength(1);
				BillCellItemVO[][] items = new BillCellItemVO[1][1];
				items[0][0] = getBillTitleCellItemVO("没有相关数据");
				items[0][0].setColwidth("300");
				cellvo[0].setCellItemVOs(items);
				return cellvo;
			}
		}
		String currid = TBUtil.getTBUtil().getCurrSession().getLoginUserId(); // 得到当前的访问人。
		String role[] = getDmo().getStringArrayFirstColByDS(null, "select rolecode from v_pub_user_role_1 where userid = '" + currid + "' and (rolecode='外部数据管理员' or rolecode='普通管理员') ");
		HashVO[] vos = getDmo().getHashVoArrayByDS(null, "select * from SAL_TARGET_CHECK_LOG where status <> '考核结束' order by checkdate ");
		if (vos != null && vos.length > 0) {
			BillCellVO[] cellvo = new BillCellVO[vos.length];
			StringBuffer sb = new StringBuffer(" (1=2 ");
			if (role.length == 0) {
				if (deptid != null && deptid.length > 0) {
					for (int d = 0; d < deptid.length; d++) {
						sb.append(" or s.dbsource like '%;" + deptid[d] + ";%' ");
					}
				}
			} else {
				sb.append(" or s.dbsource is not null");
			}
			sb.append(") ");
			// 单位
			String[] deptids = getDmo().getStringArrayFirstColByDS(null, "select id from sal_target_list s where type='部门定量指标' and dbfromtype='系统' and " + sb.toString() + " and state='参与考核' ");
			String[] perids = getDmo().getStringArrayFirstColByDS(null, "select id from sal_person_check_list s where targettype='员工定量指标' and dbfromtype='系统' and " + sb.toString() + " and state='参与考核' ");
			if ((deptids != null && deptids.length > 0) || (perids != null && perids.length > 0)) {
				for (int i = 0; i < vos.length; i++) {
					String isgz = "Y";
					List<BillCellItemVO[]> all = new ArrayList<BillCellItemVO[]>();
					List<BillCellItemVO> items0 = new ArrayList<BillCellItemVO>();
					items0.add(getBillTitleCellItemVO(vos[i].getStringValue("name")));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.add(getBillNormalCellItemVO(0, ""));
					items0.get(0).setSpan("1," + items0.size());
					all.add(items0.toArray(new BillCellItemVO[0]));
					List<BillCellItemVO> itemsd2 = new ArrayList<BillCellItemVO>();
					itemsd2.add(getBillTitleCellItemVO("编码"));
					itemsd2.add(getBillTitleCellItemVO("名称"));
					itemsd2.add(getBillTitleCellItemVO("描述"));
					itemsd2.add(getBillTitleCellItemVO("频率"));
					itemsd2.add(getBillTitleCellItemVO("单位"));
					itemsd2.add(getBillTitleCellItemVO("结果"));
					all.add(itemsd2.toArray(new BillCellItemVO[0]));
					// 部门定量指标
					HashVO[] depttargetvos = getDmo().getHashVoArrayByDS(null, " select s.*,q.finavalue, q.state qstate,q.id qid from sal_target_list s left join ( select * from sal_quantifytargetdate where targettype='部门定量指标' and checkdate='" + vos[i].getStringValue("checkdate") + "') q on s.id=q.targetid where  s.type='部门定量指标' and s.dbfromtype='系统' and s.state='参与考核' and " + sb.toString());
					if (depttargetvos != null && depttargetvos.length > 0) {
						List<BillCellItemVO> itemsd = new ArrayList<BillCellItemVO>();
						itemsd.add(getBillNormalCellItemVO(0, "部门定量指标"));
						itemsd.get(itemsd.size() - 1).setBackground("191,213,255");
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.get(0).setSpan("1," + itemsd.size());
						all.add(itemsd.toArray(new BillCellItemVO[0]));
						for (int d = 0; d < depttargetvos.length; d++) {
							List<BillCellItemVO> itemsn = new ArrayList<BillCellItemVO>();
							itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("code")));
							itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("name")));
							itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("descr")));
							itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("checkcycle")));
							itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("unitvalue", "")));
							itemsn.add(getBillNormalCellItemVO(all.size(), depttargetvos[d].getStringValue("finavalue", "")));
							itemsn.get(itemsn.size() - 1).setCustProperty("id", depttargetvos[d].getStringValue("qid", ""));
							itemsn.get(itemsn.size() - 1).setCustProperty("targetid", depttargetvos[d].getStringValue("id", ""));
							itemsn.get(itemsn.size() - 1).setCustProperty("targetcode", depttargetvos[d].getStringValue("code", ""));
							itemsn.get(itemsn.size() - 1).setCustProperty("targettype", "1");
							itemsn.get(itemsn.size() - 1).setCellkey("value");
							if (depttargetvos[d].getStringValue("qstate", "").equals("已提交")) {
								itemsn.get(itemsn.size() - 1).setIseditable("N");
							} else {
								itemsn.get(itemsn.size() - 1).setIseditable("Y");
								isgz = "N";
							}
							itemsn.get(itemsn.size() - 1).setCelltype("NUMBERTEXT");
							all.add(itemsn.toArray(new BillCellItemVO[0]));
						}
					}
					// 员工定量指标
					HashVO[] pertargetvos = getDmo().getHashVoArrayByDS(null,
							" select s.*,q.finavalue, q.state qstate,q.id qid from sal_person_check_list s left join (select * from sal_quantifytargetdate where targettype='员工定量指标' and checkdate='" + vos[i].getStringValue("checkdate") + "') q on s.id=q.targetid where s.targettype='员工定量指标' and s.dbfromtype='系统' and " + sb.toString() + " and s.state='参与考核'");
					if (pertargetvos != null && pertargetvos.length > 0) {
						List<BillCellItemVO> itemsd = new ArrayList<BillCellItemVO>();
						itemsd.add(getBillNormalCellItemVO(0, "员工定量指标"));
						itemsd.get(itemsd.size() - 1).setBackground("191,213,255");
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.add(getBillNormalCellItemVO(0, ""));
						itemsd.get(0).setSpan("1," + itemsd.size());
						all.add(itemsd.toArray(new BillCellItemVO[0]));
						for (int d = 0; d < pertargetvos.length; d++) {
							List<BillCellItemVO> itemsn = new ArrayList<BillCellItemVO>();
							itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("code")));
							itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("name")));
							itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("descr")));
							itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("checkcycle")));
							itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("unitvalue", "")));
							itemsn.add(getBillNormalCellItemVO(all.size(), pertargetvos[d].getStringValue("finavalue", "")));
							itemsn.get(itemsn.size() - 1).setCustProperty("id", pertargetvos[d].getStringValue("qid", ""));
							itemsn.get(itemsn.size() - 1).setCustProperty("targetid", pertargetvos[d].getStringValue("id", ""));
							itemsn.get(itemsn.size() - 1).setCustProperty("targetcode", pertargetvos[d].getStringValue("code", ""));
							itemsn.get(itemsn.size() - 1).setCustProperty("targettype", "2");
							itemsn.get(itemsn.size() - 1).setCellkey("value");
							if (pertargetvos[d].getStringValue("qstate", "").equals("已提交")) {
								itemsn.get(itemsn.size() - 1).setIseditable("N");
							} else {
								itemsn.get(itemsn.size() - 1).setIseditable("Y");
								isgz = "N";
							}
							itemsn.get(itemsn.size() - 1).setCelltype("NUMBERTEXT");
							all.add(itemsn.toArray(new BillCellItemVO[0]));
						}
					}
					cellvo[i] = new BillCellVO();
					cellvo[i].setSeq(isgz);
					cellvo[i].setDescr(vos[i].getStringValue("checkdate"));
					BillCellItemVO[][] items = all.toArray(new BillCellItemVO[0][0]);
					formatClen(items);
					cellvo[i].setCellItemVOs(items);
					cellvo[i].setCollength(items[0].length);
					cellvo[i].setRowlength(items.length);
				}
				return cellvo;
			} else {
				BillCellVO[] cellvo_ = new BillCellVO[1];
				cellvo_[0] = new BillCellVO();
				cellvo_[0].setDescr("没有需要录入的指标");
				cellvo_[0].setRowlength(1);
				cellvo_[0].setCollength(1);
				BillCellItemVO[][] items = new BillCellItemVO[1][1];
				items[0][0] = getBillTitleCellItemVO("没有需要录入的指标!");
				items[0][0].setColwidth("300");
				cellvo_[0].setCellItemVOs(items);
				return cellvo_;
			}
		} else {
			BillCellVO[] cellvo = new BillCellVO[1];
			cellvo[0] = new BillCellVO();
			cellvo[0].setDescr("没有正在进行的考核");
			cellvo[0].setRowlength(1);
			cellvo[0].setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("没有正在进行的考核!");
			items[0][0].setColwidth("300");
			cellvo[0].setCellItemVOs(items);
			return cellvo;
		}
	}

	/**
	 * 部门最终得分的调整
	 * 
	 * @return
	 */
	public BillCellVO[] getTarget_Check_ReviseVO(String logid) throws Exception {
		HashVO[] logvos = null;
		if (logid == null || "".equals(logid)) {
			logvos = getDmo().getHashVoArrayByDS(null, "select * from SAL_TARGET_CHECK_LOG where status <> '考核结束' order by checkdate");
		} else {
			logvos = getDmo().getHashVoArrayByDS(null, "select * from SAL_TARGET_CHECK_LOG where id=" + logid);
		}
		if (logvos != null && logvos.length > 0) {
			BillCellVO[] cellvo = new BillCellVO[logvos.length];
			for (int i = 0; i < logvos.length; i++) {
				HashVO[] alldeptdetail = getDmo().getHashVoArrayByDS(null, "select * from sal_target_check_revise_result s left join pub_corp_dept d on s.checkeddeptid=d.id where s.logid=" + logvos[i].getStringValue("id") + " order by d.linkcode");
				List<BillCellItemVO[]> item = new ArrayList<BillCellItemVO[]>();
				List<BillCellItemVO> itemso = new ArrayList<BillCellItemVO>();
				itemso.add(getBillTitleCellItemVO(logvos[i].getStringValue("name")));
				itemso.add(getBillNormalCellItemVO(0, ""));
				itemso.add(getBillNormalCellItemVO(0, ""));
				itemso.add(getBillNormalCellItemVO(0, ""));
				itemso.get(0).setSpan("1," + itemso.size());
				item.add(itemso.toArray(new BillCellItemVO[0]));
				cellvo[i] = new BillCellVO();
				cellvo[i].setDescr(logvos[i].getStringValue("checkdate"));
				if (alldeptdetail != null && alldeptdetail.length > 0) {
					if (logid != null && !"".equals(logid)) {
						cellvo[i].setSeq("Y");
					}
					List<BillCellItemVO> item0 = new ArrayList<BillCellItemVO>();
					item0.add(getBillTitleCellItemVO("部门名称"));
					item0.add(getBillTitleCellItemVO("部门总分"));
					item0.add(getBillTitleCellItemVO("调整分"));
					item0.add(getBillTitleCellItemVO("            调整理由                        "));
					item.add(item0.toArray(new BillCellItemVO[0]));
					for (int a = 0; a < alldeptdetail.length; a++) {
						List<BillCellItemVO> itema = new ArrayList<BillCellItemVO>();
						itema.add(getBillNormalCellItemVO(item.size(), alldeptdetail[a].getStringValue("checkeddeptname", "")));
						itema.add(getBillNormalCellItemVO(item.size(), alldeptdetail[a].getStringValue("currscore", "")));
						BillCellItemVO ivo = getBillNormalCellItemVO(item.size(), alldeptdetail[a].getStringValue("revisescore", ""));
						BillCellItemVO ivo2 = getBillNormalCellItemVO(item.size(), alldeptdetail[a].getStringValue("descr", ""));
						ivo.setCustProperty("id", alldeptdetail[a].getStringValue("id", ""));
						ivo.setCellkey("value");
						ivo.setCelltype("NUMBERTEXT");
						ivo2.setCelltype("TEXTAREA");
						if (logid == null || "".equals(logid)) {
							ivo.setIseditable("Y");
							ivo2.setIseditable("Y");
						}
						itema.add(ivo);
						itema.add(ivo2);
						item.add(itema.toArray(new BillCellItemVO[0]));
					}
					BillCellItemVO[][] items = item.toArray(new BillCellItemVO[0][0]);
					formatClen(items);
					cellvo[i].setCellItemVOs(items);
					cellvo[i].setRowlength(items.length);
					cellvo[i].setCollength(items[0].length);
				} else {
					cellvo[i] = new BillCellVO();
					cellvo[i].setRowlength(2);
					cellvo[i].setCollength(1);
					BillCellItemVO[][] items = new BillCellItemVO[2][1];
					BillCellItemVO vo = getBillNormalCellItemVO(0, "");
					if (logid == null || "".equals(logid)) {
						vo.setCellvalue("部门总分未计算");
					} else {
						vo.setCellvalue("未查询到相关数据");
					}
					items[0][0] = getBillTitleCellItemVO(logvos[i].getStringValue("name"));
					items[0][0].setColwidth("300");
					items[1][0] = vo;
					items[1][0].setColwidth("300");
					cellvo[i].setCellItemVOs(items);
				}
			}
			return cellvo;
		} else {
			BillCellVO[] cellvo = new BillCellVO[1];
			cellvo[0] = new BillCellVO();
			cellvo[0].setDescr("没有正在进行的考核");
			cellvo[0].setRowlength(1);
			cellvo[0].setCollength(1);
			BillCellItemVO[][] items = new BillCellItemVO[1][1];
			items[0][0] = getBillTitleCellItemVO("没有正在进行的考核!");
			items[0][0].setColwidth("300");
			cellvo[0].setCellItemVOs(items);
			return cellvo;
		}
	}

	/**
	 * 薪酬统计
	 * 
	 * @param column
	 * @param checkdate
	 * @return
	 */
	public BillCellVO getSalaryVO(String[] column, String checkdate) throws Exception {
		String[] sabillids = getDmo().getStringArrayFirstColByDS(null, "select id from sal_salarybill where monthly='" + checkdate + "'"); // 某月的所有工资单
		if (sabillids != null && sabillids.length > 0) {
			HashVO[] allbasevo = getDmo().getHashVoArrayByDS(null, "select d.*,c.name corpname,v.name username from sal_salarybill_detail d left join v_sal_personinfo v on d.userid=v.id left join pub_corp_dept c on v.maindeptid=c.id where d.salarybillid in (" + getTb().getInCondition(sabillids) + ") order by c.linkcode,v.code, d.seq ");
			if (allbasevo != null && allbasevo.length > 0) {
				LinkedHashMap<String, LinkedHashMap<String, HashVO>> lm = new LinkedHashMap<String, LinkedHashMap<String, HashVO>>();
				String userid = null;
				String username = null;
				String factorname = null;
				String factorvalue = null;
				String corpname = null;
				for (int i = 0; i < allbasevo.length; i++) {
					if (allbasevo[i] == null) {
						continue;
					}
					userid = allbasevo[i].getStringValue("userid");
					username = allbasevo[i].getStringValue("username");
					factorname = allbasevo[i].getStringValue("factorname");
					if (!getTb().isExistInArray(factorname, column)) {
						continue;
					}
					factorvalue = allbasevo[i].getStringValue("factorvalue");
					corpname = allbasevo[i].getStringValue("corpname");
					if (lm != null && lm.containsKey(corpname)) {
						LinkedHashMap<String, HashVO> userid_vo = lm.get(corpname);
						if (userid_vo.containsKey(userid)) {
							userid_vo.get(userid).setAttributeValue("部门", corpname);
							userid_vo.get(userid).setAttributeValue("人员", username);
							userid_vo.get(userid).setAttributeValue(factorname, factorvalue);
						} else {
							HashVO vo = new HashVO();
							vo.setAttributeValue("部门", corpname);
							vo.setAttributeValue("人员", username);
							vo.setAttributeValue(factorname, factorvalue);
							userid_vo.put(userid, vo);
						}
					} else if (lm != null) {
						LinkedHashMap<String, HashVO> userid_vo = new LinkedHashMap<String, HashVO>();
						HashVO vo = new HashVO();
						vo.setAttributeValue("部门", corpname);
						vo.setAttributeValue("人员", username);
						vo.setAttributeValue(factorname, factorvalue);
						userid_vo.put(userid, vo);
						lm.put(corpname, userid_vo);
					}
				}

				if (lm != null && lm.size() > 0) {
					List<BillCellItemVO[]> all = new ArrayList<BillCellItemVO[]>();
					String[] deptname = lm.keySet().toArray(new String[0]);
					for (int i = 0; i < deptname.length; i++) {
						LinkedHashMap<String, HashVO> userid_vo = lm.get(deptname[i]);
						String[] userids = userid_vo.keySet().toArray(new String[0]);
						for (int j = 0; j < userids.length; j++) {
							String[] allkeys = userid_vo.get(userids[j]).getKeys();
							if (i == 0 && j == 0) {
								List<BillCellItemVO> item0 = new ArrayList<BillCellItemVO>();
								List<BillCellItemVO> item1 = new ArrayList<BillCellItemVO>();
								BillCellItemVO vo = getBillTitleCellItemVO("部门");
								vo.setSpan("2,1");
								item0.add(vo);
								item1.add(getBillTitleCellItemVO(""));
								BillCellItemVO vo2 = getBillTitleCellItemVO("人员");
								vo2.setSpan("2,1");
								item0.add(vo2);
								item1.add(getBillTitleCellItemVO(""));
								for (int k = 0; k < allkeys.length; k++) {
									if ("部门".equals(allkeys[k]) || "人员".equals(allkeys[k])) {
										continue;
									}
									BillCellItemVO vo3 = getBillTitleCellItemVO(allkeys[k]);
									vo3.setHalign(2);
									vo3.setSpan("1,3");
									item0.add(vo3);
									item0.add(getBillTitleCellItemVO(""));
									item0.add(getBillTitleCellItemVO(""));
									item1.add(getBillTitleCellItemVO("明细"));
									item1.add(getBillTitleCellItemVO("小计"));
									item1.add(getBillTitleCellItemVO("合计"));
								}
								all.add(item0.toArray(new BillCellItemVO[0]));
								all.add(item1.toArray(new BillCellItemVO[0]));
							}

							List<BillCellItemVO> itemn = new ArrayList<BillCellItemVO>();
							BillCellItemVO vodept = getBillNormalCellItemVO(all.size(), deptname[i]);
							if (j == 0) {
								vodept.setSpan(userids.length + ",1");
								vodept.setBackground("234,240,248");
							}
							itemn.add(vodept);
							itemn.add(getBillNormalCellItemVO(all.size(), userid_vo.get(userids[j]).getStringValue("人员")));
							for (int k = 0; k < allkeys.length; k++) {
								if ("部门".equals(allkeys[k]) || "人员".equals(allkeys[k])) {
									continue;
								}
								itemn.add(getBillNormalCellItemVO(all.size(), userid_vo.get(userids[j]).getStringValue(allkeys[k])));
								BillCellItemVO voxj = getBillNormalCellItemVO(all.size(), "");
								BillCellItemVO vohj = getBillTitleCellItemVO("");
								if (j == 0) {
									voxj.setCellvalue(getXJ(userid_vo.values().toArray(new HashVO[0]), allkeys[k]));
									voxj.setSpan(userids.length + ",1");
									voxj.setBackground("234,240,248");
									String[] temp = getHJ(lm, allkeys[k]);
									vohj.setCellvalue(temp[0]);
									vohj.setSpan(temp[1] + ",1");
									vohj.setBackground("234,240,248");
									vohj.setCelltype("TEXTAREA");
									vohj.setValign(1);
								}
								itemn.add(voxj);
								itemn.add(vohj);
							}
							all.add(itemn.toArray(new BillCellItemVO[0]));
						}
					}
					BillCellVO cellvo = new BillCellVO();
					BillCellItemVO[][] items = all.toArray(new BillCellItemVO[0][0]);
					cellvo.setRowlength(items.length);
					cellvo.setCollength(items[0].length);
					cellvo.setCellItemVOs(items);
					return cellvo;
				}
			}
		}
		BillCellVO cellvo = new BillCellVO();
		cellvo.setRowlength(1);
		cellvo.setCollength(1);
		BillCellItemVO[][] items = new BillCellItemVO[1][1];
		items[0][0] = getBillTitleCellItemVO("没有查询到相关信息");
		items[0][0].setColwidth("300");
		cellvo.setCellItemVOs(items);
		return cellvo;
	}

	/**
	 * 小计
	 * 
	 * @return
	 */
	public String getXJ(HashVO[] vos, String key) {
		BigDecimal sum = new BigDecimal("0");
		for (int i = 0; i < vos.length; i++) {
			sum = sum.add(new BigDecimal(vos[i].getStringValue(key, "0")));
		}
		return sum.setScale(1, BigDecimal.ROUND_HALF_UP).toString();
	}

	/**
	 * 小计
	 * 
	 * @return
	 */
	public String[] getHJ(HashMap map, String key) {
		BigDecimal sum = new BigDecimal("0");
		int c = 0;
		HashMap[] maps = (HashMap[]) map.values().toArray(new HashMap[0]);
		for (int i = 0; i < maps.length; i++) {
			HashVO[] vos = (HashVO[]) maps[i].values().toArray(new HashVO[0]);
			sum = sum.add(new BigDecimal(getXJ(vos, key)));
			c = c + vos.length;
		}
		return new String[] { sum.setScale(1, BigDecimal.ROUND_HALF_UP).toString(), c + "" };

	}

	/** ****************************** */
	// 根据某次计划计算所有人的各个定性指标，加权计算后的值。放到sal_person_check_target_score表中。做年终汇总
	public void onePlanCalcAllUserEveryDXTargetScore(String logid) throws Exception {
		new SalaryFormulaDMO().onePlanCalcAllUserEveryDXTargetScore(logid);
	}

	public Object[][] calcYearPersonCheckReport(String[] logid) throws Exception {
		return new SalaryFormulaDMO().calcYearPersonCheckReport(logid);
	}

	/** *** 以下是公式计算远程调用方法 **** */

	public Object[] onExecute(HashVO _factorVO, HashVO _baseDataHashVO) throws Exception {
		return new SalaryFormulaDMO().onExecute(_factorVO, _baseDataHashVO);
	}

	public void onCalcPersonDLTarget(HashVO planvo, String _calbatch) throws Exception {
		new SalaryFormulaDMO().calcPersonDLtarget(planvo, _calbatch);
	}

	/*
	 * _billType 不可为空,任意写一个类型即可。
	 */
	public Object getRemoteActionSchedule(String _key, String _billType) throws Exception {
		return SalaryFormulaDMO.getRemoteActionSchedule(_key, _billType);
	}

	public void calc_P_Pay(HashVO planVO, String _calbatch) throws Exception {
		new SalaryFormulaDMO().calc_P_Pay(planVO, _calbatch);
	}

	public void calcDelayPay(String _logid) throws Exception {
		new SalaryFormulaDMO().calcDelayPay(_logid);
	}

	public void calc_QQ_Pay(HashVO planVO) throws Exception {
		new SalaryFormulaDMO().onCalcQQMoney(planVO);
	}

	public void calcDeptTotleScoreIntoReviseTable(String logid, String state) throws Exception {
		new SalaryFormulaDMO().calcDeptTotleScoreIntoReviseTable(logid, state);
	}

	public HashVO[] calcOnePersonTarget_P_Money(String _targetID, String _checkdate) throws Exception {
		return new SalaryFormulaDMO().calcOnePersonTarget_P_Money(_targetID, _checkdate);
	}

	public void calcDeptDLtarget(HashVO planvo, String state, String _targetType) throws Exception {
		new SalaryFormulaDMO().calcDeptDLtarget(planvo, state, _targetType);
	}

	/** ************* 结束 **************** */

	// 部门考核汇总
	public HashVO[] getDeptCheck(String[] checkids, String[] types) throws Exception {
		if (checkids != null && checkids.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String finalres = "";
			String logids = "";
			for (int i = 0; i < checkids.length; i++) {
				if (i == 0) {
					logids += "'" + checkids[i] + "'";
				} else {
					logids += ",'" + checkids[i] + "'";
				}

				for (int j = 0; j < types.length; j++) {
					finalres += ", a" + i + "_" + j + ".finalres result_a" + i + "_" + j;
				}
			}

			sb_sql.append(" select a.id, a.name" + finalres);
			sb_sql.append(" from pub_corp_dept a ");
			for (int i = 0; i < checkids.length; i++) {
				for (int j = 0; j < types.length; j++) {
					sb_sql.append(" left join (select finalres,checkeddeptid from sal_target_check_result " + "where logid in('" + checkids[i] + "') and targettype='" + types[j] + "') " + "a" + i + "_" + j + " on a" + i + "_" + j + ".checkeddeptid = a.id ");
				}
			}
			sb_sql.append(" where a.id in(select distinct checkeddeptid from sal_target_check_result where logid in (" + logids + ")) order by a.linkcode ");

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 员工业务汇总
	public HashVO[] getPersonBusiness(String[] checkids, String[] types_id) throws Exception {
		if (checkids != null && checkids.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String finalres = "";
			String logids = "";
			for (int i = 0; i < checkids.length; i++) {
				if (i == 0) {
					logids += "'" + checkids[i] + "'";
				} else {
					logids += ",'" + checkids[i] + "'";
				}

				for (int j = 0; j < types_id.length; j++) {
					finalres += ", a" + i + "_" + j + ".checkscore result_a" + i + "_" + j;
				}
			}

			sb_sql.append(" select a.id, a.name username, b.name corpname, a.stationkind" + finalres);
			sb_sql.append(" from v_sal_personinfo a left join pub_corp_dept b on a.maindeptid=b.id ");
			for (int i = 0; i < checkids.length; i++) {
				for (int j = 0; j < types_id.length; j++) {
					sb_sql.append(" left join (select checkscore,checkeduser from sal_person_check_score " + "where logid in ('" + checkids[i] + "') and targetid='" + types_id[j] + "') " + "a" + i + "_" + j + " on a" + i + "_" + j + ".checkeduser = a.id ");
				}
			}
			sb_sql.append(" where a.id in(select distinct checkeduser from sal_person_check_score where logid in (" + logids + ")) order by a.code ");

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 员工考核汇总
	public HashVO[] getPersonCheck(String[] checkids, String[] types, String[] types_nocou) throws Exception {
		if (checkids != null && checkids.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String finalres = "";
			String logids = "";
			for (int i = 0; i < checkids.length; i++) {
				if (i == 0) {
					logids += "'" + checkids[i] + "'";
				} else {
					logids += ",'" + checkids[i] + "'";
				}

				for (int j = 0; j < types.length; j++) {
					finalres += ", a" + i + "_" + j + ".result result_a" + i + "_" + j;
				}
				for (int j = 0; j < types_nocou.length; j++) {
					finalres += ", b" + i + "_" + j + ".result result_b" + i + "_" + j;
				}
			}

			sb_sql.append(" select a.id, a.name username, b.name corpname, a.stationkind" + finalres);
			sb_sql.append(" from v_sal_personinfo a left join pub_corp_dept b on a.maindeptid=b.id ");
			for (int i = 0; i < checkids.length; i++) {
				for (int j = 0; j < types.length; j++) {
					sb_sql.append(" left join (select case when stationkind='一般人员' then finalres3 else finalres2 end as result,checkeduserid from sal_person_check_result " + "where logid in ('" + checkids[i] + "') and targettype='" + types[j] + "')" + " a" + i + "_" + j + " on a" + i + "_" + j + ".checkeduserid = a.id ");
				}
				for (int j = 0; j < types_nocou.length; j++) {
					sb_sql.append(" left join (select finalres2 result,checkeduserid from sal_person_check_result " + "where logid in ('" + checkids[i] + "') and targettype='" + types_nocou[j] + "')" + " b" + i + "_" + j + " on b" + i + "_" + j + ".checkeduserid = a.id ");
				}
			}
			sb_sql.append(" where a.id in(select distinct checkeduserid from sal_person_check_result where logid in (" + logids + ")) order by a.code ");

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 员工工资汇总
	public HashVO[] getPersonSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception {
		if (checkids != null && checkids.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String finalres = "";
			String logids = "";
			for (int i = 0; i < checkids.length; i++) {
				if (i == 0) {
					logids += "'" + checkids[i].replace(",", "','") + "'";
				} else {
					logids += ",'" + checkids[i].replace(",", "','") + "'";
				}

				for (int j = 0; j < types_id.length; j++) {
					finalres += ", a" + i + "_" + j + ".sum result_a" + i + "_" + j;
				}
			}

			sb_sql.append(" select a.id, a.name username, b.name corpname, a.stationkind" + finalres);
			if(dept.equals("机关")){
				sb_sql.append(" from (select * from v_sal_personinfo where STATIONKIND in('前台人员','中层管理')) a left join pub_corp_dept b on a.maindeptid=b.id ");

			}else{
				sb_sql.append(" from (select * from v_sal_personinfo where STATIONKIND not in('前台人员','中层管理')) a left join pub_corp_dept b on a.maindeptid=b.id ");
			}
			for (int i = 0; i < checkids.length; i++) {
				for (int j = 0; j < types_id.length; j++) {
					String str= getDmo().getStringValueByDS(null,"select SOURCETYPE from sal_factor_def where 1=1  and  id='"+types_id[j]+"'");
					if(str.equals("数字")){
						sb_sql.append(" left join (select userid,sum(factorvalue) sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
					}else{
						sb_sql.append(" left join (select userid,factorvalue sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid,factorvalue)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
					}
				}
			}
			sb_sql.append(" where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in (" + logids + ")) order by a.code ");

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 岗位工资汇总
	public HashVO[] getPostSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception {
		if (checkids != null && checkids.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String finalres = "";
			String logids = "";
			for (int i = 0; i < checkids.length; i++) {
				if (i == 0) {
					logids += "'" + checkids[i].replace(",", "','") + "'";
				} else {
					logids += ",'" + checkids[i].replace(",", "','") + "'";
				}

				for (int j = 0; j < types_id.length; j++) {
					finalres += ", ROUND(sum(a" + i + "_" + j + ".sum)/count(a.stationkind),2) result_a" + i + "_" + j;
				}
			}

			sb_sql.append(" select a.stationkind" + finalres);
			if(dept.equals("机关")){
				sb_sql.append(" from (select * from v_sal_personinfo where STATIONKIND in('前台人员','中层管理')) a ");
			}else{
				sb_sql.append(" from (select * from v_sal_personinfo where STATIONKIND not in('前台人员','中层管理')) a ");
			}
			for (int i = 0; i < checkids.length; i++) {
				for (int j = 0; j < types_id.length; j++) {
					sb_sql.append(" left join (select userid,sum(factorvalue) sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
				}
			}
			sb_sql.append(" where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in (" + logids + ")) " + "group by a.stationkind ");//order by a.code,a.stationkind

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 部门工资汇总
	public HashVO[] getDeptSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception {
		if (checkids != null && checkids.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String finalres = "";
			String logids = "";
			for (int i = 0; i < checkids.length; i++) {
				if (i == 0) {
					logids += "'" + checkids[i].replace(",", "','") + "'";
				} else {
					logids += ",'" + checkids[i].replace(",", "','") + "'";
				}

				for (int j = 0; j < types_id.length; j++) {
					finalres += ", sum(a" + i + "_" + j + ".sum) result_a" + i + "_" + j;
				}
			}

			sb_sql.append(" select b.name corpname" + finalres);
			if(dept.equals("机关")){
				sb_sql.append(" from (select * from v_sal_personinfo where STATIONKIND in('前台人员','中层管理')) a left join pub_corp_dept b on a.maindeptid=b.id ");
			}else{
				sb_sql.append(" from (select * from v_sal_personinfo where STATIONKIND not in('前台人员','中层管理')) a left join pub_corp_dept b on a.maindeptid=b.id ");

			}
			for (int i = 0; i < checkids.length; i++) {
				for (int j = 0; j < types_id.length; j++) {
					sb_sql.append(" left join (select userid,sum(factorvalue) sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
				}
			}
			sb_sql.append(" where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in (" + logids + ")) " + "group by b.name");//order by b.linkcode

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 延期支付基金 免责基金 股权激励基金
	public HashVO[] getPersonStyle(String type, String month, String userid, String excel_code, String[] types, String[] types_nocou) throws Exception {
		if (type != null && type.contains("延期支付")) {
			return getPersonDelay(month, userid, excel_code, types, types_nocou);
		}
		String[] tablename = getDmo().getStringArrayFirstColByDS(null, "select tablename from excel_tab where excelname='" + type + "'");

		if (!(tablename != null && tablename.length > 0)) {
			return null;
		}

		String result = "";
		for (int j = 0; j < types.length; j++) {
			result += ", " + types[j] + " result_a0_" + j;
		}
		for (int j = 0; j < types_nocou.length; j++) {
			result += ", " + types_nocou[j] + " result_b0_" + j;
		}

		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append(" select a.*" + result + " from " + tablename[0] + " a");
		sb_sql.append(" left join v_sal_personinfo b on a." + excel_code + "=b.name ");
		sb_sql.append(" where b.id = " + userid + " and a.year='" + month.substring(0, 4) + "' and month='" + month.substring(5, month.length()) + "' order by b.deptseq,b.postseq");
		// sb_sql.append(" where a.year='"+month.substring(0, 4)+"' and
		// month='"+month.substring(5, month.length())+"'");

		return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
	}

	// 延期支付基金 免责基金 股权激励基金 总计
	public HashVO[] getPersonStylee(String tablename, String[] dates, String excel_code, String[] types, String[] types_nocou, HashMap _condition) throws Exception {
		if (dates != null && dates.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String result = "";
			String field = "";
			String date = "";
			for (int i = 0; i < dates.length; i++) {
				/*
				 * if (i == 0) { date += "'" + dates[i] + "'"; } else { date +=
				 * ",'" + dates[i] + "'"; }
				 */

				for (int j = 0; j < types.length; j++) {
					result += ", a" + i + "." + types[j] + " result_a" + i + "_" + j;
				}
				for (int j = 0; j < types_nocou.length; j++) {
					result += ", a" + i + "." + types_nocou[j] + " result_b" + i + "_" + j;
				}
			}

			for (int j = 0; j < types.length; j++) {
				field += ", " + types[j];
			}
			for (int j = 0; j < types_nocou.length; j++) {
				field += ", " + types_nocou[j];
			}

			sb_sql.append(" select a.id, a.name username, b.name corpname, a.stationkind" + result);
			sb_sql.append(" from v_sal_personinfo a left join pub_corp_dept b on a.maindeptid=b.id ");
			for (int i = 0; i < dates.length; i++) {
				sb_sql.append(" left join (select " + excel_code + " " + field + " from " + tablename + " where year='" + dates[i].substring(0, 4) + "' and month='" + dates[i].substring(5, dates[i].length()) + "' ) a" + i + " on a" + i + "." + excel_code + " = a.name ");
			}
			if (_condition != null && _condition.containsKey("condition")) {
				String condition = (String) _condition.get("condition");
				if (!getTb().isEmpty(condition)) {
					sb_sql.append(condition);
				}
			}
			sb_sql.append(" order by a.code ");

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 股权激励基金
	public HashVO[] getPersonGqjl(String tablename, int[] years, String excel_code, HashMap _condition) throws Exception {
		if (years != null && years.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String result = "";
			HashMap map = (HashMap) _condition.get("menu");
			String last_col = (String) map.get("股权激励报表显示的最后一列"); //默认为空,如果为F,那么F以后的列都不显示。
			String last_col_if_sum = (String) map.get("股权激励报表最后一列是否求和"); //默认为N

			String years_str[] = new String[years.length];
			for (int i = 0; i < years_str.length; i++) {
				years_str[i] = years[i] + "";
			}

			HashVO excel_self_itemname[] = getDmo().getHashVoArrayByDS(null, "select * from " + tablename + " where year in(" + getTb().getInCondition(years_str) + ") and A='姓名' order by year");

			for (int i = 0; i < years.length; i++) {
				for (int j = 0; j < excel_self_itemname.length; j++) {
					for (int k = 2; k <= 9; k++) {
						String col = stbutil.convertIntColToEn(k,false);
						String colname = excel_self_itemname[j].getStringValue(col);
						if (!getTb().isEmpty(colname)) {
							result += ", a" + i + "." + col + " '" + years[i] + "_" + col + "'";
							if (col.equalsIgnoreCase(last_col)) {
								break;
							}
						}
					}
				}
			}

			sb_sql.append(" select a." + excel_code + " '姓名',b.stationkind,b.deptname" + result + "");
			if (!getTb().isEmpty(last_col)) {
				sb_sql.append(",lasta.xcgq ");
			}
			sb_sql.append(" from " + tablename + " a left join v_sal_personinfo b on a." + excel_code + "=b.name ");
			for (int i = 0; i < years.length; i++) {
				sb_sql.append(" left join (select * from " + tablename + " where year = '" + years[i] + "') a" + i + " on a" + i + "." + excel_code + " = a." + excel_code);
			}
			if (!getTb().isEmpty(last_col) && "Y".equals(last_col_if_sum)) {
				sb_sql.append(" left join (select A,sum(" + last_col + ") xcgq from " + tablename + " xcgq where A is not null and A not in('姓名','员工股权激励台帐') group by A )  lasta on lasta." + excel_code + " = a." + excel_code); //最后一列	
			} else if (!getTb().isEmpty(last_col)) {
				sb_sql.append(" left join (select A," + last_col + " xcgq from " + tablename + " xcgq where A is not null and A not in('姓名','员工股权激励台帐') and year ='" + years[years.length - 1] + "')  lasta on lasta." + excel_code + " = a." + excel_code); //最后一列
			}
			sb_sql.append(" where a.year = '" + years[years.length - 1] + "' and a.A is not null and a.A not in('姓名','员工股权激励台帐') ");
			if (_condition != null && _condition.containsKey("condition")) {
				String condition = (String) _condition.get("condition");
				if (condition != null && !"".equals(condition)) {
					sb_sql.append(condition);
				}
			}

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	// 延期支付基金
	public HashVO[] getPersonYqzf(String year, String[] months, HashMap _condition) throws Exception {
		if (months != null && months.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String result = "";
			for (int i = 0; i < months.length; i++) {
				result += ", a" + i + ".money '" + months[i] + "入账'";
				result += ", b" + i + ".money '" + months[i] + "出账'";
			}

			int year_next = Integer.parseInt(year) + 1;
			sb_sql.append(" select a.name '姓名',a.deptname, a.stationkind, before_year.money_sum '上年度余额',now_year.money_sum '当前余额'" + result);
			sb_sql.append(" from v_sal_personinfo a  ");
			sb_sql.append(" left join (select userid,sum(case when calc='-' then 0-money else money end) as money_sum " + "from sal_person_fund_account where datadate <'" + year + "' group by userid) before_year " + "on before_year.userid = a.id ");
			sb_sql.append(" left join (select userid,sum(case when calc='-' then 0-money else money end) as money_sum " + "from sal_person_fund_account where datadate <'" + year_next + "' group by userid) now_year " + "on now_year.userid = a.id ");
			for (int i = 0; i < months.length; i++) {
				sb_sql.append(" left join (select sum(money) money ,userid from sal_person_fund_account where datadate = '" + months[i] + "' and calc='+' group by userid) a" + i + " on a" + i + ".userid = a.id ");
				sb_sql.append(" left join (select sum(money) money ,userid from sal_person_fund_account where datadate = '" + months[i] + "' and calc='-' group by userid) b" + i + " on b" + i + ".userid = a.id ");
			}
			if (_condition != null && _condition.containsKey("condition")) {
				String condition = (String) _condition.get("condition");
				if (!getTb().isEmpty(condition)) {
					sb_sql.append(condition);
				}
			}
			sb_sql.append(" order by a.code ");

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;
	}

	/*
	 * 延期支付金
	 */
	private HashVO[] getPersonDelay(String month, String userid, String excel_code, String[] types, String[] types_nocou) throws Exception {
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("select a.username,sum(case when a.calc='-' then 0-money else money end) as result_b0_0 from sal_person_fund_account a left join  v_sal_personinfo b on a.username = b.name where isdeleted ='N' or isdeleted is null group by a.username order by b.deptseq,b.postseq ");
		return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
	}

	public BillCellVO parseCellTempetToWord(BillCellVO cellTemplet, HashVO baseHVO) throws Exception {
		return new UseCellTempletParseUtil().onParse(cellTemplet, baseHVO);
	}

	// 获取预警值
	public HashVO getWarnVoById(String warnid, String _logid) throws Exception {
		return new SalaryFormulaDMO().getWarnVoById(warnid, _logid);
	}

	public void sendMessage(String[] session, String message) throws Exception {
		// ServerPushToClientIFC ifc = (ServerPushToClientIFC)
		// ServerPushToClientUtil.lookupClient(UIShowBox.class, new
		// PushConfigVO(ServerPushToClientUtil.PUSH_TYPE_BY_SESSION, session,
		// ClientEnvironment.getCurrSessionVO().getHttpsessionid()));
		// ifc.onExecute(new String[] { "", message, new
		// CommDMO().getCurrSession().getLoginUserName() });
	}

	public HashVO[] getAllInstallUser() throws Exception {
		HashMap mapUser = ServerEnvironment.getInstance().getLoginUserMap();
		String[] loginsession = ServerPushToClientServlet.getAllUser();
		List list = new ArrayList();
		for (int i = 0; i < loginsession.length; i++) {
			String[] userconfig = (String[]) mapUser.get(loginsession[i]);
			list.add(userconfig[3]);
		}
		HashMap map = new CommDMO().getHashMapBySQLByDS(null, "select code,name from pub_user where code in(" + TBUtil.getTBUtil().getInCondition(list) + ")");

		List rtlist = new ArrayList<HashVO>();
		for (int i = 0; i < loginsession.length; i++) {
			String[] userconfig = (String[]) mapUser.get(loginsession[i]);
			HashVO hvo = new HashVO();
			hvo.setAttributeValue("name", map.get(userconfig[3]));
			hvo.setAttributeValue("session", userconfig[0]);
			hvo.setToStringFieldName("name");
			rtlist.add(hvo);
		}
		return (HashVO[]) rtlist.toArray(new HashVO[0]);
	}

	public BillCellVO getReportCellVO(HashMap _where, String _filename) throws Exception {
		ReportService rs = new ReportServiceImpl();
		return rs.getReportCellVO(_where, _filename);
	};

	/** ==============================岗位价值评估--lcj================================== */

	// 生成岗位评估表记录【李春娟/2013-10-31】
	public void createPostEvalTable(String _planid) throws Exception {
		// 1.查询该计划的评估人及被评岗位记录id串
		String postids = getDmo().getStringValueByDS(null, "select postids from sal_post_eval_plan where id =" + _planid);
		if (postids == null || "".equals(postids)) {
			return;
		}
		// 2.按顺序（优先级由高到低）查出评估人和被评岗位的信息
		HashVO[] user_postvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_eval_plan_post where id in(" + getTb().getInCondition(postids) + ") order by seq");
		if (user_postvos == null || user_postvos.length == 0) {
			return;
		}
		// 3.获得评估人-被评岗位的详细对应关系
		HashMap user_postMap = new HashMap();
		for (int i = 0; i < user_postvos.length; i++) {
			String userids = user_postvos[i].getStringValue("userids");
			if (userids == null || "".equals(userids)) {// 人员
				continue;
			}
			String postlist = user_postvos[i].getStringValue("postlist");
			if (postlist == null || "".equals(postlist)) {
				continue;
			}
			String[] str_userids = getTb().split(userids, ";");
			for (int j = 0; j < str_userids.length; j++) {
				if (!user_postMap.containsKey(str_userids[j])) {// 优先级由高到低，故前面没添加过的才添加
					user_postMap.put(str_userids[j], postlist);// 评估人员id-被评岗位id串
				}
			}
		}
		// 4.获得该计划的所有评估人员id
		String[] userids = (String[]) user_postMap.keySet().toArray(new String[0]);
		if (userids == null || "".equals(userids)) {
			return;
		}
		HashMap user_deptMap = new HashMap();
		HashVO[] uservos = getDmo().getHashVoArrayByDS(null, "select * from v_pub_user_post_1 where isdefault='Y' and userid in(" + TBUtil.getTBUtil().getInCondition(userids) + ")");
		for (int i = 0; i < uservos.length; i++) {
			user_deptMap.put(uservos[i].getStringValue("userid"), uservos[i]);
		}

		// 5.在客户端已经重置了linkcode，这里按顺序获得所有的指标
		HashVO[] targetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_eval_target order by linkcode");
		if (targetvos == null || targetvos.length == 0) {
			return;
		}
		ArrayList sqlList = new ArrayList();
		sqlList.add("delete from sal_post_eval_target_copy where planid=" + _planid);
		sqlList.add("delete from sal_post_eval_score where planid=" + _planid);
		sqlList.add("delete from sal_post_eval_user_copy where planid=" + _planid);
		sqlList.add("delete from sal_post_eval_score_statistics where planid=" + _planid);
		sqlList.add("delete from sal_post_eval_score_total where planid=" + _planid);
		// 6.删除当前计划的指标版本，保证当前计划只有一个指标版本
		getDmo().executeBatchByDSImmediately(null, sqlList);
		sqlList.clear();

		HashMap treeIDMap = new HashMap();// 记录旧主建和新主键的对应关系
		InsertSQLBuilder target_sqlBuilder = new InsertSQLBuilder("sal_post_eval_target_copy");
		// 7.复制指标
		for (int i = 0; i < targetvos.length; i++) {
			String newid = getDmo().getSequenceNextValByDS(null, "S_SAL_POST_EVAL_TARGET_COPY");
			target_sqlBuilder.putFieldValue("id", newid);
			treeIDMap.put(targetvos[i].getStringValue("id"), newid);// 记录旧主建和新主键的对应关系
			target_sqlBuilder.putFieldValue("planid", _planid);
			target_sqlBuilder.putFieldValue("code", targetvos[i].getStringValue("code"));// 编码
			target_sqlBuilder.putFieldValue("targetname", targetvos[i].getStringValue("targetname"));// 名称
			target_sqlBuilder.putFieldValue("weight", targetvos[i].getStringValue("weight"));// 权重分值
			target_sqlBuilder.putFieldValue("remark", targetvos[i].getStringValue("remark"));// 说明
			target_sqlBuilder.putFieldValue("parentid", (String) treeIDMap.get(targetvos[i].getStringValue("parentid")));// 父亲记录的主键，因为遍历从第一层全部遍历完后再遍历下一层，所以这里的父亲主键在前面应该可以得到的。
			target_sqlBuilder.putFieldValue("linkcode", targetvos[i].getStringValue("linkcode"));// 关联码
			target_sqlBuilder.putFieldValue("seq", targetvos[i].getStringValue("seq"));// 排序字段
			sqlList.add(target_sqlBuilder.getSQL());
		}
		getDmo().executeBatchByDSImmediately(null, sqlList);// 复制的指标先立即执行一下，后面用到末级节点
		sqlList.clear();// 上面执行了的sql要清空

		// 8.查询复制后的指标的末级节点，即需要评分的指标。
		String[] targetids = getDmo().getStringArrayFirstColByDS(null, "select id from sal_post_eval_target_copy where planid=" + _planid + " and id not in(select distinct(parentid) from sal_post_eval_target_copy where planid=" + _planid + " and parentid is not null) order by linkcode");
		if (targetids == null || targetids.length == 0) {
			return;
		}

		String[] kinds = getDmo().getStringArrayFirstColByDS(null, "select stationkinds from sal_person_check_type where name like '%高管%' or name like '%中层%'");
		String stationkinds = null;
		if (kinds == null || kinds.length == 0) {
			stationkinds = "'高管','中层管理','支行行长','客户总经理'";
		} else {
			StringBuffer sb_kinds = new StringBuffer();
			for (int i = 0; i < kinds.length; i++) {
				sb_kinds.append(kinds[i]);
			}
			stationkinds = getTb().getInCondition(sb_kinds.toString());
		}

		HashMap postMap = getDmo().getHashMapBySQLByDS(null, "select id,id from pub_post where stationkind in(" + stationkinds + ")");

		// 9.创建评分记录，计划-评分人-指标-被评岗位
		InsertSQLBuilder score_sqlBuilder = new InsertSQLBuilder("sal_post_eval_score");
		for (int i = 0; i < userids.length; i++) {
			String[] str_postids = getTb().split((String) user_postMap.get(userids[i]), ";");
			for (int j = 0; j < targetids.length; j++) {
				for (int j2 = 0; j2 < str_postids.length; j2++) {
					score_sqlBuilder.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "s_sal_post_eval_score", 100));
					score_sqlBuilder.putFieldValue("planid", _planid);
					score_sqlBuilder.putFieldValue("scoreuser", userids[i]);
					score_sqlBuilder.putFieldValue("targetid", targetids[j]);
					score_sqlBuilder.putFieldValue("postid", str_postids[j2]);
					if (postMap.containsKey(str_postids[j2])) {// 如果是中层及以上的岗位，才可以评履职评估
						score_sqlBuilder.putFieldValue("checkscore2", "");// 需要制空
					} else {
						score_sqlBuilder.putFieldValue("checkscore2", "-");
					}
					HashVO uservo = (HashVO) user_deptMap.get(userids[i]);
					if (uservo != null) {
						score_sqlBuilder.putFieldValue("username", uservo.getStringValue("username"));// 评分人
						score_sqlBuilder.putFieldValue("scoredeptid", uservo.getStringValue("deptid"));// 评分部门
						score_sqlBuilder.putFieldValue("scoredeptname", uservo.getStringValue("deptname"));// 评分部门名称
					} else {
						score_sqlBuilder.putFieldValue("username", "");// 评分人
						score_sqlBuilder.putFieldValue("scoredeptid", "");// 评分部门
						score_sqlBuilder.putFieldValue("scoredeptname", "");// 评分部门名称
					}
					sqlList.add(score_sqlBuilder.getSQL());
				}
			}
		}
		getDmo().executeBatchByDSNoLog(null, sqlList);
	}

	// 获得岗位价值评估界面数据【李春娟/2013-10-31】
	public BillCellVO getPostEvalVO(String _planid, String _planname, String _userid, String _postids) throws Exception {
		// 1.查询被评岗位
		HashVO[] postvos = getDmo().getHashVoArrayByDS(null, "select d.shortname,p.name,p.id,p.stationkind from pub_post p left join pub_corp_dept d on d.id=p.deptid where p.id in(" + TBUtil.getTBUtil().getInCondition(_postids) + ") order by d.linkcode,p.seq,p.code");
		int postnum = 0;// 被评岗位有多少个
		if (postvos != null) {
			postnum = postvos.length;
		}

		// 2.指标一共几级
		String str_targetlevel = getDmo().getStringValueByDS(null, "select max(length(linkcode)) from sal_post_eval_target_copy where planid=" + _planid);
		int targetlevel = Integer.parseInt(str_targetlevel) / 4;

		// 3.指标末级节点有几个
		String targetnum = getDmo().getStringValueByDS(null, "select count(id) from sal_post_eval_target_copy where planid=" + _planid + " and id not in(select distinct(parentid) from sal_post_eval_target_copy where planid=" + _planid + " and parentid is not null) ");
		int evaltargetnum = Integer.parseInt(targetnum);

		// 4.记录需要评履职评估的岗位
		String[] kinds = getDmo().getStringArrayFirstColByDS(null, "select stationkinds from sal_person_check_type where name like '%高管%' or name like '%中层%'");
		String stationkinds = null;
		if (kinds == null || kinds.length == 0) {
			stationkinds = "'高管','中层管理','支行行长','客户总经理'";
		} else {
			StringBuffer sb_kinds = new StringBuffer();
			for (int i = 0; i < kinds.length; i++) {
				sb_kinds.append(kinds[i]);
			}
			stationkinds = TBUtil.getTBUtil().getInCondition(sb_kinds.toString());
		}

		int colLength = evaltargetnum * 2 + 2;// 总列数
		int rowLength = postnum + targetlevel + 2; // 总行数
		boolean showWeight = TBUtil.getTBUtil().getSysOptionBooleanValue("岗位价值评估界面是否显示权重", true);
		if (showWeight) {// 如果显示权重
			rowLength = postnum + targetlevel + 3; // 总行数
		}

		HashMap postMap = getDmo().getHashMapBySQLByDS(null, "select id,id from pub_post where stationkind in(" + stationkinds + ") and id in(" + TBUtil.getTBUtil().getInCondition(_postids) + ")");
		if (postMap.size() != postnum) {// 如果都是高管和中层的岗位，则所有岗位都需要评估履职评估，故不需要加界线格子，反之则需要加一空行
			rowLength++;
		}

		List<BillCellItemVO> titleRow = new ArrayList<BillCellItemVO>();
		SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
		// 4.计划名称
		BillCellItemVO cell_title_1 = salaryTBUtil.getBillCellVO_Blue("                          " + _planname, "1," + colLength);
		cell_title_1.setFontsize("15");
		cell_title_1.setFontstyle("1");
		cell_title_1.setHalign(1); //
		titleRow.add(cell_title_1);

		// 5.构造和添加指标表头及权重等
		List<BillCellItemVO> firstRow = new ArrayList<BillCellItemVO>();
		List<BillCellItemVO> weightRow = new ArrayList<BillCellItemVO>();// 最后一级有权重
		List<BillCellItemVO> evaltypeRow = new ArrayList<BillCellItemVO>();// 最后一级有评估类型：岗位评估、履职评估
		if (showWeight) {// 如果显示权重
			firstRow.add(salaryTBUtil.getBillCellVO_Blue("分类", (targetlevel + 2) + ",1"));
		} else {
			firstRow.add(salaryTBUtil.getBillCellVO_Blue("分类", (targetlevel + 1) + ",1"));
		}

		firstRow.add(salaryTBUtil.getBillCellVO_Blue("评价因素", targetlevel + ",1"));

		if (showWeight) {// 如果显示权重
			weightRow.add(salaryTBUtil.getBillCellVO_Blue(""));// 权重行
			weightRow.add(salaryTBUtil.getBillCellVO_Blue("所占权重"));// 权重行
		}

		evaltypeRow.add(salaryTBUtil.getBillCellVO_Blue(""));
		evaltypeRow.add(salaryTBUtil.getBillCellVO_Blue("岗位名称"));

		HashVO[] targetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_eval_target_copy where planid=" + _planid + " order by linkcode");

		HashMap rowMap = new HashMap();
		rowMap.put(1, firstRow);
		for (int i = 2; i <= targetlevel; i++) {// 有几层就加几个List
			List<BillCellItemVO> tmpRow = new ArrayList<BillCellItemVO>();
			rowMap.put(i, tmpRow);
		}

		ArrayList lasttargetList = new ArrayList();
		int tmp_lastnum = 0;// 前面有几个末级节点
		for (int i = 0; i < targetvos.length; i++) {
			BillCellItemVO cell_1 = salaryTBUtil.getBillTargetCellItemVO(targetvos[i].getStringValue("targetname")); // 显示指标名称
			String linkcode = targetvos[i].getStringValue("linkcode");
			int colnum = 1;
			for (int j = i + 1; j < targetvos.length; j++) {
				if (targetvos[j].getStringValue("linkcode").startsWith(linkcode)) {
					if (j == targetvos.length - 1) {
						colnum++;
						break;
					} else if (!targetvos[j + 1].getStringValue("linkcode").startsWith(targetvos[j].getStringValue("linkcode"))) {
						colnum++;
					}
				} else {
					break;
				}
			}

			boolean islastNode = false;
			if (colnum == 1) {
				tmp_lastnum++;// 前面有几个末级节点
				islastNode = true;
				colnum = 2;
				cell_1.setSpan("1," + 2);// 合并列
				lasttargetList.add(targetvos[i].getStringValue("id"));// 记录末级指标id
			} else {
				cell_1.setSpan("1," + (colnum * 2 - 2));// 合并列
			}
			int tmp_level = linkcode.length() / 4;
			List<BillCellItemVO> tmpList = (List<BillCellItemVO>) rowMap.get(tmp_level);

			if (tmpList.size() == 0) {// 如果第一次找到，先添加分类和评价因素合并的空格
				tmpList.add(salaryTBUtil.getBillCellVO_Blue(""));//
				tmpList.add(salaryTBUtil.getBillCellVO_Blue(""));//
				for (int j = 1; j < tmp_lastnum; j++) {
					tmpList.add(salaryTBUtil.getBillTargetCellItemVO(""));//
					tmpList.add(salaryTBUtil.getBillTargetCellItemVO(""));//
				}
			}
			tmpList.add(cell_1);
			for (int j = 1; j < colnum * 2 - 2; j++) {
				tmpList.add(salaryTBUtil.getBillTargetCellItemVO(""));
			}
			if (islastNode) {
				if (targetlevel != tmp_level) {
					for (int j = tmp_level + 1; j <= targetlevel; j++) {
						List<BillCellItemVO> nextlist = (List<BillCellItemVO>) rowMap.get(j);
						if (nextlist.size() == 0) {
							nextlist.add(salaryTBUtil.getBillCellVO_Blue(""));//
							nextlist.add(salaryTBUtil.getBillCellVO_Blue(""));//
						}
						nextlist.add(salaryTBUtil.getBillTargetCellItemVO(""));//
						nextlist.add(salaryTBUtil.getBillTargetCellItemVO(""));//
					}
					cell_1.setSpan((targetlevel - tmp_level + 1) + ",2");
				}
				if (showWeight) {
					addWeightAndEvaltype(targetvos[i], weightRow, evaltypeRow);
				} else {
					addEvaltype(targetvos[i], evaltypeRow);
				}
			}
		}

		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();// 所有的cell格子对象
		cellVOs.add(titleRow.toArray(new BillCellItemVO[0]));// 添加标题行
		for (int i = 1; i <= rowMap.size(); i++) {
			List<BillCellItemVO> tmpRow = (List<BillCellItemVO>) rowMap.get(i);
			cellVOs.add(tmpRow.toArray(new BillCellItemVO[0]));
		}
		if (showWeight) {
			cellVOs.add(weightRow.toArray(new BillCellItemVO[0]));// 最后一级有权重
		}
		cellVOs.add(evaltypeRow.toArray(new BillCellItemVO[0]));// 最后一级有评估类型：岗位评估、履职评估

		HashVO[] scorevos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_eval_score where planid=" + _planid + " and scoreuser=" + _userid);
		// 6.添加岗位名称及评分

		List<BillCellItemVO[]> generalcellVOs = new ArrayList<BillCellItemVO[]>();// 所有不需要评履职评估的cell格子对象
		for (int i = 1; i <= postnum; i++) {
			List<BillCellItemVO> row_N_VO = new ArrayList<BillCellItemVO>();
			if (postMap.containsKey(postvos[i - 1].getStringValue("id"))) {// 如果需要评履职评估则直接加入，否则，需要加到后面
				row_N_VO.add(salaryTBUtil.getBillCellVO_Blue(postvos[i - 1].getStringValue("shortname")));
				row_N_VO.add(salaryTBUtil.getBillCellVO_Blue(postvos[i - 1].getStringValue("name")));

				for (int j = 3; j <= colLength; j++) {
					BillCellItemVO cell_N = salaryTBUtil.getBillCellVO_Normal("");// 显示分数
					String targetid = (String) lasttargetList.get((j - 3) / 2);// 从第三列开始是评分表格
					for (int k = 0; k < scorevos.length; k++) {
						if (postvos[i - 1].getStringValue("id").equals(scorevos[k].getStringValue("postid")) && targetid.equals(scorevos[k].getStringValue("targetid"))) {// 如果岗位id和指标id都匹配，则找到该条记录的得分
							HashVO vo = new HashVO();
							vo.setAttributeValue("id", scorevos[k].getStringValue("id"));
							if ((j - 3) % 2 == 0) {
								vo.setAttributeValue("type", "岗位评估");
								cell_N.setCellvalue(scorevos[k].getStringValue("checkscore"));
							} else {
								vo.setAttributeValue("type", "履职评估");
								cell_N.setCellvalue(scorevos[k].getStringValue("checkscore2"));
							}
							cell_N.setCustProperty("hashvo", vo);
							break;
						}
					}
					cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.posteval.p040.PostEvalCheckValueChangeLinstener\",\"Cell执行\")");
					cell_N.setCelldesc("纯数字");
					cell_N.setCelltype("NUMBERTEXT");
					row_N_VO.add(cell_N);
				}
				cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
			} else {
				row_N_VO.add(salaryTBUtil.getBillCellVO_Blue(postvos[i - 1].getStringValue("shortname")));
				row_N_VO.add(salaryTBUtil.getBillCellVO_Blue(postvos[i - 1].getStringValue("name")));

				for (int j = 3; j <= colLength; j++) {
					BillCellItemVO cell_N = salaryTBUtil.getBillCellVO_Normal("");// 显示分数
					String targetid = (String) lasttargetList.get((j - 3) / 2);// 从第三列开始是评分表格
					for (int k = 0; k < scorevos.length; k++) {
						if (postvos[i - 1].getStringValue("id").equals(scorevos[k].getStringValue("postid")) && targetid.equals(scorevos[k].getStringValue("targetid"))) {// 如果岗位id和指标id都匹配，则找到该条记录的得分
							HashVO vo = new HashVO();
							vo.setAttributeValue("id", scorevos[k].getStringValue("id"));
							if ((j - 3) % 2 == 0) {
								vo.setAttributeValue("type", "岗位评估");
								cell_N.setCellvalue(scorevos[k].getStringValue("checkscore"));
								cell_N.setEditformula("AddItemValueChangedListener(\"cn.com.pushworld.salary.ui.posteval.p040.PostEvalCheckValueChangeLinstener\",\"Cell执行\")");
							} else {
								vo.setAttributeValue("type", "履职评估");
								cell_N.setIseditable("N");
								cell_N.setBackground("240,240,240");
							}
							cell_N.setCustProperty("hashvo", vo);
							break;
						}
					}
					cell_N.setCelldesc("纯数字");
					cell_N.setCelltype("NUMBERTEXT");
					row_N_VO.add(cell_N);
				}
				generalcellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
			}

		}
		if (generalcellVOs.size() > 0) {
			List<BillCellItemVO> row_N_VO = new ArrayList();
			BillCellItemVO cell_split = salaryTBUtil.getBillCellVO_Blue("", "1," + colLength);
			cell_split.setBackground("191,213,0");
			cell_split.setRowheight("5");
			row_N_VO.add(cell_split);// 分隔条
			cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
			cellVOs.addAll(generalcellVOs);
		}
		BillCellItemVO[][] cellitemvos = cellVOs.toArray(new BillCellItemVO[0][0]);
		BillCellVO cellVO = new BillCellVO();
		cellVO.setCellItemVOs(cellitemvos);
		cellVO.setRowlength(rowLength);
		cellVO.setCollength(colLength);
		return cellVO;
	}

	private void addWeightAndEvaltype(HashVO _targetvo, List<BillCellItemVO> weightRow, List<BillCellItemVO> evaltypeRow) {
		SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
		BillCellItemVO cell_weight = salaryTBUtil.getBillTargetCellItemVO(_targetvo.getStringValue("weight") + "%"); // 显示指标名称
		cell_weight.setSpan("1,2");// 合并列
		weightRow.add(cell_weight);// 权重行
		weightRow.add(salaryTBUtil.getBillTargetCellItemVO(""));// 增加空格

		evaltypeRow.add(salaryTBUtil.getBillTargetCellItemVO("岗位评估"));
		evaltypeRow.add(salaryTBUtil.getBillTargetCellItemVO("履职评估"));
	}

	private void addEvaltype(HashVO _targetvo, List<BillCellItemVO> evaltypeRow) {
		SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
		evaltypeRow.add(salaryTBUtil.getBillTargetCellItemVO("岗位评估"));
		evaltypeRow.add(salaryTBUtil.getBillTargetCellItemVO("履职评估"));
	}

	// 提交岗位价值评估【李春娟/2013-11-01】
	public void submitPostEvalTable(String _planid, String _userid) throws Exception {
		getDmo().executeUpdateByDS(null, "update sal_post_eval_score set status='已提交' where planid=" + _planid + " and scoreuser=" + _userid);
		String count = getDmo().getStringValueByDS(null, "select count(id) from sal_post_eval_score where (status!='已提交' or status is null) and planid=" + _planid);
		// 如果该方案的评分全部提交，则设置方案状态为【评估结束】
		if (!"0".equals(count)) {
			return;
		}
		ArrayList sqlList = new ArrayList();
		sqlList.add("delete from  sal_post_eval_user_copy where planid=" + _planid);// 先删除一下，操作出错后，还可以再重新执行
		sqlList.add("delete from  sal_post_eval_score_statistics where planid=" + _planid);
		sqlList.add("delete from  sal_post_eval_score_total where planid=" + _planid);
		getDmo().executeBatchByDSImmediately(null, sqlList);
		sqlList.clear();

		sqlList.add("update sal_post_eval_plan set status='评估结束',enddate='" + getTb().getCurrDate() + "' where id=" + _planid);

		// 将人员信息备份
		String[] userids = getDmo().getStringArrayFirstColByDS(null, "select scoreuser from sal_post_eval_score where planid=" + _planid + " group by scoreuser");
		HashVO[] uservos = getDmo().getHashVoArrayByDS(null, "select id,name,deptid,deptname,mainstationid,mainstation,stationkind,stationratio from v_sal_personinfo where id in(" + getTb().getInCondition(userids) + ")");
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("sal_post_eval_user_copy");
		sqlBuilder.putFieldValue("planid", _planid);
		for (int i = 0; i < uservos.length; i++) {
			sqlBuilder.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "s_sal_post_eval_user_copy"));
			sqlBuilder.putFieldValue("userid", uservos[i].getStringValue("id"));// 评分人
			sqlBuilder.putFieldValue("username", uservos[i].getStringValue("name"));// 评分人名称
			sqlBuilder.putFieldValue("deptid", uservos[i].getStringValue("deptid"));// 评分部门
			sqlBuilder.putFieldValue("deptname", uservos[i].getStringValue("deptname"));// 评分部门名称
			sqlBuilder.putFieldValue("postid", uservos[i].getStringValue("mainstationid"));// 评分人岗位
			sqlBuilder.putFieldValue("postname", uservos[i].getStringValue("mainstation"));// 岗位名称
			sqlBuilder.putFieldValue("stationkind", uservos[i].getStringValue("stationkind"));// 岗位分类
			sqlBuilder.putFieldValue("stationratio", uservos[i].getStringValue("stationratio"));// 岗位系数
			sqlList.add(sqlBuilder.getSQL());
		}
		// 将评分统计备份
		HashVO[] scorevos = getDmo().getHashVoArrayByDS(null, "select t1.postid,t1.targetid,t1.checkscore,t1.checkscore2,t2.stationkind,t2.stationratio,t3.weight from sal_post_eval_score t1 left join v_sal_personinfo t2 on t1.scoreuser=t2.id left join sal_post_eval_target_copy t3 on t1.targetid=t3.id where t1.planid=" + _planid + " order by t1.postid,t1.targetid,t2.stationkind");
		InsertSQLBuilder sqlBuilder2 = new InsertSQLBuilder("sal_post_eval_score_statistics");
		sqlBuilder2.putFieldValue("planid", _planid);
		String postid;
		String targetid;
		String stationkind;
		int scorecount = 0;
		double totalscore = 0;// 某个岗位某个指标的岗位评估的分母
		double totalscore2 = 0;// 某个岗位某个指标的履职评估的分母
		double totalratio = 0;// 某个岗位某个指标的分子（岗位评估和履职评估的分子相同）

		double tmpscore = 0;
		double tmpscore2 = 0;
		double tmpratio = 0;
		int tmpratiocount = 0;

		DecimalFormat df_6 = new DecimalFormat("#.000000");// 保留6位小数
		df_6.setMinimumIntegerDigits(1);// 设置整数部分至少一位

		ArrayList scoreList = new ArrayList();// 记录某个岗位某个指标的加权平均值，为后面统计某个岗位所有指标加权平均值做准备
		for (int i = 0; i < scorevos.length; i++) {
			postid = scorevos[i].getStringValue("postid");// 岗位主键
			targetid = scorevos[i].getStringValue("targetid");// 指标主键
			stationkind = scorevos[i].getStringValue("stationkind");
			double checkscore = Double.parseDouble(scorevos[i].getStringValue("checkscore"));
			String str_checkscore2 = scorevos[i].getStringValue("checkscore2");

			double stationratio = Double.parseDouble(scorevos[i].getStringValue("stationratio"));
			tmpratio += stationratio;
			tmpratiocount++;

			tmpscore += checkscore;
			if (!"-".equals(str_checkscore2)) {
				double checkscore2 = Double.parseDouble(str_checkscore2);
				tmpscore2 += checkscore2;
			}

			if (i < scorevos.length - 1 && postid.equals(scorevos[i + 1].getStringValue("postid")) && targetid.equals(scorevos[i + 1].getStringValue("targetid"))) {// 如果是同一岗位的同一指标的记录
				if (!stationkind.equals(scorevos[i + 1].getStringValue("stationkind"))) {// 如果岗位分类不一样了，则算出同一岗位分类的平均分数和权重
					totalscore += (tmpscore / tmpratiocount) * (tmpratio / tmpratiocount);
					totalscore2 += (tmpscore2 / tmpratiocount) * (tmpratio / tmpratiocount);
					totalratio += tmpratio / tmpratiocount;

					tmpscore = 0;
					tmpscore2 = 0;
					tmpratio = 0;
					tmpratiocount = 0;
				}
			} else {// 如果岗位不同或指标不同，则重新计算
				totalscore += (tmpscore / tmpratiocount) * (tmpratio / tmpratiocount);
				totalscore2 += (tmpscore2 / tmpratiocount) * (tmpratio / tmpratiocount);
				totalratio += tmpratio / tmpratiocount;

				double realscore = totalscore / totalratio;// 某岗位KPI1的岗位评估得分
				// 按打分人的岗位分类做加权平均计算,
				// 相同岗位分类的打分取平均分和平均岗位权重
				realscore = Double.parseDouble(df_6.format(realscore));

				sqlBuilder2.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "s_sal_post_eval_score_statistics"));
				sqlBuilder2.putFieldValue("postid", postid);// 岗位
				sqlBuilder2.putFieldValue("targetid", targetid);// 备份的指标主键，sal_post_eval_target_copy表中的id
				sqlBuilder2.putFieldValue("checkscore", realscore + "");// 某个指标的岗位加权分
				double realscore2 = totalscore2 / totalratio;// //某岗位KPI1的履职评估得分
				// 按打分人的岗位分类做加权平均计算,
				// 相同岗位分类的打分取平均分和平均岗位权重
				realscore2 = Double.parseDouble(df_6.format(realscore2));

				if ("-".equals(str_checkscore2)) {
					sqlBuilder2.putFieldValue("checkscore2", "-");// 某个指标的履职加权分
				} else {
					sqlBuilder2.putFieldValue("checkscore2", realscore2 + "");// 某个指标的履职加权分
				}

				sqlList.add(sqlBuilder2.getSQL());

				HashVO hashVO = new HashVO();
				hashVO.setAttributeValue("postid", postid);
				hashVO.setAttributeValue("checkscore", realscore);

				if ("-".equals(str_checkscore2)) {
					hashVO.setAttributeValue("checkscore2", "-");
				} else {
					hashVO.setAttributeValue("checkscore2", realscore2);
				}
				hashVO.setAttributeValue("weight", scorevos[i].getStringValue("weight"));
				scoreList.add(hashVO);
				totalscore = 0;
				totalscore2 = 0;
				totalratio = 0;

				tmpscore = 0;
				tmpscore2 = 0;
				tmpratio = 0;
				tmpratiocount = 0;
			}
		}

		// 计算某岗位等级加权分(m) 和 某岗位履职加权分(n)
		double score1 = 0;
		double score2 = 0;
		double totalweight = 0;
		int countnum = 0;
		InsertSQLBuilder sqlBuilder3 = new InsertSQLBuilder("sal_post_eval_score_total");
		sqlBuilder3.putFieldValue("planid", _planid);
		// 查询出各个岗位的测评人数
		HashMap postMap = getDmo().getHashMapBySQLByDS(null, "select postid,count(postid) from (select postid,scoreuser from sal_post_eval_score where planid =" + _planid + " group by postid,scoreuser) t1 group by postid");
		for (int i = 0; i < scoreList.size(); i++) {
			postid = ((HashVO) scoreList.get(i)).getStringValue("postid");// 岗位主键

			double weight = Double.parseDouble(((HashVO) scoreList.get(i)).getStringValue("weight"));
			totalweight += weight;

			double checkscore = Double.parseDouble(((HashVO) scoreList.get(i)).getStringValue("checkscore"));
			String str_checkscore2 = ((HashVO) scoreList.get(i)).getStringValue("checkscore2");

			score1 += checkscore * weight;
			if (!"-".equals(str_checkscore2)) {
				double checkscore2 = Double.parseDouble(str_checkscore2);
				score2 += checkscore2 * weight;
			}
			countnum++;
			if (i == scoreList.size() - 1 || !postid.equals(((HashVO) scoreList.get(i + 1)).getStringValue("postid"))) {// 如果是最后一条记录或岗位不同，则重新计算
				double realscore = score1 / totalweight;// 某岗位等级加权分(m) =
				// sum(每个KPI的岗位评估得分*对应的权重分值)/sum(每个KPI的权重)
				realscore = Double.parseDouble(df_6.format(realscore));

				sqlBuilder3.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "s_sal_post_eval_score_total"));
				sqlBuilder3.putFieldValue("postid", postid);// 岗位
				sqlBuilder3.putFieldValue("checkscore", realscore + "");// 某个岗位的岗位评估加权分
				double realscore2 = score2 / totalweight;// 某岗位履职加权分(n) =
				// sum(每个KPI的履职评估得分*对应的权重分值)/sum(每个KPI的权重)
				realscore2 = Double.parseDouble(df_6.format(realscore2));
				if ("-".equals(str_checkscore2)) {
					sqlBuilder3.putFieldValue("checkscore2", "-");// 某个指标的履职加权分
					sqlBuilder3.putFieldValue("totalscore", realscore + "");// 岗位价值加权总分
				} else {
					sqlBuilder3.putFieldValue("checkscore2", realscore2 + "");// 某个指标的履职加权分
					double realtotalscore = realscore * realscore2 / 10;// 岗位价值加权总分
					realtotalscore = Double.parseDouble(df_6.format(realtotalscore));
					sqlBuilder3.putFieldValue("totalscore", realtotalscore + "");
				}
				sqlBuilder3.putFieldValue("scorecount", (String) postMap.get(postid));
				sqlList.add(sqlBuilder3.getSQL());
				score1 = 0;
				score2 = 0;
				totalweight = 0;
				countnum = 0;
			}
		}
		getDmo().executeBatchByDSNoLog(null, sqlList);
	}

	// 获得岗位价值评估进度统计对象【李春娟/2013-10-31】
	public BillCellVO getPostEvalScheduleVO(String _planid) throws Exception {
		HashVO[] planvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_eval_plan where id =" + _planid);
		if (planvos == null || planvos.length == 0) {
			return getMSGCellVO("未查询到相应评估信息");
		}
		String planname = planvos[0].getStringValue("planname");
		String posts = planvos[0].getStringValue("postids");

		if (posts == null || "".equals(posts)) {
			return getMSGCellVO("未查询到相应被评岗位");
		}
		SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();// 所有的cell格子对象
		BillCellItemVO[] planitemvo = new BillCellItemVO[1];
		planitemvo[0] = getBillTitleCellItemVO(planname);
		planitemvo[0].setSpan("1,5");

		BillCellItemVO[] titleRowVO = new BillCellItemVO[5];
		titleRowVO[0] = getBillTitleCellItemVO("评分人部门");
		titleRowVO[1] = getBillTitleCellItemVO("评分人");
		titleRowVO[2] = getBillTitleCellItemVO("评分状态");
		titleRowVO[3] = getBillTitleCellItemVO("岗位评估情况");
		titleRowVO[4] = getBillTitleCellItemVO("履职评估情况");

		HashVO[] scores = getDmo().getHashVoArrayByDS(null, "select scoredeptname,scoreuser,username,status,checkscore,checkscore2  from sal_post_eval_score where planid=" + _planid + " order by scoredeptid,scoreuser");
		int count1 = 0;// 岗位评估已评个数
		int totalcount1 = 0;// 岗位评估总个数
		int count2 = 0;// 履职评估已评个数
		int totalcount2 = 0;// 履职评估总个数
		List<BillCellItemVO[]> cellVOs1 = new ArrayList<BillCellItemVO[]>();// 所有的未评分cell格子对象
		List<BillCellItemVO[]> cellVOs2 = new ArrayList<BillCellItemVO[]>();// 所有的评分中cell格子对象
		List<BillCellItemVO[]> cellVOs3 = new ArrayList<BillCellItemVO[]>();// 所有的已完成cell格子对象
		int sum_count1 = 0;// 未评分状态的个数
		int sum_count2 = 0;// 评分中状态的个数
		int sum_count3 = 0;// 已完成状态的个数

		for (int i = 0; i < scores.length; i++) {
			String checkscore = scores[i].getStringValue("checkscore");
			String checkscore2 = scores[i].getStringValue("checkscore2");
			if (checkscore != null && !"".equals(checkscore)) {
				count1++;
			}
			totalcount1++;
			if (checkscore2 == null || "".equals(checkscore2)) {
				totalcount2++;
			} else if (!"-".equals(checkscore2)) {
				totalcount2++;
				count2++;
			}
			if ((i != scores.length - 1 && !scores[i].getStringValue("scoreuser").equals(scores[i + 1].getStringValue("scoreuser"))) || (i == scores.length - 1)) {
				BillCellItemVO[] rowVO = new BillCellItemVO[5];
				rowVO[0] = salaryTBUtil.getBillCellVO_Blue(scores[i].getStringValue("scoredeptname"));
				rowVO[1] = salaryTBUtil.getBillCellVO_Normal(scores[i].getStringValue("username"));
				rowVO[3] = salaryTBUtil.getBillCellVO_Normal(count1 + "/" + totalcount1);
				rowVO[4] = salaryTBUtil.getBillCellVO_Normal(count2 + "/" + totalcount2);
				if ("已提交".equals(scores[i].getStringValue("status"))) {
					rowVO[2] = salaryTBUtil.getBillCellVO_Normal("已完成");
					rowVO[2].setForeground(getColorByState("已完成"));
					cellVOs3.add(rowVO);
					sum_count3++;
				} else if (count1 == 0 && count2 == 0) {
					rowVO[2] = salaryTBUtil.getBillCellVO_Normal("未评分");
					rowVO[2].setForeground(getColorByState("未评分"));
					cellVOs1.add(rowVO);
					sum_count1++;
				} else {
					rowVO[2] = salaryTBUtil.getBillCellVO_Normal("评分中");
					rowVO[2].setForeground(getColorByState("评分中"));
					cellVOs2.add(rowVO);
					sum_count2++;
				}
				count1 = 0;
				totalcount1 = 0;
				count2 = 0;
				totalcount2 = 0;
			}
		}
		List<BillCellItemVO[]> sumCellVOs = new ArrayList<BillCellItemVO[]>();// 评分状态统计的cell格子对象
		BillCellItemVO[] rowVO = new BillCellItemVO[5];
		rowVO[0] = getBillTitleCellItemVO("评分状态");
		rowVO[1] = getBillTitleCellItemVO("数量");
		sumCellVOs.add(rowVO);

		rowVO = new BillCellItemVO[2];
		rowVO[0] = salaryTBUtil.getBillCellVO_Normal("未评分");
		rowVO[0].setForeground(getColorByState("未评分"));
		rowVO[1] = salaryTBUtil.getBillCellVO_Normal(sum_count1 + "");
		sumCellVOs.add(rowVO);

		rowVO = new BillCellItemVO[2];
		rowVO[0] = salaryTBUtil.getBillCellVO_Normal("评分中");
		rowVO[0].setForeground(getColorByState("评分中"));
		rowVO[1] = salaryTBUtil.getBillCellVO_Normal(sum_count2 + "");
		sumCellVOs.add(rowVO);

		rowVO = new BillCellItemVO[2];
		rowVO[0] = salaryTBUtil.getBillCellVO_Normal("已完成");
		rowVO[0].setForeground(getColorByState("已完成"));
		rowVO[1] = salaryTBUtil.getBillCellVO_Normal(sum_count3 + "");
		sumCellVOs.add(rowVO);

		rowVO = new BillCellItemVO[2];
		rowVO[0] = getBillTitleCellItemVO("合计");
		rowVO[0].setBackground("240,240,0");
		rowVO[1] = getBillTitleCellItemVO((sum_count1 + sum_count2 + sum_count3) + "");
		rowVO[1].setBackground("240,240,0");
		sumCellVOs.add(rowVO);

		cellVOs.add(planitemvo);
		cellVOs.addAll(sumCellVOs);
		cellVOs.add(titleRowVO);

		cellVOs.addAll(cellVOs1);// 添加未评分的记录
		cellVOs.addAll(cellVOs2);// 添加评分中的记录
		cellVOs.addAll(cellVOs3);// 添加已完成的记录
		BillCellVO cellVO = new BillCellVO();
		cellVO.setCellItemVOs(cellVOs.toArray(new BillCellItemVO[0][0]));
		cellVO.setRowlength(cellVOs.size());
		cellVO.setCollength(titleRowVO.length);
		return cellVO;
	}

	private BillCellVO getMSGCellVO(String _msg) {
		BillCellItemVO[][] cellitemvos = new BillCellItemVO[1][1];
		cellitemvos[0][0] = getBillTitleCellItemVO(_msg);
		cellitemvos[0][0].setColwidth("350");
		BillCellVO cellVO = new BillCellVO();
		cellVO.setCellItemVOs(cellitemvos);
		cellVO.setRowlength(1);
		cellVO.setCollength(1);
		return cellVO;
	}

	// 获得岗位价值评估得分统计数据【李春娟/2013-10-31】
	public BillCellVO getPostEvalScoreVO(String _planid) throws Exception {
		HashVO[] planvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_eval_plan where id =" + _planid);
		if (planvos == null || planvos.length == 0) {
			return getMSGCellVO("未查询到相应评估信息");
		}
		String planname = planvos[0].getStringValue("planname");
		String posts = planvos[0].getStringValue("postids");

		if (posts == null || "".equals(posts)) {
			return getMSGCellVO("未查询到相应被评岗位");
		}
		SalaryTBUtil salaryTBUtil = new SalaryTBUtil();
		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();// 所有的cell格子对象
		BillCellItemVO[] planitemvo = new BillCellItemVO[1];
		planitemvo[0] = getBillTitleCellItemVO(planname);
		planitemvo[0].setSpan("1,7");

		BillCellItemVO[] titleRowVO = new BillCellItemVO[7];
		titleRowVO[0] = getBillTitleCellItemVO("岗位分类");
		titleRowVO[1] = getBillTitleCellItemVO("被评岗位");
		titleRowVO[2] = getBillTitleCellItemVO("测评人数");
		titleRowVO[3] = getBillTitleCellItemVO("岗位等级加权分");
		titleRowVO[4] = getBillTitleCellItemVO("岗位履职加权分");
		titleRowVO[5] = getBillTitleCellItemVO("岗位价值加权总分");
		titleRowVO[6] = getBillTitleCellItemVO("查看详细");

		HashVO[] scores = getDmo().getHashVoArrayByDS(null, "select t3.shortname,t2.name,t1.scorecount,t1.checkscore,t1.checkscore2,t1.totalscore,t1.postid,t1.planid from sal_post_eval_score_total t1 left join pub_post t2 on t1.postid=t2.id left join pub_corp_dept t3 on t2.deptid=t3.id where t1.planid =" + _planid + " order by t3.linkcode,t2.seq,t2.code;");
		List<BillCellItemVO[]> cellVOs1 = new ArrayList<BillCellItemVO[]>();// 所有的未评分cell格子对象
		List<BillCellItemVO[]> cellVOs2 = new ArrayList<BillCellItemVO[]>();// 所有的评分中cell格子对象

		cellVOs.add(planitemvo);
		cellVOs.add(titleRowVO);

		DecimalFormat df_3 = new DecimalFormat("#.000");// 这里保留3位小数
		df_3.setMinimumIntegerDigits(1);// 设置整数部分至少一位

		SalaryTBUtil salTBUtil = new SalaryTBUtil();
		for (int i = 0; i < scores.length; i++) {
			BillCellItemVO[] rowVO = new BillCellItemVO[7];
			rowVO[0] = salTBUtil.getBillCellVO_Blue(scores[i].getStringValue("shortname"));
			rowVO[1] = salTBUtil.getBillCellVO_Blue(scores[i].getStringValue("name"));
			rowVO[2] = salTBUtil.getBillCellVO_Normal(scores[i].getStringValue("scorecount"));
			rowVO[3] = salTBUtil.getBillCellVO_Normal(df_3.format(Double.parseDouble(scores[i].getStringValue("checkscore"))));

			rowVO[5] = salTBUtil.getBillCellVO_Normal(df_3.format(Double.parseDouble(scores[i].getStringValue("totalscore"))));
			rowVO[6] = salTBUtil.getBillCellVO_Normal("查看详细");
			rowVO[6].setForeground("0,0,255");
			rowVO[6].setCellkey(scores[i].getStringValue("planid") + "-" + scores[i].getStringValue("postid"));
			rowVO[6].setIshtmlhref("Y");
			if ("-".equals(scores[i].getStringValue("checkscore2"))) {
				rowVO[4] = salTBUtil.getBillCellVO_Normal("-");
				cellVOs2.add(rowVO);
			} else {
				rowVO[4] = salTBUtil.getBillCellVO_Normal(df_3.format(Double.parseDouble(scores[i].getStringValue("checkscore2"))));
				cellVOs1.add(rowVO);
			}
		}

		cellVOs.addAll(cellVOs1);// 添加岗位价值评估和履职评估都需要评的记录
		cellVOs.addAll(cellVOs2);// 添加只有岗位价值评估的记录
		BillCellVO cellVO = new BillCellVO();
		cellVO.setCellItemVOs(cellVOs.toArray(new BillCellItemVO[0][0]));
		cellVO.setRowlength(cellVOs.size());
		cellVO.setCollength(titleRowVO.length);
		return cellVO;
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// 获得岗位价值评估得分统计详细数据--某个岗位得分详情【李春娟/2013-10-31】
	public BillCellVO getPostEvalScoreDetailVO(String _planid, String _postid) throws Exception {
		// 1.查询评分人
		HashVO[] scoreUserVOs = getDmo().getHashVoArrayByDS(null, "select scoredeptname,scoreuser,username from sal_post_eval_score where planid=" + _planid + " and postid=" + _postid + " group by scoredeptname,scoreuser,username");
		int usernum = scoreUserVOs.length;

		// 2.指标一共几级
		String str_targetlevel = getDmo().getStringValueByDS(null, "select max(length(linkcode)) from sal_post_eval_target_copy where planid=" + _planid);
		int targetlevel = Integer.parseInt(str_targetlevel) / 4;

		// 3.指标末级节点有几个
		String targetnum = getDmo().getStringValueByDS(null, "select count(id) from sal_post_eval_target_copy where planid=" + _planid + " and id not in(select distinct(parentid) from sal_post_eval_target_copy where planid=" + _planid + " and parentid is not null) ");
		int evaltargetnum = Integer.parseInt(targetnum);

		int colLength = evaltargetnum * 2 + 1;// 总列数
		int rowLength = usernum + targetlevel + 2; // 总行数

		SalaryTBUtil salaryTBUtil = new SalaryTBUtil();

		// 5.构造和添加指标表头及权重等
		List<BillCellItemVO> firstRow = new ArrayList<BillCellItemVO>();
		List<BillCellItemVO> weightRow = new ArrayList<BillCellItemVO>();// 最后一级有权重
		List<BillCellItemVO> evaltypeRow = new ArrayList<BillCellItemVO>();// 最后一级有评估类型：岗位评估、履职评估

		firstRow.add(salaryTBUtil.getBillCellVO_Blue("评价因素", targetlevel + ",1"));

		weightRow.add(salaryTBUtil.getBillCellVO_Blue("所占权重"));// 权重行

		evaltypeRow.add(salaryTBUtil.getBillCellVO_Blue("评分人"));

		HashVO[] targetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_eval_target_copy where planid=" + _planid + " order by linkcode");

		HashMap rowMap = new HashMap();
		rowMap.put(1, firstRow);
		for (int i = 2; i <= targetlevel; i++) {// 有几层就加几个List
			List<BillCellItemVO> tmpRow = new ArrayList<BillCellItemVO>();
			rowMap.put(i, tmpRow);
		}

		ArrayList lasttargetList = new ArrayList();
		int tmp_lastnum = 0;// 前面有几个末级节点
		for (int i = 0; i < targetvos.length; i++) {
			BillCellItemVO cell_1 = salaryTBUtil.getBillTargetCellItemVO(targetvos[i].getStringValue("targetname")); // 显示指标名称
			String linkcode = targetvos[i].getStringValue("linkcode");
			int colnum = 1;
			for (int j = i + 1; j < targetvos.length; j++) {
				if (targetvos[j].getStringValue("linkcode").startsWith(linkcode)) {
					if (j == targetvos.length - 1) {
						colnum++;
						break;
					} else if (!targetvos[j + 1].getStringValue("linkcode").startsWith(targetvos[j].getStringValue("linkcode"))) {
						colnum++;
					}
				} else {
					break;
				}
			}

			boolean islastNode = false;
			if (colnum == 1) {
				tmp_lastnum++;// 前面有几个末级节点
				islastNode = true;
				colnum = 2;
				cell_1.setSpan("1," + 2);// 合并列
				lasttargetList.add(targetvos[i].getStringValue("id"));// 记录末级指标id
			} else {
				cell_1.setSpan("1," + (colnum * 2 - 2));// 合并列
			}
			int tmp_level = linkcode.length() / 4;
			List<BillCellItemVO> tmpList = (List<BillCellItemVO>) rowMap.get(tmp_level);

			if (tmpList.size() == 0) {// 如果第一次找到，先添加分类和评价因素合并的空格
				tmpList.add(salaryTBUtil.getBillCellVO_Blue(""));//
				for (int j = 1; j < tmp_lastnum; j++) {
					tmpList.add(salaryTBUtil.getBillTargetCellItemVO(""));//
				}
			}
			tmpList.add(cell_1);
			for (int j = 1; j < colnum * 2 - 2; j++) {
				tmpList.add(salaryTBUtil.getBillTargetCellItemVO(""));
			}
			if (islastNode) {
				if (targetlevel != tmp_level) {
					for (int j = tmp_level + 1; j <= targetlevel; j++) {
						List<BillCellItemVO> nextlist = (List<BillCellItemVO>) rowMap.get(j);
						if (nextlist.size() == 0) {
							nextlist.add(salaryTBUtil.getBillCellVO_Blue(""));//
						}
						nextlist.add(salaryTBUtil.getBillTargetCellItemVO(""));//
					}
					cell_1.setSpan((targetlevel - tmp_level + 1) + ",2");
				}
				addWeightAndEvaltype(targetvos[i], weightRow, evaltypeRow);
			}
		}

		List<BillCellItemVO[]> cellVOs = new ArrayList<BillCellItemVO[]>();// 所有的cell格子对象

		for (int i = 1; i <= rowMap.size(); i++) {
			List<BillCellItemVO> tmpRow = (List<BillCellItemVO>) rowMap.get(i);
			cellVOs.add(tmpRow.toArray(new BillCellItemVO[0]));
		}
		cellVOs.add(weightRow.toArray(new BillCellItemVO[0]));// 最后一级有权重
		cellVOs.add(evaltypeRow.toArray(new BillCellItemVO[0]));// 最后一级有评估类型：岗位评估、履职评估

		// 按机构和人员排序，获得某计划某岗位的所有指标的评分
		HashVO[] scorevos = getDmo().getHashVoArrayByDS(null, "select scoredeptname,username,scoreuser,targetid,checkscore,checkscore2 from sal_post_eval_score where planid=" + _planid + " and postid=" + _postid + " order by scoredeptid,scoreuser");
		// 6.添加岗位名称及评分

		DecimalFormat df_3 = new DecimalFormat("#.000");// 保留3位小数
		df_3.setMinimumIntegerDigits(1);// 设置整数部分至少一位

		for (int i = 1; i <= usernum; i++) {
			List<BillCellItemVO> row_N_VO = new ArrayList<BillCellItemVO>();
			row_N_VO.add(salaryTBUtil.getBillCellVO_Blue(scoreUserVOs[i - 1].getStringValue("username")));
			String userid = scoreUserVOs[i - 1].getStringValue("scoreuser");
			for (int j = 2; j <= colLength; j++) {
				BillCellItemVO cell_N = salaryTBUtil.getBillCellVO_Normal("");// 显示分数
				String targetid = (String) lasttargetList.get((j - 2) / 2);// 从第三列开始是评分表格
				for (int k = 0; k < scorevos.length; k++) {
					if (userid.equals(scorevos[k].getStringValue("scoreuser")) && targetid.equals(scorevos[k].getStringValue("targetid"))) {// 如果岗位id和指标id都匹配，则找到该条记录的得分
						if ((j - 2) % 2 == 0) {
							cell_N.setCellvalue(scorevos[k].getStringValue("checkscore"));
						} else {
							cell_N.setCellvalue(scorevos[k].getStringValue("checkscore2"));
						}
						break;
					}
				}
				row_N_VO.add(cell_N);
			}
			cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));
		}

		// 添加合计行
		HashVO[] targetScoreVOs = getDmo().getHashVoArrayByDS(null, "select targetid,checkscore,checkscore2 from sal_post_eval_score_statistics where planid=" + _planid + " and postid=" + _postid);

		List<BillCellItemVO> row_N_VO = new ArrayList<BillCellItemVO>();
		BillCellItemVO hj_itemVO = salaryTBUtil.getBillCellVO_Blue("合计");
		hj_itemVO.setBackground("255,255,0");
		hj_itemVO.setFontstyle("1");
		row_N_VO.add(hj_itemVO);

		for (int j = 2; j <= colLength; j++) {
			BillCellItemVO cell_N = salaryTBUtil.getBillCellVO_Normal("");// 显示分数
			cell_N.setBackground("255,255,0");
			String targetid = (String) lasttargetList.get((j - 2) / 2);// 从第二列开始是评分表格
			for (int k = 0; k < targetScoreVOs.length; k++) {
				if (targetid.equals(targetScoreVOs[k].getStringValue("targetid"))) {// 如果岗位id和指标id都匹配，则找到该条记录的得分
					if ((j - 2) % 2 == 0) {
						cell_N.setCellvalue(df_3.format(Double.parseDouble(targetScoreVOs[k].getStringValue("checkscore"))));
					} else if ("-".equals(targetScoreVOs[k].getStringValue("checkscore2"))) {
						cell_N.setCellvalue("-");
					} else {
						cell_N.setCellvalue(df_3.format(Double.parseDouble(targetScoreVOs[k].getStringValue("checkscore2"))));
					}
					break;
				}
			}
			row_N_VO.add(cell_N);
		}
		cellVOs.add(row_N_VO.toArray(new BillCellItemVO[0]));

		BillCellVO cellVO = new BillCellVO();
		cellVO.setCellItemVOs(cellVOs.toArray(new BillCellItemVO[0][0]));
		cellVO.setRowlength(cellVOs.size());
		cellVO.setCollength(colLength);
		return cellVO;
	}

	/** ========================年度经营计划--lcj================================== */
	// 复制年度经营计划【李春娟/2014-01-07】
	public void copyOperatePlan(String _planid, String _newplanid) throws Exception {
		new SalaryOperatePlanDMO().copyOperatePlan(_planid, _newplanid);
	}

	// 获得年度经营计划之完成率【李春娟/2014-01-09】
	public HashVO[] getOperatePlanFullfillRate(String _planid, String _months) throws Exception {
		return new SalaryOperatePlanDMO().getOperatePlanFullfillRate(_planid, _months);
	}

	/**
	 * 获得反馈信息
	 */
	public List getFeedbackDataHashvo(String sql, int[] rowArea) throws Exception {
		return new FeedBackDMO().getFeedbackDataHashvo(sql, rowArea);
	}

	// 计算岗位职责指标结果。
	public HashMap calcPostDutyTargetScore(HashVO planvo, String state) throws Exception {
		return new PostDutyCheckDMO().calcPostDutyTargetScore(planvo, state);
	}

	/*
	 * 进度查询。
	 * 
	 */
	public BillCellVO getPostDutyCheckProcess(String logid) throws Exception {
		return new PostDutyCheckDMO().getPostDutyCheckProcess(logid);
	}

	public BillCellVO getPostDutyCheckCompute(String logid) throws Exception {
		return new PostDutyCheckDMO().getPostDutyCheckCompute(logid);
	}

	public BillCellVO getPostDutyDetailCompute(String logid, String checkeduserid) throws Exception {
		return new PostDutyCheckDMO().getPostDutyDetailCompute(logid, checkeduserid);
	}

	@Override
	public String calcOneDeptTargetDL(HashVO targetVO, String selectDate) throws Exception {
		return new SalaryFormulaDMO().calcOneDeptTargetDL(targetVO, selectDate);
	};

	@Override
	public HashVO [] calcOneDeptTargetDL2(HashVO targetVO, String selectDate) throws Exception {
		return new SalaryFormulaDMO().calcOneDeptTargetDL2(targetVO, selectDate);
	};
	public BillCellVO getDeptTargetQueryCellVO(HashMap hashMap) throws Exception {
		return new TargetDMO().getDeptTargetQueryCellVO(hashMap);
	}

	public BillCellVO getPersonPostTargetQueryCellVO(HashMap hashMap) throws Exception {
		return new TargetDMO().getPersonPostTargetQueryCellVO(hashMap);
	}

	public BillCellVO getPostMoneyDif(String _checkdate, String factor[]) throws Exception {
		return new cn.com.pushworld.salary.bs.dmo.ReportDataBuilderDMO().getPersonSalaryDif(_checkdate, factor);
	}

	public HashMap checkDataByPolicy(HashVO[] data, String excelID) throws Exception {
		return new ImportExcelDMO().checkExcelDataByPolicy(data, excelID);
	}

	public void createTableByConfig(String _dataIFCmainID) throws Exception {
		new DataInterfaceDMO().createTableByConfig(_dataIFCmainID);
	}

	public void readDataFromFile(String dataIFCmainID, String _currfolder) throws Exception {
		new DataInterfaceDMO().readDataFromFile(dataIFCmainID, _currfolder, "");
	}

	public String[][] compareDBTOConfig(HashVO hashVO, HashVO[] hashVOs) throws Exception {
		return new DataInterfaceDMO().compareDBTOConfig(hashVO, hashVOs);
	}

	public void convertIFCDataToReportByHand(String mainid, String datadate) throws Exception {
		new DataInterfaceDMO().convertIFCDataToReportByHand(mainid, datadate);
	}

	public BillCellVO getJJTargetDeptJXMoney(String logid, String[] deptids, String[] _noJoinStationkind) throws Exception {
		return new TargetDMO().getJJTargetDeptJXMoney(logid, deptids, _noJoinStationkind);
	}
            //zzl[2018-11-23]批量同步數據接口
	@Override
	public String impReadDataFromFile(String dataIFCmainID, String datadate)throws Exception {
		
		return new DataInterfaceDMO().impReadDataFromFile(dataIFCmainID, datadate, "");
	}
//zzl[2019-3-13]数据上传不显示校验模板
	@Override
	public String impDataUpload(HashMap[] exceldatas,String table_name) {
		StringBuilder sb=new StringBuilder();
		ArrayList list_sqls=new ArrayList<String>();
		int rownum = (Integer) exceldatas[0].get("rownum");
		int colnum = (Integer) exceldatas[0].get("colnum");
		String[] strs = getColname(colnum); // excel表头字母组
		HashMap hm_fl = getFiledLength(exceldatas[0]); // excel每列数据长度
		String table_sql = creatTable(strs, hm_fl, null,table_name); // 建表语句
		list_sqls.add(table_sql);
		// 构建数据sql
		InsertSQLBuilder sb_excel_data = new InsertSQLBuilder(table_name);
		for (int i = 0; i < rownum; i++) {
			for (int j = 0; j < colnum; j++) {
				String data = "" + exceldatas[0].get(i + "_" + j);
				sb_excel_data.putFieldValue(strs[j], data);
			}
			list_sqls.add(sb_excel_data.getSQL());
		}
		try {
			getDmo().executeBatchByDS(null, list_sqls);
			sb.append("导入成功");
		} catch (Exception e) {
			sb.append("导入失败");
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * zzl[2018-11-30]
	 * @param l
	 * @return
	 *  获取excel表头字母组
	 */
	public String[] getColname(int l) {
		String[] strs = new String[l];
		for (int i = 0; i < l; i++) {
			strs[i] = getColumnName(i + 1); // 0开头
		}
		return strs;
	}
	/**
	 * zzl[2018-11-30]
	 * @param exceldata
	 * @return
	 */
	// 获取excel每列数据长度
	private HashMap getFiledLength(HashMap exceldata) {
		HashMap filed = new HashMap();

		int rownum = (Integer) exceldata.get("rownum");
		int colnum = (Integer) exceldata.get("colnum");
		String[] strs = getColname(colnum);
		for (int i = 0; i < rownum; i++) {
			if (i == 0) {
				for (int j = 0; j < colnum; j++) {
					filed.put(strs[j], 50);
				}
 			}
			for (int j = 0; j < colnum; j++) {
				String excel_value = "" + exceldata.get(i + "_" + j);
				int l = (Integer) filed.get(strs[j]);

				int length = 0;
				try {
					length = excel_value.toString().getBytes("GBK").length;
				} catch (Exception e) {
					length = new TBUtil().getStrUnicodeLength(excel_value.toString());
				}

				if (length > l) {
					filed.put(strs[j], length);
				}
			}
		}
		return filed;
	}

	/**
	 * zzl[2018-11-30]
	 * @param strs
	 * @param hm
	 * @param seq
	 * @param tablename
	 * @return
	 */
		// 建表语句
		private String creatTable(String[] strs, HashMap hm, String seq,String tablename) {
			StringBuffer sb = new StringBuffer();
			sb.append("create table " + tablename + "(");
			for (int i = 0; i < strs.length; i++) {
				String l = "" + hm.get(strs[i]);
				if ((i + 1) == strs.length) {
					sb.append(strs[i] + " varchar(" + l + ") ");
				} else {
					sb.append(strs[i] + " varchar(" + l + "), ");
				}
			}
			sb.append(")");
			return sb.toString();
		}
		
		/**
		 * zzl[2018-11-30]
		 * 	// 将一个数字转化为字母 1-A
		 * @param columnNum
		 * @return
		 */
			public String getColumnName(int columnNum) {
				String result = "";

				int first;
				int last;
				if (columnNum > 256)
					columnNum = 256;
				first = columnNum / 27;
				last = columnNum - (first * 26);

				if (first > 0)
					result = String.valueOf((char) (first + 64));

				if (last > 0)
					result = result + String.valueOf((char) (last + 64));

				return result.toUpperCase();
			}
	//2020年7月12日12:48:28  fj   岗位工资年度汇总

	@Override
	public HashVO[] getPostGatherSalary(String[] checkids,String[] types_id, String planway, String dept)
			throws Exception {

		if (checkids != null && checkids.length > 0) {
			StringBuffer sb_sql = new StringBuffer();

			String finalres = "";
			String conne_finalres = "";
			String logids = "";
			for (int i = 4; i < checkids.length; i++) {
				if (i == 4) {
					logids += "'" + checkids[i].replace(",", "','") + "'";
				} else {
					logids += ",'" + checkids[i].replace(",", "','") + "'";
				}

				for (int j = 0; j < types_id.length; j++) {
					finalres += ", ROUND(sum(a" + i + "_" + j + ".sum),2) result_a" + i + "_" + j;
					conne_finalres += ",result_a" + i  + "_" + j;
				}
			}

//				sb_sql.append("select a.stationkind" + finalres);
			sb_sql.append("select a.stationkind,b.b result_a0_0,b.c result_a1_0,b.d result_a2_0,b.e result_a3_0 "+conne_finalres+" from ");
			sb_sql.append("(select a.stationkind" + finalres);

			sb_sql.append(" from (select * from v_sal_personinfo where PLANWAY='"+planway+"' and STATIONKIND not in('前台人员','中层管理')) a ");
			for (int i = 4; i < checkids.length; i++) {
				for (int j = 0; j < types_id.length; j++) {
					sb_sql.append(" left join (select userid,sum(factorvalue) sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
				}
			}
			sb_sql.append(" where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in (" + logids + ")) " + "group by a.stationkind order by a.stationkind)");//order by a.code,a.stationkind
			sb_sql.append(" a left join excel_tab_113 b  on a.stationkind=b.a");

//				String sql = "select a.stationkind,b.b result_a0_0,b.c result_a1_0,b.d result_a2_0,b.e result_a3_0, a.result_a0_0 result_a4_0 from (select a.stationkind, ROUND(sum(a0_0.sum),2) result_a0_0 from (select * from v_sal_personinfo where PLANWAY='在册' and STATIONKIND not in('前台人员','中层管理')) a  left join (select userid,sum(factorvalue) sum from sal_salarybill_detail where salarybillid in('2933') and factorid='10342' group by userid) a0_0 on a0_0.userid = a.id  where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in ('2933')) group by a.stationkind order by a.stationkind) a left join excel_tab_113 b  on a.stationkind=b.a";

			return getDmo().getHashVoArrayByDS(null, sb_sql.toString());
		}
		return null;

	}
}
