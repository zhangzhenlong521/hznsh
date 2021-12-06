package com.pushworld.ipushgrc.ui.cmpreport.p030;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.TBUtil;
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
 * 合规专项报告维护与上报!!!
 * 就是对合规报告(cmp_report2)的单列表,上面有按钮【新增】【修改】【删除】【提交】【流程监控】
 * @author xch
 *
 */
public class CmpReport2EditWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillListPanel billList = null; //

	private WLTButton btn_insert, btn_edit, btn_del, btn_wfmonitor; //

	public void initialize() {
		billList = new BillListPanel("CMP_REPORT2_CODE1"); //
		btn_insert = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT); //
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		btn_edit.addActionListener(this);
		btn_del = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_del.addActionListener(this);
		boolean wf = TBUtil.getTBUtil().getSysOptionBooleanValue("合规报告是否走工作流", true);
		if (wf) {
			btn_wfmonitor = WLTButton.createButtonByType(WLTButton.LIST_WORKFLOWSTART_MONITOR); //
			btn_wfmonitor.addActionListener(this);
			billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_del, btn_wfmonitor }); //
		} else {
			billList.setItemVisible("state", false);//如果不走工作流，则隐藏状态【loj/2015-05-21】
			billList.addBatchBillListButton(new WLTButton[] { btn_insert, btn_edit, btn_del }); //
		}

		billList.repaintBillListButton(); //
		this.add(billList); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_edit) {
			onEdit();
		} else if (obj == btn_del) {
			onDelete();
		} else if (obj == btn_wfmonitor) {
			wfStartOrMonitor();
		}
	}

	private void onEdit() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("新建".equals(selectVO.getStringValue("state"))) {
			BillCardPanel cardpanel = new BillCardPanel(billList.getTempletVO());
			cardpanel.setBillVO(selectVO);
			cardpanel.setEditableByEditInit();
			BillCardDialog cardDialog = new BillCardDialog(this, "修改", cardpanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);
			cardDialog.setVisible(true);
			if (cardDialog.getCloseType() == 1) {
				billList.refreshCurrSelectedRow();
			}
		} else {
			MessageBox.show(this, "此状态的数据不可以编辑!");
		}
	}

	private void onDelete() {
		BillVO selectVO = billList.getSelectedBillVO();
		if (selectVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if ("新建".equals(selectVO.getStringValue("state"))) {
			if (MessageBox.confirmDel(this)) {
				try {
					UIUtil.executeBatchByDS(null, new String[] { "delete from " + selectVO.getSaveTableName() + " where id = " + selectVO.getStringValue("id") });
					billList.removeSelectedRow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			MessageBox.show(this, "此状态的数据不可以删除!");
		}
	}

	private void wfStartOrMonitor() {
		BillListPanel list = billList;
		BillVO vo = list.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}

		if (!vo.containsKey("wfprinstanceid")) {
			MessageBox.show(list, "选中的记录中没有定义工作流字段(wfprinstanceid)!"); //
			return; //
		}

		String str_wfprinstanceid = vo.getStringValue("wfprinstanceid"); //
		if (str_wfprinstanceid == null || str_wfprinstanceid.trim().equals("")) {// 如果流程未发起，则发起流程，否则监控流程
			try {
				new cn.com.infostrategy.ui.workflow.WorkFlowDealActionFactory().dealAction("deal", list, null); // 处理动作!f发起流程
				// 发起工作流的同时，将工作流状态改为审批中
				String prinstanceid = UIUtil.getStringValueByDS(null, "select wfprinstanceid from  " + list.templetVO.getTablename() + " where id = " + vo.getStringValue("id"));
				if (prinstanceid == null) // 如果流程并未发起
					return;
				if (prinstanceid.equals(""))
					return;
				else {
					String id = vo.getStringValue("id");
					String sql = "update " + list.getTempletVO().getTablename() + "  set STATE = '审批中' where id = " + id + " and STATE not like '流程结束'";
					UIUtil.executeUpdateByDS(null, sql);
					list.refreshData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {// 流程监控
			cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog wfMonitorDialog = new cn.com.infostrategy.ui.workflow.engine.WorkflowMonitorDialog(list, str_wfprinstanceid, vo); //
			wfMonitorDialog.setMaxWindowMenuBar();
			wfMonitorDialog.setVisible(true);
		}
	}
}
