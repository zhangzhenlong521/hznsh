package cn.com.pushworld.salary.ui.target.p010;

import cn.com.infostrategy.bs.common.SystemOptions;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.mdata.*;
import cn.com.infostrategy.ui.common.*;
import cn.com.infostrategy.ui.mdata.*;
import com.ibm.db2.jcc.t2zos.e;


import javax.swing.*;
import javax.xml.soap.SAAJMetaFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * SecondaryDistributionWKPanel
 * zzl
 * 做一个统一的二次分配功能
 * @author Dragon
 * @date 2021/5/20
 */
public class SecondaryDistributionWKPanel<mian> extends AbstractWorkPanel implements ActionListener{
    private BillQueryPanel query =new BillQueryPanel("SAL_TARGET_CHECK_LOG_PERSON_1");
    private TBUtil tbUtil=new TBUtil();
    private WLTSplitPane splitpanel=null;
    private StringBuffer sbSql=new StringBuffer();
    private WLTButton save_btn=new WLTButton("保存");
    private WLTButton jy_btn=new WLTButton("校验");
    private WLTButton commit_btn=new WLTButton("提交");
    private WLTButton kf_btn=new WLTButton("开放");
    private BillListPanel list=null;
    private String checkdate=null;
    private String[] autoCalcTargetIDs=null;
    private String sql=null;
    private String whereSql=null;
    private final String USERCODE = ClientEnvironment.getCurrLoginUserVO()
            .getCode();
    private String deptid=ClientEnvironment.getInstance().getLoginUserDeptId();
    private Boolean falg= TBUtil.getTBUtil().getSysOptionBooleanValue("二次分配是否开启校验",false);
    private HashMap<String,String> roleMap=new HashMap<String, String>();
    private JPanel jPanel=new JPanel();
    @Override
    public void initialize() {
        query.addBillQuickActionListener(this);
        jPanel.setLayout(null); //
        query.setBounds(0,0,400,50);
        kf_btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                createTable(query.getRealValueAt("checkdate"));
            }
        });
        kf_btn.setBounds(390,5,65,26);
        jPanel.add(query);
        try{
            String leadervo=null;
            roleMap=UIUtil.getHashMapBySQLByDS(null,"select ROLENAME,ROLENAME from v_pub_user_role_1 where usercode='"+USERCODE+"'");
            leadervo=UIUtil.getStringValueByDS(null, "select stationkind from v_sal_personinfo where code='"+USERCODE+"'");
            if(ClientEnvironment.isAdmin() || roleMap.get("绩效系统管理员")!=null){
                whereSql="where 1=1";
                jPanel.add(kf_btn);
            }else if(leadervo.equals("支行行长")){
                whereSql="where deptid='"+deptid+"'";
             }
            splitpanel=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,jPanel,null);
            splitpanel.setDividerLocation(40);
            this.add(splitpanel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createTable(String date) {
        try{
            if(date==null){
                MessageBox.show(this,"日期不能为空");
                return;
            }
            autoCalcTargetIDs= UIUtil.getStringArrayFirstColByDS(null, "select id from sal_person_check_list where state='参与考核' and secondary='Y'"); //找出所有需要计算的指标
            String [] col=UIUtil.getStringArrayFirstColByDS(null,"select targetname from sal_person_check_auto_score where datadate='"+getDateMonthGang(date)+"' and targetid in ("+tbUtil.getInCondition(autoCalcTargetIDs)+") group by targetname");
            LinkedHashMap<String,String> map=new LinkedHashMap();
            StringBuffer colsb=new StringBuffer();
            StringBuffer zdsb=new StringBuffer();
            map.put("deptid","网点id");
            map.put("deptname","网点名称");
            map.put("checkeduser","登录人ID");
            map.put("checkedusername","人员名称");
            getCol(col,map);
            if(col.length>0){
                String [] createDate= UIUtil.getStringArrayFirstColByDS(null,"select CREATED from dba_objects where object_name = 'SAL_SECONDARY_"+getDateMonth(checkdate)+"' and OBJECT_TYPE='TABLE'");
                if(createDate.length>0){
                    MessageBox.show(this,"已开放");
                    return;
                }else{
                    UIUtil.executeUpdateByDS(null,"create table SAL_SECONDARY_"+getDateMonth(checkdate)+" as "+sql+"");
                    MessageBox.show(this,"开放成功");
                    getDataQueryAndSave(map);
                }
            }else{
                MessageBox.show(this,"没有数据");
                return;
            }
            UIUtil.executeUpdateByDS(null,"create table SAL_SECONDARY_"+getDateMonth(checkdate)+" as "+sql+"");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void dataView(String date) {
        try{
            autoCalcTargetIDs= UIUtil.getStringArrayFirstColByDS(null, "select id from sal_person_check_list where state='参与考核' and secondary='Y'"); //找出所有需要计算的指标
            String sql="select targetname from sal_person_check_auto_score where datadate='"+getDateMonthGang(date)+"' and targetid in ("+tbUtil.getInCondition(autoCalcTargetIDs)+") group by targetname";
            String [] col=UIUtil.getStringArrayFirstColByDS(null,sql);
            LinkedHashMap<String,String> map=new LinkedHashMap();
            StringBuffer colsb=new StringBuffer();
            StringBuffer zdsb=new StringBuffer();
            map.put("deptid","网点id");
            map.put("deptname","网点名称");
            map.put("checkeduser","登录人ID");
            map.put("checkedusername","人员名称");
            getCol(col,map);
            if(col.length>0){
                getDataQueryAndSave(map);
            }else{
                if(list==null){

                }else{
                    list.removeAll();
                }
                MessageBox.show(this,"没有数据");
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void getCol(String [] col, LinkedHashMap<String,String> map){
        try{
            StringBuffer colsb=new StringBuffer();
            StringBuffer zdsb=new StringBuffer();
            for(int i=0;i<col.length;i++){
                String colstr=null;
                if(col[i].length()>15){
                    colstr=col[i].replace("(","").
                            replace(")","").
                            replace("+","").substring(0,10);
                }else{
                    colstr=col[i].replace("(","").
                            replace(")","").
                            replace("+","");
                }
                if(i==col.length-1){
                    colsb.append(" case when "+colstr+" is null then 0 else "+colstr+" end "+colstr+",0 "+colstr+"二次分配 ");
                }else{
                    colsb.append(" case when "+colstr+" is null then 0 else "+colstr+" end "+colstr+",0 "+colstr+"二次分配,");
                }
                if(i==col.length-1){
                    zdsb.append("'"+col[i]+"' as "+colstr+"");
                }else{
                    zdsb.append("'"+col[i]+"' as "+colstr+",");
                }
                map.put(colstr,col[i]);
                map.put(colstr+"二次分配",colstr+"二次分配");
            }
            map.put("datadate","数据日期");
            map.put("state","状态");
            sql="SELECT deptid,checkeduser,deptname,checkedusername,datadate,"+colsb.toString()+
                    " ,'未提交' state FROM (\n" +
                    "  select b.deptid,b.deptname,a.* from(select checkeduser,checkedusername,targetname,datadate,money from \n" +
                    "  sal_person_check_auto_score where datadate='"+getDateMonthGang(checkdate)+"' and targetid in ("+tbUtil.getInCondition(autoCalcTargetIDs)+"))a left join v_pub_user_post_1 b on \n" +
                    "  a.checkeduser=b.userid)\n" +
                    "  PIVOT(SUM(money)\n" +
                    "   FOR targetname IN("+zdsb.toString()+"))";
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void getDataQueryAndSave(LinkedHashMap<String,String> map){
        try{
            String [] createDate= UIUtil.getStringArrayFirstColByDS(null,"select CREATED from dba_objects where object_name = 'SAL_SECONDARY_"+getDateMonth(checkdate)+"' and OBJECT_TYPE='TABLE'");
            if(createDate.length<=0){
                MessageBox.show(this,"没有数据");
                return;
            }
            final HashVO[] vos = UIUtil.getHashVoArrayByDS(null, "select * from SAL_SECONDARY_" + getDateMonth(checkdate) + " " + whereSql + "");
            String [] columns=new String [map.size()];
            String [] columnNames=new String[map.size()];
            int a=0;
            for(Object str:map.keySet()){
                columns[a]=str.toString();
                columnNames[a]=map.get(str);
                a++;
            }
            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            templetVO.setTempletname("指标二次分配");
            templetVO.setTablename("SAL_SECONDARY_"+getDateMonth(checkdate)+"");
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            HashMap countMap=new HashMap();
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                if(columns[i].contains("二")){
                    templetItemVOs[i].setListiseditable("1");
                    countMap.put(columns[i],true);
                }else{
                    templetItemVOs[i].setListiseditable("4");
                }
                if(columns[i].equals("deptid") || columns[i].equals("checkeduser")){
                    templetItemVOs[i].setListisshowable(false);
                }else{
                    templetItemVOs[i].setListisshowable(true);
                }
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(150);
                templetItemVOs[i].setItemtype("文本框");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            list=new BillListPanel(templetVO);
            save_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try{
                        BillVO [] vosqd=list.getAllBillVOs();
                        String [] key=vosqd[0].getKeys();
                        List listsql=new ArrayList();
                        String state=null;
                        if(falg){
                            StringBuffer sb=new StringBuffer();
                            for(int j=0;j<key.length;j++){
                                if(key[j].contains("二")){
                                    BigDecimal one=new BigDecimal("0.0");
                                    BigDecimal tow=new BigDecimal("0.0");
                                   for(int i=0;i<vosqd.length;i++){
                                       System.out.println(">>>>>>>>>>"+vosqd[i].getStringValue(key[j-1]));
                                       one=one.add(new BigDecimal(vosqd[i].getStringValue(key[j-1])));
                                       tow=tow.add(new BigDecimal(vosqd[i].getStringValue(key[j])));
                                   }
                                    if(one.compareTo(tow)==0){

                                    }else{
                                        sb.append("指标【"+key[j-1]+"】的二次分配的总余额是("+tow+"),应分配总余额("+one+")，请重新分配"+ System.getProperty("line.separator"));
                                    }
                                }
                            }
                            if(sb.length()>0){
                                MessageBox.show(list,sb.toString());
                                return;
                            }else{
                                if(MessageBox.confirm(list,"是否直接提交？提交后本次分配为最终认定绩效，且不能修改！")){
                                    state="已提交";
                                };
                            }
                        }
                        for(int i=0;i<vosqd.length;i++){
                            UpdateSQLBuilder sqlBuilder=new UpdateSQLBuilder("SAL_SECONDARY_"+getDateMonth(checkdate)+"");
                            for(int j=0;j<key.length;j++){
                                if(key[j].contains("二")){
                                    sqlBuilder.putFieldValue(key[j],vosqd[i].getStringValue(key[j]));
                                }else if(key[j].equals("state") && state!=null){
                                    sqlBuilder.putFieldValue(key[j],state);
                                }
                            }
                            sqlBuilder.setWhereCondition("checkeduser='"+vosqd[i].getStringValue("checkeduser")+"' and datadate='"+getDateMonthGang(checkdate)+"'");
                            listsql.add(sqlBuilder);
                        }
                        UIUtil.executeBatchByDS(null,listsql);
                        MessageBox.show(list,"保存成功");
                        if(state!=null){
                            HashVO [] vos2 =UIUtil.getHashVoArrayByDS(null,"select * from SAL_SECONDARY_"+getDateMonth(checkdate)+" "+whereSql+"");
                            list.putValue(vos2);
                            save_btn.setEnabled(false);
                            commit_btn.setEnabled(false);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        MessageBox.show(list,"保存失败");
                    }
                }
            });
            jy_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                   getJy(list);
                }
            });
            if(ClientEnvironment.isAdmin() || roleMap.get("绩效系统管理员")!=null){
                list.addBillListButton(jy_btn);
            }
            list.addBillListButton(save_btn);
            commit_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    getCommit();
                }
            });
            list.addBillListButton(commit_btn);
            list.repaintBillListButton();
            for(Object fag:countMap.keySet()){
                list.setItemEditable(fag.toString(), (Boolean) countMap.get(fag));
            }
            list.addBillListSelectListener(new BillListSelectListener() {
                @Override
                public void onBillListSelectChanged(BillListSelectionEvent _event) {
                    BillListPanel billListPanel=_event.getBillListPanel();
                    if(billListPanel.getSelectedBillVO().getStringValue("state").equals("已提交")){
                        save_btn.setEnabled(false);
                        commit_btn.setEnabled(false);
                    }else{
                        save_btn.setEnabled(true);
                        commit_btn.setEnabled(true);
                    }
                }
            });
            list.putValue(vos);
            this.removeAll();
            splitpanel=new WLTSplitPane(WLTSplitPane.VERTICAL_SPLIT,jPanel,list);
            splitpanel.setDividerLocation(40);
            this.add(splitpanel);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * zzl 校验
     */
    private void getJy(BillListPanel billListPanel) {
        try{
            HashVO [] vos=UIUtil.getHashVoArrayByDS(null,"select * from SAL_SECONDARY_"+getDateMonth(checkdate)+" where state='未提交'");
            String [] columns=new String []{"deptname","checkedusername","state","datadate"};
            String [] columnNames=new String[]{"网点名称","人员名称","状态","数据日期"};
            Pub_Templet_1VO templetVO = new Pub_Templet_1VO();
            templetVO.setTempletname("二次分配校验查询");
            templetVO.setTablename("SAL_SECONDARY_"+getDateMonth(checkdate)+"");
            templetVO.setRealViewColumns(columns);
            templetVO.setIsshowlistpagebar(false);
            templetVO.setIsshowlistopebar(false);
            templetVO.setListheaderisgroup(false);
            templetVO.setIslistpagebarwrap(false);
            templetVO.setIsshowlistquickquery(false);
            templetVO.setIscollapsequickquery(true);
            templetVO.setIslistautorowheight(true);
            Pub_Templet_1_ItemVO[] templetItemVOs = new Pub_Templet_1_ItemVO[columns.length];
            HashMap countMap=new HashMap();
            for(int i=0;i<columns.length;i++){
                templetItemVOs[i]=new Pub_Templet_1_ItemVO();
                if(columns[i].contains("二")){
                    templetItemVOs[i].setListiseditable("1");
                    countMap.put(columns[i],true);
                }else{
                    templetItemVOs[i].setListiseditable("4");
                }
                if(columns[i].equals("deptid") || columns[i].equals("checkeduser")){
                    templetItemVOs[i].setListisshowable(false);
                }else{
                    templetItemVOs[i].setListisshowable(true);
                }
                templetItemVOs[i].setPub_Templet_1VO(templetVO);
                templetItemVOs[i].setListwidth(150);
                templetItemVOs[i].setItemtype("文本框");
                templetItemVOs[i].setListiseditable("4");
                templetItemVOs[i].setItemkey(columns[i].toString());
                templetItemVOs[i].setItemname(columnNames[i].toString());
            }
            templetVO.setItemVos(templetItemVOs);
            list=new BillListPanel(templetVO);
            list.putValue(vos);
            WLTButton tj_btn=new WLTButton("强制提交");
            tj_btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    try {
                        UIUtil.executeUpdateByDS(null,"update SAL_SECONDARY_"+getDateMonth(checkdate)+" set state='已提交'");
                        MessageBox.show(list,"提交成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            list.addBillListButton(tj_btn);
            list.repaintBillListButton();
            BillListDialog billListDialog=new BillListDialog(billListPanel,"二次分配校验查询",list,800,1000,false);
            billListDialog.setVisible(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * zzl 提交
     */
    private void getCommit() {
        try{
            BillVO [] vosqd=list.getAllBillVOs();
            String [] key=vosqd[0].getKeys();
            List listsql=new ArrayList();
            String state=null;
            if(falg){
                StringBuffer sb=new StringBuffer();
                for(int j=0;j<key.length;j++){
                    if(key[j].contains("二")){
                        BigDecimal one=new BigDecimal("0.0");
                        BigDecimal tow=new BigDecimal("0.0");
                        for(int i=0;i<vosqd.length;i++){
                            System.out.println(">>>>>>>>>>"+vosqd[i].getStringValue(key[j-1]));
                            one=one.add(new BigDecimal(vosqd[i].getStringValue(key[j-1])));
                            tow=tow.add(new BigDecimal(vosqd[i].getStringValue(key[j])));
                        }
                        if(one.compareTo(tow)==0){

                        }else{
                            sb.append("指标【"+key[j-1]+"】的二次分配的总余额是("+tow+"),应分配总余额("+one+")，请重新分配"+ System.getProperty("line.separator"));
                        }
                    }
                }
                if(sb.length()>0){
                    MessageBox.show(list,sb.toString());
                    return;
                }else{
                    if(MessageBox.confirm(list,"是否直接提交？提交后本次分配为最终认定绩效，且不能修改！")){
                        state="已提交";
                    };
                }
            }
            for(int i=0;i<vosqd.length;i++){
                UpdateSQLBuilder sqlBuilder=new UpdateSQLBuilder("SAL_SECONDARY_"+getDateMonth(checkdate)+"");
                for(int j=0;j<key.length;j++){
                    if(key[j].contains("二")){
                        sqlBuilder.putFieldValue(key[j],vosqd[i].getStringValue(key[j]));
                    }else if(state!=null) {
                        sqlBuilder.putFieldValue("state", state);
                    }
                }
                sqlBuilder.setWhereCondition("checkeduser='"+vosqd[i].getStringValue("checkeduser")+"' and datadate='"+getDateMonthGang(checkdate)+"'");
                listsql.add(sqlBuilder);
            }
            UIUtil.executeBatchByDS(null,listsql);
            if(state!=null){
                MessageBox.show(list,"提交成功");
                HashVO [] vos2 =UIUtil.getHashVoArrayByDS(null,"select * from SAL_SECONDARY_"+getDateMonth(checkdate)+" "+whereSql+"");
                list.putValue(vos2);
                save_btn.setEnabled(false);
                commit_btn.setEnabled(false);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==query){
            onQuery();
        }
    }

    private void onQuery() {
        checkdate=query.getRealValueAt("checkdate");
        dataView(checkdate);

    }
    private String getDateMonthGang(String time){
        try{
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            Date date=format.parse(time);
            cal.setTime(date);
            cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DATE));
            SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
            String lastDate = format2.format(cal.getTime());
            return lastDate;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private String getDateMonth(String time){
        try{
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            Date date=format.parse(time);
            cal.setTime(date);
            cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DATE));
            SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
            String lastDate = format2.format(cal.getTime());
            return lastDate;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        SecondaryDistributionWKPanel s=new SecondaryDistributionWKPanel();
        System.out.println(">>>>>>>>>>>>>>>>>"+s.getDateMonthGang("2021-04"));
    }


}