package cn.com.infostrategy.ui.workflow.design;

import java.util.EventObject;

import cn.com.infostrategy.to.workflow.design.ActivityVO;

public class WorkFlowCellSelectedEvent extends EventObject {

	private static final long serialVersionUID = 7559043674370715737L;

	private ActivityVO activityVO = null;

	public WorkFlowCellSelectedEvent(Object source, ActivityVO _activotyVO) {
		super(source);
		activityVO = _activotyVO; //
	}

	public Object getSource() {
		return (WorkFlowDesignWPanel) super.getSource();
	}

	public WorkFlowDesignWPanel getWorkFlowDesignWPanel() {
		return (WorkFlowDesignWPanel) super.getSource();
	}

	public ActivityVO getActivityVO() {
		return activityVO;
	}

	public void setActivityVO(ActivityVO activityVO) {
		this.activityVO = activityVO;
	}

}
