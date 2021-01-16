package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * 客户经理营销贷款户数排名
 */
public class DkNumberWKPaenl {
    private JPanel panel = new JPanel();
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
            String [][] ckpm=service.getDKRanking();
            panel.setLayout(null); //
            panel.setPreferredSize(new Dimension(1000, 1000)); //
            label_1 = new JLabel("<html><h3><font color='Black' size=6>"+DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"客户经理营销贷款排名</font></h3></html>");//
            label_1.setBounds(25, 0, 500, 50);
            panel.add(label_1);
            String [] col={"TOP","客户经理","较年初新增","支行名称"};
            int a=0;
            for(int i=1;i<4;i++){
                ImageIcon imgIcon= UIUtil.getImageFromServer("/applet/sw.gif");//
                label_2 = new JLabel("<html><font color='#191970' size=5>TOP"+i+"</font></html>");//
                label_2.setBounds(0, a+50, 100, 50);
                label_3= new JLabel("<html><font color='#191970' size=5>"+ckpm[i-1][0]+"</font></html>");
                label_3.setBounds(50, a+50, 100, 50);
                label_4= new JLabel("<html><font color='#191970' size=5>较年初新增("+ckpm[i-1][1]+"万元)</font></html>");
                label_4.setBounds(120, a+50, 200, 50);
                label_5= new JLabel("<html><font color='#191970' size=5>"+ckpm[i-1][2]+"</font></html>");
                label_5.setBounds(330, a+50, 100, 50);
                label_6= new JLabel(imgIcon);
                label_6.setBounds(400, a+50, 100, 50);
                panel.add(label_2);
                panel.add(label_3);
                panel.add(label_4);
                panel.add(label_5);
                panel.add(label_6);
                a=label_2.getY();
            }
            for(int i=1;i<4;i++){
                ImageIcon imgIcon= UIUtil.getImageFromServer("/applet/kq.png");//
                label_2 = new JLabel("<html><font color='#FF0000' size=4>TOP"+(ckpm.length-3+i-1)+"</font></html>");//
                label_2.setBounds(0, a+50, 100, 50);
                label_3= new JLabel("<html><font color='#FF0000' size=4>"+ckpm[ckpm.length-3+i-1][0]+"</font></html>");
                label_3.setBounds(50, a+50, 100, 50);
                label_4= new JLabel("<html><font color='#FF0000' size=4>较年初新增("+ckpm[ckpm.length-3+i-1][1]+"万元)</font></html>");
                label_4.setBounds(120, a+50, 200, 50);
                label_5= new JLabel("<html><font color='#FF0000' size=4>"+ckpm[ckpm.length-3+i-1][2]+"</font></html>");
                label_5.setBounds(330, a+50, 100, 50);
                label_6= new JLabel(imgIcon);
                label_6.setBounds(400, a+50, 100, 50);
                panel.add(label_2);
                panel.add(label_3);
                panel.add(label_4);
                panel.add(label_5);
                panel.add(label_6);
                a=label_2.getY();
            }
            final SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            java.util.Timer timer =new java.util.Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    try{
                        System.out.println(">>>>>>>>>>>客户经理营销贷款排名任务开始"+formatTemp.format(new Date()));
                        CockpitServiceIfc service = (CockpitServiceIfc) UIUtil
                                .lookUpRemoteService(CockpitServiceIfc.class);
                        String dk=UIUtil.getStringValueByDS(null,"select biz_dt from hzbank.s_loan_dk_"+DateUIUtil.getqytMonth()+" where biz_dt='"+DateUIUtil.getQYTTime()+"' and rownum=1");
                        if(dk==null){
                            System.out.println(">>>>>>>>>>>客户经理营销贷款排名退出任务"+formatTemp.format(new Date()));
                            return;
                        }else{
                            //zzl 记录修改的中间表
                            String count=UIUtil.getStringValueByDS(null,"select name from s_count where name='客户经理营销贷款排名' and dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"'");
                            if(count==null){
                                JLabel [][] jLabels=new JLabel[6][4];
                                JLabel JLabel= (javax.swing.JLabel) panel.getComponents()[0];
                                JLabel.setText("<html><h3><font color='Black' size=6>"+DateUIUtil.getDateMonth(1,"yyyy年MM月dd日")+"客户经理营销贷款排名</font></h3></html>");
                                int row=0;
                                int col=0;
                                for(int i=1;i<panel.getComponents().length;i++){
                                    if(i%5==0){
                                        row++;
                                        col=0;
                                    }else{
                                        jLabels[row][col]=(javax.swing.JLabel)panel.getComponents()[i];
                                        col++;
                                    }
                                }
                                String [][] ckpm=service.getDKRanking();
                                for(int i=1;i<4;i++){
                                    jLabels[i-1][0].setText("<html><font color='#191970' size=5>TOP"+i+"</font></html>");
                                    jLabels[i-1][1].setText("<html><font color='#191970' size=5>"+ckpm[i-1][0]+"</font></html>");
                                    jLabels[i-1][2].setText("<html><font color='#191970' size=5>较年初新增("+ckpm[i-1][1]+"万元)</font></html>");
                                    jLabels[i-1][3].setText("<html><font color='#191970' size=5>"+ckpm[i-1][2]+"</font></html>");
                                }
                                for(int i=1;i<4;i++){
                                    jLabels[i+2][0].setText("<html><font color='#FF0000' size=4>TOP"+(ckpm.length-3+i-1)+"</font></html>");
                                    jLabels[i+2][1].setText("<html><font color='#FF0000' size=4>"+ckpm[ckpm.length-3+i-1][0]+"</font></html>");
                                    jLabels[i+2][2].setText("<html><font color='#FF0000' size=4>较年初新增("+ckpm[ckpm.length-3+i-1][1]+"万元)</font></html>");
                                    jLabels[i+2][3].setText("<html><font color='#FF0000' size=4>"+ckpm[ckpm.length-3+i-1][2]+"</font></html>");
                                }
                                UIUtil.executeUpdateByDS(null,"Update s_count set dates='"+DateUIUtil.getDateMonth(0,"yyyyMMdd")+"' where name='客户经理营销贷款排名'");
                                panel.updateUI();
                                System.out.println(">>>>>>>>>>>客户经理营销贷款排名退出任务"+formatTemp.format(new Date()));
                            }else{
                                System.out.println(">>>>>>>>>>>客户经理营销贷款排名退出任务"+formatTemp.format(new Date()));
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
        return panel;

    }
}
