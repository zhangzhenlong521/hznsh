package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class QnedSearchWKPanel extends AbstractWorkPanel implements
		ActionListener {

	private BillListPanel listPanel;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_QNEDQY_ZPY_Q01");
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(listPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == listPanel.getQuickQueryPanel()) {
			QueryQuick();
		}
	}

	private void QueryQuick() {
		try {
			String date_time = listPanel.getQuickQueryPanel()
					.getCompentRealValue("date_time");
			String sql = "select * from V_qnedqy_zpy where 1=1 ";
			if (date_time != null && !date_time.isEmpty()) {
				date_time = date_time.substring(0, date_time.length() - 1);
				date_time = date_time.replace("年", "-").replace("月", "");
				sql = sql + " and date_time='" + date_time + "'";
			}
			String jg_name = listPanel.getQuickQueryPanel()
					.getCompentRealValue("C");
			if (jg_name != null && !jg_name.isEmpty()) {
				sql = sql + " and C like  '%" + jg_name + "%'";
			}
			String manager_name = listPanel.getQuickQueryPanel()
					.getCompentRealValue("E");
			if (manager_name != null && !manager_name.isEmpty()) {
				sql = sql + " and E like '%" + manager_name + "%'";
			}
			String qy_time = listPanel.getQuickQueryPanel()
					.getCompentRealValue("F");
			if (qy_time != null && !qy_time.isEmpty()) {
				qy_time = qy_time.substring(0, qy_time.length() - 1);
				qy_time = qy_time.replace("年", "-").replace("月", "");
				sql = sql + " and F like '%" + qy_time + "%'";
			}
			System.out.println("执行的sql是:" + sql);
			listPanel.QueryData(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
