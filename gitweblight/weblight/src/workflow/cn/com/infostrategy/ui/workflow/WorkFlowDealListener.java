package cn.com.infostrategy.ui.workflow;

import java.util.EventListener;

/**
 * 流程处理监听,即工作流处理后需要通知其他监听者做自己的逻辑处理!!
 * 比如首页的任务中心,在工作流处理后,需要刷新下!!
 * @author Administrator
 *
 */
public interface WorkFlowDealListener extends EventListener {
	public void onDealWorkFlow(WorkFlowDealEvent _event); //
}
