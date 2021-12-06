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
 *部门培训申请
 */
public class Submit extends AbstractWorkPanel implements ActionListener {
	private BillListPanel listPanel;
	private WLTButton submit;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("FN_TRAIN_CODE1");
		submit = new WLTButton("提交");
		submit.addActionListener(this);
		listPanel.addBillListButton(submit);
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == submit) {

			try {
				onSubmit();
			} catch (WLTRemoteException e1) {

				e1.printStackTrace();
			} catch (Exception e1) {

				e1.printStackTrace();
			}

		}
	}

	private void onSubmit() throws WLTRemoteException, Exception {
		BillVO billVO = listPanel.getSelectedBillVO();
		if (billVO == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		String sql = "";
		String s = listPanel.getSelectedBillVO().getStringValue("id");
		System.out.println(s + "============================");
		MessageBox mb = new MessageBox();
		boolean b = mb.confirm("确认提交？");
		if (b == true) {
			UIUtil.executeUpdateByDS(null, "update fn_train set state='申请中' where id=" + Integer.parseInt(s));
		}
		listPanel.reload();
	}

}
