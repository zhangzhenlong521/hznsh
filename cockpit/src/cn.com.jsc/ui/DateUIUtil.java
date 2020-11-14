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
     * @param a  数字
     * @param pattern 格式类型 yyyyMMdd
     * @return
     */
    public static String getDateMonth(int a,String pattern){
        String lastDate=null;
        try {
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int day = cal.get(Calendar.DATE);
            cal.set(Calendar.DATE, day - a);
            lastDate = format.format(cal.getTime());
            return lastDate;
        }catch (Exception e){
            e.printStackTrace();
            return lastDate;
        }

    }

    public static void main(String[] args) {
        String str=DateUIUtil.getDateMonth(1,"yyyy年MM月dd天");
        System.out.print(str);
    }
}
