package com.pushworld.ipushgrc.ui.lawcase.p010;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.to.mdata.StringItemVO;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.common.WLTPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

public class ImportLawerInfoDialog extends BillDialog implements ActionListener, BillListSelectListener {
	private WLTButton btn_import, btn_showall, btn_confirm, btn_cancel;
	private BillListPanel billlist_agency, billlist_lawer;
	private HashMap tempMap = new HashMap();
	private HashMap addtempMap = new HashMap();
	private String updateIds=null;
	private BillVO[] newAddVos=null;
	public ImportLawerInfoDialog(Container _parent, String _title, BillVO[] _billvos) {
		super(_parent, _title, 900, 650);
		this.setLayout(new BorderLayout()); //
		int count = 0;
		if (_billvos != null && _billvos.length > 0) {
			count = _billvos.length;
			for (int i = 0; i < _billvos.length; i++) {
				tempMap.put(_billvos[i].getStringValue("id"), _billvos[i]);
			}
		}
		billlist_agency = new BillListPanel("LBS_AGENCY_CODE1");
		billlist_agency.getQuickQueryPanel().setVisiable("AGENCYTYPE", false);
		billlist_agency.setDataFilterCustCondition("agencytype='1'");
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD2); //
		btn_show.setText("浏览");
		btn_show.setPreferredSize(new Dimension(45, 23));
		billlist_agency.addBillListButton(btn_show);
		billlist_agency.repaintBillListButton();
		billlist_agency.addBillListSelectListener(this);

		billlist_lawer = new BillListPanel("LBS_AGENCYUSER_CODE1");
		billlist_lawer.setQuickQueryPanelVisiable(false);
		btn_import = new WLTButton("加入");
		btn_showall = new WLTButton("查看(" + count + ")");
		btn_import.addActionListener(this);
		btn_showall.addActionListener(this);
		billlist_lawer.addBatchBillListButton(new WLTButton[] { btn_import, btn_showall });
		billlist_lawer.repaintBillListButton();

		WLTSplitPane split = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, billlist_agency, billlist_lawer);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(split, BorderLayout.CENTER);
		this.getContentPane().add(getSouthPanel(), BorderLayout.SOUTH);
	}

	/**
	 * 
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btn_import) { //
			onImportLawer();
		} else if (e.getSource() == btn_showall) {
			onShow();
		} else if (e.getSource() == btn_confirm) {
			onConfirm();
		} else if (e.getSource() == btn_cancel) {
			onCanel();
		}
	}

	private void onCanel() {
		this.dispose();
	}

	private JPanel getSouthPanel() {
		JPanel panel = WLTPanel.createDefaultPanel(new FlowLayout()); //
		btn_confirm = new WLTButton("确定");
		btn_cancel = new WLTButton("取消");
		btn_cancel.addActionListener(this); //
		btn_confirm.addActionListener(this);
		panel.add(btn_confirm); //
		panel.add(btn_cancel); //
		return panel;
	}

	private void onImportLawer() {
		try {
			BillVO[] billvos = billlist_lawer.getSelectedBillVOs();
			if (billvos == null||billvos.length==0) {
				MessageBox.showSelectOne(this);
				return;
			}
			StringBuffer nameSameSB=new StringBuffer();
			StringBuffer nameAddSB=new StringBuffer();
			boolean isAdd=false;
			for (int i = 0; i < billvos.length; i++) {
				String id = billvos[i].getStringValue("id");
				String name = billvos[i].getStringValue("name");
				if (tempMap.containsKey(id) || addtempMap.containsKey(id)) {
					nameSameSB.append(name+";");
					continue;
				}
				BillVO billvo1 = null;
				if (billlist_agency.getRowCount() > 0) {
					billvo1 = billlist_agency.getBillVO(0);
				} else {
					int a = billlist_agency.addEmptyRow();
					billvo1 = billlist_agency.getBillVO(a);
					billlist_agency.removeRow();
				}
				billvo1.setObject("id", new StringItemVO(id));
				billvo1.setObject("name", new StringItemVO(name));//AGENCY_ID
				//billvo1.setObject("name", new RefItemVO(name))
				addtempMap.put(id, billvos[i]);
				nameAddSB.append(name+";");
			}
			btn_showall.setText("查看(" + (tempMap.size() + addtempMap.size()) + ")");
			StringBuffer alertInfoSB=new StringBuffer();
			if(nameSameSB.length()!=0){
				String namestr=nameSameSB.toString();
				alertInfoSB.append("律师["+namestr.substring(0, namestr.length()-1)+"]已加入，请勿重复加入!");
				alertInfoSB.append("\r\n");
			}
			if(nameAddSB.length()!=0){
				String namestr=nameAddSB.toString();
				alertInfoSB.append("加入成功：["+namestr.substring(0, namestr.length()-1)+"]");
			}
			MessageBox.show(this,alertInfoSB.toString());
		
		} catch (Exception e1) {
			MessageBox.showException(this, e1);
		}
	}

	private void onShow() {
		BillListDialog billdialog = new BillListDialog(this, "所有相关律师", "LBS_AGENCYUSER_CODE1", 850, 550);
		BillListPanel billlist_show = billdialog.getBilllistPanel();
		WLTButton btn_show = WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD2); //
		btn_show.setText("浏览");
		btn_show.setPreferredSize(new Dimension(45, 23));
		billlist_show.addBillListButton(btn_show);
		billlist_show.repaintBillListButton();
		billlist_show.setQuickQueryPanelVisiable(false);
		Iterator it1 = tempMap.keySet().iterator();
		Iterator it2 = addtempMap.keySet().iterator();
		while (it1.hasNext()) {
			BillVO billvo = (BillVO) tempMap.get(it1.next());
			billlist_show.addRow(billvo);
		}
		while (it2.hasNext()) {
			BillVO billvo = (BillVO) addtempMap.get(it2.next());
			billlist_show.addRow(billvo);
		}
		billdialog.getBtn_confirm().setVisible(false);
		billdialog.getBtn_cancel().setText("关闭");
		billdialog.setVisible(true);
	}

	private void onConfirm() {
		if (addtempMap.keySet().size() != 0) {
			newAddVos = (BillVO[]) addtempMap.values().toArray(new BillVO[0]);
		}
		this.setCloseType(1);
		this.dispose();
	}

	public void onBillListSelectChanged(BillListSelectionEvent _event) {
		BillVO billvo = billlist_agency.getSelectedBillVO();
		if (billvo == null) {
			MessageBox.showSelectOne(this);
			return;
		}
		billlist_lawer.QueryDataByCondition("AGENCY_ID =" + billvo.getStringValue("id"));
	}

	public BillVO[] getNewAddBillVOs() {
		return this.newAddVos;
	}
}
