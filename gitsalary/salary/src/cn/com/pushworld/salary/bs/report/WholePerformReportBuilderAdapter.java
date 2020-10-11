package cn.com.pushworld.salary.bs.report;

import java.util.HashMap;

import cn.com.infostrategy.bs.common.CommDMO;
import cn.com.infostrategy.bs.report.style3.StyleReport_3_BuildDataIFC;
import cn.com.infostrategy.to.common.HashVO;
import cn.com.infostrategy.to.common.TBUtil;
import cn.com.infostrategy.to.sysapp.login.CurrLoginUserVO;

public class WholePerformReportBuilderAdapter implements StyleReport_3_BuildDataIFC {
	private CommDMO commdmo = null;
	private TBUtil tbutil = null;
	private String targetid = null;
	private String deptid = null;
	private String starttime = null;
	private String endtime = null;

	public HashVO[] buildDataByCondition(HashMap condition, CurrLoginUserVO userVO) throws Exception {
		targetid = (String) condition.get("targetids");
		deptid = (String) condition.get("querycorp");
		starttime = (String) condition.get("starttime");
		endtime = (String) condition.get("endtime");
		StringBuffer sb_sql = new StringBuffer();
		sb_sql.append("select t3.reportshowname 指标名称,t2.checkdate 月份, sum(t1.reportvalue) 有效值,avg(t1.reportvalue) 平均值 from sal_dept_check_score t1 left join  sal_target_check_log t2 on t1.logid = t2.id  left join sal_target_list t3 on t1.targetid = t3.id where t1.targettype ='部门定量指标' and t3.reportshowname not like '%支行%' and t3.reportshowname not like '%满意度%' ");
		if (targetid != null && !targetid.equals("")) {
			targetid = getTBUtil().getInCondition(targetid);
			sb_sql.append(" and t1.targetid in(" + targetid + ")");
		}
		if (deptid != null && !deptid.equals("")) {
			deptid = getTBUtil().getInCondition(deptid);
			sb_sql.append(" and t1.checkeddept in(" + deptid + ")");
		}
		if (starttime != null && !starttime.equals("")) {
			sb_sql.append(" and t2.checkdate >='" + starttime + "'");
		}
		if (endtime != null && !endtime.equals("")) {
			sb_sql.append(" and t2.checkdate <='" + endtime + "'");
		}
		sb_sql.append(" group by t1.targetname, t1.logid");
		HashVO[] hashvo = getCommDMO().getHashVoArrayByDS(null, sb_sql.toString());
		return hashvo;
	}

	public CommDMO getCommDMO() {
		if (commdmo == null) {
			commdmo = new CommDMO();
		}
		return commdmo;
	}

	public TBUtil getTBUtil() {
		if (tbutil == null) {
			tbutil = new TBUtil();
		}
		return tbutil;
	}

	public String getColHeadName() {
		return "月份";
	}

	public String getComputeItemName() {
		return "有效值";
	}

	public String getComputeType() {
		return StyleReport_3_BuildDataIFC.SELECT;
	}

	public String getRowHeadName() {
		return "指标名称";
	}

}
