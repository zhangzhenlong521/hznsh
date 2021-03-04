package cn.com.infostrategy.bs.workflow;

import java.io.Serializable;

/**
 * �����ߵ����ݶ���!�ǳ��ؼ�!!!
 * Bean��ʾ�Ƿ������˵Ķ���
 * @author xch
 *
 */
public class WorkFlowParticipantUserBean implements Serializable {

	private static final long serialVersionUID = 4193204214136781281L;
	private String userid = null; //������û�
	private String usercode = null; //������û�Code
	private String username = null; //������û�Name

	private String accrUserid = null; //��Ȩ�˵�����,���Լ����ܱ�����,��Ϊ����Ȩ��
	private String accrUsercode = null; //��Ȩ�˵ı���
	private String accrUsername = null; //��Ȩ�˵�����

	private String userdeptid = null; //������û���������,����ʲô������ݲ����,��Ϊһ���˿����ж�����!!
	private String userdeptcode = null; //������û���������,����ʲô������ݲ����,��Ϊһ���˿����ж�����!!
	private String userdeptname = null; //������û���������,����ʲô������ݲ����,��Ϊһ���˿����ж�����!!

	private String userroleid = null; //������û��Ľ�ɫ,����ʲôʲô��ɫ�����,��Ϊһ���˿����ж����ɫ.
	private String userrolecode = null; //������û��Ľ�ɫ,����ʲôʲô��ɫ�����,��Ϊһ���˿����ж����ɫ.
	private String userrolename = null; //������û��Ľ�ɫ,����ʲôʲô��ɫ�����,��Ϊһ���˿����ж����ɫ.

	private String participantType = null; //���������,�־�̬�û�,��̬��ɫ,��̬��ɫ����
	private String successParticipantReason = null; //

	private boolean isCCTo = false; //�Ƿ��ǳ���ģʽ!!!����ǳ��͵�ģʽ,����Ա�����ύ�������!!Ĭ�����ύ��,�����ǳ��͵�!!

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserdeptid() {
		return userdeptid;
	}

	public void setUserdeptid(String userdeptid) {
		this.userdeptid = userdeptid;
	}

	public String getUserdeptcode() {
		return userdeptcode;
	}

	public void setUserdeptcode(String userdeptcode) {
		this.userdeptcode = userdeptcode;
	}

	public String getUserdeptname() {
		return userdeptname;
	}

	public void setUserdeptname(String userdeptname) {
		this.userdeptname = userdeptname;
	}

	public String getUserroleid() {
		return userroleid;
	}

	public void setUserroleid(String userroleid) {
		this.userroleid = userroleid;
	}

	public String getUserrolecode() {
		return userrolecode;
	}

	public void setUserrolecode(String userrolecode) {
		this.userrolecode = userrolecode;
	}

	public String getUserrolename() {
		return userrolename;
	}

	public void setUserrolename(String userrolename) {
		this.userrolename = userrolename;
	}

	public String getParticipantType() {
		return participantType;
	}

	public void setParticipantType(String participantType) {
		this.participantType = participantType;
	}

	public String getSuccessParticipantReason() {
		return successParticipantReason;
	}

	public void setSuccessParticipantReason(String successParticipantReason) {
		this.successParticipantReason = successParticipantReason;
	}

	public String getAccrUserid() {
		return accrUserid;
	}

	public void setAccrUserid(String accrUserid) {
		this.accrUserid = accrUserid;
	}

	public String getAccrUsercode() {
		return accrUsercode;
	}

	public void setAccrUsercode(String accrUsercode) {
		this.accrUsercode = accrUsercode;
	}

	public String getAccrUsername() {
		return accrUsername;
	}

	public void setAccrUsername(String accrUsername) {
		this.accrUsername = accrUsername;
	}

	public boolean isCCTo() {
		return isCCTo;
	}

	public void setCCTo(boolean isCCTo) {
		this.isCCTo = isCCTo;
	}

	@Override
	protected WorkFlowParticipantUserBean clone() throws CloneNotSupportedException {
		WorkFlowParticipantUserBean newBean = new WorkFlowParticipantUserBean(); //
		newBean.setUserid(this.getUserid()); //
		newBean.setUsercode(this.getUsercode()); //
		newBean.setUsername(this.getUsername()); //

		newBean.setAccrUserid(this.getAccrUserid()); //
		newBean.setAccrUsercode(this.getAccrUsercode()); //
		newBean.setAccrUsername(this.getAccrUsername()); //

		newBean.setUserdeptid(this.getUserdeptid()); //
		newBean.setUserdeptcode(this.getUserdeptcode()); //
		newBean.setUserdeptname(this.getUserdeptname()); //

		newBean.setUserroleid(this.getUserroleid()); //
		newBean.setUserrolecode(this.getUserrolecode()); //
		newBean.setUserrolename(this.getUserrolename()); //

		newBean.setParticipantType(this.getParticipantType()); //
		newBean.setSuccessParticipantReason(this.getSuccessParticipantReason()); //

		newBean.setCCTo(this.isCCTo()); //
		return newBean;
	}

}
