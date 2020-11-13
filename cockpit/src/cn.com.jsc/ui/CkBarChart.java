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
 * zzl �ͻ�����Ӫ���������
 * ��״ͼ
 */
public class CkBarChart {
    ChartPanel frame1;
    public CkBarChart(){
        CategoryDataset dataset = getDataSet();
        JFreeChart chart = ChartFactory.createBarChart3D(
                "�ͻ�����Ӫ���������", // ͼ�����
                "����", // �ļ��������ʾ��ǩ
                "����", // ��ֵ�����ʾ��ǩ
                dataset, // ���ݼ�
                PlotOrientation.VERTICAL, // ͼ����ˮƽ����ֱ
                true,           // �Ƿ���ʾͼ��(���ڼ򵥵���״ͼ������false)
                false,          // �Ƿ����ɹ���
                false           // �Ƿ�����URL����
        );
        // �������ͼƬ �ޱ߿� �ޱ���ɫ ����ͼƬ͸��
        chart.setBorderVisible(false);
        chart.setBackgroundPaint(null);
        chart.setBackgroundImageAlpha(0.0f);

        //�������_ʼ
        CategoryPlot plot=chart.getCategoryPlot();//��ȡͼ���������
        CategoryAxis domainAxis=plot.getDomainAxis();         //ˮƽ�ײ��б�
        domainAxis.setLabelFont(new Font("����",Font.BOLD,14));         //ˮƽ�ײ�����
        domainAxis.setTickLabelFont(new Font("����",Font.BOLD,12));  //��ֱ����
        ValueAxis rangeAxis=plot.getRangeAxis();//��ȡ��״
        rangeAxis.setLabelFont(new Font("����",Font.BOLD,15));
        chart.getLegend().setItemFont(new Font("����", Font.BOLD, 15));
        chart.getTitle().setFont(new Font("����",Font.BOLD,20));//���ñ�������
        //��������������ܴ����е�࣬������Ϊһ��Ŀ�ģ����������������

        frame1=new ChartPanel(chart,true);        //����Ҳ�ܹ���chartFrame,�ܹ�ֱ������һ��������Frame
        frame1.setOpaque(true);

    }
    private static CategoryDataset getDataSet() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100, "8��", "��0֧��");
        dataset.addValue(100, "9��", "��0֧��");
        dataset.addValue(100, "10��", "��0֧��");
        dataset.addValue(200, "8��", "ũ��֧��");
        dataset.addValue(200, "9��", "ũ��֧��");
        dataset.addValue(200, "10��", "ũ��֧��");
        dataset.addValue(300, "8��", "�ǹ�֧��");
        dataset.addValue(300, "9��", "�ǹ�֧��");
        dataset.addValue(300, "10��", "�ǹ�֧��");
        dataset.addValue(400, "8��", "�ⶫ֧��");
        dataset.addValue(400, "9��", "�ⶫ֧��");
        dataset.addValue(400, "10��", "�ⶫ֧��");
        dataset.addValue(500, "8��", "����֧��");
        dataset.addValue(500, "9��", "����֧��");
        dataset.addValue(500, "10��", "����֧��");
        return dataset;
    }
//    private static CategoryDataset getDataSet() {
//        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
//        dataset.addValue(100, "��0֧��", "��0֧��");
//        dataset.addValue(200, "ũ��֧��", "ũ��֧��");
//        dataset.addValue(300, "�ǹ�֧��", "�ǹ�֧��");
//        dataset.addValue(400, "�ⶫ֧��", "�ⶫ֧��");
//        dataset.addValue(500, "����֧��", "����֧��");
//        return dataset;
//    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
