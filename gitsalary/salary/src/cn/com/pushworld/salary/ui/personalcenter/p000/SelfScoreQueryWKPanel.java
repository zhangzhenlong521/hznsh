package cn.com.pushworld.salary.ui.personalcenter.p000;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.to.SalaryTBUtil;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * ���˿��˷���������ѯ
 * 
 * @author Administrator
 * 
 */
public class SelfScoreQueryWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	private static final long serialVersionUID = 1L;
	private String queryCL = TBUtil.getTBUtil().getSysOptionStringValue("�����쵼�鿴Ա�����˽������", "1");
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "���˿��˵÷�_";

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();

		// ��������Ĭ��ֵΪ��ǰ�������� Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//���õ����ļ����� Gwang 2013-08-31
		dr.setReportExpName(exportFileName);

		dr.getBillCellPanel().setEditable(false);
		dr.getBillCellPanel().addBillCellHtmlHrefListener(this);
		bq.addBillQuickActionListener(this);
		this.add(dr, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		new SplashWindow(this, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				onQuery();
			}
		});
	}

	private void onQuery() {
		try {
			if (bq.checkValidate()) {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				String userid = ClientEnvironment.getCurrLoginUserVO().getId();
				String userids[] = new String[0];
				if (!"0".equals(queryCL)) {
					userids = new SalaryTBUtil().getSameDeptUsers(userid);
				} else {
					userids = new String[] { userid };
				}
				dr.getBillCellPanel().loadBillCellData(ifc.getScoreVO_Person(bq.getRealValueAt("checkdate"), userids));
				dr.getBillCellPanel().setEditable(false);
				//���õ����ļ����� Gwang 2013-08-31
				dr.setReportExpName(exportFileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "�����쳣�������Ա��ϵ!");
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent event) {
		if (event.getCellItemVO().getCustProperty("logid") != null) {
			boolean ishaveperson = (Boolean) event.getCellItemVO().getCustProperty("ishaveperson");
			boolean ishavedept = (Boolean) event.getCellItemVO().getCustProperty("ishavedept");
			boolean ishavegg = (Boolean) event.getCellItemVO().getCustProperty("ishavegg");
			String checkeduserid = event.getCellItemVO().getCustProperty("checkeduserid") + "";
			String logid = event.getCellItemVO().getCustProperty("logid") + "";
			BillDialog bd = new BillDialog(this, "�鿴��ϸ", 980, 600);
			WLTTabbedPane maintab = new WLTTabbedPane();
			try {
				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				if (ishaveperson) {
					DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
					dr.getBillQueryPanel().setVisible(false);
					BillCellPanel bc = dr.getBillCellPanel();
					bc.loadBillCellData(ifc.getPersonTargetCheckResVO(logid, checkeduserid));
					bc.setEditable(false);
					bc.setToolBarVisiable(false);
					bc.setAllowShowPopMenu(false);
					maintab.addTab("Ա��������ϸ", dr);
				}
				if (ishavegg) {
					DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
					dr.getBillQueryPanel().setVisible(false);
					BillCellPanel bc = dr.getBillCellPanel();
					bc.loadBillCellData(ifc.getPostCheckResVO(bq.getRealValueAt("checkdate"), checkeduserid));
					bc.setToolBarVisiable(false);
					bc.setAllowShowPopMenu(false);
					bc.setEditable(false);
					maintab.addTab("���³�������ϸ", dr);
				}
				if (ishavedept) {
					DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
					dr.getBillQueryPanel().setVisible(false);
					BillCellPanel bc = dr.getBillCellPanel();
					bc.loadBillCellData(ifc.getPostTargetCheckResVO(bq.getRealValueAt("checkdate"), checkeduserid));
					bc.setEditable(false);
					bc.setToolBarVisiable(false);
					bc.setAllowShowPopMenu(false);
					maintab.addTab("�ֹܲ��ſ�����ϸ", dr);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			bd.getContentPane().add(maintab, BorderLayout.CENTER);
			bd.addOptionButtonPanel(new String[] { "ȡ��" });
			bd.setVisible(true);
		}
	}
}
