package com.pushworld.ipushgrc.ui.duty.p040;

import javax.swing.JLabel;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;

/**
 * ��λԤ��!!
 * @author xch
 *
 */
public class DutyAlarmWKPanel extends AbstractWorkPanel {

	@Override
	public void initialize() {
		this.add(new JLabel("����Ԥ��[" + this.getClass().getName() + "],������...")); //
	}

}
