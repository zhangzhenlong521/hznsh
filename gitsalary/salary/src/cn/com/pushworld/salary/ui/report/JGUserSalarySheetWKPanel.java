package cn.com.pushworld.salary.ui.report;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.report.BillReportPanel;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Month;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.TextAnchor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * zzl Ա���ļ�Ч����
 */
public class JGUserSalarySheetWKPanel extends AbstractWorkPanel implements ActionListener {
    private static final long serialVersionUID = -1303612160615140713L;
    private BillQueryPanel billQueryPanel=null;
    private WLTTabbedPane pane = null;//zzl[2020-6-16]
    private WLTSplitPane splitpanel=null;
    private String[] types = null;
    private String[] types_id = null;
    private HashMap map=new HashMap();

    public void initialize() {
//        BillReportPanel reportPanel = new BillReportPanel("REPORTQUERY_CODE4",
//                "cn.com.pushworld.salary.bs.report.UserSalarySheetBuilderAdapter");
        billQueryPanel=new BillQueryPanel("REPORTQUERY_CODE7");
        billQueryPanel.addBillQuickActionListener(this);
        splitpanel = new WLTSplitPane(0, billQueryPanel, getJPanel(null));
        splitpanel.setOpaque(false);
        splitpanel.setDividerLocation(100);
        splitpanel.setVisible(true);
        this.add(splitpanel);

    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==billQueryPanel){
            try {
                String counttype = billQueryPanel.getRealValueAt("counttype");
                String salaryitems = billQueryPanel.getRealValueAt("salaryitems");
                String month_start = billQueryPanel.getRealValueAt("month_start");
                String month_end = billQueryPanel.getRealValueAt("month_end");
                String planway=billQueryPanel.getRealValueAt("planway");
                String dept="����";
                if (counttype == null || counttype.equals("")) {
                    MessageBox.show(this, "��ѡ��ͳ�����ͣ�");
                    return;
                }

                if (salaryitems == null || salaryitems.equals("")) {
                    MessageBox.show(this, "��ѡ������Ŀ��");
                    return;
                }

                if (month_start == null || month_start.equals("") || month_end == null || month_end.equals("")) {
                    MessageBox.show(this, "��ѡ��ʼ�·�������·ݣ�");
                    return;
                }

                String account_set = new TBUtil().getSysOptionStringValue("�ۺϱ�����ͳ������", "��ͨ����");
                account_set=dept.equals("����")?"���ع�������":"��Ч��������";
                HashVO[] hvs_log = UIUtil.getHashVoArrayByDS(null, "select id, monthly from sal_salarybill where " + "sal_account_setid in (select id from sal_account_set where name in('" + account_set.replace(";", "','") + "')) " + "and monthly>='" + month_start + "' and monthly<='" + month_end + "' order by monthly");
                if (hvs_log == null || !(hvs_log.length > 0)) {
                    MessageBox.show(this, "û�в�ѯ�������");
                    return;
                }

                HashVO[] hvs_items = UIUtil.getHashVoArrayByDS(null, "select factorid, viewname from sal_account_factor " + "where accountid in(select id from sal_account_set where name in('" + account_set.replace(";", "','") + "')) " + "and factorid in('" + salaryitems.replace(";", "','") + "') order by seq");

                if (hvs_items == null || !(hvs_items.length > 0)) {
                    MessageBox.show(this, "û�в�ѯ�������");
                    return;
                }

                LinkedHashMap hm = new LinkedHashMap();
                for (int i = 0; i < hvs_log.length; i++) {
                    String id = hvs_log[i].getStringValue("id", "");
                    String monthly = hvs_log[i].getStringValue("monthly", "");
                    if (hm.containsKey(monthly)) {
                        hm.put(monthly, id + "," + hm.get(monthly));
                        map.put(id,monthly);
                    } else {
                        hm.put(monthly, id);
                        map.put(id,monthly);
                    }
                }

                String[] str_keys = (String[]) hm.keySet().toArray(new String[0]);
                String[] checkdates = new String[str_keys.length];
                String[] checkids = new String[str_keys.length];
                for (int i = 0; i < str_keys.length; i++) {
                    checkids[i] = (String) hm.get(str_keys[i]);
                    checkdates[i] = str_keys[i];
                }

                types = new String[hvs_items.length];
                types_id = new String[hvs_items.length];
                for (int i = 0; i < hvs_items.length; i++) {
                    types_id[i] = hvs_items[i].getStringValue("factorid", "");
                    types[i] = hvs_items[i].getStringValue("viewname", "");
                }
                HashVO [] vos=null;
                if(counttype.equals("������")){
                    vos=getDeptSalary(checkids, types_id,planway,dept);
                }else{
                    vos=getPostSalary(checkids, types_id,planway,dept);
                }
                this.removeAll();
                billQueryPanel.setRealValueAt("counttype",counttype);
                billQueryPanel.setRealValueAt("salaryitems",salaryitems);
                billQueryPanel.setRealValueAt("month_start",month_start);
                billQueryPanel.setRealValueAt("month_end",month_end);
                billQueryPanel.setRealValueAt("planway",planway);
                splitpanel = new WLTSplitPane(0, billQueryPanel,getJPanel(vos));
                splitpanel.setOpaque(false);
                splitpanel.setDividerLocation(100);
                splitpanel.setVisible(true);
                this.add(splitpanel);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    public JPanel getJPanel( HashVO [] vos){
        CategoryDataset ds = getDataSet(vos);
        JFreeChart chart = ChartFactory.createBarChart3D(
                "���ؼ�Ч����", //ͼ�����
                "���ز�������", //Ŀ¼�����ʾ��ǩ
                "��Ч", //��ֵ�����ʾ��ǩ
                ds, //���ݼ�
                PlotOrientation.VERTICAL, //ͼ����
                true, //�Ƿ���ʾͼ�������ڼ򵥵���״ͼ����Ϊfalse
                false, //�Ƿ�������ʾ����
                false);         //�Ƿ�����url����

        CategoryPlot categoryplot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) categoryplot.getRenderer();
        //��ʾ��Ŀ��ǩ
        renderer.setBaseItemLabelsVisible(true);
        //������Ŀ��ǩ������,��JFreeChart1.0.6֮ǰ����ͨ��renderer.setItemLabelGenerator(CategoryItemLabelGenerator generator)����ʵ�֣����ǴӰ汾1.0.6��ʼ�����淽������
        renderer
                .setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        //������Ŀ��ǩ��ʾ��λ��,outline��ʾ����Ŀ������,baseline_center��ʾ���ڻ����Ҿ���
        renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_CENTER));

        NumberAxis numberaxis = (NumberAxis) categoryplot.getRangeAxis();

        CategoryAxis domainAxis = categoryplot.getDomainAxis();
        Font labelFont = new Font("����", Font.TRUETYPE_FONT, 12);
        domainAxis.setLabelFont(labelFont);// X��ı���bai��������
        domainAxis.setTickLabelFont(labelFont);//X����������ֵ����
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // X���ϵ�duLable����45����б

//       /*------����X�������ϵ�����-----------*/
//       domainAxis.setTickLabelFont(new Font("����", Font.PLAIN, 13));
//
//       /*------����X��ı�������------------*/
//       domainAxis.setLabelFont(new Font("����", Font.PLAIN, 12));

        /*------����Y�������ϵ�����-----------*/
        numberaxis.setTickLabelFont(new Font("sans-serif", Font.PLAIN, 12));

        /*------����Y��ı�������------------*/
        numberaxis.setLabelFont(new Font("����", Font.PLAIN, 12));

        /*------���������˵ײ��������������-----------*/
        chart.getLegend().setItemFont(new Font("����", Font.PLAIN, 12));

        /*******���������˱��⺺�����������********/
        chart.getTitle().setFont(new Font("����", Font.PLAIN, 12));


        JPanel jPanel = new ChartPanel(chart);

        return jPanel;
    }
    // ���Ź��ʻ���
    public HashVO[] getDeptSalary(String[] checkids, String[] types_id, String planway,String dept) throws Exception {
        if (checkids != null && checkids.length > 0) {
            StringBuffer sb_sql = new StringBuffer();

            String finalres = "";
            String logids = "";
            for (int i = 0; i < checkids.length; i++) {
                if (i == 0) {
                    logids += "'" + checkids[i].replace(",", "','") + "'";
                } else {
                    logids += ",'" + checkids[i].replace(",", "','") + "'";
                }

                for (int j = 0; j < types_id.length; j++) {
                    finalres += ", sum(a" + i + "_" + j + ".sum) \""+map.get(checkids[i])+"\"";
                }
            }

            sb_sql.append(" select b.name corpname" + finalres);
            if(dept.equals("����")){
                sb_sql.append(" from (select * from v_sal_personinfo where PLANWAY='"+planway+"' and STATIONKIND in('ǰ̨��Ա','�в����')) a left join pub_corp_dept b on a.maindeptid=b.id ");
            }else{
                sb_sql.append(" from (select * from v_sal_personinfo where PLANWAY='"+planway+"' and STATIONKIND not in('ǰ̨��Ա','�в����')) a left join pub_corp_dept b on a.maindeptid=b.id ");
            }
            for (int i = 0; i < checkids.length; i++) {
                for (int j = 0; j < types_id.length; j++) {
                    sb_sql.append(" left join (select userid,sum(factorvalue) sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
                }
            }
            sb_sql.append(" where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in (" + logids + ")) " + "group by b.name");//order by b.linkcode

            return UIUtil.getHashVoArrayByDS(null, sb_sql.toString());
        }
        return null;
    }
    public CategoryDataset getDataSet(HashVO [] vos) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        for(Object str:map.keySet()){
            for(int i=0;i<vos.length;i++){
                if(vos[i].getStringValue(map.get(str).toString()).equals("") || vos[i].getStringValue(map.get(str).toString())==null){
                    ds.addValue(0.0,str.toString(),vos[i].getStringValue("corpname"));
                }else{
                    ds.addValue(Double.parseDouble(vos[i].getStringValue(map.get(str).toString())),map.get(str).toString(),vos[i].getStringValue("corpname"));
                }
            }
        }
//        ds.addValue(100, "����", "ƻ��");
//        ds.addValue(100, "�Ϻ�", "ƻ��");
//        ds.addValue(100, "����", "ƻ��");
//        ds.addValue(200, "����", "����");
//        ds.addValue(200, "�Ϻ�", "����");
//        ds.addValue(200, "����", "����");
//        ds.addValue(300, "����", "����");
//        ds.addValue(300, "�Ϻ�", "����");
//        ds.addValue(300, "����", "����");
//        ds.addValue(400, "����", "����");
//        ds.addValue(400, "�Ϻ�", "����");
//        ds.addValue(400, "����", "����");
//        ds.addValue(500, "����", "�㽶");
//        ds.addValue(500, "�Ϻ�", "�㽶");
//        ds.addValue(500, "����", "�㽶");
        return ds;
    }
    // ��λ���ʻ���
    public HashVO[] getPostSalary(String[] checkids, String[] types_id,String planway,String dept) throws Exception {
        if (checkids != null && checkids.length > 0) {
            StringBuffer sb_sql = new StringBuffer();

            String finalres = "";
            String logids = "";
            for (int i = 0; i < checkids.length; i++) {
                if (i == 0) {
                    logids += "'" + checkids[i].replace(",", "','") + "'";
                } else {
                    logids += ",'" + checkids[i].replace(",", "','") + "'";
                }

                for (int j = 0; j < types_id.length; j++) {
                    finalres += ", ROUND(sum(a" + i + "_" + j + ".sum)/count(a.stationkind),2) \""+map.get(checkids[i])+"\"";
                }
            }

            sb_sql.append(" select a.stationkind corpname" + finalres);
            if(dept.equals("����")){
                sb_sql.append(" from (select * from v_sal_personinfo where PLANWAY='"+planway+"' and STATIONKIND in('ǰ̨��Ա','�в����')) a ");
            }else{
                sb_sql.append(" from (select * from v_sal_personinfo where PLANWAY='"+planway+"' and STATIONKIND not in('ǰ̨��Ա','�в����')) a ");
            }
            for (int i = 0; i < checkids.length; i++) {
                for (int j = 0; j < types_id.length; j++) {
                    sb_sql.append(" left join (select userid,sum(factorvalue) sum from sal_salarybill_detail " + "where salarybillid in('" + checkids[i].replace(",", "','") + "') and factorid='" + types_id[j] + "' group by userid)" + " a" + i + "_" + j + " on a" + i + "_" + j + ".userid = a.id ");
                }
            }
            sb_sql.append(" where a.id in(select distinct userid from sal_salarybill_detail where salarybillid in (" + logids + ")) " + "group by a.stationkind ");//order by a.code,a.stationkind

            return UIUtil.getHashVoArrayByDS(null, sb_sql.toString());
        }
        return null;
    }

}
