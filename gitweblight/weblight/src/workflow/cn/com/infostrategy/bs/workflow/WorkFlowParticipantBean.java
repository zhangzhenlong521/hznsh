package cn.com.infostrategy.bs.workflow;

/**
 * �����������ߵĶ�����!!
 * ���а���:����Ĳ���,�������Ա.
 * @author xch
 *
 */
public class WorkFlowParticipantBean {

	private String participantDeptType = null; //���벿�ŵ�����
	private String participantDeptId = null; //���벿�ŵ�����.
	private String participantDeptLinkcode = null; //���벿�ŵ�linkCode

	private WorkFlowParticipantUserBean[] participantUserBeans = null; //�����������

	private String participtMsg = null; //�ò��������ɵĲ���,��Ա�Ľ����Ϣ!!

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
