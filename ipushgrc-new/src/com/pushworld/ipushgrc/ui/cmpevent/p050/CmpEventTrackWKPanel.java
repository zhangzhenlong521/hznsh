package com.pushworld.ipushgrc.ui.cmpevent.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JSplitPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardDialog;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 违规事件跟踪维护!!!
 * @author xch
 * 此类废弃! 采用新的机构, 违规事件->整改方案->整改落实->整改跟踪
 *
 */
public class CmpEventTrackWKPanel extends AbstractWorkPanel implements ActionListener {

	private WLTButton btn_choose_event = null; //选择合规事件按钮 
	private BillCardPanel billCard_event = null; //
	private BillListPanel billList_eventtrack = null; //

	private WLTButton btn_insert, btn_update, btn_delete, btn_list;

	public void initialize() {
		btn_choose_event = new WLTButton("选择违规事件", UIUtil.getImage("refsearch.gif")); //
		billCard_event = new BillCardPanel("CMP_EVENT_CODE2"); //事件
		btn_choose_event.addActionListener(this);
		billCard_event.addBatchBillCardButton(new WLTButton[] { btn_choose_event });
		billCard_event.repaintBillCardButton();
		billList_eventtrack = new BillListPanel("CMP_EVENT_TRACK_CODE1"); //事件跟踪
		btn_update = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT); //
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE); //
		btn_list = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD); //
		btn_insert = new WLTButton("新增");

		btn_insert.addActionListener(this);
		billList_eventtrack.addBatchBillListButton(new WLTButton[] { btn_insert, btn_update, btn_delete, btn_list }); //
		billList_eventtrack.repaintBillListButton(); //刷新按钮!!!
		btn_insert.setEnabled(false);
		btn_update.setEnabled(false);
		btn_list.setEnabled(false);
		btn_delete.setEnabled(false);
		WLTSplitPane split = new WLTSplitPane(JSplitPane.VERTICAL_SPLIT, billCard_event, billList_eventtrack); //
		split.setDividerLocation(380); //
		this.add(split); //
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_choose_event) { //选择合规事件
			onChooseEvent();
		} else if (e.getSource() == btn_insert) { //插入跟踪
			onInsert();
		}
	}

	/**
	 * 选择合规事件
	 */
	public void onChooseEvent() {
		BillListDialog listDialog = new BillListDialog(this, "请选择一条违规事件", "CMP_EVENT_CODE1");
		BillListPanel listpanel = listDialog.getBilllistPanel();
		listpanel.QueryDataByCondition(null);
		listpanel.addBillListButton(WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD));
		listpanel.repaintBillListButton();
		listDialog.setVisible(true);
		if (listDialog.getCloseType() != 1) {
			return;
		}
		BillVO sportVo = listDialog.getReturnBillVOs()[0];
		// 根据主表记录刷新子表
		billCard_event.setBillVO(sportVo);
		billList_eventtrack.removeAllRows();
		billList_eventtrack.QueryDataByCondition(" cmp_event_id = " + sportVo.getStringValue("id"));
		btn_insert.setEnabled(true);
		btn_update.setEnabled(true);
		btn_list.setEnabled(true);
		btn_delete.setEnabled(true);
	}

	/**
	 * 插入跟踪数据
	 */
	public void onInsert() {
		BillVO eventItemVO = billCard_event.getBillVO();
		if (eventItemVO == null || "".equals(eventItemVO.getStringValue("id"))) {
			MessageBox.showSelectOne(this);
			return;
		}
		BillCardPanel cardPanel = new BillCardPanel("CMP_EVENT_TRACK_CODE1");
		cardPanel.insertRow();
		cardPanel.setEditableByInsertInit();
		cardPanel.setValueAt("cmp_event_id", new RefItemVO(eventItemVO.getStringValue("id"), "", eventItemVO.getStringValue("eventname")));
		BillCardDialog insertDialog = new BillCardDialog(this, "新增", cardPanel, WLTConstants.BILLDATAEDITSTATE_INSERT);
		insertDialog.setVisible(true);
		if (insertDialog.getCloseType() != 1) {
			return;
		}
		billList_eventtrack.insertRow(billList_eventtrack.getRowCount(), insertDialog.getBillVO());
	}
}
