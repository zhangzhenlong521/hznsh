package cn.com.pushworld.salary.bs.indexpage;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

public class BuLiangDaiKuanReport implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String loginUserCode) throws Exception {
		HashVO[] hvo = new CommDMO().getHashVoArrayByDS(null, "select checkdate ÈÕÆÚ ,checkeddeptname,reportvalue Öµ from sal_dept_check_score where targetid = 491 and logid=405");
		return hvo;
	}

	public HashVO[] getNewData(String loginUserCode, HashMap param) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
