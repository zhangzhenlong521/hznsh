package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
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

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * ��֧�д��Ŀ��������
 */
public class CKgzhMbiaoWcQkuang {
    ChartPanel frame1;
    CategoryPlot plot;
    JFreeChart chart;
    public CKgzhMbiaoWcQkuang(){
        CategoryDataset dataset = getDataSet();
        chart = ChartFactory.createBarChart3D(
                DateUIUtil.getDateMonth(1,"yyyy��MM��dd��")+"��֧�д��Ŀ��������(ǰ����&������)", // ͼ�����
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
        chart.getTitle().setPaint(ChartColor.green);
        // ���ͼ�����
        CategoryPlot p = chart.getCategoryPlot();
        // ����ͼ�ı�����ɫ
        p.setBackgroundPaint(new ChartColor(25,25,112));
        // ���ñ������ɫ
        p.setRangeGridlinePaint(ChartColor.blue);
//        // �������ͼƬ �ޱ߿� �ޱ���ɫ ����ͼƬ͸��
//        chart.setBorderVisible(false);
//        chart.setBackgroundPaint(null);
//        chart.setBackgroundImageAlpha(0.0f);
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
        BarRenderer3D renderer = (BarRenderer3D) plot.getRenderer();
        renderer.setBaseOutlinePaint(Color.ORANGE);//�����ϵ���ɫ
        renderer.setDrawBarOutline(true);//���ӱ߿�
        renderer.setWallPaint(new ChartColor(25,25,112));//3D ǽ����ɫ
        //��ʾ��Ŀ��ǩ
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("����",Font.BOLD,14));
        renderer.setBaseItemLabelPaint(Color.YELLOW);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //������Ŀ��ǩ��ʾ��λ��,outline��ʾ����Ŀ������,baseline_center��ʾ���ڻ����Ҿ���
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));
        //�趨�����������ɫbai
        BarRenderer3D customBarRenderer = (BarRenderer3D) plot.getRenderer();
        customBarRenderer.setSeriesPaint(0, Color.red); // ��series1 Bar
        customBarRenderer.setSeriesPaint(1, Color.orange); // ��series2 Bar
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

        frame1=new ChartPanel(chart,true);        //����Ҳ�ܹ���chartFrame,�ܹ�ֱ������һ��������Frame
        frame1.setOpaque(true);
        final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int a=0;
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>��֧�д��Ŀ������������ʼִ��"+formatTemp.format(new Date()));
                try{
                    String grhqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_psn_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String grdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String dghgs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_fx_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    String dgdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                    if(grhqs==null || grdqs==null || dghgs==null || dgdqs==null){
                        System.out.println(">>>>>>>>>>>��֧�д��Ŀ���������˳�����");
                        return;
                    }else{
                        //zzl ��¼�޸ĵ��м��
                        String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='��֧�д��Ŀ��������' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                        if(count==null){
                            chart.setTitle(DateUIUtil.getDateMonth(1,"yyyy��MM��dd��")+"��֧�д��Ŀ��������(ǰ����&������)");
                            plot.setDataset(getDataSet());
                            UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='��֧�д��Ŀ��������'");
                            System.out.println(">>>>>>>>>>>��֧�д��Ŀ���������˳�����"+formatTemp.format(new Date()));
                        }else{
                            System.out.println(">>>>>>>>>>>��֧�д��Ŀ���������˳�����"+formatTemp.format(new Date()));
                            return;
                        }

                    }
                }catch (Exception p){
                    p.printStackTrace();
                }
            }
        },new Date(),3600*1000*2);//

    }
    private static CategoryDataset getDataSet() {
        CockpitServiceIfc service = null;
        try {
            service = (CockpitServiceIfc) UIUtil
                    .lookUpRemoteService(CockpitServiceIfc.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String [][] data=service.getgzhWcqingkuang();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        //zzl ǰ����
        for(int i=0;i<3;i++){
            dataset.addValue(Double.parseDouble(data[i][0]),data[i][1],data[i][2]);
            dataset.addValue(Double.parseDouble(data[i][3]),data[i][4],data[i][5]);
        }
        //zzl ������
        for(int i=0;i<3;i++){
            int row=data.length-3+i;
            dataset.addValue(Double.parseDouble(data[row][0]),data[row][1],data[row][2]);
            dataset.addValue(Double.parseDouble(data[row][3]),data[row][4],data[row][5]);
        }
//        dataset.addValue(100, "8��", "��0֧��");
//        dataset.addValue(100, "9��", "��0֧��");
//        dataset.addValue(100, "10��", "��0֧��");
//        dataset.addValue(200, "8��", "ũ��֧��");
//        dataset.addValue(200, "9��", "ũ��֧��");
//        dataset.addValue(200, "10��", "ũ��֧��");
//        dataset.addValue(300, "8��", "�ǹ�֧��");
//        dataset.addValue(300, "9��", "�ǹ�֧��");
//        dataset.addValue(300, "10��", "�ǹ�֧��");
//        dataset.addValue(400, "8��", "�ⶫ֧��");
//        dataset.addValue(400, "9��", "�ⶫ֧��");
//        dataset.addValue(400, "10��", "�ⶫ֧��");
//        dataset.addValue(500, "8��", "����֧��");
//        dataset.addValue(500, "9��", "����֧��");
//        dataset.addValue(500, "10��", "����֧��");
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
