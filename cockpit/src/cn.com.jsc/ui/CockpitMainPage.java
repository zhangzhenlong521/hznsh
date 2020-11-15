package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTSplitPane;

import javax.swing.*;
import java.awt.*;

/**
 * zzl
 * new CkNewJLabel().getJLabel() 0.0   全行情况统计
 * new CkBarChart().getChartPanel() 0.1  客户经理营销存款排名
 * new CkPieChart().getChartPanel() 0.2   全行存款目标完成情况
 * new CKgzhMbiaoWcQkuang().getChartPanel() 0.3 上 各支行存款目标完成情况
 * new CKgzhWcQkWKPanel().getBillListPanel() 0.3 下  各支行完成情况排名
 * new DkBarChart().getChartPanel() 1.0 全年贷款目标完成情况
 * new DkTimeSeriesChart().getChartPanel() 1.1 上 各支行贷款目标完成情况
 * new DkgzhWkPanel().getBillListPanel() 1.1 下 各支行贷款目标完成情况列表
 * new DkFarmersChart().getChartPanel() 1.2 行农户贷款覆盖面
 * new DkNewJLabel().getJLabel() 1.3 全行各项贷款统计情况
 * new DkNumberWKPaenl().getBillListPanel() 1.4 客户经理营销贷款户数排名
 * new BadNewJLabel() 2.0 全行各项不良贷款统计情况
 * new BadCollectWKPanel() 2.1 客户经理清收不良贷款排名
 * new BadLoanStatisticalChart() 2.2 上 全行不良贷款统计
 * new BadLoanWKPanel() 2.2 下 各支行不良贷款任务完成情况
 * new QnyWKPanel() 2.3 黔农云
 * new QnyMarketingWKPanel().getBillListPanel() 2.4 营销黔农云排名
 * new QnEDaiMarketingWKPanel().getBillListPanel() 2.5 黔农E贷排名
 *
 */
public class CockpitMainPage extends AbstractWorkPanel {

    @Override
    public void initialize() {
        JFrame jFrame=new JFrame();
        WLTSplitPane cKcop_jpane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CkNewJLabel().getJLabel(),new CkBarChart().getJLabel());//zzl 竖向分屏
        cKcop_jpane.setDividerLocation(500);//zzl 存款的说明和柱状图   new CkNewJLabel().getJLabel() 1.0 new CkBarChart().getChartPanel()
        cKcop_jpane.setOpaque(true);
        //各支行存款情况统计
        WLTSplitPane gzhck_jpane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new CKgzhMbiaoWcQkuang().getChartPanel(),new CKgzhWcQkWKPanel().getBillListPanel());
        gzhck_jpane.setDividerLocation(200);
        WLTSplitPane gzhck_jpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CkPieChart().getChartPanel(),gzhck_jpane);
        gzhck_jpane2.setDividerLocation(200);
        WLTSplitPane cKcop_pane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,cKcop_jpane,gzhck_jpane2);//zzl 竖向分屏
        cKcop_pane.setDividerLocation(1000);
        cKcop_pane.setOpaque(true);
        WLTSplitPane title_jpane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new TitleNewJLabel().getJLabel(),cKcop_pane);//zzl 加入农商行的logo
        title_jpane.setDividerLocation(50); //
        title_jpane.setOpaque(true);
        WLTSplitPane dkcop_pane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new DkTimeSeriesChart().getChartPanel(),new DkgzhWkPanel().getBillListPanel());
        dkcop_pane.setDividerLocation(200);
        WLTSplitPane DKcop_pane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new DkBarChart().getChartPanel(),dkcop_pane);//zzl 竖向分屏
        DKcop_pane.setDividerLocation(300);
        DKcop_pane.setOpaque(true);
        WLTSplitPane DKcop_pane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,DKcop_pane,new DkFarmersChart().getChartPanel());
        DKcop_pane1.setDividerLocation(800);
        WLTSplitPane DKcop_pane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,DKcop_pane1,new DkNewJLabel().getJLabel());
        DKcop_pane2.setDividerLocation(1200);
        WLTSplitPane DKcop_pane3=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,DKcop_pane2,new DkNumberWKPaenl().getBillListPanel());
        DKcop_pane3.setDividerLocation(1500);
        WLTSplitPane cop_pane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, title_jpane, DKcop_pane3); //zzl 横向分屏
        cop_pane.setDividerLocation(380); //
        cop_pane.setOpaque(true);
        WLTSplitPane bad_pane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new BadNewJLabel().getJLabel(),new BadCollectWKPanel().getBillListPanel());
        bad_pane.setDividerLocation(300);
        WLTSplitPane bad_pane1=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new BadLoanStatisticalChart().getChartPanel(),new BadLoanWKPanel().getBillListPanel());
        bad_pane1.setDividerLocation(200);
        WLTSplitPane bad_pane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,bad_pane,bad_pane1);
        bad_pane2.setDividerLocation(900);
        WLTSplitPane bad_pane3=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,bad_pane2,new QnyWKPanel().getJLabel());
        bad_pane3.setDividerLocation(1200);
        WLTSplitPane bad_pane4=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,bad_pane3,new QnyMarketingWKPanel().getBillListPanel());
        bad_pane4.setDividerLocation(1500);
        WLTSplitPane bad_pane5=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,bad_pane4,new QnEDaiMarketingWKPanel().getBillListPanel());
        bad_pane5.setDividerLocation(1800);
        WLTSplitPane zh_pane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, cop_pane, bad_pane5); //zzl 横向分屏
        zh_pane.setDividerLocation(700); //
//        jFrame.add(zh_pane);
//        jFrame.setVisible(true);
        this.add(new CkNewJLabel().getJLabel());
    }
}
