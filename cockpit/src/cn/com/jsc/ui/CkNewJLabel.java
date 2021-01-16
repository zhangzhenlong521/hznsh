package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 存款文字描述
 */
public class CkNewJLabel{
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
            final String [][] gxck=service.getCkStatistical();
            final String [][] ckhs=service.getCKHsCount();
            final String [][] grhq=service.getCKGeRenCount();
            final String [][] grdq=service.getCKGeRenDQCount();
            final String [][] dghq=service.getCKDgHqCount();
            final String [][] dgdq=service.getCKDgDqCount();
            label_1 = new JLabel("<html><h2><font color='#FFFFFF' size=6>"+DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"全行各项存款统计情况</font></h2></html>");//
            label_2 = new JLabel("<html><font color='FFFF00' size=5>各项存款总额："+gxck[0][0]+"亿元</font></html>");//
            label_3= new JLabel("<html><font color='FFFF00' size=5>(较年初新增："+gxck[0][1]+"亿元)</font></html>");
            label_4 = new JLabel("<html><font color='#7CFC00' size=5>有效存款户数："+ckhs[0][0]+"万户</font></html>");//
            label_5= new JLabel("<html><font color='#7CFC00' size=5>(较年初新增："+ckhs[0][1]+"户)</font></html>");
            label_6= new JLabel("<html><font color='#DC143C' size=5>个人活期存款："+grhq[0][0]+"亿元</font></html>");//
            label_7= new JLabel("<html><font color='#DC143C' size=5>(较年初新增："+grhq[0][1]+"亿元)</font></html>");
            label_8= new JLabel("<html><font color='#800080' size=5>个人定期存款："+grdq[0][0]+"亿元</font></html>");//
            label_9= new JLabel("<html><font color='#800080' size=5>(较年初新增："+grdq[0][1]+"亿元)</font></html>");
            label_10= new JLabel("<html><font color='#DC143C' size=5>对公活期存款："+dghq[0][0]+"亿元</font></html>");//
            label_11= new JLabel("<html><font color='#DC143C' size=5>(较年初新增："+dghq[0][1]+"亿元)</font></html>");
            label_12= new JLabel("<html><font color='Lime' size=5>对公定期存款："+dgdq[0][0]+"亿元</font></html>");//
            label_13= new JLabel("<html><font color='Lime' size=5>(较年初新增："+dgdq[0][1]+"亿元)</font></html>");
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
                    System.out.println(">>>>>>>>>>>各项存款统计情况开始任务"+formatTemp.format(new Date()));
                    CockpitServiceIfc service = null;
                    try {
                        service = (CockpitServiceIfc) UIUtil
                                .lookUpRemoteService(CockpitServiceIfc.class);
                        String grhqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_psn_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        String grdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        String dghgs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_fx_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        String dgdqs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.a_agr_dep_acct_ent_sv_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        String kuxxs=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.S_OFCR_CI_CUSTMAST_"+DateUIUtil.getqytMonth()+" where load_dates='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        if(grhqs==null || grdqs==null || dghgs==null || dgdqs==null || kuxxs==null){
                            System.out.println(">>>>>>>>>>>各项存款统计情况退出任务"+formatTemp.format(new Date()));
                           return;
                        }else{
                            //zzl 记录修改的中间表
                            String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='各项存款统计情况' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                            if(count==null){
                                String [][] gxck=service.getCkStatistical();
                                String [][] ckhs=service.getCKHsCount();
                                String [][] grhq=service.getCKGeRenCount();
                                String [][] grdq=service.getCKGeRenDQCount();
                                String [][] dghq=service.getCKDgHqCount();
                                String [][] dgdq=service.getCKDgDqCount();
                                label_1.setText("<html><h2><font color='#FFFFFF' size=6>"+DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"全行各项存款统计情况</font></h2></html>");//
                                label_2.setText("<html><font color='FFFF00' size=5>各项存款总额："+gxck[0][0]+"亿元</font></html>");//
                                label_3.setText("<html><font color='FFFF00' size=5>(较年初新增："+gxck[0][1]+"亿元)</font></html>");
                                label_4.setText("<html><font color='#7CFC00' size=5>有效存款户数："+ckhs[0][0]+"万户</font></html>");//
                                label_5.setText("<html><font color='#7CFC00' size=5>(较年初新增："+ckhs[0][1]+"户)</font></html>");
                                label_6.setText("<html><font color='#DC143C' size=5>个人活期存款："+grhq[0][0]+"亿元</font></html>");//
                                label_7.setText("<html><font color='#DC143C' size=5>(较年初新增："+grhq[0][1]+"亿元)</font></html>");
                                label_8.setText("<html><font color='#800080' size=5>个人定期存款："+grdq[0][0]+"亿元</font></html>");//
                                label_9.setText("<html><font color='#800080' size=5>(较年初新增："+grdq[0][1]+"亿元)</font></html>");
                                label_10.setText("<html><font color='#DC143C' size=5>对公活期存款："+dghq[0][0]+"亿元</font></html>");//
                                label_11.setText("<html><font color='#DC143C' size=5>(较年初新增："+dghq[0][1]+"亿元)</font></html>");
                                label_12.setText("<html><font color='Lime' size=5>对公定期存款："+dgdq[0][0]+"亿元</font></html>");//
                                label_13.setText("<html><font color='Lime' size=5>(较年初新增："+dgdq[0][1]+"亿元)</font></html>");
                                label_1.updateUI();
                                label_2.updateUI();
                                label_3.updateUI();
                                label_4.updateUI();
                                label_5.updateUI();
                                label_6.updateUI();
                                label_7.updateUI();
                                label_8.updateUI();
                                label_9.updateUI();
                                label_10.updateUI();
                                label_11.updateUI();
                                label_12.updateUI();
                                label_13.updateUI();
                                panel.updateUI();
                                UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='各项存款统计情况'");
                            }else{
                                System.out.println(">>>>>>>>>>>各项存款统计情况退出任务"+formatTemp.format(new Date()));
                                return;

                            }
                        }
                    } catch (Exception e) {
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
