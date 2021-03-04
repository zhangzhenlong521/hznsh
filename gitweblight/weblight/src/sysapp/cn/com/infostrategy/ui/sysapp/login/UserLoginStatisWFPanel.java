package cn.com.infostrategy.ui.sysapp.login;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTRadioPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class UserLoginStatisWFPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		WLTRadioPane radioPanel = new WLTRadioPane(); //
		BillListPanel billList_detail = new BillListPanel("PUB_SYSDEALLOG_CODE2"); //
		UserLoginCountReportPanel statisPanel = new UserLoginCountReportPanel(); //

		radioPanel.addTab("Ã÷Ï¸", billList_detail); //
		radioPanel.addTab("»ã×Ü", statisPanel); //
		this.add(radioPanel); //
	}

}
