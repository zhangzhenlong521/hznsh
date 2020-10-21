package com.pushworld.ipushgrc.ui.bsd.gagerisk;

import cn.com.infostrategy.ui.mdata.styletemplet.t05.AbstractStyleWorkPanel_05;

/**
 * 标准风险点库
 * @author xch
 *
 */
public class GageRiskWKPanel extends AbstractStyleWorkPanel_05 {

	@Override
	public String getTreeTempeltCode() {
		return "BSD_GAGEWFACT_CODE1";
	}

	@Override
	public String getTableTempletCode() {
		return "BSD_GAGERISK_CODE1";
	}

	@Override
	public String getTreeAssocField() {
		return "id";
	}

	@Override
	public String getTableAssocField() {
		return "gagewfact_id";
	}

	@Override
	public String getUiinterceptor() {
		return null;
	}

	@Override
	public String getBsinterceptor() {
		return null;
	}

	@Override
	public String getCustBtnPanelName() {
		return null;
	}

}
