package cn.com.pushworld.wn.bs;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;

/**
 * 
 * @author zzl
 * 
 *         2019-4-10-下午02:30:17 网点存款日均额较年初新增任务量 网点存款日均额较上月新增任务量 网点存款时点数较年初新增任务量
 *         网点存款时点数较上月新增任务量
 */
public class DeptCkscoreJobCount {
	private CommDMO dmo = new CommDMO();

	/**
	 * 网点存款日均额较年初新增任务量
	 * 
	 * @return
	 */
	public HashMap<String, String> getJyearCount() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] data = dmo.getStringArrayByDS(null,
					"select a,c,d,e,f,g,h,i,j,k,l,m,n from excel_tab_18");
			for (int i = 0; i < data.length; i++) {
				Double count = 0.0;
				for (int j = 1; j < data[i].length; j++) {
					count = count + Double.parseDouble(data[i][j].toString());
					map.put(data[i][0].toString() + "_" + String.valueOf(j),
							String.valueOf(count));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;

	}

	/**
	 * 网点存款日均额较上月新增任务量
	 * 
	 */
	public HashMap<String, String> getJmonthCount() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] data = dmo.getStringArrayByDS(null,
					"select a,c,d,e,f,g,h,i,j,k,l,m,n from excel_tab_18");
			for (int i = 0; i < data.length; i++) {
				for (int j = 1; j < data[i].length; j++) {
					map.put(data[i][0].toString() + "_" + String.valueOf(j),
							data[i][j]);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return map;

	}

	/**
	 * 网点存款时点数较年初新增任务量
	 * 
	 * @return
	 */
	public HashMap<String, String> getJyearHourCount() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] data1 = dmo.getStringArrayByDS(null,
					"select a,c,d,e,f,g,h,i,j,k,l,m,n from excel_tab_33");
			for (int i = 0; i < data1.length; i++) {
				Double count = 0.0;
				for (int j = 1; j < data1[i].length; j++) {
					count = count + Double.parseDouble(data1[i][j].toString());
					map.put(data1[i][0].toString() + "_" + String.valueOf(j),
							String.valueOf(count));
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return map;
	}

	/**
	 * 网点存款时点数较上月新增任务量
	 * 
	 * @return
	 */

	public HashMap<String, String> getJmonthHourCount() {
		HashMap<String, String> map = new HashMap<String, String>();
		try {
			String[][] data1 = dmo.getStringArrayByDS(null,
					"select a,c,d,e,f,g,h,i,j,k,l,m,n from excel_tab_33");
			for (int i = 0; i < data1.length; i++) {
				for (int j = 1; j < data1[i].length; j++) {
					map.put(data1[i][0].toString() + "_" + String.valueOf(j),
							data1[i][j]);
				}
			}
		} catch (Exception e) {
			e.getMessage();
		}
		return map;
	}

}
