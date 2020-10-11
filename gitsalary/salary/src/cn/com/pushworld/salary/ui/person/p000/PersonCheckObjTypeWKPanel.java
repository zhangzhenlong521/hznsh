package cn.com.pushworld.salary.ui.person.p000;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 员工考核对象归类
 * @author Administrator
 *
 */
public class PersonCheckObjTypeWKPanel extends AbstractWorkPanel {
	private BillListPanel bl = null;
	private WLTButton add_btn, delete_btn, edit_btn, watch_btn = null;
	public void initialize() {
		bl = new BillListPanel("SAL_PERSON_CHECK_TYPE_CODE1");
		initBtn();
		this.add(bl, BorderLayout.CENTER);
	}
	
	private void initBtn() {
		add_btn = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "新增");
		delete_btn = WLTButton.createButtonByType(WLTButton.LIST_DELETE, "删除");
		edit_btn = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "修改");
		watch_btn = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "查看");
		bl.addBatchBillListButton(new WLTButton[]{add_btn, delete_btn, edit_btn, watch_btn});
		bl.repaintBillListButton();
	}

}
