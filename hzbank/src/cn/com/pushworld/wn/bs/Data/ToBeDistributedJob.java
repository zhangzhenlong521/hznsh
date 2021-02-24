package cn.com.pushworld.wn.bs.Data;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.TBUtil;
import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * ToBeDistributedJob
 * zzl
 *
 * @author Dragon
 * @date 2021/1/22
 */
public class ToBeDistributedJob implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    HashMap<String,String> ckmap=new HashMap<String, String>();
    HashMap<String,String> dkmap=new HashMap<String, String>();
    TBUtil tbUtil=new TBUtil();
    @Override
    public String run() throws Exception {
        try{

            ckmap=dmo.getHashMapBySQLByDS(null,"select c,b from hzdb.excel_tab_28");
            dkmap=dmo.getHashMapBySQLByDS(null,"select b,c from hzdb.excel_tab_28");
            //dk 贷款表
            String dk=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.s_loan_dk_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
            //活期存款
            String ck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
            //定期存款
            String dqck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'\n");
            //存款客户主表
            String ckkhxx=dmo.getStringValueByDS("hzbank","select distinct(load_dates) from  hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" where load_dates='"+getQYTTime()+"'");
            //对公活期存款
            String dgck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.a_agr_dep_acct_ent_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
            //对公定期存款
            String dgdqck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.a_agr_dep_acct_ent_fx_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'\n");
            //记录有没有执行
            String dkstate=dmo.getStringValueByDS(null,"select dkdates from hzdb.s_count_cdk where dkdates='"+getQYTTime()+"'");
            String ckstate=dmo.getStringValueByDS(null,"select ckdates from hzdb.s_count_cdk where ckdates='"+getQYTTime()+"'");
            if(ck==null || dqck==null || ckkhxx==null || dgck==null || dgdqck==null){

            }else{
                if(ckstate==null){
                    depositDate(ckmap);
                    dGdepositDate(ckmap);
                    dmo.executeUpdateByDS(null,"update hzdb.s_count_cdk set ckdates='"+getQYTTime()+"'");
                }
            }
            if(dk==null){

            }else{
                if(dkstate==null){
                    loanDate(dkmap);
                    dmo.executeUpdateByDS(null,"update hzdb.s_count_cdk set dkdates='"+getQYTTime()+"'");
                }
            }
            deleteTable();
            return "成功";
        }catch (Exception e){
            e.printStackTrace();
            return "失败";
        }
    }

    private void deleteTable() {
        try{
                String count=dmo.getStringValueByDS(null,"select count(*) from hzdb.s_loan_khxx_202001 where G is null");
            if(Integer.parseInt(count)>0){
                dmo.executeUpdateByDS(null,"create table hzdb.s_loan_khxx_202001_copy as select * from hzdb.s_loan_khxx_202001 where G is not null");
                dmo.executeUpdateByDS(null," drop table hzdb.s_loan_khxx_202001");
                dmo.executeUpdateByDS(null,"create table hzdb.s_loan_khxx_202001 as select * from hzdb.s_loan_khxx_202001_copy where G is not null");
                dmo.executeUpdateByDS(null,"drop table hzdb.s_loan_khxx_202001_copy");
            }
        }catch (Exception e){

        }
    }

    /**
     * zzl
     * 贷款的匹配到网格
     */
    public void loanDate(HashMap map){
        for(Object key:map.keySet()){
            try{
                String [][] data=dmo.getStringArrayByDS(null,"select * from(\n" +
                        "select case when xd_col85='30100' then '28330100-xd' else '283'||xd_col85 end xd_col85,xd_col16,xd_col2,XD_COL144 from hzbank.s_loan_dk_"+getQYDayMonth()+" group by xd_col2,xd_col85,xd_col16,XD_COL144\n" +
                        ") dk\n" +
                        "left join (select xx.*,tab.b dept from hzdb.s_loan_khxx_202001 xx left join hzdb.excel_tab_28 tab on xx.deptcode=tab.c where tab.b='"+key.toString()+"') \n" +
                        "xx on dk.xd_col85=xx.dept and upper(dk.xd_col16)=upper(xx.g) where dk.xd_col85='"+key.toString()+"' and xx.g is null");
                if(data.length>0){
                    dmo.executeUpdateByDS(null,"insert into  hzdb.s_loan_khxx_202001(A,G,J,K,deptcode)\n" +
                            "select dk.xd_col2,dk.xd_col16,'其他网格','其他网格','"+map.get(key.toString())+"' from(\n" +
                            "select case when xd_col85='30100' then '28330100-xd' else '283'||xd_col85 end xd_col85,xd_col16,xd_col2,XD_COL144 from hzbank.s_loan_dk_"+getQYDayMonth()+" group by xd_col2,xd_col85,xd_col16,XD_COL144\n" +
                            ") dk\n" +
                            "left join (select xx.*,tab.b dept from hzdb.s_loan_khxx_202001 xx left join hzdb.excel_tab_28 tab on xx.deptcode=tab.c where tab.b='"+key.toString()+"') \n" +
                            "xx on dk.xd_col85=xx.dept and upper(dk.xd_col16)=upper(xx.g) where dk.xd_col85='"+key.toString()+"' and xx.g is null\n");
                    deleteRepeat(map.get(key.toString()).toString());
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * zzl
     * 存款的匹配到网格
     */
    public void depositDate(HashMap map){
        try{
            for(Object key:map.keySet()){
                String[][] data=dmo.getStringArrayByDS(null,"select * from(\n" +
                        "select a.oact_inst_no,a.f,b.NAM_CUST_FULL,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1 from(\n" +
                        "select * from(\n" +
                        "select oact_inst_no,cust_no,sum(f) f from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" group by oact_inst_no,cust_no union all \n" +
                        "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" group by oact_inst_no,cust_no)) a left join\n" +
                        "hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" b on a.cust_no = b.COD_CUST_ID where a.oact_inst_no='"+key.toString()+"' group by a.oact_inst_no,a.f,b.NAM_CUST_FULL,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1) \n" +
                        "ck left join (select * from hzdb.s_loan_khxx_202001 where deptcode='"+key.toString()+"') xx on Upper(ck.EXTERNAL_CUSTOMER_IC)=Upper(xx.G)\n" +
                        "where xx.G is null");
                if(data.length>0){
                    dmo.executeUpdateByDS(null,"insert into  hzdb.s_loan_khxx_202001(A,G,H,J,K,deptcode)\n" +
                            "select ck.NAM_CUST_FULL,ck.EXTERNAL_CUSTOMER_IC,ck.TXT_custadr_ADD1,'其他网格','其他网格','"+key.toString()+"' from(\n" +
                            "select a.oact_inst_no,a.f,b.NAM_CUST_FULL,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1 from(\n" +
                            "select * from(\n" +
                            "select oact_inst_no,cust_no,sum(f) f from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" group by oact_inst_no,cust_no union all \n" +
                            "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" group by oact_inst_no,cust_no)) a left join\n" +
                            "hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" b on a.cust_no = b.COD_CUST_ID where a.oact_inst_no='"+key.toString()+"' group by a.oact_inst_no,a.f,b.NAM_CUST_FULL,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1) ck\n" +
                            "left join (select * from hzdb.s_loan_khxx_202001 where deptcode='"+key.toString()+"') xx on Upper(ck.EXTERNAL_CUSTOMER_IC)=Upper(xx.G)\n" +
                            "where xx.G is null");
                    deleteRepeat(key.toString());
                }
            }
        }catch (Exception e){

        }
    }
    /**
     * zzl
     * 对公存款的匹配到网格
     */
    public void dGdepositDate(HashMap map){
        try{
            for(Object key:map.keySet()){
                String[][] data=dmo.getStringArrayByDS(null,"select * from(\n" +
                        "select a.oact_inst_no,a.f,b.cod_cust_id,b.NAM_CUST_FULL,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1 from(\n" +
                        "select * from(\n" +
                        "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_ENT_SV_"+getQYDayMonth()+" group by oact_inst_no,cust_no union all \n" +
                        "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getQYDayMonth()+" group by oact_inst_no,cust_no)) a left join\n" +
                        "hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" b on a.cust_no = b.COD_CUST_ID where a.oact_inst_no='"+key.toString()+"' group by a.oact_inst_no,a.f,b.NAM_CUST_FULL,b.cod_cust_id,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1) ck\n" +
                        "left join (select * from hzdb.s_loan_khxx_202001 where deptcode='"+key.toString()+"') xx on Upper(ck.EXTERNAL_CUSTOMER_IC)=Upper(xx.G)\n" +
                        "where xx.G is null");
                if(data.length>0){
                    dmo.executeUpdateByDS(null,"insert into  hzdb.s_loan_khxx_202001(A,G,H,J,K,deptcode)\n" +
                            "select ck.NAM_CUST_FULL,ck.EXTERNAL_CUSTOMER_IC,ck.TXT_custadr_ADD1,'对公存款','对公存款','"+key.toString()+"' from(\n" +
                            "select a.oact_inst_no,a.f,b.cod_cust_id,b.NAM_CUST_FULL,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1 from(\n" +
                            "select * from(\n" +
                            "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_ENT_SV_"+getQYDayMonth()+" group by oact_inst_no,cust_no union all \n" +
                            "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getQYDayMonth()+" group by oact_inst_no,cust_no)) a left join\n" +
                            "hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" b on a.cust_no = b.COD_CUST_ID where a.oact_inst_no='"+key.toString()+"' group by a.oact_inst_no,a.f,b.NAM_CUST_FULL,b.cod_cust_id,b.EXTERNAL_CUSTOMER_IC,b.TXT_custadr_ADD1) ck\n" +
                            "left join (select * from hzdb.s_loan_khxx_202001 where deptcode='"+key.toString()+"') xx on Upper(ck.EXTERNAL_CUSTOMER_IC)=Upper(xx.G)\n" +
                            "where xx.G is null");
                    deleteRepeat(key.toString());
                }
            }
        }catch (Exception e){

        }
    }

    /**
     * zzl
     * 删除重复数据
     */
    public void deleteRepeat(String deptcode){
        try{
            String [] data=dmo.getStringArrayFirstColByDS(null,"select G from hzdb.s_loan_khxx_202001 where deptcode='"+deptcode+"' group by G having count(G)>1");
            if(data.length>0){
                dmo.executeUpdateByDS(null,"delete from hzdb.s_loan_khxx_202001 where G in(\n" +
                        "select G from hzdb.s_loan_khxx_202001 where deptcode='"+deptcode+"' group by G having count(G)>1) and rowid in(\n" +
                        "SELECT min(rowid) FROM hzdb.s_loan_khxx_202001 where deptcode='"+deptcode+"' GROUP BY G HAVING COUNT(*) > 1)");
                deleteRepeat(deptcode);
            }else{
                return;
            }
        }catch (Exception e){

        }

    }
    /**
     * 得到前一天的月份
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getQYDayMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
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
}
