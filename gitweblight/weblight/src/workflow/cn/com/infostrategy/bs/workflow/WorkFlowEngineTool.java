package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.workflow.engine.DealTaskVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * 在工作流BS端经常需要各种计算!
 * 尤其在环节拦截器,连线拦截器! 连线条件判断等地方！都需要这种计算：
 * 当前环节是否是启动环节 当前环节是否是结束环节
 * 当前环节是否是自循环内部流转  是否走过某个连线  是否走过某个环节 
 * 当前操作出去的环节编码是什么,当前走出去的连线是什么! 
 * 当前操作是从哪个环节过来的!
 * @author Administrator
 *
 */
public class WorkFlowEngineTool {

	private CommDMO commDMO = new CommDMO(); //
	private WFParVO secondCallVO = null; //

	private WorkFlowEngineTool() {
	}

	/**
	 * 构造方法
	 * @param _secondCallVO
	 */
	public WorkFlowEngineTool(WFParVO _secondCallVO) {
		this.secondCallVO = _secondCallVO; //
	}

	/**
	 * 是否是启动环节！！！
	 * @return
	 */
	public boolean isStartStep() {
		return secondCallVO.isStartStep(); //
	}

	/**
	 * 是否在环节内部循环提交的!
	 * @return
	 */
	public boolean isSelfCycleInnerSubmit() {
		return this.secondCallVO.isSecondIsSelfcycleclick(); //是否点击是自循环
	}

	/**
	 * 是否是向外提交的!即接受的任务的环节没有一个等于当前环节！
	 * 自循环就是所有接收任务的环节与本环节一样!
	 * @return
	 */
	public boolean isSubmitOutoffSelfCycle() {
		return !secondCallVO.isSecondIsSelfcycleclick(); //;  //
	}

	//从什么环节来的,如果是第一次进入,则登录人具有多个角色!是可能不准,因为取得的优先级最高的环节!
	public String getSelfCycleBindFromRoleCode() {
		return this.secondCallVO.getSecondSelfcycle_fromrolecode(); //
	}

	/**
	 * 取得自循环时,自循环如果绑定了一个角色映射的流程图!然后计算出那个流程图中从什么来的角色的“角色名称”！！！
	 * @return
	 */
	public String getSelfCycleBindFromRole() {
		return this.secondCallVO.getSecondSelfcycle_fromrolename(); //
	}

	//去的是哪个环节!!如果是自循环,则绝对是精确的!!
	public String getSelfCycleBindToRoleCode() { //
		return this.secondCallVO.getSecondSelfcycle_torolecode(); //
	}

	/**
	 * 取得自循环时,自循环如果绑定了一个角色映射的流程图!然后计算出那个流程图中去什么角色的“角色名称”！！！
	 * 这个绝对是精确的,其实就是点击的按钮的名称!!!
	 * @return
	 */
	public String getSelfCycleBindToRole() { //
		return this.secondCallVO.getSecondSelfcycle_torolename(); //
	}

	/**
	 * 计算某个连线是否走过！这个方法的好处是,有时不需要在那个连线上设置路由了！
	 * 直接根据连线编码去取
	 * @param _transitionCode
	 * @return
	 * @throws Exception
	 */
	public int getOneTransitionThrowCount(String _transitionCode) throws Exception {
		String str_prinstanceId = this.secondCallVO.getWfinstanceid(); //流程实例Id
		String str_sql = "select count(*) c1 from pub_wf_dealpool t1,pub_wf_transition t2 where t1.transition=t2.id and t1.prinstanceid='" + str_prinstanceId + "' and t2.code='" + _transitionCode + "'"; //
		String str_count = commDMO.getStringValueByDS(null, str_sql); //
		int li_count = Integer.parseInt(str_count); //
		return li_count; //
	}

	/**
	 * 某个环节是否走过！极有用的！！
	 * @param _transitionCode
	 * @return
	 */
	public boolean isOneTransitionThrowed(String _transitionCode) throws Exception {
		int li_count = getOneTransitionThrowCount(_transitionCode); //
		if (li_count > 0) {
			return true; //
		} else {
			return false; //
		}
	}

	/**
	 * 当前环节编码!
	 * @return
	 */
	public String getCurrActivityCode() {
		return this.secondCallVO.getCurractivityCode(); //
	}

	public String getCurrActivityName() {
		return this.secondCallVO.getCurractivityName(); //
	}

	/**
	 * 当前任务的往哪个环节去了！！！
	 * @return
	 */
	public String getToActivityCode() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_actCode = dealTaskVOs[i].getCurrActivityCode(); //
			if (str_actCode != null) {
				return str_actCode; //
			}
		}

		return null; //
	}

	/**
	 * 当前任务的往哪个环节去了！！！
	 * @return
	 */
	public String getToActivityName() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_actName = dealTaskVOs[i].getCurrActivityName(); //
			if (str_actName != null) {
				return str_actName; //
			}
		}
		return null; //
	}

	/**
	 * 给了哪个人,有时需要将这个人的名称
	 * @return
	 */
	public String getToUserId() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_userId = dealTaskVOs[i].getParticipantUserId(); //
			if (str_userId != null) {
				return str_userId; //
			}
		}
		return null; //
	}

	/**
	 * 给了哪个人,有时需要将这个人的名称
	 * @return
	 */
	public String getToUserCode() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_userCode = dealTaskVOs[i].getParticipantUserCode(); //
			if (str_userCode != null) {
				return str_userCode; //
			}
		}
		return null; //
	}

	/**
	 * 给了哪个人,有时需要将这个人的名称
	 * @return
	 */
	public String getToUserName() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_userName = dealTaskVOs[i].getParticipantUserCode(); //
			if (str_userName != null) {
				return str_userName; //
			}
		}
		return null; //
	}

	/**
	 * 去的哪个连线！！
	 * @return
	 */
	public String getToTransitionCode() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null; //
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_trsCode = dealTaskVOs[i].getTransitionCode(); //
			if (str_trsCode != null) {
				return str_trsCode; //
			}
		}
		return null; //
	}

	/**
	 * 去的哪个连线！！
	 * @return
	 */
	public String getToTransitionName() {
		DealTaskVO[] dealTaskVOs = this.secondCallVO.getCommitTaskVOs(); //
		if (dealTaskVOs == null) {
			return null;
		}
		for (int i = 0; i < dealTaskVOs.length; i++) { //
			String str_trsName = dealTaskVOs[i].getTransitionName(); //
			if (str_trsName != null) {
				return str_trsName; //
			}
		}
		return null; //
	}

	/**
	 * 取得路由标记！！
	 * @param _key
	 * @return
	 */
	public String getRouteMark(String _key) {
		return null; //
	}

	/**
	 * 设置路由标记！！
	 * @param _key
	 */
	public void setRouteMark(String _key) {
	}

}
