package cn.com.jsc.ui;

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
    JLabel label_2=null;
    private int a=0;
    public JPanel getJLabel() {
        JLabel label_1 = new JLabel("<html><font color='Black' size=6>"+DateUIUtil.getDateMonth(0,"yyyy年MM月")+"全行各项存款统计情况</font></html>");//
        label_2 = new JLabel("<html><font color='blue' size=5>各项存款总额："+a+"</font></html>");//
        label_1.setBounds(50, 0, 400, 50);
        label_2.setBounds(0, 50, 200, 50);
        panel.setLayout(null); //
        panel.setPreferredSize(new Dimension(800, 800)); //
        panel.add(label_1); //
        panel.add(label_2); //
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
            @Override
            public void run() {
                System.out.println(">>>>>>>>>>>任务开始执行"+a++);
                label_2.setText("<html><font color='blue' size=5>各项存款总额："+a+"</font></html>");
                label_2.updateUI();
                panel.updateUI();
            }
        },date1,5000);//24* 3600*1000
        return panel;

    }
}
