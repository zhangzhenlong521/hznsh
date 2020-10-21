package cn.com.pushworld.wn;

import java.util.HashMap;
import java.util.Map;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.to.common.HashVO;

public class ZNshwhCount {
	/**
	 * 2019-03-20 zpy 计算网格管理员助农商户绩效
	 * 
	 * @return
	 */
	public Map<String, Double> getCount(String strdate) {
		Map<String, Double> resultMap = new HashMap<String, Double>();// 存储结果
		CommDMO com = new CommDMO();
		try {
			// 所有网格管理员下面合格的商户以及达标农户数
			HashVO[] vo = com
					.getHashVoArrayByDS(
							null,
							"SELECT bb.B AS name ,sum(aa.num) AS num FROM (SELECT a.B AS B,a.C AS C,(b.B+b.C) AS num  FROM  WNSALARYDB.excel_tab_11 a LEFT JOIN WNSALARYDB.excel_tab_14 b ON a.B=b.A WHERE a.D!='否'  AND (b.B+b.C)>=a.E GROUP BY a.B,(b.B+b.C),a.C) aa LEFT JOIN WNSALARYDB.excel_tab_10 bb ON aa.c=bb.C GROUP BY bb.b ORDER BY bb.B");
			// 网格管理员下面合格的商户数量
			HashMap map2 = com
					.getHashMapBySQLByDS(
							null,
							"SELECT bb.B AS name , COUNT(aa.b) AS hgnum  FROM (SELECT a.B AS B,a.C AS C,(b.B+b.C) AS num  FROM  WNSALARYDB.excel_tab_11 a LEFT JOIN WNSALARYDB.excel_tab_14 b ON a.B=b.A WHERE a.D!='否'  AND (b.B+b.C)>=a.E GROUP BY a.B,(b.B+b.C),a.C) aa LEFT JOIN WNSALARYDB.excel_tab_10 bb ON aa.c=bb.C  GROUP BY bb.B ORDER BY bb.B");
			// 查询网格管理员下对应的商户数量
			HashMap map3 = com
					.getHashMapBySQLByDS(null,
							"SELECT B,COUNT(C) FROM WNSALARYDB.excel_tab_10 GROUP BY B ORDER BY B");
			int unit_price = Integer.parseInt(com.getStringValueByDS(null,
					"SELECT B FROM WNSALARYDB.wn_data_011"));
			for (HashVO hashVO : vo) {
				String name = hashVO.getStringValue("name");// 获取到当前网格管理员
				int hgNum = Integer.parseInt((String) map2.get(name));// 当前网格管理员对应的合格商户数量
				int num = Integer.parseInt((String) map3.get(name));
				if (hgNum < num) {
					resultMap.put(name, 0d);
				} else {
					String va = hashVO.getStringValue("num");
					int price = Integer.parseInt(va) * unit_price;
					resultMap.put(name, Double.parseDouble(price + ""));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(">>>>>>>>>>>>>>" + resultMap);
		return resultMap;
	}

}