package cn.com.infostrategy.ui.mdata.styletemplet;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class PubRegistButtonWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		BillListPanel billList = new BillListPanel("pub_regbuttons_CODE1"); //
		this.add(billList); //
	}

}
