package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl ��Ա������־ 2019-7-4-����02:55:56
 */
public class TurnoverLogWKPanel extends AbstractWorkPanel {
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("HR_RECORDPOST_CODE1");
		this.add(list);

	}

}
