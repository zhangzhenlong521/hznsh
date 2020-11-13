package cn.com.jsc.ui;

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
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.TextAnchor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl ����
 *  ȫ�����Ŀ��������
 */
public class DkBarChart {
    ChartPanel frame1;
    CategoryPlot plot;
    public DkBarChart(){
        CategoryDataset dataset = getDataSet(1);
        JFreeChart chart = ChartFactory.createBarChart3D(
                "ȫ�����Ŀ��������", // ͼ�����
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
        plot=chart.getCategoryPlot();//��ȡͼ���������
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        //��ʾ��Ŀ��ǩ
        renderer.setBaseItemLabelsVisible(true);
        renderer
                .setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //������Ŀ��ǩ��ʾ��λ��,outline��ʾ����Ŀ������,baseline_center��ʾ���ڻ����Ҿ���
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        CategoryAxis domainAxis=plot.getDomainAxis();         //ˮƽ�ײ��б�
        domainAxis.setLabelFont(new Font("����",Font.BOLD,14));         //ˮƽ�ײ�����
        domainAxis.setTickLabelFont(new Font("����",Font.BOLD,12));  //��ֱ����
        ValueAxis rangeAxis=plot.getRangeAxis();//��ȡ��״
        rangeAxis.setLabelFont(new Font("����",Font.BOLD,15));
        chart.getLegend().setItemFont(new Font("����", Font.BOLD, 15));
        chart.getTitle().setFont(new Font("����",Font.BOLD,20));//���ñ�������
        //��������������ܴ����е�࣬������Ϊһ��Ŀ�ģ����������������
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
                System.out.println(">>>>>>>>>>>����ʼִ��"+a++);
                plot.setDataset(getDataSet(a++));
            }
        },date1,24* 3600*1000);//
        frame1=new ChartPanel(chart,true);        //����Ҳ�ܹ���chartFrame,�ܹ�ֱ������һ��������Frame
        frame1.setOpaque(true);

    }
        private static CategoryDataset getDataSet(int a) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(100*a, "��0֧��", "��0֧��");
        dataset.addValue(200*a, "ũ��֧��", "ũ��֧��");
        dataset.addValue(300*a, "�ǹ�֧��", "�ǹ�֧��");
        dataset.addValue(400*a, "�ⶫ֧��", "�ⶫ֧��");
        dataset.addValue(500*a, "����֧��", "����֧��");
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
