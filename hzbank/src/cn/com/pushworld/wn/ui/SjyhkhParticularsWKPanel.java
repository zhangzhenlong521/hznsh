package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.sun.mail.handlers.message_rfc822;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class SjyhkhParticularsWKPanel extends AbstractWorkPanel implements
		ActionListener, BillListHtmlHrefListener {

	private BillListPanel listPanel;
	private BillListPanel list;
	private String str;

	@Override
	public void initialize() {
		// listPanel=new BillListPanel("V_SJYH_ZPY_Q01");
		list = new BillListPanel("WN_SJYH_RESULT_ZPY_Q01");
		list.getQuickQueryPanel().addBillQuickActionListener(this);
		// listPanel.addBillListHtmlHrefListener(this);
		this.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == list.getQuickQueryPanel()) {//
			String username = ClientEnvironment.getInstance()
					.getLoginUserName();// 获取到当前登录人姓名
			try {
				// String
				// condition=listPanel.getQuickQueryPanel().getQuerySQLCondition();//获取到查询条件
				// if(condition.contains("date_time")){//如果存在日期，则对日期进行处理
				// System.out.println("当前查询条件为:"+condition);
				// condition = condition.substring(condition.indexOf("=") + 2,
				// condition.lastIndexOf("and") - 2);
				// condition=condition.substring(0,condition.lastIndexOf("-")) ;
				// String
				// sql="select * from V_sjyh where xd_col2='"+username+"' and  date_time='"+condition+"'";
				// System.out.println("当前查询的sql:"+sql);
				// WnSalaryServiceIfc service = (WnSalaryServiceIfc)
				// UIUtil.lookUpRemoteService(WnSalaryServiceIfc.class);
				// str=service.getsjRate(condition,username);
				// listPanel.QueryData(sql);
				// MessageBox.show(this,str);
				// }else{
				// MessageBox.show(this,"请选择日期进行查询");
				// }
				String data[][] = UIUtil.getStringArrayByDS(null, list
						.getQuickQueryPanel().getQuerySQL());
				if (data.length <= 0) {
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					String date_time = list.getQuickQueryPanel()
							.getCompentRealValue("DATE_TIME");
					date_time = date_time.substring(0, date_time.length() - 1);
					date_time = date_time.replace("年", "-").replace("月", "");
					String str = service.getsjRate(date_time);
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
					MessageBox.show(this, str);
				} else {
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
					MessageBox.show(this, "查询成功");
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent e) {
		if (e.getSource() == listPanel) {
			BillVO vo = listPanel.getSelectedBillVO();
			String username = vo.getStringValue("xd_col2");// 获取到客户经理
			BillListDialog dialog = new BillListDialog(listPanel, "查看", list);
			String date_time = vo.getStringValue("date_time");
			dialog.getBilllistPanel().QueryDataByCondition(
					"username='" + username + "' and date_time='" + date_time
							+ "'");
			dialog.setVisible(true);

		}
	}

}
