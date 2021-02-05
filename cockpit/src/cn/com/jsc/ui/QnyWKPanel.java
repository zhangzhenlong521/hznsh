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
 * ǭũ��
 */
public class QnyWKPanel {
	JPanel panel=new  JPanel();

    public JPanel getJLabel() {
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
		    MyPanel backPanel=new MyPanel(Color.CYAN,Color.black,"��������-ǭũ�ƻ���-"+qnyhs+"��");
		    backPanel.setBounds(0,0,150,150);
		    MyPanel backPanel2=new MyPanel(Color.green,Color.blue,"����ǭũ��-��Ծ��-"+qnyhyl);
		    backPanel2.setBounds(160,0,150,150);
		    MyPanel backPanel3=new MyPanel(Color.yellow,Color.red,"����ǭũE��-ǩԼ-"+qned+"��");
		    backPanel3.setBounds(0,160,150,150);
		    MyPanel backPanel4=new MyPanel(Color.blue,Color.black,"����ǭũE��-����ռ��-"+qnydxszb);
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
					System.out.println(">>>>>>>>>>>ǭũ������ʼ"+formatTemp.format(new Date()));
					String dk=UIUtil.getStringValueByDS(null,"select A from hzdb.S_LOAN_QNYYX_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" where rownum=1");
					if(dk==null){
						System.out.println(">>>>>>>>>>>�ͻ�����Ӫ�����������˳�����"+formatTemp.format(new Date()));
						return;
					}else{
						//zzl ��¼�޸ĵ��м��
						String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='ǭũ��' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
						if(count==null){
							service = (CockpitServiceIfc) UIUtil
									.lookUpRemoteService(CockpitServiceIfc.class);
							// ���㱾������ǭũ�ƻ���
							int qnyhs=service.getCurYearQnyhs();
							// ����ǭũ�ƻ�Ծ�� ��������ǲ��Ǳ��꣩
							String qnyhyl= (String) service.getCurrYearQnyhyl();
							// ����ǭũE��ǩԼ
							int qned=  service.getCurrYearQned();
							//  ���㱾��ǭũe������ռ��
							String qnydxszb= service.getCurrYearQnedXszb();
							MyPanel backPanel=new MyPanel(Color.CYAN,Color.black,"��������-ǭũ�ƻ���-"+qnyhs+"��");
							backPanel.setBounds(0,0,150,150);
							MyPanel backPanel2=new MyPanel(Color.green,Color.blue,"����ǭũ��-��Ծ��-"+qnyhyl);
							backPanel2.setBounds(160,0,150,150);
							MyPanel backPanel3=new MyPanel(Color.yellow,Color.red,"����ǭũE��-ǩԼ-"+qned+"��");
							backPanel3.setBounds(0,160,150,150);
							MyPanel backPanel4=new MyPanel(Color.blue,Color.white,"����ǭũE��-����ռ��-"+qnydxszb);
							backPanel4.setBounds(160,160,150,150);
							panel.removeAll();
							panel.add(backPanel);
							panel.add(backPanel2);
							panel.add(backPanel3);
							panel.add(backPanel4);
							panel.repaint();
							panel.updateUI();
							UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='ǭũ��'");
							System.out.println(">>>>>>>>>>>�ͻ�����Ӫ�����������˳�����"+formatTemp.format(new Date()));
						}else{
							System.out.println(">>>>>>>>>>>�ͻ�����Ӫ�����������˳�����"+formatTemp.format(new Date()));
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

class MyPanel extends JPanel { // ��������
	private Shape rect;//���ζ���
	private Font font;//�������
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
		font=new Font("����",Font.BOLD,16);
		super.paint(g);
		Graphics2D g2=(Graphics2D)g;//ǿ������ת��
		g2.setColor(Color);//���õ�ǰ��ͼ��ɫ
		g2.fill(rect);//������
		g2.setColor(Colortxt);//���õ�ǰ��ͼ��ɫ
		g2.setFont(font);//��������
		String [] str=test.split("-");
//		g2.drawString(str[0], 30, 30);//�����ı�
//		g2.drawString(str[1], 30, 60);//�����ı�
//		g2.drawString(str[2], 30, 90);//�����ı�
		int a=0;
		for(int i=0;i<str.length;i++){
			g2.drawString(str[i], 30, 30+a);//�����ı�
			a=a+30;

		}
	}
}