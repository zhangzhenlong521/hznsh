package cn.com.infostrategy.to.workflow.engine;

import java.io.Serializable;
import java.util.HashMap;

import cn.com.infostrategy.to.common.TBUtil;

/**
 * 等处理环节对象,它与待处理任务一起绑定在WFPARVO中..
 * 一个环节包括多个任务,也包括多个部门!!!
 * 以前的环节,只有目标环节,而将来源环节记录在任务中!
 * 后来发现还是需要记录来源环节与来源连线,因为在自由添加人员时需要这些信息!!
 * @author xch
 *
 */
public class DealActivityVO implements Serializable {

	private static final long serialVersionUID = -5335224239603046651L;

	private int sortIndex = 1; //强行排序的字段,因为自循环需要永远把自已在第一位!

	private String fromActivityId = null; //从哪个环节id过来的!
	private String fromActivityCode = null; //从哪个环节Code过来的
	private String fromActivityName = null; //从哪个环节Name过来的

	private String fromTransitionId = null; //连线的id
	private String fromTransitionCode = null; //连线的id
	private String fromTransitionName = null; //连线的名称
	private String fromTransitionIntercept = null; //线的处理拦截器
	private String fromTransitionMailsubject = null; //连线上的邮件主题
	private String fromTransitionMailcontent = null; //连线上的主题内容
	private String fromtransitiontype = null; //来源连线类型
	private HashMap fromtransitionExtConfMap = null; //

	private String activityId = null; //接收/当前环节id
	private String activityCode = null; //接收/当前环节Code
	private String activityName = null; //接收/当前环节Name
	private String activityBelongDeptGroupName = null; //接收/当前环节的所属部门组的名称!!
	private String activityType = null; //接收/当前环节类型
	private String canselfaddparticipate = null;
	private boolean activityIselfcycle = false; //
	private String activitySelfcyclerolemap = null; //

	private String approvemodel = null; //审批模式,抢占/会签
	private String showparticimode = null; //显示参与者的模式
	private String participate_user = null; //参与的人
	private String participate_group = null; //参与的组
	private String participate_corp = null; //参与的机构
	private String participate_dynamic = null; //动态参与者

	private String ccto_user = null; //抄送人员,当前环节定义的抄送至的人员
	private String ccto_corp = null; //抄送机构,当前环节定义的抄送至的机构
	private String ccto_role = null; //抄送角色

	private String participtMsg = ""; //
	private HashMap wnParam = null;//万能参数，对应extconfmap
	private DealTaskVO[] dealTaskVOs = null; //参与的任务(人员)

	public HashMap getWnParam() {
		return wnParam;
	}

	public void setWnParam(HashMap wnParam) {
		this.wnParam = wnParam;
	}

	public String getFromActivityId() {
		return fromActivityId;
	}

	public void setFromActivityId(String fromActivityId) {
		this.fromActivityId = fromActivityId;
	}

	public String getFromActivityCode() {
		return fromActivityCode;
	}

	public void setFromActivityCode(String fromActivityCode) {
		this.fromActivityCode = fromActivityCode;
	}

	public String getFromActivityName() {
		return fromActivityName;
	}

	public void setFromActivityName(String fromActivityName) {
		this.fromActivityName = fromActivityName;
	}

	public String getFromTransitionId() {
		return fromTransitionId;
	}

	public void setFromTransitionId(String fromTransitionId) {
		this.fromTransitionId = fromTransitionId;
	}

	public String getFromTransitionCode() {
		return fromTransitionCode;
	}

	public void setFromTransitionCode(String fromTransitionCode) {
		this.fromTransitionCode = fromTransitionCode;
	}

	public String getFromTransitionName() {
		return fromTransitionName;
	}

	public void setFromTransitionName(String fromTransitionName) {
		this.fromTransitionName = fromTransitionName;
	}

	public String getFromTransitionIntercept() {
		return fromTransitionIntercept;
	}

	public void setFromTransitionIntercept(String fromTransitionIntercept) {
		this.fromTransitionIntercept = fromTransitionIntercept;
	}

	public String getFromTransitionMailsubject() {
		return fromTransitionMailsubject;
	}

	public void setFromTransitionMailsubject(String fromTransitionMailsubject) {
		this.fromTransitionMailsubject = fromTransitionMailsubject;
	}

	public String getFromTransitionMailcontent() {
		return fromTransitionMailcontent;
	}

	public void setFromTransitionMailcontent(String fromTransitionMailcontent) {
		this.fromTransitionMailcontent = fromTransitionMailcontent;
	}

	public String getActivityId() {
		return activityId;
	}

	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityBelongDeptGroupName() {
		return activityBelongDeptGroupName;
	}

	public void setActivityBelongDeptGroupName(String activityBelongDeptGroupName) {
		this.activityBelongDeptGroupName = activityBelongDeptGroupName;
	}

	public String getParticipate_user() {
		return participate_user;
	}

	public void setParticipate_user(String participate_user) {
		this.participate_user = participate_user;
	}

	public String getParticipate_group() {
		return participate_group;
	}

	public void setParticipate_group(String participate_group) {
		this.participate_group = participate_group;
	}

	public String getParticipate_corp() {
		return participate_corp;
	}

	public void setParticipate_corp(String participate_corp) {
		this.participate_corp = participate_corp;
	}

	public String getParticipate_dynamic() {
		return participate_dynamic;
	}

	public void setParticipate_dynamic(String participate_dynamic) {
		this.participate_dynamic = participate_dynamic;
	}

	public String getCcto_user() {
		return ccto_user;
	}

	public void setCcto_user(String ccto_user) {
		this.ccto_user = ccto_user;
	}

	public String getCcto_corp() {
		return ccto_corp;
	}

	public void setCcto_corp(String ccto_corp) {
		this.ccto_corp = ccto_corp;
	}

	public String getCcto_role() {
		return ccto_role;
	}

	public void setCcto_role(String ccto_role) {
		this.ccto_role = ccto_role;
	}

	public String getParticiptMsg() {
		return participtMsg;
	}

	public void setParticiptMsg(String _participtMsg) {
		this.participtMsg = _participtMsg;
	}

	public String getApprovemodel() {
		return approvemodel;
	}

	public void setApprovemodel(String approvemodel) {
		this.approvemodel = approvemodel;
	}

	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getShowparticimode() {
		return showparticimode;
	}

	public void setShowparticimode(String showparticimode) {
		this.showparticimode = showparticimode;
	}

	public String getFromtransitiontype() {
		return fromtransitiontype;
	}

	public void setFromtransitiontype(String fromtransitiontype) {
		this.fromtransitiontype = fromtransitiontype;
	}

	//连线的扩展属性!
	public void setFromtransitionExtConfMap(HashMap _extConfMap) {
		fromtransitionExtConfMap = _extConfMap; //
	}

	public HashMap getFromtransitionExtConfMap() {
		return fromtransitionExtConfMap;
	}

	public DealTaskVO[] getDealTaskVOs() {
		return dealTaskVOs;
	}

	public void setDealTaskVOs(DealTaskVO[] dealTaskVOs) {
		this.dealTaskVOs = dealTaskVOs;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

	public boolean isActivityIselfcycle() {
		return activityIselfcycle;
	}

	public void setActivityIselfcycle(boolean activityIselfcycle) {
		this.activityIselfcycle = activityIselfcycle;
	}

	public String getActivitySelfcyclerolemap() {
		return activitySelfcyclerolemap;
	}

	public void setActivitySelfcyclerolemap(String activitySelfcyclerolemap) {
		this.activitySelfcyclerolemap = activitySelfcyclerolemap;
	}

	/**
	 * 取得某一个环节的显示名称,由于考虑到有时需要显示组的名称! 而且
	 * @param _isHtml
	 * @return
	 */
	public String getCurrActivityName(boolean _isHtml) {
		TBUtil tbUtil = new TBUtil(); //
		String str_ActName = this.getActivityName(); //环节名称
		String str_tabTitle = null; //
		if (tbUtil.getSysOptionBooleanValue("提交界面是否显示环节所属组的名称", true)) {
			String str_ActBLGroup = this.getActivityBelongDeptGroupName(); //环节所属组的名称!!!
			if (str_ActBLGroup == null || "3".equals(this.getApprovemodel())) { //如果没有组名,或且是子流程(因为有的子流程画在矩阵里面),则直接使用环节名称!!
				int li_count = tbUtil.findCount(str_ActName, "\n"); //看有没有换行!
				if (li_count <= 0 || !_isHtml) { //如果没有换行!
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_tabTitle = str_ActName; //如果
				} else { //如果有换行,则强行使用Html分行显示,兴业项目中遇到说明太长的情况!
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\n", "<br>"); //
					str_tabTitle = "<html>" + str_ActName + "</html>"; //强行使用Html分行显示!!!
				}
			} else { //如果要拼接!
				if (_isHtml) {
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\r", ""); //
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\n", "<>"); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\n", ""); //
					str_tabTitle = "<html>" + str_ActBLGroup + "<br>" + str_ActName + "</html>"; //拿组名与环节名分成两行拼在一起!!
				} else {
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\r", ""); //
					str_ActBLGroup = tbUtil.replaceAll(str_ActBLGroup, "\n", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\r", ""); //
					str_ActName = tbUtil.replaceAll(str_ActName, "\n", ""); //
					str_tabTitle = str_ActBLGroup + "-" + str_ActName; //
				}
			}
		} else {
			str_tabTitle = str_ActName;
		}

		return str_tabTitle; //
	}

	public String getCanselfaddparticipate() {
		return canselfaddparticipate;
	}

	public void setCanselfaddparticipate(String canselfaddparticipate) {
		this.canselfaddparticipate = canselfaddparticipate;
	}

}
