package cn.com.jsc.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;

/***
 * zzl 客户经理营销存款排名
 * 柱状图
 */
public class CkBarChart {
    ChartPanel frame1;
    public CkBarChart(){
        CategoryDataset dataset = getDataSet();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "客户经理营销存款排名", // 图表标题
                "部门", // 文件夹轴的显示标签
                "数量", // 数值轴的显示标签
                dataset, // 数据集
                PlotOrientation.VERTICAL, // 图表方向：水平、垂直
                true,           // 是否显示图例(对于简单的柱状图必须是false)
                false,          // 是否生成工具
                false           // 是否生成URL链接
        );
        // 设置外层图片 无边框 无背景色 背景图片透明
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(null);
        chart.setBackgroundImageAlpha(0.0f);

        //从这里開始
        CategoryPlot plot=chart.getCategoryPlot();//获取图表区域对象
        CategoryAxis domainAxis=plot.getDomainAxis();         //水平底部列表
        domainAxis.setLabelFont(new Font("黑体",Font.BOLD,14));         //水平底部标题
        domainAxis.setTickLabelFont(new Font("宋体",Font.BOLD,12));  //垂直标题
        ValueAxis rangeAxis=plot.getRangeAxis();//获取柱状
        rangeAxis.setLabelFont(new Font("黑体",Font.BOLD,15));
        chart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15));
        chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
        //到这里结束，尽管代码有点多，但仅仅为一个目的，解决汉字乱码问题

        frame1=new ChartPanel(chart,true);        //这里也能够用chartFrame,能够直接生成一个独立的Frame
        frame1.setOpaque(true);

    }
    private static CategoryDataset getDataSet() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100, "8月", "九0支行");
        dataset.addValue(100, "9月", "九0支行");
        dataset.addValue(100, "10月", "九0支行");
        dataset.addValue(200, "8月", "农中支行");
        dataset.addValue(200, "9月", "农中支行");
        dataset.addValue(200, "10月", "农中支行");
        dataset.addValue(300, "8月", "城关支行");
        dataset.addValue(300, "9月", "城关支行");
        dataset.addValue(300, "10月", "城关支行");
        dataset.addValue(400, "8月", "解东支行");
        dataset.addValue(400, "9月", "解东支行");
        dataset.addValue(400, "10月", "解东支行");
        dataset.addValue(500, "8月", "西城支行");
        dataset.addValue(500, "9月", "西城支行");
        dataset.addValue(500, "10月", "西城支行");
        return dataset;
    }
//    private static CategoryDataset getDataSet() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.addValue(100, "九0支行", "九0支行");
//        dataset.addValue(200, "农中支行", "农中支行");
//        dataset.addValue(300, "城关支行", "城关支行");
//        dataset.addValue(400, "解东支行", "解东支行");
//        dataset.addValue(500, "西城支行", "西城支行");
//        return dataset;
//    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
