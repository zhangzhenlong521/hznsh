package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.MessageBox;
import cn.com.infostrategy.ui.common.WLTSplitPane;
import cn.com.infostrategy.ui.mdata.BillListPanel;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import java.awt.*;
import java.util.Date;
import java.util.TimerTask;

/**
 * zzl
 * new TitleNewJLabel().getJLabel()-----标题
 * new CkNewJLabel().getJLabel() 0.0   全行情况统计  √
 * new CkBarChart().getJLabel() 0.1  客户经理营销存款排名 √
 * new CkPieChart().getChartPanel() 0.2   全行存款目标完成情况 √
 * new CKgzhMbiaoWcQkuang().getChartPanel() 0.3 上 各支行存款目标完成情况 √
 * new CKgzhWcQkWKPanel().getJLabel() 0.3 下  各支行完成情况排名 √
 * new DkBarChart().getChartPanel() 1.0 全年贷款目标完成情况 √
 * new DkTimeSeriesChart().getChartPanel() 1.1 上 各支行贷款目标完成情况 √
 * new DkgzhWkPanel().getJLabel() 1.1 下 各支行贷款情况完成排名 √
 * new DkFarmersChart().getChartPanel() 1.2 行农户贷款覆盖面 √
 * new DkNewJLabel().getJLabel() 1.3 全行各项贷款统计情况 √ 需要核对数据
 * new DkNumberWKPaenl().getJLabel() 1.4 客户经理营销贷款户数排名 √
 * new BadNewJLabel() 2.0 全行各项不良贷款统计情况 √
 * new BadCollectWKPanel() 2.1 客户经理清收不良贷款排名 √
 * new BadLoanStatisticalChart() 2.2 上 全行不良贷款统计 √
 * new BadLoanWKPanel() 2.2 下 各支行不良贷款任务完成情况 √
 * new QnyWKPanel() 2.3 黔农云 √
 * new QnyMarketingWKPanel().getJLabel() 2.4 营销黔农云排名 √
 * new QnEDaiMarketingWKPanel().getJLabel() 2.5 黔农E贷排名
 *
 */
public class CockpitMainPage extends AbstractWorkPanel {

    @Override
    public void initialize() {
        JFrame jFrame=new JFrame();
        JFrame dkjFrame=new JFrame();
        JFrame bljFrame=new JFrame();
        try{
//            WLTSplitPane ckpane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CkNewJLabel().getJLabel(),new CkBarChart().getJLabel());
//            ckpane1.setDividerLocation(300);
//            WLTSplitPane ckpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,ckpane1,new CkPieChart().getChartPanel());
//            ckpane2.setDividerLocation(600);
//            WLTSplitPane ckpane3=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,ckpane2,new CKgzhMbiaoWcQkuang().getChartPanel());
//            ckpane3.setDividerLocation(900);
//            WLTSplitPane ckpane4=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,ckpane3,new CKgzhWcQkWKPanel().getJLabel() );
//            ckpane4.setDividerLocation(1200);
//            WLTSplitPane dkpane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new DkBarChart().getChartPanel(),new DkTimeSeriesChart().getChartPanel());
//            dkpane1.setDividerLocation(300);
//            WLTSplitPane dkpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,dkpane1,new DkgzhWkPanel().getJLabel());
//            dkpane2.setDividerLocation(600);
//            WLTSplitPane dkpane3=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,dkpane2,new DkFarmersChart().getChartPanel());
//            dkpane3.setDividerLocation(900);
//            WLTSplitPane dkpane4=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,dkpane3,new DkNewJLabel().getJLabel());
//            dkpane4.setDividerLocation(1200);
//            WLTSplitPane dkpane5=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,dkpane4,new DkNumberWKPaenl().getJLabel());
//            dkpane5.setDividerLocation(1500);
//            WLTSplitPane blpane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new BadNewJLabel().getJLabel(),new BadCollectWKPanel().getJLabel());
//            blpane1.setDividerLocation(300);
//            WLTSplitPane blpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,blpane1,new BadLoanStatisticalChart().getChartPanel());
//            blpane2.setDividerLocation(600);
//            WLTSplitPane blpane3=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,blpane2,new BadLoanWKPanel().getJLabel());
//            blpane3.setDividerLocation(900);
//            WLTSplitPane blpane4=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,blpane3,new QnyWKPanel().getJLabel());
//            blpane4.setDividerLocation(1200);
//            WLTSplitPane blpane5=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,blpane4,new QnyMarketingWKPanel().getJLabel());
//            blpane5.setDividerLocation(1500);
//            WLTSplitPane blpane6=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,blpane5,new QnEDaiMarketingWKPanel().getJLabel());
//            blpane6.setDividerLocation(1800);
//            WLTSplitPane titlepane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new TitleNewJLabel().getJLabel("存款"),ckpane4);
//            titlepane.setDividerLocation(50);
//            WLTSplitPane xjpane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,titlepane,dkpane5);
//            xjpane.setDividerLocation(400);
//            WLTSplitPane zjpane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,xjpane,blpane6);
//            zjpane.setDividerLocation(700);
//            JPanel jPanel=new QnyMarketingWKPanel().getJLabel();
//            jPanel.setBackground(new Color(0,0,128));
//            WLTSplitPane titlepane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new TitleNewJLabel().getJLabel(),new 柱状图().getChartPanel());
//            titlepane.setDividerLocation(50);
//            titlepane.setDividerSize(1);
            //-------new  存款-----///
//            WLTSplitPane ckpane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CkNewJLabel().getJLabel(),new CkBarChart().getJLabel());
//            ckpane1.setDividerLocation(600);
//            ckpane1.setDividerSize(1);
//            WLTSplitPane ckpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,ckpane1,new CkPieChart().getChartPanel());
//            ckpane2.setDividerLocation(1200);
//            ckpane2.setDividerSize(1);
//            WLTSplitPane ckpane3=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CKgzhMbiaoWcQkuang().getChartPanel(),new CKgzhWcQkWKPanel().getJLabel());
//            ckpane3.setDividerLocation(1200);
//            ckpane3.setDividerSize(1);
//            WLTSplitPane titlepane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new TitleNewJLabel().getJLabel("存款"),ckpane2);
//            titlepane.setDividerLocation(80);
//            titlepane.setDividerSize(1);
//            WLTSplitPane titlepane2=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,titlepane,ckpane3);
//            titlepane2.setDividerLocation(500);
//            titlepane2.setDividerSize(1);
//            jFrame.add(titlepane2);
//            jFrame.setUndecorated(true);
//            jFrame.getGraphicsConfiguration().getDevice().setFullScreenWindow(jFrame);
//            jFrame.setVisible(true);
            //-------new  存款-----///
            /**
             * 总和一下要不没办法交差
             */
//            WLTSplitPane ckpane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CkNewJLabel().getJLabel(),new CkBarChart().getJLabel());
//            ckpane1.setDividerLocation(600);
//            ckpane1.setDividerSize(1);
//            WLTSplitPane ckpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,ckpane1,new CkPieChart().getChartPanel());
//            ckpane2.setDividerLocation(1200);
//            ckpane2.setDividerSize(1);
//            WLTSplitPane dkpane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new DkBarChart().getChartPanel() ,new DkFarmersChart().getChartPanel());
//            dkpane1.setDividerLocation(600);
//            dkpane1.setDividerSize(1);
//            WLTSplitPane dkpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,dkpane1,new DkNewJLabel().getJLabel());
//            dkpane2.setDividerLocation(1200);
//            dkpane2.setDividerSize(1);
//            WLTSplitPane zhPanl=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,ckpane2,dkpane2);
//            zhPanl.setDividerLocation(500);
//            zhPanl.setDividerSize(1);
//            WLTSplitPane titlepane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new TitleNewJLabel().getJLabel(null),zhPanl);
//            titlepane.setDividerLocation(80);
//            titlepane.setDividerSize(1);
//            jFrame.add(titlepane);
//            jFrame.setUndecorated(true);
//            jFrame.getGraphicsConfiguration().getDevice().setFullScreenWindow(jFrame);
//            jFrame.setVisible(true);
            this.add(new BadNewJLabel().getJLabel());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
