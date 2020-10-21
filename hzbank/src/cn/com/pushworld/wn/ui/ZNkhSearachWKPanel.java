package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillListSelectListener;

/**
 * 
 * @author ZPY[助农商户维护信息查询] 2019-05-30
 */
public class ZNkhSearachWKPanel extends AbstractWorkPanel implements
		ActionListener {
	private String str = "";
	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_WN_ZNWH_ZPY_Q01");
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == listPanel.getQuickQueryPanel()) {
			QuickQuery();
		}
	}

	private void QuickQuery() {
		String sql = "select * from V_WN_ZNWH where 1=1 ";
		String date_time = listPanel.getQuickQueryPanel().getCompentRealValue(
				"date_time");
		if (date_time != null && !date_time.isEmpty()) {
			date_time = date_time.substring(0, date_time.length() - 1);
			date_time = date_time.replace("年", "-").replace("月", "");
			sql = sql + " and date_time='" + date_time + "' ";
		}
		String manager_name = listPanel.getQuickQueryPanel()
				.getCompentRealValue("B");
		if (manager_name != null && !manager_name.isEmpty()) {
			sql = sql + " and B like '%" + manager_name + "%' ";
		}
		String jg_name = listPanel.getQuickQueryPanel()
				.getCompentRealValue("A");
		if (jg_name != null && !jg_name.isEmpty()) {
			sql = sql + " and A like'%" + jg_name + "%'";
		}
		listPanel.QueryData(sql);
	}

}
