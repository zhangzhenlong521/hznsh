package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/***
 * zzl 客户经理营销存款排名
 * 柱状图
 */
public class CkBarChart {
    private  JPanel panel = new JPanel();
    JLabel label_1=null;
    JLabel label_2=null;
    JLabel label_3=null;
    JLabel label_4=null;
    JLabel label_5=null;
    JLabel label_6=null;
    JLabel label_7=null;
    JLabel label_8=null;
    JLabel label_9=null;
    JLabel label_10=null;
    JLabel label_11=null;
    JLabel label_12=null;
    JLabel label_13=null;
    private int a=0;
    public JPanel getJLabel() {
        try {
//            CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
//                    .lookUpRemoteService(CockpitServiceIfc.class);
//            String [][] gxck=service.getCkStatistical();
            panel.setLayout(null); //
            panel.setPreferredSize(new Dimension(1000, 1000)); //
            label_1 = new JLabel("<html><h3><font color='Black' size=6>"+DateUIUtil.getDateMonth(0,"yyyy年MM月")+"客户经理营销存款排名</font></h3></html>");//
            label_1.setBounds(50, 0, 400, 50);
            panel.add(label_1);
            String [] col={"TOP","客户经理","较年初新增","支行名称"};
            int a=0;
            for(int i=1;i<4;i++){
                ImageIcon imgIcon= UIUtil.getImageFromServer("/applet/sw.gif");//
                label_2 = new JLabel("<html><font color='#191970' size=5>TOP"+i+"</font></html>");//
                label_2.setBounds(0, a+50, 100, 50);
                label_3= new JLabel("<html><font color='#191970' size=5>张珍龙</font></html>");
                label_3.setBounds(label_2.getWidth(), a+50, 100, 50);
                label_4= new JLabel("<html><font color='#191970' size=5>较年初新增(1000万元)</font></html>");
                label_4.setBounds(label_2.getWidth()+label_3.getWidth(), a+50, 200, 50);
                label_5= new JLabel("<html><font color='#191970' size=5>神龙支行</font></html>");
                label_5.setBounds(label_2.getWidth()+label_3.getWidth()+label_4.getWidth(), a+50, 100, 50);
                label_6= new JLabel(imgIcon);
                label_6.setBounds(label_2.getWidth()+label_3.getWidth()+label_4.getWidth()+label_5.getWidth(), a+50, 100, 50);
                panel.add(label_2);
                panel.add(label_3);
                panel.add(label_4);
                panel.add(label_5);
                panel.add(label_6);
                a=label_2.getY();
            }
            for(int i=1;i<4;i++){
                ImageIcon imgIcon= UIUtil.getImageFromServer("/applet/kq.png");//
                label_2 = new JLabel("<html><font color='#FF0000' size=4>TOP"+(100+i)+"</font></html>");//
                label_2.setBounds(0, a+50, 100, 50);
                label_3= new JLabel("<html><font color='#FF0000' size=4>张允生</font></html>");
                label_3.setBounds(label_2.getWidth(), a+50, 100, 50);
                label_4= new JLabel("<html><font color='#FF0000' size=4>较年初新增(1000万元)</font></html>");
                label_4.setBounds(label_2.getWidth()+label_3.getWidth(), a+50, 200, 50);
                label_5= new JLabel("<html><font color='#FF0000' size=4>天津支行</font></html>");
                label_5.setBounds(label_2.getWidth()+label_3.getWidth()+label_4.getWidth(), a+50, 100, 50);
                label_6= new JLabel(imgIcon);
                label_6.setBounds(label_2.getWidth()+label_3.getWidth()+label_4.getWidth()+label_5.getWidth(), a+50, 100, 50);
                panel.add(label_2);
                panel.add(label_3);
                panel.add(label_4);
                panel.add(label_5);
                panel.add(label_6);
                a=label_2.getY();
            }
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
//            timer.scheduleAtFixedRate(new TimerTask() {
//                @Override
//                public void run() {
//                    System.out.println(">>>>>>>>>>>任务开始执行"+a++);
//                    label_2.setText("<html><font color='blue' size=5>各项存款总额："+a+"</font></html>");
//                    label_2.updateUI();
//                    panel.updateUI();
//                }
//            },date1,5000);//24* 3600*1000
        } catch (Exception e) {
            e.printStackTrace();
        }
        return panel;

    }
}
