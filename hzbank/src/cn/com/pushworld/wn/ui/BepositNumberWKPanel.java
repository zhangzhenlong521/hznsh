package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl
 * 
 *         2019-5-27-下午04:03:44 存款户数完成比
 */
public class BepositNumberWKPanel extends AbstractWorkPanel {
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("V_WN_DEPOSIT_NUMBER_CODE1");
		this.add(list);

	}

}
