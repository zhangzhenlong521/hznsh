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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TableXsKhStateWKPanel
 * fj
 * 学生客户金融服务情况表
 * @author Dragon
 * @date 2021/2/26
 */
public class TableXsKhStateWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillCellPanel billCellPanel=null;
    private String tablename;
    private String dates=null;
    private String selectDate = null;
    private JButton btn=null;
    @Override
    public void initialize() {
        billCellPanel=new BillCellPanel("s_table_xskh" ,null, null,true, false,true,true);
        try{
            getTablename();
            billCellPanel.setValueAt("dates","数据日期:"+dates);
            btn=billCellPanel.getBtn_selectData();
            btn.addActionListener(this);
            getKkNumCount();
            getYeCount();
        }catch (Exception e){

        }
        this.add(billCellPanel);
    }

    /**
     * zzl
     * 学生余额情况
     */
    private void getYeCount() {
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select dy.code,to_char(dy.num,'fm99999990.00') dyye,to_char(dy.num-sy.num,'fm9999999990.00') jsyye,to_char(dy.num-nc.num,'fm999999990.00') jncye from(\n" +
                    "select ry.d code,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) dy\n" +
                    "left join\n" +
                    "(select ry.d code,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) sy on dy.code=sy.code\n" +
                    "left join\n" +
                    "(select ry.d code,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) nc on dy.code=nc.code) order by to_number(dyye) desc)\n" +
                    "union all\n" +
                    "(select '',to_char(sum(dy.num),'fm99999990.00') dyye,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') jsyye,to_char(sum(dy.num)-sum(nc.num),'fm999999990.00') jncye from(\n" +
                    "select ry.d code,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) dy\n" +
                    "left join\n" +
                    "(select ry.d code,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) sy on dy.code=sy.code\n" +
                    "left join\n" +
                    "(select ry.d code,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) nc on dy.code=nc.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+5,j+13);
                    billCellPanel.setBackground("0,204,204",i+5,14);

                }
            }
        }catch (Exception e){

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

    /**
     * zzl
     * 学生开卡情况
     */
    private void getKkNumCount() {
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm99999990.00') jsyfgm,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2)-round(nc.hs/zj.hs*100,2),'fm99999990.00') jncfgm from(\n" +
                    "select ry.d code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) sy\n" +
                    "left join\n" +
                    "(select d code,count(d) hs from hzdb.s_qwyt_xskh_202012 group by d) zj on sy.code=zj.code\n" +
                    "left join\n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_xskh_202012 ry left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) dy on sy.code=dy.code\n" +
                    "left join \n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) nc on nc.code=dy.code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999990.00') jsyfgm,\n" +
                    "to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(nc.hs)/sum(zj.hs)*100,2),'fm99999990.00') jncfgm from(\n" +
                    "select ry.d code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) sy\n" +
                    "left join\n" +
                    "(select d code,count(d) hs from hzdb.s_qwyt_xskh_202012 group by d) zj on sy.code=zj.code\n" +
                    "left join\n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_xskh_202012 ry left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) dy on sy.code=dy.code\n" +
                    "left join \n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_xskh_202012 ry left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.c)=upper(wg.g)\n" +
                    "where wg.ckye is not null group by ry.d) nc on nc.code=dy.code)\n");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+5,j+3);
                    billCellPanel.setBackground("0,204,204",i+5,10);

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
                        getKkNumCount();
                        getYeCount();
                        billCellPanel.repaint();
                    }
                });
            }else{
                return;
            }
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
