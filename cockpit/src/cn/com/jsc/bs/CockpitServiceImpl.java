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
            data= dmo.getStringValueByDS(null,"select ROUND(sum(F)/100000000,2) from(\n" +
                    "select sum(f) f \n" +
                    "from hzbank.a_agr_dep_acct_psn_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and f>0 \n" +
                    "union all \n" +
                    "select sum(acct_bal) f from hzbank.A_AGR_DEP_ACCT_PSN_FX_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_fx_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0\n" +
                    "union all\n" +
                    "select sum(acct_bal) f from hzbank.a_agr_dep_acct_ent_sv_"+getqytMonth()+" where biz_dt='"+getQYTTime()+"' and acct_bal>0)\n");
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
}
