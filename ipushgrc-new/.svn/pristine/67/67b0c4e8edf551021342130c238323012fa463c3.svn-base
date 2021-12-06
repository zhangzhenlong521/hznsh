package com.pushworld.ipushgrc.ui.HR.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * 
 * @author longlonggo521
 *部门培训审查
 */
public class Verify extends AbstractWorkPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel;
	private WLTButton adopt;
	private WLTButton nadopt;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("FN_TRAIN_CODE2");
		adopt = new WLTButton("同意");
		nadopt = new WLTButton("不同意");
		adopt.addActionListener(this);
		nadopt.addActionListener(this);
		listPanel.addBillListButton(adopt);
		listPanel.addBillListButton(nadopt);
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == adopt) {
			try {
				onAdopt();
			} catch (WLTRemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else if (e.getSource() == nadopt) {
			try {
				onNadopt();
			} catch (WLTRemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

	}

	private void onAdopt() throws WLTRemoteException, Exception {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String state = billVO.getStringValue("state");
		if (state.equals("不同意")) {
			MessageBox.show("状态不可变");
			return;
		}else if (state.equals("同意")) {
			MessageBox.show("状态不可变");
			return;
		}
		String sql = "";
		String s = listPanel.getSelectedBillVO().getStringValue("id");
		System.out.println(s + "============================");
		MessageBox mb = new MessageBox();
		boolean b = mb.confirm("确认同意？");
		if (b == true) {
			UIUtil.executeUpdateByDS(null, "update fn_train set state='同意' where id=" + Integer.parseInt(s));
		}
		listPanel.reload();
	}

	private void onNadopt() throws WLTRemoteException, Exception {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String state = billVO.getStringValue("state");
		if (state.equals("同意")) {
			MessageBox.show("状态不可变");
			return;
		}else if (state.equals("不同意")) {
			MessageBox.show("状态不可变");
			return;
		}
		String sql = "";
		String s = listPanel.getSelectedBillVO().getStringValue("id");
		System.out.println(s + "============================");
		MessageBox mb = new MessageBox();
		boolean b = mb.confirm("确认不同意？");
		if (b == true) {
			UIUtil.executeUpdateByDS(null, "update fn_train set state='不同意' where id=" + Integer.parseInt(s));
		}
		listPanel.reload();
	}

}
