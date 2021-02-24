package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 取得某一个连线的数据对象,即转移
 * @author xch
 *
 */
public class NextTransitionVO implements Serializable {

	private static final long serialVersionUID = -1790203482126545084L;

	private int sortIndex = 1; //强行排序的字段,因为自循环需要永远把自已在第一位!

	private String fromactivityId = null;
	private String fromactivityCode = null;
	private String fromactivityWFName = null;
	private String fromactivityUIName = null;
	private String fromactivityApprovemodel = null;

	private String processid = null; //属于什么流程id
	private String id = null; //连线的主键
	private String code = null; //连线的编码
	private String wfname = null; //连线的名称
	private String uiname = null; //连线的名称
	private String mailSubject = null; //邮件主题.中间有宏代码..
	private String mailContent = null; //邮件内容.中间有宏代码..
	private String dealtype = null; //
	private String intercept = null; //连线上的拦截器,很关键!!

	private String toactivityId = null;
	private String toactivityType = null; //环节类型
	private String toactivityCode = null;
	private String toactivityWFName = null;
	private String toactivityUIName = null;
	private String toactivityBelongDeptGroupName = null; //目标环节的所属部门矩阵的名称!有可能为空!!
	private String toactivityApprovemodel = null; //目标环节的审批模式
	private String toactivityShowparticimode = null; //显示参与者的方式
	private String toactivityParticipate_user = null; //静态参与人
	private String toactivityParticipate_corp = null; //参与的机构
	private String toactivityParticipate_group = null; //参与的角色
	private String toactivityCanselfaddparticipate = null;
	
	private boolean toactivityIsSelfCycle = false; //
	private String toactivitySelfcyclerolemap = null; //

	private String toactivityParticipate_dynamic = null; //动态参与者
	private String toactivitywnparam = null;//万能参数
	private String toactivityCcto_user = null; //目标环节上定义的抄送的人员
	private String toactivityCcto_corp = null; //目标环节上定义的抄送的机构
	private String toactivityCcto_role = null; //目标环节上定义的抄送的角色

	private String reasoncodesql = null; //输入原因的SQL

	private HashMap extConfMap = null; //
	
	private boolean passcondition = false; //

	public String getFromactivityId() {
		return fromactivityId;
	}

	public void setFromactivityId(String fromactivityId) {
		this.fromactivityId = fromactivityId;
	}

	public String getFromactivityCode() {
		return fromactivityCode;
	}

	public void setFromactivityCode(String fromactivityCode) {
		this.fromactivityCode = fromactivityCode;
	}

	public String getFromactivityWFName() {
		return fromactivityWFName;
	}

	public void setFromactivityWFName(String fromactivityWFName) {
		this.fromactivityWFName = fromactivityWFName;
	}

	public String getFromactivityUIName() {
		return fromactivityUIName;
	}

	public void setFromactivityUIName(String fromactivityUIName) {
		this.fromactivityUIName = fromactivityUIName;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
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

	public String getToactivityId() {
		return toactivityId;
	}

	public void setToactivityId(String toactivityId) {
		this.toactivityId = toactivityId;
	}

	public String getToactivityCode() {
		return toactivityCode;
	}

	public void setToactivityCode(String toactivityCode) {
		this.toactivityCode = toactivityCode;
	}

	public String getToactivityWFName() {
		return toactivityWFName;
	}

	public void setToactivityWFName(String toactivityWFName) {
		this.toactivityWFName = toactivityWFName;
	}

	public String getToactivityUIName() {
		return toactivityUIName;
	}

	public void setToactivityUIName(String toactivityUIName) {
		this.toactivityUIName = toactivityUIName;
	}

	public String getToactivityBelongDeptGroupName() {
		return toactivityBelongDeptGroupName;
	}

	public void setToactivityBelongDeptGroupName(String toactivityBelongDeptGroupName) {
		this.toactivityBelongDeptGroupName = toactivityBelongDeptGroupName;
	}

	public String getToactivityParticipate_user() {
		return toactivityParticipate_user;
	}

	public void setToactivityParticipate_user(String toactivityParticipate_user) {
		this.toactivityParticipate_user = toactivityParticipate_user;
	}

	public String getToactivityParticipate_corp() {
		return toactivityParticipate_corp;
	}

	public void setToactivityParticipate_corp(String toactivityParticipate_corp) {
		this.toactivityParticipate_corp = toactivityParticipate_corp;
	}

	public String getToactivityParticipate_group() {
		return toactivityParticipate_group;
	}

	public void setToactivityParticipate_group(String toactivityParticipate_group) {
		this.toactivityParticipate_group = toactivityParticipate_group;
	}

	public String getToactivityParticipate_dynamic() {
		return toactivityParticipate_dynamic;
	}

	public void setToactivityParticipate_dynamic(String toactivityParticipate_dynamic) {
		this.toactivityParticipate_dynamic = toactivityParticipate_dynamic;
	}

	public String getToactivityCcto_user() {
		return toactivityCcto_user; //目标环节上定义的抄送的人员
	}

	public void setToactivityCcto_user(String toactivityCcto_user) {
		this.toactivityCcto_user = toactivityCcto_user;
	}

	public String getToactivityCcto_corp() {
		return toactivityCcto_corp;
	}

	public String getToactivityCcto_role() {
		return toactivityCcto_role;
	}

	public void setToactivityCcto_corp(String toactivityCcto_corp) {
		this.toactivityCcto_corp = toactivityCcto_corp;
	}

	public void setToactivityCcto_role(String toactivityCcto_role) {
		this.toactivityCcto_role = toactivityCcto_role;
	}

	public String getDealtype() {
		return dealtype;
	}

	public void setDealtype(String dealtype) {
		this.dealtype = dealtype;
	}

	public String getReasoncodesql() {
		return reasoncodesql;
	}

	public void setReasoncodesql(String reasoncodesql) {
		this.reasoncodesql = reasoncodesql;
	}

	public String getToactivityType() {
		return toactivityType;
	}

	public void setToactivityType(String toactivityType) {
		this.toactivityType = toactivityType;
	}

	public String getMailContent() {
		return mailContent;
	}

	public void setMailContent(String mailContent) {
		this.mailContent = mailContent;
	}

	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getFromactivityApprovemodel() {
		return fromactivityApprovemodel;
	}

	public void setFromactivityApprovemodel(String fromactivityApprovemodel) {
		this.fromactivityApprovemodel = fromactivityApprovemodel;
	}

	public String getToactivityApprovemodel() {
		return toactivityApprovemodel;
	}

	public void setToactivityApprovemodel(String toactivityApprovemodel) {
		this.toactivityApprovemodel = toactivityApprovemodel;
	}

	public String getIntercept() {
		return intercept;
	}

	public void setIntercept(String intercept) {
		this.intercept = intercept;
	}

	public String getToactivityShowparticimode() {
		return toactivityShowparticimode;
	}

	public void setToactivityShowparticimode(String toactivityShowparticimode) {
		this.toactivityShowparticimode = toactivityShowparticimode;
	}

	public boolean isPasscondition() {
		return passcondition;
	}

	public void setPasscondition(boolean passcondition) {
		this.passcondition = passcondition;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public String getToactivitywnparam() {
		return toactivitywnparam;
	}

	public void setToactivitywnparam(String toactivitywnparam) {
		this.toactivitywnparam = toactivitywnparam;
	}

	public HashMap getExtConfMap() {
		return extConfMap;
	}

	public void setExtConfMap(HashMap extConfMap) {
		this.extConfMap = extConfMap;
	}

	public void setToactivityIsSelfCycle(Boolean _toactivityIsSelfCycle) {
		toactivityIsSelfCycle = _toactivityIsSelfCycle; //
	}

	public boolean getToactivityIsSelfCycle() {
		return toactivityIsSelfCycle; //
	}

	public void setToactivitySelfCycleMap(String _toactivitySelfcyclerolemap) {
		toactivitySelfcyclerolemap = _toactivitySelfcyclerolemap; //
	}

	public String getToactivitySelfCycleMap() {
		return toactivitySelfcyclerolemap; //
	}

	public String getToactivityCanselfaddparticipate() {
		return toactivityCanselfaddparticipate;
	}

	public void setToactivityCanselfaddparticipate(String toactivityCanselfaddparticipate) {
		this.toactivityCanselfaddparticipate = toactivityCanselfaddparticipate;
	}

}
