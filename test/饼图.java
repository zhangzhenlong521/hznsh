import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class 饼图 {
    ChartPanel frame1;
    public 饼图(){
        DefaultPieDataset data = getDataSet();
        JFreeChart chart = ChartFactory.createPieChart3D("全行存款增长目标完成情况",data,true,false,false);
//        // 设置外层图片 无边框 无背景色 背景图片透明
//        chart.setBorderVisible(false);
//        chart.setBackgroundPaint(null);
//        chart.setBackgroundImageAlpha(0.0f);
        chart.setBackgroundPaint(new ChartColor(25,25,112));
        // 设置标题颜色
        chart.getTitle().setPaint(ChartColor.yellow);

        //设置百分比
        PiePlot pieplot = (PiePlot) chart.getPlot();
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
        chart.getLegend().setItemFont(new Font("黑体",Font.BOLD,10));
        chart.getLegend().setItemPaint(Color.green);
        chart.getLegend().setBackgroundPaint(new ChartColor(0,0,128));
        piePlot.setSectionPaint(0,Color.yellow);// 饼里的颜色
        piePlot.setSectionPaint(1,Color.green);// 饼里的颜色
        frame1=new ChartPanel (chart,true);
    }
    private static DefaultPieDataset getDataSet() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("九0支行",100);
        dataset.setValue("农中支行",200);
//        dataset.setValue("城关支行",300);
//        dataset.setValue("解东支行",400);
//        dataset.setValue("西城支行",500);
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }

    public static void main(String[] args) {
        JFrame jFrame=new JFrame();
        jFrame.add(new 饼图().getChartPanel());
        jFrame.setSize(800,800);
        jFrame.setVisible(true);
    }
}
