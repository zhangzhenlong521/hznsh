package cn.com.pushworld.salary.bs.indexpage;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC2;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

public class MoneyTotleReport implements DeskTopNewsDataBuilderIFC2 {

	public HashVO[] getNewData(String loginUserCode, HashMap param) throws Exception {

		String targetName = "";
		String targettype = "";
		String calcType = "";
		String mubiao = "";
		if (param != null) {
			String targetName_1 = (String) param.get("指标名称"); //必须输入
			if (!TBUtil.isEmpty(targetName_1)) {
				targetName = targetName_1;
			}
			String targettype_1 = (String) param.get("指标类型"); //部门定量,员工定量 
			if (!TBUtil.isEmpty(targettype_1)) {
				targettype = targettype_1;
			}
			String calcType_1 = (String) param.get("计算类型"); //求和 求平均 最大值 最小值
			if (!TBUtil.isEmpty(calcType_1)) {
				calcType = calcType_1;
			}
		}
		CommDMO dmo = new CommDMO();
		return dmo.getHashVoArrayByDS(null, "select '"+targetName+"' 名称,t2.monthly 日期,sum(t1.factorvalue) 值 from  sal_salarybill_detail t1 left join sal_salarybill  t2 on t1.salarybillid = t2.id left join sal_account_set t3 on t3.id = t2.sal_account_setid where t1.factorname='" + targetName + "' group by t2.monthly ");
	}

}
