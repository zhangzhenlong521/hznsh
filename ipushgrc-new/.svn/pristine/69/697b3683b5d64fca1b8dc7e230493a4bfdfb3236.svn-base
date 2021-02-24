package com.pushworld.ipushgrc.bs.law.p010;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.TBUtil;

public class AutoUpdateLawThread implements WLTJobIFC {
	CommDMO commdmo = new CommDMO();
	public String run() throws Exception {
		String date =new TBUtil().getCurrDate();
		commdmo.executeBatchByDS(null, new String[]{"update law_law set state='现行有效' where state='尚未生效' and implement_date<='"+date+"'"});
		return "更新法规成功！";
	}

}
