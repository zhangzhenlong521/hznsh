package cn.com.pushworld.wn.bs.Data;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.bs.DepositTimingJob;

import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * zzl
 * 贷款数据清理
 */
public class DKDatacCeaning implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    @Override
    public String run() throws Exception {
        StringBuilder sb=new StringBuilder();
        StringBuilder rzsb=new StringBuilder();
        try{
            String [] column=dmo.getStringArrayFirstColByDS("hzbank","select COLUMN_NAME from user_tab_columns  WHERE TABLE_NAME='S_LOAN_DK_"+getKHMonthTime(1)+"' ORDER BY COLUMN_ID");
            for(int i=0;i<column.length;i++){
                if(i==column.length-1){
                    sb.append(column[i]);
                }else{
                    sb.append(column[i]+",");
                }
            }
            //zzl 每个月的1号将上个月的贷款数据插入到这个月
            if(getKHDQMonth().equals("01")) {
                dmo.executeUpdateByDS(null, "insert into hzbank.s_loan_dk_" + getKHMonthTime(0) + " " +
                        "(" + sb.toString() + ") select " + sb.toString() + " from hzbank.s_loan_dk_" + getKHMonthTime(1) + " where XD_COL1||BIZ_DT in(\n" +
                        "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_" + getKHMonthTime(1) + " where XD_COL7>0 group by XD_COL1)");
            }
            //zzl 根据当前贷款号最新的数据状态修改五级分类
                dmo.executeUpdateByDS(null,"MERGE INTO hzbank.s_loan_dk_"+getQYDayMonth()+" a\n" +
                    "USING (select XD_COL1,XD_COL22 from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(\n" +
                    "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" where BIZ_DT='"+getXZTime()+"' group by XD_COL1)) b\n" +
                    "ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL22=b.XD_COL22");//BIZ_DT='"+getXZTime()+"' BIZ_DT>'"+getSYMTime(1)+"'
            //zzl 有时候省联社贷款数据不会下发，只能去还款信息里找然后修改余额
//            dmo.executeUpdateByDS(null,"MERGE INTO hzbank.s_loan_dk_"+getQYDayMonth()+" a USING (" +
//                    "select hk.xd_col1 XD_COL1,hk.hkye,dk.dkye from(" +
//                    "select XD_COL1,sum(XD_COL5) hkye from hzbank.s_loan_hk where XD_COL1||XD_COL4||biz_dt " +
//                    "in(select XD_COL1||max(XD_COL4)||max(biz_dt) from hzbank.s_loan_hk group by XD_COL1) group by XD_COL1) " +
//                    "hk left join(select XD_COL1,XD_COL7 dkye,XD_COL22 from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT " +
//                    "in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" group by XD_COL1) " +
//                    "and XD_COL7>0) dk on hk.xd_col1=dk.xd_col1 where hk.hkye=dk.dkye) b " +
//                    "ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL7=0");
            dmo.executeUpdateByDS(null,"MERGE INTO hzbank.s_loan_dk_"+getQYDayMonth()+" a USING (\n" +
                    "select hk.xd_col1 XD_COL1,hk.hkye,dk.dkye from(select XD_COL1,sum(XD_COL5) hkye from hzbank.s_loan_hk where \n" +
                    "to_char(cast (cast (XD_COL4 as timestamp) as date),'yyyy-mm-dd')<='"+getDQymTime()+"' group by XD_COL1) hk left join(select XD_COL1,XD_COL7 dkye,XD_COL22 from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT \n" +
                    " in(select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" group by XD_COL1) and XD_COL7>0) dk on \n" +
                    " hk.xd_col1=dk.xd_col1 where hk.hkye>=dk.dkye) b ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL7=0");
            String [] createDate= dmo.getStringArrayFirstColByDS(null,"select CREATED from dba_objects where object_name = 'GRID_DATA_"+getQYTTime()+"' and OBJECT_TYPE='TABLE'");
            if(createDate.length>0){

            }else{
                //zzl 每天创建数据网格数据主要为了提高查询速度
                //dk 贷款表
                String dk=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.s_loan_dk_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
                //活期存款
                String ck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'");
                //定期存款
                String dqck=dmo.getStringValueByDS("hzbank","select distinct(biz_dt) from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'\n");
                //存款客户主表
                String ckkhxx=dmo.getStringValueByDS("hzbank","select distinct(load_dates) from  hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" where load_dates='"+getQYTTime()+"'");
                if(dk==null || ck==null || dqck==null || ckkhxx==null){

                }else{
                    dmo.executeUpdateByDS(null,"create table hzdb.grid_data_"+getQYTTime()+" as select wg.*,ck.oact_inst_no,ck.name,ck.ckye,dk.dkye,case when jd.jdxx is null then '否' else jd.jdxx end jdxx ,\n" +
                            "case when qny.sf is null then '否' else qny.sf end qny ,'家庭成员' num \n" +
                            "from (select wg.*,dept.B code from hzdb.S_LOAN_KHXX_202001 wg left join hzdb.excel_tab_28 dept on (case when INSTR(wg.deptcode,'-')>0  then substr(wg.deptcode,0,INSTR(deptcode,'-')-1) else wg.deptcode end)=dept.C) wg left join \n" +
                            "(select  a.oact_inst_no,b.EXTERNAL_CUSTOMER_IC name,sum(a.f) ckye from \n" +
                            "(select oact_inst_no,cust_no cust_no,sum(f) f \n" +
                            "from hzbank.a_agr_dep_acct_psn_sv_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"' group by oact_inst_no,cust_no union all \n" +
                            "select oact_inst_no,cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getQYDayMonth()+" where biz_dt='"+getQYTTime()+"'\n" +
                            "group by oact_inst_no,cust_no) a left join (select * from hzbank.S_OFCR_CI_CUSTMAST_"+getQYDayMonth()+" where load_dates='"+getQYTTime()+"')  b on a.cust_no = b.COD_CUST_ID \n" +
                            "group by a.oact_inst_no,b.EXTERNAL_CUSTOMER_IC) ck on  wg.deptcode=ck.oact_inst_no and UPPER(wg.G)=UPPER(ck.name)\n" +
                            "left join (select '283'||XD_COL85 XD_COL85,XD_COL16,sum(XD_COL7) dkye from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(\n" +
                            "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" group by XD_COL1)\n" +
                            "and XD_COL4<'"+getXZTime()+" 00:00:00' group by XD_COL85,XD_COL16) dk on wg.code=dk.XD_COL85 and UPPER(wg.G)=UPPER(dk.XD_COL16)\n" +
                            "left join(select '283'||XD_COL2 XD_COL2,XD_COL7 XD_COL7,'已建档' jdxx from hzbank.s_loan_khxx_"+getQYDayMonth()+" where to_char(cast (cast (XD_COL3 as timestamp) as date),'yyyy-mm-dd')>='"+getKHMonthTime()+"'\n" +
                            "and to_char(cast (cast (XD_COL3 as timestamp) as date),'yyyy-mm-dd')<='"+getYmMonthTime()+"' and XD_COL32 >0 and XD_COL158>0 \n" +
                            "and  XD_COL10 in ('特优','特惠级','一般','优秀','较好') group by XD_COL2,XD_COL7) jd on wg.code=jd.XD_COL2 and UPPER(wg.G)=UPPER(jd.XD_COL7)\n" +
                            "left join (select C,F,'是' sf  from hzdb.s_loan_qnyyx_"+getSMonth()+" group by C,F having sum(REPLACE(N,',',''))+sum(REPLACE(O,',',''))+sum(REPLACE(P,',',''))+sum(REPLACE(Q,',',''))+sum(REPLACE(R,',',''))+sum(REPLACE(S,',',''))+sum(REPLACE(T,',',''))+sum(REPLACE(U,',',''))\n" +
                            "+sum(REPLACE(V,',',''))+sum(REPLACE(W,',',''))+sum(REPLACE(X,',',''))+sum(REPLACE(Y,',',''))+sum(REPLACE(Z,',',''))+sum(REPLACE(AA,',',''))+sum(REPLACE(AB,',',''))+sum(REPLACE(AC,',',''))+sum(REPLACE(AD,',',''))>0\n) qny\n" +
                            "on wg.deptcode=qny.c and UPPER(wg.G)=UPPER(qny.f)");
                }
            }
            return "清洗贷款数据成功";
        }catch (Exception e){
            e.printStackTrace();
            return "清洗贷款数据失败"+e.getStackTrace();
        }
    }
    public static void main(String[] args) {
        DKDatacCeaning a = new DKDatacCeaning();
        String inputParam = a.getDQymTime();
        System.out.println(">>>>>>>>>>>>>>" + inputParam);
    }
    /**
     * 得到实际的日期月初日期zzl
     *
     * @return
     */
    public String getYCTime(int a) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -a);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(otherDate);
    }
    /**
     * 得到前一天的月初日期
     *
     * @return
     */
    public String getKHMonthTime(int a) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -a);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
        return dateFormat.format(otherDate);
    }
    /**
     * 得到前一天的月初日期
     *
     * @return
     */
    public String getKHMonthTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, Calendar.DATE - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(otherDate);
    }

    /**
     * 得到前一天的月末日期
     *
     * @return
     */
    public String getYmMonthTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, Calendar.DATE - 1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(otherDate);
    }
    /**
     * 得到当前月末日期
     *
     * @return
     */
    public String getDQymTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DATE, Calendar.DATE - 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(otherDate);
    }
    /**
     * 得到现在的日期
     *
     * @return
     */
    public String getXZTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * 当前考核日期 zzl前一天
     *cal.getActualMinimum(Calendar.DATE)
     * @return
     */
    public String getKHDQMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
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
    /**
     * 得到上个月的日期
     *
     * @return
     */
    public String getSMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        cal.add(Calendar.MONTH, - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
}
