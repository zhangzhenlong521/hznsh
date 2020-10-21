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
 *������ѵ���
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
		adopt = new WLTButton("ͬ��");
		nadopt = new WLTButton("��ͬ��");
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
		if (state.equals("��ͬ��")) {
			MessageBox.show("״̬���ɱ�");
			return;
		}else if (state.equals("ͬ��")) {
			MessageBox.show("״̬���ɱ�");
			return;
		}
		String sql = "";
		String s = listPanel.getSelectedBillVO().getStringValue("id");
		System.out.println(s + "============================");
		MessageBox mb = new MessageBox();
		boolean b = mb.confirm("ȷ��ͬ�⣿");
		if (b == true) {
			UIUtil.executeUpdateByDS(null, "update fn_train set state='ͬ��' where id=" + Integer.parseInt(s));
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
		if (state.equals("ͬ��")) {
			MessageBox.show("״̬���ɱ�");
			return;
		}else if (state.equals("��ͬ��")) {
			MessageBox.show("״̬���ɱ�");
			return;
		}
		String sql = "";
		String s = listPanel.getSelectedBillVO().getStringValue("id");
		System.out.println(s + "============================");
		MessageBox mb = new MessageBox();
		boolean b = mb.confirm("ȷ�ϲ�ͬ�⣿");
		if (b == true) {
			UIUtil.executeUpdateByDS(null, "update fn_train set state='��ͬ��' where id=" + Integer.parseInt(s));
		}
		listPanel.reload();
	}

}
