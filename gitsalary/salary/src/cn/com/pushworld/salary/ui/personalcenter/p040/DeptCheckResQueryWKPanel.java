package cn.com.pushworld.salary.ui.personalcenter.p040;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;
import cn.com.pushworld.salary.ui.target.p030.DeptTargetComputeWKPanel;

/**
 * ���Ŷ��Կ��˵÷ֱȡ���ߵ÷ֱȡ�ƽ���÷ֱ�
 * @author Administrator
 *
 */
public class DeptCheckResQueryWKPanel extends AbstractWorkPanel implements ActionListener ,BillCellHtmlHrefListener{
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;
	
	private String exportFileName = "���ſ��˵÷ֱ�_";

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
				dr.getBillCellPanel().loadBillCellData(ifc.getDeptCheckResVO(bq.getRealValueAt("checkdate"), ClientEnvironment.getCurrLoginUserVO().getBlDeptId()));
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
		if (event.getCellItemVO().getCustProperty("logid") != null && event.getCellItemVO().getCustProperty("deptid") != null) {
			final String logid = event.getCellItemVO().getCustProperty("logid") + "";
			final String deptid = event.getCellItemVO().getCustProperty("deptid") + "";
			new SplashWindow(dr, "��ѯ��,���Ժ�...", new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
						BillCellPanel cellpanel = new BillCellPanel();
						cellpanel.setToolBarVisiable(false);
						cellpanel.loadBillCellData(ifc.getTargetCheckResVO(bq.getRealValueAt("checkdate"), deptid));
						cellpanel.setEditable(false);
						BillDialog bld = new BillDialog(dr, "��ָ��÷�����", 1000, 600);
						bld.getContentPane().add(cellpanel);
						bld.setVisible(true);
					} catch (Exception ex) {
						WLTLogger.getLogger(DeptTargetComputeWKPanel.class).error("", ex);
					}
				}
			}, false);
		}
	}

}
