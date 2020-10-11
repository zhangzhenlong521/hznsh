package cn.com.pushworld.salary.ui.report.p050;

import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.report.chart.BillChartPanel;

public class DeptYJReport_hm extends AbstractWorkPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Color[] beautifulColors = new Color[] { new Color(254, 128, 0), new Color(0, 128, 128), new Color(128, 0, 128), //
			new Color(254, 0, 254), new Color(0, 254, 254), new Color(128, 0, 254), new Color(0, 128, 0), new Color(254, 254, 128), new Color(128, 0, 128), new Color(64, 0, 128), new Color(128, 128, 0), new Color(128, 128, 192), new Color(254, 128, 192), new Color(0, 128, 254), new Color(128, 64, 0), //
			new Color(225, 213, 218), new Color(74, 63, 228), new Color(200, 234, 210), new Color(224, 242, 26), new Color(231, 192, 229), new Color(98, 69, 77), new Color(192, 188, 226), new Color(73, 129, 90), new Color(241, 244, 210), //
			new Color(225, 39, 214), new Color(195, 229, 226), new Color(169, 143, 80), new Color(225, 48, 94), new Color(100, 99, 108), new Color(42, 231, 99), new Color(153, 159, 87), new Color(96, 69, 94), new Color(74, 143, 138), new Color(244, 178, 18) }; //一系列好看的颜色,一共25个,依次交错排序

	@Override
	public void initialize() {
		BillChartPanel chartp = BillChartPanel.getInstance();
		JFreeChart chart = chartp.createBarChart2("1", "2", "3", new String[] { "行合计" }, new String[] { "行合计" }, true, new double[][] { { 1d, 2d, 4d } }, false, null);
		ChartPanel chartPanel = new ChartPanel(chart); //
		this.add(chartPanel);
	}

}
