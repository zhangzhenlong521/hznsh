package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;

public class TyxwParticularsWKPanel extends AbstractWorkPanel implements
		BillListHtmlHrefListener, ActionListener {

	private String str;
	private BillListPanel listPanel;
	private BillListPanel list;

	// private List<String> wdList;
	@Override
	public void initialize() {
		// listPanel=new BillListPanel("V_TYXW_ZPY_Q01");
		list = new BillListPanel("WN_TYXW_RESULT_ZPY_Q01");
		list.getQuickQueryPanel().addBillQuickActionListener(this);
		// listPanel.addBillListHtmlHrefListener(this);
		// wdList=new ArrayList<String>();
		// wdList=Arrays.asList(new
		// String[]{"西海信用社","人民南路分社","人民中路分社","营业部","草海信用社","龙凤分社","鸭子塘信用社","新区信用社"})
		// ;
		this.add(list);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == list.getQuickQueryPanel()) {
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
					String str = service.getTyxwRate(date_time);
					MessageBox.show(this, str);
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
				} else {
					// 输出查询语句
					System.out.println("特约、小微商户查询sql:"
							+ list.getQuickQueryPanel().getQuerySQL());
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
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
			String username = vo.getStringValue("E");
			String date_time = vo.getStringValue("date_time");
			BillListDialog dialog = new BillListDialog(listPanel, "查看", list);
			dialog.getBilllistPanel().QueryDataByCondition(
					"username='" + username + "' and date_time='" + date_time
							+ "'");
			dialog.setVisible(true);
		}

	}

}
