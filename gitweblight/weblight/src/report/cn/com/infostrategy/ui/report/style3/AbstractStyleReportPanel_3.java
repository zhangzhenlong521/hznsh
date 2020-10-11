package cn.com.infostrategy.ui.report.style3;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.JPanel;

import cn.com.infostrategy.to.report.BillChartVO;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.report.ReportServiceIfc;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

/**
 * 风格报表3,上面上个查询框,下面是个BillChartPanel
 * 将查询框中的条件送到后台,后台输出一HashVO[],然后前台将该数组塞到BillChartPanel中!!
 * 开放行统计控制显示
 * @author xch
 * 
 */
public abstract class AbstractStyleReportPanel_3 extends JPanel implements ActionListener {

	private BillQueryPanel billQueryPanel = null; // 查询面板
	private BillChartPanel billChartPanel = null; // BilChartPanel
	private JPanel contentPanel = new JPanel(); // 主内容
	private boolean isShowTotalColumn = true;

	public abstract String getBillQueryTempletCode(); // 查询模板编码...

	public abstract String getBSBuildDataClass(); // BS端构造数据的类名!!

	public AbstractStyleReportPanel_3(boolean _isautoinit) {
		if (_isautoinit) {
			initialize(); //
		}
	}

	/**
	 * 构造方法..
	 */
	public AbstractStyleReportPanel_3() {
		initialize(); //
	}

	protected void initialize() {
		this.setLayout(new BorderLayout(5,10)); //
		billQueryPanel = new BillQueryPanel(getBillQueryTempletCode()); //
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
				final HashMap map_condition = billQueryPanel.getQuickQueryConditionAsMap(); // 取得所有查询条件!!
				if (map_condition == null) {
					return; //
				}

				new SplashWindow(billQueryPanel, new AbstractAction() {
					public void actionPerformed(ActionEvent e) {
						try {
							ReportServiceIfc service = (ReportServiceIfc) UIUtil.lookUpRemoteService(ReportServiceIfc.class); // 通过远程方法，返回BillChartVO
							BillChartVO billChartVO = service.styleReport_3_BuildData(map_condition, getBSBuildDataClass(), ClientEnvironment.getCurrLoginUserVO()); //
							billChartPanel = new BillChartPanel(billChartVO.getTitle(), billChartVO.getXHeadName(), billChartVO.getYHeadName(), billChartVO, isShowTotalColumn);
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
