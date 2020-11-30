package cn.com.jsc.ui;

import javax.swing.*;


import cn.com.infostrategy.ui.common.UIUtil;

import java.awt.*;

/**
 * zzl
 * 黔农云
 */
public class QnyWKPanel {
	JPanel panel=new  JPanel();
	
    public JPanel getJLabel() {
//        JLabel label_1 = new JLabel("<html><font color='blue' size=18>黔农云</font></html>");//
//        JLabel label_2 = new JLabel("ItemName");//
//        label_1.setBounds(50, 20, 200, 50);
//        label_2.setBounds(5, 30, 80, 20);
//        JPanel panel = new JPanel(); //
//        panel.setLayout(null); //
//        panel.setPreferredSize(new Dimension(300, 55)); //
//        panel.add(label_1); //
//        panel.add(label_2); //
//        return panel;
    	// 首先在这里先获取到我们要展示的数据
    	 try {
			CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
			         .lookUpRemoteService(CockpitServiceIfc.class);
			// 计算本年新增黔农云户数
		    int qnyhs=service.getCurYearQnyhs();
		    // 计算黔农云活跃力 （不清楚是不是本年）
		    String qnyhyl= (String) service.getCurrYearQnyhyl();
		    // 计算黔农E贷签约
		    int qned=  service.getCurrYearQned();
		    //  计算本年黔农e贷线上占比
		    String qnydxszb= service.getCurrYearQnedXszb();
		    // 行了，不废话了，既然不支持html5;那就只能GUI了
//		    ImageIcon image =new  ImageIcon("/applet/circle.gif");
//		    JLabel label_1=new JLabel(image); // 设置数据
		    
//		    panel.add(label_1);
		    MyPanel backPanel=new MyPanel();
		    backPanel.repaint();
		    JLabel label_1_1=new JLabel("<html><font size='5' face='Adobe 宋体 Std L'>本月新增黔农云户数</font></html>");
		    panel.add(backPanel);
    	 }
         catch (Exception e) {
			e.printStackTrace();
		}
    	return panel;
    }
}

class MyPanel extends JPanel { // 设置内容
	@Override
	public void paint(Graphics g) {
		 Graphics2D ag=  (Graphics2D)g;
		 Color backColor=new Color(196, 214, 0);
		 g.setColor(backColor);// 设置背景色
		 // 设置长宽
		 setSize(150, 150);
		 int height= getHeight();
		 int width= getWidth();
		 int  x = 150; int y = 150;
		 
		 ag.drawString("本月新增黔农云户数", 0,180); //
		 
		 Font font=new Font("宋体", Font.PLAIN, 20);
		 // 设置一下字体颜色
		 ag.setFont(font); // 设置数据
		 ag.fillOval(0, 0, height, width);// 绘制圆形; 
		 
	}
	
}