package cn.com.infostrategy.ui.sysapp.userrole;

import cn.com.infostrategy.to.mdata.templetvo.ServerTMODefine;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ½ÇÉ«¹ÜÀí
 * @author user
 *
 */
public class RoleManagerWPanel extends AbstractWorkPanel {

	private static final long serialVersionUID = 1L;

	private BillListPanel billList = null; //
	private WLTButton btn_insert, btn_update, btn_delete; //

	@Override
	public void initialize() {
		billList = new BillListPanel(new ServerTMODefine("cn.com.infostrategy.bs.sysapp.servertmo.TMO_Pub_Role")); ////
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); ////
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); ////
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); ////
		billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete }); ////
		billList.repaintBillListButton(); //

		this.add(billList); //
	}

}
