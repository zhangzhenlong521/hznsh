package com.pushworld.ipushgrc.ui.risk.p120;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * ����3Dͳ��!!! ��ʹ��Java 3D����ҳ��!!!
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
