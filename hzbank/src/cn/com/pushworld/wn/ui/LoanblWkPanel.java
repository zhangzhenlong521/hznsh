package cn.com.pushworld.wn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cn.com.infostrategy.to.mdata.BillVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListDialog;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefEvent;
import cn.com.infostrategy.ui.mdata.BillListHtmlHrefListener;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.pushworld.wn.to.WnUtils;
import freemarker.template.SimpleDate;

public class LoanblWkPanel extends AbstractWorkPanel implements ActionListener,
		BillListHtmlHrefListener, ChangeListener {

	private BillListPanel listPanel, listPanel1, listPanel2, listPanel3,
			listPanel4;
	private WLTTabbedPane tab = null;

	@Override
	public void initialize() {
		listPanel = new BillListPanel("V_WN_SHCL_ZPY_Q01");
		listPanel.getQuickQueryPanel().addBillQuickActionListener(this);// ��ȡ����ѯ����
		listPanel.addBillListHtmlHrefListener(this);
		listPanel.repaintBillListButton();
		listPanel1 = new BillListPanel("V_WN_DQDKSHL_ZPY_Q01");// ���ڴ����ջ��ʿ���
		listPanel1.addBillListHtmlHrefListener(this);
		listPanel1.getQuickQueryPanel().addBillQuickActionListener(this);
		listPanel1.repaintBillListButton();
		listPanel2 = new BillListPanel("V_WN_DNXZBLDK_ZPY_Q01");// ����������������
		listPanel2.getQuickQueryPanel().addBillQuickActionListener(this);
		listPanel2.addBillListHtmlHrefListener(this);
		listPanel3 = new BillListPanel("V_WN_SHBWBL_ZPY_Q01");// �ջز�������
		listPanel3.repaintBillListButton();
		listPanel4 = new BillListPanel("WN_FIVECLASS_CODE1");
		tab = new WLTTabbedPane();
		tab.addTab("�ջش�����������", listPanel);
		tab.addTab("���ڴ����ջ��ʿ���", listPanel1);
		tab.addTab("����������������", listPanel2);
		tab.addTab("�ջر��ⲻ������", listPanel3);
		tab.addChangeListener(this);
		this.add(tab);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getSource() == listPanel.getQuickQueryPanel()) {
			try {
				// �漰��д��ѯ����
				String querySQL = listPanel.getQuickQueryPanel().getQuerySQL();
				querySQL = querySQL.replace("��", "-").replace("��", "");
				System.out.println("��ѯ����Ϊ:" + querySQL);
				String allSQL = "select distinct(a.XD_COL1),a.XD_COL2,a.XD_COL4,a.XD_COL5,a.XD_COL6,a.XD_COL7,a.XD_COL16,a.XD_COL22,a.khjl,a.LOAD_DATES,a.hkxx from ";
				SimpleDateFormat simple = new SimpleDateFormat("yyyy");
				// ��ȡ����ѯ����
				String date_time = listPanel.getQuickQueryPanel()
						.getRealValueAt("load_dates");
				Integer year = Integer.parseInt(simple
						.format(new SimpleDateFormat("yyyy��MM��")
								.parse(date_time))) - 1;
				String lastYearEnd = year + "-" + "12-31";
				allSQL = allSQL
						+ "("
						+ querySQL
						+ ") a left join (select * from wnbank.s_loan_dk where xd_col22 in ('03','04') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
						+ lastYearEnd
						+ "') b on b.xd_col1=a.xd_col1 where b.xd_col1 is not null and a.xd_col7>0 and a.XD_COL1 in (select xd_col1 from wnbank.s_loan_hk where LOAD_DATES like '"
						+ date_time.replace("��", "").replace("��", "")
								.replace(";", "") + "%' )";
				listPanel.QueryData(allSQL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (event.getSource() == listPanel1.getQuickQueryPanel()) {
			try {
				String manager_name = listPanel1.getQuickQueryPanel()
						.getRealValueAt("KHJL");
				String dqDate = listPanel1.getQuickQueryPanel().getRealValueAt(
						"XD_COL5");
				String load_dates = listPanel1.getQuickQueryPanel()
						.getRealValueAt("LOAD_DATES");
				String query_sql = "select * from V_WN_DQDKSHL where 1=1 ";
				// listPanel1.getQuickQueryPanel().getRealValueAt("XD_COL22")
				BillVO[] fiveClassSelect = listPanel4.getSelectedBillVOs();
				if (manager_name != null & !"".equals(manager_name)) {
					query_sql = query_sql + " and KHJL like '%" + manager_name
							+ "%'";
				}
				if (load_dates != null & !"".equals(load_dates)) {
					load_dates = load_dates.replace("��", "-").replace("��", "")
							.replace(";", "");
					query_sql = query_sql + " and load_dates like '"
							+ load_dates + "%'";
				}
				if (dqDate != null & !"".equals(dqDate)) {
					if (dqDate.contains("��")) {//
						String monthStart = dqDate.replace("��", "-")
								.replace("��", "").replace(";", "")
								+ "-01";
						String monthEnd = WnUtils.getMonthEnd(monthStart);
						query_sql = query_sql + " and XD_COL5>='" + monthStart
								+ "' and XD_COL5<='" + monthEnd + "' ";

					} else {
						dqDate = dqDate.replace(";", "");
						query_sql = query_sql + " and xd_col5<='" + dqDate
								+ "'";
					}
				}
				listPanel1.QueryData(query_sql);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		else if (event.getSource() == listPanel2.getQuickQueryPanel()) {
			try {
				String querySQL = listPanel2.getQuickQueryPanel().getQuerySQL();
				System.out.println("����������������:" + querySQL);
				String allSQL = "select a.khjl,a.xd_col1,a.xd_col2,a.xd_col4,a.xd_col5,a.xd_col6,a.xd_col7,a.xd_col22,a.xd_col16,a.load_dates from ";
				SimpleDateFormat simple = new SimpleDateFormat("yyyy");
				String date_time = listPanel2.getQuickQueryPanel()
						.getRealValueAt("load_dates");
				Integer year = Integer.parseInt(simple
						.format(new SimpleDateFormat("yyyy��MM��")
								.parse(date_time))) - 1;
				String lastYearEnd = year + "-12-31";
				allSQL = allSQL
						+ " ("
						+ querySQL
						+ ") a left join (select * from wnbank.s_loan_dk where xd_col22 in ('01','02') and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')='"
						+ lastYearEnd
						+ "')  b  on b.xd_col1=a.xd_col1 where b.xd_col1 is not null";
				listPanel2.QueryData(allSQL);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent event) {
		if (event.getSource() == listPanel) {
			BillVO vo = listPanel.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(listPanel, "������Ϣ",
					"V_S_LOAN_HK_CODE1");
			dialog.getBilllistPanel().QueryDataByCondition(
					"xd_col1='" + vo.getStringValue("xd_col1") + "'");
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		} else if (event.getSource() == listPanel1) {
			BillVO vo = listPanel1.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(listPanel1, "�鿴",
					"V_S_LOAN_HK_CODE1");
			dialog.getBilllistPanel().QueryDataByCondition(
					"xd_col1='" + vo.getStringValue("xd_col1") + "'");
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		} else if (event.getSource() == listPanel2) {
			MessageBox.show("this message");
			BillVO vo = listPanel2.getSelectedBillVO();
			BillListDialog dialog = new BillListDialog(this, "�鿴",
					"V_S_LOAN_HK_CODE1");
			dialog.getBilllistPanel().QueryDataByCondition(
					"xd_col1='" + vo.getStringValue("xd_col1") + "'");
			dialog.getBtn_confirm().setVisible(false);
			dialog.setVisible(true);
		}

	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}

}
