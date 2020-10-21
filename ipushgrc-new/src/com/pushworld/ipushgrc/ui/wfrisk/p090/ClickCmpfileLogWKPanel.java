package com.pushworld.ipushgrc.ui.wfrisk.p090;

import java.awt.BorderLayout;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ClickCmpfileLogWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.setLayout(new BorderLayout());
		this.add(new BillListPanel("CMP_CMPFILE_CLICKLOG_CODE1"));
	}
}
