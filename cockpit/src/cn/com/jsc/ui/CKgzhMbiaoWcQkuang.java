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
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 各支行存款目标完成情况
 */
public class CKgzhMbiaoWcQkuang {
    ChartPanel frame1;
    CategoryPlot plot;
    JFreeChart chart;
    public CKgzhMbiaoWcQkuang(){
        CategoryDataset dataset = getDataSet();
        chart = ChartFactory.createBarChart3D(
                DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"各支行存款目标完成情况(前三名&后三名)", // 图表标题
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
        chart.getTitle().setPaint(ChartColor.green);
        // 获得图表对象
        CategoryPlot p = chart.getCategoryPlot();
        // 设置图的背景颜色
        p.setBackgroundPaint(new ChartColor(25,25,112));
        // 设置表格线颜色
        p.setRangeGridlinePaint(ChartColor.blue);
//        // 设置外层图片 无边框 无背景色 背景图片透明
//        chart.setBorderVisible(false);
//        chart.setBackgroundPaint(null);
//        chart.setBackgroundImageAlpha(0.0f);
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
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setBaseOutlinePaint(Color.ORANGE);//柱子上的颜色
        renderer.setDrawBarOutline(true);//柱子边框
        renderer.setWallPaint(new ChartColor(25,25,112));//3D 墙体颜色
        //显示条目标签
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("黑体",Font.BOLD,14));
        renderer.setBaseItemLabelPaint(Color.YELLOW);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //设置条目标签显示的位置,outline表示在条目区域外,baseline_center表示基于基线且居中
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        //设定柱子上面的颜色bai
        BarRenderer3D customBarRenderer = (BarRenderer3D) plot.getRenderer();
        customBarRenderer.setSeriesPaint(0, Color.red); // 给series1 Bar
        customBarRenderer.setSeriesPaint(1, Color.orange); // 给series2 Bar
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

        frame1=new ChartPanel(chart,true);        //这里也能够用chartFrame,能够直接生成一个独立的Frame
        frame1.setOpaque(true);
        final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>各支行存款目标完成情况任务开始执行"+formatTemp.format(new Date()));
                try{
                    String grhqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_psn_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String grdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String dghgs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_fx_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String dgdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    if(grhqs==null || grdqs==null || dghgs==null || dgdqs==null){
                        System.out.println(">>>>>>>>>>>各支行存款目标完成情况退出任务");
                        return;
                    }else{
                        //zzl 记录修改的中间表
                        String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='各支行存款目标完成情况' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                        if(count==null){
                            chart.setTitle(DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"各支行存款目标完成情况(前三名&后三名)");
                            plot.setDataset(getDataSet());
                            UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='各支行存款目标完成情况'");
                            System.out.println(">>>>>>>>>>>各支行存款目标完成情况退出任务"+formatTemp.format(new Date()));
                        }else{
                            System.out.println(">>>>>>>>>>>各支行存款目标完成情况退出任务"+formatTemp.format(new Date()));
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
        String [][] data=service.getgzhWcqingkuang();
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
//        dataset.addValue(100, "9月", "九0支行");
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
