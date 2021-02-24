package cn.com.infostrategy.to.workflow.design;

import java.io.Serializable;

import cn.com.infostrategy.to.mdata.RefItemVO;

public class ActivityBean implements Serializable {

	private static final long serialVersionUID = -5307629314729529589L;
	private int id = 0;

	private String activitytype;
	private String code;
	private String wfname;
	private String uiname; //
	private String processid; //
	private Boolean canhalfstart; //是否可以半路启动

	private RefItemVO halfstartrole; //半路启动的角色,多选参照!!
	private RefItemVO halfstartdepttype; //半路启动的机构类型,多选参照  gaofeng

	private Boolean canhalfend; //是否可以半路结束
	private Boolean canselfaddparticipate; //是否可以自己添加参与者,这个很关键，这就意味着可以像邮件系统进行走工作流许多公司的所谓工作流都是这样一种机制

	private Boolean autocommit; //是否自动提交
	private Boolean iscanback; //是否可以回退
	private Boolean isassignapprover; //是否指定审批人!!
	private Boolean isneedmsg; //是否必须输入批语
	private Boolean isselfcycle; //是否自循环,非常重要的功能,可以大大简化流程配置
	private String selfcyclerolemap; //自循环的角色映射！！！

	private Boolean isneedreport; //是否必须打印报表
	private String iscanlookidea; //是否有权限查看审批意见
	private String approvemodel; //会签模式,取值有:抢占/会签/会办子流程
	private Integer approvenumber; //会签数量
	private String childwfcode; //子流程编码,存的是另一个流程的主键!

	private String showparticimode; //显示参与者模式
	private RefItemVO participate_user; //参与的人

	private String participate_corp; //参与的机构范围,即在某个机构范围范围下,返回的是一个机构的Id,然后找出该机构下的所有子机构!比如【福州分行】
	private RefItemVO participate_group; //参与的角色或者组!!

	private RefItemVO participate_dynamic; //动态参与者!!

	private RefItemVO ccto_user; //抄送人员.
	private String ccto_corp; //抄送机构,多选!支持公式与类!单行文本框!!!
	private RefItemVO ccto_role; //抄送角色,多选!

	private String messageformat; //消息定义
	private String messagereceiver; //消息接收者

	private String intercept1 = null; //UI端统一拦截器
	private String intercept2 = null; //BS端统 一拦截器

	private String userdef01 = null;
	private String userdef02 = null;
	private String userdef03 = null;
	private String userdef04 = null;
	private Boolean isHideTransSend = null;//是否隐藏转办
	private String extconfmap = null;//其他扩展参数
	private String smsformat = null;//是否发送短信
	private Boolean sendsms = null;

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

	//会办子流程编码!!
	public String getChildwfcode() {
		return childwfcode;
	}

	//设置会办子流程编码!!
	public void setChildwfcode(String childwfcode) {
		this.childwfcode = childwfcode;
	}

	public String getApprovemodel() {
		return approvemodel;
	}

	public void setApprovemodel(String approvemodel) {
		this.approvemodel = approvemodel;
	}

	public Boolean getAutocommit() {
		return autocommit;
	}

	public void setAutocommit(Boolean autocommit) {
		this.autocommit = autocommit;
	}

	public Integer getApprovenumber() {
		return approvenumber;
	}

	public void setApprovenumber(Integer approvenumber) {
		this.approvenumber = approvenumber;
	}

	public String getMessageformat() {
		return messageformat;
	}

	public void setMessageformat(String messageformat) {
		this.messageformat = messageformat;
	}

	public String getMessagereceiver() {
		return messagereceiver;
	}

	public void setMessagereceiver(String messagereceiver) {
		this.messagereceiver = messagereceiver;
	}

	public RefItemVO getParticipate_user() {
		return participate_user;
	}

	public void setParticipate_user(RefItemVO _participate_user) {
		this.participate_user = _participate_user;
	}

	public RefItemVO getParticipate_group() {
		return participate_group;
	}

	public void setParticipate_group(RefItemVO _participate_group) {
		this.participate_group = _participate_group;
	}

	public String getParticipate_corp() {
		return participate_corp;
	}

	public void setParticipate_corp(String participate_corp) {
		this.participate_corp = participate_corp;
	}

	public RefItemVO getParticipate_dynamic() {
		return participate_dynamic;
	}

	public void setParticipate_dynamic(RefItemVO _participate_dynamic) {
		this.participate_dynamic = _participate_dynamic;
	}

	public RefItemVO getCcto_user() {
		return ccto_user; //抄送人员
	}

	public void setCcto_user(RefItemVO ccto_user) {
		this.ccto_user = ccto_user; //抄送人员
	}

	public String getCcto_corp() {
		return ccto_corp; //抄送机构
	}

	public void setCcto_corp(String ccto_corp) {
		this.ccto_corp = ccto_corp; //抄送机构
	}

	public RefItemVO getCcto_role() {
		return ccto_role; //抄送角色
	}

	public void setCcto_role(RefItemVO ccto_role) {
		this.ccto_role = ccto_role; //抄送角色
	}

	public Boolean getIsassignapprover() {
		return isassignapprover;
	}

	public void setIsassignapprover(Boolean isassignapprover) {
		this.isassignapprover = isassignapprover;
	}

	public String getActivitytype() {
		return activitytype;
	}

	public void setActivitytype(String activitytype) {
		this.activitytype = activitytype;
	}

	public Boolean getIscanback() {
		return iscanback;
	}

	public void setIscanback(Boolean iscanback) {
		this.iscanback = iscanback;
	}

	public Boolean getIsneedmsg() {
		return isneedmsg;
	}

	public void setIsneedmsg(Boolean isneedmsg) {
		this.isneedmsg = isneedmsg;
	}

	public Boolean getCanhalfstart() {
		return canhalfstart;
	}

	public void setCanhalfstart(Boolean canhalfstart) {
		this.canhalfstart = canhalfstart;
	}

	public RefItemVO getHalfstartrole() {
		return halfstartrole;
	}

	public void setHalfstartrole(RefItemVO _halfstartrole) {
		this.halfstartrole = _halfstartrole;
	}

	public RefItemVO getHalfstartdepttype() {
		return halfstartdepttype;
	}

	public void setHalfstartdepttype(RefItemVO halfstartdepttype) {
		this.halfstartdepttype = halfstartdepttype;
	}

	public Boolean getCanhalfend() {
		return canhalfend;
	}

	public void setCanhalfend(Boolean canhalfend) {
		this.canhalfend = canhalfend;
	}

	public Boolean getCanselfaddparticipate() {
		return canselfaddparticipate;
	}

	public void setCanselfaddparticipate(Boolean canselfaddparticipate) {
		this.canselfaddparticipate = canselfaddparticipate;
	}

	public String getShowparticimode() {
		return showparticimode;
	}

	public void setShowparticimode(String showparticimode) {
		this.showparticimode = showparticimode;
	}

	public Boolean getIsneedreport() {
		return isneedreport;
	}

	public void setIsneedreport(Boolean isneedreport) {
		this.isneedreport = isneedreport;
	}

	public String getIscanlookidea() {
		return iscanlookidea;
	}

	public void setIscanlookidea(String iscanlookidea) {
		this.iscanlookidea = iscanlookidea;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
	}

	public Boolean getIsselfcycle() {
		return isselfcycle;
	}

	public void setIsselfcycle(Boolean isselfcycle) {
		this.isselfcycle = isselfcycle;
	}

	public String getSelfcyclerolemap() {
		return selfcyclerolemap;
	}

	public void setSelfcyclerolemap(String selfcyclerolemap) {
		this.selfcyclerolemap = selfcyclerolemap;
	}

	public String getIntercept1() {
		return intercept1;
	}

	public void setIntercept1(String intercept1) {
		this.intercept1 = intercept1;
	}

	public String getIntercept2() {
		return intercept2;
	}

	public void setIntercept2(String intercept2) {
		this.intercept2 = intercept2;
	}

	public String getUserdef01() {
		return userdef01;
	}

	public void setUserdef01(String userdef01) {
		this.userdef01 = userdef01;
	}

	public String getUserdef02() {
		return userdef02;
	}

	public void setUserdef02(String userdef02) {
		this.userdef02 = userdef02;
	}

	public String getUserdef03() {
		return userdef03;
	}

	public void setUserdef03(String userdef03) {
		this.userdef03 = userdef03;
	}

	public String getUserdef04() {
		return userdef04;
	}

	public void setUserdef04(String userdef04) {
		this.userdef04 = userdef04;
	}

	public Boolean getIsHideTransSend() {
		return isHideTransSend;
	}

	public void setIsHideTransSend(Boolean isHideTransSend) {
		this.isHideTransSend = isHideTransSend;
	}

	public String getExtconfmap() {
		return extconfmap;
	}

	public void setExtconfmap(String extconfmap) {
		this.extconfmap = extconfmap;
	}
	public String getSmsformat() {
		return smsformat;
	}

	public void setSmsformat(String smsformat) {
		this.smsformat = smsformat;
	}
	public Boolean getSendsms() {
		return sendsms;
	}

	public void setSendsms(Boolean sendsms) {
		this.sendsms = sendsms;
	}
}
