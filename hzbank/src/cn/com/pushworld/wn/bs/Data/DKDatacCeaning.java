package cn.com.pushworld.wn.bs.Data;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
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
        try{
            String [] column=dmo.getStringArrayFirstColByDS("hzbank","select COLUMN_NAME from user_tab_columns  WHERE TABLE_NAME='S_LOAN_DK_"+getKHMonthTime(1)+"' ORDER BY COLUMN_ID");
            for(int i=0;i<column.length;i++){
                if(i==column.length-1){
                    sb.append(column[i]);
                }else{
                    sb.append(column[i]+",");
                }
            }
            if(getKHDQMonth().equals("01")) {
                dmo.executeUpdateByDS(null, "insert into hzbank.s_loan_dk_" + getKHMonthTime(0) + " " +
                        "(" + sb.toString() + ") select " + sb.toString() + " from hzbank.s_loan_dk_" + getKHMonthTime(1) + " where XD_COL1||BIZ_DT in(\n" +
                        "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_" + getKHMonthTime(1) + " where XD_COL7>0 group by XD_COL1)");
                sb.setLength(0);
                sb.append("{insert into hzbank.s_loan_dk_" + getKHMonthTime(0) + "" +
                        "(" + sb.toString() + ") select " + sb.toString() +" from hzbank.s_loan_dk_" + getKHMonthTime(1) + " where XD_COL1||BIZ_DT in(" +
                        "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_" + getKHMonthTime(1) + " where XD_COL7>0 group by XD_COL1)}");
            }
                dmo.executeUpdateByDS(null,"MERGE INTO hzbank.s_loan_dk_"+getQYDayMonth()+" a\n" +
                    "USING (select XD_COL1,XD_COL22 from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(\n" +
                    "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" where BIZ_DT='"+getXZTime()+"' group by XD_COL1)) b\n" +
                    "ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL22=b.XD_COL22");//BIZ_DT='"+getXZTime()+"' BIZ_DT>'"+getSYMTime(1)+"'
            sb.setLength(0);
            sb.append("{MERGE INTO hzbank.s_loan_dk_"+getQYDayMonth()+" a" +
                    "USING (select XD_COL1,XD_COL22 from hzbank.s_loan_dk_"+getQYDayMonth()+" where XD_COL1||BIZ_DT in(" +
                    "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getQYDayMonth()+" where BIZ_DT='"+getQYTTime()+"' group by XD_COL1)) b" +
                    "ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL22=b.XD_COL22}");
            return "清洗贷款数据成功"+sb.toString();
        }catch (Exception e){
            e.printStackTrace();
            return "清洗贷款数据失败"+sb.toString();
        }
    }
    public static void main(String[] args) {
        DKDatacCeaning a = new DKDatacCeaning();
        String inputParam = a.getQYTTime();
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
     * 得到考核月日期
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
     * 得到现在的日期
     *
     * @return
     */
    public String getXZTime() {
        Date otherDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(otherDate);
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
}
