package com.pushworld.ipushgrc.ui.cmpevent.p160;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListPanel;
/**
 * 整改方案落实
 * @author hj
 * Apr 13, 2012 4:43:09 PM
 */
public class CmpAdjustStepWKPanel extends AbstractWorkPanel implements ActionListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7715838928817255055L;
	private BillListPanel mainList=null;
	private WLTButton btn_carryout=null;
	private WLTButton btn_view=null;

	public void initialize() {
		mainList=new BillListPanel("V_CMP_EVENT_ADJUSTSTEP_CODE1");
		
		btn_carryout=WLTButton.createButtonByType(WLTButton.LIST_POPEDIT, "整改落实");
		btn_view=WLTButton.createButtonByType(WLTButton.LIST_SHOWCARD, "浏览");
		
		mainList.addBatchBillListButton(new WLTButton[]{btn_carryout,btn_view});
		mainList.repaintBillListButton();
		this.add(mainList);
	}
	
	
	public void actionPerformed(ActionEvent e) {
		
		
	}

}
