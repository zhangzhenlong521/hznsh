package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class SjyhSearchWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_SJYH_ZPY_Q01");
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == listPanel.getQuickQueryPanel()) {

			QuerySearch();

		}
	}

	private void QuerySearch() {
		try {
			String sql = "select * from V_sjyh where 1=1 ";
			String date_time = listPanel.getQuickQueryPanel()
					.getCompentRealValue("date_time");
			if (date_time != null && !date_time.isEmpty()) {
				date_time = date_time.substring(0, date_time.length() - 1);
				date_time = date_time.replace("Äê", "-").replace("ÔÂ", "");
				sql = sql + " and date_time='" + date_time + "'";
			}
			String cus_name = listPanel.getQuickQueryPanel()
					.getCompentRealValue("XD_COL5");
			if (cus_name != null && !cus_name.isEmpty()) {
				sql = sql + " and XD_COL5 like '%" + cus_name + "%'";
			}
			String manager_no = listPanel.getQuickQueryPanel()
					.getCompentRealValue("XD_COL2");
			if (manager_no != null && !manager_no.isEmpty()) {
				sql = sql + " and XD_COL2 like '%" + manager_no + "%'";
			}
			listPanel.QueryData(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
