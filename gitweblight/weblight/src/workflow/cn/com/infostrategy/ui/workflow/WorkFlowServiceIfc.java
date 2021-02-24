package cn.com.infostrategy.ui.workflow;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface WorkFlowServiceIfc extends WLTRemoteCallServiceIfc {

	//sunxiaoBin的方法!!
	//得到一个指定的工作流流程..
	public ProcessVO getWFProcessByWFCode(String _processId) throws Exception;

	public ProcessVO getWFProcessByWFID(String _processId) throws Exception;

	public ProcessVO getWFProcessByWFID(String _datasourcename, String _processId) throws Exception;

	//得到排序的环节,用于一图两表输出等
	public ActivityVO[] getOrderedActivitys(String _dsName, String _processid) throws Exception; //

	public ProcessVO getHistoryWFProcessByWFID(String _processId) throws Exception;

	//一次远程调用得到流程定义信息与当前环节信息!!!
	public HashVO[] getWFDefineAndCurrActivityInfo(String _billtype, String _busitype, String _prinstanceid, String _dealPoolId) throws Exception;

	//根据流程实例主键取得当前环节的相关信息!
	public HashVO getCurrActivityInfoByWFId(String _prinstanceid) throws Exception;

	//保存一个工作流流程..
	public void saveWFProcess(ProcessVO _processVO, String _processID) throws Exception;

	//取得所有可以启用的环节,可能有多个!!
	public ActivityVO[] getStartActivityVOs(String _processid, String _loginUserId) throws Exception;

	//启动某一个流程,
	public String startWFProcess(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO) throws Exception;

	//取得登录人员的流程中的待处理任务!!!
	public HashVO getLoginUserDealPoolTask(String _wfinstanceid, String _loginuserid) throws Exception;

	//第一次取得需要处理的数据
	public WFParVO getFirstTaskVO(String _wfinstanceid, String _prdealpoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO, String _dealtype) throws Exception;

	//任意回退跳转时,取得第一次取得需要处理的数据
	public WFParVO getFirstTaskVO_Reject(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO) throws Exception;

	//第二次请求时，执行拦截器2的前置操作
	public void intercept2BeforeAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	//第二次请求时，执行拦截器2的后置操作
	public void intercept2AfterAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	//第二次请求!
	public BillVO secondCall(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	//回退任意一个结点时的第二次请求!
	public BillVO secondCall_Reject(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO) throws Exception;

	//取消操作
	public void cancelTask(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception;

    //管理员控制主流程结束撤回 【杨科/2013-05-29】
	public String cancelTask_admin(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception;
	
	//删除操作
	public void deleteTask(String _prinstanceid, String _loginuserid) throws Exception;

	//暂停流程
	public void holdWorkflow(String _prinstanceid, String _loginuserid) throws Exception;

	//结束一个流程
	public String endWorkFlow(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception;
	
	//管理员控制会办子流程结束 【杨科/2013-05-29】
	public String endWorkFlow_admin(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception;

	//确认一个流程任务!!
	public String confirmUnEffectTask(String _dealPoolId, String _unEffectReason, String _loginUserId) throws Exception;

	//接收一个流程!!
	public void receiveDealTask(String _prinstanceid, String _loginuserid) throws Exception;

	//判断是否有有效的任务,如果没有则还返回原因!
	public HashVO judgeTaskDeal(String _wfinstanceid, String _dealPoolId, String _loginuserid) throws Exception;

	//检查是否接受
	public boolean[] checkIsReceiveAndIsCCTo(String _prinstanceid, String _loginuserid) throws Exception;

	public boolean checkIsReceiveCancel(String _prinstanceid, String _loginuserid) throws Exception;

	//重新启动流程
	public void restartWorkflow(String _prinstanceid, String _loginuserid) throws Exception;

	//查看历史执行记录
	public HashVO[] getProcessHistoryRecord(String _prinstanceId, boolean _isHiddenMsg) throws Exception;

	//流程监控时取得所有环节
	public HashVO[] getMonitorActivitys(String _prinstanceid) throws Exception; //

	//取得流程监控时所有的执行步骤..
	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg) throws Exception;

	//取得流程监控时所有的执行步骤..
	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg, boolean _isFilterMsg) throws Exception; //

	//取得流程监控时所有的执行步骤..某一批记录
	public HashVO[] getMonitorTransitions(String _prinstanceid, String _batchno, boolean _isHiddenMsg) throws Exception; //

	//取得某个流程实例的根流程实例的创建人! 如果该流程实例本身就是根流程,则直接返回本流程的创建者!
	public String getRootInstanceCreater(String _prinstanceId) throws Exception; //

	//复制工作流，将原来工作流删除了！
	public void CopyFlow(HashVO _hvo, String _old_flowid, int _type) throws Exception;

	//复制工作流！
	public void copyWorkFlowProcess(String _fromid, String _newCode, String _newName) throws Exception;

	//给文档加水印
	public void addWatermark(String filename, String textwater, String picwater, String picposition) throws Exception;

}
