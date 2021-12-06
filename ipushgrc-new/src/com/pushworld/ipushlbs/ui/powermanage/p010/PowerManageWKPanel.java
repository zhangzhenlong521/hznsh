package com.pushworld.ipushlbs.ui.powermanage.p010;

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
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListMouseDoubleClickedListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 授权管理
 * 
 * @author yinliang
 * 
 */
public class PowerManageWKPanel extends AbstractWorkPanel implements ActionListener, BillListMouseDoubleClickedListener {
	private BillListPanel billlist = null;
	private BillCardPanel cardPanel; // 弹出的增加card
	private WLTButton btn_insert, btn_edit, btn_del, btn_query, btn_wfmonitor, btn_commit;

	@Override
	public void initialize() {
		billlist = new BillListPanel("LBS_POWERMANAGE_CODE1");
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT);
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_query = new WLTButton("浏览"); // 浏览按钮重写，去掉卡片中的按钮
		btn_del = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_commit = new WLTButton("发布"); // 提交
		// 按钮添加监听
		btn_insert.addActionListener(this);
		btn_commit.addActionListener(this);
		btn_edit.addActionListener(this);
		btn_del.addActionListener(this);
		btn_query.addActionListener(this);
		// billlist添加监听
		billlist.addBillListMouseDoubleClickedListener(this);
		billlist.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_del, btn_query, btn_commit });
		billlist.repaintBillListButton();
		this.add(billlist);
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_insert)
			onInsert(); // 增加状态
		else if (obj == btn_edit)
			onEdit(); // 点击修改按钮
		else if (obj == btn_del)
			onDelete(); // 点击删除按钮
		else if (obj == btn_wfmonitor)
			onWfmonitor(); // 点击工作流发起/监控按钮
		else if (obj == btn_commit)
			onCommit(); // 提交
		else if (obj == btn_query)
			onQuery();
	}

	// 浏览按钮事件
	private void onQuery() {
		BillVO billvo = billlist.getSelectedBillVO(); // 取得当前选择的行的billvo
		if (billvo == null) {
			MessageBox.showSelectOne(billlist);
			return;
		}
		QueryInfo(billvo);
	}

	// 双击事件
	private void QueryInfo(BillVO billvo) {
		BillCardPanel cardPanel = new BillCardPanel("LBS_POWERMANAGE_CODE2"); // 当前卡片panel
		cardPanel.setBillVO(billvo); //
		BillCardDialog dialog = new BillCardDialog(billlist, "授权信息查看", cardPanel, WLTConstants.BILLDATAEDITSTATE_INIT);
		dialog.setVisible(true);
	}

	private void onInsert() {
		// BillVO billvo = billlist.getSelectedBillVO();
		cardPanel = new BillCardPanel(billlist.templetVO); // 创建一个卡片面板
		cardPanel.setLoaderBillFormatPanel(billlist.getLoaderBillFormatPanel()); // 将列表的BillFormatPanel的句柄传给卡片
		cardPanel.insertRow(); // 卡片新增一行!
		cardPanel.setEditableByInsertInit(); // 设置卡片编辑状态为新增时的设置
		BillCardDialog dialog = new BillCardDialog(billlist, billlist.templetVO.getTempletname(), cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT); // 弹出卡片新增框
		dialog.setVisible(true); // 显示卡片窗口

		// btn_apply.addActionListener(this);
		if (dialog.getCloseType() == 1) { // 如是是点击确定返回!将则卡片中的数据赋给列表!
			int li_newrow = billlist.newRow(false); //
			billlist.setBillVOAt(li_newrow, dialog.getBillVO(), false);
			billlist.setRowStatusAs(li_newrow, WLTConstants.BILLDATAEDITSTATE_INIT); // 设置列表该行的数据为初始化状态.
			billlist.setSelectedRow(li_newrow);

		}
	}

	// 提交授权
	private void onCommit() {
		BillVO billVO = billlist.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(billlist); //
			return; //
		}
		if (billVO.getStringValue("commitstate").equals("已发布")) {
			MessageBox.show(billlist, "该信息已发布,请勿重复操作!");
			return;
		}
		if (MessageBox.confirm(billlist, "您确定要发布该信息吗?")) {
			// 更新授权下发管理中的状态为已发布
			String sql_commit = "update " + billVO.getSaveTableName() + " set commitstate = '已发布' where id = '" + billVO.getStringValue("id") + "'";
			try {
				UIUtil.executeUpdateByDS(null, sql_commit);
				// 如果当前发布的授权信息为从申请中引用的，则需要将授权申请中的那条信息的发布状态改为上级已发布
				if (billVO.getStringValue("REFAPPLY") != null && !billVO.getStringValue("REFAPPLY").equals("")) {
					sql_commit = "update lbs_powerapply set releasestate = '上级已发布' where id = '" + billVO.getStringValue("REFAPPLY") + "'";
					UIUtil.executeUpdateByDS(null, sql_commit);
					// 授权申请页面是不是要自动刷新？感觉自动刷新不是很好，如果下级单位正在执行其他操作，可能会有影响。
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			billlist.refreshCurrSelectedRow();
		}
	}

	// 工作流发起/监控
	private void onWfmonitor() {
		BillVO billVO = billlist.getSelectedBillVO(); //	
		if (billVO == null) {
			MessageBox.showSelectOne(billlist); //
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
			// 发起工作流的同时，将工作流状态改为已发起
			billlist.setSelectedRow(flag);
			if (billlist.getSelectedBillVO().getStringValue("wfprinstanceid") == null) // 如果流程并未发起
				return;
			else if (billlist.getSelectedBillVO().getStringValue("wfprinstanceid").equals(""))
				return;
			else {
				String sql_update = "update lbs_powermanage set flowstate = '已发起' where id = '" + billvo.getStringValue("id") + "'";
				UIUtil.executeUpdateByDS(null, sql_update);
				billlist.refreshCurrSelectedRow();
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
		if (billvo.getStringValue("commitstate").equals("未发布")) {
			if (MessageBox.confirmDel(this)) {
				try {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + billvo.getSaveTableName() + " where id = " + billvo.getStringValue("id") });
					billlist.removeSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			MessageBox.showNotAllow(this,billvo.getStringValue("commitstate") );
		}
	}

	private void onEdit() {
		BillVO billvo = billlist.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (billvo.getStringValue("commitstate").equals("未发布")) {
			BillCardPanel cardpanel = new BillCardPanel(billlist.getTempletVO());
			cardpanel.setBillVO(billvo);
			cardpanel.setEditableByEditInit();
			BillCardDialog carddialog = new BillCardDialog(this, "修改", cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			carddialog.setVisible(true);
			if (carddialog.getCloseType() == 1)
				billlist.refreshCurrSelectedRow();
		} else {
			MessageBox.showNotAllow(this,billvo.getStringValue("commitstate") );
		}
	}

	// list双击事件
	public void onMouseDoubleClicked(BillListMouseDoubleClickedEvent _event) {
		QueryInfo(_event.getBillListPanel().getSelectedBillVO());
	}
}
