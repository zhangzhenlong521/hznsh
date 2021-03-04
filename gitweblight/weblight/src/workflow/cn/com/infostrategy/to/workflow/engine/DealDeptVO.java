package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;

/**
 * 参与部门的VO
 * 它被包含在处理环节中,即一个环节可以有多个部门!!!
 * @author xch
 *
 */
public class DealDeptVO implements Serializable {

	private static final long serialVersionUID = 8721145820514100837L;
	private String deptType = null; //参与部门类型,包括:本部门,本支行,本分行,总行,全行,上报路线1,2,3
	private String deptId = null; //参与部门的主键
	private String deptLinkCode = null; //参与部门的LinkCode

	//由于当从部门中选择某个人员时没有流程环节,连线等信息.而生成pub_wf_dealpool任务时却必须要这些信息,所以需要指定这些信息!!
	private String fromActivityId = null; //来源环节主键.
	private String fromActivityName = null; //来源环节名称

	private String transitionId = null; //连线的主键
	private String transitionCode = null; //连线的编码...
	private String transitionName = null; //连线的名称
	private String transitionIntercept = null; //连线的拦截器
	private String transitionMailSubject = null; //连线上定义的邮件主题..
	private String transitionMailContent = null; //连线上定义的邮件内容..

	private String transitionDealtype = null; //连线类型

	private String currActivityId = null; //环节主键..
	private String currActivityType = null; //环节类型
	private String currActivityCode = null; //环节编码..
	private String currActivityName = null; //环节名称..

	public DealDeptVO(String _depttype, String _deptid) {
		this.deptType = _depttype; //
		this.deptId = _deptid; //
	}

	public String getDeptType() {
		return deptType;
	}

	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

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

	public String getTransitionId() {
		return transitionId;
	}

	public void setTransitionId(String transitionId) {
		this.transitionId = transitionId;
	}

	public String getTransitionCode() {
		return transitionCode;
	}

	public void setTransitionCode(String transitionCode) {
		this.transitionCode = transitionCode;
	}

	public String getTransitionName() {
		return transitionName;
	}

	public void setTransitionName(String transitionName) {
		this.transitionName = transitionName;
	}

	public String getTransitionIntercept() {
		return transitionIntercept;
	}

	public void setTransitionIntercept(String transitionIntercept) {
		this.transitionIntercept = transitionIntercept;
	}

	public String getTransitionMailSubject() {
		return transitionMailSubject;
	}

	public void setTransitionMailSubject(String transitionMailSubject) {
		this.transitionMailSubject = transitionMailSubject;
	}

	public String getTransitionMailContent() {
		return transitionMailContent;
	}

	public void setTransitionMailContent(String transitionMailContent) {
		this.transitionMailContent = transitionMailContent;
	}

	public String getTransitionDealtype() {
		return transitionDealtype;
	}

	public void setTransitionDealtype(String transitionDealtype) {
		this.transitionDealtype = transitionDealtype;
	}

	public String getCurrActivityId() {
		return currActivityId;
	}

	public void setCurrActivityId(String currActivityId) {
		this.currActivityId = currActivityId;
	}

	public String getCurrActivityType() {
		return currActivityType;
	}

	public void setCurrActivityType(String currActivityType) {
		this.currActivityType = currActivityType;
	}

	public String getCurrActivityCode() {
		return currActivityCode;
	}

	public void setCurrActivityCode(String currActivityCode) {
		this.currActivityCode = currActivityCode;
	}

	public String getCurrActivityName() {
		return currActivityName;
	}

	public void setCurrActivityName(String currActivityName) {
		this.currActivityName = currActivityName;
	}

	public String getDeptLinkCode() {
		return deptLinkCode;
	}

	public void setDeptLinkCode(String deptLinkCode) {
		this.deptLinkCode = deptLinkCode;
	}

	@Override
	public String toString() {
		StringBuffer sb_text = new StringBuffer(); //
		sb_text.append("机构类型:[" + this.deptType + "]\r\n");
		sb_text.append("机构Id:[" + this.deptId + "]\r\n");
		sb_text.append("机构LinkCode:[" + this.deptLinkCode + "]\r\n");
		return sb_text.toString(); //
	}

}
