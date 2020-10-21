package com.pushworld.ipushgrc.bs.cmpcheck;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.mdata.jepfunctions.IClassJepFormulaParseIFC;

/***
 * 
 * 加载公式调用类
 * 取得检查活动对应的违规事件(event)或成功防范(ward)  
 * @author Gwang
 *
 */
public class GetEventCount implements IClassJepFormulaParseIFC {


	public String getForMulaValue(String[] _pars) throws Exception {
		
		String eventName = _pars[0];
		String id = _pars[1];
		String val = "";
		if (eventName.equals("event")){	//违规事件
			val = this.getCount("cmp_event",	id);
		}else if (eventName.equals("ward")){	//成功防范
			val = this.getCount("cmp_ward",	id);
		}else if (eventName.equals("issue")){	//发现问题
			val = this.getCount("cmp_issue",	id);
		}	
		return val;
	}
	
	private String getCount(String tableName, String id) {
		
		String sql = "select count(*)as num from " + tableName + " where cmp_check_item_id = " + id;		
		CommDMO commdmo = new CommDMO();
		String count = "";
		try {
			count = commdmo.getStringValueByDS(null, sql);
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
		return count;
	}
	
}
