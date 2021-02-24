package cn.com.pushworld.wn.bs.Data;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.HashVO;

public class YktwclCount implements WLTJobIFC{
	private CommDMO dmo = new CommDMO();
	
	@Override
	public String run() throws Exception {
			HashVO[] vos = dmo.getHashVoArrayByDS(null,"select * from hzbank.t_hnyzt_dis_"+getQYlTTime()+" where load_dates='"+getQYTTime()+"' and rownum<=100");
        	if(vos.length<=0){
        		return getQYTTime()+"一卡通数据没有正常下发或正在处理";
        	}else{
        	    String [] column=dmo.getStringArrayFirstColByDS(null,"select COLUMN_NAME from user_tab_columns  WHERE TABLE_NAME='hzdb.hz_ykt_rate_"+getQYTTime()+"' ORDER BY COLUMN_ID");
        	    if(column.length>0){
        	    	return getQYTTime()+"数据已存在于库中";
        	    }else{
        		dmo.executeUpdateByDS(null,"create table hzbank.hz_ykt_rate_"+getQYTTime()+" as select a.a name,b.* from (select * from hzdb.excel_tab_164) a,(select * from hzbank.t_hnyzt_dis_"+getQYlTTime() +" where load_dates='"+getQYTTime()+"')b where (a.b||a.c)=(b.a003||b.a004)");
        		dmo.executeUpdateByDS(null,"create or replace view v_hz_ykt_rate_"+getQYTTime()+" as (select a.name,a.c as yktzyk,b.c as yktptk,c.c as ykahfs,d.c as yktcount from " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A014='是' and A015='是' and A020='是' and A021='是' group by name) a left join " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A014='是' and A015='是' and A020='是' and A021='否' group by name) b on a.name=b.name left join " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A018='是' group by name) c on b.name=c.name left join " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" group by name) d on c.name=d.name)");
    		return getQYTTime()+"一卡通数据处理成功";
        	    }
        	
        }
		
	}

    public String getQYTTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }
    
    public String getQYlTTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        Date d = new Date();
        cal.setTime(d);
        int day = cal.get(Calendar.DATE);
        cal.set(Calendar.DATE, day - 1);
        String lastDate = format.format(cal.getTime());
        return lastDate;
    }

}
