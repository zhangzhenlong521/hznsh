package cn.com.infostrategy.ui.workflow.design;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * ��������������
 * @author xch
 *
 */
public class WMFPanel_WorkFlow extends AbstractWorkPanel {

	private static final long serialVersionUID = -1903208877192499897L;

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); // ���ò�����!
		WorkFlowDesignWPanel wfpanel = new WorkFlowDesignWPanel(); //
		this.add(wfpanel); //
	}

}
