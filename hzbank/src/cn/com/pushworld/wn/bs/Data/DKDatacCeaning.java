package cn.com.pushworld.wn.bs.Data;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.pushworld.wn.bs.DepositTimingJob;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * zzl
 * ������������
 */
public class DKDatacCeaning implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    @Override
    public String run() throws Exception {
        try{
            StringBuilder sb=new StringBuilder();
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
            }
                dmo.executeUpdateByDS(null,"MERGE INTO hzbank.s_loan_dk_"+getKHMonthTime(0)+" a\n" +
                    "USING (select XD_COL1,XD_COL22 from hzbank.s_loan_dk_"+getKHMonthTime(0)+" where XD_COL1||BIZ_DT in(\n" +
                    "select XD_COL1||max(BIZ_DT) from hzbank.s_loan_dk_"+getKHMonthTime(0)+" where BIZ_DT='"+getXZTime()+"' group by XD_COL1)) b\n" +
                    "ON (a.XD_COL1=b.XD_COL1) WHEN MATCHED THEN UPDATE SET a.XD_COL22=b.XD_COL22");//BIZ_DT='"+getXZTime()+"' BIZ_DT>'"+getSYMTime(1)+"'
            return "��ϴ�������ݳɹ�";
        }catch (Exception e){
            e.printStackTrace();
            return "��ϴ��������ʧ��";
        }
    }
    public static void main(String[] args) {
        DKDatacCeaning a = new DKDatacCeaning();
        String inputParam = a.getSYMTime(1);
        System.out.println(">>>>>>>>>>>>>>" + inputParam);
    }
    /**
     * �õ�ʵ�ʵ������³�����zzl
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
     * �õ�����������
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
     * �õ�ʵ�ʵ���������ĩ����zzl
     *
     * @return
     */
    public String getSYMTime(int a) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -a);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
        Date otherDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(otherDate);
    }

    /**
     * �õ����ڵ�����
     *
     * @return
     */
    public String getXZTime() {
        Date otherDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(otherDate);
    }
    /**
     * ��ǰ�������� zzl
     *
     * @return
     */
    public String getKHDQMonth() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
        Date otherDate = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        return dateFormat.format(otherDate);
    }
}
