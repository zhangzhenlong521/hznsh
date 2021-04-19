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
 * TableNhStateWKPanel
 * fj
 * 农户金融服务情况表
 * @author Dragon
 * @date 2021/2/26
 */
public class TableNhStateWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillCellPanel billCellPanel=null;
    private String tablename;
    private String dates=null;
    private JButton btn=null;
    private String selectDate = null;
    @Override
    public void initialize() {
        billCellPanel=new BillCellPanel("s_table_nh" ,null, null,true, false,true);
        try{
            getTablename();
            billCellPanel.setValueAt("dates","数据日期:"+dates);
            getCount();
            getCkDate();
            getLoanDate();
            getQnELoanDate();
            getQnyDate();
            getBlLoanDate();
            getSbkDate();
            getSbkCkDate();
            btn=billCellPanel.getBtn_selectData();
            btn.addActionListener(this);
        }catch (Exception e){

        }
        this.add(billCellPanel);
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
                    "select * from(\n" +
                    "select sy.code,sy.hs syhs,sy.num syye,to_char(sy.blv,'fm9990.00') syblv,dy.hs byhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,to_char(dy.num,'fm99999990.00') byye,to_char(dy.num-sy.num,'fm9999990.00') jsyye,to_char(dy.num-nc.num,'fm9999990.00') jncye,\n" +
                    "to_char(dy.blv,'fm9999990.00') byblv,to_char(dy.blv-sy.blv,'fm9999990.00') jsyblv,to_char(dy.blv-nc.blv,'fm999990.00') jncblv from(\n" +
                    "select ry.g code,count(wg.ap) hs,case when sum(replace(wg.k,',','')) is null then 0 else round(sum(replace(wg.k,',',''))/10000,2) end num,\n" +
                    "case when sum(replace(zj.k,',','')) is null or sum(replace(wg.k,',','')) is null then 0 else round((sum(replace(wg.k,',',''))/10000)/(sum(replace(zj.k,',',''))/10000)*100,2) end blv  from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    "group by ry.g) sy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,case when sum(replace(wg.k,',','')) is null then 0 else round(sum(replace(wg.k,',',''))/10000,2) end num,\n" +
                    "case when sum(replace(zj.k,',','')) is null or sum(replace(wg.k,',','')) is null then 0 else round((sum(replace(wg.k,',',''))/10000)/(sum(replace(zj.k,',',''))/10000)*100,2) end blv  from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    "group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,case when sum(replace(wg.k,',','')) is null then 0 else round(sum(replace(wg.k,',',''))/10000,2) end num,\n" +
                    "case when sum(replace(zj.k,',','')) is null or sum(replace(wg.k,',','')) is null then 0 else round((sum(replace(wg.k,',',''))/10000)/(sum(replace(zj.k,',',''))/10000)*100,2) end blv  from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    "group by ry.g) nc on sy.code=nc.code) order by byblv)\n" +
                    "union all\n" +
                    "(select '合计',sum(sy.hs) syhs,sum(sy.num) syye,to_char(sum(sy.num)/sum(sy.zye)*100,'fm99990.00') syblv,sum(dy.hs) byhs,sum(dy.hs)-sum(sy.hs)jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(sum(dy.num),'fm999990.00') byye,\n" +
                    "to_char(sum(dy.num)-sum(sy.num),'fm999990.00') jsyye,to_char(sum(dy.num)-sum(nc.num),'fm999990.00') jncye,\n" +
                    "to_char(sum(dy.num)/sum(dy.zye)*100,'fm999990.00') byblv,to_char(sum(dy.num)/sum(dy.zye)*100-sum(sy.num)/sum(sy.zye)*100,'fm999990.00') jsyblv,to_char(sum(dy.num)/sum(dy.zye)*100-sum(nc.num)/sum(nc.zye)*100,'fm999990.00') jncblv from(\n" +
                    "select ry.g code,count(wg.ap) hs,case when sum(replace(wg.k,',','')) is null then 0 else round(sum(replace(wg.k,',',''))/10000,2) end num, \n" +
                    "case when sum(replace(zj.k,',','')) is null then 0 else round(sum(replace(zj.k,',',''))/10000,2) end zye from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    "group by ry.g) sy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,case when sum(replace(wg.k,',','')) is null then 0 else round(sum(replace(wg.k,',',''))/10000,2) end num, \n" +
                    "case when sum(replace(zj.k,',','')) is null then 0 else round(sum(replace(zj.k,',',''))/10000,2) end zye from hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    "group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,case when sum(replace(wg.k,',','')) is null then 0 else round(sum(replace(wg.k,',',''))/10000,2) end num, \n" +
                    "case when sum(replace(zj.k,',','')) is null then 0 else round(sum(replace(zj.k,',',''))/10000,2) end zye from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" where  replace(Q,',','')>60 group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) zj on upper(ry.c)=upper(zj.AP)\n" +
                    "group by ry.g) nc on sy.code=nc.code)\n");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+47);
                    billCellPanel.setBackground("255,102,102",i+4,47);

                }
            }
        }catch (Exception e){

        }
    }
    /**
     *51 255 255
     */
    private void getSbkDate() {
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select nc.code,nc.hs,nc.hs-sn.hs,to_char(nc.hs/zj.hs*100,'fm99990.00') fgm,to_char(nc.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99990.00') jsnfgm from(\n" +
                    "select ry.g code,count(wg.c) hs from hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(1,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) group by ry.g)\n" +
                    "nc left join\n" +
                    "(select ry.g code,count(wg.c) hs from hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(2,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) group by ry.g)\n" +
                    "sn on nc.code=sn.code\n" +
                    "left join (select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on nc.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '合计',sum(nc.hs),sum(nc.hs)-sum(sn.hs),to_char(sum(nc.hs)/sum(zj.hs)*100,'fm99990.00') fgm,to_char(sum(nc.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99990.00') jsnfgm from(\n" +
                    "select ry.g code,count(wg.c) hs from hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(1,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) group by ry.g)\n" +
                    "nc left join\n" +
                    "(select ry.g code,count(wg.c) hs from  hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(2,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) group by ry.g)\n" +
                    "sn on nc.code=sn.code\n" +
                    "left join (select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on nc.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    if(date[i][j]==null || date[i][j].equals("") || date[i][j].equals(null)){
                        continue;
                    }else{
                        billCellPanel.setValueAt(date[i][j],i+4,j+60);
                        billCellPanel.setBackground("51,255,255",i+4,60);
                    }
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * 102 255 102
     */
    private void getSbkCkDate() {
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select nc.code,nc.hs,nc.hs-sn.hs,nc.ye,nc.ye-sn.ye from(\n" +
                    "select wg.g code,count(ck.g) hs,to_char(sum(ck.ckye)/10000,'fm9999999990.00') ye from(\n" +
                    "select ry.g,wg.b from hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(1,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) where wg.b is not null) wg\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getqytYearMonth(1,"yyyyMMdd")+" ck on upper(wg.b)=upper(ck.g) where ck.ckye>1000 group by wg.g) nc\n" +
                    "left join\n" +
                    "(select wg.g code,count(ck.g) hs,to_char(sum(ck.ckye)/10000,'fm9999999990.00') ye from(\n" +
                    "select ry.g,wg.b from hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(2,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) where wg.b is not null) wg\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getqytYearMonth(2,"yyyyMMdd")+" ck on upper(wg.b)=upper(ck.g) where ck.ckye>1000 group by wg.g) sn on nc.code=sn.code) order by to_number(ye) desc)\n" +
                    "union all\n" +
                    "(select '合计',sum(nc.hs),sum(nc.hs)-sum(sn.hs),to_char(sum(nc.ye)),sum(nc.ye)-sum(sn.ye) from(\n" +
                    "select wg.g code,count(ck.g) hs,to_char(sum(ck.ckye)/10000,'fm9999999990.00') ye from(\n" +
                    "select ry.g,wg.b from hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(1,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) where wg.b is not null) wg\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getqytYearMonth(1,"yyyyMMdd")+" ck on upper(wg.b)=upper(ck.g) where ck.ckye>1000 group by wg.g) nc\n" +
                    "left join\n" +
                    "(select wg.g code,count(ck.g) hs,to_char(sum(ck.ckye)/10000,'fm9999999990.00') ye from(\n" +
                    "select ry.g,wg.b from hzdb.s_qwyt_nhjr_202012 ry left join  hzdb.s_qwyt_sbk_"+DateUIUtil.getqytYearMonth(2,"yyyyMM")+" wg on upper(ry.c)=upper(wg.b) where wg.b is not null) wg\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getqytYearMonth(2,"yyyyMMdd")+" ck on upper(wg.b)=upper(ck.g) where ck.ckye>1000 group by wg.g) sn on nc.code=sn.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    if(date[i][j]==null || date[i][j].equals("") || date[i][j].equals(null)){
                        continue;
                    }else{
                        billCellPanel.setValueAt(date[i][j],i+4,j+65);
                        billCellPanel.setBackground("102,255,102",i+4,65);
                    }
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
    /**
     * zzl时间选择框
     * @param _parent
     * @return
     */
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
    public void getQnyDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) sy \n" +
                    "left join \n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_202012 wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on sy.code=zj.code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '合计',sum(sy.hs),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) sy\n" +
                    "left join \n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.s_loan_qnyyx_202012 wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+38);
                    billCellPanel.setBackground("255,153,219",i+4,38);
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
                    "select * from(\n" +
                    "select sy.code,sy.hs,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) sy\n" +
                    "left join \n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj\n" +
                    "on sy.code=zj.code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '合计',sum(sy.hs),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) sy\n" +
                    "left join \n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs from hzdb.s_qwyt_nhjr_202012 nh left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.c)=upper(wg.f)\n" +
                    "group by nh.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+29);
                    billCellPanel.setBackground("51,255,255",i+4,29);
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
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,dy.hs dyhs,dy.hs-sy.hs hsjsy,dy.hs-nc.hs hsjnc,dy.num dynum,to_char(dy.num-sy.num,'fm999999990.00') yejsy,to_char(dy.num-nc.num,'fm99999990.00') ysjnc,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,round(dy.hs/zj.hs*100,2) fgmjnc from(\n" +
                    "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) num from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) sy\n" +
                    "left join(\n" +
                    "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) num from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) num from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select b code,d hs from hzdb.excel_tab_168) zj on sy.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '合计',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,round(sum(dy.hs)/sum(zj.hs)*100,2) fgmjnc from(\n" +
                    "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) num from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) sy\n" +
                    "left join(\n" +
                    "select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) num from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.ap) hs,round(sum(replace(wg.k,',',''))/10000,2) num from  hzdb.s_qwyt_nhjr_202012 ry \n" +
                    "left join (select AP,sum(replace(k,',','')) K from hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" group by AP) wg on upper(ry.c)=upper(wg.AP)\n" +
                    "where replace(wg.k,',','')>0 group by ry.g) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select b code,d hs from hzdb.excel_tab_168) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+16);
                    billCellPanel.setBackground("153,153,255",i+4,16);
                }
            }
        }catch (Exception e){

        }
    }
    /**
     * zzl
     * 存款
     * when dept.name='信贷部' then '营业部'
     * (selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))
     * (selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))
     * DateUIUtil.getYearYmTime()
     */
    public void getCkDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,sy.ye,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "dy.ye dyye,to_char(dy.ye-sy.ye,'fm9999990.00') jsyye,to_char(dy.ye-nc.ye,'fm9999990.00') jncye,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) sy\n" +
                    "left join \n" +
                    "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj\n" +
                    "on sy.code=zj.code)  order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '合计' code,sum(sy.hs) hs,sum(sy.ye) ye,to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "sum(dy.ye) dyye,to_char(sum(dy.ye)-sum(sy.ye),'fm9999990.00') jsyye,to_char(sum(dy.ye)-sum(nc.ye),'fm9999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) sy\n" +
                    "left join \n" +
                    "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) ye from hzdb.s_qwyt_nhjr_202012 nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.c)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+3);
                    billCellPanel.setBackground("255,153,153",i+4,3);
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * zzl
     * 总农户数
     */
    public void getCount(){
        try{
            String [][]data=UIUtil.getStringArrayByDS(null,"select g code,count(c) hs from  hzdb.s_qwyt_nhjr_202012 group by g");
            for(int i=0;i<data.length;i++){
                for(int j=0;j<data[i].length;j++){
                    billCellPanel.setValueAt(data[i][j],i+4,j+1);
                    billCellPanel.setBackground("102,255,102",i+4,1);
                }
            }
        }catch (Exception e){

        }

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
}
