package cn.com.pushworld.wn.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;

public class CsbJdfPersonAdjust implements WLTJobIFC {

	@Override
	public String run() throws Exception {
		CommDMO dmo = new CommDMO();
		String inputParam = getKhMonth();
		HashMap<String, String> khmap = new HashMap<String, String>();
		
		try {
			khmap=dmo.getHashMapBySQLByDS(null,"select a.code,b.code deptcode from (select * from v_sal_personinfo where isuncheck='N' and mainstation like '%柜员%') a ,pub_corp_dept b where a.deptname=b.name");
			for(String str : khmap.keySet()){
				String[][] jdfdata=dmo.getStringArrayByDS(null,"select jg_id from WN_JDFKMHZB_02 where  datetime like '%"+inputParam+"%' and gy_id='"+str+"'");
				String[][] khdata=dmo.getStringArrayByDS(null,"select a.deptname,b.code from v_sal_personinfo a,pub_corp_dept b where a.deptname=b.name and a.code='"+str+"'");
				if(jdfdata.length>1){
					dmo.executeUpdateByDS(null,"update WN_JDFKMHZB_02 set jg_id='"+khmap.get(str)+"' where gy_id='"+str+"' and datetime like '%"+inputParam+"%'");
					dmo.executeUpdateByDS(null,"update WN_CSBHZ_01 set WDMC='"+khdata[0][0]+"',WDJGH='"+khdata[0][1]+"' where gy_id='"+str+"' and sj_date like '%"+inputParam+"%'");
				}if(jdfdata.length<1){
					continue;
				}if(jdfdata.length==1&&jdfdata[0][0]==khmap.get(str)){
					continue;
				}else{
					dmo.executeUpdateByDS(null,"update WN_JDFKMHZB_02 set jg_id='"+khmap.get(str)+"' where gy_id='"+str+"' and datetime like '%"+inputParam+"%'");
					dmo.executeUpdateByDS(null,"update WN_CSBHZ_01 set WDMC='"+khdata[0][0]+"',WDJGH='"+khdata[0][1]+"' where gy_id='"+str+"' and sj_date like '%"+inputParam+"%'");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "计算成功";
	}

	private String getKhMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return dateFormat.format(otherDate);
	}
	

}
