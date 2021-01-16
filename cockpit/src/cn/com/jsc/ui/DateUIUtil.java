package cn.com.jsc.ui;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * zzl
 * ���ڵĹ�����
 */
public class DateUIUtil implements Serializable {
    /**
     * zzl
     * @param a  ���� ǰһ�������
     * @param pattern ��ʽ���� yyyyMMdd
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
     * zzl ȡ�����µ�ʱ��
     * @param a  ����
     * @param pattern ��ʽ���� yyyyMMdd
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
     * zzl �õ�ǰһ����·�
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
     * �õ�ǰһ�������
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
        String str=DateUIUtil.getSDateMonth(1,"yyyy��MM��dd��");
        System.out.print(10%5);
    }
}
