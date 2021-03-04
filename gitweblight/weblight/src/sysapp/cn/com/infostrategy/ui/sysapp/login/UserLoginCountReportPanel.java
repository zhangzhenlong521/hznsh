package cn.com.infostrategy.ui.sysapp.login;

import cn.com.infostrategy.ui.report.style1.AbstractStyleReportPanel_1;

public class UserLoginCountReportPanel extends AbstractStyleReportPanel_1 {

	@Override
	public String getBillQueryTempletCode() {
		return "WLTDUAL_人员&时间";
	}

	@Override
	public String getBillListTempletCode() {
		return "WLTDUAL_人员登录统计";
	}

	@Override
	public String getBSBuildDataClass() {
		return "cn.com.infostrategy.bs.sysapp.login.UserLoginCountReportBuilder";
	}

}
