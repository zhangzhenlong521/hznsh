package cn.com.pushworld.salary.ui.warn.p010;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 预警管理维护界面
 * @author haoming
 * create by 2013-11-17
 */
public class TargetWarnEditWKPanel extends AbstractWorkPanel {
	private static final long serialVersionUID = -8386575849739481797L;
	private WLTButton btn_add, btn_delete, btn_update;

	@Override
	public void initialize() {
		BillListPanel listpanel = new BillListPanel("SAL_TARGET_WARN_CODE1");
		btn_add = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		listpanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_update, btn_delete });
		listpanel.repaintBillListButton();
		this.add(listpanel);
	}

}
