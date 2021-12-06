package com.pushworld.icase.ui.p010;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class JChartTest extends JFrame {
	private static CategoryDataset getDataSet() {
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		ds.addValue(100, "北京", "苹果");
		ds.addValue(100, "上海", "苹果");
		ds.addValue(100, "广州", "苹果");
		ds.addValue(200, "北京", "梨子");
		ds.addValue(200, "上海", "梨子");
		ds.addValue(200, "广州", "梨子");
		ds.addValue(300, "北京", "葡萄");
		ds.addValue(300, "上海", "葡萄");
		ds.addValue(300, "广州", "葡萄");
		ds.addValue(400, "北京", "橘子");
		ds.addValue(400, "上海", "橘子");
		ds.addValue(400, "广州", "橘子");
		ds.addValue(500, "北京", "香蕉");
		ds.addValue(500, "上海", "香蕉");
		ds.addValue(500, "广州", "香蕉");
		return ds;
	}

	public static ChartPanel createChart() {
		JFreeChart chart = ChartFactory.createBarChart3D("水果产量图", // 图表标题
				"业务类型", // 目录轴的显示标签
				"个数", // 数值轴的显示标签
				getDataSet(), // 数据集
				PlotOrientation.VERTICAL, // 图表方向
				true, // 是否显示图例，对于简单的柱状图必须为false
				false, // 是否生成提示工具
				false); // 是否生成url链接
		CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
		NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();
		CategoryAxis domainAxis = categoryplot.getDomainAxis();
		/*------设置X轴坐标上的文字-----------*/
		domainAxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 11));
		/*------设置X轴的标题文字------------*/
		domainAxis.setLabelFont(new Font("宋体", Font.PLAIN, 12));
		/*------设置Y轴坐标上的文字-----------*/
		numberaxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));
		/*------设置Y轴的标题文字------------*/
		numberaxis.setLabelFont(new Font("黑体", Font.PLAIN, 12));
		/*------这句代码解决了底部汉字乱码的问题-----------*/
		chart.getLegend().setItemFont(new Font("宋体", Font.PLAIN, 12));
		chart.getTitle().setFont(new Font("宋体", Font.PLAIN, 12));
		return new ChartPanel(chart);
	}

	public static void main(String[] args) {
		JFrame jf = new JFrame();
		jf.setSize(1000, 800);
		jf.add(createChart());
		jf.setVisible(true);
	}
}
