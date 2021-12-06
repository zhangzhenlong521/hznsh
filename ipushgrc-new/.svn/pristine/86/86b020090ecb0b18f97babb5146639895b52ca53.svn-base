package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 首页流程文件按业务类型分布的饼图
 * @author xch
 *
 */
public class CmpfilePieChartBuilder implements DeskTopNewsDataBuilderIFC {

	/**
	 * 构造数据的
	 */
	public HashVO[] getNewData(String userCode) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select bsactname,cmpfiletype,'1' from v_cmp_cmpfile where filestate='3'"); //
		return hvs;
	}

}
