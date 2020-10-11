package cn.com.pushworld.salary.bs.indexpage;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

public class GuiYuanYeWuReport implements DeskTopNewsDataBuilderIFC {
	private TBUtil util = new TBUtil();
	private CommDMO commDMO = new CommDMO();

	public HashVO[] getNewData(String loginUserCode) throws Exception {
		String currdate = util.getCurrDate();
		String currmonth = currdate.substring(0, 7);
		String usercode = commDMO.getCurrSession().getLoginUserCode();
		HashVO[] hvo = null;
		hvo = new CommDMO().getHashVoArrayByDS(null, "select '��Աҵ�����' as ����,t2.datadate as ����,t1.e as ֵ from sal_reportstore_001 t1 left join sal_convert_ifcdata_log t2 on t1.logid =t2.id where t1.c ='" + usercode + "' and t2.datadate like '" + currmonth + "%' order by t2.datadate ");
		return hvo;
	}
}
