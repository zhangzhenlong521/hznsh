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
 * ���������ڷ������ͳ�Ʊ�
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
            billCellPanel.setValueAt("dates","��������:"+dates);
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
                    "select dept.name,sy.hs,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy left join hzdb.pub_corp_dept dept on sy.code=dept.code\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_qnyyx_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)");
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
     * E��ǩԼ
     */
    public void getQnELoanDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select dept.name,sy.hs,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy left join hzdb.pub_corp_dept dept on sy.code=dept.code\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(0,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getSDateMonth(1,"yyyyMM")+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.s_loan_esign_"+DateUIUtil.getYearMonth()+" wg on upper(nh.g)=upper(wg.f)\n" +
                    "where wg.f is not null group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)");
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
     * ����
     */
    public void getLoanDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select dept.name,sy.hs,sy.ye,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "dy.ye dyye,to_char(dy.ye-sy.ye,'fm9999990.00') jsyye,to_char(dy.ye-nc.ye,'fm9999990.00') jncye,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.dkye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by nh.deptcode) sy left join hzdb.pub_corp_dept dept on sy.code=dept.code\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.dkye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join "+tablename+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.dkye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.ye),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "sum(dy.ye) dyye,to_char(sum(dy.ye)-sum(sy.ye),'fm9999990.00') jsyye,to_char(sum(dy.ye)-sum(nc.ye),'fm9999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.dkye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.dkye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join "+tablename+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.dkye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.dkye>0 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)");
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
     * ���
     */
    public void getCkDate(){
        try{
            String date[][]=UIUtil.getStringArrayByDS(null,"select * from(\n" +
                    "select * from(\n" +
                    "select dept.name,sy.hs,sy.ye,to_char(sy.hs/zj.hs*100,'fm999990.00') syfgm,dy.hs dyhs,dy.hs-sy.hs jsyhs,dy.hs-nc.hs jnchs,\n" +
                    "dy.ye dyye,to_char(dy.ye-sy.ye,'fm9999990.00') jsyye,to_char(dy.ye-nc.ye,'fm9999990.00') jncye,\n" +
                    "to_char(dy.hs/zj.hs*100,'fm9999990.00') dyfgm,to_char(dy.hs/zj.hs*100-sy.hs/zj.hs*100,'fm9999990.00') jsyfgm,to_char(dy.hs/zj.hs*100-nc.hs/zj.hs*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) sy left join hzdb.pub_corp_dept dept on sy.code=dept.code\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join "+tablename+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null) order by to_number(dyfgm) desc)\n" +
                    "union all\n" +
                    "(select '',sum(sy.hs),sum(sy.ye),to_char(sum(sy.hs)/sum(zj.hs)*100,'fm999990.00') syfgm,sum(dy.hs) dyhs,sum(dy.hs)-sum(sy.hs) jsyhs,sum(dy.hs)-sum(nc.hs) jnchs,\n" +
                    "sum(dy.ye) dyye,to_char(sum(dy.ye)-sum(sy.ye),'fm9999990.00') jsyye,to_char(sum(dy.ye)-sum(nc.ye),'fm9999990.00') jncye,\n" +
                    "to_char(sum(dy.hs)/sum(zj.hs)*100,'fm9999990.00') dyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(sy.hs)/sum(zj.hs)*100,'fm9999990.00') jsyfgm,to_char(sum(dy.hs)/sum(zj.hs)*100-sum(nc.hs)/sum(zj.hs)*100,'fm99999990.00') jncfgm from(\n" +
                    "select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getSymDateMonth()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) sy\n" +
                    "left join \n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join "+tablename+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) dy on sy.code=dy.code\n" +
                    "left join\n" +
                    "(select nh.deptcode code,round(count(nh.deptcode)/4) hs,round(sum(wg.ckye)/10000,2) ye from(\n" +
                    "select deptcode,G from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯') nh\n" +
                    "left join hzdb.Grid_Data_"+DateUIUtil.getYearYmTime()+" wg on upper(nh.g)=upper(wg.g)\n" +
                    "where wg.ckye>1000 group by nh.deptcode) nc on sy.code=nc.code\n" +
                    "left join\n" +
                    "(select deptcode code,round(count(deptcode)/4) hs from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode) zj\n" +
                    "on sy.code=zj.code where sy.code is not null)");
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
     * �ܳ��������
     */
    public void getCount(){
        try{
            String [][]data=UIUtil.getStringArrayByDS(null,"select b.name,a.num from(\n" +
                    "select deptcode,round(count(deptcode)/4) num from hzdb.s_loan_khxx_202001 where B<>'ũҵ���廧��' and B <>'ũ��ũҵ����' and B <>'ũ��' and B <>'ũ��ũҵ����' and B<>'ũҵ��ͥ����' and B<>'ũ�徭����֯' group by deptcode\n" +
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
     * �õ�ָ�����·ݵ�����
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
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy��MM��dd��");
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
     * �õ�ǰһ�������
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
     * �õ�ǰ2�������
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
