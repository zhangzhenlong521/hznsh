package cn.com.jsc.ui;

import javax.swing.*;
import java.awt.*;

/**
 * zzl
 * 存款文字描述
 */
public class CkNewJLabel{
    public JPanel getJLabel() {
        JLabel label_1 = new JLabel("<html><font color='blue' size=18>2020年11月全行各项存款统计</font></html>");//
        JLabel label_2 = new JLabel("ItemName");//
        label_1.setBounds(50, 20, 200, 50);
        label_2.setBounds(5, 30, 80, 20);
        JPanel panel = new JPanel(); //
        panel.setLayout(null); //
        panel.setPreferredSize(new Dimension(300, 55)); //
        panel.add(label_1); //
        panel.add(label_2); //
        return panel;
    }
}
