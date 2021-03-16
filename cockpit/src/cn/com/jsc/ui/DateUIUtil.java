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
     * zzl 取得指定的月份日期
     * @param a  当月是0 上月是1
     * @param pattern 格式类型 yyyyMMdd
     * 默认是前一天的日期
     * @return
     */
    public static String getSDateMonth(int a,String pattern){
        String lastDate=null;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date d = new Date();
            cal.setTime(d);
            cal.add(Calendar.DATE,-1);
            cal.setTime(cal.getTime());
            cal.add(Calendar.MONTH,-a);
            lastDate = format.format(cal.getTime());
            return lastDate;
        }catch (Exception e){
            e.printStackTrace();
            return lastDate;
        }

    }
    /**
     * zzl 取得上月的月末日期
     * @param
     * @param
     * @return
     */
    public static String getSymDateMonth(){
        String lastDate=null;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date d = new Date();
            cal.setTime(d);
            cal.add(Calendar.DATE,-1);
            cal.setTime(cal.getTime());
            cal.add(Calendar.MONTH,-1);
            cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DATE));//最后一天
            lastDate = format.format(cal.getTime());
            return lastDate;
        }catch (Exception e){
            e.printStackTrace();
            return lastDate;
        }

    }
    /**
     * zzl 输入一个人时间 返回该时间的月末日期
     * @time      时间
     * @pattern  格式
     * @a       是否减月
     */
    public static String getymDateMonth(String time,String pattern,int a){
        String lastDate=null;
        try {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
            Date d = format.parse(time);
            cal.setTime(d);
            cal.add(Calendar.MONTH,-a);
            cal.set(Calendar.DATE,cal.getActualMaximum(Calendar.DATE));//最后一天
            SimpleDateFormat format2 = new SimpleDateFormat(pattern);
            lastDate = format2.format(cal.getTime());
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
    /**
     * 得到年初月末日期
     *
     * @return
     */
    public static String getYearYmTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.DAY_OF_YEAR,cal.getActualMaximum(Calendar.DAY_OF_YEAR));//最后一天
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    /**
     * 得到年初月分
     *
     * @return
     */
    public static String getYearMonth() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        cal.add(Calendar.YEAR, -1);
        cal.set(Calendar.MONTH,cal.getActualMaximum(Calendar.MONTH));//最后一天
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    public static void main(String[] args) {
        String str=DateUIUtil.getymDateMonth("2021-02","yyyyMMdd",1);
        System.out.print(str);
    }
}
