package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.HashSet;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.LinkForeignTableDefineVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.workflow.WorkFlowServiceIfc;

public class WorkFlowServiceImpl implements WorkFlowServiceIfc {

	/**
	 * 取得某一个流程数据类
	 */
	public ProcessVO getWFProcessByWFCode(String _processCode) throws Exception {
		return new WorkFlowDesignDMO().getWFProcessByWFCode(_processCode); //
	}

	/**
	 * 取得某一个流程数据类
	 */
	public ProcessVO getWFProcessByWFID(String _processid) throws Exception {
		return new WorkFlowDesignDMO().getWFProcessByWFID(_processid); //
	}

	public ProcessVO getWFProcessByWFID(String _datasourcename, String _processid) throws Exception {
		return new WorkFlowDesignDMO().getWFProcessByWFID(_datasourcename, _processid);
	}

	public ProcessVO getHistoryWFProcessByWFID(String _processid) throws Exception {
		return new WorkFlowDesignDMO().getHistoryWFProcessByWFID(_processid); //
	}

	public ActivityVO[] getOrderedActivitys(String _dsName, String _processid) throws Exception {
		return new WorkFlowDesignDMO().getOrderedActivitys(_dsName, _processid); //
	}

	/**
	 * 工作流程引擎中,在弹出处理界面后执行拦截器时,需要同时得到流程定义信息与当前环节信息,为了提高性能,将两者合成一次远程调用!!!
	 * @param _billtype
	 * @param _busitype
	 * @param _prinstanceid
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getWFDefineAndCurrActivityInfo(String _billtype, String _busitype, String _prinstanceid, String _dealPoolId) throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs_wfassign = commDMO.getHashVoArrayByDS(null, "select * from pub_workflowassign where billtypecode='" + _billtype + "' and busitypecode='" + _busitype + "'"); //工作流分配!!
		if (hvs_wfassign.length <= 0) {
			throw new WLTAppException("根据单据类型[" + _billtype + "]与业务类型[" + _busitype + "],取流程分配记录时没取到!");
		}
		String str_process_id = hvs_wfassign[0].getStringValue("processid"); //工作流id
		HashVO[] hvs_process = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_process where id='" + str_process_id + "'"); //
		if (hvs_process != null && hvs_process.length > 0) {
			String[] str_process_fields = hvs_process[0].getKeys(); //流程所有的字段!!!
			for (int i = 0; i < str_process_fields.length; i++) { //遍历所有字段!
				hvs_wfassign[0].setAttributeValue("process$" + str_process_fields[i], hvs_process[0].getStringValue(str_process_fields[i])); //以process$作前辍,将流程的信息也加进返回的环节对象中!!
			}
		}
		HashVO hvo_currActivityVO = getCurrActivityInfoByWFId(_prinstanceid); //取得当前环节!!!
		if (_dealPoolId != null) { //
			HashVO[] hvs_deals = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id=" + _dealPoolId); //
			if (hvs_deals != null && hvs_deals.length > 0) { //
				String[] str_keys = hvs_deals[0].getKeys(); //
				for (int j = 0; j < str_keys.length; j++) { //将待办任务中的所有信息也加载进来！！！
					hvo_currActivityVO.setAttributeValue("dealpool$" + str_keys[j], hvs_deals[0].getStringValue(str_keys[j])); ////
				}
			}
		}
		return new HashVO[] { hvs_wfassign[0], hvo_currActivityVO }; //

	}

	//根据流程实例主键取得当前环节的相关信息!
	public HashVO getCurrActivityInfoByWFId(String _prinstanceid) throws Exception {
		if (_prinstanceid == null || _prinstanceid.trim().equals("")) { //如果流程实例不为空!
			return null;
		}
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs_prinst = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (hvs_prinst.length <= 0) { //如果找到数据!!!
			throw new WLTAppException("取得当前环节的信息时,根据流程实例主键[" + _prinstanceid + "]并没有相关记录!");
		}

		String str_curractivity = hvs_prinst[0].getStringValue("curractivity"); //取得当前环节
		if (str_curractivity == null || str_curractivity.trim().equals("")) { //如果当前环节不为空,但可能有多个!
			throw new WLTAppException("取得当前环节的信息时,根据流程实例主键[" + _prinstanceid + "]取得记录,但发现当前环节为空!");
		}
		String[] str_activitys = new TBUtil().split(str_curractivity, ","); //
		if (str_activitys == null || str_activitys.length <= 0) {
			throw new WLTAppException("取得当前环节的信息时,根据流程实例主键[" + _prinstanceid + "]取得记录,但发现当前环节为空!");
		}
		HashVO[] hvs_activitys = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_activity where id='" + str_activitys[0] + "'");
		if (hvs_activitys.length <= 0) {
			throw new WLTAppException("取得当前环节的信息时,根据流程实例主键[" + _prinstanceid + "]取得记录,也发现当前环节为[" + str_activitys[0] + "],但根据该环节id去环节表中没有取得对应记录");
		}
		HashVO hvoActivity = hvs_activitys[0]; //
		String[] str_prkeys = hvs_prinst[0].getKeys(); //
		for (int i = 0; i < str_prkeys.length; i++) {
			hvoActivity.setAttributeValue("prinstance$" + str_prkeys[i], hvs_prinst[0].getStringValue(str_prkeys[i])); ////
		}
		return hvoActivity; //
	}

	/**
	 * 保存一个流程数据类
	 */
	public void saveWFProcess(ProcessVO _processVO, String _processID) throws Exception {
		new WorkFlowDesignDMO().saveWFProcess(_processVO, _processID); //
	}

	//取得所有可以启用的环节,可能有多个!!
	public ActivityVO[] getStartActivityVOs(String _processid, String _loginUserId) throws Exception {
		return new WorkFlowEngineDMO().getStartActivityVOs(_processid, _loginUserId); //
	}

	/**
	 * 启动一个流程!
	 */
	public String startWFProcess(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO) throws Exception {
		return new WorkFlowEngineDMO().startWFProcess(_processid, _billVO, _loginUserId, _startActivityVO);
	}

	/**
	 * 取得登录人员的工作流待处理任务!!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 * @throws Exception
	 */
	public HashVO getLoginUserDealPoolTask(String _wfinstanceid, String _loginuserid) throws Exception {
		return new WorkFlowEngineDMO().getLoginUserDealPoolTask(_wfinstanceid, _loginuserid); //
	}

	/**
	 * 第一次先取得需要处理的数据,用于前台显示对应的UI界面!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @param _loginUserDeptid 登录人员所属机构ID
	 * @param _billVO
	 * @param _dealtype
	 * @return
	 * @throws Exception
	 */
	public WFParVO getFirstTaskVO(String _wfinstanceid, String _prdealpoolId, String _loginuserid, String _loginUserDeptid, BillVO _billVO, String _dealtype) throws Exception {
		return new WorkFlowEngineDMO().getFirstTaskVO(_wfinstanceid, _prdealpoolId, _loginuserid, _loginUserDeptid, _billVO, _dealtype); //
	}

	/**
	 * 任意回退跳转时,取得第一次取得需要处理的数据
	 * @param _wfinstanceid 流程实例主键,必须的!
	 * @param _loginuserid  登录人员主键
	 * @param _billVO       业务单据
	 * @return
	 * @throws Exception
	 */
	public WFParVO getFirstTaskVO_Reject(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO) throws Exception {
		return new WorkFlowEngineDMO().getFirstTaskVO_Reject(_wfinstanceid, _prdealPoolId, _loginuserid, _loginUserDeptID, _billVO); //
	}

	/**
	 * 第二次请求时，执行拦截器2的后置操作
	 */
	public void intercept2AfterAction(WFParVO callVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		new WorkFlowEngineDMO().intercept2AfterAction(callVO, _loginuserid, _billVO, _dealtype);
	}

	/**
	 * 第二次请求时，执行拦截器2的前置操作 
	 */
	public void intercept2BeforeAction(WFParVO callVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		new WorkFlowEngineDMO().intercept2BeforeAction(callVO, _loginuserid, _billVO, _dealtype);
	}

	/**
	 * 一般提交的第二次请求
	 */
	public BillVO secondCall(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		return new WorkFlowEngineDMO().secondCall(_secondCallVO, _loginuserid, _billVO, _dealtype); //
	}

	/**
	 * 回退到任意一个结点时的第二次请求!!
	 */
	public BillVO secondCall_Reject(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO) throws Exception {
		return new WorkFlowEngineDMO().secondCall_Reject(_secondCallVO, _loginuserid, _billVO); //
	}

	public void cancelTask(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception {
		new WorkFlowEngineDMO().cancelTask(_prinstanceid, _dealPoolId, _taskOffId, _loginuserid, _dirCancelChildIds); //
	}
	
	//管理员控制主流程结束撤回 【杨科/2013-05-29】
	public String cancelTask_admin(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception {
		return new WorkFlowEngineDMO().cancelTask_admin(_prinstanceid, _dealPoolId, _taskOffId, _loginuserid, _dirCancelChildIds); //
	}

	/**
	 * 删除操作
	 */
	public void deleteTask(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().deleteTask(_prinstanceid, _loginuserid); //
	}

	/**
	 * 暂停工作流.
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void holdWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().holdWorkflow(_prinstanceid, _loginuserid); //
	}

	//结束一个流程
	public String endWorkFlow(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		return new WorkFlowEngineDMO().endWorkFlow(_prinstanceid, _wfParVO, _loginuserid, _message, _msgfile, _endtype, _billVO, _wfegbsintercept); //
	}
	
	//管理员控制会办子流程结束 【杨科/2013-05-29】
	public String endWorkFlow_admin(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		return new WorkFlowEngineDMO().endWorkFlow_admin(_prinstanceid, _wfParVO, _loginuserid, _message, _msgfile, _endtype, _billVO, _wfegbsintercept); 
	}

	//确认一个流程任务!
	public String  confirmUnEffectTask(String _dealPoolId, String _unEffectReason, String _loginUserId) throws Exception {
		return new WorkFlowEngineDMO().confirmUnEffectTask(_dealPoolId, _unEffectReason, _loginUserId); //
	}

	/**
	 * 接收一个任务
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void receiveDealTask(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().receiveDealTask(_prinstanceid, _loginuserid); //
	}

	/**
	 * 继续工作流...
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void restartWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().restartWorkflow(_prinstanceid, _loginuserid); //
	}

	/**
	 * 取得流程执行的历史记录
	 */
	public HashVO[] getProcessHistoryRecord(String _prinstanceId, boolean _isHiddenMsg) throws Exception {
		return new WorkFlowEngineDMO().getProcessHistoryRecord(_prinstanceId, _isHiddenMsg); //
	}

	/**
	 * 取得流程监控时所有的环节..
	 */
	public HashVO[] getMonitorActivitys(String _prinstanceid) throws Exception { //
		CommDMO commDMO = new CommDMO(); //
		String str_sql = "select distinct batchno,curractivity,isprocess from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' order by batchno"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,approvemodel,approvenumber", "id", "curractivity"); //
		HashVO[] hvs = commDMO.getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1 }); //
		return hvs; //
	}

	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg) throws Exception { //
		return getMonitorTransitions(_prinstanceid, _isHiddenMsg, false); //不过滤的
	}

	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg, boolean _isFilterMsg) throws Exception {
		return getMonitorTransitions(_prinstanceid, _isHiddenMsg, _isFilterMsg, null); //
	}

	/**
	 *  取得流程监控时所有的执行步骤..
	 * @param _prinstanceid
	 * @param _isHiddenMsg 是否隐藏/加密意见,即有些领导的意见不能被其他人看到,需要使用***加密!
	 * @param _isFilterMsg 是否过滤意见,即处理时只显示子流程的最后一步意见!中间意见太多了,没必要显示!
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg, boolean _isFilterMsg, String _loginUserId) throws Exception { //
		if (_prinstanceid == null || _prinstanceid.trim().equals("")) {
			return new HashVO[0]; //
		}
		CommDMO commDMO = new CommDMO(); //
		TBUtil tbUtil = new TBUtil(); //
		String str_rootInstId = null; //
		String str_fromDealPoolId = null; //
		HashVO[] hvs_instinfo = commDMO.getHashVoArrayByDS(null, "select rootinstanceid,fromparentdealpoolid from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (hvs_instinfo != null && hvs_instinfo.length > 0) {
			str_rootInstId = hvs_instinfo[0].getStringValue("rootinstanceid"); //根实例!
			str_fromDealPoolId = hvs_instinfo[0].getStringValue("fromparentdealpoolid"); //该实例是由哪个任务创建的!
		}
		if (str_rootInstId == null) {
			str_rootInstId = _prinstanceid; //
		}

		String str_sql = "select * from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' or rootinstanceid='" + str_rootInstId + "' order by id"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_activity", "id,code,wfname,iscanlookidea,isneedreport,belongdeptgroup,belongstationgroup", "id", "curractivity"); //
		LinkForeignTableDefineVO lfvo_2 = new LinkForeignTableDefineVO("pub_wf_activity", "id,approvemodel", "id", "submittoactivity"); //
		LinkForeignTableDefineVO lfvo_3 = new LinkForeignTableDefineVO("pub_wf_prinstance", "id,fromparentactivity,endbypoolid", "id", "prinstanceid"); //找出父亲流程的环节名称,只有是子流程时才能取到值!!
		HashVO[] hvs = commDMO.getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1, lfvo_2, lfvo_3 }); //

		String str_loginUserid = null;
		if (_loginUserId != null) {
			str_loginUserid = _loginUserId; //
		} else {
			str_loginUserid = new WLTInitContext().getCurrSession().getLoginUserId(); //登录人员id
		}
		String[] str_allCreateByIds = commDMO.getStringArrayFirstColByDS(null, "select createbyid from pub_wf_dealpool where (participant_user='" + str_loginUserid + "' or participant_accruserid='" + str_loginUserid + "') and (prinstanceid='" + _prinstanceid + "' or rootinstanceid='"
				+ str_rootInstId + "')"); ///

		if (_isFilterMsg && tbUtil.getSysOptionBooleanValue("工作流处理时需要对历史记录进行过滤", true)) { //如果需要进行过滤,即在处理界面与监控界面显示的条目不一样!处理界面要少一些!否则太多!理论上只想看本流程的,包括进入本流程与本流程出去的!
			ArrayList al_filterHvs = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) { //遍历各条记录!!
				String str_dealpoolId = hvs[i].getStringValue("id"); //该任务的id
				String str_thisInstId = hvs[i].getStringValue("prinstanceid"); //该条任务的流程实例
				String str_thisParentInstId = hvs[i].getStringValue("parentinstanceid"); //这条任务的父流程!
				String str_participant_user = hvs[i].getStringValue("participant_user"); //参与人员
				String str_participant_accruserid = hvs[i].getStringValue("participant_accruserid"); //授权人
				String str_lifecycle = hvs[i].getStringValue("lifecycle"); //生命周期!!!
				if (str_loginUserid.equals(str_participant_user) || str_loginUserid.equals(str_participant_accruserid)) { //如果这条任务是给我的,即这条意见应该也是我填的,所以肯定要显示!
					al_filterHvs.add(hvs[i]); //
					continue; //
				}

				if (tbUtil.isExistInArray(hvs[i].getStringValue("id"), str_allCreateByIds)) { //如果这条任务是本人任务的创建者,即我是本人是B,A提交给我的意见,我肯定能看到!
					al_filterHvs.add(hvs[i]); //
					continue; //
				}

				if (str_thisInstId.equals(_prinstanceid)) { //如果是本流程的,则直接显示,即如果本人就是子流程,则肯定显示本流程的!
					al_filterHvs.add(hvs[i]); //
					continue; //
				} else { //如果不是一个同一个流程实例!则
					if (str_fromDealPoolId != null && str_dealpoolId.equals(str_fromDealPoolId)) { //如果本流程是子流程(fromparentdealpoolid不为空),且这条任务是本流程实例的创建者,则永远都能看!即本子流程的发起人的意见!
						al_filterHvs.add(hvs[i]); //
						continue; //
					}
					if (_prinstanceid.equals(str_thisParentInstId) && "EC".equals(str_lifecycle)) { //如果这个流程我登录人员下属的子流程,且又是结束的记录!则也显示!
						al_filterHvs.add(hvs[i]); //
						continue; //
					}
				}

			}
			hvs = (HashVO[]) al_filterHvs.toArray(new HashVO[0]); //
		} else { //如果不过滤,则显示所有,包括主流程与子流程的都显示!!

		}

		HashSet hst = new HashSet(); //
		for (int i = 0; i < hvs.length; i++) {
			if (hvs[i].getStringValue("prinstanceid_fromparentactivity") != null) {
				hst.add(hvs[i].getStringValue("prinstanceid_fromparentactivity")); //
			}
		}
		String[] str_actIds = (String[]) hst.toArray(new String[0]); //
		if (str_actIds.length > 0) {
			HashVO[] hvs_actsInfos = commDMO.getHashVoArrayByDS(null, "select id,wfname,isneedreport,belongdeptgroup from pub_wf_activity where id in (" + new TBUtil().getInCondition(str_actIds) + ")"); //
			for (int i = 0; i < hvs.length; i++) {
				String str_fromparentactivityName = ""; //
				String str_fromparentactivityisneedreport = ""; //
				String str_fromparentactivityBldeptGroup = ""; //来源环节所属的部门组
				if (hvs[i].getStringValue("prinstanceid_fromparentactivity") != null) { //
					for (int j = 0; j < hvs_actsInfos.length; j++) {
						if (hvs_actsInfos[j].getStringValue("id").equals(hvs[i].getStringValue("prinstanceid_fromparentactivity"))) {
							str_fromparentactivityName = hvs_actsInfos[j].getStringValue("wfname"); //
							str_fromparentactivityisneedreport = hvs_actsInfos[j].getStringValue("isneedreport"); //是否输出
							str_fromparentactivityBldeptGroup = hvs_actsInfos[j].getStringValue("belongdeptgroup"); //属于哪个组
						}
					}

				}
				hvs[i].setAttributeValue("prinstanceid_fromparentactivityName", str_fromparentactivityName); //来源环节名称
				hvs[i].setAttributeValue("prinstanceid_fromparentactivityisneedreport", str_fromparentactivityisneedreport); //来源环节是否输出
				hvs[i].setAttributeValue("prinstanceid_fromparentactivitybldeptGroup", str_fromparentactivityBldeptGroup); //来源环节是否输出
			}
		}

		if (_isHiddenMsg) { //如果需要加密!则进行加密处理!
			for (int i = 0; i < hvs.length; i++) { //加密后会将"submitmessage"修改成"****",即冲掉!后来有了超级管理员可以绿色通道查看加密前的数据,所以需要将加密前的数据将存下来!!
				hvs[i].setAttributeValue("submitmessage_real", hvs[i].getStringValue("submitmessage")); //复制一遍!!
				hvs[i].setAttributeValue("submitmessagefile_real", hvs[i].getStringValue("submitmessagefile")); //复制一遍!!
			}
			new WorkFlowBSUtil().dealHiddenMsg(hvs, _prinstanceid, str_loginUserid); //进行屏蔽处理消息,即其他部门的人看不到法规部的意见
		} else {
			for (int i = 0; i < hvs.length; i++) { //
				hvs[i].setAttributeValue("submitmessage_viewreason", "因为调用时没有指定要做加密,所以统一能被查看!"); //
			}
		}

		//合并环节名称!
		for (int i = 0; i < hvs.length; i++) {
			StringBuilder sb_text = new StringBuilder(); //
			String str_FromParentWFActivityName = hvs[i].getStringValue("prinstanceid_fromparentactivityName"); //父流程的环节名称
			String str_curractivityBLDeptName = hvs[i].getStringValue("curractivity_belongdeptgroup"); //当前环节的矩阵
			String str_curractivityName = hvs[i].getStringValue("curractivity_wfname"); //环节名称
			if (str_FromParentWFActivityName != null && !str_FromParentWFActivityName.trim().equals("")) {
				sb_text.append(str_FromParentWFActivityName + "-"); //
			}
			if (str_curractivityBLDeptName != null && !str_curractivityBLDeptName.trim().equals("")) {
				sb_text.append(str_curractivityBLDeptName + "-"); //
			}
			if (str_curractivityName != null && !str_curractivityName.trim().equals("")) {
				sb_text.append(str_curractivityName + "-"); //
			}
			String str_activeNameSpan = sb_text.toString(); //
			if (str_activeNameSpan.endsWith("-")) {
				str_activeNameSpan = str_activeNameSpan.substring(0, str_activeNameSpan.length() - 1); //
			}
			str_activeNameSpan = tbUtil.replaceAll(str_activeNameSpan, "\r", ""); //
			str_activeNameSpan = tbUtil.replaceAll(str_activeNameSpan, "\n", ""); //
			hvs[i].setAttributeValue("curractivity_wfname2", str_activeNameSpan); //合并的环节名称
		}

		//new TBUtil().writeHashToHtmlTableFile(hvs, "C:/pp12345.html", new String[] { "curractivity_wfname2", "realsubmitcorpname", "realsubmitername", "submittime", "submitmessage" }); //
		return hvs; //
	}

	/**
	 *  取得流程监控时所有的执行步骤..
	 */
	public HashVO[] getMonitorTransitions(String _prinstanceid, String _batchno, boolean _isHiddenMsg) throws Exception { //
		CommDMO commDMO = new CommDMO(); //
		String str_sql = "select id,prinstanceid,curractivity,transition,participant_user,participant_usercode,participant_username,participant_accruserid,participant_accrusercode,participant_accrusername,participant_userdept,participant_userrole,isreceive,receivetime,issubmit,submitisapprove,submittime,submitmessage from pub_wf_dealpool where prinstanceid='"
				+ _prinstanceid + "' and batchno=" + _batchno + " order by id"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_transition", "wfname", "id", "transition"); //
		LinkForeignTableDefineVO lfvo_2 = new LinkForeignTableDefineVO("pub_corp_dept", "code,name", "id", "participant_userdept"); //
		LinkForeignTableDefineVO lfvo_3 = new LinkForeignTableDefineVO("pub_role", "code,name", "id", "participant_userrole"); //
		LinkForeignTableDefineVO lfvo_4 = new LinkForeignTableDefineVO("pub_wf_activity", "id,code,wfname,iscanlookidea,isneedreport,belongdeptgroup,belongstationgroup", "id", "curractivity"); //
		HashVO[] hvs = commDMO.getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1, lfvo_2, lfvo_3, lfvo_4 }); //
		if (_isHiddenMsg) {
			new WorkFlowBSUtil().dealHiddenMsg(hvs, _prinstanceid); //
		}
		return hvs; //
	}

	/**
	 * 取得某个流程的根流程的创建人!在进行是否可编辑等校验逻辑时需要! 即根流程的创建人永远是可以编辑表单的!
	 * @param _prinstanceId
	 * @return
	 */
	public String getRootInstanceCreater(String _prinstanceId) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvs_inst = commDMO.getHashVoArrayByDS(null, "select id,creater,rootinstanceid from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		if (hvs_inst != null && hvs_inst.length > 0) {
			String str_inst_id = hvs_inst[0].getStringValue("id"); //
			String str_rootinst_id = hvs_inst[0].getStringValue("rootinstanceid"); //
			if (str_inst_id.equals(str_rootinst_id) || str_rootinst_id == null) { //如果本流程实例id等于根流程实例id,即本流程就是根流程!(如果根流程实例为空,则说明是老版本!)
				return hvs_inst[0].getStringValue("creater"); //流程创建人!
			} else {
				return commDMO.getStringValueByDS(null, "select creater from pub_wf_prinstance where id='" + str_rootinst_id + "'"); //找出根流程的创建者!
			}
		} else {
			return null; //
		}
	}

	//判断是否有有效的任务! 非常关键有方法!! 也是唯一判断登录人是否有真正的待办任务的! 如果没有,则还有原因说明!!!
	public HashVO judgeTaskDeal(String _wfinstanceid, String _dealPoolId, String _loginuserid) throws Exception {
		return new WorkFlowEngineDMO().judgeTaskDeal(_wfinstanceid, _dealPoolId, _loginuserid); //
	}

	public boolean[] checkIsReceiveAndIsCCTo(String _prinstanceid, String _loginuserid) throws Exception {
		return new WorkFlowEngineDMO().checkIsReceiveAndIsCCTo(_prinstanceid, _loginuserid); //
	}

	public boolean checkIsReceiveCancel(String _prinstanceid, String _loginuserid) throws Exception {
		return new WorkFlowEngineDMO().checkIsReceiveCancel(_prinstanceid, _loginuserid); //
	}

	public void CopyFlow(HashVO _hvo, String _old_flowid, int _type) throws Exception {
		new WorkFlowEngineDMO().CopyFlow(_hvo, _old_flowid, _type);
	}

	public void copyWorkFlowProcess(String _fromid, String _newCode, String _newName) throws Exception {
		new WorkFlowDesignDMO().copyWorkFlowProcess(_fromid, _newCode, _newName); //
	}

	/**
	 * 文档加水印！！只是在pub_filewatermark 中添加一条记录，当打开文档的时候才显示水印！
	 */
	public void addWatermark(String filename, String textwater, String picwater, String picposition) throws Exception {
		new WorkFlowEngineDMO().addWatermark(filename, textwater, picwater, picposition);
	}
}