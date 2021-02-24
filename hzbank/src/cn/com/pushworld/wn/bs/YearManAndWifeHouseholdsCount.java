package cn.com.pushworld.wn.bs;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;

/**
 * 
 * @author zzl
 * 
 *         2019-4-8-下午05:20:01 计算年初的当前户数
 *         [考核月初日期]+";"+[考核的月末日期]+";"+[上月月初日期]+";"+
 *         [上月指月末日期]+";"+[年初月初日期]+";"+[年初月末日期]+";"+[天数]
 */
public class YearManAndWifeHouseholdsCount {
	private CommDMO dmo = new CommDMO();
	private String day = null;

	public HashMap<String, String> getYearCount(String date, HashVO[] vo) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		day = time[6].toString();
		HashMap<String, String> DSHQDSMap = getYearDSHQDS(date);
		HashMap<String, String> DSHQFQMap = getYearDSHQFQ(date);
		HashMap<String, String> DSDQDSMap = getYearDSDQDS(date);
		HashMap<String, String> DSDQFQMap = getYearDSDQFQ(date);
		HashMap<String, String> DGHQMap = getYearDGHQ(date);
		HashMap<String, String> DGDQMap = getYearDGDQ(date);
		try {
			for (int i = 0; i < vo.length; i++) {
				int a = 0;
				if (DSHQDSMap.get(vo[i].getStringValue("name")) != null) {
					a = Integer.parseInt(DSHQDSMap.get(
							vo[i].getStringValue("name")).toString());
				}
				if (DSHQFQMap.get(vo[i].getStringValue("name")) != null) {
					a = a
							+ Integer.parseInt(DSHQFQMap.get(
									vo[i].getStringValue("name")).toString());
				}
				if (DSDQDSMap.get(vo[i].getStringValue("name")) != null) {
					a = a
							+ Integer.parseInt(DSDQDSMap.get(vo[i]
									.getStringValue("name")));
				}
				if (DSDQFQMap.get(vo[i].getStringValue("name")) != null) {
					a = a
							+ Integer.parseInt(DSDQFQMap.get(vo[i]
									.getStringValue("name")));
				}
				if (DGHQMap.get(vo[i].getStringValue("code")) != null) {
					a = a
							+ Integer.parseInt(DGHQMap.get(vo[i]
									.getStringValue("code")));
				}
				if (DGDQMap.get(vo[i].getStringValue("code")) != null) {
					a = a
							+ Integer.parseInt(DGDQMap.get(vo[i]
									.getStringValue("code")));
				}
				map.put(vo[i].getStringValue("name"), String.valueOf(a));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * ---对私活期存款 单身
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getYearDSHQDS(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,count(yb.xd_col2) tj from(select c.xd_col96 as xd_col96,sum(a.f) as f from (  select  cust_no, (sum(f)/"
									+ day
									+ ") as f  from wnbank.a_agr_dep_acct_psn_sv where f > 100  and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CI_CUSTMAST b on a.cust_no = b.COD_CUST_ID left join wnbank.S_LOAN_KHXX c on b.EXTERNAL_CUSTOMER_IC = c.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on c.XD_COL1 = gl.XD_COL1 where c.XD_COL32 > 1 and gl.xd_col3<>'01' and a.f>5000 group by c.xd_col7,c.xd_col96 )ffq left join wnbank.S_LOAN_RYB yb on ffq.XD_COL96=yb.xd_col1 group  by yb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * ---对私活期存款 夫妻
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getYearDSHQFQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] count = dmo
					.getStringArrayByDS(
							null,
							"select xj.BAL_BOOK_AVG_M,xj.XD_COL7,yb.XD_COL2,xj.xd_col5 from(select sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M,xx.XD_COL7 XD_COL7,xx.XD_COL96 XD_COL96,gl.xd_col5 xd_col5 from (select cust_no cust_no,(sum(f)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.a_agr_dep_acct_psn_sv  where f > 100  and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) ck  left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7  left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01' group by xx.xd_col7,xx.xd_col96,gl.xd_col5)  xj left join wnbank.S_LOAN_RYB yb on xj.XD_COL96=yb.xd_col1");
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xx.XD_COL7,ck.BAL_BOOK_AVG_M BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(f)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.a_agr_dep_acct_psn_sv where f > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01'");
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

	/**
	 * ---对私定期定期 单身
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getYearDSDQDS(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,count(yb.xd_col2) tj from(select c.xd_col96 as xd_col96,sum(a.acct_bal) as f from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CI_CUSTMAST b on a.cust_no = b.COD_CUST_ID left join wnbank.S_LOAN_KHXX c on b.EXTERNAL_CUSTOMER_IC = c.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on c.XD_COL1 = gl.XD_COL1 where c.XD_COL32 > 1 and gl.xd_col3<>'01' and a.acct_bal>5000 group by c.xd_col7,c.xd_col96 )ffq left join wnbank.S_LOAN_RYB yb on ffq.XD_COL96=yb.xd_col1 group  by yb.xd_col2");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ---对私定期存款 夫妻
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getYearDSDQFQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] count = dmo
					.getStringArrayByDS(
							null,
							"select xj.BAL_BOOK_AVG_M,xj.XD_COL7,yb.XD_COL2,xj.xd_col5 from (select sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M,xx.XD_COL7 XD_COL7,xx.XD_COL96 XD_COL96,gl.xd_col5 xd_col5 from (select cust_no cust_no,(sum(acct_bal)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.A_AGR_DEP_ACCT_PSN_FX  where acct_bal > 100  and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7  left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01' group by xx.xd_col7,xx.xd_col96,gl.xd_col5)  xj left join wnbank.S_LOAN_RYB yb on xj.XD_COL96=yb.xd_col1");
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xx.XD_COL7,ck.BAL_BOOK_AVG_M BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(acct_bal)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01'");
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

	/**
	 * ---对公存款活期
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getYearDGHQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.CUS_MANAGER as CUS_MANAGER,count(c.CUS_MANAGER) tj from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal from wnbank.A_AGR_DEP_ACCT_ENT_SV where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join wnbank.S_CMIS_ACC_LOAN c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000 group by c.cus_id,c.CUS_MANAGER");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * ---对公存款定期
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getYearDGDQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.CUS_MANAGER as CUS_MANAGER,count(c.CUS_MANAGER) tj from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_ENT_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[5].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[4].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join wnbank.S_CMIS_ACC_LOAN c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000 group by c.cus_id,c.CUS_MANAGER");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
