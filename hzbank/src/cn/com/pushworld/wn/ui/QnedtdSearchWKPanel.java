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
 * 黔农E贷线上替代明细查询
 * 
 * @author ZPY【2019-05-30】
 */
public class QnedtdSearchWKPanel extends AbstractWorkPanel implements
		ActionListener, BillListHtmlHrefListener {

	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("WN_QNEDTD_Q01_ZPY");
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
			BillListDialog dialog = new BillListDialog(this, "查看",
					"V_S_LOAN_HK_CODE1");
			dialog.getBilllistPanel().QueryDataByCondition(
					"xd_col1='" + vo.getStringValue("DKNO") + "'");
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		}
	}
}