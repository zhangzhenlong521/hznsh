package com.pushworld.ipushlbs.ui.lawconsult.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ·¨ÂÉ×ÉÑ¯²éÑ¯
 * 
 * @author yinliang
 * 
 */
public class LawConsultQueryWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billlist = new BillListPanel("COMM_QUESSTION_CODE2");
		this.add(billlist);
	}

}
