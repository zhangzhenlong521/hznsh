package cn.com.jsc.ui;

import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class 柱状图 {
    ChartPanel frame1;
    CategoryPlot plot;
    public 柱状图(){
        CategoryDataset dataset = getDataSet(1);
        JFreeChart chart = ChartFactory.createBarChart3D(
                "全年贷款目标完成情况", // 图表标题
                "部门", // 文件夹轴的显示标签
                "数量", // 数值轴的显示标签
                dataset, // 数据集
                PlotOrientation.VERTICAL, // 图表方向：水平、垂直
                true,           // 是否显示图例(对于简单的柱状图必须是false)
                false,          // 是否生成工具
                false           // 是否生成URL链接
        );
            // 设置总的背景颜色
            chart.setBackgroundPaint(new ChartColor(25,25,112));
    // 设置标题颜色
            chart.getTitle().setPaint(ChartColor.white);
    // 获得图表对象
            CategoryPlot p = chart.getCategoryPlot();
    // 设置图的背景颜色
            p.setBackgroundPaint(new ChartColor(25,25,112));
    // 设置表格线颜色
            p.setRangeGridlinePaint(ChartColor.blue);
//        // 设置外层图片 无边框 无背景色 背景图片透明
//          chart.setBorderVisible(false);
//        chart.setBackgroundPaint(null);
//          chart.setBackgroundImageAlpha(0.0f);
//        chart.getLegend(); 底部
        // X 轴
//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setLabelFont(xfont);// 轴标题
//        domainAxis.setTickLabelFont(xfont);// 轴数值
//        domainAxis.setTickLabelPaint(Color.BLUE) ; // 字体颜色
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // 横轴上的label斜显示
//
//        // Y 轴
//        ValueAxis rangeAxis = plot.getRangeAxis();
//        rangeAxis.setLabelFont(yfont);
//        rangeAxis.setLabelPaint(Color.BLUE) ; // 字体颜色
//        rangeAxis.setTickLabelFont(yfont);
        //从这里開始
        plot=chart.getCategoryPlot();//获取图表区域对象
        plot.setOutlinePaint(Color.magenta);//图标的边框颜色
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setBaseOutlinePaint(Color.ORANGE);//柱子上的颜色
        renderer.setDrawBarOutline(true);//柱子边框
        renderer.setWallPaint(new ChartColor(25,25,112));//3D 墙体颜色
        //显示条目标签
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelPaint(Color.yellow);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //设置条目标签显示的位置,outline表示在条目区域外,baseline_center表示基于基线且居中
        //设定柱子上面的颜色bai
        BarRenderer customBarRenderer = (BarRenderer) plot.getRenderer();
        customBarRenderer.setSeriesPaint(0, Color.yellow); // 给series1 Bar
        customBarRenderer.setSeriesPaint(1, Color.green); // 给series2 Bar
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        CategoryAxis domainAxis=plot.getDomainAxis();         //X 轴
        domainAxis.setLabelFont(new Font("黑体",Font.BOLD,14));   //X 轴 文字
        domainAxis.setLabelPaint(Color.orange);
        domainAxis.setTickLabelFont(new Font("宋体",Font.BOLD,12));  //X 轴 数字
        domainAxis.setTickLabelPaint(Color.yellow) ; // 字体颜色 X
        ValueAxis rangeAxis=plot.getRangeAxis();//Y 轴
        rangeAxis.setLabelFont(new Font("黑体",Font.BOLD,15)); //Y 轴 文字
        rangeAxis.setLabelPaint(Color.orange);
        rangeAxis.setTickLabelPaint(Color.green) ; // Y 轴 文字

        chart.getLegend().setItemFont(new Font("黑体", Font.BOLD, 15)); //底部
        chart.getLegend().setItemPaint(Color.green);
        chart.getLegend().setBackgroundPaint(new ChartColor(0,0,128));
        chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
        //到这里结束，尽管代码有点多，但仅仅为一个目的，解决汉字乱码问题
        SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat formatTemp2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        formatTemp2.format(date);
        Date date1= null;
        try {
            date1 = formatTemp.parse(formatTemp2.format(date)+" 10:30:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>任务开始执行"+a++);
                plot.setDataset(getDataSet(a++));
            }
        },date1,24* 3600*1000);//
        frame1=new ChartPanel(chart,true);        //这里也能够用chartFrame,能够直接生成一个独立的Frame
        frame1.setOpaque(true);

    }
    private static CategoryDataset getDataSet(int a) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100*a, "九0支行", "九0支行");
        dataset.addValue(200*a, "农中支行", "农中支行");
        dataset.addValue(300*a, "城关支行", "城关支行");
        dataset.addValue(400*a, "解东支行", "解东支行");
        dataset.addValue(500*a, "西城支行", "西城支行");
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }

    public static void main(String[] args) {
        JFrame jFrame=new JFrame();
        jFrame.add(new 柱状图().getChartPanel());
        jFrame.setSize(1000,1000);
        jFrame.setVisible(true);
    }
}
