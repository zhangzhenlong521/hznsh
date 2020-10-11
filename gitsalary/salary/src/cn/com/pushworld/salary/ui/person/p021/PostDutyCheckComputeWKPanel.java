package cn.com.pushworld.salary.ui.person.p021;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JTextField;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.templetvo.AbstractTMO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
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
 * 岗责评价解读查询.
 * @author haoming
 * create by 2014-3-25
 */
public class PostDutyCheckComputeWKPanel extends AbstractWorkPanel implements ActionListener ,BillCellHtmlHrefListener {

	private static final long serialVersionUID = -2743335801600774045L;

	private DefaultStyleReportPanel_2 dr = null;
	private BillQueryPanel bq = null;

	private String exportFileName = "岗责考核统计_";

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
				String logid = UIUtil.getStringValueByDS(null, "select id from sal_target_check_log where checkdate = '" + bq.getRealValueAt("checkdate") + "'");
				dr.getBillCellPanel().loadBillCellData(ifc.getPostDutyCheckCompute(logid));
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
		final String checkeduserid = event.getCellItemVO().getCustProperty("checkeduserid") + "";
		final String logid = event.getCellItemVO().getCustProperty("logid") + "";
		new SplashWindow(dr, "查询中,请稍候...", new AbstractAction() {
			public void actionPerformed(ActionEvent arg0) {
				onWatchDetail( checkeduserid, logid);
			}
		}, false);
	}

	public void onWatchDetail( String checkeduserid, String logid) {
		try {
			BillDialog bd = new BillDialog(this, "查看详细", 980, 600);
			WLTTabbedPane maintab = new WLTTabbedPane();
			SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
			DefaultStyleReportPanel_2 dr = new DefaultStyleReportPanel_2("SAL_TARGET_CHECK_LOG_PERSON", "");
			dr.getBillQueryPanel().setVisible(false);
			BillCellPanel bc = dr.getBillCellPanel();
			bc.loadBillCellData(ifc.getPostDutyDetailCompute(logid, checkeduserid));
			bc.setEditable(false);
			bc.setToolBarVisiable(false);
			bc.setAllowShowPopMenu(false);
			maintab.addTab("岗责考核明细", dr);
			bd.getContentPane().add(maintab, BorderLayout.CENTER);
			bd.addOptionButtonPanel(new String[] { "取消" });
			bd.setVisible(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
