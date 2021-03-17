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
import java.util.HashMap;

/**
 * TableGtGshStateWKPanel
 * fj
 * 个体工商户金融服务情况表
 * @author Dragon
 * @date 2021/2/26
 */
public class TableGtGshStateWKPanel extends AbstractWorkPanel implements ActionListener {
    private BillCellPanel billCellPanel=null;
    private String tablename;
    private String dates=null;
    private HashMap<String,String> map=new HashMap();
    private JButton btn=null;
    private String selectDate = null;
    @Override
    public void initialize() {
        billCellPanel=new BillCellPanel("s_table_gtgsh" ,null, null,true, false,true,true);
        try{
            getTablename();
            map=UIUtil.getHashMapBySQLByDS(null,"select a.cellvalue,b.cellvalue from(SELECT * FROM hzdb.pub_billcelltemplet_d where templet_h_id='707' and cellrow>=4 and cellcol='1')a\n" +
                    "left join(SELECT * FROM hzdb.pub_billcelltemplet_d where templet_h_id='707' and cellrow>=4 and cellcol='2')b on a.cellrow=b.cellrow\n");
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
                    "select ry.d code,count(ry.d) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" qny on upper(ry.c)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.d) sy\n" +
                    "left join(\n" +
                    "select d code,count(d) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.d code,count(ry.d) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" qny on upper(ry.c)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.d) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.d code,count(ry.d) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.c)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.d) nc on sy.code=nc.code\n" +
                    ") order by to_number(byfgm) desc)\n" +
                    "union all(\n" +
                    "select '',sum(sy.hs) syhs,to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999990.00') byfgm,\n" +
                    "to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm9999990.00') jncfgm from(\n" +
                    "select ry.d code,count(ry.d) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" qny on upper(ry.c)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.d) sy\n" +
                    "left join(\n" +
                    "select d code,count(d) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on sy.code=zj.code\n" +
                    "left join(\n" +
                    "select ry.d code,count(ry.d) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" qny on upper(ry.c)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.d) dy on sy.code=dy.code\n" +
                    "left join (\n" +
                    "select ry.d code,count(ry.d) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" qny on upper(ry.c)=upper(qny.f) \n" +
                    "where qny.f is not null group by ry.d) nc on sy.code=nc.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+38);
                    billCellPanel.setBackground("255,0,255",i+4,44);
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
                    "select ry.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(ry.c)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.d) sy\n" +
                    "left join\n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(ry.c)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.d) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(ry.c)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.d) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select d code,count(d) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on sy.code=zj.code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs) syhs,to_char(sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,\n" +
                    "to_char((sum(dy.hs)/sum(zj.hs)*100)-(sum(sy.hs)/sum(zj.hs)*100),'fm9999990.00') jsyfgm,to_char((sum(dy.hs)/sum(zj.hs)*100)-(sum(nc.hs)/sum(zj.hs)*100),'fm9999990.00') jncfgm from(\n" +
                    "select ry.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(ry.c)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.d) sy\n" +
                    "left join\n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(ry.c)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.d) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.d code,count(wg.g) hs from hzdb.s_qwyt_gtgsh_202012 ry left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(ry.c)=upper(wg.f)\n" +
                    "where wg.f is not null group by ry.d) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select d code,count(d) hs from hzdb.s_qwyt_gtgsh_202012 group by d) zj on sy.code=zj.code)\n");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+29);
                    billCellPanel.setBackground("153,102,255",i+4,35);
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
     * zzl
     * 存款
     * (selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))
     * (selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))
     * DateUIUtil.getYearYmTime()
     */
    public void getCkDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select replace(b.a,'支行',''),a.hs,a.num,a.dyfgm,a.dyhs,a.hsjsy,a.hsjnc,a.dynum,a.yejsy,a.ysjnc,a.fgm,a.fgmjsy,a.fgmjnc from(\n" +
                    "select * from(\n" +
                    "select * from(\n" +
                    "select sy.code,sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,dy.hs dyhs,dy.hs-sy.hs hsjsy,dy.hs-nc.hs hsjnc,dy.num dynum,to_char(dy.num-sy.num,'fm999999990.00') yejsy,to_char(dy.num-nc.num,'fm99999990.00') ysjnc,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99990.00') fgmjnc from(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.c c from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.c)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) sy\n" +
                    "left join(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.c c from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) ry \n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.c)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.c c from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.c)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select deptcode code,count(f) hs from (select b.c deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) group by deptcode) zj on sy.code=zj.code) order by fgm desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99990.00') fgmjnc from(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.c c from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(ry.c)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) sy\n" +
                    "left join(\n" +
                    "select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.c c from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) ry \n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(ry.c)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select ry.deptcode code,count(wg.g) hs,round(sum(wg.ckye)/10000,2) num from (select b.c deptcode,a.c c from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(ry.c)=upper(wg.g) and ry.deptcode=wg.deptcode\n" +
                    "where wg.ckye>1000 group by ry.deptcode) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select deptcode code,count(f) hs from (select b.c deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then a.d else a.d||'支行' end)=b.a) group by deptcode) zj on sy.code=zj.code)\n" +
                    ") a left join hzdb.excel_tab_28 b on a.code=b.c");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+3);
                    billCellPanel.setBackground("0,140,255",i+4,13);
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
                    "select replace(tab.a,'支行',''),sy.hs,sy.num,to_char(round(sy.hs/zj.hs*100,2),'fm999999990.00') dyfgm,dy.hs dyhs,dy.hs-sy.hs hsjsy,dy.hs-nc.hs hsjnc,dy.num dynum,to_char(dy.num-sy.num,'fm999999990.00') yejsy,to_char(dy.num-nc.num,'fm99999990.00') ysjnc,\n" +
                    "to_char(round(dy.hs/zj.hs*100,2),'fm999999990.00') fgm,to_char(round(dy.hs/zj.hs*100,2)-round(sy.hs/zj.hs*100,2),'fm9999999990.00') fgmjsy,round(dy.hs/zj.hs*100,2) fgmjnc from(\n" +
                    "select code code,count(code) hs,sum(num) num from(\n" +
                    "select ry.deptcode code,count(ry.f) hs,round(sum(replace(wg.k,',',''))/10000,2) num from (select b.b deptcode,a.c f from hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(ry.f)=upper(wg.AP) and ry.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by ry.deptcode,ry.f) group by code) sy left join hzdb.excel_tab_28 tab on sy.code=tab.b\n" +
                    "left join(\n" +
                    "select code code,count(code) hs,sum(num) num from(\n" +
                    "select ry.deptcode code,count(ry.f) hs,round(sum(replace(wg.k,',',''))/10000,2) num from (select b.b deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(ry.f)=upper(wg.AP) and ry.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by ry.deptcode,ry.f) group by code) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select code code,count(code) hs,sum(num) num from(\n" +
                    "select ry.deptcode code,count(ry.f) hs,round(sum(replace(wg.k,',',''))/10000,2) num from (select b.b deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" wg on upper(ry.f)=upper(wg.AP) and ry.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by ry.deptcode,ry.f) group by code) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select deptcode code,count(f) hs from (select b.b deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) group by deptcode) zj on sy.code=zj.code) order by to_number(fgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.num),to_char(round(sum(sy.hs)/sum(zj.hs)*100,2),'fm999999990.00') dyfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) hsjsy,sum(dy.hs)-sum(nc.hs) hsjnc,sum(dy.num) dynum,to_char(sum(dy.num)-sum(sy.num),'fm9999999990.00') yejsy,\n" +
                    "to_char(sum(dy.num)-sum(nc.num),'fm99999999990.00') ysjnc,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2),'fm9999999990.00') fgm,to_char(round(sum(dy.hs)/sum(zj.hs)*100,2)-round(sum(sy.hs)/sum(zj.hs)*100,2),'fm99999999990.00') fgmjsy,round(sum(dy.hs)/sum(zj.hs)*100,2) fgmjnc from(\n" +
                    "select code code,count(code) hs,sum(num) num from(\n" +
                    "select ry.deptcode code,count(ry.f) hs,round(sum(replace(wg.k,',',''))/10000,2) num from (select b.b deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(ry.f)=upper(wg.AP) and ry.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by ry.deptcode,ry.f) group by code) sy\n" +
                    "left join(\n" +
                    "select code code,count(code) hs,sum(num) num from(\n" +
                    "select ry.deptcode code,count(ry.f) hs,round(sum(replace(wg.k,',',''))/10000,2) num from (select b.b deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(ry.f)=upper(wg.AP) and ry.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by ry.deptcode,ry.f) group by code) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select code code,count(code) hs,sum(num) num from(\n" +
                    "select ry.deptcode code,count(ry.f) hs,round(sum(replace(wg.k,',',''))/10000,2) num from (select b.b deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) ry \n" +
                    "left join hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" wg on upper(ry.f)=upper(wg.AP) and ry.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by ry.deptcode,ry.f) group by code) nc on sy.code=nc.code\n" +
                    "left join(\n" +
                    "select deptcode code,count(f) hs from (select b.b deptcode,a.c f from   hzdb.s_qwyt_gtgsh_202012 a left join hzdb.excel_tab_28 b on (case when a.d='营业部' then '信贷部' else a.d||'支行' end)=b.a) group by deptcode) zj on sy.code=zj.code)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+16);
                    billCellPanel.setBackground("255,153,153",i+4,26);
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
