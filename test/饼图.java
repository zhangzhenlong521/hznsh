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

public class ��ͼ {
    ChartPanel frame1;
    public ��ͼ(){
        DefaultPieDataset data = getDataSet();
        JFreeChart chart = ChartFactory.createPieChart3D("ȫ�д������Ŀ��������",data,true,false,false);
//        // �������ͼƬ �ޱ߿� �ޱ���ɫ ����ͼƬ͸��
//        chart.setBorderVisible(false);
//        chart.setBackgroundPaint(null);
//        chart.setBackgroundImageAlpha(0.0f);
        chart.setBackgroundPaint(new ChartColor(25,25,112));
        // ���ñ�����ɫ
        chart.getTitle().setPaint(ChartColor.yellow);

        //���ðٷֱ�
        PiePlot pieplot = (PiePlot) chart.getPlot();
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
        chart.getLegend().setItemFont(new Font("����",Font.BOLD,10));
        chart.getLegend().setItemPaint(Color.green);
        chart.getLegend().setBackgroundPaint(new ChartColor(0,0,128));
        piePlot.setSectionPaint(0,Color.yellow);// �������ɫ
        piePlot.setSectionPaint(1,Color.green);// �������ɫ
        frame1=new ChartPanel (chart,true);
    }
    private static DefaultPieDataset getDataSet() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("��0֧��",100);
        dataset.setValue("ũ��֧��",200);
//        dataset.setValue("�ǹ�֧��",300);
//        dataset.setValue("�ⶫ֧��",400);
//        dataset.setValue("����֧��",500);
        return dataset;
    }
    public ChartPanel getChartPanel(){
        return frame1;

    }

    public static void main(String[] args) {
        JFrame jFrame=new JFrame();
        jFrame.add(new ��ͼ().getChartPanel());
        jFrame.setSize(800,800);
        jFrame.setVisible(true);
    }
}
