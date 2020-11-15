package cn.com.jsc.ui;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.BillDialog;
import cn.com.infostrategy.ui.common.WLTSplitPane;

import javax.swing.*;
import java.awt.*;

/**
 * zzl
 * new CkNewJLabel().getJLabel() 0.0   ȫ�����ͳ��
 * new CkBarChart().getChartPanel() 0.1  �ͻ�����Ӫ���������
 * new CkPieChart().getChartPanel() 0.2   ȫ�д��Ŀ��������
 * new CKgzhMbiaoWcQkuang().getChartPanel() 0.3 �� ��֧�д��Ŀ��������
 * new CKgzhWcQkWKPanel().getBillListPanel() 0.3 ��  ��֧������������
 * new DkBarChart().getChartPanel() 1.0 ȫ�����Ŀ��������
 * new DkTimeSeriesChart().getChartPanel() 1.1 �� ��֧�д���Ŀ��������
 * new DkgzhWkPanel().getBillListPanel() 1.1 �� ��֧�д���Ŀ���������б�
 * new DkFarmersChart().getChartPanel() 1.2 ��ũ���������
 * new DkNewJLabel().getJLabel() 1.3 ȫ�и������ͳ�����
 * new DkNumberWKPaenl().getBillListPanel() 1.4 �ͻ�����Ӫ�����������
 * new BadNewJLabel() 2.0 ȫ�и��������ͳ�����
 * new BadCollectWKPanel() 2.1 �ͻ��������ղ�����������
 * new BadLoanStatisticalChart() 2.2 �� ȫ�в�������ͳ��
 * new BadLoanWKPanel() 2.2 �� ��֧�в�����������������
 * new QnyWKPanel() 2.3 ǭũ��
 * new QnyMarketingWKPanel().getBillListPanel() 2.4 Ӫ��ǭũ������
 * new QnEDaiMarketingWKPanel().getBillListPanel() 2.5 ǭũE������
 *
 */
public class CockpitMainPage extends AbstractWorkPanel {

    @Override
    public void initialize() {
        JFrame jFrame=new JFrame();
        WLTSplitPane cKcop_jpane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CkNewJLabel().getJLabel(),new CkBarChart().getJLabel());//zzl �������
        cKcop_jpane.setDividerLocation(500);//zzl ����˵������״ͼ   new CkNewJLabel().getJLabel() 1.0 new CkBarChart().getChartPanel()
        cKcop_jpane.setOpaque(true);
        //��֧�д�����ͳ��
        WLTSplitPane gzhck_jpane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new CKgzhMbiaoWcQkuang().getChartPanel(),new CKgzhWcQkWKPanel().getBillListPanel());
        gzhck_jpane.setDividerLocation(200);
        WLTSplitPane gzhck_jpane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new CkPieChart().getChartPanel(),gzhck_jpane);
        gzhck_jpane2.setDividerLocation(200);
        WLTSplitPane cKcop_pane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,cKcop_jpane,gzhck_jpane2);//zzl �������
        cKcop_pane.setDividerLocation(1000);
        cKcop_pane.setOpaque(true);
        WLTSplitPane title_jpane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new TitleNewJLabel().getJLabel(),cKcop_pane);//zzl ����ũ���е�logo
        title_jpane.setDividerLocation(50); //
        title_jpane.setOpaque(true);
        WLTSplitPane dkcop_pane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,new DkTimeSeriesChart().getChartPanel(),new DkgzhWkPanel().getBillListPanel());
        dkcop_pane.setDividerLocation(200);
        WLTSplitPane DKcop_pane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,new DkBarChart().getChartPanel(),dkcop_pane);//zzl �������
        DKcop_pane.setDividerLocation(300);
        DKcop_pane.setOpaque(true);
        WLTSplitPane DKcop_pane1=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,DKcop_pane,new DkFarmersChart().getChartPanel());
        DKcop_pane1.setDividerLocation(800);
        WLTSplitPane DKcop_pane2=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,DKcop_pane1,new DkNewJLabel().getJLabel());
        DKcop_pane2.setDividerLocation(1200);
        WLTSplitPane DKcop_pane3=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,DKcop_pane2,new DkNumberWKPaenl().getBillListPanel());
        DKcop_pane3.setDividerLocation(1500);
        WLTSplitPane cop_pane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, title_jpane, DKcop_pane3); //zzl �������
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
        WLTSplitPane zh_pane = new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT, cop_pane, bad_pane5); //zzl �������
        zh_pane.setDividerLocation(700); //
//        jFrame.add(zh_pane);
//        jFrame.setVisible(true);
        this.add(new CkNewJLabel().getJLabel());
    }
}
