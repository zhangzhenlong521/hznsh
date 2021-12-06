package com.pushworld.ipushgrc.ui.risk.p120;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * 风险3D统计!!! 即使用Java 3D做的页面!!!
 * @author xch
 *
 */
public class Risk3DStatisWKPanel extends AbstractWorkPanel {

	private Risk3DPanel this3DPanel = null; //

	@Override
	public void initialize() {
		this3DPanel = new Risk3DPanel(); //
		this.add(this3DPanel);
	}

	@Override
	public void beforeDispose() {
		this.this3DPanel.removeAllContent(); //
	}

}
