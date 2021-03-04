package cn.com.infostrategy.ui.sysapp.loginhref;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class PubLoginHrefWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billList = new BillListPanel("PUB_LOGINHREF_CODE1");  //
		this.add(billList);  //
	}

}
