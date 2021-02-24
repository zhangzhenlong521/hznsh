package cn.com.pushworld.wn.bs;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.ui.common.UIUtil;
import cn.com.pushworld.wn.ui.GydxdfWKPanel;
import cn.com.pushworld.wn.ui.WnSalaryServiceIfc;

/*
 * ��Ա����������ʱ����
 */
public class GYDXQuartzJob implements WLTJobIFC {
	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		try {
			SimpleDateFormat simple = new SimpleDateFormat("dd");
			String day = simple.format(new Date());
			if ("3".equals(day)) {// ÿ���µ�1�Ž������¿����������ɵ��¿�������
				gradeEnd(1);
				gradeScore(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "�����ɹ�";
	}

	public void gradeScore(int num) {
		try {
			// Ϊ�������˼ƻ����ɼƻ�ID
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

	// ------>��ʱ�����������ִ�мƻ�
	public void gradeEnd(int num) {// ���Կ��˼ƻ�����
		try {
			String countStr = dmo
					.getStringValueByDS(null,
							"select count(*) from WN_GYDX_TABLE where state='������' or  fhresult is null ");
			if (Integer.parseInt(countStr) <= 0) {
				return;
			} else {
				String planId = dmo
						.getStringValueByDS(null,
								"select id from WN_GYDX_TABLE where state='������' or fhresult  IS null");
				WnSalaryServiceIfc service = new WnSalaryServiceImpl();
				String str = service.gradeDXEnd(planId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
