package com.pushworld.ipushgrc.ui.lawcase.p010;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListButtonActinoListener;
import cn.com.infostrategy.ui.mdata.BillListButtonClickedEvent;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.cardcomp.CardCPanel_ChildTableImport;

public  class LawyerImportWorkPanel extends AbstractWorkPanel implements BillListButtonActinoListener , ActionListener {
	public CardCPanel_ChildTableImport childtable;
	public void onBillListAddButtonClicked(BillListButtonClickedEvent _event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void onBillListAddButtonClicking(BillListButtonClickedEvent _event) throws Exception {
		childtable = (CardCPanel_ChildTableImport) _event.getCardPanel().getCompentByKey("LAWERINFO");
		childtable.getBtn_import().addActionListener(this);
	}

	public void onBillListButtonClicked(BillListButtonClickedEvent _event) throws Exception {
		// TODO Auto-generated method stub

	}

	public void onBillListDeleteButtonClicked(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListDeleteButtonClicking(BillListButtonClickedEvent _event) throws Exception {
		
	}

	public void onBillListEditButtonClicked(BillListButtonClickedEvent _event) {
		
	}

	public void onBillListEditButtonClicking(BillListButtonClickedEvent _event) throws Exception {
		childtable = (CardCPanel_ChildTableImport) _event.getCardPanel().getCompentByKey("LAWERINFO");
		childtable.getBtn_import().addActionListener(this);
	}

	public void onBillListLookAtButtonClicked(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void onBillListLookAtButtonClicking(BillListButtonClickedEvent _event) {
		// TODO Auto-generated method stub

	}

	public void actionPerformed(ActionEvent e) {
		onImportLawerInfo();
	}

	private void onImportLawerInfo() {
		BillListPanel listpanel=childtable.getBillListPanel();
		ImportLawerInfoDialog importlawer = new ImportLawerInfoDialog(this, "导入律师", listpanel.getBillVOs());
		importlawer.setVisible(true);
		if(importlawer.getCloseType()==1){
			BillVO[] newaddvos=importlawer.getNewAddBillVOs();
			if(newaddvos!=null){
				listpanel.addBillVOs(newaddvos);
				int li_rows = listpanel.getRowCount(); //高度重设
				int li_height = 60; //
				int li_minHeight = 85; //
				if (listpanel.getTitlePanel().isVisible()) {
					li_height = li_height + 22;
					li_minHeight = li_minHeight + 22;
				}
				for (int i = 0; i < li_rows; i++) {
					li_height = li_height + listpanel.getTable().getRowHeight(i); //
				}
				if (li_height < li_minHeight) {
					li_height = li_minHeight; //
				}
				int li_width = (int) childtable.getPreferredSize().getWidth(); //
				childtable.setPreferredSize(new Dimension(li_width, li_height)); //
				childtable.updateUI(); //
			}
		}
	}

	public  void initialize(){
		
	}
}
