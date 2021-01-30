package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 加入title
 */
public class TitleNewJLabel {
    private JPanel panel=null;
    private JLabel label_1=null;
    private JLabel label_2=null;
    private SimpleDateFormat formatTemp=null;
    public JPanel getJLabel(String str) {//UIUtil.getImageFromServer("/applet/titlecenter_salary.gif")
        ImageIcon imgIcon=UIUtil.getImageFromServer("/applet/log.png");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width= new Double(screenSize.getWidth()).intValue();
        label_1 = new JLabel(imgIcon);//
        label_1.setText("<html><font color='#FFFFFF' size=14>贵州赫章农商银行绩效考核数据管理平台"+str+"</font></html>");
        label_1.setBounds(0, 0, 900, 100);
        panel = new JPanel(); //
        panel.setLayout(null); //
        panel.setPreferredSize(new Dimension(width, 100)); //
        panel.setBackground(new Color(25,25,112));
        label_2=new JLabel();
        formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        label_2.setText("<html><font color='#FFFFFF' size=1>"+formatTemp.format(new Date())+"</font></html>");
        label_2.setBounds(width-400, 25, 400, 50);
        panel.add(label_1); //
        panel.add(label_2); //
        java.util.Timer timer =new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            public void run() {
                label_2.setText("<html><font color='#FFFFFF' size=8>"+formatTemp.format(new Date())+"</font></html>");
                panel.updateUI();
            }
        },new Date(),1000);
        return panel;
    }
}
