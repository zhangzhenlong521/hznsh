package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl 单价调整记录查看 2019-7-3-下午06:09:59
 */
public class ProrataRatesWKPanel extends AbstractWorkPanel {
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("LEASEPRICE_QUGZ_CODE1");
		this.add(list);

	}

}
