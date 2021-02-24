package com.pushworld.icheck.ui.p090;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;
import cn.com.infostrategy.ui.mdata.BillListSelectionEvent;

public class CheckImplementXDWKPanel extends AbstractWorkPanel implements BillListSelectListener{
	private BillListPanel implList = new BillListPanel("CK_SCHEME_IMPL_E02");
	private BillListPanel schemeList = new BillListPanel("V_CK_SCHEME_LCJ_Q01");
	private BillListPanel itemList = new BillListPanel("V_CK_SCHEME_IMPLEMENT_SCY_E01");
	private String implid=null;
	private Object ob=null;
	
	public CheckImplementXDWKPanel(Object ob,String implid){
		this.implid=implid;
		this.ob=ob;
	}

	@Override
	public void initialize() {
		WLTSplitPane splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, schemeList, implList);
		splitPane.setDividerLocation(540);
		WLTSplitPane splitPane2 = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, splitPane, itemList);
		splitPane2.setDividerLocation(250);
		this.add(splitPane2);
		
	}

	public void onBillListSelectChanged(BillListSelectionEvent event) {
		// TODO Auto-generated method stub
		
	}

}
