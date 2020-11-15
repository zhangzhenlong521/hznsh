package cn.com.jsc.bs;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.jsc.ui.CockpitServiceIfc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CockpitServiceImpl implements CockpitServiceIfc {
    CommDMO dmo=new CommDMO();

    /**
     * zzl 各项存款统计总额 较年初
     * @return
     */
    @Override
    public String [][] getCkStatistical() {
        String [][] data=null;
        try {
            data= dmo.getStringArrayByDS(null,"select dyck.dy dyye,dyck.dy-ncck.nc ncye from(\n" +
                    "select '1' a,ROUND(sum(F)/100000000,2) dy from(\n" +
                    "select sum(f) f \n" +
                    "from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and f>0 \n" +
                    "union all \n" +
                    "select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0))dyck\n" +
                    "left join(\n" +
                    "select '1' a,ROUND(sum(F)/100000000,2) nc from(\n" +
                    "select sum(f) f \n" +
                    "from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and f>0 \n" +
                    "union all \n" +
                    "select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getYearYmTime()+" where biz_dt='"+getYearYmTime()+"' and acct_bal>0)) ncck\n" +
                    "on dyck.a=ncck.a");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKHsCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(dy.num,'fm999999990.00'),to_char(dy.num-nc.num,'fm999999990.00') from(\n" +
                    "select '1' a,count(dy.num)/10000 num from(\n" +
                    "select b.EXTERNAL_CUSTOMER_IC num from(\n" +
                    "select cust_no,sum(F) f from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' group by cust_no \n" +
                    "union all\n" +
                    "select cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' group by cust_no)\n" +
                    "a left join (select * from hzbank.S_OFCR_CI_CUSTMAST_"+getqytMonth()+" where  load_dates='"+getQYTTime()+"') b on a.cust_no = b.COD_CUST_ID group by b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) dy) dy\n" +
                    "left join(\n" +
                    "select '1' a,count(nc.num)/10000 num from(\n" +
                    "select b.EXTERNAL_CUSTOMER_IC num from(\n" +
                    "select cust_no,sum(F) f from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+" group by cust_no \n" +
                    "union all\n" +
                    "select cust_no,sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+" group by cust_no)\n" +
                    "a left join hzbank.S_OFCR_CI_CUSTMAST_"+getYearYmTime()+" b on a.cust_no = b.COD_CUST_ID group by b.EXTERNAL_CUSTOMER_IC having sum(a.f)>1000) nc)\n" +
                    "nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }

        return data;
    }

    @Override
    public String[][] getCKGeRenCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(F)/100000000 f from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(F)/100000000 f from hzbank.a_agr_dep_acct_psn_sv_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKGeRenDQCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKDgHqCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_sv_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    @Override
    public String[][] getCKDgDqCount() {
        String [][] data=null;
        try{
            data= dmo.getStringArrayByDS(null,"select to_char(ROUND(dy.f,2),'fm999999990.00'),to_char(ROUND(dy.f-nc.f,2),'fm999999990.00') from(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"') dy\n" +
                    "left join(\n" +
                    "select '1' a,sum(acct_bal)/100000000 f from hzbank.a_agr_dep_acct_ent_fx_"+getYearYmTime()+") nc on dy.a=nc.a");
        }catch (Exception e){
            e.printStackTrace();
        }
        return data;
    }

    /**
     * zzl 得到前一天的月份
     * @return
     */
    public String getqytMonth(){
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
     * 得到年初月末日期
     *
     * @return
     */
    public String getYearYmTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.DAY_OF_YEAR,cal.getActualMaximum(Calendar.DAY_OF_YEAR));//最后一天
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }

    public static void main(String[] args) {
        CockpitServiceImpl cp=new CockpitServiceImpl();
        System.out.print(cp.getYearYmTime());
    }

}
