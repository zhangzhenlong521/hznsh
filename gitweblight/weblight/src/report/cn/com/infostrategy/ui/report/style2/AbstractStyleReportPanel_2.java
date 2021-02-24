package cn.com.infostrategy.ui.report.style2;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.common.WLTButton;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;

/**
 * 风格报表2,上面上个查询框,下面是个BillCellPanel表格中!!
 * 将查询框中的条件送到后台,后台输出一HashVO[],然后前台将该数组塞到BillCellPanel中!!
 * @author xch
 *
 */
public abstract class AbstractStyleReportPanel_2 extends JPanel implements ActionListener {

	private BillQueryPanel billQueryPanel = null; //查询面板
	private BillCellPanel billCellPanel = null; //BillCellPanel
	private WLTButton btn_export_excel, btn_export_html; //导出按钮
	private String reportExpName = null;

	public abstract String getBillQueryTempletCode(); //查询模板编码...

	public abstract String getBSBuildDataClass(); //BS端构造数据的类名!!

	private JPanel panel_btn = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 2)); //

	public AbstractStyleReportPanel_2(boolean _isautoinit) {
		if (_isautoinit) {
			initialize(); //
		}
	}

	/**
	 * 构造方法..
	 */
	public AbstractStyleReportPanel_2() {
		initialize(); //
	}

	protected void initialize() {
		this.setLayout(new BorderLayout()); //
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
		billCellPanel = new BillCellPanel(); //
		billCellPanel.setToolBarVisiable(false); //
		billCellPanel.setAllowShowPopMenu(false); //
		billCellPanel.setEditable(false); //所有都不可编辑
		reportExpName = billQueryPanel.getTempletVO().getTempletname();
		billQueryPanel.addBillQuickActionListener(this); //
		this.add(billQueryPanel, BorderLayout.NORTH); //

		btn_export_excel = new WLTButton("导出Excel", UIUtil.getImage("icon_xls.gif")); //
		btn_export_html = new WLTButton("导出Html", UIUtil.getImage("zt_064.gif")); //
		btn_export_excel.addActionListener(this);
		btn_export_html.addActionListener(this);
		panel_btn.add(btn_export_excel);
		panel_btn.add(btn_export_html);

		JPanel panel_btn_cell = new JPanel(new BorderLayout()); //
		panel_btn_cell.add(panel_btn, BorderLayout.NORTH);
		panel_btn_cell.add(billCellPanel, BorderLayout.CENTER);
		this.add(panel_btn_cell, BorderLayout.CENTER); //
	}

	public void actionPerformed(ActionEvent e) {
		try {
			if (e.getSource() == billQueryPanel) {
				final HashMap map_condition = billQueryPanel.getQuickQueryConditionAsMap(); //取得所有查询条件!!
				if (map_condition == null) {
					return; //
				}

				new SplashWindow(billQueryPanel, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); //
							BillCellVO billCellVO = service.styleReport_2_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
							billCellPanel.loadBillCellData(billCellVO); //
							billCellPanel.setEditable(false); //所有都不可编辑
						} catch (Exception ex) {
							MessageBox.showException(billQueryPanel, ex); //
						}
					}
				});
			} else if (e.getSource() == btn_export_excel) {
				billCellPanel.exportExcel((reportExpName == null || "".equals(reportExpName.trim())) ? "报表数据导出" : reportExpName, "StyleReportPanel"); //袁江晓 20130911 更改  主要将风格报表和万维报表分开
			} else if (e.getSource() == btn_export_html) {
				billCellPanel.exportHtml((reportExpName == null || "".equals(reportExpName.trim())) ? "报表数据导出" : reportExpName); //
			}
		} catch (Exception ex) {
			MessageBox.showException(this, ex); //
		}
	}
	
	public BillQueryPanel getBillQueryPanel() {
		return billQueryPanel;
	}

	public BillCellPanel getBillCellPanel() {
		return billCellPanel;
	}

	public void setReportExpName(String reportExpName) {
		this.reportExpName = reportExpName;
	}
	public JPanel getPanel_btn() {
		return panel_btn;
	}

}
