package cn.com.infostrategy.ui.sysapp.userrole;

import java.awt.BorderLayout;

import javax.swing.JTabbedPane;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * 用户绑定角色!
 * @author xch
 *
 */
public class UserAssignRoleWPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 1L;

	private UserAssignRolePanel panel_1 = null;

	private RoleAssignUserPanel panel_2 = null;

	public void initialize() {
		this.setLayout(new BorderLayout()); //
		JTabbedPane tabPane = new JTabbedPane();//
		panel_1 = new UserAssignRolePanel();
		panel_2 = new RoleAssignUserPanel();
		tabPane.addTab("用户绑定角色", panel_1);
		tabPane.addTab("角色绑定用户", panel_2);

		this.add(tabPane, BorderLayout.CENTER); //
	}

}
