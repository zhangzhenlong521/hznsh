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
					.getLoginUserName();// ��ȡ����ǰ��¼������
			try {
				// String
				// condition=listPanel.getQuickQueryPanel().getQuerySQLCondition();//��ȡ����ѯ����
				// if(condition.contains("date_time")){//����������ڣ�������ڽ��д���
				// System.out.println("��ǰ��ѯ����Ϊ:"+condition);
				// condition = condition.substring(condition.indexOf("=") + 2,
				// condition.lastIndexOf("and") - 2);
				// condition=condition.substring(0,condition.lastIndexOf("-")) ;
				// String
				// sql="select * from V_sjyh where xd_col2='"+username+"' and  date_time='"+condition+"'";
				// System.out.println("��ǰ��ѯ��sql:"+sql);
				// WnSalaryServiceIfc service = (WnSalaryServiceIfc)
				// UIUtil.lookUpRemoteService(WnSalaryServiceIfc.class);
				// str=service.getsjRate(condition,username);
				// listPanel.QueryData(sql);
				// MessageBox.show(this,str);
				// }else{
				// MessageBox.show(this,"��ѡ�����ڽ��в�ѯ");
				// }
				String data[][] = UIUtil.getStringArrayByDS(null, list
						.getQuickQueryPanel().getQuerySQL());
				if (data.length <= 0) {
					WnSalaryServiceIfc service = (WnSalaryServiceIfc) UIUtil
							.lookUpRemoteService(WnSalaryServiceIfc.class);
					String date_time = list.getQuickQueryPanel()
							.getCompentRealValue("DATE_TIME");
					date_time = date_time.substring(0, date_time.length() - 1);
					date_time = date_time.replace("��", "-").replace("��", "");
					String str = service.getsjRate(date_time);
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
					MessageBox.show(this, str);
				} else {
					list.QueryData(list.getQuickQueryPanel().getQuerySQL());
					MessageBox.show(this, "��ѯ�ɹ�");
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
			String username = vo.getStringValue("xd_col2");// ��ȡ���ͻ�����
			BillListDialog dialog = new BillListDialog(listPanel, "�鿴", list);
			String date_time = vo.getStringValue("date_time");
			dialog.getBilllistPanel().QueryDataByCondition(
					"username='" + username + "' and date_time='" + date_time
							+ "'");
			dialog.setVisible(true);

		}
	}

}
