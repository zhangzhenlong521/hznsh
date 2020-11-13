package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;

import javax.swing.*;
import java.awt.*;

/**
 * zzl
 * º”»Îtitle
 */
public class TitleNewJLabel {
    public JPanel getJLabel() {//UIUtil.getImageFromServer("/applet/titlecenter_salary.gif")
        ImageIcon imgIcon=UIUtil.getImageFromServer("/applet/titlecenter_salary.gif");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width= new Double(screenSize.getWidth()).intValue();
        JLabel label_1 = new JLabel(imgIcon);//
        label_1.setBounds(0, 0, width, 50);
        JPanel panel = new JPanel(); //
        panel.setLayout(null); //
        panel.setPreferredSize(new Dimension(width, 50)); //
        panel.add(label_1); //
        return panel;
    }
}
