package cn.com.pushworld.wn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;

/**
 * 
 * @author zzl
 * 
 *         2019-3-25-下午05:23:44 计算年初夫妻的有效户数
 */
public class YearManAndWifeHouseholdsCount {
	public HashMap<String, String> getCount(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		CommDMO dmo = new CommDMO();
		try {
			String[][] count = dmo
					.getStringArrayByDS(
							null,
							"select xj.BAL_BOOK_AVG_M,xj.XD_COL7,yb.XD_COL2,xj.xd_col5 from(select sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M,xx.XD_COL7 XD_COL7,xx.XD_COL96 XD_COL96,gl.xd_col5 xd_col5 from (select COD_CUST COD_CUST,(sum(BAL_BOOK)/to_number(to_char(sysdate,'dd'))) as BAL_BOOK_AVG_M from wnbank.S_OFCR_CH_ACCT_MAST where BAL_BOOK > 100  and to_char(to_date(BIZ_DT,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' group by COD_CUST,COD_ACCT_TITLE) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.COD_CUST = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01' group by xx.xd_col7,xx.xd_col96,gl.xd_col5) xj left join wnbank.S_LOAN_RYB yb on xj.XD_COL96=yb.xd_col1");
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xx.XD_COL7,ck.BAL_BOOK_AVG_M BAL_BOOK_AVG_M from (select COD_CUST COD_CUST,(sum(BAL_BOOK)/to_number(to_char(sysdate,'dd'))) as BAL_BOOK_AVG_M from wnbank.S_OFCR_CH_ACCT_MAST where BAL_BOOK > 100  and to_char(to_date(BIZ_DT,'yyyy-mm-dd'),'yyyy-mm-dd')='"
									+ date
									+ "' group by COD_CUST,COD_ACCT_TITLE) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.COD_CUST = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01'");
			for (int i = 0; i < count.length; i++) {
				Double tj = 0.0;
				int a = 0;
				if (resultMap.get(count[i][3].toString()) != null) {
					tj = Double.parseDouble(count[i][0].toString())
							+ Double.parseDouble(resultMap.get(count[i][3]
									.toString()));
					if (tj > 5000) {
						a = a + 1;
						if (map.get(count[i][2].toString()) != null) {
							a = a
									+ Integer.parseInt(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), String.valueOf(a));
					}
				} else {
					if (Double.parseDouble(count[i][0].toString()) > 5000) {
						a = a + 1;
						if (map.get(count[i][2].toString()) != null) {
							a = a
									+ Integer.parseInt(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), String.valueOf(a));
					}
				}

			}
			for (String str : map.keySet()) {
				System.out.println(">>>>>>>" + str + ">>>>>>>>" + map.get(str));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

}
