package cn.com.pushworld.wn.ui.hz.score.p01.Grid;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.mdata.*;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * zzl
 * 只用一套网格
 */
public class GridDateMxQuery extends AbstractWorkPanel implements
        ActionListener, BillListHtmlHrefListener {
    private String code = "EXCEL_TAB_85_CODE";
    private BillListPanel listPanel = null;
    private WLTButton btn_add, btn_update, btn_delete, btn_log,btn_query;// 新增 修改 删除
    private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
            .getCode();
    private final String USERNAME = ClientEnvironment.getCurrSessionVO()
            .getLoginUserName();
    private BillListPanel list;
    private WLTButton btn_dr=new WLTButton("导入");//zzl[2020-9-18] 添加导入功能\
    private WLTButton btn_xg=new WLTButton("修改");//zzl[2020-9-18] 添加导入功能\
    private Container _parent=null;
    private String selectDate = "";
    private String deptcode;
    private String tablename;
    @Override
    public void initialize() {
        listPanel = new BillListPanel(code);
        btn_query=new WLTButton("查询");
        btn_add = new WLTButton("新增");
        btn_add.addActionListener(this);
        btn_update = new WLTButton("修改");
        btn_update.addActionListener(this);
        btn_delete = new WLTButton("删除");
        btn_delete.addActionListener(this);
        btn_log = new WLTButton("日志查看");
        btn_log.addActionListener(this);
        HashVO[] vos=null;
        HashMap<String,String> roleMap=new HashMap<String, String>();
        try{
            vos= UIUtil.getHashVoArrayByDS(null,"select * from v_pub_user_post_1 where usercode='"+USERCODE+"'");
            roleMap=UIUtil.getHashMapBySQLByDS(null,"select ROLENAME,ROLENAME from v_pub_user_role_1 where usercode='"+USERCODE+"'");
        }catch (Exception e){

        }
        if(ClientEnvironment.isAdmin() || roleMap.get("绩效系统管理员")!=null){
            listPanel.QueryDataByCondition("PARENTID='2'");//zzl[20201012]
            listPanel.addBatchBillListButton(new WLTButton[] {btn_add, btn_update});
            listPanel.setDataFilterCustCondition("PARENTID='2'");
        }else if(vos[0].getStringValue("POSTNAME").contains("行长")){
            listPanel.QueryDataByCondition("PARENTID='2' and F='"+vos[0].getStringValue("DEPTCODE")+"'");//zzl[20201012]
            listPanel.addBatchBillListButton(new WLTButton[] {btn_add, btn_update});
            listPanel.setDataFilterCustCondition("PARENTID='2' and F='"+vos[0].getStringValue("DEPTCODE")+"'");
        }else{
            listPanel.QueryDataByCondition("PARENTID='2' and G='"+vos[0].getStringValue("USERCODE")+"'");//zzl[20201012]
            listPanel.addBatchBillListButton(new WLTButton[] {btn_update});
            listPanel.setDataFilterCustCondition("PARENTID='2' and and G='"+vos[0].getStringValue("USERCODE")+"'");
        }
        list = new BillListPanel("WN_WGINFOUPDATE_LOG_CODE");
        listPanel.repaintBillListButton();// 刷新按钮
        listPanel.addBillListHtmlHrefListener(this); // zzl[20201012]
        getTablename();
        this.add(listPanel);
    }

    @Override
    public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
        final BillVO vo=listPanel.getSelectedBillVO();
        final BillListDialog dialog=new BillListDialog(listPanel,"网格信息查看","HZ_DK_WGMX_CODE1",2000,800);
        dialog.getBilllistPanel().getTempletVO().setTablename(tablename);
        dialog.getBilllistPanel().getTempletVO().setSavedtablename(tablename);
        deptcode=vo.getStringValue("F");
        dialog.getBilllistPanel().queryDataByDS(null,"select * from "+tablename+" where J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+deptcode+"'");
        dialog.getBilllistPanel().getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuffer sb=new StringBuffer();
                String A=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A");
                String G=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("G");
                String num=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("num");
                String dkye=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("dkye");
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
                onImpData(dialog,vo);
            }
        });
        btn_xg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                updateDate(dialog,vo);
            }
        });
        dialog.getBilllistPanel().addBillListButton(btn_dr);
        dialog.getBilllistPanel().addBillListButton(btn_xg);
        dialog.getBilllistPanel().repaintBillListButton();
        dialog.setBtn_confirmVisible(false);
        dialog.setVisible(true);

    }

    /**
     * zzl 添加修改功能
     * @param dialog
     * @param vo
     */
    private void updateDate(final BillListDialog dialog, BillVO vo) {
        final BillCardDialog cardDialog=new BillCardDialog(dialog,"网格信息查看","S_LOAN_KHXX_202001_CODE1",600,400);
        if(dialog.getBilllistPanel().getSelectedBillVO()==null){
            MessageBox.show(dialog,"请选择一条数据修改");
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
                    MessageBox.show(dialog,"更新完成");
                    dialog.getBilllistPanel().refreshCurrSelectedRow();
                    dialog.getBilllistPanel().repaint();
                    cardDialog.dispose();
                } catch (Exception e) {
                    MessageBox.show(dialog,"更新失败");
                    cardDialog.dispose();
                    e.printStackTrace();
                }
            }
        });
        cardDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btn_add) {// 新增按钮
            BillCardDialog dialog=new BillCardDialog(listPanel,"新增","EXCEL_TAB_85_EDIT_CODE",900,300);
            dialog.getBillcardPanel().setEditable("PARENTID",false);
            dialog.getBillcardPanel().setRealValueAt("PARENTID","2");
            dialog.setSaveBtnVisiable(false);
            dialog.setVisible(true);
            if(dialog.getBillcardPanel().getBillVO().getStringValue("D").equals("") ||
                    dialog.getBillcardPanel().getBillVO().getStringValue("D")==null ||
                    dialog.getBillcardPanel().getBillVO().getStringValue("D").equals(null) ||
                    dialog.getBillcardPanel().getBillVO().getStringValue("D").equals(" ")
            ){
            }else{
                listPanel.addRow(dialog.getBillcardPanel().getBillVO());
            }
        }else if(actionEvent.getSource() == btn_update){
            BillVO vo=listPanel.getSelectedBillVO();
            if (vo == null) {
                MessageBox.show(this, "请选中一条数据进行修改");
                return;
            }
            BillCardPanel cardPanel = new BillCardPanel(
                    "EXCEL_TAB_85_EDIT_CODE2");
            cardPanel.setBillVO(vo);
            cardPanel.setRealValueAt("PARENTID","2");
            BillCardDialog dialog = new BillCardDialog(listPanel, "修改",
                    cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);// 修改设置
            dialog.setSaveBtnVisiable(false);
            dialog.setVisible(true);
            listPanel.refreshCurrSelectedRow();
        }


    }
    /**
     * zzl 贷款网格明细查看
     * @param dialog
     * @param vo
     */
    public void getDkDialog(final Dialog dialog, final BillVO vo){
        Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
        templetVO.setTempletname("贷款明细查看");
        String [] columns = new String[]{"XD_COL1","XD_COL2","XD_COL4","XD_COL5","XD_COL6","XD_COL7","XD_COL16","XD_COL22","XD_COL86","XD_COL85"};
        String [] columnNames=new String[]{"贷款号","客户名称","贷款日期","到期日期","贷款金额","结欠金额","证件号码","五级分类","E贷","机构简称"};
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
            templetItemVOs[i].setItemtype("文本框");
            templetItemVOs[i].setListiseditable("4");
            templetItemVOs[i].setItemkey(columns[i].toString());
            templetItemVOs[i].setItemname(columnNames[i].toString());
        }
        templetVO.setItemVos(templetItemVOs);
        final BillListPanel list = new BillListPanel(templetVO);
        final HashVO[][] vos = {null};
        try{
            vos[0] =UIUtil.getHashVoArrayByDS(null,"select XD_COL1,XD_COL2,XD_COL4,XD_COL5,XD_COL6," +
                    "XD_COL7,XD_COL16,case when XD_COL22='01' then '正常' when XD_COL22='02' then '关注' when XD_COL22='03' then " +
                    "'可疑' when XD_COL22='04' then '次级' when XD_COL22='05' then '损失' else XD_COL22 end XD_COL22, " +
                    "case when XD_COL86='x_wd' or XD_COL86='x_wj' then '是' else '否' end XD_COL86,XD_COL85 from " +
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
//                        MessageBox.show(list,"只能查看考核月之前的数据");
//                        e.printStackTrace();
//                    }
//                }
//            });
//            list.repaintBillListButton();
        }catch (Exception e){
            e.printStackTrace();
        }
        BillListDialog billListDialog=new BillListDialog(dialog,"贷款明细查询",list,1600,600,true);
        billListDialog.setBtn_confirmVisible(false);
        billListDialog.setVisible(true);

    }
    /**
     * zzl 存款网格明细查看
     * @param dialog
     * @param vo
     */
    public void getCkDialog(final Dialog dialog, final BillVO vo){
        Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
        templetVO.setTempletname("存款明细查看");
        String [] columns = new String[]{"oact_inst_no","cust_no","EXTERNAL_CUSTOMER_IC","f"};
        String [] columnNames=new String[]{"机构","客户号","身份证","余额"};
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
            templetItemVOs[i].setItemtype("文本框");
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
        BillListDialog billListDialog=new BillListDialog(dialog,"存款明细查询",list,800,600,true);
        billListDialog.setBtn_confirmVisible(false);
        billListDialog.setVisible(true);
    }
    /**
     * zzl 家庭银行查看
     * @param dialog
     * @param vo
     */
    public void getJtDialog(final Dialog dialog, final BillVO vo){
        final BillListDialog jtdialog=new BillListDialog(dialog,"家庭成员网格信息查看","HZ_DK_WGMX_CODE2",2000,800);
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
     * zzl【20201012】
     * 导入户籍数据还要检验一把
     */
    public void onImpData(final BillListDialog dialog, final BillVO vo){
        final BillCardDialog cardDialog=new BillCardDialog(dialog,"网格信息查看","S_LOAN_KHXX_202001_CODE1",600,400);
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
                        if(vos[0].getStringValue("J")==null && vos[0].getStringValue("K")==null){//zzl 已存在但是没有划入网格
                            UIUtil.executeUpdateByDS(null,"update S_LOAN_KHXX_202001 set J='"+vo.getStringValue("C")+"'," +
                                    "K='"+vo.getStringValue("D")+"' where G='"+vos[0].getStringValue("G")+"' and deptcode='"+vo.getStringValue("F")+"'");
                            MessageBox.show(cardDialog,"导入成功重新查询即可");
                            insertSql(dialog,vos[0],null);
                            cardDialog.dispose();
                        }else{
                            //zzl 已存在并划入网格
                            MessageBox.show(cardDialog,"身份证号为【"+vos[0].getStringValue("G")+"】的客户已存在并划入网格内");
                            return;
                        }
                    }else{
                        if(cardDialog.getBillcardPanel().getRealValueAt("G")==null ||
                                cardDialog.getBillcardPanel().getRealValueAt("G").equals("") ||
                                cardDialog.getBillcardPanel().getRealValueAt("G").equals(null)){
                            MessageBox.show(cardDialog,"身份证不能为空");
                        }else{
                            cardDialog.getBillcardPanel().updateData();
                            insertSql(dialog,null,cardDialog.getBillcardPanel().getBillVO());
                            MessageBox.show(cardDialog,"导入成功重新查询即可");
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
     * 插入临时表的数据
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
     * 当前考核日期 zzl
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
     * 得到前一天的月份
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
     * 得到前一天的日期
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
     * 得到前2天的日期
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
        try {
//            date1 = formatTemp.parse(""+getKHDQMonth()+" 10:30:00");
            createDate=UIUtil.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'GRID_DATA_"+getQYTTime()+"' and OBJECT_TYPE='TABLE'");
        } catch (Exception e) {
            e.printStackTrace();
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
