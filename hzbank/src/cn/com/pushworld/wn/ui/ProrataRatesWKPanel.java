package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl ���۵�����¼�鿴 2019-7-3-����06:09:59
 */
public class ProrataRatesWKPanel extends AbstractWorkPanel {
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("LEASEPRICE_QUGZ_CODE1");
		this.add(list);

	}

}
