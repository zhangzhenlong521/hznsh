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
 * ������״̬������DMO!!���Ĺؼ�!!������!!!
 * ��Ӧ�ĺ��ı���:pub_wf_prinstance(����ʵ����),pub_wf_dealpool(���̴����¼�ر�),
 * ��Ȩ����ָĳ�˳�����,����ʹ��ϵͳ��,Ϊ�˲�Ӱ�����������еĴ�������ϵͳ����һ����Ȩ����,��Ȩ������һ���ˣ�������������˽���ϵͳ����ܿ���������Ȩ����������˵�����!
 * 
 * ������������Ҫ������:
 * ��һ����getFirstTaskVO(),�����ύѡ����Աʱ�����������,���︴����Ҫ��Ҫ��̬���ݻ����������ɫ�������Ӧ����! ����"���̴����ߵ�{������}��{���ɺϹ沿}"
 * �ڶ�����secondCall(),������ѡ�е���,�ύ�����ݿ�!�γ��µ������������Ϣ,����ԭ�������������Ѱ�������!
 * ���е�һ���Ĵ����漰�����¹ؼ����뷽��,��������BS���ܹ��漰��4����:WorkFlowEngineDMO,WorkFlowBSUtil,WorkFlowEngineGetCorpUtil,DataPolicyDMO
 * WorkFlowEngineDMO.getFirstTaskVO()  //148��,�����һ��ѡ����!�����߼��������ȸ�������ͼ�����߼�������,�ҳ����Գ�ȥ����һ����,Ȼ�������������ϵĲ�����!
 * WorkFlowEngineDMO.getOneActivityAllParticipantUsers()  //182��,����ĳ�������ϵ����в�����,����������������˿�Ȩ�˵�ת��!!
 * WorkFlowEngineDMO.getOneActivityParticipanUserByCorpAreaAndRole();  //78��,ʵ���ϸ��ݻ������ɫ�Ľ���������䷶Χ�е�������Ա
 * WorkFlowBSUtil.getAccredProxyUserVO();  //31��,ȥ��Ȩ���е�ƥ����������������!
 * WorkFlowEngineGetCorpUtil().getCorps()  //41��,���ݹ�ʽ����������嵥,���������˻���,�״������������̽!
 * SysDMO.getOnerUserSomeTypeParentCorp()  //20��,����һ����Ա��ĳ�����������µ����л���
 * DataPolicyDMO.secondDownFindAllCorpChildrensByCondition()  //150��,�����ת����������Ȩ�޵��Ǹ���ӵ�"����/��̽"�ķ�����ȥ��
 * 
 * @author Administrator
 * 
 */
public class WorkFlowEngineDMO extends AbstractDMO {

	private CommDMO commDMO = null; //��ҪCommDMO

	private TBUtil tBUtil = null; //������!!
	private WorkFlowBSUtil wfBSUtil = null; //�������������˵Ĺ�����!!
	private WLTInitContext thisInitContext = null; //
	private HashMap allCorpsCacheMap = null; //
	private String sendSMSImpl = getTBUtil().getSysOptionStringValue("���������Žӿ�ʵ����", "");

	/**
	 * ��������
	 */
	public String startWFProcess(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO) throws Exception {
		return startWFProcesses(_processid, _billVO, _loginUserId, _startActivityVO, null, null); //
	}

	/**
	 * ����һ������,��Ϊ��һ������ʵ��Ϊ��,��������������Ҫ�ر�Դ�!! ����������ʵ���Ǵ���������ʵ����,����ִ��Start����!!!
	 */
	public String startWFProcesses(String _processid, BillVO _billVO, String _loginUserId, ActivityVO _startActivityVO, String _pkValue, String _tablename) throws Exception {
		WLTLogger.getLogger(this).debug("\r\n\r\n����һ������ʵ��[" + _processid + "]"); //
		String str_tablename = _billVO.getSaveTableName(); //��ѯ�ı���!!
		if (_tablename != null) {
			str_tablename = _tablename;
		}
		String str_pkValue = _billVO.getPkValue(); // ������ֵ!!!!
		if (_pkValue != null) {
			str_pkValue = _pkValue; //
		}
		String str_templetCode = _billVO.getTempletCode(); //ģ�����
		String str_queryTableName = _billVO.getQueryTableName(); //����ı���
		String str_pkfieldname = _billVO.getPkName(); // �����ֶ���!!!!

		//ǿ��ȡ��toString��ֵ!
		String str_billVOToStringField = getCommDMO().getStringValueByDS(null, "select tostringkey from pub_templet_1 where upper(templetcode)='" + str_templetCode.toUpperCase() + "'"); //�����еı��������������ò���Ч��,�ɴ�Ӻ�ֱ̨��ȡһ��!���������ܻ���һ��!
		_billVO.setToStringFieldName(str_billVOToStringField); //

		String str_newPrinstaceId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_PRINSTANCE"); //��ȡ������ʵ������ֵ!!!
		String str_sql_createPrInstance = getInsertPrinstanceSQL(str_newPrinstaceId, null, str_newPrinstaceId, _processid, str_templetCode, str_tablename, str_queryTableName, str_pkfieldname, str_pkValue, _loginUserId, _loginUserId, "" + _startActivityVO.getId(), null, null); //������ʵ����������һ����¼!!
		String str_sql_updateBillStatus = "update " + str_tablename + " set wfprinstanceid='" + str_newPrinstaceId + "' where " + _billVO.getPkName() + "='" + str_pkValue + "'"; //������ʵ��������д��ҵ�񵥾���!!

		//Ԥ���ڴ��������START���ڵ�����...��ֱ�������������в���һ����¼
		HashVO[] hvs_userCodeName = getCommDMO().getHashVoArrayByDS(null, "select code,name from pub_user where id='" + _loginUserId + "'"); //
		String str_loginUserCode = null;
		String str_loginUserName = null;
		if (hvs_userCodeName.length > 0) {
			str_loginUserCode = hvs_userCodeName[0].getStringValue("code"); //
			str_loginUserName = hvs_userCodeName[0].getStringValue("name"); //
		}
		String str_userCodeName = str_loginUserCode + "/" + str_loginUserName; //
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginUserId); //��¼��Ա�Ļ���id��name

		ActivityVO activityVO = _startActivityVO; //getStartActivityVO(_processid); //
		DealTaskVO dealTaskVO = new DealTaskVO(); //
		dealTaskVO.setFromActivityId(null); //��Ϊ��������,���Բ�֪���ĸ���������
		dealTaskVO.setTransitionId(null); //��Ϊ��������,���Բ�֪���ĸ���������
		dealTaskVO.setCurrActivityId("" + activityVO.getId()); //��ǰ����,����������!
		dealTaskVO.setCurrActivityApproveModel("1"); //��ռ
		dealTaskVO.setParticipantUserId(_loginUserId); //�����ߵ�ID
		dealTaskVO.setParticipantUserCode(str_loginUserCode); //
		dealTaskVO.setParticipantUserName(str_loginUserName); //
		dealTaskVO.setParticipantUserDeptId(str_loginUserDeptIdName[0]); //�����ߵĻ���
		dealTaskVO.setParticipantUserDeptName(str_loginUserDeptIdName[1]); //

		String str_currtime = getTBUtil().getCurrTime(); //��ǰʱ��
		String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_DEALPOOL"); //����ʵ��������!!
		String str_insertDealPool = getInsertDealPoolSQL(str_newDealPoolId, str_newPrinstaceId, null, str_newPrinstaceId, _processid, _loginUserId, str_loginUserName, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], dealTaskVO, "N", null, "��������", null, null, 1, str_currtime, null, null, "C", false, null, null, null, null); //��Ԥ�Ȳ�������,�ȴ���һ���޸���!

		//��������ʱ��Ҫ�����������Ҳ����һ����¼,Ȼ��ȴ�ʹ�ý����ź����SecondCall���������ҵ��Ѱ�����,�������������������ɴ��������Ѱ����п�������¼!!!!
		String str_insertTaskDeal = getInsertTaskDealSQL(str_newDealPoolId, str_newPrinstaceId, null, str_newPrinstaceId, null, _billVO.getTempletCode(), str_tablename, str_queryTableName, str_pkfieldname, str_pkValue, _billVO.toString(), "��������", "��������", _loginUserId, str_userCodeName, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], str_currtime, _loginUserId, str_userCodeName,
				str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], null, null, null, false); //

		System.out.println("\r\n��ʼִ���������̵�SQL............."); //
		getCommDMO().executeBatchByDS(null, new String[] { str_sql_createPrInstance, str_insertDealPool, str_insertTaskDeal, str_sql_updateBillStatus, }); //ִ������������,����������ʱ��Ҫ��4��sql,����3���ǲ������,����pub_wf_instance,pub_wf_dealpool,pub_task_del��3�ű��в���!
		System.out.println("ִ���������̵�SQL����..............\r\n"); //
		return str_newPrinstaceId; //��������ʵ������!!
	}

	/**
	 * ȡ�����п��������Ļ���!!!!!
	 * �����ж����������!!!!
	 * @return
	 */
	public ActivityVO[] getStartActivityVOs(String _processid, String _loginUserId) throws Exception {
		HashVO[] hvs_process = getCommDMO().getHashVoArrayByDS(null, "select code,name from pub_wf_process where id='" + _processid + "'"); //
		if (hvs_process == null || hvs_process.length == 0) {
			throw new WLTAppException("û���ҵ�����Ϊ[" + _processid + "]�Ĺ���������,��ȷ���Ƿ���ɾ����!!");
		}
		String str_processCode = hvs_process[0].getStringValue("code"); //
		String str_processName = hvs_process[0].getStringValue("name"); //
		String str_sql_findallstarts = "select * from pub_wf_activity where processid='" + _processid + "' and (activitytype='START' or canhalfstart='Y')"; //�ҳ����������Ļ��·�����Ļ���
		HashVO[] hvs_allstart = getCommDMO().getHashVoArrayByDS(null, str_sql_findallstarts); //
		TBUtil tbutil = new TBUtil(); //
		ArrayList al_ActivityVOS = new ArrayList(); //
		HashMap al_roles = new HashMap();
		StringBuffer sb_roles = new StringBuffer();
		for (int i = 0; i < hvs_allstart.length; i++) { //�������л���			
			ActivityVO activityVO = (ActivityVO) hvs_allstart[i].convertToRealOBJ(ActivityVO.class); //
			String str_activityType = activityVO.getActivitytype(); //��������!!
			String str_parroles = activityVO.getHalfstartrole(); //���԰�·�����Ľ�ɫ
			String str_pardepttype = activityVO.getHalfstartdepttype(); // ���԰�·�����Ļ�������, gaofeng
			if (str_parroles == null || str_parroles.trim().equals("")) { //���û����
				if ("START".equals(str_activityType)) { //�������������!!,��Ĭ����Ϊ�ǿ���������!
					al_ActivityVOS.add(activityVO); //��ֱ�Ӽ���,����������ѭ��
				}
			} else if (str_pardepttype != null && !str_pardepttype.trim().equals("")) {// gaofeng
				System.out.println("�����޶��˿��������Ļ�������,��ʼ����ƥ��....");
				String[] str_items = str_parroles.split(";"); //ȡ�����н�ɫ
				String[] str_depttypeitems = str_pardepttype.split(";");
				String str_depttypeincondition = tbutil.getInCondition(str_depttypeitems);
				String str_incondition = tbutil.getInCondition(str_items); //
				HashVO[] hvos = getCommDMO().getHashVoArrayByDS(null, "select * from pub_role where id in (" + str_incondition + ")"); //ȡ�����н�ɫ
				if (hvos.length > 0) {
					if (isAllCommRole(hvos)) { //��������и������"������Ա"��"һ���û�"��ֱ�ӿ��Է���!Ҳ����˵����ĳ���û�û��������������ɫ������!!
						al_ActivityVOS.add(activityVO); //��ֱ�Ӽ���,����������ѭ��
					} else {
						for (int j = 0; j < hvos.length; j++) {
							al_roles.put(hvos[j].getStringValue("code"), hvos[j].getStringValue("code")); //
						}
						String str_checkloginuser = getCommDMO().getStringValueByDS(null, "select count(*) c1 from pub_user_role where userid='" + _loginUserId + "' and roleid in (" + str_incondition + ")");
						//��Ҫ�жϵ�ǰ��½���û��Ƿ���ϻ������͵�����
						String str_sql = "select count(*) from pub_corp_dept where id in (select userdept from pub_user_post where userid=" + _loginUserId + " and corptype in (" + str_depttypeincondition + "))";
						String str_checkloginuserdepttype = getCommDMO().getStringValueByDS(null, str_sql);

						if (Integer.parseInt(str_checkloginuser) > 0 && Integer.parseInt(str_checkloginuserdepttype) > 0) { //���������ӵ������һ����ɫ,���������Ļ������ͷ����޶��Ļ�������,����Ϊ�ҿ��Դ���!
							al_ActivityVOS.add(activityVO);
						}
					}
				}
			} else { //��¼��Ա������ָ���Ļ�����!!
				String[] str_items = str_parroles.split(";"); //ȡ�����н�ɫ
				String str_incondition = tbutil.getInCondition(str_items); //
				HashVO[] hvos = getCommDMO().getHashVoArrayByDS(null, "select * from pub_role where id in (" + str_incondition + ")"); //ȡ�����н�ɫ
				if (hvos.length > 0) {
					if (isAllCommRole(hvos)) { //��������и������"������Ա"��"һ���û�"��ֱ�ӿ��Է���!Ҳ����˵����ĳ���û�û��������������ɫ������!!
						al_ActivityVOS.add(activityVO); //��ֱ�Ӽ���,����������ѭ��
					} else {
						for (int j = 0; j < hvos.length; j++) {
							al_roles.put(hvos[j].getStringValue("code"), hvos[j].getStringValue("code")); //
						}
						String str_checkloginuser = getCommDMO().getStringValueByDS(null, "select count(*) c1 from pub_user_role where userid='" + _loginUserId + "' and roleid in (" + str_incondition + ")");
						if (Integer.parseInt(str_checkloginuser) > 0) { //���������ӵ������һ����ɫ,����Ϊ�ҿ��Դ���!
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
			throw new WLTAppException("������������,ֻ�н�ɫΪ[" + sb_roles.toString() + "]����Ա�ſ�������������[" + str_processCode + "/" + str_processName + "]!"); //
		}
		return (ActivityVO[]) al_ActivityVOS.toArray(new ActivityVO[0]); //
	}

	/**
	 * �Ƿ���������Ա��ɫ
	 * @param hvos
	 * @return
	 */
	private boolean isAllCommRole(HashVO[] hvos) {
		for (int i = 0; i < hvos.length; i++) {
			if (hvos[i].getStringValue("code").equals("һ���û�") || //���������еĿͻ���������[һ��Ա��]������[һ���û�],�е�����ǡǡ�෴,����û�취ֻ��ʲô��ƥ����! ���ڽ�ɫ����ֻ��ͬʱ������һ��!
					hvos[i].getStringValue("code").equals("һ��Ա��") || //
					hvos[i].getStringValue("code").equals("һ����Ա") || //
					hvos[i].getStringValue("code").equals("��ͨ�û�") || //
					hvos[i].getStringValue("code").equals("��ͨԱ��") || //�ʴ���Ŀ�н���ͨԱ����xch/2012-08-15��
					hvos[i].getStringValue("code").equals("��ͨ��Ա") || //
					hvos[i].getStringValue("code").equals("������Ա")) { //
				return true;
			}
		}
		return false;
	}

	/**
	 * ȡ��һ����Ա��ĳ����ʵ���еĴ����������SQL,�����������������е�SQL,������ж�ĳ���˵ĵ�ǰ����!
	 * ���ĳ��������,������ֻ�᷵��һ����¼!!!!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 */
	private String getOneUserDealPoolTaskSQL(String _wfinstanceid, String _loginuserid) {
		return "select * from pub_wf_dealpool where prinstanceid='" + getTBUtil().getNullCondition(_wfinstanceid) + "' and (participant_user='" + _loginuserid + "' or participant_accruserid='" + _loginuserid + "') and issubmit='N' and isprocess='N'";
	}

	/**
	 * ȡ�õ�¼��Ա�����̴�����е�����,������Ӧ��ֻ��ȡ��һ��,��������뱻��������ͬһ����,��ǰ̨Ҳ����Ψһ�Թ���!!���Ա�Ȼֻ����һ����¼!!!!!
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
	 * ȡ�õ�¼��Ա��Ҫ����ļ�¼
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 * @throws Exception
	 */
	private HashVO getLoginUserDealRecord(String _wfinstanceid, String _prdealpoolId, String _loginuserid) throws Exception {
		HashVO[] hvs_instance = getCommDMO().getHashVoArrayByDS(null, "select status,creater,createactivity from pub_wf_prinstance where id='" + getTBUtil().getNullCondition(_wfinstanceid) + "'");
		if (hvs_instance.length == 0) {
			throw new WLTAppException("û���ҵ�ID=[" + _wfinstanceid + "]������ʵ��!");
		}
		if (hvs_instance[0].getStringValue("status", "").equals("END")) { //����ѽ���
			throw new WLTAppException("�����ѽ���,���ܽ����κδ���!");
		}

		HashVO hvo_judge_dealpool = judgeTaskDeal(_wfinstanceid, _prdealpoolId, _loginuserid); //�жϸ������Ƿ���Ч,�����Ч�򵯳�����ʾ!!! 		
		if (!hvo_judge_dealpool.getBooleanValue("�����Ƿ���Ч")) {
			throw new WLTAppException(hvo_judge_dealpool.getStringValue("ԭ��˵��")); //
		}

		String str_parentsql = "select * from pub_wf_dealpool where id='" + hvo_judge_dealpool.getStringValue("id") + "'"; //
		String str_childsql_prinstanceid = "select billtempletcode,billtablename,billquerytablename,billpkname,billpkvalue,creater,parentinstanceid,rootinstanceid from pub_wf_prinstance where id='{prinstanceid}'"; //������
		String str_childsql_rootprinstanceid = "select processid from pub_wf_prinstance where id={rootinstanceid}";
		String str_childsql_creater = "select code,name from pub_user where id='{creater}'"; //������
		String str_childsql_fromactivity = "select code,wfname,activitytype,belongdeptgroup from pub_wf_activity where id='{fromactivity}'"; //��ԴID
		String str_childsql_transition = "select code,wfname,uiname,dealtype from pub_wf_transition where id='{transition}'"; //����
		String str_childsql_curractivity = "select code,wfname,activitytype,belongdeptgroup,approvemodel,approvenumber,isassignapprover,canhalfend,canselfaddparticipate,iscanback,isneedmsg,isselfcycle,selfcyclerolemap,checkuserpanel,showparticimode,participate_user,participate_corp,participate_group,participate_dynamic,ccto_user,ccto_corp,ccto_role,intercept1,intercept2 from pub_wf_activity where id='{curractivity}'"; //��ǰ����
		//String str_childsql_submitcount = "select count(*) submitcount from pub_wf_transition where fromactivity='{curractivity}' and dealtype='SUBMIT'"; //�ύ��������
		//String str_childsql_rejectcount = "select count(*) rejectcount from pub_wf_transition where fromactivity='{curractivity}' and dealtype='REJECT'"; //�ܾ���������
		String str_childsql_participant_user = "select code,name from pub_user where id='{participant_user}'"; //������
		String str_childsql_submittouser = "select code,name from pub_user where id='{submittouser}'"; //�ύ��˭
		String str_childsql_submittotransition = "select code,wfname,uiname from pub_wf_transition where id='{submittotransition}'"; //�ύ��ȥ������
		String str_childsql_submittoactivity = "select code,wfname,uiname,belongdeptgroup from pub_wf_activity where id='{submittoactivity}'"; //�ύ��ȥ�Ļ���
		String str_childsql_createbyid = "select participant_user,participant_username,realsubmiter,realsubmitername,participant_accruserid,participant_accrusercode,participant_accrusername,batchno from pub_wf_dealpool where id='{createbyid}'"; //
		HashVO[] hvs_wfdealpool = getCommDMO().getHashVoArrayBySubSQL(null, str_parentsql,
				new String[] { str_childsql_prinstanceid, str_childsql_rootprinstanceid, str_childsql_creater, str_childsql_fromactivity, str_childsql_transition, str_childsql_curractivity, str_childsql_participant_user, str_childsql_submittouser, str_childsql_submittotransition, str_childsql_submittoactivity, str_childsql_createbyid }); //

		if (hvs_wfdealpool == null || hvs_wfdealpool.length == 0) {
			throw new WLTAppException("��ǰû����Ҫ���������!");
		}

		//�����������̫��,���ͻ���Ӧ!Ĭ��Ϊȥ������!!
		//		if (!hvs_instance[0].getStringValue("status", "").equals("START")) {
		//			if (hvs_wfdealpool[0].getStringValue("isreceive") == null || hvs_wfdealpool[0].getStringValue("isreceive").equalsIgnoreCase("N")) {
		//				throw new WLTAppException("���Ƚ�������!");
		//			}
		//		}

		//ȡ�����̴����ߵ�id,code,name����Ϣ!!
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
	 * ��֤�Ƿ��������ֻ�н�����������ܼ����ύ
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @return
	 * @throws Exception
	 */
	public boolean[] checkIsReceiveAndIsCCTo(String _wfinstanceid, String _loginuserid) throws Exception {
		String str_wfsatu = getCommDMO().getStringValueByDS(null, "select status from pub_wf_prinstance where id='" + _wfinstanceid + "'");
		if (str_wfsatu != null && str_wfsatu.equals("END")) {
			throw new WLTAppException("�����ѽ���,���ܽ����κδ���!");
		}
		boolean[] isReceiveAndIsCCTo = new boolean[] { true, false }; //�Ƿ�������Ƿ���!!!
		HashVO hvo_dealtask = getLoginUserDealPoolTask(_wfinstanceid, _loginuserid); //
		if (!"START".equals(str_wfsatu)) { //�����������ģʽ
			if (hvo_dealtask != null) {
				if (hvo_dealtask.getStringValue("isreceive") == null || hvo_dealtask.getStringValue("isreceive").equalsIgnoreCase("N")) { //
					isReceiveAndIsCCTo[0] = false;
				}

				if (hvo_dealtask.getStringValue("isccto") != null && hvo_dealtask.getStringValue("isccto").equalsIgnoreCase("Y")) { //��һ�д���,������"��"д����"��",����������д�����Ĵ������û�д������!!
					isReceiveAndIsCCTo[1] = true;
				}
			}

		}
		return isReceiveAndIsCCTo;
	}

	public boolean checkIsReceiveCancel(String _wfinstanceid, String _loginuserid) throws Exception {
		String str_wfsatu = getCommDMO().getStringValueByDS(null, "select status from pub_wf_prinstance where id='" + _wfinstanceid + "'");
		if (str_wfsatu != null && str_wfsatu.equals("END")) {
			throw new WLTAppException("�����ѽ���,���ܽ����κδ���!");
		}
		if (!str_wfsatu.equals("START")) {
			String str_parentsql = "select currowner,lastsubmiter from pub_wf_prinstance where id='" + _wfinstanceid + "'"; // �ҳ������ҵ�δ���������
			HashVO[] hvs_currowner = getCommDMO().getHashVoArrayByDS(null, str_parentsql); //
			String str_currowner = hvs_currowner[0].getStringValue("currowner"); //
			String str_lastsubmiter = hvs_currowner[0].getStringValue("lastsubmiter"); //����ύ��!!
			if (!_loginuserid.equals(str_lastsubmiter)) {
				String str_lastsubmiter_name = getCommDMO().getStringValueByDS(null, "select name from pub_user where id='" + str_lastsubmiter + "'"); //
				throw new WLTAppException("����ύ���ǡ�" + str_lastsubmiter_name + "��,ֻ�����ſ��Խ��г��ز���,�㲻�ܽ��г���!"); //
			}

			if (str_currowner == null || str_currowner.equals("")) {
				return true;
			} else {
				String newcurrowner = str_currowner.substring(1, str_currowner.length() - 1); //
				newcurrowner = new TBUtil().replaceAll(newcurrowner, ";", ","); //�����ǰ���в�����
				HashVO[] hashvo = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where  prinstanceid='" + _wfinstanceid + "' and  participant_user in (" + newcurrowner + ") and issubmit='N'"); //�ҳ����в�����,����û���ύ
				for (int i = 0; i < hashvo.length; i++) {
					if (hashvo[i].getStringValue("isreceive").equalsIgnoreCase("Y")) { //ֻҪ��һ��������,����Ϊ�ǽ�����!!
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

	/**
	 * �ж����������Ƿ�ɴ���!������Ҫ!! ����ʱ����Ϣ����������������񣬵��������ʱȴ����,��Ϊ���ܸû����ѹ��ˣ������Ǹ�������Ϣ��!!! ������Ҫһ��ʵ���ж��Ƿ�����Ч����ķ���!!
	 * ������ʱ�㶼��Ҫ�����������:
	 * һ�����������е������ťʱ
	 * �����ڵ��������е�����ύ����ťʱ(Ҳ����getFirstTaskVO()������),��Ϊ����������ڷ����Ĺ�����,���˿�����ռ������!
	 * ������ѡ������û������е����ȷ����ʱ(Ҳ����secondCall()������),��Ϊ��ѡ���û��ķ���������,���˿�����ռ������!
	 * �ɵĻ����Ǹ���ʵ��id,�µĻ����Ǹ�������id,��������id
	 * ���û����������Ч����,�򻹷���ԭ��,�����Ǳ����˴�����,���͵�,�ȵ�....
	 * @return
	 */
	public HashVO judgeTaskDeal(String _wfinstanceid, String _dealPoolId, String _loginuserid) throws Exception {
		HashVO returnHVO = null; //��������ֵ
		String str_key1 = "�����Ƿ���Ч"; //
		String str_key2 = "ԭ��˵��"; //
		String str_dealPoolId = _dealPoolId; //
		if (str_dealPoolId == null || str_dealPoolId.equals("")) { //���û�ж�����������ֻ������ʵ��,���ϵĻ���!!��ʱ�����ҳ���������,���Ұ�����Ӧ��ֻ���ҳ�һ������!!
			StringBuilder sb_sql = new StringBuilder(); //
			sb_sql.append("select id from pub_wf_dealpool "); //�����̴������������id
			sb_sql.append("where prinstanceid='" + getTBUtil().getNullCondition(_wfinstanceid) + "' "); //��Ӧ������ʵ��
			sb_sql.append("and (participant_user='" + _loginuserid + "' or participant_accruserid='" + _loginuserid + "') "); //�����߻�����ߵ���Ȩ�˵��ڵ�¼��Ա! �����Ժ�Ӧ�����жϲ���,���е�ģʽ����Ȩ���ǲ��ܴ����
			sb_sql.append("and issubmit='N' "); //û���ύ��,��Ϊ���ܴ��ڻ�ǩģʽ,�����ύ��,�������׶�����û�н���!!
			sb_sql.append("and isprocess='N' "); //û�й���
			String str_getDealPoolId = getCommDMO().getStringValueByDS(null, sb_sql.toString()); //�ϵĻ���ȥȡdealpoolId
			if (str_getDealPoolId == null || str_getDealPoolId.trim().equals("")) {
				returnHVO = new HashVO(); //��������ֵ
				returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //��������Ч����!
				returnHVO.setAttributeValue(str_key2, "�㲻�Ǹ����̵ĵ�ǰ������,�������̿������ύ��������������!"); //�����ϵĻ�����pub_wf_dealpool����û���ҵ�participant_user='��¼��' and issubmit='N' and isprocess='N'�ļ�¼,��û�������Ĵ�������!
				return returnHVO; //
			}
			str_dealPoolId = str_getDealPoolId; //
		}

		HashVO[] hvs_dealpool = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id='" + str_dealPoolId + "'"); //
		if (hvs_dealpool.length == 0) { //���û�ҵ�����
			returnHVO = new HashVO(); //��������ֵ
			returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //��������Ч����!
			returnHVO.setAttributeValue(str_key2, "����id[" + str_dealPoolId + "]û����pub_wf_dealpool�����ҵ�һ����¼,�����ǲ��Ի����б���ɾ������!"); //��������Ч����!
			return returnHVO; //
		} else {
			returnHVO = hvs_dealpool[0]; //ֱ�Ӹ�������ֵ!��ΪһЩ�ط��õ�!
			//����ҵ�������,��Ҫ��һЩ״̬!!!
			boolean isCCTo = returnHVO.getBooleanValue("isccto", false); //
			boolean isSubmit = returnHVO.getBooleanValue("issubmit", false); //�Ƿ��ύ��
			boolean isProcess = returnHVO.getBooleanValue("isprocess", false); //�Ƿ����,�������ռ,������ʲôԭ��ᵼ����ΪY
			if (isSubmit || isProcess) { //����ύ��,�ѱ������˴������
				if (isCCTo) { //����ǳ��͵�����
					returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //��������Ч����!
					returnHVO.setAttributeValue(str_key2, "���Ǹ�����ı�������,������д���,��֪Ϥ!\r\n�����ȷ����ť,���[ȷ��]�ɽ�������ת�Ƶ��Ѱ�������!"); //\r\n��������ˢ�´��������б�,��������ʾ��������!��������Ժ�ᾭ������,����ռģʽ��,���˽�����,�ٵ���ֻ�ܿ����ܴ���
				} else {
					returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //��������Ч����!
					returnHVO.setAttributeValue(str_key2, "�������ѹ���(���ѱ����������ύ�˻�����ʹ�ø��ʺ�����һ���ط�������),�����ڲ��ܽ��д���!\r\n�����ȷ����ť,���[ȷ��]�ɽ�������ת�Ƶ��Ѱ�������!"); //��������Ժ�ᾭ������,����ռģʽ��,���˽�����,�ٵ���ֻ�ܿ����ܴ���
				}
			} else { //�����������Ч
				if (isCCTo) { //����ǳ��͵�����
					returnHVO.setAttributeValue(str_key1, Boolean.FALSE); //��������Ч����!
					returnHVO.setAttributeValue(str_key2, "���Ǹ�����ı�������,������д���,��֪Ϥ!\r\n�����ȷ����ť,���[ȷ��]�ɽ�������ת�Ƶ��Ѱ�������!"); //
				} else { //��Ч����
					returnHVO.setAttributeValue(str_key1, Boolean.TRUE); //����Ϊ��Ч
					returnHVO.setAttributeValue(str_key2, null); //
				}
			}
			return returnHVO; //
		}
	}

	/**
	 * �ж�һ�������Ƿ������������!!! ��ǰ�����ǲ������߶�ʮһֱ�ӽ�����ʵ�����Ϊ����! ���������Ӷ�·�ύ�������,�Ͳ�Ӧ��������һ������!
	 * ��Ӧ����ÿ�ζ��Ǽ���һ��,��������ʵ���Ƿ��д����������ȴ��û�����(isprocess='N'),�����,��˵�������̲�û�н���!! 
	 * @return
	 */
	public boolean judgeWFisRealEnd(String _prinstanceid) throws Exception {
		String str_count = getCommDMO().getStringValueByDS(null, "select count(*) c1 from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and isprocess='N' and (isccto is null or isccto='N')"); //���Ƿ���δ�����!
		int li_count = Integer.parseInt(str_count); //
		if (li_count == 0) { //���һ����Ч����û����,��˵���ǿ��Խ�����!!�򷵻�true
			return true; //
		} else {
			return false;
		}
	}

	/**
	 * ��ѯʱ,�ɵĻ����ǵõ�һ��prinstanceid,�µĻ�����ͬʱ�õ�taskid,prdealpoolid,prinstanceid
	 * ��ǰ���߼��ǣ��ڵ������ťʱ,�ȼ���һ��,Ȼ�󵯳�һ���Ի���,��ʾ�����ѽ���,û�д�����������! Ȼ��Ͳ�������Ƭ��!!
	 * �µĻ�����ĳɣ�Ҳ���ȼ���һ��,����Ľ���ж�������,���и����˵��,����ǿ����ύ��,����ʾ�ύ��ť�������,�����������������ݾ��������ʾ��ʷ����б��밴ť!! �����ǳ��͵�,����ʾ�Ǳ������߲��ܴ���,����������ѱ����������ύ��,����ʾ! ����ѽ���,��Ҳ��Ӧ��ʾ��
	 * ֮�����ڵ�����Ƭ�Ľ�������ʾ,����Ϊ��ʹ��ʱ����˲����ύ(û�д����������),������Ȼ��Ҫ�鿴����Ϣ��������ʷ�������! ���Կ϶��ڵ�����������ʾ!! ��������Ǻ�ɫ��������������ʾ��
	 * �������ؼ�ʱ���ж��Ƿ����ʵ������ һ������������˵�����������ϵ������ť��һ�������ڵ��������е���ύ��ť(��getFirstTaskVO)!!! ֮�������ζ�Ҫ,����Ϊ�����ߺܿ����ڵ��������ܳ�ʱ���ڲ���,����ʱ���������ѱ������˴�����! ���Ա���ÿ�ζ����㣡����
	 * Ϊ������,Ӧ�������һ��ר�ŷ����жϵ�¼�û��Ĵ��������ʵ�ʿɴ������,�������µĻ�����,�����ܱ�������ɾ��,����Ҫ����ж�!! �����жϵĽ��˵��Ҫͨ���׶�!!! 
	 * �������ڵ���������ѡ�񴰿ں�,�ڵ��ȷ����ʽ�ύǰҲͬ��������һ���ж�(��SecondCall��ʱ��),��Ϊ�ڵ���ѡ���û����ں�,ͬ������"����",����ʱ����ͬ���ᱻ����������!!
	 */

	private void setFirstWFParVOFromHVO(WFParVO _firstWFParVO, HashVO _hvoRecord) {
		_firstWFParVO.setParentinstanceid(_hvoRecord.getStringValue("prinstanceid_parentinstanceid")); //��������ʵ��id
		_firstWFParVO.setRootinstanceid(_hvoRecord.getStringValue("prinstanceid_rootinstanceid")); //������ʵ��id

		_firstWFParVO.setBilltempletcode(_hvoRecord.getStringValue("prinstanceid_billtempletcode")); //ģ�����
		_firstWFParVO.setBilltablename(_hvoRecord.getStringValue("prinstanceid_billtablename")); //�����ҵ�����
		_firstWFParVO.setBillQueryTablename(_hvoRecord.getStringValue("prinstanceid_billquerytablename")); //��ѯ�ı���
		_firstWFParVO.setBillpkname(_hvoRecord.getStringValue("prinstanceid_billpkname")); //ҵ���������!
		_firstWFParVO.setBillpkvalue(_hvoRecord.getStringValue("prinstanceid_billpkvalue")); //ҵ�������ֵ!

		_firstWFParVO.setRejectedDirUp(_hvoRecord.getBooleanValue("isrejecteddirup", false)); //�Ƿ�ֱ���˻�???
		_firstWFParVO.setWfinstance_createuserid(_hvoRecord.getStringValue("wf_prinstance_creater_id")); //���̴����ߵ�ID
		_firstWFParVO.setWfinstance_createusercode(_hvoRecord.getStringValue("wf_prinstance_creater_code")); //���̴����ߵ�Code
		_firstWFParVO.setWfinstance_createusername(_hvoRecord.getStringValue("wf_prinstance_creater_name")); //���̴����ߵ�Name

		_firstWFParVO.setWfinstance_createactivityid(_hvoRecord.getStringValue("wf_prinstance_createactivity_id")); //���̴����ߵ�ID
		_firstWFParVO.setWfinstance_createactivitycode(_hvoRecord.getStringValue("wf_prinstance_createactivity_code")); //���̴������ڵ�Code
		//		_firstWFParVO.setWfinstance_createactivityname(_hvoRecord.getStringValue("wf_prinstance_createactivity_name")); //���̴������ڵ�Name

		//�޸��˻�ʱ���ɵ����񻷽�û�в��ŷ���/sunfujun/20121114
		String activityname_cre = _hvoRecord.getStringValue("wf_prinstance_createactivity_name", "");
		activityname_cre = activityname_cre.replaceAll("\r", "");
		activityname_cre = activityname_cre.replaceAll("\n", "");
		boolean ishavegroup = cn.com.infostrategy.bs.common.SystemOptions.getBooleanValue("�ύ�����Ƿ���ʾ���������������", true);
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
			_firstWFParVO.setWfinstance_createactivityname(activityname_cre); //������һ�����ڵ�����
		}

		_firstWFParVO.setWfinstance_createactivitytype(_hvoRecord.getStringValue("wf_prinstance_createactivity_type")); //���̴������ڵ�����

		_firstWFParVO.setDealpooltask_createuserid(_hvoRecord.getStringValue("creater")); //��������������Ĵ����ߵ�id,���ύ���ҵ��˵�ID
		_firstWFParVO.setDealpooltask_createusercode(_hvoRecord.getStringValue("creater_code")); //

		//��ǩ-��ͬ���Ż�ǩ,ͬ������ռ����׷�� �����/2013-04-25��
		_firstWFParVO.setCreatebyid(_hvoRecord.getStringValue("createbyid"));
		_firstWFParVO.setParticipant_userdept(_hvoRecord.getStringValue("participant_userdept"));

		String str_createby_participant_accruserid = _hvoRecord.getStringValue("createbyid_participant_accruserid"); //��Ȩ��
		if (str_createby_participant_accruserid != null && !str_createby_participant_accruserid.equals(_hvoRecord.getStringValue("creater"))) { //�������Ȩ��,���Ҵ����˲�����Ȩ��,���Ǳ���Ȩ��! ��ʱ��Ҫ������Ȩ��!
			String str_createuserName = _hvoRecord.getStringValue("creater_name"); //
			String str_accrUserCode = _hvoRecord.getStringValue("createbyid_participant_accrusercode"); //
			String str_accrUserName = _hvoRecord.getStringValue("createbyid_participant_accrusername"); //
			if (str_accrUserName != null && str_accrUserName.indexOf("/") > 0) {
				str_accrUserName = str_accrUserName.substring(str_accrUserName.indexOf("/") + 1, str_accrUserName.length()); //
			}
			_firstWFParVO.setDealpooltask_createusername(str_createuserName + "(" + str_accrUserName + "��Ȩ)"); //
			_firstWFParVO.setDealpooltask_createuser_accruserid(str_createby_participant_accruserid); //
			_firstWFParVO.setDealpooltask_createuser_accrusercode(str_accrUserCode); //
			_firstWFParVO.setDealpooltask_createuser_accrusername(str_accrUserName); //
		} else {
			_firstWFParVO.setDealpooltask_createusername(_hvoRecord.getStringValue("creater_name")); //
		}

		_firstWFParVO.setCurrParticipantUserName(_hvoRecord.getStringValue("participant_username")); //��ǰ������!!!��Ϊ������Ȩ��,���Բ�һ�����ǵ�¼��Ա

		_firstWFParVO.setProcessid(_hvoRecord.getStringValue("processid")); //���̶�������!!
		_firstWFParVO.setDealpoolid(_hvoRecord.getStringValue("id")); //���������ĳһ�����¼������!!������Ҫ�޸ĸü�¼��!
		_firstWFParVO.setDealpoolidCreatetime(_hvoRecord.getStringValue("createtime")); //����ʱ��

		//��һ��ȡ��,Ҫ��5���ֶθ�ֵ!!!ȡ����!
		_firstWFParVO.setDealpooltask_isselfcycleclick(_hvoRecord.getBooleanValue("isselfcycleclick", false)); //
		_firstWFParVO.setDealpooltask_selfcycle_fromrolecode(_hvoRecord.getStringValue("selfcycle_fromrolecode")); //
		_firstWFParVO.setDealpooltask_selfcycle_fromrolename(_hvoRecord.getStringValue("selfcycle_fromrolename ")); //
		_firstWFParVO.setDealpooltask_selfcycle_torolecode(_hvoRecord.getStringValue("selfcycle_torolecode")); //
		_firstWFParVO.setDealpooltask_selfcycle_torolename(_hvoRecord.getStringValue("selfcycle_torolename")); //

		//��ǰ�����Ǵ��ĸ���������!
		_firstWFParVO.setFromactivity(_hvoRecord.getStringValue("fromactivity")); //From����!!��ǰ�������Դ����
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
			_firstWFParVO.setFromactivityName(activityname_fro); //������һ�����ڵ�����
		}

		_firstWFParVO.setFromactivityType(_hvoRecord.getStringValue("fromactivity_activitytype")); //

		_firstWFParVO.setCurractivity(_hvoRecord.getStringValue("curractivity")); //��ǰ����Ļ���!!!!
		_firstWFParVO.setCurractivityCode(_hvoRecord.getStringValue("curractivity_code")); //��ǰ���ڱ���!
		//		_firstWFParVO.setCurractivityName(_hvoRecord.getStringValue("curractivity_wfname")); //��ǰ��������

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

		_firstWFParVO.setCurractivityBLDeptName(_hvoRecord.getStringValue("curractivity_belongdeptgroup")); //��ǰ���ڵ��������ž�������!
		_firstWFParVO.setCurractivityType(_hvoRecord.getStringValue("curractivity_activitytype")); //��ǰ��������!

		_firstWFParVO.setCurractivityCanhalfend(_hvoRecord.getBooleanValue("curractivity_canhalfend")); //�Ƿ���԰�·����
		_firstWFParVO.setCurractivityCanselfaddparticipate(_hvoRecord.getBooleanValue("curractivity_canselfaddparticipate")); //�Ƿ�����Լ���Ӳ�����

		_firstWFParVO.setCurractivityIsSelfCycle(_hvoRecord.getBooleanValue("curractivity_isselfcycle", false)); //��ǰ�����Ƿ���ѭ��
		_firstWFParVO.setCurractivitySelfCycleRoleMap(_hvoRecord.getStringValue("curractivity_selfcyclerolemap")); //��ǰ�����Ƿ���ѭ��

		_firstWFParVO.setCurractivityCheckUserPanel(_hvoRecord.getStringValue("curractivity_checkuserpanel")); //
		_firstWFParVO.setIntercept1(_hvoRecord.getStringValue("curractivity_intercept1")); //������1
		_firstWFParVO.setIntercept2(_hvoRecord.getStringValue("curractivity_intercept2")); //������2
		_firstWFParVO.setBatchno(_hvoRecord.getStringValue("batchno")); //����,����Ҫ
		_firstWFParVO.setApproveModel(_hvoRecord.getStringValue("curractivity_approvemodel"));
	}

	//��������VO�ҵ�������Ϊ������VO��ֵ
	private void setDealActivityVOFromTransitionVO(DealActivityVO _dealActivityVO, NextTransitionVO _nextTransitionVO) {
		_dealActivityVO.setSortIndex(_nextTransitionVO.getSortIndex()); //����
		_dealActivityVO.setFromActivityId(_nextTransitionVO.getFromactivityId()); //���ĸ���������!
		_dealActivityVO.setFromActivityCode(_nextTransitionVO.getFromactivityCode()); //���ĸ�����Code
		_dealActivityVO.setFromActivityName(_nextTransitionVO.getFromactivityWFName()); //��������

		_dealActivityVO.setFromTransitionId(_nextTransitionVO.getId()); //
		_dealActivityVO.setFromTransitionCode(_nextTransitionVO.getCode()); //
		_dealActivityVO.setFromTransitionName(_nextTransitionVO.getWfname()); //
		_dealActivityVO.setFromTransitionIntercept(_nextTransitionVO.getIntercept()); //
		_dealActivityVO.setFromTransitionMailsubject(_nextTransitionVO.getMailSubject()); //�ʼ�����
		_dealActivityVO.setFromTransitionMailcontent(_nextTransitionVO.getMailContent()); //�ʼ�����
		_dealActivityVO.setFromtransitiontype(_nextTransitionVO.getDealtype()); ////
		_dealActivityVO.setFromtransitionExtConfMap(_nextTransitionVO.getExtConfMap()); //���ĸ���������Map

		_dealActivityVO.setActivityId(_nextTransitionVO.getToactivityId()); //���ĸ�����
		_dealActivityVO.setActivityCode(_nextTransitionVO.getToactivityCode()); //
		_dealActivityVO.setActivityType(_nextTransitionVO.getToactivityType()); //��������
		_dealActivityVO.setActivityName(_nextTransitionVO.getToactivityWFName()); //
		_dealActivityVO.setActivityBelongDeptGroupName(_nextTransitionVO.getToactivityBelongDeptGroupName()); //�û��������������!! ��Ϊ�ڿ粿�Ż�������ʱ������ʾ����ʲô��!
		_dealActivityVO.setApprovemodel(_nextTransitionVO.getToactivityApprovemodel()); //
		_dealActivityVO.setActivityIselfcycle(_nextTransitionVO.getToactivityIsSelfCycle()); //
		_dealActivityVO.setActivitySelfcyclerolemap(_nextTransitionVO.getToactivitySelfCycleMap()); //

		_dealActivityVO.setShowparticimode(_nextTransitionVO.getToactivityShowparticimode()); //
		_dealActivityVO.setParticipate_user(_nextTransitionVO.getToactivityParticipate_user()); //
		_dealActivityVO.setParticipate_group(_nextTransitionVO.getToactivityParticipate_group()); //
		_dealActivityVO.setParticipate_corp(_nextTransitionVO.getToactivityParticipate_corp()); //����Ļ�������
		_dealActivityVO.setParticipate_dynamic(_nextTransitionVO.getToactivityParticipate_dynamic()); //
		_dealActivityVO.setCcto_user(_nextTransitionVO.getToactivityCcto_user()); //������Ա!!
		_dealActivityVO.setCcto_corp(_nextTransitionVO.getToactivityCcto_corp()); //���͵Ļ���!!
		_dealActivityVO.setCcto_role(_nextTransitionVO.getToactivityCcto_role()); //���͵Ľ�ɫ!!
		_dealActivityVO.setWnParam(getTBUtil().convertStrToMapByExpress(_nextTransitionVO.getToactivitywnparam(), ";", "="));
		_dealActivityVO.setCanselfaddparticipate(_nextTransitionVO.getToactivityCanselfaddparticipate());

	}

	/**
	 * ��ȡ�ύʱUI��Dialog��Ҫ�Ĳ���! ����Ǹû��ڵ��ս��ߣ�����Ҫ���� ���������ǰ������Ҫָ�������ˣ��򵯳���һ���ڵĲ����߸��û�ѡ��!
	 * ���������ǰ���ڱ�����������򵯳���������û�����! ���ؽ�������������һ����ֻ����ĳһ������,��һ���ǲ��⴦��ĳһ���񣬻�����ĳһ����
	 * ��ν����ĳһ���߾��ǽ����û��ڣ���״̬������һ���ڣ�ͬʱ�ڴ�����������Ϊ��һ��������ϵͳ����!
	 * 
	 * @return
	 */
	public WFParVO getFirstTaskVO(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptid, BillVO _billVO, String _dealtype) throws Exception {
		HashVO hvo_dealrecord = getLoginUserDealRecord(_wfinstanceid, _prdealPoolId, _loginuserid); //�õ�����������	
		//������Ӧ��ֻ���ҵ�һ������!!�������ҵ�����..
		WFParVO firstWFParVO = new WFParVO(); //
		firstWFParVO.setWfinstanceid(_wfinstanceid); //��������ʵ������!
		setFirstWFParVOFromHVO(firstWFParVO, hvo_dealrecord); //�Ƚ�ȡ�õ����̹ؼ���Ϣ����WFParVO!!

		//�ж��Ƿ����ս���,������ؼ���
		boolean bo_isLastSubmiter = isLastCommit(_wfinstanceid, hvo_dealrecord.getIntegerValue("batchno"), hvo_dealrecord.getStringValue("curractivity_approvemodel"), hvo_dealrecord.getIntegerValue("curractivity_approvenumber")); //�ڵ�һ��ȡ��ʱ���ж��Ƿ����ս���!
		if (bo_isLastSubmiter || "SEND".equals(_dealtype)) { //���������ύ��
			firstWFParVO.setIsprocessed(bo_isLastSubmiter); //����Ϊ�������ύ��!!! ������Ҫ!!! //sunfujun/20120810/�����ǩ����ת������� ���κŵ�����
			if (firstWFParVO.getCurractivityType() != null && firstWFParVO.getCurractivityType().equals("END") && !"SEND".equals(_dealtype)) { //�����ǰ������END,����������!��Ҫ���⴦��
				//����,����ȡ��һ��·��,����ʾ�ڽ��������ϲ�Ӧ�����߳�ȥ��!!!��ʹ����Ҳ��û��Ч����!!!!�м�!!!
			} else {
				//ArrayList v_taskVO = new ArrayList(); //
				//�������������֧,����Ŀ�껷�ڵ����в�����!!!������Ҫִ��ÿ�������ϵ�����!!!!����ǰ������ΪFromActivity����!!
				NextTransitionVO[] nextTransitionVOs = getNextTransitionVOs(firstWFParVO, _wfinstanceid, hvo_dealrecord.getStringValue("id"), hvo_dealrecord, _billVO, _dealtype); //ȡ���������������г�·!!
				if (nextTransitionVOs == null || nextTransitionVOs.length == 0) { //���û���ҵ�һ����·,��ֱ�ӱ���
					throw new WLTAppException("û���ҵ�һ����������λ���!��û��Ȩ�����д˲���!"); //
				}

				//��������д�������!
				HashMap map_dealactivity = new HashMap(); //
				boolean bo_iffindpasscondition = false; //���Ƿ��ҵ�һ����·
				//String str_notpassdesc = ""; //
				for (int i = 0; i < nextTransitionVOs.length; i++) { //����ÿ����·(������),�ҳ�ÿ����·Ŀ���ϵ����в�����
					if (!nextTransitionVOs[i].isPasscondition()) { //���������ͨ�����˳�!!
						continue; //
					} else {
						bo_iffindpasscondition = true; //
					}
					if (!map_dealactivity.containsKey(nextTransitionVOs[i].getToactivityId())) { //���ǵ���������֮���������˶����,���������ߵ�Ŀ�껷��һ��,����Ҫ��Ψһ�Դ���!
						DealActivityVO dealActivityVO = new DealActivityVO(); //
						setDealActivityVOFromTransitionVO(dealActivityVO, nextTransitionVOs[i]); //
						map_dealactivity.put(nextTransitionVOs[i].getToactivityId(), dealActivityVO); //
					}
				}

				if (!bo_iffindpasscondition || map_dealactivity.size() == 0) {
					throw new WLTAppException("û���ҵ�һ���������������λ���!��û��Ȩ�����д˲���!"); //
				}

				//Ϊ���������,��һ���ӽ�������Ȩ������ȡ����,�����ڼ��������ʱҪѭ�������õ�,������Ҫ�������!!!
				HashMap accreditAndProxyMap = getWFBSUtil().getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //ȡ����Ȩ���������Ϣ!!!!
				String str_accredModel = getAccrModel(_billVO); //ȡ�õ�ǰ���������Ȩģ��,����ͬ�ı����в�ͬ����Ȩģ��!!��ǰ�Ƿ���ÿ�������м����,�������м������ھͻ��㼸��,���ܽϵ�!! Ӧ��һ�������,Ȼ��ÿ����!!!

				//ȡ�����д��컷��!
				DealActivityVO[] allDealActs = (DealActivityVO[]) map_dealactivity.values().toArray(new DealActivityVO[0]); //
				for (int i = 0; i < allDealActs.length; i++) { //
					if ("SEND".equals(_dealtype)) {//�ʴ����ת��ֻ��ת�����������Ա�����Լ�/sunfujun/20120830
						String str_parMsg = "��ΪΪת��,����ֱ�ӱ����ų����Լ�����!###<br>"; //
						allDealActs[i].setParticiptMsg(str_parMsg);
						WorkFlowParticipantBean parBean = getOneActivityParticipanUserByCorpAreaAndRole(hvo_dealrecord.getStringValue("participant_user"), "getWFCorp(\"type=��¼�����ڻ���\",\"������̽�Ƿ��������=N\");", null, false);
						allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + "��" + parBean.getParticiptMsg()); //
						if (parBean.getParticipantUserBeans() != null && parBean.getParticipantUserBeans().length > 0) {
							LinkedHashMap hs_taskVOs = new LinkedHashMap(); ////
							WorkFlowParticipantUserBean[] parUserBeans = parBean.getParticipantUserBeans(); //
							for (int k = 0; k < parUserBeans.length; k++) { //����������Ա!!
								if (hvo_dealrecord.getStringValue("participant_user", "").equals(parUserBeans[k].getUserid())) {
									continue;
								}
								String str_identifyKey = parUserBeans[k].getUserid() + "#" + parUserBeans[k].getAccrUserid() + "#" + parUserBeans[k].isCCTo(); //��ͬ�Ľ�����Ա,��Ȩ��Ա,����ֻȡһ��!!
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
					if ("REJECT".equalsIgnoreCase(allDealActs[i].getFromtransitiontype())) { //���������,����ʾֻ���������߹���!!
						String str_parMsg = "��ΪΪ����,����ֱ�Ӽ���û����������߹�����!###<br>"; //
						allDealActs[i].setParticiptMsg(str_parMsg); //
						WorkFlowParticipantBean parBean = getOneActivityHistDealUserVOs(_wfinstanceid, allDealActs[i].getActivityId()); //ȡ��Ŀ�껷���������߹�����
						allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + "��" + parBean.getParticiptMsg()); //
						if (parBean.getParticipantUserBeans() != null && parBean.getParticipantUserBeans().length > 0) {
							LinkedHashMap hs_taskVOs = new LinkedHashMap(); ////
							WorkFlowParticipantUserBean[] parUserBeans = parBean.getParticipantUserBeans(); //
							for (int k = 0; k < parUserBeans.length; k++) { //����������Ա!!
								String str_identifyKey = parUserBeans[k].getUserid() + "#" + parUserBeans[k].getAccrUserid() + "#" + parUserBeans[k].isCCTo(); //��ͬ�Ľ�����Ա,��Ȩ��Ա,����ֻȡһ��!!
								if (!hs_taskVOs.containsKey(str_identifyKey)) { //
									DealTaskVO taskVO = getDealTaskVOByCompute(allDealActs[i], parUserBeans[k]); //�����������!!!
									hs_taskVOs.put(str_identifyKey, taskVO); //��Ψһ�Թ���!
								}
							}
							DealTaskVO[] taskVOs = (DealTaskVO[]) hs_taskVOs.values().toArray(new DealTaskVO[0]); //
							allDealActs[i].setDealTaskVOs(taskVOs); //��������!!!!

						}
					} else { //����������ύ,����ݻ����ϵĶ�����м���!!!
						String str_parMsg = "������Ա�Ķ���=��" + toEmpty(allDealActs[i].getParticipate_user()) + "��<br>��������Ķ���=��" + toEmpty(allDealActs[i].getParticipate_corp()) + "��  �����ɫ�Ķ���=��" + toEmpty(allDealActs[i].getParticipate_group()) + "��<br>��̬�����ߵĶ���=��" + toEmpty(allDealActs[i].getParticipate_dynamic()) + "��###<br>"; //
						allDealActs[i].setParticiptMsg(str_parMsg); //
						//ȡ��ĳһ�����ϵ����в�����!!!��һ�����ؽ�Ҫ!!!�ֱ�Ҫ���������!!����̬�������,��̬�������,��̬������
						WorkFlowParticipantBean[] parDeptUserBeans = getOneActivityAllParticipantUsers(_loginuserid, _loginUserDeptid, _billVO, hvo_dealrecord, accreditAndProxyMap, str_accredModel, allDealActs[i]); //��������ؼ��ļ���!��Ҫ���ĳ�������ϵ���!
						String str_sessionInfoMsg = getInitContext().getCurrSessionCustStrInfoByKey("$���ڲ����߼������", true); //
						if (str_sessionInfoMsg != null) {
							allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + str_sessionInfoMsg); //Ϊ�˸��õļ����μ���Ĺ���!!!��ʾ����������!!!
						}
						if (parDeptUserBeans != null && parDeptUserBeans.length > 0) { //���ȡ�ö��������,��һ��������!! һ�����͵Ĳ��붨��,����һ�������Bean,�����ж��!! ���о�������Ƕ����,Ӧ��ֱ�ӷ�����Ա�嵥����ˬ!!��WorkFlowParticipantUserBean[]
							LinkedHashMap hs_taskVOs = new LinkedHashMap(); ////
							for (int j = 0; j < parDeptUserBeans.length; j++) { //�������ߵĽ�����ڻ��ڵĽ����
								if (parDeptUserBeans[j].getParticiptMsg() != null) { //����д�����Ϣ!!
									allDealActs[i].setParticiptMsg(allDealActs[i].getParticiptMsg() + "��" + parDeptUserBeans[j].getParticiptMsg()); //
								}
								if (parDeptUserBeans[j].getParticipantUserBeans() != null) { //����в�����
									WorkFlowParticipantUserBean[] parUserBeans = parDeptUserBeans[j].getParticipantUserBeans(); //�û����ϵ����в������е�ĳһ���ο��ߵ����в�����Ա.
									for (int k = 0; k < parUserBeans.length; k++) {
										String str_identifyKey = parUserBeans[k].getUserid() + "#" + parUserBeans[k].getAccrUserid() + "#" + parUserBeans[k].isCCTo(); //������Ա,��Ȩ��Ա,����
										if (!hs_taskVOs.containsKey(str_identifyKey)) { //
											DealTaskVO taskVO = getDealTaskVOByCompute(allDealActs[i], parUserBeans[k]); //��������������,���л��ڵ���Ϣ,Ҳ�и��ݻ��ڲ����߶���������������Ա��Ϣ!!
											hs_taskVOs.put(str_identifyKey, taskVO); ////
										}
									}
								}
							} //end for
							DealTaskVO[] taskVOs = (DealTaskVO[]) hs_taskVOs.values().toArray(new DealTaskVO[0]); //
							//������Ҫ��������Ļ�����������!!!
							Arrays.sort(taskVOs, new DealTaskVOComparator()); //���ջ�����������һ��!!!����Ҫ,��Ϊ���˶��ʱ��,��������ˬ���!!!
							allDealActs[i].setDealTaskVOs(taskVOs); //���øû��ڵĴ�������!!
						}
					} // end normal submit
				}
				firstWFParVO.setDealActivityVOs(allDealActs); //��������Ŀ�껷��!!!
				if (hvo_dealrecord.getStringValue("curractivity_isassignapprover").equals("Y")) { // �����Ҫ�˹�ѡ�������!!����һ����ǣ�UI�˿��������Ǿ͵���һ���б�
					firstWFParVO.setIsassignapprover(true); //��Ҫ�˹�ѡ��������this.firstTaskVO
				} else { // ���������Ҫ�˹�ѡ�������,�򷵻����в�����.UI�����صĽ���Щ�����߼�ס!Ȼ��һ���ύ
					firstWFParVO.setIsassignapprover(false); //����Ҫ�˹�ѡ��������
				}
			}

			//����ҵ��̬���ι���
			busiDynSecondFilter(_wfinstanceid, _billVO.getStringValue("billtype"), _billVO.getStringValue("busitype"), _billVO, firstWFParVO.getDealActivityVOs()); //�������������ɵĲ������뻷�ڽ��ж���ҵ��̬����!!
		} else { //����Ҳ����ս���.��ֻ���޸ĵ�ǰ�������
			firstWFParVO.setIsprocessed(false); //�����ս���
		}

		if (hvo_dealrecord.getStringValue("curractivity_isneedmsg").equals("Y")) { // ���������������..
			firstWFParVO.setIsneedmsg(true); //
		} else {
			firstWFParVO.setIsneedmsg(false); //
		}

		return firstWFParVO; //
	}

	/**
	 * �˻ص�����һ���ĵ�һ��ȡ�õĲ�����
	 * �����߼���һ����ύ��һ��,�����߾ܾ�Ҳ��һ��.
	 * ���������������һ��Ĭ�ϻ���,û����ʹ��������������
	 * ��ĳ��������˵�Ժ��������Ҳ��ֻ��Ҫ�����������߶�����Ҫ���������,������ǻ��˵ĺ��壬���ҿ������˻ص�����һ������!!
	 * ��ǰ�˻ؽ������ύ����һ��,���г���������,Ȼ��һ��������һ��ҳǩ!!!
	 * @param _wfinstanceid
	 * @param _loginuserid
	 * @param _billVO
	 * @return
	 */
	public WFParVO getFirstTaskVO_Reject(String _wfinstanceid, String _prdealPoolId, String _loginuserid, String _loginUserDeptID, BillVO _billVO) throws Exception {
		//����ȷ�����ҵ�����!�������ͼ��������������,������Ҫ�Ż�!���������SQL
		HashVO hvo_dealrecord = getLoginUserDealRecord(_wfinstanceid, _prdealPoolId, _loginuserid); //

		//������Ӧ��ֻ���ҵ�һ������!!�������ҵ�����..
		WFParVO firstWFParVO = new WFParVO(); //
		firstWFParVO.setIsprocessed(true); //�˻�ʱ,���ܸû����Ƿ��ǻ�ǩģʽ,����Զ��������ռʽ�˻�
		firstWFParVO.setWfinstanceid(_wfinstanceid); //��������ʵ������!
		setFirstWFParVOFromHVO(firstWFParVO, hvo_dealrecord); //����ѯ�������йؼ���Ϣ������WFParVO

		//�����һ�������в�����,������ͨ�ύ��ʽ������������,����ͨ�ύ��ʽ�Ǹ�������ͼ�����ǰ���ڵ���һ���������ߵ�Ŀ�껷�ڵ����в����ߵĲ���,������ȴ��ȥ���̴�����ʷ�����ҳ����д�����ļ�¼!
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select "); //
		sb_sql.append("pub_wf_dealpool.prinstanceid,"); //
		sb_sql.append("pub_wf_dealpool.realsubmiter userid,"); //ʵ���ύ����!
		sb_sql.append("pub_wf_dealpool.realsubmitername username,"); //ʵ���ύ����!
		sb_sql.append("pub_wf_dealpool.participant_accruserid   accruserid,"); //��Ȩ��!
		sb_sql.append("pub_wf_dealpool.participant_accrusercode accrusercode,"); //��Ȩ��!
		sb_sql.append("pub_wf_dealpool.participant_accrusername accrusername,"); //��Ȩ��!
		sb_sql.append("pub_wf_dealpool.curractivity,"); //����
		sb_sql.append("pub_wf_activity.code curractivity_code,"); //���ڱ���
		sb_sql.append("pub_wf_activity.wfname curractivity_name,"); //��������
		sb_sql.append("pub_wf_activity.activitytype curractivity_type,"); //��������
		sb_sql.append("pub_wf_activity.belongdeptgroup curractivity_belongdeptgroup,");
		sb_sql.append("pub_wf_activity.approvemodel curractivity_approvemodel,"); //��������ģʽ.
		sb_sql.append("pub_wf_activity.participate_group,"); //��̬������.
		sb_sql.append("pub_wf_activity.participate_dynamic "); //��̬������.
		sb_sql.append("from pub_wf_dealpool "); //
		sb_sql.append("left outer join pub_wf_activity on pub_wf_dealpool.curractivity=pub_wf_activity.id "); //
		sb_sql.append("where prinstanceid='" + _wfinstanceid + "' and isprocess='Y' and issubmit='Y' order by pub_wf_dealpool.id"); //�ҳ������Ѿ����˵�,�����ύͬ���������ʷ��¼

		HashVO[] hvs_returnactivitys = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //ȡ�����п��Է��ػ����ϲ�����
		HashVO[] hvs_alluser = appendMyCorpAndRoleInfo(hvs_returnactivitys); //���Ͻ�ɫ�����,userdept,userdeptcode,userdeptname,roleid,rolecode,rolename
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
		boolean ishavegroup = cn.com.infostrategy.bs.common.SystemOptions.getBooleanValue("�ύ�����Ƿ���ʾ���������������", true);
		String groupname, activityname = null;
		for (int i = 0; i < hvs_returnactivitys.length; i++) { //������������!!
			if (firstWFParVO.getCurractivity().equals(hvo_dealrecord.getStringValue("curractivity")) && _loginuserid.equals(hvs_returnactivitys[i].getStringValue("userid"))) { //��������߾��ǵ�¼��Ա,�򲻳���!�������˻ظ��Լ�!
				continue; //���ֻ��ڵ��ڵ�ǰ����,��Ա���ڵ�ǰ��Ա,�������ڱ������˻ظ�����!!
			}
			String str_submituserid = hvs_returnactivitys[i].getStringValue("userid"); //
			//������ǰȡ��hvo_dealrecord.getStringValue("curractivity")�����¶������ͬһ������ˣ�ֻ���˻ص����˵�һ����˵Ļ��ڣ���̫ƽ��ҵ�������˻ر����˻ص�ĳ�����ڣ�
			//���޸�Ϊͬһ����ͬһ���˵ļ�¼���ظ����ɣ���Ҳ��������ǰ����ʦ����˼��ֻ����δ���ǵ�ͬһ��������������ڵ���������/2017-05-11��
			String str_identified_key = hvs_returnactivitys[i].getStringValue("curractivity") + "#" + str_submituserid;
			if (hs_identified.contains(str_identified_key)) {
				continue; //���������ͬ�Ļ�������,��ֻ��ʾһ��,���ظ��Ļ������˲����ֶ��!���硾�Ƴ����ڡ��Ƴ���ˡ��������������!
			}
			hs_identified.add(str_identified_key); //����,�����´ξͻ᲻�ظ�����
			DealTaskVO taskVO = new DealTaskVO(); //һ������.
			taskVO.setFromActivityId(hvo_dealrecord.getStringValue("curractivity")); //��ʲô��������ȥ��,���ǵ�ǰ����Ļ���!
			taskVO.setFromActivityName(hvo_dealrecord.getStringValue("curractivity_wfname")); //��ʲô��������ȥ��,���ǵ�ǰ����Ļ���!

			taskVO.setTransitionId(null); //��Ϊ��ֱ����ת,����û������
			taskVO.setTransitionCode(null); //��Ϊ��ֱ����ת,����û������
			taskVO.setTransitionName(null); //��Ϊ��ֱ����ת,����û������
			taskVO.setTransitionDealtype(null); //��Ϊ��ֱ����ת,����û������
			taskVO.setTransitionMailSubject(null); //��Ϊ��ֱ����ת,����û������.
			taskVO.setTransitionMailContent(null); //��Ϊ��ֱ����ת,����û������
			taskVO.setTransitionIntercept(null); //���ߵ�������,��Ϊ����ת,����û�о����κ�����,���Բ����κ�����������

			taskVO.setCurrActivityId(hvs_returnactivitys[i].getStringValue("curractivity")); //������һ�����ڵ�����
			taskVO.setCurrActivityCode(hvs_returnactivitys[i].getStringValue("curractivity_code")); //������һ�����ڵı���
			//			taskVO.setCurrActivityName(hvs_returnactivitys[i].getStringValue("curractivity_name")); //������һ�����ڵ�����

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
				taskVO.setCurrActivityName(activityname); //������һ�����ڵ�����
			}

			taskVO.setCurrActivityType(hvs_returnactivitys[i].getStringValue("curractivity_type")); //������һ�����ڵ�����
			taskVO.setCurrActivityApproveModel("1"); //�˻ظ��ı������ߵ�����ģʽĬ��Ӧ������ռ,��Ҳ�����ǻ�ǩ(������Ŀ�껷�ڵĶ�����ѡ)!! ����Ϊ�˻صĶ���˿��ܻᴦ�ڶ������,����ȡ�ĸ����ڵ�? ����˵�˻غ�,���µĵ�ǰ���ڵ�����˭?��ԭ���Ļ��ڣ����ǽ����ߵĻ��ڣ��������߻����Ƕ��,�������ĸ�?
			//����������ϸ˼��,�˻�ʱĬ��Ӧ����ռ,���ύ�߿��޸�,���������Ǹ��Ǹ��Ļ���,�������һ������,�������ռ�����һ�����Լ��Ļ����϶�������߳�ȥ,����ǻ�ǩ,�������һ���˰��Լ������϶�������߳�ȥ!�������˻�ʱ��Ӧ���л��������ģʽ!!��Ϊ������ģʽ��Զֻ֧������һ�����̵Ĵ���������!!!

			taskVO.setParticipantUserId(hvs_returnactivitys[i].getStringValue("userid")); //������ID
			String codeAndName = hvs_returnactivitys[i].getStringValue("username", "");//�����е�����sunfujun/20121114
			if (codeAndName.indexOf("/") >= 0) {
				taskVO.setParticipantUserCode(codeAndName.substring(0, codeAndName.indexOf("/") + 1));
				taskVO.setParticipantUserName(codeAndName.substring(codeAndName.indexOf("/") + 1, codeAndName.length()));
			} else {
				taskVO.setParticipantUserCode(codeAndName); //������Code
				taskVO.setParticipantUserName(codeAndName); //����������
			}
			taskVO.setParticipantUserDeptId(hvs_returnactivitys[i].getStringValue("userdept")); //������ID
			taskVO.setParticipantUserDeptCode(hvs_returnactivitys[i].getStringValue("userdeptcode")); //������Code
			taskVO.setParticipantUserDeptName(hvs_returnactivitys[i].getStringValue("userdeptname")); //����������

			taskVO.setParticipantUserRoleId(hvs_returnactivitys[i].getStringValue("userroleid")); //������ID
			taskVO.setParticipantUserRoleCode(hvs_returnactivitys[i].getStringValue("userrolecode")); //�����߽�ɫCode.
			taskVO.setParticipantUserRoleName(hvs_returnactivitys[i].getStringValue("userrolename")); //�����߽�ɫ����.

			String str_accruserid = hvs_returnactivitys[i].getStringValue("accruserid"); //��Ȩ��!
			if (str_accruserid != null && !str_accruserid.equals(str_submituserid)) { //�����Ȩ�˲�Ϊ��,������ʵ�ʴ����˲���ͬһ��!!
				taskVO.setAccrUserId(str_accruserid); //
				String str_accrusercode = hvs_returnactivitys[i].getStringValue("accrusercode");
				String str_accrusername = hvs_returnactivitys[i].getStringValue("accrusername");
				taskVO.setAccrUserCode(str_accrusercode); //
				taskVO.setAccrUserName(str_accrusername); //
				if (str_accrusername != null && str_accrusername.indexOf("/") > 0) {
					str_accrusername = str_accrusername.substring(str_accrusername.indexOf("/") + 1, str_accrusername.length()); //
				}
				taskVO.setParticipantUserName(taskVO.getParticipantUserName() + "(" + str_accrusername + "��Ȩ)"); //
			}

			taskVO.setSuccessParticipantReason("ֱ����תѡ���"); //
			list_taskVOs.add(taskVO); //
		}

		DealTaskVO[] dealTaskVOs = (DealTaskVO[]) list_taskVOs.toArray(new DealTaskVO[0]); ////

		//������Ȩ��Ϣ!�����ĳ�������ύ����������Ȩ����,���绪����Ȩ���������,��ô�����ǻ����ύ��,����ʱҲ��Ҫͬʱ�����˶��ύ����!!
		//Ϊ���������,��һ���ӽ�������Ȩ������ȡ����,�����ڼ��������ʱҪѭ�������õ�,������Ҫ�������!!!
		//		HashMap accreditAndProxyMap = getWFBSUtil().getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //ȡ����Ȩ���������Ϣ!!!!
		//		String str_accredModel = getAccrModel(_billVO); //ȡ�õ�ǰ���������Ȩģ��,����ͬ�ı����в�ͬ����Ȩģ��!!��ǰ�Ƿ���ÿ�������м����,�������м������ھͻ��㼸��,���ܽϵ�!! Ӧ��һ�������,Ȼ��ÿ����!!!
		//		if (accreditAndProxyMap.size() > 0) { //�����������Ȩ��Ϣ!!
		//			for (int i = 0; i < dealTaskVOs.length; i++) {
		//				String str_olduserid = dealTaskVOs[i].getParticipantUserId(); //ԭ��������Ĳ�����,
		//				if (accreditAndProxyMap.containsKey(str_olduserid)) { //�������û�������Ȩ!!
		//					HashVO hvo_accr = getWFBSUtil().getAccredProxyUserVO(accreditAndProxyMap, str_accredModel, str_olduserid);
		//					if (hvo_accr != null) { //ȡ����Ȩ���
		//						String str_oldUserCode = dealTaskVOs[i].getParticipantUserCode(); //ԭ����Ա����
		//						String str_oldUserName = dealTaskVOs[i].getParticipantUserName(); //ԭ����Ա����
		//						dealTaskVOs[i].setParticipantUserId(hvo_accr.getStringValue("proxyuserid")); //����λ,�Ǵ����˳��˴�����!
		//						dealTaskVOs[i].setParticipantUserCode(hvo_accr.getStringValue("proxyusercode")); //����λ,�Ǵ����˳��˴�����!
		//						dealTaskVOs[i].setParticipantUserName(str_oldUserName + "(����Ȩ" + hvo_accr.getStringValue("proxyusername") + ")"); //
		//
		//						dealTaskVOs[i].setParticipantUserDeptId(hvo_accr.getStringValue("proxyuserdeptid")); //
		//						dealTaskVOs[i].setParticipantUserDeptCode(hvo_accr.getStringValue("proxyuserdeptcode")); //
		//						dealTaskVOs[i].setParticipantUserDeptName(hvo_accr.getStringValue("proxyuserdeptname")); //
		//
		//						dealTaskVOs[i].setParticipantUserRoleId(hvo_accr.getStringValue("proxyuserroleid")); //
		//						dealTaskVOs[i].setParticipantUserRoleCode(hvo_accr.getStringValue("proxyuserrolecode")); //
		//						dealTaskVOs[i].setParticipantUserRoleName(hvo_accr.getStringValue("proxyuserrolename")); //
		//
		//						dealTaskVOs[i].setAccrUserId(str_olduserid); //��Ȩ��ID!
		//						dealTaskVOs[i].setAccrUserCode(str_oldUserCode); //��Ȩ�˱���!
		//						dealTaskVOs[i].setAccrUserName(str_oldUserName); //��Ȩ������!
		//					}
		//				}
		//			}
		//		}

		DealActivityVO dealActivityVO = new DealActivityVO(); //�˻ز���ʱ��һ������!Ŀǰ��Ƶ���û����Դ����,��Դ���ߵ�!! ������������Դ���ڻ����е�,������Ӧ��û��!
		dealActivityVO.setFromActivityId(hvo_dealrecord.getStringValue("curractivity")); //���ĸ���������!
		dealActivityVO.setFromActivityCode(hvo_dealrecord.getStringValue("curractivity_code")); //���ĸ�����Code
		dealActivityVO.setFromActivityName(hvo_dealrecord.getStringValue("curractivity_wfname")); //��������

		dealActivityVO.setFromTransitionId(null); //
		dealActivityVO.setFromTransitionCode(null); //
		dealActivityVO.setFromTransitionName(null); //
		dealActivityVO.setFromTransitionIntercept(null); //
		dealActivityVO.setFromTransitionMailsubject(null); //�ʼ�����
		dealActivityVO.setFromTransitionMailcontent(null); //�ʼ�����

		dealActivityVO.setActivityId(null); //����/Ŀ�껷�ڵ�id,��Ϊ���������
		dealActivityVO.setActivityCode(null); //����/Ŀ�껷�ڵ�Code
		dealActivityVO.setActivityName("ѡ������˻ص���Ա"); //
		dealActivityVO.setActivityType(null); //
		dealActivityVO.setApprovemodel(null); //����ģʽ
		dealActivityVO.setDealTaskVOs(dealTaskVOs); //�����˻ؽ����������Ա

		firstWFParVO.setDealActivityVOs(new DealActivityVO[] { dealActivityVO }); //

		if (hvo_dealrecord.getStringValue("curractivity_isassignapprover").equals("Y")) { // �����Ҫ�˹�ѡ�������!!����һ����ǣ�UI�˿��������Ǿ͵���һ���б�
			firstWFParVO.setIsassignapprover(true); //��Ҫ�˹�ѡ��������this.firstTaskVO
		} else { // ���������Ҫ�˹�ѡ�������,�򷵻����в�����.UI�����صĽ���Щ�����߼�ס!Ȼ��һ���ύ
			firstWFParVO.setIsassignapprover(false); //����Ҫ�˹�ѡ��������
		}

		if (hvo_dealrecord.getStringValue("curractivity_isneedmsg").equals("Y")) { // ���������������..
			firstWFParVO.setIsneedmsg(true); //
		} else {
			firstWFParVO.setIsneedmsg(false); //
		}
		return firstWFParVO; //
	}

	/**
	 * �ڶ����ύ,����ǰ̨������������
	 * @param _secondCallVO
	 * @param _loginuserid
	 */
	public BillVO secondCall(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		//���ж�һ��,��Ϊ��ȫ�п���������ѡ����Ա���������ʱ������ռ������!
		HashVO hvo_judge_dealpool = judgeTaskDeal(_secondCallVO.getWfinstanceid(), _secondCallVO.getDealpoolid(), _loginuserid); //
		if (!hvo_judge_dealpool.getBooleanValue("�����Ƿ���Ч")) {
			throw new WLTAppException(hvo_judge_dealpool.getStringValue("ԭ��˵��")); //
		}

		String str_loginusername = getUserCodeNameById(_loginuserid); //
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginuserid); //

		String str_billVOToStringField = getCommDMO().getStringValueByDS(null, "select tostringkey from pub_templet_1 where templetcode='" + _secondCallVO.getBilltempletcode() + "'"); //�����еı��������������ò���Ч��,�ɴ�Ӻ�ֱ̨��ȡһ��!���������ܻ���һ��!
		_billVO.setToStringFieldName(str_billVOToStringField); //
		_billVO.setObject("WFPRINSTANCEID", new StringItemVO(_secondCallVO.getWfinstanceid())); //��������ʵ������

		intercept2BeforeAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //ִ��������2��ǰ�ò���

		String str_dealpoolid = _secondCallVO.getDealpoolid(); //����,�����������һ������!!!!!!    ������Ҫ!!!
		String str_prinstanceid = _secondCallVO.getWfinstanceid(); //����ʵ������!!305
		String str_parentInstanceId = _secondCallVO.getParentinstanceid(); //������ʵ��ID
		String str_rootinstanceid = _secondCallVO.getRootinstanceid(); //������ʵ��Id
		String str_billTempletCode = _secondCallVO.getBilltempletcode(); //����ģ��
		String str_billtableName = _secondCallVO.getBilltablename(); //ҵ�����
		String str_billQueryTableName = _secondCallVO.getBillQueryTablename(); //����ı���
		String str_billPkName = _secondCallVO.getBillpkname(); //ҵ���������
		String str_billPkValue = _secondCallVO.getBillpkvalue(); //ҵ�������ֵ
		String str_processid = _secondCallVO.getProcessid(); //���̶�������!
		String str_fromactivity = _secondCallVO.getFromactivity(); //From��������
		String str_curractivity = _secondCallVO.getCurractivity(); //��ǰ��������!!!  ������Ҫ!!!
		String str_currbatchno = _secondCallVO.getBatchno(); //��ǰ����,�ؼ���Ϣ!
		String str_message = _secondCallVO.getMessage(); //�ύ���..
		String str_messagefile = _secondCallVO.getMessagefile(); //�ύ����ĸ���..
		String str_approveresult = getApproveResult(_dealtype); //�ύ����,���ύ��Y,�˻���N
		String str_submitreasoncode = getSubmitReasonCode(_secondCallVO); //�ύ���������,�������ԭ����˻�
		String str_currtime = getTBUtil().getCurrTime(); //��ǰʱ��

		DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //ȡ�����д���������,����ǰ̨ѡ�е�ʵ�ʲ�����!!!
		String[] str_submitTos = getSubmitToInfo(commitTaskVOs); //�ȼ���������ߵ���Ա,����,���ߵ���Ϣ,����Ҫ�õ�!!
		String str_submittouser = str_submitTos[0]; //�ύ��˭
		String str_submittousername = str_submitTos[1]; //�ύ��˭������
		String str_submittocorp = str_submitTos[2]; //�ύ���Ļ���!
		String str_submittocorpName = str_submitTos[3]; //�ύ���Ļ���!
		String submittotransition = str_submitTos[4]; //�ύ���ĸ����� 
		String submittoactivity = str_submitTos[5]; //�ύ���ĸ�����
		String submittoactivityName = str_submitTos[6]; //�ύ���ĸ����ڵ�����!

		int li_newBatchNo = 0;
		if (_secondCallVO.isIsprocessed()) { //������ս���,��Ҫ�����µ�����!
			li_newBatchNo = getNewBatchNo(str_prinstanceid); //ȡ���µ�����,����ǵ�һ���򷵻�1,���򷵻�ʵ�����еĵ�ǰ����+1
		} else {
			li_newBatchNo = Integer.parseInt(str_currbatchno);//sunfujun/20120810/��ǩ����ת��
		}

		ArrayList al_sqls = new ArrayList(); //����װ��SQL������!!!
		//�ؼ��߼�����,���������pub_wf_prinstance�޸���1��,��pub_wf_dealpool�޸���3��!!
		//�ȴ�������!!!�ؼ�,��ʵ���Ǹ���ҳ���,����pub_task_deal�еļ�¼�Ƶ�pub_task_off����
		String str_sql_insert_taskoff = getInsertTaskOffSQL(str_dealpoolid, _loginuserid, str_loginusername, str_currtime, str_message, str_submittouser, str_submittousername, str_submittocorp, str_submittocorpName, true, null); //
		if (str_sql_insert_taskoff != null) {
			al_sqls.add(str_sql_insert_taskoff); //
		}
		al_sqls.add("delete from pub_task_deal where prdealpoolid='" + str_dealpoolid + "'"); //ɾ����������!!!

		//�Ƚ��ո�����
		al_sqls.add("update pub_wf_dealpool set isreceive='Y',receivetime='" + str_currtime + "' where id='" + str_dealpoolid + "' and (isreceive is null or isreceive='N')"); //���û��û�н���,��ֱ�ӽ���,���ύ�������ͬһʱ��!

		//����ǰ����ʵ��.
		String[] str_instHistInfos = getNewSubmiterHist(str_prinstanceid, _loginuserid, str_currtime, str_message); //ȡ���µ��ύ�˵���ʷ�嵥,������ԭ���嵥�����ټ��ϱ���!
		String str_newsubmiterhist = str_instHistInfos[0]; //
		String str_mylastsubmittime = str_instHistInfos[1]; //
		String str_mylastsubmitmsg = str_instHistInfos[2]; //

		UpdateSQLBuilder isql_1 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_prinstanceid + "'"); //
		isql_1.putFieldValue("lastsubmiter", _loginuserid); //����ύ��
		isql_1.putFieldValue("lastsubmitername", str_loginusername); //����ύ������
		isql_1.putFieldValue("lastsubmitresult", str_approveresult); //����ύ���
		isql_1.putFieldValue("lastsubmitmsg", str_message); //����ύ�����
		isql_1.putFieldValue("lastsubmitactivity", str_curractivity); //����ύ�Ļ���
		isql_1.putFieldValue("lastsubmittime", str_currtime); //����ύ��ʱ��
		isql_1.putFieldValue("submiterhist", str_newsubmiterhist); //��ʷ�ύ����
		isql_1.putFieldValue("mylastsubmittime", str_mylastsubmittime); //�ҵ�����ύʱ��,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
		isql_1.putFieldValue("mylastsubmitmsg", str_mylastsubmitmsg); //�ҵ�����ύ���,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
		isql_1.putFieldValue("status", "RUN"); //
		if (_secondCallVO.isIsprocessed()) { //������ս���,��Ҫ���µ�ǰʵ����[��ǰ����,��ǰ������,��ǰ����]����Ϣ
			isql_1.putFieldValue("curractivity", submittoactivity); //��ǰ����
			isql_1.putFieldValue("curractivityname", submittoactivityName); //��ǰ��������
			isql_1.putFieldValue("currowner", str_submittouser); //��ǰ�Ĵ�����
			isql_1.putFieldValue("currbatchno", li_newBatchNo); //��ǰ����,
		}
		al_sqls.add(isql_1.getSQL()); //

		//���������
		if (str_rootinstanceid != null && !str_rootinstanceid.equalsIgnoreCase(str_prinstanceid)) { //��������̲����ڱ�����,��Ҫ�ڸ������м�¼����ʷ������!
			String[] str_rootInstHistInfos = getNewSubmiterHist(str_rootinstanceid, _loginuserid, str_currtime, str_message); //ȡ���µ��ύ�˵���ʷ�嵥,������ԭ���嵥�����ټ��ϱ���!
			String str_root_newsubmiterhist = str_rootInstHistInfos[0]; //
			String str_root_mylastsubmittime = str_rootInstHistInfos[1]; //
			String str_root_mylastsubmitmsg = str_rootInstHistInfos[2]; //
			UpdateSQLBuilder isql_2 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_rootinstanceid + "'"); //
			isql_2.putFieldValue("submiterhist", str_root_newsubmiterhist); //��ʷ�ύ����
			isql_2.putFieldValue("mylastsubmittime", str_root_mylastsubmittime); //�ҵ�����ύʱ��,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
			isql_2.putFieldValue("mylastsubmitmsg", str_root_mylastsubmitmsg); //�ҵ�����ύ���,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
			al_sqls.add(isql_2.getSQL()); //
		}

		//����ǰ��������.
		UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + str_dealpoolid + "'"); //
		isql_3.putFieldValue("issubmit", "Y"); //�Ƿ����ύ
		//isql_2.putFieldValue("isprocess", "Y"); //�Ƿ��ѹ�? �����Ҫ��?����ǻ�ǩ�Ļ�,Ҫ��Ҫ���Լ���������Ϊisprocess=Y??
		isql_3.putFieldValue("submittime", str_currtime); //�ύ��ʱ��
		isql_3.putFieldValue("realsubmiter", _loginuserid); //ʵ���ύ����!
		isql_3.putFieldValue("realsubmitername", str_loginusername); //ʵ���ύ�˵�����!
		isql_3.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //ʵ���ύ�Ļ���!
		isql_3.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //ʵ���ύ�Ļ�������!
		isql_3.putFieldValue("submitisapprove", str_approveresult); //��ͬ�⻹���˻�
		isql_3.putFieldValue("submitreasoncode", str_submitreasoncode); //�ύ��ԭ��������,�������ԭ����˻�!!
		isql_3.putFieldValue("submitmessage", str_message); //�ύ�����
		isql_3.putFieldValue("submitmessagefile", str_messagefile); //�ύ�ĸ���
		if (_secondCallVO.isIsprocessed()) { //������ս���
			isql_3.putFieldValue("isprocesser", "Y"); //���ս���!
			isql_3.putFieldValue("isprocess", "Y"); //����
			isql_3.putFieldValue("submittouser", str_submittouser); //�ύ��˭
			isql_3.putFieldValue("submittousername", str_submittousername); //�ύ��˭
			isql_3.putFieldValue("submittocorp", str_submittocorp); //�ύ���ĸ�����
			isql_3.putFieldValue("submittocorpname", str_submittocorpName); //�ύ���ĸ�����������
			isql_3.putFieldValue("submittotransition", submittotransition); //�ύ��ʲô����,���˷�֧ģʽ��,Ӧ�ÿ����ж��!
			isql_3.putFieldValue("submittoactivity", submittoactivity); //�ύ��ʲô����,���˷�֧ģʽ��,Ӧ�ÿ����ж��!
			isql_3.putFieldValue("submittobatchno", li_newBatchNo); //�ύ���ĸ�����!!
		} else {
			isql_3.putFieldValue("isprocesser", "N"); //�����ս���!
		}
		isql_3.putFieldValue("participant_yjbduserid", _secondCallVO.getDealpooltask_yjbduserid()); //������id
		isql_3.putFieldValue("participant_yjbdusercode", _secondCallVO.getDealpooltask_yjbdusercode()); //�����˱���
		isql_3.putFieldValue("participant_yjbdusername", _secondCallVO.getDealpooltask_yjbdusername()); //����������
		al_sqls.add(isql_3.getSQL()); //

		//������ս��ߣ�����Ҫ��ɸû���,ͬʱΪ��һ������������.(�Ƿ����ս������ڵ�һ�μ�������ʱȡ����!!!������ռ���ǩ�����һ��)
		if (_secondCallVO.isIsprocessed() || "SEND".equals(_dealtype)) {
			if (_secondCallVO.isIsprocessed() && !"4".equals(_secondCallVO.getApproveModel())) {
				al_sqls.add("update pub_wf_dealpool set isprocess='Y' where prinstanceid='" + str_prinstanceid + "' and batchno='" + str_currbatchno + "' and isprocess='N'"); //�޸ı�������,��ʾ���ڽ���!��������Ҫ���߼�֮һ!!! ������ս���,��û��������·������,����û�д���������,�����ζ����������ǿ��Խ�����!!!
				//��ǩ-��ͬ���Ż�ǩ,ͬ������ռ �����/2013-04-25��
				if (getTBUtil().getSysOptionBooleanValue("��ǩ�Ƿ����ò�ͬ���Ż�ǩ,ͬ������ռ", false)) {
					String str_parentinstanceid = _secondCallVO.getParentinstanceid();
					String str_createbyid = _secondCallVO.getCreatebyid();
					String str_participant_userdept = _secondCallVO.getParticipant_userdept();
					if (str_parentinstanceid != null && !str_parentinstanceid.equals("")) {
						al_sqls.add("update pub_wf_dealpool set isprocess='Y' where parentinstanceid='" + str_parentinstanceid + "' and createbyid = '" + str_createbyid + "' and batchno='1' and isprocess='N' and participant_userdept='" + str_participant_userdept + "'");
						al_sqls.add("update pub_wf_prinstance set status='END' where id in (select prinstanceid from pub_wf_dealpool where parentinstanceid='" + str_parentinstanceid + "' and createbyid = '" + str_createbyid + "' and batchno='1' and participant_userdept='" + str_participant_userdept + "' and prinstanceid<>'" + str_prinstanceid + "')");
					}
				}
			}
			if (commitTaskVOs != null) { //�������������Ϊ��!		
				//Ϊ��һ������������,�������µ�����!��Ϊ����������������,�����Եø�Ϊ����!!!
				for (int i = 0; i < commitTaskVOs.length; i++) { //Ϊ���������ߴ����µ�����!����pub_wf_dealpool���д���һ���µļ�¼!!!��������ߵĻ���������������,����ֱ�Ӵ���������!!!
					if ("3".equals(commitTaskVOs[i].getCurrActivityApproveModel()) && !commitTaskVOs[i].isCCTo()) { //�������ģʽ��3,��������,���Ҳ��ǳ��͵���Ϣ!!���Զ��ҳ��û��ڵ�������id,Ȼ��Ϊ֮����������ʵ��!!! ����������ʵ�ֵ���ؼ����߼�,����γ�ȥ!!!
						//���Ŀ�껷��������������,���Զ���Ŀ�껷���ϵļ���������Ϊ�����̵��������봴����,�Զ�����N��������ʵ��,Ȼ����Щ�����߽���ϵͳ��,��ǰ״̬�������̵���������,Ȼ���ύʱ����ֱ�����������̵�·��������!!!
						String str_childwfid = getCommDMO().getStringValueByDS(null, "select childwfcode from pub_wf_activity where id='" + commitTaskVOs[i].getCurrActivityId() + "'"); //
						if (str_childwfid == null || str_childwfid.trim().equals("")) {
							throw new WLTAppException("����[" + commitTaskVOs[i].getCurrActivityName() + "]������ģʽ�������̻��,��û�ж�������������!"); ////
						}

						String str_childwf_startactivityid = getCommDMO().getStringValueByDS(null, "select id from pub_wf_activity where processid='" + str_childwfid + "' and activitytype='START'"); //�ҳ��������е��������ڵ�id,����Ҫ,��Ϊ��������Ҫ֪����ǰ����,Ȼ��֪����δ�������ڿ�ʼ��������ȥ!
						if (str_childwf_startactivityid == null || str_childwf_startactivityid.trim().equals("")) {
							throw new WLTAppException("������[" + str_childwfid + "]û�ж���һ������ΪSTART�Ļ���,���Ҳ������ĸ����ڿ�ʼ�Զ�����!"); //
						}

						//����������ʵ��,������֮!!! ��ν��ĳһ��������һ������ʵ��,ʵ���Ͼ����� pub_wf_prinstance,pub_wf_dealpool,pub_task_deal�����ű��и�����һ����¼����!! ��������������,���ܻ�Ҫ��pub_task_deal���в�������!!�����ַ�ʽ������������̵���Ա��һ�������������в���,һ�������������в���!!!!!
						String str_newInstanceId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_PRINSTANCE"); //�µ�����ʵ������!
						String str_newInstanceSQL = getInsertPrinstanceSQL(str_newInstanceId, str_prinstanceid, str_rootinstanceid, str_childwfid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserId(), str_childwf_startactivityid, commitTaskVOs[i].getCurrActivityId(),
								str_dealpoolid); //����������ʵ��

						//Ϊ�����̴�����һ������,��������������ѡ�е���Щ��Ա!
						DealTaskVO dealTaskVO = new DealTaskVO(); //
						dealTaskVO.setFromActivityId(null); //��Ϊ��������,���Բ�֪���ĸ���������
						dealTaskVO.setTransitionId(null); //��Ϊ��������,���Բ�֪���ĸ���������
						dealTaskVO.setCurrActivityId(str_childwf_startactivityid); //��ǰ����,����������!
						dealTaskVO.setCurrActivityApproveModel(commitTaskVOs[i].getCurrActivityApproveModel()); //����ģʽ
						dealTaskVO.setParticipantUserId(commitTaskVOs[i].getParticipantUserId()); //�����ߵ�ID
						dealTaskVO.setParticipantUserCode(commitTaskVOs[i].getParticipantUserCode()); //����������
						dealTaskVO.setParticipantUserName(commitTaskVOs[i].getParticipantUserName()); //����������
						dealTaskVO.setParticipantUserDeptId(commitTaskVOs[i].getParticipantUserDeptId()); //�����߻���id
						dealTaskVO.setParticipantUserDeptName(commitTaskVOs[i].getParticipantUserDeptName()); //��������
						dealTaskVO.setParticipantUserRoleId(commitTaskVOs[i].getParticipantUserRoleId()); //��ɫid..
						dealTaskVO.setParticipantUserRoleName(commitTaskVOs[i].getParticipantUserRoleName()); //��ɫName
						dealTaskVO.setAccrUserId(commitTaskVOs[i].getAccrUserId()); //��Ȩ��
						dealTaskVO.setCCTo(commitTaskVOs[i].isCCTo()); //�Ƿ���!!!

						String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_DEALPOOL"); //����ʵ��������!!
						String str_newDealPoolSQL = getInsertDealPoolSQL(str_newDealPoolId, str_newInstanceId, str_prinstanceid, str_rootinstanceid, str_childwfid, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], dealTaskVO, "N", "Y", "", str_dealpoolid, null, 1, str_currtime, null, null, "CF", false, null, null, null, null); //

						//������ҳ��������!!
						String str_msg = getDealTaskMsg(_secondCallVO.getProcessid(), commitTaskVOs[i].getFromActivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_newDealPoolId, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //������Ϣ��ʾ,�ǿ����õ�!
						String str_newTaskDealSQL = getInsertTaskDealSQL(str_newDealPoolId, str_newInstanceId, str_parentInstanceId, str_rootinstanceid, str_dealpoolid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, _billVO.toString(), str_message, str_msg, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1],
								str_currtime, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserName(), commitTaskVOs[i].getParticipantUserDeptId(), commitTaskVOs[i].getParticipantUserDeptName(), dealTaskVO.getAccrUserId(), _secondCallVO.getPrioritylevel(), _secondCallVO.getDealtimelimit(), commitTaskVOs[i].isCCTo()); //��������SQL
						al_sqls.add(str_newInstanceSQL); //����������
						al_sqls.add(str_newDealPoolSQL); //���������̵���������
						al_sqls.add(str_newTaskDealSQL); //������ҳ����SQL.

						//��д��������������ڵ�״̬ΪCC
						al_sqls.add("update pub_wf_dealpool set lifecycle='CC' where id='" + str_dealpoolid + "'"); //��¼��������������������Ǵ�����������!!
					} else { //�������������,����ռ���ǩģʽ,����ԭ�����߼�!!��������������pub_wf_dealpool��pub_task_deal���ű��в�������!!
						//���ɴ���������.
						String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_dealpool"); //
						String str_sql_insert_dealpool = getInsertDealPoolSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_processid, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], commitTaskVOs[i], "N", null, null, str_dealpoolid, null, li_newBatchNo, str_currtime, _secondCallVO.getPrioritylevel(), _secondCallVO
								.getDealtimelimit(), null, _secondCallVO.isSecondIsSelfcycleclick(), _secondCallVO.getSecondSelfcycle_fromrolecode(), _secondCallVO.getSecondSelfcycle_fromrolename(), _secondCallVO.getSecondSelfcycle_torolecode(), _secondCallVO.getSecondSelfcycle_torolename()); //
						al_sqls.add(str_sql_insert_dealpool);

						//������ҳ����������
						String str_msg = getDealTaskMsg(_secondCallVO.getProcessid(), commitTaskVOs[i].getFromActivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_newDealPoolId, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //������Ϣ��ʾ,�ǿ����õ�!
						String str_sql_insert_dealtask = getInsertTaskDealSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_dealpoolid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, _billVO.toString(), str_message, str_msg, _loginuserid, str_loginusername, str_loginUserDeptIdName[0],
								str_loginUserDeptIdName[1], str_currtime, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserName(), commitTaskVOs[i].getParticipantUserDeptId(), commitTaskVOs[i].getParticipantUserDeptName(), commitTaskVOs[i].getAccrUserId(), _secondCallVO.getPrioritylevel(), _secondCallVO.getDealtimelimit(), commitTaskVOs[i].isCCTo()); //��ҳ��������!!!
						al_sqls.add(str_sql_insert_dealtask); //
					} //�������̴�����������!!!
				} //end ��for (int i = 0; i < commitTaskVOs.length; i++)��
			} //end ��if (commitTaskVOs != null)�� 
		} //enc ��if (_secondCallVO.isIsprocessed())��

		getCommDMO().executeBatchByDS(null, al_sqls); //ִ��������SQL,��һ�������������������,��ֻҪ����������ͻ��������,������:create index in_pub_wf_dealpool_3 on pub_wf_dealpool (prinstanceid,batchno,ispass);

		//�����ǰ������END����,������ʵ���޸ĳ�END??  �����ܽ����Ļ��ڵ���������˽�������!!! �����󻷽ڵ�������[END]�����ύʱ�Ͳ�����ѡ���û���,��ֱ����ʾ,�������һ����!!
		if (_secondCallVO.getCurractivityType() != null && _secondCallVO.getCurractivityType().equalsIgnoreCase("END")) { //����ǽ������͵Ļ���,���������һ�����ս���,������ʵ���޸ĳ�END
			boolean isCanRealEnd = judgeWFisRealEnd(str_prinstanceid); //�жϱ������Ƿ����ʵ�ʽ���,�������ʵ�ʽ���,�������������!!!
			if (isCanRealEnd) { //������������Խ���,����������!!!
				al_sqls.add("update pub_wf_prinstance set status='END' where id='" + str_prinstanceid + "'"); ////
			}
		}

		//ִ�������ϵĹ�ʽ,����ִ�и��������ϵĹ�ʽ!!!!�ؼ�֮�ؼ�!!!��RouteMark�ȹ���Ч���ͱ���ʹ����ʵ��!!
		execTransitionIntercept(_secondCallVO, str_prinstanceid, str_dealpoolid, _billVO); //

		//���ʼ�,sfj�����޸�....
		sendMail(_secondCallVO, _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message); //

		//ִ�л����ϵ�������2..
		intercept2AfterAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //ִ��������2�ĺ��ò���

		return _billVO; //
	}

	/**
	 * �˻ز���,��������! �����и�С��������˻ظ��ύ��ʱ,û��ȡ�ý����ߵĻ�����Ϣ!!
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @return
	 * @throws Exception
	 */
	public BillVO secondCall_Reject(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO) throws Exception {
		HashVO hvo_judge_dealpool = judgeTaskDeal(_secondCallVO.getWfinstanceid(), _secondCallVO.getDealpoolid(), _loginuserid); //
		if (!hvo_judge_dealpool.getBooleanValue("�����Ƿ���Ч")) {
			throw new WLTAppException(hvo_judge_dealpool.getStringValue("ԭ��˵��")); //
		}

		String str_loginusername = getUserCodeNameById(_loginuserid); //ȡ�õ�¼��Ա����,�ǡ�003/�������ĸ�ʽ,��Ϊ���ǵ�����!
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginuserid); //
		String str_billVOToStringField = getCommDMO().getStringValueByDS(null, "select tostringkey from pub_templet_1 where upper(templetcode)='" + _secondCallVO.getBilltempletcode().toUpperCase() + "'"); //�����еı��������������ò���Ч��,�ɴ�Ӻ�ֱ̨��ȡһ��!���������ܻ���һ��!
		_billVO.setToStringFieldName(str_billVOToStringField); //
		_billVO.setObject("WFPRINSTANCEID", new StringItemVO(_secondCallVO.getWfinstanceid())); //��������ʵ������??ΪʲôҪ��һ��?�ѵ���һ��ʼû��?

		intercept2BeforeAction(_secondCallVO, _loginuserid, _billVO, "REJECT");
		//		WFIntercept2IFC intercept2 = null;
		//		if (_secondCallVO.getIntercept2() != null && !_secondCallVO.getIntercept2().trim().equals("")) {
		//			intercept2 = (WFIntercept2IFC) Class.forName(_secondCallVO.getIntercept2()).newInstance(); //����������2
		//		}
		//		//ִ��ǰ��������
		//		if (intercept2 != null) {
		//			intercept2.beforeAction(_secondCallVO, _loginuserid, _billVO, "REJECT"); //
		//		}
		String str_prinstanceid = _secondCallVO.getWfinstanceid(); //����ʵ������!!
		String str_parentInstanceId = _secondCallVO.getParentinstanceid(); //������ʵ��ID
		String str_rootinstanceid = _secondCallVO.getRootinstanceid(); //������ʵ��Id
		String str_billTempletCode = _secondCallVO.getBilltempletcode(); //����ģ��
		String str_billtableName = _secondCallVO.getBilltablename(); //ҵ�����
		String str_billQueryTableName = _secondCallVO.getBillQueryTablename(); //����ı���
		String str_billPkName = _secondCallVO.getBillpkname(); //ҵ���������
		String str_billPkValue = _secondCallVO.getBillpkvalue(); //ҵ�������ֵ
		String str_processid = _secondCallVO.getProcessid(); //���̶�������!
		String str_dealpoolid = _secondCallVO.getDealpoolid(); //����
		String str_fromactivity = _secondCallVO.getFromactivity(); //From��������
		String str_curractivity = _secondCallVO.getCurractivity(); //��ǰ��������
		String str_batchno = _secondCallVO.getBatchno(); //ԭ��������
		String str_message = _secondCallVO.getMessage(); //�������!!
		String str_messagefile = _secondCallVO.getMessagefile(); //���︽��..
		String str_approveresult = "N"; //��Ϊ���˻�,����ͬ����Ϊ��!
		String str_currtime = getTBUtil().getCurrTime(); //��ǰʱ��!

		DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //ȡ�����д���������,����ǰ̨ѡ�е�ʵ�ʲ�����!!!
		String[] str_submitTos = getSubmitToInfo(commitTaskVOs); //�ȼ���������ߵ���Ա,����,���ߵ���Ϣ,����Ҫ�õ�!!
		String str_submittouser = str_submitTos[0]; //�ύ��˭
		String str_submittousername = str_submitTos[1]; //�ύ��˭������
		String str_submittocorp = str_submitTos[2]; //�ύ���Ļ���!
		String str_submittocorpName = str_submitTos[3]; //�ύ���Ļ���!
		String submittotransition = str_submitTos[4]; //�ύ���ĸ����� 
		String submittoactivity = str_submitTos[5]; //�ύ���ĸ�����
		String submittoactivityName = str_submitTos[6]; //�ύ���ĸ����ڵ�����!

		int li_newBatchNo = 0;
		if (_secondCallVO.isIsprocessed()) { //������ս���,��Ҫ�����µ�����!
			li_newBatchNo = getNewBatchNo(str_prinstanceid); //ȡ���µ�����,����ǵ�һ���򷵻�1,���򷵻�ʵ�����еĵ�ǰ����+1
		}

		ArrayList al_sqls = new ArrayList(); //�洢���е�SQL
		//�ؼ��߼�����,���������pub_wf_prinstance�޸���1��,��pub_wf_dealpool�޸���3��!!
		//�ȴ�������!!!�ؼ�,��ʵ���Ǹ���ҳ���,����pub_task_deal�еļ�¼�Ƶ�pub_task_off����
		String str_sql_insert_taskoff = getInsertTaskOffSQL(str_dealpoolid, _loginuserid, str_loginusername, str_currtime, str_message, str_submittouser, str_submittousername, str_submittocorp, str_submittocorpName, true, null); //
		if (str_sql_insert_taskoff != null) {
			al_sqls.add(str_sql_insert_taskoff); //
		}
		al_sqls.add("delete from pub_task_deal where prdealpoolid='" + str_dealpoolid + "'"); //ɾ����������!!!

		//�Ƚ���!!
		al_sqls.add("update pub_wf_dealpool set isreceive='Y',receivetime='" + str_currtime + "' where id='" + str_dealpoolid + "' and isreceive='N'"); //���û��û�н���,��ֱ�ӽ���,���ύ�������ͬһʱ��!

		//����ǰ����ʵ��.
		String[] str_instHistInfos = getNewSubmiterHist(str_prinstanceid, _loginuserid, str_currtime, str_message); //ȡ���µ��ύ�˵���ʷ�嵥,������ԭ���嵥�����ټ��ϱ���!
		String str_newsubmiterhist = str_instHistInfos[0]; //������ʷ
		String str_mylastsubmittime = str_instHistInfos[1]; //�ҵ������ʱ��!
		String str_mylastsubmitmsg = str_instHistInfos[2]; //�ҵ���������!

		UpdateSQLBuilder isql_1 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_prinstanceid + "'"); //
		isql_1.putFieldValue("lastsubmiter", _loginuserid); //����ύ��
		isql_1.putFieldValue("lastsubmitername", str_loginusername); //����ύ������
		isql_1.putFieldValue("lastsubmitresult", str_approveresult); //����ύ���
		isql_1.putFieldValue("lastsubmitmsg", str_message); //����ύ�����
		isql_1.putFieldValue("lastsubmitactivity", str_curractivity); //����ύ�Ļ���
		isql_1.putFieldValue("lastsubmittime", str_currtime); //����ύ��ʱ��
		isql_1.putFieldValue("submiterhist", str_newsubmiterhist); //��ʷ�ύ����
		isql_1.putFieldValue("mylastsubmittime", str_mylastsubmittime); //�ҵ�����ύʱ��,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
		isql_1.putFieldValue("mylastsubmitmsg", str_mylastsubmitmsg); //�ҵ�����ύ���,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
		isql_1.putFieldValue("status", "RUN"); //
		if (_secondCallVO.isIsprocessed()) { //������ս���,��Ҫ���µ�ǰʵ����[��ǰ����,��ǰ������,��ǰ����]����Ϣ
			isql_1.putFieldValue("curractivity", submittoactivity); //��ǰ����
			isql_1.putFieldValue("curractivityname", submittoactivityName); //��ǰ��������
			isql_1.putFieldValue("currowner", str_submittouser); //��ǰ�Ĵ�����
			isql_1.putFieldValue("currbatchno", li_newBatchNo); //��ǰ����,
		}
		al_sqls.add(isql_1.getSQL()); //

		//���������
		if (!str_rootinstanceid.equalsIgnoreCase(str_prinstanceid)) { //��������̲����ڱ�����,��Ҫ�ڸ������м�¼����ʷ������!
			String[] str_rootInstHistInfos = getNewSubmiterHist(str_rootinstanceid, _loginuserid, str_currtime, str_message); //ȡ���µ��ύ�˵���ʷ�嵥,������ԭ���嵥�����ټ��ϱ���!
			String str_root_newsubmiterhist = str_rootInstHistInfos[0]; //
			String str_root_mylastsubmittime = str_rootInstHistInfos[1]; //
			String str_root_mylastsubmitmsg = str_rootInstHistInfos[2]; //
			UpdateSQLBuilder isql_2 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + str_rootinstanceid + "'"); //
			isql_2.putFieldValue("submiterhist", str_root_newsubmiterhist); //��ʷ�ύ����
			isql_2.putFieldValue("mylastsubmittime", str_root_mylastsubmittime); //�ҵ�����ύʱ��,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
			isql_2.putFieldValue("mylastsubmitmsg", str_root_mylastsubmitmsg); //�ҵ�����ύ���,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
			al_sqls.add(isql_2.getSQL()); //
		}

		//����ǰ����,���޸����������
		UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + str_dealpoolid + "'"); //
		isql_3.putFieldValue("issubmit", "Y"); //
		isql_3.putFieldValue("submittime", str_currtime); //
		isql_3.putFieldValue("realsubmiter", _loginuserid); //ʵ���ύ����!
		isql_3.putFieldValue("realsubmitername", str_loginusername); //ʵ���ύ�˵�����!
		isql_3.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //ʵ���ύ�Ļ���!
		isql_3.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //ʵ���ύ�Ļ���!
		isql_3.putFieldValue("submitisapprove", str_approveresult); //
		isql_3.putFieldValue("submitreasoncode", (String) null); //
		isql_3.putFieldValue("submitmessage", str_message); //
		isql_3.putFieldValue("submitmessagefile", str_messagefile); //
		if (_secondCallVO.isIsprocessed()) { //������ս���
			isql_3.putFieldValue("isprocesser", "Y"); //���ս���!
			isql_3.putFieldValue("isprocess", "Y");
			isql_3.putFieldValue("submittouser", str_submittouser); //�ύ��˭
			isql_3.putFieldValue("submittousername", str_submittousername); //�ύ��˭
			isql_3.putFieldValue("submittocorp", str_submittocorp); //�ύ���ĸ�����
			isql_3.putFieldValue("submittocorpname", str_submittocorpName); //�ύ���ĸ�����������
			isql_3.putFieldValue("submittotransition", submittotransition); //�ύ��ʲô����,���˷�֧ģʽ��,Ӧ�ÿ����ж��!
			isql_3.putFieldValue("submittoactivity", submittoactivity); //�ύ��ʲô����,���˷�֧ģʽ��,Ӧ�ÿ����ж��!
			isql_3.putFieldValue("submittobatchno", li_newBatchNo); //�ύ���ĸ�����!!
		} else {
			isql_3.putFieldValue("isprocesser", "N"); //�����ս���!
		}
		isql_3.putFieldValue("participant_yjbduserid", _secondCallVO.getDealpooltask_yjbduserid()); //������id
		isql_3.putFieldValue("participant_yjbdusercode", _secondCallVO.getDealpooltask_yjbdusercode()); //�����˱���
		isql_3.putFieldValue("participant_yjbdusername", _secondCallVO.getDealpooltask_yjbdusername()); //����������

		al_sqls.add(isql_3.getSQL()); ////

		if (_secondCallVO.isIsprocessed()) { //������ս���
			//�޸ı�������,��ʾ���ڽ���
			if (!"4".equals(_secondCallVO)) { //��������ж�һ�㲻���˻�
				al_sqls.add("update pub_wf_dealpool set isprocess='Y' where prinstanceid='" + str_prinstanceid + "' and batchno='" + str_batchno + "' and isprocess='N'"); //�޸ı������ݱ�Ǳ����ڽ���
			}

			//Ϊ��һ������������..
			if (commitTaskVOs != null) { //�������������Ϊ��!
				for (int i = 0; i < commitTaskVOs.length; i++) {
					//�ڴ���������ɴ���������
					String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_dealpool"); //
					String str_sql_insert_dealpool = getInsertDealPoolSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_processid, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], commitTaskVOs[i], "N", null, null, str_dealpoolid, null, li_newBatchNo, str_currtime, _secondCallVO.getPrioritylevel(), _secondCallVO
							.getDealtimelimit(), null, false, null, null, null, null); //
					al_sqls.add(str_sql_insert_dealpool); //

					//������ҳ��Ҫ�Ĵ���������.
					String str_msg = getDealTaskMsg(_secondCallVO.getProcessid(), commitTaskVOs[i].getFromActivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_newDealPoolId, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //������Ϣ��ʾ,�ǿ����õ�!
					String str_sql_insert_dealtask = getInsertTaskDealSQL(str_newDealPoolId, str_prinstanceid, str_parentInstanceId, str_rootinstanceid, str_dealpoolid, str_billTempletCode, str_billtableName, str_billQueryTableName, str_billPkName, str_billPkValue, _billVO.toString(), str_message, str_msg, _loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1],
							str_currtime, commitTaskVOs[i].getParticipantUserId(), commitTaskVOs[i].getParticipantUserName(), commitTaskVOs[i].getParticipantUserDeptId(), commitTaskVOs[i].getParticipantUserDeptName(), commitTaskVOs[i].getAccrUserId(), _secondCallVO.getPrioritylevel(), _secondCallVO.getDealtimelimit(), commitTaskVOs[i].isCCTo());
					al_sqls.add(str_sql_insert_dealtask); //
				}
			}
		} else { //��������ս���
		}

		getCommDMO().executeBatchByDS(null, al_sqls); //ִ��������SQL

		//���ʼ�,sfj�����޸�....
		sendMail(_secondCallVO, _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message); //

		//ִ�к���������!!
		//		if (intercept2 != null) {
		//			intercept2.afterAction(_secondCallVO, _loginuserid, _billVO, "REJECT"); //
		//		}
		intercept2AfterAction(_secondCallVO, _loginuserid, _billVO, "REJECT");

		return _billVO; //
	}

	/**
	 * ȡ��һ�������������߹�����!!
	 * @param _activityId
	 * @return
	 */
	private WorkFlowParticipantBean getOneActivityHistDealUserVOs(String _wfinstanceid, String _activityId) throws Exception { //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		String str_sql_oneactivity = "select distinct participant_user from pub_wf_dealpool where prinstanceid='" + getTBUtil().getNullCondition(_wfinstanceid) + "' and curractivity='" + getTBUtil().getNullCondition(_activityId) + "' and isprocess='Y'";
		String[] str_userids = getCommDMO().getStringArrayFirstColByDS(null, str_sql_oneactivity); //�ҳ�������ʵ���иû��������߹���������
		if (str_userids != null && str_userids.length > 0) {
			String str_sql = "select id userid,code usercode,name username from pub_user where id in (" + getTBUtil().getInCondition(str_userids) + ")"; //�ҳ���Щ��Ա
			WorkFlowParticipantUserBean[] userBeans = getParticipanUserBeansBySQL(str_sql, false, "����ֱ�Ӽ���", "��Ϊ����ֱ�Ӽ��������߹�����!"); //������Щ��Ա��������̲����߶���!!!
			parBean.setParticipantUserBeans(userBeans); //�����߹���!!!
			parBean.setParticiptMsg("����SQL[" + str_sql_oneactivity + "]���ҵ�" + userBeans.length + "����!"); //
		} else {
			parBean.setParticiptMsg("����SQL[" + str_sql_oneactivity + "]û���ҵ�һ����!"); //
		}
		return parBean; //
	}

	//ȡ����Ȩģ��!!
	private String getAccrModel(BillVO _billVO) throws Exception {
		//������Ȩ����!!!!!!!! ���Ǳ��������ҵ�����,Ȼ�����Ȩ���嵥��Ѱ�ҵ�����˵���Ȩ��Ϣ,Ȼ��¼��ȥ!!!
		//String str_processId = _hvs_dealrecord.getStringValue("rootinstanceid_processid"); //����id
		String str_billtype = _billVO.getStringValue("billtype"); //��������
		String str_busitype = _billVO.getStringValue("busitype"); //ҵ������
		String str_accrmodel = null; //
		if (str_billtype != null && !str_billtype.trim().equals("") && str_busitype != null && !str_busitype.trim().equals("")) { //������߶���Ϊ��!!��ȥ�ҵ����˵���Ȩģ��
			String[][] str_accrModels = getCommDMO().getStringArrayByDS(null, "select accrmodel from pub_workflowassign where billtypecode='" + str_billtype + "' and busitypecode='" + str_busitype + "'"); //����ģʽ!!!
			if (str_accrModels.length == 0) { //
				throw new WLTAppException("����Ȩģ�����ʱ����,���ݵ�������[" + str_billtype + "],ҵ������[" + str_busitype + "]�����̷������û���ҵ�һ�������¼,���ǲ��Ե�,�������Ա��ϵ!"); //
			} else if (str_accrModels.length > 1) {
				throw new WLTAppException("����Ȩģ�����ʱ����,���ݵ�������[" + str_billtype + "],ҵ������[" + str_busitype + "]�����̷�������ҵ��������ϵ�������Ϣ!\r\n�⽫�ᵼ�µõ��������Ȩģ��,�ǲ������,�������Ա��ϵ!!"); //
			}
			str_accrmodel = str_accrModels[0][0]; //��ֵ
		}
		return str_accrmodel; //��Ȩģ��!!!
	}

	/**
	 * ҵ����ι���!!!
	 * @return
	 */
	private void busiDynSecondFilter(String _wfinstanceid, String _billtype, String _busitype, BillVO _billVO, DealActivityVO[] _dealActivitys) {
		HashVO[] hvs_defines = isDefineFilter2(_wfinstanceid, _billtype, _busitype); //ȡ�ö����ж��ι�������
		if (hvs_defines != null && hvs_defines.length > 0) { //��������˶��ι���...
			if (_dealActivitys == null || _dealActivitys.length <= 0) {
				return;
			}

			for (int i = 0; i < _dealActivitys.length; i++) {
				DealTaskVO[] taskVOs = _dealActivitys[i].getDealTaskVOs(); //ȡ�ô���������
				if (taskVOs != null && taskVOs.length > 0) {
					ArrayList al_taskvo = new ArrayList(); //
					for (int j = 0; j < taskVOs.length; j++) {
						String str_userid = taskVOs[j].getParticipantUserId(); //�����ߵ�Id
						String str_userroleid = taskVOs[j].getParticipantUserRoleId(); //�����ߵĽ�ɫId
						String str_activityid = taskVOs[j].getCurrActivityId(); //
						if (isDefineFilter2Item(hvs_defines, _billVO, str_activityid, str_userid, str_userroleid)) { //����ÿһ��
							al_taskvo.add(taskVOs[j]); //
						}
					}
					_dealActivitys[i].setDealTaskVOs((DealTaskVO[]) al_taskvo.toArray(new DealTaskVO[0])); //��������һ�¹��˺������!!!
				}

			}
		}
	}

	/**
	 * ��ѯ�Ƿ���ҵ����ζ�̬����!!!
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
	 * �Ƿ�����ĳһ��Ķ��ι�������
	 * @param _al_filter2Define
	 * @return
	 */
	private boolean isDefineFilter2Item(HashVO[] _hvs_filter2Define, BillVO _billVO, String _activityid, String _userid, String _roleid) {
		for (int i = 0; i < _hvs_filter2Define.length; i++) { //
			if (_hvs_filter2Define[i].getStringValue("activityid").equals(_activityid)) { //����Ǹû��ڵĶ���
				String str_itemkey = _hvs_filter2Define[i].getStringValue("itemkey");
				String str_defineCondition = _hvs_filter2Define[i].getStringValue("itemvalue"); //�����ֵ������
				String str_billValue = _billVO.getStringValue(str_itemkey); //
				if (meetCondition(str_billValue, str_defineCondition)) { //��������еĸ�itemkey��ֵ���õ��ڶ����itemvalue,���ٱȽ���Ա
					String str_defineuserids = _hvs_filter2Define[i].getStringValue("userids"); //
					String str_defineuserroleids = _hvs_filter2Define[i].getStringValue("roleids"); //
					if ((str_defineuserids != null && str_defineuserids.indexOf(_userid + ";") >= 0) || (str_defineuserroleids != null && str_defineuserroleids.indexOf(_roleid + ";") >= 0)) {
						return true; //�����ɫ�������Ա�����а������������д���������Ա���ɫ,��Ը���Ա�Ź�ȥ,��ͨ��!!
					} else {
						return false; //��������������ɫ,���������������洫��������,��ܾ�����ͨ��
					}
				}

			}
		}

		return true; //
	}

	/**
	 * ��ҳ���ϵ�ֵ�붨����������бȽ�,���Ʒ�������򷵻�True
	 * @param _billVaue
	 * @param _condition
	 * @return
	 */
	private boolean meetCondition(String _billVaue, String _condition) {
		if (_billVaue == null || _condition == null) { //���ҳ���ϵ�ֵΪ��,�򲻸�!
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
			str_sql = "select 1 from wltdual where " + str_sql_item1 + _condition; //ֱ��ƴ,�� 100>200
		} else {
			str_sql = "select 1 from wltdual where " + str_sql_item1 + "='" + _condition + "'"; //
		}

		try {
			String[][] str_data = getCommDMO().getStringArrayByDS(null, str_sql); //ִ��SQL
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
	 * �ж�ֵ�Ƿ�������
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

	//������ҪVO: NextTransitionVO ���ߵ�VO DealActivity ���컷��VO  DealTaskVO:һ�������а����Ĵ�������VO  
	private DealTaskVO getDealTaskVOByCompute(DealActivityVO _dealActVO, WorkFlowParticipantUserBean _wfParUsers) {
		DealTaskVO taskVO = new DealTaskVO();
		taskVO.setFromActivityId(_dealActVO.getFromActivityId()); //
		taskVO.setFromActivityName(_dealActVO.getFromActivityName()); //

		taskVO.setTransitionId(_dealActVO.getFromTransitionId()); //
		taskVO.setTransitionCode(_dealActVO.getFromTransitionCode()); //���߱���...
		taskVO.setTransitionName(_dealActVO.getFromTransitionName()); //
		taskVO.setTransitionIntercept(_dealActVO.getFromTransitionIntercept()); //���ߵ�������..
		taskVO.setTransitionDealtype(_dealActVO.getFromtransitiontype()); //��������
		taskVO.setTransitionMailSubject(_dealActVO.getFromTransitionMailsubject()); //�����϶�����ʼ�����...
		taskVO.setTransitionMailContent(_dealActVO.getFromTransitionMailcontent()); //�����϶�����ʼ�����..

		taskVO.setCurrActivityId(_dealActVO.getActivityId()); //
		taskVO.setCurrActivityType(_dealActVO.getActivityType()); //��������
		taskVO.setCurrActivityCode(_dealActVO.getActivityCode()); //
		taskVO.setCurrActivityName(_dealActVO.getActivityName()); //

		taskVO.setParticipantUserId(_wfParUsers.getUserid()); //������ID
		taskVO.setParticipantUserCode(_wfParUsers.getUsercode()); //�����߱���
		taskVO.setParticipantUserName(_wfParUsers.getUsername()); //����������

		taskVO.setParticipantUserDeptId(_wfParUsers.getUserdeptid()); //�����߻�������
		taskVO.setParticipantUserDeptCode(_wfParUsers.getUserdeptcode()); //�����߻�������
		taskVO.setParticipantUserDeptName(_wfParUsers.getUserdeptname()); //�����߻�������

		taskVO.setParticipantUserRoleId(_wfParUsers.getUserroleid()); //�����߽�ɫ����
		taskVO.setParticipantUserRoleCode(_wfParUsers.getUserrolecode()); //�����߽�ɫ����
		taskVO.setParticipantUserRoleName(_wfParUsers.getUserrolename()); //�����߽�ɫ����

		taskVO.setAccrUserId(_wfParUsers.getAccrUserid()); //��Ȩ��ID.
		taskVO.setAccrUserCode(_wfParUsers.getAccrUsercode()); //��Ȩ�˱���.
		taskVO.setAccrUserName(_wfParUsers.getAccrUsername()); //��Ȩ������.

		taskVO.setCCTo(_wfParUsers.isCCTo()); //�Ƿ��ǳ���ģʽ!!!
		taskVO.setSuccessParticipantReason(_wfParUsers.getSuccessParticipantReason()); //
		return taskVO; //
	}

	/**
	 * ִ��������2��ǰ�ò���
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void intercept2BeforeAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		String str_interceptClsName = _secondCallVO.getIntercept2(); //����������!!
		if (str_interceptClsName != null && str_interceptClsName.trim().indexOf(".") > 0) { //
			Object obj = Class.forName(str_interceptClsName).newInstance(); //
			if (obj instanceof WFIntercept2IFC) { //��ǰ��BS���������Ǽ̳�������ӿ�,Ϊ�˼�����ǰ��,���Բ��ܷ�����ǰ�Ļ���!!
				WFIntercept2IFC intercept = (WFIntercept2IFC) obj; //����������2
				intercept.beforeAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //
			} else if (obj instanceof WorkFlowEngineBSIntercept) { //�µĻ�����ʹ����ͳһ����������BS��������
				String str_billtype = _billVO.getStringValue("billtype"); //
				String str_busitype = _billVO.getStringValue("busitype"); //
				WorkFlowEngineBSIntercept intercept = (WorkFlowEngineBSIntercept) obj; //����������
				WLTHashMap parMap = new WLTHashMap(); //Ϊ���Ժ���չ����Ӱ�����,����һ��Map�������������������չ�Ĳ���!!
				intercept.beforeActivityAction(str_billtype, str_busitype, _secondCallVO, _loginuserid, _billVO, _dealtype, parMap); //
			} else { //�����쳣���ǲ����κ��߼�����???
				throw new WLTAppException("������ע���BS�����������Ͳ���,������ʵ�ֽӿ�[WFIntercept2IFC],Ҳ���Ǽ̳��ڳ�����[WorkFlowEngineBSIntercept]"); //
			}
		}
	}

	/**
	 * ִ��������2�ĺ��ò���
	 * @param _secondCallVO
	 * @param _loginuserid
	 * @param _billVO
	 * @param _dealtype
	 * @throws Exception
	 */
	public void intercept2AfterAction(WFParVO _secondCallVO, String _loginuserid, BillVO _billVO, String _dealtype) throws Exception {
		try {
			String str_interceptClsName = _secondCallVO.getIntercept2(); //����������!!
			if (str_interceptClsName != null && str_interceptClsName.trim().indexOf(".") > 0) { //
				Object obj = Class.forName(str_interceptClsName).newInstance(); //
				if (obj instanceof WFIntercept2IFC) { //��ǰ��BS���������Ǽ̳�������ӿ�,Ϊ�˼�����ǰ��,���Բ��ܷ�����ǰ�Ļ���!!
					WFIntercept2IFC intercept = (WFIntercept2IFC) obj; //����������2
					intercept.afterAction(_secondCallVO, _loginuserid, _billVO, _dealtype); //
				} else if (obj instanceof WorkFlowEngineBSIntercept) { //�µĻ�����ʹ����ͳһ����������BS��������
					String str_billtype = _billVO.getStringValue("billtype"); //
					String str_busitype = _billVO.getStringValue("busitype"); //
					WorkFlowEngineBSIntercept intercept = (WorkFlowEngineBSIntercept) obj; //����������
					WLTHashMap parMap = new WLTHashMap(); //Ϊ���Ժ���չ����Ӱ�����,����һ��Map�������������������չ�Ĳ���!!

					//parMap
					intercept.afterActivityAction(str_billtype, str_busitype, _secondCallVO, _loginuserid, _billVO, _dealtype, parMap); //
				} else { //�����쳣���ǲ����κ��߼�����???
					throw new WLTAppException("������ע���BS�����������Ͳ���,������ʵ�ֽӿ�[WFIntercept2IFC],Ҳ���Ǽ̳��ڳ�����[WorkFlowEngineBSIntercept]"); //
				}
			}
		} catch (Throwable ex) {
			System.out.println("Ϊ�˲�Ӱ�������Աʹ��,�Ȱ��쳣�Ե�!!!"); //
			ex.printStackTrace(); //
		}
	}

	//������µ���ʷ�������嵥!!
	private String[] getNewSubmiterHist(String _prinstanceId, String _loginUserId, String _currtime, String _dealMsg) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select submiterhist,mylastsubmittime,mylastsubmitmsg from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		String str_newsubmiterhist = hvs[0].getStringValue("submiterhist"); //ȡ����ʷִ�й�����!!!
		if (str_newsubmiterhist == null || str_newsubmiterhist.trim().equals("")) {
			str_newsubmiterhist = ";" + _loginUserId + ";"; //���Ϊ��,���һ����;��
		} else {
			str_newsubmiterhist = str_newsubmiterhist + _loginUserId + ";"; //
		}

		//�ҵ������ʱ��,�����ڵ����в鿴!!
		String str_mylastsubmittime = getNewMyLastInfo(hvs[0].getStringValue("mylastsubmittime"), _loginUserId, _currtime); //

		//�ҵ���������,�����ڵ����в鿴!!
		String str_dealMsg = (_dealMsg == null ? "" : _dealMsg);
		str_dealMsg = getTBUtil().replaceAll(str_dealMsg, ";", ","); //Ϊ�˷�ֹ����о��зֺŽ��ֺ�ͳͳǿ�ƻ��ɶ���
		if (str_dealMsg.length() > 20) { //�������20����,������ȡ����,����Ϊ�˷�ֹ̫�����������ۼӻ���ɴ治�µ��쳣!!!
			str_dealMsg = str_dealMsg.substring(0, 20) + "..."; //
		}
		String str_mylastsubmitmsg = getNewMyLastInfo(hvs[0].getStringValue("mylastsubmitmsg"), _loginUserId, str_dealMsg); //

		return new String[] { str_newsubmiterhist, str_mylastsubmittime, str_mylastsubmitmsg }; //
	}

	//�ҳ����滻�ҵ��µ�����,�ֻ�ֱ������Ӵ���! ����ɡ�123=�ҵ����;456=�������;���Ĺ�ϣ���ַ���������
	private String getNewMyLastInfo(String _oldText, String _loginUserId, String _newReplacedText) {
		String str_returnText = null; //
		if (_oldText == null || _oldText.trim().equals("")) { //���Ϊ��
			str_returnText = ";" + _loginUserId + "=" + _newReplacedText + ";"; //
		} else {
			int li_pos = _oldText.indexOf(";" + _loginUserId + "="); //
			if (li_pos >= 0) { //�����������,����replace����!
				String str_prefix = _oldText.substring(0, li_pos + 1 + _loginUserId.length() + 1); ////ֱ��������
				String str_subfix_1 = _oldText.substring(li_pos + 1 + _loginUserId.length() + 1, _oldText.length()); //
				String str_subfix_2 = str_subfix_1.substring(str_subfix_1.indexOf(";"), str_subfix_1.length()); //�ͱ�����ʷ����������ź������Ϣ!
				str_returnText = str_prefix + _newReplacedText + str_subfix_2; //��()�е�ԭ����ʱ���滻���µ�ʱ��!
			} else { //��������Ӵ���!
				str_returnText = _oldText + _loginUserId + "=" + _newReplacedText + ";"; //
			}
		}
		return str_returnText; //
	}

	//�����ĳ����ʵ�����µ�����! �������ֻ���������,���ڲ���ʱ���ܳ����غ�!! ������Ҫʹ���ڴ����ļ���! ��һ����ȡ�ú���һ����ȡʱ���ᴦ��ȴ���ȡ��һ����!!!
	private int getNewBatchNo(String _prinstanceId) throws Exception {
		HashVO[] hvs_batchNo = getCommDMO().getHashVoArrayByDS(null, "select currbatchno c1 from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		int li_batchNo = 1;
		if (hvs_batchNo[0].getIntegerValue("c1") != null) {
			li_batchNo = hvs_batchNo[0].getIntegerValue("c1").intValue() + 1; // ���ż�1!!!û��ʹ��SQL�﷨+1,��Ϊ�˿����ݿ�ƽ̨!!!
		}
		return li_batchNo; //
	}

	//ȡ�ô��������
	private String getApproveResult(String _dealtype) {
		if (_dealtype == null || _dealtype.trim().equals("")) { //�������,���ύ���˻�������������Ը���ύ!!!
			return "Y";
		} else if (_dealtype.toUpperCase().equals("SUBMIT")) { //������ύ
			return "Y";
		} else if (_dealtype.toUpperCase().equals("REJECT")) { //����Ǿܾ�
			return "N";
		} else {
			return "Y";
		}
	}

	//�ύ���������,�������ԭ����˻�,�Ժ�Ĺ���ͳ����Ҫ!
	private String getSubmitReasonCode(WFParVO _secondCallVO) {
		if (_secondCallVO != null && _secondCallVO.getSelectedReasonCode() >= 0 && _secondCallVO.getReasonCodeComBoxItemVOs() != null) {
			return _secondCallVO.getReasonCodeComBoxItemVOs()[_secondCallVO.getSelectedReasonCode()].getId(); //
		} else {
			return null;
		}
	}

	//������Աidȡ�ñ���������!!
	private String getUserCodeNameById(String _userId) throws Exception {
		String[][] str_userCodeName = getCommDMO().getStringArrayByDS(null, "select code,name from pub_user where id='" + _userId + "'"); //
		if (str_userCodeName != null && str_userCodeName.length > 0) {
			return str_userCodeName[0][0] + "/" + str_userCodeName[0][1]; //�û�����,���ǵ����ܻ�����,����ʹ�ù���/���Ƶ���ʽ
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
			if ("Y".equalsIgnoreCase(hvs[i].getStringValue("isdefault"))) { //����и�Ĭ�ϲ���
				str_deptId = hvs[i].getStringValue("userdept"); //
				str_deptName = hvs[i].getStringValue("deptname"); //
				isFindDefault = true;
				break; //
			}
		}

		if (!isFindDefault) { //���û����Ĭ�ϲ���,��ֱ�ӷ��ص�һ��!������һ����ǰ��Ĭ�ϲ���!!!
			str_deptId = hvs[0].getStringValue("userdept"); //
			str_deptName = hvs[0].getStringValue("deptname"); //
		}

		if (str_deptId != null) {
			System.out.println("_userId=========================" + _userId + "    str_deptId" + str_deptId);//��һ�����������Ժ󱨴�Ļ���֪���ĸ��˵Ļ��������ˡ�Ԭ����/2017-11-27��
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

	//�������ߵ���Ϣƴ����һ��,Ȼ������SubmitTo����Ϣ,���ύ����˭!!!
	private String[] getSubmitToInfo(DealTaskVO[] commitTaskVOs) {
		String str_submittouser = null, str_submittousername = null, str_submittocorp = null, str_submittocorpName = null, submittotransition = null, submittoactivity = null, submittoactivityname = null; //
		if (commitTaskVOs != null && commitTaskVOs.length > 0) { //�������������Ϊ��!
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
				str_submittouser = str_submittouser + commitTaskVOs[i].getParticipantUserId() + ";"; //�����н�����ƴ��һ��!!
				str_submittousername = str_submittousername + commitTaskVOs[i].getParticipantUserName() + ";"; //�����ߵ�����
				if (commitTaskVOs[i].getParticipantUserDeptId() != null && str_submittocorp.indexOf(";" + commitTaskVOs[i].getParticipantUserDeptId() + ";") < 0) { //����л���,��û�г��ֹ�,�����
					str_submittocorp = str_submittocorp + commitTaskVOs[i].getParticipantUserDeptId() + ";"; //
				}
				if (commitTaskVOs[i].getParticipantUserDeptName() != null && str_submittocorpName.indexOf(";" + commitTaskVOs[i].getParticipantUserDeptName() + ";") < 0) { //����л���,��û�г��ֹ�,�����
					str_submittocorpName = str_submittocorpName + commitTaskVOs[i].getParticipantUserDeptName() + ";"; //
				}
				submittotransition = commitTaskVOs[i].getTransitionId(); //�ύ������!�Ժ���ڷֱ���·�����,���ܲ���Ҫ���ֶ�,���ߴ���Ƕ�ֵ
				submittoactivity = commitTaskVOs[i].getCurrActivityId(); //�ύ�Ļ���!!
				submittoactivityname = commitTaskVOs[i].getCurrActivityName(); //�ύ�Ļ�������
			}
		}
		return new String[] { str_submittouser, str_submittousername, str_submittocorp, str_submittocorpName, submittotransition, submittoactivity, submittoactivityname }; //
	}

	//ִ�������ϵ�������
	private void execTransitionIntercept(WFParVO _secondCallVO, String str_prinstanceid, String str_dealpoolid, BillVO _billVO) throws Exception {
		DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //�ύ�Ķ���!!
		JepFormulaParseAtWorkFlow jepFormula = null; //
		if (commitTaskVOs != null) {
			for (int i = 0; i < commitTaskVOs.length; i++) {
				String transiintercept = commitTaskVOs[i].getTransitionIntercept(); //Ŀ�괦�����������!!!
				if (transiintercept != null && !transiintercept.trim().equals("")) { //������ߵ���������Ϊ��
					if (jepFormula == null) {
						jepFormula = new JepFormulaParseAtWorkFlow(_secondCallVO, str_prinstanceid, str_dealpoolid, _billVO); //�����̴���������ʵ����������������������������ʽ����
					}
					String[] str_items = getTransFormulas(transiintercept); //�ָ�!!!
					for (int j = 0; j < str_items.length; j++) { //
						if (str_items[j].startsWith("=>")) { //�����ĳ����!!!��ֱ�ӷ�����ã�����
							String str_clsName = str_items[j].substring(2, str_items[j].length()); //������
							System.out.println("���������潫ִ�������ϵ��������ࡾ" + str_clsName + "��,�����Ӧ�ü̳��ڽӿ�[WorkFlowTransitionExecIfc]���ܳɹ�ִ��..."); //
							WorkFlowTransitionExecIfc ifc = (WorkFlowTransitionExecIfc) (Class.forName(str_clsName).newInstance()); //
							ifc.afterExecTransition(_secondCallVO, str_prinstanceid, str_dealpoolid, _billVO); //
						} else {
							System.out.println("���������潫ִ�������ϵĹ�ʽ��������" + str_items[j] + "��..."); //
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

	//�������ϵĹ�ʽ���й��˴�����
	private String[] getTransFormulas(String _formula) {
		String[] str_items = null; //
		_formula = _formula.trim(); ////������
		_formula = getTBUtil().replaceAll(_formula, "\r", ""); //
		_formula = getTBUtil().replaceAll(_formula, "\n", ""); //
		if (_formula.indexOf("��") > 0) {
			str_items = getTBUtil().split(_formula, "��"); //
		} else { //
			str_items = getTBUtil().split(_formula, ";"); //
		}
		for (int i = 0; i < str_items.length; i++) {
			str_items[i] = str_items[i].trim(); //
		}
		return str_items; //
	}

	//���ʼ�,��Ϊ�����ύ���õ�,���Է�װ��һ������!!! ���ڵĻ����ǿ���ֱ�ӷ��ʼ�! ���������ܺܵ�! Ӧ��ר���и��ʼ����ݱ�,Ȼ��ר������һ���ʼ��̴߳�������ж�ȡ���ݷ��ʼ�!
	//�������еĻ��ƾ�����һ�����в�����,Ȼ�󵥶����˸������ʼ�!
	private void sendMail(WFParVO _secondCallVO, BillVO _billVO, String _loginuserid, String str_loginusername, String str_currtime, String str_prinstanceid, String str_message) {
		try {
			DealTaskVO[] commitTaskVOs = _secondCallVO.getCommitTaskVOs(); //�ύ�Ķ���!!
			Vector v_sendmail = null; //
			if ("Y".equals(_secondCallVO.getIfSendEmail())) { //�����Ҫ���ʼ�!!!
				String str_mailsubject = getEmailSubJectMsg(_secondCallVO.getProcessid(), _secondCallVO.getCurractivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //�ʼ�����.
				String str_mailcontent = getEmailContentMsg(_secondCallVO.getProcessid(), _secondCallVO.getCurractivityName(), _billVO, _loginuserid, str_loginusername, str_currtime, str_prinstanceid, str_message, _secondCallVO.getDealtimelimit()); //�ʼ�����.
				if (str_mailcontent != null && !str_mailcontent.trim().equals("")) { //����ʼ����ݲ�Ϊ�գ���Ҫ�����ʼ�
					String str_usermailaddr = ""; //
					for (int i = 0; i < commitTaskVOs.length; i++) {
						str_usermailaddr = str_usermailaddr + getUserMailAddr(commitTaskVOs[i].getParticipantUserId()) + ";";
					}
					if (str_usermailaddr != null && !"".equals(str_usermailaddr)) { //����ʼ���ַ��Ϊ��...
						if (v_sendmail == null) {
							v_sendmail = new Vector();
						}
						v_sendmail.add(new String[] { str_usermailaddr, str_mailsubject, str_mailcontent }); //��ĳ����,��ʲô�ʼ�..
					}
				}
			}
			if (v_sendmail != null && v_sendmail.size() > 0) { //�����ȷ����Ҫ���ʼ���
				for (int i = 0; i < v_sendmail.size(); i++) { //���������ʼ�
					if (!"".equals(getTBUtil().getSysOptionStringValue("�Զ����ʼ�������", ""))) {
						try {
							Object selfDescSendMail = Class.forName(getTBUtil().getSysOptionStringValue("�Զ����ʼ�������", "")).newInstance();
							if (selfDescSendMail instanceof AbstractSelfDescSendMail) { //
								_billVO.setUserObject("WFParVO", _secondCallVO); //by haoming 2016-04-18 
								((AbstractSelfDescSendMail) selfDescSendMail).sendMail(_billVO, str_message, _loginuserid, v_sendmail); //���ʼ�!
							} else {
								throw new Exception(getTBUtil().getSysOptionStringValue("�Զ����ʼ�������", "") + "û�м̳�AbstractSelfDescSendMail!!");
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else { //Ĭ�ϵķ��ʼ�����!!
						String str_mailserver = getTBUtil().getSysOptionStringValue("�ʼ�����", ""); //
						if (!"".equals(str_mailserver) && str_mailserver.split(";").length == 3) { //��ƽ̨��������ȡ����������!
							String str_touser = ((String[]) v_sendmail.get(i))[0]; //������
							if (str_touser != null && !"".equals(str_touser) && !"null".equals(str_touser)) {
								String str_mailsubject = ((String[]) v_sendmail.get(i))[1]; //�ʼ�����.
								String str_mailcontent = ((String[]) v_sendmail.get(i))[2]; //�ʼ�����.
								str_mailcontent = replaceAllMailItem(str_mailcontent, _billVO); //�ʼ�����,�滻{}�е�ֵ 
								System.out.println("�����ʼ�,SMTP Server[" + str_mailserver.split(";")[0] + "],FromUser[" + _loginuserid + "],ToUser[" + str_touser + "],Subject[" + str_mailsubject + "],Content[" + str_mailcontent + "]"); ////
								new MailUtil().sendMail(str_mailserver.split(";")[0], str_mailserver.split(";")[1], str_mailserver.split(";")[2], str_touser.split(";"), str_mailsubject, str_mailcontent); //�����ʼ�... 
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace(); //���ʼ������쳣,ֱ�ӳԵ�,��֤�����������!
		}
	}

	/**
	 * �滻����{}�е��ַ�..
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
	 * ���������������Ϣ����ʾ����
	 * ����������һ�λ�,Ȼ��ӱ���ȡ����Ӧ������!!
	 * @param _processid
	 * @param _fromActivityName
	 * @param _billVO
	 * @return
	 */
	private String getDealTaskMsg(String _processid, String _fromActivityName, BillVO _billVO, String _loginuserId, String _loginusername, String _dealpoolIdCreateTime, String _dealPoolId, String _str_prinstanceid, String _ideaMsg, String _dealLimit) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select name,userdef01 from pub_wf_process where id='" + _processid + "'"); //
		String str_msg = null; //
		if (hvs == null || hvs.length == 0 || hvs[0].getStringValue("userdef01") == null || hvs[0].getStringValue("userdef01").trim().equals("")) {
			str_msg = "[" + _loginusername + "]�ڻ���[" + _fromActivityName + "]�ύ�����[" + _billVO.getTempletName() + "][" + _billVO.getPkValue() + "]��Ҫ�㴦��!"; //
		} else {
			TBUtil tbUtil = new TBUtil();
			String str_msgtemplet = hvs[0].getStringValue("userdef01"); //��Ϣģ��
			String[] str_macroItems = tbUtil.getFormulaMacPars(str_msgtemplet, "{", "}"); //
			for (int i = 0; i < str_macroItems.length; i++) { //ѭ���滻���е�ֵ
				if (str_macroItems[i].equals("�ύ��")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _loginusername); //�ύ��
				} else if (str_macroItems[i].equals("�ύʱ��")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealpoolIdCreateTime); //�ύ��
				} else if (str_macroItems[i].equals("�ύ����")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _fromActivityName); //�ύ����
				} else if (str_macroItems[i].equals("��������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealLimit);
				} else if (str_macroItems[i].equals("�������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _ideaMsg); //����
				} else if (str_macroItems[i].equals("������������")) { //
					try {
						String createtime = commDMO.getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + _str_prinstanceid + "'");
						String currtime = tbUtil.getCurrDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date_createtime = sdf.parse(createtime.substring(0, 10));
						java.util.Date date_currtime = sdf.parse(currtime);

						str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", "" + ((date_currtime.getTime() - date_createtime.getTime()) / (1000 * 3600 * 24))); //�ύ�˼���
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str_macroItems[i].equals("��������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _billVO.getTempletName()); //��������
				} else {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", (_billVO.getStringViewValue(str_macroItems[i]) == null ? "" : _billVO.getStringViewValue(str_macroItems[i]))); //�����е�ĳһ���ֵ
				}
			} //end for
			str_msg = str_msgtemplet; //
		}
		return str_msg;
	}

	/**
	 * ȡ���ʼ����� ʹ��userdef18
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
			str_msg = ServerEnvironment.getProperty("PROJECT_NAME_CN") + "����������!"; //
		} else {
			TBUtil tbUtil = new TBUtil();
			String str_msgtemplet = hvs[0].getStringValue("userdef18"); //�ʼ�����ģ��
			String[] str_macroItems = tbUtil.getFormulaMacPars(str_msgtemplet, "{", "}"); //
			for (int i = 0; i < str_macroItems.length; i++) { //ѭ���滻���е�ֵ
				if (str_macroItems[i].equals("�ύ��")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _loginusername); //�ύ��
				} else if (str_macroItems[i].equals("ϵͳ��")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", ServerEnvironment.getProperty("PROJECT_SHORTNAME")); //ϵͳ��
				} else if (str_macroItems[i].equals("�ύʱ��")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealpoolIdCreateTime); //�ύ��
				} else if (str_macroItems[i].equals("�ύ����")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _fromActivityName); //�ύ����
				} else if (str_macroItems[i].equals("��������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealLimit);
				} else if (str_macroItems[i].equals("�������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _ideaMsg); //����
				} else if (str_macroItems[i].equals("������������")) { //
					try {
						String createtime = commDMO.getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + _str_prinstanceid + "'");
						String currtime = tbUtil.getCurrDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date_createtime = sdf.parse(createtime.substring(0, 10));
						java.util.Date date_currtime = sdf.parse(currtime);

						str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", "" + ((date_currtime.getTime() - date_createtime.getTime()) / (1000 * 3600 * 24))); //�ύ�˼���
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str_macroItems[i].equals("��������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _billVO.getTempletName()); //��������
				} else {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", (_billVO.getStringViewValue(str_macroItems[i]) == null ? "" : _billVO.getStringViewValue(str_macroItems[i]))); //�����е�ĳһ���ֵ
				}
			} //end for
			str_msg = str_msgtemplet; //
		}
		return str_msg;
	}

	/**
	 * ȡ���ʼ����� ʹ��userdef19
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
			str_msg = "[" + _loginusername + "]�ڻ���[" + _fromActivityName + "]�ύ�����[" + _billVO.getTempletName() + "][" + _billVO.getPkValue() + "]��Ҫ�㴦��!"; //
		} else {
			TBUtil tbUtil = new TBUtil();
			String str_msgtemplet = hvs[0].getStringValue("userdef19"); //�ʼ�����ģ��
			String[] str_macroItems = tbUtil.getFormulaMacPars(str_msgtemplet, "{", "}"); //
			for (int i = 0; i < str_macroItems.length; i++) { //ѭ���滻���е�ֵ
				if (str_macroItems[i].equals("�ύ��")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _loginusername); //�ύ��
				} else if (str_macroItems[i].equals("�ύʱ��")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealpoolIdCreateTime); //�ύ��
				} else if (str_macroItems[i].equals("�ύ����")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _fromActivityName); //�ύ����
				} else if (str_macroItems[i].equals("��������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _dealLimit);
				} else if (str_macroItems[i].equals("�������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _ideaMsg); //����
				} else if (str_macroItems[i].equals("������������")) { //
					try {
						String createtime = commDMO.getStringValueByDS(null, "select createtime from pub_wf_prinstance where id='" + _str_prinstanceid + "'");
						String currtime = tbUtil.getCurrDate();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						java.util.Date date_createtime = sdf.parse(createtime.substring(0, 10));
						java.util.Date date_currtime = sdf.parse(currtime);

						str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", "" + ((date_currtime.getTime() - date_createtime.getTime()) / (1000 * 3600 * 24))); //�ύ�˼���
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (str_macroItems[i].equals("��������")) {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", _billVO.getTempletName()); //��������
				} else {
					str_msgtemplet = tbUtil.replaceAll(str_msgtemplet, "{" + str_macroItems[i] + "}", (_billVO.getStringViewValue(str_macroItems[i]) == null ? "" : _billVO.getStringViewValue(str_macroItems[i]))); //�����е�ĳһ���ֵ
				}
			} //end for
			str_msg = str_msgtemplet; //
		}
		return str_msg;
	}

	/**
	 * ����һ������..
	 * @throws Exception
	 */
	public void receiveDealTask(String _prinstanceid, String _loginuserid) throws Exception {
		String str_parentsql = "select id,isreceive from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and participant_user='" + _loginuserid + "' and issubmit='N' and isprocess='N'"; // �ҳ������ҵ�δ���������
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, str_parentsql); //�鿴��û�д�������!!!
		if (hvs.length == 0) {
			throw new WLTAppException("��ǰ����û���������!"); //
		}

		String str_dealpoolid = hvs[0].getStringValue("id"); //
		String str_isreceive = hvs[0].getStringValue("isreceive"); //
		if (str_isreceive != null && str_isreceive.equals("Y")) {
			if (new TBUtil().getSysOptionBooleanValue("����������ť��������Ƿ���ʾ���հ�ť", true)) {
				throw new WLTAppException("�õ����ѽ��չ���!"); //
			} else {
				return;
			}
		}

		String str_time = new TBUtil().getCurrTime(); //
		getCommDMO().executeUpdateByDS(null, "update pub_wf_dealpool set isreceive='Y',receivetime='" + str_time + "' where id='" + str_dealpoolid + "'"); //�޸Ľ��ձ��
	}

	/**
	 * ���ز���,�������ύ��������������ȡ����!��Ҫ���������ʱ���е�!!
	 * ��ǰ�Ļ����и�bug,���ǳ��غ�,��ҳ�Ĵ���������û����!!
	 * ���µĻ��ƽ�������������!!
	 * @param _prinstanceid
	 * @param _loginuserid
	 */
	public void cancelTask(String _prinstanceid, String _dealPoolId, String _taskOff_id, String _loginuserid, String[] _dirCancelChildIds) throws Exception {
		//�����ҵ�����id,��ǰ����ʵ��idѰ�ҵķ�ʽ��������!!! ����Ϊʲô���Ǹ���ʵ������������id��? ����һ���ǳ����ʧ��!!! 
		//String str_prdealpoolid = "";  //
		//HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select count(*) c1 from pub_wf_dealpool where createbyid='" + str_prdealpoolid + "'");  //
		//boolean isCanCancel = false;  //�Ƿ����������ز���
		//�߼�:
		//1.�����жϱ��������isprocess,�����N,˵���ǻ�ǩ,���϶����Գ���,���ҳ����߼��ܼ�,ֻ�轫isubmit,submittime����Ϊ�ռ���!
		//2.���isprocessΪY��,����isprocesser='N',�����Ѳ���������������,��Ϊ�ѱ������˴�����,���isprocesser='Y',�����ս���,������ж��Ƿ񴴽�������
		//3.���û�д�������,˵���ǽ�������,������˻�,ֻҪ��������������ָ���������!
		//4.�������������,���������������isreceive=Y,�����Ѳ�����������,������Գ���,��ɾ��������������,�ָ���������!

		if (_dirCancelChildIds != null && _dirCancelChildIds.length > 0) { //������ݽ���ֱ���Ҽ�ɾ��һ����!
			String str_delChild_inCons = getTBUtil().getInCondition(_dirCancelChildIds); //
			String str_sql_delchild_1 = "delete from pub_wf_dealpool where id in (" + str_delChild_inCons + ")"; //
			String str_sql_delchild_2 = "delete from pub_task_deal   where createbydealpoolid in (" + str_delChild_inCons + ")"; //
			String str_sql_delchild_3 = "delete from pub_task_off    where createbydealpoolid in (" + str_delChild_inCons + ")"; //
			getCommDMO().executeBatchByDS(null, new String[] { str_sql_delchild_1, str_sql_delchild_2, str_sql_delchild_3 }); //��ɾ��,��Ϊ������Ҫ����������д���
		}

		//���ϻָ�ʱʱ,��ͬʱ���Ѱ�����д������������ȥ!
		String str_dealPoolId = _dealPoolId; //
		if (str_dealPoolId == null || str_dealPoolId.trim().equals("")) { //���û������id,������ǾɵĻ���,Ҫ����һ��!
			String str_submithist = getCommDMO().getStringValueByDS(null, "select submiterhist from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
			if (str_submithist != null && !str_submithist.endsWith(";" + _loginuserid + ";")) { //��������ύ�߲�����!
				throw new WLTAppException("�����ѱ����ν����ߴ�����,���ܽ��г��ش�����!"); //
			}
			str_dealPoolId = getCommDMO().getStringValueByDS(null, "select max(id) from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and participant_user='" + _loginuserid + "' and issubmit='Y'"); //�ҳ��Ҵ�������ļ�¼!!�������µ��Ѱ�����!
		}

		if (str_dealPoolId == null || str_dealPoolId.trim().equals("")) { //
			throw new WLTAppException("�������Ѿ����˻���,���߲�������ո��ύ��!"); //
		}

		if (_dirCancelChildIds == null && _taskOff_id != null) {
			String str_submitorconfirm = getCommDMO().getStringValueByDS(null, "select submitorconfirm from pub_task_off where id='" + _taskOff_id + "'"); //
			if ("confirm".equalsIgnoreCase(str_submitorconfirm)) {
				//�������񳷻���ʾ�޸� �����/2013-04-23��
				String str_confirmreason = getCommDMO().getStringValueByDS(null, "select confirmreason from pub_task_off where id='" + _taskOff_id + "'");
				if (str_confirmreason.contains("���Ǹ�����ı�������")) {
					throw new WLTAppException("���Ǹ�����ı�������,�������˲��ܽ��г��ش���!");
				} else {
					throw new WLTAppException("�������Ǳ�ȷ��ɾ������Ч����,��ʹ����Ҳ���ܴ���,����û�����������ز���!"); //	
				}
			}
		}

		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id='" + str_dealPoolId + "'"); //
		if (hvs == null || hvs.length == 0) {
			throw new WLTAppException("����id[" + str_dealPoolId + "]û����pub_wf_dealpool�����ҵ���Ӧ�ļ�¼,���������ݴ���,���뿪������ϵ!"); //
		}
		String str_prinstanceId = hvs[0].getStringValue("prinstanceid"); //����ʵ��id
		String str_rootInstanceId = hvs[0].getStringValue("rootinstanceid"); //������!
		String str_batchNo = hvs[0].getStringValue("batchno"); //����
		String str_curractivity = hvs[0].getStringValue("curractivity"); //��ǰ����!������Ҵ�������������,�򳷻�ʱ��Ҫ������ʵ���ĵ�ǰ���ڸĳ����ֵ!
		//��ǰֻ�ж��������̣������̽�������Գ��أ����޷����ύ������Ҫ���������̽����������̾Ͳ��������˰ɡ����/2016-12-21��
		String str_status = getCommDMO().getStringValueByDS(null, "select status from pub_wf_prinstance where id='" + str_prinstanceId + "'"); //����û�и�����!
		if ("END".equalsIgnoreCase(str_status)) {
			throw new WLTAppException((str_rootInstanceId == null || str_rootInstanceId.equals(str_prinstanceId) ? "" : "��") + "�����ѽ���,���ܽ��г��ش�����!"); //
		}

		ArrayList al_sqls = new ArrayList(); //
		//submitmessage=null,submitmessagefile=null,
		String str_sql_1 = "update pub_wf_dealpool set isreceive='N',receivetime=null,issubmit='N',submittime=null,submitisapprove=null,submittobatchno=null,submittotransition=null,submittoactivity=null,submittouser=null,submitreasoncode=null,realsubmiter=null,realsubmitername=null,realsubmitcorp=null,realsubmitcorpname=null,submittousername=null,submittocorp=null,submittocorpname=null,lifecycle=null where id='"
				+ str_dealPoolId + "'"; //
		String str_isProcess = hvs[0].getStringValue("isprocess"); //�Ƿ��ύ��!

		if (str_isProcess == null || "N".equals(str_isProcess)) { //���û�ύ,��˵���϶��ǻ�ǩ�е�,��Ϊ�������ռ���ս���,���������ܵ��Ѱ�����!����˵�϶����Գ���!!!
			al_sqls.add(str_sql_1); //�޸��Լ�������
			String str_cancelSQL = getCancelToTaskDealSQL(str_dealPoolId); //
			if (str_cancelSQL != null) { //��Ϊ�ɵĻ��ƿ���û���Ѱ�����!!
				al_sqls.add(str_cancelSQL); //���Ѱ������д������������!!
			}
			al_sqls.add("delete from pub_task_off where prdealpoolid='" + str_dealPoolId + "'"); //ɾ���Ѱ�����!!
		} else { //����Ѵ���...
			String str_isProcesser = hvs[0].getStringValue("isprocesser"); //�Ƿ����ս���!
			if ("Y".equals(str_isProcesser)) { //������ս���
				HashVO[] hvs_child = getCommDMO().getHashVoArrayByDS(null, "select id,isreceive,receivetime from pub_wf_dealpool where createbyid='" + str_dealPoolId + "'"); //��û�����Ҵ����Ķ���
				if (hvs_child == null || hvs_child.length == 0) { //���û�д�������,�����������һ��(�����̵Ľ�����)!���ܳ���!!!
					al_sqls.add("update pub_wf_dealpool set isprocess='N',isprocesser=null where prinstanceid='" + str_prinstanceId + "' and batchno='" + str_batchNo + "'"); //�޸���һ������Ϊ
					al_sqls.add(str_sql_1); //
					al_sqls.add("update pub_wf_prinstance set curractivity='" + str_curractivity + "',currowner=';" + _loginuserid + ";',currbatchno='" + str_batchNo + "' where id='" + str_prinstanceId + "'"); //�޸�����ʵ���ĵ�ǰ������!
					String str_cancelSQL = getCancelToTaskDealSQL(str_dealPoolId); //
					if (str_cancelSQL != null) { //��Ϊ�ɵĻ��ƿ���û���Ѱ�����!!
						al_sqls.add(str_cancelSQL); //���Ѱ������д������������!!
					}
					al_sqls.add("delete from pub_task_off where prdealpoolid='" + str_dealPoolId + "'"); //ɾ���Ѱ�����!!
				} else { //������������ε���!!
					for (int i = 0; i < hvs_child.length; i++) {
						if (getTBUtil().getSysOptionBooleanValue("���������������Ƿ��ܳ���", true)) { //Ĭ���ǲ��ܳ��ص�!!! �еĿͻ�˵�ܳ���,�е�˵���ܳ���!!
							if ("Y".equals(hvs_child[i].getStringValue("isreceive"))) { //����ѱ��˴�����,����ʾ��������!
								throw new WLTAppException("�õ�������[" + hvs_child[i].getStringValue("receivetime") + "]�������߽��ջ�����,���ܳ����ѱ����˽��ջ����˵ĵ���!"); ////
							}
						}
					}
					//������λ�û����,���ܳ���!!
					al_sqls.add("delete from pub_wf_dealpool where createbyid='" + str_dealPoolId + "'"); //��ɾ�����������ε���!!!!
					al_sqls.add("delete from pub_task_deal   where createbydealpoolid='" + str_dealPoolId + "'"); //ɾ�������������񴴽�����������!!
					al_sqls.add("update pub_wf_dealpool set isprocess='N',isprocesser=null where prinstanceid='" + str_prinstanceId + "' and batchno='" + str_batchNo + "'"); //�޸���һ������Ϊ
					al_sqls.add(str_sql_1); //�޸��Լ�������
					al_sqls.add("update pub_wf_prinstance set  curractivity='" + str_curractivity + "',currowner=';" + _loginuserid + ";',currbatchno='" + str_batchNo + "' where id='" + str_prinstanceId + "'"); //�޸�����ʵ���ĵ�ǰ������!!
					String str_cancelSQL = getCancelToTaskDealSQL(str_dealPoolId); //
					if (str_cancelSQL != null) { //��Ϊ�ɵĻ��ƿ���û���Ѱ�����!!
						al_sqls.add(str_cancelSQL); //���Ѱ������д������������!!
					}
					al_sqls.add("delete from pub_task_off where prdealpoolid='" + str_dealPoolId + "'"); //ɾ���Ѱ�����!!
					if (str_rootInstanceId != null && !str_rootInstanceId.trim().equals("")) { //����������Ǵ��������̵�����,��ô����ʱ����ͬʱɾ��������������ʵ��!�������Ǹ����ص�bug,����ҵ��Ŀ�о���Ϊ���ԭ��,������������̽���ʱ�������ص������̵ļ�¼!�������̽��в���ȥ! ��Ϊ�����Ƿ����������Ƿ�����Ǹ����ֵ����̵ĸ��������,�����ﲻɾ���ͻᵼ����Ϊ���û�н���!
						al_sqls.add("delete from pub_wf_prinstance where rootinstanceid='" + str_rootInstanceId + "' and id not in (select prinstanceid from pub_wf_dealpool where rootinstanceid='" + str_rootInstanceId + "')"); //
					}
				}
			} else { //��������ս���
				throw new WLTAppException("�õ����ѻ�ǩ����,���ֲ������������,�����㲻�ܽ��г��ز���!"); //
			}
		}

		String str_submiterhist = getCommDMO().getStringValueByDS(null, "select submiterhist from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (str_submiterhist.endsWith(";" + _loginuserid + ";")) { //����������ǵ�ǰ��,��Ҫ����!! ��������������,������Ҫ��һ��!����������񻹻�������Ѱ�����!
			str_submiterhist = str_submiterhist.substring(0, str_submiterhist.lastIndexOf(";" + _loginuserid + ";") + 1); //�ύ����ʷҪȥ����ǰ��!!
			al_sqls.add("update pub_wf_prinstance set submiterhist='" + str_submiterhist + "' where id='" + _prinstanceid + "'"); ////
		}
		getCommDMO().executeBatchByDS(null, al_sqls); //ִ������SQL!!!
	}

	//����Ա���������̽������� �����/2013-05-29��
	public String cancelTask_admin(String _prinstanceid, String _dealPoolId, String _taskOff_id, String _loginuserid, String[] _dirCancelChildIds) throws Exception {

		//����δ�������ܳ���
		boolean isCanRealEnd = judgeWFisRealEnd(_prinstanceid);
		if (!isCanRealEnd) {
			return "δ�������̲��ܳ���!";
		}

		//������δ�������ܳ���
		String sql_status = "select status from pub_wf_prinstance where id='" + _prinstanceid + "' and (parentinstanceid is null or parentinstanceid='')";
		String status = getCommDMO().getStringValueByDS(null, sql_status);
		if (status.equals("END")) {
			String str_dealPoolId_max = getCommDMO().getStringValueByDS(null, "select max(id) from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and issubmit='Y' and (isccto is null or isccto='' or isccto='N')");
			if (!str_dealPoolId_max.equals(_dealPoolId)) {
				return "�Ǹ����̽����ڵ㲻�ܳ���!";
			}

			String upd_sql = "update pub_wf_prinstance set status='RUN' where id='" + _prinstanceid + "'";
			getCommDMO().executeBatchByDS(null, new String[] { upd_sql });

			cancelTask(_prinstanceid, _dealPoolId, _taskOff_id, _loginuserid, _dirCancelChildIds);
			return "���سɹ�!";
		} else {
			return "δ�������̲��ܳ���!";
		}
	}

	/**
	 * ɾ������
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void deleteTask(String _prinstanceid, String _loginuserid) throws Exception {
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_prinstance where id='" + _prinstanceid + "'"); //
		if (hvs.length > 0) {
			if (!_loginuserid.equals(hvs[0].getStringValue("creater"))) {
				throw new WLTAppException("ֻ�����̴����߲��ܽ���ɾ������!"); //
			}
			String str_sql_1 = "delete from " + hvs[0].getStringValue("billtablename") + " where " + hvs[0].getStringValue("billpkname") + "='" + hvs[0].getStringValue("billpkvalue") + "'";
			String str_sql_2 = "delete from pub_wf_prinstance where id='" + _prinstanceid + "'"; //����ʵ��
			String str_sql_3 = "delete from pub_wf_dealpool   where prinstanceid='" + _prinstanceid + "'"; //�����
			String str_sql_4 = "delete from pub_task_deal     where prinstanceid='" + _prinstanceid + "'"; //ɾ����ҳ�ϵĴ���������
			getCommDMO().executeBatchByDS(null, new String[] { str_sql_1, str_sql_2, str_sql_3, str_sql_4 }); //
		}
	}

	/**
	 * ��ͣ������..
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void holdWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		String str_sql_1 = "select currbatchno from pub_wf_prinstance where id='" + _prinstanceid + "'"; //ȡ������ʵ���ĵ�ǰ����
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1); //
		int li_batchno = hvs_1[0].getIntegerValue("currbatchno").intValue(); //

		String str_sql_2 = "update pub_wf_prinstance set status='HOLD' where id='" + _prinstanceid + "'"; //���õ�ǰ״̬ΪHold

		String str_new_id = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_holdhistory"); //
		String str_actiontime = getTBUtil().getCurrTime(); //

		String str_sql_3 = "insert into pub_wf_holdhistory (id,prinstanceid,batchno,actiontype,actiontime,actioner) values (" + str_new_id + ",'" + _prinstanceid + "'," + li_batchno + ",'HOLD','" + str_actiontime + "','" + _loginuserid + "')"; //
		getCommDMO().executeBatchByDS(null, new String[] { str_sql_2, str_sql_3 }); //ִ����SQL.

	}

	/**
	 * ����ĳһ������
	 * ��ǰ���߼��ܼ�,����ֱ�ӽ�������Ϊ���������,�����������С���·���������Խ�����������,����������ڲ����ύ��!!!
	 * ����Ӧ�����ж��Ƿ񻹴���isProcess='N'�ļ�¼,������û�н���������?? �����,�����Ϊ��������������
	 * ����Ҳ�����ԭ�еķ���,��Ϊԭ�������ǲ�֧�ֶ�����ڲ����ύ��!
	 */
	public String endWorkFlow(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		String _dealpoolid = _wfParVO.getDealpoolid(); //��������id
		Vector v_sqls = new Vector(); //
		String str_currtime = getTBUtil().getCurrTime(); //ȡ�õ�ǰʱ��!!
		String str_loginusername = getUserCodeNameById(_loginuserid); //�õ���Ա����
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginuserid); //�õ���Ա��������
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
		sb_sql.append("and t1.id='" + getTBUtil().getNullCondition(_dealpoolid) + "'"); //idΪ��ʱ����һ�¡����/2012-05-11��
		HashVO[] hvs_dealpool = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //ȡ�õ�ǰʵ����Ϣ
		String str_parentInstanceId = hvs_dealpool[0].getStringValue("parentinstanceid"); //����ʵ������!! ����Ϊ��,�����Ϊ����Ҫ�����ж��ֵ������Ƿ�����Ĵ���,����ֵ����̶�������,����ҪΪ�������̴����µ�����!!
		String str_templetCode = hvs_dealpool[0].getStringValue("billtempletcode"); //����ģ��
		String str_tableName = hvs_dealpool[0].getStringValue("billtablename"); //����ģ��
		String str_queryTableName = hvs_dealpool[0].getStringValue("billquerytablename"); //����ı���
		String str_pkName = hvs_dealpool[0].getStringValue("billpkname"); //�����ֶ���
		String str_pkValue = hvs_dealpool[0].getStringValue("billpkvalue"); //����ֵ

		String str_curractivity = hvs_dealpool[0].getStringValue("curractivity"); //��ǰ����!!
		String str_batchno = hvs_dealpool[0].getStringValue("batchno"); //����!!
		String str_fromParentActivityId = hvs_dealpool[0].getStringValue("fromparentactivity"); //�Ӹ����̵��ĸ����ڴ�����
		String str_fromParentDealPoolId = hvs_dealpool[0].getStringValue("fromparentdealpoolid"); //���ĸ����������������!!!

		//��յ�ǰ����������־����,�����!!
		String str_sql_insert_taskoff = getInsertTaskOffSQL(_dealpoolid, _loginuserid, str_loginusername, str_currtime, _message, null, null, null, null, true, null); //����ʱû���ύ���κ��������,����xubmitto���ֶζ�Ϊ��!
		if (str_sql_insert_taskoff != null) {
			v_sqls.add(str_sql_insert_taskoff); //
		}
		v_sqls.add("delete from pub_task_deal where prdealpoolid='" + _dealpoolid + "'"); //ɾ����������!!!

		//����ǰ��������
		UpdateSQLBuilder isql_1 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + _dealpoolid + "'"); //
		isql_1.putFieldValue("isreceive", "Y"); //�Ƿ����
		isql_1.putFieldValue("receivetime", str_currtime); //����ʱ��
		isql_1.putFieldValue("issubmit", "Y"); //�Ƿ��ύ
		isql_1.putFieldValue("submittime", str_currtime); //�ύʱ��
		isql_1.putFieldValue("realsubmiter", _loginuserid); //ʵ���ύ����!
		isql_1.putFieldValue("realsubmitername", str_loginusername); //ʵ���ύ�˵�����!
		isql_1.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //ʵ���ύ�Ļ���!
		isql_1.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //ʵ���ύ�Ļ�������!
		isql_1.putFieldValue("submitisapprove", "Y"); //�ύ�Ľ��,��ͬ��/�˻�/
		isql_1.putFieldValue("submitmessage", _message); //�ύ�����
		isql_1.putFieldValue("submitmessagefile", _msgfile); //�ύ�ĸ���
		isql_1.putFieldValue("isprocesser", "Y"); //�Ƿ��ǽ���!!
		isql_1.putFieldValue("isprocess", "Y"); //����
		if (str_parentInstanceId == null || str_parentInstanceId.trim().equals("")) { //�����������ʵ��Ϊ��,��˵���Ǹ�����
			isql_1.putFieldValue("lifecycle", "E"); //�Ǹ����̽���!!
		} else {
			isql_1.putFieldValue("lifecycle", "EC"); //�������̽���!!
		}
		v_sqls.add(isql_1.getSQL()); //

		//�޸ĵ�ǰ��һ������,������Ҫ!
		if (!"4".equals(_wfParVO.getApproveModel())) { //ɢ��ģʽ����ô��
			v_sqls.add("update pub_wf_dealpool  set isprocess='Y' where prinstanceid='" + _prinstanceid + "' and batchno='" + str_batchno + "' and isprocess='N'"); //�޸ĵ�ǰ�������ѹ�.
		}

		//��дʵ������ʷִ����
		String[] str_instHistInfos = getNewSubmiterHist(_prinstanceid, _loginuserid, str_currtime, _message); //ȡ���µ��ύ�˵���ʷ�嵥,������ԭ���嵥�����ټ��ϱ���!
		String str_newsubmiterhist = str_instHistInfos[0]; //������ʷ
		String str_mylastsubmittime = str_instHistInfos[1]; //�ҵ������ʱ��!
		String str_mylastsubmitmsg = str_instHistInfos[2]; //�ҵ���������!

		UpdateSQLBuilder isql_2 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + _prinstanceid + "'"); //
		isql_2.putFieldValue("lastsubmiter", _loginuserid); //�����ύ��
		isql_2.putFieldValue("lastsubmitmsg", _message); //����ύ�˵����
		isql_2.putFieldValue("lastsubmitactivity", str_curractivity); //����ύ�Ļ���
		isql_2.putFieldValue("lastsubmitresult", "Y"); //����ύ�Ľ��,��ͬ��/�˻�
		isql_2.putFieldValue("lastsubmittime", str_currtime); //����ύ��ʱ��
		isql_2.putFieldValue("submiterhist", str_newsubmiterhist); //��ʷ�ύ�˵��嵥!
		isql_2.putFieldValue("mylastsubmittime", str_mylastsubmittime); //�ҵ�����ύʱ��,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
		isql_2.putFieldValue("mylastsubmitmsg", str_mylastsubmitmsg); //�ҵ�����ύ���,������ֻ��ʾһ��ҵ�񵥾�ʱ���ع�ʽ����!
		v_sqls.add(isql_2); //

		getCommDMO().executeBatchByDS(null, v_sqls); //ִ��һ��

		boolean isWFEnd = judgeWFisRealEnd(_prinstanceid); //�ж������Ƿ����!! �����ܻ�������������ŵ�����,����֧�����!!!����жϱ�������ִ�к�һ���SQL����,��Ϊ���������ñ�������isprocess=Y����ܽ���!! ��������������ԭ��!!
		//System.out.println("�����Ƿ������?=[" + isWFEnd + "]"); //
		String str_endResult = null; //
		if (isWFEnd) { //��������̵�ȷ�ǿ��Խ�����,�����������!!!
			boolean isExecInterceptAfterWfEnd = getTBUtil().getSysOptionBooleanValue("�����������̽���ʱ�Ƿ�Ҫִ�к�̨������", false); //
			//ִ�л��ڶ����ǰ��������!!!
			_wfParVO.setMessage(_message);//���̽�������������Ҫ�������������_wfParVO.setMessage()��ǰ̨����ֵΪ��ע��δ��ʵ�ʴ������ת����̨��Ϊ�˲�Ӱ����ǰ���̣���ֻ��������ִ��ǰ����һ�¡����/2016-12-15��
			_wfParVO.setMessagefile(_msgfile);

			if (isExecInterceptAfterWfEnd) {
				intercept2BeforeAction(_wfParVO, _loginuserid, _billVO, _endtype); //ִ�л��ڵ�ǰ�����ػ���
			}

			UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_prinstance", "id='" + _prinstanceid + "'"); //
			isql_3.putFieldValue("status", "END"); //����״̬ΪEND!!�ؼ����!!!
			isql_3.putFieldValue("endtype", _endtype); //���ý�������!!!
			isql_3.putFieldValue("endtime", str_currtime); //���ý���ʱ��!
			isql_3.putFieldValue("endbypoolid", _dealpoolid); //����Ϊ�������������!!��ǰ��û������ֶε�,����������ҵ��Ŀ����Ҫֻ��ʾ�����̵�������(���м�����������ʾ,��Ϊ̫����),�����Ҫһ��״̬�������¼��������������һ�����������!Ȼ����������ǽ��й���!
			isql_3.putFieldValue("currowner", (String) null); //��ǰӵ����Ϊ��
			isql_3.putFieldValue("curractivity", (String) null); //��ǰ����Ϊ��
			getCommDMO().executeUpdateByDS(null, isql_3); //������ִ��һ��,��Ϊ������Ҫ�ж��ֵ������Ƿ����,��Ҫ��������ԭ����ȡ���ݿ��!!!

			if (str_parentInstanceId != null && !str_parentInstanceId.trim().equals("")) { //���������ʵ���и�����,��˵�����Ǹ�������,��Ҫ�ж��ֵ������Ƿ����!!!
				//������Ҫ��SQL���߼�,���ж��ֵ������Ƿ����!!! �ҳ����������뱾�˵ĸ���һ��������ͬһ���������񴴽�������,���ҵ��ֵ�����,Ȼ���Ƿ���״̬��ΪEND��!!!
				String str_judgeBrotherInstIsEndSQL = "select count(*) c1 from pub_wf_prinstance where parentinstanceid='" + str_parentInstanceId + "' and fromparentdealpoolid='" + str_fromParentDealPoolId + "' and status<>'END'";
				String str_brotherInstEndCount = getCommDMO().getStringValueByDS(null, str_judgeBrotherInstIsEndSQL);
				int li_brotherInstEndCount = Integer.parseInt(str_brotherInstEndCount); //�ֵ�����û�н����ĸ���!!!
				//System.out.println("�����̵������ֵ����̽�����?=[" + li_brotherInstEndCount + "]"); //
				if (li_brotherInstEndCount == 0) { //�������״̬<>'END'�ĸ���Ϊ0,��˵���ֵ����̶�������!!,��ҪΪ�����̴����µ�����!!
					HashVO[] hvsFrom = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_dealpool where id='" + str_fromParentDealPoolId + "'"); //�ҳ�������ʵ���ĵ�һ�����������ĸ��������񴴽�,Ӧ�ý�����������һ��!!
					String str_newDealPoolId = getCommDMO().getSequenceNextValByDS(null, "S_PUB_WF_DEALPOOL"); //
					DealTaskVO dealTaskVO = new DealTaskVO(); //
					dealTaskVO.setFromActivityId(str_fromParentActivityId); //��Ϊ��������,���Բ�֪���ĸ���������
					dealTaskVO.setTransitionId(null); //��Ϊ��������,���Բ�֪���ĸ���������
					dealTaskVO.setCurrActivityId(hvsFrom[0].getStringValue("curractivity")); //��ǰ����,����������!

					dealTaskVO.setParticipantUserId(hvsFrom[0].getStringValue("participant_user")); //�����ߵ�ID
					dealTaskVO.setParticipantUserCode(hvsFrom[0].getStringValue("participant_usercode")); //
					dealTaskVO.setParticipantUserName(hvsFrom[0].getStringValue("participant_username")); //
					dealTaskVO.setParticipantUserDeptId(hvsFrom[0].getStringValue("participant_userdept")); //�����ߵĻ���
					dealTaskVO.setParticipantUserDeptName(hvsFrom[0].getStringValue("participant_userdeptname")); //�����߻���������!!!
					dealTaskVO.setAccrUserId(hvsFrom[0].getStringValue("participant_accruserid")); //��Ȩ��!

					int li_newParentInstanceBatchNo = getNewBatchNo(str_parentInstanceId); //�������µ�����!!!
					//�����ɷ���,����η�����ʱ,�����̼����,������ʾ���λ... ���ڻ����޸�.  2012-10-25 ���뵽ƽ̨. gaofeng
					String str_parentWFCreateByid = hvsFrom[0].getStringValue("parentwfcreatebyid");
					if (str_parentWFCreateByid == null || str_parentWFCreateByid.trim().equals(""))
						str_parentWFCreateByid = hvsFrom[0].getStringValue("createbyid"); //�ҵ�ԭ��������¼�Ĵ�����,��¼����,���������̼��������ʾʱ��������"�ع�"Ч��!!!���ֲ���ֱ�ӽ�createbyid����,��Ϊ���ʵĴ�����ϵ����Ҫ�е�!!��ֱ�ӳ���ʱ����ʹ�ñ��ʵĹ�����ϵ!!
					//�޸Ľ���
					if (str_parentWFCreateByid == null) { //���������ʵ���Ĵ����ߵĴ�����Ϊ��(������ʵ���ĸ��׵ĸ���,��үү!!),��˵���������Ǹ����!!�����������̵ĵ�һ������!Ϊ�˱�֤�����̵Ļع�Ч��,��Ҫ���⴦��Ϊ-99999,����ʾ�Ǹ����!Ȼ��ǰ̨������ʱ����-9999��ֱ�ӹ��ڸ������!
						str_parentWFCreateByid = "-99999"; //���-99999,��ʾ�Ǹ����!!!��Ϊֻ���ڴ��������̻ع�����ʱ���ݿ��и��ֶ�(parentwfcreatebyid)����ֵ,Ҫô�Ǹ�ʵ��ֵ,Ҫô����-99999,���Ϊ��,��˵�����ǻع�����!��ʹ���Լ���createbyid�������ͽṹ!!
					}

					String str_backWriteParentDealPoolSQL = getInsertDealPoolSQL(str_newDealPoolId, hvsFrom[0].getStringValue("prinstanceid"), hvsFrom[0].getStringValue("parentinstanceid"), hvsFrom[0].getStringValue("rootinstanceid"), hvsFrom[0].getStringValue("processid"), //
							_loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], dealTaskVO, "N", null, "", _dealpoolid, str_parentWFCreateByid, li_newParentInstanceBatchNo, str_currtime, null, null, "CB", false, null, null, null, null); //�������ǵ�¼��,�����߾���ԭ���Ľ�����,�������µ�����!!!

					String str_backWriteParentTaskSQL = getInsertTaskDealSQL(str_newDealPoolId, hvsFrom[0].getStringValue("prinstanceid"), hvsFrom[0].getStringValue("parentinstanceid"), hvsFrom[0].getStringValue("rootinstanceid"), _dealpoolid, // 
							str_templetCode, str_tableName, str_queryTableName, str_pkName, str_pkValue, "����ʱûȡ��", "", "[" + _billVO.getTempletName() + "]�����̻��ȫ������(" + _billVO.toString() + "),�봦��!", //��ǰ��������,����췢���˴�����������ʱû����Ϣ����,���ʹ������������û�������,�����ʹ����ǰ����ҳ������������ʾ����Ϊnull,�϶��޷�����!����ƴ����һ����ʾ����(xch-2012-01-11)!
							_loginuserid, str_loginusername, str_loginUserDeptIdName[0], str_loginUserDeptIdName[1], str_currtime, //
							hvsFrom[0].getStringValue("participant_user"), hvsFrom[0].getStringValue("participant_username"), hvsFrom[0].getStringValue("participant_userdept"), hvsFrom[0].getStringValue("participant_userdeptname"),//
							hvsFrom[0].getStringValue("participant_accruserid"), null, null, false); //

					String str_backWriteParentInstence = "update pub_wf_prinstance set curractivity='" + dealTaskVO.getCurrActivityId() + "',currowner=';" + dealTaskVO.getParticipantUserId() + ";' where id='" + hvsFrom[0].getStringValue("prinstanceid") + "'"; //��д��������ʵ���ĵ�ǰ״̬�뵱ǰ������,��ǰû�������ʱ��,����������ҵ��Ŀ�з���,�����̻���ʱ,�Ǹ����ڵ�������ûִ��!�������Ч�༭ʱȴ�ǻҵ�!
					getCommDMO().executeBatchByDS(null, new String[] { str_backWriteParentDealPoolSQL, str_backWriteParentTaskSQL, str_backWriteParentInstence }); ////�ҵ��������̵ĵ�ǰ���������̵��Ǹ���,���Ǹ��˻�д�µ�����,������,�����̻���ѽ�����,�������!!!
					String str_endMsg = getTBUtil().getSysOptionStringValue("�����������̽���ʱ����ʾ��", "���л�����,ϵͳ���Զ��ύ����췢����"); //
					str_endResult = str_endMsg + "��" + dealTaskVO.getParticipantUserName() + "��!"; //
				} else {
					//String str_createUserName = getCommDMO().getStringValueByDS(null, "select participant_username from pub_wf_dealpool where id='" + str_fromParentDealPoolId + "'");
					str_endResult = getTBUtil().getSysOptionStringValue("������������ĳһ��֧����ʱ����ʾ��", "���λ���ѽ���"); //
				}
			} else { //˵���Ǹ�����!!!
			}

			//ִ�л��ڶ���ĺ���������!!!
			if (isExecInterceptAfterWfEnd) { //���ݲ�����!!!
				intercept2AfterAction(_wfParVO, _loginuserid, _billVO, _endtype); //ִ�л��ڵĺ���������!!!
			}

			//ִ�����̶���ĺ���������!!
			if (_wfegbsintercept != null && _wfegbsintercept.indexOf(".") > 0) { //������̶�����������!!!����BS���������̶�����������еĽ������̺�ķ���!!!
				String str_billtype = _billVO.getStringValue("billtype"); //
				String str_busitype = _billVO.getStringValue("busitype"); //
				WLTHashMap parMap = new WLTHashMap(); //
				WorkFlowEngineBSIntercept bsIntercept = (WorkFlowEngineBSIntercept) Class.forName(_wfegbsintercept).newInstance(); //BS�˵��������̶����������
				bsIntercept.afterWorkFlowEnd(str_billtype, str_busitype, _wfParVO, _loginuserid, _billVO, _endtype, parMap); //ִ�н����󷽷�
			}
		}
		return str_endResult; //
	}

	//����Ա���ƻ�������̽��� �����/2013-05-29��
	public String endWorkFlow_admin(String _prinstanceid, WFParVO _wfParVO, String _loginuserid, String _message, String _msgfile, String _endtype, BillVO _billVO, String _wfegbsintercept) throws Exception {
		String str_endResult = "";
		String str_parentinstanceid = _wfParVO.getParentinstanceid();

		//����������ѱ�ɾ�� �����ظ�ɾ��
		String sql_self = "select count(*) cou from pub_wf_prinstance where id='" + _prinstanceid + "'";
		String str_brotherInstEndCount_self = getCommDMO().getStringValueByDS(null, sql_self);
		if (Integer.parseInt(str_brotherInstEndCount_self) == 0) {
			return "����������ѱ�ɾ��,�����ظ�ɾ��!";
		}

		//��������̽��� ��ɾ��
		String sql_end = "select count(*) cou from pub_wf_prinstance where parentinstanceid='" + str_parentinstanceid + "' and status<>'END'";
		String str_brotherInstEndCount_end = getCommDMO().getStringValueByDS(null, sql_end);
		if (Integer.parseInt(str_brotherInstEndCount_end) == 0) {
			return "����������ѽ���,�޷�ɾ��!";
		}

		//��������� ��������ʵ��δ����������ʵ���ѽ���
		String sql = "select count(*) cou from pub_wf_prinstance where parentinstanceid='" + str_parentinstanceid + "' and id<>'" + _prinstanceid + "' and status<>'END'";
		String str_brotherInstEndCount = getCommDMO().getStringValueByDS(null, sql);
		if (Integer.parseInt(str_brotherInstEndCount) == 0) {
			//ǿ�ƽ���������ʵ�����������������¼ ��Ϊ�п��ܱ�����ʵ�����������Ѿ����� ������Աѡ��Ĳ��Ǵ������������� �����ж�ʱ �������޷�����
			String upd_sql = "update pub_wf_dealpool  set isprocess='Y' where prinstanceid='" + _prinstanceid + "' and isprocess='N'";
			getCommDMO().executeBatchByDS(null, new String[] { upd_sql });

			str_endResult = endWorkFlow(_prinstanceid, _wfParVO, _loginuserid, _message, _msgfile, _endtype, _billVO, _wfegbsintercept);
		}

		//���������ʵ������������ؼ�¼
		ArrayList al_sqls = new ArrayList();
		al_sqls.add("delete from pub_task_deal where prinstanceid='" + _prinstanceid + "'"); //ɾ����������
		al_sqls.add("delete from pub_task_off where prinstanceid='" + _prinstanceid + "'"); //ɾ���Ѱ�����
		al_sqls.add("delete from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "'"); //ɾ�����̴����
		al_sqls.add("delete from pub_wf_prinstance where id='" + _prinstanceid + "'"); //ɾ������ʵ����

		//δ�������̷���������̵����̴�������ؼ�¼ ��Ӱ������ Ҳ��������У��

		getCommDMO().executeBatchByDS(null, al_sqls);

		if (str_endResult.equals("")) {
			str_endResult = "ɾ���ɹ�!";
		} else {
			str_endResult = "ɾ���ɹ�! �� " + str_endResult;
		}

		return str_endResult;
	}

	/**
	 * ȷ��(��ɾ��)��Ч������(���糭��,���ڵ�)!! ��ֱ�Ӱ��
	 * @param _taskId
	 * @param _dealPoolId
	 * @param _unEffectReason ��Ч��ԭ��,���糭��,���ڵ�!!! �������Ч������,���������ش���! ��Ϊ��ʹ������Ҳû������!!
	 */
	public String confirmUnEffectTask(String _dealPoolId, String _unEffectReason, String _loginUserId) throws Exception {
		String str_userName = getUserCodeNameById(_loginUserId); //
		String str_currtime = getTBUtil().getCurrTime(); //��ǰʱ��!
		String str_dealMsg = "ֱ��ȷ������(���类���͵������)"; //
		String[] str_loginUserDeptIdName = getUserDeptIdName(_loginUserId); //��¼��Ա�Ļ���id��name
		String str_sql_insert_taskoff = getInsertTaskOffSQL(_dealPoolId, _loginUserId, str_userName, str_currtime, str_dealMsg, null, null, null, null, false, _unEffectReason); //
		if (str_sql_insert_taskoff == null) {
			//����ֻ�Ǹ���ʾ��̫ƽ���Ž��鲻Ҫ�׳��쳣�����/2018-07-25��
			return "�������Ѳ�����,�����������ط���������,��û��Ҫ��ȷ���߼���!"; //����ǳ��͵���Ϣ,����ֵ��,������Ǳ������ط������,��Ϊ��,��ǰ�ͻ��������Ǳ��쳣��,�����ͻ��˸ĳɳԵ��쳣��!
		}
		String str_sql_delete_taskdeal = "delete from pub_task_deal where prdealpoolid='" + _dealPoolId + "'"; //ɾ���Ѱ�����

		UpdateSQLBuilder isql_3 = new UpdateSQLBuilder("pub_wf_dealpool", "id='" + _dealPoolId + "'"); //
		isql_3.putFieldValue("isreceive", "Y"); //�Ƿ����
		isql_3.putFieldValue("issubmit", "Y"); //�Ƿ����ύ
		isql_3.putFieldValue("submittime", str_currtime); //�ύ��ʱ��
		isql_3.putFieldValue("realsubmiter", _loginUserId); //ʵ���ύ����!
		isql_3.putFieldValue("realsubmitername", str_userName); //ʵ���ύ�˵�����!
		isql_3.putFieldValue("realsubmitcorp", str_loginUserDeptIdName[0]); //ʵ���ύ�Ļ���!
		isql_3.putFieldValue("realsubmitcorpname", str_loginUserDeptIdName[1]); //ʵ���ύ�Ļ�������!
		isql_3.putFieldValue("submitmessage", str_dealMsg); //�ύ�����

		getCommDMO().executeBatchByDS(null, new String[] { str_sql_insert_taskoff, str_sql_delete_taskdeal, isql_3.getSQL() }); //ִ��������SQL
		return null;
	}

	/**
	 * ����������,��Զ���ͣ���Ե�,�����̿�����ͣ�����,��Ҫ�����ڽ���ĳ����������ʱ���ͳ�������!!!
	 * @param _prinstanceid
	 * @param _loginuserid
	 * @throws Exception
	 */
	public void restartWorkflow(String _prinstanceid, String _loginuserid) throws Exception {
		String str_sql_1 = "select currbatchno from pub_wf_prinstance where id='" + _prinstanceid + "'"; //ȡ������ʵ���ĵ�ǰ����
		HashVO[] hvs_1 = getCommDMO().getHashVoArrayByDS(null, str_sql_1); //
		int li_batchno = hvs_1[0].getIntegerValue("currbatchno").intValue(); //

		String str_sql_2 = "update pub_wf_prinstance set status='RUN' where id='" + _prinstanceid + "'"; //���õ�ǰ״̬ΪHold

		String str_new_id = getCommDMO().getSequenceNextValByDS(null, "s_pub_wf_holdhistory"); //
		String str_actiontime = getTBUtil().getCurrTime(); //

		String str_sql_3 = "insert into pub_wf_holdhistory (id,prinstanceid,batchno,actiontype,actiontime,actioner) values (" + str_new_id + ",'" + _prinstanceid + "'," + li_batchno + ",'RESTART','" + str_actiontime + "','" + _loginuserid + "')"; //
		getCommDMO().executeBatchByDS(null, new String[] { str_sql_2, str_sql_3 }); //ִ����SQL.
	}

	/**
	 * ȡ��һ������������ʷ�����¼!
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
		HashVO[] hvs = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //��ȡ����ʷ��¼

		if (!_isHiddenMsg) { //����������δ�����ֱ�ӷ���!
			return hvs;
		}
		new WorkFlowBSUtil().dealHiddenMsg(hvs, _prinstanceId); //�����δ���.. 
		return hvs;
	}

	/**
	 * ��������ʵ����SQL,������Ҫ!
	 */
	private String getInsertPrinstanceSQL(String _newPrinstanceId, String _parentInstanceId, String _rootInstanceId, String _processid, String _templetCode, String _tableName, String _queryTabName, String _pkName, String _pkValue, String _createUserId, String _currOwner, String _createdActivityId, String _fromParentActivity, String _fromParentDealPoolId) {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_wf_prinstance"); //
		isql.putFieldValue("id", _newPrinstanceId); //����
		isql.putFieldValue("parentinstanceid", _parentInstanceId); //��������ʵ����Id,��Ϊ���������������̹���
		isql.putFieldValue("rootinstanceid", _rootInstanceId); //������ʵ��Id,��Ϊ���������������̹���
		isql.putFieldValue("processid", _processid); //���̶���id

		isql.putFieldValue("billtempletcode", _templetCode); //ģ�����
		isql.putFieldValue("billtablename", _tableName); //ҵ�񱣴��ѯ����
		isql.putFieldValue("billquerytablename", _queryTabName); //ҵ�񱣴����
		isql.putFieldValue("billpkname", _pkName); //ҵ���������
		isql.putFieldValue("billpkvalue", _pkValue); //ҵ�������ֵ

		isql.putFieldValue("creater", _createUserId); //���̴�����,��ǰ���ø����̵Ĵ����˵ĺ����ĳ���ʹ�������̵Ľ�����ֱ����Ϊ�����̴�����!����������Щ,��Ϊ�������еĲ���������ʹ�����̴�����ʱ,�͸���! ��Ϊ�����Ĳ�����һ�㶼�Ǵ����ߵ�ͬһ����!!
		isql.putFieldValue("createactivity", _createdActivityId); //�����Ļ���
		isql.putFieldValue("createtime", getCurrDateTime()); //������ʱ��
		isql.putFieldValue("endtime", (String) null); //���ʱ��,��Ϊ�մ���,����Ϊ��

		isql.putFieldValue("curractivity", _createdActivityId); //��ǰ����
		isql.putFieldValue("curractivityname", ""); //��ǰ��������
		isql.putFieldValue("currowner", ";" + _currOwner + ";"); //��ǰӵ����,����ؼ�!!!�ɵĻ��ƾ��Ǹ�������ж��Ƿ������ҵĴ�������!!
		isql.putFieldValue("currbatchno", "1"); //��ǰ����,һ��ʼ��

		isql.putFieldValue("lastsubmiter", _createUserId); //����ύ��
		isql.putFieldValue("lastsubmitername", _createUserId); //����ύ������
		isql.putFieldValue("lastsubmittime", (String) null); //����ύʱ��
		isql.putFieldValue("lastsubmitmsg", "��������"); //����ύ����Ϣ,��Ϊ�մ���,���Ըɴ�ֱ�ӽ�"��������"
		isql.putFieldValue("lastsubmitactivity", (String) null); //����ύ�Ļ���
		isql.putFieldValue("lastsubmitresult", (String) null); //����ύ�Ļ���

		//isql.putFieldValue("mylastsubmittime", (String) null); //�ҵ�����ύʱ��
		//isql.putFieldValue("mylastsubmitmsg", (String) null); //�ҵ�����ύ���

		isql.putFieldValue("fromparentactivity", _fromParentActivity); //�������ǴӸ����̵��ĸ����ڴ�����
		isql.putFieldValue("fromparentdealpoolid", _fromParentDealPoolId); //�������ǴӸ����̵��ĸ��������񴴽���!����ʵ�Ϳ�����Ϊ��һ�������̵Ĺ�ͬ��ʶ!!

		isql.putFieldValue("status", "START"); //״̬,����ʱΪSTART
		return isql.getSQL(); //����SQL
	}

	/**
	 * �������������SQL,������Ҫ!
	 */
	private String getInsertDealPoolSQL(String _newDealPoolId, String _prinstanceId, String _parentInstanceId, String _rootInstanceId, String _processID, String _createrId, String _createrName, String _createCorp, String _createCorpName, DealTaskVO _dealTaskVO, String _issubmit, String _isApprove, String _message, String _createbyid, String _parentwfcreatebyid, int _batchno, String _createTime,
			String _prioritylevel, String _dealtimelimit, String _lifecycle, boolean _isSelfCycleclick, String _selfFromRoleCode, String _selfFromRoleName, String _selfToRoleCode, String _selfToRoleName) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_wf_dealpool"); ////
		isql.putFieldValue("id", _newDealPoolId); //��������
		isql.putFieldValue("prinstanceid", _prinstanceId); //ʵ������
		isql.putFieldValue("parentinstanceid", _parentInstanceId); //��������ʵ������
		isql.putFieldValue("rootinstanceid", _rootInstanceId); //������ʵ������
		isql.putFieldValue("processid", _processID); //���̶����ID
		isql.putFieldValue("creater", _createrId); //���񴴽���
		isql.putFieldValue("creatername", _createrName); //���񴴽�������
		isql.putFieldValue("createcorp", _createCorp); //���񴴽��ߵĻ���,������ӦΪ��,��ʵ���������Ϊ��,��Ϊ����û�󶨻���!
		isql.putFieldValue("createcorpname", _createCorpName); //���񴴽��ߵĻ�������
		isql.putFieldValue("createtime", _createTime); //���񴴽�ʱ��
		isql.putFieldValue("prioritylevel", _prioritylevel); //�����̶� 
		isql.putFieldValue("dealtimelimit", _dealtimelimit); //Ҫ�����������
		isql.putFieldValue("batchno", _batchno); //�µ�����
		isql.putFieldValue("fromactivity", _dealTaskVO.getFromActivityId()); //���ĸ���������
		isql.putFieldValue("transition", _dealTaskVO.getTransitionId()); //���ĸ���������
		isql.putFieldValue("curractivity", _dealTaskVO.getCurrActivityId()); //��ǰ����
		isql.putFieldValue("currapprovemodel", _dealTaskVO.getCurrActivityApproveModel()); //��ǰ���ڵ�����ģʽ!!!
		isql.putFieldValue("participant_user", _dealTaskVO.getParticipantUserId()); //������(������),��Ϊ����һ���˿��Լ�ְ��������,���Ի�����ϲ����ߵ���������!!!����Ҫָ����A���ŵ���������B���ŵ��������������.
		isql.putFieldValue("participant_usercode", _dealTaskVO.getParticipantUserCode()); //������(������)����
		isql.putFieldValue("participant_username", _dealTaskVO.getParticipantUserName()); //������(������)����
		isql.putFieldValue("participant_userdept", _dealTaskVO.getParticipantUserDeptId()); //�����ߵ���������,��Ϊ���Լ�ְ�������,���������ʾ�Ǵ������һ������. 
		isql.putFieldValue("participant_userdeptname", _dealTaskVO.getParticipantUserDeptName()); //�����ߵ�������������,��Ϊ���Լ�ְ�������,���������ʾ�Ǵ������һ������.
		isql.putFieldValue("participant_userrole", _dealTaskVO.getParticipantUserRoleId()); //�����߽�ɫ
		isql.putFieldValue("participant_userrolename", _dealTaskVO.getParticipantUserRoleName()); //�����߽�ɫ
		isql.putFieldValue("participant_accruserid", _dealTaskVO.getAccrUserId()); //��Ȩ��id
		isql.putFieldValue("participant_accrusercode", _dealTaskVO.getAccrUserCode()); //��Ȩ�˱���
		isql.putFieldValue("participant_accrusername", _dealTaskVO.getAccrUserName()); //��Ȩ������
		isql.putFieldValue("isrejecteddirup", (_dealTaskVO.isRejectedDirUp() ? "Y" : "N")); //�Ƿ�ֱ�Ӵ�صķ�ʽ

		isql.putFieldValue("isselfcycleclick", _isSelfCycleclick ? "Y" : "N"); //�Ƿ�����ѭ���ڲ���ת��??
		isql.putFieldValue("selfcycle_fromrolecode", _selfFromRoleCode); //��ѭ������Ķ�Ӧ�ġ�С���̡��Ļ��ڱ���
		isql.putFieldValue("selfcycle_fromrolename", _selfFromRoleName); //��ѭ������Ķ�Ӧ�ġ�С���̡��Ļ������ƣ���ʵ���ǽ�ɫ���ƣ���

		isql.putFieldValue("selfcycle_torolecode", _selfToRoleCode); //��ѭ������Ķ�Ӧ�ġ�С���̡��Ļ��ڱ���
		isql.putFieldValue("selfcycle_torolename", _selfToRoleName); //��ѭ������Ķ�Ӧ�ġ�С���̡��Ļ������ƣ���ʵ���ǽ�ɫ���ƣ���

		isql.putFieldValue("isdeal", "N"); //�Ƿ���,��Ϊ�մ�������ΪN
		isql.putFieldValue("isreceive", "N"); //�Ƿ����,��Ϊ�մ���,���Ա�ȻΪN
		isql.putFieldValue("isprocess", "N"); //���������Ƿ����
		isql.putFieldValue("ispass", "N"); //���������Ƿ�ͨ��
		isql.putFieldValue("isccto", (_dealTaskVO.isCCTo() ? "Y" : "N")); //�Ƿ���
		isql.putFieldValue("issubmit", _issubmit); //�Ƿ��ύ,��Ϊ�����˻����ύ���ַ�ʽ,����һ��ʼ����Ƴɱ�ʾͬ������! ������ͳһ�ĳ����Ƿ�����ύ,��û����ʱ��N,��������Y
		isql.putFieldValue("submitisapprove", _isApprove); //�ύ�Ľ��,����ͬ���ύ���ͬ���ύ!
		isql.putFieldValue("submitmessage", _message); //�ύ���
		isql.putFieldValue("createbyid", _createbyid); //��¼�¸�����������Ϊ��һ�����񴴽���,�����Ϣ�ǳ���Ҫ!!!
		isql.putFieldValue("parentwfcreatebyid", _parentwfcreatebyid); //��������¼�ĸ���������������˭������!!��������������ʱ,�ص����������̵Ĵ�������ʱ,��Ҫ��¼�������̵�ԭ������˭������,������������ʾʱ���ܸ������ֵ���ع�Ч�����ֳ���!���ֲ���ֱ�ӽ�createbyid����,��Ϊ��ֱ�ӳ���ʱ����Ҫʵ�ʵĴ�����ϵ!
		isql.putFieldValue("lifecycle", _lifecycle); //��������,ȡֵ��C(����������)/CC(����������)/EC(����������)/E(����������)
		return isql.getSQL(); //����SQL
	}

	/**
	 * ���촴�����������SQL,������Ҫ!!
	 */
	private String getInsertTaskDealSQL(String _newDealPoolId, String _prinstanceid, String _parentInstanceId, String _rootInstanceId, String _createByDealPoolId, String _templetcode, String _tabname, String _queryTableName, String _pkName, String _pkValue, String _tabItemValue, String _dealMsg, String _msg, String _creater, String _createrName, String _createrCorpId, String _createrCorpName,
			String _createtime, String _dealuser, String _dealuserName, String _dealUserCorp, String _dealUserCorpName, String _accrUserId, String _prioritylevel, String _dealtimelimit, boolean _isCCTo) throws Exception {
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_task_deal"); //
		isql.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_TASK_DEAL")); //
		//isql.putFieldValue("taskclass", ""); //�������
		isql.putFieldValue("prinstanceid", _prinstanceid); //����ʵ��ID
		isql.putFieldValue("parentinstanceid", _parentInstanceId); //������ʵ��id
		isql.putFieldValue("rootinstanceid", _rootInstanceId); //������ʵ��id
		isql.putFieldValue("prdealpoolid", _newDealPoolId); //��������ID
		isql.putFieldValue("createbydealpoolid", _createByDealPoolId); //���ĸ��������񴴽���
		isql.putFieldValue("templetcode", _templetcode); //ģ�����
		isql.putFieldValue("tabname", _tabname); //����
		//isql.putFieldValue("querytabname", _queryTableName); //��ѯ�ı���,����Ժ�Ҫ��!
		isql.putFieldValue("pkname", _pkName); //�����ֶ���
		isql.putFieldValue("pkvalue", _pkValue); //�����ֶ�ֵ
		isql.putFieldValue("tabitemvalue", _tabItemValue); //����ĳ���ֶε�ֵ,��������¼�����,����������Ļ�,����ζ�Ų�Ҫ����ҵ�����!

		//�����ߵ������Ϣ
		isql.putFieldValue("creater", _creater); //������
		isql.putFieldValue("creatername", _createrName); //����������
		isql.putFieldValue("createcorp", _createrCorpId); //��������id(�������˵���������)
		isql.putFieldValue("createcorpname", _createrCorpName); //������������(�������˵���������)
		isql.putFieldValue("createtime", _createtime); //����ʱ��
		isql.putFieldValue("createrdealmsg", _dealMsg); //�������ύ�����
		isql.putFieldValue("msg", _msg); //ƽ̨���ɵ���ʾ��Ϣ,��ʲô����ʲô�����ύ����������!

		//������(������)�����Ϣ
		isql.putFieldValue("dealuser", _dealuser); //������id,�ǳ��ؼ�,�Ժ�͸�������ж��Ƿ����ڵ�¼��Ա������!
		isql.putFieldValue("dealusername", _dealuserName); //����������
		isql.putFieldValue("dealusercorp", _dealUserCorp); //�����˵Ļ���id
		isql.putFieldValue("dealusercorpname", _dealUserCorpName); //�����˵Ļ�������
		isql.putFieldValue("accruserid", _accrUserId); //��Ȩ��id
		isql.putFieldValue("isccto", (_isCCTo ? "Y" : "N")); //�Ƿ��ǳ��͵���Ϣ
		isql.putFieldValue("prioritylevel", _prioritylevel); //���ȼ�
		isql.putFieldValue("dealtimelimit", _dealtimelimit); //����ʱ������
		if (!TBUtil.isEmpty(sendSMSImpl)) {
			try {
				Object obj = Class.forName(sendSMSImpl).newInstance();
				if (obj instanceof SendSMSIFC && (!_isCCTo && !"��������".equals(_msg))) {
					SendSMSIFC sendsms = (SendSMSIFC) obj;
					sendsms.send(_dealuser, _msg, "����������", _creater, null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//ʵ���Ķ�����Ϣ
		isql.putFieldValue("islookat", "N"); //�Ƿ��Ѳ鿴,����ʱĬ��ΪN
		isql.putFieldValue("lookattime", (String) null); //�鿴ʱ��,����ʱĬ��Ϊ��
		return isql.getSQL(); //
	}

	/**
	 * �����Ѱ������SQL,������Ҫ,��Ҫ�����ű�pub_wf_prinstance,pub_wf_dealpool,pub_task_dealpool,pub_task_off���ǹ��������ֵĺ��ı�
	 * ����������������Ѱ�����(��һ��˸�λ��),��һ������������������һ�������������ɵ�,�����˵���еĴ����������Ѱ��������һ��Ӧ����һ�����ͽṹ!!!
	 * ���������չʾ,���Ѵ��������ɫ��ʾ,δ�����ú�ɫ��ʾ,�ύ����ĳ��ͼ����ʾ,�˻ص���ĳ��ͼ����ʾ! ���͵���б����ʾ�ȸ���Ч��! δ�Ķ����ô�����ʾ,�ȵ�,
	 * ��֮,ʹ�ø���Ч����һ��Ч���ǳ�Ư�������ͽṹ,�Ӷ�һ�����֪���������̴�����ʷ,����Ч���ȱ��νṹ���Ÿ������Ч��,�����ܸ�������֪���ύ˳��,��˭����˭!
	 * ��ͬһ��rootinstanceid�е����м�¼�϶��ǿ��Թ���һ������!!
	 * @return
	 */
	private String getInsertTaskOffSQL(String _dealPoolId, String _realDealUser, String _realDealUserName, String _dealtime, String _dealMsg, String _submittouser, String _submittousername, String _submittocorp, String _submittocorpname, boolean _isSubmit, String _confirmReason) throws Exception {
		HashVO[] taskHVOs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_task_deal where prdealpoolid='" + _dealPoolId + "'"); //���ҳ���������!
		if (taskHVOs == null || taskHVOs.length == 0) {
			return null; //
		}
		String[] str_copyCols = new String[] { "taskclass", "prinstanceid", "parentinstanceid", "rootinstanceid", "prdealpoolid", "createbydealpoolid", "templetcode", "tabname", "pkname", "pkvalue", "tabitemvalue", // 
				"creater", "creatername", "createcorp", "createcorpname", "createtime", "createrdealmsg", "msg", //�����ߵ������Ϣ
				"dealuser", "dealusername", "dealusercorp", "dealusercorpname", "accruserid", "isccto", "prioritylevel", "dealtimelimit" //�������ߵ������Ϣ
		}; //
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_task_off"); //
		isql.putFieldValue("id", getCommDMO().getSequenceNextValByDS(null, "S_PUB_TASK_OFF")); //����!!
		isql.putFieldValue("taskdealid", taskHVOs[0].getStringValue("id")); //�������������,����֪���Ǵ��ĸ����񿽱�������!
		for (int i = 0; i < str_copyCols.length; i++) {
			isql.putFieldValue(str_copyCols[i], taskHVOs[0].getStringValue(str_copyCols[i])); //ֱ�Ӹ���
		}

		//ʵ�ʴ���������Ϣ
		isql.putFieldValue("realdealuser", _realDealUser); //ʵ�ʴ�����,��Ϊ������Ȩ����,����Ȩ��������˶����ܻᴦ��,����ʵ�ʴ����˲���֪����˭,������Ҫ��¼����!
		isql.putFieldValue("realdealusername", _realDealUserName); //ʵ�ʴ���������,�ǹ���/���Ƶ�����
		isql.putFieldValue("dealtime", _dealtime); //����ʱ��,��ʵ�ʴ����ʱ��
		isql.putFieldValue("dealmsg", _dealMsg); //�������,��ʵ�ʴ������!Ҳ���ǹ������ύ�����е�����������������!��Ӧ�û������pub_task_deal���е���һ����¼��createrdealmsg�ֶ���,�����ڴ���������л�Ӧ�и��ֶν�createbytaskoffid,��ĳ��������������ĳ���Ѱ��������ɵ�

		isql.putFieldValue("submittouser", _submittouser); //�ύ��ʲô��
		isql.putFieldValue("submittousername", _submittousername); //�ύ��ʲô�˵�����
		isql.putFieldValue("submittocorp", _submittocorp); //�ύ��ʲô����
		isql.putFieldValue("submittocorpname", _submittocorpname); //�ύ���Ļ�������!!

		isql.putFieldValue("submitorconfirm", (_isSubmit ? "submit" : "confirm")); //�ύ����ȷ��!!
		isql.putFieldValue("confirmreason", _confirmReason); //ȷ�ϵ�ԭ��!!

		return isql.getSQL(); //����SQL
	}

	//���Ѱ������д������������ȥ��SQL
	private String getCancelToTaskDealSQL(String _dealpoolId) throws Exception {
		HashVO[] taskHVOs = getCommDMO().getHashVoArrayByDS(null, "select * from pub_task_off where prdealpoolid='" + _dealpoolId + "'"); //���ҳ���������!
		if (taskHVOs == null || taskHVOs.length <= 0) { //����ǾɵĻ���,�����û���Ѱ�����(��Ϊ��ʱ������û�����ű���)!!!���봦��!!
			return null; //
		}
		String[] str_copyCols = new String[] { "taskclass", "prinstanceid", "parentinstanceid", "rootinstanceid", "prdealpoolid", "createbydealpoolid", "templetcode", "tabname", "pkname", "pkvalue", "tabitemvalue", // 
				"creater", "creatername", "createcorp", "createcorpname", "createtime", "createrdealmsg", "msg", //�����ߵ������Ϣ
				"dealuser", "dealusername", "dealusercorp", "dealusercorpname", "accruserid", "isccto", "prioritylevel", "dealtimelimit" //�������ߵ������Ϣ
		}; //
		InsertSQLBuilder isql = new InsertSQLBuilder("pub_task_deal"); //
		isql.putFieldValue("id", taskHVOs[0].getStringValue("taskdealid")); //����ԭ��������id
		for (int i = 0; i < str_copyCols.length; i++) {
			isql.putFieldValue(str_copyCols[i], taskHVOs[0].getStringValue(str_copyCols[i])); //ֱ�Ӹ���
		}
		return isql.getSQL(); //����SQL
	}

	/**
	 * �Ƿ������һ������,�ѵ���ؼ���֮һ!!!
	 * 
	 * @param _prinstanceid
	 * @param _currActivityVO
	 * @return
	 */
	private boolean isLastCommit(String _prinstanceid, Integer _batchno, String _approveModel, Integer _approveNumber) throws Exception {
		// ȡ�û��ڵ���ϸ��Ϣ
		if (_approveModel == null || _approveModel.equals("1") || _approveModel.equals("4")) { //�������ռʽ,��ֱ�Ӷ�˵���������ύ��!!!
			return true;
		} else { // ����ǻ�ǩ,��Ҫ�����Ƿ������һ���ύ��!!!����������һ���ύ��,��˵����������ύ����!!
			String str_sql = "select id from pub_wf_dealpool where prinstanceid='" + _prinstanceid + "' and batchno='" + _batchno + "' and issubmit='N' and (isccto='N' or isccto is null)"; // ȡ����ǰ���̣���ǰ���ڣ�����δ����ļ�¼,Ҫ�����͵��޳���!
			HashVO[] vos = getCommDMO().getHashVoArrayByDS(null, str_sql); //
			if (vos.length == 1) { // ���ֻʣ��һ��û�ύ!!
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * ȡ����һ������,��Ҫ�ж�����!!!�Ƚ��鷳!!Ҳ���ѵ�֮һ!!
	 * @param string2 
	 * @param string 
	 * @param firstWFParVO 
	 * 
	 * @param _currActivityVO
	 * @param _billvo
	 * @return
	 */
	private NextTransitionVO[] getNextTransitionVOs(WFParVO _firstWFParVO, String _prinstanceid, String _dealpoolid, HashVO _hashVO, BillVO _billvo, String _dealType) throws Exception {
		// �ҳ��м�����ȥ������
		// �ֱ�ִ��ÿ�����ϵ�����,
		// ���ؽ��ΪTrue�������ߵ�Ŀ¼���!
		Vector vector_tmp = new Vector(); //�ȴ�������,���ڴ洢!

		String str_fromActivity_id = _hashVO.getStringValue("curractivity"); //��Դ���ڵ�id
		String str_fromActivity_type = _hashVO.getStringValue("curractivity_activitytype"); //��Դ���ڵ�����
		String str_fromActivity_isselfcycle = _hashVO.getStringValue("curractivity_isselfcycle"); //�Ƿ���ѭ��

		//���ж��Ƿ������ѭ������,�����,���Ȳ��ҳ���ǰ����,����Դ������Ŀ�껷����һ����!!�����ǽ���ѭ�����ڵ�һ����λ�� !!!
		if ("Y".equals(str_fromActivity_isselfcycle) || "SEND".equals(_dealType)) { //�����Դ��������ѭ��!!!
			NextTransitionVO nextTransitionVO = new NextTransitionVO(); //
			nextTransitionVO.setSortIndex(0); //Ϊ�˱�֤��ѭ������Զ���ڵ�һλ,ǿ����0,�������ڵ�һ����!����������

			nextTransitionVO.setFromactivityId(str_fromActivity_id); //Դ����
			nextTransitionVO.setFromactivityCode(_hashVO.getStringValue("curractivity_code")); //���ڱ���
			nextTransitionVO.setFromactivityWFName(_hashVO.getStringValue("curractivity_wfname")); //��������
			nextTransitionVO.setFromactivityUIName(_hashVO.getStringValue("curractivity_uiname")); //��������
			nextTransitionVO.setFromactivityApprovemodel(_hashVO.getStringValue("curractivity_approvemodel")); //����ģʽ

			nextTransitionVO.setToactivityId(str_fromActivity_id); //Ŀ�껷��,��Ϊ����ѭ��,����Ŀ�껷����Դ����һ��!!
			nextTransitionVO.setToactivityCode(_hashVO.getStringValue("curractivity_code")); //Ŀ�껷�ڱ���
			nextTransitionVO.setToactivityWFName(_hashVO.getStringValue("curractivity_wfname")); //Ŀ�껷������
			nextTransitionVO.setToactivityUIName(_hashVO.getStringValue("curractivity_uiname")); //Ŀ�껷������
			nextTransitionVO.setToactivityBelongDeptGroupName(_hashVO.getStringValue("curractivity_belongdeptgroup")); //Ŀ�껷�ڵ������������,�ڿ粿�Ż���������ʱ��Ҫ����ʾ����ʱͬʱ��ʾ�����������!!! ��ҵ�Ŀͻ���ǿ������������,��Ϊ���Ǻ͹����������ǵ�Notes������,����ͻỷ������������!

			nextTransitionVO.setToactivityType(str_fromActivity_type); //Ŀ�껷������
			nextTransitionVO.setToactivityApprovemodel(_hashVO.getStringValue("curractivity_approvemodel")); //Ŀ�껷�ڵ�����ģʽ.
			nextTransitionVO.setToactivityShowparticimode(_hashVO.getStringValue("curractivity_showparticimode")); //Ŀ�껷�ڵ���ʾ�����ߵ�ģʽ!
			nextTransitionVO.setToactivityParticipate_user(_hashVO.getStringValue("curractivity_participate_user")); //�������!!!!
			nextTransitionVO.setToactivityParticipate_corp(_hashVO.getStringValue("curractivity_participate_corp")); //����Ļ���!!!
			nextTransitionVO.setToactivityParticipate_group(_hashVO.getStringValue("curractivity_participate_group")); //����Ľ�ɫ!!
			nextTransitionVO.setToactivityParticipate_dynamic(_hashVO.getStringValue("curractivity_participate_dynamic")); //��̬������
			nextTransitionVO.setToactivityCanselfaddparticipate(_hashVO.getStringValue("curractivity_canselfaddparticipate"));

			nextTransitionVO.setToactivityCcto_user(_hashVO.getStringValue("curractivity_ccto_user")); //Ŀ�껷���϶���ĳ��͵���Ա
			nextTransitionVO.setToactivityCcto_corp(_hashVO.getStringValue("curractivity_ccto_corp")); //Ŀ�껷���϶���ĳ��͵Ļ���
			nextTransitionVO.setToactivityCcto_role(_hashVO.getStringValue("curractivity_ccto_role")); //Ŀ�껷���϶���ĳ��͵Ľ�ɫ
			nextTransitionVO.setToactivityIsSelfCycle(_hashVO.getBooleanValue("curractivity_isselfcycle", false)); //�Ƿ���ѭ��!
			nextTransitionVO.setToactivitySelfCycleMap(_hashVO.getStringValue("curractivity_selfcyclerolemap")); //�Ƿ���ѭ��!

			nextTransitionVO.setPasscondition(true); //��ѭ��,��ֱ��ͨ��!
			vector_tmp.add(nextTransitionVO); //����
		}

		if (!"SEND".equals(_dealType)) {
			String str_sql = "select * from pub_wf_transition where fromactivity='" + str_fromActivity_id + "'"; //�ҳ����е���
			LinkForeignTableDefineVO lfvo_from = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,uiname,activitytype,approvemodel", "id", "fromactivity"); //
			LinkForeignTableDefineVO lfvo_to = new LinkForeignTableDefineVO("pub_wf_activity", "code,wfname,uiname,belongdeptgroup,activitytype,approvemodel,isselfcycle,selfcyclerolemap,showparticimode,participate_user,participate_corp,participate_group,participate_dynamic,ccto_user,ccto_corp,ccto_role,extconfmap,canselfaddparticipate", "id", "toactivity"); //

			HashVO[] hvs = getCommDMO().getHashVoArrayBySubSQL(null, str_sql, new LinkForeignTableDefineVO[] { lfvo_from, lfvo_to }); //
			for (int i = 0; i < hvs.length; i++) {
				boolean bo_ifpasscondition = true; //�Ƿ�ͨ������!Ĭ����ͨ����! ����ﴦ��ʽ���������
				String str_condition = hvs[i].getStringValue("conditions"); //
				JepFormulaParseAtWorkFlow jepFormula = new JepFormulaParseAtWorkFlow(_firstWFParVO, _prinstanceid, _dealpoolid, _billvo); //�����̴���������ʵ����������������������������ʽ����
				HashMap tsConfMap = null; //�����ϵ���չ����
				//��������϶���������!�����֮��
				if (str_condition != null && !str_condition.trim().equals("")) { //��������϶���������������
					str_condition = str_condition.trim(); //
					str_condition = getTBUtil().replaceAll(str_condition, "\r", ""); //
					str_condition = getTBUtil().replaceAll(str_condition, "\n", ""); //
					String[] str_conItems = null; //
					if (str_condition.indexOf("��") > 0) { //������ڹ�ʽ�о��зֺŵ����,�����ʹ�á����ķֺš��ָ�������
						str_conItems = getTBUtil().split(str_condition, "��"); //�÷ֺ����
					} else {
						str_conItems = getTBUtil().split(str_condition, ";"); //�÷ֺ����
					}

					//��ǰֻ��һ����ʽ!�������Ӷ�����ʽ�������������ø�����չ����������
					if (str_conItems.length >= 2) { //�������������!
						//�ȴ���ʽ��������!
						Object obj_1 = jepFormula.execFormula(str_conItems[0]); //ִ�еĽ��!!! 
						if (("" + obj_1).equalsIgnoreCase("true")) { //����Ƿ��ص���True
							bo_ifpasscondition = true; //
						} else if (("" + obj_1).equalsIgnoreCase("false")) { //
							bo_ifpasscondition = false; //
						} else {
							bo_ifpasscondition = false; //
						}
						//�ټ������
						Object obj_2 = jepFormula.execFormula(str_conItems[1]); //ִ�еĽ��!!!
						tsConfMap = (HashMap) obj_2; //
					} else { //���ֻ��һ������!
						Object obj = jepFormula.execFormula(str_conItems[0]); //ִ�еĽ��!!! ����
						if (obj != null) {
							if (obj instanceof HashMap) { //������Ǹ���ϣ��!�����ֻ����չ������û���������壡
								bo_ifpasscondition = true; //
								tsConfMap = (HashMap) obj; //
								//System.out.println("ֻ��һ������,����HashMap,������:" + tsConfMap); //
							} else {
								if (obj.toString().equalsIgnoreCase("true")) { //����Ƿ��ص���True
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
					nextTransitionVO.setCode(hvs[i].getStringValue("code")); //���ߵı���
					nextTransitionVO.setWfname(hvs[i].getStringValue("wfname")); //
					nextTransitionVO.setUiname(hvs[i].getStringValue("uiname")); //
					nextTransitionVO.setMailSubject(hvs[i].getStringValue("mailsubject")); //�ʼ�����
					nextTransitionVO.setMailContent(hvs[i].getStringValue("mailcontent")); //�ʼ�����
					nextTransitionVO.setDealtype(hvs[i].getStringValue("dealtype")); //���ߵĴ�������,��Submit��Reject,��ǰ���������˻ش����!�������������Ƿ�������ջ���ֻ��ʾ����Ļ��������߹���!
					nextTransitionVO.setIntercept(hvs[i].getStringValue("intercept")); //����������
					nextTransitionVO.setReasoncodesql(hvs[i].getStringValue("reasoncodesql")); //

					nextTransitionVO.setFromactivityId(hvs[i].getStringValue("fromactivity")); //Դ����
					nextTransitionVO.setFromactivityCode(hvs[i].getStringValue("fromactivity_code")); //Դ����
					nextTransitionVO.setFromactivityWFName(hvs[i].getStringValue("fromactivity_wfname")); //Դ����
					nextTransitionVO.setFromactivityUIName(hvs[i].getStringValue("fromactivity_uiname")); //Դ����
					nextTransitionVO.setFromactivityApprovemodel(hvs[i].getStringValue("fromactivity_approvemodel")); //Դ���ڵ�����ģʽ

					nextTransitionVO.setToactivityId(hvs[i].getStringValue("toactivity")); //Ŀ�껷��
					nextTransitionVO.setToactivityCode(hvs[i].getStringValue("toactivity_code")); //Ŀ�껷��
					nextTransitionVO.setToactivityWFName(hvs[i].getStringValue("toactivity_wfname")); //Ŀ�껷��
					nextTransitionVO.setToactivityUIName(hvs[i].getStringValue("toactivity_uiname")); //Ŀ�껷��
					nextTransitionVO.setToactivityBelongDeptGroupName(hvs[i].getStringValue("toactivity_belongdeptgroup")); //Ŀ�껷�ڵ����������������!
					nextTransitionVO.setToactivityType(hvs[i].getStringValue("toactivity_activitytype")); //Ŀ�껷������
					nextTransitionVO.setToactivityApprovemodel(hvs[i].getStringValue("toactivity_approvemodel")); //Ŀ�껷�ڵ�����ģʽ.
					nextTransitionVO.setToactivityShowparticimode(hvs[i].getStringValue("toactivity_showparticimode")); //Ŀ�껷�ڵ���ʾ�����ߵ�ģʽ!
					nextTransitionVO.setToactivityParticipate_user(hvs[i].getStringValue("toactivity_participate_user")); //�������!!!!

					nextTransitionVO.setToactivityParticipate_corp(hvs[i].getStringValue("toactivity_participate_corp")); //����Ļ���!!!
					nextTransitionVO.setToactivityParticipate_group(hvs[i].getStringValue("toactivity_participate_group")); //����Ľ�ɫ!!
					nextTransitionVO.setToactivityParticipate_dynamic(hvs[i].getStringValue("toactivity_participate_dynamic")); //��̬������
					nextTransitionVO.setToactivityCanselfaddparticipate(hvs[i].getStringValue("toactivity_canselfaddparticipate"));//�Ƿ��������ӽ����˰�ť
					nextTransitionVO.setToactivityIsSelfCycle(hvs[i].getBooleanValue("toactivity_isselfcycle", false)); //�Ƿ���ѭ��!
					nextTransitionVO.setToactivitySelfCycleMap(hvs[i].getStringValue("toactivity_selfcyclerolemap")); //��ѭ���Ķ���

					nextTransitionVO.setToactivityCcto_user(hvs[i].getStringValue("toactivity_ccto_user")); //Ŀ�껷���϶���ĳ��͵���Ա
					nextTransitionVO.setToactivityCcto_corp(hvs[i].getStringValue("toactivity_ccto_corp")); //Ŀ�껷���϶���ĳ��͵Ļ���
					nextTransitionVO.setToactivityCcto_role(hvs[i].getStringValue("toactivity_ccto_role")); //Ŀ�껷���϶���ĳ��͵Ľ�ɫ
					nextTransitionVO.setToactivitywnparam(hvs[i].getStringValue("toactivity_extconfmap"));//���ܲ���
					nextTransitionVO.setPasscondition(bo_ifpasscondition); //

					nextTransitionVO.setExtConfMap(tsConfMap); //������չ������
					vector_tmp.add(nextTransitionVO); //����
				}
			}
		}//ת����ʵ������ô��
		if (vector_tmp.size() == 0) {
			if (str_fromActivity_type.equals("END")) { //
				return null;
			} else {
				System.err.println("��[" + str_fromActivity_id + "]��ȡ��һ����ʱû��ȡ��һ��!(�ͻ�˵:������δ������Ĳ���,�봦��������ύ!)"); //
				throw new WLTAppException("�Ҳ��������ύ�Ľ�����,�����������������ֿ���:\r\n1.�㵱ǰ��������δ������Ĳ��赼������������,�봦��������ύ!\r\n2.�����������,��ǰ�ѵ��������һ��,��ֱ�ӵ������������ť�������!"); ////
			}
		}

		return (NextTransitionVO[]) vector_tmp.toArray(new NextTransitionVO[0]); //
	}

	/**
	 * ȡ��һ�������ϵ����п��ܲ����ߵĲ���!!!!���ؽ�Ҫ!!!
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

		ArrayList al_return = new ArrayList(); //�ȶ���һ�������б����ڲ���!!

		//��������������������,������Ա,����,��ɫ,��̬������,���ĸ����Զ�Ϊ��,��ɶ��û��,��Ĭ����ʹ�ñ��˲���!!!�������������Ӳ����ߵĹ���,����ֻ�ܻ���ͼ,��������߾��������̵�Ч��!!!�Ӷ��򻯿���!
		if (!"START".equals(_curractivityType) && !"END".equals(_curractivityType) && (_participan_user == null || _participan_user.trim().equals("")) && (_participan_corp == null || _participan_corp.trim().equals("")) && (_participan_role == null || _participan_role.trim().equals("")) && (_dynamic_participan == null || _dynamic_participan.trim().equals(""))) {
			getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "��û��һ�����������,ϵͳĬ���Զ�ʹ�ù�ʽ[getWFCorp(\"type=��¼�����ڻ���\",\"������̽�Ƿ��������=N\");]���м���!<br>"); //
			WorkFlowParticipantBean parBeanByCorpAreaAndRole_receive = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), "getWFCorp(\"type=��¼�����ڻ���\",\"������̽�Ƿ��������=N\");", null, false); //���ʲô��û����,��Ĭ�ϱ�ʾֻ�鱾����!!!���Ҳ������ӽ��,�������������Ƿ����г�,����ҳ�̫�����,������������!
			if (parBeanByCorpAreaAndRole_receive != null) {
				al_return.add(parBeanByCorpAreaAndRole_receive); ////
			}
		} else {
			//�������ѭ��,��ǿ��ʹ�ñ������ڻ�����Χ!
			if (_hvs_dealrecord.getBooleanValue("curractivity_isselfcycle", false) && !_hvs_dealrecord.getStringValue("curractivity_selfcyclerolemap", "").equals("") && dealActivityVO.getFromActivityId() != null && dealActivityVO.getFromActivityId().equals(dealActivityVO.getActivityId())) { //������Լ�ѭ��
				getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "������Ŀ�껷������ѭ��,�Ҷ����ɫӳ��,�ұ�������Ŀ�껷����ͬһ��(�����ڲ���ת,�����ǵ�һ�ν���),��ϵͳĬ���Զ�ʹ�ù�ʽ[getWFCorp(\"type=��¼�����ڻ����ķ�Χ\",\"������̽�Ƿ��������=N\");]���м���!<br>"); //
				WorkFlowParticipantBean parBeanByCorpAreaAndRole_receive = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), "getWFCorp(\"type=��¼�����ڻ����ķ�Χ\",\"������̽�Ƿ��������=N\");", null, false); //���ʲô��û����,��Ĭ�ϱ�ʾֻ�鱾����!!!���Ҳ������ӽ��,�������������Ƿ����г�,����ҳ�̫�����,������������!
				if (parBeanByCorpAreaAndRole_receive != null) {

					//�������˵Ĳ������ύ�˵Ĳ���ͬ�� �����������Ա��ְ���� �����/2013-03-13��
					if (getTBUtil().getSysOptionBooleanValue("�������Ƿ�����Ա��ְ", false)) {
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
			} else { //���������ѭ��!
				if (_hvs_dealrecord.getBooleanValue("curractivity_isselfcycle", false) && !_hvs_dealrecord.getStringValue("curractivity_selfcyclerolemap", "").equals("")) {
					getInitContext().addCurrSessionCustStrInfoByKey("$���ڲ����߼������", "���ɹ�����Ŀ�껷������ѭ��,���н�ɫӳ��,����Ϊ������[" + dealActivityVO.getFromActivityId() + "/" + _fromactivity + "]��Ŀ�껷��[" + dealActivityVO.getActivityId() + "/" + _curractivitycode + "]��һ��,˵���ǵ�һ�ν���!����Ȼʹ����������������ǿ��ʹ��[��¼�����ڻ����ķ�Χ]����!<br>"); //
				}
				//��̬�������...
				WorkFlowParticipantBean parBeanByUserDefine_receive = getOneActivityParticipanUserByUserDefine(_participan_user, false); //
				if (parBeanByUserDefine_receive != null) {
					al_return.add(parBeanByUserDefine_receive); ////
				}

				//����������ɫ�Ľ���!!! �������Ҫ�ļ���!!!.������
				WorkFlowParticipantBean parBeanByCorpAreaAndRole_receive = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), _participan_corp, _participan_role, false); //
				if (parBeanByCorpAreaAndRole_receive != null) {
					if (!_hvs_dealrecord.getStringValue("parentinstanceid", "").equals("")) { //��ǩ
						//�������˵Ĳ������ύ�˵Ĳ���ͬ�� �����������Ա��ְ���� �����/2013-035-25��
						if (getTBUtil().getSysOptionBooleanValue("�������Ƿ�����Ա��ְ", false)) {
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

				//����̬������,��Ҫ�������,�е��鷳!!�����ֻ����������������,������ϵͳ��ʵ����ôʹ����!
				if (_dynamic_participan != null && !_dynamic_participan.trim().equals("")) { //�����̬�����߲�Ϊ��,����������Ҳ�ǿ��Զ�ѡ��!!!,�����ﻹ������һ��ѭ��!!!��һ����̬�����߶���,����һ��ҳǩ!
					String[] str_dynamicItems = getTBUtil().split(_dynamic_participan, ";"); //�����ж��..
					for (int k = 0; k < str_dynamicItems.length; k++) { //����ÿ����̬�����߶���
						HashVO[] hvs_dynamic = getCommDMO().getHashVoArrayByDS(null, "select id,name,prefixcondition,corptype,rolename,classname from pub_wf_dypardefines where name='" + str_dynamicItems[k] + "'"); //
						if (hvs_dynamic == null || hvs_dynamic.length == 0) {//���Ϊ��,�ٸ���idȡһ��..�ҽ���̬�����ߵĶ����Ϊ��ѡ���պ����ݿ��б���ĳ���id�ˣ���֮ǰ���������...gaofeng
							hvs_dynamic = getCommDMO().getHashVoArrayByDS(null, "select id,name,prefixcondition,corptype,rolename,classname from pub_wf_dypardefines where id='" + str_dynamicItems[k] + "'"); //
						}
						if (hvs_dynamic.length == 0) {
							throw new WLTAppException("û��ȡ�ö�̬�����߶���[" + _dynamic_participan + "]�е�[" + str_dynamicItems[k] + "]��̬�����߶���,����ƽ̨����->����������->��̬�����߶����ж���֮!"); //
						}

						String str_prefixcondition = hvs_dynamic[0].getStringValue("prefixcondition"); //��̬�������ж���Ļ�����Χ���㷽ʽ!!
						String str_corptype = hvs_dynamic[0].getStringValue("corptype"); //��̬�������ж���Ļ�������!!
						String str_rolename = hvs_dynamic[0].getStringValue("rolename"); //��̬�������ж���Ľ�ɫ
						String str_dypclassname = hvs_dynamic[0].getStringValue("classname"); //

						WorkflowDynamicParticipateIfc dynamicPars = null; //
						if (str_prefixcondition.equals("ȫ��")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_AllBankRole(str_rolename); //ȫ��
						} else if (str_prefixcondition.equals("��������ĳ�����͵��ϼ�����")) { //����Ҫ�ļ��㷽ʽ,�����Ժ���Ψһ�ļ��㷽ʽ!����������·���������ҵ�ָ�����͵Ļ����µ����л���!!
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_TreePathCorpChooseRole(str_dynamicItems[k], _loginUserDeptID, str_corptype, str_rolename); //
						} else if (str_prefixcondition.equals("������")) { //
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameDeptChooseRole(_loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("�����в���")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_fenghbm", "�����в���", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("������")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_fengh", "������", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("����ҵ���ֲ�")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_shiybfb", "����ҵ���ֲ�", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("����ҵ��")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_shiyb", "����ҵ��", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("��֧��")) { //
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_zhih", "��֧��", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("�����в���")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_SameBankChooseRole("bl_zhonghbm", "�����в���", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("�����в���/֧��/��ҵ���ֲ�")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_BatchDeptChooseRole(new String[] { "bl_fenghbm", "bl_zhih", "bl_shiybfb" }, "�����в���/֧��/��ҵ���ֲ�", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("���ͻ������в���/֧��/��ҵ���ֲ�")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_BatchDeptChooseRole(new String[] { "bl_fenghbm", "bl_zhih", "bl_shiybfb" }, "���ͻ������в���/֧��/��ҵ���ֲ�", _billvo.getStringValue("create_deptid"), str_rolename); //
						} else if (str_prefixcondition.equals("�����в���/֧��/��ҵ���ֲ�/���в���")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_BatchDeptChooseRole(new String[] { "bl_fenghbm", "bl_zhih", "bl_shiybfb", "bl_zhonghbm" }, "�����в���/֧��/��ҵ���ֲ�/���в���", _loginUserDeptID, str_rolename); //
						} else if (str_prefixcondition.equals("����")) {
							dynamicPars = new cn.com.infostrategy.bs.workflow.WDPUser_HeadBankRole(_loginUserDeptID, str_rolename); //����
						} else if (str_prefixcondition.equals("�Զ�����")) {
							if (str_dypclassname.indexOf("(") > 0) { //����в�������ö�Ӧ�����Ĺ��췽��!!!
								TBUtil tbutil = new TBUtil(); //
								String str_csname = str_dypclassname.substring(0, str_dypclassname.indexOf("(")); //
								String str_par = str_dypclassname.substring(str_dypclassname.indexOf("(") + 1, str_dypclassname.indexOf(")")); //
								str_par = tbutil.replaceAll(str_par, "\"", ""); //��˫����ȥ��!!!
								String[] str_pars = tbutil.split(str_par, ","); //�Զ���Ϊ�ָ��,��������
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
							throw new WLTAppException("�ڶ�̬�����߶���[" + _dynamic_participan + "]�з���δ֪������[" + str_prefixcondition + "]!"); //
						}
						_hvs_dealrecord.setAttributeValue("DyRole", str_rolename);
						WorkFlowParticipantBean parBean = dynamicPars.getDynamicParUsers(_loginuserid, _billvo, _hvs_dealrecord, _fromTransition, _fromTransition_dealtype, _fromactivity, _curractivitycode, _curractivityname); //
						if (parBean != null) {
							al_return.add(parBean); //�����Ϊ�������!
						}
					}
				}
			}
		}

		//��������˳�����Ա
		WorkFlowParticipantBean parBeanByUserDefine_ccTo = getOneActivityParticipanUserByUserDefine(_ccToUser, true); //
		if (parBeanByUserDefine_ccTo != null) {
			al_return.add(parBeanByUserDefine_ccTo); ////
		}

		//���ݳ��ͻ����볭�ͽ�ɫ�Ķ���,���㳭�͵���!!
		WorkFlowParticipantBean parBeanByCorpAreaAndRole_ccTo = getOneActivityParticipanUserByCorpAreaAndRole(_hvs_dealrecord.getStringValue("prinstanceid_creater"), _ccToCorp, _ccToRole, true); //
		if (parBeanByCorpAreaAndRole_ccTo != null) {
			al_return.add(parBeanByCorpAreaAndRole_ccTo); ////
		}

		WorkFlowParticipantBean[] allParsbeans = (WorkFlowParticipantBean[]) al_return.toArray(new WorkFlowParticipantBean[0]); //
		//������Ȩ,��������ص���Ա��,��������Ȩ��,��Ҫ��ʾ�����˵���Ϣ!!!
		if (_allAccrditAndProxyMap.size() > 0) { //�����������Ȩ��Ϣ!!
			for (int i = 0; i < allParsbeans.length; i++) { //����������Ȩ����Ϣ!!!���仰˵���������������в����߻��Ǹ������,��Ȩ��������ǵ������ڵ�!!
				WorkFlowParticipantUserBean[] parUserBeans = allParsbeans[i].getParticipantUserBeans(); //���в������Ա!!!
				if (parUserBeans != null) { //�����Ϊ��!!!
					ArrayList al_temp = new ArrayList(); //
					for (int j = 0; j < parUserBeans.length; j++) { //����ÿһ����
						al_temp.add(parUserBeans[j]); //�Ƚ�ԭ������Ա����!���µĻ��Ʋ��Ǹ��ǻ�����!!��������������!!
						String str_olduserid = parUserBeans[j].getUserid(); //ԭ��������Ĳ�����,
						if (_allAccrditAndProxyMap.containsKey(str_olduserid)) { //������ָ��û�������Ȩ����,��������Ȩ����������!!!!��Ҫ���滻����!!
							HashVO hvo_accr = getWFBSUtil().getAccredProxyUserVO(_allAccrditAndProxyMap, _accrdModel, str_olduserid); ////��ؼ��ļ���!!!!ȡ�����������Ȩ����һ���˵����!!!
							if (hvo_accr != null) {
								WorkFlowParticipantUserBean cloneProxyUserBean = parUserBeans[j].clone(); //��¡һ��!����Ŀ�¡�������ع���!

								//��������Ȩʱһ�������ֱ�﷽ʽ:һ����XX�쵼(��ȨXX����)����һ���ǣ�XX����(XX�쵼��Ȩ),�Ժ�����Ӧ���и�����,����ͬ�Ŀͻ�ϲ����ͬ�ı�﷽ʽ!!�����
								String str_username_1 = parUserBeans[j].getUsername() + "(��Ȩ" + hvo_accr.getStringValue("proxyusername") + ")"; //XX�쵼(��ȨXX����)
								String str_username_2 = hvo_accr.getStringValue("proxyusername") + "(" + parUserBeans[j].getUsername() + "��Ȩ)"; //XX����(XX�쵼��Ȩ)
								cloneProxyUserBean.setUsername(str_username_1); //������(������)������Ҫ������Ȩ˼��!!!�������˵�����Ҫƴ��һ��!!

								cloneProxyUserBean.setAccrUserid(hvo_accr.getStringValue("proxyuserid")); //��������˾��Ǵ�����!
								cloneProxyUserBean.setAccrUsercode(hvo_accr.getStringValue("proxyusercode")); //��������˾��Ǵ�����!
								cloneProxyUserBean.setAccrUsername(hvo_accr.getStringValue("proxyusername")); //��������˾��Ǵ�����!
								al_temp.add(cloneProxyUserBean); //�����¡�Ĵ�������Ϣ!
							} else {
								System.out.println("û���ҵ���Ȩģ��!"); //
							}
						}
					}

					WorkFlowParticipantUserBean[] newParUserBeans = (WorkFlowParticipantUserBean[]) al_temp.toArray(new WorkFlowParticipantUserBean[0]); //������Ȩ���Ժ����Ϣ!
					allParsbeans[i].setParticipantUserBeans(newParUserBeans); //����һ��!!!!
				}
			}
		}

		return allParsbeans; //
	}

	/**
	 * ֱ�Ӹ�����Ա������������!
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
		parBean_static_user.setParticipantDeptType(null); //��̬��ɫʱ������ʲô?
		parBean_static_user.setParticipantDeptId(null); //��̬��ɫʱ������ʲô?

		String[] str_items = getTBUtil().split(_userDefine, ";"); //
		HashVO[] hvs_user_role_corp = getCommDMO().getHashVoArrayByDS(null, "select id userid,code usercode,name username from pub_user where id in (" + getTBUtil().getInCondition(str_items) + ") and (status is null or status <> 'D')"); //������ȥ��ѯ,������������Ա��¼,��Ҫ��Ψһ�Ժϲ�!!����ͬ����Ա,Ҫ��Ψһ�Ժϲ�!!!!
		if (hvs_user_role_corp == null || hvs_user_role_corp.length == 0) { //���û�ҵ�һ��������,��ֱ�ӷ��ؿ�����!!!
			return null;
		}

		HashVO[] hvs_realUser = appendMyCorpAndRoleInfo(hvs_user_role_corp); //�ϲ���ɫ�����!!!!����һ���˵����н�ɫ������г���!!
		parBean_static_user.setParticiptMsg("��̬�����˶���[" + _userDefine + "],���ҵ�[" + hvs_realUser.length + "]��!"); //
		if (hvs_realUser.length > 0) {
			WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs_realUser.length]; //
			for (int i = 0; i < hvs_realUser.length; i++) {
				userBeans[i] = new WorkFlowParticipantUserBean(); //
				userBeans[i].setUserid(hvs_realUser[i].getStringValue("id")); //����������.
				userBeans[i].setUsercode(hvs_realUser[i].getStringValue("code")); //�����߱���.
				userBeans[i].setUsername(hvs_realUser[i].getStringValue("name")); //����������.

				userBeans[i].setUserdeptid(hvs_realUser[i].getStringValue("userdept")); //����id
				userBeans[i].setUserdeptcode(hvs_realUser[i].getStringValue("userdeptname")); //��������
				userBeans[i].setUserdeptname(hvs_realUser[i].getStringValue("userdeptname")); //��������!!

				userBeans[i].setUserroleid(hvs_realUser[i].getStringValue("roleid")); //�����߽�ɫ.
				userBeans[i].setUserrolecode(hvs_realUser[i].getStringValue("rolename")); //�����߽�ɫ����.
				userBeans[i].setUserrolename(hvs_realUser[i].getStringValue("rolename")); //�����߽�ɫ����.

				userBeans[i].setParticipantType("��̬�������"); //
				userBeans[i].setSuccessParticipantReason("���㾲̬�����˵�����"); //
				userBeans[i].setCCTo(_isCCTo); //
			}
			parBean_static_user.setParticipantUserBeans(userBeans); //
		}

		return parBean_static_user;
	}

	/**
	 * ���ݻ������ɫ��������в�����,�⽫�ǽ����������м�������ߵ�Ψһ������ĵķ���! �����ύ�볭��!!!
	 * ����ԭ����ȡ��������Χ+��ɫ�����˵Ľ���,���л�����Χ��֧�����ܵĹ�ʽ����,����ɫ�ǲ��ö�ѡ����,�Ժ��ɫҲ���Կ��ǲ��ù�ʽ,�������ָ����ɫ����ģ��Ʒ��,��
	 * @param _roleDefine
	 * @param _corpDefine
	 * @param _isCCTo
	 * @return
	 * @throws Exception
	 */
	private WorkFlowParticipantBean getOneActivityParticipanUserByCorpAreaAndRole(String _wfCreater, String _corpDefine, String _roleDefine, boolean _isCCTo) throws Exception {
		if ((_roleDefine == null || _roleDefine.trim().equals("")) && (_corpDefine == null || _corpDefine.trim().equals(""))) { //������߶�Ϊ��,��ֱ�ӷ��ؿ�
			return null;
		}
		StringBuilder sb_msgInfo = new StringBuilder("�������ɫ��������,��������[" + _corpDefine + "],��ɫ����[" + _roleDefine + "]"); //
		WorkFlowParticipantBean parBean = new WorkFlowParticipantBean(); //
		parBean.setParticipantDeptType(null); //��̬��ɫʱ������ʲô?
		parBean.setParticipantDeptId(null); //��̬��ɫʱ������ʲô?

		String[] str_roleIds = null; //���еĽ�ɫ
		String str_roleInCondition = null; //��ɫ��In����..
		if (_roleDefine != null) {
			str_roleIds = getTBUtil().split(_roleDefine, ";"); //��ɫ��ɫ��Ids
			str_roleInCondition = getTBUtil().getInCondition(str_roleIds); //
		}

		String[] str_corpIds = null; //
		String str_inCorpCondition = null; //
		if (_corpDefine != null && !_corpDefine.trim().equals("")) { //��������˹�ʽ
			HashMap[] formulaMaps = getWFParse(_corpDefine); //���ݻ�����ʽ�õ�ʵ�ʵĻ���!!
			if (formulaMaps != null && formulaMaps.length > 0) { //���ȡ����ֵ!!
				for (int i = 0; i < formulaMaps.length; i++) { //
					formulaMaps[i].put("���̴�����", _wfCreater); //��ÿ��������ǿ�м���"���̴�����"!!
				}
				str_corpIds = new WorkFlowEngineGetCorpUtil().getCorps(formulaMaps, getAllCorpsCacheMap()); //�õ����еĻ���!!!��������ؼ����㷨!!! ��������������������!!!�����
				if (str_corpIds == null || str_corpIds.length == 0) { //���û�ҵ�!!!�����ж�Ϊnull����������/2016-12-18��
					sb_msgInfo.append(",��û�ҵ�һ������!"); //
					parBean.setParticiptMsg(sb_msgInfo.toString()); ////
					return parBean; //����!!!
				}
			}
			str_inCorpCondition = getCommDMO().getSubSQLFromTempSQLTableByIDs(str_corpIds); //ȡ�û�����in����!!!
		}

		//��ؼ���SQL,���SQL֮����û�й�����ɫ�����������������,��Ϊ���������,���Ұ������������Ľ�����Ѿ���С��,����������Java�߼��д�������!!!
		StringBuffer sb_sql = new StringBuffer();
		String userCondition = getTBUtil().getSysOptionStringValue("��������Ա״̬��������", "status <> 'D'");//̫ƽ������Ч��Ա״̬Ϊ0�������Ӳ��������/2017-10-19��
		if (!(_roleDefine == null || _roleDefine.trim().equals("")) && !(_corpDefine == null || _corpDefine.trim().equals(""))) { //���ͬʱ�л������ɫ����,��ȡ���ߵĽ���!!
			sb_sql.append("select ");
			sb_sql.append("t1.userid, ");
			sb_sql.append("t3.code usercode, ");
			sb_sql.append("t3.name username ");
			sb_sql.append("from pub_user_post t1,pub_user_role t2,pub_user t3 "); //
			sb_sql.append("where t1.userid=t2.userid  ");
			sb_sql.append("and t1.userid=t3.id ");
			sb_sql.append("and t1.userdept in (" + str_inCorpCondition + ")"); //����������ָ���ķ�Χ��!
			sb_sql.append("and (t3.status is null or t3." + userCondition + ") "); //״̬������D
			sb_sql.append("and t2.roleid   in (" + str_roleInCondition + ") "); //��ɫid
		} else { //���ֻ�л�����ֻ�н�ɫ����!!! 
			if (_roleDefine != null && !_roleDefine.trim().equals("")) { //���ֻ�н�ɫ,��ֻҪֱ���ý�ɫ����!!
				sb_sql.append("select ");
				sb_sql.append("t1.userid, ");
				sb_sql.append("t2.code usercode, ");
				sb_sql.append("t2.name username ");
				sb_sql.append("from pub_user_role t1,pub_user t2 ");
				sb_sql.append("where t1.userid=t2.id ");
				sb_sql.append("and t1.roleid in (" + str_roleInCondition + ") "); //
				sb_sql.append("and (t2.status is null or t2." + userCondition + ") "); //״̬������D
			} else { //�����ֻ�л�������
				sb_sql.append("select ");
				sb_sql.append("t1.userid, ");
				sb_sql.append("t2.code usercode, ");
				sb_sql.append("t2.name username ");
				sb_sql.append("from pub_user_post t1,pub_user t2 ");
				sb_sql.append("where t1.userid=t2.id ");
				sb_sql.append("and t1.userdept in (" + str_inCorpCondition + ") ");
				sb_sql.append("and (t2.status is null or t2." + userCondition + ") "); //״̬������D
			}
		}
		WorkFlowParticipantUserBean[] userBeans = getParticipanUserBeansBySQL(sb_sql.toString(), _isCCTo, "�������ɫ��������", "����������ɫ��������"); //
		if (userBeans == null) {
			return null;
		}
		parBean.setParticipantUserBeans(userBeans); ////

		sb_msgInfo.append(",���ҵ�[" + userBeans.length + "]��!"); //
		parBean.setParticiptMsg(sb_msgInfo.toString()); ////
		return parBean; //����!!!
	}

	//���ݲ�ѯ��Ա��SQL�������̲����߶���WorkFlowParticipantUserBean,����������Ҫ���߼��ǲ�������Ա�����н�ɫ���������Ϣ!!!
	private WorkFlowParticipantUserBean[] getParticipanUserBeansBySQL(String sb_sql, boolean _isCCTo, String _parType, String _successReason) throws Exception {
		HashVO[] hvsUsers = getCommDMO().getHashVoArrayByDS(null, sb_sql); //������ȥ��ѯ,������������Ա��¼,��Ҫ��Ψһ�Ժϲ�!!����ͬ����Ա,Ҫ��Ψһ�Ժϲ�!!!!
		if (hvsUsers == null || hvsUsers.length == 0) { //���û�ҵ�һ��������,��ֱ�ӷ��ؿ�����!!!
			return null;
		}
		HashVO[] hvs_realUser = appendMyCorpAndRoleInfo(hvsUsers); //���ϻ������ɫ!!����ǰ�Ļ�����������!!! �ǳ���Ҫ���߼�!!! ���ڻ���ֻ����default=Y��,Ӧ��������Ҫ��!!
		WorkFlowParticipantUserBean[] userBeans = new WorkFlowParticipantUserBean[hvs_realUser.length]; //
		for (int i = 0; i < hvs_realUser.length; i++) { //����ʵ����Ա!!!
			userBeans[i] = new WorkFlowParticipantUserBean(); //
			userBeans[i].setUserid(hvs_realUser[i].getStringValue("id")); //����������.
			userBeans[i].setUsercode(hvs_realUser[i].getStringValue("code")); //�����߱���.
			userBeans[i].setUsername(hvs_realUser[i].getStringValue("name")); //����������.

			userBeans[i].setUserdeptid(hvs_realUser[i].getStringValue("userdept")); //����id
			userBeans[i].setUserdeptcode(hvs_realUser[i].getStringValue("userdeptcode")); //��������
			userBeans[i].setUserdeptname(hvs_realUser[i].getStringValue("userdeptname")); //��������!!

			userBeans[i].setUserroleid(hvs_realUser[i].getStringValue("roleid")); //�����߽�ɫ.
			userBeans[i].setUserrolecode(hvs_realUser[i].getStringValue("rolecode")); //�����߽�ɫ����.
			userBeans[i].setUserrolename(hvs_realUser[i].getStringValue("rolename")); //�����߽�ɫ����.

			userBeans[i].setParticipantType(_parType); //
			userBeans[i].setSuccessParticipantReason(_successReason); //
			userBeans[i].setCCTo(_isCCTo); //
		}
		return userBeans; //
	}

	//��Ϊ�ڼ��㹫ʽʱ��ʱ������������!Ϊ���������,����ʱ�ͼ����,Ȼ�󴫽�ȥ!!!
	private HashMap getAllCorpsCacheMap() throws Exception {
		if (this.allCorpsCacheMap != null) {
			return allCorpsCacheMap; //
		}
		allCorpsCacheMap = wfBSUtil.createAllCorpsCacheMap(); //����!!!
		return allCorpsCacheMap; //
	}

	/**
	 * ����Ա�����ϻ������ɫ��Ϣ,��ǰ�ķ���������,�������г�ĳ����Ա�����л������ɫ!
	 * ��������ǳ���Ҫ!
	 * @param _hvsUsers
	 * @return
	 * @throws Exception
	 */
	private HashVO[] appendMyCorpAndRoleInfo(HashVO[] _hvsUsers) throws Exception {
		LinkedHashMap lnkMap = new LinkedHashMap(); //
		for (int i = 0; i < _hvsUsers.length; i++) { ////
			String str_userid = _hvsUsers[i].getStringValue("userid"); //
			if (!lnkMap.containsKey(str_userid)) { //������������û�,����ͬ��ֻ��һ��!!
				HashVO hvoitem = new HashVO(); //
				hvoitem.setAttributeValue("id", str_userid); //
				hvoitem.setAttributeValue("code", _hvsUsers[i].getStringValue("usercode")); //
				hvoitem.setAttributeValue("name", _hvsUsers[i].getStringValue("username")); //
				lnkMap.put(str_userid, hvoitem); //
			}
		}
		String[] str_allUserIds = (String[]) lnkMap.keySet().toArray(new String[0]); //���е��û�id

		//�����˵����л���,�Ժ�Ҫ��չ�ҳ�����������·��,����ֻ����ʾ����
		StringBuilder sb_sql_myAllCorp = new StringBuilder(); //
		sb_sql_myAllCorp.append("select ");
		sb_sql_myAllCorp.append("t1.userid, ");
		sb_sql_myAllCorp.append("t1.userdept, ");
		sb_sql_myAllCorp.append("t2.code userdeptcode, ");
		sb_sql_myAllCorp.append("t2.name userdeptname, ");
		sb_sql_myAllCorp.append("t1.isdefault ");
		sb_sql_myAllCorp.append("from pub_user_post t1,pub_corp_dept t2 ");
		sb_sql_myAllCorp.append("where t1.userdept=t2.id ");
		sb_sql_myAllCorp.append("and t1.userid in (" + getCommDMO().getSubSQLFromTempSQLTableByIDs(str_allUserIds) + ") "); //����
		sb_sql_myAllCorp.append("order by t1.userid,t1.isdefault "); //����

		HashVO[] hvs_myAllCorps = getCommDMO().getHashVoArrayByDS(null, sb_sql_myAllCorp.toString()); //�ҳ����������������˵����ڻ���

		//�������ҳ���������,Ȼ���ҳ���Щ������·������,Ȼ�����!!!!
		for (int i = 0; i < hvs_myAllCorps.length; i++) {
			HashVO hvoitem = (HashVO) lnkMap.get(hvs_myAllCorps[i].getStringValue("userid")); //�ɵ�����
			if ("Y".equals(hvs_myAllCorps[i].getStringValue("isdefault")) || hvoitem.getStringValue("userdept") == null) { //�������Ĭ�ϻ���,����û�оɵ�
				hvoitem.setAttributeValue("userdept", hvs_myAllCorps[i].getStringValue("userdept")); //�������id...
			}
			hvoitem.setAttributeValue("userdeptcode", hvoitem.getStringValue("userdeptcode", "") + hvs_myAllCorps[i].getStringValue("userdeptcode") + ";"); //��������
			hvoitem.setAttributeValue("userdeptname", hvoitem.getStringValue("userdeptname", "") + hvs_myAllCorps[i].getStringValue("userdeptname") + ";"); //��������
		}

		//�����˵����н�ɫ
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
			HashVO hvoitem = (HashVO) lnkMap.get(hvs_myAllRoles[i].getStringValue("userid")); //�ɵ�����
			if (hvoitem.getStringValue("roleid") == null) {
				hvoitem.setAttributeValue("roleid", ";"); //
			}
			hvoitem.setAttributeValue("roleid", hvoitem.getStringValue("roleid") + hvs_myAllRoles[i].getStringValue("roleid") + ";"); //��ɫid
			hvoitem.setAttributeValue("rolecode", hvoitem.getStringValue("rolecode", "") + hvs_myAllRoles[i].getStringValue("rolecode") + ";"); //��ɫ����
			hvoitem.setAttributeValue("rolename", hvoitem.getStringValue("rolename", "") + hvs_myAllRoles[i].getStringValue("rolename") + ";"); //��ɫ����

		}
		HashVO[] hvs_realUser = (HashVO[]) lnkMap.values().toArray(new HashVO[0]); //ʵ�ʵ������û��嵥,������Ҫ�������в���!!	
		getWFBSUtil().appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvs_realUser, "userdept", "userdeptname"); //���ϻ������Ƶ�ȫ��!!! ��������ȫ·����,��ʵ������Ҫ�ص�����������ĩ���!!
		return hvs_realUser; //
	}

	/**
	 * ȡ�ù������л������幫ʽ�Ľ���!!!���������ʽ:
	 * getWFCorp(\"type=������\");getWFCorp(\"type=��������Χ\","�Ƿ�����ӽ��=Y");getWFCorp(\"type=����ĳ�����ϼ�����\",\"��������=���в���=>����;֧��=>����;���в���=>����\");
	 * @param _formula
	 * @return һ��getWFCorp()������һ��HashMap,�м����ͷ��ؼ���,ÿ����ϣ���е�key��value����һ���ַ�������"="�ָ���ǰ��������!!֮�����������,��Ϊ�˿���չ��,���������µĲ���ʱ����չ,API�Ĳ�������Ҳֻ��һ��HashMap,������Ϊ���������仯����Ĵ���!!
	 * ����չ���ѳ�Ϊһ���ǳ���Ҫ������,����ƽ̨���пؼ�,���,��������һ������,������Ӧ��ʹ������key=value,���ַ�������,Ȼ��ƴ��һ��HashMap����Ϊ��������API������ȥ,��������"����"�Ŀ���չ!!!!
	 * ���ݹ�ʽ�����������Ӧ����һ������id��һά����!!!Ȼ����ǻ������ɫ���н�������!!!
	 */
	private HashMap[] getWFParse(String _formulaPar) {
		try {
			String str_formula = _formulaPar; //
			str_formula = str_formula.trim(); //
			str_formula = getTBUtil().replaceAll(str_formula, " ", ""); //ȥ���ո�
			str_formula = getTBUtil().replaceAll(str_formula, "\r", ""); //�滻����
			str_formula = getTBUtil().replaceAll(str_formula, "\n", ""); //�滻����
			if (!str_formula.endsWith(";")) { //��������ԷֺŽ�β,���Ϸֺ�,��������ȡ��λʱ�ᱨ��!
				str_formula = str_formula + ";"; //
			}
			//System.out.println("���յĹ�ʽ=[" + str_formula + "]"); //
			String[] str_items = getTBUtil().split(str_formula, "getWFCorp("); //
			HashMap[] maps = new HashMap[str_items.length]; //
			for (int i = 0; i < str_items.length; i++) {
				maps[i] = new HashMap(); //
				String str_item = str_items[i].substring(0, str_items[i].length() - 2); //ȥ������[);]
				String[] str_keyvalue = getTBUtil().split(str_item, ","); //�ٽ���!!
				for (int j = 0; j < str_keyvalue.length; j++) { //ÿһ��,��"aaa=bbbb"
					str_keyvalue[j] = str_keyvalue[j].substring(1, str_keyvalue[j].length() - 1); //ȥ��ǰ���˫����
					int li_pos = str_keyvalue[j].indexOf("="); //
					String str_key = str_keyvalue[j].substring(0, li_pos); //
					String str_value = str_keyvalue[j].substring(li_pos + 1, str_keyvalue[j].length()); //
					maps[i].put(str_key, str_value); //
				}
			}
			return maps; ////
		} catch (Exception ex) { //��ǰ��������Ϊ��ʽ��������,����substringʱ�����������,���˲�������,�����װһ��,����ʵʩ��ʹ����Ա���������ҵ�ԭ��!!
			//ϵͳ��Ӧ�õ�������������,����ϵͳ�쳣��װ�ɿ����ڿ��ٷ���ԭ�����ʾ�쳣! �Ѳ�������ԭ��ĸ��ֿ���ֱ����ʾ!!
			ex.printStackTrace(); //
			throw new WLTAppException("������ʽ[" + _formulaPar + "],�����쳣[" + ex.getMessage() + "]!\r\n�ܿ�������Ϊ���˸��Ⱥ���ɵ�,�����������˿���̨�鿴��ϸ!"); ////
		}
	}

	/**
	 * ȡ��ĳ���û����ʼ���ַ
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
	 * ��������
	 */

	public void CopyFlow(HashVO _hvs, String old_flowid, int type) throws Exception {
		ArrayList al_sql = new ArrayList();
		// ����
		HashVO[] hvo_activity = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_activity where processid=" + old_flowid + "");
		//   �ϵĻ���
		String[] str_old = new String[hvo_activity.length];
		//   �µĻ���
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
		//  ���ŷ���
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
		// �����µĻ���
		HashVO[] new_activity = getCommDMO().getHashVoArrayByDS(null, "select * from pub_wf_activity where processid=" + _hvs.getStringValue("flowid") + "");
		ArrayList<String> al_Sql2 = null;
		if (type == 1) {
			//  ��
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
			// �޸����Ա��еĻ�������			
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
	 * �ĵ���ˮӡ����ֻ����pub_filewatermark �����һ����¼�������ĵ���ʱ�����ʾˮӡ��֧��������ӣ���
	 */
	public void addWatermark(String filename, String textwater, String picwater, String picposition) {
		InsertSQLBuilder sqlBuilder = new InsertSQLBuilder("pub_filewatermark");
		List sqllist = new ArrayList();
		String hexstr_textwater = getTBUtil().convertStrToHexString(textwater); //ת����ʮ������
		String hexstr_picwater = getTBUtil().convertStrToHexString(picwater); //ת����ʮ������
		try {
			if (filename.contains(";")) { //������������ˮӡ
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