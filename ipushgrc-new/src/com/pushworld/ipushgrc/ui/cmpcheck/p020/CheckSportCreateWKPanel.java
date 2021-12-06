package com.pushworld.ipushgrc.ui.cmpcheck.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 创建检查活动实例 和计划弱关联, 检查的子项目做为引用子表加入进来
 * 
 * @author Gwang
 * 
 */
public class CheckSportCreateWKPanel extends AbstractWorkPanel implements ActionListener, BillListSelectListener {

	private BillListPanel billList_check = null; // 检查活动

	private WLTButton btn_insert, btn_insertfromplan, btn_edit, btn_delete, btn_browse;

	private BillCardPanel cardPanel; // 从计划新增活动的面板

	@Override
	public void initialize() {
		billList_check = new BillListPanel("CMP_CHECK_CODE1"); // 检查活动

		// 按钮
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "直接创建"); // 新增
		btn_insert.addActionListener(this);
		btn_insertfromplan = WLTButton.createButtonByType(WLTButton.COMM, "从计划创建"); // 新增
		btn_insertfromplan.addActionListener(this);
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // 编辑
		btn_edit.addActionListener(this);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // 删除
		btn_delete.addActionListener(this);

		btn_browse = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); // 浏览

		billList_check.addBatchBillListButton(new WLTButton[] { btn_insert, btn_insertfromplan, btn_edit, btn_delete, btn_browse });
		billList_check.repaintBillListButton();

		billList_check.addBillListSelectListener(this);
		this.add(billList_check);

		// 加载数据
		billList_check.QueryDataByCondition(null);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_delete) { // 删除
			onDelete();
		} else if (obj == btn_insert) {
			onInsert(); // 直接创建
		} else if (obj == btn_insertfromplan) {
			onInsertFormPlan(); // 从计划创建
		} else if (obj == btn_edit) { // 修改
			onEdit();
		}
	}

	private void onInsert() {
		cardPanel = new BillCardPanel("CMP_CHECK_CODE2");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setVisiable("cmp_checkplan_id", false);
		BillCardDialog billCardDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		billCardDialog.setVisible(true);

		// 新增后刷新列表
		if (billCardDialog.getCloseType() == 1) {
			int li_newrow = billList_check.newRow(false); //
			billList_check.setBillVOAt(li_newrow, billCardDialog.getBillVO(), false);
			billList_check.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			billList_check.setSelectedRow(li_newrow); //
		}

	}

	private void onInsertFormPlan() {
		BillListDialog billlistDialog = new BillListDialog(this, "选择一条计划点击下一步", "CMP_CHECKPLAN_CODE1");
		billlistDialog.getBtn_confirm().setText("下一步");
		billlistDialog.getBilllistPanel().QueryDataByCondition(null);
		billlistDialog.getBilllistPanel().addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		billlistDialog.getBilllistPanel().repaintBillListButton();//增加浏览按钮【李春娟/2012-03-28】
		billlistDialog.setVisible(true);
		if (billlistDialog.getCloseType() != 1) {
			return;
		}
		BillVO planVo = billlistDialog.getReturnBillVOs()[0];
		cardPanel = new BillCardPanel("CMP_CHECK_CODE2");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		BillCardDialog billCardDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		cardPanel.setValueAt("cmp_checkplan_id", new RefItemVO(planVo.getStringValue("id"), planVo.getStringValue("planname"), planVo.getStringValue("planname")));
		cardPanel.setValueAt("planname", new StringItemVO(planVo.getStringValue("planname")));
		cardPanel.setValueAt("checkedcorp", new RefItemVO(planVo.getStringValue("checkedcorp"), "", planVo.getStringViewValue("checkedcorp")));
		cardPanel.setValueAt("checkdescr", new StringItemVO(planVo.getStringValue("plandescr")));
		billCardDialog.setVisible(true);

		// 新增后刷新列表
		if (billCardDialog.getCloseType() == 1) {
			int li_newrow = billList_check.newRow(false); //
			billList_check.setBillVOAt(li_newrow, billCardDialog.getBillVO(), false);
			billList_check.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); //设置列表该行的数据为初始化状态.
			billList_check.setSelectedRow(li_newrow); //
		}
	}

	/**
	 * 删除主,子表
	 */
	private void onDelete() {
		BillVO selectedRow = billList_check.getSelectedBillVO();
		if (selectedRow == null) {
			MessageBox.showSelectOne(this);
			return;
		} else if (!MessageBox.confirmDel(this)) {
			return;
		}

		String id = selectedRow.getStringValue("id");
		ArrayList sqlList = new ArrayList();
		sqlList.add("delete from cmp_check where id = " + id);
		sqlList.add("delete from cmp_check_item where cmp_check_id = " + id);
		billList_check.removeSelectedRow();
		try {
			UIUtil.executeBatchByDS(null, sqlList);
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	private void onEdit() {
		BillVO selectedRow = billList_check.getSelectedBillVO();
		if (selectedRow == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		String id = selectedRow.getStringValue("id");
		cardPanel = new BillCardPanel("CMP_CHECK_CODE2");
		cardPanel.queryDataByCondition("id = " + id);
		String planid = cardPanel.getBillVO().getStringValue("cmp_checkplan_id", "");
		if ("".equals(planid)) {
			cardPanel.setVisiable("cmp_checkplan_id", false);//这里如果是直接创建的即计划为空则直接隐藏计划【李春娟/2012-03-28】
		}
		BillCardDialog billCardDialog = new BillCardDialog(this, "修改", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		billCardDialog.setVisible(true);

	}

	/**
	 * 处理不同状态的记录操作
	 */
	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO selectedRow = billList_check.getSelectedBillVO();
		// 开始实施或实施完成的记录不能修改, 删除
		if (!selectedRow.getStringValue("status").equals("1")) {
			btn_edit.setEnabled(false);
			btn_delete.setEnabled(false);
		} else {
			btn_edit.setEnabled(true);
			btn_delete.setEnabled(true);
		}
	}

}
