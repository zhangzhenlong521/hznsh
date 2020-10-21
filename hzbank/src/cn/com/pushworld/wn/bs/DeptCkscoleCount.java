package cn.com.pushworld.wn.bs;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.print.DocFlavor.STRING;

import com.ibm.db2.jcc.a.a;
import com.ibm.db2.jcc.a.de;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;
import freemarker.template.SimpleDate;

/**
 * 
 * @author zzl
 * 
 *         2019-4-10-上午11:08:27 部门存款得分计算
 */
public class DeptCkscoleCount {
	private static CommDMO dmo = new CommDMO();
	private YearManAndWifeHouseholdsCount year = new YearManAndWifeHouseholdsCount();
	private HashVO[] vo = null;
	private String day = null;
	private static String[] deptid = null;
	private static HashMap deptCode, deptName = null;
	private YearDeptCkscoleCountRJ yearRJ = new YearDeptCkscoleCountRJ();// 年初存款日均数
	private MonthDeptCkscoleCountRJ monthRJ = new MonthDeptCkscoleCountRJ();// 月存款日均
	private MonthDeptCkscoleCountSD monthSD = new MonthDeptCkscoleCountSD();// 存款考核月实点数
	private YearDeptCkscoleCountSD yearSD = new YearDeptCkscoleCountSD();// 年初存款实点数
	private SMonthDeptCkscoleCountRJ smontnRJ = new SMonthDeptCkscoleCountRJ();// 上月存款日均
	private SMonthDeptCkscoleCountSD smontnSD = new SMonthDeptCkscoleCountSD();// 上月存款实点数
	private DeptCkscoreJobCount job = new DeptCkscoreJobCount();// 任务数
	private String[] time = null;
	static {
		try {
			String[] deptIds = dmo
					.getStringArrayFirstColByDS(
							null,
							"select DEPTID from sal_target_checkeddept where 1=1  and (targetid='2721')  order by id");// 获取到部门
			String deptId = new String();
			for (int i = 0; i < deptIds.length; i++) {
				deptId = deptId + deptIds[i];
			}
			deptId = deptId.replaceAll(";;", ";");
			deptid = deptId.substring(1, deptId.length() - 1).split(";");
			deptCode = dmo.getHashMapBySQLByDS(null,
					"SELECT id,CODE FROM PUB_CORP_DEPT");// 获取到部门ID code
			deptName = dmo.getHashMapBySQLByDS(null,
					"SELECT id,name FROM PUB_CORP_DEPT");// 获取到部门ID name
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 较年初新增
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, HashMap<String, String>> getResult(String date) {
		time = date.split(";");
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> yearRJMap = yearRJ.getCountYearRJ(date, deptid,
				deptCode);// 年初存款日均数
		HashMap<String, String> monthRJMap = monthRJ.getCountYearRJ(date,
				deptid, deptCode);// 月存款日均数
		HashMap<String, String> monthSDMap = monthSD.getCountYearRJ(date,
				deptid, deptCode);// 存款考核月实点数
		HashMap<String, String> yearSDMap = yearSD.getCountYearRJ(date, deptid,
				deptCode);// 年初存款实点数
		HashMap<String, String> yearRJobMap = job.getJyearCount();// 网点存款日均额较年初新增任务量
		HashMap<String, String> yearSDjobMap = job.getJyearHourCount();// 网点存款时点数较年初新增任务量
		HashMap<String, String> map1 = new HashMap<String, String>();// 用来存入月存款日均数-年初存款日均数/任务数的值
		HashMap<String, String> map2 = new HashMap<String, String>();// 存款考核月实点数-年初存款实点数/任务数的值
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		int month = 0;
		try {
			month = Integer.parseInt(new SimpleDateFormat("MM").format(simple
					.parse(time[1])));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < deptid.length; i++) {
			BigDecimal db1 = new BigDecimal("0.0");
			// 月存款日均数-年初存款日均数
			db1 = new BigDecimal(monthRJMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(yearRJMap.get(deptCode
							.get(deptid[i]))));
			// 月存款日均数-年初存款日均数/网点存款日均额较年初新增任务量
			db1 = db1.divide(new BigDecimal("10000")).divide(
					new BigDecimal(yearRJobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map1.put(deptCode.get(deptid[i]).toString(), db1.toString());
			System.out.println(""
					+ deptName.get(deptid[i])
					+ "月存款日均数="
					+ monthRJMap.get(deptCode.get(deptid[i]))
					+ "年初存款日均数="
					+ yearRJMap.get(deptCode.get(deptid[i]))
					+ "日均额较年初新增任务量 ="
					+ yearRJobMap.get(deptName.get(deptid[i]).toString() + "_"
							+ String.valueOf(month))
					+ "月存款日均数-年初存款日均数/网点存款日均额较年初新增任务量 =" + db1);
			// --------------------------------
			BigDecimal db2 = new BigDecimal("0.0");
			// 存款考核月实点数-年初存款实点数
			db2 = new BigDecimal(monthSDMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(yearSDMap.get(deptCode
							.get(deptid[i]))));
			// 存款考核月实点数-年初存款实点数/时点数较年初新增任务量
			db2 = db2.divide(new BigDecimal("10000")).divide(
					new BigDecimal(yearSDjobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map2.put(deptCode.get(deptid[i]).toString(), db2.toString());
			System.out.println(""
					+ deptName.get(deptid[i])
					+ "存款考核月实点数="
					+ monthSDMap.get(deptCode.get(deptid[i]))
					+ "年初存款实点数="
					+ yearSDMap.get(deptCode.get(deptid[i]))
					+ "时点数较年初新增任务量 ="
					+ yearSDjobMap.get(deptName.get(deptid[i]).toString() + "_"
							+ String.valueOf(month))
					+ "存款考核月实点数-年初存款实点数/时点数较年初新增任务量 =" + db2);
		}
		map.put("月日均数-年初日均数/较年初新增任务量", map1);
		map.put("月实点数-年初点数/较上月新增任务量", map2);
		return map;
	}

	/**
	 * 较上月新增
	 * 
	 * @param date
	 * @return
	 */
	public HashMap<String, HashMap<String, String>> getResult1(String date) {
		String[] time = date.split(";");
		HashMap<String, HashMap<String, String>> map = new HashMap<String, HashMap<String, String>>();
		HashMap<String, String> monthRJMap = monthRJ.getCountYearRJ(date,
				deptid, deptCode);// 月存款日均数
		HashMap<String, String> monthSDMap = monthSD.getCountYearRJ(date,
				deptid, deptCode);// 存款考核月实点数
		HashMap<String, String> smonthRJMap = smontnRJ.getCountYearRJ(date,
				deptid, deptCode);// 上月存款日均
		HashMap<String, String> smonthSDMap = smontnSD.getCountYearRJ(date,
				deptid, deptCode);// 上月存款实点数
		HashMap<String, String> map3 = new HashMap<String, String>();// 月存款日均数-上月月存款日均数/任务数的值
		HashMap<String, String> map4 = new HashMap<String, String>();// 月实点数-上月实点数/任务数的值
		HashMap<String, String> smonthRJobMap = job.getJmonthCount();// 网点存款日均额较上月新增任务量
		HashMap<String, String> smonthSDjobMap = job.getJmonthHourCount();// 网点存款时点数较上月新增任务量
		SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd");
		int month = 0;
		try {
			month = Integer.parseInt(new SimpleDateFormat("MM").format(simple
					.parse(time[2])));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < deptid.length; i++) {
			// --------------------------------
			BigDecimal db3 = new BigDecimal("0.0");
			// 月存款日均数-上月月存款日均数
			db3 = new BigDecimal(monthRJMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(smonthRJMap.get(deptCode
							.get(deptid[i]))));
			// 月存款日均数-上月月存款日均数/任务数的值
			db3 = db3.divide(new BigDecimal("10000")).divide(
					new BigDecimal(smonthRJobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map3.put(deptCode.get(deptid[i]).toString(), db3.toString());
			System.out.println(">>>>>>>"
					+ deptName.get(deptid[i])
					+ "月存款日均数="
					+ monthRJMap.get(deptCode.get(deptid[i]))
					+ "上月月存款日均数="
					+ smonthRJMap.get(deptCode.get(deptid[i]))
					+ "日均额较上月新增任务量 ="
					+ smonthRJobMap.get(deptName.get(deptid[i]).toString()
							+ "_" + String.valueOf(month))
					+ "月存款日均数-上月月存款日均数/任务数的值 =" + db3);
			// --------------------------------
			BigDecimal db4 = new BigDecimal("0.0");
			// 考核月实点数-上月存款实点数
			db4 = new BigDecimal(monthSDMap.get(deptCode.get(deptid[i])))
					.subtract(new BigDecimal(smonthSDMap.get(deptCode
							.get(deptid[i]))));
			// 考核月实点数-上月存款实点数/任务数的值
			db4 = db4.divide(new BigDecimal("10000")).divide(
					new BigDecimal(smonthSDjobMap.get(deptName.get(deptid[i])
							.toString() + "_" + String.valueOf(month))), 2);
			map4.put(deptCode.get(deptid[i]).toString(), db4.toString());
			System.out.println(""
					+ deptName.get(deptid[i])
					+ "月实点数="
					+ monthSDMap.get(deptCode.get(deptid[i]))
					+ "上月存款实点数="
					+ smonthSDMap.get(deptCode.get(deptid[i]))
					+ "时点数较上月新增任务量 ="
					+ smonthSDjobMap.get(deptName.get(deptid[i]).toString()
							+ "_" + String.valueOf(month))
					+ "考核月实点数-上月存款实点数/任务数的值 =" + db4);
		}
		map.put("月日均数-上月日均数/日均额较上月新增任务量", map3);
		map.put("月实点数-上月点数/时点数较上月新增任务量", map4);
		return map;
	}
}