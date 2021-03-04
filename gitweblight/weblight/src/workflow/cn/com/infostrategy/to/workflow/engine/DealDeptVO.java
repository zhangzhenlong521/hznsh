package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;

/**
 * ���벿�ŵ�VO
 * ���������ڴ�������,��һ�����ڿ����ж������!!!
 * @author xch
 *
 */
public class DealDeptVO implements Serializable {

	private static final long serialVersionUID = 8721145820514100837L;
	private String deptType = null; //���벿������,����:������,��֧��,������,����,ȫ��,�ϱ�·��1,2,3
	private String deptId = null; //���벿�ŵ�����
	private String deptLinkCode = null; //���벿�ŵ�LinkCode

	//���ڵ��Ӳ�����ѡ��ĳ����Աʱû�����̻���,���ߵ���Ϣ.������pub_wf_dealpool����ʱȴ����Ҫ��Щ��Ϣ,������Ҫָ����Щ��Ϣ!!
	private String fromActivityId = null; //��Դ��������.
	private String fromActivityName = null; //��Դ��������

	private String transitionId = null; //���ߵ�����
	private String transitionCode = null; //���ߵı���...
	private String transitionName = null; //���ߵ�����
	private String transitionIntercept = null; //���ߵ�������
	private String transitionMailSubject = null; //�����϶�����ʼ�����..
	private String transitionMailContent = null; //�����϶�����ʼ�����..

	private String transitionDealtype = null; //��������

	private String currActivityId = null; //��������..
	private String currActivityType = null; //��������
	private String currActivityCode = null; //���ڱ���..
	private String currActivityName = null; //��������..

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
		sb_text.append("��������:[" + this.deptType + "]\r\n");
		sb_text.append("����Id:[" + this.deptId + "]\r\n");
		sb_text.append("����LinkCode:[" + this.deptLinkCode + "]\r\n");
		return sb_text.toString(); //
	}

}
