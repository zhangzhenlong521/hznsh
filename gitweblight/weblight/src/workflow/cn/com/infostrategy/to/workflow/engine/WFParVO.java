package cn.com.infostrategy.to.workflow.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;

public class WFParVO implements Serializable {

	private static final long serialVersionUID = 4973482326221989604L;

	private String wfinstanceid = null; //����ʵ������..
	private String parentinstanceid = null; //������ʵ��id
	private String rootinstanceid = null; //������ʵ��id

	private String billtempletcode = null; //����ģ��
	private String billtablename = null; //ҵ�����
	private String billQueryTablename = null; //ҵ�񱣴�ı���
	private String billpkname = null; //ҵ����������
	private String billpkvalue = null; //ҵ���������ֵ

	private String wfinstance_createuserid = null; //���̴����ߵ�id
	private String wfinstance_createusercode = null; //���̴����ߵ�code
	private String wfinstance_createusername = null; //���̴����ߵ�name

	private String wfinstance_createactivityid = null; //���̴������ڵ�id
	private String wfinstance_createactivitycode = null; //���̴������ڵ�code
	private String wfinstance_createactivityname = null; //���̴������ڵ�name
	private String wfinstance_createactivitytype = null; //���̴������ڵ�name

	private String currParticipantUserName = null; //��ǰ������,��������ǵ�¼��Ա,����Ϊ������Ȩ��,���Բ�һ���ǵ�¼��,����Ҫȡ����������ʾ!!

	private String dealpooltask_createuserid = null; //����������Ĵ�����,��ʵ����˭�ύ���ҵ�
	private String dealpooltask_createusercode = null; //����������Ĵ�����,��ʵ����˭�ύ���ҵ�
	private String dealpooltask_createusername = null; //����������Ĵ�����,��ʵ����˭�ύ���ҵ�

	private String dealpooltask_createuser_accruserid = null; //����������Ĵ�����,��ʵ����˭�ύ���ҵ�
	private String dealpooltask_createuser_accrusercode = null; //����������Ĵ�����,��ʵ����˭�ύ���ҵ�
	private String dealpooltask_createuser_accrusername = null; //����������Ĵ�����,��ʵ����˭�ύ���ҵ�

	private String dealpooltask_yjbduserid = null; //�������
	private String dealpooltask_yjbdusercode = null; //�������
	private String dealpooltask_yjbdusername = null; //�������
	
	private boolean dealpooltask_isselfcycleclick = false; //��һ������
	private String dealpooltask_selfcycle_fromrolecode = null; //�Լ�ѭ��ʱ,���ĸ���ɫ��!
	private String dealpooltask_selfcycle_fromrolename = null; //�Լ�ѭ��ʱ,�����ѭ���󶨰�����һ����ɫӳ���ͼ,������
	private String dealpooltask_selfcycle_torolecode = null; //ȥ�ĸ���ɫ! 
	private String dealpooltask_selfcycle_torolename = null; //�Լ�ѭ��ʱ,�����ѭ���󶨰�����һ����ɫӳ���ͼ,������ȥ��Ľ�ɫ���ƣ���

	//�ڶ��ε��ʱ��¼����ѭ������Դ��ɫ��Ŀ���ɫ!!!
	private boolean secondIsSelfcycleclick = false; //��¼�ڶ��ε��ʱ,�Ƿ��ǵ������ѭ���еİ�ť!!
	private String secondSelfcycle_fromrolecode = null; //�ڶ��ε��ʱ����ѭ��ȥ�Ľ�ɫ����!!  ���ڴ����µ�����ʱ,�������е�from��ɫ�ǵ�һ�������е�torole,
	private String secondSelfcycle_fromrolename = null; //�ڶ��ε��ʱ����ѭ��ȥ�Ľ�ɫ����!!
	private String secondSelfcycle_torolecode = null; //�ڶ��ε��ʱ����ѭ��ȥ�Ľ�ɫ����!!  ���ڴ����µ�����ʱ,�������е�from��ɫ�ǵ�һ�������е�torole,
	private String secondSelfcycle_torolename = null; //�ڶ��ε��ʱ����ѭ��ȥ�Ľ�ɫ����!!

	private String processid = null; //���̶�������!
	private String dealpoolid = null; //������������м�¼������
	private String dealpoolidCreatetime = null; //����ʱ��

	private String fromactivity = null; //From���ڵ�����
	private String fromactivityCode = null; //From���ڵı���
	private String fromactivityName = null; //From���ڵ�����
	private String fromactivityType = null; //From���ڵ�����

	private String curractivity = null; //��ǰ���ڵ�����
	private String curractivityCode = null; //��ǰ���ڵı���
	private String curractivityName = null; //��ǰ���ڵ�����
	private String curractivityBLDeptName = null; //��ǰ�����������������!
	private String curractivityType = null; //��ǰ���ڵ�����
	private Boolean curractivityCanhalfend = false; //�Ƿ���԰�·����
	private Boolean curractivityCanselfaddparticipate = false; //�Ƿ�����Լ���Ӳ�����
	private Boolean curractivityIsSelfCycle = false; //��ǰ�����Ƿ���ѭ������
	private String curractivitySelfCycleRoleMap = null; //��ǰ���ڵĽ�ɫӳ�䣡��
	private String curractivityCheckUserPanel = null; //��ǰ���ڵ�ѡ���û��ĵ����򣬼���ѡ���û���������ұ��и���ť��"CheckUser",����ð�ť��ᵯ��һ���Ի��򣬸ò������������öԻ����·����!
	private String intercept1 = null; //������1
	private String intercept2 = null; //������2

	private String batchno = null; //����

	private boolean isStartStep = false; //�Ƿ�����������!����ʱд������ʱ��Ҫָ����ǰ�����Ƿ��������ĵ�һ����

	//������ս��ߵĻ�,����Ҫ�������в���!
	private boolean isprocessed = false; //�Ƿ����ս���
	private String approveModel = null; //���ģʽ 
	private DealActivityVO[] dealActivityVOs = null; //���д�����Ļ���.
	private DealTaskVO[] commitTaskVOs = null; //�ύʱ������!!!!�ؼ�,��һ��ȡ������ʱ,�����ǰ�DealActivityVO��,���ύʱ,���ǰ󶨸����,��ȡ������ʱ�Ƿֻ��ڵ�,���ύʱ�ǲ��ֻ��ڵ�!!!��ʵ���Ǵ�DealActivityVO��DealTaskVO����������!!

	private boolean isassignapprover = false; //�Ƿ���Ҫָ��������

	private boolean isneedmsg = false; //�Ƿ������������

	private String prioritylevel = null; //�����̶�
	private String dealtimelimit = null; //���������
	private String message = null; //����
	private String messagefile = null; //���︽��!!!
	private String ifSendEmail = null;//�Ƿ����ʼ�

	private int selectedRow = -1; //ѡ�е���,��ѡ����ĳһ��������û���������.
	private int selectedReasonCode = -1; //ѡ�е�Reasoncode

	private ComBoxItemVO[] reasonCodeComBoxItemVOs = null; //ԭ��..

	private boolean isRejectedDirUp = false; //
	
	//��ǩ-��ͬ���Ż�ǩ,ͬ������ռ����׷�� �����/2013-04-25��
	private String createbyid = null; //����һ�������¼�����
	private String participant_userdept = null; //�ύ�߻���

	public String getDealpoolid() {
		return dealpoolid;
	}

	public void setDealpoolid(String dealpoolid) {
		this.dealpoolid = dealpoolid;
	}

	public boolean isIsprocessed() {
		return isprocessed;
	}

	public void setIsprocessed(boolean isprocessed) {
		this.isprocessed = isprocessed; //
	}

	public DealActivityVO[] getDealActivityVOs() {
		return dealActivityVOs;
	}

	public void setDealActivityVOs(DealActivityVO[] dealActivityVOs) {
		this.dealActivityVOs = dealActivityVOs;
	}

	public boolean isIsassignapprover() {
		return isassignapprover;
	}

	public void setIsassignapprover(boolean isassignapprover) {
		this.isassignapprover = isassignapprover;
	}

	public boolean isIsneedmsg() {
		return isneedmsg;
	}

	public void setIsneedmsg(boolean isneedmsg) {
		this.isneedmsg = isneedmsg;
	}

	public String getWfinstanceid() {
		return wfinstanceid;
	}

	public void setWfinstanceid(String wfinstanceid) {
		this.wfinstanceid = wfinstanceid;
	}

	/**
	 * ������ʵ��id
	 * @return
	 */
	public String getParentinstanceid() {
		return parentinstanceid;
	}

	public void setParentinstanceid(String parentinstanceid) {
		this.parentinstanceid = parentinstanceid;
	}

	/**
	 * ������ʵ��id
	 * @return
	 */
	public String getRootinstanceid() {
		return rootinstanceid;
	}

	public void setRootinstanceid(String rootinstanceid) {
		this.rootinstanceid = rootinstanceid;
	}

	public String getBilltempletcode() {
		return billtempletcode;
	}

	public void setBilltempletcode(String billtempletcode) {
		this.billtempletcode = billtempletcode;
	}

	public String getBilltablename() {
		return billtablename;
	}

	public void setBilltablename(String billtablename) {
		this.billtablename = billtablename;
	}

	public String getBillQueryTablename() {
		return billQueryTablename;
	}

	public void setBillQueryTablename(String _billQueryTabName) {
		this.billQueryTablename = _billQueryTabName;
	}

	public String getBillpkname() {
		return billpkname;
	}

	public void setBillpkname(String billpkname) {
		this.billpkname = billpkname;
	}

	public String getBillpkvalue() {
		return billpkvalue;
	}

	public void setBillpkvalue(String billpkvalue) {
		this.billpkvalue = billpkvalue;
	}

	public String getWfinstance_createuserid() {
		return wfinstance_createuserid;
	}

	public void setWfinstance_createuserid(String wfinstance_createuserid) {
		this.wfinstance_createuserid = wfinstance_createuserid;
	}

	public String getWfinstance_createusercode() {
		return wfinstance_createusercode;
	}

	public void setWfinstance_createusercode(String wfinstance_createusercode) {
		this.wfinstance_createusercode = wfinstance_createusercode;
	}

	public String getWfinstance_createusername() {
		return wfinstance_createusername;
	}

	public void setWfinstance_createusername(String wfinstance_createusername) {
		this.wfinstance_createusername = wfinstance_createusername;
	}

	public String getWfinstance_createactivityid() {
		return wfinstance_createactivityid;
	}

	public void setWfinstance_createactivityid(String wfinstance_createactivityid) {
		this.wfinstance_createactivityid = wfinstance_createactivityid;
	}

	public String getWfinstance_createactivitycode() {
		return wfinstance_createactivitycode;
	}

	public void setWfinstance_createactivitycode(String wfinstance_createactivitycode) {
		this.wfinstance_createactivitycode = wfinstance_createactivitycode;
	}

	public String getWfinstance_createactivityname() {
		return wfinstance_createactivityname;
	}

	public void setWfinstance_createactivityname(String wfinstance_createactivityname) {
		this.wfinstance_createactivityname = wfinstance_createactivityname;
	}

	public String getWfinstance_createactivitytype() {
		return wfinstance_createactivitytype;
	}

	public void setWfinstance_createactivitytype(String wfinstance_createactivitytype) {
		this.wfinstance_createactivitytype = wfinstance_createactivitytype;
	}

	public String getCurrParticipantUserName() {
		return currParticipantUserName;
	}

	public void setCurrParticipantUserName(String currParticipantUserName) {
		this.currParticipantUserName = currParticipantUserName;
	}

	public String getDealpooltask_createuserid() {
		return dealpooltask_createuserid;
	}

	public void setDealpooltask_createuserid(String dealpooltask_createuserid) {
		this.dealpooltask_createuserid = dealpooltask_createuserid;
	}

	public String getDealpooltask_createusercode() {
		return dealpooltask_createusercode;
	}

	public void setDealpooltask_createusercode(String dealpooltask_createusercode) {
		this.dealpooltask_createusercode = dealpooltask_createusercode;
	}

	public String getDealpooltask_createusername() {
		return dealpooltask_createusername;
	}

	public void setDealpooltask_createusername(String dealpooltask_createusername) {
		this.dealpooltask_createusername = dealpooltask_createusername;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessagefile() {
		return messagefile;
	}

	public void setMessagefile(String messagefile) {
		this.messagefile = messagefile;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public String getCurractivity() {
		return curractivity;
	}

	public void setCurractivity(String curractivity) {
		this.curractivity = curractivity;
	}

	public String getFromactivity() {
		return fromactivity;
	}

	public void setFromactivity(String fromactivity) {
		this.fromactivity = fromactivity;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public String getIntercept1() {
		return intercept1;
	}

	public void setIntercept1(String intercept1) {
		this.intercept1 = intercept1;
	}

	public String getIntercept2() {
		return intercept2;
	}

	public void setIntercept2(String intercept2) {
		this.intercept2 = intercept2;
	}

	public String getBatchno() {
		return batchno;
	}

	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}

	public String getCurractivityCheckUserPanel() {
		return curractivityCheckUserPanel;
	}

	public void setCurractivityCheckUserPanel(String _curractivityCheckUserPanel) {
		this.curractivityCheckUserPanel = _curractivityCheckUserPanel;
	}

	public ComBoxItemVO[] getReasonCodeComBoxItemVOs() {
		return reasonCodeComBoxItemVOs;
	}

	public void setReasonCodeComBoxItemVOs(ComBoxItemVO[] reasonCodeComBoxItemVOs) {
		this.reasonCodeComBoxItemVOs = reasonCodeComBoxItemVOs;
	}

	public int getSelectedReasonCode() {
		return selectedReasonCode;
	}

	public void setSelectedReasonCode(int selectedReasonCode) {
		this.selectedReasonCode = selectedReasonCode;
	}

	public String getFromactivityCode() {
		return fromactivityCode;
	}

	public void setFromactivityCode(String fromactivityCode) {
		this.fromactivityCode = fromactivityCode;
	}

	public String getCurractivityCode() {
		return curractivityCode;
	}

	public String getFromactivityName() {
		return fromactivityName;
	}

	public void setFromactivityName(String fromactivityName) {
		this.fromactivityName = fromactivityName;
	}

	public String getFromactivityType() {
		return fromactivityType;
	}

	public void setFromactivityType(String fromactivityType) {
		this.fromactivityType = fromactivityType;
	}

	public void setCurractivityCode(String curractivityCode) {
		this.curractivityCode = curractivityCode;
	}

	public String getCurractivityName() {
		return curractivityName;
	}

	public void setCurractivityName(String curractivityName) {
		this.curractivityName = curractivityName;
	}

	public String getCurractivityBLDeptName() {
		return curractivityBLDeptName;
	}

	public void setCurractivityBLDeptName(String curractivityBLDeptName) {
		this.curractivityBLDeptName = curractivityBLDeptName;
	}

	public String getCurractivityType() {
		return curractivityType;
	}

	public void setCurractivityType(String curractivityType) {
		this.curractivityType = curractivityType;
	}

	public Boolean getCurractivityCanhalfend() {
		return curractivityCanhalfend;
	}

	public void setCurractivityCanhalfend(Boolean curractivityCanhalfend) {
		this.curractivityCanhalfend = curractivityCanhalfend;
	}

	public Boolean getCurractivityCanselfaddparticipate() {
		return curractivityCanselfaddparticipate;
	}

	public void setCurractivityCanselfaddparticipate(Boolean curractivityCanselfaddparticipate) {
		this.curractivityCanselfaddparticipate = curractivityCanselfaddparticipate;
	}

	public BillVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this); //
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (BillVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	public DealTaskVO[] getCommitTaskVOs() {
		return commitTaskVOs;
	}

	public void setCommitTaskVOs(DealTaskVO[] commitTaskVOs) {
		this.commitTaskVOs = commitTaskVOs;
	}

	public String getDealpoolidCreatetime() {
		return dealpoolidCreatetime;
	}

	public void setDealpoolidCreatetime(String dealpoolidcreatetime) {
		this.dealpoolidCreatetime = dealpoolidcreatetime;
	}

	public String getPrioritylevel() {
		return prioritylevel;
	}

	public void setPrioritylevel(String prioritylevel) {
		this.prioritylevel = prioritylevel;
	}

	public String getDealtimelimit() {
		return dealtimelimit;
	}

	public void setDealtimelimit(String dealtimelimit) {
		this.dealtimelimit = dealtimelimit;
	}

	public boolean isRejectedDirUp() {
		return isRejectedDirUp;
	}

	public void setRejectedDirUp(boolean isRejectedDirUp) {
		this.isRejectedDirUp = isRejectedDirUp;
	}

	public String getIfSendEmail() {
		return ifSendEmail;
	}

	public void setIfSendEmail(String ifSendEmail) {
		this.ifSendEmail = ifSendEmail;
	}

	public String getDealpooltask_createuser_accruserid() {
		return dealpooltask_createuser_accruserid;
	}

	public void setDealpooltask_createuser_accruserid(String dealpooltask_createuser_accruserid) {
		this.dealpooltask_createuser_accruserid = dealpooltask_createuser_accruserid;
	}

	public String getDealpooltask_createuser_accrusercode() {
		return dealpooltask_createuser_accrusercode;
	}

	public void setDealpooltask_createuser_accrusercode(String dealpooltask_createuser_accrusercode) {
		this.dealpooltask_createuser_accrusercode = dealpooltask_createuser_accrusercode;
	}

	public String getDealpooltask_createuser_accrusername() {
		return dealpooltask_createuser_accrusername;
	}

	public void setDealpooltask_createuser_accrusername(String dealpooltask_createuser_accrusername) {
		this.dealpooltask_createuser_accrusername = dealpooltask_createuser_accrusername;
	}

	public void setCurractivityIsSelfCycle(Boolean _selfCycle) {
		this.curractivityIsSelfCycle = _selfCycle; //
	}

	//��ѭ��ӳ��
	public void setCurractivitySelfCycleRoleMap(String _map) {
		this.curractivitySelfCycleRoleMap = _map; //
	}

	public Boolean getCurractivityIsSelfCycle() {
		return curractivityIsSelfCycle;
	}

	public String getCurractivitySelfCycleRoleMap() {
		return curractivitySelfCycleRoleMap;
	}

	public boolean isStartStep() {
		return isStartStep;
	}

	public void setStartStep(boolean isStartStep) {
		this.isStartStep = isStartStep;
	}

	public boolean isDealpooltask_isselfcycleclick() {
		return dealpooltask_isselfcycleclick;
	}

	public void setDealpooltask_isselfcycleclick(boolean dealpooltask_isselfcycleclick) {
		this.dealpooltask_isselfcycleclick = dealpooltask_isselfcycleclick;
	}

	public String getDealpooltask_selfcycle_fromrolecode() {
		return dealpooltask_selfcycle_fromrolecode;
	}

	public void setDealpooltask_selfcycle_fromrolecode(String dealpooltask_selfcycle_fromrolecode) {
		this.dealpooltask_selfcycle_fromrolecode = dealpooltask_selfcycle_fromrolecode;
	}

	public String getDealpooltask_selfcycle_fromrolename() {
		return dealpooltask_selfcycle_fromrolename;
	}

	public void setDealpooltask_selfcycle_fromrolename(String dealpooltask_selfcycle_fromrolename) {
		this.dealpooltask_selfcycle_fromrolename = dealpooltask_selfcycle_fromrolename;
	}

	public String getDealpooltask_selfcycle_torolecode() {
		return dealpooltask_selfcycle_torolecode;
	}

	public void setDealpooltask_selfcycle_torolecode(String dealpooltask_selfcycle_torolecode) {
		this.dealpooltask_selfcycle_torolecode = dealpooltask_selfcycle_torolecode;
	}

	public String getDealpooltask_selfcycle_torolename() {
		return dealpooltask_selfcycle_torolename;
	}

	public void setDealpooltask_selfcycle_torolename(String dealpooltask_selfcycle_torolename) {
		this.dealpooltask_selfcycle_torolename = dealpooltask_selfcycle_torolename;
	}

	public boolean isSecondIsSelfcycleclick() {
		return secondIsSelfcycleclick;
	}

	public void setSecondIsSelfcycleclick(boolean secondIsSelfcycleclick) {
		this.secondIsSelfcycleclick = secondIsSelfcycleclick;
	}

	public String getSecondSelfcycle_torolecode() {
		return secondSelfcycle_torolecode;
	}

	public void setSecondSelfcycle_torolecode(String secondSelfcycle_torolecode) {
		this.secondSelfcycle_torolecode = secondSelfcycle_torolecode;
	}

	public String getSecondSelfcycle_torolename() {
		return secondSelfcycle_torolename;
	}

	public void setSecondSelfcycle_torolename(String secondSelfcycle_torolename) {
		this.secondSelfcycle_torolename = secondSelfcycle_torolename;
	}

	public String getSecondSelfcycle_fromrolecode() {
		return secondSelfcycle_fromrolecode;
	}

	public void setSecondSelfcycle_fromrolecode(String secondSelfcycle_fromrolecode) {
		this.secondSelfcycle_fromrolecode = secondSelfcycle_fromrolecode;
	}

	public String getSecondSelfcycle_fromrolename() {
		return secondSelfcycle_fromrolename;
	}

	public void setSecondSelfcycle_fromrolename(String secondSelfcycle_fromrolename) {
		this.secondSelfcycle_fromrolename = secondSelfcycle_fromrolename;
	}

	public String getDealpooltask_yjbduserid() {
		return dealpooltask_yjbduserid;
	}

	public void setDealpooltask_yjbduserid(String dealpooltask_yjbduserid) {
		this.dealpooltask_yjbduserid = dealpooltask_yjbduserid;
	}

	public String getDealpooltask_yjbdusercode() {
		return dealpooltask_yjbdusercode;
	}

	public void setDealpooltask_yjbdusercode(String dealpooltask_yjbdusercode) {
		this.dealpooltask_yjbdusercode = dealpooltask_yjbdusercode;
	}

	public String getDealpooltask_yjbdusername() {
		return dealpooltask_yjbdusername;
	}

	public void setDealpooltask_yjbdusername(String dealpooltask_yjbdusername) {
		this.dealpooltask_yjbdusername = dealpooltask_yjbdusername;
	}

	public String getApproveModel() {
		return approveModel;
	}

	public void setApproveModel(String approveModel) {
		this.approveModel = approveModel;
	}

	public String getCreatebyid() {
		return createbyid;
	}

	public void setCreatebyid(String createbyid) {
		this.createbyid = createbyid;
	}

	public String getParticipant_userdept() {
		return participant_userdept;
	}

	public void setParticipant_userdept(String participant_userdept) {
		this.participant_userdept = participant_userdept;
	}

}
