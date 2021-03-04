package cn.com.infostrategy.ui.sysapp.bug;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * Bug«Âµ•Œ¨ª§
 * @author xch
 *
 */
public class PubBugRecordWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billList = new BillListPanel("PUB_BUG_CODE1");  //
		this.add(billList);  //
	}

}
