package cn.com.infostrategy.ui.sysapp.userrole;

import cn.com.infostrategy.ui.mdata.styletemplet.t02.AbstractStyleWorkPanel_02;

/**
 * 用户管理
 * @author user
 *
 */
public class UserManagerWPanel extends AbstractStyleWorkPanel_02 {

	private static final long serialVersionUID = 1L;

	public String getTempletcode() {
		return "PUB_USER_CODE1";
	}

	public String getBsinterceptor() {
		return "cn.com.infostrategy.bs.sysapp.userrole.UserManagerBSIntercept"; //
	}

	public String getCustBtnPanelName() {
		return null;
	}

	public String getUiinterceptor() {
		return null;
	}

}
