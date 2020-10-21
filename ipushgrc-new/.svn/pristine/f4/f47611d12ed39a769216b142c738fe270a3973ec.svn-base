package com.pushworld.ipushgrc.ui.cmpcheck.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CheckItemListPanel extends BillListPanel implements BillListHtmlHrefListener, ActionListener {
	private int type = 1;
	private WLTButton btn_update, btn_delete; // 删除，修改按钮需要自己写逻辑！
	BillListPanel billList;

	public CheckItemListPanel(String _templetcode, int _type) { // _type
		// 1有删除修改按钮。2没有
		super(_templetcode);
		type = _type;
		init();
	}

	public void init() {
		this.addBillListHtmlHrefListener(this);

	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		BillVO checkItemVO = this.getSelectedBillVO();
		if (_event.getItemkey().equals("ward_count")) {
			updateWard(checkItemVO);
		} else if (_event.getItemkey().equals("event_count")) {
			updateEvent(checkItemVO);
		} else if (_event.getItemkey().equals("issue_count")) {
			updateIssue(checkItemVO);
		}
	}

	/*
	 * 修改成功防范
	 */
	private void updateWard(BillVO checkItemVO) {
		// 弹出列表供修改，记录数发生变化时刷新子表中对应数量
		if (showUpdateWin(checkItemVO.getStringValue("id"), "成功防范列表", "CMP_WARD_CODE1")) {
			this.refreshCurrSelectedRow();
		}
	}

	/*
	 * 修改违规事件
	 */
	private void updateEvent(BillVO checkItemVO) {
		// 弹出列表供修改，记录数发生变化时刷新子表中对应数量
		if (showUpdateWin(checkItemVO.getStringValue("id"), "违规事件列表", "CMP_EVENT_CODE1")) {
			this.refreshCurrSelectedRow();
		}
	}

	/*
	 * 修改发现问题
	 */
	private void updateIssue(BillVO checkItemVO) {
		// 弹出列表供修改，记录数发生变化时刷新子表中对应数量
		if (showUpdateWin(checkItemVO.getStringValue("id"), "发现问题列表", "CMP_ISSUE_CODE1")) {
			this.refreshCurrSelectedRow();
		}
	}

	/***************************************************************************
	 * 弹出列表对话框
	 * 
	 * @param pk
	 * @param title
	 * @param templetCode
	 * @return true-记录数发生变化
	 */
	private boolean showUpdateWin(String pk, String title, String templetCode) {
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_update.addActionListener(this);
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_delete.addActionListener(this);
		BillListDialog listDialog = new BillListDialog(this, title, templetCode);
		billList = listDialog.getBilllistPanel();
		billList.setQuickQueryPanelVisiable(false);//这里需要用此方法，该方法中隐藏了展开按钮【李春娟/2013-09-02】
		billList.queryDataByCondition(" cmp_check_item_id = " + pk, null);
		int rowCountOld = billList.getRowCount();
		if (type == 1) {
			// 增加修改、删除按钮
			billList.addBatchBillListButton(new WLTButton[] { btn_update, btn_delete, WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
			billList.repaintBillListButton();
		} else {
			billList.addBatchBillListButton(new WLTButton[] { WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD) });
			billList.repaintBillListButton();
		}
		listDialog.getBtn_confirm().setVisible(false); // 隐藏确认按钮
		listDialog.getBtn_cancel().setText("关闭");
		listDialog.setVisible(true);

		int rowCountNew = billList.getRowCount();

		return rowCountOld != rowCountNew;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_update) { // 更新
			onUpdate();
		} else if (e.getSource() == btn_delete) { // 删除
			onDelete();
		}

	}

	public void onUpdate() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel(billList.getTempletVO().getTempletcode());
		String tableName = cardPanel.getTempletVO().getTablename();
		if ("cmp_event".equalsIgnoreCase(tableName)) {//如果是修改的违规事件
			if (this.isHasAdjustCase(selectVO)) {
				MessageBox.show(this, "此违规记录已有相应的调整方案,不可更新!");
				return;
			}
		}
		if (this.getClientProperty("checkedcorp") != null) {
			cardPanel.putClientProperty("checkedcorp", this.getClientProperty("checkedcorp"));
		}
		cardPanel.setBillVO(selectVO);
		cardPanel.setEditableByEditInit();
		BillCardDialog dialog = new BillCardDialog(billList, "修改", cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
		if (selectVO.getStringValue("cmp_cmpfile_id") == null || selectVO.getStringValue("cmp_cmpfile_id").equals("")) {
			cardPanel.setEditable("refrisks", false);
		}
		dialog.setVisible(true);
		if (dialog.getCloseType() != 1) {
			return;
		} else {
			if ("cmp_ward".equalsIgnoreCase(tableName) || "cmp_event".equalsIgnoreCase(tableName)) {
				//将billList传参过去,来用于“风险评估申请”对话框的父窗口，以避免某些问题，详见GeneralInsertIntoRiskEval类[YangQing/2013-09-18]
				new GeneralInsertIntoRiskEval(billList, cardPanel, tableName, "修改");
			}
		}
		billList.refreshCurrSelectedRow();
	}

	public void onDelete() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(billList);
			return;
		}
		String table = selectVO.getSaveTableName();
		if ("cmp_event".equalsIgnoreCase(table)) {//如果是修改的违规事件
			if (this.isHasAdjustCase(selectVO)) {
				MessageBox.show(this, "此违规记录已有相应的调整方案,不可删除!");
				return;
			}
		}
		if (!MessageBox.confirmDel(billList)) {
			return;
		}
		List deleteSQL = new ArrayList();
		TBUtil tbutil = new TBUtil();
		if (selectVO.getStringValue("wardcust") != null && !selectVO.getStringValue("wardcust").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("wardcust"));
			deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // 删除涉及的客户子表数据
			// 成功防范
		}
		if (selectVO.getStringValue("warduser") != null && !selectVO.getStringValue("warduser").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("warduser"));
			deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // 删除涉及的员工子表数据
			// 成功防范
		}
		if (selectVO.getStringValue("eventcust") != null && !selectVO.getStringValue("eventcust").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("eventcust"));
			deleteSQL.add("delete from cmp_wardevent_cust where id in (" + inCondition + ")"); // 删除涉及的客户子表数据
			// 违规事件
		}
		if (selectVO.getStringValue("eventuser") != null && !selectVO.getStringValue("eventuser").equals("")) {
			String inCondition = tbutil.getInCondition(selectVO.getStringValue("eventuser"));
			deleteSQL.add("delete from cmp_wardevent_user where id in (" + inCondition + ")"); // 删除涉及的员工子表数据
			// 违规事件
		}
		String tableName = billList.getTempletVO().getTablename();
		deleteSQL.add("delete from " + tableName + " where id = '" + selectVO.getStringValue("id") + "'");
		if (deleteSQL.size() > 0) {
			try {
				UIUtil.executeBatchByDS(null, deleteSQL);
				billList.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 查看一个违规事件是否有调整方案
	 * @param vo
	 * @return
	 */
	private boolean isHasAdjustCase(BillVO vo) {
		if (vo == null)
			return false;
		String eventid = vo.getStringValue("id");
		String sql = "select * from CMP_EVENT_ADJUSTPROJECT where eventid =" + eventid;
		HashVO[] vos = null;
		try {
			vos = UIUtil.getHashVoArrayByDS(null, sql);
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (vos == null || vos.length == 0)
			return false;
		else
			return true;

	}
}
