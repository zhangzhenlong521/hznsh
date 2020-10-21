package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl
 * 
 *         2019-5-27-下午03:30:34 收回存量不良贷款完成比
 */
public class BaBTheStockOfLoanWKPanel extends AbstractWorkPanel implements
		ActionListener {
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("V_WN_STOCK_LOAN_CODE1");
		list.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(list);

	}

	@Override
	public void actionPerformed(ActionEvent actionevent) {
		if (actionevent.getSource() == list.getQuickQueryPanel()) {
			String[][] data;
			try {
				data = UIUtil.getStringArrayByDS(null, list
						.getQuickQueryPanel().getQuerySQL());
				if (data.length <= 0) {
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					String str = service.getTheStockOfLoan(list
							.getQuickQueryPanel()
							.getCompentRealValue("DATE_TIME")
							.substring(
									0,
									list.getQuickQueryPanel()
											.getCompentRealValue("DATE_TIME")
											.length() - 1));
					MessageBox.show(this, str);
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
				} else {
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
				}
			} catch (WLTRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
