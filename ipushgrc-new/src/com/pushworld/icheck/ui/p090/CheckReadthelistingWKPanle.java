package com.pushworld.icheck.ui.p090;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

public class CheckReadthelistingWKPanle extends AbstractWorkPanel implements BillListSelectListener, ActionListener, BillListHtmlHrefListener{

	private BillListPanel listPanel=null;
	public void initialize() {
		listPanel=new BillListPanel("CK_DYQINDAN_CODE1");
		this.add(listPanel);
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void actionPerformed(ActionEvent actionevent) {
		// TODO Auto-generated method stub
		
	}

	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		// TODO Auto-generated method stub
		
	}

}
