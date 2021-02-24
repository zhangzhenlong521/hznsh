package cn.com.infostrategy.ui.sysapp.lookandfeel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * œµÕ≥∆§∑Ù…Ë÷√
 * @author xch
 *
 */
public class PubLookAndFeelWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billList = new BillListPanel("PUB_LOOKANDFEEL_CODE1"); //
		this.add(billList); //
	}

}

