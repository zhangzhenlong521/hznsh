package cn.com.infostrategy.ui.workflow;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class WorkFlowDynPartiDefineWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billList = new BillListPanel("pub_wf_dypardefines_CODE1"); //
		this.add(billList);  //
	}

}
