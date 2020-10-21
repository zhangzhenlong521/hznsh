package com.pushworld.ipushgrc.ui.HR.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * 
 * @author longlonggo521
 *培训维护
 */
public class Maintain extends AbstractWorkPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel;
	private WLTButton delete;
	private WLTButton change;

	public void initialize() {
		listPanel = new BillListPanel("FN_TRAIN_CODE3");
		delete = new WLTButton("删除");
		change = new WLTButton("修改");
		delete.addActionListener((ActionListener) this);
		change.addActionListener((ActionListener) this);
		listPanel.addBillListButton(delete);
		listPanel.addBillListButton(change);
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == delete) {
			try {
				onDel();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == change) {
			try {
				onChange();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}

	}

	private void onDel() throws WLTRemoteException, NumberFormatException, Exception {

		String userDeptName = ClientEnvironment.getInstance().getLoginUserDeptName();
		String userDeptName1 = userDeptName + ";";
		System.out.println(userDeptName1 + "ttttttttttttttttttttttttttttt");
		BillVO billVO = listPanel.getSelectedBillVO();
		String dName = billVO.getStringViewValue("tradepart");
		System.out.println(dName + "kjkfndsnknsdgkfdsngnfdsngnnfdsgnkfdnsk");	
		if (userDeptName1.equals(dName)) {
			BillVO vo = listPanel.getSelectedBillVO();
			String s = listPanel.getSelectedBillVO().getStringValue("id");
			MessageBox mb = new MessageBox();
			boolean b = mb.confirm("确认删除？");
			if (b == true) {
				UIUtil.executeUpdateByDS(null, "delete from fn_train where id = " + Integer.parseInt(s));
				listPanel.reload();
			}

		} else {
			MessageBox mb = new MessageBox();
			mb.show("只有权限部门才能删除!");
			// delete.setEnabled(true);
		}
	}

	private void onChange() {
		String userDeptName = ClientEnvironment.getInstance().getLoginUserDeptName();
		String userDeptName1 = userDeptName + ";";
		System.out.println(userDeptName1 + "ttttttttttttttttttttttttttttt");
		BillVO billVO = listPanel.getSelectedBillVO();
		String dName = billVO.getStringViewValue("tradepart");
		System.out.println(dName + "kjkfndsnknsdgkfdsngnfdsngnnfdsgnkfdnsk");
		if (userDeptName1.equals(dName)) {

			listPanel.doEdit();
			listPanel.reload();
		} else {
			MessageBox mb = new MessageBox();
			mb.show(this, "只有权限部门才能修改!");
			change.setEnabled(true);
		}
	}
}
