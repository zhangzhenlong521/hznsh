package cn.com.infostrategy.ui.workflow;

import javax.swing.JPanel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.sysapp.login.taskcenter.TaskAndMsgCenterPanel;

public class MyTaskCenterWFPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		JPanel taskAndMsgCenterPanel = new TaskAndMsgCenterPanel();
		this.add(taskAndMsgCenterPanel);
		
	}

}
