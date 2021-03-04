package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * 工作流设计器面板
 * @author xch
 *
 */
public class WMFPanel_WorkFlow extends AbstractWorkPanel {

	private static final long serialVersionUID = -1903208877192499897L;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); // 设置布局类!
		WorkFlowDesignWPanel wfpanel = new WorkFlowDesignWPanel(); //
		this.add(wfpanel); //
	}

}
