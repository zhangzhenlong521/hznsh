package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

public class BargainTrackDataBuilder implements DeskTopNewsDataBuilderIFC{
	public HashVO[] getNewData(String userCode) throws Exception {
		HashVO[] hvs=new CommDMO().getHashVoArrayByDS(null, "select * from  lbs_bargaintrack  where carryoutstate='1' and carryoutdate< '"+new TBUtil().getCurrDate()+"'");
		for (int i = 0; i < hvs.length; i++) {
			hvs[i].setToStringFieldName("mainpoint");
		}
		return hvs;
	}

}
