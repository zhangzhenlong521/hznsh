package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl
 * 
 *         2019-5-27-下午04:34:42WNSALARYDB 存款余额新增完成比
 */
public class BepositBalanceWKPanel extends AbstractWorkPanel {
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("V_WN_DEPOSIT_BALANCE_CODE1");
		this.add(list);
	}

}
