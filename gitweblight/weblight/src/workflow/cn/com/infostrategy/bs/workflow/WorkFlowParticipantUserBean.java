package cn.com.infostrategy.bs.workflow;

import java.io.Serializable;

/**
 * 参与者的数据对象!非常关键!!!
 * Bean表示是服务器端的对象
 * @author xch
 *
 */
public class WorkFlowParticipantUserBean implements Serializable {

	private static final long serialVersionUID = 4193204214136781281L;
	private String userid = null; //参与的用户
	private String usercode = null; //参与的用户Code
	private String username = null; //参与的用户Name

	private String accrUserid = null; //授权人的主键,即自己可能被换掉,因为被授权了
	private String accrUsercode = null; //授权人的编码
	private String accrUsername = null; //授权人的名称

	private String userdeptid = null; //参与的用户所属机构,即以什么机构身份参与的,因为一个人可以有多个身份!!
	private String userdeptcode = null; //参与的用户所属机构,即以什么机构身份参与的,因为一个人可以有多个身份!!
	private String userdeptname = null; //参与的用户所属机构,即以什么机构身份参与的,因为一个人可以有多个身份!!

	private String userroleid = null; //参与的用户的角色,即是什么什么角色参与的,因为一个人可以有多个角色.
	private String userrolecode = null; //参与的用户的角色,即是什么什么角色参与的,因为一个人可以有多个角色.
	private String userrolename = null; //参与的用户的角色,即是什么什么角色参与的,因为一个人可以有多个角色.

	private String participantType = null; //参与的类型,分静态用户,静态角色,动态角色三种
	private String successParticipantReason = null; //

	private boolean isCCTo = false; //是否是抄送模式!!!如果是抄送的模式,则人员出现提交框的下面!!默认是提交的,而不是抄送的!!

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
