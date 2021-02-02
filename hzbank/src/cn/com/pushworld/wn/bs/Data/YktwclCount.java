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
        String [] column=dmo.getStringArrayFirstColByDS(null,"select COLUMN_NAME from user_tab_columns  WHERE TABLE_NAME='hzdb.hz_ykt_rate_"+getQYTTime()+"' ORDER BY COLUMN_ID");
//        HashVO[] vos = dmo.getHashVoArrayByDS(null, )
        if(column.length>0){
        	return "一卡通数据表hz_ykt_rate_"+getQYTTime()+"表已存在，请删除后重新执行";
        }else{
        	dmo.executeUpdateByDS(null,"create table hzbank.hz_ykt_rate_"+getQYTTime()+" as select a.a,b.* from (select * from hzdb.excel_tab_164) a,(select * from hzbank.t_hnyzt_dis_"+getQYlTTime() +")b where (a.b||a.c)=(b.a003||b.a004)");
        		dmo.executeUpdateByDS(null,"create or replace view v_hz_ykt_rate_"+getQYTTime()+" as (select a.name,a.c as yktzyk,b.c as yktptk,c.c as ykahfs,d.c as yktcount from " +
        				"(select A name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A014='是' and A015='是' and A020='是' and A021='是' group by name) a left join " +
        				"(select A name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A014='是' and A015='是' and A020='是' and A021='否' group by name) b on a.name=b.name left join " +
        				"(select A name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A018='是' group by name) c on b.name=c.name left join " +
        				"(select A name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" group by name) d on c.name=d.name)");

        	dmo.executeUpdateByDS(null,"create table hzbank.hz_ykt_rate_"+getQYTTime()+" as(select * from (select case when substr(a.name,0,2)=substr(b.name,0,2) then b.name end name, \n" +
        			"a.A003,a.A004,a.A005,a.A006,a.A007,a.A008,a.A009,a.A010,a.A011,a.A012,a.A013,a.A014,a.A015,a.A016,a.A017,a.A018,a.A019,a.A020,a.A021,a.A022,a.A023,a.CZ_KA_FLG,a.A024,a.PK_FLG,a.NEW_CARD_NO,a.NEW_SF_YKT,a.MDM_STS_NM,\n" +
        			"a.A025,a.A026,a.A027,a.A028,a.A029,a.A030,a.A031,a.A032,a.A033,a.A034,a.A035,a.A036,a.A037,a.A038,a.A039,a.CRUT_NO,a.LOAD_DATES,a.SEQ from (select case when substr(name,0,2) not in \n" +
        			"(select substr(name,0,2) from hzdb.pub_corp_dept where shortnameen is not null) then '田坝支行' when name='营业' then '田坝支行' else name end as name,A003,A004,A005,A006,A007,A008,A009,A010,\n" +
        			"A011,A012,A013,A014,A015,A016,A017,A018,A019,A020,A021,A022,A023,CZ_KA_FLG,A024,PK_FLG,NEW_CARD_NO,NEW_SF_YKT,MDM_STS_NM,\n" +
        			"A025,A026,A027,A028,A029,A030,A031,A032,A033,A034,A035,A036,A037,A038,A039,CRUT_NO,LOAD_DATES,SEQ from \n" +
        			"((select a.A name,b.* from  (select A,NAME from hzdb.excel_tab_85) a,(select * from hzbank.t_hnyzt_dis_"+getQYTTime()+") b \n" +
        			"where instr(b.A035,a.NAME)>0) union all (select a.A name,b.* from (select A,NAME from hzdb.excel_tab_85) a,(select * from hzbank.t_hnyzt_dis_"+getQYTTime()+" where SEQ not in (\n" +
        			"select b.seq from (select A,NAME from hzdb.excel_tab_85) a,(select A035,SEQ from hzbank.t_hnyzt_dis_"+getQYTTime()+") b where instr(b.A035,a.NAME)>0)) b \n" +
        			"where instr(b.A007,a.NAME)>0) union all (select \n" +
        			"case when a007A is null and instr(A035,'乡')=0 and instr(A035,'镇')=0 then substr(A026,instr(A026,'司')+1,2) \n" +
        			"when a007A is null and A035 is not null then substr(A035,instr(A035,'县')+1,2) \n" +
        			"when a007A is null and A035 is null then substr(A026,instr(A026,'司')+1,2) \n" +
        			"else a007A \n" +
        			"end a007A,A003,A004,A005,A006,A007,A008,A009,A010,A011,A012,A013,A014,A015,A016,A017,A018,A019,A020,A021,A022,A023,CZ_KA_FLG,A024,PK_FLG,NEW_CARD_NO,NEW_SF_YKT,MDM_STS_NM, \n" +
        			"A025,A026,A027,A028,A029,A030,A031,A032,A033,A034,A035,A036,A037,A038,A039,CRUT_NO,LOAD_DATES,SEQ \n" +
        			"from( \n" +
        			"select  case  \n" +
        			"when instr(a007,'乡')>0 and instr(a007,'县')>0 then substr(a007,instr(a007,'县')+1,2) \n" +
        			"when instr(a007,'镇')>0 and instr(a007,'县')>0 then substr(a007,instr(a007,'县')+1,2) \n" +
        			"when instr(a007,'镇')>0 then substr(a007,0,2) \n" +
        			"when instr(a007,'乡')>0 then substr(a007,0,2) \n" +
        			"end a007a,a.* \n" +
        			"from (select * from hzbank.t_hnyzt_dis_"+getQYTTime()+" where seq not in (select b.seq from  (select A,NAME from hzdb.excel_tab_85) a,(select A007,SEQ from hzbank.t_hnyzt_dis_"+getQYTTime()+" where SEQ not in ( \n" +
        			"select b.seq from (select A,NAME from hzdb.excel_tab_85) a,(select A035,SEQ from hzbank.t_hnyzt_dis_"+getQYTTime()+") b where instr(b.A035,a.NAME)>0)) b \n" + 
        			"where instr(b.A007,a.NAME)>0 ) and seq not in (select b.seq from (select A,NAME from hzdb.excel_tab_85) a,(select A035,SEQ from hzbank.t_hnyzt_dis_"+getQYTTime()+") b  \n" +
        			"where instr(b.A035,a.NAME)>0))a))))a,(select name from hzdb.pub_corp_dept where shortnameen is not null)b) where name is not null)");
        		dmo.executeUpdateByDS(null,"create or replace view v_hz_ykt_rate_"+getQYTTime()+" as (select a.name,a.c as yktzyk,b.c as yktptk,c.c as ykahfs,d.c as yktcount from " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A014='是' and A015='是' and A020='是' and A021='是' group by name) a left join " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A014='是' and A015='是' and A020='是' and A021='否' group by name) b on a.name=b.name left join " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" where A018='是' group by name) c on b.name=c.name left join " +
        				"(select name,count(name) as c from hzbank.hz_ykt_rate_"+getQYTTime()+" group by name) d on c.name=d.name)");
    		return "一卡通数据处理成功";
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
