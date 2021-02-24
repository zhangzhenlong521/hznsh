package com.pushworld.ipushgrc.ui.icheck.p040;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.mdata.BillCardPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_Button;

/**
 * 检查实施子表-逐条录入
 * @author scy
 *
 */
public class BillCardDialog_CheckIn extends BillDialog implements ActionListener {

	/**
	 *  检查底稿录入
	 */
	private static final long serialVersionUID = 1L;
	public BillCardPanel billcardPanel;
	protected WLTButton btn_confirm, btn_cancel, btn_up, btn_next;
	private int row = 0;
	private BillVO[] billvo;
	private BillListPanel implList, itemList;
	private boolean isClickSaveButton = false;

	public BillCardDialog_CheckIn(BillListPanel _implList, BillListPanel _listpanel, String _code, String title) {
		super(_listpanel, title, 650, 600); //
		this.getContentPane().setLayout(new BorderLayout());
		billcardPanel = new BillCardPanel(_code);
		billcardPanel.setEditableByEditInit();
		implList = _implList;
		itemList = _listpanel;

		billvo = itemList.getAllBillVOs();
		row = itemList.getSelectedRow();
		if (row < 0) {
			itemList.setSelectedRow(0);
		}
		setCardValue(itemList.getSelectedBillVO());
		onBillListSelect_Job();

		billcardPanel.setEditState(WLTConstants.BILLDATAEDITSTATE_UPDATE);
		if (billcardPanel.getCompentByKey("rule_search") != null) {
			JButton btnRuleSearch = ((CardCPanel_Button) billcardPanel.getCompentByKey("rule_search")).getButtontn();
			btnRuleSearch.setText("");
			btnRuleSearch.setIcon(UIUtil.getImage("zoomnormal.gif"));
			btnRuleSearch.setPreferredSize(new Dimension(24, 50));
		}
		this.getContentPane().add(billcardPanel, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
		locationToCenterPosition();
	}

	/**
	 * 在窗口关闭前做!!!
	 */
	public void closeMe() {
		itemList.refreshCurrSelectedRow();
		if (isClickSaveButton) {
			try {
				UIUtil.executeUpdateByDS(null, "update ck_scheme_impl set status='执行中' where id =" + billcardPanel.getBillVO().getStringValue("implid"));
			} catch (WLTRemoteException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			implList.refreshCurrSelectedRow();
		}
		this.dispose();
	}

	// 查询问题数量
	private void onBillListSelect_Job() {
		BillVO vo = itemList.getSelectedBillVO();
		String schemeid = vo.getStringValue("schemeid");
		String deptid = vo.getStringValue("deptid");
		String parentid = vo.getStringValue("id");

		try {
			String count = UIUtil.getStringValueByDS(null, "select count(id)  from  ck_problem_info where schemeid = '" + schemeid + "' and deptid = '" + deptid + "' and parentid = '" + parentid + "'    ");
			if (null != count && !"0".equals(count)) {
				((CardCPanel_Button) billcardPanel.getCompentByKey("btn_insert")).getButtontn().setForeground(Color.RED);
				((CardCPanel_Button) billcardPanel.getCompentByKey("btn_insert")).getButtontn().setText("问题录入(" + count + ")");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout());
		btn_confirm = new WLTButton("确定");
		btn_confirm.setIcon(UIUtil.getImage("office_175.gif"));
		btn_cancel = new WLTButton("取消");
		btn_cancel.setIcon(UIUtil.getImage("zt_031.gif"));

		btn_up = new WLTButton("上一条");
		btn_up.setIcon(UIUtil.getImage("zt_072.gif"));

		btn_next = new WLTButton("下一条");
		btn_next.setIcon(UIUtil.getImage("zt_073.gif"));
		//
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this); //
		btn_up.addActionListener(this); //
		btn_next.addActionListener(this); //

		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		panel.add(btn_up); //
		panel.add(btn_next); //
		setWltButton(btn_confirm);
		setWltButton(btn_cancel);
		setWltButton(btn_up);
		setWltButton(btn_next);
		return panel;
	}

	private void setWltButton(WLTButton _btn) {
		_btn.setMargin(new Insets(0, 0, 0, 0)); //
		_btn.setPreferredSize(new Dimension(80, btn_up.BTN_HEIGHT));
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_up) {
			onUp();
		} else if (e.getSource() == btn_next) {
			onNext();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCancel();
		}
		itemList.setSelectedRow(row);
	}

	public boolean onSave() {
		try {
			billcardPanel.stopEditing(); //
			if (!billcardPanel.checkValidate()) {
				return false;
			}
			updateData();
			isClickSaveButton = true;
			return true;
		} catch (Exception ex) {
			MessageBox.showException(this, ex);
		} //
		return false;
	}

	// 
	public void onConfirm() {
		try {
			if (!onSave()) {
				return; //如果校验失败
			}
			closeType = 1;
			itemList.setBillVOAt(itemList.getSelectedRow(), billcardPanel.getBillVO(), false); //
			itemList.setRowStatusAs(itemList.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
			itemList.saveData();
			MessageBox.show(this, "保存数据成功!"); //
			closeMe();
		} catch (Exception e) {
			MessageBox.showException(this, e);
		}
	}

	public void onCancel() {
		if (isClickSaveButton) {
			closeType = 1;
		} else {
			closeType = 2;
		}
		closeMe();
	}

	public void onUp() {
		if (row == 0) {
			MessageBox.show("没有可以显示的记录！");
			return;
		}
		this.billcardPanel.stopEditing();
		try {
			if (!onSave()) {
				return;
			}
			billvo[itemList.getSelectedRow()] = billcardPanel.getBillVO();
			itemList.refreshCurrSelectedRow();
			itemList.setBillVOAt(itemList.getSelectedRow(), billcardPanel.getBillVO(), false); //
			itemList.setRowStatusAs(itemList.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setCardValue(billvo[row - 1]);
		row--;

	}

	public void onNext() {
		if (row == billvo.length - 1) {
			MessageBox.show("没有可以显示的记录！");
			return;
		}
		try {
			if (!onSave()) {
				return;
			}
			billvo[itemList.getSelectedRow()] = billcardPanel.getBillVO();
			itemList.refreshCurrSelectedRow();
			itemList.setBillVOAt(itemList.getSelectedRow(), billcardPanel.getBillVO(), false); //
			itemList.setRowStatusAs(itemList.getSelectedRow(), WLTConstants.BILLDATAEDITSTATE_INIT);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		setCardValue(billvo[row + 1]);
		row++;
	}

	//这个地方是给面板复制的唯一方法，里面会做一些处理。
	private void setCardValue(BillVO _value) {
		this.billcardPanel.setBillVO(_value);
		if (_value.getStringValue("rules_id") == null || "".equals(_value.getStringValue("rules_id"))) { //如果没有制度内容,设置提示值
			billcardPanel.setValueAt("rules_id", new StringItemVO("格式为:《制度名称》(制度文号)换行"));
		}
	}

	private void updateData() {
		String sql = billcardPanel.getUpdateSQL().substring(0, billcardPanel.getUpdateSQL().indexOf("where")) + "  where id='" + billcardPanel.getBillVO().getStringValue("id") + "'";
		BillVO billVO = billcardPanel.getBillVO();
		try {
			UIUtil.executeUpdateByDS(null, sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
