package cn.com.jsc.ui;

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

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

public class ��״ͼ {
    ChartPanel frame1;
    CategoryPlot plot;
    public ��״ͼ(){
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
            // �����ܵı�����ɫ
            chart.setBackgroundPaint(new ChartColor(25,25,112));
    // ���ñ�����ɫ
            chart.getTitle().setPaint(ChartColor.white);
    // ���ͼ�����
            CategoryPlot p = chart.getCategoryPlot();
    // ����ͼ�ı�����ɫ
            p.setBackgroundPaint(new ChartColor(25,25,112));
    // ���ñ������ɫ
            p.setRangeGridlinePaint(ChartColor.blue);
//        // �������ͼƬ �ޱ߿� �ޱ���ɫ ����ͼƬ͸��
//          chart.setBorderVisible(false);
//        chart.setBackgroundPaint(null);
//          chart.setBackgroundImageAlpha(0.0f);
//        chart.getLegend(); �ײ�
        // X ��
//        CategoryAxis domainAxis = plot.getDomainAxis();
//        domainAxis.setLabelFont(xfont);// �����
//        domainAxis.setTickLabelFont(xfont);// ����ֵ
//        domainAxis.setTickLabelPaint(Color.BLUE) ; // ������ɫ
//        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // �����ϵ�labelб��ʾ
//
//        // Y ��
//        ValueAxis rangeAxis = plot.getRangeAxis();
//        rangeAxis.setLabelFont(yfont);
//        rangeAxis.setLabelPaint(Color.BLUE) ; // ������ɫ
//        rangeAxis.setTickLabelFont(yfont);
        //�������_ʼ
        plot=chart.getCategoryPlot();//��ȡͼ���������
        plot.setOutlinePaint(Color.magenta);//ͼ��ı߿���ɫ
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setBaseOutlinePaint(Color.ORANGE);//�����ϵ���ɫ
        renderer.setDrawBarOutline(true);//���ӱ߿�
        renderer.setWallPaint(new ChartColor(25,25,112));//3D ǽ����ɫ
        //��ʾ��Ŀ��ǩ
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelPaint(Color.yellow);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //������Ŀ��ǩ��ʾ��λ��,outline��ʾ����Ŀ������,baseline_center��ʾ���ڻ����Ҿ���
        //�趨�����������ɫbai
        BarRenderer customBarRenderer = (BarRenderer) plot.getRenderer();
        customBarRenderer.setSeriesPaint(0, Color.yellow); // ��series1 Bar
        customBarRenderer.setSeriesPaint(1, Color.green); // ��series2 Bar
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        CategoryAxis domainAxis=plot.getDomainAxis();         //X ��
        domainAxis.setLabelFont(new Font("����",Font.BOLD,14));   //X �� ����
        domainAxis.setLabelPaint(Color.orange);
        domainAxis.setTickLabelFont(new Font("����",Font.BOLD,12));  //X �� ����
        domainAxis.setTickLabelPaint(Color.yellow) ; // ������ɫ X
        ValueAxis rangeAxis=plot.getRangeAxis();//Y ��
        rangeAxis.setLabelFont(new Font("����",Font.BOLD,15)); //Y �� ����
        rangeAxis.setLabelPaint(Color.orange);
        rangeAxis.setTickLabelPaint(Color.green) ; // Y �� ����

        chart.getLegend().setItemFont(new Font("����", Font.BOLD, 15)); //�ײ�
        chart.getLegend().setItemPaint(Color.green);
        chart.getLegend().setBackgroundPaint(new ChartColor(0,0,128));
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

    public static void main(String[] args) {
        JFrame jFrame=new JFrame();
        jFrame.add(new ��״ͼ().getChartPanel());
        jFrame.setSize(1000,1000);
        jFrame.setVisible(true);
    }
}
