package cn.com.pushworld.wn.ui.hz.score.p01.Grid;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.jsc.ui.CockpitServiceIfc;
import cn.com.jsc.ui.DateUIUtil;
import org.jfree.chart.ChartColor;
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
 * PieChart
 * zzl
 * 添加饼图
 * @author Dragon
 * @date 2021/3/25
 */
public class PieChart {
    ChartPanel frame1;
    PiePlot pieplot;
    JFreeChart chart;
    public DefaultPieDataset dataset=null;
    public String str=null;
    public PieChart(){
        DefaultPieDataset data = getDataSet();
        chart = ChartFactory.createPieChart3D(str+"覆盖面",data,true,false,false);
        chart.setBackgroundPaint(new ChartColor(25,25,112));
        // 设置标题颜色
        chart.getTitle().setPaint(ChartColor.yellow);

        //设置百分比
        pieplot = (PiePlot) chart.getPlot();
        pieplot.setBackgroundPaint(new ChartColor(25,25,112));
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
        chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
        PiePlot piePlot= (PiePlot) chart.getPlot();//获取图表区域对象
        piePlot.setLabelFont(new Font("宋体",Font.BOLD,10));//解决乱码
        piePlot.setLabelPaint(Color.green);
        piePlot.setLabelBackgroundPaint(new ChartColor(0,0,128));
        chart.getLegend().setItemFont(new Font("黑体",Font.BOLD,12));
        chart.getLegend().setItemPaint(Color.yellow);
        chart.getLegend().setBackgroundPaint(new ChartColor(0,0,128));
        piePlot.setSectionPaint(0,Color.yellow);// 饼里的颜色
        piePlot.setSectionPaint(1,Color.green);// 饼里的颜色
        frame1=new ChartPanel (chart,true);
        chart.getTitle().setFont(new Font("宋体",Font.BOLD,20));//设置标题字体
        piePlot.setLabelFont(new Font("宋体",Font.BOLD,12));//解决乱码
        piePlot.setLabelPaint(Color.white);
        chart.getLegend().setItemFont(new Font("黑体",Font.BOLD,12));
    }
    public DefaultPieDataset getDataSet() {
        return dataset;
    }
    public ChartPanel getChartPanel(String str,DefaultPieDataset dataset){
        this.str=str;
        this.dataset=dataset;
        return frame1;

    }
}
