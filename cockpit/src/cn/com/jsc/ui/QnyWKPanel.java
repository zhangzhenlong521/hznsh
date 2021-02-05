package cn.com.jsc.ui;

import javax.swing.*;


import cn.com.infostrategy.ui.common.UIUtil;
import com.pushworld.ipushgrc.ui.StringFormat;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 黔农云
 */
public class QnyWKPanel {
	JPanel panel=new  JPanel();

    public JPanel getJLabel() {
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
		    MyPanel backPanel=new MyPanel(Color.CYAN,Color.black,"本年新增-黔农云户数-"+qnyhs+"户");
		    backPanel.setBounds(0,0,150,150);
		    MyPanel backPanel2=new MyPanel(Color.green,Color.blue,"本年黔农云-活跃率-"+qnyhyl);
		    backPanel2.setBounds(160,0,150,150);
		    MyPanel backPanel3=new MyPanel(Color.yellow,Color.red,"本年黔农E贷-签约-"+qned+"户");
		    backPanel3.setBounds(0,160,150,150);
		    MyPanel backPanel4=new MyPanel(Color.blue,Color.black,"本年黔农E贷-线上占比-"+qnydxszb);
		    backPanel4.setBounds(160,160,150,150);
            panel.setLayout(null); //
            panel.setPreferredSize(new Dimension(500, 500)); //
     		panel.add(backPanel);
     		panel.add(backPanel2);
     		panel.add(backPanel3);
			panel.add(backPanel4);
    	 }
         catch (Exception e) {
			e.printStackTrace();
		}
		final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		java.util.Timer timer =new java.util.Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			int a=0;
			@Override
			public void run() {
				a++;
				CockpitServiceIfc service = null;
				try {
					System.out.println(">>>>>>>>>>>黔农云任务开始"+formatTemp.format(new Date()));
					String dk=UIUtil.getStringValueByDS(null,"select A from hzdb.S_LOAN_QNYYX_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" where rownum=1");
					if(dk==null){
						System.out.println(">>>>>>>>>>>客户经理营销贷款排名退出任务"+formatTemp.format(new Date()));
						return;
					}else{
						//zzl 记录修改的中间表
						String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='黔农云' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
						if(count==null){
							service = (CockpitServiceIfc) UIUtil
									.lookUpRemoteService(CockpitServiceIfc.class);
							// 计算本年新增黔农云户数
							int qnyhs=service.getCurYearQnyhs();
							// 计算黔农云活跃力 （不清楚是不是本年）
							String qnyhyl= (String) service.getCurrYearQnyhyl();
							// 计算黔农E贷签约
							int qned=  service.getCurrYearQned();
							//  计算本年黔农e贷线上占比
							String qnydxszb= service.getCurrYearQnedXszb();
							MyPanel backPanel=new MyPanel(Color.CYAN,Color.black,"本年新增-黔农云户数-"+qnyhs+"户");
							backPanel.setBounds(0,0,150,150);
							MyPanel backPanel2=new MyPanel(Color.green,Color.blue,"本年黔农云-活跃率-"+qnyhyl);
							backPanel2.setBounds(160,0,150,150);
							MyPanel backPanel3=new MyPanel(Color.yellow,Color.red,"本年黔农E贷-签约-"+qned+"户");
							backPanel3.setBounds(0,160,150,150);
							MyPanel backPanel4=new MyPanel(Color.blue,Color.white,"本年黔农E贷-线上占比-"+qnydxszb);
							backPanel4.setBounds(160,160,150,150);
							panel.removeAll();
							panel.add(backPanel);
							panel.add(backPanel2);
							panel.add(backPanel3);
							panel.add(backPanel4);
							panel.repaint();
							panel.updateUI();
							UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='黔农云'");
							System.out.println(">>>>>>>>>>>客户经理营销贷款排名退出任务"+formatTemp.format(new Date()));
						}else{
							System.out.println(">>>>>>>>>>>客户经理营销贷款排名退出任务"+formatTemp.format(new Date()));
							return;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		},new Date(),3600*1000*2);//3600*1000*2
		panel.setBackground(new Color(25,25,112));
    	return panel;

    }
}

class MyPanel extends JPanel { // 设置内容
	private Shape rect;//矩形对象
	private Font font;//字体对象
	private Color Color;
	private Color Colortxt;
	private String test;
	MyPanel(Color Color,Color Colortxt,String test){
		this.Color=Color;
		this.test=test;
		this.Colortxt=Colortxt;
		this.setBackground(new Color(25,25,112));
	}
	public void paint(Graphics g) {
		rect=new RoundRectangle2D.Double(0,0,150,150,150,150);
		font=new Font("宋体",Font.BOLD,16);
		super.paint(g);
		Graphics2D g2=(Graphics2D)g;//强制类型转换
		g2.setColor(Color);//设置当前绘图颜色
		g2.fill(rect);//填充矩形
		g2.setColor(Colortxt);//设置当前绘图颜色
		g2.setFont(font);//设置字体
		String [] str=test.split("-");
//		g2.drawString(str[0], 30, 30);//绘制文本
//		g2.drawString(str[1], 30, 60);//绘制文本
//		g2.drawString(str[2], 30, 90);//绘制文本
		int a=0;
		for(int i=0;i<str.length;i++){
			g2.drawString(str[i], 30, 30+a);//绘制文本
			a=a+30;

		}
	}
}