package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;

/**
 * 待处理任务的VO,包括来源环节,目标环节,参与者
 * @author xch
 *
 */
public class DealTaskVO implements Serializable {

	private static final long serialVersionUID = 8157945443051358457L;

	private String fromActivityId = null; //来源环节主键.
	private String fromActivityName = null; //来源环节名称

	private String transitionId = null; //连线的主键
	private String transitionCode = null; //连线的编码...
	private String transitionName = null; //连线的名称
	private String transitionDealtype = null; //连线类型
	private String transitionIntercept = null; //连线的拦截器
	private String transitionMailSubject = null; //连线上定义的邮件主题..
	private String transitionMailContent = null; //连线上定义的邮件内容..

	private String currActivityId = null; //环节主键..
	private String currActivityType = null; //环节类型,有START,END,子流程
	private String currActivityCode = null; //环节编码..
	private String currActivityName = null; //环节名称..
	private String currActivityApproveModel = null; //当前环节的审核模式,比如是[抢占/会签/会办]

	private String participantUserId = null; //参与者的主键.
	private String participantUserCode = null; //参与者的编码
	private String participantUserName = null; //参与者的主键..

	private String participantUserDeptId = null; //参与者部门的主键.
	private String participantUserDeptCode = null; //参与者部门的编码
	private String participantUserDeptName = null; //参与者部门的名称

	private String participantUserRoleId = null; //参与者角色的主键..
	private String participantUserRoleCode = null; //参与者角色的编码.
	private String participantUserRoleName = null; //参与者角色的主键..

	private String accrUserId = null; //授权人主键.
	private String accrUserCode = null; //授权人编码.
	private String accrUserName = null; //授权人名称.
	
	private String yjbdUserId = null; //补登人主键.
	private String yjbdUserCode = null; //补登人编码.
	private String yjbdUserName = null; //补登人名称.

	private String successParticipantReason = null; //成功参与的原因..
	private String iseverprocessed = null; //是否曾经走过 

	private boolean isRejectedDirUp = false; //退回后直接乒乓式提交给我!!

	private boolean isCCTo = false; //是否是抄送模式,即如果是抄送的话,则会出现在提交框的下方!!!

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
