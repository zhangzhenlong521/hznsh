package cn.com.pushworld.wn.bs;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.mdata.DeleteSQLBuilder;
import cn.com.infostrategy.to.mdata.InsertSQLBuilder;

/**
 * 农民工存款归社指考核当月本年度新增农民工信息中客户存款较年初增加1000元（含）以上。 农民工存款归社
 * （考核当月农民工存款归社人数-上月农民工存款归社人数）X每人次单价 该定时任务负责计算出每户定期存款和活期存款之和，按照身份证进行聚合
 * @author 85378
 */
public class NmgckgsQuartzJob implements WLTJobIFC {

	private CommDMO dmo = new CommDMO();

	@Override
	public String run() throws Exception {
		System.out.println("农民工存款归社定时任务~~~~");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		// 首先获取到当前考核月的月末日期和上一个月的月末日期
		 String khMonthDay = getKHMonthDay();// 考核月日期
		 String lastMonthDay = getKHLastMonthDay();// 考核月上月日期
		 String khMonth = getKHMonth();// 获取到当前考核月
		 String khLastMonth = getLastMonth();// 获取到当前考核月上月
		String lastYearMonth = getLastYearMonth();// 获取到年初考核月
		// 判断考核月上月的数据是否存在
		// String khMonthData
		String[] khArray = dmo.getStringArrayFirstColByDS(null,
				"select 1 from wn_nmgck_result where createdate='" + khMonth
						+ "'");
		String[] khlastArray = dmo.getStringArrayFirstColByDS(null,
				"select 1 from wn_nmgck_result where createdate='"
						+ khLastMonth + "'");
		String[] lastYearArray = dmo.getStringArrayFirstColByDS(null,
				"select 1 from wn_nmgck_result where createdate='"
						+ lastYearMonth + "'");
		// 创建对应的SQL
		String khhqSQL = "SELECT sc.EXTERNAL_CUSTOMER_IC,sum(ck.money) FROM ("
				+ "(SELECT CUST_NO,sum(f) money FROM  WNBANK.A_AGR_DEP_ACCT_PSN_SV_"
				+ khMonth.replace("-", "")
				+ "  GROUP BY CUST_NO ) ck "
				+ "  JOIN (SELECT COD_CUST_ID,EXTERNAL_CUSTOMER_IC FROM wnbank.S_OFCR_CI_CUSTMAST_"
				+ khMonth.replace("-", "")
				+ ") sc ON ck.CUST_NO=sc.COD_CUST_ID "
				+ "JOIN  (select c,i,j from  wnsalarydb.excel_tab_91 where  e is not null and f is not null and  i is not null and j is not null and k is not null  and l is not null and  o > 0  and  y like '"
				+ khMonth
				+ "%'  and (g not  like '威宁%'  or  g is null) and  year||'-'||month='"+khMonth+"') nhxx ON nhxx.c=sc.EXTERNAL_CUSTOMER_IC"
				+ ")  GROUP BY sc.EXTERNAL_CUSTOMER_IC";
		String khdqSQL = "SELECT cardno, money FROM ("
				+ " (SELECT sc.EXTERNAL_CUSTOMER_IC cardno,  sum(ck.money) money FROM ( "
				+ " (SELECT  COD_CUST_ID,EXTERNAL_CUSTOMER_IC  FROM wnbank.S_OFCR_CI_CUSTMAST_"
				+ khMonth.replace("-", "")
				+ ") sc"
				+ " JOIN (SELECT CUST_NO,sum(acct_bal) money FROM  WNBANK.A_AGR_DEP_ACCT_PSN_FX_"
				+ khMonth.replace("-", "")
				+ "  GROUP BY CUST_NO ) ck ON ck.CUST_NO=sc.COD_CUST_ID"
				+ ") GROUP BY  sc.EXTERNAL_CUSTOMER_IC) ck "
				+ "JOIN (select c,i,j from  wnsalarydb.excel_tab_91 where  e is not null and f is not null and  i is not null and j is not null and k is not null  and l is not null and  o > 0  and  y like '"
				+ khMonth
				+ "%'  and (g not  like '威宁%'  or  g is null) and  year||'-'||month='"+khMonth+"') nhxx ON ck.cardno=nhxx.c"
				+ ")";
		String lasthqSQL = "SELECT sc.EXTERNAL_CUSTOMER_IC,sum(ck.money) FROM ( "
				+ " (SELECT CUST_NO,sum(f) money FROM  WNBANK.A_AGR_DEP_ACCT_PSN_SV_"
				+ khLastMonth.replace("-", "")
				+ "  GROUP BY CUST_NO ) ck "
				+ "  JOIN (SELECT COD_CUST_ID,EXTERNAL_CUSTOMER_IC FROM wnbank.S_OFCR_CI_CUSTMAST_"
				+ khLastMonth.replace("-", "")
				+ ") sc ON ck.CUST_NO=sc.COD_CUST_ID "
				+ "JOIN  (select c,i,j from  wnsalarydb.excel_tab_91 where  e is not null and f is not null and  i is not null and j is not null and k is not null  and l is not null and  o > 0  and  y like '"
				+ khMonth
				+ "%'  and (g not  like '威宁%'  or  g is null)) nhxx ON nhxx.c=sc.EXTERNAL_CUSTOMER_IC"
				+ ")   GROUP BY sc.EXTERNAL_CUSTOMER_IC";
		String lastdqSQL = "SELECT cardno, money FROM ("
				+ "(SELECT sc.EXTERNAL_CUSTOMER_IC cardno,  sum(ck.money) money FROM ("
				+ " (SELECT  COD_CUST_ID,EXTERNAL_CUSTOMER_IC  FROM wnbank.S_OFCR_CI_CUSTMAST_"
				+ khLastMonth.replace("-", "")
				+ ") sc "
				+ " JOIN (SELECT CUST_NO,sum(acct_bal) money FROM  WNBANK.A_AGR_DEP_ACCT_PSN_FX_"
				+ khLastMonth.replace("-", "")
				+ "  GROUP BY CUST_NO ) ck ON ck.CUST_NO=sc.COD_CUST_ID"
				+ ") GROUP BY  sc.EXTERNAL_CUSTOMER_IC) ck "
				+ " JOIN (select c,i,j from  wnsalarydb.excel_tab_91 where  e is not null and f is not null and  i is not null and j is not null and k is not null  and l is not null and  o > 0  and  y like '"
				+ khMonth
				+ "%'  and (g not  like '威宁%'  or  g is null)) nhxx ON ck.cardno=nhxx.c"
				+ ")";
		// 去年考核
		String lastYearhqSQL = "SELECT sc.EXTERNAL_CUSTOMER_IC,sum(ck.money) FROM ( "
				+ " (SELECT CUST_NO,sum(f) money FROM  WNBANK.A_AGR_DEP_ACCT_PSN_SV_"
				+ lastYearMonth.replace("-", "")
				+ "  GROUP BY CUST_NO ) ck "
				+ "  JOIN (SELECT COD_CUST_ID,EXTERNAL_CUSTOMER_IC FROM wnbank.S_OFCR_CI_CUSTMAST_"
				+ lastYearMonth.replace("-", "")
				+ ") sc ON ck.CUST_NO=sc.COD_CUST_ID "
				+ "JOIN  (select c,i,j from  wnsalarydb.excel_tab_91 where  e is not null and f is not null and  i is not null and j is not null and k is not null  and l is not null and  o > 0  and  y like '"
				+ khMonth
				+ "%'  and (g not  like '威宁%'  or  g is null) and  year||'-'||month='"+khMonth+"') nhxx ON nhxx.c=sc.EXTERNAL_CUSTOMER_IC"
				+ ")   GROUP BY sc.EXTERNAL_CUSTOMER_IC";
		System.out.println("去年活期存款:"+lastYearhqSQL);
		String lastYearDqSql = "SELECT cardno, money FROM ( "
				+ "(SELECT sc.EXTERNAL_CUSTOMER_IC cardno,  sum(ck.money) money FROM ("
				+ " (SELECT  COD_CUST_ID,EXTERNAL_CUSTOMER_IC  FROM wnbank.S_OFCR_CI_CUSTMAST_"
				+ lastYearMonth.replace("-", "")
				+ ") sc "
				+ " JOIN (SELECT CUST_NO,sum(acct_bal) money FROM  WNBANK.A_AGR_DEP_ACCT_PSN_FX_"
				+ lastYearMonth.replace("-", "")
				+ "  GROUP BY CUST_NO ) ck ON ck.CUST_NO=sc.COD_CUST_ID"
				+ ") GROUP BY  sc.EXTERNAL_CUSTOMER_IC) ck "
				+ "JOIN (select c,i,j from  wnsalarydb.excel_tab_91 where  e is not null and f is not null and  i is not null and j is not null and k is not null  and l is not null and  o > 0  and  y like '"
				+ khMonth
				+ "%'  and (g not  like '威宁%'  or  g is null) and  year||'-'||month='"+khMonth+"') nhxx ON ck.cardno=nhxx.c"
				+ ")";
		System.out.println("去去年定期存款:"+lastYearDqSql);
		List<String> list = new ArrayList<String>();
		DeleteSQLBuilder delete = new DeleteSQLBuilder("wn_nmgck_result");
		if (khArray != null && khArray.length > 0) {// 判断当前考核月日期的数据是否存在
			delete.setWhereCondition("CREATEDATE='"+khMonth+"' ");
			dmo.executeUpdateByDS(null, delete.getSQL());
		}
		HashMap<String, String> hqMap = dmo.getHashMapBySQLByDS(null,
				khhqSQL);
		HashMap<String, String> dqMap = dmo.getHashMapBySQLByDS(null,
				khdqSQL);
		insertTable(hqMap, dqMap, khMonth);// 插入考核月数据
//		if (khlastArray == null || khlastArray.length <= 0) {
//			HashMap<String, String> lastMonthHqMap = dmo.getHashMapBySQLByDS(
//					null, lasthqSQL);
//			HashMap<String, String> lastMonthdqMap = dmo.getHashMapBySQLByDS(
//					null, lastdqSQL);
//			insertTable(lastMonthHqMap, lastMonthdqMap, khLastMonth);// 插入考核月上月数据
//		}
		if (lastYearArray == null || lastYearArray.length <= 0) {// 判断年初数据是否存在
			delete.setWhereCondition("CREATEDATE='"+lastYearMonth+"' ");
			dmo.executeUpdateByDS(null, delete.getSQL());
		}
		HashMap<String, String> lastYearhqMap = dmo.getHashMapBySQLByDS(
				null, lastYearhqSQL);
		HashMap<String, String> lastYearDqMap = dmo.getHashMapBySQLByDS(
				null, lastYearDqSql);
		insertTable(lastYearhqMap, lastYearDqMap, lastYearMonth);// 插入年初的数据
		/**
		 * 计算出最终结果
		 */
		return "" + format.format(new Date()) + "   【农民工存款归社】指标定时任务执行完成";
	}

	/**
	 * 获取到当前考核月的时点日期
	 * 
	 * @return
	 */
	public String getKHMonthDay() {
		String result = "";
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);// 获取到考核月日期
		if (month == 0) {// 表示当前考核月是去年的12月份
			month = 12;
			year--;
		}
		String lastDay = getMonthLastDay(month, year);
		if (String.valueOf(month).length() <= 1) {
			result = year + "0" + month + lastDay;
		} else {
			result = year + "" + month + lastDay;
		}
		return result;
	}

	/**
	 * 获取到考核月上月时点日期
	 * 
	 * @return
	 */
	public String getKHLastMonthDay() {
		String result = "";
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);// 获取到考核月日期
		if (month == 0) {
			month = 11;
			year--;
		} else {
			if (--month == 0) {
				year--;
				month = 12;
			}
		}
		String lastDay = getMonthLastDay(month, year);
		if (String.valueOf(month).length() <= 1) {
			result = year + "0" + (month) + lastDay;
		} else {
			result = year + "" + month + lastDay;
		}
		return result;
	}

	/**
	 * 获取到当前考核月的最后一天
	 * 
	 * @param month
	 *            :考核月
	 * @param year
	 *            :年
	 * @return
	 */
	public String getMonthLastDay(int month, int year) {// 获取到当前月的最后日期
		String day = "";
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12:
			day = "31";
			break;
		case 4:
		case 6:
		case 9:
		case 11:
			day = "30";
			break;
		default:
			// 如果是2月，则需要判断是否是闰年
			if (year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
				day = "29";
			} else {
				day = "28";
			}
			break;
		}
		return day;
	}

	/**
	 * 获取到当前考核月
	 * 
	 * @return
	 */
	public String getKHMonth() {
		String result = "";
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);// 获取到考核月日期
		if (month == 0) {// 表示当前考核月是去年的12月份
			month = 12;
			year--;
		}
		if (String.valueOf(month).length() <= 1) {
			result = year + "-0" + month;
		} else {
			result = year + "-" + month;
		}
		return result;
	}

	/**
	 * 获取到考核月上月
	 * 
	 * @return
	 */
	public String getLastMonth() {
		String result = "";
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);// 获取到考核月日期
		if (month == 0) {
			month = 11;
			year--;
		} else {
			if (--month == 0) {
				year--;
				month = 12;
			}
		}
		if (String.valueOf(month).length() <= 1) {
			result = year + "-0" + (month);
		} else {
			result = year + "-" + month;
		}
		return result;
	}

	public void insertTable(HashMap<String, String> hqMap,
			HashMap<String, String> dqMap, String date) {
		HashMap<String, BigDecimal> money = new HashMap<String, BigDecimal>();
		if (hqMap.size() != 0) {
			Set<String> hqSet = hqMap.keySet();
			for (String cardNo : hqSet) {
				money.put(
						cardNo,
						money.get(cardNo) == null ? new BigDecimal(hqMap
								.get(cardNo)) : money.get(cardNo).add(
								new BigDecimal(hqMap.get(cardNo))));
			}
		}
		if (dqMap.size() != 0) {
			Set<String> dqSet = dqMap.keySet();
			for (String cardNo : dqSet) {
				money.put(
						cardNo,
						money.get(cardNo) == null ? new BigDecimal(dqMap
								.get(cardNo)) : money.get(cardNo).add(
								new BigDecimal(dqMap.get(cardNo))));
			}
		}
		Set<String> monSet = money.keySet();
		InsertSQLBuilder insert = new InsertSQLBuilder("WN_NMGCK_RESULT");
		List<String> list = new ArrayList<String>();
		try {
			BigDecimal zeroDecimal = new BigDecimal("1000.00");
			String lastYearMonth = getLastYearMonth();
			for (String cardNo : monSet) {
				if (!lastYearMonth.equals(date)
						&& money.get(cardNo).compareTo(zeroDecimal) < 0) {
					continue;
				}
				insert.putFieldValue("CKMONEY", money.get(cardNo).toString());
				insert.putFieldValue("CARDNO", cardNo);
				insert.putFieldValue("CREATEDATE", date);
				insert.putFieldValue("id",
						dmo.getSequenceNextValByDS(null, "S_WN_NMGCK_RESULT"));
				list.add(insert.getSQL());
				if (list.size() >= 5000) {
					dmo.executeBatchByDS(null, list);
					list.clear();
				}
			}
			if (list.size() > 0) {
				dmo.executeBatchByDS(null, list);
				list.clear();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 获取到去年年初的时间
	 * 
	 * @return
	 */
	public String getLastYearDay() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR) - 1;
		return year + "1231";
	}

	/**
	 * 获取到去年考核月上月
	 * 
	 * @return
	 */
	public String getLastYearMonth() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR) - 1;
		return year + "-12";
	}
}
