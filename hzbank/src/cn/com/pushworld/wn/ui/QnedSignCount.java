package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class QnedSignCount extends AbstractWorkPanel implements ActionListener {

	/**
	 * @author FJ[黔农e贷签约完成率] 2019年6月3日17:23:04
	 */
	private static final long serialVersionUID = 1L;
	private BillListPanel billListPanel = null;

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billListPanel.getQuickQueryPanel()) {
			QuickQuery();
		}

	}

	private void QuickQuery() {
		String sql = "select * from V_QNED_QY where 1=1 ";
		String date_time = billListPanel.getQuickQueryPanel()
				.getCompentRealValue("KHDATE");
		if (date_time != null && !date_time.isEmpty()) {
			date_time = date_time.substring(0, date_time.length() - 1);
			date_time = date_time.replace("-", "年");
			sql = sql + " and KHDATE='" + date_time + "' ";
		}
		String code = billListPanel.getQuickQueryPanel().getCompentRealValue(
				"B");
		if (code != null && !code.isEmpty()) {
			sql = sql + " and B like '%" + code + "%' ";
		}
		String name = billListPanel.getQuickQueryPanel().getCompentRealValue(
				"E");
		if (name != null && !name.isEmpty()) {
			sql = sql + " and E like'%" + name + "%'";
		}
		billListPanel.QueryData(sql);

	}

	@Override
	public void initialize() {
		billListPanel = new BillListPanel("V_QNED_QY_CODE1");
		billListPanel.getQuickQueryPanel().addBillQuickActionListener(this);
		billListPanel.repaintBillListButton();
		this.add(billListPanel);

	}

}
