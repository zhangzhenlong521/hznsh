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
 * @author ZPY ǭũE��ǩԼ������ѯ
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
			BillListDialog dialog = new BillListDialog(listPanel, "�鿴", list);
			String date_time = vo.getStringValue("date_time").replace("-", "��");
			dialog.getBilllistPanel().QueryDataByCondition(
					"username='" + vo.getStringValue("E") + "' and date_time='"
							+ vo.getStringValue("date_time") + "'");
			// dialog.setBtn_confirm(new WLTButton("�鿴"));
			// dialog.setBtn_confirmVisible(false);
			dialog.setVisible(true);
		} else if (e.getSource() == list) {
			MessageBox.show(this, "�鿴��ʾ");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == list.getQuickQueryPanel()) {// ��ȡ�����ٲ�ѯ�¼�
			try {
				// String
				// username=ClientEnvironment.getInstance().getLoginUserName();//��ȡ����¼�˵�����
				// //��ȡ����ǰ��ѯ����
				// String condition =
				// listPanel.getQuickQueryPanel().getQuerySQLCondition();//��ȡ����ǰ��ѯ����
				// String
				// sql="select * from V_qnedqy_zpy where 1=1 and E='"+username+"' ";
				// //�����жϵ�ǰ�ͻ������Ƿ����ǭũE��ǩԼ����
				// HashVO[] data = UIUtil.getHashVoArrayByDS(null, sql);
				// if(data.length<=0){//��ʾ��ǰ�ͻ�����û������
				// listPanel.QueryData(sql);
				// }else {
				// //����ǭũE��ǩԼ������ɱ�
				// WnSalaryServiceIfc service = (WnSalaryServiceIfc)
				// UIUtil.lookUpRemoteService(WnSalaryServiceIfc.class);
				// String date_time="";
				// if(condition.contains("date_time")){//��ʾ������ѯʱ��
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