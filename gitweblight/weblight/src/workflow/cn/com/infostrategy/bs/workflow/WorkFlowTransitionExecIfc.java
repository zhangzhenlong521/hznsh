package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * 工作流某个连线执行后要做的处理!
 * @author Administrator
 *
 */
public interface WorkFlowTransitionExecIfc {

	//连线执行后的动作!
	public void afterExecTransition(WFParVO _callVO, String _prinstanceid, String _dealpoolid, BillVO _billvo) throws Exception;
}
