package cn.com.pushworld.wn;

import java.math.BigDecimal;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;

/**
 * 
 * @author zzl
 * 
 *         2019-3-19-上午10:14:42 贷款管户数的计算 逐笔计算
 */
public class DKGuanHuShuCount {
	public HashMap<String, String> getCount(String strDate) {
		String[] time = strDate.split(";");
		HashMap<String, String> map = new HashMap<String, String>();
		CommDMO dmo = new CommDMO();
		String[] countStr = { "乡镇农户", "城区农户", "一般自然人", "企业", "职工", "按揭" };
		String sql = "select rr.xd_col2 As xd_col2,dk.countxj As  countxj,dk.XD_COL72 from (select aa.xd_col81,count(aa.XD_COL72) countxj,aa.XD_COL72 from (select XD_COL16,XD_COL72,XD_COL81 from  wnbank.s_loan_dk where TO_CHAR(TO_DATE(BIZ_DT,'yyyy-mm-dd'),'yyyy-mm-dd')>='"
				+ time[0].toString()
				+ "' AND TO_CHAR(TO_DATE(BIZ_DT,'yyyy-mm-dd'),'yyyy-mm-dd')<='"
				+ time[1].toString()
				+ "' group by XD_COL16,XD_COL72,XD_COL81) aa group by aa.xd_col81,aa.XD_COL72) dk left join wnbank.S_LOAN_RYB rr on dk.XD_COL81=rr.XD_COL1";
		String counterre = null;
		try {
			// 得到单价的Map
			HashMap<String, String> djMap = dmo.getHashMapBySQLByDS(null,
					"select A,B from wn_data_003");
			// 得到岗位归类Map
			HashMap<String, String> deptMap = dmo
					.getHashMapBySQLByDS(null,
							"select name,stationkind from v_sal_personinfo where stationkind is not null");
			// 得到业务品种Map
			HashMap<String, String> ywpzMap = dmo.getHashMapBySQLByDS(null,
					"select A,B from wn_data_22");
			// 得到唯一的客户经理作为条件
			String[] str = dmo
					.getStringArrayFirstColByDS(
							null,
							"select dkhj.xd_col2 from ("
									+ sql
									+ ") dkhj where dkhj.xd_col2 is not null group by dkhj.xd_col2");

			for (int i = 0; i < str.length; i++) {

				String[][] data = dmo.getStringArrayByDS(null, sql
						+ " where rr.xd_col2='" + str[i].toString() + "'");// 得到所有的户数笔数，及类型
				Double countzj = 0.0;
				for (int j = 0; j < data.length; j++) {

					BigDecimal countxj = new BigDecimal("0.0");
					counterre = data[j][2].toString();
					if (ywpzMap.get(data[j][2].toString()).contains("农户")) {
						countxj = new BigDecimal(data[j][1].toString())
								.multiply(new BigDecimal(djMap.get(countStr[0]
										.toString())));
					} else if (ywpzMap.get(data[j][2].toString()).contains(
							countStr[2].toString())) {
						countxj = new BigDecimal(data[j][1].toString())
								.multiply(new BigDecimal(djMap.get(countStr[2]
										.toString())));
					} else if (ywpzMap.get(data[j][2].toString()).contains(
							countStr[3].toString())) {
						countxj = new BigDecimal(data[j][1].toString())
								.multiply(new BigDecimal(djMap.get(countStr[3]
										.toString())));
					} else if (ywpzMap.get(data[j][2].toString()).contains(
							countStr[4].toString())) {
						countxj = new BigDecimal(data[j][1].toString())
								.multiply(new BigDecimal(djMap.get(countStr[4]
										.toString())));
					} else if (ywpzMap.get(data[j][2].toString()).contains(
							countStr[5].toString())) {
						countxj = new BigDecimal(data[j][1].toString())
								.multiply(new BigDecimal(djMap.get(countStr[5]
										.toString())));
					}
					if (j > 0) {
						countxj = new BigDecimal(map.get(data[j][0].toString()))
								.add(countxj);
					}
					map.put(data[j][0], countxj.toString());
				}
			}
		} catch (Exception e) {
			System.out.println(">>>>>>>>>>>>>>" + counterre);
			e.printStackTrace();
		}
		return map;
	}

}
