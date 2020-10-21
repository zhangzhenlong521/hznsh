package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * ZPY[2019-05-31] 柜员服务质量情况查明
 * 
 * 
 */
public class GYServerSearchWKPanel extends AbstractWorkPanel implements
		ActionListener, BillListHtmlHrefListener {

	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_GYSERVER1_ZPY_Q01");
		listPanel.addBillListHtmlHrefListener(this);
		this.add(listPanel);
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent e) {
		if (e.getSource() == listPanel) {
			BillVO vo = listPanel.getSelectedBillVO();
			String usercode = vo.getStringValue("usercode");
			String pftime = vo.getStringValue("pftime");
			System.out.println(usercode + " " + pftime);
			BillListDialog dialog = new BillListDialog(this, "查看",
					"V_GYSERVER2_ZPY_Q01");
			dialog.getBilllistPanel().QueryDataByCondition(
					"usercode='" + usercode + "' and pftime='" + pftime + "'");
			dialog.setVisible(true);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

}
