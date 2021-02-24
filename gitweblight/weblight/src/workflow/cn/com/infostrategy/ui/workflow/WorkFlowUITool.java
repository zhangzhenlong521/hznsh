package cn.com.infostrategy.ui.workflow;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.UIUtil;

/**
 * 工作流UI端的工具类!
 * @author Administrator
 *
 */
public class WorkFlowUITool {

	private HashVO hvoCurrActivityInfo = null; //当前环节信息其中dealpool$。。。有一堆任务信息！！！ 

	private WorkFlowUITool() {
	}

	public String getPrinstanceId() {
		return hvoCurrActivityInfo.getStringValue("prinstance$id"); //
	}

	/**
	 * 将拦截器中得到当前环节与当前任务等信息
	 * @param _hvoCurrActivityInfo
	 */
	public WorkFlowUITool(HashVO _hvoCurrActivityInfo) {
		hvoCurrActivityInfo = _hvoCurrActivityInfo; //
	}

	public boolean isSelfCycleInnerThisTask() {
		return hvoCurrActivityInfo.getBooleanValue("dealpool$isselfcycleclick", false); //
	}

	/**
	 * 取得当前环节如果是自循环时,对应的小流程图中的角色名称！！其实也就是图中的环节名称！！！
	 * @return
	 */
	public String getCurrSelfCycleBindRoleCode() {
		return hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_torolecode"); //
	}

	/**
	 * 取得当前环节如果是自循环时,对应的小流程图中的角色名称！！其实也就是图中的环节名称！！！
	 * @return
	 */
	public String getCurrSelfCycleBindRoleName() {
		String str_name = hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_torolename"); //
		if (str_name == null) {
			return null;
		}
		str_name = trimAndReplace(str_name); //
		return str_name; //
	}

	/**
	 * 当前环节在小流程图中是从哪个"内环节"来的!!!
	 * @return
	 */
	public String getCurrSelfCycleBindFromRoleCode() {
		return hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_fromrolecode"); //
	}

	/**
	 * 当前环节在小流程图中是从哪个"内环节"来的!!!
	 * @return
	 */
	public String getCurrSelfCycleBindFromRoleName() {
		String str_name = hvoCurrActivityInfo.getStringValue("dealpool$selfcycle_fromrolename"); //
		if (str_name == null) {
			return null;
		}
		str_name = trimAndReplace(str_name); //
		return str_name; //
	}

	//将环节上的换行符等去掉!
	private String trimAndReplace(String _str) {
		_str = _str.trim(); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "\r", ""); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "\n", ""); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "（", "("); //
		_str = TBUtil.getTBUtil().replaceAll(_str, "）", ")"); //
		_str = TBUtil.getTBUtil().replaceAll(_str, " ", ""); //
		return _str;
	}

	/**
	 * 当前环节编码!!!
	 * @return
	 */
	public String getCurrActivityCode() {
		return hvoCurrActivityInfo.getStringValue("code"); //
	}

	/**
	 * 当前环节名称!!
	 * @return
	 */
	public String getCurrActivityName() {
		return hvoCurrActivityInfo.getStringValue("wfname"); //
	}

	/**
	 * 取得当前任务是从哪个连线过来的！！！
	 * @return
	 */
	public String getCurrTaskFromTransitionCode() {
		return null; //
	}

	/**
	 * 取得当前任务是从哪个环节过来的！！！
	 * @return
	 */
	public String getCurrTaskFromActivityCode() {
		return null; //
	}

	/**
	 * 取得当前任务是从哪个环节过来的！！！
	 * @return
	 */
	public String getCurrTaskFromActivityName() {
		return null; //
	}

	/**
	 * 计算出某个连线共走过几次！！！
	 * @param _transitionCode
	 * @return
	 * @throws Exception
	 */
	public int getOneTransitionThrowCount(String _transitionCode) throws Exception {
		String str_sql = "select count(*) c1 from pub_wf_dealpool t1,pub_wf_transition t2 where t1.transition=t2.id and t1.prinstanceid='" + getPrinstanceId() + "' and t2.code='" + _transitionCode + "'"; //
		String str_count = UIUtil.getStringValueByDS(null, str_sql); //
		int li_count = Integer.parseInt(str_count); //
		return li_count; //
	}

}
