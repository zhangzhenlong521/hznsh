package com.pushworld.ipushgrc.bs;

import java.text.DecimalFormat;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.sysapp.login.DeskTopNewsDataBuilderIFC;
import cn.com.infostrategy.to.common.CommonDate;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.WLTRemoteException;

/**
 * 系统公告的首页数据构造器!!!以后在首页配置时应该将类型可以作为参数传进来!!!
 * 
 * @author xch
 * 
 */
public class SysboardDataBuilder1 implements DeskTopNewsDataBuilderIFC {

	/**
	 * 
	 */
	public HashVO[] getNewData(String _userCode) throws Exception {
		CommDMO dmo = new CommDMO();

		double eventDate = 0d; // 时间间隔 实际值
		try {
			HashVO[] eventVO = dmo.getHashVoArrayByDS(null, "select id,finddate,happendate from cmp_event where 1 = 1");
			if (eventVO == null || eventVO.length <= 0) {
				return null;
			}
			CommonDate finddate = null;
			CommonDate happendate = null;
			int days = 0;
			for (int i = 0; i < eventVO.length; i++) {
				finddate = new CommonDate(eventVO[i].getDateValue("finddate"));
				happendate = new CommonDate(eventVO[i].getDateValue("happendate"));
				days += CommonDate.getDaysBetween(happendate, finddate);
			}
			eventDate = (double) days / eventVO.length;
		} catch (WLTRemoteException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		DecimalFormat df = new DecimalFormat("0.0");
		double d_real = Double.parseDouble(df.format(eventDate));// 实际值
		HashVO[] targetVO = dmo.getHashVoArrayByDS(null, "select * from cmp_target where name ='事件发生发现时间间隔'");
		if (targetVO == null || targetVO.length <= 0) {
			return null;
		}
		double d_warnvalue = Double.parseDouble(targetVO[0].getStringValue("warnvalue"));
		double d_normalvalue = Double.parseDouble(targetVO[0].getStringValue("normalvalue"));
		if (d_real < d_normalvalue) {
			return new HashVO[0];
		}
		if (d_real > d_normalvalue && d_real < d_warnvalue) {
			targetVO[0].setAttributeValue("descr", "指标---事件发生发现时间间隔实际值[" + d_real + "]已超出正常值[" + d_normalvalue + "]但未达到预警值[" + d_warnvalue + "]");
			targetVO[0].setToStringFieldName("descr");
			targetVO[0].setUserObject("icon", "office_053.gif");
		} else if (d_real > d_warnvalue) {
			targetVO[0].setAttributeValue("descr", "指标---事件发生发现时间间隔实际值[" + d_real + "]已超出预警值[" + d_warnvalue + "]");
			targetVO[0].setToStringFieldName("descr");
			targetVO[0].setUserObject("icon", "office_016.gif");
		}
		return targetVO;
	}
}
