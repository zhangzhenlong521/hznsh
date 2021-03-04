package cn.com.pushworld.wn.ui.hz.table;

import cn.com.infostrategy.ui.common.AbstractWorkPanel;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.infostrategy.ui.report.BillCellPanel;
import cn.com.jsc.ui.DateUIUtil;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TableCzJmStateWKPanel
 * fj
 * 城镇居民金融服务情况统计表
 * @author Dragon
 * @date 2021/2/26
 */
public class TableCzJmStateWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillCellPanel billCellPanel=null;
    private String tablename;
    private String dates=null;
    @Override
    public void initialize() {
        billCellPanel=new BillCellPanel("s_table_czjm" ,null, null,true, false,true);
        try{
            getTablename();
            billCellPanel.setValueAt("dates","数据日期:"+dates);
            getCount();
            getCkDate();
            getLoanDate();
            getQnELoanDate();
            getQnyDate();
        }catch (Exception e){

        }
        this.add(billCellPanel);
    }
    public void getQnyDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs syhs,to_char(round(sy.hs/zj.hs*100,2),'fm9999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,to_char(round(dy.hs/zj.hs*100,2),'fm9999990.00') byfgm,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999990.00') jsyfgm,to_char(round(dy.hs/zj.hs*100,2),'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" qny on upper(ry.f)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) sy\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" qny on upper(ry.f)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.f)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) nc on sy.code=nc.code\n" +
                    ") order by to_number(byfgm) desc)\n" +
                    "union all(\n" +
                    "select '',sum(sy.hs) syhs,to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999990.00') byfgm,\n" +
                    "to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') jsyfgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" qny on upper(ry.f)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) sy\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" qny on upper(ry.f)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.f)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) nc on sy.code=nc.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+38);
                    billCellPanel.setBackground("255,153,153",i+4,44);
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
                    "select sy.code,sy.hs syhs,to_char(sy.hs/zj.hs,'fm9999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,to_char(dy.hs/zj.hs,'fm9999990.00') dyfgm,\n" +
                    "to_char((dy.hs/zj.hs)-(sy.hs/zj.hs),'fm9999990.00') jsyfgm,to_char((dy.hs/zj.hs)-(nc.hs/zj.hs),'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join (select * from hzbank.esign_202101 where load_dates=(select max(load_dates) from hzbank.esign_202101)) wg on upper(ry.f)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.g) sy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join (select * from hzbank.esign_202102 where load_dates=(select max(load_dates) from hzbank.esign_202102)) wg on upper(ry.f)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join (select * from hzbank.esign_202012 where load_dates=(select max(load_dates) from hzbank.esign_202012)) wg on upper(ry.f)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs) syhs,to_char(sum(sy.hs)/sum(zj.hs),'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(sum(dy.hs)/sum(zj.hs),'fm9999990.00') dyfgm,\n" +
                    "to_char((sum(dy.hs)/sum(zj.hs))-(sum(sy.hs)/sum(zj.hs)),'fm9999990.00') jsyfgm,to_char((sum(dy.hs)/sum(zj.hs))-(sum(nc.hs)/sum(zj.hs)),'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join (select * from hzbank.esign_202101 where load_dates=(select max(load_dates) from hzbank.esign_202101)) wg on upper(ry.f)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.g) sy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join (select * from hzbank.esign_202102 where load_dates=(select max(load_dates) from hzbank.esign_202102)) wg on upper(ry.f)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_dwzg_202012 ry left join (select * from hzbank.esign_202012 where load_dates=(select max(load_dates) from hzbank.esign_202012)) wg on upper(ry.f)=upper(wg.c)\n" +
                    "where wg.c is not null group by ry.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+29);
                    billCellPanel.setBackground("204,153,255",i+4,35);
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
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,to_char(round(dy.hs/zj.hs*100-nc.hs/zj.hs*100,2),'fm99999990.00') fgmjnc from(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.g) sy\n" +
                    "left join(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join "+tablename+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.g) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,\n" +
                    "to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,to_char(round(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,2),'fm9999990.00') fgmjnc from(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.g) sy\n" +
                    "left join(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join "+tablename+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs,round(sum(wg.dkye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by ry.g) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+16);
                    billCellPanel.setBackground("255,153,102",i+4,26);
                }
            }
        }catch (Exception e){

        }
    }
    /**
     * zzl
     * 存款
     */
    public void getCkDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,dy.hs dyhs,dy.hs-sy.hs hsjsy,dy.hs-nc.hs hsjnc,dy.num dynum,to_char(dy.num-sy.num,'fm999999990.00') yejsy,to_char(dy.num-nc.num,'fm99999990.00') ysjnc,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,round((dy.hs/zj.hs*100)-(nc.hs/zj.hs*100),2) fgmjnc from(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+ DateUIUtil.getSymDateMonth()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.g) sy\n" +
                    "left join(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join "+tablename+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.g) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,round(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,2) fgmjnc from(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.g) sy\n" +
                    "left join(\n" +
                    "select ry.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join "+tablename+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_dwzg_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.f)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by ry.g) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_dwzg_202012 group by g) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+3);
                    billCellPanel.setBackground("255,51,255",i+4,13);
                }
            }
        }catch (Exception e){

        }
    }
    @Override
    public void actionPerformed(ActionEvent actionEvent) {

    }
    /**
     * zzl
     * 总城镇居民数
     */
    public void getCount(){
        try{
            String [][]data=UIUtil.getStringArrayByDS(null,"select b.name,a.num from(\n" +
                    "select deptcode,round(count(deptcode)/4) num from hzdb.s_loan_khxx_202001 where B<>'农业集体户口' and B <>'农村农业户口' and B <>'农户' and B <>'农村农业户口' and B<>'农业家庭户口' and B<>'农村经济组织' group by deptcode\n" +
                    ") a left join hzdb.pub_corp_dept b on a.deptcode=b.code where a.num>0 order by a.num ");
            for(int i=0;i<data.length;i++){
                for(int j=0;j<data[i].length;j++){
                    billCellPanel.setValueAt(data[i][j],i+4,j+1);
                    billCellPanel.setBackground("102,255,102",i+4,2);
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
