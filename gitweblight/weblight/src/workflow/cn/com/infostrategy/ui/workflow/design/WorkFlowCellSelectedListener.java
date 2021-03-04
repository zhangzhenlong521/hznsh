package cn.com.infostrategy.ui.workflow.design;

import java.util.EventListener;

public interface WorkFlowCellSelectedListener extends EventListener {

	public void onWorkFlowCellSelected(WorkFlowCellSelectedEvent _event); //选中的是哪一行!!

}
