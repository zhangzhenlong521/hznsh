package com.pushworld.ipushgrc.ui.HR.p020;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * 
 * @author longlonggo521
 * 员工培训需求统计
 */
public class Query extends AbstractWorkPanel implements ActionListener {

	private BillListPanel listPanel;
	private WLTButton query;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("FN_PERSONALTRA_CODE2");
		query = new WLTButton("申请统计");
		query.addActionListener(this);
		listPanel.addBillListButton(query);
		listPanel.repaintBillListButton();
		this.add(listPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == query) {

			try {
				onCount();
			} catch (WLTRemoteException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
	}

	private void onCount() throws WLTRemoteException, Exception {
		BillVO billVO = listPanel.getSelectedBillVO();
		listPanel.QueryDataByCondition("state='申请中'");

	}

}
