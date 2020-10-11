package cn.com.infostrategy.ui.sysapp.login.taskcenter;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

public class TaskAndMsgCenterWFPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = -5637687240992199313L;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		this.add(new TaskAndMsgCenterPanel()); //
	}

}
