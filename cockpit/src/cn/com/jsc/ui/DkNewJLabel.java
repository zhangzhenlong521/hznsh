package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 全行各项贷款统计情况
 */
public class DkNewJLabel {
    private  JPanel panel = new JPanel();
    JLabel label_1=null;
    JLabel label_2=null;
    JLabel label_3=null;
    JLabel label_4=null;
    JLabel label_5=null;
    JLabel label_6=null;
    JLabel label_7=null;
    JLabel label_8=null;
    JLabel label_9=null;
    JLabel label_10=null;
    JLabel label_11=null;
    JLabel label_12=null;
    JLabel label_13=null;
    private int a=0;
    public JPanel getJLabel() {
        try {
            CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
                    .lookUpRemoteService(CockpitServiceIfc.class);
            final String [][] gxdk=service.getDkStatistical();
            final String [][] dkhs=service.getDkStatisticalHs();
            final String [][] bmdk=service.getBmDkStatisticalHs();
            final String [][] gtdk=service.getGtDkStatisticalHs();
            final String [][] xdldk=service.getXdlDkStatisticalHs();
            final String [][] nhdk=service.getnhDkStatisticalHs();
            label_1 = new JLabel("<html><h2><font color='#FFFFFF' size=6>"+DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"全行各项贷款统计情况</font></h2></html>");//
            label_2 = new JLabel("<html><font color='FFFF00' size=5>各项贷款总额："+gxdk[0][0]+"亿元</font></html>");//
            label_3= new JLabel("<html><font color='FFFF00' size=5>(较年初新增："+gxdk[0][1]+"亿元)</font></html>");
            label_4 = new JLabel("<html><font color='#DC143C' size=5>有效贷款户数："+dkhs[0][0]+"万户</font></html>");//
            label_5= new JLabel("<html><font color='#DC143C' size=5>(较年初新增："+dkhs[0][1]+"户)</font></html>");
            label_6= new JLabel("<html><font color='FFFF00' size=5>便民快贷："+bmdk[0][0]+"户</font></html>");//
            label_7= new JLabel("<html><font color='FFFF00' size=5>(较年初新增："+bmdk[0][1]+"户)</font></html>");
            label_8= new JLabel("<html><font color='#7CFC00' size=5>个体工商户："+gtdk[0][0]+"户</font></html>");//
            label_9= new JLabel("<html><font color='#7CFC00' size=5>(较年初新增："+gtdk[0][1]+"户)</font></html>");
            label_10= new JLabel("<html><font color='#DC143C' size=5>新动力："+xdldk[0][0]+"户</font></html>");//
            label_11= new JLabel("<html><font color='#DC143C' size=5>(较年初新增："+xdldk[0][1]+"户)</font></html>");
            label_12= new JLabel("<html><font color='Lime' size=5>农户："+nhdk[0][0]+"户</font></html>");//
            label_13= new JLabel("<html><font color='Lime' size=5>(较年初新增："+nhdk[0][1]+"户)</font></html>");
            label_1.setBounds(50, 0, 500, 50);
            label_2.setBounds(0, 50, 300, 50);
            label_3.setBounds(label_2.getWidth(), 50, 200, 50);
            label_4.setBounds(0,100, 300, 50);
            label_5.setBounds(label_2.getWidth(), 100, 200, 50);
            label_6.setBounds(0,150, 300, 50);
            label_7.setBounds(label_2.getWidth(), 150, 200, 50);
            label_8.setBounds(0,200, 300, 50);
            label_9.setBounds(label_2.getWidth(), 200, 200, 50);
            label_10.setBounds(0,250, 300, 50);
            label_11.setBounds(label_2.getWidth(), 250, 200, 50);
            label_12.setBounds(0,300, 300, 50);
            label_13.setBounds(label_2.getWidth(), 300, 200, 50);
            panel.setLayout(null); //
            panel.setPreferredSize(new Dimension(800, 800)); //
            panel.add(label_1); //
            panel.add(label_2); //
            panel.add(label_3);
            panel.add(label_4);
            panel.add(label_5);
            panel.add(label_6);
            panel.add(label_7);
            panel.add(label_8);
            panel.add(label_9);
            panel.add(label_10);
            panel.add(label_11);
            panel.add(label_12);
            panel.add(label_13);
            final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            java.util.Timer timer =new java.util.Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println(">>>>>>>>>>>全全年贷款目标完成情况任务开始执行"+formatTemp.format(new Date()));
                    try{
                        String dk=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        if(dk==null){
                            System.out.println(">>>>>>>>>>>全全年贷款目标完成情况任务结束"+formatTemp.format(new Date()));
                        }else{
                            //zzl 记录修改的中间表
                            String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='全行各项贷款统计情况' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                            if(count==null){
                                CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
                                        .lookUpRemoteService(CockpitServiceIfc.class);
                                String [][] gxdk=service.getDkStatistical();
                                String [][] dkhs=service.getDkStatisticalHs();
                                String [][] bmdk=service.getBmDkStatisticalHs();
                                String [][] gtdk=service.getGtDkStatisticalHs();
                                String [][] xdldk=service.getXdlDkStatisticalHs();
                                String [][] nhdk=service.getnhDkStatisticalHs();
                                label_1.setText("<html><h2><font color='#FFFFFF' size=6>"+DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"全行各项贷款统计情况</font></h2></html>");//
                                label_2.setText("<html><font color='FFFF00' size=5>各项贷款总额："+gxdk[0][0]+"亿元</font></html>");//
                                label_3.setText("<html><font color='FFFF00' size=5>(较年初新增："+gxdk[0][1]+"亿元)</font></html>");
                                label_4.setText("<html><font color='#DC143C' size=5>有效贷款户数："+dkhs[0][0]+"万户</font></html>");//
                                label_5.setText("<html><font color='#DC143C' size=5>(较年初新增："+dkhs[0][1]+"户)</font></html>");
                                label_6.setText("<html><font color='#0000FF' size=5>便民快贷："+bmdk[0][0]+"户</font></html>");//
                                label_7.setText("<html><font color='#0000FF' size=5>(较年初新增："+bmdk[0][1]+"户)</font></html>");
                                label_8.setText("<html><font color='#7CFC00' size=5>个体工商户："+gtdk[0][0]+"户</font></html>");//
                                label_9.setText("<html><font color='#7CFC00' size=5>(较年初新增："+gtdk[0][1]+"户)</font></html>");
                                label_10.setText("<html><font color='#DC143C' size=5>新动力："+xdldk[0][0]+"户</font></html>");//
                                label_11.setText("<html><font color='#DC143C' size=5>(较年初新增："+xdldk[0][1]+"户)</font></html>");
                                label_12.setText("<html><font color='Lime' size=5>农户："+nhdk[0][0]+"户</font></html>");//
                                label_13.setText("<html><font color='Lime' size=5>(较年初新增："+nhdk[0][1]+"户)</font></html>");
                                panel.updateUI();
                                UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='全行各项贷款统计情况'");
                                System.out.println(">>>>>>>>>>>全全年贷款目标完成情况任务结束"+formatTemp.format(new Date()));
                            }else{
                                System.out.println(">>>>>>>>>>>全全年贷款目标完成情况任务结束"+formatTemp.format(new Date()));
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
