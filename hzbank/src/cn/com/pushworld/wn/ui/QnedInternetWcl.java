package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class QnedInternetWcl extends AbstractWorkPanel implements
		ActionListener {

	/**
	 * @author FJ[黔农E贷线上替代完成率] 2019年6月4日14:31:49
	 */
	private static final long serialVersionUID = 1L;
	private BillListPanel billListPanel;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billListPanel.getQuickQueryPanel()) {
			Querydate();
		}

	}

	private void Querydate() {
		String[][] data;
		try {
			data = UIUtil.getStringArrayByDS(null, billListPanel
					.getQuickQueryPanel().getQuerySQL());
			if (data.length <= 0) {
				WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
						.lookUpRemoteService(WnSalaryServiceIfc.class);
				String result = service
						.getQnedXstd(billListPanel
								.getQuickQueryPanel()
								.getCompentRealValue("time")
								.substring(
										0,
										billListPanel.getQuickQueryPanel()
												.getCompentRealValue("time")
												.length() - 1));
				MessageBox.show(this, result);
				billListPanel.QueryData(billListPanel.getQuickQueryPanel()
						.getQuerySQL());
			} else {
				billListPanel.QueryData(billListPanel.getQuickQueryPanel()
						.getQuerySQL());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("V_WN_QNED_XSTD_CODE1");
		billListPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(billListPanel);

	}

}
