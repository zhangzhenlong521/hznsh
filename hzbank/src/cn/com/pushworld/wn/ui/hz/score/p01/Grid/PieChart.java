package cn.com.pushworld.wn.ui.hz.score.p01.Grid;


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


/**
 * PieChart
 * zzl
 * ��ӱ�ͼ
 * @author Dragon
 * @date 2021/3/25
 */
public class PieChart {
    ChartPanel frame1;
    PiePlot pieplot;
    JFreeChart chart;
    public DefaultPieDataset dataset=null;
    public String str=null;
    public PieChart(String str,DefaultPieDataset dataset){
        this.str=str;
        this.dataset=dataset;
        DefaultPieDataset data = getDataSet();
        chart = ChartFactory.createPieChart3D(str+"������",data,true,false,false);
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(null);
        chart.setBackgroundImageAlpha(0.0f);

        // ���ñ�����ɫ
        chart.getTitle().setPaint(ChartColor.BLACK);

        //���ðٷֱ�
        pieplot = (PiePlot) chart.getPlot();
        pieplot.setBackgroundPaint(null);//zzl ���ñ�ͼ�ı���
        pieplot.setBackgroundImageAlpha(0.0f);
        DecimalFormat df = new DecimalFormat("0.00%");//���һ��DecimalFormat������Ҫ������С������
        NumberFormat nf = NumberFormat.getNumberInstance();//���һ��NumberFormat����
        StandardPieSectionLabelGenerator sp1 = new StandardPieSectionLabelGenerator("{0}  {2}", nf, df);//���StandardPieSectionLabelGenerator����
        pieplot.setLabelGenerator(sp1);//���ñ�ͼ��ʾ�ٷֱ�

        //û�����ݵ�ʱ����ʾ������
        pieplot.setNoDataMessage("��������ʾ");
        pieplot.setCircular(false);
        pieplot.setLabelGap(0.02D);
//        // ����ɫ ͸����
//        pieplot.setBackgroundAlpha(0.5f);
//        // ǰ��ɫ ͸����
//        pieplot.setForegroundAlpha(0.5f);

        pieplot.setIgnoreNullValues(true);//���ò���ʾ��ֵ
        pieplot.setIgnoreZeroValues(true);//���ò���ʾ��ֵ
        chart.getTitle().setFont(new Font("����",Font.BOLD,20));//���ñ�������
        PiePlot piePlot= (PiePlot) chart.getPlot();//��ȡͼ���������
        piePlot.setLabelFont(new Font("����",Font.BOLD,10));//�������
        piePlot.setLabelPaint(Color.green);
        piePlot.setLabelBackgroundPaint(new ChartColor(0,0,128));
        chart.getLegend().setItemFont(new Font("����",Font.BOLD,12));
        chart.getLegend().setItemPaint(Color.yellow);
        chart.getLegend().setBackgroundPaint(new ChartColor(0,0,128));
        piePlot.setSectionPaint(0,Color.yellow);// �������ɫ
        piePlot.setSectionPaint(1,Color.green);// �������ɫ
        frame1=new ChartPanel (chart,true);
        chart.getTitle().setFont(new Font("����",Font.BOLD,20));//���ñ�������
        piePlot.setLabelFont(new Font("����",Font.BOLD,12));//�������
        piePlot.setLabelPaint(Color.white);
        chart.getLegend().setItemFont(new Font("����",Font.BOLD,12));
    }
    public DefaultPieDataset getDataSet() {
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
