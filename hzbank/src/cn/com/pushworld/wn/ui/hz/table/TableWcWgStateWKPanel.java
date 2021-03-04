package cn.com.pushworld.wn.ui.hz.table;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.jsc.ui.DateUIUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * TableWcWgStateWKPanel
 * zzl
 * 外出务工客户金融服务情况表
 * @author Dragon
 * @date 2021/2/26
 */
public class TableWcWgStateWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillCellPanel billCellPanel=null;
    private String tablename;
    private String dates=null;
    private HashMap<String,String> map=new HashMap();
    @Override
    public void initialize() {
        try{
            billCellPanel=new BillCellPanel("s_table_wcwg" ,null, null,true, false,true);
            map=UIUtil.getHashMapBySQLByDS(null,"select a.cellvalue,b.cellvalue from(SELECT * FROM hzdb.pub_billcelltemplet_d where templet_h_id='673' and cellrow>=4 and cellcol='1')a\n" +
                    "left join(SELECT * FROM hzdb.pub_billcelltemplet_d where templet_h_id='673' and cellrow>=4 and cellcol='4')b on a.cellrow=b.cellrow\n");
            getTablename();
            billCellPanel.setValueAt("dates","数据日期:"+dates);
            getCkDate();
            getLoanDate();
            getQnELoanDate();
            getQnyDate();
        }catch (Exception e){

        }
        this.add(billCellPanel);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
    public void getQnELoanDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs syhs,to_char(sy.hs/zj.hs,'fm9999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,to_char(dy.hs/zj.hs,'fm9999990.00') dyfgm,\n" +
                    "to_char((dy.hs/zj.hs)-(sy.hs/zj.hs),'fm9999990.00') jsyfgm,to_char((dy.hs/zj.hs)-(nc.hs/zj.hs),'fm9999990.00') jncfgm from(\n" +
                    "select ry.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" wg on upper(ry.a)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.c) sy\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" wg on upper(ry.a)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.c) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(ry.a)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.c) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs) syhs,to_char(sum(sy.hs)/sum(zj.hs),'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(sum(dy.hs)/sum(zj.hs),'fm9999990.00') dyfgm,\n" +
                    "to_char((sum(dy.hs)/sum(zj.hs))-(sum(sy.hs)/sum(zj.hs)),'fm9999990.00') jsyfgm,to_char((sum(dy.hs)/sum(zj.hs))-(sum(nc.hs)/sum(zj.hs)),'fm9999990.00') jncfgm from(\n" +
                    "select ry.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" wg on upper(ry.a)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.c) sy\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" wg on upper(ry.a)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.c) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(ry.a)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.c) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+31);
                    billCellPanel.setBackground("255,51,255",i+4,37);
                }
            }
        }catch (Exception e){

        }
    }
    public void getQnyDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs syhs,to_char(round(sy.hs/zj.hs*100,2),'fm9999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,to_char(round(dy.hs/zj.hs*100,2),'fm9999990.00') byfgm,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999990.00') jsyfgm,to_char(round(dy.hs/zj.hs*100,2),'fm9999990.00') jncfgm from(\n" +
                    "select ry.c code,count(ry.c) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" qny on upper(ry.a)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.c) sy\n" +
                    "left join(\n" +
                    "select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.c code,count(ry.c) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" qny on upper(ry.a)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.c) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.c code,count(ry.c) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.a)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.c) nc on sy.code=nc.code\n" +
                    ") order by to_number(byfgm) desc)\n" +
                    "union all(\n" +
                    "select '',sum(sy.hs) syhs,to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999990.00') byfgm,\n" +
                    "to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') jsyfgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999990.00') jncfgm from(\n" +
                    "select ry.c code,count(ry.c) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" qny on upper(ry.a)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.c) sy\n" +
                    "left join(\n" +
                    "select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.c code,count(ry.c) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" qny on upper(ry.a)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.c) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.c code,count(ry.c) hs from hzdb.hz_wcnmg_info_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.a)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.c) nc on sy.code=nc.code)");
            DecimalFormat df= new DecimalFormat("######0.00");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    if(j==date[i].length-1){
                        billCellPanel.setValueAt(df.format(Double.parseDouble(date[i][j])-Double.parseDouble(map.get(date[i][0]))),i+4,j+40);
                    }else{
                        billCellPanel.setValueAt(date[i][j],i+4,j+40);
                        billCellPanel.setBackground("0,204,204",i+4,46);
                    }
                }
            }
        }catch (Exception e){

        }
    }
    public void getLoanDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,dy.hs dyhs,dy.hs-sy.hs hsjsy,dy.hs-nc.hs hsjnc,dy.num dynum,to_char(dy.num-sy.num,'fm999999990.00') yejsy,to_char(dy.num-nc.num,'fm99999990.00') ysjnc,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,round(dy.hs/zj.hs*100,2) fgmjnc from(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.c) sy\n" +
                    "left join(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join "+tablename+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.c) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.c) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,round(sum(dy.hs)/sum(zj.hs)*100,2) fgmjnc from(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.c) sy\n" +
                    "left join(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join "+tablename+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.c) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.c) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code)");
            DecimalFormat df= new DecimalFormat("######0.00");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    if(j==date[i].length-1){
                        billCellPanel.setValueAt(df.format(Double.parseDouble(date[i][j])-Double.parseDouble(map.get(date[i][0]))),i+4,j+18);
                    }else{
                        billCellPanel.setValueAt(date[i][j],i+4,j+18);
                        billCellPanel.setBackground("39,236,73",i+4,28);
                    }
                }
            }
        }catch (Exception e){

        }
    }
    public void getCkDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,dy.hs dyhs,dy.hs-sy.hs hsjsy,dy.hs-nc.hs hsjnc,dy.num dynum,to_char(dy.num-sy.num,'fm999999990.00') yejsy,to_char(dy.num-nc.num,'fm99999990.00') ysjnc,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,round(dy.hs/zj.hs*100,2) fgmjnc from(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.c) sy\n" +
                    "left join(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join "+tablename+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.c) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.c) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,round(sum(dy.hs)/sum(zj.hs)*100,2) fgmjnc from(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.c) sy\n" +
                    "left join(\n" +
                    "select ry.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join "+tablename+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.c) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.c code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.hz_wcnmg_info_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.a)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.c) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select c code,count(c) hs from hzdb.hz_wcnmg_info_202012 group by c) zj on sy.code=zj.code)");
            DecimalFormat df= new DecimalFormat("######0.00");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    if(j==date[i].length-1){
                        billCellPanel.setValueAt(df.format(Double.parseDouble(date[i][j])-Double.parseDouble(map.get(date[i][0]))),i+4,j+5);
                    }else{
                        billCellPanel.setValueAt(date[i][j],i+4,j+5);
                        billCellPanel.setBackground("0,140,255",i+4,15);
                    }
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
