package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 *首页风险统计柱形图查看
 * @author xch
 *
 */
public class RiskBarChartBuilder implements DeskTopNewsDataBuilderIFC {

	/**
	 * 构造数据的
	 */
	public HashVO[] getNewData(String userCode) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select bsactname,(case risk_rank when '极大风险' then '高风险' when '极小风险' then '低风险' else risk_rank end) as risk_rank,count(id) from v_risk_process_file where filestate='3' group by bsactname,risk_rank"); //
		return hvs;
	}

}
