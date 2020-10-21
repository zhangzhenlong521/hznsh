package com.pushworld.ipushgrc.ui.cmpcheck.p010;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 检查计划维护
 * 就是检查计划(cmp_checkplan)的单列表维护
 * @author Gwang
 *
 */
public class CheckPlanEditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList_plan = null;

	private WLTButton btn_insert, btn_edit, btn_delete, btn_browse;

	@Override
	public void initialize() {
		billList_plan = new BillListPanel("CMP_CHECKPLAN_CODE1");
		//按钮
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); // 新增
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //编辑
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //删除
		btn_delete.addActionListener(this);

		btn_browse = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //浏览		

		billList_plan.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_delete, btn_browse });
		billList_plan.repaintBillListButton();

		this.add(billList_plan);

		//加载数据
		billList_plan.QueryDataByCondition(null);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_delete) {
			doDelete();
		}
	}

	/**
	 * 删除主表
	 */
	private void doDelete() {

		BillVO selectedRow = billList_plan.getSelectedBillVO();
		if (selectedRow == null) {
			MessageBox.showSelectOne(this);
			return;
		} else if (!MessageBox.confirmDel(this)) {
			return;
		}

		String id = selectedRow.getStringValue("id");
		ArrayList sqlList = new ArrayList();
		sqlList.add("delete from cmp_checkplan where id = " + id);
		billList_plan.removeSelectedRow();
		try {
			UIUtil.executeBatchByDS(null, sqlList);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}

	}
}
