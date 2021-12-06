package com.pushworld.ipushgrc.bs.cmpkpi.p030;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.style2.StyleReport_2_BuildDataIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public class CmpKpiStatisBuildDate implements StyleReport_2_BuildDataIFC {
	private CommDMO commdmo = null;
	private HashVO[] returnvos = null;

	public HashVO[] buildDataByCondition(HashMap _condition, CurrLoginUserVO userVO) throws Exception {
		String kpiyear = (String) _condition.get("kpiyear");
		String kpicycle = (String) _condition.get("kpicycle");
		commdmo = new CommDMO();
		StringBuffer sb = new StringBuffer(" select (select name from pub_corp_dept where id=kpicorp) 机构,kpiyear 年度,kpicycle 周期, t1 自评分,t2 复合分 from cmp_kpi where 1=1 ");
		if(kpiyear!=null && !kpiyear.equals("")){
			sb.append(" and kpiyear = '"+kpiyear+"'");
		}
		if(kpicycle!=null && !kpicycle.equals("")){
			sb.append(" and kpicycle = '"+kpicycle+"'");
		}
		sb.append("  order by kpicorp,kpiyear,kpicycle  ");
		returnvos = commdmo.getHashVoArrayByDS(null, sb.toString());
		return returnvos;
	}

	public String[][] getSortColumns() {
		return new String[][] { { "复合分", "Y", "Y" } };
	}

	public String[] getSpanColumns() {
		return new String[] { "机构" };
	}

	public String getTitle() {
		return "合规考核统计";
	}

}
