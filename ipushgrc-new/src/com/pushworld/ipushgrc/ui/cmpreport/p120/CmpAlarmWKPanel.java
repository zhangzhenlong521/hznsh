package com.pushworld.ipushgrc.ui.cmpreport.p120;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * �Ϲ汨��Ԥ��
 * ���ڱ�����Ԥ�� �����±���ʱû���ύ�����ȱ�����û���ύ���걨����û���ύ
 * Ԥ����Ҫ��û���ύ������˺͹����˷�����ʾ��
 * @author xch
 *
 */
public class CmpAlarmWKPanel extends AbstractWorkPanel {
	private BillListPanel billList = null; //

	@Override
	public void initialize() {
		billList = new BillListPanel("CMP_REPORT_ALARM_CODE1"); //
		this.add(billList); //
	}

}