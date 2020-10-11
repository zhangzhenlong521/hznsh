package cn.com.pushworld.salary.ui.posteval.p050;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Timer;
import java.util.TimerTask;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * 岗位价值评估进度统计【李春娟/2013-10-31】
 * @author lcj
 *
 */
public class PostEvalScheduleWKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private String exportFileName = "岗位评估进度_";
	private DefaultStyleReportPanel_2 reportPanel = null;
	private BillQueryPanel queryPanel = null;
	private boolean isquerying = false;
	private SalaryServiceIfc service = null;

	public void initialize() {
		reportPanel = new DefaultStyleReportPanel_2("SAL_POST_EVAL_PLAN_LCJ_Q01", "");//只能选择评估中或评估结束的计划，因为未评估的计划还没有创建评分记录，故查不到记录
		reportPanel.setReportExpName(exportFileName);//设置导出文件名称
		queryPanel = reportPanel.getBillQueryPanel();
		reportPanel.getBillCellPanel().setEditable(false);
		queryPanel.addBillQuickActionListener(this);
		this.add(reportPanel);
		try {
			HashVO[] planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_POST_EVAL_PLAN where status='评估中'");
			if (planvos != null && planvos.length > 0) {//默认选中当前评价中中计划，并且统计出来
				QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("planid");
				RefItemVO itemvo = new RefItemVO(planvos[0].getStringValue("id"), "", planvos[0].getStringValue("planname"));
				dateRef.setObject(itemvo);
				new Timer().schedule(new TimerTask() {
					public void run() {
						onQuery();
					}
				}, 0);
			} else {//如果没有评价中的计划，则找最近结束的计划
				planvos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_POST_EVAL_PLAN where status='评估结束' order by enddate desc");
				if (planvos != null && planvos.length > 0) {//默认选中最后结束的计划，并且统计出来
					QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) queryPanel.getCompentByKey("planid");
					RefItemVO itemvo = new RefItemVO(planvos[0].getStringValue("id"), "", planvos[0].getStringValue("planname"));
					dateRef.setObject(itemvo);
					new Timer().schedule(new TimerTask() {
						public void run() {
							onQuery();
						}
					}, 0);
				}
			}

		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent e) {
		onQuery();
	}

	private void onQuery() {
		if (isquerying) {
			return;
		}
		isquerying = true;
		try {
			if (queryPanel.checkValidate()) {
				BillCellPanel cellPanel = reportPanel.getBillCellPanel();
				cellPanel.loadBillCellData(getService().getPostEvalScheduleVO(queryPanel.getRealValueAt("planid")));
				cellPanel.setEditable(false);
				if (cellPanel.getRowCount() > 7) {
					cellPanel.setLockedCell(7, 1); //锁定表头
					cellPanel.getTable().getColumnModel().getColumn(0).setPreferredWidth(150);
					cellPanel.getTable().getColumnModel().getColumn(3).setPreferredWidth(100);
					cellPanel.getTable().getColumnModel().getColumn(4).setPreferredWidth(100);
					reportPanel.setReportExpName(exportFileName + queryPanel.getValueAt("planid"));//设置导出文件名称
					new SalaryUIUtil().formatSpan(cellPanel, new int[] { 0 });
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			MessageBox.showException(this, ex);
		}
		isquerying = false;
	}

	private SalaryServiceIfc getService() throws Exception {
		if (service == null) {
			service = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
		}
		return service;
	}
}
