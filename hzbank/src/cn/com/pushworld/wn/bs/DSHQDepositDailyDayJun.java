package cn.com.pushworld.wn.bs;

import cn.com.infostrategy.bs.common.CommDMO;

public class DSHQDepositDailyDayJun {
	private CommDMO dmo = new CommDMO();

	public String getDayJun() {
		String str = null;
		try {
			String[][] data = dmo
					.getStringArrayByDS(null,
							"select ACCT_NO from wnbank.a_agr_dep_acct_psn_sv sv group by ACCT_NO");
			str = "Å£±Æ";
		} catch (Exception e) {
			str = "À­¸×";
			e.printStackTrace();
		}
		return str;
	}

}
