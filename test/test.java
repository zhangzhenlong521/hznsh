import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class test {
    public static void main(String[] args) throws ParseException {
        try{
            String line;
            File file = new File("C:\\Users\\17744\\Desktop\\������ϸ��ѯ20210930��������.csv");
            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// ��5M�Ļ����ȡ�ı��ļ�
            int a=0;
            String [] count=null;
            while (!StringUtils.isEmpty(line = reader.readLine())) {
                count=line.split("&");
                a++;
            }
            System.out.println(">>>>>>>>>>"+a);

        }catch (Exception e){

        }
//        String regex = "\\d{15}(\\d{2}[0-9xX])?";s_cmis_cus_com
//        String id = "130426198811242312";
//
//        if(id.matches(regex)){
//            System.out.println("�����֤��");
//        }else{
//            System.out.println("�������֤��");
//        }
        // ��ȡ��ǰ��ݡ��·ݡ�����
//        Calendar cale = null;
//        cale = Calendar.getInstance();
//        // ��ȡ���µ�һ������һ��
//        SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
////        String firstday, lastday;
////        // ��ȡ��ǰ�µĵ�һ��
////        cale = Calendar.getInstance();
////        cale.add(Calendar.MONTH, -1);
////        firstday = formatTemp.format(cale.getTime());
////        System.out.println(">>>>>>>>>>>>>>>"+firstday);
//        Date date1=formatTemp.parse("2020-11-04 14:59:00");
//        Date date2=new Date();
//        if(date1.getTime()>date2.getTime()){
//            System.out.println(">>>>>>>����>>>>>>>>");
//        }else{
//            System.out.println(">>>>>>>С��>>>>>>>>");
//        }
//        SimpleDateFormat formatTemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        SimpleDateFormat formatTemp2 = new SimpleDateFormat("yyyy-MM-dd");
//        Date date=new Date();
//        formatTemp2.format(date);
//        Date date1=formatTemp.parse(formatTemp2.format(date)+" 15:50:00");
//        Timer timer =new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            int a=0;
//            @Override
//            public void run() {
//               System.out.println(a++);
//            }
//        },date1,5000);//24* 3600*1000

    }
}
