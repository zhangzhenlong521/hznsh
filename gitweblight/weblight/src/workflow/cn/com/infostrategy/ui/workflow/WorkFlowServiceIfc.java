package cn.com.infostrategy.ui.workflow;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.design.ActivityVO;
import cn.com.infostrategy.to.workflow.design.ProcessVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;
import cn.com.infostrategy.ui.common.WLTRemoteCallServiceIfc;

public interface WorkFlowServiceIfc extends WLTRemoteCallServiceIfc {

	//sunxiaoBin�ķ���!!
	//�õ�һ��ָ���Ĺ���������..
	public ProcessVO getWFProcessByWFCode(String _processId) throws Exception;

	public ProcessVO getWFProcessByWFID(String _processId) throws Exception;

	public ProcessVO getWFProcessByWFID(String _datasourcename, String _processId) throws Exception;

	//�õ�����Ļ���,����һͼ���������
	public ActivityVO[] getOrderedActivitys(String _dsName, String _processid) throws Exception; //

	public ProcessVO getHistoryWFProcessByWFID(String _processId) throws Exception;

	//һ��Զ�̵��õõ����̶�����Ϣ�뵱ǰ������Ϣ!!!
	public HashVO[] getWFDefineAndCurrActivityInfo(String _billtype, String _busitype, String _prinstanceid, String _dealPoolId) throws Exception;

	//��������ʵ������ȡ�õ�ǰ���ڵ������Ϣ!
	public HashVO getCurrActivityInfoByWFId(String _prinstanceid) throws Exception;

	//����һ������������..
	public void saveWFProcess(ProcessVO _processVO, String _processID) throws Exception;

	//ȡ�����п������õĻ���,�����ж��!!
	public ActivityVO[] getStartActivityVOs(String _processid, String _loginUserId) throws Exception;

	//����ĳһ������,
	public String startWFProcess(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO) throws Exception;

	//ȡ�õ�¼��Ա�������еĴ���������!!!
	public HashVO getLoginUserDealPoolTask(String _wfinstanceid, String _loginuserid) throws Exception;

	//��һ��ȡ����Ҫ���������
	public WFParVO getFirstTaskVO(String _wfinstanceid, String _prdealpoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO, String _dealtype) throws Exception;

	//���������תʱ,ȡ�õ�һ��ȡ����Ҫ���������
	public WFParVO getFirstTaskVO_Reject(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO) throws Exception;

	//�ڶ�������ʱ��ִ��������2��ǰ�ò���
	public void intercept2BeforeAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	//�ڶ�������ʱ��ִ��������2�ĺ��ò���
	public void intercept2AfterAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	//�ڶ�������!
	public BillVO secondCall(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception;

	//��������һ�����ʱ�ĵڶ�������!
	public BillVO secondCall_Reject(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO) throws Exception;

	//ȡ������
	public void cancelTask(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception;

    //����Ա���������̽������� �����/2013-05-29��
	public String cancelTask_admin(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception;
	
	//ɾ������
	public void deleteTask(String _prinstanceid, String _loginuserid) throws Exception;

	//��ͣ����
	public void holdWorkflow(String _prinstanceid, String _loginuserid) throws Exception;

	//����һ������
	public String endWorkFlow(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception;
	
	//����Ա���ƻ�������̽��� �����/2013-05-29��
	public String endWorkFlow_admin(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception;

	//ȷ��һ����������!!
	public String confirmUnEffectTask(String _dealPoolId, String _unEffectReason, String _loginUserId) throws Exception;

	//����һ������!!
	public void receiveDealTask(String _prinstanceid, String _loginuserid) throws Exception;

	//�ж��Ƿ�����Ч������,���û���򻹷���ԭ��!
	public HashVO judgeTaskDeal(String _wfinstanceid, String _dealPoolId, String _loginuserid) throws Exception;

	//����Ƿ����
	public boolean[] checkIsReceiveAndIsCCTo(String _prinstanceid, String _loginuserid) throws Exception;

	public boolean checkIsReceiveCancel(String _prinstanceid, String _loginuserid) throws Exception;

	//������������
	public void restartWorkflow(String _prinstanceid, String _loginuserid) throws Exception;

	//�鿴��ʷִ�м�¼
	public HashVO[] getProcessHistoryRecord(String _prinstanceId, boolean _isHiddenMsg) throws Exception;

	//���̼��ʱȡ�����л���
	public HashVO[] getMonitorActivitys(String _prinstanceid) throws Exception; //

	//ȡ�����̼��ʱ���е�ִ�в���..
	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg) throws Exception;

	//ȡ�����̼��ʱ���е�ִ�в���..
	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg, boolean _isFilterMsg) throws Exception; //

	//ȡ�����̼��ʱ���е�ִ�в���..ĳһ����¼
	public HashVO[] getMonitorTransitions(String _prinstanceid, String _batchno, boolean _isHiddenMsg) throws Exception; //

	//ȡ��ĳ������ʵ���ĸ�����ʵ���Ĵ�����! ���������ʵ��������Ǹ�����,��ֱ�ӷ��ر����̵Ĵ�����!
	public String getRootInstanceCreater(String _prinstanceId) throws Exception; //

	//���ƹ���������ԭ��������ɾ���ˣ�
	public void CopyFlow(HashVO _hvo, String _old_flowid, int _type) throws Exception;

	//���ƹ�������
	public void copyWorkFlowProcess(String _fromid, String _newCode, String _newName) throws Exception;

	//���ĵ���ˮӡ
	public void addWatermark(String filename, String textwater, String picwater, String picposition) throws Exception;

}
