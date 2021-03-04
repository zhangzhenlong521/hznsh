package cn.com.infostrategy.ui.sysapp.sysboard;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 系统公告维护!!!
 * @author xch
 *
 */
public class PubSysBoardWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel  billList = new BillListPanel("PUB_SYSBOARD_CODE1");  //
		this.add(billList);  //
	}

}
