package com.pushworld.ipushgrc.ui.cmpkpi.p040;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * �Ϲ濼��Ԥ��!!!
 * @author xch
 *
 */
public class CmpKpiAlarmWKPanel extends AbstractWorkPanel {
	private BillListPanel billList = null; //
	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_KPI_ALARM_CODE1"); //
		this.add(billList); //
	}

}
