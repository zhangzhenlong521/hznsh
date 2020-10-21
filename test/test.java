import org.apache.commons.lang.StringUtils;

import java.io.*;

public class test {
    public static void main(String[] args) {
//        try{
//            String line;
//            File file = new File("C:\\Users\\17744\\Desktop\\2020年8月31日贷款台账（调整后以此为准）.csv");
//            BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));
//            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "utf-8"), 5 * 1024 * 1024);// 用5M的缓冲读取文本文件
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
            System.out.println("是身份证号");
        }else{
            System.out.println("不是身份证号");
        }

    }
}
