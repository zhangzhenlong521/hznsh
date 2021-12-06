package com.pushworld.ipushgrc.ui.wfrisk.p091;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.to.sysapp.login.RoleVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.LookAndFeel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

/**
 * 流程文件评价回复的窗口!! 要模仿BBS效果,即关键是行高!!
 * 
 * @author xch
 * 
 */
public class CmpFileEvalRevertDialog extends BillDialog implements ActionListener, BillListSelectListener {

	private BillListPanel billList_evel_b; //

	private BillCardPanel billCard_evel_b;

	private String CMPFILE_EVAL_ID;

	private WLTButton btn_cancel = WLTButton.createButtonByType(WLTButton.COMM, "返回");

	private WLTButton btn_revert = WLTButton.createButtonByType(WLTButton.COMM, "回复");

	private WLTButton btn_update = new WLTButton("修改");

	private WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);

	private String loginUserID = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 登陆人员ID

	private RoleVO[] roles = ClientEnvironment.getCurrLoginUserVO().getRoleVOs();

	public CmpFileEvalRevertDialog(Container _parent, String file_eva_id, int _width, int _height) {
		super(_parent, "回复--新增状态", _width, _height); //
		CMPFILE_EVAL_ID = file_eva_id;
		initialize(); //
	}

	private void initialize() {
		billList_evel_b = new BillListPanel("CMPFILE_EVAL_REPLY_CODE1"); //
		btn_update.addActionListener(this);
		btn_delete.addActionListener(this);
		billList_evel_b.addBatchBillListButton(new WLTButton[] { btn_update, btn_delete });
		billList_evel_b.repaintBillListButton();
		billList_evel_b.queryDataByCondition(" cmpfile_eval_id = " + CMPFILE_EVAL_ID, " revertdate asc");
		billList_evel_b.setDataFilterCustCondition(" cmpfile_eval_id = " + CMPFILE_EVAL_ID);
		billList_evel_b.setOrderCustCondition(" revertdate asc ");
		billList_evel_b.addBillListSelectListener(this);
		billCard_evel_b = new BillCardPanel("CMPFILE_EVAL_REPLY_CODE1");
		billCard_evel_b.insertRow(); // 初始化面板时候默认是新增回复！
		billCard_evel_b.setEditableByEditInit();
		billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_evel_b, billCard_evel_b);
		split.setDividerLocation(280);
		WLTPanel btnpanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, false);
		btn_revert.addActionListener(this);
		btn_cancel.addActionListener(this);
		btn_revert.setEnabled(false);
		btn_revert.setEnabled(true);
		btnpanel.add(btn_revert);
		btnpanel.add(btn_cancel);
		billCard_evel_b.setValueAt("cmpfile_eval_id", new StringItemVO(CMPFILE_EVAL_ID));
		this.getContentPane().add(split, BorderLayout.CENTER); //
		this.getContentPane().add(btnpanel, BorderLayout.SOUTH); //
	}

	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == btn_cancel) {
			this.dispose();
		} else if (obj == btn_revert) { // 回复！
			onRevert();
		} else if (obj == btn_update) { // 修改回复！！
			onUpdate();
		} else if (obj == btn_delete) { // 删除回复！
			onDelete();
		}
	}

	/*
	 * 修改评价的回复！
	 */
	private void onUpdate() {
		if (billList_evel_b.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		setBillCardState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
	}

	/*
	 * 回复制度评价
	 */
	private void onRevert() {
		try {
			if (!billCard_evel_b.checkValidate()) {
				return;
			}
			if (billCard_evel_b.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) {
				billCard_evel_b.updateData(); // 此条执行完会把BillCardPanel状态设置为update。所有放入里面更新数据
				billList_evel_b.refreshCurrSelectedRow();
				MessageBox.show(this, "修改成功！");
			} else {
				billCard_evel_b.updateData();
				billList_evel_b.insertRow(billList_evel_b.getRowCount(), billCard_evel_b.getBillVO());

				//billList_evel_b.setSelectedRow(billList_evel_b.getRowCount() - 1);
				billList_evel_b.refreshData();
				MessageBox.show(this, "回复成功！");
			}
			setBillCardState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	// 设置卡片状态。
	public void setBillCardState(String type) {
		if (type.equals(WLTConstants.BILLDATAEDITSTATE_INSERT)) {
			this.setTitle("流程文件回复");
			billCard_evel_b.insertRow();
			billCard_evel_b.setValueAt("cmpfile_eval_id", new StringItemVO(CMPFILE_EVAL_ID));
			billCard_evel_b.setEditableByInsertInit();
			billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); // 新增或者修改完，默认为选择状态。
		} else {
			this.setTitle("流程文件回复--修改");
			billCard_evel_b.setBillVO(billList_evel_b.getSelectedBillVO());
			billCard_evel_b.setEditableByEditInit();
			billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		}
	}

	private void onDelete() {
		if (billList_evel_b.getSelectedBillVO() == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		if (MessageBox.showConfirmDialog(this, "你确定要删除此记录吗？") == JOptionPane.YES_OPTION) {
			try {
				UIUtil.executeBatchByDS(null, new String[] { "delete from cmpfile_eval_reply where id = '" + billList_evel_b.getSelectedBillVO().getStringValue("id") + "'" });
				billList_evel_b.removeSelectedRow();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (billCard_evel_b.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) { // 如果是更新状态，需要删除掉数据
			setBillCardState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		}
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		if (_event.getCurrSelectedVO() == null) {
			return;
		}
		String selectUserID = _event.getCurrSelectedVO().getStringValue("revertuserid");
		if (billCard_evel_b.getEditState().equals(WLTConstants.BILLDATAEDITSTATE_UPDATE)) { // 如果是更新状态，可能有数据存在。需清空
			setBillCardState(WLTConstants.BILLDATAEDITSTATE_INSERT);
		} else { // 如果是insert状态 那么不需要做任何操作。 否则ID会增加

		}
		if (loginUserID.equals(selectUserID)) {
			btn_update.setEnabled(true);
			btn_delete.setEnabled(true);
		} else {
			btn_update.setEnabled(false);
			btn_delete.setEnabled(false);
		}
	}

	public BillListPanel getBillListPanel() {
		return billList_evel_b;
	}

}
