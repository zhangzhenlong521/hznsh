package com.pushworld.ipushgrc.ui.cmpevent.p150;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 整改方案制定
 * @author hj
 * Apr 13, 2012 4:42:49 PM
 */
public class CmpAdjustProjectWKPanel extends AbstractWorkPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3357086488556077880L;
	/***********************/
	private BillListPanel listPanel;
	private WLTButton btn_add, btn_edit, btn_delete, btn_confirm;

	/***********************/
	public void initialize() {
		listPanel = new BillListPanel("CMP_EVENT_ADJUSTPROJECT_CODE1");
		btn_add = WLTButton.createButtonByType(WLTButton.LIST_POPINSERT, "创建方案");
		btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
		btn_delete.addActionListener(this);
		btn_edit = WLTButton.createButtonByType(WLTButton.LIST_POPEDIT);
		btn_edit.addActionListener(this);
		btn_confirm = new WLTButton("开始整改");
		btn_confirm.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[] { btn_add, btn_edit, btn_delete, btn_confirm });
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == btn_edit) {
			onEdit();
		} else if (source == btn_delete) {
			onDelete();
		} else if (btn_confirm == btn_confirm) {
			onConfirm();
		}
	}

	public void onEdit() {
		BillVO vo = listPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!"未整改".equals(vo.getStringValue("status"))) {
			MessageBox.showNotAllow(this, vo.getStringValue("STATUS"));
			return;
		}
		listPanel.doEdit();
	}

	public void onDelete() {
		BillVO vo = listPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!"未整改".equals(vo.getStringValue("status"))) {
			MessageBox.showNotAllow(this, vo.getStringValue("STATUS"));
			return;
		}
		if (MessageBox.confirmDel(this)) {
			String sql1 = "delete from CMP_EVENT_ADJUSTPROJECT where id = " + vo.getPkValue();
			String sql2 = "delete from CMP_EVENT_ADJUSTSTEP where PROJECTID = " + vo.getPkValue();
			try {
				UIUtil.executeBatchByDS(null, new String[] { sql1, sql2 });
				listPanel.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void onConfirm() {
		BillVO vo = listPanel.getSelectedBillVO();
		if (vo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (!"未整改".equals(vo.getStringValue("status"))) {
			MessageBox.showNotAllow(this, vo.getStringValue("STATUS"));
			return;
		}
		if (MessageBox.confirm(this, "您确定要[开始整改]吗?")) {
			String sql1 = "update CMP_EVENT_ADJUSTPROJECT set status='整改中' where id = " + vo.getPkValue();
			String sql2 = "update CMP_EVENT set adjustresulttype='整改中' where id = " + vo.getStringValue("eventid");
			try {
				UIUtil.executeBatchByDS(null, new String[] {sql1, sql2});
				listPanel.refreshCurrSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
