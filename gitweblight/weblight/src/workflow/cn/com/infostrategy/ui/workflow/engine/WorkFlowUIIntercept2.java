package cn.com.infostrategy.ui.workflow.engine;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * 工作流UI端的连接器,在流程最后结束的时候调用
 * @author gaofeng
 * @since 2010-1-27
 */
public interface WorkFlowUIIntercept2 {
	
	public void afterWorkFlowEnd(WorkFlowProcessPanel _processPanel,BillVO billvo) throws Exception;
}
