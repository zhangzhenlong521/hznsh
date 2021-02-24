package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import freemarker.template.SimpleDate;

public class QnedtdParticularsWKPanel extends AbstractWorkPanel implements
		BillListHtmlHrefListener, ActionListener {

	private BillListPanel listPanel;
	private BillListPanel list;

	@Override
	public void initialize() {
		// listPanel = new BillListPanel("WN_QNEDTD_Q01_ZPY");
		list = new BillListPanel("WN_QNEDTD_RESULT_Q01_ZPY");// 黔农E贷线上替代结果panel
		// listPanel.addBillListHtmlHrefListener(this);
		list.getQuickQueryPanel().addBillQuickActionListener(this);
		this.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == list.getQuickQueryPanel()) {// 设置快速查询
			try {
				String data[][] = UIUtil.getStringArrayByDS(null, list
						.getQuickQueryPanel().getQuerySQL());
				if (data.length <= 0) {
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					String date_time = list.getQuickQueryPanel()
							.getCompentRealValue("DATE_TIME");
					date_time = date_time.substring(0, date_time.length() - 1);
					date_time = date_time.replace("年", "-").replace("月", "");
					String str = service.getQnedtdRate(date_time);
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
					MessageBox.show(this, str);
				} else {
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
				}

				// String username =
				// ClientEnvironment.getInstance().getLoginUserName();
				// String condition =
				// listPanel.getQuickQueryPanel().getQuerySQLCondition();//获取到当前查询条件
				// if (condition.contains("date_time")) {//表示查询条件中包含时间
				// //对条件中的日期进行处理
				// condition = condition.substring(condition.indexOf("=") + 2,
				// condition.lastIndexOf("and") - 2);
				// String date_time=getLastMonth(condition);
				// String
				// sql="select * from wn_qnedtd where 1=1 and mName='"+username+"' and date_time='"+date_time+"'";
				// System.out.println("查询sql:"+sql);
				// listPanel.QueryData(sql);
				// WnSalaryServiceIfc service = (WnSalaryServiceIfc)
				// UIUtil.lookUpRemoteService(WnSalaryServiceIfc.class);
				// String str=service.getQnedtdRate(date_time,username);
				// MessageBox.show(this,str);
				// }
			} catch (Exception e2) {
				e2.printStackTrace();
			}

		}
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent e) {
		if (e.getSource() == listPanel) {
			BillVO vo = listPanel.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(listPanel, "查看", list);
			String date_time = vo.getStringValue("date_time");
			dialog.getBilllistPanel().QueryDataByCondition(
					"username='" + vo.getStringValue("mname")
							+ "' and date_time='" + date_time.substring(0, 7)
							+ "'");
			dialog.setVisible(true);
		}
	}

	// 对日期进行处理,获取到月末日期
	public String getLastMonth(String date) {
		String result = "";
		try {
			SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
			int year = Integer.parseInt(new SimpleDateFormat("yyyy")
					.format(simple.parse(date)));
			int month = Integer.parseInt(new SimpleDateFormat("MM")
					.format(simple.parse(date)));
			String day = "";
			switch (month) {
			case 1:
			case 3:
			case 5:
			case 7:
			case 8:
			case 10:
			case 12:
				day = "31";
				break;
			case 4:
			case 6:
			case 9:
			case 11:
				day = "30";
				break;
			case 2:
				if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
					day = "29";
				} else {
					day = "28";
				}
				break;
			default:
				break;
			}
			String month2 = String.valueOf(month);
			month2 = month2.length() == 1 ? "0" + month2 : month2;
			result = year + "-" + month2 + "-" + day;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			return result;
		}

	}
}
