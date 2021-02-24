package cn.com.pushworld.wn.bs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;
import cn.com.infostrategy.to.mdata.UpdateSQLBuilder;

/**
 * 
 * @author zzl
 * 
 *         2019-3-25-下午05:23:44 计算有效日均余额
 */
public class AverageDailyManAnd {
	private CommDMO dmo = new CommDMO();
	private HashVO[] vo = null;
	private YearAverageDailyManAnd year = new YearAverageDailyManAnd();// 年初的余额
	private MonthAverageDailyManAnd month = new MonthAverageDailyManAnd();// 上月的余额
	private String day = null;

	/**
	 * 计算和比较有效存款余额 把大于300万的和小于300万的先存到一张表里
	 * 
	 * @return
	 */
	public HashMap<String, String> getComputeMap(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		day = time[6].toString();
		try {
			vo = dmo.getHashVoArrayByDS(
					null,
					"select code,name from v_sal_personinfo where STATIONKIND in ('乡镇客户经理','城区客户经理','乡镇网点副主任','城区网点副主任')");
			HashMap<String, String> yearmap = year.getYearCount(date, vo);
			HashMap<String, String> monthmap = month.getYearCount(date, vo);
			InsertSQLBuilder insert = new InsertSQLBuilder("WN_RJ_CKYXHSTJ");
			UpdateSQLBuilder update = new UpdateSQLBuilder("WN_RJ_CKYXHSTJ");
			InsertSQLBuilder insertB = new InsertSQLBuilder(
					"WN_deposit_balance");// 记录完成数做完成比用
			InsertSQLBuilder insertYe = new InsertSQLBuilder("wn_ck_balance");// 记录存款余额
			HashVO[] hsvo = dmo.getHashVoArrayByDS(
					null,
					"select * from wn_ck_balance where date_time='"
							+ time[1].toString() + "'");
			if (hsvo.length > 0) {
				dmo.executeUpdateByDS(
						null,
						"delete from wn_ck_balance where date_time='"
								+ time[1].toString() + "'");
			}
			List list = new ArrayList<String>();
			HashMap<String, String> countMap = getCount(date);
			// 防止重复计算，故做删除
			HashVO[] tjvos = dmo.getHashVoArrayByDS(
					null,
					"select * from WN_RJ_CKYXHSTJ where E='"
							+ time[1].toString() + "'");
			if (tjvos.length > 0) {
				dmo.executeUpdateByDS(
						null,
						"delete from WN_RJ_CKYXHSTJ where E='"
								+ time[1].toString() + "'");
			}
			HashVO[] ckbi = dmo.getHashVoArrayByDS(null,
					"select * from WN_deposit_balance where date_time='"
							+ time[1].toString() + "'");
			if (ckbi.length > 0) {
				dmo.executeUpdateByDS(null,
						"delete from WN_deposit_balance where date_time='"
								+ time[1].toString() + "'");
			}
			// 得到客户经理的任务数
			HashMap<String, String> rwMap = dmo.getHashMapBySQLByDS(null,
					"select A,sum(C) from EXCEL_TAB_53 where year='"
							+ time[1].toString().substring(0, 4)
							+ "' group by A");
			// 已计发jfmap
			HashMap<String, String> jfmap = dmo.getHashMapBySQLByDS(null,
					"select B,sum(C) C from WN_RJ_CKYXHSTJ  group by B");
			// 未计发
			HashMap<String, String> Wjfmap = dmo.getHashMapBySQLByDS(null,
					"select B,sum(D) D from WN_RJ_CKYXHSTJ group by B");
			for (int i = 0; i < vo.length; i++) {
				String id = dmo
						.getSequenceNextValByDS(null, "S_WN_RJ_CKYXHSTJ");
				Double a, b, c = 0.0;
				if (countMap.get(vo[i].getStringValue("name").trim()) == null) {
					a = 0.0;
				} else {
					a = Double.parseDouble(countMap.get(vo[i].getStringValue(
							"name").trim())) / 10000;
				}
				if (monthmap.get(vo[i].getStringValue("name").trim()) == null) {
					b = 0.0;
				} else {
					b = Double.parseDouble(monthmap.get(
							vo[i].getStringValue("name").trim()).toString()) / 10000;
				}
				if (yearmap.get(vo[i].getStringValue("name").trim()) == null) {
					c = 0.0;
				} else {
					c = Double.parseDouble(yearmap.get(
							vo[i].getStringValue("name").trim()).toString()) / 10000;
				}
				insert.putFieldValue("id", id);
				insert.putFieldValue("A", vo[i].getStringValue("code"));
				insert.putFieldValue("B", vo[i].getStringValue("name"));
				insert.putFieldValue("E", time[1].toString());
				if (jfmap.size() > 0) {
					Double count = 0.0;
					Double wcount = 0.0;
					if (jfmap.get(vo[i].getStringValue("name").trim()) != null) {
						count = Double.parseDouble(jfmap.get(vo[i]
								.getStringValue("name").trim()));// 已计发的Map
					}
					if (Wjfmap.get(vo[i].getStringValue("name").trim()) != null) {
						wcount = Double.parseDouble(Wjfmap.get(vo[i]
								.getStringValue("name").trim()));// 未计发的Map
					}
					if (((a - b) - count + wcount) > 300) {
						insert.putFieldValue("C", "300");// 已计发的
						insert.putFieldValue("D",
								((a - b) - count + wcount) - 300);// 没有计发的
						map.put(vo[i].getStringValue("name").trim(),
								String.valueOf("300"));
					} else if (((a - b) - count + wcount) < -300) {
						insert.putFieldValue("C", "-300");// 已计发的
						insert.putFieldValue("D", ((a - b) - count + wcount)
								- (-300));// 没有计发的
						map.put(vo[i].getStringValue("name").trim(),
								String.valueOf("-300"));
					} else {
						insert.putFieldValue("C", ((a - b) - count + wcount));// 已计发的
						insert.putFieldValue("D", "0");// 未计发的
						map.put(vo[i].getStringValue("name").trim(),
								String.valueOf(((a - b) - count + wcount)));
					}
				} else {
					if ((a - b) > 300) {
						insert.putFieldValue("C", "300");// 已计发的
						insert.putFieldValue("D", (a - b) - 300);// 没有计发的
						map.put(vo[i].getStringValue("name").trim(),
								String.valueOf("300"));
					} else if ((a - b) < -300) {
						insert.putFieldValue("C", "-300");// 已计发的
						insert.putFieldValue("D", (a - b) - (-300));// 没有计发的
						map.put(vo[i].getStringValue("name").trim(),
								String.valueOf("-300"));
					} else {
						insert.putFieldValue("C", (a - b));// 已计发的
						insert.putFieldValue("D", "0");// 已计发的
						map.put(vo[i].getStringValue("name").trim(),
								String.valueOf((a - b)));
					}
				}

				list.add(insert.getSQL());
				insertB.putFieldValue("name", vo[i].getStringValue("name"));
				insertB.putFieldValue("passed", a - c);
				insertB.putFieldValue("task",
						rwMap.get(vo[i].getStringValue("name")));
				insertB.putFieldValue("date_time", time[1].toString());
				list.add(insertB.getSQL());
			}
			if (jfmap.size() > 0) {
				update.setWhereCondition("E='" + time[3].toString() + "'");
				update.putFieldValue("D", "0");
				list.add(update.getSQL());
			}
			for (String str : yearmap.keySet()) {
				System.out.println("" + str + "年初余额" + yearmap.get(str));
			}
			for (String str : countMap.keySet()) {
				System.out.println("" + str + "考核月余额" + countMap.get(str));
			}
			// zzl 把余额记录到一张表里
			for (String str : map.keySet()) {
				insertYe.putFieldValue("username", str);
				insertYe.putFieldValue("counths", map.get(str));
				insertYe.putFieldValue("date_time", time[1].toString());
				list.add(insertYe.getSQL());
			}
			dmo.executeBatchByDS(null, list);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

	/**
	 * 把所有的户数相加。
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, String> getCount(String date) {
		HashMap<String, String> map = new HashMap<String, String>();
		String[] time = date.split(";");
		HashMap<String, String> DSHQDSMap = getDSHQDS(date);
		HashMap<String, String> DSHQFQMap = getDSHQFQ(date);
		HashMap<String, String> DSDQDSMap = getDSDQDS(date);
		HashMap<String, String> DSDQFQMap = getDSDQFQ(date);
		HashMap<String, String> DGHQMap = getDGHQ(date);
		HashMap<String, String> DGDQMap = getDGDQ(date);
		try {
			for (int i = 0; i < vo.length; i++) {
				Double a = 0.0;
				if (DSHQDSMap.get(vo[i].getStringValue("name").trim()) != null) {
					a = Double.parseDouble(DSHQDSMap.get(
							vo[i].getStringValue("name").trim()).toString());
				}
				if (DSHQFQMap.get(vo[i].getStringValue("name").trim()) != null) {
					a = a
							+ Double.parseDouble(DSHQFQMap.get(
									vo[i].getStringValue("name").trim())
									.toString());
				}
				if (DSDQDSMap.get(vo[i].getStringValue("name").trim()) != null) {
					a = a
							+ Double.parseDouble(DSDQDSMap.get(vo[i]
									.getStringValue("name").trim()));
				}
				if (DSDQFQMap.get(vo[i].getStringValue("name").trim()) != null) {
					a = a
							+ Double.parseDouble(DSDQFQMap.get(vo[i]
									.getStringValue("name").trim()));
				}
				if (DGHQMap.get(vo[i].getStringValue("code").trim()) != null) {
					a = a
							+ Double.parseDouble(DGHQMap.get(vo[i]
									.getStringValue("code").trim()));
				}
				if (DGDQMap.get(vo[i].getStringValue("code").trim()) != null) {
					a = a
							+ Double.parseDouble(DGDQMap.get(vo[i]
									.getStringValue("code").trim()));
				}
				map.put(vo[i].getStringValue("name").trim(), String.valueOf(a));
			}
			for (String str : map.keySet()) {
				System.out.println(">>>>>>>" + str + ">>总计>>>>>>"
						+ map.get(str));
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
	 *            load_dates
	 * @return
	 */
	public HashMap<String, String> getDSHQDS(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,ffq.BAL_BOOK_AVG_M tj from (select xx.XD_COL96 XD_COL96,sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(f)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.a_agr_dep_acct_psn_sv where f > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3<>'01' and ck.BAL_BOOK_AVG_M>5000 group by xx.xd_col96 ) ffq left join  wnbank.S_LOAN_RYB yb on ffq.XD_COL96=yb.xd_col1");
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
	public HashMap<String, String> getDSHQFQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] count = dmo
					.getStringArrayByDS(
							null,
							"select xj.BAL_BOOK_AVG_M,xj.XD_COL7,yb.XD_COL2,xj.xd_col5 from (select sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M,xx.XD_COL7 XD_COL7,xx.XD_COL96 XD_COL96,gl.xd_col5 xd_col5 from (select cust_no cust_no,(sum(f)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.a_agr_dep_acct_psn_sv where f > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01' group by xx.xd_col7,xx.xd_col96,gl.xd_col5) xj left join wnbank.S_LOAN_RYB yb on xj.XD_COL96=yb.xd_col1");
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xx.XD_COL7,ck.BAL_BOOK_AVG_M BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(f)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.a_agr_dep_acct_psn_sv where f > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx  on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01'");
			for (int i = 0; i < count.length; i++) {
				Double tj = 0.0;
				if (resultMap.get(count[i][3].toString()) != null) {
					tj = Double.parseDouble(count[i][0].toString())
							+ Double.parseDouble(resultMap.get(count[i][3]
									.toString()));
					if (tj > 5000) {
						if (map.get(count[i][2].toString()) != null) {
							tj = tj
									+ Double.parseDouble(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), tj.toString());
					}
				} else {
					if (Double.parseDouble(count[i][0].toString()) > 5000) {
						if (map.get(count[i][2].toString()) != null) {
							tj = tj
									+ Double.parseDouble(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), tj.toString());
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
	public HashMap<String, String> getDSDQDS(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select yb.xd_col2 xd_col2,ffq.BAL_BOOK_AVG_M tj from (select xx.XD_COL96 XD_COL96,sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(acct_bal)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3<>'01' and ck.BAL_BOOK_AVG_M>5000 group by xx.xd_col96) ffq left join  wnbank.S_LOAN_RYB yb on ffq.XD_COL96=yb.xd_col1");
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
	public HashMap<String, String> getDSDQFQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] count = dmo
					.getStringArrayByDS(
							null,
							"select xj.BAL_BOOK_AVG_M,xj.XD_COL7,yb.XD_COL2,xj.xd_col5 from (select sum(ck.BAL_BOOK_AVG_M) BAL_BOOK_AVG_M,xx.XD_COL7 XD_COL7,xx.XD_COL96 XD_COL96,gl.xd_col5 xd_col5 from (select cust_no cust_no,(sum(acct_bal)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01' group by xx.xd_col7,xx.xd_col96,gl.xd_col5) xj left join wnbank.S_LOAN_RYB yb on xj.XD_COL96=yb.xd_col1");
			HashMap<String, String> resultMap = dmo
					.getHashMapBySQLByDS(
							null,
							"select xx.XD_COL7,ck.BAL_BOOK_AVG_M BAL_BOOK_AVG_M from (select cust_no cust_no,(sum(acct_bal)/"
									+ day
									+ ") as BAL_BOOK_AVG_M from wnbank.A_AGR_DEP_ACCT_PSN_FX where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) ck left join wnbank.S_OFCR_CI_CUSTMAST st on ck.cust_no = st.COD_CUST_ID left join wnbank.S_LOAN_KHXX xx  on st.EXTERNAL_CUSTOMER_IC = xx.XD_COL7 left join wnbank.S_LOAN_KHXXGL gl on xx.XD_COL1 = gl.XD_COL1 where gl.xd_col3='01'");
			for (int i = 0; i < count.length; i++) {
				Double tj = 0.0;
				if (resultMap.get(count[i][3].toString()) != null) {
					tj = Double.parseDouble(count[i][0].toString())
							+ Double.parseDouble(resultMap.get(count[i][3]
									.toString()));
					if (tj > 5000) {
						if (map.get(count[i][2].toString()) != null) {
							tj = tj
									+ Double.parseDouble(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), tj.toString());
					}
				} else {
					if (Double.parseDouble(count[i][0].toString()) > 5000) {
						if (map.get(count[i][2].toString()) != null) {
							tj = tj
									+ Double.parseDouble(map.get(count[i][2]
											.toString()));
						}
						map.put(count[i][2].toString(), tj.toString());
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
	public HashMap<String, String> getDGHQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.CUS_MANAGER as CUS_MANAGER,sum(a.acct_bal) tj from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_ENT_SV where acct_bal > 100 and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join wnbank.S_CMIS_ACC_LOAN c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000 group by c.CUS_MANAGER");
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
	public HashMap<String, String> getDGDQ(String date) {
		String[] time = date.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			map = dmo
					.getHashMapBySQLByDS(
							null,
							"select c.CUS_MANAGER as CUS_MANAGER,sum(a.acct_bal) tj from (select  cust_no, (sum(acct_bal)/"
									+ day
									+ ") as acct_bal  from wnbank.A_AGR_DEP_ACCT_ENT_FX where acct_bal > 100  and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
									+ time[0].toString()
									+ "' and to_char(to_date(load_dates,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
									+ time[1].toString()
									+ "' group by cust_no) a left join wnbank.S_OFCR_CH_ACCT_MAST b on a.cust_no = b.COD_CUST left join wnbank.S_CMIS_ACC_LOAN c on b.NAM_CUST_SHRT = c.CUS_NAME where c.LOAN_BALANCE > 0 and a.acct_bal>5000 group by c.CUS_MANAGER");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}

}
