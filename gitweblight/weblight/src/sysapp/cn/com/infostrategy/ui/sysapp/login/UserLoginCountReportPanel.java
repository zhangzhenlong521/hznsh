package cn.com.infostrategy.ui.sysapp.login;

import cn.com.infostrategy.ui.report.style1.AbstractStyleReportPanel_1;

public class UserLoginCountReportPanel extends AbstractStyleReportPanel_1 {

	@Override
	public String getBillQueryTempletCode() {
		return "WLTDUAL_��Ա&ʱ��";
	}

	@Override
	public String getBillListTempletCode() {
		return "WLTDUAL_��Ա��¼ͳ��";
	}

	@Override
	public String getBSBuildDataClass() {
		return "cn.com.infostrategy.bs.sysapp.login.UserLoginCountReportBuilder";
	}

}
