package cn.com.pushworld.wn.bs;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.bs.WnSalaryServiceImpl;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

public class ManagerQuartzJob implements WLTJobIFC {
	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		try {
			// 获取到当前时间
			SimpleDateFormat simple = new SimpleDateFormat("dd");
			int day = Integer.parseInt(simple.format(new Date()));
			if (day == 1) {// 每个月的1号启动打分
				ManagerGradeStart();
			} else if (day == 5) {// 每个月的5号晚上结束打分
				ManagerGradeEnd();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "客户经理打分计划生成成功";
	}

	private void ManagerGradeEnd() {
		try {
			String planid = dmo.getStringValueByDS(null,
					"select max(planid) from WN_MANAGERDX_TABLE");
			if (planid == null || planid.isEmpty()) {
				return;
			} else {
				WnSalaryServiceIfc service = new WnSalaryServiceImpl();
				service.endManagerDXscore(planid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void ManagerGradeStart() {
		try {
			WnSalaryServiceIfc service = new WnSalaryServiceImpl();
			// 对id进行处理
			String planid = dmo.getStringValueByDS(null,
					"select max(planid) from WN_MANAGERDX_TABLE");
			if (planid == null || planid.isEmpty()) {
				planid = "1";
			} else {
				planid = String.valueOf(Integer.parseInt(planid) + 1);
			}
			service.gradeManagerDXscore(planid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}