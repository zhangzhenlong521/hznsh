package cn.com.infostrategy.ui.workflow.engine;

import cn.com.infostrategy.to.mdata.BillVO;

/**
 * ������UI�˵�������,��������������ʱ�����
 * @author gaofeng
 * @since 2010-1-27
 */
public interface WorkFlowUIIntercept2 {
	
	public void afterWorkFlowEnd(WorkFlowProcessPanel _processPanel,BillVO billvo) throws Exception;
}
