package cn.com.infostrategy.ui.sysapp.login.click2;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * 修改密码的面板
 * @author xch
 *
 */
public class ResetPwdWFPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout()); //
		this.add(new ResetPwdPanel(getMenuConfMap())); //加入真正的修改密码的面板!!!
	}
}
