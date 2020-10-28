package cn.com.pushworld.wn.ui;

import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTConstants;
import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.*;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;
import cn.com.infostrategy.ui.sysapp.other.BigFileUpload;
import cn.com.infostrategy.ui.sysapp.other.RefDialog_Month;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * zzl
 * 添加贷款网格
 */
public class GridDataManageDKWkPanel extends AbstractWorkPanel implements ActionListener, BillListHtmlHrefListener {
    private String code = "EXCEL_TAB_85_CODE";
    private BillListPanel listPanel = null;
    private WLTButton btn_add, btn_update, btn_delete, btn_log,btn_query;// 新增 修改 删除
    private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
            .getCode();
    private final String USERNAME = ClientEnvironment.getCurrSessionVO()
            .getLoginUserName();
    private BillListPanel list;
    private WLTButton btn_dr=new WLTButton("导入");//zzl[2020-9-18] 添加导入功能
    private Container _parent=null;
    private String selectDate = "";
    @Override
    public void initialize() {

    }
    public BillListPanel getListPanel(){
        createView();
        this._parent=_parent;
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
        try{
            vos=UIUtil.getHashVoArrayByDS(null,"select * from v_pub_user_post_1 where usercode='"+USERCODE+"'");
        }catch (Exception e){

        }
        if(ClientEnvironment.isAdmin()){
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
        return listPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btn_add) {// 新增按钮
            BillCardDialog dialog=new BillCardDialog(listPanel,"新增","EXCEL_TAB_85_EDIT_CODE",900,300);
            dialog.getBillcardPanel().setEditable("PARENTID",false);
            dialog.getBillcardPanel().setRealValueAt("PARENTID","1");
            dialog.setSaveBtnVisiable(false);
            dialog.setVisible(true);
            listPanel.addRow(dialog.getBillcardPanel().getBillVO());
        } else if (e.getSource() == btn_update) {// 修改操作
            BillVO vo = listPanel.getSelectedBillVO();
            if (vo == null) {
                MessageBox.show(this, "请选中一条数据进行修改");
                return;
            }
            BillCardPanel cardPanel = new BillCardPanel(//EXCEL_TAB_85_EDIT_CODE
                    "EXCEL_TAB_85_EDIT_CODE2");
            cardPanel.setBillVO(vo);
            BillCardDialog dialog = new BillCardDialog(listPanel, "修改",
                    cardPanel, WLTConstants.BILLDATAEDITSTATE_UPDATE);// 修改设置
            dialog.setSaveBtnVisiable(false);
            dialog.setVisible(true);
            // UIUtil.getStringValueByDS(null,
            // "SELECT * FROM WNSALARYDB.PUB_USER WHERE CODE='"+newVo.getStringValue("G")+"'");
            try {
                int closeType = dialog.getCloseType();
                System.out.printf("closeType=%d", closeType);
                List<String> list = new ArrayList<String>();
                if (closeType == 1) {// 保存和确定都是1
                    BillVO newVo = dialog.getBillVO();
                    String gyName = UIUtil.getStringValueByDS(null,
                            "SELECT NAME FROM WNSALARYDB.PUB_USER WHERE CODE='"
                                    + newVo.getStringValue("G") + "'");
                    String deptName = UIUtil.getStringValueByDS(null,
                            "SELECT NAME FROM HZDB.pub_corp_dept WHERE CODE='"
                                    + newVo.getStringValue("F") + "'");
                    String wgNum = newVo.getStringValue("E");// 网格号
                    UpdateSQLBuilder update = new UpdateSQLBuilder();
                    update.setTableName("EXCEL_TAB_85");
                    update.setWhereCondition("E='" + wgNum + "'");
                    update.putFieldValue("D", newVo.getStringValue("D"));// 网格名称
                    update.putFieldValue("F", newVo.getStringValue("F"));
                    update.putFieldValue("A", deptName);
                    update.putFieldValue("G", newVo.getStringValue("G"));
                    update.putFieldValue("B", gyName);
                    list.add(update.getSQL());
                    InsertSQLBuilder insert = new InsertSQLBuilder();
                    insert.setTableName("wn_wginfoupdate_log");
                    insert.putFieldValue("code", USERCODE);
                    insert.putFieldValue("name", USERNAME);
                    insert.putFieldValue("dept_old", vo.getStringValue("F"));
                    insert.putFieldValue("dept_new", newVo.getStringValue("F"));
                    insert.putFieldValue("person_old", vo.getStringValue("G"));
                    insert.putFieldValue("person_new",
                            newVo.getStringValue("G"));
                    insert.putFieldValue("wgname_old", vo.getStringValue("D"));
                    insert.putFieldValue("wgname_new",
                            newVo.getStringValue("D"));
                    insert.putFieldValue("operate_name", "修改");
                    insert.putFieldValue("update_time", new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss").format(new Date()));
                    list.add(insert.getSQL());
                    UIUtil.executeBatchByDS(null, list);
                } else {
                    return;
                }
                MessageBox.show(this, "修改完成");
                listPanel.refreshData();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            listPanel.refreshData();
        } else if (e.getSource() == btn_delete) {
            BillVO vo = listPanel.getSelectedBillVO();
            if (vo == null) {
                MessageBox.show(this, "请选中一条数据删除");
                return;
            }
            boolean confirm = MessageBox.confirm("确定删除当前选中网格信息吗?");
            if (confirm) {
                try {
                    /**
                     * 操作日志记录
                     */
                    InsertSQLBuilder insert = new InsertSQLBuilder();
                    insert.setTableName("wn_wginfoupdate_log");// 记录操作日志
                    insert.putFieldValue("operate_name", "删除");
                    insert.putFieldValue("code", USERCODE);
                    insert.putFieldValue("name", USERNAME);
                    insert.putFieldValue("dept_old", vo.getStringValue("A"));
                    insert.putFieldValue("person_old", vo.getStringValue("B"));
                    insert.putFieldValue("wgname_old", vo.getStringValue("D"));
                    insert.putFieldValue("update_time", new SimpleDateFormat(
                            "yyyy-MM-dd").format(new Date()));
                    UIUtil.executeUpdateByDS(null, insert.getSQL());
                    DeleteSQLBuilder delete = new DeleteSQLBuilder(
                            "EXCEL_TAB_85");
                    delete.setWhereCondition("E='" + vo.getStringValue("E")
                            + "'");
                    UIUtil.executeUpdateByDS(null, delete.getSQL());
                    listPanel.refreshData();
                    MessageBox.show(this, "删除成功");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == btn_log) {// 日志查看
            BillListDialog dialog = new BillListDialog(listPanel, "部门系数调整情况查看",
                    list);
            dialog.getBtn_confirm().setVisible(false);
            dialog.getBilllistPanel().QueryDataByCondition("1=1");
            dialog.setVisible(true);
        }

    }

    /**
     * zzl [2020-10-12]
     * @param _event
     */
    @Override
    public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
        final BillVO vo=listPanel.getSelectedBillVO();
        final BillListDialog dialog=new BillListDialog(listPanel,"网格信息查看","HZ_DK_WGMX_CODE1",1600,800);
        dialog.getBilllistPanel().QueryDataByCondition("J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+vo.getStringValue("F")+"'");
        dialog.getBilllistPanel().getQuickQueryPanel().addBillQuickActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                StringBuffer sb=new StringBuffer();
                String A=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("A");
                String G=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("G");
                String num=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("num");
                String ye=dialog.getBilllistPanel().getQuickQueryPanel().getRealValueAt("ye");
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
                if(ye==null || ye.equals("") || ye.equals(null) || ye.equals(" ")){
                }else{
                    String [] str=ye.split(";");
                    sb.append(" and ye>='"+str[0]+"' and ye<='"+str[1]+"'");
                }
                if(sb.toString()==null){
                    dialog.getBilllistPanel().QueryDataByCondition("J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+vo.getStringValue("F")+"'");
                }else{
                    dialog.getBilllistPanel().QueryDataByCondition("J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"' and deptcode='"+vo.getStringValue("F")+"' "+sb.toString()+"");
                }
            }
        });
        dialog.getBilllistPanel().addBillListHtmlHrefListener(new BillListHtmlHrefListener(){
            @Override
            public void onBillListHtmlHrefClicked(BillListHtmlHrefEvent _event) {
                BillVO vo=dialog.getBilllistPanel().getSelectedBillVO();
                getDkDialog(dialog,vo);
            }
        });
        btn_dr.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                onImpData(dialog,vo);
            }
        });
        dialog.getBilllistPanel().addBillListButton(btn_dr);
        dialog.getBilllistPanel().repaintBillListButton();
        dialog.setBtn_confirmVisible(false);
        dialog.setVisible(true);
    }

    /**
     * zzl 贷款网格明细查看
     * @param dialog
     * @param vo
     */
    public void getDkDialog(final Dialog dialog, final BillVO vo){
        Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
        templetVO.setTempletname("贷款明细查看");
        String [] columns = new String[]{"B","C","D","E","F","J","K","AP","BH","BI"};
        String [] columnNames=new String[]{"贷款号","客户名称","手机号码","贷款日期","到期日期","贷款金额","结欠金额","证件号码","机构号","机构简称"};
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
            vos[0] =UIUtil.getHashVoArrayByDS(null,"select * from hzdb.s_loan_dk_"+getDateUpMonth()+" " +
                    "where '283'||BH='"+vo.getStringValue("code")+"' and UPPER(AP)=UPPER('"+vo.getStringValue("G")+"')");
            list.addBillListButton(btn_query);
            list.putValue(vos[0]);
            btn_query.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    String [] datas=getDate(list);
                    String date=datas[0].toString().replace("-","");
                    try {
                        vos[0] =UIUtil.getHashVoArrayByDS(null,"select * from hzdb.s_loan_dk_"+date+" " +
                                "where '283'||BH='"+vo.getStringValue("code")+"' and UPPER(AP)=UPPER('"+vo.getStringValue("G")+"')");
                        list.putValue(vos[0]);
                        list.repaint();
                    } catch (Exception e) {
                        MessageBox.show(list,"只能查看考核月之前的数据");
                        e.printStackTrace();
                    }
                }
            });
            list.repaintBillListButton();
        }catch (Exception e){
            e.printStackTrace();
        }
        BillListDialog billListDialog=new BillListDialog(dialog,"贷款明细查询",list,1600,600,true);
        billListDialog.setBtn_confirmVisible(false);
        billListDialog.setVisible(true);
    }

    /**
     * zzl【20201012】
     * 导入户籍数据还要检验一把
     */
    public void onImpData(BillListDialog dialog,final BillVO vo){
        final BillCardDialog cardDialog=new BillCardDialog(dialog,"网格信息查看","S_LOAN_KHXX_202001_CODE1",600,400);
        cardDialog.getBillcardPanel().setRealValueAt("J",vo.getStringValue("C"));
        cardDialog.getBillcardPanel().setRealValueAt("K",vo.getStringValue("D"));
        cardDialog.getBtn_save().setVisible(false);
        cardDialog.getBtn_confirm().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                try{//and J='"+vo.getStringValue("C")+"' and K='"+vo.getStringValue("D")+"'
                    HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select * from S_LOAN_KHXX_202001 where G='"+
                            cardDialog.getBillcardPanel().getRealValueAt("G")+"' and deptcode='"+vo.getStringValue("F")+"'");
                    if(vos.length>0){
                        if(vos[0].getStringValue("J")==null && vos[0].getStringValue("K")==null){//zzl 已存在但是没有划入网格
                            UIUtil.executeUpdateByDS(null,"update S_LOAN_KHXX_202001 set J='"+vo.getStringValue("C")+"',K='"+vo.getStringValue("D")+"' where G='"+vos[0].getStringValue("G")+"'");
                            MessageBox.show(cardDialog,"导入成功重新查询即可");
                            cardDialog.dispose();
                        }else{
                            //zzl 已存在并划入网格
                            MessageBox.show(cardDialog,"身份证号为【"+vos[0].getStringValue("G")+"】的客户已存在并划入网格内");
                        }
                    }else{
                        cardDialog.getBillcardPanel().updateData();
                        MessageBox.show(cardDialog,"导入成功重新查询即可");
                        cardDialog.dispose();
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
     * 比较日期决定创建的视图
     */
    public void createView() {
        try {
            SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String createDate = UIUtil.getStringValueByDS(null, "select CREATED from dba_objects where object_name = 'V_HZ_DK_WGMX' and OBJECT_TYPE='VIEW'");
            if (createDate == null || createDate.equals("") || createDate.equals(null)) {
                UIUtil.executeUpdateByDS(null, "create or replace view hzdb.v_hz_dk_wgmx as select wg.*,case when dk.ye is null then '待开发' else '我行客户' end num,dk.ye from(select xx.A,xx.B,xx.C,xx.D,xx.E,xx.F,xx.G,xx.H,xx.I,xx.J,xx.K,wg.f deptcode,wg.code code from hzdb.s_loan_khxx_202001 xx left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg left join (select BH,AP,sum(K) ye from hzdb.s_loan_dk_"+getDateUpMonth()+" group by BH,AP) dk on wg.code='283'||dk.BH and UPPER(wg.G)=UPPER(dk.AP)");
            } else {
                Date date1 = formatTemp.parse(createDate);
                Date date2 = formatTemp.parse(getDateOneDay());
                if (date1.getTime() < date2.getTime()) {
                    UIUtil.executeUpdateByDS(null, "create or replace view hzdb.v_hz_dk_wgmx as select wg.*,case when dk.ye is null then '待开发' else '我行客户' end num,dk.ye from(select xx.A,xx.B,xx.C,xx.D,xx.E,xx.F,xx.G,xx.H,xx.I,xx.J,xx.K,wg.f deptcode,wg.code code from hzdb.s_loan_khxx_202001 xx left join (select wg.*,dept.B code from hzdb.excel_tab_85 wg left join hzdb.excel_tab_28 dept on wg.f=dept.C where wg.parentid='2') wg on xx.deptcode||xx.j||xx.K=wg.F||wg.C||wg.D) wg left join (select BH,AP,sum(K) ye from hzdb.s_loan_dk_"+getDateUpMonth()+" group by BH,AP) dk on wg.code='283'||dk.BH and UPPER(wg.G)=UPPER(dk.AP)");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * zzl得到每月1号
     * 当前时间
     */
    public String getDateOneDay(){
        Calendar cale = null;
        cale = Calendar.getInstance();
        // 获取当月第一天和最后一天
        SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String firstday, lastday;
        // 获取当前月的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, 0);
        cale.set(Calendar.DAY_OF_MONTH, 1);
        firstday = formatTemp.format(cale.getTime());
        return firstday;
    }
    /**
     * zzl得到上月的月份
     * 当前时间
     */
    public String getDateUpMonth(){
        Calendar cale = null;
        cale = Calendar.getInstance();
        // 获取当月第一天和最后一天
        SimpleDateFormat formatTemp = new SimpleDateFormat("yyyyMM");
        String firstday, lastday;
        // 获取当前月的第一天
        cale = Calendar.getInstance();
        cale.add(Calendar.MONTH, -1);
        firstday = formatTemp.format(cale.getTime());
        return firstday;
    }
    /**
     * 时间
     * @param _parent
     * @return
     */
    private String [] getDate(Container _parent) {
        String [] str=null;
        int a=0;
        try {
            RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "请选择上传数据的月份", new RefItemVO(selectDate, "", selectDate), null);
            chooseMonth.initialize();
            chooseMonth.setVisible(true);
            selectDate = chooseMonth.getReturnRefItemVO().getName();
            a=chooseMonth.getCloseType();
            str=new String[]{selectDate,String.valueOf(a)};
            return str;
        } catch (Exception e) {
            WLTLogger.getLogger(BigFileUpload.class).error(e);
        }
        return new String[]{"2013-08",String.valueOf(a)};
    }
}
