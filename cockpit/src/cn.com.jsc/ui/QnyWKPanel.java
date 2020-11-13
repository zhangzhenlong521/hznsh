package cn.com.jsc.ui;

import javax.swing.*;
import java.awt.*;

/**
 * zzl
 * Ç­Å©ÔÆ
 */
public class QnyWKPanel {
    public JPanel getJLabel() {
        JLabel label_1 = new JLabel("<html><font color='blue' size=18>Ç­Å©ÔÆ</font></html>");//
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
