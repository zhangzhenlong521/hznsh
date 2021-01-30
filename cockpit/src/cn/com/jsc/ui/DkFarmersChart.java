package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
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
 * zzl
 * ȫ��ũ���������
 */
public class DkFarmersChart {
    ChartPanel frame1;
    PiePlot pieplot;
    JFreeChart chart;
    public DkFarmersChart(){
        DefaultPieDataset data = getDataSet();
        chart = ChartFactory.createPieChart3D(DateUIUtil.getDateMonth(1,"yyyy��MM��dd��")+"ȫ��ũ���������",data,true,false,false);
        chart.setBackgroundPaint(new ChartColor(25,25,112));
        // ���ñ�����ɫ
        chart.getTitle().setPaint(ChartColor.yellow);

        //���ðٷֱ�
        pieplot = (PiePlot) chart.getPlot();
        pieplot.setBackgroundPaint(new ChartColor(25,25,112));
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
        final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>ȫ��ũ�������������ʼִ��"+formatTemp.format(new Date()));
                try{
                    String dk=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    if(dk==null){
                        System.out.println(">>>>>>>>>>>ȫ��ũ����������������"+formatTemp.format(new Date()));
                    }else{
                        //zzl ��¼�޸ĵ��м��
                        String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='ȫ��ũ���������' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                        if(count==null){
                            chart.setTitle(DateUIUtil.getDateMonth(a++,"yyyy��MM��dd��")+"ȫ��ũ���������");
                            pieplot.setDataset(getDataSet());
                            UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='ȫ��ũ���������'");
                            System.out.println(">>>>>>>>>>>ȫ��ũ����������������"+formatTemp.format(new Date()));
                        }else{
                            System.out.println(">>>>>>>>>>>ȫ��ũ����������������"+formatTemp.format(new Date()));
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
        dataset.setValue(data[0][1]+" "+data[0][2]+"��",Integer.parseInt(data[0][2]));
        dataset.setValue(data[0][3]+""+data[0][4]+"��",Integer.parseInt(data[0][4]));
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }
}
