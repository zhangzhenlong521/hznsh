package cn.com.infostrategy.to.workflow.engine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.ComBoxItemVO;

public class WFParVO implements Serializable {

	private static final long serialVersionUID = 4973482326221989604L;

	private String wfinstanceid = null; //流程实例主键..
	private String parentinstanceid = null; //父流程实例id
	private String rootinstanceid = null; //根流程实例id

	private String billtempletcode = null; //单据模板
	private String billtablename = null; //业务表名
	private String billQueryTablename = null; //业务保存的表名
	private String billpkname = null; //业务表的主键名
	private String billpkvalue = null; //业务表我主键值

	private String wfinstance_createuserid = null; //流程创建者的id
	private String wfinstance_createusercode = null; //流程创建者的code
	private String wfinstance_createusername = null; //流程创建者的name

	private String wfinstance_createactivityid = null; //流程创建环节的id
	private String wfinstance_createactivitycode = null; //流程创建环节的code
	private String wfinstance_createactivityname = null; //流程创建环节的name
	private String wfinstance_createactivitytype = null; //流程创建环节的name

	private String currParticipantUserName = null; //当前处理人,按道理就是登录人员,但因为存在授权后,所以不一定是登录人,所以要取出来用于显示!!

	private String dealpooltask_createuserid = null; //我这条任务的创建者,其实就是谁提交给我的
	private String dealpooltask_createusercode = null; //我这条任务的创建者,其实就是谁提交给我的
	private String dealpooltask_createusername = null; //我这条任务的创建者,其实就是谁提交给我的

	private String dealpooltask_createuser_accruserid = null; //我这条任务的创建者,其实就是谁提交给我的
	private String dealpooltask_createuser_accrusercode = null; //我这条任务的创建者,其实就是谁提交给我的
	private String dealpooltask_createuser_accrusername = null; //我这条任务的创建者,其实就是谁提交给我的

	private String dealpooltask_yjbduserid = null; //意见补登
	private String dealpooltask_yjbdusercode = null; //意见补登
	private String dealpooltask_yjbdusername = null; //意见补登
	
	private boolean dealpooltask_isselfcycleclick = false; //第一次任务
	private String dealpooltask_selfcycle_fromrolecode = null; //自己循环时,从哪个角色来!
	private String dealpooltask_selfcycle_fromrolename = null; //自己循环时,如果自循环绑定绑定了另一个角色映射的图,则计算出
	private String dealpooltask_selfcycle_torolecode = null; //去哪个角色! 
	private String dealpooltask_selfcycle_torolename = null; //自己循环时,如果自循环绑定绑定了另一个角色映射的图,则计算出去向的角色名称！！

	//第二次点击时记录的自循环的来源角色与目标角色!!!
	private boolean secondIsSelfcycleclick = false; //记录第二次点击时,是否是点击的自循环中的按钮!!
	private String secondSelfcycle_fromrolecode = null; //第二次点击时的自循环去的角色编码!!  即在创建新的任务时,新任务中的from角色是第一次任务中的torole,
	private String secondSelfcycle_fromrolename = null; //第二次点击时的自循环去的角色名称!!
	private String secondSelfcycle_torolecode = null; //第二次点击时的自循环去的角色编码!!  即在创建新的任务时,新任务中的from角色是第一次任务中的torole,
	private String secondSelfcycle_torolename = null; //第二次点击时的自循环去的角色名称!!

	private String processid = null; //流程定义主键!
	private String dealpoolid = null; //待处理任务池中记录的主键
	private String dealpoolidCreatetime = null; //创建时间

	private String fromactivity = null; //From环节的主键
	private String fromactivityCode = null; //From环节的编码
	private String fromactivityName = null; //From环节的名称
	private String fromactivityType = null; //From环节的类型

	private String curractivity = null; //当前环节的主键
	private String curractivityCode = null; //当前环节的编码
	private String curractivityName = null; //当前环节的名称
	private String curractivityBLDeptName = null; //当前环节所属矩阵的名称!
	private String curractivityType = null; //当前环节的类型
	private Boolean curractivityCanhalfend = false; //是否可以半路结束
	private Boolean curractivityCanselfaddparticipate = false; //是否可以自己添加参与者
	private Boolean curractivityIsSelfCycle = false; //当前环节是否自循环！！
	private String curractivitySelfCycleRoleMap = null; //当前环节的角色映射！！
	private String curractivityCheckUserPanel = null; //当前环节的选择用户的弹出框，即在选择用户下拉框的右边有个按钮叫"CheckUser",点击该按钮后会弹出一个对话框，该参数就是批定该对话框的路径名!
	private String intercept1 = null; //拦截器1
	private String intercept2 = null; //拦截器2

	private String batchno = null; //批号

	private boolean isStartStep = false; //是否是启动步骤!即有时写拦截器时需要指明当前任务是否是启动的第一步！

	//如果是终结者的话,则需要输入下列参数!
	private boolean isprocessed = false; //是否是终结者
	private String approveModel = null; //审核模式 
	private DealActivityVO[] dealActivityVOs = null; //所有待处理的环节.
	private DealTaskVO[] commitTaskVOs = null; //提交时的任务!!!!关键,第一次取得任务时,任务是绑定DealActivityVO的,而提交时,则是绑定该类的,即取得任务时是分环节的,而提交时是不分环节的!!!其实它是从DealActivityVO中DealTaskVO拷贝过来的!!

	private boolean isassignapprover = false; //是否需要指定审批人

	private boolean isneedmsg = false; //是否必须输入批语

	private String prioritylevel = null; //紧急程度
	private String dealtimelimit = null; //最后处理期限
	private String message = null; //批语
	private String messagefile = null; //批语附件!!!
	private String ifSendEmail = null;//是否发送邮件

	private int selectedRow = -1; //选中的行,即选中了某一个具体的用户生成任务.
	private int selectedReasonCode = -1; //选中的Reasoncode

	private ComBoxItemVO[] reasonCodeComBoxItemVOs = null; //原因..

	private boolean isRejectedDirUp = false; //
	
	//会签-不同部门会签,同部门抢占问题追加 【杨科/2013-04-25】
	private String createbyid = null; //是哪一个处理记录处理的
	private String participant_userdept = null; //提交者机构

	public String getDealpoolid() {
		return dealpoolid;
	}

	public void setDealpoolid(String dealpoolid) {
		this.dealpoolid = dealpoolid;
	}

	public boolean isIsprocessed() {
		return isprocessed;
	}

	public void setIsprocessed(boolean isprocessed) {
		this.isprocessed = isprocessed; //
	}

	public DealActivityVO[] getDealActivityVOs() {
		return dealActivityVOs;
	}

	public void setDealActivityVOs(DealActivityVO[] dealActivityVOs) {
		this.dealActivityVOs = dealActivityVOs;
	}

	public boolean isIsassignapprover() {
		return isassignapprover;
	}

	public void setIsassignapprover(boolean isassignapprover) {
		this.isassignapprover = isassignapprover;
	}

	public boolean isIsneedmsg() {
		return isneedmsg;
	}

	public void setIsneedmsg(boolean isneedmsg) {
		this.isneedmsg = isneedmsg;
	}

	public String getWfinstanceid() {
		return wfinstanceid;
	}

	public void setWfinstanceid(String wfinstanceid) {
		this.wfinstanceid = wfinstanceid;
	}

	/**
	 * 父流程实例id
	 * @return
	 */
	public String getParentinstanceid() {
		return parentinstanceid;
	}

	public void setParentinstanceid(String parentinstanceid) {
		this.parentinstanceid = parentinstanceid;
	}

	/**
	 * 根流程实例id
	 * @return
	 */
	public String getRootinstanceid() {
		return rootinstanceid;
	}

	public void setRootinstanceid(String rootinstanceid) {
		this.rootinstanceid = rootinstanceid;
	}

	public String getBilltempletcode() {
		return billtempletcode;
	}

	public void setBilltempletcode(String billtempletcode) {
		this.billtempletcode = billtempletcode;
	}

	public String getBilltablename() {
		return billtablename;
	}

	public void setBilltablename(String billtablename) {
		this.billtablename = billtablename;
	}

	public String getBillQueryTablename() {
		return billQueryTablename;
	}

	public void setBillQueryTablename(String _billQueryTabName) {
		this.billQueryTablename = _billQueryTabName;
	}

	public String getBillpkname() {
		return billpkname;
	}

	public void setBillpkname(String billpkname) {
		this.billpkname = billpkname;
	}

	public String getBillpkvalue() {
		return billpkvalue;
	}

	public void setBillpkvalue(String billpkvalue) {
		this.billpkvalue = billpkvalue;
	}

	public String getWfinstance_createuserid() {
		return wfinstance_createuserid;
	}

	public void setWfinstance_createuserid(String wfinstance_createuserid) {
		this.wfinstance_createuserid = wfinstance_createuserid;
	}

	public String getWfinstance_createusercode() {
		return wfinstance_createusercode;
	}

	public void setWfinstance_createusercode(String wfinstance_createusercode) {
		this.wfinstance_createusercode = wfinstance_createusercode;
	}

	public String getWfinstance_createusername() {
		return wfinstance_createusername;
	}

	public void setWfinstance_createusername(String wfinstance_createusername) {
		this.wfinstance_createusername = wfinstance_createusername;
	}

	public String getWfinstance_createactivityid() {
		return wfinstance_createactivityid;
	}

	public void setWfinstance_createactivityid(String wfinstance_createactivityid) {
		this.wfinstance_createactivityid = wfinstance_createactivityid;
	}

	public String getWfinstance_createactivitycode() {
		return wfinstance_createactivitycode;
	}

	public void setWfinstance_createactivitycode(String wfinstance_createactivitycode) {
		this.wfinstance_createactivitycode = wfinstance_createactivitycode;
	}

	public String getWfinstance_createactivityname() {
		return wfinstance_createactivityname;
	}

	public void setWfinstance_createactivityname(String wfinstance_createactivityname) {
		this.wfinstance_createactivityname = wfinstance_createactivityname;
	}

	public String getWfinstance_createactivitytype() {
		return wfinstance_createactivitytype;
	}

	public void setWfinstance_createactivitytype(String wfinstance_createactivitytype) {
		this.wfinstance_createactivitytype = wfinstance_createactivitytype;
	}

	public String getCurrParticipantUserName() {
		return currParticipantUserName;
	}

	public void setCurrParticipantUserName(String currParticipantUserName) {
		this.currParticipantUserName = currParticipantUserName;
	}

	public String getDealpooltask_createuserid() {
		return dealpooltask_createuserid;
	}

	public void setDealpooltask_createuserid(String dealpooltask_createuserid) {
		this.dealpooltask_createuserid = dealpooltask_createuserid;
	}

	public String getDealpooltask_createusercode() {
		return dealpooltask_createusercode;
	}

	public void setDealpooltask_createusercode(String dealpooltask_createusercode) {
		this.dealpooltask_createusercode = dealpooltask_createusercode;
	}

	public String getDealpooltask_createusername() {
		return dealpooltask_createusername;
	}

	public void setDealpooltask_createusername(String dealpooltask_createusername) {
		this.dealpooltask_createusername = dealpooltask_createusername;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMessagefile() {
		return messagefile;
	}

	public void setMessagefile(String messagefile) {
		this.messagefile = messagefile;
	}

	public int getSelectedRow() {
		return selectedRow;
	}

	public void setSelectedRow(int selectedRow) {
		this.selectedRow = selectedRow;
	}

	public String getCurractivity() {
		return curractivity;
	}

	public void setCurractivity(String curractivity) {
		this.curractivity = curractivity;
	}

	public String getFromactivity() {
		return fromactivity;
	}

	public void setFromactivity(String fromactivity) {
		this.fromactivity = fromactivity;
	}

	public String getProcessid() {
		return processid;
	}

	public void setProcessid(String processid) {
		this.processid = processid;
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

	public String getBatchno() {
		return batchno;
	}

	public void setBatchno(String batchno) {
		this.batchno = batchno;
	}

	public String getCurractivityCheckUserPanel() {
		return curractivityCheckUserPanel;
	}

	public void setCurractivityCheckUserPanel(String _curractivityCheckUserPanel) {
		this.curractivityCheckUserPanel = _curractivityCheckUserPanel;
	}

	public ComBoxItemVO[] getReasonCodeComBoxItemVOs() {
		return reasonCodeComBoxItemVOs;
	}

	public void setReasonCodeComBoxItemVOs(ComBoxItemVO[] reasonCodeComBoxItemVOs) {
		this.reasonCodeComBoxItemVOs = reasonCodeComBoxItemVOs;
	}

	public int getSelectedReasonCode() {
		return selectedReasonCode;
	}

	public void setSelectedReasonCode(int selectedReasonCode) {
		this.selectedReasonCode = selectedReasonCode;
	}

	public String getFromactivityCode() {
		return fromactivityCode;
	}

	public void setFromactivityCode(String fromactivityCode) {
		this.fromactivityCode = fromactivityCode;
	}

	public String getCurractivityCode() {
		return curractivityCode;
	}

	public String getFromactivityName() {
		return fromactivityName;
	}

	public void setFromactivityName(String fromactivityName) {
		this.fromactivityName = fromactivityName;
	}

	public String getFromactivityType() {
		return fromactivityType;
	}

	public void setFromactivityType(String fromactivityType) {
		this.fromactivityType = fromactivityType;
	}

	public void setCurractivityCode(String curractivityCode) {
		this.curractivityCode = curractivityCode;
	}

	public String getCurractivityName() {
		return curractivityName;
	}

	public void setCurractivityName(String curractivityName) {
		this.curractivityName = curractivityName;
	}

	public String getCurractivityBLDeptName() {
		return curractivityBLDeptName;
	}

	public void setCurractivityBLDeptName(String curractivityBLDeptName) {
		this.curractivityBLDeptName = curractivityBLDeptName;
	}

	public String getCurractivityType() {
		return curractivityType;
	}

	public void setCurractivityType(String curractivityType) {
		this.curractivityType = curractivityType;
	}

	public Boolean getCurractivityCanhalfend() {
		return curractivityCanhalfend;
	}

	public void setCurractivityCanhalfend(Boolean curractivityCanhalfend) {
		this.curractivityCanhalfend = curractivityCanhalfend;
	}

	public Boolean getCurractivityCanselfaddparticipate() {
		return curractivityCanselfaddparticipate;
	}

	public void setCurractivityCanselfaddparticipate(Boolean curractivityCanselfaddparticipate) {
		this.curractivityCanselfaddparticipate = curractivityCanselfaddparticipate;
	}

	public BillVO deepClone() {
		try {
			ByteArrayOutputStream buf = new ByteArrayOutputStream();
			ObjectOutputStream out = new ObjectOutputStream(buf);
			out.writeObject(this); //
			ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buf.toByteArray()));
			return (BillVO) in.readObject();
		} catch (Exception ex) {
			ex.printStackTrace(); //
			return null;
		}
	}

	public DealTaskVO[] getCommitTaskVOs() {
		return commitTaskVOs;
	}

	public void setCommitTaskVOs(DealTaskVO[] commitTaskVOs) {
		this.commitTaskVOs = commitTaskVOs;
	}

	public String getDealpoolidCreatetime() {
		return dealpoolidCreatetime;
	}

	public void setDealpoolidCreatetime(String dealpoolidcreatetime) {
		this.dealpoolidCreatetime = dealpoolidcreatetime;
	}

	public String getPrioritylevel() {
		return prioritylevel;
	}

	public void setPrioritylevel(String prioritylevel) {
		this.prioritylevel = prioritylevel;
	}

	public String getDealtimelimit() {
		return dealtimelimit;
	}

	public void setDealtimelimit(String dealtimelimit) {
		this.dealtimelimit = dealtimelimit;
	}

	public boolean isRejectedDirUp() {
		return isRejectedDirUp;
	}

	public void setRejectedDirUp(boolean isRejectedDirUp) {
		this.isRejectedDirUp = isRejectedDirUp;
	}

	public String getIfSendEmail() {
		return ifSendEmail;
	}

	public void setIfSendEmail(String ifSendEmail) {
		this.ifSendEmail = ifSendEmail;
	}

	public String getDealpooltask_createuser_accruserid() {
		return dealpooltask_createuser_accruserid;
	}

	public void setDealpooltask_createuser_accruserid(String dealpooltask_createuser_accruserid) {
		this.dealpooltask_createuser_accruserid = dealpooltask_createuser_accruserid;
	}

	public String getDealpooltask_createuser_accrusercode() {
		return dealpooltask_createuser_accrusercode;
	}

	public void setDealpooltask_createuser_accrusercode(String dealpooltask_createuser_accrusercode) {
		this.dealpooltask_createuser_accrusercode = dealpooltask_createuser_accrusercode;
	}

	public String getDealpooltask_createuser_accrusername() {
		return dealpooltask_createuser_accrusername;
	}

	public void setDealpooltask_createuser_accrusername(String dealpooltask_createuser_accrusername) {
		this.dealpooltask_createuser_accrusername = dealpooltask_createuser_accrusername;
	}

	public void setCurractivityIsSelfCycle(Boolean _selfCycle) {
		this.curractivityIsSelfCycle = _selfCycle; //
	}

	//自循环映射
	public void setCurractivitySelfCycleRoleMap(String _map) {
		this.curractivitySelfCycleRoleMap = _map; //
	}

	public Boolean getCurractivityIsSelfCycle() {
		return curractivityIsSelfCycle;
	}

	public String getCurractivitySelfCycleRoleMap() {
		return curractivitySelfCycleRoleMap;
	}

	public boolean isStartStep() {
		return isStartStep;
	}

	public void setStartStep(boolean isStartStep) {
		this.isStartStep = isStartStep;
	}

	public boolean isDealpooltask_isselfcycleclick() {
		return dealpooltask_isselfcycleclick;
	}

	public void setDealpooltask_isselfcycleclick(boolean dealpooltask_isselfcycleclick) {
		this.dealpooltask_isselfcycleclick = dealpooltask_isselfcycleclick;
	}

	public String getDealpooltask_selfcycle_fromrolecode() {
		return dealpooltask_selfcycle_fromrolecode;
	}

	public void setDealpooltask_selfcycle_fromrolecode(String dealpooltask_selfcycle_fromrolecode) {
		this.dealpooltask_selfcycle_fromrolecode = dealpooltask_selfcycle_fromrolecode;
	}

	public String getDealpooltask_selfcycle_fromrolename() {
		return dealpooltask_selfcycle_fromrolename;
	}

	public void setDealpooltask_selfcycle_fromrolename(String dealpooltask_selfcycle_fromrolename) {
		this.dealpooltask_selfcycle_fromrolename = dealpooltask_selfcycle_fromrolename;
	}

	public String getDealpooltask_selfcycle_torolecode() {
		return dealpooltask_selfcycle_torolecode;
	}

	public void setDealpooltask_selfcycle_torolecode(String dealpooltask_selfcycle_torolecode) {
		this.dealpooltask_selfcycle_torolecode = dealpooltask_selfcycle_torolecode;
	}

	public String getDealpooltask_selfcycle_torolename() {
		return dealpooltask_selfcycle_torolename;
	}

	public void setDealpooltask_selfcycle_torolename(String dealpooltask_selfcycle_torolename) {
		this.dealpooltask_selfcycle_torolename = dealpooltask_selfcycle_torolename;
	}

	public boolean isSecondIsSelfcycleclick() {
		return secondIsSelfcycleclick;
	}

	public void setSecondIsSelfcycleclick(boolean secondIsSelfcycleclick) {
		this.secondIsSelfcycleclick = secondIsSelfcycleclick;
	}

	public String getSecondSelfcycle_torolecode() {
		return secondSelfcycle_torolecode;
	}

	public void setSecondSelfcycle_torolecode(String secondSelfcycle_torolecode) {
		this.secondSelfcycle_torolecode = secondSelfcycle_torolecode;
	}

	public String getSecondSelfcycle_torolename() {
		return secondSelfcycle_torolename;
	}

	public void setSecondSelfcycle_torolename(String secondSelfcycle_torolename) {
		this.secondSelfcycle_torolename = secondSelfcycle_torolename;
	}

	public String getSecondSelfcycle_fromrolecode() {
		return secondSelfcycle_fromrolecode;
	}

	public void setSecondSelfcycle_fromrolecode(String secondSelfcycle_fromrolecode) {
		this.secondSelfcycle_fromrolecode = secondSelfcycle_fromrolecode;
	}

	public String getSecondSelfcycle_fromrolename() {
		return secondSelfcycle_fromrolename;
	}

	public void setSecondSelfcycle_fromrolename(String secondSelfcycle_fromrolename) {
		this.secondSelfcycle_fromrolename = secondSelfcycle_fromrolename;
	}

	public String getDealpooltask_yjbduserid() {
		return dealpooltask_yjbduserid;
	}

	public void setDealpooltask_yjbduserid(String dealpooltask_yjbduserid) {
		this.dealpooltask_yjbduserid = dealpooltask_yjbduserid;
	}

	public String getDealpooltask_yjbdusercode() {
		return dealpooltask_yjbdusercode;
	}

	public void setDealpooltask_yjbdusercode(String dealpooltask_yjbdusercode) {
		this.dealpooltask_yjbdusercode = dealpooltask_yjbdusercode;
	}

	public String getDealpooltask_yjbdusername() {
		return dealpooltask_yjbdusername;
	}

	public void setDealpooltask_yjbdusername(String dealpooltask_yjbdusername) {
		this.dealpooltask_yjbdusername = dealpooltask_yjbdusername;
	}

	public String getApproveModel() {
		return approveModel;
	}

	public void setApproveModel(String approveModel) {
		this.approveModel = approveModel;
	}

	public String getCreatebyid() {
		return createbyid;
	}

	public void setCreatebyid(String createbyid) {
		this.createbyid = createbyid;
	}

	public String getParticipant_userdept() {
		return participant_userdept;
	}

	public void setParticipant_userdept(String participant_userdept) {
		this.participant_userdept = participant_userdept;
	}

}
