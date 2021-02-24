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
	 * ȡ��ĳһ������������
	 */
	public ProcessVO getWFProcessByWFCode(String _processCode) throws Exception {
		return new WorkFlowDesignDMO().getWFProcessByWFCode(_processCode); //
	}

	/**
	 * ȡ��ĳһ������������
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
	 * ��������������,�ڵ�����������ִ��������ʱ,��Ҫͬʱ�õ����̶�����Ϣ�뵱ǰ������Ϣ,Ϊ���������,�����ߺϳ�һ��Զ�̵���!!!
	 * @param _billtype
	 * @param _busitype
	 * @param _prinstanceid
	 * @return
	 * @throws Exception
	 */
	public HashVO[] getWFDefineAndCurrActivityInfo(String _billtype, String _busitype, String _prinstanceid, String _dealPoolId) throws Exception {
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs_wfassign = commDMO.getHashVoArrayByDS(null, "select * from pub_workflowassign where billtypecode='" + _billtype + "' and busitypecode='" + _busitype + "'"); //����������!!
		if (hvs_wfassign.length <= 0) {
			throw new WLTAppException("���ݵ�������[" + _billtype + "]��ҵ������[" + _busitype + "],ȡ���̷����¼ʱûȡ��!");
		}
		String str_process_id = hvs_wfassign[0].getStringValue("processid"); //������id
		HashVO[] hvs_process = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_process where id='" + str_process_id + "'"); //
		if (hvs_process != null && hvs_process.length > 0) {
			String[] str_process_fields = hvs_process[0].getKeys(); //�������е��ֶ�!!!
			for (int i = 0; i < str_process_fields.length; i++) { //���������ֶ�!
				hvs_wfassign[0].setAttributeValue("process$" + str_process_fields[i], hvs_process[0].getStringValue(str_process_fields[i])); //��process$��ǰ�,�����̵���ϢҲ�ӽ����صĻ��ڶ�����!!
			}
		}
		HashVO hvo_currActivityVO = getCurrActivityInfoByWFId(_prinstanceid); //ȡ�õ�ǰ����!!!
		if (_dealPoolId != null) { //
			HashVO[] hvs_deals = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id=" + _dealPoolId); //
			if (hvs_deals != null && hvs_deals.length > 0) { //
				String[] str_keys = hvs_deals[0].getKeys(); //
				for (int j = 0; j < str_keys.length; j++) { //�����������е�������ϢҲ���ؽ���������
					hvo_currActivityVO.setAttributeValue("dealpool$" + str_keys[j], hvs_deals[0].getStringValue(str_keys[j])); ////
				}
			}
		}
		return new HashVO[] { hvs_wfassign[0], hvo_currActivityVO }; //

	}

	//��������ʵ������ȡ�õ�ǰ���ڵ������Ϣ!
	public HashVO getCurrActivityInfoByWFId(String _prinstanceid) throws Exception {
		if (_prinstanceid == null || _prinstanceid.trim().equals("")) { //�������ʵ����Ϊ��!
			return null;
		}
		CommDMO commDMO = new CommDMO();
		HashVO[] hvs_prinst = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (hvs_prinst.length <= 0) { //����ҵ�����!!!
			throw new WLTAppException("ȡ�õ�ǰ���ڵ���Ϣʱ,��������ʵ������[" + _prinstanceid + "]��û����ؼ�¼!");
		}

		String str_curractivity = hvs_prinst[0].getStringValue("curractivity"); //ȡ�õ�ǰ����
		if (str_curractivity == null || str_curractivity.trim().equals("")) { //�����ǰ���ڲ�Ϊ��,�������ж��!
			throw new WLTAppException("ȡ�õ�ǰ���ڵ���Ϣʱ,��������ʵ������[" + _prinstanceid + "]ȡ�ü�¼,�����ֵ�ǰ����Ϊ��!");
		}
		String[] str_activitys = new TBUtil().split(str_curractivity, ","); //
		if (str_activitys == null || str_activitys.length <= 0) {
			throw new WLTAppException("ȡ�õ�ǰ���ڵ���Ϣʱ,��������ʵ������[" + _prinstanceid + "]ȡ�ü�¼,�����ֵ�ǰ����Ϊ��!");
		}
		HashVO[] hvs_activitys = commDMO.getHashVoArrayByDS(null, "select * from pub_wf_activity where id='" + str_activitys[0] + "'");
		if (hvs_activitys.length <= 0) {
			throw new WLTAppException("ȡ�õ�ǰ���ڵ���Ϣʱ,��������ʵ������[" + _prinstanceid + "]ȡ�ü�¼,Ҳ���ֵ�ǰ����Ϊ[" + str_activitys[0] + "],�����ݸû���idȥ���ڱ���û��ȡ�ö�Ӧ��¼");
		}
		HashVO hvoActivity = hvs_activitys[0]; //
		String[] str_prkeys = hvs_prinst[0].getKeys(); //
		for (int i = 0; i < str_prkeys.length; i++) {
			hvoActivity.setAttributeValue("prinstance$" + str_prkeys[i], hvs_prinst[0].getStringValue(str_prkeys[i])); ////
		}
		return hvoActivity; //
	}

	/**
	 * ����һ������������
	 */
	public void saveWFProcess(ProcessVO _processVO, String _processID) throws Exception {
		new WorkFlowDesignDMO().saveWFProcess(_processVO, _processID); //
	}

	//ȡ�����п������õĻ���,�����ж��!!
	public ActivityVO[] getStartActivityVOs(String _processid, String _loginUserId) throws Exception {
		return new WorkFlowEngineDMO().getStartActivityVOs(_processid, _loginUserId); //
	}

	/**
	 * ����һ������!
	 */
	public String startWFProcess(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO) throws Exception {
		return new WorkFlowEngineDMO().startWFProcess(_processid, _billVO, _loginUserId, _startActivityVO);
	}

	/**
	 * ȡ�õ�¼��Ա�Ĺ���������������!!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 * @throws Exception
	 */
	public HashVO getLoginUserDealPoolTask(String _wfinstanceid, String _loginuserid) throws Exception {
		return new WorkFlowEngineDMO().getLoginUserDealPoolTask(_wfinstanceid, _loginuserid); //
	}

	/**
	 * ��һ����ȡ����Ҫ���������,����ǰ̨��ʾ��Ӧ��UI����!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @param _loginUserDeptid ��¼��Ա��������ID
	 * @param _billVO
	 * @param _dealtype
	 * @return
	 * @throws Exception
	 */
	public WFParVO getFirstTaskVO(String _wfinstanceid, String _prdealpoolId, String _loginuserid, String _loginUserDeptid, BillVO _billVO, String _dealtype) throws Exception {
		return new WorkFlowEngineDMO().getFirstTaskVO(_wfinstanceid, _prdealpoolId, _loginuserid, _loginUserDeptid, _billVO, _dealtype); //
	}

	/**
	 * ���������תʱ,ȡ�õ�һ��ȡ����Ҫ���������
	 * @param _wfinstanceid ����ʵ������,�����!
	 * @param _loginuserid  ��¼��Ա����
	 * @param _billVO       ҵ�񵥾�
	 * @return
	 * @throws Exception
	 */
	public WFParVO getFirstTaskVO_Reject(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO) throws Exception {
		return new WorkFlowEngineDMO().getFirstTaskVO_Reject(_wfinstanceid, _prdealPoolId, _loginuserid, _loginUserDeptID, _billVO); //
	}

	/**
	 * �ڶ�������ʱ��ִ��������2�ĺ��ò���
	 */
	public void intercept2AfterAction(WFParVO callVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		new WorkFlowEngineDMO().intercept2AfterAction(callVO, _loginuserid, _billVO, _dealtype);
	}

	/**
	 * �ڶ�������ʱ��ִ��������2��ǰ�ò��� 
	 */
	public void intercept2BeforeAction(WFParVO callVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		new WorkFlowEngineDMO().intercept2BeforeAction(callVO, _loginuserid, _billVO, _dealtype);
	}

	/**
	 * һ���ύ�ĵڶ�������
	 */
	public BillVO secondCall(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		return new WorkFlowEngineDMO().secondCall(_secondCallVO, _loginuserid, _billVO, _dealtype); //
	}

	/**
	 * ���˵�����һ�����ʱ�ĵڶ�������!!
	 */
	public BillVO secondCall_Reject(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO) throws Exception {
		return new WorkFlowEngineDMO().secondCall_Reject(_secondCallVO, _loginuserid, _billVO); //
	}

	public void cancelTask(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception {
		new WorkFlowEngineDMO().cancelTask(_prinstanceid, _dealPoolId, _taskOffId, _loginuserid, _dirCancelChildIds); //
	}
	
	//����Ա���������̽������� �����/2013-05-29��
	public String cancelTask_admin(String _prinstanceid, String _dealPoolId, String _taskOffId, String _loginuserid, String[] _dirCancelChildIds) throws Exception {
		return new WorkFlowEngineDMO().cancelTask_admin(_prinstanceid, _dealPoolId, _taskOffId, _loginuserid, _dirCancelChildIds); //
	}

	/**
	 * ɾ������
	 */
	public void deleteTask(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().deleteTask(_prinstanceid, _loginuserid); //
	}

	/**
	 * ��ͣ������.
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void holdWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().holdWorkflow(_prinstanceid, _loginuserid); //
	}

	//����һ������
	public String endWorkFlow(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		return new WorkFlowEngineDMO().endWorkFlow(_prinstanceid, _wfParVO, _loginuserid, _message, _msgfile, _endtype, _billVO, _wfegbsintercept); //
	}
	
	//����Ա���ƻ�������̽��� �����/2013-05-29��
	public String endWorkFlow_admin(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		return new WorkFlowEngineDMO().endWorkFlow_admin(_prinstanceid, _wfParVO, _loginuserid, _message, _msgfile, _endtype, _billVO, _wfegbsintercept); 
	}

	//ȷ��һ����������!
	public String  confirmUnEffectTask(String _dealPoolId, String _unEffectReason, String _loginUserId) throws Exception {
		return new WorkFlowEngineDMO().confirmUnEffectTask(_dealPoolId, _unEffectReason, _loginUserId); //
	}

	/**
	 * ����һ������
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void receiveDealTask(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().receiveDealTask(_prinstanceid, _loginuserid); //
	}

	/**
	 * ����������...
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void restartWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		new WorkFlowEngineDMO().restartWorkflow(_prinstanceid, _loginuserid); //
	}

	/**
	 * ȡ������ִ�е���ʷ��¼
	 */
	public HashVO[] getProcessHistoryRecord(String _prinstanceId, boolean _isHiddenMsg) throws Exception {
		return new WorkFlowEngineDMO().getProcessHistoryRecord(_prinstanceId, _isHiddenMsg); //
	}

	/**
	 * ȡ�����̼��ʱ���еĻ���..
	 */
	public HashVO[] getMonitorActivitys(String _prinstanceid) throws Exception { //
		CommDMO commDMO = new CommDMO(); //
		String str_sql = "select distinct batchno,curractivity,isprocess from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' order by batchno"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,approvemodel,approvenumber", "id", "curractivity"); //
		HashVO[] hvs = commDMO.getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1 }); //
		return hvs; //
	}

	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg) throws Exception { //
		return getMonitorTransitions(_prinstanceid, _isHiddenMsg, false); //�����˵�
	}

	public HashVO[] getMonitorTransitions(String _prinstanceid, boolean _isHiddenMsg, boolean _isFilterMsg) throws Exception {
		return getMonitorTransitions(_prinstanceid, _isHiddenMsg, _isFilterMsg, null); //
	}

	/**
	 *  ȡ�����̼��ʱ���е�ִ�в���..
	 * @param _prinstanceid
	 * @param _isHiddenMsg �Ƿ�����/�������,����Щ�쵼��������ܱ������˿���,��Ҫʹ��***����!
	 * @param _isFilterMsg �Ƿ�������,������ʱֻ��ʾ�����̵����һ�����!�м����̫����,û��Ҫ��ʾ!
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
			str_rootInstId = hvs_instinfo[0].getStringValue("rootinstanceid"); //��ʵ��!
			str_fromDealPoolId = hvs_instinfo[0].getStringValue("fromparentdealpoolid"); //��ʵ�������ĸ����񴴽���!
		}
		if (str_rootInstId == null) {
			str_rootInstId = _prinstanceid; //
		}

		String str_sql = "select * from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' or rootinstanceid='" + str_rootInstId + "' order by id"; //
		LinkForeignTableDefineVO lfvo_1 = new LinkForeignTableDefineVO("pub_wf_activity", "id,code,wfname,iscanlookidea,isneedreport,belongdeptgroup,belongstationgroup", "id", "curractivity"); //
		LinkForeignTableDefineVO lfvo_2 = new LinkForeignTableDefineVO("pub_wf_activity", "id,approvemodel", "id", "submittoactivity"); //
		LinkForeignTableDefineVO lfvo_3 = new LinkForeignTableDefineVO("pub_wf_prinstance", "id,fromparentactivity,endbypoolid", "id", "prinstanceid"); //�ҳ��������̵Ļ�������,ֻ����������ʱ����ȡ��ֵ!!
		HashVO[] hvs = commDMO.getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_1, lfvo_2, lfvo_3 }); //

		String str_loginUserid = null;
		if (_loginUserId != null) {
			str_loginUserid = _loginUserId; //
		} else {
			str_loginUserid = new WLTInitContext().getCurrSession().getLoginUserId(); //��¼��Աid
		}
		String[] str_allCreateByIds = commDMO.getStringArrayFirstColByDS(null, "select createbyid from pub_wf_dealpool where (participant_user='" + str_loginUserid + "' or participant_accruserid='" + str_loginUserid + "') and (prinstanceid='" + _prinstanceid + "' or rootinstanceid='"
				+ str_rootInstId + "')"); ///

		if (_isFilterMsg && tbUtil.getSysOptionBooleanValue("����������ʱ��Ҫ����ʷ��¼���й���", true)) { //�����Ҫ���й���,���ڴ���������ؽ�����ʾ����Ŀ��һ��!�������Ҫ��һЩ!����̫��!������ֻ�뿴�����̵�,�������뱾�����뱾���̳�ȥ��!
			ArrayList al_filterHvs = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) { //����������¼!!
				String str_dealpoolId = hvs[i].getStringValue("id"); //�������id
				String str_thisInstId = hvs[i].getStringValue("prinstanceid"); //�������������ʵ��
				String str_thisParentInstId = hvs[i].getStringValue("parentinstanceid"); //��������ĸ�����!
				String str_participant_user = hvs[i].getStringValue("participant_user"); //������Ա
				String str_participant_accruserid = hvs[i].getStringValue("participant_accruserid"); //��Ȩ��
				String str_lifecycle = hvs[i].getStringValue("lifecycle"); //��������!!!
				if (str_loginUserid.equals(str_participant_user) || str_loginUserid.equals(str_participant_accruserid)) { //������������Ǹ��ҵ�,���������Ӧ��Ҳ�������,���Կ϶�Ҫ��ʾ!
					al_filterHvs.add(hvs[i]); //
					continue; //
				}

				if (tbUtil.isExistInArray(hvs[i].getStringValue("id"), str_allCreateByIds)) { //������������Ǳ�������Ĵ�����,�����Ǳ�����B,A�ύ���ҵ����,�ҿ϶��ܿ���!
					al_filterHvs.add(hvs[i]); //
					continue; //
				}

				if (str_thisInstId.equals(_prinstanceid)) { //����Ǳ����̵�,��ֱ����ʾ,��������˾���������,��϶���ʾ�����̵�!
					al_filterHvs.add(hvs[i]); //
					continue; //
				} else { //�������һ��ͬһ������ʵ��!��
					if (str_fromDealPoolId != null && str_dealpoolId.equals(str_fromDealPoolId)) { //�����������������(fromparentdealpoolid��Ϊ��),�����������Ǳ�����ʵ���Ĵ�����,����Զ���ܿ�!���������̵ķ����˵����!
						al_filterHvs.add(hvs[i]); //
						continue; //
					}
					if (_prinstanceid.equals(str_thisParentInstId) && "EC".equals(str_lifecycle)) { //�����������ҵ�¼��Ա������������,�����ǽ����ļ�¼!��Ҳ��ʾ!
						al_filterHvs.add(hvs[i]); //
						continue; //
					}
				}

			}
			hvs = (HashVO[]) al_filterHvs.toArray(new HashVO[0]); //
		} else { //���������,����ʾ����,�����������������̵Ķ���ʾ!!

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
				String str_fromparentactivityBldeptGroup = ""; //��Դ���������Ĳ�����
				if (hvs[i].getStringValue("prinstanceid_fromparentactivity") != null) { //
					for (int j = 0; j < hvs_actsInfos.length; j++) {
						if (hvs_actsInfos[j].getStringValue("id").equals(hvs[i].getStringValue("prinstanceid_fromparentactivity"))) {
							str_fromparentactivityName = hvs_actsInfos[j].getStringValue("wfname"); //
							str_fromparentactivityisneedreport = hvs_actsInfos[j].getStringValue("isneedreport"); //�Ƿ����
							str_fromparentactivityBldeptGroup = hvs_actsInfos[j].getStringValue("belongdeptgroup"); //�����ĸ���
						}
					}

				}
				hvs[i].setAttributeValue("prinstanceid_fromparentactivityName", str_fromparentactivityName); //��Դ��������
				hvs[i].setAttributeValue("prinstanceid_fromparentactivityisneedreport", str_fromparentactivityisneedreport); //��Դ�����Ƿ����
				hvs[i].setAttributeValue("prinstanceid_fromparentactivitybldeptGroup", str_fromparentactivityBldeptGroup); //��Դ�����Ƿ����
			}
		}

		if (_isHiddenMsg) { //�����Ҫ����!����м��ܴ���!
			for (int i = 0; i < hvs.length; i++) { //���ܺ�Ὣ"submitmessage"�޸ĳ�"****",�����!�������˳�������Ա������ɫͨ���鿴����ǰ������,������Ҫ������ǰ�����ݽ�������!!
				hvs[i].setAttributeValue("submitmessage_real", hvs[i].getStringValue("submitmessage")); //����һ��!!
				hvs[i].setAttributeValue("submitmessagefile_real", hvs[i].getStringValue("submitmessagefile")); //����һ��!!
			}
			new WorkFlowBSUtil().dealHiddenMsg(hvs, _prinstanceid, str_loginUserid); //�������δ�����Ϣ,���������ŵ��˿��������沿�����
		} else {
			for (int i = 0; i < hvs.length; i++) { //
				hvs[i].setAttributeValue("submitmessage_viewreason", "��Ϊ����ʱû��ָ��Ҫ������,����ͳһ�ܱ��鿴!"); //
			}
		}

		//�ϲ���������!
		for (int i = 0; i < hvs.length; i++) {
			StringBuilder sb_text = new StringBuilder(); //
			String str_FromParentWFActivityName = hvs[i].getStringValue("prinstanceid_fromparentactivityName"); //�����̵Ļ�������
			String str_curractivityBLDeptName = hvs[i].getStringValue("curractivity_belongdeptgroup"); //��ǰ���ڵľ���
			String str_curractivityName = hvs[i].getStringValue("curractivity_wfname"); //��������
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
			hvs[i].setAttributeValue("curractivity_wfname2", str_activeNameSpan); //�ϲ��Ļ�������
		}

		//new TBUtil().writeHashToHtmlTableFile(hvs, "C:/pp12345.html", new String[] { "curractivity_wfname2", "realsubmitcorpname", "realsubmitername", "submittime", "submitmessage" }); //
		return hvs; //
	}

	/**
	 *  ȡ�����̼��ʱ���е�ִ�в���..
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
	 * ȡ��ĳ�����̵ĸ����̵Ĵ�����!�ڽ����Ƿ�ɱ༭��У���߼�ʱ��Ҫ! �������̵Ĵ�������Զ�ǿ��Ա༭����!
	 * @param _prinstanceId
	 * @return
	 */
	public String getRootInstanceCreater(String _prinstanceId) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvs_inst = commDMO.getHashVoArrayByDS(null, "select id,creater,rootinstanceid from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		if (hvs_inst != null && hvs_inst.length > 0) {
			String str_inst_id = hvs_inst[0].getStringValue("id"); //
			String str_rootinst_id = hvs_inst[0].getStringValue("rootinstanceid"); //
			if (str_inst_id.equals(str_rootinst_id) || str_rootinst_id == null) { //���������ʵ��id���ڸ�����ʵ��id,�������̾��Ǹ�����!(���������ʵ��Ϊ��,��˵�����ϰ汾!)
				return hvs_inst[0].getStringValue("creater"); //���̴�����!
			} else {
				return commDMO.getStringValueByDS(null, "select creater from pub_wf_prinstance where id='" + str_rootinst_id + "'"); //�ҳ������̵Ĵ�����!
			}
		} else {
			return null; //
		}
	}

	//�ж��Ƿ�����Ч������! �ǳ��ؼ��з���!! Ҳ��Ψһ�жϵ�¼���Ƿ��������Ĵ��������! ���û��,����ԭ��˵��!!!
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
	 * �ĵ���ˮӡ����ֻ����pub_filewatermark �����һ����¼�������ĵ���ʱ�����ʾˮӡ��
	 */
	public void addWatermark(String filename, String textwater, String picwater, String picposition) throws Exception {
		new WorkFlowEngineDMO().addWatermark(filename, textwater, picwater, picposition);
	}
}