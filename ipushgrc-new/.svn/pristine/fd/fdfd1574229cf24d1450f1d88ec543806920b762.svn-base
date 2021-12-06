package com.pushworld.ipushgrc.ui.wfrisk.p010;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class LookSelectedTaskWLTAction implements WLTActionListener {
	BillCardPanel cardPanel = null;

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		cardPanel = (BillCardPanel) _event.getBillPanelFrom();
		BillVO billVO = cardPanel.getBillVO();
		String task = billVO.getStringValue("task");

		if (task == null || "".equals(task)) {
			MessageBox.show(cardPanel, "未选择工作任务!");
			return;
		}

		BillListDialog listdialog = new BillListDialog(cardPanel, "查看工作任务", "CMP_POSTDUTY_CODE1", 700, 500);
		listdialog.getBilllistPanel().QueryDataByCondition("id in(" + new TBUtil().getInCondition(task) + ")");
		listdialog.getBtn_confirm().setVisible(false);
		listdialog.getBtn_cancel().setText("关闭");
		listdialog.getBtn_cancel().setToolTipText("关闭");
		listdialog.setVisible(true);
	}
}