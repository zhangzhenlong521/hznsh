import org.apache.commons.lang.StringUtils;

import java.io.*;

public class test {
    public static void main(String[] args) {
//        try{
//            String line;
//            File file = new File("C:\\Users\\17744\\Desktop\\2020��8��31�մ���̨�ˣ��������Դ�Ϊ׼��.csv");
//            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
//            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// ��5M�Ļ����ȡ�ı��ļ�
//            int a=0;
//            while (!StringUtils.isEmpty(line = reader.readLine())) {
//                a++;
//            }
//            System.out.println(">>>>>>>>>>"+a);
//
//        }catch (Exception e){
//
//        }
        String regex = "\\d{15}(\\d{2}[0-9xX])?";
        String id = "130426198811242312";

        if(id.matches(regex)){
            System.out.println("�����֤��");
        }else{
            System.out.println("�������֤��");
        }

    }
}
