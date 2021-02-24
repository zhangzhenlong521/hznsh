package com.pushworld.ipushgrc.bs.login.p010;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.MultiLevelReportDataBuilderAdapter;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.common.WLTRemoteException;
import cn.com.infostrategy.to.report.BeforeHandGroupTypeVO;

/**
 * 部门登录统计
 * @author YangQing/2013-11-27
 *
 */
public class DeptLoginReportBuilderAdapter extends MultiLevelReportDataBuilderAdapter {

	public HashVO[] buildReportData(HashMap consMap) throws WLTRemoteException, Exception {
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String str_date = (String) consMap.get("date");//选择的日期
		String today = new TBUtil().getCurrDate();//今天日期

		AnalyseLoginData loginInfo = new AnalyseLoginData();
		List<String> dayofweeklist = loginInfo.getDayofWeek(str_date);
		List<String> curr_dayofweek = loginInfo.getDayofWeek(today);
		String weekofyear = loginInfo.getWeekOfYear(dayofweeklist.get(0), dayofweeklist.get(dayofweeklist.size() - 2));

		Date searchMonday = sdf2.parse(dayofweeklist.get(0));//查询日期所在周周一
		Date currSunday = sdf2.parse(curr_dayofweek.get(curr_dayofweek.size() - 1));//当前日期所在周周末
		if (searchMonday.getTime() > currSunday.getTime()) {
			//查询的是本周以后的日期，没有记录
			return new HashVO[0];
		}

		loginInfo.analyseDeptData(str_date);//分析部门登录数据

		String sql_deptlogin = "select ID,ONLINE_HOUR 在线时长,concat(concat('【',WEEKOFYEAR),'】在线时长(小时)') 在线时长_标题,DEPARTNAME 部门名称 From pub_deptlogindata where weekofyear='" + weekofyear + "' order by online_hour desc";
		HashVO[] deptloginVO = new CommDMO().getHashVoArrayByDS(null, sql_deptlogin);
		return deptloginVO;
	}

	public String[] getGroupFieldNames() {

		return null;
	}

	public String[] getSumFiledNames() {

		return null;
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType_Chart() {
		return getBeforehandGroupType(2);
	}

	public BeforeHandGroupTypeVO[] getBeforehandGroupType(int _type) {
		ArrayList<BeforeHandGroupTypeVO> al = new ArrayList<BeforeHandGroupTypeVO>(); //

		BeforeHandGroupTypeVO typeVo1 = new BeforeHandGroupTypeVO();
		typeVo1.setName("部门_在线时长");//维度类型
		typeVo1.setRowHeaderGroupFields(new String[] { "部门名称" });
		typeVo1.setColHeaderGroupFields(new String[] { "在线时长_标题" });
		typeVo1.setComputeGroupFields(new String[][] { { "在线时长", BeforeHandGroupTypeVO.INIT } });
		al.add(typeVo1);
		return (BeforeHandGroupTypeVO[]) al.toArray(new BeforeHandGroupTypeVO[0]);
	}

	/**
	 * 钻取显示详细数据
	 */
	public String getDrillActionClassPath() throws Exception {
		return "com.pushworld.ipushgrc.ui.login.p010.DeptLoginStatisWKPanel";
	}
}
