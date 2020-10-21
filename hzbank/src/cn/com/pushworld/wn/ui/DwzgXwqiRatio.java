package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class DwzgXwqiRatio extends AbstractWorkPanel implements ActionListener {

	/**
	 * @author FJ[单位职工，小微企业建档指标完成比]
	 */
	private static final long serialVersionUID = 1L;
	private BillListPanel listPanel = null;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == listPanel.getQuickQueryPanel()) {
			String[][] data;
			try {
				data = UIUtil.getStringArrayByDS(null, listPanel
						.getQuickQueryPanel().getQuerySQL());
				if (data.length <= 0) {
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					// <<<<<<< HEAD
					// }
					// } catch (Exception e2) {
					// // TODO: handle exception
					// }
					// }
					//
					// =======
					String str = service.getDwzgXwqyRatio(listPanel
							.getQuickQueryPanel()
							.getCompentRealValue("DATE_TIME")
							.substring(
									0,
									listPanel.getQuickQueryPanel()
											.getCompentRealValue("DATE_TIME")
											.length() - 1));
					MessageBox.show(this, str);
					listPanel.QueryData(listPanel.getQuickQueryPanel()
							.getQuerySQL());
				} else {
					listPanel.QueryData(listPanel.getQuickQueryPanel()
							.getQuerySQL());
				}
			} catch (WLTRemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void initialize() {
		// <<<<<<< HEAD
		// listPanel = new BillListPanel("");
		// =======
		listPanel = new BillListPanel("V_WN_ZGXW_WCB_CODE1");
		// >>>>>>> 5893a9e34fae888803f17ebc95aff84e07f5e9fc
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(listPanel);

	}

}
