package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl 全行存款增长目标完成情况
 * 柱状图
 */
public class CkPieChart {
    ChartPanel frame1;
    CategoryPlot plot;
    JFreeChart chart;
    public CkPieChart(){
    CategoryDataset dataset = getDataSet();
        chart= ChartFactory.createBarChart3D(
            DateUIUtil.getDateMonth(0,"yyyy年")+"全行存款增长目标完成情况   单位：亿元", // 图表标题
            "时间", // 文件夹轴的显示标签
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
        chart.getTitle().setPaint(ChartColor.green);
        // 获得图表对象
        CategoryPlot p = chart.getCategoryPlot();
        // 设置图的背景颜色
        p.setBackgroundPaint(new ChartColor(25,25,112));
        // 设置表格线颜色
        p.setRangeGridlinePaint(ChartColor.blue);
    // 设置外层图片 无边框 无背景色 背景图片透明
        chart.setBorderVisible(false);
//        chart.setBackgroundPaint(null);
//        chart.setBackgroundImageAlpha(0.0f);
        plot=chart.getCategoryPlot();//获取图表区域对象
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setBaseOutlinePaint(Color.ORANGE);//柱子上的颜色
        renderer.setDrawBarOutline(true);//柱子边框
        renderer.setWallPaint(new ChartColor(25,25,112));//3D 墙体颜色
        //显示条目标签
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("黑体",Font.BOLD,14));
        renderer.setBaseItemLabelPaint(Color.white);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //显示负值
        renderer.setNegativeItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12,TextAnchor.BASELINE_LEFT));
        //设置条目标签显示的位置,outline表示在条目区域外,baseline_center表示基于基线且居中
        //设定柱子上面的颜色bai
        BarRenderer3D customBarRenderer = (BarRenderer3D) plot.getRenderer();
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

       frame1=new ChartPanel(chart,true);        //这里也能够用chartFrame,能够直接生成一个独立的Frame
        frame1.setOpaque(true);
        final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>全行存款增长目标完成情况任务开始执行"+formatTemp.format(new Date()));
                try{
                    String grhqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_psn_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String grdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String dghgs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_fx_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String dgdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    if(grhqs==null || grdqs==null || dghgs==null || dgdqs==null){
                        System.out.println(">>>>>>>>>>>全行存款增长目标完成情况退出任务");
                        return;
                    }else{
                        //zzl 记录修改的中间表
                        String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='全行存款增长目标完成情况' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                        if(count==null){
                            chart.setTitle(DateUIUtil.getDateMonth(0,"yyyy年")+"全行存款增长目标完成情况   单位：亿元");
                            plot.setDataset(getDataSet());
                            UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='全行存款增长目标完成情况'");
                            System.out.println(">>>>>>>>>>>全行存款增长目标完成情况任务结束"+formatTemp.format(new Date()));
                        }else{
                            System.out.println(">>>>>>>>>>>全行存款增长目标完成情况任务结束"+formatTemp.format(new Date()));
                            return;
                        }

                    }
                }catch (Exception p){
                    p.printStackTrace();
                }
            }
        },new Date(),3600*1000*2);//

}
    private static CategoryDataset getDataSet() {
        CockpitServiceIfc service = null;
        try {
            service = (CockpitServiceIfc) UIUtil
                    .lookUpRemoteService(CockpitServiceIfc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String [][] data=service.getQhCkCompletion();
        String [][] data2=service.getQhCkCompletion2();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for(int i=0;i<data.length;i++){
            dataset.addValue(Double.parseDouble(data[i][0]),data[i][1],data[i][2]);
        }
        for(int i=0;i<data2.length;i++){
            dataset.addValue(Double.parseDouble(data2[i][0]),data2[i][1],data2[i][2]);
        }
//        dataset.addValue(100, "目标任务", "九0支行");
//        dataset.addValue(100, "实际完成", "九0支行");
//        dataset.addValue(100, "10月", "九0支行");
//        dataset.addValue(200, "8月", "农中支行");
//        dataset.addValue(200, "9月", "农中支行");
//        dataset.addValue(200, "10月", "农中支行");
//        dataset.addValue(300, "8月", "城关支行");
//        dataset.addValue(300, "9月", "城关支行");
//        dataset.addValue(300, "10月", "城关支行");
//        dataset.addValue(400, "8月", "解东支行");
//        dataset.addValue(400, "9月", "解东支行");
//        dataset.addValue(400, "10月", "解东支行");
//        dataset.addValue(500, "8月", "西城支行");
//        dataset.addValue(500, "9月", "西城支行");
//        dataset.addValue(500, "10月", "西城支行");
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
