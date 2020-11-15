package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 存款文字描述
 */
public class CkNewJLabel{
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
            CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
                    .lookUpRemoteService(CockpitServiceIfc.class);
            String [][] gxck=service.getCkStatistical();
            String [][] ckhs=service.getCKHsCount();
            String [][] grhq=service.getCKGeRenCount();
            String [][] grdq=service.getCKGeRenDQCount();
            String [][] dghq=service.getCKDgHqCount();
            String [][] dgdq=service.getCKDgDqCount();
            label_1 = new JLabel("<html><h3><font color='Black' size=6>"+DateUIUtil.getDateMonth(0,"yyyy年MM月")+"全行各项存款统计情况</font></h3></html>");//
            label_2 = new JLabel("<html><font color='#2F4F4F' size=5>各项存款总额："+gxck[0][0]+"亿元</font></html>");//
            label_3= new JLabel("<html><font color='#2F4F4F' size=5>(较年初新增："+gxck[0][1]+"亿元)</font></html>");
            label_4 = new JLabel("<html><font color='#4682B4' size=5>有效存款户数："+ckhs[0][0]+"万户</font></html>");//
            label_5= new JLabel("<html><font color='#4682B4' size=5>(较年初新增："+ckhs[0][1]+"户)</font></html>");
            label_6= new JLabel("<html><font color='#0000FF' size=5>个人活期存款："+grhq[0][0]+"亿元</font></html>");//
            label_7= new JLabel("<html><font color='#0000FF' size=5>(较年初新增："+grhq[0][1]+"亿元)</font></html>");
            label_8= new JLabel("<html><font color='#800080' size=5>个人定期存款："+grdq[0][0]+"亿元</font></html>");//
            label_9= new JLabel("<html><font color='#800080' size=5>(较年初新增："+grdq[0][1]+"亿元)</font></html>");
            label_10= new JLabel("<html><font color='#DC143C' size=5>对公活期存款："+dghq[0][0]+"亿元</font></html>");//
            label_11= new JLabel("<html><font color='#DC143C' size=5>(较年初新增："+dghq[0][1]+"亿元)</font></html>");
            label_12= new JLabel("<html><font color='Lime' size=5>对公定期存款："+dgdq[0][0]+"亿元</font></html>");//
            label_13= new JLabel("<html><font color='Lime' size=5>(较年初新增："+dgdq[0][1]+"亿元)</font></html>");
            label_1.setBounds(50, 0, 400, 50);
            label_2.setBounds(0, 50, 300, 50);
            label_3.setBounds(label_2.getWidth(), 50, 200, 50);
            label_4.setBounds(0,80, 300, 50);
            label_5.setBounds(label_2.getWidth(), 80, 200, 50);
            label_6.setBounds(0,110, 300, 50);
            label_7.setBounds(label_2.getWidth(), 110, 200, 50);
            label_8.setBounds(0,140, 300, 50);
            label_9.setBounds(label_2.getWidth(), 140, 200, 50);
            label_10.setBounds(0,170, 300, 50);
            label_11.setBounds(label_2.getWidth(), 170, 200, 50);
            label_12.setBounds(0,200, 300, 50);
            label_13.setBounds(label_2.getWidth(), 200, 200, 50);
            panel.setLayout(null); //
            panel.setPreferredSize(new Dimension(800, 800)); //
            panel.add(label_1); //
            panel.add(label_2); //
            panel.add(label_3);
            panel.add(label_4);
            panel.add(label_5);
            panel.add(label_6);
            panel.add(label_7);
            panel.add(label_8);
            panel.add(label_9);
            panel.add(label_10);
            panel.add(label_11);
            panel.add(label_12);
            panel.add(label_13);
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
