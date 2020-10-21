package cn.com.pushworld.wn.ui;

import java.util.HashMap;

import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author zzl
 * 
 *         2019-5-29-下午03:34:56 存款明细查询
 */
public class DepositDetailQueryWKPanel extends AbstractWorkPanel implements
		BillListHtmlHrefListener {
	private BillListPanel list = null;

	@Override
	public void initialize() {
		list = new BillListPanel("V_WN_DEPOSIT_MXCX_CODE1");
		list.addBillListHtmlHrefListener(this);
		this.add(list);
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		if (event.getSource() == list) {
			BillVO vo = list.getSelectedBillVO();
			try {
				HashMap<String, String> usernamp = UIUtil.getHashMapBySQLByDS(
						null, "select xd_col2,xd_col1 from wnbank.s_loan_ryb");
				BillListDialog dialog = new BillListDialog(this, "有效户数查询",
						"WN_DEPOSIT_DETAIL_CODE1");
				dialog.getBilllistPanel().QueryDataByCondition(
						"khjlid='" + usernamp.get(vo.getStringValue("NAME"))
								+ "' and date_time='"
								+ vo.getStringValue("date_time") + "'");
				dialog.getBtn_confirm().setVisible(false);
				dialog.setVisible(true);
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
