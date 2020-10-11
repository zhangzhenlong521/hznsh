package cn.com.pushworld.salary.ui.target.p030;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
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

public class DeptTargetComputeWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	private static final long serialVersionUID = 1L;
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "部门考核得分统计_";

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();

		//设置日期默认值为当前考核日期  Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//设置导出文件名称 Gwang 2013-08-31
		dr.setReportExpName(exportFileName);

		dr.getBillCellPanel().setEditable(false);
		bq.addBillQuickActionListener(this);
		dr.getBillCellPanel().addBillCellHtmlHrefListener(this);
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
				dr.getBillCellPanel().loadBillCellData(ifc.getScoreVO(bq.getRealValueAt("checkdate")));
				dr.getBillCellPanel().setEditable(false);
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(2, 1); //锁定表头 杨科 2013-10-10
				}

				//设置导出文件名称 Gwang 2013-08-31
				dr.setReportExpName(exportFileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, "发生异常请与管理员联系!");
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent event) {
		if (event.getCellItemVO().getCustProperty("logid") != null && event.getCellItemVO().getCustProperty("deptid") != null) {
			final String logid = event.getCellItemVO().getCustProperty("logid") + "";
			final String deptid = event.getCellItemVO().getCustProperty("deptid") + "";
			new SplashWindow(dr, "查询中,请稍候...", new AbstractAction() {
				public void actionPerformed(ActionEvent arg0) {
					try {
						SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
						BillCellPanel cellpanel = new BillCellPanel();
						cellpanel.setToolBarVisiable(false);
						cellpanel.loadBillCellData(ifc.getTargetCheckResVO(bq.getRealValueAt("checkdate"), deptid));
						cellpanel.setEditable(false);
						BillDialog bld = new BillDialog(dr, "各指标得分详情", 1000, 600);
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
