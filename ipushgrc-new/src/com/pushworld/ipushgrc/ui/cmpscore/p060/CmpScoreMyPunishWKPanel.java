package com.pushworld.ipushgrc.ui.cmpscore.p060;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class CmpScoreMyPunishWKPanel extends AbstractWorkPanel {

	private BillListPanel listPanel = null;
	
	@Override
	public void initialize() {
		listPanel = new BillListPanel("CMP_SCORE_PUNISH_CODE1");
		String userID = ClientEnvironment.getInstance().getLoginUserID();
		listPanel.setDataFilterCustCondition("userid = " + userID);
		listPanel.QueryDataByCondition(null);
		
		this.add(listPanel);

	}

}
