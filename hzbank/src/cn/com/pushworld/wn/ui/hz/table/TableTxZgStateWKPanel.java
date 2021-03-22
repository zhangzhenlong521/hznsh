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
 * TableTxZgStateWKPanel
 * fj
 * 退休职工客户金融服务情况表
 * @author Dragon
 * @date 2021/2/26
 */
public class TableTxZgStateWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillCellPanel billCellPanel=null;
    private String tablename;
    private String dates=null;
    private JButton btn=null;
    private String selectDate = null;
    @Override
    public void initialize() {
        billCellPanel=new BillCellPanel("s_table_txzg" ,null, null,true, false,true);
        try{
            getTablename();
            billCellPanel.setValueAt("dates","数据日期:"+dates);
            getCkDate();
            getLoanDate();
            getQnELoanDate();
            getQnyDate();
            btn=billCellPanel.getBtn_selectData();
            btn.addActionListener(this);
        }catch (Exception e){

        }
        this.add(billCellPanel);
    }
    public void getQnyDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs syhs,to_char(round(sy.hs/zj.hs*100,2),'fm9999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,to_char(round(dy.hs/zj.hs*100,2),'fm9999990.00') byfgm,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" qny on upper(ry.i)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) sy\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_txry_202012 group by g) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" qny on upper(ry.i)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.i)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) nc on sy.code=nc.code\n" +
                    ") order by to_number(byfgm) desc)\n" +
                    "union all(\n" +
                    "select '',sum(sy.hs) syhs,to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999990.00') byfgm,\n" +
                    "to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" qny on upper(ry.i)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) sy\n" +
                    "left join(\n" +
                    "select g code,count(g) hs from hzdb.s_qwyt_txry_202012 group by g) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" qny on upper(ry.i)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.g code,count(ry.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.i)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.g) nc on sy.code=nc.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+38);
                    billCellPanel.setBackground("153,153,255",i+4,38);
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
                    "select sy.code,sy.hs syhs,to_char(sy.hs/zj.hs*100,'fm9999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,\n" +
                    "to_char((dy.hs/zj.hs*100)-(sy.hs/zj.hs*100),'fm9999990.00') jsyfgm,to_char((dy.hs/zj.hs*100)-(nc.hs/zj.hs*100),'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(wg.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(ry.i)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.g) sy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(ry.i)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(ry.i)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(g) hs from hzdb.s_qwyt_txry_202012 group by g) zj on sy.code=zj.code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs) syhs,to_char(sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,\n" +
                    "to_char((sum(dy.hs)/sum(zj.hs)*100)-(sum(sy.hs)/sum(zj.hs)*100),'fm9999990.00') jsyfgm,to_char((sum(dy.hs)/sum(zj.hs)*100)-(sum(nc.hs)/sum(zj.hs)*100),'fm9999990.00') jncfgm from(\n" +
                    "select ry.g code,count(wg.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(ry.i)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.g) sy\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(ry.i)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.g) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.g code,count(wg.g) hs from hzdb.s_qwyt_txry_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(ry.i)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.g) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select g code,count(g) hs from hzdb.s_qwyt_txry_202012 group by g) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+29);
                    billCellPanel.setBackground("102,255,102",i+4,29);
                }
            }
        }catch (Exception e){

        }
    }
    /**
     * zzl
     * 贷款
     * (selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))
     * (selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))
     * DateUIUtil.getYearMonth()
     */
    public void getLoanDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select replace(dept.a,'支行','') code,sy.hs,sy.ye,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,case when dy.hs is null then '0'else to_char(dy.hs) end dyhs,\n" +
                    "case when dy.hs is null and sy.hs is null then '0' when sy.hs is null then to_char(dy.hs) when dy.hs is null then to_char(0-sy.hs) else to_char(dy.hs-sy.hs) end jsyhs,\n" +
                    "  case when dy.hs is null and nc.hs is null then '0' when nc.hs is null then to_char(dy.hs) when dy.hs is null then to_char(0-nc.hs) else to_char(dy.hs-nc.hs) end jnchs,\n" +
                    "case when dy.ye is null then '0' else to_char(dy.ye,'fm999990.00') end dyye,  \n" +
                    "  case when dy.ye is null and sy.ye is null then '0' when sy.ye is null then to_char(dy.ye,'fm9999990.00') when dy.ye is null then to_char(0-sy.ye,'fm9999990.00') \n" +
                    "    else to_char(dy.ye-sy.ye,'fm9999990.00') end jsyye,\n" +
                    "        case when dy.ye is null and nc.ye is null then '0' when nc.ye is null then to_char(dy.ye,'fm9999990.00') when dy.ye is null then to_char(0-nc.ye,'fm9999990.00') \n" +
                    "    else to_char(dy.ye-nc.ye,'fm9999990.00') end jncye,\n" +
                    "      case when dy.hs is null and zj.hs is null then '0' when zj.hs is null then '0' when dy.hs is null then '0' else to_char(dy.hs/zj.hs*100,'fm9999990.00') end dyfgm,\n" +
                    "        case when zj.hs is null then '0' when dy.hs is null and sy.hs is null then '0' when dy.hs is null then to_char(0-sy.hs/zj.hs*100,'fm9999990.00')\n" +
                    "          when sy.hs is null then to_char(dy.hs/zj.hs*100,'fm9999990.00') else to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') end jsyfgm,\n" +
                    "         case when zj.hs is null then '0' when dy.hs is null and nc.hs is null then '0' when dy.hs is null then to_char(0-nc.hs/zj.hs*100,'fm9999990.00')\n" +
                    "          when nc.hs is null then to_char(dy.hs/zj.hs*100,'fm9999990.00') else to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm9999990.00') end jncfgm\n" +
                    " from(\n" +
                    "select code code,count(code) hs,sum(ye) ye from(\n" +
                    "select nh.deptcode code,round(count(wg.ap)) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) nh\n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.f)=upper(wg.AP) and nh.deptcode=(case when wg.bh='30100' then '28330100-xd' else '283'||wg.bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode,wg.ap) group by code) sy left join hzdb.excel_tab_28 dept on sy.code=dept.b\n" +
                    "left join \n" +
                    "(select code code,count(code) hs,sum(ye) ye from(\n" +
                    "select nh.deptcode code,round(count(wg.ap)) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) nh\n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.f)=upper(wg.AP) and nh.deptcode=(case when wg.bh='30100' then '28330100-xd' else '283'||wg.bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode,wg.ap) group by code) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select code code,count(code) hs,sum(ye) ye from(\n" +
                    "select nh.deptcode code,round(count(wg.ap)) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) nh\n" +
                    "left join hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" wg on upper(nh.f)=upper(wg.AP) and nh.deptcode=(case when wg.bh='30100' then '28330100-xd' else '283'||wg.bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode,wg.ap) group by code) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,count(deptcode) hs from (select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.ye),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,to_char(sum(dy.hs)) dyhs,to_char(sum(dy.hs)-sum(sy.hs)) jsyhs,to_char(sum(dy.hs)-sum(nc.hs)) jnchs,\n" +
                    "to_char(sum(dy.ye)) dyye,to_char(sum(dy.ye)-sum(sy.ye),'fm9999990.00') jsyye,to_char(sum(dy.ye)-sum(nc.ye),'fm9999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select code code,count(code) hs,sum(ye) ye from(\n" +
                    "select nh.deptcode code,round(count(wg.ap)) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) nh\n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.f)=upper(wg.AP) and nh.deptcode=(case when wg.bh='30100' then '28330100-xd' else '283'||wg.bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode,wg.ap) group by code) sy\n" +
                    "left join \n" +
                    "(select code code,count(code) hs,sum(ye) ye from(\n" +
                    "select nh.deptcode code,round(count(wg.ap)) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) nh\n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.f)=upper(wg.AP) and nh.deptcode=(case when wg.bh='30100' then '28330100-xd' else '283'||wg.bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode,wg.ap) group by code) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select code code,count(code) hs,sum(ye) ye from(\n" +
                    "select nh.deptcode code,round(count(wg.ap)) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) nh\n" +
                    "left join hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" wg on upper(nh.f)=upper(wg.AP) and nh.deptcode=(case when wg.bh='30100' then '28330100-xd' else '283'||wg.bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode,wg.ap) group by code) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,count(deptcode) hs from (select b.b deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)\n");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+16);
                    billCellPanel.setBackground("0,153,153",i+4,16);
                }
            }
        }catch (Exception e){

        }
    }
    /**
     * zzl
     * 存款
     * "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+"
     * "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+"
     * DateUIUtil.getYearYmTime()
     */
    public void getCkDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select replace(tab.a,'支行',''),sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,dy.hs dyhs,dy.hs-sy.hs hsjsy,dy.hs-nc.hs hsjnc,dy.num dynum,to_char(dy.num-sy.num,'fm999999990.00') yejsy,to_char(dy.num-nc.num,'fm99999990.00') ysjnc,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,to_char((dy.hs/zj.hs*100)-(nc.hs/zj.hs*100),'fm999990.00') fgmjnc from(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.i i from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) ry\n" +
                    " left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.i)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) sy left join hzdb.excel_tab_28 tab on sy.code=tab.c\n" +
                    "left join(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.i i from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) ry\n" +
                    " left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.i)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.i i from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) ry\n" +
                    " left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.i)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select deptcode code,count(deptcode) hs from (select b.c deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) group by deptcode) zj on sy.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99990.00') fgmjnc from(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.i i from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) ry\n" +
                    " left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.i)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) sy\n" +
                    "left join(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.i i from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) ry\n" +
                    " left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.i)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.i i from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) ry\n" +
                    " left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.i)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select deptcode code,count(deptcode) hs from (select b.c deptcode,a.i f from hzdb.s_qwyt_txry_202012 a left join hzdb.excel_tab_28 b on a.g||'支行'=b.a) group by deptcode) zj on sy.code=zj.code)\n");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+3);
                    billCellPanel.setBackground("255,102,255",i+4,3);
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
