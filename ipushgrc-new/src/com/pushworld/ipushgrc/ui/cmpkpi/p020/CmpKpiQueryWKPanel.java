package com.pushworld.ipushgrc.ui.cmpkpi.p020;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * �Ϲ濼�� ��ѯ!!!
 * @author xch
 *
 */
public class CmpKpiQueryWKPanel extends AbstractWorkPanel {

	private BillListPanel billList; //

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_KPI_CODE1"); //
		this.add(billList);  //
	}

}
