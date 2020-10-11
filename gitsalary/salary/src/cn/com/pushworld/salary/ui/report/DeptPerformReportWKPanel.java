package cn.com.pushworld.salary.ui.report;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.BillTreePanel;
import cn.com.infostrategy.ui.mdata.BillTreeSelectListener;
import cn.com.infostrategy.ui.mdata.BillTreeSelectionEvent;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

public class DeptPerformReportWKPanel extends AbstractWorkPanel implements ActionListener, BillTreeSelectListener {

	private BillQueryPanel billQueryPanel = null; // ��ѯ���
	private BillChartPanel billChartPanel = null; // BilChartPanel
	private JPanel contentPanel = new JPanel(); // ������
	private boolean isShowTotalColumn = true;
	private HashMap map_condition = new HashMap();
	private String userdeptname = null;
	private String userpostid[] = null;
	private BillTreePanel billTreePanel = null;
	private WLTSplitPane splitPane = null;
	private JPanel panel = new JPanel();
	private String deptid = null;
	private Set hashset = new HashSet();

	public String getBSBuildDataClass() {
		return "cn.com.pushworld.salary.bs.report.DeptPerformReportBuilderAdapter";
	}; // BS�˹������ݵ�����!!

	public String getBillQueryTempletCode() {
		return "REPORTQUERY_CODE1_1";
	}

	/**
	 * ���췽��..
	 */
	public DeptPerformReportWKPanel() {
		initialize(); //
	}

	public DeptPerformReportWKPanel(HashMap hashMap) {
		this.map_condition = hashMap;
		initialize(); //
	}

	public void initialize() {
		deptid = ClientEnvironment.getCurrSessionVO().getLoginUserPKDept();
		userdeptname = ClientEnvironment.getCurrLoginUserVO().getDeptname();
		userpostid = ClientEnvironment.getCurrLoginUserVO().getAllPostNames();
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
		QueryCPanel_UIRefPanel month_endRef = (QueryCPanel_UIRefPanel)billQueryPanel.getCompentByKey("month_end");
		String checkDate = new SalaryUIUtil().getCheckDate();
		month_endRef.setValue(checkDate);
		
		QueryCPanel_UIRefPanel month_startRef = (QueryCPanel_UIRefPanel)billQueryPanel.getCompentByKey("month_start");
		if(checkDate!=null&&checkDate.length()>4){
			month_startRef.setValue(checkDate.substring(0, 4)+"-01");
		}
		
		billQueryPanel.addBillQuickActionListener(this); //	
		contentPanel.setLayout(new BorderLayout()); //
		BillCellPanel cellPanel = new BillCellPanel(null, true, false, true); //
		cellPanel.span(0, 2, 2, 4); //
		cellPanel.setValueAt("�������ѯ����������ѯ��ť!", 0, 2); //
		cellPanel.setHalign(new int[] { 0 }, new int[] { 2 }, 2); //
		contentPanel.add(cellPanel); // Ԥ����м�Ϊ�գ�Ȼ������ѯ����CHART
		billTreePanel = new BillTreePanel("PUB_CORP_DEPT_CODE1");
		billTreePanel.queryDataByCondition(null);
		billTreePanel.addBillTreeSelectListener(this);
		if (userdeptname.equals("���»�") || userdeptname.equals("���»�") || userpostid == null || userpostid[0] == null || userpostid[0].equals("")) {
			map_condition.put("targetid", "28;29;259;249;253;251;277;279;64;");
			hashset.add("1");
			hashset.add("53");
			hashset.add("83");
			hashset.add("85");
			panel.setLayout(new BorderLayout(5, 10)); //
			panel.add(billQueryPanel, BorderLayout.NORTH); //
			panel.add(contentPanel, BorderLayout.CENTER); //
			splitPane = new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT, billTreePanel, panel);
			splitPane.setDividerLocation(200);
			this.add(splitPane);
		} else {
			map_condition.put("deptid", deptid);
			this.setLayout(new BorderLayout(5, 10)); //
			this.add(billQueryPanel, BorderLayout.NORTH); //
			this.add(contentPanel, BorderLayout.CENTER); //
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == billQueryPanel) {
			doQuerypanel(deptid);
		}
	}

	private void doQuerypanel(String deptid) {
		if (deptid != null && !hashset.contains(deptid)) {
			map_condition.put("deptid", deptid);
		} else {
			map_condition.remove("deptid");
		}
		final HashMap deptmap = billQueryPanel.getQuickQueryConditionAsMap(); // ȡ�����в�ѯ����!!
		if (deptmap != null && deptmap.containsKey("month_start")) {
			map_condition.put("starttime", deptmap.get("month_start"));
		}
		if (deptmap != null && deptmap.containsKey("month_end")) {
			map_condition.put("endtime", deptmap.get("month_end"));
		}
		new SplashWindow(billQueryPanel, new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				try {
					ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); // ͨ��Զ�̷���������BillChartVO
					BillChartVO billChartVO = service.styleReport_3_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
					billChartPanel = new BillChartPanel(billChartVO.getTitle(), billChartVO.getXHeadName(), billChartVO.getYHeadName(), billChartVO, isShowTotalColumn);
					
					billChartPanel.getJTable_1().setSelectionMode(ListSelectionModel.SINGLE_SELECTION); //��״
					billChartPanel.getJTable_1().setRowSelectionInterval(1, 0);
					billChartPanel.getJTable_1().setColumnSelectionInterval(1, 0);
					
					billChartPanel.getJTable_2().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//����
					billChartPanel.getJTable_2().setRowSelectionInterval(1, 0);
					billChartPanel.getJTable_2().setColumnSelectionInterval(1, 0);
					
					
					billChartPanel.onDataTableSelectChanged(1, "������");
					billChartPanel.onDataTableSelectChanged(2, "������");
					
					
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

	public void onBillTreeSelectChanged(BillTreeSelectionEvent _event) {
		deptid = _event.getCurrSelectedVO().getStringValue("id");
		doQuerypanel(deptid);
	}

}
