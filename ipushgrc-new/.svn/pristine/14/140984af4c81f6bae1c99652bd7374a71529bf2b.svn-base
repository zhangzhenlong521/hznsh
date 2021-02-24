package com.pushworld.ipushlbs.ui.powermanage.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 授权申请
 * 
 * @author yinliang
 * @since 2011.12.14
 */
public class PowerApplyWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billlist = null;
	private WLTButton btn_insert, btn_edit, btn_query, btn_del, btn_wfmonitor;

	@Override
	public void initialize() {

		billlist = new BillListPanel("LBS_POWERAPPLY_CODE1");
		billlist.setVisible(true);
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); // 增加
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); // 修改
		btn_query = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); // 浏览
		btn_del = WLTButton.createButtonByType(WLTButton.LIST_DELETE); // 删除
		btn_wfmonitor = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR); // 监控发起流程
		btn_wfmonitor.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_del.addActionListener(this);

		billlist.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_del, btn_query, btn_wfmonitor });
		billlist.repaintBillListButton();

		this.add(billlist);

	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_edit)
			onEdit(); // 点击修改按钮
		if (e.getSource() == btn_del)
			onDelete(); // 点击删除按钮
		if (e.getSource() == btn_wfmonitor)
			onWfmonitor(); // 点击工作流发起/监控按钮

	}

	// 工作流发起/监控
	private void onWfmonitor() {
		BillVO billVO = billlist.getSelectedBillVO(); //	
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return; //
		}
		if (!billVO.containsKey("wfprinstanceid")) {
			MessageBox.show(billlist, "选中的记录中没有定义工作流字段(wfprinstanceid)!"); //
			return; //
		}
		int flag = billlist.getSelectedRow(); // 当前选择行
		String str_wfprinstanceid = billVO.getStringValue("wfprinstanceid"); // 取得此工作流序列ID

		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {// 如果流程未发起，则发起流程，否则监控流程
			onBillListWorkFlowProcess(billVO, flag);
		} else { // 监控工作流
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(billlist, str_wfprinstanceid, billVO); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true); //
		}
	}

	// 发起工作流
	private void onBillListWorkFlowProcess(BillVO billvo, int flag) {
		try {
			new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", billlist, null); // 处理动作!
			// 发起工作流的同时，将工作流状态改为审批中
			billlist.setSelectedRow(flag);
			if (billlist.getSelectedBillVO().getStringValue("wfprinstanceid") == null) // 如果流程并未发起
				return;
			if (billlist.getSelectedBillVO().getStringValue("wfprinstanceid").equals(""))
				return;
			else {
				String sql_update = "update " + billvo.getSaveTableName() + " set flowstate = '审批中' where id = '" + billvo.getStringValue("id") + "'";
				UIUtil.executeUpdateByDS(null, sql_update);
				billlist.refreshData();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void onDelete() {
		BillVO billvo = billlist.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (getStatus(billvo)) {
			if (MessageBox.confirmDel(this)) {
				try {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id") });
					billlist.removeSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			MessageBox.show(this, "此状态的数据不可删除!");
		}
	}

	private void onEdit() {
		BillVO billvo = billlist.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (getStatus(billvo)) {
			BillCardPanel cardpanel = new BillCardPanel(billlist.getTempletVO());
			cardpanel.setBillVO(billvo);
			cardpanel.setEditableByEditInit();
			BillCardDialog carddialog = new BillCardDialog(this, "修改", cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			carddialog.setVisible(true);
			if (carddialog.getCloseType() == 1)
				billlist.refreshCurrSelectedRow();
		} else {
			MessageBox.show(this, "此状态的数据不可编辑!");
		}
	}

	private boolean getStatus(BillVO billvo) {
		// 判断流程是否已经发起或者结束
		if (billvo.getStringValue("flowstate").equals("审批中") || billvo.getStringValue("flowstate").equals("审批通过") || billvo.getStringValue("flowstate").equals("审批未通过"))
			return false;
		else
			return true;
	}
}
