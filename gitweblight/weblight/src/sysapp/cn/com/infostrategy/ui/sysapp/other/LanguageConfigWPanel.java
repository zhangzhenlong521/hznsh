package cn.com.infostrategy.ui.sysapp.other;

import cn.com.infostrategy.to.sysapp.other.TMO_Pub_Language;
import cn.com.infostrategy.ui.mdata.styletemplet.t01.AbstractStyleWorkPanel_01;

public class LanguageConfigWPanel extends AbstractStyleWorkPanel_01 {

	private static final long serialVersionUID = -849171833990234772L;

	public String getTempletcode() {
		return TMO_Pub_Language.class.getName();
	}

	public String getBsinterceptor() {
		return null;
	}

	public String getCustBtnPanelName() {
		return null;
	}

	public String getUiinterceptor() {
		return null;
	}

	public boolean isCanInsert() {
		return false;
	}
}
