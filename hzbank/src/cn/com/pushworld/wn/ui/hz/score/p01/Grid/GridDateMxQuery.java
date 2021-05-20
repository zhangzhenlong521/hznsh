package cn.com.pushworld.wn.ui.hz.score.p01.Grid;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.*;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;
import cn.com.infostrategy.ui.report.cellcompent.ExcelUtil;
import cn.com.jsc.ui.DateUIUtil;
import cn.com.jsc.ui.TitleNewJLabel;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * zzl
 * ֻ��һ������
 */
public class GridDateMxQuery extends AbstractWorkPanel implements
        ActionListener, BillListHtmlHrefListener {
    private String code = "EXCEL_TAB_85_CODE";
    private BillListPanel listPanel = null;
    private WLTButton btn_add, btn_update, btn_delete, btn_log,btn_query,btn_card;// ���� �޸� ɾ��
    private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
            .getCode();
    private final String USERNAME = ClientEnvironment.getCurrSessionVO()
            .getLoginUserName();
    private final String DEPTCODE = ClientEnvironment.getCurrLoginUserVO()
    		.getDeptcode();
    private BillListPanel list;
    private WLTButton btn_dr=new WLTButton("����");//zzl[2020-9-18] ��ӵ��빦��\
    private WLTButton btn_xg=new WLTButton("�޸�");//zzl[2020-9-18] ����޸Ĺ���\
    private WLTButton btn_qy=new WLTButton("Ǩ��");//zzl[2020-9-18] ���Ǩ�ƹ���\
    private WLTButton btn_dow=new WLTButton("Excelģ������");//zzl[2020-9-18] ���Excelģ�����ع���\
    private WLTButton btn_up=new WLTButton("�ϴ�Excel");//zzl[2020-9-18] ����ϴ�Excel����\
    private WLTButton btn_ckthan=new WLTButton("���ȶ�");//zzl[2020-9-18] ����ϴ�Excel����\
    private WLTButton btn_dkthan=new WLTButton("������ϸ");//zzl[2020-9-18] ����ϴ�Excel����\
    private ExcelUtil excelUtil=new ExcelUtil();
    private Container _parent=null;
    private String selectDate = "";
    private String deptcode;
    private String tablename;
    private String bltablename;
    private String dgbltablename;
    private String jlMbCode;
    private Boolean flag=false;
    private Boolean isleader=false;//�ж��ǲ��ǹ���Ա
    private BillListPanel wglist=new BillListPanel("S_LOAN_KHXX_202001_CODE1");
    private BillListDialog dialog=null;
    private WLTSplitPane wltSplitPane=null;
    private Boolean wgfalg= TBUtil.getTBUtil().getSysOptionBooleanValue("����ſ��Ƿ������ӻ�",false);
    private Boolean  ymfalg= TBUtil.getTBUtil().getSysOptionBooleanValue("���������Ƿ�鿴����ĩ����",false);
    private StringBuffer sbsql=new StringBuffer();

    @Override
    public void initialize() {
        listPanel = new BillListPanel(code);
        btn_query=new WLTButton("��ѯ");
        btn_card=new WLTButton("���֤��ѯ");
        btn_card.addActionListener(this);
        btn_add = new WLTButton("����");
        btn_add.addActionListener(this);
        btn_update = new WLTButton("�޸�");
        btn_update.addActionListener(this);
        btn_delete = new WLTButton("ɾ��");
        btn_delete.addActionListener(this);
        btn_log = new WLTButton("��־�鿴");
        btn_log.addActionListener(this);
        HashVO[] vos=null;
        HashMap<String,String> roleMap=new HashMap<String, String>();
        String leadervo=null;
        try{
            vos= UIUtil.getHashVoArrayByDS(null,"select * from v_pub_user_post_1 where usercode='"+USERCODE+"'");
            roleMap=UIUtil.getHashMapBySQLByDS(null,"select ROLENAME,ROLENAME from v_pub_user_role_1 where usercode='"+USERCODE+"'");
            leadervo=UIUtil.getStringValueByDS(null, "select stationkind from v_sal_personinfo where code='"+USERCODE+"'");
        }catch (Exception e){

        }
        if(ClientEnvironment.isAdmin() || roleMap.get("��Чϵͳ����Ա")!=null){
            flag=true;
            listPanel.QueryDataByCondition("PARENTID='2'");//zzl[20201012]
            listPanel.addBatchBillListButton(new WLTButton[] {btn_add, btn_update,btn_card});
            listPanel.setDataFilterCustCondition("PARENTID='2'");
            sbsql.append("1=1");
        }else if(leadervo.contains("֧���г�")){
            flag=true;
            listPanel.QueryDataByCondition("PARENTID='2' and F='"+vos[0].getStringValue("DEPTCODE")+"'");//zzl[20201012]
            listPanel.addBatchBillListButton(new WLTButton[] {btn_card});
            listPanel.setDataFilterCustCondition("PARENTID='2' and F='"+vos[0].getStringValue("DEPTCODE")+"'");
            sbsql.append("deptcode='"+vos[0].getStringValue("DEPTCODE")+"'");
        }else{
            listPanel.QueryDataByCondition("PARENTID='2' and G='"+vos[0].getStringValue("USERCODE")+"'");//zzl[20201012]
            listPanel.addBatchBillListButton(new WLTButton[] {btn_card});
            listPanel.setDataFilterCustCondition("PARENTID='2' and G='"+vos[0].getStringValue("USERCODE")+"'");
            sbsql.append("deptcode='"+vos[0].getStringValue("DEPTCODE")+"'");
        }
        list = new BillListPanel("WN_WGINFOUPDATE_LOG_CODE");
        listPanel.repaintBillListButton();// ˢ�°�ť
        listPanel.addBillListHtmlHrefListener(this); // zzl[20201012]
        getTablename();
//        tablename="grid_data_20210514";
        WLTSplitPane wltSplitPane=new WLTSplitPane(WLTSplitPane.HORIZONTAL_SPLIT,listPanel,new DayAvgPanel().getJLabel());
        wltSplitPane.setDividerLocation(1000);
        wltSplitPane.setDividerSize(1);
        this.add(listPanel);
    }

    @Override
    public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
        final BillVO vo=listPanel.getSelectedBillVO();
        deptcode=vo.getStringValue("F");
        int width=0;
        String mbCode;
        jlMbCode=listPanel.getSelectedBillVO().getStringValue(_event.getItemkey());
        if(_event.getItemkey().equals("D")){
            if(jlMbCode.equals("�Թ����")){
                mbCode="HZ_DK_WGMX_CODE3";
                width=1000;
            }else{
                mbCode="HZ_DK_WGMX_CODE1";
                width=2000;
            }
            dialog=new BillListDialog(listPanel,"������Ϣ�鿴",mbCode,width,800);
            dialog.getBilllistPanel().getTempletVO().setTablename(tablename);
            dialog.getBilllistPanel().getTempletVO().setSavedtablename(tablename);
            dialog.getBilllistPanel().setRowNumberChecked(true);
            dialog.getBilllistPanel().queryDataByDS(null,"select * from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"'");
            dialog.getBilllistPanel().getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    StringBuffer sb=new StringBuffer();
                    String A=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A");
                    String G=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("G");
                    String num=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("num");
                    String dkye=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("dkye");
                    String ckye=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("ckye");
                    String dgck=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("dgck");
                    String H=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("H");
                    if(A==null || A.equals("") || A.equals(null) || A.equals(" ")){
                    }else{
                        sb.append(" and A='"+A+"'");
                    }
                    if(G==null || G.equals("") || G.equals(null) || G.equals(" ")){
                    }else{
                        sb.append(" and G='"+G+"'");
                    }
                    if(num==null || num.equals("") || num.equals(null) || num.equals(" ")){
                    }else{
                        sb.append(" and num='"+num+"'");
                    }
                    if(dkye==null || dkye.equals("") || dkye.equals(null) || dkye.equals(" ")){
                    }else{
                        String [] str=dkye.split(";");
                        sb.append(" and dkye>='"+str[0]+"' and dkye<='"+str[1]+"'");
                    }
                    if(ckye==null || ckye.equals("") || ckye.equals(null) || ckye.equals(" ")){
                    }else{
                        String [] str=ckye.split(";");
                        sb.append(" and ckye>='"+str[0]+"' and ckye<='"+str[1]+"'");
                    }
                    if(dgck==null || dgck.equals("") || dgck.equals(null) || dgck.equals(" ")){
                    }else{
                        String [] str=dgck.split(";");
                        sb.append(" and dgck>='"+str[0]+"' and dgck<='"+str[1]+"'");
                    }
                    if(H==null || H.equals("") || H.equals(null) || H.equals(" ")){
                    }else{
                        sb.append(" and H like'%"+H+"%'");
                    }
                    if(sb.toString()==null){
                        dialog.getBilllistPanel().queryDataByDS(null,"select * from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"'");
                    }else{
                        dialog.getBilllistPanel().queryDataByDS(null,"select * from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"' "+sb.toString()+"");
                    }
                }
            });
            dialog.getBilllistPanel().addBillListHtmlHrefListener(new BillListHtmlHrefListener(){
                @Override
                public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
                    BillVO vo=dialog.getBilllistPanel().getSelectedBillVO();
                    if(_event.getItemkey().equals("dkye")){
                        getDkDialog(dialog,vo);
                    }else if(_event.getItemkey().equals("ckye")){
                        getCkDialog(dialog,vo);
                    }else if(_event.getItemkey().equals("num")){
                        getJtDialog(dialog,vo);
                    }
                }
            });
            btn_dr.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    onImpData(dialog,vo,jlMbCode);
                }
            });
            btn_xg.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    updateDate(dialog,vo,jlMbCode);
                }
            });
            btn_qy.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    qyData(dialog,vo);
                }
            });
            btn_dow.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    onDoExcel(dialog);
                }
            });
            btn_ckthan.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    onCkThan(dialog,vo);
                }
            });
            btn_dkthan.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    onLoanThan(dialog,vo);
                }
            });
            btn_up.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    onUpExcel(dialog);
                    dialog.getBilllistPanel().refreshData();
                }
            });
        	dialog.getBilllistPanel().addBillListButton(btn_dr);
        	if(isleader){
            dialog.getBilllistPanel().addBillListButton(btn_xg);
        	}//fj20210519,�����г�Ҳ�����޸�������Ϣ��ֻ�ܹ���Ա��
            dialog.getBilllistPanel().addBillListButton(btn_ckthan);
            dialog.getBilllistPanel().addBillListButton(btn_dkthan);
             if(flag){
                dialog.getBilllistPanel().addBillListButton(btn_dow);
                dialog.getBilllistPanel().addBillListButton(btn_up);
                dialog.getBilllistPanel().addBillListButton(btn_qy);
            }//fj20210207  Ӧϯ��Ҫ�󣬲����г��ģ�������ťȫ����
            dialog.getBilllistPanel().repaintBillListButton();
            dialog.setBtn_confirmVisible(false);
            dialog.setVisible(true);
        }else if(_event.getItemkey().equals("QK")){
            StringBuilder sb=new StringBuilder();
            HashVO [] vos=new HashVO[4];
            try {
                sb.append(vo.getStringValue("C")+vo.getStringValue("D")+":");
                //zzl �ܿͻ���
                String zhs=UIUtil.getStringValueByDS(null,"select count(*) from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"'");
                sb.append("�������ܿͻ���Ϊ����"+zhs+"����");
//                sb.append(System.getProperty("line.separator"));
                //zzl ���ͻ�
                String ckhs=UIUtil.getStringValueByDS(null,"select count(*) from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"' and ckye>0");
//                sb.append("���д��ͻ�����"+ckhs+"����      ���������ͻ�����"+(Integer.parseInt(zhs)-Integer.parseInt(ckhs))+"����");
//                sb.append(System.getProperty("line.separator"));
                String ckye=UIUtil.getStringValueByDS(null,"select sum(ckye) ckye from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"' and ckye>0");
                vos[0]=new HashVO();
                vos[0].setAttributeValue("yikf","���д��ͻ���"+ckhs+"��");
                vos[0].setAttributeValue("dkf","���������ͻ���"+(Integer.parseInt(zhs)-Integer.parseInt(ckhs))+"��");
                vos[0].setAttributeValue("yetj","�����"+(ckye==null || ckye.equals("") || ckye.equals(null)?"0":ckye));
                // zzl ����ͻ�
                String dkhs=UIUtil.getStringValueByDS(null,"select count(*) from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"' and dkye>0");
//                sb.append("���д���ͻ�����"+dkhs+"����      ����������ͻ�����"+(Integer.parseInt(zhs)-Integer.parseInt(dkhs))+"����");
//                sb.append(System.getProperty("line.separator"));
                String dkye=UIUtil.getStringValueByDS(null,"select sum(dkye) dkye from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"' and dkye>0");
                vos[1]=new HashVO();
                vos[1].setAttributeValue("yikf","���д���ͻ���"+dkhs+"��");
                vos[1].setAttributeValue("dkf","����������ͻ���"+(Integer.parseInt(zhs)-Integer.parseInt(dkhs))+"��");
                vos[1].setAttributeValue("yetj","������"+(dkye==null || dkye.equals("") || dkye.equals(null)?"0":dkye));
                //zzl ��������
                String jdhs=UIUtil.getStringValueByDS(null,"select count(*) from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"' and jdxx='�ѽ���'");
//                sb.append("���н����ͻ�����"+jdhs+"����      �����������ͻ�����"+(Integer.parseInt(zhs)-Integer.parseInt(jdhs))+"����");
//                sb.append(System.getProperty("line.separator"));
                vos[2]=new HashVO();
                vos[2].setAttributeValue("yikf","���н����ͻ���"+jdhs+"��");
                vos[2].setAttributeValue("dkf","�����������ͻ���"+(Integer.parseInt(zhs)-Integer.parseInt(jdhs))+"��");
                //zzl ǭũ��
                String qnyhs=UIUtil.getStringValueByDS(null,"select count(*) from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"' and qny='��'");
//                sb.append("����ǭũ�ƿͻ�����"+qnyhs+"����      ������ǭũ�ƿͻ�����"+(Integer.parseInt(zhs)-Integer.parseInt(qnyhs))+"����");
//                sb.append(System.getProperty("line.separator"));
                vos[3]=new HashVO();
                vos[3].setAttributeValue("yikf","����ǭũ�ƿͻ���"+qnyhs+"��");
                vos[3].setAttributeValue("dkf","������ǭũ�ƿͻ���"+(Integer.parseInt(zhs)-Integer.parseInt(qnyhs))+"��");
                Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
                templetVO.setTempletname(sb.toString());
                String [] columns = new String[]{"yikf","dkf","yetj"};
                String [] columnNames=new String[]{"�ѿ����ܼ�","�������ܼ�","���ͳ��(Ԫ)"};
                templetVO.setRealViewColumns(columns);
                templetVO.setIsshowlistpagebar(false);
                templetVO.setIsshowlistopebar(false);
                templetVO.setListheaderisgroup(false);
                templetVO.setIslistpagebarwrap(false);
                templetVO.setIsshowlistquickquery(false);
                templetVO.setIscollapsequickquery(true);
                templetVO.setIslistautorowheight(true);
                Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
                for(int i=0;i<columns.length;i++){
                    templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                    templetItemVOs[i].setListisshowable(true);
                    templetItemVOs[i].setPub_Templet_1VO(templetVO);
                    templetItemVOs[i].setListwidth(200);
                    templetItemVOs[i].setItemtype("�ı���");
                    templetItemVOs[i].setListiseditable("4");
                    templetItemVOs[i].setItemkey(columns[i].toString());
                    templetItemVOs[i].setItemname(columnNames[i].toString());
                }
                templetVO.setItemVos(templetItemVOs);
                final BillListPanel list=new BillListPanel(templetVO);
                list.putValue(vos);
                list.addBillListSelectListener(new BillListSelectListener() {
                    @Override
                    public void onBillListSelectChanged(BillListSelectionEvent _event) {
                        BillVO vo=list.getSelectedBillVO();
                        String [] yikf=vo.getStringValue("yikf").split("��");
                        String [] dkf=vo.getStringValue("dkf").split("��");
                        String titie=null;
                        DefaultPieDataset data=new DefaultPieDataset();
                        data.setValue(yikf[0],Integer.parseInt(yikf[1].replace("��","")));
                        data.setValue(dkf[0],Integer.parseInt(dkf[1].replace("��","")));
                         if(yikf[0].contains("���")){
                             titie="���";
                         }else if(yikf[0].contains("����")){
                             titie="����";
                         }else if(yikf[0].contains("����")){
                             titie="����";
                         }else if(yikf[0].contains("ǭũ��")){
                             titie="ǭũ��";
                         }
                        wltSplitPane.removeAll();
                        wltSplitPane.add(list);
                        wltSplitPane.add(new PieChart(titie,data).getChartPanel());
                        wltSplitPane.setDividerLocation(200);
                        wltSplitPane.updateUI();
                    }
                });
                if(wgfalg){
                    wltSplitPane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,list,null);
                    wltSplitPane.setDividerLocation(200);
                    wltSplitPane.setDividerSize(1);
                }
                BillDialog dialog=new BillDialog(listPanel,700,(wltSplitPane==null?300:700));
                dialog.add(wltSplitPane==null?list:wltSplitPane);
                dialog.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if(_event.getItemkey().equals("blloan")){
            getViewBlLoan(listPanel,vo);
        }

    }

    /**
     * zzl
     * ���ȶ�
     */
    private void onCkThan(Dialog dialog,BillVO vo) {
        try{
            String J=vo.getStringValue("c");
            String K=vo.getStringValue("d");
            String deptcode=vo.getStringValue("f");
            HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select dy.a,dy.b,dy.g,dy.h,dy.j,dy.k,dy.ckye,case when sy.ckye is null then dy.ckye else dy.ckye-sy.ckye end jsyck\n" +
                    ",case when nc.ckye is null then dy.ckye else dy.ckye-nc.ckye end jncck from(\n" +
                    "select * from "+tablename+" where J='"+J+"' and K='"+K+"' and deptcode='"+deptcode+"' and ckye>0) dy\n" +
                    "left join (select g,ckye from GRID_DATA_"+DateUIUtil.getqytSMonth()+" where J='"+J+"' and K='"+K+"' and deptcode='"+deptcode+"' ) sy on upper(dy.g)=upper(sy.g)\n" +
                    "left join (select g,ckye from GRID_DATA_"+DateUIUtil.getqytYearMonth()+" where J='"+J+"' and K='"+K+"' and deptcode='"+deptcode+"' ) nc on upper(dy.g)=upper(nc.g)");
            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            templetVO.setTempletname("���ȶԲ鿴");
            String [] columns = new String[]{"a","b","g","h","j","k","ckye","jsyck","jncck"};
            String [] columnNames=new String[]{"�ͻ�����","�ͻ�����","֤������","������ַ","��-��","��������","������","������","�����"};
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                templetItemVOs[i].setListisshowable(true);
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(100);
                templetItemVOs[i].setItemtype("�ı���");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            BillListPanel list=new BillListPanel(templetVO);
            list.putValue(vos);
            BillListDialog dialog2=new BillListDialog(dialog,"���ȶԲ鿴",list,1500,800,true);
            dialog2.getBtn_confirm().setVisible(false);
            dialog2.setVisible(true);
        }catch (Exception e){

        }

    }
    /**
     * zzl
     * ������ϸ
     */
    private void onLoanThan(Dialog dialog,BillVO vo){
        try{
            HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select dk.* from(\n" +
                    "select case when wg.code='28330100-xd' then '28330100' else wg.code end code,xx.G sfz,wg.id id,wg.g g,wg.f from(select deptcode,G,j,k from hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx \n" +
                    "  left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg on \n" +
                    "  xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D where wg.c='"+vo.getStringValue("c")+"' and wg.d='"+vo.getStringValue("d")+"' and wg.f='"+vo.getStringValue("f")+"') wg\n" +
                    "  left join\n" +
                    "  (select * from(select XD_COL1,XD_COL2,XD_COL4,XD_COL5,XD_COL6,XD_COL7,XD_COL16,\n" +
                    "case when XD_COL22='01' then '����' when XD_COL22='02' then '��ע' when XD_COL22='03' then '����' \n" +
                    "  when XD_COL22='04' then '�μ�' when XD_COL22='05' then '��ʧ' else XD_COL22 end XD_COL22, \n" +
                    "    case when XD_COL86='x_wd' or XD_COL86='x_wj' then '��' else '��' end XD_COL86,XD_COL85 from\n" +
                    "       hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" group by XD_COL1) and\n" +
                    "        XD_COL4<'"+getKHDQMonth()+" 00:00:00')\n" +
                    "        union all\n" +
                    "(select dk.CONT_NO,xx.CUS_NAME,dk.LOAN_START_DATE,dk.LOAN_END_DATE,dk.LOAN_AMOUNT,dk.LOAN_BALANCE,xx.CERT_CODE xd_col16,\n" +
                    "case when dk.CLA='01' then '����' when dk.CLA='02' then '��ע' when dk.CLA='03' then '����' \n" +
                    "  when dk.CLA='04' then '�μ�' when dk.CLA='05' then '��ʧ' else dk.CLA end CLA,'��','30100' xd_col85 from\n" +
                    "  (select * from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" where CUS_ID||biz_dt in\n" +
                    "  (select CUS_ID||max(biz_dt) from hzbank.S_CMIS_ACC_LOAN_"+getQYDayMonth()+" group by CUS_ID)) dk left join \n" +
                    "  hzbank.S_CMIS_CUS_BASE_"+getQYDayMonth()+" xx on dk.CUS_ID=xx.CUS_ID where dk.LOAN_BALANCE>0)) dk on wg.code='283'||dk.XD_COL85 and upper(wg.sfz)=upper(dk.XD_COL16) where dk.XD_COL7 is not null\n");

            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            templetVO.setTempletname("������ϸ�鿴");
            String [] columns = new String[]{"XD_COL1","XD_COL2","XD_COL4","XD_COL5","XD_COL6","XD_COL7","XD_COL16","XD_COL22","XD_COL86","XD_COL85"};
            String [] columnNames=new String[]{"�����","�ͻ�����","��������","��������","������","��Ƿ���","֤������","�弶����","E��","�������"};
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                templetItemVOs[i].setListisshowable(true);
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(150);
                templetItemVOs[i].setItemtype("�ı���");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            BillListPanel list = new BillListPanel(templetVO);
            list.putValue(vos);
            BillListDialog billListDialog=new BillListDialog(dialog,"������ϸ��ѯ",list,1600,600,true);
            billListDialog.setBtn_confirmVisible(false);
            billListDialog.setVisible(true);
        }catch (Exception e){

        }
    }

    /**
     * zzl ��������չʾ
     * "+(ymfalg==true?getQYDaySMonth(2):getQYDaySMonth(1))+"
     */
    private void getViewBlLoan(BillListPanel listPanel,BillVO vo) {
        try{
            HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select bl.*,wg.c xzname,wg.d wgname from(\n" +
                    "select * from(\n" +
                    "select B,case when BH='30100' then '28330100-xd' else '283'||BH end code,C,bi,D,E,F,to_number(replace(j,',','')) dkye,to_number(replace(K,',','')) jqje,AP name,replace(Q,',','') yqday from "+bltablename+" where replace(Q,',','')>60)\n" +
                    "union all\n" +
                    "select * from (\n" +
                    "select C B,'28330100-xd' code,B C,'�Ŵ���' bi,'' D,DG E,DH F,to_number(replace(DE,',','')) dkye,to_number(replace(DF,',','')) jqje,E name,EA yqday from "+dgbltablename+" where replace(EA,',','')>60)\n" +
                    ") bl\n" +
                    "left join(\n" +
                    "select wg.code,xx.G name,wg.id,wg.g,wg.c,wg.d,wg.a,xx.deptcode from(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx \n" +
                    "left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg \n" +
                    "on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(bl.name) = UPPER(wg.name) and bl.code=wg.code \n" +
                    "where wg.deptcode='"+vo.getStringValue("f")+"' and wg.c='"+vo.getStringValue("c")+"' and wg.d='"+vo.getStringValue("d")+"'");
            HashVO [] vos2=UIUtil.getHashVoArrayByDS(null,"select bl.*,wg.c xzname,wg.d wgname from(\n" +
                    "select * from(\n" +
                    "select B,case when BH='30100' then '28330100-xd' else '283'||BH end code,C,bi,D,E,F,to_number(replace(j,',','')) dkye,to_number(replace(K,',','')) jqje,AP name,replace(Q,',','') yqday from hzdb.s_loan_dk_"+(ymfalg==true?getQYDaySMonth(2):getQYDaySMonth(1))+" where replace(Q,',','')>60)\n" +
                    "union all\n" +
                    "select * from (\n" +
                    "select C B,'28330100-xd' code,B C,'�Ŵ���' bi,'' D,DG E,DH F,to_number(replace(DE,',','')) dkye,to_number(replace(DF,',','')) jqje,E name,EA yqday from hzdb.s_loan_dk_dg_"+(ymfalg==true?getQYDaySMonth(2):getQYDaySMonth(1))+" where replace(EA,',','')>60)\n" +
                    ") bl\n" +
                    "left join(\n" +
                    "select wg.code,xx.G name,wg.id,wg.g,wg.c,wg.d,wg.a,xx.deptcode from(select deptcode,G,j,k from  hzdb.s_loan_khxx_202001 group by deptcode,G,j,k) xx \n" +
                    "left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg \n" +
                    "on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg on UPPER(bl.name) = UPPER(wg.name) and bl.code=wg.code \n" +
                    "where wg.deptcode='"+vo.getStringValue("f")+"' and wg.c='"+vo.getStringValue("c")+"' and wg.d='"+vo.getStringValue("d")+"'");
            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            String [] columns = new String[]{"b","code","c","bi","d","e","f","dkye","jqje","name","yqday","xzname","wgname"};
            String [] columnNames=new String[]{"�����","������","�ͻ�����","��������","�ֻ�����","��������","��������","������","��Ƿ���","֤������","�弶������������","��-��","��������"};
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                templetItemVOs[i].setListisshowable(true);
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(100);
                templetItemVOs[i].setItemtype("�ı���");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            templetVO.setTempletname("��ǰ��������");
            BillListPanel list=new BillListPanel(templetVO);
            list.putValue(vos);
            templetVO.setTempletname("���²�������");
            BillListPanel list2=new BillListPanel(templetVO);
            list2.putValue(vos2);
            WLTSplitPane titlepane=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,list,list2);
            titlepane.setDividerLocation(400);
            titlepane.setDividerSize(1);
            BillDialog dialog=new BillDialog(listPanel,"����������ϸ�鿴",1500,1000);
            dialog.add(titlepane);
            dialog.setVisible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * zzl
     * excl ģ������
     */
    private void onDoExcel(Dialog dialog) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("��ѡ������·��");
        chooser.setApproveButtonText("ѡ��");
        FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel ������", "xls", "xlsx");
        chooser.setSelectedFile(new File("����ͻ���ϸ����ģ��.xls"));
        chooser.setFileFilter(filter);
        int flag = chooser.showOpenDialog(this);
        if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
            return;
        }
        final String str_path = chooser.getSelectedFile().getAbsolutePath();
        List list=new ArrayList();
        Pub_Templet_1_ItemVO itemVos[] =wglist.getTempletVO().getItemVos();
        for(int i=0;i<itemVos.length;i++) {
            if (itemVos[i].getListisshowable()) {
                list.add(itemVos[i].getItemname());
            }
        }
        String [][] data=new String[1][list.size()];
        for(int i=0;i<list.size();i++){
            data[0][i]=list.get(i).toString();
        }
        try{
            excelUtil.setDataToExcelFile(data,str_path);
            MessageBox.show(dialog,"�����ɹ�");
        }catch (Exception e){
            MessageBox.show(dialog,"����ʧ��");
            e.printStackTrace();
        }
    }

    /**
     * zzl
     * �ϴ�ģ�� ���鲢�ҵ���
     */
    private void onUpExcel(final Dialog dialog){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("��ѡ���ϴ��ļ�");
        chooser.setApproveButtonText("ѡ��");
        FileFilter filter = new FileNameExtensionFilter("Microsoft Office Excel ������", "xls", "xlsx");
        chooser.setFileFilter(filter);
        int flag = chooser.showOpenDialog(this);
        if (flag != JFileChooser.APPROVE_OPTION || chooser.getSelectedFile() == null) {
            return;
        }
        final String str_path = chooser.getSelectedFile().getAbsolutePath();
        new SplashWindow(dialog, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String [][] data=excelUtil.getExcelFileData(str_path);
                LinkedHashMap map=new LinkedHashMap();
                Pub_Templet_1_ItemVO itemVos[] =wglist.getTempletVO().getItemVos();
                for(int i=0;i<itemVos.length;i++) {
                    if (itemVos[i].getListisshowable()) {
                        map.put(itemVos[i].getItemname(),itemVos[i].getItemkey());
                    }
                }
                if(data[0].length!=map.size()){
                    MessageBox.show(dialog,"ϵͳ��⵽�ļ��е�����ģ���е��в�һ�£���ʹ������ͻ���ϸģ����е���");
                    return;
                }
                BillVO vo=listPanel.getSelectedBillVO();
                String xName=vo.getStringValue("C");//����
                String wgName=vo.getStringValue("D");//��������
                String deptcode=vo.getStringValue("F");//������
                List updateList=new ArrayList();
                UpdateSQLBuilder wgUpdate=new UpdateSQLBuilder(tablename);
                UpdateSQLBuilder khxxUpdate=new UpdateSQLBuilder("S_LOAN_KHXX_202001");
                //��ʼ��������
                InsertSQLBuilder wgInsert=new InsertSQLBuilder(tablename);
                InsertSQLBuilder khxxInsert=new InsertSQLBuilder("S_LOAN_KHXX_202001");
                try{
                    HashMap idMap=UIUtil.getHashMapBySQLByDS(null,"select UPPER(G),deptcode from "+tablename+" where deptcode='"+deptcode+"'");//zzl �����ǰ�������������֤
                    List <List>listRow=new ArrayList();//��
                    StringBuffer sb=new StringBuffer();
                    for(int i=0;i<data.length;i++){
                        if(i==0){
                            List listCol=new ArrayList();//lie
                            for(int k=0;k<data[i].length;k++){
                                listCol.add(data[i][k]);
                            }
                            listRow.add(listCol);
                        }else{
                            for(int j=0;j<data[i].length;j++){
                                if(data[0][j].equals("���֤��")){
                                    if(idMap.get(data[i][j].toUpperCase())==null){
                                        List listCol=new ArrayList();//lie
                                        for(int k=0;k<data[i].length;k++){
                                            System.out.println(">>>>>>>>>>>>"+data[i].length);
                                            if(data[0][k].equals("��-��")){
                                                listCol.add(xName);
                                            }else if(data[0][k].equals("��������")){
                                                listCol.add(wgName);
                                            }else if(data[0][k].equals("��������")){
                                                listCol.add(deptcode);
                                            }else{
                                                listCol.add(data[i][k]);
                                            }
                                        }
                                        listRow.add(listCol);
                                    }else{
//                                        HashVO [] vos=UIUtil.getHashVoArrayByDS(null,
//                                                "select * from "+tablename+" where deptcode='"+deptcode+"' and G='"+data[i][j]+"'");
//                                        sb.append("�ͻ����֤Ϊ["+data[i][j]+"]�Ѿ����ڱ�������"+vos[0].getStringValue("J")+"-"+vos[0].getStringValue("K")+"�����ڣ������ظ�����"+System.getProperty("line.separator"));
                                        wgUpdate.setWhereCondition("deptcode='"+deptcode+"' and G='"+data[i][j]+"'");
                                        wgUpdate.putFieldValue("J",xName);
                                        wgUpdate.putFieldValue("K",wgName);
                                        khxxUpdate.setWhereCondition("deptcode='"+deptcode+"' and G='"+data[i][j]+"'");
                                        khxxUpdate.putFieldValue("J",xName);
                                        khxxUpdate.putFieldValue("K",wgName);
                                        if(data[i][0]==null || data[i][0].equals(null) || data[i][0].equals("")){
                                        }else{
                                            wgUpdate.putFieldValue("A",data[i][0]);
                                            khxxUpdate.putFieldValue("A",data[i][0]);
                                        }
                                        updateList.add(wgUpdate.getSQL());
                                        updateList.add(khxxUpdate.getSQL());
                                    }
                                }

                            }
                        }
                    }
                    List sqlList=new ArrayList();
                    if(listRow.size()>0){
                        for(int i=1;i<listRow.size();i++){
                            List col=listRow.get(i);
                            for(int j=0;j<col.size();j++){
                                if(map.get(listRow.get(0).get(j).toString())==null || col.get(j)==null){

                                }else{
                                    wgInsert.putFieldValue(map.get(listRow.get(0).get(j).toString()).toString(),col.get(j).toString());
                                    khxxInsert.putFieldValue(map.get(listRow.get(0).get(j).toString()).toString(),col.get(j).toString());
                                }
                           }
                            sqlList.add(wgInsert.getSQL());
                            sqlList.add(khxxInsert.getSQL());
                        }
                    }
                    UIUtil.executeBatchByDS(null,sqlList);
                    sb.append("���ι�����"+sqlList.size()/2+"������"+System.getProperty("line.separator"));
                    UIUtil.executeBatchByDS(null,updateList);
                    sb.append("���ι�Ǩ��"+updateList.size()/2+"������"+System.getProperty("line.separator"));
                    MessageBox.show(dialog,sb.toString());
                }catch (Exception a){
                    System.out.println(">>>>>>>>"+wgInsert.getSQL());
                    MessageBox.show(dialog,"����ʧ�ܣ�����ϵ����Ա");
                    a.printStackTrace();
                }
            }

        });

    }

    /**
     * zzl ���Ǩ�ƹ���
     */
    private void qyData(final BillListDialog dialog, final BillVO vo){
        new SplashWindow(dialog, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BillListDialog cardDialog=new BillListDialog(dialog,"����ͻ�Ǩ��","EXCEL_TAB_85_CODE_3");
                cardDialog.getBilllistPanel().QueryData("select c,d,G from hzdb.EXCEL_TAB_85 where 1=1  and (PARENTID='2')  and f='"+vo.getStringValue("F")+"' and C||D<>'"+vo.getStringValue("C")+vo.getStringValue("D")+"'");
                BillVO [] wgVos=dialog.getBilllistPanel().getCheckedBillVOs();
                if(wgVos.length<=0){
                    MessageBox.show(cardDialog,"�빴ѡһ�����ݽ��в���");
                    return;
                }
                cardDialog.setVisible(true);
                if(cardDialog.getCloseType()==1){
                    BillVO billVO=cardDialog.getBilllistPanel().getSelectedBillVO();
                    if(billVO==null){
                        MessageBox.show(cardDialog,"��ѡ��һ�����ݽ��в���");
                        return;
                    }
                    List list=new ArrayList();
                    UpdateSQLBuilder wgUpdate=new UpdateSQLBuilder(tablename);
                    UpdateSQLBuilder khxxUpdate=new UpdateSQLBuilder("s_loan_khxx_202001");
                    for(int i=0;i<wgVos.length;i++){
                        wgUpdate.setWhereCondition("G='"+wgVos[i].getStringValue("G")+"' and deptcode='"+wgVos[i].getStringValue("deptcode")+"'");
                        khxxUpdate.setWhereCondition("G='"+wgVos[i].getStringValue("G")+"' and deptcode='"+wgVos[i].getStringValue("deptcode")+"'");
                        wgUpdate.putFieldValue("J",billVO.getStringValue("C"));
                        khxxUpdate.putFieldValue("J",billVO.getStringValue("C"));
                        wgUpdate.putFieldValue("K",billVO.getStringValue("D"));
                        khxxUpdate.putFieldValue("K",billVO.getStringValue("D"));
                        list.add(wgUpdate.getSQL());
                        list.add(khxxUpdate.getSQL());
                    }
                    try {
                        UIUtil.executeBatchByDS(null,list);
                        MessageBox.show(cardDialog,"Ǩ�Ƴɹ�");
                        dialog.getBilllistPanel().repaint();
                        dialog.getBilllistPanel().refreshData();
                    } catch (Exception a) {
                        MessageBox.show(cardDialog,"Ǩ��ʧ��");
                        a.printStackTrace();
                    }
                }

            }
        });
     }
    /**
     * zzl ����޸Ĺ���
     * @param dialog
     * @param vo
     */
    private void updateDate(final BillListDialog dialog, BillVO vo,String code) {
        String mbstr;
        if(code.equals("�Թ����")){
            mbstr="S_LOAN_KHXX_202001_CODE2";
        }else{
            mbstr="S_LOAN_KHXX_202001_CODE1";
        }
        final BillCardDialog cardDialog=new BillCardDialog(dialog,"������Ϣ�鿴",mbstr,600,400);
        if(dialog.getBilllistPanel().getSelectedBillVO()==null){
            MessageBox.show(dialog,"��ѡ��һ�������޸�");
            return;
        }
        cardDialog.getBillcardPanel().setBillVO(dialog.getBilllistPanel().getSelectedBillVO());
        cardDialog.setSaveBtnVisiable(false);
        cardDialog.getBtn_confirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String [] keys=cardDialog.getBillcardPanel().getTempletVO().getItemKeys();
                UpdateSQLBuilder update= new UpdateSQLBuilder(cardDialog.getBillcardPanel().getTempletVO().getTablename());
                UpdateSQLBuilder newupdate= new UpdateSQLBuilder(tablename);
                StringBuilder sb=new StringBuilder();
                for(int i=0;i<keys.length;i++){
                    update.putFieldValue(keys[i],cardDialog.getBillcardPanel().getBillVO().getStringValue(keys[i]));
                    newupdate.putFieldValue(keys[i],cardDialog.getBillcardPanel().getBillVO().getStringValue(keys[i]));
                }
                sb.append("G='"+dialog.getBilllistPanel().getSelectedBillVO().getStringValue("G")+"'");
                sb.append(" and deptcode='"+dialog.getBilllistPanel().getSelectedBillVO().getStringValue("deptcode")+"'");
                update.setWhereCondition(sb.toString());
                newupdate.setWhereCondition(sb.toString());
                try {
                    UIUtil.executeUpdateByDS(null,update.getSQL());
                    UIUtil.executeUpdateByDS(null,newupdate.getSQL());
                    MessageBox.show(dialog,"�������");
                    dialog.getBilllistPanel().refreshCurrSelectedRow();
                    dialog.getBilllistPanel().repaint();
                    cardDialog.dispose();
                } catch (Exception e) {
                    MessageBox.show(dialog,"����ʧ��");
                    cardDialog.dispose();
                    e.printStackTrace();
                }
            }
        });
        cardDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btn_add) {// ������ť
            final BillCardDialog dialog=new BillCardDialog(listPanel,"����","EXCEL_TAB_85_EDIT_CODE",900,300);
            dialog.getBillcardPanel().setEditable("PARENTID",false);
            dialog.getBillcardPanel().setRealValueAt("PARENTID","2");
            dialog.getBillcardPanel().setRealValueAt("QK","����ſ�");
            dialog.getBillcardPanel().setEditable("QK",false);
            dialog.getBillcardPanel().setRealValueAt("blloan","��������");
            dialog.getBillcardPanel().setEditable("blloan",false);
            Pub_Templet_1VO itemVO=dialog.getBillcardPanel().getTempletVO();
            final Pub_Templet_1_ItemVO [] itemVOS=itemVO.getItemVos();
            dialog.getBtn_confirm().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                         String deptcode = dialog.getBillcardPanel().getBillVO().getStringValue("F");
                         String wgname = dialog.getBillcardPanel().getBillVO().getStringValue("D");
                        HashVO[] hashvo = UIUtil.getHashVoArrayByDS(null, "select * from EXCEL_TAB_85 where F='"+deptcode+"' and D='"+wgname+"'");
                        if(hashvo.length>0){
                            MessageBox.show(dialog,"����ǰ�������������"+wgname+"�������ڵĻ���"+deptcode+"�Ѿ����ڣ���������������");
                            return;
                        }else{
                            StringBuffer sb=new StringBuffer();
                            for(int i=0;i<itemVOS.length;i++){
                               Boolean falg= itemVOS[i].getIsmustinput();
                               if(falg){
                                   String code=dialog.getBillcardPanel().getRealValueAt(itemVOS[i].getItemkey());
                                   if(code==null || code.equals("") || code.equals(null)){
                                       sb.append(itemVOS[i].getItemname()+"������Ϊ��"+System.getProperty("line.separator"));
                                   }
                               }
                            }
                            if(sb.length()>0){
                                MessageBox.show(dialog,sb.toString());
                                return;
                            }else{
                                dialog.getBillcardPanel().updateData();
                                listPanel.addRow(dialog.getBillcardPanel().getBillVO());
                                dialog.dispose();
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            });
            dialog.setSaveBtnVisiable(false);
            dialog.setVisible(true);
        }else if(actionEvent.getSource() == btn_update){
            final BillVO vo=listPanel.getSelectedBillVO();
            if (vo == null) {
                MessageBox.show(this, "��ѡ��һ�����ݽ����޸�");
                return;
            }
            BillCardPanel cardPanel = new BillCardPanel(
                    "EXCEL_TAB_85_EDIT_CODE2");
            cardPanel.setBillVO(vo);
            cardPanel.setRealValueAt("PARENTID","2");
            final BillCardDialog dialog = new BillCardDialog(listPanel, "�޸�",
                    cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);// �޸�����
            dialog.setSaveBtnVisiable(false);
            dialog.getBtn_confirm().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        dialog.getBillcardPanel().updateData();
                        listPanel.refreshCurrSelectedRow();
                        dialog.dispose();
                        String newxz=dialog.getBillcardPanel().getRealValueAt("c");
                        String newWg=dialog.getBillcardPanel().getRealValueAt("d");
                        UIUtil.executeUpdateByDS(null,"update "+tablename+" set J='"+newxz+"',K='"+newWg+"' " +
                                "where J='"+vo.getStringValue("c")+"' and K='"+vo.getStringValue("d")+"" +
                                "' and deptcode='"+vo.getStringValue("f")+"'");
                        UIUtil.executeUpdateByDS(null,"update hzdb.s_loan_khxx_202001 set J='"+newxz+"',K='"+newWg+"'" +
                                "where J='"+vo.getStringValue("c")+"' and K='"+vo.getStringValue("d")+"' and deptcode='"+vo.getStringValue("f")+"'");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            dialog.setVisible(true);
        }else if(actionEvent.getSource() == btn_card){
            final BillListDialog billListDialog=new BillListDialog(listPanel,"���֤��ѯ","HZ_DK_WGMX_CODE4",2000,800);
            billListDialog.getBilllistPanel().getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try{
                        String card=billListDialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("G");
                        HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select * from "+tablename+" where "+sbsql.toString()+" and upper(G)=upper('"+card+"')");
                        if(vos.length==0){
                            MessageBox.show(billListDialog,"û�д�֤����������ݡ�"+card+"��");
                            return;
                        }
                        billListDialog.getBilllistPanel().queryDataByDS(null,"select * from "+tablename+" where "+sbsql.toString()+" and upper(G)=upper('"+card+"')");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                   }
            });
            billListDialog.getBilllistPanel().addBillListHtmlHrefListener(new BillListHtmlHrefListener() {
                @Override
                public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
                    BillVO vo = dialog.getBilllistPanel().getSelectedBillVO();
                    if (_event.getItemkey().equals("dkye")) {
                        getDkDialog(dialog, vo);
                    } else if (_event.getItemkey().equals("ckye")) {
                        getCkDialog(dialog, vo);
                    } else if (_event.getItemkey().equals("num")) {
                        getJtDialog(dialog, vo);
                    }
                }
            });
            billListDialog.setBtn_confirmVisible(false);
            billListDialog.getBtn_confirm().setVisible(false);
            billListDialog.setVisible(true);

        }


    }
    /**
     * zzl ����������ϸ�鿴
     * @param dialog
     * @param vo
     */
    public void getDkDialog(final Dialog dialog, final BillVO vo){
        Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
        templetVO.setTempletname("������ϸ�鿴");
        String [] columns = new String[]{"XD_COL1","XD_COL2","XD_COL4","XD_COL5","XD_COL6","XD_COL7","XD_COL16","XD_COL22","XD_COL86","XD_COL85"};
        String [] columnNames=new String[]{"�����","�ͻ�����","��������","��������","������","��Ƿ���","֤������","�弶����","E��","�������"};
        templetVO.setRealViewColumns(columns);
        templetVO.setIsshowlistpagebar(false);
        templetVO.setIsshowlistopebar(false);
        templetVO.setListheaderisgroup(false);
        templetVO.setIslistpagebarwrap(false);
        templetVO.setIsshowlistquickquery(false);
        templetVO.setIscollapsequickquery(true);
        templetVO.setIslistautorowheight(true);
        Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
        for(int i=0;i<columns.length;i++){
            templetItemVOs[i]=new Pub_Templet_1_ItemVO();
            templetItemVOs[i].setListisshowable(true);
            templetItemVOs[i].setPub_Templet_1VO(templetVO);
            templetItemVOs[i].setListwidth(150);
            templetItemVOs[i].setItemtype("�ı���");
            templetItemVOs[i].setListiseditable("4");
            templetItemVOs[i].setItemkey(columns[i].toString());
            templetItemVOs[i].setItemname(columnNames[i].toString());
        }
        templetVO.setItemVos(templetItemVOs);
        final BillListPanel list = new BillListPanel(templetVO);
        final HashVO[][] vos = {null};
        try{
            vos[0] =UIUtil.getHashVoArrayByDS(null,"select XD_COL1,XD_COL2,XD_COL4,XD_COL5,XD_COL6," +
                    "XD_COL7,XD_COL16,case when XD_COL22='01' then '����' when XD_COL22='02' then '��ע' when XD_COL22='03' then " +
                    "'����' when XD_COL22='04' then '�μ�' when XD_COL22='05' then '��ʧ' else XD_COL22 end XD_COL22, " +
                    "case when XD_COL86='x_wd' or XD_COL86='x_wj' then '��' else '��' end XD_COL86,XD_COL85 from " +
                    "hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" " +
                    "group by XD_COL1) and XD_COL4<'"+getKHDQMonth()+" 00:00:00' and '283'||XD_COL85='"+vo.getStringValue("code")+"' and UPPER(XD_COL16)=UPPER('"+vo.getStringValue("G")+"')");
//            list.addBillListButton(btn_query);
              list.putValue(vos[0]);
//            btn_query.addActionListener(new ActionListener() {
//                @Override
//                public void actionPerformed(ActionEvent actionEvent) {
//                    String [] datas=getDate(list);
//                    String date=datas[0].toString().replace("-","");
//                    try {
//                        vos[0] =UIUtil.getHashVoArrayByDS(null,"select * from hzdb.s_loan_dk_"+date+" " +
//                                "where '283'||BH='"+vo.getStringValue("code")+"' and UPPER(AP)=UPPER('"+vo.getStringValue("G")+"')");
//                        list.putValue(vos[0]);
//                        list.repaint();
//                    } catch (Exception e) {
//                        MessageBox.show(list,"ֻ�ܲ鿴������֮ǰ������");
//                        e.printStackTrace();
//                    }
//                }
//            });
//            list.repaintBillListButton();
        }catch (Exception e){
            e.printStackTrace();
        }
        BillListDialog billListDialog=new BillListDialog(dialog,"������ϸ��ѯ",list,1600,600,true);
        billListDialog.setBtn_confirmVisible(false);
        billListDialog.setVisible(true);

    }
    /**
     * zzl ���������ϸ�鿴
     * @param dialog
     * @param vo
     */
    public void getCkDialog(final Dialog dialog, final BillVO vo){
        Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
        templetVO.setTempletname("�����ϸ�鿴");
        String [] columns = new String[]{"oact_inst_no","cust_no","EXTERNAL_CUSTOMER_IC","f"};
        String [] columnNames=new String[]{"����","�ͻ���","���֤","���"};
        templetVO.setRealViewColumns(columns);
        templetVO.setIsshowlistpagebar(false);
        templetVO.setIsshowlistopebar(false);
        templetVO.setListheaderisgroup(false);
        templetVO.setIslistpagebarwrap(false);
        templetVO.setIsshowlistquickquery(false);
        templetVO.setIscollapsequickquery(true);
        templetVO.setIslistautorowheight(true);
        Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
        for(int i=0;i<columns.length;i++){
            templetItemVOs[i]=new Pub_Templet_1_ItemVO();
            templetItemVOs[i].setListisshowable(true);
            templetItemVOs[i].setPub_Templet_1VO(templetVO);
            templetItemVOs[i].setListwidth(150);
            templetItemVOs[i].setItemtype("�ı���");
            templetItemVOs[i].setListiseditable("4");
            templetItemVOs[i].setItemkey(columns[i].toString());
            templetItemVOs[i].setItemname(columnNames[i].toString());
        }
        templetVO.setItemVos(templetItemVOs);
        final BillListPanel list = new BillListPanel(templetVO);
        final HashVO[][] vos = {null};
        try{
            String date=null;
            if(getTablename()){
                date=getQYTTime();
            }else{
                date=getQYTTime2();
            }
            vos[0] =UIUtil.getHashVoArrayByDS(null,"" +
                    "select a.oact_inst_no,a.cust_no,b.EXTERNAL_CUSTOMER_IC,a.f from (select oact_inst_no,cust_no cust_no,sum(f) f " +
                    "from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" where biz_dt='"+date+"' group by oact_inst_no,cust_no union all select oact_inst_no,cust_no," +
                    "sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" where biz_dt='"+date+"' group by oact_inst_no,cust_no) a left join " +
                    "(select * from hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" where load_dates='"+date+"') b on a.cust_no = b.COD_CUST_ID " +
                    "where a.oact_inst_no='"+vo.getStringValue("deptcode")+"' " +
                    "and b.EXTERNAL_CUSTOMER_IC='"+vo.getStringValue("G")+"'");
            list.putValue(vos[0]);
        }catch (Exception e){
            e.printStackTrace();
        }
        BillListDialog billListDialog=new BillListDialog(dialog,"�����ϸ��ѯ",list,800,600,true);
        billListDialog.setBtn_confirmVisible(false);
        billListDialog.setVisible(true);
    }
    /**
     * zzl ��ͥ���в鿴
     * @param dialog
     * @param vo
     */
    public void getJtDialog(final Dialog dialog, final BillVO vo){
        final BillListDialog jtdialog=new BillListDialog(dialog,"��ͥ��Ա������Ϣ�鿴","HZ_DK_WGMX_CODE2",2000,800);
        jtdialog.getBilllistPanel().getTempletVO().setTablename(tablename);
        jtdialog.getBilllistPanel().getTempletVO().setSavedtablename(tablename);
        jtdialog.getBilllistPanel().queryDataByDS(null,"select * from "+tablename+" where hh='"+vo.getStringValue("hh")+"'");
        jtdialog.getBilllistPanel().addBillListHtmlHrefListener(new BillListHtmlHrefListener(){
            @Override
            public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
                BillVO vo=jtdialog.getBilllistPanel().getSelectedBillVO();
                if(_event.getItemkey().equals("dkye")){
                    getDkDialog(jtdialog,vo);
                }else if(_event.getItemkey().equals("ckye")){
                    getCkDialog(jtdialog,vo);
                }
            }
        });
        jtdialog.setBtn_confirmVisible(false);
        jtdialog.setVisible(true);

    }
    /**
     * zzl��20201012��
     * ���뻧�����ݻ�Ҫ����һ��
     */
    public void onImpData(final BillListDialog dialog, final BillVO vo,String code){
        String mbstr;
        final String count;
        if(code.equals("�Թ����")){
            mbstr="S_LOAN_KHXX_202001_CODE2";
            count="�ͻ���";
        }else{
            mbstr="S_LOAN_KHXX_202001_CODE1";
            count="���֤��";
        }
        final BillCardDialog cardDialog=new BillCardDialog(dialog,"������Ϣ�鿴",mbstr,600,400);
        cardDialog.getBillcardPanel().setRealValueAt("J",vo.getStringValue("C"));
        cardDialog.getBillcardPanel().setRealValueAt("K",vo.getStringValue("D"));
        cardDialog.getBillcardPanel().setRealValueAt("deptcode",vo.getStringValue("F"));
        cardDialog.getBtn_save().setVisible(false);
        cardDialog.getBtn_confirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try{//and J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"'
                    HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select * from S_LOAN_KHXX_202001 where G='"+
                            cardDialog.getBillcardPanel().getRealValueAt("G")+"' and deptcode='"+vo.getStringValue("F")+"'");
                    if(vos.length>0){
                        if(vos[0].getStringValue("J")==null && vos[0].getStringValue("K")==null){//zzl �Ѵ��ڵ���û�л�������
                            UIUtil.executeUpdateByDS(null,"update S_LOAN_KHXX_202001 set J='"+vo.getStringValue("C")+"'," +
                                    "K='"+vo.getStringValue("D")+"' where G='"+vos[0].getStringValue("G")+"' and deptcode='"+vo.getStringValue("F")+"'");
                            MessageBox.show(cardDialog,"����ɹ����²�ѯ����");
                            insertSql(dialog,vos[0],null);
                            cardDialog.dispose();
                        }else{
                        	if(flag){
                        		boolean fag= MessageBox.confirm(cardDialog,""+count+"Ϊ��"+vos[0].getStringValue("G")+"���Ŀͻ��Ѵ��ڲ����뵽��"+vos[0].getStringValue("J")+"-"+vos[0].getStringValue("K")+"��������,��ȷ��Ҫǿ�Ƶ�����");
                                if(fag){
                                    updateSql(cardDialog,vo);
                                    MessageBox.show(cardDialog,"�޸ĳɹ����²�ѯ����");
                                    cardDialog.dispose();
                                }else{
                                    return;
                                }
                        	}else{
                                MessageBox.show(cardDialog,""+count+"Ϊ��"+vos[0].getStringValue("G")+"���Ŀͻ��Ѵ��ڲ����뵽��"+vos[0].getStringValue("J")+"-"+vos[0].getStringValue("K")+"��������,����ǿ�Ƶ��룬����ϵ������֧���г���");
                                return;
                        	}
                            //zzl �Ѵ��ڲ���������
                            
                        }
                    }else{
                        if(cardDialog.getBillcardPanel().getRealValueAt("G")==null ||
                                cardDialog.getBillcardPanel().getRealValueAt("G").equals("") ||
                                cardDialog.getBillcardPanel().getRealValueAt("G").equals(null)){
                            MessageBox.show(cardDialog,""+count+"����Ϊ��");
                        }else{
                            cardDialog.getBillcardPanel().updateData();
                            insertSql(dialog,null,cardDialog.getBillcardPanel().getBillVO());
                            MessageBox.show(cardDialog,"����ɹ����²�ѯ����");
                            cardDialog.dispose();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        cardDialog.setVisible(true);
    }

    /**
     * zzl
     * ǿ���޸�
     *
     */
    private void updateSql(BillCardDialog cardDialog,BillVO vo){
        try{
            UIUtil.executeUpdateByDS(null,"update S_LOAN_KHXX_202001 set J='"+vo.getStringValue("C")+"'," +
                    "K='"+vo.getStringValue("D")+"' where G='"+cardDialog.getBillcardPanel().getRealValueAt("G")+"' and deptcode='"+vo.getStringValue("F")+"'");
            UIUtil.executeUpdateByDS(null,"update "+tablename+" set J='"+vo.getStringValue("C")+"'," +
                    "K='"+vo.getStringValue("D")+"' where G='"+cardDialog.getBillcardPanel().getRealValueAt("G")+"' and deptcode='"+vo.getStringValue("F")+"'");

        }catch (Exception e){

        }
    }
    /**
     * zzl
     * ������ʱ�������
     * @param vo
     */
    public void insertSql(BillListDialog dialog,HashVO vo,BillVO bvo){
        InsertSQLBuilder insert=new InsertSQLBuilder(tablename);
        String [] keys=dialog.getBilllistPanel().getTempletVO().getItemKeys();
        List list=new ArrayList();
        if(vo!=null){
            for(int i=0;i<keys.length;i++){
                insert.putFieldValue(keys[i],vo.getStringValue(keys[i]));
            }
            list.add(insert.getSQL());
        }else if(bvo!=null){
            for(int i=0;i<keys.length;i++){
                insert.putFieldValue(keys[i],bvo.getStringValue(keys[i]));
            }
            list.add(insert.getSQL());
        }
        try {
            UIUtil.executeBatchByDS(null,list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * ��ǰ�������� zzl
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getKHDQMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * �õ�ǰһ����·�
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getQYDayMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * �õ�ǰһ��������·�
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getQYDayMonth(int a) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - a);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * �õ�ǰһ�������·�
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getQYDaySMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        cal.setTime(cal.getTime());
        cal.add(Calendar.MONTH,-1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * �õ�ǰһ�������·�
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getQYDaySMonth(int a) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        cal.setTime(cal.getTime());
        cal.add(Calendar.MONTH,-a);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }

    public static void main(String[] args) {
        GridDateMxQuery c=new GridDateMxQuery();
        System.out.println(">>>>>>>>>>>>>>>>"+c.getQYDaySMonth());
    }

    /**
     * �õ�ǰһ�������
     *
     * @return
     */
    public String getQYTTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * �õ�ǰ2�������
     *
     * @return
     */
    public String getQYTTime2() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 2);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     *
     */
    public Boolean getTablename(){
        SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date1= null;
        String createDate=null;
        String blcreateDate=null;
        String dgblcreateDate=null;
        try {
//            date1 = formatTemp.parse(""+getKHDQMonth()+" 10:30:00");
            createDate=UIUtil.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'GRID_DATA_"+getQYTTime()+"' and OBJECT_TYPE='TABLE'");
            blcreateDate=UIUtil.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_DK_"+getQYDayMonth()+"' and OBJECT_TYPE='TABLE'");
            dgblcreateDate=UIUtil.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'S_LOAN_DK_DG_"+getQYDayMonth()+"' and OBJECT_TYPE='TABLE'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(blcreateDate==null || blcreateDate.equals("") || blcreateDate.equals(null)){
            bltablename="S_LOAN_DK_"+(ymfalg==true?getQYDaySMonth(2):getQYDaySMonth());
        }else{
            bltablename="S_LOAN_DK_"+(ymfalg==true?getQYDaySMonth():getQYDaySMonth());
        }
        if(dgblcreateDate==null || dgblcreateDate.equals("") || dgblcreateDate.equals(null)){
            dgbltablename="s_loan_dk_dg_"+(ymfalg==true?getQYDaySMonth(2):getQYDaySMonth());
        }else{
            dgbltablename="s_loan_dk_dg_"+(ymfalg==true?getQYDaySMonth():getQYDaySMonth());
        }
        if(createDate==null || createDate.equals("") || createDate.equals(null)){
            tablename="grid_data_"+getQYTTime2();
            return false;
        }else{
            tablename="GRID_DATA_"+getQYTTime();
            return true;
        }
//        Date date2=new Date();
//        if(date1.getTime()>date2.getTime()){
//            tablename="grid_data_"+getQYTTime2();
//            return false;
//        }else{
//            tablename="GRID_DATA_"+getQYTTime();
//            return true;
//        }

    }

}
