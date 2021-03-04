package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

public class TransitionBean implements Serializable {

	private static final long serialVersionUID = 6258580336400486781L;
	private int id;
	private String code;
	private String wfname;
	private String uiname;
	private String dealtype; //处理类型,有SUBMIT与REJECT之分,即提交与拒绝之分!
	private String mailsubject; //邮件主题
	private String mailcontent; //邮件内容
	private Integer fromactivity;
	private Integer toactivity;
	private String conditions;
	private String reasoncodesql = null; //
	private String intercept = null; //拦截器

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getWfname() {
		return wfname;
	}

	public void setWfname(String wfname) {
		this.wfname = wfname;
	}

	public String getUiname() {
		return uiname;
	}

	public void setUiname(String uiname) {
		this.uiname = uiname;
	}

	public Integer getFromactivity() {
		return fromactivity;
	}

	public void setFromactivity(Integer fromactivity) {
		this.fromactivity = fromactivity;
	}

	public Integer getToactivity() {
		return toactivity;
	}

	public void setToactivity(Integer toactivity) {
		this.toactivity = toactivity;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String condition) {
		this.conditions = condition;
	}

	public String getDealtype() {
		return dealtype;
	}

	public void setDealtype(String dealtype) {
		this.dealtype = dealtype;
	}

	////..
	public String getReasoncodesql() {
		return reasoncodesql;
	}

	public void setReasoncodesql(String reasoncodesql) {
		this.reasoncodesql = reasoncodesql;
	}

	public String getMailcontent() {
		return mailcontent;
	}

	public void setMailcontent(String mailcontent) {
		this.mailcontent = mailcontent;
	}

	public String getMailsubject() {
		return mailsubject;
	}

	public void setMailsubject(String mailsubject) {
		this.mailsubject = mailsubject;
	}

	public String getIntercept() {
		return intercept;
	}

	public void setIntercept(String intercept) {
		this.intercept = intercept;
	}

}
