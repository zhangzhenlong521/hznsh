package com.pushworld.ipushgrc.ui.rule.p050;

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
 * 制度评价回复的窗口!! 要模仿BBS效果,即关键是行高!!
 * 
 * @author xch
 * 
 */
public class RuleEvalRevertDialog extends BillDialog implements ActionListener, BillListSelectListener {

	private BillListPanel billList_evel_b; //

	private BillCardPanel billCard_evel_b;

	private String ruleEvalid;

	private WLTButton btn_cancel = WLTButton.createButtonByType(WLTButton.COMM, "返回");

	private WLTButton btn_revert = WLTButton.createButtonByType(WLTButton.COMM, "回复");

	private WLTButton btn_update = new WLTButton("修改");

	private WLTButton btn_delete = WLTButton.createButtonByType(WLTButton.LIST_DELETE);
	
	private WLTButton btn_view = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD);

	private String loginUserID = ClientEnvironment.getCurrSessionVO().getLoginUserId(); // 登陆人员ID

	private RoleVO [] roles = ClientEnvironment.getCurrLoginUserVO().getRoleVOs();
	
	private boolean isRuleAdmin = false; //是否是制度管理员。。。这里暂时不控制 制度的归口部门了。
	public RuleEvalRevertDialog(Container _parent, String _ruleId, int _width, int _height) {
		super(_parent, "制度回复--新增状态", _width, _height); //
		ruleEvalid = _ruleId;
		initialize(); //
	}

	private void initialize() {
		for (int i = 0; i < roles.length; i++) {
			if(roles[i].getName()!=null && roles[i].getName().contains("制度管理员")){
				isRuleAdmin = true;
				break;
			}
		}
		billList_evel_b = new BillListPanel("RULE_EVAL_B_CODE1"); //
		btn_update.addActionListener(this);
		btn_delete.addActionListener(this);
		billList_evel_b.queryDataByCondition(" rule_eval_id = " + ruleEvalid, " revertdate asc");
		billList_evel_b.setDataFilterCustCondition(" rule_eval_id = " + ruleEvalid);
		billList_evel_b.setOrderCustCondition(" revertdate asc ");
		if(isRuleAdmin){
			billList_evel_b.addBatchBillListButton(new WLTButton[] { btn_update, btn_delete ,btn_view});
			billList_evel_b.repaintBillListButton();
			billList_evel_b.addBillListSelectListener(this);
			billCard_evel_b = new BillCardPanel("RULE_EVAL_B_CODE1");
			billCard_evel_b.insertRow(); // 初始化面板时候默认是新增回复！
			billCard_evel_b.setEditableByEditInit();
			billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT);
			WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billList_evel_b, billCard_evel_b);
			split.setDividerLocation(280);
			WLTPanel btnpanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, false);
			btn_revert.addActionListener(this);
			btn_cancel.addActionListener(this);
			btnpanel.add(btn_revert);
			btnpanel.add(btn_cancel);
			billCard_evel_b.setValueAt("rule_eval_id", new StringItemVO(ruleEvalid));
			this.getContentPane().add(split, BorderLayout.CENTER); //
			this.getContentPane().add(btnpanel, BorderLayout.SOUTH); //
		}else{
			billList_evel_b.addBatchBillListButton(new WLTButton[]{btn_view});
			billList_evel_b.repaintBillListButton();
			WLTPanel btnpanel = new WLTPanel(WLTPanel.HORIZONTAL_LEFT_TO_RIGHT, new FlowLayout(), LookAndFeel.defaultShadeColor1, false);
			btnpanel.add(btn_cancel);
			btn_cancel.addActionListener(this);
			this.getContentPane().add(billList_evel_b, BorderLayout.CENTER); //
			this.getContentPane().add(btnpanel, BorderLayout.SOUTH); //
		}
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
				billList_evel_b.setSelectedRow(billList_evel_b.getRowCount() - 1);
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
			this.setTitle("制度回复");
			billCard_evel_b.insertRow();
			billCard_evel_b.setValueAt("rule_eval_id", new StringItemVO(ruleEvalid));
			billCard_evel_b.setEditableByInsertInit();
			billCard_evel_b.setEditState(WLTConstants.BILLDATAEDITSTATE_INSERT); // 新增或者修改完，默认为选择状态。
		} else {
			this.setTitle("制度回复--修改");
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
				UIUtil.executeBatchByDS(null, new String[] { "delete from rule_eval_b where id = '" + billList_evel_b.getSelectedBillVO().getStringValue("id") + "'" });
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
