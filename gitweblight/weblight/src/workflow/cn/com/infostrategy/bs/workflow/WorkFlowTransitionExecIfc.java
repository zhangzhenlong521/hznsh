package cn.com.infostrategy.bs.workflow;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.workflow.engine.WFParVO;

/**
 * ������ĳ������ִ�к�Ҫ���Ĵ���!
 * @author Administrator
 *
 */
public interface WorkFlowTransitionExecIfc {

	//����ִ�к�Ķ���!
	public void afterExecTransition(WFParVO _callVO, String _prinstanceid, String _dealpoolid, BillVO _billvo) throws Exception;
}
