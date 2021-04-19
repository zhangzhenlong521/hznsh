package cn.com.pushworld.salary.bs.dinterface;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.TBUtil;
import org.apache.poi.ss.formula.functions.T;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * TableCopyJob
 * zzl
 *
 * @author Dragon
 * @date 2021/4/12
 */
public class TableCopyJob implements WLTJobIFC {
    private CommDMO dmo = new CommDMO();
    TBUtil tbUtil=new TBUtil();
    String table=tbUtil.getSysOptionStringValue("备份表",null);
    @Override
    public String run() throws Exception {
        StringBuffer sb=new StringBuffer();
        try{
            String [] tablenames=table.split(";");
            for(int i=0;i<tablenames.length;i++){
                dmo.executeUpdateByDS(null,"create table "+tablenames[i]+"_"+getTime()+" as select * from "+tablenames[i]);
                sb.append("备份表"+tablenames[i]+"成功<<<>>>");
            }
        }catch (Exception e){
            sb.append(e.toString());
            e.printStackTrace();
        }
        return sb.toString();
    }
    /**
     * 得到前日期
     *
     * @return
     */
    public String getTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String lastDate=format.format(new Date());
        return lastDate;
    }

    public static void main(String[] args) {
        TableCopyJob tableCopyJob=new TableCopyJob();
        System.out.println(">>>>>>>>>>"+tableCopyJob.getTime());
    }
}
