package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * �޸���������
 * @author xch
 *
 */
public class ResetPwdWFPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		this.add(new ResetPwdPanel(getMenuConfMap())); //�����������޸���������!!!
	}
}
