package cn.com.pushworld.salary.ui.person.p060;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTTabbedPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 员工考核得分统计
 * 
 * @author Administrator
 * 
 */
public class PersonCheckComputeWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
	private static final long serialVersionUID = 1L;
	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "个人考核得分统计_";

	public void initialize() {
		dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
		bq = dr.getBillQueryPanel();

		// 设置日期默认值为当前考核日期 Gwang 2013-08-21
		QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("checkdate");
		String checkDate = new SalaryUIUtil().getCheckDate();
		dateRef.setValue(checkDate);

		//设置导出文件名称 Gwang 2013-08-31
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
				dr.getBillCellPanel().loadBillCellData(ifc.getScoreVO_Person(bq.getRealValueAt("checkdate")));
				dr.getBillCellPanel().setEditable(false);
				if (dr.getBillCellPanel().getRowCount() > 2) {
					dr.getBillCellPanel().setLockedCell(2, 1); //锁定表头 杨科 2013-10-10
				}

				//设置导出文件名称 Gwang 2013-08-31
				dr.setReportExpName(exportFileName + bq.getRealValueAt("checkdate"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox.show(this, e.getMessage());
		}
	}

	public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent event) {
		final boolean ishaveperson = (Boolean) event.getCellItemVO().getCustProperty("ishaveperson");
		final boolean ishavedept = (Boolean) event.getCellItemVO().getCustProperty("ishavedept");
		final boolean ishavegg = (Boolean) event.getCellItemVO().getCustProperty("ishavegg");
		final String checkeduserid = event.getCellItemVO().getCustProperty("checkeduserid") + "";
		final String logid = event.getCellItemVO().getCustProperty("logid") + "";
		new SplashWindow(dr, "查询中,请稍候...", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				onWatchDetail(ishaveperson, ishavedept, ishavegg, checkeduserid, logid);
			}
		}, false);
	}

	public void onWatchDetail(boolean ishaveperson, boolean ishavedept, boolean ishavegg, String checkeduserid, String logid) {
		try {
			BillDialog bd = new BillDialog(this, "查看详细", 980, 600);
			WLTTabbedPane maintab = new WLTTabbedPane();
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			if (ishaveperson) {
				/*
				BillListPanel bl = new BillListPanel("SAL_PERSON_CHECK_SCORE_CODE1");
				bl.templetVO.setIsshowlistpagebar(false);
				bl.setPagePanelVisible(false);
				bl.getPageDescLabel().setVisible(true);
				bl.setQuickQueryPanelVisiable(false);
				bl.setDataFilterCustCondition(" targettype='员工定性指标' and scoretype='手动打分' and logid=" + logid + " and checkeduser=" + checkeduserid);
				bl.QueryDataByCondition(null);
				maintab.addTab("员工考核明细", bl);
				*/
				DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
				dr.getBillQueryPanel().setVisible(false);
				BillCellPanel bc = dr.getBillCellPanel();

				bc.loadBillCellData(ifc.getPersonTargetCheckResVO(logid, checkeduserid));
				bc.setEditable(false);
				bc.setToolBarVisiable(false);
				bc.setAllowShowPopMenu(false);
				maintab.addTab("员工考核明细", dr);
			}
			if (ishavegg) {
				BillListPanel bl = new BillListPanel("SAL_PERSON_CHECK_SCORE_CODE1");
				bl.templetVO.setIsshowlistpagebar(false);
				bl.setPagePanelVisible(false);
				bl.getPageDescLabel().setVisible(true);
				bl.setQuickQueryPanelVisiable(false);
				bl.setDataFilterCustCondition(" targettype='高管定性指标' and scoretype='手动打分' and logid=" + logid + " and checkeduser=" + checkeduserid);
				bl.QueryDataByCondition(null);
				maintab.addTab("高管考核明细", bl);
			}
			if (ishavedept) {
				/*	BillListPanel bl = new BillListPanel("SAL_DEPT_CHECK_SCORE_CODE3");
					bl.templetVO.setIsshowlistpagebar(false);
					bl.setPagePanelVisible(false);
					bl.getPageDescLabel().setVisible(true);
					bl.setQuickQueryPanelVisiable(false);
					bl.setDataFilterCustCondition(" checktype<>'垂直R' and targetid in (select targetid  from sal_dept_check_score where checktype='垂直R' and scoreuser=" + checkeduserid + " and logid=" + logid + ") and logid=" + logid);
					bl.QueryDataByCondition(null);
					maintab.addTab("分管部门考核明细", bl);*/
				DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
				dr.getBillQueryPanel().setVisible(false);
				BillCellPanel bc = dr.getBillCellPanel();
				bc.loadBillCellData(ifc.getPostTargetCheckResVO(bq.getRealValueAt("checkdate"), checkeduserid));
				bc.setEditable(false);
				bc.setToolBarVisiable(false);
				bc.setAllowShowPopMenu(false);
				maintab.addTab("分管部门考核明细", dr);

			}
			bd.getContentPane().add(maintab, BorderLayout.CENTER);
			bd.addOptionButtonPanel(new String[] { "取消" });
			bd.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
