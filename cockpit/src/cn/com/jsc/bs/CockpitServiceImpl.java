package cn.com.jsc.bs;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.jsc.ui.CockpitServiceIfc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CockpitServiceImpl implements CockpitServiceIfc {
    CommDMO dmo=new CommDMO();

    /**
     * zzl 各项存款统计
     * @return
     */
    @Override
    public String getCkStatistical() {
        String data=null;
        try {
            data= dmo.getStringValueByDS(null,"select dyck.dy dyye,dyck.dy-ncck.nc ncye from(\n" +
                    "select '1' a,ROUND(sum(F)/100000000,2) dy from(\n" +
                    "select sum(f) f \n" +
                    "from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='20201111' and f>0 \n" +
                    "union all \n" +
                    "select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='20201111' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='20201111' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='20201111' and acct_bal>0))dyck\n" +
                    "left join(\n" +
                    "select '1' a,ROUND(sum(F)/100000000,2) nc from(\n" +
                    "select sum(f) f \n" +
                    "from hzbank.a_agr_dep_acct_psn_sv_\"+getqytMonth()+\" where biz_dt='20191231' and f>0 \n" +
                    "union all \n" +
                    "select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_20191231 where biz_dt='20191231' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_20191231 where biz_dt='20191231' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_20191231 where biz_dt='20191231' and acct_bal>0)) ncck\n" +
                    "on dyck.a=ncck.a");
        } catch (Exception e) {
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
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.YEAR, -1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }

    public static void main(String[] args) {
        CockpitServiceImpl cp=new CockpitServiceImpl();
        System.out.print(cp.getYearYmTime());
    }

}
