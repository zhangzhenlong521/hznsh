package com.pushworld.ipushgrc.ui.duty.p020;

import java.awt.Container;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 通过看岗责和流程环节操作要求相关页面，来修改岗责的工作流程与要求、操作指南、最佳实践
 * @author hm
 *
 */
public class PostDutyDetailWKDialog extends BillDialog implements BillListSelectListener {
	BillVO postvo = null;
	BillListPanel listPanel_0 = null;
	BillListPanel listPanel_1 = null;

	public PostDutyDetailWKDialog(Container _parent, String _title, int _width, int li_height, BillVO vo) {
		super(_parent, _title, _width, li_height);
		postvo = vo;
		init();
	}

	public void init() {
		listPanel_0 = new BillListPanel("CMP_POSTDUTY_CODE1");
		WLTButton btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		listPanel_0.addBatchBillListButton(new WLTButton[] { btn_update });
		listPanel_0.repaintBillListButton();
		listPanel_0.addBillListSelectListener(this);
		listPanel_0.QueryDataByCondition(" postid = " + postvo.getStringValue("id"));
		listPanel_0.setDataFilterCustCondition(" postid = " + postvo.getStringValue("id"));
		listPanel_1 = new BillListPanel("CMP_CMPFILE_WFOPEREQ_CODE1");
		WLTSplitPane splitePane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT);
		splitePane.setDividerLocation(180);
		splitePane.add(listPanel_0, WLTSplitPane.TOP);
		splitePane.add(listPanel_1, WLTSplitPane.BOTTOM);
		this.add(splitePane);

	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO vo = listPanel_0.getSelectedBillVO();
		if (vo != null) {
			listPanel_1.QueryDataByCondition(" task like '%;" + vo.getStringValue("id") + ";%'");
		}
	}
}
