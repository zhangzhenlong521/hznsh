package cn.com.jsc.ui;

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
 * zzl
 * ȫ��ũ���������
 */
public class DkFarmersChart {
    ChartPanel frame1;
    public DkFarmersChart(){
        DefaultPieDataset data = getDataSet();
        JFreeChart chart = ChartFactory.createPieChart3D("ȫ��ũ���������",data,true,false,false);
        // �������ͼƬ �ޱ߿� �ޱ���ɫ ����ͼƬ͸��
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(null);
        chart.setBackgroundImageAlpha(0.0f);
        //���ðٷֱ�
        PiePlot pieplot = (PiePlot) chart.getPlot();
        DecimalFormat df = new DecimalFormat("0.00%");//���һ��DecimalFormat������Ҫ������С������
        NumberFormat nf = NumberFormat.getNumberInstance();//���һ��NumberFormat����
        StandardPieSectionLabelGenerator sp1 = new StandardPieSectionLabelGenerator("{0}  {2}", nf, df);//���StandardPieSectionLabelGenerator����
        pieplot.setLabelGenerator(sp1);//���ñ�ͼ��ʾ�ٷֱ�

        //û�����ݵ�ʱ����ʾ������
        pieplot.setNoDataMessage("��������ʾ");
        pieplot.setCircular(false);
        pieplot.setLabelGap(0.02D);

        pieplot.setIgnoreNullValues(true);//���ò���ʾ��ֵ
        pieplot.setIgnoreZeroValues(true);//���ò���ʾ��ֵ
        frame1=new ChartPanel(chart,true);
        chart.getTitle().setFont(new Font("����",Font.BOLD,20));//���ñ�������
        PiePlot piePlot= (PiePlot) chart.getPlot();//��ȡͼ���������
        piePlot.setLabelFont(new Font("����",Font.BOLD,10));//�������
        chart.getLegend().setItemFont(new Font("����",Font.BOLD,10));
    }
    private static DefaultPieDataset getDataSet() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("��0֧��",100);
        dataset.setValue("ũ��֧��",200);
        dataset.setValue("�ǹ�֧��",300);
        dataset.setValue("�ⶫ֧��",400);
        dataset.setValue("����֧��",500);
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
