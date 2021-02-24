package com.pushworld.ipushgrc.bs.indexpage;

import java.text.DecimalFormat;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;

/**
 * 风险仪表盘统计!!!
 * 需要取补充完实际逻辑!!!
 * @author xch
 *
 */
public class RiskDiaplotChartBuilder implements DeskTopNewsDataBuilderIFC {

	public HashVO[] getNewData(String userCode) throws Exception {
		CommDMO dmo = new CommDMO();
		HashVO[] hvs = new HashVO[2]; //

		hvs[0] = new HashVO(); //
		HashVO[] eventVO = dmo.getHashVoArrayByDS(null, "select id,finddate,happendate from cmp_event where happendate like '" + new TBUtil().getCurrDate().substring(0, 4) + "%'");//取今年发生的事件
		CommonDate finddate = null;
		CommonDate happendate = null;
		int days = 0;
		for (int i = 0; i < eventVO.length; i++) {
			finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
			happendate = new CommonDate(eventVO[i].getDateValue("happendate"));
			days += CommonDate.getDaysBetween(happendate, finddate);
		}
		DecimalFormat df = new DecimalFormat("0");
		String eventDate = "0";
		if (eventVO.length != 0) {
			eventDate = df.format((double) days / eventVO.length);// 时间间隔 实际值（平均值）
		}

		hvs[0].setAttributeValue("标题", "违规事件发现及时率"); //违规事件发现及时率【李春娟/2012-03-30】
		hvs[0].setAttributeValue("X轴", "天数"); //
		hvs[0].setAttributeValue("实际值", eventDate); //
		hvs[0].setAttributeValue("最小值", 0); //
		hvs[0].setAttributeValue("正常值", 30); //
		hvs[0].setAttributeValue("警界值", 90); //
		hvs[0].setAttributeValue("最大值", 150); //
		hvs[0].setAttributeValue("背景色", "FFBD9D");
		hvs[0].setAttributeValue("提示", "发现时间与发生时间相差天数的平均数"); //

		hvs[1] = new HashVO(); //
		String[] str_counts = dmo.getStringArrayFirstColByDS(null, "select count(id) from v_risk_process_file where filestate='3' and ctrlfneffect like '%有效%' union all select count(id) from v_risk_process_file where filestate='3'");
		String percent = "0";
		if (str_counts.length != 0) {
			df = new DecimalFormat("0.0");
			percent = df.format(100 - Double.parseDouble(str_counts[0]) * 100 / Double.parseDouble(str_counts[1]));//风险控制率	
		}
		hvs[1].setAttributeValue("标题", "风险失控率"); //先找出所有风险点总数!! 再找出控制类型为控制基本有效和控制有效的总数!! 两个总数相除,换成百分比!!
		hvs[1].setAttributeValue("X轴", "百分比"); //
		hvs[1].setAttributeValue("实际值", percent); //
		hvs[1].setAttributeValue("最小值", 0); //
		hvs[1].setAttributeValue("正常值", 50); //
		hvs[1].setAttributeValue("警界值", 80); //
		hvs[1].setAttributeValue("最大值", 100); //
		hvs[1].setAttributeValue("背景色", "62FFFF"); //
		hvs[1].setAttributeValue("提示", "控制适当的风险点数量在所有风险点中的占比"); //

		return hvs;
	}
}
