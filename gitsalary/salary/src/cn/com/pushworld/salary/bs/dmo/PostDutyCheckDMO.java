package cn.com.pushworld.salary.bs.dmo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.pushworld.salary.bs.SalaryFormulaDMO;
import cn.com.pushworld.salary.to.SalaryFomulaParseUtil;
import cn.com.pushworld.salary.to.SalaryTBUtil;

/**
 * 岗位职责评议服务器段通用方法。
 * @author haoming
 * create by 2014-3-25
 * zzl
 */
public class PostDutyCheckDMO extends SalaryAbstractCommDMO {

	/**
	 * 创建岗责评价指标评分表
	 * 根据岗位职责指标，系统取值、手工录入，系统取值：需要考核前上传excel数据表，手工录入，根据考核策略，生成打分表。
	 * @param sqls
	 * @param param
	 */
	public void createPostDutyScoreTable(List<String> sqls, HashMap param) throws Exception {
		String containsPostDutyCheck = getTb().getSysOptionStringValue("是否包含岗位职责评价功能", "N");
		if ("N".equals(containsPostDutyCheck)) {
			return;
		}
		HashVO hvo[] = getDmo().getHashVoArrayByDS(null, "select * from sal_post_duty_target_list where (state='参与考核' or state is null)"); //取到所有的岗责指标。
		SalaryTBUtil stbutil = new SalaryTBUtil();
		HashMap sort_postids = getDmo().getHashMapBySQLByDS(null, "select stationkind,postid from v_pub_user_post_2 where isdefault='Y' ", true);//找到所有岗位归类对应的实际岗位串
		HashVO targets_postids[] = getDmo().getHashVoArrayByDS(null, "select * from sal_post_duty_check_post where targetid in(" + stbutil.getInCondition(hvo, "id") + ")"); //指标对应的被考核岗位
		HashMap<String, String> post_dept_map = getDmo().getHashMapBySQLByDS(null, "select id,deptid from pub_post"); //找到所有的岗位  by haoming 2016-01-25.为了判断某部门弄下的岗位
		String isupdate = param.get("isupdate") + ""; // 是否是更新操作
		HashMap<String, HashVO> ishave = new HashMap<String, HashVO>(); // 被考核人_考核人_指标_指标类型
		// 唯一的存在
		param.put("person", ishave);
		if ("Y".equals(isupdate)) { // 如果是更新的操作 则将旧数据放到map里判断是否已经存在
			String month = param.get("month") + "";
			HashVO[] vs = getDmo().getHashVoArrayByDS(null, "select * from sal_person_postduty_score where checkdate='" + month + "' and targettype='岗责评价指标'");
			for (int v = 0; v < vs.length; v++) {
				if (ishave != null && vs[v] != null) {
					ishave.put(vs[v].getStringValue("checkeduser") + "_" + vs[v].getStringValue("scoreuser", "") + "_" + vs[v].getStringValue("targetid", ""), vs[v]);
				}
			}
		}
		if (targets_postids != null && targets_postids.length > 0) {
			String postids = null;
			String targetids = null;
			String groupid = null;
			List<String> allposts = new ArrayList<String>();
			String[] realpostid = null;
			for (int q = 0; q < targets_postids.length; q++) {
				postids = targets_postids[q].getStringValue("postid", "");
				String checkedDeptid = targets_postids[q].getStringValue("checkeddepts", ""); //可能配置了某部门下的某岗位归类
				StringBuffer sb = new StringBuffer();
				String[] postid = getTb().split(postids, ";");
				if (postid != null && postid.length > 0) {
					for (int d = 0; d < postid.length; d++) {
						if (postid[d] != null && !"".equals(postid[d])) {
							if (sort_postids.containsKey(postid[d])) {
								realpostid = getTb().split(sort_postids.get(postid[d]) + "", ";");
								if (realpostid != null && realpostid.length > 0) {
									for (int p = 0; p < realpostid.length; p++) {
										if (!checkedDeptid.equals("")) { //不为空，说明配置了某部门下的岗位类型
											String thePostDeptID = post_dept_map.get(realpostid[p]);
											if (!getTb().isEmpty(thePostDeptID)) {
												if (checkedDeptid.contains(";" + thePostDeptID + ";")) { //该岗位在配置中
													allposts.add(realpostid[p]);
													sb.append(";" + realpostid[p]);
												}
											}
										} else {
											allposts.add(realpostid[p]);
											sb.append(";" + realpostid[p]);
										}
									}
								}
							}
						}
					}
				}
				targets_postids[q].setAttributeValue("postid", sb.toString()); // 改成真正的岗位id后面的逻辑就不用改了，为了后来选择岗位归类而改
			}
			HashVO[] postdutytargetvos = getDmo().getHashVoArrayByDS(null, "select * from sal_post_duty_target_list where 1=1  and (state='参与考核' or state is null) order by seq");
			HashMap<String, HashVO> targets_vo = new HashMap<String, HashVO>();
			for (int t = 0; t < postdutytargetvos.length; t++) { // 将指标缓存
				targets_vo.put(postdutytargetvos[t].getStringValue("id"), postdutytargetvos[t]);
			}
			HashMap allpostusers = getDmo().getHashMapBySQLByDS(null, "select postid,userid from v_pub_user_post_2", true); //找到所有岗位的所有人员。
			HashMap post_userids = getDmo().getHashMapBySQLByDS(null, "select mainstationid postid,id userid from  v_sal_personinfo where isuncheck='N' or isuncheck is null  ", true);

			HashVO[] uservos = getDmo().getHashVoArrayByDS(null, "select * from  v_sal_personinfo");
			HashMap<String, HashVO> user_vo = new HashMap<String, HashVO>();
			for (int i = 0; i < uservos.length; i++) {
				user_vo.put(uservos[i].getStringValue("id"), uservos[i]);
			}

			HashMap<String, String> checkuser_userid_targetid = new HashMap<String, String>(); // 一个人对同一指标只能有一条记录
			String checkeduser = null;
			String scoreuser = null;
			String targetid = null;
			String targettype = null;
			String groupweights = null;
			HashMap uncheckuser = (HashMap) param.get("uncheckuser");

			//找人的直属领导。 目前系统中查询的岗位都是按照主岗位查的，如果是兼职情况，就会出问题 --by haoming 2016-01-13
			HashVO postvos[] = getDmo().getHashVoArrayByDS(null, "select * from v_pub_user_post_2 where isdefault='Y' order by deptid,seq,postcode");
			HashMap<String, String> post_leader_map = new HashMap<String, String>(); //找到岗位配置了的直属领导。一般是某部门或者分支机构的一把手会配置。
			HashMap<String, String> dept_leader_map = new HashMap<String, String>();//找到部门的一把手，主要岗位排第一的。
			HashMap<String, String> dept_userids_map = new HashMap<String, String>(); //本部门所有人。
			for (int i = 0; i < postvos.length; i++) {
				String leader = postvos[i].getStringValue("leader");
				String postid = postvos[i].getStringValue("postid");
				if (!getTb().isEmpty(leader)) { //把包含直属领导的岗位放进去。
					String[] users = null;
					if (post_userids.containsKey(getTb().split(leader, ";")[0])) {
						users = getTb().split(String.valueOf(post_userids.get(getTb().split(leader, ";")[0])), ";");
					} else {
						//有些银行可能一个管理人员兼任多个岗位，例如行长助理兼任某部门的经理。这两个岗位同时生效。
						if (allpostusers.containsKey(getTb().split(leader, ";")[0])) {
							users = getTb().split(String.valueOf(allpostusers.get(getTb().split(leader, ";")[0])), ";");
						} else {
							throw new WLTAppException(postvos[i].getStringValue("deptname") + "-" + postvos[i].getStringValue("postname") + "设置的直属领导不存在.");
						}
					}
					if (users.length == 0) {
						throw new WLTAppException(postvos[i].getStringValue("deptname") + "-" + postvos[i].getStringValue("postname") + "设置的直属领导不存在,请重新设置.");
					} else if (users.length > 1) {
						throw new WLTAppException(postvos[i].getStringValue("deptname") + "-" + postvos[i].getStringValue("postname") + "设置的直属领导有多个,请保证唯一性.");
					}
					for (int j = 0; j < users.length; j++) {
						if (!getTb().isEmpty(users[j])) {
							post_leader_map.put(postid, users[j]); //直接放人得了。key=岗位ID，value该岗位的负责领导ID
							break; //跳出，其实这个地方应该先整体验证一把。就是看看岗位有没有人，领导岗位有没有多个人。需要提示。
						}
					}
				}
				String deptid = postvos[i].getStringValue("deptid");
				if (dept_userids_map.containsKey(deptid)) {
					String str_temp = dept_userids_map.get(deptid);
					dept_userids_map.put(deptid, str_temp + ";" + postvos[i].getStringValue("userid")); //在屁股后面继续增加
				} else {
					dept_userids_map.put(deptid, postvos[i].getStringValue("userid")); //向哈希表中塞入...
				}
				//目前：默认部门的领导是主岗位第一的。兼任总经理的并不是本部门打分人。可能需要修改? by haoming 2016-1-13
				if (!dept_leader_map.containsKey(deptid)) {
					//检查此岗位下是否有人。
					if (post_userids.containsKey(postid)) {
						if (post_userids.get(postid) != null) {//岗位下有人
							String[] users = getTb().split(String.valueOf(post_userids.get(postid)), ";");
							for (int j = 0; j < users.length; j++) {
								if (!getTb().isEmpty(users[j])) {
									dept_leader_map.put(deptid, users[j]);
									break; //跳出，其实这个地方应该先整体验证一把。就是看看岗位有没有人，领导岗位有没有多个人。需要提示。
								}
							}
						}
					}
				}

			}

			/**
			 * 本次需要沿用的方案
			 */
			HashMap<String, HashVO> cuseishave = new HashMap<String, HashVO>();
			String plv = param.containsKey("plv") ? param.get("plv").toString() : null;
			String delaytarget[] = getDmo().getStringArrayFirstColByDS(null, "select * from sal_post_duty_target_list where (state='参与考核' or state is null) and checkcycle not in(" + getTb().getInCondition(plv) + ")");

			// 就得沿用上个月的
			String lastmonth = getBackMonth(param.get("month") + "", 1); // 上个月
			HashVO[] cusevs = getDmo().getHashVoArrayByDS(null, "select * from sal_person_postduty_score where checkdate='" + lastmonth + "' and  targettype='岗责评价指标' and targetid in(" + getTb().getInCondition(delaytarget) + ") ");
			if (cusevs != null && cusevs.length > 0) {
				for (int v = 0; v < cusevs.length; v++) {
					cuseishave.put(cusevs[v].getStringValue("checkeduser") + "_" + cusevs[v].getStringValue("scoreuser", "") + "_" + cusevs[v].getStringValue("targetid", ""), cusevs[v]);
				}
			}
			HashMap<String, String[]> target_score_user_cache = new HashMap<String, String[]>(); //把没有选择本部门的进行缓存。
			for (int q = 0; q < targets_postids.length; q++) {
				if (targets_postids[q].getStringValue("postid") != null) {
					postids = targets_postids[q].getStringValue("postid", "");
					targetids = targets_postids[q].getStringValue("targetid");
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
											if (userids[u] != null && !"".equals(userids[u])) {
												checkeduser = userids[u];
												if (uncheckuser != null && uncheckuser.containsKey(checkeduser)) {
													continue;
												}
												scoreuser = "-99999";
												targetid = targetids;
												targettype = "岗责评价指标";
												HashVO targetvo = targets_vo.get(targetids);

												String scoretype = targetvo.getStringValue("dbfromtype", "");
												String scoreusers[] = null;// 实际打分人群
												if ("系统取值".equals(scoretype)) { //
													scoreusers = new String[] { "-99999" };
												} else if ("直属领导".equals(targets_postids[q].getStringValue("type", ""))) {
													String leader_userid = null;
													if (post_leader_map.containsKey(postid[d])) { //看看该岗位有没有配置直属领导
														leader_userid = post_leader_map.get(postid[d]);
													} else if (dept_leader_map.containsKey(user_vo.get(userids[u]).getStringValue("deptid"))) { //部门的一把手。
														leader_userid = dept_leader_map.get(user_vo.get(userids[u]).getStringValue("deptid"));
													}
													if (getTb().isEmpty(leader_userid) || "null".equals(leader_userid)) {
														throw new Exception(user_vo.get(userids[u]).getStringValue("name") + ",找不到直属领导");
													}
													scoreusers = new String[] { leader_userid };
												} else if ("自由选择".equals(targets_postids[q].getStringValue("type", ""))) {
													String scoreuserids = targets_postids[q].getStringValue("userids"); //找到自由选择的打分人员。
													if (scoreuserids != null) {
														scoreusers = getTb().split(scoreuserids, ";");
													}
												} else if ("岗位组".equals(targets_postids[q].getStringValue("type", ""))) {
													String innertype = targets_postids[q].getStringValue("innertype"); //内部过滤条件
													String posttypes = targets_postids[q].getStringValue("posttypes");
													if (TBUtil.isEmpty(posttypes)) {
														throw new Exception(targetvo.getStringValue("name") + "打分人中没有选择岗位归类");
													} else {
														if ("本部门".equals(innertype)) { //如果选择了本部门，先把本部门人都找出来。
															String deptid = user_vo.get(userids[u]).getStringValue("deptid");
															String deptusers = dept_userids_map.get(deptid); //找到本部门人
															if (getTb().isEmpty(deptusers)) {
																String username = user_vo.get(userids[u]).getStringValue("name"); //应该不会执行
																throw new Exception(targetvo.getStringValue("name") + "考核[" + username + "]没找到同部门人,请检查");
															} else {
																String same_deptusers[] = getTb().split(deptusers, ";");
																HashSet<String> scoreUser = new HashSet<String>(); //打分人
																String[] post_types = getTb().split(posttypes, ";");
																for (int i = 0; i < same_deptusers.length; i++) {
																	HashVO scoreuservo = user_vo.get(same_deptusers[i]); //打分人对象
																	for (int j = 0; j < post_types.length; j++) {
																		if (!getTb().isEmpty(post_types[j]) && post_types[j].equals(scoreuservo.getStringValue("stationkind", ""))) {
																			scoreUser.add(scoreuservo.getStringValue("id"));
																		}
																	}
																}
																scoreusers = scoreUser.toArray(new String[0]);
															}
														} else if ("指定部门".equals(innertype)) {
															if (!target_score_user_cache.containsKey(groupid)) {
																String deptids = targets_postids[q].getStringValue("deptids");
																String[] deptids_1 = getTb().split(deptids, ";");
																HashSet<String> scoreUser = new HashSet<String>(); //打分人
																for (int i = 0; i < deptids_1.length; i++) {
																	String deptusers = dept_userids_map.get(deptids_1[i]); //找到本部门人
																	String same_deptusers[] = getTb().split(deptusers, ";");
																	String[] post_types = getTb().split(posttypes, ";");
																	for (int m = 0; m < same_deptusers.length; m++) {
																		HashVO scoreuservo = user_vo.get(same_deptusers[m]); //打分人对象
																		for (int j = 0; j < post_types.length; j++) {
																			if (!getTb().isEmpty(post_types[j]) && post_types[j].equals(scoreuservo.getStringValue("stationkind", ""))) {
																				scoreUser.add(scoreuservo.getStringValue("id"));
																			}
																		}
																	}
																}
																scoreusers = scoreUser.toArray(new String[0]);
																target_score_user_cache.put(groupid, scoreusers);
															} else {
																scoreusers = target_score_user_cache.get(groupid);
															}
														} else { //如果没有选择本部门，找出改岗位类别下的所有岗位的所有人。
															if (!target_score_user_cache.containsKey(groupid)) {
																String[] post_types = getTb().split(posttypes, ";");
																HashSet<String> scoreUser = new HashSet<String>(); //打分
																for (int i = 0; i < post_types.length; i++) {
																	String p_type = post_types[i]; //岗位归类
																	String score_post_ids = (String) sort_postids.get(p_type);
																	String[] score_curr_post_arrays = getTb().split(score_post_ids, ";");
																	for (int j = 0; j < score_curr_post_arrays.length; j++) {
																		String post_users = (String) post_userids.get(score_curr_post_arrays[i]); //找到岗位下 的人
																		if (getTb().isEmpty(post_users)) {
																			String users[] = getTb().split(post_users, ";");
																			for (int k = 0; k < users.length; k++) {
																				scoreUser.add(users[i]);
																			}
																		}
																	}

																}
																scoreusers = scoreUser.toArray(new String[0]);
																target_score_user_cache.put(groupid, scoreusers);
															} else {
																scoreusers = target_score_user_cache.get(groupid);
															}
														}
													}
												} else if ("实体岗位".equals(targets_postids[q].getStringValue("type", ""))) {
													String innertype = targets_postids[q].getStringValue("innertype"); //内部过滤条件
													if ("本部门".equals(innertype)) { //如果选择了本部门，先把本部门人都找出来。
														String deptid = user_vo.get(userids[u]).getStringValue("deptid");
														String deptusers = dept_userids_map.get(deptid); //找到本部门人
														if (getTb().isEmpty(deptusers)) {
															String username = user_vo.get(userids[u]).getStringValue("name"); //应该不会执行
															throw new Exception(targetvo.getStringValue("name") + "考核[" + username + "]没找到同部门人,请检查");
														} else {
															String same_deptusers[] = getTb().split(deptusers, ";"); //同机构下的人
															HashSet<String> scoreUser = new HashSet<String>(); //打分人
															String score_postids = targets_postids[q].getStringValue("postids");
															String[] post_ids = getTb().split(score_postids, ";");
															for (int i = 0; i < same_deptusers.length; i++) {
																HashVO scoreuservo = user_vo.get(same_deptusers[i]); //打分人对象
																for (int j = 0; j < post_ids.length; j++) {
																	if (!getTb().isEmpty(post_ids[j]) && post_ids[j].equals(scoreuservo.getStringValue("mainstation", ""))) {
																		scoreUser.add(scoreuservo.getStringValue("id"));
																	}
																}
															}
															scoreusers = scoreUser.toArray(new String[0]);
														}
													} else if ("指定部门".equals(innertype)) {
														if (!target_score_user_cache.containsKey(groupid)) {
															String deptids = targets_postids[q].getStringValue("deptids");
															String[] deptids_1 = getTb().split(deptids, ";");
															String score_postids = targets_postids[q].getStringValue("postids");
															HashSet<String> scoreUser = new HashSet<String>(); //打分人
															for (int m = 0; m < deptids_1.length; m++) {
																String deptusers = dept_userids_map.get(deptids_1[m]); //找到指定部门人
																String same_deptusers[] = getTb().split(deptusers, ";");
																String[] post_ids = getTb().split(score_postids, ";");
																for (int i = 0; i < same_deptusers.length; i++) {
																	HashVO scoreuservo = user_vo.get(same_deptusers[i]); //打分人对象
																	for (int j = 0; j < post_ids.length; j++) {
																		if (!getTb().isEmpty(post_ids[j]) && post_ids[j].equals(scoreuservo.getStringValue("mainstation", ""))) {
																			scoreUser.add(scoreuservo.getStringValue("id"));
																		}
																	}
																}
															}
															scoreusers = scoreUser.toArray(new String[0]);
															target_score_user_cache.put(groupid, scoreusers);
														} else {
															scoreusers = target_score_user_cache.get(groupid);
														}
													} else {
														if (!target_score_user_cache.containsKey(groupid)) {
															HashSet<String> scoreUser = new HashSet<String>(); //打分人
															String score_postids = targets_postids[q].getStringValue("postids");
															String[] post_ids = getTb().split(score_postids, ";");
															for (int i = 0; i < uservos.length; i++) {
																if (getTb().isEmpty(uservos[i].getStringValue("isuncheck")) || "N".equals(uservos[i].getStringValue("isuncheck"))) {
																	for (int k = 0; k < post_ids.length; k++) {
																		if (!getTb().isEmpty(post_ids[k]) && post_ids[k].equals(uservos[i].getStringValue("mainstation", ""))) {
																			scoreUser.add(uservos[i].getStringValue("id"));
																		}
																	}
																}
															}
															scoreusers = scoreUser.toArray(new String[0]);
															target_score_user_cache.put(groupid, scoreusers);
														} else {
															scoreusers = target_score_user_cache.get(groupid);
														}
													}
												}
												for (int i = 0; i < scoreusers.length; i++) {
													if (!getTb().isEmpty(scoreusers[i])) {
														if (scoreusers[i].equals(userids[u])) { //不能自己给自己打分
															continue;
														}
														scoreuser = scoreusers[i];
													}
													String key = checkeduser + "_" + scoreuser + "_" + targetid;
													if (checkuser_userid_targetid.containsKey(key)) {
														continue;
													}
													if ("Y".equals(isupdate) && ishave != null && ishave.containsKey(key)) {
														targetvo.setAttributeValue("checktype", "");
														targetvo.setAttributeValue("weights", groupweights);
														targetvo.setAttributeValue("scoretype", scoretype);
														if (compareIsCanUpdatePerson(targetvo, ishave.get(key)) || !groupid.equals(ishave.get(key).getStringValue("groupid", ""))) {
															UpdateSQLBuilder isb = new UpdateSQLBuilder("sal_person_postduty_score");
															isb.setWhereCondition(" id=" + ishave.get(key).getStringValue("id"));
															isb.putFieldValue("targetid", targetids);
															isb.putFieldValue("targetname", targetvo.getStringValue("name", ""));
															isb.putFieldValue("checktype", "");
															isb.putFieldValue("targettype", targettype);
															isb.putFieldValue("scoreusertype", "");
															isb.putFieldValue("weights", targetvo.getStringValue("weights", "")); // 指标权重
															isb.putFieldValue("checkdate", param.get("month") + "");
															isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
															isb.putFieldValue("scoretype", scoretype); // 后来在高管考核方案中增加了这个字段
															isb.putFieldValue("groupid", groupid);
															isb.putFieldValue("getvalue", targetvo.getStringValue("getvalue", ""));
															isb.putFieldValue("targetdefine", targetvo.getStringValue("define", ""));
															sqls.add(isb.getSQL());
														}
														ishave.remove(key);
													} else {
														InsertSQLBuilder isb = new InsertSQLBuilder("sal_person_postduty_score");
														isb.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_POSTDUTY_SCORE", 8000));
														isb.putFieldValue("targetid", targetids);
														isb.putFieldValue("targetname", targetvo.getStringValue("name", ""));
														isb.putFieldValue("checktype", "");
														isb.putFieldValue("targettype", targettype);
														isb.putFieldValue("checkeduser", userids[u]);
														String username = user_vo.get(userids[u]).getStringValue("name");
														isb.putFieldValue("checkedusername", username + "");
														if (scoreuser != null && scoreuser.equals(userids[u])) { //不能自己给自己打分
															continue;
														}
														isb.putFieldValue("scoreuser", scoreuser);
														if ((!"系统取值".equals(targetvo.getStringValue("dbfromtype", "")))) {
															try {
																String username_leader = user_vo.get(scoreuser).getStringValue("name");
																isb.putFieldValue("scoreusername", username_leader);
															} catch (Exception ex) {
																System.out.println(scoreuser + "找不到");
																throw ex;
															}
														}

														isb.putFieldValue("scoreusertype", "");
														isb.putFieldValue("weights", groupweights); // 指标权重
														// 后来改成子表中的权重而指标的权重也影藏了
														isb.putFieldValue("checkdate", param.get("month") + "");
														isb.putFieldValue("createdeptid", param.get("logindeptid") + "");
														isb.putFieldValue("logid", param.get("logid") + "");
														isb.putFieldValue("scoretype", targetvo.getStringValue("dbfromtype", "")); // 后来在高管考核方案中增加了这个字段
														isb.putFieldValue("groupid", groupid);
														if ((!"系统取值".equals(targetvo.getStringValue("dbfromtype", ""))) && cuseishave.containsKey(key)) {
															isb.putFieldValue("checkscore", cuseishave.get(key).getStringValue("checkscore", ""));
															isb.putFieldValue("status", "待提交");
														}
														isb.putFieldValue("getvalue", targetvo.getStringValue("getvalue", ""));
														isb.putFieldValue("targetdefine", targetvo.getStringValue("define", ""));
														sqls.add(isb.getSQL());
													}
													checkuser_userid_targetid.put(key, "");
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
		 * 剩下的就是需要删除的评分项
		 */
		if (ishave != null && ishave.size() > 0) {
			String[] keys = (String[]) ishave.keySet().toArray(new String[0]);
			for (int i = 0; i < keys.length; i++) {
				DeleteSQLBuilder dsb = new DeleteSQLBuilder("sal_person_postduty_score");
				dsb.setWhereCondition("id=" + ishave.get(keys[i]).getStringValue("id"));
				sqls.add(dsb.getSQL());
			}
		}
	}

	//计算岗位职责指标结果。
	public HashMap calcPostDutyTargetScore(HashVO planvo, String state) throws Exception {
		String containsPostDutyCheck = getTb().getSysOptionStringValue("是否包含岗位职责评价功能", "N");
		if ("N".equals(containsPostDutyCheck)) {
			return new HashMap();
		}
		String logid = planvo.getStringValue("id");
		String input_value_range = TBUtil.getTBUtil().getSysOptionStringValue("薪酬模块人员打分区间", "0-10");
		String[] values = TBUtil.getTBUtil().split(input_value_range, "-");
		int begin = 0;
		int fullscore = 10;
		if (values.length == 2) {
			begin = Integer.parseInt(values[0]);
			fullscore = Integer.parseInt(values[1]);
			if (begin >= fullscore) { // 如果值前后顺序有问题。
				begin = 0;
				fullscore = 10;
			}
		}
		try {
			HashMap rtn_map = new HashMap();
			String[] unfinishPersonScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_person_postduty_score where scoretype!='系统取值 ' and (status <> '已提交' or status is null) and logid=" + logid);
			if (unfinishPersonScorer.length > 0) {//有未完成的。
				BillCellVO vo = getPostDutyCheckProcess(logid, true);
				rtn_map.put("vo", vo);
				rtn_map.put("res", "fail");
				return rtn_map;
			}
			SalaryFomulaParseUtil formulaUtil = new SalaryFomulaParseUtil();
			HashVO[] hvoperson = getSalaryTBUtil().getHashVoArrayByDS(null, "select t1.*,t3.maindeptid,t3.maindeptid deptid from sal_person_postduty_score t1  left join v_sal_personinfo t3 on t1.checkeduser = t3.id  where 1=1 and t1.scoretype='系统取值 ' and t1.targettype='岗责评价指标' and t1.logid='" + logid + "'");
			List updateSqlList = new ArrayList();
			int personNum = hvoperson.length;
			for (int i = 0; i < hvoperson.length; i++) {
				HashVO scoreVO = hvoperson[i];
				String targetName = scoreVO.getStringValue("targetname"); // 得到指标名称
				String targetFactorName = targetName + "对象";
				String targetdef = scoreVO.getStringValue("targetdefine"); // 公式定义
				String getvalue = scoreVO.getStringValue("getvalue"); // 得到取值的公式
				String operationtype = scoreVO.getStringValue("operationtype"); // 计算方式
				// ,加减
				StringBuffer sb = new StringBuffer();
				formulaUtil.putDefaultFactorVO("数字", getvalue, targetFactorName, "", "4位小数");
				Object currvalue = formulaUtil.onExecute(formulaUtil.getFoctorHashVO(targetFactorName), scoreVO, sb);
				UpdateSQLBuilder sql = new UpdateSQLBuilder("sal_person_postduty_score");
				if (currvalue == null || "".equals(String.valueOf(currvalue))) {
					currvalue = 0;
				}
				sql.putFieldValue("checkscore", String.valueOf(currvalue));
				sql.putFieldValue("status", "已提交");
				sql.putFieldValue("descr", sb.toString());
				sql.setWhereCondition("id='" + scoreVO.getStringValue("id") + "'");
				updateSqlList.add(sql);
				SalaryFormulaDMO.setRemoteActionSchedule(logid, "指标计算", "\r\n岗责评价指标取值进度:" + (i + 1) + "/" + (personNum));
			}
			String stateSql = "update sal_target_check_log set status='" + state + "' where id=" + logid;
			updateSqlList.add(stateSql);
			getDmo().executeBatchByDS(null, updateSqlList, true, false);
			//接下来把定量指标和定性的一同计算。 评议的和取值的。

			HashVO[] dx_dl_alltarget = getSalaryTBUtil().getHashVoArrayByDS(null, "select t1.*,t3.maindeptid,t3.maindeptid deptid from sal_person_postduty_score t1  left join v_sal_personinfo t3 on t1.checkeduser = t3.id  where t1.targettype='岗责评价指标' and t1.logid='" + logid + "'");

			HashMap<String, List<HashVO>> user_scorelist = new HashMap<String, List<HashVO>>();
			for (int i = 0; i < dx_dl_alltarget.length; i++) {
				String checkuser = dx_dl_alltarget[i].getStringValue("checkeduser");
				if (user_scorelist.containsKey(checkuser)) {
					List scorelist = user_scorelist.get(checkuser);
					scorelist.add(dx_dl_alltarget[i]);
				} else {
					List scorelist = new ArrayList<HashVO>();
					scorelist.add(dx_dl_alltarget[i]);
					user_scorelist.put(checkuser, scorelist);
				}
			}
			HashVO alluserhvo[] = getDmo().getHashVoArrayByDS(null, "select * from v_sal_personinfo where id in(" + getTb().getInCondition(user_scorelist.keySet().toArray(new String[0])) + ")  order by deptseq,postseq");
			List sqllist = new ArrayList();
			String deleteSql = "delete from sal_person_check_result where targettype='岗责评价指标' and logid=" + logid;
			sqllist.add(deleteSql);
			for (int i = 0; i < alluserhvo.length; i++) {
				String checkeduserid = alluserhvo[i].getStringValue("id");//被考核人ID
				List scoreList = user_scorelist.get(checkeduserid);
				BigDecimal weights_totle = BigDecimal.ZERO;
				BigDecimal score_totle = BigDecimal.ZERO;
				for (int j = 0; j < scoreList.size(); j++) {
					HashVO scorevo = (HashVO) scoreList.get(j);
					String weight = scorevo.getStringValue("weights", "0");
					String scorevalue = scorevo.getStringValue("checkscore", "0");
					weights_totle = weights_totle.add(new BigDecimal(weight));
					score_totle = score_totle.add(new BigDecimal(scorevalue).multiply(new BigDecimal(weight)));
				}
				if (weights_totle.longValue() > 0) {
					BigDecimal lastvalue = score_totle.divide(weights_totle, 4, BigDecimal.ROUND_HALF_UP);//加权平均最后得分。
					BigDecimal bfzvalue = lastvalue.divide(BigDecimal.valueOf(fullscore)).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_HALF_UP);//百分之
					InsertSQLBuilder insertsql = new InsertSQLBuilder("sal_person_check_result");
					insertsql.putFieldValue("id", getDmo().getSequenceNextValByDS(null, "S_SAL_PERSON_CHECK_RESULT"));
					insertsql.putFieldValue("checkeduserid", checkeduserid);
					insertsql.putFieldValue("finalres", lastvalue.toString());
					insertsql.putFieldValue("finalres2", bfzvalue.toString());
					insertsql.putFieldValue("logid", logid);
					insertsql.putFieldValue("targettype", "岗责评价指标");
					insertsql.putFieldValue("checkeduserdeptname", alluserhvo[i].getStringValue("deptname"));
					insertsql.putFieldValue("checkeduserdeptid", alluserhvo[i].getStringValue("deptid"));
					insertsql.putFieldValue("checkedusername", alluserhvo[i].getStringValue("name"));
					insertsql.putFieldValue("state", "评分完成");
					sqllist.add(insertsql.getSQL());
				}
			}
			getDmo().executeBatchByDS(null, sqllist);
			rtn_map.put("res", "success");
			SalaryFormulaDMO.removeRemoteActionSchedule(logid, "指标计算");
			return rtn_map;
		} catch (Exception ex) {
			SalaryFormulaDMO.removeRemoteActionSchedule(logid, "指标计算");
			throw ex;
		}
	}

	public BillCellVO getPostDutyCheckProcess(String logid) throws Exception {
		return getPostDutyCheckProcess(logid, false);
	}

	/*
	 *isunfinished 是否只显示未完成。
	 */
	private BillCellVO getPostDutyCheckProcess(String logid, boolean isunfinished) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append("select scoreuser,count(id) from sal_person_postduty_score where 1=1 and logid = '" + logid + "' ");
		sql.append(" and (checkscore is null or checkscore='' )");
		sql.append(" group by scoreuser");

		HashMap unchecked = getDmo().getHashMapBySQLByDS(null, sql.toString());

		String[] unfinishPersonScorer = getDmo().getStringArrayFirstColByDS(null, "select distinct scoreuser from sal_person_postduty_score where scoretype!='系统取值 ' and (checkscore is not null and checkscore !='' and ( status <> '已提交' or status is null)) and logid=" + logid);
		for (int i = 0; i < unfinishPersonScorer.length; i++) {
			if (!unchecked.containsKey(unfinishPersonScorer[i])) {
				unchecked.put(unfinishPersonScorer[i], "0");
			}
		}

		String[] allunfinisheduser = (String[]) unchecked.keySet().toArray(new String[0]);
		String incondition = getTb().getInCondition(allunfinisheduser);
		StringBuffer sql2 = new StringBuffer();
		sql2.append("select scoreuser,count(id) from sal_person_postduty_score where 1=1 and logid = '" + logid + "' ");
		if (isunfinished) {
			sql2.append(" and scoreuser in(" + incondition + ")  ");
		}
		sql2.append(" group by scoreuser");
		HashMap all = getDmo().getHashMapBySQLByDS(null, sql2.toString());

		StringBuffer sql3 = new StringBuffer();
		sql3.append("select scoreuser,count(id) from sal_person_postduty_score where 1=1 and status='已提交' and logid = '" + logid + "' ");
		if (isunfinished) {
			sql3.append(" and scoreuser in(" + incondition + ")  ");
		}
		sql3.append(" group by scoreuser");

		HashMap submited = getDmo().getHashMapBySQLByDS(null, sql3.toString());

		if (!isunfinished) {
			incondition = getTb().getInCondition((String[]) all.keySet().toArray(new String[0]));
		}
		HashVO hvo[] = getDmo().getHashVoArrayByDS(null, "select * from v_sal_personinfo where id in(" + incondition + ")  order by deptseq,postseq");
		BillCellVO vo = new BillCellVO();
		vo.setCollength(2);
		List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();

		BillCellItemVO[] _items0 = new BillCellItemVO[4];
		if (isunfinished) {
			_items0[0] = getBillTitleCellItemVO("未完成评分人员");
		} else {
			_items0[0] = getBillTitleCellItemVO("岗责评价指标进度统计");
		}
		_items0[0].setHalign(2);
		_items0[0].setSpan("1,4");
		_items0[1] = getBillTitleCellItemVO("");
		_items0[2] = getBillTitleCellItemVO("");
		_items0[3] = getBillTitleCellItemVO("");
		items.add(_items0);

		BillCellItemVO[] _items1 = new BillCellItemVO[4];
		_items1[0] = getBillTitleCellItemVO("机构");
		_items1[1] = getBillTitleCellItemVO("姓名");
		_items1[2] = getBillTitleCellItemVO("完成比");
		_items1[3] = getBillTitleCellItemVO("状态");
		items.add(_items1);
		for (int i = 0; i < hvo.length; i++) {
			BillCellItemVO[] _items = new BillCellItemVO[4];
			_items[0] = getBillNormalCellItemVO(i, hvo[i].getStringValue("deptname"));
			_items[1] = getBillNormalCellItemVO(i, hvo[i].getStringValue("name"));
			String userid = hvo[i].getStringValue("id");
			if (!unchecked.containsKey(userid) && isunfinished) {
				continue;
			}
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
		vo.setRowlength(items.size());
		vo.setCollength(4);
		BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
		formatClen(allitems);
		formatSpan(allitems, new int[1]);
		vo.setCellItemVOs(allitems);
		return vo;
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
		return false;
	}

	public BillCellVO getPostDutyCheckCompute(String logid) throws Exception {
		return getPostDutyCheckCompute(logid, false);
	}

	private BillCellVO getPostDutyCheckCompute(String logid, boolean isunfinished) throws Exception {
		HashVO hvo[] = getDmo().getHashVoArrayByDS(null, "select t1.* from sal_person_check_result t1 left join v_sal_personinfo t2 on t1.checkeduserid =t2.id where t1.targettype='岗责评价指标' and t1.logid ='" + logid + "'  order by t2.deptseq,t2.postseq");
		BillCellVO vo = new BillCellVO();
		vo.setCollength(2);
		List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();

		BillCellItemVO[] _items0 = new BillCellItemVO[4];
		if (hvo == null || hvo.length == 0) {
			_items0[0] = getBillTitleCellItemVO("未查询到相应考评信息");
			_items0[0].setHalign(2);
			_items0[0].setSpan("1,4");
			_items0[1] = getBillTitleCellItemVO("");
			_items0[2] = getBillTitleCellItemVO("");
			_items0[3] = getBillTitleCellItemVO("");
			items.add(_items0);
			vo.setRowlength(items.size());
			vo.setCollength(4);
			BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
			formatClen(allitems);
			vo.setCellItemVOs(allitems);
		} else {
			_items0[0] = getBillTitleCellItemVO("岗责评价指标考核统计");
			_items0[0].setHalign(2);
			_items0[0].setSpan("1,4");
			_items0[1] = getBillTitleCellItemVO("");
			_items0[2] = getBillTitleCellItemVO("");
			_items0[3] = getBillTitleCellItemVO("");
			items.add(_items0);

			BillCellItemVO[] _items1 = new BillCellItemVO[4];
			_items1[0] = getBillTitleCellItemVO("机构");
			_items1[1] = getBillTitleCellItemVO("姓名");
			_items1[2] = getBillTitleCellItemVO("考核得分");
			_items1[3] = getBillTitleCellItemVO("查看详细");
			items.add(_items1);
			for (int i = 0; i < hvo.length; i++) {
				BillCellItemVO[] _items = new BillCellItemVO[4];
				_items[0] = getBillNormalCellItemVO(i, hvo[i].getStringValue("checkeduserdeptname"));
				_items[1] = getBillNormalCellItemVO(i, hvo[i].getStringValue("checkedusername"));
				_items[2] = getBillNormalCellItemVO(i, hvo[i].getStringValue("finalres"));
				_items[3] = getBillNormalCellItemVO(i, "查看详细");
				_items[3].setIshtmlhref("Y");
				_items[3].setCustProperty("checkeduserid", hvo[i].getStringValue("checkeduserid"));
				_items[3].setCustProperty("logid", logid);
				items.add(_items);
			}
			vo.setRowlength(items.size());
			vo.setCollength(4);
			BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
			formatClen(allitems);
			formatSpan(allitems, new int[1]);
			vo.setCellItemVOs(allitems);
		}
		return vo;
	}

	public BillCellVO getPostDutyDetailCompute(String logid, String checkeduserid) throws Exception {
		HashVO hvo[] = getDmo().getHashVoArrayByDS(null, "select * from sal_person_postduty_score where logid ='" + logid + "' and checkeduser='" + checkeduserid + "'");
		BillCellVO vo = new BillCellVO();
		vo.setCollength(2);
		List<BillCellItemVO[]> items = new ArrayList<BillCellItemVO[]>();
		BillCellItemVO[] _items1 = new BillCellItemVO[3];
		_items1[0] = getBillTitleCellItemVO("指标名称");
		_items1[1] = getBillTitleCellItemVO("考核得分");
		_items1[2] = getBillTitleCellItemVO("考评类型");
		items.add(_items1);
		for (int i = 0; i < hvo.length; i++) {
			BillCellItemVO[] _items = new BillCellItemVO[3];
			_items[0] = getBillNormalCellItemVO(i, hvo[i].getStringValue("targetname"));
			_items[1] = getBillNormalCellItemVO(i, hvo[i].getStringValue("checkscore"));
			_items[2] = getBillNormalCellItemVO(i, hvo[i].getStringValue("scoretype"));
			items.add(_items);
		}
		vo.setRowlength(items.size());
		vo.setCollength(3);
		BillCellItemVO[][] allitems = items.toArray(new BillCellItemVO[0][0]);
		formatClen(allitems);
		formatSpan(allitems, new int[1]);
		vo.setCellItemVOs(allitems);
		return vo;
	}
}
