package cn.com.infostrategy.ui.sysapp.dataaccess;

import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.styletemplet.t01.AbstractStyleWorkPanel_01;

/**
 * 权限权限策略
 * @author xch
 *
 */
public class DataPolicyWKPanel extends AbstractStyleWorkPanel_01 {

	@Override
	public String getTempletcode() {
		return "PUB_DATAPOLICY_CODE1"; //策略主表
	}

	@Override
	public void afterInitialize() throws Exception {
		super.afterInitialize();

		BillListPanel billList = getBillListPanel(); //
		WLTButton btn_moveup = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEUP); //
		WLTButton btn_movedown = WLTButton.createButtonByType(WLTButton.LIST_ROWMOVEDOWN); //
		billList.addBatchBillListButton(new WLTButton[] { btn_moveup, btn_movedown }); //
		billList.repaintBillListButton(); //
	}

}
