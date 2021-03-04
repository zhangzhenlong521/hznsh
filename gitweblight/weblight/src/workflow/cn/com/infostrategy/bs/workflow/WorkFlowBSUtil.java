package cn.com.infostrategy.bs.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTInitContext;
import cn.com.infostrategy.bs.sysapp.DataPolicyDMO;
import cn.com.infostrategy.bs.sysapp.ServerCacheDataFactory;
import cn.com.infostrategy.bs.sysapp.install.InstallDMO;
import cn.com.infostrategy.bs.workflow.msg.MessageBSUtil;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.HashVOStruct;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTAppException;
import cn.com.infostrategy.to.mdata.BillVO;

public class WorkFlowBSUtil {

	private TBUtil tbUtil = null; //
	private CommDMO commDMO = null; //
	private HashMap allCorpsCacheMap = null; //

	public void dealHiddenMsg(HashVO[] hvs, String _prinstanceId) throws Exception {
		dealHiddenMsg(hvs, _prinstanceId, null); //
	}

	/**
	 * �����Ƿ�����/���δ������,����*�ű�ʾ!
	 * @param hvs
	 * @param _prinstanceId
	 * @throws Exception
	 */
	public void dealHiddenMsg(HashVO[] hvs, String _prinstanceId, String _loginUserId) throws Exception {
		if (new TBUtil().getSysOptionBooleanValue("���������������", false)) { //������������������������=Y,��ֱ�ӷ���,���������ܴ�����
			for (int i = 0; i < hvs.length; i++) { //
				hvs[i].setAttributeValue("submitmessage_viewreason", "��Ϊϵͳ���ܿ��ز���[���������������=Y],�����ܿ�"); //
			}
			return; //
		}
		CommDMO commDMO = new CommDMO(); //
		//Ȼ�����������Ϣ����,��Ҫ���д���
		String str_loginUserid = null;
		if (_loginUserId != null) {
			str_loginUserid = _loginUserId;
		} else {
			str_loginUserid = new WLTInitContext().getCurrSession().getLoginUserId(); //
		}
		String str_rootInstId = commDMO.getStringValueByDS(null, "select rootinstanceid from pub_wf_prinstance where id='" + _prinstanceId + "'"); //
		if (str_rootInstId == null) {
			str_rootInstId = _prinstanceId; //
		}

		//��������ڸ��������߹���
		String str_1 = "select t1.prinstanceid,t1.processid,t1.createbyid,t1.curractivity,t2.fromparentactivity from pub_wf_dealpool t1 left join pub_wf_prinstance t2 on t1.prinstanceid=t2.id where (t1.prinstanceid='" + _prinstanceId + "' or t1.rootinstanceid='" + str_rootInstId + "') and (t1.participant_user='" + str_loginUserid + "' or participant_accruserid='" + str_loginUserid + "')"; //

		HashVO[] hvs_myWalkedCode = commDMO.getHashVoArrayByDS(null, str_1); //

		//		HashVO[] hvs_myWalkedCode = commDMO.getHashVoArrayByDS(null, "select t1.prinstanceid,t1.processid,t1.createbyid,t2.code activitycode,t2.belongdeptgroup activitybelongdeptgroup " + // 
		//				"from pub_wf_dealpool t1 left join pub_wf_activity t2 on t1.curractivity=t2.id where (t1.prinstanceid='" + _prinstanceId + "' or t1.rootinstanceid='" + str_rootInstId + "') " + // 
		//				"and (t1.participant_user='" + str_loginUserid + "' or participant_accruserid='" + str_loginUserid + "')"); //���߹������л���!!
		//		
		String[] str_allCreateByIds = new String[hvs_myWalkedCode.length]; //������,��
		String[] str_myparinstids = new String[hvs_myWalkedCode.length]; //���˸�������
		HashSet hst_thisActids = new HashSet(); //
		HashSet hst_parentActids = new HashSet(); //
		for (int i = 0; i < hvs_myWalkedCode.length; i++) {
			str_allCreateByIds[i] = hvs_myWalkedCode[i].getStringValue("createbyid"); // 
			str_myparinstids[i] = hvs_myWalkedCode[i].getStringValue("prinstanceid"); // 
			if (hvs_myWalkedCode[i].getStringValue("curractivity") != null) {
				hst_thisActids.add(hvs_myWalkedCode[i].getStringValue("curractivity")); //
			}
			if (hvs_myWalkedCode[i].getStringValue("fromparentactivity") != null) { //�����������������,������Ǳ����������ڸ��������е�����!!
				hst_parentActids.add(hvs_myWalkedCode[i].getStringValue("fromparentactivity")); //
			}
		}

		//�����Ҿ����Ļ��ڵ�����
		String[] str_thisActids = (String[]) hst_thisActids.toArray(new String[0]); //
		String[] str_parentActids = (String[]) hst_parentActids.toArray(new String[0]); //

		String str_2 = "";
		str_2 = str_2 + "select id,code,wfname,belongdeptgroup,'�����̻���' c1 from pub_wf_activity where id in (" + getTBUtil().getInCondition(str_thisActids) + ")  union all "; //
		str_2 = str_2 + "select id,code,wfname,belongdeptgroup,'�����̻���' c1 from pub_wf_activity where id in (" + getTBUtil().getInCondition(str_parentActids) + ")"; //
		HashVO[] hvs_allActs = commDMO.getHashVoArrayByDS(null, str_2); //
		HashSet hst_thisWFActGroups = new HashSet();
		HashSet hst_parentWFActGroups = new HashSet();
		for (int i = 0; i < hvs_allActs.length; i++) {
			String str_groupName = hvs_allActs[i].getStringValue("belongdeptgroup"); //
			if (str_groupName != null) { //����û�ж�����!��ֱ�ӻ���
				String str_type = hvs_allActs[i].getStringValue("c1"); //
				if (str_type.equals("�����̻���")) { //����Ǳ����̻���
					hst_thisWFActGroups.add(str_groupName); //
				} else if (str_type.equals("�����̻���")) {
					hst_parentWFActGroups.add(str_groupName); //
					System.out.println("�ɹ���������������,���������Ӧ�ĸ������еĻ�����[" + hvs_allActs[i].getStringValue("wfname") + "],����[" + hvs_allActs[i].getStringValue("belongdeptgroup") + "]"); //
				}
			}
		}
		String[] str_myWalkedGroup = (String[]) hst_thisWFActGroups.toArray(new String[0]); //���ڱ������д���������,������Ǹ�����,����Ȼ
		String[] str_myWalkedParentGroup = (String[]) hst_parentWFActGroups.toArray(new String[0]); //���������ĸ�������!ֻ�б�������������,����Ż���ֵ!

		//str_myWalkedGroup[i] = hvs_myWalkedCode[i].getStringValue("activitybelongdeptgroup"); //����ʲô��

		//		HashSet hst_walkedgroup = new HashSet(); //
		//		for (int i = 0; i < str_myWalkedGroup.length; i++) {
		//			if (str_myWalkedGroup[i] != null && !str_myWalkedGroup[i].trim().equals("")) {
		//				hst_walkedgroup.add(str_myWalkedGroup[i]); //
		//			}
		//		}
		//		str_myWalkedGroup = (String[]) hst_walkedgroup.toArray(new String[0]); //Ψһ�Թ���!!

		if (hvs_myWalkedCode.length <= 0) { //����Ҵ�û�����������,��ͳͳ���ܿ�!
			for (int i = 0; i < hvs.length; i++) {
				hvs[i].setAttributeValue("submitmessage_viewreason", "��Ϊ�Ҵ���û�в����������,���Լ���"); //
				if (hvs[i].getStringValue("submitmessage") != null) {
					hvs[i].setAttributeValue("submitmessage", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
				}
				if (hvs[i].getStringValue("submitmessagefile") != null) {
					hvs[i].setAttributeValue("submitmessagefile", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
				}
			}
		} else { //�Ҳ����˸�����!!!
			for (int i = 0; i < hvs.length; i++) { //����ÿһ����¼!
				StringBuilder sb_reason = new StringBuilder(); //Ϊ�˸��Ѻõ���ǰ̨֪��Ϊʲô��������ܱ�����������,��ԭ��Ҳ�������!!
				if (str_loginUserid.equals(hvs[i].getStringValue("participant_user")) || str_loginUserid.equals(hvs[i].getStringValue("participant_accruserid"))) { //�����������Ĵ�����ǵ�¼��Ա,������������δ���,��Ϊ������������ҵ�,������������ұ�����д��!!
					sb_reason.append("��Ϊ�Ҿ�����������Ĵ�����,�����ܿ�"); //
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue;
				}
				if (getTBUtil().isExistInArray(hvs[i].getStringValue("id"), str_allCreateByIds)) { //������������Ǳ�������Ĵ�����,�����Ǳ�����B,A�ύ���ҵ����,�ҿ϶��ܿ���!
					sb_reason.append("��Ϊ������������ύ���ҵ�,�����ܿ�"); //
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue; //
				}

				String str_taskid = hvs[i].getStringValue("id"); //����id
				String str_prinstid = hvs[i].getStringValue("prinstanceid"); //�������������ʵ��id
				String str_processid = hvs[i].getStringValue("processid"); //����ͼid
				String str_lifecycle = hvs[i].getStringValue("lifecycle"); //����ͬ��
				String str_hidActivitys = hvs[i].getStringValue("curractivity_iscanlookidea"); //ָ�������ڿ��Ա���Щ���ڲ鿴!!

				//System.out.println("ʲô����:\r\n" + hvs[i].getSBStr());  //

				if ("E".equals(str_lifecycle)) { //����������̵Ľ�����,���ܿ�,����ν�������������!!
					sb_reason.append("��Ϊ��������������̽�����,������������˶��ܿ�,�����ܿ�"); //
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue; //
				}

				if (str_hidActivitys != null && str_hidActivitys.trim().equals("*")) {
					sb_reason.append("��Ϊ���������ض�����Լ����[*],��ֱ�Ӷ������˿���,�����ܿ�"); ////
					hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
					continue; //
				}

				if (getTBUtil().isExistInArray(str_prinstid, str_myparinstids)) { //����Ҳ����˸�����,��û�в���������ʱ�϶������������,���ɰ汾�Ļ���!!
					if (str_hidActivitys != null && !str_hidActivitys.trim().equals("")) { //�������������,�Ŵ���,û�������ʾ�û����ϵ�������Ա��κ��˿�
						boolean isMineWalkedThisProcess = false; //���Ƿ��߹��������???
						for (int j = 0; j < hvs_myWalkedCode.length; j++) { //
							if (str_processid.equals(hvs_myWalkedCode[j].getStringValue("processid"))) { //������߹��˸�����!
								isMineWalkedThisProcess = true; //
								break;
							}
						}
						if (isMineWalkedThisProcess) { //���������¼���ڵ��������������߹�������!!������Ҷ�û�ڸ�������,�򲻼���,��Ϊ���ܲ�ͬ������������ͬ����Ļ���!!
							boolean isBlackList = false; //�Ƿ��Ǻڰ׵�
							if (str_hidActivitys.startsWith("-(")) { //����Ǻ�����
								isBlackList = true; //
								str_hidActivitys = str_hidActivitys.substring(2, str_hidActivitys.length() - 1); //ȥ����һ��"-"��,������������Ļ����б�!
							}
							String[] str_hids = getTBUtil().split(str_hidActivitys, ";"); //�÷ֺŸ���!
							if (!isRealShow(str_hids, hvs_myWalkedCode, isBlackList)) { //�����������,����*��
								sb_reason.append("��Ϊ���������ض�����Լ����" + str_hidActivitys + "����ָ���û����ϵ����(ֻ/��)�ܱ���Щ���ڲ鿴��,\r\n���˲���У��ʧ��,���Լ��ܣ�"); ////
								if (hvs[i].getStringValue("submitmessage") != null) {
									hvs[i].setAttributeValue("submitmessage", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
								}
								if (hvs[i].getStringValue("submitmessagefile") != null) {
									hvs[i].setAttributeValue("submitmessagefile", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
								}
							} else {
								sb_reason.append("���������ض�����Լ����" + str_hidActivitys + "����ָ���û����ϵ����(ֻ/��)�ܱ���Щ���ڲ鿴��,\r\n���˲���У��ɹ�,�����ܿ���"); ////
							}
						} else { //
							sb_reason.append("���������ض�����Լ����" + str_hidActivitys + "����ָ���û����ϵ����(ֻ/��)�ܱ���Щ���ڲ鿴��,\r\n����û�߹�������,���Ժ�������,ֱ�Ӽ��ܣ�"); ////
							if (hvs[i].getStringValue("submitmessage") != null) {
								hvs[i].setAttributeValue("submitmessage", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
							}
							if (hvs[i].getStringValue("submitmessagefile") != null) {
								hvs[i].setAttributeValue("submitmessagefile", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
							}
						}
					} else { //�����Ҫ���и��鴦��
						boolean isHiddenByDiffGroup = true; //Ҫ��ϵͳ����ȡ!!
						if (isHiddenByDiffGroup) { //
							String str_thistask_bldeptgroup = hvs[i].getStringValue("curractivity_belongdeptgroup", ""); //������ʲô��!!
							if (str_thistask_bldeptgroup == null || str_thistask_bldeptgroup.trim().equals("")) {
								sb_reason.append("��Ϊ�����뱾����ͬһ�������е�,û���ض�����Լ��,��Ҫ����ָ�,������¼������Ϊ��,�����ܿ�"); ////
							} else {
								String str_mychildren_task_bldeprGroup = getMyChildrenTaskBlDeprname(hvs, str_taskid); //
								if (!str_thistask_bldeptgroup.equals(str_mychildren_task_bldeprGroup)) { //�������������Ķ��Ӳ���ͬһ��,���ǳ�ȥ�����!!
									sb_reason.append("��Ϊ�����뱾����ͬһ�������е�,û���ض�����Լ��,��Ҫ����ָ�,�Ҳ��Ǳ����ڵ�,\r\n���ύ��������һ��(����[" + str_thistask_bldeptgroup + "]�ύ����[" + str_mychildren_task_bldeprGroup + "]),���ǲ��ų�ȥ�ĵ����,���ų�ȥ�������Ҷ��ܿ�(�ٽ紦),�����ܿ�"); ////
								} else {
									if (getTBUtil().isExistInArray(str_thistask_bldeptgroup, str_myWalkedGroup)) { //���
										sb_reason.append("��Ϊ�����뱾����ͬһ�������е�,û���ض�����Լ��,��Ҫ����ָ�,�ҳ�ȥ��Ҳ��ͬһ����,\r\n�������������[" + str_thistask_bldeptgroup + "],�����߹�������[" + getSBStr(str_myWalkedGroup) + "],��ƥ��ɹ���,����һ���������,�����ܿ�"); ////
									} else {
										sb_reason.append("��Ϊ�����뱾����ͬһ�������е�,û���ض�����Լ��,��Ҫ����ָ�,�ҳ�ȥ��Ҳ��ͬһ����,\r\n�������������[" + str_thistask_bldeptgroup + "],�����߹�������[" + getSBStr(str_myWalkedGroup) + "],ƥ��ʧ��,�����Ǳ��˼������,���Լ���"); ////
										if (hvs[i].getStringValue("submitmessage") != null) {
											hvs[i].setAttributeValue("submitmessage", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
										}
										if (hvs[i].getStringValue("submitmessagefile") != null) {
											hvs[i].setAttributeValue("submitmessagefile", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
										}
									}
								}
							}
						} else {
							sb_reason.append("��Ϊ�����뱾����ͬһ�������е�,��û���ض�����Լ��,����û��Ҫ��������,�����ܿ�"); ////
						}
					}
				} else { //�����û�в��������,�������������̵���������������,�������������̵���������������!
					//�ʴ���Ŀ���е�������,��ǩʱ����Ҫ����֮������,����ֻҪ�ڸ�����������ͬһ��"����ͨ��",�������˶�������!!!
					//���������е����ܿ���ǩ�ڲ���,ͬ����ǩ�ڲ���Ҳ�ܿ������!���ڸ����̵Ļ���ͨ���п���ʱ�Ͳ��ܿ���....
					//����ʵ����ͬ����һ�����������������֮������!!!
					boolean isChildWFCanLookAll = true;
					if (isChildWFCanLookAll) { //
						String str_parentid = hvs[i].getStringValue("parentinstanceid"); //
						if (str_parentid != null && !str_parentid.equals("")) { //�����Ϊ��,��˵����������!!��������������ͬ,˵���ҿ϶����������еĲ�����
							String str_blcorpName = hvs[i].getStringValue("prinstanceid_fromparentactivitybldeptgroup"); //���������ڸ����������еĻ�������	
							if (getTBUtil().isExistInArray(str_blcorpName, str_myWalkedGroup) || getTBUtil().isExistInArray(str_blcorpName, str_myWalkedParentGroup)) { //
								sb_reason.append("�����������ڲ�ͬ����ʵ��,���Ҹ�������������,�����ڸ����̻�������һ�������̷�֧��,\r\n�ҷ����������������Ӧ�ĸ����̻�����[" + str_blcorpName + "]���ҵı�������[" + getSBStr(str_myWalkedGroup) + "]��������[" + getSBStr(str_myWalkedParentGroup) + "]��ͬһ��Χ,�����ܿ�!"); //
							} else {
								sb_reason.append("�����������ڲ�ͬ����ʵ��,���Ҹ�������������,�����ڸ����̻�������һ�������̷�֧��,\r\n�ҷ����������������Ӧ�ĸ����̻�����[" + str_blcorpName + "]���ҵı�������[" + getSBStr(str_myWalkedGroup) + "]��������[" + getSBStr(str_myWalkedParentGroup) + "]����ͬһ��Χ,���Լ���!"); //
								hvs[i].setAttributeValue("submitmessage", "*****"); //
							}
						} else { //������������Ǹ�������,����һ��,��Ӧ�þ����������е�!
							String str_blcorpName = hvs[i].getStringValue("curractivity_belongdeptgroup"); //����/һ��
							if (getTBUtil().isExistInArray(str_blcorpName, str_myWalkedParentGroup)) { //�������������������ĸ������еĻ�������!
								sb_reason.append("�����������ڲ�ͬ����ʵ��,���Ҹ������Ǹ�����,������������,\r\n�ҷ����������������Ӧ�Ļ�����[" + str_blcorpName + "]��������Ӧ�ĸ����̻���[" + getSBStr(str_myWalkedParentGroup) + "]��ͬ,�����ܿ�"); //
							} else {
								sb_reason.append("�����������ڲ�ͬ����ʵ��,���Ҹ������Ǹ�����,������������,\r\n�ҷ����������������Ӧ�Ļ�����[" + str_blcorpName + "]��������Ӧ�ĸ����̻���[" + getSBStr(str_myWalkedParentGroup) + "]����ͬ,���Լ���!");
								hvs[i].setAttributeValue("submitmessage", "*****");
							}
						}
					} else {
						if ("CC".equals(str_lifecycle) || "EC".equals(str_lifecycle)) { //����ǽ��������̻������̽��������,���Ҷ�����!!
							sb_reason.append("��Ϊ���������ǽ��������̻������̽�����,�����ܿ�"); ////
						} else {
							sb_reason.append("��������뱾�˲���ͬһ������,���Լ���"); ////
							if (hvs[i].getStringValue("submitmessage") != null) {
								hvs[i].setAttributeValue("submitmessage", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
							}
							if (hvs[i].getStringValue("submitmessagefile") != null) {
								hvs[i].setAttributeValue("submitmessagefile", "*****"); //����Ҵ��������������,��ͳͳ���ÿ�
							}
						}
					}
				}
				hvs[i].setAttributeValue("submitmessage_viewreason", sb_reason.toString()); //
			}
		}
	}

	private String getSBStr(String[] _array) {
		StringBuilder sb_str = new StringBuilder(); //
		for (int i = 0; i < _array.length; i++) {
			sb_str.append(_array[i] + ";"); //
		}
		return sb_str.toString(); //
	}

	private String getMyChildrenTaskBlDeprname(HashVO[] hvs, String str_taskid) {
		for (int i = 0; i < hvs.length; i++) {
			String str_createbyid = hvs[i].getStringValue("createbyid"); //������
			if (str_taskid.equals(str_createbyid)) { //�����ҵĶ���!!!�������ж������!!!���������ӿ϶�����һ����,����ֻҪһ���͹���!
				return hvs[i].getStringValue("curractivity_belongdeptgroup"); //
			}
		}
		return null;
	}

	private String[] getMySameGroupTaskCreateByIds(HashVO[] _hvs, String[] _myWalkedGroup) {
		HashSet hst_temp = new HashSet(); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_thisgroup = _hvs[i].getStringValue("curractivity_belongdeptgroup"); //
			for (int j = 0; j < _myWalkedGroup.length; j++) {
				if (_myWalkedGroup[j] != null && _myWalkedGroup[j].equals(str_thisgroup)) { //����ͬһ��!
					if (!_hvs[i].getStringValue("createbyid", "").equals("")) { //
						hst_temp.add(_hvs[i].getStringValue("createbyid")); //��¼������¼����Դ!����!!
					}
					break; //
				}
			}
		}
		return (String[]) hst_temp.toArray(new String[0]);
	}

	//�Ƿ���������..
	private boolean isRealShow(String[] str_hidCodes, HashVO[] _myWalkedHVS, boolean isBlackList) {
		String[] str_myWalkedCode = new String[_myWalkedHVS.length]; //
		for (int i = 0; i < _myWalkedHVS.length; i++) {
			str_myWalkedCode[i] = _myWalkedHVS[i].getStringValue("activitycode"); //ȡ�û��ڱ���
		}

		if (!isBlackList) { //����ǰ�����!��
			return getTBUtil().containTwoArrayCompare(str_hidCodes, str_myWalkedCode); //������߹��Ļ�������һ��Ʒ�����˶����,��˵�����Բ鿴!!
		} else { //����Ǻ�����
			for (int i = 0; i < str_myWalkedCode.length; i++) {
				if (!getTBUtil().isExistInArray(str_myWalkedCode[i], str_hidCodes)) { //��ֻҪ��һ�����ڲ��ڶ���ĺ�������,��Ͷ��ҿ���!
					return true; //
				}
			}
			return false; //����ܲ���,�Ҷ��ڱ��еĺ�������,����ʾ
		}
	}

	/**
	 * ȡ�ô����������Ѱ�����Ľ����!!
	 * ��ģ��������!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getTaskClassCountMap(HashMap _parMap) throws Exception {
		boolean isSuperShow = (Boolean) _parMap.get("isSuperShow"); //�Ƿ��ǳ����鿴??
		Object templetcode = _parMap.get("templetcode");
		Object model = _parMap.get("model");
		String str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId(); //
		StringBuffer sb1 = new StringBuffer("select templetcode,count(*) c1 from pub_task_deal " + (isSuperShow ? "" : "where (dealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "')") + " ");
		StringBuffer sb2 = new StringBuffer("select templetcode,count(*) c1 from pub_task_off  " + (isSuperShow ? "" : "where (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "')") + " ");
		if (templetcode != null && !"".equals((String) templetcode)) {
			sb1.append(" and templetcode='" + _parMap.get("templetcode") + "' ");
			sb2.append(" and templetcode='" + _parMap.get("templetcode") + "' ");
		}
		sb1.append(" group by templetcode ");
		sb2.append(" group by templetcode ");
		HashMap retrunMap = new HashMap(); //
		if (model == null || "dealTask".equals(model)) {
			retrunMap.put("��������", getClassCountBySQL(sb1.toString())); //�����˻���Ȩ�˵����ҵ�
		}

		if (model == null || "offTask".equals(model)) {
			if (getTBUtil().getSysOptionBooleanValue("�Ѱ�����ͬһ�����Ƿ�ֻ��ʾ���һ��", false)) {
				//�Ѱ�������ͬ����ϲ� �����/2013-03-08��
				StringBuffer sb22 = new StringBuffer(" select a.templetcode,count(*) c1 from pub_task_off a ");
				sb22.append(" inner join ( select pkvalue, max(dealtime) dealtime from pub_task_off where 1=1 ");
				sb22.append((isSuperShow ? "" : " and (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "') "));
				sb22.append(" group by pkvalue ) b on a.pkvalue = b.pkvalue and a.dealtime = b.dealtime ");
				sb22.append((isSuperShow ? " where 1=1 " : " where (realdealuser='" + str_loginUserId + "' or accruserid='" + str_loginUserId + "') "));
				if (templetcode != null && !"".equals((String) templetcode)) {
					sb22.append(" and a.templetcode='" + templetcode + "' ");
				}
				sb22.append(" group by a.templetcode ");
				retrunMap.put("�Ѱ�����", getClassCountBySQL(sb22.toString())); //ʵ���Ѱ��˵����ҵ�!
			} else {
				retrunMap.put("�Ѱ�����", getClassCountBySQL(sb2.toString())); //ʵ���Ѱ��˵����ҵ�!
			}
		}
		//retrunMap.put("�Ѱ�����", getClassCountBySQL(sb2.toString())); //ʵ���Ѱ��˵����ҵ�!
		if (model == null || "msg".equals(model)) {
			retrunMap.put("��Ϣ����", new cn.com.infostrategy.bs.workflow.msg.MsgBsUtil().getMsgCountMap(new HashMap(), -1)); //
		}
		if (model == null || "bd".equals(model)) {
			retrunMap.put("�������", getBDCount(_parMap, str_loginUserId));//sunfujun/�������
		}

		if (model == null || "pr".equals(model)) {
			retrunMap.put("����", new MessageBSUtil().getPassReadClassCountMap(_parMap)); //���� �����/2012-12-14��
		}
		return retrunMap; //
	}

	//ȡ��ĳһ�����������!!!
	private HashMap getClassCountBySQL(String _sql) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvs = commDMO.getHashVoArrayByDS(null, _sql); //
		if (hvs != null && hvs.length > 0) {
			ArrayList al_templetCodes = new ArrayList(); //
			for (int i = 0; i < hvs.length; i++) {
				al_templetCodes.add(hvs[i].getStringValue("templetcode")); //����ģ�����!
			}
			//�����ݿ�����!!!
			HashVO[] templetHVs = commDMO.getHashVoArrayByDS(null, "select templetcode,templetname,tablename,savedtablename,pkname from pub_templet_1 where templetcode in (" + getTBUtil().getInCondition(al_templetCodes) + ")"); //�ҵ�ģ����������Ƶ�ӳ��Ĺ�ϣ��!!

			//���û�ҵ�,���XML����!! ��Ϊ�µĻ����ǿ��Դ�XML����!!! �������·���ʱ��Ϊ���ݿ�����û�����ݵ�,���Թ������������ı���!!
			ArrayList al_notFindFromDB = new ArrayList(); //û�д����ݿ��з��ֵ��嵥!!!
			for (int i = 0; i < al_templetCodes.size(); i++) {
				String str_itemcode = (String) al_templetCodes.get(i); //
				boolean isFinded = false; //�Ƿ��ֱ��!!
				for (int j = 0; j < templetHVs.length; j++) {
					if (str_itemcode != null && str_itemcode.equals(templetHVs[j].getStringValue("templetcode"))) { //������ݿ�����!!
						isFinded = true; //
						break; //
					}
				}
				if (!isFinded) { //��������ݿ���û����,������嵥!!
					al_notFindFromDB.add(str_itemcode); //����!
				}
			}

			if (al_notFindFromDB.size() > 0) { //����ҵ�!!//ȥXML���ҳ���Ӧ��ģ��!!!
				HashVO[] hvs_xml = new InstallDMO().getInstallTempletsAsHashVO((String[]) al_notFindFromDB.toArray(new String[0])); //
				if (hvs_xml != null && hvs_xml.length > 0) { //����ҵ�
					HashVO[] spanVOs = new HashVO[templetHVs.length + hvs_xml.length]; //	
					System.arraycopy(templetHVs, 0, spanVOs, 0, templetHVs.length); //����!
					System.arraycopy(hvs_xml, 0, spanVOs, templetHVs.length, hvs_xml.length); //����!
					templetHVs = spanVOs; //����!!
				}
			}

			LinkedHashMap returnMap = new LinkedHashMap(); //���صĹ�ϣ��!!�������,ʡ��ÿ��ˢ��ʱ����һ��!
			for (int i = 0; i < hvs.length; i++) {
				String str_itemCode = hvs[i].getStringValue("templetcode"); //ģ�����!
				String str_templetName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "templetname"); //
				String str_tableName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "tablename"); //
				String str_savedTableName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "savedtablename"); //
				String str_pkName = getItemValueFromTempletVOs(templetHVs, str_itemCode, "pkname"); //
				returnMap.put(str_itemCode, new String[] { str_templetName, str_tableName, str_savedTableName, str_pkName, hvs[i].getStringValue("c1") }); //����key=ģ�����,Value={"ģ������","����"}�Ĺ�ϣ��!!
			}
			return returnMap; //
		}
		return null; //
	}

	/**
	 * �ֹ���ӽ�����Աʱ,��һ�δ�ʱȡ����¼�����ڻ���,�Լ����������������Ա!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getUserCorpAndUsers(HashMap _parMap) throws Exception {
		String str_accrModel = null; // 
		if (_parMap != null) {
			String str_billType = (String) _parMap.get("billtype"); //��������
			String str_busiType = (String) _parMap.get("busitype"); //ҵ������
			str_accrModel = getAccrModelByBillTypeAndBusitype(str_billType, str_busiType); //
		}
		String str_loginuserId = new WLTInitContext().getCurrSession().getLoginUserId(); //��¼��Աid
		CommDMO commDMO = new CommDMO(); //
		HashVO[] hvsCorp = commDMO.getHashVoArrayByDS(null, "select userid,userdept,isdefault from pub_user_post where userid='" + str_loginuserId + "'"); //
		String str_corpId = null; //���ҳ�����!!!
		if (hvsCorp.length > 0) {
			boolean isFinded = false; //
			for (int i = 0; i < hvsCorp.length; i++) {
				if ("Y".equals(hvsCorp[i].getStringValue("isdefault"))) { //�����Ĭ�ϻ���
					str_corpId = hvsCorp[i].getStringValue("userdept"); //�û�����!!!
					isFinded = true; //
					break; //
				}
			}
			if (!isFinded) { //���û����Ĭ�ϻ���!
				str_corpId = hvsCorp[0].getStringValue("userdept"); //ȡ��һ��
			}
		}
		if (str_corpId == null) {
			return null; //
		}
		//�ҳ��û�����������Ա,ͬʱ����������н�ɫ�����!!
		return getCorpAndUserByCorpId(str_corpId, str_accrModel); //
	}

	/**
	 * �ֹ���ӽ�����Աʱ,ѡ����߻�����ˢ���ұߵ�Ա!!!
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getUserCorpAndUsersBycorpId(HashMap _parMap) throws Exception {
		String str_corpId = (String) _parMap.get("corpid"); //
		if (_parMap.containsKey("isaddaccr") && !(Boolean) _parMap.get("isaddaccr")) { //�Ƿ��������Ȩ,�����ĳ����������Ȩ����,�Ƿ���������Ȩ��?
			return getCorpAndUserByCorpId(str_corpId, null, false);
		}
		String str_billType = (String) _parMap.get("billtype"); //��������
		String str_busiType = (String) _parMap.get("busitype"); //ҵ������
		String str_accrModel = getAccrModelByBillTypeAndBusitype(str_billType, str_busiType); //
		return getCorpAndUserByCorpId(str_corpId, str_accrModel); //
	}

	private String getAccrModelByBillTypeAndBusitype(String str_billType, String str_busiType) throws Exception {
		String[][] str_accrModels = getCommDMO().getStringArrayByDS(null, "select accrmodel from pub_workflowassign where billtypecode='" + str_billType + "' and busitypecode='" + str_busiType + "'"); //����ģʽ!!!
		if (str_accrModels.length == 0) { //
			throw new WLTAppException("����Ȩģ�����ʱ����,���ݵ�������[" + str_billType + "],ҵ������[" + str_busiType + "]�����̷������û���ҵ�һ�������¼,���ǲ��Ե�,�������Ա��ϵ!"); //
		} else if (str_accrModels.length > 1) {
			throw new WLTAppException("����Ȩģ�����ʱ����,���ݵ�������[" + str_billType + "],ҵ������[" + str_busiType + "]�����̷�������ҵ��������ϵ�������Ϣ!\r\n�⽫�ᵼ�µõ��������Ȩģ��,�ǲ������,�������Ա��ϵ!!"); //
		}
		String str_accrModel = str_accrModels[0][0]; //��ֵ
		return str_accrModel;
	}

	/**
	 * ���ݻ���id�ҳ���������µ�������Ա!! ͬʱ����ָ������Ȩģ�����Ա������Ȩ����!!
	 * @param _corpId
	 * @return
	 * @throws Exception
	 */

	private HashMap getCorpAndUserByCorpId(String _corpId, String _accrModel) throws Exception {
		return getCorpAndUserByCorpId(_corpId, _accrModel, true);
	}

	/**
	 * ���ݻ������ͺͽ�ɫ������
	 * �߼���ѯ��
	 * @param param
	 * @return
	 */
	public HashMap getUserCorpAndUsersBycorpTypeAndRole(HashMap param) throws Exception {
		DataPolicyDMO ddmo = new DataPolicyDMO();
		HashMap map = ddmo.getDataPolicyTargetCorpsByUserId(new WLTInitContext().getCurrSession().getLoginUserId(), "������ѡ�˻�������Ȩ��", "id");
		String userCondition = getTBUtil().getSysOptionStringValue("��������Ա״̬��������", null);//̫ƽ������Ч��Ա״̬Ϊ0�������Ӳ��������/2017-10-19��
		String[] ids = (String[]) map.get("AllCorpIDs");
		CommDMO commDMO = new CommDMO(); //
		StringBuilder sb_sql = new StringBuilder(); //
		sb_sql.append("select ");
		sb_sql.append("t1.userid,");
		sb_sql.append("t2.code usercode,");
		sb_sql.append("t2.name username,");
		sb_sql.append("t1.userdept,");
		sb_sql.append("t3.roleid userroleid,");
		sb_sql.append("t4.name userrolename ");
		sb_sql.append("from pub_user_post t1 ");
		sb_sql.append("left join pub_user      t2 on t1.userid=t2.id "); //
		sb_sql.append("left join pub_user_role t3 on t2.id =t3.userid ");
		sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
		sb_sql.append("where 1=1 "); //
		if (userCondition != null && !userCondition.trim().equals("")) {
			sb_sql.append(" and (t2.status is null or t2." + userCondition + ") ");
		}
		sb_sql.append("and t1.userdept in (select id from pub_corp_dept where id in(" + getTBUtil().getInCondition(ids) + ") " + (((List) param.get("corptype")).size() > 0 ? " and corptype in (" + getTBUtil().getInCondition((List) param.get("corptype")) + ")" : "") + ")"); //�������ڵ�¼��Ա���ڵĻ���!!
		sb_sql.append("and t4.name in (" + getTBUtil().getInCondition((List) param.get("role")) + ")");
		sb_sql.append("order by t1.seq desc");
		HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString()); ///
		LinkedHashMap userMap = new LinkedHashMap(); //
		for (int i = 0; i < hvsUsers.length; i++) {
			String str_userId = hvsUsers[i].getStringValue("userid"); //
			if (userMap.containsKey(str_userId)) { //�������!
				HashVO hvoItem = (HashVO) userMap.get(str_userId); //ȡ��
				resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true); //���ý�ɫid
				resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false); //���ý�ɫname
				userMap.put(str_userId, hvoItem); //��������!!
			} else {
				HashVO hvoItem = new HashVO(); //
				hvoItem.setAttributeValue("userid", str_userId); //
				hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode")); //��Ա����
				hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username")); //��Ա����
				hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid")); //��ɫid
				hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename")); //��ɫ����
				hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept")); //
				userMap.put(str_userId, hvoItem); //
			}
		}

		HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]); ////���˺ϲ����û��嵥!!!
		appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname");
		//�����˻���������
		HashMap returnMap = new HashMap(); //
		returnMap.put("users", hvsUserSpan); //��һ�����µ�������Ա!!!
		return returnMap; //

	}

	private HashMap getCorpAndUserByCorpId(String _corpId, String _accrModel, boolean isaddaccr) throws Exception {
		CommDMO commDMO = new CommDMO(); //
		StringBuilder sb_sql = new StringBuilder(); //
		String userCondition = getTBUtil().getSysOptionStringValue("��������Ա״̬��������", null);//̫ƽ������Ч��Ա״̬Ϊ0�������Ӳ��������/2017-10-19��
		sb_sql.append("select ");
		sb_sql.append("t1.userid,");
		sb_sql.append("t2.code usercode,");
		sb_sql.append("t2.name username,");
		sb_sql.append("t1.userdept,");
		sb_sql.append("t3.roleid userroleid,");
		sb_sql.append("t4.name userrolename ");
		sb_sql.append("from pub_user_post t1 ");
		sb_sql.append("left join pub_user      t2 on t1.userid=t2.id "); //
		sb_sql.append("left join pub_user_role t3 on t2.id =t3.userid ");
		sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
		sb_sql.append("where 1=1 "); //
		if (userCondition != null && !userCondition.trim().equals("")) {
			sb_sql.append(" and (t2.status is null or t2." + userCondition + ") ");
		}
		sb_sql.append("and t1.userdept='" + _corpId + "' "); //�������ڵ�¼��Ա���ڵĻ���!!
		sb_sql.append("order by t1.seq desc"); //����һ��,��֤ÿ�γ�������Ա˳��һ��!������������Ա���򣬺����߼���������һ�ѣ���������Ҳ��seq�����ţ�����޸�
		HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString()); ///
		LinkedHashMap userMap = new LinkedHashMap(); //
		for (int i = 0; i < hvsUsers.length; i++) {
			String str_userId = hvsUsers[i].getStringValue("userid"); //
			if (userMap.containsKey(str_userId)) { //�������!
				HashVO hvoItem = (HashVO) userMap.get(str_userId); //ȡ��
				resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true); //���ý�ɫid
				resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false); //���ý�ɫname
				userMap.put(str_userId, hvoItem); //��������!!
			} else {
				HashVO hvoItem = new HashVO(); //
				hvoItem.setAttributeValue("userid", str_userId); //
				hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode")); //��Ա����
				hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username")); //��Ա����
				hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid")); //��ɫid
				hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename")); //��ɫ����
				hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept")); //
				userMap.put(str_userId, hvoItem); //
			}
		}

		HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]); ////���˺ϲ����û��嵥!!!
		appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname"); //�����˻���������

		if (isaddaccr) {//������Ȩ�˼���,�����ĳ���û���Ȩ��������һ����,��Ҫ��ʾ��Ȩ�˵�����!!! ���绪����Ȩ���������,�������Ա�ǻ��ܵĻ�,��Ҫ����ʾ�ĳɡ������(������Ȩ)��!!!!
			HashMap allAccreditDefinemap = getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //ȡ����Ȩ����ļ�¼!!
			if (allAccreditDefinemap.size() > 0) { //�������Ȩ�����������
				ArrayList al_temp = new ArrayList(); //
				for (int i = 0; i < hvsUserSpan.length; i++) { //����������Ա!!!
					al_temp.add(hvsUserSpan[i]); //�ȼ���!!!
					String str_oldUserId = hvsUserSpan[i].getStringValue("userid"); //
					HashVO proxyUserVO = getAccredProxyUserVO(allAccreditDefinemap, _accrModel, str_oldUserId); //�ҵ������˵�VO
					if (proxyUserVO != null) { //����ҵ��˴�����,�����Ӵ����˵���Ϣ
						HashVO cloneProxyUserHVO = hvsUserSpan[i].clone(); //
						String str_username1 = hvsUserSpan[i].getStringValue("username") + "(��Ȩ" + proxyUserVO.getStringValue("proxyusername") + ")"; //
						String str_username2 = proxyUserVO.getStringValue("proxyusername") + "(" + hvsUserSpan[i].getStringValue("username") + "��Ȩ)"; //����Ժ������Ҫ��������!
						cloneProxyUserHVO.setAttributeValue("username", str_username1);//��workflowenginedmo���һ�� ��Զ��ʾ ����( ��Ȩ�︻��) ������ʽ

						//���ñ���Ȩ��
						cloneProxyUserHVO.setAttributeValue("accruserid", hvsUserSpan[i].getStringValue("proxyuserid")); //
						cloneProxyUserHVO.setAttributeValue("accrusercode", hvsUserSpan[i].getStringValue("proxyusercode")); //
						cloneProxyUserHVO.setAttributeValue("accrusername", hvsUserSpan[i].getStringValue("proxyusername")); //

						al_temp.add(cloneProxyUserHVO); //�ȼ���!!!
					}
				}
				hvsUserSpan = (HashVO[]) al_temp.toArray(new HashVO[0]); //���¸�ֵ!!
			}
		}
		HashMap returnMap = new HashMap(); //
		returnMap.put("userCorp", _corpId); //�û�����
		returnMap.put("sameCorpUsers", hvsUserSpan); //��һ�����µ�������Ա!!!
		return returnMap; //
	}

	//���������Ϣ
	private void resetHashVOItem(HashVO _hvoItem, HashVO _rowHVO, String _itemName, boolean _isfirst) {
		if (_hvoItem.getStringValue(_itemName) != null && !_hvoItem.getStringValue(_itemName).equals("")) {
			String str_oldRoleid = _hvoItem.getStringValue(_itemName); //ȡ�þɵ�
			if (str_oldRoleid == null || str_oldRoleid.equals("")) { //����ɵĽ�ɫidΪ��,��ֱ�����µ�
				_hvoItem.setAttributeValue(_itemName, _rowHVO.getStringValue(_itemName)); //ֱ�����µ�
			} else {
				if (str_oldRoleid.indexOf(";") >= 0) { //������зֺ�!!
					_hvoItem.setAttributeValue(_itemName, str_oldRoleid + _rowHVO.getStringValue(_itemName) + ";"); //
				} else { //���û�зֺ�
					String str_newRoleId = (_isfirst ? ";" : "") + str_oldRoleid + ";" + _rowHVO.getStringValue(_itemName) + ";"; //�µ�id
					_hvoItem.setAttributeValue(_itemName, str_newRoleId); //
				}
			}
		}
	}

	/**
	 * ȡ����Ȩ����Ϣ,��Ϊ����������Ĳ��������ֹ�����µĽ�����Աʱ,����Ҫ������Ȩ! Ϊ������,���ڷ�������!!!
	 * ��ǰ�ֹ����ʱ��������Ȩ,ʵ�������������!!!
	 * @return
	 * @throws Exception
	 */
	public HashMap getAccreditAndProxyUsersMap(HashMap _allCorpCacheDataMap) throws Exception {
		HashMap returnMap = new HashMap(); //
		String str_currtime = getTBUtil().getCurrTime(); //ȡ�õ�ǰʱ��(���Ƿ������˵�ʱ��)!!!
		StringBuffer sb_sql = new StringBuffer(); //
		sb_sql.append("select ");
		sb_sql.append("t1.id,"); //����
		sb_sql.append("t1.accruserid,"); //��Ȩ��id,
		sb_sql.append("t2.code accrusercode,"); //��Ȩ�˱���
		sb_sql.append("t2.name accrusername,"); //��Ȩ������
		sb_sql.append("t1.proxyuserid,"); //����������
		sb_sql.append("t1.accrmodel,"); //��Ȩģ��!
		sb_sql.append("t3.code proxyusercode,"); //�����˱���
		sb_sql.append("t3.name proxyusername "); //����������
		sb_sql.append("from pub_workflowaccrproxy t1 ");
		sb_sql.append("left join pub_user       t2 on t1.accruserid=t2.id "); //��Ȩ�˹���
		sb_sql.append("left join pub_user       t3 on t1.proxyuserid=t3.id "); //�����˹���proxyuserdeptid,proxyuserdeptname
		sb_sql.append("where 1=1 "); //
		sb_sql.append("and t1.accrbegintime<'" + str_currtime + "' and (t1.accrendtime is null or t1.accrendtime>'" + str_currtime + "') "); //��Ȩ��������Ч����!!!
		HashVO[] hvs_allAccrditAndProxy = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString()); //�ҳ�������Ȩ������!!
		if (hvs_allAccrditAndProxy.length <= 0) {
			return returnMap; //
		}

		HashSet hst = new HashSet(); //
		for (int i = 0; i < hvs_allAccrditAndProxy.length; i++) {
			if (hvs_allAccrditAndProxy[i].getStringValue("proxyuserid") != null) {
				hst.add(hvs_allAccrditAndProxy[i].getStringValue("proxyuserid")); //
			}
		}
		String[] str_allProxyUserIds = (String[]) hst.toArray(new String[0]); //������!!!
		String str_proxyUserInCondition = getTBUtil().getInCondition(str_allProxyUserIds); //
		HashVO[] hvs_allProxyUserRoles = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.roleid,t2.code rolecode,t2.name rolename   from pub_user_role t1 left join pub_role t2 on t1.roleid=t2.id where t1.userid in (" + str_proxyUserInCondition + ")"); //
		HashVO[] hvs_allProxyUserCorps = getCommDMO().getHashVoArrayByDS(null, "select t1.userid,t1.userdept,t2.code userdeptcode,t2.name userdeptname from pub_user_post t1 left join pub_corp_dept t2 on t1.userdept=t2.id where t1.userid in (" + str_proxyUserInCondition + ") and t1.isdefault='Y'"); //

		HashMap map_userRoles = spellHashVOsByField(hvs_allProxyUserRoles, "userid", new String[] { "roleid", "rolecode", "rolename" }, new boolean[] { true, true, false }, true); //�ϲ���ɫ
		HashMap map_userCorps = spellHashVOsByField(hvs_allProxyUserCorps, "userid", new String[] { "userdept", "userdeptcode", "userdeptname" }, new boolean[] { true, true, false }, false); //�ϲ�����!!!

		//���ϴ����˵Ľ�ɫ�����!!
		for (int i = 0; i < hvs_allAccrditAndProxy.length; i++) {
			String str_proxyUserid = hvs_allAccrditAndProxy[i].getStringValue("proxyuserid"); //
			String[] str_proxyUserAllRoles = (String[]) map_userRoles.get(str_proxyUserid); //
			if (str_proxyUserAllRoles != null) { //��Ϊ�����˿���û���ɫ,����Ҫ���ǿ��ж�!!!
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserroleid", str_proxyUserAllRoles[0]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserrolecode", str_proxyUserAllRoles[1]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserrolename", str_proxyUserAllRoles[2]); //
			}

			String[] str_proxyUserAllCorps = (String[]) map_userCorps.get(str_proxyUserid); //
			if (str_proxyUserAllCorps != null) { //��Ϊ�����˿���û�����,����Ҫ���ǿ��ж�!!!
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserdeptid", str_proxyUserAllCorps[0]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserdeptcode", str_proxyUserAllCorps[1]); //
				hvs_allAccrditAndProxy[i].setAttributeValue("proxyuserdeptname", str_proxyUserAllCorps[2]); //
			}
		}

		//���ϻ���ȫ��!
		appendWholeCorpNameByHashVOs(_allCorpCacheDataMap, hvs_allAccrditAndProxy, "proxyuserdeptid", "proxyuserdeptname"); //���ϴ����˵Ļ�������!! ���绪����Ȩ���������,����ʾ�����Ļ�������!!

		for (int i = 0; i < hvs_allAccrditAndProxy.length; i++) { //����!!
			String str_accruserid = hvs_allAccrditAndProxy[i].getStringValue("accruserid"); //��Ȩ��
			if (!returnMap.containsKey(str_accruserid)) { //��ǰ��ֱ������,���������˷�ģ����Ȩ,����һ������!!
				ArrayList al_temp = new ArrayList(); //
				al_temp.add(hvs_allAccrditAndProxy[i]); //
				returnMap.put(str_accruserid, al_temp); //
			} else {
				ArrayList al_temp = (ArrayList) returnMap.get(str_accruserid); //
				al_temp.add(hvs_allAccrditAndProxy[i]); //
				returnMap.put(str_accruserid, al_temp); //
			}
		}
		return returnMap; //
	}

	private HashMap spellHashVOsByField(HashVO[] _hvs, String _mapKeyField, String[] str_spancols, boolean[] _isFirstAddFH, boolean _isAppend) {
		HashMap returnMap = new HashMap(); //
		for (int i = 0; i < _hvs.length; i++) {
			String str_mapKeyValue = _hvs[i].getStringValue(_mapKeyField); //
			if (!returnMap.containsKey(str_mapKeyValue)) { //���û��!!
				String[] str_rowValues = new String[str_spancols.length]; //
				for (int j = 0; j < str_rowValues.length; j++) {
					if (_isAppend) {
						str_rowValues[j] = (_isFirstAddFH[j] ? ";" : "") + _hvs[i].getStringValue(str_spancols[j], "") + ";"; //
					} else {
						str_rowValues[j] = _hvs[i].getStringValue(str_spancols[j], ""); //
					}
				}
				returnMap.put(str_mapKeyValue, str_rowValues); //
			} else {
				String[] str_rowValues = (String[]) returnMap.get(str_mapKeyValue); //
				for (int j = 0; j < str_rowValues.length; j++) {
					if (_isAppend) {
						str_rowValues[j] = str_rowValues[j] + _hvs[i].getStringValue(str_spancols[j], "") + ";"; //
					} else {
						str_rowValues[j] = _hvs[i].getStringValue(str_spancols[j], ""); //
					}
				}
				returnMap.put(str_mapKeyValue, str_rowValues); //��������!!
			}
		}
		return returnMap; //
	}

	//��һ��HashVO�����е�ĳ������id���ֶν��м���,�ҳ���������Ƶ�ȫ·����!!
	public void appendWholeCorpNameByHashVOs(HashMap _allCorpCacheDataMap, HashVO[] _hvs, String _fromDeptidField, String _toDeptNameField) throws Exception {
		HashMap mapAllCorps = _allCorpCacheDataMap; //ȡ�����л����Ļ���!!
		if (mapAllCorps == null) { //���û��,��ӱ���Ļ�����ȡ,�������Ϊ�÷����п��ܱ�WorkFlowEngineDMO����,Ҳ���ܱ��������,���ֹ���ӽ�����Աʱ����!! ������Ҫ�����ж�!!
			mapAllCorps = getAllCorpsCacheMap(); //
		}
		HashMap mapIdHVO = (HashMap) mapAllCorps.get("AllCorpHashMap"); //����Map
		for (int i = 0; i < _hvs.length; i++) {
			String str_corpId = _hvs[i].getStringValue(_fromDeptidField); //
			if (str_corpId != null && !str_corpId.trim().equals("")) {
				Object obj = mapIdHVO.get(str_corpId); //�ҵ��������
				if (obj == null) {//�Ҳ����û��������ܱ�ɾ���ˡ����/2015-07-29��
					_hvs[i].setAttributeValue(_toDeptNameField, ""); //���ÿմ�����ֹȡֵʱδ�ж��Ƿ�Ϊ�ն�����
				} else {
					HashVO hvoCorpItem = (HashVO) obj;
					String str_nameLink = hvoCorpItem.getStringValue("$parentpathnamelink1"); //��Ϊ�������,���кܿ�!
					_hvs[i].setAttributeValue(_toDeptNameField, str_nameLink); //���û�������
				}
			}
		}
	}

	//ȡ�ñ���Ȩ��(������)����Ϣ,�����������������Ȩ����Ϣ������ǰ�������̰󶨵���Ȩģ��!
	public HashVO getAccredProxyUserVO(HashMap _allAccrditAndProxyMap, String _accrdModel, String _olduserid) {
		ArrayList al_proxyUsers = (ArrayList) _allAccrditAndProxyMap.get(_olduserid); //
		if (al_proxyUsers == null) {
			return null; //
		}
		HashVO[] hvs_AccrUsers = (HashVO[]) al_proxyUsers.toArray(new HashVO[0]); ////
		HashVO hvo_accr = null; //
		if (_accrdModel == null || _accrdModel.trim().equals("")) { //������̷��䶨���ģʽΪ��,��ȡĬ�ϵ���Ȩģʽ!!!
			for (int k = 0; k < hvs_AccrUsers.length; k++) {
				if (hvs_AccrUsers[k].getStringValue("accrmodel", "").equals("") || hvs_AccrUsers[k].getStringValue("accrmodel", "").indexOf("Ĭ����Ȩ") >= 0) {
					hvo_accr = hvs_AccrUsers[k]; //
					break; //
				}
			}
		} else { //���ָ����ģʽ,�������ָ����ģʽ,���û�ҵ�Ʒ���,��ȴ��һ����Ĭ�ϵ�,����Ȼʹ��Ĭ�ϵ�!! ��֮,�����и���Ĭ�ϵ�!!
			HashVO hvo_accrDefault = null; //
			for (int k = 0; k < hvs_AccrUsers.length; k++) {
				if (hvs_AccrUsers[k].getStringValue("accrmodel", "").equals("") || hvs_AccrUsers[k].getStringValue("accrmodel", "").indexOf("Ĭ����Ȩ") >= 0) { //��������˶�ѡ!
					hvo_accrDefault = hvs_AccrUsers[k]; //���Ϊ��!!��˵����Ĭ�ϵ�
				}
				if (hvs_AccrUsers[k].getStringValue("accrmodel", "").equals(_accrdModel) || hvs_AccrUsers[k].getStringValue("accrmodel", "").indexOf(";" + _accrdModel + ";") >= 0) { //���Ʒ������ sunfujun/20121102/��Ȩģ����Ըĳɶ�ѡ
					hvo_accr = hvs_AccrUsers[k]; //
					break; //
				}
			}
			if (hvo_accr == null) { //���ûƷ����,��ֱ��ʹ��Ĭ�ϵ�,������Ĭ��Ҳû��!!
				hvo_accr = hvo_accrDefault; //
			}
		}
		return hvo_accr; //
	}

	private String getItemValueFromTempletVOs(HashVO[] _hvs, String _templetCode, String _returnItemName) {
		for (int i = 0; i < _hvs.length; i++) {
			if (_hvs[i].getStringValue("templetcode").equals(_templetCode)) {
				return _hvs[i].getStringValue(_returnItemName); //
			}
		}
		return null;
	}

	//��Ϊ�ڼ��㹫ʽʱ��ʱ������������!Ϊ���������,����ʱ�ͼ����,Ȼ�󴫽�ȥ!!!
	private HashMap getAllCorpsCacheMap() throws Exception {
		if (this.allCorpsCacheMap != null) {
			return this.allCorpsCacheMap; //
		}
		allCorpsCacheMap = createAllCorpsCacheMap(); //����!!!
		return allCorpsCacheMap; //
	}

	//�������л����Ļ����ϣ��
	public HashMap createAllCorpsCacheMap() {
		HashVO[] hvs_allCorps = ServerCacheDataFactory.getInstance().getCorpCacheDataByAutoCreate(); //getCommDMO().getHashVoArrayAsTreeStructByDS(null, "select id,name,parentid,corptype,extcorptype,seq from pub_corp_dept", "id", "parentid", "seq", null); //�ҳ����л���,���ҷ������ͽṹ!!!
		HashMap allCorpsMap = new HashMap(); //�������ù�ϣ�������,��Ϊ����Ҫ�������!!
		for (int i = 0; i < hvs_allCorps.length; i++) {
			allCorpsMap.put(hvs_allCorps[i].getStringValue("id"), hvs_allCorps[i]); //��ע��һ��!Ϊ���������id�һ���ʱҪ�ǳ���!!
		}
		HashMap returnMap = new HashMap(); //
		returnMap.put("AllCorpHashVOs", hvs_allCorps); //���뻺��!
		returnMap.put("AllCorpHashMap", allCorpsMap); //���뻺��!
		return returnMap; //
	}

	/**
	 * ��������ͼ
	 */
	public String copyProcessById(String _processid, String _processcode, String _processname) throws Exception {
		CommDMO commdmo = new CommDMO();
		HashVOStruct hvos_process = commdmo.getHashVoStructByDS(null, "select * from pub_wf_process where id =" + _processid);
		if (hvos_process == null || hvos_process.getHashVOs() == null || hvos_process.getHashVOs().length == 0) {
			return null;
		}
		HashVOStruct hvos_group = commdmo.getHashVoStructByDS(null, "select * from pub_wf_group where processid =" + _processid);// ����������
		HashVOStruct hvos_activity = commdmo.getHashVoStructByDS(null, "select * from pub_wf_activity where processid =" + _processid);// �������л���
		HashVOStruct hvos_transition = commdmo.getHashVoStructByDS(null, "select * from pub_wf_transition where processid =" + _processid);// ����������

		// ����������Ϣ
		String processid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_process");

		HashVO[] hvo = hvos_process.getHashVOs();
		hvo[0].setAttributeValue("id", processid);
		if (_processcode == null) {//�������Ϊ��
			hvo[0].setAttributeValue("code", hvo[0].getAttributeValue("code") + "_copy");
			hvo[0].setAttributeValue("name", hvo[0].getAttributeValue("name") + "_copy");
		} else {
			hvo[0].setAttributeValue("code", _processcode);
			hvo[0].setAttributeValue("name", _processname);
		}
		String str_curruserid = new WLTInitContext().getCurrSession().getLoginUserId(); //
		String str_deptid = new CommDMO().getStringValueByDS(null, "select userdept from pub_user_post where userid='" + str_curruserid + "' and isdefault='Y'"); //

		hvo[0].setAttributeValue("userdef01", str_deptid);//��������id
		hvo[0].setAttributeValue("userdef02", "1");//����״̬����ʼ״̬�ǡ�1-��Ч������1-��Ч��2-���ã�3-��ֹ���������ҵ�����͡��������͹����˾ͱ�Ϊ�����á�״̬�������ֶ���ֹ������ֹ���������¼������Կ���
		hvo[0].setAttributeValue("userdef03", _processid);//���ĸ����̸��Ƶ�����id

		hvos_process.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_process", hvos_process);

		hvo = hvos_group.getHashVOs();
		for (int j = 0; j < hvo.length; j++) {
			String groupid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_group");
			hvo[j].setAttributeValue("id", groupid);
			hvo[j].setAttributeValue("processid", processid);
		}
		hvos_group.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_group", hvos_group);

		HashMap hashmap = new HashMap();
		hvo = hvos_activity.getHashVOs();
		for (int j = 0; j < hvo.length; j++) {
			String acitivityid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_activity");
			hvo[j].setAttributeValue("userdef03", hvo[j].getStringValue("id"));//sunfujun/20120615/gaofeng��Ҫ
			hashmap.put(hvo[j].getStringValue("id"), acitivityid);
			hvo[j].setAttributeValue("id", acitivityid);
			hvo[j].setAttributeValue("processid", processid);

		}
		hvos_activity.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_activity", hvos_activity);

		hvo = hvos_transition.getHashVOs();
		for (int j = 0; j < hvo.length; j++) {
			String transitionid = new CommDMO().getSequenceNextValByDS(null, "s_pub_wf_transition");
			hvo[j].setAttributeValue("id", transitionid);
			hvo[j].setAttributeValue("processid", processid);
			hvo[j].setAttributeValue("fromactivity", hashmap.get(hvo[j].getStringValue("fromactivity")));
			hvo[j].setAttributeValue("toactivity", hashmap.get(hvo[j].getStringValue("toactivity")));
		}
		hvos_transition.setHashVOs(hvo);
		commdmo.executeInsertData(null, "pub_wf_transition", hvos_transition);
		return processid;
	}

	private CommDMO getCommDMO() {
		if (commDMO != null) {
			return commDMO;
		}

		commDMO = new CommDMO(); //
		return commDMO;
	}

	private TBUtil getTBUtil() {
		if (tbUtil == null) {
			tbUtil = new TBUtil();
		}
		return tbUtil;
	}

	private HashMap getBDCount(HashMap _parMap, String str_loginUserId) throws Exception {
		DataPolicyDMO dpdmo = new DataPolicyDMO();
		boolean isSuperShow = (Boolean) _parMap.get("isSuperShow");
		HashMap users = dpdmo.getDataPolicyTargetUsersByUserId(str_loginUserId, "�����������Ȩ��", "id");
		if (users != null && users.containsKey("AllUserIDs")) {
			StringBuffer sb1 = new StringBuffer("select templetcode,count(*) c1 from pub_task_deal " + (isSuperShow ? "" : "where (dealuser in (" + getTBUtil().getInCondition((String[]) users.get("AllUserIDs")) + ") ) ") + " ");//or accruserid in (" + getTBUtil().getInCondition((String[])users.get("AllUserIDs")) + ")
			if (_parMap.get("templetcode") != null && !"".equals((String) _parMap.get("templetcode"))) {
				sb1.append(" and templetcode='" + _parMap.get("templetcode") + "' ");
			}
			sb1.append(" group by templetcode ");
			return getClassCountBySQL(sb1.toString());
		}
		return null;
	}

	public HashMap getUserIdsByDataPolicy(HashMap param) throws Exception {
		DataPolicyDMO dpdmo = new DataPolicyDMO();
		String str_loginUserId = new WLTInitContext().getCurrSession().getLoginUserId();
		return dpdmo.getDataPolicyTargetUsersByUserId(str_loginUserId, "�����������Ȩ��", "id");
	}

	/**
	 * �����������������ʱ,��Ҫһ����Ⱥ��ѡ��Ľ���!!! ����sfj����,����UI�˵�ChooseUserByGroupPanel�������
	 * @param _parMap
	 * @return
	 * @throws Exception
	 */
	public HashMap getUserCorpAndUsersByGroup(HashMap _parMap) throws Exception {
		BillVO groupVO = (BillVO) _parMap.get("groupVO");
		if (_parMap.containsKey("isaddaccr") && !(Boolean) _parMap.get("isaddaccr")) {
			return getCorpAndUserByGroup(groupVO, null, false);
		}
		String str_billType = (String) _parMap.get("billtype");
		String str_busiType = (String) _parMap.get("busitype");
		String str_accrModel = getAccrModelByBillTypeAndBusitype(str_billType, str_busiType);
		return getCorpAndUserByGroup(groupVO, str_accrModel);
	}

	private HashMap getCorpAndUserByGroup(BillVO _groupVO, String _accrModel) throws Exception {
		return this.getCorpAndUserByGroup(_groupVO, _accrModel, true);
	}

	private HashMap getCorpAndUserByGroup(BillVO _groupVO, String _accrModel, boolean isaddaccr) throws Exception {
		CommDMO commDMO = new CommDMO();
		StringBuilder sb_sql = new StringBuilder();
		sb_sql.append("select ");
		sb_sql.append("t1.id userid,");
		sb_sql.append("t1.code usercode,");
		sb_sql.append("t1.name username,");
		sb_sql.append("t2.userdept,");
		sb_sql.append("t3.roleid userroleid,");
		sb_sql.append("t4.name userrolename, ");
		sb_sql.append("t2.isdefault isdefault ");
		sb_sql.append("from (select * from pub_user where id in(" + getTBUtil().getInCondition(_groupVO.getStringValue("USERIDS")) + ")) t1 ");
		sb_sql.append(" left join pub_user_post t2 on t1.id= t2.userid ");
		sb_sql.append("left join pub_user_role t3 on t1.id =t3.userid ");
		sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
		HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString()); ///
		LinkedHashMap userMap = new LinkedHashMap(); //
		for (int i = 0; i < hvsUsers.length; i++) {
			String str_userId = hvsUsers[i].getStringValue("userid"); //
			if (userMap.containsKey(str_userId)) { //�������!
				HashVO hvoItem = (HashVO) userMap.get(str_userId); //ȡ��
				resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true); //���ý�ɫid
				resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false); //���ý�ɫname
				if (!"Y".equals(hvoItem.getStringValue("isdefault", ""))) {
					if ("Y".equals(hvsUsers[i].getStringValue("isdefault", ""))) {
						hvoItem.setAttributeValue("userdept", hvsUsers[i].getStringValue("userdept", ""));
						hvoItem.setAttributeValue("isdefault", "Y");
					}
				}
				userMap.put(str_userId, hvoItem); //��������!!
			} else {
				HashVO hvoItem = new HashVO(); //
				hvoItem.setAttributeValue("userid", str_userId); //
				hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode")); //��Ա����
				hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username")); //��Ա����
				hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid")); //��ɫid
				hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename")); //��ɫ����
				hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept")); //
				hvoItem.setAttributeValue("isdefault", hvsUsers[i].getAttributeValue("isdefault"));
				userMap.put(str_userId, hvoItem); //
			}
		}

		HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]); ////���˺ϲ����û��嵥!!!
		appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname"); //�����˻���������
		StringBuffer sb_helpmsg = new StringBuffer(); //���ص��ͻ��˵İ�����Ϣ!
		if (isaddaccr) {
			//������Ȩ�˼���,�����ĳ���û���Ȩ��������һ����,��Ҫ��ʾ��Ȩ�˵�����!!! ���绪����Ȩ���������,�������Ա�ǻ��ܵĻ�,��Ҫ����ʾ�ĳɡ������(������Ȩ)��!!!!
			HashMap allAccreditDefinemap = getAccreditAndProxyUsersMap(getAllCorpsCacheMap()); //ȡ����Ȩ����ļ�¼!!
			if (allAccreditDefinemap.size() > 0) { //�������Ȩ�����������
				ArrayList al_temp = new ArrayList(); //
				for (int i = 0; i < hvsUserSpan.length; i++) { //����������Ա!!!
					al_temp.add(hvsUserSpan[i]); //�ȼ���!!!
					String str_oldUserId = hvsUserSpan[i].getStringValue("userid"); //
					HashVO proxyUserVO = getAccredProxyUserVO(allAccreditDefinemap, _accrModel, str_oldUserId); //�ҵ������˵�VO
					if (proxyUserVO != null) { //����ҵ��˴�����,�����Ӵ����˵���Ϣ
						HashVO cloneProxyUserHVO = hvsUserSpan[i].clone(); //
						String str_username1 = hvsUserSpan[i].getStringValue("username") + "(��Ȩ" + proxyUserVO.getStringValue("proxyusername") + ")"; //
						String str_username2 = proxyUserVO.getStringValue("proxyusername") + "(" + hvsUserSpan[i].getStringValue("username") + "��Ȩ)"; //����Ժ������Ҫ��������!				
						cloneProxyUserHVO.setAttributeValue("username", str_username1);//��workflowenginedmo���һ����������ʼ����ʾ��Ȩ��

						//���ñ���Ȩ��...
						cloneProxyUserHVO.setAttributeValue("accruserid", hvsUserSpan[i].getStringValue("proxyuserid")); //
						cloneProxyUserHVO.setAttributeValue("accrusercode", hvsUserSpan[i].getStringValue("proxyusercode")); //
						cloneProxyUserHVO.setAttributeValue("accrusername", hvsUserSpan[i].getStringValue("proxyusername")); //

						al_temp.add(cloneProxyUserHVO); //�ȼ���!!!
						sb_helpmsg.append("[" + hvsUserSpan[i].getStringValue("username") + "]��Ȩ����[" + proxyUserVO.getStringValue("proxyusername") + "]\r\n");
					}
				}
				hvsUserSpan = (HashVO[]) al_temp.toArray(new HashVO[0]); //���¸�ֵ!!
			}
		}
		HashMap returnMap = new HashMap();
		returnMap.put("sqinf", sb_helpmsg.toString());//�Ƿ������Ȩ�����
		returnMap.put("users", hvsUserSpan);
		return returnMap; //

	}

	public HashMap getBDObj(HashMap param) throws Exception {
		DataPolicyDMO ddmo = new DataPolicyDMO();
		HashMap map = ddmo.getDataPolicyTargetUsersByUserId(new WLTInitContext().getCurrSession().getLoginUserId(), "�����������Ȩ��", "id");
		String[] ids = (String[]) map.get("AllUserIDs");
		HashMap rtnmap = new HashMap();
		if (ids != null && ids.length > 0) {
			CommDMO commDMO = new CommDMO();
			StringBuilder sb_sql = new StringBuilder();
			sb_sql.append("select ");
			sb_sql.append("t1.id userid,");
			sb_sql.append("t1.code usercode,");
			sb_sql.append("t1.name username,");
			sb_sql.append("t2.userdept,");
			sb_sql.append("t3.roleid userroleid,");
			sb_sql.append("t4.name userrolename, ");
			sb_sql.append("t2.isdefault isdefault ");
			sb_sql.append("from (select * from pub_user where id in(" + getTBUtil().getInCondition(ids) + ")) t1 ");
			sb_sql.append(" left join pub_user_post t2 on t1.id= t2.userid ");
			sb_sql.append("left join pub_user_role t3 on t1.id =t3.userid ");
			sb_sql.append("left join pub_role      t4 on t3.roleid=t4.id ");
			HashVO[] hvsUsers = commDMO.getHashVoArrayByDS(null, sb_sql.toString());
			LinkedHashMap userMap = new LinkedHashMap();
			for (int i = 0; i < hvsUsers.length; i++) {
				String str_userId = hvsUsers[i].getStringValue("userid");
				if (userMap.containsKey(str_userId)) {
					HashVO hvoItem = (HashVO) userMap.get(str_userId);
					resetHashVOItem(hvoItem, hvsUsers[i], "userroleid", true);
					resetHashVOItem(hvoItem, hvsUsers[i], "userrolename", false);
					if (!"Y".equals(hvoItem.getStringValue("isdefault", ""))) {
						if ("Y".equals(hvsUsers[i].getStringValue("isdefault", ""))) {
							hvoItem.setAttributeValue("userdept", hvsUsers[i].getStringValue("userdept", ""));
							hvoItem.setAttributeValue("isdefault", "Y");
						}
					}
					userMap.put(str_userId, hvoItem);
				} else {
					HashVO hvoItem = new HashVO();
					hvoItem.setAttributeValue("userid", str_userId);
					hvoItem.setAttributeValue("usercode", hvsUsers[i].getAttributeValue("usercode"));
					hvoItem.setAttributeValue("username", hvsUsers[i].getAttributeValue("username"));
					hvoItem.setAttributeValue("userroleid", hvsUsers[i].getAttributeValue("userroleid"));
					hvoItem.setAttributeValue("userrolename", hvsUsers[i].getAttributeValue("userrolename"));
					hvoItem.setAttributeValue("userdept", hvsUsers[i].getAttributeValue("userdept"));
					hvoItem.setAttributeValue("isdefault", hvsUsers[i].getAttributeValue("isdefault"));
					userMap.put(str_userId, hvoItem);
				}
			}
			HashVO[] hvsUserSpan = (HashVO[]) userMap.values().toArray(new HashVO[0]);
			appendWholeCorpNameByHashVOs(getAllCorpsCacheMap(), hvsUserSpan, "userdept", "userdeptname");
			rtnmap.put("vos", hvsUserSpan);
		}
		return rtnmap;
	}
}
