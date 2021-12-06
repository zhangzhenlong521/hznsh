package com.pushworld.ipushgrc.ui.wfrisk;

import java.util.HashMap;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.WLTActionEvent;
import cn.com.infostrategy.ui.mdata.WLTActionListener;

public class CmpFileShowHistAsWordAction implements WLTActionListener {

	public void actionPerformed(WLTActionEvent _event) throws Exception {
		BillListPanel billList = (BillListPanel) _event.getBillPanelFrom(); //
		String str_cmpfile_histid = billList.getRealValueAtModel(_event.getRow(), "id"); //取得主键
		String str_webean = "com.pushworld.ipushgrc.bs.wfrisk.WFRiskHistDocViewWebCallBean"; //
		HashMap parMap = new HashMap(); //
		parMap.put("cmpfilehistid", str_cmpfile_histid); //
		UIUtil.openRemoteServerHtml(str_webean, parMap, true); //打开!!
	}

}
