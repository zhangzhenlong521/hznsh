package cn.com.infostrategy.bs.workflow;

import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Vector;

import cn.com.infostrategy.bs.common.AbstractDMO;
import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.ServerEnvironment;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.mainscreen.MailUtil;
import cn.com.infostrategy.bs.workflow.msg.SendSMSIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.LinkForeignTableDefineVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.common.WLTHashMap;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealActivityVO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.NextTransitionVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.workflow.engine.WorkflowDynamicParticipateIfc;

/**
 * 工作流状态机引擎DMO!!核心关键!!超复杂!!!
 * 对应的核心表有:pub_wf_prinstance(流程实例表),pub_wf_dealpool(流程处理记录池表),
 * 授权就是指某人出差了,不能使用系统了,为了不影响他在流程中的处理，他在系统中做一个授权处理,授权给另外一个人，这样另外这个人进入系统后就能看到所有授权给他的这个人的任务!
 * 
 * 工作流引擎主要中两步:
 * 第一步叫getFirstTaskVO(),即点提交选择人员时计算出参与者,这里复杂主要是要动态根据机构条件与角色计算出对应的人! 比如"流程创建者的{本机构}的{法律合规部}"
 * 第二步叫secondCall(),即根据选中的人,提交至数据库!形成新的任务与待办信息,并把原来的任务搬家至已办任务中!
 * 其中第一步的处理涉及到以下关键类与方法,流程引擎BS端总共涉及到4个类:WorkFlowEngineDMO,WorkFlowBSUtil,WorkFlowEngineGetCorpUtil,DataPolicyDMO
 * WorkFlowEngineDMO.getFirstTaskVO()  //148行,计算第一次选择人!核心逻辑就是首先根据流程图的连线计算条件,找出可以出去的下一环节,然后计算出各环节上的参与者!
 * WorkFlowEngineDMO.getOneActivityAllParticipantUsers()  //182行,计算某个环节上的所有参与者,在这个函数最后进行了控权人的转换!!
 * WorkFlowEngineDMO.getOneActivityParticipanUserByCorpAreaAndRole();  //78行,实际上根据机构与角色的交集计算出其范围中的所有人员
 * WorkFlowBSUtil.getAccredProxyUserVO();  //31行,去授权表中的匹配计算出满足条件的!
 * WorkFlowEngineGetCorpUtil().getCorps()  //41行,根据公式计算出机构清单,包括创建人机构,首次上溯与二次下探!
 * SysDMO.getOnerUserSomeTypeParentCorp()  //20行,计算一个人员的某个机构类型下的所有机构
 * DataPolicyDMO.secondDownFindAllCorpChildrensByCondition()  //150行,最后还是转调到了数据权限的那个最复杂的"上溯/下探"的方法中去了
 * 
 * @author Administrator
 * 
 */
public class WorkFlowEngineDMO extends AbstractDMO {

	private CommDMO commDMO = null; //需要CommDMO

	private TBUtil tBUtil = null; //工具类!!
	private WorkFlowBSUtil wfBSUtil = null; //工作流服务器端的工具类!!
	private WLTInitContext thisInitContext = null; //
	private HashMap allCorpsCacheMap = null; //
	private String sendSMSImpl = getTBUtil().getSysOptionStringValue("工作流短信接口实现类", "");

	/**
	 * 启动流程
	 */
	public String startWFProcess(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO) throws Exception {
		return startWFProcesses(_processid, _billVO, _loginUserId, _startActivityVO, null, null); //
	}

	/**
	 * 启动一个流程,因为第一次流程实例为空,所以启动流程需要特别对待!! 启动流程其实就是创建好流程实例后,立即执行Start环节!!!
	 */
	public String startWFProcesses(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO, String _pkValue, String _tablename) throws Exception {
		WLTLogger.getLogger(this).debug("\r\n\r\n启动一个流程实例[" + _processid + "]"); //
		String str_tablename = _billVO.getSaveTableName(); //查询的表名!!
		if (_tablename != null) {
			str_tablename = _tablename;
		}
		String str_pkValue = _billVO.getPkValue(); // 主键的值!!!!
		if (_pkValue != null) {
			str_pkValue = _pkValue; //
		}
		String str_templetCode = _billVO.getTempletCode(); //模板编码
		String str_queryTableName = _billVO.getQueryTableName(); //保存的表名
		String str_pkfieldname = _billVO.getPkName(); // 主键字段名!!!!

		//强制取下toString的值!
		String str_billVOToStringField = getCommDMO().getStringValueByDS(null, "select tostringkey from pub_templet_1 where upper(templetcode)='" + str_templetCode.toUpperCase() + "'"); //发现有的表单的任务事项设置不起效果,干脆从后台直接取一下!但这样性能会慢一点!
		_billVO.setToStringFieldName(str_billVOToStringField); //

		String str_newPrinstaceId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_PRINSTANCE"); //先取得流程实例主键值!!!
		String str_sql_createPrInstance = getInsertPrinstanceSQL(str_newPrinstaceId, null, str_newPrinstaceId, _processid, str_templetCode, str_tablename, str_queryTableName, str_pkfieldname, str_pkValue, _loginUserId, _loginUserId, "" + _startActivityVO.getId(), null, null); //往流程实例表中新增一条记录!!
		String str_sql_updateBillStatus = "update " + str_tablename + " set wfprinstanceid='" + str_newPrinstaceId + "' where " + _billVO.getPkName() + "='" + str_pkValue + "'"; //将流程实例主键回写到业务单据中!!

		//预算在待处理池中START环节的任务...即直接在启动环节中插入一条记录
		HashVO[] hvs_userCodeName = getCommDMO().getHashVoArrayByDS(null, "select code,name from pub_user where id='" + _loginUserId + "'"); //
		String str_loginUserCode = null;
		String str_loginUserName = null;
		if (hvs_userCodeName.length > 0) {
			str_loginUserCode = hvs_userCodeName[0].getStringValue("code"); //
			str_loginUserName = hvs_userCodeName[0].getStringValue("name"); //
		}
		String str_userCodeName = str_loginUserCode + "/" + str_loginUserName; //
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginUserId); //登录人员的机构id与name

		ActivityVO activityVO = _startActivityVO; //getStartActivityVO(_processid); //
		DealTaskVO dealTaskVO = new DealTaskVO(); //
		dealTaskVO.setFromActivityId(null); //因为是启动的,所以不知从哪个环节来的
		dealTaskVO.setTransitionId(null); //因为是启动的,所以不知从哪根连线来的
		dealTaskVO.setCurrActivityId("" + activityVO.getId()); //当前环节,即启动环节!
		dealTaskVO.setCurrActivityApproveModel("1"); //抢占
		dealTaskVO.setParticipantUserId(_loginUserId); //参与者的ID
		dealTaskVO.setParticipantUserCode(str_loginUserCode); //
		dealTaskVO.setParticipantUserName(str_loginUserName); //
		dealTaskVO.setParticipantUserDeptId(str_loginUserDeptIdName[0]); //参与者的机构
		dealTaskVO.setParticipantUserDeptName(str_loginUserDeptIdName[1]); //

		String str_currtime = getTBUtil().getCurrTime(); //当前时间
		String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_DEALPOOL"); //流程实例的主键!!
		String str_insertDealPool = getInsertDealPoolSQL(str_newDealPoolId, str_newPrinstaceId, null, str_newPrinstaceId, _processid, _loginUserId, str_loginUserName, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], dealTaskVO, "N", null, "启动流程", null, null, 1, str_currtime, null, null, "C", false, null, null, null, null); //先预先插入数据,等待下一步修改它!

		//创建流程时需要往待办任务表也插入一条记录,然后等待使用紧接着后面的SecondCall方法将其搬家到已办箱中,如果不做这个处理则会造成创建人在已办箱中看不到记录!!!!
		String str_insertTaskDeal = getInsertTaskDealSQL(str_newDealPoolId, str_newPrinstaceId, null, str_newPrinstaceId, null, _billVO.getTempletCode(), str_tablename, str_queryTableName, str_pkfieldname, str_pkValue, _billVO.toString(), "启动流程", "启动流程", _loginUserId, str_userCodeName, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], str_currtime, _loginUserId, str_userCodeName,
				str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], null, null, null, false); //

		System.out.println("\r\n开始执行启动流程的SQL............."); //
		getCommDMO().executeBatchByDS(null, new String[] { str_sql_createPrInstance, str_insertDealPool, str_insertTaskDeal, str_sql_updateBillStatus, }); //执行批插入任务,即启动流程时需要做4个sql,其中3条是插入操作,即往pub_wf_instance,pub_wf_dealpool,pub_task_del这3张表中插入!
		System.out.println("执行启动流程的SQL结束..............\r\n"); //
		return str_newPrinstaceId; //返回流程实例主键!!
	}

	/**
	 * 取得所有可以启动的环节!!!!!
	 * 可以有多个启动环节!!!!
	 * @return
	 */
	public ActivityVO[] getStartActivityVOs(String _processid, String _loginUserId) throws Exception {
		HashVO[] hvs_process = getCommDMO().getHashVoArrayByDS(null, "select code,name from pub_wf_process where id='" + _processid + "'"); //
		if (hvs_process == null || hvs_process.length == 0) {
			throw new WLTAppException("没有找到主键为[" + _processid + "]的工作流定义,请确认是否被误删除了!!");
		}
		String str_processCode = hvs_process[0].getStringValue("code"); //
		String str_processName = hvs_process[0].getStringValue("name"); //
		String str_sql_findallstarts = "select * from pub_wf_activity where processid='" + _processid + "' and (activitytype='START' or canhalfstart='Y')"; //找出所有启动的或半路启动的环节
		HashVO[] hvs_allstart = getCommDMO().getHashVoArrayByDS(null, str_sql_findallstarts); //
		TBUtil tbutil = new TBUtil(); //
		ArrayList al_ActivityVOS = new ArrayList(); //
		HashMap al_roles = new HashMap();
		StringBuffer sb_roles = new StringBuffer();
		for (int i = 0; i < hvs_allstart.length; i++) { //遍历所有环节			
			ActivityVO activityVO = (ActivityVO) hvs_allstart[i].convertToRealOBJ(ActivityVO.class); //
			String str_activityType = activityVO.getActivitytype(); //环节类型!!
			String str_parroles = activityVO.getHalfstartrole(); //可以半路启动的角色
			String str_pardepttype = activityVO.getHalfstartdepttype(); // 可以半路启动的机构类型, gaofeng
			if (str_parroles == null || str_parroles.trim().equals("")) { //如果没有设
				if ("START".equals(str_activityType)) { //如果是启动环节!!,则默认认为是可以启动的!
					al_ActivityVOS.add(activityVO); //则直接加入,并跳过本次循环
				}
			} else if (str_pardepttype != null && !str_pardepttype.trim().equals("")) {// gaofeng
				System.out.println("流程限定了可以启动的机构类型,开始进行匹配....");
				String[] str_items = str_parroles.split(";"); //取得所有角色
				String[] str_depttypeitems = str_pardepttype.split(";");
				String str_depttypeincondition = tbutil.getInCondition(str_depttypeitems);
				String str_incondition = tbutil.getInCondition(str_items); //
				HashVO[] hvos = getCommDMO().getHashVoArrayByDS(null, "select * from pub_role where id in (" + str_incondition + ")"); //取得所有角色
				if (hvos.length > 0) {
					if (isAllCommRole(hvos)) { //如果里面有个特殊的"所有人员"或"一般用户"则直接可以发起!也就是说哪怕某个用户没有配置这两个角色都可以!!
						al_ActivityVOS.add(activityVO); //则直接加入,并跳过本次循环
					} else {
						for (int j = 0; j < hvos.length; j++) {
							al_roles.put(hvos[j].getStringValue("code"), hvos[j].getStringValue("code")); //
						}
						String str_checkloginuser = getCommDMO().getStringValueByDS(null, "select count(*) c1 from pub_user_role where userid='" + _loginUserId + "' and roleid in (" + str_incondition + ")");
						//还要判断当前登陆的用户是否符合机构类型的设置
						String str_sql = "select count(*) from pub_corp_dept where id in (select userdept from pub_user_post where userid=" + _loginUserId + " and corptype in (" + str_depttypeincondition + "))";
						String str_checkloginuserdepttype = getCommDMO().getStringValueByDS(null, str_sql);

						if (Integer.parseInt(str_checkloginuser) > 0 && Integer.parseInt(str_checkloginuserdepttype) > 0) { //如果发现我拥有其中一个角色,并且我所的机构类型符合限定的机构类型,则认为我可以处理!
							al_ActivityVOS.add(activityVO);
						}
					}
				}
			} else { //登录人员必须在指定的环节中!!
				String[] str_items = str_parroles.split(";"); //取得所有角色
				String str_incondition = tbutil.getInCondition(str_items); //
				HashVO[] hvos = getCommDMO().getHashVoArrayByDS(null, "select * from pub_role where id in (" + str_incondition + ")"); //取得所有角色
				if (hvos.length > 0) {
					if (isAllCommRole(hvos)) { //如果里面有个特殊的"所有人员"或"一般用户"则直接可以发起!也就是说哪怕某个用户没有配置这两个角色都可以!!
						al_ActivityVOS.add(activityVO); //则直接加入,并跳过本次循环
					} else {
						for (int j = 0; j < hvos.length; j++) {
							al_roles.put(hvos[j].getStringValue("code"), hvos[j].getStringValue("code")); //
						}
						String str_checkloginuser = getCommDMO().getStringValueByDS(null, "select count(*) c1 from pub_user_role where userid='" + _loginUserId + "' and roleid in (" + str_incondition + ")");
						if (Integer.parseInt(str_checkloginuser) > 0) { //如果发现我拥有其中一个角色,则认为我可以处理!
							al_ActivityVOS.add(activityVO);
						}
					}
				}
			}
		}
		if (al_ActivityVOS.size() == 0) {
			String[] str_keys = (String[]) al_roles.keySet().toArray(new String[0]);
			for (int j = 0; j < str_keys.length; j++) {
				sb_roles.append(al_roles.get(str_keys[j]) + ";");
			}
			throw new WLTAppException("根据流程配置,只有角色为[" + sb_roles.toString() + "]的人员才可以启动该流程[" + str_processCode + "/" + str_processName + "]!"); //
		}
		return (ActivityVO[]) al_ActivityVOS.toArray(new ActivityVO[0]); //
	}

	/**
	 * 是否是所人人员角色
	 * @param hvos
	 * @return
	 */
	private boolean isAllCommRole(HashVO[] hvos) {
		for (int i = 0; i < hvos.length; i++) {
			if (hvos[i].getStringValue("code").equals("一般用户") || //就曾遇到有的客户死活必须叫[一般员工]而不是[一般用户],有的又是恰恰相反,所以没办法只能什么都匹配下! 但在角色表中只能同时有其中一个!
					hvos[i].getStringValue("code").equals("一般员工") || //
					hvos[i].getStringValue("code").equals("一般人员") || //
					hvos[i].getStringValue("code").equals("普通用户") || //
					hvos[i].getStringValue("code").equals("普通员工") || //邮储项目中叫普通员工【xch/2012-08-15】
					hvos[i].getStringValue("code").equals("普通人员") || //
					hvos[i].getStringValue("code").equals("所有人员")) { //
				return true;
			}
		}
		return false;
	}

	/**
	 * 取得一个人员在某流程实例中的待处理任务的SQL,这是最经典的流程引擎中的SQL,即如何判断某个人的当前任务!
	 * 如果某人有任务,按道理只会返回一条记录!!!!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 */
	private String getOneUserDealPoolTaskSQL(String _wfinstanceid, String _loginuserid) {
		return "select * from pub_wf_dealpool where prinstanceid='" + getTBUtil().getNullCondition(_wfinstanceid) + "' and (participant_user='" + _loginuserid + "' or participant_accruserid='" + _loginuserid + "') and issubmit='N' and isprocess='N'";
	}

	/**
	 * 取得登录人员在流程处理池中的任务,按道理应该只会取得一条,如果接收与被抄送者是同一个人,在前台也做了唯一性过滤!!所以必然只会有一条记录!!!!!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 * @throws Exception
	 */
	public HashVO getLoginUserDealPoolTask(String _wfinstanceid, String _loginuserid) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select t1.*,t2.status prinstance_status from pub_wf_dealpool t1,pub_wf_prinstance t2 where t1.prinstanceid=t2.id and t1.prinstanceid='" + getTBUtil().getNullCondition(_wfinstanceid) + "' and t1.participant_user='" + _loginuserid + "' and t1.issubmit='N' and t1.isprocess='N'"); //
		if (hvs == null || hvs.length == 0) {
			return null;
		} else {
			return hvs[0]; //
		}
	}

	/**
	 * 取得登录人员需要处理的记录
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 * @throws Exception
	 */
	private HashVO getLoginUserDealRecord(String _wfinstanceid, String _prdealpoolId, String _loginuserid) throws Exception {
		HashVO[] hvs_instance = getCommDMO().getHashVoArrayByDS(null, "select status,creater,createactivity from pub_wf_prinstance where id='" + getTBUtil().getNullCondition(_wfinstanceid) + "'");
		if (hvs_instance.length == 0) {
			throw new WLTAppException("没有找到ID=[" + _wfinstanceid + "]的流程实例!");
		}
		if (hvs_instance[0].getStringValue("status", "").equals("END")) { //如果已结束
			throw new WLTAppException("流程已结束,不能进行任何处理!");
		}

		HashVO hvo_judge_dealpool = judgeTaskDeal(_wfinstanceid, _prdealpoolId, _loginuserid); //判断该任务是否有效,如果无效则弹出框提示!!! 		
		if (!hvo_judge_dealpool.getBooleanValue("任务是否有效")) {
			throw new WLTAppException(hvo_judge_dealpool.getStringValue("原因说明")); //
		}

		String str_parentsql = "select * from pub_wf_dealpool where id='" + hvo_judge_dealpool.getStringValue("id") + "'"; //
		String str_childsql_prinstanceid = "select billtempletcode,billtablename,billquerytablename,billpkname,billpkvalue,creater,parentinstanceid,rootinstanceid from pub_wf_prinstance where id='{prinstanceid}'"; //创建者
		String str_childsql_rootprinstanceid = "select processid from pub_wf_prinstance where id={rootinstanceid}";
		String str_childsql_creater = "select code,name from pub_user where id='{creater}'"; //创建者
		String str_childsql_fromactivity = "select code,wfname,activitytype,belongdeptgroup from pub_wf_activity where id='{fromactivity}'"; //来源ID
		String str_childsql_transition = "select code,wfname,uiname,dealtype from pub_wf_transition where id='{transition}'"; //连线
		String str_childsql_curractivity = "select code,wfname,activitytype,belongdeptgroup,approvemodel,approvenumber,isassignapprover,canhalfend,canselfaddparticipate,iscanback,isneedmsg,isselfcycle,selfcyclerolemap,checkuserpanel,showparticimode,participate_user,participate_corp,participate_group,participate_dynamic,ccto_user,ccto_corp,ccto_role,intercept1,intercept2 from pub_wf_activity where id='{curractivity}'"; //当前环节
		//String str_childsql_submitcount = "select count(*) submitcount from pub_wf_transition where fromactivity='{curractivity}' and dealtype='SUBMIT'"; //提交连线数量
		//String str_childsql_rejectcount = "select count(*) rejectcount from pub_wf_transition where fromactivity='{curractivity}' and dealtype='REJECT'"; //拒绝连线数量
		String str_childsql_participant_user = "select code,name from pub_user where id='{participant_user}'"; //参与者
		String str_childsql_submittouser = "select code,name from pub_user where id='{submittouser}'"; //提交给谁
		String str_childsql_submittotransition = "select code,wfname,uiname from pub_wf_transition where id='{submittotransition}'"; //提交出去的连线
		String str_childsql_submittoactivity = "select code,wfname,uiname,belongdeptgroup from pub_wf_activity where id='{submittoactivity}'"; //提交出去的环节
		String str_childsql_createbyid = "select participant_user,participant_username,realsubmiter,realsubmitername,participant_accruserid,participant_accrusercode,participant_accrusername,batchno from pub_wf_dealpool where id='{createbyid}'"; //
		HashVO[] hvs_wfdealpool = getCommDMO().getHashVoArrayBySubSQL(null, str_parentsql,
				new String[] { str_childsql_prinstanceid, str_childsql_rootprinstanceid, str_childsql_creater, str_childsql_fromactivity, str_childsql_transition, str_childsql_curractivity, str_childsql_participant_user, str_childsql_submittouser, str_childsql_submittotransition, str_childsql_submittoactivity, str_childsql_createbyid }); //

		if (hvs_wfdealpool == null || hvs_wfdealpool.length == 0) {
			throw new WLTAppException("当前没有需要处理的任务!");
		}

		//接收这个玩意太烦,许多客户反应!默认为去掉算了!!
		//		if (!hvs_instance[0].getStringValue("status", "").equals("START")) {
		//			if (hvs_wfdealpool[0].getStringValue("isreceive") == null || hvs_wfdealpool[0].getStringValue("isreceive").equalsIgnoreCase("N")) {
		//				throw new WLTAppException("请先接收任务!");
		//			}
		//		}

		//取得流程创建者的id,code,name等信息!!
		HashVO[] hvs_instance_createuser = getCommDMO().getHashVoArrayByDS(null, "select id,code,name from pub_user where id='" + hvs_instance[0].getStringValue("creater") + "'"); //
		hvs_wfdealpool[0].setAttributeValue("wf_prinstance_creater_id", hvs_instance_createuser[0].getStringValue("id")); //
		hvs_wfdealpool[0].setAttributeValue("wf_prinstance_creater_code", hvs_instance_createuser[0].getStringValue("code")); //
		hvs_wfdealpool[0].setAttributeValue("wf_prinstance_creater_name", hvs_instance_createuser[0].getStringValue("name")); //

		HashVO[] hvs_instance_createactivity = getCommDMO().getHashVoArrayByDS(null, "select id,code,wfname,activitytype from pub_wf_activity where id='" + hvs_instance[0].getStringValue("createactivity") + "'"); //
		if (hvs_instance_createactivity != null && hvs_instance_createactivity.length > 0) {
			hvs_wfdealpool[0].setAttributeValue("wf_prinstance_createactivity_id", hvs_instance_createactivity[0].getStringValue("id")); //
			hvs_wfdealpool[0].setAttributeValue("wf_prinstance_createactivity_code", hvs_instance_createactivity[0].getStringValue("code")); //
			hvs_wfdealpool[0].setAttributeValue("wf_prinstance_createactivity_name", hvs_instance_createactivity[0].getStringValue("wfname")); //
			hvs_wfdealpool[0].setAttributeValue("wf_prinstance_createactivity_type", hvs_instance_createactivity[0].getStringValue("activitytype")); //
			hvs_wfdealpool[0].setAttributeValue("wf_prinstance_createactivity_belongdeptgroup", hvs_instance_createactivity[0].getStringValue("belongdeptgroup"));
		}

		return hvs_wfdealpool[0]; //
	}

	/**
	 * 验证是否接受任务，只有接受了任务才能继续提交
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 * @throws Exception
	 */
	public boolean[] checkIsReceiveAndIsCCTo(String _wfinstanceid, String _loginuserid) throws Exception {
		String str_wfsatu = getCommDMO().getStringValueByDS(null, "select status from pub_wf_prinstance where id='" + _wfinstanceid + "'");
		if (str_wfsatu != null && str_wfsatu.equals("END")) {
			throw new WLTAppException("流程已结束,不能进行任何处理!");
		}
		boolean[] isReceiveAndIsCCTo = new boolean[] { true, false }; //是否接收与是否抄送!!!
		HashVO hvo_dealtask = getLoginUserDealPoolTask(_wfinstanceid, _loginuserid); //
		if (!"START".equals(str_wfsatu)) { //如果不是启动模式
			if (hvo_dealtask != null) {
				if (hvo_dealtask.getStringValue("isreceive") == null || hvo_dealtask.getStringValue("isreceive").equalsIgnoreCase("N")) { //
					isReceiveAndIsCCTo[0] = false;
				}

				if (hvo_dealtask.getStringValue("isccto") != null && hvo_dealtask.getStringValue("isccto").equalsIgnoreCase("Y")) { //这一行代码,曾经将"与"写成了"或",结果导致所有待办箱的处理界面没有处理框了!!
					isReceiveAndIsCCTo[1] = true;
				}
			}

		}
		return isReceiveAndIsCCTo;
	}

	public boolean checkIsReceiveCancel(String _wfinstanceid, String _loginuserid) throws Exception {
		String str_wfsatu = getCommDMO().getStringValueByDS(null, "select status from pub_wf_prinstance where id='" + _wfinstanceid + "'");
		if (str_wfsatu != null && str_wfsatu.equals("END")) {
			throw new WLTAppException("流程已结束,不能进行任何处理!");
		}
		if (!str_wfsatu.equals("START")) {
			String str_parentsql = "select currowner,lastsubmiter from pub_wf_prinstance where id='" + _wfinstanceid + "'"; // 找出属于我的未处理的流程
			HashVO[] hvs_currowner = getCommDMO().getHashVoArrayByDS(null, str_parentsql); //
			String str_currowner = hvs_currowner[0].getStringValue("currowner"); //
			String str_lastsubmiter = hvs_currowner[0].getStringValue("lastsubmiter"); //最后提交者!!
			if (!_loginuserid.equals(str_lastsubmiter)) {
				String str_lastsubmiter_name = getCommDMO().getStringValueByDS(null, "select name from pub_user where id='" + str_lastsubmiter + "'"); //
				throw new WLTAppException("最后提交者是【" + str_lastsubmiter_name + "】,只有他才可以进行撤回操作,你不能进行撤回!"); //
			}

			if (str_currowner == null || str_currowner.equals("")) {
				return true;
			} else {
				String newcurrowner = str_currowner.substring(1, str_currowner.length() - 1); //
				newcurrowner = new TBUtil().replaceAll(newcurrowner, ";", ","); //算出当前所有参与者
				HashVO[] hashvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where  prinstanceid='" + _wfinstanceid + "' and  participant_user in (" + newcurrowner + ") and issubmit='N'"); //找出所有参与者,并者没有提交
				for (int i = 0; i < hashvo.length; i++) {
					if (hashvo[i].getStringValue("isreceive").equalsIgnoreCase("Y")) { //只要有一个接收了,则认为是接收了!!
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * 判断流程任务是否可处理!至关重要!! 即有时在消息任务表中明明有任务，但点击处理时却不行,因为可能该环节已过了，即这是个过期消息了!!! 所以需要一个实际判断是否是有效任务的方法!!
	 * 在三个时点都需要调用这个方法:
	 * 一是在主界面中点击处理按钮时
	 * 二是在弹出界面中点击【提交】按钮时(也即在getFirstTaskVO()方法中),因为弹出界面后在发呆的过程中,别人可能抢占处理了!
	 * 三是在选择参与用户窗口中点击【确定】时(也即在secondCall()方法中),因为在选择用户的发呆过程中,别人可能抢占处理了!
	 * 旧的机构是根据实例id,新的机构是根据任务id,流程任务id
	 * 如果没有真正的有效任务,则还返回原因,比如是被他人处理了,抄送的,等等....
	 * @return
	 */
	public HashVO judgeTaskDeal(String _wfinstanceid, String _dealPoolId, String _loginuserid) throws Exception {
		HashVO returnHVO = null; //创建返回值
		String str_key1 = "任务是否有效"; //
		String str_key2 = "原因说明"; //
		String str_dealPoolId = _dealPoolId; //
		if (str_dealPoolId == null || str_dealPoolId.equals("")) { //如果没有定义待办任务而只有流程实例,即老的机制!!这时必须找出流程任务,而且按道理应该只会找出一条任务!!
			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("select id from pub_wf_dealpool "); //从流程处理任务表中找id
			sb_sql.append("where prinstanceid='" + getTBUtil().getNullCondition(_wfinstanceid) + "' "); //对应的流程实例
			sb_sql.append("and (participant_user='" + _loginuserid + "' or participant_accruserid='" + _loginuserid + "') "); //接收者或接收者的授权人等于登录人员! 这里以后应该有判断参数,即有的模式是授权人是不能处理的
			sb_sql.append("and issubmit='N' "); //没有提交的,因为可能存在会签模式,即我提交了,但整个阶段任务并没有结束!!
			sb_sql.append("and isprocess='N' "); //没有过的
			String str_getDealPoolId = getCommDMO().getStringValueByDS(null, sb_sql.toString()); //老的机制去取dealpoolId
			if (str_getDealPoolId == null || str_getDealPoolId.trim().equals("")) {
				returnHVO = new HashVO(); //创建返回值
				returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //设置有无效任务!
				returnHVO.setAttributeValue(str_key2, "你不是该流程的当前处理人,即该流程可能已提交到其他人那里了!"); //根据老的机制在pub_wf_dealpool表中没有找到participant_user='登录人' and issubmit='N' and isprocess='N'的记录,即没有真正的处理任务!
				return returnHVO; //
			}
			str_dealPoolId = str_getDealPoolId; //
		}

		HashVO[] hvs_dealpool = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id='" + str_dealPoolId + "'"); //
		if (hvs_dealpool.length == 0) { //如果没找到数据
			returnHVO = new HashVO(); //创建返回值
			returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //设置有无效任务!
			returnHVO.setAttributeValue(str_key2, "根据id[" + str_dealPoolId + "]没有在pub_wf_dealpool表中找到一条记录,可能是测试环境中被人删了数据!"); //设置有无效任务!
			return returnHVO; //
		} else {
			returnHVO = hvs_dealpool[0]; //直接赋给返回值!因为一些地方用到!
			//如果找到了数据,则还要看一些状态!!!
			boolean isCCTo = returnHVO.getBooleanValue("isccto", false); //
			boolean isSubmit = returnHVO.getBooleanValue("issubmit", false); //是否提交了
			boolean isProcess = returnHVO.getBooleanValue("isprocess", false); //是否过了,如果是抢占,或其他什么原因会导致其为Y
			if (isSubmit || isProcess) { //如果提交了,已被其他人处理过了
				if (isCCTo) { //如果是抄送的任务
					returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //设置有无效任务!
					returnHVO.setAttributeValue(str_key2, "您是该任务的被抄送者,无需进行处理,请知悉!\r\n如果有确定按钮,点击[确认]可将该任务转移到已办任务中!"); //\r\n或者重新刷新待办任务列表,将不再显示该任务了!这种情况以后会经常发生,即抢占模式下,多人接收者,迟到者只能看不能处理。
				} else {
					returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //设置有无效任务!
					returnHVO.setAttributeValue(str_key2, "该任务已过期(即已被他人抢先提交了或有人使用该帐号在另一个地方处理了),故现在不能进行处理!\r\n如果有确定按钮,点击[确认]可将该任务转移到已办任务中!"); //这种情况以后会经常发生,即抢占模式下,多人接收者,迟到者只能看不能处理。
				}
			} else { //如果任务仍有效
				if (isCCTo) { //如果是抄送的任务
					returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //设置有无效任务!
					returnHVO.setAttributeValue(str_key2, "您是该任务的被抄送者,无需进行处理,请知悉!\r\n如果有确定按钮,点击[确认]可将该任务转移到已办任务中!"); //
				} else { //有效任务
					returnHVO.setAttributeValue(str_key1, Boolean.TRUE); //设置为有效
					returnHVO.setAttributeValue(str_key2, null); //
				}
			}
			return returnHVO; //
		}
	}

	/**
	 * 判断一个流程是否可以真正结束!!! 以前结束是不管三七二十一直接将流程实例标记为结束! 但后来增加多路提交的情况后,就不应该有这样一个动作!
	 * 而应该是每次都是计算一把,看该流程实例是否还有待处理的任务却还没处理的(isprocess='N'),如果有,则说明该流程并没有结束!! 
	 * @return
	 */
	public boolean judgeWFisRealEnd(String _prinstanceid) throws Exception {
		String str_count = getCommDMO().getStringValueByDS(null, "select count(*) c1 from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and isprocess='N' and (isccto is null or isccto='N')"); //看是否还有未处理的!
		int li_count = Integer.parseInt(str_count); //
		if (li_count == 0) { //如果一条有效任务没有了,则说明是可以结束的!!则返回true
			return true; //
		} else {
			return false;
		}
	}

	/**
	 * 查询时,旧的机构是得到一个prinstanceid,新的机制是同时得到taskid,prdealpoolid,prinstanceid
	 * 以前的逻辑是：在点击处理按钮时,先计算一把,然后弹出一个对话框,提示流程已结束,没有待处理的任务等! 然后就不弹出卡片了!!
	 * 新的机制想改成：也是先计算一把,计算的结果有多种类型,且有个结果说明,如果是可以提交的,则显示提交按钮与意见框,如果是其他类型则根据具体情况显示历史意见列表与按钮!! 比如是抄送的,则提示是被抄送者不能处理,如果该任务已被他人抢点提交了,则提示! 如果已结束,则也相应提示！
	 * 之所以在弹出卡片的界面中提示,是因为即使这时这个人不能提交(没有待处理的任务),但他仍然需要查看表单信息与流程历史处理意见! 所以肯定在弹出界面中显示!! 而且最好是红色光亮滚动框外显示！
	 * 在两个关键时点判断是否具体实际任务！ 一个就是上面所说的在主界面上点击处理按钮，一个就是在弹出界面中点击提交按钮(即getFirstTaskVO)!!! 之所以两次都要,是因为操作者很可能在弹出界面后很长时间内不动,而这时可能任务已被其他人处理了! 所以必须每次都现算！！！
	 * 为了重用,应该有这第一个专门方法判断登录用户的待办任务的实际可处理情况,尤其在新的机制下,任务不能被其他人删除,更需要这个判断!! 而且判断的结果说明要通俗易懂!!! 
	 * 按道理在弹出参与者选择窗口后,在点击确认正式提交前也同样有这样一个判断(即SecondCall的时候),因为在弹出选择用户窗口后,同样可能"发呆",而这时任务同样会被其他人抢走!!
	 */

	private void setFirstWFParVOFromHVO(WFParVO _firstWFParVO, HashVO _hvoRecord) {
		_firstWFParVO.setParentinstanceid(_hvoRecord.getStringValue("prinstanceid_parentinstanceid")); //父亲流程实例id
		_firstWFParVO.setRootinstanceid(_hvoRecord.getStringValue("prinstanceid_rootinstanceid")); //根流程实例id

		_firstWFParVO.setBilltempletcode(_hvoRecord.getStringValue("prinstanceid_billtempletcode")); //模板编码
		_firstWFParVO.setBilltablename(_hvoRecord.getStringValue("prinstanceid_billtablename")); //保存的业务表名
		_firstWFParVO.setBillQueryTablename(_hvoRecord.getStringValue("prinstanceid_billquerytablename")); //查询的表名
		_firstWFParVO.setBillpkname(_hvoRecord.getStringValue("prinstanceid_billpkname")); //业务表主键名!
		_firstWFParVO.setBillpkvalue(_hvoRecord.getStringValue("prinstanceid_billpkvalue")); //业务表主键值!

		_firstWFParVO.setRejectedDirUp(_hvoRecord.getBooleanValue("isrejecteddirup", false)); //是否直接退回???
		_firstWFParVO.setWfinstance_createuserid(_hvoRecord.getStringValue("wf_prinstance_creater_id")); //流程创建者的ID
		_firstWFParVO.setWfinstance_createusercode(_hvoRecord.getStringValue("wf_prinstance_creater_code")); //流程创建者的Code
		_firstWFParVO.setWfinstance_createusername(_hvoRecord.getStringValue("wf_prinstance_creater_name")); //流程创建者的Name

		_firstWFParVO.setWfinstance_createactivityid(_hvoRecord.getStringValue("wf_prinstance_createactivity_id")); //流程创建者的ID
		_firstWFParVO.setWfinstance_createactivitycode(_hvoRecord.getStringValue("wf_prinstance_createactivity_code")); //流程创建环节的Code
		//		_firstWFParVO.setWfinstance_createactivityname(_hvoRecord.getStringValue("wf_prinstance_createactivity_name")); //流程创建环节的Name

		//修改退回时生成的任务环节没有部门分组/sunfujun/20121114
		String activityname_cre = _hvoRecord.getStringValue("wf_prinstance_createactivity_name", "");
		activityname_cre = activityname_cre.replaceAll("\r", "");
		activityname_cre = activityname_cre.replaceAll("\n", "");
		boolean ishavegroup = cn.com.infostrategy.bs.common.SystemOptions.getBooleanValue("提交界面是否显示环节所属组的名称", true);
		if (ishavegroup) {
			String groupname_cre = _hvoRecord.getStringValue("wf_prinstance_createactivity_belongdeptgroup", "");
			groupname_cre = groupname_cre.replaceAll("\r", "");
			groupname_cre = groupname_cre.replaceAll("\n", "");
			if (!"".equals(groupname_cre)) {
				_firstWFParVO.setWfinstance_createactivityname(groupname_cre + "-" + activityname_cre);
			} else {
				_firstWFParVO.setWfinstance_createactivityname(activityname_cre);
			}
		} else {
			_firstWFParVO.setWfinstance_createactivityname(activityname_cre); //跳到哪一个环节的名称
		}

		_firstWFParVO.setWfinstance_createactivitytype(_hvoRecord.getStringValue("wf_prinstance_createactivity_type")); //流程创建环节的类型

		_firstWFParVO.setDealpooltask_createuserid(_hvoRecord.getStringValue("creater")); //我这条流程任务的创建者的id,即提交给我的人的ID
		_firstWFParVO.setDealpooltask_createusercode(_hvoRecord.getStringValue("creater_code")); //

		//会签-不同部门会签,同部门抢占问题追加 【杨科/2013-04-25】
		_firstWFParVO.setCreatebyid(_hvoRecord.getStringValue("createbyid"));
		_firstWFParVO.setParticipant_userdept(_hvoRecord.getStringValue("participant_userdept"));

		String str_createby_participant_accruserid = _hvoRecord.getStringValue("createbyid_participant_accruserid"); //授权人
		if (str_createby_participant_accruserid != null && !str_createby_participant_accruserid.equals(_hvoRecord.getStringValue("creater"))) { //如果有授权人,并且处理人不是授权人,而是被授权人! 这时需要返回授权人!
			String str_createuserName = _hvoRecord.getStringValue("creater_name"); //
			String str_accrUserCode = _hvoRecord.getStringValue("createbyid_participant_accrusercode"); //
			String str_accrUserName = _hvoRecord.getStringValue("createbyid_participant_accrusername"); //
			if (str_accrUserName != null && str_accrUserName.indexOf("/") > 0) {
				str_accrUserName = str_accrUserName.substring(str_accrUserName.indexOf("/") + 1, str_accrUserName.length()); //
			}
			_firstWFParVO.setDealpooltask_createusername(str_createuserName + "(" + str_accrUserName + "授权)"); //
			_firstWFParVO.setDealpooltask_createuser_accruserid(str_createby_participant_accruserid); //
			_firstWFParVO.setDealpooltask_createuser_accrusercode(str_accrUserCode); //
			_firstWFParVO.setDealpooltask_createuser_accrusername(str_accrUserName); //
		} else {
			_firstWFParVO.setDealpooltask_createusername(_hvoRecord.getStringValue("creater_name")); //
		}

		_firstWFParVO.setCurrParticipantUserName(_hvoRecord.getStringValue("participant_username")); //当前参与者!!!因为存在授权后,所以不一定就是登录人员

		_firstWFParVO.setProcessid(_hvoRecord.getStringValue("processid")); //流程定义主键!!
		_firstWFParVO.setDealpoolid(_hvoRecord.getStringValue("id")); //待处理池中某一任务记录的主键!!总是需要修改该记录的!
		_firstWFParVO.setDealpoolidCreatetime(_hvoRecord.getStringValue("createtime")); //创建时间

		//第一次取得,要给5个字段赋值!!!取出来!
		_firstWFParVO.setDealpooltask_isselfcycleclick(_hvoRecord.getBooleanValue("isselfcycleclick", false)); //
		_firstWFParVO.setDealpooltask_selfcycle_fromrolecode(_hvoRecord.getStringValue("selfcycle_fromrolecode")); //
		_firstWFParVO.setDealpooltask_selfcycle_fromrolename(_hvoRecord.getStringValue("selfcycle_fromrolename ")); //
		_firstWFParVO.setDealpooltask_selfcycle_torolecode(_hvoRecord.getStringValue("selfcycle_torolecode")); //
		_firstWFParVO.setDealpooltask_selfcycle_torolename(_hvoRecord.getStringValue("selfcycle_torolename")); //

		//当前任务是从哪个任务来的!
		_firstWFParVO.setFromactivity(_hvoRecord.getStringValue("fromactivity")); //From环节!!当前任务的来源环节
		_firstWFParVO.setFromactivityCode(_hvoRecord.getStringValue("fromactivity_code")); //
		//		_firstWFParVO.setFromactivityName(_hvoRecord.getStringValue("fromactivity_wfname")); //

		String activityname_fro = _hvoRecord.getStringValue("fromactivity_wfname", "");
		activityname_fro = activityname_fro.replaceAll("\r", "");
		activityname_fro = activityname_fro.replaceAll("\n", "");
		if (ishavegroup) {
			String groupname_fro = _hvoRecord.getStringValue("fromactivity_belongdeptgroup", "");
			groupname_fro = groupname_fro.replaceAll("\r", "");
			groupname_fro = groupname_fro.replaceAll("\n", "");
			if (!"".equals(groupname_fro)) {
				_firstWFParVO.setFromactivityName(groupname_fro + "-" + activityname_fro);
			} else {
				_firstWFParVO.setFromactivityName(activityname_fro);
			}
		} else {
			_firstWFParVO.setFromactivityName(activityname_fro); //跳到哪一个环节的名称
		}

		_firstWFParVO.setFromactivityType(_hvoRecord.getStringValue("fromactivity_activitytype")); //

		_firstWFParVO.setCurractivity(_hvoRecord.getStringValue("curractivity")); //当前任务的环节!!!!
		_firstWFParVO.setCurractivityCode(_hvoRecord.getStringValue("curractivity_code")); //当前环节编码!
		//		_firstWFParVO.setCurractivityName(_hvoRecord.getStringValue("curractivity_wfname")); //当前环节名称

		String activityname_curr = _hvoRecord.getStringValue("curractivity_wfname", "");
		activityname_curr = activityname_curr.replaceAll("\r", "");
		activityname_curr = activityname_curr.replaceAll("\n", "");
		if (ishavegroup) {
			String groupname_curr = _hvoRecord.getStringValue("curractivity_belongdeptgroup", "");
			groupname_curr = groupname_curr.replaceAll("\r", "");
			groupname_curr = groupname_curr.replaceAll("\n", "");
			if (!"".equals(groupname_curr)) {
				_firstWFParVO.setCurractivityName(groupname_curr + "-" + activityname_curr);
			} else {
				_firstWFParVO.setCurractivityName(activityname_curr);
			}
		} else {
			_firstWFParVO.setCurractivityName(activityname_curr);
		}

		_firstWFParVO.setCurractivityBLDeptName(_hvoRecord.getStringValue("curractivity_belongdeptgroup")); //当前环节的所属部门矩阵名称!
		_firstWFParVO.setCurractivityType(_hvoRecord.getStringValue("curractivity_activitytype")); //当前环节类型!

		_firstWFParVO.setCurractivityCanhalfend(_hvoRecord.getBooleanValue("curractivity_canhalfend")); //是否可以半路结束
		_firstWFParVO.setCurractivityCanselfaddparticipate(_hvoRecord.getBooleanValue("curractivity_canselfaddparticipate")); //是否可以自己添加参与者

		_firstWFParVO.setCurractivityIsSelfCycle(_hvoRecord.getBooleanValue("curractivity_isselfcycle", false)); //当前环节是否自循环
		_firstWFParVO.setCurractivitySelfCycleRoleMap(_hvoRecord.getStringValue("curractivity_selfcyclerolemap")); //当前环节是否自循环

		_firstWFParVO.setCurractivityCheckUserPanel(_hvoRecord.getStringValue("curractivity_checkuserpanel")); //
		_firstWFParVO.setIntercept1(_hvoRecord.getStringValue("curractivity_intercept1")); //拦截器1
		_firstWFParVO.setIntercept2(_hvoRecord.getStringValue("curractivity_intercept2")); //拦截器2
		_firstWFParVO.setBatchno(_hvoRecord.getStringValue("batchno")); //批号,很重要
		_firstWFParVO.setApproveModel(_hvoRecord.getStringValue("curractivity_approvemodel"));
	}

	//根据连线VO找到的数据为处理环节VO赋值
	private void setDealActivityVOFromTransitionVO(DealActivityVO _dealActivityVO, NextTransitionVO _nextTransitionVO) {
		_dealActivityVO.setSortIndex(_nextTransitionVO.getSortIndex()); //排序
		_dealActivityVO.setFromActivityId(_nextTransitionVO.getFromactivityId()); //从哪个环节来的!
		_dealActivityVO.setFromActivityCode(_nextTransitionVO.getFromactivityCode()); //从哪个环节Code
		_dealActivityVO.setFromActivityName(_nextTransitionVO.getFromactivityWFName()); //环节名称

		_dealActivityVO.setFromTransitionId(_nextTransitionVO.getId()); //
		_dealActivityVO.setFromTransitionCode(_nextTransitionVO.getCode()); //
		_dealActivityVO.setFromTransitionName(_nextTransitionVO.getWfname()); //
		_dealActivityVO.setFromTransitionIntercept(_nextTransitionVO.getIntercept()); //
		_dealActivityVO.setFromTransitionMailsubject(_nextTransitionVO.getMailSubject()); //邮件主题
		_dealActivityVO.setFromTransitionMailcontent(_nextTransitionVO.getMailContent()); //邮件内容
		_dealActivityVO.setFromtransitiontype(_nextTransitionVO.getDealtype()); ////
		_dealActivityVO.setFromtransitionExtConfMap(_nextTransitionVO.getExtConfMap()); //从哪个连线来的Map

		_dealActivityVO.setActivityId(_nextTransitionVO.getToactivityId()); //到哪个环节
		_dealActivityVO.setActivityCode(_nextTransitionVO.getToactivityCode()); //
		_dealActivityVO.setActivityType(_nextTransitionVO.getToactivityType()); //环节类型
		_dealActivityVO.setActivityName(_nextTransitionVO.getToactivityWFName()); //
		_dealActivityVO.setActivityBelongDeptGroupName(_nextTransitionVO.getToactivityBelongDeptGroupName()); //该环节所属组的名称!! 因为在跨部门或子流程时必须显示出是什么组!
		_dealActivityVO.setApprovemodel(_nextTransitionVO.getToactivityApprovemodel()); //
		_dealActivityVO.setActivityIselfcycle(_nextTransitionVO.getToactivityIsSelfCycle()); //
		_dealActivityVO.setActivitySelfcyclerolemap(_nextTransitionVO.getToactivitySelfCycleMap()); //

		_dealActivityVO.setShowparticimode(_nextTransitionVO.getToactivityShowparticimode()); //
		_dealActivityVO.setParticipate_user(_nextTransitionVO.getToactivityParticipate_user()); //
		_dealActivityVO.setParticipate_group(_nextTransitionVO.getToactivityParticipate_group()); //
		_dealActivityVO.setParticipate_corp(_nextTransitionVO.getToactivityParticipate_corp()); //参与的机构定义
		_dealActivityVO.setParticipate_dynamic(_nextTransitionVO.getToactivityParticipate_dynamic()); //
		_dealActivityVO.setCcto_user(_nextTransitionVO.getToactivityCcto_user()); //抄送人员!!
		_dealActivityVO.setCcto_corp(_nextTransitionVO.getToactivityCcto_corp()); //抄送的机构!!
		_dealActivityVO.setCcto_role(_nextTransitionVO.getToactivityCcto_role()); //抄送的角色!!
		_dealActivityVO.setWnParam(getTBUtil().convertStrToMapByExpress(_nextTransitionVO.getToactivitywnparam(), ";", "="));
		_dealActivityVO.setCanselfaddparticipate(_nextTransitionVO.getToactivityCanselfaddparticipate());

	}

	/**
	 * 获取提交时UI端Dialog需要的参数! 如果是该环节的终结者，则需要计算 比如如果当前环节需要指定审批人，则弹出下一环节的参与者给用户选择!
	 * 比如如果当前环节必须输入批语，则弹出批语框，让用户输入! 返回结果有两种情况，一种是只处理某一个任务,另一种是不光处理某一任务，还处理某一连线
	 * 所谓处理某一连线就是结束该环节，将状态跳到下一环节，同时在待处理任务中为下一环节生成系统任务!
	 * 
	 * @return
	 */
	public WFParVO getFirstTaskVO(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptid, BillVO _billVO, String _dealtype) throws Exception {
		HashVO hvo_dealrecord = getLoginUserDealRecord(_wfinstanceid, _prdealPoolId, _loginuserid); //得到待处理任务	
		//按道理应该只会找到一条数据!!即属于我的任务..
		WFParVO firstWFParVO = new WFParVO(); //
		firstWFParVO.setWfinstanceid(_wfinstanceid); //设置流程实例主键!
		setFirstWFParVOFromHVO(firstWFParVO, hvo_dealrecord); //先将取得到流程关键信息赋给WFParVO!!

		//判断是否是终结者,这是最关键的
		boolean bo_isLastSubmiter = isLastCommit(_wfinstanceid, hvo_dealrecord.getIntegerValue("batchno"), hvo_dealrecord.getStringValue("curractivity_approvemodel"), hvo_dealrecord.getIntegerValue("curractivity_approvenumber")); //在第一次取数时就判断是否是终结者!
		if (bo_isLastSubmiter || "SEND".equals(_dealtype)) { //如果是最后提交者
			firstWFParVO.setIsprocessed(bo_isLastSubmiter); //设置为是最后的提交者!!! 至关重要!!! //sunfujun/20120810/解决会签任务转办的问题 批次号的问题
			if (firstWFParVO.getCurractivityType() != null && firstWFParVO.getCurractivityType().equals("END") && !"SEND".equals(_dealtype)) { //如果当前环节是END,即结束环节!需要特殊处理
				//不做,即不取下一出路了,即表示在结束环节上不应该有线出去的!!!即使有了也是没有效果的!!!!切记!!!
			} else {
				//ArrayList v_taskVO = new ArrayList(); //
				//算出满足条件分支,及其目标环节的所有参与者!!!这里需要执行每条连线上的条件!!!!将当前环节作为FromActivity计算!!
				NextTransitionVO[] nextTransitionVOs = getNextTransitionVOs(firstWFParVO, _wfinstanceid, hvo_dealrecord.getStringValue("id"), hvo_dealrecord, _billVO, _dealtype); //取得满足条件的所有出路!!
				if (nextTransitionVOs == null || nextTransitionVOs.length == 0) { //如果没有找到一个出路,则直接报错
					throw new WLTAppException("没有找到一个定义的下游环节!你没有权利进行此操作!"); //
				}

				//先算出所有待处理环节!
				HashMap map_dealactivity = new HashMap(); //
				boolean bo_iffindpasscondition = false; //看是否找到一个出路
				//String str_notpassdesc = ""; //
				for (int i = 0; i < nextTransitionVOs.length; i++) { //遍历每个出路(即连线),找出每个出路目标上的所有参与者
					if (!nextTransitionVOs[i].isPasscondition()) { //如果条件不通过则退出!!
						continue; //
					} else {
						bo_iffindpasscondition = true; //
					}
					if (!map_dealactivity.containsKey(nextTransitionVOs[i].getToactivityId())) { //考虑到两个环节之间有人误画了多根线,即有两根线的目标环节一样,所以要做唯一性处理!
						DealActivityVO dealActivityVO = new DealActivityVO(); //
						setDealActivityVOFromTransitionVO(dealActivityVO, nextTransitionVOs[i]); //
						map_dealactivity.put(nextTransitionVOs[i].getToactivityId(), dealActivityVO); //
					}
				}

				if (!bo_iffindpasscondition || map_dealactivity.size() == 0) {
					throw new WLTAppException("没有找到一个满足条件的下游环节!你没有权利进行此操作!"); //
				}

				//为了提高性能,先一下子将所有授权的数据取出来,下面在计算参与者时要循环反复用到,所以需要先算出来!!!
				HashMap accreditAndProxyMap = getWFBSUtil().getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //取得授权信与代理信息!!!!
				String str_accredModel = getAccrModel(_billVO); //取得当前表单定义的授权模块,即不同的表单会有不同的授权模块!!发前是放在每个环节中计算的,但那样有几个环节就会算几次,性能较低!! 应该一次先算好,然后每次用!!!

				//取得所有待办环节!
				DealActivityVO[] allDealActs = (DealActivityVO[]) map_dealactivity.values().toArray(new DealActivityVO[0]); //
				for (int i = 0; i < allDealActs.length; i++) { //
					if ("SEND".equals(_dealtype)) {//邮储提出转办只能转办给本部门人员除了自己/sunfujun/20120830
						String str_parMsg = "因为为转办,所以直接本部门除了自己的人!###<br>"; //
						allDealActs[i].setParticiptMsg(str_parMsg);
						WorkFlowParticipantBean parBean = getOneActivityParticipanUserByCorpAreaAndRole(hvo_dealrecord.getStringValue("participant_user"), "getWFCorp(\"type=登录人所在机构\",\"二次下探是否包含子孙=N\");", null, false);
						allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + "■" + parBean.getParticiptMsg()); //
						if (parBean.getParticipantUserBeans() != null && parBean.getParticipantUserBeans().length > 0) {
							LinkedHashMap hs_taskVOs = new LinkedHashMap(); ////
							WorkFlowParticipantUserBean[] parUserBeans = parBean.getParticipantUserBeans(); //
							for (int k = 0; k < parUserBeans.length; k++) { //遍历所有人员!!
								if (hvo_dealrecord.getStringValue("participant_user", "").equals(parUserBeans[k].getUserid())) {
									continue;
								}
								String str_identifyKey = parUserBeans[k].getUserid() + "#" + parUserBeans[k].getAccrUserid() + "#" + parUserBeans[k].isCCTo(); //相同的接收人员,授权人员,抄送只取一个!!
								if (!hs_taskVOs.containsKey(str_identifyKey)) { //
									DealTaskVO taskVO = getDealTaskVOByCompute(allDealActs[i], parUserBeans[k]);
									hs_taskVOs.put(str_identifyKey, taskVO);
								}
							}
							DealTaskVO[] taskVOs = (DealTaskVO[]) hs_taskVOs.values().toArray(new DealTaskVO[0]); //
							allDealActs[i].setDealTaskVOs(taskVOs);

						}
						continue;
					}
					if ("REJECT".equalsIgnoreCase(allDealActs[i].getFromtransitiontype())) { //如果是虚线,即表示只计算曾经走过的!!
						String str_parMsg = "因为为虚线,所以直接计算该环节上曾经走过的人!###<br>"; //
						allDealActs[i].setParticiptMsg(str_parMsg); //
						WorkFlowParticipantBean parBean = getOneActivityHistDealUserVOs(_wfinstanceid, allDealActs[i].getActivityId()); //取得目标环节上曾经走过的人
						allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + "■" + parBean.getParticiptMsg()); //
						if (parBean.getParticipantUserBeans() != null && parBean.getParticipantUserBeans().length > 0) {
							LinkedHashMap hs_taskVOs = new LinkedHashMap(); ////
							WorkFlowParticipantUserBean[] parUserBeans = parBean.getParticipantUserBeans(); //
							for (int k = 0; k < parUserBeans.length; k++) { //遍历所有人员!!
								String str_identifyKey = parUserBeans[k].getUserid() + "#" + parUserBeans[k].getAccrUserid() + "#" + parUserBeans[k].isCCTo(); //相同的接收人员,授权人员,抄送只取一个!!
								if (!hs_taskVOs.containsKey(str_identifyKey)) { //
									DealTaskVO taskVO = getDealTaskVOByCompute(allDealActs[i], parUserBeans[k]); //计算任务对象!!!
									hs_taskVOs.put(str_identifyKey, taskVO); //做唯一性过滤!
								}
							}
							DealTaskVO[] taskVOs = (DealTaskVO[]) hs_taskVOs.values().toArray(new DealTaskVO[0]); //
							allDealActs[i].setDealTaskVOs(taskVOs); //设置任务!!!!

						}
					} else { //如果是正常提交,则根据环节上的定义进行计算!!!
						String str_parMsg = "参与人员的定义=【" + toEmpty(allDealActs[i].getParticipate_user()) + "】<br>参与机构的定义=【" + toEmpty(allDealActs[i].getParticipate_corp()) + "】  参与角色的定义=【" + toEmpty(allDealActs[i].getParticipate_group()) + "】<br>动态参与者的定义=【" + toEmpty(allDealActs[i].getParticipate_dynamic()) + "】###<br>"; //
						allDealActs[i].setParticiptMsg(str_parMsg); //
						//取得某一环节上的所有参与者!!!这一步至关紧要!!!分别要算三种情况!!即静态参与的人,静态参与的组,动态参与者
						WorkFlowParticipantBean[] parDeptUserBeans = getOneActivityAllParticipantUsers(_loginuserid, _loginUserDeptid, _billVO, hvo_dealrecord, accreditAndProxyMap, str_accredModel, allDealActs[i]); //★★★★★最关键的计算!即要算出某个环节上的人!
						String str_sessionInfoMsg = getInitContext().getCurrSessionCustStrInfoByKey("$环节参与者计算过程", true); //
						if (str_sessionInfoMsg != null) {
							allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + str_sessionInfoMsg); //为了更好的监控如何计算的过程!!!显示出如果计算的!!!
						}
						if (parDeptUserBeans != null && parDeptUserBeans.length > 0) { //如果取得多个参与者,则一个个加入!! 一种类型的参与定义,就有一个参与的Bean,所以有多个!! 但感觉这个还是多余的,应该直接返回人员清单更清爽!!即WorkFlowParticipantUserBean[]
							LinkedHashMap hs_taskVOs = new LinkedHashMap(); ////
							for (int j = 0; j < parDeptUserBeans.length; j++) { //将参与者的结果加在环节的结果上
								if (parDeptUserBeans[j].getParticiptMsg() != null) { //如果有处理消息!!
									allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + "■" + parDeptUserBeans[j].getParticiptMsg()); //
								}
								if (parDeptUserBeans[j].getParticipantUserBeans() != null) { //如果有参与者
									WorkFlowParticipantUserBean[] parUserBeans = parDeptUserBeans[j].getParticipantUserBeans(); //该环节上的所有参与者中的某一个参考者的所有参与人员.
									for (int k = 0; k < parUserBeans.length; k++) {
										String str_identifyKey = parUserBeans[k].getUserid() + "#" + parUserBeans[k].getAccrUserid() + "#" + parUserBeans[k].isCCTo(); //接收人员,授权人员,抄送
										if (!hs_taskVOs.containsKey(str_identifyKey)) { //
											DealTaskVO taskVO = getDealTaskVOByCompute(allDealActs[i], parUserBeans[k]); //创建待处理任务,既有环节的信息,也有根据环节参与者定义而计算出来的人员信息!!
											hs_taskVOs.put(str_identifyKey, taskVO); ////
										}
									}
								}
							} //end for
							DealTaskVO[] taskVOs = (DealTaskVO[]) hs_taskVOs.values().toArray(new DealTaskVO[0]); //
							//这里需要按照任务的机构名称排序!!!
							Arrays.sort(taskVOs, new DealTaskVOComparator()); //按照机构名称排序一把!!!很重要,因为当人多的时候,这样会清爽许多!!!
							allDealActs[i].setDealTaskVOs(taskVOs); //设置该环节的待办任务!!
						}
					} // end normal submit
				}
				firstWFParVO.setDealActivityVOs(allDealActs); //设置所有目标环节!!!
				if (hvo_dealrecord.getStringValue("curractivity_isassignapprover").equals("Y")) { // 如果需要人工选择参与者!!返回一个标记，UI端看到这个标记就弹出一个列表
					firstWFParVO.setIsassignapprover(true); //需要人工选定参与者this.firstTaskVO
				} else { // 如果不是需要人工选择参与者,则返回所有参与者.UI端隐藏的将这些参与者记住!然后一起提交
					firstWFParVO.setIsassignapprover(false); //不需要人工选定参与者
				}
			}

			//进行业务动态二次过滤
			busiDynSecondFilter(_wfinstanceid, _billVO.getStringValue("billtype"), _billVO.getStringValue("busitype"), _billVO, firstWFParVO.getDealActivityVOs()); //对流程引擎生成的参与者与环节进行二次业务动态过滤!!
		} else { //如果我不是终结者.即只需修改当前任务而已
			firstWFParVO.setIsprocessed(false); //不是终结者
		}

		if (hvo_dealrecord.getStringValue("curractivity_isneedmsg").equals("Y")) { // 如果必须输入批语..
			firstWFParVO.setIsneedmsg(true); //
		} else {
			firstWFParVO.setIsneedmsg(false); //
		}

		return firstWFParVO; //
	}

	/**
	 * 退回到任意一步的第一次取得的参与者
	 * 它的逻辑与一般的提交不一样,与连线拒绝也不一样.
	 * 最大的区别就是它是一种默认机制,没有有使用连线来驱动。
	 * 从某种意义上说以后工作流设计也许只需要描出正向的连线而不需要逆向的连线,逆向就是回退的含义，而且可以是退回到任意一个环节!!
	 * 以前退回界面与提交界面一样,即列出各个环节,然后一个环节是一个页签!!!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @param _billVO
	 * @return
	 */
	public WFParVO getFirstTaskVO_Reject(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO) throws Exception {
		//首先确认有我的任务!但这个视图好象有性能问题,可能需要优化!即拆成三条SQL
		HashVO hvo_dealrecord = getLoginUserDealRecord(_wfinstanceid, _prdealPoolId, _loginuserid); //

		//按道理应该只会找到一条数据!!即属于我的任务..
		WFParVO firstWFParVO = new WFParVO(); //
		firstWFParVO.setIsprocessed(true); //退回时,不管该环节是否是会签模式,都永远可以是抢占式退回
		firstWFParVO.setWfinstanceid(_wfinstanceid); //设置流程实例主键!
		setFirstWFParVOFromHVO(firstWFParVO, hvo_dealrecord); //将查询出的所有关键信息都赋给WFParVO

		//算出下一步的所有参与者,这与普通提交方式最大的区别所在,即普通提交方式是根据流程图算出当前环节的下一步所有连线的目标环节的所有参与者的并集,而这里却是去流程处理历史表中找出所有处理过的记录!
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select "); //
		sb_sql.append("pub_wf_dealpool.prinstanceid,"); //
		sb_sql.append("pub_wf_dealpool.realsubmiter userid,"); //实际提交的人!
		sb_sql.append("pub_wf_dealpool.realsubmitername username,"); //实际提交的人!
		sb_sql.append("pub_wf_dealpool.participant_accruserid   accruserid,"); //授权人!
		sb_sql.append("pub_wf_dealpool.participant_accrusercode accrusercode,"); //授权人!
		sb_sql.append("pub_wf_dealpool.participant_accrusername accrusername,"); //授权人!
		sb_sql.append("pub_wf_dealpool.curractivity,"); //环节
		sb_sql.append("pub_wf_activity.code curractivity_code,"); //环节编码
		sb_sql.append("pub_wf_activity.wfname curractivity_name,"); //环节名称
		sb_sql.append("pub_wf_activity.activitytype curractivity_type,"); //环节类型
		sb_sql.append("pub_wf_activity.belongdeptgroup curractivity_belongdeptgroup,");
		sb_sql.append("pub_wf_activity.approvemodel curractivity_approvemodel,"); //环节审批模式.
		sb_sql.append("pub_wf_activity.participate_group,"); //静态参与组.
		sb_sql.append("pub_wf_activity.participate_dynamic "); //动态参与者.
		sb_sql.append("from pub_wf_dealpool "); //
		sb_sql.append("left outer join pub_wf_activity on pub_wf_dealpool.curractivity=pub_wf_activity.id "); //
		sb_sql.append("where prinstanceid='" + _wfinstanceid + "' and isprocess='Y' and issubmit='Y' order by pub_wf_dealpool.id"); //找出曾经已经过了的,且是提交同意的所有历史记录

		HashVO[] hvs_returnactivitys = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //取得所有可以返回环节上参与者
		HashVO[] hvs_alluser = appendMyCorpAndRoleInfo(hvs_returnactivitys); //补上角色与机构,userdept,userdeptcode,userdeptname,roleid,rolecode,rolename
		//new TBUtil().writeHashToHtmlTableFile(hvs_alluser, "C:/www.html"); //
		for (int i = 0; i < hvs_returnactivitys.length; i++) {
			HashVO hvoUserItem = getTBUtil().findHashVOFromHVS(hvs_alluser, "id", hvs_returnactivitys[i].getStringValue("userid")); ////
			if (hvoUserItem != null) {
				hvs_returnactivitys[i].setAttributeValue("userdept", hvoUserItem.getStringValue("userdept")); //
				hvs_returnactivitys[i].setAttributeValue("userdeptcode", hvoUserItem.getStringValue("userdeptcode")); //
				hvs_returnactivitys[i].setAttributeValue("userdeptname", hvoUserItem.getStringValue("userdeptname")); //
				hvs_returnactivitys[i].setAttributeValue("userroleid", hvoUserItem.getStringValue("roleid")); //
				hvs_returnactivitys[i].setAttributeValue("userrolecode", hvoUserItem.getStringValue("rolecode")); //
				hvs_returnactivitys[i].setAttributeValue("userrolename", hvoUserItem.getStringValue("rolename")); //
			}
		}

		HashSet hs_identified = new HashSet(); //
		ArrayList list_taskVOs = new ArrayList(); //
		boolean ishavegroup = cn.com.infostrategy.bs.common.SystemOptions.getBooleanValue("提交界面是否显示环节所属组的名称", true);
		String groupname, activityname = null;
		for (int i = 0; i < hvs_returnactivitys.length; i++) { //遍历所有数据!!
			if (firstWFParVO.getCurractivity().equals(hvo_dealrecord.getStringValue("curractivity")) && _loginuserid.equals(hvs_returnactivitys[i].getStringValue("userid"))) { //如果接收者就是登录人员,则不出现!即不能退回给自己!
				continue; //出现环节等于当前环节,人员等于当前人员,即不能在本环节退回给本人!!
			}
			String str_submituserid = hvs_returnactivitys[i].getStringValue("userid"); //
			//这里以前取的hvo_dealrecord.getStringValue("curractivity")，导致多个环节同一个人审核，只能退回到该人第一次审核的环节，但太平有业务需求退回必须退回到某个环节，
			//故修改为同一环节同一个人的记录不重复即可，这也可能是以前徐老师的意思，只是他未考虑到同一个人审批多个环节的情况【李春娟/2017-05-11】
			String str_identified_key = hvs_returnactivitys[i].getStringValue("curractivity") + "#" + str_submituserid;
			if (hs_identified.contains(str_identified_key)) {
				continue; //如果出现相同的环节与人,则只显示一个,即重复的环节与人不出现多次!比如【科长】在【科长审核】环节审过两次了!
			}
			hs_identified.add(str_identified_key); //加入,这样下次就会不重复出现
			DealTaskVO taskVO = new DealTaskVO(); //一条任务.
			taskVO.setFromActivityId(hvo_dealrecord.getStringValue("curractivity")); //从什么环节跳过去的,就是当前任务的环节!
			taskVO.setFromActivityName(hvo_dealrecord.getStringValue("curractivity_wfname")); //从什么环节跳过去的,就是当前任务的环节!

			taskVO.setTransitionId(null); //因为是直接跳转,所有没有连线
			taskVO.setTransitionCode(null); //因为是直接跳转,所有没有连线
			taskVO.setTransitionName(null); //因为是直接跳转,所有没有连线
			taskVO.setTransitionDealtype(null); //因为是直接跳转,所有没有连线
			taskVO.setTransitionMailSubject(null); //因为是直接跳转,所有没有连线.
			taskVO.setTransitionMailContent(null); //因为是直接跳转,所有没有连线
			taskVO.setTransitionIntercept(null); //连线的拦截器,因为是跳转,所以没有经过任何连线,所以不做任何拦截器处理

			taskVO.setCurrActivityId(hvs_returnactivitys[i].getStringValue("curractivity")); //跳到哪一个环节的主键
			taskVO.setCurrActivityCode(hvs_returnactivitys[i].getStringValue("curractivity_code")); //跳到哪一个环节的编码
			//			taskVO.setCurrActivityName(hvs_returnactivitys[i].getStringValue("curractivity_name")); //跳到哪一个环节的名称

			activityname = hvs_returnactivitys[i].getStringValue("curractivity_name", "");
			activityname = activityname.replaceAll("\r", "");
			activityname = activityname.replaceAll("\n", "");
			if (ishavegroup) {
				groupname = hvs_returnactivitys[i].getStringValue("curractivity_belongdeptgroup", "");
				groupname = groupname.replaceAll("\r", "");
				groupname = groupname.replaceAll("\n", "");
				if (!"".equals(groupname)) {
					taskVO.setCurrActivityName(groupname + "-" + activityname);
				} else {
					taskVO.setCurrActivityName(activityname);
				}
			} else {
				taskVO.setCurrActivityName(activityname); //跳到哪一个环节的名称
			}

			taskVO.setCurrActivityType(hvs_returnactivitys[i].getStringValue("curractivity_type")); //跳到哪一个环节的类型
			taskVO.setCurrActivityApproveModel("1"); //退回给的被接受者的审批模式默认应该是抢占,但也可能是会签(即根据目标环节的定义来选)!! 但因为退回的多个人可能会处于多个环节,到底取哪个环节的? 或者说退回后,最新的当前环节到底是谁?是原来的环节？还是接收者的环节？但接收者环节是多个,到底算哪个?
			//后来经过仔细思考,退回时默认应是抢占,但提交者可修改,各个任务还是各是各的环节,但大家是一批任务,如果是抢占，则第一个按自己的环节上定义的连线出去,如果是会签,则是最后一个人按自己环节上定义的连线出去!按道理退回时不应该有会办子流程模式!!因为子流程模式永远只支出现在一个流程的创建任务中!!!

			taskVO.setParticipantUserId(hvs_returnactivitys[i].getStringValue("userid")); //参与者ID
			String codeAndName = hvs_returnactivitys[i].getStringValue("username", "");//这里有点问题sunfujun/20121114
			if (codeAndName.indexOf("/") >= 0) {
				taskVO.setParticipantUserCode(codeAndName.substring(0, codeAndName.indexOf("/") + 1));
				taskVO.setParticipantUserName(codeAndName.substring(codeAndName.indexOf("/") + 1, codeAndName.length()));
			} else {
				taskVO.setParticipantUserCode(codeAndName); //参与者Code
				taskVO.setParticipantUserName(codeAndName); //参与者名称
			}
			taskVO.setParticipantUserDeptId(hvs_returnactivitys[i].getStringValue("userdept")); //参与者ID
			taskVO.setParticipantUserDeptCode(hvs_returnactivitys[i].getStringValue("userdeptcode")); //参与者Code
			taskVO.setParticipantUserDeptName(hvs_returnactivitys[i].getStringValue("userdeptname")); //参与者名称

			taskVO.setParticipantUserRoleId(hvs_returnactivitys[i].getStringValue("userroleid")); //参与者ID
			taskVO.setParticipantUserRoleCode(hvs_returnactivitys[i].getStringValue("userrolecode")); //参与者角色Code.
			taskVO.setParticipantUserRoleName(hvs_returnactivitys[i].getStringValue("userrolename")); //参与者角色名称.

			String str_accruserid = hvs_returnactivitys[i].getStringValue("accruserid"); //授权人!
			if (str_accruserid != null && !str_accruserid.equals(str_submituserid)) { //如果授权人不为空,并且与实际处理人不是同一人!!
				taskVO.setAccrUserId(str_accruserid); //
				String str_accrusercode = hvs_returnactivitys[i].getStringValue("accrusercode");
				String str_accrusername = hvs_returnactivitys[i].getStringValue("accrusername");
				taskVO.setAccrUserCode(str_accrusercode); //
				taskVO.setAccrUserName(str_accrusername); //
				if (str_accrusername != null && str_accrusername.indexOf("/") > 0) {
					str_accrusername = str_accrusername.substring(str_accrusername.indexOf("/") + 1, str_accrusername.length()); //
				}
				taskVO.setParticipantUserName(taskVO.getParticipantUserName() + "(" + str_accrusername + "授权)"); //
			}

			taskVO.setSuccessParticipantReason("直接跳转选择的"); //
			list_taskVOs.add(taskVO); //
		}

		DealTaskVO[] dealTaskVOs = (DealTaskVO[]) list_taskVOs.toArray(new DealTaskVO[0]); ////

		//处理授权信息!即如果某个曾经提交的人做了授权处理,比如华兵授权给了李克振,那么曾经是华兵提交的,则这时也需要同时给两人都提交任务!!
		//为了提高性能,先一下子将所有授权的数据取出来,下面在计算参与者时要循环反复用到,所以需要先算出来!!!
		//		HashMap accreditAndProxyMap = getWFBSUtil().getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //取得授权信与代理信息!!!!
		//		String str_accredModel = getAccrModel(_billVO); //取得当前表单定义的授权模块,即不同的表单会有不同的授权模块!!发前是放在每个环节中计算的,但那样有几个环节就会算几次,性能较低!! 应该一次先算好,然后每次用!!!
		//		if (accreditAndProxyMap.size() > 0) { //如果定义了授权信息!!
		//			for (int i = 0; i < dealTaskVOs.length; i++) {
		//				String str_olduserid = dealTaskVOs[i].getParticipantUserId(); //原来计算出的参与者,
		//				if (accreditAndProxyMap.containsKey(str_olduserid)) { //如果这个用户做了授权!!
		//					HashVO hvo_accr = getWFBSUtil().getAccredProxyUserVO(accreditAndProxyMap, str_accredModel, str_olduserid);
		//					if (hvo_accr != null) { //取得授权情况
		//						String str_oldUserCode = dealTaskVOs[i].getParticipantUserCode(); //原来人员编码
		//						String str_oldUserName = dealTaskVOs[i].getParticipantUserName(); //原来人员名称
		//						dealTaskVOs[i].setParticipantUserId(hvo_accr.getStringValue("proxyuserid")); //我让位,是代理人成了处理人!
		//						dealTaskVOs[i].setParticipantUserCode(hvo_accr.getStringValue("proxyusercode")); //我让位,是代理人成了处理人!
		//						dealTaskVOs[i].setParticipantUserName(str_oldUserName + "(已授权" + hvo_accr.getStringValue("proxyusername") + ")"); //
		//
		//						dealTaskVOs[i].setParticipantUserDeptId(hvo_accr.getStringValue("proxyuserdeptid")); //
		//						dealTaskVOs[i].setParticipantUserDeptCode(hvo_accr.getStringValue("proxyuserdeptcode")); //
		//						dealTaskVOs[i].setParticipantUserDeptName(hvo_accr.getStringValue("proxyuserdeptname")); //
		//
		//						dealTaskVOs[i].setParticipantUserRoleId(hvo_accr.getStringValue("proxyuserroleid")); //
		//						dealTaskVOs[i].setParticipantUserRoleCode(hvo_accr.getStringValue("proxyuserrolecode")); //
		//						dealTaskVOs[i].setParticipantUserRoleName(hvo_accr.getStringValue("proxyuserrolename")); //
		//
		//						dealTaskVOs[i].setAccrUserId(str_olduserid); //授权人ID!
		//						dealTaskVOs[i].setAccrUserCode(str_oldUserCode); //授权人编码!
		//						dealTaskVOs[i].setAccrUserName(str_oldUserName); //授权人名称!
		//					}
		//				}
		//			}
		//		}

		DealActivityVO dealActivityVO = new DealActivityVO(); //退回操作时就一个环节!目前设计的是没有来源环节,来源连线等!! 但后来觉得来源环节还是有的,但连线应该没有!
		dealActivityVO.setFromActivityId(hvo_dealrecord.getStringValue("curractivity")); //从哪个环节来的!
		dealActivityVO.setFromActivityCode(hvo_dealrecord.getStringValue("curractivity_code")); //从哪个环节Code
		dealActivityVO.setFromActivityName(hvo_dealrecord.getStringValue("curractivity_wfname")); //环节名称

		dealActivityVO.setFromTransitionId(null); //
		dealActivityVO.setFromTransitionCode(null); //
		dealActivityVO.setFromTransitionName(null); //
		dealActivityVO.setFromTransitionIntercept(null); //
		dealActivityVO.setFromTransitionMailsubject(null); //邮件主题
		dealActivityVO.setFromTransitionMailcontent(null); //邮件内容

		dealActivityVO.setActivityId(null); //接收/目标环节的id,因为各个任务的
		dealActivityVO.setActivityCode(null); //接收/目标环节的Code
		dealActivityVO.setActivityName("选择可以退回的人员"); //
		dealActivityVO.setActivityType(null); //
		dealActivityVO.setApprovemodel(null); //审批模式
		dealActivityVO.setDealTaskVOs(dealTaskVOs); //设置退回界面的所有人员

		firstWFParVO.setDealActivityVOs(new DealActivityVO[] { dealActivityVO }); //

		if (hvo_dealrecord.getStringValue("curractivity_isassignapprover").equals("Y")) { // 如果需要人工选择参与者!!返回一个标记，UI端看到这个标记就弹出一个列表
			firstWFParVO.setIsassignapprover(true); //需要人工选定参与者this.firstTaskVO
		} else { // 如果不是需要人工选择参与者,则返回所有参与者.UI端隐藏的将这些参与者记住!然后一起提交
			firstWFParVO.setIsassignapprover(false); //不需要人工选定参与者
		}

		if (hvo_dealrecord.getStringValue("curractivity_isneedmsg").equals("Y")) { // 如果必须输入批语..
			firstWFParVO.setIsneedmsg(true); //
		} else {
			firstWFParVO.setIsneedmsg(false); //
		}
		return firstWFParVO; //
	}

	/**
	 * 第二次提交,即由前台传过来的数据
	 * @param _secondCallVO
	 * @param _loginuserid
	 */
	public BillVO secondCall(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		//先判断一把,因为完全有可能有人在选择人员的这个发呆时间内抢占处理了!
		HashVO hvo_judge_dealpool = judgeTaskDeal(_secondCallVO.getWfinstanceid(), _secondCallVO.getDealpoolid(), _loginuserid); //
		if (!hvo_judge_dealpool.getBooleanValue("任务是否有效")) {
			throw new WLTAppException(hvo_judge_dealpool.getStringValue("原因说明")); //
		}

		String str_loginusername = getUserCodeNameById(_loginuserid); //
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginuserid); //

		String str_billVOToStringField = getCommDMO().getStringValueByDS(null, "select tostringkey from pub_templet_1 where templetcode='" + _secondCallVO.getBilltempletcode() + "'"); //发现有的表单的任务事项设置不起效果,干脆从后台直接取一下!但这样性能会慢一点!
		_billVO.setToStringFieldName(str_billVOToStringField); //
		_billVO.setObject("WFPRINSTANCEID", new StringItemVO(_secondCallVO.getWfinstanceid())); //设置流程实例主键

		intercept2BeforeAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //执行拦截器2的前置操作

		String str_dealpoolid = _secondCallVO.getDealpoolid(); //主键,即处理的是哪一条任务!!!!!!    至关重要!!!
		String str_prinstanceid = _secondCallVO.getWfinstanceid(); //流程实例主键!!305
		String str_parentInstanceId = _secondCallVO.getParentinstanceid(); //父流程实例ID
		String str_rootinstanceid = _secondCallVO.getRootinstanceid(); //根流程实例Id
		String str_billTempletCode = _secondCallVO.getBilltempletcode(); //单据模板
		String str_billtableName = _secondCallVO.getBilltablename(); //业务表名
		String str_billQueryTableName = _secondCallVO.getBillQueryTablename(); //保存的表名
		String str_billPkName = _secondCallVO.getBillpkname(); //业务表主键名
		String str_billPkValue = _secondCallVO.getBillpkvalue(); //业务表主键值
		String str_processid = _secondCallVO.getProcessid(); //流程定义主键!
		String str_fromactivity = _secondCallVO.getFromactivity(); //From环节主键
		String str_curractivity = _secondCallVO.getCurractivity(); //当前环节主键!!!  至关重要!!!
		String str_currbatchno = _secondCallVO.getBatchno(); //当前批号,关键信息!
		String str_message = _secondCallVO.getMessage(); //提交意见..
		String str_messagefile = _secondCallVO.getMessagefile(); //提交意见的附件..
		String str_approveresult = getApproveResult(_dealtype); //提交类型,即提交是Y,退回是N
		String str_submitreasoncode = getSubmitReasonCode(_secondCallVO); //提交的意见分类,比如因何原因而退回
		String str_currtime = getTBUtil().getCurrTime(); //当前时间

		DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //取得所有待处理任务,即从前台选中的实际参与者!!!
		String[] str_submitTos = getSubmitToInfo(commitTaskVOs); //先计算出接收者的人员,环节,连线等信息,下面要用到!!
		String str_submittouser = str_submitTos[0]; //提交给谁
		String str_submittousername = str_submitTos[1]; //提交给谁的名称
		String str_submittocorp = str_submitTos[2]; //提交给的机构!
		String str_submittocorpName = str_submitTos[3]; //提交给的机构!
		String submittotransition = str_submitTos[4]; //提交给哪个连线 
		String submittoactivity = str_submitTos[5]; //提交给哪个环节
		String submittoactivityName = str_submitTos[6]; //提交给哪个环节的名称!

		int li_newBatchNo = 0;
		if (_secondCallVO.isIsprocessed()) { //如果是终结者,则要计算新的批号!
			li_newBatchNo = getNewBatchNo(str_prinstanceid); //取得新的批号,如果是第一次则返回1,否则返回实例表中的当前批号+1
		} else {
			li_newBatchNo = Integer.parseInt(str_currbatchno);//sunfujun/20120810/会签任务转办
		}

		ArrayList al_sqls = new ArrayList(); //用来装载SQL的容器!!!
		//关键逻辑处理,最多的情况对pub_wf_prinstance修改了1次,对pub_wf_dealpool修改了3次!!
		//先处理任务!!!关键,其实就是个搬家程序,即将pub_task_deal中的记录移到pub_task_off表中
		String str_sql_insert_taskoff = getInsertTaskOffSQL(str_dealpoolid, _loginuserid, str_loginusername, str_currtime, str_message, str_submittouser, str_submittousername, str_submittocorp, str_submittocorpName, true, null); //
		if (str_sql_insert_taskoff != null) {
			al_sqls.add(str_sql_insert_taskoff); //
		}
		al_sqls.add("delete from pub_task_deal where prdealpoolid='" + str_dealpoolid + "'"); //删除本条任务!!!

		//先接收该任务
		al_sqls.add("update pub_wf_dealpool set isreceive='Y',receivetime='" + str_currtime + "' where id='" + str_dealpoolid + "' and (isreceive is null or isreceive='N')"); //如果没有没有接收,则直接接收,即提交与接受是同一时间!

		//处理当前流程实例.
		String[] str_instHistInfos = getNewSubmiterHist(str_prinstanceid, _loginuserid, str_currtime, str_message); //取得新的提交人的历史清单,就是在原有清单后面再加上本人!
		String str_newsubmiterhist = str_instHistInfos[0]; //
		String str_mylastsubmittime = str_instHistInfos[1]; //
		String str_mylastsubmitmsg = str_instHistInfos[2]; //

		UpdateSQLBuilder isql_1 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_prinstanceid + "'"); //
		isql_1.putFieldValue("lastsubmiter", _loginuserid); //最后提交者
		isql_1.putFieldValue("lastsubmitername", str_loginusername); //最后提交者名称
		isql_1.putFieldValue("lastsubmitresult", str_approveresult); //最后提交结果
		isql_1.putFieldValue("lastsubmitmsg", str_message); //最后提交的意见
		isql_1.putFieldValue("lastsubmitactivity", str_curractivity); //最后提交的环节
		isql_1.putFieldValue("lastsubmittime", str_currtime); //最后提交的时间
		isql_1.putFieldValue("submiterhist", str_newsubmiterhist); //历史提交的人
		isql_1.putFieldValue("mylastsubmittime", str_mylastsubmittime); //我的最后提交时间,用于在只显示一条业务单据时加载公式解析!
		isql_1.putFieldValue("mylastsubmitmsg", str_mylastsubmitmsg); //我的最后提交意见,用于在只显示一条业务单据时加载公式解析!
		isql_1.putFieldValue("status", "RUN"); //
		if (_secondCallVO.isIsprocessed()) { //如果是终结者,则要更新当前实例的[当前环节,当前所有者,当前批号]等信息
			isql_1.putFieldValue("curractivity", submittoactivity); //当前环节
			isql_1.putFieldValue("curractivityname", submittoactivityName); //当前环节名称
			isql_1.putFieldValue("currowner", str_submittouser); //当前的待办人
			isql_1.putFieldValue("currbatchno", li_newBatchNo); //当前批号,
		}
		al_sqls.add(isql_1.getSQL()); //

		//处理根流程
		if (str_rootinstanceid != null && !str_rootinstanceid.equalsIgnoreCase(str_prinstanceid)) { //如果根流程不等于本流程,则还要在根流程中记录下历史处理人!
			String[] str_rootInstHistInfos = getNewSubmiterHist(str_rootinstanceid, _loginuserid, str_currtime, str_message); //取得新的提交人的历史清单,就是在原有清单后面再加上本人!
			String str_root_newsubmiterhist = str_rootInstHistInfos[0]; //
			String str_root_mylastsubmittime = str_rootInstHistInfos[1]; //
			String str_root_mylastsubmitmsg = str_rootInstHistInfos[2]; //
			UpdateSQLBuilder isql_2 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_rootinstanceid + "'"); //
			isql_2.putFieldValue("submiterhist", str_root_newsubmiterhist); //历史提交的人
			isql_2.putFieldValue("mylastsubmittime", str_root_mylastsubmittime); //我的最后提交时间,用于在只显示一条业务单据时加载公式解析!
			isql_2.putFieldValue("mylastsubmitmsg", str_root_mylastsubmitmsg); //我的最后提交意见,用于在只显示一条业务单据时加载公式解析!
			al_sqls.add(isql_2.getSQL()); //
		}

		//处理当前流程任务.
		UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + str_dealpoolid + "'"); //
		isql_3.putFieldValue("issubmit", "Y"); //是否已提交
		//isql_2.putFieldValue("isprocess", "Y"); //是否已过? 这个需要吗?如果是会签的话,要不要将自己的任务置为isprocess=Y??
		isql_3.putFieldValue("submittime", str_currtime); //提交的时间
		isql_3.putFieldValue("realsubmiter", _loginuserid); //实际提交的人!
		isql_3.putFieldValue("realsubmitername", str_loginusername); //实际提交人的名称!
		isql_3.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //实际提交的机构!
		isql_3.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //实际提交的机构名称!
		isql_3.putFieldValue("submitisapprove", str_approveresult); //是同意还是退回
		isql_3.putFieldValue("submitreasoncode", str_submitreasoncode); //提交的原因分类编码,比如因何原因而退回!!
		isql_3.putFieldValue("submitmessage", str_message); //提交的意见
		isql_3.putFieldValue("submitmessagefile", str_messagefile); //提交的附件
		if (_secondCallVO.isIsprocessed()) { //如果是终结者
			isql_3.putFieldValue("isprocesser", "Y"); //是终结者!
			isql_3.putFieldValue("isprocess", "Y"); //过了
			isql_3.putFieldValue("submittouser", str_submittouser); //提交给谁
			isql_3.putFieldValue("submittousername", str_submittousername); //提交给谁
			isql_3.putFieldValue("submittocorp", str_submittocorp); //提交给哪个机构
			isql_3.putFieldValue("submittocorpname", str_submittocorpName); //提交给哪个机构的名称
			isql_3.putFieldValue("submittotransition", submittotransition); //提交给什么连线,有了分支模式后,应该可以有多个!
			isql_3.putFieldValue("submittoactivity", submittoactivity); //提交给什么环节,有了分支模式后,应该可以有多个!
			isql_3.putFieldValue("submittobatchno", li_newBatchNo); //提交给哪个批号!!
		} else {
			isql_3.putFieldValue("isprocesser", "N"); //不是终结者!
		}
		isql_3.putFieldValue("participant_yjbduserid", _secondCallVO.getDealpooltask_yjbduserid()); //补登人id
		isql_3.putFieldValue("participant_yjbdusercode", _secondCallVO.getDealpooltask_yjbdusercode()); //补登人编码
		isql_3.putFieldValue("participant_yjbdusername", _secondCallVO.getDealpooltask_yjbdusername()); //补登人名称
		al_sqls.add(isql_3.getSQL()); //

		//如果是终结者，即需要完成该环节,同时为下一环节生成任务.(是否是终结者已在第一次计算任务时取得了!!!即在抢占或会签的最后一步)
		if (_secondCallVO.isIsprocessed() || "SEND".equals(_dealtype)) {
			if (_secondCallVO.isIsprocessed() && !"4".equals(_secondCallVO.getApproveModel())) {
				al_sqls.add("update pub_wf_dealpool set isprocess='Y' where prinstanceid='" + str_prinstanceid + "' and batchno='" + str_currbatchno + "' and isprocess='N'"); //修改本批数据,表示环节结束!这是最重要的逻辑之一!!! 如果是终结者,且没有其他分路的任务,且又没有创建新任务,则就意味着这个流程是可以结束的!!!
				//会签-不同部门会签,同部门抢占 【杨科/2013-04-25】
				if (getTBUtil().getSysOptionBooleanValue("会签是否启用不同部门会签,同部门抢占", false)) {
					String str_parentinstanceid = _secondCallVO.getParentinstanceid();
					String str_createbyid = _secondCallVO.getCreatebyid();
					String str_participant_userdept = _secondCallVO.getParticipant_userdept();
					if (str_parentinstanceid != null && !str_parentinstanceid.equals("")) {
						al_sqls.add("update pub_wf_dealpool set isprocess='Y' where parentinstanceid='" + str_parentinstanceid + "' and createbyid = '" + str_createbyid + "' and batchno='1' and isprocess='N' and participant_userdept='" + str_participant_userdept + "'");
						al_sqls.add("update pub_wf_prinstance set status='END' where id in (select prinstanceid from pub_wf_dealpool where parentinstanceid='" + str_parentinstanceid + "' and createbyid = '" + str_createbyid + "' and batchno='1' and participant_userdept='" + str_participant_userdept + "' and prinstanceid<>'" + str_prinstanceid + "')");
					}
				}
			}
			if (commitTaskVOs != null) { //如果待处理任务不为空!		
				//为下一环节生成任务,即创建新的任务!因为后来增辑了子流程,所以显得更为复杂!!!
				for (int i = 0; i < commitTaskVOs.length; i++) { //为各个接收者创建新的任务!即在pub_wf_dealpool表中创建一批新的记录!!!如果接受者的环节类型是子流程,则是直接创建子流程!!!
					if ("3".equals(commitTaskVOs[i].getCurrActivityApproveModel()) && !commitTaskVOs[i].isCCTo()) { //如果审批模式是3,即子流程,并且不是抄送的信息!!则自动找出该环节的子流程id,然后为之创建子流程实例!!! 这是子流程实现的最关键的逻辑,即如何出去!!!
						//如果目标环节是子流程类型,则自动以目标环节上的几个接收者为子流程的启动者与创建者,自动创建N个子流程实例,然后这些接收者进入系统后,当前状态是子流程的启动环节,然后提交时就是直接沿着子流程的路径往下走!!!
						String str_childwfid = getCommDMO().getStringValueByDS(null, "select childwfcode from pub_wf_activity where id='" + commitTaskVOs[i].getCurrActivityId() + "'"); //
						if (str_childwfid == null || str_childwfid.trim().equals("")) {
							throw new WLTAppException("环节[" + commitTaskVOs[i].getCurrActivityName() + "]的审批模式是子流程会办,但没有定义具体的子流程!"); ////
						}

						String str_childwf_startactivityid = getCommDMO().getStringValueByDS(null, "select id from pub_wf_activity where processid='" + str_childwfid + "' and activitytype='START'"); //找出子流程中的启动环节的id,必须要,因为子流程需要知道当前环节,然后知道如何从这个环节开始继续走下去!
						if (str_childwf_startactivityid == null || str_childwf_startactivityid.trim().equals("")) {
							throw new WLTAppException("子流程[" + str_childwfid + "]没有定义一个类型为START的环节,即找不到从哪个环节开始自动启动!"); //
						}

						//创建子流程实例,并启动之!!! 所谓以某一个人启动一个流程实例,实际上就是往 pub_wf_prinstance,pub_wf_dealpool,pub_task_deal这三张表中各插入一条记录而已!! 但由于是子流程,可能还要往pub_task_deal表中插入数据!!有两种方式解决插入子流程的人员，一种是在主流程中插入,一种是在子流程中插入!!!!!
						String str_newInstanceId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_PRINSTANCE"); //新的流程实例主键!
						String str_newInstanceSQL = getInsertPrinstanceSQL(str_newInstanceId, str_prinstanceid, str_rootinstanceid, str_childwfid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserId(), str_childwf_startactivityid, commitTaskVOs[i].getCurrActivityId(),
								str_dealpoolid); //创建子流程实例

						//为子流程创建第一条任务,参与者是主流程选中的这些人员!
						DealTaskVO dealTaskVO = new DealTaskVO(); //
						dealTaskVO.setFromActivityId(null); //因为是启动的,所以不知从哪个环节来的
						dealTaskVO.setTransitionId(null); //因为是启动的,所以不知从哪根连线来的
						dealTaskVO.setCurrActivityId(str_childwf_startactivityid); //当前环节,即启动环节!
						dealTaskVO.setCurrActivityApproveModel(commitTaskVOs[i].getCurrActivityApproveModel()); //审批模式
						dealTaskVO.setParticipantUserId(commitTaskVOs[i].getParticipantUserId()); //参与者的ID
						dealTaskVO.setParticipantUserCode(commitTaskVOs[i].getParticipantUserCode()); //参与者名称
						dealTaskVO.setParticipantUserName(commitTaskVOs[i].getParticipantUserName()); //参与者名称
						dealTaskVO.setParticipantUserDeptId(commitTaskVOs[i].getParticipantUserDeptId()); //参与者机构id
						dealTaskVO.setParticipantUserDeptName(commitTaskVOs[i].getParticipantUserDeptName()); //机构名称
						dealTaskVO.setParticipantUserRoleId(commitTaskVOs[i].getParticipantUserRoleId()); //角色id..
						dealTaskVO.setParticipantUserRoleName(commitTaskVOs[i].getParticipantUserRoleName()); //角色Name
						dealTaskVO.setAccrUserId(commitTaskVOs[i].getAccrUserId()); //授权人
						dealTaskVO.setCCTo(commitTaskVOs[i].isCCTo()); //是否抄送!!!

						String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_DEALPOOL"); //流程实例的主键!!
						String str_newDealPoolSQL = getInsertDealPoolSQL(str_newDealPoolId, str_newInstanceId, str_prinstanceid, str_rootinstanceid, str_childwfid, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], dealTaskVO, "N", "Y", "", str_dealpoolid, null, 1, str_currtime, null, null, "CF", false, null, null, null, null); //

						//创建首页待办任务!!
						String str_msg = getDealTaskMsg(_secondCallVO.getProcessid(), commitTaskVOs[i].getFromActivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_newDealPoolId, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //生成消息提示,是可配置的!
						String str_newTaskDealSQL = getInsertTaskDealSQL(str_newDealPoolId, str_newInstanceId, str_parentInstanceId, str_rootinstanceid, str_dealpoolid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, _billVO.toString(), str_message, str_msg, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1],
								str_currtime, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserName(), commitTaskVOs[i].getParticipantUserDeptId(), commitTaskVOs[i].getParticipantUserDeptName(), dealTaskVO.getAccrUserId(), _secondCallVO.getPrioritylevel(), _secondCallVO.getDealtimelimit(), commitTaskVOs[i].isCCTo()); //创建任务SQL
						al_sqls.add(str_newInstanceSQL); //创建子流程
						al_sqls.add(str_newDealPoolSQL); //创建子流程的启动任务
						al_sqls.add(str_newTaskDealSQL); //创建首页任务SQL.

						//回写本任务的生命周期的状态为CC
						al_sqls.add("update pub_wf_dealpool set lifecycle='CC' where id='" + str_dealpoolid + "'"); //记录该任务的生命周期类型是创建了子流程!!
					} else { //如果不是子流程,即抢占与会签模式,则是原来的逻辑!!即在主流程中往pub_wf_dealpool与pub_task_deal两张表中插入数据!!
						//生成待处理任务.
						String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_dealpool"); //
						String str_sql_insert_dealpool = getInsertDealPoolSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_processid, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], commitTaskVOs[i], "N", null, null, str_dealpoolid, null, li_newBatchNo, str_currtime, _secondCallVO.getPrioritylevel(), _secondCallVO
								.getDealtimelimit(), null, _secondCallVO.isSecondIsSelfcycleclick(), _secondCallVO.getSecondSelfcycle_fromrolecode(), _secondCallVO.getSecondSelfcycle_fromrolename(), _secondCallVO.getSecondSelfcycle_torolecode(), _secondCallVO.getSecondSelfcycle_torolename()); //
						al_sqls.add(str_sql_insert_dealpool);

						//生成首页的任务提醒
						String str_msg = getDealTaskMsg(_secondCallVO.getProcessid(), commitTaskVOs[i].getFromActivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_newDealPoolId, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //生成消息提示,是可配置的!
						String str_sql_insert_dealtask = getInsertTaskDealSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_dealpoolid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, _billVO.toString(), str_message, str_msg, _loginuserid, str_loginusername, str_loginUserDeptIdName[0],
								str_loginUserDeptIdName[1], str_currtime, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserName(), commitTaskVOs[i].getParticipantUserDeptId(), commitTaskVOs[i].getParticipantUserDeptName(), commitTaskVOs[i].getAccrUserId(), _secondCallVO.getPrioritylevel(), _secondCallVO.getDealtimelimit(), commitTaskVOs[i].isCCTo()); //首页提醒任务!!!
						al_sqls.add(str_sql_insert_dealtask); //
					} //非子流程创建下游数据!!!
				} //end 【for (int i = 0; i < commitTaskVOs.length; i++)】
			} //end 【if (commitTaskVOs != null)】 
		} //enc 【if (_secondCallVO.isIsprocessed())】

		getCommDMO().executeBatchByDS(null, al_sqls); //执行批处理SQL,这一步很容易造成性能问题,但只要创建索引后就会明显提高,索引是:create index in_pub_wf_dealpool_3 on pub_wf_dealpool (prinstanceid,batchno,ispass);

		//如果当前环节是END类型,则将流程实例修改成END??  即可能将最后的环节的类型设成了结束类型!!! 如果最后环节的类型是[END]，则提交时就不弹出选择用户框,而直接提示,你是最后一步了!!
		if (_secondCallVO.getCurractivityType() != null && _secondCallVO.getCurractivityType().equalsIgnoreCase("END")) { //如果是结束类型的环节,且又是最后一个的终结者,则将流程实例修改成END
			boolean isCanRealEnd = judgeWFisRealEnd(str_prinstanceid); //判断本流程是否可以实际结束,如果可以实际结束,则才能真正结束!!!
			if (isCanRealEnd) { //如果是真正可以结束,才真正结束!!!
				al_sqls.add("update pub_wf_prinstance set status='END' where id='" + str_prinstanceid + "'"); ////
			}
		}

		//执行连线上的公式,遍历执行各个连线上的公式!!!!关键之关键!!!像RouteMark等功能效果就必须使用其实现!!
		execTransitionIntercept(_secondCallVO, str_prinstanceid, str_dealpoolid, _billVO); //

		//发邮件,sfj后来修改....
		sendMail(_secondCallVO, _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message); //

		//执行环节上的拦截器2..
		intercept2AfterAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //执行拦截器2的后置操作

		return _billVO; //
	}

	/**
	 * 退回操作,即不走线! 现在有个小问题就是退回给提交人时,没有取得接收者的机构信息!!
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @return
	 * @throws Exception
	 */
	public BillVO secondCall_Reject(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO) throws Exception {
		HashVO hvo_judge_dealpool = judgeTaskDeal(_secondCallVO.getWfinstanceid(), _secondCallVO.getDealpoolid(), _loginuserid); //
		if (!hvo_judge_dealpool.getBooleanValue("任务是否有效")) {
			throw new WLTAppException(hvo_judge_dealpool.getStringValue("原因说明")); //
		}

		String str_loginusername = getUserCodeNameById(_loginuserid); //取得登录人员名称,是【003/张三】的格式,因为考虑到重名!
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginuserid); //
		String str_billVOToStringField = getCommDMO().getStringValueByDS(null, "select tostringkey from pub_templet_1 where upper(templetcode)='" + _secondCallVO.getBilltempletcode().toUpperCase() + "'"); //发现有的表单的任务事项设置不起效果,干脆从后台直接取一下!但这样性能会慢一点!
		_billVO.setToStringFieldName(str_billVOToStringField); //
		_billVO.setObject("WFPRINSTANCEID", new StringItemVO(_secondCallVO.getWfinstanceid())); //设置流程实例主键??为什么要再一下?难道会一开始没有?

		intercept2BeforeAction(_secondCallVO, _loginuserid, _billVO, "REJECT");
		//		WFIntercept2IFC intercept2 = null;
		//		if (_secondCallVO.getIntercept2() != null && !_secondCallVO.getIntercept2().trim().equals("")) {
		//			intercept2 = (WFIntercept2IFC) Class.forName(_secondCallVO.getIntercept2()).newInstance(); //创建拦截器2
		//		}
		//		//执行前置拦截器
		//		if (intercept2 != null) {
		//			intercept2.beforeAction(_secondCallVO, _loginuserid, _billVO, "REJECT"); //
		//		}
		String str_prinstanceid = _secondCallVO.getWfinstanceid(); //流程实例主键!!
		String str_parentInstanceId = _secondCallVO.getParentinstanceid(); //父流程实例ID
		String str_rootinstanceid = _secondCallVO.getRootinstanceid(); //根流程实例Id
		String str_billTempletCode = _secondCallVO.getBilltempletcode(); //单据模板
		String str_billtableName = _secondCallVO.getBilltablename(); //业务表名
		String str_billQueryTableName = _secondCallVO.getBillQueryTablename(); //保存的表名
		String str_billPkName = _secondCallVO.getBillpkname(); //业务表主键名
		String str_billPkValue = _secondCallVO.getBillpkvalue(); //业务表主键值
		String str_processid = _secondCallVO.getProcessid(); //流程定义主键!
		String str_dealpoolid = _secondCallVO.getDealpoolid(); //主键
		String str_fromactivity = _secondCallVO.getFromactivity(); //From环节主键
		String str_curractivity = _secondCallVO.getCurractivity(); //当前环节主键
		String str_batchno = _secondCallVO.getBatchno(); //原来的批号
		String str_message = _secondCallVO.getMessage(); //处理意见!!
		String str_messagefile = _secondCallVO.getMessagefile(); //批语附件..
		String str_approveresult = "N"; //因为是退回,所以同意结果为否!
		String str_currtime = getTBUtil().getCurrTime(); //当前时间!

		DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //取得所有待处理任务,即从前台选中的实际参与者!!!
		String[] str_submitTos = getSubmitToInfo(commitTaskVOs); //先计算出接收者的人员,环节,连线等信息,下面要用到!!
		String str_submittouser = str_submitTos[0]; //提交给谁
		String str_submittousername = str_submitTos[1]; //提交给谁的名称
		String str_submittocorp = str_submitTos[2]; //提交给的机构!
		String str_submittocorpName = str_submitTos[3]; //提交给的机构!
		String submittotransition = str_submitTos[4]; //提交给哪个连线 
		String submittoactivity = str_submitTos[5]; //提交给哪个环节
		String submittoactivityName = str_submitTos[6]; //提交给哪个环节的名称!

		int li_newBatchNo = 0;
		if (_secondCallVO.isIsprocessed()) { //如果是终结者,则要计算新的批号!
			li_newBatchNo = getNewBatchNo(str_prinstanceid); //取得新的批号,如果是第一次则返回1,否则返回实例表中的当前批号+1
		}

		ArrayList al_sqls = new ArrayList(); //存储所有的SQL
		//关键逻辑处理,最多的情况对pub_wf_prinstance修改了1次,对pub_wf_dealpool修改了3次!!
		//先处理任务!!!关键,其实就是个搬家程序,即将pub_task_deal中的记录移到pub_task_off表中
		String str_sql_insert_taskoff = getInsertTaskOffSQL(str_dealpoolid, _loginuserid, str_loginusername, str_currtime, str_message, str_submittouser, str_submittousername, str_submittocorp, str_submittocorpName, true, null); //
		if (str_sql_insert_taskoff != null) {
			al_sqls.add(str_sql_insert_taskoff); //
		}
		al_sqls.add("delete from pub_task_deal where prdealpoolid='" + str_dealpoolid + "'"); //删除本条任务!!!

		//先接收!!
		al_sqls.add("update pub_wf_dealpool set isreceive='Y',receivetime='" + str_currtime + "' where id='" + str_dealpoolid + "' and isreceive='N'"); //如果没有没有接收,则直接接收,即提交与接受是同一时间!

		//处理当前流程实例.
		String[] str_instHistInfos = getNewSubmiterHist(str_prinstanceid, _loginuserid, str_currtime, str_message); //取得新的提交人的历史清单,就是在原有清单后面再加上本人!
		String str_newsubmiterhist = str_instHistInfos[0]; //处理历史
		String str_mylastsubmittime = str_instHistInfos[1]; //我的最后处理时间!
		String str_mylastsubmitmsg = str_instHistInfos[2]; //我的最后处理意见!

		UpdateSQLBuilder isql_1 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_prinstanceid + "'"); //
		isql_1.putFieldValue("lastsubmiter", _loginuserid); //最后提交者
		isql_1.putFieldValue("lastsubmitername", str_loginusername); //最后提交者名称
		isql_1.putFieldValue("lastsubmitresult", str_approveresult); //最后提交结果
		isql_1.putFieldValue("lastsubmitmsg", str_message); //最后提交的意见
		isql_1.putFieldValue("lastsubmitactivity", str_curractivity); //最后提交的环节
		isql_1.putFieldValue("lastsubmittime", str_currtime); //最后提交的时间
		isql_1.putFieldValue("submiterhist", str_newsubmiterhist); //历史提交的人
		isql_1.putFieldValue("mylastsubmittime", str_mylastsubmittime); //我的最后提交时间,用于在只显示一条业务单据时加载公式解析!
		isql_1.putFieldValue("mylastsubmitmsg", str_mylastsubmitmsg); //我的最后提交意见,用于在只显示一条业务单据时加载公式解析!
		isql_1.putFieldValue("status", "RUN"); //
		if (_secondCallVO.isIsprocessed()) { //如果是终结者,则要更新当前实例的[当前环节,当前所有者,当前批号]等信息
			isql_1.putFieldValue("curractivity", submittoactivity); //当前环节
			isql_1.putFieldValue("curractivityname", submittoactivityName); //当前环节名称
			isql_1.putFieldValue("currowner", str_submittouser); //当前的待办人
			isql_1.putFieldValue("currbatchno", li_newBatchNo); //当前批号,
		}
		al_sqls.add(isql_1.getSQL()); //

		//处理根流程
		if (!str_rootinstanceid.equalsIgnoreCase(str_prinstanceid)) { //如果根流程不等于本流程,则还要在根流程中记录下历史处理人!
			String[] str_rootInstHistInfos = getNewSubmiterHist(str_rootinstanceid, _loginuserid, str_currtime, str_message); //取得新的提交人的历史清单,就是在原有清单后面再加上本人!
			String str_root_newsubmiterhist = str_rootInstHistInfos[0]; //
			String str_root_mylastsubmittime = str_rootInstHistInfos[1]; //
			String str_root_mylastsubmitmsg = str_rootInstHistInfos[2]; //
			UpdateSQLBuilder isql_2 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_rootinstanceid + "'"); //
			isql_2.putFieldValue("submiterhist", str_root_newsubmiterhist); //历史提交的人
			isql_2.putFieldValue("mylastsubmittime", str_root_mylastsubmittime); //我的最后提交时间,用于在只显示一条业务单据时加载公式解析!
			isql_2.putFieldValue("mylastsubmitmsg", str_root_mylastsubmitmsg); //我的最后提交意见,用于在只显示一条业务单据时加载公式解析!
			al_sqls.add(isql_2.getSQL()); //
		}

		//处理当前任务,即修改审批意见等
		UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + str_dealpoolid + "'"); //
		isql_3.putFieldValue("issubmit", "Y"); //
		isql_3.putFieldValue("submittime", str_currtime); //
		isql_3.putFieldValue("realsubmiter", _loginuserid); //实际提交的人!
		isql_3.putFieldValue("realsubmitername", str_loginusername); //实际提交人的名称!
		isql_3.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //实际提交的机构!
		isql_3.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //实际提交的机构!
		isql_3.putFieldValue("submitisapprove", str_approveresult); //
		isql_3.putFieldValue("submitreasoncode", (String) null); //
		isql_3.putFieldValue("submitmessage", str_message); //
		isql_3.putFieldValue("submitmessagefile", str_messagefile); //
		if (_secondCallVO.isIsprocessed()) { //如果是终结者
			isql_3.putFieldValue("isprocesser", "Y"); //是终结者!
			isql_3.putFieldValue("isprocess", "Y");
			isql_3.putFieldValue("submittouser", str_submittouser); //提交给谁
			isql_3.putFieldValue("submittousername", str_submittousername); //提交给谁
			isql_3.putFieldValue("submittocorp", str_submittocorp); //提交给哪个机构
			isql_3.putFieldValue("submittocorpname", str_submittocorpName); //提交给哪个机构的名称
			isql_3.putFieldValue("submittotransition", submittotransition); //提交给什么连线,有了分支模式后,应该可以有多个!
			isql_3.putFieldValue("submittoactivity", submittoactivity); //提交给什么环节,有了分支模式后,应该可以有多个!
			isql_3.putFieldValue("submittobatchno", li_newBatchNo); //提交给哪个批号!!
		} else {
			isql_3.putFieldValue("isprocesser", "N"); //不是终结者!
		}
		isql_3.putFieldValue("participant_yjbduserid", _secondCallVO.getDealpooltask_yjbduserid()); //补登人id
		isql_3.putFieldValue("participant_yjbdusercode", _secondCallVO.getDealpooltask_yjbdusercode()); //补登人编码
		isql_3.putFieldValue("participant_yjbdusername", _secondCallVO.getDealpooltask_yjbdusername()); //补登人名称

		al_sqls.add(isql_3.getSQL()); ////

		if (_secondCallVO.isIsprocessed()) { //如果是终结者
			//修改本批数据,表示环节结束
			if (!"4".equals(_secondCallVO)) { //加上这个判断一般不走退回
				al_sqls.add("update pub_wf_dealpool set isprocess='Y' where prinstanceid='" + str_prinstanceid + "' and batchno='" + str_batchno + "' and isprocess='N'"); //修改本批数据标记本环节结束
			}

			//为下一环节生成任务..
			if (commitTaskVOs != null) { //如果待处理任务不为空!
				for (int i = 0; i < commitTaskVOs.length; i++) {
					//在处理池中生成待处理任务
					String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_dealpool"); //
					String str_sql_insert_dealpool = getInsertDealPoolSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_processid, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], commitTaskVOs[i], "N", null, null, str_dealpoolid, null, li_newBatchNo, str_currtime, _secondCallVO.getPrioritylevel(), _secondCallVO
							.getDealtimelimit(), null, false, null, null, null, null); //
					al_sqls.add(str_sql_insert_dealpool); //

					//生成首页需要的待处理任务.
					String str_msg = getDealTaskMsg(_secondCallVO.getProcessid(), commitTaskVOs[i].getFromActivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_newDealPoolId, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //生成消息提示,是可配置的!
					String str_sql_insert_dealtask = getInsertTaskDealSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_dealpoolid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, _billVO.toString(), str_message, str_msg, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1],
							str_currtime, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserName(), commitTaskVOs[i].getParticipantUserDeptId(), commitTaskVOs[i].getParticipantUserDeptName(), commitTaskVOs[i].getAccrUserId(), _secondCallVO.getPrioritylevel(), _secondCallVO.getDealtimelimit(), commitTaskVOs[i].isCCTo());
					al_sqls.add(str_sql_insert_dealtask); //
				}
			}
		} else { //如果不是终结者
		}

		getCommDMO().executeBatchByDS(null, al_sqls); //执行批处理SQL

		//发邮件,sfj后来修改....
		sendMail(_secondCallVO, _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message); //

		//执行后置拦截器!!
		//		if (intercept2 != null) {
		//			intercept2.afterAction(_secondCallVO, _loginuserid, _billVO, "REJECT"); //
		//		}
		intercept2AfterAction(_secondCallVO, _loginuserid, _billVO, "REJECT");

		return _billVO; //
	}

	/**
	 * 取得一个环节上曾经走过的人!!
	 * @param _activityId
	 * @return
	 */
	private WorkFlowParticipantBean getOneActivityHistDealUserVOs(String _wfinstanceid, String _activityId) throws Exception { //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		String str_sql_oneactivity = "select distinct participant_user from pub_wf_dealpool where prinstanceid='" + getTBUtil().getNullCondition(_wfinstanceid) + "' and curractivity='" + getTBUtil().getNullCondition(_activityId) + "' and isprocess='Y'";
		String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, str_sql_oneactivity); //找出本流程实例中该环节上曾走过的所有人
		if (str_userids != null && str_userids.length > 0) {
			String str_sql = "select id userid,code usercode,name username from pub_user where id in (" + getTBUtil().getInCondition(str_userids) + ")"; //找出这些人员
			WorkFlowParticipantUserBean[] userBeans = getParticipanUserBeansBySQL(str_sql, false, "虚线直接计算", "因为虚线直接计算曾经走过的人!"); //根据这些人员计算出流程参与者对象!!!
			parBean.setParticipantUserBeans(userBeans); //曾经走过的!!!
			parBean.setParticiptMsg("根据SQL[" + str_sql_oneactivity + "]共找到" + userBeans.length + "个人!"); //
		} else {
			parBean.setParticiptMsg("根据SQL[" + str_sql_oneactivity + "]没有找到一个人!"); //
		}
		return parBean; //
	}

	//取得授权模块!!
	private String getAccrModel(BillVO _billVO) throws Exception {
		//处理授权问题!!!!!!!! 就是遍历所有找到的人,然后从授权的清单中寻找到这个人的授权信息,然后补录上去!!!
		//String str_processId = _hvs_dealrecord.getStringValue("rootinstanceid_processid"); //流程id
		String str_billtype = _billVO.getStringValue("billtype"); //单据类型
		String str_busitype = _billVO.getStringValue("busitype"); //业务类型
		String str_accrmodel = null; //
		if (str_billtype != null && !str_billtype.trim().equals("") && str_busitype != null && !str_busitype.trim().equals("")) { //如果三者都不为空!!则去找到本人的授权模块
			String[][] str_accrModels = getCommDMO().getStringArrayByDS(null, "select accrmodel from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"); //审批模式!!!
			if (str_accrModels.length == 0) { //
				throw new WLTAppException("做授权模块计算时发现,根据单据类型[" + str_billtype + "],业务类型[" + str_busitype + "]在流程分配表中没有找到一条分配记录,这是不对的,请与管理员联系!"); //
			} else if (str_accrModels.length > 1) {
				throw new WLTAppException("做授权模块计算时发现,根据单据类型[" + str_billtype + "],业务类型[" + str_busitype + "]在流程分配表中找到两条以上的配置信息!\r\n这将会导致得到随机的授权模块,是不允许的,请与管理员联系!!"); //
			}
			str_accrmodel = str_accrModels[0][0]; //赋值
		}
		return str_accrmodel; //授权模块!!!
	}

	/**
	 * 业务二次过滤!!!
	 * @return
	 */
	private void busiDynSecondFilter(String _wfinstanceid, String _billtype, String _busitype, BillVO _billVO, DealActivityVO[] _dealActivitys) {
		HashVO[] hvs_defines = isDefineFilter2(_wfinstanceid, _billtype, _busitype); //取得定义有二次过滤条件
		if (hvs_defines != null && hvs_defines.length > 0) { //如果定义了二次过滤...
			if (_dealActivitys == null || _dealActivitys.length <= 0) {
				return;
			}

			for (int i = 0; i < _dealActivitys.length; i++) {
				DealTaskVO[] taskVOs = _dealActivitys[i].getDealTaskVOs(); //取得待处理任务
				if (taskVOs != null && taskVOs.length > 0) {
					ArrayList al_taskvo = new ArrayList(); //
					for (int j = 0; j < taskVOs.length; j++) {
						String str_userid = taskVOs[j].getParticipantUserId(); //参与者的Id
						String str_userroleid = taskVOs[j].getParticipantUserRoleId(); //参与者的角色Id
						String str_activityid = taskVOs[j].getCurrActivityId(); //
						if (isDefineFilter2Item(hvs_defines, _billVO, str_activityid, str_userid, str_userroleid)) { //处理每一条
							al_taskvo.add(taskVOs[j]); //
						}
					}
					_dealActivitys[i].setDealTaskVOs((DealTaskVO[]) al_taskvo.toArray(new DealTaskVO[0])); //重新设置一下过滤后的任务!!!
				}

			}
		}
	}

	/**
	 * 查询是否定义业务二次动态过滤!!!
	 * @param _wfinstanceid
	 * @param _billtype
	 * @param _busitype
	 * @return
	 */
	private HashVO[] isDefineFilter2(String _wfinstanceid, String _billtype, String _busitype) {
		try {
			CommDMO commDMO = new CommDMO();
			String processid = commDMO.getStringValueByDS(null, "select processid from pub_wf_prinstance where id='" + _wfinstanceid + "'");
			String workflowassign_id = commDMO.getStringValueByDS(null, "select id from  pub_workflowassign where billtypecode='" + _billtype + "' and busitypecode='" + _busitype + "' and processid='" + processid + "'");
			HashVO[] hashvo = commDMO.getHashVoArrayByDS(null, "select * from  pub_workflowassign_dynfilter2 where assignid='" + getTBUtil().getNullCondition(workflowassign_id) + "'");
			return hashvo;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/***
	 * 是否定义了某一项的二次过滤条件
	 * @param _al_filter2Define
	 * @return
	 */
	private boolean isDefineFilter2Item(HashVO[] _hvs_filter2Define, BillVO _billVO, String _activityid, String _userid, String _roleid) {
		for (int i = 0; i < _hvs_filter2Define.length; i++) { //
			if (_hvs_filter2Define[i].getStringValue("activityid").equals(_activityid)) { //如果是该环节的定义
				String str_itemkey = _hvs_filter2Define[i].getStringValue("itemkey");
				String str_defineCondition = _hvs_filter2Define[i].getStringValue("itemvalue"); //定义的值与条件
				String str_billValue = _billVO.getStringValue(str_itemkey); //
				if (meetCondition(str_billValue, str_defineCondition)) { //如果单据中的该itemkey的值正好等于定义的itemvalue,则再比较人员
					String str_defineuserids = _hvs_filter2Define[i].getStringValue("userids"); //
					String str_defineuserroleids = _hvs_filter2Define[i].getStringValue("roleids"); //
					if ((str_defineuserids != null && str_defineuserids.indexOf(_userid + ";") >= 0) || (str_defineuserroleids != null && str_defineuserroleids.indexOf(_roleid + ";") >= 0)) {
						return true; //如果角色定义或人员定义中包括流程引擎中传过来的人员或角色,则对该人员放过去,即通过!!
					} else {
						return false; //如果定义了人与角色,但不包括流程引擎传过来的人,则拒绝该人通过
					}
				}

			}
		}

		return true; //
	}

	/**
	 * 将页面上的值与定义的条件进行比较,如果品配上了则返回True
	 * @param _billVaue
	 * @param _condition
	 * @return
	 */
	private boolean meetCondition(String _billVaue, String _condition) {
		if (_billVaue == null || _condition == null) { //如果页面上的值为空,则不干!
			return false;
		}
		_condition = _condition.trim(); //

		String str_sql = "";
		String str_sql_item1 = "";
		if (isDecimal(_billVaue)) {
			str_sql_item1 = _billVaue;
		} else {
			str_sql_item1 = "'" + _billVaue + "'"; //
		}
		if (_condition.toLowerCase().startsWith(">") || _condition.toLowerCase().startsWith("<") || _condition.toLowerCase().startsWith("=") || _condition.toLowerCase().startsWith("between")) {
			str_sql = "select 1 from wltdual where " + str_sql_item1 + _condition; //直接拼,如 100>200
		} else {
			str_sql = "select 1 from wltdual where " + str_sql_item1 + "='" + _condition + "'"; //
		}

		try {
			String[][] str_data = getCommDMO().getStringArrayByDS(null, str_sql); //执行SQL
			if (str_data != null && str_data.length > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断值是否是数字
	 * @param _value
	 * @return
	 */
	private boolean isDecimal(String _value) {
		try {
			new Double(_value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private String toEmpty(String _str) {
		if (_str == null) {
			return "";
		}
		return _str;
	}

	//几个重要VO: NextTransitionVO 连线的VO DealActivity 待办环节VO  DealTaskVO:一个环节中包含的待办任务VO  
	private DealTaskVO getDealTaskVOByCompute(DealActivityVO _dealActVO, WorkFlowParticipantUserBean _wfParUsers) {
		DealTaskVO taskVO = new DealTaskVO();
		taskVO.setFromActivityId(_dealActVO.getFromActivityId()); //
		taskVO.setFromActivityName(_dealActVO.getFromActivityName()); //

		taskVO.setTransitionId(_dealActVO.getFromTransitionId()); //
		taskVO.setTransitionCode(_dealActVO.getFromTransitionCode()); //连线编码...
		taskVO.setTransitionName(_dealActVO.getFromTransitionName()); //
		taskVO.setTransitionIntercept(_dealActVO.getFromTransitionIntercept()); //连线的拦截器..
		taskVO.setTransitionDealtype(_dealActVO.getFromtransitiontype()); //处理类型
		taskVO.setTransitionMailSubject(_dealActVO.getFromTransitionMailsubject()); //连线上定义的邮件主题...
		taskVO.setTransitionMailContent(_dealActVO.getFromTransitionMailcontent()); //连线上定义的邮件内容..

		taskVO.setCurrActivityId(_dealActVO.getActivityId()); //
		taskVO.setCurrActivityType(_dealActVO.getActivityType()); //环节类型
		taskVO.setCurrActivityCode(_dealActVO.getActivityCode()); //
		taskVO.setCurrActivityName(_dealActVO.getActivityName()); //

		taskVO.setParticipantUserId(_wfParUsers.getUserid()); //参与者ID
		taskVO.setParticipantUserCode(_wfParUsers.getUsercode()); //参与者编码
		taskVO.setParticipantUserName(_wfParUsers.getUsername()); //参与者名称

		taskVO.setParticipantUserDeptId(_wfParUsers.getUserdeptid()); //参与者机构主键
		taskVO.setParticipantUserDeptCode(_wfParUsers.getUserdeptcode()); //参与者机构编码
		taskVO.setParticipantUserDeptName(_wfParUsers.getUserdeptname()); //参与者机构名称

		taskVO.setParticipantUserRoleId(_wfParUsers.getUserroleid()); //参与者角色主键
		taskVO.setParticipantUserRoleCode(_wfParUsers.getUserrolecode()); //参与者角色编码
		taskVO.setParticipantUserRoleName(_wfParUsers.getUserrolename()); //参与者角色名称

		taskVO.setAccrUserId(_wfParUsers.getAccrUserid()); //授权人ID.
		taskVO.setAccrUserCode(_wfParUsers.getAccrUsercode()); //授权人编码.
		taskVO.setAccrUserName(_wfParUsers.getAccrUsername()); //授权人名称.

		taskVO.setCCTo(_wfParUsers.isCCTo()); //是否是抄送模式!!!
		taskVO.setSuccessParticipantReason(_wfParUsers.getSuccessParticipantReason()); //
		return taskVO; //
	}

	/**
	 * 执行拦截器2的前置操作
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void intercept2BeforeAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		String str_interceptClsName = _secondCallVO.getIntercept2(); //拦截器名称!!
		if (str_interceptClsName != null && str_interceptClsName.trim().indexOf(".") > 0) { //
			Object obj = Class.forName(str_interceptClsName).newInstance(); //
			if (obj instanceof WFIntercept2IFC) { //以前的BS端拦截器是继承于这个接口,为了兼容以前的,所以不能放弃以前的机制!!
				WFIntercept2IFC intercept = (WFIntercept2IFC) obj; //创建拦截器2
				intercept.beforeAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //
			} else if (obj instanceof WorkFlowEngineBSIntercept) { //新的机制是使用了统一的流程引擎BS端拦截器
				String str_billtype = _billVO.getStringValue("billtype"); //
				String str_busitype = _billVO.getStringValue("busitype"); //
				WorkFlowEngineBSIntercept intercept = (WorkFlowEngineBSIntercept) obj; //创建拦截器
				WLTHashMap parMap = new WLTHashMap(); //为了以后扩展而不影响代码,都搞一个Map用来存放其他可能新扩展的参数!!
				intercept.beforeActivityAction(str_billtype, str_busitype, _secondCallVO, _loginuserid, _billVO, _dealtype, parMap); //
			} else { //是抛异常还是不做任何逻辑处理???
				throw new WLTAppException("环节上注册的BS端拦截器类型不对,即不是实现接口[WFIntercept2IFC],也不是继承于抽象类[WorkFlowEngineBSIntercept]"); //
			}
		}
	}

	/**
	 * 执行拦截器2的后置操作
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void intercept2AfterAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		try {
			String str_interceptClsName = _secondCallVO.getIntercept2(); //拦截器名称!!
			if (str_interceptClsName != null && str_interceptClsName.trim().indexOf(".") > 0) { //
				Object obj = Class.forName(str_interceptClsName).newInstance(); //
				if (obj instanceof WFIntercept2IFC) { //以前的BS端拦截器是继承于这个接口,为了兼容以前的,所以不能放弃以前的机制!!
					WFIntercept2IFC intercept = (WFIntercept2IFC) obj; //创建拦截器2
					intercept.afterAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //
				} else if (obj instanceof WorkFlowEngineBSIntercept) { //新的机制是使用了统一的流程引擎BS端拦截器
					String str_billtype = _billVO.getStringValue("billtype"); //
					String str_busitype = _billVO.getStringValue("busitype"); //
					WorkFlowEngineBSIntercept intercept = (WorkFlowEngineBSIntercept) obj; //创建拦截器
					WLTHashMap parMap = new WLTHashMap(); //为了以后扩展而不影响代码,都搞一个Map用来存放其他可能新扩展的参数!!

					//parMap
					intercept.afterActivityAction(str_billtype, str_busitype, _secondCallVO, _loginuserid, _billVO, _dealtype, parMap); //
				} else { //是抛异常还是不做任何逻辑处理???
					throw new WLTAppException("环节上注册的BS端拦截器类型不对,即不是实现接口[WFIntercept2IFC],也不是继承于抽象类[WorkFlowEngineBSIntercept]"); //
				}
			}
		} catch (Throwable ex) {
			System.out.println("为了不影响测试人员使用,先把异常吃掉!!!"); //
			ex.printStackTrace(); //
		}
	}

	//计算出新的历史处理人清单!!
	private String[] getNewSubmiterHist(String _prinstanceId, String _loginUserId, String _currtime, String _dealMsg) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select submiterhist,mylastsubmittime,mylastsubmitmsg from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		String str_newsubmiterhist = hvs[0].getStringValue("submiterhist"); //取得历史执行过的人!!!
		if (str_newsubmiterhist == null || str_newsubmiterhist.trim().equals("")) {
			str_newsubmiterhist = ";" + _loginUserId + ";"; //如果为空,则第一个加;号
		} else {
			str_newsubmiterhist = str_newsubmiterhist + _loginUserId + ";"; //
		}

		//我的最后处理时间,用于在单据中查看!!
		String str_mylastsubmittime = getNewMyLastInfo(hvs[0].getStringValue("mylastsubmittime"), _loginUserId, _currtime); //

		//我的最后处理意见,用于在单据中查看!!
		String str_dealMsg = (_dealMsg == null ? "" : _dealMsg);
		str_dealMsg = getTBUtil().replaceAll(str_dealMsg, ";", ","); //为了防止意见中就有分号将分号统统强制换成逗号
		if (str_dealMsg.length() > 20) { //如果超过20个字,则做截取处理,这是为了防止太多的意见进行累加会造成存不下的异常!!!
			str_dealMsg = str_dealMsg.substring(0, 20) + "..."; //
		}
		String str_mylastsubmitmsg = getNewMyLastInfo(hvs[0].getStringValue("mylastsubmitmsg"), _loginUserId, str_dealMsg); //

		return new String[] { str_newsubmiterhist, str_mylastsubmittime, str_mylastsubmitmsg }; //
	}

	//找出并替换我的新的内容,抑或直接做添加处理! 即搞成【123=我的意见;456=他的意见;】的哈希表字符串的样子
	private String getNewMyLastInfo(String _oldText, String _loginUserId, String _newReplacedText) {
		String str_returnText = null; //
		if (_oldText == null || _oldText.trim().equals("")) { //如果为空
			str_returnText = ";" + _loginUserId + "=" + _newReplacedText + ";"; //
		} else {
			int li_pos = _oldText.indexOf(";" + _loginUserId + "="); //
			if (li_pos >= 0) { //如果曾经有了,则做replace处理!
				String str_prefix = _oldText.substring(0, li_pos + 1 + _loginUserId.length() + 1); ////直到左括号
				String str_subfix_1 = _oldText.substring(li_pos + 1 + _loginUserId.length() + 1, _oldText.length()); //
				String str_subfix_2 = str_subfix_1.substring(str_subfix_1.indexOf(";"), str_subfix_1.length()); //就本人历史意见的右括号后面的信息!
				str_returnText = str_prefix + _newReplacedText + str_subfix_2; //将()中的原来的时间替换成新的时间!
			} else { //否则做添加处理!
				str_returnText = _oldText + _loginUserId + "=" + _newReplacedText + ";"; //
			}
		}
		return str_returnText; //
	}

	//计算出某流程实例上新的批号! 现在这种机制有问题,即在并发时可能出现重号!! 所以需要使用内存锁的技术! 即一个人取得后另一个再取时将会处理等待或取下一个号!!!
	private int getNewBatchNo(String _prinstanceId) throws Exception {
		HashVO[] hvs_batchNo = getCommDMO().getHashVoArrayByDS(null, "select currbatchno c1 from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		int li_batchNo = 1;
		if (hvs_batchNo[0].getIntegerValue("c1") != null) {
			li_batchNo = hvs_batchNo[0].getIntegerValue("c1").intValue() + 1; // 批号加1!!!没有使用SQL语法+1,是为了跨数据库平台!!!
		}
		return li_batchNo; //
	}

	//取得处理的类型
	private String getApproveResult(String _dealtype) {
		if (_dealtype == null || _dealtype.trim().equals("")) { //如果类型,即提交与退回是两种类型意愿的提交!!!
			return "Y";
		} else if (_dealtype.toUpperCase().equals("SUBMIT")) { //如果是提交
			return "Y";
		} else if (_dealtype.toUpperCase().equals("REJECT")) { //如果是拒绝
			return "N";
		} else {
			return "Y";
		}
	}

	//提交的意见分类,比如因何原因而退回,以后的管理统计需要!
	private String getSubmitReasonCode(WFParVO _secondCallVO) {
		if (_secondCallVO != null && _secondCallVO.getSelectedReasonCode() >= 0 && _secondCallVO.getReasonCodeComBoxItemVOs() != null) {
			return _secondCallVO.getReasonCodeComBoxItemVOs()[_secondCallVO.getSelectedReasonCode()].getId(); //
		} else {
			return null;
		}
	}

	//根据人员id取得编码与名称!!
	private String getUserCodeNameById(String _userId) throws Exception {
		String[][] str_userCodeName = getCommDMO().getStringArrayByDS(null, "select code,name from pub_user where id='" + _userId + "'"); //
		if (str_userCodeName != null && str_userCodeName.length > 0) {
			return str_userCodeName[0][0] + "/" + str_userCodeName[0][1]; //用户名称,考虑到可能会重名,所以使用工号/名称的样式
		} else {
			return "null";
		}
	}

	private String[] getUserDeptIdName(String _userId) throws Exception {
		String str_sql = "select t1.userid,t1.userdept,t2.name deptname,t1.isdefault from pub_user_post t1 left join pub_corp_dept t2 on t1.userdept=t2.id where t1.userid='" + _userId + "' order by t1.id"; //
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, str_sql); //
		if (hvs == null || hvs.length == 0) {
			return new String[] { null, null };
		}

		String str_deptId = null;
		String str_deptName = null; //
		boolean isFindDefault = false; //
		for (int i = 0; i < hvs.length; i++) {
			if ("Y".equalsIgnoreCase(hvs[i].getStringValue("isdefault"))) { //如果有个默认部门
				str_deptId = hvs[i].getStringValue("userdept"); //
				str_deptName = hvs[i].getStringValue("deptname"); //
				isFindDefault = true;
				break; //
			}
		}

		if (!isFindDefault) { //如果没发现默认部门,则直接返回第一个!即将第一个当前是默认部门!!!
			str_deptId = hvs[0].getStringValue("userdept"); //
			str_deptName = hvs[0].getStringValue("deptname"); //
		}

		if (str_deptId != null) {
			System.out.println("_userId=========================" + _userId + "    str_deptId" + str_deptId);//加一行输出，如果以后报错的话就知道哪个人的机构报错了【袁江晓/2017-11-27】
			HashVO[] parentPathHVS = getCommDMO().getTreePathVOsByOneRecord(null, "pub_corp_dept", "id", "parentid", "id", str_deptId); //
			if (parentPathHVS.length > 2) {
				String str_pathNames = ""; //
				for (int i = 1; i < parentPathHVS.length; i++) {
					str_pathNames = str_pathNames + parentPathHVS[i].getStringValue("name"); //
					if (i != parentPathHVS.length - 1) {
						str_pathNames = str_pathNames + "-";
					}
				}
				str_deptName = str_pathNames; //
			}
		}

		return new String[] { str_deptId, str_deptName };

	}

	//将接收者的信息拼接在一起,然后生成SubmitTo的信息,即提交给了谁!!!
	private String[] getSubmitToInfo(DealTaskVO[] commitTaskVOs) {
		String str_submittouser = null, str_submittousername = null, str_submittocorp = null, str_submittocorpName = null, submittotransition = null, submittoactivity = null, submittoactivityname = null; //
		if (commitTaskVOs != null && commitTaskVOs.length > 0) { //如果待处理任务不为空!
			for (int i = 0; i < commitTaskVOs.length; i++) {
				if (str_submittouser == null) {
					str_submittouser = ";";
				}
				if (str_submittousername == null) {
					str_submittousername = ";";
				}
				if (str_submittocorp == null) {
					str_submittocorp = ";";
				}
				if (str_submittocorpName == null) {
					str_submittocorpName = ";";
				}
				str_submittouser = str_submittouser + commitTaskVOs[i].getParticipantUserId() + ";"; //将所有接收者拼在一起!!
				str_submittousername = str_submittousername + commitTaskVOs[i].getParticipantUserName() + ";"; //接收者的名称
				if (commitTaskVOs[i].getParticipantUserDeptId() != null && str_submittocorp.indexOf(";" + commitTaskVOs[i].getParticipantUserDeptId() + ";") < 0) { //如果有机构,则没有出现过,则加入
					str_submittocorp = str_submittocorp + commitTaskVOs[i].getParticipantUserDeptId() + ";"; //
				}
				if (commitTaskVOs[i].getParticipantUserDeptName() != null && str_submittocorpName.indexOf(";" + commitTaskVOs[i].getParticipantUserDeptName() + ";") < 0) { //如果有机构,则没有出现过,则加入
					str_submittocorpName = str_submittocorpName + commitTaskVOs[i].getParticipantUserDeptName() + ";"; //
				}
				submittotransition = commitTaskVOs[i].getTransitionId(); //提交的连线!以后存在分兵多路的情况,可能不需要该字段,或者存的是多值
				submittoactivity = commitTaskVOs[i].getCurrActivityId(); //提交的环节!!
				submittoactivityname = commitTaskVOs[i].getCurrActivityName(); //提交的环节名称
			}
		}
		return new String[] { str_submittouser, str_submittousername, str_submittocorp, str_submittocorpName, submittotransition, submittoactivity, submittoactivityname }; //
	}

	//执行连线上的拦截器
	private void execTransitionIntercept(WFParVO _secondCallVO, String str_prinstanceid, String str_dealpoolid, BillVO _billVO) throws Exception {
		DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //提交的对象!!
		JepFormulaParseAtWorkFlow jepFormula = null; //
		if (commitTaskVOs != null) {
			for (int i = 0; i < commitTaskVOs.length; i++) {
				String transiintercept = commitTaskVOs[i].getTransitionIntercept(); //目标处理任务的连线!!!
				if (transiintercept != null && !transiintercept.trim().equals("")) { //如果连线的拦截器不为空
					if (jepFormula == null) {
						jepFormula = new JepFormulaParseAtWorkFlow(_secondCallVO, str_prinstanceid, str_dealpoolid, _billVO); //将流程处理任务与实例主键，处理任务主键都传给公式引擎
					}
					String[] str_items = getTransFormulas(transiintercept); //分割!!!
					for (int j = 0; j < str_items.length; j++) { //
						if (str_items[j].startsWith("=>")) { //如果是某个类!!!则直接反射调用！！！
							String str_clsName = str_items[j].substring(2, str_items[j].length()); //类名！
							System.out.println("工作流引擎将执行连线上的拦截器类【" + str_clsName + "】,这个类应该继承于接口[WorkFlowTransitionExecIfc]才能成功执行..."); //
							WorkFlowTransitionExecIfc ifc = (WorkFlowTransitionExecIfc) (Class.forName(str_clsName).newInstance()); //
							ifc.afterExecTransition(_secondCallVO, str_prinstanceid, str_dealpoolid, _billVO); //
						} else {
							System.out.println("工作流引擎将执行连线上的公式拦截器【" + str_items[j] + "】..."); //
							Object obj = jepFormula.execFormula(str_items[j]); //
							if (obj instanceof WLTAppException) {
								throw (WLTAppException) obj; //
							}
						}
					}
				}
			}
		}
	}

	//对连线上的公式进行过滤处理！！
	private String[] getTransFormulas(String _formula) {
		String[] str_items = null; //
		_formula = _formula.trim(); ////！！！
		_formula = getTBUtil().replaceAll(_formula, "\r", ""); //
		_formula = getTBUtil().replaceAll(_formula, "\n", ""); //
		if (_formula.indexOf("；") > 0) {
			str_items = getTBUtil().split(_formula, "；"); //
		} else { //
			str_items = getTBUtil().split(_formula, ";"); //
		}
		for (int i = 0; i < str_items.length; i++) {
			str_items[i] = str_items[i].trim(); //
		}
		return str_items; //
	}

	//发邮件,因为两种提交都用到,所以封装成一个方法!!! 现在的机制是可以直接发邮件! 但这样性能很低! 应该专门有个邮件内容表,然后专门另起一个邮件线程从这个表中读取数据发邮件!
	//后来招行的机制就是往一个表中插数据,然后单独起了个程序发邮件!
	private void sendMail(WFParVO _secondCallVO, BillVO _billVO, String _loginuserid, String str_loginusername, String str_currtime, String str_prinstanceid, String str_message) {
		try {
			DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //提交的对象!!
			Vector v_sendmail = null; //
			if ("Y".equals(_secondCallVO.getIfSendEmail())) { //如果需要发邮件!!!
				String str_mailsubject = getEmailSubJectMsg(_secondCallVO.getProcessid(), _secondCallVO.getCurractivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //邮件主题.
				String str_mailcontent = getEmailContentMsg(_secondCallVO.getProcessid(), _secondCallVO.getCurractivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //邮件內容.
				if (str_mailcontent != null && !str_mailcontent.trim().equals("")) { //如果邮件内容不为空，则要发送邮件
					String str_usermailaddr = ""; //
					for (int i = 0; i < commitTaskVOs.length; i++) {
						str_usermailaddr = str_usermailaddr + getUserMailAddr(commitTaskVOs[i].getParticipantUserId()) + ";";
					}
					if (str_usermailaddr != null && !"".equals(str_usermailaddr)) { //如果邮件地址不为空...
						if (v_sendmail == null) {
							v_sendmail = new Vector();
						}
						v_sendmail.add(new String[] { str_usermailaddr, str_mailsubject, str_mailcontent }); //给某个人,发什么邮件..
					}
				}
			}
			if (v_sendmail != null && v_sendmail.size() > 0) { //如果的确有需要发邮件的
				for (int i = 0; i < v_sendmail.size(); i++) { //遍历发送邮件
					if (!"".equals(getTBUtil().getSysOptionStringValue("自定义邮件发送器", ""))) {
						try {
							Object selfDescSendMail = Class.forName(getTBUtil().getSysOptionStringValue("自定义邮件发送器", "")).newInstance();
							if (selfDescSendMail instanceof AbstractSelfDescSendMail) { //
								_billVO.setUserObject("WFParVO", _secondCallVO); //by haoming 2016-04-18 
								((AbstractSelfDescSendMail) selfDescSendMail).sendMail(_billVO, str_message, _loginuserid, v_sendmail); //发邮件!
							} else {
								throw new Exception(getTBUtil().getSysOptionStringValue("自定义邮件发送器", "") + "没有继承AbstractSelfDescSendMail!!");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else { //默认的发邮件机制!!
						String str_mailserver = getTBUtil().getSysOptionStringValue("邮件配置", ""); //
						if (!"".equals(str_mailserver) && str_mailserver.split(";").length == 3) { //从平台参数表中取得三个参数!
							String str_touser = ((String[]) v_sendmail.get(i))[0]; //接收者
							if (str_touser != null && !"".equals(str_touser) && !"null".equals(str_touser)) {
								String str_mailsubject = ((String[]) v_sendmail.get(i))[1]; //邮件主题.
								String str_mailcontent = ((String[]) v_sendmail.get(i))[2]; //邮件内容.
								str_mailcontent = replaceAllMailItem(str_mailcontent, _billVO); //邮件内容,替换{}中的值 
								System.out.println("发送邮件,SMTP Server[" + str_mailserver.split(";")[0] + "],FromUser[" + _loginuserid + "],ToUser[" + str_touser + "],Subject[" + str_mailsubject + "],Content[" + str_mailcontent + "]"); ////
								new MailUtil().sendMail(str_mailserver.split(";")[0], str_mailserver.split(";")[1], str_mailserver.split(";")[2], str_touser.split(";"), str_mailsubject, str_mailcontent); //发送邮件... 
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //发邮件发生异常,直接吃掉,保证主程序继续走!
		}
	}

	/**
	 * 替换所有{}中的字符..
	 * @param _content
	 * @param _billVO
	 */
	private String replaceAllMailItem(String _content, BillVO _billVO) throws Exception {
		String str_content = _content; //
		String[] str_keys = getTBUtil().getFormulaMacPars(str_content); //
		for (int i = 0; i < str_keys.length; i++) {
			str_content = getTBUtil().replaceAll(str_content, "{" + str_keys[i] + "}", _billVO.getStringValue(str_keys[i])); //
		}
		return str_content; //
	}

	/**
	 * 计算待处理任务消息的提示内容
	 * 即可以配置一段话,然后从表单中取得相应的数据!!
	 * @param _processid
	 * @param _fromActivityName
	 * @param _billVO
	 * @return
	 */
	private String getDealTaskMsg(String _processid, String _fromActivityName, BillVO _billVO, String _loginuserId, String _loginusername, String _dealpoolIdCreateTime, String _dealPoolId, String _str_prinstanceid, String _ideaMsg, String _dealLimit) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select name,userdef01 from pub_wf_process where id='" + _processid + "'"); //
		String str_msg = null; //
		if (hvs == null || hvs.length == 0 || hvs[0].getStringValue("userdef01") == null || hvs[0].getStringValue("userdef01").trim().equals("")) {
			str_msg = "[" + _loginusername + "]在环节[" + _fromActivityName + "]提交给你的[" + _billVO.getTempletName() + "][" + _billVO.getPkValue() + "]需要你处理!"; //
		} else {
			TBUtil tbUtil = new TBUtil();
			String str_msgtemplet = hvs[0].getStringValue("userdef01"); //消息模板
			String[] str_macroItems = tbUtil.getFormulaMacPars(str_msgtemplet, "{", "}"); //
			for (int i = 0; i < str_macroItems.length; i++) { //循环替换表单中的值
				if (str_macroItems[i].equals("提交人")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _loginusername); //提交人
				} else if (str_macroItems[i].equals("提交时间")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealpoolIdCreateTime); //提交人
				} else if (str_macroItems[i].equals("提交环节")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _fromActivityName); //提交环节
				} else if (str_macroItems[i].equals("处理期限")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealLimit);
				} else if (str_macroItems[i].equals("处理意见")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _ideaMsg); //批语
				} else if (str_macroItems[i].equals("流程启动天数")) { //
					try {
						String createtime = commDMO.getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + _str_prinstanceid + "'");
						String currtime = tbUtil.getCurrDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date_createtime = sdf.parse(createtime.substring(0, 10));
						java.util.Date date_currtime = sdf.parse(currtime);

						str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", "" + ((date_currtime.getTime() - date_createtime.getTime()) / (1000 * 3600 * 24))); //提交了几天
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str_macroItems[i].equals("单据名称")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _billVO.getTempletName()); //单据名称
				} else {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", (_billVO.getStringViewValue(str_macroItems[i]) == null ? "" : _billVO.getStringViewValue(str_macroItems[i]))); //单据中的某一项的值
				}
			} //end for
			str_msg = str_msgtemplet; //
		}
		return str_msg;
	}

	/**
	 * 取得邮件主题 使用userdef18
	 * @param _processid
	 * @param _fromActivityName
	 * @param _billVO
	 * @param _loginuserId
	 * @param _loginusername
	 * @param _dealpoolIdCreateTime
	 * @param _str_prinstanceid
	 * @param _ideaMsg
	 * @param _dealLimit
	 * @return
	 * @throws Exception
	 */
	private String getEmailSubJectMsg(String _processid, String _fromActivityName, BillVO _billVO, String _loginuserId, String _loginusername, String _dealpoolIdCreateTime, String _str_prinstanceid, String _ideaMsg, String _dealLimit) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select name,userdef18 from pub_wf_process where id='" + _processid + "'"); //
		String str_msg = null; //
		if (hvs == null || hvs.length == 0 || hvs[0].getStringValue("userdef18") == null || hvs[0].getStringValue("userdef18").trim().equals("")) {
			str_msg = ServerEnvironment.getProperty("PROJECT_NAME_CN") + "新任务提醒!"; //
		} else {
			TBUtil tbUtil = new TBUtil();
			String str_msgtemplet = hvs[0].getStringValue("userdef18"); //邮件主题模板
			String[] str_macroItems = tbUtil.getFormulaMacPars(str_msgtemplet, "{", "}"); //
			for (int i = 0; i < str_macroItems.length; i++) { //循环替换表单中的值
				if (str_macroItems[i].equals("提交人")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _loginusername); //提交人
				} else if (str_macroItems[i].equals("系统名")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", ServerEnvironment.getProperty("PROJECT_SHORTNAME")); //系统名
				} else if (str_macroItems[i].equals("提交时间")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealpoolIdCreateTime); //提交人
				} else if (str_macroItems[i].equals("提交环节")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _fromActivityName); //提交环节
				} else if (str_macroItems[i].equals("处理期限")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealLimit);
				} else if (str_macroItems[i].equals("处理意见")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _ideaMsg); //批语
				} else if (str_macroItems[i].equals("流程启动天数")) { //
					try {
						String createtime = commDMO.getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + _str_prinstanceid + "'");
						String currtime = tbUtil.getCurrDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date_createtime = sdf.parse(createtime.substring(0, 10));
						java.util.Date date_currtime = sdf.parse(currtime);

						str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", "" + ((date_currtime.getTime() - date_createtime.getTime()) / (1000 * 3600 * 24))); //提交了几天
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str_macroItems[i].equals("单据名称")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _billVO.getTempletName()); //单据名称
				} else {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", (_billVO.getStringViewValue(str_macroItems[i]) == null ? "" : _billVO.getStringViewValue(str_macroItems[i]))); //单据中的某一项的值
				}
			} //end for
			str_msg = str_msgtemplet; //
		}
		return str_msg;
	}

	/**
	 * 取得邮件内容 使用userdef19
	 * @param _processid
	 * @param _fromActivityName
	 * @param _billVO
	 * @param _loginuserId
	 * @param _loginusername
	 * @param _dealpoolIdCreateTime
	 * @param _str_prinstanceid
	 * @param _ideaMsg
	 * @param _dealLimit
	 * @return
	 * @throws Exception
	 */
	private String getEmailContentMsg(String _processid, String _fromActivityName, BillVO _billVO, String _loginuserId, String _loginusername, String _dealpoolIdCreateTime, String _str_prinstanceid, String _ideaMsg, String _dealLimit) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select name,userdef19 from pub_wf_process where id='" + _processid + "'"); //
		String str_msg = null; //
		if (hvs == null || hvs.length == 0 || hvs[0].getStringValue("userdef19") == null || hvs[0].getStringValue("userdef19").trim().equals("")) {
			str_msg = "[" + _loginusername + "]在环节[" + _fromActivityName + "]提交给你的[" + _billVO.getTempletName() + "][" + _billVO.getPkValue() + "]需要你处理!"; //
		} else {
			TBUtil tbUtil = new TBUtil();
			String str_msgtemplet = hvs[0].getStringValue("userdef19"); //邮件内容模板
			String[] str_macroItems = tbUtil.getFormulaMacPars(str_msgtemplet, "{", "}"); //
			for (int i = 0; i < str_macroItems.length; i++) { //循环替换表单中的值
				if (str_macroItems[i].equals("提交人")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _loginusername); //提交人
				} else if (str_macroItems[i].equals("提交时间")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealpoolIdCreateTime); //提交人
				} else if (str_macroItems[i].equals("提交环节")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _fromActivityName); //提交环节
				} else if (str_macroItems[i].equals("处理期限")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealLimit);
				} else if (str_macroItems[i].equals("处理意见")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _ideaMsg); //批语
				} else if (str_macroItems[i].equals("流程启动天数")) { //
					try {
						String createtime = commDMO.getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + _str_prinstanceid + "'");
						String currtime = tbUtil.getCurrDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date_createtime = sdf.parse(createtime.substring(0, 10));
						java.util.Date date_currtime = sdf.parse(currtime);

						str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", "" + ((date_currtime.getTime() - date_createtime.getTime()) / (1000 * 3600 * 24))); //提交了几天
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str_macroItems[i].equals("单据名称")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _billVO.getTempletName()); //单据名称
				} else {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", (_billVO.getStringViewValue(str_macroItems[i]) == null ? "" : _billVO.getStringViewValue(str_macroItems[i]))); //单据中的某一项的值
				}
			} //end for
			str_msg = str_msgtemplet; //
		}
		return str_msg;
	}

	/**
	 * 接收一个任务..
	 * @throws Exception
	 */
	public void receiveDealTask(String _prinstanceid, String _loginuserid) throws Exception {
		String str_parentsql = "select id,isreceive from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and participant_user='" + _loginuserid + "' and issubmit='N' and isprocess='N'"; // 找出属于我的未处理的流程
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, str_parentsql); //查看有没有待办任务!!!
		if (hvs.length == 0) {
			throw new WLTAppException("当前流程没有你的任务!"); //
		}

		String str_dealpoolid = hvs[0].getStringValue("id"); //
		String str_isreceive = hvs[0].getStringValue("isreceive"); //
		if (str_isreceive != null && str_isreceive.equals("Y")) {
			if (new TBUtil().getSysOptionBooleanValue("工作流处理按钮的面板中是否显示接收按钮", true)) {
				throw new WLTAppException("该单据已接收过了!"); //
			} else {
				return;
			}
		}

		String str_time = new TBUtil().getCurrTime(); //
		getCommDMO().executeUpdateByDS(null, "update pub_wf_dealpool set isreceive='Y',receivetime='" + str_time + "' where id='" + str_dealpoolid + "'"); //修改接收标记
	}

	/**
	 * 撤回操作,即将我提交过的流程数据再取回来!主要用于误操作时进行的!!
	 * 以前的机制有个bug,就是撤回后,首页的待处理任务没有了!!
	 * 但新的机制将解决了这个问题!!
	 * @param _prinstanceid
	 * @param _loginuserid
	 */
	public void cancelTask(String _prinstanceid, String _dealPoolId, String _taskOff_id, String _loginuserid, String[] _dirCancelChildIds) throws Exception {
		//必须找到任务id,以前根据实例id寻找的方式都有问题!!! 当初为什么总是根据实例而不是任务id呢? 这是一个非常大的失误!!! 
		//String str_prdealpoolid = "";  //
		//HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select count(*) c1 from pub_wf_dealpool where createbyid='" + str_prdealpoolid + "'");  //
		//boolean isCanCancel = false;  //是否允许做撤回操作
		//逻辑:
		//1.首先判断本条任务的isprocess,如果是N,说明是会签,即肯定可以撤回,而且撤回逻辑很简单,只需将isubmit,submittime反置为空即可!
		//2.如果isprocess为Y了,并且isprocesser='N',则提醒不能做撤消操作了,因为已被其他人处理了,如果isprocesser='Y',即是终结者,则继续判断是否创建了下游
		//3.如果没有创建下游,说明是结束环节,则可以退回,只要将本条流程任务恢复过来就行!
		//4.如果创建了下游,并且下游有任务的isreceive=Y,则提醒不能做撤回了,否则可以撤回,即删除所有下游任务,恢复本条任务!

		if (_dirCancelChildIds != null && _dirCancelChildIds.length > 0) { //管理身份进入直接右键删除一批的!
			String str_delChild_inCons = getTBUtil().getInCondition(_dirCancelChildIds); //
			String str_sql_delchild_1 = "delete from pub_wf_dealpool where id in (" + str_delChild_inCons + ")"; //
			String str_sql_delchild_2 = "delete from pub_task_deal   where createbydealpoolid in (" + str_delChild_inCons + ")"; //
			String str_sql_delchild_3 = "delete from pub_task_off    where createbydealpoolid in (" + str_delChild_inCons + ")"; //
			getCommDMO().executeBatchByDS(null, new String[] { str_sql_delchild_1, str_sql_delchild_2, str_sql_delchild_3 }); //先删除,因为下面需要根据这个进行处理
		}

		//以上恢复时时,都同时将已办任务反写到待办任务中去!
		String str_dealPoolId = _dealPoolId; //
		if (str_dealPoolId == null || str_dealPoolId.trim().equals("")) { //如果没有任务id,则可能是旧的机制,要计算一下!
			String str_submithist = getCommDMO().getStringValueByDS(null, "select submiterhist from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
			if (str_submithist != null && !str_submithist.endsWith(";" + _loginuserid + ";")) { //如果最后的提交者不是我!
				throw new WLTAppException("任务已被下游接收者处理了,不能进行撤回处理了!"); //
			}
			str_dealPoolId = getCommDMO().getStringValueByDS(null, "select max(id) from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and participant_user='" + _loginuserid + "' and issubmit='Y'"); //找出我处理的最大的记录!!即我最新的已办任务!
		}

		if (str_dealPoolId == null || str_dealPoolId.trim().equals("")) { //
			throw new WLTAppException("该任务已经被退回了,或者不是由你刚刚提交的!"); //
		}

		if (_dirCancelChildIds == null && _taskOff_id != null) {
			String str_submitorconfirm = getCommDMO().getStringValueByDS(null, "select submitorconfirm from pub_task_off where id='" + _taskOff_id + "'"); //
			if ("confirm".equalsIgnoreCase(str_submitorconfirm)) {
				//抄送任务撤回提示修改 【杨科/2013-04-23】
				String str_confirmreason = getCommDMO().getStringValueByDS(null, "select confirmreason from pub_task_off where id='" + _taskOff_id + "'");
				if (str_confirmreason.contains("您是该任务的被抄送者")) {
					throw new WLTAppException("您是该任务的被抄送者,被抄送人不能进行撤回处理!");
				} else {
					throw new WLTAppException("该任务是被确认删除的无效任务,即使撤回也不能处理,所以没有意义做撤回操作!"); //	
				}
			}
		}

		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id='" + str_dealPoolId + "'"); //
		if (hvs == null || hvs.length == 0) {
			throw new WLTAppException("根据id[" + str_dealPoolId + "]没有在pub_wf_dealpool表中找到对应的记录,可能是数据错了,请与开发商联系!"); //
		}
		String str_prinstanceId = hvs[0].getStringValue("prinstanceid"); //流程实例id
		String str_rootInstanceId = hvs[0].getStringValue("rootinstanceid"); //根流程!
		String str_batchNo = hvs[0].getStringValue("batchno"); //批号
		String str_curractivity = hvs[0].getStringValue("curractivity"); //当前环节!如果是我创建了下游任务,则撤回时需要将流程实例的当前环节改成这个值!
		//以前只判断了主流程，子流程结束后可以撤回，但无法再提交，故需要设置子流程结束后子流程就不允许撤回了吧【李春娟/2016-12-21】
		String str_status = getCommDMO().getStringValueByDS(null, "select status from pub_wf_prinstance where id='" + str_prinstanceId + "'"); //可能没有根流程!
		if ("END".equalsIgnoreCase(str_status)) {
			throw new WLTAppException((str_rootInstanceId == null || str_rootInstanceId.equals(str_prinstanceId) ? "" : "子") + "流程已结束,不能进行撤回处理了!"); //
		}

		ArrayList al_sqls = new ArrayList(); //
		//submitmessage=null,submitmessagefile=null,
		String str_sql_1 = "update pub_wf_dealpool set isreceive='N',receivetime=null,issubmit='N',submittime=null,submitisapprove=null,submittobatchno=null,submittotransition=null,submittoactivity=null,submittouser=null,submitreasoncode=null,realsubmiter=null,realsubmitername=null,realsubmitcorp=null,realsubmitcorpname=null,submittousername=null,submittocorp=null,submittocorpname=null,lifecycle=null where id='"
				+ str_dealPoolId + "'"; //
		String str_isProcess = hvs[0].getStringValue("isprocess"); //是否提交了!

		if (str_isProcess == null || "N".equals(str_isProcess)) { //如果没提交,则说明肯定是会签中的,因为如果是抢占或终结者,都不可能跑到已办箱中!所以说肯定可以撤回!!!
			al_sqls.add(str_sql_1); //修改自己的任务
			String str_cancelSQL = getCancelToTaskDealSQL(str_dealPoolId); //
			if (str_cancelSQL != null) { //因为旧的机制可能没有已办任务!!
				al_sqls.add(str_cancelSQL); //将已办任务回写到待办任务中!!
			}
			al_sqls.add("delete from pub_task_off where prdealpoolid='" + str_dealPoolId + "'"); //删除已办任务!!
		} else { //如果已处理...
			String str_isProcesser = hvs[0].getStringValue("isprocesser"); //是否是终结者!
			if ("Y".equals(str_isProcesser)) { //如果是终结者
				HashVO[] hvs_child = getCommDMO().getHashVoArrayByDS(null, "select id,isreceive,receivetime from pub_wf_dealpool where createbyid='" + str_dealPoolId + "'"); //有没有由我创建的儿子
				if (hvs_child == null || hvs_child.length == 0) { //如果没有创建下游,即可能是最后一步(即流程的结束步)!则能撤回!!!
					al_sqls.add("update pub_wf_dealpool set isprocess='N',isprocesser=null where prinstanceid='" + str_prinstanceId + "' and batchno='" + str_batchNo + "'"); //修改这一批数据为
					al_sqls.add(str_sql_1); //
					al_sqls.add("update pub_wf_prinstance set curractivity='" + str_curractivity + "',currowner=';" + _loginuserid + ";',currbatchno='" + str_batchNo + "' where id='" + str_prinstanceId + "'"); //修改流程实例的当前所有者!
					String str_cancelSQL = getCancelToTaskDealSQL(str_dealPoolId); //
					if (str_cancelSQL != null) { //因为旧的机制可能没有已办任务!!
						al_sqls.add(str_cancelSQL); //将已办任务回写到待办任务中!!
					}
					al_sqls.add("delete from pub_task_off where prdealpoolid='" + str_dealPoolId + "'"); //删除已办任务!!
				} else { //如果创建了下游单据!!
					for (int i = 0; i < hvs_child.length; i++) {
						if (getTBUtil().getSysOptionBooleanValue("工作流被接受了是否不能撤回", true)) { //默认是不能撤回的!!! 有的客户说能撤回,有的说不能撤回!!
							if ("Y".equals(hvs_child[i].getStringValue("isreceive"))) { //如果已被人处理了,则提示不能做了!
								throw new WLTAppException("该单据已在[" + hvs_child[i].getStringValue("receivetime") + "]被接收者接收或处理了,不能撤回已被别人接收或处理了的单据!"); ////
							}
						}
					}
					//如果下游还没处理,则能撤回!!
					al_sqls.add("delete from pub_wf_dealpool where createbyid='" + str_dealPoolId + "'"); //先删除创建的下游单据!!!!
					al_sqls.add("delete from pub_task_deal   where createbydealpoolid='" + str_dealPoolId + "'"); //删除由我这条任务创建的所有任务!!
					al_sqls.add("update pub_wf_dealpool set isprocess='N',isprocesser=null where prinstanceid='" + str_prinstanceId + "' and batchno='" + str_batchNo + "'"); //修改这一批数据为
					al_sqls.add(str_sql_1); //修改自己的任务
					al_sqls.add("update pub_wf_prinstance set  curractivity='" + str_curractivity + "',currowner=';" + _loginuserid + ";',currbatchno='" + str_batchNo + "' where id='" + str_prinstanceId + "'"); //修改流程实例的当前所有者!!
					String str_cancelSQL = getCancelToTaskDealSQL(str_dealPoolId); //
					if (str_cancelSQL != null) { //因为旧的机制可能没有已办任务!!
						al_sqls.add(str_cancelSQL); //将已办任务回写到待办任务中!!
					}
					al_sqls.add("delete from pub_task_off where prdealpoolid='" + str_dealPoolId + "'"); //删除已办任务!!
					if (str_rootInstanceId != null && !str_rootInstanceId.trim().equals("")) { //如果该任务是创建子流程的任务,那么撤回时必须同时删除创建的子流程实例!这曾经是个严重的bug,在兴业项目中就因为这个原因,结果导致子流程结束时不创建回到主流程的记录!导致流程进行不下去! 因为计算是否会办子流程是否结束是根据兄弟流程的个数计算的,而这里不删除就会导致认为会办没有结束!
						al_sqls.add("delete from pub_wf_prinstance where rootinstanceid='" + str_rootInstanceId + "' and id not in (select prinstanceid from pub_wf_dealpool where rootinstanceid='" + str_rootInstanceId + "')"); //
					}
				}
			} else { //如果不是终结者
				throw new WLTAppException("该单据已会签结束,但又不是由你结束的,所以你不能进行撤回操作!"); //
			}
		}

		String str_submiterhist = getCommDMO().getStringValueByDS(null, "select submiterhist from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (str_submiterhist.endsWith(";" + _loginuserid + ";")) { //如果结束者是当前人,则要处理!! 本来不想搞这个的,但还是要搞一下!否则这个任务还会出现在已办箱中!
			str_submiterhist = str_submiterhist.substring(0, str_submiterhist.lastIndexOf(";" + _loginuserid + ";") + 1); //提交人历史要去掉当前人!!
			al_sqls.add("update pub_wf_prinstance set submiterhist='" + str_submiterhist + "' where id='" + _prinstanceid + "'"); ////
		}
		getCommDMO().executeBatchByDS(null, al_sqls); //执行所有SQL!!!
	}

	//管理员控制主流程结束撤回 【杨科/2013-05-29】
	public String cancelTask_admin(String _prinstanceid, String _dealPoolId, String _taskOff_id, String _loginuserid, String[] _dirCancelChildIds) throws Exception {

		//流程未结束不能撤回
		boolean isCanRealEnd = judgeWFisRealEnd(_prinstanceid);
		if (!isCanRealEnd) {
			return "未结束流程不能撤回!";
		}

		//主流程未结束不能撤回
		String sql_status = "select status from pub_wf_prinstance where id='" + _prinstanceid + "' and (parentinstanceid is null or parentinstanceid='')";
		String status = getCommDMO().getStringValueByDS(null, sql_status);
		if (status.equals("END")) {
			String str_dealPoolId_max = getCommDMO().getStringValueByDS(null, "select max(id) from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and issubmit='Y' and (isccto is null or isccto='' or isccto='N')");
			if (!str_dealPoolId_max.equals(_dealPoolId)) {
				return "非该流程结束节点不能撤回!";
			}

			String upd_sql = "update pub_wf_prinstance set status='RUN' where id='" + _prinstanceid + "'";
			getCommDMO().executeBatchByDS(null, new String[] { upd_sql });

			cancelTask(_prinstanceid, _dealPoolId, _taskOff_id, _loginuserid, _dirCancelChildIds);
			return "撤回成功!";
		} else {
			return "未结束流程不能撤回!";
		}
	}

	/**
	 * 删除任务
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void deleteTask(String _prinstanceid, String _loginuserid) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (hvs.length > 0) {
			if (!_loginuserid.equals(hvs[0].getStringValue("creater"))) {
				throw new WLTAppException("只有流程创建者才能进行删除操作!"); //
			}
			String str_sql_1 = "delete from " + hvs[0].getStringValue("billtablename") + " where " + hvs[0].getStringValue("billpkname") + "='" + hvs[0].getStringValue("billpkvalue") + "'";
			String str_sql_2 = "delete from pub_wf_prinstance where id='" + _prinstanceid + "'"; //流程实例
			String str_sql_3 = "delete from pub_wf_dealpool   where prinstanceid='" + _prinstanceid + "'"; //处理池
			String str_sql_4 = "delete from pub_task_deal     where prinstanceid='" + _prinstanceid + "'"; //删除首页上的待处理任务
			getCommDMO().executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4 }); //
		}
	}

	/**
	 * 暂停工作流..
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void holdWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		String str_sql_1 = "select currbatchno from pub_wf_prinstance where id='" + _prinstanceid + "'"; //取得流程实例的当前批号
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1); //
		int li_batchno = hvs_1[0].getIntegerValue("currbatchno").intValue(); //

		String str_sql_2 = "update pub_wf_prinstance set status='HOLD' where id='" + _prinstanceid + "'"; //设置当前状态为Hold

		String str_new_id = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_holdhistory"); //
		String str_actiontime = getTBUtil().getCurrTime(); //

		String str_sql_3 = "insert into pub_wf_holdhistory (id,prinstanceid,batchno,actiontype,actiontime,actioner) values (" + str_new_id + ",'" + _prinstanceid + "'," + li_batchno + ",'HOLD','" + str_actiontime + "','" + _loginuserid + "')"; //
		getCommDMO().executeBatchByDS(null, new String[] { str_sql_2, str_sql_3 }); //执行批SQL.

	}

	/**
	 * 结束某一个流程
	 * 以前的逻辑很简单,就是直接将流程置为结束标记了,但后来发现有“多路出发，各自结束”的需求,即“多个环节并行提交”!!!
	 * 所以应该是判断是否还存有isProcess='N'的记录,即还有没有结束的任务?? 如果有,则才认为是真正结束！！
	 * 这样也会兼容原有的方法,因为原来我们是不支持多个环节并行提交的!
	 */
	public String endWorkFlow(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		String _dealpoolid = _wfParVO.getDealpoolid(); //处理任务id
		Vector v_sqls = new Vector(); //
		String str_currtime = getTBUtil().getCurrTime(); //取得当前时间!!
		String str_loginusername = getUserCodeNameById(_loginuserid); //得到人员编码
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginuserid); //得到人员所属机构
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select "); //
		sb_sql.append("t1.prinstanceid,"); //
		sb_sql.append("t1.curractivity,"); //
		sb_sql.append("t1.batchno,"); //
		sb_sql.append("t2.parentinstanceid,"); //
		sb_sql.append("t2.creater,"); //
		sb_sql.append("t2.fromparentactivity,"); //
		sb_sql.append("t2.fromparentdealpoolid,"); //
		sb_sql.append("t2.billtempletcode,"); //
		sb_sql.append("t2.billtablename,"); //
		sb_sql.append("t2.billsavedtablename,"); //
		sb_sql.append("t2.billpkname,"); //
		sb_sql.append("t2.billpkvalue "); //
		sb_sql.append("from pub_wf_dealpool t1,pub_wf_prinstance t2 "); //
		sb_sql.append("where t1.prinstanceid=t2.id "); //
		sb_sql.append("and t1.id='" + getTBUtil().getNullCondition(_dealpoolid) + "'"); //id为空时处理一下【李春娟/2012-05-11】
		HashVO[] hvs_dealpool = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //取得当前实例信息
		String str_parentInstanceId = hvs_dealpool[0].getStringValue("parentinstanceid"); //父亲实例主键!! 可能为空,如果不为空则要进行判断兄弟流程是否继续的处理,如果兄弟流程都结束了,则需要为父亲流程创建新的任务!!
		String str_templetCode = hvs_dealpool[0].getStringValue("billtempletcode"); //单据模板
		String str_tableName = hvs_dealpool[0].getStringValue("billtablename"); //单据模板
		String str_queryTableName = hvs_dealpool[0].getStringValue("billquerytablename"); //保存的表名
		String str_pkName = hvs_dealpool[0].getStringValue("billpkname"); //主键字段名
		String str_pkValue = hvs_dealpool[0].getStringValue("billpkvalue"); //主键值

		String str_curractivity = hvs_dealpool[0].getStringValue("curractivity"); //当前环节!!
		String str_batchno = hvs_dealpool[0].getStringValue("batchno"); //批号!!
		String str_fromParentActivityId = hvs_dealpool[0].getStringValue("fromparentactivity"); //从父流程的哪个环节创建的
		String str_fromParentDealPoolId = hvs_dealpool[0].getStringValue("fromparentdealpoolid"); //从哪个父流程任务过来的!!!

		//清空当前流程所有日志数据,即搬家!!
		String str_sql_insert_taskoff = getInsertTaskOffSQL(_dealpoolid, _loginuserid, str_loginusername, str_currtime, _message, null, null, null, null, true, null); //结束时没有提交给任何人与机构,所以xubmitto的字段都为空!
		if (str_sql_insert_taskoff != null) {
			v_sqls.add(str_sql_insert_taskoff); //
		}
		v_sqls.add("delete from pub_task_deal where prdealpoolid='" + _dealpoolid + "'"); //删除本条任务!!!

		//处理当前流程任务
		UpdateSQLBuilder isql_1 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + _dealpoolid + "'"); //
		isql_1.putFieldValue("isreceive", "Y"); //是否接收
		isql_1.putFieldValue("receivetime", str_currtime); //接收时间
		isql_1.putFieldValue("issubmit", "Y"); //是否提交
		isql_1.putFieldValue("submittime", str_currtime); //提交时间
		isql_1.putFieldValue("realsubmiter", _loginuserid); //实际提交的人!
		isql_1.putFieldValue("realsubmitername", str_loginusername); //实际提交人的名称!
		isql_1.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //实际提交的机构!
		isql_1.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //实际提交的机构名称!
		isql_1.putFieldValue("submitisapprove", "Y"); //提交的结果,有同意/退回/
		isql_1.putFieldValue("submitmessage", _message); //提交的意见
		isql_1.putFieldValue("submitmessagefile", _msgfile); //提交的附件
		isql_1.putFieldValue("isprocesser", "Y"); //是否是结者!!
		isql_1.putFieldValue("isprocess", "Y"); //过了
		if (str_parentInstanceId == null || str_parentInstanceId.trim().equals("")) { //如果父亲流程实例为空,则说明是根流程
			isql_1.putFieldValue("lifecycle", "E"); //是根流程结束!!
		} else {
			isql_1.putFieldValue("lifecycle", "EC"); //是子流程结束!!
		}
		v_sqls.add(isql_1.getSQL()); //

		//修改当前这一批数据,至关重要!
		if (!"4".equals(_wfParVO.getApproveModel())) { //散列模式不这么做
			v_sqls.add("update pub_wf_dealpool  set isprocess='Y' where prinstanceid='" + _prinstanceid + "' and batchno='" + str_batchno + "' and isprocess='N'"); //修改当前批数据已过.
		}

		//回写实例的历史执行人
		String[] str_instHistInfos = getNewSubmiterHist(_prinstanceid, _loginuserid, str_currtime, _message); //取得新的提交人的历史清单,就是在原有清单后面再加上本人!
		String str_newsubmiterhist = str_instHistInfos[0]; //处理历史
		String str_mylastsubmittime = str_instHistInfos[1]; //我的最后处理时间!
		String str_mylastsubmitmsg = str_instHistInfos[2]; //我的最后处理意见!

		UpdateSQLBuilder isql_2 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + _prinstanceid + "'"); //
		isql_2.putFieldValue("lastsubmiter", _loginuserid); //最后的提交人
		isql_2.putFieldValue("lastsubmitmsg", _message); //最后提交人的意见
		isql_2.putFieldValue("lastsubmitactivity", str_curractivity); //最后提交的环节
		isql_2.putFieldValue("lastsubmitresult", "Y"); //最后提交的结果,有同意/退回
		isql_2.putFieldValue("lastsubmittime", str_currtime); //最后提交的时间
		isql_2.putFieldValue("submiterhist", str_newsubmiterhist); //历史提交人的清单!
		isql_2.putFieldValue("mylastsubmittime", str_mylastsubmittime); //我的最后提交时间,用于在只显示一条业务单据时加载公式解析!
		isql_2.putFieldValue("mylastsubmitmsg", str_mylastsubmitmsg); //我的最后提交意见,用于在只显示一条业务单据时加载公式解析!
		v_sqls.add(isql_2); //

		getCommDMO().executeBatchByDS(null, v_sqls); //执行一把

		boolean isWFEnd = judgeWFisRealEnd(_prinstanceid); //判断流程是否结束!! 即可能还会存在其他批号的任务,即分支的情况!!!这个判断必须在先执行好一面的SQL进行,因为必须先设置本批任务isprocess=Y后才能进行!! 这充分利用了事务原理!!
		//System.out.println("流程是否结束了?=[" + isWFEnd + "]"); //
		String str_endResult = null; //
		if (isWFEnd) { //如果本流程的确是可以结束的,则结束本流程!!!
			boolean isExecInterceptAfterWfEnd = getTBUtil().getSysOptionBooleanValue("工作流中流程结束时是否要执行后台拦截器", false); //
			//执行环节定义的前置拦截器!!!
			_wfParVO.setMessage(_message);//流程结束拦截器中需要获得最后处理意见，_wfParVO.setMessage()在前台设置值为批注，未将实际处理意见转到后台，为了不影响以前流程，故只在拦截器执行前设置一下【李春娟/2016-12-15】
			_wfParVO.setMessagefile(_msgfile);

			if (isExecInterceptAfterWfEnd) {
				intercept2BeforeAction(_wfParVO, _loginuserid, _billVO, _endtype); //执行环节的前置拦截患者
			}

			UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + _prinstanceid + "'"); //
			isql_3.putFieldValue("status", "END"); //设置状态为END!!关键标记!!!
			isql_3.putFieldValue("endtype", _endtype); //设置结束类型!!!
			isql_3.putFieldValue("endtime", str_currtime); //设置结束时间!
			isql_3.putFieldValue("endbypoolid", _dealpoolid); //是因为哪条任务结束的!!以前是没有这个字段的,但后来在兴业项目中需要只显示子流程的最后意见(即中间过程意见不显示,因为太多了),这就需要一个状态标记来记录哪条任务才是最后一条结束的意见!然后根据这个标记进行过滤!
			isql_3.putFieldValue("currowner", (String) null); //当前拥有者为空
			isql_3.putFieldValue("curractivity", (String) null); //当前环节为空
			getCommDMO().executeUpdateByDS(null, isql_3); //必须先执行一把,因为下面需要判断兄弟流程是否结束,需要利用事务原理重取数据库的!!!

			if (str_parentInstanceId != null && !str_parentInstanceId.trim().equals("")) { //如果本流程实列有父流程,则说明这是个子流程,则还要判断兄弟流程是否结束!!!
				//至关重要的SQL与逻辑,即判断兄弟流程是否结束!!! 找出父亲流程与本人的父亲一样且是由同一个流程任务创建的流程,即我的兄弟流程,然后看是否还有状态不为END的!!!
				String str_judgeBrotherInstIsEndSQL = "select count(*) c1 from pub_wf_prinstance where parentinstanceid='" + str_parentInstanceId + "' and fromparentdealpoolid='" + str_fromParentDealPoolId + "' and status<>'END'";
				String str_brotherInstEndCount = getCommDMO().getStringValueByDS(null, str_judgeBrotherInstIsEndSQL);
				int li_brotherInstEndCount = Integer.parseInt(str_brotherInstEndCount); //兄弟流程没有结束的个数!!!
				//System.out.println("子流程的所有兄弟流程结束否?=[" + li_brotherInstEndCount + "]"); //
				if (li_brotherInstEndCount == 0) { //如果发现状态<>'END'的个数为0,则说明兄弟流程都结束了!!,则要为父流程创建新的任务!!
					HashVO[] hvsFrom = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id='" + str_fromParentDealPoolId + "'"); //找出本流程实例的第一个任务是由哪个流程任务创建,应该将这条任务复制一下!!
					String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_DEALPOOL"); //
					DealTaskVO dealTaskVO = new DealTaskVO(); //
					dealTaskVO.setFromActivityId(str_fromParentActivityId); //因为是启动的,所以不知从哪个环节来的
					dealTaskVO.setTransitionId(null); //因为是启动的,所以不知从哪根连线来的
					dealTaskVO.setCurrActivityId(hvsFrom[0].getStringValue("curractivity")); //当前环节,即启动环节!

					dealTaskVO.setParticipantUserId(hvsFrom[0].getStringValue("participant_user")); //参与者的ID
					dealTaskVO.setParticipantUserCode(hvsFrom[0].getStringValue("participant_usercode")); //
					dealTaskVO.setParticipantUserName(hvsFrom[0].getStringValue("participant_username")); //
					dealTaskVO.setParticipantUserDeptId(hvsFrom[0].getStringValue("participant_userdept")); //参与者的机构
					dealTaskVO.setParticipantUserDeptName(hvsFrom[0].getStringValue("participant_userdeptname")); //参与者机构的名称!!!
					dealTaskVO.setAccrUserId(hvsFrom[0].getStringValue("participant_accruserid")); //授权人!

					int li_newParentInstanceBatchNo = getNewBatchNo(str_parentInstanceId); //父流程新的批号!!!
					//刘旋飞发现,当多次发起会办时,在流程监控中,树形显示会错位... 已在徽商修改.  2012-10-25 加入到平台. gaofeng
					String str_parentWFCreateByid = hvsFrom[0].getStringValue("parentwfcreatebyid");
					if (str_parentWFCreateByid == null || str_parentWFCreateByid.trim().equals(""))
						str_parentWFCreateByid = hvsFrom[0].getStringValue("createbyid"); //找到原来那条记录的创建人,记录下来,这样在流程监控树型显示时就能有种"回归"效果!!!但又不能直接将createbyid换掉,因为本质的创建关系必须要有的!!在直接撤回时必须使用本质的关联关系!!
					//修改结束
					if (str_parentWFCreateByid == null) { //如果本流程实例的创建者的创建者为空(即流程实例的父亲的父亲,即爷爷!!),则说明他正好是根结点!!即是整个流程的第一个任务!为了保证子流程的回归效果,需要特殊处理为-99999,即表示是根结点!然后前台构建树时看到-9999就直接挂在根结点中!
						str_parentWFCreateByid = "-99999"; //设成-99999,表示是根结点!!!因为只有在创建子流程回归任务时数据库中该字段(parentwfcreatebyid)才有值,要么是个实际值,要么就是-99999,如果为空,则说明不是回归任务!则使用自己的createbyid勾连树型结构!!
					}

					String str_backWriteParentDealPoolSQL = getInsertDealPoolSQL(str_newDealPoolId, hvsFrom[0].getStringValue("prinstanceid"), hvsFrom[0].getStringValue("parentinstanceid"), hvsFrom[0].getStringValue("rootinstanceid"), hvsFrom[0].getStringValue("processid"), //
							_loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], dealTaskVO, "N", null, "", _dealpoolid, str_parentWFCreateByid, li_newParentInstanceBatchNo, str_currtime, null, null, "CB", false, null, null, null, null); //创建人是登录人,接收者就是原来的接收者,批号是新的批号!!!

					String str_backWriteParentTaskSQL = getInsertTaskDealSQL(str_newDealPoolId, hvsFrom[0].getStringValue("prinstanceid"), hvsFrom[0].getStringValue("parentinstanceid"), hvsFrom[0].getStringValue("rootinstanceid"), _dealpoolid, // 
							str_templetCode, str_tableName, str_queryTableName, str_pkName, str_pkValue, "结束时没取到", "", "[" + _billVO.getTempletName() + "]子流程会办全部结束(" + _billVO.toString() + "),请处理!", //以前会办结束后,给会办发起人创建回收任务时没有消息内容,如果使用任务中心是没有问题的,但如果使用以前的首页待办任务则提示内容为null,肯定无法接受!所以拼接了一个提示内容(xch-2012-01-11)!
							_loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], str_currtime, //
							hvsFrom[0].getStringValue("participant_user"), hvsFrom[0].getStringValue("participant_username"), hvsFrom[0].getStringValue("participant_userdept"), hvsFrom[0].getStringValue("participant_userdeptname"),//
							hvsFrom[0].getStringValue("participant_accruserid"), null, null, false); //

					String str_backWriteParentInstence = "update pub_wf_prinstance set curractivity='" + dealTaskVO.getCurrActivityId() + "',currowner=';" + dealTaskVO.getParticipantUserId() + ";' where id='" + hvsFrom[0].getStringValue("prinstanceid") + "'"; //回写父亲流程实例的当前状态与当前处理人,以前没做这个的时候,刘旋飞在兴业项目中发现,子流程回来时,那个环节的拦截器没执行!结果该有效编辑时却是灰的!
					getCommDMO().executeBatchByDS(null, new String[] { str_backWriteParentDealPoolSQL, str_backWriteParentTaskSQL, str_backWriteParentInstence }); ////找到父亲流程的当前发起子流程的那个人,给那个人回写新的任务,告诉他,子流程会办已结束了,请继续走!!!
					String str_endMsg = getTBUtil().getSysOptionStringValue("工作流子流程结束时的提示语", "所有会办结束,系统已自动提交给会办发起人"); //
					str_endResult = str_endMsg + "【" + dealTaskVO.getParticipantUserName() + "】!"; //
				} else {
					//String str_createUserName = getCommDMO().getStringValueByDS(null, "select participant_username from pub_wf_dealpool where id='" + str_fromParentDealPoolId + "'");
					str_endResult = getTBUtil().getSysOptionStringValue("工作流子流程某一分支结束时的提示语", "本次会办已结束"); //
				}
			} else { //说明是根流程!!!
			}

			//执行环节定义的后置拦截器!!!
			if (isExecInterceptAfterWfEnd) { //根据参数来!!!
				intercept2AfterAction(_wfParVO, _loginuserid, _billVO, _endtype); //执行环节的后置拦截器!!!
			}

			//执行流程定义的后置拦截器!!
			if (_wfegbsintercept != null && _wfegbsintercept.indexOf(".") > 0) { //如果流程定义了拦截器!!!则在BS端拦截流程定义的拦截器中的结束流程后的方法!!!
				String str_billtype = _billVO.getStringValue("billtype"); //
				String str_busitype = _billVO.getStringValue("busitype"); //
				WLTHashMap parMap = new WLTHashMap(); //
				WorkFlowEngineBSIntercept bsIntercept = (WorkFlowEngineBSIntercept) Class.forName(_wfegbsintercept).newInstance(); //BS端的整个流程定义的拦截器
				bsIntercept.afterWorkFlowEnd(str_billtype, str_busitype, _wfParVO, _loginuserid, _billVO, _endtype, parMap); //执行结束后方法
			}
		}
		return str_endResult; //
	}

	//管理员控制会办子流程结束 【杨科/2013-05-29】
	public String endWorkFlow_admin(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		String str_endResult = "";
		String str_parentinstanceid = _wfParVO.getParentinstanceid();

		//会办子流程已被删除 不能重复删除
		String sql_self = "select count(*) cou from pub_wf_prinstance where id='" + _prinstanceid + "'";
		String str_brotherInstEndCount_self = getCommDMO().getStringValueByDS(null, sql_self);
		if (Integer.parseInt(str_brotherInstEndCount_self) == 0) {
			return "会办子流程已被删除,不能重复删除!";
		}

		//会办子流程结束 不删除
		String sql_end = "select count(*) cou from pub_wf_prinstance where parentinstanceid='" + str_parentinstanceid + "' and status<>'END'";
		String str_brotherInstEndCount_end = getCommDMO().getStringValueByDS(null, sql_end);
		if (Integer.parseInt(str_brotherInstEndCount_end) == 0) {
			return "会办子流程已结束,无法删除!";
		}

		//会办子流程 除本流程实例未结束，其他实例已结束
		String sql = "select count(*) cou from pub_wf_prinstance where parentinstanceid='" + str_parentinstanceid + "' and id<>'" + _prinstanceid + "' and status<>'END'";
		String str_brotherInstEndCount = getCommDMO().getStringValueByDS(null, sql);
		if (Integer.parseInt(str_brotherInstEndCount) == 0) {
			//强制结束本流程实例的所有流程任务记录 因为有可能本流程实例流程任务已经处理 而管理员选择的不是待处理流程任务 结束判断时 该流程无法结束
			String upd_sql = "update pub_wf_dealpool  set isprocess='Y' where prinstanceid='" + _prinstanceid + "' and isprocess='N'";
			getCommDMO().executeBatchByDS(null, new String[] { upd_sql });

			str_endResult = endWorkFlow(_prinstanceid, _wfParVO, _loginuserid, _message, _msgfile, _endtype, _billVO, _wfegbsintercept);
		}

		//清除本流程实例所有流程相关记录
		ArrayList al_sqls = new ArrayList();
		al_sqls.add("delete from pub_task_deal where prinstanceid='" + _prinstanceid + "'"); //删除待办任务
		al_sqls.add("delete from pub_task_off where prinstanceid='" + _prinstanceid + "'"); //删除已办任务
		al_sqls.add("delete from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "'"); //删除流程处理表
		al_sqls.add("delete from pub_wf_prinstance where id='" + _prinstanceid + "'"); //删除流程实例表

		//未处理父流程分配给该流程的流程处理人相关记录 不影响流程 也可作纠错校验

		getCommDMO().executeBatchByDS(null, al_sqls);

		if (str_endResult.equals("")) {
			str_endResult = "删除成功!";
		} else {
			str_endResult = "删除成功! 且 " + str_endResult;
		}

		return str_endResult;
	}

	/**
	 * 确认(即删除)无效的任务(比如抄送,过期等)!! 即直接搬家
	 * @param _taskId
	 * @param _dealPoolId
	 * @param _unEffectReason 无效的原因,比如抄送,过期等!!! 如果是无效的任务,则不能做撤回处理! 因为即使撤回了也没有意义!!
	 */
	public String confirmUnEffectTask(String _dealPoolId, String _unEffectReason, String _loginUserId) throws Exception {
		String str_userName = getUserCodeNameById(_loginUserId); //
		String str_currtime = getTBUtil().getCurrTime(); //当前时间!
		String str_dealMsg = "直接确认任务(比如被抄送的任务等)"; //
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginUserId); //登录人员的机构id与name
		String str_sql_insert_taskoff = getInsertTaskOffSQL(_dealPoolId, _loginUserId, str_userName, str_currtime, str_dealMsg, null, null, null, null, false, _unEffectReason); //
		if (str_sql_insert_taskoff == null) {
			//这里只是个提示，太平集团建议不要抛出异常【李春娟/2018-07-25】
			return "该任务已不存在,可能在其他地方被处理了,故没必要做确认逻辑了!"; //如果是抄送的信息,是有值的,但如果是被其他地方处理的,则为空,以前客户端这里是报异常的,后来客户端改成吃掉异常了!
		}
		String str_sql_delete_taskdeal = "delete from pub_task_deal where prdealpoolid='" + _dealPoolId + "'"; //删除已办任务

		UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + _dealPoolId + "'"); //
		isql_3.putFieldValue("isreceive", "Y"); //是否接收
		isql_3.putFieldValue("issubmit", "Y"); //是否已提交
		isql_3.putFieldValue("submittime", str_currtime); //提交的时间
		isql_3.putFieldValue("realsubmiter", _loginUserId); //实际提交的人!
		isql_3.putFieldValue("realsubmitername", str_userName); //实际提交人的名称!
		isql_3.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //实际提交的机构!
		isql_3.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //实际提交的机构名称!
		isql_3.putFieldValue("submitmessage", str_dealMsg); //提交的意见

		getCommDMO().executeBatchByDS(null, new String[] { str_sql_insert_taskoff, str_sql_delete_taskdeal, isql_3.getSQL() }); //执行这三条SQL
		return null;
	}

	/**
	 * 继续工作流,相对对暂停而言的,即流程可以暂停与继续,主要是用于进行某种流程消耗时间的统计与分析!!!
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void restartWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		String str_sql_1 = "select currbatchno from pub_wf_prinstance where id='" + _prinstanceid + "'"; //取得流程实例的当前批号
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1); //
		int li_batchno = hvs_1[0].getIntegerValue("currbatchno").intValue(); //

		String str_sql_2 = "update pub_wf_prinstance set status='RUN' where id='" + _prinstanceid + "'"; //设置当前状态为Hold

		String str_new_id = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_holdhistory"); //
		String str_actiontime = getTBUtil().getCurrTime(); //

		String str_sql_3 = "insert into pub_wf_holdhistory (id,prinstanceid,batchno,actiontype,actiontime,actioner) values (" + str_new_id + ",'" + _prinstanceid + "'," + li_batchno + ",'RESTART','" + str_actiontime + "','" + _loginuserid + "')"; //
		getCommDMO().executeBatchByDS(null, new String[] { str_sql_2, str_sql_3 }); //执行批SQL.
	}

	/**
	 * 取得一个工作流的历史处理记录!
	 * @param _prinstanceId
	 * @return
	 */
	public HashVO[] getProcessHistoryRecord(String _prinstanceId, boolean _isHiddenMsg) throws Exception {
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select "); //
		sb_sql.append("pub_wf_dealpool.id as id,curractivity,pub_wf_activity.iscanlookidea as curractivity_iscanlookidea,pub_wf_activity.isneedreport as isneedreport,pub_wf_activity.wfname curractivity_name,prinstanceid,");
		sb_sql.append("participant_user,participant_usercode,participant_username,submittime,submitmessage,submitmessagefile,submitisapprove "); //
		sb_sql.append("from pub_wf_dealpool "); //
		sb_sql.append("left outer join pub_wf_activity on pub_wf_dealpool.curractivity=pub_wf_activity.id "); //
		sb_sql.append("where issubmit='Y' and prinstanceid='" + _prinstanceId + "' order by pub_wf_dealpool.submittime"); //
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //先取得历史记录

		if (!_isHiddenMsg) { //如果不做屏蔽处理，则直接返回!
			return hvs;
		}
		new WorkFlowBSUtil().dealHiddenMsg(hvs, _prinstanceId); //做屏蔽处理.. 
		return hvs;
	}

	/**
	 * 构造流程实例的SQL,至关重要!
	 */
	private String getInsertPrinstanceSQL(String _newPrinstanceId, String _parentInstanceId, String _rootInstanceId, String _processid, String _templetCode, String _tableName, String _queryTabName, String _pkName, String _pkValue, String _createUserId, String _currOwner, String _createdActivityId, String _fromParentActivity, String _fromParentDealPoolId) {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_wf_prinstance"); //
		isql.putFieldValue("id", _newPrinstanceId); //主键
		isql.putFieldValue("parentinstanceid", _parentInstanceId); //父亲流程实例的Id,因为后来加入了子流程功能
		isql.putFieldValue("rootinstanceid", _rootInstanceId); //根流程实例Id,因为后来加入了子流程功能
		isql.putFieldValue("processid", _processid); //流程定义id

		isql.putFieldValue("billtempletcode", _templetCode); //模板编码
		isql.putFieldValue("billtablename", _tableName); //业务保存查询表名
		isql.putFieldValue("billquerytablename", _queryTabName); //业务保存表名
		isql.putFieldValue("billpkname", _pkName); //业务表主键名
		isql.putFieldValue("billpkvalue", _pkValue); //业务表主键值

		isql.putFieldValue("creater", _createUserId); //流程创建者,以前是用父流程的创建人的后来改成了使用子流程的接收者直接作为子流程创建者!这样更合理些,因为子流程中的参与者配置使用流程创建者时,就更对! 因为后续的参与者一般都是创建者的同一部门!!
		isql.putFieldValue("createactivity", _createdActivityId); //创建的环节
		isql.putFieldValue("createtime", getCurrDateTime()); //创建的时间
		isql.putFieldValue("endtime", (String) null); //结果时间,因为刚创建,所以为空

		isql.putFieldValue("curractivity", _createdActivityId); //当前环节
		isql.putFieldValue("curractivityname", ""); //当前环节名称
		isql.putFieldValue("currowner", ";" + _currOwner + ";"); //当前拥有者,极其关键!!!旧的机制就是根据这个判断是否属于我的待办任务!!
		isql.putFieldValue("currbatchno", "1"); //当前批号,一开始是

		isql.putFieldValue("lastsubmiter", _createUserId); //最后提交者
		isql.putFieldValue("lastsubmitername", _createUserId); //最后提交者名称
		isql.putFieldValue("lastsubmittime", (String) null); //最后提交时间
		isql.putFieldValue("lastsubmitmsg", "流程启动"); //最后提交的信息,因为刚创建,所以干脆直接叫"流程启动"
		isql.putFieldValue("lastsubmitactivity", (String) null); //最后提交的环节
		isql.putFieldValue("lastsubmitresult", (String) null); //最后提交的环节

		//isql.putFieldValue("mylastsubmittime", (String) null); //我的最后提交时间
		//isql.putFieldValue("mylastsubmitmsg", (String) null); //我的最后提交意见

		isql.putFieldValue("fromparentactivity", _fromParentActivity); //子流程是从父流程的哪个环节创建的
		isql.putFieldValue("fromparentdealpoolid", _fromParentDealPoolId); //子流程是从父流程的哪个流程任务创建的!它其实就可以作为这一批子流程的共同标识!!

		isql.putFieldValue("status", "START"); //状态,创建时为START
		return isql.getSQL(); //返回SQL
	}

	/**
	 * 构造流程任务的SQL,至关重要!
	 */
	private String getInsertDealPoolSQL(String _newDealPoolId, String _prinstanceId, String _parentInstanceId, String _rootInstanceId, String _processID, String _createrId, String _createrName, String _createCorp, String _createCorpName, DealTaskVO _dealTaskVO, String _issubmit, String _isApprove, String _message, String _createbyid, String _parentwfcreatebyid, int _batchno, String _createTime,
			String _prioritylevel, String _dealtimelimit, String _lifecycle, boolean _isSelfCycleclick, String _selfFromRoleCode, String _selfFromRoleName, String _selfToRoleCode, String _selfToRoleName) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_wf_dealpool"); ////
		isql.putFieldValue("id", _newDealPoolId); //任务主键
		isql.putFieldValue("prinstanceid", _prinstanceId); //实例主键
		isql.putFieldValue("parentinstanceid", _parentInstanceId); //父亲流程实例主键
		isql.putFieldValue("rootinstanceid", _rootInstanceId); //根流程实例主键
		isql.putFieldValue("processid", _processID); //流程定义的ID
		isql.putFieldValue("creater", _createrId); //任务创建者
		isql.putFieldValue("creatername", _createrName); //任务创建者名称
		isql.putFieldValue("createcorp", _createCorp); //任务创建者的机构,按道理不应为空,但实际情况可能为空,因为有人没绑定机构!
		isql.putFieldValue("createcorpname", _createCorpName); //任务创建者的机构名称
		isql.putFieldValue("createtime", _createTime); //任务创建时间
		isql.putFieldValue("prioritylevel", _prioritylevel); //紧急程度 
		isql.putFieldValue("dealtimelimit", _dealtimelimit); //要求最后处理期限
		isql.putFieldValue("batchno", _batchno); //新的批号
		isql.putFieldValue("fromactivity", _dealTaskVO.getFromActivityId()); //从哪个环节来的
		isql.putFieldValue("transition", _dealTaskVO.getTransitionId()); //从哪根线上来的
		isql.putFieldValue("curractivity", _dealTaskVO.getCurrActivityId()); //当前环节
		isql.putFieldValue("currapprovemodel", _dealTaskVO.getCurrActivityApproveModel()); //当前环节的审批模式!!!
		isql.putFieldValue("participant_user", _dealTaskVO.getParticipantUserId()); //参与者(接收者),因为后来一个人可以兼职三个部门,所以还需加上参与者的所属部门!!!即需要指明是A部门的张三还是B部门的张三处理该任务.
		isql.putFieldValue("participant_usercode", _dealTaskVO.getParticipantUserCode()); //参与者(接收者)编码
		isql.putFieldValue("participant_username", _dealTaskVO.getParticipantUserName()); //参与者(接收者)名称
		isql.putFieldValue("participant_userdept", _dealTaskVO.getParticipantUserDeptId()); //参与者的所属机构,因为可以兼职多个机构,所以这里表示是代表的哪一个机构. 
		isql.putFieldValue("participant_userdeptname", _dealTaskVO.getParticipantUserDeptName()); //参与者的所属机构名称,因为可以兼职多个机构,所以这里表示是代表的哪一个机构.
		isql.putFieldValue("participant_userrole", _dealTaskVO.getParticipantUserRoleId()); //参与者角色
		isql.putFieldValue("participant_userrolename", _dealTaskVO.getParticipantUserRoleName()); //参与者角色
		isql.putFieldValue("participant_accruserid", _dealTaskVO.getAccrUserId()); //授权人id
		isql.putFieldValue("participant_accrusercode", _dealTaskVO.getAccrUserCode()); //授权人编码
		isql.putFieldValue("participant_accrusername", _dealTaskVO.getAccrUserName()); //授权人名称
		isql.putFieldValue("isrejecteddirup", (_dealTaskVO.isRejectedDirUp() ? "Y" : "N")); //是否直接打回的方式

		isql.putFieldValue("isselfcycleclick", _isSelfCycleclick ? "Y" : "N"); //是否是自循环内部流转的??
		isql.putFieldValue("selfcycle_fromrolecode", _selfFromRoleCode); //自循环点击的对应的“小流程”的环节编码
		isql.putFieldValue("selfcycle_fromrolename", _selfFromRoleName); //自循环点击的对应的“小流程”的环节名称（其实就是角色名称！）

		isql.putFieldValue("selfcycle_torolecode", _selfToRoleCode); //自循环点击的对应的“小流程”的环节编码
		isql.putFieldValue("selfcycle_torolename", _selfToRoleName); //自循环点击的对应的“小流程”的环节名称（其实就是角色名称！）

		isql.putFieldValue("isdeal", "N"); //是否处理,因为刚创建所以为N
		isql.putFieldValue("isreceive", "N"); //是否接收,因为刚创建,所以必然为N
		isql.putFieldValue("isprocess", "N"); //该批任务是否结束
		isql.putFieldValue("ispass", "N"); //该批任务是否通过
		isql.putFieldValue("isccto", (_dealTaskVO.isCCTo() ? "Y" : "N")); //是否抄送
		isql.putFieldValue("issubmit", _issubmit); //是否提交,因为存在退回与提交两种方式,所有一开始是设计成表示同意与否的! 但后来统一改成了是否点了提交,即没处理时是N,处理后就是Y
		isql.putFieldValue("submitisapprove", _isApprove); //提交的结果,即有同意提交与非同意提交!
		isql.putFieldValue("submitmessage", _message); //提交意见
		isql.putFieldValue("createbyid", _createbyid); //记录下该条任务是因为哪一个任务创建的,这个信息非常重要!!!
		isql.putFieldValue("parentwfcreatebyid", _parentwfcreatebyid); //我这条记录的父亲流程任务是由谁创建的!!即当发生子流程时,回到创建子流程的创建人那时,需要记录父亲流程的原来是由谁创建的,由于在树型显示时就能根据这个值将回归效果体现出来!但又不能直接将createbyid换掉,因为做直接撤回时又需要实质的创建关系!
		isql.putFieldValue("lifecycle", _lifecycle); //生命周期,取值有C(创建主流程)/CC(创建子流程)/EC(结束子流程)/E(结束主流程)
		return isql.getSQL(); //返回SQL
	}

	/**
	 * 构造创建待办任务的SQL,至关重要!!
	 */
	private String getInsertTaskDealSQL(String _newDealPoolId, String _prinstanceid, String _parentInstanceId, String _rootInstanceId, String _createByDealPoolId, String _templetcode, String _tabname, String _queryTableName, String _pkName, String _pkValue, String _tabItemValue, String _dealMsg, String _msg, String _creater, String _createrName, String _createrCorpId, String _createrCorpName,
			String _createtime, String _dealuser, String _dealuserName, String _dealUserCorp, String _dealUserCorpName, String _accrUserId, String _prioritylevel, String _dealtimelimit, boolean _isCCTo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_task_deal"); //
		isql.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_TASK_DEAL")); //
		//isql.putFieldValue("taskclass", ""); //任务大类
		isql.putFieldValue("prinstanceid", _prinstanceid); //流程实例ID
		isql.putFieldValue("parentinstanceid", _parentInstanceId); //父流程实例id
		isql.putFieldValue("rootinstanceid", _rootInstanceId); //根流程实例id
		isql.putFieldValue("prdealpoolid", _newDealPoolId); //流程任务ID
		isql.putFieldValue("createbydealpoolid", _createByDealPoolId); //由哪个流程任务创建的
		isql.putFieldValue("templetcode", _templetcode); //模板编码
		isql.putFieldValue("tabname", _tabname); //表名
		//isql.putFieldValue("querytabname", _queryTableName); //查询的表名,这个以后不要了!
		isql.putFieldValue("pkname", _pkName); //主键字段名
		isql.putFieldValue("pkvalue", _pkValue); //主键字段值
		isql.putFieldValue("tabitemvalue", _tabItemValue); //表中某个字段的值,比如风险事件名称,如果存下来的话,则意味着不要关联业务表了!

		//创建者的相关信息
		isql.putFieldValue("creater", _creater); //创建者
		isql.putFieldValue("creatername", _createrName); //创建者名称
		isql.putFieldValue("createcorp", _createrCorpId); //创建机构id(即创建人的所属机构)
		isql.putFieldValue("createcorpname", _createrCorpName); //创建机构名称(即创建人的所属机构)
		isql.putFieldValue("createtime", _createtime); //创建时间
		isql.putFieldValue("createrdealmsg", _dealMsg); //创建人提交的意见
		isql.putFieldValue("msg", _msg); //平台生成的提示消息,即什么人在什么环节提交给你了任务!

		//处理者(接收人)相关信息
		isql.putFieldValue("dealuser", _dealuser); //处理人id,非常关键,以后就根据这个判断是否属于登录人员的任务!
		isql.putFieldValue("dealusername", _dealuserName); //处理人名称
		isql.putFieldValue("dealusercorp", _dealUserCorp); //待办人的机构id
		isql.putFieldValue("dealusercorpname", _dealUserCorpName); //待办人的机构名称
		isql.putFieldValue("accruserid", _accrUserId); //授权人id
		isql.putFieldValue("isccto", (_isCCTo ? "Y" : "N")); //是否是抄送的消息
		isql.putFieldValue("prioritylevel", _prioritylevel); //优先级
		isql.putFieldValue("dealtimelimit", _dealtimelimit); //处理时间限制
		if (!TBUtil.isEmpty(sendSMSImpl)) {
			try {
				Object obj = Class.forName(sendSMSImpl).newInstance();
				if (obj instanceof SendSMSIFC && (!_isCCTo && !"启动流程".equals(_msg))) {
					SendSMSIFC sendsms = (SendSMSIFC) obj;
					sendsms.send(_dealuser, _msg, "工作流短信", _creater, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//实际阅读的信息
		isql.putFieldValue("islookat", "N"); //是否已查看,创建时默认为N
		isql.putFieldValue("lookattime", (String) null); //查看时间,创建时默认为空
		return isql.getSQL(); //
	}

	/**
	 * 构造已办任务的SQL,至关重要,主要的四张表pub_wf_prinstance,pub_wf_dealpool,pub_task_dealpool,pub_task_off就是工作流部分的核心表
	 * 待办任务处理后会进入已办任务(搬家换了个位置),而一个待办任务总是由另一个待办任务生成的,这就是说所有的待办任务与已办任务合在一起应该是一个树型结构!!!
	 * 如果用树型展示,则将已处理的用绿色显示,未处理用红色显示,提交的用某种图标显示,退回的用某种图标显示! 抄送的用斜体显示等各种效果! 未阅读的用粗体显示,等等,
	 * 总之,使用各种效果做一个效果非常漂亮的树型结构,从而一眼清的知道整个流程处理历史,这种效果比表形结构有着更形象的效果,即它能更清晰的知道提交顺序,即谁给了谁!
	 * 在同一个rootinstanceid中的所有记录肯定是可以构成一颗树的!!
	 * @return
	 */
	private String getInsertTaskOffSQL(String _dealPoolId, String _realDealUser, String _realDealUserName, String _dealtime, String _dealMsg, String _submittouser, String _submittousername, String _submittocorp, String _submittocorpname, boolean _isSubmit, String _confirmReason) throws Exception {
		HashVO[] taskHVOs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_task_deal where prdealpoolid='" + _dealPoolId + "'"); //先找出待办任务!
		if (taskHVOs == null || taskHVOs.length == 0) {
			return null; //
		}
		String[] str_copyCols = new String[] { "taskclass", "prinstanceid", "parentinstanceid", "rootinstanceid", "prdealpoolid", "createbydealpoolid", "templetcode", "tabname", "pkname", "pkvalue", "tabitemvalue", // 
				"creater", "creatername", "createcorp", "createcorpname", "createtime", "createrdealmsg", "msg", //创建者的相关信息
				"dealuser", "dealusername", "dealusercorp", "dealusercorpname", "accruserid", "isccto", "prioritylevel", "dealtimelimit" //待处理者的相关信息
		}; //
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_task_off"); //
		isql.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_TASK_OFF")); //主键!!
		isql.putFieldValue("taskdealid", taskHVOs[0].getStringValue("id")); //待办任务的主键,用于知道是从哪个任务拷贝过来的!
		for (int i = 0; i < str_copyCols.length; i++) {
			isql.putFieldValue(str_copyCols[i], taskHVOs[0].getStringValue(str_copyCols[i])); //直接复制
		}

		//实际处理的相关信息
		isql.putFieldValue("realdealuser", _realDealUser); //实际处理人,因为存在授权问题,即授权人与待办人都可能会处理,所以实际处理人并不知道是谁,所以需要记录下来!
		isql.putFieldValue("realdealusername", _realDealUserName); //实际处理人名称,是工号/名称的样子
		isql.putFieldValue("dealtime", _dealtime); //处理时间,即实际处理的时间
		isql.putFieldValue("dealmsg", _dealMsg); //处理意见,即实际处理意见!也就是工作流提交界面中的意见框中输入的内容!它应该会出现在pub_task_deal表中的另一条记录的createrdealmsg字段中,所以在待办任务表中还应有个字段叫createbytaskoffid,即某个待办任务是由某个已办任务生成的

		isql.putFieldValue("submittouser", _submittouser); //提交给什么人
		isql.putFieldValue("submittousername", _submittousername); //提交给什么人的名称
		isql.putFieldValue("submittocorp", _submittocorp); //提交给什么机构
		isql.putFieldValue("submittocorpname", _submittocorpname); //提交给的机构名称!!

		isql.putFieldValue("submitorconfirm", (_isSubmit ? "submit" : "confirm")); //提交还是确认!!
		isql.putFieldValue("confirmreason", _confirmReason); //确认的原因!!

		return isql.getSQL(); //返回SQL
	}

	//将已办任务回写到待办任务中去的SQL
	private String getCancelToTaskDealSQL(String _dealpoolId) throws Exception {
		HashVO[] taskHVOs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_task_off where prdealpoolid='" + _dealpoolId + "'"); //先找出待办任务!
		if (taskHVOs == null || taskHVOs.length <= 0) { //如果是旧的机制,则可能没有已办任务(因为当时根本就没有这张表呢)!!!必须处理!!
			return null; //
		}
		String[] str_copyCols = new String[] { "taskclass", "prinstanceid", "parentinstanceid", "rootinstanceid", "prdealpoolid", "createbydealpoolid", "templetcode", "tabname", "pkname", "pkvalue", "tabitemvalue", // 
				"creater", "creatername", "createcorp", "createcorpname", "createtime", "createrdealmsg", "msg", //创建者的相关信息
				"dealuser", "dealusername", "dealusercorp", "dealusercorpname", "accruserid", "isccto", "prioritylevel", "dealtimelimit" //待处理者的相关信息
		}; //
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_task_deal"); //
		isql.putFieldValue("id", taskHVOs[0].getStringValue("taskdealid")); //还是原来的任务id
		for (int i = 0; i < str_copyCols.length; i++) {
			isql.putFieldValue(str_copyCols[i], taskHVOs[0].getStringValue(str_copyCols[i])); //直接复制
		}
		return isql.getSQL(); //返回SQL
	}

	/**
	 * 是否是最后一个处理,难点与关键点之一!!!
	 * 
	 * @param _prinstanceid
	 * @param _currActivityVO
	 * @return
	 */
	private boolean isLastCommit(String _prinstanceid, Integer _batchno, String _approveModel, Integer _approveNumber) throws Exception {
		// 取得环节的详细信息
		if (_approveModel == null || _approveModel.equals("1") || _approveModel.equals("4")) { //如果是抢占式,则直接都说明是最后的提交者!!!
			return true;
		} else { // 如果是会签,则要看我是否是最后一个提交的!!!如果我是最后一个提交的,则说明我是最后提交的人!!
			String str_sql = "select id from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and batchno='" + _batchno + "' and issubmit='N' and (isccto='N' or isccto is null)"; // 取出当前流程，当前环节，所有未处理的记录,要将抄送的剔除掉!
			HashVO[] vos = getCommDMO().getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 1) { // 如果只剩下一个没提交!!
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 取得下一个环节,需要判断条件!!!比较麻烦!!也是难点之一!!
	 * @param string2 
	 * @param string 
	 * @param firstWFParVO 
	 * 
	 * @param _currActivityVO
	 * @param _billvo
	 * @return
	 */
	private NextTransitionVO[] getNextTransitionVOs(WFParVO _firstWFParVO, String _prinstanceid, String _dealpoolid, HashVO _hashVO, BillVO _billvo, String _dealType) throws Exception {
		// 找出有几根出去的连线
		// 分别执行每根线上的条件,
		// 返回结果为True的所有线的目录结点!
		Vector vector_tmp = new Vector(); //先创建向量,用于存储!

		String str_fromActivity_id = _hashVO.getStringValue("curractivity"); //来源环节的id
		String str_fromActivity_type = _hashVO.getStringValue("curractivity_activitytype"); //来源环节的类型
		String str_fromActivity_isselfcycle = _hashVO.getStringValue("curractivity_isselfcycle"); //是否自循环

		//先判断是否具有自循环功能,如果有,则先查找出当前环节,即来源环节与目标环节是一样的!!即总是将自循环放在第一个的位置 !!!
		if ("Y".equals(str_fromActivity_isselfcycle) || "SEND".equals(_dealType)) { //如果来源环节是自循环!!!
			NextTransitionVO nextTransitionVO = new NextTransitionVO(); //
			nextTransitionVO.setSortIndex(0); //为了保证自循环的永远排在第一位,强制设0,就在排在第一个了!★★★★★★★★★

			nextTransitionVO.setFromactivityId(str_fromActivity_id); //源环节
			nextTransitionVO.setFromactivityCode(_hashVO.getStringValue("curractivity_code")); //环节编码
			nextTransitionVO.setFromactivityWFName(_hashVO.getStringValue("curractivity_wfname")); //环节名称
			nextTransitionVO.setFromactivityUIName(_hashVO.getStringValue("curractivity_uiname")); //环节名称
			nextTransitionVO.setFromactivityApprovemodel(_hashVO.getStringValue("curractivity_approvemodel")); //审批模式

			nextTransitionVO.setToactivityId(str_fromActivity_id); //目标环节,因为是自循环,所以目标环节与源环节一样!!
			nextTransitionVO.setToactivityCode(_hashVO.getStringValue("curractivity_code")); //目标环节编码
			nextTransitionVO.setToactivityWFName(_hashVO.getStringValue("curractivity_wfname")); //目标环节名称
			nextTransitionVO.setToactivityUIName(_hashVO.getStringValue("curractivity_uiname")); //目标环节名称
			nextTransitionVO.setToactivityBelongDeptGroupName(_hashVO.getStringValue("curractivity_belongdeptgroup")); //目标环节的所属组的名称,在跨部门或发生子流程时需要在显示环节时同时显示所属组的名称!!! 兴业的客户曾强烈提出这个问题,因为我们和工作流比他们的Notes更复杂,结果就会环节名称有问题!

			nextTransitionVO.setToactivityType(str_fromActivity_type); //目标环节类型
			nextTransitionVO.setToactivityApprovemodel(_hashVO.getStringValue("curractivity_approvemodel")); //目标环节的审批模式.
			nextTransitionVO.setToactivityShowparticimode(_hashVO.getStringValue("curractivity_showparticimode")); //目标环节的显示参与者的模式!
			nextTransitionVO.setToactivityParticipate_user(_hashVO.getStringValue("curractivity_participate_user")); //参与的人!!!!
			nextTransitionVO.setToactivityParticipate_corp(_hashVO.getStringValue("curractivity_participate_corp")); //参与的机构!!!
			nextTransitionVO.setToactivityParticipate_group(_hashVO.getStringValue("curractivity_participate_group")); //参与的角色!!
			nextTransitionVO.setToactivityParticipate_dynamic(_hashVO.getStringValue("curractivity_participate_dynamic")); //动态参与组
			nextTransitionVO.setToactivityCanselfaddparticipate(_hashVO.getStringValue("curractivity_canselfaddparticipate"));

			nextTransitionVO.setToactivityCcto_user(_hashVO.getStringValue("curractivity_ccto_user")); //目标环节上定义的抄送的人员
			nextTransitionVO.setToactivityCcto_corp(_hashVO.getStringValue("curractivity_ccto_corp")); //目标环节上定义的抄送的机构
			nextTransitionVO.setToactivityCcto_role(_hashVO.getStringValue("curractivity_ccto_role")); //目标环节上定义的抄送的角色
			nextTransitionVO.setToactivityIsSelfCycle(_hashVO.getBooleanValue("curractivity_isselfcycle", false)); //是否自循环!
			nextTransitionVO.setToactivitySelfCycleMap(_hashVO.getStringValue("curractivity_selfcyclerolemap")); //是否自循环!

			nextTransitionVO.setPasscondition(true); //自循环,则直接通过!
			vector_tmp.add(nextTransitionVO); //加入
		}

		if (!"SEND".equals(_dealType)) {
			String str_sql = "select * from pub_wf_transition where fromactivity='" + str_fromActivity_id + "'"; //找出所有的线
			LinkForeignTableDefineVO lfvo_from = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,uiname,activitytype,approvemodel", "id", "fromactivity"); //
			LinkForeignTableDefineVO lfvo_to = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,uiname,belongdeptgroup,activitytype,approvemodel,isselfcycle,selfcyclerolemap,showparticimode,participate_user,participate_corp,participate_group,participate_dynamic,ccto_user,ccto_corp,ccto_role,extconfmap,canselfaddparticipate", "id", "toactivity"); //

			HashVO[] hvs = getCommDMO().getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_from, lfvo_to }); //
			for (int i = 0; i < hvs.length; i++) {
				boolean bo_ifpasscondition = true; //是否通过条件!默认是通过的! ★★★★处理公式条件★★★★
				String str_condition = hvs[i].getStringValue("conditions"); //
				JepFormulaParseAtWorkFlow jepFormula = new JepFormulaParseAtWorkFlow(_firstWFParVO, _prinstanceid, _dealpoolid, _billvo); //将流程处理任务与实例主键，处理任务主键都传给公式引擎
				HashMap tsConfMap = null; //条件上的扩展参数
				//如果连线上定义了条件!则计算之！
				if (str_condition != null && !str_condition.trim().equals("")) { //如果连线上定义了条件！！！
					str_condition = str_condition.trim(); //
					str_condition = getTBUtil().replaceAll(str_condition, "\r", ""); //
					str_condition = getTBUtil().replaceAll(str_condition, "\n", ""); //
					String[] str_conItems = null; //
					if (str_condition.indexOf("；") > 0) { //如果存在公式中就有分号的情况,则可以使用【中文分号】分隔！！！
						str_conItems = getTBUtil().split(str_condition, "；"); //用分号相隔
					} else {
						str_conItems = getTBUtil().split(str_condition, ";"); //用分号相隔
					}

					//以前只有一条公式!现在增加多条公式！即还可以设置更多扩展参数！！！
					if (str_conItems.length >= 2) { //如果有两个参数!
						//先处理公式条件计算!
						Object obj_1 = jepFormula.execFormula(str_conItems[0]); //执行的结果!!! 
						if (("" + obj_1).equalsIgnoreCase("true")) { //如果是返回的是True
							bo_ifpasscondition = true; //
						} else if (("" + obj_1).equalsIgnoreCase("false")) { //
							bo_ifpasscondition = false; //
						} else {
							bo_ifpasscondition = false; //
						}
						//再计算参数
						Object obj_2 = jepFormula.execFormula(str_conItems[1]); //执行的结果!!!
						tsConfMap = (HashMap) obj_2; //
					} else { //如果只有一个参数!
						Object obj = jepFormula.execFormula(str_conItems[0]); //执行的结果!!! 比如
						if (obj != null) {
							if (obj instanceof HashMap) { //如果就是个哈希表!则表明只有扩展参数，没有条件定义！
								bo_ifpasscondition = true; //
								tsConfMap = (HashMap) obj; //
								//System.out.println("只有一个参数,且是HashMap,内容是:" + tsConfMap); //
							} else {
								if (obj.toString().equalsIgnoreCase("true")) { //如果是返回的是True
									bo_ifpasscondition = true; //
								} else if (obj.toString().equalsIgnoreCase("false")) { //
									bo_ifpasscondition = false; //
								} else {
									bo_ifpasscondition = false; //
								}
							}
						}
					}
				}

				if (bo_ifpasscondition) {
					NextTransitionVO nextTransitionVO = new NextTransitionVO(); //
					nextTransitionVO.setId(hvs[i].getStringValue("id")); //
					nextTransitionVO.setProcessid(hvs[i].getStringValue("processid")); //
					nextTransitionVO.setCode(hvs[i].getStringValue("code")); //连线的编码
					nextTransitionVO.setWfname(hvs[i].getStringValue("wfname")); //
					nextTransitionVO.setUiname(hvs[i].getStringValue("uiname")); //
					nextTransitionVO.setMailSubject(hvs[i].getStringValue("mailsubject")); //邮件主题
					nextTransitionVO.setMailContent(hvs[i].getStringValue("mailcontent")); //邮件内容
					nextTransitionVO.setDealtype(hvs[i].getStringValue("dealtype")); //连线的处理类型,有Submit与Reject,以前用他来做退回处理的!现在用他来做是否决定接收环节只显示计算的还是曾经走过的!
					nextTransitionVO.setIntercept(hvs[i].getStringValue("intercept")); //设置拦截器
					nextTransitionVO.setReasoncodesql(hvs[i].getStringValue("reasoncodesql")); //

					nextTransitionVO.setFromactivityId(hvs[i].getStringValue("fromactivity")); //源环节
					nextTransitionVO.setFromactivityCode(hvs[i].getStringValue("fromactivity_code")); //源环节
					nextTransitionVO.setFromactivityWFName(hvs[i].getStringValue("fromactivity_wfname")); //源环节
					nextTransitionVO.setFromactivityUIName(hvs[i].getStringValue("fromactivity_uiname")); //源环节
					nextTransitionVO.setFromactivityApprovemodel(hvs[i].getStringValue("fromactivity_approvemodel")); //源环节的审批模式

					nextTransitionVO.setToactivityId(hvs[i].getStringValue("toactivity")); //目标环节
					nextTransitionVO.setToactivityCode(hvs[i].getStringValue("toactivity_code")); //目标环节
					nextTransitionVO.setToactivityWFName(hvs[i].getStringValue("toactivity_wfname")); //目标环节
					nextTransitionVO.setToactivityUIName(hvs[i].getStringValue("toactivity_uiname")); //目标环节
					nextTransitionVO.setToactivityBelongDeptGroupName(hvs[i].getStringValue("toactivity_belongdeptgroup")); //目标环节的所属部门组的名称!
					nextTransitionVO.setToactivityType(hvs[i].getStringValue("toactivity_activitytype")); //目标环节类型
					nextTransitionVO.setToactivityApprovemodel(hvs[i].getStringValue("toactivity_approvemodel")); //目标环节的审批模式.
					nextTransitionVO.setToactivityShowparticimode(hvs[i].getStringValue("toactivity_showparticimode")); //目标环节的显示参与者的模式!
					nextTransitionVO.setToactivityParticipate_user(hvs[i].getStringValue("toactivity_participate_user")); //参与的人!!!!

					nextTransitionVO.setToactivityParticipate_corp(hvs[i].getStringValue("toactivity_participate_corp")); //参与的机构!!!
					nextTransitionVO.setToactivityParticipate_group(hvs[i].getStringValue("toactivity_participate_group")); //参与的角色!!
					nextTransitionVO.setToactivityParticipate_dynamic(hvs[i].getStringValue("toactivity_participate_dynamic")); //动态参与组
					nextTransitionVO.setToactivityCanselfaddparticipate(hvs[i].getStringValue("toactivity_canselfaddparticipate"));//是否可以有添加接受人按钮
					nextTransitionVO.setToactivityIsSelfCycle(hvs[i].getBooleanValue("toactivity_isselfcycle", false)); //是否自循环!
					nextTransitionVO.setToactivitySelfCycleMap(hvs[i].getStringValue("toactivity_selfcyclerolemap")); //自循环的定义

					nextTransitionVO.setToactivityCcto_user(hvs[i].getStringValue("toactivity_ccto_user")); //目标环节上定义的抄送的人员
					nextTransitionVO.setToactivityCcto_corp(hvs[i].getStringValue("toactivity_ccto_corp")); //目标环节上定义的抄送的机构
					nextTransitionVO.setToactivityCcto_role(hvs[i].getStringValue("toactivity_ccto_role")); //目标环节上定义的抄送的角色
					nextTransitionVO.setToactivitywnparam(hvs[i].getStringValue("toactivity_extconfmap"));//万能参数
					nextTransitionVO.setPasscondition(bo_ifpasscondition); //

					nextTransitionVO.setExtConfMap(tsConfMap); //设置扩展参数！
					vector_tmp.add(nextTransitionVO); //加入
				}
			}
		}//转办其实就是这么简单
		if (vector_tmp.size() == 0) {
			if (str_fromActivity_type.equals("END")) { //
				return null;
			} else {
				System.err.println("在[" + str_fromActivity_id + "]上取下一环节时没有取到一个!(客户说:你尚有未处理完的步骤,请处理完毕再提交!)"); //
				throw new WLTAppException("找不到可以提交的接收者,有两种情况会造成这种可能:\r\n1.你当前表单上尚有未处理完的步骤导致条件不满足,请处理完毕再提交!\r\n2.根据流程设计,当前已到流程最后一步,请直接点击【结束】按钮完成终审!"); ////
			}
		}

		return (NextTransitionVO[]) vector_tmp.toArray(new NextTransitionVO[0]); //
	}

	/**
	 * 取得一个环节上的所有可能参与者的并集!!!!至关紧要!!!
	 * @param _billvo 
	 * @param _dealpool 
	 * @param  
	 * @param _fromTransition 
	 */
	private WorkFlowParticipantBean[] getOneActivityAllParticipantUsers(String _loginuserid, String _loginUserDeptID, BillVO _billvo, HashVO _hvs_dealrecord, HashMap _allAccrditAndProxyMap, String _accrdModel, DealActivityVO dealActivityVO) throws Exception {
		String _fromTransition = dealActivityVO.getFromTransitionCode(); //
		String _fromTransition_dealtype = dealActivityVO.getFromtransitiontype(); //
		String _fromactivity = dealActivityVO.getFromActivityCode(); //
		String _curractivitycode = dealActivityVO.getActivityCode(); //
		String _curractivityname = dealActivityVO.getActivityName(); //
		String _curractivityType = dealActivityVO.getActivityType(); //
		String _participan_user = dealActivityVO.getParticipate_user(); //
		String _participan_corp = dealActivityVO.getParticipate_corp(); //
		String _participan_role = dealActivityVO.getParticipate_group(); //
		String _dynamic_participan = dealActivityVO.getParticipate_dynamic(); //
		String _ccToUser = dealActivityVO.getCcto_user(); //
		String _ccToCorp = dealActivityVO.getCcto_corp(); //
		String _ccToRole = dealActivityVO.getCcto_role(); //

		ArrayList al_return = new ArrayList(); //先定义一个向量列表用于插入!!

		//如果不是启动与结束环节,并且人员,机构,角色,动态参与者,这四个属性都为空,即啥都没设,则默认是使用本人部门!!!这样结合自由添加参与者的功能,就能只能画个图,不设参与者就能走流程的效果!!!从而简化开发!
		if (!"START".equals(_curractivityType) && !"END".equals(_curractivityType) && (_participan_user == null || _participan_user.trim().equals("")) && (_participan_corp == null || _participan_corp.trim().equals("")) && (_participan_role == null || _participan_role.trim().equals("")) && (_dynamic_participan == null || _dynamic_participan.trim().equals(""))) {
			getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆没有一项参与者配置,系统默认自动使用公式[getWFCorp(\"type=登录人所在机构\",\"二次下探是否包含子孙=N\");]进行计算!<br>"); //
			WorkFlowParticipantBean parBeanByCorpAreaAndRole_receive = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), "getWFCorp(\"type=登录人所在机构\",\"二次下探是否包含子孙=N\");", null, false); //如果什么都没定义,则默认表示只查本部门!!!而且不包含子结点,否则如果这个人是分行行长,则会找出太多的人,导致性能问题!
			if (parBeanByCorpAreaAndRole_receive != null) {
				al_return.add(parBeanByCorpAreaAndRole_receive); ////
			}
		} else {
			//如果是自循环,则强行使用本人所在机构范围!
			if (_hvs_dealrecord.getBooleanValue("curractivity_isselfcycle", false) && !_hvs_dealrecord.getStringValue("curractivity_selfcyclerolemap", "").equals("") && dealActivityVO.getFromActivityId() != null && dealActivityVO.getFromActivityId().equals(dealActivityVO.getActivityId())) { //如果是自己循环
				getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆由于目标环节是自循环,且定义角色映射,且本环节与目标环节是同一个(即是内部流转,而不是第一次进入),则系统默认自动使用公式[getWFCorp(\"type=登录人所在机构的范围\",\"二次下探是否包含子孙=N\");]进行计算!<br>"); //
				WorkFlowParticipantBean parBeanByCorpAreaAndRole_receive = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), "getWFCorp(\"type=登录人所在机构的范围\",\"二次下探是否包含子孙=N\");", null, false); //如果什么都没定义,则默认表示只查本部门!!!而且不包含子结点,否则如果这个人是分行行长,则会找出太多的人,导致性能问题!
				if (parBeanByCorpAreaAndRole_receive != null) {

					//将参与人的部门与提交人的部门同步 解决工作流人员兼职问题 【杨科/2013-03-13】
					if (getTBUtil().getSysOptionBooleanValue("工作流是否处理人员兼职", false)) {
						WorkFlowParticipantUserBean[] participantUserBeans = parBeanByCorpAreaAndRole_receive.getParticipantUserBeans();
						for (int i = 0; i < participantUserBeans.length; i++) {
							if (_hvs_dealrecord.getStringValue("participant_userdept") != null && !_hvs_dealrecord.getStringValue("participant_userdept").equals(_hvs_dealrecord.getStringValue("createcorp"))) {
								participantUserBeans[i].setUserdeptid(_hvs_dealrecord.getStringValue("participant_userdept"));
								participantUserBeans[i].setUserdeptcode(_hvs_dealrecord.getStringValue("participant_userdeptname"));
								participantUserBeans[i].setUserdeptname(_hvs_dealrecord.getStringValue("participant_userdeptname"));
							} else {
								participantUserBeans[i].setUserdeptid(_hvs_dealrecord.getStringValue("createcorp"));
								participantUserBeans[i].setUserdeptcode(_hvs_dealrecord.getStringValue("createcorpname"));
								participantUserBeans[i].setUserdeptname(_hvs_dealrecord.getStringValue("createcorpname"));
							}
						}
						parBeanByCorpAreaAndRole_receive.setParticipantUserBeans(participantUserBeans);
					}

					al_return.add(parBeanByCorpAreaAndRole_receive); ////
				}
			} else { //如果不是自循环!
				if (_hvs_dealrecord.getBooleanValue("curractivity_isselfcycle", false) && !_hvs_dealrecord.getStringValue("curractivity_selfcyclerolemap", "").equals("")) {
					getInitContext().addCurrSessionCustStrInfoByKey("$环节参与者计算过程", "◆成功发现目标环节是自循环,且有角色映射,但因为本环节[" + dealActivityVO.getFromActivityId() + "/" + _fromactivity + "]与目标环节[" + dealActivityVO.getActivityId() + "/" + _curractivitycode + "]不一样,说明是第一次进入!则依然使用配置项计算而不是强行使用[登录人所在机构的范围]计算!<br>"); //
				}
				//静态参与的人...
				WorkFlowParticipantBean parBeanByUserDefine_receive = getOneActivityParticipanUserByUserDefine(_participan_user, false); //
				if (parBeanByUserDefine_receive != null) {
					al_return.add(parBeanByUserDefine_receive); ////
				}

				//处理机构与角色的交集!!! 最复杂与重要的计算!!!.★★★★★
				WorkFlowParticipantBean parBeanByCorpAreaAndRole_receive = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), _participan_corp, _participan_role, false); //
				if (parBeanByCorpAreaAndRole_receive != null) {
					if (!_hvs_dealrecord.getStringValue("parentinstanceid", "").equals("")) { //会签
						//将参与人的部门与提交人的部门同步 解决工作流人员兼职问题 【杨科/2013-035-25】
						if (getTBUtil().getSysOptionBooleanValue("工作流是否处理人员兼职", false)) {
							WorkFlowParticipantUserBean[] participantUserBeans = parBeanByCorpAreaAndRole_receive.getParticipantUserBeans();
							for (int i = 0; i < participantUserBeans.length; i++) {
								if (_hvs_dealrecord.getStringValue("participant_userdept") != null && !_hvs_dealrecord.getStringValue("participant_userdept").equals(_hvs_dealrecord.getStringValue("createcorp"))) {
									participantUserBeans[i].setUserdeptid(_hvs_dealrecord.getStringValue("participant_userdept"));
									participantUserBeans[i].setUserdeptcode(_hvs_dealrecord.getStringValue("participant_userdeptname"));
									participantUserBeans[i].setUserdeptname(_hvs_dealrecord.getStringValue("participant_userdeptname"));
								} else {
									participantUserBeans[i].setUserdeptid(_hvs_dealrecord.getStringValue("createcorp"));
									participantUserBeans[i].setUserdeptcode(_hvs_dealrecord.getStringValue("createcorpname"));
									participantUserBeans[i].setUserdeptname(_hvs_dealrecord.getStringValue("createcorpname"));
								}
							}
							parBeanByCorpAreaAndRole_receive.setParticipantUserBeans(participantUserBeans);
						}
					}
					al_return.add(parBeanByCorpAreaAndRole_receive); ////
				}

				//处理动态参与者,需要反射调用,有点麻烦!!但这个只在民生银行中用了,后来的系统其实不怎么使用了!
				if (_dynamic_participan != null && !_dynamic_participan.trim().equals("")) { //如果动态参与者不为空,按道理这里也是可以多选的!!!,即这里还将会有一个循环!!!有一个动态参与者定义,就有一个页签!
					String[] str_dynamicItems = getTBUtil().split(_dynamic_participan, ";"); //可能有多个..
					for (int k = 0; k < str_dynamicItems.length; k++) { //遍历每个动态参与者定义
						HashVO[] hvs_dynamic = getCommDMO().getHashVoArrayByDS(null, "select id,name,prefixcondition,corptype,rolename,classname from pub_wf_dypardefines where name='" + str_dynamicItems[k] + "'"); //
						if (hvs_dynamic == null || hvs_dynamic.length == 0) {//如果为空,再根据id取一次..我将动态参与者的定义改为多选参照后，数据库中保存的成了id了，而之前存的是名称...gaofeng
							hvs_dynamic = getCommDMO().getHashVoArrayByDS(null, "select id,name,prefixcondition,corptype,rolename,classname from pub_wf_dypardefines where id='" + str_dynamicItems[k] + "'"); //
						}
						if (hvs_dynamic.length == 0) {
							throw new WLTAppException("没有取得动态参与者定义[" + _dynamic_participan + "]中的[" + str_dynamicItems[k] + "]动态参与者定义,请在平台配置->工作流管理->动态参与者定义中定义之!"); //
						}

						String str_prefixcondition = hvs_dynamic[0].getStringValue("prefixcondition"); //动态参与者中定义的机构范围计算方式!!
						String str_corptype = hvs_dynamic[0].getStringValue("corptype"); //动态参与者中定义的机构类型!!
						String str_rolename = hvs_dynamic[0].getStringValue("rolename"); //动态参与者中定义的角色
						String str_dypclassname = hvs_dynamic[0].getStringValue("classname"); //

						WorkflowDynamicParticipateIfc dynamicPars = null; //
						if (str_prefixcondition.equals("全行")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_AllBankRole(str_rolename); //全行
						} else if (str_prefixcondition.equals("本机构的某种类型的上级机构")) { //最重要的计算方式,甚至以后是唯一的计算方式!即根据树型路径往上爬找到指定类型的机构下的所有机构!!
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_TreePathCorpChooseRole(str_dynamicItems[k], _loginUserDeptID, str_corptype, str_rolename); //
						} else if (str_prefixcondition.equals("本部门")) { //
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameDeptChooseRole(_loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("本分行部门")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_fenghbm", "本分行部门", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("本分行")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_fengh", "本分行", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("本事业部分部")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_shiybfb", "本事业部分部", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("本事业部")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_shiyb", "本事业部", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("本支行")) { //
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_zhih", "本支行", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("本总行部门")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_zhonghbm", "本总行部门", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("本分行部门/支行/事业部分部")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_BatchDeptChooseRole(new String[] { "bl_fenghbm", "bl_zhih", "bl_shiybfb" }, "本分行部门/支行/事业部分部", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("报送机构分行部门/支行/事业部分部")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_BatchDeptChooseRole(new String[] { "bl_fenghbm", "bl_zhih", "bl_shiybfb" }, "报送机构分行部门/支行/事业部分部", _billvo.getStringValue("create_deptid"), str_rolename); //
						} else if (str_prefixcondition.equals("本分行部门/支行/事业部分部/总行部门")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_BatchDeptChooseRole(new String[] { "bl_fenghbm", "bl_zhih", "bl_shiybfb", "bl_zhonghbm" }, "本分行部门/支行/事业部分部/总行部门", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("总行")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_HeadBankRole(_loginUserDeptID, str_rolename); //总行
						} else if (str_prefixcondition.equals("自定义类")) {
							if (str_dypclassname.indexOf("(") > 0) { //如果有参数则调用对应参数的构造方法!!!
								TBUtil tbutil = new TBUtil(); //
								String str_csname = str_dypclassname.substring(0, str_dypclassname.indexOf("(")); //
								String str_par = str_dypclassname.substring(str_dypclassname.indexOf("(") + 1, str_dypclassname.indexOf(")")); //
								str_par = tbutil.replaceAll(str_par, "\"", ""); //把双引号去掉!!!
								String[] str_pars = tbutil.split(str_par, ","); //以逗号为分割除,创建数组
								Class cp[] = new Class[str_pars.length];
								for (int i = 0; i < cp.length; i++) {
									cp[i] = java.lang.String.class;
								}

								Class panelclass = Class.forName(str_csname); //
								Constructor constructor = panelclass.getConstructor(cp); //
								dynamicPars = (WorkflowDynamicParticipateIfc) constructor.newInstance(str_pars); ////..
							} else {
								dynamicPars = (WorkflowDynamicParticipateIfc) Class.forName(str_dypclassname).newInstance(); //
							}
						} else {
							throw new WLTAppException("在动态参与者定义[" + _dynamic_participan + "]中发现未知的类型[" + str_prefixcondition + "]!"); //
						}
						_hvs_dealrecord.setAttributeValue("DyRole", str_rolename);
						WorkFlowParticipantBean parBean = dynamicPars.getDynamicParUsers(_loginuserid, _billvo, _hvs_dealrecord, _fromTransition, _fromTransition_dealtype, _fromactivity, _curractivitycode, _curractivityname); //
						if (parBean != null) {
							al_return.add(parBean); //如果不为空则加入!
						}
					}
				}
			}
		}

		//如果定义了抄送人员
		WorkFlowParticipantBean parBeanByUserDefine_ccTo = getOneActivityParticipanUserByUserDefine(_ccToUser, true); //
		if (parBeanByUserDefine_ccTo != null) {
			al_return.add(parBeanByUserDefine_ccTo); ////
		}

		//根据抄送机构与抄送角色的定义,计算抄送的人!!
		WorkFlowParticipantBean parBeanByCorpAreaAndRole_ccTo = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), _ccToCorp, _ccToRole, true); //
		if (parBeanByCorpAreaAndRole_ccTo != null) {
			al_return.add(parBeanByCorpAreaAndRole_ccTo); ////
		}

		WorkFlowParticipantBean[] allParsbeans = (WorkFlowParticipantBean[]) al_return.toArray(new WorkFlowParticipantBean[0]); //
		//处理授权,即如果返回的人员中,有做了授权的,则要显示代理人的信息!!!
		if (_allAccrditAndProxyMap.size() > 0) { //如果定义了授权信息!!
			for (int i = 0; i < allParsbeans.length; i++) { //处理所有授权的信息!!!换句话说就是在流程配置中参与者还是各设各的,授权代理机制是单独存在的!!
				WorkFlowParticipantUserBean[] parUserBeans = allParsbeans[i].getParticipantUserBeans(); //所有参与的人员!!!
				if (parUserBeans != null) { //如果不为空!!!
					ArrayList al_temp = new ArrayList(); //
					for (int j = 0; j < parUserBeans.length; j++) { //遍历每一个人
						al_temp.add(parUserBeans[j]); //先将原来的人员加入!即新的机制不是覆盖机制了!!而是新增机制了!!
						String str_olduserid = parUserBeans[j].getUserid(); //原来计算出的参与者,
						if (_allAccrditAndProxyMap.containsKey(str_olduserid)) { //如果发现该用户做了授权处理,即他是授权给了其他人!!!!则要做替换处理!!
							HashVO hvo_accr = getWFBSUtil().getAccredProxyUserVO(_allAccrditAndProxyMap, _accrdModel, str_olduserid); ////最关键的计算!!!!取得这个人已授权给另一个人的情况!!!
							if (hvo_accr != null) {
								WorkFlowParticipantUserBean cloneProxyUserBean = parUserBeans[j].clone(); //克隆一个!该类的克隆方法已重构了!

								//发生的授权时一般有两种表达方式:一种是XX领导(授权XX秘书)；另一种是：XX秘书(XX领导授权),以后这里应该有个参数,即不同的客户喜欢不同的表达方式!!★★★★
								String str_username_1 = parUserBeans[j].getUsername() + "(授权" + hvo_accr.getStringValue("proxyusername") + ")"; //XX领导(授权XX秘书)
								String str_username_2 = hvo_accr.getStringValue("proxyusername") + "(" + parUserBeans[j].getUsername() + "授权)"; //XX秘书(XX领导授权)
								cloneProxyUserBean.setUsername(str_username_1); //参与者(代理人)的名称要体现授权思想!!!即两个人的名称要拼在一起!!

								cloneProxyUserBean.setAccrUserid(hvo_accr.getStringValue("proxyuserid")); //被授予的人就是代理人!
								cloneProxyUserBean.setAccrUsercode(hvo_accr.getStringValue("proxyusercode")); //被授予的人就是代理人!
								cloneProxyUserBean.setAccrUsername(hvo_accr.getStringValue("proxyusername")); //被授予的人就是代理人!
								al_temp.add(cloneProxyUserBean); //加入克隆的代理人信息!
							} else {
								System.out.println("没有找到授权模块!"); //
							}
						}
					}

					WorkFlowParticipantUserBean[] newParUserBeans = (WorkFlowParticipantUserBean[]) al_temp.toArray(new WorkFlowParticipantUserBean[0]); //加上授权人以后的信息!
					allParsbeans[i].setParticipantUserBeans(newParUserBeans); //重置一下!!!!
				}
			}
		}

		return allParsbeans; //
	}

	/**
	 * 直接根据人员定义计算参与者!
	 * @param _userDefine
	 * @param _isCCTo
	 * @return
	 * @throws Exception
	 */
	private WorkFlowParticipantBean getOneActivityParticipanUserByUserDefine(String _userDefine, boolean _isCCTo) throws Exception {
		if (_userDefine == null || _userDefine.trim().equals("")) {
			return null;
		}

		WorkFlowParticipantBean parBean_static_user = new WorkFlowParticipantBean(); //
		parBean_static_user.setParticipantDeptType(null); //静态角色时部门设什么?
		parBean_static_user.setParticipantDeptId(null); //静态角色时部门设什么?

		String[] str_items = getTBUtil().split(_userDefine, ";"); //
		HashVO[] hvs_user_role_corp = getCommDMO().getHashVoArrayByDS(null, "select id userid,code usercode,name username from pub_user where id in (" + getTBUtil().getInCondition(str_items) + ") and (status is null or status <> 'D')"); //真正的去查询,这会产生多条人员记录,需要做唯一性合并!!即相同的人员,要做唯一性合并!!!!
		if (hvs_user_role_corp == null || hvs_user_role_corp.length == 0) { //如果没找到一个参与者,则直接返回空算了!!!
			return null;
		}

		HashVO[] hvs_realUser = appendMyCorpAndRoleInfo(hvs_user_role_corp); //合并角色与机构!!!!即将一个人的所有角色与机构列出来!!
		parBean_static_user.setParticiptMsg("静态参与人定义[" + _userDefine + "],共找到[" + hvs_realUser.length + "]人!"); //
		if (hvs_realUser.length > 0) {
			WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs_realUser.length]; //
			for (int i = 0; i < hvs_realUser.length; i++) {
				userBeans[i] = new WorkFlowParticipantUserBean(); //
				userBeans[i].setUserid(hvs_realUser[i].getStringValue("id")); //参与者主键.
				userBeans[i].setUsercode(hvs_realUser[i].getStringValue("code")); //参与者编码.
				userBeans[i].setUsername(hvs_realUser[i].getStringValue("name")); //参与者名称.

				userBeans[i].setUserdeptid(hvs_realUser[i].getStringValue("userdept")); //机构id
				userBeans[i].setUserdeptcode(hvs_realUser[i].getStringValue("userdeptname")); //机构编码
				userBeans[i].setUserdeptname(hvs_realUser[i].getStringValue("userdeptname")); //机构名称!!

				userBeans[i].setUserroleid(hvs_realUser[i].getStringValue("roleid")); //参与者角色.
				userBeans[i].setUserrolecode(hvs_realUser[i].getStringValue("rolename")); //参与者角色编码.
				userBeans[i].setUserrolename(hvs_realUser[i].getStringValue("rolename")); //参与者角色名称.

				userBeans[i].setParticipantType("静态定义的人"); //
				userBeans[i].setSuccessParticipantReason("满足静态参与人的条件"); //
				userBeans[i].setCCTo(_isCCTo); //
			}
			parBean_static_user.setParticipantUserBeans(userBeans); //
		}

		return parBean_static_user;
	}

	/**
	 * 根据机构与角色计算出所有参与者,这将是将来工作流中计算参与者的唯一的最核心的方法! 包括提交与抄送!!!
	 * 核心原理是取【机构范围+角色】过滤的交集,其中机构范围是支持万能的公式处理,而角色是采用多选参照,以后角色也可以考虑采用公式,比如可以指定角色编码模糊品配,等
	 * @param _roleDefine
	 * @param _corpDefine
	 * @param _isCCTo
	 * @return
	 * @throws Exception
	 */
	private WorkFlowParticipantBean getOneActivityParticipanUserByCorpAreaAndRole(String _wfCreater, String _corpDefine, String _roleDefine, boolean _isCCTo) throws Exception {
		if ((_roleDefine == null || _roleDefine.trim().equals("")) && (_corpDefine == null || _corpDefine.trim().equals(""))) { //如果两者都为空,则直接返回空
			return null;
		}
		StringBuilder sb_msgInfo = new StringBuilder("机构与角色交集过滤,机构定义[" + _corpDefine + "],角色定义[" + _roleDefine + "]"); //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		parBean.setParticipantDeptType(null); //静态角色时部门设什么?
		parBean.setParticipantDeptId(null); //静态角色时部门设什么?

		String[] str_roleIds = null; //所有的角色
		String str_roleInCondition = null; //角色的In条件..
		if (_roleDefine != null) {
			str_roleIds = getTBUtil().split(_roleDefine, ";"); //角色角色的Ids
			str_roleInCondition = getTBUtil().getInCondition(str_roleIds); //
		}

		String[] str_corpIds = null; //
		String str_inCorpCondition = null; //
		if (_corpDefine != null && !_corpDefine.trim().equals("")) { //如果定义了公式
			HashMap[] formulaMaps = getWFParse(_corpDefine); //根据机构公式得到实际的机构!!
			if (formulaMaps != null && formulaMaps.length > 0) { //如果取得了值!!
				for (int i = 0; i < formulaMaps.length; i++) { //
					formulaMaps[i].put("流程创建者", _wfCreater); //在每个参数中强行加入"流程创建者"!!
				}
				str_corpIds = new WorkFlowEngineGetCorpUtil().getCorps(formulaMaps, getAllCorpsCacheMap()); //得到所有的机构!!!这是最想关键的算法!!! 即如果算出机构都在这里!!!★★★★
				if (str_corpIds == null || str_corpIds.length == 0) { //如果没找到!!!增加判断为null的情况【李春娟/2016-12-18】
					sb_msgInfo.append(",但没找到一个机构!"); //
					parBean.setParticiptMsg(sb_msgInfo.toString()); ////
					return parBean; //返回!!!
				}
			}
			str_inCorpCondition = getCommDMO().getSubSQLFromTempSQLTableByIDs(str_corpIds); //取得机构的in条件!!!
		}

		//最关键的SQL,这个SQL之所有没有关联角色基本表与机构基本表,是为了提高性能,而且按道理它出来的结果集已经很小了,所以在下面Java逻辑中处理名称!!!
		StringBuffer sb_sql = new StringBuffer();
		String userCondition = getTBUtil().getSysOptionStringValue("工作流人员状态过滤条件", "status <> 'D'");//太平集团有效人员状态为0，故增加参数【李春娟/2017-10-19】
		if (!(_roleDefine == null || _roleDefine.trim().equals("")) && !(_corpDefine == null || _corpDefine.trim().equals(""))) { //如果同时有机构与角色定义,则取两者的交集!!
			sb_sql.append("select ");
			sb_sql.append("t1.userid, ");
			sb_sql.append("t3.code usercode, ");
			sb_sql.append("t3.name username ");
			sb_sql.append("from pub_user_post t1,pub_user_role t2,pub_user t3 "); //
			sb_sql.append("where t1.userid=t2.userid  ");
			sb_sql.append("and t1.userid=t3.id ");
			sb_sql.append("and t1.userdept in (" + str_inCorpCondition + ")"); //机构必须在指定的范围内!
			sb_sql.append("and (t3.status is null or t3." + userCondition + ") "); //状态不等于D
			sb_sql.append("and t2.roleid   in (" + str_roleInCondition + ") "); //角色id
		} else { //如果只有机构或只有角色定义!!! 
			if (_roleDefine != null && !_roleDefine.trim().equals("")) { //如果只有角色,则只要直接拿角色关联!!
				sb_sql.append("select ");
				sb_sql.append("t1.userid, ");
				sb_sql.append("t2.code usercode, ");
				sb_sql.append("t2.name username ");
				sb_sql.append("from pub_user_role t1,pub_user t2 ");
				sb_sql.append("where t1.userid=t2.id ");
				sb_sql.append("and t1.roleid in (" + str_roleInCondition + ") "); //
				sb_sql.append("and (t2.status is null or t2." + userCondition + ") "); //状态不等于D
			} else { //如果是只有机构定义
				sb_sql.append("select ");
				sb_sql.append("t1.userid, ");
				sb_sql.append("t2.code usercode, ");
				sb_sql.append("t2.name username ");
				sb_sql.append("from pub_user_post t1,pub_user t2 ");
				sb_sql.append("where t1.userid=t2.id ");
				sb_sql.append("and t1.userdept in (" + str_inCorpCondition + ") ");
				sb_sql.append("and (t2.status is null or t2." + userCondition + ") "); //状态不等于D
			}
		}
		WorkFlowParticipantUserBean[] userBeans = getParticipanUserBeansBySQL(sb_sql.toString(), _isCCTo, "机构与角色交集过滤", "满足机构与角色交集过滤"); //
		if (userBeans == null) {
			return null;
		}
		parBean.setParticipantUserBeans(userBeans); ////

		sb_msgInfo.append(",共找到[" + userBeans.length + "]人!"); //
		parBean.setParticiptMsg(sb_msgInfo.toString()); ////
		return parBean; //返回!!!
	}

	//根据查询人员的SQL计算流程参与者对象WorkFlowParticipantUserBean,这里面最主要的逻辑是补上了人员的所有角色与机构的信息!!!
	private WorkFlowParticipantUserBean[] getParticipanUserBeansBySQL(String sb_sql, boolean _isCCTo, String _parType, String _successReason) throws Exception {
		HashVO[] hvsUsers = getCommDMO().getHashVoArrayByDS(null, sb_sql); //真正的去查询,这会产生多条人员记录,需要做唯一性合并!!即相同的人员,要做唯一性合并!!!!
		if (hvsUsers == null || hvsUsers.length == 0) { //如果没找到一个参与者,则直接返回空算了!!!
			return null;
		}
		HashVO[] hvs_realUser = appendMyCorpAndRoleInfo(hvsUsers); //补上机构与角色!!与以前的机构有所区别!!! 非常重要的逻辑!!! 现在机构只算了default=Y的,应该两个都要有!!
		WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs_realUser.length]; //
		for (int i = 0; i < hvs_realUser.length; i++) { //遍历实际人员!!!
			userBeans[i] = new WorkFlowParticipantUserBean(); //
			userBeans[i].setUserid(hvs_realUser[i].getStringValue("id")); //参与者主键.
			userBeans[i].setUsercode(hvs_realUser[i].getStringValue("code")); //参与者编码.
			userBeans[i].setUsername(hvs_realUser[i].getStringValue("name")); //参与者名称.

			userBeans[i].setUserdeptid(hvs_realUser[i].getStringValue("userdept")); //机构id
			userBeans[i].setUserdeptcode(hvs_realUser[i].getStringValue("userdeptcode")); //机构编码
			userBeans[i].setUserdeptname(hvs_realUser[i].getStringValue("userdeptname")); //机构名称!!

			userBeans[i].setUserroleid(hvs_realUser[i].getStringValue("roleid")); //参与者角色.
			userBeans[i].setUserrolecode(hvs_realUser[i].getStringValue("rolecode")); //参与者角色编码.
			userBeans[i].setUserrolename(hvs_realUser[i].getStringValue("rolename")); //参与者角色名称.

			userBeans[i].setParticipantType(_parType); //
			userBeans[i].setSuccessParticipantReason(_successReason); //
			userBeans[i].setCCTo(_isCCTo); //
		}
		return userBeans; //
	}

	//因为在计算公式时随时遇到机构计算!为了提高性能,在这时就计算好,然后传进去!!!
	private HashMap getAllCorpsCacheMap() throws Exception {
		if (this.allCorpsCacheMap != null) {
			return allCorpsCacheMap; //
		}
		allCorpsCacheMap = wfBSUtil.createAllCorpsCacheMap(); //创建!!!
		return allCorpsCacheMap; //
	}

	/**
	 * 对人员补充上机构与角色信息,以前的方法有问题,即不能列出某个人员的所有机构与角色!
	 * 这个方法非常重要!
	 * @param _hvsUsers
	 * @return
	 * @throws Exception
	 */
	private HashVO[] appendMyCorpAndRoleInfo(HashVO[] _hvsUsers) throws Exception {
		LinkedHashMap lnkMap = new LinkedHashMap(); //
		for (int i = 0; i < _hvsUsers.length; i++) { ////
			String str_userid = _hvsUsers[i].getStringValue("userid"); //
			if (!lnkMap.containsKey(str_userid)) { //如果不包含该用户,即相同的只存一个!!
				HashVO hvoitem = new HashVO(); //
				hvoitem.setAttributeValue("id", str_userid); //
				hvoitem.setAttributeValue("code", _hvsUsers[i].getStringValue("usercode")); //
				hvoitem.setAttributeValue("name", _hvsUsers[i].getStringValue("username")); //
				lnkMap.put(str_userid, hvoitem); //
			}
		}
		String[] str_allUserIds = (String[]) lnkMap.keySet().toArray(new String[0]); //所有的用户id

		//补上人的所有机构,以后还要扩展找出机构的名称路径,现在只能显示名称
		StringBuilder sb_sql_myAllCorp = new StringBuilder(); //
		sb_sql_myAllCorp.append("select ");
		sb_sql_myAllCorp.append("t1.userid, ");
		sb_sql_myAllCorp.append("t1.userdept, ");
		sb_sql_myAllCorp.append("t2.code userdeptcode, ");
		sb_sql_myAllCorp.append("t2.name userdeptname, ");
		sb_sql_myAllCorp.append("t1.isdefault ");
		sb_sql_myAllCorp.append("from pub_user_post t1,pub_corp_dept t2 ");
		sb_sql_myAllCorp.append("where t1.userdept=t2.id ");
		sb_sql_myAllCorp.append("and t1.userid in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_allUserIds) + ") "); //条件
		sb_sql_myAllCorp.append("order by t1.userid,t1.isdefault "); //排序

		HashVO[] hvs_myAllCorps = getCommDMO().getHashVoArrayByDS(null, sb_sql_myAllCorp.toString()); //找出满足条件的所有人的所在机构

		//在这里找出机构缓存,然后找出这些机构的路径名称,然后加入!!!!
		for (int i = 0; i < hvs_myAllCorps.length; i++) {
			HashVO hvoitem = (HashVO) lnkMap.get(hvs_myAllCorps[i].getStringValue("userid")); //旧的数据
			if ("Y".equals(hvs_myAllCorps[i].getStringValue("isdefault")) || hvoitem.getStringValue("userdept") == null) { //如果我是默认机构,或者没有旧的
				hvoitem.setAttributeValue("userdept", hvs_myAllCorps[i].getStringValue("userdept")); //送入机构id...
			}
			hvoitem.setAttributeValue("userdeptcode", hvoitem.getStringValue("userdeptcode", "") + hvs_myAllCorps[i].getStringValue("userdeptcode") + ";"); //机构编码
			hvoitem.setAttributeValue("userdeptname", hvoitem.getStringValue("userdeptname", "") + hvs_myAllCorps[i].getStringValue("userdeptname") + ";"); //机构名称
		}

		//补上人的所有角色
		StringBuilder sb_sql_myAllRole = new StringBuilder(); //
		sb_sql_myAllRole.append("select ");
		sb_sql_myAllRole.append("t1.userid, ");
		sb_sql_myAllRole.append("t1.roleid, ");
		sb_sql_myAllRole.append("t2.code rolecode, ");
		sb_sql_myAllRole.append("t2.name rolename ");
		sb_sql_myAllRole.append("from pub_user_role t1,pub_role t2 ");
		sb_sql_myAllRole.append("where t1.roleid=t2.id ");
		sb_sql_myAllRole.append("and t1.userid in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_allUserIds) + ") "); //
		sb_sql_myAllRole.append("order by t2.code ");
		HashVO[] hvs_myAllRoles = getCommDMO().getHashVoArrayByDS(null, sb_sql_myAllRole.toString()); //
		for (int i = 0; i < hvs_myAllRoles.length; i++) {
			HashVO hvoitem = (HashVO) lnkMap.get(hvs_myAllRoles[i].getStringValue("userid")); //旧的数据
			if (hvoitem.getStringValue("roleid") == null) {
				hvoitem.setAttributeValue("roleid", ";"); //
			}
			hvoitem.setAttributeValue("roleid", hvoitem.getStringValue("roleid") + hvs_myAllRoles[i].getStringValue("roleid") + ";"); //角色id
			hvoitem.setAttributeValue("rolecode", hvoitem.getStringValue("rolecode", "") + hvs_myAllRoles[i].getStringValue("rolecode") + ";"); //角色编码
			hvoitem.setAttributeValue("rolename", hvoitem.getStringValue("rolename", "") + hvs_myAllRoles[i].getStringValue("rolename") + ";"); //角色名称

		}
		HashVO[] hvs_realUser = (HashVO[]) lnkMap.values().toArray(new HashVO[0]); //实际的所有用户清单,下面需要对它进行补充!!	
		getWFBSUtil().appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvs_realUser, "userdept", "userdeptname"); //补上机构名称的全称!!! 即机构的全路径名,其实按道理要截掉下属机构的末结点!!
		return hvs_realUser; //
	}

	/**
	 * 取得工作流中机构定义公式的解析!!!比如下面格式:
	 * getWFCorp(\"type=本机构\");getWFCorp(\"type=本机构范围\","是否包括子结点=Y");getWFCorp(\"type=本人某类型上级机构\",\"类型条件=分行部门=>分行;支行=>分行;总行部门=>总行\");
	 * @param _formula
	 * @return 一个getWFCorp()将返回一个HashMap,有几个就返回几个,每个哈希表中的key与value就是一个字符串中以"="分隔的前后两部分!!之所以这样设计,是为了可扩展性,即当增加新的参数时好扩展,API的参数类型也只有一个HashMap,不会因为函数参数变化而大改代码!!
	 * 可扩展性已成为一个非常重要的问题,包括平台所有控件,组件,都有这样一个问题,按道理都应该使用这种key=value,的字符串定义,然后拼成一个HashMap，作为参数送入API函数中去,这样才能"万能"的可扩展!!!!
	 * 根据公式最后计算出来的应该是一个机构id的一维数组!!!然后就是机构与角色进行交集处理!!!
	 */
	private HashMap[] getWFParse(String _formulaPar) {
		try {
			String str_formula = _formulaPar; //
			str_formula = str_formula.trim(); //
			str_formula = getTBUtil().replaceAll(str_formula, " ", ""); //去掉空格
			str_formula = getTBUtil().replaceAll(str_formula, "\r", ""); //替换换行
			str_formula = getTBUtil().replaceAll(str_formula, "\n", ""); //替换换行
			if (!str_formula.endsWith(";")) { //如果不是以分号结尾,则补上分号,否则后面截取两位时会报错!
				str_formula = str_formula + ";"; //
			}
			//System.out.println("最终的公式=[" + str_formula + "]"); //
			String[] str_items = getTBUtil().split(str_formula, "getWFCorp("); //
			HashMap[] maps = new HashMap[str_items.length]; //
			for (int i = 0; i < str_items.length; i++) {
				maps[i] = new HashMap(); //
				String str_item = str_items[i].substring(0, str_items[i].length() - 2); //去掉最后的[);]
				String[] str_keyvalue = getTBUtil().split(str_item, ","); //再解析!!
				for (int j = 0; j < str_keyvalue.length; j++) { //每一项,即"aaa=bbbb"
					str_keyvalue[j] = str_keyvalue[j].substring(1, str_keyvalue[j].length() - 1); //去掉前后的双引号
					int li_pos = str_keyvalue[j].indexOf("="); //
					String str_key = str_keyvalue[j].substring(0, li_pos); //
					String str_value = str_keyvalue[j].substring(li_pos + 1, str_keyvalue[j].length()); //
					maps[i].put(str_key, str_value); //
				}
			}
			return maps; ////
		} catch (Exception ex) { //以前遇到过因为公式定义有误,导致substring时数组溢出报错,让人不明所以,必须包装一下,这样实施与使用人员才能最快的找到原因!!
			//系统中应该到处有这种需求,即对系统异常包装成可用于快速分析原因的提示异常! 把产生这种原因的各种可能直接提示!!
			ex.printStackTrace(); //
			throw new WLTAppException("解析公式[" + _formulaPar + "],发生异常[" + ex.getMessage() + "]!\r\n很可能是因为少了个等号造成的,请至服务器端控制台查看详细!"); ////
		}
	}

	/**
	 * 取得某个用户的邮件地址
	 * @param _userid
	 * @return
	 */
	private String getUserMailAddr(String _userid) throws Exception {
		String str_sql = "select email from pub_user where id='" + _userid + "'"; //
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, str_sql); //
		if (hvs == null || hvs.length == 0) {
			return null;
		} else {
			return hvs[0].getStringValue("email"); //
		}
	}

	private CommDMO getCommDMO() {
		if (commDMO == null) {
			commDMO = new CommDMO();
		}
		return commDMO;
	}

	private TBUtil getTBUtil() {
		if (tBUtil == null) {
			tBUtil = new TBUtil();
		}
		return tBUtil;
	}

	private WorkFlowBSUtil getWFBSUtil() {
		if (wfBSUtil != null) {
			return wfBSUtil; //
		}
		wfBSUtil = new WorkFlowBSUtil(); //
		return wfBSUtil; //
	}

	/**
	 * 
	 * 复制流程
	 */

	public void CopyFlow(HashVO _hvs, String old_flowid, int type) throws Exception {
		ArrayList al_sql = new ArrayList();
		// 环节
		HashVO[] hvo_activity = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_activity where processid=" + old_flowid + "");
		//   老的环节
		String[] str_old = new String[hvo_activity.length];
		//   新的环节
		String[] str_new = new String[hvo_activity.length];
		String activityid = "";
		for (int i = 0; i < hvo_activity.length; i++) {
			str_old[i] = hvo_activity[i].getStringValue("id");
			InsertSQLBuilder iSB = new InsertSQLBuilder("pub_wf_activity");
			activityid = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
			str_new[i] = activityid;
			iSB.putFieldValue("id", "" + activityid + ""); //id
			iSB.putFieldValue("processid", "" + _hvs.getStringValue("flowid") + ""); //processid
			iSB.putFieldValue("code", "" + hvo_activity[i].getStringValue("code") + ""); //code
			iSB.putFieldValue("wfname", "" + hvo_activity[i].getStringValue("wfname") + ""); //wfname
			iSB.putFieldValue("uiname", "" + hvo_activity[i].getStringValue("uiname") + ""); //uiname
			iSB.putFieldValue("x", "" + hvo_activity[i].getStringValue("x") + ""); //x
			iSB.putFieldValue("y", "" + hvo_activity[i].getStringValue("y") + ""); //y
			iSB.putFieldValue("autocommit", "" + hvo_activity[i].getStringValue("autocommit") + ""); //autocommit
			iSB.putFieldValue("isassignapprover", "" + hvo_activity[i].getStringValue("isassignapprover") + ""); //isassignapprover
			iSB.putFieldValue("approvemodel", "" + hvo_activity[i].getStringValue("approvemodel") + ""); //approvemodel
			iSB.putFieldValue("approvenumber", "" + hvo_activity[i].getStringValue("approvenumber") + ""); //approvenumber
			iSB.putFieldValue("participate_user", "" + hvo_activity[i].getStringValue("participate_user") + ""); //participate_user
			iSB.putFieldValue("participate_group", "" + hvo_activity[i].getStringValue("participate_group") + ""); //participate_group
			iSB.putFieldValue("participate_dynamic", "" + hvo_activity[i].getStringValue("participate_dynamic") + ""); //participate_dynamic
			iSB.putFieldValue("messageformat", "" + hvo_activity[i].getStringValue("messageformat") + ""); //messageformat
			iSB.putFieldValue("messagereceiver", "" + hvo_activity[i].getStringValue("messagereceiver") + ""); //messagereceiver
			iSB.putFieldValue("descr", "" + hvo_activity[i].getStringValue("descr") + ""); //descr
			iSB.putFieldValue("activitytype", "" + hvo_activity[i].getStringValue("activitytype") + ""); //activitytype
			iSB.putFieldValue("iscanback", "" + hvo_activity[i].getStringValue("iscanback") + ""); //iscanback
			iSB.putFieldValue("isneedmsg", "" + hvo_activity[i].getStringValue("isneedmsg") + ""); //isneedmsg
			iSB.putFieldValue("intercept1", "" + hvo_activity[i].getStringValue("intercept1") + ""); //intercept1
			iSB.putFieldValue("intercept2", "" + hvo_activity[i].getStringValue("intercept2") + ""); //intercept2
			iSB.putFieldValue("checkuserpanel", "" + hvo_activity[i].getStringValue("checkuserpanel") + ""); //checkuserpanel
			iSB.putFieldValue("width", "" + hvo_activity[i].getStringValue("width") + ""); //width
			iSB.putFieldValue("height", "" + hvo_activity[i].getStringValue("height") + ""); //height
			iSB.putFieldValue("viewtype", "" + hvo_activity[i].getStringValue("viewtype") + ""); //viewtype
			iSB.putFieldValue("belongdeptgroup", "" + hvo_activity[i].getStringValue("belongdeptgroup") + ""); //belongdeptgroup
			iSB.putFieldValue("belongstationgroup", "" + hvo_activity[i].getStringValue("belongstationgroup") + ""); //belongstationgroup
			iSB.putFieldValue("canhalfstart", "" + hvo_activity[i].getStringValue("canhalfstart") + ""); //canhalfstart
			iSB.putFieldValue("halfstartrole", "" + hvo_activity[i].getStringValue("halfstartrole") + ""); //halfstartrole
			iSB.putFieldValue("canhalfend", "" + hvo_activity[i].getStringValue("canhalfend") + ""); //canhalfend
			iSB.putFieldValue("canselfaddparticipate", "" + hvo_activity[i].getStringValue("canselfaddparticipate") + ""); //canselfaddparticipate
			iSB.putFieldValue("showparticimode", "" + hvo_activity[i].getStringValue("showparticimode") + ""); //showparticimode
			iSB.putFieldValue("isneedreport", "" + hvo_activity[i].getStringValue("isneedreport") + ""); //isneedreport
			iSB.putFieldValue("pushuser", ""); //pushuser
			iSB.putFieldValue("fonttype", "" + hvo_activity[i].getStringValue("fonttype") + ""); //fonttype
			iSB.putFieldValue("fontsize", "" + hvo_activity[i].getStringValue("fontsize") + ""); //fontsize
			iSB.putFieldValue("foreground", "" + hvo_activity[i].getStringValue("foreground") + ""); //foreground
			iSB.putFieldValue("background", "" + hvo_activity[i].getStringValue("background") + ""); //background
			al_sql.add(iSB.getSQL());
		}
		//  部门分组
		HashVO[] hvo_group = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_group where processid=" + old_flowid + "");
		String groupid = "";
		for (int i = 0; i < hvo_group.length; i++) {
			InsertSQLBuilder iSB = new InsertSQLBuilder("pub_wf_group");
			groupid = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
			iSB.putFieldValue("id", "" + groupid + ""); //id
			iSB.putFieldValue("processid", "" + _hvs.getStringValue("flowid") + ""); //processid
			iSB.putFieldValue("grouptype", "" + hvo_group[i].getStringValue("grouptype") + ""); //grouptype
			iSB.putFieldValue("code", "" + hvo_group[i].getStringValue("code") + ""); //code
			iSB.putFieldValue("wfname", "" + hvo_group[i].getStringValue("wfname") + ""); //wfname
			iSB.putFieldValue("uiname", "" + hvo_group[i].getStringValue("uiname") + ""); //uiname
			iSB.putFieldValue("x", "" + hvo_group[i].getStringValue("x") + ""); //x
			iSB.putFieldValue("y", "" + hvo_group[i].getStringValue("y") + ""); //y
			iSB.putFieldValue("width", "" + hvo_group[i].getStringValue("width") + ""); //width
			iSB.putFieldValue("height", "" + hvo_group[i].getStringValue("height") + ""); //height
			iSB.putFieldValue("descr", "" + hvo_group[i].getStringValue("descr") + ""); //descr
			//iSB.putFieldValue("pushuser",""+hvo_group[i].getStringValue("pushuser")+""); //pushuser
			iSB.putFieldValue("fonttype", "" + hvo_group[i].getStringValue("fonttype") + ""); //fonttype
			iSB.putFieldValue("fontsize", "" + hvo_group[i].getStringValue("fontsize") + ""); //fontsize
			iSB.putFieldValue("foreground", "" + hvo_group[i].getStringValue("foreground") + ""); //foreground
			iSB.putFieldValue("background", "" + hvo_group[i].getStringValue("background") + ""); //background
			al_sql.add(iSB.getSQL());
		}
		// 查找新的环节
		HashVO[] new_activity = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_activity where processid=" + _hvs.getStringValue("flowid") + "");
		ArrayList<String> al_Sql2 = null;
		if (type == 1) {
			//  线
			HashVO[] hvo_transition = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_transition where processid=" + old_flowid + "");
			String transitionid = "";
			for (int i = 0; i < hvo_transition.length; i++) {
				InsertSQLBuilder iSB = new InsertSQLBuilder("pub_wf_transition");
				transitionid = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_transition");
				iSB.putFieldValue("id", "" + transitionid + ""); //id
				iSB.putFieldValue("processid", "" + _hvs.getStringValue("flowid") + ""); //processid
				iSB.putFieldValue("code", "" + hvo_transition[i].getStringValue("code") + ""); //code
				iSB.putFieldValue("wfname", "" + hvo_transition[i].getStringValue("wfname") + ""); //wfname
				iSB.putFieldValue("uiname", "" + hvo_transition[i].getStringValue("uiname") + ""); //uiname
				iSB.putFieldValue("fromactivity", "" + hvo_transition[i].getStringValue("fromactivity") + ""); //fromactivity
				iSB.putFieldValue("toactivity", "" + hvo_transition[i].getStringValue("toactivity") + ""); //toactivity
				iSB.putFieldValue("conditions", "" + hvo_transition[i].getStringValue("conditions") + ""); //conditions
				iSB.putFieldValue("points", "" + hvo_transition[i].getStringValue("points") + ""); //points
				iSB.putFieldValue("dealtype", "" + hvo_transition[i].getStringValue("dealtype") + ""); //dealtype
				iSB.putFieldValue("reasoncodesql", "" + hvo_transition[i].getStringValue("reasoncodesql") + ""); //reasoncodesql
				iSB.putFieldValue("mailsubject", "" + hvo_transition[i].getStringValue("mailsubject") + ""); //mailsubject
				iSB.putFieldValue("mailcontent", "" + hvo_transition[i].getStringValue("mailcontent") + ""); //mailcontent
				iSB.putFieldValue("intercept", "" + hvo_transition[i].getStringValue("intercept") + ""); //intercept
				iSB.putFieldValue("pushuser", ""); //pushuser
				iSB.putFieldValue("fonttype", "" + hvo_transition[i].getStringValue("fonttype") + ""); //fonttype
				iSB.putFieldValue("fontsize", "" + hvo_transition[i].getStringValue("fontsize") + ""); //fontsize
				iSB.putFieldValue("foreground", "" + hvo_transition[i].getStringValue("foreground") + ""); //foreground
				iSB.putFieldValue("background", "" + hvo_transition[i].getStringValue("background") + ""); //background				
				al_sql.add(iSB.getSQL());
			}
			// 修改线性表中的环节主键			
			al_Sql2 = new ArrayList<String>();
			for (int i = 0; i < str_old.length; i++) {
				//	fromactivity  //	toactivity
				al_Sql2.add("update  pub_wf_transition  set fromactivity=" + str_new[i] + "  where   fromactivity=" + str_old[i] + "  and  processid=" + _hvs.getStringValue("flowid") + "");
				al_Sql2.add("update  pub_wf_transition  set toactivity=" + str_new[i] + "  where   toactivity=" + str_old[i] + "   and  processid=" + _hvs.getStringValue("flowid") + "");
			}
		}
		getCommDMO().executeBatchByDS(null, al_sql);
		if (type == 1) {
			getCommDMO().executeBatchByDS(null, al_Sql2);
		}
	}

	/**
	 * 文档加水印！！只是在pub_filewatermark 中添加一条记录，当打开文档的时候才显示水印！支持批量添加！！
	 */
	public void addWatermark(String filename, String textwater, String picwater, String picposition) {
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("pub_filewatermark");
		List sqllist = new ArrayList();
		String hexstr_textwater = getTBUtil().convertStrToHexString(textwater); //转换成十六进制
		String hexstr_picwater = getTBUtil().convertStrToHexString(picwater); //转换成十六进制
		try {
			if (filename.contains(";")) { //附件里批量加水印
				String[] filenames = filename.split(";");
				for (int i = 0; i < filenames.length; i++) {
					sqllist.add("delete from pub_filewatermark where filename='" + filenames[i] + "' ");
					sqlBuilder.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_pub_filewatermark"));
					sqlBuilder.putFieldValue("filename", filenames[i]);
					sqlBuilder.putFieldValue("textwater", hexstr_textwater);
					sqlBuilder.putFieldValue("picwater", hexstr_picwater);
					sqlBuilder.putFieldValue("picposition", picposition);
					sqllist.add(sqlBuilder.getSQL());
				}
			} else {
				sqllist.add("delete from pub_filewatermark where filename='" + filename + "'");
				sqlBuilder.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_pub_filewatermark"));
				sqlBuilder.putFieldValue("filename", filename);
				sqlBuilder.putFieldValue("textwater", hexstr_textwater);
				sqlBuilder.putFieldValue("picwater", hexstr_picwater);
				sqlBuilder.putFieldValue("picposition", picposition);
				sqllist.add(sqlBuilder.getSQL());
			}

			getCommDMO().executeBatchByDS(null, sqllist);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private WLTInitContext getInitContext() {
		if (thisInitContext != null) {
			return thisInitContext; //
		}
		thisInitContext = new WLTInitContext();
		return thisInitContext;
	}

}