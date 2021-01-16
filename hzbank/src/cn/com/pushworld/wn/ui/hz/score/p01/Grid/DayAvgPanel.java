package cn.com.pushworld.wn.ui.hz.score.p01.Grid;

import cn.com.jsc.ui.DateUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * DayAvgPanel
 * zzl
 *
 * @author Dragon
 * @date 2021/1/10
 */
public class DayAvgPanel {
    private JPanel panel = new JPanel();
    private JPanel panelck = new JPanel();
    private JPanel paneldk = new JPanel();
    JLabel label_1=null;
    JLabel label_2=null;
    public JPanel getJLabel() {
        label_1 = new JLabel("<html><h2><font color='#FFFFFF' size=6>����վ����</font></h2></html>");//
        label_2 = new JLabel("<html><font color='#FFFFFF' size=6>�����վ����</font></html>");//
        label_1.setBounds(0,0,100,100);
        panelck.add(label_1);
        panelck.setBackground(Color.green);
        panelck.setBounds(0,0,500,200);
        label_2.setBounds(0,100,100,100);
        paneldk.add(label_2);
        paneldk.setBackground(Color.blue);
        paneldk.setBounds(0,200,500,200);
        MyPanel backPanel=new MyPanel(Color.green,Color.black,"����վ����");
        backPanel.setBounds(0,0,1000,150);
        panel.add(backPanel);
        panel.add(paneldk);
        panel.setLayout(null); //
        panel.setOpaque(true);
        return panel;
    }
    class MyPanel extends JPanel { // ��������
        private Shape rect;//���ζ���
        private Font font;//�������
        private Color Color;
        private Color Colortxt;
        private String test;

        MyPanel(Color Color, Color Colortxt, String test) {
            this.Color = Color;
            this.test = test;
            this.Colortxt = Colortxt;
        }

        public void paint(Graphics g) {
            rect = new RoundRectangle2D.Double(0, 0, 1000, 150, 150, 150);
            font = new Font("����", Font.BOLD, 20);
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;//ǿ������ת��
            g2.setColor(Color);//���õ�ǰ��ͼ��ɫ
            g2.fill(rect);//������
            g2.setColor(Colortxt);//���õ�ǰ��ͼ��ɫ
            g2.setFont(font);//��������
            g2.setColor(Color.WHITE);
            String[] str = test.split("-");
//		g2.drawString(str[0], 30, 30);//�����ı�
//		g2.drawString(str[1], 30, 60);//�����ı�
//		g2.drawString(str[2], 30, 90);//�����ı�
            int a = 0;
            for (int i = 0; i < str.length; i++) {
                g2.drawString(str[i], 30, 30 + a);//�����ı�
                a = a + 30;

            }
        }
    }
}
