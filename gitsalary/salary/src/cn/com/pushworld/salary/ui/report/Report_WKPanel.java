package cn.com.pushworld.salary.ui.report;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_ComboBox;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.pushworld.salary.ui.SalaryServiceIfc;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

public class Report_WKPanel extends AbstractWorkPanel implements ActionListener {
	private static final long serialVersionUID = 8843963611614427088L;
	private BillQueryPanel billQueryPanel = null;
	private BillCellPanel billCellPanel = null;
	private WLTButton btn_export_excel, btn_export_html;
	private String reportname = "报表名称";
	private String xmlname = "";
	private String queryname = "REPORTQUERY_CODE1";
	private static final Logger logger = WLTLogger.getLogger(Report_WKPanel.class);

	public void initialize() {
		if (getMenuConfMapValueAsStr("reportname") != null) {
			reportname = getMenuConfMapValueAsStr("reportname");
		}
		
		if (getMenuConfMapValueAsStr("xmlname") != null) {
			xmlname = getMenuConfMapValueAsStr("xmlname");
		}
		
		if (getMenuConfMapValueAsStr("queryname") != null) {
			queryname = getMenuConfMapValueAsStr("queryname");
		}
		
		this.setLayout(new BorderLayout());
		billQueryPanel = new BillQueryPanel(queryname);
		billQueryPanel.addBillQuickActionListener(this);

		QueryCPanel_UIRefPanel month_endRef = (QueryCPanel_UIRefPanel) billQueryPanel.getCompentByKey("month_end");
		String checkDate = new SalaryUIUtil().getCheckDate();
		month_endRef.setValue(checkDate);

		QueryCPanel_UIRefPanel month_startRef = (QueryCPanel_UIRefPanel) billQueryPanel.getCompentByKey("month_start");
		if (checkDate != null && checkDate.length() > 4) {
			month_startRef.setValue(checkDate.substring(0, 4) + "-01");
		}
		
		if(queryname.equals("REPORTQUERY_CODE4")){
			QueryCPanel_ComboBox counttypeRef = (QueryCPanel_ComboBox) billQueryPanel.getCompentByKey("counttype");
			counttypeRef.setValue("按员工");
		}

		billCellPanel = new BillCellPanel();
		billCellPanel.setToolBarVisiable(false); //隐藏工具栏
		billCellPanel.setAllowShowPopMenu(false);
		billCellPanel.setEditable(false);

		btn_export_excel = new WLTButton("导出Excel", UIUtil.getImage("icon_xls.gif"));
		btn_export_excel.addActionListener(this);

		btn_export_html = new WLTButton("导出Html", UIUtil.getImage("zt_064.gif"));
		btn_export_html.addActionListener(this);

		JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2));
		panel_btn.add(btn_export_excel);
		panel_btn.add(btn_export_html);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(panel_btn, BorderLayout.NORTH);
		panel.add(billCellPanel, BorderLayout.CENTER);

		this.add(billQueryPanel, BorderLayout.NORTH);
		this.add(panel, BorderLayout.CENTER);
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				if (xmlname.equals("")) {
					MessageBox.show(this, "请配置报表XML文件！");
					return;
				}
				
				String month_start = billQueryPanel.getRealValueAt("month_start");
				String month_end = billQueryPanel.getRealValueAt("month_end");
				

				if (month_start == null || month_start.equals("") || month_end == null || month_end.equals("")) {
					MessageBox.show(this, "请选择开始月份与结束月份！");
					return;
				}
				
				HashMap hm_where = new HashMap();
				hm_where.put("?month_start?", "'"+month_start+"'");
				hm_where.put("?month_end?", "'"+month_end+"'");
				
				if(queryname.equals("REPORTQUERY_CODE3")){
					String checktarget = billQueryPanel.getRealValueAt("checktarget");
					if (checktarget==null||checktarget.equals("")) {
						MessageBox.show(this, "请选择定量指标！");
						return;
					}
					
					hm_where.put("?checktarget?", "'"+checktarget.replace(";", "','")+"'");
				}
				
				if(queryname.equals("REPORTQUERY_CODE4")){
					String counttype = billQueryPanel.getRealValueAt("counttype");
					if (counttype == null || counttype.equals("")) {
						MessageBox.show(this, "请选择统计类型！");
						return;
					}
	                
					if (counttype.equals("按员工")) {
						xmlname = "salary_salary_person";
					} else if (counttype.equals("按部门")) {
						xmlname = "salary_salary_dept";
					} else if (counttype.equals("按岗位归类")) {
						xmlname = "salary_salary_post";
					}
					
					System.out.println("counttype="+counttype+"--xmlname="+xmlname);
					
					String salaryitems = billQueryPanel.getRealValueAt("salaryitems");
					if (salaryitems==null||salaryitems.equals("")) {
						MessageBox.show(this, "请选择工资条目！");
						return;
					}
					
					hm_where.put("?salaryitems?", "'"+salaryitems.replace(";", "','")+"'");
				}

				SalaryServiceIfc ifc = (SalaryServiceIfc) UIUtil.lookUpRemoteService(SalaryServiceIfc.class);
				BillCellVO bcvo = ifc.getReportCellVO(hm_where, xmlname);
               
				if(bcvo==null){
					MessageBox.show(this, "没有查询到结果！");
					return;
				}
				
				billCellPanel.setIfSetRowHeight(true);
				billCellPanel.loadBillCellData(bcvo);
				billCellPanel.setEditable(false);
				if (billCellPanel.getRowCount() > 2) {
					billCellPanel.setLockedCell(2, 1); //锁定表头
				}
			} else if (e.getSource() == btn_export_excel) {
				billCellPanel.exportExcel(reportname);
			} else if (e.getSource() == btn_export_html) {
				billCellPanel.exportHtml(reportname);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
