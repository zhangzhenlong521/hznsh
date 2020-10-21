package cn.com.pushworld.wn.bs;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.ui.GydxdfWKPanel;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

/*
 * 柜员工作质量定时任务
 */
public class GYDXQuartzJob implements WLTJobIFC {
	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		try {
			SimpleDateFormat simple = new SimpleDateFormat("dd");
			String day = simple.format(new Date());
			if ("3".equals(day)) {// 每个月的1号结束上月考核任务，生成当月考核任务
				gradeEnd(1);
				gradeScore(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "结束成功";
	}

	public void gradeScore(int num) {
		try {
			// 为整个考核计划生成计划ID
			String maxId = dmo.getStringValueByDS(null,
					"select max(ID) from WN_GYDX_TABLE");
			if (maxId == null || maxId.isEmpty()) {
				maxId = "1";
			} else {
				maxId = String.valueOf((Integer.parseInt(maxId) + 1));
			}
			WnSalaryServiceIfc service = new WnSalaryServiceImpl();
			String str = service.gradeDXscore(maxId);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ------>定时任务结束启动执行计划
	public void gradeEnd(int num) {// 定性考核计划结束
		try {
			String countStr = dmo
					.getStringValueByDS(null,
							"select count(*) from WN_GYDX_TABLE where state='评分中' or  fhresult is null ");
			if (Integer.parseInt(countStr) <= 0) {
				return;
			} else {
				String planId = dmo
						.getStringValueByDS(null,
								"select id from WN_GYDX_TABLE where state='评分中' or fhresult  IS null");
				WnSalaryServiceIfc service = new WnSalaryServiceImpl();
				String str = service.gradeDXEnd(planId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
