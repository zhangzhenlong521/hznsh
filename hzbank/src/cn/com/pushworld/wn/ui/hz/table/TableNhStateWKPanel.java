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
            btn=billCellPanel.getBtn_selectData();
            btn.addActionListener(this);
        }catch (Exception e){

        }
        this.add(billCellPanel);
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
    public void getQnyDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select code,sum(hs),to_char(sum(syfgm),'fm9990.00'),sum(dyhs),sum(jsyhs),sum(jnchs),to_char(sum(dyfgm),'fm99990.00') dyfgm,to_char(sum(jsyfgm),'fm9990.00'),to_char(sum(jncfgm),'fm99990.00') from(\n" +
                    "select dept.name code,sy.hs,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy left join hzdb.pub_corp_dept dept on sy.code=dept.code\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select case when deptcode='2830018' then '2830017' else deptcode end code,round(count(deptcode)/4) hs from hzdb.s_qwyt_nhjr_202012  group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) group by code )order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select case when deptcode='2830018' then '2830017' else deptcode end code,round(count(deptcode)/4) hs from hzdb.s_qwyt_nhjr_202012  group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)\n");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+38);
                    billCellPanel.setBackground("255,153,219",i+4,44);
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
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(select * from(\n" +
                    "select code,sum(hs),to_char(sum(syfgm),'fm9990.00'),sum(dyhs),sum(jsyhs),sum(jnchs),to_char(sum(dyfgm),'fm99990.00') dyfgm,to_char(sum(jsyfgm),'fm9990.00'),to_char(sum(jncfgm),'fm99990.00') from(\n" +
                    "select dept.name code,sy.hs,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy left join hzdb.pub_corp_dept dept on sy.code=dept.code\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select case when deptcode='2830018' then '2830017' else deptcode end code,round(count(deptcode)/4) hs from hzdb.s_qwyt_nhjr_202012  group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) group by code) order by to_number(dyfgm) desc)" +
                    "union all\n" +
                    "(select '',sum(sy.hs),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_esign_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select case when deptcode='2830018' then '2830017' else deptcode end deptcode,G  from hzdb.s_qwyt_nhjr_202012 ) nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select case when deptcode='2830018' then '2830017' else deptcode end code,round(count(deptcode)/4) hs from hzdb.s_qwyt_nhjr_202012  group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+29);
                    billCellPanel.setBackground("51,255,255",i+4,35);
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
                    "select code,sum(hs) hs,sum(ye) ye,to_char(sum(syfgm),'fm999990.00') syfgm,sum(dyhs) dyhs,sum(jsyhs) jsyhs,sum(jnchs) jnchs,sum(dyye) dyye,to_char(sum(jsyye),'fm99990.00') jsyye,to_char(sum(jncye),'fm99990.00') jncye,to_char(sum(dyfgm),'fm999990.00') dyfgm,to_char(sum(jsyfgm),'fm9990.00') jsyfgm,to_char(sum(jncfgm),'fm99990.00') jncfgm from(\n" +
                    "select dept.a code,sy.hs,sy.ye,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "dy.ye dyye,to_char(dy.ye-sy.ye,'fm9999990.00') jsyye,to_char(dy.ye-nc.ye,'fm9999990.00') jncye,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) nh\n" +
                    "left join  hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.g)=upper(wg.AP) and nh.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode) sy left join hzdb.excel_tab_28 dept on sy.code=dept.b\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) nh\n" +
                    "left join  hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.g)=upper(wg.AP) and nh.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) nh\n" +
                    "left join hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.AP) and nh.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(g)/4) hs from(select case when b.b='28330100' then '28330100-xd' else b.b end deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) group by code order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.ye),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "sum(dy.ye) dyye,to_char(sum(dy.ye)-sum(sy.ye),'fm9999990.00') jsyye,to_char(sum(dy.ye)-sum(nc.ye),'fm9999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) nh\n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(1,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",1))+" wg on upper(nh.g)=upper(wg.AP) and nh.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) nh\n" +
                    "left join hzdb.s_loan_dk_"+(selectDate==null?DateUIUtil.getSDateMonth(0,"yyyyMM"):DateUIUtil.getymDateMonth(selectDate,"yyyyMM",0))+" wg on upper(nh.g)=upper(wg.AP) and nh.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(replace(wg.k,',',''))/10000,2) ye from(\n" +
                    "select b.b deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) nh\n" +
                    "left join hzdb.s_loan_dk_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.AP) and nh.deptcode=(case when bh='30100' then '28330100-xd' else '283'||bh end)\n" +
                    "where replace(wg.k,',','')>0 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(g)/4) hs from(select case when b.b='28330100' then '28330100-xd' else b.b end deptcode,a.G  from hzdb.s_qwyt_nhjr_202012 a left join hzdb.excel_tab_28 b on a.deptcode=b.c) group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+16);
                    billCellPanel.setBackground("153,153,255",i+4,26);
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
                    "select code,sum(hs) hs,sum(ye) ye,to_char(sum(syfgm),'fm999990.00') syfgm,sum(dyhs) dyhs,sum(jsyhs) jsyhs,sum(jnchs) jnchs,sum(dyye) dyye,to_char(sum(jsyye),'fm99990.00') jsyye,to_char(sum(jncye),'fm99990.00') jncye,to_char(sum(dyfgm),'fm999990.00') dyfgm,to_char(sum(jsyfgm),'fm9990.00') jsyfgm,to_char(sum(jncfgm),'fm99990.00') jncfgm from(\n" +
                    "select case when dept.name='野马川支行新街分理处' then '野马川支行' else dept.name end code,sy.hs,sy.ye,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "dy.ye dyye,to_char(dy.ye-sy.ye,'fm9999990.00') jsyye,to_char(dy.ye-nc.ye,'fm9999990.00') jncye,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G  from hzdb.s_qwyt_nhjr_202012) nh\n" +
                    "left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) sy left join hzdb.pub_corp_dept dept on sy.code=dept.code\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G  from hzdb.s_qwyt_nhjr_202012) nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G  from hzdb.s_qwyt_nhjr_202012) nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_qwyt_nhjr_202012 group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) group by code) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '' code,sum(sy.hs) hs,sum(sy.ye) ye,to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "sum(dy.ye) dyye,to_char(sum(dy.ye)-sum(sy.ye),'fm9999990.00') jsyye,to_char(sum(dy.ye)-sum(nc.ye),'fm9999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G  from hzdb.s_qwyt_nhjr_202012) nh\n" +
                    "left join hzdb.Grid_Data_"+(selectDate==null?DateUIUtil.getSymDateMonth():DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",1))+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G  from hzdb.s_qwyt_nhjr_202012) nh\n" +
                    "left join "+(selectDate==null?tablename:"hzdb.Grid_Data_"+DateUIUtil.getymDateMonth(selectDate,"yyyyMMdd",0))+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G  from hzdb.s_qwyt_nhjr_202012) nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_qwyt_nhjr_202012 group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)");
            for(int i=0;i<date.length;i++){
                for(int j=0;j<date[i].length;j++){
                    billCellPanel.setValueAt(date[i][j],i+4,j+3);
                    billCellPanel.setBackground("255,153,153",i+4,13);
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
            String [][]data=UIUtil.getStringArrayByDS(null,"select * from (\n" +
                    "select * from(\n" +
                    "select code code,sum(num) num from(\n" +
                    "select case when b.name='野马川支行新街分理处' then '野马川支行' else b.name end code,a.num num from(select deptcode,round(count(deptcode)/4) num from hzdb.s_qwyt_nhjr_202012  group by deptcode)a \n" +
                    "left join hzdb.pub_corp_dept b on a.deptcode=b.code where a.num>0 order by a.num) group by code) order by num desc)\n" +
                    "union all\n" +
                    "(select '' code,sum(num) num from(select deptcode,round(count(deptcode)/4) num from hzdb.s_qwyt_nhjr_202012  group by deptcode))");
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
