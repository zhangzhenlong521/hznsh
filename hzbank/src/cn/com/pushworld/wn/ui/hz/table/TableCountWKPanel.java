package cn.com.pushworld.wn.ui.hz.table;

import cn.com.infostrategy.to.common.WLTLogger;
import cn.com.infostrategy.to.mdata.RefItemVO;
import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.infostrategy.ui.sysapp.other.BigFileUpload;
import cn.com.infostrategy.ui.sysapp.other.RefDialog_Month;
import cn.com.jsc.ui.DateUIUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TableCountWKPanel
 * zzl
 * 七位一体汇总表
 * @author Dragon
 * @date 2021/4/10
 */
public class TableCountWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillCellPanel billCellPanel=null;
    private String tablename;
    private String dates=null;
    private JButton btn=null;
    private String selectDate = null;
    @Override
    public void initialize() {
        billCellPanel=new BillCellPanel("s_table_count" ,null, null,true, false,true);
        try{
            getTablename();
            billCellPanel.setValueAt("dates","数据日期:"+dates);
            getCkDate();
            getLoanDate();
            getQnELoanDate();
            getQnyDate();
            getBlLoanDate();
//            getSbkDate();
//            getSbkCkDate();
            btn=billCellPanel.getBtn_selectData();
            btn.addActionListener(this);
        }catch (Exception e){

        }
        this.add(billCellPanel);
    }
    public Boolean getTablename(){
        SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date1= null;
        String createDate=null;
        try {
//            date1 = formatTemp.parse(""+getKHDQMonth()+" 10:30:00");
            createDate= UIUtil.getStringValueByDS(null,"select CREATED from dba_objects where object_name = 'GRID_DATA_"+getQYTTime()+"' and OBJECT_TYPE='TABLE'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(createDate==null || createDate.equals("") || createDate.equals(null)){
            tablename="grid_data_"+getQYTTime2();
            getdates(getQYTTime2());
            return false;
        }else{
            tablename="GRID_DATA_"+getQYTTime();
            getdates(getQYTTime());
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
    /**
     * 得到指定的月份的日期
     *
     * @return
     */
    public void getdates(String date) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        try {
            cal.setTime(format.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");
        dates = format2.format(cal.getTime());
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
     * zzl
     * 存款
     */
    public void getCkDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) dy \n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.hz_wcnmg_info_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.c) dy \n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.hz_wcnmg_info_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from  hzdb.hz_wcnmg_info_202012 group by c) zj on dy.code=zj.code)\n" +
                    "union all(\n" +
                    "select sum(dyhs), sum(dyhs) - sum(nchs), to_char(sum(dyye)), to_char(sum(dyye) - sum(ncye)), to_char(sum(dyhs) / sum(zjhs) * 100, 'fm9990.00'),\n" +
                    "to_char(sum(dyhs)/sum(zjhs)*100-sum(nchs)/sum(zjhs)*100,'fm99990.00') from(\n" +
                            "select * from(\n" +
                            "select sum(dy.hs) dyhs,sum(nc.hs) nchs,\n" +
                            "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(nc.ye),'fm999990.00') ncye,sum(zj.hs) zjhs from(\n" +
                            "select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                            "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.c)=upper(wg.g)\n" +
                            "where wg.ckye>1000 group by nh.g) dy \n" +
                            "left join\n" +
                            "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                            "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.c)=upper(wg.g)\n" +
                            "where wg.ckye>1000 group by nh.g) nc on dy.code=nc.code\n" +
                            "left join\n" +
                            "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on dy.code=zj.code)\n" +
                            "union all\n" +
                            "(select sum(dy.hs) dyhs,sum(nc.hs) nchs,\n" +
                            "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(nc.ye),'fm999990.00') ncye,sum(zj.hs) zjhs from(\n" +
                            "select nh.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.hz_wcnmg_info_202012 nh\n" +
                            "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.a)=upper(wg.g)\n" +
                            "where wg.ckye>1000 group by nh.c) dy \n" +
                            "left join\n" +
                            "(select nh.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.hz_wcnmg_info_202012 nh\n" +
                            "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.a)=upper(wg.g)\n" +
                            "where wg.ckye>1000 group by nh.c) nc on dy.code=nc.code\n" +
                            "left join\n" +
                            "(select c code,count(a) hs from  hzdb.hz_wcnmg_info_202012 group by c) zj on dy.code=zj.code)))\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.d code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_gtgsh_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.d) dy \n" +
                    "left join\n" +
                    "(select nh.d code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_gtgsh_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.d) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select d code,count(c) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_dwzg_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) dy \n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_dwzg_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(f) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_txry_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.i)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) dy \n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_txry_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.i)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(i) hs from hzdb.s_qwyt_txry_202012 group by G) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_czjm_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.c) dy \n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_czjm_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from hzdb.s_qwyt_czjm_202012 group by c) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.d code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_xskh_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.d) dy \n" +
                    "left join\n" +
                    "(select nh.d code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_xskh_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.d) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select d code,count(C) hs from hzdb.s_qwyt_xskh_202012 group by d) zj on dy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+4);
                    billCellPanel.setBackground("255,51,255",i+4,4);
                }
            }
        }catch (Exception e){

        }
    }
    /**
     * zzl
     * 贷款
     */
    public void getLoanDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) dy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) nc on dy.code=nc.code\n" +
                    "left join(\n" +
                    "select b code,d hs from hzdb.excel_tab_168) zj on dy.code=zj.code\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select ry.c code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.hz_wcnmg_info_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.c) dy\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.hz_wcnmg_info_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.c) nc on dy.code=nc.code\n" +
                    "left join(\n" +
                    "select b code,e hs from hzdb.excel_tab_168) zj on dy.code=zj.code)\n" +
                    "union all(\n" +
                            "select sum(dyhs), sum(dyhs) - sum(nchs), to_char(sum(dyye)), to_char(sum(dyye) - sum(ncye)), to_char(sum(dyhs) / sum(zjhs) * 100, 'fm99990.00'),\n" +
                    "to_char(sum(dyhs)/sum(zjhs)*100-sum(nchs)/sum(zjhs)*100,'fm99990.00') from(\n" +
                            "select sum(dy.hs) dyhs,sum(nc.hs) nchs,\n" +
                            "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(nc.ye),'fm999990.00') ncye,sum(zj.hs) zjhs from(\n" +
                            "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                            "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                            "where replace(wg.k,',','')>0 group by ry.g) dy\n" +
                            "left join\n" +
                            "(select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                            "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                            "where replace(wg.k,',','')>0 group by ry.g) nc on dy.code=nc.code\n" +
                            "left join(\n" +
                            "select b code,d hs from hzdb.excel_tab_168) zj on dy.code=zj.code\n" +
                            "union all\n" +
                            "(select sum(dy.hs) dyhs,sum(nc.hs) nchs,\n" +
                            "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(nc.ye),'fm999990.00') ncye,sum(zj.hs) zjhs from(\n" +
                            "select ry.c code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.hz_wcnmg_info_202012 ry \n" +
                            "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                            "where replace(wg.k,',','')>0 group by ry.c) dy\n" +
                            "left join\n" +
                            "(select ry.c code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.hz_wcnmg_info_202012 ry \n" +
                            "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                            "where replace(wg.k,',','')>0 group by ry.c) nc on dy.code=nc.code\n" +
                            "left join(\n" +
                            "select b code,e hs from hzdb.excel_tab_168) zj on dy.code=zj.code)))\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select ry.d code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_gtgsh_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.d) dy\n" +
                    "left join\n" +
                    "(select ry.d code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_gtgsh_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.d) nc on dy.code=nc.code\n" +
                    "left join(\n" +
                    "select d code,count(c) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_dwzg_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.f)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) dy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from hzdb.s_qwyt_dwzg_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.f)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) nc on dy.code=nc.code\n" +
                    "left join(\n" +
                    "select g code,count(f) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_txry_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.i)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) dy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from hzdb.s_qwyt_txry_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.i)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) nc on dy.code=nc.code\n" +
                    "left join(\n" +
                    "select g code,count(i) hs from hzdb.s_qwyt_txry_202012 group by G) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.ye),'fm9999990.00') dyye,to_char(sum(dy.ye)-sum(nc.ye),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select ry.c code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from  hzdb.s_qwyt_czjm_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.c) dy\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from hzdb.s_qwyt_czjm_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.c) nc on dy.code=nc.code\n" +
                    "left join(\n" +
                    "select c code,count(a) hs from hzdb.s_qwyt_czjm_202012 group by c) zj on dy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+10);
                    billCellPanel.setBackground("255,153,102",i+4,10);
                }
            }
        }catch (Exception e){

        }
    }
    /**
     * zzl
     * E贷签约
     */
    public void getQnELoanDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from (\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from (\n" +
                    "select nh.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) dy\n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dyhs),sum(dyhs)-sum(nchs),to_char(sum(dyhs)/sum(zjhs)*100,'fm99990.00'),to_char(sum(dyhs)/sum(zjhs)*100-sum(nchs)/sum(zjhs)*100,'fm99990.00') from(\n" +
                    "select * from(\n" +
                    "select sum(dy.hs) dyhs,sum(nc.hs) nchs,sum(zj.hs) zjhs from (\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(nc.hs) nchs,sum(zj.hs) zjhs from (\n" +
                    "select nh.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) dy\n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on dy.code=zj.code)))\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from (\n" +
                    "select nh.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.d) dy\n" +
                    "left join\n" +
                    "(select nh.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.d) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select d code,count(c) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from (\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.f)=upper(wg.f)\n" +
                    "group by nh.g) dy\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.f)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(f) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from (\n" +
                    "select nh.g code,count(wg.g) hs from  hzdb.s_qwyt_txry_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.i)=upper(wg.f)\n" +
                    "group by nh.g) dy\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from  hzdb.s_qwyt_txry_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.i)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(i) hs from hzdb.s_qwyt_txry_202012 group by G) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from (\n" +
                    "select nh.c code,count(wg.g) hs from hzdb.s_qwyt_czjm_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) dy\n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.g) hs from  hzdb.s_qwyt_czjm_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from hzdb.s_qwyt_czjm_202012 group by c) zj on dy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+16);
                    billCellPanel.setBackground("204,153,255",i+4,16);
                }
            }
        }catch (Exception e){

        }
    }
    public void getQnyDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.f) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy \n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.f) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.c code,count(wg.f) hs from hzdb.hz_wcnmg_info_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) dy \n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.f) hs from hzdb.hz_wcnmg_info_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dyhs),sum(dyhs)-sum(nchs),to_char(sum(dyhs)/sum(zjhs)*100,'fm99990.00'),to_char(sum(dyhs)/sum(zjhs)*100-sum(nchs)/sum(zjhs)*100,'fm99990.00') from(select * from(\n" +
                    "select sum(dy.hs) dyhs,sum(nc.hs) nchs,sum(zj.hs) zjhs from(\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy \n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(nc.hs) nchs,sum(zj.hs) zjhs from(\n" +
                    "select nh.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) dy \n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on dy.code=zj.code)))\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.d code,count(wg.f) hs from hzdb.s_qwyt_gtgsh_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.d) dy \n" +
                    "left join\n" +
                    "(select nh.d code,count(wg.f) hs from hzdb.s_qwyt_gtgsh_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.d) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select d code,count(c) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.f) hs from hzdb.s_qwyt_dwzg_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.f)=upper(wg.f)\n" +
                    "group by nh.g) dy \n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.f) hs from hzdb.s_qwyt_dwzg_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.f)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(f) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.f) hs from hzdb.s_qwyt_txry_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.i)=upper(wg.f)\n" +
                    "group by nh.g) dy \n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.f) hs from hzdb.s_qwyt_txry_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.i)=upper(wg.f)\n" +
                    "group by nh.g) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(i) hs from hzdb.s_qwyt_txry_202012 group by G) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.c code,count(wg.f) hs from hzdb.s_qwyt_czjm_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) dy \n" +
                    "left join\n" +
                    "(select nh.c code,count(wg.f) hs from hzdb.s_qwyt_czjm_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.a)=upper(wg.f)\n" +
                    "group by nh.c) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(a) hs from hzdb.s_qwyt_czjm_202012 group by c) zj on dy.code=zj.code)\n" +
                    "union all\n" +
                    "(select sum(dy.hs) dyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs),'fm999990.00') jncfgm from(\n" +
                    "select nh.d code,count(wg.f) hs from hzdb.s_qwyt_xskh_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.d) dy \n" +
                    "left join\n" +
                    "(select nh.d code,count(wg.f) hs from hzdb.s_qwyt_xskh_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.d) nc on dy.code=nc.code\n" +
                    "left join\n" +
                    "(select d code,count(C) hs from hzdb.s_qwyt_xskh_202012 group by d) zj on dy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+20);
                    billCellPanel.setBackground("255,153,153",i+4,20);
                }
            }
        }catch (Exception e){

        }
    }
    /**
     *51 255 255
     */
    private void getSbkDate() {
    }

    /**
     * 102 255 102
     */
    private void getSbkCkDate() {
    }
    /**
     * 255 102 102
     *      * (selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))
     *      *
     *      * DateUIUtil.getYearMonth()
     */
    private void getBlLoanDate() {
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select to_char(dy.ye,'fm99999990.00') dyye,to_char(dy.ye-nc.ye,'fm99999990.00') jncye,to_char(dy.ye/dy.zye*100,'fm9990.00') dyblv,to_char(dy.ye/dy.zye*100-nc.ye/nc.zye*100,'fm9990.00') jncblv from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.c)=upper(zj.AP)) nc on dy.code=nc.code\n" +
                    ")\n" +
                    "union all\n" +
                    "(select to_char(dy.ye,'fm99999990.00') dyye,to_char(dy.ye-nc.ye,'fm99999990.00') jncye,to_char(dy.ye/dy.zye*100,'fm9990.00') dyblv,to_char(dy.ye/dy.zye*100-nc.ye/nc.zye*100,'fm9990.00') jncblv from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.hz_wcnmg_info_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.a)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.hz_wcnmg_info_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.a)=upper(zj.AP)) nc on dy.code=nc.code\n" +
                    ")\n" +
                    "union all\n" +
                    "(select to_char(sum(dyye)),to_char(sum(dyye-ncye)),to_char(sum(dyye)/sum(zye)*100,'fm999990.00') dyblv,to_char(sum(dyye)/sum(zye)*100-sum(ncye)/sum(zye)*100,'fm999990.00') from(\n" +
                    "select * from(\n" +
                    "select to_char(dy.ye,'fm99999990.00') dyye,to_char(nc.ye,'fm99999990.00') ncye,to_char(dy.zye,'fm99999990.00') zye from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.c)=upper(zj.AP)) nc on dy.code=nc.code\n" +
                    ")\n" +
                    "union all\n" +
                    "(select to_char(dy.ye,'fm99999990.00') dyye,to_char(nc.ye,'fm99999990.00') ncye,to_char(dy.zye,'fm99999990.00') zye from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.hz_wcnmg_info_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.a)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.hz_wcnmg_info_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.a)=upper(zj.AP)) nc on dy.code=nc.code\n" +
                    ")))\n" +
                    "union all\n" +
                    "(\n" +
                    "select to_char(dy.ye,'fm99999990.00') dyye,to_char(dy.ye-nc.ye,'fm99999990.00') jncye,to_char(dy.ye/dy.zye*100,'fm9990.00') dyblv,to_char(dy.ye/dy.zye*100-nc.ye/nc.zye*100,'fm9990.00') jncblv from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_gtgsh_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_gtgsh_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.c)=upper(zj.AP)) nc on dy.code=nc.code)\n" +
                    "union all\n" +
                    "(select to_char(dy.ye,'fm99999990.00') dyye,to_char(dy.ye-nc.ye,'fm99999990.00') jncye,to_char(dy.ye/dy.zye*100,'fm9990.00') dyblv,to_char(dy.ye/dy.zye*100-nc.ye/nc.zye*100,'fm9990.00') jncblv from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_dwzg_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.f)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.f)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_dwzg_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.f)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.f)=upper(zj.AP)) nc on dy.code=nc.code)\n" +
                    "union all\n" +
                    "(select to_char(dy.ye,'fm99999990.00') dyye,to_char(dy.ye-nc.ye,'fm99999990.00') jncye,to_char(dy.ye/dy.zye*100,'fm9990.00') dyblv,to_char(dy.ye/dy.zye*100-nc.ye/nc.zye*100,'fm9990.00') jncblv from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_txry_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.i)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.i)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_txry_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.i)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.i)=upper(zj.AP)) nc on dy.code=nc.code)\n" +
                    "union all\n" +
                    "(select to_char(dy.ye,'fm99999990.00') dyye,to_char(dy.ye-nc.ye,'fm99999990.00') jncye,to_char(dy.ye/dy.zye*100,'fm9990.00') dyblv,to_char(dy.ye/dy.zye*100-nc.ye/nc.zye*100,'fm9990.00') jncblv from(\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_czjm_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.a)=upper(zj.AP)\n" +
                    ") dy left join (\n" +
                    "select '1' code,sum(replace(wg.k,',',''))/10000 ye,sum(replace(zj.k,',',''))/10000 zye from hzdb.s_qwyt_czjm_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.a)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.a)=upper(zj.AP)) nc on dy.code=nc.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+32);
                    billCellPanel.setBackground("255,102,102",i+4,32);

                }
            }
        }catch (Exception e){

        }
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if(actionEvent.getSource()==btn){
            String [] datas=getDate(this);
            if(datas.toString().equals("1") || Integer.parseInt(datas[1].toString())==1){
                selectDate=datas[0].toString();
                new SplashWindow(this, new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        billCellPanel.setValueAt("dates","数据日期:"+DateUIUtil.getymDateMonth(selectDate,"yyyy-MM-dd",0));
                        getCkDate();
                        getLoanDate();
                        getQnELoanDate();
                        getQnyDate();
                        getBlLoanDate();
                        getSbkDate();
                        getSbkCkDate();
                        billCellPanel.repaint();
                    }
                });
            }else{
                return;
            }
        }
    }
    private String [] getDate(Container _parent) {
        String [] str=null;
        int a=0;
        try {
            RefDialog_Month chooseMonth = new RefDialog_Month(_parent, "请选择查询的月份默认是查询月份月末数据", new RefItemVO("", "", ""), null);
            chooseMonth.initialize();
            chooseMonth.setVisible(true);
            String selectDate = chooseMonth.getReturnRefItemVO().getName();
            a=chooseMonth.getCloseType();
            str=new String[]{selectDate,String.valueOf(a)};
            return str;
        } catch (Exception e) {
            WLTLogger.getLogger(BigFileUpload.class).error(e);
        }
        return new String[]{"2013-08",String.valueOf(a)};
    }
}
