import cn.com.infostrategy.bs.common.CommDMO;

/**
 * WgDataUpdate
 * zzl
 *
 * @author Dragon
 * @date 2021/1/27
 */
public class WgDataUpdate {
    public static void main(String[] args) {
        CommDMO dmo=new CommDMO();
        try{
            String [][] data=dmo.getStringArrayByDS(null,"select c,d,f from hzdb.EXCEL_TAB_85 where 1=1  and (PARENTID='2')");
            for(int i=0;i<data.length;i++){
                System.out.println(">>>>>>>>>>>>>>"+data[i][0]);
            }
        }catch (Exception e){

        }
    }
}
