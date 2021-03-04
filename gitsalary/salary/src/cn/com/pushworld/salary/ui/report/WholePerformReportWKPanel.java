package cn.com.pushworld.salary.ui.report;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

public class WholePerformReportWKPanel extends AbstractWorkPanel implements ActionListener {

	private BillQueryPanel billQueryPanel = null; // 查询面板
	private BillChartPanel billChartPanel = null; // BilChartPanel
	private JPanel contentPanel = new JPanel(); // 主内容
	private boolean isShowTotalColumn = true;
	private HashMap map_condition = new HashMap();

	public String getBSBuildDataClass() {
		return "cn.com.pushworld.salary.bs.report.WholePerformReportBuilderAdapter";
	}; // BS端构造数据的类名!!

	public String getBillQueryTempletCode() {
		return "REPORTQUERY_CODE2_1";
	}

	/**
	 * 构造方法..
	 */
	public WholePerformReportWKPanel() {
		initialize(); //
	}

	public WholePerformReportWKPanel(HashMap hashMap) {
		this.map_condition = hashMap;
		initialize(); //
	}

	public void initialize() {
		this.setLayout(new BorderLayout(5, 10)); //
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
		QueryCPanel_UIRefPanel month_endRef = (QueryCPanel_UIRefPanel) billQueryPanel.getCompentByKey("month_end");
		String checkDate = new SalaryUIUtil().getCheckDate();
		month_endRef.setValue(checkDate);

		QueryCPanel_UIRefPanel month_startRef = (QueryCPanel_UIRefPanel) billQueryPanel.getCompentByKey("month_start");
		if (checkDate != null && checkDate.length() > 4) {
			month_startRef.setValue(checkDate.substring(0, 4) + "-01");
		}

		billQueryPanel.addBillQuickActionListener(this); //	
		contentPanel.setLayout(new BorderLayout()); //
		BillCellPanel cellPanel = new BillCellPanel(null, true, false, true); //
		cellPanel.span(0, 2, 2, 4); //
		cellPanel.setValueAt("请输入查询条件后点击查询按钮!", 0, 2); //
		cellPanel.setHalign(new int[] { 0 }, new int[] { 2 }, 2); //
		contentPanel.add(cellPanel); // 预设的中间为空，然后点击查询出来CHART

		this.add(billQueryPanel, BorderLayout.NORTH); //
		this.add(contentPanel, BorderLayout.CENTER); //
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				final HashMap deptmap = billQueryPanel.getQuickQueryConditionAsMap(); // 取得所有查询条件!!
				if (deptmap != null && deptmap.containsKey("querycorp")) {
					map_condition.put("querycorp", deptmap.get("querycorp"));
				}
				if (deptmap != null && deptmap.containsKey("month_start")) {
					map_condition.put("starttime", deptmap.get("month_start"));
				}
				if (deptmap != null && deptmap.containsKey("month_end")) {
					map_condition.put("endtime", deptmap.get("month_end"));
				}
				new SplashWindow(billQueryPanel, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); // 通过远程方法，返回BillChartVO
							BillChartVO billChartVO = service.styleReport_3_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
							billChartPanel = new BillChartPanel(billChartVO.getTitle(), billChartVO.getXHeadName(), billChartVO.getYHeadName(), billChartVO, isShowTotalColumn);
							billChartPanel.getJTable_1().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //柱状
							billChartPanel.getJTable_1().setRowSelectionInterval(1, 0);
							billChartPanel.getJTable_1().setColumnSelectionInterval(1, 0);
							
							billChartPanel.getJTable_2().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//曲线
							billChartPanel.getJTable_2().setRowSelectionInterval(1, 0);
							billChartPanel.getJTable_2().setColumnSelectionInterval(1, 0);
							
							
							billChartPanel.onDataTableSelectChanged(1, "行数据");
							billChartPanel.onDataTableSelectChanged(2, "行数据");
							contentPanel.removeAll(); //
							contentPanel.setLayout(new BorderLayout()); //
							contentPanel.add(billChartPanel, BorderLayout.CENTER); //
							contentPanel.updateUI(); //

						} catch (Exception ex) {
							MessageBox.showException(billQueryPanel, ex); //
						}
					}
				});
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}

	public boolean isShowTotalColumn() {
		return isShowTotalColumn;
	}

	public void setShowTotalColumn(boolean newvalue) {
		this.isShowTotalColumn = newvalue;
	}

	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	public BillChartPanel getBillChartPanel() {
		return billChartPanel;
	}

}
