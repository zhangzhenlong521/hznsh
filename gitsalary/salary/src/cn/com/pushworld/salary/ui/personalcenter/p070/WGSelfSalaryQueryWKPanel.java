package cn.com.pushworld.salary.ui.personalcenter.p070;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.BillCellItemVO;
import cn.com.infostrategy.to.mdata.BillCellVO;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.BillQueryPanel;
import cn.com.infostrategy.ui.mdata.querycomp.QueryCPanel_UIRefPanel;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefEvent;
import cn.com.infostrategy.ui.report.BillCellHtmlHrefListener;
import cn.com.infostrategy.ui.report.style2.DefaultStyleReportPanel_2;
import cn.com.pushworld.salary.ui.SalaryUIUtil;

/**
 * zzl[�����ʵ�չʾ]
 * 2020-5-19
 */
public class WGSelfSalaryQueryWKPanel extends AbstractWorkPanel implements ActionListener, BillCellHtmlHrefListener {
    private static final long serialVersionUID = 1L;
    private DefaultStyleReportPanel_2 dr= null;//zzl [2020-5-19] �����ʵ�
    private BillQueryPanel bq = null;
    private HashMap detailid_desc = null;
    private String exportFileName = "���ʵ�_";
    private WLTTabbedPane pane = null;//zzl[2020-5-19] ���������ʵ���ҳǩ

    public void initialize() {
        dr = new DefaultStyleReportPanel_2("SAL_SALARYBILL_CODE1", "");
        bq = dr.getBillQueryPanel();
        //��������Ĭ��ֵΪ��ǰ��������  Gwang 2013-08-21
        QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("monthly");
        String checkDate = new SalaryUIUtil().getCheckDate();
        dateRef.setValue(checkDate);

        //���õ����ļ����� Gwang 2013-08-22
        String loginUserName = ClientEnvironment.getInstance().getLoginUserName();
        exportFileName += loginUserName;
        dr.setReportExpName(exportFileName);
        //zzl[2020-5-19] �õ���¼��Code ����code�õ�����id
        String loginCorpdistinct = ClientEnvironment.getInstance().getLoginUserDeptCorpdistinct();
        dr.getBillCellPanel().setEditable(false);
        dr.getBillCellPanel().addBillCellHtmlHrefListener(this);
        JLabel info = new JLabel("������ֿɲ鿴�������");
        info.setOpaque(false);
        info.setForeground(Color.RED);
        dr.getPanel_btn().add(info);
        bq.addBillQuickActionListener(this);
    }
    public DefaultStyleReportPanel_2 getDr(){
        dr = new DefaultStyleReportPanel_2("SAL_SALARYBILL_CODE1", "");
        bq = dr.getBillQueryPanel();
        //��������Ĭ��ֵΪ��ǰ��������  Gwang 2013-08-21
        QueryCPanel_UIRefPanel dateRef = (QueryCPanel_UIRefPanel) bq.getCompentByKey("monthly");
        String checkDate = new SalaryUIUtil().getCheckDate();
        dateRef.setValue(checkDate);

        //���õ����ļ����� Gwang 2013-08-22
        String loginUserName = ClientEnvironment.getInstance().getLoginUserName();
        exportFileName += loginUserName;
        dr.setReportExpName(exportFileName);
        //zzl[2020-5-19] �õ���¼��Code ����code�õ�����id
        String loginCorpdistinct = ClientEnvironment.getInstance().getLoginUserDeptCorpdistinct();
        dr.getBillCellPanel().setEditable(false);
        dr.getBillCellPanel().addBillCellHtmlHrefListener(this);
        JLabel info = new JLabel("������ֿɲ鿴�������");
        info.setOpaque(false);
        info.setForeground(Color.RED);
        dr.getPanel_btn().add(info);
        bq.addBillQuickActionListener(this);
        return dr;
    }

    public void actionPerformed(ActionEvent arg0) {
        final HashMap map_condition = bq.getQuickQueryConditionAsMap();
        if (map_condition == null) {
            return;
        }
        new SplashWindow(bq, new AbstractAction() {
            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent e) {
                try {
                    BillCellVO vo = getCellVO(map_condition);
                    if (vo != null && vo.getCellItemVOs() != null && vo.getCellItemVOs().length > 0) {
                        dr.getBillCellPanel().loadBillCellData(vo);
                    }
                    if (map_condition == null || map_condition.get("monthly") == null) {
                        dr.setReportExpName(exportFileName + "_ȫ��");
                    } else {
                        dr.setReportExpName(exportFileName + "_" + map_condition.get("monthly").toString());
                    }
                    dr.getBillCellPanel().setEditable(false);
                } catch (Exception ex) {
                    MessageBox.showException(bq, ex);
                }
            }
        });
    }

    public BillCellVO getCellVO(HashMap condition) {
        BillCellVO cell = new BillCellVO();
        if (condition != null && condition.containsKey("monthly")) {
            String monthly = " 1=1 ";
            if (condition.get("monthly") != null && !"".equals(condition.get("monthly").toString().trim())) {
                monthly = " b.monthly='" + condition.get("monthly").toString().trim() + "' ";
            }
            try {
                HashVO [] wgidVos=UIUtil.getHashVoArrayByDS(null,"select id from excel_tab_85 where G='"+ClientEnvironment.getCurrLoginUserVO().getCode()+"'");
                BillCellItemVO[][] items=null;
                int maxlength = 0;
                String[] allkeys=null;
                detailid_desc = new HashMap();
                for(int w=0;w<wgidVos.length;w++){
                    HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select d.*,b.monthly,b.name billname from sal_salarybill_detail d left join sal_salarybill b on d.salarybillid=b.id where d.userid='"+wgidVos[w].getStringValue("id")+"'  and " + monthly + " and d.viewname is not null  and d.factorid is not null and b.state='�ѿ���'  order by b.monthly,d.salarybillid, d.seq");
                    if (vos != null && vos.length > 0) {
                        LinkedHashMap<String, LinkedHashMap> map = new LinkedHashMap<String, LinkedHashMap>();
                        HashMap id_name = new HashMap();
                        HashMap salaryid_name = new HashMap();
                        int j1 = 0;
                        for (int i = 0; i < vos.length; i++) {
                            detailid_desc.put(vos[i].getStringValue("id"), vos[i].getStringValue("computedesc"));
                            id_name.put(vos[i].getStringValue("id"), vos[i].getStringValue("viewname").trim());
                            salaryid_name.put(vos[i].getStringValue("salarybillid"), "" + vos[i].getStringValue("billname") + "");
                            if (map.containsKey(vos[i].getStringValue("salarybillid"))) {
                                j1 = j1 + 1;
                                if (j1 > maxlength) {
                                    maxlength = j1;
                                }
                                ((LinkedHashMap) map.get(vos[i].getStringValue("salarybillid"))).put(vos[i].getStringValue("id"), vos[i].getStringValue("factorvalue"));
                            } else {
                                LinkedHashMap item = new LinkedHashMap();
                                item.put(vos[i].getStringValue("id"), vos[i].getStringValue("factorvalue"));
                                map.put(vos[i].getStringValue("salarybillid"), item);
                                if (j1 > maxlength) {
                                    maxlength = j1;
                                }
                                j1 = 1;
                            }
                        }
                        if(w==0){
                            items = new BillCellItemVO[wgidVos.length+2][maxlength];
                            allkeys = map.keySet().toArray(new String[0]);
                            for (int i = 0; i < map.size(); i++) {
                                LinkedHashMap column_calue = (LinkedHashMap) map.get(allkeys[i]);
                                String[] allcolumns = (String[]) column_calue.keySet().toArray(new String[0]);
                                int rowbg = 0;
                                for (int p = 0; p < maxlength; p++) {
                                    rowbg = i * 4;
                                    if (p < allcolumns.length) {
                                        items[rowbg][p] = new BillCellItemVO();
                                        items[rowbg][p].setSpan("1," + maxlength);
                                        items[rowbg][p].setCellkey(salaryid_name.get(allkeys[i]) + "");
                                        items[rowbg][p].setCellvalue(salaryid_name.get(allkeys[i]) + "");
                                        items[rowbg][p].setBackground("191,213,255");
                                        items[rowbg][p].setFonttype("������");
                                        items[rowbg][p].setFontsize("12");
                                        items[rowbg][p].setFontstyle("1");
                                        items[rowbg + 1][p] = new BillCellItemVO();
                                        items[rowbg + 1][p].setCellkey(id_name.get(allcolumns[p]) + "");
                                        items[rowbg + 1][p].setCellvalue(id_name.get(allcolumns[p]) + "");
                                        items[rowbg + 1][p].setBackground("191,213,255");
                                        items[rowbg + 1][p].setFonttype("������");
                                        items[rowbg + 1][p].setFontsize("12");
                                        items[rowbg + 1][p].setFontstyle("1");
                                        items[rowbg + 1][p].setBackground("191,213,255");
                                        items[rowbg + 2][p] = new BillCellItemVO();
                                        items[rowbg + 2][p].setCellkey(allcolumns[p]);
                                        items[rowbg + 2][p].setCellvalue(column_calue.get(allcolumns[p]) + "");
                                        items[rowbg + 2][p].setBackground("191,213,255");
                                        items[rowbg + 2][p].setIshtmlhref("Y");
                                        items[rowbg + 2][p].setCelldesc("����鿴������ϸ");
                                        items[rowbg + 2][p].setCellhelp("����鿴������ϸ");
                                        if (i < map.size() - 1) {
                                            items[rowbg + 3][p] = new BillCellItemVO();
                                            items[rowbg + 3][p].setSpan("1," + allcolumns.length);
                                            items[rowbg + 2][p].setCellkey(allcolumns[p]);
                                            items[rowbg + 3][p].setCellvalue(column_calue.get(allcolumns[p]) + "");
                                            items[rowbg + 3][p].setBackground("191,213,255");
                                            items[rowbg + 3][p].setIshtmlhref("Y");
                                            items[rowbg + 3][p].setCelldesc("����鿴������ϸ");
                                            items[rowbg + 3][p].setCellhelp("����鿴������ϸ");
                                        }
                                    } else {
                                        items[rowbg][p] = new BillCellItemVO();
                                        items[rowbg][p].setBackground("191,213,255");
                                        items[rowbg + 1][p] = new BillCellItemVO();
                                        items[rowbg + 1][p].setBackground("191,213,255");
                                        items[rowbg + 2][p] = new BillCellItemVO();
                                        items[rowbg + 2][p].setBackground("191,213,255");
                                        if (i < map.size() - 1) {
                                            items[rowbg + 3][p] = new BillCellItemVO();
                                            items[rowbg + 3][p].setSpan("1," + allcolumns.length);
                                            items[rowbg + 2][p].setCellkey(allcolumns[p]);
                                            items[rowbg + 3][p].setCellvalue(column_calue.get(allcolumns[p]) + "");
                                            items[rowbg + 3][p].setBackground("191,213,255");
                                            items[rowbg + 3][p].setIshtmlhref("Y");
                                            items[rowbg + 3][p].setCelldesc("����鿴������ϸ");
                                            items[rowbg + 3][p].setCellhelp("����鿴������ϸ");
                                        }
                                    }
                                }
                            }
                        }else{
                            for (int i = 0; i < map.size(); i++) {
                                LinkedHashMap column_calue = (LinkedHashMap) map.get(allkeys[i]);
                                String[] allcolumns = (String[]) column_calue.keySet().toArray(new String[0]);
                                int rowbg = 0;
                                for (int p = 0; p < maxlength; p++) {
                                    items[w + 2][p] = new BillCellItemVO();
                                    items[w + 2][p].setCellkey(allcolumns[p]);
                                    items[w + 2][p].setCellvalue(column_calue.get(allcolumns[p]) + "");
                                    items[w + 2][p].setBackground("191,213,255");
                                    items[w + 2][p].setIshtmlhref("Y");
                                    items[w + 2][p].setCelldesc("����鿴������ϸ");
                                    items[w + 2][p].setCellhelp("����鿴������ϸ");

                                }
                            }

                        }
                    } else {
                        cell.setRowlength(1);
                        cell.setCollength(1);
                        items = new BillCellItemVO[1][1];
                        items[0][0] = new BillCellItemVO();
                        items[0][0].setCellvalue("δ��ѯ����Ӧ��Ϣ");
                        items[0][0].setBackground("191,213,255");
                        items[0][0].setFonttype("������");
                        items[0][0].setFontsize("12");
                        items[0][0].setFontstyle("1");
                        items[0][0].setColwidth("300");
                        cell.setCellItemVOs(items);
                    }
                }
                // ���б�񳤶ȵ���
                FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(LookAndFeel.font);
                int li_allowMaxColWidth = 175;
                for (int j = 0; j < items[0].length; j++) {
                    int li_maxwidth = 0;
                    String str_cellValue = null;
                    for (int i = 0; i < items.length; i++) {
                        str_cellValue = items[i][j].getCellvalue();
                        if (str_cellValue == null || "null".equals(str_cellValue)) {
                            items[i][j].setCellvalue("");
                        }
                        int column = 1;
                        if (items[i][j] != null && items[i][j].getSpan() != null) {
                            column = Integer.parseInt(items[i][j].getSpan().split(",")[1]);
                        }
                        if (str_cellValue != null && !str_cellValue.trim().equals("") && column <= 1) {
                            int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue);
                            if (li_width > li_maxwidth) {
                                li_maxwidth = li_width;
                            }
                        }
                    }
                    li_maxwidth = li_maxwidth + 13;
                    if (li_maxwidth > li_allowMaxColWidth) {
                        li_maxwidth = li_allowMaxColWidth;
                    }
                    for (int i = 0; i < items.length; i++) {
                        str_cellValue = items[i][j].getCellvalue();
                        if (str_cellValue != null && !str_cellValue.trim().equals("")) {
                            //								int li_width = SwingUtilities.computeStringWidth(fm, str_cellValue);
                            //								if (li_width > 0) {
                            //									int li_length = (li_width / li_maxwidth) + 1;
                            //									int li_itemRowHeight = li_length * 17 + 5;
                            //									if (i == 1) {
                            //										if (li_itemRowHeight > 35) {
                            //											items[i][j].setRowheight("" + li_itemRowHeight);
                            //										} else {
                            //											items[i][j].setRowheight("35");
                            //										}
                            //									} else {
                            //										items[i][j].setRowheight("" + li_itemRowHeight);
                            //									}
                            //									items[i][j].setColwidth("" + li_maxwidth);
                            //								}
                            items[i][j].setColwidth("" + li_maxwidth);
                        }
                    }
                }
                cell.setCellItemVOs(items);
                cell.setRowlength(items.length);
                cell.setCollength(maxlength);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cell;
    }

    public void onBillCellHtmlHrefClicked(BillCellHtmlHrefEvent event) {
        onWatchComputeDesc(event.getCellItemKey());
    }

    public void onWatchComputeDesc(String key) {
        if (detailid_desc.containsKey(key)) {
            MessageBox.show(this, detailid_desc.get(key) == null ? "��" : detailid_desc.get(key));
        }
    }
}
