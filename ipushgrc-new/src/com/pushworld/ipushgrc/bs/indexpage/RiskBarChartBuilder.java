package com.pushworld.ipushgrc.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;

/**
 *��ҳ����ͳ������ͼ�鿴
 * @author xch
 *
 */
public class RiskBarChartBuilder implements DeskTopNewsDataBuilderIFC {

	/**
	 * �������ݵ�
	 */
	public HashVO[] getNewData(String userCode) throws Exception {
		HashVO[] hvs = new CommDMO().getHashVoArrayByDS(null, "select bsactname,(case risk_rank when '�������' then '�߷���' when '��С����' then '�ͷ���' else risk_rank end) as risk_rank,count(id) from v_risk_process_file where filestate='3' group by bsactname,risk_rank"); //
		return hvs;
	}

}
