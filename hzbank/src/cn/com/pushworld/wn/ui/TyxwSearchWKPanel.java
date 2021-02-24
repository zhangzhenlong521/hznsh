package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class TyxwSearchWKPanel extends AbstractWorkPanel implements
		ActionListener {
	private BillListPanel listPanel;

	@Override
	public void initialize() {
		// listPanel=new BillListPanel("V_TYXW_ZPY_Q01");
		listPanel = new BillListPanel("V_WN_TYXW_ZPY_Q01");
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
			String sql = "";
			String date_time = listPanel.getQuickQueryPanel()
					.getCompentRealValue("date_time");
			if (date_time != null && !date_time.isEmpty()) {
				date_time = date_time.substring(0, date_time.length() - 1);
				date_time = date_time.replace("年", "-").replace("月", "");
				sql = "SELECT  distinct(a.mcht_id) mcht_id,b.mcht_name,b.mcht_prop,b.client_local,a.num,a.cus_name,b.date_time,CASE WHEN b.mcht_prop='小微商户' THEN (CASE WHEN a.num>=5 THEN '合格' ELSE '不合格' END ) ELSE (CASE WHEN a.num>=10 THEN '合格' ELSE '不合格' end) end db FROM (SELECT mcht_id,cus_name,sum(num) num  FROM v_wn_tyxw WHERE date_time='"
						+ date_time
						+ "' GROUP  BY mcht_id,cus_name) a LEFT JOIN (SELECT * FROM v_wn_tyxw WHERE date_time='"
						+ date_time
						+ "') b ON a.mcht_id=b.mcht_id WHERE b.mcht_name IS NOT NULL";
			} else {
				MessageBox.show(this, "请输入查询日期");
				return;
			}
			String mcht_id = listPanel.getQuickQueryPanel()
					.getCompentRealValue("MCHT_ID");// 商户号
			if (mcht_id != null && !mcht_id.isEmpty()) {
				sql = sql + " and a.mcht_id like '%" + mcht_id + "%' ";
			}

			String mcht_prop = listPanel.getQuickQueryPanel()
					.getCompentRealValue("MCHT_PROP");// 商户类型
			if (mcht_prop != null && !mcht_prop.isEmpty()) {
				sql = sql + " and b.mcht_prop like '%" + mcht_prop + "%' ";
			}
			//
			String client_local = listPanel.getQuickQueryPanel()
					.getCompentRealValue("CLIENT_LOCAL");
			if (client_local != null && !client_local.isEmpty()) {
				sql = sql + " and b.client_local like '%" + client_local
						+ "%' ";
			}

			String cus_name = listPanel.getQuickQueryPanel()
					.getCompentRealValue("CUS_NAME");// 客户经理
			if (cus_name != null && !cus_name.isEmpty()) {
				sql = sql + " and a.CUS_NAME like '%" + cus_name + "%'";
			}
			String db = listPanel.getQuickQueryPanel()
					.getCompentRealValue("DB");
			if (db != null && !db.isEmpty()) {
				sql = sql + " db = '" + db + "' ";
			}
			System.out.println("当前执行的sql:" + sql);
			listPanel.QueryData(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
