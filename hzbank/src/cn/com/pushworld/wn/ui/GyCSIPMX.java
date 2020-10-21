package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class GyCSIPMX extends AbstractWorkPanel implements ActionListener {
	
	/**
	 * @author fj[柜员CSIP业务量明细] 2020年5月15日16:25:19
	 */

	
	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel;
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_CSIP_MX_CODE1");
		this.add(listPanel);

	}

}
