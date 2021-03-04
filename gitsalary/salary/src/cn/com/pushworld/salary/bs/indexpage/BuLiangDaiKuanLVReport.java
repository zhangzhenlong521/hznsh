package cn.com.pushworld.salary.bs.indexpage;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC2;
import cn.com.infostrategy.to.common.HashVO;

public class BuLiangDaiKuanLVReport implements DeskTopNewsDataBuilderIFC2 {

	public HashVO[] getNewData(String loginUserCode, HashMap _map) throws Exception {
		HashVO[] hvo = new CommDMO().getHashVoArrayByDS(null, "select '不良贷款率' 名称,'第一季度' 日期 ,reportvalue 值 from sal_dept_check_score t1 left join sal_target_check_log t2 on t1.logid = t2.id  where t1.targetid = 256 order by t2.checkdate");
		HashVO rtvo = new HashVO();
		rtvo.setAttributeValue("标题", "不良贷款率"); //
		rtvo.setAttributeValue("X轴", "实际值(%)"); //
		rtvo.setAttributeValue("实际值", hvo[hvo.length - 1].getStringValue("值")); //
		rtvo.setAttributeValue("最小值", 0d); //
		rtvo.setAttributeValue("正常值", 5d); //
		rtvo.setAttributeValue("警界值", 8d); //
		rtvo.setAttributeValue("最大值", 15d); //

		HashVO rtvo_1 = new HashVO();
		rtvo_1.setAttributeValue("标题", "地区银行业存款占比"); //
		rtvo_1.setAttributeValue("X轴", "3月(%)"); //
		rtvo_1.setAttributeValue("实际值", 30.2); //
		rtvo_1.setAttributeValue("最小值", 0d); //
		rtvo_1.setAttributeValue("正常值", 60); //
		rtvo_1.setAttributeValue("警界值", 20d); //
		rtvo_1.setAttributeValue("最大值", 100d); //

		return new HashVO[] { rtvo, rtvo_1 };
	}

}
