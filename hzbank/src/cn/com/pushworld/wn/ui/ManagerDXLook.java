package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class ManagerDXLook extends AbstractWorkPanel implements ActionListener,
		BillListHtmlHrefListener {

	private BillListPanel listPanel;
	private String str;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_MANAGERSCORE_LOOK_ZPY_Q01");
		listPanel.addBillListHtmlHrefListener(this);
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent e) {
		if (e.getSource() == listPanel) {
			BillVO vo = listPanel.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(this, "²é¿´",
					"WN_MANAGERDX_TABLE_ZPY_Q01");
			dialog.getBilllistPanel().QueryDataByCondition(
					"planid='" + vo.getStringValue("planid")
							+ "' and username='"
							+ vo.getStringValue("username") + "'");
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		}
	}
}