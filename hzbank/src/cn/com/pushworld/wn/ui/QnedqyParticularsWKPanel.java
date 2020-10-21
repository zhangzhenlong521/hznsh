package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.apache.poi.hssf.record.cont.ContinuableRecord;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

/**
 * 
 * @author ZPY 黔农E贷签约户数查询
 */
public class QnedqyParticularsWKPanel extends AbstractWorkPanel implements
		BillListHtmlHrefListener, ActionListener {

	private BillListPanel listPanel = null;
	private BillListPanel list = null;

	@Override
	public void initialize() {
		// listPanel=new BillListPanel("V_QNEDQY_ZPY_Q01");
		list = new BillListPanel("WN_QNED_RESULT_ZPY_Q01");
		list.getQuickQueryPanel().addBillQuickActionListener(this);
		// listPanel.addBillListHtmlHrefListener(this);
		this.add(list);
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent e) {
		if (e.getSource() == listPanel) {
			BillVO vo = listPanel.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(listPanel, "查看", list);
			String date_time = vo.getStringValue("date_time").replace("-", "年");
			dialog.getBilllistPanel().QueryDataByCondition(
					"username='" + vo.getStringValue("E") + "' and date_time='"
							+ vo.getStringValue("date_time") + "'");
			// dialog.setBtn_confirm(new WLTButton("查看"));
			// dialog.setBtn_confirmVisible(false);
			dialog.setVisible(true);
		} else if (e.getSource() == list) {
			MessageBox.show(this, "查看显示");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == list.getQuickQueryPanel()) {// 获取到快速查询事件
			try {
				// String
				// username=ClientEnvironment.getInstance().getLoginUserName();//获取到登录人的姓名
				// //获取到当前查询条件
				// String condition =
				// listPanel.getQuickQueryPanel().getQuerySQLCondition();//获取到当前查询条件
				// String
				// sql="select * from V_qnedqy_zpy where 1=1 and E='"+username+"' ";
				// //首先判断当前客户经理是否存在黔农E贷签约数据
				// HashVO[] data = UIUtil.getHashVoArrayByDS(null, sql);
				// if(data.length<=0){//表示当前客户经理没有数据
				// listPanel.QueryData(sql);
				// }else {
				// //计算黔农E贷签约数的完成比
				// WnSalaryServiceIfc service = (WnSalaryServiceIfc)
				// UIUtil.lookUpRemoteService(WnSalaryServiceIfc.class);
				// String date_time="";
				// if(condition.contains("date_time")){//表示包含查询时间
				// condition=condition.substring(condition.indexOf("=")+1,condition.lastIndexOf("and")-1);
				// condition=condition.substring(0,condition.lastIndexOf("-"))+"'";
				// listPanel.QueryData(sql+" and date_time="+condition);
				// date_time=listPanel.getQuickQueryPanel().getCompentRealValue("DATE_TIME").substring(0,listPanel.getQuickQueryPanel().getCompentRealValue("DATE_TIME").length()-1);
				// String str=service.getQnedRate(date_time,username);
				// MessageBox.show(this,str);
				// }
				// }
				String data[][] = UIUtil.getStringArrayByDS(null, list
						.getQuickQueryPanel().getQuerySQL());
				if (data.length <= 0) {
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					String date_time = list.getQuickQueryPanel()
							.getCompentRealValue("DATE_TIME");
					date_time = date_time.substring(0, date_time.length() - 1);
					String str = service.getQnedRate(date_time);
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
				} else {
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
				}

			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
}