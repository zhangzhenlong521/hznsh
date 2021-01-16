package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 全行农户贷款覆盖面
 */
public class DkFarmersChart {
    ChartPanel frame1;
    PiePlot pieplot;
    JFreeChart chart;
    public DkFarmersChart(){
        DefaultPieDataset data = getDataSet();
        chart = ChartFactory.createPieChart3D(DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"全行农户贷款覆盖面",data,true,false,false);
        // 设置外层图片 无边框 无背景色 背景图片透明
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(null);
        chart.setBackgroundImageAlpha(0.0f);
        //设置百分比
        pieplot = (PiePlot) chart.getPlot();
        DecimalFormat df = new DecimalFormat("0.00%");//获得一个DecimalFormat对象，主要是设置小数问题
        NumberFormat nf = NumberFormat.getNumberInstance();//获得一个NumberFormat对象
        StandardPieSectionLabelGenerator sp1 = new StandardPieSectionLabelGenerator("{0}  {2}", nf, df);//获得StandardPieSectionLabelGenerator对象
        pieplot.setLabelGenerator(sp1);//设置饼图显示百分比

        //没有数据的时候显示的内容
        pieplot.setNoDataMessage("无数据显示");
        pieplot.setCircular(false);
        pieplot.setLabelGap(0.02D);

        pieplot.setIgnoreNullValues(true);//设置不显示空值
        pieplot.setIgnoreZeroValues(true);//设置不显示负值
        frame1=new ChartPanel(chart,true);
        chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
        PiePlot piePlot= (PiePlot) chart.getPlot();//获取图表区域对象
        piePlot.setLabelFont(new Font("宋体",Font.BOLD,10));//解决乱码
        chart.getLegend().setItemFont(new Font("黑体",Font.BOLD,10));
        final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>全行农户贷款覆盖面任务开始执行"+formatTemp.format(new Date()));
                try{
                    String dk=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    if(dk==null){
                        System.out.println(">>>>>>>>>>>全行农户贷款覆盖面任务结束"+formatTemp.format(new Date()));
                    }else{
                        //zzl 记录修改的中间表
                        String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='全行农户贷款覆盖面' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                        if(count==null){
                            chart.setTitle(DateUIUtil.getDateMonth(a++,"yyyy年MM月dd日")+"全行农户贷款覆盖面");
                            pieplot.setDataset(getDataSet());
                            UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='全行农户贷款覆盖面'");
                            System.out.println(">>>>>>>>>>>全行农户贷款覆盖面任务结束"+formatTemp.format(new Date()));
                        }else{
                            System.out.println(">>>>>>>>>>>全行农户贷款覆盖面任务结束"+formatTemp.format(new Date()));
                            return;
                        }
                    }
                }catch (Exception p){
                    p.printStackTrace();
                }
            }
        },new Date(),2* 3600*1000);//2* 3600*1000
    }
    private static DefaultPieDataset getDataSet() {
        CockpitServiceIfc service = null;
        try {
            service = (CockpitServiceIfc) UIUtil
                    .lookUpRemoteService(CockpitServiceIfc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String [][] data=service.getNhFgMian();
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue(data[0][1]+"-"+data[0][2]+"户",Integer.parseInt(data[0][2]));
        dataset.setValue(data[0][3]+"-"+data[0][4]+"户",Integer.parseInt(data[0][4]));
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
