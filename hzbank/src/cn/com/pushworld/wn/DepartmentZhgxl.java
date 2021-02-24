package cn.com.pushworld.wn;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.common.WLTJobIFC;
import cn.com.infostrategy.to.common.TBUtil;

public class DepartmentZhgxl  implements WLTJobIFC {
	/**
	 * 部门定量综合贡献度
	 */
	
	private static final long serialVersionUID = 1L;
	private int month = Calendar.getInstance().get(Calendar.MONTH);
	private String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
	private String monthColum = getMonthColum(month);
	private CommDMO dmo = new CommDMO();
	private String lastMonth = getSYYMMonth();
	

	@Override
	public String run() throws Exception {
		DepartmentGxl gxl = new DepartmentGxl();
		HashMap<String, String>  ckgxl = gxl.getCkgxl(year, monthColum, lastMonth);
		HashMap<String, String>  srgxl = gxl.getSrgxl(year, monthColum, lastMonth);
		gxl.getZhgxd(ckgxl, srgxl, lastMonth);
		return "计算成功";
	}
	public String getMonthColum(int month){
		switch (month) {
		case 1:
			return "C";
		case 2:
			return "D";
		case 3:
			return "E";
		case 4:
			return "F";
		case 5:
			return "G";
		case 6:
			return "H";
		case 7:
			return "I";
		case 8:
			return "G";
		case 9:
			return "K";
		case 10:
			return "L";
		case 11:
			return "M";
		case 12:
			return "N";
		default:
			return null;
		}
	
		
	}
	public String getSYYMMonth() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.add(Calendar.MONTH, -2);
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DATE));
		Date otherDate = cal.getTime();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
		return dateFormat.format(otherDate);
	}

}
