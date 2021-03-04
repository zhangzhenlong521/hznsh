package cn.com.pushworld.salary.ui.target.p070;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 高管定性指标定义（岗责）
 * 
 * @author Administrator
 */
public class PostTargetListWKPanel extends AbstractWorkPanel {
	private BillListPanel listPanel; // 指标列表
	private WLTButton btn_add, btn_edit, btn_delete, btn_watch; // 列表上的所有按钮

	public void initialize() {
		listPanel = new BillListPanel("SAL_POST_CHECK_LIST_CODE1");
		btn_add = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_watch = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_watch });
		listPanel.repaintBillListButton();
		this.add(listPanel, BorderLayout.CENTER);
	}
}
