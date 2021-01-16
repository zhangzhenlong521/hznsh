package cn.com.jsc.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * zzl
 * 日期的工具类
 */
public class DateUIUtil implements Serializable {
    /**
     * zzl
     * @param a  数字 前一天的日期
     * @param pattern 格式类型 yyyyMMdd
     * @return
     */
    public static String getDateMonth(int a,String pattern){
        String lastDate=null;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date d = new Date();
            cal.setTime(d);
            int day = cal.get(Calendar.DATE);
            cal.set(Calendar.DATE, day - a);
            lastDate = format.format(cal.getTime());
            return lastDate;
        }catch (Exception e){
            e.printStackTrace();
            return lastDate;
        }

    }
    /**
     * zzl 取得上月的时间
     * @param a  数字
     * @param pattern 格式类型 yyyyMMdd
     * @return
     */
    public static String getSDateMonth(int a,String pattern){
        String lastDate=null;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date d = new Date();
            cal.setTime(d);
            cal.add(Calendar.MONTH,-a);
            lastDate = format.format(cal.getTime());
            return lastDate;
        }catch (Exception e){
            e.printStackTrace();
            return lastDate;
        }

    }
    /**
     * zzl 得到前一天的月份
     * @return
     */
    public static String getqytMonth(){
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
    public static String getQYTTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    public static void main(String[] args) {
        String str=DateUIUtil.getSDateMonth(1,"yyyy年MM月dd天");
        System.out.print(10%5);
    }
}
