package cn.com.jsc.ui;

import javax.swing.*;


import cn.com.infostrategy.ui.common.UIUtil;

import java.awt.*;

/**
 * zzl
 * ǭũ��
 */
public class QnyWKPanel {
	JPanel panel=new  JPanel();
	
    public JPanel getJLabel() {
//        JLabel label_1 = new JLabel("<html><font color='blue' size=18>ǭũ��</font></html>");//
//        JLabel label_2 = new JLabel("ItemName");//
//        label_1.setBounds(50, 20, 200, 50);
//        label_2.setBounds(5, 30, 80, 20);
//        JPanel panel = new JPanel(); //
//        panel.setLayout(null); //
//        panel.setPreferredSize(new Dimension(300, 55)); //
//        panel.add(label_1); //
//        panel.add(label_2); //
//        return panel;
    	// �����������Ȼ�ȡ������Ҫչʾ������
    	 try {
			CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
			         .lookUpRemoteService(CockpitServiceIfc.class);
			// ���㱾������ǭũ�ƻ���
		    int qnyhs=service.getCurYearQnyhs();
		    // ����ǭũ�ƻ�Ծ�� ��������ǲ��Ǳ��꣩
		    String qnyhyl= (String) service.getCurrYearQnyhyl();
		    // ����ǭũE��ǩԼ
		    int qned=  service.getCurrYearQned();
		    //  ���㱾��ǭũe������ռ��
		    String qnydxszb= service.getCurrYearQnedXszb();
		    // ���ˣ����ϻ��ˣ���Ȼ��֧��html5;�Ǿ�ֻ��GUI��
//		    ImageIcon image =new  ImageIcon("/applet/circle.gif");
//		    JLabel label_1=new JLabel(image); // ��������
		    
//		    panel.add(label_1);
		    MyPanel backPanel=new MyPanel();
		    backPanel.repaint();
		    JLabel label_1_1=new JLabel("<html><font size='5' face='Adobe ���� Std L'>��������ǭũ�ƻ���</font></html>");
		    panel.add(backPanel);
    	 }
         catch (Exception e) {
			e.printStackTrace();
		}
    	return panel;
    }
}

class MyPanel extends JPanel { // ��������
	@Override
	public void paint(Graphics g) {
		 Graphics2D ag=  (Graphics2D)g;
		 Color backColor=new Color(196, 214, 0);
		 g.setColor(backColor);// ���ñ���ɫ
		 // ���ó���
		 setSize(150, 150);
		 int height= getHeight();
		 int width= getWidth();
		 int  x = 150; int y = 150;
		 
		 ag.drawString("��������ǭũ�ƻ���", 0,180); //
		 
		 Font font=new Font("����", Font.PLAIN, 20);
		 // ����һ��������ɫ
		 ag.setFont(font); // ��������
		 ag.fillOval(0, 0, height, width);// ����Բ��; 
		 
	}
	
}