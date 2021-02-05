package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * ȫ�и��������ͳ�����
 */
public class BadNewJLabel {
    private  JPanel panel = new JPanel();
    JLabel label_1=null;
    JLabel label_2=null;
    JLabel label_3=null;
    JLabel label_4=null;
    JLabel label_5=null;
    JLabel label_6=null;
    JLabel label_8=null;
    JLabel label_10=null;
    JLabel label_11=null;
    public JPanel getJLabel() {
        try {
            panel.setLayout(null); //
            panel.setPreferredSize(new Dimension(800, 800)); //
            CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
                    .lookUpRemoteService(CockpitServiceIfc.class);
            String [][] gxck=service.getBlDKCount();
            String [][] gxck2=service.getBlDKCount2();
//            String [][] ckhs=service.getBlDKLvCount();
            String [][] grhq=service.getShBlDKLvCount();
            String [][] grdq=service.getxzBlDKLvCount();
            String [][] dghq=service.getShBwBlDKLvCount();
//            String [][] xzbw=service.getxzBwBlDKLvCount();
            label_1 = new JLabel("<html><h2><font color='#FFFFFF' size=6>"+DateUIUtil.getDateMonth(1,"yyyy��MM��dd��")+"ȫ�и��������ͳ�����</font></h2></html>");//
            label_2 = new JLabel("<html><font color='FFFF00' size=5>60�����ϲ��������ܶ"+gxck[0][0]+"��Ԫ</font></html>");//
            label_3= new JLabel("<html><font color='FFFF00' size=5>(�����������"+gxck[0][1]+"��Ԫ)</font></html>");
            label_4 = new JLabel("<html><font color='FFFF00' size=5>90�����ϲ��������ܶ"+gxck2[0][0]+"��Ԫ</font></html>");//
            label_5= new JLabel("<html><font color='FFFF00' size=5>(�����������"+gxck2[0][1]+"��Ԫ)</font></html>");
//            label_4 = new JLabel("<html><font color='#DC143C' size=5>���������ʣ�"+ckhs[0][0]+"%</font></html>");//
//            label_5= new JLabel("<html><font color='#DC143C' size=5>(�����������"+ckhs[0][1]+"%)</font></html>");
            label_6= new JLabel("<html><font color='FFFF00 size=5>�����ջ�60���ϲ������"+grhq[0][0]+"��Ԫ</font></html>");//
            label_8= new JLabel("<html><font color='FFFF00' size=5>�����ջ�90���ϲ������"+grdq[0][0]+"��Ԫ</font></html>");//
            label_10= new JLabel("<html><font color='FFFF00' size=5>�����ջر��ⲻ�����"+dghq[0][0]+"��Ԫ</font></html>");//
//            label_11= new JLabel("<html><font color='#DC143C' size=5>�����������ⲻ�����"+(xzbw[0][0]==null || xzbw[0][0].equals("")?"0.0":xzbw[0][0])+"��Ԫ</font></html>");//
            label_1.setBounds(50, 0, 500, 50);
            label_2.setBounds(0, 50, 300, 50);
            label_3.setBounds(label_2.getWidth(), 50, 200, 50);
            label_4.setBounds(0,100, 300, 50);
            label_5.setBounds(label_2.getWidth(), 100, 200, 50);
            label_6.setBounds(0,150, 300, 50);
            label_8.setBounds(0,200, 300, 50);
            label_10.setBounds(0,250, 300, 50);
//            label_11.setBounds(0,300, 300, 50);
            panel.add(label_1); //
            panel.add(label_2); //
            panel.add(label_3);
            panel.add(label_4);
            panel.add(label_5);
            panel.add(label_6);
            panel.add(label_8);
            panel.add(label_10);
//            panel.add(label_11);
            final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            java.util.Timer timer =new java.util.Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try{
                        System.out.println(">>>>>>>>>>>ȫ�и��������ͳ���������ʼ"+formatTemp.format(new Date()));
                        CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
                                .lookUpRemoteService(CockpitServiceIfc.class);
                        String dk=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                       if(dk==null){
                            System.out.println(">>>>>>>>>>>ȫ�и��������ͳ������˳�����"+formatTemp.format(new Date()));
                            return;
                        }else{
                            //zzl ��¼�޸ĵ��м��
                            String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='ȫ�и��������ͳ�����' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                            if(count==null){
                                String [][] gxck=service.getBlDKCount();
                                String [][] gxck2=service.getBlDKCount2();
//                                String [][] ckhs=service.getBlDKLvCount();
                                String [][] grhq=service.getShBlDKLvCount();
                                String [][] grdq=service.getxzBlDKLvCount();
                                String [][] dghq=service.getShBwBlDKLvCount();
//                                String [][] xzbw=service.getxzBwBlDKLvCount();
                                label_1 .setText("<html><h2><font color='#FFFFFF' size=6>"+DateUIUtil.getDateMonth(1,"yyyy��MM��dd��")+"ȫ�и��������ͳ�����</font></h2></html>");//
                                label_2 .setText("<html><font color='FFFF00' size=5>60�����ϲ��������ܶ"+gxck[0][0]+"��Ԫ</font></html>");//
                                label_3.setText("<html><font color='FFFF00' size=5>(�����������"+gxck[0][1]+"��Ԫ)</font></html>");
//                                label_4 .setText("<html><font color='#DC143C' size=5>���������ʣ�"+ckhs[0][0]+"%</font></html>");//
//                                label_5.setText("<html><font color='#DC143C' size=5>(�����������"+ckhs[0][1]+"%)</font></html>");
                                label_4 .setText("<html><font color='FFFF00' size=5>90�����ϲ��������ܶ"+gxck2[0][0]+"��Ԫ</font></html>");//
                                label_5.setText("<html><font color='FFFF00' size=5>(�����������"+gxck2[0][1]+"��Ԫ)</font></html>");
                                label_6.setText("<html><font color='FFFF00' size=5>�����ջ�60���ϲ������"+grhq[0][0]+"��Ԫ</font></html>");//
                                label_8.setText("<html><font color='FFFF00' size=5>�����ջ�90���ϲ������"+grdq[0][0]+"��Ԫ</font></html>");//
                                label_10.setText("<html><font color='FFFF00' size=5>�����ջر��ⲻ�����"+dghq[0][0]+"��Ԫ</font></html>");//
//                                label_11.setText("<html><font color='#DC143C' size=5>�����������ⲻ�����"+(xzbw[0][0]==null || xzbw[0][0].equals("")?"0.0":xzbw[0][0])+"��Ԫ</font></html>");//
                                UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='ȫ�и��������ͳ�����'");
                                panel.updateUI();
                                System.out.println(">>>>>>>>>>>ȫ�и��������ͳ������˳�����"+formatTemp.format(new Date()));
                            }else{
                                System.out.println(">>>>>>>>>>>ȫ�и��������ͳ������˳�����"+formatTemp.format(new Date()));
                                return;

                            }
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            },new Date(),3600*1000*2);//24* 3600*1000
        } catch (Exception e) {
            e.printStackTrace();
        }
        panel.setBackground(new Color(25,25,112));
        return panel;

    }
}
