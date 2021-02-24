package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;

/**
 * �����������VO,������Դ����,Ŀ�껷��,������
 * @author xch
 *
 */
public class DealTaskVO implements Serializable {

	private static final long serialVersionUID = 8157945443051358457L;

	private String fromActivityId = null; //��Դ��������.
	private String fromActivityName = null; //��Դ��������

	private String transitionId = null; //���ߵ�����
	private String transitionCode = null; //���ߵı���...
	private String transitionName = null; //���ߵ�����
	private String transitionDealtype = null; //��������
	private String transitionIntercept = null; //���ߵ�������
	private String transitionMailSubject = null; //�����϶�����ʼ�����..
	private String transitionMailContent = null; //�����϶�����ʼ�����..

	private String currActivityId = null; //��������..
	private String currActivityType = null; //��������,��START,END,������
	private String currActivityCode = null; //���ڱ���..
	private String currActivityName = null; //��������..
	private String currActivityApproveModel = null; //��ǰ���ڵ����ģʽ,������[��ռ/��ǩ/���]

	private String participantUserId = null; //�����ߵ�����.
	private String participantUserCode = null; //�����ߵı���
	private String participantUserName = null; //�����ߵ�����..

	private String participantUserDeptId = null; //�����߲��ŵ�����.
	private String participantUserDeptCode = null; //�����߲��ŵı���
	private String participantUserDeptName = null; //�����߲��ŵ�����

	private String participantUserRoleId = null; //�����߽�ɫ������..
	private String participantUserRoleCode = null; //�����߽�ɫ�ı���.
	private String participantUserRoleName = null; //�����߽�ɫ������..

	private String accrUserId = null; //��Ȩ������.
	private String accrUserCode = null; //��Ȩ�˱���.
	private String accrUserName = null; //��Ȩ������.
	
	private String yjbdUserId = null; //����������.
	private String yjbdUserCode = null; //�����˱���.
	private String yjbdUserName = null; //����������.

	private String successParticipantReason = null; //�ɹ������ԭ��..
	private String iseverprocessed = null; //�Ƿ������߹� 

	private boolean isRejectedDirUp = false; //�˻غ�ֱ��ƹ��ʽ�ύ����!!

	private boolean isCCTo = false; //�Ƿ��ǳ���ģʽ,������ǳ��͵Ļ�,���������ύ����·�!!!

	public String getFromActivityId() {
		return fromActivityId;
	}

	public void setFromActivityId(String fromActivityId) {
		this.fromActivityId = fromActivityId;
	}

	public String getFromActivityName() {
		return fromActivityName;
	}

	public void setFromActivityName(String fromActivityName) {
		this.fromActivityName = fromActivityName;
	}

	public String getCurrActivityId() {
		return currActivityId;
	}

	public void setCurrActivityId(String currActivityId) {
		this.currActivityId = currActivityId;
	}

	public String getCurrActivityName() {
		return currActivityName;
	}

	public void setCurrActivityName(String currActivityName) {
		this.currActivityName = currActivityName;
	}

	public String getParticipantUserId() {
		return participantUserId;
	}

	public void setParticipantUserId(String participantUserId) {
		this.participantUserId = participantUserId;
	}

	public String getParticipantUserName() {
		return participantUserName;
	}

	public void setParticipantUserName(String participantUserName) {
		this.participantUserName = participantUserName;
	}

	public String getTransitionId() {
		return transitionId;
	}

	public void setTransitionId(String transitionId) {
		this.transitionId = transitionId;
	}

	public String getTransitionName() {
		return transitionName;
	}

	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}

	public String toString() {
		return getParticipantUserName(); // + "(" + getCurrActivityName() + ")";
	}

	public String getTransitionDealtype() {
		return transitionDealtype;
	}

	public void setTransitionDealtype(String transitionDealtype) {
		this.transitionDealtype = transitionDealtype;
	}

	public String getTransitionCode() {
		return transitionCode;
	}

	public void setTransitionCode(String _transitionCode) {
		this.transitionCode = _transitionCode;
	}

	public String getCurrActivityCode() {
		return currActivityCode;
	}

	public void setCurrActivityCode(String currActivityCode) {
		this.currActivityCode = currActivityCode;
	}

	public String getCurrActivityType() {
		return currActivityType;
	}

	public void setCurrActivityType(String currActivityType) {
		this.currActivityType = currActivityType;
	}

	public String getCurrActivityApproveModel() {
		return currActivityApproveModel;
	}

	public void setCurrActivityApproveModel(String currActivityApproveModel) {
		this.currActivityApproveModel = currActivityApproveModel;
	}

	public String getTransitionMailContent() {
		return transitionMailContent;
	}

	public void setTransitionMailContent(String transitionMailContent) {
		this.transitionMailContent = transitionMailContent;
	}

	public String getTransitionMailSubject() {
		return transitionMailSubject;
	}

	public void setTransitionMailSubject(String transitionMailSubject) {
		this.transitionMailSubject = transitionMailSubject;
	}

	public String getParticipantUserCode() {
		return participantUserCode;
	}

	public void setParticipantUserCode(String participantUserCode) {
		this.participantUserCode = participantUserCode;
	}

	public String getSuccessParticipantReason() {
		return successParticipantReason;
	}

	public void setSuccessParticipantReason(String successParticipantReason) {
		this.successParticipantReason = successParticipantReason;
	}

	public String getTransitionIntercept() {
		return transitionIntercept;
	}

	public void setTransitionIntercept(String transitionIntercept) {
		this.transitionIntercept = transitionIntercept;
	}

	public String getIseverprocessed() {
		return iseverprocessed;
	}

	public void setIseverprocessed(String iseverprocessed) {
		this.iseverprocessed = iseverprocessed;
	}

	public String getParticipantUserDeptId() {
		return participantUserDeptId;
	}

	public void setParticipantUserDeptId(String participantUserDeptId) {
		this.participantUserDeptId = participantUserDeptId;
	}

	public String getParticipantUserDeptCode() {
		return participantUserDeptCode;
	}

	public void setParticipantUserDeptCode(String participantUserDeptCode) {
		this.participantUserDeptCode = participantUserDeptCode;
	}

	public String getParticipantUserDeptName() {
		return participantUserDeptName;
	}

	public void setParticipantUserDeptName(String participantUserDeptName) {
		this.participantUserDeptName = participantUserDeptName;
	}

	public String getParticipantUserRoleId() {
		return participantUserRoleId;
	}

	public void setParticipantUserRoleId(String participantUserRoleId) {
		this.participantUserRoleId = participantUserRoleId;
	}

	public String getParticipantUserRoleCode() {
		return participantUserRoleCode;
	}

	public void setParticipantUserRoleCode(String participantUserRoleCode) {
		this.participantUserRoleCode = participantUserRoleCode;
	}

	public String getParticipantUserRoleName() {
		return participantUserRoleName;
	}

	public void setParticipantUserRoleName(String participantUserRoleName) {
		this.participantUserRoleName = participantUserRoleName;
	}

	public boolean isRejectedDirUp() {
		return isRejectedDirUp;
	}

	public void setRejectedDirUp(boolean isRejectedDirUp) {
		this.isRejectedDirUp = isRejectedDirUp;
	}

	public String getAccrUserId() {
		return accrUserId;
	}

	public void setAccrUserId(String accrUserId) {
		this.accrUserId = accrUserId;
	}

	public String getAccrUserCode() {
		return accrUserCode;
	}

	public void setAccrUserCode(String accrUserCode) {
		this.accrUserCode = accrUserCode;
	}

	public String getAccrUserName() {
		return accrUserName;
	}

	public void setAccrUserName(String accrUserName) {
		this.accrUserName = accrUserName;
	}

	public boolean isCCTo() {
		return isCCTo;
	}

	public void setCCTo(boolean isCCTo) {
		this.isCCTo = isCCTo;
	}

	public String getYjbdUserId() {
		return yjbdUserId;
	}

	public void setYjbdUserId(String yjbdUserId) {
		this.yjbdUserId = yjbdUserId;
	}

	public String getYjbdUserCode() {
		return yjbdUserCode;
	}

	public void setYjbdUserCode(String yjbdUserCode) {
		this.yjbdUserCode = yjbdUserCode;
	}

	public String getYjbdUserName() {
		return yjbdUserName;
	}

	public void setYjbdUserName(String yjbdUserName) {
		this.yjbdUserName = yjbdUserName;
	}

}
