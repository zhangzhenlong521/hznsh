package com.pushworld.ipushgrc.ui.cmpevent.p130;

import javax.swing.JLabel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * 违规事件预警...
 * @author xch
 *
 */
public class CmpEventAlarmWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.add(new JLabel("违规事件预警[" + this.getClass().getName() + "],开发中...")); //

	}

}
