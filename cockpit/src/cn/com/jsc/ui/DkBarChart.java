package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
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
 *  ȫ�д���Ŀ��������
 */
public class DkBarChart {
    ChartPanel frame1;
    CategoryPlot plot;
    JFreeChart chart;
    public DkBarChart(){
        CategoryDataset dataset = getDataSet();
        chart = ChartFactory.createBarChart3D(
                DateUIUtil.getDateMonth(0,"yyyy��")+"ȫ�д���Ŀ��������   ��λ����Ԫ", // ͼ�����
                "ʱ��", // �ļ��������ʾ��ǩ
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
        //��ʾ��Ŀ��ǩ
        plot=chart.getCategoryPlot();//��ȡͼ���������
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //������Ŀ��ǩ��ʾ��λ��,outline��ʾ����Ŀ������,baseline_center��ʾ���ڻ����Ҿ���
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        //�������_ʼ
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
        final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>ȫ�����Ŀ������������ʼִ��"+formatTemp.format(new Date()));
                try{
                    String dk=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        if(dk==null){
                            System.out.println(">>>>>>>>>>>ȫȫ�����Ŀ���������������"+formatTemp.format(new Date()));
                        }else{
                        //zzl ��¼�޸ĵ��м��
                        String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='ȫ�����Ŀ��������' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                        if(count==null){
                            chart.setTitle(DateUIUtil.getDateMonth(0,"yyyy��")+"ȫ�д���Ŀ��������   ��λ����Ԫ");
                            plot.setDataset(getDataSet());
                            UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='ȫ�����Ŀ��������'");
                            System.out.println(">>>>>>>>>>>ȫȫ�����Ŀ���������������"+formatTemp.format(new Date()));
                        }else{
                            System.out.println(">>>>>>>>>>>ȫ�����Ŀ���������������"+formatTemp.format(new Date()));
                            return;
                        }
                    }
                }catch (Exception p){
                    p.printStackTrace();
                }
            }
        },new Date(),2* 3600*1000);//
    }
        private static CategoryDataset getDataSet() {
            CockpitServiceIfc service = null;
            try {
                service = (CockpitServiceIfc) UIUtil
                        .lookUpRemoteService(CockpitServiceIfc.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String [][] data=service.getqhDkWcCount();
           DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            for(int i=0;i<data.length;i++){
                dataset.addValue(Double.parseDouble(data[i][3]),data[i][4],data[i][5]);
                dataset.addValue(Double.parseDouble(data[i][0]),data[i][1],data[i][2]);
            }
//        dataset.addValue(100*a, "��0֧��", "��0֧��");
//        dataset.addValue(200*a, "ũ��֧��", "ũ��֧��");
//        dataset.addValue(300*a, "�ǹ�֧��", "�ǹ�֧��");
//        dataset.addValue(400*a, "�ⶫ֧��", "�ⶫ֧��");
//        dataset.addValue(500*a, "����֧��", "����֧��");
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
