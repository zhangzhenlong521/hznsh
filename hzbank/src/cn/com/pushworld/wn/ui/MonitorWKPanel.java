package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class MonitorWKPanel  extends AbstractWorkPanel implements ActionListener,BillListHtmlHrefListener{

	private  BillListPanel listPanel;
	private  WLTButton importButton;
	@Override
	public void initialize() {
		listPanel=new  BillListPanel("V_WN_MONITOR_CODE");
		importButton=new WLTButton("µ¼³ö");
		importButton.addActionListener(this);
		listPanel.addBatchBillListButton(new WLTButton[]{importButton});
		listPanel.addBillListHtmlHrefListener(this);
		this.add(listPanel);
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==importButton){// 
			
		}
	}
	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
		
	}
}