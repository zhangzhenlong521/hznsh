package cn.com.infostrategy.ui.workflow;

import java.util.EventObject;

/**
 * 流程处理事件,即工作流处理后,需要通知其他面板刷新等! 比如首页的任务中心!
 * @author Administrator
 *
 */
public class WorkFlowDealEvent extends EventObject {

	private static final long serialVersionUID = 5065941700069269833L;
	private int dealType = -1; //
	private String prInstanceId = null; //实例id
	private String prDealPoolId = null; //流程任务id

	public WorkFlowDealEvent(Object _source, int _dealType, String _prinstanceId, String _prdealPoolId) {
		super(_source);
		this.dealType = _dealType; //
		this.prInstanceId = _prinstanceId; //流程实例id
		this.prDealPoolId = _prdealPoolId; //流程任务id
	}

	public int getDealType() {
		return dealType;
	}

	public String getPrInstanceId() {
		return prInstanceId;
	}

	public String getPrDealPoolId() {
		return prDealPoolId;
	}

}
