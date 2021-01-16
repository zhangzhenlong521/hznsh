package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl ck 各支行贷款目标完成情况()
 */
public class DkTimeSeriesChart {
    ChartPanel frame1;
    CategoryPlot plot;
    JFreeChart chart;
    public DkTimeSeriesChart(){
        CategoryDataset dataset = getDataSet();
        chart = ChartFactory.createBarChart3D(
                DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"各支行贷款目标完成情况(前三名&后三名) 单位:亿元", // 图表标题
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
        plot=chart.getCategoryPlot();//获取图表区域对象
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //设置条目标签显示的位置,outline表示在条目区域外,baseline_center表示基于基线且居中
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
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
        final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>各支行贷款目标完成情况开始执行"+formatTemp.format(new Date()));
                try{
                    String dk=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    if(dk==null){
                        System.out.println(">>>>>>>>>>>全全年贷款目标完成情况任务结束"+formatTemp.format(new Date()));
                    }else{
                        //zzl 记录修改的中间表
                        String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='各支行贷款目标完成情况' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                        if(count==null){
                            chart.setTitle(DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"各支行贷款目标完成情况(前三名&后三名) 单位:亿元");
                            plot.setDataset(getDataSet());
                            UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='各支行贷款目标完成情况'");
                            System.out.println(">>>>>>>>>>>各支行贷款目标完成情况任务结束"+formatTemp.format(new Date()));
                        }else{
                            System.out.println(">>>>>>>>>>>各支行贷款目标完成情况任务结束"+formatTemp.format(new Date()));
                            return;
                        }
                    }
                }catch (Exception p){
                    p.printStackTrace();
                }
            }
        },new Date(),2* 3600*1000);//

    }
    private static CategoryDataset getDataSet() {  //这个数据集有点多，但都不难理解
        CockpitServiceIfc service = null;
        try {
            service = (CockpitServiceIfc) UIUtil
                    .lookUpRemoteService(CockpitServiceIfc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String [][] data=service.getDKgzhWcqingkuang();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        //zzl 前三名
        for(int i=0;i<3;i++){
            dataset.addValue(Double.parseDouble(data[i][0]),data[i][1],data[i][2]);
            dataset.addValue(Double.parseDouble(data[i][3]),data[i][4],data[i][5]);
        }
        //zzl 后三名
        for(int i=0;i<3;i++){
            int row=data.length-3+i;
            dataset.addValue(Double.parseDouble(data[row][0]),data[row][1],data[row][2]);
            dataset.addValue(Double.parseDouble(data[row][3]),data[row][4],data[row][5]);
        }
//        dataset.addValue(100, "8月", "九0支行");
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
