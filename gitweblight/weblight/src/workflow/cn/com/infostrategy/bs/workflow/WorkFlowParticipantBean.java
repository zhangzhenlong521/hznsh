package cn.com.infostrategy.bs.workflow;

/**
 * 工作流参与者的定义类!!
 * 其中包括:参与的部门,参与的人员.
 * @author xch
 *
 */
public class WorkFlowParticipantBean {

	private String participantDeptType = null; //参与部门的类型
	private String participantDeptId = null; //参与部门的主键.
	private String participantDeptLinkcode = null; //参与部门的linkCode

	private WorkFlowParticipantUserBean[] participantUserBeans = null; //参与的所有人

	private String participtMsg = null; //该参与者生成的部门,人员的结果信息!!

	public String getParticipantDeptType() {
		return participantDeptType;
	}

	public void setParticipantDeptType(String participantDeptType) {
		this.participantDeptType = participantDeptType;
	}

	public String getParticipantDeptId() {
		return participantDeptId;
	}

	public void setParticipantDeptId(String participantDeptId) {
		this.participantDeptId = participantDeptId;
	}

	public WorkFlowParticipantUserBean[] getParticipantUserBeans() {
		return participantUserBeans;
	}

	public void setParticipantUserBeans(WorkFlowParticipantUserBean[] participantUserBeans) {
		this.participantUserBeans = participantUserBeans;
	}

	public String getParticipantDeptLinkcode() {
		return participantDeptLinkcode;
	}

	public void setParticipantDeptLinkcode(String participantDeptLinkcode) {
		this.participantDeptLinkcode = participantDeptLinkcode;
	}

	public String getParticiptMsg() {
		return participtMsg;
	}

	public void setParticiptMsg(String participtMsg) {
		this.participtMsg = participtMsg;
	}

}
