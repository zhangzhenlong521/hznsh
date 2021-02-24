package cn.com.pushworld.wn.bs;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.bs.common.WLTJobTimer;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;
import cn.com.infostrategy.ui.common.ClientEnvironment;
import cn.com.infostrategy.ui.common.SplashWindow;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.GYServerKHPlan;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

public class GYQuartzJob implements WLTJobIFC {

	private String str;
	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		try {
			SimpleDateFormat format = new SimpleDateFormat("dd");
			int currentDay = Integer.parseInt(format.format(new Date()));
			GYQuartzJob gy = new GYQuartzJob();
			switch (currentDay) {
			case 1:
			case 8:
			case 9:
			case 15:
			case 22:
				gy.gradeEnd();
				gy.gradeScore();
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "计算结束";
	}

	public void gradeEnd() {
		try {// 结束打分:1.修改状态;2.计算总分
			System.out.println("柜员服务质量打分成功");
			HashVO[] vo = dmo
					.getHashVoArrayByDS(
							null,
							"SELECT distinct(USERCODE) AS USERCODE,STATE,PFTIME FROM WN_GYPF_TABLE WHERE STATE='评分中'");
			if (vo == null || vo.length <= 0) {
				return;
			}
			for (int i = 0; i < vo.length; i++) {
				HashVO v = vo[i];
				gradeEveryOne(v.getStringValue("USERCODE"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 结束每个人的评分
	/**
	 * 结束当前柜员服务质量考核,如果当前服务质量尚未评分或者尚未复核完成直接结束。
	 * 
	 * @param usercode
	 *            :柜员号
	 */
	public void gradeEveryOne(String usercode) {
		try {
			Double KOUOFEN = 0.0;
			Double result = 0.0;
			UpdateSQLBuilder update = new UpdateSQLBuilder(
					"wnSalaryDb.WN_GYPF_TABLE");
			HashVO[] vo = dmo.getHashVoArrayByDS(null,
					"select * from wnSalaryDb.WN_GYPF_TABLE where usercode='"
							+ usercode + "' and state='评分中' Order by ID");
			List list = new ArrayList<String>();
			for (int i = 0; i < vo.length - 1; i++) {
				String koufenValue = vo[i].getStringValue("KOUOFEN");
				String defenValue = vo[i].getStringValue("FENZHI");
				KOUOFEN = Double.parseDouble(defenValue);
				result = result + KOUOFEN;
				update.setWhereCondition("id='" + vo[i].getStringValue("id")
						+ "'");
				update.putFieldValue("KOUOFEN", KOUOFEN);
				update.putFieldValue("state", "评分结束");
				list.add(update.getSQL());
			}
			double sum = Double.parseDouble(vo[vo.length - 1]
					.getStringValue("KOUOFEN"));
			double sumfen = sum - result;
			String sumSQL = "update wnSalaryDb.WN_GYPF_TABLE set KOUOFEN='"
					+ sumfen + "',state='评分结束' where usercode='" + usercode
					+ "' and xiangmu='总分'";
			System.out.println(sumSQL);
			list.add(sumSQL);
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void gradeScore() {
		try {
			WnSalaryServiceImpl service = new WnSalaryServiceImpl();
			String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.format(new Date());
			System.out.println("开始生成打分计划");
			str = service.getSqlInsert(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}