package cn.com.pushworld.wn.bs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.com.infostrategy.bs.common.WLTJobIFC;

/**
 * 
 * @author zzl
 * 
 *         2019-6-20-下午03:37:26 存款的定时任务，由于数据量大，比较耗时。
 *         [考核月初日期]+";"+[考核的月末日期]+";"+[
 *         上月月初日期]+";"+[上月指月末日期]+";"+[年初月初日期]+";"+[年初月末日期]+";"+[天数]
 */
public class DepositTimingJob implements WLTJobIFC {

	@Override
	public String run() throws Exception {
		String inputParam = getKHYCMonth() + ";" + getKHYMMonth() + ";"
				+ getSYYCMonth() + ";" + getSYYMMonth() + ";" + getKHNCMYear()
				+ ";" + getKHNCYear() + ";" + getMonthCount();
//		String inputParam = "2019-12-01;2019-12-31;2019-11-01;2019-11-30;2018-12-01;2018-12-31;31";
		ManAndWifeHouseholdsCount mc = new ManAndWifeHouseholdsCount();
		mc.getComputeMap(inputParam);
		AverageDailyManAnd am = new AverageDailyManAnd();
		am.getComputeMap(inputParam);
		// System.out.println(">>>>>>>>>>>>>"+inputParam);
		return "计算成功";
	}

	public static void main(String[] args) {
		DepositTimingJob a = new DepositTimingJob();
		String inputParam = a.getKHYCMonth() + ";" + a.getKHYMMonth() + ";"
				+ a.getSYYCMonth() + ";" + a.getSYYMMonth() + ";"
				+ a.getKHNCMYear() + ";" + a.getKHNCYear() + ";"
				+ a.getMonthCount();
		System.out.println(">>>>>>>>>>>>>>" + inputParam);
	}

	/**
	 * 考核月初日期 zzl
	 * 
	 * @return
	 */
	public String getKHYCMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(otherDate);
	}

	/**
	 * 考核的月末日期 zzl
	 * 
	 * @return
	 */
	public String getKHYMMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(otherDate);
	}

	/**
	 * 年初月末日期 zzl
	 * 
	 * @return
	 */
	public String getKHNCYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		return dateFormat.format(otherDate) + "-12-31";
	}

	/**
	 * 上月月末指标考核时间 zzl
	 * 
	 * @return
	 */
	public String getSYYMMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -2);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(otherDate);
	}

	/**
	 * 上月月初指标考核时间 zzl
	 * 
	 * @return
	 */
	public String getSYYCMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -2);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(otherDate);
	}

	/**
	 * 本月天数str zzl
	 * 
	 * @return
	 */
	public String getMonthCount() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
		return String.valueOf(Integer.parseInt(dateFormat.format(otherDate)));
	}

	/**
	 * 年初月初日期 zzl
	 * 
	 * @return
	 */
	public String getKHNCMYear() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.YEAR, -1);
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
		return dateFormat.format(otherDate) + "-12-01";
	}
}
